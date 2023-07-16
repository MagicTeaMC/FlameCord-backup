package org.jline.utils;

import java.util.Locale;
import java.util.Objects;
import java.util.function.Function;
import java.util.logging.Level;
import java.util.logging.Logger;

public class StyleResolver {
  private static final Logger log = Logger.getLogger(StyleResolver.class.getName());
  
  private final Function<String, String> source;
  
  public StyleResolver(Function<String, String> source) {
    this.source = Objects.<Function<String, String>>requireNonNull(source);
  }
  
  private static Integer colorRgb(String name) {
    name = name.toLowerCase(Locale.US);
    if (name.charAt(0) == 'x' || name.charAt(0) == '#')
      try {
        return Integer.valueOf(Integer.parseInt(name.substring(1), 16));
      } catch (NumberFormatException e) {
        log.warning("Invalid hexadecimal color: " + name);
        return null;
      }  
    Integer color = color(name);
    if (color != null && color.intValue() != -1)
      color = Integer.valueOf(Colors.DEFAULT_COLORS_256[color.intValue()]); 
    return color;
  }
  
  private static Integer color(String name) {
    int flags = 0;
    if (name.equals("default"))
      return Integer.valueOf(-1); 
    if (name.charAt(0) == '!') {
      name = name.substring(1);
      flags = 8;
    } else if (name.startsWith("bright-")) {
      name = name.substring(7);
      flags = 8;
    } else if (name.charAt(0) == '~') {
      name = name.substring(1);
      try {
        return Colors.rgbColor(name);
      } catch (IllegalArgumentException e) {
        log.warning("Invalid style-color name: " + name);
        return null;
      } 
    } 
    switch (name) {
      case "black":
      case "k":
        return Integer.valueOf(flags + 0);
      case "red":
      case "r":
        return Integer.valueOf(flags + 1);
      case "green":
      case "g":
        return Integer.valueOf(flags + 2);
      case "yellow":
      case "y":
        return Integer.valueOf(flags + 3);
      case "blue":
      case "b":
        return Integer.valueOf(flags + 4);
      case "magenta":
      case "m":
        return Integer.valueOf(flags + 5);
      case "cyan":
      case "c":
        return Integer.valueOf(flags + 6);
      case "white":
      case "w":
        return Integer.valueOf(flags + 7);
    } 
    return null;
  }
  
  public AttributedStyle resolve(String spec) {
    Objects.requireNonNull(spec);
    if (log.isLoggable(Level.FINEST))
      log.finest("Resolve: " + spec); 
    int i = spec.indexOf(":-");
    if (i != -1) {
      String[] parts = spec.split(":-");
      return resolve(parts[0].trim(), parts[1].trim());
    } 
    return apply(AttributedStyle.DEFAULT, spec);
  }
  
  public AttributedStyle resolve(String spec, String defaultSpec) {
    Objects.requireNonNull(spec);
    if (log.isLoggable(Level.FINEST))
      log.finest(String.format("Resolve: %s; default: %s", new Object[] { spec, defaultSpec })); 
    AttributedStyle style = apply(AttributedStyle.DEFAULT, spec);
    if (style == AttributedStyle.DEFAULT && defaultSpec != null)
      style = apply(style, defaultSpec); 
    return style;
  }
  
  private AttributedStyle apply(AttributedStyle style, String spec) {
    if (log.isLoggable(Level.FINEST))
      log.finest("Apply: " + spec); 
    for (String item : spec.split(",")) {
      item = item.trim();
      if (!item.isEmpty())
        if (item.startsWith(".")) {
          style = applyReference(style, item);
        } else if (item.contains(":")) {
          style = applyColor(style, item);
        } else if (item.matches("[0-9]+(;[0-9]+)*")) {
          style = applyAnsi(style, item);
        } else {
          style = applyNamed(style, item);
        }  
    } 
    return style;
  }
  
  private AttributedStyle applyAnsi(AttributedStyle style, String spec) {
    if (log.isLoggable(Level.FINEST))
      log.finest("Apply-ansi: " + spec); 
    return (new AttributedStringBuilder())
      .style(style)
      .ansiAppend("\033[" + spec + "m")
      .style();
  }
  
  private AttributedStyle applyReference(AttributedStyle style, String spec) {
    if (log.isLoggable(Level.FINEST))
      log.finest("Apply-reference: " + spec); 
    if (spec.length() == 1) {
      log.warning("Invalid style-reference; missing discriminator: " + spec);
    } else {
      String name = spec.substring(1, spec.length());
      String resolvedSpec = this.source.apply(name);
      if (resolvedSpec != null)
        return apply(style, resolvedSpec); 
    } 
    return style;
  }
  
  private AttributedStyle applyNamed(AttributedStyle style, String name) {
    if (log.isLoggable(Level.FINEST))
      log.finest("Apply-named: " + name); 
    switch (name.toLowerCase(Locale.US)) {
      case "default":
        return AttributedStyle.DEFAULT;
      case "bold":
        return style.bold();
      case "faint":
        return style.faint();
      case "italic":
        return style.italic();
      case "underline":
        return style.underline();
      case "blink":
        return style.blink();
      case "inverse":
        return style.inverse();
      case "inverse-neg":
      case "inverseneg":
        return style.inverseNeg();
      case "conceal":
        return style.conceal();
      case "crossed-out":
      case "crossedout":
        return style.crossedOut();
      case "hidden":
        return style.hidden();
    } 
    log.warning("Unknown style: " + name);
    return style;
  }
  
  private AttributedStyle applyColor(AttributedStyle style, String spec) {
    Integer color;
    if (log.isLoggable(Level.FINEST))
      log.finest("Apply-color: " + spec); 
    String[] parts = spec.split(":", 2);
    String colorMode = parts[0].trim();
    String colorName = parts[1].trim();
    switch (colorMode.toLowerCase(Locale.US)) {
      case "foreground":
      case "fg":
      case "f":
        color = color(colorName);
        if (color == null) {
          log.warning("Invalid color-name: " + colorName);
        } else {
          return (color.intValue() >= 0) ? style.foreground(color.intValue()) : style.foregroundDefault();
        } 
        return style;
      case "background":
      case "bg":
      case "b":
        color = color(colorName);
        if (color == null) {
          log.warning("Invalid color-name: " + colorName);
        } else {
          return (color.intValue() >= 0) ? style.background(color.intValue()) : style.backgroundDefault();
        } 
        return style;
      case "foreground-rgb":
      case "fg-rgb":
      case "f-rgb":
        color = colorRgb(colorName);
        if (color == null) {
          log.warning("Invalid color-name: " + colorName);
        } else {
          return (color.intValue() >= 0) ? style.foregroundRgb(color.intValue()) : style.foregroundDefault();
        } 
        return style;
      case "background-rgb":
      case "bg-rgb":
      case "b-rgb":
        color = colorRgb(colorName);
        if (color == null) {
          log.warning("Invalid color-name: " + colorName);
        } else {
          return (color.intValue() >= 0) ? style.backgroundRgb(color.intValue()) : style.backgroundDefault();
        } 
        return style;
    } 
    log.warning("Invalid color-mode: " + colorMode);
    return style;
  }
}
