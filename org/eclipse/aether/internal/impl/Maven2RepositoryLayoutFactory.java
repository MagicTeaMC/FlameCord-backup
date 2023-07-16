package org.eclipse.aether.internal.impl;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import javax.inject.Named;
import org.eclipse.aether.RepositorySystemSession;
import org.eclipse.aether.artifact.Artifact;
import org.eclipse.aether.metadata.Metadata;
import org.eclipse.aether.repository.RemoteRepository;
import org.eclipse.aether.spi.connector.layout.RepositoryLayout;
import org.eclipse.aether.spi.connector.layout.RepositoryLayoutFactory;
import org.eclipse.aether.transfer.NoRepositoryLayoutException;
import org.eclipse.aether.util.ConfigUtils;

@Named("maven2")
public final class Maven2RepositoryLayoutFactory implements RepositoryLayoutFactory {
  static final String CONFIG_PROP_SIGNATURE_CHECKSUMS = "aether.checksums.forSignature";
  
  static final String CONFIG_PROP_CHECKSUMS_ALGORITHMS = "aether.checksums.algorithms";
  
  static final String DEFAULT_CHECKSUMS_ALGORITHMS = "SHA-1,MD5";
  
  private float priority;
  
  public float getPriority() {
    return this.priority;
  }
  
  public Maven2RepositoryLayoutFactory setPriority(float priority) {
    this.priority = priority;
    return this;
  }
  
  public RepositoryLayout newInstance(RepositorySystemSession session, RemoteRepository repository) throws NoRepositoryLayoutException {
    if (!"default".equals(repository.getContentType()))
      throw new NoRepositoryLayoutException(repository); 
    boolean forSignature = ConfigUtils.getBoolean(session, false, new String[] { "aether.checksums.forSignature" });
    List<String> checksumsAlgorithms = Arrays.asList(ConfigUtils.getString(session, "SHA-1,MD5", new String[] { "aether.checksums.algorithms" }).split(","));
    return forSignature ? new Maven2RepositoryLayout(checksumsAlgorithms) : new Maven2RepositoryLayoutEx(checksumsAlgorithms);
  }
  
  private static class Maven2RepositoryLayout implements RepositoryLayout {
    private final List<String> checksumsAlgorithms;
    
    protected Maven2RepositoryLayout(List<String> checksumsAlgorithms) {
      this.checksumsAlgorithms = checksumsAlgorithms;
    }
    
    private URI toUri(String path) {
      try {
        return new URI(null, null, path, null);
      } catch (URISyntaxException e) {
        throw new IllegalStateException(e);
      } 
    }
    
    public URI getLocation(Artifact artifact, boolean upload) {
      StringBuilder path = new StringBuilder(128);
      path.append(artifact.getGroupId().replace('.', '/')).append('/');
      path.append(artifact.getArtifactId()).append('/');
      path.append(artifact.getBaseVersion()).append('/');
      path.append(artifact.getArtifactId()).append('-').append(artifact.getVersion());
      if (artifact.getClassifier().length() > 0)
        path.append('-').append(artifact.getClassifier()); 
      if (artifact.getExtension().length() > 0)
        path.append('.').append(artifact.getExtension()); 
      return toUri(path.toString());
    }
    
    public URI getLocation(Metadata metadata, boolean upload) {
      StringBuilder path = new StringBuilder(128);
      if (metadata.getGroupId().length() > 0) {
        path.append(metadata.getGroupId().replace('.', '/')).append('/');
        if (metadata.getArtifactId().length() > 0) {
          path.append(metadata.getArtifactId()).append('/');
          if (metadata.getVersion().length() > 0)
            path.append(metadata.getVersion()).append('/'); 
        } 
      } 
      path.append(metadata.getType());
      return toUri(path.toString());
    }
    
    public List<RepositoryLayout.Checksum> getChecksums(Artifact artifact, boolean upload, URI location) {
      return getChecksums(location);
    }
    
    public List<RepositoryLayout.Checksum> getChecksums(Metadata metadata, boolean upload, URI location) {
      return getChecksums(location);
    }
    
    private List<RepositoryLayout.Checksum> getChecksums(URI location) {
      List<RepositoryLayout.Checksum> checksums = new ArrayList<>(this.checksumsAlgorithms.size());
      for (String algorithm : this.checksumsAlgorithms)
        checksums.add(RepositoryLayout.Checksum.forLocation(location, algorithm)); 
      return checksums;
    }
  }
  
  private static class Maven2RepositoryLayoutEx extends Maven2RepositoryLayout {
    protected Maven2RepositoryLayoutEx(List<String> checksumsAlgorithms) {
      super(checksumsAlgorithms);
    }
    
    public List<RepositoryLayout.Checksum> getChecksums(Artifact artifact, boolean upload, URI location) {
      if (isSignature(artifact.getExtension()))
        return Collections.emptyList(); 
      return super.getChecksums(artifact, upload, location);
    }
    
    private boolean isSignature(String extension) {
      return extension.endsWith(".asc");
    }
  }
}
