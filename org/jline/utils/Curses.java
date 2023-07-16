package org.jline.utils;

import java.io.Flushable;
import java.io.IOError;
import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayDeque;

public final class Curses {
  private static final Object[] sv = new Object[26];
  
  private static final Object[] dv = new Object[26];
  
  private static final int IFTE_NONE = 0;
  
  private static final int IFTE_IF = 1;
  
  private static final int IFTE_THEN = 2;
  
  private static final int IFTE_ELSE = 3;
  
  public static String tputs(String cap, Object... params) {
    if (cap != null) {
      StringWriter sw = new StringWriter();
      tputs(sw, cap, params);
      return sw.toString();
    } 
    return null;
  }
  
  public static void tputs(Appendable out, String str, Object... params) {
    try {
      doTputs(out, str, params);
    } catch (Exception e) {
      throw new IOError(e);
    } 
  }
  
  private static void doTputs(Appendable out, String str, Object... params) throws IOException {
    int index = 0;
    int length = str.length();
    int ifte = 0;
    boolean exec = true;
    ArrayDeque<Object> stack = new ArrayDeque();
    while (index < length) {
      int start;
      boolean alternate, left, space, plus;
      int width, prec, cnv;
      char ch = str.charAt(index++);
      switch (ch) {
        case '\\':
          ch = str.charAt(index++);
          if (ch >= '0' && ch <= '7') {
            int val = ch - 48;
            for (int i = 0; i < 2; i++) {
              ch = str.charAt(index++);
              if (ch < '0' || ch > '7')
                throw new IllegalStateException(); 
              val = val * 8 + ch - 48;
            } 
            out.append((char)val);
            continue;
          } 
          switch (ch) {
            case 'E':
            case 'e':
              if (exec)
                out.append('\033'); 
              continue;
            case 'n':
              out.append('\n');
              continue;
            case 'r':
              if (exec)
                out.append('\r'); 
              continue;
            case 't':
              if (exec)
                out.append('\t'); 
              continue;
            case 'b':
              if (exec)
                out.append('\b'); 
              continue;
            case 'f':
              if (exec)
                out.append('\f'); 
              continue;
            case 's':
              if (exec)
                out.append(' '); 
              continue;
            case ':':
            case '\\':
            case '^':
              if (exec)
                out.append(ch); 
              continue;
          } 
          throw new IllegalArgumentException();
        case '^':
          ch = str.charAt(index++);
          if (exec)
            out.append((char)(ch - 64)); 
          continue;
        case '%':
          ch = str.charAt(index++);
          switch (ch) {
            case '%':
              if (exec)
                out.append('%'); 
              continue;
            case 'p':
              ch = str.charAt(index++);
              if (exec)
                stack.push(params[ch - 49]); 
              continue;
            case 'P':
              ch = str.charAt(index++);
              if (ch >= 'a' && ch <= 'z') {
                if (exec)
                  dv[ch - 97] = stack.pop(); 
                continue;
              } 
              if (ch >= 'A' && ch <= 'Z') {
                if (exec)
                  sv[ch - 65] = stack.pop(); 
                continue;
              } 
              throw new IllegalArgumentException();
            case 'g':
              ch = str.charAt(index++);
              if (ch >= 'a' && ch <= 'z') {
                if (exec)
                  stack.push(dv[ch - 97]); 
                continue;
              } 
              if (ch >= 'A' && ch <= 'Z') {
                if (exec)
                  stack.push(sv[ch - 65]); 
                continue;
              } 
              throw new IllegalArgumentException();
            case '\'':
              ch = str.charAt(index++);
              if (exec)
                stack.push(Integer.valueOf(ch)); 
              ch = str.charAt(index++);
              if (ch != '\'')
                throw new IllegalArgumentException(); 
              continue;
            case '{':
              start = index;
              while (str.charAt(index++) != '}');
              if (exec) {
                int v = Integer.parseInt(str.substring(start, index - 1));
                stack.push(Integer.valueOf(v));
              } 
              continue;
            case 'l':
              if (exec)
                stack.push(Integer.valueOf(stack.pop().toString().length())); 
              continue;
            case '+':
              if (exec) {
                int v2 = toInteger(stack.pop());
                int v1 = toInteger(stack.pop());
                stack.push(Integer.valueOf(v1 + v2));
              } 
              continue;
            case '-':
              if (exec) {
                int v2 = toInteger(stack.pop());
                int v1 = toInteger(stack.pop());
                stack.push(Integer.valueOf(v1 - v2));
              } 
              continue;
            case '*':
              if (exec) {
                int v2 = toInteger(stack.pop());
                int v1 = toInteger(stack.pop());
                stack.push(Integer.valueOf(v1 * v2));
              } 
              continue;
            case '/':
              if (exec) {
                int v2 = toInteger(stack.pop());
                int v1 = toInteger(stack.pop());
                stack.push(Integer.valueOf(v1 / v2));
              } 
              continue;
            case 'm':
              if (exec) {
                int v2 = toInteger(stack.pop());
                int v1 = toInteger(stack.pop());
                stack.push(Integer.valueOf(v1 % v2));
              } 
              continue;
            case '&':
              if (exec) {
                int v2 = toInteger(stack.pop());
                int v1 = toInteger(stack.pop());
                stack.push(Integer.valueOf(v1 & v2));
              } 
              continue;
            case '|':
              if (exec) {
                int v2 = toInteger(stack.pop());
                int v1 = toInteger(stack.pop());
                stack.push(Integer.valueOf(v1 | v2));
              } 
              continue;
            case '^':
              if (exec) {
                int v2 = toInteger(stack.pop());
                int v1 = toInteger(stack.pop());
                stack.push(Integer.valueOf(v1 ^ v2));
              } 
              continue;
            case '=':
              if (exec) {
                int v2 = toInteger(stack.pop());
                int v1 = toInteger(stack.pop());
                stack.push(Boolean.valueOf((v1 == v2)));
              } 
              continue;
            case '>':
              if (exec) {
                int v2 = toInteger(stack.pop());
                int v1 = toInteger(stack.pop());
                stack.push(Boolean.valueOf((v1 > v2)));
              } 
              continue;
            case '<':
              if (exec) {
                int v2 = toInteger(stack.pop());
                int v1 = toInteger(stack.pop());
                stack.push(Boolean.valueOf((v1 < v2)));
              } 
              continue;
            case 'A':
              if (exec) {
                int v2 = toInteger(stack.pop());
                int v1 = toInteger(stack.pop());
                stack.push(Boolean.valueOf((v1 != 0 && v2 != 0)));
              } 
              continue;
            case '!':
              if (exec) {
                int v1 = toInteger(stack.pop());
                stack.push(Boolean.valueOf((v1 == 0)));
              } 
              continue;
            case '~':
              if (exec) {
                int v1 = toInteger(stack.pop());
                stack.push(Integer.valueOf(v1 ^ 0xFFFFFFFF));
              } 
              continue;
            case 'O':
              if (exec) {
                int v2 = toInteger(stack.pop());
                int v1 = toInteger(stack.pop());
                stack.push(Boolean.valueOf((v1 != 0 || v2 != 0)));
              } 
              continue;
            case '?':
              if (ifte != 0)
                throw new IllegalArgumentException(); 
              ifte = 1;
              continue;
            case 't':
              if (ifte != 1 && ifte != 3)
                throw new IllegalArgumentException(); 
              ifte = 2;
              exec = (toInteger(stack.pop()) != 0);
              continue;
            case 'e':
              if (ifte != 2)
                throw new IllegalArgumentException(); 
              ifte = 3;
              exec = !exec;
              continue;
            case ';':
              if (ifte == 0 || ifte == 1)
                throw new IllegalArgumentException(); 
              ifte = 0;
              exec = true;
              continue;
            case 'i':
              if (params.length >= 1)
                params[0] = Integer.valueOf(toInteger(params[0]) + 1); 
              if (params.length >= 2)
                params[1] = Integer.valueOf(toInteger(params[1]) + 1); 
              continue;
            case 'd':
              out.append(Integer.toString(toInteger(stack.pop())));
              continue;
          } 
          if (ch == ':')
            ch = str.charAt(index++); 
          alternate = false;
          left = false;
          space = false;
          plus = false;
          width = 0;
          prec = -1;
          while ("-+# ".indexOf(ch) >= 0) {
            switch (ch) {
              case '-':
                left = true;
                break;
              case '+':
                plus = true;
                break;
              case '#':
                alternate = true;
                break;
              case ' ':
                space = true;
                break;
            } 
            ch = str.charAt(index++);
          } 
          if ("123456789".indexOf(ch) >= 0)
            do {
              width = width * 10 + ch - 48;
              ch = str.charAt(index++);
            } while ("0123456789".indexOf(ch) >= 0); 
          if (ch == '.') {
            prec = 0;
            ch = str.charAt(index++);
          } 
          if ("0123456789".indexOf(ch) >= 0)
            do {
              prec = prec * 10 + ch - 48;
              ch = str.charAt(index++);
            } while ("0123456789".indexOf(ch) >= 0); 
          if ("cdoxXs".indexOf(ch) < 0)
            throw new IllegalArgumentException(); 
          cnv = ch;
          if (exec) {
            String res;
            if (cnv == 115) {
              res = (String)stack.pop();
              if (prec >= 0)
                res = res.substring(0, prec); 
            } else {
              int p = toInteger(stack.pop());
              StringBuilder fmt = new StringBuilder(16);
              fmt.append('%');
              if (alternate)
                fmt.append('#'); 
              if (plus)
                fmt.append('+'); 
              if (space)
                fmt.append(' '); 
              if (prec >= 0) {
                fmt.append('0');
                fmt.append(prec);
              } 
              fmt.append((char)cnv);
              res = String.format(fmt.toString(), new Object[] { Integer.valueOf(p) });
            } 
            if (width > res.length())
              res = String.format("%" + (left ? "-" : "") + width + "s", new Object[] { res }); 
            out.append(res);
          } 
          continue;
        case '$':
          if (index < length && str.charAt(index) == '<') {
            int nb = 0;
            while ((ch = str.charAt(++index)) != '>') {
              if (ch >= '0' && ch <= '9') {
                nb = nb * 10 + ch - 48;
                continue;
              } 
              if (ch == '*')
                continue; 
              if (ch == '/');
            } 
            index++;
            try {
              if (out instanceof Flushable)
                ((Flushable)out).flush(); 
              Thread.sleep(nb);
            } catch (InterruptedException interruptedException) {}
            continue;
          } 
          if (exec)
            out.append(ch); 
          continue;
      } 
      if (exec)
        out.append(ch); 
    } 
  }
  
  private static int toInteger(Object pop) {
    if (pop instanceof Number)
      return ((Number)pop).intValue(); 
    if (pop instanceof Boolean)
      return ((Boolean)pop).booleanValue() ? 1 : 0; 
    return Integer.parseInt(pop.toString());
  }
}
