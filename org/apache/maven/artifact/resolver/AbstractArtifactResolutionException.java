package org.apache.maven.artifact.resolver;

import java.util.Iterator;
import java.util.List;
import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.repository.ArtifactRepository;
import org.apache.maven.artifact.repository.ArtifactRepositoryPolicy;

public class AbstractArtifactResolutionException extends Exception {
  private String groupId;
  
  private String artifactId;
  
  private String version;
  
  private String type;
  
  private String classifier;
  
  private Artifact artifact;
  
  private List<ArtifactRepository> remoteRepositories;
  
  private final String originalMessage;
  
  private final String path;
  
  static final String LS = System.lineSeparator();
  
  protected AbstractArtifactResolutionException(String message, String groupId, String artifactId, String version, String type, String classifier, List<ArtifactRepository> remoteRepositories, List<String> path) {
    this(message, groupId, artifactId, version, type, classifier, remoteRepositories, path, null);
  }
  
  protected AbstractArtifactResolutionException(String message, String groupId, String artifactId, String version, String type, String classifier, List<ArtifactRepository> remoteRepositories, List<String> path, Throwable t) {
    super(constructMessageBase(message, groupId, artifactId, version, type, remoteRepositories, path), t);
    this.originalMessage = message;
    this.groupId = groupId;
    this.artifactId = artifactId;
    this.type = type;
    this.classifier = classifier;
    this.version = version;
    this.remoteRepositories = remoteRepositories;
    this.path = constructArtifactPath(path, "");
  }
  
  protected AbstractArtifactResolutionException(String message, Artifact artifact) {
    this(message, artifact, null);
  }
  
  protected AbstractArtifactResolutionException(String message, Artifact artifact, List<ArtifactRepository> remoteRepositories) {
    this(message, artifact, remoteRepositories, null);
  }
  
  protected AbstractArtifactResolutionException(String message, Artifact artifact, List<ArtifactRepository> remoteRepositories, Throwable t) {
    this(message, artifact.getGroupId(), artifact.getArtifactId(), artifact.getVersion(), artifact.getType(), artifact
        .getClassifier(), remoteRepositories, artifact.getDependencyTrail(), t);
    this.artifact = artifact;
  }
  
  public Artifact getArtifact() {
    return this.artifact;
  }
  
  public String getGroupId() {
    return this.groupId;
  }
  
  public String getArtifactId() {
    return this.artifactId;
  }
  
  public String getVersion() {
    return this.version;
  }
  
  public String getType() {
    return this.type;
  }
  
  public String getClassifier() {
    return this.classifier;
  }
  
  public String getPath() {
    return this.path;
  }
  
  public List<ArtifactRepository> getRemoteRepositories() {
    return this.remoteRepositories;
  }
  
  public String getOriginalMessage() {
    return this.originalMessage;
  }
  
  protected static String constructArtifactPath(List<String> path, String indentation) {
    StringBuilder sb = new StringBuilder();
    if (path != null) {
      sb.append(LS);
      sb.append(indentation);
      sb.append("Path to dependency: ");
      sb.append(LS);
      int num = 1;
      for (Iterator<String> i = path.iterator(); i.hasNext(); num++) {
        sb.append(indentation);
        sb.append('\t');
        sb.append(num);
        sb.append(") ");
        sb.append(i.next());
        sb.append(LS);
      } 
    } 
    return sb.toString();
  }
  
  private static String constructMessageBase(String message, String groupId, String artifactId, String version, String type, List<ArtifactRepository> remoteRepositories, List<String> path) {
    StringBuilder sb = new StringBuilder();
    sb.append(message);
    if (message == null || !message.contains("from the specified remote repositories:")) {
      sb.append(LS);
      sb.append("  ").append(groupId).append(':').append(artifactId).append(':').append(type).append(':')
        .append(version);
      sb.append(LS);
      if (remoteRepositories != null) {
        sb.append(LS);
        sb.append("from the specified remote repositories:");
        sb.append(LS).append("  ");
        if (remoteRepositories.isEmpty())
          sb.append("(none)"); 
        for (Iterator<ArtifactRepository> i = remoteRepositories.iterator(); i.hasNext(); ) {
          ArtifactRepository remoteRepository = i.next();
          sb.append(remoteRepository.getId());
          sb.append(" (");
          sb.append(remoteRepository.getUrl());
          ArtifactRepositoryPolicy releases = remoteRepository.getReleases();
          if (releases != null)
            sb.append(", releases=").append(releases.isEnabled()); 
          ArtifactRepositoryPolicy snapshots = remoteRepository.getSnapshots();
          if (snapshots != null)
            sb.append(", snapshots=").append(snapshots.isEnabled()); 
          sb.append(')');
          if (i.hasNext())
            sb.append(',').append(LS).append("  "); 
        } 
      } 
      sb.append(constructArtifactPath(path, ""));
      sb.append(LS);
    } 
    return sb.toString();
  }
  
  protected static String constructMissingArtifactMessage(String message, String indentation, String groupId, String artifactId, String version, String type, String classifier, String downloadUrl, List<String> path) {
    StringBuilder sb = new StringBuilder(message);
    if (!"pom".equals(type)) {
      if (downloadUrl != null) {
        sb.append(LS);
        sb.append(LS);
        sb.append(indentation);
        sb.append("Try downloading the file manually from: ");
        sb.append(LS);
        sb.append(indentation);
        sb.append("    ");
        sb.append(downloadUrl);
      } else {
        sb.append(LS);
        sb.append(LS);
        sb.append(indentation);
        sb.append("Try downloading the file manually from the project website.");
      } 
      sb.append(LS);
      sb.append(LS);
      sb.append(indentation);
      sb.append("Then, install it using the command: ");
      sb.append(LS);
      sb.append(indentation);
      sb.append("    mvn install:install-file -DgroupId=");
      sb.append(groupId);
      sb.append(" -DartifactId=");
      sb.append(artifactId);
      sb.append(" -Dversion=");
      sb.append(version);
      if (classifier != null && !classifier.equals("")) {
        sb.append(" -Dclassifier=");
        sb.append(classifier);
      } 
      sb.append(" -Dpackaging=");
      sb.append(type);
      sb.append(" -Dfile=/path/to/file");
      sb.append(LS);
      sb.append(LS);
      sb.append(indentation);
      sb.append("Alternatively, if you host your own repository you can deploy the file there: ");
      sb.append(LS);
      sb.append(indentation);
      sb.append("    mvn deploy:deploy-file -DgroupId=");
      sb.append(groupId);
      sb.append(" -DartifactId=");
      sb.append(artifactId);
      sb.append(" -Dversion=");
      sb.append(version);
      if (classifier != null && !classifier.equals("")) {
        sb.append(" -Dclassifier=");
        sb.append(classifier);
      } 
      sb.append(" -Dpackaging=");
      sb.append(type);
      sb.append(" -Dfile=/path/to/file");
      sb.append(" -Durl=[url] -DrepositoryId=[id]");
      sb.append(LS);
    } 
    sb.append(constructArtifactPath(path, indentation));
    sb.append(LS);
    return sb.toString();
  }
  
  public String getArtifactPath() {
    return this.path;
  }
}
