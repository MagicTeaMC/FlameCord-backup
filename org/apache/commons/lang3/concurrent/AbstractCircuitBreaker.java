package org.apache.commons.lang3.concurrent;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.concurrent.atomic.AtomicReference;

public abstract class AbstractCircuitBreaker<T> implements CircuitBreaker<T> {
  public static final String PROPERTY_NAME = "open";
  
  protected final AtomicReference<State> state = new AtomicReference<>(State.CLOSED);
  
  private final PropertyChangeSupport changeSupport;
  
  public AbstractCircuitBreaker() {
    this.changeSupport = new PropertyChangeSupport(this);
  }
  
  public boolean isOpen() {
    return isOpen(this.state.get());
  }
  
  public boolean isClosed() {
    return !isOpen();
  }
  
  public abstract boolean checkState();
  
  public abstract boolean incrementAndCheckState(T paramT);
  
  public void close() {
    changeState(State.CLOSED);
  }
  
  public void open() {
    changeState(State.OPEN);
  }
  
  protected static boolean isOpen(State state) {
    return (state == State.OPEN);
  }
  
  protected void changeState(State newState) {
    if (this.state.compareAndSet(newState.oppositeState(), newState))
      this.changeSupport.firePropertyChange("open", !isOpen(newState), isOpen(newState)); 
  }
  
  public void addChangeListener(PropertyChangeListener listener) {
    this.changeSupport.addPropertyChangeListener(listener);
  }
  
  public void removeChangeListener(PropertyChangeListener listener) {
    this.changeSupport.removePropertyChangeListener(listener);
  }
  
  protected enum State {
    CLOSED {
      public State oppositeState() {
        return OPEN;
      }
    },
    OPEN {
      public State oppositeState() {
        return CLOSED;
      }
    };
    
    public abstract State oppositeState();
  }
}
