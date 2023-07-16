package org.eclipse.aether.util.repository;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import org.eclipse.aether.artifact.Artifact;
import org.eclipse.aether.repository.WorkspaceReader;
import org.eclipse.aether.repository.WorkspaceRepository;

public final class ChainedWorkspaceReader implements WorkspaceReader {
  private List<WorkspaceReader> readers = new ArrayList<>();
  
  private WorkspaceRepository repository;
  
  public ChainedWorkspaceReader(WorkspaceReader... readers) {
    if (readers != null)
      Collections.addAll(this.readers, readers); 
    StringBuilder buffer = new StringBuilder();
    for (WorkspaceReader reader : this.readers) {
      if (buffer.length() > 0)
        buffer.append('+'); 
      buffer.append(reader.getRepository().getContentType());
    } 
    this.repository = new WorkspaceRepository(buffer.toString(), new Key(this.readers));
  }
  
  public static WorkspaceReader newInstance(WorkspaceReader reader1, WorkspaceReader reader2) {
    if (reader1 == null)
      return reader2; 
    if (reader2 == null)
      return reader1; 
    return new ChainedWorkspaceReader(new WorkspaceReader[] { reader1, reader2 });
  }
  
  public File findArtifact(Artifact artifact) {
    File file = null;
    for (WorkspaceReader reader : this.readers) {
      file = reader.findArtifact(artifact);
      if (file != null)
        break; 
    } 
    return file;
  }
  
  public List<String> findVersions(Artifact artifact) {
    Collection<String> versions = new LinkedHashSet<>();
    for (WorkspaceReader reader : this.readers)
      versions.addAll(reader.findVersions(artifact)); 
    return Collections.unmodifiableList(new ArrayList<>(versions));
  }
  
  public WorkspaceRepository getRepository() {
    Key key = new Key(this.readers);
    if (!key.equals(this.repository.getKey()))
      this.repository = new WorkspaceRepository(this.repository.getContentType(), key); 
    return this.repository;
  }
  
  private static class Key {
    private final List<Object> keys = new ArrayList();
    
    Key(List<WorkspaceReader> readers) {
      for (WorkspaceReader reader : readers)
        this.keys.add(reader.getRepository().getKey()); 
    }
    
    public boolean equals(Object obj) {
      if (this == obj)
        return true; 
      if (obj == null || !getClass().equals(obj.getClass()))
        return false; 
      return this.keys.equals(((Key)obj).keys);
    }
    
    public int hashCode() {
      return this.keys.hashCode();
    }
  }
}
