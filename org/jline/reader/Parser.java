package org.jline.reader;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public interface Parser {
  public static final String REGEX_VARIABLE = "[a-zA-Z_]{1,}[a-zA-Z0-9_-]*";
  
  public static final String REGEX_COMMAND = "[:]{0,1}[a-zA-Z]{1,}[a-zA-Z0-9_-]*";
  
  ParsedLine parse(String paramString, int paramInt, ParseContext paramParseContext) throws SyntaxError;
  
  default ParsedLine parse(String line, int cursor) throws SyntaxError {
    return parse(line, cursor, ParseContext.UNSPECIFIED);
  }
  
  default boolean isEscapeChar(char ch) {
    return (ch == '\\');
  }
  
  default boolean validCommandName(String name) {
    return (name != null && name.matches("[:]{0,1}[a-zA-Z]{1,}[a-zA-Z0-9_-]*"));
  }
  
  default boolean validVariableName(String name) {
    return (name != null && name.matches("[a-zA-Z_]{1,}[a-zA-Z0-9_-]*"));
  }
  
  default String getCommand(String line) {
    String out = "";
    Pattern patternCommand = Pattern.compile("^\\s*[a-zA-Z_]{1,}[a-zA-Z0-9_-]*=([:]{0,1}[a-zA-Z]{1,}[a-zA-Z0-9_-]*)(\\s+|$)");
    Matcher matcher = patternCommand.matcher(line);
    if (matcher.find()) {
      out = matcher.group(1);
    } else {
      out = line.trim().split("\\s+")[0];
      if (!out.matches("[:]{0,1}[a-zA-Z]{1,}[a-zA-Z0-9_-]*"))
        out = ""; 
    } 
    return out;
  }
  
  default String getVariable(String line) {
    String out = null;
    Pattern patternCommand = Pattern.compile("^\\s*([a-zA-Z_]{1,}[a-zA-Z0-9_-]*)\\s*=[^=~].*");
    Matcher matcher = patternCommand.matcher(line);
    if (matcher.find())
      out = matcher.group(1); 
    return out;
  }
  
  public enum ParseContext {
    UNSPECIFIED, ACCEPT_LINE, SPLIT_LINE, COMPLETE, SECONDARY_PROMPT;
  }
}
