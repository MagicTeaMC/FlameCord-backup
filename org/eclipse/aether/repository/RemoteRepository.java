package org.eclipse.aether.repository;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class RemoteRepository implements ArtifactRepository {
  private static final Pattern URL_PATTERN = Pattern.compile("([^:/]+(:[^:/]{2,}+(?=://))?):(//([^@/]*@)?([^/:]+))?.*");
  
  private final String id;
  
  private final String type;
  
  private final String url;
  
  private final String host;
  
  private final String protocol;
  
  private final RepositoryPolicy releasePolicy;
  
  private final RepositoryPolicy snapshotPolicy;
  
  private final Proxy proxy;
  
  private final Authentication authentication;
  
  private final List<RemoteRepository> mirroredRepositories;
  
  private final boolean repositoryManager;
  
  private final boolean blocked;
  
  RemoteRepository(Builder builder) {
    if (builder.prototype != null) {
      this.id = ((builder.delta & 0x1) != 0) ? builder.id : builder.prototype.id;
      this.type = ((builder.delta & 0x2) != 0) ? builder.type : builder.prototype.type;
      this.url = ((builder.delta & 0x4) != 0) ? builder.url : builder.prototype.url;
      this.releasePolicy = ((builder.delta & 0x8) != 0) ? builder.releasePolicy : builder.prototype.releasePolicy;
      this.snapshotPolicy = ((builder.delta & 0x10) != 0) ? builder.snapshotPolicy : builder.prototype.snapshotPolicy;
      this.proxy = ((builder.delta & 0x20) != 0) ? builder.proxy : builder.prototype.proxy;
      this.authentication = ((builder.delta & 0x40) != 0) ? builder.authentication : builder.prototype.authentication;
      this.repositoryManager = ((builder.delta & 0x100) != 0) ? builder.repositoryManager : builder.prototype.repositoryManager;
      this.blocked = ((builder.delta & 0x200) != 0) ? builder.blocked : builder.prototype.blocked;
      this
        .mirroredRepositories = ((builder.delta & 0x80) != 0) ? copy(builder.mirroredRepositories) : builder.prototype.mirroredRepositories;
    } else {
      this.id = builder.id;
      this.type = builder.type;
      this.url = builder.url;
      this.releasePolicy = builder.releasePolicy;
      this.snapshotPolicy = builder.snapshotPolicy;
      this.proxy = builder.proxy;
      this.authentication = builder.authentication;
      this.repositoryManager = builder.repositoryManager;
      this.blocked = builder.blocked;
      this.mirroredRepositories = copy(builder.mirroredRepositories);
    } 
    Matcher m = URL_PATTERN.matcher(this.url);
    if (m.matches()) {
      this.protocol = m.group(1);
      String host = m.group(5);
      this.host = (host != null) ? host : "";
    } else {
      this.protocol = "";
      this.host = "";
    } 
  }
  
  private static List<RemoteRepository> copy(List<RemoteRepository> repos) {
    if (repos == null || repos.isEmpty())
      return Collections.emptyList(); 
    return Collections.unmodifiableList(Arrays.asList(repos.toArray(new RemoteRepository[repos.size()])));
  }
  
  public String getId() {
    return this.id;
  }
  
  public String getContentType() {
    return this.type;
  }
  
  public String getUrl() {
    return this.url;
  }
  
  public String getProtocol() {
    return this.protocol;
  }
  
  public String getHost() {
    return this.host;
  }
  
  public RepositoryPolicy getPolicy(boolean snapshot) {
    return snapshot ? this.snapshotPolicy : this.releasePolicy;
  }
  
  public Proxy getProxy() {
    return this.proxy;
  }
  
  public Authentication getAuthentication() {
    return this.authentication;
  }
  
  public List<RemoteRepository> getMirroredRepositories() {
    return this.mirroredRepositories;
  }
  
  public boolean isRepositoryManager() {
    return this.repositoryManager;
  }
  
  public boolean isBlocked() {
    return this.blocked;
  }
  
  public String toString() {
    StringBuilder buffer = new StringBuilder(256);
    buffer.append(getId());
    buffer.append(" (").append(getUrl());
    buffer.append(", ").append(getContentType());
    boolean r = getPolicy(false).isEnabled(), s = getPolicy(true).isEnabled();
    if (r && s) {
      buffer.append(", releases+snapshots");
    } else if (r) {
      buffer.append(", releases");
    } else if (s) {
      buffer.append(", snapshots");
    } else {
      buffer.append(", disabled");
    } 
    if (isRepositoryManager())
      buffer.append(", managed"); 
    if (isBlocked())
      buffer.append(", blocked"); 
    buffer.append(")");
    return buffer.toString();
  }
  
  public boolean equals(Object obj) {
    if (this == obj)
      return true; 
    if (obj == null || !getClass().equals(obj.getClass()))
      return false; 
    RemoteRepository that = (RemoteRepository)obj;
    return (Objects.equals(this.url, that.url) && Objects.equals(this.type, that.type) && 
      Objects.equals(this.id, that.id) && Objects.equals(this.releasePolicy, that.releasePolicy) && 
      Objects.equals(this.snapshotPolicy, that.snapshotPolicy) && Objects.equals(this.proxy, that.proxy) && 
      Objects.equals(this.authentication, that.authentication) && 
      Objects.equals(this.mirroredRepositories, that.mirroredRepositories) && this.repositoryManager == that.repositoryManager);
  }
  
  public int hashCode() {
    int hash = 17;
    hash = hash * 31 + hash(this.url);
    hash = hash * 31 + hash(this.type);
    hash = hash * 31 + hash(this.id);
    hash = hash * 31 + hash(this.releasePolicy);
    hash = hash * 31 + hash(this.snapshotPolicy);
    hash = hash * 31 + hash(this.proxy);
    hash = hash * 31 + hash(this.authentication);
    hash = hash * 31 + hash(this.mirroredRepositories);
    hash = hash * 31 + (this.repositoryManager ? 1 : 0);
    return hash;
  }
  
  private static int hash(Object obj) {
    return (obj != null) ? obj.hashCode() : 0;
  }
  
  public static final class Builder {
    private static final RepositoryPolicy DEFAULT_POLICY = new RepositoryPolicy();
    
    static final int ID = 1;
    
    static final int TYPE = 2;
    
    static final int URL = 4;
    
    static final int RELEASES = 8;
    
    static final int SNAPSHOTS = 16;
    
    static final int PROXY = 32;
    
    static final int AUTH = 64;
    
    static final int MIRRORED = 128;
    
    static final int REPOMAN = 256;
    
    static final int BLOCKED = 512;
    
    int delta;
    
    RemoteRepository prototype;
    
    String id;
    
    String type;
    
    String url;
    
    RepositoryPolicy releasePolicy = DEFAULT_POLICY;
    
    RepositoryPolicy snapshotPolicy = DEFAULT_POLICY;
    
    Proxy proxy;
    
    Authentication authentication;
    
    List<RemoteRepository> mirroredRepositories;
    
    boolean repositoryManager;
    
    boolean blocked;
    
    public Builder(String id, String type, String url) {
      this.id = (id != null) ? id : "";
      this.type = (type != null) ? type : "";
      this.url = (url != null) ? url : "";
    }
    
    public Builder(RemoteRepository prototype) {
      this.prototype = Objects.<RemoteRepository>requireNonNull(prototype, "remote repository prototype cannot be null");
    }
    
    public RemoteRepository build() {
      if (this.prototype != null && this.delta == 0)
        return this.prototype; 
      return new RemoteRepository(this);
    }
    
    private <T> void delta(int flag, T builder, T prototype) {
      boolean equal = Objects.equals(builder, prototype);
      if (equal) {
        this.delta &= flag ^ 0xFFFFFFFF;
      } else {
        this.delta |= flag;
      } 
    }
    
    public Builder setId(String id) {
      this.id = (id != null) ? id : "";
      if (this.prototype != null)
        delta(1, this.id, this.prototype.getId()); 
      return this;
    }
    
    public Builder setContentType(String type) {
      this.type = (type != null) ? type : "";
      if (this.prototype != null)
        delta(2, this.type, this.prototype.getContentType()); 
      return this;
    }
    
    public Builder setUrl(String url) {
      this.url = (url != null) ? url : "";
      if (this.prototype != null)
        delta(4, this.url, this.prototype.getUrl()); 
      return this;
    }
    
    public Builder setPolicy(RepositoryPolicy policy) {
      this.releasePolicy = (policy != null) ? policy : DEFAULT_POLICY;
      this.snapshotPolicy = (policy != null) ? policy : DEFAULT_POLICY;
      if (this.prototype != null) {
        delta(8, this.releasePolicy, this.prototype.getPolicy(false));
        delta(16, this.snapshotPolicy, this.prototype.getPolicy(true));
      } 
      return this;
    }
    
    public Builder setReleasePolicy(RepositoryPolicy releasePolicy) {
      this.releasePolicy = (releasePolicy != null) ? releasePolicy : DEFAULT_POLICY;
      if (this.prototype != null)
        delta(8, this.releasePolicy, this.prototype.getPolicy(false)); 
      return this;
    }
    
    public Builder setSnapshotPolicy(RepositoryPolicy snapshotPolicy) {
      this.snapshotPolicy = (snapshotPolicy != null) ? snapshotPolicy : DEFAULT_POLICY;
      if (this.prototype != null)
        delta(16, this.snapshotPolicy, this.prototype.getPolicy(true)); 
      return this;
    }
    
    public Builder setProxy(Proxy proxy) {
      this.proxy = proxy;
      if (this.prototype != null)
        delta(32, this.proxy, this.prototype.getProxy()); 
      return this;
    }
    
    public Builder setAuthentication(Authentication authentication) {
      this.authentication = authentication;
      if (this.prototype != null)
        delta(64, this.authentication, this.prototype.getAuthentication()); 
      return this;
    }
    
    public Builder setMirroredRepositories(List<RemoteRepository> mirroredRepositories) {
      if (this.mirroredRepositories == null) {
        this.mirroredRepositories = new ArrayList<>();
      } else {
        this.mirroredRepositories.clear();
      } 
      if (mirroredRepositories != null)
        this.mirroredRepositories.addAll(mirroredRepositories); 
      if (this.prototype != null)
        delta(128, this.mirroredRepositories, this.prototype.getMirroredRepositories()); 
      return this;
    }
    
    public Builder addMirroredRepository(RemoteRepository mirroredRepository) {
      if (mirroredRepository != null) {
        if (this.mirroredRepositories == null) {
          this.mirroredRepositories = new ArrayList<>();
          if (this.prototype != null)
            this.mirroredRepositories.addAll(this.prototype.getMirroredRepositories()); 
        } 
        this.mirroredRepositories.add(mirroredRepository);
        if (this.prototype != null)
          this.delta |= 0x80; 
      } 
      return this;
    }
    
    public Builder setRepositoryManager(boolean repositoryManager) {
      this.repositoryManager = repositoryManager;
      if (this.prototype != null)
        delta(256, Boolean.valueOf(this.repositoryManager), Boolean.valueOf(this.prototype.isRepositoryManager())); 
      return this;
    }
    
    public Builder setBlocked(boolean blocked) {
      this.blocked = blocked;
      if (this.prototype != null)
        delta(512, Boolean.valueOf(this.blocked), Boolean.valueOf(this.prototype.isBlocked())); 
      return this;
    }
  }
}
