package com.mysql.cj.protocol;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

public class NetworkResources {
  private final Socket mysqlConnection;
  
  private final InputStream mysqlInput;
  
  private final OutputStream mysqlOutput;
  
  public NetworkResources(Socket mysqlConnection, InputStream mysqlInput, OutputStream mysqlOutput) {
    this.mysqlConnection = mysqlConnection;
    this.mysqlInput = mysqlInput;
    this.mysqlOutput = mysqlOutput;
  }
  
  public final void forceClose() {
    try {
      if (!ExportControlled.isSSLEstablished(this.mysqlConnection))
        try {
          if (this.mysqlInput != null)
            this.mysqlInput.close(); 
        } finally {
          if (this.mysqlConnection != null && !this.mysqlConnection.isClosed() && !this.mysqlConnection.isInputShutdown())
            try {
              this.mysqlConnection.shutdownInput();
            } catch (UnsupportedOperationException unsupportedOperationException) {} 
        }  
    } catch (IOException iOException) {}
    try {
      if (!ExportControlled.isSSLEstablished(this.mysqlConnection))
        try {
          if (this.mysqlOutput != null)
            this.mysqlOutput.close(); 
        } finally {
          if (this.mysqlConnection != null && !this.mysqlConnection.isClosed() && !this.mysqlConnection.isOutputShutdown())
            try {
              this.mysqlConnection.shutdownOutput();
            } catch (UnsupportedOperationException unsupportedOperationException) {} 
        }  
    } catch (IOException iOException) {}
    try {
      if (this.mysqlConnection != null)
        this.mysqlConnection.close(); 
    } catch (IOException iOException) {}
  }
}
