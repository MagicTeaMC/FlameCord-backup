package net.md_5.bungee.protocol;

public class Location {
  private final String dimension;
  
  private final long pos;
  
  public Location(String dimension, long pos) {
    this.dimension = dimension;
    this.pos = pos;
  }
  
  public boolean equals(Object o) {
    if (o == this)
      return true; 
    if (!(o instanceof Location))
      return false; 
    Location other = (Location)o;
    if (!other.canEqual(this))
      return false; 
    if (getPos() != other.getPos())
      return false; 
    Object this$dimension = getDimension(), other$dimension = other.getDimension();
    return !((this$dimension == null) ? (other$dimension != null) : !this$dimension.equals(other$dimension));
  }
  
  protected boolean canEqual(Object other) {
    return other instanceof Location;
  }
  
  public int hashCode() {
    int PRIME = 59;
    result = 1;
    long $pos = getPos();
    result = result * 59 + (int)($pos >>> 32L ^ $pos);
    Object $dimension = getDimension();
    return result * 59 + (($dimension == null) ? 43 : $dimension.hashCode());
  }
  
  public String toString() {
    return "Location(dimension=" + getDimension() + ", pos=" + getPos() + ")";
  }
  
  public String getDimension() {
    return this.dimension;
  }
  
  public long getPos() {
    return this.pos;
  }
}
