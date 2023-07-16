package io.netty.handler.codec.compression;

import com.ning.compress.BufferRecycler;
import com.ning.compress.lzf.ChunkEncoder;
import com.ning.compress.lzf.LZFChunk;
import com.ning.compress.lzf.LZFEncoder;
import com.ning.compress.lzf.util.ChunkEncoderFactory;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

public class LzfEncoder extends MessageToByteEncoder<ByteBuf> {
  private static final int MIN_BLOCK_TO_COMPRESS = 16;
  
  private final int compressThreshold;
  
  private final ChunkEncoder encoder;
  
  private final BufferRecycler recycler;
  
  public LzfEncoder() {
    this(false);
  }
  
  public LzfEncoder(boolean safeInstance) {
    this(safeInstance, 65535);
  }
  
  public LzfEncoder(boolean safeInstance, int totalLength) {
    this(safeInstance, totalLength, 16);
  }
  
  public LzfEncoder(int totalLength) {
    this(false, totalLength);
  }
  
  public LzfEncoder(boolean safeInstance, int totalLength, int compressThreshold) {
    super(false);
    if (totalLength < 16 || totalLength > 65535)
      throw new IllegalArgumentException("totalLength: " + totalLength + " (expected: " + '\020' + '-' + Character.MAX_VALUE + ')'); 
    if (compressThreshold < 16)
      throw new IllegalArgumentException("compressThreshold:" + compressThreshold + " expected >=" + '\020'); 
    this.compressThreshold = compressThreshold;
    this
      
      .encoder = safeInstance ? ChunkEncoderFactory.safeNonAllocatingInstance(totalLength) : ChunkEncoderFactory.optimalNonAllocatingInstance(totalLength);
    this.recycler = BufferRecycler.instance();
  }
  
  protected void encode(ChannelHandlerContext ctx, ByteBuf in, ByteBuf out) throws Exception {
    byte[] input;
    int inputPtr;
    byte[] output;
    int outputPtr, outputLength, length = in.readableBytes();
    int idx = in.readerIndex();
    if (in.hasArray()) {
      input = in.array();
      inputPtr = in.arrayOffset() + idx;
    } else {
      input = this.recycler.allocInputBuffer(length);
      in.getBytes(idx, input, 0, length);
      inputPtr = 0;
    } 
    int maxOutputLength = LZFEncoder.estimateMaxWorkspaceSize(length) + 1;
    out.ensureWritable(maxOutputLength);
    if (out.hasArray()) {
      output = out.array();
      outputPtr = out.arrayOffset() + out.writerIndex();
    } else {
      output = new byte[maxOutputLength];
      outputPtr = 0;
    } 
    if (length >= this.compressThreshold) {
      outputLength = encodeCompress(input, inputPtr, length, output, outputPtr);
    } else {
      outputLength = encodeNonCompress(input, inputPtr, length, output, outputPtr);
    } 
    if (out.hasArray()) {
      out.writerIndex(out.writerIndex() + outputLength);
    } else {
      out.writeBytes(output, 0, outputLength);
    } 
    in.skipBytes(length);
    if (!in.hasArray())
      this.recycler.releaseInputBuffer(input); 
  }
  
  private int encodeCompress(byte[] input, int inputPtr, int length, byte[] output, int outputPtr) {
    return LZFEncoder.appendEncoded(this.encoder, input, inputPtr, length, output, outputPtr) - outputPtr;
  }
  
  private static int lzfEncodeNonCompress(byte[] input, int inputPtr, int length, byte[] output, int outputPtr) {
    int left = length;
    int chunkLen = Math.min(65535, left);
    outputPtr = LZFChunk.appendNonCompressed(input, inputPtr, chunkLen, output, outputPtr);
    left -= chunkLen;
    if (left < 1)
      return outputPtr; 
    inputPtr += chunkLen;
    while (true) {
      chunkLen = Math.min(left, 65535);
      outputPtr = LZFChunk.appendNonCompressed(input, inputPtr, chunkLen, output, outputPtr);
      inputPtr += chunkLen;
      left -= chunkLen;
      if (left <= 0)
        return outputPtr; 
    } 
  }
  
  private static int encodeNonCompress(byte[] input, int inputPtr, int length, byte[] output, int outputPtr) {
    return lzfEncodeNonCompress(input, inputPtr, length, output, outputPtr) - outputPtr;
  }
  
  public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
    this.encoder.close();
    super.handlerRemoved(ctx);
  }
}
