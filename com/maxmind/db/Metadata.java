package com.maxmind.db;

import java.math.BigInteger;
import java.util.Date;
import java.util.List;
import java.util.Map;

public final class Metadata {
  private final int binaryFormatMajorVersion;
  
  private final int binaryFormatMinorVersion;
  
  private final BigInteger buildEpoch;
  
  private final String databaseType;
  
  private final Map<String, String> description;
  
  private final int ipVersion;
  
  private final List<String> languages;
  
  private final int nodeByteSize;
  
  private final int nodeCount;
  
  private final int recordSize;
  
  private final int searchTreeSize;
  
  @MaxMindDbConstructor
  public Metadata(@MaxMindDbParameter(name = "binary_format_major_version") int binaryFormatMajorVersion, @MaxMindDbParameter(name = "binary_format_minor_version") int binaryFormatMinorVersion, @MaxMindDbParameter(name = "build_epoch") BigInteger buildEpoch, @MaxMindDbParameter(name = "database_type") String databaseType, @MaxMindDbParameter(name = "languages") List<String> languages, @MaxMindDbParameter(name = "description") Map<String, String> description, @MaxMindDbParameter(name = "ip_version") int ipVersion, @MaxMindDbParameter(name = "node_count") long nodeCount, @MaxMindDbParameter(name = "record_size") int recordSize) {
    this.binaryFormatMajorVersion = binaryFormatMajorVersion;
    this.binaryFormatMinorVersion = binaryFormatMinorVersion;
    this.buildEpoch = buildEpoch;
    this.databaseType = databaseType;
    this.languages = languages;
    this.description = description;
    this.ipVersion = ipVersion;
    this.nodeCount = (int)nodeCount;
    this.recordSize = recordSize;
    this.nodeByteSize = this.recordSize / 4;
    this.searchTreeSize = this.nodeCount * this.nodeByteSize;
  }
  
  public int getBinaryFormatMajorVersion() {
    return this.binaryFormatMajorVersion;
  }
  
  public int getBinaryFormatMinorVersion() {
    return this.binaryFormatMinorVersion;
  }
  
  public Date getBuildDate() {
    return new Date(this.buildEpoch.longValue() * 1000L);
  }
  
  public String getDatabaseType() {
    return this.databaseType;
  }
  
  public Map<String, String> getDescription() {
    return this.description;
  }
  
  public int getIpVersion() {
    return this.ipVersion;
  }
  
  public List<String> getLanguages() {
    return this.languages;
  }
  
  int getNodeByteSize() {
    return this.nodeByteSize;
  }
  
  int getNodeCount() {
    return this.nodeCount;
  }
  
  int getRecordSize() {
    return this.recordSize;
  }
  
  int getSearchTreeSize() {
    return this.searchTreeSize;
  }
  
  public String toString() {
    return "Metadata [binaryFormatMajorVersion=" + this.binaryFormatMajorVersion + ", binaryFormatMinorVersion=" + this.binaryFormatMinorVersion + ", buildEpoch=" + this.buildEpoch + ", databaseType=" + this.databaseType + ", description=" + this.description + ", ipVersion=" + this.ipVersion + ", nodeCount=" + this.nodeCount + ", recordSize=" + this.recordSize + "]";
  }
}
