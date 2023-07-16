package org.eclipse.aether.internal.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import javax.inject.Inject;
import javax.inject.Named;
import org.eclipse.aether.RepositorySystemSession;
import org.eclipse.aether.repository.RemoteRepository;
import org.eclipse.aether.spi.connector.layout.RepositoryLayout;
import org.eclipse.aether.spi.connector.layout.RepositoryLayoutFactory;
import org.eclipse.aether.spi.connector.layout.RepositoryLayoutProvider;
import org.eclipse.aether.spi.locator.Service;
import org.eclipse.aether.spi.locator.ServiceLocator;
import org.eclipse.aether.transfer.NoRepositoryLayoutException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Named
public final class DefaultRepositoryLayoutProvider implements RepositoryLayoutProvider, Service {
  private static final Logger LOGGER = LoggerFactory.getLogger(DefaultRepositoryLayoutProvider.class);
  
  private Collection<RepositoryLayoutFactory> factories = new ArrayList<>();
  
  public DefaultRepositoryLayoutProvider() {}
  
  @Inject
  DefaultRepositoryLayoutProvider(Set<RepositoryLayoutFactory> layoutFactories) {
    setRepositoryLayoutFactories(layoutFactories);
  }
  
  public void initService(ServiceLocator locator) {
    setRepositoryLayoutFactories(locator.getServices(RepositoryLayoutFactory.class));
  }
  
  public DefaultRepositoryLayoutProvider addRepositoryLayoutFactory(RepositoryLayoutFactory factory) {
    this.factories.add(Objects.requireNonNull(factory, "layout factory cannot be null"));
    return this;
  }
  
  public DefaultRepositoryLayoutProvider setRepositoryLayoutFactories(Collection<RepositoryLayoutFactory> factories) {
    if (factories == null) {
      this.factories = new ArrayList<>();
    } else {
      this.factories = factories;
    } 
    return this;
  }
  
  public RepositoryLayout newRepositoryLayout(RepositorySystemSession session, RemoteRepository repository) throws NoRepositoryLayoutException {
    Objects.requireNonNull(repository, "remote repository cannot be null");
    PrioritizedComponents<RepositoryLayoutFactory> factories = new PrioritizedComponents<>(session);
    for (RepositoryLayoutFactory factory : this.factories)
      factories.add(factory, factory.getPriority()); 
    List<NoRepositoryLayoutException> errors = new ArrayList<>();
    for (PrioritizedComponent<RepositoryLayoutFactory> factory : factories.getEnabled()) {
      try {
        return ((RepositoryLayoutFactory)factory.getComponent()).newInstance(session, repository);
      } catch (NoRepositoryLayoutException e) {
        errors.add(e);
      } 
    } 
    if (LOGGER.isDebugEnabled() && errors.size() > 1)
      for (Exception e : errors)
        LOGGER.debug("Could not obtain layout factory for {}", repository, e);  
    StringBuilder buffer = new StringBuilder(256);
    if (factories.isEmpty()) {
      buffer.append("No layout factories registered");
    } else {
      buffer.append("Cannot access ").append(repository.getUrl());
      buffer.append(" with type ").append(repository.getContentType());
      buffer.append(" using the available layout factories: ");
      factories.list(buffer);
    } 
    throw new NoRepositoryLayoutException(repository, buffer.toString(), (errors.size() == 1) ? (NoRepositoryLayoutException)errors.get(0) : null);
  }
}
