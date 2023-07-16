package org.apache.logging.log4j.core.net.ssl;

import java.util.Arrays;

class MemoryPasswordProvider implements PasswordProvider {
  private final char[] password;
  
  public MemoryPasswordProvider(char[] chars) {
    if (chars != null) {
      this.password = (char[])chars.clone();
    } else {
      this.password = null;
    } 
  }
  
  public char[] getPassword() {
    if (this.password == null)
      return null; 
    return (char[])this.password.clone();
  }
  
  public void clearSecrets() {
    Arrays.fill(this.password, false);
  }
}
