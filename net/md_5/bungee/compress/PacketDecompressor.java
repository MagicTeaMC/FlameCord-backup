package net.md_5.bungee.compress;

import dev._2lstudios.flamecord.natives.MoreByteBufUtils;
import dev._2lstudios.flamecord.natives.compress.Compressor;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;
import java.util.List;
import net.md_5.bungee.protocol.BadPacketException;
import net.md_5.bungee.protocol.DefinedPacket;

public class PacketDecompressor extends MessageToMessageDecoder<ByteBuf> {
  private static final int VANILLA_MAXIMUM_UNCOMPRESSED_SIZE = 8388608;
  
  private static final int MAXIMUM_UNCOMPRESSED_SIZE_WHILE_CHECKING = 33177;
  
  private boolean checking;
  
  private final Compressor compressor;
  
  private final int threshold;
  
  public PacketDecompressor(Compressor compressor, int threshold) {
    this.checking = false;
    this.compressor = compressor;
    this.threshold = threshold;
  }
  
  public void setChecking(boolean checking) {
    this.checking = checking;
  }
  
  public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
    this.compressor.close();
  }
  
  protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
    ByteBuf uncompressed;
    int size = DefinedPacket.readVarInt(in);
    if (size == 0) {
      out.add(in.retain());
      return;
    } 
    if (this.threshold != -1 && size < this.threshold)
      throw new BadPacketException("Uncompressed size " + size + " is less than threshold " + this.threshold); 
    if (size > 8388608)
      throw new BadPacketException("Uncompressed size " + size + " exceeds threshold of " + 8388608); 
    if (this.checking && size > 33177)
      throw new BadPacketException("Uncompressed size " + size + " exceeds threshold of " + '膙' + " (While checking)"); 
    ByteBuf compatibleIn = MoreByteBufUtils.ensureCompatible(ctx.alloc(), this.compressor, in);
    if (this.checking) {
      uncompressed = ctx.alloc().directBuffer(size, size);
    } else {
      uncompressed = ctx.alloc().directBuffer(size);
    } 
    try {
      this.compressor.inflate(compatibleIn, uncompressed, size);
      out.add(uncompressed);
    } catch (Exception e) {
      uncompressed.release();
      throw e;
    } finally {
      compatibleIn.release();
    } 
  }
}
