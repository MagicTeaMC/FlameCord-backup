package org.codehaus.plexus.util;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Vector;

public class DirectoryScanner extends AbstractScanner {
  protected File basedir;
  
  protected Vector<String> filesIncluded;
  
  protected Vector<String> filesNotIncluded;
  
  protected Vector<String> filesExcluded;
  
  protected Vector<String> dirsIncluded;
  
  protected Vector<String> dirsNotIncluded;
  
  protected Vector<String> dirsExcluded;
  
  protected Vector<String> filesDeselected;
  
  protected Vector<String> dirsDeselected;
  
  protected boolean haveSlowResults = false;
  
  private boolean followSymlinks = true;
  
  protected boolean everythingIncluded = true;
  
  private final String[] tokenizedEmpty = MatchPattern.tokenizePathToString("", File.separator);
  
  public void setBasedir(String basedir) {
    setBasedir(new File(basedir.replace('/', File.separatorChar).replace('\\', File.separatorChar)));
  }
  
  public void setBasedir(File basedir) {
    this.basedir = basedir;
  }
  
  public File getBasedir() {
    return this.basedir;
  }
  
  public void setFollowSymlinks(boolean followSymlinks) {
    this.followSymlinks = followSymlinks;
  }
  
  public boolean isEverythingIncluded() {
    return this.everythingIncluded;
  }
  
  public void scan() throws IllegalStateException {
    if (this.basedir == null)
      throw new IllegalStateException("No basedir set"); 
    if (!this.basedir.exists())
      throw new IllegalStateException("basedir " + this.basedir + " does not exist"); 
    if (!this.basedir.isDirectory())
      throw new IllegalStateException("basedir " + this.basedir + " is not a directory"); 
    setupDefaultFilters();
    setupMatchPatterns();
    this.filesIncluded = new Vector<String>();
    this.filesNotIncluded = new Vector<String>();
    this.filesExcluded = new Vector<String>();
    this.filesDeselected = new Vector<String>();
    this.dirsIncluded = new Vector<String>();
    this.dirsNotIncluded = new Vector<String>();
    this.dirsExcluded = new Vector<String>();
    this.dirsDeselected = new Vector<String>();
    if (isIncluded("", this.tokenizedEmpty)) {
      if (!isExcluded("", this.tokenizedEmpty)) {
        if (isSelected("", this.basedir)) {
          this.dirsIncluded.addElement("");
        } else {
          this.dirsDeselected.addElement("");
        } 
      } else {
        this.dirsExcluded.addElement("");
      } 
    } else {
      this.dirsNotIncluded.addElement("");
    } 
    scandir(this.basedir, "", true);
  }
  
  protected void slowScan() {
    if (this.haveSlowResults)
      return; 
    String[] excl = new String[this.dirsExcluded.size()];
    this.dirsExcluded.copyInto((Object[])excl);
    String[] notIncl = new String[this.dirsNotIncluded.size()];
    this.dirsNotIncluded.copyInto((Object[])notIncl);
    for (String anExcl : excl) {
      if (!couldHoldIncluded(anExcl))
        scandir(new File(this.basedir, anExcl), anExcl + File.separator, false); 
    } 
    for (String aNotIncl : notIncl) {
      if (!couldHoldIncluded(aNotIncl))
        scandir(new File(this.basedir, aNotIncl), aNotIncl + File.separator, false); 
    } 
    this.haveSlowResults = true;
  }
  
  protected void scandir(File dir, String vpath, boolean fast) {
    String[] newfiles = dir.list();
    if (newfiles == null)
      newfiles = new String[0]; 
    if (!this.followSymlinks) {
      ArrayList<String> noLinks = new ArrayList<String>();
      for (String newfile : newfiles) {
        try {
          if (isParentSymbolicLink(dir, newfile)) {
            String name = vpath + newfile;
            File file = new File(dir, newfile);
            if (file.isDirectory()) {
              this.dirsExcluded.addElement(name);
            } else {
              this.filesExcluded.addElement(name);
            } 
          } else {
            noLinks.add(newfile);
          } 
        } catch (IOException ioe) {
          String msg = "IOException caught while checking for links, couldn't get canonical path!";
          System.err.println(msg);
          noLinks.add(newfile);
        } 
      } 
      newfiles = noLinks.<String>toArray(new String[noLinks.size()]);
    } 
    if (this.filenameComparator != null)
      Arrays.sort(newfiles, this.filenameComparator); 
    for (String newfile : newfiles) {
      String name = vpath + newfile;
      String[] tokenizedName = MatchPattern.tokenizePathToString(name, File.separator);
      File file = new File(dir, newfile);
      if (file.isDirectory()) {
        if (isIncluded(name, tokenizedName)) {
          if (!isExcluded(name, tokenizedName)) {
            if (isSelected(name, file)) {
              this.dirsIncluded.addElement(name);
              if (fast)
                scandir(file, name + File.separator, fast); 
            } else {
              this.everythingIncluded = false;
              this.dirsDeselected.addElement(name);
              if (fast && couldHoldIncluded(name))
                scandir(file, name + File.separator, fast); 
            } 
          } else {
            this.everythingIncluded = false;
            this.dirsExcluded.addElement(name);
            if (fast && couldHoldIncluded(name))
              scandir(file, name + File.separator, fast); 
          } 
        } else {
          this.everythingIncluded = false;
          this.dirsNotIncluded.addElement(name);
          if (fast && couldHoldIncluded(name))
            scandir(file, name + File.separator, fast); 
        } 
        if (!fast)
          scandir(file, name + File.separator, fast); 
      } else if (file.isFile()) {
        if (isIncluded(name, tokenizedName)) {
          if (!isExcluded(name, tokenizedName)) {
            if (isSelected(name, file)) {
              this.filesIncluded.addElement(name);
            } else {
              this.everythingIncluded = false;
              this.filesDeselected.addElement(name);
            } 
          } else {
            this.everythingIncluded = false;
            this.filesExcluded.addElement(name);
          } 
        } else {
          this.everythingIncluded = false;
          this.filesNotIncluded.addElement(name);
        } 
      } 
    } 
  }
  
  protected boolean isSelected(String name, File file) {
    return true;
  }
  
  public String[] getIncludedFiles() {
    String[] files = new String[this.filesIncluded.size()];
    this.filesIncluded.copyInto((Object[])files);
    return files;
  }
  
  public String[] getNotIncludedFiles() {
    slowScan();
    String[] files = new String[this.filesNotIncluded.size()];
    this.filesNotIncluded.copyInto((Object[])files);
    return files;
  }
  
  public String[] getExcludedFiles() {
    slowScan();
    String[] files = new String[this.filesExcluded.size()];
    this.filesExcluded.copyInto((Object[])files);
    return files;
  }
  
  public String[] getDeselectedFiles() {
    slowScan();
    String[] files = new String[this.filesDeselected.size()];
    this.filesDeselected.copyInto((Object[])files);
    return files;
  }
  
  public String[] getIncludedDirectories() {
    String[] directories = new String[this.dirsIncluded.size()];
    this.dirsIncluded.copyInto((Object[])directories);
    return directories;
  }
  
  public String[] getNotIncludedDirectories() {
    slowScan();
    String[] directories = new String[this.dirsNotIncluded.size()];
    this.dirsNotIncluded.copyInto((Object[])directories);
    return directories;
  }
  
  public String[] getExcludedDirectories() {
    slowScan();
    String[] directories = new String[this.dirsExcluded.size()];
    this.dirsExcluded.copyInto((Object[])directories);
    return directories;
  }
  
  public String[] getDeselectedDirectories() {
    slowScan();
    String[] directories = new String[this.dirsDeselected.size()];
    this.dirsDeselected.copyInto((Object[])directories);
    return directories;
  }
  
  public boolean isSymbolicLink(File parent, String name) throws IOException {
    if (Java7Detector.isJava7())
      return NioFiles.isSymbolicLink(new File(parent, name)); 
    File resolvedParent = new File(parent.getCanonicalPath());
    File toTest = new File(resolvedParent, name);
    return !toTest.getAbsolutePath().equals(toTest.getCanonicalPath());
  }
  
  public boolean isParentSymbolicLink(File parent, String name) throws IOException {
    if (Java7Detector.isJava7())
      return NioFiles.isSymbolicLink(parent); 
    File resolvedParent = new File(parent.getCanonicalPath());
    File toTest = new File(resolvedParent, name);
    return !toTest.getAbsolutePath().equals(toTest.getCanonicalPath());
  }
}
