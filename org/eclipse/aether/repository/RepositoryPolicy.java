package org.eclipse.aether.repository;

public final class RepositoryPolicy {
  public static final String UPDATE_POLICY_NEVER = "never";
  
  public static final String UPDATE_POLICY_ALWAYS = "always";
  
  public static final String UPDATE_POLICY_DAILY = "daily";
  
  public static final String UPDATE_POLICY_INTERVAL = "interval";
  
  public static final String CHECKSUM_POLICY_FAIL = "fail";
  
  public static final String CHECKSUM_POLICY_WARN = "warn";
  
  public static final String CHECKSUM_POLICY_IGNORE = "ignore";
  
  private final boolean enabled;
  
  private final String updatePolicy;
  
  private final String checksumPolicy;
  
  public RepositoryPolicy() {
    this(true, "daily", "warn");
  }
  
  public RepositoryPolicy(boolean enabled, String updatePolicy, String checksumPolicy) {
    this.enabled = enabled;
    this.updatePolicy = (updatePolicy != null) ? updatePolicy : "";
    this.checksumPolicy = (checksumPolicy != null) ? checksumPolicy : "";
  }
  
  public boolean isEnabled() {
    return this.enabled;
  }
  
  public String getUpdatePolicy() {
    return this.updatePolicy;
  }
  
  public String getChecksumPolicy() {
    return this.checksumPolicy;
  }
  
  public String toString() {
    StringBuilder buffer = new StringBuilder(256);
    buffer.append("enabled=").append(isEnabled());
    buffer.append(", checksums=").append(getChecksumPolicy());
    buffer.append(", updates=").append(getUpdatePolicy());
    return buffer.toString();
  }
  
  public boolean equals(Object obj) {
    if (this == obj)
      return true; 
    if (obj == null || !getClass().equals(obj.getClass()))
      return false; 
    RepositoryPolicy that = (RepositoryPolicy)obj;
    return (this.enabled == that.enabled && this.updatePolicy.equals(that.updatePolicy) && this.checksumPolicy
      .equals(that.checksumPolicy));
  }
  
  public int hashCode() {
    int hash = 17;
    hash = hash * 31 + (this.enabled ? 1 : 0);
    hash = hash * 31 + this.updatePolicy.hashCode();
    hash = hash * 31 + this.checksumPolicy.hashCode();
    return hash;
  }
}
