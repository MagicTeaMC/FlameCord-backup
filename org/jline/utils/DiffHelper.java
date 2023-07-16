package org.jline.utils;

import java.util.LinkedList;
import java.util.List;

public class DiffHelper {
  public enum Operation {
    DELETE, INSERT, EQUAL;
  }
  
  public static class Diff {
    public final DiffHelper.Operation operation;
    
    public final AttributedString text;
    
    public Diff(DiffHelper.Operation operation, AttributedString text) {
      this.operation = operation;
      this.text = text;
    }
    
    public String toString() {
      return "Diff(" + this.operation + ",\"" + this.text + "\")";
    }
  }
  
  public static List<Diff> diff(AttributedString text1, AttributedString text2) {
    int l1 = text1.length();
    int l2 = text2.length();
    int n = Math.min(l1, l2);
    int commonStart = 0;
    int startHiddenRange = -1;
    while (commonStart < n && text1
      .charAt(commonStart) == text2.charAt(commonStart) && text1
      .styleAt(commonStart).equals(text2.styleAt(commonStart))) {
      if (text1.isHidden(commonStart)) {
        if (startHiddenRange < 0)
          startHiddenRange = commonStart; 
      } else {
        startHiddenRange = -1;
      } 
      commonStart++;
    } 
    if (startHiddenRange >= 0 && ((l1 > commonStart && text1
      .isHidden(commonStart)) || (l2 > commonStart && text2
      .isHidden(commonStart))))
      commonStart = startHiddenRange; 
    startHiddenRange = -1;
    int commonEnd = 0;
    while (commonEnd < n - commonStart && text1
      .charAt(l1 - commonEnd - 1) == text2.charAt(l2 - commonEnd - 1) && text1
      .styleAt(l1 - commonEnd - 1).equals(text2.styleAt(l2 - commonEnd - 1))) {
      if (text1.isHidden(l1 - commonEnd - 1)) {
        if (startHiddenRange < 0)
          startHiddenRange = commonEnd; 
      } else {
        startHiddenRange = -1;
      } 
      commonEnd++;
    } 
    if (startHiddenRange >= 0)
      commonEnd = startHiddenRange; 
    LinkedList<Diff> diffs = new LinkedList<>();
    if (commonStart > 0)
      diffs.add(new Diff(Operation.EQUAL, text1
            .subSequence(0, commonStart))); 
    if (l2 > commonStart + commonEnd)
      diffs.add(new Diff(Operation.INSERT, text2
            .subSequence(commonStart, l2 - commonEnd))); 
    if (l1 > commonStart + commonEnd)
      diffs.add(new Diff(Operation.DELETE, text1
            .subSequence(commonStart, l1 - commonEnd))); 
    if (commonEnd > 0)
      diffs.add(new Diff(Operation.EQUAL, text1
            .subSequence(l1 - commonEnd, l1))); 
    return diffs;
  }
}
