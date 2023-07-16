package net.md_5.bungee.module;

public class ModuleVersion {
  private final String build;
  
  private final String git;
  
  public boolean equals(Object o) {
    if (o == this)
      return true; 
    if (!(o instanceof ModuleVersion))
      return false; 
    ModuleVersion other = (ModuleVersion)o;
    if (!other.canEqual(this))
      return false; 
    Object this$build = getBuild(), other$build = other.getBuild();
    if ((this$build == null) ? (other$build != null) : !this$build.equals(other$build))
      return false; 
    Object this$git = getGit(), other$git = other.getGit();
    return !((this$git == null) ? (other$git != null) : !this$git.equals(other$git));
  }
  
  protected boolean canEqual(Object other) {
    return other instanceof ModuleVersion;
  }
  
  public int hashCode() {
    int PRIME = 59;
    result = 1;
    Object $build = getBuild();
    result = result * 59 + (($build == null) ? 43 : $build.hashCode());
    Object $git = getGit();
    return result * 59 + (($git == null) ? 43 : $git.hashCode());
  }
  
  public String toString() {
    return "ModuleVersion(build=" + getBuild() + ", git=" + getGit() + ")";
  }
  
  private ModuleVersion(String build, String git) {
    this.build = build;
    this.git = git;
  }
  
  public String getBuild() {
    return this.build;
  }
  
  public String getGit() {
    return this.git;
  }
  
  public static ModuleVersion parse(String version) {
    int lastColon = version.lastIndexOf(':');
    int secondLastColon = version.lastIndexOf(':', lastColon - 1);
    if (lastColon == -1 || secondLastColon == -1)
      return null; 
    String buildNumber = version.substring(lastColon + 1, version.length());
    String gitCommit = version.substring(secondLastColon + 1, lastColon).replaceAll("\"", "");
    if ("unknown".equals(buildNumber) || "unknown".equals(gitCommit))
      return null; 
    return new ModuleVersion(buildNumber, gitCommit);
  }
}
