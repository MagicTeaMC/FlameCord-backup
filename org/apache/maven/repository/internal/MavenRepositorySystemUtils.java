package org.apache.maven.repository.internal;

import java.util.Properties;
import org.eclipse.aether.DefaultRepositorySystemSession;
import org.eclipse.aether.artifact.ArtifactType;
import org.eclipse.aether.artifact.ArtifactTypeRegistry;
import org.eclipse.aether.artifact.DefaultArtifactType;
import org.eclipse.aether.collection.DependencyGraphTransformer;
import org.eclipse.aether.collection.DependencyManager;
import org.eclipse.aether.collection.DependencySelector;
import org.eclipse.aether.collection.DependencyTraverser;
import org.eclipse.aether.impl.ArtifactDescriptorReader;
import org.eclipse.aether.impl.DefaultServiceLocator;
import org.eclipse.aether.impl.MetadataGeneratorFactory;
import org.eclipse.aether.impl.VersionRangeResolver;
import org.eclipse.aether.impl.VersionResolver;
import org.eclipse.aether.resolution.ArtifactDescriptorPolicy;
import org.eclipse.aether.util.artifact.DefaultArtifactTypeRegistry;
import org.eclipse.aether.util.graph.manager.ClassicDependencyManager;
import org.eclipse.aether.util.graph.selector.AndDependencySelector;
import org.eclipse.aether.util.graph.selector.ExclusionDependencySelector;
import org.eclipse.aether.util.graph.selector.OptionalDependencySelector;
import org.eclipse.aether.util.graph.selector.ScopeDependencySelector;
import org.eclipse.aether.util.graph.transformer.ChainedDependencyGraphTransformer;
import org.eclipse.aether.util.graph.transformer.ConflictResolver;
import org.eclipse.aether.util.graph.transformer.JavaDependencyContextRefiner;
import org.eclipse.aether.util.graph.transformer.JavaScopeDeriver;
import org.eclipse.aether.util.graph.transformer.JavaScopeSelector;
import org.eclipse.aether.util.graph.transformer.NearestVersionSelector;
import org.eclipse.aether.util.graph.transformer.SimpleOptionalitySelector;
import org.eclipse.aether.util.graph.traverser.FatArtifactTraverser;
import org.eclipse.aether.util.repository.SimpleArtifactDescriptorPolicy;

public final class MavenRepositorySystemUtils {
  public static DefaultServiceLocator newServiceLocator() {
    DefaultServiceLocator locator = new DefaultServiceLocator();
    locator.addService(ArtifactDescriptorReader.class, DefaultArtifactDescriptorReader.class);
    locator.addService(VersionResolver.class, DefaultVersionResolver.class);
    locator.addService(VersionRangeResolver.class, DefaultVersionRangeResolver.class);
    locator.addService(MetadataGeneratorFactory.class, SnapshotMetadataGeneratorFactory.class);
    locator.addService(MetadataGeneratorFactory.class, VersionsMetadataGeneratorFactory.class);
    return locator;
  }
  
  public static DefaultRepositorySystemSession newSession() {
    DefaultRepositorySystemSession session = new DefaultRepositorySystemSession();
    FatArtifactTraverser fatArtifactTraverser = new FatArtifactTraverser();
    session.setDependencyTraverser((DependencyTraverser)fatArtifactTraverser);
    ClassicDependencyManager classicDependencyManager = new ClassicDependencyManager();
    session.setDependencyManager((DependencyManager)classicDependencyManager);
    AndDependencySelector andDependencySelector = new AndDependencySelector(new DependencySelector[] { (DependencySelector)new ScopeDependencySelector(new String[] { "test", "provided" }), (DependencySelector)new OptionalDependencySelector(), (DependencySelector)new ExclusionDependencySelector() });
    session.setDependencySelector((DependencySelector)andDependencySelector);
    ConflictResolver conflictResolver = new ConflictResolver((ConflictResolver.VersionSelector)new NearestVersionSelector(), (ConflictResolver.ScopeSelector)new JavaScopeSelector(), (ConflictResolver.OptionalitySelector)new SimpleOptionalitySelector(), (ConflictResolver.ScopeDeriver)new JavaScopeDeriver());
    ChainedDependencyGraphTransformer chainedDependencyGraphTransformer = new ChainedDependencyGraphTransformer(new DependencyGraphTransformer[] { (DependencyGraphTransformer)conflictResolver, (DependencyGraphTransformer)new JavaDependencyContextRefiner() });
    session.setDependencyGraphTransformer((DependencyGraphTransformer)chainedDependencyGraphTransformer);
    DefaultArtifactTypeRegistry stereotypes = new DefaultArtifactTypeRegistry();
    stereotypes.add((ArtifactType)new DefaultArtifactType("pom"));
    stereotypes.add((ArtifactType)new DefaultArtifactType("maven-plugin", "jar", "", "java"));
    stereotypes.add((ArtifactType)new DefaultArtifactType("jar", "jar", "", "java"));
    stereotypes.add((ArtifactType)new DefaultArtifactType("ejb", "jar", "", "java"));
    stereotypes.add((ArtifactType)new DefaultArtifactType("ejb-client", "jar", "client", "java"));
    stereotypes.add((ArtifactType)new DefaultArtifactType("test-jar", "jar", "tests", "java"));
    stereotypes.add((ArtifactType)new DefaultArtifactType("javadoc", "jar", "javadoc", "java"));
    stereotypes.add((ArtifactType)new DefaultArtifactType("java-source", "jar", "sources", "java", false, false));
    stereotypes.add((ArtifactType)new DefaultArtifactType("war", "war", "", "java", false, true));
    stereotypes.add((ArtifactType)new DefaultArtifactType("ear", "ear", "", "java", false, true));
    stereotypes.add((ArtifactType)new DefaultArtifactType("rar", "rar", "", "java", false, true));
    stereotypes.add((ArtifactType)new DefaultArtifactType("par", "par", "", "java", false, true));
    session.setArtifactTypeRegistry((ArtifactTypeRegistry)stereotypes);
    session.setArtifactDescriptorPolicy((ArtifactDescriptorPolicy)new SimpleArtifactDescriptorPolicy(true, true));
    Properties systemProperties = new Properties();
    Properties sysProp = System.getProperties();
    synchronized (sysProp) {
      systemProperties.putAll(sysProp);
    } 
    session.setSystemProperties(systemProperties);
    session.setConfigProperties(systemProperties);
    return session;
  }
}
