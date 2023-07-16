package com.google.gson.internal;

import com.google.gson.InstanceCreator;
import com.google.gson.JsonIOException;
import com.google.gson.ReflectionAccessFilter;
import com.google.gson.internal.reflect.ReflectionHelper;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ConcurrentNavigableMap;
import java.util.concurrent.ConcurrentSkipListMap;

public final class ConstructorConstructor {
  private final Map<Type, InstanceCreator<?>> instanceCreators;
  
  private final boolean useJdkUnsafe;
  
  private final List<ReflectionAccessFilter> reflectionFilters;
  
  public ConstructorConstructor(Map<Type, InstanceCreator<?>> instanceCreators, boolean useJdkUnsafe, List<ReflectionAccessFilter> reflectionFilters) {
    this.instanceCreators = instanceCreators;
    this.useJdkUnsafe = useJdkUnsafe;
    this.reflectionFilters = reflectionFilters;
  }
  
  static String checkInstantiable(Class<?> c) {
    int modifiers = c.getModifiers();
    if (Modifier.isInterface(modifiers))
      return "Interfaces can't be instantiated! Register an InstanceCreator or a TypeAdapter for this type. Interface name: " + c
        .getName(); 
    if (Modifier.isAbstract(modifiers))
      return "Abstract classes can't be instantiated! Register an InstanceCreator or a TypeAdapter for this type. Class name: " + c
        .getName(); 
    return null;
  }
  
  public <T> ObjectConstructor<T> get(TypeToken<T> typeToken) {
    final Type type = typeToken.getType();
    Class<? super T> rawType = typeToken.getRawType();
    final InstanceCreator<T> typeCreator = (InstanceCreator<T>)this.instanceCreators.get(type);
    if (typeCreator != null)
      return new ObjectConstructor<T>() {
          public T construct() {
            return (T)typeCreator.createInstance(type);
          }
        }; 
    final InstanceCreator<T> rawTypeCreator = (InstanceCreator<T>)this.instanceCreators.get(rawType);
    if (rawTypeCreator != null)
      return new ObjectConstructor<T>() {
          public T construct() {
            return (T)rawTypeCreator.createInstance(type);
          }
        }; 
    ObjectConstructor<T> specialConstructor = newSpecialCollectionConstructor(type, rawType);
    if (specialConstructor != null)
      return specialConstructor; 
    ReflectionAccessFilter.FilterResult filterResult = ReflectionAccessFilterHelper.getFilterResult(this.reflectionFilters, rawType);
    ObjectConstructor<T> defaultConstructor = newDefaultConstructor(rawType, filterResult);
    if (defaultConstructor != null)
      return defaultConstructor; 
    ObjectConstructor<T> defaultImplementation = newDefaultImplementationConstructor(type, rawType);
    if (defaultImplementation != null)
      return defaultImplementation; 
    final String exceptionMessage = checkInstantiable(rawType);
    if (exceptionMessage != null)
      return new ObjectConstructor<T>() {
          public T construct() {
            throw new JsonIOException(exceptionMessage);
          }
        }; 
    if (filterResult == ReflectionAccessFilter.FilterResult.ALLOW)
      return newUnsafeAllocator(rawType); 
    final String message = "Unable to create instance of " + rawType + "; ReflectionAccessFilter does not permit using reflection or Unsafe. Register an InstanceCreator or a TypeAdapter for this type or adjust the access filter to allow using reflection.";
    return new ObjectConstructor<T>() {
        public T construct() {
          throw new JsonIOException(message);
        }
      };
  }
  
  private static <T> ObjectConstructor<T> newSpecialCollectionConstructor(final Type type, Class<? super T> rawType) {
    if (EnumSet.class.isAssignableFrom(rawType))
      return new ObjectConstructor<T>() {
          public T construct() {
            if (type instanceof ParameterizedType) {
              Type elementType = ((ParameterizedType)type).getActualTypeArguments()[0];
              if (elementType instanceof Class)
                return (T)EnumSet.noneOf((Class<Enum>)elementType); 
              throw new JsonIOException("Invalid EnumSet type: " + type.toString());
            } 
            throw new JsonIOException("Invalid EnumSet type: " + type.toString());
          }
        }; 
    if (rawType == EnumMap.class)
      return new ObjectConstructor<T>() {
          public T construct() {
            if (type instanceof ParameterizedType) {
              Type elementType = ((ParameterizedType)type).getActualTypeArguments()[0];
              if (elementType instanceof Class)
                return (T)new EnumMap<>((Class<Enum>)elementType); 
              throw new JsonIOException("Invalid EnumMap type: " + type.toString());
            } 
            throw new JsonIOException("Invalid EnumMap type: " + type.toString());
          }
        }; 
    return null;
  }
  
  private static <T> ObjectConstructor<T> newDefaultConstructor(Class<? super T> rawType, ReflectionAccessFilter.FilterResult filterResult) {
    final Constructor<? super T> constructor;
    if (Modifier.isAbstract(rawType.getModifiers()))
      return null; 
    try {
      constructor = rawType.getDeclaredConstructor(new Class[0]);
    } catch (NoSuchMethodException e) {
      return null;
    } 
    boolean canAccess = (filterResult == ReflectionAccessFilter.FilterResult.ALLOW || (ReflectionAccessFilterHelper.canAccess(constructor, null) && (filterResult != ReflectionAccessFilter.FilterResult.BLOCK_ALL || Modifier.isPublic(constructor.getModifiers()))));
    if (!canAccess) {
      final String message = "Unable to invoke no-args constructor of " + rawType + "; constructor is not accessible and ReflectionAccessFilter does not permit making it accessible. Register an InstanceCreator or a TypeAdapter for this type, change the visibility of the constructor or adjust the access filter.";
      return new ObjectConstructor<T>() {
          public T construct() {
            throw new JsonIOException(message);
          }
        };
    } 
    if (filterResult == ReflectionAccessFilter.FilterResult.ALLOW) {
      final String exceptionMessage = ReflectionHelper.tryMakeAccessible(constructor);
      if (exceptionMessage != null)
        return new ObjectConstructor<T>() {
            public T construct() {
              throw new JsonIOException(exceptionMessage);
            }
          }; 
    } 
    return new ObjectConstructor<T>() {
        public T construct() {
          try {
            T newInstance = constructor.newInstance(new Object[0]);
            return newInstance;
          } catch (InstantiationException e) {
            throw new RuntimeException("Failed to invoke constructor '" + ReflectionHelper.constructorToString(constructor) + "' with no args", e);
          } catch (InvocationTargetException e) {
            throw new RuntimeException("Failed to invoke constructor '" + ReflectionHelper.constructorToString(constructor) + "' with no args", e
                .getCause());
          } catch (IllegalAccessException e) {
            throw ReflectionHelper.createExceptionForUnexpectedIllegalAccess(e);
          } 
        }
      };
  }
  
  private static <T> ObjectConstructor<T> newDefaultImplementationConstructor(Type type, Class<? super T> rawType) {
    if (Collection.class.isAssignableFrom(rawType)) {
      if (SortedSet.class.isAssignableFrom(rawType))
        return new ObjectConstructor<T>() {
            public T construct() {
              return (T)new TreeSet();
            }
          }; 
      if (Set.class.isAssignableFrom(rawType))
        return new ObjectConstructor<T>() {
            public T construct() {
              return (T)new LinkedHashSet();
            }
          }; 
      if (Queue.class.isAssignableFrom(rawType))
        return new ObjectConstructor<T>() {
            public T construct() {
              return (T)new ArrayDeque();
            }
          }; 
      return new ObjectConstructor<T>() {
          public T construct() {
            return (T)new ArrayList();
          }
        };
    } 
    if (Map.class.isAssignableFrom(rawType)) {
      if (ConcurrentNavigableMap.class.isAssignableFrom(rawType))
        return new ObjectConstructor<T>() {
            public T construct() {
              return (T)new ConcurrentSkipListMap<>();
            }
          }; 
      if (ConcurrentMap.class.isAssignableFrom(rawType))
        return new ObjectConstructor<T>() {
            public T construct() {
              return (T)new ConcurrentHashMap<>();
            }
          }; 
      if (SortedMap.class.isAssignableFrom(rawType))
        return new ObjectConstructor<T>() {
            public T construct() {
              return (T)new TreeMap<>();
            }
          }; 
      if (type instanceof ParameterizedType && !String.class.isAssignableFrom(
          TypeToken.get(((ParameterizedType)type).getActualTypeArguments()[0]).getRawType()))
        return new ObjectConstructor<T>() {
            public T construct() {
              return (T)new LinkedHashMap<>();
            }
          }; 
      return new ObjectConstructor<T>() {
          public T construct() {
            return (T)new LinkedTreeMap<>();
          }
        };
    } 
    return null;
  }
  
  private <T> ObjectConstructor<T> newUnsafeAllocator(final Class<? super T> rawType) {
    if (this.useJdkUnsafe)
      return new ObjectConstructor<T>() {
          public T construct() {
            try {
              T newInstance = UnsafeAllocator.INSTANCE.newInstance(rawType);
              return newInstance;
            } catch (Exception e) {
              throw new RuntimeException("Unable to create instance of " + rawType + ". Registering an InstanceCreator or a TypeAdapter for this type, or adding a no-args constructor may fix this problem.", e);
            } 
          }
        }; 
    final String exceptionMessage = "Unable to create instance of " + rawType + "; usage of JDK Unsafe is disabled. Registering an InstanceCreator or a TypeAdapter for this type, adding a no-args constructor, or enabling usage of JDK Unsafe may fix this problem.";
    return new ObjectConstructor<T>() {
        public T construct() {
          throw new JsonIOException(exceptionMessage);
        }
      };
  }
  
  public String toString() {
    return this.instanceCreators.toString();
  }
}
