package org.fusesource.jansi;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.util.Properties;
import org.fusesource.jansi.internal.CLibrary;
import org.fusesource.jansi.internal.JansiLoader;

public class AnsiMain {
  public static void main(String... args) throws IOException {
    System.out.println("Jansi " + getJansiVersion());
    System.out.println();
    System.out.println("library.jansi.path= " + System.getProperty("library.jansi.path", ""));
    System.out.println("library.jansi.version= " + System.getProperty("library.jansi.version", ""));
    boolean loaded = JansiLoader.initialize();
    if (loaded) {
      System.out.println("Jansi native library loaded from " + JansiLoader.getNativeLibraryPath());
      if (JansiLoader.getNativeLibrarySourceUrl() != null)
        System.out.println("   which was auto-extracted from " + JansiLoader.getNativeLibrarySourceUrl()); 
    } else {
      String prev = System.getProperty("jansi.graceful");
      try {
        System.setProperty("jansi.graceful", "false");
        JansiLoader.initialize();
      } catch (Throwable e) {
        e.printStackTrace(System.out);
      } finally {
        if (prev != null) {
          System.setProperty("jansi.graceful", prev);
        } else {
          System.clearProperty("jansi.graceful");
        } 
      } 
    } 
    System.out.println();
    System.out.println("os.name= " + System.getProperty("os.name") + ", os.version= " + 
        System.getProperty("os.version") + ", os.arch= " + 
        System.getProperty("os.arch"));
    System.out.println("file.encoding= " + System.getProperty("file.encoding"));
    System.out.println("java.version= " + System.getProperty("java.version") + ", java.vendor= " + 
        System.getProperty("java.vendor") + ", java.home= " + 
        System.getProperty("java.home"));
    System.out.println();
    System.out.println("jansi.graceful= " + System.getProperty("jansi.graceful", ""));
    System.out.println("jansi.mode= " + System.getProperty("jansi.mode", ""));
    System.out.println("jansi.out.mode= " + System.getProperty("jansi.out.mode", ""));
    System.out.println("jansi.err.mode= " + System.getProperty("jansi.err.mode", ""));
    System.out.println("jansi.colors= " + System.getProperty("jansi.colors", ""));
    System.out.println("jansi.out.colors= " + System.getProperty("jansi.out.colors", ""));
    System.out.println("jansi.err.colors= " + System.getProperty("jansi.err.colors", ""));
    System.out.println("jansi.passthrough= " + AnsiConsole.getBoolean("jansi.passthrough"));
    System.out.println("jansi.strip= " + AnsiConsole.getBoolean("jansi.strip"));
    System.out.println("jansi.force= " + AnsiConsole.getBoolean("jansi.force"));
    System.out.println("jansi.noreset= " + AnsiConsole.getBoolean("jansi.noreset"));
    System.out.println(Ansi.DISABLE + "= " + AnsiConsole.getBoolean(Ansi.DISABLE));
    System.out.println();
    System.out.println("IS_WINDOWS: " + AnsiConsole.IS_WINDOWS);
    if (AnsiConsole.IS_WINDOWS) {
      System.out.println("IS_CONEMU: " + AnsiConsole.IS_CONEMU);
      System.out.println("IS_CYGWIN: " + AnsiConsole.IS_CYGWIN);
      System.out.println("IS_MSYSTEM: " + AnsiConsole.IS_MSYSTEM);
    } 
    System.out.println();
    diagnoseTty(false);
    diagnoseTty(true);
    AnsiConsole.systemInstall();
    System.out.println();
    System.out.println("Resulting Jansi modes for stout/stderr streams:");
    System.out.println("  - System.out: " + AnsiConsole.out().toString());
    System.out.println("  - System.err: " + AnsiConsole.err().toString());
    System.out.println("Processor types description:");
    for (AnsiType type : AnsiType.values())
      System.out.println("  - " + type + ": " + type.getDescription()); 
    System.out.println("Colors support description:");
    for (AnsiColors colors : AnsiColors.values())
      System.out.println("  - " + colors + ": " + colors.getDescription()); 
    System.out.println("Modes description:");
    for (AnsiMode mode : AnsiMode.values())
      System.out.println("  - " + mode + ": " + mode.getDescription()); 
    try {
      System.out.println();
      testAnsi(false);
      testAnsi(true);
      if (args.length == 0) {
        printJansiLogoDemo();
        return;
      } 
      System.out.println();
      if (args.length == 1) {
        File f = new File(args[0]);
        if (f.exists()) {
          System.out.println(Ansi.ansi().bold().a("\"" + args[0] + "\" content:").reset());
          writeFileContent(f);
          return;
        } 
      } 
      System.out.println(Ansi.ansi().bold().a("original args:").reset());
      int i = 1;
      for (String arg : args) {
        AnsiConsole.system_out.print(i++ + ": ");
        AnsiConsole.system_out.println(arg);
      } 
      System.out.println(Ansi.ansi().bold().a("Jansi filtered args:").reset());
      i = 1;
      for (String arg : args) {
        System.out.print(i++ + ": ");
        System.out.println(arg);
      } 
    } finally {
      AnsiConsole.systemUninstall();
    } 
  }
  
  private static String getJansiVersion() {
    Package p = AnsiMain.class.getPackage();
    return (p == null) ? null : p.getImplementationVersion();
  }
  
  private static void diagnoseTty(boolean stderr) {
    int fd = stderr ? CLibrary.STDERR_FILENO : CLibrary.STDOUT_FILENO;
    int isatty = CLibrary.LOADED ? CLibrary.isatty(fd) : 0;
    System.out.println("isatty(STD" + (stderr ? "ERR" : "OUT") + "_FILENO): " + isatty + ", System." + (
        stderr ? "err" : "out") + " " + ((isatty == 0) ? "is *NOT*" : "is") + " a terminal");
  }
  
  private static void testAnsi(boolean stderr) {
    PrintStream s = stderr ? System.err : System.out;
    s.print("test on System." + (stderr ? "err" : "out") + ":");
    for (Ansi.Color c : Ansi.Color.values())
      s.print(" " + Ansi.ansi().fg(c) + c + Ansi.ansi().reset()); 
    s.println();
    s.print("            bright:");
    for (Ansi.Color c : Ansi.Color.values())
      s.print(" " + Ansi.ansi().fgBright(c) + c + Ansi.ansi().reset()); 
    s.println();
    s.print("              bold:");
    for (Ansi.Color c : Ansi.Color.values())
      s.print(" " + Ansi.ansi().bold().fg(c) + c + Ansi.ansi().reset()); 
    s.println();
    s.print("             faint:");
    for (Ansi.Color c : Ansi.Color.values())
      s.print(" " + Ansi.ansi().a(Ansi.Attribute.INTENSITY_FAINT).fg(c) + c + Ansi.ansi().reset()); 
    s.println();
    s.print("        bold+faint:");
    for (Ansi.Color c : Ansi.Color.values())
      s.print(" " + Ansi.ansi().bold().a(Ansi.Attribute.INTENSITY_FAINT).fg(c) + c + Ansi.ansi().reset()); 
    s.println();
    Ansi ansi = Ansi.ansi();
    ansi.a("        256 colors: ");
    int i;
    for (i = 0; i < 216; i++) {
      if (i > 0 && i % 36 == 0) {
        ansi.reset();
        ansi.newline();
        ansi.a("                    ");
      } else if (i > 0 && i % 6 == 0) {
        ansi.reset();
        ansi.a("  ");
      } 
      int a0 = i % 6;
      int a1 = i / 6 % 6;
      int a2 = i / 36;
      ansi.bg(16 + a0 + a2 * 6 + a1 * 36).a(' ');
    } 
    ansi.reset();
    s.println(ansi);
    ansi = Ansi.ansi();
    ansi.a("         truecolor: ");
    for (i = 0; i < 256; i++) {
      if (i > 0 && i % 48 == 0) {
        ansi.reset();
        ansi.newline();
        ansi.a("                    ");
      } 
      int r = 255 - i;
      int g = (i * 2 > 255) ? (255 - 2 * i) : (2 * i);
      int b = i;
      ansi.bgRgb(r, g, b).fgRgb(255 - r, 255 - g, 255 - b).a((i % 2 == 0) ? 47 : 92);
    } 
    ansi.reset();
    s.println(ansi);
  }
  
  private static String getPomPropertiesVersion(String path) throws IOException {
    InputStream in = AnsiMain.class.getResourceAsStream("/META-INF/maven/" + path + "/pom.properties");
    if (in == null)
      return null; 
    try {
      Properties p = new Properties();
      p.load(in);
      return p.getProperty("version");
    } finally {
      closeQuietly(in);
    } 
  }
  
  private static void printJansiLogoDemo() throws IOException {
    BufferedReader in = new BufferedReader(new InputStreamReader(AnsiMain.class.getResourceAsStream("jansi.txt"), "UTF-8"));
    try {
      String l;
      while ((l = in.readLine()) != null)
        System.out.println(l); 
    } finally {
      closeQuietly(in);
    } 
  }
  
  private static void writeFileContent(File f) throws IOException {
    InputStream in = new FileInputStream(f);
    try {
      byte[] buf = new byte[1024];
      int l = 0;
      while ((l = in.read(buf)) >= 0)
        System.out.write(buf, 0, l); 
    } finally {
      closeQuietly(in);
    } 
  }
  
  private static void closeQuietly(Closeable c) {
    try {
      c.close();
    } catch (IOException ioe) {
      ioe.printStackTrace(System.err);
    } 
  }
}
