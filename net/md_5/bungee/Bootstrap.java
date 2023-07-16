package net.md_5.bungee;

public class Bootstrap {
  public static void main(String[] args) throws Exception {
    if (Float.parseFloat(System.getProperty("java.class.version")) < 52.0D) {
      System.err.println("*** ERROR *** FlameCord requires Java 8 or above to function! Please download and install it!");
      System.out.println("You can check your Java version with the command: java -version");
      return;
    } 
    BungeeCordLauncher.main(args);
  }
}