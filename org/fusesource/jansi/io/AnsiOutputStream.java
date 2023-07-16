package org.fusesource.jansi.io;

import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.ArrayList;
import org.fusesource.jansi.AnsiColors;
import org.fusesource.jansi.AnsiMode;
import org.fusesource.jansi.AnsiType;

public class AnsiOutputStream extends FilterOutputStream {
  public static final byte[] RESET_CODE = "\033[0m".getBytes();
  
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
  
  private static final int FIRST_ESC_CHAR = 27;
  
  private static final int SECOND_ESC_CHAR = 91;
  
  private static final int SECOND_OSC_CHAR = 93;
  
  private static final int BEL = 7;
  
  private static final int SECOND_ST_CHAR = 92;
  
  private static final int SECOND_CHARSET0_CHAR = 40;
  
  private static final int SECOND_CHARSET1_CHAR = 41;
  
  private AnsiProcessor ap;
  
  private static final int MAX_ESCAPE_SEQUENCE_LENGTH = 100;
  
  public static class ZeroWidthSupplier implements WidthSupplier {
    public int getTerminalWidth() {
      return 0;
    }
  }
  
  private final byte[] buffer = new byte[100];
  
  private int pos = 0;
  
  private int startOfValue;
  
  private final ArrayList<Object> options = new ArrayList();
  
  private int state = 0;
  
  private final Charset cs;
  
  private final WidthSupplier width;
  
  private final AnsiProcessor processor;
  
  private final AnsiType type;
  
  private final AnsiColors colors;
  
  private final IoRunnable installer;
  
  private final IoRunnable uninstaller;
  
  private AnsiMode mode;
  
  private boolean resetAtUninstall;
  
  public AnsiOutputStream(OutputStream os, WidthSupplier width, AnsiMode mode, AnsiProcessor processor, AnsiType type, AnsiColors colors, Charset cs, IoRunnable installer, IoRunnable uninstaller, boolean resetAtUninstall) {
    super(os);
    this.width = width;
    this.processor = processor;
    this.type = type;
    this.colors = colors;
    this.installer = installer;
    this.uninstaller = uninstaller;
    this.resetAtUninstall = resetAtUninstall;
    this.cs = cs;
    setMode(mode);
  }
  
  public int getTerminalWidth() {
    return this.width.getTerminalWidth();
  }
  
  public AnsiType getType() {
    return this.type;
  }
  
  public AnsiColors getColors() {
    return this.colors;
  }
  
  public AnsiMode getMode() {
    return this.mode;
  }
  
  public void setMode(AnsiMode mode) {
    this
      
      .ap = (mode == AnsiMode.Strip) ? new AnsiProcessor(this.out) : ((mode == AnsiMode.Force || this.processor == null) ? new ColorsAnsiProcessor(this.out, this.colors) : this.processor);
    this.mode = mode;
  }
  
  public boolean isResetAtUninstall() {
    return this.resetAtUninstall;
  }
  
  public void setResetAtUninstall(boolean resetAtUninstall) {
    this.resetAtUninstall = resetAtUninstall;
  }
  
  public void write(int data) throws IOException {
    switch (this.state) {
      case 0:
        if (data == 27) {
          this.buffer[this.pos++] = (byte)data;
          this.state = 1;
          break;
        } 
        this.out.write(data);
        break;
      case 1:
        this.buffer[this.pos++] = (byte)data;
        if (data == 91) {
          this.state = 2;
          break;
        } 
        if (data == 93) {
          this.state = 5;
          break;
        } 
        if (data == 40) {
          this.options.add(Integer.valueOf(0));
          this.state = 9;
          break;
        } 
        if (data == 41) {
          this.options.add(Integer.valueOf(1));
          this.state = 9;
          break;
        } 
        reset(false);
        break;
      case 2:
        this.buffer[this.pos++] = (byte)data;
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
        processEscapeCommand(data);
        break;
      case 4:
        this.buffer[this.pos++] = (byte)data;
        if (48 > data || data > 57) {
          String strValue = new String(this.buffer, this.startOfValue, this.pos - 1 - this.startOfValue);
          Integer value = Integer.valueOf(strValue);
          this.options.add(value);
          if (data == 59) {
            this.state = 2;
            break;
          } 
          processEscapeCommand(data);
        } 
        break;
      case 3:
        this.buffer[this.pos++] = (byte)data;
        if (34 != data) {
          String value = new String(this.buffer, this.startOfValue, this.pos - 1 - this.startOfValue, this.cs);
          this.options.add(value);
          if (data == 59) {
            this.state = 2;
            break;
          } 
          processEscapeCommand(data);
        } 
        break;
      case 5:
        this.buffer[this.pos++] = (byte)data;
        if (48 <= data && data <= 57) {
          this.startOfValue = this.pos - 1;
          this.state = 6;
          break;
        } 
        reset(false);
        break;
      case 6:
        this.buffer[this.pos++] = (byte)data;
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
        this.buffer[this.pos++] = (byte)data;
        if (7 == data) {
          String value = new String(this.buffer, this.startOfValue, this.pos - 1 - this.startOfValue, this.cs);
          this.options.add(value);
          processOperatingSystemCommand();
          break;
        } 
        if (27 == data)
          this.state = 8; 
        break;
      case 8:
        this.buffer[this.pos++] = (byte)data;
        if (92 == data) {
          String value = new String(this.buffer, this.startOfValue, this.pos - 2 - this.startOfValue, this.cs);
          this.options.add(value);
          processOperatingSystemCommand();
          break;
        } 
        this.state = 7;
        break;
      case 9:
        this.options.add(Character.valueOf((char)data));
        processCharsetSelect();
        break;
    } 
    if (this.pos >= this.buffer.length)
      reset(false); 
  }
  
  private void processCharsetSelect() throws IOException {
    try {
      reset((this.ap != null && this.ap.processCharsetSelect(this.options)));
    } catch (RuntimeException e) {
      reset(true);
      throw e;
    } 
  }
  
  private void processOperatingSystemCommand() throws IOException {
    try {
      reset((this.ap != null && this.ap.processOperatingSystemCommand(this.options)));
    } catch (RuntimeException e) {
      reset(true);
      throw e;
    } 
  }
  
  private void processEscapeCommand(int data) throws IOException {
    try {
      reset((this.ap != null && this.ap.processEscapeCommand(this.options, data)));
    } catch (RuntimeException e) {
      reset(true);
      throw e;
    } 
  }
  
  private void reset(boolean skipBuffer) throws IOException {
    if (!skipBuffer)
      this.out.write(this.buffer, 0, this.pos); 
    this.pos = 0;
    this.startOfValue = 0;
    this.options.clear();
    this.state = 0;
  }
  
  public void install() throws IOException {
    if (this.installer != null)
      this.installer.run(); 
  }
  
  public void uninstall() throws IOException {
    if (this.resetAtUninstall && this.type != AnsiType.Redirected && this.type != AnsiType.Unsupported) {
      setMode(AnsiMode.Default);
      write(RESET_CODE);
      flush();
    } 
    if (this.uninstaller != null)
      this.uninstaller.run(); 
  }
  
  public void close() throws IOException {
    uninstall();
    super.close();
  }
  
  public static interface WidthSupplier {
    int getTerminalWidth();
  }
  
  public static interface IoRunnable {
    void run() throws IOException;
  }
}
