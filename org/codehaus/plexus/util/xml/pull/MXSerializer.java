package org.codehaus.plexus.util.xml.pull;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;

public class MXSerializer implements XmlSerializer {
  protected static final String XML_URI = "http://www.w3.org/XML/1998/namespace";
  
  protected static final String XMLNS_URI = "http://www.w3.org/2000/xmlns/";
  
  private static final boolean TRACE_SIZING = false;
  
  protected final String FEATURE_SERIALIZER_ATTVALUE_USE_APOSTROPHE = "http://xmlpull.org/v1/doc/features.html#serializer-attvalue-use-apostrophe";
  
  protected final String FEATURE_NAMES_INTERNED = "http://xmlpull.org/v1/doc/features.html#names-interned";
  
  protected final String PROPERTY_SERIALIZER_INDENTATION = "http://xmlpull.org/v1/doc/properties.html#serializer-indentation";
  
  protected final String PROPERTY_SERIALIZER_LINE_SEPARATOR = "http://xmlpull.org/v1/doc/properties.html#serializer-line-separator";
  
  protected static final String PROPERTY_LOCATION = "http://xmlpull.org/v1/doc/properties.html#location";
  
  protected boolean namesInterned;
  
  protected boolean attributeUseApostrophe;
  
  protected String indentationString = null;
  
  protected String lineSeparator = "\n";
  
  protected String location;
  
  protected Writer out;
  
  protected int autoDeclaredPrefixes;
  
  protected int depth = 0;
  
  protected String[] elNamespace = new String[2];
  
  protected String[] elName = new String[this.elNamespace.length];
  
  protected int[] elNamespaceCount = new int[this.elNamespace.length];
  
  protected int namespaceEnd = 0;
  
  protected String[] namespacePrefix = new String[8];
  
  protected String[] namespaceUri = new String[this.namespacePrefix.length];
  
  protected boolean finished;
  
  protected boolean pastRoot;
  
  protected boolean setPrefixCalled;
  
  protected boolean startTagIncomplete;
  
  protected boolean doIndent;
  
  protected boolean seenTag;
  
  protected boolean seenBracket;
  
  protected boolean seenBracketBracket;
  
  private static final int BUF_LEN = (Runtime.getRuntime().freeMemory() > 1000000L) ? 8192 : 256;
  
  protected char[] buf = new char[BUF_LEN];
  
  protected static final String[] precomputedPrefixes = new String[32];
  
  static {
    for (int i = 0; i < precomputedPrefixes.length; i++)
      precomputedPrefixes[i] = ("n" + i).intern(); 
  }
  
  private boolean checkNamesInterned = false;
  
  protected int offsetNewLine;
  
  protected int indentationJump;
  
  protected char[] indentationBuf;
  
  protected int maxIndentLevel;
  
  protected boolean writeLineSeparator;
  
  protected boolean writeIndentation;
  
  private void checkInterning(String name) {
    if (this.namesInterned && name != name.intern())
      throw new IllegalArgumentException("all names passed as arguments must be internedwhen NAMES INTERNED feature is enabled"); 
  }
  
  protected void reset() {
    this.location = null;
    this.out = null;
    this.autoDeclaredPrefixes = 0;
    this.depth = 0;
    for (int i = 0; i < this.elNamespaceCount.length; i++) {
      this.elName[i] = null;
      this.elNamespace[i] = null;
      this.elNamespaceCount[i] = 2;
    } 
    this.namespaceEnd = 0;
    this.namespacePrefix[this.namespaceEnd] = "xmlns";
    this.namespaceUri[this.namespaceEnd] = "http://www.w3.org/2000/xmlns/";
    this.namespaceEnd++;
    this.namespacePrefix[this.namespaceEnd] = "xml";
    this.namespaceUri[this.namespaceEnd] = "http://www.w3.org/XML/1998/namespace";
    this.namespaceEnd++;
    this.finished = false;
    this.pastRoot = false;
    this.setPrefixCalled = false;
    this.startTagIncomplete = false;
    this.seenTag = false;
    this.seenBracket = false;
    this.seenBracketBracket = false;
  }
  
  protected void ensureElementsCapacity() {
    int elStackSize = this.elName.length;
    int newSize = ((this.depth >= 7) ? (2 * this.depth) : 8) + 2;
    boolean needsCopying = (elStackSize > 0);
    String[] arr = null;
    arr = new String[newSize];
    if (needsCopying)
      System.arraycopy(this.elName, 0, arr, 0, elStackSize); 
    this.elName = arr;
    arr = new String[newSize];
    if (needsCopying)
      System.arraycopy(this.elNamespace, 0, arr, 0, elStackSize); 
    this.elNamespace = arr;
    int[] iarr = new int[newSize];
    if (needsCopying) {
      System.arraycopy(this.elNamespaceCount, 0, iarr, 0, elStackSize);
    } else {
      iarr[0] = 0;
    } 
    this.elNamespaceCount = iarr;
  }
  
  protected void ensureNamespacesCapacity() {
    int newSize = (this.namespaceEnd > 7) ? (2 * this.namespaceEnd) : 8;
    String[] newNamespacePrefix = new String[newSize];
    String[] newNamespaceUri = new String[newSize];
    if (this.namespacePrefix != null) {
      System.arraycopy(this.namespacePrefix, 0, newNamespacePrefix, 0, this.namespaceEnd);
      System.arraycopy(this.namespaceUri, 0, newNamespaceUri, 0, this.namespaceEnd);
    } 
    this.namespacePrefix = newNamespacePrefix;
    this.namespaceUri = newNamespaceUri;
  }
  
  public void setFeature(String name, boolean state) throws IllegalArgumentException, IllegalStateException {
    if (name == null)
      throw new IllegalArgumentException("feature name can not be null"); 
    if ("http://xmlpull.org/v1/doc/features.html#names-interned".equals(name)) {
      this.namesInterned = state;
    } else if ("http://xmlpull.org/v1/doc/features.html#serializer-attvalue-use-apostrophe".equals(name)) {
      this.attributeUseApostrophe = state;
    } else {
      throw new IllegalStateException("unsupported feature " + name);
    } 
  }
  
  public boolean getFeature(String name) throws IllegalArgumentException {
    if (name == null)
      throw new IllegalArgumentException("feature name can not be null"); 
    if ("http://xmlpull.org/v1/doc/features.html#names-interned".equals(name))
      return this.namesInterned; 
    if ("http://xmlpull.org/v1/doc/features.html#serializer-attvalue-use-apostrophe".equals(name))
      return this.attributeUseApostrophe; 
    return false;
  }
  
  protected void rebuildIndentationBuf() {
    if (!this.doIndent)
      return; 
    int maxIndent = 65;
    int bufSize = 0;
    this.offsetNewLine = 0;
    if (this.writeLineSeparator) {
      this.offsetNewLine = this.lineSeparator.length();
      bufSize += this.offsetNewLine;
    } 
    this.maxIndentLevel = 0;
    if (this.writeIndentation) {
      this.indentationJump = this.indentationString.length();
      this.maxIndentLevel = 65 / this.indentationJump;
      bufSize += this.maxIndentLevel * this.indentationJump;
    } 
    if (this.indentationBuf == null || this.indentationBuf.length < bufSize)
      this.indentationBuf = new char[bufSize + 8]; 
    int bufPos = 0;
    if (this.writeLineSeparator)
      for (int i = 0; i < this.lineSeparator.length(); i++)
        this.indentationBuf[bufPos++] = this.lineSeparator.charAt(i);  
    if (this.writeIndentation)
      for (int i = 0; i < this.maxIndentLevel; i++) {
        for (int j = 0; j < this.indentationString.length(); j++)
          this.indentationBuf[bufPos++] = this.indentationString.charAt(j); 
      }  
  }
  
  protected void writeIndent() throws IOException {
    int start = this.writeLineSeparator ? 0 : this.offsetNewLine;
    int level = (this.depth > this.maxIndentLevel) ? this.maxIndentLevel : this.depth;
    this.out.write(this.indentationBuf, start, level * this.indentationJump + this.offsetNewLine);
  }
  
  public void setProperty(String name, Object value) throws IllegalArgumentException, IllegalStateException {
    if (name == null)
      throw new IllegalArgumentException("property name can not be null"); 
    if ("http://xmlpull.org/v1/doc/properties.html#serializer-indentation".equals(name)) {
      this.indentationString = (String)value;
    } else if ("http://xmlpull.org/v1/doc/properties.html#serializer-line-separator".equals(name)) {
      this.lineSeparator = (String)value;
    } else if ("http://xmlpull.org/v1/doc/properties.html#location".equals(name)) {
      this.location = (String)value;
    } else {
      throw new IllegalStateException("unsupported property " + name);
    } 
    this.writeLineSeparator = (this.lineSeparator != null && this.lineSeparator.length() > 0);
    this.writeIndentation = (this.indentationString != null && this.indentationString.length() > 0);
    this.doIndent = (this.indentationString != null && (this.writeLineSeparator || this.writeIndentation));
    rebuildIndentationBuf();
    this.seenTag = false;
  }
  
  public Object getProperty(String name) throws IllegalArgumentException {
    if (name == null)
      throw new IllegalArgumentException("property name can not be null"); 
    if ("http://xmlpull.org/v1/doc/properties.html#serializer-indentation".equals(name))
      return this.indentationString; 
    if ("http://xmlpull.org/v1/doc/properties.html#serializer-line-separator".equals(name))
      return this.lineSeparator; 
    if ("http://xmlpull.org/v1/doc/properties.html#location".equals(name))
      return this.location; 
    return null;
  }
  
  private String getLocation() {
    return (this.location != null) ? (" @" + this.location) : "";
  }
  
  public Writer getWriter() {
    return this.out;
  }
  
  public void setOutput(Writer writer) {
    reset();
    this.out = writer;
  }
  
  public void setOutput(OutputStream os, String encoding) throws IOException {
    if (os == null)
      throw new IllegalArgumentException("output stream can not be null"); 
    reset();
    if (encoding != null) {
      this.out = new OutputStreamWriter(os, encoding);
    } else {
      this.out = new OutputStreamWriter(os);
    } 
  }
  
  public void startDocument(String encoding, Boolean standalone) throws IOException {
    char apos = this.attributeUseApostrophe ? '\'' : '"';
    if (this.attributeUseApostrophe) {
      this.out.write("<?xml version='1.0'");
    } else {
      this.out.write("<?xml version=\"1.0\"");
    } 
    if (encoding != null) {
      this.out.write(" encoding=");
      this.out.write(this.attributeUseApostrophe ? 39 : 34);
      this.out.write(encoding);
      this.out.write(this.attributeUseApostrophe ? 39 : 34);
    } 
    if (standalone != null) {
      this.out.write(" standalone=");
      this.out.write(this.attributeUseApostrophe ? 39 : 34);
      if (standalone.booleanValue()) {
        this.out.write("yes");
      } else {
        this.out.write("no");
      } 
      this.out.write(this.attributeUseApostrophe ? 39 : 34);
    } 
    this.out.write("?>");
    if (this.writeLineSeparator)
      this.out.write(this.lineSeparator); 
  }
  
  public void endDocument() throws IOException {
    while (this.depth > 0)
      endTag(this.elNamespace[this.depth], this.elName[this.depth]); 
    if (this.writeLineSeparator)
      this.out.write(this.lineSeparator); 
    this.finished = this.pastRoot = this.startTagIncomplete = true;
    this.out.flush();
  }
  
  public void setPrefix(String prefix, String namespace) throws IOException {
    if (this.startTagIncomplete)
      closeStartTag(); 
    if (prefix == null)
      prefix = ""; 
    if (!this.namesInterned) {
      prefix = prefix.intern();
    } else if (this.checkNamesInterned) {
      checkInterning(prefix);
    } else if (prefix == null) {
      throw new IllegalArgumentException("prefix must be not null" + getLocation());
    } 
    for (int i = this.elNamespaceCount[this.depth]; i < this.namespaceEnd; i++) {
      if (prefix == this.namespacePrefix[i])
        throw new IllegalStateException("duplicated prefix " + printable(prefix) + getLocation()); 
    } 
    if (!this.namesInterned) {
      namespace = namespace.intern();
    } else if (this.checkNamesInterned) {
      checkInterning(namespace);
    } else if (namespace == null) {
      throw new IllegalArgumentException("namespace must be not null" + getLocation());
    } 
    if (this.namespaceEnd >= this.namespacePrefix.length)
      ensureNamespacesCapacity(); 
    this.namespacePrefix[this.namespaceEnd] = prefix;
    this.namespaceUri[this.namespaceEnd] = namespace;
    this.namespaceEnd++;
    this.setPrefixCalled = true;
  }
  
  protected String lookupOrDeclarePrefix(String namespace) {
    return getPrefix(namespace, true);
  }
  
  public String getPrefix(String namespace, boolean generatePrefix) {
    if (!this.namesInterned) {
      namespace = namespace.intern();
    } else if (this.checkNamesInterned) {
      checkInterning(namespace);
    } 
    if (namespace == null)
      throw new IllegalArgumentException("namespace must be not null" + getLocation()); 
    if (namespace.length() == 0)
      throw new IllegalArgumentException("default namespace cannot have prefix" + getLocation()); 
    for (int i = this.namespaceEnd - 1; i >= 0; i--) {
      if (namespace == this.namespaceUri[i]) {
        String prefix = this.namespacePrefix[i];
        for (int p = this.namespaceEnd - 1; p > i; p--) {
          if (prefix == this.namespacePrefix[p]);
        } 
        return prefix;
      } 
    } 
    if (!generatePrefix)
      return null; 
    return generatePrefix(namespace);
  }
  
  private String generatePrefix(String namespace) {
    this.autoDeclaredPrefixes++;
    String prefix = (this.autoDeclaredPrefixes < precomputedPrefixes.length) ? precomputedPrefixes[this.autoDeclaredPrefixes] : ("n" + this.autoDeclaredPrefixes).intern();
    for (int i = this.namespaceEnd - 1; i >= 0; i--) {
      if (prefix == this.namespacePrefix[i]);
    } 
    if (this.namespaceEnd >= this.namespacePrefix.length)
      ensureNamespacesCapacity(); 
    this.namespacePrefix[this.namespaceEnd] = prefix;
    this.namespaceUri[this.namespaceEnd] = namespace;
    this.namespaceEnd++;
    return prefix;
  }
  
  public int getDepth() {
    return this.depth;
  }
  
  public String getNamespace() {
    return this.elNamespace[this.depth];
  }
  
  public String getName() {
    return this.elName[this.depth];
  }
  
  public XmlSerializer startTag(String namespace, String name) throws IOException {
    if (this.startTagIncomplete)
      closeStartTag(); 
    this.seenBracket = this.seenBracketBracket = false;
    if (this.doIndent && this.depth > 0 && this.seenTag)
      writeIndent(); 
    this.seenTag = true;
    this.setPrefixCalled = false;
    this.startTagIncomplete = true;
    this.depth++;
    if (this.depth + 1 >= this.elName.length)
      ensureElementsCapacity(); 
    if (this.checkNamesInterned && this.namesInterned)
      checkInterning(namespace); 
    this.elNamespace[this.depth] = (this.namesInterned || namespace == null) ? namespace : namespace.intern();
    if (this.checkNamesInterned && this.namesInterned)
      checkInterning(name); 
    this.elName[this.depth] = (this.namesInterned || name == null) ? name : name.intern();
    if (this.out == null)
      throw new IllegalStateException("setOutput() must called set before serialization can start"); 
    this.out.write(60);
    if (namespace != null)
      if (namespace.length() > 0) {
        String prefix = null;
        if (this.depth > 0 && this.namespaceEnd - this.elNamespaceCount[this.depth - 1] == 1) {
          String uri = this.namespaceUri[this.namespaceEnd - 1];
          if (uri == namespace || uri.equals(namespace)) {
            String elPfx = this.namespacePrefix[this.namespaceEnd - 1];
            for (int pos = this.elNamespaceCount[this.depth - 1] - 1; pos >= 2; pos--) {
              String pf = this.namespacePrefix[pos];
              if (pf == elPfx || pf.equals(elPfx)) {
                String n = this.namespaceUri[pos];
                if (n == uri || n.equals(uri)) {
                  this.namespaceEnd--;
                  prefix = elPfx;
                } 
                break;
              } 
            } 
          } 
        } 
        if (prefix == null)
          prefix = lookupOrDeclarePrefix(namespace); 
        if (prefix.length() > 0) {
          this.out.write(prefix);
          this.out.write(58);
        } 
      } else {
        for (int i = this.namespaceEnd - 1; i >= 0; i--) {
          if (this.namespacePrefix[i] == "") {
            String uri = this.namespaceUri[i];
            if (uri == null) {
              setPrefix("", "");
              break;
            } 
            if (uri.length() > 0)
              throw new IllegalStateException("start tag can not be written in empty default namespace as default namespace is currently bound to '" + uri + "'" + getLocation()); 
            break;
          } 
        } 
      }  
    this.out.write(name);
    return this;
  }
  
  public XmlSerializer attribute(String namespace, String name, String value) throws IOException {
    if (!this.startTagIncomplete)
      throw new IllegalArgumentException("startTag() must be called before attribute()" + getLocation()); 
    this.out.write(32);
    if (namespace != null && namespace.length() > 0) {
      if (!this.namesInterned) {
        namespace = namespace.intern();
      } else if (this.checkNamesInterned) {
        checkInterning(namespace);
      } 
      String prefix = lookupOrDeclarePrefix(namespace);
      if (prefix.length() == 0)
        prefix = generatePrefix(namespace); 
      this.out.write(prefix);
      this.out.write(58);
    } 
    this.out.write(name);
    this.out.write(61);
    this.out.write(this.attributeUseApostrophe ? 39 : 34);
    writeAttributeValue(value, this.out);
    this.out.write(this.attributeUseApostrophe ? 39 : 34);
    return this;
  }
  
  protected void closeStartTag() throws IOException {
    if (this.finished)
      throw new IllegalArgumentException("trying to write past already finished output" + getLocation()); 
    if (this.seenBracket)
      this.seenBracket = this.seenBracketBracket = false; 
    if (this.startTagIncomplete || this.setPrefixCalled) {
      if (this.setPrefixCalled)
        throw new IllegalArgumentException("startTag() must be called immediately after setPrefix()" + getLocation()); 
      if (!this.startTagIncomplete)
        throw new IllegalArgumentException("trying to close start tag that is not opened" + getLocation()); 
      writeNamespaceDeclarations();
      this.out.write(62);
      this.elNamespaceCount[this.depth] = this.namespaceEnd;
      this.startTagIncomplete = false;
    } 
  }
  
  private void writeNamespaceDeclarations() throws IOException {
    for (int i = this.elNamespaceCount[this.depth - 1]; i < this.namespaceEnd; i++) {
      if (this.doIndent && this.namespaceUri[i].length() > 40) {
        writeIndent();
        this.out.write(" ");
      } 
      if (this.namespacePrefix[i] != "") {
        this.out.write(" xmlns:");
        this.out.write(this.namespacePrefix[i]);
        this.out.write(61);
      } else {
        this.out.write(" xmlns=");
      } 
      this.out.write(this.attributeUseApostrophe ? 39 : 34);
      writeAttributeValue(this.namespaceUri[i], this.out);
      this.out.write(this.attributeUseApostrophe ? 39 : 34);
    } 
  }
  
  public XmlSerializer endTag(String namespace, String name) throws IOException {
    this.seenBracket = this.seenBracketBracket = false;
    if (namespace != null)
      if (!this.namesInterned) {
        namespace = namespace.intern();
      } else if (this.checkNamesInterned) {
        checkInterning(namespace);
      }  
    if (namespace != this.elNamespace[this.depth])
      throw new IllegalArgumentException("expected namespace " + printable(this.elNamespace[this.depth]) + " and not " + printable(namespace) + getLocation()); 
    if (name == null)
      throw new IllegalArgumentException("end tag name can not be null" + getLocation()); 
    if (this.checkNamesInterned && this.namesInterned)
      checkInterning(name); 
    if ((!this.namesInterned && !name.equals(this.elName[this.depth])) || (this.namesInterned && name != this.elName[this.depth]))
      throw new IllegalArgumentException("expected element name " + printable(this.elName[this.depth]) + " and not " + printable(name) + getLocation()); 
    if (this.startTagIncomplete) {
      writeNamespaceDeclarations();
      this.out.write(" />");
      this.depth--;
    } else {
      this.depth--;
      if (this.doIndent && this.seenTag)
        writeIndent(); 
      this.out.write("</");
      if (namespace != null && namespace.length() > 0) {
        String prefix = lookupOrDeclarePrefix(namespace);
        if (prefix.length() > 0) {
          this.out.write(prefix);
          this.out.write(58);
        } 
      } 
      this.out.write(name);
      this.out.write(62);
    } 
    this.namespaceEnd = this.elNamespaceCount[this.depth];
    this.startTagIncomplete = false;
    this.seenTag = true;
    return this;
  }
  
  public XmlSerializer text(String text) throws IOException {
    if (this.startTagIncomplete || this.setPrefixCalled)
      closeStartTag(); 
    if (this.doIndent && this.seenTag)
      this.seenTag = false; 
    writeElementContent(text, this.out);
    return this;
  }
  
  public XmlSerializer text(char[] buf, int start, int len) throws IOException {
    if (this.startTagIncomplete || this.setPrefixCalled)
      closeStartTag(); 
    if (this.doIndent && this.seenTag)
      this.seenTag = false; 
    writeElementContent(buf, start, len, this.out);
    return this;
  }
  
  public void cdsect(String text) throws IOException {
    if (this.startTagIncomplete || this.setPrefixCalled || this.seenBracket)
      closeStartTag(); 
    if (this.doIndent && this.seenTag)
      this.seenTag = false; 
    this.out.write("<![CDATA[");
    this.out.write(text);
    this.out.write("]]>");
  }
  
  public void entityRef(String text) throws IOException {
    if (this.startTagIncomplete || this.setPrefixCalled || this.seenBracket)
      closeStartTag(); 
    if (this.doIndent && this.seenTag)
      this.seenTag = false; 
    this.out.write(38);
    this.out.write(text);
    this.out.write(59);
  }
  
  public void processingInstruction(String text) throws IOException {
    if (this.startTagIncomplete || this.setPrefixCalled || this.seenBracket)
      closeStartTag(); 
    if (this.doIndent && this.seenTag)
      this.seenTag = false; 
    this.out.write("<?");
    this.out.write(text);
    this.out.write("?>");
  }
  
  public void comment(String text) throws IOException {
    if (this.startTagIncomplete || this.setPrefixCalled || this.seenBracket)
      closeStartTag(); 
    if (this.doIndent && this.seenTag)
      this.seenTag = false; 
    this.out.write("<!--");
    this.out.write(text);
    this.out.write("-->");
  }
  
  public void docdecl(String text) throws IOException {
    if (this.startTagIncomplete || this.setPrefixCalled || this.seenBracket)
      closeStartTag(); 
    if (this.doIndent && this.seenTag)
      this.seenTag = false; 
    this.out.write("<!DOCTYPE ");
    this.out.write(text);
    this.out.write(">");
  }
  
  public void ignorableWhitespace(String text) throws IOException {
    if (this.startTagIncomplete || this.setPrefixCalled || this.seenBracket)
      closeStartTag(); 
    if (this.doIndent && this.seenTag)
      this.seenTag = false; 
    if (text.length() == 0)
      throw new IllegalArgumentException("empty string is not allowed for ignorable whitespace" + getLocation()); 
    this.out.write(text);
  }
  
  public void flush() throws IOException {
    if (!this.finished && this.startTagIncomplete)
      closeStartTag(); 
    this.out.flush();
  }
  
  protected void writeAttributeValue(String value, Writer out) throws IOException {
    char quot = this.attributeUseApostrophe ? '\'' : '"';
    String quotEntity = this.attributeUseApostrophe ? "&apos;" : "&quot;";
    int pos = 0;
    for (int i = 0; i < value.length(); i++) {
      char ch = value.charAt(i);
      if (ch == '&') {
        if (i > pos)
          out.write(value.substring(pos, i)); 
        out.write("&amp;");
        pos = i + 1;
      } 
      if (ch == '<') {
        if (i > pos)
          out.write(value.substring(pos, i)); 
        out.write("&lt;");
        pos = i + 1;
      } else if (ch == quot) {
        if (i > pos)
          out.write(value.substring(pos, i)); 
        out.write(quotEntity);
        pos = i + 1;
      } else if (ch < ' ') {
        if (ch == '\r' || ch == '\n' || ch == '\t') {
          if (i > pos)
            out.write(value.substring(pos, i)); 
          out.write("&#");
          out.write(Integer.toString(ch));
          out.write(59);
          pos = i + 1;
        } else {
          throw new IllegalStateException("character " + Integer.toString(ch) + " is not allowed in output" + getLocation());
        } 
      } 
    } 
    if (pos > 0) {
      out.write(value.substring(pos));
    } else {
      out.write(value);
    } 
  }
  
  protected void writeElementContent(String text, Writer out) throws IOException {
    int pos = 0;
    for (int i = 0; i < text.length(); i++) {
      char ch = text.charAt(i);
      if (ch == ']') {
        if (this.seenBracket) {
          this.seenBracketBracket = true;
        } else {
          this.seenBracket = true;
        } 
      } else {
        if (ch == '&') {
          if (i > pos)
            out.write(text.substring(pos, i)); 
          out.write("&amp;");
          pos = i + 1;
        } else if (ch == '<') {
          if (i > pos)
            out.write(text.substring(pos, i)); 
          out.write("&lt;");
          pos = i + 1;
        } else if (this.seenBracketBracket && ch == '>') {
          if (i > pos)
            out.write(text.substring(pos, i)); 
          out.write("&gt;");
          pos = i + 1;
        } else if (ch < ' ') {
          if (ch != '\t' && ch != '\n' && ch != '\r')
            throw new IllegalStateException("character " + Integer.toString(ch) + " is not allowed in output" + getLocation()); 
        } 
        if (this.seenBracket)
          this.seenBracketBracket = this.seenBracket = false; 
      } 
    } 
    if (pos > 0) {
      out.write(text.substring(pos));
    } else {
      out.write(text);
    } 
  }
  
  protected void writeElementContent(char[] buf, int off, int len, Writer out) throws IOException {
    int end = off + len;
    int pos = off;
    for (int i = off; i < end; i++) {
      char ch = buf[i];
      if (ch == ']') {
        if (this.seenBracket) {
          this.seenBracketBracket = true;
        } else {
          this.seenBracket = true;
        } 
      } else {
        if (ch == '&') {
          if (i > pos)
            out.write(buf, pos, i - pos); 
          out.write("&amp;");
          pos = i + 1;
        } else if (ch == '<') {
          if (i > pos)
            out.write(buf, pos, i - pos); 
          out.write("&lt;");
          pos = i + 1;
        } else if (this.seenBracketBracket && ch == '>') {
          if (i > pos)
            out.write(buf, pos, i - pos); 
          out.write("&gt;");
          pos = i + 1;
        } else if (ch < ' ') {
          if (ch != '\t' && ch != '\n' && ch != '\r')
            throw new IllegalStateException("character " + Integer.toString(ch) + " is not allowed in output" + getLocation()); 
        } 
        if (this.seenBracket)
          this.seenBracketBracket = this.seenBracket = false; 
      } 
    } 
    if (end > pos)
      out.write(buf, pos, end - pos); 
  }
  
  protected static final String printable(String s) {
    if (s == null)
      return "null"; 
    StringBuilder retval = new StringBuilder(s.length() + 16);
    retval.append("'");
    for (int i = 0; i < s.length(); i++)
      addPrintable(retval, s.charAt(i)); 
    retval.append("'");
    return retval.toString();
  }
  
  protected static final String printable(char ch) {
    StringBuilder retval = new StringBuilder();
    addPrintable(retval, ch);
    return retval.toString();
  }
  
  private static void addPrintable(StringBuilder retval, char ch) {
    switch (ch) {
      case '\b':
        retval.append("\\b");
        return;
      case '\t':
        retval.append("\\t");
        return;
      case '\n':
        retval.append("\\n");
        return;
      case '\f':
        retval.append("\\f");
        return;
      case '\r':
        retval.append("\\r");
        return;
      case '"':
        retval.append("\\\"");
        return;
      case '\'':
        retval.append("\\'");
        return;
      case '\\':
        retval.append("\\\\");
        return;
    } 
    if (ch < ' ' || ch > '~') {
      String ss = "0000" + Integer.toString(ch, 16);
      retval.append("\\u").append(ss, ss.length() - 4, ss.length());
    } else {
      retval.append(ch);
    } 
  }
}
