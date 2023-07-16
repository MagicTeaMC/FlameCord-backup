package org.fusesource.jansi.internal;

public class CLibrary {
  public static final boolean LOADED = JansiLoader.initialize();
  
  private static native void init();
  
  public static native int isatty(int paramInt);
  
  public static native String ttyname(int paramInt);
  
  public static native int openpty(int[] paramArrayOfint1, int[] paramArrayOfint2, byte[] paramArrayOfbyte, Termios paramTermios, WinSize paramWinSize);
  
  public static native int tcgetattr(int paramInt, Termios paramTermios);
  
  public static native int tcsetattr(int paramInt1, int paramInt2, Termios paramTermios);
  
  public static native int ioctl(int paramInt, long paramLong, int[] paramArrayOfint);
  
  public static native int ioctl(int paramInt, long paramLong, WinSize paramWinSize);
  
  static {
    if (LOADED)
      init(); 
  }
  
  public static int STDOUT_FILENO = 1;
  
  public static int STDERR_FILENO = 2;
  
  public static boolean HAVE_ISATTY;
  
  public static boolean HAVE_TTYNAME;
  
  public static int TCSANOW;
  
  public static int TCSADRAIN;
  
  public static int TCSAFLUSH;
  
  public static long TIOCGETA;
  
  public static long TIOCSETA;
  
  public static long TIOCGETD;
  
  public static long TIOCSETD;
  
  public static long TIOCGWINSZ;
  
  public static long TIOCSWINSZ;
  
  public static class WinSize {
    public static int SIZEOF;
    
    public short ws_row;
    
    public short ws_col;
    
    public short ws_xpixel;
    
    public short ws_ypixel;
    
    static {
      JansiLoader.initialize();
      init();
    }
    
    public WinSize() {}
    
    public WinSize(short ws_row, short ws_col) {
      this.ws_row = ws_row;
      this.ws_col = ws_col;
    }
    
    private static native void init();
  }
  
  public static class Termios {
    public static int SIZEOF;
    
    public long c_iflag;
    
    public long c_oflag;
    
    public long c_cflag;
    
    public long c_lflag;
    
    private static native void init();
    
    static {
      JansiLoader.initialize();
      init();
    }
    
    public byte[] c_cc = new byte[32];
    
    public long c_ispeed;
    
    public long c_ospeed;
  }
}
