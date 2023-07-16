package org.eclipse.aether.internal.impl;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Provider;
import javax.inject.Singleton;
import org.eclipse.aether.spi.log.LoggerFactory;

@Named
@Singleton
public class LoggerFactoryProvider implements Provider<LoggerFactory> {
  @Inject
  @Named("slf4j")
  private Provider<LoggerFactory> slf4j;
  
  public LoggerFactory get() {
    return (LoggerFactory)this.slf4j.get();
  }
}
