package com.google.protobuf;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

public class TypeRegistry {
  private static final Logger logger = Logger.getLogger(TypeRegistry.class.getName());
  
  private final Map<String, Descriptors.Descriptor> types;
  
  private static class EmptyTypeRegistryHolder {
    private static final TypeRegistry EMPTY = new TypeRegistry(
        Collections.emptyMap());
  }
  
  public static TypeRegistry getEmptyTypeRegistry() {
    return EmptyTypeRegistryHolder.EMPTY;
  }
  
  public static Builder newBuilder() {
    return new Builder();
  }
  
  public Descriptors.Descriptor find(String name) {
    return this.types.get(name);
  }
  
  public final Descriptors.Descriptor getDescriptorForTypeUrl(String typeUrl) throws InvalidProtocolBufferException {
    return find(getTypeName(typeUrl));
  }
  
  TypeRegistry(Map<String, Descriptors.Descriptor> types) {
    this.types = types;
  }
  
  private static String getTypeName(String typeUrl) throws InvalidProtocolBufferException {
    String[] parts = typeUrl.split("/");
    if (parts.length == 1)
      throw new InvalidProtocolBufferException("Invalid type url found: " + typeUrl); 
    return parts[parts.length - 1];
  }
  
  public static final class Builder {
    public Builder add(Descriptors.Descriptor messageType) {
      if (this.types == null)
        throw new IllegalStateException("A TypeRegistry.Builder can only be used once."); 
      addFile(messageType.getFile());
      return this;
    }
    
    public Builder add(Iterable<Descriptors.Descriptor> messageTypes) {
      if (this.types == null)
        throw new IllegalStateException("A TypeRegistry.Builder can only be used once."); 
      for (Descriptors.Descriptor type : messageTypes)
        addFile(type.getFile()); 
      return this;
    }
    
    public TypeRegistry build() {
      TypeRegistry result = new TypeRegistry(this.types);
      this.types = null;
      return result;
    }
    
    private void addFile(Descriptors.FileDescriptor file) {
      if (!this.files.add(file.getFullName()))
        return; 
      for (Descriptors.FileDescriptor dependency : file.getDependencies())
        addFile(dependency); 
      for (Descriptors.Descriptor message : file.getMessageTypes())
        addMessage(message); 
    }
    
    private void addMessage(Descriptors.Descriptor message) {
      for (Descriptors.Descriptor nestedType : message.getNestedTypes())
        addMessage(nestedType); 
      if (this.types.containsKey(message.getFullName())) {
        TypeRegistry.logger.warning("Type " + message.getFullName() + " is added multiple times.");
        return;
      } 
      this.types.put(message.getFullName(), message);
    }
    
    private final Set<String> files = new HashSet<>();
    
    private Map<String, Descriptors.Descriptor> types = new HashMap<>();
    
    private Builder() {}
  }
}
