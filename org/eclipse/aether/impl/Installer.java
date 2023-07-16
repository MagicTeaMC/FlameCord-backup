package org.eclipse.aether.impl;

import org.eclipse.aether.RepositorySystemSession;
import org.eclipse.aether.installation.InstallRequest;
import org.eclipse.aether.installation.InstallResult;
import org.eclipse.aether.installation.InstallationException;

public interface Installer {
  InstallResult install(RepositorySystemSession paramRepositorySystemSession, InstallRequest paramInstallRequest) throws InstallationException;
}
