package com.google.protobuf;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

class MessageReflection {
  static void writeMessageTo(Message message, Map<Descriptors.FieldDescriptor, Object> fields, CodedOutputStream output, boolean alwaysWriteRequiredFields) throws IOException {
    boolean isMessageSet = message.getDescriptorForType().getOptions().getMessageSetWireFormat();
    if (alwaysWriteRequiredFields) {
      fields = new TreeMap<>(fields);
      for (Descriptors.FieldDescriptor field : message.getDescriptorForType().getFields()) {
        if (field.isRequired() && !fields.containsKey(field))
          fields.put(field, message.getField(field)); 
      } 
    } 
    for (Map.Entry<Descriptors.FieldDescriptor, Object> entry : fields.entrySet()) {
      Descriptors.FieldDescriptor field = entry.getKey();
      Object value = entry.getValue();
      if (isMessageSet && field
        .isExtension() && field
        .getType() == Descriptors.FieldDescriptor.Type.MESSAGE && 
        !field.isRepeated()) {
        output.writeMessageSetExtension(field.getNumber(), (Message)value);
        continue;
      } 
      FieldSet.writeField(field, value, output);
    } 
    UnknownFieldSet unknownFields = message.getUnknownFields();
    if (isMessageSet) {
      unknownFields.writeAsMessageSetTo(output);
    } else {
      unknownFields.writeTo(output);
    } 
  }
  
  static int getSerializedSize(Message message, Map<Descriptors.FieldDescriptor, Object> fields) {
    int size = 0;
    boolean isMessageSet = message.getDescriptorForType().getOptions().getMessageSetWireFormat();
    for (Map.Entry<Descriptors.FieldDescriptor, Object> entry : fields.entrySet()) {
      Descriptors.FieldDescriptor field = entry.getKey();
      Object value = entry.getValue();
      if (isMessageSet && field
        .isExtension() && field
        .getType() == Descriptors.FieldDescriptor.Type.MESSAGE && 
        !field.isRepeated()) {
        size += 
          CodedOutputStream.computeMessageSetExtensionSize(field.getNumber(), (Message)value);
        continue;
      } 
      size += FieldSet.computeFieldSize(field, value);
    } 
    UnknownFieldSet unknownFields = message.getUnknownFields();
    if (isMessageSet) {
      size += unknownFields.getSerializedSizeAsMessageSet();
    } else {
      size += unknownFields.getSerializedSize();
    } 
    return size;
  }
  
  static String delimitWithCommas(List<String> parts) {
    StringBuilder result = new StringBuilder();
    for (String part : parts) {
      if (result.length() > 0)
        result.append(", "); 
      result.append(part);
    } 
    return result.toString();
  }
  
  static boolean isInitialized(MessageOrBuilder message) {
    for (Descriptors.FieldDescriptor field : message.getDescriptorForType().getFields()) {
      if (field.isRequired() && 
        !message.hasField(field))
        return false; 
    } 
    for (Map.Entry<Descriptors.FieldDescriptor, Object> entry : message.getAllFields().entrySet()) {
      Descriptors.FieldDescriptor field = entry.getKey();
      if (field.getJavaType() == Descriptors.FieldDescriptor.JavaType.MESSAGE) {
        if (field.isRepeated()) {
          for (Message element : entry.getValue()) {
            if (!element.isInitialized())
              return false; 
          } 
          continue;
        } 
        if (!((Message)entry.getValue()).isInitialized())
          return false; 
      } 
    } 
    return true;
  }
  
  private static String subMessagePrefix(String prefix, Descriptors.FieldDescriptor field, int index) {
    StringBuilder result = new StringBuilder(prefix);
    if (field.isExtension()) {
      result.append('(').append(field.getFullName()).append(')');
    } else {
      result.append(field.getName());
    } 
    if (index != -1)
      result.append('[').append(index).append(']'); 
    result.append('.');
    return result.toString();
  }
  
  private static void findMissingFields(MessageOrBuilder message, String prefix, List<String> results) {
    for (Descriptors.FieldDescriptor field : message.getDescriptorForType().getFields()) {
      if (field.isRequired() && !message.hasField(field))
        results.add(prefix + field.getName()); 
    } 
    for (Map.Entry<Descriptors.FieldDescriptor, Object> entry : message.getAllFields().entrySet()) {
      Descriptors.FieldDescriptor field = entry.getKey();
      Object value = entry.getValue();
      if (field.getJavaType() == Descriptors.FieldDescriptor.JavaType.MESSAGE) {
        if (field.isRepeated()) {
          int i = 0;
          for (Object element : value)
            findMissingFields((MessageOrBuilder)element, 
                subMessagePrefix(prefix, field, i++), results); 
          continue;
        } 
        if (message.hasField(field))
          findMissingFields((MessageOrBuilder)value, 
              subMessagePrefix(prefix, field, -1), results); 
      } 
    } 
  }
  
  static List<String> findMissingFields(MessageOrBuilder message) {
    List<String> results = new ArrayList<>();
    findMissingFields(message, "", results);
    return results;
  }
  
  public enum ContainerType {
    MESSAGE, EXTENSION_SET;
  }
  
  static interface MergeTarget {
    Descriptors.Descriptor getDescriptorForType();
    
    ContainerType getContainerType();
    
    ExtensionRegistry.ExtensionInfo findExtensionByName(ExtensionRegistry param1ExtensionRegistry, String param1String);
    
    ExtensionRegistry.ExtensionInfo findExtensionByNumber(ExtensionRegistry param1ExtensionRegistry, Descriptors.Descriptor param1Descriptor, int param1Int);
    
    Object getField(Descriptors.FieldDescriptor param1FieldDescriptor);
    
    boolean hasField(Descriptors.FieldDescriptor param1FieldDescriptor);
    
    MergeTarget setField(Descriptors.FieldDescriptor param1FieldDescriptor, Object param1Object);
    
    MergeTarget clearField(Descriptors.FieldDescriptor param1FieldDescriptor);
    
    MergeTarget setRepeatedField(Descriptors.FieldDescriptor param1FieldDescriptor, int param1Int, Object param1Object);
    
    MergeTarget addRepeatedField(Descriptors.FieldDescriptor param1FieldDescriptor, Object param1Object);
    
    boolean hasOneof(Descriptors.OneofDescriptor param1OneofDescriptor);
    
    MergeTarget clearOneof(Descriptors.OneofDescriptor param1OneofDescriptor);
    
    Descriptors.FieldDescriptor getOneofFieldDescriptor(Descriptors.OneofDescriptor param1OneofDescriptor);
    
    Object parseGroup(CodedInputStream param1CodedInputStream, ExtensionRegistryLite param1ExtensionRegistryLite, Descriptors.FieldDescriptor param1FieldDescriptor, Message param1Message) throws IOException;
    
    Object parseMessage(CodedInputStream param1CodedInputStream, ExtensionRegistryLite param1ExtensionRegistryLite, Descriptors.FieldDescriptor param1FieldDescriptor, Message param1Message) throws IOException;
    
    Object parseMessageFromBytes(ByteString param1ByteString, ExtensionRegistryLite param1ExtensionRegistryLite, Descriptors.FieldDescriptor param1FieldDescriptor, Message param1Message) throws IOException;
    
    void mergeGroup(CodedInputStream param1CodedInputStream, ExtensionRegistryLite param1ExtensionRegistryLite, Descriptors.FieldDescriptor param1FieldDescriptor, Message param1Message) throws IOException;
    
    void mergeMessage(CodedInputStream param1CodedInputStream, ExtensionRegistryLite param1ExtensionRegistryLite, Descriptors.FieldDescriptor param1FieldDescriptor, Message param1Message) throws IOException;
    
    WireFormat.Utf8Validation getUtf8Validation(Descriptors.FieldDescriptor param1FieldDescriptor);
    
    MergeTarget newMergeTargetForField(Descriptors.FieldDescriptor param1FieldDescriptor, Message param1Message);
    
    MergeTarget newEmptyTargetForField(Descriptors.FieldDescriptor param1FieldDescriptor, Message param1Message);
    
    Object finish();
    
    public enum ContainerType {
      MESSAGE, EXTENSION_SET;
    }
  }
  
  static class BuilderAdapter implements MergeTarget {
    private final Message.Builder builder;
    
    private boolean hasNestedBuilders = true;
    
    public Descriptors.Descriptor getDescriptorForType() {
      return this.builder.getDescriptorForType();
    }
    
    public BuilderAdapter(Message.Builder builder) {
      this.builder = builder;
    }
    
    public Object getField(Descriptors.FieldDescriptor field) {
      return this.builder.getField(field);
    }
    
    private Message.Builder getFieldBuilder(Descriptors.FieldDescriptor field) {
      if (this.hasNestedBuilders)
        try {
          return this.builder.getFieldBuilder(field);
        } catch (UnsupportedOperationException e) {
          this.hasNestedBuilders = false;
        }  
      return null;
    }
    
    public boolean hasField(Descriptors.FieldDescriptor field) {
      return this.builder.hasField(field);
    }
    
    public MessageReflection.MergeTarget setField(Descriptors.FieldDescriptor field, Object value) {
      if (!field.isRepeated() && value instanceof MessageLite.Builder) {
        if (value != getFieldBuilder(field))
          this.builder.setField(field, ((MessageLite.Builder)value).buildPartial()); 
        return this;
      } 
      this.builder.setField(field, value);
      return this;
    }
    
    public MessageReflection.MergeTarget clearField(Descriptors.FieldDescriptor field) {
      this.builder.clearField(field);
      return this;
    }
    
    public MessageReflection.MergeTarget setRepeatedField(Descriptors.FieldDescriptor field, int index, Object value) {
      if (value instanceof MessageLite.Builder)
        value = ((MessageLite.Builder)value).buildPartial(); 
      this.builder.setRepeatedField(field, index, value);
      return this;
    }
    
    public MessageReflection.MergeTarget addRepeatedField(Descriptors.FieldDescriptor field, Object value) {
      if (value instanceof MessageLite.Builder)
        value = ((MessageLite.Builder)value).buildPartial(); 
      this.builder.addRepeatedField(field, value);
      return this;
    }
    
    public boolean hasOneof(Descriptors.OneofDescriptor oneof) {
      return this.builder.hasOneof(oneof);
    }
    
    public MessageReflection.MergeTarget clearOneof(Descriptors.OneofDescriptor oneof) {
      this.builder.clearOneof(oneof);
      return this;
    }
    
    public Descriptors.FieldDescriptor getOneofFieldDescriptor(Descriptors.OneofDescriptor oneof) {
      return this.builder.getOneofFieldDescriptor(oneof);
    }
    
    public MessageReflection.MergeTarget.ContainerType getContainerType() {
      return MessageReflection.MergeTarget.ContainerType.MESSAGE;
    }
    
    public ExtensionRegistry.ExtensionInfo findExtensionByName(ExtensionRegistry registry, String name) {
      return registry.findImmutableExtensionByName(name);
    }
    
    public ExtensionRegistry.ExtensionInfo findExtensionByNumber(ExtensionRegistry registry, Descriptors.Descriptor containingType, int fieldNumber) {
      return registry.findImmutableExtensionByNumber(containingType, fieldNumber);
    }
    
    public Object parseGroup(CodedInputStream input, ExtensionRegistryLite extensionRegistry, Descriptors.FieldDescriptor field, Message defaultInstance) throws IOException {
      Message.Builder subBuilder;
      if (defaultInstance != null) {
        subBuilder = defaultInstance.newBuilderForType();
      } else {
        subBuilder = this.builder.newBuilderForField(field);
      } 
      if (!field.isRepeated()) {
        Message originalMessage = (Message)getField(field);
        if (originalMessage != null)
          subBuilder.mergeFrom(originalMessage); 
      } 
      input.readGroup(field.getNumber(), subBuilder, extensionRegistry);
      return subBuilder.buildPartial();
    }
    
    public Object parseMessage(CodedInputStream input, ExtensionRegistryLite extensionRegistry, Descriptors.FieldDescriptor field, Message defaultInstance) throws IOException {
      Message.Builder subBuilder;
      if (defaultInstance != null) {
        subBuilder = defaultInstance.newBuilderForType();
      } else {
        subBuilder = this.builder.newBuilderForField(field);
      } 
      if (!field.isRepeated()) {
        Message originalMessage = (Message)getField(field);
        if (originalMessage != null)
          subBuilder.mergeFrom(originalMessage); 
      } 
      input.readMessage(subBuilder, extensionRegistry);
      return subBuilder.buildPartial();
    }
    
    public Object parseMessageFromBytes(ByteString bytes, ExtensionRegistryLite extensionRegistry, Descriptors.FieldDescriptor field, Message defaultInstance) throws IOException {
      Message.Builder subBuilder;
      if (defaultInstance != null) {
        subBuilder = defaultInstance.newBuilderForType();
      } else {
        subBuilder = this.builder.newBuilderForField(field);
      } 
      if (!field.isRepeated()) {
        Message originalMessage = (Message)getField(field);
        if (originalMessage != null)
          subBuilder.mergeFrom(originalMessage); 
      } 
      subBuilder.mergeFrom(bytes, extensionRegistry);
      return subBuilder.buildPartial();
    }
    
    public void mergeGroup(CodedInputStream input, ExtensionRegistryLite extensionRegistry, Descriptors.FieldDescriptor field, Message defaultInstance) throws IOException {
      if (!field.isRepeated()) {
        Message.Builder subBuilder;
        if (hasField(field)) {
          subBuilder = getFieldBuilder(field);
          if (subBuilder != null) {
            input.readGroup(field.getNumber(), subBuilder, extensionRegistry);
            return;
          } 
          subBuilder = newMessageFieldInstance(field, defaultInstance);
          subBuilder.mergeFrom((Message)getField(field));
        } else {
          subBuilder = newMessageFieldInstance(field, defaultInstance);
        } 
        input.readGroup(field.getNumber(), subBuilder, extensionRegistry);
        MessageReflection.MergeTarget mergeTarget = setField(field, subBuilder.buildPartial());
      } else {
        Message.Builder subBuilder = newMessageFieldInstance(field, defaultInstance);
        input.readGroup(field.getNumber(), subBuilder, extensionRegistry);
        MessageReflection.MergeTarget mergeTarget = addRepeatedField(field, subBuilder.buildPartial());
      } 
    }
    
    public void mergeMessage(CodedInputStream input, ExtensionRegistryLite extensionRegistry, Descriptors.FieldDescriptor field, Message defaultInstance) throws IOException {
      if (!field.isRepeated()) {
        Message.Builder subBuilder;
        if (hasField(field)) {
          subBuilder = getFieldBuilder(field);
          if (subBuilder != null) {
            input.readMessage(subBuilder, extensionRegistry);
            return;
          } 
          subBuilder = newMessageFieldInstance(field, defaultInstance);
          subBuilder.mergeFrom((Message)getField(field));
        } else {
          subBuilder = newMessageFieldInstance(field, defaultInstance);
        } 
        input.readMessage(subBuilder, extensionRegistry);
        MessageReflection.MergeTarget mergeTarget = setField(field, subBuilder.buildPartial());
      } else {
        Message.Builder subBuilder = newMessageFieldInstance(field, defaultInstance);
        input.readMessage(subBuilder, extensionRegistry);
        MessageReflection.MergeTarget mergeTarget = addRepeatedField(field, subBuilder.buildPartial());
      } 
    }
    
    private Message.Builder newMessageFieldInstance(Descriptors.FieldDescriptor field, Message defaultInstance) {
      if (defaultInstance != null)
        return defaultInstance.newBuilderForType(); 
      return this.builder.newBuilderForField(field);
    }
    
    public MessageReflection.MergeTarget newMergeTargetForField(Descriptors.FieldDescriptor field, Message defaultInstance) {
      if (!field.isRepeated() && hasField(field)) {
        Message.Builder builder = getFieldBuilder(field);
        if (builder != null)
          return new BuilderAdapter(builder); 
      } 
      Message.Builder subBuilder = newMessageFieldInstance(field, defaultInstance);
      if (!field.isRepeated()) {
        Message originalMessage = (Message)getField(field);
        if (originalMessage != null)
          subBuilder.mergeFrom(originalMessage); 
      } 
      return new BuilderAdapter(subBuilder);
    }
    
    public MessageReflection.MergeTarget newEmptyTargetForField(Descriptors.FieldDescriptor field, Message defaultInstance) {
      Message.Builder subBuilder;
      if (defaultInstance != null) {
        subBuilder = defaultInstance.newBuilderForType();
      } else {
        subBuilder = this.builder.newBuilderForField(field);
      } 
      return new BuilderAdapter(subBuilder);
    }
    
    public WireFormat.Utf8Validation getUtf8Validation(Descriptors.FieldDescriptor descriptor) {
      if (descriptor.needsUtf8Check())
        return WireFormat.Utf8Validation.STRICT; 
      if (!descriptor.isRepeated() && this.builder instanceof GeneratedMessage.Builder)
        return WireFormat.Utf8Validation.LAZY; 
      return WireFormat.Utf8Validation.LOOSE;
    }
    
    public Object finish() {
      return this.builder;
    }
  }
  
  static class ExtensionAdapter implements MergeTarget {
    private final FieldSet<Descriptors.FieldDescriptor> extensions;
    
    ExtensionAdapter(FieldSet<Descriptors.FieldDescriptor> extensions) {
      this.extensions = extensions;
    }
    
    public Descriptors.Descriptor getDescriptorForType() {
      throw new UnsupportedOperationException("getDescriptorForType() called on FieldSet object");
    }
    
    public Object getField(Descriptors.FieldDescriptor field) {
      return this.extensions.getField(field);
    }
    
    public boolean hasField(Descriptors.FieldDescriptor field) {
      return this.extensions.hasField(field);
    }
    
    public MessageReflection.MergeTarget setField(Descriptors.FieldDescriptor field, Object value) {
      this.extensions.setField(field, value);
      return this;
    }
    
    public MessageReflection.MergeTarget clearField(Descriptors.FieldDescriptor field) {
      this.extensions.clearField(field);
      return this;
    }
    
    public MessageReflection.MergeTarget setRepeatedField(Descriptors.FieldDescriptor field, int index, Object value) {
      this.extensions.setRepeatedField(field, index, value);
      return this;
    }
    
    public MessageReflection.MergeTarget addRepeatedField(Descriptors.FieldDescriptor field, Object value) {
      this.extensions.addRepeatedField(field, value);
      return this;
    }
    
    public boolean hasOneof(Descriptors.OneofDescriptor oneof) {
      return false;
    }
    
    public MessageReflection.MergeTarget clearOneof(Descriptors.OneofDescriptor oneof) {
      return this;
    }
    
    public Descriptors.FieldDescriptor getOneofFieldDescriptor(Descriptors.OneofDescriptor oneof) {
      return null;
    }
    
    public MessageReflection.MergeTarget.ContainerType getContainerType() {
      return MessageReflection.MergeTarget.ContainerType.EXTENSION_SET;
    }
    
    public ExtensionRegistry.ExtensionInfo findExtensionByName(ExtensionRegistry registry, String name) {
      return registry.findImmutableExtensionByName(name);
    }
    
    public ExtensionRegistry.ExtensionInfo findExtensionByNumber(ExtensionRegistry registry, Descriptors.Descriptor containingType, int fieldNumber) {
      return registry.findImmutableExtensionByNumber(containingType, fieldNumber);
    }
    
    public Object parseGroup(CodedInputStream input, ExtensionRegistryLite registry, Descriptors.FieldDescriptor field, Message defaultInstance) throws IOException {
      Message.Builder subBuilder = defaultInstance.newBuilderForType();
      if (!field.isRepeated()) {
        Message originalMessage = (Message)getField(field);
        if (originalMessage != null)
          subBuilder.mergeFrom(originalMessage); 
      } 
      input.readGroup(field.getNumber(), subBuilder, registry);
      return subBuilder.buildPartial();
    }
    
    public Object parseMessage(CodedInputStream input, ExtensionRegistryLite registry, Descriptors.FieldDescriptor field, Message defaultInstance) throws IOException {
      Message.Builder subBuilder = defaultInstance.newBuilderForType();
      if (!field.isRepeated()) {
        Message originalMessage = (Message)getField(field);
        if (originalMessage != null)
          subBuilder.mergeFrom(originalMessage); 
      } 
      input.readMessage(subBuilder, registry);
      return subBuilder.buildPartial();
    }
    
    public void mergeGroup(CodedInputStream input, ExtensionRegistryLite extensionRegistry, Descriptors.FieldDescriptor field, Message defaultInstance) throws IOException {
      if (!field.isRepeated()) {
        if (hasField(field)) {
          MessageLite.Builder current = ((MessageLite)getField(field)).toBuilder();
          input.readGroup(field.getNumber(), current, extensionRegistry);
          Object unused = setField(field, current.buildPartial());
          return;
        } 
        Message.Builder subBuilder = defaultInstance.newBuilderForType();
        input.readGroup(field.getNumber(), subBuilder, extensionRegistry);
        MessageReflection.MergeTarget mergeTarget = setField(field, subBuilder.buildPartial());
      } else {
        Message.Builder subBuilder = defaultInstance.newBuilderForType();
        input.readGroup(field.getNumber(), subBuilder, extensionRegistry);
        MessageReflection.MergeTarget mergeTarget = addRepeatedField(field, subBuilder.buildPartial());
      } 
    }
    
    public void mergeMessage(CodedInputStream input, ExtensionRegistryLite extensionRegistry, Descriptors.FieldDescriptor field, Message defaultInstance) throws IOException {
      if (!field.isRepeated()) {
        if (hasField(field)) {
          MessageLite.Builder current = ((MessageLite)getField(field)).toBuilder();
          input.readMessage(current, extensionRegistry);
          Object unused = setField(field, current.buildPartial());
          return;
        } 
        Message.Builder subBuilder = defaultInstance.newBuilderForType();
        input.readMessage(subBuilder, extensionRegistry);
        MessageReflection.MergeTarget mergeTarget = setField(field, subBuilder.buildPartial());
      } else {
        Message.Builder subBuilder = defaultInstance.newBuilderForType();
        input.readMessage(subBuilder, extensionRegistry);
        MessageReflection.MergeTarget mergeTarget = addRepeatedField(field, subBuilder.buildPartial());
      } 
    }
    
    public Object parseMessageFromBytes(ByteString bytes, ExtensionRegistryLite registry, Descriptors.FieldDescriptor field, Message defaultInstance) throws IOException {
      Message.Builder subBuilder = defaultInstance.newBuilderForType();
      if (!field.isRepeated()) {
        Message originalMessage = (Message)getField(field);
        if (originalMessage != null)
          subBuilder.mergeFrom(originalMessage); 
      } 
      subBuilder.mergeFrom(bytes, registry);
      return subBuilder.buildPartial();
    }
    
    public MessageReflection.MergeTarget newMergeTargetForField(Descriptors.FieldDescriptor descriptor, Message defaultInstance) {
      throw new UnsupportedOperationException("newMergeTargetForField() called on FieldSet object");
    }
    
    public MessageReflection.MergeTarget newEmptyTargetForField(Descriptors.FieldDescriptor descriptor, Message defaultInstance) {
      throw new UnsupportedOperationException("newEmptyTargetForField() called on FieldSet object");
    }
    
    public WireFormat.Utf8Validation getUtf8Validation(Descriptors.FieldDescriptor descriptor) {
      if (descriptor.needsUtf8Check())
        return WireFormat.Utf8Validation.STRICT; 
      return WireFormat.Utf8Validation.LOOSE;
    }
    
    public Object finish() {
      throw new UnsupportedOperationException("finish() called on FieldSet object");
    }
  }
  
  static class ExtensionBuilderAdapter implements MergeTarget {
    private final FieldSet.Builder<Descriptors.FieldDescriptor> extensions;
    
    ExtensionBuilderAdapter(FieldSet.Builder<Descriptors.FieldDescriptor> extensions) {
      this.extensions = extensions;
    }
    
    public Descriptors.Descriptor getDescriptorForType() {
      throw new UnsupportedOperationException("getDescriptorForType() called on FieldSet object");
    }
    
    public Object getField(Descriptors.FieldDescriptor field) {
      return this.extensions.getField(field);
    }
    
    public boolean hasField(Descriptors.FieldDescriptor field) {
      return this.extensions.hasField(field);
    }
    
    @CanIgnoreReturnValue
    public MessageReflection.MergeTarget setField(Descriptors.FieldDescriptor field, Object value) {
      this.extensions.setField(field, value);
      return this;
    }
    
    @CanIgnoreReturnValue
    public MessageReflection.MergeTarget clearField(Descriptors.FieldDescriptor field) {
      this.extensions.clearField(field);
      return this;
    }
    
    @CanIgnoreReturnValue
    public MessageReflection.MergeTarget setRepeatedField(Descriptors.FieldDescriptor field, int index, Object value) {
      this.extensions.setRepeatedField(field, index, value);
      return this;
    }
    
    @CanIgnoreReturnValue
    public MessageReflection.MergeTarget addRepeatedField(Descriptors.FieldDescriptor field, Object value) {
      this.extensions.addRepeatedField(field, value);
      return this;
    }
    
    public boolean hasOneof(Descriptors.OneofDescriptor oneof) {
      return false;
    }
    
    @CanIgnoreReturnValue
    public MessageReflection.MergeTarget clearOneof(Descriptors.OneofDescriptor oneof) {
      return this;
    }
    
    public Descriptors.FieldDescriptor getOneofFieldDescriptor(Descriptors.OneofDescriptor oneof) {
      return null;
    }
    
    public MessageReflection.MergeTarget.ContainerType getContainerType() {
      return MessageReflection.MergeTarget.ContainerType.EXTENSION_SET;
    }
    
    public ExtensionRegistry.ExtensionInfo findExtensionByName(ExtensionRegistry registry, String name) {
      return registry.findImmutableExtensionByName(name);
    }
    
    public ExtensionRegistry.ExtensionInfo findExtensionByNumber(ExtensionRegistry registry, Descriptors.Descriptor containingType, int fieldNumber) {
      return registry.findImmutableExtensionByNumber(containingType, fieldNumber);
    }
    
    public Object parseGroup(CodedInputStream input, ExtensionRegistryLite registry, Descriptors.FieldDescriptor field, Message defaultInstance) throws IOException {
      Message.Builder subBuilder = defaultInstance.newBuilderForType();
      if (!field.isRepeated()) {
        Message originalMessage = (Message)getField(field);
        if (originalMessage != null)
          subBuilder.mergeFrom(originalMessage); 
      } 
      input.readGroup(field.getNumber(), subBuilder, registry);
      return subBuilder.buildPartial();
    }
    
    public Object parseMessage(CodedInputStream input, ExtensionRegistryLite registry, Descriptors.FieldDescriptor field, Message defaultInstance) throws IOException {
      Message.Builder subBuilder = defaultInstance.newBuilderForType();
      if (!field.isRepeated()) {
        Message originalMessage = (Message)getField(field);
        if (originalMessage != null)
          subBuilder.mergeFrom(originalMessage); 
      } 
      input.readMessage(subBuilder, registry);
      return subBuilder.buildPartial();
    }
    
    public void mergeGroup(CodedInputStream input, ExtensionRegistryLite extensionRegistry, Descriptors.FieldDescriptor field, Message defaultInstance) throws IOException {
      if (!field.isRepeated()) {
        if (hasField(field)) {
          MessageLite.Builder builder;
          Object fieldOrBuilder = this.extensions.getFieldAllowBuilders(field);
          if (fieldOrBuilder instanceof MessageLite.Builder) {
            builder = (MessageLite.Builder)fieldOrBuilder;
          } else {
            builder = ((MessageLite)fieldOrBuilder).toBuilder();
            this.extensions.setField(field, builder);
          } 
          input.readGroup(field.getNumber(), builder, extensionRegistry);
          return;
        } 
        Message.Builder subBuilder = defaultInstance.newBuilderForType();
        input.readGroup(field.getNumber(), subBuilder, extensionRegistry);
        MessageReflection.MergeTarget mergeTarget = setField(field, subBuilder);
      } else {
        Message.Builder subBuilder = defaultInstance.newBuilderForType();
        input.readGroup(field.getNumber(), subBuilder, extensionRegistry);
        MessageReflection.MergeTarget mergeTarget = addRepeatedField(field, subBuilder.buildPartial());
      } 
    }
    
    public void mergeMessage(CodedInputStream input, ExtensionRegistryLite extensionRegistry, Descriptors.FieldDescriptor field, Message defaultInstance) throws IOException {
      if (!field.isRepeated()) {
        if (hasField(field)) {
          MessageLite.Builder builder;
          Object fieldOrBuilder = this.extensions.getFieldAllowBuilders(field);
          if (fieldOrBuilder instanceof MessageLite.Builder) {
            builder = (MessageLite.Builder)fieldOrBuilder;
          } else {
            builder = ((MessageLite)fieldOrBuilder).toBuilder();
            this.extensions.setField(field, builder);
          } 
          input.readMessage(builder, extensionRegistry);
          return;
        } 
        Message.Builder subBuilder = defaultInstance.newBuilderForType();
        input.readMessage(subBuilder, extensionRegistry);
        MessageReflection.MergeTarget mergeTarget = setField(field, subBuilder);
      } else {
        Message.Builder subBuilder = defaultInstance.newBuilderForType();
        input.readMessage(subBuilder, extensionRegistry);
        MessageReflection.MergeTarget mergeTarget = addRepeatedField(field, subBuilder.buildPartial());
      } 
    }
    
    public Object parseMessageFromBytes(ByteString bytes, ExtensionRegistryLite registry, Descriptors.FieldDescriptor field, Message defaultInstance) throws IOException {
      Message.Builder subBuilder = defaultInstance.newBuilderForType();
      if (!field.isRepeated()) {
        Message originalMessage = (Message)getField(field);
        if (originalMessage != null)
          subBuilder.mergeFrom(originalMessage); 
      } 
      subBuilder.mergeFrom(bytes, registry);
      return subBuilder.buildPartial();
    }
    
    public MessageReflection.MergeTarget newMergeTargetForField(Descriptors.FieldDescriptor descriptor, Message defaultInstance) {
      throw new UnsupportedOperationException("newMergeTargetForField() called on FieldSet object");
    }
    
    public MessageReflection.MergeTarget newEmptyTargetForField(Descriptors.FieldDescriptor descriptor, Message defaultInstance) {
      throw new UnsupportedOperationException("newEmptyTargetForField() called on FieldSet object");
    }
    
    public WireFormat.Utf8Validation getUtf8Validation(Descriptors.FieldDescriptor descriptor) {
      if (descriptor.needsUtf8Check())
        return WireFormat.Utf8Validation.STRICT; 
      return WireFormat.Utf8Validation.LOOSE;
    }
    
    public Object finish() {
      throw new UnsupportedOperationException("finish() called on FieldSet object");
    }
  }
  
  static boolean mergeFieldFrom(CodedInputStream input, UnknownFieldSet.Builder unknownFields, ExtensionRegistryLite extensionRegistry, Descriptors.Descriptor type, MergeTarget target, int tag) throws IOException {
    Descriptors.FieldDescriptor field;
    if (type.getOptions().getMessageSetWireFormat() && tag == WireFormat.MESSAGE_SET_ITEM_TAG) {
      mergeMessageSetExtensionFromCodedStream(input, unknownFields, extensionRegistry, type, target);
      return true;
    } 
    int wireType = WireFormat.getTagWireType(tag);
    int fieldNumber = WireFormat.getTagFieldNumber(tag);
    Message defaultInstance = null;
    if (type.isExtensionNumber(fieldNumber)) {
      if (extensionRegistry instanceof ExtensionRegistry) {
        ExtensionRegistry.ExtensionInfo extension = target.findExtensionByNumber((ExtensionRegistry)extensionRegistry, type, fieldNumber);
        if (extension == null) {
          field = null;
        } else {
          field = extension.descriptor;
          defaultInstance = extension.defaultInstance;
          if (defaultInstance == null && field
            .getJavaType() == Descriptors.FieldDescriptor.JavaType.MESSAGE)
            throw new IllegalStateException("Message-typed extension lacked default instance: " + field
                .getFullName()); 
        } 
      } else {
        field = null;
      } 
    } else if (target.getContainerType() == MergeTarget.ContainerType.MESSAGE) {
      field = type.findFieldByNumber(fieldNumber);
    } else {
      field = null;
    } 
    boolean unknown = false;
    boolean packed = false;
    if (field == null) {
      unknown = true;
    } else if (wireType == 
      FieldSet.getWireFormatForFieldType(field.getLiteType(), false)) {
      packed = false;
    } else if (field.isPackable() && wireType == 
      
      FieldSet.getWireFormatForFieldType(field.getLiteType(), true)) {
      packed = true;
    } else {
      unknown = true;
    } 
    if (unknown) {
      if (unknownFields != null)
        return unknownFields.mergeFieldFrom(tag, input); 
      return input.skipField(tag);
    } 
    if (packed) {
      int length = input.readRawVarint32();
      int limit = input.pushLimit(length);
      if (field.getLiteType() == WireFormat.FieldType.ENUM) {
        while (input.getBytesUntilLimit() > 0) {
          int rawValue = input.readEnum();
          if (field.getFile().supportsUnknownEnumValue()) {
            target.addRepeatedField(field, field
                .getEnumType().findValueByNumberCreatingIfUnknown(rawValue));
            continue;
          } 
          Object value = field.getEnumType().findValueByNumber(rawValue);
          if (value == null) {
            if (unknownFields != null)
              unknownFields.mergeVarintField(fieldNumber, rawValue); 
            continue;
          } 
          target.addRepeatedField(field, value);
        } 
      } else {
        while (input.getBytesUntilLimit() > 0) {
          Object value = WireFormat.readPrimitiveField(input, field
              .getLiteType(), target.getUtf8Validation(field));
          target.addRepeatedField(field, value);
        } 
      } 
      input.popLimit(limit);
    } else {
      Object value;
      int rawValue;
      switch (field.getType()) {
        case GROUP:
          target.mergeGroup(input, extensionRegistry, field, defaultInstance);
          return true;
        case MESSAGE:
          target.mergeMessage(input, extensionRegistry, field, defaultInstance);
          return true;
        case ENUM:
          rawValue = input.readEnum();
          if (field.getFile().supportsUnknownEnumValue()) {
            Object object = field.getEnumType().findValueByNumberCreatingIfUnknown(rawValue);
            break;
          } 
          value = field.getEnumType().findValueByNumber(rawValue);
          if (value == null) {
            if (unknownFields != null)
              unknownFields.mergeVarintField(fieldNumber, rawValue); 
            return true;
          } 
          break;
        default:
          value = WireFormat.readPrimitiveField(input, field
              .getLiteType(), target.getUtf8Validation(field));
          break;
      } 
      if (field.isRepeated()) {
        target.addRepeatedField(field, value);
      } else {
        target.setField(field, value);
      } 
    } 
    return true;
  }
  
  static void mergeMessageFrom(Message.Builder target, UnknownFieldSet.Builder unknownFields, CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
    int tag;
    BuilderAdapter builderAdapter = new BuilderAdapter(target);
    Descriptors.Descriptor descriptorForType = target.getDescriptorForType();
    do {
      tag = input.readTag();
      if (tag == 0)
        break; 
    } while (mergeFieldFrom(input, unknownFields, extensionRegistry, descriptorForType, builderAdapter, tag));
  }
  
  private static void mergeMessageSetExtensionFromCodedStream(CodedInputStream input, UnknownFieldSet.Builder unknownFields, ExtensionRegistryLite extensionRegistry, Descriptors.Descriptor type, MergeTarget target) throws IOException {
    int typeId = 0;
    ByteString rawBytes = null;
    ExtensionRegistry.ExtensionInfo extension = null;
    while (true) {
      int tag = input.readTag();
      if (tag == 0)
        break; 
      if (tag == WireFormat.MESSAGE_SET_TYPE_ID_TAG) {
        typeId = input.readUInt32();
        if (typeId != 0)
          if (extensionRegistry instanceof ExtensionRegistry)
            extension = target.findExtensionByNumber((ExtensionRegistry)extensionRegistry, type, typeId);  
        continue;
      } 
      if (tag == WireFormat.MESSAGE_SET_MESSAGE_TAG) {
        if (typeId != 0 && 
          extension != null && ExtensionRegistryLite.isEagerlyParseMessageSets()) {
          eagerlyMergeMessageSetExtension(input, extension, extensionRegistry, target);
          rawBytes = null;
          continue;
        } 
        rawBytes = input.readBytes();
        continue;
      } 
      if (!input.skipField(tag))
        break; 
    } 
    input.checkLastTagWas(WireFormat.MESSAGE_SET_ITEM_END_TAG);
    if (rawBytes != null && typeId != 0)
      if (extension != null) {
        mergeMessageSetExtensionFromBytes(rawBytes, extension, extensionRegistry, target);
      } else if (rawBytes != null && unknownFields != null) {
        unknownFields.mergeField(typeId, 
            UnknownFieldSet.Field.newBuilder().addLengthDelimited(rawBytes).build());
      }  
  }
  
  private static void mergeMessageSetExtensionFromBytes(ByteString rawBytes, ExtensionRegistry.ExtensionInfo extension, ExtensionRegistryLite extensionRegistry, MergeTarget target) throws IOException {
    Descriptors.FieldDescriptor field = extension.descriptor;
    boolean hasOriginalValue = target.hasField(field);
    if (hasOriginalValue || ExtensionRegistryLite.isEagerlyParseMessageSets()) {
      Object value = target.parseMessageFromBytes(rawBytes, extensionRegistry, field, extension.defaultInstance);
      target.setField(field, value);
    } else {
      LazyField lazyField = new LazyField(extension.defaultInstance, extensionRegistry, rawBytes);
      target.setField(field, lazyField);
    } 
  }
  
  private static void eagerlyMergeMessageSetExtension(CodedInputStream input, ExtensionRegistry.ExtensionInfo extension, ExtensionRegistryLite extensionRegistry, MergeTarget target) throws IOException {
    Descriptors.FieldDescriptor field = extension.descriptor;
    Object value = target.parseMessage(input, extensionRegistry, field, extension.defaultInstance);
    target.setField(field, value);
  }
}
