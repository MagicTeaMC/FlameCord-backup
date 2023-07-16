package joptsimple;

import java.util.Collections;

class IllegalOptionSpecificationException extends OptionException {
  private static final long serialVersionUID = -1L;
  
  IllegalOptionSpecificationException(String option) {
    super(Collections.singletonList(option));
  }
  
  Object[] messageArguments() {
    return new Object[] { singleOptionString() };
  }
}
