package io.github.waterfallmc.waterfall.log4j;

import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.io.IoBuilder;

public final class WaterfallLogger {
  public static Logger create() {
    Logger redirect = LogManager.getRootLogger();
    System.setOut(IoBuilder.forLogger(redirect).setLevel(Level.INFO).buildPrintStream());
    System.setErr(IoBuilder.forLogger(redirect).setLevel(Level.ERROR).buildPrintStream());
    Logger root = Logger.getLogger("");
    root.setUseParentHandlers(false);
    for (Handler handler : root.getHandlers())
      root.removeHandler(handler); 
    root.setLevel(Level.ALL);
    root.addHandler(new Log4JLogHandler());
    return Logger.getLogger("BungeeCord");
  }
}
