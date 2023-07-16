package net.md_5.bungee.api.plugin;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.maven.repository.internal.MavenRepositorySystemUtils;
import org.eclipse.aether.DefaultRepositorySystemSession;
import org.eclipse.aether.RepositorySystem;
import org.eclipse.aether.RepositorySystemSession;
import org.eclipse.aether.artifact.Artifact;
import org.eclipse.aether.artifact.DefaultArtifact;
import org.eclipse.aether.collection.CollectRequest;
import org.eclipse.aether.connector.basic.BasicRepositoryConnectorFactory;
import org.eclipse.aether.graph.Dependency;
import org.eclipse.aether.impl.DefaultServiceLocator;
import org.eclipse.aether.repository.LocalRepository;
import org.eclipse.aether.repository.RemoteRepository;
import org.eclipse.aether.resolution.ArtifactResult;
import org.eclipse.aether.resolution.DependencyRequest;
import org.eclipse.aether.resolution.DependencyResolutionException;
import org.eclipse.aether.resolution.DependencyResult;
import org.eclipse.aether.spi.connector.RepositoryConnectorFactory;
import org.eclipse.aether.spi.connector.transport.TransporterFactory;
import org.eclipse.aether.transfer.AbstractTransferListener;
import org.eclipse.aether.transfer.TransferCancelledException;
import org.eclipse.aether.transfer.TransferEvent;
import org.eclipse.aether.transfer.TransferListener;
import org.eclipse.aether.transport.http.HttpTransporterFactory;

class LibraryLoader {
  private final Logger logger;
  
  private final RepositorySystem repository;
  
  private final DefaultRepositorySystemSession session;
  
  private final List<RemoteRepository> repositories;
  
  public LibraryLoader(final Logger logger) {
    this.logger = logger;
    DefaultServiceLocator locator = MavenRepositorySystemUtils.newServiceLocator();
    locator.addService(RepositoryConnectorFactory.class, BasicRepositoryConnectorFactory.class);
    locator.addService(TransporterFactory.class, HttpTransporterFactory.class);
    this.repository = (RepositorySystem)locator.getService(RepositorySystem.class);
    this.session = MavenRepositorySystemUtils.newSession();
    this.session.setChecksumPolicy("fail");
    this.session.setLocalRepositoryManager(this.repository.newLocalRepositoryManager((RepositorySystemSession)this.session, new LocalRepository("libraries")));
    this.session.setTransferListener((TransferListener)new AbstractTransferListener() {
          public void transferStarted(TransferEvent event) throws TransferCancelledException {
            logger.log(Level.INFO, "Downloading {0}", event.getResource().getRepositoryUrl() + event.getResource().getResourceName());
          }
        });
    this.session.setReadOnly();
    this.repositories = this.repository.newResolutionRepositories((RepositorySystemSession)this.session, Arrays.asList(new RemoteRepository[] { (new RemoteRepository.Builder("central", "default", "https://repo.maven.apache.org/maven2")).build() }));
  }
  
  public ClassLoader createLoader(PluginDescription desc) {
    DependencyResult result;
    if (desc.getLibraries().isEmpty())
      return null; 
    this.logger.log(Level.INFO, "[{0}] Loading {1} libraries... please wait", new Object[] { desc
          
          .getName(), Integer.valueOf(desc.getLibraries().size()) });
    List<Dependency> dependencies = new ArrayList<>();
    for (String library : desc.getLibraries()) {
      DefaultArtifact defaultArtifact = new DefaultArtifact(library);
      Dependency dependency = new Dependency((Artifact)defaultArtifact, null);
      dependencies.add(dependency);
    } 
    try {
      result = this.repository.resolveDependencies((RepositorySystemSession)this.session, new DependencyRequest(new CollectRequest((Dependency)null, dependencies, this.repositories), null));
    } catch (DependencyResolutionException ex) {
      throw new RuntimeException("Error resolving libraries", ex);
    } 
    List<URL> jarFiles = new ArrayList<>();
    for (ArtifactResult artifact : result.getArtifactResults()) {
      URL url;
      File file = artifact.getArtifact().getFile();
      try {
        url = file.toURI().toURL();
      } catch (MalformedURLException ex) {
        throw new AssertionError(ex);
      } 
      jarFiles.add(url);
      this.logger.log(Level.INFO, "[{0}] Loaded library {1}", new Object[] { desc
            
            .getName(), file });
    } 
    URLClassLoader loader = new URLClassLoader(jarFiles.<URL>toArray(new URL[0]));
    return loader;
  }
}
