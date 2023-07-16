package org.jline.reader;

import java.util.List;

public interface Completer {
  void complete(LineReader paramLineReader, ParsedLine paramParsedLine, List<Candidate> paramList);
}
