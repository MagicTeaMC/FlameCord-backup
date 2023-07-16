package org.eclipse.aether.internal.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import javax.inject.Inject;
import javax.inject.Named;
import org.eclipse.aether.RepositorySystemSession;
import org.eclipse.aether.impl.LocalRepositoryProvider;
import org.eclipse.aether.repository.LocalRepository;
import org.eclipse.aether.repository.LocalRepositoryManager;
import org.eclipse.aether.repository.NoLocalRepositoryManagerException;
import org.eclipse.aether.spi.localrepo.LocalRepositoryManagerFactory;
import org.eclipse.aether.spi.locator.Service;
import org.eclipse.aether.spi.locator.ServiceLocator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Named
public class DefaultLocalRepositoryProvider implements LocalRepositoryProvider, Service {
  private static final Logger LOGGER = LoggerFactory.getLogger(DefaultLocalRepositoryProvider.class);
  
  private Collection<LocalRepositoryManagerFactory> managerFactories = new ArrayList<>();
  
  public DefaultLocalRepositoryProvider() {}
  
  @Inject
  DefaultLocalRepositoryProvider(Set<LocalRepositoryManagerFactory> factories) {
    setLocalRepositoryManagerFactories(factories);
  }
  
  public void initService(ServiceLocator locator) {
    setLocalRepositoryManagerFactories(locator.getServices(LocalRepositoryManagerFactory.class));
  }
  
  public DefaultLocalRepositoryProvider addLocalRepositoryManagerFactory(LocalRepositoryManagerFactory factory) {
    this.managerFactories.add(Objects.requireNonNull(factory, "local repository manager factory cannot be null"));
    return this;
  }
  
  public DefaultLocalRepositoryProvider setLocalRepositoryManagerFactories(Collection<LocalRepositoryManagerFactory> factories) {
    if (factories == null) {
      this.managerFactories = new ArrayList<>(2);
    } else {
      this.managerFactories = factories;
    } 
    return this;
  }
  
  public LocalRepositoryManager newLocalRepositoryManager(RepositorySystemSession session, LocalRepository repository) throws NoLocalRepositoryManagerException {
    PrioritizedComponents<LocalRepositoryManagerFactory> factories = new PrioritizedComponents<>(session);
    for (LocalRepositoryManagerFactory factory : this.managerFactories)
      factories.add(factory, factory.getPriority()); 
    List<NoLocalRepositoryManagerException> errors = new ArrayList<>();
    for (PrioritizedComponent<LocalRepositoryManagerFactory> factory : factories.getEnabled()) {
      try {
        LocalRepositoryManager manager = ((LocalRepositoryManagerFactory)factory.getComponent()).newInstance(session, repository);
        if (LOGGER.isDebugEnabled()) {
          StringBuilder stringBuilder = new StringBuilder(256);
          stringBuilder.append("Using manager ").append(manager.getClass().getSimpleName());
          Utils.appendClassLoader(stringBuilder, manager);
          stringBuilder.append(" with priority ").append(factory.getPriority());
          stringBuilder.append(" for ").append(repository.getBasedir());
          LOGGER.debug(stringBuilder.toString());
        } 
        return manager;
      } catch (NoLocalRepositoryManagerException e) {
        errors.add(e);
      } 
    } 
    if (LOGGER.isDebugEnabled() && errors.size() > 1)
      for (Exception e : errors)
        LOGGER.debug("Could not obtain local repository manager for {}", repository, e);  
    StringBuilder buffer = new StringBuilder(256);
    if (factories.isEmpty()) {
      buffer.append("No local repository managers registered");
    } else {
      buffer.append("Cannot access ").append(repository.getBasedir());
      buffer.append(" with type ").append(repository.getContentType());
      buffer.append(" using the available factories ");
      factories.list(buffer);
    } 
    throw new NoLocalRepositoryManagerException(repository, buffer.toString(), (errors.size() == 1) ? (NoLocalRepositoryManagerException)errors.get(0) : null);
  }
}
