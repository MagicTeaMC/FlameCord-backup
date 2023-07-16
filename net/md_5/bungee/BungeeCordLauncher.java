package net.md_5.bungee;

import io.github.waterfallmc.waterfall.console.WaterfallConsole;
import java.security.Security;
import java.util.Arrays;
import joptsimple.OptionParser;
import joptsimple.OptionSet;
import net.md_5.bungee.api.ProxyServer;

public class BungeeCordLauncher {
  public static void main(String[] args) throws Exception {
    Security.setProperty("networkaddress.cache.ttl", "30");
    Security.setProperty("networkaddress.cache.negative.ttl", "10");
    if (System.getProperty("jdk.util.jar.enableMultiRelease") == null)
      System.setProperty("jdk.util.jar.enableMultiRelease", "force"); 
    OptionParser parser = new OptionParser();
    parser.allowsUnrecognizedOptions();
    parser.acceptsAll(Arrays.asList(new String[] { "help" }, ), "Show the help");
    parser.acceptsAll(Arrays.asList(new String[] { "v", "version" }, ), "Print version and exit");
    parser.acceptsAll(Arrays.asList(new String[] { "noconsole" }, ), "Disable console input");
    OptionSet options = parser.parse(args);
    if (options.has("help")) {
      parser.printHelpOn(System.out);
      return;
    } 
    if (options.has("version")) {
      System.out.println(BungeeCord.class.getPackage().getImplementationVersion());
      return;
    } 
    BungeeCord bungee = new BungeeCord();
    ProxyServer.setInstance(bungee);
    bungee.getLogger().info("Enabled " + bungee.getName() + " version " + bungee.getVersion());
    bungee.start();
    if (!options.has("noconsole"))
      (new WaterfallConsole()).start(); 
  }
}
