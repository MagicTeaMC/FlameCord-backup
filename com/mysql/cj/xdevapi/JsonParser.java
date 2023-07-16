package com.mysql.cj.xdevapi;

import com.mysql.cj.Messages;
import com.mysql.cj.exceptions.AssertionFailedException;
import com.mysql.cj.exceptions.ExceptionFactory;
import com.mysql.cj.exceptions.WrongArgumentException;
import java.io.IOException;
import java.io.StringReader;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class JsonParser {
  enum Whitespace {
    TAB('\t'),
    LF('\n'),
    CR('\r'),
    SPACE(' ');
    
    public final char CHAR;
    
    Whitespace(char character) {
      this.CHAR = character;
    }
  }
  
  enum StructuralToken {
    LSQBRACKET('['),
    RSQBRACKET(']'),
    LCRBRACKET('{'),
    RCRBRACKET('}'),
    COLON(':'),
    COMMA(',');
    
    public final char CHAR;
    
    StructuralToken(char character) {
      this.CHAR = character;
    }
  }
  
  enum EscapeChar {
    QUOTE('"', "\\\"", true),
    RSOLIDUS('\\', "\\\\", true),
    SOLIDUS('/', "\\/", false),
    BACKSPACE('\b', "\\b", true),
    FF('\f', "\\f", true),
    LF('\n', "\\n", true),
    CR('\r', "\\r", true),
    TAB('\t', "\\t", true);
    
    public final char CHAR;
    
    public final String ESCAPED;
    
    public final boolean NEEDS_ESCAPING;
    
    EscapeChar(char character, String escaped, boolean needsEscaping) {
      this.CHAR = character;
      this.ESCAPED = escaped;
      this.NEEDS_ESCAPING = needsEscaping;
    }
  }
  
  static Set<Character> whitespaceChars = new HashSet<>();
  
  static HashMap<Character, Character> escapeChars = new HashMap<>();
  
  static {
    for (EscapeChar ec : EscapeChar.values())
      escapeChars.put(Character.valueOf(ec.ESCAPED.charAt(1)), Character.valueOf(ec.CHAR)); 
    for (Whitespace ws : Whitespace.values())
      whitespaceChars.add(Character.valueOf(ws.CHAR)); 
  }
  
  private static boolean isValidEndOfValue(char ch) {
    return (StructuralToken.COMMA.CHAR == ch || StructuralToken.RCRBRACKET.CHAR == ch || StructuralToken.RSQBRACKET.CHAR == ch);
  }
  
  public static DbDoc parseDoc(String jsonString) {
    try {
      return parseDoc(new StringReader(jsonString));
    } catch (IOException ex) {
      throw AssertionFailedException.shouldNotHappen(ex);
    } 
  }
  
  public static DbDoc parseDoc(StringReader reader) throws IOException {
    DbDoc doc = new DbDocImpl();
    int leftBrackets = 0;
    int rightBrackets = 0;
    int intch;
    while ((intch = reader.read()) != -1) {
      String key = null;
      char ch = (char)intch;
      if (ch == StructuralToken.LCRBRACKET.CHAR || ch == StructuralToken.COMMA.CHAR) {
        if (ch == StructuralToken.LCRBRACKET.CHAR)
          leftBrackets++; 
        if ((key = nextKey(reader)) != null) {
          try {
            JsonValue val;
            if ((val = nextValue(reader)) != null) {
              doc.put(key, val);
              continue;
            } 
            reader.reset();
          } catch (WrongArgumentException ex) {
            throw (WrongArgumentException)ExceptionFactory.createException(WrongArgumentException.class, Messages.getString("JsonParser.0", new String[] { key }), ex);
          } 
          continue;
        } 
        reader.reset();
        continue;
      } 
      if (ch == StructuralToken.RCRBRACKET.CHAR) {
        rightBrackets++;
        break;
      } 
      if (!whitespaceChars.contains(Character.valueOf(ch)))
        throw (WrongArgumentException)ExceptionFactory.createException(WrongArgumentException.class, Messages.getString("JsonParser.1", new Character[] { Character.valueOf(ch) })); 
    } 
    if (leftBrackets == 0)
      throw (WrongArgumentException)ExceptionFactory.createException(WrongArgumentException.class, Messages.getString("JsonParser.2")); 
    if (leftBrackets > rightBrackets)
      throw (WrongArgumentException)ExceptionFactory.createException(WrongArgumentException.class, 
          Messages.getString("JsonParser.3", new Character[] { Character.valueOf(StructuralToken.RCRBRACKET.CHAR) })); 
    return doc;
  }
  
  public static JsonArray parseArray(StringReader reader) throws IOException {
    JsonArray arr = new JsonArray();
    int openings = 0;
    int intch;
    while ((intch = reader.read()) != -1) {
      char ch = (char)intch;
      if (ch == StructuralToken.LSQBRACKET.CHAR || ch == StructuralToken.COMMA.CHAR) {
        if (ch == StructuralToken.LSQBRACKET.CHAR)
          openings++; 
        JsonValue val;
        if ((val = nextValue(reader)) != null) {
          arr.add(val);
          continue;
        } 
        reader.reset();
        continue;
      } 
      if (ch == StructuralToken.RSQBRACKET.CHAR) {
        openings--;
        break;
      } 
      if (!whitespaceChars.contains(Character.valueOf(ch)))
        throw (WrongArgumentException)ExceptionFactory.createException(WrongArgumentException.class, Messages.getString("JsonParser.1", new Character[] { Character.valueOf(ch) })); 
    } 
    if (openings > 0)
      throw (WrongArgumentException)ExceptionFactory.createException(WrongArgumentException.class, 
          Messages.getString("JsonParser.3", new Character[] { Character.valueOf(StructuralToken.RSQBRACKET.CHAR) })); 
    return arr;
  }
  
  private static String nextKey(StringReader reader) throws IOException {
    reader.mark(1);
    JsonString val = parseString(reader);
    if (val == null)
      reader.reset(); 
    char ch = ' ';
    int intch;
    while ((intch = reader.read()) != -1) {
      ch = (char)intch;
      if (ch == StructuralToken.COLON.CHAR)
        break; 
      if (ch == StructuralToken.RCRBRACKET.CHAR)
        break; 
      if (!whitespaceChars.contains(Character.valueOf(ch)))
        throw (WrongArgumentException)ExceptionFactory.createException(WrongArgumentException.class, Messages.getString("JsonParser.1", new Character[] { Character.valueOf(ch) })); 
    } 
    if (ch != StructuralToken.COLON.CHAR && val != null && val.getString().length() > 0)
      throw (WrongArgumentException)ExceptionFactory.createException(WrongArgumentException.class, Messages.getString("JsonParser.4", new String[] { val.getString() })); 
    return (val != null) ? val.getString() : null;
  }
  
  private static JsonValue nextValue(StringReader reader) throws IOException {
    reader.mark(1);
    int intch;
    while ((intch = reader.read()) != -1) {
      char ch = (char)intch;
      if (ch == EscapeChar.QUOTE.CHAR) {
        reader.reset();
        return parseString(reader);
      } 
      if (ch == StructuralToken.LSQBRACKET.CHAR) {
        reader.reset();
        return parseArray(reader);
      } 
      if (ch == StructuralToken.LCRBRACKET.CHAR) {
        reader.reset();
        return parseDoc(reader);
      } 
      if (ch == '-' || (ch >= '0' && ch <= '9')) {
        reader.reset();
        return parseNumber(reader);
      } 
      if (ch == JsonLiteral.TRUE.value.charAt(0)) {
        reader.reset();
        return parseLiteral(reader);
      } 
      if (ch == JsonLiteral.FALSE.value.charAt(0)) {
        reader.reset();
        return parseLiteral(reader);
      } 
      if (ch == JsonLiteral.NULL.value.charAt(0)) {
        reader.reset();
        return parseLiteral(reader);
      } 
      if (ch == StructuralToken.RSQBRACKET.CHAR)
        return null; 
      if (!whitespaceChars.contains(Character.valueOf(ch)))
        throw (WrongArgumentException)ExceptionFactory.createException(WrongArgumentException.class, Messages.getString("JsonParser.1", new Character[] { Character.valueOf(ch) })); 
      reader.mark(1);
    } 
    throw (WrongArgumentException)ExceptionFactory.createException(WrongArgumentException.class, Messages.getString("JsonParser.5"));
  }
  
  private static void appendChar(StringBuilder sb, char ch) {
    if (sb == null) {
      if (!whitespaceChars.contains(Character.valueOf(ch)))
        throw (WrongArgumentException)ExceptionFactory.createException(WrongArgumentException.class, Messages.getString("JsonParser.6", new Character[] { Character.valueOf(ch) })); 
    } else {
      sb.append(ch);
    } 
  }
  
  static JsonString parseString(StringReader reader) throws IOException {
    int quotes = 0;
    boolean escapeNextChar = false;
    StringBuilder sb = null;
    int intch;
    while ((intch = reader.read()) != -1) {
      char ch = (char)intch;
      if (escapeNextChar) {
        if (escapeChars.containsKey(Character.valueOf(ch))) {
          appendChar(sb, ((Character)escapeChars.get(Character.valueOf(ch))).charValue());
        } else if (ch == 'u') {
          char[] buf = new char[4];
          int countRead = reader.read(buf);
          String hexCodePoint = (countRead == -1) ? "" : String.valueOf(buf, 0, countRead);
          if (countRead != 4)
            throw (WrongArgumentException)ExceptionFactory.createException(WrongArgumentException.class, 
                Messages.getString("JsonParser.13", new String[] { hexCodePoint })); 
          try {
            appendChar(sb, (char)Integer.parseInt(hexCodePoint, 16));
          } catch (NumberFormatException e) {
            throw (WrongArgumentException)ExceptionFactory.createException(WrongArgumentException.class, 
                Messages.getString("JsonParser.13", new String[] { hexCodePoint }));
          } 
        } else {
          throw (WrongArgumentException)ExceptionFactory.createException(WrongArgumentException.class, Messages.getString("JsonParser.7", new Character[] { Character.valueOf(ch) }));
        } 
        escapeNextChar = false;
        continue;
      } 
      if (ch == EscapeChar.QUOTE.CHAR) {
        if (sb == null) {
          sb = new StringBuilder();
          quotes++;
          continue;
        } 
        quotes--;
        break;
      } 
      if (quotes == 0 && ch == StructuralToken.RCRBRACKET.CHAR)
        break; 
      if (ch == EscapeChar.RSOLIDUS.CHAR) {
        escapeNextChar = true;
        continue;
      } 
      appendChar(sb, ch);
    } 
    if (quotes > 0)
      throw (WrongArgumentException)ExceptionFactory.createException(WrongArgumentException.class, Messages.getString("JsonParser.3", new Character[] { Character.valueOf(EscapeChar.QUOTE.CHAR) })); 
    return (sb == null) ? null : (new JsonString()).setValue(sb.toString());
  }
  
  static JsonNumber parseNumber(StringReader reader) throws IOException {
    StringBuilder sb = null;
    char lastChar = ' ';
    boolean hasFractionalPart = false;
    boolean hasExponent = false;
    int intch;
    while ((intch = reader.read()) != -1) {
      char ch = (char)intch;
      if (sb == null) {
        if (ch == '-') {
          sb = new StringBuilder();
          sb.append(ch);
        } else if (ch >= '0' && ch <= '9') {
          sb = new StringBuilder();
          sb.append(ch);
        } else if (!whitespaceChars.contains(Character.valueOf(ch))) {
          throw (WrongArgumentException)ExceptionFactory.createException(WrongArgumentException.class, Messages.getString("JsonParser.1", new Character[] { Character.valueOf(ch) }));
        } 
      } else if (ch == '-') {
        if (lastChar == 'E' || lastChar == 'e') {
          sb.append(ch);
        } else {
          throw (WrongArgumentException)ExceptionFactory.createException(WrongArgumentException.class, 
              Messages.getString("JsonParser.8", new Object[] { Character.valueOf(ch), sb.toString() }));
        } 
      } else if (ch >= '0' && ch <= '9') {
        sb.append(ch);
      } else if (ch == 'E' || ch == 'e') {
        if (lastChar >= '0' && lastChar <= '9') {
          hasExponent = true;
          sb.append(ch);
        } else {
          throw (WrongArgumentException)ExceptionFactory.createException(WrongArgumentException.class, 
              Messages.getString("JsonParser.8", new Object[] { Character.valueOf(ch), sb.toString() }));
        } 
      } else if (ch == '.') {
        if (hasFractionalPart)
          throw (WrongArgumentException)ExceptionFactory.createException(WrongArgumentException.class, 
              Messages.getString("JsonParser.10", new Object[] { Character.valueOf(ch), sb.toString() })); 
        if (hasExponent)
          throw (WrongArgumentException)ExceptionFactory.createException(WrongArgumentException.class, Messages.getString("JsonParser.11")); 
        if (lastChar >= '0' && lastChar <= '9') {
          hasFractionalPart = true;
          sb.append(ch);
        } else {
          throw (WrongArgumentException)ExceptionFactory.createException(WrongArgumentException.class, 
              Messages.getString("JsonParser.8", new Object[] { Character.valueOf(ch), sb.toString() }));
        } 
      } else if (ch == '+') {
        if (lastChar == 'E' || lastChar == 'e') {
          sb.append(ch);
        } else {
          throw (WrongArgumentException)ExceptionFactory.createException(WrongArgumentException.class, 
              Messages.getString("JsonParser.8", new Object[] { Character.valueOf(ch), sb.toString() }));
        } 
      } else {
        if (whitespaceChars.contains(Character.valueOf(ch)) || isValidEndOfValue(ch)) {
          reader.reset();
          break;
        } 
        throw (WrongArgumentException)ExceptionFactory.createException(WrongArgumentException.class, Messages.getString("JsonParser.1", new Character[] { Character.valueOf(ch) }));
      } 
      lastChar = ch;
      reader.mark(1);
    } 
    if (sb == null || sb.length() == 0)
      throw (WrongArgumentException)ExceptionFactory.createException(WrongArgumentException.class, Messages.getString("JsonParser.5")); 
    return (new JsonNumber()).setValue(sb.toString());
  }
  
  static JsonLiteral parseLiteral(StringReader reader) throws IOException {
    StringBuilder sb = null;
    JsonLiteral res = null;
    int literalIndex = 0;
    int intch;
    while ((intch = reader.read()) != -1) {
      char ch = (char)intch;
      if (sb == null) {
        if (ch == JsonLiteral.TRUE.value.charAt(0)) {
          res = JsonLiteral.TRUE;
          sb = new StringBuilder();
          sb.append(ch);
          literalIndex++;
        } else if (ch == JsonLiteral.FALSE.value.charAt(0)) {
          res = JsonLiteral.FALSE;
          sb = new StringBuilder();
          sb.append(ch);
          literalIndex++;
        } else if (ch == JsonLiteral.NULL.value.charAt(0)) {
          res = JsonLiteral.NULL;
          sb = new StringBuilder();
          sb.append(ch);
          literalIndex++;
        } else if (!whitespaceChars.contains(Character.valueOf(ch))) {
          throw (WrongArgumentException)ExceptionFactory.createException(WrongArgumentException.class, Messages.getString("JsonParser.1", new Character[] { Character.valueOf(ch) }));
        } 
      } else if (literalIndex < res.value.length() && ch == res.value.charAt(literalIndex)) {
        sb.append(ch);
        literalIndex++;
      } else {
        if (whitespaceChars.contains(Character.valueOf(ch)) || isValidEndOfValue(ch)) {
          reader.reset();
          break;
        } 
        throw (WrongArgumentException)ExceptionFactory.createException(WrongArgumentException.class, Messages.getString("JsonParser.1", new Character[] { Character.valueOf(ch) }));
      } 
      reader.mark(1);
    } 
    if (sb == null)
      throw (WrongArgumentException)ExceptionFactory.createException(WrongArgumentException.class, Messages.getString("JsonParser.5")); 
    if (literalIndex == res.value.length())
      return res; 
    throw (WrongArgumentException)ExceptionFactory.createException(WrongArgumentException.class, Messages.getString("JsonParser.12", new String[] { sb.toString() }));
  }
}
