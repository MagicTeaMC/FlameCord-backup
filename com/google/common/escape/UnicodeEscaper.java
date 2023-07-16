package com.google.common.escape;

import com.google.common.annotations.GwtCompatible;
import com.google.common.base.Preconditions;
import javax.annotation.CheckForNull;

@ElementTypesAreNonnullByDefault
@GwtCompatible
public abstract class UnicodeEscaper extends Escaper {
  private static final int DEST_PAD = 32;
  
  @CheckForNull
  protected abstract char[] escape(int paramInt);
  
  public String escape(String string) {
    Preconditions.checkNotNull(string);
    int end = string.length();
    int index = nextEscapeIndex(string, 0, end);
    return (index == end) ? string : escapeSlow(string, index);
  }
  
  protected int nextEscapeIndex(CharSequence csq, int start, int end) {
    int index = start;
    while (index < end) {
      int cp = codePointAt(csq, index, end);
      if (cp < 0 || escape(cp) != null)
        break; 
      index += Character.isSupplementaryCodePoint(cp) ? 2 : 1;
    } 
    return index;
  }
  
  protected final String escapeSlow(String s, int index) {
    int end = s.length();
    char[] dest = Platform.charBufferFromThreadLocal();
    int destIndex = 0;
    int unescapedChunkStart = 0;
    while (index < end) {
      int cp = codePointAt(s, index, end);
      if (cp < 0)
        throw new IllegalArgumentException("Trailing high surrogate at end of input"); 
      char[] escaped = escape(cp);
      int nextIndex = index + (Character.isSupplementaryCodePoint(cp) ? 2 : 1);
      if (escaped != null) {
        int i = index - unescapedChunkStart;
        int sizeNeeded = destIndex + i + escaped.length;
        if (dest.length < sizeNeeded) {
          int destLength = sizeNeeded + end - index + 32;
          dest = growBuffer(dest, destIndex, destLength);
        } 
        if (i > 0) {
          s.getChars(unescapedChunkStart, index, dest, destIndex);
          destIndex += i;
        } 
        if (escaped.length > 0) {
          System.arraycopy(escaped, 0, dest, destIndex, escaped.length);
          destIndex += escaped.length;
        } 
        unescapedChunkStart = nextIndex;
      } 
      index = nextEscapeIndex(s, nextIndex, end);
    } 
    int charsSkipped = end - unescapedChunkStart;
    if (charsSkipped > 0) {
      int endIndex = destIndex + charsSkipped;
      if (dest.length < endIndex)
        dest = growBuffer(dest, destIndex, endIndex); 
      s.getChars(unescapedChunkStart, end, dest, destIndex);
      destIndex = endIndex;
    } 
    return new String(dest, 0, destIndex);
  }
  
  protected static int codePointAt(CharSequence seq, int index, int end) {
    Preconditions.checkNotNull(seq);
    if (index < end) {
      char c1 = seq.charAt(index++);
      if (c1 < '?' || c1 > '?')
        return c1; 
      if (c1 <= '?') {
        if (index == end)
          return -c1; 
        char c2 = seq.charAt(index);
        if (Character.isLowSurrogate(c2))
          return Character.toCodePoint(c1, c2); 
        char c3 = c2;
        int j = index;
        String str1 = String.valueOf(seq);
        throw new IllegalArgumentException((new StringBuilder(89 + String.valueOf(str1).length())).append("Expected low surrogate but got char '").append(c2).append("' with value ").append(c3).append(" at index ").append(j).append(" in '").append(str1).append("'").toString());
      } 
      char c = c1;
      int i = index - 1;
      String str = String.valueOf(seq);
      throw new IllegalArgumentException((new StringBuilder(88 + String.valueOf(str).length())).append("Unexpected low surrogate character '").append(c1).append("' with value ").append(c).append(" at index ").append(i).append(" in '").append(str).append("'").toString());
    } 
    throw new IndexOutOfBoundsException("Index exceeds specified range");
  }
  
  private static char[] growBuffer(char[] dest, int index, int size) {
    if (size < 0)
      throw new AssertionError("Cannot increase internal buffer any further"); 
    char[] copy = new char[size];
    if (index > 0)
      System.arraycopy(dest, 0, copy, 0, index); 
    return copy;
  }
}
