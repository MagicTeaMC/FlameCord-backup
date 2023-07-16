package org.apache.logging.log4j.core.config.plugins.validation.validators;

import java.lang.annotation.Annotation;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.config.plugins.validation.ConstraintValidator;
import org.apache.logging.log4j.core.config.plugins.validation.constraints.NotBlank;
import org.apache.logging.log4j.status.StatusLogger;
import org.apache.logging.log4j.util.Strings;

public class NotBlankValidator implements ConstraintValidator<NotBlank> {
  private static final Logger LOGGER = (Logger)StatusLogger.getLogger();
  
  private NotBlank annotation;
  
  public void initialize(NotBlank anAnnotation) {
    this.annotation = anAnnotation;
  }
  
  public boolean isValid(String name, Object value) {
    return (Strings.isNotBlank(name) || err(name));
  }
  
  private boolean err(String name) {
    LOGGER.error(this.annotation.message(), name);
    return false;
  }
}
