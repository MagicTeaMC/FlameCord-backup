package org.eclipse.aether.util.repository;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.eclipse.aether.repository.MirrorSelector;
import org.eclipse.aether.repository.RemoteRepository;

public final class DefaultMirrorSelector implements MirrorSelector {
  private static final String WILDCARD = "*";
  
  private static final String EXTERNAL_WILDCARD = "external:*";
  
  private static final String EXTERNAL_HTTP_WILDCARD = "external:http:*";
  
  private final List<MirrorDef> mirrors = new ArrayList<>();
  
  @Deprecated
  public DefaultMirrorSelector add(String id, String url, String type, boolean repositoryManager, String mirrorOfIds, String mirrorOfTypes) {
    return add(id, url, type, repositoryManager, false, mirrorOfIds, mirrorOfTypes);
  }
  
  public DefaultMirrorSelector add(String id, String url, String type, boolean repositoryManager, boolean blocked, String mirrorOfIds, String mirrorOfTypes) {
    this.mirrors.add(new MirrorDef(id, url, type, repositoryManager, blocked, mirrorOfIds, mirrorOfTypes));
    return this;
  }
  
  public RemoteRepository getMirror(RemoteRepository repository) {
    MirrorDef mirror = findMirror(repository);
    if (mirror == null)
      return null; 
    RemoteRepository.Builder builder = new RemoteRepository.Builder(mirror.id, repository.getContentType(), mirror.url);
    builder.setRepositoryManager(mirror.repositoryManager);
    builder.setBlocked(mirror.blocked);
    if (mirror.type != null && mirror.type.length() > 0)
      builder.setContentType(mirror.type); 
    builder.setSnapshotPolicy(repository.getPolicy(true));
    builder.setReleasePolicy(repository.getPolicy(false));
    builder.setMirroredRepositories(Collections.singletonList(repository));
    return builder.build();
  }
  
  private MirrorDef findMirror(RemoteRepository repository) {
    String repoId = repository.getId();
    if (repoId != null && !this.mirrors.isEmpty()) {
      for (MirrorDef mirror : this.mirrors) {
        if (repoId.equals(mirror.mirrorOfIds) && matchesType(repository.getContentType(), mirror.mirrorOfTypes))
          return mirror; 
      } 
      for (MirrorDef mirror : this.mirrors) {
        if (matchPattern(repository, mirror.mirrorOfIds) && matchesType(repository.getContentType(), mirror.mirrorOfTypes))
          return mirror; 
      } 
    } 
    return null;
  }
  
  static boolean matchPattern(RemoteRepository repository, String pattern) {
    boolean result = false;
    String originalId = repository.getId();
    if ("*".equals(pattern) || pattern.equals(originalId)) {
      result = true;
    } else {
      String[] repos = pattern.split(",");
      for (String repo : repos) {
        if (repo.length() > 1 && repo.startsWith("!")) {
          if (repo.substring(1).equals(originalId)) {
            result = false;
            break;
          } 
        } else {
          if (repo.equals(originalId)) {
            result = true;
            break;
          } 
          if ("external:*".equals(repo) && isExternalRepo(repository)) {
            result = true;
          } else if ("external:http:*".equals(repo) && isExternalHttpRepo(repository)) {
            result = true;
          } else if ("*".equals(repo)) {
            result = true;
          } 
        } 
      } 
    } 
    return result;
  }
  
  static boolean isExternalRepo(RemoteRepository repository) {
    boolean local = (isLocal(repository.getHost()) || "file".equalsIgnoreCase(repository.getProtocol()));
    return !local;
  }
  
  private static boolean isLocal(String host) {
    return ("localhost".equals(host) || "127.0.0.1".equals(host));
  }
  
  static boolean isExternalHttpRepo(RemoteRepository repository) {
    return (("http".equalsIgnoreCase(repository.getProtocol()) || "dav"
      .equalsIgnoreCase(repository.getProtocol()) || "dav:http"
      .equalsIgnoreCase(repository.getProtocol()) || "dav+http"
      .equalsIgnoreCase(repository.getProtocol())) && 
      !isLocal(repository.getHost()));
  }
  
  static boolean matchesType(String repoType, String mirrorType) {
    boolean result = false;
    if (mirrorType == null || mirrorType.length() <= 0 || "*".equals(mirrorType)) {
      result = true;
    } else if (mirrorType.equals(repoType)) {
      result = true;
    } else {
      String[] layouts = mirrorType.split(",");
      for (String layout : layouts) {
        if (layout.length() > 1 && layout.startsWith("!")) {
          if (layout.substring(1).equals(repoType)) {
            result = false;
            break;
          } 
        } else {
          if (layout.equals(repoType)) {
            result = true;
            break;
          } 
          if ("*".equals(layout))
            result = true; 
        } 
      } 
    } 
    return result;
  }
  
  static class MirrorDef {
    final String id;
    
    final String url;
    
    final String type;
    
    final boolean repositoryManager;
    
    final boolean blocked;
    
    final String mirrorOfIds;
    
    final String mirrorOfTypes;
    
    MirrorDef(String id, String url, String type, boolean repositoryManager, boolean blocked, String mirrorOfIds, String mirrorOfTypes) {
      this.id = id;
      this.url = url;
      this.type = type;
      this.repositoryManager = repositoryManager;
      this.blocked = blocked;
      this.mirrorOfIds = mirrorOfIds;
      this.mirrorOfTypes = mirrorOfTypes;
    }
  }
}
