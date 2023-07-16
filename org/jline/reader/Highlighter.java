package org.jline.reader;

import java.util.regex.Pattern;
import org.jline.utils.AttributedString;

public interface Highlighter {
  AttributedString highlight(LineReader paramLineReader, String paramString);
  
  void setErrorPattern(Pattern paramPattern);
  
  void setErrorIndex(int paramInt);
}
