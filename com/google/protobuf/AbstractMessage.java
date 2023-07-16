package com.google.protobuf;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public abstract class AbstractMessage extends AbstractMessageLite implements Message {
  public boolean isInitialized() {
    return MessageReflection.isInitialized(this);
  }
  
  protected Message.Builder newBuilderForType(BuilderParent parent) {
    throw new UnsupportedOperationException("Nested builder is not supported for this type.");
  }
  
  public List<String> findInitializationErrors() {
    return MessageReflection.findMissingFields(this);
  }
  
  public String getInitializationErrorString() {
    return MessageReflection.delimitWithCommas(findInitializationErrors());
  }
  
  public boolean hasOneof(Descriptors.OneofDescriptor oneof) {
    throw new UnsupportedOperationException("hasOneof() is not implemented.");
  }
  
  public Descriptors.FieldDescriptor getOneofFieldDescriptor(Descriptors.OneofDescriptor oneof) {
    throw new UnsupportedOperationException("getOneofFieldDescriptor() is not implemented.");
  }
  
  public final String toString() {
    return TextFormat.printer().printToString(this);
  }
  
  public void writeTo(CodedOutputStream output) throws IOException {
    MessageReflection.writeMessageTo(this, getAllFields(), output, false);
  }
  
  protected int memoizedSize = -1;
  
  int getMemoizedSerializedSize() {
    return this.memoizedSize;
  }
  
  void setMemoizedSerializedSize(int size) {
    this.memoizedSize = size;
  }
  
  public int getSerializedSize() {
    int size = this.memoizedSize;
    if (size != -1)
      return size; 
    this.memoizedSize = MessageReflection.getSerializedSize(this, getAllFields());
    return this.memoizedSize;
  }
  
  public boolean equals(Object other) {
    if (other == this)
      return true; 
    if (!(other instanceof Message))
      return false; 
    Message otherMessage = (Message)other;
    if (getDescriptorForType() != otherMessage.getDescriptorForType())
      return false; 
    return (compareFields(getAllFields(), otherMessage.getAllFields()) && 
      getUnknownFields().equals(otherMessage.getUnknownFields()));
  }
  
  public int hashCode() {
    int hash = this.memoizedHashCode;
    if (hash == 0) {
      hash = 41;
      hash = 19 * hash + getDescriptorForType().hashCode();
      hash = hashFields(hash, getAllFields());
      hash = 29 * hash + getUnknownFields().hashCode();
      this.memoizedHashCode = hash;
    } 
    return hash;
  }
  
  private static ByteString toByteString(Object value) {
    if (value instanceof byte[])
      return ByteString.copyFrom((byte[])value); 
    return (ByteString)value;
  }
  
  private static boolean compareBytes(Object a, Object b) {
    if (a instanceof byte[] && b instanceof byte[])
      return Arrays.equals((byte[])a, (byte[])b); 
    return toByteString(a).equals(toByteString(b));
  }
  
  private static Map convertMapEntryListToMap(List list) {
    if (list.isEmpty())
      return Collections.emptyMap(); 
    Map<Object, Object> result = new HashMap<>();
    Iterator<Message> iterator = list.iterator();
    Message entry = iterator.next();
    Descriptors.Descriptor descriptor = entry.getDescriptorForType();
    Descriptors.FieldDescriptor key = descriptor.findFieldByName("key");
    Descriptors.FieldDescriptor value = descriptor.findFieldByName("value");
    Object fieldValue = entry.getField(value);
    if (fieldValue instanceof Descriptors.EnumValueDescriptor)
      fieldValue = Integer.valueOf(((Descriptors.EnumValueDescriptor)fieldValue).getNumber()); 
    result.put(entry.getField(key), fieldValue);
    while (iterator.hasNext()) {
      entry = iterator.next();
      fieldValue = entry.getField(value);
      if (fieldValue instanceof Descriptors.EnumValueDescriptor)
        fieldValue = Integer.valueOf(((Descriptors.EnumValueDescriptor)fieldValue).getNumber()); 
      result.put(entry.getField(key), fieldValue);
    } 
    return result;
  }
  
  private static boolean compareMapField(Object a, Object b) {
    Map<?, ?> ma = convertMapEntryListToMap((List)a);
    Map<?, ?> mb = convertMapEntryListToMap((List)b);
    return MapFieldLite.equals(ma, mb);
  }
  
  static boolean compareFields(Map<Descriptors.FieldDescriptor, Object> a, Map<Descriptors.FieldDescriptor, Object> b) {
    if (a.size() != b.size())
      return false; 
    for (Descriptors.FieldDescriptor descriptor : a.keySet()) {
      if (!b.containsKey(descriptor))
        return false; 
      Object value1 = a.get(descriptor);
      Object value2 = b.get(descriptor);
      if (descriptor.getType() == Descriptors.FieldDescriptor.Type.BYTES) {
        if (descriptor.isRepeated()) {
          List<?> list1 = (List)value1;
          List<?> list2 = (List)value2;
          if (list1.size() != list2.size())
            return false; 
          for (int i = 0; i < list1.size(); i++) {
            if (!compareBytes(list1.get(i), list2.get(i)))
              return false; 
          } 
          continue;
        } 
        if (!compareBytes(value1, value2))
          return false; 
        continue;
      } 
      if (descriptor.isMapField()) {
        if (!compareMapField(value1, value2))
          return false; 
        continue;
      } 
      if (!value1.equals(value2))
        return false; 
    } 
    return true;
  }
  
  private static int hashMapField(Object value) {
    return MapFieldLite.calculateHashCodeForMap(convertMapEntryListToMap((List)value));
  }
  
  protected static int hashFields(int hash, Map<Descriptors.FieldDescriptor, Object> map) {
    for (Map.Entry<Descriptors.FieldDescriptor, Object> entry : map.entrySet()) {
      Descriptors.FieldDescriptor field = entry.getKey();
      Object value = entry.getValue();
      hash = 37 * hash + field.getNumber();
      if (field.isMapField()) {
        hash = 53 * hash + hashMapField(value);
        continue;
      } 
      if (field.getType() != Descriptors.FieldDescriptor.Type.ENUM) {
        hash = 53 * hash + value.hashCode();
        continue;
      } 
      if (field.isRepeated()) {
        List<? extends Internal.EnumLite> list = (List<? extends Internal.EnumLite>)value;
        hash = 53 * hash + Internal.hashEnumList(list);
        continue;
      } 
      hash = 53 * hash + Internal.hashEnum((Internal.EnumLite)value);
    } 
    return hash;
  }
  
  UninitializedMessageException newUninitializedMessageException() {
    return Builder.newUninitializedMessageException(this);
  }
  
  public static abstract class Builder<BuilderType extends Builder<BuilderType>> extends AbstractMessageLite.Builder implements Message.Builder {
    public BuilderType clone() {
      throw new UnsupportedOperationException("clone() should be implemented in subclasses.");
    }
    
    public boolean hasOneof(Descriptors.OneofDescriptor oneof) {
      throw new UnsupportedOperationException("hasOneof() is not implemented.");
    }
    
    public Descriptors.FieldDescriptor getOneofFieldDescriptor(Descriptors.OneofDescriptor oneof) {
      throw new UnsupportedOperationException("getOneofFieldDescriptor() is not implemented.");
    }
    
    public BuilderType clearOneof(Descriptors.OneofDescriptor oneof) {
      throw new UnsupportedOperationException("clearOneof() is not implemented.");
    }
    
    public BuilderType clear() {
      for (Map.Entry<Descriptors.FieldDescriptor, Object> entry : getAllFields().entrySet())
        clearField(entry.getKey()); 
      return (BuilderType)this;
    }
    
    public List<String> findInitializationErrors() {
      return MessageReflection.findMissingFields(this);
    }
    
    public String getInitializationErrorString() {
      return MessageReflection.delimitWithCommas(findInitializationErrors());
    }
    
    protected BuilderType internalMergeFrom(AbstractMessageLite other) {
      return mergeFrom((Message)other);
    }
    
    public BuilderType mergeFrom(Message other) {
      return mergeFrom(other, other.getAllFields());
    }
    
    BuilderType mergeFrom(Message other, Map<Descriptors.FieldDescriptor, Object> allFields) {
      if (other.getDescriptorForType() != getDescriptorForType())
        throw new IllegalArgumentException("mergeFrom(Message) can only merge messages of the same type."); 
      for (Map.Entry<Descriptors.FieldDescriptor, Object> entry : allFields.entrySet()) {
        Descriptors.FieldDescriptor field = entry.getKey();
        if (field.isRepeated()) {
          for (Object element : entry.getValue())
            addRepeatedField(field, element); 
          continue;
        } 
        if (field.getJavaType() == Descriptors.FieldDescriptor.JavaType.MESSAGE) {
          Message existingValue = (Message)getField(field);
          if (existingValue == existingValue.getDefaultInstanceForType()) {
            setField(field, entry.getValue());
            continue;
          } 
          setField(field, existingValue
              
              .newBuilderForType()
              .mergeFrom(existingValue)
              .mergeFrom((Message)entry.getValue())
              .build());
          continue;
        } 
        setField(field, entry.getValue());
      } 
      mergeUnknownFields(other.getUnknownFields());
      return (BuilderType)this;
    }
    
    public BuilderType mergeFrom(CodedInputStream input) throws IOException {
      return mergeFrom(input, ExtensionRegistry.getEmptyRegistry());
    }
    
    public BuilderType mergeFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
      boolean discardUnknown = input.shouldDiscardUnknownFields();
      UnknownFieldSet.Builder unknownFields = discardUnknown ? null : getUnknownFieldSetBuilder();
      MessageReflection.mergeMessageFrom(this, unknownFields, input, extensionRegistry);
      if (unknownFields != null)
        setUnknownFieldSetBuilder(unknownFields); 
      return (BuilderType)this;
    }
    
    protected UnknownFieldSet.Builder getUnknownFieldSetBuilder() {
      return UnknownFieldSet.newBuilder(getUnknownFields());
    }
    
    protected void setUnknownFieldSetBuilder(UnknownFieldSet.Builder builder) {
      setUnknownFields(builder.build());
    }
    
    public BuilderType mergeUnknownFields(UnknownFieldSet unknownFields) {
      setUnknownFields(
          UnknownFieldSet.newBuilder(getUnknownFields()).mergeFrom(unknownFields).build());
      return (BuilderType)this;
    }
    
    public Message.Builder getFieldBuilder(Descriptors.FieldDescriptor field) {
      throw new UnsupportedOperationException("getFieldBuilder() called on an unsupported message type.");
    }
    
    public Message.Builder getRepeatedFieldBuilder(Descriptors.FieldDescriptor field, int index) {
      throw new UnsupportedOperationException("getRepeatedFieldBuilder() called on an unsupported message type.");
    }
    
    public String toString() {
      return TextFormat.printer().printToString(this);
    }
    
    protected static UninitializedMessageException newUninitializedMessageException(Message message) {
      return new UninitializedMessageException(MessageReflection.findMissingFields(message));
    }
    
    void markClean() {
      throw new IllegalStateException("Should be overridden by subclasses.");
    }
    
    void dispose() {
      throw new IllegalStateException("Should be overridden by subclasses.");
    }
    
    public BuilderType mergeFrom(ByteString data) throws InvalidProtocolBufferException {
      return (BuilderType)super.mergeFrom(data);
    }
    
    public BuilderType mergeFrom(ByteString data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
      return (BuilderType)super.mergeFrom(data, extensionRegistry);
    }
    
    public BuilderType mergeFrom(byte[] data) throws InvalidProtocolBufferException {
      return (BuilderType)super.mergeFrom(data);
    }
    
    public BuilderType mergeFrom(byte[] data, int off, int len) throws InvalidProtocolBufferException {
      return (BuilderType)super.mergeFrom(data, off, len);
    }
    
    public BuilderType mergeFrom(byte[] data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
      return (BuilderType)super.mergeFrom(data, extensionRegistry);
    }
    
    public BuilderType mergeFrom(byte[] data, int off, int len, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
      return (BuilderType)super.mergeFrom(data, off, len, extensionRegistry);
    }
    
    public BuilderType mergeFrom(InputStream input) throws IOException {
      return (BuilderType)super.mergeFrom(input);
    }
    
    public BuilderType mergeFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
      return (BuilderType)super.mergeFrom(input, extensionRegistry);
    }
  }
  
  @Deprecated
  protected static int hashLong(long n) {
    return (int)(n ^ n >>> 32L);
  }
  
  @Deprecated
  protected static int hashBoolean(boolean b) {
    return b ? 1231 : 1237;
  }
  
  @Deprecated
  protected static int hashEnum(Internal.EnumLite e) {
    return e.getNumber();
  }
  
  @Deprecated
  protected static int hashEnumList(List<? extends Internal.EnumLite> list) {
    int hash = 1;
    for (Internal.EnumLite e : list)
      hash = 31 * hash + hashEnum(e); 
    return hash;
  }
  
  protected static interface BuilderParent {
    void markDirty();
  }
}
