package org.apache.maven.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class ReportPlugin extends ConfigurationContainer implements Serializable, Cloneable {
  private String groupId = "org.apache.maven.plugins";
  
  private String artifactId;
  
  private String version;
  
  private List<ReportSet> reportSets;
  
  public void addReportSet(ReportSet reportSet) {
    getReportSets().add(reportSet);
  }
  
  public ReportPlugin clone() {
    try {
      ReportPlugin copy = (ReportPlugin)super.clone();
      if (this.reportSets != null) {
        copy.reportSets = new ArrayList<>();
        for (ReportSet item : this.reportSets)
          copy.reportSets.add(item.clone()); 
      } 
      return copy;
    } catch (Exception ex) {
      throw (RuntimeException)(new UnsupportedOperationException(getClass().getName() + " does not support clone()"))
        .initCause(ex);
    } 
  }
  
  public String getArtifactId() {
    return this.artifactId;
  }
  
  public String getGroupId() {
    return this.groupId;
  }
  
  public List<ReportSet> getReportSets() {
    if (this.reportSets == null)
      this.reportSets = new ArrayList<>(); 
    return this.reportSets;
  }
  
  public String getVersion() {
    return this.version;
  }
  
  public void removeReportSet(ReportSet reportSet) {
    getReportSets().remove(reportSet);
  }
  
  public void setArtifactId(String artifactId) {
    this.artifactId = artifactId;
  }
  
  public void setGroupId(String groupId) {
    this.groupId = groupId;
  }
  
  public void setReportSets(List<ReportSet> reportSets) {
    this.reportSets = reportSets;
  }
  
  public void setVersion(String version) {
    this.version = version;
  }
  
  private Map<String, ReportSet> reportSetMap = null;
  
  public void flushReportSetMap() {
    this.reportSetMap = null;
  }
  
  public Map<String, ReportSet> getReportSetsAsMap() {
    if (this.reportSetMap == null) {
      this.reportSetMap = new LinkedHashMap<>();
      if (getReportSets() != null)
        for (Iterator<ReportSet> i = getReportSets().iterator(); i.hasNext(); ) {
          ReportSet reportSet = i.next();
          this.reportSetMap.put(reportSet.getId(), reportSet);
        }  
    } 
    return this.reportSetMap;
  }
  
  public String getKey() {
    return constructKey(this.groupId, this.artifactId);
  }
  
  public static String constructKey(String groupId, String artifactId) {
    return groupId + ":" + artifactId;
  }
}
