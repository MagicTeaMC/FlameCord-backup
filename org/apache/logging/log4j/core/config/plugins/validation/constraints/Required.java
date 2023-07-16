package org.apache.logging.log4j.core.config.plugins.validation.constraints;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.apache.logging.log4j.core.config.plugins.validation.Constraint;
import org.apache.logging.log4j.core.config.plugins.validation.validators.RequiredValidator;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Constraint(RequiredValidator.class)
public @interface Required {
  String message() default "The parameter is null: {}";
}
