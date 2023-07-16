package com.google.protobuf;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@CheckReturnValue
final class Protobuf {
  private static final Protobuf INSTANCE = new Protobuf();
  
  private final SchemaFactory schemaFactory;
  
  private final ConcurrentMap<Class<?>, Schema<?>> schemaCache = new ConcurrentHashMap<>();
  
  public static Protobuf getInstance() {
    return INSTANCE;
  }
  
  public <T> void writeTo(T message, Writer writer) throws IOException {
    schemaFor(message).writeTo(message, writer);
  }
  
  public <T> void mergeFrom(T message, Reader reader) throws IOException {
    mergeFrom(message, reader, ExtensionRegistryLite.getEmptyRegistry());
  }
  
  public <T> void mergeFrom(T message, Reader reader, ExtensionRegistryLite extensionRegistry) throws IOException {
    schemaFor(message).mergeFrom(message, reader, extensionRegistry);
  }
  
  public <T> void makeImmutable(T message) {
    schemaFor(message).makeImmutable(message);
  }
  
  <T> boolean isInitialized(T message) {
    return schemaFor(message).isInitialized(message);
  }
  
  public <T> Schema<T> schemaFor(Class<T> messageType) {
    Internal.checkNotNull(messageType, "messageType");
    Schema<T> schema = (Schema<T>)this.schemaCache.get(messageType);
    if (schema == null) {
      schema = this.schemaFactory.createSchema(messageType);
      Schema<T> previous = (Schema)registerSchema(messageType, schema);
      if (previous != null)
        schema = previous; 
    } 
    return schema;
  }
  
  public <T> Schema<T> schemaFor(T message) {
    return schemaFor((Class)message.getClass());
  }
  
  public Schema<?> registerSchema(Class<?> messageType, Schema<?> schema) {
    Internal.checkNotNull(messageType, "messageType");
    Internal.checkNotNull(schema, "schema");
    return this.schemaCache.putIfAbsent(messageType, schema);
  }
  
  @CanIgnoreReturnValue
  public Schema<?> registerSchemaOverride(Class<?> messageType, Schema<?> schema) {
    Internal.checkNotNull(messageType, "messageType");
    Internal.checkNotNull(schema, "schema");
    return this.schemaCache.put(messageType, schema);
  }
  
  private Protobuf() {
    this.schemaFactory = new ManifestSchemaFactory();
  }
  
  int getTotalSchemaSize() {
    int result = 0;
    for (Schema<?> schema : this.schemaCache.values()) {
      if (schema instanceof MessageSchema)
        result += ((MessageSchema)schema).getSchemaSize(); 
    } 
    return result;
  }
}
