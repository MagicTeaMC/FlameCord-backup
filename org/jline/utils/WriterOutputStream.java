package org.jline.utils;

import java.io.IOException;
import java.io.OutputStream;
import java.io.Writer;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CoderResult;
import java.nio.charset.CodingErrorAction;

public class WriterOutputStream extends OutputStream {
  private final Writer out;
  
  private final CharsetDecoder decoder;
  
  private final ByteBuffer decoderIn = ByteBuffer.allocate(256);
  
  private final CharBuffer decoderOut = CharBuffer.allocate(128);
  
  public WriterOutputStream(Writer out, Charset charset) {
    this(out, charset.newDecoder()
        .onMalformedInput(CodingErrorAction.REPLACE)
        .onUnmappableCharacter(CodingErrorAction.REPLACE));
  }
  
  public WriterOutputStream(Writer out, CharsetDecoder decoder) {
    this.out = out;
    this.decoder = decoder;
  }
  
  public void write(int b) throws IOException {
    write(new byte[] { (byte)b }, 0, 1);
  }
  
  public void write(byte[] b) throws IOException {
    write(b, 0, b.length);
  }
  
  public void write(byte[] b, int off, int len) throws IOException {
    while (len > 0) {
      int c = Math.min(len, this.decoderIn.remaining());
      this.decoderIn.put(b, off, c);
      processInput(false);
      len -= c;
      off += c;
    } 
    flush();
  }
  
  public void flush() throws IOException {
    flushOutput();
    this.out.flush();
  }
  
  public void close() throws IOException {
    processInput(true);
    flush();
    this.out.close();
  }
  
  private void processInput(boolean endOfInput) throws IOException {
    CoderResult coderResult;
    this.decoderIn.flip();
    while (true) {
      coderResult = this.decoder.decode(this.decoderIn, this.decoderOut, endOfInput);
      if (coderResult.isOverflow()) {
        flushOutput();
        continue;
      } 
      break;
    } 
    if (coderResult.isUnderflow()) {
      this.decoderIn.compact();
      return;
    } 
    throw new IOException("Unexpected coder result");
  }
  
  private void flushOutput() throws IOException {
    if (this.decoderOut.position() > 0) {
      this.out.write(this.decoderOut.array(), 0, this.decoderOut.position());
      this.decoderOut.rewind();
    } 
  }
}
