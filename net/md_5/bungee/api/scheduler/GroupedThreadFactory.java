package net.md_5.bungee.api.scheduler;

import java.util.concurrent.ThreadFactory;
import net.md_5.bungee.api.plugin.Plugin;

@Deprecated
public class GroupedThreadFactory implements ThreadFactory {
  private final ThreadGroup group;
  
  public boolean equals(Object o) {
    if (o == this)
      return true; 
    if (!(o instanceof GroupedThreadFactory))
      return false; 
    GroupedThreadFactory other = (GroupedThreadFactory)o;
    if (!other.canEqual(this))
      return false; 
    Object this$group = getGroup(), other$group = other.getGroup();
    return !((this$group == null) ? (other$group != null) : !this$group.equals(other$group));
  }
  
  protected boolean canEqual(Object other) {
    return other instanceof GroupedThreadFactory;
  }
  
  public int hashCode() {
    int PRIME = 59;
    result = 1;
    Object $group = getGroup();
    return result * 59 + (($group == null) ? 43 : $group.hashCode());
  }
  
  public String toString() {
    return "GroupedThreadFactory(group=" + getGroup() + ")";
  }
  
  public ThreadGroup getGroup() {
    return this.group;
  }
  
  public static final class BungeeGroup extends ThreadGroup {
    private BungeeGroup(String name) {
      super(name);
    }
  }
  
  public GroupedThreadFactory(Plugin plugin, String name) {
    this.group = new BungeeGroup(name);
  }
  
  public Thread newThread(Runnable r) {
    return new Thread(this.group, r);
  }
}
