package org.codehaus.plexus.util;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

public class MatchPattern {
  private final String source;
  
  private final String regexPattern;
  
  private final String separator;
  
  private final String[] tokenized;
  
  private final char[][] tokenizedChar;
  
  private MatchPattern(String source, String separator) {
    this.regexPattern = SelectorUtils.isRegexPrefixedPattern(source) ? source.substring("%regex[".length(), source.length() - "]".length()) : null;
    this.source = SelectorUtils.isAntPrefixedPattern(source) ? source.substring("%ant[".length(), source.length() - "]".length()) : source;
    this.separator = separator;
    this.tokenized = tokenizePathToString(this.source, separator);
    this.tokenizedChar = new char[this.tokenized.length][];
    for (int i = 0; i < this.tokenized.length; i++)
      this.tokenizedChar[i] = this.tokenized[i].toCharArray(); 
  }
  
  public boolean matchPath(String str, boolean isCaseSensitive) {
    if (this.regexPattern != null)
      return str.matches(this.regexPattern); 
    return SelectorUtils.matchAntPathPattern(this, str, this.separator, isCaseSensitive);
  }
  
  boolean matchPath(String str, char[][] strDirs, boolean isCaseSensitive) {
    if (this.regexPattern != null)
      return str.matches(this.regexPattern); 
    return SelectorUtils.matchAntPathPattern(getTokenizedPathChars(), strDirs, isCaseSensitive);
  }
  
  public boolean matchPatternStart(String str, boolean isCaseSensitive) {
    if (this.regexPattern != null)
      return true; 
    String altStr = str.replace('\\', '/');
    return (SelectorUtils.matchAntPathPatternStart(this, str, File.separator, isCaseSensitive) || SelectorUtils.matchAntPathPatternStart(this, altStr, "/", isCaseSensitive));
  }
  
  public String[] getTokenizedPathString() {
    return this.tokenized;
  }
  
  public char[][] getTokenizedPathChars() {
    return this.tokenizedChar;
  }
  
  public boolean startsWith(String string) {
    return this.source.startsWith(string);
  }
  
  static String[] tokenizePathToString(String path, String separator) {
    List<String> ret = new ArrayList<String>();
    StringTokenizer st = new StringTokenizer(path, separator);
    while (st.hasMoreTokens())
      ret.add(st.nextToken()); 
    return ret.<String>toArray(new String[ret.size()]);
  }
  
  public static MatchPattern fromString(String source) {
    return new MatchPattern(source, File.separator);
  }
}
