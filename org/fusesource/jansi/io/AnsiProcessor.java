package org.fusesource.jansi.io;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Iterator;

public class AnsiProcessor {
  protected final OutputStream os;
  
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
  
  public AnsiProcessor(OutputStream os) {
    this.os = os;
  }
  
  protected int getNextOptionInt(Iterator<Object> optionsIterator) throws IOException {
    while (true) {
      if (!optionsIterator.hasNext())
        throw new IllegalArgumentException(); 
      Object arg = optionsIterator.next();
      if (arg != null)
        return ((Integer)arg).intValue(); 
    } 
  }
  
  protected boolean processEscapeCommand(ArrayList<Object> options, int command) throws IOException {
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
                if (!optionsIterator.hasNext())
                  continue; 
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
                  processAttributeReset();
                  continue;
              } 
              processSetAttribute(value);
            } 
          } 
          if (count == 0)
            processAttributeReset(); 
          return true;
        case 115:
          processSaveCursorPosition();
          return true;
        case 117:
          processRestoreCursorPosition();
          return true;
      } 
      if (97 <= command && command <= 122) {
        processUnknownExtension(options, command);
        return true;
      } 
      if (65 <= command && command <= 90) {
        processUnknownExtension(options, command);
        return true;
      } 
      return false;
    } catch (IllegalArgumentException illegalArgumentException) {
      return false;
    } 
  }
  
  protected boolean processOperatingSystemCommand(ArrayList<Object> options) {
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
  
  protected boolean processCharsetSelect(ArrayList<Object> options) {
    int set = optionInt(options, 0);
    char seq = ((Character)options.get(1)).charValue();
    processCharsetSelect(set, seq);
    return true;
  }
  
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
  
  protected void processSetForegroundColor(int color, boolean bright) throws IOException {}
  
  protected void processSetForegroundColorExt(int paletteIndex) throws IOException {}
  
  protected void processSetForegroundColorExt(int r, int g, int b) throws IOException {}
  
  protected void processSetBackgroundColor(int color) throws IOException {
    processSetBackgroundColor(color, false);
  }
  
  protected void processSetBackgroundColor(int color, boolean bright) throws IOException {}
  
  protected void processSetBackgroundColorExt(int paletteIndex) throws IOException {}
  
  protected void processSetBackgroundColorExt(int r, int g, int b) throws IOException {}
  
  protected void processDefaultTextColor() throws IOException {}
  
  protected void processDefaultBackgroundColor() throws IOException {}
  
  protected void processAttributeReset() throws IOException {}
  
  protected void processCursorTo(int row, int col) throws IOException {}
  
  protected void processCursorToColumn(int x) throws IOException {}
  
  protected void processCursorUpLine(int count) throws IOException {}
  
  protected void processCursorDownLine(int count) throws IOException {
    for (int i = 0; i < count; i++)
      this.os.write(10); 
  }
  
  protected void processCursorLeft(int count) throws IOException {}
  
  protected void processCursorRight(int count) throws IOException {
    for (int i = 0; i < count; i++)
      this.os.write(32); 
  }
  
  protected void processCursorDown(int count) throws IOException {}
  
  protected void processCursorUp(int count) throws IOException {}
  
  protected void processUnknownExtension(ArrayList<Object> options, int command) {}
  
  protected void processChangeIconNameAndWindowTitle(String label) {
    processChangeIconName(label);
    processChangeWindowTitle(label);
  }
  
  protected void processChangeIconName(String label) {}
  
  protected void processChangeWindowTitle(String label) {}
  
  protected void processUnknownOperatingSystemCommand(int command, String param) {}
  
  protected void processCharsetSelect(int set, char seq) {}
}
