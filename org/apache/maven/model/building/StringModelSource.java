package org.apache.maven.model.building;

import org.apache.maven.building.StringSource;

@Deprecated
public class StringModelSource extends StringSource implements ModelSource {
  public StringModelSource(CharSequence pom) {
    this(pom, null);
  }
  
  public StringModelSource(CharSequence pom, String location) {
    super(pom, location);
  }
}
