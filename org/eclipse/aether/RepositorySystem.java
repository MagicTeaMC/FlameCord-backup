package org.eclipse.aether;

import java.util.Collection;
import java.util.List;
import org.eclipse.aether.collection.CollectRequest;
import org.eclipse.aether.collection.CollectResult;
import org.eclipse.aether.collection.DependencyCollectionException;
import org.eclipse.aether.deployment.DeployRequest;
import org.eclipse.aether.deployment.DeployResult;
import org.eclipse.aether.deployment.DeploymentException;
import org.eclipse.aether.installation.InstallRequest;
import org.eclipse.aether.installation.InstallResult;
import org.eclipse.aether.installation.InstallationException;
import org.eclipse.aether.repository.LocalRepository;
import org.eclipse.aether.repository.LocalRepositoryManager;
import org.eclipse.aether.repository.RemoteRepository;
import org.eclipse.aether.resolution.ArtifactDescriptorException;
import org.eclipse.aether.resolution.ArtifactDescriptorRequest;
import org.eclipse.aether.resolution.ArtifactDescriptorResult;
import org.eclipse.aether.resolution.ArtifactRequest;
import org.eclipse.aether.resolution.ArtifactResolutionException;
import org.eclipse.aether.resolution.ArtifactResult;
import org.eclipse.aether.resolution.DependencyRequest;
import org.eclipse.aether.resolution.DependencyResolutionException;
import org.eclipse.aether.resolution.DependencyResult;
import org.eclipse.aether.resolution.MetadataRequest;
import org.eclipse.aether.resolution.MetadataResult;
import org.eclipse.aether.resolution.VersionRangeRequest;
import org.eclipse.aether.resolution.VersionRangeResolutionException;
import org.eclipse.aether.resolution.VersionRangeResult;
import org.eclipse.aether.resolution.VersionRequest;
import org.eclipse.aether.resolution.VersionResolutionException;
import org.eclipse.aether.resolution.VersionResult;

public interface RepositorySystem {
  VersionRangeResult resolveVersionRange(RepositorySystemSession paramRepositorySystemSession, VersionRangeRequest paramVersionRangeRequest) throws VersionRangeResolutionException;
  
  VersionResult resolveVersion(RepositorySystemSession paramRepositorySystemSession, VersionRequest paramVersionRequest) throws VersionResolutionException;
  
  ArtifactDescriptorResult readArtifactDescriptor(RepositorySystemSession paramRepositorySystemSession, ArtifactDescriptorRequest paramArtifactDescriptorRequest) throws ArtifactDescriptorException;
  
  CollectResult collectDependencies(RepositorySystemSession paramRepositorySystemSession, CollectRequest paramCollectRequest) throws DependencyCollectionException;
  
  DependencyResult resolveDependencies(RepositorySystemSession paramRepositorySystemSession, DependencyRequest paramDependencyRequest) throws DependencyResolutionException;
  
  ArtifactResult resolveArtifact(RepositorySystemSession paramRepositorySystemSession, ArtifactRequest paramArtifactRequest) throws ArtifactResolutionException;
  
  List<ArtifactResult> resolveArtifacts(RepositorySystemSession paramRepositorySystemSession, Collection<? extends ArtifactRequest> paramCollection) throws ArtifactResolutionException;
  
  List<MetadataResult> resolveMetadata(RepositorySystemSession paramRepositorySystemSession, Collection<? extends MetadataRequest> paramCollection);
  
  InstallResult install(RepositorySystemSession paramRepositorySystemSession, InstallRequest paramInstallRequest) throws InstallationException;
  
  DeployResult deploy(RepositorySystemSession paramRepositorySystemSession, DeployRequest paramDeployRequest) throws DeploymentException;
  
  LocalRepositoryManager newLocalRepositoryManager(RepositorySystemSession paramRepositorySystemSession, LocalRepository paramLocalRepository);
  
  SyncContext newSyncContext(RepositorySystemSession paramRepositorySystemSession, boolean paramBoolean);
  
  List<RemoteRepository> newResolutionRepositories(RepositorySystemSession paramRepositorySystemSession, List<RemoteRepository> paramList);
  
  RemoteRepository newDeploymentRepository(RepositorySystemSession paramRepositorySystemSession, RemoteRepository paramRemoteRepository);
}
