package dev._2lstudios.flamecord.natives;

import dev._2lstudios.flamecord.natives.compress.Compressor;
import dev._2lstudios.flamecord.natives.compress.CompressorFactory;
import dev._2lstudios.flamecord.natives.compress.JavaCompressor;
import dev._2lstudios.flamecord.natives.compress.LibdeflateCompressor;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.CopyOption;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.FileAttribute;
import java.util.Arrays;
import java.util.List;

public class Natives {
  private static final CompressorFactory COMPRESSOR_FACTORY = loadAvailableCompressFactory();
  
  public static CompressorFactory getCompressorFactory() {
    return COMPRESSOR_FACTORY;
  }
  
  public static List<CompressorFactory> getAvailableCompressorFactories() {
    return Arrays.asList(new CompressorFactory[] { new CompressorFactory() {
            public String getName() {
              return "Libdeflate (linux_x86_64)";
            }
            
            public boolean isCorrectEnvironment() {
              return NativeEnvironmentDetector.isLinux_X86_64();
            }
            
            public String getNativePath() {
              return "/libdeflate_x86_64.so";
            }
            
            public Compressor create(int level) {
              return (Compressor)new LibdeflateCompressor(level);
            }
          }, new CompressorFactory() {
            public String getName() {
              return "Libdeflate (linux_aarch64)";
            }
            
            public boolean isCorrectEnvironment() {
              return NativeEnvironmentDetector.isLinux_AARCH64();
            }
            
            public String getNativePath() {
              return "/libdeflate_aarch64.so";
            }
            
            public Compressor create(int level) {
              return (Compressor)new LibdeflateCompressor(level);
            }
          }, new CompressorFactory() {
            public String getName() {
              return "Java";
            }
            
            public boolean isCorrectEnvironment() {
              return true;
            }
            
            public String getNativePath() {
              return null;
            }
            
            public Compressor create(int level) {
              return (Compressor)new JavaCompressor(level);
            }
          } });
  }
  
  public static CompressorFactory loadAvailableCompressFactory() {
    for (CompressorFactory factory : getAvailableCompressorFactories()) {
      if (factory.isCorrectEnvironment()) {
        String nativePath = factory.getNativePath();
        if (nativePath != null)
          try {
            copyAndLoadNative(nativePath);
          } catch (Exception ignored) {
            continue;
          }  
        return factory;
      } 
    } 
    throw new IllegalStateException("None of the compress factories recognized the environment!");
  }
  
  private static void copyAndLoadNative(String path) {
    try {
      InputStream nativeLib = Natives.class.getResourceAsStream(path);
      if (nativeLib == null)
        throw new IllegalStateException("Native library " + path + " not found."); 
      Path tempFile = Files.createTempFile("native-", path.substring(path.lastIndexOf('.')), (FileAttribute<?>[])new FileAttribute[0]);
      Files.copy(nativeLib, tempFile, new CopyOption[] { StandardCopyOption.REPLACE_EXISTING });
      Runtime.getRuntime().addShutdownHook(new Thread(() -> {
              try {
                Files.deleteIfExists(tempFile);
              } catch (IOException iOException) {}
            }));
      try {
        System.load(tempFile.toAbsolutePath().toString());
      } catch (UnsatisfiedLinkError e) {
        throw new RuntimeException("Unable to load native " + tempFile.toAbsolutePath(), e);
      } 
    } catch (IOException e) {
      throw new RuntimeException("Unable to copy natives", e);
    } 
  }
}
