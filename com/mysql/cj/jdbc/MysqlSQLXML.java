package com.mysql.cj.jdbc;

import com.mysql.cj.Messages;
import com.mysql.cj.exceptions.CJException;
import com.mysql.cj.exceptions.ExceptionInterceptor;
import com.mysql.cj.jdbc.exceptions.SQLError;
import com.mysql.cj.jdbc.exceptions.SQLExceptionsMapping;
import com.mysql.cj.jdbc.result.ResultSetInternalMethods;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.sql.SQLException;
import java.sql.SQLXML;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.transform.Result;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.sax.SAXResult;
import javax.xml.transform.sax.SAXSource;
import javax.xml.transform.stax.StAXResult;
import javax.xml.transform.stax.StAXSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.helpers.XMLReaderFactory;

public class MysqlSQLXML implements SQLXML {
  private XMLInputFactory inputFactory;
  
  private XMLOutputFactory outputFactory;
  
  private String stringRep;
  
  private ResultSetInternalMethods owningResultSet;
  
  private int columnIndexOfXml;
  
  private boolean fromResultSet;
  
  private boolean isClosed = false;
  
  private boolean workingWithResult;
  
  private DOMResult asDOMResult;
  
  private SAXResult asSAXResult;
  
  private SimpleSaxToReader saxToReaderConverter;
  
  private StringWriter asStringWriter;
  
  private ByteArrayOutputStream asByteArrayOutputStream;
  
  private ExceptionInterceptor exceptionInterceptor;
  
  public MysqlSQLXML(ResultSetInternalMethods owner, int index, ExceptionInterceptor exceptionInterceptor) {
    this.owningResultSet = owner;
    this.columnIndexOfXml = index;
    this.fromResultSet = true;
    this.exceptionInterceptor = exceptionInterceptor;
  }
  
  public MysqlSQLXML(ExceptionInterceptor exceptionInterceptor) {
    this.fromResultSet = false;
    this.exceptionInterceptor = exceptionInterceptor;
  }
  
  public synchronized void free() throws SQLException {
    try {
      this.stringRep = null;
      this.asDOMResult = null;
      this.asSAXResult = null;
      this.inputFactory = null;
      this.outputFactory = null;
      this.owningResultSet = null;
      this.workingWithResult = false;
      this.isClosed = true;
      return;
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, this.exceptionInterceptor);
    } 
  }
  
  public synchronized String getString() throws SQLException {
    try {
      checkClosed();
      checkWorkingWithResult();
      if (this.fromResultSet)
        return this.owningResultSet.getString(this.columnIndexOfXml); 
      return this.stringRep;
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, this.exceptionInterceptor);
    } 
  }
  
  private synchronized void checkClosed() throws SQLException {
    if (this.isClosed)
      throw SQLError.createSQLException(Messages.getString("MysqlSQLXML.0"), this.exceptionInterceptor); 
  }
  
  private synchronized void checkWorkingWithResult() throws SQLException {
    if (this.workingWithResult)
      throw SQLError.createSQLException(Messages.getString("MysqlSQLXML.1"), "S1009", this.exceptionInterceptor); 
  }
  
  public synchronized void setString(String str) throws SQLException {
    try {
      checkClosed();
      checkWorkingWithResult();
      this.stringRep = str;
      this.fromResultSet = false;
      return;
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, this.exceptionInterceptor);
    } 
  }
  
  public synchronized boolean isEmpty() throws SQLException {
    checkClosed();
    checkWorkingWithResult();
    if (!this.fromResultSet)
      return (this.stringRep == null || this.stringRep.length() == 0); 
    return false;
  }
  
  public synchronized InputStream getBinaryStream() throws SQLException {
    try {
      checkClosed();
      checkWorkingWithResult();
      return this.owningResultSet.getBinaryStream(this.columnIndexOfXml);
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, this.exceptionInterceptor);
    } 
  }
  
  public synchronized Reader getCharacterStream() throws SQLException {
    try {
      checkClosed();
      checkWorkingWithResult();
      return this.owningResultSet.getCharacterStream(this.columnIndexOfXml);
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, this.exceptionInterceptor);
    } 
  }
  
  public <T extends javax.xml.transform.Source> T getSource(Class<T> clazz) throws SQLException {
    try {
      checkClosed();
      checkWorkingWithResult();
      if (clazz == null || clazz.equals(SAXSource.class))
        try {
          XMLReader reader = XMLReaderFactory.createXMLReader();
          reader.setFeature("http://javax.xml.XMLConstants/feature/secure-processing", true);
          setFeature(reader, "http://apache.org/xml/features/disallow-doctype-decl", true);
          setFeature(reader, "http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
          setFeature(reader, "http://xml.org/sax/features/external-general-entities", false);
          setFeature(reader, "http://xml.org/sax/features/external-parameter-entities", false);
          return (T)new SAXSource(reader, this.fromResultSet ? new InputSource(this.owningResultSet.getCharacterStream(this.columnIndexOfXml)) : new InputSource(new StringReader(this.stringRep)));
        } catch (SAXException ex) {
          SQLException sqlEx = SQLError.createSQLException(ex.getMessage(), "S1009", ex, this.exceptionInterceptor);
          throw sqlEx;
        }  
      if (clazz.equals(DOMSource.class))
        try {
          DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
          builderFactory.setNamespaceAware(true);
          setFeature(builderFactory, "http://javax.xml.XMLConstants/feature/secure-processing", true);
          setFeature(builderFactory, "http://apache.org/xml/features/disallow-doctype-decl", true);
          setFeature(builderFactory, "http://xml.org/sax/features/external-general-entities", false);
          setFeature(builderFactory, "http://xml.org/sax/features/external-parameter-entities", false);
          setFeature(builderFactory, "http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
          builderFactory.setXIncludeAware(false);
          builderFactory.setExpandEntityReferences(false);
          builderFactory.setAttribute("http://javax.xml.XMLConstants/property/accessExternalSchema", "");
          DocumentBuilder builder = builderFactory.newDocumentBuilder();
          return (T)new DOMSource(builder.parse(this.fromResultSet ? new InputSource(this.owningResultSet.getCharacterStream(this.columnIndexOfXml)) : new InputSource(new StringReader(this.stringRep))));
        } catch (Throwable t) {
          SQLException sqlEx = SQLError.createSQLException(t.getMessage(), "S1009", t, this.exceptionInterceptor);
          throw sqlEx;
        }  
      if (clazz.equals(StreamSource.class))
        return (T)new StreamSource(this.fromResultSet ? this.owningResultSet.getCharacterStream(this.columnIndexOfXml) : new StringReader(this.stringRep)); 
      if (clazz.equals(StAXSource.class))
        try {
          return (T)new StAXSource(this.inputFactory.createXMLStreamReader(this.fromResultSet ? this.owningResultSet
                .getCharacterStream(this.columnIndexOfXml) : new StringReader(this.stringRep)));
        } catch (XMLStreamException ex) {
          SQLException sqlEx = SQLError.createSQLException(ex.getMessage(), "S1009", ex, this.exceptionInterceptor);
          throw sqlEx;
        }  
      throw SQLError.createSQLException(Messages.getString("MysqlSQLXML.2", new Object[] { clazz.toString() }), "S1009", this.exceptionInterceptor);
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, this.exceptionInterceptor);
    } 
  }
  
  private static void setFeature(Object factory, String name, boolean value) {
    try {
      if (factory instanceof DocumentBuilderFactory) {
        ((DocumentBuilderFactory)factory).setFeature(name, value);
      } else if (factory instanceof XMLReader) {
        ((XMLReader)factory).setFeature(name, value);
      } 
    } catch (Exception exception) {}
  }
  
  public synchronized OutputStream setBinaryStream() throws SQLException {
    try {
      checkClosed();
      checkWorkingWithResult();
      this.workingWithResult = true;
      return setBinaryStreamInternal();
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, this.exceptionInterceptor);
    } 
  }
  
  private synchronized OutputStream setBinaryStreamInternal() throws SQLException {
    this.asByteArrayOutputStream = new ByteArrayOutputStream();
    return this.asByteArrayOutputStream;
  }
  
  public synchronized Writer setCharacterStream() throws SQLException {
    try {
      checkClosed();
      checkWorkingWithResult();
      this.workingWithResult = true;
      return setCharacterStreamInternal();
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, this.exceptionInterceptor);
    } 
  }
  
  private synchronized Writer setCharacterStreamInternal() throws SQLException {
    this.asStringWriter = new StringWriter();
    return this.asStringWriter;
  }
  
  public synchronized <T extends Result> T setResult(Class<T> clazz) throws SQLException {
    try {
      checkClosed();
      checkWorkingWithResult();
      this.workingWithResult = true;
      this.asDOMResult = null;
      this.asSAXResult = null;
      this.saxToReaderConverter = null;
      this.stringRep = null;
      this.asStringWriter = null;
      this.asByteArrayOutputStream = null;
      if (clazz == null || clazz.equals(SAXResult.class)) {
        this.saxToReaderConverter = new SimpleSaxToReader();
        this.asSAXResult = new SAXResult(this.saxToReaderConverter);
        return (T)this.asSAXResult;
      } 
      if (clazz.equals(DOMResult.class)) {
        this.asDOMResult = new DOMResult();
        return (T)this.asDOMResult;
      } 
      if (clazz.equals(StreamResult.class))
        return (T)new StreamResult(setCharacterStreamInternal()); 
      if (clazz.equals(StAXResult.class))
        try {
          if (this.outputFactory == null)
            this.outputFactory = XMLOutputFactory.newInstance(); 
          return (T)new StAXResult(this.outputFactory.createXMLEventWriter(setCharacterStreamInternal()));
        } catch (XMLStreamException ex) {
          SQLException sqlEx = SQLError.createSQLException(ex.getMessage(), "S1009", ex, this.exceptionInterceptor);
          throw sqlEx;
        }  
      throw SQLError.createSQLException(Messages.getString("MysqlSQLXML.3", new Object[] { clazz.toString() }), "S1009", this.exceptionInterceptor);
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, this.exceptionInterceptor);
    } 
  }
  
  private Reader binaryInputStreamStreamToReader(ByteArrayOutputStream out) {
    try {
      String encoding = "UTF-8";
      try {
        ByteArrayInputStream bIn = new ByteArrayInputStream(out.toByteArray());
        XMLStreamReader reader = this.inputFactory.createXMLStreamReader(bIn);
        int eventType = 0;
        while ((eventType = reader.next()) != 8) {
          if (eventType == 7) {
            String possibleEncoding = reader.getEncoding();
            if (possibleEncoding != null)
              encoding = possibleEncoding; 
            break;
          } 
        } 
      } catch (Throwable throwable) {}
      return new StringReader(new String(out.toByteArray(), encoding));
    } catch (UnsupportedEncodingException badEnc) {
      throw new RuntimeException(badEnc);
    } 
  }
  
  protected String readerToString(Reader reader) throws SQLException {
    StringBuilder buf = new StringBuilder();
    int charsRead = 0;
    char[] charBuf = new char[512];
    try {
      while ((charsRead = reader.read(charBuf)) != -1)
        buf.append(charBuf, 0, charsRead); 
    } catch (IOException ioEx) {
      SQLException sqlEx = SQLError.createSQLException(ioEx.getMessage(), "S1009", ioEx, this.exceptionInterceptor);
      throw sqlEx;
    } 
    return buf.toString();
  }
  
  protected synchronized Reader serializeAsCharacterStream() throws SQLException {
    checkClosed();
    if (this.workingWithResult || this.owningResultSet == null) {
      if (this.stringRep != null)
        return new StringReader(this.stringRep); 
      if (this.asDOMResult != null)
        return new StringReader(domSourceToString()); 
      if (this.asStringWriter != null)
        return new StringReader(this.asStringWriter.toString()); 
      if (this.asSAXResult != null)
        return this.saxToReaderConverter.toReader(); 
      if (this.asByteArrayOutputStream != null)
        return binaryInputStreamStreamToReader(this.asByteArrayOutputStream); 
    } 
    return this.owningResultSet.getCharacterStream(this.columnIndexOfXml);
  }
  
  protected String domSourceToString() throws SQLException {
    try {
      DOMSource source = new DOMSource(this.asDOMResult.getNode());
      Transformer identity = TransformerFactory.newInstance().newTransformer();
      StringWriter stringOut = new StringWriter();
      Result result = new StreamResult(stringOut);
      identity.transform(source, result);
      return stringOut.toString();
    } catch (Throwable t) {
      SQLException sqlEx = SQLError.createSQLException(t.getMessage(), "S1009", t, this.exceptionInterceptor);
      throw sqlEx;
    } 
  }
  
  protected synchronized String serializeAsString() throws SQLException {
    checkClosed();
    if (this.workingWithResult) {
      if (this.stringRep != null)
        return this.stringRep; 
      if (this.asDOMResult != null)
        return domSourceToString(); 
      if (this.asStringWriter != null)
        return this.asStringWriter.toString(); 
      if (this.asSAXResult != null)
        return readerToString(this.saxToReaderConverter.toReader()); 
      if (this.asByteArrayOutputStream != null)
        return readerToString(binaryInputStreamStreamToReader(this.asByteArrayOutputStream)); 
    } 
    return this.owningResultSet.getString(this.columnIndexOfXml);
  }
  
  class SimpleSaxToReader extends DefaultHandler {
    StringBuilder buf = new StringBuilder();
    
    public void startDocument() throws SAXException {
      this.buf.append("<?xml version='1.0' encoding='UTF-8'?>");
    }
    
    public void endDocument() throws SAXException {}
    
    public void startElement(String namespaceURI, String sName, String qName, Attributes attrs) throws SAXException {
      this.buf.append("<");
      this.buf.append(qName);
      if (attrs != null)
        for (int i = 0; i < attrs.getLength(); i++) {
          this.buf.append(" ");
          this.buf.append(attrs.getQName(i)).append("=\"");
          escapeCharsForXml(attrs.getValue(i), true);
          this.buf.append("\"");
        }  
      this.buf.append(">");
    }
    
    public void characters(char[] buffer, int offset, int len) throws SAXException {
      if (!this.inCDATA) {
        escapeCharsForXml(buffer, offset, len, false);
      } else {
        this.buf.append(buffer, offset, len);
      } 
    }
    
    public void ignorableWhitespace(char[] ch, int start, int length) throws SAXException {
      characters(ch, start, length);
    }
    
    private boolean inCDATA = false;
    
    public void startCDATA() throws SAXException {
      this.buf.append("<![CDATA[");
      this.inCDATA = true;
    }
    
    public void endCDATA() throws SAXException {
      this.inCDATA = false;
      this.buf.append("]]>");
    }
    
    public void comment(char[] ch, int start, int length) throws SAXException {
      this.buf.append("<!--");
      for (int i = 0; i < length; i++)
        this.buf.append(ch[start + i]); 
      this.buf.append("-->");
    }
    
    Reader toReader() {
      return new StringReader(this.buf.toString());
    }
    
    private void escapeCharsForXml(String str, boolean isAttributeData) {
      if (str == null)
        return; 
      int strLen = str.length();
      for (int i = 0; i < strLen; i++)
        escapeCharsForXml(str.charAt(i), isAttributeData); 
    }
    
    private void escapeCharsForXml(char[] buffer, int offset, int len, boolean isAttributeData) {
      if (buffer == null)
        return; 
      for (int i = 0; i < len; i++)
        escapeCharsForXml(buffer[offset + i], isAttributeData); 
    }
    
    private void escapeCharsForXml(char c, boolean isAttributeData) {
      switch (c) {
        case '<':
          this.buf.append("&lt;");
          return;
        case '>':
          this.buf.append("&gt;");
          return;
        case '&':
          this.buf.append("&amp;");
          return;
        case '"':
          if (!isAttributeData) {
            this.buf.append("\"");
          } else {
            this.buf.append("&quot;");
          } 
          return;
        case '\r':
          this.buf.append("&#xD;");
          return;
      } 
      if ((c >= '\001' && c <= '\037' && c != '\t' && c != '\n') || (c >= '' && c <= '') || c == ' ' || (isAttributeData && (c == '\t' || c == '\n'))) {
        this.buf.append("&#x");
        this.buf.append(Integer.toHexString(c).toUpperCase());
        this.buf.append(";");
      } else {
        this.buf.append(c);
      } 
    }
  }
}
