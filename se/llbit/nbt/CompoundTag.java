package se.llbit.nbt;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class CompoundTag extends SpecificTag implements Iterable<NamedTag> {
  final List<NamedTag> items;
  
  public void add(String name, SpecificTag tag) {
    add(new NamedTag(name, tag));
  }
  
  public static SpecificTag read(DataInputStream in) {
    CompoundTag tagThis = new CompoundTag();
    while (true) {
      Tag last = NamedTag.read(in);
      if (last.isEnd())
        break; 
      tagThis.add((NamedTag)last);
    } 
    return tagThis;
  }
  
  public void write(DataOutputStream out) throws IOException {
    for (Tag item : this.items)
      item.write(out); 
    out.writeByte(0);
  }
  
  static Map<String, Tag> partialParse(DataInputStream in, String prefix, Map<String, Tag> result, Set<String> request, Set<String> prefixes) {
    try {
      while (true) {
        byte type = in.readByte();
        if (type == 0)
          break; 
        SpecificTag name = StringTag.read(in);
        String tag = prefix + "." + name.stringValue();
        boolean parsed = NamedTag.partiallyParseTag(in, result, request, prefixes, type, tag);
        if (parsed) {
          if (request.isEmpty())
            return result; 
          continue;
        } 
        SpecificTag.skip(type, in);
      } 
    } catch (IOException iOException) {}
    return result;
  }
  
  static void skip(DataInputStream in) {
    try {
      while (true) {
        byte itemType = in.readByte();
        if (itemType == 0)
          break; 
        StringTag.skip(in);
        SpecificTag.skip(itemType, in);
      } 
    } catch (IOException iOException) {}
  }
  
  public CompoundTag() {
    this.items = new ArrayList<>();
  }
  
  public void printTag(StringBuilder buff, String indent) {
    buff.append(indent);
    printTagInfo(buff);
    for (NamedTag tag : this.items) {
      buff.append(String.format("%s  %s:\n", new Object[] { indent, tag.name() }));
      tag.tag.printTag(buff, indent + "    ");
    } 
  }
  
  public CompoundTag(List<? extends NamedTag> items) {
    this.items = new ArrayList<>(items);
  }
  
  public int size() {
    return this.items.size();
  }
  
  public void add(NamedTag node) {
    this.items.add(node);
  }
  
  public String toString() {
    return dumpTree();
  }
  
  public String type() {
    return "TAG_Compound";
  }
  
  public String tagName() {
    return "TAG_Compound";
  }
  
  public int tagType() {
    return 10;
  }
  
  public boolean isCompoundTag() {
    return true;
  }
  
  public Tag get(String name) {
    for (Tag item : this.items) {
      if (item.isNamed(name))
        return item.unpack(); 
    } 
    return new ErrorTag("No item named \"" + name + "\" in this compound tag.");
  }
  
  public void set(String name, Tag tag) {
    throw new Error();
  }
  
  public Iterator<NamedTag> iterator() {
    return this.items.iterator();
  }
  
  public CompoundTag asCompound() {
    return this;
  }
  
  public boolean isEmpty() {
    return this.items.isEmpty();
  }
  
  public boolean equals(Object obj) {
    if (obj == this)
      return true; 
    if (!(obj instanceof CompoundTag))
      return false; 
    CompoundTag other = (CompoundTag)obj;
    for (NamedTag tag : this.items) {
      if (!other.get(tag.name()).equals(tag.tag))
        return false; 
    } 
    for (NamedTag tag : other.items) {
      if (!get(tag.name()).equals(tag.tag))
        return false; 
    } 
    return true;
  }
  
  public int hashCode() {
    int code = 0;
    for (NamedTag tag : this.items)
      code ^= tag.hashCode(); 
    return code;
  }
}
