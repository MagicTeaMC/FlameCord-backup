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
import com.google.protobuf.UninitializedMessageException;
import com.google.protobuf.UnknownFieldSet;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class MysqlxResultset {
  private static final Descriptors.Descriptor internal_static_Mysqlx_Resultset_FetchDoneMoreOutParams_descriptor;
  
  private static final GeneratedMessageV3.FieldAccessorTable internal_static_Mysqlx_Resultset_FetchDoneMoreOutParams_fieldAccessorTable;
  
  private static final Descriptors.Descriptor internal_static_Mysqlx_Resultset_FetchDoneMoreResultsets_descriptor;
  
  private static final GeneratedMessageV3.FieldAccessorTable internal_static_Mysqlx_Resultset_FetchDoneMoreResultsets_fieldAccessorTable;
  
  private static final Descriptors.Descriptor internal_static_Mysqlx_Resultset_FetchDone_descriptor;
  
  private static final GeneratedMessageV3.FieldAccessorTable internal_static_Mysqlx_Resultset_FetchDone_fieldAccessorTable;
  
  private static final Descriptors.Descriptor internal_static_Mysqlx_Resultset_FetchSuspended_descriptor;
  
  private static final GeneratedMessageV3.FieldAccessorTable internal_static_Mysqlx_Resultset_FetchSuspended_fieldAccessorTable;
  
  private static final Descriptors.Descriptor internal_static_Mysqlx_Resultset_ColumnMetaData_descriptor;
  
  private static final GeneratedMessageV3.FieldAccessorTable internal_static_Mysqlx_Resultset_ColumnMetaData_fieldAccessorTable;
  
  private static final Descriptors.Descriptor internal_static_Mysqlx_Resultset_Row_descriptor;
  
  private static final GeneratedMessageV3.FieldAccessorTable internal_static_Mysqlx_Resultset_Row_fieldAccessorTable;
  
  private static Descriptors.FileDescriptor descriptor;
  
  public static void registerAllExtensions(ExtensionRegistryLite registry) {}
  
  public static void registerAllExtensions(ExtensionRegistry registry) {
    registerAllExtensions((ExtensionRegistryLite)registry);
  }
  
  public enum ContentType_BYTES implements ProtocolMessageEnum {
    GEOMETRY(1),
    JSON(2),
    XML(3);
    
    public static final int GEOMETRY_VALUE = 1;
    
    public static final int JSON_VALUE = 2;
    
    public static final int XML_VALUE = 3;
    
    private static final Internal.EnumLiteMap<ContentType_BYTES> internalValueMap = new Internal.EnumLiteMap<ContentType_BYTES>() {
        public MysqlxResultset.ContentType_BYTES findValueByNumber(int number) {
          return MysqlxResultset.ContentType_BYTES.forNumber(number);
        }
      };
    
    private static final ContentType_BYTES[] VALUES = values();
    
    private final int value;
    
    public final int getNumber() {
      return this.value;
    }
    
    public static ContentType_BYTES forNumber(int value) {
      switch (value) {
        case 1:
          return GEOMETRY;
        case 2:
          return JSON;
        case 3:
          return XML;
      } 
      return null;
    }
    
    public static Internal.EnumLiteMap<ContentType_BYTES> internalGetValueMap() {
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
      return MysqlxResultset.getDescriptor().getEnumTypes().get(0);
    }
    
    ContentType_BYTES(int value) {
      this.value = value;
    }
  }
  
  public enum ContentType_DATETIME implements ProtocolMessageEnum {
    DATE(1),
    DATETIME(2);
    
    public static final int DATE_VALUE = 1;
    
    public static final int DATETIME_VALUE = 2;
    
    private static final Internal.EnumLiteMap<ContentType_DATETIME> internalValueMap = new Internal.EnumLiteMap<ContentType_DATETIME>() {
        public MysqlxResultset.ContentType_DATETIME findValueByNumber(int number) {
          return MysqlxResultset.ContentType_DATETIME.forNumber(number);
        }
      };
    
    private static final ContentType_DATETIME[] VALUES = values();
    
    private final int value;
    
    public final int getNumber() {
      return this.value;
    }
    
    public static ContentType_DATETIME forNumber(int value) {
      switch (value) {
        case 1:
          return DATE;
        case 2:
          return DATETIME;
      } 
      return null;
    }
    
    public static Internal.EnumLiteMap<ContentType_DATETIME> internalGetValueMap() {
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
      return MysqlxResultset.getDescriptor().getEnumTypes().get(1);
    }
    
    ContentType_DATETIME(int value) {
      this.value = value;
    }
  }
  
  public static interface FetchDoneMoreOutParamsOrBuilder extends MessageOrBuilder {}
  
  public static final class FetchDoneMoreOutParams extends GeneratedMessageV3 implements FetchDoneMoreOutParamsOrBuilder {
    private static final long serialVersionUID = 0L;
    
    private byte memoizedIsInitialized;
    
    private FetchDoneMoreOutParams(GeneratedMessageV3.Builder<?> builder) {
      super(builder);
      this.memoizedIsInitialized = -1;
    }
    
    private FetchDoneMoreOutParams() {
      this.memoizedIsInitialized = -1;
    }
    
    protected Object newInstance(GeneratedMessageV3.UnusedPrivateParameter unused) {
      return new FetchDoneMoreOutParams();
    }
    
    public final UnknownFieldSet getUnknownFields() {
      return this.unknownFields;
    }
    
    public static final Descriptors.Descriptor getDescriptor() {
      return MysqlxResultset.internal_static_Mysqlx_Resultset_FetchDoneMoreOutParams_descriptor;
    }
    
    protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() {
      return MysqlxResultset.internal_static_Mysqlx_Resultset_FetchDoneMoreOutParams_fieldAccessorTable.ensureFieldAccessorsInitialized(FetchDoneMoreOutParams.class, Builder.class);
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
      if (!(obj instanceof FetchDoneMoreOutParams))
        return super.equals(obj); 
      FetchDoneMoreOutParams other = (FetchDoneMoreOutParams)obj;
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
    
    public static FetchDoneMoreOutParams parseFrom(ByteBuffer data) throws InvalidProtocolBufferException {
      return (FetchDoneMoreOutParams)PARSER.parseFrom(data);
    }
    
    public static FetchDoneMoreOutParams parseFrom(ByteBuffer data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
      return (FetchDoneMoreOutParams)PARSER.parseFrom(data, extensionRegistry);
    }
    
    public static FetchDoneMoreOutParams parseFrom(ByteString data) throws InvalidProtocolBufferException {
      return (FetchDoneMoreOutParams)PARSER.parseFrom(data);
    }
    
    public static FetchDoneMoreOutParams parseFrom(ByteString data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
      return (FetchDoneMoreOutParams)PARSER.parseFrom(data, extensionRegistry);
    }
    
    public static FetchDoneMoreOutParams parseFrom(byte[] data) throws InvalidProtocolBufferException {
      return (FetchDoneMoreOutParams)PARSER.parseFrom(data);
    }
    
    public static FetchDoneMoreOutParams parseFrom(byte[] data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
      return (FetchDoneMoreOutParams)PARSER.parseFrom(data, extensionRegistry);
    }
    
    public static FetchDoneMoreOutParams parseFrom(InputStream input) throws IOException {
      return 
        (FetchDoneMoreOutParams)GeneratedMessageV3.parseWithIOException(PARSER, input);
    }
    
    public static FetchDoneMoreOutParams parseFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
      return 
        (FetchDoneMoreOutParams)GeneratedMessageV3.parseWithIOException(PARSER, input, extensionRegistry);
    }
    
    public static FetchDoneMoreOutParams parseDelimitedFrom(InputStream input) throws IOException {
      return 
        (FetchDoneMoreOutParams)GeneratedMessageV3.parseDelimitedWithIOException(PARSER, input);
    }
    
    public static FetchDoneMoreOutParams parseDelimitedFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
      return 
        (FetchDoneMoreOutParams)GeneratedMessageV3.parseDelimitedWithIOException(PARSER, input, extensionRegistry);
    }
    
    public static FetchDoneMoreOutParams parseFrom(CodedInputStream input) throws IOException {
      return 
        (FetchDoneMoreOutParams)GeneratedMessageV3.parseWithIOException(PARSER, input);
    }
    
    public static FetchDoneMoreOutParams parseFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
      return 
        (FetchDoneMoreOutParams)GeneratedMessageV3.parseWithIOException(PARSER, input, extensionRegistry);
    }
    
    public Builder newBuilderForType() {
      return newBuilder();
    }
    
    public static Builder newBuilder() {
      return DEFAULT_INSTANCE.toBuilder();
    }
    
    public static Builder newBuilder(FetchDoneMoreOutParams prototype) {
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
    
    public static final class Builder extends GeneratedMessageV3.Builder<Builder> implements MysqlxResultset.FetchDoneMoreOutParamsOrBuilder {
      public static final Descriptors.Descriptor getDescriptor() {
        return MysqlxResultset.internal_static_Mysqlx_Resultset_FetchDoneMoreOutParams_descriptor;
      }
      
      protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() {
        return MysqlxResultset.internal_static_Mysqlx_Resultset_FetchDoneMoreOutParams_fieldAccessorTable
          .ensureFieldAccessorsInitialized(MysqlxResultset.FetchDoneMoreOutParams.class, Builder.class);
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
        return MysqlxResultset.internal_static_Mysqlx_Resultset_FetchDoneMoreOutParams_descriptor;
      }
      
      public MysqlxResultset.FetchDoneMoreOutParams getDefaultInstanceForType() {
        return MysqlxResultset.FetchDoneMoreOutParams.getDefaultInstance();
      }
      
      public MysqlxResultset.FetchDoneMoreOutParams build() {
        MysqlxResultset.FetchDoneMoreOutParams result = buildPartial();
        if (!result.isInitialized())
          throw newUninitializedMessageException(result); 
        return result;
      }
      
      public MysqlxResultset.FetchDoneMoreOutParams buildPartial() {
        MysqlxResultset.FetchDoneMoreOutParams result = new MysqlxResultset.FetchDoneMoreOutParams(this);
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
        if (other instanceof MysqlxResultset.FetchDoneMoreOutParams)
          return mergeFrom((MysqlxResultset.FetchDoneMoreOutParams)other); 
        super.mergeFrom(other);
        return this;
      }
      
      public Builder mergeFrom(MysqlxResultset.FetchDoneMoreOutParams other) {
        if (other == MysqlxResultset.FetchDoneMoreOutParams.getDefaultInstance())
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
    
    private static final FetchDoneMoreOutParams DEFAULT_INSTANCE = new FetchDoneMoreOutParams();
    
    public static FetchDoneMoreOutParams getDefaultInstance() {
      return DEFAULT_INSTANCE;
    }
    
    @Deprecated
    public static final Parser<FetchDoneMoreOutParams> PARSER = (Parser<FetchDoneMoreOutParams>)new AbstractParser<FetchDoneMoreOutParams>() {
        public MysqlxResultset.FetchDoneMoreOutParams parsePartialFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
          MysqlxResultset.FetchDoneMoreOutParams.Builder builder = MysqlxResultset.FetchDoneMoreOutParams.newBuilder();
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
    
    public static Parser<FetchDoneMoreOutParams> parser() {
      return PARSER;
    }
    
    public Parser<FetchDoneMoreOutParams> getParserForType() {
      return PARSER;
    }
    
    public FetchDoneMoreOutParams getDefaultInstanceForType() {
      return DEFAULT_INSTANCE;
    }
  }
  
  public static interface FetchDoneMoreResultsetsOrBuilder extends MessageOrBuilder {}
  
  public static final class FetchDoneMoreResultsets extends GeneratedMessageV3 implements FetchDoneMoreResultsetsOrBuilder {
    private static final long serialVersionUID = 0L;
    
    private byte memoizedIsInitialized;
    
    private FetchDoneMoreResultsets(GeneratedMessageV3.Builder<?> builder) {
      super(builder);
      this.memoizedIsInitialized = -1;
    }
    
    private FetchDoneMoreResultsets() {
      this.memoizedIsInitialized = -1;
    }
    
    protected Object newInstance(GeneratedMessageV3.UnusedPrivateParameter unused) {
      return new FetchDoneMoreResultsets();
    }
    
    public final UnknownFieldSet getUnknownFields() {
      return this.unknownFields;
    }
    
    public static final Descriptors.Descriptor getDescriptor() {
      return MysqlxResultset.internal_static_Mysqlx_Resultset_FetchDoneMoreResultsets_descriptor;
    }
    
    protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() {
      return MysqlxResultset.internal_static_Mysqlx_Resultset_FetchDoneMoreResultsets_fieldAccessorTable.ensureFieldAccessorsInitialized(FetchDoneMoreResultsets.class, Builder.class);
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
      if (!(obj instanceof FetchDoneMoreResultsets))
        return super.equals(obj); 
      FetchDoneMoreResultsets other = (FetchDoneMoreResultsets)obj;
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
    
    public static FetchDoneMoreResultsets parseFrom(ByteBuffer data) throws InvalidProtocolBufferException {
      return (FetchDoneMoreResultsets)PARSER.parseFrom(data);
    }
    
    public static FetchDoneMoreResultsets parseFrom(ByteBuffer data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
      return (FetchDoneMoreResultsets)PARSER.parseFrom(data, extensionRegistry);
    }
    
    public static FetchDoneMoreResultsets parseFrom(ByteString data) throws InvalidProtocolBufferException {
      return (FetchDoneMoreResultsets)PARSER.parseFrom(data);
    }
    
    public static FetchDoneMoreResultsets parseFrom(ByteString data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
      return (FetchDoneMoreResultsets)PARSER.parseFrom(data, extensionRegistry);
    }
    
    public static FetchDoneMoreResultsets parseFrom(byte[] data) throws InvalidProtocolBufferException {
      return (FetchDoneMoreResultsets)PARSER.parseFrom(data);
    }
    
    public static FetchDoneMoreResultsets parseFrom(byte[] data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
      return (FetchDoneMoreResultsets)PARSER.parseFrom(data, extensionRegistry);
    }
    
    public static FetchDoneMoreResultsets parseFrom(InputStream input) throws IOException {
      return 
        (FetchDoneMoreResultsets)GeneratedMessageV3.parseWithIOException(PARSER, input);
    }
    
    public static FetchDoneMoreResultsets parseFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
      return 
        (FetchDoneMoreResultsets)GeneratedMessageV3.parseWithIOException(PARSER, input, extensionRegistry);
    }
    
    public static FetchDoneMoreResultsets parseDelimitedFrom(InputStream input) throws IOException {
      return 
        (FetchDoneMoreResultsets)GeneratedMessageV3.parseDelimitedWithIOException(PARSER, input);
    }
    
    public static FetchDoneMoreResultsets parseDelimitedFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
      return 
        (FetchDoneMoreResultsets)GeneratedMessageV3.parseDelimitedWithIOException(PARSER, input, extensionRegistry);
    }
    
    public static FetchDoneMoreResultsets parseFrom(CodedInputStream input) throws IOException {
      return 
        (FetchDoneMoreResultsets)GeneratedMessageV3.parseWithIOException(PARSER, input);
    }
    
    public static FetchDoneMoreResultsets parseFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
      return 
        (FetchDoneMoreResultsets)GeneratedMessageV3.parseWithIOException(PARSER, input, extensionRegistry);
    }
    
    public Builder newBuilderForType() {
      return newBuilder();
    }
    
    public static Builder newBuilder() {
      return DEFAULT_INSTANCE.toBuilder();
    }
    
    public static Builder newBuilder(FetchDoneMoreResultsets prototype) {
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
    
    public static final class Builder extends GeneratedMessageV3.Builder<Builder> implements MysqlxResultset.FetchDoneMoreResultsetsOrBuilder {
      public static final Descriptors.Descriptor getDescriptor() {
        return MysqlxResultset.internal_static_Mysqlx_Resultset_FetchDoneMoreResultsets_descriptor;
      }
      
      protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() {
        return MysqlxResultset.internal_static_Mysqlx_Resultset_FetchDoneMoreResultsets_fieldAccessorTable
          .ensureFieldAccessorsInitialized(MysqlxResultset.FetchDoneMoreResultsets.class, Builder.class);
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
        return MysqlxResultset.internal_static_Mysqlx_Resultset_FetchDoneMoreResultsets_descriptor;
      }
      
      public MysqlxResultset.FetchDoneMoreResultsets getDefaultInstanceForType() {
        return MysqlxResultset.FetchDoneMoreResultsets.getDefaultInstance();
      }
      
      public MysqlxResultset.FetchDoneMoreResultsets build() {
        MysqlxResultset.FetchDoneMoreResultsets result = buildPartial();
        if (!result.isInitialized())
          throw newUninitializedMessageException(result); 
        return result;
      }
      
      public MysqlxResultset.FetchDoneMoreResultsets buildPartial() {
        MysqlxResultset.FetchDoneMoreResultsets result = new MysqlxResultset.FetchDoneMoreResultsets(this);
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
        if (other instanceof MysqlxResultset.FetchDoneMoreResultsets)
          return mergeFrom((MysqlxResultset.FetchDoneMoreResultsets)other); 
        super.mergeFrom(other);
        return this;
      }
      
      public Builder mergeFrom(MysqlxResultset.FetchDoneMoreResultsets other) {
        if (other == MysqlxResultset.FetchDoneMoreResultsets.getDefaultInstance())
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
    
    private static final FetchDoneMoreResultsets DEFAULT_INSTANCE = new FetchDoneMoreResultsets();
    
    public static FetchDoneMoreResultsets getDefaultInstance() {
      return DEFAULT_INSTANCE;
    }
    
    @Deprecated
    public static final Parser<FetchDoneMoreResultsets> PARSER = (Parser<FetchDoneMoreResultsets>)new AbstractParser<FetchDoneMoreResultsets>() {
        public MysqlxResultset.FetchDoneMoreResultsets parsePartialFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
          MysqlxResultset.FetchDoneMoreResultsets.Builder builder = MysqlxResultset.FetchDoneMoreResultsets.newBuilder();
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
    
    public static Parser<FetchDoneMoreResultsets> parser() {
      return PARSER;
    }
    
    public Parser<FetchDoneMoreResultsets> getParserForType() {
      return PARSER;
    }
    
    public FetchDoneMoreResultsets getDefaultInstanceForType() {
      return DEFAULT_INSTANCE;
    }
  }
  
  public static interface FetchDoneOrBuilder extends MessageOrBuilder {}
  
  public static final class FetchDone extends GeneratedMessageV3 implements FetchDoneOrBuilder {
    private static final long serialVersionUID = 0L;
    
    private byte memoizedIsInitialized;
    
    private FetchDone(GeneratedMessageV3.Builder<?> builder) {
      super(builder);
      this.memoizedIsInitialized = -1;
    }
    
    private FetchDone() {
      this.memoizedIsInitialized = -1;
    }
    
    protected Object newInstance(GeneratedMessageV3.UnusedPrivateParameter unused) {
      return new FetchDone();
    }
    
    public final UnknownFieldSet getUnknownFields() {
      return this.unknownFields;
    }
    
    public static final Descriptors.Descriptor getDescriptor() {
      return MysqlxResultset.internal_static_Mysqlx_Resultset_FetchDone_descriptor;
    }
    
    protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() {
      return MysqlxResultset.internal_static_Mysqlx_Resultset_FetchDone_fieldAccessorTable.ensureFieldAccessorsInitialized(FetchDone.class, Builder.class);
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
      if (!(obj instanceof FetchDone))
        return super.equals(obj); 
      FetchDone other = (FetchDone)obj;
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
    
    public static FetchDone parseFrom(ByteBuffer data) throws InvalidProtocolBufferException {
      return (FetchDone)PARSER.parseFrom(data);
    }
    
    public static FetchDone parseFrom(ByteBuffer data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
      return (FetchDone)PARSER.parseFrom(data, extensionRegistry);
    }
    
    public static FetchDone parseFrom(ByteString data) throws InvalidProtocolBufferException {
      return (FetchDone)PARSER.parseFrom(data);
    }
    
    public static FetchDone parseFrom(ByteString data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
      return (FetchDone)PARSER.parseFrom(data, extensionRegistry);
    }
    
    public static FetchDone parseFrom(byte[] data) throws InvalidProtocolBufferException {
      return (FetchDone)PARSER.parseFrom(data);
    }
    
    public static FetchDone parseFrom(byte[] data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
      return (FetchDone)PARSER.parseFrom(data, extensionRegistry);
    }
    
    public static FetchDone parseFrom(InputStream input) throws IOException {
      return 
        (FetchDone)GeneratedMessageV3.parseWithIOException(PARSER, input);
    }
    
    public static FetchDone parseFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
      return 
        (FetchDone)GeneratedMessageV3.parseWithIOException(PARSER, input, extensionRegistry);
    }
    
    public static FetchDone parseDelimitedFrom(InputStream input) throws IOException {
      return 
        (FetchDone)GeneratedMessageV3.parseDelimitedWithIOException(PARSER, input);
    }
    
    public static FetchDone parseDelimitedFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
      return 
        (FetchDone)GeneratedMessageV3.parseDelimitedWithIOException(PARSER, input, extensionRegistry);
    }
    
    public static FetchDone parseFrom(CodedInputStream input) throws IOException {
      return 
        (FetchDone)GeneratedMessageV3.parseWithIOException(PARSER, input);
    }
    
    public static FetchDone parseFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
      return 
        (FetchDone)GeneratedMessageV3.parseWithIOException(PARSER, input, extensionRegistry);
    }
    
    public Builder newBuilderForType() {
      return newBuilder();
    }
    
    public static Builder newBuilder() {
      return DEFAULT_INSTANCE.toBuilder();
    }
    
    public static Builder newBuilder(FetchDone prototype) {
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
    
    public static final class Builder extends GeneratedMessageV3.Builder<Builder> implements MysqlxResultset.FetchDoneOrBuilder {
      public static final Descriptors.Descriptor getDescriptor() {
        return MysqlxResultset.internal_static_Mysqlx_Resultset_FetchDone_descriptor;
      }
      
      protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() {
        return MysqlxResultset.internal_static_Mysqlx_Resultset_FetchDone_fieldAccessorTable
          .ensureFieldAccessorsInitialized(MysqlxResultset.FetchDone.class, Builder.class);
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
        return MysqlxResultset.internal_static_Mysqlx_Resultset_FetchDone_descriptor;
      }
      
      public MysqlxResultset.FetchDone getDefaultInstanceForType() {
        return MysqlxResultset.FetchDone.getDefaultInstance();
      }
      
      public MysqlxResultset.FetchDone build() {
        MysqlxResultset.FetchDone result = buildPartial();
        if (!result.isInitialized())
          throw newUninitializedMessageException(result); 
        return result;
      }
      
      public MysqlxResultset.FetchDone buildPartial() {
        MysqlxResultset.FetchDone result = new MysqlxResultset.FetchDone(this);
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
        if (other instanceof MysqlxResultset.FetchDone)
          return mergeFrom((MysqlxResultset.FetchDone)other); 
        super.mergeFrom(other);
        return this;
      }
      
      public Builder mergeFrom(MysqlxResultset.FetchDone other) {
        if (other == MysqlxResultset.FetchDone.getDefaultInstance())
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
    
    private static final FetchDone DEFAULT_INSTANCE = new FetchDone();
    
    public static FetchDone getDefaultInstance() {
      return DEFAULT_INSTANCE;
    }
    
    @Deprecated
    public static final Parser<FetchDone> PARSER = (Parser<FetchDone>)new AbstractParser<FetchDone>() {
        public MysqlxResultset.FetchDone parsePartialFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
          MysqlxResultset.FetchDone.Builder builder = MysqlxResultset.FetchDone.newBuilder();
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
    
    public static Parser<FetchDone> parser() {
      return PARSER;
    }
    
    public Parser<FetchDone> getParserForType() {
      return PARSER;
    }
    
    public FetchDone getDefaultInstanceForType() {
      return DEFAULT_INSTANCE;
    }
  }
  
  public static interface FetchSuspendedOrBuilder extends MessageOrBuilder {}
  
  public static final class FetchSuspended extends GeneratedMessageV3 implements FetchSuspendedOrBuilder {
    private static final long serialVersionUID = 0L;
    
    private byte memoizedIsInitialized;
    
    private FetchSuspended(GeneratedMessageV3.Builder<?> builder) {
      super(builder);
      this.memoizedIsInitialized = -1;
    }
    
    private FetchSuspended() {
      this.memoizedIsInitialized = -1;
    }
    
    protected Object newInstance(GeneratedMessageV3.UnusedPrivateParameter unused) {
      return new FetchSuspended();
    }
    
    public final UnknownFieldSet getUnknownFields() {
      return this.unknownFields;
    }
    
    public static final Descriptors.Descriptor getDescriptor() {
      return MysqlxResultset.internal_static_Mysqlx_Resultset_FetchSuspended_descriptor;
    }
    
    protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() {
      return MysqlxResultset.internal_static_Mysqlx_Resultset_FetchSuspended_fieldAccessorTable.ensureFieldAccessorsInitialized(FetchSuspended.class, Builder.class);
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
      if (!(obj instanceof FetchSuspended))
        return super.equals(obj); 
      FetchSuspended other = (FetchSuspended)obj;
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
    
    public static FetchSuspended parseFrom(ByteBuffer data) throws InvalidProtocolBufferException {
      return (FetchSuspended)PARSER.parseFrom(data);
    }
    
    public static FetchSuspended parseFrom(ByteBuffer data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
      return (FetchSuspended)PARSER.parseFrom(data, extensionRegistry);
    }
    
    public static FetchSuspended parseFrom(ByteString data) throws InvalidProtocolBufferException {
      return (FetchSuspended)PARSER.parseFrom(data);
    }
    
    public static FetchSuspended parseFrom(ByteString data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
      return (FetchSuspended)PARSER.parseFrom(data, extensionRegistry);
    }
    
    public static FetchSuspended parseFrom(byte[] data) throws InvalidProtocolBufferException {
      return (FetchSuspended)PARSER.parseFrom(data);
    }
    
    public static FetchSuspended parseFrom(byte[] data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
      return (FetchSuspended)PARSER.parseFrom(data, extensionRegistry);
    }
    
    public static FetchSuspended parseFrom(InputStream input) throws IOException {
      return 
        (FetchSuspended)GeneratedMessageV3.parseWithIOException(PARSER, input);
    }
    
    public static FetchSuspended parseFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
      return 
        (FetchSuspended)GeneratedMessageV3.parseWithIOException(PARSER, input, extensionRegistry);
    }
    
    public static FetchSuspended parseDelimitedFrom(InputStream input) throws IOException {
      return 
        (FetchSuspended)GeneratedMessageV3.parseDelimitedWithIOException(PARSER, input);
    }
    
    public static FetchSuspended parseDelimitedFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
      return 
        (FetchSuspended)GeneratedMessageV3.parseDelimitedWithIOException(PARSER, input, extensionRegistry);
    }
    
    public static FetchSuspended parseFrom(CodedInputStream input) throws IOException {
      return 
        (FetchSuspended)GeneratedMessageV3.parseWithIOException(PARSER, input);
    }
    
    public static FetchSuspended parseFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
      return 
        (FetchSuspended)GeneratedMessageV3.parseWithIOException(PARSER, input, extensionRegistry);
    }
    
    public Builder newBuilderForType() {
      return newBuilder();
    }
    
    public static Builder newBuilder() {
      return DEFAULT_INSTANCE.toBuilder();
    }
    
    public static Builder newBuilder(FetchSuspended prototype) {
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
    
    public static final class Builder extends GeneratedMessageV3.Builder<Builder> implements MysqlxResultset.FetchSuspendedOrBuilder {
      public static final Descriptors.Descriptor getDescriptor() {
        return MysqlxResultset.internal_static_Mysqlx_Resultset_FetchSuspended_descriptor;
      }
      
      protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() {
        return MysqlxResultset.internal_static_Mysqlx_Resultset_FetchSuspended_fieldAccessorTable
          .ensureFieldAccessorsInitialized(MysqlxResultset.FetchSuspended.class, Builder.class);
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
        return MysqlxResultset.internal_static_Mysqlx_Resultset_FetchSuspended_descriptor;
      }
      
      public MysqlxResultset.FetchSuspended getDefaultInstanceForType() {
        return MysqlxResultset.FetchSuspended.getDefaultInstance();
      }
      
      public MysqlxResultset.FetchSuspended build() {
        MysqlxResultset.FetchSuspended result = buildPartial();
        if (!result.isInitialized())
          throw newUninitializedMessageException(result); 
        return result;
      }
      
      public MysqlxResultset.FetchSuspended buildPartial() {
        MysqlxResultset.FetchSuspended result = new MysqlxResultset.FetchSuspended(this);
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
        if (other instanceof MysqlxResultset.FetchSuspended)
          return mergeFrom((MysqlxResultset.FetchSuspended)other); 
        super.mergeFrom(other);
        return this;
      }
      
      public Builder mergeFrom(MysqlxResultset.FetchSuspended other) {
        if (other == MysqlxResultset.FetchSuspended.getDefaultInstance())
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
    
    private static final FetchSuspended DEFAULT_INSTANCE = new FetchSuspended();
    
    public static FetchSuspended getDefaultInstance() {
      return DEFAULT_INSTANCE;
    }
    
    @Deprecated
    public static final Parser<FetchSuspended> PARSER = (Parser<FetchSuspended>)new AbstractParser<FetchSuspended>() {
        public MysqlxResultset.FetchSuspended parsePartialFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
          MysqlxResultset.FetchSuspended.Builder builder = MysqlxResultset.FetchSuspended.newBuilder();
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
    
    public static Parser<FetchSuspended> parser() {
      return PARSER;
    }
    
    public Parser<FetchSuspended> getParserForType() {
      return PARSER;
    }
    
    public FetchSuspended getDefaultInstanceForType() {
      return DEFAULT_INSTANCE;
    }
  }
  
  public static interface ColumnMetaDataOrBuilder extends MessageOrBuilder {
    boolean hasType();
    
    MysqlxResultset.ColumnMetaData.FieldType getType();
    
    boolean hasName();
    
    ByteString getName();
    
    boolean hasOriginalName();
    
    ByteString getOriginalName();
    
    boolean hasTable();
    
    ByteString getTable();
    
    boolean hasOriginalTable();
    
    ByteString getOriginalTable();
    
    boolean hasSchema();
    
    ByteString getSchema();
    
    boolean hasCatalog();
    
    ByteString getCatalog();
    
    boolean hasCollation();
    
    long getCollation();
    
    boolean hasFractionalDigits();
    
    int getFractionalDigits();
    
    boolean hasLength();
    
    int getLength();
    
    boolean hasFlags();
    
    int getFlags();
    
    boolean hasContentType();
    
    int getContentType();
  }
  
  public static final class ColumnMetaData extends GeneratedMessageV3 implements ColumnMetaDataOrBuilder {
    private static final long serialVersionUID = 0L;
    
    private int bitField0_;
    
    public static final int TYPE_FIELD_NUMBER = 1;
    
    private int type_;
    
    public static final int NAME_FIELD_NUMBER = 2;
    
    private ByteString name_;
    
    public static final int ORIGINAL_NAME_FIELD_NUMBER = 3;
    
    private ByteString originalName_;
    
    public static final int TABLE_FIELD_NUMBER = 4;
    
    private ByteString table_;
    
    public static final int ORIGINAL_TABLE_FIELD_NUMBER = 5;
    
    private ByteString originalTable_;
    
    public static final int SCHEMA_FIELD_NUMBER = 6;
    
    private ByteString schema_;
    
    public static final int CATALOG_FIELD_NUMBER = 7;
    
    private ByteString catalog_;
    
    public static final int COLLATION_FIELD_NUMBER = 8;
    
    private long collation_;
    
    public static final int FRACTIONAL_DIGITS_FIELD_NUMBER = 9;
    
    private int fractionalDigits_;
    
    public static final int LENGTH_FIELD_NUMBER = 10;
    
    private int length_;
    
    public static final int FLAGS_FIELD_NUMBER = 11;
    
    private int flags_;
    
    public static final int CONTENT_TYPE_FIELD_NUMBER = 12;
    
    private int contentType_;
    
    private byte memoizedIsInitialized;
    
    private ColumnMetaData(GeneratedMessageV3.Builder<?> builder) {
      super(builder);
      this.memoizedIsInitialized = -1;
    }
    
    private ColumnMetaData() {
      this.memoizedIsInitialized = -1;
      this.type_ = 1;
      this.name_ = ByteString.EMPTY;
      this.originalName_ = ByteString.EMPTY;
      this.table_ = ByteString.EMPTY;
      this.originalTable_ = ByteString.EMPTY;
      this.schema_ = ByteString.EMPTY;
      this.catalog_ = ByteString.EMPTY;
    }
    
    protected Object newInstance(GeneratedMessageV3.UnusedPrivateParameter unused) {
      return new ColumnMetaData();
    }
    
    public final UnknownFieldSet getUnknownFields() {
      return this.unknownFields;
    }
    
    public static final Descriptors.Descriptor getDescriptor() {
      return MysqlxResultset.internal_static_Mysqlx_Resultset_ColumnMetaData_descriptor;
    }
    
    protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() {
      return MysqlxResultset.internal_static_Mysqlx_Resultset_ColumnMetaData_fieldAccessorTable.ensureFieldAccessorsInitialized(ColumnMetaData.class, Builder.class);
    }
    
    public enum FieldType implements ProtocolMessageEnum {
      SINT(1),
      UINT(2),
      DOUBLE(5),
      FLOAT(6),
      BYTES(7),
      TIME(10),
      DATETIME(12),
      SET(15),
      ENUM(16),
      BIT(17),
      DECIMAL(18);
      
      public static final int SINT_VALUE = 1;
      
      public static final int UINT_VALUE = 2;
      
      public static final int DOUBLE_VALUE = 5;
      
      public static final int FLOAT_VALUE = 6;
      
      public static final int BYTES_VALUE = 7;
      
      public static final int TIME_VALUE = 10;
      
      public static final int DATETIME_VALUE = 12;
      
      public static final int SET_VALUE = 15;
      
      public static final int ENUM_VALUE = 16;
      
      public static final int BIT_VALUE = 17;
      
      public static final int DECIMAL_VALUE = 18;
      
      private static final Internal.EnumLiteMap<FieldType> internalValueMap = new Internal.EnumLiteMap<FieldType>() {
          public MysqlxResultset.ColumnMetaData.FieldType findValueByNumber(int number) {
            return MysqlxResultset.ColumnMetaData.FieldType.forNumber(number);
          }
        };
      
      private static final FieldType[] VALUES = values();
      
      private final int value;
      
      public final int getNumber() {
        return this.value;
      }
      
      public static FieldType forNumber(int value) {
        switch (value) {
          case 1:
            return SINT;
          case 2:
            return UINT;
          case 5:
            return DOUBLE;
          case 6:
            return FLOAT;
          case 7:
            return BYTES;
          case 10:
            return TIME;
          case 12:
            return DATETIME;
          case 15:
            return SET;
          case 16:
            return ENUM;
          case 17:
            return BIT;
          case 18:
            return DECIMAL;
        } 
        return null;
      }
      
      public static Internal.EnumLiteMap<FieldType> internalGetValueMap() {
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
        return MysqlxResultset.ColumnMetaData.getDescriptor().getEnumTypes().get(0);
      }
      
      FieldType(int value) {
        this.value = value;
      }
    }
    
    public boolean hasType() {
      return ((this.bitField0_ & 0x1) != 0);
    }
    
    public FieldType getType() {
      FieldType result = FieldType.valueOf(this.type_);
      return (result == null) ? FieldType.SINT : result;
    }
    
    public boolean hasName() {
      return ((this.bitField0_ & 0x2) != 0);
    }
    
    public ByteString getName() {
      return this.name_;
    }
    
    public boolean hasOriginalName() {
      return ((this.bitField0_ & 0x4) != 0);
    }
    
    public ByteString getOriginalName() {
      return this.originalName_;
    }
    
    public boolean hasTable() {
      return ((this.bitField0_ & 0x8) != 0);
    }
    
    public ByteString getTable() {
      return this.table_;
    }
    
    public boolean hasOriginalTable() {
      return ((this.bitField0_ & 0x10) != 0);
    }
    
    public ByteString getOriginalTable() {
      return this.originalTable_;
    }
    
    public boolean hasSchema() {
      return ((this.bitField0_ & 0x20) != 0);
    }
    
    public ByteString getSchema() {
      return this.schema_;
    }
    
    public boolean hasCatalog() {
      return ((this.bitField0_ & 0x40) != 0);
    }
    
    public ByteString getCatalog() {
      return this.catalog_;
    }
    
    public boolean hasCollation() {
      return ((this.bitField0_ & 0x80) != 0);
    }
    
    public long getCollation() {
      return this.collation_;
    }
    
    public boolean hasFractionalDigits() {
      return ((this.bitField0_ & 0x100) != 0);
    }
    
    public int getFractionalDigits() {
      return this.fractionalDigits_;
    }
    
    public boolean hasLength() {
      return ((this.bitField0_ & 0x200) != 0);
    }
    
    public int getLength() {
      return this.length_;
    }
    
    public boolean hasFlags() {
      return ((this.bitField0_ & 0x400) != 0);
    }
    
    public int getFlags() {
      return this.flags_;
    }
    
    public boolean hasContentType() {
      return ((this.bitField0_ & 0x800) != 0);
    }
    
    public int getContentType() {
      return this.contentType_;
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
        output.writeEnum(1, this.type_); 
      if ((this.bitField0_ & 0x2) != 0)
        output.writeBytes(2, this.name_); 
      if ((this.bitField0_ & 0x4) != 0)
        output.writeBytes(3, this.originalName_); 
      if ((this.bitField0_ & 0x8) != 0)
        output.writeBytes(4, this.table_); 
      if ((this.bitField0_ & 0x10) != 0)
        output.writeBytes(5, this.originalTable_); 
      if ((this.bitField0_ & 0x20) != 0)
        output.writeBytes(6, this.schema_); 
      if ((this.bitField0_ & 0x40) != 0)
        output.writeBytes(7, this.catalog_); 
      if ((this.bitField0_ & 0x80) != 0)
        output.writeUInt64(8, this.collation_); 
      if ((this.bitField0_ & 0x100) != 0)
        output.writeUInt32(9, this.fractionalDigits_); 
      if ((this.bitField0_ & 0x200) != 0)
        output.writeUInt32(10, this.length_); 
      if ((this.bitField0_ & 0x400) != 0)
        output.writeUInt32(11, this.flags_); 
      if ((this.bitField0_ & 0x800) != 0)
        output.writeUInt32(12, this.contentType_); 
      getUnknownFields().writeTo(output);
    }
    
    public int getSerializedSize() {
      int size = this.memoizedSize;
      if (size != -1)
        return size; 
      size = 0;
      if ((this.bitField0_ & 0x1) != 0)
        size += 
          CodedOutputStream.computeEnumSize(1, this.type_); 
      if ((this.bitField0_ & 0x2) != 0)
        size += 
          CodedOutputStream.computeBytesSize(2, this.name_); 
      if ((this.bitField0_ & 0x4) != 0)
        size += 
          CodedOutputStream.computeBytesSize(3, this.originalName_); 
      if ((this.bitField0_ & 0x8) != 0)
        size += 
          CodedOutputStream.computeBytesSize(4, this.table_); 
      if ((this.bitField0_ & 0x10) != 0)
        size += 
          CodedOutputStream.computeBytesSize(5, this.originalTable_); 
      if ((this.bitField0_ & 0x20) != 0)
        size += 
          CodedOutputStream.computeBytesSize(6, this.schema_); 
      if ((this.bitField0_ & 0x40) != 0)
        size += 
          CodedOutputStream.computeBytesSize(7, this.catalog_); 
      if ((this.bitField0_ & 0x80) != 0)
        size += 
          CodedOutputStream.computeUInt64Size(8, this.collation_); 
      if ((this.bitField0_ & 0x100) != 0)
        size += 
          CodedOutputStream.computeUInt32Size(9, this.fractionalDigits_); 
      if ((this.bitField0_ & 0x200) != 0)
        size += 
          CodedOutputStream.computeUInt32Size(10, this.length_); 
      if ((this.bitField0_ & 0x400) != 0)
        size += 
          CodedOutputStream.computeUInt32Size(11, this.flags_); 
      if ((this.bitField0_ & 0x800) != 0)
        size += 
          CodedOutputStream.computeUInt32Size(12, this.contentType_); 
      size += getUnknownFields().getSerializedSize();
      this.memoizedSize = size;
      return size;
    }
    
    public boolean equals(Object obj) {
      if (obj == this)
        return true; 
      if (!(obj instanceof ColumnMetaData))
        return super.equals(obj); 
      ColumnMetaData other = (ColumnMetaData)obj;
      if (hasType() != other.hasType())
        return false; 
      if (hasType() && 
        this.type_ != other.type_)
        return false; 
      if (hasName() != other.hasName())
        return false; 
      if (hasName() && 
        
        !getName().equals(other.getName()))
        return false; 
      if (hasOriginalName() != other.hasOriginalName())
        return false; 
      if (hasOriginalName() && 
        
        !getOriginalName().equals(other.getOriginalName()))
        return false; 
      if (hasTable() != other.hasTable())
        return false; 
      if (hasTable() && 
        
        !getTable().equals(other.getTable()))
        return false; 
      if (hasOriginalTable() != other.hasOriginalTable())
        return false; 
      if (hasOriginalTable() && 
        
        !getOriginalTable().equals(other.getOriginalTable()))
        return false; 
      if (hasSchema() != other.hasSchema())
        return false; 
      if (hasSchema() && 
        
        !getSchema().equals(other.getSchema()))
        return false; 
      if (hasCatalog() != other.hasCatalog())
        return false; 
      if (hasCatalog() && 
        
        !getCatalog().equals(other.getCatalog()))
        return false; 
      if (hasCollation() != other.hasCollation())
        return false; 
      if (hasCollation() && 
        getCollation() != other
        .getCollation())
        return false; 
      if (hasFractionalDigits() != other.hasFractionalDigits())
        return false; 
      if (hasFractionalDigits() && 
        getFractionalDigits() != other
        .getFractionalDigits())
        return false; 
      if (hasLength() != other.hasLength())
        return false; 
      if (hasLength() && 
        getLength() != other
        .getLength())
        return false; 
      if (hasFlags() != other.hasFlags())
        return false; 
      if (hasFlags() && 
        getFlags() != other
        .getFlags())
        return false; 
      if (hasContentType() != other.hasContentType())
        return false; 
      if (hasContentType() && 
        getContentType() != other
        .getContentType())
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
        hash = 53 * hash + this.type_;
      } 
      if (hasName()) {
        hash = 37 * hash + 2;
        hash = 53 * hash + getName().hashCode();
      } 
      if (hasOriginalName()) {
        hash = 37 * hash + 3;
        hash = 53 * hash + getOriginalName().hashCode();
      } 
      if (hasTable()) {
        hash = 37 * hash + 4;
        hash = 53 * hash + getTable().hashCode();
      } 
      if (hasOriginalTable()) {
        hash = 37 * hash + 5;
        hash = 53 * hash + getOriginalTable().hashCode();
      } 
      if (hasSchema()) {
        hash = 37 * hash + 6;
        hash = 53 * hash + getSchema().hashCode();
      } 
      if (hasCatalog()) {
        hash = 37 * hash + 7;
        hash = 53 * hash + getCatalog().hashCode();
      } 
      if (hasCollation()) {
        hash = 37 * hash + 8;
        hash = 53 * hash + Internal.hashLong(
            getCollation());
      } 
      if (hasFractionalDigits()) {
        hash = 37 * hash + 9;
        hash = 53 * hash + getFractionalDigits();
      } 
      if (hasLength()) {
        hash = 37 * hash + 10;
        hash = 53 * hash + getLength();
      } 
      if (hasFlags()) {
        hash = 37 * hash + 11;
        hash = 53 * hash + getFlags();
      } 
      if (hasContentType()) {
        hash = 37 * hash + 12;
        hash = 53 * hash + getContentType();
      } 
      hash = 29 * hash + getUnknownFields().hashCode();
      this.memoizedHashCode = hash;
      return hash;
    }
    
    public static ColumnMetaData parseFrom(ByteBuffer data) throws InvalidProtocolBufferException {
      return (ColumnMetaData)PARSER.parseFrom(data);
    }
    
    public static ColumnMetaData parseFrom(ByteBuffer data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
      return (ColumnMetaData)PARSER.parseFrom(data, extensionRegistry);
    }
    
    public static ColumnMetaData parseFrom(ByteString data) throws InvalidProtocolBufferException {
      return (ColumnMetaData)PARSER.parseFrom(data);
    }
    
    public static ColumnMetaData parseFrom(ByteString data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
      return (ColumnMetaData)PARSER.parseFrom(data, extensionRegistry);
    }
    
    public static ColumnMetaData parseFrom(byte[] data) throws InvalidProtocolBufferException {
      return (ColumnMetaData)PARSER.parseFrom(data);
    }
    
    public static ColumnMetaData parseFrom(byte[] data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
      return (ColumnMetaData)PARSER.parseFrom(data, extensionRegistry);
    }
    
    public static ColumnMetaData parseFrom(InputStream input) throws IOException {
      return 
        (ColumnMetaData)GeneratedMessageV3.parseWithIOException(PARSER, input);
    }
    
    public static ColumnMetaData parseFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
      return 
        (ColumnMetaData)GeneratedMessageV3.parseWithIOException(PARSER, input, extensionRegistry);
    }
    
    public static ColumnMetaData parseDelimitedFrom(InputStream input) throws IOException {
      return 
        (ColumnMetaData)GeneratedMessageV3.parseDelimitedWithIOException(PARSER, input);
    }
    
    public static ColumnMetaData parseDelimitedFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
      return 
        (ColumnMetaData)GeneratedMessageV3.parseDelimitedWithIOException(PARSER, input, extensionRegistry);
    }
    
    public static ColumnMetaData parseFrom(CodedInputStream input) throws IOException {
      return 
        (ColumnMetaData)GeneratedMessageV3.parseWithIOException(PARSER, input);
    }
    
    public static ColumnMetaData parseFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
      return 
        (ColumnMetaData)GeneratedMessageV3.parseWithIOException(PARSER, input, extensionRegistry);
    }
    
    public Builder newBuilderForType() {
      return newBuilder();
    }
    
    public static Builder newBuilder() {
      return DEFAULT_INSTANCE.toBuilder();
    }
    
    public static Builder newBuilder(ColumnMetaData prototype) {
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
    
    public static final class Builder extends GeneratedMessageV3.Builder<Builder> implements MysqlxResultset.ColumnMetaDataOrBuilder {
      private int bitField0_;
      
      private int type_;
      
      private ByteString name_;
      
      private ByteString originalName_;
      
      private ByteString table_;
      
      private ByteString originalTable_;
      
      private ByteString schema_;
      
      private ByteString catalog_;
      
      private long collation_;
      
      private int fractionalDigits_;
      
      private int length_;
      
      private int flags_;
      
      private int contentType_;
      
      public static final Descriptors.Descriptor getDescriptor() {
        return MysqlxResultset.internal_static_Mysqlx_Resultset_ColumnMetaData_descriptor;
      }
      
      protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() {
        return MysqlxResultset.internal_static_Mysqlx_Resultset_ColumnMetaData_fieldAccessorTable
          .ensureFieldAccessorsInitialized(MysqlxResultset.ColumnMetaData.class, Builder.class);
      }
      
      private Builder() {
        this.type_ = 1;
        this.name_ = ByteString.EMPTY;
        this.originalName_ = ByteString.EMPTY;
        this.table_ = ByteString.EMPTY;
        this.originalTable_ = ByteString.EMPTY;
        this.schema_ = ByteString.EMPTY;
        this.catalog_ = ByteString.EMPTY;
      }
      
      private Builder(GeneratedMessageV3.BuilderParent parent) {
        super(parent);
        this.type_ = 1;
        this.name_ = ByteString.EMPTY;
        this.originalName_ = ByteString.EMPTY;
        this.table_ = ByteString.EMPTY;
        this.originalTable_ = ByteString.EMPTY;
        this.schema_ = ByteString.EMPTY;
        this.catalog_ = ByteString.EMPTY;
      }
      
      public Builder clear() {
        super.clear();
        this.type_ = 1;
        this.bitField0_ &= 0xFFFFFFFE;
        this.name_ = ByteString.EMPTY;
        this.bitField0_ &= 0xFFFFFFFD;
        this.originalName_ = ByteString.EMPTY;
        this.bitField0_ &= 0xFFFFFFFB;
        this.table_ = ByteString.EMPTY;
        this.bitField0_ &= 0xFFFFFFF7;
        this.originalTable_ = ByteString.EMPTY;
        this.bitField0_ &= 0xFFFFFFEF;
        this.schema_ = ByteString.EMPTY;
        this.bitField0_ &= 0xFFFFFFDF;
        this.catalog_ = ByteString.EMPTY;
        this.bitField0_ &= 0xFFFFFFBF;
        this.collation_ = 0L;
        this.bitField0_ &= 0xFFFFFF7F;
        this.fractionalDigits_ = 0;
        this.bitField0_ &= 0xFFFFFEFF;
        this.length_ = 0;
        this.bitField0_ &= 0xFFFFFDFF;
        this.flags_ = 0;
        this.bitField0_ &= 0xFFFFFBFF;
        this.contentType_ = 0;
        this.bitField0_ &= 0xFFFFF7FF;
        return this;
      }
      
      public Descriptors.Descriptor getDescriptorForType() {
        return MysqlxResultset.internal_static_Mysqlx_Resultset_ColumnMetaData_descriptor;
      }
      
      public MysqlxResultset.ColumnMetaData getDefaultInstanceForType() {
        return MysqlxResultset.ColumnMetaData.getDefaultInstance();
      }
      
      public MysqlxResultset.ColumnMetaData build() {
        MysqlxResultset.ColumnMetaData result = buildPartial();
        if (!result.isInitialized())
          throw newUninitializedMessageException(result); 
        return result;
      }
      
      public MysqlxResultset.ColumnMetaData buildPartial() {
        MysqlxResultset.ColumnMetaData result = new MysqlxResultset.ColumnMetaData(this);
        int from_bitField0_ = this.bitField0_;
        int to_bitField0_ = 0;
        if ((from_bitField0_ & 0x1) != 0)
          to_bitField0_ |= 0x1; 
        result.type_ = this.type_;
        if ((from_bitField0_ & 0x2) != 0)
          to_bitField0_ |= 0x2; 
        result.name_ = this.name_;
        if ((from_bitField0_ & 0x4) != 0)
          to_bitField0_ |= 0x4; 
        result.originalName_ = this.originalName_;
        if ((from_bitField0_ & 0x8) != 0)
          to_bitField0_ |= 0x8; 
        result.table_ = this.table_;
        if ((from_bitField0_ & 0x10) != 0)
          to_bitField0_ |= 0x10; 
        result.originalTable_ = this.originalTable_;
        if ((from_bitField0_ & 0x20) != 0)
          to_bitField0_ |= 0x20; 
        result.schema_ = this.schema_;
        if ((from_bitField0_ & 0x40) != 0)
          to_bitField0_ |= 0x40; 
        result.catalog_ = this.catalog_;
        if ((from_bitField0_ & 0x80) != 0) {
          result.collation_ = this.collation_;
          to_bitField0_ |= 0x80;
        } 
        if ((from_bitField0_ & 0x100) != 0) {
          result.fractionalDigits_ = this.fractionalDigits_;
          to_bitField0_ |= 0x100;
        } 
        if ((from_bitField0_ & 0x200) != 0) {
          result.length_ = this.length_;
          to_bitField0_ |= 0x200;
        } 
        if ((from_bitField0_ & 0x400) != 0) {
          result.flags_ = this.flags_;
          to_bitField0_ |= 0x400;
        } 
        if ((from_bitField0_ & 0x800) != 0) {
          result.contentType_ = this.contentType_;
          to_bitField0_ |= 0x800;
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
        if (other instanceof MysqlxResultset.ColumnMetaData)
          return mergeFrom((MysqlxResultset.ColumnMetaData)other); 
        super.mergeFrom(other);
        return this;
      }
      
      public Builder mergeFrom(MysqlxResultset.ColumnMetaData other) {
        if (other == MysqlxResultset.ColumnMetaData.getDefaultInstance())
          return this; 
        if (other.hasType())
          setType(other.getType()); 
        if (other.hasName())
          setName(other.getName()); 
        if (other.hasOriginalName())
          setOriginalName(other.getOriginalName()); 
        if (other.hasTable())
          setTable(other.getTable()); 
        if (other.hasOriginalTable())
          setOriginalTable(other.getOriginalTable()); 
        if (other.hasSchema())
          setSchema(other.getSchema()); 
        if (other.hasCatalog())
          setCatalog(other.getCatalog()); 
        if (other.hasCollation())
          setCollation(other.getCollation()); 
        if (other.hasFractionalDigits())
          setFractionalDigits(other.getFractionalDigits()); 
        if (other.hasLength())
          setLength(other.getLength()); 
        if (other.hasFlags())
          setFlags(other.getFlags()); 
        if (other.hasContentType())
          setContentType(other.getContentType()); 
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
            MysqlxResultset.ColumnMetaData.FieldType tmpValue;
            int tag = input.readTag();
            switch (tag) {
              case 0:
                done = true;
                continue;
              case 8:
                tmpRaw = input.readEnum();
                tmpValue = MysqlxResultset.ColumnMetaData.FieldType.forNumber(tmpRaw);
                if (tmpValue == null) {
                  mergeUnknownVarintField(1, tmpRaw);
                  continue;
                } 
                this.type_ = tmpRaw;
                this.bitField0_ |= 0x1;
                continue;
              case 18:
                this.name_ = input.readBytes();
                this.bitField0_ |= 0x2;
                continue;
              case 26:
                this.originalName_ = input.readBytes();
                this.bitField0_ |= 0x4;
                continue;
              case 34:
                this.table_ = input.readBytes();
                this.bitField0_ |= 0x8;
                continue;
              case 42:
                this.originalTable_ = input.readBytes();
                this.bitField0_ |= 0x10;
                continue;
              case 50:
                this.schema_ = input.readBytes();
                this.bitField0_ |= 0x20;
                continue;
              case 58:
                this.catalog_ = input.readBytes();
                this.bitField0_ |= 0x40;
                continue;
              case 64:
                this.collation_ = input.readUInt64();
                this.bitField0_ |= 0x80;
                continue;
              case 72:
                this.fractionalDigits_ = input.readUInt32();
                this.bitField0_ |= 0x100;
                continue;
              case 80:
                this.length_ = input.readUInt32();
                this.bitField0_ |= 0x200;
                continue;
              case 88:
                this.flags_ = input.readUInt32();
                this.bitField0_ |= 0x400;
                continue;
              case 96:
                this.contentType_ = input.readUInt32();
                this.bitField0_ |= 0x800;
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
      
      public MysqlxResultset.ColumnMetaData.FieldType getType() {
        MysqlxResultset.ColumnMetaData.FieldType result = MysqlxResultset.ColumnMetaData.FieldType.valueOf(this.type_);
        return (result == null) ? MysqlxResultset.ColumnMetaData.FieldType.SINT : result;
      }
      
      public Builder setType(MysqlxResultset.ColumnMetaData.FieldType value) {
        if (value == null)
          throw new NullPointerException(); 
        this.bitField0_ |= 0x1;
        this.type_ = value.getNumber();
        onChanged();
        return this;
      }
      
      public Builder clearType() {
        this.bitField0_ &= 0xFFFFFFFE;
        this.type_ = 1;
        onChanged();
        return this;
      }
      
      public boolean hasName() {
        return ((this.bitField0_ & 0x2) != 0);
      }
      
      public ByteString getName() {
        return this.name_;
      }
      
      public Builder setName(ByteString value) {
        if (value == null)
          throw new NullPointerException(); 
        this.bitField0_ |= 0x2;
        this.name_ = value;
        onChanged();
        return this;
      }
      
      public Builder clearName() {
        this.bitField0_ &= 0xFFFFFFFD;
        this.name_ = MysqlxResultset.ColumnMetaData.getDefaultInstance().getName();
        onChanged();
        return this;
      }
      
      public boolean hasOriginalName() {
        return ((this.bitField0_ & 0x4) != 0);
      }
      
      public ByteString getOriginalName() {
        return this.originalName_;
      }
      
      public Builder setOriginalName(ByteString value) {
        if (value == null)
          throw new NullPointerException(); 
        this.bitField0_ |= 0x4;
        this.originalName_ = value;
        onChanged();
        return this;
      }
      
      public Builder clearOriginalName() {
        this.bitField0_ &= 0xFFFFFFFB;
        this.originalName_ = MysqlxResultset.ColumnMetaData.getDefaultInstance().getOriginalName();
        onChanged();
        return this;
      }
      
      public boolean hasTable() {
        return ((this.bitField0_ & 0x8) != 0);
      }
      
      public ByteString getTable() {
        return this.table_;
      }
      
      public Builder setTable(ByteString value) {
        if (value == null)
          throw new NullPointerException(); 
        this.bitField0_ |= 0x8;
        this.table_ = value;
        onChanged();
        return this;
      }
      
      public Builder clearTable() {
        this.bitField0_ &= 0xFFFFFFF7;
        this.table_ = MysqlxResultset.ColumnMetaData.getDefaultInstance().getTable();
        onChanged();
        return this;
      }
      
      public boolean hasOriginalTable() {
        return ((this.bitField0_ & 0x10) != 0);
      }
      
      public ByteString getOriginalTable() {
        return this.originalTable_;
      }
      
      public Builder setOriginalTable(ByteString value) {
        if (value == null)
          throw new NullPointerException(); 
        this.bitField0_ |= 0x10;
        this.originalTable_ = value;
        onChanged();
        return this;
      }
      
      public Builder clearOriginalTable() {
        this.bitField0_ &= 0xFFFFFFEF;
        this.originalTable_ = MysqlxResultset.ColumnMetaData.getDefaultInstance().getOriginalTable();
        onChanged();
        return this;
      }
      
      public boolean hasSchema() {
        return ((this.bitField0_ & 0x20) != 0);
      }
      
      public ByteString getSchema() {
        return this.schema_;
      }
      
      public Builder setSchema(ByteString value) {
        if (value == null)
          throw new NullPointerException(); 
        this.bitField0_ |= 0x20;
        this.schema_ = value;
        onChanged();
        return this;
      }
      
      public Builder clearSchema() {
        this.bitField0_ &= 0xFFFFFFDF;
        this.schema_ = MysqlxResultset.ColumnMetaData.getDefaultInstance().getSchema();
        onChanged();
        return this;
      }
      
      public boolean hasCatalog() {
        return ((this.bitField0_ & 0x40) != 0);
      }
      
      public ByteString getCatalog() {
        return this.catalog_;
      }
      
      public Builder setCatalog(ByteString value) {
        if (value == null)
          throw new NullPointerException(); 
        this.bitField0_ |= 0x40;
        this.catalog_ = value;
        onChanged();
        return this;
      }
      
      public Builder clearCatalog() {
        this.bitField0_ &= 0xFFFFFFBF;
        this.catalog_ = MysqlxResultset.ColumnMetaData.getDefaultInstance().getCatalog();
        onChanged();
        return this;
      }
      
      public boolean hasCollation() {
        return ((this.bitField0_ & 0x80) != 0);
      }
      
      public long getCollation() {
        return this.collation_;
      }
      
      public Builder setCollation(long value) {
        this.bitField0_ |= 0x80;
        this.collation_ = value;
        onChanged();
        return this;
      }
      
      public Builder clearCollation() {
        this.bitField0_ &= 0xFFFFFF7F;
        this.collation_ = 0L;
        onChanged();
        return this;
      }
      
      public boolean hasFractionalDigits() {
        return ((this.bitField0_ & 0x100) != 0);
      }
      
      public int getFractionalDigits() {
        return this.fractionalDigits_;
      }
      
      public Builder setFractionalDigits(int value) {
        this.bitField0_ |= 0x100;
        this.fractionalDigits_ = value;
        onChanged();
        return this;
      }
      
      public Builder clearFractionalDigits() {
        this.bitField0_ &= 0xFFFFFEFF;
        this.fractionalDigits_ = 0;
        onChanged();
        return this;
      }
      
      public boolean hasLength() {
        return ((this.bitField0_ & 0x200) != 0);
      }
      
      public int getLength() {
        return this.length_;
      }
      
      public Builder setLength(int value) {
        this.bitField0_ |= 0x200;
        this.length_ = value;
        onChanged();
        return this;
      }
      
      public Builder clearLength() {
        this.bitField0_ &= 0xFFFFFDFF;
        this.length_ = 0;
        onChanged();
        return this;
      }
      
      public boolean hasFlags() {
        return ((this.bitField0_ & 0x400) != 0);
      }
      
      public int getFlags() {
        return this.flags_;
      }
      
      public Builder setFlags(int value) {
        this.bitField0_ |= 0x400;
        this.flags_ = value;
        onChanged();
        return this;
      }
      
      public Builder clearFlags() {
        this.bitField0_ &= 0xFFFFFBFF;
        this.flags_ = 0;
        onChanged();
        return this;
      }
      
      public boolean hasContentType() {
        return ((this.bitField0_ & 0x800) != 0);
      }
      
      public int getContentType() {
        return this.contentType_;
      }
      
      public Builder setContentType(int value) {
        this.bitField0_ |= 0x800;
        this.contentType_ = value;
        onChanged();
        return this;
      }
      
      public Builder clearContentType() {
        this.bitField0_ &= 0xFFFFF7FF;
        this.contentType_ = 0;
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
    
    private static final ColumnMetaData DEFAULT_INSTANCE = new ColumnMetaData();
    
    public static ColumnMetaData getDefaultInstance() {
      return DEFAULT_INSTANCE;
    }
    
    @Deprecated
    public static final Parser<ColumnMetaData> PARSER = (Parser<ColumnMetaData>)new AbstractParser<ColumnMetaData>() {
        public MysqlxResultset.ColumnMetaData parsePartialFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
          MysqlxResultset.ColumnMetaData.Builder builder = MysqlxResultset.ColumnMetaData.newBuilder();
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
    
    public static Parser<ColumnMetaData> parser() {
      return PARSER;
    }
    
    public Parser<ColumnMetaData> getParserForType() {
      return PARSER;
    }
    
    public ColumnMetaData getDefaultInstanceForType() {
      return DEFAULT_INSTANCE;
    }
  }
  
  public static interface RowOrBuilder extends MessageOrBuilder {
    List<ByteString> getFieldList();
    
    int getFieldCount();
    
    ByteString getField(int param1Int);
  }
  
  public static final class Row extends GeneratedMessageV3 implements RowOrBuilder {
    private static final long serialVersionUID = 0L;
    
    public static final int FIELD_FIELD_NUMBER = 1;
    
    private List<ByteString> field_;
    
    private byte memoizedIsInitialized;
    
    private Row(GeneratedMessageV3.Builder<?> builder) {
      super(builder);
      this.memoizedIsInitialized = -1;
    }
    
    private Row() {
      this.memoizedIsInitialized = -1;
      this.field_ = Collections.emptyList();
    }
    
    protected Object newInstance(GeneratedMessageV3.UnusedPrivateParameter unused) {
      return new Row();
    }
    
    public final UnknownFieldSet getUnknownFields() {
      return this.unknownFields;
    }
    
    public static final Descriptors.Descriptor getDescriptor() {
      return MysqlxResultset.internal_static_Mysqlx_Resultset_Row_descriptor;
    }
    
    protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() {
      return MysqlxResultset.internal_static_Mysqlx_Resultset_Row_fieldAccessorTable.ensureFieldAccessorsInitialized(Row.class, Builder.class);
    }
    
    public List<ByteString> getFieldList() {
      return this.field_;
    }
    
    public int getFieldCount() {
      return this.field_.size();
    }
    
    public ByteString getField(int index) {
      return this.field_.get(index);
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
      for (int i = 0; i < this.field_.size(); i++)
        output.writeBytes(1, this.field_.get(i)); 
      getUnknownFields().writeTo(output);
    }
    
    public int getSerializedSize() {
      int size = this.memoizedSize;
      if (size != -1)
        return size; 
      size = 0;
      int dataSize = 0;
      for (int i = 0; i < this.field_.size(); i++)
        dataSize += 
          CodedOutputStream.computeBytesSizeNoTag(this.field_.get(i)); 
      size += dataSize;
      size += 1 * getFieldList().size();
      size += getUnknownFields().getSerializedSize();
      this.memoizedSize = size;
      return size;
    }
    
    public boolean equals(Object obj) {
      if (obj == this)
        return true; 
      if (!(obj instanceof Row))
        return super.equals(obj); 
      Row other = (Row)obj;
      if (!getFieldList().equals(other.getFieldList()))
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
      if (getFieldCount() > 0) {
        hash = 37 * hash + 1;
        hash = 53 * hash + getFieldList().hashCode();
      } 
      hash = 29 * hash + getUnknownFields().hashCode();
      this.memoizedHashCode = hash;
      return hash;
    }
    
    public static Row parseFrom(ByteBuffer data) throws InvalidProtocolBufferException {
      return (Row)PARSER.parseFrom(data);
    }
    
    public static Row parseFrom(ByteBuffer data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
      return (Row)PARSER.parseFrom(data, extensionRegistry);
    }
    
    public static Row parseFrom(ByteString data) throws InvalidProtocolBufferException {
      return (Row)PARSER.parseFrom(data);
    }
    
    public static Row parseFrom(ByteString data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
      return (Row)PARSER.parseFrom(data, extensionRegistry);
    }
    
    public static Row parseFrom(byte[] data) throws InvalidProtocolBufferException {
      return (Row)PARSER.parseFrom(data);
    }
    
    public static Row parseFrom(byte[] data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
      return (Row)PARSER.parseFrom(data, extensionRegistry);
    }
    
    public static Row parseFrom(InputStream input) throws IOException {
      return 
        (Row)GeneratedMessageV3.parseWithIOException(PARSER, input);
    }
    
    public static Row parseFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
      return 
        (Row)GeneratedMessageV3.parseWithIOException(PARSER, input, extensionRegistry);
    }
    
    public static Row parseDelimitedFrom(InputStream input) throws IOException {
      return 
        (Row)GeneratedMessageV3.parseDelimitedWithIOException(PARSER, input);
    }
    
    public static Row parseDelimitedFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
      return 
        (Row)GeneratedMessageV3.parseDelimitedWithIOException(PARSER, input, extensionRegistry);
    }
    
    public static Row parseFrom(CodedInputStream input) throws IOException {
      return 
        (Row)GeneratedMessageV3.parseWithIOException(PARSER, input);
    }
    
    public static Row parseFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
      return 
        (Row)GeneratedMessageV3.parseWithIOException(PARSER, input, extensionRegistry);
    }
    
    public Builder newBuilderForType() {
      return newBuilder();
    }
    
    public static Builder newBuilder() {
      return DEFAULT_INSTANCE.toBuilder();
    }
    
    public static Builder newBuilder(Row prototype) {
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
    
    public static final class Builder extends GeneratedMessageV3.Builder<Builder> implements MysqlxResultset.RowOrBuilder {
      private int bitField0_;
      
      private List<ByteString> field_;
      
      public static final Descriptors.Descriptor getDescriptor() {
        return MysqlxResultset.internal_static_Mysqlx_Resultset_Row_descriptor;
      }
      
      protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() {
        return MysqlxResultset.internal_static_Mysqlx_Resultset_Row_fieldAccessorTable
          .ensureFieldAccessorsInitialized(MysqlxResultset.Row.class, Builder.class);
      }
      
      private Builder() {
        this.field_ = Collections.emptyList();
      }
      
      private Builder(GeneratedMessageV3.BuilderParent parent) {
        super(parent);
        this.field_ = Collections.emptyList();
      }
      
      public Builder clear() {
        super.clear();
        this.field_ = Collections.emptyList();
        this.bitField0_ &= 0xFFFFFFFE;
        return this;
      }
      
      public Descriptors.Descriptor getDescriptorForType() {
        return MysqlxResultset.internal_static_Mysqlx_Resultset_Row_descriptor;
      }
      
      public MysqlxResultset.Row getDefaultInstanceForType() {
        return MysqlxResultset.Row.getDefaultInstance();
      }
      
      public MysqlxResultset.Row build() {
        MysqlxResultset.Row result = buildPartial();
        if (!result.isInitialized())
          throw newUninitializedMessageException(result); 
        return result;
      }
      
      public MysqlxResultset.Row buildPartial() {
        MysqlxResultset.Row result = new MysqlxResultset.Row(this);
        int from_bitField0_ = this.bitField0_;
        if ((this.bitField0_ & 0x1) != 0) {
          this.field_ = Collections.unmodifiableList(this.field_);
          this.bitField0_ &= 0xFFFFFFFE;
        } 
        result.field_ = this.field_;
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
        if (other instanceof MysqlxResultset.Row)
          return mergeFrom((MysqlxResultset.Row)other); 
        super.mergeFrom(other);
        return this;
      }
      
      public Builder mergeFrom(MysqlxResultset.Row other) {
        if (other == MysqlxResultset.Row.getDefaultInstance())
          return this; 
        if (!other.field_.isEmpty()) {
          if (this.field_.isEmpty()) {
            this.field_ = other.field_;
            this.bitField0_ &= 0xFFFFFFFE;
          } else {
            ensureFieldIsMutable();
            this.field_.addAll(other.field_);
          } 
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
            ByteString v;
            int tag = input.readTag();
            switch (tag) {
              case 0:
                done = true;
                continue;
              case 10:
                v = input.readBytes();
                ensureFieldIsMutable();
                this.field_.add(v);
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
      
      private void ensureFieldIsMutable() {
        if ((this.bitField0_ & 0x1) == 0) {
          this.field_ = new ArrayList<>(this.field_);
          this.bitField0_ |= 0x1;
        } 
      }
      
      public List<ByteString> getFieldList() {
        return ((this.bitField0_ & 0x1) != 0) ? 
          Collections.<ByteString>unmodifiableList(this.field_) : this.field_;
      }
      
      public int getFieldCount() {
        return this.field_.size();
      }
      
      public ByteString getField(int index) {
        return this.field_.get(index);
      }
      
      public Builder setField(int index, ByteString value) {
        if (value == null)
          throw new NullPointerException(); 
        ensureFieldIsMutable();
        this.field_.set(index, value);
        onChanged();
        return this;
      }
      
      public Builder addField(ByteString value) {
        if (value == null)
          throw new NullPointerException(); 
        ensureFieldIsMutable();
        this.field_.add(value);
        onChanged();
        return this;
      }
      
      public Builder addAllField(Iterable<? extends ByteString> values) {
        ensureFieldIsMutable();
        AbstractMessageLite.Builder.addAll(values, this.field_);
        onChanged();
        return this;
      }
      
      public Builder clearField() {
        this.field_ = Collections.emptyList();
        this.bitField0_ &= 0xFFFFFFFE;
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
    
    private static final Row DEFAULT_INSTANCE = new Row();
    
    public static Row getDefaultInstance() {
      return DEFAULT_INSTANCE;
    }
    
    @Deprecated
    public static final Parser<Row> PARSER = (Parser<Row>)new AbstractParser<Row>() {
        public MysqlxResultset.Row parsePartialFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
          MysqlxResultset.Row.Builder builder = MysqlxResultset.Row.newBuilder();
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
    
    public static Parser<Row> parser() {
      return PARSER;
    }
    
    public Parser<Row> getParserForType() {
      return PARSER;
    }
    
    public Row getDefaultInstanceForType() {
      return DEFAULT_INSTANCE;
    }
  }
  
  public static Descriptors.FileDescriptor getDescriptor() {
    return descriptor;
  }
  
  static {
    String[] descriptorData = { "\n\026mysqlx_resultset.proto\022\020Mysqlx.Resultset\032\fmysqlx.proto\"\036\n\026FetchDoneMoreOutParams:\004ê0\022\"\037\n\027FetchDoneMoreResultsets:\004ê0\020\"\021\n\tFetchDone:\004ê0\016\"\026\n\016FetchSuspended:\004ê0\017\"¥\003\n\016ColumnMetaData\0228\n\004type\030\001 \002(\0162*.Mysqlx.Resultset.ColumnMetaData.FieldType\022\f\n\004name\030\002 \001(\f\022\025\n\roriginal_name\030\003 \001(\f\022\r\n\005table\030\004 \001(\f\022\026\n\016original_table\030\005 \001(\f\022\016\n\006schema\030\006 \001(\f\022\017\n\007catalog\030\007 \001(\f\022\021\n\tcollation\030\b \001(\004\022\031\n\021fractional_digits\030\t \001(\r\022\016\n\006length\030\n \001(\r\022\r\n\005flags\030\013 \001(\r\022\024\n\fcontent_type\030\f \001(\r\"\001\n\tFieldType\022\b\n\004SINT\020\001\022\b\n\004UINT\020\002\022\n\n\006DOUBLE\020\005\022\t\n\005FLOAT\020\006\022\t\n\005BYTES\020\007\022\b\n\004TIME\020\n\022\f\n\bDATETIME\020\f\022\007\n\003SET\020\017\022\b\n\004ENUM\020\020\022\007\n\003BIT\020\021\022\013\n\007DECIMAL\020\022:\004ê0\f\"\032\n\003Row\022\r\n\005field\030\001 \003(\f:\004ê0\r*4\n\021ContentType_BYTES\022\f\n\bGEOMETRY\020\001\022\b\n\004JSON\020\002\022\007\n\003XML\020\003*.\n\024ContentType_DATETIME\022\b\n\004DATE\020\001\022\f\n\bDATETIME\020\002B\031\n\027com.mysql.cj.x.protobuf" };
    descriptor = Descriptors.FileDescriptor.internalBuildGeneratedFileFrom(descriptorData, new Descriptors.FileDescriptor[] { Mysqlx.getDescriptor() });
    internal_static_Mysqlx_Resultset_FetchDoneMoreOutParams_descriptor = getDescriptor().getMessageTypes().get(0);
    internal_static_Mysqlx_Resultset_FetchDoneMoreOutParams_fieldAccessorTable = new GeneratedMessageV3.FieldAccessorTable(internal_static_Mysqlx_Resultset_FetchDoneMoreOutParams_descriptor, new String[0]);
    internal_static_Mysqlx_Resultset_FetchDoneMoreResultsets_descriptor = getDescriptor().getMessageTypes().get(1);
    internal_static_Mysqlx_Resultset_FetchDoneMoreResultsets_fieldAccessorTable = new GeneratedMessageV3.FieldAccessorTable(internal_static_Mysqlx_Resultset_FetchDoneMoreResultsets_descriptor, new String[0]);
    internal_static_Mysqlx_Resultset_FetchDone_descriptor = getDescriptor().getMessageTypes().get(2);
    internal_static_Mysqlx_Resultset_FetchDone_fieldAccessorTable = new GeneratedMessageV3.FieldAccessorTable(internal_static_Mysqlx_Resultset_FetchDone_descriptor, new String[0]);
    internal_static_Mysqlx_Resultset_FetchSuspended_descriptor = getDescriptor().getMessageTypes().get(3);
    internal_static_Mysqlx_Resultset_FetchSuspended_fieldAccessorTable = new GeneratedMessageV3.FieldAccessorTable(internal_static_Mysqlx_Resultset_FetchSuspended_descriptor, new String[0]);
    internal_static_Mysqlx_Resultset_ColumnMetaData_descriptor = getDescriptor().getMessageTypes().get(4);
    internal_static_Mysqlx_Resultset_ColumnMetaData_fieldAccessorTable = new GeneratedMessageV3.FieldAccessorTable(internal_static_Mysqlx_Resultset_ColumnMetaData_descriptor, new String[] { 
          "Type", "Name", "OriginalName", "Table", "OriginalTable", "Schema", "Catalog", "Collation", "FractionalDigits", "Length", 
          "Flags", "ContentType" });
    internal_static_Mysqlx_Resultset_Row_descriptor = getDescriptor().getMessageTypes().get(5);
    internal_static_Mysqlx_Resultset_Row_fieldAccessorTable = new GeneratedMessageV3.FieldAccessorTable(internal_static_Mysqlx_Resultset_Row_descriptor, new String[] { "Field" });
    ExtensionRegistry registry = ExtensionRegistry.newInstance();
    registry.add(Mysqlx.serverMessageId);
    Descriptors.FileDescriptor.internalUpdateFileDescriptor(descriptor, registry);
    Mysqlx.getDescriptor();
  }
}
