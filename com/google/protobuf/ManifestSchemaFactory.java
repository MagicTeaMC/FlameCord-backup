package com.google.protobuf;

@CheckReturnValue
final class ManifestSchemaFactory implements SchemaFactory {
  private final MessageInfoFactory messageInfoFactory;
  
  public ManifestSchemaFactory() {
    this(getDefaultMessageInfoFactory());
  }
  
  private ManifestSchemaFactory(MessageInfoFactory messageInfoFactory) {
    this.messageInfoFactory = Internal.<MessageInfoFactory>checkNotNull(messageInfoFactory, "messageInfoFactory");
  }
  
  public <T> Schema<T> createSchema(Class<T> messageType) {
    SchemaUtil.requireGeneratedMessage(messageType);
    MessageInfo messageInfo = this.messageInfoFactory.messageInfoFor(messageType);
    if (messageInfo.isMessageSetWireFormat()) {
      if (GeneratedMessageLite.class.isAssignableFrom(messageType))
        return MessageSetSchema.newSchema(
            SchemaUtil.unknownFieldSetLiteSchema(), 
            ExtensionSchemas.lite(), messageInfo
            .getDefaultInstance()); 
      return MessageSetSchema.newSchema(
          SchemaUtil.proto2UnknownFieldSetSchema(), 
          ExtensionSchemas.full(), messageInfo
          .getDefaultInstance());
    } 
    return newSchema(messageType, messageInfo);
  }
  
  private static <T> Schema<T> newSchema(Class<T> messageType, MessageInfo messageInfo) {
    if (GeneratedMessageLite.class.isAssignableFrom(messageType))
      return isProto2(messageInfo) ? 
        MessageSchema.<T>newSchema(messageType, messageInfo, 
          
          NewInstanceSchemas.lite(), 
          ListFieldSchema.lite(), 
          SchemaUtil.unknownFieldSetLiteSchema(), 
          ExtensionSchemas.lite(), 
          MapFieldSchemas.lite()) : 
        MessageSchema.<T>newSchema(messageType, messageInfo, 
          
          NewInstanceSchemas.lite(), 
          ListFieldSchema.lite(), 
          SchemaUtil.unknownFieldSetLiteSchema(), null, 
          
          MapFieldSchemas.lite()); 
    return isProto2(messageInfo) ? 
      MessageSchema.<T>newSchema(messageType, messageInfo, 
        
        NewInstanceSchemas.full(), 
        ListFieldSchema.full(), 
        SchemaUtil.proto2UnknownFieldSetSchema(), 
        ExtensionSchemas.full(), 
        MapFieldSchemas.full()) : 
      MessageSchema.<T>newSchema(messageType, messageInfo, 
        
        NewInstanceSchemas.full(), 
        ListFieldSchema.full(), 
        SchemaUtil.proto3UnknownFieldSetSchema(), null, 
        
        MapFieldSchemas.full());
  }
  
  private static boolean isProto2(MessageInfo messageInfo) {
    return (messageInfo.getSyntax() == ProtoSyntax.PROTO2);
  }
  
  private static MessageInfoFactory getDefaultMessageInfoFactory() {
    return new CompositeMessageInfoFactory(new MessageInfoFactory[] { GeneratedMessageInfoFactory.getInstance(), getDescriptorMessageInfoFactory() });
  }
  
  private static class CompositeMessageInfoFactory implements MessageInfoFactory {
    private MessageInfoFactory[] factories;
    
    CompositeMessageInfoFactory(MessageInfoFactory... factories) {
      this.factories = factories;
    }
    
    public boolean isSupported(Class<?> clazz) {
      for (MessageInfoFactory factory : this.factories) {
        if (factory.isSupported(clazz))
          return true; 
      } 
      return false;
    }
    
    public MessageInfo messageInfoFor(Class<?> clazz) {
      for (MessageInfoFactory factory : this.factories) {
        if (factory.isSupported(clazz))
          return factory.messageInfoFor(clazz); 
      } 
      throw new UnsupportedOperationException("No factory is available for message type: " + clazz
          .getName());
    }
  }
  
  private static final MessageInfoFactory EMPTY_FACTORY = new MessageInfoFactory() {
      public boolean isSupported(Class<?> clazz) {
        return false;
      }
      
      public MessageInfo messageInfoFor(Class<?> clazz) {
        throw new IllegalStateException("This should never be called.");
      }
    };
  
  private static MessageInfoFactory getDescriptorMessageInfoFactory() {
    try {
      Class<?> clazz = Class.forName("com.google.protobuf.DescriptorMessageInfoFactory");
      return (MessageInfoFactory)clazz.getDeclaredMethod("getInstance", new Class[0]).invoke(null, new Object[0]);
    } catch (Exception e) {
      return EMPTY_FACTORY;
    } 
  }
}
