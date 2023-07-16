package org.yaml.snakeyaml.inspector;

import java.util.List;
import org.yaml.snakeyaml.nodes.Tag;

public final class TrustedPrefixesTagInspector implements TagInspector {
  private final List<String> trustedList;
  
  public TrustedPrefixesTagInspector(List<String> trustedList) {
    this.trustedList = trustedList;
  }
  
  public boolean isGlobalTagAllowed(Tag tag) {
    for (String trusted : this.trustedList) {
      if (tag.getClassName().startsWith(trusted))
        return true; 
    } 
    return false;
  }
}
