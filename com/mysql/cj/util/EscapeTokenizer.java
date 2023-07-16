package com.mysql.cj.util;

public class EscapeTokenizer {
  private static final char CHR_ESCAPE = '\\';
  
  private static final char CHR_SGL_QUOTE = '\'';
  
  private static final char CHR_DBL_QUOTE = '"';
  
  private static final char CHR_LF = '\n';
  
  private static final char CHR_CR = '\r';
  
  private static final char CHR_COMMENT = '-';
  
  private static final char CHR_BEGIN_TOKEN = '{';
  
  private static final char CHR_END_TOKEN = '}';
  
  private static final char CHR_VARIABLE = '@';
  
  private String source = null;
  
  private int sourceLength = 0;
  
  private int pos = 0;
  
  private boolean emittingEscapeCode = false;
  
  private boolean sawVariableUse = false;
  
  private int bracesLevel = 0;
  
  private boolean inQuotes = false;
  
  private char quoteChar = Character.MIN_VALUE;
  
  public EscapeTokenizer(String source) {
    this.source = source;
    this.sourceLength = source.length();
    this.pos = 0;
  }
  
  public synchronized boolean hasMoreTokens() {
    return (this.pos < this.sourceLength);
  }
  
  public synchronized String nextToken() {
    StringBuilder tokenBuf = new StringBuilder();
    boolean backslashEscape = false;
    if (this.emittingEscapeCode) {
      tokenBuf.append("{");
      this.emittingEscapeCode = false;
    } 
    while (true) {
      char c;
      if (this.pos < this.sourceLength) {
        c = this.source.charAt(this.pos);
        if (c == '\\') {
          tokenBuf.append(c);
          backslashEscape = !backslashEscape;
          continue;
        } 
        if ((c == '\'' || c == '"') && !backslashEscape) {
          tokenBuf.append(c);
          if (this.inQuotes) {
            if (c == this.quoteChar)
              if (this.pos + 1 < this.sourceLength && this.source.charAt(this.pos + 1) == this.quoteChar) {
                tokenBuf.append(c);
                this.pos++;
              } else {
                this.inQuotes = false;
              }  
          } else {
            this.inQuotes = true;
            this.quoteChar = c;
          } 
          continue;
        } 
        if (c == '\n' || c == '\r') {
          tokenBuf.append(c);
          backslashEscape = false;
          continue;
        } 
        if (!this.inQuotes && !backslashEscape) {
          if (c == '-') {
            tokenBuf.append(c);
            if (this.pos + 1 < this.sourceLength && this.source.charAt(this.pos + 1) == '-') {
              while (++this.pos < this.sourceLength && c != '\n' && c != '\r') {
                c = this.source.charAt(this.pos);
                tokenBuf.append(c);
              } 
              this.pos--;
            } 
          } else if (c == '{') {
            this.bracesLevel++;
            if (this.bracesLevel == 1) {
              this.emittingEscapeCode = true;
              this.pos++;
              return tokenBuf.toString();
            } 
            tokenBuf.append(c);
          } else if (c == '}') {
            tokenBuf.append(c);
            this.bracesLevel--;
            if (this.bracesLevel == 0) {
              this.pos++;
              return tokenBuf.toString();
            } 
          } else {
            if (c == '@')
              this.sawVariableUse = true; 
            tokenBuf.append(c);
            backslashEscape = false;
          } 
          continue;
        } 
      } else {
        break;
      } 
      tokenBuf.append(c);
      backslashEscape = false;
      this.pos++;
    } 
    return tokenBuf.toString();
  }
  
  public boolean sawVariableUse() {
    return this.sawVariableUse;
  }
}
