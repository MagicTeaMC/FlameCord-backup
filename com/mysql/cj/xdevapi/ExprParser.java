package com.mysql.cj.xdevapi;

import com.mysql.cj.exceptions.WrongArgumentException;
import com.mysql.cj.x.protobuf.MysqlxCrud;
import com.mysql.cj.x.protobuf.MysqlxExpr;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

public class ExprParser {
  private static HashMap<Character, Character> escapeChars = new HashMap<>();
  
  String string;
  
  static {
    escapeChars.put(Character.valueOf('"'), Character.valueOf('"'));
    escapeChars.put(Character.valueOf('\''), Character.valueOf('\''));
    escapeChars.put(Character.valueOf('`'), Character.valueOf('`'));
    escapeChars.put(Character.valueOf('\\'), Character.valueOf('\\'));
    escapeChars.put(Character.valueOf('/'), Character.valueOf('/'));
    escapeChars.put(Character.valueOf('b'), Character.valueOf('\b'));
    escapeChars.put(Character.valueOf('f'), Character.valueOf('\f'));
    escapeChars.put(Character.valueOf('n'), Character.valueOf('\n'));
    escapeChars.put(Character.valueOf('r'), Character.valueOf('\r'));
    escapeChars.put(Character.valueOf('t'), Character.valueOf('\t'));
  }
  
  List<Token> tokens = new ArrayList<>();
  
  int tokenPos = 0;
  
  Map<String, Integer> placeholderNameToPosition = new HashMap<>();
  
  int positionalPlaceholderCount = 0;
  
  private boolean allowRelationalColumns;
  
  public ExprParser(String s) {
    this(s, true);
  }
  
  public ExprParser(String s, boolean allowRelationalColumns) {
    this.string = s;
    lex();
    this.allowRelationalColumns = allowRelationalColumns;
  }
  
  private enum TokenType {
    NOT, AND, ANDAND, OR, OROR, XOR, IS, LPAREN, RPAREN, LSQBRACKET, RSQBRACKET, BETWEEN, TRUE, NULL, FALSE, IN, LIKE, INTERVAL, REGEXP, ESCAPE, IDENT, LSTRING, LNUM_INT, LNUM_DOUBLE, DOT, DOLLAR, COMMA, EQ, NE, GT, GE, LT, LE, BITAND, BITOR, BITXOR, LSHIFT, RSHIFT, PLUS, MINUS, STAR, SLASH, HEX, BIN, NEG, BANG, EROTEME, MICROSECOND, SECOND, MINUTE, HOUR, DAY, WEEK, MONTH, QUARTER, YEAR, SECOND_MICROSECOND, MINUTE_MICROSECOND, MINUTE_SECOND, HOUR_MICROSECOND, HOUR_SECOND, HOUR_MINUTE, DAY_MICROSECOND, DAY_SECOND, DAY_MINUTE, DAY_HOUR, YEAR_MONTH, DOUBLESTAR, MOD, COLON, ORDERBY_ASC, ORDERBY_DESC, AS, LCURLY, RCURLY, DOTSTAR, CAST, DECIMAL, UNSIGNED, SIGNED, INTEGER, DATE, TIME, DATETIME, CHAR, BINARY, JSON, COLDOCPATH, OVERLAPS;
  }
  
  static class Token {
    ExprParser.TokenType type;
    
    String value;
    
    public Token(ExprParser.TokenType x, char c) {
      this.type = x;
      this.value = new String(new char[] { c });
    }
    
    public Token(ExprParser.TokenType t, String v) {
      this.type = t;
      this.value = v;
    }
    
    public String toString() {
      if (this.type == ExprParser.TokenType.IDENT || this.type == ExprParser.TokenType.LNUM_INT || this.type == ExprParser.TokenType.LNUM_DOUBLE || this.type == ExprParser.TokenType.LSTRING)
        return this.type.toString() + "(" + this.value + ")"; 
      return this.type.toString();
    }
  }
  
  static Map<String, TokenType> reservedWords = new HashMap<>();
  
  static {
    reservedWords.put("and", TokenType.AND);
    reservedWords.put("or", TokenType.OR);
    reservedWords.put("xor", TokenType.XOR);
    reservedWords.put("is", TokenType.IS);
    reservedWords.put("not", TokenType.NOT);
    reservedWords.put("like", TokenType.LIKE);
    reservedWords.put("in", TokenType.IN);
    reservedWords.put("regexp", TokenType.REGEXP);
    reservedWords.put("between", TokenType.BETWEEN);
    reservedWords.put("interval", TokenType.INTERVAL);
    reservedWords.put("escape", TokenType.ESCAPE);
    reservedWords.put("div", TokenType.SLASH);
    reservedWords.put("hex", TokenType.HEX);
    reservedWords.put("bin", TokenType.BIN);
    reservedWords.put("true", TokenType.TRUE);
    reservedWords.put("false", TokenType.FALSE);
    reservedWords.put("null", TokenType.NULL);
    reservedWords.put("microsecond", TokenType.MICROSECOND);
    reservedWords.put("second", TokenType.SECOND);
    reservedWords.put("minute", TokenType.MINUTE);
    reservedWords.put("hour", TokenType.HOUR);
    reservedWords.put("day", TokenType.DAY);
    reservedWords.put("week", TokenType.WEEK);
    reservedWords.put("month", TokenType.MONTH);
    reservedWords.put("quarter", TokenType.QUARTER);
    reservedWords.put("year", TokenType.YEAR);
    reservedWords.put("second_microsecond", TokenType.SECOND_MICROSECOND);
    reservedWords.put("minute_microsecond", TokenType.MINUTE_MICROSECOND);
    reservedWords.put("minute_second", TokenType.MINUTE_SECOND);
    reservedWords.put("hour_microsecond", TokenType.HOUR_MICROSECOND);
    reservedWords.put("hour_second", TokenType.HOUR_SECOND);
    reservedWords.put("hour_minute", TokenType.HOUR_MINUTE);
    reservedWords.put("day_microsecond", TokenType.DAY_MICROSECOND);
    reservedWords.put("day_second", TokenType.DAY_SECOND);
    reservedWords.put("day_minute", TokenType.DAY_MINUTE);
    reservedWords.put("day_hour", TokenType.DAY_HOUR);
    reservedWords.put("year_month", TokenType.YEAR_MONTH);
    reservedWords.put("asc", TokenType.ORDERBY_ASC);
    reservedWords.put("desc", TokenType.ORDERBY_DESC);
    reservedWords.put("as", TokenType.AS);
    reservedWords.put("cast", TokenType.CAST);
    reservedWords.put("decimal", TokenType.DECIMAL);
    reservedWords.put("unsigned", TokenType.UNSIGNED);
    reservedWords.put("signed", TokenType.SIGNED);
    reservedWords.put("integer", TokenType.INTEGER);
    reservedWords.put("date", TokenType.DATE);
    reservedWords.put("time", TokenType.TIME);
    reservedWords.put("datetime", TokenType.DATETIME);
    reservedWords.put("char", TokenType.CHAR);
    reservedWords.put("binary", TokenType.BINARY);
    reservedWords.put("json", TokenType.BINARY);
    reservedWords.put("overlaps", TokenType.OVERLAPS);
  }
  
  boolean nextCharEquals(int i, char c) {
    return (i + 1 < this.string.length() && this.string.charAt(i + 1) == c);
  }
  
  private int lexNumber(int i) {
    boolean isInt = true;
    int start = i;
    for (; i < this.string.length(); i++) {
      char c = this.string.charAt(i);
      if (c == '.') {
        isInt = false;
      } else if (c == 'e' || c == 'E') {
        isInt = false;
        if (nextCharEquals(i, '-') || nextCharEquals(i, '+'))
          i++; 
      } else if (!Character.isDigit(c)) {
        break;
      } 
    } 
    if (isInt) {
      this.tokens.add(new Token(TokenType.LNUM_INT, this.string.substring(start, i)));
    } else {
      this.tokens.add(new Token(TokenType.LNUM_DOUBLE, this.string.substring(start, i)));
    } 
    i--;
    return i;
  }
  
  void lex() {
    for (int i = 0; i < this.string.length(); i++) {
      int start = i;
      char c = this.string.charAt(i);
      if (!Character.isWhitespace(c))
        if (Character.isDigit(c)) {
          i = lexNumber(i);
        } else if (c != '_' && !Character.isUnicodeIdentifierStart(c)) {
          char quoteChar;
          StringBuilder val;
          switch (c) {
            case ':':
              this.tokens.add(new Token(TokenType.COLON, c));
              break;
            case '+':
              this.tokens.add(new Token(TokenType.PLUS, c));
              break;
            case '-':
              if (nextCharEquals(i, '>')) {
                i++;
                this.tokens.add(new Token(TokenType.COLDOCPATH, "->"));
                break;
              } 
              this.tokens.add(new Token(TokenType.MINUS, c));
              break;
            case '*':
              if (nextCharEquals(i, '*')) {
                i++;
                this.tokens.add(new Token(TokenType.DOUBLESTAR, "**"));
                break;
              } 
              this.tokens.add(new Token(TokenType.STAR, c));
              break;
            case '/':
              this.tokens.add(new Token(TokenType.SLASH, c));
              break;
            case '$':
              this.tokens.add(new Token(TokenType.DOLLAR, c));
              break;
            case '%':
              this.tokens.add(new Token(TokenType.MOD, c));
              break;
            case '=':
              if (nextCharEquals(i, '='))
                i++; 
              this.tokens.add(new Token(TokenType.EQ, "=="));
              break;
            case '&':
              if (nextCharEquals(i, '&')) {
                i++;
                this.tokens.add(new Token(TokenType.ANDAND, "&&"));
                break;
              } 
              this.tokens.add(new Token(TokenType.BITAND, c));
              break;
            case '|':
              if (nextCharEquals(i, '|')) {
                i++;
                this.tokens.add(new Token(TokenType.OROR, "||"));
                break;
              } 
              this.tokens.add(new Token(TokenType.BITOR, c));
              break;
            case '^':
              this.tokens.add(new Token(TokenType.BITXOR, c));
              break;
            case '(':
              this.tokens.add(new Token(TokenType.LPAREN, c));
              break;
            case ')':
              this.tokens.add(new Token(TokenType.RPAREN, c));
              break;
            case '[':
              this.tokens.add(new Token(TokenType.LSQBRACKET, c));
              break;
            case ']':
              this.tokens.add(new Token(TokenType.RSQBRACKET, c));
              break;
            case '{':
              this.tokens.add(new Token(TokenType.LCURLY, c));
              break;
            case '}':
              this.tokens.add(new Token(TokenType.RCURLY, c));
              break;
            case '~':
              this.tokens.add(new Token(TokenType.NEG, c));
              break;
            case ',':
              this.tokens.add(new Token(TokenType.COMMA, c));
              break;
            case '!':
              if (nextCharEquals(i, '=')) {
                i++;
                this.tokens.add(new Token(TokenType.NE, "!="));
                break;
              } 
              this.tokens.add(new Token(TokenType.BANG, c));
              break;
            case '?':
              this.tokens.add(new Token(TokenType.EROTEME, c));
              break;
            case '<':
              if (nextCharEquals(i, '<')) {
                i++;
                this.tokens.add(new Token(TokenType.LSHIFT, "<<"));
                break;
              } 
              if (nextCharEquals(i, '=')) {
                i++;
                this.tokens.add(new Token(TokenType.LE, "<="));
                break;
              } 
              this.tokens.add(new Token(TokenType.LT, c));
              break;
            case '>':
              if (nextCharEquals(i, '>')) {
                i++;
                this.tokens.add(new Token(TokenType.RSHIFT, ">>"));
                break;
              } 
              if (nextCharEquals(i, '=')) {
                i++;
                this.tokens.add(new Token(TokenType.GE, ">="));
                break;
              } 
              this.tokens.add(new Token(TokenType.GT, c));
              break;
            case '.':
              if (nextCharEquals(i, '*')) {
                i++;
                this.tokens.add(new Token(TokenType.DOTSTAR, ".*"));
                break;
              } 
              if (i + 1 < this.string.length() && Character.isDigit(this.string.charAt(i + 1))) {
                i = lexNumber(i);
                break;
              } 
              this.tokens.add(new Token(TokenType.DOT, c));
              break;
            case '"':
            case '\'':
            case '`':
              quoteChar = c;
              val = new StringBuilder();
              try {
                boolean escapeNextChar = false;
                c = this.string.charAt(++i);
                for (; c != quoteChar || escapeNextChar || (i + 1 < this.string.length() && this.string.charAt(i + 1) == quoteChar); c = this.string.charAt(++i)) {
                  if (escapeNextChar) {
                    if (escapeChars.containsKey(Character.valueOf(c))) {
                      val.append(escapeChars.get(Character.valueOf(c)));
                    } else if (c == 'u') {
                      char[] buf = new char[4];
                      this.string.getChars(++i, i + 4, buf, 0);
                      String hexCodePoint = String.valueOf(buf);
                      try {
                        val.append((char)Integer.parseInt(hexCodePoint, 16));
                      } catch (NumberFormatException e) {
                        throw new WrongArgumentException("Invalid Unicode code point '" + hexCodePoint + "'");
                      } 
                      i += 3;
                    } else {
                      val.append('\\').append(c);
                    } 
                    escapeNextChar = false;
                  } else if (c == '\\' || c == quoteChar) {
                    escapeNextChar = true;
                  } else {
                    val.append(c);
                  } 
                } 
                if (escapeNextChar)
                  throw new WrongArgumentException("Unterminated escape sequence at " + i); 
              } catch (StringIndexOutOfBoundsException ex) {
                throw new WrongArgumentException("Unterminated string starting at " + start);
              } 
              this.tokens.add(new Token((quoteChar == '`') ? TokenType.IDENT : TokenType.LSTRING, val.toString()));
              break;
            default:
              throw new WrongArgumentException("Can't parse at position " + i);
          } 
        } else {
          for (; i < this.string.length() && Character.isUnicodeIdentifierPart(this.string.charAt(i)); i++);
          String val = this.string.substring(start, i);
          String valLower = val.toLowerCase();
          if (i < this.string.length())
            i--; 
          if (reservedWords.containsKey(valLower)) {
            if ("and".equals(valLower)) {
              this.tokens.add(new Token(reservedWords.get(valLower), "&&"));
            } else if ("or".equals(valLower)) {
              this.tokens.add(new Token(reservedWords.get(valLower), "||"));
            } else {
              this.tokens.add(new Token(reservedWords.get(valLower), valLower));
            } 
          } else {
            this.tokens.add(new Token(TokenType.IDENT, val));
          } 
        }  
    } 
  }
  
  void assertTokenAt(int pos, TokenType type) {
    if (this.tokens.size() <= pos)
      throw new WrongArgumentException("No more tokens when expecting " + type + " at token position " + pos); 
    if (((Token)this.tokens.get(pos)).type != type)
      throw new WrongArgumentException("Expected token type " + type + " at token position " + pos); 
  }
  
  boolean currentTokenTypeEquals(TokenType t) {
    return posTokenTypeEquals(this.tokenPos, t);
  }
  
  boolean nextTokenTypeEquals(TokenType t) {
    return posTokenTypeEquals(this.tokenPos + 1, t);
  }
  
  boolean posTokenTypeEquals(int pos, TokenType t) {
    return (this.tokens.size() > pos && ((Token)this.tokens.get(pos)).type == t);
  }
  
  String consumeToken(TokenType t) {
    assertTokenAt(this.tokenPos, t);
    String value = ((Token)this.tokens.get(this.tokenPos)).value;
    this.tokenPos++;
    return value;
  }
  
  List<MysqlxExpr.Expr> parenExprList() {
    List<MysqlxExpr.Expr> exprs = new ArrayList<>();
    consumeToken(TokenType.LPAREN);
    if (!currentTokenTypeEquals(TokenType.RPAREN)) {
      exprs.add(expr());
      while (currentTokenTypeEquals(TokenType.COMMA)) {
        consumeToken(TokenType.COMMA);
        exprs.add(expr());
      } 
    } 
    consumeToken(TokenType.RPAREN);
    return exprs;
  }
  
  MysqlxExpr.Expr functionCall() {
    MysqlxExpr.Identifier id = identifier();
    MysqlxExpr.FunctionCall.Builder b = MysqlxExpr.FunctionCall.newBuilder();
    b.setName(id);
    b.addAllParam(parenExprList());
    return MysqlxExpr.Expr.newBuilder().setType(MysqlxExpr.Expr.Type.FUNC_CALL).setFunctionCall(b.build()).build();
  }
  
  MysqlxExpr.Expr starOperator() {
    MysqlxExpr.Operator op = MysqlxExpr.Operator.newBuilder().setName("*").build();
    return MysqlxExpr.Expr.newBuilder().setType(MysqlxExpr.Expr.Type.OPERATOR).setOperator(op).build();
  }
  
  MysqlxExpr.Identifier identifier() {
    MysqlxExpr.Identifier.Builder builder = MysqlxExpr.Identifier.newBuilder();
    assertTokenAt(this.tokenPos, TokenType.IDENT);
    if (nextTokenTypeEquals(TokenType.DOT)) {
      builder.setSchemaName(((Token)this.tokens.get(this.tokenPos)).value);
      consumeToken(TokenType.IDENT);
      consumeToken(TokenType.DOT);
      assertTokenAt(this.tokenPos, TokenType.IDENT);
    } 
    builder.setName(((Token)this.tokens.get(this.tokenPos)).value);
    consumeToken(TokenType.IDENT);
    return builder.build();
  }
  
  MysqlxExpr.DocumentPathItem docPathMember() {
    String memberName;
    consumeToken(TokenType.DOT);
    Token t = this.tokens.get(this.tokenPos);
    if (currentTokenTypeEquals(TokenType.IDENT)) {
      if (!t.value.equals(ExprUnparser.quoteIdentifier(t.value)))
        throw new WrongArgumentException("'" + t.value + "' is not a valid JSON/ECMAScript identifier"); 
      consumeToken(TokenType.IDENT);
      memberName = t.value;
    } else if (currentTokenTypeEquals(TokenType.LSTRING)) {
      consumeToken(TokenType.LSTRING);
      memberName = t.value;
    } else {
      throw new WrongArgumentException("Expected token type IDENT or LSTRING in JSON path at token position " + this.tokenPos);
    } 
    MysqlxExpr.DocumentPathItem.Builder item = MysqlxExpr.DocumentPathItem.newBuilder();
    item.setType(MysqlxExpr.DocumentPathItem.Type.MEMBER);
    item.setValue(memberName);
    return item.build();
  }
  
  MysqlxExpr.DocumentPathItem docPathArrayLoc() {
    MysqlxExpr.DocumentPathItem.Builder builder = MysqlxExpr.DocumentPathItem.newBuilder();
    consumeToken(TokenType.LSQBRACKET);
    if (currentTokenTypeEquals(TokenType.STAR)) {
      consumeToken(TokenType.STAR);
      consumeToken(TokenType.RSQBRACKET);
      return builder.setType(MysqlxExpr.DocumentPathItem.Type.ARRAY_INDEX_ASTERISK).build();
    } 
    if (currentTokenTypeEquals(TokenType.LNUM_INT)) {
      Integer v = Integer.valueOf(((Token)this.tokens.get(this.tokenPos)).value);
      if (v.intValue() < 0)
        throw new WrongArgumentException("Array index cannot be negative at " + this.tokenPos); 
      consumeToken(TokenType.LNUM_INT);
      consumeToken(TokenType.RSQBRACKET);
      return builder.setType(MysqlxExpr.DocumentPathItem.Type.ARRAY_INDEX).setIndex(v.intValue()).build();
    } 
    throw new WrongArgumentException("Expected token type STAR or LNUM_INT in JSON path array index at token position " + this.tokenPos);
  }
  
  public List<MysqlxExpr.DocumentPathItem> documentPath() {
    List<MysqlxExpr.DocumentPathItem> items = new ArrayList<>();
    while (true) {
      while (currentTokenTypeEquals(TokenType.DOT))
        items.add(docPathMember()); 
      if (currentTokenTypeEquals(TokenType.DOTSTAR)) {
        consumeToken(TokenType.DOTSTAR);
        items.add(MysqlxExpr.DocumentPathItem.newBuilder().setType(MysqlxExpr.DocumentPathItem.Type.MEMBER_ASTERISK).build());
        continue;
      } 
      if (currentTokenTypeEquals(TokenType.LSQBRACKET)) {
        items.add(docPathArrayLoc());
        continue;
      } 
      if (currentTokenTypeEquals(TokenType.DOUBLESTAR)) {
        consumeToken(TokenType.DOUBLESTAR);
        items.add(MysqlxExpr.DocumentPathItem.newBuilder().setType(MysqlxExpr.DocumentPathItem.Type.DOUBLE_ASTERISK).build());
        continue;
      } 
      break;
    } 
    if (items.size() > 0 && ((MysqlxExpr.DocumentPathItem)items.get(items.size() - 1)).getType() == MysqlxExpr.DocumentPathItem.Type.DOUBLE_ASTERISK)
      throw new WrongArgumentException("JSON path may not end in '**' at " + this.tokenPos); 
    return items;
  }
  
  public MysqlxExpr.Expr documentField() {
    MysqlxExpr.ColumnIdentifier.Builder builder = MysqlxExpr.ColumnIdentifier.newBuilder();
    if (currentTokenTypeEquals(TokenType.IDENT))
      builder.addDocumentPath(MysqlxExpr.DocumentPathItem.newBuilder().setType(MysqlxExpr.DocumentPathItem.Type.MEMBER).setValue(consumeToken(TokenType.IDENT)).build()); 
    builder.addAllDocumentPath(documentPath());
    return MysqlxExpr.Expr.newBuilder().setType(MysqlxExpr.Expr.Type.IDENT).setIdentifier(builder.build()).build();
  }
  
  MysqlxExpr.Expr columnIdentifier() {
    List<String> parts = new LinkedList<>();
    parts.add(consumeToken(TokenType.IDENT));
    while (currentTokenTypeEquals(TokenType.DOT)) {
      consumeToken(TokenType.DOT);
      parts.add(consumeToken(TokenType.IDENT));
      if (parts.size() == 3)
        break; 
    } 
    Collections.reverse(parts);
    MysqlxExpr.ColumnIdentifier.Builder id = MysqlxExpr.ColumnIdentifier.newBuilder();
    for (int i = 0; i < parts.size(); i++) {
      switch (i) {
        case 0:
          id.setName(parts.get(0));
          break;
        case 1:
          id.setTableName(parts.get(1));
          break;
        case 2:
          id.setSchemaName(parts.get(2));
          break;
      } 
    } 
    if (currentTokenTypeEquals(TokenType.COLDOCPATH)) {
      consumeToken(TokenType.COLDOCPATH);
      if (currentTokenTypeEquals(TokenType.DOLLAR)) {
        consumeToken(TokenType.DOLLAR);
        id.addAllDocumentPath(documentPath());
      } else if (currentTokenTypeEquals(TokenType.LSTRING)) {
        String path = consumeToken(TokenType.LSTRING);
        if (path.charAt(0) != '$')
          throw new WrongArgumentException("Invalid document path at " + this.tokenPos); 
        id.addAllDocumentPath((new ExprParser(path.substring(1, path.length()))).documentPath());
      } 
      if (id.getDocumentPathCount() == 0)
        throw new WrongArgumentException("Invalid document path at " + this.tokenPos); 
    } 
    return MysqlxExpr.Expr.newBuilder().setType(MysqlxExpr.Expr.Type.IDENT).setIdentifier(id.build()).build();
  }
  
  MysqlxExpr.Expr buildUnaryOp(String name, MysqlxExpr.Expr param) {
    String opName = "-".equals(name) ? "sign_minus" : ("+".equals(name) ? "sign_plus" : name);
    MysqlxExpr.Operator op = MysqlxExpr.Operator.newBuilder().setName(opName).addParam(param).build();
    return MysqlxExpr.Expr.newBuilder().setType(MysqlxExpr.Expr.Type.OPERATOR).setOperator(op).build();
  }
  
  MysqlxExpr.Expr atomicExpr() {
    String placeholderName;
    MysqlxExpr.Expr e;
    MysqlxExpr.Object.Builder builder2;
    MysqlxExpr.Array.Builder builder1;
    MysqlxExpr.Operator.Builder builder;
    MysqlxExpr.Expr.Builder placeholder;
    StringBuilder typeStr;
    if (this.tokenPos >= this.tokens.size())
      throw new WrongArgumentException("No more tokens when expecting one at token position " + this.tokenPos); 
    Token t = this.tokens.get(this.tokenPos);
    this.tokenPos++;
    switch (t.type) {
      case EROTEME:
      case COLON:
        if (currentTokenTypeEquals(TokenType.LNUM_INT)) {
          placeholderName = consumeToken(TokenType.LNUM_INT);
        } else if (currentTokenTypeEquals(TokenType.IDENT)) {
          placeholderName = consumeToken(TokenType.IDENT);
        } else if (t.type == TokenType.EROTEME) {
          placeholderName = String.valueOf(this.positionalPlaceholderCount);
        } else {
          throw new WrongArgumentException("Invalid placeholder name at token position " + this.tokenPos);
        } 
        placeholder = MysqlxExpr.Expr.newBuilder().setType(MysqlxExpr.Expr.Type.PLACEHOLDER);
        if (this.placeholderNameToPosition.containsKey(placeholderName)) {
          placeholder.setPosition(((Integer)this.placeholderNameToPosition.get(placeholderName)).intValue());
        } else {
          placeholder.setPosition(this.positionalPlaceholderCount);
          this.placeholderNameToPosition.put(placeholderName, Integer.valueOf(this.positionalPlaceholderCount));
          this.positionalPlaceholderCount++;
        } 
        return placeholder.build();
      case LPAREN:
        e = expr();
        consumeToken(TokenType.RPAREN);
        return e;
      case LCURLY:
        builder2 = MysqlxExpr.Object.newBuilder();
        if (currentTokenTypeEquals(TokenType.LSTRING))
          parseCommaSeparatedList(() -> {
                String key = consumeToken(TokenType.LSTRING);
                consumeToken(TokenType.COLON);
                MysqlxExpr.Expr value = expr();
                return Collections.singletonMap(key, value);
              }).stream().map(pair -> (Map.Entry)pair.entrySet().iterator().next()).map(e -> MysqlxExpr.Object.ObjectField.newBuilder().setKey((String)e.getKey()).setValue((MysqlxExpr.Expr)e.getValue()))
            .forEach(builder2::addFld); 
        consumeToken(TokenType.RCURLY);
        return MysqlxExpr.Expr.newBuilder().setType(MysqlxExpr.Expr.Type.OBJECT).setObject(builder2.build()).build();
      case LSQBRACKET:
        builder1 = MysqlxExpr.Expr.newBuilder().setType(MysqlxExpr.Expr.Type.ARRAY).getArrayBuilder();
        if (!currentTokenTypeEquals(TokenType.RSQBRACKET))
          parseCommaSeparatedList(() -> expr())
            
            .stream().forEach(builder1::addValue); 
        consumeToken(TokenType.RSQBRACKET);
        return MysqlxExpr.Expr.newBuilder().setType(MysqlxExpr.Expr.Type.ARRAY).setArray(builder1).build();
      case CAST:
        consumeToken(TokenType.LPAREN);
        builder = MysqlxExpr.Operator.newBuilder().setName(TokenType.CAST.toString().toLowerCase());
        builder.addParam(expr());
        consumeToken(TokenType.AS);
        typeStr = new StringBuilder(((Token)this.tokens.get(this.tokenPos)).value.toUpperCase());
        if (currentTokenTypeEquals(TokenType.DECIMAL)) {
          this.tokenPos++;
          if (currentTokenTypeEquals(TokenType.LPAREN)) {
            typeStr.append(consumeToken(TokenType.LPAREN));
            typeStr.append(consumeToken(TokenType.LNUM_INT));
            if (currentTokenTypeEquals(TokenType.COMMA)) {
              typeStr.append(consumeToken(TokenType.COMMA));
              typeStr.append(consumeToken(TokenType.LNUM_INT));
            } 
            typeStr.append(consumeToken(TokenType.RPAREN));
          } 
        } else if (currentTokenTypeEquals(TokenType.CHAR) || currentTokenTypeEquals(TokenType.BINARY)) {
          this.tokenPos++;
          if (currentTokenTypeEquals(TokenType.LPAREN)) {
            typeStr.append(consumeToken(TokenType.LPAREN));
            typeStr.append(consumeToken(TokenType.LNUM_INT));
            typeStr.append(consumeToken(TokenType.RPAREN));
          } 
        } else if (currentTokenTypeEquals(TokenType.UNSIGNED) || currentTokenTypeEquals(TokenType.SIGNED)) {
          this.tokenPos++;
          if (currentTokenTypeEquals(TokenType.INTEGER))
            consumeToken(TokenType.INTEGER); 
        } else if (currentTokenTypeEquals(TokenType.JSON) || currentTokenTypeEquals(TokenType.DATE) || currentTokenTypeEquals(TokenType.DATETIME) || 
          currentTokenTypeEquals(TokenType.TIME)) {
          this.tokenPos++;
        } else {
          throw new WrongArgumentException("Expected valid CAST type argument at " + this.tokenPos);
        } 
        consumeToken(TokenType.RPAREN);
        builder.addParam(ExprUtil.buildLiteralScalar(typeStr.toString().getBytes()));
        return MysqlxExpr.Expr.newBuilder().setType(MysqlxExpr.Expr.Type.OPERATOR).setOperator(builder.build()).build();
      case PLUS:
      case MINUS:
        if (currentTokenTypeEquals(TokenType.LNUM_INT) || currentTokenTypeEquals(TokenType.LNUM_DOUBLE)) {
          t.value += ((Token)this.tokens.get(this.tokenPos)).value;
          return atomicExpr();
        } 
        return buildUnaryOp(t.value, atomicExpr());
      case NOT:
      case NEG:
      case BANG:
        return buildUnaryOp(t.value, atomicExpr());
      case LSTRING:
        return ExprUtil.buildLiteralScalar(t.value);
      case NULL:
        return ExprUtil.buildLiteralNullScalar();
      case LNUM_INT:
        return ExprUtil.buildLiteralScalar(Long.parseLong(t.value));
      case LNUM_DOUBLE:
        return ExprUtil.buildLiteralScalar(Double.parseDouble(t.value));
      case TRUE:
      case FALSE:
        return ExprUtil.buildLiteralScalar((t.type == TokenType.TRUE));
      case DOLLAR:
        return documentField();
      case STAR:
        return starOperator();
      case IDENT:
        this.tokenPos--;
        if (nextTokenTypeEquals(TokenType.LPAREN) || (posTokenTypeEquals(this.tokenPos + 1, TokenType.DOT) && 
          posTokenTypeEquals(this.tokenPos + 2, TokenType.IDENT) && posTokenTypeEquals(this.tokenPos + 3, TokenType.LPAREN)))
          return functionCall(); 
        if (this.allowRelationalColumns)
          return columnIdentifier(); 
        return documentField();
    } 
    throw new WrongArgumentException("Cannot find atomic expression at token position " + (this.tokenPos - 1));
  }
  
  @FunctionalInterface
  static interface ParseExpr {
    MysqlxExpr.Expr parseExpr();
  }
  
  MysqlxExpr.Expr parseLeftAssocBinaryOpExpr(TokenType[] types, ParseExpr innerParser) {
    MysqlxExpr.Expr lhs = innerParser.parseExpr();
    while (this.tokenPos < this.tokens.size() && Arrays.<TokenType>asList(types).contains(((Token)this.tokens.get(this.tokenPos)).type)) {
      MysqlxExpr.Operator.Builder builder = MysqlxExpr.Operator.newBuilder().setName(((Token)this.tokens.get(this.tokenPos)).value).addParam(lhs);
      this.tokenPos++;
      builder.addParam(innerParser.parseExpr());
      lhs = MysqlxExpr.Expr.newBuilder().setType(MysqlxExpr.Expr.Type.OPERATOR).setOperator(builder.build()).build();
    } 
    return lhs;
  }
  
  MysqlxExpr.Expr addSubIntervalExpr() {
    MysqlxExpr.Expr lhs = atomicExpr();
    while ((currentTokenTypeEquals(TokenType.PLUS) || currentTokenTypeEquals(TokenType.MINUS)) && nextTokenTypeEquals(TokenType.INTERVAL)) {
      Token op = this.tokens.get(this.tokenPos);
      this.tokenPos++;
      MysqlxExpr.Operator.Builder builder = MysqlxExpr.Operator.newBuilder().addParam(lhs);
      consumeToken(TokenType.INTERVAL);
      if (op.type == TokenType.PLUS) {
        builder.setName("date_add");
      } else {
        builder.setName("date_sub");
      } 
      builder.addParam(bitExpr());
      if (currentTokenTypeEquals(TokenType.MICROSECOND) || currentTokenTypeEquals(TokenType.SECOND) || currentTokenTypeEquals(TokenType.MINUTE) || 
        currentTokenTypeEquals(TokenType.HOUR) || currentTokenTypeEquals(TokenType.DAY) || currentTokenTypeEquals(TokenType.WEEK) || 
        currentTokenTypeEquals(TokenType.MONTH) || currentTokenTypeEquals(TokenType.QUARTER) || currentTokenTypeEquals(TokenType.YEAR) || 
        currentTokenTypeEquals(TokenType.SECOND_MICROSECOND) || currentTokenTypeEquals(TokenType.MINUTE_MICROSECOND) || 
        currentTokenTypeEquals(TokenType.MINUTE_SECOND) || currentTokenTypeEquals(TokenType.HOUR_MICROSECOND) || 
        currentTokenTypeEquals(TokenType.HOUR_SECOND) || currentTokenTypeEquals(TokenType.HOUR_MINUTE) || 
        currentTokenTypeEquals(TokenType.DAY_MICROSECOND) || currentTokenTypeEquals(TokenType.DAY_SECOND) || 
        currentTokenTypeEquals(TokenType.DAY_MINUTE) || currentTokenTypeEquals(TokenType.DAY_HOUR) || 
        currentTokenTypeEquals(TokenType.YEAR_MONTH)) {
        builder.addParam(ExprUtil.buildLiteralScalar(((Token)this.tokens.get(this.tokenPos)).value.toUpperCase().getBytes()));
        this.tokenPos++;
        lhs = MysqlxExpr.Expr.newBuilder().setType(MysqlxExpr.Expr.Type.OPERATOR).setOperator(builder.build()).build();
        continue;
      } 
      throw new WrongArgumentException("Expected interval units at " + this.tokenPos);
    } 
    return lhs;
  }
  
  MysqlxExpr.Expr mulDivExpr() {
    return parseLeftAssocBinaryOpExpr(new TokenType[] { TokenType.STAR, TokenType.SLASH, TokenType.MOD }, this::addSubIntervalExpr);
  }
  
  MysqlxExpr.Expr addSubExpr() {
    return parseLeftAssocBinaryOpExpr(new TokenType[] { TokenType.PLUS, TokenType.MINUS }, this::mulDivExpr);
  }
  
  MysqlxExpr.Expr shiftExpr() {
    return parseLeftAssocBinaryOpExpr(new TokenType[] { TokenType.LSHIFT, TokenType.RSHIFT }, this::addSubExpr);
  }
  
  MysqlxExpr.Expr bitExpr() {
    return parseLeftAssocBinaryOpExpr(new TokenType[] { TokenType.BITAND, TokenType.BITOR, TokenType.BITXOR }, this::shiftExpr);
  }
  
  MysqlxExpr.Expr compExpr() {
    return parseLeftAssocBinaryOpExpr(new TokenType[] { TokenType.GE, TokenType.GT, TokenType.LE, TokenType.LT, TokenType.EQ, TokenType.NE }, this::bitExpr);
  }
  
  MysqlxExpr.Expr ilriExpr() {
    MysqlxExpr.Expr lhs = compExpr();
    List<TokenType> expected = Arrays.asList(new TokenType[] { TokenType.IS, TokenType.IN, TokenType.LIKE, TokenType.BETWEEN, TokenType.REGEXP, TokenType.NOT, TokenType.OVERLAPS });
    while (this.tokenPos < this.tokens.size() && expected.contains(((Token)this.tokens.get(this.tokenPos)).type)) {
      boolean isNot = false;
      if (currentTokenTypeEquals(TokenType.NOT)) {
        consumeToken(TokenType.NOT);
        isNot = true;
      } 
      if (this.tokenPos < this.tokens.size()) {
        List<MysqlxExpr.Expr> params = new ArrayList<>();
        params.add(lhs);
        String opName = ((Token)this.tokens.get(this.tokenPos)).value.toLowerCase();
        switch (((Token)this.tokens.get(this.tokenPos)).type) {
          case IS:
            consumeToken(TokenType.IS);
            if (currentTokenTypeEquals(TokenType.NOT)) {
              consumeToken(TokenType.NOT);
              opName = "is_not";
            } 
            params.add(compExpr());
            break;
          case IN:
            consumeToken(TokenType.IN);
            if (currentTokenTypeEquals(TokenType.LPAREN)) {
              params.addAll(parenExprList());
              break;
            } 
            opName = "cont_in";
            params.add(compExpr());
            break;
          case LIKE:
            consumeToken(TokenType.LIKE);
            params.add(compExpr());
            if (currentTokenTypeEquals(TokenType.ESCAPE)) {
              consumeToken(TokenType.ESCAPE);
              params.add(compExpr());
            } 
            break;
          case BETWEEN:
            consumeToken(TokenType.BETWEEN);
            params.add(compExpr());
            assertTokenAt(this.tokenPos, TokenType.AND);
            consumeToken(TokenType.AND);
            params.add(compExpr());
            break;
          case REGEXP:
            consumeToken(TokenType.REGEXP);
            params.add(compExpr());
            break;
          case OVERLAPS:
            consumeToken(TokenType.OVERLAPS);
            params.add(compExpr());
            break;
          default:
            throw new WrongArgumentException("Unknown token after NOT at position " + this.tokenPos);
        } 
        if (isNot)
          opName = "not_" + opName; 
        MysqlxExpr.Operator.Builder builder = MysqlxExpr.Operator.newBuilder().setName(opName).addAllParam(params);
        lhs = MysqlxExpr.Expr.newBuilder().setType(MysqlxExpr.Expr.Type.OPERATOR).setOperator(builder.build()).build();
      } 
    } 
    return lhs;
  }
  
  MysqlxExpr.Expr andExpr() {
    return parseLeftAssocBinaryOpExpr(new TokenType[] { TokenType.AND, TokenType.ANDAND }, this::ilriExpr);
  }
  
  MysqlxExpr.Expr orExpr() {
    return parseLeftAssocBinaryOpExpr(new TokenType[] { TokenType.OR, TokenType.OROR }, this::andExpr);
  }
  
  MysqlxExpr.Expr expr() {
    MysqlxExpr.Expr e = orExpr();
    return e;
  }
  
  public MysqlxExpr.Expr parse() {
    try {
      MysqlxExpr.Expr e = expr();
      if (this.tokenPos != this.tokens.size())
        throw new WrongArgumentException("Only " + this.tokenPos + " tokens consumed, out of " + this.tokens.size()); 
      return e;
    } catch (IllegalArgumentException ex) {
      throw new WrongArgumentException("Unable to parse query '" + this.string + "'", ex);
    } 
  }
  
  private <T> List<T> parseCommaSeparatedList(Supplier<T> elementParser) {
    List<T> elements = new ArrayList<>();
    boolean first = true;
    while (first || currentTokenTypeEquals(TokenType.COMMA)) {
      if (!first) {
        consumeToken(TokenType.COMMA);
      } else {
        first = false;
      } 
      elements.add(elementParser.get());
    } 
    return elements;
  }
  
  public List<MysqlxCrud.Order> parseOrderSpec() {
    return parseCommaSeparatedList(() -> {
          MysqlxCrud.Order.Builder builder = MysqlxCrud.Order.newBuilder();
          builder.setExpr(expr());
          if (currentTokenTypeEquals(TokenType.ORDERBY_ASC)) {
            consumeToken(TokenType.ORDERBY_ASC);
            builder.setDirection(MysqlxCrud.Order.Direction.ASC);
          } else if (currentTokenTypeEquals(TokenType.ORDERBY_DESC)) {
            consumeToken(TokenType.ORDERBY_DESC);
            builder.setDirection(MysqlxCrud.Order.Direction.DESC);
          } 
          return builder.build();
        });
  }
  
  public List<MysqlxCrud.Projection> parseTableSelectProjection() {
    return parseCommaSeparatedList(() -> {
          MysqlxCrud.Projection.Builder builder = MysqlxCrud.Projection.newBuilder();
          builder.setSource(expr());
          if (currentTokenTypeEquals(TokenType.AS)) {
            consumeToken(TokenType.AS);
            builder.setAlias(consumeToken(TokenType.IDENT));
          } 
          return builder.build();
        });
  }
  
  public MysqlxCrud.Column parseTableInsertField() {
    return MysqlxCrud.Column.newBuilder().setName(consumeToken(TokenType.IDENT)).build();
  }
  
  public MysqlxExpr.ColumnIdentifier parseTableUpdateField() {
    return columnIdentifier().getIdentifier();
  }
  
  public List<MysqlxCrud.Projection> parseDocumentProjection() {
    this.allowRelationalColumns = false;
    return parseCommaSeparatedList(() -> {
          MysqlxCrud.Projection.Builder builder = MysqlxCrud.Projection.newBuilder();
          builder.setSource(expr());
          consumeToken(TokenType.AS);
          builder.setAlias(consumeToken(TokenType.IDENT));
          return builder.build();
        });
  }
  
  public List<MysqlxExpr.Expr> parseExprList() {
    return parseCommaSeparatedList(this::expr);
  }
  
  public int getPositionalPlaceholderCount() {
    return this.positionalPlaceholderCount;
  }
  
  public Map<String, Integer> getPlaceholderNameToPositionMap() {
    return Collections.unmodifiableMap(this.placeholderNameToPosition);
  }
}
