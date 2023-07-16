package org.apache.maven.model.path;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;
import org.apache.maven.model.Build;
import org.apache.maven.model.Model;
import org.apache.maven.model.Reporting;
import org.apache.maven.model.Resource;
import org.apache.maven.model.building.ModelBuildingRequest;

@Named
@Singleton
public class DefaultModelPathTranslator implements ModelPathTranslator {
  @Inject
  private PathTranslator pathTranslator;
  
  public DefaultModelPathTranslator setPathTranslator(PathTranslator pathTranslator) {
    this.pathTranslator = pathTranslator;
    return this;
  }
  
  public void alignToBaseDirectory(Model model, File basedir, ModelBuildingRequest request) {
    if (model == null || basedir == null)
      return; 
    Build build = model.getBuild();
    if (build != null) {
      build.setDirectory(alignToBaseDirectory(build.getDirectory(), basedir));
      build.setSourceDirectory(alignToBaseDirectory(build.getSourceDirectory(), basedir));
      build.setTestSourceDirectory(alignToBaseDirectory(build.getTestSourceDirectory(), basedir));
      build.setScriptSourceDirectory(alignToBaseDirectory(build.getScriptSourceDirectory(), basedir));
      for (Resource resource : build.getResources())
        resource.setDirectory(alignToBaseDirectory(resource.getDirectory(), basedir)); 
      for (Resource resource : build.getTestResources())
        resource.setDirectory(alignToBaseDirectory(resource.getDirectory(), basedir)); 
      if (build.getFilters() != null) {
        List<String> filters = new ArrayList<>(build.getFilters().size());
        for (String filter : build.getFilters())
          filters.add(alignToBaseDirectory(filter, basedir)); 
        build.setFilters(filters);
      } 
      build.setOutputDirectory(alignToBaseDirectory(build.getOutputDirectory(), basedir));
      build.setTestOutputDirectory(alignToBaseDirectory(build.getTestOutputDirectory(), basedir));
    } 
    Reporting reporting = model.getReporting();
    if (reporting != null)
      reporting.setOutputDirectory(alignToBaseDirectory(reporting.getOutputDirectory(), basedir)); 
  }
  
  private String alignToBaseDirectory(String path, File basedir) {
    return this.pathTranslator.alignToBaseDirectory(path, basedir);
  }
}
