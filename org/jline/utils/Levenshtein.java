package org.jline.utils;

import java.util.HashMap;
import java.util.Map;

public class Levenshtein {
  public static int distance(CharSequence lhs, CharSequence rhs) {
    return distance(lhs, rhs, 1, 1, 1, 1);
  }
  
  public static int distance(CharSequence source, CharSequence target, int deleteCost, int insertCost, int replaceCost, int swapCost) {
    if (2 * swapCost < insertCost + deleteCost)
      throw new IllegalArgumentException("Unsupported cost assignment"); 
    if (source.length() == 0)
      return target.length() * insertCost; 
    if (target.length() == 0)
      return source.length() * deleteCost; 
    int[][] table = new int[source.length()][target.length()];
    Map<Character, Integer> sourceIndexByCharacter = new HashMap<>();
    if (source.charAt(0) != target.charAt(0))
      table[0][0] = Math.min(replaceCost, deleteCost + insertCost); 
    sourceIndexByCharacter.put(Character.valueOf(source.charAt(0)), Integer.valueOf(0));
    for (int k = 1; k < source.length(); k++) {
      int deleteDistance = table[k - 1][0] + deleteCost;
      int insertDistance = (k + 1) * deleteCost + insertCost;
      int matchDistance = k * deleteCost + ((source.charAt(k) == target.charAt(0)) ? 0 : replaceCost);
      table[k][0] = Math.min(Math.min(deleteDistance, insertDistance), matchDistance);
    } 
    for (int j = 1; j < target.length(); j++) {
      int deleteDistance = (j + 1) * insertCost + deleteCost;
      int insertDistance = table[0][j - 1] + insertCost;
      int matchDistance = j * insertCost + ((source.charAt(0) == target.charAt(j)) ? 0 : replaceCost);
      table[0][j] = Math.min(Math.min(deleteDistance, insertDistance), matchDistance);
    } 
    for (int i = 1; i < source.length(); i++) {
      int maxSourceLetterMatchIndex = (source.charAt(i) == target.charAt(0)) ? 0 : -1;
      for (int m = 1; m < target.length(); m++) {
        int swapDistance;
        Integer candidateSwapIndex = sourceIndexByCharacter.get(Character.valueOf(target.charAt(m)));
        int jSwap = maxSourceLetterMatchIndex;
        int deleteDistance = table[i - 1][m] + deleteCost;
        int insertDistance = table[i][m - 1] + insertCost;
        int matchDistance = table[i - 1][m - 1];
        if (source.charAt(i) != target.charAt(m)) {
          matchDistance += replaceCost;
        } else {
          maxSourceLetterMatchIndex = m;
        } 
        if (candidateSwapIndex != null && jSwap != -1) {
          int preSwapCost, iSwap = candidateSwapIndex.intValue();
          if (iSwap == 0 && jSwap == 0) {
            preSwapCost = 0;
          } else {
            preSwapCost = table[Math.max(0, iSwap - 1)][Math.max(0, jSwap - 1)];
          } 
          swapDistance = preSwapCost + (i - iSwap - 1) * deleteCost + (m - jSwap - 1) * insertCost + swapCost;
        } else {
          swapDistance = Integer.MAX_VALUE;
        } 
        table[i][m] = Math.min(Math.min(Math.min(deleteDistance, insertDistance), matchDistance), swapDistance);
      } 
      sourceIndexByCharacter.put(Character.valueOf(source.charAt(i)), Integer.valueOf(i));
    } 
    return table[source.length() - 1][target.length() - 1];
  }
}
