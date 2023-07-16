package org.jline.reader;

import java.util.Objects;

public class Candidate implements Comparable<Candidate> {
  private final String value;
  
  private final String displ;
  
  private final String group;
  
  private final String descr;
  
  private final String suffix;
  
  private final String key;
  
  private final boolean complete;
  
  public Candidate(String value) {
    this(value, value, null, null, null, null, true);
  }
  
  public Candidate(String value, String displ, String group, String descr, String suffix, String key, boolean complete) {
    this.value = Objects.<String>requireNonNull(value);
    this.displ = Objects.<String>requireNonNull(displ);
    this.group = group;
    this.descr = descr;
    this.suffix = suffix;
    this.key = key;
    this.complete = complete;
  }
  
  public String value() {
    return this.value;
  }
  
  public String displ() {
    return this.displ;
  }
  
  public String group() {
    return this.group;
  }
  
  public String descr() {
    return this.descr;
  }
  
  public String suffix() {
    return this.suffix;
  }
  
  public String key() {
    return this.key;
  }
  
  public boolean complete() {
    return this.complete;
  }
  
  public int compareTo(Candidate o) {
    return this.value.compareTo(o.value);
  }
  
  public String toString() {
    return "Candidate{" + this.value + "}";
  }
}
