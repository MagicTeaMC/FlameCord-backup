package com.mysql.cj.x.protobuf;

import com.google.protobuf.AbstractMessage;
import com.google.protobuf.AbstractMessageLite;
import com.google.protobuf.AbstractParser;
import com.google.protobuf.ByteString;
import com.google.protobuf.CodedInputStream;
import com.google.protobuf.CodedOutputStream;
import com.google.protobuf.Descriptors;
import com.google.protobuf.ExtensionRegistry;
import com.google.protobuf.ExtensionRegistryLite;
import com.google.protobuf.GeneratedMessageV3;
import com.google.protobuf.Internal;
import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.Message;
import com.google.protobuf.MessageLite;
import com.google.protobuf.MessageOrBuilder;
import com.google.protobuf.Parser;
import com.google.protobuf.ProtocolMessageEnum;
import com.google.protobuf.RepeatedFieldBuilderV3;
import com.google.protobuf.UninitializedMessageException;
import com.google.protobuf.UnknownFieldSet;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class MysqlxExpect {
  private static final Descriptors.Descriptor internal_static_Mysqlx_Expect_Open_descriptor;
  
  private static final GeneratedMessageV3.FieldAccessorTable internal_static_Mysqlx_Expect_Open_fieldAccessorTable;
  
  private static final Descriptors.Descriptor internal_static_Mysqlx_Expect_Open_Condition_descriptor;
  
  private static final GeneratedMessageV3.FieldAccessorTable internal_static_Mysqlx_Expect_Open_Condition_fieldAccessorTable;
  
  private static final Descriptors.Descriptor internal_static_Mysqlx_Expect_Close_descriptor;
  
  private static final GeneratedMessageV3.FieldAccessorTable internal_static_Mysqlx_Expect_Close_fieldAccessorTable;
  
  private static Descriptors.FileDescriptor descriptor;
  
  public static void registerAllExtensions(ExtensionRegistryLite registry) {}
  
  public static void registerAllExtensions(ExtensionRegistry registry) {
    registerAllExtensions((ExtensionRegistryLite)registry);
  }
  
  public static interface OpenOrBuilder extends MessageOrBuilder {
    boolean hasOp();
    
    MysqlxExpect.Open.CtxOperation getOp();
    
    List<MysqlxExpect.Open.Condition> getCondList();
    
    MysqlxExpect.Open.Condition getCond(int param1Int);
    
    int getCondCount();
    
    List<? extends MysqlxExpect.Open.ConditionOrBuilder> getCondOrBuilderList();
    
    MysqlxExpect.Open.ConditionOrBuilder getCondOrBuilder(int param1Int);
  }
  
  public static final class Open extends GeneratedMessageV3 implements OpenOrBuilder {
    private static final long serialVersionUID = 0L;
    
    private int bitField0_;
    
    public static final int OP_FIELD_NUMBER = 1;
    
    private int op_;
    
    public static final int COND_FIELD_NUMBER = 2;
    
    private List<Condition> cond_;
    
    private byte memoizedIsInitialized;
    
    private Open(GeneratedMessageV3.Builder<?> builder) {
      super(builder);
      this.memoizedIsInitialized = -1;
    }
    
    private Open() {
      this.memoizedIsInitialized = -1;
      this.op_ = 0;
      this.cond_ = Collections.emptyList();
    }
    
    protected Object newInstance(GeneratedMessageV3.UnusedPrivateParameter unused) {
      return new Open();
    }
    
    public final UnknownFieldSet getUnknownFields() {
      return this.unknownFields;
    }
    
    public static final Descriptors.Descriptor getDescriptor() {
      return MysqlxExpect.internal_static_Mysqlx_Expect_Open_descriptor;
    }
    
    protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() {
      return MysqlxExpect.internal_static_Mysqlx_Expect_Open_fieldAccessorTable.ensureFieldAccessorsInitialized(Open.class, Builder.class);
    }
    
    public enum CtxOperation implements ProtocolMessageEnum {
      EXPECT_CTX_COPY_PREV(0),
      EXPECT_CTX_EMPTY(1);
      
      public static final int EXPECT_CTX_COPY_PREV_VALUE = 0;
      
      public static final int EXPECT_CTX_EMPTY_VALUE = 1;
      
      private static final Internal.EnumLiteMap<CtxOperation> internalValueMap = new Internal.EnumLiteMap<CtxOperation>() {
          public MysqlxExpect.Open.CtxOperation findValueByNumber(int number) {
            return MysqlxExpect.Open.CtxOperation.forNumber(number);
          }
        };
      
      private static final CtxOperation[] VALUES = values();
      
      private final int value;
      
      public final int getNumber() {
        return this.value;
      }
      
      public static CtxOperation forNumber(int value) {
        switch (value) {
          case 0:
            return EXPECT_CTX_COPY_PREV;
          case 1:
            return EXPECT_CTX_EMPTY;
        } 
        return null;
      }
      
      public static Internal.EnumLiteMap<CtxOperation> internalGetValueMap() {
        return internalValueMap;
      }
      
      static {
      
      }
      
      public final Descriptors.EnumValueDescriptor getValueDescriptor() {
        return getDescriptor().getValues().get(ordinal());
      }
      
      public final Descriptors.EnumDescriptor getDescriptorForType() {
        return getDescriptor();
      }
      
      public static final Descriptors.EnumDescriptor getDescriptor() {
        return MysqlxExpect.Open.getDescriptor().getEnumTypes().get(0);
      }
      
      CtxOperation(int value) {
        this.value = value;
      }
    }
    
    public static final class Condition extends GeneratedMessageV3 implements ConditionOrBuilder {
      private static final long serialVersionUID = 0L;
      
      private int bitField0_;
      
      public static final int CONDITION_KEY_FIELD_NUMBER = 1;
      
      private int conditionKey_;
      
      public static final int CONDITION_VALUE_FIELD_NUMBER = 2;
      
      private ByteString conditionValue_;
      
      public static final int OP_FIELD_NUMBER = 3;
      
      private int op_;
      
      private byte memoizedIsInitialized;
      
      private Condition(GeneratedMessageV3.Builder<?> builder) {
        super(builder);
        this.memoizedIsInitialized = -1;
      }
      
      private Condition() {
        this.memoizedIsInitialized = -1;
        this.conditionValue_ = ByteString.EMPTY;
        this.op_ = 0;
      }
      
      protected Object newInstance(GeneratedMessageV3.UnusedPrivateParameter unused) {
        return new Condition();
      }
      
      public final UnknownFieldSet getUnknownFields() {
        return this.unknownFields;
      }
      
      public static final Descriptors.Descriptor getDescriptor() {
        return MysqlxExpect.internal_static_Mysqlx_Expect_Open_Condition_descriptor;
      }
      
      protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() {
        return MysqlxExpect.internal_static_Mysqlx_Expect_Open_Condition_fieldAccessorTable.ensureFieldAccessorsInitialized(Condition.class, Builder.class);
      }
      
      public enum Key implements ProtocolMessageEnum {
        EXPECT_NO_ERROR(1),
        EXPECT_FIELD_EXIST(2),
        EXPECT_DOCID_GENERATED(3);
        
        public static final int EXPECT_NO_ERROR_VALUE = 1;
        
        public static final int EXPECT_FIELD_EXIST_VALUE = 2;
        
        public static final int EXPECT_DOCID_GENERATED_VALUE = 3;
        
        private static final Internal.EnumLiteMap<Key> internalValueMap = new Internal.EnumLiteMap<Key>() {
            public MysqlxExpect.Open.Condition.Key findValueByNumber(int number) {
              return MysqlxExpect.Open.Condition.Key.forNumber(number);
            }
          };
        
        private static final Key[] VALUES = values();
        
        private final int value;
        
        public final int getNumber() {
          return this.value;
        }
        
        public static Key forNumber(int value) {
          switch (value) {
            case 1:
              return EXPECT_NO_ERROR;
            case 2:
              return EXPECT_FIELD_EXIST;
            case 3:
              return EXPECT_DOCID_GENERATED;
          } 
          return null;
        }
        
        public static Internal.EnumLiteMap<Key> internalGetValueMap() {
          return internalValueMap;
        }
        
        static {
        
        }
        
        public final Descriptors.EnumValueDescriptor getValueDescriptor() {
          return getDescriptor().getValues().get(ordinal());
        }
        
        public final Descriptors.EnumDescriptor getDescriptorForType() {
          return getDescriptor();
        }
        
        public static final Descriptors.EnumDescriptor getDescriptor() {
          return MysqlxExpect.Open.Condition.getDescriptor().getEnumTypes().get(0);
        }
        
        Key(int value) {
          this.value = value;
        }
      }
      
      public enum ConditionOperation implements ProtocolMessageEnum {
        EXPECT_OP_SET(0),
        EXPECT_OP_UNSET(1);
        
        public static final int EXPECT_OP_SET_VALUE = 0;
        
        public static final int EXPECT_OP_UNSET_VALUE = 1;
        
        private static final Internal.EnumLiteMap<ConditionOperation> internalValueMap = new Internal.EnumLiteMap<ConditionOperation>() {
            public MysqlxExpect.Open.Condition.ConditionOperation findValueByNumber(int number) {
              return MysqlxExpect.Open.Condition.ConditionOperation.forNumber(number);
            }
          };
        
        private static final ConditionOperation[] VALUES = values();
        
        private final int value;
        
        public final int getNumber() {
          return this.value;
        }
        
        public static ConditionOperation forNumber(int value) {
          switch (value) {
            case 0:
              return EXPECT_OP_SET;
            case 1:
              return EXPECT_OP_UNSET;
          } 
          return null;
        }
        
        public static Internal.EnumLiteMap<ConditionOperation> internalGetValueMap() {
          return internalValueMap;
        }
        
        static {
        
        }
        
        public final Descriptors.EnumValueDescriptor getValueDescriptor() {
          return getDescriptor().getValues().get(ordinal());
        }
        
        public final Descriptors.EnumDescriptor getDescriptorForType() {
          return getDescriptor();
        }
        
        public static final Descriptors.EnumDescriptor getDescriptor() {
          return MysqlxExpect.Open.Condition.getDescriptor().getEnumTypes().get(1);
        }
        
        ConditionOperation(int value) {
          this.value = value;
        }
      }
      
      public boolean hasConditionKey() {
        return ((this.bitField0_ & 0x1) != 0);
      }
      
      public int getConditionKey() {
        return this.conditionKey_;
      }
      
      public boolean hasConditionValue() {
        return ((this.bitField0_ & 0x2) != 0);
      }
      
      public ByteString getConditionValue() {
        return this.conditionValue_;
      }
      
      public boolean hasOp() {
        return ((this.bitField0_ & 0x4) != 0);
      }
      
      public ConditionOperation getOp() {
        ConditionOperation result = ConditionOperation.valueOf(this.op_);
        return (result == null) ? ConditionOperation.EXPECT_OP_SET : result;
      }
      
      public final boolean isInitialized() {
        byte isInitialized = this.memoizedIsInitialized;
        if (isInitialized == 1)
          return true; 
        if (isInitialized == 0)
          return false; 
        if (!hasConditionKey()) {
          this.memoizedIsInitialized = 0;
          return false;
        } 
        this.memoizedIsInitialized = 1;
        return true;
      }
      
      public void writeTo(CodedOutputStream output) throws IOException {
        if ((this.bitField0_ & 0x1) != 0)
          output.writeUInt32(1, this.conditionKey_); 
        if ((this.bitField0_ & 0x2) != 0)
          output.writeBytes(2, this.conditionValue_); 
        if ((this.bitField0_ & 0x4) != 0)
          output.writeEnum(3, this.op_); 
        getUnknownFields().writeTo(output);
      }
      
      public int getSerializedSize() {
        int size = this.memoizedSize;
        if (size != -1)
          return size; 
        size = 0;
        if ((this.bitField0_ & 0x1) != 0)
          size += CodedOutputStream.computeUInt32Size(1, this.conditionKey_); 
        if ((this.bitField0_ & 0x2) != 0)
          size += CodedOutputStream.computeBytesSize(2, this.conditionValue_); 
        if ((this.bitField0_ & 0x4) != 0)
          size += CodedOutputStream.computeEnumSize(3, this.op_); 
        size += getUnknownFields().getSerializedSize();
        this.memoizedSize = size;
        return size;
      }
      
      public boolean equals(Object obj) {
        if (obj == this)
          return true; 
        if (!(obj instanceof Condition))
          return super.equals(obj); 
        Condition other = (Condition)obj;
        if (hasConditionKey() != other.hasConditionKey())
          return false; 
        if (hasConditionKey() && getConditionKey() != other.getConditionKey())
          return false; 
        if (hasConditionValue() != other.hasConditionValue())
          return false; 
        if (hasConditionValue() && !getConditionValue().equals(other.getConditionValue()))
          return false; 
        if (hasOp() != other.hasOp())
          return false; 
        if (hasOp() && this.op_ != other.op_)
          return false; 
        if (!getUnknownFields().equals(other.getUnknownFields()))
          return false; 
        return true;
      }
      
      public int hashCode() {
        if (this.memoizedHashCode != 0)
          return this.memoizedHashCode; 
        int hash = 41;
        hash = 19 * hash + getDescriptor().hashCode();
        if (hasConditionKey()) {
          hash = 37 * hash + 1;
          hash = 53 * hash + getConditionKey();
        } 
        if (hasConditionValue()) {
          hash = 37 * hash + 2;
          hash = 53 * hash + getConditionValue().hashCode();
        } 
        if (hasOp()) {
          hash = 37 * hash + 3;
          hash = 53 * hash + this.op_;
        } 
        hash = 29 * hash + getUnknownFields().hashCode();
        this.memoizedHashCode = hash;
        return hash;
      }
      
      public static Condition parseFrom(ByteBuffer data) throws InvalidProtocolBufferException {
        return (Condition)PARSER.parseFrom(data);
      }
      
      public static Condition parseFrom(ByteBuffer data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
        return (Condition)PARSER.parseFrom(data, extensionRegistry);
      }
      
      public static Condition parseFrom(ByteString data) throws InvalidProtocolBufferException {
        return (Condition)PARSER.parseFrom(data);
      }
      
      public static Condition parseFrom(ByteString data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
        return (Condition)PARSER.parseFrom(data, extensionRegistry);
      }
      
      public static Condition parseFrom(byte[] data) throws InvalidProtocolBufferException {
        return (Condition)PARSER.parseFrom(data);
      }
      
      public static Condition parseFrom(byte[] data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
        return (Condition)PARSER.parseFrom(data, extensionRegistry);
      }
      
      public static Condition parseFrom(InputStream input) throws IOException {
        return (Condition)GeneratedMessageV3.parseWithIOException(PARSER, input);
      }
      
      public static Condition parseFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
        return (Condition)GeneratedMessageV3.parseWithIOException(PARSER, input, extensionRegistry);
      }
      
      public static Condition parseDelimitedFrom(InputStream input) throws IOException {
        return (Condition)GeneratedMessageV3.parseDelimitedWithIOException(PARSER, input);
      }
      
      public static Condition parseDelimitedFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
        return (Condition)GeneratedMessageV3.parseDelimitedWithIOException(PARSER, input, extensionRegistry);
      }
      
      public static Condition parseFrom(CodedInputStream input) throws IOException {
        return (Condition)GeneratedMessageV3.parseWithIOException(PARSER, input);
      }
      
      public static Condition parseFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
        return (Condition)GeneratedMessageV3.parseWithIOException(PARSER, input, extensionRegistry);
      }
      
      public Builder newBuilderForType() {
        return newBuilder();
      }
      
      public static Builder newBuilder() {
        return DEFAULT_INSTANCE.toBuilder();
      }
      
      public static Builder newBuilder(Condition prototype) {
        return DEFAULT_INSTANCE.toBuilder().mergeFrom(prototype);
      }
      
      public Builder toBuilder() {
        return (this == DEFAULT_INSTANCE) ? new Builder() : (new Builder()).mergeFrom(this);
      }
      
      protected Builder newBuilderForType(GeneratedMessageV3.BuilderParent parent) {
        Builder builder = new Builder(parent);
        return builder;
      }
      
      public static final class Builder extends GeneratedMessageV3.Builder<Builder> implements MysqlxExpect.Open.ConditionOrBuilder {
        private int bitField0_;
        
        private int conditionKey_;
        
        private ByteString conditionValue_;
        
        private int op_;
        
        public static final Descriptors.Descriptor getDescriptor() {
          return MysqlxExpect.internal_static_Mysqlx_Expect_Open_Condition_descriptor;
        }
        
        protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() {
          return MysqlxExpect.internal_static_Mysqlx_Expect_Open_Condition_fieldAccessorTable.ensureFieldAccessorsInitialized(MysqlxExpect.Open.Condition.class, Builder.class);
        }
        
        private Builder() {
          this.conditionValue_ = ByteString.EMPTY;
          this.op_ = 0;
        }
        
        private Builder(GeneratedMessageV3.BuilderParent parent) {
          super(parent);
          this.conditionValue_ = ByteString.EMPTY;
          this.op_ = 0;
        }
        
        public Builder clear() {
          super.clear();
          this.conditionKey_ = 0;
          this.bitField0_ &= 0xFFFFFFFE;
          this.conditionValue_ = ByteString.EMPTY;
          this.bitField0_ &= 0xFFFFFFFD;
          this.op_ = 0;
          this.bitField0_ &= 0xFFFFFFFB;
          return this;
        }
        
        public Descriptors.Descriptor getDescriptorForType() {
          return MysqlxExpect.internal_static_Mysqlx_Expect_Open_Condition_descriptor;
        }
        
        public MysqlxExpect.Open.Condition getDefaultInstanceForType() {
          return MysqlxExpect.Open.Condition.getDefaultInstance();
        }
        
        public MysqlxExpect.Open.Condition build() {
          MysqlxExpect.Open.Condition result = buildPartial();
          if (!result.isInitialized())
            throw newUninitializedMessageException(result); 
          return result;
        }
        
        public MysqlxExpect.Open.Condition buildPartial() {
          MysqlxExpect.Open.Condition result = new MysqlxExpect.Open.Condition(this);
          int from_bitField0_ = this.bitField0_;
          int to_bitField0_ = 0;
          if ((from_bitField0_ & 0x1) != 0) {
            result.conditionKey_ = this.conditionKey_;
            to_bitField0_ |= 0x1;
          } 
          if ((from_bitField0_ & 0x2) != 0)
            to_bitField0_ |= 0x2; 
          result.conditionValue_ = this.conditionValue_;
          if ((from_bitField0_ & 0x4) != 0)
            to_bitField0_ |= 0x4; 
          result.op_ = this.op_;
          result.bitField0_ = to_bitField0_;
          onBuilt();
          return result;
        }
        
        public Builder clone() {
          return (Builder)super.clone();
        }
        
        public Builder setField(Descriptors.FieldDescriptor field, Object value) {
          return (Builder)super.setField(field, value);
        }
        
        public Builder clearField(Descriptors.FieldDescriptor field) {
          return (Builder)super.clearField(field);
        }
        
        public Builder clearOneof(Descriptors.OneofDescriptor oneof) {
          return (Builder)super.clearOneof(oneof);
        }
        
        public Builder setRepeatedField(Descriptors.FieldDescriptor field, int index, Object value) {
          return (Builder)super.setRepeatedField(field, index, value);
        }
        
        public Builder addRepeatedField(Descriptors.FieldDescriptor field, Object value) {
          return (Builder)super.addRepeatedField(field, value);
        }
        
        public Builder mergeFrom(Message other) {
          if (other instanceof MysqlxExpect.Open.Condition)
            return mergeFrom((MysqlxExpect.Open.Condition)other); 
          super.mergeFrom(other);
          return this;
        }
        
        public Builder mergeFrom(MysqlxExpect.Open.Condition other) {
          if (other == MysqlxExpect.Open.Condition.getDefaultInstance())
            return this; 
          if (other.hasConditionKey())
            setConditionKey(other.getConditionKey()); 
          if (other.hasConditionValue())
            setConditionValue(other.getConditionValue()); 
          if (other.hasOp())
            setOp(other.getOp()); 
          mergeUnknownFields(other.getUnknownFields());
          onChanged();
          return this;
        }
        
        public final boolean isInitialized() {
          if (!hasConditionKey())
            return false; 
          return true;
        }
        
        public Builder mergeFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
          if (extensionRegistry == null)
            throw new NullPointerException(); 
          try {
            boolean done = false;
            while (!done) {
              int tmpRaw;
              MysqlxExpect.Open.Condition.ConditionOperation tmpValue;
              int tag = input.readTag();
              switch (tag) {
                case 0:
                  done = true;
                  continue;
                case 8:
                  this.conditionKey_ = input.readUInt32();
                  this.bitField0_ |= 0x1;
                  continue;
                case 18:
                  this.conditionValue_ = input.readBytes();
                  this.bitField0_ |= 0x2;
                  continue;
                case 24:
                  tmpRaw = input.readEnum();
                  tmpValue = MysqlxExpect.Open.Condition.ConditionOperation.forNumber(tmpRaw);
                  if (tmpValue == null) {
                    mergeUnknownVarintField(3, tmpRaw);
                    continue;
                  } 
                  this.op_ = tmpRaw;
                  this.bitField0_ |= 0x4;
                  continue;
              } 
              if (!parseUnknownField(input, extensionRegistry, tag))
                done = true; 
            } 
          } catch (InvalidProtocolBufferException e) {
            throw e.unwrapIOException();
          } finally {
            onChanged();
          } 
          return this;
        }
        
        public boolean hasConditionKey() {
          return ((this.bitField0_ & 0x1) != 0);
        }
        
        public int getConditionKey() {
          return this.conditionKey_;
        }
        
        public Builder setConditionKey(int value) {
          this.bitField0_ |= 0x1;
          this.conditionKey_ = value;
          onChanged();
          return this;
        }
        
        public Builder clearConditionKey() {
          this.bitField0_ &= 0xFFFFFFFE;
          this.conditionKey_ = 0;
          onChanged();
          return this;
        }
        
        public boolean hasConditionValue() {
          return ((this.bitField0_ & 0x2) != 0);
        }
        
        public ByteString getConditionValue() {
          return this.conditionValue_;
        }
        
        public Builder setConditionValue(ByteString value) {
          if (value == null)
            throw new NullPointerException(); 
          this.bitField0_ |= 0x2;
          this.conditionValue_ = value;
          onChanged();
          return this;
        }
        
        public Builder clearConditionValue() {
          this.bitField0_ &= 0xFFFFFFFD;
          this.conditionValue_ = MysqlxExpect.Open.Condition.getDefaultInstance().getConditionValue();
          onChanged();
          return this;
        }
        
        public boolean hasOp() {
          return ((this.bitField0_ & 0x4) != 0);
        }
        
        public MysqlxExpect.Open.Condition.ConditionOperation getOp() {
          MysqlxExpect.Open.Condition.ConditionOperation result = MysqlxExpect.Open.Condition.ConditionOperation.valueOf(this.op_);
          return (result == null) ? MysqlxExpect.Open.Condition.ConditionOperation.EXPECT_OP_SET : result;
        }
        
        public Builder setOp(MysqlxExpect.Open.Condition.ConditionOperation value) {
          if (value == null)
            throw new NullPointerException(); 
          this.bitField0_ |= 0x4;
          this.op_ = value.getNumber();
          onChanged();
          return this;
        }
        
        public Builder clearOp() {
          this.bitField0_ &= 0xFFFFFFFB;
          this.op_ = 0;
          onChanged();
          return this;
        }
        
        public final Builder setUnknownFields(UnknownFieldSet unknownFields) {
          return (Builder)super.setUnknownFields(unknownFields);
        }
        
        public final Builder mergeUnknownFields(UnknownFieldSet unknownFields) {
          return (Builder)super.mergeUnknownFields(unknownFields);
        }
      }
      
      private static final Condition DEFAULT_INSTANCE = new Condition();
      
      public static Condition getDefaultInstance() {
        return DEFAULT_INSTANCE;
      }
      
      @Deprecated
      public static final Parser<Condition> PARSER = (Parser<Condition>)new AbstractParser<Condition>() {
          public MysqlxExpect.Open.Condition parsePartialFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            MysqlxExpect.Open.Condition.Builder builder = MysqlxExpect.Open.Condition.newBuilder();
            try {
              builder.mergeFrom(input, extensionRegistry);
            } catch (InvalidProtocolBufferException e) {
              throw e.setUnfinishedMessage(builder.buildPartial());
            } catch (UninitializedMessageException e) {
              throw e.asInvalidProtocolBufferException().setUnfinishedMessage(builder.buildPartial());
            } catch (IOException e) {
              throw (new InvalidProtocolBufferException(e)).setUnfinishedMessage(builder.buildPartial());
            } 
            return builder.buildPartial();
          }
        };
      
      public static Parser<Condition> parser() {
        return PARSER;
      }
      
      public Parser<Condition> getParserForType() {
        return PARSER;
      }
      
      public Condition getDefaultInstanceForType() {
        return DEFAULT_INSTANCE;
      }
    }
    
    public boolean hasOp() {
      return ((this.bitField0_ & 0x1) != 0);
    }
    
    public CtxOperation getOp() {
      CtxOperation result = CtxOperation.valueOf(this.op_);
      return (result == null) ? CtxOperation.EXPECT_CTX_COPY_PREV : result;
    }
    
    public List<Condition> getCondList() {
      return this.cond_;
    }
    
    public List<? extends ConditionOrBuilder> getCondOrBuilderList() {
      return (List)this.cond_;
    }
    
    public int getCondCount() {
      return this.cond_.size();
    }
    
    public Condition getCond(int index) {
      return this.cond_.get(index);
    }
    
    public ConditionOrBuilder getCondOrBuilder(int index) {
      return this.cond_.get(index);
    }
    
    public final boolean isInitialized() {
      byte isInitialized = this.memoizedIsInitialized;
      if (isInitialized == 1)
        return true; 
      if (isInitialized == 0)
        return false; 
      for (int i = 0; i < getCondCount(); i++) {
        if (!getCond(i).isInitialized()) {
          this.memoizedIsInitialized = 0;
          return false;
        } 
      } 
      this.memoizedIsInitialized = 1;
      return true;
    }
    
    public void writeTo(CodedOutputStream output) throws IOException {
      if ((this.bitField0_ & 0x1) != 0)
        output.writeEnum(1, this.op_); 
      for (int i = 0; i < this.cond_.size(); i++)
        output.writeMessage(2, (MessageLite)this.cond_.get(i)); 
      getUnknownFields().writeTo(output);
    }
    
    public int getSerializedSize() {
      int size = this.memoizedSize;
      if (size != -1)
        return size; 
      size = 0;
      if ((this.bitField0_ & 0x1) != 0)
        size += 
          CodedOutputStream.computeEnumSize(1, this.op_); 
      for (int i = 0; i < this.cond_.size(); i++)
        size += 
          CodedOutputStream.computeMessageSize(2, (MessageLite)this.cond_.get(i)); 
      size += getUnknownFields().getSerializedSize();
      this.memoizedSize = size;
      return size;
    }
    
    public boolean equals(Object obj) {
      if (obj == this)
        return true; 
      if (!(obj instanceof Open))
        return super.equals(obj); 
      Open other = (Open)obj;
      if (hasOp() != other.hasOp())
        return false; 
      if (hasOp() && 
        this.op_ != other.op_)
        return false; 
      if (!getCondList().equals(other.getCondList()))
        return false; 
      if (!getUnknownFields().equals(other.getUnknownFields()))
        return false; 
      return true;
    }
    
    public int hashCode() {
      if (this.memoizedHashCode != 0)
        return this.memoizedHashCode; 
      int hash = 41;
      hash = 19 * hash + getDescriptor().hashCode();
      if (hasOp()) {
        hash = 37 * hash + 1;
        hash = 53 * hash + this.op_;
      } 
      if (getCondCount() > 0) {
        hash = 37 * hash + 2;
        hash = 53 * hash + getCondList().hashCode();
      } 
      hash = 29 * hash + getUnknownFields().hashCode();
      this.memoizedHashCode = hash;
      return hash;
    }
    
    public static Open parseFrom(ByteBuffer data) throws InvalidProtocolBufferException {
      return (Open)PARSER.parseFrom(data);
    }
    
    public static Open parseFrom(ByteBuffer data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
      return (Open)PARSER.parseFrom(data, extensionRegistry);
    }
    
    public static Open parseFrom(ByteString data) throws InvalidProtocolBufferException {
      return (Open)PARSER.parseFrom(data);
    }
    
    public static Open parseFrom(ByteString data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
      return (Open)PARSER.parseFrom(data, extensionRegistry);
    }
    
    public static Open parseFrom(byte[] data) throws InvalidProtocolBufferException {
      return (Open)PARSER.parseFrom(data);
    }
    
    public static Open parseFrom(byte[] data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
      return (Open)PARSER.parseFrom(data, extensionRegistry);
    }
    
    public static Open parseFrom(InputStream input) throws IOException {
      return 
        (Open)GeneratedMessageV3.parseWithIOException(PARSER, input);
    }
    
    public static Open parseFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
      return 
        (Open)GeneratedMessageV3.parseWithIOException(PARSER, input, extensionRegistry);
    }
    
    public static Open parseDelimitedFrom(InputStream input) throws IOException {
      return 
        (Open)GeneratedMessageV3.parseDelimitedWithIOException(PARSER, input);
    }
    
    public static Open parseDelimitedFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
      return 
        (Open)GeneratedMessageV3.parseDelimitedWithIOException(PARSER, input, extensionRegistry);
    }
    
    public static Open parseFrom(CodedInputStream input) throws IOException {
      return 
        (Open)GeneratedMessageV3.parseWithIOException(PARSER, input);
    }
    
    public static Open parseFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
      return 
        (Open)GeneratedMessageV3.parseWithIOException(PARSER, input, extensionRegistry);
    }
    
    public Builder newBuilderForType() {
      return newBuilder();
    }
    
    public static Builder newBuilder() {
      return DEFAULT_INSTANCE.toBuilder();
    }
    
    public static Builder newBuilder(Open prototype) {
      return DEFAULT_INSTANCE.toBuilder().mergeFrom(prototype);
    }
    
    public Builder toBuilder() {
      return (this == DEFAULT_INSTANCE) ? new Builder() : (new Builder())
        .mergeFrom(this);
    }
    
    protected Builder newBuilderForType(GeneratedMessageV3.BuilderParent parent) {
      Builder builder = new Builder(parent);
      return builder;
    }
    
    public static final class Builder extends GeneratedMessageV3.Builder<Builder> implements MysqlxExpect.OpenOrBuilder {
      private int bitField0_;
      
      private int op_;
      
      private List<MysqlxExpect.Open.Condition> cond_;
      
      private RepeatedFieldBuilderV3<MysqlxExpect.Open.Condition, MysqlxExpect.Open.Condition.Builder, MysqlxExpect.Open.ConditionOrBuilder> condBuilder_;
      
      public static final Descriptors.Descriptor getDescriptor() {
        return MysqlxExpect.internal_static_Mysqlx_Expect_Open_descriptor;
      }
      
      protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() {
        return MysqlxExpect.internal_static_Mysqlx_Expect_Open_fieldAccessorTable
          .ensureFieldAccessorsInitialized(MysqlxExpect.Open.class, Builder.class);
      }
      
      private Builder() {
        this.op_ = 0;
        this
          .cond_ = Collections.emptyList();
      }
      
      private Builder(GeneratedMessageV3.BuilderParent parent) {
        super(parent);
        this.op_ = 0;
        this.cond_ = Collections.emptyList();
      }
      
      public Builder clear() {
        super.clear();
        this.op_ = 0;
        this.bitField0_ &= 0xFFFFFFFE;
        if (this.condBuilder_ == null) {
          this.cond_ = Collections.emptyList();
        } else {
          this.cond_ = null;
          this.condBuilder_.clear();
        } 
        this.bitField0_ &= 0xFFFFFFFD;
        return this;
      }
      
      public Descriptors.Descriptor getDescriptorForType() {
        return MysqlxExpect.internal_static_Mysqlx_Expect_Open_descriptor;
      }
      
      public MysqlxExpect.Open getDefaultInstanceForType() {
        return MysqlxExpect.Open.getDefaultInstance();
      }
      
      public MysqlxExpect.Open build() {
        MysqlxExpect.Open result = buildPartial();
        if (!result.isInitialized())
          throw newUninitializedMessageException(result); 
        return result;
      }
      
      public MysqlxExpect.Open buildPartial() {
        MysqlxExpect.Open result = new MysqlxExpect.Open(this);
        int from_bitField0_ = this.bitField0_;
        int to_bitField0_ = 0;
        if ((from_bitField0_ & 0x1) != 0)
          to_bitField0_ |= 0x1; 
        result.op_ = this.op_;
        if (this.condBuilder_ == null) {
          if ((this.bitField0_ & 0x2) != 0) {
            this.cond_ = Collections.unmodifiableList(this.cond_);
            this.bitField0_ &= 0xFFFFFFFD;
          } 
          result.cond_ = this.cond_;
        } else {
          result.cond_ = this.condBuilder_.build();
        } 
        result.bitField0_ = to_bitField0_;
        onBuilt();
        return result;
      }
      
      public Builder clone() {
        return (Builder)super.clone();
      }
      
      public Builder setField(Descriptors.FieldDescriptor field, Object value) {
        return (Builder)super.setField(field, value);
      }
      
      public Builder clearField(Descriptors.FieldDescriptor field) {
        return (Builder)super.clearField(field);
      }
      
      public Builder clearOneof(Descriptors.OneofDescriptor oneof) {
        return (Builder)super.clearOneof(oneof);
      }
      
      public Builder setRepeatedField(Descriptors.FieldDescriptor field, int index, Object value) {
        return (Builder)super.setRepeatedField(field, index, value);
      }
      
      public Builder addRepeatedField(Descriptors.FieldDescriptor field, Object value) {
        return (Builder)super.addRepeatedField(field, value);
      }
      
      public Builder mergeFrom(Message other) {
        if (other instanceof MysqlxExpect.Open)
          return mergeFrom((MysqlxExpect.Open)other); 
        super.mergeFrom(other);
        return this;
      }
      
      public Builder mergeFrom(MysqlxExpect.Open other) {
        if (other == MysqlxExpect.Open.getDefaultInstance())
          return this; 
        if (other.hasOp())
          setOp(other.getOp()); 
        if (this.condBuilder_ == null) {
          if (!other.cond_.isEmpty()) {
            if (this.cond_.isEmpty()) {
              this.cond_ = other.cond_;
              this.bitField0_ &= 0xFFFFFFFD;
            } else {
              ensureCondIsMutable();
              this.cond_.addAll(other.cond_);
            } 
            onChanged();
          } 
        } else if (!other.cond_.isEmpty()) {
          if (this.condBuilder_.isEmpty()) {
            this.condBuilder_.dispose();
            this.condBuilder_ = null;
            this.cond_ = other.cond_;
            this.bitField0_ &= 0xFFFFFFFD;
            this.condBuilder_ = MysqlxExpect.Open.alwaysUseFieldBuilders ? getCondFieldBuilder() : null;
          } else {
            this.condBuilder_.addAllMessages(other.cond_);
          } 
        } 
        mergeUnknownFields(other.getUnknownFields());
        onChanged();
        return this;
      }
      
      public final boolean isInitialized() {
        for (int i = 0; i < getCondCount(); i++) {
          if (!getCond(i).isInitialized())
            return false; 
        } 
        return true;
      }
      
      public Builder mergeFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
        if (extensionRegistry == null)
          throw new NullPointerException(); 
        try {
          boolean done = false;
          while (!done) {
            int tmpRaw;
            MysqlxExpect.Open.Condition m;
            MysqlxExpect.Open.CtxOperation tmpValue;
            int tag = input.readTag();
            switch (tag) {
              case 0:
                done = true;
                continue;
              case 8:
                tmpRaw = input.readEnum();
                tmpValue = MysqlxExpect.Open.CtxOperation.forNumber(tmpRaw);
                if (tmpValue == null) {
                  mergeUnknownVarintField(1, tmpRaw);
                  continue;
                } 
                this.op_ = tmpRaw;
                this.bitField0_ |= 0x1;
                continue;
              case 18:
                m = (MysqlxExpect.Open.Condition)input.readMessage(MysqlxExpect.Open.Condition.PARSER, extensionRegistry);
                if (this.condBuilder_ == null) {
                  ensureCondIsMutable();
                  this.cond_.add(m);
                  continue;
                } 
                this.condBuilder_.addMessage((AbstractMessage)m);
                continue;
            } 
            if (!parseUnknownField(input, extensionRegistry, tag))
              done = true; 
          } 
        } catch (InvalidProtocolBufferException e) {
          throw e.unwrapIOException();
        } finally {
          onChanged();
        } 
        return this;
      }
      
      public boolean hasOp() {
        return ((this.bitField0_ & 0x1) != 0);
      }
      
      public MysqlxExpect.Open.CtxOperation getOp() {
        MysqlxExpect.Open.CtxOperation result = MysqlxExpect.Open.CtxOperation.valueOf(this.op_);
        return (result == null) ? MysqlxExpect.Open.CtxOperation.EXPECT_CTX_COPY_PREV : result;
      }
      
      public Builder setOp(MysqlxExpect.Open.CtxOperation value) {
        if (value == null)
          throw new NullPointerException(); 
        this.bitField0_ |= 0x1;
        this.op_ = value.getNumber();
        onChanged();
        return this;
      }
      
      public Builder clearOp() {
        this.bitField0_ &= 0xFFFFFFFE;
        this.op_ = 0;
        onChanged();
        return this;
      }
      
      private void ensureCondIsMutable() {
        if ((this.bitField0_ & 0x2) == 0) {
          this.cond_ = new ArrayList<>(this.cond_);
          this.bitField0_ |= 0x2;
        } 
      }
      
      public List<MysqlxExpect.Open.Condition> getCondList() {
        if (this.condBuilder_ == null)
          return Collections.unmodifiableList(this.cond_); 
        return this.condBuilder_.getMessageList();
      }
      
      public int getCondCount() {
        if (this.condBuilder_ == null)
          return this.cond_.size(); 
        return this.condBuilder_.getCount();
      }
      
      public MysqlxExpect.Open.Condition getCond(int index) {
        if (this.condBuilder_ == null)
          return this.cond_.get(index); 
        return (MysqlxExpect.Open.Condition)this.condBuilder_.getMessage(index);
      }
      
      public Builder setCond(int index, MysqlxExpect.Open.Condition value) {
        if (this.condBuilder_ == null) {
          if (value == null)
            throw new NullPointerException(); 
          ensureCondIsMutable();
          this.cond_.set(index, value);
          onChanged();
        } else {
          this.condBuilder_.setMessage(index, (AbstractMessage)value);
        } 
        return this;
      }
      
      public Builder setCond(int index, MysqlxExpect.Open.Condition.Builder builderForValue) {
        if (this.condBuilder_ == null) {
          ensureCondIsMutable();
          this.cond_.set(index, builderForValue.build());
          onChanged();
        } else {
          this.condBuilder_.setMessage(index, (AbstractMessage)builderForValue.build());
        } 
        return this;
      }
      
      public Builder addCond(MysqlxExpect.Open.Condition value) {
        if (this.condBuilder_ == null) {
          if (value == null)
            throw new NullPointerException(); 
          ensureCondIsMutable();
          this.cond_.add(value);
          onChanged();
        } else {
          this.condBuilder_.addMessage((AbstractMessage)value);
        } 
        return this;
      }
      
      public Builder addCond(int index, MysqlxExpect.Open.Condition value) {
        if (this.condBuilder_ == null) {
          if (value == null)
            throw new NullPointerException(); 
          ensureCondIsMutable();
          this.cond_.add(index, value);
          onChanged();
        } else {
          this.condBuilder_.addMessage(index, (AbstractMessage)value);
        } 
        return this;
      }
      
      public Builder addCond(MysqlxExpect.Open.Condition.Builder builderForValue) {
        if (this.condBuilder_ == null) {
          ensureCondIsMutable();
          this.cond_.add(builderForValue.build());
          onChanged();
        } else {
          this.condBuilder_.addMessage((AbstractMessage)builderForValue.build());
        } 
        return this;
      }
      
      public Builder addCond(int index, MysqlxExpect.Open.Condition.Builder builderForValue) {
        if (this.condBuilder_ == null) {
          ensureCondIsMutable();
          this.cond_.add(index, builderForValue.build());
          onChanged();
        } else {
          this.condBuilder_.addMessage(index, (AbstractMessage)builderForValue.build());
        } 
        return this;
      }
      
      public Builder addAllCond(Iterable<? extends MysqlxExpect.Open.Condition> values) {
        if (this.condBuilder_ == null) {
          ensureCondIsMutable();
          AbstractMessageLite.Builder.addAll(values, this.cond_);
          onChanged();
        } else {
          this.condBuilder_.addAllMessages(values);
        } 
        return this;
      }
      
      public Builder clearCond() {
        if (this.condBuilder_ == null) {
          this.cond_ = Collections.emptyList();
          this.bitField0_ &= 0xFFFFFFFD;
          onChanged();
        } else {
          this.condBuilder_.clear();
        } 
        return this;
      }
      
      public Builder removeCond(int index) {
        if (this.condBuilder_ == null) {
          ensureCondIsMutable();
          this.cond_.remove(index);
          onChanged();
        } else {
          this.condBuilder_.remove(index);
        } 
        return this;
      }
      
      public MysqlxExpect.Open.Condition.Builder getCondBuilder(int index) {
        return (MysqlxExpect.Open.Condition.Builder)getCondFieldBuilder().getBuilder(index);
      }
      
      public MysqlxExpect.Open.ConditionOrBuilder getCondOrBuilder(int index) {
        if (this.condBuilder_ == null)
          return this.cond_.get(index); 
        return (MysqlxExpect.Open.ConditionOrBuilder)this.condBuilder_.getMessageOrBuilder(index);
      }
      
      public List<? extends MysqlxExpect.Open.ConditionOrBuilder> getCondOrBuilderList() {
        if (this.condBuilder_ != null)
          return this.condBuilder_.getMessageOrBuilderList(); 
        return Collections.unmodifiableList((List)this.cond_);
      }
      
      public MysqlxExpect.Open.Condition.Builder addCondBuilder() {
        return (MysqlxExpect.Open.Condition.Builder)getCondFieldBuilder().addBuilder(
            (AbstractMessage)MysqlxExpect.Open.Condition.getDefaultInstance());
      }
      
      public MysqlxExpect.Open.Condition.Builder addCondBuilder(int index) {
        return (MysqlxExpect.Open.Condition.Builder)getCondFieldBuilder().addBuilder(index, 
            (AbstractMessage)MysqlxExpect.Open.Condition.getDefaultInstance());
      }
      
      public List<MysqlxExpect.Open.Condition.Builder> getCondBuilderList() {
        return getCondFieldBuilder().getBuilderList();
      }
      
      private RepeatedFieldBuilderV3<MysqlxExpect.Open.Condition, MysqlxExpect.Open.Condition.Builder, MysqlxExpect.Open.ConditionOrBuilder> getCondFieldBuilder() {
        if (this.condBuilder_ == null) {
          this
            
            .condBuilder_ = new RepeatedFieldBuilderV3(this.cond_, ((this.bitField0_ & 0x2) != 0), (AbstractMessage.BuilderParent)getParentForChildren(), isClean());
          this.cond_ = null;
        } 
        return this.condBuilder_;
      }
      
      public final Builder setUnknownFields(UnknownFieldSet unknownFields) {
        return (Builder)super.setUnknownFields(unknownFields);
      }
      
      public final Builder mergeUnknownFields(UnknownFieldSet unknownFields) {
        return (Builder)super.mergeUnknownFields(unknownFields);
      }
    }
    
    private static final Open DEFAULT_INSTANCE = new Open();
    
    public static Open getDefaultInstance() {
      return DEFAULT_INSTANCE;
    }
    
    @Deprecated
    public static final Parser<Open> PARSER = (Parser<Open>)new AbstractParser<Open>() {
        public MysqlxExpect.Open parsePartialFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
          MysqlxExpect.Open.Builder builder = MysqlxExpect.Open.newBuilder();
          try {
            builder.mergeFrom(input, extensionRegistry);
          } catch (InvalidProtocolBufferException e) {
            throw e.setUnfinishedMessage(builder.buildPartial());
          } catch (UninitializedMessageException e) {
            throw e.asInvalidProtocolBufferException().setUnfinishedMessage(builder.buildPartial());
          } catch (IOException e) {
            throw (new InvalidProtocolBufferException(e))
              .setUnfinishedMessage(builder.buildPartial());
          } 
          return builder.buildPartial();
        }
      };
    
    public static Parser<Open> parser() {
      return PARSER;
    }
    
    public Parser<Open> getParserForType() {
      return PARSER;
    }
    
    public Open getDefaultInstanceForType() {
      return DEFAULT_INSTANCE;
    }
    
    public static interface ConditionOrBuilder extends MessageOrBuilder {
      boolean hasConditionKey();
      
      int getConditionKey();
      
      boolean hasConditionValue();
      
      ByteString getConditionValue();
      
      boolean hasOp();
      
      MysqlxExpect.Open.Condition.ConditionOperation getOp();
    }
  }
  
  public static interface CloseOrBuilder extends MessageOrBuilder {}
  
  public static final class Close extends GeneratedMessageV3 implements CloseOrBuilder {
    private static final long serialVersionUID = 0L;
    
    private byte memoizedIsInitialized;
    
    private Close(GeneratedMessageV3.Builder<?> builder) {
      super(builder);
      this.memoizedIsInitialized = -1;
    }
    
    private Close() {
      this.memoizedIsInitialized = -1;
    }
    
    protected Object newInstance(GeneratedMessageV3.UnusedPrivateParameter unused) {
      return new Close();
    }
    
    public final UnknownFieldSet getUnknownFields() {
      return this.unknownFields;
    }
    
    public static final Descriptors.Descriptor getDescriptor() {
      return MysqlxExpect.internal_static_Mysqlx_Expect_Close_descriptor;
    }
    
    protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() {
      return MysqlxExpect.internal_static_Mysqlx_Expect_Close_fieldAccessorTable.ensureFieldAccessorsInitialized(Close.class, Builder.class);
    }
    
    public final boolean isInitialized() {
      byte isInitialized = this.memoizedIsInitialized;
      if (isInitialized == 1)
        return true; 
      if (isInitialized == 0)
        return false; 
      this.memoizedIsInitialized = 1;
      return true;
    }
    
    public void writeTo(CodedOutputStream output) throws IOException {
      getUnknownFields().writeTo(output);
    }
    
    public int getSerializedSize() {
      int size = this.memoizedSize;
      if (size != -1)
        return size; 
      size = 0;
      size += getUnknownFields().getSerializedSize();
      this.memoizedSize = size;
      return size;
    }
    
    public boolean equals(Object obj) {
      if (obj == this)
        return true; 
      if (!(obj instanceof Close))
        return super.equals(obj); 
      Close other = (Close)obj;
      if (!getUnknownFields().equals(other.getUnknownFields()))
        return false; 
      return true;
    }
    
    public int hashCode() {
      if (this.memoizedHashCode != 0)
        return this.memoizedHashCode; 
      int hash = 41;
      hash = 19 * hash + getDescriptor().hashCode();
      hash = 29 * hash + getUnknownFields().hashCode();
      this.memoizedHashCode = hash;
      return hash;
    }
    
    public static Close parseFrom(ByteBuffer data) throws InvalidProtocolBufferException {
      return (Close)PARSER.parseFrom(data);
    }
    
    public static Close parseFrom(ByteBuffer data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
      return (Close)PARSER.parseFrom(data, extensionRegistry);
    }
    
    public static Close parseFrom(ByteString data) throws InvalidProtocolBufferException {
      return (Close)PARSER.parseFrom(data);
    }
    
    public static Close parseFrom(ByteString data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
      return (Close)PARSER.parseFrom(data, extensionRegistry);
    }
    
    public static Close parseFrom(byte[] data) throws InvalidProtocolBufferException {
      return (Close)PARSER.parseFrom(data);
    }
    
    public static Close parseFrom(byte[] data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
      return (Close)PARSER.parseFrom(data, extensionRegistry);
    }
    
    public static Close parseFrom(InputStream input) throws IOException {
      return 
        (Close)GeneratedMessageV3.parseWithIOException(PARSER, input);
    }
    
    public static Close parseFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
      return 
        (Close)GeneratedMessageV3.parseWithIOException(PARSER, input, extensionRegistry);
    }
    
    public static Close parseDelimitedFrom(InputStream input) throws IOException {
      return 
        (Close)GeneratedMessageV3.parseDelimitedWithIOException(PARSER, input);
    }
    
    public static Close parseDelimitedFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
      return 
        (Close)GeneratedMessageV3.parseDelimitedWithIOException(PARSER, input, extensionRegistry);
    }
    
    public static Close parseFrom(CodedInputStream input) throws IOException {
      return 
        (Close)GeneratedMessageV3.parseWithIOException(PARSER, input);
    }
    
    public static Close parseFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
      return 
        (Close)GeneratedMessageV3.parseWithIOException(PARSER, input, extensionRegistry);
    }
    
    public Builder newBuilderForType() {
      return newBuilder();
    }
    
    public static Builder newBuilder() {
      return DEFAULT_INSTANCE.toBuilder();
    }
    
    public static Builder newBuilder(Close prototype) {
      return DEFAULT_INSTANCE.toBuilder().mergeFrom(prototype);
    }
    
    public Builder toBuilder() {
      return (this == DEFAULT_INSTANCE) ? new Builder() : (new Builder())
        .mergeFrom(this);
    }
    
    protected Builder newBuilderForType(GeneratedMessageV3.BuilderParent parent) {
      Builder builder = new Builder(parent);
      return builder;
    }
    
    public static final class Builder extends GeneratedMessageV3.Builder<Builder> implements MysqlxExpect.CloseOrBuilder {
      public static final Descriptors.Descriptor getDescriptor() {
        return MysqlxExpect.internal_static_Mysqlx_Expect_Close_descriptor;
      }
      
      protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() {
        return MysqlxExpect.internal_static_Mysqlx_Expect_Close_fieldAccessorTable
          .ensureFieldAccessorsInitialized(MysqlxExpect.Close.class, Builder.class);
      }
      
      private Builder() {}
      
      private Builder(GeneratedMessageV3.BuilderParent parent) {
        super(parent);
      }
      
      public Builder clear() {
        super.clear();
        return this;
      }
      
      public Descriptors.Descriptor getDescriptorForType() {
        return MysqlxExpect.internal_static_Mysqlx_Expect_Close_descriptor;
      }
      
      public MysqlxExpect.Close getDefaultInstanceForType() {
        return MysqlxExpect.Close.getDefaultInstance();
      }
      
      public MysqlxExpect.Close build() {
        MysqlxExpect.Close result = buildPartial();
        if (!result.isInitialized())
          throw newUninitializedMessageException(result); 
        return result;
      }
      
      public MysqlxExpect.Close buildPartial() {
        MysqlxExpect.Close result = new MysqlxExpect.Close(this);
        onBuilt();
        return result;
      }
      
      public Builder clone() {
        return (Builder)super.clone();
      }
      
      public Builder setField(Descriptors.FieldDescriptor field, Object value) {
        return (Builder)super.setField(field, value);
      }
      
      public Builder clearField(Descriptors.FieldDescriptor field) {
        return (Builder)super.clearField(field);
      }
      
      public Builder clearOneof(Descriptors.OneofDescriptor oneof) {
        return (Builder)super.clearOneof(oneof);
      }
      
      public Builder setRepeatedField(Descriptors.FieldDescriptor field, int index, Object value) {
        return (Builder)super.setRepeatedField(field, index, value);
      }
      
      public Builder addRepeatedField(Descriptors.FieldDescriptor field, Object value) {
        return (Builder)super.addRepeatedField(field, value);
      }
      
      public Builder mergeFrom(Message other) {
        if (other instanceof MysqlxExpect.Close)
          return mergeFrom((MysqlxExpect.Close)other); 
        super.mergeFrom(other);
        return this;
      }
      
      public Builder mergeFrom(MysqlxExpect.Close other) {
        if (other == MysqlxExpect.Close.getDefaultInstance())
          return this; 
        mergeUnknownFields(other.getUnknownFields());
        onChanged();
        return this;
      }
      
      public final boolean isInitialized() {
        return true;
      }
      
      public Builder mergeFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
        if (extensionRegistry == null)
          throw new NullPointerException(); 
        try {
          boolean done = false;
          while (!done) {
            int tag = input.readTag();
            switch (tag) {
              case 0:
                done = true;
                continue;
            } 
            if (!parseUnknownField(input, extensionRegistry, tag))
              done = true; 
          } 
        } catch (InvalidProtocolBufferException e) {
          throw e.unwrapIOException();
        } finally {
          onChanged();
        } 
        return this;
      }
      
      public final Builder setUnknownFields(UnknownFieldSet unknownFields) {
        return (Builder)super.setUnknownFields(unknownFields);
      }
      
      public final Builder mergeUnknownFields(UnknownFieldSet unknownFields) {
        return (Builder)super.mergeUnknownFields(unknownFields);
      }
    }
    
    private static final Close DEFAULT_INSTANCE = new Close();
    
    public static Close getDefaultInstance() {
      return DEFAULT_INSTANCE;
    }
    
    @Deprecated
    public static final Parser<Close> PARSER = (Parser<Close>)new AbstractParser<Close>() {
        public MysqlxExpect.Close parsePartialFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
          MysqlxExpect.Close.Builder builder = MysqlxExpect.Close.newBuilder();
          try {
            builder.mergeFrom(input, extensionRegistry);
          } catch (InvalidProtocolBufferException e) {
            throw e.setUnfinishedMessage(builder.buildPartial());
          } catch (UninitializedMessageException e) {
            throw e.asInvalidProtocolBufferException().setUnfinishedMessage(builder.buildPartial());
          } catch (IOException e) {
            throw (new InvalidProtocolBufferException(e))
              .setUnfinishedMessage(builder.buildPartial());
          } 
          return builder.buildPartial();
        }
      };
    
    public static Parser<Close> parser() {
      return PARSER;
    }
    
    public Parser<Close> getParserForType() {
      return PARSER;
    }
    
    public Close getDefaultInstanceForType() {
      return DEFAULT_INSTANCE;
    }
  }
  
  public static Descriptors.FileDescriptor getDescriptor() {
    return descriptor;
  }
  
  static {
    String[] descriptorData = { "\n\023mysqlx_expect.proto\022\rMysqlx.Expect\032\fmysqlx.proto\"Ö\003\n\004Open\022B\n\002op\030\001 \001(\0162 .Mysqlx.Expect.Open.CtxOperation:\024EXPECT_CTX_COPY_PREV\022+\n\004cond\030\002 \003(\0132\035.Mysqlx.Expect.Open.Condition\032\002\n\tCondition\022\025\n\rcondition_key\030\001 \002(\r\022\027\n\017condition_value\030\002 \001(\f\022K\n\002op\030\003 \001(\01620.Mysqlx.Expect.Open.Condition.ConditionOperation:\rEXPECT_OP_SET\"N\n\003Key\022\023\n\017EXPECT_NO_ERROR\020\001\022\026\n\022EXPECT_FIELD_EXIST\020\002\022\032\n\026EXPECT_DOCID_GENERATED\020\003\"<\n\022ConditionOperation\022\021\n\rEXPECT_OP_SET\020\000\022\023\n\017EXPECT_OP_UNSET\020\001\">\n\fCtxOperation\022\030\n\024EXPECT_CTX_COPY_PREV\020\000\022\024\n\020EXPECT_CTX_EMPTY\020\001:\004ê0\030\"\r\n\005Close:\004ê0\031B\031\n\027com.mysql.cj.x.protobuf" };
    descriptor = Descriptors.FileDescriptor.internalBuildGeneratedFileFrom(descriptorData, new Descriptors.FileDescriptor[] { Mysqlx.getDescriptor() });
    internal_static_Mysqlx_Expect_Open_descriptor = getDescriptor().getMessageTypes().get(0);
    internal_static_Mysqlx_Expect_Open_fieldAccessorTable = new GeneratedMessageV3.FieldAccessorTable(internal_static_Mysqlx_Expect_Open_descriptor, new String[] { "Op", "Cond" });
    internal_static_Mysqlx_Expect_Open_Condition_descriptor = internal_static_Mysqlx_Expect_Open_descriptor.getNestedTypes().get(0);
    internal_static_Mysqlx_Expect_Open_Condition_fieldAccessorTable = new GeneratedMessageV3.FieldAccessorTable(internal_static_Mysqlx_Expect_Open_Condition_descriptor, new String[] { "ConditionKey", "ConditionValue", "Op" });
    internal_static_Mysqlx_Expect_Close_descriptor = getDescriptor().getMessageTypes().get(1);
    internal_static_Mysqlx_Expect_Close_fieldAccessorTable = new GeneratedMessageV3.FieldAccessorTable(internal_static_Mysqlx_Expect_Close_descriptor, new String[0]);
    ExtensionRegistry registry = ExtensionRegistry.newInstance();
    registry.add(Mysqlx.clientMessageId);
    Descriptors.FileDescriptor.internalUpdateFileDescriptor(descriptor, registry);
    Mysqlx.getDescriptor();
  }
}
