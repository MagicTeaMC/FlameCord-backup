package org.apache.http.impl.conn;

import java.io.IOException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.HttpException;
import org.apache.http.HttpMessage;
import org.apache.http.HttpResponseFactory;
import org.apache.http.NoHttpResponseException;
import org.apache.http.ProtocolException;
import org.apache.http.StatusLine;
import org.apache.http.annotation.Contract;
import org.apache.http.annotation.ThreadingBehavior;
import org.apache.http.impl.io.AbstractMessageParser;
import org.apache.http.io.SessionInputBuffer;
import org.apache.http.message.LineParser;
import org.apache.http.message.ParserCursor;
import org.apache.http.params.HttpParams;
import org.apache.http.util.Args;
import org.apache.http.util.CharArrayBuffer;

@Deprecated
@Contract(threading = ThreadingBehavior.SAFE_CONDITIONAL)
public class DefaultResponseParser extends AbstractMessageParser<HttpMessage> {
  private final Log log = LogFactory.getLog(getClass());
  
  private final HttpResponseFactory responseFactory;
  
  private final CharArrayBuffer lineBuf;
  
  private final int maxGarbageLines;
  
  public DefaultResponseParser(SessionInputBuffer buffer, LineParser parser, HttpResponseFactory responseFactory, HttpParams params) {
    super(buffer, parser, params);
    Args.notNull(responseFactory, "Response factory");
    this.responseFactory = responseFactory;
    this.lineBuf = new CharArrayBuffer(128);
    this.maxGarbageLines = getMaxGarbageLines(params);
  }
  
  protected int getMaxGarbageLines(HttpParams params) {
    return params.getIntParameter("http.connection.max-status-line-garbage", 2147483647);
  }
  
  protected HttpMessage parseHead(SessionInputBuffer sessionBuffer) throws IOException, HttpException {
    int count = 0;
    ParserCursor cursor = null;
    while (true) {
      this.lineBuf.clear();
      int i = sessionBuffer.readLine(this.lineBuf);
      if (i == -1 && count == 0)
        throw new NoHttpResponseException("The target server failed to respond"); 
      cursor = new ParserCursor(0, this.lineBuf.length());
      if (this.lineParser.hasProtocolVersion(this.lineBuf, cursor))
        break; 
      if (i == -1 || count >= this.maxGarbageLines)
        throw new ProtocolException("The server failed to respond with a valid HTTP response"); 
      if (this.log.isDebugEnabled())
        this.log.debug("Garbage in response: " + this.lineBuf.toString()); 
      count++;
    } 
    StatusLine statusline = this.lineParser.parseStatusLine(this.lineBuf, cursor);
    return (HttpMessage)this.responseFactory.newHttpResponse(statusline, null);
  }
}
