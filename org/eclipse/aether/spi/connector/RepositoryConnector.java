package org.eclipse.aether.spi.connector;

import java.io.Closeable;
import java.util.Collection;

public interface RepositoryConnector extends Closeable {
  void get(Collection<? extends ArtifactDownload> paramCollection, Collection<? extends MetadataDownload> paramCollection1);
  
  void put(Collection<? extends ArtifactUpload> paramCollection, Collection<? extends MetadataUpload> paramCollection1);
  
  void close();
}
