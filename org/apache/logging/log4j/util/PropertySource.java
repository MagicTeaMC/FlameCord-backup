package org.apache.logging.log4j.util;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public interface PropertySource {
  int getPriority();
  
  default void forEach(BiConsumer<String, String> action) {}
  
  default Collection<String> getPropertyNames() {
    return Collections.emptySet();
  }
  
  default CharSequence getNormalForm(Iterable<? extends CharSequence> tokens) {
    return null;
  }
  
  default String getProperty(String key) {
    return null;
  }
  
  default boolean containsProperty(String key) {
    return false;
  }
  
  public static class Comparator implements java.util.Comparator<PropertySource>, Serializable {
    private static final long serialVersionUID = 1L;
    
    public int compare(PropertySource o1, PropertySource o2) {
      return Integer.compare(((PropertySource)Objects.<PropertySource>requireNonNull(o1)).getPriority(), ((PropertySource)Objects.<PropertySource>requireNonNull(o2)).getPriority());
    }
  }
  
  public static final class Util {
    private static final Pattern PREFIX_PATTERN = Pattern.compile("(^log4j2?[-._/]?|^org\\.apache\\.logging\\.log4j\\.)|(?=AsyncLogger(Config)?\\.)", 2);
    
    private static final Pattern PROPERTY_TOKENIZER = Pattern.compile("([A-Z]*[a-z0-9]+|[A-Z0-9]+)[-._/]?");
    
    private static final Map<CharSequence, List<CharSequence>> CACHE = new ConcurrentHashMap<>();
    
    static {
      CACHE.put("disableThreadContext", Arrays.asList(new CharSequence[] { "disable", "thread", "context" }));
      CACHE.put("disableThreadContextStack", Arrays.asList(new CharSequence[] { "disable", "thread", "context", "stack" }));
      CACHE.put("disableThreadContextMap", Arrays.asList(new CharSequence[] { "disable", "thread", "context", "map" }));
      CACHE.put("isThreadContextMapInheritable", Arrays.asList(new CharSequence[] { "is", "thread", "context", "map", "inheritable" }));
    }
    
    public static List<CharSequence> tokenize(CharSequence value) {
      if (CACHE.containsKey(value))
        return CACHE.get(value); 
      List<CharSequence> tokens = new ArrayList<>();
      int start = 0;
      Matcher prefixMatcher = PREFIX_PATTERN.matcher(value);
      if (prefixMatcher.find(start)) {
        start = prefixMatcher.end();
        Matcher matcher = PROPERTY_TOKENIZER.matcher(value);
        while (matcher.find(start)) {
          tokens.add(matcher.group(1).toLowerCase());
          start = matcher.end();
        } 
      } 
      CACHE.put(value, tokens);
      return tokens;
    }
    
    public static CharSequence joinAsCamelCase(Iterable<? extends CharSequence> tokens) {
      StringBuilder sb = new StringBuilder();
      boolean first = true;
      for (CharSequence token : tokens) {
        if (first) {
          sb.append(token);
        } else {
          sb.append(Character.toUpperCase(token.charAt(0)));
          if (token.length() > 1)
            sb.append(token.subSequence(1, token.length())); 
        } 
        first = false;
      } 
      return sb.toString();
    }
  }
}
