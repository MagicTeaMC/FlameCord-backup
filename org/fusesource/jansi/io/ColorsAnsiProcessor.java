package org.fusesource.jansi.io;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Iterator;
import org.fusesource.jansi.AnsiColors;

public class ColorsAnsiProcessor extends AnsiProcessor {
  private final AnsiColors colors;
  
  public ColorsAnsiProcessor(OutputStream os, AnsiColors colors) {
    super(os);
    this.colors = colors;
  }
  
  protected boolean processEscapeCommand(ArrayList<Object> options, int command) throws IOException {
    if (command == 109 && (this.colors == AnsiColors.Colors256 || this.colors == AnsiColors.Colors16)) {
      int i;
      boolean has38or48 = false;
      for (Object next : options) {
        if (next != null && next.getClass() != Integer.class)
          throw new IllegalArgumentException(); 
        Integer value = (Integer)next;
        i = has38or48 | ((value.intValue() == 38 || value.intValue() == 48) ? 1 : 0);
      } 
      if (i == 0)
        return false; 
      StringBuilder sb = new StringBuilder(32);
      sb.append('\033').append('[');
      boolean first = true;
      Iterator<Object> optionsIterator = options.iterator();
      while (optionsIterator.hasNext()) {
        Object next = optionsIterator.next();
        if (next != null) {
          int value = ((Integer)next).intValue();
          if (value == 38 || value == 48) {
            int arg2or5 = getNextOptionInt(optionsIterator);
            if (arg2or5 == 2) {
              int r = getNextOptionInt(optionsIterator);
              int g = getNextOptionInt(optionsIterator);
              int b = getNextOptionInt(optionsIterator);
              if (this.colors == AnsiColors.Colors256) {
                int j = Colors.roundRgbColor(r, g, b, 256);
                if (!first)
                  sb.append(';'); 
                first = false;
                sb.append(value);
                sb.append(';');
                sb.append(5);
                sb.append(';');
                sb.append(j);
                continue;
              } 
              int col = Colors.roundRgbColor(r, g, b, 16);
              if (!first)
                sb.append(';'); 
              first = false;
              sb.append((value == 38) ? ((col >= 8) ? (90 + col - 8) : (30 + col)) : ((col >= 8) ? (100 + col - 8) : (40 + col)));
              continue;
            } 
            if (arg2or5 == 5) {
              int paletteIndex = getNextOptionInt(optionsIterator);
              if (this.colors == AnsiColors.Colors256) {
                if (!first)
                  sb.append(';'); 
                first = false;
                sb.append(value);
                sb.append(';');
                sb.append(5);
                sb.append(';');
                sb.append(paletteIndex);
                continue;
              } 
              int col = Colors.roundColor(paletteIndex, 16);
              if (!first)
                sb.append(';'); 
              first = false;
              sb.append((value == 38) ? ((col >= 8) ? (90 + col - 8) : (30 + col)) : ((col >= 8) ? (100 + col - 8) : (40 + col)));
              continue;
            } 
            throw new IllegalArgumentException();
          } 
          if (!first)
            sb.append(';'); 
          first = false;
          sb.append(value);
        } 
      } 
      sb.append('m');
      this.os.write(sb.toString().getBytes());
      return true;
    } 
    return false;
  }
  
  protected boolean processOperatingSystemCommand(ArrayList<Object> options) {
    return false;
  }
  
  protected boolean processCharsetSelect(ArrayList<Object> options) {
    return false;
  }
}
