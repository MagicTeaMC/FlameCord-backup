package org.apache.logging.log4j.core.util;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.config.ConfigurationSource;
import org.apache.logging.log4j.status.StatusLogger;
import org.apache.logging.log4j.util.Strings;

public class Source {
  private static final Logger LOGGER = (Logger)StatusLogger.getLogger();
  
  private final File file;
  
  private final URI uri;
  
  private final String location;
  
  private static String normalize(File file) {
    try {
      return file.getCanonicalFile().getAbsolutePath();
    } catch (IOException e) {
      throw new IllegalArgumentException(e);
    } 
  }
  
  private static File toFile(Path path) {
    try {
      return ((Path)Objects.<Path>requireNonNull(path, "path")).toFile();
    } catch (UnsupportedOperationException e) {
      return null;
    } 
  }
  
  private static File toFile(URI uri) {
    try {
      String scheme = ((URI)Objects.<URI>requireNonNull(uri, "uri")).getScheme();
      if (Strings.isBlank(scheme) || scheme.equals("file"))
        return new File(uri.getPath()); 
      LOGGER.debug("uri does not represent a local file: " + uri);
      return null;
    } catch (Exception e) {
      LOGGER.debug("uri is malformed: " + uri.toString());
      return null;
    } 
  }
  
  private static URI toURI(URL url) {
    try {
      return ((URL)Objects.<URL>requireNonNull(url, "url")).toURI();
    } catch (URISyntaxException e) {
      throw new IllegalArgumentException(e);
    } 
  }
  
  public Source(ConfigurationSource source) {
    this.file = source.getFile();
    this.uri = source.getURI();
    this.location = source.getLocation();
  }
  
  public Source(File file) {
    this.file = Objects.<File>requireNonNull(file, "file");
    this.location = normalize(file);
    this.uri = file.toURI();
  }
  
  public Source(Path path) {
    Path normPath = ((Path)Objects.<Path>requireNonNull(path, "path")).normalize();
    this.file = toFile(normPath);
    this.uri = normPath.toUri();
    this.location = normPath.toString();
  }
  
  public Source(URI uri) {
    URI normUri = ((URI)Objects.<URI>requireNonNull(uri, "uri")).normalize();
    this.uri = normUri;
    this.location = normUri.toString();
    this.file = toFile(normUri);
  }
  
  @Deprecated
  public Source(URI uri, long lastModified) {
    this(uri);
  }
  
  public Source(URL url) {
    this.uri = toURI(url);
    this.location = this.uri.toString();
    this.file = toFile(this.uri);
  }
  
  public boolean equals(Object obj) {
    if (this == obj)
      return true; 
    if (!(obj instanceof Source))
      return false; 
    Source other = (Source)obj;
    return Objects.equals(this.location, other.location);
  }
  
  public File getFile() {
    return this.file;
  }
  
  public String getLocation() {
    return this.location;
  }
  
  public Path getPath() {
    return (this.file != null) ? this.file.toPath() : ((this.uri != null) ? Paths.get(this.uri) : Paths.get(this.location, new String[0]));
  }
  
  public URI getURI() {
    return this.uri;
  }
  
  public URL getURL() {
    try {
      return this.uri.toURL();
    } catch (MalformedURLException e) {
      throw new IllegalStateException(e);
    } 
  }
  
  public int hashCode() {
    return Objects.hash(new Object[] { this.location });
  }
  
  public String toString() {
    return this.location;
  }
}
