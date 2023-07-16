package org.fusesource.jansi;

import java.io.IOException;
import java.util.Locale;

public class AnsiRenderer {
  public static final String BEGIN_TOKEN = "@|";
  
  public static final String END_TOKEN = "|@";
  
  public static final String CODE_TEXT_SEPARATOR = " ";
  
  public static final String CODE_LIST_SEPARATOR = ",";
  
  private static final int BEGIN_TOKEN_LEN = 2;
  
  private static final int END_TOKEN_LEN = 2;
  
  public static String render(String input) throws IllegalArgumentException {
    try {
      return render(input, new StringBuilder()).toString();
    } catch (IOException e) {
      throw new IllegalArgumentException(e);
    } 
  }
  
  public static Appendable render(String input, Appendable target) throws IOException {
    int i = 0;
    while (true) {
      int j = input.indexOf("@|", i);
      if (j == -1) {
        if (i == 0) {
          target.append(input);
          return target;
        } 
        target.append(input.substring(i));
        return target;
      } 
      target.append(input.substring(i, j));
      int k = input.indexOf("|@", j);
      if (k == -1) {
        target.append(input);
        return target;
      } 
      j += 2;
      String spec = input.substring(j, k);
      String[] items = spec.split(" ", 2);
      if (items.length == 1) {
        target.append(input);
        return target;
      } 
      String replacement = render(items[1], items[0].split(","));
      target.append(replacement);
      i = k + 2;
    } 
  }
  
  public static String render(String text, String... codes) {
    return render(Ansi.ansi(), codes)
      .a(text).reset().toString();
  }
  
  public static String renderCodes(String... codes) {
    return render(Ansi.ansi(), codes).toString();
  }
  
  public static String renderCodes(String codes) {
    return renderCodes(codes.split("\\s"));
  }
  
  private static Ansi render(Ansi ansi, String... names) {
    for (String name : names) {
      Code code = Code.valueOf(name.toUpperCase(Locale.ENGLISH));
      if (code.isColor()) {
        if (code.isBackground()) {
          ansi.bg(code.getColor());
        } else {
          ansi.fg(code.getColor());
        } 
      } else if (code.isAttribute()) {
        ansi.a(code.getAttribute());
      } 
    } 
    return ansi;
  }
  
  public static boolean test(String text) {
    return (text != null && text.contains("@|"));
  }
  
  public enum Code {
    BLACK((String)Ansi.Color.BLACK),
    RED((String)Ansi.Color.RED),
    GREEN((String)Ansi.Color.GREEN),
    YELLOW((String)Ansi.Color.YELLOW),
    BLUE((String)Ansi.Color.BLUE),
    MAGENTA((String)Ansi.Color.MAGENTA),
    CYAN((String)Ansi.Color.CYAN),
    WHITE((String)Ansi.Color.WHITE),
    DEFAULT((String)Ansi.Color.DEFAULT),
    FG_BLACK((String)Ansi.Color.BLACK, false),
    FG_RED((String)Ansi.Color.RED, false),
    FG_GREEN((String)Ansi.Color.GREEN, false),
    FG_YELLOW((String)Ansi.Color.YELLOW, false),
    FG_BLUE((String)Ansi.Color.BLUE, false),
    FG_MAGENTA((String)Ansi.Color.MAGENTA, false),
    FG_CYAN((String)Ansi.Color.CYAN, false),
    FG_WHITE((String)Ansi.Color.WHITE, false),
    FG_DEFAULT((String)Ansi.Color.DEFAULT, false),
    BG_BLACK((String)Ansi.Color.BLACK, true),
    BG_RED((String)Ansi.Color.RED, true),
    BG_GREEN((String)Ansi.Color.GREEN, true),
    BG_YELLOW((String)Ansi.Color.YELLOW, true),
    BG_BLUE((String)Ansi.Color.BLUE, true),
    BG_MAGENTA((String)Ansi.Color.MAGENTA, true),
    BG_CYAN((String)Ansi.Color.CYAN, true),
    BG_WHITE((String)Ansi.Color.WHITE, true),
    BG_DEFAULT((String)Ansi.Color.DEFAULT, true),
    RESET((String)Ansi.Attribute.RESET),
    INTENSITY_BOLD((String)Ansi.Attribute.INTENSITY_BOLD),
    INTENSITY_FAINT((String)Ansi.Attribute.INTENSITY_FAINT),
    ITALIC((String)Ansi.Attribute.ITALIC),
    UNDERLINE((String)Ansi.Attribute.UNDERLINE),
    BLINK_SLOW((String)Ansi.Attribute.BLINK_SLOW),
    BLINK_FAST((String)Ansi.Attribute.BLINK_FAST),
    BLINK_OFF((String)Ansi.Attribute.BLINK_OFF),
    NEGATIVE_ON((String)Ansi.Attribute.NEGATIVE_ON),
    NEGATIVE_OFF((String)Ansi.Attribute.NEGATIVE_OFF),
    CONCEAL_ON((String)Ansi.Attribute.CONCEAL_ON),
    CONCEAL_OFF((String)Ansi.Attribute.CONCEAL_OFF),
    UNDERLINE_DOUBLE((String)Ansi.Attribute.UNDERLINE_DOUBLE),
    UNDERLINE_OFF((String)Ansi.Attribute.UNDERLINE_OFF),
    BOLD((String)Ansi.Attribute.INTENSITY_BOLD),
    FAINT((String)Ansi.Attribute.INTENSITY_FAINT);
    
    private final Enum<?> n;
    
    private final boolean background;
    
    Code(Enum<?> n, boolean background) {
      this.n = n;
      this.background = background;
    }
    
    public boolean isColor() {
      return this.n instanceof Ansi.Color;
    }
    
    public Ansi.Color getColor() {
      return (Ansi.Color)this.n;
    }
    
    public boolean isAttribute() {
      return this.n instanceof Ansi.Attribute;
    }
    
    public Ansi.Attribute getAttribute() {
      return (Ansi.Attribute)this.n;
    }
    
    public boolean isBackground() {
      return this.background;
    }
  }
}
