package net.md_5.bungee.compress;

import dev._2lstudios.flamecord.natives.MoreByteBufUtils;
import dev._2lstudios.flamecord.natives.compress.Compressor;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import java.util.zip.DataFormatException;
import net.md_5.bungee.protocol.DefinedPacket;

public class PacketCompressor extends MessageToByteEncoder<ByteBuf> {
  private final Compressor compressor;
  
  private int threshold = 256;
  
  public void setThreshold(int threshold) {
    this.threshold = threshold;
  }
  
  public PacketCompressor(Compressor compressor) {
    this.compressor = compressor;
  }
  
  public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
    this.compressor.close();
  }
  
  protected void encode(ChannelHandlerContext ctx, ByteBuf msg, ByteBuf out) throws Exception {
    int origSize = msg.readableBytes();
    if (origSize < this.threshold) {
      DefinedPacket.writeVarInt(0, out);
      out.writeBytes(msg);
      return;
    } 
    int uncompressed = msg.readableBytes();
    DefinedPacket.writeVarInt(uncompressed, out);
    ByteBuf compatibleIn = MoreByteBufUtils.ensureCompatible(ctx.alloc(), this.compressor, msg);
    int startCompressed = out.writerIndex();
    try {
      this.compressor.deflate(compatibleIn, out);
    } finally {
      compatibleIn.release();
    } 
    int compressedLength = out.writerIndex() - startCompressed;
    if (compressedLength >= 2097152)
      throw new DataFormatException("The server sent a very large (over 2MiB compressed) packet."); 
  }
  
  protected ByteBuf allocateBuffer(ChannelHandlerContext ctx, ByteBuf msg, boolean preferDirect) throws Exception {
    return allocateByteBufForCompression(ctx.alloc(), msg, this.threshold);
  }
  
  public static ByteBuf allocateByteBufForCompression(ByteBufAllocator allocator, ByteBuf msg, int threshold) {
    int uncompressed = msg.readableBytes();
    if (uncompressed < threshold) {
      int finalBufferSize = uncompressed + 1;
      finalBufferSize += DefinedPacket.varIntBytes(finalBufferSize);
      return allocator.directBuffer(finalBufferSize);
    } 
    int initialBufferSize = uncompressed - 1 + 3 + DefinedPacket.varIntBytes(uncompressed);
    return allocator.directBuffer(initialBufferSize);
  }
}
