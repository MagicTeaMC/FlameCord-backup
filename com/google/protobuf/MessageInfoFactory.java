package com.google.protobuf;

@CheckReturnValue
interface MessageInfoFactory {
  boolean isSupported(Class<?> paramClass);
  
  MessageInfo messageInfoFor(Class<?> paramClass);
}
