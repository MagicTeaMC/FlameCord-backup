package org.codehaus.plexus.interpolation;

import java.io.FilterReader;
import java.io.IOException;
import java.io.Reader;

public class InterpolatorFilterReader extends FilterReader {
  private Interpolator interpolator;
  
  private RecursionInterceptor recursionInterceptor;
  
  private String replaceData = null;
  
  private int replaceIndex = -1;
  
  private int previousIndex = -1;
  
  public static final String DEFAULT_BEGIN_TOKEN = "${";
  
  public static final String DEFAULT_END_TOKEN = "}";
  
  private String beginToken;
  
  private String orginalBeginToken;
  
  private String endToken;
  
  private boolean interpolateWithPrefixPattern = true;
  
  private String escapeString;
  
  private boolean useEscape = false;
  
  private boolean preserveEscapeString = false;
  
  public InterpolatorFilterReader(Reader in, Interpolator interpolator) {
    this(in, interpolator, "${", "}");
  }
  
  public InterpolatorFilterReader(Reader in, Interpolator interpolator, String beginToken, String endToken) {
    this(in, interpolator, beginToken, endToken, new SimpleRecursionInterceptor());
  }
  
  public InterpolatorFilterReader(Reader in, Interpolator interpolator, RecursionInterceptor ri) {
    this(in, interpolator, "${", "}", ri);
  }
  
  public InterpolatorFilterReader(Reader in, Interpolator interpolator, String beginToken, String endToken, RecursionInterceptor ri) {
    super(in);
    this.interpolator = interpolator;
    this.beginToken = beginToken;
    this.endToken = endToken;
    this.recursionInterceptor = ri;
    this.orginalBeginToken = this.beginToken;
  }
  
  public long skip(long n) throws IOException {
    if (n < 0L)
      throw new IllegalArgumentException("skip value is negative"); 
    for (long i = 0L; i < n; i++) {
      if (read() == -1)
        return i; 
    } 
    return n;
  }
  
  public int read(char[] cbuf, int off, int len) throws IOException {
    for (int i = 0; i < len; i++) {
      int ch = read();
      if (ch == -1) {
        if (i == 0)
          return -1; 
        return i;
      } 
      cbuf[off + i] = (char)ch;
    } 
    return len;
  }
  
  public int read() throws IOException {
    if (this.replaceIndex != -1 && this.replaceIndex < this.replaceData.length()) {
      int i = this.replaceData.charAt(this.replaceIndex++);
      if (this.replaceIndex >= this.replaceData.length())
        this.replaceIndex = -1; 
      return i;
    } 
    int ch = -1;
    if (this.previousIndex != -1 && this.previousIndex < this.endToken.length()) {
      ch = this.endToken.charAt(this.previousIndex++);
    } else {
      ch = this.in.read();
    } 
    if (ch == this.beginToken.charAt(0) || (this.useEscape && ch == this.orginalBeginToken.charAt(0))) {
      StringBuilder key = new StringBuilder();
      key.append((char)ch);
      int beginTokenMatchPos = 1;
      while (true) {
        if (this.previousIndex != -1 && this.previousIndex < this.endToken.length()) {
          ch = this.endToken.charAt(this.previousIndex++);
        } else {
          ch = this.in.read();
        } 
        if (ch != -1) {
          key.append((char)ch);
          if (beginTokenMatchPos < this.beginToken.length() && ch != this.beginToken
            .charAt(beginTokenMatchPos++) && this.useEscape && this.orginalBeginToken
            .length() > beginTokenMatchPos - 1 && ch != this.orginalBeginToken
            .charAt(beginTokenMatchPos - 1)) {
            ch = -1;
            break;
          } 
          if (this.useEscape && this.orginalBeginToken == this.endToken && key.toString().startsWith(this.beginToken)) {
            ch = this.in.read();
            key.append((char)ch);
          } 
          if (ch == this.endToken.charAt(0))
            break; 
          continue;
        } 
        break;
      } 
      if (ch != -1 && this.endToken.length() > 1) {
        int endTokenMatchPos = 1;
        while (true) {
          if (this.previousIndex != -1 && this.previousIndex < this.endToken.length()) {
            ch = this.endToken.charAt(this.previousIndex++);
          } else {
            ch = this.in.read();
          } 
          if (ch != -1) {
            key.append((char)ch);
            if (ch != this.endToken.charAt(endTokenMatchPos++)) {
              ch = -1;
              break;
            } 
            if (endTokenMatchPos >= this.endToken.length())
              break; 
            continue;
          } 
          break;
        } 
      } 
      if (ch == -1) {
        this.replaceData = key.toString();
        this.replaceIndex = 1;
        return this.replaceData.charAt(0);
      } 
      String value = null;
      try {
        boolean escapeFound = false;
        if (this.useEscape)
          if (key.toString().startsWith(this.escapeString + this.orginalBeginToken)) {
            String keyStr = key.toString();
            if (!this.preserveEscapeString) {
              value = keyStr.substring(this.escapeString.length(), keyStr.length());
            } else {
              value = keyStr;
            } 
            escapeFound = true;
          }  
        if (!escapeFound)
          if (this.interpolateWithPrefixPattern) {
            value = this.interpolator.interpolate(key.toString(), "", this.recursionInterceptor);
          } else {
            value = this.interpolator.interpolate(key.toString(), this.recursionInterceptor);
          }  
      } catch (InterpolationException e) {
        IllegalArgumentException error = new IllegalArgumentException(e.getMessage());
        error.initCause(e);
        throw error;
      } 
      if (value != null) {
        if (value.length() != 0) {
          this.replaceData = value;
          this.replaceIndex = 0;
        } 
        return read();
      } 
      this.previousIndex = 0;
      this.replaceData = key.substring(0, key.length() - this.endToken.length());
      this.replaceIndex = 0;
      return this.beginToken.charAt(0);
    } 
    return ch;
  }
  
  public boolean isInterpolateWithPrefixPattern() {
    return this.interpolateWithPrefixPattern;
  }
  
  public void setInterpolateWithPrefixPattern(boolean interpolateWithPrefixPattern) {
    this.interpolateWithPrefixPattern = interpolateWithPrefixPattern;
  }
  
  public String getEscapeString() {
    return this.escapeString;
  }
  
  public void setEscapeString(String escapeString) {
    if (escapeString != null && escapeString.length() >= 1) {
      this.escapeString = escapeString;
      this.orginalBeginToken = this.beginToken;
      this.beginToken = escapeString + this.beginToken;
      this.useEscape = (escapeString != null && escapeString.length() >= 1);
    } 
  }
  
  public boolean isPreserveEscapeString() {
    return this.preserveEscapeString;
  }
  
  public void setPreserveEscapeString(boolean preserveEscapeString) {
    this.preserveEscapeString = preserveEscapeString;
  }
  
  public RecursionInterceptor getRecursionInterceptor() {
    return this.recursionInterceptor;
  }
  
  public InterpolatorFilterReader setRecursionInterceptor(RecursionInterceptor recursionInterceptor) {
    this.recursionInterceptor = recursionInterceptor;
    return this;
  }
}
