package io.netty.handler.codec.compression;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.util.internal.ObjectUtil;
import java.util.List;
import java.util.zip.CRC32;
import java.util.zip.DataFormatException;
import java.util.zip.Inflater;

public class JdkZlibDecoder extends ZlibDecoder {
  private static final int FHCRC = 2;
  
  private static final int FEXTRA = 4;
  
  private static final int FNAME = 8;
  
  private static final int FCOMMENT = 16;
  
  private static final int FRESERVED = 224;
  
  private Inflater inflater;
  
  private final byte[] dictionary;
  
  private final ByteBufChecksum crc;
  
  private final boolean decompressConcatenated;
  
  private enum GzipState {
    HEADER_START, HEADER_END, FLG_READ, XLEN_READ, SKIP_FNAME, SKIP_COMMENT, PROCESS_FHCRC, FOOTER_START;
  }
  
  private GzipState gzipState = GzipState.HEADER_START;
  
  private int flags = -1;
  
  private int xlen = -1;
  
  private volatile boolean finished;
  
  private boolean decideZlibOrNone;
  
  public JdkZlibDecoder() {
    this(ZlibWrapper.ZLIB, null, false, 0);
  }
  
  public JdkZlibDecoder(int maxAllocation) {
    this(ZlibWrapper.ZLIB, null, false, maxAllocation);
  }
  
  public JdkZlibDecoder(byte[] dictionary) {
    this(ZlibWrapper.ZLIB, dictionary, false, 0);
  }
  
  public JdkZlibDecoder(byte[] dictionary, int maxAllocation) {
    this(ZlibWrapper.ZLIB, dictionary, false, maxAllocation);
  }
  
  public JdkZlibDecoder(ZlibWrapper wrapper) {
    this(wrapper, null, false, 0);
  }
  
  public JdkZlibDecoder(ZlibWrapper wrapper, int maxAllocation) {
    this(wrapper, null, false, maxAllocation);
  }
  
  public JdkZlibDecoder(ZlibWrapper wrapper, boolean decompressConcatenated) {
    this(wrapper, null, decompressConcatenated, 0);
  }
  
  public JdkZlibDecoder(ZlibWrapper wrapper, boolean decompressConcatenated, int maxAllocation) {
    this(wrapper, null, decompressConcatenated, maxAllocation);
  }
  
  public JdkZlibDecoder(boolean decompressConcatenated) {
    this(ZlibWrapper.GZIP, null, decompressConcatenated, 0);
  }
  
  public JdkZlibDecoder(boolean decompressConcatenated, int maxAllocation) {
    this(ZlibWrapper.GZIP, null, decompressConcatenated, maxAllocation);
  }
  
  private JdkZlibDecoder(ZlibWrapper wrapper, byte[] dictionary, boolean decompressConcatenated, int maxAllocation) {
    super(maxAllocation);
    ObjectUtil.checkNotNull(wrapper, "wrapper");
    this.decompressConcatenated = decompressConcatenated;
    switch (wrapper) {
      case HEADER_START:
        this.inflater = new Inflater(true);
        this.crc = ByteBufChecksum.wrapChecksum(new CRC32());
        break;
      case FLG_READ:
        this.inflater = new Inflater(true);
        this.crc = null;
        break;
      case XLEN_READ:
        this.inflater = new Inflater();
        this.crc = null;
        break;
      case SKIP_FNAME:
        this.decideZlibOrNone = true;
        this.crc = null;
        break;
      default:
        throw new IllegalArgumentException("Only GZIP or ZLIB is supported, but you used " + wrapper);
    } 
    this.dictionary = dictionary;
  }
  
  public boolean isClosed() {
    return this.finished;
  }
  
  protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
    if (this.finished) {
      in.skipBytes(in.readableBytes());
      return;
    } 
    int readableBytes = in.readableBytes();
    if (readableBytes == 0)
      return; 
    if (this.decideZlibOrNone) {
      if (readableBytes < 2)
        return; 
      boolean nowrap = !looksLikeZlib(in.getShort(in.readerIndex()));
      this.inflater = new Inflater(nowrap);
      this.decideZlibOrNone = false;
    } 
    if (this.crc != null && 
      this.gzipState != GzipState.HEADER_END) {
      if (this.gzipState == GzipState.FOOTER_START) {
        if (!handleGzipFooter(in))
          return; 
        assert this.gzipState == GzipState.HEADER_START;
      } 
      if (!readGZIPHeader(in))
        return; 
      readableBytes = in.readableBytes();
      if (readableBytes == 0)
        return; 
    } 
    if (this.inflater.needsInput())
      if (in.hasArray()) {
        this.inflater.setInput(in.array(), in.arrayOffset() + in.readerIndex(), readableBytes);
      } else {
        byte[] array = new byte[readableBytes];
        in.getBytes(in.readerIndex(), array);
        this.inflater.setInput(array);
      }  
    ByteBuf decompressed = prepareDecompressBuffer(ctx, (ByteBuf)null, this.inflater.getRemaining() << 1);
    try {
      boolean readFooter = false;
      while (!this.inflater.needsInput()) {
        byte[] outArray = decompressed.array();
        int writerIndex = decompressed.writerIndex();
        int outIndex = decompressed.arrayOffset() + writerIndex;
        int writable = decompressed.writableBytes();
        int outputLength = this.inflater.inflate(outArray, outIndex, writable);
        if (outputLength > 0) {
          decompressed.writerIndex(writerIndex + outputLength);
          if (this.crc != null)
            this.crc.update(outArray, outIndex, outputLength); 
        } else if (this.inflater.needsDictionary()) {
          if (this.dictionary == null)
            throw new DecompressionException("decompression failure, unable to set dictionary as non was specified"); 
          this.inflater.setDictionary(this.dictionary);
        } 
        if (this.inflater.finished()) {
          if (this.crc == null) {
            this.finished = true;
            break;
          } 
          readFooter = true;
          break;
        } 
        decompressed = prepareDecompressBuffer(ctx, decompressed, this.inflater.getRemaining() << 1);
      } 
      in.skipBytes(readableBytes - this.inflater.getRemaining());
      if (readFooter) {
        this.gzipState = GzipState.FOOTER_START;
        handleGzipFooter(in);
      } 
    } catch (DataFormatException e) {
      throw new DecompressionException("decompression failure", e);
    } finally {
      if (decompressed.isReadable()) {
        out.add(decompressed);
      } else {
        decompressed.release();
      } 
    } 
  }
  
  private boolean handleGzipFooter(ByteBuf in) {
    if (readGZIPFooter(in)) {
      this.finished = !this.decompressConcatenated;
      if (!this.finished) {
        this.inflater.reset();
        this.crc.reset();
        this.gzipState = GzipState.HEADER_START;
        return true;
      } 
    } 
    return false;
  }
  
  protected void decompressionBufferExhausted(ByteBuf buffer) {
    this.finished = true;
  }
  
  protected void handlerRemoved0(ChannelHandlerContext ctx) throws Exception {
    super.handlerRemoved0(ctx);
    if (this.inflater != null)
      this.inflater.end(); 
  }
  
  private boolean readGZIPHeader(ByteBuf in) {
    int magic0;
    int magic1;
    int method;
    switch (this.gzipState) {
      case HEADER_START:
        if (in.readableBytes() < 10)
          return false; 
        magic0 = in.readByte();
        magic1 = in.readByte();
        if (magic0 != 31)
          throw new DecompressionException("Input is not in the GZIP format"); 
        this.crc.update(magic0);
        this.crc.update(magic1);
        method = in.readUnsignedByte();
        if (method != 8)
          throw new DecompressionException("Unsupported compression method " + method + " in the GZIP header"); 
        this.crc.update(method);
        this.flags = in.readUnsignedByte();
        this.crc.update(this.flags);
        if ((this.flags & 0xE0) != 0)
          throw new DecompressionException("Reserved flags are set in the GZIP header"); 
        this.crc.update(in, in.readerIndex(), 4);
        in.skipBytes(4);
        this.crc.update(in.readUnsignedByte());
        this.crc.update(in.readUnsignedByte());
        this.gzipState = GzipState.FLG_READ;
      case FLG_READ:
        if ((this.flags & 0x4) != 0) {
          if (in.readableBytes() < 2)
            return false; 
          int xlen1 = in.readUnsignedByte();
          int xlen2 = in.readUnsignedByte();
          this.crc.update(xlen1);
          this.crc.update(xlen2);
          this.xlen |= xlen1 << 8 | xlen2;
        } 
        this.gzipState = GzipState.XLEN_READ;
      case XLEN_READ:
        if (this.xlen != -1) {
          if (in.readableBytes() < this.xlen)
            return false; 
          this.crc.update(in, in.readerIndex(), this.xlen);
          in.skipBytes(this.xlen);
        } 
        this.gzipState = GzipState.SKIP_FNAME;
      case SKIP_FNAME:
        if (!skipIfNeeded(in, 8))
          return false; 
        this.gzipState = GzipState.SKIP_COMMENT;
      case SKIP_COMMENT:
        if (!skipIfNeeded(in, 16))
          return false; 
        this.gzipState = GzipState.PROCESS_FHCRC;
      case PROCESS_FHCRC:
        if ((this.flags & 0x2) != 0 && 
          !verifyCrc16(in))
          return false; 
        this.crc.reset();
        this.gzipState = GzipState.HEADER_END;
      case HEADER_END:
        return true;
    } 
    throw new IllegalStateException();
  }
  
  private boolean skipIfNeeded(ByteBuf in, int flagMask) {
    if ((this.flags & flagMask) != 0) {
      int b;
      do {
        if (!in.isReadable())
          return false; 
        b = in.readUnsignedByte();
        this.crc.update(b);
      } while (b != 0);
    } 
    return true;
  }
  
  private boolean readGZIPFooter(ByteBuf in) {
    if (in.readableBytes() < 8)
      return false; 
    boolean enoughData = verifyCrc(in);
    assert enoughData;
    int dataLength = 0;
    for (int i = 0; i < 4; i++)
      dataLength |= in.readUnsignedByte() << i * 8; 
    int readLength = this.inflater.getTotalOut();
    if (dataLength != readLength)
      throw new DecompressionException("Number of bytes mismatch. Expected: " + dataLength + ", Got: " + readLength); 
    return true;
  }
  
  private boolean verifyCrc(ByteBuf in) {
    if (in.readableBytes() < 4)
      return false; 
    long crcValue = 0L;
    for (int i = 0; i < 4; i++)
      crcValue |= in.readUnsignedByte() << i * 8; 
    long readCrc = this.crc.getValue();
    if (crcValue != readCrc)
      throw new DecompressionException("CRC value mismatch. Expected: " + crcValue + ", Got: " + readCrc); 
    return true;
  }
  
  private boolean verifyCrc16(ByteBuf in) {
    if (in.readableBytes() < 2)
      return false; 
    long readCrc32 = this.crc.getValue();
    long crc16Value = 0L;
    long readCrc16 = 0L;
    for (int i = 0; i < 2; i++) {
      crc16Value |= in.readUnsignedByte() << i * 8;
      readCrc16 |= (readCrc32 >> i * 8 & 0xFFL) << i * 8;
    } 
    if (crc16Value != readCrc16)
      throw new DecompressionException("CRC16 value mismatch. Expected: " + crc16Value + ", Got: " + readCrc16); 
    return true;
  }
  
  private static boolean looksLikeZlib(short cmf_flg) {
    return ((cmf_flg & 0x7800) == 30720 && cmf_flg % 31 == 0);
  }
}