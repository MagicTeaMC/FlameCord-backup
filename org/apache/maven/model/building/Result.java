package org.apache.maven.model.building;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class Result<T> {
  private final boolean errors;
  
  private final T value;
  
  private final Iterable<? extends ModelProblem> problems;
  
  public static <T> Result<T> success(T model) {
    return success(model, Collections.emptyList());
  }
  
  public static <T> Result<T> success(T model, Iterable<? extends ModelProblem> problems) {
    assert !hasErrors(problems);
    return new Result<>(false, model, problems);
  }
  
  public static <T> Result<T> success(T model, Result<?>... results) {
    List<ModelProblem> problemsList = new ArrayList<>();
    for (Result<?> result1 : results) {
      for (ModelProblem modelProblem : result1.getProblems())
        problemsList.add(modelProblem); 
    } 
    return success(model, problemsList);
  }
  
  public static <T> Result<T> error(Iterable<? extends ModelProblem> problems) {
    return error(null, problems);
  }
  
  public static <T> Result<T> error(T model) {
    return error(model, Collections.emptyList());
  }
  
  public static <T> Result<T> error(Result<?> result) {
    return error(result.getProblems());
  }
  
  public static <T> Result<T> error(Result<?>... results) {
    List<ModelProblem> problemsList = new ArrayList<>();
    for (Result<?> result1 : results) {
      for (ModelProblem modelProblem : result1.getProblems())
        problemsList.add(modelProblem); 
    } 
    return error(problemsList);
  }
  
  public static <T> Result<T> error(T model, Iterable<? extends ModelProblem> problems) {
    return new Result<>(true, model, problems);
  }
  
  public static <T> Result<T> newResult(T model, Iterable<? extends ModelProblem> problems) {
    return new Result<>(hasErrors(problems), model, problems);
  }
  
  public static <T> Result<T> addProblem(Result<T> result, ModelProblem problem) {
    return addProblems(result, Collections.singleton(problem));
  }
  
  public static <T> Result<T> addProblems(Result<T> result, Iterable<? extends ModelProblem> problems) {
    Collection<ModelProblem> list = new ArrayList<>();
    for (ModelProblem item : problems)
      list.add(item); 
    for (ModelProblem item : result.getProblems())
      list.add(item); 
    return new Result<>((result.hasErrors() || hasErrors(problems)), result.get(), list);
  }
  
  public static <T> Result<T> addProblems(Result<T> result, Result<?>... results) {
    List<ModelProblem> problemsList = new ArrayList<>();
    for (Result<?> result1 : results) {
      for (ModelProblem modelProblem : result1.getProblems())
        problemsList.add(modelProblem); 
    } 
    return addProblems(result, problemsList);
  }
  
  public static <T> Result<Iterable<T>> newResultSet(Iterable<? extends Result<? extends T>> results) {
    boolean hasErrors = false;
    List<T> modelsList = new ArrayList<>();
    List<ModelProblem> problemsList = new ArrayList<>();
    for (Result<? extends T> result : results) {
      modelsList.add(result.get());
      for (ModelProblem modelProblem : result.getProblems())
        problemsList.add(modelProblem); 
      if (result.hasErrors())
        hasErrors = true; 
    } 
    return new Result<>(hasErrors, modelsList, problemsList);
  }
  
  private static boolean hasErrors(Iterable<? extends ModelProblem> problems) {
    for (ModelProblem input : problems) {
      if (input.getSeverity().equals(ModelProblem.Severity.ERROR) || input.getSeverity().equals(ModelProblem.Severity.FATAL))
        return true; 
    } 
    return false;
  }
  
  private Result(boolean errors, T model, Iterable<? extends ModelProblem> problems) {
    this.errors = errors;
    this.value = model;
    this.problems = problems;
  }
  
  public Iterable<? extends ModelProblem> getProblems() {
    return this.problems;
  }
  
  public T get() {
    return this.value;
  }
  
  public boolean hasErrors() {
    return this.errors;
  }
}
