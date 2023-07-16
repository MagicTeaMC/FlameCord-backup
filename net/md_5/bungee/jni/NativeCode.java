package net.md_5.bungee.jni;

import com.google.common.io.ByteStreams;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.function.Supplier;
import net.md_5.bungee.jni.cipher.BungeeCipher;

public final class NativeCode<T> {
  private final String name;
  
  private final Supplier<? extends T> javaImpl;
  
  private final Supplier<? extends T> nativeImpl;
  
  private boolean loaded;
  
  public NativeCode(String name, Supplier<? extends T> javaImpl, Supplier<? extends T> nativeImpl) {
    if ("Mac OS X".equals(System.getProperty("os.name")))
      name = "osx-" + name; 
    this.name = name;
    this.javaImpl = javaImpl;
    this.nativeImpl = nativeImpl;
  }
  
  public T newInstance() {
    return this.loaded ? this.nativeImpl.get() : this.javaImpl.get();
  }
  
  public boolean load() {
    if (!this.loaded && isSupported()) {
      String fullName = "bungeecord-" + this.name;
      try {
        System.loadLibrary(fullName);
        this.loaded = true;
      } catch (Throwable throwable) {}
      if (!this.loaded)
        try (InputStream soFile = BungeeCipher.class.getClassLoader().getResourceAsStream(this.name + ".so")) {
          File temp = File.createTempFile(fullName, ".so");
          temp.deleteOnExit();
          try (OutputStream outputStream = new FileOutputStream(temp)) {
            ByteStreams.copy(soFile, outputStream);
          } 
          System.load(temp.getPath());
          this.loaded = true;
        } catch (IOException iOException) {
        
        } catch (UnsatisfiedLinkError ex) {
          System.out.println("Could not load native library: " + ex.getMessage());
        }  
    } 
    return this.loaded;
  }
  
  public static boolean isSupported() {
    return (("Linux".equals(System.getProperty("os.name")) || "Mac OS X".equals(System.getProperty("os.name"))) && ("amd64".equals(System.getProperty("os.arch")) || "x86_64".equals(System.getProperty("os.arch"))));
  }
}
