package org.jline.utils;

import java.io.FilterWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Iterator;

public class AnsiWriter extends FilterWriter {
  private static final char[] RESET_CODE = "\033[0m".toCharArray();
  
  private static final int MAX_ESCAPE_SEQUENCE_LENGTH = 100;
  
  private final char[] buffer;
  
  private int pos;
  
  private int startOfValue;
  
  private final ArrayList<Object> options;
  
  private static final int LOOKING_FOR_FIRST_ESC_CHAR = 0;
  
  private static final int LOOKING_FOR_SECOND_ESC_CHAR = 1;
  
  private static final int LOOKING_FOR_NEXT_ARG = 2;
  
  private static final int LOOKING_FOR_STR_ARG_END = 3;
  
  private static final int LOOKING_FOR_INT_ARG_END = 4;
  
  private static final int LOOKING_FOR_OSC_COMMAND = 5;
  
  private static final int LOOKING_FOR_OSC_COMMAND_END = 6;
  
  private static final int LOOKING_FOR_OSC_PARAM = 7;
  
  private static final int LOOKING_FOR_ST = 8;
  
  private static final int LOOKING_FOR_CHARSET = 9;
  
  int state;
  
  private static final int FIRST_ESC_CHAR = 27;
  
  private static final int SECOND_ESC_CHAR = 91;
  
  private static final int SECOND_OSC_CHAR = 93;
  
  private static final int BEL = 7;
  
  private static final int SECOND_ST_CHAR = 92;
  
  private static final int SECOND_CHARSET0_CHAR = 40;
  
  private static final int SECOND_CHARSET1_CHAR = 41;
  
  protected static final int ERASE_SCREEN_TO_END = 0;
  
  protected static final int ERASE_SCREEN_TO_BEGINING = 1;
  
  protected static final int ERASE_SCREEN = 2;
  
  protected static final int ERASE_LINE_TO_END = 0;
  
  protected static final int ERASE_LINE_TO_BEGINING = 1;
  
  protected static final int ERASE_LINE = 2;
  
  protected static final int ATTRIBUTE_INTENSITY_BOLD = 1;
  
  protected static final int ATTRIBUTE_INTENSITY_FAINT = 2;
  
  protected static final int ATTRIBUTE_ITALIC = 3;
  
  protected static final int ATTRIBUTE_UNDERLINE = 4;
  
  protected static final int ATTRIBUTE_BLINK_SLOW = 5;
  
  protected static final int ATTRIBUTE_BLINK_FAST = 6;
  
  protected static final int ATTRIBUTE_NEGATIVE_ON = 7;
  
  protected static final int ATTRIBUTE_CONCEAL_ON = 8;
  
  protected static final int ATTRIBUTE_UNDERLINE_DOUBLE = 21;
  
  protected static final int ATTRIBUTE_INTENSITY_NORMAL = 22;
  
  protected static final int ATTRIBUTE_UNDERLINE_OFF = 24;
  
  protected static final int ATTRIBUTE_BLINK_OFF = 25;
  
  @Deprecated
  protected static final int ATTRIBUTE_NEGATIVE_Off = 27;
  
  protected static final int ATTRIBUTE_NEGATIVE_OFF = 27;
  
  protected static final int ATTRIBUTE_CONCEAL_OFF = 28;
  
  protected static final int BLACK = 0;
  
  protected static final int RED = 1;
  
  protected static final int GREEN = 2;
  
  protected static final int YELLOW = 3;
  
  protected static final int BLUE = 4;
  
  protected static final int MAGENTA = 5;
  
  protected static final int CYAN = 6;
  
  protected static final int WHITE = 7;
  
  public AnsiWriter(Writer out) {
    super(out);
    this.buffer = new char[100];
    this.pos = 0;
    this.options = new ArrayList();
    this.state = 0;
  }
  
  public synchronized void write(int data) throws IOException {
    boolean skip;
    switch (this.state) {
      case 0:
        if (data == 27) {
          this.buffer[this.pos++] = (char)data;
          this.state = 1;
          break;
        } 
        this.out.write(data);
        break;
      case 1:
        this.buffer[this.pos++] = (char)data;
        if (data == 91) {
          this.state = 2;
          break;
        } 
        if (data == 93) {
          this.state = 5;
          break;
        } 
        if (data == 40) {
          this.options.add(Integer.valueOf(48));
          this.state = 9;
          break;
        } 
        if (data == 41) {
          this.options.add(Integer.valueOf(49));
          this.state = 9;
          break;
        } 
        reset(false);
        break;
      case 2:
        this.buffer[this.pos++] = (char)data;
        if (34 == data) {
          this.startOfValue = this.pos - 1;
          this.state = 3;
          break;
        } 
        if (48 <= data && data <= 57) {
          this.startOfValue = this.pos - 1;
          this.state = 4;
          break;
        } 
        if (59 == data) {
          this.options.add(null);
          break;
        } 
        if (63 == data) {
          this.options.add(Character.valueOf('?'));
          break;
        } 
        if (61 == data) {
          this.options.add(Character.valueOf('='));
          break;
        } 
        skip = true;
        try {
          skip = processEscapeCommand(this.options, data);
        } finally {
          reset(skip);
        } 
        break;
      case 4:
        this.buffer[this.pos++] = (char)data;
        if (48 > data || data > 57) {
          String strValue = new String(this.buffer, this.startOfValue, this.pos - 1 - this.startOfValue);
          Integer value = Integer.valueOf(strValue);
          this.options.add(value);
          if (data == 59) {
            this.state = 2;
            break;
          } 
          boolean bool = true;
          try {
            bool = processEscapeCommand(this.options, data);
          } finally {
            reset(bool);
          } 
        } 
        break;
      case 3:
        this.buffer[this.pos++] = (char)data;
        if (34 != data) {
          String value = new String(this.buffer, this.startOfValue, this.pos - 1 - this.startOfValue);
          this.options.add(value);
          if (data == 59) {
            this.state = 2;
            break;
          } 
          reset(processEscapeCommand(this.options, data));
        } 
        break;
      case 5:
        this.buffer[this.pos++] = (char)data;
        if (48 <= data && data <= 57) {
          this.startOfValue = this.pos - 1;
          this.state = 6;
          break;
        } 
        reset(false);
        break;
      case 6:
        this.buffer[this.pos++] = (char)data;
        if (59 == data) {
          String strValue = new String(this.buffer, this.startOfValue, this.pos - 1 - this.startOfValue);
          Integer value = Integer.valueOf(strValue);
          this.options.add(value);
          this.startOfValue = this.pos;
          this.state = 7;
          break;
        } 
        if (48 <= data && data <= 57)
          break; 
        reset(false);
        break;
      case 7:
        this.buffer[this.pos++] = (char)data;
        if (7 == data) {
          String value = new String(this.buffer, this.startOfValue, this.pos - 1 - this.startOfValue);
          this.options.add(value);
          boolean bool = true;
          try {
            bool = processOperatingSystemCommand(this.options);
          } finally {
            reset(bool);
          } 
          break;
        } 
        if (27 == data)
          this.state = 8; 
        break;
      case 8:
        this.buffer[this.pos++] = (char)data;
        if (92 == data) {
          String value = new String(this.buffer, this.startOfValue, this.pos - 2 - this.startOfValue);
          this.options.add(value);
          boolean bool = true;
          try {
            bool = processOperatingSystemCommand(this.options);
          } finally {
            reset(bool);
          } 
          break;
        } 
        this.state = 7;
        break;
      case 9:
        this.options.add(Character.valueOf((char)data));
        reset(processCharsetSelect(this.options));
        break;
    } 
    if (this.pos >= this.buffer.length)
      reset(false); 
  }
  
  private void reset(boolean skipBuffer) throws IOException {
    if (!skipBuffer)
      this.out.write(this.buffer, 0, this.pos); 
    this.pos = 0;
    this.startOfValue = 0;
    this.options.clear();
    this.state = 0;
  }
  
  private int getNextOptionInt(Iterator<Object> optionsIterator) throws IOException {
    while (true) {
      if (!optionsIterator.hasNext())
        throw new IllegalArgumentException(); 
      Object arg = optionsIterator.next();
      if (arg != null)
        return ((Integer)arg).intValue(); 
    } 
  }
  
  private boolean processEscapeCommand(ArrayList<Object> options, int command) throws IOException {
    try {
      int count;
      Iterator<Object> optionsIterator;
      switch (command) {
        case 65:
          processCursorUp(optionInt(options, 0, 1));
          return true;
        case 66:
          processCursorDown(optionInt(options, 0, 1));
          return true;
        case 67:
          processCursorRight(optionInt(options, 0, 1));
          return true;
        case 68:
          processCursorLeft(optionInt(options, 0, 1));
          return true;
        case 69:
          processCursorDownLine(optionInt(options, 0, 1));
          return true;
        case 70:
          processCursorUpLine(optionInt(options, 0, 1));
          return true;
        case 71:
          processCursorToColumn(optionInt(options, 0));
          return true;
        case 72:
        case 102:
          processCursorTo(optionInt(options, 0, 1), optionInt(options, 1, 1));
          return true;
        case 74:
          processEraseScreen(optionInt(options, 0, 0));
          return true;
        case 75:
          processEraseLine(optionInt(options, 0, 0));
          return true;
        case 76:
          processInsertLine(optionInt(options, 0, 1));
          return true;
        case 77:
          processDeleteLine(optionInt(options, 0, 1));
          return true;
        case 83:
          processScrollUp(optionInt(options, 0, 1));
          return true;
        case 84:
          processScrollDown(optionInt(options, 0, 1));
          return true;
        case 109:
          for (Object next : options) {
            if (next != null && next.getClass() != Integer.class)
              throw new IllegalArgumentException(); 
          } 
          count = 0;
          optionsIterator = options.iterator();
          while (optionsIterator.hasNext()) {
            Object next = optionsIterator.next();
            if (next != null) {
              count++;
              int value = ((Integer)next).intValue();
              if (30 <= value && value <= 37) {
                processSetForegroundColor(value - 30);
                continue;
              } 
              if (40 <= value && value <= 47) {
                processSetBackgroundColor(value - 40);
                continue;
              } 
              if (90 <= value && value <= 97) {
                processSetForegroundColor(value - 90, true);
                continue;
              } 
              if (100 <= value && value <= 107) {
                processSetBackgroundColor(value - 100, true);
                continue;
              } 
              if (value == 38 || value == 48) {
                int arg2or5 = getNextOptionInt(optionsIterator);
                if (arg2or5 == 2) {
                  int r = getNextOptionInt(optionsIterator);
                  int g = getNextOptionInt(optionsIterator);
                  int b = getNextOptionInt(optionsIterator);
                  if (r >= 0 && r <= 255 && g >= 0 && g <= 255 && b >= 0 && b <= 255) {
                    if (value == 38) {
                      processSetForegroundColorExt(r, g, b);
                      continue;
                    } 
                    processSetBackgroundColorExt(r, g, b);
                    continue;
                  } 
                  throw new IllegalArgumentException();
                } 
                if (arg2or5 == 5) {
                  int paletteIndex = getNextOptionInt(optionsIterator);
                  if (paletteIndex >= 0 && paletteIndex <= 255) {
                    if (value == 38) {
                      processSetForegroundColorExt(paletteIndex);
                      continue;
                    } 
                    processSetBackgroundColorExt(paletteIndex);
                    continue;
                  } 
                  throw new IllegalArgumentException();
                } 
                throw new IllegalArgumentException();
              } 
              switch (value) {
                case 39:
                  processDefaultTextColor();
                  continue;
                case 49:
                  processDefaultBackgroundColor();
                  continue;
                case 0:
                  processAttributeRest();
                  continue;
              } 
              processSetAttribute(value);
            } 
          } 
          if (count == 0)
            processAttributeRest(); 
          return true;
        case 115:
          processSaveCursorPosition();
          return true;
        case 117:
          processRestoreCursorPosition();
          return true;
      } 
      if (97 <= command && 122 <= command) {
        processUnknownExtension(options, command);
        return true;
      } 
      if (65 <= command && 90 <= command) {
        processUnknownExtension(options, command);
        return true;
      } 
      return false;
    } catch (IllegalArgumentException illegalArgumentException) {
      return false;
    } 
  }
  
  private boolean processOperatingSystemCommand(ArrayList<Object> options) throws IOException {
    int command = optionInt(options, 0);
    String label = (String)options.get(1);
    try {
      switch (command) {
        case 0:
          processChangeIconNameAndWindowTitle(label);
          return true;
        case 1:
          processChangeIconName(label);
          return true;
        case 2:
          processChangeWindowTitle(label);
          return true;
      } 
      processUnknownOperatingSystemCommand(command, label);
      return true;
    } catch (IllegalArgumentException illegalArgumentException) {
      return false;
    } 
  }
  
  protected void processRestoreCursorPosition() throws IOException {}
  
  protected void processSaveCursorPosition() throws IOException {}
  
  protected void processInsertLine(int optionInt) throws IOException {}
  
  protected void processDeleteLine(int optionInt) throws IOException {}
  
  protected void processScrollDown(int optionInt) throws IOException {}
  
  protected void processScrollUp(int optionInt) throws IOException {}
  
  protected void processEraseScreen(int eraseOption) throws IOException {}
  
  protected void processEraseLine(int eraseOption) throws IOException {}
  
  protected void processSetAttribute(int attribute) throws IOException {}
  
  protected void processSetForegroundColor(int color) throws IOException {
    processSetForegroundColor(color, false);
  }
  
  protected void processSetForegroundColor(int color, boolean bright) throws IOException {
    processSetForegroundColorExt(bright ? (color + 8) : color);
  }
  
  protected void processSetForegroundColorExt(int paletteIndex) throws IOException {}
  
  protected void processSetForegroundColorExt(int r, int g, int b) throws IOException {
    processSetForegroundColorExt(Colors.roundRgbColor(r, g, b, 16));
  }
  
  protected void processSetBackgroundColor(int color) throws IOException {
    processSetBackgroundColor(color, false);
  }
  
  protected void processSetBackgroundColor(int color, boolean bright) throws IOException {
    processSetBackgroundColorExt(bright ? (color + 8) : color);
  }
  
  protected void processSetBackgroundColorExt(int paletteIndex) throws IOException {}
  
  protected void processSetBackgroundColorExt(int r, int g, int b) throws IOException {
    processSetBackgroundColorExt(Colors.roundRgbColor(r, g, b, 16));
  }
  
  protected void processDefaultTextColor() throws IOException {}
  
  protected void processDefaultBackgroundColor() throws IOException {}
  
  protected void processAttributeRest() throws IOException {}
  
  protected void processCursorTo(int row, int col) throws IOException {}
  
  protected void processCursorToColumn(int x) throws IOException {}
  
  protected void processCursorUpLine(int count) throws IOException {}
  
  protected void processCursorDownLine(int count) throws IOException {
    for (int i = 0; i < count; i++)
      this.out.write(10); 
  }
  
  protected void processCursorLeft(int count) throws IOException {}
  
  protected void processCursorRight(int count) throws IOException {
    for (int i = 0; i < count; i++)
      this.out.write(32); 
  }
  
  protected void processCursorDown(int count) throws IOException {}
  
  protected void processCursorUp(int count) throws IOException {}
  
  protected void processUnknownExtension(ArrayList<Object> options, int command) {}
  
  protected void processChangeIconNameAndWindowTitle(String label) {
    processChangeIconName(label);
    processChangeWindowTitle(label);
  }
  
  protected void processChangeIconName(String name) {}
  
  protected void processChangeWindowTitle(String title) {}
  
  protected void processUnknownOperatingSystemCommand(int command, String param) {}
  
  private boolean processCharsetSelect(ArrayList<Object> options) throws IOException {
    int set = optionInt(options, 0);
    char seq = ((Character)options.get(1)).charValue();
    processCharsetSelect(set, seq);
    return true;
  }
  
  protected void processCharsetSelect(int set, char seq) {}
  
  private int optionInt(ArrayList<Object> options, int index) {
    if (options.size() <= index)
      throw new IllegalArgumentException(); 
    Object value = options.get(index);
    if (value == null)
      throw new IllegalArgumentException(); 
    if (!value.getClass().equals(Integer.class))
      throw new IllegalArgumentException(); 
    return ((Integer)value).intValue();
  }
  
  private int optionInt(ArrayList<Object> options, int index, int defaultValue) {
    if (options.size() > index) {
      Object value = options.get(index);
      if (value == null)
        return defaultValue; 
      return ((Integer)value).intValue();
    } 
    return defaultValue;
  }
  
  public void write(char[] cbuf, int off, int len) throws IOException {
    for (int i = 0; i < len; i++)
      write(cbuf[off + i]); 
  }
  
  public void write(String str, int off, int len) throws IOException {
    for (int i = 0; i < len; i++)
      write(str.charAt(off + i)); 
  }
  
  public void close() throws IOException {
    write(RESET_CODE);
    flush();
    super.close();
  }
}
