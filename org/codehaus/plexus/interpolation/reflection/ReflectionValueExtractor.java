package org.codehaus.plexus.interpolation.reflection;

import java.lang.ref.WeakReference;
import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.WeakHashMap;
import org.codehaus.plexus.interpolation.util.StringUtils;

public class ReflectionValueExtractor {
  private static final Class<?>[] CLASS_ARGS = new Class[0];
  
  private static final Object[] OBJECT_ARGS = new Object[0];
  
  private static final Map<Class<?>, WeakReference<ClassMap>> classMaps = new WeakHashMap<Class<?>, WeakReference<ClassMap>>();
  
  static final int EOF = -1;
  
  static final char PROPERTY_START = '.';
  
  static final char INDEXED_START = '[';
  
  static final char INDEXED_END = ']';
  
  static final char MAPPED_START = '(';
  
  static final char MAPPED_END = ')';
  
  static class Tokenizer {
    final String expression;
    
    int idx;
    
    public Tokenizer(String expression) {
      this.expression = expression;
    }
    
    public int peekChar() {
      return (this.idx < this.expression.length()) ? this.expression.charAt(this.idx) : -1;
    }
    
    public int skipChar() {
      return (this.idx < this.expression.length()) ? this.expression.charAt(this.idx++) : -1;
    }
    
    public String nextToken(char delimiter) {
      int start = this.idx;
      while (this.idx < this.expression.length() && delimiter != this.expression.charAt(this.idx))
        this.idx++; 
      if (this.idx <= start || this.idx >= this.expression.length())
        return null; 
      return this.expression.substring(start, this.idx++);
    }
    
    public String nextPropertyName() {
      int start = this.idx;
      while (this.idx < this.expression.length() && Character.isJavaIdentifierPart(this.expression.charAt(this.idx)))
        this.idx++; 
      if (this.idx <= start || this.idx > this.expression.length())
        return null; 
      return this.expression.substring(start, this.idx);
    }
    
    public int getPosition() {
      return (this.idx < this.expression.length()) ? this.idx : -1;
    }
    
    public String toString() {
      return (this.idx < this.expression.length()) ? this.expression.substring(this.idx) : "<EOF>";
    }
  }
  
  public static Object evaluate(String expression, Object root) throws Exception {
    return evaluate(expression, root, true);
  }
  
  public static Object evaluate(String expression, Object root, boolean trimRootToken) throws Exception {
    Tokenizer tokenizer;
    Object value = root;
    if (expression == null || "".equals(expression.trim()) || 
      !Character.isJavaIdentifierStart(expression.charAt(0)))
      return null; 
    boolean hasDots = (expression.indexOf('.') >= 0);
    if (trimRootToken && hasDots) {
      tokenizer = new Tokenizer(expression);
      tokenizer.nextPropertyName();
      if (tokenizer.getPosition() == -1)
        return null; 
    } else {
      tokenizer = new Tokenizer("." + expression);
    } 
    int propertyPosition = tokenizer.getPosition();
    while (value != null && tokenizer.peekChar() != -1) {
      switch (tokenizer.skipChar()) {
        case 91:
          value = getIndexedValue(expression, propertyPosition, tokenizer.getPosition(), value, tokenizer
              .nextToken(']'));
          continue;
        case 40:
          value = getMappedValue(expression, propertyPosition, tokenizer.getPosition(), value, tokenizer
              .nextToken(')'));
          continue;
        case 46:
          propertyPosition = tokenizer.getPosition();
          value = getPropertyValue(value, tokenizer.nextPropertyName());
          continue;
      } 
      return null;
    } 
    return value;
  }
  
  private static Object getMappedValue(String expression, int from, int to, Object value, String key) throws Exception {
    if (value == null || key == null)
      return null; 
    if (value instanceof Map) {
      Object[] localParams = { key };
      ClassMap classMap = getClassMap(value.getClass());
      Method method = classMap.findMethod("get", localParams);
      return method.invoke(value, localParams);
    } 
    String message = String.format("The token '%s' at position '%d' refers to a java.util.Map, but the value seems is an instance of '%s'", new Object[] { expression.subSequence(from, to), Integer.valueOf(from), value.getClass() });
    throw new Exception(message);
  }
  
  private static Object getIndexedValue(String expression, int from, int to, Object value, String indexStr) throws Exception {
    try {
      int index = Integer.parseInt(indexStr);
      if (value.getClass().isArray())
        return Array.get(value, index); 
      if (value instanceof java.util.List) {
        ClassMap classMap = getClassMap(value.getClass());
        Object[] localParams = { Integer.valueOf(index) };
        Method method = classMap.findMethod("get", localParams);
        return method.invoke(value, localParams);
      } 
    } catch (NumberFormatException e) {
      return null;
    } catch (InvocationTargetException e) {
      if (e.getCause() instanceof IndexOutOfBoundsException)
        return null; 
      throw e;
    } 
    String message = String.format("The token '%s' at position '%d' refers to a java.util.List or an array, but the value seems is an instance of '%s'", new Object[] { expression.subSequence(from, to), Integer.valueOf(from), value.getClass() });
    throw new Exception(message);
  }
  
  private static Object getPropertyValue(Object value, String property) throws Exception {
    if (value == null || property == null)
      return null; 
    ClassMap classMap = getClassMap(value.getClass());
    String methodBase = StringUtils.capitalizeFirstLetter(property);
    String methodName = "get" + methodBase;
    Method method = classMap.findMethod(methodName, (Object[])CLASS_ARGS);
    if (method == null) {
      methodName = "is" + methodBase;
      method = classMap.findMethod(methodName, (Object[])CLASS_ARGS);
    } 
    if (method == null)
      return null; 
    try {
      return method.invoke(value, OBJECT_ARGS);
    } catch (InvocationTargetException e) {
      throw e;
    } 
  }
  
  private static ClassMap getClassMap(Class<?> clazz) {
    WeakReference<ClassMap> softRef = classMaps.get(clazz);
    ClassMap classMap;
    if (softRef == null || (classMap = softRef.get()) == null) {
      classMap = new ClassMap(clazz);
      classMaps.put(clazz, new WeakReference<ClassMap>(classMap));
    } 
    return classMap;
  }
}
