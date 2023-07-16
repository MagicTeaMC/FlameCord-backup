package org.apache.maven.model.interpolation;

import java.io.File;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.apache.maven.model.InputLocation;
import org.apache.maven.model.Model;
import org.apache.maven.model.building.ModelBuildingRequest;
import org.apache.maven.model.building.ModelProblem;
import org.apache.maven.model.building.ModelProblemCollector;
import org.apache.maven.model.building.ModelProblemCollectorRequest;
import org.codehaus.plexus.interpolation.InterpolationException;
import org.codehaus.plexus.interpolation.InterpolationPostProcessor;
import org.codehaus.plexus.interpolation.RecursionInterceptor;
import org.codehaus.plexus.interpolation.StringSearchInterpolator;
import org.codehaus.plexus.interpolation.ValueSource;

@Deprecated
public class StringSearchModelInterpolator extends AbstractStringBasedModelInterpolator {
  private static final Map<Class<?>, InterpolateObjectAction.CacheItem> CACHED_ENTRIES = new ConcurrentHashMap<>(80, 0.75F, 2);
  
  public Model interpolateModel(Model model, File projectDir, ModelBuildingRequest config, ModelProblemCollector problems) {
    interpolateObject(model, model, projectDir, config, problems);
    return model;
  }
  
  void interpolateObject(Object obj, Model model, File projectDir, ModelBuildingRequest config, ModelProblemCollector problems) {
    List<? extends ValueSource> valueSources = createValueSources(model, projectDir, config, problems);
    List<? extends InterpolationPostProcessor> postProcessors = createPostProcessors(model, projectDir, config);
    InnerInterpolator innerInterpolator = createInterpolator(valueSources, postProcessors, problems);
    PrivilegedAction<Object> action = new InterpolateObjectAction(obj, innerInterpolator, problems);
    AccessController.doPrivileged(action);
  }
  
  private InnerInterpolator createInterpolator(List<? extends ValueSource> valueSources, List<? extends InterpolationPostProcessor> postProcessors, final ModelProblemCollector problems) {
    final Map<String, String> cache = new HashMap<>();
    final StringSearchInterpolator interpolator = new StringSearchInterpolator();
    interpolator.setCacheAnswers(true);
    for (ValueSource vs : valueSources)
      interpolator.addValueSource(vs); 
    for (InterpolationPostProcessor postProcessor : postProcessors)
      interpolator.addPostProcessor(postProcessor); 
    final RecursionInterceptor recursionInterceptor = createRecursionInterceptor();
    return new InnerInterpolator() {
        public String interpolate(String value) {
          if (value != null && value.contains("${")) {
            String c = (String)cache.get(value);
            if (c == null) {
              try {
                c = interpolator.interpolate(value, recursionInterceptor);
              } catch (InterpolationException e) {
                problems.add((new ModelProblemCollectorRequest(ModelProblem.Severity.ERROR, ModelProblem.Version.BASE))
                    .setMessage(e.getMessage()).setException((Exception)e));
              } 
              cache.put(value, c);
            } 
            return c;
          } 
          return value;
        }
      };
  }
  
  private static final class InterpolateObjectAction implements PrivilegedAction<Object> {
    private final LinkedList<Object> interpolationTargets;
    
    private final StringSearchModelInterpolator.InnerInterpolator interpolator;
    
    private final ModelProblemCollector problems;
    
    InterpolateObjectAction(Object target, StringSearchModelInterpolator.InnerInterpolator interpolator, ModelProblemCollector problems) {
      this.interpolationTargets = new LinkedList();
      this.interpolationTargets.add(target);
      this.interpolator = interpolator;
      this.problems = problems;
    }
    
    public Object run() {
      while (!this.interpolationTargets.isEmpty()) {
        Object obj = this.interpolationTargets.removeFirst();
        traverseObjectWithParents(obj.getClass(), obj);
      } 
      return null;
    }
    
    private String interpolate(String value) {
      return this.interpolator.interpolate(value);
    }
    
    private void traverseObjectWithParents(Class<?> cls, Object target) {
      if (cls == null)
        return; 
      CacheItem cacheEntry = getCacheEntry(cls);
      if (cacheEntry.isArray()) {
        evaluateArray(target, this);
      } else if (cacheEntry.isQualifiedForInterpolation) {
        cacheEntry.interpolate(target, this);
        traverseObjectWithParents(cls.getSuperclass(), target);
      } 
    }
    
    private CacheItem getCacheEntry(Class<?> cls) {
      CacheItem cacheItem = (CacheItem)StringSearchModelInterpolator.CACHED_ENTRIES.get(cls);
      if (cacheItem == null) {
        cacheItem = new CacheItem(cls);
        StringSearchModelInterpolator.CACHED_ENTRIES.put(cls, cacheItem);
      } 
      return cacheItem;
    }
    
    private static void evaluateArray(Object target, InterpolateObjectAction ctx) {
      int len = Array.getLength(target);
      for (int i = 0; i < len; i++) {
        Object value = Array.get(target, i);
        if (value != null)
          if (String.class == value.getClass()) {
            String interpolated = ctx.interpolate((String)value);
            if (!interpolated.equals(value))
              Array.set(target, i, interpolated); 
          } else {
            ctx.interpolationTargets.add(value);
          }  
      } 
    }
    
    private static class CacheItem {
      private final boolean isArray;
      
      private final boolean isQualifiedForInterpolation;
      
      private final StringSearchModelInterpolator.InterpolateObjectAction.CacheField[] fields;
      
      private boolean isQualifiedForInterpolation(Class<?> cls) {
        Package pkg = cls.getPackage();
        if (pkg == null)
          return true; 
        String pkgName = pkg.getName();
        return (!pkgName.startsWith("java.") && !pkgName.startsWith("javax."));
      }
      
      private boolean isQualifiedForInterpolation(Field field, Class<?> fieldType) {
        if (Map.class.equals(fieldType) && "locations".equals(field.getName()))
          return false; 
        if (InputLocation.class.equals(fieldType))
          return false; 
        if (fieldType.isPrimitive())
          return false; 
        return !"parent".equals(field.getName());
      }
      
      CacheItem(Class<?> clazz) {
        this.isQualifiedForInterpolation = isQualifiedForInterpolation(clazz);
        this.isArray = clazz.isArray();
        List<StringSearchModelInterpolator.InterpolateObjectAction.CacheField> fields = new ArrayList<>();
        if (this.isQualifiedForInterpolation)
          for (Field currentField : clazz.getDeclaredFields()) {
            Class<?> type = currentField.getType();
            if (isQualifiedForInterpolation(currentField, type))
              if (String.class == type) {
                if (!Modifier.isFinal(currentField.getModifiers()))
                  fields.add(new StringSearchModelInterpolator.InterpolateObjectAction.StringField(currentField)); 
              } else if (List.class.isAssignableFrom(type)) {
                fields.add(new StringSearchModelInterpolator.InterpolateObjectAction.ListField(currentField));
              } else {
                if (Collection.class.isAssignableFrom(type))
                  throw new RuntimeException("We dont interpolate into collections, use a list instead"); 
                if (Map.class.isAssignableFrom(type)) {
                  fields.add(new StringSearchModelInterpolator.InterpolateObjectAction.MapField(currentField));
                } else {
                  fields.add(new StringSearchModelInterpolator.InterpolateObjectAction.ObjectField(currentField));
                } 
              }  
          }  
        this.fields = fields.<StringSearchModelInterpolator.InterpolateObjectAction.CacheField>toArray(new StringSearchModelInterpolator.InterpolateObjectAction.CacheField[0]);
      }
      
      void interpolate(Object target, StringSearchModelInterpolator.InterpolateObjectAction interpolateObjectAction) {
        for (StringSearchModelInterpolator.InterpolateObjectAction.CacheField field : this.fields)
          field.interpolate(target, interpolateObjectAction); 
      }
      
      boolean isArray() {
        return this.isArray;
      }
    }
    
    static abstract class CacheField {
      final Field field;
      
      CacheField(Field field) {
        this.field = field;
        field.setAccessible(true);
      }
      
      void interpolate(Object target, StringSearchModelInterpolator.InterpolateObjectAction interpolateObjectAction) {
        try {
          doInterpolate(target, interpolateObjectAction);
        } catch (IllegalArgumentException e) {
          interpolateObjectAction.problems.add((new ModelProblemCollectorRequest(ModelProblem.Severity.ERROR, ModelProblem.Version.BASE))
              .setMessage("Failed to interpolate field3: " + this.field + " on class: " + this.field
                
                .getType().getName()).setException(e));
        } catch (IllegalAccessException e) {
          interpolateObjectAction.problems.add((new ModelProblemCollectorRequest(ModelProblem.Severity.ERROR, ModelProblem.Version.BASE))
              .setMessage("Failed to interpolate field4: " + this.field + " on class: " + this.field
                
                .getType().getName()).setException(e));
        } 
      }
      
      abstract void doInterpolate(Object param2Object, StringSearchModelInterpolator.InterpolateObjectAction param2InterpolateObjectAction) throws IllegalAccessException;
    }
    
    static final class StringField extends CacheField {
      StringField(Field field) {
        super(field);
      }
      
      void doInterpolate(Object target, StringSearchModelInterpolator.InterpolateObjectAction ctx) throws IllegalAccessException {
        String value = (String)this.field.get(target);
        if (value == null)
          return; 
        String interpolated = ctx.interpolate(value);
        if (interpolated != null && !interpolated.equals(value))
          this.field.set(target, interpolated); 
      }
    }
    
    static final class ListField extends CacheField {
      ListField(Field field) {
        super(field);
      }
      
      void doInterpolate(Object target, StringSearchModelInterpolator.InterpolateObjectAction ctx) throws IllegalAccessException {
        List<Object> c = (List<Object>)this.field.get(target);
        if (c == null)
          return; 
        for (int i = 0, size = c.size(); i < size; i++) {
          Object value = c.get(i);
          if (value != null)
            if (String.class == value.getClass()) {
              String interpolated = ctx.interpolate((String)value);
              if (!interpolated.equals(value))
                try {
                  c.set(i, interpolated);
                } catch (UnsupportedOperationException e) {
                  return;
                }  
            } else if (value.getClass().isArray()) {
              StringSearchModelInterpolator.InterpolateObjectAction.evaluateArray(value, ctx);
            } else {
              ctx.interpolationTargets.add(value);
            }  
        } 
      }
    }
    
    static final class MapField extends CacheField {
      MapField(Field field) {
        super(field);
      }
      
      void doInterpolate(Object target, StringSearchModelInterpolator.InterpolateObjectAction ctx) throws IllegalAccessException {
        Map<Object, Object> m = (Map<Object, Object>)this.field.get(target);
        if (m == null || m.isEmpty())
          return; 
        for (Map.Entry<Object, Object> entry : m.entrySet()) {
          Object value = entry.getValue();
          if (value == null)
            continue; 
          if (String.class == value.getClass()) {
            String interpolated = ctx.interpolate((String)value);
            if (interpolated != null && !interpolated.equals(value))
              try {
                entry.setValue(interpolated);
              } catch (UnsupportedOperationException unsupportedOperationException) {} 
            continue;
          } 
          if (value.getClass().isArray()) {
            StringSearchModelInterpolator.InterpolateObjectAction.evaluateArray(value, ctx);
            continue;
          } 
          ctx.interpolationTargets.add(value);
        } 
      }
    }
    
    static final class ObjectField extends CacheField {
      private final boolean isArray;
      
      ObjectField(Field field) {
        super(field);
        this.isArray = field.getType().isArray();
      }
      
      void doInterpolate(Object target, StringSearchModelInterpolator.InterpolateObjectAction ctx) throws IllegalAccessException {
        Object value = this.field.get(target);
        if (value != null)
          if (this.isArray) {
            StringSearchModelInterpolator.InterpolateObjectAction.evaluateArray(value, ctx);
          } else {
            ctx.interpolationTargets.add(value);
          }  
      }
    }
  }
  
  private static class CacheItem {
    private final boolean isArray;
    
    private final boolean isQualifiedForInterpolation;
    
    private final StringSearchModelInterpolator.InterpolateObjectAction.CacheField[] fields;
    
    private boolean isQualifiedForInterpolation(Class<?> cls) {
      Package pkg = cls.getPackage();
      if (pkg == null)
        return true; 
      String pkgName = pkg.getName();
      return (!pkgName.startsWith("java.") && !pkgName.startsWith("javax."));
    }
    
    private boolean isQualifiedForInterpolation(Field field, Class<?> fieldType) {
      if (Map.class.equals(fieldType) && "locations".equals(field.getName()))
        return false; 
      if (InputLocation.class.equals(fieldType))
        return false; 
      if (fieldType.isPrimitive())
        return false; 
      return !"parent".equals(field.getName());
    }
    
    CacheItem(Class<?> clazz) {
      this.isQualifiedForInterpolation = isQualifiedForInterpolation(clazz);
      this.isArray = clazz.isArray();
      List<StringSearchModelInterpolator.InterpolateObjectAction.CacheField> fields = new ArrayList<>();
      if (this.isQualifiedForInterpolation)
        for (Field currentField : clazz.getDeclaredFields()) {
          Class<?> type = currentField.getType();
          if (isQualifiedForInterpolation(currentField, type))
            if (String.class == type) {
              if (!Modifier.isFinal(currentField.getModifiers()))
                fields.add(new StringSearchModelInterpolator.InterpolateObjectAction.StringField(currentField)); 
            } else if (List.class.isAssignableFrom(type)) {
              fields.add(new StringSearchModelInterpolator.InterpolateObjectAction.ListField(currentField));
            } else {
              if (Collection.class.isAssignableFrom(type))
                throw new RuntimeException("We dont interpolate into collections, use a list instead"); 
              if (Map.class.isAssignableFrom(type)) {
                fields.add(new StringSearchModelInterpolator.InterpolateObjectAction.MapField(currentField));
              } else {
                fields.add(new StringSearchModelInterpolator.InterpolateObjectAction.ObjectField(currentField));
              } 
            }  
        }  
      this.fields = fields.<StringSearchModelInterpolator.InterpolateObjectAction.CacheField>toArray(new StringSearchModelInterpolator.InterpolateObjectAction.CacheField[0]);
    }
    
    void interpolate(Object target, StringSearchModelInterpolator.InterpolateObjectAction interpolateObjectAction) {
      for (StringSearchModelInterpolator.InterpolateObjectAction.CacheField field : this.fields)
        field.interpolate(target, interpolateObjectAction); 
    }
    
    boolean isArray() {
      return this.isArray;
    }
  }
  
  private static interface InnerInterpolator {
    String interpolate(String param1String);
  }
}
