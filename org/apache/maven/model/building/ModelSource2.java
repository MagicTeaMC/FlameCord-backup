package org.apache.maven.model.building;

import java.net.URI;

public interface ModelSource2 extends ModelSource {
  ModelSource2 getRelatedSource(String paramString);
  
  URI getLocationURI();
}
