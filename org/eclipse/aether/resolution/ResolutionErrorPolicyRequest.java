package org.eclipse.aether.resolution;

import org.eclipse.aether.repository.RemoteRepository;

public final class ResolutionErrorPolicyRequest<T> {
  private T item;
  
  private RemoteRepository repository;
  
  public ResolutionErrorPolicyRequest() {}
  
  public ResolutionErrorPolicyRequest(T item, RemoteRepository repository) {
    setItem(item);
    setRepository(repository);
  }
  
  public T getItem() {
    return this.item;
  }
  
  public ResolutionErrorPolicyRequest<T> setItem(T item) {
    this.item = item;
    return this;
  }
  
  public RemoteRepository getRepository() {
    return this.repository;
  }
  
  public ResolutionErrorPolicyRequest<T> setRepository(RemoteRepository repository) {
    this.repository = repository;
    return this;
  }
  
  public String toString() {
    return (new StringBuilder()).append(getItem()).append(" < ").append(getRepository()).toString();
  }
}
