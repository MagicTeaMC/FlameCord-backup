package se.llbit.nbt;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ListTag extends SpecificTag implements Iterable<SpecificTag> {
  public final int type;
  
  public final List<SpecificTag> items;
  
  public static SpecificTag read(DataInputStream in) {
    try {
      byte itemType = in.readByte();
      int numItems = in.readInt();
      if (itemType == 0 && numItems > 0)
        return new ErrorTag("Cannot create list of TAG_End"); 
      ListTag tagThis = new ListTag(itemType, Collections.emptyList());
      for (int i = 0; i < numItems; i++) {
        SpecificTag last = SpecificTag.read(itemType, in);
        tagThis.add(last);
      } 
      return tagThis;
    } catch (IOException e) {
      return new ErrorTag("IOException while reading TAG_List:\n" + e.getMessage());
    } 
  }
  
  public void write(DataOutputStream out) throws IOException {
    out.writeByte(getType());
    out.writeInt(size());
    for (SpecificTag item : this.items)
      item.write(out); 
  }
  
  static Map<String, Tag> partialParse(DataInputStream in, String prefix, Map<String, Tag> result, Set<String> request, Set<String> prefixes) {
    try {
      byte itemType = in.readByte();
      int numItems = in.readInt();
      if (itemType == 0 && numItems > 0)
        return result; 
      for (int i = 0; i < numItems; i++) {
        String tag = prefix + "." + i;
        boolean parsed = NamedTag.partiallyParseTag(in, result, request, prefixes, itemType, tag);
        if (parsed) {
          if (request.isEmpty())
            return result; 
        } else {
          SpecificTag.skip(itemType, in);
        } 
      } 
    } catch (IOException iOException) {}
    return result;
  }
  
  static void skip(DataInputStream in) {
    try {
      byte itemType = in.readByte();
      int numItems = in.readInt();
      if (itemType == 0)
        return; 
      for (int i = 0; i < numItems; i++)
        SpecificTag.skip(itemType, in); 
    } catch (IOException iOException) {}
  }
  
  public ListTag(int type, List<? extends SpecificTag> items) {
    this.type = type;
    this.items = new ArrayList<>(items);
  }
  
  public int getType() {
    return this.type;
  }
  
  public int size() {
    return this.items.size();
  }
  
  public void add(SpecificTag node) {
    this.items.add(node);
  }
  
  public void set(int i, SpecificTag node) {
    this.items.set(i, node);
  }
  
  public String toString() {
    return dumpTree();
  }
  
  public String type() {
    return "TAG_List";
  }
  
  public void printTag(StringBuilder buff, String indent) {
    buff.append(indent);
    printTagInfo(buff);
    for (Tag item : this.items)
      item.printTag(buff, indent + "  "); 
  }
  
  public String tagName() {
    return "TAG_List";
  }
  
  public int tagType() {
    return 9;
  }
  
  public boolean isList() {
    return true;
  }
  
  public Tag get(int i) {
    return this.items.get(i);
  }
  
  public Iterator<SpecificTag> iterator() {
    return this.items.iterator();
  }
  
  public ListTag asList() {
    return this;
  }
  
  public boolean isEmpty() {
    return this.items.isEmpty();
  }
  
  public boolean equals(Object obj) {
    if (obj == this)
      return true; 
    if (!(obj instanceof ListTag))
      return false; 
    return this.items.equals(((ListTag)obj).items);
  }
  
  public int hashCode() {
    int code = 0;
    for (SpecificTag tag : this.items) {
      code *= 31;
      code ^= tag.hashCode();
    } 
    return code;
  }
}
