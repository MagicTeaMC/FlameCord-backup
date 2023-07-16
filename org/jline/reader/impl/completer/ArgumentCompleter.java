package org.jline.reader.impl.completer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import org.jline.reader.Candidate;
import org.jline.reader.Completer;
import org.jline.reader.LineReader;
import org.jline.reader.ParsedLine;

public class ArgumentCompleter implements Completer {
  private final List<Completer> completers = new ArrayList<>();
  
  private boolean strict = true;
  
  private boolean strictCommand = true;
  
  public ArgumentCompleter(Collection<Completer> completers) {
    Objects.requireNonNull(completers);
    this.completers.addAll(completers);
  }
  
  public ArgumentCompleter(Completer... completers) {
    this(Arrays.asList(completers));
  }
  
  public void setStrict(boolean strict) {
    this.strict = strict;
  }
  
  public void setStrictCommand(boolean strictCommand) {
    this.strictCommand = strictCommand;
  }
  
  public boolean isStrict() {
    return this.strict;
  }
  
  public List<Completer> getCompleters() {
    return this.completers;
  }
  
  public void complete(LineReader reader, ParsedLine line, List<Candidate> candidates) {
    Completer completer;
    Objects.requireNonNull(line);
    Objects.requireNonNull(candidates);
    if (line.wordIndex() < 0)
      return; 
    List<Completer> completers = getCompleters();
    if (line.wordIndex() >= completers.size()) {
      completer = completers.get(completers.size() - 1);
    } else {
      completer = completers.get(line.wordIndex());
    } 
    for (int i = this.strictCommand ? 0 : 1; isStrict() && i < line.wordIndex(); i++) {
      int idx = (i >= completers.size()) ? (completers.size() - 1) : i;
      if (idx != 0 || this.strictCommand) {
        Completer sub = completers.get(idx);
        List<? extends CharSequence> args = line.words();
        String arg = (args == null || i >= args.size()) ? "" : ((CharSequence)args.get(i)).toString();
        List<Candidate> subCandidates = new LinkedList<>();
        sub.complete(reader, new ArgumentLine(arg, arg.length()), subCandidates);
        boolean found = false;
        for (Candidate cand : subCandidates) {
          if (cand.value().equals(arg)) {
            found = true;
            break;
          } 
        } 
        if (!found)
          return; 
      } 
    } 
    completer.complete(reader, line, candidates);
  }
  
  public static class ArgumentLine implements ParsedLine {
    private final String word;
    
    private final int cursor;
    
    public ArgumentLine(String word, int cursor) {
      this.word = word;
      this.cursor = cursor;
    }
    
    public String word() {
      return this.word;
    }
    
    public int wordCursor() {
      return this.cursor;
    }
    
    public int wordIndex() {
      return 0;
    }
    
    public List<String> words() {
      return Collections.singletonList(this.word);
    }
    
    public String line() {
      return this.word;
    }
    
    public int cursor() {
      return this.cursor;
    }
  }
}
