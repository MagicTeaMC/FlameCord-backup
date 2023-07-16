package org.apache.maven.artifact.repository.metadata;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

public class Versioning implements Serializable, Cloneable {
  private String latest;
  
  private String release;
  
  private List<String> versions;
  
  private String lastUpdated;
  
  private Snapshot snapshot;
  
  private List<SnapshotVersion> snapshotVersions;
  
  public void addSnapshotVersion(SnapshotVersion snapshotVersion) {
    getSnapshotVersions().add(snapshotVersion);
  }
  
  public void addVersion(String string) {
    getVersions().add(string);
  }
  
  public Versioning clone() {
    try {
      Versioning copy = (Versioning)super.clone();
      if (this.versions != null) {
        copy.versions = new ArrayList<>();
        copy.versions.addAll(this.versions);
      } 
      if (this.snapshot != null)
        copy.snapshot = this.snapshot.clone(); 
      if (this.snapshotVersions != null) {
        copy.snapshotVersions = new ArrayList<>();
        for (SnapshotVersion item : this.snapshotVersions)
          copy.snapshotVersions.add(item.clone()); 
      } 
      return copy;
    } catch (Exception ex) {
      throw (RuntimeException)(new UnsupportedOperationException(getClass().getName() + " does not support clone()"))
        .initCause(ex);
    } 
  }
  
  public String getLastUpdated() {
    return this.lastUpdated;
  }
  
  public String getLatest() {
    return this.latest;
  }
  
  public String getRelease() {
    return this.release;
  }
  
  public Snapshot getSnapshot() {
    return this.snapshot;
  }
  
  public List<SnapshotVersion> getSnapshotVersions() {
    if (this.snapshotVersions == null)
      this.snapshotVersions = new ArrayList<>(); 
    return this.snapshotVersions;
  }
  
  public List<String> getVersions() {
    if (this.versions == null)
      this.versions = new ArrayList<>(); 
    return this.versions;
  }
  
  public void removeSnapshotVersion(SnapshotVersion snapshotVersion) {
    getSnapshotVersions().remove(snapshotVersion);
  }
  
  public void removeVersion(String string) {
    getVersions().remove(string);
  }
  
  public void setLastUpdated(String lastUpdated) {
    this.lastUpdated = lastUpdated;
  }
  
  public void setLatest(String latest) {
    this.latest = latest;
  }
  
  public void setRelease(String release) {
    this.release = release;
  }
  
  public void setSnapshot(Snapshot snapshot) {
    this.snapshot = snapshot;
  }
  
  public void setSnapshotVersions(List<SnapshotVersion> snapshotVersions) {
    this.snapshotVersions = snapshotVersions;
  }
  
  public void setVersions(List<String> versions) {
    this.versions = versions;
  }
  
  public void updateTimestamp() {
    setLastUpdatedTimestamp(new Date());
  }
  
  public void setLastUpdatedTimestamp(Date date) {
    TimeZone timezone = TimeZone.getTimeZone("UTC");
    DateFormat fmt = new SimpleDateFormat("yyyyMMddHHmmss");
    fmt.setTimeZone(timezone);
    setLastUpdated(fmt.format(date));
  }
}
