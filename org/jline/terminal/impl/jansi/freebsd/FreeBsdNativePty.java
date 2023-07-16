package org.jline.terminal.impl.jansi.freebsd;

import java.io.FileDescriptor;
import java.io.IOException;
import java.util.EnumMap;
import java.util.EnumSet;
import org.fusesource.jansi.internal.CLibrary;
import org.jline.terminal.Attributes;
import org.jline.terminal.Size;
import org.jline.terminal.impl.jansi.JansiNativePty;

public class FreeBsdNativePty extends JansiNativePty {
  private static final int VEOF = 0;
  
  private static final int VEOL = 1;
  
  private static final int VEOL2 = 2;
  
  private static final int VERASE = 3;
  
  private static final int VWERASE = 4;
  
  private static final int VKILL = 5;
  
  private static final int VREPRINT = 6;
  
  private static final int VERASE2 = 7;
  
  private static final int VINTR = 8;
  
  private static final int VQUIT = 9;
  
  private static final int VSUSP = 10;
  
  private static final int VDSUSP = 11;
  
  private static final int VSTART = 12;
  
  private static final int VSTOP = 13;
  
  private static final int VLNEXT = 14;
  
  private static final int VDISCARD = 15;
  
  private static final int VMIN = 16;
  
  private static final int VTIME = 17;
  
  private static final int VSTATUS = 18;
  
  private static final int IGNBRK = 1;
  
  private static final int BRKINT = 2;
  
  private static final int IGNPAR = 4;
  
  private static final int PARMRK = 8;
  
  private static final int INPCK = 16;
  
  private static final int ISTRIP = 32;
  
  private static final int INLCR = 64;
  
  private static final int IGNCR = 128;
  
  private static final int ICRNL = 256;
  
  private static final int IXON = 512;
  
  private static final int IXOFF = 1024;
  
  private static final int IXANY = 2048;
  
  private static final int IMAXBEL = 8192;
  
  private static final int OPOST = 1;
  
  private static final int ONLCR = 2;
  
  private static final int TABDLY = 4;
  
  private static final int TAB0 = 0;
  
  private static final int TAB3 = 4;
  
  private static final int ONOEOT = 8;
  
  private static final int OCRNL = 16;
  
  private static final int ONLRET = 64;
  
  private static final int CIGNORE = 1;
  
  private static final int CSIZE = 768;
  
  private static final int CS5 = 0;
  
  private static final int CS6 = 256;
  
  private static final int CS7 = 512;
  
  private static final int CS8 = 768;
  
  private static final int CSTOPB = 1024;
  
  private static final int CREAD = 2048;
  
  private static final int PARENB = 4096;
  
  private static final int PARODD = 8192;
  
  private static final int HUPCL = 16384;
  
  private static final int CLOCAL = 32768;
  
  private static final int ECHOKE = 1;
  
  private static final int ECHOE = 2;
  
  private static final int ECHOK = 4;
  
  private static final int ECHO = 8;
  
  private static final int ECHONL = 16;
  
  private static final int ECHOPRT = 32;
  
  private static final int ECHOCTL = 64;
  
  private static final int ISIG = 128;
  
  private static final int ICANON = 256;
  
  private static final int ALTWERASE = 512;
  
  private static final int IEXTEN = 1024;
  
  private static final int EXTPROC = 2048;
  
  private static final int TOSTOP = 4194304;
  
  private static final int FLUSHO = 8388608;
  
  private static final int PENDIN = 33554432;
  
  private static final int NOFLSH = 134217728;
  
  public static FreeBsdNativePty current() throws IOException {
    try {
      String name = ttyname();
      return new FreeBsdNativePty(-1, null, 0, FileDescriptor.in, 1, FileDescriptor.out, name);
    } catch (IOException e) {
      throw new IOException("Not a tty", e);
    } 
  }
  
  public static FreeBsdNativePty open(Attributes attr, Size size) throws IOException {
    int[] master = new int[1];
    int[] slave = new int[1];
    byte[] buf = new byte[64];
    CLibrary.openpty(master, slave, buf, 
        (attr != null) ? termios(attr) : null, 
        (size != null) ? new CLibrary.WinSize((short)size.getRows(), (short)size.getColumns()) : null);
    int len = 0;
    while (buf[len] != 0)
      len++; 
    String name = new String(buf, 0, len);
    return new FreeBsdNativePty(master[0], newDescriptor(master[0]), slave[0], newDescriptor(slave[0]), name);
  }
  
  public FreeBsdNativePty(int master, FileDescriptor masterFD, int slave, FileDescriptor slaveFD, String name) {
    super(master, masterFD, slave, slaveFD, name);
  }
  
  public FreeBsdNativePty(int master, FileDescriptor masterFD, int slave, FileDescriptor slaveFD, int slaveOut, FileDescriptor slaveOutFD, String name) {
    super(master, masterFD, slave, slaveFD, slaveOut, slaveOutFD, name);
  }
  
  protected CLibrary.Termios toTermios(Attributes t) {
    return termios(t);
  }
  
  static CLibrary.Termios termios(Attributes t) {
    CLibrary.Termios tio = new CLibrary.Termios();
    tio.c_iflag = setFlag(t.getInputFlag(Attributes.InputFlag.IGNBRK), 1L, tio.c_iflag);
    tio.c_iflag = setFlag(t.getInputFlag(Attributes.InputFlag.BRKINT), 2L, tio.c_iflag);
    tio.c_iflag = setFlag(t.getInputFlag(Attributes.InputFlag.IGNPAR), 4L, tio.c_iflag);
    tio.c_iflag = setFlag(t.getInputFlag(Attributes.InputFlag.PARMRK), 8L, tio.c_iflag);
    tio.c_iflag = setFlag(t.getInputFlag(Attributes.InputFlag.INPCK), 16L, tio.c_iflag);
    tio.c_iflag = setFlag(t.getInputFlag(Attributes.InputFlag.ISTRIP), 32L, tio.c_iflag);
    tio.c_iflag = setFlag(t.getInputFlag(Attributes.InputFlag.INLCR), 64L, tio.c_iflag);
    tio.c_iflag = setFlag(t.getInputFlag(Attributes.InputFlag.IGNCR), 128L, tio.c_iflag);
    tio.c_iflag = setFlag(t.getInputFlag(Attributes.InputFlag.ICRNL), 256L, tio.c_iflag);
    tio.c_iflag = setFlag(t.getInputFlag(Attributes.InputFlag.IXON), 512L, tio.c_iflag);
    tio.c_iflag = setFlag(t.getInputFlag(Attributes.InputFlag.IXOFF), 1024L, tio.c_iflag);
    tio.c_iflag = setFlag(t.getInputFlag(Attributes.InputFlag.IXANY), 2048L, tio.c_iflag);
    tio.c_iflag = setFlag(t.getInputFlag(Attributes.InputFlag.IMAXBEL), 8192L, tio.c_iflag);
    tio.c_oflag = setFlag(t.getOutputFlag(Attributes.OutputFlag.OPOST), 1L, tio.c_oflag);
    tio.c_oflag = setFlag(t.getOutputFlag(Attributes.OutputFlag.ONLCR), 2L, tio.c_oflag);
    tio.c_oflag = setFlag(t.getOutputFlag(Attributes.OutputFlag.ONOEOT), 8L, tio.c_oflag);
    tio.c_oflag = setFlag(t.getOutputFlag(Attributes.OutputFlag.OCRNL), 16L, tio.c_oflag);
    tio.c_oflag = setFlag(t.getOutputFlag(Attributes.OutputFlag.ONLRET), 64L, tio.c_oflag);
    tio.c_oflag = setFlag(t.getOutputFlag(Attributes.OutputFlag.TABDLY), 4L, tio.c_oflag);
    tio.c_cflag = setFlag(t.getControlFlag(Attributes.ControlFlag.CIGNORE), 1L, tio.c_cflag);
    tio.c_cflag = setFlag(t.getControlFlag(Attributes.ControlFlag.CS5), 0L, tio.c_cflag);
    tio.c_cflag = setFlag(t.getControlFlag(Attributes.ControlFlag.CS6), 256L, tio.c_cflag);
    tio.c_cflag = setFlag(t.getControlFlag(Attributes.ControlFlag.CS7), 512L, tio.c_cflag);
    tio.c_cflag = setFlag(t.getControlFlag(Attributes.ControlFlag.CS8), 768L, tio.c_cflag);
    tio.c_cflag = setFlag(t.getControlFlag(Attributes.ControlFlag.CSTOPB), 1024L, tio.c_cflag);
    tio.c_cflag = setFlag(t.getControlFlag(Attributes.ControlFlag.CREAD), 2048L, tio.c_cflag);
    tio.c_cflag = setFlag(t.getControlFlag(Attributes.ControlFlag.PARENB), 4096L, tio.c_cflag);
    tio.c_cflag = setFlag(t.getControlFlag(Attributes.ControlFlag.PARODD), 8192L, tio.c_cflag);
    tio.c_cflag = setFlag(t.getControlFlag(Attributes.ControlFlag.HUPCL), 16384L, tio.c_cflag);
    tio.c_cflag = setFlag(t.getControlFlag(Attributes.ControlFlag.CLOCAL), 32768L, tio.c_cflag);
    tio.c_lflag = setFlag(t.getLocalFlag(Attributes.LocalFlag.ECHOKE), 1L, tio.c_lflag);
    tio.c_lflag = setFlag(t.getLocalFlag(Attributes.LocalFlag.ECHOE), 2L, tio.c_lflag);
    tio.c_lflag = setFlag(t.getLocalFlag(Attributes.LocalFlag.ECHOK), 4L, tio.c_lflag);
    tio.c_lflag = setFlag(t.getLocalFlag(Attributes.LocalFlag.ECHO), 8L, tio.c_lflag);
    tio.c_lflag = setFlag(t.getLocalFlag(Attributes.LocalFlag.ECHONL), 16L, tio.c_lflag);
    tio.c_lflag = setFlag(t.getLocalFlag(Attributes.LocalFlag.ECHOPRT), 32L, tio.c_lflag);
    tio.c_lflag = setFlag(t.getLocalFlag(Attributes.LocalFlag.ECHOCTL), 64L, tio.c_lflag);
    tio.c_lflag = setFlag(t.getLocalFlag(Attributes.LocalFlag.ISIG), 128L, tio.c_lflag);
    tio.c_lflag = setFlag(t.getLocalFlag(Attributes.LocalFlag.ICANON), 256L, tio.c_lflag);
    tio.c_lflag = setFlag(t.getLocalFlag(Attributes.LocalFlag.ALTWERASE), 512L, tio.c_lflag);
    tio.c_lflag = setFlag(t.getLocalFlag(Attributes.LocalFlag.IEXTEN), 1024L, tio.c_lflag);
    tio.c_lflag = setFlag(t.getLocalFlag(Attributes.LocalFlag.EXTPROC), 2048L, tio.c_lflag);
    tio.c_lflag = setFlag(t.getLocalFlag(Attributes.LocalFlag.TOSTOP), 4194304L, tio.c_lflag);
    tio.c_lflag = setFlag(t.getLocalFlag(Attributes.LocalFlag.FLUSHO), 8388608L, tio.c_lflag);
    tio.c_lflag = setFlag(t.getLocalFlag(Attributes.LocalFlag.PENDIN), 33554432L, tio.c_lflag);
    tio.c_lflag = setFlag(t.getLocalFlag(Attributes.LocalFlag.NOFLSH), 134217728L, tio.c_lflag);
    tio.c_cc[0] = (byte)t.getControlChar(Attributes.ControlChar.VEOF);
    tio.c_cc[1] = (byte)t.getControlChar(Attributes.ControlChar.VEOL);
    tio.c_cc[2] = (byte)t.getControlChar(Attributes.ControlChar.VEOL2);
    tio.c_cc[3] = (byte)t.getControlChar(Attributes.ControlChar.VERASE);
    tio.c_cc[4] = (byte)t.getControlChar(Attributes.ControlChar.VWERASE);
    tio.c_cc[5] = (byte)t.getControlChar(Attributes.ControlChar.VKILL);
    tio.c_cc[6] = (byte)t.getControlChar(Attributes.ControlChar.VREPRINT);
    tio.c_cc[8] = (byte)t.getControlChar(Attributes.ControlChar.VINTR);
    tio.c_cc[9] = (byte)t.getControlChar(Attributes.ControlChar.VQUIT);
    tio.c_cc[10] = (byte)t.getControlChar(Attributes.ControlChar.VSUSP);
    tio.c_cc[12] = (byte)t.getControlChar(Attributes.ControlChar.VSTART);
    tio.c_cc[13] = (byte)t.getControlChar(Attributes.ControlChar.VSTOP);
    tio.c_cc[14] = (byte)t.getControlChar(Attributes.ControlChar.VLNEXT);
    tio.c_cc[15] = (byte)t.getControlChar(Attributes.ControlChar.VDISCARD);
    tio.c_cc[16] = (byte)t.getControlChar(Attributes.ControlChar.VMIN);
    tio.c_cc[17] = (byte)t.getControlChar(Attributes.ControlChar.VTIME);
    return tio;
  }
  
  protected Attributes toAttributes(CLibrary.Termios tio) {
    Attributes attr = new Attributes();
    EnumSet<Attributes.InputFlag> iflag = attr.getInputFlags();
    addFlag(tio.c_iflag, iflag, Attributes.InputFlag.IGNBRK, 1);
    addFlag(tio.c_iflag, iflag, Attributes.InputFlag.IGNBRK, 1);
    addFlag(tio.c_iflag, iflag, Attributes.InputFlag.BRKINT, 2);
    addFlag(tio.c_iflag, iflag, Attributes.InputFlag.IGNPAR, 4);
    addFlag(tio.c_iflag, iflag, Attributes.InputFlag.PARMRK, 8);
    addFlag(tio.c_iflag, iflag, Attributes.InputFlag.INPCK, 16);
    addFlag(tio.c_iflag, iflag, Attributes.InputFlag.ISTRIP, 32);
    addFlag(tio.c_iflag, iflag, Attributes.InputFlag.INLCR, 64);
    addFlag(tio.c_iflag, iflag, Attributes.InputFlag.IGNCR, 128);
    addFlag(tio.c_iflag, iflag, Attributes.InputFlag.ICRNL, 256);
    addFlag(tio.c_iflag, iflag, Attributes.InputFlag.IXON, 512);
    addFlag(tio.c_iflag, iflag, Attributes.InputFlag.IXOFF, 1024);
    addFlag(tio.c_iflag, iflag, Attributes.InputFlag.IXANY, 2048);
    addFlag(tio.c_iflag, iflag, Attributes.InputFlag.IMAXBEL, 8192);
    EnumSet<Attributes.OutputFlag> oflag = attr.getOutputFlags();
    addFlag(tio.c_oflag, oflag, Attributes.OutputFlag.OPOST, 1);
    addFlag(tio.c_oflag, oflag, Attributes.OutputFlag.ONLCR, 2);
    addFlag(tio.c_oflag, oflag, Attributes.OutputFlag.ONOEOT, 8);
    addFlag(tio.c_oflag, oflag, Attributes.OutputFlag.OCRNL, 16);
    addFlag(tio.c_oflag, oflag, Attributes.OutputFlag.ONLRET, 64);
    addFlag(tio.c_oflag, oflag, Attributes.OutputFlag.TABDLY, 4);
    EnumSet<Attributes.ControlFlag> cflag = attr.getControlFlags();
    addFlag(tio.c_cflag, cflag, Attributes.ControlFlag.CIGNORE, 1);
    addFlag(tio.c_cflag, cflag, Attributes.ControlFlag.CS5, 0);
    addFlag(tio.c_cflag, cflag, Attributes.ControlFlag.CS6, 256);
    addFlag(tio.c_cflag, cflag, Attributes.ControlFlag.CS7, 512);
    addFlag(tio.c_cflag, cflag, Attributes.ControlFlag.CS8, 768);
    addFlag(tio.c_cflag, cflag, Attributes.ControlFlag.CSTOPB, 1024);
    addFlag(tio.c_cflag, cflag, Attributes.ControlFlag.CREAD, 2048);
    addFlag(tio.c_cflag, cflag, Attributes.ControlFlag.PARENB, 4096);
    addFlag(tio.c_cflag, cflag, Attributes.ControlFlag.PARODD, 8192);
    addFlag(tio.c_cflag, cflag, Attributes.ControlFlag.HUPCL, 16384);
    addFlag(tio.c_cflag, cflag, Attributes.ControlFlag.CLOCAL, 32768);
    EnumSet<Attributes.LocalFlag> lflag = attr.getLocalFlags();
    addFlag(tio.c_lflag, lflag, Attributes.LocalFlag.ECHOKE, 1);
    addFlag(tio.c_lflag, lflag, Attributes.LocalFlag.ECHOE, 2);
    addFlag(tio.c_lflag, lflag, Attributes.LocalFlag.ECHOK, 4);
    addFlag(tio.c_lflag, lflag, Attributes.LocalFlag.ECHO, 8);
    addFlag(tio.c_lflag, lflag, Attributes.LocalFlag.ECHONL, 16);
    addFlag(tio.c_lflag, lflag, Attributes.LocalFlag.ECHOPRT, 32);
    addFlag(tio.c_lflag, lflag, Attributes.LocalFlag.ECHOCTL, 64);
    addFlag(tio.c_lflag, lflag, Attributes.LocalFlag.ISIG, 128);
    addFlag(tio.c_lflag, lflag, Attributes.LocalFlag.ICANON, 256);
    addFlag(tio.c_lflag, lflag, Attributes.LocalFlag.ALTWERASE, 512);
    addFlag(tio.c_lflag, lflag, Attributes.LocalFlag.IEXTEN, 1024);
    addFlag(tio.c_lflag, lflag, Attributes.LocalFlag.EXTPROC, 2048);
    addFlag(tio.c_lflag, lflag, Attributes.LocalFlag.TOSTOP, 4194304);
    addFlag(tio.c_lflag, lflag, Attributes.LocalFlag.FLUSHO, 8388608);
    addFlag(tio.c_lflag, lflag, Attributes.LocalFlag.PENDIN, 33554432);
    addFlag(tio.c_lflag, lflag, Attributes.LocalFlag.NOFLSH, 134217728);
    EnumMap<Attributes.ControlChar, Integer> cc = attr.getControlChars();
    cc.put(Attributes.ControlChar.VEOF, Integer.valueOf(tio.c_cc[0]));
    cc.put(Attributes.ControlChar.VEOL, Integer.valueOf(tio.c_cc[1]));
    cc.put(Attributes.ControlChar.VEOL2, Integer.valueOf(tio.c_cc[2]));
    cc.put(Attributes.ControlChar.VERASE, Integer.valueOf(tio.c_cc[3]));
    cc.put(Attributes.ControlChar.VWERASE, Integer.valueOf(tio.c_cc[4]));
    cc.put(Attributes.ControlChar.VKILL, Integer.valueOf(tio.c_cc[5]));
    cc.put(Attributes.ControlChar.VREPRINT, Integer.valueOf(tio.c_cc[6]));
    cc.put(Attributes.ControlChar.VINTR, Integer.valueOf(tio.c_cc[8]));
    cc.put(Attributes.ControlChar.VQUIT, Integer.valueOf(tio.c_cc[9]));
    cc.put(Attributes.ControlChar.VSUSP, Integer.valueOf(tio.c_cc[10]));
    cc.put(Attributes.ControlChar.VSTART, Integer.valueOf(tio.c_cc[12]));
    cc.put(Attributes.ControlChar.VSTOP, Integer.valueOf(tio.c_cc[13]));
    cc.put(Attributes.ControlChar.VLNEXT, Integer.valueOf(tio.c_cc[14]));
    cc.put(Attributes.ControlChar.VDISCARD, Integer.valueOf(tio.c_cc[15]));
    cc.put(Attributes.ControlChar.VMIN, Integer.valueOf(tio.c_cc[16]));
    cc.put(Attributes.ControlChar.VTIME, Integer.valueOf(tio.c_cc[17]));
    return attr;
  }
  
  private static long setFlag(boolean flag, long value, long org) {
    return flag ? (org | value) : org;
  }
  
  private static <T extends Enum<T>> void addFlag(long value, EnumSet<T> flags, T flag, int v) {
    if ((value & v) != 0L)
      flags.add(flag); 
  }
}
