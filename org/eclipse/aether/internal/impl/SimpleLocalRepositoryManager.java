package org.eclipse.aether.internal.impl;

import java.io.File;
import java.util.Objects;
import java.util.SortedSet;
import java.util.TreeSet;
import org.eclipse.aether.RepositorySystemSession;
import org.eclipse.aether.artifact.Artifact;
import org.eclipse.aether.metadata.Metadata;
import org.eclipse.aether.repository.LocalArtifactRegistration;
import org.eclipse.aether.repository.LocalArtifactRequest;
import org.eclipse.aether.repository.LocalArtifactResult;
import org.eclipse.aether.repository.LocalMetadataRegistration;
import org.eclipse.aether.repository.LocalMetadataRequest;
import org.eclipse.aether.repository.LocalMetadataResult;
import org.eclipse.aether.repository.LocalRepository;
import org.eclipse.aether.repository.LocalRepositoryManager;
import org.eclipse.aether.repository.RemoteRepository;

class SimpleLocalRepositoryManager implements LocalRepositoryManager {
  private final LocalRepository repository;
  
  SimpleLocalRepositoryManager(File basedir) {
    this(basedir, "simple");
  }
  
  SimpleLocalRepositoryManager(String basedir) {
    this((basedir != null) ? new File(basedir) : null, "simple");
  }
  
  SimpleLocalRepositoryManager(File basedir, String type) {
    Objects.requireNonNull(basedir, "base directory cannot be null");
    this.repository = new LocalRepository(basedir.getAbsoluteFile(), type);
  }
  
  public LocalRepository getRepository() {
    return this.repository;
  }
  
  String getPathForArtifact(Artifact artifact, boolean local) {
    StringBuilder path = new StringBuilder(128);
    path.append(artifact.getGroupId().replace('.', '/')).append('/');
    path.append(artifact.getArtifactId()).append('/');
    path.append(artifact.getBaseVersion()).append('/');
    path.append(artifact.getArtifactId()).append('-');
    if (local) {
      path.append(artifact.getBaseVersion());
    } else {
      path.append(artifact.getVersion());
    } 
    if (artifact.getClassifier().length() > 0)
      path.append('-').append(artifact.getClassifier()); 
    if (artifact.getExtension().length() > 0)
      path.append('.').append(artifact.getExtension()); 
    return path.toString();
  }
  
  public String getPathForLocalArtifact(Artifact artifact) {
    return getPathForArtifact(artifact, true);
  }
  
  public String getPathForRemoteArtifact(Artifact artifact, RemoteRepository repository, String context) {
    return getPathForArtifact(artifact, false);
  }
  
  public String getPathForLocalMetadata(Metadata metadata) {
    return getPath(metadata, "local");
  }
  
  public String getPathForRemoteMetadata(Metadata metadata, RemoteRepository repository, String context) {
    return getPath(metadata, getRepositoryKey(repository, context));
  }
  
  String getRepositoryKey(RemoteRepository repository, String context) {
    String key;
    if (repository.isRepositoryManager()) {
      StringBuilder buffer = new StringBuilder(128);
      buffer.append(repository.getId());
      buffer.append('-');
      SortedSet<String> subKeys = new TreeSet<>();
      for (RemoteRepository mirroredRepo : repository.getMirroredRepositories())
        subKeys.add(mirroredRepo.getId()); 
      SimpleDigest digest = new SimpleDigest();
      digest.update(context);
      for (String subKey : subKeys)
        digest.update(subKey); 
      buffer.append(digest.digest());
      key = buffer.toString();
    } else {
      key = repository.getId();
    } 
    return key;
  }
  
  private String getPath(Metadata metadata, String repositoryKey) {
    StringBuilder path = new StringBuilder(128);
    if (metadata.getGroupId().length() > 0) {
      path.append(metadata.getGroupId().replace('.', '/')).append('/');
      if (metadata.getArtifactId().length() > 0) {
        path.append(metadata.getArtifactId()).append('/');
        if (metadata.getVersion().length() > 0)
          path.append(metadata.getVersion()).append('/'); 
      } 
    } 
    path.append(insertRepositoryKey(metadata.getType(), repositoryKey));
    return path.toString();
  }
  
  private String insertRepositoryKey(String filename, String repositoryKey) {
    String result;
    int idx = filename.indexOf('.');
    if (idx < 0) {
      result = filename + '-' + repositoryKey;
    } else {
      result = filename.substring(0, idx) + '-' + repositoryKey + filename.substring(idx);
    } 
    return result;
  }
  
  public LocalArtifactResult find(RepositorySystemSession session, LocalArtifactRequest request) {
    String path = getPathForArtifact(request.getArtifact(), false);
    File file = new File(getRepository().getBasedir(), path);
    LocalArtifactResult result = new LocalArtifactResult(request);
    if (file.isFile()) {
      result.setFile(file);
      result.setAvailable(true);
    } 
    return result;
  }
  
  public void add(RepositorySystemSession session, LocalArtifactRegistration request) {}
  
  public String toString() {
    return String.valueOf(getRepository());
  }
  
  public LocalMetadataResult find(RepositorySystemSession session, LocalMetadataRequest request) {
    String path;
    LocalMetadataResult result = new LocalMetadataResult(request);
    Metadata metadata = request.getMetadata();
    String context = request.getContext();
    RemoteRepository remote = request.getRepository();
    if (remote != null) {
      path = getPathForRemoteMetadata(metadata, remote, context);
    } else {
      path = getPathForLocalMetadata(metadata);
    } 
    File file = new File(getRepository().getBasedir(), path);
    if (file.isFile())
      result.setFile(file); 
    return result;
  }
  
  public void add(RepositorySystemSession session, LocalMetadataRegistration request) {}
}
