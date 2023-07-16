package org.codehaus.plexus.interpolation.multi;

import java.io.FilterReader;
import java.io.IOException;
import java.io.Reader;
import java.util.LinkedHashSet;
import org.codehaus.plexus.interpolation.InterpolationException;
import org.codehaus.plexus.interpolation.Interpolator;
import org.codehaus.plexus.interpolation.RecursionInterceptor;
import org.codehaus.plexus.interpolation.SimpleRecursionInterceptor;

public class MultiDelimiterInterpolatorFilterReader extends FilterReader {
  private Interpolator interpolator;
  
  private RecursionInterceptor recursionInterceptor;
  
  private String replaceData = null;
  
  private int replaceIndex = -1;
  
  private int previousIndex = -1;
  
  public static final String DEFAULT_BEGIN_TOKEN = "${";
  
  public static final String DEFAULT_END_TOKEN = "}";
  
  private boolean interpolateWithPrefixPattern = true;
  
  private String escapeString;
  
  private boolean useEscape = false;
  
  private boolean preserveEscapeString = false;
  
  private LinkedHashSet<DelimiterSpecification> delimiters = new LinkedHashSet<DelimiterSpecification>();
  
  private DelimiterSpecification currentSpec;
  
  private String beginToken;
  
  private String originalBeginToken;
  
  private String endToken;
  
  public MultiDelimiterInterpolatorFilterReader(Reader in, Interpolator interpolator) {
    this(in, interpolator, (RecursionInterceptor)new SimpleRecursionInterceptor());
  }
  
  public MultiDelimiterInterpolatorFilterReader(Reader in, Interpolator interpolator, RecursionInterceptor ri) {
    super(in);
    this.interpolator = interpolator;
    this.interpolator.setCacheAnswers(true);
    this.recursionInterceptor = ri;
    this.delimiters.add(DelimiterSpecification.DEFAULT_SPEC);
  }
  
  public MultiDelimiterInterpolatorFilterReader addDelimiterSpec(String delimiterSpec) {
    if (delimiterSpec == null)
      return this; 
    this.delimiters.add(DelimiterSpecification.parse(delimiterSpec));
    return this;
  }
  
  public boolean removeDelimiterSpec(String delimiterSpec) {
    if (delimiterSpec == null)
      return false; 
    return this.delimiters.remove(DelimiterSpecification.parse(delimiterSpec));
  }
  
  public MultiDelimiterInterpolatorFilterReader setDelimiterSpecs(LinkedHashSet<String> specs) {
    this.delimiters.clear();
    for (String spec : specs) {
      if (spec == null)
        continue; 
      this.delimiters.add(DelimiterSpecification.parse(spec));
    } 
    return this;
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
    boolean inEscape = false;
    if ((inEscape = (this.useEscape && ch == this.escapeString.charAt(0))) || reselectDelimiterSpec(ch)) {
      StringBuilder key = new StringBuilder();
      key.append((char)ch);
      boolean atEnd = false;
      if (inEscape) {
        for (int i = 0; i < this.escapeString.length() - 1; i++) {
          ch = this.in.read();
          if (ch == -1) {
            atEnd = true;
            break;
          } 
          key.append((char)ch);
        } 
        if (!atEnd) {
          ch = this.in.read();
          if (!reselectDelimiterSpec(ch)) {
            this.replaceData = key.toString();
            this.replaceIndex = 1;
            return this.replaceData.charAt(0);
          } 
          key.append((char)ch);
        } 
      } 
      int beginTokenMatchPos = 1;
      while (!atEnd) {
        if (this.previousIndex != -1 && this.previousIndex < this.endToken.length()) {
          ch = this.endToken.charAt(this.previousIndex++);
        } else {
          ch = this.in.read();
        } 
        if (ch != -1) {
          key.append((char)ch);
          if (beginTokenMatchPos < this.originalBeginToken.length() && ch != this.originalBeginToken
            .charAt(beginTokenMatchPos)) {
            ch = -1;
            break;
          } 
          beginTokenMatchPos++;
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
          if (key.toString().startsWith(this.beginToken)) {
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
        error.initCause((Throwable)e);
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
  
  private boolean reselectDelimiterSpec(int ch) {
    for (DelimiterSpecification spec : this.delimiters) {
      if (ch == spec.getBegin().charAt(0)) {
        this.currentSpec = spec;
        this.originalBeginToken = this.currentSpec.getBegin();
        this.beginToken = this.useEscape ? (this.escapeString + this.originalBeginToken) : this.originalBeginToken;
        this.endToken = this.currentSpec.getEnd();
        return true;
      } 
    } 
    return false;
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
  
  public MultiDelimiterInterpolatorFilterReader setRecursionInterceptor(RecursionInterceptor recursionInterceptor) {
    this.recursionInterceptor = recursionInterceptor;
    return this;
  }
}
