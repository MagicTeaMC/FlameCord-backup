package org.jline.reader.impl.history;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.file.CopyOption;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import java.nio.file.attribute.FileAttribute;
import java.time.DateTimeException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Objects;
import java.util.Spliterator;
import org.jline.reader.History;
import org.jline.reader.LineReader;
import org.jline.reader.impl.ReaderUtils;
import org.jline.utils.Log;

public class DefaultHistory implements History {
  public static final int DEFAULT_HISTORY_SIZE = 500;
  
  public static final int DEFAULT_HISTORY_FILE_SIZE = 10000;
  
  private final LinkedList<History.Entry> items = new LinkedList<>();
  
  private LineReader reader;
  
  private Map<String, HistoryFileData> historyFiles = new HashMap<>();
  
  private int offset = 0;
  
  private int index = 0;
  
  public DefaultHistory(LineReader reader) {
    attach(reader);
  }
  
  private Path getPath() {
    Object obj = (this.reader != null) ? this.reader.getVariables().get("history-file") : null;
    if (obj instanceof Path)
      return (Path)obj; 
    if (obj instanceof File)
      return ((File)obj).toPath(); 
    if (obj != null)
      return Paths.get(obj.toString(), new String[0]); 
    return null;
  }
  
  public void attach(LineReader reader) {
    if (this.reader != reader) {
      this.reader = reader;
      try {
        load();
      } catch (IllegalArgumentException|IOException e) {
        Log.warn(new Object[] { "Failed to load history", e });
      } 
    } 
  }
  
  public void load() throws IOException {
    Path path = getPath();
    if (path != null)
      try {
        if (Files.exists(path, new java.nio.file.LinkOption[0])) {
          Log.trace(new Object[] { "Loading history from: ", path });
          BufferedReader reader = Files.newBufferedReader(path);
          try {
            internalClear();
            reader.lines().forEach(line -> addHistoryLine(path, line));
            setHistoryFileData(path, new HistoryFileData(this.items.size(), this.offset + this.items.size()));
            maybeResize();
            if (reader != null)
              reader.close(); 
          } catch (Throwable throwable) {
            if (reader != null)
              try {
                reader.close();
              } catch (Throwable throwable1) {
                throwable.addSuppressed(throwable1);
              }  
            throw throwable;
          } 
        } 
      } catch (IllegalArgumentException|IOException e) {
        Log.debug(new Object[] { "Failed to load history; clearing", e });
        internalClear();
        throw e;
      }  
  }
  
  public void read(Path file, boolean incremental) throws IOException {
    Path path = (file != null) ? file : getPath();
    if (path != null)
      try {
        if (Files.exists(path, new java.nio.file.LinkOption[0])) {
          Log.trace(new Object[] { "Reading history from: ", path });
          BufferedReader reader = Files.newBufferedReader(path);
          try {
            reader.lines().forEach(line -> addHistoryLine(path, line, incremental));
            setHistoryFileData(path, new HistoryFileData(this.items.size(), this.offset + this.items.size()));
            maybeResize();
            if (reader != null)
              reader.close(); 
          } catch (Throwable throwable) {
            if (reader != null)
              try {
                reader.close();
              } catch (Throwable throwable1) {
                throwable.addSuppressed(throwable1);
              }  
            throw throwable;
          } 
        } 
      } catch (IllegalArgumentException|IOException e) {
        Log.debug(new Object[] { "Failed to read history; clearing", e });
        internalClear();
        throw e;
      }  
  }
  
  private String doHistoryFileDataKey(Path path) {
    return (path != null) ? path.toAbsolutePath().toString() : null;
  }
  
  private HistoryFileData getHistoryFileData(Path path) {
    String key = doHistoryFileDataKey(path);
    if (!this.historyFiles.containsKey(key))
      this.historyFiles.put(key, new HistoryFileData()); 
    return this.historyFiles.get(key);
  }
  
  private void setHistoryFileData(Path path, HistoryFileData historyFileData) {
    this.historyFiles.put(doHistoryFileDataKey(path), historyFileData);
  }
  
  private boolean isLineReaderHistory(Path path) throws IOException {
    Path lrp = getPath();
    if (lrp == null)
      return (path == null); 
    return Files.isSameFile(lrp, path);
  }
  
  private void setLastLoaded(Path path, int lastloaded) {
    getHistoryFileData(path).setLastLoaded(lastloaded);
  }
  
  private void setEntriesInFile(Path path, int entriesInFile) {
    getHistoryFileData(path).setEntriesInFile(entriesInFile);
  }
  
  private void incEntriesInFile(Path path, int amount) {
    getHistoryFileData(path).incEntriesInFile(amount);
  }
  
  private int getLastLoaded(Path path) {
    return getHistoryFileData(path).getLastLoaded();
  }
  
  private int getEntriesInFile(Path path) {
    return getHistoryFileData(path).getEntriesInFile();
  }
  
  protected void addHistoryLine(Path path, String line) {
    addHistoryLine(path, line, false);
  }
  
  protected void addHistoryLine(Path path, String line, boolean checkDuplicates) {
    if (this.reader.isSet(LineReader.Option.HISTORY_TIMESTAMPED)) {
      Instant time;
      int idx = line.indexOf(':');
      String badHistoryFileSyntax = "Bad history file syntax! The history file `" + path + "` may be an older history: please remove it or use a different history file.";
      if (idx < 0)
        throw new IllegalArgumentException(badHistoryFileSyntax); 
      try {
        time = Instant.ofEpochMilli(Long.parseLong(line.substring(0, idx)));
      } catch (DateTimeException|NumberFormatException e) {
        throw new IllegalArgumentException(badHistoryFileSyntax);
      } 
      String unescaped = unescape(line.substring(idx + 1));
      internalAdd(time, unescaped, checkDuplicates);
    } else {
      internalAdd(Instant.now(), unescape(line), checkDuplicates);
    } 
  }
  
  public void purge() throws IOException {
    internalClear();
    Path path = getPath();
    if (path != null) {
      Log.trace(new Object[] { "Purging history from: ", path });
      Files.deleteIfExists(path);
    } 
  }
  
  public void write(Path file, boolean incremental) throws IOException {
    Path path = (file != null) ? file : getPath();
    if (path != null && Files.exists(path, new java.nio.file.LinkOption[0]))
      path.toFile().delete(); 
    internalWrite(path, incremental ? getLastLoaded(path) : 0);
  }
  
  public void append(Path file, boolean incremental) throws IOException {
    internalWrite((file != null) ? file : getPath(), 
        incremental ? getLastLoaded(file) : 0);
  }
  
  public void save() throws IOException {
    internalWrite(getPath(), getLastLoaded(getPath()));
  }
  
  private void internalWrite(Path path, int from) throws IOException {
    if (path != null) {
      Log.trace(new Object[] { "Saving history to: ", path });
      Path parent = path.toAbsolutePath().getParent();
      if (!Files.exists(parent, new java.nio.file.LinkOption[0]))
        Files.createDirectories(parent, (FileAttribute<?>[])new FileAttribute[0]); 
      BufferedWriter writer = Files.newBufferedWriter(path.toAbsolutePath(), new OpenOption[] { StandardOpenOption.WRITE, StandardOpenOption.APPEND, StandardOpenOption.CREATE });
      try {
        for (History.Entry entry : this.items.subList(from, this.items.size())) {
          if (isPersistable(entry))
            writer.append(format(entry)); 
        } 
        if (writer != null)
          writer.close(); 
      } catch (Throwable throwable) {
        if (writer != null)
          try {
            writer.close();
          } catch (Throwable throwable1) {
            throwable.addSuppressed(throwable1);
          }  
        throw throwable;
      } 
      incEntriesInFile(path, this.items.size() - from);
      int max = ReaderUtils.getInt(this.reader, "history-file-size", 10000);
      if (getEntriesInFile(path) > max + max / 4)
        trimHistory(path, max); 
    } 
    setLastLoaded(path, this.items.size());
  }
  
  protected void trimHistory(Path path, int max) throws IOException {
    Log.trace(new Object[] { "Trimming history path: ", path });
    LinkedList<History.Entry> allItems = new LinkedList<>();
    BufferedReader reader = Files.newBufferedReader(path);
    try {
      reader.lines().forEach(l -> {
            int idx = l.indexOf(':');
            Instant time = Instant.ofEpochMilli(Long.parseLong(l.substring(0, idx)));
            String line = unescape(l.substring(idx + 1));
            allItems.add(createEntry(allItems.size(), time, line));
          });
      if (reader != null)
        reader.close(); 
    } catch (Throwable throwable) {
      if (reader != null)
        try {
          reader.close();
        } catch (Throwable throwable1) {
          throwable.addSuppressed(throwable1);
        }  
      throw throwable;
    } 
    List<History.Entry> trimmedItems = doTrimHistory(allItems, max);
    Path temp = Files.createTempFile(path.toAbsolutePath().getParent(), path.getFileName().toString(), ".tmp", (FileAttribute<?>[])new FileAttribute[0]);
    BufferedWriter writer = Files.newBufferedWriter(temp, new OpenOption[] { StandardOpenOption.WRITE });
    try {
      for (History.Entry entry : trimmedItems)
        writer.append(format(entry)); 
      if (writer != null)
        writer.close(); 
    } catch (Throwable throwable) {
      if (writer != null)
        try {
          writer.close();
        } catch (Throwable throwable1) {
          throwable.addSuppressed(throwable1);
        }  
      throw throwable;
    } 
    Files.move(temp, path, new CopyOption[] { StandardCopyOption.REPLACE_EXISTING });
    if (isLineReaderHistory(path)) {
      internalClear();
      this.offset = ((History.Entry)trimmedItems.get(0)).index();
      this.items.addAll(trimmedItems);
      setHistoryFileData(path, new HistoryFileData(this.items.size(), this.items.size()));
    } else {
      setEntriesInFile(path, allItems.size());
    } 
    maybeResize();
  }
  
  protected EntryImpl createEntry(int index, Instant time, String line) {
    return new EntryImpl(index, time, line);
  }
  
  private void internalClear() {
    this.offset = 0;
    this.index = 0;
    this.historyFiles = new HashMap<>();
    this.items.clear();
  }
  
  static List<History.Entry> doTrimHistory(List<History.Entry> allItems, int max) {
    int idx = 0;
    while (idx < allItems.size()) {
      int ridx = allItems.size() - idx - 1;
      String line = ((History.Entry)allItems.get(ridx)).line().trim();
      ListIterator<History.Entry> iterator = allItems.listIterator(ridx);
      while (iterator.hasPrevious()) {
        String l = ((History.Entry)iterator.previous()).line();
        if (line.equals(l.trim()))
          iterator.remove(); 
      } 
      idx++;
    } 
    while (allItems.size() > max)
      allItems.remove(0); 
    int index = ((History.Entry)allItems.get(allItems.size() - 1)).index() - allItems.size() + 1;
    List<History.Entry> out = new ArrayList<>();
    for (History.Entry e : allItems)
      out.add(new EntryImpl(index++, e.time(), e.line())); 
    return out;
  }
  
  public int size() {
    return this.items.size();
  }
  
  public boolean isEmpty() {
    return this.items.isEmpty();
  }
  
  public int index() {
    return this.offset + this.index;
  }
  
  public int first() {
    return this.offset;
  }
  
  public int last() {
    return this.offset + this.items.size() - 1;
  }
  
  private String format(History.Entry entry) {
    if (this.reader.isSet(LineReader.Option.HISTORY_TIMESTAMPED))
      return entry.time().toEpochMilli() + ":" + escape(entry.line()) + "\n"; 
    return escape(entry.line()) + "\n";
  }
  
  public String get(int index) {
    int idx = index - this.offset;
    if (idx >= this.items.size() || idx < 0)
      throw new IllegalArgumentException("IndexOutOfBounds: Index:" + idx + ", Size:" + this.items.size()); 
    return ((History.Entry)this.items.get(idx)).line();
  }
  
  public void add(Instant time, String line) {
    Objects.requireNonNull(time);
    Objects.requireNonNull(line);
    if (ReaderUtils.getBoolean(this.reader, "disable-history", false))
      return; 
    if (ReaderUtils.isSet(this.reader, LineReader.Option.HISTORY_IGNORE_SPACE) && line.startsWith(" "))
      return; 
    if (ReaderUtils.isSet(this.reader, LineReader.Option.HISTORY_REDUCE_BLANKS))
      line = line.trim(); 
    if (ReaderUtils.isSet(this.reader, LineReader.Option.HISTORY_IGNORE_DUPS) && 
      !this.items.isEmpty() && line.equals(((History.Entry)this.items.getLast()).line()))
      return; 
    if (matchPatterns(ReaderUtils.getString(this.reader, "history-ignore", ""), line))
      return; 
    internalAdd(time, line);
    if (ReaderUtils.isSet(this.reader, LineReader.Option.HISTORY_INCREMENTAL))
      try {
        save();
      } catch (IOException e) {
        Log.warn(new Object[] { "Failed to save history", e });
      }  
  }
  
  protected boolean matchPatterns(String patterns, String line) {
    if (patterns == null || patterns.isEmpty())
      return false; 
    StringBuilder sb = new StringBuilder();
    for (int i = 0; i < patterns.length(); i++) {
      char ch = patterns.charAt(i);
      if (ch == '\\') {
        ch = patterns.charAt(++i);
        sb.append(ch);
      } else if (ch == ':') {
        sb.append('|');
      } else if (ch == '*') {
        sb.append('.').append('*');
      } else {
        sb.append(ch);
      } 
    } 
    return line.matches(sb.toString());
  }
  
  protected void internalAdd(Instant time, String line) {
    internalAdd(time, line, false);
  }
  
  protected void internalAdd(Instant time, String line, boolean checkDuplicates) {
    History.Entry entry = new EntryImpl(this.offset + this.items.size(), time, line);
    if (checkDuplicates)
      for (History.Entry e : this.items) {
        if (e.line().trim().equals(line.trim()))
          return; 
      }  
    this.items.add(entry);
    maybeResize();
  }
  
  private void maybeResize() {
    while (size() > ReaderUtils.getInt(this.reader, "history-size", 500)) {
      this.items.removeFirst();
      for (HistoryFileData hfd : this.historyFiles.values())
        hfd.decLastLoaded(); 
      this.offset++;
    } 
    this.index = size();
  }
  
  public ListIterator<History.Entry> iterator(int index) {
    return this.items.listIterator(index - this.offset);
  }
  
  public Spliterator<History.Entry> spliterator() {
    return this.items.spliterator();
  }
  
  public void resetIndex() {
    this.index = Math.min(this.index, this.items.size());
  }
  
  protected static class EntryImpl implements History.Entry {
    private final int index;
    
    private final Instant time;
    
    private final String line;
    
    public EntryImpl(int index, Instant time, String line) {
      this.index = index;
      this.time = time;
      this.line = line;
    }
    
    public int index() {
      return this.index;
    }
    
    public Instant time() {
      return this.time;
    }
    
    public String line() {
      return this.line;
    }
    
    public String toString() {
      return String.format("%d: %s", new Object[] { Integer.valueOf(this.index), this.line });
    }
  }
  
  public boolean moveToLast() {
    int lastEntry = size() - 1;
    if (lastEntry >= 0 && lastEntry != this.index) {
      this.index = size() - 1;
      return true;
    } 
    return false;
  }
  
  public boolean moveTo(int index) {
    index -= this.offset;
    if (index >= 0 && index < size()) {
      this.index = index;
      return true;
    } 
    return false;
  }
  
  public boolean moveToFirst() {
    if (size() > 0 && this.index != 0) {
      this.index = 0;
      return true;
    } 
    return false;
  }
  
  public void moveToEnd() {
    this.index = size();
  }
  
  public String current() {
    if (this.index >= size())
      return ""; 
    return ((History.Entry)this.items.get(this.index)).line();
  }
  
  public boolean previous() {
    if (this.index <= 0)
      return false; 
    this.index--;
    return true;
  }
  
  public boolean next() {
    if (this.index >= size())
      return false; 
    this.index++;
    return true;
  }
  
  public String toString() {
    StringBuilder sb = new StringBuilder();
    for (ListIterator<History.Entry> listIterator = iterator(); listIterator.hasNext(); ) {
      History.Entry e = listIterator.next();
      sb.append(e.toString()).append("\n");
    } 
    return sb.toString();
  }
  
  private static String escape(String s) {
    StringBuilder sb = new StringBuilder();
    for (int i = 0; i < s.length(); i++) {
      char ch = s.charAt(i);
      switch (ch) {
        case '\n':
          sb.append('\\');
          sb.append('n');
          break;
        case '\r':
          sb.append('\\');
          sb.append('r');
          break;
        case '\\':
          sb.append('\\');
          sb.append('\\');
          break;
        default:
          sb.append(ch);
          break;
      } 
    } 
    return sb.toString();
  }
  
  static String unescape(String s) {
    StringBuilder sb = new StringBuilder();
    for (int i = 0; i < s.length(); i++) {
      char ch = s.charAt(i);
      switch (ch) {
        case '\\':
          ch = s.charAt(++i);
          if (ch == 'n') {
            sb.append('\n');
            break;
          } 
          if (ch == 'r') {
            sb.append('\r');
            break;
          } 
          sb.append(ch);
          break;
        default:
          sb.append(ch);
          break;
      } 
    } 
    return sb.toString();
  }
  
  public DefaultHistory() {}
  
  private static class HistoryFileData {
    private int lastLoaded = 0;
    
    private int entriesInFile = 0;
    
    public HistoryFileData() {}
    
    public HistoryFileData(int lastLoaded, int entriesInFile) {
      this.lastLoaded = lastLoaded;
      this.entriesInFile = entriesInFile;
    }
    
    public int getLastLoaded() {
      return this.lastLoaded;
    }
    
    public void setLastLoaded(int lastLoaded) {
      this.lastLoaded = lastLoaded;
    }
    
    public void decLastLoaded() {
      this.lastLoaded--;
      if (this.lastLoaded < 0)
        this.lastLoaded = 0; 
    }
    
    public int getEntriesInFile() {
      return this.entriesInFile;
    }
    
    public void setEntriesInFile(int entriesInFile) {
      this.entriesInFile = entriesInFile;
    }
    
    public void incEntriesInFile(int amount) {
      this.entriesInFile += amount;
    }
  }
}
