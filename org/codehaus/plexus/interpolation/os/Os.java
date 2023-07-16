package org.codehaus.plexus.interpolation.os;

import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

public class Os {
  public static final String FAMILY_DOS = "dos";
  
  public static final String FAMILY_MAC = "mac";
  
  public static final String FAMILY_NETWARE = "netware";
  
  public static final String FAMILY_OS2 = "os/2";
  
  public static final String FAMILY_TANDEM = "tandem";
  
  public static final String FAMILY_UNIX = "unix";
  
  public static final String FAMILY_WINDOWS = "windows";
  
  public static final String FAMILY_WIN9X = "win9x";
  
  public static final String FAMILY_ZOS = "z/os";
  
  public static final String FAMILY_OS400 = "os/400";
  
  public static final String FAMILY_OPENVMS = "openvms";
  
  private static final Set<String> validFamilies = setValidFamilies();
  
  private static final String PATH_SEP = System.getProperty("path.separator");
  
  public static final String OS_NAME = System.getProperty("os.name").toLowerCase(Locale.US);
  
  public static final String OS_ARCH = System.getProperty("os.arch").toLowerCase(Locale.US);
  
  public static final String OS_VERSION = System.getProperty("os.version").toLowerCase(Locale.US);
  
  public static final String OS_FAMILY = getOsFamily();
  
  private String family;
  
  private String name;
  
  private String version;
  
  private String arch;
  
  public Os() {}
  
  public Os(String family) {
    setFamily(family);
  }
  
  private static Set<String> setValidFamilies() {
    Set<String> valid = new HashSet<String>();
    valid.add("dos");
    valid.add("mac");
    valid.add("netware");
    valid.add("os/2");
    valid.add("tandem");
    valid.add("unix");
    valid.add("windows");
    valid.add("win9x");
    valid.add("z/os");
    valid.add("os/400");
    valid.add("openvms");
    return valid;
  }
  
  public void setFamily(String f) {
    this.family = f.toLowerCase(Locale.US);
  }
  
  public void setName(String name) {
    this.name = name.toLowerCase(Locale.US);
  }
  
  public void setArch(String arch) {
    this.arch = arch.toLowerCase(Locale.US);
  }
  
  public void setVersion(String version) {
    this.version = version.toLowerCase(Locale.US);
  }
  
  public boolean eval() throws Exception {
    return isOs(this.family, this.name, this.arch, this.version);
  }
  
  public static boolean isFamily(String family) {
    return isOs(family, null, null, null);
  }
  
  public static boolean isName(String name) {
    return isOs(null, name, null, null);
  }
  
  public static boolean isArch(String arch) {
    return isOs(null, null, arch, null);
  }
  
  public static boolean isVersion(String version) {
    return isOs(null, null, null, version);
  }
  
  public static boolean isOs(String family, String name, String arch, String version) {
    boolean retValue = false;
    if (family != null || name != null || arch != null || version != null) {
      boolean isFamily = true;
      boolean isName = true;
      boolean isArch = true;
      boolean isVersion = true;
      if (family != null)
        if (family.equalsIgnoreCase("windows")) {
          isFamily = OS_NAME.contains("windows");
        } else if (family.equalsIgnoreCase("os/2")) {
          isFamily = OS_NAME.contains("os/2");
        } else if (family.equalsIgnoreCase("netware")) {
          isFamily = OS_NAME.contains("netware");
        } else if (family.equalsIgnoreCase("dos")) {
          isFamily = (PATH_SEP.equals(";") && !isFamily("netware"));
        } else if (family.equalsIgnoreCase("mac")) {
          isFamily = OS_NAME.contains("mac");
        } else if (family.equalsIgnoreCase("tandem")) {
          isFamily = OS_NAME.contains("nonstop_kernel");
        } else if (family.equalsIgnoreCase("unix")) {
          isFamily = (PATH_SEP.equals(":") && !isFamily("openvms") && (!isFamily("mac") || OS_NAME.endsWith("x")));
        } else if (family.equalsIgnoreCase("win9x")) {
          isFamily = (isFamily("windows") && (OS_NAME.contains("95") || OS_NAME.contains("98") || OS_NAME.contains("me") || OS_NAME.contains("ce")));
        } else if (family.equalsIgnoreCase("z/os")) {
          isFamily = (OS_NAME.contains("z/os") || OS_NAME.contains("os/390"));
        } else if (family.equalsIgnoreCase("os/400")) {
          isFamily = OS_NAME.contains("os/400");
        } else if (family.equalsIgnoreCase("openvms")) {
          isFamily = OS_NAME.contains("openvms");
        } else {
          isFamily = OS_NAME.contains(family.toLowerCase(Locale.US));
        }  
      if (name != null)
        isName = name.toLowerCase(Locale.US).equals(OS_NAME); 
      if (arch != null)
        isArch = arch.toLowerCase(Locale.US).equals(OS_ARCH); 
      if (version != null)
        isVersion = version.toLowerCase(Locale.US).equals(OS_VERSION); 
      retValue = (isFamily && isName && isArch && isVersion);
    } 
    return retValue;
  }
  
  private static String getOsFamily() {
    Set<String> families = null;
    if (!validFamilies.isEmpty()) {
      families = validFamilies;
    } else {
      families = setValidFamilies();
    } 
    for (String fam : families) {
      if (isFamily(fam))
        return fam; 
    } 
    return null;
  }
  
  public static boolean isValidFamily(String theFamily) {
    return validFamilies.contains(theFamily);
  }
  
  public static Set<String> getValidFamilies() {
    return new HashSet<String>(validFamilies);
  }
}
