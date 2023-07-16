package org.apache.logging.log4j.message;

public interface ThreadInformation {
  void printThreadInfo(StringBuilder paramStringBuilder);
  
  void printStack(StringBuilder paramStringBuilder, StackTraceElement[] paramArrayOfStackTraceElement);
}
