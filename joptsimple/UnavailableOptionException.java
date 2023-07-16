package joptsimple;

import java.util.List;

class UnavailableOptionException extends OptionException {
  private static final long serialVersionUID = -1L;
  
  UnavailableOptionException(List<? extends OptionSpec<?>> forbiddenOptions) {
    super(forbiddenOptions);
  }
  
  Object[] messageArguments() {
    return new Object[] { multipleOptionString() };
  }
}
