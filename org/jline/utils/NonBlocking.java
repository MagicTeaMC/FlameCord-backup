package org.jline.utils;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;
import java.nio.charset.CodingErrorAction;

public class NonBlocking {
  public static NonBlockingPumpReader nonBlockingPumpReader() {
    return new NonBlockingPumpReader();
  }
  
  public static NonBlockingPumpReader nonBlockingPumpReader(int size) {
    return new NonBlockingPumpReader(size);
  }
  
  public static NonBlockingPumpInputStream nonBlockingPumpInputStream() {
    return new NonBlockingPumpInputStream();
  }
  
  public static NonBlockingPumpInputStream nonBlockingPumpInputStream(int size) {
    return new NonBlockingPumpInputStream(size);
  }
  
  public static NonBlockingInputStream nonBlockingStream(NonBlockingReader reader, Charset encoding) {
    return new NonBlockingReaderInputStream(reader, encoding);
  }
  
  public static NonBlockingInputStream nonBlocking(String name, InputStream inputStream) {
    if (inputStream instanceof NonBlockingInputStream)
      return (NonBlockingInputStream)inputStream; 
    return new NonBlockingInputStreamImpl(name, inputStream);
  }
  
  public static NonBlockingReader nonBlocking(String name, Reader reader) {
    if (reader instanceof NonBlockingReader)
      return (NonBlockingReader)reader; 
    return new NonBlockingReaderImpl(name, reader);
  }
  
  public static NonBlockingReader nonBlocking(String name, InputStream inputStream, Charset encoding) {
    return new NonBlockingInputStreamReader(nonBlocking(name, inputStream), encoding);
  }
  
  private static class NonBlockingReaderInputStream extends NonBlockingInputStream {
    private final NonBlockingReader reader;
    
    private final CharsetEncoder encoder;
    
    private final ByteBuffer bytes;
    
    private final CharBuffer chars;
    
    private NonBlockingReaderInputStream(NonBlockingReader reader, Charset charset) {
      this.reader = reader;
      this
        
        .encoder = charset.newEncoder().onUnmappableCharacter(CodingErrorAction.REPLACE).onMalformedInput(CodingErrorAction.REPLACE);
      this.bytes = ByteBuffer.allocate(4);
      this.chars = CharBuffer.allocate(2);
      this.bytes.limit(0);
      this.chars.limit(0);
    }
    
    public int available() {
      return (int)(this.reader.available() * this.encoder.averageBytesPerChar()) + this.bytes
        .remaining();
    }
    
    public void close() throws IOException {
      this.reader.close();
    }
    
    public int read(long timeout, boolean isPeek) throws IOException {
      boolean isInfinite = (timeout <= 0L);
      while (!this.bytes.hasRemaining() && (isInfinite || timeout > 0L)) {
        long start = 0L;
        if (!isInfinite)
          start = System.currentTimeMillis(); 
        int c = this.reader.read(timeout);
        if (c == -1)
          return -1; 
        if (c >= 0) {
          if (!this.chars.hasRemaining()) {
            this.chars.position(0);
            this.chars.limit(0);
          } 
          int l = this.chars.limit();
          this.chars.array()[this.chars.arrayOffset() + l] = (char)c;
          this.chars.limit(l + 1);
          this.bytes.clear();
          this.encoder.encode(this.chars, this.bytes, false);
          this.bytes.flip();
        } 
        if (!isInfinite)
          timeout -= System.currentTimeMillis() - start; 
      } 
      if (this.bytes.hasRemaining()) {
        if (isPeek)
          return this.bytes.get(this.bytes.position()); 
        return this.bytes.get();
      } 
      return -2;
    }
  }
  
  private static class NonBlockingInputStreamReader extends NonBlockingReader {
    private final NonBlockingInputStream input;
    
    private final CharsetDecoder decoder;
    
    private final ByteBuffer bytes;
    
    private final CharBuffer chars;
    
    public NonBlockingInputStreamReader(NonBlockingInputStream inputStream, Charset encoding) {
      this(inputStream, (
          (encoding != null) ? encoding : Charset.defaultCharset()).newDecoder()
          .onMalformedInput(CodingErrorAction.REPLACE)
          .onUnmappableCharacter(CodingErrorAction.REPLACE));
    }
    
    public NonBlockingInputStreamReader(NonBlockingInputStream input, CharsetDecoder decoder) {
      this.input = input;
      this.decoder = decoder;
      this.bytes = ByteBuffer.allocate(4);
      this.chars = CharBuffer.allocate(2);
      this.bytes.limit(0);
      this.chars.limit(0);
    }
    
    protected int read(long timeout, boolean isPeek) throws IOException {
      boolean isInfinite = (timeout <= 0L);
      while (!this.chars.hasRemaining() && (isInfinite || timeout > 0L)) {
        long start = 0L;
        if (!isInfinite)
          start = System.currentTimeMillis(); 
        int b = this.input.read(timeout);
        if (b == -1)
          return -1; 
        if (b >= 0) {
          if (!this.bytes.hasRemaining()) {
            this.bytes.position(0);
            this.bytes.limit(0);
          } 
          int l = this.bytes.limit();
          this.bytes.array()[this.bytes.arrayOffset() + l] = (byte)b;
          this.bytes.limit(l + 1);
          this.chars.clear();
          this.decoder.decode(this.bytes, this.chars, false);
          this.chars.flip();
        } 
        if (!isInfinite)
          timeout -= System.currentTimeMillis() - start; 
      } 
      if (this.chars.hasRemaining()) {
        if (isPeek)
          return this.chars.get(this.chars.position()); 
        return this.chars.get();
      } 
      return -2;
    }
    
    public int readBuffered(char[] b) throws IOException {
      if (b == null)
        throw new NullPointerException(); 
      if (b.length == 0)
        return 0; 
      if (this.chars.hasRemaining()) {
        int r = Math.min(b.length, this.chars.remaining());
        this.chars.get(b);
        return r;
      } 
      byte[] buf = new byte[b.length];
      int l = this.input.readBuffered(buf);
      if (l < 0)
        return l; 
      ByteBuffer bytes = ByteBuffer.wrap(buf, 0, l);
      CharBuffer chars = CharBuffer.wrap(b);
      this.decoder.decode(bytes, chars, false);
      chars.flip();
      return chars.remaining();
    }
    
    public void shutdown() {
      this.input.shutdown();
    }
    
    public void close() throws IOException {
      this.input.close();
    }
  }
}
