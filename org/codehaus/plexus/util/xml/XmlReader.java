package org.codehaus.plexus.util.xml;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.net.URL;
import java.net.URLConnection;
import java.text.MessageFormat;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class XmlReader extends Reader {
  private static final int BUFFER_SIZE = 4096;
  
  private static final String UTF_8 = "UTF-8";
  
  private static final String US_ASCII = "US-ASCII";
  
  private static final String UTF_16BE = "UTF-16BE";
  
  private static final String UTF_16LE = "UTF-16LE";
  
  private static final String UTF_16 = "UTF-16";
  
  private static final String EBCDIC = "CP1047";
  
  private static String _staticDefaultEncoding = null;
  
  private Reader _reader;
  
  private String _encoding;
  
  private String _defaultEncoding;
  
  public static void setDefaultEncoding(String encoding) {
    _staticDefaultEncoding = encoding;
  }
  
  public static String getDefaultEncoding() {
    return _staticDefaultEncoding;
  }
  
  public XmlReader(File file) throws IOException {
    this(new FileInputStream(file));
  }
  
  public XmlReader(InputStream is) throws IOException {
    this(is, true);
  }
  
  public XmlReader(InputStream is, boolean lenient) throws IOException, XmlStreamReaderException {
    this._defaultEncoding = _staticDefaultEncoding;
    try {
      doRawStream(is, lenient);
    } catch (XmlStreamReaderException ex) {
      if (!lenient)
        throw ex; 
      doLenientDetection(null, ex);
    } 
  }
  
  public XmlReader(URL url) throws IOException {
    this(url.openConnection());
  }
  
  public XmlReader(URLConnection conn) throws IOException {
    this._defaultEncoding = _staticDefaultEncoding;
    boolean lenient = true;
    if (conn instanceof java.net.HttpURLConnection) {
      try {
        doHttpStream(conn.getInputStream(), conn.getContentType(), lenient);
      } catch (XmlStreamReaderException ex) {
        doLenientDetection(conn.getContentType(), ex);
      } 
    } else if (conn.getContentType() != null) {
      try {
        doHttpStream(conn.getInputStream(), conn.getContentType(), lenient);
      } catch (XmlStreamReaderException ex) {
        doLenientDetection(conn.getContentType(), ex);
      } 
    } else {
      try {
        doRawStream(conn.getInputStream(), lenient);
      } catch (XmlStreamReaderException ex) {
        doLenientDetection(null, ex);
      } 
    } 
  }
  
  public XmlReader(InputStream is, String httpContentType) throws IOException {
    this(is, httpContentType, true);
  }
  
  public XmlReader(InputStream is, String httpContentType, boolean lenient, String defaultEncoding) throws IOException, XmlStreamReaderException {
    this._defaultEncoding = (defaultEncoding == null) ? _staticDefaultEncoding : defaultEncoding;
    try {
      doHttpStream(is, httpContentType, lenient);
    } catch (XmlStreamReaderException ex) {
      if (!lenient)
        throw ex; 
      doLenientDetection(httpContentType, ex);
    } 
  }
  
  public XmlReader(InputStream is, String httpContentType, boolean lenient) throws IOException, XmlStreamReaderException {
    this(is, httpContentType, lenient, null);
  }
  
  private void doLenientDetection(String httpContentType, XmlStreamReaderException ex) throws IOException {
    if (httpContentType != null)
      if (httpContentType.startsWith("text/html")) {
        httpContentType = httpContentType.substring("text/html".length());
        httpContentType = "text/xml" + httpContentType;
        try {
          doHttpStream(ex.getInputStream(), httpContentType, true);
          ex = null;
        } catch (XmlStreamReaderException ex2) {
          ex = ex2;
        } 
      }  
    if (ex != null) {
      String encoding = ex.getXmlEncoding();
      if (encoding == null)
        encoding = ex.getContentTypeEncoding(); 
      if (encoding == null)
        encoding = (this._defaultEncoding == null) ? "UTF-8" : this._defaultEncoding; 
      prepareReader(ex.getInputStream(), encoding);
    } 
  }
  
  public String getEncoding() {
    return this._encoding;
  }
  
  public int read(char[] buf, int offset, int len) throws IOException {
    return this._reader.read(buf, offset, len);
  }
  
  public void close() throws IOException {
    this._reader.close();
  }
  
  private void doRawStream(InputStream is, boolean lenient) throws IOException {
    BufferedInputStream pis = new BufferedInputStream(is, 4096);
    String bomEnc = getBOMEncoding(pis);
    String xmlGuessEnc = getXMLGuessEncoding(pis);
    String xmlEnc = getXmlProlog(pis, xmlGuessEnc);
    String encoding = calculateRawEncoding(bomEnc, xmlGuessEnc, xmlEnc, pis);
    prepareReader(pis, encoding);
  }
  
  private void doHttpStream(InputStream is, String httpContentType, boolean lenient) throws IOException {
    BufferedInputStream pis = new BufferedInputStream(is, 4096);
    String cTMime = getContentTypeMime(httpContentType);
    String cTEnc = getContentTypeEncoding(httpContentType);
    String bomEnc = getBOMEncoding(pis);
    String xmlGuessEnc = getXMLGuessEncoding(pis);
    String xmlEnc = getXmlProlog(pis, xmlGuessEnc);
    String encoding = calculateHttpEncoding(cTMime, cTEnc, bomEnc, xmlGuessEnc, xmlEnc, pis, lenient);
    prepareReader(pis, encoding);
  }
  
  private void prepareReader(InputStream is, String encoding) throws IOException {
    this._reader = new InputStreamReader(is, encoding);
    this._encoding = encoding;
  }
  
  private String calculateRawEncoding(String bomEnc, String xmlGuessEnc, String xmlEnc, InputStream is) throws IOException {
    String encoding;
    if (bomEnc == null) {
      if (xmlGuessEnc == null || xmlEnc == null) {
        encoding = (this._defaultEncoding == null) ? "UTF-8" : this._defaultEncoding;
      } else if (xmlEnc.equals("UTF-16") && (xmlGuessEnc.equals("UTF-16BE") || xmlGuessEnc.equals("UTF-16LE"))) {
        encoding = xmlGuessEnc;
      } else {
        encoding = xmlEnc;
      } 
    } else if (bomEnc.equals("UTF-8")) {
      if (xmlGuessEnc != null && !xmlGuessEnc.equals("UTF-8"))
        throw new XmlStreamReaderException(RAW_EX_1.format(new Object[] { bomEnc, xmlGuessEnc, xmlEnc }, ), bomEnc, xmlGuessEnc, xmlEnc, is); 
      if (xmlEnc != null && !xmlEnc.equals("UTF-8"))
        throw new XmlStreamReaderException(RAW_EX_1.format(new Object[] { bomEnc, xmlGuessEnc, xmlEnc }, ), bomEnc, xmlGuessEnc, xmlEnc, is); 
      encoding = "UTF-8";
    } else if (bomEnc.equals("UTF-16BE") || bomEnc.equals("UTF-16LE")) {
      if (xmlGuessEnc != null && !xmlGuessEnc.equals(bomEnc))
        throw new IOException(RAW_EX_1.format(new Object[] { bomEnc, xmlGuessEnc, xmlEnc })); 
      if (xmlEnc != null && !xmlEnc.equals("UTF-16") && !xmlEnc.equals(bomEnc))
        throw new XmlStreamReaderException(RAW_EX_1.format(new Object[] { bomEnc, xmlGuessEnc, xmlEnc }, ), bomEnc, xmlGuessEnc, xmlEnc, is); 
      encoding = bomEnc;
    } else {
      throw new XmlStreamReaderException(RAW_EX_2.format(new Object[] { bomEnc, xmlGuessEnc, xmlEnc }, ), bomEnc, xmlGuessEnc, xmlEnc, is);
    } 
    return encoding;
  }
  
  private String calculateHttpEncoding(String cTMime, String cTEnc, String bomEnc, String xmlGuessEnc, String xmlEnc, InputStream is, boolean lenient) throws IOException {
    String encoding;
    if ((lenient & ((xmlEnc != null) ? 1 : 0)) != 0) {
      encoding = xmlEnc;
    } else {
      boolean appXml = isAppXml(cTMime);
      boolean textXml = isTextXml(cTMime);
      if (appXml || textXml) {
        if (cTEnc == null) {
          if (appXml) {
            encoding = calculateRawEncoding(bomEnc, xmlGuessEnc, xmlEnc, is);
          } else {
            encoding = (this._defaultEncoding == null) ? "US-ASCII" : this._defaultEncoding;
          } 
        } else {
          if (bomEnc != null && (cTEnc.equals("UTF-16BE") || cTEnc.equals("UTF-16LE")))
            throw new XmlStreamReaderException(HTTP_EX_1.format(new Object[] { cTMime, cTEnc, bomEnc, xmlGuessEnc, xmlEnc }, ), cTMime, cTEnc, bomEnc, xmlGuessEnc, xmlEnc, is); 
          if (cTEnc.equals("UTF-16")) {
            if (bomEnc != null && bomEnc.startsWith("UTF-16")) {
              encoding = bomEnc;
            } else {
              throw new XmlStreamReaderException(HTTP_EX_2.format(new Object[] { cTMime, cTEnc, bomEnc, xmlGuessEnc, xmlEnc }, ), cTMime, cTEnc, bomEnc, xmlGuessEnc, xmlEnc, is);
            } 
          } else {
            encoding = cTEnc;
          } 
        } 
      } else {
        throw new XmlStreamReaderException(HTTP_EX_3.format(new Object[] { cTMime, cTEnc, bomEnc, xmlGuessEnc, xmlEnc }, ), cTMime, cTEnc, bomEnc, xmlGuessEnc, xmlEnc, is);
      } 
    } 
    return encoding;
  }
  
  private static String getContentTypeMime(String httpContentType) {
    String mime = null;
    if (httpContentType != null) {
      int i = httpContentType.indexOf(";");
      mime = ((i == -1) ? httpContentType : httpContentType.substring(0, i)).trim();
    } 
    return mime;
  }
  
  private static final Pattern CHARSET_PATTERN = Pattern.compile("charset=([.[^; ]]*)");
  
  private static String getContentTypeEncoding(String httpContentType) {
    String encoding = null;
    if (httpContentType != null) {
      int i = httpContentType.indexOf(";");
      if (i > -1) {
        String postMime = httpContentType.substring(i + 1);
        Matcher m = CHARSET_PATTERN.matcher(postMime);
        encoding = m.find() ? m.group(1) : null;
        encoding = (encoding != null) ? encoding.toUpperCase(Locale.ENGLISH) : null;
      } 
    } 
    return encoding;
  }
  
  private static String getBOMEncoding(BufferedInputStream is) throws IOException {
    String encoding = null;
    int[] bytes = new int[3];
    is.mark(3);
    bytes[0] = is.read();
    bytes[1] = is.read();
    bytes[2] = is.read();
    if (bytes[0] == 254 && bytes[1] == 255) {
      encoding = "UTF-16BE";
      is.reset();
      is.read();
      is.read();
    } else if (bytes[0] == 255 && bytes[1] == 254) {
      encoding = "UTF-16LE";
      is.reset();
      is.read();
      is.read();
    } else if (bytes[0] == 239 && bytes[1] == 187 && bytes[2] == 191) {
      encoding = "UTF-8";
    } else {
      is.reset();
    } 
    return encoding;
  }
  
  private static String getXMLGuessEncoding(BufferedInputStream is) throws IOException {
    String encoding = null;
    int[] bytes = new int[4];
    is.mark(4);
    bytes[0] = is.read();
    bytes[1] = is.read();
    bytes[2] = is.read();
    bytes[3] = is.read();
    is.reset();
    if (bytes[0] == 0 && bytes[1] == 60 && bytes[2] == 0 && bytes[3] == 63) {
      encoding = "UTF-16BE";
    } else if (bytes[0] == 60 && bytes[1] == 0 && bytes[2] == 63 && bytes[3] == 0) {
      encoding = "UTF-16LE";
    } else if (bytes[0] == 60 && bytes[1] == 63 && bytes[2] == 120 && bytes[3] == 109) {
      encoding = "UTF-8";
    } else if (bytes[0] == 76 && bytes[1] == 111 && bytes[2] == 167 && bytes[3] == 148) {
      encoding = "CP1047";
    } 
    return encoding;
  }
  
  static final Pattern ENCODING_PATTERN = Pattern.compile("<\\?xml.*encoding[\\s]*=[\\s]*((?:\".[^\"]*\")|(?:'.[^']*'))", 8);
  
  private static String getXmlProlog(BufferedInputStream is, String guessedEnc) throws IOException {
    String encoding = null;
    if (guessedEnc != null) {
      byte[] bytes = new byte[4096];
      is.mark(4096);
      int offset = 0;
      int max = 4096;
      int c = is.read(bytes, offset, max);
      int firstGT = -1;
      String xmlProlog = null;
      while (c != -1 && firstGT == -1 && offset < 4096) {
        offset += c;
        max -= c;
        c = is.read(bytes, offset, max);
        xmlProlog = new String(bytes, 0, offset, guessedEnc);
        firstGT = xmlProlog.indexOf('>');
      } 
      if (firstGT == -1) {
        if (c == -1)
          throw new IOException("Unexpected end of XML stream"); 
        throw new IOException("XML prolog or ROOT element not found on first " + offset + " bytes");
      } 
      int bytesRead = offset;
      if (bytesRead > 0) {
        is.reset();
        BufferedReader bReader = new BufferedReader(new StringReader(xmlProlog.substring(0, firstGT + 1)));
        StringBuilder prolog = new StringBuilder();
        String line = bReader.readLine();
        while (line != null) {
          prolog.append(line);
          line = bReader.readLine();
        } 
        Matcher m = ENCODING_PATTERN.matcher(prolog);
        if (m.find()) {
          encoding = m.group(1).toUpperCase(Locale.ENGLISH);
          encoding = encoding.substring(1, encoding.length() - 1);
        } 
      } 
    } 
    return encoding;
  }
  
  private static boolean isAppXml(String mime) {
    return (mime != null && (mime.equals("application/xml") || mime.equals("application/xml-dtd") || mime.equals("application/xml-external-parsed-entity") || (mime.startsWith("application/") && mime.endsWith("+xml"))));
  }
  
  private static boolean isTextXml(String mime) {
    return (mime != null && (mime.equals("text/xml") || mime.equals("text/xml-external-parsed-entity") || (mime.startsWith("text/") && mime.endsWith("+xml"))));
  }
  
  private static final MessageFormat RAW_EX_1 = new MessageFormat("Invalid encoding, BOM [{0}] XML guess [{1}] XML prolog [{2}] encoding mismatch");
  
  private static final MessageFormat RAW_EX_2 = new MessageFormat("Invalid encoding, BOM [{0}] XML guess [{1}] XML prolog [{2}] unknown BOM");
  
  private static final MessageFormat HTTP_EX_1 = new MessageFormat("Invalid encoding, CT-MIME [{0}] CT-Enc [{1}] BOM [{2}] XML guess [{3}] XML prolog [{4}], BOM must be NULL");
  
  private static final MessageFormat HTTP_EX_2 = new MessageFormat("Invalid encoding, CT-MIME [{0}] CT-Enc [{1}] BOM [{2}] XML guess [{3}] XML prolog [{4}], encoding mismatch");
  
  private static final MessageFormat HTTP_EX_3 = new MessageFormat("Invalid encoding, CT-MIME [{0}] CT-Enc [{1}] BOM [{2}] XML guess [{3}] XML prolog [{4}], Invalid MIME");
}
