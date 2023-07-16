package io.netty.handler.codec.compression;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import java.util.List;

public class SnappyFrameDecoder extends ByteToMessageDecoder {
  private static final int SNAPPY_IDENTIFIER_LEN = 6;
  
  private static final int MAX_UNCOMPRESSED_DATA_SIZE = 65540;
  
  private static final int MAX_DECOMPRESSED_DATA_SIZE = 65536;
  
  private static final int MAX_COMPRESSED_CHUNK_SIZE = 16777215;
  
  private enum ChunkType {
    STREAM_IDENTIFIER, COMPRESSED_DATA, UNCOMPRESSED_DATA, RESERVED_UNSKIPPABLE, RESERVED_SKIPPABLE;
  }
  
  private final Snappy snappy = new Snappy();
  
  private final boolean validateChecksums;
  
  private boolean started;
  
  private boolean corrupted;
  
  private int numBytesToSkip;
  
  public SnappyFrameDecoder() {
    this(false);
  }
  
  public SnappyFrameDecoder(boolean validateChecksums) {
    this.validateChecksums = validateChecksums;
  }
  
  protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
    if (this.corrupted) {
      in.skipBytes(in.readableBytes());
      return;
    } 
    if (this.numBytesToSkip != 0) {
      int skipBytes = Math.min(this.numBytesToSkip, in.readableBytes());
      in.skipBytes(skipBytes);
      this.numBytesToSkip -= skipBytes;
      return;
    } 
    try {
      int offset, skipBytes, checksum, uncompressedSize;
      ByteBuf uncompressed;
      int idx = in.readerIndex();
      int inSize = in.readableBytes();
      if (inSize < 4)
        return; 
      int chunkTypeVal = in.getUnsignedByte(idx);
      ChunkType chunkType = mapChunkType((byte)chunkTypeVal);
      int chunkLength = in.getUnsignedMediumLE(idx + 1);
      switch (chunkType) {
        case STREAM_IDENTIFIER:
          if (chunkLength != 6)
            throw new DecompressionException("Unexpected length of stream identifier: " + chunkLength); 
          if (inSize < 10)
            break; 
          in.skipBytes(4);
          offset = in.readerIndex();
          in.skipBytes(6);
          checkByte(in.getByte(offset++), (byte)115);
          checkByte(in.getByte(offset++), (byte)78);
          checkByte(in.getByte(offset++), (byte)97);
          checkByte(in.getByte(offset++), (byte)80);
          checkByte(in.getByte(offset++), (byte)112);
          checkByte(in.getByte(offset), (byte)89);
          this.started = true;
          break;
        case RESERVED_SKIPPABLE:
          if (!this.started)
            throw new DecompressionException("Received RESERVED_SKIPPABLE tag before STREAM_IDENTIFIER"); 
          in.skipBytes(4);
          skipBytes = Math.min(chunkLength, in.readableBytes());
          in.skipBytes(skipBytes);
          if (skipBytes != chunkLength)
            this.numBytesToSkip = chunkLength - skipBytes; 
          break;
        case RESERVED_UNSKIPPABLE:
          throw new DecompressionException("Found reserved unskippable chunk type: 0x" + 
              Integer.toHexString(chunkTypeVal));
        case UNCOMPRESSED_DATA:
          if (!this.started)
            throw new DecompressionException("Received UNCOMPRESSED_DATA tag before STREAM_IDENTIFIER"); 
          if (chunkLength > 65540)
            throw new DecompressionException("Received UNCOMPRESSED_DATA larger than 65540 bytes"); 
          if (inSize < 4 + chunkLength)
            return; 
          in.skipBytes(4);
          if (this.validateChecksums) {
            int i = in.readIntLE();
            Snappy.validateChecksum(i, in, in.readerIndex(), chunkLength - 4);
          } else {
            in.skipBytes(4);
          } 
          out.add(in.readRetainedSlice(chunkLength - 4));
          break;
        case COMPRESSED_DATA:
          if (!this.started)
            throw new DecompressionException("Received COMPRESSED_DATA tag before STREAM_IDENTIFIER"); 
          if (chunkLength > 16777215)
            throw new DecompressionException("Received COMPRESSED_DATA that contains chunk that exceeds 16777215 bytes"); 
          if (inSize < 4 + chunkLength)
            return; 
          in.skipBytes(4);
          checksum = in.readIntLE();
          uncompressedSize = this.snappy.getPreamble(in);
          if (uncompressedSize > 65536)
            throw new DecompressionException("Received COMPRESSED_DATA that contains uncompressed data that exceeds 65536 bytes"); 
          uncompressed = ctx.alloc().buffer(uncompressedSize, 65536);
          try {
            if (this.validateChecksums) {
              int oldWriterIndex = in.writerIndex();
              try {
                in.writerIndex(in.readerIndex() + chunkLength - 4);
                this.snappy.decode(in, uncompressed);
              } finally {
                in.writerIndex(oldWriterIndex);
              } 
              Snappy.validateChecksum(checksum, uncompressed, 0, uncompressed.writerIndex());
            } else {
              this.snappy.decode(in.readSlice(chunkLength - 4), uncompressed);
            } 
            out.add(uncompressed);
            uncompressed = null;
          } finally {
            if (uncompressed != null)
              uncompressed.release(); 
          } 
          this.snappy.reset();
          break;
      } 
    } catch (Exception e) {
      this.corrupted = true;
      throw e;
    } 
  }
  
  private static void checkByte(byte actual, byte expect) {
    if (actual != expect)
      throw new DecompressionException("Unexpected stream identifier contents. Mismatched snappy protocol version?"); 
  }
  
  private static ChunkType mapChunkType(byte type) {
    if (type == 0)
      return ChunkType.COMPRESSED_DATA; 
    if (type == 1)
      return ChunkType.UNCOMPRESSED_DATA; 
    if (type == -1)
      return ChunkType.STREAM_IDENTIFIER; 
    if ((type & 0x80) == 128)
      return ChunkType.RESERVED_SKIPPABLE; 
    return ChunkType.RESERVED_UNSKIPPABLE;
  }
}