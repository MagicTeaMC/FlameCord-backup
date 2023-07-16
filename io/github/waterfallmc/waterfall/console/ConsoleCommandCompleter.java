package io.github.waterfallmc.waterfall.console;

import java.util.List;
import net.md_5.bungee.api.ProxyServer;
import org.jline.reader.Candidate;
import org.jline.reader.Completer;
import org.jline.reader.LineReader;
import org.jline.reader.ParsedLine;

final class ConsoleCommandCompleter implements Completer {
  private final ProxyServer proxy;
  
  ConsoleCommandCompleter(ProxyServer proxy) {
    this.proxy = proxy;
  }
  
  public void complete(LineReader reader, ParsedLine line, List<Candidate> candidates) {
    List<String> suggestions = this.proxy.getPluginManager().tabCompleteCommand(this.proxy.getConsole(), line.line());
    if (suggestions.isEmpty())
      return; 
    for (String suggestion : suggestions)
      candidates.add(new Candidate(suggestion)); 
  }
}
