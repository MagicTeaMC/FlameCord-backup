package org.apache.maven.repository.internal;

import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.util.Collections;
import java.util.Date;
import java.util.Map;
import org.apache.maven.artifact.repository.metadata.Metadata;
import org.apache.maven.artifact.repository.metadata.io.xpp3.MetadataXpp3Reader;
import org.apache.maven.artifact.repository.metadata.io.xpp3.MetadataXpp3Writer;
import org.codehaus.plexus.util.ReaderFactory;
import org.codehaus.plexus.util.WriterFactory;
import org.codehaus.plexus.util.xml.XmlStreamReader;
import org.codehaus.plexus.util.xml.XmlStreamWriter;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;
import org.eclipse.aether.RepositoryException;
import org.eclipse.aether.metadata.AbstractMetadata;
import org.eclipse.aether.metadata.MergeableMetadata;
import org.eclipse.aether.metadata.Metadata;

abstract class MavenMetadata extends AbstractMetadata implements MergeableMetadata {
  static final String MAVEN_METADATA_XML = "maven-metadata.xml";
  
  protected Metadata metadata;
  
  private final File file;
  
  protected final Date timestamp;
  
  private boolean merged;
  
  protected MavenMetadata(Metadata metadata, File file, Date timestamp) {
    this.metadata = metadata;
    this.file = file;
    this.timestamp = timestamp;
  }
  
  public String getType() {
    return "maven-metadata.xml";
  }
  
  public File getFile() {
    return this.file;
  }
  
  public void merge(File existing, File result) throws RepositoryException {
    Metadata recessive = read(existing);
    merge(recessive);
    write(result, this.metadata);
    this.merged = true;
  }
  
  public boolean isMerged() {
    return this.merged;
  }
  
  protected abstract void merge(Metadata paramMetadata);
  
  static Metadata read(File metadataFile) throws RepositoryException {
    if (metadataFile.length() <= 0L)
      return new Metadata(); 
    try (XmlStreamReader null = ReaderFactory.newXmlReader(metadataFile)) {
      return (new MetadataXpp3Reader()).read((Reader)xmlStreamReader, false);
    } catch (IOException e) {
      throw new RepositoryException("Could not read metadata " + metadataFile + ": " + e.getMessage(), e);
    } catch (XmlPullParserException e) {
      throw new RepositoryException("Could not parse metadata " + metadataFile + ": " + e.getMessage(), e);
    } 
  }
  
  private void write(File metadataFile, Metadata metadata) throws RepositoryException {
    metadataFile.getParentFile().mkdirs();
    try (XmlStreamWriter null = WriterFactory.newXmlWriter(metadataFile)) {
      (new MetadataXpp3Writer()).write((Writer)xmlStreamWriter, metadata);
    } catch (IOException e) {
      throw new RepositoryException("Could not write metadata " + metadataFile + ": " + e.getMessage(), e);
    } 
  }
  
  public Map<String, String> getProperties() {
    return Collections.emptyMap();
  }
  
  public Metadata setProperties(Map<String, String> properties) {
    return (Metadata)this;
  }
}
