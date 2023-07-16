package com.mysql.cj.xdevapi;

import java.math.BigDecimal;

public class JsonNumber implements JsonValue {
  private String val = "null";
  
  public Integer getInteger() {
    return Integer.valueOf((new BigDecimal(this.val)).intValue());
  }
  
  public BigDecimal getBigDecimal() {
    return new BigDecimal(this.val);
  }
  
  public JsonNumber setValue(String value) {
    this.val = (new BigDecimal(value)).toString();
    return this;
  }
  
  public String toString() {
    return this.val;
  }
}
