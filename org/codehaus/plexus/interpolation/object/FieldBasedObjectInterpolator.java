package org.codehaus.plexus.interpolation.object;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;
import org.codehaus.plexus.interpolation.BasicInterpolator;
import org.codehaus.plexus.interpolation.InterpolationException;
import org.codehaus.plexus.interpolation.RecursionInterceptor;
import org.codehaus.plexus.interpolation.SimpleRecursionInterceptor;

public class FieldBasedObjectInterpolator implements ObjectInterpolator {
  public static final Set<String> DEFAULT_BLACKLISTED_FIELD_NAMES;
  
  public static final Set<String> DEFAULT_BLACKLISTED_PACKAGE_PREFIXES;
  
  private static final Map<Class, Field[]> fieldsByClass = (Map)new WeakHashMap<Class<?>, Field>();
  
  private static final Map<Class, Boolean> fieldIsPrimitiveByClass = (Map)new WeakHashMap<Class<?>, Boolean>();
  
  private Set<String> blacklistedFieldNames;
  
  private Set<String> blacklistedPackagePrefixes;
  
  static {
    Set<String> blacklistedFields = new HashSet<String>();
    blacklistedFields.add("parent");
    DEFAULT_BLACKLISTED_FIELD_NAMES = Collections.unmodifiableSet(blacklistedFields);
    Set<String> blacklistedPackages = new HashSet<String>();
    blacklistedPackages.add("java");
    DEFAULT_BLACKLISTED_PACKAGE_PREFIXES = Collections.unmodifiableSet(blacklistedPackages);
  }
  
  public static void clearCaches() {
    fieldsByClass.clear();
    fieldIsPrimitiveByClass.clear();
  }
  
  private List<ObjectInterpolationWarning> warnings = new ArrayList<ObjectInterpolationWarning>();
  
  public FieldBasedObjectInterpolator() {
    this.blacklistedFieldNames = DEFAULT_BLACKLISTED_FIELD_NAMES;
    this.blacklistedPackagePrefixes = DEFAULT_BLACKLISTED_PACKAGE_PREFIXES;
  }
  
  public FieldBasedObjectInterpolator(Set<String> blacklistedFieldNames, Set<String> blacklistedPackagePrefixes) {
    this.blacklistedFieldNames = blacklistedFieldNames;
    this.blacklistedPackagePrefixes = blacklistedPackagePrefixes;
  }
  
  public boolean hasWarnings() {
    return (this.warnings != null && !this.warnings.isEmpty());
  }
  
  public List<ObjectInterpolationWarning> getWarnings() {
    return new ArrayList<ObjectInterpolationWarning>(this.warnings);
  }
  
  public void interpolate(Object target, BasicInterpolator interpolator) throws InterpolationException {
    interpolate(target, interpolator, (RecursionInterceptor)new SimpleRecursionInterceptor());
  }
  
  public void interpolate(Object target, BasicInterpolator interpolator, RecursionInterceptor recursionInterceptor) throws InterpolationException {
    this.warnings.clear();
    InterpolateObjectAction action = new InterpolateObjectAction(target, interpolator, recursionInterceptor, this.blacklistedFieldNames, this.blacklistedPackagePrefixes, this.warnings);
    InterpolationException error = AccessController.<InterpolationException>doPrivileged(action);
    if (error != null)
      throw error; 
  }
  
  private static final class InterpolateObjectAction implements PrivilegedAction {
    private final LinkedList<FieldBasedObjectInterpolator.InterpolationTarget> interpolationTargets;
    
    private final BasicInterpolator interpolator;
    
    private final Set blacklistedFieldNames;
    
    private final String[] blacklistedPackagePrefixes;
    
    private final List<ObjectInterpolationWarning> warningCollector;
    
    private final RecursionInterceptor recursionInterceptor;
    
    public InterpolateObjectAction(Object target, BasicInterpolator interpolator, RecursionInterceptor recursionInterceptor, Set blacklistedFieldNames, Set blacklistedPackagePrefixes, List<ObjectInterpolationWarning> warningCollector) {
      this.recursionInterceptor = recursionInterceptor;
      this.blacklistedFieldNames = blacklistedFieldNames;
      this.warningCollector = warningCollector;
      this.blacklistedPackagePrefixes = (String[])blacklistedPackagePrefixes.toArray((Object[])new String[blacklistedPackagePrefixes.size()]);
      this.interpolationTargets = new LinkedList<FieldBasedObjectInterpolator.InterpolationTarget>();
      this.interpolationTargets.add(new FieldBasedObjectInterpolator.InterpolationTarget(target, ""));
      this.interpolator = interpolator;
    }
    
    public Object run() {
      while (!this.interpolationTargets.isEmpty()) {
        FieldBasedObjectInterpolator.InterpolationTarget target = this.interpolationTargets.removeFirst();
        try {
          traverseObjectWithParents(target.value.getClass(), target);
        } catch (InterpolationException e) {
          return e;
        } 
      } 
      return null;
    }
    
    private void traverseObjectWithParents(Class<?> cls, FieldBasedObjectInterpolator.InterpolationTarget target) throws InterpolationException {
      Object obj = target.value;
      String basePath = target.path;
      if (cls == null)
        return; 
      if (cls.isArray()) {
        evaluateArray(obj, basePath);
      } else if (isQualifiedForInterpolation(cls)) {
        Field[] fields = (Field[])FieldBasedObjectInterpolator.fieldsByClass.get(cls);
        if (fields == null) {
          fields = cls.getDeclaredFields();
          FieldBasedObjectInterpolator.fieldsByClass.put(cls, fields);
        } 
        for (Field field : fields) {
          Class<?> type = field.getType();
          if (isQualifiedForInterpolation(field, type)) {
            boolean isAccessible = field.isAccessible();
            synchronized (cls) {
              field.setAccessible(true);
              try {
                if (String.class == type) {
                  interpolateString(obj, field);
                } else if (Collection.class.isAssignableFrom(type)) {
                  if (interpolateCollection(obj, basePath, field)) {
                    field.setAccessible(isAccessible);
                    continue;
                  } 
                } else if (Map.class.isAssignableFrom(type)) {
                  interpolateMap(obj, basePath, field);
                } else {
                  interpolateObject(obj, basePath, field);
                } 
              } catch (IllegalArgumentException e) {
                this.warningCollector.add(new ObjectInterpolationWarning("Failed to interpolate field. Skipping.", basePath + "." + field.getName(), e));
              } catch (IllegalAccessException e) {
                this.warningCollector.add(new ObjectInterpolationWarning("Failed to interpolate field. Skipping.", basePath + "." + field.getName(), e));
              } finally {
                field.setAccessible(isAccessible);
              } 
            } 
          } 
          continue;
        } 
        traverseObjectWithParents(cls.getSuperclass(), target);
      } 
    }
    
    private void interpolateObject(Object obj, String basePath, Field field) throws IllegalAccessException, InterpolationException {
      Object value = field.get(obj);
      if (value != null)
        if (field.getType().isArray()) {
          evaluateArray(value, basePath + "." + field.getName());
        } else {
          this.interpolationTargets.add(new FieldBasedObjectInterpolator.InterpolationTarget(value, basePath + "." + field.getName()));
        }  
    }
    
    private void interpolateMap(Object obj, String basePath, Field field) throws IllegalAccessException, InterpolationException {
      Map m = (Map)field.get(obj);
      if (m != null && !m.isEmpty())
        for (Object o : m.entrySet()) {
          Map.Entry entry = (Map.Entry)o;
          Object value = entry.getValue();
          if (value != null) {
            if (String.class == value.getClass()) {
              String interpolated = this.interpolator.interpolate((String)value, this.recursionInterceptor);
              if (!interpolated.equals(value))
                try {
                  entry.setValue(interpolated);
                } catch (UnsupportedOperationException e) {
                  this.warningCollector.add(new ObjectInterpolationWarning("Field is an unmodifiable collection. Skipping interpolation.", basePath + "." + field
                        
                        .getName(), e));
                }  
              continue;
            } 
            if (value.getClass().isArray()) {
              evaluateArray(value, basePath + "." + field.getName());
              continue;
            } 
            this.interpolationTargets.add(new FieldBasedObjectInterpolator.InterpolationTarget(value, basePath + "." + field
                  .getName()));
          } 
        }  
    }
    
    private boolean interpolateCollection(Object obj, String basePath, Field field) throws IllegalAccessException, InterpolationException {
      Collection<?> c = (Collection)field.get(obj);
      if (c != null && !c.isEmpty()) {
        List originalValues = new ArrayList(c);
        try {
          c.clear();
        } catch (UnsupportedOperationException e) {
          this.warningCollector.add(new ObjectInterpolationWarning("Field is an unmodifiable collection. Skipping interpolation.", basePath + "." + field
                
                .getName(), e));
          return true;
        } 
        for (Object value : originalValues) {
          if (value != null) {
            if (String.class == value.getClass()) {
              String interpolated = this.interpolator.interpolate((String)value, this.recursionInterceptor);
              if (!interpolated.equals(value)) {
                c.add(interpolated);
                continue;
              } 
              c.add(value);
              continue;
            } 
            c.add(value);
            if (value.getClass().isArray()) {
              evaluateArray(value, basePath + "." + field.getName());
              continue;
            } 
            this.interpolationTargets.add(new FieldBasedObjectInterpolator.InterpolationTarget(value, basePath + "." + field
                  .getName()));
            continue;
          } 
          c.add(value);
        } 
      } 
      return false;
    }
    
    private void interpolateString(Object obj, Field field) throws IllegalAccessException, InterpolationException {
      String value = (String)field.get(obj);
      if (value != null) {
        String interpolated = this.interpolator.interpolate(value, this.recursionInterceptor);
        if (!interpolated.equals(value))
          field.set(obj, interpolated); 
      } 
    }
    
    private boolean isQualifiedForInterpolation(Class cls) {
      String pkgName = cls.getPackage().getName();
      for (String prefix : this.blacklistedPackagePrefixes) {
        if (pkgName.startsWith(prefix))
          return false; 
      } 
      return true;
    }
    
    private boolean isQualifiedForInterpolation(Field field, Class<?> fieldType) {
      if (!FieldBasedObjectInterpolator.fieldIsPrimitiveByClass.containsKey(fieldType))
        FieldBasedObjectInterpolator.fieldIsPrimitiveByClass.put(fieldType, Boolean.valueOf(fieldType.isPrimitive())); 
      if (((Boolean)FieldBasedObjectInterpolator.fieldIsPrimitiveByClass.get(fieldType)).booleanValue())
        return false; 
      return !this.blacklistedFieldNames.contains(field.getName());
    }
    
    private void evaluateArray(Object target, String basePath) throws InterpolationException {
      int len = Array.getLength(target);
      for (int i = 0; i < len; i++) {
        Object value = Array.get(target, i);
        if (value != null)
          if (String.class == value.getClass()) {
            String interpolated = this.interpolator.interpolate((String)value, this.recursionInterceptor);
            if (!interpolated.equals(value))
              Array.set(target, i, interpolated); 
          } else {
            this.interpolationTargets.add(new FieldBasedObjectInterpolator.InterpolationTarget(value, basePath + "[" + i + "]"));
          }  
      } 
    }
  }
  
  private static final class InterpolationTarget {
    private Object value;
    
    private String path;
    
    private InterpolationTarget(Object value, String path) {
      this.value = value;
      this.path = path;
    }
  }
}
