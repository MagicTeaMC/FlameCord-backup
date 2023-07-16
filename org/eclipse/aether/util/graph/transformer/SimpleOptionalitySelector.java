package org.eclipse.aether.util.graph.transformer;

import java.util.Collection;
import org.eclipse.aether.RepositoryException;

public final class SimpleOptionalitySelector extends ConflictResolver.OptionalitySelector {
  public void selectOptionality(ConflictResolver.ConflictContext context) throws RepositoryException {
    boolean optional = chooseEffectiveOptionality(context.getItems());
    context.setOptional(Boolean.valueOf(optional));
  }
  
  private boolean chooseEffectiveOptionality(Collection<ConflictResolver.ConflictItem> items) {
    boolean optional = true;
    for (ConflictResolver.ConflictItem item : items) {
      if (item.getDepth() <= 1)
        return item.getDependency().isOptional(); 
      if ((item.getOptionalities() & 0x1) != 0)
        optional = false; 
    } 
    return optional;
  }
}
