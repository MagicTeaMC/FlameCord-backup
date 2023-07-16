package org.eclipse.aether;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import org.eclipse.aether.artifact.Artifact;
import org.eclipse.aether.artifact.ArtifactType;
import org.eclipse.aether.artifact.ArtifactTypeRegistry;
import org.eclipse.aether.collection.DependencyGraphTransformer;
import org.eclipse.aether.collection.DependencyManager;
import org.eclipse.aether.collection.DependencySelector;
import org.eclipse.aether.collection.DependencyTraverser;
import org.eclipse.aether.collection.VersionFilter;
import org.eclipse.aether.repository.Authentication;
import org.eclipse.aether.repository.AuthenticationSelector;
import org.eclipse.aether.repository.LocalRepository;
import org.eclipse.aether.repository.LocalRepositoryManager;
import org.eclipse.aether.repository.MirrorSelector;
import org.eclipse.aether.repository.Proxy;
import org.eclipse.aether.repository.ProxySelector;
import org.eclipse.aether.repository.RemoteRepository;
import org.eclipse.aether.repository.WorkspaceReader;
import org.eclipse.aether.resolution.ArtifactDescriptorPolicy;
import org.eclipse.aether.resolution.ResolutionErrorPolicy;
import org.eclipse.aether.transfer.TransferListener;
import org.eclipse.aether.transform.FileTransformer;
import org.eclipse.aether.transform.FileTransformerManager;

public final class DefaultRepositorySystemSession implements RepositorySystemSession {
  private boolean readOnly;
  
  private boolean offline;
  
  private boolean ignoreArtifactDescriptorRepositories;
  
  private ResolutionErrorPolicy resolutionErrorPolicy;
  
  private ArtifactDescriptorPolicy artifactDescriptorPolicy;
  
  private String checksumPolicy;
  
  private String updatePolicy;
  
  private LocalRepositoryManager localRepositoryManager;
  
  private FileTransformerManager fileTransformerManager;
  
  private WorkspaceReader workspaceReader;
  
  private RepositoryListener repositoryListener;
  
  private TransferListener transferListener;
  
  private Map<String, String> systemProperties;
  
  private Map<String, String> systemPropertiesView;
  
  private Map<String, String> userProperties;
  
  private Map<String, String> userPropertiesView;
  
  private Map<String, Object> configProperties;
  
  private Map<String, Object> configPropertiesView;
  
  private MirrorSelector mirrorSelector;
  
  private ProxySelector proxySelector;
  
  private AuthenticationSelector authenticationSelector;
  
  private ArtifactTypeRegistry artifactTypeRegistry;
  
  private DependencyTraverser dependencyTraverser;
  
  private DependencyManager dependencyManager;
  
  private DependencySelector dependencySelector;
  
  private VersionFilter versionFilter;
  
  private DependencyGraphTransformer dependencyGraphTransformer;
  
  private SessionData data;
  
  private RepositoryCache cache;
  
  public DefaultRepositorySystemSession() {
    this.systemProperties = new HashMap<>();
    this.systemPropertiesView = Collections.unmodifiableMap(this.systemProperties);
    this.userProperties = new HashMap<>();
    this.userPropertiesView = Collections.unmodifiableMap(this.userProperties);
    this.configProperties = new HashMap<>();
    this.configPropertiesView = Collections.unmodifiableMap(this.configProperties);
    this.mirrorSelector = NullMirrorSelector.INSTANCE;
    this.proxySelector = NullProxySelector.INSTANCE;
    this.authenticationSelector = NullAuthenticationSelector.INSTANCE;
    this.artifactTypeRegistry = NullArtifactTypeRegistry.INSTANCE;
    this.fileTransformerManager = NullFileTransformerManager.INSTANCE;
    this.data = new DefaultSessionData();
  }
  
  public DefaultRepositorySystemSession(RepositorySystemSession session) {
    Objects.requireNonNull(session, "repository system session cannot be null");
    setOffline(session.isOffline());
    setIgnoreArtifactDescriptorRepositories(session.isIgnoreArtifactDescriptorRepositories());
    setResolutionErrorPolicy(session.getResolutionErrorPolicy());
    setArtifactDescriptorPolicy(session.getArtifactDescriptorPolicy());
    setChecksumPolicy(session.getChecksumPolicy());
    setUpdatePolicy(session.getUpdatePolicy());
    setLocalRepositoryManager(session.getLocalRepositoryManager());
    setWorkspaceReader(session.getWorkspaceReader());
    setRepositoryListener(session.getRepositoryListener());
    setTransferListener(session.getTransferListener());
    setSystemProperties(session.getSystemProperties());
    setUserProperties(session.getUserProperties());
    setConfigProperties(session.getConfigProperties());
    setMirrorSelector(session.getMirrorSelector());
    setProxySelector(session.getProxySelector());
    setAuthenticationSelector(session.getAuthenticationSelector());
    setArtifactTypeRegistry(session.getArtifactTypeRegistry());
    setDependencyTraverser(session.getDependencyTraverser());
    setDependencyManager(session.getDependencyManager());
    setDependencySelector(session.getDependencySelector());
    setVersionFilter(session.getVersionFilter());
    setDependencyGraphTransformer(session.getDependencyGraphTransformer());
    setFileTransformerManager(session.getFileTransformerManager());
    setData(session.getData());
    setCache(session.getCache());
  }
  
  public boolean isOffline() {
    return this.offline;
  }
  
  public DefaultRepositorySystemSession setOffline(boolean offline) {
    failIfReadOnly();
    this.offline = offline;
    return this;
  }
  
  public boolean isIgnoreArtifactDescriptorRepositories() {
    return this.ignoreArtifactDescriptorRepositories;
  }
  
  public DefaultRepositorySystemSession setIgnoreArtifactDescriptorRepositories(boolean ignoreArtifactDescriptorRepositories) {
    failIfReadOnly();
    this.ignoreArtifactDescriptorRepositories = ignoreArtifactDescriptorRepositories;
    return this;
  }
  
  public ResolutionErrorPolicy getResolutionErrorPolicy() {
    return this.resolutionErrorPolicy;
  }
  
  public DefaultRepositorySystemSession setResolutionErrorPolicy(ResolutionErrorPolicy resolutionErrorPolicy) {
    failIfReadOnly();
    this.resolutionErrorPolicy = resolutionErrorPolicy;
    return this;
  }
  
  public ArtifactDescriptorPolicy getArtifactDescriptorPolicy() {
    return this.artifactDescriptorPolicy;
  }
  
  public DefaultRepositorySystemSession setArtifactDescriptorPolicy(ArtifactDescriptorPolicy artifactDescriptorPolicy) {
    failIfReadOnly();
    this.artifactDescriptorPolicy = artifactDescriptorPolicy;
    return this;
  }
  
  public String getChecksumPolicy() {
    return this.checksumPolicy;
  }
  
  public DefaultRepositorySystemSession setChecksumPolicy(String checksumPolicy) {
    failIfReadOnly();
    this.checksumPolicy = checksumPolicy;
    return this;
  }
  
  public String getUpdatePolicy() {
    return this.updatePolicy;
  }
  
  public DefaultRepositorySystemSession setUpdatePolicy(String updatePolicy) {
    failIfReadOnly();
    this.updatePolicy = updatePolicy;
    return this;
  }
  
  public LocalRepository getLocalRepository() {
    LocalRepositoryManager lrm = getLocalRepositoryManager();
    return (lrm != null) ? lrm.getRepository() : null;
  }
  
  public LocalRepositoryManager getLocalRepositoryManager() {
    return this.localRepositoryManager;
  }
  
  public DefaultRepositorySystemSession setLocalRepositoryManager(LocalRepositoryManager localRepositoryManager) {
    failIfReadOnly();
    this.localRepositoryManager = localRepositoryManager;
    return this;
  }
  
  public FileTransformerManager getFileTransformerManager() {
    return this.fileTransformerManager;
  }
  
  public DefaultRepositorySystemSession setFileTransformerManager(FileTransformerManager fileTransformerManager) {
    failIfReadOnly();
    this.fileTransformerManager = fileTransformerManager;
    if (this.fileTransformerManager == null)
      this.fileTransformerManager = NullFileTransformerManager.INSTANCE; 
    return this;
  }
  
  public WorkspaceReader getWorkspaceReader() {
    return this.workspaceReader;
  }
  
  public DefaultRepositorySystemSession setWorkspaceReader(WorkspaceReader workspaceReader) {
    failIfReadOnly();
    this.workspaceReader = workspaceReader;
    return this;
  }
  
  public RepositoryListener getRepositoryListener() {
    return this.repositoryListener;
  }
  
  public DefaultRepositorySystemSession setRepositoryListener(RepositoryListener repositoryListener) {
    failIfReadOnly();
    this.repositoryListener = repositoryListener;
    return this;
  }
  
  public TransferListener getTransferListener() {
    return this.transferListener;
  }
  
  public DefaultRepositorySystemSession setTransferListener(TransferListener transferListener) {
    failIfReadOnly();
    this.transferListener = transferListener;
    return this;
  }
  
  private <T> Map<String, T> copySafe(Map<?, ?> table, Class<T> valueType) {
    Map<String, T> map;
    if (table == null || table.isEmpty()) {
      map = new HashMap<>();
    } else {
      map = new HashMap<>((int)(table.size() / 0.75F) + 1);
      for (Map.Entry<?, ?> entry : table.entrySet()) {
        Object key = entry.getKey();
        if (key instanceof String) {
          Object value = entry.getValue();
          if (valueType.isInstance(value))
            map.put(key.toString(), valueType.cast(value)); 
        } 
      } 
    } 
    return map;
  }
  
  public Map<String, String> getSystemProperties() {
    return this.systemPropertiesView;
  }
  
  public DefaultRepositorySystemSession setSystemProperties(Map<?, ?> systemProperties) {
    failIfReadOnly();
    this.systemProperties = copySafe(systemProperties, String.class);
    this.systemPropertiesView = Collections.unmodifiableMap(this.systemProperties);
    return this;
  }
  
  public DefaultRepositorySystemSession setSystemProperty(String key, String value) {
    failIfReadOnly();
    if (value != null) {
      this.systemProperties.put(key, value);
    } else {
      this.systemProperties.remove(key);
    } 
    return this;
  }
  
  public Map<String, String> getUserProperties() {
    return this.userPropertiesView;
  }
  
  public DefaultRepositorySystemSession setUserProperties(Map<?, ?> userProperties) {
    failIfReadOnly();
    this.userProperties = copySafe(userProperties, String.class);
    this.userPropertiesView = Collections.unmodifiableMap(this.userProperties);
    return this;
  }
  
  public DefaultRepositorySystemSession setUserProperty(String key, String value) {
    failIfReadOnly();
    if (value != null) {
      this.userProperties.put(key, value);
    } else {
      this.userProperties.remove(key);
    } 
    return this;
  }
  
  public Map<String, Object> getConfigProperties() {
    return this.configPropertiesView;
  }
  
  public DefaultRepositorySystemSession setConfigProperties(Map<?, ?> configProperties) {
    failIfReadOnly();
    this.configProperties = copySafe(configProperties, Object.class);
    this.configPropertiesView = Collections.unmodifiableMap(this.configProperties);
    return this;
  }
  
  public DefaultRepositorySystemSession setConfigProperty(String key, Object value) {
    failIfReadOnly();
    if (value != null) {
      this.configProperties.put(key, value);
    } else {
      this.configProperties.remove(key);
    } 
    return this;
  }
  
  public MirrorSelector getMirrorSelector() {
    return this.mirrorSelector;
  }
  
  public DefaultRepositorySystemSession setMirrorSelector(MirrorSelector mirrorSelector) {
    failIfReadOnly();
    this.mirrorSelector = mirrorSelector;
    if (this.mirrorSelector == null)
      this.mirrorSelector = NullMirrorSelector.INSTANCE; 
    return this;
  }
  
  public ProxySelector getProxySelector() {
    return this.proxySelector;
  }
  
  public DefaultRepositorySystemSession setProxySelector(ProxySelector proxySelector) {
    failIfReadOnly();
    this.proxySelector = proxySelector;
    if (this.proxySelector == null)
      this.proxySelector = NullProxySelector.INSTANCE; 
    return this;
  }
  
  public AuthenticationSelector getAuthenticationSelector() {
    return this.authenticationSelector;
  }
  
  public DefaultRepositorySystemSession setAuthenticationSelector(AuthenticationSelector authenticationSelector) {
    failIfReadOnly();
    this.authenticationSelector = authenticationSelector;
    if (this.authenticationSelector == null)
      this.authenticationSelector = NullAuthenticationSelector.INSTANCE; 
    return this;
  }
  
  public ArtifactTypeRegistry getArtifactTypeRegistry() {
    return this.artifactTypeRegistry;
  }
  
  public DefaultRepositorySystemSession setArtifactTypeRegistry(ArtifactTypeRegistry artifactTypeRegistry) {
    failIfReadOnly();
    this.artifactTypeRegistry = artifactTypeRegistry;
    if (this.artifactTypeRegistry == null)
      this.artifactTypeRegistry = NullArtifactTypeRegistry.INSTANCE; 
    return this;
  }
  
  public DependencyTraverser getDependencyTraverser() {
    return this.dependencyTraverser;
  }
  
  public DefaultRepositorySystemSession setDependencyTraverser(DependencyTraverser dependencyTraverser) {
    failIfReadOnly();
    this.dependencyTraverser = dependencyTraverser;
    return this;
  }
  
  public DependencyManager getDependencyManager() {
    return this.dependencyManager;
  }
  
  public DefaultRepositorySystemSession setDependencyManager(DependencyManager dependencyManager) {
    failIfReadOnly();
    this.dependencyManager = dependencyManager;
    return this;
  }
  
  public DependencySelector getDependencySelector() {
    return this.dependencySelector;
  }
  
  public DefaultRepositorySystemSession setDependencySelector(DependencySelector dependencySelector) {
    failIfReadOnly();
    this.dependencySelector = dependencySelector;
    return this;
  }
  
  public VersionFilter getVersionFilter() {
    return this.versionFilter;
  }
  
  public DefaultRepositorySystemSession setVersionFilter(VersionFilter versionFilter) {
    failIfReadOnly();
    this.versionFilter = versionFilter;
    return this;
  }
  
  public DependencyGraphTransformer getDependencyGraphTransformer() {
    return this.dependencyGraphTransformer;
  }
  
  public DefaultRepositorySystemSession setDependencyGraphTransformer(DependencyGraphTransformer dependencyGraphTransformer) {
    failIfReadOnly();
    this.dependencyGraphTransformer = dependencyGraphTransformer;
    return this;
  }
  
  public SessionData getData() {
    return this.data;
  }
  
  public DefaultRepositorySystemSession setData(SessionData data) {
    failIfReadOnly();
    this.data = data;
    if (this.data == null)
      this.data = new DefaultSessionData(); 
    return this;
  }
  
  public RepositoryCache getCache() {
    return this.cache;
  }
  
  public DefaultRepositorySystemSession setCache(RepositoryCache cache) {
    failIfReadOnly();
    this.cache = cache;
    return this;
  }
  
  public void setReadOnly() {
    this.readOnly = true;
  }
  
  private void failIfReadOnly() {
    if (this.readOnly)
      throw new IllegalStateException("repository system session is read-only"); 
  }
  
  static class NullProxySelector implements ProxySelector {
    public static final ProxySelector INSTANCE = new NullProxySelector();
    
    public Proxy getProxy(RemoteRepository repository) {
      return repository.getProxy();
    }
  }
  
  static class NullMirrorSelector implements MirrorSelector {
    public static final MirrorSelector INSTANCE = new NullMirrorSelector();
    
    public RemoteRepository getMirror(RemoteRepository repository) {
      return null;
    }
  }
  
  static class NullAuthenticationSelector implements AuthenticationSelector {
    public static final AuthenticationSelector INSTANCE = new NullAuthenticationSelector();
    
    public Authentication getAuthentication(RemoteRepository repository) {
      return repository.getAuthentication();
    }
  }
  
  static final class NullArtifactTypeRegistry implements ArtifactTypeRegistry {
    public static final ArtifactTypeRegistry INSTANCE = new NullArtifactTypeRegistry();
    
    public ArtifactType get(String typeId) {
      return null;
    }
  }
  
  static final class NullFileTransformerManager implements FileTransformerManager {
    public static final FileTransformerManager INSTANCE = new NullFileTransformerManager();
    
    public Collection<FileTransformer> getTransformersForArtifact(Artifact artifact) {
      return Collections.emptyList();
    }
  }
}
