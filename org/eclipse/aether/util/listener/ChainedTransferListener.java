package org.eclipse.aether.util.listener;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import org.eclipse.aether.transfer.AbstractTransferListener;
import org.eclipse.aether.transfer.TransferCancelledException;
import org.eclipse.aether.transfer.TransferEvent;
import org.eclipse.aether.transfer.TransferListener;

public final class ChainedTransferListener extends AbstractTransferListener {
  private final List<TransferListener> listeners = new CopyOnWriteArrayList<>();
  
  public static TransferListener newInstance(TransferListener listener1, TransferListener listener2) {
    if (listener1 == null)
      return listener2; 
    if (listener2 == null)
      return listener1; 
    return (TransferListener)new ChainedTransferListener(new TransferListener[] { listener1, listener2 });
  }
  
  public ChainedTransferListener(TransferListener... listeners) {
    if (listeners != null)
      add(Arrays.asList(listeners)); 
  }
  
  public ChainedTransferListener(Collection<? extends TransferListener> listeners) {
    add(listeners);
  }
  
  public void add(Collection<? extends TransferListener> listeners) {
    if (listeners != null)
      for (TransferListener listener : listeners)
        add(listener);  
  }
  
  public void add(TransferListener listener) {
    if (listener != null)
      this.listeners.add(listener); 
  }
  
  public void remove(TransferListener listener) {
    if (listener != null)
      this.listeners.remove(listener); 
  }
  
  protected void handleError(TransferEvent event, TransferListener listener, RuntimeException error) {}
  
  public void transferInitiated(TransferEvent event) throws TransferCancelledException {
    for (TransferListener listener : this.listeners) {
      try {
        listener.transferInitiated(event);
      } catch (RuntimeException e) {
        handleError(event, listener, e);
      } 
    } 
  }
  
  public void transferStarted(TransferEvent event) throws TransferCancelledException {
    for (TransferListener listener : this.listeners) {
      try {
        listener.transferStarted(event);
      } catch (RuntimeException e) {
        handleError(event, listener, e);
      } 
    } 
  }
  
  public void transferProgressed(TransferEvent event) throws TransferCancelledException {
    for (TransferListener listener : this.listeners) {
      try {
        listener.transferProgressed(event);
      } catch (RuntimeException e) {
        handleError(event, listener, e);
      } 
    } 
  }
  
  public void transferCorrupted(TransferEvent event) throws TransferCancelledException {
    for (TransferListener listener : this.listeners) {
      try {
        listener.transferCorrupted(event);
      } catch (RuntimeException e) {
        handleError(event, listener, e);
      } 
    } 
  }
  
  public void transferSucceeded(TransferEvent event) {
    for (TransferListener listener : this.listeners) {
      try {
        listener.transferSucceeded(event);
      } catch (RuntimeException e) {
        handleError(event, listener, e);
      } 
    } 
  }
  
  public void transferFailed(TransferEvent event) {
    for (TransferListener listener : this.listeners) {
      try {
        listener.transferFailed(event);
      } catch (RuntimeException e) {
        handleError(event, listener, e);
      } 
    } 
  }
}
