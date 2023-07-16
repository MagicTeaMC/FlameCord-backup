package io.netty.util;

import io.netty.util.internal.EmptyArrays;
import io.netty.util.internal.ObjectUtil;
import io.netty.util.internal.PlatformDependent;
import io.netty.util.internal.StringUtil;
import io.netty.util.internal.SystemPropertyUtil;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicIntegerFieldUpdater;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.atomic.AtomicReferenceFieldUpdater;

public class ResourceLeakDetector<T> {
  private static final String PROP_LEVEL_OLD = "io.netty.leakDetectionLevel";
  
  private static final String PROP_LEVEL = "io.netty.leakDetection.level";
  
  static {
    boolean disabled;
  }
  
  private static final Level DEFAULT_LEVEL = Level.SIMPLE;
  
  private static final String PROP_TARGET_RECORDS = "io.netty.leakDetection.targetRecords";
  
  private static final int DEFAULT_TARGET_RECORDS = 4;
  
  private static final String PROP_SAMPLING_INTERVAL = "io.netty.leakDetection.samplingInterval";
  
  private static final int DEFAULT_SAMPLING_INTERVAL = 128;
  
  private static final int TARGET_RECORDS;
  
  static final int SAMPLING_INTERVAL;
  
  private static Level level;
  
  public enum Level {
    DISABLED, SIMPLE, ADVANCED, PARANOID;
    
    static Level parseLevel(String levelStr) {
      String trimmedLevelStr = levelStr.trim();
      for (Level l : values()) {
        if (trimmedLevelStr.equalsIgnoreCase(l.name()) || trimmedLevelStr.equals(String.valueOf(l.ordinal())))
          return l; 
      } 
      return ResourceLeakDetector.DEFAULT_LEVEL;
    }
  }
  
  private static final InternalLogger logger = InternalLoggerFactory.getInstance(ResourceLeakDetector.class);
  
  static {
    if (SystemPropertyUtil.get("io.netty.noResourceLeakDetection") != null) {
      disabled = SystemPropertyUtil.getBoolean("io.netty.noResourceLeakDetection", false);
      logger.debug("-Dio.netty.noResourceLeakDetection: {}", Boolean.valueOf(disabled));
      logger.warn("-Dio.netty.noResourceLeakDetection is deprecated. Use '-D{}={}' instead.", "io.netty.leakDetection.level", Level.DISABLED
          
          .name().toLowerCase());
    } else {
      disabled = false;
    } 
    Level defaultLevel = disabled ? Level.DISABLED : DEFAULT_LEVEL;
    String levelStr = SystemPropertyUtil.get("io.netty.leakDetectionLevel", defaultLevel.name());
    levelStr = SystemPropertyUtil.get("io.netty.leakDetection.level", levelStr);
    Level level = Level.parseLevel(levelStr);
    TARGET_RECORDS = SystemPropertyUtil.getInt("io.netty.leakDetection.targetRecords", 4);
    SAMPLING_INTERVAL = SystemPropertyUtil.getInt("io.netty.leakDetection.samplingInterval", 128);
    ResourceLeakDetector.level = level;
    if (logger.isDebugEnabled()) {
      logger.debug("-D{}: {}", "io.netty.leakDetection.level", level.name().toLowerCase());
      logger.debug("-D{}: {}", "io.netty.leakDetection.targetRecords", Integer.valueOf(TARGET_RECORDS));
    } 
    excludedMethods = (AtomicReference)new AtomicReference<String>(EmptyArrays.EMPTY_STRINGS);
  }
  
  @Deprecated
  public static void setEnabled(boolean enabled) {
    setLevel(enabled ? Level.SIMPLE : Level.DISABLED);
  }
  
  public static boolean isEnabled() {
    return (getLevel().ordinal() > Level.DISABLED.ordinal());
  }
  
  public static void setLevel(Level level) {
    ResourceLeakDetector.level = (Level)ObjectUtil.checkNotNull(level, "level");
  }
  
  public static Level getLevel() {
    return level;
  }
  
  private final Set<DefaultResourceLeak<?>> allLeaks = Collections.newSetFromMap(new ConcurrentHashMap<DefaultResourceLeak<?>, Boolean>());
  
  private final ReferenceQueue<Object> refQueue = new ReferenceQueue();
  
  private final Set<String> reportedLeaks = Collections.newSetFromMap(new ConcurrentHashMap<String, Boolean>());
  
  private final String resourceType;
  
  private final int samplingInterval;
  
  private static final AtomicReference<String[]> excludedMethods;
  
  @Deprecated
  public ResourceLeakDetector(Class<?> resourceType) {
    this(StringUtil.simpleClassName(resourceType));
  }
  
  @Deprecated
  public ResourceLeakDetector(String resourceType) {
    this(resourceType, 128, Long.MAX_VALUE);
  }
  
  @Deprecated
  public ResourceLeakDetector(Class<?> resourceType, int samplingInterval, long maxActive) {
    this(resourceType, samplingInterval);
  }
  
  public ResourceLeakDetector(Class<?> resourceType, int samplingInterval) {
    this(StringUtil.simpleClassName(resourceType), samplingInterval, Long.MAX_VALUE);
  }
  
  @Deprecated
  public ResourceLeakDetector(String resourceType, int samplingInterval, long maxActive) {
    this.resourceType = (String)ObjectUtil.checkNotNull(resourceType, "resourceType");
    this.samplingInterval = samplingInterval;
  }
  
  @Deprecated
  public final ResourceLeak open(T obj) {
    return track0(obj, false);
  }
  
  public final ResourceLeakTracker<T> track(T obj) {
    return track0(obj, false);
  }
  
  public ResourceLeakTracker<T> trackForcibly(T obj) {
    return track0(obj, true);
  }
  
  private DefaultResourceLeak track0(T obj, boolean force) {
    Level level = ResourceLeakDetector.level;
    if (!force && level != Level.PARANOID) {
      if (level != Level.DISABLED)
        if (PlatformDependent.threadLocalRandom().nextInt(this.samplingInterval) == 0) {
          reportLeak();
          return new DefaultResourceLeak(obj, this.refQueue, this.allLeaks, getInitialHint(this.resourceType));
        }  
    } else {
      reportLeak();
      return new DefaultResourceLeak(obj, this.refQueue, this.allLeaks, getInitialHint(this.resourceType));
    } 
    return null;
  }
  
  private void clearRefQueue() {
    while (true) {
      DefaultResourceLeak ref = (DefaultResourceLeak)this.refQueue.poll();
      if (ref == null)
        break; 
      ref.dispose();
    } 
  }
  
  protected boolean needReport() {
    return logger.isErrorEnabled();
  }
  
  private void reportLeak() {
    if (!needReport()) {
      clearRefQueue();
      return;
    } 
    while (true) {
      DefaultResourceLeak ref = (DefaultResourceLeak)this.refQueue.poll();
      if (ref == null)
        break; 
      if (!ref.dispose())
        continue; 
      String records = ref.getReportAndClearRecords();
      if (this.reportedLeaks.add(records)) {
        if (records.isEmpty()) {
          reportUntracedLeak(this.resourceType);
          continue;
        } 
        reportTracedLeak(this.resourceType, records);
      } 
    } 
  }
  
  protected void reportTracedLeak(String resourceType, String records) {
    logger.error("LEAK: {}.release() was not called before it's garbage-collected. See https://netty.io/wiki/reference-counted-objects.html for more information.{}", resourceType, records);
  }
  
  protected void reportUntracedLeak(String resourceType) {
    logger.error("LEAK: {}.release() was not called before it's garbage-collected. Enable advanced leak reporting to find out where the leak occurred. To enable advanced leak reporting, specify the JVM option '-D{}={}' or call {}.setLevel() See https://netty.io/wiki/reference-counted-objects.html for more information.", new Object[] { resourceType, "io.netty.leakDetection.level", Level.ADVANCED.name().toLowerCase(), StringUtil.simpleClassName(this) });
  }
  
  @Deprecated
  protected void reportInstancesLeak(String resourceType) {}
  
  protected Object getInitialHint(String resourceType) {
    return null;
  }
  
  private static final class DefaultResourceLeak<T> extends WeakReference<Object> implements ResourceLeakTracker<T>, ResourceLeak {
    private static final AtomicReferenceFieldUpdater<DefaultResourceLeak<?>, ResourceLeakDetector.TraceRecord> headUpdater = AtomicReferenceFieldUpdater.newUpdater((Class)DefaultResourceLeak.class, ResourceLeakDetector.TraceRecord.class, "head");
    
    private static final AtomicIntegerFieldUpdater<DefaultResourceLeak<?>> droppedRecordsUpdater = AtomicIntegerFieldUpdater.newUpdater((Class)DefaultResourceLeak.class, "droppedRecords");
    
    private volatile ResourceLeakDetector.TraceRecord head;
    
    private volatile int droppedRecords;
    
    private final Set<DefaultResourceLeak<?>> allLeaks;
    
    private final int trackedHash;
    
    DefaultResourceLeak(Object referent, ReferenceQueue<Object> refQueue, Set<DefaultResourceLeak<?>> allLeaks, Object initialHint) {
      super(referent, refQueue);
      assert referent != null;
      this.trackedHash = System.identityHashCode(referent);
      allLeaks.add(this);
      headUpdater.set(this, (initialHint == null) ? new ResourceLeakDetector.TraceRecord(ResourceLeakDetector.TraceRecord.BOTTOM) : new ResourceLeakDetector.TraceRecord(ResourceLeakDetector.TraceRecord.BOTTOM, initialHint));
      this.allLeaks = allLeaks;
    }
    
    public void record() {
      record0(null);
    }
    
    public void record(Object hint) {
      record0(hint);
    }
    
    private void record0(Object hint) {
      if (ResourceLeakDetector.TARGET_RECORDS > 0)
        while (true) {
          boolean dropped;
          ResourceLeakDetector.TraceRecord oldHead, prevHead;
          if ((prevHead = oldHead = headUpdater.get(this)) == null)
            return; 
          int numElements = oldHead.pos + 1;
          if (numElements >= ResourceLeakDetector.TARGET_RECORDS) {
            int backOffFactor = Math.min(numElements - ResourceLeakDetector.TARGET_RECORDS, 30);
            if (dropped = (PlatformDependent.threadLocalRandom().nextInt(1 << backOffFactor) != 0))
              prevHead = oldHead.next; 
          } else {
            dropped = false;
          } 
          ResourceLeakDetector.TraceRecord newHead = (hint != null) ? new ResourceLeakDetector.TraceRecord(prevHead, hint) : new ResourceLeakDetector.TraceRecord(prevHead);
          if (headUpdater.compareAndSet(this, oldHead, newHead)) {
            if (dropped)
              droppedRecordsUpdater.incrementAndGet(this); 
            break;
          } 
        }  
    }
    
    boolean dispose() {
      clear();
      return this.allLeaks.remove(this);
    }
    
    public boolean close() {
      if (this.allLeaks.remove(this)) {
        clear();
        headUpdater.set(this, null);
        return true;
      } 
      return false;
    }
    
    public boolean close(T trackedObject) {
      assert this.trackedHash == System.identityHashCode(trackedObject);
      try {
        return close();
      } finally {
        reachabilityFence0(trackedObject);
      } 
    }
    
    private static void reachabilityFence0(Object ref) {
      if (ref != null)
        synchronized (ref) {
        
        }  
    }
    
    public String toString() {
      ResourceLeakDetector.TraceRecord oldHead = headUpdater.get(this);
      return generateReport(oldHead);
    }
    
    String getReportAndClearRecords() {
      ResourceLeakDetector.TraceRecord oldHead = headUpdater.getAndSet(this, null);
      return generateReport(oldHead);
    }
    
    private String generateReport(ResourceLeakDetector.TraceRecord oldHead) {
      if (oldHead == null)
        return ""; 
      int dropped = droppedRecordsUpdater.get(this);
      int duped = 0;
      int present = oldHead.pos + 1;
      StringBuilder buf = (new StringBuilder(present * 2048)).append(StringUtil.NEWLINE);
      buf.append("Recent access records: ").append(StringUtil.NEWLINE);
      int i = 1;
      Set<String> seen = new HashSet<String>(present);
      for (; oldHead != ResourceLeakDetector.TraceRecord.BOTTOM; oldHead = oldHead.next) {
        String s = oldHead.toString();
        if (seen.add(s)) {
          if (oldHead.next == ResourceLeakDetector.TraceRecord.BOTTOM) {
            buf.append("Created at:").append(StringUtil.NEWLINE).append(s);
          } else {
            buf.append('#').append(i++).append(':').append(StringUtil.NEWLINE).append(s);
          } 
        } else {
          duped++;
        } 
      } 
      if (duped > 0)
        buf.append(": ").append(duped).append(" leak records were discarded because they were duplicates").append(StringUtil.NEWLINE); 
      if (dropped > 0)
        buf.append(": ").append(dropped).append(" leak records were discarded because the leak record count is targeted to ").append(ResourceLeakDetector.TARGET_RECORDS).append(". Use system property ").append("io.netty.leakDetection.targetRecords").append(" to increase the limit.").append(StringUtil.NEWLINE); 
      buf.setLength(buf.length() - StringUtil.NEWLINE.length());
      return buf.toString();
    }
  }
  
  public static void addExclusions(Class clz, String... methodNames) {
    String[] oldMethods, newMethods;
    Set<String> nameSet = new HashSet<String>(Arrays.asList(methodNames));
    for (Method method : clz.getDeclaredMethods()) {
      if (nameSet.remove(method.getName()) && nameSet.isEmpty())
        break; 
    } 
    if (!nameSet.isEmpty())
      throw new IllegalArgumentException("Can't find '" + nameSet + "' in " + clz.getName()); 
    do {
      oldMethods = excludedMethods.get();
      newMethods = Arrays.<String>copyOf(oldMethods, oldMethods.length + 2 * methodNames.length);
      for (int i = 0; i < methodNames.length; i++) {
        newMethods[oldMethods.length + i * 2] = clz.getName();
        newMethods[oldMethods.length + i * 2 + 1] = methodNames[i];
      } 
    } while (!excludedMethods.compareAndSet(oldMethods, newMethods));
  }
  
  private static class TraceRecord extends Throwable {
    private static final long serialVersionUID = 6065153674892850720L;
    
    private static final TraceRecord BOTTOM = new TraceRecord() {
        private static final long serialVersionUID = 7396077602074694571L;
        
        public Throwable fillInStackTrace() {
          return this;
        }
      };
    
    private final String hintString;
    
    private final TraceRecord next;
    
    private final int pos;
    
    TraceRecord(TraceRecord next, Object hint) {
      this.hintString = (hint instanceof ResourceLeakHint) ? ((ResourceLeakHint)hint).toHintString() : hint.toString();
      this.next = next;
      next.pos++;
    }
    
    TraceRecord(TraceRecord next) {
      this.hintString = null;
      this.next = next;
      next.pos++;
    }
    
    private TraceRecord() {
      this.hintString = null;
      this.next = null;
      this.pos = -1;
    }
    
    public String toString() {
      StringBuilder buf = new StringBuilder(2048);
      if (this.hintString != null)
        buf.append("\tHint: ").append(this.hintString).append(StringUtil.NEWLINE); 
      StackTraceElement[] array = getStackTrace();
      for (int i = 3; i < array.length; i++) {
        StackTraceElement element = array[i];
        String[] exclusions = ResourceLeakDetector.excludedMethods.get();
        int k = 0;
        while (true) {
          if (k < exclusions.length) {
            if (exclusions[k].equals(element.getClassName()) && exclusions[k + 1]
              .equals(element.getMethodName()))
              break; 
            k += 2;
            continue;
          } 
          buf.append('\t');
          buf.append(element.toString());
          buf.append(StringUtil.NEWLINE);
          break;
        } 
      } 
      return buf.toString();
    }
  }
}
