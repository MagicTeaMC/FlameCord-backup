package org.apache.logging.log4j.core.config.plugins;

import java.lang.annotation.Annotation;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.apache.logging.log4j.core.config.plugins.visitors.PluginVisitor;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.ANNOTATION_TYPE})
public @interface PluginVisitorStrategy {
  Class<? extends PluginVisitor<? extends Annotation>> value();
}
