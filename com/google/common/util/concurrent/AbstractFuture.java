package com.google.common.util.concurrent;

import com.google.common.annotations.Beta;
import com.google.common.annotations.GwtCompatible;
import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import com.google.common.base.Throwables;
import com.google.common.util.concurrent.internal.InternalFutureFailureAccess;
import com.google.common.util.concurrent.internal.InternalFutures;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import com.google.errorprone.annotations.ForOverride;
import com.google.j2objc.annotations.ReflectionSupport;
import java.lang.reflect.Field;
import java.security.AccessController;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.util.Locale;
import java.util.Objects;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicReferenceFieldUpdater;
import java.util.concurrent.locks.LockSupport;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.CheckForNull;
import sun.misc.Unsafe;

@ElementTypesAreNonnullByDefault
@GwtCompatible(emulated = true)
@ReflectionSupport(ReflectionSupport.Level.FULL)
public abstract class AbstractFuture<V> extends InternalFutureFailureAccess implements ListenableFuture<V> {
  static final boolean GENERATE_CANCELLATION_CAUSES;
  
  static {
    boolean generateCancellationCauses;
    AtomicHelper helper;
    try {
      generateCancellationCauses = Boolean.parseBoolean(
          System.getProperty("guava.concurrent.generate_cancellation_cause", "false"));
    } catch (SecurityException e) {
      generateCancellationCauses = false;
    } 
    GENERATE_CANCELLATION_CAUSES = generateCancellationCauses;
  }
  
  static interface Trusted<V> extends ListenableFuture<V> {}
  
  static abstract class TrustedFuture<V> extends AbstractFuture<V> implements Trusted<V> {
    @ParametricNullness
    @CanIgnoreReturnValue
    public final V get() throws InterruptedException, ExecutionException {
      return super.get();
    }
    
    @ParametricNullness
    @CanIgnoreReturnValue
    public final V get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
      return super.get(timeout, unit);
    }
    
    public final boolean isDone() {
      return super.isDone();
    }
    
    public final boolean isCancelled() {
      return super.isCancelled();
    }
    
    public final void addListener(Runnable listener, Executor executor) {
      super.addListener(listener, executor);
    }
    
    @CanIgnoreReturnValue
    public final boolean cancel(boolean mayInterruptIfRunning) {
      return super.cancel(mayInterruptIfRunning);
    }
  }
  
  private static final Logger log = Logger.getLogger(AbstractFuture.class.getName());
  
  private static final long SPIN_THRESHOLD_NANOS = 1000L;
  
  private static final AtomicHelper ATOMIC_HELPER;
  
  static {
    Throwable thrownUnsafeFailure = null;
    Throwable thrownAtomicReferenceFieldUpdaterFailure = null;
    try {
      helper = new UnsafeAtomicHelper();
    } catch (Throwable unsafeFailure) {
      thrownUnsafeFailure = unsafeFailure;
      try {
        helper = new SafeAtomicHelper(AtomicReferenceFieldUpdater.newUpdater(Waiter.class, Thread.class, "thread"), AtomicReferenceFieldUpdater.newUpdater(Waiter.class, Waiter.class, "next"), AtomicReferenceFieldUpdater.newUpdater(AbstractFuture.class, Waiter.class, "waiters"), AtomicReferenceFieldUpdater.newUpdater(AbstractFuture.class, Listener.class, "listeners"), AtomicReferenceFieldUpdater.newUpdater(AbstractFuture.class, Object.class, "value"));
      } catch (Throwable atomicReferenceFieldUpdaterFailure) {
        thrownAtomicReferenceFieldUpdaterFailure = atomicReferenceFieldUpdaterFailure;
        helper = new SynchronizedHelper();
      } 
    } 
    ATOMIC_HELPER = helper;
    Class<?> ensureLoaded = LockSupport.class;
    if (thrownAtomicReferenceFieldUpdaterFailure != null) {
      log.log(Level.SEVERE, "UnsafeAtomicHelper is broken!", thrownUnsafeFailure);
      log.log(Level.SEVERE, "SafeAtomicHelper is broken!", thrownAtomicReferenceFieldUpdaterFailure);
    } 
  }
  
  private static final class Waiter {
    static final Waiter TOMBSTONE = new Waiter(false);
    
    @CheckForNull
    volatile Thread thread;
    
    @CheckForNull
    volatile Waiter next;
    
    Waiter(boolean unused) {}
    
    Waiter() {
      AbstractFuture.ATOMIC_HELPER.putThread(this, Thread.currentThread());
    }
    
    void setNext(@CheckForNull Waiter next) {
      AbstractFuture.ATOMIC_HELPER.putNext(this, next);
    }
    
    void unpark() {
      Thread w = this.thread;
      if (w != null) {
        this.thread = null;
        LockSupport.unpark(w);
      } 
    }
  }
  
  private void removeWaiter(Waiter node) {
    node.thread = null;
    label22: while (true) {
      Waiter pred = null;
      Waiter curr = this.waiters;
      if (curr == Waiter.TOMBSTONE)
        return; 
      while (curr != null) {
        Waiter succ = curr.next;
        if (curr.thread != null) {
          pred = curr;
        } else if (pred != null) {
          pred.next = succ;
          if (pred.thread == null)
            continue label22; 
        } else if (!ATOMIC_HELPER.casWaiters(this, curr, succ)) {
          continue label22;
        } 
        curr = succ;
      } 
      break;
    } 
  }
  
  private static final class Listener {
    static final Listener TOMBSTONE = new Listener();
    
    @CheckForNull
    final Runnable task;
    
    @CheckForNull
    final Executor executor;
    
    @CheckForNull
    Listener next;
    
    Listener(Runnable task, Executor executor) {
      this.task = task;
      this.executor = executor;
    }
    
    Listener() {
      this.task = null;
      this.executor = null;
    }
  }
  
  private static final Object NULL = new Object();
  
  @CheckForNull
  private volatile Object value;
  
  @CheckForNull
  private volatile Listener listeners;
  
  @CheckForNull
  private volatile Waiter waiters;
  
  private static final class Failure {
    static final Failure FALLBACK_INSTANCE = new Failure(new Throwable("Failure occurred while trying to finish a future.") {
          public synchronized Throwable fillInStackTrace() {
            return this;
          }
        });
    
    final Throwable exception;
    
    Failure(Throwable exception) {
      this.exception = (Throwable)Preconditions.checkNotNull(exception);
    }
  }
  
  private static final class Cancellation {
    @CheckForNull
    static final Cancellation CAUSELESS_INTERRUPTED;
    
    @CheckForNull
    static final Cancellation CAUSELESS_CANCELLED;
    
    final boolean wasInterrupted;
    
    @CheckForNull
    final Throwable cause;
    
    static {
      if (AbstractFuture.GENERATE_CANCELLATION_CAUSES) {
        CAUSELESS_CANCELLED = null;
        CAUSELESS_INTERRUPTED = null;
      } else {
        CAUSELESS_CANCELLED = new Cancellation(false, null);
        CAUSELESS_INTERRUPTED = new Cancellation(true, null);
      } 
    }
    
    Cancellation(boolean wasInterrupted, @CheckForNull Throwable cause) {
      this.wasInterrupted = wasInterrupted;
      this.cause = cause;
    }
  }
  
  private static final class SetFuture<V> implements Runnable {
    final AbstractFuture<V> owner;
    
    final ListenableFuture<? extends V> future;
    
    SetFuture(AbstractFuture<V> owner, ListenableFuture<? extends V> future) {
      this.owner = owner;
      this.future = future;
    }
    
    public void run() {
      if (this.owner.value != this)
        return; 
      Object valueToSet = AbstractFuture.getFutureValue(this.future);
      if (AbstractFuture.ATOMIC_HELPER.casValue(this.owner, this, valueToSet))
        AbstractFuture.complete(this.owner); 
    }
  }
  
  @ParametricNullness
  @CanIgnoreReturnValue
  public V get(long timeout, TimeUnit unit) throws InterruptedException, TimeoutException, ExecutionException {
    long timeoutNanos = unit.toNanos(timeout);
    long remainingNanos = timeoutNanos;
    if (Thread.interrupted())
      throw new InterruptedException(); 
    Object localValue = this.value;
    if ((((localValue != null) ? 1 : 0) & (!(localValue instanceof SetFuture) ? 1 : 0)) != 0)
      return getDoneValue(localValue); 
    long endNanos = (remainingNanos > 0L) ? (System.nanoTime() + remainingNanos) : 0L;
    if (remainingNanos >= 1000L) {
      Waiter oldHead = this.waiters;
      if (oldHead != Waiter.TOMBSTONE) {
        Waiter node = new Waiter();
        label77: while (true) {
          node.setNext(oldHead);
          if (ATOMIC_HELPER.casWaiters(this, oldHead, node)) {
            do {
              OverflowAvoidingLockSupport.parkNanos(this, remainingNanos);
              if (Thread.interrupted()) {
                removeWaiter(node);
                throw new InterruptedException();
              } 
              localValue = this.value;
              if ((((localValue != null) ? 1 : 0) & (!(localValue instanceof SetFuture) ? 1 : 0)) != 0)
                return getDoneValue(localValue); 
              remainingNanos = endNanos - System.nanoTime();
            } while (remainingNanos >= 1000L);
            removeWaiter(node);
            break;
          } 
          oldHead = this.waiters;
          if (oldHead == Waiter.TOMBSTONE)
            break label77; 
        } 
      } else {
        return getDoneValue(Objects.requireNonNull(this.value));
      } 
    } 
    while (remainingNanos > 0L) {
      localValue = this.value;
      if ((((localValue != null) ? 1 : 0) & (!(localValue instanceof SetFuture) ? 1 : 0)) != 0)
        return getDoneValue(localValue); 
      if (Thread.interrupted())
        throw new InterruptedException(); 
      remainingNanos = endNanos - System.nanoTime();
    } 
    String futureToString = toString();
    String unitString = unit.toString().toLowerCase(Locale.ROOT);
    String str1 = unit.toString().toLowerCase(Locale.ROOT), message = (new StringBuilder(28 + String.valueOf(str1).length())).append("Waited ").append(timeout).append(" ").append(str1).toString();
    if (remainingNanos + 1000L < 0L) {
      message = String.valueOf(message).concat(" (plus ");
      long overWaitNanos = -remainingNanos;
      long overWaitUnits = unit.convert(overWaitNanos, TimeUnit.NANOSECONDS);
      long overWaitLeftoverNanos = overWaitNanos - unit.toNanos(overWaitUnits);
      boolean shouldShowExtraNanos = (overWaitUnits == 0L || overWaitLeftoverNanos > 1000L);
      if (overWaitUnits > 0L) {
        String str = String.valueOf(message);
        message = (new StringBuilder(21 + String.valueOf(str).length() + String.valueOf(unitString).length())).append(str).append(overWaitUnits).append(" ").append(unitString).toString();
        if (shouldShowExtraNanos)
          message = String.valueOf(message).concat(","); 
        message = String.valueOf(message).concat(" ");
      } 
      if (shouldShowExtraNanos) {
        String str = String.valueOf(message);
        message = (new StringBuilder(33 + String.valueOf(str).length())).append(str).append(overWaitLeftoverNanos).append(" nanoseconds ").toString();
      } 
      message = String.valueOf(message).concat("delay)");
    } 
    if (isDone())
      throw new TimeoutException(String.valueOf(message).concat(" but future completed as timeout expired")); 
    str1 = message;
    throw new TimeoutException((new StringBuilder(5 + String.valueOf(str1).length() + String.valueOf(futureToString).length())).append(str1).append(" for ").append(futureToString).toString());
  }
  
  @ParametricNullness
  @CanIgnoreReturnValue
  public V get() throws InterruptedException, ExecutionException {
    if (Thread.interrupted())
      throw new InterruptedException(); 
    Object localValue = this.value;
    if ((((localValue != null) ? 1 : 0) & (!(localValue instanceof SetFuture) ? 1 : 0)) != 0)
      return getDoneValue(localValue); 
    Waiter oldHead = this.waiters;
    if (oldHead != Waiter.TOMBSTONE) {
      Waiter node = new Waiter();
      do {
        node.setNext(oldHead);
        if (ATOMIC_HELPER.casWaiters(this, oldHead, node))
          while (true) {
            LockSupport.park(this);
            if (Thread.interrupted()) {
              removeWaiter(node);
              throw new InterruptedException();
            } 
            localValue = this.value;
            if ((((localValue != null) ? 1 : 0) & (!(localValue instanceof SetFuture) ? 1 : 0)) != 0)
              return getDoneValue(localValue); 
          }  
        oldHead = this.waiters;
      } while (oldHead != Waiter.TOMBSTONE);
    } 
    return getDoneValue(Objects.requireNonNull(this.value));
  }
  
  @ParametricNullness
  private V getDoneValue(Object obj) throws ExecutionException {
    if (obj instanceof Cancellation)
      throw cancellationExceptionWithCause("Task was cancelled.", ((Cancellation)obj).cause); 
    if (obj instanceof Failure)
      throw new ExecutionException(((Failure)obj).exception); 
    if (obj == NULL)
      return NullnessCasts.uncheckedNull(); 
    V asV = (V)obj;
    return asV;
  }
  
  public boolean isDone() {
    Object localValue = this.value;
    return ((localValue != null)) & (!(localValue instanceof SetFuture));
  }
  
  public boolean isCancelled() {
    Object localValue = this.value;
    return localValue instanceof Cancellation;
  }
  
  @CanIgnoreReturnValue
  public boolean cancel(boolean mayInterruptIfRunning) {
    Object localValue = this.value;
    boolean rValue = false;
    if ((((localValue == null) ? 1 : 0) | localValue instanceof SetFuture) != 0) {
      Object valueToSet = GENERATE_CANCELLATION_CAUSES ? new Cancellation(mayInterruptIfRunning, new CancellationException("Future.cancel() was called.")) : Objects.<Cancellation>requireNonNull(
          mayInterruptIfRunning ? 
          Cancellation.CAUSELESS_INTERRUPTED : 
          Cancellation.CAUSELESS_CANCELLED);
      AbstractFuture<?> abstractFuture = this;
      do {
        while (ATOMIC_HELPER.casValue(abstractFuture, localValue, valueToSet)) {
          rValue = true;
          if (mayInterruptIfRunning)
            abstractFuture.interruptTask(); 
          complete(abstractFuture);
          if (localValue instanceof SetFuture) {
            ListenableFuture<?> futureToPropagateTo = ((SetFuture)localValue).future;
            if (futureToPropagateTo instanceof Trusted) {
              AbstractFuture<?> trusted = (AbstractFuture)futureToPropagateTo;
              localValue = trusted.value;
              if ((((localValue == null) ? 1 : 0) | localValue instanceof SetFuture) != 0) {
                abstractFuture = trusted;
                continue;
              } 
              // Byte code: goto -> 193
            } 
            futureToPropagateTo.cancel(mayInterruptIfRunning);
            break;
          } 
          // Byte code: goto -> 193
        } 
        localValue = abstractFuture.value;
      } while (localValue instanceof SetFuture);
    } 
    return rValue;
  }
  
  protected void interruptTask() {}
  
  protected final boolean wasInterrupted() {
    Object localValue = this.value;
    return (localValue instanceof Cancellation && ((Cancellation)localValue).wasInterrupted);
  }
  
  public void addListener(Runnable listener, Executor executor) {
    Preconditions.checkNotNull(listener, "Runnable was null.");
    Preconditions.checkNotNull(executor, "Executor was null.");
    if (!isDone()) {
      Listener oldHead = this.listeners;
      if (oldHead != Listener.TOMBSTONE) {
        Listener newNode = new Listener(listener, executor);
        do {
          newNode.next = oldHead;
          if (ATOMIC_HELPER.casListeners(this, oldHead, newNode))
            return; 
          oldHead = this.listeners;
        } while (oldHead != Listener.TOMBSTONE);
      } 
    } 
    executeListener(listener, executor);
  }
  
  @CanIgnoreReturnValue
  protected boolean set(@ParametricNullness V value) {
    Object valueToSet = (value == null) ? NULL : value;
    if (ATOMIC_HELPER.casValue(this, null, valueToSet)) {
      complete(this);
      return true;
    } 
    return false;
  }
  
  @CanIgnoreReturnValue
  protected boolean setException(Throwable throwable) {
    Object valueToSet = new Failure((Throwable)Preconditions.checkNotNull(throwable));
    if (ATOMIC_HELPER.casValue(this, null, valueToSet)) {
      complete(this);
      return true;
    } 
    return false;
  }
  
  @CanIgnoreReturnValue
  protected boolean setFuture(ListenableFuture<? extends V> future) {
    Preconditions.checkNotNull(future);
    Object localValue = this.value;
    if (localValue == null) {
      if (future.isDone()) {
        Object value = getFutureValue(future);
        if (ATOMIC_HELPER.casValue(this, null, value)) {
          complete(this);
          return true;
        } 
        return false;
      } 
      SetFuture<V> valueToSet = new SetFuture<>(this, future);
      if (ATOMIC_HELPER.casValue(this, null, valueToSet)) {
        try {
          future.addListener(valueToSet, DirectExecutor.INSTANCE);
        } catch (Throwable t) {
          Failure failure;
          try {
            failure = new Failure(t);
          } catch (Throwable oomMostLikely) {
            failure = Failure.FALLBACK_INSTANCE;
          } 
          boolean bool = ATOMIC_HELPER.casValue(this, valueToSet, failure);
        } 
        return true;
      } 
      localValue = this.value;
    } 
    if (localValue instanceof Cancellation)
      future.cancel(((Cancellation)localValue).wasInterrupted); 
    return false;
  }
  
  private static Object getFutureValue(ListenableFuture<?> future) {
    if (future instanceof Trusted) {
      Object v = ((AbstractFuture)future).value;
      if (v instanceof Cancellation) {
        Cancellation c = (Cancellation)v;
        if (c.wasInterrupted)
          v = (c.cause != null) ? new Cancellation(false, c.cause) : Cancellation.CAUSELESS_CANCELLED; 
      } 
      return Objects.requireNonNull(v);
    } 
    if (future instanceof InternalFutureFailureAccess) {
      Throwable throwable = InternalFutures.tryInternalFastPathGetFailure((InternalFutureFailureAccess)future);
      if (throwable != null)
        return new Failure(throwable); 
    } 
    boolean wasCancelled = future.isCancelled();
    if (((!GENERATE_CANCELLATION_CAUSES ? 1 : 0) & wasCancelled) != 0)
      return Objects.requireNonNull(Cancellation.CAUSELESS_CANCELLED); 
    try {
      Object v = getUninterruptibly(future);
      if (wasCancelled) {
        String str = String.valueOf(future);
        return new Cancellation(false, new IllegalArgumentException((new StringBuilder(84 + String.valueOf(str).length())).append("get() did not throw CancellationException, despite reporting isCancelled() == true: ").append(str).toString()));
      } 
      return (v == null) ? NULL : v;
    } catch (ExecutionException exception) {
      if (wasCancelled) {
        String str = String.valueOf(future);
        return new Cancellation(false, new IllegalArgumentException((new StringBuilder(84 + String.valueOf(str).length())).append("get() did not throw CancellationException, despite reporting isCancelled() == true: ").append(str).toString(), exception));
      } 
      return new Failure(exception.getCause());
    } catch (CancellationException cancellation) {
      if (!wasCancelled) {
        String str = String.valueOf(future);
        return new Failure(new IllegalArgumentException((new StringBuilder(77 + String.valueOf(str).length())).append("get() threw CancellationException, despite reporting isCancelled() == false: ").append(str).toString(), cancellation));
      } 
      return new Cancellation(false, cancellation);
    } catch (Throwable t) {
      return new Failure(t);
    } 
  }
  
  @ParametricNullness
  private static <V> V getUninterruptibly(Future<V> future) throws ExecutionException {
    boolean interrupted = false;
    while (true) {
      try {
        return future.get();
      } catch (InterruptedException e) {
      
      } finally {
        if (interrupted)
          Thread.currentThread().interrupt(); 
      } 
    } 
  }
  
  private static void complete(AbstractFuture<?> param) {
    AbstractFuture<?> future = param;
    Listener next = null;
    label17: while (true) {
      future.releaseWaiters();
      future.afterDone();
      next = future.clearListeners(next);
      future = null;
      while (next != null) {
        Listener curr = next;
        next = next.next;
        Runnable task = Objects.<Runnable>requireNonNull(curr.task);
        if (task instanceof SetFuture) {
          SetFuture<?> setFuture = (SetFuture)task;
          future = setFuture.owner;
          if (future.value == setFuture) {
            Object valueToSet = getFutureValue(setFuture.future);
            if (ATOMIC_HELPER.casValue(future, setFuture, valueToSet))
              continue label17; 
          } 
          continue;
        } 
        executeListener(task, Objects.<Executor>requireNonNull(curr.executor));
      } 
      break;
    } 
  }
  
  @Beta
  @ForOverride
  protected void afterDone() {}
  
  @CheckForNull
  protected final Throwable tryInternalFastPathGetFailure() {
    if (this instanceof Trusted) {
      Object obj = this.value;
      if (obj instanceof Failure)
        return ((Failure)obj).exception; 
    } 
    return null;
  }
  
  final void maybePropagateCancellationTo(@CheckForNull Future<?> related) {
    if ((((related != null) ? 1 : 0) & isCancelled()) != 0)
      related.cancel(wasInterrupted()); 
  }
  
  private void releaseWaiters() {
    Waiter head = ATOMIC_HELPER.gasWaiters(this, Waiter.TOMBSTONE);
    for (Waiter currentWaiter = head; currentWaiter != null; currentWaiter = currentWaiter.next)
      currentWaiter.unpark(); 
  }
  
  @CheckForNull
  private Listener clearListeners(@CheckForNull Listener onto) {
    Listener head = ATOMIC_HELPER.gasListeners(this, Listener.TOMBSTONE);
    Listener reversedList = onto;
    while (head != null) {
      Listener tmp = head;
      head = head.next;
      tmp.next = reversedList;
      reversedList = tmp;
    } 
    return reversedList;
  }
  
  public String toString() {
    StringBuilder builder = new StringBuilder();
    if (getClass().getName().startsWith("com.google.common.util.concurrent.")) {
      builder.append(getClass().getSimpleName());
    } else {
      builder.append(getClass().getName());
    } 
    builder.append('@').append(Integer.toHexString(System.identityHashCode(this))).append("[status=");
    if (isCancelled()) {
      builder.append("CANCELLED");
    } else if (isDone()) {
      addDoneString(builder);
    } else {
      addPendingString(builder);
    } 
    return builder.append("]").toString();
  }
  
  @CheckForNull
  protected String pendingToString() {
    if (this instanceof ScheduledFuture) {
      long l = ((ScheduledFuture)this).getDelay(TimeUnit.MILLISECONDS);
      return (new StringBuilder(41)).append("remaining delay=[").append(l).append(" ms]").toString();
    } 
    return null;
  }
  
  private void addPendingString(StringBuilder builder) {
    int truncateLength = builder.length();
    builder.append("PENDING");
    Object localValue = this.value;
    if (localValue instanceof SetFuture) {
      builder.append(", setFuture=[");
      appendUserObject(builder, ((SetFuture)localValue).future);
      builder.append("]");
    } else {
      String pendingDescription;
      try {
        pendingDescription = Strings.emptyToNull(pendingToString());
      } catch (RuntimeException|StackOverflowError e) {
        String str = String.valueOf(e.getClass());
        pendingDescription = (new StringBuilder(38 + String.valueOf(str).length())).append("Exception thrown from implementation: ").append(str).toString();
      } 
      if (pendingDescription != null)
        builder.append(", info=[").append(pendingDescription).append("]"); 
    } 
    if (isDone()) {
      builder.delete(truncateLength, builder.length());
      addDoneString(builder);
    } 
  }
  
  private void addDoneString(StringBuilder builder) {
    try {
      V value = getUninterruptibly(this);
      builder.append("SUCCESS, result=[");
      appendResultObject(builder, value);
      builder.append("]");
    } catch (ExecutionException e) {
      builder.append("FAILURE, cause=[").append(e.getCause()).append("]");
    } catch (CancellationException e) {
      builder.append("CANCELLED");
    } catch (RuntimeException e) {
      builder.append("UNKNOWN, cause=[").append(e.getClass()).append(" thrown from get()]");
    } 
  }
  
  private void appendResultObject(StringBuilder builder, @CheckForNull Object o) {
    if (o == null) {
      builder.append("null");
    } else if (o == this) {
      builder.append("this future");
    } else {
      builder
        .append(o.getClass().getName())
        .append("@")
        .append(Integer.toHexString(System.identityHashCode(o)));
    } 
  }
  
  private void appendUserObject(StringBuilder builder, @CheckForNull Object o) {
    try {
      if (o == this) {
        builder.append("this future");
      } else {
        builder.append(o);
      } 
    } catch (RuntimeException|StackOverflowError e) {
      builder.append("Exception thrown from implementation: ").append(e.getClass());
    } 
  }
  
  private static void executeListener(Runnable runnable, Executor executor) {
    try {
      executor.execute(runnable);
    } catch (RuntimeException e) {
      String str1 = String.valueOf(runnable), str2 = String.valueOf(executor);
      log.log(Level.SEVERE, (new StringBuilder(57 + String.valueOf(str1).length() + String.valueOf(str2).length())).append("RuntimeException while executing runnable ").append(str1).append(" with executor ").append(str2).toString(), e);
    } 
  }
  
  private static abstract class AtomicHelper {
    private AtomicHelper() {}
    
    abstract void putThread(AbstractFuture.Waiter param1Waiter, Thread param1Thread);
    
    abstract void putNext(AbstractFuture.Waiter param1Waiter1, @CheckForNull AbstractFuture.Waiter param1Waiter2);
    
    abstract boolean casWaiters(AbstractFuture<?> param1AbstractFuture, @CheckForNull AbstractFuture.Waiter param1Waiter1, @CheckForNull AbstractFuture.Waiter param1Waiter2);
    
    abstract boolean casListeners(AbstractFuture<?> param1AbstractFuture, @CheckForNull AbstractFuture.Listener param1Listener1, AbstractFuture.Listener param1Listener2);
    
    abstract AbstractFuture.Waiter gasWaiters(AbstractFuture<?> param1AbstractFuture, AbstractFuture.Waiter param1Waiter);
    
    abstract AbstractFuture.Listener gasListeners(AbstractFuture<?> param1AbstractFuture, AbstractFuture.Listener param1Listener);
    
    abstract boolean casValue(AbstractFuture<?> param1AbstractFuture, @CheckForNull Object param1Object1, Object param1Object2);
  }
  
  private static final class UnsafeAtomicHelper extends AtomicHelper {
    static final Unsafe UNSAFE;
    
    static final long LISTENERS_OFFSET;
    
    static final long WAITERS_OFFSET;
    
    static final long VALUE_OFFSET;
    
    static final long WAITER_THREAD_OFFSET;
    
    static final long WAITER_NEXT_OFFSET;
    
    private UnsafeAtomicHelper() {}
    
    static {
      Unsafe unsafe = null;
      try {
        unsafe = Unsafe.getUnsafe();
      } catch (SecurityException tryReflectionInstead) {
        try {
          unsafe = AccessController.<Unsafe>doPrivileged(new PrivilegedExceptionAction<Unsafe>() {
                public Unsafe run() throws Exception {
                  Class<Unsafe> k = Unsafe.class;
                  for (Field f : k.getDeclaredFields()) {
                    f.setAccessible(true);
                    Object x = f.get(null);
                    if (k.isInstance(x))
                      return k.cast(x); 
                  } 
                  throw new NoSuchFieldError("the Unsafe");
                }
              });
        } catch (PrivilegedActionException e) {
          throw new RuntimeException("Could not initialize intrinsics", e.getCause());
        } 
      } 
      try {
        Class<?> abstractFuture = AbstractFuture.class;
        WAITERS_OFFSET = unsafe.objectFieldOffset(abstractFuture.getDeclaredField("waiters"));
        LISTENERS_OFFSET = unsafe.objectFieldOffset(abstractFuture.getDeclaredField("listeners"));
        VALUE_OFFSET = unsafe.objectFieldOffset(abstractFuture.getDeclaredField("value"));
        WAITER_THREAD_OFFSET = unsafe.objectFieldOffset(AbstractFuture.Waiter.class.getDeclaredField("thread"));
        WAITER_NEXT_OFFSET = unsafe.objectFieldOffset(AbstractFuture.Waiter.class.getDeclaredField("next"));
        UNSAFE = unsafe;
      } catch (Exception e) {
        Throwables.throwIfUnchecked(e);
        throw new RuntimeException(e);
      } 
    }
    
    void putThread(AbstractFuture.Waiter waiter, Thread newValue) {
      UNSAFE.putObject(waiter, WAITER_THREAD_OFFSET, newValue);
    }
    
    void putNext(AbstractFuture.Waiter waiter, @CheckForNull AbstractFuture.Waiter newValue) {
      UNSAFE.putObject(waiter, WAITER_NEXT_OFFSET, newValue);
    }
    
    boolean casWaiters(AbstractFuture<?> future, @CheckForNull AbstractFuture.Waiter expect, @CheckForNull AbstractFuture.Waiter update) {
      return UNSAFE.compareAndSwapObject(future, WAITERS_OFFSET, expect, update);
    }
    
    boolean casListeners(AbstractFuture<?> future, @CheckForNull AbstractFuture.Listener expect, AbstractFuture.Listener update) {
      return UNSAFE.compareAndSwapObject(future, LISTENERS_OFFSET, expect, update);
    }
    
    AbstractFuture.Listener gasListeners(AbstractFuture<?> future, AbstractFuture.Listener update) {
      return (AbstractFuture.Listener)UNSAFE.getAndSetObject(future, LISTENERS_OFFSET, update);
    }
    
    AbstractFuture.Waiter gasWaiters(AbstractFuture<?> future, AbstractFuture.Waiter update) {
      return (AbstractFuture.Waiter)UNSAFE.getAndSetObject(future, WAITERS_OFFSET, update);
    }
    
    boolean casValue(AbstractFuture<?> future, @CheckForNull Object expect, Object update) {
      return UNSAFE.compareAndSwapObject(future, VALUE_OFFSET, expect, update);
    }
  }
  
  private static final class SafeAtomicHelper extends AtomicHelper {
    final AtomicReferenceFieldUpdater<AbstractFuture.Waiter, Thread> waiterThreadUpdater;
    
    final AtomicReferenceFieldUpdater<AbstractFuture.Waiter, AbstractFuture.Waiter> waiterNextUpdater;
    
    final AtomicReferenceFieldUpdater<AbstractFuture, AbstractFuture.Waiter> waitersUpdater;
    
    final AtomicReferenceFieldUpdater<AbstractFuture, AbstractFuture.Listener> listenersUpdater;
    
    final AtomicReferenceFieldUpdater<AbstractFuture, Object> valueUpdater;
    
    SafeAtomicHelper(AtomicReferenceFieldUpdater<AbstractFuture.Waiter, Thread> waiterThreadUpdater, AtomicReferenceFieldUpdater<AbstractFuture.Waiter, AbstractFuture.Waiter> waiterNextUpdater, AtomicReferenceFieldUpdater<AbstractFuture, AbstractFuture.Waiter> waitersUpdater, AtomicReferenceFieldUpdater<AbstractFuture, AbstractFuture.Listener> listenersUpdater, AtomicReferenceFieldUpdater<AbstractFuture, Object> valueUpdater) {
      this.waiterThreadUpdater = waiterThreadUpdater;
      this.waiterNextUpdater = waiterNextUpdater;
      this.waitersUpdater = waitersUpdater;
      this.listenersUpdater = listenersUpdater;
      this.valueUpdater = valueUpdater;
    }
    
    void putThread(AbstractFuture.Waiter waiter, Thread newValue) {
      this.waiterThreadUpdater.lazySet(waiter, newValue);
    }
    
    void putNext(AbstractFuture.Waiter waiter, @CheckForNull AbstractFuture.Waiter newValue) {
      this.waiterNextUpdater.lazySet(waiter, newValue);
    }
    
    boolean casWaiters(AbstractFuture<?> future, @CheckForNull AbstractFuture.Waiter expect, @CheckForNull AbstractFuture.Waiter update) {
      return this.waitersUpdater.compareAndSet(future, expect, update);
    }
    
    boolean casListeners(AbstractFuture<?> future, @CheckForNull AbstractFuture.Listener expect, AbstractFuture.Listener update) {
      return this.listenersUpdater.compareAndSet(future, expect, update);
    }
    
    AbstractFuture.Listener gasListeners(AbstractFuture<?> future, AbstractFuture.Listener update) {
      return this.listenersUpdater.getAndSet(future, update);
    }
    
    AbstractFuture.Waiter gasWaiters(AbstractFuture<?> future, AbstractFuture.Waiter update) {
      return this.waitersUpdater.getAndSet(future, update);
    }
    
    boolean casValue(AbstractFuture<?> future, @CheckForNull Object expect, Object update) {
      return this.valueUpdater.compareAndSet(future, expect, update);
    }
  }
  
  private static final class SynchronizedHelper extends AtomicHelper {
    private SynchronizedHelper() {}
    
    void putThread(AbstractFuture.Waiter waiter, Thread newValue) {
      waiter.thread = newValue;
    }
    
    void putNext(AbstractFuture.Waiter waiter, @CheckForNull AbstractFuture.Waiter newValue) {
      waiter.next = newValue;
    }
    
    boolean casWaiters(AbstractFuture<?> future, @CheckForNull AbstractFuture.Waiter expect, @CheckForNull AbstractFuture.Waiter update) {
      synchronized (future) {
        if (future.waiters == expect) {
          future.waiters = update;
          return true;
        } 
        return false;
      } 
    }
    
    boolean casListeners(AbstractFuture<?> future, @CheckForNull AbstractFuture.Listener expect, AbstractFuture.Listener update) {
      synchronized (future) {
        if (future.listeners == expect) {
          future.listeners = update;
          return true;
        } 
        return false;
      } 
    }
    
    AbstractFuture.Listener gasListeners(AbstractFuture<?> future, AbstractFuture.Listener update) {
      synchronized (future) {
        AbstractFuture.Listener old = future.listeners;
        if (old != update)
          future.listeners = update; 
        return old;
      } 
    }
    
    AbstractFuture.Waiter gasWaiters(AbstractFuture<?> future, AbstractFuture.Waiter update) {
      synchronized (future) {
        AbstractFuture.Waiter old = future.waiters;
        if (old != update)
          future.waiters = update; 
        return old;
      } 
    }
    
    boolean casValue(AbstractFuture<?> future, @CheckForNull Object expect, Object update) {
      synchronized (future) {
        if (future.value == expect) {
          future.value = update;
          return true;
        } 
        return false;
      } 
    }
  }
  
  private static CancellationException cancellationExceptionWithCause(String message, @CheckForNull Throwable cause) {
    CancellationException exception = new CancellationException(message);
    exception.initCause(cause);
    return exception;
  }
}
