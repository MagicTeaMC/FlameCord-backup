package org.codehaus.plexus.util;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Stack;

public class DirectoryWalker {
  private File baseDir;
  
  private int baseDirOffset;
  
  private Stack<DirStackEntry> dirStack;
  
  private List<String> excludes;
  
  private List<String> includes;
  
  class DirStackEntry {
    public int count;
    
    public File dir;
    
    public int index;
    
    public double percentageOffset;
    
    public double percentageSize;
    
    public DirStackEntry(File d, int length) {
      this.dir = d;
      this.count = length;
    }
    
    public double getNextPercentageOffset() {
      return this.percentageOffset + this.index * this.percentageSize / this.count;
    }
    
    public double getNextPercentageSize() {
      return this.percentageSize / this.count;
    }
    
    public int getPercentage() {
      double percentageWithinDir = this.index / this.count;
      return (int)Math.floor(this.percentageOffset + percentageWithinDir * this.percentageSize);
    }
    
    public String toString() {
      return "DirStackEntry[dir=" + this.dir.getAbsolutePath() + ",count=" + this.count + ",index=" + this.index + ",percentageOffset=" + this.percentageOffset + ",percentageSize=" + this.percentageSize + ",percentage()=" + getPercentage() + ",getNextPercentageOffset()=" + getNextPercentageOffset() + ",getNextPercentageSize()=" + getNextPercentageSize() + "]";
    }
  }
  
  private boolean isCaseSensitive = true;
  
  private List<DirectoryWalkListener> listeners;
  
  private boolean debugEnabled = false;
  
  public DirectoryWalker() {
    this.includes = new ArrayList<String>();
    this.excludes = new ArrayList<String>();
    this.listeners = new ArrayList<DirectoryWalkListener>();
  }
  
  public void addDirectoryWalkListener(DirectoryWalkListener listener) {
    this.listeners.add(listener);
  }
  
  public void addExclude(String exclude) {
    this.excludes.add(fixPattern(exclude));
  }
  
  public void addInclude(String include) {
    this.includes.add(fixPattern(include));
  }
  
  public void addSCMExcludes() {
    String[] scmexcludes = DirectoryScanner.DEFAULTEXCLUDES;
    for (String scmexclude : scmexcludes)
      addExclude(scmexclude); 
  }
  
  private void fireStep(File file) {
    DirStackEntry dsEntry = this.dirStack.peek();
    int percentage = dsEntry.getPercentage();
    for (DirectoryWalkListener listener1 : this.listeners) {
      DirectoryWalkListener listener = listener1;
      listener.directoryWalkStep(percentage, file);
    } 
  }
  
  private void fireWalkFinished() {
    for (DirectoryWalkListener listener1 : this.listeners)
      listener1.directoryWalkFinished(); 
  }
  
  private void fireWalkStarting() {
    for (DirectoryWalkListener listener1 : this.listeners)
      listener1.directoryWalkStarting(this.baseDir); 
  }
  
  private void fireDebugMessage(String message) {
    for (DirectoryWalkListener listener1 : this.listeners)
      listener1.debug(message); 
  }
  
  private String fixPattern(String pattern) {
    String cleanPattern = pattern;
    if (File.separatorChar != '/')
      cleanPattern = cleanPattern.replace('/', File.separatorChar); 
    if (File.separatorChar != '\\')
      cleanPattern = cleanPattern.replace('\\', File.separatorChar); 
    return cleanPattern;
  }
  
  public void setDebugMode(boolean debugEnabled) {
    this.debugEnabled = debugEnabled;
  }
  
  public File getBaseDir() {
    return this.baseDir;
  }
  
  public List<String> getExcludes() {
    return this.excludes;
  }
  
  public List<String> getIncludes() {
    return this.includes;
  }
  
  private boolean isExcluded(String name) {
    return isMatch(this.excludes, name);
  }
  
  private boolean isIncluded(String name) {
    return isMatch(this.includes, name);
  }
  
  private boolean isMatch(List<String> patterns, String name) {
    for (String pattern1 : patterns) {
      if (SelectorUtils.matchPath(pattern1, name, this.isCaseSensitive))
        return true; 
    } 
    return false;
  }
  
  private String relativeToBaseDir(File file) {
    return file.getAbsolutePath().substring(this.baseDirOffset + 1);
  }
  
  public void removeDirectoryWalkListener(DirectoryWalkListener listener) {
    this.listeners.remove(listener);
  }
  
  public void scan() {
    if (this.baseDir == null)
      throw new IllegalStateException("Scan Failure.  BaseDir not specified."); 
    if (!this.baseDir.exists())
      throw new IllegalStateException("Scan Failure.  BaseDir does not exist."); 
    if (!this.baseDir.isDirectory())
      throw new IllegalStateException("Scan Failure.  BaseDir is not a directory."); 
    if (this.includes.isEmpty())
      addInclude("**"); 
    if (this.debugEnabled) {
      StringBuilder dbg = new StringBuilder();
      dbg.append("DirectoryWalker Scan");
      dbg.append("\n  Base Dir: ").append(this.baseDir.getAbsolutePath());
      dbg.append("\n  Includes: ");
      Iterator<String> it = this.includes.iterator();
      while (it.hasNext()) {
        String include = it.next();
        dbg.append("\n    - \"").append(include).append("\"");
      } 
      dbg.append("\n  Excludes: ");
      it = this.excludes.iterator();
      while (it.hasNext()) {
        String exclude = it.next();
        dbg.append("\n    - \"").append(exclude).append("\"");
      } 
      fireDebugMessage(dbg.toString());
    } 
    fireWalkStarting();
    this.dirStack = new Stack<DirStackEntry>();
    scanDir(this.baseDir);
    fireWalkFinished();
  }
  
  private void scanDir(File dir) {
    File[] files = dir.listFiles();
    if (files == null)
      return; 
    DirStackEntry curStackEntry = new DirStackEntry(dir, files.length);
    if (this.dirStack.isEmpty()) {
      curStackEntry.percentageOffset = 0.0D;
      curStackEntry.percentageSize = 100.0D;
    } else {
      DirStackEntry previousStackEntry = this.dirStack.peek();
      curStackEntry.percentageOffset = previousStackEntry.getNextPercentageOffset();
      curStackEntry.percentageSize = previousStackEntry.getNextPercentageSize();
    } 
    this.dirStack.push(curStackEntry);
    for (int idx = 0; idx < files.length; idx++) {
      curStackEntry.index = idx;
      String name = relativeToBaseDir(files[idx]);
      if (isExcluded(name)) {
        fireDebugMessage(name + " is excluded.");
      } else if (files[idx].isDirectory()) {
        scanDir(files[idx]);
      } else if (isIncluded(name)) {
        fireStep(files[idx]);
      } 
    } 
    this.dirStack.pop();
  }
  
  public void setBaseDir(File baseDir) {
    this.baseDir = baseDir;
    this.baseDirOffset = baseDir.getAbsolutePath().length();
  }
  
  public void setExcludes(List<String> entries) {
    this.excludes.clear();
    if (entries != null)
      for (String entry : entries)
        this.excludes.add(fixPattern(entry));  
  }
  
  public void setIncludes(List<String> entries) {
    this.includes.clear();
    if (entries != null)
      for (String entry : entries)
        this.includes.add(fixPattern(entry));  
  }
}
