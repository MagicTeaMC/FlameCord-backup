package org.apache.logging.log4j.core.net.ssl;

import java.util.Objects;

class EnvironmentPasswordProvider implements PasswordProvider {
  private final String passwordEnvironmentVariable;
  
  public EnvironmentPasswordProvider(String passwordEnvironmentVariable) {
    this.passwordEnvironmentVariable = Objects.<String>requireNonNull(passwordEnvironmentVariable, "passwordEnvironmentVariable");
  }
  
  public char[] getPassword() {
    String password = System.getenv(this.passwordEnvironmentVariable);
    return (password == null) ? null : password.toCharArray();
  }
}
