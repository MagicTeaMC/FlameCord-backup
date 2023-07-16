package org.apache.http.message;

import org.apache.http.Header;
import org.apache.http.ParseException;
import org.apache.http.ProtocolVersion;
import org.apache.http.RequestLine;
import org.apache.http.StatusLine;
import org.apache.http.util.CharArrayBuffer;

public interface LineParser {
  ProtocolVersion parseProtocolVersion(CharArrayBuffer paramCharArrayBuffer, ParserCursor paramParserCursor) throws ParseException;
  
  boolean hasProtocolVersion(CharArrayBuffer paramCharArrayBuffer, ParserCursor paramParserCursor);
  
  RequestLine parseRequestLine(CharArrayBuffer paramCharArrayBuffer, ParserCursor paramParserCursor) throws ParseException;
  
  StatusLine parseStatusLine(CharArrayBuffer paramCharArrayBuffer, ParserCursor paramParserCursor) throws ParseException;
  
  Header parseHeader(CharArrayBuffer paramCharArrayBuffer) throws ParseException;
}
