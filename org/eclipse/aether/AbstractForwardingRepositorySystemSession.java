package org.eclipse.aether;

import java.util.Map;
import org.eclipse.aether.artifact.ArtifactTypeRegistry;
import org.eclipse.aether.collection.DependencyGraphTransformer;
import org.eclipse.aether.collection.DependencyManager;
import org.eclipse.aether.collection.DependencySelector;
import org.eclipse.aether.collection.DependencyTraverser;
import org.eclipse.aether.collection.VersionFilter;
import org.eclipse.aether.repository.AuthenticationSelector;
import org.eclipse.aether.repository.LocalRepository;
import org.eclipse.aether.repository.LocalRepositoryManager;
import org.eclipse.aether.repository.MirrorSelector;
import org.eclipse.aether.repository.ProxySelector;
import org.eclipse.aether.repository.WorkspaceReader;
import org.eclipse.aether.resolution.ArtifactDescriptorPolicy;
import org.eclipse.aether.resolution.ResolutionErrorPolicy;
import org.eclipse.aether.transfer.TransferListener;
import org.eclipse.aether.transform.FileTransformerManager;

public abstract class AbstractForwardingRepositorySystemSession implements RepositorySystemSession {
  protected abstract RepositorySystemSession getSession();
  
  public boolean isOffline() {
    return getSession().isOffline();
  }
  
  public boolean isIgnoreArtifactDescriptorRepositories() {
    return getSession().isIgnoreArtifactDescriptorRepositories();
  }
  
  public ResolutionErrorPolicy getResolutionErrorPolicy() {
    return getSession().getResolutionErrorPolicy();
  }
  
  public ArtifactDescriptorPolicy getArtifactDescriptorPolicy() {
    return getSession().getArtifactDescriptorPolicy();
  }
  
  public String getChecksumPolicy() {
    return getSession().getChecksumPolicy();
  }
  
  public String getUpdatePolicy() {
    return getSession().getUpdatePolicy();
  }
  
  public LocalRepository getLocalRepository() {
    return getSession().getLocalRepository();
  }
  
  public LocalRepositoryManager getLocalRepositoryManager() {
    return getSession().getLocalRepositoryManager();
  }
  
  public WorkspaceReader getWorkspaceReader() {
    return getSession().getWorkspaceReader();
  }
  
  public RepositoryListener getRepositoryListener() {
    return getSession().getRepositoryListener();
  }
  
  public TransferListener getTransferListener() {
    return getSession().getTransferListener();
  }
  
  public Map<String, String> getSystemProperties() {
    return getSession().getSystemProperties();
  }
  
  public Map<String, String> getUserProperties() {
    return getSession().getUserProperties();
  }
  
  public Map<String, Object> getConfigProperties() {
    return getSession().getConfigProperties();
  }
  
  public MirrorSelector getMirrorSelector() {
    return getSession().getMirrorSelector();
  }
  
  public ProxySelector getProxySelector() {
    return getSession().getProxySelector();
  }
  
  public AuthenticationSelector getAuthenticationSelector() {
    return getSession().getAuthenticationSelector();
  }
  
  public ArtifactTypeRegistry getArtifactTypeRegistry() {
    return getSession().getArtifactTypeRegistry();
  }
  
  public DependencyTraverser getDependencyTraverser() {
    return getSession().getDependencyTraverser();
  }
  
  public DependencyManager getDependencyManager() {
    return getSession().getDependencyManager();
  }
  
  public DependencySelector getDependencySelector() {
    return getSession().getDependencySelector();
  }
  
  public VersionFilter getVersionFilter() {
    return getSession().getVersionFilter();
  }
  
  public DependencyGraphTransformer getDependencyGraphTransformer() {
    return getSession().getDependencyGraphTransformer();
  }
  
  public SessionData getData() {
    return getSession().getData();
  }
  
  public RepositoryCache getCache() {
    return getSession().getCache();
  }
  
  public FileTransformerManager getFileTransformerManager() {
    return getSession().getFileTransformerManager();
  }
}
