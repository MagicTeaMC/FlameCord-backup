package org.apache.maven.model.profile;

import java.util.Collection;
import java.util.List;
import org.apache.maven.model.Profile;
import org.apache.maven.model.building.ModelProblemCollector;

public interface ProfileSelector {
  List<Profile> getActiveProfiles(Collection<Profile> paramCollection, ProfileActivationContext paramProfileActivationContext, ModelProblemCollector paramModelProblemCollector);
}
