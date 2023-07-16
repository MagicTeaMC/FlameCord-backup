package org.eclipse.aether.util.listener;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import org.eclipse.aether.AbstractRepositoryListener;
import org.eclipse.aether.RepositoryEvent;
import org.eclipse.aether.RepositoryListener;

public final class ChainedRepositoryListener extends AbstractRepositoryListener {
  private final List<RepositoryListener> listeners = new CopyOnWriteArrayList<>();
  
  public static RepositoryListener newInstance(RepositoryListener listener1, RepositoryListener listener2) {
    if (listener1 == null)
      return listener2; 
    if (listener2 == null)
      return listener1; 
    return (RepositoryListener)new ChainedRepositoryListener(new RepositoryListener[] { listener1, listener2 });
  }
  
  public ChainedRepositoryListener(RepositoryListener... listeners) {
    if (listeners != null)
      add(Arrays.asList(listeners)); 
  }
  
  public ChainedRepositoryListener(Collection<? extends RepositoryListener> listeners) {
    add(listeners);
  }
  
  public void add(Collection<? extends RepositoryListener> listeners) {
    if (listeners != null)
      for (RepositoryListener listener : listeners)
        add(listener);  
  }
  
  public void add(RepositoryListener listener) {
    if (listener != null)
      this.listeners.add(listener); 
  }
  
  public void remove(RepositoryListener listener) {
    if (listener != null)
      this.listeners.remove(listener); 
  }
  
  protected void handleError(RepositoryEvent event, RepositoryListener listener, RuntimeException error) {}
  
  public void artifactDeployed(RepositoryEvent event) {
    for (RepositoryListener listener : this.listeners) {
      try {
        listener.artifactDeployed(event);
      } catch (RuntimeException e) {
        handleError(event, listener, e);
      } 
    } 
  }
  
  public void artifactDeploying(RepositoryEvent event) {
    for (RepositoryListener listener : this.listeners) {
      try {
        listener.artifactDeploying(event);
      } catch (RuntimeException e) {
        handleError(event, listener, e);
      } 
    } 
  }
  
  public void artifactDescriptorInvalid(RepositoryEvent event) {
    for (RepositoryListener listener : this.listeners) {
      try {
        listener.artifactDescriptorInvalid(event);
      } catch (RuntimeException e) {
        handleError(event, listener, e);
      } 
    } 
  }
  
  public void artifactDescriptorMissing(RepositoryEvent event) {
    for (RepositoryListener listener : this.listeners) {
      try {
        listener.artifactDescriptorMissing(event);
      } catch (RuntimeException e) {
        handleError(event, listener, e);
      } 
    } 
  }
  
  public void artifactDownloaded(RepositoryEvent event) {
    for (RepositoryListener listener : this.listeners) {
      try {
        listener.artifactDownloaded(event);
      } catch (RuntimeException e) {
        handleError(event, listener, e);
      } 
    } 
  }
  
  public void artifactDownloading(RepositoryEvent event) {
    for (RepositoryListener listener : this.listeners) {
      try {
        listener.artifactDownloading(event);
      } catch (RuntimeException e) {
        handleError(event, listener, e);
      } 
    } 
  }
  
  public void artifactInstalled(RepositoryEvent event) {
    for (RepositoryListener listener : this.listeners) {
      try {
        listener.artifactInstalled(event);
      } catch (RuntimeException e) {
        handleError(event, listener, e);
      } 
    } 
  }
  
  public void artifactInstalling(RepositoryEvent event) {
    for (RepositoryListener listener : this.listeners) {
      try {
        listener.artifactInstalling(event);
      } catch (RuntimeException e) {
        handleError(event, listener, e);
      } 
    } 
  }
  
  public void artifactResolved(RepositoryEvent event) {
    for (RepositoryListener listener : this.listeners) {
      try {
        listener.artifactResolved(event);
      } catch (RuntimeException e) {
        handleError(event, listener, e);
      } 
    } 
  }
  
  public void artifactResolving(RepositoryEvent event) {
    for (RepositoryListener listener : this.listeners) {
      try {
        listener.artifactResolving(event);
      } catch (RuntimeException e) {
        handleError(event, listener, e);
      } 
    } 
  }
  
  public void metadataDeployed(RepositoryEvent event) {
    for (RepositoryListener listener : this.listeners) {
      try {
        listener.metadataDeployed(event);
      } catch (RuntimeException e) {
        handleError(event, listener, e);
      } 
    } 
  }
  
  public void metadataDeploying(RepositoryEvent event) {
    for (RepositoryListener listener : this.listeners) {
      try {
        listener.metadataDeploying(event);
      } catch (RuntimeException e) {
        handleError(event, listener, e);
      } 
    } 
  }
  
  public void metadataDownloaded(RepositoryEvent event) {
    for (RepositoryListener listener : this.listeners) {
      try {
        listener.metadataDownloaded(event);
      } catch (RuntimeException e) {
        handleError(event, listener, e);
      } 
    } 
  }
  
  public void metadataDownloading(RepositoryEvent event) {
    for (RepositoryListener listener : this.listeners) {
      try {
        listener.metadataDownloading(event);
      } catch (RuntimeException e) {
        handleError(event, listener, e);
      } 
    } 
  }
  
  public void metadataInstalled(RepositoryEvent event) {
    for (RepositoryListener listener : this.listeners) {
      try {
        listener.metadataInstalled(event);
      } catch (RuntimeException e) {
        handleError(event, listener, e);
      } 
    } 
  }
  
  public void metadataInstalling(RepositoryEvent event) {
    for (RepositoryListener listener : this.listeners) {
      try {
        listener.metadataInstalling(event);
      } catch (RuntimeException e) {
        handleError(event, listener, e);
      } 
    } 
  }
  
  public void metadataInvalid(RepositoryEvent event) {
    for (RepositoryListener listener : this.listeners) {
      try {
        listener.metadataInvalid(event);
      } catch (RuntimeException e) {
        handleError(event, listener, e);
      } 
    } 
  }
  
  public void metadataResolved(RepositoryEvent event) {
    for (RepositoryListener listener : this.listeners) {
      try {
        listener.metadataResolved(event);
      } catch (RuntimeException e) {
        handleError(event, listener, e);
      } 
    } 
  }
  
  public void metadataResolving(RepositoryEvent event) {
    for (RepositoryListener listener : this.listeners) {
      try {
        listener.metadataResolving(event);
      } catch (RuntimeException e) {
        handleError(event, listener, e);
      } 
    } 
  }
}
