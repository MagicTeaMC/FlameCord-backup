package org.codehaus.plexus.util.cli;

import java.util.StringTokenizer;

public final class EnhancedStringTokenizer {
  private StringTokenizer cst = null;
  
  String cdelim;
  
  final boolean cdelimSingleChar;
  
  final char cdelimChar;
  
  boolean creturnDelims;
  
  String lastToken = null;
  
  boolean delimLast = true;
  
  public EnhancedStringTokenizer(String str) {
    this(str, " \t\n\r\f", false);
  }
  
  public EnhancedStringTokenizer(String str, String delim) {
    this(str, delim, false);
  }
  
  public EnhancedStringTokenizer(String str, String delim, boolean returnDelims) {
    this.cst = new StringTokenizer(str, delim, true);
    this.cdelim = delim;
    this.creturnDelims = returnDelims;
    this.cdelimSingleChar = (delim.length() == 1);
    this.cdelimChar = delim.charAt(0);
  }
  
  public boolean hasMoreTokens() {
    return this.cst.hasMoreTokens();
  }
  
  private String internalNextToken() {
    if (this.lastToken != null) {
      String last = this.lastToken;
      this.lastToken = null;
      return last;
    } 
    String token = this.cst.nextToken();
    if (isDelim(token)) {
      if (this.delimLast) {
        this.lastToken = token;
        return "";
      } 
      this.delimLast = true;
      return token;
    } 
    this.delimLast = false;
    return token;
  }
  
  public String nextToken() {
    String token = internalNextToken();
    if (this.creturnDelims)
      return token; 
    if (isDelim(token))
      return hasMoreTokens() ? internalNextToken() : ""; 
    return token;
  }
  
  private boolean isDelim(String str) {
    if (str.length() == 1) {
      char ch = str.charAt(0);
      if (this.cdelimSingleChar) {
        if (this.cdelimChar == ch)
          return true; 
      } else if (this.cdelim.indexOf(ch) >= 0) {
        return true;
      } 
    } 
    return false;
  }
}
