package org.apache.http.client.methods;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicMarkableReference;
import org.apache.http.HttpRequest;
import org.apache.http.client.utils.CloneUtils;
import org.apache.http.concurrent.Cancellable;
import org.apache.http.conn.ClientConnectionRequest;
import org.apache.http.conn.ConnectionReleaseTrigger;
import org.apache.http.message.AbstractHttpMessage;
import org.apache.http.message.HeaderGroup;
import org.apache.http.params.HttpParams;

public abstract class AbstractExecutionAwareRequest extends AbstractHttpMessage implements HttpExecutionAware, AbortableHttpRequest, Cloneable, HttpRequest {
  private final AtomicMarkableReference<Cancellable> cancellableRef = new AtomicMarkableReference<Cancellable>(null, false);
  
  @Deprecated
  public void setConnectionRequest(final ClientConnectionRequest connRequest) {
    setCancellable(new Cancellable() {
          public boolean cancel() {
            connRequest.abortRequest();
            return true;
          }
        });
  }
  
  @Deprecated
  public void setReleaseTrigger(final ConnectionReleaseTrigger releaseTrigger) {
    setCancellable(new Cancellable() {
          public boolean cancel() {
            try {
              releaseTrigger.abortConnection();
              return true;
            } catch (IOException ex) {
              return false;
            } 
          }
        });
  }
  
  public void abort() {
    while (!this.cancellableRef.isMarked()) {
      Cancellable actualCancellable = this.cancellableRef.getReference();
      if (this.cancellableRef.compareAndSet(actualCancellable, actualCancellable, false, true) && 
        actualCancellable != null)
        actualCancellable.cancel(); 
    } 
  }
  
  public boolean isAborted() {
    return this.cancellableRef.isMarked();
  }
  
  public void setCancellable(Cancellable cancellable) {
    Cancellable actualCancellable = this.cancellableRef.getReference();
    if (!this.cancellableRef.compareAndSet(actualCancellable, cancellable, false, false))
      cancellable.cancel(); 
  }
  
  public Object clone() throws CloneNotSupportedException {
    AbstractExecutionAwareRequest clone = (AbstractExecutionAwareRequest)super.clone();
    clone.headergroup = (HeaderGroup)CloneUtils.cloneObject(this.headergroup);
    clone.params = (HttpParams)CloneUtils.cloneObject(this.params);
    return clone;
  }
  
  @Deprecated
  public void completed() {
    this.cancellableRef.set(null, false);
  }
  
  public void reset() {
    boolean marked;
    Cancellable actualCancellable;
    do {
      marked = this.cancellableRef.isMarked();
      actualCancellable = this.cancellableRef.getReference();
      if (actualCancellable == null)
        continue; 
      actualCancellable.cancel();
    } while (!this.cancellableRef.compareAndSet(actualCancellable, null, marked, false));
  }
}
