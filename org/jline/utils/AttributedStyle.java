package org.jline.utils;

public class AttributedStyle {
  public static final int BLACK = 0;
  
  public static final int RED = 1;
  
  public static final int GREEN = 2;
  
  public static final int YELLOW = 3;
  
  public static final int BLUE = 4;
  
  public static final int MAGENTA = 5;
  
  public static final int CYAN = 6;
  
  public static final int WHITE = 7;
  
  public static final int BRIGHT = 8;
  
  static final long F_BOLD = 1L;
  
  static final long F_FAINT = 2L;
  
  static final long F_ITALIC = 4L;
  
  static final long F_UNDERLINE = 8L;
  
  static final long F_BLINK = 16L;
  
  static final long F_INVERSE = 32L;
  
  static final long F_CONCEAL = 64L;
  
  static final long F_CROSSED_OUT = 128L;
  
  static final long F_FOREGROUND_IND = 256L;
  
  static final long F_FOREGROUND_RGB = 512L;
  
  static final long F_FOREGROUND = 768L;
  
  static final long F_BACKGROUND_IND = 1024L;
  
  static final long F_BACKGROUND_RGB = 2048L;
  
  static final long F_BACKGROUND = 3072L;
  
  static final long F_HIDDEN = 4096L;
  
  static final long MASK = 8191L;
  
  static final int FG_COLOR_EXP = 15;
  
  static final int BG_COLOR_EXP = 39;
  
  static final long FG_COLOR = 549755781120L;
  
  static final long BG_COLOR = 9223371487098961920L;
  
  public static final AttributedStyle DEFAULT = new AttributedStyle();
  
  public static final AttributedStyle BOLD = DEFAULT.bold();
  
  public static final AttributedStyle BOLD_OFF = DEFAULT.boldOff();
  
  public static final AttributedStyle INVERSE = DEFAULT.inverse();
  
  public static final AttributedStyle INVERSE_OFF = DEFAULT.inverseOff();
  
  public static final AttributedStyle HIDDEN = DEFAULT.hidden();
  
  public static final AttributedStyle HIDDEN_OFF = DEFAULT.hiddenOff();
  
  final long style;
  
  final long mask;
  
  public AttributedStyle() {
    this(0L, 0L);
  }
  
  public AttributedStyle(AttributedStyle s) {
    this(s.style, s.mask);
  }
  
  public AttributedStyle(long style, long mask) {
    this.style = style;
    this
      .mask = mask & 0x1FFFL | (((style & 0x300L) != 0L) ? 549755781120L : 0L) | (((style & 0xC00L) != 0L) ? 9223371487098961920L : 0L);
  }
  
  public AttributedStyle bold() {
    return new AttributedStyle(this.style | 0x1L, this.mask | 0x1L);
  }
  
  public AttributedStyle boldOff() {
    return new AttributedStyle(this.style & 0xFFFFFFFFFFFFFFFEL, this.mask | 0x1L);
  }
  
  public AttributedStyle boldDefault() {
    return new AttributedStyle(this.style & 0xFFFFFFFFFFFFFFFEL, this.mask & 0xFFFFFFFFFFFFFFFEL);
  }
  
  public AttributedStyle faint() {
    return new AttributedStyle(this.style | 0x2L, this.mask | 0x2L);
  }
  
  public AttributedStyle faintOff() {
    return new AttributedStyle(this.style & 0xFFFFFFFFFFFFFFFDL, this.mask | 0x2L);
  }
  
  public AttributedStyle faintDefault() {
    return new AttributedStyle(this.style & 0xFFFFFFFFFFFFFFFDL, this.mask & 0xFFFFFFFFFFFFFFFDL);
  }
  
  public AttributedStyle italic() {
    return new AttributedStyle(this.style | 0x4L, this.mask | 0x4L);
  }
  
  public AttributedStyle italicOff() {
    return new AttributedStyle(this.style & 0xFFFFFFFFFFFFFFFBL, this.mask | 0x4L);
  }
  
  public AttributedStyle italicDefault() {
    return new AttributedStyle(this.style & 0xFFFFFFFFFFFFFFFBL, this.mask & 0xFFFFFFFFFFFFFFFBL);
  }
  
  public AttributedStyle underline() {
    return new AttributedStyle(this.style | 0x8L, this.mask | 0x8L);
  }
  
  public AttributedStyle underlineOff() {
    return new AttributedStyle(this.style & 0xFFFFFFFFFFFFFFF7L, this.mask | 0x8L);
  }
  
  public AttributedStyle underlineDefault() {
    return new AttributedStyle(this.style & 0xFFFFFFFFFFFFFFF7L, this.mask & 0xFFFFFFFFFFFFFFF7L);
  }
  
  public AttributedStyle blink() {
    return new AttributedStyle(this.style | 0x10L, this.mask | 0x10L);
  }
  
  public AttributedStyle blinkOff() {
    return new AttributedStyle(this.style & 0xFFFFFFFFFFFFFFEFL, this.mask | 0x10L);
  }
  
  public AttributedStyle blinkDefault() {
    return new AttributedStyle(this.style & 0xFFFFFFFFFFFFFFEFL, this.mask & 0xFFFFFFFFFFFFFFEFL);
  }
  
  public AttributedStyle inverse() {
    return new AttributedStyle(this.style | 0x20L, this.mask | 0x20L);
  }
  
  public AttributedStyle inverseNeg() {
    long s = ((this.style & 0x20L) != 0L) ? (this.style & 0xFFFFFFFFFFFFFFDFL) : (this.style | 0x20L);
    return new AttributedStyle(s, this.mask | 0x20L);
  }
  
  public AttributedStyle inverseOff() {
    return new AttributedStyle(this.style & 0xFFFFFFFFFFFFFFDFL, this.mask | 0x20L);
  }
  
  public AttributedStyle inverseDefault() {
    return new AttributedStyle(this.style & 0xFFFFFFFFFFFFFFDFL, this.mask & 0xFFFFFFFFFFFFFFDFL);
  }
  
  public AttributedStyle conceal() {
    return new AttributedStyle(this.style | 0x40L, this.mask | 0x40L);
  }
  
  public AttributedStyle concealOff() {
    return new AttributedStyle(this.style & 0xFFFFFFFFFFFFFFBFL, this.mask | 0x40L);
  }
  
  public AttributedStyle concealDefault() {
    return new AttributedStyle(this.style & 0xFFFFFFFFFFFFFFBFL, this.mask & 0xFFFFFFFFFFFFFFBFL);
  }
  
  public AttributedStyle crossedOut() {
    return new AttributedStyle(this.style | 0x80L, this.mask | 0x80L);
  }
  
  public AttributedStyle crossedOutOff() {
    return new AttributedStyle(this.style & 0xFFFFFFFFFFFFFF7FL, this.mask | 0x80L);
  }
  
  public AttributedStyle crossedOutDefault() {
    return new AttributedStyle(this.style & 0xFFFFFFFFFFFFFF7FL, this.mask & 0xFFFFFFFFFFFFFF7FL);
  }
  
  public AttributedStyle foreground(int color) {
    return new AttributedStyle(this.style & 0xFFFFFF8000007FFFL | 0x100L | color << 15L & 0x7FFFFF8000L, this.mask | 0x100L);
  }
  
  public AttributedStyle foreground(int r, int g, int b) {
    return foregroundRgb(r << 16 | g << 8 | b);
  }
  
  public AttributedStyle foregroundRgb(int color) {
    return new AttributedStyle(this.style & 0xFFFFFF8000007FFFL | 0x200L | (color & 0xFFFFFFL) << 15L & 0x7FFFFF8000L, this.mask | 0x200L);
  }
  
  public AttributedStyle foregroundOff() {
    return new AttributedStyle(this.style & 0xFFFFFF8000007FFFL & 0xFFFFFFFFFFFFFCFFL, this.mask | 0x300L);
  }
  
  public AttributedStyle foregroundDefault() {
    return new AttributedStyle(this.style & 0xFFFFFF8000007FFFL & 0xFFFFFFFFFFFFFCFFL, this.mask & 0xFFFFFF8000007CFFL);
  }
  
  public AttributedStyle background(int color) {
    return new AttributedStyle(this.style & 0x8000007FFFFFFFFFL | 0x400L | color << 39L & 0x7FFFFF8000000000L, this.mask | 0x400L);
  }
  
  public AttributedStyle background(int r, int g, int b) {
    return backgroundRgb(r << 16 | g << 8 | b);
  }
  
  public AttributedStyle backgroundRgb(int color) {
    return new AttributedStyle(this.style & 0x8000007FFFFFFFFFL | 0x800L | (color & 0xFFFFFFL) << 39L & 0x7FFFFF8000000000L, this.mask | 0x800L);
  }
  
  public AttributedStyle backgroundOff() {
    return new AttributedStyle(this.style & 0x8000007FFFFFFFFFL & 0xFFFFFFFFFFFFF3FFL, this.mask | 0xC00L);
  }
  
  public AttributedStyle backgroundDefault() {
    return new AttributedStyle(this.style & 0x8000007FFFFFFFFFL & 0xFFFFFFFFFFFFF3FFL, this.mask & 0x8000007FFFFFF3FFL);
  }
  
  public AttributedStyle hidden() {
    return new AttributedStyle(this.style | 0x1000L, this.mask | 0x1000L);
  }
  
  public AttributedStyle hiddenOff() {
    return new AttributedStyle(this.style & 0xFFFFFFFFFFFFEFFFL, this.mask | 0x1000L);
  }
  
  public AttributedStyle hiddenDefault() {
    return new AttributedStyle(this.style & 0xFFFFFFFFFFFFEFFFL, this.mask & 0xFFFFFFFFFFFFEFFFL);
  }
  
  public long getStyle() {
    return this.style;
  }
  
  public long getMask() {
    return this.mask;
  }
  
  public boolean equals(Object o) {
    if (this == o)
      return true; 
    if (o == null || getClass() != o.getClass())
      return false; 
    AttributedStyle that = (AttributedStyle)o;
    if (this.style != that.style)
      return false; 
    return (this.mask == that.mask);
  }
  
  public int hashCode() {
    return 31 * Long.hashCode(this.style) + Long.hashCode(this.mask);
  }
  
  public String toAnsi() {
    AttributedStringBuilder sb = new AttributedStringBuilder();
    sb.styled(this, " ");
    String s = sb.toAnsi(16777216, AttributedCharSequence.ForceMode.None);
    return (s.length() > 1) ? s.substring(2, s.indexOf('m')) : s;
  }
  
  public String toString() {
    return "AttributedStyle{style=" + this.style + ", mask=" + this.mask + ", ansi=" + 
      
      toAnsi() + '}';
  }
}
