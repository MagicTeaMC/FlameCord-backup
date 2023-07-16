package org.eclipse.sisu.inject;

import com.google.inject.Binding;
import com.google.inject.servlet.InstanceFilterBinding;
import com.google.inject.servlet.InstanceServletBinding;
import com.google.inject.servlet.LinkedFilterBinding;
import com.google.inject.servlet.LinkedServletBinding;
import com.google.inject.servlet.ServletModuleTargetVisitor;
import com.google.inject.spi.BindingTargetVisitor;
import com.google.inject.spi.ConstructorBinding;
import com.google.inject.spi.DefaultBindingTargetVisitor;
import com.google.inject.spi.ExposedBinding;
import com.google.inject.spi.InstanceBinding;
import com.google.inject.spi.LinkedKeyBinding;
import com.google.inject.spi.ProviderInstanceBinding;
import com.google.inject.spi.UntargettedBinding;
import javax.annotation.Priority;
import javax.inject.Provider;
import org.eclipse.sisu.Description;
import org.eclipse.sisu.Priority;
import org.sonatype.inject.Description;

final class Implementations {
  private static final boolean HAS_GUICE_SERVLET;
  
  private static final boolean HAS_JSR250_PRIORITY;
  
  static {
    boolean hasGuiceServlet, hasJsr250Priority;
    try {
      hasGuiceServlet = BindingTargetVisitor.class.isInstance(ServletFinder.THIS);
    } catch (LinkageError linkageError) {
      hasGuiceServlet = false;
    } 
    HAS_GUICE_SERVLET = hasGuiceServlet;
    try {
      hasJsr250Priority = Priority.class.isAnnotation();
    } catch (LinkageError linkageError) {
      hasJsr250Priority = false;
    } 
    HAS_JSR250_PRIORITY = hasJsr250Priority;
  }
  
  public static Class<?> find(Binding<?> binding) {
    return (Class)binding.acceptTargetVisitor(ClassFinder.THIS);
  }
  
  public static <T extends java.lang.annotation.Annotation> T getAnnotation(Binding<?> binding, Class<T> annotationType) {
    boolean isPriority = Priority.class.equals(annotationType);
    Class<?> implementation = 
      (Class)binding.acceptTargetVisitor((HAS_GUICE_SERVLET && isPriority) ? ServletFinder.THIS : ClassFinder.THIS);
    T annotation = null;
    if (implementation != null) {
      annotation = implementation.getAnnotation(annotationType);
      if (annotation == null)
        if (HAS_JSR250_PRIORITY && isPriority) {
          annotation = adaptJsr250(binding, implementation);
        } else if (Description.class.equals(annotationType)) {
          annotation = adaptLegacy(binding, implementation);
        }  
    } 
    return annotation;
  }
  
  private static <T extends java.lang.annotation.Annotation> T adaptJsr250(Binding<?> binding, Class<?> clazz) {
    Priority jsr250 = clazz.<Priority>getAnnotation(Priority.class);
    return (jsr250 != null) ? (T)new PrioritySource(binding.getSource(), jsr250.value()) : null;
  }
  
  private static <T extends java.lang.annotation.Annotation> T adaptLegacy(Binding<?> binding, Class<?> clazz) {
    Description legacy = clazz.<Description>getAnnotation(Description.class);
    return (legacy != null) ? (T)new DescriptionSource(binding.getSource(), legacy.value()) : null;
  }
  
  static class ClassFinder extends DefaultBindingTargetVisitor<Object, Class<?>> {
    static final BindingTargetVisitor<Object, Class<?>> THIS = (BindingTargetVisitor<Object, Class<?>>)new ClassFinder();
    
    public Class<?> visit(UntargettedBinding<?> binding) {
      return binding.getKey().getTypeLiteral().getRawType();
    }
    
    public Class<?> visit(LinkedKeyBinding<?> binding) {
      return binding.getLinkedKey().getTypeLiteral().getRawType();
    }
    
    public Class<?> visit(ConstructorBinding<?> binding) {
      return binding.getConstructor().getDeclaringType().getRawType();
    }
    
    public Class<?> visit(InstanceBinding<?> binding) {
      return binding.getInstance().getClass();
    }
    
    public Class<?> visit(ProviderInstanceBinding<?> binding) {
      Provider<?> provider = Guice4.getProviderInstance(binding);
      if (provider instanceof DeferredProvider)
        try {
          return ((DeferredProvider)provider).getImplementationClass().load();
        } catch (TypeNotPresentException typeNotPresentException) {} 
      return null;
    }
    
    public Class<?> visit(ExposedBinding<?> binding) {
      return (Class)binding.getPrivateElements().getInjector().getBinding(binding.getKey()).acceptTargetVisitor((BindingTargetVisitor)this);
    }
  }
  
  static final class ServletFinder extends ClassFinder implements ServletModuleTargetVisitor<Object, Class<?>> {
    static final BindingTargetVisitor<Object, Class<?>> THIS = (BindingTargetVisitor<Object, Class<?>>)new ServletFinder();
    
    public Class<?> visit(InstanceFilterBinding binding) {
      return binding.getFilterInstance().getClass();
    }
    
    public Class<?> visit(InstanceServletBinding binding) {
      return binding.getServletInstance().getClass();
    }
    
    public Class<?> visit(LinkedFilterBinding binding) {
      return binding.getLinkedKey().getTypeLiteral().getRawType();
    }
    
    public Class<?> visit(LinkedServletBinding binding) {
      return binding.getLinkedKey().getTypeLiteral().getRawType();
    }
  }
}
