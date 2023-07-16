package org.apache.maven.model.path;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;
import org.apache.maven.model.DistributionManagement;
import org.apache.maven.model.Model;
import org.apache.maven.model.Scm;
import org.apache.maven.model.Site;
import org.apache.maven.model.building.ModelBuildingRequest;

@Named
@Singleton
public class DefaultModelUrlNormalizer implements ModelUrlNormalizer {
  @Inject
  private UrlNormalizer urlNormalizer;
  
  public DefaultModelUrlNormalizer setUrlNormalizer(UrlNormalizer urlNormalizer) {
    this.urlNormalizer = urlNormalizer;
    return this;
  }
  
  public void normalize(Model model, ModelBuildingRequest request) {
    if (model == null)
      return; 
    model.setUrl(normalize(model.getUrl()));
    Scm scm = model.getScm();
    if (scm != null) {
      scm.setUrl(normalize(scm.getUrl()));
      scm.setConnection(normalize(scm.getConnection()));
      scm.setDeveloperConnection(normalize(scm.getDeveloperConnection()));
    } 
    DistributionManagement dist = model.getDistributionManagement();
    if (dist != null) {
      Site site = dist.getSite();
      if (site != null)
        site.setUrl(normalize(site.getUrl())); 
    } 
  }
  
  private String normalize(String url) {
    return this.urlNormalizer.normalize(url);
  }
}
