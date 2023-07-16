package org.apache.logging.log4j.core.pattern;

import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

class DynamicWordAbbreviator extends NameAbbreviator {
  private final int rightWordCount;
  
  static DynamicWordAbbreviator create(String pattern) {
    if (pattern != null) {
      Matcher matcher = Pattern.compile("1\\.([1-9][0-9]*)\\*").matcher(pattern);
      if (matcher.matches())
        return new DynamicWordAbbreviator(Integer.parseInt(matcher.group(1))); 
    } 
    return null;
  }
  
  private DynamicWordAbbreviator(int rightWordCount) {
    this.rightWordCount = rightWordCount;
  }
  
  public void abbreviate(String original, StringBuilder destination) {
    if (original == null || destination == null)
      return; 
    String[] words = split(original, '.');
    int wordCount = words.length;
    if (this.rightWordCount >= wordCount) {
      destination.append(original);
      return;
    } 
    int lastAbbrevIdx = wordCount - this.rightWordCount;
    for (int i = 0; i < wordCount; i++) {
      if (i >= lastAbbrevIdx) {
        destination.append(words[i]);
        if (i < wordCount - 1)
          destination.append("."); 
      } else if (words[i].length() > 0) {
        destination.append(words[i].charAt(0))
          .append(".");
      } 
    } 
  }
  
  static String[] split(String input, char delim) {
    if (input == null)
      return null; 
    if (input.isEmpty())
      return new String[0]; 
    int countDelim = input.chars().filter(c -> (c == delim)).map(c -> 1).sum();
    String[] tokens = new String[countDelim + 1];
    int countToken = 0;
    int idxBegin = 0;
    int idxDelim = 0;
    while ((idxDelim = input.indexOf(delim, idxBegin)) > -1) {
      if (idxBegin < idxDelim)
        tokens[countToken++] = input.substring(idxBegin, idxDelim); 
      idxBegin = idxDelim + 1;
    } 
    if (idxBegin < input.length())
      tokens[countToken++] = input.substring(idxBegin); 
    if (countToken < tokens.length)
      return Arrays.<String>copyOf(tokens, countToken); 
    return tokens;
  }
}
