package org.apache.maven.model.building;

import java.net.URL;
import org.apache.maven.building.UrlSource;

@Deprecated
public class UrlModelSource extends UrlSource implements ModelSource {
  public UrlModelSource(URL pomUrl) {
    super(pomUrl);
  }
}
