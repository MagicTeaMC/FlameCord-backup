package org.apache.logging.log4j.message;

public interface ParameterConsumer<S> {
  void accept(Object paramObject, int paramInt, S paramS);
}
