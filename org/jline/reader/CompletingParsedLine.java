package org.jline.reader;

public interface CompletingParsedLine extends ParsedLine {
  CharSequence escape(CharSequence paramCharSequence, boolean paramBoolean);
  
  int rawWordCursor();
  
  int rawWordLength();
}
