package org.eclipse.aether.spi.connector.transport;

import java.io.Closeable;

public interface Transporter extends Closeable {
  public static final int ERROR_OTHER = 0;
  
  public static final int ERROR_NOT_FOUND = 1;
  
  int classify(Throwable paramThrowable);
  
  void peek(PeekTask paramPeekTask) throws Exception;
  
  void get(GetTask paramGetTask) throws Exception;
  
  void put(PutTask paramPutTask) throws Exception;
  
  void close();
}
