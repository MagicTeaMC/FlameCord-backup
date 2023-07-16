package net.md_5.bungee.event;

public interface EventExceptionHandler<T> {
  void handleEventException(String paramString, T paramT, EventHandlerMethod paramEventHandlerMethod, Throwable paramThrowable);
}
