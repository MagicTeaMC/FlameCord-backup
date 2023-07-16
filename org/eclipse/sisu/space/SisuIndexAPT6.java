package org.eclipse.sisu.space;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.Writer;
import java.util.Collections;
import java.util.Set;
import javax.annotation.processing.Completion;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.inject.Qualifier;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;
import javax.tools.Diagnostic;
import javax.tools.FileObject;
import javax.tools.StandardLocation;

public final class SisuIndexAPT6 extends AbstractSisuIndex implements Processor {
  private static final String QUALIFIERS = "qualifiers";
  
  private static final String ALL = "all";
  
  private static final String NONE = "none";
  
  private static final boolean HAS_QUALIFIER;
  
  private ProcessingEnvironment environment;
  
  private String qualifiers;
  
  static {
    boolean hasQualifier;
    try {
      hasQualifier = Qualifier.class.isAnnotation();
    } catch (LinkageError linkageError) {
      hasQualifier = false;
    } 
    HAS_QUALIFIER = hasQualifier;
  }
  
  public void init(ProcessingEnvironment _environment) {
    this.environment = _environment;
    this.qualifiers = _environment.getOptions().get("qualifiers");
  }
  
  public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment round) {
    Elements elementUtils = this.environment.getElementUtils();
    for (TypeElement anno : annotations) {
      if (!"all".equals(this.qualifiers) || hasQualifier(anno))
        for (Element elem : round.getElementsAnnotatedWith(anno)) {
          if (elem.getKind().isClass())
            addClassToIndex("javax.inject.Named", elementUtils.getBinaryName((TypeElement)elem)); 
        }  
    } 
    if (round.processingOver())
      flushIndex(); 
    return false;
  }
  
  public Iterable<? extends Completion> getCompletions(Element element, AnnotationMirror annotation, ExecutableElement member, String userText) {
    return Collections.emptySet();
  }
  
  public Set<String> getSupportedAnnotationTypes() {
    if ("all".equalsIgnoreCase(this.qualifiers))
      return Collections.singleton("*"); 
    if ("none".equalsIgnoreCase(this.qualifiers))
      return Collections.emptySet(); 
    return Collections.singleton("javax.inject.Named");
  }
  
  public Set<String> getSupportedOptions() {
    return Collections.singleton("qualifiers");
  }
  
  public SourceVersion getSupportedSourceVersion() {
    return SourceVersion.latestSupported();
  }
  
  protected void info(String msg) {
    this.environment.getMessager().printMessage(Diagnostic.Kind.NOTE, msg);
  }
  
  protected void warn(String msg) {
    this.environment.getMessager().printMessage(Diagnostic.Kind.WARNING, msg);
  }
  
  protected Reader getReader(String path) throws IOException {
    FileObject file = this.environment.getFiler().getResource(StandardLocation.CLASS_OUTPUT, "", path);
    return new InputStreamReader(file.openInputStream(), "UTF-8");
  }
  
  protected Writer getWriter(String path) throws IOException {
    return this.environment.getFiler().createResource(StandardLocation.CLASS_OUTPUT, "", path, new Element[0]).openWriter();
  }
  
  private static boolean hasQualifier(TypeElement anno) {
    if (HAS_QUALIFIER)
      return (anno.getAnnotation(Qualifier.class) != null); 
    for (AnnotationMirror mirror : anno.getAnnotationMirrors()) {
      if ("javax.inject.Qualifier".equals(mirror.getAnnotationType().toString()))
        return true; 
    } 
    return false;
  }
}
