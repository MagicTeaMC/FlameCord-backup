package org.apache.http;

import java.io.IOException;
import java.net.Socket;

public interface HttpConnectionFactory<T extends HttpConnection> {
  T createConnection(Socket paramSocket) throws IOException;
}
