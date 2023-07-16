package org.eclipse.aether.internal.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;
import java.util.Set;
import javax.inject.Inject;
import javax.inject.Named;
import org.eclipse.aether.RepositoryEvent;
import org.eclipse.aether.RepositoryListener;
import org.eclipse.aether.impl.RepositoryEventDispatcher;
import org.eclipse.aether.spi.locator.Service;
import org.eclipse.aether.spi.locator.ServiceLocator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Named
public class DefaultRepositoryEventDispatcher implements RepositoryEventDispatcher, Service {
  private static final Logger LOGGER = LoggerFactory.getLogger(DefaultRepositoryEventDispatcher.class);
  
  private Collection<RepositoryListener> listeners = new ArrayList<>();
  
  public DefaultRepositoryEventDispatcher() {}
  
  @Inject
  DefaultRepositoryEventDispatcher(Set<RepositoryListener> listeners) {
    setRepositoryListeners(listeners);
  }
  
  public DefaultRepositoryEventDispatcher addRepositoryListener(RepositoryListener listener) {
    this.listeners.add(Objects.requireNonNull(listener, "repository listener cannot be null"));
    return this;
  }
  
  public DefaultRepositoryEventDispatcher setRepositoryListeners(Collection<RepositoryListener> listeners) {
    if (listeners == null) {
      this.listeners = new ArrayList<>();
    } else {
      this.listeners = listeners;
    } 
    return this;
  }
  
  public void initService(ServiceLocator locator) {
    setRepositoryListeners(locator.getServices(RepositoryListener.class));
  }
  
  public void dispatch(RepositoryEvent event) {
    if (!this.listeners.isEmpty())
      for (RepositoryListener repositoryListener : this.listeners)
        dispatch(event, repositoryListener);  
    RepositoryListener listener = event.getSession().getRepositoryListener();
    if (listener != null)
      dispatch(event, listener); 
  }
  
  private void dispatch(RepositoryEvent event, RepositoryListener listener) {
    try {
      switch (event.getType()) {
        case ARTIFACT_DEPLOYED:
          listener.artifactDeployed(event);
          return;
        case ARTIFACT_DEPLOYING:
          listener.artifactDeploying(event);
          return;
        case ARTIFACT_DESCRIPTOR_INVALID:
          listener.artifactDescriptorInvalid(event);
          return;
        case ARTIFACT_DESCRIPTOR_MISSING:
          listener.artifactDescriptorMissing(event);
          return;
        case ARTIFACT_DOWNLOADED:
          listener.artifactDownloaded(event);
          return;
        case ARTIFACT_DOWNLOADING:
          listener.artifactDownloading(event);
          return;
        case ARTIFACT_INSTALLED:
          listener.artifactInstalled(event);
          return;
        case ARTIFACT_INSTALLING:
          listener.artifactInstalling(event);
          return;
        case ARTIFACT_RESOLVED:
          listener.artifactResolved(event);
          return;
        case ARTIFACT_RESOLVING:
          listener.artifactResolving(event);
          return;
        case METADATA_DEPLOYED:
          listener.metadataDeployed(event);
          return;
        case METADATA_DEPLOYING:
          listener.metadataDeploying(event);
          return;
        case METADATA_DOWNLOADED:
          listener.metadataDownloaded(event);
          return;
        case METADATA_DOWNLOADING:
          listener.metadataDownloading(event);
          return;
        case METADATA_INSTALLED:
          listener.metadataInstalled(event);
          return;
        case METADATA_INSTALLING:
          listener.metadataInstalling(event);
          return;
        case METADATA_INVALID:
          listener.metadataInvalid(event);
          return;
        case METADATA_RESOLVED:
          listener.metadataResolved(event);
          return;
        case METADATA_RESOLVING:
          listener.metadataResolving(event);
          return;
      } 
      throw new IllegalStateException("unknown repository event type " + event.getType());
    } catch (Exception|LinkageError e) {
      logError(e, listener);
    } 
  }
  
  private void logError(Throwable e, Object listener) {
    LOGGER.warn("Failed to dispatch repository event to {}", listener.getClass().getCanonicalName(), e);
  }
}
