package org.eclipse.sisu.space.asm;

final class Handle {
  final int tag;
  
  final String owner;
  
  final String name;
  
  final String desc;
  
  public Handle(int tag, String owner, String name, String desc) {
    this.tag = tag;
    this.owner = owner;
    this.name = name;
    this.desc = desc;
  }
  
  public int getTag() {
    return this.tag;
  }
  
  public String getOwner() {
    return this.owner;
  }
  
  public String getName() {
    return this.name;
  }
  
  public String getDesc() {
    return this.desc;
  }
  
  public boolean equals(Object obj) {
    if (obj == this)
      return true; 
    if (!(obj instanceof Handle))
      return false; 
    Handle h = (Handle)obj;
    return (this.tag == h.tag && this.owner.equals(h.owner) && this.name.equals(h.name) && 
      this.desc.equals(h.desc));
  }
  
  public int hashCode() {
    return this.tag + this.owner.hashCode() * this.name.hashCode() * this.desc.hashCode();
  }
  
  public String toString() {
    return String.valueOf(this.owner) + '.' + this.name + this.desc + " (" + this.tag + ')';
  }
}
