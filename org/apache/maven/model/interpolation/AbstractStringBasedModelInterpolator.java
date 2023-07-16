package org.apache.maven.model.interpolation;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import javax.inject.Inject;
import org.apache.maven.model.Model;
import org.apache.maven.model.building.ModelBuildingRequest;
import org.apache.maven.model.building.ModelProblemCollector;
import org.apache.maven.model.path.PathTranslator;
import org.apache.maven.model.path.UrlNormalizer;
import org.codehaus.plexus.interpolation.AbstractValueSource;
import org.codehaus.plexus.interpolation.InterpolationPostProcessor;
import org.codehaus.plexus.interpolation.MapBasedValueSource;
import org.codehaus.plexus.interpolation.ObjectBasedValueSource;
import org.codehaus.plexus.interpolation.PrefixAwareRecursionInterceptor;
import org.codehaus.plexus.interpolation.PrefixedObjectValueSource;
import org.codehaus.plexus.interpolation.PrefixedValueSourceWrapper;
import org.codehaus.plexus.interpolation.RecursionInterceptor;
import org.codehaus.plexus.interpolation.ValueSource;

public abstract class AbstractStringBasedModelInterpolator implements ModelInterpolator {
  private static final List<String> PROJECT_PREFIXES = Arrays.asList(new String[] { "pom.", "project." });
  
  private static final Collection<String> TRANSLATED_PATH_EXPRESSIONS;
  
  @Inject
  private PathTranslator pathTranslator;
  
  @Inject
  private UrlNormalizer urlNormalizer;
  
  @Inject
  private ModelVersionProcessor versionProcessor;
  
  static {
    Collection<String> translatedPrefixes = new HashSet<>();
    translatedPrefixes.add("build.directory");
    translatedPrefixes.add("build.outputDirectory");
    translatedPrefixes.add("build.testOutputDirectory");
    translatedPrefixes.add("build.sourceDirectory");
    translatedPrefixes.add("build.testSourceDirectory");
    translatedPrefixes.add("build.scriptSourceDirectory");
    translatedPrefixes.add("reporting.outputDirectory");
    TRANSLATED_PATH_EXPRESSIONS = translatedPrefixes;
  }
  
  public AbstractStringBasedModelInterpolator setPathTranslator(PathTranslator pathTranslator) {
    this.pathTranslator = pathTranslator;
    return this;
  }
  
  public AbstractStringBasedModelInterpolator setUrlNormalizer(UrlNormalizer urlNormalizer) {
    this.urlNormalizer = urlNormalizer;
    return this;
  }
  
  public AbstractStringBasedModelInterpolator setVersionPropertiesProcessor(ModelVersionProcessor processor) {
    this.versionProcessor = processor;
    return this;
  }
  
  protected List<ValueSource> createValueSources(Model model, final File projectDir, final ModelBuildingRequest config, ModelProblemCollector problems) {
    ProblemDetectingValueSource problemDetectingValueSource1, problemDetectingValueSource2;
    Properties modelProperties = model.getProperties();
    PrefixedObjectValueSource prefixedObjectValueSource = new PrefixedObjectValueSource(PROJECT_PREFIXES, model, false);
    if (config.getValidationLevel() >= 20)
      problemDetectingValueSource1 = new ProblemDetectingValueSource((ValueSource)prefixedObjectValueSource, "pom.", "project.", problems); 
    ObjectBasedValueSource objectBasedValueSource = new ObjectBasedValueSource(model);
    if (config.getValidationLevel() >= 20)
      problemDetectingValueSource2 = new ProblemDetectingValueSource((ValueSource)objectBasedValueSource, "", "project.", problems); 
    List<ValueSource> valueSources = new ArrayList<>(9);
    if (projectDir != null) {
      PrefixedValueSourceWrapper prefixedValueSourceWrapper1 = new PrefixedValueSourceWrapper((ValueSource)new AbstractValueSource(false) {
            public Object getValue(String expression) {
              if ("basedir".equals(expression))
                return projectDir.getAbsolutePath(); 
              return null;
            }
          }PROJECT_PREFIXES, true);
      valueSources.add(prefixedValueSourceWrapper1);
      PrefixedValueSourceWrapper prefixedValueSourceWrapper2 = new PrefixedValueSourceWrapper((ValueSource)new AbstractValueSource(false) {
            public Object getValue(String expression) {
              if ("baseUri".equals(expression))
                return projectDir.getAbsoluteFile().toPath().toUri().toASCIIString(); 
              return null;
            }
          }PROJECT_PREFIXES, false);
      valueSources.add(prefixedValueSourceWrapper2);
      valueSources.add(new BuildTimestampValueSource(config.getBuildStartTime(), modelProperties));
    } 
    valueSources.add(problemDetectingValueSource1);
    valueSources.add(new MapBasedValueSource(config.getUserProperties()));
    this.versionProcessor.overwriteModelProperties(modelProperties, config);
    valueSources.add(new MapBasedValueSource(modelProperties));
    valueSources.add(new MapBasedValueSource(config.getSystemProperties()));
    valueSources.add(new AbstractValueSource(false) {
          public Object getValue(String expression) {
            return config.getSystemProperties().getProperty("env." + expression);
          }
        });
    valueSources.add(problemDetectingValueSource2);
    return valueSources;
  }
  
  protected List<? extends InterpolationPostProcessor> createPostProcessors(Model model, File projectDir, ModelBuildingRequest config) {
    List<InterpolationPostProcessor> processors = new ArrayList<>(2);
    if (projectDir != null)
      processors.add(new PathTranslatingPostProcessor(PROJECT_PREFIXES, TRANSLATED_PATH_EXPRESSIONS, projectDir, this.pathTranslator)); 
    processors.add(new UrlNormalizingPostProcessor(this.urlNormalizer));
    return processors;
  }
  
  protected RecursionInterceptor createRecursionInterceptor() {
    return (RecursionInterceptor)new PrefixAwareRecursionInterceptor(PROJECT_PREFIXES);
  }
}
