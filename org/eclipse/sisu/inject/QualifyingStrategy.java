package org.eclipse.sisu.inject;

import com.google.inject.Binding;
import com.google.inject.Key;
import com.google.inject.name.Named;
import com.google.inject.name.Names;
import com.google.inject.spi.ProviderKeyBinding;
import java.lang.annotation.Annotation;
import javax.inject.Named;
import javax.inject.Provider;

enum QualifyingStrategy {
  UNRESTRICTED {
    final Annotation qualifies(Key<?> requirement, Binding<?> binding) {
      Annotation qualifier = null.qualify(binding.getKey());
      return (qualifier != null) ? qualifier : BLANK_QUALIFIER;
    }
  },
  NAMED {
    final Annotation qualifies(Key<?> requirement, Binding<?> binding) {
      Annotation qualifier = null.qualify(binding.getKey());
      return (qualifier instanceof Named) ? qualifier : null;
    }
  },
  NAMED_WITH_ATTRIBUTES {
    final Annotation qualifies(Key<?> requirement, Binding<?> binding) {
      Annotation qualifier = null.qualify(binding.getKey());
      if (requirement.getAnnotation().equals(qualifier))
        return qualifier; 
      if (binding instanceof com.google.inject.spi.ConstructorBinding && binding.getKey().getAnnotationType() == null) {
        Class<?> clazz = binding.getKey().getTypeLiteral().getRawType();
        Named alias = clazz.<Named>getAnnotation(Named.class);
        if (alias != null && alias.value().equals(((Named)requirement.getAnnotation()).value()) && 
          clazz.equals(Implementations.find(binding)))
          return requirement.getAnnotation(); 
      } 
      return null;
    }
  },
  MARKED {
    final Annotation qualifies(Key<?> requirement, Binding<?> binding) {
      final Class<? extends Annotation> markerType = requirement.getAnnotationType();
      Annotation qualifier = null.qualify(binding.getKey());
      if (markerType.isInstance(qualifier))
        return qualifier; 
      if (markerType.equals(binding.getKey().getAnnotationType()) && (
        markerType.getDeclaredMethods()).length == 0)
        return new Annotation() {
            public Class<? extends Annotation> annotationType() {
              return markerType;
            }
          }; 
      if (binding instanceof ProviderKeyBinding) {
        Key<?> providerKey = ((ProviderKeyBinding)binding).getProviderKey();
        return (Annotation)providerKey.getTypeLiteral().getRawType().getAnnotation(markerType);
      } 
      Class<?> implementation = Implementations.find(binding);
      return (implementation != null) ? implementation.<Annotation>getAnnotation((Class)markerType) : null;
    }
  },
  MARKED_WITH_ATTRIBUTES {
    final Annotation qualifies(Key<?> requirement, Binding<?> binding) {
      Annotation qualifier = MARKED.qualifies(requirement, binding);
      return requirement.getAnnotation().equals(qualifier) ? qualifier : null;
    }
  };
  
  static final Annotation DEFAULT_QUALIFIER;
  
  static final Annotation BLANK_QUALIFIER;
  
  static {
    DEFAULT_QUALIFIER = (Annotation)Names.named("default");
    BLANK_QUALIFIER = (Annotation)Names.named("");
  }
  
  static final QualifyingStrategy selectFor(Key<?> key) {
    Class<?> qualifierType = key.getAnnotationType();
    if (qualifierType == null)
      return UNRESTRICTED; 
    if (Named.class == qualifierType)
      return key.hasAttributes() ? NAMED_WITH_ATTRIBUTES : NAMED; 
    return key.hasAttributes() ? MARKED_WITH_ATTRIBUTES : MARKED;
  }
  
  static final Annotation qualify(Key<?> key) {
    if (key.getAnnotationType() == null)
      return DEFAULT_QUALIFIER; 
    Annotation qualifier = key.getAnnotation();
    if (qualifier instanceof Provider) {
      Object original = ((Provider)qualifier).get();
      return (original instanceof Annotation) ? (Annotation)original : DEFAULT_QUALIFIER;
    } 
    return qualifier;
  }
  
  abstract Annotation qualifies(Key<?> paramKey, Binding<?> paramBinding);
}
