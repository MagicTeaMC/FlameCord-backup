package org.apache.maven.model.superpom;

import org.apache.maven.model.Model;

public interface SuperPomProvider {
  Model getSuperModel(String paramString);
}
