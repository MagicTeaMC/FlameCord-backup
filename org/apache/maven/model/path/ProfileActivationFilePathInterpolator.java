package org.apache.maven.model.path;

import java.io.File;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;
import org.apache.maven.model.profile.ProfileActivationContext;
import org.codehaus.plexus.interpolation.AbstractValueSource;
import org.codehaus.plexus.interpolation.InterpolationException;
import org.codehaus.plexus.interpolation.MapBasedValueSource;
import org.codehaus.plexus.interpolation.RegexBasedInterpolator;
import org.codehaus.plexus.interpolation.ValueSource;

@Named
@Singleton
public class ProfileActivationFilePathInterpolator {
  @Inject
  private PathTranslator pathTranslator;
  
  public ProfileActivationFilePathInterpolator setPathTranslator(PathTranslator pathTranslator) {
    this.pathTranslator = pathTranslator;
    return this;
  }
  
  public String interpolate(String path, ProfileActivationContext context) throws InterpolationException {
    if (path == null)
      return null; 
    RegexBasedInterpolator interpolator = new RegexBasedInterpolator();
    final File basedir = context.getProjectDirectory();
    if (basedir != null) {
      interpolator.addValueSource((ValueSource)new AbstractValueSource(false) {
            public Object getValue(String expression) {
              if ("basedir".equals(expression))
                return basedir.getAbsolutePath(); 
              return null;
            }
          });
    } else if (path.contains("${basedir}")) {
      return null;
    } 
    interpolator.addValueSource((ValueSource)new MapBasedValueSource(context.getProjectProperties()));
    interpolator.addValueSource((ValueSource)new MapBasedValueSource(context.getUserProperties()));
    interpolator.addValueSource((ValueSource)new MapBasedValueSource(context.getSystemProperties()));
    String absolutePath = interpolator.interpolate(path, "");
    return this.pathTranslator.alignToBaseDirectory(absolutePath, basedir);
  }
}
