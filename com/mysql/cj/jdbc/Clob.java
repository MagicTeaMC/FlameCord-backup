package com.mysql.cj.jdbc;

import com.mysql.cj.Messages;
import com.mysql.cj.exceptions.CJException;
import com.mysql.cj.exceptions.ExceptionInterceptor;
import com.mysql.cj.jdbc.exceptions.SQLError;
import com.mysql.cj.jdbc.exceptions.SQLExceptionsMapping;
import com.mysql.cj.protocol.OutputStreamWatcher;
import com.mysql.cj.protocol.WatchableOutputStream;
import com.mysql.cj.protocol.WatchableStream;
import com.mysql.cj.protocol.WatchableWriter;
import com.mysql.cj.protocol.WriterWatcher;
import com.mysql.cj.util.StringUtils;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.StringReader;
import java.io.Writer;
import java.sql.Clob;
import java.sql.SQLException;

public class Clob implements Clob, OutputStreamWatcher, WriterWatcher {
  private String charData;
  
  private ExceptionInterceptor exceptionInterceptor;
  
  Clob(ExceptionInterceptor exceptionInterceptor) {
    this.charData = "";
    this.exceptionInterceptor = exceptionInterceptor;
  }
  
  public Clob(String charDataInit, ExceptionInterceptor exceptionInterceptor) {
    this.charData = charDataInit;
    this.exceptionInterceptor = exceptionInterceptor;
  }
  
  public InputStream getAsciiStream() throws SQLException {
    try {
      if (this.charData != null)
        return new ByteArrayInputStream(StringUtils.getBytes(this.charData)); 
      return null;
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, this.exceptionInterceptor);
    } 
  }
  
  public Reader getCharacterStream() throws SQLException {
    try {
      if (this.charData != null)
        return new StringReader(this.charData); 
      return null;
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, this.exceptionInterceptor);
    } 
  }
  
  public String getSubString(long startPos, int length) throws SQLException {
    try {
      if (startPos < 1L)
        throw SQLError.createSQLException(Messages.getString("Clob.6"), "S1009", this.exceptionInterceptor); 
      int adjustedStartPos = (int)startPos - 1;
      int adjustedEndIndex = adjustedStartPos + length;
      if (this.charData != null) {
        if (adjustedEndIndex > this.charData.length())
          throw SQLError.createSQLException(Messages.getString("Clob.7"), "S1009", this.exceptionInterceptor); 
        return this.charData.substring(adjustedStartPos, adjustedEndIndex);
      } 
      return null;
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, this.exceptionInterceptor);
    } 
  }
  
  public long length() throws SQLException {
    try {
      if (this.charData != null)
        return this.charData.length(); 
      return 0L;
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, this.exceptionInterceptor);
    } 
  }
  
  public long position(Clob arg0, long arg1) throws SQLException {
    try {
      return position(arg0.getSubString(1L, (int)arg0.length()), arg1);
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, this.exceptionInterceptor);
    } 
  }
  
  public long position(String stringToFind, long startPos) throws SQLException {
    try {
      if (startPos < 1L)
        throw SQLError.createSQLException(Messages.getString("Clob.8", new Object[] { Long.valueOf(startPos) }), "S1009", this.exceptionInterceptor); 
      if (this.charData != null) {
        if (startPos - 1L > this.charData.length())
          throw SQLError.createSQLException(Messages.getString("Clob.10"), "S1009", this.exceptionInterceptor); 
        int pos = this.charData.indexOf(stringToFind, (int)(startPos - 1L));
        return (pos == -1) ? -1L : (pos + 1);
      } 
      return -1L;
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, this.exceptionInterceptor);
    } 
  }
  
  public OutputStream setAsciiStream(long indexToWriteAt) throws SQLException {
    try {
      if (indexToWriteAt < 1L)
        throw SQLError.createSQLException(Messages.getString("Clob.0"), "S1009", this.exceptionInterceptor); 
      WatchableOutputStream bytesOut = new WatchableOutputStream();
      bytesOut.setWatcher(this);
      if (indexToWriteAt > 0L)
        bytesOut.write(StringUtils.getBytes(this.charData), 0, (int)(indexToWriteAt - 1L)); 
      return (OutputStream)bytesOut;
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, this.exceptionInterceptor);
    } 
  }
  
  public Writer setCharacterStream(long indexToWriteAt) throws SQLException {
    try {
      if (indexToWriteAt < 1L)
        throw SQLError.createSQLException(Messages.getString("Clob.1"), "S1009", this.exceptionInterceptor); 
      WatchableWriter writer = new WatchableWriter();
      writer.setWatcher(this);
      if (indexToWriteAt > 1L)
        writer.write(this.charData, 0, (int)(indexToWriteAt - 1L)); 
      return (Writer)writer;
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, this.exceptionInterceptor);
    } 
  }
  
  public int setString(long pos, String str) throws SQLException {
    try {
      if (pos < 1L)
        throw SQLError.createSQLException(Messages.getString("Clob.2"), "S1009", this.exceptionInterceptor); 
      if (str == null)
        throw SQLError.createSQLException(Messages.getString("Clob.3"), "S1009", this.exceptionInterceptor); 
      StringBuilder charBuf = new StringBuilder(this.charData);
      pos--;
      int strLength = str.length();
      charBuf.replace((int)pos, (int)(pos + strLength), str);
      this.charData = charBuf.toString();
      return strLength;
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, this.exceptionInterceptor);
    } 
  }
  
  public int setString(long pos, String str, int offset, int len) throws SQLException {
    try {
      if (pos < 1L)
        throw SQLError.createSQLException(Messages.getString("Clob.4"), "S1009", this.exceptionInterceptor); 
      if (str == null)
        throw SQLError.createSQLException(Messages.getString("Clob.5"), "S1009", this.exceptionInterceptor); 
      StringBuilder charBuf = new StringBuilder(this.charData);
      pos--;
      try {
        String replaceString = str.substring(offset, offset + len);
        charBuf.replace((int)pos, (int)(pos + replaceString.length()), replaceString);
      } catch (StringIndexOutOfBoundsException e) {
        throw SQLError.createSQLException(e.getMessage(), "S1009", e, this.exceptionInterceptor);
      } 
      this.charData = charBuf.toString();
      return len;
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, this.exceptionInterceptor);
    } 
  }
  
  public void streamClosed(WatchableStream out) {
    int streamSize = out.size();
    if (streamSize < this.charData.length())
      out.write(StringUtils.getBytes(this.charData), streamSize, this.charData.length() - streamSize); 
    this.charData = StringUtils.toAsciiString(out.toByteArray());
  }
  
  public void truncate(long length) throws SQLException {
    try {
      if (length > this.charData.length())
        throw SQLError.createSQLException(
            Messages.getString("Clob.11") + this.charData.length() + Messages.getString("Clob.12") + length + Messages.getString("Clob.13"), this.exceptionInterceptor); 
      this.charData = this.charData.substring(0, (int)length);
      return;
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, this.exceptionInterceptor);
    } 
  }
  
  public void writerClosed(char[] charDataBeingWritten) {
    this.charData = new String(charDataBeingWritten);
  }
  
  public void writerClosed(WatchableWriter out) {
    int dataLength = out.size();
    if (dataLength < this.charData.length())
      out.write(this.charData, dataLength, this.charData.length() - dataLength); 
    this.charData = out.toString();
  }
  
  public void free() throws SQLException {
    try {
      this.charData = null;
      return;
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, this.exceptionInterceptor);
    } 
  }
  
  public Reader getCharacterStream(long pos, long length) throws SQLException {
    try {
      return new StringReader(getSubString(pos, (int)length));
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, this.exceptionInterceptor);
    } 
  }
}
