package org.apache.maven.model.profile.activation;

import org.apache.maven.model.Profile;
import org.apache.maven.model.building.ModelProblemCollector;
import org.apache.maven.model.profile.ProfileActivationContext;

public interface ProfileActivator {
  boolean isActive(Profile paramProfile, ProfileActivationContext paramProfileActivationContext, ModelProblemCollector paramModelProblemCollector);
  
  boolean presentInConfig(Profile paramProfile, ProfileActivationContext paramProfileActivationContext, ModelProblemCollector paramModelProblemCollector);
}
