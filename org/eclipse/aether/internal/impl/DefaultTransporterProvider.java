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
import org.eclipse.aether.spi.connector.transport.Transporter;
import org.eclipse.aether.spi.connector.transport.TransporterFactory;
import org.eclipse.aether.spi.connector.transport.TransporterProvider;
import org.eclipse.aether.spi.locator.Service;
import org.eclipse.aether.spi.locator.ServiceLocator;
import org.eclipse.aether.transfer.NoTransporterException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Named
public final class DefaultTransporterProvider implements TransporterProvider, Service {
  private static final Logger LOGGER = LoggerFactory.getLogger(DefaultTransporterProvider.class);
  
  private Collection<TransporterFactory> factories = new ArrayList<>();
  
  public DefaultTransporterProvider() {}
  
  @Inject
  DefaultTransporterProvider(Set<TransporterFactory> transporterFactories) {
    setTransporterFactories(transporterFactories);
  }
  
  public void initService(ServiceLocator locator) {
    setTransporterFactories(locator.getServices(TransporterFactory.class));
  }
  
  public DefaultTransporterProvider addTransporterFactory(TransporterFactory factory) {
    this.factories.add(Objects.requireNonNull(factory, "transporter factory cannot be null"));
    return this;
  }
  
  public DefaultTransporterProvider setTransporterFactories(Collection<TransporterFactory> factories) {
    if (factories == null) {
      this.factories = new ArrayList<>();
    } else {
      this.factories = factories;
    } 
    return this;
  }
  
  public Transporter newTransporter(RepositorySystemSession session, RemoteRepository repository) throws NoTransporterException {
    Objects.requireNonNull(repository, "remote repository cannot be null");
    PrioritizedComponents<TransporterFactory> factories = new PrioritizedComponents<>(session);
    for (TransporterFactory factory : this.factories)
      factories.add(factory, factory.getPriority()); 
    List<NoTransporterException> errors = new ArrayList<>();
    for (PrioritizedComponent<TransporterFactory> factory : factories.getEnabled()) {
      try {
        Transporter transporter = ((TransporterFactory)factory.getComponent()).newInstance(session, repository);
        if (LOGGER.isDebugEnabled()) {
          StringBuilder stringBuilder = new StringBuilder(256);
          stringBuilder.append("Using transporter ").append(transporter.getClass().getSimpleName());
          Utils.appendClassLoader(stringBuilder, transporter);
          stringBuilder.append(" with priority ").append(factory.getPriority());
          stringBuilder.append(" for ").append(repository.getUrl());
          LOGGER.debug(stringBuilder.toString());
        } 
        return transporter;
      } catch (NoTransporterException e) {
        errors.add(e);
      } 
    } 
    if (LOGGER.isDebugEnabled() && errors.size() > 1)
      for (Exception e : errors)
        LOGGER.debug("Could not obtain transporter factory for {}", repository, e);  
    StringBuilder buffer = new StringBuilder(256);
    if (factories.isEmpty()) {
      buffer.append("No transporter factories registered");
    } else {
      buffer.append("Cannot access ").append(repository.getUrl());
      buffer.append(" using the registered transporter factories: ");
      factories.list(buffer);
    } 
    throw new NoTransporterException(repository, buffer.toString(), (errors.size() == 1) ? (NoTransporterException)errors.get(0) : null);
  }
}
