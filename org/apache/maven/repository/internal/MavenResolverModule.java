package org.apache.maven.repository.internal;

import com.google.inject.AbstractModule;
import com.google.inject.Module;
import com.google.inject.Provides;
import com.google.inject.name.Names;
import java.lang.annotation.Annotation;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import javax.inject.Named;
import javax.inject.Singleton;
import org.apache.maven.model.building.DefaultModelBuilderFactory;
import org.apache.maven.model.building.ModelBuilder;
import org.eclipse.aether.impl.ArtifactDescriptorReader;
import org.eclipse.aether.impl.MetadataGeneratorFactory;
import org.eclipse.aether.impl.VersionRangeResolver;
import org.eclipse.aether.impl.VersionResolver;
import org.eclipse.aether.impl.guice.AetherModule;

public final class MavenResolverModule extends AbstractModule {
  protected void configure() {
    install((Module)new AetherModule());
    bind(ArtifactDescriptorReader.class).to(DefaultArtifactDescriptorReader.class).in(Singleton.class);
    bind(VersionResolver.class).to(DefaultVersionResolver.class).in(Singleton.class);
    bind(VersionRangeResolver.class).to(DefaultVersionRangeResolver.class).in(Singleton.class);
    bind(MetadataGeneratorFactory.class).annotatedWith((Annotation)Names.named("snapshot"))
      .to(SnapshotMetadataGeneratorFactory.class).in(Singleton.class);
    bind(MetadataGeneratorFactory.class).annotatedWith((Annotation)Names.named("versions"))
      .to(VersionsMetadataGeneratorFactory.class).in(Singleton.class);
    bind(ModelBuilder.class).toInstance((new DefaultModelBuilderFactory()).newInstance());
  }
  
  @Provides
  @Singleton
  Set<MetadataGeneratorFactory> provideMetadataGeneratorFactories(@Named("snapshot") MetadataGeneratorFactory snapshot, @Named("versions") MetadataGeneratorFactory versions) {
    Set<MetadataGeneratorFactory> factories = new HashSet<>(2);
    factories.add(snapshot);
    factories.add(versions);
    return Collections.unmodifiableSet(factories);
  }
}
