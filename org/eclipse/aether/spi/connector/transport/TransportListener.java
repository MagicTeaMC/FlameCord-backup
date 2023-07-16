package org.eclipse.aether.spi.connector.transport;

import java.nio.ByteBuffer;
import org.eclipse.aether.transfer.TransferCancelledException;

public abstract class TransportListener {
  public void transportStarted(long dataOffset, long dataLength) throws TransferCancelledException {}
  
  public void transportProgressed(ByteBuffer data) throws TransferCancelledException {}
}
