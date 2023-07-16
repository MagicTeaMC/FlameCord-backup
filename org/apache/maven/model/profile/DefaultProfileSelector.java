package org.apache.maven.model.profile;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;
import org.apache.maven.model.Activation;
import org.apache.maven.model.Profile;
import org.apache.maven.model.building.ModelProblem;
import org.apache.maven.model.building.ModelProblemCollector;
import org.apache.maven.model.building.ModelProblemCollectorRequest;
import org.apache.maven.model.profile.activation.ProfileActivator;

@Named
@Singleton
public class DefaultProfileSelector implements ProfileSelector {
  @Inject
  private List<ProfileActivator> activators = new ArrayList<>();
  
  public DefaultProfileSelector addProfileActivator(ProfileActivator profileActivator) {
    if (profileActivator != null)
      this.activators.add(profileActivator); 
    return this;
  }
  
  public List<Profile> getActiveProfiles(Collection<Profile> profiles, ProfileActivationContext context, ModelProblemCollector problems) {
    Collection<String> activatedIds = new HashSet<>(context.getActiveProfileIds());
    Collection<String> deactivatedIds = new HashSet<>(context.getInactiveProfileIds());
    List<Profile> activeProfiles = new ArrayList<>(profiles.size());
    List<Profile> activePomProfilesByDefault = new ArrayList<>();
    boolean activatedPomProfileNotByDefault = false;
    for (Profile profile : profiles) {
      if (!deactivatedIds.contains(profile.getId())) {
        if (activatedIds.contains(profile.getId()) || isActive(profile, context, problems)) {
          activeProfiles.add(profile);
          if ("pom".equals(profile.getSource()))
            activatedPomProfileNotByDefault = true; 
          continue;
        } 
        if (isActiveByDefault(profile)) {
          if ("pom".equals(profile.getSource())) {
            activePomProfilesByDefault.add(profile);
            continue;
          } 
          activeProfiles.add(profile);
        } 
      } 
    } 
    if (!activatedPomProfileNotByDefault)
      activeProfiles.addAll(activePomProfilesByDefault); 
    return activeProfiles;
  }
  
  private boolean isActive(Profile profile, ProfileActivationContext context, ModelProblemCollector problems) {
    boolean isActive = false;
    for (ProfileActivator activator : this.activators) {
      if (activator.presentInConfig(profile, context, problems))
        isActive = true; 
    } 
    for (ProfileActivator activator : this.activators) {
      try {
        if (activator.presentInConfig(profile, context, problems))
          isActive &= activator.isActive(profile, context, problems); 
      } catch (RuntimeException e) {
        problems.add((new ModelProblemCollectorRequest(ModelProblem.Severity.ERROR, ModelProblem.Version.BASE))
            .setMessage("Failed to determine activation for profile " + profile.getId())
            .setLocation(profile.getLocation(""))
            .setException(e));
        return false;
      } 
    } 
    return isActive;
  }
  
  private boolean isActiveByDefault(Profile profile) {
    Activation activation = profile.getActivation();
    return (activation != null && activation.isActiveByDefault());
  }
}
