package org.codehaus.plexus.util;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.nio.channels.Channel;

public final class IOUtil {
  private static final int DEFAULT_BUFFER_SIZE = 16384;
  
  public static void copy(InputStream input, OutputStream output) throws IOException {
    copy(input, output, 16384);
  }
  
  public static void copy(InputStream input, OutputStream output, int bufferSize) throws IOException {
    byte[] buffer = new byte[bufferSize];
    int n = 0;
    while (0 <= (n = input.read(buffer)))
      output.write(buffer, 0, n); 
  }
  
  public static void copy(Reader input, Writer output) throws IOException {
    copy(input, output, 16384);
  }
  
  public static void copy(Reader input, Writer output, int bufferSize) throws IOException {
    char[] buffer = new char[bufferSize];
    int n = 0;
    while (0 <= (n = input.read(buffer)))
      output.write(buffer, 0, n); 
    output.flush();
  }
  
  public static void copy(InputStream input, Writer output) throws IOException {
    copy(input, output, 16384);
  }
  
  public static void copy(InputStream input, Writer output, int bufferSize) throws IOException {
    InputStreamReader in = new InputStreamReader(input);
    copy(in, output, bufferSize);
  }
  
  public static void copy(InputStream input, Writer output, String encoding) throws IOException {
    InputStreamReader in = new InputStreamReader(input, encoding);
    copy(in, output);
  }
  
  public static void copy(InputStream input, Writer output, String encoding, int bufferSize) throws IOException {
    InputStreamReader in = new InputStreamReader(input, encoding);
    copy(in, output, bufferSize);
  }
  
  public static String toString(InputStream input) throws IOException {
    return toString(input, 16384);
  }
  
  public static String toString(InputStream input, int bufferSize) throws IOException {
    StringWriter sw = new StringWriter();
    copy(input, sw, bufferSize);
    return sw.toString();
  }
  
  public static String toString(InputStream input, String encoding) throws IOException {
    return toString(input, encoding, 16384);
  }
  
  public static String toString(InputStream input, String encoding, int bufferSize) throws IOException {
    StringWriter sw = new StringWriter();
    copy(input, sw, encoding, bufferSize);
    return sw.toString();
  }
  
  public static byte[] toByteArray(InputStream input) throws IOException {
    return toByteArray(input, 16384);
  }
  
  public static byte[] toByteArray(InputStream input, int bufferSize) throws IOException {
    ByteArrayOutputStream output = new ByteArrayOutputStream();
    copy(input, output, bufferSize);
    return output.toByteArray();
  }
  
  public static void copy(Reader input, OutputStream output) throws IOException {
    copy(input, output, 16384);
  }
  
  public static void copy(Reader input, OutputStream output, int bufferSize) throws IOException {
    OutputStreamWriter out = new OutputStreamWriter(output);
    copy(input, out, bufferSize);
    out.flush();
  }
  
  public static String toString(Reader input) throws IOException {
    return toString(input, 16384);
  }
  
  public static String toString(Reader input, int bufferSize) throws IOException {
    StringWriter sw = new StringWriter();
    copy(input, sw, bufferSize);
    return sw.toString();
  }
  
  public static byte[] toByteArray(Reader input) throws IOException {
    return toByteArray(input, 16384);
  }
  
  public static byte[] toByteArray(Reader input, int bufferSize) throws IOException {
    ByteArrayOutputStream output = new ByteArrayOutputStream();
    copy(input, output, bufferSize);
    return output.toByteArray();
  }
  
  public static void copy(String input, OutputStream output) throws IOException {
    copy(input, output, 16384);
  }
  
  public static void copy(String input, OutputStream output, int bufferSize) throws IOException {
    StringReader in = new StringReader(input);
    OutputStreamWriter out = new OutputStreamWriter(output);
    copy(in, out, bufferSize);
    out.flush();
  }
  
  public static void copy(String input, Writer output) throws IOException {
    output.write(input);
  }
  
  public static void bufferedCopy(InputStream input, OutputStream output) throws IOException {
    BufferedInputStream in = new BufferedInputStream(input);
    BufferedOutputStream out = new BufferedOutputStream(output);
    copy(in, out);
    out.flush();
  }
  
  public static byte[] toByteArray(String input) throws IOException {
    return toByteArray(input, 16384);
  }
  
  public static byte[] toByteArray(String input, int bufferSize) throws IOException {
    ByteArrayOutputStream output = new ByteArrayOutputStream();
    copy(input, output, bufferSize);
    return output.toByteArray();
  }
  
  public static void copy(byte[] input, Writer output) throws IOException {
    copy(input, output, 16384);
  }
  
  public static void copy(byte[] input, Writer output, int bufferSize) throws IOException {
    ByteArrayInputStream in = new ByteArrayInputStream(input);
    copy(in, output, bufferSize);
  }
  
  public static void copy(byte[] input, Writer output, String encoding) throws IOException {
    ByteArrayInputStream in = new ByteArrayInputStream(input);
    copy(in, output, encoding);
  }
  
  public static void copy(byte[] input, Writer output, String encoding, int bufferSize) throws IOException {
    ByteArrayInputStream in = new ByteArrayInputStream(input);
    copy(in, output, encoding, bufferSize);
  }
  
  public static String toString(byte[] input) throws IOException {
    return toString(input, 16384);
  }
  
  public static String toString(byte[] input, int bufferSize) throws IOException {
    StringWriter sw = new StringWriter();
    copy(input, sw, bufferSize);
    return sw.toString();
  }
  
  public static String toString(byte[] input, String encoding) throws IOException {
    return toString(input, encoding, 16384);
  }
  
  public static String toString(byte[] input, String encoding, int bufferSize) throws IOException {
    StringWriter sw = new StringWriter();
    copy(input, sw, encoding, bufferSize);
    return sw.toString();
  }
  
  public static void copy(byte[] input, OutputStream output) throws IOException {
    copy(input, output, 16384);
  }
  
  public static void copy(byte[] input, OutputStream output, int bufferSize) throws IOException {
    output.write(input);
  }
  
  public static boolean contentEquals(InputStream input1, InputStream input2) throws IOException {
    InputStream bufferedInput1 = new BufferedInputStream(input1);
    InputStream bufferedInput2 = new BufferedInputStream(input2);
    int ch = bufferedInput1.read();
    while (0 <= ch) {
      int i = bufferedInput2.read();
      if (ch != i)
        return false; 
      ch = bufferedInput1.read();
    } 
    int ch2 = bufferedInput2.read();
    if (0 <= ch2)
      return false; 
    return true;
  }
  
  public static void close(InputStream inputStream) {
    if (inputStream == null)
      return; 
    try {
      inputStream.close();
    } catch (IOException iOException) {}
  }
  
  public static void close(Channel channel) {
    if (channel == null)
      return; 
    try {
      channel.close();
    } catch (IOException iOException) {}
  }
  
  public static void close(OutputStream outputStream) {
    if (outputStream == null)
      return; 
    try {
      outputStream.close();
    } catch (IOException iOException) {}
  }
  
  public static void close(Reader reader) {
    if (reader == null)
      return; 
    try {
      reader.close();
    } catch (IOException iOException) {}
  }
  
  public static void close(Writer writer) {
    if (writer == null)
      return; 
    try {
      writer.close();
    } catch (IOException iOException) {}
  }
}
