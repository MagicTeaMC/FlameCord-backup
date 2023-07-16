package com.google.protobuf;

import java.io.IOException;
import java.util.Collections;
import java.util.Map;
import java.util.TreeMap;

public final class MapEntry<K, V> extends AbstractMessage {
  private final K key;
  
  private final V value;
  
  private final Metadata<K, V> metadata;
  
  private volatile int cachedSerializedSize;
  
  private static final class Metadata<K, V> extends MapEntryLite.Metadata<K, V> {
    public final Descriptors.Descriptor descriptor;
    
    public final Parser<MapEntry<K, V>> parser;
    
    public Metadata(Descriptors.Descriptor descriptor, MapEntry<K, V> defaultInstance, WireFormat.FieldType keyType, WireFormat.FieldType valueType) {
      super(keyType, defaultInstance.key, valueType, defaultInstance.value);
      this.descriptor = descriptor;
      this.parser = new AbstractParser<MapEntry<K, V>>() {
          public MapEntry<K, V> parsePartialFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            return new MapEntry<>(MapEntry.Metadata.this, input, extensionRegistry);
          }
        };
    }
  }
  
  private MapEntry(Descriptors.Descriptor descriptor, WireFormat.FieldType keyType, K defaultKey, WireFormat.FieldType valueType, V defaultValue) {
    this.cachedSerializedSize = -1;
    this.key = defaultKey;
    this.value = defaultValue;
    this.metadata = new Metadata<>(descriptor, this, keyType, valueType);
  }
  
  private MapEntry(Metadata<K, V> metadata, K key, V value) {
    this.cachedSerializedSize = -1;
    this.key = key;
    this.value = value;
    this.metadata = metadata;
  }
  
  private MapEntry(Metadata<K, V> metadata, CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
    this.cachedSerializedSize = -1;
    try {
      this.metadata = metadata;
      Map.Entry<K, V> entry = MapEntryLite.parseEntry(input, metadata, extensionRegistry);
      this.key = entry.getKey();
      this.value = entry.getValue();
    } catch (InvalidProtocolBufferException e) {
      throw e.setUnfinishedMessage(this);
    } catch (IOException e) {
      throw (new InvalidProtocolBufferException(e)).setUnfinishedMessage(this);
    } 
  }
  
  public static <K, V> MapEntry<K, V> newDefaultInstance(Descriptors.Descriptor descriptor, WireFormat.FieldType keyType, K defaultKey, WireFormat.FieldType valueType, V defaultValue) {
    return new MapEntry<>(descriptor, keyType, defaultKey, valueType, defaultValue);
  }
  
  public K getKey() {
    return this.key;
  }
  
  public V getValue() {
    return this.value;
  }
  
  public int getSerializedSize() {
    if (this.cachedSerializedSize != -1)
      return this.cachedSerializedSize; 
    int size = MapEntryLite.computeSerializedSize(this.metadata, this.key, this.value);
    this.cachedSerializedSize = size;
    return size;
  }
  
  public void writeTo(CodedOutputStream output) throws IOException {
    MapEntryLite.writeTo(output, this.metadata, this.key, this.value);
  }
  
  public boolean isInitialized() {
    return isInitialized(this.metadata, this.value);
  }
  
  public Parser<MapEntry<K, V>> getParserForType() {
    return this.metadata.parser;
  }
  
  public Builder<K, V> newBuilderForType() {
    return new Builder<>(this.metadata);
  }
  
  public Builder<K, V> toBuilder() {
    return new Builder<>(this.metadata, this.key, this.value, true, true);
  }
  
  public MapEntry<K, V> getDefaultInstanceForType() {
    return new MapEntry(this.metadata, this.metadata.defaultKey, this.metadata.defaultValue);
  }
  
  public Descriptors.Descriptor getDescriptorForType() {
    return this.metadata.descriptor;
  }
  
  public Map<Descriptors.FieldDescriptor, Object> getAllFields() {
    TreeMap<Descriptors.FieldDescriptor, Object> result = new TreeMap<>();
    for (Descriptors.FieldDescriptor field : this.metadata.descriptor.getFields()) {
      if (hasField(field))
        result.put(field, getField(field)); 
    } 
    return Collections.unmodifiableMap(result);
  }
  
  private void checkFieldDescriptor(Descriptors.FieldDescriptor field) {
    if (field.getContainingType() != this.metadata.descriptor)
      throw new RuntimeException("Wrong FieldDescriptor \"" + field
          
          .getFullName() + "\" used in message \"" + this.metadata.descriptor
          
          .getFullName()); 
  }
  
  public boolean hasField(Descriptors.FieldDescriptor field) {
    checkFieldDescriptor(field);
    return true;
  }
  
  public Object getField(Descriptors.FieldDescriptor field) {
    checkFieldDescriptor(field);
    Object result = (field.getNumber() == 1) ? getKey() : getValue();
    if (field.getType() == Descriptors.FieldDescriptor.Type.ENUM)
      result = field.getEnumType().findValueByNumberCreatingIfUnknown(((Integer)result).intValue()); 
    return result;
  }
  
  public int getRepeatedFieldCount(Descriptors.FieldDescriptor field) {
    throw new RuntimeException("There is no repeated field in a map entry message.");
  }
  
  public Object getRepeatedField(Descriptors.FieldDescriptor field, int index) {
    throw new RuntimeException("There is no repeated field in a map entry message.");
  }
  
  public UnknownFieldSet getUnknownFields() {
    return UnknownFieldSet.getDefaultInstance();
  }
  
  public static class Builder<K, V> extends AbstractMessage.Builder<Builder<K, V>> {
    private final MapEntry.Metadata<K, V> metadata;
    
    private K key;
    
    private V value;
    
    private boolean hasKey;
    
    private boolean hasValue;
    
    private Builder(MapEntry.Metadata<K, V> metadata) {
      this(metadata, metadata.defaultKey, metadata.defaultValue, false, false);
    }
    
    private Builder(MapEntry.Metadata<K, V> metadata, K key, V value, boolean hasKey, boolean hasValue) {
      this.metadata = metadata;
      this.key = key;
      this.value = value;
      this.hasKey = hasKey;
      this.hasValue = hasValue;
    }
    
    public K getKey() {
      return this.key;
    }
    
    public V getValue() {
      return this.value;
    }
    
    public Builder<K, V> setKey(K key) {
      this.key = key;
      this.hasKey = true;
      return this;
    }
    
    public Builder<K, V> clearKey() {
      this.key = this.metadata.defaultKey;
      this.hasKey = false;
      return this;
    }
    
    public Builder<K, V> setValue(V value) {
      this.value = value;
      this.hasValue = true;
      return this;
    }
    
    public Builder<K, V> clearValue() {
      this.value = this.metadata.defaultValue;
      this.hasValue = false;
      return this;
    }
    
    public MapEntry<K, V> build() {
      MapEntry<K, V> result = buildPartial();
      if (!result.isInitialized())
        throw newUninitializedMessageException(result); 
      return result;
    }
    
    public MapEntry<K, V> buildPartial() {
      return new MapEntry<>(this.metadata, this.key, this.value);
    }
    
    public Descriptors.Descriptor getDescriptorForType() {
      return this.metadata.descriptor;
    }
    
    private void checkFieldDescriptor(Descriptors.FieldDescriptor field) {
      if (field.getContainingType() != this.metadata.descriptor)
        throw new RuntimeException("Wrong FieldDescriptor \"" + field
            
            .getFullName() + "\" used in message \"" + this.metadata.descriptor
            
            .getFullName()); 
    }
    
    public Message.Builder newBuilderForField(Descriptors.FieldDescriptor field) {
      checkFieldDescriptor(field);
      if (field.getNumber() != 2 || field.getJavaType() != Descriptors.FieldDescriptor.JavaType.MESSAGE)
        throw new RuntimeException("\"" + field.getFullName() + "\" is not a message value field."); 
      return ((Message)this.value).newBuilderForType();
    }
    
    public Builder<K, V> setField(Descriptors.FieldDescriptor field, Object value) {
      checkFieldDescriptor(field);
      if (value == null)
        throw new NullPointerException(field.getFullName() + " is null"); 
      if (field.getNumber() == 1) {
        setKey((K)value);
      } else {
        if (field.getType() == Descriptors.FieldDescriptor.Type.ENUM) {
          value = Integer.valueOf(((Descriptors.EnumValueDescriptor)value).getNumber());
        } else if (field.getType() == Descriptors.FieldDescriptor.Type.MESSAGE && 
          !this.metadata.defaultValue.getClass().isInstance(value)) {
          value = ((Message)this.metadata.defaultValue).toBuilder().mergeFrom((Message)value).build();
        } 
        setValue((V)value);
      } 
      return this;
    }
    
    public Builder<K, V> clearField(Descriptors.FieldDescriptor field) {
      checkFieldDescriptor(field);
      if (field.getNumber() == 1) {
        clearKey();
      } else {
        clearValue();
      } 
      return this;
    }
    
    public Builder<K, V> setRepeatedField(Descriptors.FieldDescriptor field, int index, Object value) {
      throw new RuntimeException("There is no repeated field in a map entry message.");
    }
    
    public Builder<K, V> addRepeatedField(Descriptors.FieldDescriptor field, Object value) {
      throw new RuntimeException("There is no repeated field in a map entry message.");
    }
    
    public Builder<K, V> setUnknownFields(UnknownFieldSet unknownFields) {
      return this;
    }
    
    public MapEntry<K, V> getDefaultInstanceForType() {
      return new MapEntry<>(this.metadata, this.metadata.defaultKey, this.metadata.defaultValue);
    }
    
    public boolean isInitialized() {
      return MapEntry.isInitialized(this.metadata, this.value);
    }
    
    public Map<Descriptors.FieldDescriptor, Object> getAllFields() {
      TreeMap<Descriptors.FieldDescriptor, Object> result = new TreeMap<>();
      for (Descriptors.FieldDescriptor field : this.metadata.descriptor.getFields()) {
        if (hasField(field))
          result.put(field, getField(field)); 
      } 
      return Collections.unmodifiableMap(result);
    }
    
    public boolean hasField(Descriptors.FieldDescriptor field) {
      checkFieldDescriptor(field);
      return (field.getNumber() == 1) ? this.hasKey : this.hasValue;
    }
    
    public Object getField(Descriptors.FieldDescriptor field) {
      checkFieldDescriptor(field);
      Object result = (field.getNumber() == 1) ? getKey() : getValue();
      if (field.getType() == Descriptors.FieldDescriptor.Type.ENUM)
        result = field.getEnumType().findValueByNumberCreatingIfUnknown(((Integer)result).intValue()); 
      return result;
    }
    
    public int getRepeatedFieldCount(Descriptors.FieldDescriptor field) {
      throw new RuntimeException("There is no repeated field in a map entry message.");
    }
    
    public Object getRepeatedField(Descriptors.FieldDescriptor field, int index) {
      throw new RuntimeException("There is no repeated field in a map entry message.");
    }
    
    public UnknownFieldSet getUnknownFields() {
      return UnknownFieldSet.getDefaultInstance();
    }
    
    public Builder<K, V> clone() {
      return new Builder(this.metadata, this.key, this.value, this.hasKey, this.hasValue);
    }
  }
  
  private static <V> boolean isInitialized(Metadata metadata, V value) {
    if (metadata.valueType.getJavaType() == WireFormat.JavaType.MESSAGE)
      return ((MessageLite)value).isInitialized(); 
    return true;
  }
  
  final Metadata<K, V> getMetadata() {
    return this.metadata;
  }
}
