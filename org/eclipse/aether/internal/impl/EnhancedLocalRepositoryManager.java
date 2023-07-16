package org.eclipse.aether.internal.impl;

import java.io.File;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;
import org.eclipse.aether.RepositorySystemSession;
import org.eclipse.aether.artifact.Artifact;
import org.eclipse.aether.repository.LocalArtifactRegistration;
import org.eclipse.aether.repository.LocalArtifactRequest;
import org.eclipse.aether.repository.LocalArtifactResult;
import org.eclipse.aether.repository.RemoteRepository;
import org.eclipse.aether.util.ConfigUtils;

class EnhancedLocalRepositoryManager extends SimpleLocalRepositoryManager {
  private static final String LOCAL_REPO_ID = "";
  
  private final String trackingFilename;
  
  private final TrackingFileManager trackingFileManager;
  
  EnhancedLocalRepositoryManager(File basedir, RepositorySystemSession session) {
    super(basedir, "enhanced");
    String filename = ConfigUtils.getString(session, "", new String[] { "aether.enhancedLocalRepository.trackingFilename" });
    if (filename.length() <= 0 || filename.contains("/") || filename.contains("\\") || filename
      .contains(".."))
      filename = "_remote.repositories"; 
    this.trackingFilename = filename;
    this.trackingFileManager = new TrackingFileManager();
  }
  
  public LocalArtifactResult find(RepositorySystemSession session, LocalArtifactRequest request) {
    String path = getPathForArtifact(request.getArtifact(), false);
    File file = new File(getRepository().getBasedir(), path);
    LocalArtifactResult result = new LocalArtifactResult(request);
    if (file.isFile()) {
      result.setFile(file);
      Properties props = readRepos(file);
      if (props.get(getKey(file, "")) != null) {
        result.setAvailable(true);
      } else {
        String context = request.getContext();
        for (RemoteRepository repository : request.getRepositories()) {
          if (props.get(getKey(file, getRepositoryKey(repository, context))) != null) {
            result.setAvailable(true);
            result.setRepository(repository);
            break;
          } 
        } 
        if (!result.isAvailable() && !isTracked(props, file))
          result.setAvailable(true); 
      } 
    } 
    return result;
  }
  
  public void add(RepositorySystemSession session, LocalArtifactRegistration request) {
    Collection<String> repositories;
    if (request.getRepository() == null) {
      repositories = Collections.singleton("");
    } else {
      repositories = getRepositoryKeys(request.getRepository(), request.getContexts());
    } 
    addArtifact(request.getArtifact(), repositories, (request.getRepository() == null));
  }
  
  private Collection<String> getRepositoryKeys(RemoteRepository repository, Collection<String> contexts) {
    Collection<String> keys = new HashSet<>();
    if (contexts != null)
      for (String context : contexts)
        keys.add(getRepositoryKey(repository, context));  
    return keys;
  }
  
  private void addArtifact(Artifact artifact, Collection<String> repositories, boolean local) {
    String path = getPathForArtifact(Objects.<Artifact>requireNonNull(artifact, "artifact cannot be null"), local);
    File file = new File(getRepository().getBasedir(), path);
    addRepo(file, repositories);
  }
  
  private Properties readRepos(File artifactFile) {
    File trackingFile = getTrackingFile(artifactFile);
    Properties props = this.trackingFileManager.read(trackingFile);
    return (props != null) ? props : new Properties();
  }
  
  private void addRepo(File artifactFile, Collection<String> repositories) {
    Map<String, String> updates = new HashMap<>();
    for (String repository : repositories)
      updates.put(getKey(artifactFile, repository), ""); 
    File trackingFile = getTrackingFile(artifactFile);
    this.trackingFileManager.update(trackingFile, updates);
  }
  
  private File getTrackingFile(File artifactFile) {
    return new File(artifactFile.getParentFile(), this.trackingFilename);
  }
  
  private String getKey(File file, String repository) {
    return file.getName() + '>' + repository;
  }
  
  private boolean isTracked(Properties props, File file) {
    if (props != null) {
      String keyPrefix = file.getName() + '>';
      for (Object key : props.keySet()) {
        if (key.toString().startsWith(keyPrefix))
          return true; 
      } 
    } 
    return false;
  }
}
