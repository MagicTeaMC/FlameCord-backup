package com.mysql.cj;

import java.util.Arrays;

class Collation {
  public final int index;
  
  public final String[] collationNames;
  
  public final int priority;
  
  public final MysqlCharset mysqlCharset;
  
  public Collation(int index, String collationName, int priority, String charsetName) {
    this(index, new String[] { collationName }, priority, charsetName);
  }
  
  public Collation(int index, String[] collationNames, int priority, String charsetName) {
    this.index = index;
    this.collationNames = collationNames;
    this.priority = priority;
    this.mysqlCharset = CharsetMapping.getStaticMysqlCharsetByName(charsetName);
  }
  
  public String toString() {
    StringBuilder asString = new StringBuilder();
    asString.append("[");
    asString.append("index=");
    asString.append(this.index);
    asString.append(",collationNames=");
    asString.append(Arrays.toString((Object[])this.collationNames));
    asString.append(",charsetName=");
    asString.append(this.mysqlCharset.charsetName);
    asString.append(",javaCharsetName=");
    asString.append(this.mysqlCharset.getMatchingJavaEncoding(null));
    asString.append("]");
    return asString.toString();
  }
}
