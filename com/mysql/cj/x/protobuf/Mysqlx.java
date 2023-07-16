package com.mysql.cj.x.protobuf;

import com.google.protobuf.AbstractMessage;
import com.google.protobuf.AbstractMessageLite;
import com.google.protobuf.AbstractParser;
import com.google.protobuf.ByteString;
import com.google.protobuf.CodedInputStream;
import com.google.protobuf.CodedOutputStream;
import com.google.protobuf.DescriptorProtos;
import com.google.protobuf.Descriptors;
import com.google.protobuf.ExtensionLite;
import com.google.protobuf.ExtensionRegistry;
import com.google.protobuf.ExtensionRegistryLite;
import com.google.protobuf.GeneratedMessage;
import com.google.protobuf.GeneratedMessageV3;
import com.google.protobuf.Internal;
import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.Message;
import com.google.protobuf.MessageLite;
import com.google.protobuf.MessageOrBuilder;
import com.google.protobuf.Parser;
import com.google.protobuf.ProtocolMessageEnum;
import com.google.protobuf.UninitializedMessageException;
import com.google.protobuf.UnknownFieldSet;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

public final class Mysqlx {
  public static final int CLIENT_MESSAGE_ID_FIELD_NUMBER = 100001;
  
  public static void registerAllExtensions(ExtensionRegistryLite registry) {
    registry.add((ExtensionLite)clientMessageId);
    registry.add((ExtensionLite)serverMessageId);
  }
  
  public static void registerAllExtensions(ExtensionRegistry registry) {
    registerAllExtensions((ExtensionRegistryLite)registry);
  }
  
  public static final class ClientMessages extends GeneratedMessageV3 implements ClientMessagesOrBuilder {
    private static final long serialVersionUID = 0L;
    
    private byte memoizedIsInitialized;
    
    private ClientMessages(GeneratedMessageV3.Builder<?> builder) {
      super(builder);
      this.memoizedIsInitialized = -1;
    }
    
    private ClientMessages() {
      this.memoizedIsInitialized = -1;
    }
    
    protected Object newInstance(GeneratedMessageV3.UnusedPrivateParameter unused) {
      return new ClientMessages();
    }
    
    public final UnknownFieldSet getUnknownFields() {
      return this.unknownFields;
    }
    
    public static final Descriptors.Descriptor getDescriptor() {
      return Mysqlx.internal_static_Mysqlx_ClientMessages_descriptor;
    }
    
    protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() {
      return Mysqlx.internal_static_Mysqlx_ClientMessages_fieldAccessorTable.ensureFieldAccessorsInitialized(ClientMessages.class, Builder.class);
    }
    
    public enum Type implements ProtocolMessageEnum {
      CON_CAPABILITIES_GET(1),
      CON_CAPABILITIES_SET(2),
      CON_CLOSE(3),
      SESS_AUTHENTICATE_START(4),
      SESS_AUTHENTICATE_CONTINUE(5),
      SESS_RESET(6),
      SESS_CLOSE(7),
      SQL_STMT_EXECUTE(12),
      CRUD_FIND(17),
      CRUD_INSERT(18),
      CRUD_UPDATE(19),
      CRUD_DELETE(20),
      EXPECT_OPEN(24),
      EXPECT_CLOSE(25),
      CRUD_CREATE_VIEW(30),
      CRUD_MODIFY_VIEW(31),
      CRUD_DROP_VIEW(32),
      PREPARE_PREPARE(40),
      PREPARE_EXECUTE(41),
      PREPARE_DEALLOCATE(42),
      CURSOR_OPEN(43),
      CURSOR_CLOSE(44),
      CURSOR_FETCH(45),
      COMPRESSION(46);
      
      public static final int CON_CAPABILITIES_GET_VALUE = 1;
      
      public static final int CON_CAPABILITIES_SET_VALUE = 2;
      
      public static final int CON_CLOSE_VALUE = 3;
      
      public static final int SESS_AUTHENTICATE_START_VALUE = 4;
      
      public static final int SESS_AUTHENTICATE_CONTINUE_VALUE = 5;
      
      public static final int SESS_RESET_VALUE = 6;
      
      public static final int SESS_CLOSE_VALUE = 7;
      
      public static final int SQL_STMT_EXECUTE_VALUE = 12;
      
      public static final int CRUD_FIND_VALUE = 17;
      
      public static final int CRUD_INSERT_VALUE = 18;
      
      public static final int CRUD_UPDATE_VALUE = 19;
      
      public static final int CRUD_DELETE_VALUE = 20;
      
      public static final int EXPECT_OPEN_VALUE = 24;
      
      public static final int EXPECT_CLOSE_VALUE = 25;
      
      public static final int CRUD_CREATE_VIEW_VALUE = 30;
      
      public static final int CRUD_MODIFY_VIEW_VALUE = 31;
      
      public static final int CRUD_DROP_VIEW_VALUE = 32;
      
      public static final int PREPARE_PREPARE_VALUE = 40;
      
      public static final int PREPARE_EXECUTE_VALUE = 41;
      
      public static final int PREPARE_DEALLOCATE_VALUE = 42;
      
      public static final int CURSOR_OPEN_VALUE = 43;
      
      public static final int CURSOR_CLOSE_VALUE = 44;
      
      public static final int CURSOR_FETCH_VALUE = 45;
      
      public static final int COMPRESSION_VALUE = 46;
      
      private static final Internal.EnumLiteMap<Type> internalValueMap = new Internal.EnumLiteMap<Type>() {
          public Mysqlx.ClientMessages.Type findValueByNumber(int number) {
            return Mysqlx.ClientMessages.Type.forNumber(number);
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
            return CON_CAPABILITIES_GET;
          case 2:
            return CON_CAPABILITIES_SET;
          case 3:
            return CON_CLOSE;
          case 4:
            return SESS_AUTHENTICATE_START;
          case 5:
            return SESS_AUTHENTICATE_CONTINUE;
          case 6:
            return SESS_RESET;
          case 7:
            return SESS_CLOSE;
          case 12:
            return SQL_STMT_EXECUTE;
          case 17:
            return CRUD_FIND;
          case 18:
            return CRUD_INSERT;
          case 19:
            return CRUD_UPDATE;
          case 20:
            return CRUD_DELETE;
          case 24:
            return EXPECT_OPEN;
          case 25:
            return EXPECT_CLOSE;
          case 30:
            return CRUD_CREATE_VIEW;
          case 31:
            return CRUD_MODIFY_VIEW;
          case 32:
            return CRUD_DROP_VIEW;
          case 40:
            return PREPARE_PREPARE;
          case 41:
            return PREPARE_EXECUTE;
          case 42:
            return PREPARE_DEALLOCATE;
          case 43:
            return CURSOR_OPEN;
          case 44:
            return CURSOR_CLOSE;
          case 45:
            return CURSOR_FETCH;
          case 46:
            return COMPRESSION;
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
        return Mysqlx.ClientMessages.getDescriptor().getEnumTypes().get(0);
      }
      
      Type(int value) {
        this.value = value;
      }
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
      if (!(obj instanceof ClientMessages))
        return super.equals(obj); 
      ClientMessages other = (ClientMessages)obj;
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
    
    public static ClientMessages parseFrom(ByteBuffer data) throws InvalidProtocolBufferException {
      return (ClientMessages)PARSER.parseFrom(data);
    }
    
    public static ClientMessages parseFrom(ByteBuffer data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
      return (ClientMessages)PARSER.parseFrom(data, extensionRegistry);
    }
    
    public static ClientMessages parseFrom(ByteString data) throws InvalidProtocolBufferException {
      return (ClientMessages)PARSER.parseFrom(data);
    }
    
    public static ClientMessages parseFrom(ByteString data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
      return (ClientMessages)PARSER.parseFrom(data, extensionRegistry);
    }
    
    public static ClientMessages parseFrom(byte[] data) throws InvalidProtocolBufferException {
      return (ClientMessages)PARSER.parseFrom(data);
    }
    
    public static ClientMessages parseFrom(byte[] data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
      return (ClientMessages)PARSER.parseFrom(data, extensionRegistry);
    }
    
    public static ClientMessages parseFrom(InputStream input) throws IOException {
      return 
        (ClientMessages)GeneratedMessageV3.parseWithIOException(PARSER, input);
    }
    
    public static ClientMessages parseFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
      return 
        (ClientMessages)GeneratedMessageV3.parseWithIOException(PARSER, input, extensionRegistry);
    }
    
    public static ClientMessages parseDelimitedFrom(InputStream input) throws IOException {
      return 
        (ClientMessages)GeneratedMessageV3.parseDelimitedWithIOException(PARSER, input);
    }
    
    public static ClientMessages parseDelimitedFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
      return 
        (ClientMessages)GeneratedMessageV3.parseDelimitedWithIOException(PARSER, input, extensionRegistry);
    }
    
    public static ClientMessages parseFrom(CodedInputStream input) throws IOException {
      return 
        (ClientMessages)GeneratedMessageV3.parseWithIOException(PARSER, input);
    }
    
    public static ClientMessages parseFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
      return 
        (ClientMessages)GeneratedMessageV3.parseWithIOException(PARSER, input, extensionRegistry);
    }
    
    public Builder newBuilderForType() {
      return newBuilder();
    }
    
    public static Builder newBuilder() {
      return DEFAULT_INSTANCE.toBuilder();
    }
    
    public static Builder newBuilder(ClientMessages prototype) {
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
    
    public static final class Builder extends GeneratedMessageV3.Builder<Builder> implements Mysqlx.ClientMessagesOrBuilder {
      public static final Descriptors.Descriptor getDescriptor() {
        return Mysqlx.internal_static_Mysqlx_ClientMessages_descriptor;
      }
      
      protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() {
        return Mysqlx.internal_static_Mysqlx_ClientMessages_fieldAccessorTable
          .ensureFieldAccessorsInitialized(Mysqlx.ClientMessages.class, Builder.class);
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
        return Mysqlx.internal_static_Mysqlx_ClientMessages_descriptor;
      }
      
      public Mysqlx.ClientMessages getDefaultInstanceForType() {
        return Mysqlx.ClientMessages.getDefaultInstance();
      }
      
      public Mysqlx.ClientMessages build() {
        Mysqlx.ClientMessages result = buildPartial();
        if (!result.isInitialized())
          throw newUninitializedMessageException(result); 
        return result;
      }
      
      public Mysqlx.ClientMessages buildPartial() {
        Mysqlx.ClientMessages result = new Mysqlx.ClientMessages(this);
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
        if (other instanceof Mysqlx.ClientMessages)
          return mergeFrom((Mysqlx.ClientMessages)other); 
        super.mergeFrom(other);
        return this;
      }
      
      public Builder mergeFrom(Mysqlx.ClientMessages other) {
        if (other == Mysqlx.ClientMessages.getDefaultInstance())
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
    
    private static final ClientMessages DEFAULT_INSTANCE = new ClientMessages();
    
    public static ClientMessages getDefaultInstance() {
      return DEFAULT_INSTANCE;
    }
    
    @Deprecated
    public static final Parser<ClientMessages> PARSER = (Parser<ClientMessages>)new AbstractParser<ClientMessages>() {
        public Mysqlx.ClientMessages parsePartialFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
          Mysqlx.ClientMessages.Builder builder = Mysqlx.ClientMessages.newBuilder();
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
    
    public static Parser<ClientMessages> parser() {
      return PARSER;
    }
    
    public Parser<ClientMessages> getParserForType() {
      return PARSER;
    }
    
    public ClientMessages getDefaultInstanceForType() {
      return DEFAULT_INSTANCE;
    }
  }
  
  public enum Type implements ProtocolMessageEnum {
    CON_CAPABILITIES_GET(1),
    CON_CAPABILITIES_SET(2),
    CON_CLOSE(3),
    SESS_AUTHENTICATE_START(4),
    SESS_AUTHENTICATE_CONTINUE(5),
    SESS_RESET(6),
    SESS_CLOSE(7),
    SQL_STMT_EXECUTE(12),
    CRUD_FIND(17),
    CRUD_INSERT(18),
    CRUD_UPDATE(19),
    CRUD_DELETE(20),
    EXPECT_OPEN(24),
    EXPECT_CLOSE(25),
    CRUD_CREATE_VIEW(30),
    CRUD_MODIFY_VIEW(31),
    CRUD_DROP_VIEW(32),
    PREPARE_PREPARE(40),
    PREPARE_EXECUTE(41),
    PREPARE_DEALLOCATE(42),
    CURSOR_OPEN(43),
    CURSOR_CLOSE(44),
    CURSOR_FETCH(45),
    COMPRESSION(46);
    
    public static final int CON_CAPABILITIES_GET_VALUE = 1;
    
    public static final int CON_CAPABILITIES_SET_VALUE = 2;
    
    public static final int CON_CLOSE_VALUE = 3;
    
    public static final int SESS_AUTHENTICATE_START_VALUE = 4;
    
    public static final int SESS_AUTHENTICATE_CONTINUE_VALUE = 5;
    
    public static final int SESS_RESET_VALUE = 6;
    
    public static final int SESS_CLOSE_VALUE = 7;
    
    public static final int SQL_STMT_EXECUTE_VALUE = 12;
    
    public static final int CRUD_FIND_VALUE = 17;
    
    public static final int CRUD_INSERT_VALUE = 18;
    
    public static final int CRUD_UPDATE_VALUE = 19;
    
    public static final int CRUD_DELETE_VALUE = 20;
    
    public static final int EXPECT_OPEN_VALUE = 24;
    
    public static final int EXPECT_CLOSE_VALUE = 25;
    
    public static final int CRUD_CREATE_VIEW_VALUE = 30;
    
    public static final int CRUD_MODIFY_VIEW_VALUE = 31;
    
    public static final int CRUD_DROP_VIEW_VALUE = 32;
    
    public static final int PREPARE_PREPARE_VALUE = 40;
    
    public static final int PREPARE_EXECUTE_VALUE = 41;
    
    public static final int PREPARE_DEALLOCATE_VALUE = 42;
    
    public static final int CURSOR_OPEN_VALUE = 43;
    
    public static final int CURSOR_CLOSE_VALUE = 44;
    
    public static final int CURSOR_FETCH_VALUE = 45;
    
    public static final int COMPRESSION_VALUE = 46;
    
    private static final Internal.EnumLiteMap<Type> internalValueMap = new Internal.EnumLiteMap<Type>() {
        public Mysqlx.ClientMessages.Type findValueByNumber(int number) {
          return Mysqlx.ClientMessages.Type.forNumber(number);
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
          return CON_CAPABILITIES_GET;
        case 2:
          return CON_CAPABILITIES_SET;
        case 3:
          return CON_CLOSE;
        case 4:
          return SESS_AUTHENTICATE_START;
        case 5:
          return SESS_AUTHENTICATE_CONTINUE;
        case 6:
          return SESS_RESET;
        case 7:
          return SESS_CLOSE;
        case 12:
          return SQL_STMT_EXECUTE;
        case 17:
          return CRUD_FIND;
        case 18:
          return CRUD_INSERT;
        case 19:
          return CRUD_UPDATE;
        case 20:
          return CRUD_DELETE;
        case 24:
          return EXPECT_OPEN;
        case 25:
          return EXPECT_CLOSE;
        case 30:
          return CRUD_CREATE_VIEW;
        case 31:
          return CRUD_MODIFY_VIEW;
        case 32:
          return CRUD_DROP_VIEW;
        case 40:
          return PREPARE_PREPARE;
        case 41:
          return PREPARE_EXECUTE;
        case 42:
          return PREPARE_DEALLOCATE;
        case 43:
          return CURSOR_OPEN;
        case 44:
          return CURSOR_CLOSE;
        case 45:
          return CURSOR_FETCH;
        case 46:
          return COMPRESSION;
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
      return Mysqlx.ClientMessages.getDescriptor().getEnumTypes().get(0);
    }
    
    Type(int value) {
      this.value = value;
    }
  }
  
  public static final class ServerMessages extends GeneratedMessageV3 implements ServerMessagesOrBuilder {
    private static final long serialVersionUID = 0L;
    
    private byte memoizedIsInitialized;
    
    private ServerMessages(GeneratedMessageV3.Builder<?> builder) {
      super(builder);
      this.memoizedIsInitialized = -1;
    }
    
    private ServerMessages() {
      this.memoizedIsInitialized = -1;
    }
    
    protected Object newInstance(GeneratedMessageV3.UnusedPrivateParameter unused) {
      return new ServerMessages();
    }
    
    public final UnknownFieldSet getUnknownFields() {
      return this.unknownFields;
    }
    
    public static final Descriptors.Descriptor getDescriptor() {
      return Mysqlx.internal_static_Mysqlx_ServerMessages_descriptor;
    }
    
    protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() {
      return Mysqlx.internal_static_Mysqlx_ServerMessages_fieldAccessorTable.ensureFieldAccessorsInitialized(ServerMessages.class, Builder.class);
    }
    
    public enum Type implements ProtocolMessageEnum {
      OK(0),
      ERROR(1),
      CONN_CAPABILITIES(2),
      SESS_AUTHENTICATE_CONTINUE(3),
      SESS_AUTHENTICATE_OK(4),
      NOTICE(11),
      RESULTSET_COLUMN_META_DATA(12),
      RESULTSET_ROW(13),
      RESULTSET_FETCH_DONE(14),
      RESULTSET_FETCH_SUSPENDED(15),
      RESULTSET_FETCH_DONE_MORE_RESULTSETS(16),
      SQL_STMT_EXECUTE_OK(17),
      RESULTSET_FETCH_DONE_MORE_OUT_PARAMS(18),
      COMPRESSION(19);
      
      public static final int OK_VALUE = 0;
      
      public static final int ERROR_VALUE = 1;
      
      public static final int CONN_CAPABILITIES_VALUE = 2;
      
      public static final int SESS_AUTHENTICATE_CONTINUE_VALUE = 3;
      
      public static final int SESS_AUTHENTICATE_OK_VALUE = 4;
      
      public static final int NOTICE_VALUE = 11;
      
      public static final int RESULTSET_COLUMN_META_DATA_VALUE = 12;
      
      public static final int RESULTSET_ROW_VALUE = 13;
      
      public static final int RESULTSET_FETCH_DONE_VALUE = 14;
      
      public static final int RESULTSET_FETCH_SUSPENDED_VALUE = 15;
      
      public static final int RESULTSET_FETCH_DONE_MORE_RESULTSETS_VALUE = 16;
      
      public static final int SQL_STMT_EXECUTE_OK_VALUE = 17;
      
      public static final int RESULTSET_FETCH_DONE_MORE_OUT_PARAMS_VALUE = 18;
      
      public static final int COMPRESSION_VALUE = 19;
      
      private static final Internal.EnumLiteMap<Type> internalValueMap = new Internal.EnumLiteMap<Type>() {
          public Mysqlx.ServerMessages.Type findValueByNumber(int number) {
            return Mysqlx.ServerMessages.Type.forNumber(number);
          }
        };
      
      private static final Type[] VALUES = values();
      
      private final int value;
      
      public final int getNumber() {
        return this.value;
      }
      
      public static Type forNumber(int value) {
        switch (value) {
          case 0:
            return OK;
          case 1:
            return ERROR;
          case 2:
            return CONN_CAPABILITIES;
          case 3:
            return SESS_AUTHENTICATE_CONTINUE;
          case 4:
            return SESS_AUTHENTICATE_OK;
          case 11:
            return NOTICE;
          case 12:
            return RESULTSET_COLUMN_META_DATA;
          case 13:
            return RESULTSET_ROW;
          case 14:
            return RESULTSET_FETCH_DONE;
          case 15:
            return RESULTSET_FETCH_SUSPENDED;
          case 16:
            return RESULTSET_FETCH_DONE_MORE_RESULTSETS;
          case 17:
            return SQL_STMT_EXECUTE_OK;
          case 18:
            return RESULTSET_FETCH_DONE_MORE_OUT_PARAMS;
          case 19:
            return COMPRESSION;
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
        return Mysqlx.ServerMessages.getDescriptor().getEnumTypes().get(0);
      }
      
      Type(int value) {
        this.value = value;
      }
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
      if (!(obj instanceof ServerMessages))
        return super.equals(obj); 
      ServerMessages other = (ServerMessages)obj;
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
    
    public static ServerMessages parseFrom(ByteBuffer data) throws InvalidProtocolBufferException {
      return (ServerMessages)PARSER.parseFrom(data);
    }
    
    public static ServerMessages parseFrom(ByteBuffer data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
      return (ServerMessages)PARSER.parseFrom(data, extensionRegistry);
    }
    
    public static ServerMessages parseFrom(ByteString data) throws InvalidProtocolBufferException {
      return (ServerMessages)PARSER.parseFrom(data);
    }
    
    public static ServerMessages parseFrom(ByteString data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
      return (ServerMessages)PARSER.parseFrom(data, extensionRegistry);
    }
    
    public static ServerMessages parseFrom(byte[] data) throws InvalidProtocolBufferException {
      return (ServerMessages)PARSER.parseFrom(data);
    }
    
    public static ServerMessages parseFrom(byte[] data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
      return (ServerMessages)PARSER.parseFrom(data, extensionRegistry);
    }
    
    public static ServerMessages parseFrom(InputStream input) throws IOException {
      return 
        (ServerMessages)GeneratedMessageV3.parseWithIOException(PARSER, input);
    }
    
    public static ServerMessages parseFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
      return 
        (ServerMessages)GeneratedMessageV3.parseWithIOException(PARSER, input, extensionRegistry);
    }
    
    public static ServerMessages parseDelimitedFrom(InputStream input) throws IOException {
      return 
        (ServerMessages)GeneratedMessageV3.parseDelimitedWithIOException(PARSER, input);
    }
    
    public static ServerMessages parseDelimitedFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
      return 
        (ServerMessages)GeneratedMessageV3.parseDelimitedWithIOException(PARSER, input, extensionRegistry);
    }
    
    public static ServerMessages parseFrom(CodedInputStream input) throws IOException {
      return 
        (ServerMessages)GeneratedMessageV3.parseWithIOException(PARSER, input);
    }
    
    public static ServerMessages parseFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
      return 
        (ServerMessages)GeneratedMessageV3.parseWithIOException(PARSER, input, extensionRegistry);
    }
    
    public Builder newBuilderForType() {
      return newBuilder();
    }
    
    public static Builder newBuilder() {
      return DEFAULT_INSTANCE.toBuilder();
    }
    
    public static Builder newBuilder(ServerMessages prototype) {
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
    
    public static final class Builder extends GeneratedMessageV3.Builder<Builder> implements Mysqlx.ServerMessagesOrBuilder {
      public static final Descriptors.Descriptor getDescriptor() {
        return Mysqlx.internal_static_Mysqlx_ServerMessages_descriptor;
      }
      
      protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() {
        return Mysqlx.internal_static_Mysqlx_ServerMessages_fieldAccessorTable
          .ensureFieldAccessorsInitialized(Mysqlx.ServerMessages.class, Builder.class);
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
        return Mysqlx.internal_static_Mysqlx_ServerMessages_descriptor;
      }
      
      public Mysqlx.ServerMessages getDefaultInstanceForType() {
        return Mysqlx.ServerMessages.getDefaultInstance();
      }
      
      public Mysqlx.ServerMessages build() {
        Mysqlx.ServerMessages result = buildPartial();
        if (!result.isInitialized())
          throw newUninitializedMessageException(result); 
        return result;
      }
      
      public Mysqlx.ServerMessages buildPartial() {
        Mysqlx.ServerMessages result = new Mysqlx.ServerMessages(this);
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
        if (other instanceof Mysqlx.ServerMessages)
          return mergeFrom((Mysqlx.ServerMessages)other); 
        super.mergeFrom(other);
        return this;
      }
      
      public Builder mergeFrom(Mysqlx.ServerMessages other) {
        if (other == Mysqlx.ServerMessages.getDefaultInstance())
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
    
    private static final ServerMessages DEFAULT_INSTANCE = new ServerMessages();
    
    public static ServerMessages getDefaultInstance() {
      return DEFAULT_INSTANCE;
    }
    
    @Deprecated
    public static final Parser<ServerMessages> PARSER = (Parser<ServerMessages>)new AbstractParser<ServerMessages>() {
        public Mysqlx.ServerMessages parsePartialFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
          Mysqlx.ServerMessages.Builder builder = Mysqlx.ServerMessages.newBuilder();
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
    
    public static Parser<ServerMessages> parser() {
      return PARSER;
    }
    
    public Parser<ServerMessages> getParserForType() {
      return PARSER;
    }
    
    public ServerMessages getDefaultInstanceForType() {
      return DEFAULT_INSTANCE;
    }
  }
  
  public enum Type implements ProtocolMessageEnum {
    OK(0),
    ERROR(1),
    CONN_CAPABILITIES(2),
    SESS_AUTHENTICATE_CONTINUE(3),
    SESS_AUTHENTICATE_OK(4),
    NOTICE(11),
    RESULTSET_COLUMN_META_DATA(12),
    RESULTSET_ROW(13),
    RESULTSET_FETCH_DONE(14),
    RESULTSET_FETCH_SUSPENDED(15),
    RESULTSET_FETCH_DONE_MORE_RESULTSETS(16),
    SQL_STMT_EXECUTE_OK(17),
    RESULTSET_FETCH_DONE_MORE_OUT_PARAMS(18),
    COMPRESSION(19);
    
    public static final int OK_VALUE = 0;
    
    public static final int ERROR_VALUE = 1;
    
    public static final int CONN_CAPABILITIES_VALUE = 2;
    
    public static final int SESS_AUTHENTICATE_CONTINUE_VALUE = 3;
    
    public static final int SESS_AUTHENTICATE_OK_VALUE = 4;
    
    public static final int NOTICE_VALUE = 11;
    
    public static final int RESULTSET_COLUMN_META_DATA_VALUE = 12;
    
    public static final int RESULTSET_ROW_VALUE = 13;
    
    public static final int RESULTSET_FETCH_DONE_VALUE = 14;
    
    public static final int RESULTSET_FETCH_SUSPENDED_VALUE = 15;
    
    public static final int RESULTSET_FETCH_DONE_MORE_RESULTSETS_VALUE = 16;
    
    public static final int SQL_STMT_EXECUTE_OK_VALUE = 17;
    
    public static final int RESULTSET_FETCH_DONE_MORE_OUT_PARAMS_VALUE = 18;
    
    public static final int COMPRESSION_VALUE = 19;
    
    private static final Internal.EnumLiteMap<Type> internalValueMap = new Internal.EnumLiteMap<Type>() {
        public Mysqlx.ServerMessages.Type findValueByNumber(int number) {
          return Mysqlx.ServerMessages.Type.forNumber(number);
        }
      };
    
    private static final Type[] VALUES = values();
    
    private final int value;
    
    public final int getNumber() {
      return this.value;
    }
    
    public static Type forNumber(int value) {
      switch (value) {
        case 0:
          return OK;
        case 1:
          return ERROR;
        case 2:
          return CONN_CAPABILITIES;
        case 3:
          return SESS_AUTHENTICATE_CONTINUE;
        case 4:
          return SESS_AUTHENTICATE_OK;
        case 11:
          return NOTICE;
        case 12:
          return RESULTSET_COLUMN_META_DATA;
        case 13:
          return RESULTSET_ROW;
        case 14:
          return RESULTSET_FETCH_DONE;
        case 15:
          return RESULTSET_FETCH_SUSPENDED;
        case 16:
          return RESULTSET_FETCH_DONE_MORE_RESULTSETS;
        case 17:
          return SQL_STMT_EXECUTE_OK;
        case 18:
          return RESULTSET_FETCH_DONE_MORE_OUT_PARAMS;
        case 19:
          return COMPRESSION;
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
      return Mysqlx.ServerMessages.getDescriptor().getEnumTypes().get(0);
    }
    
    Type(int value) {
      this.value = value;
    }
  }
  
  public static final class Ok extends GeneratedMessageV3 implements OkOrBuilder {
    private static final long serialVersionUID = 0L;
    
    private int bitField0_;
    
    public static final int MSG_FIELD_NUMBER = 1;
    
    private volatile Object msg_;
    
    private byte memoizedIsInitialized;
    
    private Ok(GeneratedMessageV3.Builder<?> builder) {
      super(builder);
      this.memoizedIsInitialized = -1;
    }
    
    private Ok() {
      this.memoizedIsInitialized = -1;
      this.msg_ = "";
    }
    
    protected Object newInstance(GeneratedMessageV3.UnusedPrivateParameter unused) {
      return new Ok();
    }
    
    public final UnknownFieldSet getUnknownFields() {
      return this.unknownFields;
    }
    
    public static final Descriptors.Descriptor getDescriptor() {
      return Mysqlx.internal_static_Mysqlx_Ok_descriptor;
    }
    
    protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() {
      return Mysqlx.internal_static_Mysqlx_Ok_fieldAccessorTable.ensureFieldAccessorsInitialized(Ok.class, Builder.class);
    }
    
    public boolean hasMsg() {
      return ((this.bitField0_ & 0x1) != 0);
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
      this.memoizedIsInitialized = 1;
      return true;
    }
    
    public void writeTo(CodedOutputStream output) throws IOException {
      if ((this.bitField0_ & 0x1) != 0)
        GeneratedMessageV3.writeString(output, 1, this.msg_); 
      getUnknownFields().writeTo(output);
    }
    
    public int getSerializedSize() {
      int size = this.memoizedSize;
      if (size != -1)
        return size; 
      size = 0;
      if ((this.bitField0_ & 0x1) != 0)
        size += GeneratedMessageV3.computeStringSize(1, this.msg_); 
      size += getUnknownFields().getSerializedSize();
      this.memoizedSize = size;
      return size;
    }
    
    public boolean equals(Object obj) {
      if (obj == this)
        return true; 
      if (!(obj instanceof Ok))
        return super.equals(obj); 
      Ok other = (Ok)obj;
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
      if (hasMsg()) {
        hash = 37 * hash + 1;
        hash = 53 * hash + getMsg().hashCode();
      } 
      hash = 29 * hash + getUnknownFields().hashCode();
      this.memoizedHashCode = hash;
      return hash;
    }
    
    public static Ok parseFrom(ByteBuffer data) throws InvalidProtocolBufferException {
      return (Ok)PARSER.parseFrom(data);
    }
    
    public static Ok parseFrom(ByteBuffer data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
      return (Ok)PARSER.parseFrom(data, extensionRegistry);
    }
    
    public static Ok parseFrom(ByteString data) throws InvalidProtocolBufferException {
      return (Ok)PARSER.parseFrom(data);
    }
    
    public static Ok parseFrom(ByteString data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
      return (Ok)PARSER.parseFrom(data, extensionRegistry);
    }
    
    public static Ok parseFrom(byte[] data) throws InvalidProtocolBufferException {
      return (Ok)PARSER.parseFrom(data);
    }
    
    public static Ok parseFrom(byte[] data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
      return (Ok)PARSER.parseFrom(data, extensionRegistry);
    }
    
    public static Ok parseFrom(InputStream input) throws IOException {
      return 
        (Ok)GeneratedMessageV3.parseWithIOException(PARSER, input);
    }
    
    public static Ok parseFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
      return 
        (Ok)GeneratedMessageV3.parseWithIOException(PARSER, input, extensionRegistry);
    }
    
    public static Ok parseDelimitedFrom(InputStream input) throws IOException {
      return 
        (Ok)GeneratedMessageV3.parseDelimitedWithIOException(PARSER, input);
    }
    
    public static Ok parseDelimitedFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
      return 
        (Ok)GeneratedMessageV3.parseDelimitedWithIOException(PARSER, input, extensionRegistry);
    }
    
    public static Ok parseFrom(CodedInputStream input) throws IOException {
      return 
        (Ok)GeneratedMessageV3.parseWithIOException(PARSER, input);
    }
    
    public static Ok parseFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
      return 
        (Ok)GeneratedMessageV3.parseWithIOException(PARSER, input, extensionRegistry);
    }
    
    public Builder newBuilderForType() {
      return newBuilder();
    }
    
    public static Builder newBuilder() {
      return DEFAULT_INSTANCE.toBuilder();
    }
    
    public static Builder newBuilder(Ok prototype) {
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
    
    public static final class Builder extends GeneratedMessageV3.Builder<Builder> implements Mysqlx.OkOrBuilder {
      private int bitField0_;
      
      private Object msg_;
      
      public static final Descriptors.Descriptor getDescriptor() {
        return Mysqlx.internal_static_Mysqlx_Ok_descriptor;
      }
      
      protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() {
        return Mysqlx.internal_static_Mysqlx_Ok_fieldAccessorTable
          .ensureFieldAccessorsInitialized(Mysqlx.Ok.class, Builder.class);
      }
      
      private Builder() {
        this.msg_ = "";
      }
      
      private Builder(GeneratedMessageV3.BuilderParent parent) {
        super(parent);
        this.msg_ = "";
      }
      
      public Builder clear() {
        super.clear();
        this.msg_ = "";
        this.bitField0_ &= 0xFFFFFFFE;
        return this;
      }
      
      public Descriptors.Descriptor getDescriptorForType() {
        return Mysqlx.internal_static_Mysqlx_Ok_descriptor;
      }
      
      public Mysqlx.Ok getDefaultInstanceForType() {
        return Mysqlx.Ok.getDefaultInstance();
      }
      
      public Mysqlx.Ok build() {
        Mysqlx.Ok result = buildPartial();
        if (!result.isInitialized())
          throw newUninitializedMessageException(result); 
        return result;
      }
      
      public Mysqlx.Ok buildPartial() {
        Mysqlx.Ok result = new Mysqlx.Ok(this);
        int from_bitField0_ = this.bitField0_;
        int to_bitField0_ = 0;
        if ((from_bitField0_ & 0x1) != 0)
          to_bitField0_ |= 0x1; 
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
        if (other instanceof Mysqlx.Ok)
          return mergeFrom((Mysqlx.Ok)other); 
        super.mergeFrom(other);
        return this;
      }
      
      public Builder mergeFrom(Mysqlx.Ok other) {
        if (other == Mysqlx.Ok.getDefaultInstance())
          return this; 
        if (other.hasMsg()) {
          this.bitField0_ |= 0x1;
          this.msg_ = other.msg_;
          onChanged();
        } 
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
              case 10:
                this.msg_ = input.readBytes();
                this.bitField0_ |= 0x1;
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
      
      public boolean hasMsg() {
        return ((this.bitField0_ & 0x1) != 0);
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
        this.bitField0_ |= 0x1;
        this.msg_ = value;
        onChanged();
        return this;
      }
      
      public Builder clearMsg() {
        this.bitField0_ &= 0xFFFFFFFE;
        this.msg_ = Mysqlx.Ok.getDefaultInstance().getMsg();
        onChanged();
        return this;
      }
      
      public Builder setMsgBytes(ByteString value) {
        if (value == null)
          throw new NullPointerException(); 
        this.bitField0_ |= 0x1;
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
    
    private static final Ok DEFAULT_INSTANCE = new Ok();
    
    public static Ok getDefaultInstance() {
      return DEFAULT_INSTANCE;
    }
    
    @Deprecated
    public static final Parser<Ok> PARSER = (Parser<Ok>)new AbstractParser<Ok>() {
        public Mysqlx.Ok parsePartialFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
          Mysqlx.Ok.Builder builder = Mysqlx.Ok.newBuilder();
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
    
    public static Parser<Ok> parser() {
      return PARSER;
    }
    
    public Parser<Ok> getParserForType() {
      return PARSER;
    }
    
    public Ok getDefaultInstanceForType() {
      return DEFAULT_INSTANCE;
    }
  }
  
  public static final class Error extends GeneratedMessageV3 implements ErrorOrBuilder {
    private static final long serialVersionUID = 0L;
    
    private int bitField0_;
    
    public static final int SEVERITY_FIELD_NUMBER = 1;
    
    private int severity_;
    
    public static final int CODE_FIELD_NUMBER = 2;
    
    private int code_;
    
    public static final int SQL_STATE_FIELD_NUMBER = 4;
    
    private volatile Object sqlState_;
    
    public static final int MSG_FIELD_NUMBER = 3;
    
    private volatile Object msg_;
    
    private byte memoizedIsInitialized;
    
    private Error(GeneratedMessageV3.Builder<?> builder) {
      super(builder);
      this.memoizedIsInitialized = -1;
    }
    
    private Error() {
      this.memoizedIsInitialized = -1;
      this.severity_ = 0;
      this.sqlState_ = "";
      this.msg_ = "";
    }
    
    protected Object newInstance(GeneratedMessageV3.UnusedPrivateParameter unused) {
      return new Error();
    }
    
    public final UnknownFieldSet getUnknownFields() {
      return this.unknownFields;
    }
    
    public static final Descriptors.Descriptor getDescriptor() {
      return Mysqlx.internal_static_Mysqlx_Error_descriptor;
    }
    
    protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() {
      return Mysqlx.internal_static_Mysqlx_Error_fieldAccessorTable.ensureFieldAccessorsInitialized(Error.class, Builder.class);
    }
    
    public enum Severity implements ProtocolMessageEnum {
      ERROR(0),
      FATAL(1);
      
      public static final int ERROR_VALUE = 0;
      
      public static final int FATAL_VALUE = 1;
      
      private static final Internal.EnumLiteMap<Severity> internalValueMap = new Internal.EnumLiteMap<Severity>() {
          public Mysqlx.Error.Severity findValueByNumber(int number) {
            return Mysqlx.Error.Severity.forNumber(number);
          }
        };
      
      private static final Severity[] VALUES = values();
      
      private final int value;
      
      public final int getNumber() {
        return this.value;
      }
      
      public static Severity forNumber(int value) {
        switch (value) {
          case 0:
            return ERROR;
          case 1:
            return FATAL;
        } 
        return null;
      }
      
      public static Internal.EnumLiteMap<Severity> internalGetValueMap() {
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
        return Mysqlx.Error.getDescriptor().getEnumTypes().get(0);
      }
      
      Severity(int value) {
        this.value = value;
      }
    }
    
    public boolean hasSeverity() {
      return ((this.bitField0_ & 0x1) != 0);
    }
    
    public Severity getSeverity() {
      Severity result = Severity.valueOf(this.severity_);
      return (result == null) ? Severity.ERROR : result;
    }
    
    public boolean hasCode() {
      return ((this.bitField0_ & 0x2) != 0);
    }
    
    public int getCode() {
      return this.code_;
    }
    
    public boolean hasSqlState() {
      return ((this.bitField0_ & 0x4) != 0);
    }
    
    public String getSqlState() {
      Object ref = this.sqlState_;
      if (ref instanceof String)
        return (String)ref; 
      ByteString bs = (ByteString)ref;
      String s = bs.toStringUtf8();
      if (bs.isValidUtf8())
        this.sqlState_ = s; 
      return s;
    }
    
    public ByteString getSqlStateBytes() {
      Object ref = this.sqlState_;
      if (ref instanceof String) {
        ByteString b = ByteString.copyFromUtf8((String)ref);
        this.sqlState_ = b;
        return b;
      } 
      return (ByteString)ref;
    }
    
    public boolean hasMsg() {
      return ((this.bitField0_ & 0x8) != 0);
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
      if (!hasSqlState()) {
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
        output.writeEnum(1, this.severity_); 
      if ((this.bitField0_ & 0x2) != 0)
        output.writeUInt32(2, this.code_); 
      if ((this.bitField0_ & 0x8) != 0)
        GeneratedMessageV3.writeString(output, 3, this.msg_); 
      if ((this.bitField0_ & 0x4) != 0)
        GeneratedMessageV3.writeString(output, 4, this.sqlState_); 
      getUnknownFields().writeTo(output);
    }
    
    public int getSerializedSize() {
      int size = this.memoizedSize;
      if (size != -1)
        return size; 
      size = 0;
      if ((this.bitField0_ & 0x1) != 0)
        size += 
          CodedOutputStream.computeEnumSize(1, this.severity_); 
      if ((this.bitField0_ & 0x2) != 0)
        size += 
          CodedOutputStream.computeUInt32Size(2, this.code_); 
      if ((this.bitField0_ & 0x8) != 0)
        size += GeneratedMessageV3.computeStringSize(3, this.msg_); 
      if ((this.bitField0_ & 0x4) != 0)
        size += GeneratedMessageV3.computeStringSize(4, this.sqlState_); 
      size += getUnknownFields().getSerializedSize();
      this.memoizedSize = size;
      return size;
    }
    
    public boolean equals(Object obj) {
      if (obj == this)
        return true; 
      if (!(obj instanceof Error))
        return super.equals(obj); 
      Error other = (Error)obj;
      if (hasSeverity() != other.hasSeverity())
        return false; 
      if (hasSeverity() && 
        this.severity_ != other.severity_)
        return false; 
      if (hasCode() != other.hasCode())
        return false; 
      if (hasCode() && 
        getCode() != other
        .getCode())
        return false; 
      if (hasSqlState() != other.hasSqlState())
        return false; 
      if (hasSqlState() && 
        
        !getSqlState().equals(other.getSqlState()))
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
      if (hasSeverity()) {
        hash = 37 * hash + 1;
        hash = 53 * hash + this.severity_;
      } 
      if (hasCode()) {
        hash = 37 * hash + 2;
        hash = 53 * hash + getCode();
      } 
      if (hasSqlState()) {
        hash = 37 * hash + 4;
        hash = 53 * hash + getSqlState().hashCode();
      } 
      if (hasMsg()) {
        hash = 37 * hash + 3;
        hash = 53 * hash + getMsg().hashCode();
      } 
      hash = 29 * hash + getUnknownFields().hashCode();
      this.memoizedHashCode = hash;
      return hash;
    }
    
    public static Error parseFrom(ByteBuffer data) throws InvalidProtocolBufferException {
      return (Error)PARSER.parseFrom(data);
    }
    
    public static Error parseFrom(ByteBuffer data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
      return (Error)PARSER.parseFrom(data, extensionRegistry);
    }
    
    public static Error parseFrom(ByteString data) throws InvalidProtocolBufferException {
      return (Error)PARSER.parseFrom(data);
    }
    
    public static Error parseFrom(ByteString data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
      return (Error)PARSER.parseFrom(data, extensionRegistry);
    }
    
    public static Error parseFrom(byte[] data) throws InvalidProtocolBufferException {
      return (Error)PARSER.parseFrom(data);
    }
    
    public static Error parseFrom(byte[] data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
      return (Error)PARSER.parseFrom(data, extensionRegistry);
    }
    
    public static Error parseFrom(InputStream input) throws IOException {
      return 
        (Error)GeneratedMessageV3.parseWithIOException(PARSER, input);
    }
    
    public static Error parseFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
      return 
        (Error)GeneratedMessageV3.parseWithIOException(PARSER, input, extensionRegistry);
    }
    
    public static Error parseDelimitedFrom(InputStream input) throws IOException {
      return 
        (Error)GeneratedMessageV3.parseDelimitedWithIOException(PARSER, input);
    }
    
    public static Error parseDelimitedFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
      return 
        (Error)GeneratedMessageV3.parseDelimitedWithIOException(PARSER, input, extensionRegistry);
    }
    
    public static Error parseFrom(CodedInputStream input) throws IOException {
      return 
        (Error)GeneratedMessageV3.parseWithIOException(PARSER, input);
    }
    
    public static Error parseFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
      return 
        (Error)GeneratedMessageV3.parseWithIOException(PARSER, input, extensionRegistry);
    }
    
    public Builder newBuilderForType() {
      return newBuilder();
    }
    
    public static Builder newBuilder() {
      return DEFAULT_INSTANCE.toBuilder();
    }
    
    public static Builder newBuilder(Error prototype) {
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
    
    public static final class Builder extends GeneratedMessageV3.Builder<Builder> implements Mysqlx.ErrorOrBuilder {
      private int bitField0_;
      
      private int severity_;
      
      private int code_;
      
      private Object sqlState_;
      
      private Object msg_;
      
      public static final Descriptors.Descriptor getDescriptor() {
        return Mysqlx.internal_static_Mysqlx_Error_descriptor;
      }
      
      protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() {
        return Mysqlx.internal_static_Mysqlx_Error_fieldAccessorTable
          .ensureFieldAccessorsInitialized(Mysqlx.Error.class, Builder.class);
      }
      
      private Builder() {
        this.severity_ = 0;
        this.sqlState_ = "";
        this.msg_ = "";
      }
      
      private Builder(GeneratedMessageV3.BuilderParent parent) {
        super(parent);
        this.severity_ = 0;
        this.sqlState_ = "";
        this.msg_ = "";
      }
      
      public Builder clear() {
        super.clear();
        this.severity_ = 0;
        this.bitField0_ &= 0xFFFFFFFE;
        this.code_ = 0;
        this.bitField0_ &= 0xFFFFFFFD;
        this.sqlState_ = "";
        this.bitField0_ &= 0xFFFFFFFB;
        this.msg_ = "";
        this.bitField0_ &= 0xFFFFFFF7;
        return this;
      }
      
      public Descriptors.Descriptor getDescriptorForType() {
        return Mysqlx.internal_static_Mysqlx_Error_descriptor;
      }
      
      public Mysqlx.Error getDefaultInstanceForType() {
        return Mysqlx.Error.getDefaultInstance();
      }
      
      public Mysqlx.Error build() {
        Mysqlx.Error result = buildPartial();
        if (!result.isInitialized())
          throw newUninitializedMessageException(result); 
        return result;
      }
      
      public Mysqlx.Error buildPartial() {
        Mysqlx.Error result = new Mysqlx.Error(this);
        int from_bitField0_ = this.bitField0_;
        int to_bitField0_ = 0;
        if ((from_bitField0_ & 0x1) != 0)
          to_bitField0_ |= 0x1; 
        result.severity_ = this.severity_;
        if ((from_bitField0_ & 0x2) != 0) {
          result.code_ = this.code_;
          to_bitField0_ |= 0x2;
        } 
        if ((from_bitField0_ & 0x4) != 0)
          to_bitField0_ |= 0x4; 
        result.sqlState_ = this.sqlState_;
        if ((from_bitField0_ & 0x8) != 0)
          to_bitField0_ |= 0x8; 
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
        if (other instanceof Mysqlx.Error)
          return mergeFrom((Mysqlx.Error)other); 
        super.mergeFrom(other);
        return this;
      }
      
      public Builder mergeFrom(Mysqlx.Error other) {
        if (other == Mysqlx.Error.getDefaultInstance())
          return this; 
        if (other.hasSeverity())
          setSeverity(other.getSeverity()); 
        if (other.hasCode())
          setCode(other.getCode()); 
        if (other.hasSqlState()) {
          this.bitField0_ |= 0x4;
          this.sqlState_ = other.sqlState_;
          onChanged();
        } 
        if (other.hasMsg()) {
          this.bitField0_ |= 0x8;
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
        if (!hasSqlState())
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
            Mysqlx.Error.Severity tmpValue;
            int tag = input.readTag();
            switch (tag) {
              case 0:
                done = true;
                continue;
              case 8:
                tmpRaw = input.readEnum();
                tmpValue = Mysqlx.Error.Severity.forNumber(tmpRaw);
                if (tmpValue == null) {
                  mergeUnknownVarintField(1, tmpRaw);
                  continue;
                } 
                this.severity_ = tmpRaw;
                this.bitField0_ |= 0x1;
                continue;
              case 16:
                this.code_ = input.readUInt32();
                this.bitField0_ |= 0x2;
                continue;
              case 26:
                this.msg_ = input.readBytes();
                this.bitField0_ |= 0x8;
                continue;
              case 34:
                this.sqlState_ = input.readBytes();
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
      
      public boolean hasSeverity() {
        return ((this.bitField0_ & 0x1) != 0);
      }
      
      public Mysqlx.Error.Severity getSeverity() {
        Mysqlx.Error.Severity result = Mysqlx.Error.Severity.valueOf(this.severity_);
        return (result == null) ? Mysqlx.Error.Severity.ERROR : result;
      }
      
      public Builder setSeverity(Mysqlx.Error.Severity value) {
        if (value == null)
          throw new NullPointerException(); 
        this.bitField0_ |= 0x1;
        this.severity_ = value.getNumber();
        onChanged();
        return this;
      }
      
      public Builder clearSeverity() {
        this.bitField0_ &= 0xFFFFFFFE;
        this.severity_ = 0;
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
      
      public boolean hasSqlState() {
        return ((this.bitField0_ & 0x4) != 0);
      }
      
      public String getSqlState() {
        Object ref = this.sqlState_;
        if (!(ref instanceof String)) {
          ByteString bs = (ByteString)ref;
          String s = bs.toStringUtf8();
          if (bs.isValidUtf8())
            this.sqlState_ = s; 
          return s;
        } 
        return (String)ref;
      }
      
      public ByteString getSqlStateBytes() {
        Object ref = this.sqlState_;
        if (ref instanceof String) {
          ByteString b = ByteString.copyFromUtf8((String)ref);
          this.sqlState_ = b;
          return b;
        } 
        return (ByteString)ref;
      }
      
      public Builder setSqlState(String value) {
        if (value == null)
          throw new NullPointerException(); 
        this.bitField0_ |= 0x4;
        this.sqlState_ = value;
        onChanged();
        return this;
      }
      
      public Builder clearSqlState() {
        this.bitField0_ &= 0xFFFFFFFB;
        this.sqlState_ = Mysqlx.Error.getDefaultInstance().getSqlState();
        onChanged();
        return this;
      }
      
      public Builder setSqlStateBytes(ByteString value) {
        if (value == null)
          throw new NullPointerException(); 
        this.bitField0_ |= 0x4;
        this.sqlState_ = value;
        onChanged();
        return this;
      }
      
      public boolean hasMsg() {
        return ((this.bitField0_ & 0x8) != 0);
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
        this.bitField0_ |= 0x8;
        this.msg_ = value;
        onChanged();
        return this;
      }
      
      public Builder clearMsg() {
        this.bitField0_ &= 0xFFFFFFF7;
        this.msg_ = Mysqlx.Error.getDefaultInstance().getMsg();
        onChanged();
        return this;
      }
      
      public Builder setMsgBytes(ByteString value) {
        if (value == null)
          throw new NullPointerException(); 
        this.bitField0_ |= 0x8;
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
    
    private static final Error DEFAULT_INSTANCE = new Error();
    
    public static Error getDefaultInstance() {
      return DEFAULT_INSTANCE;
    }
    
    @Deprecated
    public static final Parser<Error> PARSER = (Parser<Error>)new AbstractParser<Error>() {
        public Mysqlx.Error parsePartialFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
          Mysqlx.Error.Builder builder = Mysqlx.Error.newBuilder();
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
    
    public static Parser<Error> parser() {
      return PARSER;
    }
    
    public Parser<Error> getParserForType() {
      return PARSER;
    }
    
    public Error getDefaultInstanceForType() {
      return DEFAULT_INSTANCE;
    }
  }
  
  public static final GeneratedMessage.GeneratedExtension<DescriptorProtos.MessageOptions, ClientMessages.Type> clientMessageId = GeneratedMessage.newFileScopedGeneratedExtension(ClientMessages.Type.class, null);
  
  public static final int SERVER_MESSAGE_ID_FIELD_NUMBER = 100002;
  
  public static final GeneratedMessage.GeneratedExtension<DescriptorProtos.MessageOptions, ServerMessages.Type> serverMessageId = GeneratedMessage.newFileScopedGeneratedExtension(ServerMessages.Type.class, null);
  
  private static final Descriptors.Descriptor internal_static_Mysqlx_ClientMessages_descriptor;
  
  private static final GeneratedMessageV3.FieldAccessorTable internal_static_Mysqlx_ClientMessages_fieldAccessorTable;
  
  private static final Descriptors.Descriptor internal_static_Mysqlx_ServerMessages_descriptor;
  
  private static final GeneratedMessageV3.FieldAccessorTable internal_static_Mysqlx_ServerMessages_fieldAccessorTable;
  
  private static final Descriptors.Descriptor internal_static_Mysqlx_Ok_descriptor;
  
  private static final GeneratedMessageV3.FieldAccessorTable internal_static_Mysqlx_Ok_fieldAccessorTable;
  
  private static final Descriptors.Descriptor internal_static_Mysqlx_Error_descriptor;
  
  private static final GeneratedMessageV3.FieldAccessorTable internal_static_Mysqlx_Error_fieldAccessorTable;
  
  private static Descriptors.FileDescriptor descriptor;
  
  public static Descriptors.FileDescriptor getDescriptor() {
    return descriptor;
  }
  
  static {
    String[] descriptorData = { "\n\fmysqlx.proto\022\006Mysqlx\032 google/protobuf/descriptor.proto\"ü\003\n\016ClientMessages\"é\003\n\004Type\022\030\n\024CON_CAPABILITIES_GET\020\001\022\030\n\024CON_CAPABILITIES_SET\020\002\022\r\n\tCON_CLOSE\020\003\022\033\n\027SESS_AUTHENTICATE_START\020\004\022\036\n\032SESS_AUTHENTICATE_CONTINUE\020\005\022\016\n\nSESS_RESET\020\006\022\016\n\nSESS_CLOSE\020\007\022\024\n\020SQL_STMT_EXECUTE\020\f\022\r\n\tCRUD_FIND\020\021\022\017\n\013CRUD_INSERT\020\022\022\017\n\013CRUD_UPDATE\020\023\022\017\n\013CRUD_DELETE\020\024\022\017\n\013EXPECT_OPEN\020\030\022\020\n\fEXPECT_CLOSE\020\031\022\024\n\020CRUD_CREATE_VIEW\020\036\022\024\n\020CRUD_MODIFY_VIEW\020\037\022\022\n\016CRUD_DROP_VIEW\020 \022\023\n\017PREPARE_PREPARE\020(\022\023\n\017PREPARE_EXECUTE\020)\022\026\n\022PREPARE_DEALLOCATE\020*\022\017\n\013CURSOR_OPEN\020+\022\020\n\fCURSOR_CLOSE\020,\022\020\n\fCURSOR_FETCH\020-\022\017\n\013COMPRESSION\020.\"ó\002\n\016ServerMessages\"à\002\n\004Type\022\006\n\002OK\020\000\022\t\n\005ERROR\020\001\022\025\n\021CONN_CAPABILITIES\020\002\022\036\n\032SESS_AUTHENTICATE_CONTINUE\020\003\022\030\n\024SESS_AUTHENTICATE_OK\020\004\022\n\n\006NOTICE\020\013\022\036\n\032RESULTSET_COLUMN_META_DATA\020\f\022\021\n\rRESULTSET_ROW\020\r\022\030\n\024RESULTSET_FETCH_DONE\020\016\022\035\n\031RESULTSET_FETCH_SUSPENDED\020\017\022(\n$RESULTSET_FETCH_DONE_MORE_RESULTSETS\020\020\022\027\n\023SQL_STMT_EXECUTE_OK\020\021\022(\n$RESULTSET_FETCH_DONE_MORE_OUT_PARAMS\020\022\022\017\n\013COMPRESSION\020\023\"\027\n\002Ok\022\013\n\003msg\030\001 \001(\t:\004ê0\000\"\001\n\005Error\022/\n\bseverity\030\001 \001(\0162\026.Mysqlx.Error.Severity:\005ERROR\022\f\n\004code\030\002 \002(\r\022\021\n\tsql_state\030\004 \002(\t\022\013\n\003msg\030\003 \002(\t\" \n\bSeverity\022\t\n\005ERROR\020\000\022\t\n\005FATAL\020\001:\004ê0\001:Y\n\021client_message_id\022\037.google.protobuf.MessageOptions\030¡\006 \001(\0162\033.Mysqlx.ClientMessages.Type:Y\n\021server_message_id\022\037.google.protobuf.MessageOptions\030¢\006 \001(\0162\033.Mysqlx.ServerMessages.TypeB\031\n\027com.mysql.cj.x.protobuf" };
    descriptor = Descriptors.FileDescriptor.internalBuildGeneratedFileFrom(descriptorData, new Descriptors.FileDescriptor[] { DescriptorProtos.getDescriptor() });
    internal_static_Mysqlx_ClientMessages_descriptor = getDescriptor().getMessageTypes().get(0);
    internal_static_Mysqlx_ClientMessages_fieldAccessorTable = new GeneratedMessageV3.FieldAccessorTable(internal_static_Mysqlx_ClientMessages_descriptor, new String[0]);
    internal_static_Mysqlx_ServerMessages_descriptor = getDescriptor().getMessageTypes().get(1);
    internal_static_Mysqlx_ServerMessages_fieldAccessorTable = new GeneratedMessageV3.FieldAccessorTable(internal_static_Mysqlx_ServerMessages_descriptor, new String[0]);
    internal_static_Mysqlx_Ok_descriptor = getDescriptor().getMessageTypes().get(2);
    internal_static_Mysqlx_Ok_fieldAccessorTable = new GeneratedMessageV3.FieldAccessorTable(internal_static_Mysqlx_Ok_descriptor, new String[] { "Msg" });
    internal_static_Mysqlx_Error_descriptor = getDescriptor().getMessageTypes().get(3);
    internal_static_Mysqlx_Error_fieldAccessorTable = new GeneratedMessageV3.FieldAccessorTable(internal_static_Mysqlx_Error_descriptor, new String[] { "Severity", "Code", "SqlState", "Msg" });
    clientMessageId.internalInit(descriptor.getExtensions().get(0));
    serverMessageId.internalInit(descriptor.getExtensions().get(1));
    ExtensionRegistry registry = ExtensionRegistry.newInstance();
    registry.add(serverMessageId);
    Descriptors.FileDescriptor.internalUpdateFileDescriptor(descriptor, registry);
    DescriptorProtos.getDescriptor();
  }
  
  public static interface ErrorOrBuilder extends MessageOrBuilder {
    boolean hasSeverity();
    
    Mysqlx.Error.Severity getSeverity();
    
    boolean hasCode();
    
    int getCode();
    
    boolean hasSqlState();
    
    String getSqlState();
    
    ByteString getSqlStateBytes();
    
    boolean hasMsg();
    
    String getMsg();
    
    ByteString getMsgBytes();
  }
  
  public static interface OkOrBuilder extends MessageOrBuilder {
    boolean hasMsg();
    
    String getMsg();
    
    ByteString getMsgBytes();
  }
  
  public static interface ServerMessagesOrBuilder extends MessageOrBuilder {}
  
  public static interface ClientMessagesOrBuilder extends MessageOrBuilder {}
}
