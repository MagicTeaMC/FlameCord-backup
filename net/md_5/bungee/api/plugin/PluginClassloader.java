package net.md_5.bungee.api.plugin;

import com.google.common.base.Preconditions;
import com.google.common.io.ByteStreams;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLClassLoader;
import java.security.CodeSigner;
import java.security.CodeSource;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.Manifest;
import net.md_5.bungee.api.ProxyServer;

final class PluginClassloader extends URLClassLoader {
  public String toString() {
    return "PluginClassloader(desc=" + this.desc + ")";
  }
  
  private static final Set<PluginClassloader> allLoaders = new CopyOnWriteArraySet<>();
  
  private final ProxyServer proxy;
  
  private final PluginDescription desc;
  
  private final JarFile jar;
  
  private final Manifest manifest;
  
  private final URL url;
  
  private final ClassLoader libraryLoader;
  
  private Plugin plugin;
  
  static {
    ClassLoader.registerAsParallelCapable();
  }
  
  public PluginClassloader(ProxyServer proxy, PluginDescription desc, File file, ClassLoader libraryLoader) throws IOException {
    super(new URL[] { file
          
          .toURI().toURL() });
    this.proxy = proxy;
    this.desc = desc;
    this.jar = new JarFile(file);
    this.manifest = this.jar.getManifest();
    this.url = file.toURI().toURL();
    this.libraryLoader = libraryLoader;
    allLoaders.add(this);
  }
  
  protected Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
    return loadClass0(name, resolve, true, true);
  }
  
  private Class<?> loadClass0(String name, boolean resolve, boolean checkOther, boolean checkLibraries) throws ClassNotFoundException {
    try {
      Class<?> result = super.loadClass(name, resolve);
      if (checkOther || result.getClassLoader() == this)
        return result; 
    } catch (ClassNotFoundException classNotFoundException) {}
    if (checkLibraries && this.libraryLoader != null)
      try {
        return this.libraryLoader.loadClass(name);
      } catch (ClassNotFoundException classNotFoundException) {} 
    if (checkOther)
      for (PluginClassloader loader : allLoaders) {
        if (loader != this)
          try {
            return loader.loadClass0(name, resolve, false, this.proxy.getPluginManager().isTransitiveDepend(this.desc, loader.desc));
          } catch (ClassNotFoundException classNotFoundException) {} 
      }  
    throw new ClassNotFoundException(name);
  }
  
  protected Class<?> findClass(String name) throws ClassNotFoundException {
    String path = name.replace('.', '/').concat(".class");
    JarEntry entry = this.jar.getJarEntry(path);
    if (entry != null) {
      byte[] classBytes;
      try (InputStream is = this.jar.getInputStream(entry)) {
        classBytes = ByteStreams.toByteArray(is);
      } catch (IOException ex) {
        throw new ClassNotFoundException(name, ex);
      } 
      int dot = name.lastIndexOf('.');
      if (dot != -1) {
        String pkgName = name.substring(0, dot);
        if (getPackage(pkgName) == null)
          try {
            if (this.manifest != null) {
              definePackage(pkgName, this.manifest, this.url);
            } else {
              definePackage(pkgName, null, null, null, null, null, null, null);
            } 
          } catch (IllegalArgumentException ex) {
            if (getPackage(pkgName) == null)
              throw new IllegalStateException("Cannot find package " + pkgName); 
          }  
      } 
      CodeSigner[] signers = entry.getCodeSigners();
      CodeSource source = new CodeSource(this.url, signers);
      return defineClass(name, classBytes, 0, classBytes.length, source);
    } 
    return super.findClass(name);
  }
  
  public void close() throws IOException {
    try {
      super.close();
    } finally {
      this.jar.close();
    } 
  }
  
  void init(Plugin plugin) {
    Preconditions.checkArgument((plugin != null), "plugin");
    Preconditions.checkArgument((plugin.getClass().getClassLoader() == this), "Plugin has incorrect ClassLoader");
    if (this.plugin != null)
      throw new IllegalArgumentException("Plugin already initialized!"); 
    this.plugin = plugin;
    plugin.init(this.proxy, this.desc);
  }
}
