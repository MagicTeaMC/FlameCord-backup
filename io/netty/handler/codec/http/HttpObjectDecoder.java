package io.netty.handler.codec.http;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.handler.codec.DecoderResult;
import io.netty.handler.codec.PrematureChannelClosureException;
import io.netty.handler.codec.TooLongFrameException;
import io.netty.util.AsciiString;
import io.netty.util.ByteProcessor;
import io.netty.util.internal.ObjectUtil;
import io.netty.util.internal.StringUtil;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public abstract class HttpObjectDecoder extends ByteToMessageDecoder {
  public static final int DEFAULT_MAX_INITIAL_LINE_LENGTH = 4096;
  
  public static final int DEFAULT_MAX_HEADER_SIZE = 8192;
  
  public static final boolean DEFAULT_CHUNKED_SUPPORTED = true;
  
  public static final boolean DEFAULT_ALLOW_PARTIAL_CHUNKS = true;
  
  public static final int DEFAULT_MAX_CHUNK_SIZE = 8192;
  
  public static final boolean DEFAULT_VALIDATE_HEADERS = true;
  
  public static final int DEFAULT_INITIAL_BUFFER_SIZE = 128;
  
  public static final boolean DEFAULT_ALLOW_DUPLICATE_CONTENT_LENGTHS = false;
  
  private final int maxChunkSize;
  
  private final boolean chunkedSupported;
  
  private final boolean allowPartialChunks;
  
  protected final boolean validateHeaders;
  
  private final boolean allowDuplicateContentLengths;
  
  private final ByteBuf parserScratchBuffer;
  
  private final HeaderParser headerParser;
  
  private final LineParser lineParser;
  
  private HttpMessage message;
  
  private long chunkSize;
  
  private long contentLength = Long.MIN_VALUE;
  
  private final AtomicBoolean resetRequested = new AtomicBoolean();
  
  private AsciiString name;
  
  private String value;
  
  private LastHttpContent trailer;
  
  protected void handlerRemoved0(ChannelHandlerContext ctx) throws Exception {
    try {
      this.parserScratchBuffer.release();
    } finally {
      super.handlerRemoved0(ctx);
    } 
  }
  
  private enum State {
    SKIP_CONTROL_CHARS, READ_INITIAL, READ_HEADER, READ_VARIABLE_LENGTH_CONTENT, READ_FIXED_LENGTH_CONTENT, READ_CHUNK_SIZE, READ_CHUNKED_CONTENT, READ_CHUNK_DELIMITER, READ_CHUNK_FOOTER, BAD_MESSAGE, UPGRADED;
  }
  
  private State currentState = State.SKIP_CONTROL_CHARS;
  
  protected HttpObjectDecoder() {
    this(4096, 8192, 8192, true);
  }
  
  protected HttpObjectDecoder(int maxInitialLineLength, int maxHeaderSize, int maxChunkSize, boolean chunkedSupported) {
    this(maxInitialLineLength, maxHeaderSize, maxChunkSize, chunkedSupported, true);
  }
  
  protected HttpObjectDecoder(int maxInitialLineLength, int maxHeaderSize, int maxChunkSize, boolean chunkedSupported, boolean validateHeaders) {
    this(maxInitialLineLength, maxHeaderSize, maxChunkSize, chunkedSupported, validateHeaders, 128);
  }
  
  protected HttpObjectDecoder(int maxInitialLineLength, int maxHeaderSize, int maxChunkSize, boolean chunkedSupported, boolean validateHeaders, int initialBufferSize) {
    this(maxInitialLineLength, maxHeaderSize, maxChunkSize, chunkedSupported, validateHeaders, initialBufferSize, false);
  }
  
  protected HttpObjectDecoder(int maxInitialLineLength, int maxHeaderSize, int maxChunkSize, boolean chunkedSupported, boolean validateHeaders, int initialBufferSize, boolean allowDuplicateContentLengths) {
    this(maxInitialLineLength, maxHeaderSize, maxChunkSize, chunkedSupported, validateHeaders, initialBufferSize, allowDuplicateContentLengths, true);
  }
  
  protected HttpObjectDecoder(int maxInitialLineLength, int maxHeaderSize, int maxChunkSize, boolean chunkedSupported, boolean validateHeaders, int initialBufferSize, boolean allowDuplicateContentLengths, boolean allowPartialChunks) {
    ObjectUtil.checkPositive(maxInitialLineLength, "maxInitialLineLength");
    ObjectUtil.checkPositive(maxHeaderSize, "maxHeaderSize");
    ObjectUtil.checkPositive(maxChunkSize, "maxChunkSize");
    this.parserScratchBuffer = Unpooled.buffer(initialBufferSize);
    this.lineParser = new LineParser(this.parserScratchBuffer, maxInitialLineLength);
    this.headerParser = new HeaderParser(this.parserScratchBuffer, maxHeaderSize);
    this.maxChunkSize = maxChunkSize;
    this.chunkedSupported = chunkedSupported;
    this.validateHeaders = validateHeaders;
    this.allowDuplicateContentLengths = allowDuplicateContentLengths;
    this.allowPartialChunks = allowPartialChunks;
  }
  
  protected void decode(ChannelHandlerContext ctx, ByteBuf buffer, List<Object> out) throws Exception {
    int i;
    int readLimit;
    int toRead;
    int wIdx;
    int readableBytes;
    int j;
    HttpContent chunk;
    int rIdx;
    ByteBuf content;
    if (this.resetRequested.get())
      resetNow(); 
    switch (this.currentState) {
      case SKIP_CONTROL_CHARS:
      case READ_INITIAL:
        try {
          ByteBuf line = this.lineParser.parse(buffer);
          if (line == null)
            return; 
          String[] initialLine = splitInitialLine(line);
          assert initialLine.length == 3 : "initialLine::length must be 3";
          this.message = createMessage(initialLine);
          this.currentState = State.READ_HEADER;
        } catch (Exception e) {
          out.add(invalidMessage(buffer, e));
          return;
        } 
      case READ_HEADER:
        try {
          State nextState = readHeaders(buffer);
          if (nextState == null)
            return; 
          this.currentState = nextState;
          switch (nextState) {
            case SKIP_CONTROL_CHARS:
              out.add(this.message);
              out.add(LastHttpContent.EMPTY_LAST_CONTENT);
              resetNow();
              return;
            case READ_CHUNK_SIZE:
              if (!this.chunkedSupported)
                throw new IllegalArgumentException("Chunked messages not supported"); 
              out.add(this.message);
              return;
          } 
          long contentLength = contentLength();
          if (contentLength == 0L || (contentLength == -1L && isDecodingRequest())) {
            out.add(this.message);
            out.add(LastHttpContent.EMPTY_LAST_CONTENT);
            resetNow();
            return;
          } 
          assert nextState == State.READ_FIXED_LENGTH_CONTENT || nextState == State.READ_VARIABLE_LENGTH_CONTENT;
          out.add(this.message);
          if (nextState == State.READ_FIXED_LENGTH_CONTENT)
            this.chunkSize = contentLength; 
          return;
        } catch (Exception e) {
          out.add(invalidMessage(buffer, e));
          return;
        } 
      case READ_VARIABLE_LENGTH_CONTENT:
        i = Math.min(buffer.readableBytes(), this.maxChunkSize);
        if (i > 0) {
          ByteBuf byteBuf = buffer.readRetainedSlice(i);
          out.add(new DefaultHttpContent(byteBuf));
        } 
        return;
      case READ_FIXED_LENGTH_CONTENT:
        readLimit = buffer.readableBytes();
        if (readLimit == 0)
          return; 
        j = Math.min(readLimit, this.maxChunkSize);
        if (j > this.chunkSize)
          j = (int)this.chunkSize; 
        content = buffer.readRetainedSlice(j);
        this.chunkSize -= j;
        if (this.chunkSize == 0L) {
          out.add(new DefaultLastHttpContent(content, this.validateHeaders));
          resetNow();
        } else {
          out.add(new DefaultHttpContent(content));
        } 
        return;
      case READ_CHUNK_SIZE:
        try {
          ByteBuf line = this.lineParser.parse(buffer);
          if (line == null)
            return; 
          int chunkSize = getChunkSize(line.array(), line.arrayOffset() + line.readerIndex(), line.readableBytes());
          this.chunkSize = chunkSize;
          if (chunkSize == 0) {
            this.currentState = State.READ_CHUNK_FOOTER;
            return;
          } 
          this.currentState = State.READ_CHUNKED_CONTENT;
        } catch (Exception e) {
          out.add(invalidChunk(buffer, e));
          return;
        } 
      case READ_CHUNKED_CONTENT:
        assert this.chunkSize <= 2147483647L;
        toRead = Math.min((int)this.chunkSize, this.maxChunkSize);
        if (!this.allowPartialChunks && buffer.readableBytes() < toRead)
          return; 
        toRead = Math.min(toRead, buffer.readableBytes());
        if (toRead == 0)
          return; 
        chunk = new DefaultHttpContent(buffer.readRetainedSlice(toRead));
        this.chunkSize -= toRead;
        out.add(chunk);
        if (this.chunkSize != 0L)
          return; 
        this.currentState = State.READ_CHUNK_DELIMITER;
      case READ_CHUNK_DELIMITER:
        wIdx = buffer.writerIndex();
        rIdx = buffer.readerIndex();
        while (wIdx > rIdx) {
          byte next = buffer.getByte(rIdx++);
          if (next == 10) {
            this.currentState = State.READ_CHUNK_SIZE;
            break;
          } 
        } 
        buffer.readerIndex(rIdx);
        return;
      case READ_CHUNK_FOOTER:
        try {
          LastHttpContent trailer = readTrailingHeaders(buffer);
          if (trailer == null)
            return; 
          out.add(trailer);
          resetNow();
          return;
        } catch (Exception e) {
          out.add(invalidChunk(buffer, e));
          return;
        } 
      case BAD_MESSAGE:
        buffer.skipBytes(buffer.readableBytes());
        break;
      case UPGRADED:
        readableBytes = buffer.readableBytes();
        if (readableBytes > 0)
          out.add(buffer.readBytes(readableBytes)); 
        break;
    } 
  }
  
  protected void decodeLast(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
    super.decodeLast(ctx, in, out);
    if (this.resetRequested.get())
      resetNow(); 
    if (this.message != null) {
      boolean prematureClosure, chunked = HttpUtil.isTransferEncodingChunked(this.message);
      if (this.currentState == State.READ_VARIABLE_LENGTH_CONTENT && !in.isReadable() && !chunked) {
        out.add(LastHttpContent.EMPTY_LAST_CONTENT);
        resetNow();
        return;
      } 
      if (this.currentState == State.READ_HEADER) {
        out.add(invalidMessage(Unpooled.EMPTY_BUFFER, (Exception)new PrematureChannelClosureException("Connection closed before received headers")));
        resetNow();
        return;
      } 
      if (isDecodingRequest() || chunked) {
        prematureClosure = true;
      } else {
        prematureClosure = (contentLength() > 0L);
      } 
      if (!prematureClosure)
        out.add(LastHttpContent.EMPTY_LAST_CONTENT); 
      resetNow();
    } 
  }
  
  public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
    if (evt instanceof HttpExpectationFailedEvent)
      switch (this.currentState) {
        case READ_CHUNK_SIZE:
        case READ_VARIABLE_LENGTH_CONTENT:
        case READ_FIXED_LENGTH_CONTENT:
          reset();
          break;
      }  
    super.userEventTriggered(ctx, evt);
  }
  
  protected boolean isContentAlwaysEmpty(HttpMessage msg) {
    if (msg instanceof HttpResponse) {
      HttpResponse res = (HttpResponse)msg;
      HttpResponseStatus status = res.status();
      int code = status.code();
      HttpStatusClass statusClass = status.codeClass();
      if (statusClass == HttpStatusClass.INFORMATIONAL)
        return (code != 101 || res.headers().contains((CharSequence)HttpHeaderNames.SEC_WEBSOCKET_ACCEPT) || 
          !res.headers().contains((CharSequence)HttpHeaderNames.UPGRADE, (CharSequence)HttpHeaderValues.WEBSOCKET, true)); 
      switch (code) {
        case 204:
        case 304:
          return true;
      } 
      return false;
    } 
    return false;
  }
  
  protected boolean isSwitchingToNonHttp1Protocol(HttpResponse msg) {
    if (msg.status().code() != HttpResponseStatus.SWITCHING_PROTOCOLS.code())
      return false; 
    String newProtocol = msg.headers().get((CharSequence)HttpHeaderNames.UPGRADE);
    return (newProtocol == null || (
      !newProtocol.contains(HttpVersion.HTTP_1_0.text()) && 
      !newProtocol.contains(HttpVersion.HTTP_1_1.text())));
  }
  
  public void reset() {
    this.resetRequested.lazySet(true);
  }
  
  private void resetNow() {
    HttpMessage message = this.message;
    this.message = null;
    this.name = null;
    this.value = null;
    this.contentLength = Long.MIN_VALUE;
    this.lineParser.reset();
    this.headerParser.reset();
    this.trailer = null;
    if (!isDecodingRequest()) {
      HttpResponse res = (HttpResponse)message;
      if (res != null && isSwitchingToNonHttp1Protocol(res)) {
        this.currentState = State.UPGRADED;
        return;
      } 
    } 
    this.resetRequested.lazySet(false);
    this.currentState = State.SKIP_CONTROL_CHARS;
  }
  
  private HttpMessage invalidMessage(ByteBuf in, Exception cause) {
    this.currentState = State.BAD_MESSAGE;
    in.skipBytes(in.readableBytes());
    if (this.message == null)
      this.message = createInvalidMessage(); 
    this.message.setDecoderResult(DecoderResult.failure(cause));
    HttpMessage ret = this.message;
    this.message = null;
    return ret;
  }
  
  private HttpContent invalidChunk(ByteBuf in, Exception cause) {
    this.currentState = State.BAD_MESSAGE;
    in.skipBytes(in.readableBytes());
    HttpContent chunk = new DefaultLastHttpContent(Unpooled.EMPTY_BUFFER);
    chunk.setDecoderResult(DecoderResult.failure(cause));
    this.message = null;
    this.trailer = null;
    return chunk;
  }
  
  private State readHeaders(ByteBuf buffer) {
    HttpMessage message = this.message;
    HttpHeaders headers = message.headers();
    HeaderParser headerParser = this.headerParser;
    ByteBuf line = headerParser.parse(buffer);
    if (line == null)
      return null; 
    int lineLength = line.readableBytes();
    while (lineLength > 0) {
      byte[] lineContent = line.array();
      int startLine = line.arrayOffset() + line.readerIndex();
      byte firstChar = lineContent[startLine];
      if (this.name != null && (firstChar == 32 || firstChar == 9)) {
        String trimmedLine = langAsciiString(lineContent, startLine, lineLength).trim();
        String valueStr = this.value;
        this.value = valueStr + ' ' + trimmedLine;
      } else {
        if (this.name != null)
          headers.add((CharSequence)this.name, this.value); 
        splitHeader(lineContent, startLine, lineLength);
      } 
      line = headerParser.parse(buffer);
      if (line == null)
        return null; 
      lineLength = line.readableBytes();
    } 
    if (this.name != null)
      headers.add((CharSequence)this.name, this.value); 
    this.name = null;
    this.value = null;
    HttpMessageDecoderResult decoderResult = new HttpMessageDecoderResult(this.lineParser.size, headerParser.size);
    message.setDecoderResult(decoderResult);
    List<String> contentLengthFields = headers.getAll((CharSequence)HttpHeaderNames.CONTENT_LENGTH);
    if (!contentLengthFields.isEmpty()) {
      HttpVersion version = message.protocolVersion();
      boolean isHttp10OrEarlier = (version.majorVersion() < 1 || (version.majorVersion() == 1 && version.minorVersion() == 0));
      this.contentLength = HttpUtil.normalizeAndGetContentLength((List)contentLengthFields, isHttp10OrEarlier, this.allowDuplicateContentLengths);
      if (this.contentLength != -1L) {
        String lengthValue = ((String)contentLengthFields.get(0)).trim();
        if (contentLengthFields.size() > 1 || 
          !lengthValue.equals(Long.toString(this.contentLength)))
          headers.set((CharSequence)HttpHeaderNames.CONTENT_LENGTH, Long.valueOf(this.contentLength)); 
      } 
    } 
    if (isContentAlwaysEmpty(message)) {
      HttpUtil.setTransferEncodingChunked(message, false);
      return State.SKIP_CONTROL_CHARS;
    } 
    if (HttpUtil.isTransferEncodingChunked(message)) {
      if (!contentLengthFields.isEmpty() && message.protocolVersion() == HttpVersion.HTTP_1_1)
        handleTransferEncodingChunkedWithContentLength(message); 
      return State.READ_CHUNK_SIZE;
    } 
    if (contentLength() >= 0L)
      return State.READ_FIXED_LENGTH_CONTENT; 
    return State.READ_VARIABLE_LENGTH_CONTENT;
  }
  
  protected void handleTransferEncodingChunkedWithContentLength(HttpMessage message) {
    message.headers().remove((CharSequence)HttpHeaderNames.CONTENT_LENGTH);
    this.contentLength = Long.MIN_VALUE;
  }
  
  private long contentLength() {
    if (this.contentLength == Long.MIN_VALUE)
      this.contentLength = HttpUtil.getContentLength(this.message, -1L); 
    return this.contentLength;
  }
  
  private LastHttpContent readTrailingHeaders(ByteBuf buffer) {
    HeaderParser headerParser = this.headerParser;
    ByteBuf line = headerParser.parse(buffer);
    if (line == null)
      return null; 
    LastHttpContent trailer = this.trailer;
    int lineLength = line.readableBytes();
    if (lineLength == 0 && trailer == null)
      return LastHttpContent.EMPTY_LAST_CONTENT; 
    CharSequence lastHeader = null;
    if (trailer == null)
      trailer = this.trailer = new DefaultLastHttpContent(Unpooled.EMPTY_BUFFER, this.validateHeaders); 
    while (lineLength > 0) {
      byte[] lineContent = line.array();
      int startLine = line.arrayOffset() + line.readerIndex();
      byte firstChar = lineContent[startLine];
      if (lastHeader != null && (firstChar == 32 || firstChar == 9)) {
        List<String> current = trailer.trailingHeaders().getAll(lastHeader);
        if (!current.isEmpty()) {
          int lastPos = current.size() - 1;
          String lineTrimmed = langAsciiString(lineContent, startLine, line.readableBytes()).trim();
          String currentLastPos = current.get(lastPos);
          current.set(lastPos, currentLastPos + lineTrimmed);
        } 
      } else {
        splitHeader(lineContent, startLine, lineLength);
        AsciiString headerName = this.name;
        if (!HttpHeaderNames.CONTENT_LENGTH.contentEqualsIgnoreCase((CharSequence)headerName) && 
          !HttpHeaderNames.TRANSFER_ENCODING.contentEqualsIgnoreCase((CharSequence)headerName) && 
          !HttpHeaderNames.TRAILER.contentEqualsIgnoreCase((CharSequence)headerName))
          trailer.trailingHeaders().add((CharSequence)headerName, this.value); 
        AsciiString asciiString1 = this.name;
        this.name = null;
        this.value = null;
      } 
      line = headerParser.parse(buffer);
      if (line == null)
        return null; 
      lineLength = line.readableBytes();
    } 
    this.trailer = null;
    return trailer;
  }
  
  private static int skipWhiteSpaces(byte[] hex, int start, int length) {
    for (int i = 0; i < length; i++) {
      if (!isWhitespace(hex[start + i]))
        return i; 
    } 
    return length;
  }
  
  private static int getChunkSize(byte[] hex, int start, int length) {
    int skipped = skipWhiteSpaces(hex, start, length);
    if (skipped == length)
      throw new NumberFormatException(); 
    start += skipped;
    length -= skipped;
    int result = 0;
    for (int i = 0; i < length; i++) {
      int digit = StringUtil.decodeHexNibble(hex[start + i]);
      if (digit == -1) {
        byte b = hex[start + i];
        if (b == 59 || isControlOrWhitespaceAsciiChar(b)) {
          if (i == 0)
            throw new NumberFormatException(); 
          return result;
        } 
        throw new NumberFormatException();
      } 
      result *= 16;
      result += digit;
    } 
    return result;
  }
  
  private String[] splitInitialLine(ByteBuf asciiBuffer) {
    byte[] asciiBytes = asciiBuffer.array();
    int arrayOffset = asciiBuffer.arrayOffset();
    int startContent = arrayOffset + asciiBuffer.readerIndex();
    int end = startContent + asciiBuffer.readableBytes();
    int aStart = findNonSPLenient(asciiBytes, startContent, end);
    int aEnd = findSPLenient(asciiBytes, aStart, end);
    int bStart = findNonSPLenient(asciiBytes, aEnd, end);
    int bEnd = findSPLenient(asciiBytes, bStart, end);
    int cStart = findNonSPLenient(asciiBytes, bEnd, end);
    int cEnd = findEndOfString(asciiBytes, Math.max(cStart - 1, startContent), end);
    return new String[] { splitFirstWordInitialLine(asciiBytes, aStart, aEnd - aStart), 
        splitSecondWordInitialLine(asciiBytes, bStart, bEnd - bStart), (cStart < cEnd) ? 
        splitThirdWordInitialLine(asciiBytes, cStart, cEnd - cStart) : "" };
  }
  
  protected String splitFirstWordInitialLine(byte[] asciiContent, int start, int length) {
    return langAsciiString(asciiContent, start, length);
  }
  
  protected String splitSecondWordInitialLine(byte[] asciiContent, int start, int length) {
    return langAsciiString(asciiContent, start, length);
  }
  
  protected String splitThirdWordInitialLine(byte[] asciiContent, int start, int length) {
    return langAsciiString(asciiContent, start, length);
  }
  
  private static String langAsciiString(byte[] asciiContent, int start, int length) {
    if (length == 0)
      return ""; 
    if (start == 0) {
      if (length == asciiContent.length)
        return new String(asciiContent, 0, 0, asciiContent.length); 
      return new String(asciiContent, 0, 0, length);
    } 
    return new String(asciiContent, 0, start, length);
  }
  
  private void splitHeader(byte[] line, int start, int length) {
    int end = start + length;
    int nameStart = findNonWhitespace(line, start, end);
    boolean isDecodingRequest = isDecodingRequest();
    int nameEnd;
    for (nameEnd = nameStart; nameEnd < end; nameEnd++) {
      byte ch = line[nameEnd];
      if (ch == 58 || (!isDecodingRequest && 
        
        isOWS(ch)))
        break; 
    } 
    if (nameEnd == end)
      throw new IllegalArgumentException("No colon found"); 
    int colonEnd;
    for (colonEnd = nameEnd; colonEnd < end; colonEnd++) {
      if (line[colonEnd] == 58) {
        colonEnd++;
        break;
      } 
    } 
    this.name = splitHeaderName(line, nameStart, nameEnd - nameStart);
    int valueStart = findNonWhitespace(line, colonEnd, end);
    if (valueStart == end) {
      this.value = "";
    } else {
      int valueEnd = findEndOfString(line, start, end);
      this.value = langAsciiString(line, valueStart, valueEnd - valueStart);
    } 
  }
  
  protected AsciiString splitHeaderName(byte[] sb, int start, int length) {
    return new AsciiString(sb, start, length, true);
  }
  
  private static int findNonSPLenient(byte[] sb, int offset, int end) {
    for (int result = offset; result < end; ) {
      byte c = sb[result];
      if (isSPLenient(c)) {
        result++;
        continue;
      } 
      if (isWhitespace(c))
        throw new IllegalArgumentException("Invalid separator"); 
      return result;
    } 
    return end;
  }
  
  private static int findSPLenient(byte[] sb, int offset, int end) {
    for (int result = offset; result < end; result++) {
      if (isSPLenient(sb[result]))
        return result; 
    } 
    return end;
  }
  
  private static final boolean[] SP_LENIENT_BYTES = new boolean[256];
  
  static {
    SP_LENIENT_BYTES[160] = true;
    SP_LENIENT_BYTES[137] = true;
    SP_LENIENT_BYTES[139] = true;
    SP_LENIENT_BYTES[140] = true;
    SP_LENIENT_BYTES[141] = true;
  }
  
  private static final boolean[] LATIN_WHITESPACE = new boolean[256];
  
  static {
    byte b;
    for (b = Byte.MIN_VALUE; b < Byte.MAX_VALUE; b = (byte)(b + 1))
      LATIN_WHITESPACE[128 + b] = Character.isWhitespace(b); 
  }
  
  private static boolean isSPLenient(byte c) {
    return SP_LENIENT_BYTES[c + 128];
  }
  
  private static boolean isWhitespace(byte b) {
    return LATIN_WHITESPACE[b + 128];
  }
  
  private static int findNonWhitespace(byte[] sb, int offset, int end) {
    for (int result = offset; result < end; result++) {
      byte c = sb[result];
      if (!isWhitespace(c))
        return result; 
      if (!isOWS(c))
        throw new IllegalArgumentException("Invalid separator, only a single space or horizontal tab allowed, but received a '" + c + "' (0x" + 
            Integer.toHexString(c) + ")"); 
    } 
    return end;
  }
  
  private static int findEndOfString(byte[] sb, int start, int end) {
    for (int result = end - 1; result > start; result--) {
      if (!isWhitespace(sb[result]))
        return result + 1; 
    } 
    return 0;
  }
  
  private static boolean isOWS(byte ch) {
    return (ch == 32 || ch == 9);
  }
  
  private static class HeaderParser {
    protected final ByteBuf seq;
    
    protected final int maxLength;
    
    int size;
    
    HeaderParser(ByteBuf seq, int maxLength) {
      this.seq = seq;
      this.maxLength = maxLength;
    }
    
    public ByteBuf parse(ByteBuf buffer) {
      int endOfSeqIncluded, readableBytes = buffer.readableBytes();
      int readerIndex = buffer.readerIndex();
      int maxBodySize = this.maxLength - this.size;
      assert maxBodySize >= 0;
      long maxBodySizeWithCRLF = maxBodySize + 2L;
      int toProcess = (int)Math.min(maxBodySizeWithCRLF, readableBytes);
      int toIndexExclusive = readerIndex + toProcess;
      assert toIndexExclusive >= readerIndex;
      int indexOfLf = buffer.indexOf(readerIndex, toIndexExclusive, (byte)10);
      if (indexOfLf == -1) {
        if (readableBytes > maxBodySize)
          throw newException(this.maxLength); 
        return null;
      } 
      if (indexOfLf > readerIndex && buffer.getByte(indexOfLf - 1) == 13) {
        endOfSeqIncluded = indexOfLf - 1;
      } else {
        endOfSeqIncluded = indexOfLf;
      } 
      int newSize = endOfSeqIncluded - readerIndex;
      if (newSize == 0) {
        this.seq.clear();
        buffer.readerIndex(indexOfLf + 1);
        return this.seq;
      } 
      int size = this.size + newSize;
      if (size > this.maxLength)
        throw newException(this.maxLength); 
      this.size = size;
      this.seq.clear();
      this.seq.writeBytes(buffer, readerIndex, newSize);
      buffer.readerIndex(indexOfLf + 1);
      return this.seq;
    }
    
    public void reset() {
      this.size = 0;
    }
    
    protected TooLongFrameException newException(int maxLength) {
      return new TooLongHttpHeaderException("HTTP header is larger than " + maxLength + " bytes.");
    }
  }
  
  private final class LineParser extends HeaderParser {
    LineParser(ByteBuf seq, int maxLength) {
      super(seq, maxLength);
    }
    
    public ByteBuf parse(ByteBuf buffer) {
      reset();
      int readableBytes = buffer.readableBytes();
      if (readableBytes == 0)
        return null; 
      int readerIndex = buffer.readerIndex();
      if (HttpObjectDecoder.this.currentState == HttpObjectDecoder.State.SKIP_CONTROL_CHARS && skipControlChars(buffer, readableBytes, readerIndex))
        return null; 
      return super.parse(buffer);
    }
    
    private boolean skipControlChars(ByteBuf buffer, int readableBytes, int readerIndex) {
      assert HttpObjectDecoder.this.currentState == HttpObjectDecoder.State.SKIP_CONTROL_CHARS;
      int maxToSkip = Math.min(this.maxLength, readableBytes);
      int firstNonControlIndex = buffer.forEachByte(readerIndex, maxToSkip, HttpObjectDecoder.SKIP_CONTROL_CHARS_BYTES);
      if (firstNonControlIndex == -1) {
        buffer.skipBytes(maxToSkip);
        if (readableBytes > this.maxLength)
          throw newException(this.maxLength); 
        return true;
      } 
      buffer.readerIndex(firstNonControlIndex);
      HttpObjectDecoder.this.currentState = HttpObjectDecoder.State.READ_INITIAL;
      return false;
    }
    
    protected TooLongFrameException newException(int maxLength) {
      return new TooLongHttpLineException("An HTTP line is larger than " + maxLength + " bytes.");
    }
  }
  
  private static final boolean[] ISO_CONTROL_OR_WHITESPACE = new boolean[256];
  
  static {
    for (b = Byte.MIN_VALUE; b < Byte.MAX_VALUE; b = (byte)(b + 1))
      ISO_CONTROL_OR_WHITESPACE[128 + b] = (Character.isISOControl(b) || isWhitespace(b)); 
  }
  
  private static final ByteProcessor SKIP_CONTROL_CHARS_BYTES = new ByteProcessor() {
      public boolean process(byte value) {
        return HttpObjectDecoder.ISO_CONTROL_OR_WHITESPACE[128 + value];
      }
    };
  
  private static boolean isControlOrWhitespaceAsciiChar(byte b) {
    return ISO_CONTROL_OR_WHITESPACE[128 + b];
  }
  
  protected abstract boolean isDecodingRequest();
  
  protected abstract HttpMessage createMessage(String[] paramArrayOfString) throws Exception;
  
  protected abstract HttpMessage createInvalidMessage();
}
