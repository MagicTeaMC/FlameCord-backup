package org.apache.maven.model.plugin;

import javax.inject.Named;
import javax.inject.Singleton;
import org.apache.maven.model.Model;
import org.apache.maven.model.ReportPlugin;
import org.apache.maven.model.ReportSet;
import org.apache.maven.model.Reporting;
import org.apache.maven.model.building.ModelBuildingRequest;
import org.apache.maven.model.building.ModelProblemCollector;
import org.codehaus.plexus.util.xml.Xpp3Dom;

@Named
@Singleton
public class DefaultReportConfigurationExpander implements ReportConfigurationExpander {
  public void expandPluginConfiguration(Model model, ModelBuildingRequest request, ModelProblemCollector problems) {
    Reporting reporting = model.getReporting();
    if (reporting != null)
      for (ReportPlugin reportPlugin : reporting.getPlugins()) {
        Xpp3Dom parentDom = (Xpp3Dom)reportPlugin.getConfiguration();
        if (parentDom != null)
          for (ReportSet execution : reportPlugin.getReportSets()) {
            Xpp3Dom childDom = (Xpp3Dom)execution.getConfiguration();
            childDom = Xpp3Dom.mergeXpp3Dom(childDom, new Xpp3Dom(parentDom));
            execution.setConfiguration(childDom);
          }  
      }  
  }
}
