package org.eclipse.sisu.launch;

import org.eclipse.sisu.inject.BindingPublisher;
import org.osgi.framework.Bundle;

public interface BundlePlan {
  BindingPublisher prepare(Bundle paramBundle);
}
