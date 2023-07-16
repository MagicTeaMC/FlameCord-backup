package org.eclipse.sisu.bean;

public interface PropertyBinding {
  <B> void injectProperty(B paramB);
}
