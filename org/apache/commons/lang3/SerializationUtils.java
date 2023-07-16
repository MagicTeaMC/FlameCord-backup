package org.apache.commons.lang3;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.ObjectStreamClass;
import java.io.OutputStream;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class SerializationUtils {
  public static <T extends Serializable> T clone(T object) {
    if (object == null)
      return null; 
    byte[] objectData = serialize((Serializable)object);
    ByteArrayInputStream bais = new ByteArrayInputStream(objectData);
    try (ClassLoaderAwareObjectInputStream in = new ClassLoaderAwareObjectInputStream(bais, object
          .getClass().getClassLoader())) {
      Serializable serializable = (Serializable)in.readObject();
      return (T)serializable;
    } catch (ClassNotFoundException ex) {
      throw new SerializationException("ClassNotFoundException while reading cloned object data", ex);
    } catch (IOException ex) {
      throw new SerializationException("IOException while reading or closing cloned object data", ex);
    } 
  }
  
  public static <T extends Serializable> T roundtrip(T msg) {
    return (T)deserialize(serialize((Serializable)msg));
  }
  
  public static void serialize(Serializable obj, OutputStream outputStream) {
    Validate.isTrue((outputStream != null), "The OutputStream must not be null", new Object[0]);
    try (ObjectOutputStream out = new ObjectOutputStream(outputStream)) {
      out.writeObject(obj);
    } catch (IOException ex) {
      throw new SerializationException(ex);
    } 
  }
  
  public static byte[] serialize(Serializable obj) {
    ByteArrayOutputStream baos = new ByteArrayOutputStream(512);
    serialize(obj, baos);
    return baos.toByteArray();
  }
  
  public static <T> T deserialize(InputStream inputStream) {
    Validate.isTrue((inputStream != null), "The InputStream must not be null", new Object[0]);
    try (ObjectInputStream in = new ObjectInputStream(inputStream)) {
      T obj = (T)in.readObject();
      return obj;
    } catch (ClassNotFoundException|IOException ex) {
      throw new SerializationException(ex);
    } 
  }
  
  public static <T> T deserialize(byte[] objectData) {
    Validate.isTrue((objectData != null), "The byte[] must not be null", new Object[0]);
    return deserialize(new ByteArrayInputStream(objectData));
  }
  
  static class ClassLoaderAwareObjectInputStream extends ObjectInputStream {
    private static final Map<String, Class<?>> primitiveTypes = new HashMap<>();
    
    private final ClassLoader classLoader;
    
    static {
      primitiveTypes.put("byte", byte.class);
      primitiveTypes.put("short", short.class);
      primitiveTypes.put("int", int.class);
      primitiveTypes.put("long", long.class);
      primitiveTypes.put("float", float.class);
      primitiveTypes.put("double", double.class);
      primitiveTypes.put("boolean", boolean.class);
      primitiveTypes.put("char", char.class);
      primitiveTypes.put("void", void.class);
    }
    
    ClassLoaderAwareObjectInputStream(InputStream in, ClassLoader classLoader) throws IOException {
      super(in);
      this.classLoader = classLoader;
    }
    
    protected Class<?> resolveClass(ObjectStreamClass desc) throws IOException, ClassNotFoundException {
      String name = desc.getName();
      try {
        return Class.forName(name, false, this.classLoader);
      } catch (ClassNotFoundException ex) {
        try {
          return Class.forName(name, false, Thread.currentThread().getContextClassLoader());
        } catch (ClassNotFoundException cnfe) {
          Class<?> cls = primitiveTypes.get(name);
          if (cls != null)
            return cls; 
          throw cnfe;
        } 
      } 
    }
  }
}
