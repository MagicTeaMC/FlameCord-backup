package META-INF.versions.9.org.apache.logging.log4j.util.internal;

import java.io.ObjectInputFilter;
import java.util.Arrays;
import java.util.List;

public class DefaultObjectInputFilter implements ObjectInputFilter {
  private static final List<String> REQUIRED_JAVA_CLASSES = Arrays.asList(new String[] { "java.math.BigDecimal", "java.math.BigInteger", "java.rmi.MarshalledObject", "[B" });
  
  private static final List<String> REQUIRED_JAVA_PACKAGES = Arrays.asList(new String[] { "java.lang.", "java.time", "java.util.", "org.apache.logging.log4j.", "[Lorg.apache.logging.log4j." });
  
  private final ObjectInputFilter delegate;
  
  public DefaultObjectInputFilter() {
    this.delegate = null;
  }
  
  public DefaultObjectInputFilter(ObjectInputFilter filter) {
    this.delegate = filter;
  }
  
  public static org.apache.logging.log4j.util.internal.DefaultObjectInputFilter newInstance(ObjectInputFilter filter) {
    return new org.apache.logging.log4j.util.internal.DefaultObjectInputFilter(filter);
  }
  
  public ObjectInputFilter.Status checkInput(ObjectInputFilter.FilterInfo filterInfo) {
    ObjectInputFilter.Status status = null;
    if (this.delegate != null) {
      status = this.delegate.checkInput(filterInfo);
      if (status != ObjectInputFilter.Status.UNDECIDED)
        return status; 
    } 
    ObjectInputFilter serialFilter = ObjectInputFilter.Config.getSerialFilter();
    if (serialFilter != null) {
      status = serialFilter.checkInput(filterInfo);
      if (status != ObjectInputFilter.Status.UNDECIDED)
        return status; 
    } 
    if (filterInfo.serialClass() != null) {
      String name = filterInfo.serialClass().getName();
      if (isAllowedByDefault(name) || isRequiredPackage(name))
        return ObjectInputFilter.Status.ALLOWED; 
    } 
    return ObjectInputFilter.Status.REJECTED;
  }
  
  private static boolean isAllowedByDefault(String name) {
    return (isRequiredPackage(name) || REQUIRED_JAVA_CLASSES.contains(name));
  }
  
  private static boolean isRequiredPackage(String name) {
    for (String packageName : REQUIRED_JAVA_PACKAGES) {
      if (name.startsWith(packageName))
        return true; 
    } 
    return false;
  }
}
