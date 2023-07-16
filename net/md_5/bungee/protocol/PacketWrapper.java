package net.md_5.bungee.protocol;

import io.netty.buffer.ByteBuf;

public class PacketWrapper {
  public final DefinedPacket packet;
  
  public final ByteBuf buf;
  
  private boolean released;
  
  public PacketWrapper(DefinedPacket packet, ByteBuf buf) {
    this.packet = packet;
    this.buf = buf;
  }
  
  public void setReleased(boolean released) {
    this.released = released;
  }
  
  public void trySingleRelease() {
    if (!this.released) {
      this.buf.release();
      this.released = true;
    } 
  }
}
