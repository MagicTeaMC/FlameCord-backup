package org.jline.reader.impl;

import java.util.ListIterator;
import org.jline.reader.Expander;
import org.jline.reader.History;

public class DefaultExpander implements Expander {
  public String expandHistory(History history, String line) {
    boolean inQuote = false;
    StringBuilder sb = new StringBuilder();
    boolean escaped = false;
    int unicode = 0;
    for (int i = 0; i < line.length(); i++) {
      char c = line.charAt(i);
      if (unicode > 0) {
        escaped = (--unicode >= 0);
        sb.append(c);
      } else if (escaped) {
        if (c == 'u') {
          unicode = 4;
        } else {
          escaped = false;
        } 
        sb.append(c);
      } else if (c == '\'') {
        inQuote = !inQuote;
        sb.append(c);
      } else if (inQuote) {
        sb.append(c);
      } else {
        switch (c) {
          case '\\':
            escaped = true;
            sb.append(c);
            break;
          case '!':
            if (i + 1 < line.length()) {
              int i1, idx;
              String sc, previous;
              int lastSpace;
              String ss;
              c = line.charAt(++i);
              boolean neg = false;
              String rep = null;
              switch (c) {
                case '!':
                  if (history.size() == 0)
                    throw new IllegalArgumentException("!!: event not found"); 
                  rep = history.get(history.index() - 1);
                  break;
                case '#':
                  sb.append(sb.toString());
                  break;
                case '?':
                  i1 = line.indexOf('?', i + 1);
                  if (i1 < 0)
                    i1 = line.length(); 
                  sc = line.substring(i + 1, i1);
                  i = i1;
                  idx = searchBackwards(history, sc, history.index(), false);
                  if (idx < 0)
                    throw new IllegalArgumentException("!?" + sc + ": event not found"); 
                  rep = history.get(idx);
                  break;
                case '$':
                  if (history.size() == 0)
                    throw new IllegalArgumentException("!$: event not found"); 
                  previous = history.get(history.index() - 1).trim();
                  lastSpace = previous.lastIndexOf(' ');
                  if (lastSpace != -1) {
                    rep = previous.substring(lastSpace + 1);
                    break;
                  } 
                  rep = previous;
                  break;
                case '\t':
                case ' ':
                  sb.append('!');
                  sb.append(c);
                  break;
                case '-':
                  neg = true;
                  i++;
                case '0':
                case '1':
                case '2':
                case '3':
                case '4':
                case '5':
                case '6':
                case '7':
                case '8':
                case '9':
                  i1 = i;
                  for (; i < line.length(); i++) {
                    c = line.charAt(i);
                    if (c < '0' || c > '9')
                      break; 
                  } 
                  try {
                    idx = Integer.parseInt(line.substring(i1, i));
                  } catch (NumberFormatException e) {
                    throw new IllegalArgumentException((neg ? "!-" : "!") + line.substring(i1, i) + ": event not found");
                  } 
                  if (neg && idx > 0 && idx <= history.size()) {
                    rep = history.get(history.index() - idx);
                    break;
                  } 
                  if (!neg && idx > history.index() - history.size() && idx <= history.index()) {
                    rep = history.get(idx - 1);
                    break;
                  } 
                  throw new IllegalArgumentException((neg ? "!-" : "!") + line.substring(i1, i) + ": event not found");
                default:
                  ss = line.substring(i);
                  i = line.length();
                  idx = searchBackwards(history, ss, history.index(), true);
                  if (idx < 0)
                    throw new IllegalArgumentException("!" + ss + ": event not found"); 
                  rep = history.get(idx);
                  break;
              } 
              if (rep != null)
                sb.append(rep); 
              break;
            } 
            sb.append(c);
            break;
          case '^':
            if (i == 0) {
              int i1 = line.indexOf('^', i + 1);
              int i2 = line.indexOf('^', i1 + 1);
              if (i2 < 0)
                i2 = line.length(); 
              if (i1 > 0 && i2 > 0) {
                String s1 = line.substring(i + 1, i1);
                String s2 = line.substring(i1 + 1, i2);
                String s = history.get(history.index() - 1).replace(s1, s2);
                sb.append(s);
                i = i2 + 1;
                break;
              } 
            } 
            sb.append(c);
            break;
          default:
            sb.append(c);
            break;
        } 
      } 
    } 
    return sb.toString();
  }
  
  public String expandVar(String word) {
    return word;
  }
  
  protected int searchBackwards(History history, String searchTerm, int startIndex, boolean startsWith) {
    ListIterator<History.Entry> it = history.iterator(startIndex);
    while (it.hasPrevious()) {
      History.Entry e = it.previous();
      if (startsWith) {
        if (e.line().startsWith(searchTerm))
          return e.index(); 
        continue;
      } 
      if (e.line().contains(searchTerm))
        return e.index(); 
    } 
    return -1;
  }
}
