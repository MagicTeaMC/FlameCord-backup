package org.slf4j.impl;

import org.apache.logging.slf4j.Log4jMDCAdapter;
import org.slf4j.spi.MDCAdapter;

public final class StaticMDCBinder {
  public static final StaticMDCBinder SINGLETON = new StaticMDCBinder();
  
  private final MDCAdapter mdcAdapter = (MDCAdapter)new Log4jMDCAdapter();
  
  public static StaticMDCBinder getSingleton() {
    return SINGLETON;
  }
  
  public MDCAdapter getMDCA() {
    return this.mdcAdapter;
  }
  
  public String getMDCAdapterClassStr() {
    return Log4jMDCAdapter.class.getName();
  }
}
