package org.eclipse.aether.internal.impl;

import java.util.Calendar;
import javax.inject.Named;
import org.eclipse.aether.RepositorySystemSession;
import org.eclipse.aether.impl.UpdatePolicyAnalyzer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Named
public class DefaultUpdatePolicyAnalyzer implements UpdatePolicyAnalyzer {
  private static final Logger LOGGER = LoggerFactory.getLogger(DefaultUpdatePolicyAnalyzer.class);
  
  public String getEffectiveUpdatePolicy(RepositorySystemSession session, String policy1, String policy2) {
    return (ordinalOfUpdatePolicy(policy1) < ordinalOfUpdatePolicy(policy2)) ? policy1 : policy2;
  }
  
  private int ordinalOfUpdatePolicy(String policy) {
    if ("daily".equals(policy))
      return 1440; 
    if ("always".equals(policy))
      return 0; 
    if (policy != null && policy.startsWith("interval"))
      return getMinutes(policy); 
    return Integer.MAX_VALUE;
  }
  
  public boolean isUpdatedRequired(RepositorySystemSession session, long lastModified, String policy) {
    boolean checkForUpdates;
    if (policy == null)
      policy = ""; 
    if ("always".equals(policy)) {
      checkForUpdates = true;
    } else if ("daily".equals(policy)) {
      Calendar cal = Calendar.getInstance();
      cal.set(11, 0);
      cal.set(12, 0);
      cal.set(13, 0);
      cal.set(14, 0);
      checkForUpdates = (cal.getTimeInMillis() > lastModified);
    } else if (policy.startsWith("interval")) {
      int minutes = getMinutes(policy);
      Calendar cal = Calendar.getInstance();
      cal.add(12, -minutes);
      checkForUpdates = (cal.getTimeInMillis() > lastModified);
    } else {
      checkForUpdates = false;
      if (!"never".equals(policy))
        LOGGER.warn("Unknown repository update policy '{}', assuming '{}'", policy, "never"); 
    } 
    return checkForUpdates;
  }
  
  private int getMinutes(String policy) {
    int minutes;
    try {
      String s = policy.substring("interval".length() + 1);
      minutes = Integer.parseInt(s);
    } catch (RuntimeException e) {
      minutes = 1440;
      LOGGER.warn("Non-parseable repository update policy '{}', assuming '{}:1440'", policy, "interval");
    } 
    return minutes;
  }
}
