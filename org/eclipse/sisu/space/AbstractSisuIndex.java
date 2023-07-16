package org.eclipse.sisu.space;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

abstract class AbstractSisuIndex {
  static final String INDEX_FOLDER = "META-INF/sisu/";
  
  static final String QUALIFIER = "javax.inject.Qualifier";
  
  static final String NAMED = "javax.inject.Named";
  
  private final Map<Object, Set<String>> index = new LinkedHashMap<Object, Set<String>>();
  
  protected final synchronized void addClassToIndex(Object anno, Object clazz) {
    Set<String> table = this.index.get(anno);
    if (table == null) {
      table = readTable(anno);
      this.index.put(anno, table);
    } 
    table.add(String.valueOf(clazz));
  }
  
  protected final synchronized void flushIndex() {
    for (Map.Entry<Object, Set<String>> entry : this.index.entrySet())
      writeTable(entry.getKey(), entry.getValue()); 
  }
  
  protected abstract void info(String paramString);
  
  protected abstract void warn(String paramString);
  
  protected abstract Reader getReader(String paramString) throws IOException;
  
  protected abstract Writer getWriter(String paramString) throws IOException;
  
  private Set<String> readTable(Object name) {
    Set<String> table = new TreeSet<String>();
    try {
      BufferedReader reader = new BufferedReader(getReader("META-INF/sisu/" + name));
      try {
        for (String line = reader.readLine(); line != null; line = reader.readLine())
          table.add(line); 
      } finally {
        reader.close();
      } 
    } catch (IOException iOException) {}
    return table;
  }
  
  private void writeTable(Object name, Set<String> table) {
    try {
      BufferedWriter writer = new BufferedWriter(getWriter("META-INF/sisu/" + name));
      try {
        for (String line : table) {
          writer.write(line);
          writer.newLine();
        } 
      } finally {
        writer.close();
      } 
    } catch (IOException e) {
      warn(e.toString());
    } 
  }
}
