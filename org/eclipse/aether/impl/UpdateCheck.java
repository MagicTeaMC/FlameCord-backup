package org.eclipse.aether.impl;

import java.io.File;
import org.eclipse.aether.RepositoryException;
import org.eclipse.aether.repository.RemoteRepository;

public final class UpdateCheck<T, E extends RepositoryException> {
  private long localLastUpdated;
  
  private T item;
  
  private File file;
  
  private boolean fileValid = true;
  
  private String policy;
  
  private RemoteRepository repository;
  
  private RemoteRepository authoritativeRepository;
  
  private boolean required;
  
  private E exception;
  
  public long getLocalLastUpdated() {
    return this.localLastUpdated;
  }
  
  public UpdateCheck<T, E> setLocalLastUpdated(long localLastUpdated) {
    this.localLastUpdated = localLastUpdated;
    return this;
  }
  
  public T getItem() {
    return this.item;
  }
  
  public UpdateCheck<T, E> setItem(T item) {
    this.item = item;
    return this;
  }
  
  public File getFile() {
    return this.file;
  }
  
  public UpdateCheck<T, E> setFile(File file) {
    this.file = file;
    return this;
  }
  
  public boolean isFileValid() {
    return this.fileValid;
  }
  
  public UpdateCheck<T, E> setFileValid(boolean fileValid) {
    this.fileValid = fileValid;
    return this;
  }
  
  public String getPolicy() {
    return this.policy;
  }
  
  public UpdateCheck<T, E> setPolicy(String policy) {
    this.policy = policy;
    return this;
  }
  
  public RemoteRepository getRepository() {
    return this.repository;
  }
  
  public UpdateCheck<T, E> setRepository(RemoteRepository repository) {
    this.repository = repository;
    return this;
  }
  
  public RemoteRepository getAuthoritativeRepository() {
    return (this.authoritativeRepository != null) ? this.authoritativeRepository : this.repository;
  }
  
  public UpdateCheck<T, E> setAuthoritativeRepository(RemoteRepository authoritativeRepository) {
    this.authoritativeRepository = authoritativeRepository;
    return this;
  }
  
  public boolean isRequired() {
    return this.required;
  }
  
  public UpdateCheck<T, E> setRequired(boolean required) {
    this.required = required;
    return this;
  }
  
  public E getException() {
    return this.exception;
  }
  
  public UpdateCheck<T, E> setException(E exception) {
    this.exception = exception;
    return this;
  }
  
  public String toString() {
    return getPolicy() + ": " + getFile() + " < " + getRepository();
  }
}
