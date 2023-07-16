package net.md_5.bungee.module;

import com.google.common.base.Preconditions;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.InputStream;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.logging.Level;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.util.CaseInsensitiveMap;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;

public class ModuleManager {
  private final Map<String, ModuleSource> knownSources = new HashMap<>();
  
  public ModuleManager() {
    this.knownSources.put("jenkins", new JenkinsModuleSource());
  }
  
  @SuppressFBWarnings({"SF_SWITCH_FALLTHROUGH", "SF_SWITCH_NO_DEFAULT"})
  public void load(ProxyServer proxy, File moduleDirectory) throws Exception {
    Map<String, Object> config;
    CaseInsensitiveMap<String, List<String>> caseInsensitiveMap;
    moduleDirectory.mkdir();
    ModuleVersion bungeeVersion = ModuleVersion.parse(proxy.getVersion());
    if (bungeeVersion == null) {
      proxy.getLogger().warning("Couldn't detect bungee version. Custom build?");
      return;
    } 
    List<ModuleSpec> modules = new ArrayList<>();
    File configFile = new File("modules.yml");
    DumperOptions options = new DumperOptions();
    options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
    Yaml yaml = new Yaml(options);
    configFile.createNewFile();
    try (InputStream is = new FileInputStream(configFile)) {
      config = (Map<String, Object>)yaml.load(is);
    } 
    if (config == null) {
      caseInsensitiveMap = new CaseInsensitiveMap();
    } else {
      caseInsensitiveMap = new CaseInsensitiveMap((Map)caseInsensitiveMap);
    } 
    List<String> defaults = new ArrayList<>();
    Object readModules = caseInsensitiveMap.get("modules");
    if (readModules != null)
      defaults.addAll((Collection<? extends String>)readModules); 
    int version = caseInsensitiveMap.containsKey("version") ? ((Integer)caseInsensitiveMap.get("version")).intValue() : 0;
    switch (version) {
      case 0:
        defaults.add("jenkins://cmd_alert");
        defaults.add("jenkins://cmd_find");
        defaults.add("jenkins://cmd_list");
        defaults.add("jenkins://cmd_send");
        defaults.add("jenkins://cmd_server");
      case 1:
        defaults.add("jenkins://reconnect_yaml");
        break;
    } 
    caseInsensitiveMap.put("modules", defaults);
    caseInsensitiveMap.put("version", Integer.valueOf(2));
    try (FileWriter wr = new FileWriter(configFile)) {
      yaml.dump(caseInsensitiveMap, wr);
    } 
    for (String s : caseInsensitiveMap.get("modules")) {
      URI uri = new URI(s);
      ModuleSource source = this.knownSources.get(uri.getScheme());
      if (source == null) {
        System.out.println("Unknown module source: " + s);
        continue;
      } 
      String name = uri.getAuthority();
      if (name == null) {
        System.out.println("Unknown module host: " + s);
        continue;
      } 
      ModuleSpec spec = new ModuleSpec(name, new File(moduleDirectory, name + ".jar"), source);
      modules.add(spec);
      System.out.println("Discovered module: " + spec);
    } 
    for (ModuleSpec module : modules) {
      ModuleVersion moduleVersion = module.getFile().exists() ? getVersion(module.getFile()) : null;
      if (!bungeeVersion.equals(moduleVersion)) {
        System.out.println("Attempting to update plugin from " + moduleVersion + " to " + bungeeVersion);
        module.getProvider().retrieve(module, bungeeVersion);
      } 
    } 
  }
  
  @SuppressFBWarnings({"REC_CATCH_EXCEPTION"})
  private ModuleVersion getVersion(File file) {
    try (JarFile jar = new JarFile(file)) {
      JarEntry pdf = jar.getJarEntry("plugin.yml");
      Preconditions.checkNotNull(pdf, "Plugin must have a plugin.yml");
    } catch (Exception ex) {
      ProxyServer.getInstance().getLogger().log(Level.WARNING, "Could not check module from file " + file, ex);
      return null;
    } 
  }
}
