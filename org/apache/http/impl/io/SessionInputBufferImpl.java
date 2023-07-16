package org.apache.http.impl.io;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CoderResult;
import org.apache.http.MessageConstraintException;
import org.apache.http.config.MessageConstraints;
import org.apache.http.io.BufferInfo;
import org.apache.http.io.HttpTransportMetrics;
import org.apache.http.io.SessionInputBuffer;
import org.apache.http.util.Args;
import org.apache.http.util.Asserts;
import org.apache.http.util.ByteArrayBuffer;
import org.apache.http.util.CharArrayBuffer;

public class SessionInputBufferImpl implements SessionInputBuffer, BufferInfo {
  private final HttpTransportMetricsImpl metrics;
  
  private final byte[] buffer;
  
  private final ByteArrayBuffer lineBuffer;
  
  private final int minChunkLimit;
  
  private final MessageConstraints constraints;
  
  private final CharsetDecoder decoder;
  
  private InputStream inStream;
  
  private int bufferPos;
  
  private int bufferLen;
  
  private CharBuffer cbuf;
  
  public SessionInputBufferImpl(HttpTransportMetricsImpl metrics, int bufferSize, int minChunkLimit, MessageConstraints constraints, CharsetDecoder charDecoder) {
    Args.notNull(metrics, "HTTP transport metrcis");
    Args.positive(bufferSize, "Buffer size");
    this.metrics = metrics;
    this.buffer = new byte[bufferSize];
    this.bufferPos = 0;
    this.bufferLen = 0;
    this.minChunkLimit = (minChunkLimit >= 0) ? minChunkLimit : 512;
    this.constraints = (constraints != null) ? constraints : MessageConstraints.DEFAULT;
    this.lineBuffer = new ByteArrayBuffer(bufferSize);
    this.decoder = charDecoder;
  }
  
  public SessionInputBufferImpl(HttpTransportMetricsImpl metrics, int bufferSize) {
    this(metrics, bufferSize, bufferSize, null, null);
  }
  
  public void bind(InputStream inputStream) {
    this.inStream = inputStream;
  }
  
  public boolean isBound() {
    return (this.inStream != null);
  }
  
  public int capacity() {
    return this.buffer.length;
  }
  
  public int length() {
    return this.bufferLen - this.bufferPos;
  }
  
  public int available() {
    return capacity() - length();
  }
  
  private int streamRead(byte[] b, int off, int len) throws IOException {
    Asserts.notNull(this.inStream, "Input stream");
    return this.inStream.read(b, off, len);
  }
  
  public int fillBuffer() throws IOException {
    if (this.bufferPos > 0) {
      int i = this.bufferLen - this.bufferPos;
      if (i > 0)
        System.arraycopy(this.buffer, this.bufferPos, this.buffer, 0, i); 
      this.bufferPos = 0;
      this.bufferLen = i;
    } 
    int off = this.bufferLen;
    int len = this.buffer.length - off;
    int readLen = streamRead(this.buffer, off, len);
    if (readLen == -1)
      return -1; 
    this.bufferLen = off + readLen;
    this.metrics.incrementBytesTransferred(readLen);
    return readLen;
  }
  
  public boolean hasBufferedData() {
    return (this.bufferPos < this.bufferLen);
  }
  
  public void clear() {
    this.bufferPos = 0;
    this.bufferLen = 0;
  }
  
  public int read() throws IOException {
    while (!hasBufferedData()) {
      int noRead = fillBuffer();
      if (noRead == -1)
        return -1; 
    } 
    return this.buffer[this.bufferPos++] & 0xFF;
  }
  
  public int read(byte[] b, int off, int len) throws IOException {
    if (b == null)
      return 0; 
    if (hasBufferedData()) {
      int i = Math.min(len, this.bufferLen - this.bufferPos);
      System.arraycopy(this.buffer, this.bufferPos, b, off, i);
      this.bufferPos += i;
      return i;
    } 
    if (len > this.minChunkLimit) {
      int readLen = streamRead(b, off, len);
      if (readLen > 0)
        this.metrics.incrementBytesTransferred(readLen); 
      return readLen;
    } 
    while (!hasBufferedData()) {
      int noRead = fillBuffer();
      if (noRead == -1)
        return -1; 
    } 
    int chunk = Math.min(len, this.bufferLen - this.bufferPos);
    System.arraycopy(this.buffer, this.bufferPos, b, off, chunk);
    this.bufferPos += chunk;
    return chunk;
  }
  
  public int read(byte[] b) throws IOException {
    if (b == null)
      return 0; 
    return read(b, 0, b.length);
  }
  
  public int readLine(CharArrayBuffer charbuffer) throws IOException {
    Args.notNull(charbuffer, "Char array buffer");
    int maxLineLen = this.constraints.getMaxLineLength();
    int noRead = 0;
    boolean retry = true;
    while (retry) {
      int pos = -1;
      for (int i = this.bufferPos; i < this.bufferLen; i++) {
        if (this.buffer[i] == 10) {
          pos = i;
          break;
        } 
      } 
      if (maxLineLen > 0) {
        int currentLen = this.lineBuffer.length() + ((pos >= 0) ? pos : this.bufferLen) - this.bufferPos;
        if (currentLen >= maxLineLen)
          throw new MessageConstraintException("Maximum line length limit exceeded"); 
      } 
      if (pos != -1) {
        if (this.lineBuffer.isEmpty())
          return lineFromReadBuffer(charbuffer, pos); 
        retry = false;
        int len = pos + 1 - this.bufferPos;
        this.lineBuffer.append(this.buffer, this.bufferPos, len);
        this.bufferPos = pos + 1;
        continue;
      } 
      if (hasBufferedData()) {
        int len = this.bufferLen - this.bufferPos;
        this.lineBuffer.append(this.buffer, this.bufferPos, len);
        this.bufferPos = this.bufferLen;
      } 
      noRead = fillBuffer();
      if (noRead == -1)
        retry = false; 
    } 
    if (noRead == -1 && this.lineBuffer.isEmpty())
      return -1; 
    return lineFromLineBuffer(charbuffer);
  }
  
  private int lineFromLineBuffer(CharArrayBuffer charbuffer) throws IOException {
    int len = this.lineBuffer.length();
    if (len > 0) {
      if (this.lineBuffer.byteAt(len - 1) == 10)
        len--; 
      if (len > 0 && 
        this.lineBuffer.byteAt(len - 1) == 13)
        len--; 
    } 
    if (this.decoder == null) {
      charbuffer.append(this.lineBuffer, 0, len);
    } else {
      ByteBuffer bbuf = ByteBuffer.wrap(this.lineBuffer.buffer(), 0, len);
      len = appendDecoded(charbuffer, bbuf);
    } 
    this.lineBuffer.clear();
    return len;
  }
  
  private int lineFromReadBuffer(CharArrayBuffer charbuffer, int position) throws IOException {
    int pos = position;
    int off = this.bufferPos;
    this.bufferPos = pos + 1;
    if (pos > off && this.buffer[pos - 1] == 13)
      pos--; 
    int len = pos - off;
    if (this.decoder == null) {
      charbuffer.append(this.buffer, off, len);
    } else {
      ByteBuffer bbuf = ByteBuffer.wrap(this.buffer, off, len);
      len = appendDecoded(charbuffer, bbuf);
    } 
    return len;
  }
  
  private int appendDecoded(CharArrayBuffer charbuffer, ByteBuffer bbuf) throws IOException {
    if (!bbuf.hasRemaining())
      return 0; 
    if (this.cbuf == null)
      this.cbuf = CharBuffer.allocate(1024); 
    this.decoder.reset();
    int len = 0;
    while (bbuf.hasRemaining()) {
      CoderResult coderResult = this.decoder.decode(bbuf, this.cbuf, true);
      len += handleDecodingResult(coderResult, charbuffer, bbuf);
    } 
    CoderResult result = this.decoder.flush(this.cbuf);
    len += handleDecodingResult(result, charbuffer, bbuf);
    this.cbuf.clear();
    return len;
  }
  
  private int handleDecodingResult(CoderResult result, CharArrayBuffer charbuffer, ByteBuffer bbuf) throws IOException {
    if (result.isError())
      result.throwException(); 
    this.cbuf.flip();
    int len = this.cbuf.remaining();
    while (this.cbuf.hasRemaining())
      charbuffer.append(this.cbuf.get()); 
    this.cbuf.compact();
    return len;
  }
  
  public String readLine() throws IOException {
    CharArrayBuffer charbuffer = new CharArrayBuffer(64);
    int readLen = readLine(charbuffer);
    return (readLen != -1) ? charbuffer.toString() : null;
  }
  
  public boolean isDataAvailable(int timeout) throws IOException {
    return hasBufferedData();
  }
  
  public HttpTransportMetrics getMetrics() {
    return this.metrics;
  }
}
