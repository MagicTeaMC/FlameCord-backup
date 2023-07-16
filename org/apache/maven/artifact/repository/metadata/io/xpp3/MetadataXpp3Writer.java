package org.apache.maven.artifact.repository.metadata.io.xpp3;

import java.io.IOException;
import java.io.OutputStream;
import java.io.Writer;
import java.util.Iterator;
import org.apache.maven.artifact.repository.metadata.Metadata;
import org.apache.maven.artifact.repository.metadata.Plugin;
import org.apache.maven.artifact.repository.metadata.Snapshot;
import org.apache.maven.artifact.repository.metadata.SnapshotVersion;
import org.apache.maven.artifact.repository.metadata.Versioning;
import org.codehaus.plexus.util.xml.pull.MXSerializer;
import org.codehaus.plexus.util.xml.pull.XmlSerializer;

public class MetadataXpp3Writer {
  private static final String NAMESPACE = null;
  
  private String fileComment = null;
  
  public void setFileComment(String fileComment) {
    this.fileComment = fileComment;
  }
  
  public void write(Writer writer, Metadata metadata) throws IOException {
    MXSerializer mXSerializer = new MXSerializer();
    mXSerializer.setProperty("http://xmlpull.org/v1/doc/properties.html#serializer-indentation", "  ");
    mXSerializer.setProperty("http://xmlpull.org/v1/doc/properties.html#serializer-line-separator", "\n");
    mXSerializer.setOutput(writer);
    mXSerializer.startDocument(metadata.getModelEncoding(), null);
    writeMetadata(metadata, "metadata", (XmlSerializer)mXSerializer);
    mXSerializer.endDocument();
  }
  
  public void write(OutputStream stream, Metadata metadata) throws IOException {
    MXSerializer mXSerializer = new MXSerializer();
    mXSerializer.setProperty("http://xmlpull.org/v1/doc/properties.html#serializer-indentation", "  ");
    mXSerializer.setProperty("http://xmlpull.org/v1/doc/properties.html#serializer-line-separator", "\n");
    mXSerializer.setOutput(stream, metadata.getModelEncoding());
    mXSerializer.startDocument(metadata.getModelEncoding(), null);
    writeMetadata(metadata, "metadata", (XmlSerializer)mXSerializer);
    mXSerializer.endDocument();
  }
  
  private void writeMetadata(Metadata metadata, String tagName, XmlSerializer serializer) throws IOException {
    serializer.startTag(NAMESPACE, tagName);
    if (metadata.getModelVersion() != null)
      serializer.attribute(NAMESPACE, "modelVersion", metadata.getModelVersion()); 
    if (metadata.getGroupId() != null)
      serializer.startTag(NAMESPACE, "groupId").text(metadata.getGroupId()).endTag(NAMESPACE, "groupId"); 
    if (metadata.getArtifactId() != null)
      serializer.startTag(NAMESPACE, "artifactId").text(metadata.getArtifactId()).endTag(NAMESPACE, "artifactId"); 
    if (metadata.getVersioning() != null)
      writeVersioning(metadata.getVersioning(), "versioning", serializer); 
    if (metadata.getVersion() != null)
      serializer.startTag(NAMESPACE, "version").text(metadata.getVersion()).endTag(NAMESPACE, "version"); 
    if (metadata.getPlugins() != null && metadata.getPlugins().size() > 0) {
      serializer.startTag(NAMESPACE, "plugins");
      for (Iterator<Plugin> iter = metadata.getPlugins().iterator(); iter.hasNext(); ) {
        Plugin o = iter.next();
        writePlugin(o, "plugin", serializer);
      } 
      serializer.endTag(NAMESPACE, "plugins");
    } 
    serializer.endTag(NAMESPACE, tagName);
  }
  
  private void writePlugin(Plugin plugin, String tagName, XmlSerializer serializer) throws IOException {
    serializer.startTag(NAMESPACE, tagName);
    if (plugin.getName() != null)
      serializer.startTag(NAMESPACE, "name").text(plugin.getName()).endTag(NAMESPACE, "name"); 
    if (plugin.getPrefix() != null)
      serializer.startTag(NAMESPACE, "prefix").text(plugin.getPrefix()).endTag(NAMESPACE, "prefix"); 
    if (plugin.getArtifactId() != null)
      serializer.startTag(NAMESPACE, "artifactId").text(plugin.getArtifactId()).endTag(NAMESPACE, "artifactId"); 
    serializer.endTag(NAMESPACE, tagName);
  }
  
  private void writeSnapshot(Snapshot snapshot, String tagName, XmlSerializer serializer) throws IOException {
    serializer.startTag(NAMESPACE, tagName);
    if (snapshot.getTimestamp() != null)
      serializer.startTag(NAMESPACE, "timestamp").text(snapshot.getTimestamp()).endTag(NAMESPACE, "timestamp"); 
    if (snapshot.getBuildNumber() != 0)
      serializer.startTag(NAMESPACE, "buildNumber").text(String.valueOf(snapshot.getBuildNumber())).endTag(NAMESPACE, "buildNumber"); 
    if (snapshot.isLocalCopy())
      serializer.startTag(NAMESPACE, "localCopy").text(String.valueOf(snapshot.isLocalCopy())).endTag(NAMESPACE, "localCopy"); 
    serializer.endTag(NAMESPACE, tagName);
  }
  
  private void writeSnapshotVersion(SnapshotVersion snapshotVersion, String tagName, XmlSerializer serializer) throws IOException {
    serializer.startTag(NAMESPACE, tagName);
    if (snapshotVersion.getClassifier() != null && !snapshotVersion.getClassifier().equals(""))
      serializer.startTag(NAMESPACE, "classifier").text(snapshotVersion.getClassifier()).endTag(NAMESPACE, "classifier"); 
    if (snapshotVersion.getExtension() != null)
      serializer.startTag(NAMESPACE, "extension").text(snapshotVersion.getExtension()).endTag(NAMESPACE, "extension"); 
    if (snapshotVersion.getVersion() != null)
      serializer.startTag(NAMESPACE, "value").text(snapshotVersion.getVersion()).endTag(NAMESPACE, "value"); 
    if (snapshotVersion.getUpdated() != null)
      serializer.startTag(NAMESPACE, "updated").text(snapshotVersion.getUpdated()).endTag(NAMESPACE, "updated"); 
    serializer.endTag(NAMESPACE, tagName);
  }
  
  private void writeVersioning(Versioning versioning, String tagName, XmlSerializer serializer) throws IOException {
    serializer.startTag(NAMESPACE, tagName);
    if (versioning.getLatest() != null)
      serializer.startTag(NAMESPACE, "latest").text(versioning.getLatest()).endTag(NAMESPACE, "latest"); 
    if (versioning.getRelease() != null)
      serializer.startTag(NAMESPACE, "release").text(versioning.getRelease()).endTag(NAMESPACE, "release"); 
    if (versioning.getVersions() != null && versioning.getVersions().size() > 0) {
      serializer.startTag(NAMESPACE, "versions");
      for (Iterator<String> iter = versioning.getVersions().iterator(); iter.hasNext(); ) {
        String version = iter.next();
        serializer.startTag(NAMESPACE, "version").text(version).endTag(NAMESPACE, "version");
      } 
      serializer.endTag(NAMESPACE, "versions");
    } 
    if (versioning.getLastUpdated() != null)
      serializer.startTag(NAMESPACE, "lastUpdated").text(versioning.getLastUpdated()).endTag(NAMESPACE, "lastUpdated"); 
    if (versioning.getSnapshot() != null)
      writeSnapshot(versioning.getSnapshot(), "snapshot", serializer); 
    if (versioning.getSnapshotVersions() != null && versioning.getSnapshotVersions().size() > 0) {
      serializer.startTag(NAMESPACE, "snapshotVersions");
      for (Iterator<SnapshotVersion> iter = versioning.getSnapshotVersions().iterator(); iter.hasNext(); ) {
        SnapshotVersion o = iter.next();
        writeSnapshotVersion(o, "snapshotVersion", serializer);
      } 
      serializer.endTag(NAMESPACE, "snapshotVersions");
    } 
    serializer.endTag(NAMESPACE, tagName);
  }
}
