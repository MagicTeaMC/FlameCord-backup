package net.md_5.bungee.api.score;

public class Score {
  private final String itemName;
  
  private final String scoreName;
  
  private final int value;
  
  public Score(String itemName, String scoreName, int value) {
    this.itemName = itemName;
    this.scoreName = scoreName;
    this.value = value;
  }
  
  public boolean equals(Object o) {
    if (o == this)
      return true; 
    if (!(o instanceof Score))
      return false; 
    Score other = (Score)o;
    if (!other.canEqual(this))
      return false; 
    if (getValue() != other.getValue())
      return false; 
    Object this$itemName = getItemName(), other$itemName = other.getItemName();
    if ((this$itemName == null) ? (other$itemName != null) : !this$itemName.equals(other$itemName))
      return false; 
    Object this$scoreName = getScoreName(), other$scoreName = other.getScoreName();
    return !((this$scoreName == null) ? (other$scoreName != null) : !this$scoreName.equals(other$scoreName));
  }
  
  protected boolean canEqual(Object other) {
    return other instanceof Score;
  }
  
  public int hashCode() {
    int PRIME = 59;
    result = 1;
    result = result * 59 + getValue();
    Object $itemName = getItemName();
    result = result * 59 + (($itemName == null) ? 43 : $itemName.hashCode());
    Object $scoreName = getScoreName();
    return result * 59 + (($scoreName == null) ? 43 : $scoreName.hashCode());
  }
  
  public String toString() {
    return "Score(itemName=" + getItemName() + ", scoreName=" + getScoreName() + ", value=" + getValue() + ")";
  }
  
  public String getItemName() {
    return this.itemName;
  }
  
  public String getScoreName() {
    return this.scoreName;
  }
  
  public int getValue() {
    return this.value;
  }
}
