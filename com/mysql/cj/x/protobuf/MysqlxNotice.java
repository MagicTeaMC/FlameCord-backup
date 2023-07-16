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
import com.google.protobuf.SingleFieldBuilderV3;
import com.google.protobuf.UninitializedMessageException;
import com.google.protobuf.UnknownFieldSet;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class MysqlxNotice {
  private static final Descriptors.Descriptor internal_static_Mysqlx_Notice_Frame_descriptor;
  
  private static final GeneratedMessageV3.FieldAccessorTable internal_static_Mysqlx_Notice_Frame_fieldAccessorTable;
  
  private static final Descriptors.Descriptor internal_static_Mysqlx_Notice_Warning_descriptor;
  
  private static final GeneratedMessageV3.FieldAccessorTable internal_static_Mysqlx_Notice_Warning_fieldAccessorTable;
  
  private static final Descriptors.Descriptor internal_static_Mysqlx_Notice_SessionVariableChanged_descriptor;
  
  private static final GeneratedMessageV3.FieldAccessorTable internal_static_Mysqlx_Notice_SessionVariableChanged_fieldAccessorTable;
  
  private static final Descriptors.Descriptor internal_static_Mysqlx_Notice_SessionStateChanged_descriptor;
  
  private static final GeneratedMessageV3.FieldAccessorTable internal_static_Mysqlx_Notice_SessionStateChanged_fieldAccessorTable;
  
  private static final Descriptors.Descriptor internal_static_Mysqlx_Notice_GroupReplicationStateChanged_descriptor;
  
  private static final GeneratedMessageV3.FieldAccessorTable internal_static_Mysqlx_Notice_GroupReplicationStateChanged_fieldAccessorTable;
  
  private static final Descriptors.Descriptor internal_static_Mysqlx_Notice_ServerHello_descriptor;
  
  private static final GeneratedMessageV3.FieldAccessorTable internal_static_Mysqlx_Notice_ServerHello_fieldAccessorTable;
  
  private static Descriptors.FileDescriptor descriptor;
  
  public static void registerAllExtensions(ExtensionRegistryLite registry) {}
  
  public static void registerAllExtensions(ExtensionRegistry registry) {
    registerAllExtensions((ExtensionRegistryLite)registry);
  }
  
  public static interface FrameOrBuilder extends MessageOrBuilder {
    boolean hasType();
    
    int getType();
    
    boolean hasScope();
    
    MysqlxNotice.Frame.Scope getScope();
    
    boolean hasPayload();
    
    ByteString getPayload();
  }
  
  public static final class Frame extends GeneratedMessageV3 implements FrameOrBuilder {
    private static final long serialVersionUID = 0L;
    
    private int bitField0_;
    
    public static final int TYPE_FIELD_NUMBER = 1;
    
    private int type_;
    
    public static final int SCOPE_FIELD_NUMBER = 2;
    
    private int scope_;
    
    public static final int PAYLOAD_FIELD_NUMBER = 3;
    
    private ByteString payload_;
    
    private byte memoizedIsInitialized;
    
    private Frame(GeneratedMessageV3.Builder<?> builder) {
      super(builder);
      this.memoizedIsInitialized = -1;
    }
    
    private Frame() {
      this.memoizedIsInitialized = -1;
      this.scope_ = 1;
      this.payload_ = ByteString.EMPTY;
    }
    
    protected Object newInstance(GeneratedMessageV3.UnusedPrivateParameter unused) {
      return new Frame();
    }
    
    public final UnknownFieldSet getUnknownFields() {
      return this.unknownFields;
    }
    
    public static final Descriptors.Descriptor getDescriptor() {
      return MysqlxNotice.internal_static_Mysqlx_Notice_Frame_descriptor;
    }
    
    protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() {
      return MysqlxNotice.internal_static_Mysqlx_Notice_Frame_fieldAccessorTable.ensureFieldAccessorsInitialized(Frame.class, Builder.class);
    }
    
    public enum Scope implements ProtocolMessageEnum {
      GLOBAL(1),
      LOCAL(2);
      
      public static final int GLOBAL_VALUE = 1;
      
      public static final int LOCAL_VALUE = 2;
      
      private static final Internal.EnumLiteMap<Scope> internalValueMap = new Internal.EnumLiteMap<Scope>() {
          public MysqlxNotice.Frame.Scope findValueByNumber(int number) {
            return MysqlxNotice.Frame.Scope.forNumber(number);
          }
        };
      
      private static final Scope[] VALUES = values();
      
      private final int value;
      
      public final int getNumber() {
        return this.value;
      }
      
      public static Scope forNumber(int value) {
        switch (value) {
          case 1:
            return GLOBAL;
          case 2:
            return LOCAL;
        } 
        return null;
      }
      
      public static Internal.EnumLiteMap<Scope> internalGetValueMap() {
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
        return MysqlxNotice.Frame.getDescriptor().getEnumTypes().get(0);
      }
      
      Scope(int value) {
        this.value = value;
      }
    }
    
    public enum Type implements ProtocolMessageEnum {
      WARNING(1),
      SESSION_VARIABLE_CHANGED(2),
      SESSION_STATE_CHANGED(3),
      GROUP_REPLICATION_STATE_CHANGED(4),
      SERVER_HELLO(5);
      
      public static final int WARNING_VALUE = 1;
      
      public static final int SESSION_VARIABLE_CHANGED_VALUE = 2;
      
      public static final int SESSION_STATE_CHANGED_VALUE = 3;
      
      public static final int GROUP_REPLICATION_STATE_CHANGED_VALUE = 4;
      
      public static final int SERVER_HELLO_VALUE = 5;
      
      private static final Internal.EnumLiteMap<Type> internalValueMap = new Internal.EnumLiteMap<Type>() {
          public MysqlxNotice.Frame.Type findValueByNumber(int number) {
            return MysqlxNotice.Frame.Type.forNumber(number);
          }
        };
      
      private static final Type[] VALUES = values();
      
      private final int value;
      
      public final int getNumber() {
        return this.value;
      }
      
      public static Type forNumber(int value) {
        switch (value) {
          case 1:
            return WARNING;
          case 2:
            return SESSION_VARIABLE_CHANGED;
          case 3:
            return SESSION_STATE_CHANGED;
          case 4:
            return GROUP_REPLICATION_STATE_CHANGED;
          case 5:
            return SERVER_HELLO;
        } 
        return null;
      }
      
      public static Internal.EnumLiteMap<Type> internalGetValueMap() {
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
        return MysqlxNotice.Frame.getDescriptor().getEnumTypes().get(1);
      }
      
      Type(int value) {
        this.value = value;
      }
    }
    
    public boolean hasType() {
      return ((this.bitField0_ & 0x1) != 0);
    }
    
    public int getType() {
      return this.type_;
    }
    
    public boolean hasScope() {
      return ((this.bitField0_ & 0x2) != 0);
    }
    
    public Scope getScope() {
      Scope result = Scope.valueOf(this.scope_);
      return (result == null) ? Scope.GLOBAL : result;
    }
    
    public boolean hasPayload() {
      return ((this.bitField0_ & 0x4) != 0);
    }
    
    public ByteString getPayload() {
      return this.payload_;
    }
    
    public final boolean isInitialized() {
      byte isInitialized = this.memoizedIsInitialized;
      if (isInitialized == 1)
        return true; 
      if (isInitialized == 0)
        return false; 
      if (!hasType()) {
        this.memoizedIsInitialized = 0;
        return false;
      } 
      this.memoizedIsInitialized = 1;
      return true;
    }
    
    public void writeTo(CodedOutputStream output) throws IOException {
      if ((this.bitField0_ & 0x1) != 0)
        output.writeUInt32(1, this.type_); 
      if ((this.bitField0_ & 0x2) != 0)
        output.writeEnum(2, this.scope_); 
      if ((this.bitField0_ & 0x4) != 0)
        output.writeBytes(3, this.payload_); 
      getUnknownFields().writeTo(output);
    }
    
    public int getSerializedSize() {
      int size = this.memoizedSize;
      if (size != -1)
        return size; 
      size = 0;
      if ((this.bitField0_ & 0x1) != 0)
        size += 
          CodedOutputStream.computeUInt32Size(1, this.type_); 
      if ((this.bitField0_ & 0x2) != 0)
        size += 
          CodedOutputStream.computeEnumSize(2, this.scope_); 
      if ((this.bitField0_ & 0x4) != 0)
        size += 
          CodedOutputStream.computeBytesSize(3, this.payload_); 
      size += getUnknownFields().getSerializedSize();
      this.memoizedSize = size;
      return size;
    }
    
    public boolean equals(Object obj) {
      if (obj == this)
        return true; 
      if (!(obj instanceof Frame))
        return super.equals(obj); 
      Frame other = (Frame)obj;
      if (hasType() != other.hasType())
        return false; 
      if (hasType() && 
        getType() != other
        .getType())
        return false; 
      if (hasScope() != other.hasScope())
        return false; 
      if (hasScope() && 
        this.scope_ != other.scope_)
        return false; 
      if (hasPayload() != other.hasPayload())
        return false; 
      if (hasPayload() && 
        
        !getPayload().equals(other.getPayload()))
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
      if (hasType()) {
        hash = 37 * hash + 1;
        hash = 53 * hash + getType();
      } 
      if (hasScope()) {
        hash = 37 * hash + 2;
        hash = 53 * hash + this.scope_;
      } 
      if (hasPayload()) {
        hash = 37 * hash + 3;
        hash = 53 * hash + getPayload().hashCode();
      } 
      hash = 29 * hash + getUnknownFields().hashCode();
      this.memoizedHashCode = hash;
      return hash;
    }
    
    public static Frame parseFrom(ByteBuffer data) throws InvalidProtocolBufferException {
      return (Frame)PARSER.parseFrom(data);
    }
    
    public static Frame parseFrom(ByteBuffer data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
      return (Frame)PARSER.parseFrom(data, extensionRegistry);
    }
    
    public static Frame parseFrom(ByteString data) throws InvalidProtocolBufferException {
      return (Frame)PARSER.parseFrom(data);
    }
    
    public static Frame parseFrom(ByteString data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
      return (Frame)PARSER.parseFrom(data, extensionRegistry);
    }
    
    public static Frame parseFrom(byte[] data) throws InvalidProtocolBufferException {
      return (Frame)PARSER.parseFrom(data);
    }
    
    public static Frame parseFrom(byte[] data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
      return (Frame)PARSER.parseFrom(data, extensionRegistry);
    }
    
    public static Frame parseFrom(InputStream input) throws IOException {
      return 
        (Frame)GeneratedMessageV3.parseWithIOException(PARSER, input);
    }
    
    public static Frame parseFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
      return 
        (Frame)GeneratedMessageV3.parseWithIOException(PARSER, input, extensionRegistry);
    }
    
    public static Frame parseDelimitedFrom(InputStream input) throws IOException {
      return 
        (Frame)GeneratedMessageV3.parseDelimitedWithIOException(PARSER, input);
    }
    
    public static Frame parseDelimitedFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
      return 
        (Frame)GeneratedMessageV3.parseDelimitedWithIOException(PARSER, input, extensionRegistry);
    }
    
    public static Frame parseFrom(CodedInputStream input) throws IOException {
      return 
        (Frame)GeneratedMessageV3.parseWithIOException(PARSER, input);
    }
    
    public static Frame parseFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
      return 
        (Frame)GeneratedMessageV3.parseWithIOException(PARSER, input, extensionRegistry);
    }
    
    public Builder newBuilderForType() {
      return newBuilder();
    }
    
    public static Builder newBuilder() {
      return DEFAULT_INSTANCE.toBuilder();
    }
    
    public static Builder newBuilder(Frame prototype) {
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
    
    public static final class Builder extends GeneratedMessageV3.Builder<Builder> implements MysqlxNotice.FrameOrBuilder {
      private int bitField0_;
      
      private int type_;
      
      private int scope_;
      
      private ByteString payload_;
      
      public static final Descriptors.Descriptor getDescriptor() {
        return MysqlxNotice.internal_static_Mysqlx_Notice_Frame_descriptor;
      }
      
      protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() {
        return MysqlxNotice.internal_static_Mysqlx_Notice_Frame_fieldAccessorTable
          .ensureFieldAccessorsInitialized(MysqlxNotice.Frame.class, Builder.class);
      }
      
      private Builder() {
        this.scope_ = 1;
        this.payload_ = ByteString.EMPTY;
      }
      
      private Builder(GeneratedMessageV3.BuilderParent parent) {
        super(parent);
        this.scope_ = 1;
        this.payload_ = ByteString.EMPTY;
      }
      
      public Builder clear() {
        super.clear();
        this.type_ = 0;
        this.bitField0_ &= 0xFFFFFFFE;
        this.scope_ = 1;
        this.bitField0_ &= 0xFFFFFFFD;
        this.payload_ = ByteString.EMPTY;
        this.bitField0_ &= 0xFFFFFFFB;
        return this;
      }
      
      public Descriptors.Descriptor getDescriptorForType() {
        return MysqlxNotice.internal_static_Mysqlx_Notice_Frame_descriptor;
      }
      
      public MysqlxNotice.Frame getDefaultInstanceForType() {
        return MysqlxNotice.Frame.getDefaultInstance();
      }
      
      public MysqlxNotice.Frame build() {
        MysqlxNotice.Frame result = buildPartial();
        if (!result.isInitialized())
          throw newUninitializedMessageException(result); 
        return result;
      }
      
      public MysqlxNotice.Frame buildPartial() {
        MysqlxNotice.Frame result = new MysqlxNotice.Frame(this);
        int from_bitField0_ = this.bitField0_;
        int to_bitField0_ = 0;
        if ((from_bitField0_ & 0x1) != 0) {
          result.type_ = this.type_;
          to_bitField0_ |= 0x1;
        } 
        if ((from_bitField0_ & 0x2) != 0)
          to_bitField0_ |= 0x2; 
        result.scope_ = this.scope_;
        if ((from_bitField0_ & 0x4) != 0)
          to_bitField0_ |= 0x4; 
        result.payload_ = this.payload_;
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
        if (other instanceof MysqlxNotice.Frame)
          return mergeFrom((MysqlxNotice.Frame)other); 
        super.mergeFrom(other);
        return this;
      }
      
      public Builder mergeFrom(MysqlxNotice.Frame other) {
        if (other == MysqlxNotice.Frame.getDefaultInstance())
          return this; 
        if (other.hasType())
          setType(other.getType()); 
        if (other.hasScope())
          setScope(other.getScope()); 
        if (other.hasPayload())
          setPayload(other.getPayload()); 
        mergeUnknownFields(other.getUnknownFields());
        onChanged();
        return this;
      }
      
      public final boolean isInitialized() {
        if (!hasType())
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
            MysqlxNotice.Frame.Scope tmpValue;
            int tag = input.readTag();
            switch (tag) {
              case 0:
                done = true;
                continue;
              case 8:
                this.type_ = input.readUInt32();
                this.bitField0_ |= 0x1;
                continue;
              case 16:
                tmpRaw = input.readEnum();
                tmpValue = MysqlxNotice.Frame.Scope.forNumber(tmpRaw);
                if (tmpValue == null) {
                  mergeUnknownVarintField(2, tmpRaw);
                  continue;
                } 
                this.scope_ = tmpRaw;
                this.bitField0_ |= 0x2;
                continue;
              case 26:
                this.payload_ = input.readBytes();
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
      
      public boolean hasType() {
        return ((this.bitField0_ & 0x1) != 0);
      }
      
      public int getType() {
        return this.type_;
      }
      
      public Builder setType(int value) {
        this.bitField0_ |= 0x1;
        this.type_ = value;
        onChanged();
        return this;
      }
      
      public Builder clearType() {
        this.bitField0_ &= 0xFFFFFFFE;
        this.type_ = 0;
        onChanged();
        return this;
      }
      
      public boolean hasScope() {
        return ((this.bitField0_ & 0x2) != 0);
      }
      
      public MysqlxNotice.Frame.Scope getScope() {
        MysqlxNotice.Frame.Scope result = MysqlxNotice.Frame.Scope.valueOf(this.scope_);
        return (result == null) ? MysqlxNotice.Frame.Scope.GLOBAL : result;
      }
      
      public Builder setScope(MysqlxNotice.Frame.Scope value) {
        if (value == null)
          throw new NullPointerException(); 
        this.bitField0_ |= 0x2;
        this.scope_ = value.getNumber();
        onChanged();
        return this;
      }
      
      public Builder clearScope() {
        this.bitField0_ &= 0xFFFFFFFD;
        this.scope_ = 1;
        onChanged();
        return this;
      }
      
      public boolean hasPayload() {
        return ((this.bitField0_ & 0x4) != 0);
      }
      
      public ByteString getPayload() {
        return this.payload_;
      }
      
      public Builder setPayload(ByteString value) {
        if (value == null)
          throw new NullPointerException(); 
        this.bitField0_ |= 0x4;
        this.payload_ = value;
        onChanged();
        return this;
      }
      
      public Builder clearPayload() {
        this.bitField0_ &= 0xFFFFFFFB;
        this.payload_ = MysqlxNotice.Frame.getDefaultInstance().getPayload();
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
    
    private static final Frame DEFAULT_INSTANCE = new Frame();
    
    public static Frame getDefaultInstance() {
      return DEFAULT_INSTANCE;
    }
    
    @Deprecated
    public static final Parser<Frame> PARSER = (Parser<Frame>)new AbstractParser<Frame>() {
        public MysqlxNotice.Frame parsePartialFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
          MysqlxNotice.Frame.Builder builder = MysqlxNotice.Frame.newBuilder();
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
    
    public static Parser<Frame> parser() {
      return PARSER;
    }
    
    public Parser<Frame> getParserForType() {
      return PARSER;
    }
    
    public Frame getDefaultInstanceForType() {
      return DEFAULT_INSTANCE;
    }
  }
  
  public static interface WarningOrBuilder extends MessageOrBuilder {
    boolean hasLevel();
    
    MysqlxNotice.Warning.Level getLevel();
    
    boolean hasCode();
    
    int getCode();
    
    boolean hasMsg();
    
    String getMsg();
    
    ByteString getMsgBytes();
  }
  
  public static final class Warning extends GeneratedMessageV3 implements WarningOrBuilder {
    private static final long serialVersionUID = 0L;
    
    private int bitField0_;
    
    public static final int LEVEL_FIELD_NUMBER = 1;
    
    private int level_;
    
    public static final int CODE_FIELD_NUMBER = 2;
    
    private int code_;
    
    public static final int MSG_FIELD_NUMBER = 3;
    
    private volatile Object msg_;
    
    private byte memoizedIsInitialized;
    
    private Warning(GeneratedMessageV3.Builder<?> builder) {
      super(builder);
      this.memoizedIsInitialized = -1;
    }
    
    private Warning() {
      this.memoizedIsInitialized = -1;
      this.level_ = 2;
      this.msg_ = "";
    }
    
    protected Object newInstance(GeneratedMessageV3.UnusedPrivateParameter unused) {
      return new Warning();
    }
    
    public final UnknownFieldSet getUnknownFields() {
      return this.unknownFields;
    }
    
    public static final Descriptors.Descriptor getDescriptor() {
      return MysqlxNotice.internal_static_Mysqlx_Notice_Warning_descriptor;
    }
    
    protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() {
      return MysqlxNotice.internal_static_Mysqlx_Notice_Warning_fieldAccessorTable.ensureFieldAccessorsInitialized(Warning.class, Builder.class);
    }
    
    public enum Level implements ProtocolMessageEnum {
      NOTE(1),
      WARNING(2),
      ERROR(3);
      
      public static final int NOTE_VALUE = 1;
      
      public static final int WARNING_VALUE = 2;
      
      public static final int ERROR_VALUE = 3;
      
      private static final Internal.EnumLiteMap<Level> internalValueMap = new Internal.EnumLiteMap<Level>() {
          public MysqlxNotice.Warning.Level findValueByNumber(int number) {
            return MysqlxNotice.Warning.Level.forNumber(number);
          }
        };
      
      private static final Level[] VALUES = values();
      
      private final int value;
      
      public final int getNumber() {
        return this.value;
      }
      
      public static Level forNumber(int value) {
        switch (value) {
          case 1:
            return NOTE;
          case 2:
            return WARNING;
          case 3:
            return ERROR;
        } 
        return null;
      }
      
      public static Internal.EnumLiteMap<Level> internalGetValueMap() {
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
        return MysqlxNotice.Warning.getDescriptor().getEnumTypes().get(0);
      }
      
      Level(int value) {
        this.value = value;
      }
    }
    
    public boolean hasLevel() {
      return ((this.bitField0_ & 0x1) != 0);
    }
    
    public Level getLevel() {
      Level result = Level.valueOf(this.level_);
      return (result == null) ? Level.WARNING : result;
    }
    
    public boolean hasCode() {
      return ((this.bitField0_ & 0x2) != 0);
    }
    
    public int getCode() {
      return this.code_;
    }
    
    public boolean hasMsg() {
      return ((this.bitField0_ & 0x4) != 0);
    }
    
    public String getMsg() {
      Object ref = this.msg_;
      if (ref instanceof String)
        return (String)ref; 
      ByteString bs = (ByteString)ref;
      String s = bs.toStringUtf8();
      if (bs.isValidUtf8())
        this.msg_ = s; 
      return s;
    }
    
    public ByteString getMsgBytes() {
      Object ref = this.msg_;
      if (ref instanceof String) {
        ByteString b = ByteString.copyFromUtf8((String)ref);
        this.msg_ = b;
        return b;
      } 
      return (ByteString)ref;
    }
    
    public final boolean isInitialized() {
      byte isInitialized = this.memoizedIsInitialized;
      if (isInitialized == 1)
        return true; 
      if (isInitialized == 0)
        return false; 
      if (!hasCode()) {
        this.memoizedIsInitialized = 0;
        return false;
      } 
      if (!hasMsg()) {
        this.memoizedIsInitialized = 0;
        return false;
      } 
      this.memoizedIsInitialized = 1;
      return true;
    }
    
    public void writeTo(CodedOutputStream output) throws IOException {
      if ((this.bitField0_ & 0x1) != 0)
        output.writeEnum(1, this.level_); 
      if ((this.bitField0_ & 0x2) != 0)
        output.writeUInt32(2, this.code_); 
      if ((this.bitField0_ & 0x4) != 0)
        GeneratedMessageV3.writeString(output, 3, this.msg_); 
      getUnknownFields().writeTo(output);
    }
    
    public int getSerializedSize() {
      int size = this.memoizedSize;
      if (size != -1)
        return size; 
      size = 0;
      if ((this.bitField0_ & 0x1) != 0)
        size += 
          CodedOutputStream.computeEnumSize(1, this.level_); 
      if ((this.bitField0_ & 0x2) != 0)
        size += 
          CodedOutputStream.computeUInt32Size(2, this.code_); 
      if ((this.bitField0_ & 0x4) != 0)
        size += GeneratedMessageV3.computeStringSize(3, this.msg_); 
      size += getUnknownFields().getSerializedSize();
      this.memoizedSize = size;
      return size;
    }
    
    public boolean equals(Object obj) {
      if (obj == this)
        return true; 
      if (!(obj instanceof Warning))
        return super.equals(obj); 
      Warning other = (Warning)obj;
      if (hasLevel() != other.hasLevel())
        return false; 
      if (hasLevel() && 
        this.level_ != other.level_)
        return false; 
      if (hasCode() != other.hasCode())
        return false; 
      if (hasCode() && 
        getCode() != other
        .getCode())
        return false; 
      if (hasMsg() != other.hasMsg())
        return false; 
      if (hasMsg() && 
        
        !getMsg().equals(other.getMsg()))
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
      if (hasLevel()) {
        hash = 37 * hash + 1;
        hash = 53 * hash + this.level_;
      } 
      if (hasCode()) {
        hash = 37 * hash + 2;
        hash = 53 * hash + getCode();
      } 
      if (hasMsg()) {
        hash = 37 * hash + 3;
        hash = 53 * hash + getMsg().hashCode();
      } 
      hash = 29 * hash + getUnknownFields().hashCode();
      this.memoizedHashCode = hash;
      return hash;
    }
    
    public static Warning parseFrom(ByteBuffer data) throws InvalidProtocolBufferException {
      return (Warning)PARSER.parseFrom(data);
    }
    
    public static Warning parseFrom(ByteBuffer data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
      return (Warning)PARSER.parseFrom(data, extensionRegistry);
    }
    
    public static Warning parseFrom(ByteString data) throws InvalidProtocolBufferException {
      return (Warning)PARSER.parseFrom(data);
    }
    
    public static Warning parseFrom(ByteString data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
      return (Warning)PARSER.parseFrom(data, extensionRegistry);
    }
    
    public static Warning parseFrom(byte[] data) throws InvalidProtocolBufferException {
      return (Warning)PARSER.parseFrom(data);
    }
    
    public static Warning parseFrom(byte[] data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
      return (Warning)PARSER.parseFrom(data, extensionRegistry);
    }
    
    public static Warning parseFrom(InputStream input) throws IOException {
      return 
        (Warning)GeneratedMessageV3.parseWithIOException(PARSER, input);
    }
    
    public static Warning parseFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
      return 
        (Warning)GeneratedMessageV3.parseWithIOException(PARSER, input, extensionRegistry);
    }
    
    public static Warning parseDelimitedFrom(InputStream input) throws IOException {
      return 
        (Warning)GeneratedMessageV3.parseDelimitedWithIOException(PARSER, input);
    }
    
    public static Warning parseDelimitedFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
      return 
        (Warning)GeneratedMessageV3.parseDelimitedWithIOException(PARSER, input, extensionRegistry);
    }
    
    public static Warning parseFrom(CodedInputStream input) throws IOException {
      return 
        (Warning)GeneratedMessageV3.parseWithIOException(PARSER, input);
    }
    
    public static Warning parseFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
      return 
        (Warning)GeneratedMessageV3.parseWithIOException(PARSER, input, extensionRegistry);
    }
    
    public Builder newBuilderForType() {
      return newBuilder();
    }
    
    public static Builder newBuilder() {
      return DEFAULT_INSTANCE.toBuilder();
    }
    
    public static Builder newBuilder(Warning prototype) {
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
    
    public static final class Builder extends GeneratedMessageV3.Builder<Builder> implements MysqlxNotice.WarningOrBuilder {
      private int bitField0_;
      
      private int level_;
      
      private int code_;
      
      private Object msg_;
      
      public static final Descriptors.Descriptor getDescriptor() {
        return MysqlxNotice.internal_static_Mysqlx_Notice_Warning_descriptor;
      }
      
      protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() {
        return MysqlxNotice.internal_static_Mysqlx_Notice_Warning_fieldAccessorTable
          .ensureFieldAccessorsInitialized(MysqlxNotice.Warning.class, Builder.class);
      }
      
      private Builder() {
        this.level_ = 2;
        this.msg_ = "";
      }
      
      private Builder(GeneratedMessageV3.BuilderParent parent) {
        super(parent);
        this.level_ = 2;
        this.msg_ = "";
      }
      
      public Builder clear() {
        super.clear();
        this.level_ = 2;
        this.bitField0_ &= 0xFFFFFFFE;
        this.code_ = 0;
        this.bitField0_ &= 0xFFFFFFFD;
        this.msg_ = "";
        this.bitField0_ &= 0xFFFFFFFB;
        return this;
      }
      
      public Descriptors.Descriptor getDescriptorForType() {
        return MysqlxNotice.internal_static_Mysqlx_Notice_Warning_descriptor;
      }
      
      public MysqlxNotice.Warning getDefaultInstanceForType() {
        return MysqlxNotice.Warning.getDefaultInstance();
      }
      
      public MysqlxNotice.Warning build() {
        MysqlxNotice.Warning result = buildPartial();
        if (!result.isInitialized())
          throw newUninitializedMessageException(result); 
        return result;
      }
      
      public MysqlxNotice.Warning buildPartial() {
        MysqlxNotice.Warning result = new MysqlxNotice.Warning(this);
        int from_bitField0_ = this.bitField0_;
        int to_bitField0_ = 0;
        if ((from_bitField0_ & 0x1) != 0)
          to_bitField0_ |= 0x1; 
        result.level_ = this.level_;
        if ((from_bitField0_ & 0x2) != 0) {
          result.code_ = this.code_;
          to_bitField0_ |= 0x2;
        } 
        if ((from_bitField0_ & 0x4) != 0)
          to_bitField0_ |= 0x4; 
        result.msg_ = this.msg_;
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
        if (other instanceof MysqlxNotice.Warning)
          return mergeFrom((MysqlxNotice.Warning)other); 
        super.mergeFrom(other);
        return this;
      }
      
      public Builder mergeFrom(MysqlxNotice.Warning other) {
        if (other == MysqlxNotice.Warning.getDefaultInstance())
          return this; 
        if (other.hasLevel())
          setLevel(other.getLevel()); 
        if (other.hasCode())
          setCode(other.getCode()); 
        if (other.hasMsg()) {
          this.bitField0_ |= 0x4;
          this.msg_ = other.msg_;
          onChanged();
        } 
        mergeUnknownFields(other.getUnknownFields());
        onChanged();
        return this;
      }
      
      public final boolean isInitialized() {
        if (!hasCode())
          return false; 
        if (!hasMsg())
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
            MysqlxNotice.Warning.Level tmpValue;
            int tag = input.readTag();
            switch (tag) {
              case 0:
                done = true;
                continue;
              case 8:
                tmpRaw = input.readEnum();
                tmpValue = MysqlxNotice.Warning.Level.forNumber(tmpRaw);
                if (tmpValue == null) {
                  mergeUnknownVarintField(1, tmpRaw);
                  continue;
                } 
                this.level_ = tmpRaw;
                this.bitField0_ |= 0x1;
                continue;
              case 16:
                this.code_ = input.readUInt32();
                this.bitField0_ |= 0x2;
                continue;
              case 26:
                this.msg_ = input.readBytes();
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
      
      public boolean hasLevel() {
        return ((this.bitField0_ & 0x1) != 0);
      }
      
      public MysqlxNotice.Warning.Level getLevel() {
        MysqlxNotice.Warning.Level result = MysqlxNotice.Warning.Level.valueOf(this.level_);
        return (result == null) ? MysqlxNotice.Warning.Level.WARNING : result;
      }
      
      public Builder setLevel(MysqlxNotice.Warning.Level value) {
        if (value == null)
          throw new NullPointerException(); 
        this.bitField0_ |= 0x1;
        this.level_ = value.getNumber();
        onChanged();
        return this;
      }
      
      public Builder clearLevel() {
        this.bitField0_ &= 0xFFFFFFFE;
        this.level_ = 2;
        onChanged();
        return this;
      }
      
      public boolean hasCode() {
        return ((this.bitField0_ & 0x2) != 0);
      }
      
      public int getCode() {
        return this.code_;
      }
      
      public Builder setCode(int value) {
        this.bitField0_ |= 0x2;
        this.code_ = value;
        onChanged();
        return this;
      }
      
      public Builder clearCode() {
        this.bitField0_ &= 0xFFFFFFFD;
        this.code_ = 0;
        onChanged();
        return this;
      }
      
      public boolean hasMsg() {
        return ((this.bitField0_ & 0x4) != 0);
      }
      
      public String getMsg() {
        Object ref = this.msg_;
        if (!(ref instanceof String)) {
          ByteString bs = (ByteString)ref;
          String s = bs.toStringUtf8();
          if (bs.isValidUtf8())
            this.msg_ = s; 
          return s;
        } 
        return (String)ref;
      }
      
      public ByteString getMsgBytes() {
        Object ref = this.msg_;
        if (ref instanceof String) {
          ByteString b = ByteString.copyFromUtf8((String)ref);
          this.msg_ = b;
          return b;
        } 
        return (ByteString)ref;
      }
      
      public Builder setMsg(String value) {
        if (value == null)
          throw new NullPointerException(); 
        this.bitField0_ |= 0x4;
        this.msg_ = value;
        onChanged();
        return this;
      }
      
      public Builder clearMsg() {
        this.bitField0_ &= 0xFFFFFFFB;
        this.msg_ = MysqlxNotice.Warning.getDefaultInstance().getMsg();
        onChanged();
        return this;
      }
      
      public Builder setMsgBytes(ByteString value) {
        if (value == null)
          throw new NullPointerException(); 
        this.bitField0_ |= 0x4;
        this.msg_ = value;
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
    
    private static final Warning DEFAULT_INSTANCE = new Warning();
    
    public static Warning getDefaultInstance() {
      return DEFAULT_INSTANCE;
    }
    
    @Deprecated
    public static final Parser<Warning> PARSER = (Parser<Warning>)new AbstractParser<Warning>() {
        public MysqlxNotice.Warning parsePartialFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
          MysqlxNotice.Warning.Builder builder = MysqlxNotice.Warning.newBuilder();
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
    
    public static Parser<Warning> parser() {
      return PARSER;
    }
    
    public Parser<Warning> getParserForType() {
      return PARSER;
    }
    
    public Warning getDefaultInstanceForType() {
      return DEFAULT_INSTANCE;
    }
  }
  
  public static interface SessionVariableChangedOrBuilder extends MessageOrBuilder {
    boolean hasParam();
    
    String getParam();
    
    ByteString getParamBytes();
    
    boolean hasValue();
    
    MysqlxDatatypes.Scalar getValue();
    
    MysqlxDatatypes.ScalarOrBuilder getValueOrBuilder();
  }
  
  public static final class SessionVariableChanged extends GeneratedMessageV3 implements SessionVariableChangedOrBuilder {
    private static final long serialVersionUID = 0L;
    
    private int bitField0_;
    
    public static final int PARAM_FIELD_NUMBER = 1;
    
    private volatile Object param_;
    
    public static final int VALUE_FIELD_NUMBER = 2;
    
    private MysqlxDatatypes.Scalar value_;
    
    private byte memoizedIsInitialized;
    
    private SessionVariableChanged(GeneratedMessageV3.Builder<?> builder) {
      super(builder);
      this.memoizedIsInitialized = -1;
    }
    
    private SessionVariableChanged() {
      this.memoizedIsInitialized = -1;
      this.param_ = "";
    }
    
    protected Object newInstance(GeneratedMessageV3.UnusedPrivateParameter unused) {
      return new SessionVariableChanged();
    }
    
    public final UnknownFieldSet getUnknownFields() {
      return this.unknownFields;
    }
    
    public static final Descriptors.Descriptor getDescriptor() {
      return MysqlxNotice.internal_static_Mysqlx_Notice_SessionVariableChanged_descriptor;
    }
    
    protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() {
      return MysqlxNotice.internal_static_Mysqlx_Notice_SessionVariableChanged_fieldAccessorTable.ensureFieldAccessorsInitialized(SessionVariableChanged.class, Builder.class);
    }
    
    public boolean hasParam() {
      return ((this.bitField0_ & 0x1) != 0);
    }
    
    public String getParam() {
      Object ref = this.param_;
      if (ref instanceof String)
        return (String)ref; 
      ByteString bs = (ByteString)ref;
      String s = bs.toStringUtf8();
      if (bs.isValidUtf8())
        this.param_ = s; 
      return s;
    }
    
    public ByteString getParamBytes() {
      Object ref = this.param_;
      if (ref instanceof String) {
        ByteString b = ByteString.copyFromUtf8((String)ref);
        this.param_ = b;
        return b;
      } 
      return (ByteString)ref;
    }
    
    public boolean hasValue() {
      return ((this.bitField0_ & 0x2) != 0);
    }
    
    public MysqlxDatatypes.Scalar getValue() {
      return (this.value_ == null) ? MysqlxDatatypes.Scalar.getDefaultInstance() : this.value_;
    }
    
    public MysqlxDatatypes.ScalarOrBuilder getValueOrBuilder() {
      return (this.value_ == null) ? MysqlxDatatypes.Scalar.getDefaultInstance() : this.value_;
    }
    
    public final boolean isInitialized() {
      byte isInitialized = this.memoizedIsInitialized;
      if (isInitialized == 1)
        return true; 
      if (isInitialized == 0)
        return false; 
      if (!hasParam()) {
        this.memoizedIsInitialized = 0;
        return false;
      } 
      if (hasValue() && 
        !getValue().isInitialized()) {
        this.memoizedIsInitialized = 0;
        return false;
      } 
      this.memoizedIsInitialized = 1;
      return true;
    }
    
    public void writeTo(CodedOutputStream output) throws IOException {
      if ((this.bitField0_ & 0x1) != 0)
        GeneratedMessageV3.writeString(output, 1, this.param_); 
      if ((this.bitField0_ & 0x2) != 0)
        output.writeMessage(2, (MessageLite)getValue()); 
      getUnknownFields().writeTo(output);
    }
    
    public int getSerializedSize() {
      int size = this.memoizedSize;
      if (size != -1)
        return size; 
      size = 0;
      if ((this.bitField0_ & 0x1) != 0)
        size += GeneratedMessageV3.computeStringSize(1, this.param_); 
      if ((this.bitField0_ & 0x2) != 0)
        size += 
          CodedOutputStream.computeMessageSize(2, (MessageLite)getValue()); 
      size += getUnknownFields().getSerializedSize();
      this.memoizedSize = size;
      return size;
    }
    
    public boolean equals(Object obj) {
      if (obj == this)
        return true; 
      if (!(obj instanceof SessionVariableChanged))
        return super.equals(obj); 
      SessionVariableChanged other = (SessionVariableChanged)obj;
      if (hasParam() != other.hasParam())
        return false; 
      if (hasParam() && 
        
        !getParam().equals(other.getParam()))
        return false; 
      if (hasValue() != other.hasValue())
        return false; 
      if (hasValue() && 
        
        !getValue().equals(other.getValue()))
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
      if (hasParam()) {
        hash = 37 * hash + 1;
        hash = 53 * hash + getParam().hashCode();
      } 
      if (hasValue()) {
        hash = 37 * hash + 2;
        hash = 53 * hash + getValue().hashCode();
      } 
      hash = 29 * hash + getUnknownFields().hashCode();
      this.memoizedHashCode = hash;
      return hash;
    }
    
    public static SessionVariableChanged parseFrom(ByteBuffer data) throws InvalidProtocolBufferException {
      return (SessionVariableChanged)PARSER.parseFrom(data);
    }
    
    public static SessionVariableChanged parseFrom(ByteBuffer data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
      return (SessionVariableChanged)PARSER.parseFrom(data, extensionRegistry);
    }
    
    public static SessionVariableChanged parseFrom(ByteString data) throws InvalidProtocolBufferException {
      return (SessionVariableChanged)PARSER.parseFrom(data);
    }
    
    public static SessionVariableChanged parseFrom(ByteString data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
      return (SessionVariableChanged)PARSER.parseFrom(data, extensionRegistry);
    }
    
    public static SessionVariableChanged parseFrom(byte[] data) throws InvalidProtocolBufferException {
      return (SessionVariableChanged)PARSER.parseFrom(data);
    }
    
    public static SessionVariableChanged parseFrom(byte[] data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
      return (SessionVariableChanged)PARSER.parseFrom(data, extensionRegistry);
    }
    
    public static SessionVariableChanged parseFrom(InputStream input) throws IOException {
      return 
        (SessionVariableChanged)GeneratedMessageV3.parseWithIOException(PARSER, input);
    }
    
    public static SessionVariableChanged parseFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
      return 
        (SessionVariableChanged)GeneratedMessageV3.parseWithIOException(PARSER, input, extensionRegistry);
    }
    
    public static SessionVariableChanged parseDelimitedFrom(InputStream input) throws IOException {
      return 
        (SessionVariableChanged)GeneratedMessageV3.parseDelimitedWithIOException(PARSER, input);
    }
    
    public static SessionVariableChanged parseDelimitedFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
      return 
        (SessionVariableChanged)GeneratedMessageV3.parseDelimitedWithIOException(PARSER, input, extensionRegistry);
    }
    
    public static SessionVariableChanged parseFrom(CodedInputStream input) throws IOException {
      return 
        (SessionVariableChanged)GeneratedMessageV3.parseWithIOException(PARSER, input);
    }
    
    public static SessionVariableChanged parseFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
      return 
        (SessionVariableChanged)GeneratedMessageV3.parseWithIOException(PARSER, input, extensionRegistry);
    }
    
    public Builder newBuilderForType() {
      return newBuilder();
    }
    
    public static Builder newBuilder() {
      return DEFAULT_INSTANCE.toBuilder();
    }
    
    public static Builder newBuilder(SessionVariableChanged prototype) {
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
    
    public static final class Builder extends GeneratedMessageV3.Builder<Builder> implements MysqlxNotice.SessionVariableChangedOrBuilder {
      private int bitField0_;
      
      private Object param_;
      
      private MysqlxDatatypes.Scalar value_;
      
      private SingleFieldBuilderV3<MysqlxDatatypes.Scalar, MysqlxDatatypes.Scalar.Builder, MysqlxDatatypes.ScalarOrBuilder> valueBuilder_;
      
      public static final Descriptors.Descriptor getDescriptor() {
        return MysqlxNotice.internal_static_Mysqlx_Notice_SessionVariableChanged_descriptor;
      }
      
      protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() {
        return MysqlxNotice.internal_static_Mysqlx_Notice_SessionVariableChanged_fieldAccessorTable
          .ensureFieldAccessorsInitialized(MysqlxNotice.SessionVariableChanged.class, Builder.class);
      }
      
      private Builder() {
        this.param_ = "";
        maybeForceBuilderInitialization();
      }
      
      private Builder(GeneratedMessageV3.BuilderParent parent) {
        super(parent);
        this.param_ = "";
        maybeForceBuilderInitialization();
      }
      
      private void maybeForceBuilderInitialization() {
        if (MysqlxNotice.SessionVariableChanged.alwaysUseFieldBuilders)
          getValueFieldBuilder(); 
      }
      
      public Builder clear() {
        super.clear();
        this.param_ = "";
        this.bitField0_ &= 0xFFFFFFFE;
        if (this.valueBuilder_ == null) {
          this.value_ = null;
        } else {
          this.valueBuilder_.clear();
        } 
        this.bitField0_ &= 0xFFFFFFFD;
        return this;
      }
      
      public Descriptors.Descriptor getDescriptorForType() {
        return MysqlxNotice.internal_static_Mysqlx_Notice_SessionVariableChanged_descriptor;
      }
      
      public MysqlxNotice.SessionVariableChanged getDefaultInstanceForType() {
        return MysqlxNotice.SessionVariableChanged.getDefaultInstance();
      }
      
      public MysqlxNotice.SessionVariableChanged build() {
        MysqlxNotice.SessionVariableChanged result = buildPartial();
        if (!result.isInitialized())
          throw newUninitializedMessageException(result); 
        return result;
      }
      
      public MysqlxNotice.SessionVariableChanged buildPartial() {
        MysqlxNotice.SessionVariableChanged result = new MysqlxNotice.SessionVariableChanged(this);
        int from_bitField0_ = this.bitField0_;
        int to_bitField0_ = 0;
        if ((from_bitField0_ & 0x1) != 0)
          to_bitField0_ |= 0x1; 
        result.param_ = this.param_;
        if ((from_bitField0_ & 0x2) != 0) {
          if (this.valueBuilder_ == null) {
            result.value_ = this.value_;
          } else {
            result.value_ = (MysqlxDatatypes.Scalar)this.valueBuilder_.build();
          } 
          to_bitField0_ |= 0x2;
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
        if (other instanceof MysqlxNotice.SessionVariableChanged)
          return mergeFrom((MysqlxNotice.SessionVariableChanged)other); 
        super.mergeFrom(other);
        return this;
      }
      
      public Builder mergeFrom(MysqlxNotice.SessionVariableChanged other) {
        if (other == MysqlxNotice.SessionVariableChanged.getDefaultInstance())
          return this; 
        if (other.hasParam()) {
          this.bitField0_ |= 0x1;
          this.param_ = other.param_;
          onChanged();
        } 
        if (other.hasValue())
          mergeValue(other.getValue()); 
        mergeUnknownFields(other.getUnknownFields());
        onChanged();
        return this;
      }
      
      public final boolean isInitialized() {
        if (!hasParam())
          return false; 
        if (hasValue() && !getValue().isInitialized())
          return false; 
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
              case 10:
                this.param_ = input.readBytes();
                this.bitField0_ |= 0x1;
                continue;
              case 18:
                input.readMessage((MessageLite.Builder)getValueFieldBuilder().getBuilder(), extensionRegistry);
                this.bitField0_ |= 0x2;
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
      
      public boolean hasParam() {
        return ((this.bitField0_ & 0x1) != 0);
      }
      
      public String getParam() {
        Object ref = this.param_;
        if (!(ref instanceof String)) {
          ByteString bs = (ByteString)ref;
          String s = bs.toStringUtf8();
          if (bs.isValidUtf8())
            this.param_ = s; 
          return s;
        } 
        return (String)ref;
      }
      
      public ByteString getParamBytes() {
        Object ref = this.param_;
        if (ref instanceof String) {
          ByteString b = ByteString.copyFromUtf8((String)ref);
          this.param_ = b;
          return b;
        } 
        return (ByteString)ref;
      }
      
      public Builder setParam(String value) {
        if (value == null)
          throw new NullPointerException(); 
        this.bitField0_ |= 0x1;
        this.param_ = value;
        onChanged();
        return this;
      }
      
      public Builder clearParam() {
        this.bitField0_ &= 0xFFFFFFFE;
        this.param_ = MysqlxNotice.SessionVariableChanged.getDefaultInstance().getParam();
        onChanged();
        return this;
      }
      
      public Builder setParamBytes(ByteString value) {
        if (value == null)
          throw new NullPointerException(); 
        this.bitField0_ |= 0x1;
        this.param_ = value;
        onChanged();
        return this;
      }
      
      public boolean hasValue() {
        return ((this.bitField0_ & 0x2) != 0);
      }
      
      public MysqlxDatatypes.Scalar getValue() {
        if (this.valueBuilder_ == null)
          return (this.value_ == null) ? MysqlxDatatypes.Scalar.getDefaultInstance() : this.value_; 
        return (MysqlxDatatypes.Scalar)this.valueBuilder_.getMessage();
      }
      
      public Builder setValue(MysqlxDatatypes.Scalar value) {
        if (this.valueBuilder_ == null) {
          if (value == null)
            throw new NullPointerException(); 
          this.value_ = value;
          onChanged();
        } else {
          this.valueBuilder_.setMessage((AbstractMessage)value);
        } 
        this.bitField0_ |= 0x2;
        return this;
      }
      
      public Builder setValue(MysqlxDatatypes.Scalar.Builder builderForValue) {
        if (this.valueBuilder_ == null) {
          this.value_ = builderForValue.build();
          onChanged();
        } else {
          this.valueBuilder_.setMessage((AbstractMessage)builderForValue.build());
        } 
        this.bitField0_ |= 0x2;
        return this;
      }
      
      public Builder mergeValue(MysqlxDatatypes.Scalar value) {
        if (this.valueBuilder_ == null) {
          if ((this.bitField0_ & 0x2) != 0 && this.value_ != null && this.value_ != 
            
            MysqlxDatatypes.Scalar.getDefaultInstance()) {
            this
              .value_ = MysqlxDatatypes.Scalar.newBuilder(this.value_).mergeFrom(value).buildPartial();
          } else {
            this.value_ = value;
          } 
          onChanged();
        } else {
          this.valueBuilder_.mergeFrom((AbstractMessage)value);
        } 
        this.bitField0_ |= 0x2;
        return this;
      }
      
      public Builder clearValue() {
        if (this.valueBuilder_ == null) {
          this.value_ = null;
          onChanged();
        } else {
          this.valueBuilder_.clear();
        } 
        this.bitField0_ &= 0xFFFFFFFD;
        return this;
      }
      
      public MysqlxDatatypes.Scalar.Builder getValueBuilder() {
        this.bitField0_ |= 0x2;
        onChanged();
        return (MysqlxDatatypes.Scalar.Builder)getValueFieldBuilder().getBuilder();
      }
      
      public MysqlxDatatypes.ScalarOrBuilder getValueOrBuilder() {
        if (this.valueBuilder_ != null)
          return (MysqlxDatatypes.ScalarOrBuilder)this.valueBuilder_.getMessageOrBuilder(); 
        return (this.value_ == null) ? 
          MysqlxDatatypes.Scalar.getDefaultInstance() : this.value_;
      }
      
      private SingleFieldBuilderV3<MysqlxDatatypes.Scalar, MysqlxDatatypes.Scalar.Builder, MysqlxDatatypes.ScalarOrBuilder> getValueFieldBuilder() {
        if (this.valueBuilder_ == null) {
          this
            
            .valueBuilder_ = new SingleFieldBuilderV3((AbstractMessage)getValue(), (AbstractMessage.BuilderParent)getParentForChildren(), isClean());
          this.value_ = null;
        } 
        return this.valueBuilder_;
      }
      
      public final Builder setUnknownFields(UnknownFieldSet unknownFields) {
        return (Builder)super.setUnknownFields(unknownFields);
      }
      
      public final Builder mergeUnknownFields(UnknownFieldSet unknownFields) {
        return (Builder)super.mergeUnknownFields(unknownFields);
      }
    }
    
    private static final SessionVariableChanged DEFAULT_INSTANCE = new SessionVariableChanged();
    
    public static SessionVariableChanged getDefaultInstance() {
      return DEFAULT_INSTANCE;
    }
    
    @Deprecated
    public static final Parser<SessionVariableChanged> PARSER = (Parser<SessionVariableChanged>)new AbstractParser<SessionVariableChanged>() {
        public MysqlxNotice.SessionVariableChanged parsePartialFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
          MysqlxNotice.SessionVariableChanged.Builder builder = MysqlxNotice.SessionVariableChanged.newBuilder();
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
    
    public static Parser<SessionVariableChanged> parser() {
      return PARSER;
    }
    
    public Parser<SessionVariableChanged> getParserForType() {
      return PARSER;
    }
    
    public SessionVariableChanged getDefaultInstanceForType() {
      return DEFAULT_INSTANCE;
    }
  }
  
  public static interface SessionStateChangedOrBuilder extends MessageOrBuilder {
    boolean hasParam();
    
    MysqlxNotice.SessionStateChanged.Parameter getParam();
    
    List<MysqlxDatatypes.Scalar> getValueList();
    
    MysqlxDatatypes.Scalar getValue(int param1Int);
    
    int getValueCount();
    
    List<? extends MysqlxDatatypes.ScalarOrBuilder> getValueOrBuilderList();
    
    MysqlxDatatypes.ScalarOrBuilder getValueOrBuilder(int param1Int);
  }
  
  public static final class SessionStateChanged extends GeneratedMessageV3 implements SessionStateChangedOrBuilder {
    private static final long serialVersionUID = 0L;
    
    private int bitField0_;
    
    public static final int PARAM_FIELD_NUMBER = 1;
    
    private int param_;
    
    public static final int VALUE_FIELD_NUMBER = 2;
    
    private List<MysqlxDatatypes.Scalar> value_;
    
    private byte memoizedIsInitialized;
    
    private SessionStateChanged(GeneratedMessageV3.Builder<?> builder) {
      super(builder);
      this.memoizedIsInitialized = -1;
    }
    
    private SessionStateChanged() {
      this.memoizedIsInitialized = -1;
      this.param_ = 1;
      this.value_ = Collections.emptyList();
    }
    
    protected Object newInstance(GeneratedMessageV3.UnusedPrivateParameter unused) {
      return new SessionStateChanged();
    }
    
    public final UnknownFieldSet getUnknownFields() {
      return this.unknownFields;
    }
    
    public static final Descriptors.Descriptor getDescriptor() {
      return MysqlxNotice.internal_static_Mysqlx_Notice_SessionStateChanged_descriptor;
    }
    
    protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() {
      return MysqlxNotice.internal_static_Mysqlx_Notice_SessionStateChanged_fieldAccessorTable.ensureFieldAccessorsInitialized(SessionStateChanged.class, Builder.class);
    }
    
    public enum Parameter implements ProtocolMessageEnum {
      CURRENT_SCHEMA(1),
      ACCOUNT_EXPIRED(2),
      GENERATED_INSERT_ID(3),
      ROWS_AFFECTED(4),
      ROWS_FOUND(5),
      ROWS_MATCHED(6),
      TRX_COMMITTED(7),
      TRX_ROLLEDBACK(9),
      PRODUCED_MESSAGE(10),
      CLIENT_ID_ASSIGNED(11),
      GENERATED_DOCUMENT_IDS(12);
      
      public static final int CURRENT_SCHEMA_VALUE = 1;
      
      public static final int ACCOUNT_EXPIRED_VALUE = 2;
      
      public static final int GENERATED_INSERT_ID_VALUE = 3;
      
      public static final int ROWS_AFFECTED_VALUE = 4;
      
      public static final int ROWS_FOUND_VALUE = 5;
      
      public static final int ROWS_MATCHED_VALUE = 6;
      
      public static final int TRX_COMMITTED_VALUE = 7;
      
      public static final int TRX_ROLLEDBACK_VALUE = 9;
      
      public static final int PRODUCED_MESSAGE_VALUE = 10;
      
      public static final int CLIENT_ID_ASSIGNED_VALUE = 11;
      
      public static final int GENERATED_DOCUMENT_IDS_VALUE = 12;
      
      private static final Internal.EnumLiteMap<Parameter> internalValueMap = new Internal.EnumLiteMap<Parameter>() {
          public MysqlxNotice.SessionStateChanged.Parameter findValueByNumber(int number) {
            return MysqlxNotice.SessionStateChanged.Parameter.forNumber(number);
          }
        };
      
      private static final Parameter[] VALUES = values();
      
      private final int value;
      
      public final int getNumber() {
        return this.value;
      }
      
      public static Parameter forNumber(int value) {
        switch (value) {
          case 1:
            return CURRENT_SCHEMA;
          case 2:
            return ACCOUNT_EXPIRED;
          case 3:
            return GENERATED_INSERT_ID;
          case 4:
            return ROWS_AFFECTED;
          case 5:
            return ROWS_FOUND;
          case 6:
            return ROWS_MATCHED;
          case 7:
            return TRX_COMMITTED;
          case 9:
            return TRX_ROLLEDBACK;
          case 10:
            return PRODUCED_MESSAGE;
          case 11:
            return CLIENT_ID_ASSIGNED;
          case 12:
            return GENERATED_DOCUMENT_IDS;
        } 
        return null;
      }
      
      public static Internal.EnumLiteMap<Parameter> internalGetValueMap() {
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
        return MysqlxNotice.SessionStateChanged.getDescriptor().getEnumTypes().get(0);
      }
      
      Parameter(int value) {
        this.value = value;
      }
    }
    
    public boolean hasParam() {
      return ((this.bitField0_ & 0x1) != 0);
    }
    
    public Parameter getParam() {
      Parameter result = Parameter.valueOf(this.param_);
      return (result == null) ? Parameter.CURRENT_SCHEMA : result;
    }
    
    public List<MysqlxDatatypes.Scalar> getValueList() {
      return this.value_;
    }
    
    public List<? extends MysqlxDatatypes.ScalarOrBuilder> getValueOrBuilderList() {
      return (List)this.value_;
    }
    
    public int getValueCount() {
      return this.value_.size();
    }
    
    public MysqlxDatatypes.Scalar getValue(int index) {
      return this.value_.get(index);
    }
    
    public MysqlxDatatypes.ScalarOrBuilder getValueOrBuilder(int index) {
      return this.value_.get(index);
    }
    
    public final boolean isInitialized() {
      byte isInitialized = this.memoizedIsInitialized;
      if (isInitialized == 1)
        return true; 
      if (isInitialized == 0)
        return false; 
      if (!hasParam()) {
        this.memoizedIsInitialized = 0;
        return false;
      } 
      for (int i = 0; i < getValueCount(); i++) {
        if (!getValue(i).isInitialized()) {
          this.memoizedIsInitialized = 0;
          return false;
        } 
      } 
      this.memoizedIsInitialized = 1;
      return true;
    }
    
    public void writeTo(CodedOutputStream output) throws IOException {
      if ((this.bitField0_ & 0x1) != 0)
        output.writeEnum(1, this.param_); 
      for (int i = 0; i < this.value_.size(); i++)
        output.writeMessage(2, (MessageLite)this.value_.get(i)); 
      getUnknownFields().writeTo(output);
    }
    
    public int getSerializedSize() {
      int size = this.memoizedSize;
      if (size != -1)
        return size; 
      size = 0;
      if ((this.bitField0_ & 0x1) != 0)
        size += 
          CodedOutputStream.computeEnumSize(1, this.param_); 
      for (int i = 0; i < this.value_.size(); i++)
        size += 
          CodedOutputStream.computeMessageSize(2, (MessageLite)this.value_.get(i)); 
      size += getUnknownFields().getSerializedSize();
      this.memoizedSize = size;
      return size;
    }
    
    public boolean equals(Object obj) {
      if (obj == this)
        return true; 
      if (!(obj instanceof SessionStateChanged))
        return super.equals(obj); 
      SessionStateChanged other = (SessionStateChanged)obj;
      if (hasParam() != other.hasParam())
        return false; 
      if (hasParam() && 
        this.param_ != other.param_)
        return false; 
      if (!getValueList().equals(other.getValueList()))
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
      if (hasParam()) {
        hash = 37 * hash + 1;
        hash = 53 * hash + this.param_;
      } 
      if (getValueCount() > 0) {
        hash = 37 * hash + 2;
        hash = 53 * hash + getValueList().hashCode();
      } 
      hash = 29 * hash + getUnknownFields().hashCode();
      this.memoizedHashCode = hash;
      return hash;
    }
    
    public static SessionStateChanged parseFrom(ByteBuffer data) throws InvalidProtocolBufferException {
      return (SessionStateChanged)PARSER.parseFrom(data);
    }
    
    public static SessionStateChanged parseFrom(ByteBuffer data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
      return (SessionStateChanged)PARSER.parseFrom(data, extensionRegistry);
    }
    
    public static SessionStateChanged parseFrom(ByteString data) throws InvalidProtocolBufferException {
      return (SessionStateChanged)PARSER.parseFrom(data);
    }
    
    public static SessionStateChanged parseFrom(ByteString data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
      return (SessionStateChanged)PARSER.parseFrom(data, extensionRegistry);
    }
    
    public static SessionStateChanged parseFrom(byte[] data) throws InvalidProtocolBufferException {
      return (SessionStateChanged)PARSER.parseFrom(data);
    }
    
    public static SessionStateChanged parseFrom(byte[] data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
      return (SessionStateChanged)PARSER.parseFrom(data, extensionRegistry);
    }
    
    public static SessionStateChanged parseFrom(InputStream input) throws IOException {
      return 
        (SessionStateChanged)GeneratedMessageV3.parseWithIOException(PARSER, input);
    }
    
    public static SessionStateChanged parseFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
      return 
        (SessionStateChanged)GeneratedMessageV3.parseWithIOException(PARSER, input, extensionRegistry);
    }
    
    public static SessionStateChanged parseDelimitedFrom(InputStream input) throws IOException {
      return 
        (SessionStateChanged)GeneratedMessageV3.parseDelimitedWithIOException(PARSER, input);
    }
    
    public static SessionStateChanged parseDelimitedFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
      return 
        (SessionStateChanged)GeneratedMessageV3.parseDelimitedWithIOException(PARSER, input, extensionRegistry);
    }
    
    public static SessionStateChanged parseFrom(CodedInputStream input) throws IOException {
      return 
        (SessionStateChanged)GeneratedMessageV3.parseWithIOException(PARSER, input);
    }
    
    public static SessionStateChanged parseFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
      return 
        (SessionStateChanged)GeneratedMessageV3.parseWithIOException(PARSER, input, extensionRegistry);
    }
    
    public Builder newBuilderForType() {
      return newBuilder();
    }
    
    public static Builder newBuilder() {
      return DEFAULT_INSTANCE.toBuilder();
    }
    
    public static Builder newBuilder(SessionStateChanged prototype) {
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
    
    public static final class Builder extends GeneratedMessageV3.Builder<Builder> implements MysqlxNotice.SessionStateChangedOrBuilder {
      private int bitField0_;
      
      private int param_;
      
      private List<MysqlxDatatypes.Scalar> value_;
      
      private RepeatedFieldBuilderV3<MysqlxDatatypes.Scalar, MysqlxDatatypes.Scalar.Builder, MysqlxDatatypes.ScalarOrBuilder> valueBuilder_;
      
      public static final Descriptors.Descriptor getDescriptor() {
        return MysqlxNotice.internal_static_Mysqlx_Notice_SessionStateChanged_descriptor;
      }
      
      protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() {
        return MysqlxNotice.internal_static_Mysqlx_Notice_SessionStateChanged_fieldAccessorTable
          .ensureFieldAccessorsInitialized(MysqlxNotice.SessionStateChanged.class, Builder.class);
      }
      
      private Builder() {
        this.param_ = 1;
        this
          .value_ = Collections.emptyList();
      }
      
      private Builder(GeneratedMessageV3.BuilderParent parent) {
        super(parent);
        this.param_ = 1;
        this.value_ = Collections.emptyList();
      }
      
      public Builder clear() {
        super.clear();
        this.param_ = 1;
        this.bitField0_ &= 0xFFFFFFFE;
        if (this.valueBuilder_ == null) {
          this.value_ = Collections.emptyList();
        } else {
          this.value_ = null;
          this.valueBuilder_.clear();
        } 
        this.bitField0_ &= 0xFFFFFFFD;
        return this;
      }
      
      public Descriptors.Descriptor getDescriptorForType() {
        return MysqlxNotice.internal_static_Mysqlx_Notice_SessionStateChanged_descriptor;
      }
      
      public MysqlxNotice.SessionStateChanged getDefaultInstanceForType() {
        return MysqlxNotice.SessionStateChanged.getDefaultInstance();
      }
      
      public MysqlxNotice.SessionStateChanged build() {
        MysqlxNotice.SessionStateChanged result = buildPartial();
        if (!result.isInitialized())
          throw newUninitializedMessageException(result); 
        return result;
      }
      
      public MysqlxNotice.SessionStateChanged buildPartial() {
        MysqlxNotice.SessionStateChanged result = new MysqlxNotice.SessionStateChanged(this);
        int from_bitField0_ = this.bitField0_;
        int to_bitField0_ = 0;
        if ((from_bitField0_ & 0x1) != 0)
          to_bitField0_ |= 0x1; 
        result.param_ = this.param_;
        if (this.valueBuilder_ == null) {
          if ((this.bitField0_ & 0x2) != 0) {
            this.value_ = Collections.unmodifiableList(this.value_);
            this.bitField0_ &= 0xFFFFFFFD;
          } 
          result.value_ = this.value_;
        } else {
          result.value_ = this.valueBuilder_.build();
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
        if (other instanceof MysqlxNotice.SessionStateChanged)
          return mergeFrom((MysqlxNotice.SessionStateChanged)other); 
        super.mergeFrom(other);
        return this;
      }
      
      public Builder mergeFrom(MysqlxNotice.SessionStateChanged other) {
        if (other == MysqlxNotice.SessionStateChanged.getDefaultInstance())
          return this; 
        if (other.hasParam())
          setParam(other.getParam()); 
        if (this.valueBuilder_ == null) {
          if (!other.value_.isEmpty()) {
            if (this.value_.isEmpty()) {
              this.value_ = other.value_;
              this.bitField0_ &= 0xFFFFFFFD;
            } else {
              ensureValueIsMutable();
              this.value_.addAll(other.value_);
            } 
            onChanged();
          } 
        } else if (!other.value_.isEmpty()) {
          if (this.valueBuilder_.isEmpty()) {
            this.valueBuilder_.dispose();
            this.valueBuilder_ = null;
            this.value_ = other.value_;
            this.bitField0_ &= 0xFFFFFFFD;
            this.valueBuilder_ = MysqlxNotice.SessionStateChanged.alwaysUseFieldBuilders ? getValueFieldBuilder() : null;
          } else {
            this.valueBuilder_.addAllMessages(other.value_);
          } 
        } 
        mergeUnknownFields(other.getUnknownFields());
        onChanged();
        return this;
      }
      
      public final boolean isInitialized() {
        if (!hasParam())
          return false; 
        for (int i = 0; i < getValueCount(); i++) {
          if (!getValue(i).isInitialized())
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
            MysqlxDatatypes.Scalar m;
            MysqlxNotice.SessionStateChanged.Parameter tmpValue;
            int tag = input.readTag();
            switch (tag) {
              case 0:
                done = true;
                continue;
              case 8:
                tmpRaw = input.readEnum();
                tmpValue = MysqlxNotice.SessionStateChanged.Parameter.forNumber(tmpRaw);
                if (tmpValue == null) {
                  mergeUnknownVarintField(1, tmpRaw);
                  continue;
                } 
                this.param_ = tmpRaw;
                this.bitField0_ |= 0x1;
                continue;
              case 18:
                m = (MysqlxDatatypes.Scalar)input.readMessage(MysqlxDatatypes.Scalar.PARSER, extensionRegistry);
                if (this.valueBuilder_ == null) {
                  ensureValueIsMutable();
                  this.value_.add(m);
                  continue;
                } 
                this.valueBuilder_.addMessage((AbstractMessage)m);
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
      
      public boolean hasParam() {
        return ((this.bitField0_ & 0x1) != 0);
      }
      
      public MysqlxNotice.SessionStateChanged.Parameter getParam() {
        MysqlxNotice.SessionStateChanged.Parameter result = MysqlxNotice.SessionStateChanged.Parameter.valueOf(this.param_);
        return (result == null) ? MysqlxNotice.SessionStateChanged.Parameter.CURRENT_SCHEMA : result;
      }
      
      public Builder setParam(MysqlxNotice.SessionStateChanged.Parameter value) {
        if (value == null)
          throw new NullPointerException(); 
        this.bitField0_ |= 0x1;
        this.param_ = value.getNumber();
        onChanged();
        return this;
      }
      
      public Builder clearParam() {
        this.bitField0_ &= 0xFFFFFFFE;
        this.param_ = 1;
        onChanged();
        return this;
      }
      
      private void ensureValueIsMutable() {
        if ((this.bitField0_ & 0x2) == 0) {
          this.value_ = new ArrayList<>(this.value_);
          this.bitField0_ |= 0x2;
        } 
      }
      
      public List<MysqlxDatatypes.Scalar> getValueList() {
        if (this.valueBuilder_ == null)
          return Collections.unmodifiableList(this.value_); 
        return this.valueBuilder_.getMessageList();
      }
      
      public int getValueCount() {
        if (this.valueBuilder_ == null)
          return this.value_.size(); 
        return this.valueBuilder_.getCount();
      }
      
      public MysqlxDatatypes.Scalar getValue(int index) {
        if (this.valueBuilder_ == null)
          return this.value_.get(index); 
        return (MysqlxDatatypes.Scalar)this.valueBuilder_.getMessage(index);
      }
      
      public Builder setValue(int index, MysqlxDatatypes.Scalar value) {
        if (this.valueBuilder_ == null) {
          if (value == null)
            throw new NullPointerException(); 
          ensureValueIsMutable();
          this.value_.set(index, value);
          onChanged();
        } else {
          this.valueBuilder_.setMessage(index, (AbstractMessage)value);
        } 
        return this;
      }
      
      public Builder setValue(int index, MysqlxDatatypes.Scalar.Builder builderForValue) {
        if (this.valueBuilder_ == null) {
          ensureValueIsMutable();
          this.value_.set(index, builderForValue.build());
          onChanged();
        } else {
          this.valueBuilder_.setMessage(index, (AbstractMessage)builderForValue.build());
        } 
        return this;
      }
      
      public Builder addValue(MysqlxDatatypes.Scalar value) {
        if (this.valueBuilder_ == null) {
          if (value == null)
            throw new NullPointerException(); 
          ensureValueIsMutable();
          this.value_.add(value);
          onChanged();
        } else {
          this.valueBuilder_.addMessage((AbstractMessage)value);
        } 
        return this;
      }
      
      public Builder addValue(int index, MysqlxDatatypes.Scalar value) {
        if (this.valueBuilder_ == null) {
          if (value == null)
            throw new NullPointerException(); 
          ensureValueIsMutable();
          this.value_.add(index, value);
          onChanged();
        } else {
          this.valueBuilder_.addMessage(index, (AbstractMessage)value);
        } 
        return this;
      }
      
      public Builder addValue(MysqlxDatatypes.Scalar.Builder builderForValue) {
        if (this.valueBuilder_ == null) {
          ensureValueIsMutable();
          this.value_.add(builderForValue.build());
          onChanged();
        } else {
          this.valueBuilder_.addMessage((AbstractMessage)builderForValue.build());
        } 
        return this;
      }
      
      public Builder addValue(int index, MysqlxDatatypes.Scalar.Builder builderForValue) {
        if (this.valueBuilder_ == null) {
          ensureValueIsMutable();
          this.value_.add(index, builderForValue.build());
          onChanged();
        } else {
          this.valueBuilder_.addMessage(index, (AbstractMessage)builderForValue.build());
        } 
        return this;
      }
      
      public Builder addAllValue(Iterable<? extends MysqlxDatatypes.Scalar> values) {
        if (this.valueBuilder_ == null) {
          ensureValueIsMutable();
          AbstractMessageLite.Builder.addAll(values, this.value_);
          onChanged();
        } else {
          this.valueBuilder_.addAllMessages(values);
        } 
        return this;
      }
      
      public Builder clearValue() {
        if (this.valueBuilder_ == null) {
          this.value_ = Collections.emptyList();
          this.bitField0_ &= 0xFFFFFFFD;
          onChanged();
        } else {
          this.valueBuilder_.clear();
        } 
        return this;
      }
      
      public Builder removeValue(int index) {
        if (this.valueBuilder_ == null) {
          ensureValueIsMutable();
          this.value_.remove(index);
          onChanged();
        } else {
          this.valueBuilder_.remove(index);
        } 
        return this;
      }
      
      public MysqlxDatatypes.Scalar.Builder getValueBuilder(int index) {
        return (MysqlxDatatypes.Scalar.Builder)getValueFieldBuilder().getBuilder(index);
      }
      
      public MysqlxDatatypes.ScalarOrBuilder getValueOrBuilder(int index) {
        if (this.valueBuilder_ == null)
          return this.value_.get(index); 
        return (MysqlxDatatypes.ScalarOrBuilder)this.valueBuilder_.getMessageOrBuilder(index);
      }
      
      public List<? extends MysqlxDatatypes.ScalarOrBuilder> getValueOrBuilderList() {
        if (this.valueBuilder_ != null)
          return this.valueBuilder_.getMessageOrBuilderList(); 
        return Collections.unmodifiableList((List)this.value_);
      }
      
      public MysqlxDatatypes.Scalar.Builder addValueBuilder() {
        return (MysqlxDatatypes.Scalar.Builder)getValueFieldBuilder().addBuilder(
            (AbstractMessage)MysqlxDatatypes.Scalar.getDefaultInstance());
      }
      
      public MysqlxDatatypes.Scalar.Builder addValueBuilder(int index) {
        return (MysqlxDatatypes.Scalar.Builder)getValueFieldBuilder().addBuilder(index, 
            (AbstractMessage)MysqlxDatatypes.Scalar.getDefaultInstance());
      }
      
      public List<MysqlxDatatypes.Scalar.Builder> getValueBuilderList() {
        return getValueFieldBuilder().getBuilderList();
      }
      
      private RepeatedFieldBuilderV3<MysqlxDatatypes.Scalar, MysqlxDatatypes.Scalar.Builder, MysqlxDatatypes.ScalarOrBuilder> getValueFieldBuilder() {
        if (this.valueBuilder_ == null) {
          this
            
            .valueBuilder_ = new RepeatedFieldBuilderV3(this.value_, ((this.bitField0_ & 0x2) != 0), (AbstractMessage.BuilderParent)getParentForChildren(), isClean());
          this.value_ = null;
        } 
        return this.valueBuilder_;
      }
      
      public final Builder setUnknownFields(UnknownFieldSet unknownFields) {
        return (Builder)super.setUnknownFields(unknownFields);
      }
      
      public final Builder mergeUnknownFields(UnknownFieldSet unknownFields) {
        return (Builder)super.mergeUnknownFields(unknownFields);
      }
    }
    
    private static final SessionStateChanged DEFAULT_INSTANCE = new SessionStateChanged();
    
    public static SessionStateChanged getDefaultInstance() {
      return DEFAULT_INSTANCE;
    }
    
    @Deprecated
    public static final Parser<SessionStateChanged> PARSER = (Parser<SessionStateChanged>)new AbstractParser<SessionStateChanged>() {
        public MysqlxNotice.SessionStateChanged parsePartialFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
          MysqlxNotice.SessionStateChanged.Builder builder = MysqlxNotice.SessionStateChanged.newBuilder();
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
    
    public static Parser<SessionStateChanged> parser() {
      return PARSER;
    }
    
    public Parser<SessionStateChanged> getParserForType() {
      return PARSER;
    }
    
    public SessionStateChanged getDefaultInstanceForType() {
      return DEFAULT_INSTANCE;
    }
  }
  
  public static interface GroupReplicationStateChangedOrBuilder extends MessageOrBuilder {
    boolean hasType();
    
    int getType();
    
    boolean hasViewId();
    
    String getViewId();
    
    ByteString getViewIdBytes();
  }
  
  public static final class GroupReplicationStateChanged extends GeneratedMessageV3 implements GroupReplicationStateChangedOrBuilder {
    private static final long serialVersionUID = 0L;
    
    private int bitField0_;
    
    public static final int TYPE_FIELD_NUMBER = 1;
    
    private int type_;
    
    public static final int VIEW_ID_FIELD_NUMBER = 2;
    
    private volatile Object viewId_;
    
    private byte memoizedIsInitialized;
    
    private GroupReplicationStateChanged(GeneratedMessageV3.Builder<?> builder) {
      super(builder);
      this.memoizedIsInitialized = -1;
    }
    
    private GroupReplicationStateChanged() {
      this.memoizedIsInitialized = -1;
      this.viewId_ = "";
    }
    
    protected Object newInstance(GeneratedMessageV3.UnusedPrivateParameter unused) {
      return new GroupReplicationStateChanged();
    }
    
    public final UnknownFieldSet getUnknownFields() {
      return this.unknownFields;
    }
    
    public static final Descriptors.Descriptor getDescriptor() {
      return MysqlxNotice.internal_static_Mysqlx_Notice_GroupReplicationStateChanged_descriptor;
    }
    
    protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() {
      return MysqlxNotice.internal_static_Mysqlx_Notice_GroupReplicationStateChanged_fieldAccessorTable.ensureFieldAccessorsInitialized(GroupReplicationStateChanged.class, Builder.class);
    }
    
    public enum Type implements ProtocolMessageEnum {
      MEMBERSHIP_QUORUM_LOSS(1),
      MEMBERSHIP_VIEW_CHANGE(2),
      MEMBER_ROLE_CHANGE(3),
      MEMBER_STATE_CHANGE(4);
      
      public static final int MEMBERSHIP_QUORUM_LOSS_VALUE = 1;
      
      public static final int MEMBERSHIP_VIEW_CHANGE_VALUE = 2;
      
      public static final int MEMBER_ROLE_CHANGE_VALUE = 3;
      
      public static final int MEMBER_STATE_CHANGE_VALUE = 4;
      
      private static final Internal.EnumLiteMap<Type> internalValueMap = new Internal.EnumLiteMap<Type>() {
          public MysqlxNotice.GroupReplicationStateChanged.Type findValueByNumber(int number) {
            return MysqlxNotice.GroupReplicationStateChanged.Type.forNumber(number);
          }
        };
      
      private static final Type[] VALUES = values();
      
      private final int value;
      
      public final int getNumber() {
        return this.value;
      }
      
      public static Type forNumber(int value) {
        switch (value) {
          case 1:
            return MEMBERSHIP_QUORUM_LOSS;
          case 2:
            return MEMBERSHIP_VIEW_CHANGE;
          case 3:
            return MEMBER_ROLE_CHANGE;
          case 4:
            return MEMBER_STATE_CHANGE;
        } 
        return null;
      }
      
      public static Internal.EnumLiteMap<Type> internalGetValueMap() {
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
        return MysqlxNotice.GroupReplicationStateChanged.getDescriptor().getEnumTypes().get(0);
      }
      
      Type(int value) {
        this.value = value;
      }
    }
    
    public boolean hasType() {
      return ((this.bitField0_ & 0x1) != 0);
    }
    
    public int getType() {
      return this.type_;
    }
    
    public boolean hasViewId() {
      return ((this.bitField0_ & 0x2) != 0);
    }
    
    public String getViewId() {
      Object ref = this.viewId_;
      if (ref instanceof String)
        return (String)ref; 
      ByteString bs = (ByteString)ref;
      String s = bs.toStringUtf8();
      if (bs.isValidUtf8())
        this.viewId_ = s; 
      return s;
    }
    
    public ByteString getViewIdBytes() {
      Object ref = this.viewId_;
      if (ref instanceof String) {
        ByteString b = ByteString.copyFromUtf8((String)ref);
        this.viewId_ = b;
        return b;
      } 
      return (ByteString)ref;
    }
    
    public final boolean isInitialized() {
      byte isInitialized = this.memoizedIsInitialized;
      if (isInitialized == 1)
        return true; 
      if (isInitialized == 0)
        return false; 
      if (!hasType()) {
        this.memoizedIsInitialized = 0;
        return false;
      } 
      this.memoizedIsInitialized = 1;
      return true;
    }
    
    public void writeTo(CodedOutputStream output) throws IOException {
      if ((this.bitField0_ & 0x1) != 0)
        output.writeUInt32(1, this.type_); 
      if ((this.bitField0_ & 0x2) != 0)
        GeneratedMessageV3.writeString(output, 2, this.viewId_); 
      getUnknownFields().writeTo(output);
    }
    
    public int getSerializedSize() {
      int size = this.memoizedSize;
      if (size != -1)
        return size; 
      size = 0;
      if ((this.bitField0_ & 0x1) != 0)
        size += 
          CodedOutputStream.computeUInt32Size(1, this.type_); 
      if ((this.bitField0_ & 0x2) != 0)
        size += GeneratedMessageV3.computeStringSize(2, this.viewId_); 
      size += getUnknownFields().getSerializedSize();
      this.memoizedSize = size;
      return size;
    }
    
    public boolean equals(Object obj) {
      if (obj == this)
        return true; 
      if (!(obj instanceof GroupReplicationStateChanged))
        return super.equals(obj); 
      GroupReplicationStateChanged other = (GroupReplicationStateChanged)obj;
      if (hasType() != other.hasType())
        return false; 
      if (hasType() && 
        getType() != other
        .getType())
        return false; 
      if (hasViewId() != other.hasViewId())
        return false; 
      if (hasViewId() && 
        
        !getViewId().equals(other.getViewId()))
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
      if (hasType()) {
        hash = 37 * hash + 1;
        hash = 53 * hash + getType();
      } 
      if (hasViewId()) {
        hash = 37 * hash + 2;
        hash = 53 * hash + getViewId().hashCode();
      } 
      hash = 29 * hash + getUnknownFields().hashCode();
      this.memoizedHashCode = hash;
      return hash;
    }
    
    public static GroupReplicationStateChanged parseFrom(ByteBuffer data) throws InvalidProtocolBufferException {
      return (GroupReplicationStateChanged)PARSER.parseFrom(data);
    }
    
    public static GroupReplicationStateChanged parseFrom(ByteBuffer data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
      return (GroupReplicationStateChanged)PARSER.parseFrom(data, extensionRegistry);
    }
    
    public static GroupReplicationStateChanged parseFrom(ByteString data) throws InvalidProtocolBufferException {
      return (GroupReplicationStateChanged)PARSER.parseFrom(data);
    }
    
    public static GroupReplicationStateChanged parseFrom(ByteString data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
      return (GroupReplicationStateChanged)PARSER.parseFrom(data, extensionRegistry);
    }
    
    public static GroupReplicationStateChanged parseFrom(byte[] data) throws InvalidProtocolBufferException {
      return (GroupReplicationStateChanged)PARSER.parseFrom(data);
    }
    
    public static GroupReplicationStateChanged parseFrom(byte[] data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
      return (GroupReplicationStateChanged)PARSER.parseFrom(data, extensionRegistry);
    }
    
    public static GroupReplicationStateChanged parseFrom(InputStream input) throws IOException {
      return 
        (GroupReplicationStateChanged)GeneratedMessageV3.parseWithIOException(PARSER, input);
    }
    
    public static GroupReplicationStateChanged parseFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
      return 
        (GroupReplicationStateChanged)GeneratedMessageV3.parseWithIOException(PARSER, input, extensionRegistry);
    }
    
    public static GroupReplicationStateChanged parseDelimitedFrom(InputStream input) throws IOException {
      return 
        (GroupReplicationStateChanged)GeneratedMessageV3.parseDelimitedWithIOException(PARSER, input);
    }
    
    public static GroupReplicationStateChanged parseDelimitedFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
      return 
        (GroupReplicationStateChanged)GeneratedMessageV3.parseDelimitedWithIOException(PARSER, input, extensionRegistry);
    }
    
    public static GroupReplicationStateChanged parseFrom(CodedInputStream input) throws IOException {
      return 
        (GroupReplicationStateChanged)GeneratedMessageV3.parseWithIOException(PARSER, input);
    }
    
    public static GroupReplicationStateChanged parseFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
      return 
        (GroupReplicationStateChanged)GeneratedMessageV3.parseWithIOException(PARSER, input, extensionRegistry);
    }
    
    public Builder newBuilderForType() {
      return newBuilder();
    }
    
    public static Builder newBuilder() {
      return DEFAULT_INSTANCE.toBuilder();
    }
    
    public static Builder newBuilder(GroupReplicationStateChanged prototype) {
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
    
    public static final class Builder extends GeneratedMessageV3.Builder<Builder> implements MysqlxNotice.GroupReplicationStateChangedOrBuilder {
      private int bitField0_;
      
      private int type_;
      
      private Object viewId_;
      
      public static final Descriptors.Descriptor getDescriptor() {
        return MysqlxNotice.internal_static_Mysqlx_Notice_GroupReplicationStateChanged_descriptor;
      }
      
      protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() {
        return MysqlxNotice.internal_static_Mysqlx_Notice_GroupReplicationStateChanged_fieldAccessorTable
          .ensureFieldAccessorsInitialized(MysqlxNotice.GroupReplicationStateChanged.class, Builder.class);
      }
      
      private Builder() {
        this.viewId_ = "";
      }
      
      private Builder(GeneratedMessageV3.BuilderParent parent) {
        super(parent);
        this.viewId_ = "";
      }
      
      public Builder clear() {
        super.clear();
        this.type_ = 0;
        this.bitField0_ &= 0xFFFFFFFE;
        this.viewId_ = "";
        this.bitField0_ &= 0xFFFFFFFD;
        return this;
      }
      
      public Descriptors.Descriptor getDescriptorForType() {
        return MysqlxNotice.internal_static_Mysqlx_Notice_GroupReplicationStateChanged_descriptor;
      }
      
      public MysqlxNotice.GroupReplicationStateChanged getDefaultInstanceForType() {
        return MysqlxNotice.GroupReplicationStateChanged.getDefaultInstance();
      }
      
      public MysqlxNotice.GroupReplicationStateChanged build() {
        MysqlxNotice.GroupReplicationStateChanged result = buildPartial();
        if (!result.isInitialized())
          throw newUninitializedMessageException(result); 
        return result;
      }
      
      public MysqlxNotice.GroupReplicationStateChanged buildPartial() {
        MysqlxNotice.GroupReplicationStateChanged result = new MysqlxNotice.GroupReplicationStateChanged(this);
        int from_bitField0_ = this.bitField0_;
        int to_bitField0_ = 0;
        if ((from_bitField0_ & 0x1) != 0) {
          result.type_ = this.type_;
          to_bitField0_ |= 0x1;
        } 
        if ((from_bitField0_ & 0x2) != 0)
          to_bitField0_ |= 0x2; 
        result.viewId_ = this.viewId_;
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
        if (other instanceof MysqlxNotice.GroupReplicationStateChanged)
          return mergeFrom((MysqlxNotice.GroupReplicationStateChanged)other); 
        super.mergeFrom(other);
        return this;
      }
      
      public Builder mergeFrom(MysqlxNotice.GroupReplicationStateChanged other) {
        if (other == MysqlxNotice.GroupReplicationStateChanged.getDefaultInstance())
          return this; 
        if (other.hasType())
          setType(other.getType()); 
        if (other.hasViewId()) {
          this.bitField0_ |= 0x2;
          this.viewId_ = other.viewId_;
          onChanged();
        } 
        mergeUnknownFields(other.getUnknownFields());
        onChanged();
        return this;
      }
      
      public final boolean isInitialized() {
        if (!hasType())
          return false; 
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
              case 8:
                this.type_ = input.readUInt32();
                this.bitField0_ |= 0x1;
                continue;
              case 18:
                this.viewId_ = input.readBytes();
                this.bitField0_ |= 0x2;
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
      
      public boolean hasType() {
        return ((this.bitField0_ & 0x1) != 0);
      }
      
      public int getType() {
        return this.type_;
      }
      
      public Builder setType(int value) {
        this.bitField0_ |= 0x1;
        this.type_ = value;
        onChanged();
        return this;
      }
      
      public Builder clearType() {
        this.bitField0_ &= 0xFFFFFFFE;
        this.type_ = 0;
        onChanged();
        return this;
      }
      
      public boolean hasViewId() {
        return ((this.bitField0_ & 0x2) != 0);
      }
      
      public String getViewId() {
        Object ref = this.viewId_;
        if (!(ref instanceof String)) {
          ByteString bs = (ByteString)ref;
          String s = bs.toStringUtf8();
          if (bs.isValidUtf8())
            this.viewId_ = s; 
          return s;
        } 
        return (String)ref;
      }
      
      public ByteString getViewIdBytes() {
        Object ref = this.viewId_;
        if (ref instanceof String) {
          ByteString b = ByteString.copyFromUtf8((String)ref);
          this.viewId_ = b;
          return b;
        } 
        return (ByteString)ref;
      }
      
      public Builder setViewId(String value) {
        if (value == null)
          throw new NullPointerException(); 
        this.bitField0_ |= 0x2;
        this.viewId_ = value;
        onChanged();
        return this;
      }
      
      public Builder clearViewId() {
        this.bitField0_ &= 0xFFFFFFFD;
        this.viewId_ = MysqlxNotice.GroupReplicationStateChanged.getDefaultInstance().getViewId();
        onChanged();
        return this;
      }
      
      public Builder setViewIdBytes(ByteString value) {
        if (value == null)
          throw new NullPointerException(); 
        this.bitField0_ |= 0x2;
        this.viewId_ = value;
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
    
    private static final GroupReplicationStateChanged DEFAULT_INSTANCE = new GroupReplicationStateChanged();
    
    public static GroupReplicationStateChanged getDefaultInstance() {
      return DEFAULT_INSTANCE;
    }
    
    @Deprecated
    public static final Parser<GroupReplicationStateChanged> PARSER = (Parser<GroupReplicationStateChanged>)new AbstractParser<GroupReplicationStateChanged>() {
        public MysqlxNotice.GroupReplicationStateChanged parsePartialFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
          MysqlxNotice.GroupReplicationStateChanged.Builder builder = MysqlxNotice.GroupReplicationStateChanged.newBuilder();
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
    
    public static Parser<GroupReplicationStateChanged> parser() {
      return PARSER;
    }
    
    public Parser<GroupReplicationStateChanged> getParserForType() {
      return PARSER;
    }
    
    public GroupReplicationStateChanged getDefaultInstanceForType() {
      return DEFAULT_INSTANCE;
    }
  }
  
  public static interface ServerHelloOrBuilder extends MessageOrBuilder {}
  
  public static final class ServerHello extends GeneratedMessageV3 implements ServerHelloOrBuilder {
    private static final long serialVersionUID = 0L;
    
    private byte memoizedIsInitialized;
    
    private ServerHello(GeneratedMessageV3.Builder<?> builder) {
      super(builder);
      this.memoizedIsInitialized = -1;
    }
    
    private ServerHello() {
      this.memoizedIsInitialized = -1;
    }
    
    protected Object newInstance(GeneratedMessageV3.UnusedPrivateParameter unused) {
      return new ServerHello();
    }
    
    public final UnknownFieldSet getUnknownFields() {
      return this.unknownFields;
    }
    
    public static final Descriptors.Descriptor getDescriptor() {
      return MysqlxNotice.internal_static_Mysqlx_Notice_ServerHello_descriptor;
    }
    
    protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() {
      return MysqlxNotice.internal_static_Mysqlx_Notice_ServerHello_fieldAccessorTable.ensureFieldAccessorsInitialized(ServerHello.class, Builder.class);
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
      if (!(obj instanceof ServerHello))
        return super.equals(obj); 
      ServerHello other = (ServerHello)obj;
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
    
    public static ServerHello parseFrom(ByteBuffer data) throws InvalidProtocolBufferException {
      return (ServerHello)PARSER.parseFrom(data);
    }
    
    public static ServerHello parseFrom(ByteBuffer data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
      return (ServerHello)PARSER.parseFrom(data, extensionRegistry);
    }
    
    public static ServerHello parseFrom(ByteString data) throws InvalidProtocolBufferException {
      return (ServerHello)PARSER.parseFrom(data);
    }
    
    public static ServerHello parseFrom(ByteString data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
      return (ServerHello)PARSER.parseFrom(data, extensionRegistry);
    }
    
    public static ServerHello parseFrom(byte[] data) throws InvalidProtocolBufferException {
      return (ServerHello)PARSER.parseFrom(data);
    }
    
    public static ServerHello parseFrom(byte[] data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
      return (ServerHello)PARSER.parseFrom(data, extensionRegistry);
    }
    
    public static ServerHello parseFrom(InputStream input) throws IOException {
      return 
        (ServerHello)GeneratedMessageV3.parseWithIOException(PARSER, input);
    }
    
    public static ServerHello parseFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
      return 
        (ServerHello)GeneratedMessageV3.parseWithIOException(PARSER, input, extensionRegistry);
    }
    
    public static ServerHello parseDelimitedFrom(InputStream input) throws IOException {
      return 
        (ServerHello)GeneratedMessageV3.parseDelimitedWithIOException(PARSER, input);
    }
    
    public static ServerHello parseDelimitedFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
      return 
        (ServerHello)GeneratedMessageV3.parseDelimitedWithIOException(PARSER, input, extensionRegistry);
    }
    
    public static ServerHello parseFrom(CodedInputStream input) throws IOException {
      return 
        (ServerHello)GeneratedMessageV3.parseWithIOException(PARSER, input);
    }
    
    public static ServerHello parseFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
      return 
        (ServerHello)GeneratedMessageV3.parseWithIOException(PARSER, input, extensionRegistry);
    }
    
    public Builder newBuilderForType() {
      return newBuilder();
    }
    
    public static Builder newBuilder() {
      return DEFAULT_INSTANCE.toBuilder();
    }
    
    public static Builder newBuilder(ServerHello prototype) {
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
    
    public static final class Builder extends GeneratedMessageV3.Builder<Builder> implements MysqlxNotice.ServerHelloOrBuilder {
      public static final Descriptors.Descriptor getDescriptor() {
        return MysqlxNotice.internal_static_Mysqlx_Notice_ServerHello_descriptor;
      }
      
      protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() {
        return MysqlxNotice.internal_static_Mysqlx_Notice_ServerHello_fieldAccessorTable
          .ensureFieldAccessorsInitialized(MysqlxNotice.ServerHello.class, Builder.class);
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
        return MysqlxNotice.internal_static_Mysqlx_Notice_ServerHello_descriptor;
      }
      
      public MysqlxNotice.ServerHello getDefaultInstanceForType() {
        return MysqlxNotice.ServerHello.getDefaultInstance();
      }
      
      public MysqlxNotice.ServerHello build() {
        MysqlxNotice.ServerHello result = buildPartial();
        if (!result.isInitialized())
          throw newUninitializedMessageException(result); 
        return result;
      }
      
      public MysqlxNotice.ServerHello buildPartial() {
        MysqlxNotice.ServerHello result = new MysqlxNotice.ServerHello(this);
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
        if (other instanceof MysqlxNotice.ServerHello)
          return mergeFrom((MysqlxNotice.ServerHello)other); 
        super.mergeFrom(other);
        return this;
      }
      
      public Builder mergeFrom(MysqlxNotice.ServerHello other) {
        if (other == MysqlxNotice.ServerHello.getDefaultInstance())
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
    
    private static final ServerHello DEFAULT_INSTANCE = new ServerHello();
    
    public static ServerHello getDefaultInstance() {
      return DEFAULT_INSTANCE;
    }
    
    @Deprecated
    public static final Parser<ServerHello> PARSER = (Parser<ServerHello>)new AbstractParser<ServerHello>() {
        public MysqlxNotice.ServerHello parsePartialFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
          MysqlxNotice.ServerHello.Builder builder = MysqlxNotice.ServerHello.newBuilder();
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
    
    public static Parser<ServerHello> parser() {
      return PARSER;
    }
    
    public Parser<ServerHello> getParserForType() {
      return PARSER;
    }
    
    public ServerHello getDefaultInstanceForType() {
      return DEFAULT_INSTANCE;
    }
  }
  
  public static Descriptors.FileDescriptor getDescriptor() {
    return descriptor;
  }
  
  static {
    String[] descriptorData = { "\n\023mysqlx_notice.proto\022\rMysqlx.Notice\032\fmysqlx.proto\032\026mysqlx_datatypes.proto\"\002\n\005Frame\022\f\n\004type\030\001 \002(\r\0221\n\005scope\030\002 \001(\0162\032.Mysqlx.Notice.Frame.Scope:\006GLOBAL\022\017\n\007payload\030\003 \001(\f\"\036\n\005Scope\022\n\n\006GLOBAL\020\001\022\t\n\005LOCAL\020\002\"\001\n\004Type\022\013\n\007WARNING\020\001\022\034\n\030SESSION_VARIABLE_CHANGED\020\002\022\031\n\025SESSION_STATE_CHANGED\020\003\022#\n\037GROUP_REPLICATION_STATE_CHANGED\020\004\022\020\n\fSERVER_HELLO\020\005:\004ê0\013\"\001\n\007Warning\0224\n\005level\030\001 \001(\0162\034.Mysqlx.Notice.Warning.Level:\007WARNING\022\f\n\004code\030\002 \002(\r\022\013\n\003msg\030\003 \002(\t\")\n\005Level\022\b\n\004NOTE\020\001\022\013\n\007WARNING\020\002\022\t\n\005ERROR\020\003\"P\n\026SessionVariableChanged\022\r\n\005param\030\001 \002(\t\022'\n\005value\030\002 \001(\0132\030.Mysqlx.Datatypes.Scalar\"ñ\002\n\023SessionStateChanged\022;\n\005param\030\001 \002(\0162,.Mysqlx.Notice.SessionStateChanged.Parameter\022'\n\005value\030\002 \003(\0132\030.Mysqlx.Datatypes.Scalar\"ó\001\n\tParameter\022\022\n\016CURRENT_SCHEMA\020\001\022\023\n\017ACCOUNT_EXPIRED\020\002\022\027\n\023GENERATED_INSERT_ID\020\003\022\021\n\rROWS_AFFECTED\020\004\022\016\n\nROWS_FOUND\020\005\022\020\n\fROWS_MATCHED\020\006\022\021\n\rTRX_COMMITTED\020\007\022\022\n\016TRX_ROLLEDBACK\020\t\022\024\n\020PRODUCED_MESSAGE\020\n\022\026\n\022CLIENT_ID_ASSIGNED\020\013\022\032\n\026GENERATED_DOCUMENT_IDS\020\f\"®\001\n\034GroupReplicationStateChanged\022\f\n\004type\030\001 \002(\r\022\017\n\007view_id\030\002 \001(\t\"o\n\004Type\022\032\n\026MEMBERSHIP_QUORUM_LOSS\020\001\022\032\n\026MEMBERSHIP_VIEW_CHANGE\020\002\022\026\n\022MEMBER_ROLE_CHANGE\020\003\022\027\n\023MEMBER_STATE_CHANGE\020\004\"\r\n\013ServerHelloB\031\n\027com.mysql.cj.x.protobuf" };
    descriptor = Descriptors.FileDescriptor.internalBuildGeneratedFileFrom(descriptorData, new Descriptors.FileDescriptor[] { Mysqlx.getDescriptor(), 
          MysqlxDatatypes.getDescriptor() });
    internal_static_Mysqlx_Notice_Frame_descriptor = getDescriptor().getMessageTypes().get(0);
    internal_static_Mysqlx_Notice_Frame_fieldAccessorTable = new GeneratedMessageV3.FieldAccessorTable(internal_static_Mysqlx_Notice_Frame_descriptor, new String[] { "Type", "Scope", "Payload" });
    internal_static_Mysqlx_Notice_Warning_descriptor = getDescriptor().getMessageTypes().get(1);
    internal_static_Mysqlx_Notice_Warning_fieldAccessorTable = new GeneratedMessageV3.FieldAccessorTable(internal_static_Mysqlx_Notice_Warning_descriptor, new String[] { "Level", "Code", "Msg" });
    internal_static_Mysqlx_Notice_SessionVariableChanged_descriptor = getDescriptor().getMessageTypes().get(2);
    internal_static_Mysqlx_Notice_SessionVariableChanged_fieldAccessorTable = new GeneratedMessageV3.FieldAccessorTable(internal_static_Mysqlx_Notice_SessionVariableChanged_descriptor, new String[] { "Param", "Value" });
    internal_static_Mysqlx_Notice_SessionStateChanged_descriptor = getDescriptor().getMessageTypes().get(3);
    internal_static_Mysqlx_Notice_SessionStateChanged_fieldAccessorTable = new GeneratedMessageV3.FieldAccessorTable(internal_static_Mysqlx_Notice_SessionStateChanged_descriptor, new String[] { "Param", "Value" });
    internal_static_Mysqlx_Notice_GroupReplicationStateChanged_descriptor = getDescriptor().getMessageTypes().get(4);
    internal_static_Mysqlx_Notice_GroupReplicationStateChanged_fieldAccessorTable = new GeneratedMessageV3.FieldAccessorTable(internal_static_Mysqlx_Notice_GroupReplicationStateChanged_descriptor, new String[] { "Type", "ViewId" });
    internal_static_Mysqlx_Notice_ServerHello_descriptor = getDescriptor().getMessageTypes().get(5);
    internal_static_Mysqlx_Notice_ServerHello_fieldAccessorTable = new GeneratedMessageV3.FieldAccessorTable(internal_static_Mysqlx_Notice_ServerHello_descriptor, new String[0]);
    ExtensionRegistry registry = ExtensionRegistry.newInstance();
    registry.add(Mysqlx.serverMessageId);
    Descriptors.FileDescriptor.internalUpdateFileDescriptor(descriptor, registry);
    Mysqlx.getDescriptor();
    MysqlxDatatypes.getDescriptor();
  }
}
