package com.google.protobuf;

class GeneratedMessageInfoFactory implements MessageInfoFactory {
  private static final GeneratedMessageInfoFactory instance = new GeneratedMessageInfoFactory();
  
  public static GeneratedMessageInfoFactory getInstance() {
    return instance;
  }
  
  public boolean isSupported(Class<?> messageType) {
    return GeneratedMessageLite.class.isAssignableFrom(messageType);
  }
  
  public MessageInfo messageInfoFor(Class<?> messageType) {
    if (!GeneratedMessageLite.class.isAssignableFrom(messageType))
      throw new IllegalArgumentException("Unsupported message type: " + messageType.getName()); 
    try {
      return (MessageInfo)GeneratedMessageLite.<GeneratedMessageLite<?, ?>>getDefaultInstance((Class)messageType
          .asSubclass((Class)GeneratedMessageLite.class))
        .buildMessageInfo();
    } catch (Exception e) {
      throw new RuntimeException("Unable to get message info for " + messageType.getName(), e);
    } 
  }
}
