package org.eclipse.aether.version;

public interface VersionScheme {
  Version parseVersion(String paramString) throws InvalidVersionSpecificationException;
  
  VersionRange parseVersionRange(String paramString) throws InvalidVersionSpecificationException;
  
  VersionConstraint parseVersionConstraint(String paramString) throws InvalidVersionSpecificationException;
}
