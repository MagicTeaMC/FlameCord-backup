package org.apache.logging.log4j.core.config;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.JarURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;
import org.apache.logging.log4j.core.net.UrlConnectionFactory;
import org.apache.logging.log4j.core.util.FileUtils;
import org.apache.logging.log4j.core.util.Loader;
import org.apache.logging.log4j.core.util.Source;
import org.apache.logging.log4j.util.Constants;
import org.apache.logging.log4j.util.LoaderUtil;

public class ConfigurationSource {
  public static final ConfigurationSource NULL_SOURCE = new ConfigurationSource(Constants.EMPTY_BYTE_ARRAY, null, 0L);
  
  public static final ConfigurationSource COMPOSITE_SOURCE = new ConfigurationSource(Constants.EMPTY_BYTE_ARRAY, null, 0L);
  
  private final InputStream stream;
  
  private volatile byte[] data;
  
  private volatile Source source;
  
  private final long lastModified;
  
  private volatile long modifiedMillis;
  
  public ConfigurationSource(InputStream stream, File file) {
    this.stream = Objects.<InputStream>requireNonNull(stream, "stream is null");
    this.data = null;
    this.source = new Source(file);
    long modified = 0L;
    try {
      modified = file.lastModified();
    } catch (Exception exception) {}
    this.lastModified = modified;
  }
  
  public ConfigurationSource(InputStream stream, Path path) {
    this.stream = Objects.<InputStream>requireNonNull(stream, "stream is null");
    this.data = null;
    this.source = new Source(path);
    long modified = 0L;
    try {
      modified = Files.getLastModifiedTime(path, new java.nio.file.LinkOption[0]).toMillis();
    } catch (Exception exception) {}
    this.lastModified = modified;
  }
  
  public ConfigurationSource(InputStream stream, URL url) {
    this.stream = Objects.<InputStream>requireNonNull(stream, "stream is null");
    this.data = null;
    this.lastModified = 0L;
    this.source = new Source(url);
  }
  
  public ConfigurationSource(InputStream stream, URL url, long lastModified) {
    this.stream = Objects.<InputStream>requireNonNull(stream, "stream is null");
    this.data = null;
    this.lastModified = lastModified;
    this.source = new Source(url);
  }
  
  public ConfigurationSource(InputStream stream) throws IOException {
    this(toByteArray(stream), (URL)null, 0L);
  }
  
  public ConfigurationSource(Source source, byte[] data, long lastModified) {
    Objects.requireNonNull(source, "source is null");
    this.data = Objects.<byte[]>requireNonNull(data, "data is null");
    this.stream = new ByteArrayInputStream(data);
    this.lastModified = lastModified;
    this.source = source;
  }
  
  private ConfigurationSource(byte[] data, URL url, long lastModified) {
    this.data = Objects.<byte[]>requireNonNull(data, "data is null");
    this.stream = new ByteArrayInputStream(data);
    this.lastModified = lastModified;
    if (url == null) {
      this.data = data;
    } else {
      this.source = new Source(url);
    } 
  }
  
  private static byte[] toByteArray(InputStream inputStream) throws IOException {
    int buffSize = Math.max(4096, inputStream.available());
    ByteArrayOutputStream contents = new ByteArrayOutputStream(buffSize);
    byte[] buff = new byte[buffSize];
    int length = inputStream.read(buff);
    while (length > 0) {
      contents.write(buff, 0, length);
      length = inputStream.read(buff);
    } 
    return contents.toByteArray();
  }
  
  public File getFile() {
    return (this.source == null) ? null : this.source.getFile();
  }
  
  private boolean isFile() {
    return (this.source == null) ? false : ((this.source.getFile() != null));
  }
  
  private boolean isURL() {
    return (this.source == null) ? false : ((this.source.getURI() != null));
  }
  
  private boolean isLocation() {
    return (this.source == null) ? false : ((this.source.getLocation() != null));
  }
  
  public URL getURL() {
    return (this.source == null) ? null : this.source.getURL();
  }
  
  @Deprecated
  public void setSource(Source source) {
    this.source = source;
  }
  
  public void setData(byte[] data) {
    this.data = data;
  }
  
  public void setModifiedMillis(long modifiedMillis) {
    this.modifiedMillis = modifiedMillis;
  }
  
  public URI getURI() {
    return (this.source == null) ? null : this.source.getURI();
  }
  
  public long getLastModified() {
    return this.lastModified;
  }
  
  public String getLocation() {
    return (this.source == null) ? null : this.source.getLocation();
  }
  
  public InputStream getInputStream() {
    return this.stream;
  }
  
  public ConfigurationSource resetInputStream() throws IOException {
    if (this.source != null && this.data != null)
      return new ConfigurationSource(this.source, this.data, this.lastModified); 
    if (isFile())
      return new ConfigurationSource(new FileInputStream(getFile()), getFile()); 
    if (isURL() && this.data != null)
      return new ConfigurationSource(this.data, getURL(), (this.modifiedMillis == 0L) ? this.lastModified : this.modifiedMillis); 
    if (isURL())
      return fromUri(getURI()); 
    if (this.data != null)
      return new ConfigurationSource(this.data, null, this.lastModified); 
    return null;
  }
  
  public String toString() {
    if (isLocation())
      return getLocation(); 
    if (this == NULL_SOURCE)
      return "NULL_SOURCE"; 
    int length = (this.data == null) ? -1 : this.data.length;
    return "stream (" + length + " bytes, unknown location)";
  }
  
  public static ConfigurationSource fromUri(URI configLocation) {
    File configFile = FileUtils.fileFromUri(configLocation);
    if (configFile != null && configFile.exists() && configFile.canRead())
      try {
        return new ConfigurationSource(new FileInputStream(configFile), configFile);
      } catch (FileNotFoundException ex) {
        ConfigurationFactory.LOGGER.error("Cannot locate file {}", configLocation.getPath(), ex);
      }  
    if (ConfigurationFactory.isClassLoaderUri(configLocation)) {
      ClassLoader loader = LoaderUtil.getThreadContextClassLoader();
      String path = ConfigurationFactory.extractClassLoaderUriPath(configLocation);
      return fromResource(path, loader);
    } 
    if (!configLocation.isAbsolute()) {
      ConfigurationFactory.LOGGER.error("File not found in file system or classpath: {}", configLocation.toString());
      return null;
    } 
    try {
      return getConfigurationSource(configLocation.toURL());
    } catch (MalformedURLException ex) {
      ConfigurationFactory.LOGGER.error("Invalid URL {}", configLocation.toString(), ex);
      return null;
    } 
  }
  
  public static ConfigurationSource fromResource(String resource, ClassLoader loader) {
    URL url = Loader.getResource(resource, loader);
    if (url == null)
      return null; 
    return getConfigurationSource(url);
  }
  
  private static ConfigurationSource getConfigurationSource(URL url) {
    try {
      File file = FileUtils.fileFromUri(url.toURI());
      URLConnection urlConnection = UrlConnectionFactory.createConnection(url);
      try {
        if (file != null)
          return new ConfigurationSource(urlConnection.getInputStream(), FileUtils.fileFromUri(url.toURI())); 
        if (urlConnection instanceof JarURLConnection) {
          long lastModified = (new File(((JarURLConnection)urlConnection).getJarFile().getName())).lastModified();
          return new ConfigurationSource(urlConnection.getInputStream(), url, lastModified);
        } 
        return new ConfigurationSource(urlConnection.getInputStream(), url, urlConnection.getLastModified());
      } catch (FileNotFoundException ex) {
        ConfigurationFactory.LOGGER.info("Unable to locate file {}, ignoring.", url.toString());
        return null;
      } 
    } catch (IOException|java.net.URISyntaxException ex) {
      ConfigurationFactory.LOGGER.warn("Error accessing {} due to {}, ignoring.", url.toString(), ex
          .getMessage());
      return null;
    } 
  }
}
