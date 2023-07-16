package org.apache.maven.artifact.versioning;

import java.math.BigInteger;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Deque;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Properties;

public class ComparableVersion implements Comparable<ComparableVersion> {
  private static final int MAX_INTITEM_LENGTH = 9;
  
  private static final int MAX_LONGITEM_LENGTH = 18;
  
  private String value;
  
  private String canonical;
  
  private ListItem items;
  
  private static interface Item {
    public static final int INT_ITEM = 3;
    
    public static final int LONG_ITEM = 4;
    
    public static final int BIGINTEGER_ITEM = 0;
    
    public static final int STRING_ITEM = 1;
    
    public static final int LIST_ITEM = 2;
    
    int compareTo(Item param1Item);
    
    int getType();
    
    boolean isNull();
  }
  
  private static class IntItem implements Item {
    private final int value;
    
    public static final IntItem ZERO = new IntItem();
    
    private IntItem() {
      this.value = 0;
    }
    
    IntItem(String str) {
      this.value = Integer.parseInt(str);
    }
    
    public int getType() {
      return 3;
    }
    
    public boolean isNull() {
      return (this.value == 0);
    }
    
    public int compareTo(ComparableVersion.Item item) {
      int itemValue;
      if (item == null)
        return (this.value == 0) ? 0 : 1; 
      switch (item.getType()) {
        case 3:
          itemValue = ((IntItem)item).value;
          return (this.value < itemValue) ? -1 : ((this.value == itemValue) ? 0 : 1);
        case 0:
        case 4:
          return -1;
        case 1:
          return 1;
        case 2:
          return 1;
      } 
      throw new IllegalStateException("invalid item: " + item.getClass());
    }
    
    public boolean equals(Object o) {
      if (this == o)
        return true; 
      if (o == null || getClass() != o.getClass())
        return false; 
      IntItem intItem = (IntItem)o;
      return (this.value == intItem.value);
    }
    
    public int hashCode() {
      return this.value;
    }
    
    public String toString() {
      return Integer.toString(this.value);
    }
  }
  
  private static class LongItem implements Item {
    private final long value;
    
    LongItem(String str) {
      this.value = Long.parseLong(str);
    }
    
    public int getType() {
      return 4;
    }
    
    public boolean isNull() {
      return (this.value == 0L);
    }
    
    public int compareTo(ComparableVersion.Item item) {
      long itemValue;
      if (item == null)
        return (this.value == 0L) ? 0 : 1; 
      switch (item.getType()) {
        case 3:
          return 1;
        case 4:
          itemValue = ((LongItem)item).value;
          return (this.value < itemValue) ? -1 : ((this.value == itemValue) ? 0 : 1);
        case 0:
          return -1;
        case 1:
          return 1;
        case 2:
          return 1;
      } 
      throw new IllegalStateException("invalid item: " + item.getClass());
    }
    
    public boolean equals(Object o) {
      if (this == o)
        return true; 
      if (o == null || getClass() != o.getClass())
        return false; 
      LongItem longItem = (LongItem)o;
      return (this.value == longItem.value);
    }
    
    public int hashCode() {
      return (int)(this.value ^ this.value >>> 32L);
    }
    
    public String toString() {
      return Long.toString(this.value);
    }
  }
  
  private static class BigIntegerItem implements Item {
    private final BigInteger value;
    
    BigIntegerItem(String str) {
      this.value = new BigInteger(str);
    }
    
    public int getType() {
      return 0;
    }
    
    public boolean isNull() {
      return BigInteger.ZERO.equals(this.value);
    }
    
    public int compareTo(ComparableVersion.Item item) {
      if (item == null)
        return BigInteger.ZERO.equals(this.value) ? 0 : 1; 
      switch (item.getType()) {
        case 3:
        case 4:
          return 1;
        case 0:
          return this.value.compareTo(((BigIntegerItem)item).value);
        case 1:
          return 1;
        case 2:
          return 1;
      } 
      throw new IllegalStateException("invalid item: " + item.getClass());
    }
    
    public boolean equals(Object o) {
      if (this == o)
        return true; 
      if (o == null || getClass() != o.getClass())
        return false; 
      BigIntegerItem that = (BigIntegerItem)o;
      return this.value.equals(that.value);
    }
    
    public int hashCode() {
      return this.value.hashCode();
    }
    
    public String toString() {
      return this.value.toString();
    }
  }
  
  private static class StringItem implements Item {
    private static final List<String> QUALIFIERS = Arrays.asList(new String[] { "alpha", "beta", "milestone", "rc", "snapshot", "", "sp" });
    
    private static final Properties ALIASES = new Properties();
    
    static {
      ALIASES.put("ga", "");
      ALIASES.put("final", "");
      ALIASES.put("release", "");
      ALIASES.put("cr", "rc");
    }
    
    private static final String RELEASE_VERSION_INDEX = String.valueOf(QUALIFIERS.indexOf(""));
    
    private final String value;
    
    StringItem(String value, boolean followedByDigit) {
      if (followedByDigit && value.length() == 1)
        switch (value.charAt(0)) {
          case 'a':
            value = "alpha";
            break;
          case 'b':
            value = "beta";
            break;
          case 'm':
            value = "milestone";
            break;
        }  
      this.value = ALIASES.getProperty(value, value);
    }
    
    public int getType() {
      return 1;
    }
    
    public boolean isNull() {
      return (comparableQualifier(this.value).compareTo(RELEASE_VERSION_INDEX) == 0);
    }
    
    public static String comparableQualifier(String qualifier) {
      int i = QUALIFIERS.indexOf(qualifier);
      return (i == -1) ? (QUALIFIERS.size() + "-" + qualifier) : String.valueOf(i);
    }
    
    public int compareTo(ComparableVersion.Item item) {
      if (item == null)
        return comparableQualifier(this.value).compareTo(RELEASE_VERSION_INDEX); 
      switch (item.getType()) {
        case 0:
        case 3:
        case 4:
          return -1;
        case 1:
          return comparableQualifier(this.value).compareTo(comparableQualifier(((StringItem)item).value));
        case 2:
          return -1;
      } 
      throw new IllegalStateException("invalid item: " + item.getClass());
    }
    
    public boolean equals(Object o) {
      if (this == o)
        return true; 
      if (o == null || getClass() != o.getClass())
        return false; 
      StringItem that = (StringItem)o;
      return this.value.equals(that.value);
    }
    
    public int hashCode() {
      return this.value.hashCode();
    }
    
    public String toString() {
      return this.value;
    }
  }
  
  private static class ListItem extends ArrayList<Item> implements Item {
    private ListItem() {}
    
    public int getType() {
      return 2;
    }
    
    public boolean isNull() {
      return (size() == 0);
    }
    
    void normalize() {
      for (int i = size() - 1; i >= 0; i--) {
        ComparableVersion.Item lastItem = get(i);
        if (lastItem.isNull()) {
          remove(i);
        } else if (!(lastItem instanceof ListItem)) {
          break;
        } 
      } 
    }
    
    public int compareTo(ComparableVersion.Item item) {
      Iterator<ComparableVersion.Item> left;
      Iterator<ComparableVersion.Item> right;
      if (item == null) {
        if (size() == 0)
          return 0; 
        for (ComparableVersion.Item i : this) {
          int result = i.compareTo(null);
          if (result != 0)
            return result; 
        } 
        return 0;
      } 
      switch (item.getType()) {
        case 0:
        case 3:
        case 4:
          return -1;
        case 1:
          return 1;
        case 2:
          left = iterator();
          right = ((ListItem)item).iterator();
          while (left.hasNext() || right.hasNext()) {
            ComparableVersion.Item l = left.hasNext() ? left.next() : null;
            ComparableVersion.Item r = right.hasNext() ? right.next() : null;
            int result = (l == null) ? ((r == null) ? 0 : (-1 * r.compareTo(l))) : l.compareTo(r);
            if (result != 0)
              return result; 
          } 
          return 0;
      } 
      throw new IllegalStateException("invalid item: " + item.getClass());
    }
    
    public String toString() {
      StringBuilder buffer = new StringBuilder();
      for (ComparableVersion.Item item : this) {
        if (buffer.length() > 0)
          buffer.append((item instanceof ListItem) ? 45 : 46); 
        buffer.append(item);
      } 
      return buffer.toString();
    }
    
    private String toListString() {
      StringBuilder buffer = new StringBuilder();
      buffer.append("[");
      for (ComparableVersion.Item item : this) {
        if (buffer.length() > 1)
          buffer.append(", "); 
        if (item instanceof ListItem) {
          buffer.append(((ListItem)item).toListString());
          continue;
        } 
        buffer.append(item);
      } 
      buffer.append("]");
      return buffer.toString();
    }
  }
  
  public ComparableVersion(String version) {
    parseVersion(version);
  }
  
  public final void parseVersion(String version) {
    this.value = version;
    this.items = new ListItem();
    version = version.toLowerCase(Locale.ENGLISH);
    ListItem list = this.items;
    Deque<Item> stack = new ArrayDeque<>();
    stack.push(list);
    boolean isDigit = false;
    int startIndex = 0;
    for (int i = 0; i < version.length(); i++) {
      char c = version.charAt(i);
      if (c == '.') {
        if (i == startIndex) {
          list.add(IntItem.ZERO);
        } else {
          list.add(parseItem(isDigit, version.substring(startIndex, i)));
        } 
        startIndex = i + 1;
      } else if (c == '-') {
        if (i == startIndex) {
          list.add(IntItem.ZERO);
        } else {
          list.add(parseItem(isDigit, version.substring(startIndex, i)));
        } 
        startIndex = i + 1;
        list.add(list = new ListItem());
        stack.push(list);
      } else if (Character.isDigit(c)) {
        if (!isDigit && i > startIndex) {
          list.add(new StringItem(version.substring(startIndex, i), true));
          startIndex = i;
          list.add(list = new ListItem());
          stack.push(list);
        } 
        isDigit = true;
      } else {
        if (isDigit && i > startIndex) {
          list.add(parseItem(true, version.substring(startIndex, i)));
          startIndex = i;
          list.add(list = new ListItem());
          stack.push(list);
        } 
        isDigit = false;
      } 
    } 
    if (version.length() > startIndex)
      list.add(parseItem(isDigit, version.substring(startIndex))); 
    while (!stack.isEmpty()) {
      list = (ListItem)stack.pop();
      list.normalize();
    } 
  }
  
  private static Item parseItem(boolean isDigit, String buf) {
    if (isDigit) {
      buf = stripLeadingZeroes(buf);
      if (buf.length() <= 9)
        return new IntItem(buf); 
      if (buf.length() <= 18)
        return new LongItem(buf); 
      return new BigIntegerItem(buf);
    } 
    return new StringItem(buf, false);
  }
  
  private static String stripLeadingZeroes(String buf) {
    if (buf == null || buf.isEmpty())
      return "0"; 
    for (int i = 0; i < buf.length(); i++) {
      char c = buf.charAt(i);
      if (c != '0')
        return buf.substring(i); 
    } 
    return buf;
  }
  
  public int compareTo(ComparableVersion o) {
    return this.items.compareTo(o.items);
  }
  
  public String toString() {
    return this.value;
  }
  
  public String getCanonical() {
    if (this.canonical == null)
      this.canonical = this.items.toString(); 
    return this.canonical;
  }
  
  public boolean equals(Object o) {
    return (o instanceof ComparableVersion && this.items.equals(((ComparableVersion)o).items));
  }
  
  public int hashCode() {
    return this.items.hashCode();
  }
  
  public static void main(String... args) {
    System.out.println("Display parameters as parsed by Maven (in canonical form and as a list of tokens) and comparison result:");
    if (args.length == 0)
      return; 
    ComparableVersion prev = null;
    int i = 1;
    for (String version : args) {
      ComparableVersion c = new ComparableVersion(version);
      if (prev != null) {
        int compare = prev.compareTo(c);
        System.out.println("   " + prev.toString() + ' ' + ((compare == 0) ? "==" : ((compare < 0) ? "<" : ">")) + ' ' + version);
      } 
      System.out.println(i++ + ". " + version + " -> " + c.getCanonical() + "; tokens: " + c.items.toListString());
      prev = c;
    } 
  }
}
