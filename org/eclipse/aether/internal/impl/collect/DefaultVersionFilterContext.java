package org.eclipse.aether.internal.impl.collect;

import java.util.ArrayList;
import java.util.Collections;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import org.eclipse.aether.RepositorySystemSession;
import org.eclipse.aether.collection.VersionFilter;
import org.eclipse.aether.graph.Dependency;
import org.eclipse.aether.repository.ArtifactRepository;
import org.eclipse.aether.repository.RemoteRepository;
import org.eclipse.aether.resolution.VersionRangeResult;
import org.eclipse.aether.version.Version;
import org.eclipse.aether.version.VersionConstraint;

final class DefaultVersionFilterContext implements VersionFilter.VersionFilterContext {
  private final RepositorySystemSession session;
  
  private Dependency dependency;
  
  VersionRangeResult result;
  
  int count;
  
  byte[] deleted = new byte[64];
  
  DefaultVersionFilterContext(RepositorySystemSession session) {
    this.session = session;
  }
  
  public void set(Dependency dependency, VersionRangeResult result) {
    this.dependency = dependency;
    this.result = result;
    this.count = result.getVersions().size();
    if (this.deleted.length < this.count) {
      this.deleted = new byte[this.count];
    } else {
      for (int i = this.count - 1; i >= 0; i--)
        this.deleted[i] = 0; 
    } 
  }
  
  public List<Version> get() {
    if (this.count == this.result.getVersions().size())
      return this.result.getVersions(); 
    if (this.count <= 1) {
      if (this.count <= 0)
        return Collections.emptyList(); 
      return Collections.singletonList(iterator().next());
    } 
    List<Version> versions = new ArrayList<>(this.count);
    for (Version version : this)
      versions.add(version); 
    return versions;
  }
  
  public RepositorySystemSession getSession() {
    return this.session;
  }
  
  public Dependency getDependency() {
    return this.dependency;
  }
  
  public VersionConstraint getVersionConstraint() {
    return this.result.getVersionConstraint();
  }
  
  public int getCount() {
    return this.count;
  }
  
  public ArtifactRepository getRepository(Version version) {
    return this.result.getRepository(version);
  }
  
  public List<RemoteRepository> getRepositories() {
    return Collections.unmodifiableList(this.result.getRequest().getRepositories());
  }
  
  public Iterator<Version> iterator() {
    return (this.count > 0) ? new VersionIterator() : Collections.<Version>emptySet().iterator();
  }
  
  public String toString() {
    return this.dependency + " " + this.result.getVersions();
  }
  
  private class VersionIterator implements Iterator<Version> {
    private final List<Version> versions;
    
    private final int size;
    
    private int count;
    
    private int index;
    
    private int next;
    
    VersionIterator() {
      this.count = DefaultVersionFilterContext.this.count;
      this.index = -1;
      this.next = 0;
      this.versions = DefaultVersionFilterContext.this.result.getVersions();
      this.size = this.versions.size();
      advance();
    }
    
    private void advance() {
      for (this.next = this.index + 1; this.next < this.size && DefaultVersionFilterContext.this.deleted[this.next] != 0; this.next++);
    }
    
    public boolean hasNext() {
      return (this.next < this.size);
    }
    
    public Version next() {
      if (this.count != DefaultVersionFilterContext.this.count)
        throw new ConcurrentModificationException(); 
      if (this.next >= this.size)
        throw new NoSuchElementException(); 
      this.index = this.next;
      advance();
      return this.versions.get(this.index);
    }
    
    public void remove() {
      if (this.count != DefaultVersionFilterContext.this.count)
        throw new ConcurrentModificationException(); 
      if (this.index < 0 || DefaultVersionFilterContext.this.deleted[this.index] == 1)
        throw new IllegalStateException(); 
      DefaultVersionFilterContext.this.deleted[this.index] = 1;
      this.count = --DefaultVersionFilterContext.this.count;
    }
    
    public String toString() {
      return (this.index < 0) ? "null" : String.valueOf(this.versions.get(this.index));
    }
  }
}
