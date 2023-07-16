package joptsimple;

import java.util.List;

class RequiredArgumentOptionSpec<V> extends ArgumentAcceptingOptionSpec<V> {
  RequiredArgumentOptionSpec(String option) {
    super(option, true);
  }
  
  RequiredArgumentOptionSpec(List<String> options, String description) {
    super(options, true, description);
  }
  
  protected void detectOptionArgument(OptionParser parser, ArgumentList arguments, OptionSet detectedOptions) {
    if (!arguments.hasMore())
      throw new OptionMissingRequiredArgumentException(this); 
    addArguments(detectedOptions, arguments.next());
  }
}
