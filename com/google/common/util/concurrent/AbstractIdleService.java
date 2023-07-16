package com.google.common.util.concurrent;

import com.google.common.annotations.GwtIncompatible;
import com.google.common.base.Supplier;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import java.time.Duration;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@ElementTypesAreNonnullByDefault
@GwtIncompatible
public abstract class AbstractIdleService implements Service {
  private final Supplier<String> threadNameSupplier = new ThreadNameSupplier();
  
  private final class ThreadNameSupplier implements Supplier<String> {
    private ThreadNameSupplier() {}
    
    public String get() {
      String str1 = AbstractIdleService.this.serviceName(), str2 = String.valueOf(AbstractIdleService.this.state());
      return (new StringBuilder(1 + String.valueOf(str1).length() + String.valueOf(str2).length())).append(str1).append(" ").append(str2).toString();
    }
  }
  
  private final Service delegate = new DelegateService();
  
  private final class DelegateService extends AbstractService {
    private DelegateService() {}
    
    protected final void doStart() {
      MoreExecutors.renamingDecorator(AbstractIdleService.this.executor(), AbstractIdleService.this.threadNameSupplier)
        .execute(new Runnable() {
            public void run() {
              try {
                AbstractIdleService.this.startUp();
                AbstractIdleService.DelegateService.this.notifyStarted();
              } catch (Throwable t) {
                AbstractIdleService.DelegateService.this.notifyFailed(t);
              } 
            }
          });
    }
    
    protected final void doStop() {
      MoreExecutors.renamingDecorator(AbstractIdleService.this.executor(), AbstractIdleService.this.threadNameSupplier)
        .execute(new Runnable() {
            public void run() {
              try {
                AbstractIdleService.this.shutDown();
                AbstractIdleService.DelegateService.this.notifyStopped();
              } catch (Throwable t) {
                AbstractIdleService.DelegateService.this.notifyFailed(t);
              } 
            }
          });
    }
    
    public String toString() {
      return AbstractIdleService.this.toString();
    }
  }
  
  protected Executor executor() {
    return new Executor() {
        public void execute(Runnable command) {
          MoreExecutors.newThread((String)AbstractIdleService.this.threadNameSupplier.get(), command).start();
        }
      };
  }
  
  public String toString() {
    String str1 = serviceName(), str2 = String.valueOf(state());
    return (new StringBuilder(3 + String.valueOf(str1).length() + String.valueOf(str2).length())).append(str1).append(" [").append(str2).append("]").toString();
  }
  
  public final boolean isRunning() {
    return this.delegate.isRunning();
  }
  
  public final Service.State state() {
    return this.delegate.state();
  }
  
  public final void addListener(Service.Listener listener, Executor executor) {
    this.delegate.addListener(listener, executor);
  }
  
  public final Throwable failureCause() {
    return this.delegate.failureCause();
  }
  
  @CanIgnoreReturnValue
  public final Service startAsync() {
    this.delegate.startAsync();
    return this;
  }
  
  @CanIgnoreReturnValue
  public final Service stopAsync() {
    this.delegate.stopAsync();
    return this;
  }
  
  public final void awaitRunning() {
    this.delegate.awaitRunning();
  }
  
  public final void awaitRunning(Duration timeout) throws TimeoutException {
    super.awaitRunning(timeout);
  }
  
  public final void awaitRunning(long timeout, TimeUnit unit) throws TimeoutException {
    this.delegate.awaitRunning(timeout, unit);
  }
  
  public final void awaitTerminated() {
    this.delegate.awaitTerminated();
  }
  
  public final void awaitTerminated(Duration timeout) throws TimeoutException {
    super.awaitTerminated(timeout);
  }
  
  public final void awaitTerminated(long timeout, TimeUnit unit) throws TimeoutException {
    this.delegate.awaitTerminated(timeout, unit);
  }
  
  protected String serviceName() {
    return getClass().getSimpleName();
  }
  
  protected abstract void startUp() throws Exception;
  
  protected abstract void shutDown() throws Exception;
}
