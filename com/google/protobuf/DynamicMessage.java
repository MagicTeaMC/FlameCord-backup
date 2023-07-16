package com.google.protobuf;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Collections;
import java.util.Map;

public final class DynamicMessage extends AbstractMessage {
  private final Descriptors.Descriptor type;
  
  private final FieldSet<Descriptors.FieldDescriptor> fields;
  
  private final Descriptors.FieldDescriptor[] oneofCases;
  
  private final UnknownFieldSet unknownFields;
  
  private int memoizedSize = -1;
  
  DynamicMessage(Descriptors.Descriptor type, FieldSet<Descriptors.FieldDescriptor> fields, Descriptors.FieldDescriptor[] oneofCases, UnknownFieldSet unknownFields) {
    this.type = type;
    this.fields = fields;
    this.oneofCases = oneofCases;
    this.unknownFields = unknownFields;
  }
  
  public static DynamicMessage getDefaultInstance(Descriptors.Descriptor type) {
    int oneofDeclCount = type.toProto().getOneofDeclCount();
    Descriptors.FieldDescriptor[] oneofCases = new Descriptors.FieldDescriptor[oneofDeclCount];
    return new DynamicMessage(type, 
        
        FieldSet.emptySet(), oneofCases, 
        
        UnknownFieldSet.getDefaultInstance());
  }
  
  public static DynamicMessage parseFrom(Descriptors.Descriptor type, CodedInputStream input) throws IOException {
    return newBuilder(type).mergeFrom(input).buildParsed();
  }
  
  public static DynamicMessage parseFrom(Descriptors.Descriptor type, CodedInputStream input, ExtensionRegistry extensionRegistry) throws IOException {
    return newBuilder(type).mergeFrom(input, extensionRegistry).buildParsed();
  }
  
  public static DynamicMessage parseFrom(Descriptors.Descriptor type, ByteString data) throws InvalidProtocolBufferException {
    return newBuilder(type).mergeFrom(data).buildParsed();
  }
  
  public static DynamicMessage parseFrom(Descriptors.Descriptor type, ByteString data, ExtensionRegistry extensionRegistry) throws InvalidProtocolBufferException {
    return newBuilder(type).mergeFrom(data, extensionRegistry).buildParsed();
  }
  
  public static DynamicMessage parseFrom(Descriptors.Descriptor type, byte[] data) throws InvalidProtocolBufferException {
    return newBuilder(type).mergeFrom(data).buildParsed();
  }
  
  public static DynamicMessage parseFrom(Descriptors.Descriptor type, byte[] data, ExtensionRegistry extensionRegistry) throws InvalidProtocolBufferException {
    return newBuilder(type).mergeFrom(data, extensionRegistry).buildParsed();
  }
  
  public static DynamicMessage parseFrom(Descriptors.Descriptor type, InputStream input) throws IOException {
    return newBuilder(type).mergeFrom(input).buildParsed();
  }
  
  public static DynamicMessage parseFrom(Descriptors.Descriptor type, InputStream input, ExtensionRegistry extensionRegistry) throws IOException {
    return newBuilder(type).mergeFrom(input, extensionRegistry).buildParsed();
  }
  
  public static Builder newBuilder(Descriptors.Descriptor type) {
    return new Builder(type);
  }
  
  public static Builder newBuilder(Message prototype) {
    return (new Builder(prototype.getDescriptorForType())).mergeFrom(prototype);
  }
  
  public Descriptors.Descriptor getDescriptorForType() {
    return this.type;
  }
  
  public DynamicMessage getDefaultInstanceForType() {
    return getDefaultInstance(this.type);
  }
  
  public Map<Descriptors.FieldDescriptor, Object> getAllFields() {
    return this.fields.getAllFields();
  }
  
  public boolean hasOneof(Descriptors.OneofDescriptor oneof) {
    verifyOneofContainingType(oneof);
    Descriptors.FieldDescriptor field = this.oneofCases[oneof.getIndex()];
    if (field == null)
      return false; 
    return true;
  }
  
  public Descriptors.FieldDescriptor getOneofFieldDescriptor(Descriptors.OneofDescriptor oneof) {
    verifyOneofContainingType(oneof);
    return this.oneofCases[oneof.getIndex()];
  }
  
  public boolean hasField(Descriptors.FieldDescriptor field) {
    verifyContainingType(field);
    return this.fields.hasField(field);
  }
  
  public Object getField(Descriptors.FieldDescriptor field) {
    verifyContainingType(field);
    Object<?> result = (Object<?>)this.fields.getField(field);
    if (result == null)
      if (field.isRepeated()) {
        result = Collections.emptyList();
      } else if (field.getJavaType() == Descriptors.FieldDescriptor.JavaType.MESSAGE) {
        result = (Object<?>)getDefaultInstance(field.getMessageType());
      } else {
        result = (Object<?>)field.getDefaultValue();
      }  
    return result;
  }
  
  public int getRepeatedFieldCount(Descriptors.FieldDescriptor field) {
    verifyContainingType(field);
    return this.fields.getRepeatedFieldCount(field);
  }
  
  public Object getRepeatedField(Descriptors.FieldDescriptor field, int index) {
    verifyContainingType(field);
    return this.fields.getRepeatedField(field, index);
  }
  
  public UnknownFieldSet getUnknownFields() {
    return this.unknownFields;
  }
  
  static boolean isInitialized(Descriptors.Descriptor type, FieldSet<Descriptors.FieldDescriptor> fields) {
    for (Descriptors.FieldDescriptor field : type.getFields()) {
      if (field.isRequired() && 
        !fields.hasField(field))
        return false; 
    } 
    return fields.isInitialized();
  }
  
  public boolean isInitialized() {
    return isInitialized(this.type, this.fields);
  }
  
  public void writeTo(CodedOutputStream output) throws IOException {
    if (this.type.getOptions().getMessageSetWireFormat()) {
      this.fields.writeMessageSetTo(output);
      this.unknownFields.writeAsMessageSetTo(output);
    } else {
      this.fields.writeTo(output);
      this.unknownFields.writeTo(output);
    } 
  }
  
  public int getSerializedSize() {
    int size = this.memoizedSize;
    if (size != -1)
      return size; 
    if (this.type.getOptions().getMessageSetWireFormat()) {
      size = this.fields.getMessageSetSerializedSize();
      size += this.unknownFields.getSerializedSizeAsMessageSet();
    } else {
      size = this.fields.getSerializedSize();
      size += this.unknownFields.getSerializedSize();
    } 
    this.memoizedSize = size;
    return size;
  }
  
  public Builder newBuilderForType() {
    return new Builder(this.type);
  }
  
  public Builder toBuilder() {
    return newBuilderForType().mergeFrom(this);
  }
  
  public Parser<DynamicMessage> getParserForType() {
    return new AbstractParser<DynamicMessage>() {
        public DynamicMessage parsePartialFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
          DynamicMessage.Builder builder = DynamicMessage.newBuilder(DynamicMessage.this.type);
          try {
            builder.mergeFrom(input, extensionRegistry);
          } catch (InvalidProtocolBufferException e) {
            throw e.setUnfinishedMessage(builder.buildPartial());
          } catch (IOException e) {
            throw (new InvalidProtocolBufferException(e)).setUnfinishedMessage(builder.buildPartial());
          } 
          return builder.buildPartial();
        }
      };
  }
  
  private void verifyContainingType(Descriptors.FieldDescriptor field) {
    if (field.getContainingType() != this.type)
      throw new IllegalArgumentException("FieldDescriptor does not match message type."); 
  }
  
  private void verifyOneofContainingType(Descriptors.OneofDescriptor oneof) {
    if (oneof.getContainingType() != this.type)
      throw new IllegalArgumentException("OneofDescriptor does not match message type."); 
  }
  
  public static final class Builder extends AbstractMessage.Builder<Builder> {
    private final Descriptors.Descriptor type;
    
    private FieldSet.Builder<Descriptors.FieldDescriptor> fields;
    
    private final Descriptors.FieldDescriptor[] oneofCases;
    
    private UnknownFieldSet unknownFields;
    
    private Builder(Descriptors.Descriptor type) {
      this.type = type;
      this.fields = FieldSet.newBuilder();
      this.unknownFields = UnknownFieldSet.getDefaultInstance();
      this.oneofCases = new Descriptors.FieldDescriptor[type.toProto().getOneofDeclCount()];
    }
    
    public Builder clear() {
      this.fields = FieldSet.newBuilder();
      this.unknownFields = UnknownFieldSet.getDefaultInstance();
      return this;
    }
    
    public Builder mergeFrom(Message other) {
      if (other instanceof DynamicMessage) {
        DynamicMessage otherDynamicMessage = (DynamicMessage)other;
        if (otherDynamicMessage.type != this.type)
          throw new IllegalArgumentException("mergeFrom(Message) can only merge messages of the same type."); 
        this.fields.mergeFrom(otherDynamicMessage.fields);
        mergeUnknownFields(otherDynamicMessage.unknownFields);
        for (int i = 0; i < this.oneofCases.length; i++) {
          if (this.oneofCases[i] == null) {
            this.oneofCases[i] = otherDynamicMessage.oneofCases[i];
          } else if (otherDynamicMessage.oneofCases[i] != null && this.oneofCases[i] != otherDynamicMessage
            .oneofCases[i]) {
            this.fields.clearField(this.oneofCases[i]);
            this.oneofCases[i] = otherDynamicMessage.oneofCases[i];
          } 
        } 
        return this;
      } 
      return super.mergeFrom(other);
    }
    
    public DynamicMessage build() {
      if (!isInitialized())
        throw newUninitializedMessageException(new DynamicMessage(this.type, this.fields
              
              .build(), (Descriptors.FieldDescriptor[])Arrays.copyOf(this.oneofCases, this.oneofCases.length), this.unknownFields)); 
      return buildPartial();
    }
    
    private DynamicMessage buildParsed() throws InvalidProtocolBufferException {
      if (!isInitialized())
        throw newUninitializedMessageException(new DynamicMessage(this.type, this.fields
              
              .build(), 
              (Descriptors.FieldDescriptor[])Arrays.copyOf(this.oneofCases, this.oneofCases.length), this.unknownFields))
          
          .asInvalidProtocolBufferException(); 
      return buildPartial();
    }
    
    public DynamicMessage buildPartial() {
      if (this.type.getOptions().getMapEntry())
        for (Descriptors.FieldDescriptor field : this.type.getFields()) {
          if (field.isOptional() && !this.fields.hasField(field)) {
            if (field.getJavaType() == Descriptors.FieldDescriptor.JavaType.MESSAGE) {
              this.fields.setField(field, DynamicMessage.getDefaultInstance(field.getMessageType()));
              continue;
            } 
            this.fields.setField(field, field.getDefaultValue());
          } 
        }  
      DynamicMessage result = new DynamicMessage(this.type, this.fields.buildPartial(), Arrays.<Descriptors.FieldDescriptor>copyOf(this.oneofCases, this.oneofCases.length), this.unknownFields);
      return result;
    }
    
    public Builder clone() {
      Builder result = new Builder(this.type);
      result.fields.mergeFrom(this.fields.build());
      result.mergeUnknownFields(this.unknownFields);
      System.arraycopy(this.oneofCases, 0, result.oneofCases, 0, this.oneofCases.length);
      return result;
    }
    
    public boolean isInitialized() {
      for (Descriptors.FieldDescriptor field : this.type.getFields()) {
        if (field.isRequired() && 
          !this.fields.hasField(field))
          return false; 
      } 
      return this.fields.isInitialized();
    }
    
    public Descriptors.Descriptor getDescriptorForType() {
      return this.type;
    }
    
    public DynamicMessage getDefaultInstanceForType() {
      return DynamicMessage.getDefaultInstance(this.type);
    }
    
    public Map<Descriptors.FieldDescriptor, Object> getAllFields() {
      return this.fields.getAllFields();
    }
    
    public Builder newBuilderForField(Descriptors.FieldDescriptor field) {
      verifyContainingType(field);
      if (field.getJavaType() != Descriptors.FieldDescriptor.JavaType.MESSAGE)
        throw new IllegalArgumentException("newBuilderForField is only valid for fields with message type."); 
      return new Builder(field.getMessageType());
    }
    
    public boolean hasOneof(Descriptors.OneofDescriptor oneof) {
      verifyOneofContainingType(oneof);
      Descriptors.FieldDescriptor field = this.oneofCases[oneof.getIndex()];
      if (field == null)
        return false; 
      return true;
    }
    
    public Descriptors.FieldDescriptor getOneofFieldDescriptor(Descriptors.OneofDescriptor oneof) {
      verifyOneofContainingType(oneof);
      return this.oneofCases[oneof.getIndex()];
    }
    
    public Builder clearOneof(Descriptors.OneofDescriptor oneof) {
      verifyOneofContainingType(oneof);
      Descriptors.FieldDescriptor field = this.oneofCases[oneof.getIndex()];
      if (field != null)
        clearField(field); 
      return this;
    }
    
    public boolean hasField(Descriptors.FieldDescriptor field) {
      verifyContainingType(field);
      return this.fields.hasField(field);
    }
    
    public Object getField(Descriptors.FieldDescriptor field) {
      verifyContainingType(field);
      Object<?> result = (Object<?>)this.fields.getField(field);
      if (result == null)
        if (field.isRepeated()) {
          result = Collections.emptyList();
        } else if (field.getJavaType() == Descriptors.FieldDescriptor.JavaType.MESSAGE) {
          result = (Object<?>)DynamicMessage.getDefaultInstance(field.getMessageType());
        } else {
          result = (Object<?>)field.getDefaultValue();
        }  
      return result;
    }
    
    public Builder setField(Descriptors.FieldDescriptor field, Object value) {
      verifyContainingType(field);
      verifyType(field, value);
      Descriptors.OneofDescriptor oneofDescriptor = field.getContainingOneof();
      if (oneofDescriptor != null) {
        int index = oneofDescriptor.getIndex();
        Descriptors.FieldDescriptor oldField = this.oneofCases[index];
        if (oldField != null && oldField != field)
          this.fields.clearField(oldField); 
        this.oneofCases[index] = field;
      } else if (field.getFile().getSyntax() == Descriptors.FileDescriptor.Syntax.PROTO3 && 
        !field.isRepeated() && field
        .getJavaType() != Descriptors.FieldDescriptor.JavaType.MESSAGE && value
        .equals(field.getDefaultValue())) {
        this.fields.clearField(field);
        return this;
      } 
      this.fields.setField(field, value);
      return this;
    }
    
    public Builder clearField(Descriptors.FieldDescriptor field) {
      verifyContainingType(field);
      Descriptors.OneofDescriptor oneofDescriptor = field.getContainingOneof();
      if (oneofDescriptor != null) {
        int index = oneofDescriptor.getIndex();
        if (this.oneofCases[index] == field)
          this.oneofCases[index] = null; 
      } 
      this.fields.clearField(field);
      return this;
    }
    
    public int getRepeatedFieldCount(Descriptors.FieldDescriptor field) {
      verifyContainingType(field);
      return this.fields.getRepeatedFieldCount(field);
    }
    
    public Object getRepeatedField(Descriptors.FieldDescriptor field, int index) {
      verifyContainingType(field);
      return this.fields.getRepeatedField(field, index);
    }
    
    public Builder setRepeatedField(Descriptors.FieldDescriptor field, int index, Object value) {
      verifyContainingType(field);
      verifySingularValueType(field, value);
      this.fields.setRepeatedField(field, index, value);
      return this;
    }
    
    public Builder addRepeatedField(Descriptors.FieldDescriptor field, Object value) {
      verifyContainingType(field);
      verifySingularValueType(field, value);
      this.fields.addRepeatedField(field, value);
      return this;
    }
    
    public UnknownFieldSet getUnknownFields() {
      return this.unknownFields;
    }
    
    public Builder setUnknownFields(UnknownFieldSet unknownFields) {
      this.unknownFields = unknownFields;
      return this;
    }
    
    public Builder mergeUnknownFields(UnknownFieldSet unknownFields) {
      this
        .unknownFields = UnknownFieldSet.newBuilder(this.unknownFields).mergeFrom(unknownFields).build();
      return this;
    }
    
    private void verifyContainingType(Descriptors.FieldDescriptor field) {
      if (field.getContainingType() != this.type)
        throw new IllegalArgumentException("FieldDescriptor does not match message type."); 
    }
    
    private void verifyOneofContainingType(Descriptors.OneofDescriptor oneof) {
      if (oneof.getContainingType() != this.type)
        throw new IllegalArgumentException("OneofDescriptor does not match message type."); 
    }
    
    private void verifySingularValueType(Descriptors.FieldDescriptor field, Object value) {
      switch (field.getType()) {
        case ENUM:
          Internal.checkNotNull(value);
          if (!(value instanceof Descriptors.EnumValueDescriptor))
            throw new IllegalArgumentException("DynamicMessage should use EnumValueDescriptor to set Enum Value."); 
          break;
        case MESSAGE:
          if (value instanceof Message.Builder)
            throw new IllegalArgumentException(
                String.format("Wrong object type used with protocol message reflection.\nField number: %d, field java type: %s, value type: %s\n", new Object[] { Integer.valueOf(field.getNumber()), field
                    .getLiteType().getJavaType(), value
                    .getClass().getName() })); 
          break;
      } 
    }
    
    private void verifyType(Descriptors.FieldDescriptor field, Object value) {
      if (field.isRepeated()) {
        for (Object item : value)
          verifySingularValueType(field, item); 
      } else {
        verifySingularValueType(field, value);
      } 
    }
    
    public Message.Builder getFieldBuilder(Descriptors.FieldDescriptor field) {
      verifyContainingType(field);
      if (field.isMapField())
        throw new UnsupportedOperationException("Nested builder not supported for map fields."); 
      if (field.getJavaType() != Descriptors.FieldDescriptor.JavaType.MESSAGE)
        throw new UnsupportedOperationException("getFieldBuilder() called on a non-Message type."); 
      Object existingValue = this.fields.getFieldAllowBuilders(field);
      Message.Builder builder = (existingValue == null) ? new Builder(field.getMessageType()) : toMessageBuilder(existingValue);
      this.fields.setField(field, builder);
      return builder;
    }
    
    public Message.Builder getRepeatedFieldBuilder(Descriptors.FieldDescriptor field, int index) {
      verifyContainingType(field);
      if (field.isMapField())
        throw new UnsupportedOperationException("Map fields cannot be repeated"); 
      if (field.getJavaType() != Descriptors.FieldDescriptor.JavaType.MESSAGE)
        throw new UnsupportedOperationException("getRepeatedFieldBuilder() called on a non-Message type."); 
      Message.Builder builder = toMessageBuilder(this.fields.getRepeatedFieldAllowBuilders(field, index));
      this.fields.setRepeatedField(field, index, builder);
      return builder;
    }
    
    private static Message.Builder toMessageBuilder(Object o) {
      if (o instanceof Message.Builder)
        return (Message.Builder)o; 
      if (o instanceof LazyField)
        o = ((LazyField)o).getValue(); 
      if (o instanceof Message)
        return ((Message)o).toBuilder(); 
      throw new IllegalArgumentException(
          String.format("Cannot convert %s to Message.Builder", new Object[] { o.getClass() }));
    }
  }
}
