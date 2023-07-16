package joptsimple;

import java.util.List;

public interface OptionSpec<V> {
  List<V> values(OptionSet paramOptionSet);
  
  V value(OptionSet paramOptionSet);
  
  List<String> options();
  
  boolean isForHelp();
}
