package org.apache.commons.codec.cli;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.util.Arrays;
import java.util.Locale;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.codec.digest.MessageDigestAlgorithms;

public class Digest {
  private final String algorithm;
  
  private final String[] args;
  
  private final String[] inputs;
  
  public static void main(String[] args) throws IOException {
    (new Digest(args)).run();
  }
  
  private Digest(String[] args) {
    if (args == null)
      throw new IllegalArgumentException("args"); 
    if (args.length == 0)
      throw new IllegalArgumentException(
          String.format("Usage: java %s [algorithm] [FILE|DIRECTORY|string] ...", new Object[] { Digest.class.getName() })); 
    this.args = args;
    this.algorithm = args[0];
    if (args.length <= 1) {
      this.inputs = null;
    } else {
      this.inputs = new String[args.length - 1];
      System.arraycopy(args, 1, this.inputs, 0, this.inputs.length);
    } 
  }
  
  private void println(String prefix, byte[] digest) {
    println(prefix, digest, null);
  }
  
  private void println(String prefix, byte[] digest, String fileName) {
    System.out.println(prefix + Hex.encodeHexString(digest) + ((fileName != null) ? ("  " + fileName) : ""));
  }
  
  private void run() throws IOException {
    if (this.algorithm.equalsIgnoreCase("ALL") || this.algorithm.equals("*")) {
      run(MessageDigestAlgorithms.values());
      return;
    } 
    MessageDigest messageDigest = DigestUtils.getDigest(this.algorithm, null);
    if (messageDigest != null) {
      run("", messageDigest);
    } else {
      run("", DigestUtils.getDigest(this.algorithm.toUpperCase(Locale.ROOT)));
    } 
  }
  
  private void run(String[] digestAlgorithms) throws IOException {
    for (String messageDigestAlgorithm : digestAlgorithms) {
      if (DigestUtils.isAvailable(messageDigestAlgorithm))
        run(messageDigestAlgorithm + " ", messageDigestAlgorithm); 
    } 
  }
  
  private void run(String prefix, MessageDigest messageDigest) throws IOException {
    if (this.inputs == null) {
      println(prefix, DigestUtils.digest(messageDigest, System.in));
      return;
    } 
    for (String source : this.inputs) {
      File file = new File(source);
      if (file.isFile()) {
        println(prefix, DigestUtils.digest(messageDigest, file), source);
      } else if (file.isDirectory()) {
        File[] listFiles = file.listFiles();
        if (listFiles != null)
          run(prefix, messageDigest, listFiles); 
      } else {
        byte[] bytes = source.getBytes(Charset.defaultCharset());
        println(prefix, DigestUtils.digest(messageDigest, bytes));
      } 
    } 
  }
  
  private void run(String prefix, MessageDigest messageDigest, File[] files) throws IOException {
    for (File file : files) {
      if (file.isFile())
        println(prefix, DigestUtils.digest(messageDigest, file), file.getName()); 
    } 
  }
  
  private void run(String prefix, String messageDigestAlgorithm) throws IOException {
    run(prefix, DigestUtils.getDigest(messageDigestAlgorithm));
  }
  
  public String toString() {
    return String.format("%s %s", new Object[] { super.toString(), Arrays.toString((Object[])this.args) });
  }
}
