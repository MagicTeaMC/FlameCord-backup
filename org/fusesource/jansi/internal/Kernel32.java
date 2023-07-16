package org.fusesource.jansi.internal;

import java.io.IOException;
import org.fusesource.jansi.WindowsSupport;

public class Kernel32 {
  public static short FOREGROUND_BLUE;
  
  public static short FOREGROUND_GREEN;
  
  public static short FOREGROUND_RED;
  
  public static short FOREGROUND_INTENSITY;
  
  public static short BACKGROUND_BLUE;
  
  public static short BACKGROUND_GREEN;
  
  public static short BACKGROUND_RED;
  
  public static short BACKGROUND_INTENSITY;
  
  public static short COMMON_LVB_LEADING_BYTE;
  
  public static short COMMON_LVB_TRAILING_BYTE;
  
  public static short COMMON_LVB_GRID_HORIZONTAL;
  
  public static short COMMON_LVB_GRID_LVERTICAL;
  
  public static short COMMON_LVB_GRID_RVERTICAL;
  
  public static short COMMON_LVB_REVERSE_VIDEO;
  
  public static short COMMON_LVB_UNDERSCORE;
  
  public static int FORMAT_MESSAGE_FROM_SYSTEM;
  
  public static int STD_INPUT_HANDLE;
  
  public static int STD_OUTPUT_HANDLE;
  
  public static int STD_ERROR_HANDLE;
  
  public static int INVALID_HANDLE_VALUE;
  
  static {
    if (JansiLoader.initialize())
      init(); 
  }
  
  public static class SMALL_RECT {
    public static int SIZEOF;
    
    public short left;
    
    public short top;
    
    public short right;
    
    public short bottom;
    
    static {
      JansiLoader.initialize();
      init();
    }
    
    public short width() {
      return (short)(this.right - this.left);
    }
    
    public short height() {
      return (short)(this.bottom - this.top);
    }
    
    public SMALL_RECT copy() {
      SMALL_RECT rc = new SMALL_RECT();
      rc.left = this.left;
      rc.top = this.top;
      rc.right = this.right;
      rc.bottom = this.bottom;
      return rc;
    }
    
    private static native void init();
  }
  
  public static class COORD {
    public static int SIZEOF;
    
    public short x;
    
    public short y;
    
    static {
      JansiLoader.initialize();
      init();
    }
    
    public COORD copy() {
      COORD rc = new COORD();
      rc.x = this.x;
      rc.y = this.y;
      return rc;
    }
    
    private static native void init();
  }
  
  public static class CONSOLE_SCREEN_BUFFER_INFO {
    public static int SIZEOF;
    
    static {
      JansiLoader.initialize();
      init();
    }
    
    public Kernel32.COORD size = new Kernel32.COORD();
    
    public Kernel32.COORD cursorPosition = new Kernel32.COORD();
    
    public short attributes;
    
    public Kernel32.SMALL_RECT window = new Kernel32.SMALL_RECT();
    
    public Kernel32.COORD maximumWindowSize = new Kernel32.COORD();
    
    public int windowWidth() {
      return this.window.width() + 1;
    }
    
    public int windowHeight() {
      return this.window.height() + 1;
    }
    
    private static native void init();
  }
  
  public static class CHAR_INFO {
    public static int SIZEOF;
    
    public short attributes;
    
    public char unicodeChar;
    
    private static native void init();
    
    static {
      JansiLoader.initialize();
      init();
    }
  }
  
  public static class KEY_EVENT_RECORD {
    public static int SIZEOF;
    
    public static int CAPSLOCK_ON;
    
    public static int NUMLOCK_ON;
    
    public static int SCROLLLOCK_ON;
    
    public static int ENHANCED_KEY;
    
    public static int LEFT_ALT_PRESSED;
    
    public static int LEFT_CTRL_PRESSED;
    
    public static int RIGHT_ALT_PRESSED;
    
    public static int RIGHT_CTRL_PRESSED;
    
    public static int SHIFT_PRESSED;
    
    public boolean keyDown;
    
    public short repeatCount;
    
    public short keyCode;
    
    public short scanCode;
    
    public char uchar;
    
    public int controlKeyState;
    
    static {
      JansiLoader.initialize();
      init();
    }
    
    public String toString() {
      return "KEY_EVENT_RECORD{keyDown=" + this.keyDown + ", repeatCount=" + this.repeatCount + ", keyCode=" + this.keyCode + ", scanCode=" + this.scanCode + ", uchar=" + this.uchar + ", controlKeyState=" + this.controlKeyState + '}';
    }
    
    private static native void init();
  }
  
  public static class MOUSE_EVENT_RECORD {
    public static int SIZEOF;
    
    public static int FROM_LEFT_1ST_BUTTON_PRESSED;
    
    public static int FROM_LEFT_2ND_BUTTON_PRESSED;
    
    public static int FROM_LEFT_3RD_BUTTON_PRESSED;
    
    public static int FROM_LEFT_4TH_BUTTON_PRESSED;
    
    public static int RIGHTMOST_BUTTON_PRESSED;
    
    public static int CAPSLOCK_ON;
    
    public static int NUMLOCK_ON;
    
    public static int SCROLLLOCK_ON;
    
    public static int ENHANCED_KEY;
    
    public static int LEFT_ALT_PRESSED;
    
    public static int LEFT_CTRL_PRESSED;
    
    public static int RIGHT_ALT_PRESSED;
    
    public static int RIGHT_CTRL_PRESSED;
    
    public static int SHIFT_PRESSED;
    
    public static int DOUBLE_CLICK;
    
    public static int MOUSE_HWHEELED;
    
    public static int MOUSE_MOVED;
    
    public static int MOUSE_WHEELED;
    
    static {
      JansiLoader.initialize();
      init();
    }
    
    public Kernel32.COORD mousePosition = new Kernel32.COORD();
    
    public int buttonState;
    
    public int controlKeyState;
    
    public int eventFlags;
    
    public String toString() {
      return "MOUSE_EVENT_RECORD{mousePosition=" + this.mousePosition + ", buttonState=" + this.buttonState + ", controlKeyState=" + this.controlKeyState + ", eventFlags=" + this.eventFlags + '}';
    }
    
    private static native void init();
  }
  
  public static class WINDOW_BUFFER_SIZE_RECORD {
    public static int SIZEOF;
    
    static {
      JansiLoader.initialize();
      init();
    }
    
    public Kernel32.COORD size = new Kernel32.COORD();
    
    public String toString() {
      return "WINDOW_BUFFER_SIZE_RECORD{size=" + this.size + '}';
    }
    
    private static native void init();
  }
  
  public static class FOCUS_EVENT_RECORD {
    public static int SIZEOF;
    
    public boolean setFocus;
    
    private static native void init();
    
    static {
      JansiLoader.initialize();
      init();
    }
  }
  
  public static class MENU_EVENT_RECORD {
    public static int SIZEOF;
    
    public int commandId;
    
    private static native void init();
    
    static {
      JansiLoader.initialize();
      init();
    }
  }
  
  public static class INPUT_RECORD {
    public static int SIZEOF;
    
    public static short KEY_EVENT;
    
    public static short MOUSE_EVENT;
    
    public static short WINDOW_BUFFER_SIZE_EVENT;
    
    public static short FOCUS_EVENT;
    
    public static short MENU_EVENT;
    
    public short eventType;
    
    private static native void init();
    
    public static native void memmove(INPUT_RECORD param1INPUT_RECORD, long param1Long1, long param1Long2);
    
    static {
      JansiLoader.initialize();
      init();
    }
    
    public Kernel32.KEY_EVENT_RECORD keyEvent = new Kernel32.KEY_EVENT_RECORD();
    
    public Kernel32.MOUSE_EVENT_RECORD mouseEvent = new Kernel32.MOUSE_EVENT_RECORD();
    
    public Kernel32.WINDOW_BUFFER_SIZE_RECORD windowBufferSizeEvent = new Kernel32.WINDOW_BUFFER_SIZE_RECORD();
    
    public Kernel32.MENU_EVENT_RECORD menuEvent = new Kernel32.MENU_EVENT_RECORD();
    
    public Kernel32.FOCUS_EVENT_RECORD focusEvent = new Kernel32.FOCUS_EVENT_RECORD();
  }
  
  public static INPUT_RECORD[] readConsoleInputHelper(long handle, int count, boolean peek) throws IOException {
    int[] length = new int[1];
    long inputRecordPtr = 0L;
    try {
      inputRecordPtr = malloc((INPUT_RECORD.SIZEOF * count));
      if (inputRecordPtr == 0L)
        throw new IOException("cannot allocate memory with JNI"); 
      int res = peek ? PeekConsoleInputW(handle, inputRecordPtr, count, length) : ReadConsoleInputW(handle, inputRecordPtr, count, length);
      if (res == 0)
        throw new IOException("ReadConsoleInputW failed: " + WindowsSupport.getLastErrorMessage()); 
      if (length[0] <= 0)
        return new INPUT_RECORD[0]; 
      INPUT_RECORD[] records = new INPUT_RECORD[length[0]];
      for (int i = 0; i < records.length; i++) {
        records[i] = new INPUT_RECORD();
        INPUT_RECORD.memmove(records[i], inputRecordPtr + (i * INPUT_RECORD.SIZEOF), INPUT_RECORD.SIZEOF);
      } 
      return records;
    } finally {
      if (inputRecordPtr != 0L)
        free(inputRecordPtr); 
    } 
  }
  
  public static INPUT_RECORD[] readConsoleKeyInput(long handle, int count, boolean peek) throws IOException {
    while (true) {
      INPUT_RECORD[] evts = readConsoleInputHelper(handle, count, peek);
      int keyEvtCount = 0;
      for (INPUT_RECORD evt : evts) {
        if (evt.eventType == INPUT_RECORD.KEY_EVENT)
          keyEvtCount++; 
      } 
      if (keyEvtCount > 0) {
        INPUT_RECORD[] res = new INPUT_RECORD[keyEvtCount];
        int i = 0;
        for (INPUT_RECORD evt : evts) {
          if (evt.eventType == INPUT_RECORD.KEY_EVENT)
            res[i++] = evt; 
        } 
        return res;
      } 
    } 
  }
  
  private static native void init();
  
  public static native long malloc(long paramLong);
  
  public static native void free(long paramLong);
  
  public static native int SetConsoleTextAttribute(long paramLong, short paramShort);
  
  public static native int WaitForSingleObject(long paramLong, int paramInt);
  
  public static native int CloseHandle(long paramLong);
  
  public static native int GetLastError();
  
  public static native int FormatMessageW(int paramInt1, long paramLong, int paramInt2, int paramInt3, byte[] paramArrayOfbyte, int paramInt4, long[] paramArrayOflong);
  
  public static native int GetConsoleScreenBufferInfo(long paramLong, CONSOLE_SCREEN_BUFFER_INFO paramCONSOLE_SCREEN_BUFFER_INFO);
  
  public static native long GetStdHandle(int paramInt);
  
  public static native int SetConsoleCursorPosition(long paramLong, COORD paramCOORD);
  
  public static native int FillConsoleOutputCharacterW(long paramLong, char paramChar, int paramInt, COORD paramCOORD, int[] paramArrayOfint);
  
  public static native int FillConsoleOutputAttribute(long paramLong, short paramShort, int paramInt, COORD paramCOORD, int[] paramArrayOfint);
  
  public static native int WriteConsoleW(long paramLong1, char[] paramArrayOfchar, int paramInt, int[] paramArrayOfint, long paramLong2);
  
  public static native int GetConsoleMode(long paramLong, int[] paramArrayOfint);
  
  public static native int SetConsoleMode(long paramLong, int paramInt);
  
  public static native int _getch();
  
  public static native int SetConsoleTitle(String paramString);
  
  public static native int GetConsoleOutputCP();
  
  public static native int SetConsoleOutputCP(int paramInt);
  
  public static native int ScrollConsoleScreenBuffer(long paramLong, SMALL_RECT paramSMALL_RECT1, SMALL_RECT paramSMALL_RECT2, COORD paramCOORD, CHAR_INFO paramCHAR_INFO);
  
  private static native int ReadConsoleInputW(long paramLong1, long paramLong2, int paramInt, int[] paramArrayOfint);
  
  private static native int PeekConsoleInputW(long paramLong1, long paramLong2, int paramInt, int[] paramArrayOfint);
  
  public static native int GetNumberOfConsoleInputEvents(long paramLong, int[] paramArrayOfint);
  
  public static native int FlushConsoleInputBuffer(long paramLong);
}
