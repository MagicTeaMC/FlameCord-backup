package org.eclipse.sisu.space;

import com.google.inject.Binder;
import com.google.inject.Key;
import com.google.inject.Module;
import com.google.inject.PrivateBinder;
import com.google.inject.spi.Element;
import com.google.inject.spi.Elements;
import com.google.inject.spi.MembersInjectorLookup;
import com.google.inject.spi.PrivateElements;
import com.google.inject.spi.ProviderLookup;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public final class SpaceModule implements Module {
  private static final String NAMED_INDEX = "META-INF/sisu/javax.inject.Named";
  
  public static final ClassFinder LOCAL_INDEX = new IndexedClassFinder("META-INF/sisu/javax.inject.Named", false);
  
  public static final ClassFinder GLOBAL_INDEX = new IndexedClassFinder("META-INF/sisu/javax.inject.Named", true);
  
  public static final ClassFinder LOCAL_SCAN = SpaceScanner.DEFAULT_FINDER;
  
  private final boolean caching;
  
  private final ClassSpace space;
  
  private final ClassFinder finder;
  
  private static final class RecordedElements {
    static final ConcurrentMap<String, List<Element>> cache = new ConcurrentHashMap<String, List<Element>>();
  }
  
  private Strategy strategy = Strategy.DEFAULT;
  
  public SpaceModule(ClassSpace space) {
    this(space, BeanScanning.ON);
  }
  
  public SpaceModule(ClassSpace space, ClassFinder finder) {
    this.caching = false;
    this.space = space;
    this.finder = finder;
  }
  
  public SpaceModule(ClassSpace space, BeanScanning scanning) {
    this.caching = (BeanScanning.CACHE == scanning);
    this.space = space;
    switch (scanning) {
      case OFF:
        this.finder = null;
        return;
      case INDEX:
        this.finder = LOCAL_INDEX;
        return;
      case GLOBAL_INDEX:
        this.finder = GLOBAL_INDEX;
        return;
    } 
    this.finder = LOCAL_SCAN;
  }
  
  public Module with(Strategy _strategy) {
    this.strategy = _strategy;
    return this;
  }
  
  public void configure(Binder binder) {
    binder.bind(ClassSpace.class).toInstance(this.space);
    if (this.caching) {
      recordAndReplayElements(binder);
    } else if (this.finder != null) {
      scanForElements(binder);
    } 
  }
  
  public static interface Strategy {
    public static final Strategy DEFAULT = new Strategy() {
        public SpaceVisitor visitor(Binder binder) {
          return new QualifiedTypeVisitor(new QualifiedTypeBinder(binder));
        }
      };
    
    SpaceVisitor visitor(Binder param1Binder);
  }
  
  void scanForElements(Binder binder) {
    (new SpaceScanner(this.space, this.finder)).accept(this.strategy.visitor(binder));
  }
  
  private void recordAndReplayElements(Binder binder) {
    String key = this.space.toString();
    List<Element> elements = RecordedElements.cache.get(key);
    if (elements == null) {
      List<Element> recording = Elements.getElements(new Module[] { new Module() {
              public void configure(Binder recorder) {
                SpaceModule.this.scanForElements(recorder);
              }
            } });
      elements = RecordedElements.cache.putIfAbsent(key, recording);
      if (elements == null) {
        Elements.getModule(recording).configure(binder);
        return;
      } 
    } 
    replayRecordedElements(binder, elements);
  }
  
  private static void replayRecordedElements(Binder binder, List<Element> elements) {
    for (Element e : elements) {
      if (e instanceof ProviderLookup) {
        binder.getProvider(((ProviderLookup)e).getKey());
        continue;
      } 
      if (e instanceof MembersInjectorLookup) {
        binder.getMembersInjector(((MembersInjectorLookup)e).getType());
        continue;
      } 
      if (e instanceof PrivateElements) {
        PrivateElements privateElements = (PrivateElements)e;
        PrivateBinder privateBinder = binder.withSource(e.getSource()).newPrivateBinder();
        replayRecordedElements((Binder)privateBinder, privateElements.getElements());
        for (Key<?> k : (Iterable<Key<?>>)privateElements.getExposedKeys())
          privateBinder.withSource(privateElements.getExposedSource(k)).expose(k); 
        continue;
      } 
      e.applyTo(binder);
    } 
  }
}
