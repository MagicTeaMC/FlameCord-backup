package org.eclipse.aether.util.version;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;
import org.eclipse.aether.version.Version;

final class GenericVersion implements Version {
  private final String version;
  
  private final Item[] items;
  
  private final int hash;
  
  GenericVersion(String version) {
    this.version = version;
    this.items = parse(version);
    this.hash = Arrays.hashCode((Object[])this.items);
  }
  
  private static Item[] parse(String version) {
    List<Item> items = new ArrayList<>();
    for (Tokenizer tokenizer = new Tokenizer(version); tokenizer.next(); ) {
      Item item = tokenizer.toItem();
      items.add(item);
    } 
    trimPadding(items);
    return items.<Item>toArray(new Item[items.size()]);
  }
  
  private static void trimPadding(List<Item> items) {
    Boolean number = null;
    int end = items.size() - 1;
    for (int i = end; i > 0; i--) {
      Item item = items.get(i);
      if (!Boolean.valueOf(item.isNumber()).equals(number)) {
        end = i;
        number = Boolean.valueOf(item.isNumber());
      } 
      if (end == i && (i == items.size() - 1 || ((Item)items.get(i - 1)).isNumber() == item.isNumber()) && item
        .compareTo(null) == 0) {
        items.remove(i);
        end--;
      } 
    } 
  }
  
  public int compareTo(Version obj) {
    Item[] these = this.items;
    Item[] those = ((GenericVersion)obj).items;
    boolean number = true;
    for (int index = 0;; index++) {
      if (index >= these.length && index >= those.length)
        return 0; 
      if (index >= these.length)
        return -comparePadding(those, index, null); 
      if (index >= those.length)
        return comparePadding(these, index, null); 
      Item thisItem = these[index];
      Item thatItem = those[index];
      if (thisItem.isNumber() != thatItem.isNumber()) {
        if (number == thisItem.isNumber())
          return comparePadding(these, index, Boolean.valueOf(number)); 
        return -comparePadding(those, index, Boolean.valueOf(number));
      } 
      int rel = thisItem.compareTo(thatItem);
      if (rel != 0)
        return rel; 
      number = thisItem.isNumber();
    } 
  }
  
  private static int comparePadding(Item[] items, int index, Boolean number) {
    int rel = 0;
    for (int i = index; i < items.length; i++) {
      Item item = items[i];
      if (number != null && number.booleanValue() != item.isNumber())
        break; 
      rel = item.compareTo(null);
      if (rel != 0)
        break; 
    } 
    return rel;
  }
  
  public boolean equals(Object obj) {
    return (obj instanceof GenericVersion && compareTo((GenericVersion)obj) == 0);
  }
  
  public int hashCode() {
    return this.hash;
  }
  
  public String toString() {
    return this.version;
  }
  
  static final class Tokenizer {
    private static final Integer QUALIFIER_ALPHA = Integer.valueOf(-5);
    
    private static final Integer QUALIFIER_BETA = Integer.valueOf(-4);
    
    private static final Integer QUALIFIER_MILESTONE = Integer.valueOf(-3);
    
    private static final Map<String, Integer> QUALIFIERS = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
    
    private final String version;
    
    private int index;
    
    private String token;
    
    private boolean number;
    
    private boolean terminatedByNumber;
    
    static {
      QUALIFIERS.put("alpha", QUALIFIER_ALPHA);
      QUALIFIERS.put("beta", QUALIFIER_BETA);
      QUALIFIERS.put("milestone", QUALIFIER_MILESTONE);
      QUALIFIERS.put("cr", Integer.valueOf(-2));
      QUALIFIERS.put("rc", Integer.valueOf(-2));
      QUALIFIERS.put("snapshot", Integer.valueOf(-1));
      QUALIFIERS.put("ga", Integer.valueOf(0));
      QUALIFIERS.put("final", Integer.valueOf(0));
      QUALIFIERS.put("release", Integer.valueOf(0));
      QUALIFIERS.put("", Integer.valueOf(0));
      QUALIFIERS.put("sp", Integer.valueOf(1));
    }
    
    Tokenizer(String version) {
      this.version = (version.length() > 0) ? version : "0";
    }
    
    public boolean next() {
      int n = this.version.length();
      if (this.index >= n)
        return false; 
      int state = -2;
      int start = this.index;
      int end = n;
      this.terminatedByNumber = false;
      for (; this.index < n; this.index++) {
        char c = this.version.charAt(this.index);
        if (c == '.' || c == '-' || c == '_') {
          end = this.index;
          this.index++;
          break;
        } 
        int digit = Character.digit(c, 10);
        if (digit >= 0) {
          if (state == -1) {
            end = this.index;
            this.terminatedByNumber = true;
            break;
          } 
          if (state == 0)
            start++; 
          state = (state > 0 || digit > 0) ? 1 : 0;
        } else {
          if (state >= 0) {
            end = this.index;
            break;
          } 
          state = -1;
        } 
      } 
      if (end - start > 0) {
        this.token = this.version.substring(start, end);
        this.number = (state >= 0);
      } else {
        this.token = "0";
        this.number = true;
      } 
      return true;
    }
    
    public String toString() {
      return String.valueOf(this.token);
    }
    
    public GenericVersion.Item toItem() {
      if (this.number)
        try {
          if (this.token.length() < 10)
            return new GenericVersion.Item(4, Integer.valueOf(Integer.parseInt(this.token))); 
          return new GenericVersion.Item(5, new BigInteger(this.token));
        } catch (NumberFormatException e) {
          throw new IllegalStateException(e);
        }  
      if (this.index >= this.version.length()) {
        if ("min".equalsIgnoreCase(this.token))
          return GenericVersion.Item.MIN; 
        if ("max".equalsIgnoreCase(this.token))
          return GenericVersion.Item.MAX; 
      } 
      if (this.terminatedByNumber && this.token.length() == 1)
        switch (this.token.charAt(0)) {
          case 'A':
          case 'a':
            return new GenericVersion.Item(2, QUALIFIER_ALPHA);
          case 'B':
          case 'b':
            return new GenericVersion.Item(2, QUALIFIER_BETA);
          case 'M':
          case 'm':
            return new GenericVersion.Item(2, QUALIFIER_MILESTONE);
        }  
      Integer qualifier = QUALIFIERS.get(this.token);
      if (qualifier != null)
        return new GenericVersion.Item(2, qualifier); 
      return new GenericVersion.Item(3, this.token.toLowerCase(Locale.ENGLISH));
    }
  }
  
  static final class Item {
    static final int KIND_MAX = 8;
    
    static final int KIND_BIGINT = 5;
    
    static final int KIND_INT = 4;
    
    static final int KIND_STRING = 3;
    
    static final int KIND_QUALIFIER = 2;
    
    static final int KIND_MIN = 0;
    
    static final Item MAX = new Item(8, "max");
    
    static final Item MIN = new Item(0, "min");
    
    private final int kind;
    
    private final Object value;
    
    Item(int kind, Object value) {
      this.kind = kind;
      this.value = value;
    }
    
    public boolean isNumber() {
      return ((this.kind & 0x2) == 0);
    }
    
    public int compareTo(Item that) {
      if (that == null) {
        int i;
        switch (this.kind) {
          case 0:
            i = -1;
          case 3:
          case 5:
          case 8:
            i = 1;
          case 2:
          case 4:
            i = ((Integer)this.value).intValue();
        } 
        throw new IllegalStateException("unknown version item kind " + this.kind);
      } 
      int rel = this.kind - that.kind;
      if (rel == 0) {
        switch (this.kind) {
          case 0:
          case 8:
            return rel;
          case 5:
            rel = ((BigInteger)this.value).compareTo((BigInteger)that.value);
          case 2:
          case 4:
            rel = ((Integer)this.value).compareTo((Integer)that.value);
          case 3:
            rel = ((String)this.value).compareToIgnoreCase((String)that.value);
        } 
        throw new IllegalStateException("unknown version item kind " + this.kind);
      } 
    }
    
    public boolean equals(Object obj) {
      return (obj instanceof Item && compareTo((Item)obj) == 0);
    }
    
    public int hashCode() {
      return this.value.hashCode() + this.kind * 31;
    }
    
    public String toString() {
      return String.valueOf(this.value);
    }
  }
}
