package org.eclipse.sisu.osgi;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.regex.Pattern;
import org.eclipse.sisu.inject.BindingPublisher;
import org.eclipse.sisu.inject.BindingSubscriber;
import org.eclipse.sisu.inject.Logs;
import org.osgi.framework.BundleContext;

public final class ServiceBindings implements BindingPublisher {
  private static final Pattern GLOB_SYNTAX = Pattern.compile("(?:\\w+|\\*)(?:\\.?(?:\\w+|\\*))*");
  
  private final ConcurrentMap<String, BindingTracker<?>> trackers = new ConcurrentHashMap<String, BindingTracker<?>>();
  
  private final BundleContext context;
  
  private final Pattern[] allowed;
  
  private final Pattern[] ignored;
  
  private final int maxRank;
  
  public ServiceBindings(BundleContext context, String allow, String ignore, int maxRank) {
    this.context = context;
    this.maxRank = maxRank;
    this.allowed = parseGlobs(allow);
    this.ignored = parseGlobs(ignore);
  }
  
  public ServiceBindings(BundleContext context) {
    this(context, defaultAllow(), defaultIgnore(), -2147483648);
  }
  
  public static String defaultAllow() {
    return System.getProperty(String.valueOf(ServiceBindings.class.getName()) + ".allow", "");
  }
  
  public static String defaultIgnore() {
    return System.getProperty(String.valueOf(ServiceBindings.class.getName()) + ".ignore", "");
  }
  
  public <T> void subscribe(BindingSubscriber<T> subscriber) {
    String clazzName = subscriber.type().getRawType().getName();
    if (shouldTrack(clazzName)) {
      BindingTracker<?> tracker = this.trackers.get(clazzName);
      if (tracker == null) {
        tracker = new BindingTracker(this.context, this.maxRank, clazzName);
        BindingTracker<?> oldTracker = this.trackers.putIfAbsent(clazzName, tracker);
        if (oldTracker != null)
          tracker = oldTracker; 
      } 
      tracker.subscribe(subscriber);
    } 
  }
  
  public <T> void unsubscribe(BindingSubscriber<T> subscriber) {
    String clazzName = subscriber.type().getRawType().getName();
    BindingTracker<T> tracker = (BindingTracker)this.trackers.get(clazzName);
    if (tracker != null)
      tracker.unsubscribe(subscriber); 
  }
  
  public int maxBindingRank() {
    return this.maxRank;
  }
  
  private boolean shouldTrack(String clazzName) {
    byte b;
    int i;
    Pattern[] arrayOfPattern;
    for (i = (arrayOfPattern = this.allowed).length, b = 0; b < i; ) {
      Pattern allow = arrayOfPattern[b];
      if (allow.matcher(clazzName).matches()) {
        byte b1;
        int j;
        Pattern[] arrayOfPattern1;
        for (j = (arrayOfPattern1 = this.ignored).length, b1 = 0; b1 < j; ) {
          Pattern ignore = arrayOfPattern1[b1];
          if (ignore.matcher(clazzName).matches())
            return false; 
          b1++;
        } 
        return true;
      } 
      b++;
    } 
    return false;
  }
  
  private static Pattern[] parseGlobs(String globs) {
    List<Pattern> patterns = new ArrayList<Pattern>();
    byte b;
    int i;
    String[] arrayOfString;
    for (i = (arrayOfString = globs.split("\\s*,\\s*")).length, b = 0; b < i; ) {
      String glob = arrayOfString[b];
      if (GLOB_SYNTAX.matcher(glob).matches()) {
        patterns.add(Pattern.compile(glob.replace(".", "\\.").replace("*", ".*")));
      } else if (glob.length() > 0) {
        Logs.warn("Ignoring malformed glob pattern: {}", glob, null);
      } 
      b++;
    } 
    return patterns.<Pattern>toArray(new Pattern[patterns.size()]);
  }
}
