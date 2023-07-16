package META-INF.versions.9.org.yaml.snakeyaml.internal;

public class Logger {
  private final System.Logger logger;
  
  private Logger(String name) {
    this.logger = System.getLogger(name);
  }
  
  public static org.yaml.snakeyaml.internal.Logger getLogger(String name) {
    return new org.yaml.snakeyaml.internal.Logger(name);
  }
  
  public boolean isLoggable(Level level) {
    return this.logger.isLoggable(Level.access$000(level));
  }
  
  public void warn(String msg) {
    this.logger.log(Level.access$000(Level.WARNING), msg);
  }
}
