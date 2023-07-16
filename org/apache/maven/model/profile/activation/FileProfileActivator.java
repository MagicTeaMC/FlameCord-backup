package org.apache.maven.model.profile.activation;

import java.io.File;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;
import org.apache.maven.model.Activation;
import org.apache.maven.model.ActivationFile;
import org.apache.maven.model.Profile;
import org.apache.maven.model.building.ModelProblem;
import org.apache.maven.model.building.ModelProblemCollector;
import org.apache.maven.model.building.ModelProblemCollectorRequest;
import org.apache.maven.model.path.ProfileActivationFilePathInterpolator;
import org.apache.maven.model.profile.ProfileActivationContext;
import org.codehaus.plexus.interpolation.InterpolationException;
import org.codehaus.plexus.util.StringUtils;

@Named("file")
@Singleton
public class FileProfileActivator implements ProfileActivator {
  @Inject
  private ProfileActivationFilePathInterpolator profileActivationFilePathInterpolator;
  
  public FileProfileActivator setProfileActivationFilePathInterpolator(ProfileActivationFilePathInterpolator profileActivationFilePathInterpolator) {
    this.profileActivationFilePathInterpolator = profileActivationFilePathInterpolator;
    return this;
  }
  
  public boolean isActive(Profile profile, ProfileActivationContext context, ModelProblemCollector problems) {
    String path;
    boolean missing;
    Activation activation = profile.getActivation();
    if (activation == null)
      return false; 
    ActivationFile file = activation.getFile();
    if (file == null)
      return false; 
    if (StringUtils.isNotEmpty(file.getExists())) {
      path = file.getExists();
      missing = false;
    } else if (StringUtils.isNotEmpty(file.getMissing())) {
      path = file.getMissing();
      missing = true;
    } else {
      return false;
    } 
    try {
      path = this.profileActivationFilePathInterpolator.interpolate(path, context);
    } catch (InterpolationException e) {
      problems.add((new ModelProblemCollectorRequest(ModelProblem.Severity.ERROR, ModelProblem.Version.BASE))
          .setMessage("Failed to interpolate file location " + path + " for profile " + profile.getId() + ": " + e
            .getMessage())
          .setLocation(file.getLocation(missing ? "missing" : "exists"))
          .setException((Exception)e));
      return false;
    } 
    if (path == null)
      return false; 
    File f = new File(path);
    if (!f.isAbsolute())
      return false; 
    boolean fileExists = f.exists();
    return missing ? (!fileExists) : fileExists;
  }
  
  public boolean presentInConfig(Profile profile, ProfileActivationContext context, ModelProblemCollector problems) {
    Activation activation = profile.getActivation();
    if (activation == null)
      return false; 
    ActivationFile file = activation.getFile();
    if (file == null)
      return false; 
    return true;
  }
}
