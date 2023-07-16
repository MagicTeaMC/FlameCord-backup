package org.eclipse.aether.spi.connector.layout;

import java.net.URI;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import org.eclipse.aether.artifact.Artifact;
import org.eclipse.aether.metadata.Metadata;

public interface RepositoryLayout {
  URI getLocation(Artifact paramArtifact, boolean paramBoolean);
  
  URI getLocation(Metadata paramMetadata, boolean paramBoolean);
  
  List<Checksum> getChecksums(Artifact paramArtifact, boolean paramBoolean, URI paramURI);
  
  List<Checksum> getChecksums(Metadata paramMetadata, boolean paramBoolean, URI paramURI);
  
  public static final class Checksum {
    private final String algorithm;
    
    private final URI location;
    
    public Checksum(String algorithm, URI location) {
      verify(algorithm, location);
      this.algorithm = algorithm;
      this.location = location;
    }
    
    public static Checksum forLocation(URI location, String algorithm) {
      verify(algorithm, location);
      if (location.getRawQuery() != null)
        throw new IllegalArgumentException("resource location must not have query parameters: " + location); 
      if (location.getRawFragment() != null)
        throw new IllegalArgumentException("resource location must not have a fragment: " + location); 
      String extension = '.' + algorithm.replace("-", "").toLowerCase(Locale.ENGLISH);
      return new Checksum(algorithm, URI.create(location.toString() + extension));
    }
    
    private static void verify(String algorithm, URI location) {
      Objects.requireNonNull(algorithm, "checksum algorithm cannot be null");
      if (algorithm.length() == 0)
        throw new IllegalArgumentException("checksum algorithm cannot be empty"); 
      Objects.requireNonNull(location, "checksum location cannot be null");
      if (location.isAbsolute())
        throw new IllegalArgumentException("checksum location must be relative"); 
    }
    
    public String getAlgorithm() {
      return this.algorithm;
    }
    
    public URI getLocation() {
      return this.location;
    }
    
    public String toString() {
      return this.location + " (" + this.algorithm + ")";
    }
  }
}
