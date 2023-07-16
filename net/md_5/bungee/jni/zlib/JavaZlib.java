package net.md_5.bungee.jni.zlib;

import io.netty.buffer.ByteBuf;
import java.util.zip.DataFormatException;
import java.util.zip.Deflater;
import java.util.zip.Inflater;

public class JavaZlib implements BungeeZlib {
  private final byte[] buffer = new byte[8192];
  
  private boolean compress;
  
  private Deflater deflater;
  
  private Inflater inflater;
  
  public void init(boolean compress, int level) {
    this.compress = compress;
    free();
    if (compress) {
      this.deflater = new Deflater(level);
    } else {
      this.inflater = new Inflater();
    } 
  }
  
  public void free() {
    if (this.deflater != null)
      this.deflater.end(); 
    if (this.inflater != null)
      this.inflater.end(); 
  }
  
  public void process(ByteBuf in, ByteBuf out) throws DataFormatException {
    byte[] inData = new byte[in.readableBytes()];
    in.readBytes(inData);
    if (this.compress) {
      this.deflater.setInput(inData);
      this.deflater.finish();
      while (!this.deflater.finished()) {
        int count = this.deflater.deflate(this.buffer);
        out.writeBytes(this.buffer, 0, count);
      } 
      this.deflater.reset();
    } else {
      this.inflater.setInput(inData);
      while (!this.inflater.finished() && this.inflater.getTotalIn() < inData.length) {
        int count = this.inflater.inflate(this.buffer);
        out.writeBytes(this.buffer, 0, count);
      } 
      this.inflater.reset();
    } 
  }
}
