package org.apache.maven.model.profile.activation;

import javax.inject.Named;
import javax.inject.Singleton;
import org.apache.maven.model.Activation;
import org.apache.maven.model.ActivationProperty;
import org.apache.maven.model.Profile;
import org.apache.maven.model.building.ModelProblem;
import org.apache.maven.model.building.ModelProblemCollector;
import org.apache.maven.model.building.ModelProblemCollectorRequest;
import org.apache.maven.model.profile.ProfileActivationContext;
import org.codehaus.plexus.util.StringUtils;

@Named("property")
@Singleton
public class PropertyProfileActivator implements ProfileActivator {
  public boolean isActive(Profile profile, ProfileActivationContext context, ModelProblemCollector problems) {
    Activation activation = profile.getActivation();
    if (activation == null)
      return false; 
    ActivationProperty property = activation.getProperty();
    if (property == null)
      return false; 
    String name = property.getName();
    boolean reverseName = false;
    if (name != null && name.startsWith("!")) {
      reverseName = true;
      name = name.substring(1);
    } 
    if (name == null || name.length() <= 0) {
      problems.add((new ModelProblemCollectorRequest(ModelProblem.Severity.ERROR, ModelProblem.Version.BASE))
          .setMessage("The property name is required to activate the profile " + profile.getId())
          .setLocation(property.getLocation("")));
      return false;
    } 
    String sysValue = (String)context.getUserProperties().get(name);
    if (sysValue == null)
      sysValue = (String)context.getSystemProperties().get(name); 
    String propValue = property.getValue();
    if (StringUtils.isNotEmpty(propValue)) {
      boolean reverseValue = false;
      if (propValue.startsWith("!")) {
        reverseValue = true;
        propValue = propValue.substring(1);
      } 
      boolean bool1 = propValue.equals(sysValue);
      return reverseValue ? (!bool1) : bool1;
    } 
    boolean result = StringUtils.isNotEmpty(sysValue);
    return reverseName ? (!result) : result;
  }
  
  public boolean presentInConfig(Profile profile, ProfileActivationContext context, ModelProblemCollector problems) {
    Activation activation = profile.getActivation();
    if (activation == null)
      return false; 
    ActivationProperty property = activation.getProperty();
    if (property == null)
      return false; 
    return true;
  }
}
