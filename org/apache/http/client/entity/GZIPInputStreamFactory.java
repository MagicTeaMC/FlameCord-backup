package org.apache.http.client.entity;

import java.io.IOException;
import java.io.InputStream;
import java.util.zip.GZIPInputStream;
import org.apache.http.annotation.Contract;
import org.apache.http.annotation.ThreadingBehavior;

@Contract(threading = ThreadingBehavior.IMMUTABLE)
public class GZIPInputStreamFactory implements InputStreamFactory {
  private static final GZIPInputStreamFactory INSTANCE = new GZIPInputStreamFactory();
  
  public static GZIPInputStreamFactory getInstance() {
    return INSTANCE;
  }
  
  public InputStream create(InputStream inputStream) throws IOException {
    return new GZIPInputStream(inputStream);
  }
}
