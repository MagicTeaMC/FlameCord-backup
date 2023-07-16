package org.eclipse.sisu.inject;

import com.google.inject.Binding;
import com.google.inject.Provider;
import com.google.inject.Scopes;
import com.google.inject.spi.ElementSource;
import com.google.inject.spi.ProviderInstanceBinding;
import javax.inject.Provider;

public final class Guice4 {
  private static final boolean HAS_DECLARING_SOURCE;
  
  private static final boolean HAS_USER_SUPPLIED_PROVIDER;
  
  private static final boolean HAS_OLD_SCOPES_SINGLETON;
  
  static {
    boolean hasDeclaringSource, hasUserSuppliedProvider, hasOldScopesSingleton;
    try {
      hasDeclaringSource = (ElementSource.class.getMethod("getDeclaringSource", new Class[0]) != null);
    } catch (Exception exception) {
      hasDeclaringSource = false;
    } catch (LinkageError linkageError) {
      hasDeclaringSource = false;
    } 
    HAS_DECLARING_SOURCE = hasDeclaringSource;
    try {
      hasUserSuppliedProvider = (ProviderInstanceBinding.class.getMethod("getUserSuppliedProvider", new Class[0]) != null);
    } catch (Exception exception) {
      hasUserSuppliedProvider = false;
    } catch (LinkageError linkageError) {
      hasUserSuppliedProvider = false;
    } 
    HAS_USER_SUPPLIED_PROVIDER = hasUserSuppliedProvider;
    try {
      hasOldScopesSingleton = (Scopes.class.equals(Scopes.SINGLETON.getClass().getEnclosingClass()) && 
        Scopes.SINGLETON.scope(null, null) != null);
    } catch (Exception exception) {
      hasOldScopesSingleton = false;
    } catch (LinkageError linkageError) {
      hasOldScopesSingleton = false;
    } 
    HAS_OLD_SCOPES_SINGLETON = hasOldScopesSingleton;
  }
  
  static final Object NIL = new Object();
  
  public static Object getDeclaringSource(Binding<?> binding) {
    Object source = binding.getSource();
    if (HAS_DECLARING_SOURCE && source instanceof ElementSource)
      return ((ElementSource)source).getDeclaringSource(); 
    return source;
  }
  
  public static Provider<?> getProviderInstance(ProviderInstanceBinding<?> binding) {
    return HAS_USER_SUPPLIED_PROVIDER ? binding.getUserSuppliedProvider() : (Provider<?>)binding.getProviderInstance();
  }
  
  public static <T> Provider<T> lazy(Binding<T> binding) {
    if (HAS_OLD_SCOPES_SINGLETON)
      return (Provider<T>)Scopes.SINGLETON.scope(binding.getKey(), binding.getProvider()); 
    final Provider provider = binding.getProvider();
    return new Provider<T>() {
        private volatile Object value = Guice4.NIL;
        
        public T get() {
          if (Guice4.NIL == this.value)
            synchronized (this) {
              if (Guice4.NIL == this.value)
                this.value = provider.get(); 
            }  
          return (T)this.value;
        }
      };
  }
}
