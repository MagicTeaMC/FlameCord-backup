package org.apache.http.impl.io;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CoderResult;
import java.nio.charset.CodingErrorAction;
import org.apache.http.Consts;
import org.apache.http.io.BufferInfo;
import org.apache.http.io.HttpTransportMetrics;
import org.apache.http.io.SessionInputBuffer;
import org.apache.http.params.HttpParams;
import org.apache.http.util.Args;
import org.apache.http.util.ByteArrayBuffer;
import org.apache.http.util.CharArrayBuffer;

@Deprecated
public abstract class AbstractSessionInputBuffer implements SessionInputBuffer, BufferInfo {
  private InputStream inStream;
  
  private byte[] buffer;
  
  private ByteArrayBuffer lineBuffer;
  
  private Charset charset;
  
  private boolean ascii;
  
  private int maxLineLen;
  
  private int minChunkLimit;
  
  private HttpTransportMetricsImpl metrics;
  
  private CodingErrorAction onMalformedCharAction;
  
  private CodingErrorAction onUnmappableCharAction;
  
  private int bufferPos;
  
  private int bufferLen;
  
  private CharsetDecoder decoder;
  
  private CharBuffer cbuf;
  
  protected void init(InputStream inputStream, int bufferSize, HttpParams params) {
    Args.notNull(inputStream, "Input stream");
    Args.notNegative(bufferSize, "Buffer size");
    Args.notNull(params, "HTTP parameters");
    this.inStream = inputStream;
    this.buffer = new byte[bufferSize];
    this.bufferPos = 0;
    this.bufferLen = 0;
    this.lineBuffer = new ByteArrayBuffer(bufferSize);
    String charset = (String)params.getParameter("http.protocol.element-charset");
    this.charset = (charset != null) ? Charset.forName(charset) : Consts.ASCII;
    this.ascii = this.charset.equals(Consts.ASCII);
    this.decoder = null;
    this.maxLineLen = params.getIntParameter("http.connection.max-line-length", -1);
    this.minChunkLimit = params.getIntParameter("http.connection.min-chunk-limit", 512);
    this.metrics = createTransportMetrics();
    CodingErrorAction a1 = (CodingErrorAction)params.getParameter("http.malformed.input.action");
    this.onMalformedCharAction = (a1 != null) ? a1 : CodingErrorAction.REPORT;
    CodingErrorAction a2 = (CodingErrorAction)params.getParameter("http.unmappable.input.action");
    this.onUnmappableCharAction = (a2 != null) ? a2 : CodingErrorAction.REPORT;
  }
  
  protected HttpTransportMetricsImpl createTransportMetrics() {
    return new HttpTransportMetricsImpl();
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
  
  protected int fillBuffer() throws IOException {
    if (this.bufferPos > 0) {
      int i = this.bufferLen - this.bufferPos;
      if (i > 0)
        System.arraycopy(this.buffer, this.bufferPos, this.buffer, 0, i); 
      this.bufferPos = 0;
      this.bufferLen = i;
    } 
    int off = this.bufferLen;
    int len = this.buffer.length - off;
    int readLen = this.inStream.read(this.buffer, off, len);
    if (readLen == -1)
      return -1; 
    this.bufferLen = off + readLen;
    this.metrics.incrementBytesTransferred(readLen);
    return readLen;
  }
  
  protected boolean hasBufferedData() {
    return (this.bufferPos < this.bufferLen);
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
      int read = this.inStream.read(b, off, len);
      if (read > 0)
        this.metrics.incrementBytesTransferred(read); 
      return read;
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
  
  private int locateLF() {
    for (int i = this.bufferPos; i < this.bufferLen; i++) {
      if (this.buffer[i] == 10)
        return i; 
    } 
    return -1;
  }
  
  public int readLine(CharArrayBuffer charbuffer) throws IOException {
    Args.notNull(charbuffer, "Char array buffer");
    int noRead = 0;
    boolean retry = true;
    while (retry) {
      int i = locateLF();
      if (i != -1) {
        if (this.lineBuffer.isEmpty())
          return lineFromReadBuffer(charbuffer, i); 
        retry = false;
        int len = i + 1 - this.bufferPos;
        this.lineBuffer.append(this.buffer, this.bufferPos, len);
        this.bufferPos = i + 1;
      } else {
        if (hasBufferedData()) {
          int len = this.bufferLen - this.bufferPos;
          this.lineBuffer.append(this.buffer, this.bufferPos, len);
          this.bufferPos = this.bufferLen;
        } 
        noRead = fillBuffer();
        if (noRead == -1)
          retry = false; 
      } 
      if (this.maxLineLen > 0 && this.lineBuffer.length() >= this.maxLineLen)
        throw new IOException("Maximum line length limit exceeded"); 
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
    if (this.ascii) {
      charbuffer.append(this.lineBuffer, 0, len);
    } else {
      ByteBuffer bbuf = ByteBuffer.wrap(this.lineBuffer.buffer(), 0, len);
      len = appendDecoded(charbuffer, bbuf);
    } 
    this.lineBuffer.clear();
    return len;
  }
  
  private int lineFromReadBuffer(CharArrayBuffer charbuffer, int position) throws IOException {
    int off = this.bufferPos;
    int i = position;
    this.bufferPos = i + 1;
    if (i > off && this.buffer[i - 1] == 13)
      i--; 
    int len = i - off;
    if (this.ascii) {
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
    if (this.decoder == null) {
      this.decoder = this.charset.newDecoder();
      this.decoder.onMalformedInput(this.onMalformedCharAction);
      this.decoder.onUnmappableCharacter(this.onUnmappableCharAction);
    } 
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
    if (readLen != -1)
      return charbuffer.toString(); 
    return null;
  }
  
  public HttpTransportMetrics getMetrics() {
    return this.metrics;
  }
}
