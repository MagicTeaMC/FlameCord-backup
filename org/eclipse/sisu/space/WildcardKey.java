package org.eclipse.sisu.space;

import com.google.inject.Key;
import com.google.inject.TypeLiteral;
import java.lang.annotation.Annotation;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import javax.inject.Provider;
import javax.inject.Qualifier;

final class WildcardKey {
  private static final TypeLiteral<Object> OBJECT_TYPE_LITERAL = TypeLiteral.get(Object.class);
  
  public static Key<Object> get(Class<?> type, Annotation qualifier) {
    return Key.get(OBJECT_TYPE_LITERAL, new QualifiedImpl(type, qualifier));
  }
  
  @Qualifier
  @Retention(RetentionPolicy.RUNTIME)
  private static @interface Qualified {
    Class<?> value();
  }
  
  private static final class QualifiedImpl implements Qualified, Provider<Annotation> {
    private final Class<?> value;
    
    private final Annotation qualifier;
    
    QualifiedImpl(Class<?> value, Annotation qualifier) {
      this.value = value;
      this.qualifier = qualifier;
    }
    
    public Class<?> value() {
      return this.value;
    }
    
    public Annotation get() {
      return this.qualifier;
    }
    
    public Class<? extends Annotation> annotationType() {
      return (Class)WildcardKey.Qualified.class;
    }
    
    public int hashCode() {
      return this.value.hashCode();
    }
    
    public boolean equals(Object rhs) {
      if (this == rhs)
        return true; 
      if (rhs instanceof QualifiedImpl)
        return (this.value == ((QualifiedImpl)rhs).value); 
      return false;
    }
    
    public String toString() {
      return "*";
    }
  }
}
