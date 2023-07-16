package org.jline.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public final class ShutdownHooks {
  private static final List<Task> tasks = new ArrayList<>();
  
  private static Thread hook;
  
  public static synchronized <T extends Task> T add(T task) {
    Objects.requireNonNull(task);
    if (hook == null)
      hook = addHook(new Thread("JLine Shutdown Hook") {
            public void run() {
              ShutdownHooks.runTasks();
            }
          }); 
    Log.debug(new Object[] { "Adding shutdown-hook task: ", task });
    tasks.add((Task)task);
    return task;
  }
  
  public static interface Task {
    void run() throws Exception;
  }
  
  private static synchronized void runTasks() {
    Log.debug(new Object[] { "Running all shutdown-hook tasks" });
    for (Task task : (Task[])tasks.<Task>toArray(new Task[tasks.size()])) {
      Log.debug(new Object[] { "Running task: ", task });
      try {
        task.run();
      } catch (Throwable e) {
        Log.warn(new Object[] { "Task failed", e });
      } 
    } 
    tasks.clear();
  }
  
  private static Thread addHook(Thread thread) {
    Log.debug(new Object[] { "Registering shutdown-hook: ", thread });
    Runtime.getRuntime().addShutdownHook(thread);
    return thread;
  }
  
  public static synchronized void remove(Task task) {
    Objects.requireNonNull(task);
    if (hook == null)
      return; 
    tasks.remove(task);
    if (tasks.isEmpty()) {
      removeHook(hook);
      hook = null;
    } 
  }
  
  private static void removeHook(Thread thread) {
    Log.debug(new Object[] { "Removing shutdown-hook: ", thread });
    try {
      Runtime.getRuntime().removeShutdownHook(thread);
    } catch (IllegalStateException illegalStateException) {}
  }
}
