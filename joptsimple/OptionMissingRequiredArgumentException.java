package joptsimple;

import java.util.Arrays;

class OptionMissingRequiredArgumentException extends OptionException {
  private static final long serialVersionUID = -1L;
  
  OptionMissingRequiredArgumentException(OptionSpec<?> option) {
    super(Arrays.asList((OptionSpec<?>[])new OptionSpec[] { option }));
  }
  
  Object[] messageArguments() {
    return new Object[] { singleOptionString() };
  }
}
