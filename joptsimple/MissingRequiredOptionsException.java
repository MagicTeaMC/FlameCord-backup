package joptsimple;

import java.util.List;

class MissingRequiredOptionsException extends OptionException {
  private static final long serialVersionUID = -1L;
  
  protected MissingRequiredOptionsException(List<? extends OptionSpec<?>> missingRequiredOptions) {
    super(missingRequiredOptions);
  }
  
  Object[] messageArguments() {
    return new Object[] { multipleOptionString() };
  }
}
