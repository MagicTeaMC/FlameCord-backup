package org.codehaus.plexus.interpolation.fixed;

import java.util.List;

public class PrefixedObjectValueSource extends AbstractDelegatingValueSource {
  public PrefixedObjectValueSource(String prefix, Object root) {
    super(new PrefixedValueSourceWrapper(new ObjectBasedValueSource(root), prefix));
  }
  
  public PrefixedObjectValueSource(List<String> possiblePrefixes, Object root, boolean allowUnprefixedExpressions) {
    super(new PrefixedValueSourceWrapper(new ObjectBasedValueSource(root), possiblePrefixes, allowUnprefixedExpressions));
  }
}
