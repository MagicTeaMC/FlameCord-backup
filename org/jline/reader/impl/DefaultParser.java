package org.jline.reader.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.jline.reader.CompletingParsedLine;
import org.jline.reader.EOFError;
import org.jline.reader.ParsedLine;
import org.jline.reader.Parser;

public class DefaultParser implements Parser {
  public enum Bracket {
    ROUND, CURLY, SQUARE, ANGLE;
  }
  
  private char[] quoteChars = new char[] { '\'', '"' };
  
  private char[] escapeChars = new char[] { '\\' };
  
  private boolean eofOnUnclosedQuote;
  
  private boolean eofOnEscapedNewLine;
  
  private char[] openingBrackets = null;
  
  private char[] closingBrackets = null;
  
  private String regexVariable = "[a-zA-Z_]+[a-zA-Z0-9_-]*((\\.|\\['|\\[\"|\\[)[a-zA-Z0-9_-]*(|']|\"]|]))?";
  
  private String regexCommand = "[:]?[a-zA-Z]+[a-zA-Z0-9_-]*";
  
  private int commandGroup = 4;
  
  public DefaultParser quoteChars(char[] chars) {
    this.quoteChars = chars;
    return this;
  }
  
  public DefaultParser escapeChars(char[] chars) {
    this.escapeChars = chars;
    return this;
  }
  
  public DefaultParser eofOnUnclosedQuote(boolean eofOnUnclosedQuote) {
    this.eofOnUnclosedQuote = eofOnUnclosedQuote;
    return this;
  }
  
  public DefaultParser eofOnUnclosedBracket(Bracket... brackets) {
    setEofOnUnclosedBracket(brackets);
    return this;
  }
  
  public DefaultParser eofOnEscapedNewLine(boolean eofOnEscapedNewLine) {
    this.eofOnEscapedNewLine = eofOnEscapedNewLine;
    return this;
  }
  
  public DefaultParser regexVariable(String regexVariable) {
    this.regexVariable = regexVariable;
    return this;
  }
  
  public DefaultParser regexCommand(String regexCommand) {
    this.regexCommand = regexCommand;
    return this;
  }
  
  public DefaultParser commandGroup(int commandGroup) {
    this.commandGroup = commandGroup;
    return this;
  }
  
  public void setQuoteChars(char[] chars) {
    this.quoteChars = chars;
  }
  
  public char[] getQuoteChars() {
    return this.quoteChars;
  }
  
  public void setEscapeChars(char[] chars) {
    this.escapeChars = chars;
  }
  
  public char[] getEscapeChars() {
    return this.escapeChars;
  }
  
  public void setEofOnUnclosedQuote(boolean eofOnUnclosedQuote) {
    this.eofOnUnclosedQuote = eofOnUnclosedQuote;
  }
  
  public boolean isEofOnUnclosedQuote() {
    return this.eofOnUnclosedQuote;
  }
  
  public void setEofOnEscapedNewLine(boolean eofOnEscapedNewLine) {
    this.eofOnEscapedNewLine = eofOnEscapedNewLine;
  }
  
  public boolean isEofOnEscapedNewLine() {
    return this.eofOnEscapedNewLine;
  }
  
  public void setEofOnUnclosedBracket(Bracket... brackets) {
    if (brackets == null) {
      this.openingBrackets = null;
      this.closingBrackets = null;
    } else {
      Set<Bracket> bs = new HashSet<>(Arrays.asList(brackets));
      this.openingBrackets = new char[bs.size()];
      this.closingBrackets = new char[bs.size()];
      int i = 0;
      for (Bracket b : bs) {
        switch (b) {
          case ROUND:
            this.openingBrackets[i] = '(';
            this.closingBrackets[i] = ')';
            break;
          case CURLY:
            this.openingBrackets[i] = '{';
            this.closingBrackets[i] = '}';
            break;
          case SQUARE:
            this.openingBrackets[i] = '[';
            this.closingBrackets[i] = ']';
            break;
          case ANGLE:
            this.openingBrackets[i] = '<';
            this.closingBrackets[i] = '>';
            break;
        } 
        i++;
      } 
    } 
  }
  
  public void setRegexVariable(String regexVariable) {
    this.regexVariable = regexVariable;
  }
  
  public void setRegexCommand(String regexCommand) {
    this.regexCommand = regexCommand;
  }
  
  public void setCommandGroup(int commandGroup) {
    this.commandGroup = commandGroup;
  }
  
  public boolean validCommandName(String name) {
    return (name != null && name.matches(this.regexCommand));
  }
  
  public boolean validVariableName(String name) {
    return (name != null && this.regexVariable != null && name.matches(this.regexVariable));
  }
  
  public String getCommand(String line) {
    String out = "";
    boolean checkCommandOnly = (this.regexVariable == null);
    if (!checkCommandOnly) {
      Pattern patternCommand = Pattern.compile("^\\s*" + this.regexVariable + "=(" + this.regexCommand + ")(\\s+|$)");
      Matcher matcher = patternCommand.matcher(line);
      if (matcher.find()) {
        out = matcher.group(this.commandGroup);
      } else {
        checkCommandOnly = true;
      } 
    } 
    if (checkCommandOnly) {
      out = line.trim().split("\\s+")[0];
      if (!out.matches(this.regexCommand))
        out = ""; 
    } 
    return out;
  }
  
  public String getVariable(String line) {
    String out = null;
    if (this.regexVariable != null) {
      Pattern patternCommand = Pattern.compile("^\\s*(" + this.regexVariable + ")\\s*=[^=~].*");
      Matcher matcher = patternCommand.matcher(line);
      if (matcher.find())
        out = matcher.group(1); 
    } 
    return out;
  }
  
  public ParsedLine parse(String line, int cursor, Parser.ParseContext context) {
    List<String> words = new LinkedList<>();
    StringBuilder current = new StringBuilder();
    int wordCursor = -1;
    int wordIndex = -1;
    int quoteStart = -1;
    int rawWordCursor = -1;
    int rawWordLength = -1;
    int rawWordStart = 0;
    BracketChecker bracketChecker = new BracketChecker(cursor);
    boolean quotedWord = false;
    for (int i = 0; line != null && i < line.length(); i++) {
      if (i == cursor) {
        wordIndex = words.size();
        wordCursor = current.length();
        rawWordCursor = i - rawWordStart;
      } 
      if (quoteStart < 0 && isQuoteChar(line, i)) {
        quoteStart = i;
        if (current.length() == 0) {
          quotedWord = true;
          if (context == Parser.ParseContext.SPLIT_LINE)
            current.append(line.charAt(i)); 
        } else {
          current.append(line.charAt(i));
        } 
      } else if (quoteStart >= 0 && line.charAt(quoteStart) == line.charAt(i) && !isEscaped(line, i)) {
        if (!quotedWord || context == Parser.ParseContext.SPLIT_LINE) {
          current.append(line.charAt(i));
        } else if (rawWordCursor >= 0 && rawWordLength < 0) {
          rawWordLength = i - rawWordStart + 1;
        } 
        quoteStart = -1;
        quotedWord = false;
      } else if (quoteStart < 0 && isDelimiter(line, i)) {
        if (current.length() > 0) {
          words.add(current.toString());
          current.setLength(0);
          if (rawWordCursor >= 0 && rawWordLength < 0)
            rawWordLength = i - rawWordStart; 
        } 
        rawWordStart = i + 1;
      } else if (!isEscapeChar(line, i)) {
        current.append(line.charAt(i));
        if (quoteStart < 0)
          bracketChecker.check(line, i); 
      } else if (context == Parser.ParseContext.SPLIT_LINE) {
        current.append(line.charAt(i));
      } 
    } 
    if (current.length() > 0 || cursor == line.length()) {
      words.add(current.toString());
      if (rawWordCursor >= 0 && rawWordLength < 0)
        rawWordLength = line.length() - rawWordStart; 
    } 
    if (cursor == line.length()) {
      wordIndex = words.size() - 1;
      wordCursor = ((String)words.get(words.size() - 1)).length();
      rawWordCursor = cursor - rawWordStart;
      rawWordLength = rawWordCursor;
    } 
    if (context != Parser.ParseContext.COMPLETE && context != Parser.ParseContext.SPLIT_LINE) {
      if (this.eofOnEscapedNewLine && isEscapeChar(line, line.length() - 1))
        throw new EOFError(-1, -1, "Escaped new line", "newline"); 
      if (this.eofOnUnclosedQuote && quoteStart >= 0)
        throw new EOFError(-1, -1, "Missing closing quote", (line.charAt(quoteStart) == '\'') ? 
            "quote" : "dquote"); 
      if (bracketChecker.isClosingBracketMissing() || bracketChecker.isOpeningBracketMissing()) {
        String message = null;
        String missing = null;
        if (bracketChecker.isClosingBracketMissing()) {
          message = "Missing closing brackets";
          missing = "add: " + bracketChecker.getMissingClosingBrackets();
        } else {
          message = "Missing opening bracket";
          missing = "missing: " + bracketChecker.getMissingOpeningBracket();
        } 
        throw new EOFError(-1, -1, message, missing, bracketChecker
            .getOpenBrackets(), bracketChecker.getNextClosingBracket());
      } 
    } 
    String openingQuote = quotedWord ? line.substring(quoteStart, quoteStart + 1) : null;
    return new ArgumentList(line, words, wordIndex, wordCursor, cursor, openingQuote, rawWordCursor, rawWordLength);
  }
  
  public boolean isDelimiter(CharSequence buffer, int pos) {
    return (!isQuoted(buffer, pos) && !isEscaped(buffer, pos) && isDelimiterChar(buffer, pos));
  }
  
  public boolean isQuoted(CharSequence buffer, int pos) {
    return false;
  }
  
  public boolean isQuoteChar(CharSequence buffer, int pos) {
    if (pos < 0)
      return false; 
    if (this.quoteChars != null)
      for (char e : this.quoteChars) {
        if (e == buffer.charAt(pos))
          return !isEscaped(buffer, pos); 
      }  
    return false;
  }
  
  public boolean isEscapeChar(char ch) {
    if (this.escapeChars != null)
      for (char e : this.escapeChars) {
        if (e == ch)
          return true; 
      }  
    return false;
  }
  
  public boolean isEscapeChar(CharSequence buffer, int pos) {
    if (pos < 0)
      return false; 
    char ch = buffer.charAt(pos);
    return (isEscapeChar(ch) && !isEscaped(buffer, pos));
  }
  
  public boolean isEscaped(CharSequence buffer, int pos) {
    if (pos <= 0)
      return false; 
    return isEscapeChar(buffer, pos - 1);
  }
  
  public boolean isDelimiterChar(CharSequence buffer, int pos) {
    return Character.isWhitespace(buffer.charAt(pos));
  }
  
  private boolean isRawEscapeChar(char key) {
    if (this.escapeChars != null)
      for (char e : this.escapeChars) {
        if (e == key)
          return true; 
      }  
    return false;
  }
  
  private boolean isRawQuoteChar(char key) {
    if (this.quoteChars != null)
      for (char e : this.quoteChars) {
        if (e == key)
          return true; 
      }  
    return false;
  }
  
  private class BracketChecker {
    private int missingOpeningBracket = -1;
    
    private List<Integer> nested = new ArrayList<>();
    
    private int openBrackets = 0;
    
    private int cursor;
    
    private String nextClosingBracket;
    
    public BracketChecker(int cursor) {
      this.cursor = cursor;
    }
    
    public void check(CharSequence buffer, int pos) {
      if (DefaultParser.this.openingBrackets == null || pos < 0)
        return; 
      int bid = bracketId(DefaultParser.this.openingBrackets, buffer, pos);
      if (bid >= 0) {
        this.nested.add(Integer.valueOf(bid));
      } else {
        bid = bracketId(DefaultParser.this.closingBrackets, buffer, pos);
        if (bid >= 0)
          if (!this.nested.isEmpty() && bid == ((Integer)this.nested.get(this.nested.size() - 1)).intValue()) {
            this.nested.remove(this.nested.size() - 1);
          } else {
            this.missingOpeningBracket = bid;
          }  
      } 
      if (this.cursor > pos) {
        this.openBrackets = this.nested.size();
        if (this.nested.size() > 0)
          this.nextClosingBracket = String.valueOf(DefaultParser.this.closingBrackets[((Integer)this.nested.get(this.nested.size() - 1)).intValue()]); 
      } 
    }
    
    public boolean isOpeningBracketMissing() {
      return (this.missingOpeningBracket != -1);
    }
    
    public String getMissingOpeningBracket() {
      if (!isOpeningBracketMissing())
        return null; 
      return Character.toString(DefaultParser.this.openingBrackets[this.missingOpeningBracket]);
    }
    
    public boolean isClosingBracketMissing() {
      return !this.nested.isEmpty();
    }
    
    public String getMissingClosingBrackets() {
      if (!isClosingBracketMissing())
        return null; 
      StringBuilder out = new StringBuilder();
      for (int i = this.nested.size() - 1; i > -1; i--)
        out.append(DefaultParser.this.closingBrackets[((Integer)this.nested.get(i)).intValue()]); 
      return out.toString();
    }
    
    public int getOpenBrackets() {
      return this.openBrackets;
    }
    
    public String getNextClosingBracket() {
      return (this.nested.size() == 2) ? this.nextClosingBracket : null;
    }
    
    private int bracketId(char[] brackets, CharSequence buffer, int pos) {
      for (int i = 0; i < brackets.length; i++) {
        if (buffer.charAt(pos) == brackets[i])
          return i; 
      } 
      return -1;
    }
  }
  
  public class ArgumentList implements ParsedLine, CompletingParsedLine {
    private final String line;
    
    private final List<String> words;
    
    private final int wordIndex;
    
    private final int wordCursor;
    
    private final int cursor;
    
    private final String openingQuote;
    
    private final int rawWordCursor;
    
    private final int rawWordLength;
    
    @Deprecated
    public ArgumentList(String line, List<String> words, int wordIndex, int wordCursor, int cursor) {
      this(line, words, wordIndex, wordCursor, cursor, null, wordCursor, ((String)words
          .get(wordIndex)).length());
    }
    
    public ArgumentList(String line, List<String> words, int wordIndex, int wordCursor, int cursor, String openingQuote, int rawWordCursor, int rawWordLength) {
      this.line = line;
      this.words = Collections.unmodifiableList(Objects.<List<? extends String>>requireNonNull(words));
      this.wordIndex = wordIndex;
      this.wordCursor = wordCursor;
      this.cursor = cursor;
      this.openingQuote = openingQuote;
      this.rawWordCursor = rawWordCursor;
      this.rawWordLength = rawWordLength;
    }
    
    public int wordIndex() {
      return this.wordIndex;
    }
    
    public String word() {
      if (this.wordIndex < 0 || this.wordIndex >= this.words.size())
        return ""; 
      return this.words.get(this.wordIndex);
    }
    
    public int wordCursor() {
      return this.wordCursor;
    }
    
    public List<String> words() {
      return this.words;
    }
    
    public int cursor() {
      return this.cursor;
    }
    
    public String line() {
      return this.line;
    }
    
    public CharSequence escape(CharSequence candidate, boolean complete) {
      StringBuilder sb = new StringBuilder(candidate);
      String quote = this.openingQuote;
      boolean middleQuotes = false;
      if (this.openingQuote == null)
        for (int i = 0; i < sb.length(); i++) {
          if (DefaultParser.this.isQuoteChar(sb, i)) {
            middleQuotes = true;
            break;
          } 
        }  
      if (DefaultParser.this.escapeChars != null) {
        if (DefaultParser.this.escapeChars.length > 0) {
          Predicate<Integer> needToBeEscaped;
          if (this.openingQuote != null) {
            needToBeEscaped = (i -> (DefaultParser.this.isRawEscapeChar(sb.charAt(i.intValue())) || String.valueOf(sb.charAt(i.intValue())).equals(this.openingQuote)));
          } else if (middleQuotes) {
            needToBeEscaped = (i -> DefaultParser.this.isRawEscapeChar(sb.charAt(i.intValue())));
          } else {
            needToBeEscaped = (i -> (DefaultParser.this.isDelimiterChar(sb, i.intValue()) || DefaultParser.this.isRawEscapeChar(sb.charAt(i.intValue())) || DefaultParser.this.isRawQuoteChar(sb.charAt(i.intValue()))));
          } 
          for (int i = 0; i < sb.length(); i++) {
            if (needToBeEscaped.test(Integer.valueOf(i)))
              sb.insert(i++, DefaultParser.this.escapeChars[0]); 
          } 
        } 
      } else if (this.openingQuote == null && !middleQuotes) {
        for (int i = 0; i < sb.length(); i++) {
          if (DefaultParser.this.isDelimiterChar(sb, i)) {
            quote = "'";
            break;
          } 
        } 
      } 
      if (quote != null) {
        sb.insert(0, quote);
        if (complete)
          sb.append(quote); 
      } 
      return sb;
    }
    
    public int rawWordCursor() {
      return this.rawWordCursor;
    }
    
    public int rawWordLength() {
      return this.rawWordLength;
    }
  }
}
