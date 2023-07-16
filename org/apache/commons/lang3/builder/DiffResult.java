package org.apache.commons.lang3.builder;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import org.apache.commons.lang3.Validate;

public class DiffResult implements Iterable<Diff<?>> {
  public static final String OBJECTS_SAME_STRING = "";
  
  private static final String DIFFERS_STRING = "differs from";
  
  private final List<Diff<?>> diffs;
  
  private final Object lhs;
  
  private final Object rhs;
  
  private final ToStringStyle style;
  
  DiffResult(Object lhs, Object rhs, List<Diff<?>> diffs, ToStringStyle style) {
    Validate.isTrue((lhs != null), "Left hand object cannot be null", new Object[0]);
    Validate.isTrue((rhs != null), "Right hand object cannot be null", new Object[0]);
    Validate.isTrue((diffs != null), "List of differences cannot be null", new Object[0]);
    this.diffs = diffs;
    this.lhs = lhs;
    this.rhs = rhs;
    if (style == null) {
      this.style = ToStringStyle.DEFAULT_STYLE;
    } else {
      this.style = style;
    } 
  }
  
  public List<Diff<?>> getDiffs() {
    return Collections.unmodifiableList(this.diffs);
  }
  
  public int getNumberOfDiffs() {
    return this.diffs.size();
  }
  
  public ToStringStyle getToStringStyle() {
    return this.style;
  }
  
  public String toString() {
    return toString(this.style);
  }
  
  public String toString(ToStringStyle style) {
    if (this.diffs.isEmpty())
      return ""; 
    ToStringBuilder lhsBuilder = new ToStringBuilder(this.lhs, style);
    ToStringBuilder rhsBuilder = new ToStringBuilder(this.rhs, style);
    for (Diff<?> diff : this.diffs) {
      lhsBuilder.append(diff.getFieldName(), diff.getLeft());
      rhsBuilder.append(diff.getFieldName(), diff.getRight());
    } 
    return String.format("%s %s %s", new Object[] { lhsBuilder.build(), "differs from", rhsBuilder
          .build() });
  }
  
  public Iterator<Diff<?>> iterator() {
    return this.diffs.iterator();
  }
}
