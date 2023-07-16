package org.jline.utils;

import java.util.ArrayList;
import java.util.List;
import org.jline.terminal.Terminal;

public abstract class AttributedCharSequence implements CharSequence {
  public static final int TRUE_COLORS = 16777216;
  
  private static final int HIGH_COLORS = 32767;
  
  public enum ForceMode {
    None, Force256Colors, ForceTrueColors;
  }
  
  static final boolean DISABLE_ALTERNATE_CHARSET = Boolean.getBoolean("org.jline.utils.disableAlternateCharset");
  
  public void print(Terminal terminal) {
    terminal.writer().print(toAnsi(terminal));
  }
  
  public void println(Terminal terminal) {
    terminal.writer().println(toAnsi(terminal));
  }
  
  public String toAnsi() {
    return toAnsi(null);
  }
  
  public String toAnsi(Terminal terminal) {
    if (terminal != null && "dumb".equals(terminal.getType()))
      return toString(); 
    int colors = 256;
    ForceMode forceMode = ForceMode.None;
    ColorPalette palette = null;
    String alternateIn = null, alternateOut = null;
    if (terminal != null) {
      Integer max_colors = terminal.getNumericCapability(InfoCmp.Capability.max_colors);
      if (max_colors != null)
        colors = max_colors.intValue(); 
      if ("windows-256color".equals(terminal.getType()) || "windows-conemu"
        .equals(terminal.getType()))
        forceMode = ForceMode.Force256Colors; 
      palette = terminal.getPalette();
      if (!DISABLE_ALTERNATE_CHARSET) {
        alternateIn = Curses.tputs(terminal.getStringCapability(InfoCmp.Capability.enter_alt_charset_mode), new Object[0]);
        alternateOut = Curses.tputs(terminal.getStringCapability(InfoCmp.Capability.exit_alt_charset_mode), new Object[0]);
      } 
    } 
    return toAnsi(colors, forceMode, palette, alternateIn, alternateOut);
  }
  
  @Deprecated
  public String toAnsi(int colors, boolean force256colors) {
    return toAnsi(colors, force256colors, null, null);
  }
  
  @Deprecated
  public String toAnsi(int colors, boolean force256colors, String altIn, String altOut) {
    return toAnsi(colors, force256colors ? ForceMode.Force256Colors : ForceMode.None, null, altIn, altOut);
  }
  
  public String toAnsi(int colors, ForceMode force) {
    return toAnsi(colors, force, null, null, null);
  }
  
  public String toAnsi(int colors, ForceMode force, ColorPalette palette) {
    return toAnsi(colors, force, palette, null, null);
  }
  
  public String toAnsi(int colors, ForceMode force, ColorPalette palette, String altIn, String altOut) {
    StringBuilder sb = new StringBuilder();
    long style = 0L;
    long foreground = 0L;
    long background = 0L;
    boolean alt = false;
    if (palette == null)
      palette = ColorPalette.DEFAULT; 
    for (int i = 0; i < length(); i++) {
      char c = charAt(i);
      if (altIn != null && altOut != null) {
        char pc = c;
        switch (c) {
          case '┘':
            c = 'j';
            break;
          case '┐':
            c = 'k';
            break;
          case '┌':
            c = 'l';
            break;
          case '└':
            c = 'm';
            break;
          case '┼':
            c = 'n';
            break;
          case '─':
            c = 'q';
            break;
          case '├':
            c = 't';
            break;
          case '┤':
            c = 'u';
            break;
          case '┴':
            c = 'v';
            break;
          case '┬':
            c = 'w';
            break;
          case '│':
            c = 'x';
            break;
        } 
        boolean oldalt = alt;
        alt = (c != pc);
        if (oldalt ^ alt)
          sb.append(alt ? altIn : altOut); 
      } 
      long s = styleCodeAt(i) & 0xFFFFFFFFFFFFEFFFL;
      if (style != s) {
        long d = (style ^ s) & 0x1FFFL;
        long fg = ((s & 0x300L) != 0L) ? (s & 0x7FFFFF8300L) : 0L;
        long bg = ((s & 0xC00L) != 0L) ? (s & 0x7FFFFF8000000C00L) : 0L;
        sb.append("\033[0m");
        foreground = background = 0L;
        sb.append("\033[");
        boolean first = true;
        if ((d & 0x4L) != 0L)
          first = attr(sb, ((s & 0x4L) != 0L) ? "3" : "23", first); 
        if ((d & 0x8L) != 0L)
          first = attr(sb, ((s & 0x8L) != 0L) ? "4" : "24", first); 
        if ((d & 0x10L) != 0L)
          first = attr(sb, ((s & 0x10L) != 0L) ? "5" : "25", first); 
        if ((d & 0x20L) != 0L)
          first = attr(sb, ((s & 0x20L) != 0L) ? "7" : "27", first); 
        if ((d & 0x40L) != 0L)
          first = attr(sb, ((s & 0x40L) != 0L) ? "8" : "28", first); 
        if ((d & 0x80L) != 0L)
          first = attr(sb, ((s & 0x80L) != 0L) ? "9" : "29", first); 
        if (foreground != fg) {
          if (fg > 0L) {
            int rounded = -1;
            if ((fg & 0x200L) != 0L) {
              int r = (int)(fg >> 31L) & 0xFF;
              int g = (int)(fg >> 23L) & 0xFF;
              int b = (int)(fg >> 15L) & 0xFF;
              if (colors >= 32767) {
                first = attr(sb, "38;2;" + r + ";" + g + ";" + b, first);
              } else {
                rounded = palette.round(r, g, b);
              } 
            } else if ((fg & 0x100L) != 0L) {
              rounded = palette.round((int)(fg >> 15L) & 0xFF);
            } 
            if (rounded >= 0)
              if (colors >= 32767 && force == ForceMode.ForceTrueColors) {
                int col = palette.getColor(rounded);
                int r = col >> 16 & 0xFF;
                int g = col >> 8 & 0xFF;
                int b = col & 0xFF;
                first = attr(sb, "38;2;" + r + ";" + g + ";" + b, first);
              } else if (force == ForceMode.Force256Colors || rounded >= 16) {
                first = attr(sb, "38;5;" + rounded, first);
              } else if (rounded >= 8) {
                first = attr(sb, "9" + (rounded - 8), first);
                d |= s & 0x1L;
              } else {
                first = attr(sb, "3" + rounded, first);
                d |= s & 0x1L;
              }  
          } else {
            first = attr(sb, "39", first);
          } 
          foreground = fg;
        } 
        if (background != bg) {
          if (bg > 0L) {
            int rounded = -1;
            if ((bg & 0x800L) != 0L) {
              int r = (int)(bg >> 55L) & 0xFF;
              int g = (int)(bg >> 47L) & 0xFF;
              int b = (int)(bg >> 39L) & 0xFF;
              if (colors >= 32767) {
                first = attr(sb, "48;2;" + r + ";" + g + ";" + b, first);
              } else {
                rounded = palette.round(r, g, b);
              } 
            } else if ((bg & 0x400L) != 0L) {
              rounded = palette.round((int)(bg >> 39L) & 0xFF);
            } 
            if (rounded >= 0)
              if (colors >= 32767 && force == ForceMode.ForceTrueColors) {
                int col = palette.getColor(rounded);
                int r = col >> 16 & 0xFF;
                int g = col >> 8 & 0xFF;
                int b = col & 0xFF;
                first = attr(sb, "48;2;" + r + ";" + g + ";" + b, first);
              } else if (force == ForceMode.Force256Colors || rounded >= 16) {
                first = attr(sb, "48;5;" + rounded, first);
              } else if (rounded >= 8) {
                first = attr(sb, "10" + (rounded - 8), first);
              } else {
                first = attr(sb, "4" + rounded, first);
              }  
          } else {
            first = attr(sb, "49", first);
          } 
          background = bg;
        } 
        if ((d & 0x3L) != 0L) {
          if (((d & 0x1L) != 0L && (s & 0x1L) == 0L) || ((d & 0x2L) != 0L && (s & 0x2L) == 0L))
            first = attr(sb, "22", first); 
          if ((d & 0x1L) != 0L && (s & 0x1L) != 0L)
            first = attr(sb, "1", first); 
          if ((d & 0x2L) != 0L && (s & 0x2L) != 0L)
            first = attr(sb, "2", first); 
        } 
        sb.append("m");
        style = s;
      } 
      sb.append(c);
    } 
    if (alt)
      sb.append(altOut); 
    if (style != 0L)
      sb.append("\033[0m"); 
    return sb.toString();
  }
  
  @Deprecated
  public static int rgbColor(int col) {
    return Colors.rgbColor(col);
  }
  
  @Deprecated
  public static int roundColor(int col, int max) {
    return Colors.roundColor(col, max);
  }
  
  @Deprecated
  public static int roundRgbColor(int r, int g, int b, int max) {
    return Colors.roundRgbColor(r, g, b, max);
  }
  
  private static boolean attr(StringBuilder sb, String s, boolean first) {
    if (!first)
      sb.append(";"); 
    sb.append(s);
    return false;
  }
  
  long styleCodeAt(int index) {
    return styleAt(index).getStyle();
  }
  
  public boolean isHidden(int index) {
    return ((styleCodeAt(index) & 0x1000L) != 0L);
  }
  
  public int runStart(int index) {
    AttributedStyle style = styleAt(index);
    while (index > 0 && styleAt(index - 1).equals(style))
      index--; 
    return index;
  }
  
  public int runLimit(int index) {
    AttributedStyle style = styleAt(index);
    while (index < length() - 1 && styleAt(index + 1).equals(style))
      index++; 
    return index + 1;
  }
  
  public AttributedString substring(int start, int end) {
    return subSequence(start, end);
  }
  
  public char charAt(int index) {
    return buffer()[offset() + index];
  }
  
  public int codePointAt(int index) {
    return Character.codePointAt(buffer(), index + offset());
  }
  
  public boolean contains(char c) {
    for (int i = 0; i < length(); i++) {
      if (charAt(i) == c)
        return true; 
    } 
    return false;
  }
  
  public int codePointBefore(int index) {
    return Character.codePointBefore(buffer(), index + offset());
  }
  
  public int codePointCount(int index, int length) {
    return Character.codePointCount(buffer(), index + offset(), length);
  }
  
  public int columnLength() {
    int cols = 0;
    int len = length();
    for (int cur = 0; cur < len; ) {
      int cp = codePointAt(cur);
      if (!isHidden(cur))
        cols += WCWidth.wcwidth(cp); 
      cur += Character.charCount(cp);
    } 
    return cols;
  }
  
  public AttributedString columnSubSequence(int start, int stop) {
    int begin = 0;
    int col = 0;
    while (begin < length()) {
      int cp = codePointAt(begin);
      int w = isHidden(begin) ? 0 : WCWidth.wcwidth(cp);
      if (col + w > start)
        break; 
      begin += Character.charCount(cp);
      col += w;
    } 
    int end = begin;
    while (end < length()) {
      int cp = codePointAt(end);
      if (cp == 10)
        break; 
      int w = isHidden(end) ? 0 : WCWidth.wcwidth(cp);
      if (col + w > stop)
        break; 
      end += Character.charCount(cp);
      col += w;
    } 
    return subSequence(begin, end);
  }
  
  public List<AttributedString> columnSplitLength(int columns) {
    return columnSplitLength(columns, false, true);
  }
  
  public List<AttributedString> columnSplitLength(int columns, boolean includeNewlines, boolean delayLineWrap) {
    List<AttributedString> strings = new ArrayList<>();
    int cur = 0;
    int beg = cur;
    int col = 0;
    while (cur < length()) {
      int cp = codePointAt(cur);
      int w = isHidden(cur) ? 0 : WCWidth.wcwidth(cp);
      if (cp == 10) {
        strings.add(subSequence(beg, includeNewlines ? (cur + 1) : cur));
        beg = cur + 1;
        col = 0;
      } else if ((col += w) > columns) {
        strings.add(subSequence(beg, cur));
        beg = cur;
        col = w;
      } 
      cur += Character.charCount(cp);
    } 
    strings.add(subSequence(beg, cur));
    return strings;
  }
  
  public String toString() {
    return new String(buffer(), offset(), length());
  }
  
  public AttributedString toAttributedString() {
    return substring(0, length());
  }
  
  public abstract AttributedStyle styleAt(int paramInt);
  
  public abstract AttributedString subSequence(int paramInt1, int paramInt2);
  
  protected abstract char[] buffer();
  
  protected abstract int offset();
}
