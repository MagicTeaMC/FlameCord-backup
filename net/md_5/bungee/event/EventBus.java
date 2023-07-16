package net.md_5.bungee.event;

import com.google.common.collect.ImmutableSet;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Level;
import java.util.logging.Logger;

public class EventBus {
  private final Map<Class<?>, Map<Byte, Map<Object, Method[]>>> byListenerAndPriority = new HashMap<>();
  
  private final Map<Class<?>, EventHandlerMethod[]> byEventBaked = (Map)new ConcurrentHashMap<>();
  
  private final Lock lock = new ReentrantLock();
  
  private final Logger logger;
  
  public EventBus() {
    this(null);
  }
  
  public EventBus(Logger logger) {
    this.logger = (logger == null) ? Logger.getLogger("global") : logger;
  }
  
  public <T> void post(T event, EventExceptionHandler<T> exceptionHandler) {
    EventHandlerMethod[] handlers = this.byEventBaked.get(event.getClass());
    if (handlers != null)
      for (EventHandlerMethod method : handlers) {
        long start = System.nanoTime();
        try {
          method.invoke(event);
        } catch (IllegalAccessException ex) {
          throw new Error("Method became inaccessible: " + event, ex);
        } catch (IllegalArgumentException ex) {
          throw new Error("Method rejected target/argument: " + event, ex);
        } catch (InvocationTargetException ex) {
          String msg = MessageFormat.format("Error dispatching event {0} to listener {1}", new Object[] { event, method.getListener() });
          this.logger.log(Level.WARNING, msg, ex.getCause());
          if (exceptionHandler != null)
            exceptionHandler.handleEventException(msg, event, method, ex); 
        } 
        long elapsed = System.nanoTime() - start;
        if (elapsed > 50000000L)
          this.logger.log(Level.WARNING, "Plugin listener {0} took {1}ms to process event {2}!", new Object[] { method
                
                .getListener().getClass().getName(), Long.valueOf(elapsed / 1000000L), event }); 
      }  
  }
  
  private Map<Class<?>, Map<Byte, Set<Method>>> findHandlers(Object listener) {
    Map<Class<?>, Map<Byte, Set<Method>>> handler = new HashMap<>();
    ImmutableSet immutableSet = ImmutableSet.builder().add((Object[])listener.getClass().getMethods()).add((Object[])listener.getClass().getDeclaredMethods()).build();
    for (Method m : immutableSet) {
      EventHandler annotation = m.<EventHandler>getAnnotation(EventHandler.class);
      if (annotation != null) {
        Class<?>[] params = m.getParameterTypes();
        if (params.length != 1) {
          this.logger.log(Level.INFO, "Method {0} in class {1} annotated with {2} does not have single argument", new Object[] { m, listener
                
                .getClass(), annotation });
          continue;
        } 
        Map<Byte, Set<Method>> prioritiesMap = handler.computeIfAbsent(params[0], k -> new HashMap<>());
        Set<Method> priority = prioritiesMap.computeIfAbsent(Byte.valueOf(annotation.priority()), k -> new HashSet());
        priority.add(m);
      } 
    } 
    return handler;
  }
  
  public void register(Object listener) {
    Map<Class<?>, Map<Byte, Set<Method>>> handler = findHandlers(listener);
    this.lock.lock();
    try {
      for (Map.Entry<Class<?>, Map<Byte, Set<Method>>> e : handler.entrySet()) {
        Map<Byte, Map<Object, Method[]>> prioritiesMap = this.byListenerAndPriority.computeIfAbsent(e.getKey(), k -> new HashMap<>());
        for (Map.Entry<Byte, Set<Method>> entry : (Iterable<Map.Entry<Byte, Set<Method>>>)((Map)e.getValue()).entrySet()) {
          Map<Object, Method[]> currentPriorityMap = prioritiesMap.computeIfAbsent(entry.getKey(), k -> new HashMap<>());
          currentPriorityMap.put(listener, (Method[])((Set)entry.getValue()).toArray((Object[])new Method[0]));
        } 
        bakeHandlers(e.getKey());
      } 
    } finally {
      this.lock.unlock();
    } 
  }
  
  public void unregister(Object listener) {
    Map<Class<?>, Map<Byte, Set<Method>>> handler = findHandlers(listener);
    this.lock.lock();
    try {
      for (Map.Entry<Class<?>, Map<Byte, Set<Method>>> e : handler.entrySet()) {
        Map<Byte, Map<Object, Method[]>> prioritiesMap = this.byListenerAndPriority.get(e.getKey());
        if (prioritiesMap != null) {
          for (Byte priority : ((Map)e.getValue()).keySet()) {
            Map<Object, Method[]> currentPriority = prioritiesMap.get(priority);
            if (currentPriority != null) {
              currentPriority.remove(listener);
              if (currentPriority.isEmpty())
                prioritiesMap.remove(priority); 
            } 
          } 
          if (prioritiesMap.isEmpty())
            this.byListenerAndPriority.remove(e.getKey()); 
        } 
        bakeHandlers(e.getKey());
      } 
    } finally {
      this.lock.unlock();
    } 
  }
  
  private void bakeHandlers(Class<?> eventClass) {
    Map<Byte, Map<Object, Method[]>> handlersByPriority = this.byListenerAndPriority.get(eventClass);
    if (handlersByPriority != null) {
      List<EventHandlerMethod> handlersList = new ArrayList<>(handlersByPriority.size() * 2);
      byte value = Byte.MIN_VALUE;
      while (true) {
        Map<Object, Method[]> handlersByListener = handlersByPriority.get(Byte.valueOf(value));
        if (handlersByListener != null)
          for (Map.Entry<Object, Method[]> listenerHandlers : handlersByListener.entrySet()) {
            for (Method method : (Method[])listenerHandlers.getValue()) {
              EventHandlerMethod ehm = new EventHandlerMethod(listenerHandlers.getKey(), method);
              handlersList.add(ehm);
            } 
          }  
        value = (byte)(value + 1);
        if (value >= Byte.MAX_VALUE) {
          this.byEventBaked.put(eventClass, handlersList.toArray(new EventHandlerMethod[0]));
          return;
        } 
      } 
    } 
    this.byEventBaked.remove(eventClass);
  }
}