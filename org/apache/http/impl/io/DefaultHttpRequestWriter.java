package org.apache.http.impl.io;

import java.io.IOException;
import org.apache.http.HttpMessage;
import org.apache.http.HttpRequest;
import org.apache.http.io.SessionOutputBuffer;
import org.apache.http.message.LineFormatter;

public class DefaultHttpRequestWriter extends AbstractMessageWriter<HttpRequest> {
  public DefaultHttpRequestWriter(SessionOutputBuffer buffer, LineFormatter formatter) {
    super(buffer, formatter);
  }
  
  public DefaultHttpRequestWriter(SessionOutputBuffer buffer) {
    this(buffer, (LineFormatter)null);
  }
  
  protected void writeHeadLine(HttpRequest message) throws IOException {
    this.lineFormatter.formatRequestLine(this.lineBuf, message.getRequestLine());
    this.sessionBuffer.writeLine(this.lineBuf);
  }
}
