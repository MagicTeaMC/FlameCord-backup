package org.apache.maven.repository.internal;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.TimeZone;
import org.apache.maven.artifact.repository.metadata.Metadata;
import org.apache.maven.artifact.repository.metadata.Snapshot;
import org.apache.maven.artifact.repository.metadata.SnapshotVersion;
import org.apache.maven.artifact.repository.metadata.Versioning;
import org.eclipse.aether.artifact.Artifact;
import org.eclipse.aether.metadata.Metadata;

final class RemoteSnapshotMetadata extends MavenSnapshotMetadata {
  public static final String DEFAULT_SNAPSHOT_TIMESTAMP_FORMAT = "yyyyMMdd.HHmmss";
  
  public static final TimeZone DEFAULT_SNAPSHOT_TIME_ZONE = TimeZone.getTimeZone("Etc/UTC");
  
  private final Map<String, SnapshotVersion> versions = new LinkedHashMap<>();
  
  RemoteSnapshotMetadata(Artifact artifact, boolean legacyFormat, Date timestamp) {
    super(createRepositoryMetadata(artifact, legacyFormat), (File)null, legacyFormat, timestamp);
  }
  
  private RemoteSnapshotMetadata(Metadata metadata, File file, boolean legacyFormat, Date timestamp) {
    super(metadata, file, legacyFormat, timestamp);
  }
  
  public MavenMetadata setFile(File file) {
    return new RemoteSnapshotMetadata(this.metadata, file, this.legacyFormat, this.timestamp);
  }
  
  public String getExpandedVersion(Artifact artifact) {
    String key = getKey(artifact.getClassifier(), artifact.getExtension());
    return ((SnapshotVersion)this.versions.get(key)).getVersion();
  }
  
  protected void merge(Metadata recessive) {
    Snapshot snapshot;
    String lastUpdated;
    if (this.metadata.getVersioning() == null) {
      DateFormat utcDateFormatter = new SimpleDateFormat("yyyyMMdd.HHmmss");
      utcDateFormatter.setCalendar(new GregorianCalendar());
      utcDateFormatter.setTimeZone(DEFAULT_SNAPSHOT_TIME_ZONE);
      snapshot = new Snapshot();
      snapshot.setBuildNumber(getBuildNumber(recessive) + 1);
      snapshot.setTimestamp(utcDateFormatter.format(this.timestamp));
      Versioning versioning1 = new Versioning();
      versioning1.setSnapshot(snapshot);
      versioning1.setLastUpdatedTimestamp(this.timestamp);
      lastUpdated = versioning1.getLastUpdated();
      this.metadata.setVersioning(versioning1);
    } else {
      snapshot = this.metadata.getVersioning().getSnapshot();
      lastUpdated = this.metadata.getVersioning().getLastUpdated();
    } 
    for (Artifact artifact : this.artifacts) {
      String version = artifact.getVersion();
      if (version.endsWith("SNAPSHOT")) {
        String qualifier = snapshot.getTimestamp() + '-' + snapshot.getBuildNumber();
        version = version.substring(0, version.length() - "SNAPSHOT".length()) + qualifier;
      } 
      SnapshotVersion sv = new SnapshotVersion();
      sv.setClassifier(artifact.getClassifier());
      sv.setExtension(artifact.getExtension());
      sv.setVersion(version);
      sv.setUpdated(lastUpdated);
      this.versions.put(getKey(sv.getClassifier(), sv.getExtension()), sv);
    } 
    this.artifacts.clear();
    Versioning versioning = recessive.getVersioning();
    if (versioning != null)
      for (SnapshotVersion sv : versioning.getSnapshotVersions()) {
        String key = getKey(sv.getClassifier(), sv.getExtension());
        if (!this.versions.containsKey(key))
          this.versions.put(key, sv); 
      }  
    if (!this.legacyFormat)
      this.metadata.getVersioning().setSnapshotVersions(new ArrayList(this.versions.values())); 
  }
  
  private static int getBuildNumber(Metadata metadata) {
    int number = 0;
    Versioning versioning = metadata.getVersioning();
    if (versioning != null) {
      Snapshot snapshot = versioning.getSnapshot();
      if (snapshot != null && snapshot.getBuildNumber() > 0)
        number = snapshot.getBuildNumber(); 
    } 
    return number;
  }
}
