package org.codehaus.plexus.util;

import java.io.FilterReader;
import java.io.IOException;
import java.io.PushbackReader;
import java.io.Reader;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import org.codehaus.plexus.util.reflection.Reflector;
import org.codehaus.plexus.util.reflection.ReflectorException;

public class LineOrientedInterpolatingReader extends FilterReader {
  public static final String DEFAULT_START_DELIM = "${";
  
  public static final String DEFAULT_END_DELIM = "}";
  
  public static final String DEFAULT_ESCAPE_SEQ = "\\";
  
  private static final char CARRIAGE_RETURN_CHAR = '\r';
  
  private static final char NEWLINE_CHAR = '\n';
  
  private final PushbackReader pushbackReader;
  
  private final Map<String, Object> context;
  
  private final String startDelim;
  
  private final String endDelim;
  
  private final String escapeSeq;
  
  private final int minExpressionSize;
  
  private final Reflector reflector;
  
  private int lineIdx = -1;
  
  private String line;
  
  public LineOrientedInterpolatingReader(Reader reader, Map<String, ?> context, String startDelim, String endDelim, String escapeSeq) {
    super(reader);
    this.startDelim = startDelim;
    this.endDelim = endDelim;
    this.escapeSeq = escapeSeq;
    this.minExpressionSize = startDelim.length() + endDelim.length() + 1;
    this.context = Collections.unmodifiableMap(context);
    this.reflector = new Reflector();
    if (reader instanceof PushbackReader) {
      this.pushbackReader = (PushbackReader)reader;
    } else {
      this.pushbackReader = new PushbackReader(reader, 1);
    } 
  }
  
  public LineOrientedInterpolatingReader(Reader reader, Map<String, ?> context, String startDelim, String endDelim) {
    this(reader, context, startDelim, endDelim, "\\");
  }
  
  public LineOrientedInterpolatingReader(Reader reader, Map<String, ?> context) {
    this(reader, context, "${", "}", "\\");
  }
  
  public int read() throws IOException {
    if (this.line == null || this.lineIdx >= this.line.length())
      readAndInterpolateLine(); 
    int next = -1;
    if (this.line != null && this.lineIdx < this.line.length())
      next = this.line.charAt(this.lineIdx++); 
    return next;
  }
  
  public int read(char[] cbuf, int off, int len) throws IOException {
    int fillCount = 0;
    for (int i = off; i < off + len; ) {
      int next = read();
      if (next > -1) {
        cbuf[i] = (char)next;
        fillCount++;
        i++;
      } 
    } 
    if (fillCount == 0)
      fillCount = -1; 
    return fillCount;
  }
  
  public long skip(long n) throws IOException {
    long skipCount = 0L;
    long i;
    for (i = 0L; i < n; i++) {
      int next = read();
      if (next < 0)
        break; 
      skipCount++;
    } 
    return skipCount;
  }
  
  private void readAndInterpolateLine() throws IOException {
    String rawLine = readLine();
    if (rawLine != null) {
      Set<String> expressions = parseForExpressions(rawLine);
      Map<String, Object> evaluatedExpressions = evaluateExpressions(expressions);
      String interpolated = replaceWithInterpolatedValues(rawLine, evaluatedExpressions);
      if (interpolated != null && interpolated.length() > 0) {
        this.line = interpolated;
        this.lineIdx = 0;
      } 
    } else {
      this.line = null;
      this.lineIdx = -1;
    } 
  }
  
  private String readLine() throws IOException {
    StringBuilder lineBuffer = new StringBuilder(40);
    boolean lastWasCR = false;
    int next;
    while ((next = this.pushbackReader.read()) > -1) {
      char c = (char)next;
      if (c == '\r') {
        lastWasCR = true;
        lineBuffer.append(c);
        continue;
      } 
      if (c == '\n') {
        lineBuffer.append(c);
        break;
      } 
      if (lastWasCR) {
        this.pushbackReader.unread(c);
        break;
      } 
      lineBuffer.append(c);
    } 
    if (lineBuffer.length() < 1)
      return null; 
    return lineBuffer.toString();
  }
  
  private String replaceWithInterpolatedValues(String rawLine, Map<String, Object> evaluatedExpressions) {
    String result = rawLine;
    for (Map.Entry<String, Object> o : evaluatedExpressions.entrySet()) {
      Map.Entry entry = o;
      String expression = (String)entry.getKey();
      String value = String.valueOf(entry.getValue());
      result = findAndReplaceUnlessEscaped(result, expression, value);
    } 
    return result;
  }
  
  private Map<String, Object> evaluateExpressions(Set<String> expressions) {
    Map<String, Object> evaluated = new TreeMap<String, Object>();
    for (String expression : expressions) {
      String rawExpression = expression;
      String realExpression = rawExpression.substring(this.startDelim.length(), rawExpression.length() - this.endDelim.length());
      String[] parts = realExpression.split("\\.");
      if (parts.length > 0) {
        Object value = this.context.get(parts[0]);
        if (value != null) {
          for (int i = 1; i < parts.length; i++) {
            try {
              value = this.reflector.getObjectProperty(value, parts[i]);
              if (value == null)
                break; 
            } catch (ReflectorException e) {
              e.printStackTrace();
              break;
            } 
          } 
          evaluated.put(rawExpression, value);
        } 
      } 
    } 
    return evaluated;
  }
  
  private Set<String> parseForExpressions(String rawLine) {
    Set<String> expressions = new HashSet<String>();
    if (rawLine != null) {
      int placeholder = -1;
      do {
        int start = findDelimiter(rawLine, this.startDelim, placeholder);
        if (start < 0)
          break; 
        int end = findDelimiter(rawLine, this.endDelim, start + 1);
        if (end < 0)
          break; 
        expressions.add(rawLine.substring(start, end + this.endDelim.length()));
        placeholder = end + 1;
      } while (placeholder < rawLine.length() - this.minExpressionSize);
    } 
    return expressions;
  }
  
  private int findDelimiter(String rawLine, String delimiter, int lastPos) {
    int position, placeholder = lastPos;
    do {
      position = rawLine.indexOf(delimiter, placeholder);
      if (position < 0)
        break; 
      int escEndIdx = rawLine.indexOf(this.escapeSeq, placeholder) + this.escapeSeq.length();
      if (escEndIdx <= this.escapeSeq.length() - 1 || escEndIdx != position)
        continue; 
      placeholder = position + 1;
      position = -1;
    } while (position < 0 && placeholder < rawLine.length() - this.endDelim.length());
    return position;
  }
  
  private String findAndReplaceUnlessEscaped(String rawLine, String search, String replace) {
    StringBuilder lineBuffer = new StringBuilder((int)(rawLine.length() * 1.5D));
    int lastReplacement = -1;
    while (true) {
      int nextReplacement = rawLine.indexOf(search, lastReplacement + 1);
      if (nextReplacement > -1) {
        if (lastReplacement < 0)
          lastReplacement = 0; 
        lineBuffer.append(rawLine, lastReplacement, nextReplacement);
        int escIdx = rawLine.indexOf(this.escapeSeq, lastReplacement + 1);
        if (escIdx > -1 && escIdx + this.escapeSeq.length() == nextReplacement) {
          lineBuffer.setLength(lineBuffer.length() - this.escapeSeq.length());
          lineBuffer.append(search);
        } else {
          lineBuffer.append(replace);
        } 
        lastReplacement = nextReplacement + search.length();
        if (lastReplacement <= -1)
          break; 
        continue;
      } 
      break;
    } 
    if (lastReplacement < rawLine.length())
      lineBuffer.append(rawLine, lastReplacement, rawLine.length()); 
    return lineBuffer.toString();
  }
}
