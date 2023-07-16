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
import com.google.protobuf.LazyStringArrayList;
import com.google.protobuf.LazyStringList;
import com.google.protobuf.Message;
import com.google.protobuf.MessageLite;
import com.google.protobuf.MessageOrBuilder;
import com.google.protobuf.Parser;
import com.google.protobuf.ProtocolMessageEnum;
import com.google.protobuf.ProtocolStringList;
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

public final class MysqlxCrud {
  private static final Descriptors.Descriptor internal_static_Mysqlx_Crud_Column_descriptor;
  
  private static final GeneratedMessageV3.FieldAccessorTable internal_static_Mysqlx_Crud_Column_fieldAccessorTable;
  
  private static final Descriptors.Descriptor internal_static_Mysqlx_Crud_Projection_descriptor;
  
  private static final GeneratedMessageV3.FieldAccessorTable internal_static_Mysqlx_Crud_Projection_fieldAccessorTable;
  
  private static final Descriptors.Descriptor internal_static_Mysqlx_Crud_Collection_descriptor;
  
  private static final GeneratedMessageV3.FieldAccessorTable internal_static_Mysqlx_Crud_Collection_fieldAccessorTable;
  
  private static final Descriptors.Descriptor internal_static_Mysqlx_Crud_Limit_descriptor;
  
  private static final GeneratedMessageV3.FieldAccessorTable internal_static_Mysqlx_Crud_Limit_fieldAccessorTable;
  
  private static final Descriptors.Descriptor internal_static_Mysqlx_Crud_LimitExpr_descriptor;
  
  private static final GeneratedMessageV3.FieldAccessorTable internal_static_Mysqlx_Crud_LimitExpr_fieldAccessorTable;
  
  private static final Descriptors.Descriptor internal_static_Mysqlx_Crud_Order_descriptor;
  
  private static final GeneratedMessageV3.FieldAccessorTable internal_static_Mysqlx_Crud_Order_fieldAccessorTable;
  
  private static final Descriptors.Descriptor internal_static_Mysqlx_Crud_UpdateOperation_descriptor;
  
  private static final GeneratedMessageV3.FieldAccessorTable internal_static_Mysqlx_Crud_UpdateOperation_fieldAccessorTable;
  
  private static final Descriptors.Descriptor internal_static_Mysqlx_Crud_Find_descriptor;
  
  private static final GeneratedMessageV3.FieldAccessorTable internal_static_Mysqlx_Crud_Find_fieldAccessorTable;
  
  private static final Descriptors.Descriptor internal_static_Mysqlx_Crud_Insert_descriptor;
  
  private static final GeneratedMessageV3.FieldAccessorTable internal_static_Mysqlx_Crud_Insert_fieldAccessorTable;
  
  private static final Descriptors.Descriptor internal_static_Mysqlx_Crud_Insert_TypedRow_descriptor;
  
  private static final GeneratedMessageV3.FieldAccessorTable internal_static_Mysqlx_Crud_Insert_TypedRow_fieldAccessorTable;
  
  private static final Descriptors.Descriptor internal_static_Mysqlx_Crud_Update_descriptor;
  
  private static final GeneratedMessageV3.FieldAccessorTable internal_static_Mysqlx_Crud_Update_fieldAccessorTable;
  
  private static final Descriptors.Descriptor internal_static_Mysqlx_Crud_Delete_descriptor;
  
  private static final GeneratedMessageV3.FieldAccessorTable internal_static_Mysqlx_Crud_Delete_fieldAccessorTable;
  
  private static final Descriptors.Descriptor internal_static_Mysqlx_Crud_CreateView_descriptor;
  
  private static final GeneratedMessageV3.FieldAccessorTable internal_static_Mysqlx_Crud_CreateView_fieldAccessorTable;
  
  private static final Descriptors.Descriptor internal_static_Mysqlx_Crud_ModifyView_descriptor;
  
  private static final GeneratedMessageV3.FieldAccessorTable internal_static_Mysqlx_Crud_ModifyView_fieldAccessorTable;
  
  private static final Descriptors.Descriptor internal_static_Mysqlx_Crud_DropView_descriptor;
  
  private static final GeneratedMessageV3.FieldAccessorTable internal_static_Mysqlx_Crud_DropView_fieldAccessorTable;
  
  private static Descriptors.FileDescriptor descriptor;
  
  public static void registerAllExtensions(ExtensionRegistryLite registry) {}
  
  public static void registerAllExtensions(ExtensionRegistry registry) {
    registerAllExtensions((ExtensionRegistryLite)registry);
  }
  
  public enum DataModel implements ProtocolMessageEnum {
    DOCUMENT(1),
    TABLE(2);
    
    public static final int DOCUMENT_VALUE = 1;
    
    public static final int TABLE_VALUE = 2;
    
    private static final Internal.EnumLiteMap<DataModel> internalValueMap = new Internal.EnumLiteMap<DataModel>() {
        public MysqlxCrud.DataModel findValueByNumber(int number) {
          return MysqlxCrud.DataModel.forNumber(number);
        }
      };
    
    private static final DataModel[] VALUES = values();
    
    private final int value;
    
    public final int getNumber() {
      return this.value;
    }
    
    public static DataModel forNumber(int value) {
      switch (value) {
        case 1:
          return DOCUMENT;
        case 2:
          return TABLE;
      } 
      return null;
    }
    
    public static Internal.EnumLiteMap<DataModel> internalGetValueMap() {
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
      return MysqlxCrud.getDescriptor().getEnumTypes().get(0);
    }
    
    DataModel(int value) {
      this.value = value;
    }
  }
  
  public enum ViewAlgorithm implements ProtocolMessageEnum {
    UNDEFINED(1),
    MERGE(2),
    TEMPTABLE(3);
    
    public static final int UNDEFINED_VALUE = 1;
    
    public static final int MERGE_VALUE = 2;
    
    public static final int TEMPTABLE_VALUE = 3;
    
    private static final Internal.EnumLiteMap<ViewAlgorithm> internalValueMap = new Internal.EnumLiteMap<ViewAlgorithm>() {
        public MysqlxCrud.ViewAlgorithm findValueByNumber(int number) {
          return MysqlxCrud.ViewAlgorithm.forNumber(number);
        }
      };
    
    private static final ViewAlgorithm[] VALUES = values();
    
    private final int value;
    
    public final int getNumber() {
      return this.value;
    }
    
    public static ViewAlgorithm forNumber(int value) {
      switch (value) {
        case 1:
          return UNDEFINED;
        case 2:
          return MERGE;
        case 3:
          return TEMPTABLE;
      } 
      return null;
    }
    
    public static Internal.EnumLiteMap<ViewAlgorithm> internalGetValueMap() {
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
      return MysqlxCrud.getDescriptor().getEnumTypes().get(1);
    }
    
    ViewAlgorithm(int value) {
      this.value = value;
    }
  }
  
  public enum ViewSqlSecurity implements ProtocolMessageEnum {
    INVOKER(1),
    DEFINER(2);
    
    public static final int INVOKER_VALUE = 1;
    
    public static final int DEFINER_VALUE = 2;
    
    private static final Internal.EnumLiteMap<ViewSqlSecurity> internalValueMap = new Internal.EnumLiteMap<ViewSqlSecurity>() {
        public MysqlxCrud.ViewSqlSecurity findValueByNumber(int number) {
          return MysqlxCrud.ViewSqlSecurity.forNumber(number);
        }
      };
    
    private static final ViewSqlSecurity[] VALUES = values();
    
    private final int value;
    
    public final int getNumber() {
      return this.value;
    }
    
    public static ViewSqlSecurity forNumber(int value) {
      switch (value) {
        case 1:
          return INVOKER;
        case 2:
          return DEFINER;
      } 
      return null;
    }
    
    public static Internal.EnumLiteMap<ViewSqlSecurity> internalGetValueMap() {
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
      return MysqlxCrud.getDescriptor().getEnumTypes().get(2);
    }
    
    ViewSqlSecurity(int value) {
      this.value = value;
    }
  }
  
  public enum ViewCheckOption implements ProtocolMessageEnum {
    LOCAL(1),
    CASCADED(2);
    
    public static final int LOCAL_VALUE = 1;
    
    public static final int CASCADED_VALUE = 2;
    
    private static final Internal.EnumLiteMap<ViewCheckOption> internalValueMap = new Internal.EnumLiteMap<ViewCheckOption>() {
        public MysqlxCrud.ViewCheckOption findValueByNumber(int number) {
          return MysqlxCrud.ViewCheckOption.forNumber(number);
        }
      };
    
    private static final ViewCheckOption[] VALUES = values();
    
    private final int value;
    
    public final int getNumber() {
      return this.value;
    }
    
    public static ViewCheckOption forNumber(int value) {
      switch (value) {
        case 1:
          return LOCAL;
        case 2:
          return CASCADED;
      } 
      return null;
    }
    
    public static Internal.EnumLiteMap<ViewCheckOption> internalGetValueMap() {
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
      return MysqlxCrud.getDescriptor().getEnumTypes().get(3);
    }
    
    ViewCheckOption(int value) {
      this.value = value;
    }
  }
  
  public static interface ColumnOrBuilder extends MessageOrBuilder {
    boolean hasName();
    
    String getName();
    
    ByteString getNameBytes();
    
    boolean hasAlias();
    
    String getAlias();
    
    ByteString getAliasBytes();
    
    List<MysqlxExpr.DocumentPathItem> getDocumentPathList();
    
    MysqlxExpr.DocumentPathItem getDocumentPath(int param1Int);
    
    int getDocumentPathCount();
    
    List<? extends MysqlxExpr.DocumentPathItemOrBuilder> getDocumentPathOrBuilderList();
    
    MysqlxExpr.DocumentPathItemOrBuilder getDocumentPathOrBuilder(int param1Int);
  }
  
  public static final class Column extends GeneratedMessageV3 implements ColumnOrBuilder {
    private static final long serialVersionUID = 0L;
    
    private int bitField0_;
    
    public static final int NAME_FIELD_NUMBER = 1;
    
    private volatile Object name_;
    
    public static final int ALIAS_FIELD_NUMBER = 2;
    
    private volatile Object alias_;
    
    public static final int DOCUMENT_PATH_FIELD_NUMBER = 3;
    
    private List<MysqlxExpr.DocumentPathItem> documentPath_;
    
    private byte memoizedIsInitialized;
    
    private Column(GeneratedMessageV3.Builder<?> builder) {
      super(builder);
      this.memoizedIsInitialized = -1;
    }
    
    private Column() {
      this.memoizedIsInitialized = -1;
      this.name_ = "";
      this.alias_ = "";
      this.documentPath_ = Collections.emptyList();
    }
    
    protected Object newInstance(GeneratedMessageV3.UnusedPrivateParameter unused) {
      return new Column();
    }
    
    public final UnknownFieldSet getUnknownFields() {
      return this.unknownFields;
    }
    
    public static final Descriptors.Descriptor getDescriptor() {
      return MysqlxCrud.internal_static_Mysqlx_Crud_Column_descriptor;
    }
    
    protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() {
      return MysqlxCrud.internal_static_Mysqlx_Crud_Column_fieldAccessorTable.ensureFieldAccessorsInitialized(Column.class, Builder.class);
    }
    
    public boolean hasName() {
      return ((this.bitField0_ & 0x1) != 0);
    }
    
    public String getName() {
      Object ref = this.name_;
      if (ref instanceof String)
        return (String)ref; 
      ByteString bs = (ByteString)ref;
      String s = bs.toStringUtf8();
      if (bs.isValidUtf8())
        this.name_ = s; 
      return s;
    }
    
    public ByteString getNameBytes() {
      Object ref = this.name_;
      if (ref instanceof String) {
        ByteString b = ByteString.copyFromUtf8((String)ref);
        this.name_ = b;
        return b;
      } 
      return (ByteString)ref;
    }
    
    public boolean hasAlias() {
      return ((this.bitField0_ & 0x2) != 0);
    }
    
    public String getAlias() {
      Object ref = this.alias_;
      if (ref instanceof String)
        return (String)ref; 
      ByteString bs = (ByteString)ref;
      String s = bs.toStringUtf8();
      if (bs.isValidUtf8())
        this.alias_ = s; 
      return s;
    }
    
    public ByteString getAliasBytes() {
      Object ref = this.alias_;
      if (ref instanceof String) {
        ByteString b = ByteString.copyFromUtf8((String)ref);
        this.alias_ = b;
        return b;
      } 
      return (ByteString)ref;
    }
    
    public List<MysqlxExpr.DocumentPathItem> getDocumentPathList() {
      return this.documentPath_;
    }
    
    public List<? extends MysqlxExpr.DocumentPathItemOrBuilder> getDocumentPathOrBuilderList() {
      return (List)this.documentPath_;
    }
    
    public int getDocumentPathCount() {
      return this.documentPath_.size();
    }
    
    public MysqlxExpr.DocumentPathItem getDocumentPath(int index) {
      return this.documentPath_.get(index);
    }
    
    public MysqlxExpr.DocumentPathItemOrBuilder getDocumentPathOrBuilder(int index) {
      return this.documentPath_.get(index);
    }
    
    public final boolean isInitialized() {
      byte isInitialized = this.memoizedIsInitialized;
      if (isInitialized == 1)
        return true; 
      if (isInitialized == 0)
        return false; 
      for (int i = 0; i < getDocumentPathCount(); i++) {
        if (!getDocumentPath(i).isInitialized()) {
          this.memoizedIsInitialized = 0;
          return false;
        } 
      } 
      this.memoizedIsInitialized = 1;
      return true;
    }
    
    public void writeTo(CodedOutputStream output) throws IOException {
      if ((this.bitField0_ & 0x1) != 0)
        GeneratedMessageV3.writeString(output, 1, this.name_); 
      if ((this.bitField0_ & 0x2) != 0)
        GeneratedMessageV3.writeString(output, 2, this.alias_); 
      for (int i = 0; i < this.documentPath_.size(); i++)
        output.writeMessage(3, (MessageLite)this.documentPath_.get(i)); 
      getUnknownFields().writeTo(output);
    }
    
    public int getSerializedSize() {
      int size = this.memoizedSize;
      if (size != -1)
        return size; 
      size = 0;
      if ((this.bitField0_ & 0x1) != 0)
        size += GeneratedMessageV3.computeStringSize(1, this.name_); 
      if ((this.bitField0_ & 0x2) != 0)
        size += GeneratedMessageV3.computeStringSize(2, this.alias_); 
      for (int i = 0; i < this.documentPath_.size(); i++)
        size += 
          CodedOutputStream.computeMessageSize(3, (MessageLite)this.documentPath_.get(i)); 
      size += getUnknownFields().getSerializedSize();
      this.memoizedSize = size;
      return size;
    }
    
    public boolean equals(Object obj) {
      if (obj == this)
        return true; 
      if (!(obj instanceof Column))
        return super.equals(obj); 
      Column other = (Column)obj;
      if (hasName() != other.hasName())
        return false; 
      if (hasName() && 
        
        !getName().equals(other.getName()))
        return false; 
      if (hasAlias() != other.hasAlias())
        return false; 
      if (hasAlias() && 
        
        !getAlias().equals(other.getAlias()))
        return false; 
      if (!getDocumentPathList().equals(other.getDocumentPathList()))
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
      if (hasName()) {
        hash = 37 * hash + 1;
        hash = 53 * hash + getName().hashCode();
      } 
      if (hasAlias()) {
        hash = 37 * hash + 2;
        hash = 53 * hash + getAlias().hashCode();
      } 
      if (getDocumentPathCount() > 0) {
        hash = 37 * hash + 3;
        hash = 53 * hash + getDocumentPathList().hashCode();
      } 
      hash = 29 * hash + getUnknownFields().hashCode();
      this.memoizedHashCode = hash;
      return hash;
    }
    
    public static Column parseFrom(ByteBuffer data) throws InvalidProtocolBufferException {
      return (Column)PARSER.parseFrom(data);
    }
    
    public static Column parseFrom(ByteBuffer data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
      return (Column)PARSER.parseFrom(data, extensionRegistry);
    }
    
    public static Column parseFrom(ByteString data) throws InvalidProtocolBufferException {
      return (Column)PARSER.parseFrom(data);
    }
    
    public static Column parseFrom(ByteString data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
      return (Column)PARSER.parseFrom(data, extensionRegistry);
    }
    
    public static Column parseFrom(byte[] data) throws InvalidProtocolBufferException {
      return (Column)PARSER.parseFrom(data);
    }
    
    public static Column parseFrom(byte[] data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
      return (Column)PARSER.parseFrom(data, extensionRegistry);
    }
    
    public static Column parseFrom(InputStream input) throws IOException {
      return 
        (Column)GeneratedMessageV3.parseWithIOException(PARSER, input);
    }
    
    public static Column parseFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
      return 
        (Column)GeneratedMessageV3.parseWithIOException(PARSER, input, extensionRegistry);
    }
    
    public static Column parseDelimitedFrom(InputStream input) throws IOException {
      return 
        (Column)GeneratedMessageV3.parseDelimitedWithIOException(PARSER, input);
    }
    
    public static Column parseDelimitedFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
      return 
        (Column)GeneratedMessageV3.parseDelimitedWithIOException(PARSER, input, extensionRegistry);
    }
    
    public static Column parseFrom(CodedInputStream input) throws IOException {
      return 
        (Column)GeneratedMessageV3.parseWithIOException(PARSER, input);
    }
    
    public static Column parseFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
      return 
        (Column)GeneratedMessageV3.parseWithIOException(PARSER, input, extensionRegistry);
    }
    
    public Builder newBuilderForType() {
      return newBuilder();
    }
    
    public static Builder newBuilder() {
      return DEFAULT_INSTANCE.toBuilder();
    }
    
    public static Builder newBuilder(Column prototype) {
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
    
    public static final class Builder extends GeneratedMessageV3.Builder<Builder> implements MysqlxCrud.ColumnOrBuilder {
      private int bitField0_;
      
      private Object name_;
      
      private Object alias_;
      
      private List<MysqlxExpr.DocumentPathItem> documentPath_;
      
      private RepeatedFieldBuilderV3<MysqlxExpr.DocumentPathItem, MysqlxExpr.DocumentPathItem.Builder, MysqlxExpr.DocumentPathItemOrBuilder> documentPathBuilder_;
      
      public static final Descriptors.Descriptor getDescriptor() {
        return MysqlxCrud.internal_static_Mysqlx_Crud_Column_descriptor;
      }
      
      protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() {
        return MysqlxCrud.internal_static_Mysqlx_Crud_Column_fieldAccessorTable
          .ensureFieldAccessorsInitialized(MysqlxCrud.Column.class, Builder.class);
      }
      
      private Builder() {
        this.name_ = "";
        this.alias_ = "";
        this
          .documentPath_ = Collections.emptyList();
      }
      
      private Builder(GeneratedMessageV3.BuilderParent parent) {
        super(parent);
        this.name_ = "";
        this.alias_ = "";
        this.documentPath_ = Collections.emptyList();
      }
      
      public Builder clear() {
        super.clear();
        this.name_ = "";
        this.bitField0_ &= 0xFFFFFFFE;
        this.alias_ = "";
        this.bitField0_ &= 0xFFFFFFFD;
        if (this.documentPathBuilder_ == null) {
          this.documentPath_ = Collections.emptyList();
        } else {
          this.documentPath_ = null;
          this.documentPathBuilder_.clear();
        } 
        this.bitField0_ &= 0xFFFFFFFB;
        return this;
      }
      
      public Descriptors.Descriptor getDescriptorForType() {
        return MysqlxCrud.internal_static_Mysqlx_Crud_Column_descriptor;
      }
      
      public MysqlxCrud.Column getDefaultInstanceForType() {
        return MysqlxCrud.Column.getDefaultInstance();
      }
      
      public MysqlxCrud.Column build() {
        MysqlxCrud.Column result = buildPartial();
        if (!result.isInitialized())
          throw newUninitializedMessageException(result); 
        return result;
      }
      
      public MysqlxCrud.Column buildPartial() {
        MysqlxCrud.Column result = new MysqlxCrud.Column(this);
        int from_bitField0_ = this.bitField0_;
        int to_bitField0_ = 0;
        if ((from_bitField0_ & 0x1) != 0)
          to_bitField0_ |= 0x1; 
        result.name_ = this.name_;
        if ((from_bitField0_ & 0x2) != 0)
          to_bitField0_ |= 0x2; 
        result.alias_ = this.alias_;
        if (this.documentPathBuilder_ == null) {
          if ((this.bitField0_ & 0x4) != 0) {
            this.documentPath_ = Collections.unmodifiableList(this.documentPath_);
            this.bitField0_ &= 0xFFFFFFFB;
          } 
          result.documentPath_ = this.documentPath_;
        } else {
          result.documentPath_ = this.documentPathBuilder_.build();
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
        if (other instanceof MysqlxCrud.Column)
          return mergeFrom((MysqlxCrud.Column)other); 
        super.mergeFrom(other);
        return this;
      }
      
      public Builder mergeFrom(MysqlxCrud.Column other) {
        if (other == MysqlxCrud.Column.getDefaultInstance())
          return this; 
        if (other.hasName()) {
          this.bitField0_ |= 0x1;
          this.name_ = other.name_;
          onChanged();
        } 
        if (other.hasAlias()) {
          this.bitField0_ |= 0x2;
          this.alias_ = other.alias_;
          onChanged();
        } 
        if (this.documentPathBuilder_ == null) {
          if (!other.documentPath_.isEmpty()) {
            if (this.documentPath_.isEmpty()) {
              this.documentPath_ = other.documentPath_;
              this.bitField0_ &= 0xFFFFFFFB;
            } else {
              ensureDocumentPathIsMutable();
              this.documentPath_.addAll(other.documentPath_);
            } 
            onChanged();
          } 
        } else if (!other.documentPath_.isEmpty()) {
          if (this.documentPathBuilder_.isEmpty()) {
            this.documentPathBuilder_.dispose();
            this.documentPathBuilder_ = null;
            this.documentPath_ = other.documentPath_;
            this.bitField0_ &= 0xFFFFFFFB;
            this.documentPathBuilder_ = MysqlxCrud.Column.alwaysUseFieldBuilders ? getDocumentPathFieldBuilder() : null;
          } else {
            this.documentPathBuilder_.addAllMessages(other.documentPath_);
          } 
        } 
        mergeUnknownFields(other.getUnknownFields());
        onChanged();
        return this;
      }
      
      public final boolean isInitialized() {
        for (int i = 0; i < getDocumentPathCount(); i++) {
          if (!getDocumentPath(i).isInitialized())
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
            MysqlxExpr.DocumentPathItem m;
            int tag = input.readTag();
            switch (tag) {
              case 0:
                done = true;
                continue;
              case 10:
                this.name_ = input.readBytes();
                this.bitField0_ |= 0x1;
                continue;
              case 18:
                this.alias_ = input.readBytes();
                this.bitField0_ |= 0x2;
                continue;
              case 26:
                m = (MysqlxExpr.DocumentPathItem)input.readMessage(MysqlxExpr.DocumentPathItem.PARSER, extensionRegistry);
                if (this.documentPathBuilder_ == null) {
                  ensureDocumentPathIsMutable();
                  this.documentPath_.add(m);
                  continue;
                } 
                this.documentPathBuilder_.addMessage((AbstractMessage)m);
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
      
      public boolean hasName() {
        return ((this.bitField0_ & 0x1) != 0);
      }
      
      public String getName() {
        Object ref = this.name_;
        if (!(ref instanceof String)) {
          ByteString bs = (ByteString)ref;
          String s = bs.toStringUtf8();
          if (bs.isValidUtf8())
            this.name_ = s; 
          return s;
        } 
        return (String)ref;
      }
      
      public ByteString getNameBytes() {
        Object ref = this.name_;
        if (ref instanceof String) {
          ByteString b = ByteString.copyFromUtf8((String)ref);
          this.name_ = b;
          return b;
        } 
        return (ByteString)ref;
      }
      
      public Builder setName(String value) {
        if (value == null)
          throw new NullPointerException(); 
        this.bitField0_ |= 0x1;
        this.name_ = value;
        onChanged();
        return this;
      }
      
      public Builder clearName() {
        this.bitField0_ &= 0xFFFFFFFE;
        this.name_ = MysqlxCrud.Column.getDefaultInstance().getName();
        onChanged();
        return this;
      }
      
      public Builder setNameBytes(ByteString value) {
        if (value == null)
          throw new NullPointerException(); 
        this.bitField0_ |= 0x1;
        this.name_ = value;
        onChanged();
        return this;
      }
      
      public boolean hasAlias() {
        return ((this.bitField0_ & 0x2) != 0);
      }
      
      public String getAlias() {
        Object ref = this.alias_;
        if (!(ref instanceof String)) {
          ByteString bs = (ByteString)ref;
          String s = bs.toStringUtf8();
          if (bs.isValidUtf8())
            this.alias_ = s; 
          return s;
        } 
        return (String)ref;
      }
      
      public ByteString getAliasBytes() {
        Object ref = this.alias_;
        if (ref instanceof String) {
          ByteString b = ByteString.copyFromUtf8((String)ref);
          this.alias_ = b;
          return b;
        } 
        return (ByteString)ref;
      }
      
      public Builder setAlias(String value) {
        if (value == null)
          throw new NullPointerException(); 
        this.bitField0_ |= 0x2;
        this.alias_ = value;
        onChanged();
        return this;
      }
      
      public Builder clearAlias() {
        this.bitField0_ &= 0xFFFFFFFD;
        this.alias_ = MysqlxCrud.Column.getDefaultInstance().getAlias();
        onChanged();
        return this;
      }
      
      public Builder setAliasBytes(ByteString value) {
        if (value == null)
          throw new NullPointerException(); 
        this.bitField0_ |= 0x2;
        this.alias_ = value;
        onChanged();
        return this;
      }
      
      private void ensureDocumentPathIsMutable() {
        if ((this.bitField0_ & 0x4) == 0) {
          this.documentPath_ = new ArrayList<>(this.documentPath_);
          this.bitField0_ |= 0x4;
        } 
      }
      
      public List<MysqlxExpr.DocumentPathItem> getDocumentPathList() {
        if (this.documentPathBuilder_ == null)
          return Collections.unmodifiableList(this.documentPath_); 
        return this.documentPathBuilder_.getMessageList();
      }
      
      public int getDocumentPathCount() {
        if (this.documentPathBuilder_ == null)
          return this.documentPath_.size(); 
        return this.documentPathBuilder_.getCount();
      }
      
      public MysqlxExpr.DocumentPathItem getDocumentPath(int index) {
        if (this.documentPathBuilder_ == null)
          return this.documentPath_.get(index); 
        return (MysqlxExpr.DocumentPathItem)this.documentPathBuilder_.getMessage(index);
      }
      
      public Builder setDocumentPath(int index, MysqlxExpr.DocumentPathItem value) {
        if (this.documentPathBuilder_ == null) {
          if (value == null)
            throw new NullPointerException(); 
          ensureDocumentPathIsMutable();
          this.documentPath_.set(index, value);
          onChanged();
        } else {
          this.documentPathBuilder_.setMessage(index, (AbstractMessage)value);
        } 
        return this;
      }
      
      public Builder setDocumentPath(int index, MysqlxExpr.DocumentPathItem.Builder builderForValue) {
        if (this.documentPathBuilder_ == null) {
          ensureDocumentPathIsMutable();
          this.documentPath_.set(index, builderForValue.build());
          onChanged();
        } else {
          this.documentPathBuilder_.setMessage(index, (AbstractMessage)builderForValue.build());
        } 
        return this;
      }
      
      public Builder addDocumentPath(MysqlxExpr.DocumentPathItem value) {
        if (this.documentPathBuilder_ == null) {
          if (value == null)
            throw new NullPointerException(); 
          ensureDocumentPathIsMutable();
          this.documentPath_.add(value);
          onChanged();
        } else {
          this.documentPathBuilder_.addMessage((AbstractMessage)value);
        } 
        return this;
      }
      
      public Builder addDocumentPath(int index, MysqlxExpr.DocumentPathItem value) {
        if (this.documentPathBuilder_ == null) {
          if (value == null)
            throw new NullPointerException(); 
          ensureDocumentPathIsMutable();
          this.documentPath_.add(index, value);
          onChanged();
        } else {
          this.documentPathBuilder_.addMessage(index, (AbstractMessage)value);
        } 
        return this;
      }
      
      public Builder addDocumentPath(MysqlxExpr.DocumentPathItem.Builder builderForValue) {
        if (this.documentPathBuilder_ == null) {
          ensureDocumentPathIsMutable();
          this.documentPath_.add(builderForValue.build());
          onChanged();
        } else {
          this.documentPathBuilder_.addMessage((AbstractMessage)builderForValue.build());
        } 
        return this;
      }
      
      public Builder addDocumentPath(int index, MysqlxExpr.DocumentPathItem.Builder builderForValue) {
        if (this.documentPathBuilder_ == null) {
          ensureDocumentPathIsMutable();
          this.documentPath_.add(index, builderForValue.build());
          onChanged();
        } else {
          this.documentPathBuilder_.addMessage(index, (AbstractMessage)builderForValue.build());
        } 
        return this;
      }
      
      public Builder addAllDocumentPath(Iterable<? extends MysqlxExpr.DocumentPathItem> values) {
        if (this.documentPathBuilder_ == null) {
          ensureDocumentPathIsMutable();
          AbstractMessageLite.Builder.addAll(values, this.documentPath_);
          onChanged();
        } else {
          this.documentPathBuilder_.addAllMessages(values);
        } 
        return this;
      }
      
      public Builder clearDocumentPath() {
        if (this.documentPathBuilder_ == null) {
          this.documentPath_ = Collections.emptyList();
          this.bitField0_ &= 0xFFFFFFFB;
          onChanged();
        } else {
          this.documentPathBuilder_.clear();
        } 
        return this;
      }
      
      public Builder removeDocumentPath(int index) {
        if (this.documentPathBuilder_ == null) {
          ensureDocumentPathIsMutable();
          this.documentPath_.remove(index);
          onChanged();
        } else {
          this.documentPathBuilder_.remove(index);
        } 
        return this;
      }
      
      public MysqlxExpr.DocumentPathItem.Builder getDocumentPathBuilder(int index) {
        return (MysqlxExpr.DocumentPathItem.Builder)getDocumentPathFieldBuilder().getBuilder(index);
      }
      
      public MysqlxExpr.DocumentPathItemOrBuilder getDocumentPathOrBuilder(int index) {
        if (this.documentPathBuilder_ == null)
          return this.documentPath_.get(index); 
        return (MysqlxExpr.DocumentPathItemOrBuilder)this.documentPathBuilder_.getMessageOrBuilder(index);
      }
      
      public List<? extends MysqlxExpr.DocumentPathItemOrBuilder> getDocumentPathOrBuilderList() {
        if (this.documentPathBuilder_ != null)
          return this.documentPathBuilder_.getMessageOrBuilderList(); 
        return Collections.unmodifiableList((List)this.documentPath_);
      }
      
      public MysqlxExpr.DocumentPathItem.Builder addDocumentPathBuilder() {
        return (MysqlxExpr.DocumentPathItem.Builder)getDocumentPathFieldBuilder().addBuilder(
            (AbstractMessage)MysqlxExpr.DocumentPathItem.getDefaultInstance());
      }
      
      public MysqlxExpr.DocumentPathItem.Builder addDocumentPathBuilder(int index) {
        return (MysqlxExpr.DocumentPathItem.Builder)getDocumentPathFieldBuilder().addBuilder(index, 
            (AbstractMessage)MysqlxExpr.DocumentPathItem.getDefaultInstance());
      }
      
      public List<MysqlxExpr.DocumentPathItem.Builder> getDocumentPathBuilderList() {
        return getDocumentPathFieldBuilder().getBuilderList();
      }
      
      private RepeatedFieldBuilderV3<MysqlxExpr.DocumentPathItem, MysqlxExpr.DocumentPathItem.Builder, MysqlxExpr.DocumentPathItemOrBuilder> getDocumentPathFieldBuilder() {
        if (this.documentPathBuilder_ == null) {
          this
            
            .documentPathBuilder_ = new RepeatedFieldBuilderV3(this.documentPath_, ((this.bitField0_ & 0x4) != 0), (AbstractMessage.BuilderParent)getParentForChildren(), isClean());
          this.documentPath_ = null;
        } 
        return this.documentPathBuilder_;
      }
      
      public final Builder setUnknownFields(UnknownFieldSet unknownFields) {
        return (Builder)super.setUnknownFields(unknownFields);
      }
      
      public final Builder mergeUnknownFields(UnknownFieldSet unknownFields) {
        return (Builder)super.mergeUnknownFields(unknownFields);
      }
    }
    
    private static final Column DEFAULT_INSTANCE = new Column();
    
    public static Column getDefaultInstance() {
      return DEFAULT_INSTANCE;
    }
    
    @Deprecated
    public static final Parser<Column> PARSER = (Parser<Column>)new AbstractParser<Column>() {
        public MysqlxCrud.Column parsePartialFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
          MysqlxCrud.Column.Builder builder = MysqlxCrud.Column.newBuilder();
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
    
    public static Parser<Column> parser() {
      return PARSER;
    }
    
    public Parser<Column> getParserForType() {
      return PARSER;
    }
    
    public Column getDefaultInstanceForType() {
      return DEFAULT_INSTANCE;
    }
  }
  
  public static interface ProjectionOrBuilder extends MessageOrBuilder {
    boolean hasSource();
    
    MysqlxExpr.Expr getSource();
    
    MysqlxExpr.ExprOrBuilder getSourceOrBuilder();
    
    boolean hasAlias();
    
    String getAlias();
    
    ByteString getAliasBytes();
  }
  
  public static final class Projection extends GeneratedMessageV3 implements ProjectionOrBuilder {
    private static final long serialVersionUID = 0L;
    
    private int bitField0_;
    
    public static final int SOURCE_FIELD_NUMBER = 1;
    
    private MysqlxExpr.Expr source_;
    
    public static final int ALIAS_FIELD_NUMBER = 2;
    
    private volatile Object alias_;
    
    private byte memoizedIsInitialized;
    
    private Projection(GeneratedMessageV3.Builder<?> builder) {
      super(builder);
      this.memoizedIsInitialized = -1;
    }
    
    private Projection() {
      this.memoizedIsInitialized = -1;
      this.alias_ = "";
    }
    
    protected Object newInstance(GeneratedMessageV3.UnusedPrivateParameter unused) {
      return new Projection();
    }
    
    public final UnknownFieldSet getUnknownFields() {
      return this.unknownFields;
    }
    
    public static final Descriptors.Descriptor getDescriptor() {
      return MysqlxCrud.internal_static_Mysqlx_Crud_Projection_descriptor;
    }
    
    protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() {
      return MysqlxCrud.internal_static_Mysqlx_Crud_Projection_fieldAccessorTable.ensureFieldAccessorsInitialized(Projection.class, Builder.class);
    }
    
    public boolean hasSource() {
      return ((this.bitField0_ & 0x1) != 0);
    }
    
    public MysqlxExpr.Expr getSource() {
      return (this.source_ == null) ? MysqlxExpr.Expr.getDefaultInstance() : this.source_;
    }
    
    public MysqlxExpr.ExprOrBuilder getSourceOrBuilder() {
      return (this.source_ == null) ? MysqlxExpr.Expr.getDefaultInstance() : this.source_;
    }
    
    public boolean hasAlias() {
      return ((this.bitField0_ & 0x2) != 0);
    }
    
    public String getAlias() {
      Object ref = this.alias_;
      if (ref instanceof String)
        return (String)ref; 
      ByteString bs = (ByteString)ref;
      String s = bs.toStringUtf8();
      if (bs.isValidUtf8())
        this.alias_ = s; 
      return s;
    }
    
    public ByteString getAliasBytes() {
      Object ref = this.alias_;
      if (ref instanceof String) {
        ByteString b = ByteString.copyFromUtf8((String)ref);
        this.alias_ = b;
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
      if (!hasSource()) {
        this.memoizedIsInitialized = 0;
        return false;
      } 
      if (!getSource().isInitialized()) {
        this.memoizedIsInitialized = 0;
        return false;
      } 
      this.memoizedIsInitialized = 1;
      return true;
    }
    
    public void writeTo(CodedOutputStream output) throws IOException {
      if ((this.bitField0_ & 0x1) != 0)
        output.writeMessage(1, (MessageLite)getSource()); 
      if ((this.bitField0_ & 0x2) != 0)
        GeneratedMessageV3.writeString(output, 2, this.alias_); 
      getUnknownFields().writeTo(output);
    }
    
    public int getSerializedSize() {
      int size = this.memoizedSize;
      if (size != -1)
        return size; 
      size = 0;
      if ((this.bitField0_ & 0x1) != 0)
        size += 
          CodedOutputStream.computeMessageSize(1, (MessageLite)getSource()); 
      if ((this.bitField0_ & 0x2) != 0)
        size += GeneratedMessageV3.computeStringSize(2, this.alias_); 
      size += getUnknownFields().getSerializedSize();
      this.memoizedSize = size;
      return size;
    }
    
    public boolean equals(Object obj) {
      if (obj == this)
        return true; 
      if (!(obj instanceof Projection))
        return super.equals(obj); 
      Projection other = (Projection)obj;
      if (hasSource() != other.hasSource())
        return false; 
      if (hasSource() && 
        
        !getSource().equals(other.getSource()))
        return false; 
      if (hasAlias() != other.hasAlias())
        return false; 
      if (hasAlias() && 
        
        !getAlias().equals(other.getAlias()))
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
      if (hasSource()) {
        hash = 37 * hash + 1;
        hash = 53 * hash + getSource().hashCode();
      } 
      if (hasAlias()) {
        hash = 37 * hash + 2;
        hash = 53 * hash + getAlias().hashCode();
      } 
      hash = 29 * hash + getUnknownFields().hashCode();
      this.memoizedHashCode = hash;
      return hash;
    }
    
    public static Projection parseFrom(ByteBuffer data) throws InvalidProtocolBufferException {
      return (Projection)PARSER.parseFrom(data);
    }
    
    public static Projection parseFrom(ByteBuffer data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
      return (Projection)PARSER.parseFrom(data, extensionRegistry);
    }
    
    public static Projection parseFrom(ByteString data) throws InvalidProtocolBufferException {
      return (Projection)PARSER.parseFrom(data);
    }
    
    public static Projection parseFrom(ByteString data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
      return (Projection)PARSER.parseFrom(data, extensionRegistry);
    }
    
    public static Projection parseFrom(byte[] data) throws InvalidProtocolBufferException {
      return (Projection)PARSER.parseFrom(data);
    }
    
    public static Projection parseFrom(byte[] data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
      return (Projection)PARSER.parseFrom(data, extensionRegistry);
    }
    
    public static Projection parseFrom(InputStream input) throws IOException {
      return 
        (Projection)GeneratedMessageV3.parseWithIOException(PARSER, input);
    }
    
    public static Projection parseFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
      return 
        (Projection)GeneratedMessageV3.parseWithIOException(PARSER, input, extensionRegistry);
    }
    
    public static Projection parseDelimitedFrom(InputStream input) throws IOException {
      return 
        (Projection)GeneratedMessageV3.parseDelimitedWithIOException(PARSER, input);
    }
    
    public static Projection parseDelimitedFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
      return 
        (Projection)GeneratedMessageV3.parseDelimitedWithIOException(PARSER, input, extensionRegistry);
    }
    
    public static Projection parseFrom(CodedInputStream input) throws IOException {
      return 
        (Projection)GeneratedMessageV3.parseWithIOException(PARSER, input);
    }
    
    public static Projection parseFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
      return 
        (Projection)GeneratedMessageV3.parseWithIOException(PARSER, input, extensionRegistry);
    }
    
    public Builder newBuilderForType() {
      return newBuilder();
    }
    
    public static Builder newBuilder() {
      return DEFAULT_INSTANCE.toBuilder();
    }
    
    public static Builder newBuilder(Projection prototype) {
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
    
    public static final class Builder extends GeneratedMessageV3.Builder<Builder> implements MysqlxCrud.ProjectionOrBuilder {
      private int bitField0_;
      
      private MysqlxExpr.Expr source_;
      
      private SingleFieldBuilderV3<MysqlxExpr.Expr, MysqlxExpr.Expr.Builder, MysqlxExpr.ExprOrBuilder> sourceBuilder_;
      
      private Object alias_;
      
      public static final Descriptors.Descriptor getDescriptor() {
        return MysqlxCrud.internal_static_Mysqlx_Crud_Projection_descriptor;
      }
      
      protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() {
        return MysqlxCrud.internal_static_Mysqlx_Crud_Projection_fieldAccessorTable
          .ensureFieldAccessorsInitialized(MysqlxCrud.Projection.class, Builder.class);
      }
      
      private Builder() {
        this.alias_ = "";
        maybeForceBuilderInitialization();
      }
      
      private Builder(GeneratedMessageV3.BuilderParent parent) {
        super(parent);
        this.alias_ = "";
        maybeForceBuilderInitialization();
      }
      
      private void maybeForceBuilderInitialization() {
        if (MysqlxCrud.Projection.alwaysUseFieldBuilders)
          getSourceFieldBuilder(); 
      }
      
      public Builder clear() {
        super.clear();
        if (this.sourceBuilder_ == null) {
          this.source_ = null;
        } else {
          this.sourceBuilder_.clear();
        } 
        this.bitField0_ &= 0xFFFFFFFE;
        this.alias_ = "";
        this.bitField0_ &= 0xFFFFFFFD;
        return this;
      }
      
      public Descriptors.Descriptor getDescriptorForType() {
        return MysqlxCrud.internal_static_Mysqlx_Crud_Projection_descriptor;
      }
      
      public MysqlxCrud.Projection getDefaultInstanceForType() {
        return MysqlxCrud.Projection.getDefaultInstance();
      }
      
      public MysqlxCrud.Projection build() {
        MysqlxCrud.Projection result = buildPartial();
        if (!result.isInitialized())
          throw newUninitializedMessageException(result); 
        return result;
      }
      
      public MysqlxCrud.Projection buildPartial() {
        MysqlxCrud.Projection result = new MysqlxCrud.Projection(this);
        int from_bitField0_ = this.bitField0_;
        int to_bitField0_ = 0;
        if ((from_bitField0_ & 0x1) != 0) {
          if (this.sourceBuilder_ == null) {
            result.source_ = this.source_;
          } else {
            result.source_ = (MysqlxExpr.Expr)this.sourceBuilder_.build();
          } 
          to_bitField0_ |= 0x1;
        } 
        if ((from_bitField0_ & 0x2) != 0)
          to_bitField0_ |= 0x2; 
        result.alias_ = this.alias_;
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
        if (other instanceof MysqlxCrud.Projection)
          return mergeFrom((MysqlxCrud.Projection)other); 
        super.mergeFrom(other);
        return this;
      }
      
      public Builder mergeFrom(MysqlxCrud.Projection other) {
        if (other == MysqlxCrud.Projection.getDefaultInstance())
          return this; 
        if (other.hasSource())
          mergeSource(other.getSource()); 
        if (other.hasAlias()) {
          this.bitField0_ |= 0x2;
          this.alias_ = other.alias_;
          onChanged();
        } 
        mergeUnknownFields(other.getUnknownFields());
        onChanged();
        return this;
      }
      
      public final boolean isInitialized() {
        if (!hasSource())
          return false; 
        if (!getSource().isInitialized())
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
                input.readMessage((MessageLite.Builder)getSourceFieldBuilder().getBuilder(), extensionRegistry);
                this.bitField0_ |= 0x1;
                continue;
              case 18:
                this.alias_ = input.readBytes();
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
      
      public boolean hasSource() {
        return ((this.bitField0_ & 0x1) != 0);
      }
      
      public MysqlxExpr.Expr getSource() {
        if (this.sourceBuilder_ == null)
          return (this.source_ == null) ? MysqlxExpr.Expr.getDefaultInstance() : this.source_; 
        return (MysqlxExpr.Expr)this.sourceBuilder_.getMessage();
      }
      
      public Builder setSource(MysqlxExpr.Expr value) {
        if (this.sourceBuilder_ == null) {
          if (value == null)
            throw new NullPointerException(); 
          this.source_ = value;
          onChanged();
        } else {
          this.sourceBuilder_.setMessage((AbstractMessage)value);
        } 
        this.bitField0_ |= 0x1;
        return this;
      }
      
      public Builder setSource(MysqlxExpr.Expr.Builder builderForValue) {
        if (this.sourceBuilder_ == null) {
          this.source_ = builderForValue.build();
          onChanged();
        } else {
          this.sourceBuilder_.setMessage((AbstractMessage)builderForValue.build());
        } 
        this.bitField0_ |= 0x1;
        return this;
      }
      
      public Builder mergeSource(MysqlxExpr.Expr value) {
        if (this.sourceBuilder_ == null) {
          if ((this.bitField0_ & 0x1) != 0 && this.source_ != null && this.source_ != MysqlxExpr.Expr.getDefaultInstance()) {
            this.source_ = MysqlxExpr.Expr.newBuilder(this.source_).mergeFrom(value).buildPartial();
          } else {
            this.source_ = value;
          } 
          onChanged();
        } else {
          this.sourceBuilder_.mergeFrom((AbstractMessage)value);
        } 
        this.bitField0_ |= 0x1;
        return this;
      }
      
      public Builder clearSource() {
        if (this.sourceBuilder_ == null) {
          this.source_ = null;
          onChanged();
        } else {
          this.sourceBuilder_.clear();
        } 
        this.bitField0_ &= 0xFFFFFFFE;
        return this;
      }
      
      public MysqlxExpr.Expr.Builder getSourceBuilder() {
        this.bitField0_ |= 0x1;
        onChanged();
        return (MysqlxExpr.Expr.Builder)getSourceFieldBuilder().getBuilder();
      }
      
      public MysqlxExpr.ExprOrBuilder getSourceOrBuilder() {
        if (this.sourceBuilder_ != null)
          return (MysqlxExpr.ExprOrBuilder)this.sourceBuilder_.getMessageOrBuilder(); 
        return (this.source_ == null) ? MysqlxExpr.Expr.getDefaultInstance() : this.source_;
      }
      
      private SingleFieldBuilderV3<MysqlxExpr.Expr, MysqlxExpr.Expr.Builder, MysqlxExpr.ExprOrBuilder> getSourceFieldBuilder() {
        if (this.sourceBuilder_ == null) {
          this.sourceBuilder_ = new SingleFieldBuilderV3((AbstractMessage)getSource(), (AbstractMessage.BuilderParent)getParentForChildren(), isClean());
          this.source_ = null;
        } 
        return this.sourceBuilder_;
      }
      
      public boolean hasAlias() {
        return ((this.bitField0_ & 0x2) != 0);
      }
      
      public String getAlias() {
        Object ref = this.alias_;
        if (!(ref instanceof String)) {
          ByteString bs = (ByteString)ref;
          String s = bs.toStringUtf8();
          if (bs.isValidUtf8())
            this.alias_ = s; 
          return s;
        } 
        return (String)ref;
      }
      
      public ByteString getAliasBytes() {
        Object ref = this.alias_;
        if (ref instanceof String) {
          ByteString b = ByteString.copyFromUtf8((String)ref);
          this.alias_ = b;
          return b;
        } 
        return (ByteString)ref;
      }
      
      public Builder setAlias(String value) {
        if (value == null)
          throw new NullPointerException(); 
        this.bitField0_ |= 0x2;
        this.alias_ = value;
        onChanged();
        return this;
      }
      
      public Builder clearAlias() {
        this.bitField0_ &= 0xFFFFFFFD;
        this.alias_ = MysqlxCrud.Projection.getDefaultInstance().getAlias();
        onChanged();
        return this;
      }
      
      public Builder setAliasBytes(ByteString value) {
        if (value == null)
          throw new NullPointerException(); 
        this.bitField0_ |= 0x2;
        this.alias_ = value;
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
    
    private static final Projection DEFAULT_INSTANCE = new Projection();
    
    public static Projection getDefaultInstance() {
      return DEFAULT_INSTANCE;
    }
    
    @Deprecated
    public static final Parser<Projection> PARSER = (Parser<Projection>)new AbstractParser<Projection>() {
        public MysqlxCrud.Projection parsePartialFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
          MysqlxCrud.Projection.Builder builder = MysqlxCrud.Projection.newBuilder();
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
    
    public static Parser<Projection> parser() {
      return PARSER;
    }
    
    public Parser<Projection> getParserForType() {
      return PARSER;
    }
    
    public Projection getDefaultInstanceForType() {
      return DEFAULT_INSTANCE;
    }
  }
  
  public static interface CollectionOrBuilder extends MessageOrBuilder {
    boolean hasName();
    
    String getName();
    
    ByteString getNameBytes();
    
    boolean hasSchema();
    
    String getSchema();
    
    ByteString getSchemaBytes();
  }
  
  public static final class Collection extends GeneratedMessageV3 implements CollectionOrBuilder {
    private static final long serialVersionUID = 0L;
    
    private int bitField0_;
    
    public static final int NAME_FIELD_NUMBER = 1;
    
    private volatile Object name_;
    
    public static final int SCHEMA_FIELD_NUMBER = 2;
    
    private volatile Object schema_;
    
    private byte memoizedIsInitialized;
    
    private Collection(GeneratedMessageV3.Builder<?> builder) {
      super(builder);
      this.memoizedIsInitialized = -1;
    }
    
    private Collection() {
      this.memoizedIsInitialized = -1;
      this.name_ = "";
      this.schema_ = "";
    }
    
    protected Object newInstance(GeneratedMessageV3.UnusedPrivateParameter unused) {
      return new Collection();
    }
    
    public final UnknownFieldSet getUnknownFields() {
      return this.unknownFields;
    }
    
    public static final Descriptors.Descriptor getDescriptor() {
      return MysqlxCrud.internal_static_Mysqlx_Crud_Collection_descriptor;
    }
    
    protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() {
      return MysqlxCrud.internal_static_Mysqlx_Crud_Collection_fieldAccessorTable.ensureFieldAccessorsInitialized(Collection.class, Builder.class);
    }
    
    public boolean hasName() {
      return ((this.bitField0_ & 0x1) != 0);
    }
    
    public String getName() {
      Object ref = this.name_;
      if (ref instanceof String)
        return (String)ref; 
      ByteString bs = (ByteString)ref;
      String s = bs.toStringUtf8();
      if (bs.isValidUtf8())
        this.name_ = s; 
      return s;
    }
    
    public ByteString getNameBytes() {
      Object ref = this.name_;
      if (ref instanceof String) {
        ByteString b = ByteString.copyFromUtf8((String)ref);
        this.name_ = b;
        return b;
      } 
      return (ByteString)ref;
    }
    
    public boolean hasSchema() {
      return ((this.bitField0_ & 0x2) != 0);
    }
    
    public String getSchema() {
      Object ref = this.schema_;
      if (ref instanceof String)
        return (String)ref; 
      ByteString bs = (ByteString)ref;
      String s = bs.toStringUtf8();
      if (bs.isValidUtf8())
        this.schema_ = s; 
      return s;
    }
    
    public ByteString getSchemaBytes() {
      Object ref = this.schema_;
      if (ref instanceof String) {
        ByteString b = ByteString.copyFromUtf8((String)ref);
        this.schema_ = b;
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
      if (!hasName()) {
        this.memoizedIsInitialized = 0;
        return false;
      } 
      this.memoizedIsInitialized = 1;
      return true;
    }
    
    public void writeTo(CodedOutputStream output) throws IOException {
      if ((this.bitField0_ & 0x1) != 0)
        GeneratedMessageV3.writeString(output, 1, this.name_); 
      if ((this.bitField0_ & 0x2) != 0)
        GeneratedMessageV3.writeString(output, 2, this.schema_); 
      getUnknownFields().writeTo(output);
    }
    
    public int getSerializedSize() {
      int size = this.memoizedSize;
      if (size != -1)
        return size; 
      size = 0;
      if ((this.bitField0_ & 0x1) != 0)
        size += GeneratedMessageV3.computeStringSize(1, this.name_); 
      if ((this.bitField0_ & 0x2) != 0)
        size += GeneratedMessageV3.computeStringSize(2, this.schema_); 
      size += getUnknownFields().getSerializedSize();
      this.memoizedSize = size;
      return size;
    }
    
    public boolean equals(Object obj) {
      if (obj == this)
        return true; 
      if (!(obj instanceof Collection))
        return super.equals(obj); 
      Collection other = (Collection)obj;
      if (hasName() != other.hasName())
        return false; 
      if (hasName() && 
        
        !getName().equals(other.getName()))
        return false; 
      if (hasSchema() != other.hasSchema())
        return false; 
      if (hasSchema() && 
        
        !getSchema().equals(other.getSchema()))
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
      if (hasName()) {
        hash = 37 * hash + 1;
        hash = 53 * hash + getName().hashCode();
      } 
      if (hasSchema()) {
        hash = 37 * hash + 2;
        hash = 53 * hash + getSchema().hashCode();
      } 
      hash = 29 * hash + getUnknownFields().hashCode();
      this.memoizedHashCode = hash;
      return hash;
    }
    
    public static Collection parseFrom(ByteBuffer data) throws InvalidProtocolBufferException {
      return (Collection)PARSER.parseFrom(data);
    }
    
    public static Collection parseFrom(ByteBuffer data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
      return (Collection)PARSER.parseFrom(data, extensionRegistry);
    }
    
    public static Collection parseFrom(ByteString data) throws InvalidProtocolBufferException {
      return (Collection)PARSER.parseFrom(data);
    }
    
    public static Collection parseFrom(ByteString data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
      return (Collection)PARSER.parseFrom(data, extensionRegistry);
    }
    
    public static Collection parseFrom(byte[] data) throws InvalidProtocolBufferException {
      return (Collection)PARSER.parseFrom(data);
    }
    
    public static Collection parseFrom(byte[] data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
      return (Collection)PARSER.parseFrom(data, extensionRegistry);
    }
    
    public static Collection parseFrom(InputStream input) throws IOException {
      return 
        (Collection)GeneratedMessageV3.parseWithIOException(PARSER, input);
    }
    
    public static Collection parseFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
      return 
        (Collection)GeneratedMessageV3.parseWithIOException(PARSER, input, extensionRegistry);
    }
    
    public static Collection parseDelimitedFrom(InputStream input) throws IOException {
      return 
        (Collection)GeneratedMessageV3.parseDelimitedWithIOException(PARSER, input);
    }
    
    public static Collection parseDelimitedFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
      return 
        (Collection)GeneratedMessageV3.parseDelimitedWithIOException(PARSER, input, extensionRegistry);
    }
    
    public static Collection parseFrom(CodedInputStream input) throws IOException {
      return 
        (Collection)GeneratedMessageV3.parseWithIOException(PARSER, input);
    }
    
    public static Collection parseFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
      return 
        (Collection)GeneratedMessageV3.parseWithIOException(PARSER, input, extensionRegistry);
    }
    
    public Builder newBuilderForType() {
      return newBuilder();
    }
    
    public static Builder newBuilder() {
      return DEFAULT_INSTANCE.toBuilder();
    }
    
    public static Builder newBuilder(Collection prototype) {
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
    
    public static final class Builder extends GeneratedMessageV3.Builder<Builder> implements MysqlxCrud.CollectionOrBuilder {
      private int bitField0_;
      
      private Object name_;
      
      private Object schema_;
      
      public static final Descriptors.Descriptor getDescriptor() {
        return MysqlxCrud.internal_static_Mysqlx_Crud_Collection_descriptor;
      }
      
      protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() {
        return MysqlxCrud.internal_static_Mysqlx_Crud_Collection_fieldAccessorTable
          .ensureFieldAccessorsInitialized(MysqlxCrud.Collection.class, Builder.class);
      }
      
      private Builder() {
        this.name_ = "";
        this.schema_ = "";
      }
      
      private Builder(GeneratedMessageV3.BuilderParent parent) {
        super(parent);
        this.name_ = "";
        this.schema_ = "";
      }
      
      public Builder clear() {
        super.clear();
        this.name_ = "";
        this.bitField0_ &= 0xFFFFFFFE;
        this.schema_ = "";
        this.bitField0_ &= 0xFFFFFFFD;
        return this;
      }
      
      public Descriptors.Descriptor getDescriptorForType() {
        return MysqlxCrud.internal_static_Mysqlx_Crud_Collection_descriptor;
      }
      
      public MysqlxCrud.Collection getDefaultInstanceForType() {
        return MysqlxCrud.Collection.getDefaultInstance();
      }
      
      public MysqlxCrud.Collection build() {
        MysqlxCrud.Collection result = buildPartial();
        if (!result.isInitialized())
          throw newUninitializedMessageException(result); 
        return result;
      }
      
      public MysqlxCrud.Collection buildPartial() {
        MysqlxCrud.Collection result = new MysqlxCrud.Collection(this);
        int from_bitField0_ = this.bitField0_;
        int to_bitField0_ = 0;
        if ((from_bitField0_ & 0x1) != 0)
          to_bitField0_ |= 0x1; 
        result.name_ = this.name_;
        if ((from_bitField0_ & 0x2) != 0)
          to_bitField0_ |= 0x2; 
        result.schema_ = this.schema_;
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
        if (other instanceof MysqlxCrud.Collection)
          return mergeFrom((MysqlxCrud.Collection)other); 
        super.mergeFrom(other);
        return this;
      }
      
      public Builder mergeFrom(MysqlxCrud.Collection other) {
        if (other == MysqlxCrud.Collection.getDefaultInstance())
          return this; 
        if (other.hasName()) {
          this.bitField0_ |= 0x1;
          this.name_ = other.name_;
          onChanged();
        } 
        if (other.hasSchema()) {
          this.bitField0_ |= 0x2;
          this.schema_ = other.schema_;
          onChanged();
        } 
        mergeUnknownFields(other.getUnknownFields());
        onChanged();
        return this;
      }
      
      public final boolean isInitialized() {
        if (!hasName())
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
                this.name_ = input.readBytes();
                this.bitField0_ |= 0x1;
                continue;
              case 18:
                this.schema_ = input.readBytes();
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
      
      public boolean hasName() {
        return ((this.bitField0_ & 0x1) != 0);
      }
      
      public String getName() {
        Object ref = this.name_;
        if (!(ref instanceof String)) {
          ByteString bs = (ByteString)ref;
          String s = bs.toStringUtf8();
          if (bs.isValidUtf8())
            this.name_ = s; 
          return s;
        } 
        return (String)ref;
      }
      
      public ByteString getNameBytes() {
        Object ref = this.name_;
        if (ref instanceof String) {
          ByteString b = ByteString.copyFromUtf8((String)ref);
          this.name_ = b;
          return b;
        } 
        return (ByteString)ref;
      }
      
      public Builder setName(String value) {
        if (value == null)
          throw new NullPointerException(); 
        this.bitField0_ |= 0x1;
        this.name_ = value;
        onChanged();
        return this;
      }
      
      public Builder clearName() {
        this.bitField0_ &= 0xFFFFFFFE;
        this.name_ = MysqlxCrud.Collection.getDefaultInstance().getName();
        onChanged();
        return this;
      }
      
      public Builder setNameBytes(ByteString value) {
        if (value == null)
          throw new NullPointerException(); 
        this.bitField0_ |= 0x1;
        this.name_ = value;
        onChanged();
        return this;
      }
      
      public boolean hasSchema() {
        return ((this.bitField0_ & 0x2) != 0);
      }
      
      public String getSchema() {
        Object ref = this.schema_;
        if (!(ref instanceof String)) {
          ByteString bs = (ByteString)ref;
          String s = bs.toStringUtf8();
          if (bs.isValidUtf8())
            this.schema_ = s; 
          return s;
        } 
        return (String)ref;
      }
      
      public ByteString getSchemaBytes() {
        Object ref = this.schema_;
        if (ref instanceof String) {
          ByteString b = ByteString.copyFromUtf8((String)ref);
          this.schema_ = b;
          return b;
        } 
        return (ByteString)ref;
      }
      
      public Builder setSchema(String value) {
        if (value == null)
          throw new NullPointerException(); 
        this.bitField0_ |= 0x2;
        this.schema_ = value;
        onChanged();
        return this;
      }
      
      public Builder clearSchema() {
        this.bitField0_ &= 0xFFFFFFFD;
        this.schema_ = MysqlxCrud.Collection.getDefaultInstance().getSchema();
        onChanged();
        return this;
      }
      
      public Builder setSchemaBytes(ByteString value) {
        if (value == null)
          throw new NullPointerException(); 
        this.bitField0_ |= 0x2;
        this.schema_ = value;
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
    
    private static final Collection DEFAULT_INSTANCE = new Collection();
    
    public static Collection getDefaultInstance() {
      return DEFAULT_INSTANCE;
    }
    
    @Deprecated
    public static final Parser<Collection> PARSER = (Parser<Collection>)new AbstractParser<Collection>() {
        public MysqlxCrud.Collection parsePartialFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
          MysqlxCrud.Collection.Builder builder = MysqlxCrud.Collection.newBuilder();
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
    
    public static Parser<Collection> parser() {
      return PARSER;
    }
    
    public Parser<Collection> getParserForType() {
      return PARSER;
    }
    
    public Collection getDefaultInstanceForType() {
      return DEFAULT_INSTANCE;
    }
  }
  
  public static interface LimitOrBuilder extends MessageOrBuilder {
    boolean hasRowCount();
    
    long getRowCount();
    
    boolean hasOffset();
    
    long getOffset();
  }
  
  public static final class Limit extends GeneratedMessageV3 implements LimitOrBuilder {
    private static final long serialVersionUID = 0L;
    
    private int bitField0_;
    
    public static final int ROW_COUNT_FIELD_NUMBER = 1;
    
    private long rowCount_;
    
    public static final int OFFSET_FIELD_NUMBER = 2;
    
    private long offset_;
    
    private byte memoizedIsInitialized;
    
    private Limit(GeneratedMessageV3.Builder<?> builder) {
      super(builder);
      this.memoizedIsInitialized = -1;
    }
    
    private Limit() {
      this.memoizedIsInitialized = -1;
    }
    
    protected Object newInstance(GeneratedMessageV3.UnusedPrivateParameter unused) {
      return new Limit();
    }
    
    public final UnknownFieldSet getUnknownFields() {
      return this.unknownFields;
    }
    
    public static final Descriptors.Descriptor getDescriptor() {
      return MysqlxCrud.internal_static_Mysqlx_Crud_Limit_descriptor;
    }
    
    protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() {
      return MysqlxCrud.internal_static_Mysqlx_Crud_Limit_fieldAccessorTable.ensureFieldAccessorsInitialized(Limit.class, Builder.class);
    }
    
    public boolean hasRowCount() {
      return ((this.bitField0_ & 0x1) != 0);
    }
    
    public long getRowCount() {
      return this.rowCount_;
    }
    
    public boolean hasOffset() {
      return ((this.bitField0_ & 0x2) != 0);
    }
    
    public long getOffset() {
      return this.offset_;
    }
    
    public final boolean isInitialized() {
      byte isInitialized = this.memoizedIsInitialized;
      if (isInitialized == 1)
        return true; 
      if (isInitialized == 0)
        return false; 
      if (!hasRowCount()) {
        this.memoizedIsInitialized = 0;
        return false;
      } 
      this.memoizedIsInitialized = 1;
      return true;
    }
    
    public void writeTo(CodedOutputStream output) throws IOException {
      if ((this.bitField0_ & 0x1) != 0)
        output.writeUInt64(1, this.rowCount_); 
      if ((this.bitField0_ & 0x2) != 0)
        output.writeUInt64(2, this.offset_); 
      getUnknownFields().writeTo(output);
    }
    
    public int getSerializedSize() {
      int size = this.memoizedSize;
      if (size != -1)
        return size; 
      size = 0;
      if ((this.bitField0_ & 0x1) != 0)
        size += 
          CodedOutputStream.computeUInt64Size(1, this.rowCount_); 
      if ((this.bitField0_ & 0x2) != 0)
        size += 
          CodedOutputStream.computeUInt64Size(2, this.offset_); 
      size += getUnknownFields().getSerializedSize();
      this.memoizedSize = size;
      return size;
    }
    
    public boolean equals(Object obj) {
      if (obj == this)
        return true; 
      if (!(obj instanceof Limit))
        return super.equals(obj); 
      Limit other = (Limit)obj;
      if (hasRowCount() != other.hasRowCount())
        return false; 
      if (hasRowCount() && 
        getRowCount() != other
        .getRowCount())
        return false; 
      if (hasOffset() != other.hasOffset())
        return false; 
      if (hasOffset() && 
        getOffset() != other
        .getOffset())
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
      if (hasRowCount()) {
        hash = 37 * hash + 1;
        hash = 53 * hash + Internal.hashLong(
            getRowCount());
      } 
      if (hasOffset()) {
        hash = 37 * hash + 2;
        hash = 53 * hash + Internal.hashLong(
            getOffset());
      } 
      hash = 29 * hash + getUnknownFields().hashCode();
      this.memoizedHashCode = hash;
      return hash;
    }
    
    public static Limit parseFrom(ByteBuffer data) throws InvalidProtocolBufferException {
      return (Limit)PARSER.parseFrom(data);
    }
    
    public static Limit parseFrom(ByteBuffer data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
      return (Limit)PARSER.parseFrom(data, extensionRegistry);
    }
    
    public static Limit parseFrom(ByteString data) throws InvalidProtocolBufferException {
      return (Limit)PARSER.parseFrom(data);
    }
    
    public static Limit parseFrom(ByteString data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
      return (Limit)PARSER.parseFrom(data, extensionRegistry);
    }
    
    public static Limit parseFrom(byte[] data) throws InvalidProtocolBufferException {
      return (Limit)PARSER.parseFrom(data);
    }
    
    public static Limit parseFrom(byte[] data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
      return (Limit)PARSER.parseFrom(data, extensionRegistry);
    }
    
    public static Limit parseFrom(InputStream input) throws IOException {
      return 
        (Limit)GeneratedMessageV3.parseWithIOException(PARSER, input);
    }
    
    public static Limit parseFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
      return 
        (Limit)GeneratedMessageV3.parseWithIOException(PARSER, input, extensionRegistry);
    }
    
    public static Limit parseDelimitedFrom(InputStream input) throws IOException {
      return 
        (Limit)GeneratedMessageV3.parseDelimitedWithIOException(PARSER, input);
    }
    
    public static Limit parseDelimitedFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
      return 
        (Limit)GeneratedMessageV3.parseDelimitedWithIOException(PARSER, input, extensionRegistry);
    }
    
    public static Limit parseFrom(CodedInputStream input) throws IOException {
      return 
        (Limit)GeneratedMessageV3.parseWithIOException(PARSER, input);
    }
    
    public static Limit parseFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
      return 
        (Limit)GeneratedMessageV3.parseWithIOException(PARSER, input, extensionRegistry);
    }
    
    public Builder newBuilderForType() {
      return newBuilder();
    }
    
    public static Builder newBuilder() {
      return DEFAULT_INSTANCE.toBuilder();
    }
    
    public static Builder newBuilder(Limit prototype) {
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
    
    public static final class Builder extends GeneratedMessageV3.Builder<Builder> implements MysqlxCrud.LimitOrBuilder {
      private int bitField0_;
      
      private long rowCount_;
      
      private long offset_;
      
      public static final Descriptors.Descriptor getDescriptor() {
        return MysqlxCrud.internal_static_Mysqlx_Crud_Limit_descriptor;
      }
      
      protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() {
        return MysqlxCrud.internal_static_Mysqlx_Crud_Limit_fieldAccessorTable
          .ensureFieldAccessorsInitialized(MysqlxCrud.Limit.class, Builder.class);
      }
      
      private Builder() {}
      
      private Builder(GeneratedMessageV3.BuilderParent parent) {
        super(parent);
      }
      
      public Builder clear() {
        super.clear();
        this.rowCount_ = 0L;
        this.bitField0_ &= 0xFFFFFFFE;
        this.offset_ = 0L;
        this.bitField0_ &= 0xFFFFFFFD;
        return this;
      }
      
      public Descriptors.Descriptor getDescriptorForType() {
        return MysqlxCrud.internal_static_Mysqlx_Crud_Limit_descriptor;
      }
      
      public MysqlxCrud.Limit getDefaultInstanceForType() {
        return MysqlxCrud.Limit.getDefaultInstance();
      }
      
      public MysqlxCrud.Limit build() {
        MysqlxCrud.Limit result = buildPartial();
        if (!result.isInitialized())
          throw newUninitializedMessageException(result); 
        return result;
      }
      
      public MysqlxCrud.Limit buildPartial() {
        MysqlxCrud.Limit result = new MysqlxCrud.Limit(this);
        int from_bitField0_ = this.bitField0_;
        int to_bitField0_ = 0;
        if ((from_bitField0_ & 0x1) != 0) {
          result.rowCount_ = this.rowCount_;
          to_bitField0_ |= 0x1;
        } 
        if ((from_bitField0_ & 0x2) != 0) {
          result.offset_ = this.offset_;
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
        if (other instanceof MysqlxCrud.Limit)
          return mergeFrom((MysqlxCrud.Limit)other); 
        super.mergeFrom(other);
        return this;
      }
      
      public Builder mergeFrom(MysqlxCrud.Limit other) {
        if (other == MysqlxCrud.Limit.getDefaultInstance())
          return this; 
        if (other.hasRowCount())
          setRowCount(other.getRowCount()); 
        if (other.hasOffset())
          setOffset(other.getOffset()); 
        mergeUnknownFields(other.getUnknownFields());
        onChanged();
        return this;
      }
      
      public final boolean isInitialized() {
        if (!hasRowCount())
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
                this.rowCount_ = input.readUInt64();
                this.bitField0_ |= 0x1;
                continue;
              case 16:
                this.offset_ = input.readUInt64();
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
      
      public boolean hasRowCount() {
        return ((this.bitField0_ & 0x1) != 0);
      }
      
      public long getRowCount() {
        return this.rowCount_;
      }
      
      public Builder setRowCount(long value) {
        this.bitField0_ |= 0x1;
        this.rowCount_ = value;
        onChanged();
        return this;
      }
      
      public Builder clearRowCount() {
        this.bitField0_ &= 0xFFFFFFFE;
        this.rowCount_ = 0L;
        onChanged();
        return this;
      }
      
      public boolean hasOffset() {
        return ((this.bitField0_ & 0x2) != 0);
      }
      
      public long getOffset() {
        return this.offset_;
      }
      
      public Builder setOffset(long value) {
        this.bitField0_ |= 0x2;
        this.offset_ = value;
        onChanged();
        return this;
      }
      
      public Builder clearOffset() {
        this.bitField0_ &= 0xFFFFFFFD;
        this.offset_ = 0L;
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
    
    private static final Limit DEFAULT_INSTANCE = new Limit();
    
    public static Limit getDefaultInstance() {
      return DEFAULT_INSTANCE;
    }
    
    @Deprecated
    public static final Parser<Limit> PARSER = (Parser<Limit>)new AbstractParser<Limit>() {
        public MysqlxCrud.Limit parsePartialFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
          MysqlxCrud.Limit.Builder builder = MysqlxCrud.Limit.newBuilder();
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
    
    public static Parser<Limit> parser() {
      return PARSER;
    }
    
    public Parser<Limit> getParserForType() {
      return PARSER;
    }
    
    public Limit getDefaultInstanceForType() {
      return DEFAULT_INSTANCE;
    }
  }
  
  public static interface LimitExprOrBuilder extends MessageOrBuilder {
    boolean hasRowCount();
    
    MysqlxExpr.Expr getRowCount();
    
    MysqlxExpr.ExprOrBuilder getRowCountOrBuilder();
    
    boolean hasOffset();
    
    MysqlxExpr.Expr getOffset();
    
    MysqlxExpr.ExprOrBuilder getOffsetOrBuilder();
  }
  
  public static final class LimitExpr extends GeneratedMessageV3 implements LimitExprOrBuilder {
    private static final long serialVersionUID = 0L;
    
    private int bitField0_;
    
    public static final int ROW_COUNT_FIELD_NUMBER = 1;
    
    private MysqlxExpr.Expr rowCount_;
    
    public static final int OFFSET_FIELD_NUMBER = 2;
    
    private MysqlxExpr.Expr offset_;
    
    private byte memoizedIsInitialized;
    
    private LimitExpr(GeneratedMessageV3.Builder<?> builder) {
      super(builder);
      this.memoizedIsInitialized = -1;
    }
    
    private LimitExpr() {
      this.memoizedIsInitialized = -1;
    }
    
    protected Object newInstance(GeneratedMessageV3.UnusedPrivateParameter unused) {
      return new LimitExpr();
    }
    
    public final UnknownFieldSet getUnknownFields() {
      return this.unknownFields;
    }
    
    public static final Descriptors.Descriptor getDescriptor() {
      return MysqlxCrud.internal_static_Mysqlx_Crud_LimitExpr_descriptor;
    }
    
    protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() {
      return MysqlxCrud.internal_static_Mysqlx_Crud_LimitExpr_fieldAccessorTable.ensureFieldAccessorsInitialized(LimitExpr.class, Builder.class);
    }
    
    public boolean hasRowCount() {
      return ((this.bitField0_ & 0x1) != 0);
    }
    
    public MysqlxExpr.Expr getRowCount() {
      return (this.rowCount_ == null) ? MysqlxExpr.Expr.getDefaultInstance() : this.rowCount_;
    }
    
    public MysqlxExpr.ExprOrBuilder getRowCountOrBuilder() {
      return (this.rowCount_ == null) ? MysqlxExpr.Expr.getDefaultInstance() : this.rowCount_;
    }
    
    public boolean hasOffset() {
      return ((this.bitField0_ & 0x2) != 0);
    }
    
    public MysqlxExpr.Expr getOffset() {
      return (this.offset_ == null) ? MysqlxExpr.Expr.getDefaultInstance() : this.offset_;
    }
    
    public MysqlxExpr.ExprOrBuilder getOffsetOrBuilder() {
      return (this.offset_ == null) ? MysqlxExpr.Expr.getDefaultInstance() : this.offset_;
    }
    
    public final boolean isInitialized() {
      byte isInitialized = this.memoizedIsInitialized;
      if (isInitialized == 1)
        return true; 
      if (isInitialized == 0)
        return false; 
      if (!hasRowCount()) {
        this.memoizedIsInitialized = 0;
        return false;
      } 
      if (!getRowCount().isInitialized()) {
        this.memoizedIsInitialized = 0;
        return false;
      } 
      if (hasOffset() && 
        !getOffset().isInitialized()) {
        this.memoizedIsInitialized = 0;
        return false;
      } 
      this.memoizedIsInitialized = 1;
      return true;
    }
    
    public void writeTo(CodedOutputStream output) throws IOException {
      if ((this.bitField0_ & 0x1) != 0)
        output.writeMessage(1, (MessageLite)getRowCount()); 
      if ((this.bitField0_ & 0x2) != 0)
        output.writeMessage(2, (MessageLite)getOffset()); 
      getUnknownFields().writeTo(output);
    }
    
    public int getSerializedSize() {
      int size = this.memoizedSize;
      if (size != -1)
        return size; 
      size = 0;
      if ((this.bitField0_ & 0x1) != 0)
        size += 
          CodedOutputStream.computeMessageSize(1, (MessageLite)getRowCount()); 
      if ((this.bitField0_ & 0x2) != 0)
        size += 
          CodedOutputStream.computeMessageSize(2, (MessageLite)getOffset()); 
      size += getUnknownFields().getSerializedSize();
      this.memoizedSize = size;
      return size;
    }
    
    public boolean equals(Object obj) {
      if (obj == this)
        return true; 
      if (!(obj instanceof LimitExpr))
        return super.equals(obj); 
      LimitExpr other = (LimitExpr)obj;
      if (hasRowCount() != other.hasRowCount())
        return false; 
      if (hasRowCount() && 
        
        !getRowCount().equals(other.getRowCount()))
        return false; 
      if (hasOffset() != other.hasOffset())
        return false; 
      if (hasOffset() && 
        
        !getOffset().equals(other.getOffset()))
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
      if (hasRowCount()) {
        hash = 37 * hash + 1;
        hash = 53 * hash + getRowCount().hashCode();
      } 
      if (hasOffset()) {
        hash = 37 * hash + 2;
        hash = 53 * hash + getOffset().hashCode();
      } 
      hash = 29 * hash + getUnknownFields().hashCode();
      this.memoizedHashCode = hash;
      return hash;
    }
    
    public static LimitExpr parseFrom(ByteBuffer data) throws InvalidProtocolBufferException {
      return (LimitExpr)PARSER.parseFrom(data);
    }
    
    public static LimitExpr parseFrom(ByteBuffer data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
      return (LimitExpr)PARSER.parseFrom(data, extensionRegistry);
    }
    
    public static LimitExpr parseFrom(ByteString data) throws InvalidProtocolBufferException {
      return (LimitExpr)PARSER.parseFrom(data);
    }
    
    public static LimitExpr parseFrom(ByteString data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
      return (LimitExpr)PARSER.parseFrom(data, extensionRegistry);
    }
    
    public static LimitExpr parseFrom(byte[] data) throws InvalidProtocolBufferException {
      return (LimitExpr)PARSER.parseFrom(data);
    }
    
    public static LimitExpr parseFrom(byte[] data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
      return (LimitExpr)PARSER.parseFrom(data, extensionRegistry);
    }
    
    public static LimitExpr parseFrom(InputStream input) throws IOException {
      return 
        (LimitExpr)GeneratedMessageV3.parseWithIOException(PARSER, input);
    }
    
    public static LimitExpr parseFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
      return 
        (LimitExpr)GeneratedMessageV3.parseWithIOException(PARSER, input, extensionRegistry);
    }
    
    public static LimitExpr parseDelimitedFrom(InputStream input) throws IOException {
      return 
        (LimitExpr)GeneratedMessageV3.parseDelimitedWithIOException(PARSER, input);
    }
    
    public static LimitExpr parseDelimitedFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
      return 
        (LimitExpr)GeneratedMessageV3.parseDelimitedWithIOException(PARSER, input, extensionRegistry);
    }
    
    public static LimitExpr parseFrom(CodedInputStream input) throws IOException {
      return 
        (LimitExpr)GeneratedMessageV3.parseWithIOException(PARSER, input);
    }
    
    public static LimitExpr parseFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
      return 
        (LimitExpr)GeneratedMessageV3.parseWithIOException(PARSER, input, extensionRegistry);
    }
    
    public Builder newBuilderForType() {
      return newBuilder();
    }
    
    public static Builder newBuilder() {
      return DEFAULT_INSTANCE.toBuilder();
    }
    
    public static Builder newBuilder(LimitExpr prototype) {
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
    
    public static final class Builder extends GeneratedMessageV3.Builder<Builder> implements MysqlxCrud.LimitExprOrBuilder {
      private int bitField0_;
      
      private MysqlxExpr.Expr rowCount_;
      
      private SingleFieldBuilderV3<MysqlxExpr.Expr, MysqlxExpr.Expr.Builder, MysqlxExpr.ExprOrBuilder> rowCountBuilder_;
      
      private MysqlxExpr.Expr offset_;
      
      private SingleFieldBuilderV3<MysqlxExpr.Expr, MysqlxExpr.Expr.Builder, MysqlxExpr.ExprOrBuilder> offsetBuilder_;
      
      public static final Descriptors.Descriptor getDescriptor() {
        return MysqlxCrud.internal_static_Mysqlx_Crud_LimitExpr_descriptor;
      }
      
      protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() {
        return MysqlxCrud.internal_static_Mysqlx_Crud_LimitExpr_fieldAccessorTable
          .ensureFieldAccessorsInitialized(MysqlxCrud.LimitExpr.class, Builder.class);
      }
      
      private Builder() {
        maybeForceBuilderInitialization();
      }
      
      private Builder(GeneratedMessageV3.BuilderParent parent) {
        super(parent);
        maybeForceBuilderInitialization();
      }
      
      private void maybeForceBuilderInitialization() {
        if (MysqlxCrud.LimitExpr.alwaysUseFieldBuilders) {
          getRowCountFieldBuilder();
          getOffsetFieldBuilder();
        } 
      }
      
      public Builder clear() {
        super.clear();
        if (this.rowCountBuilder_ == null) {
          this.rowCount_ = null;
        } else {
          this.rowCountBuilder_.clear();
        } 
        this.bitField0_ &= 0xFFFFFFFE;
        if (this.offsetBuilder_ == null) {
          this.offset_ = null;
        } else {
          this.offsetBuilder_.clear();
        } 
        this.bitField0_ &= 0xFFFFFFFD;
        return this;
      }
      
      public Descriptors.Descriptor getDescriptorForType() {
        return MysqlxCrud.internal_static_Mysqlx_Crud_LimitExpr_descriptor;
      }
      
      public MysqlxCrud.LimitExpr getDefaultInstanceForType() {
        return MysqlxCrud.LimitExpr.getDefaultInstance();
      }
      
      public MysqlxCrud.LimitExpr build() {
        MysqlxCrud.LimitExpr result = buildPartial();
        if (!result.isInitialized())
          throw newUninitializedMessageException(result); 
        return result;
      }
      
      public MysqlxCrud.LimitExpr buildPartial() {
        MysqlxCrud.LimitExpr result = new MysqlxCrud.LimitExpr(this);
        int from_bitField0_ = this.bitField0_;
        int to_bitField0_ = 0;
        if ((from_bitField0_ & 0x1) != 0) {
          if (this.rowCountBuilder_ == null) {
            result.rowCount_ = this.rowCount_;
          } else {
            result.rowCount_ = (MysqlxExpr.Expr)this.rowCountBuilder_.build();
          } 
          to_bitField0_ |= 0x1;
        } 
        if ((from_bitField0_ & 0x2) != 0) {
          if (this.offsetBuilder_ == null) {
            result.offset_ = this.offset_;
          } else {
            result.offset_ = (MysqlxExpr.Expr)this.offsetBuilder_.build();
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
        if (other instanceof MysqlxCrud.LimitExpr)
          return mergeFrom((MysqlxCrud.LimitExpr)other); 
        super.mergeFrom(other);
        return this;
      }
      
      public Builder mergeFrom(MysqlxCrud.LimitExpr other) {
        if (other == MysqlxCrud.LimitExpr.getDefaultInstance())
          return this; 
        if (other.hasRowCount())
          mergeRowCount(other.getRowCount()); 
        if (other.hasOffset())
          mergeOffset(other.getOffset()); 
        mergeUnknownFields(other.getUnknownFields());
        onChanged();
        return this;
      }
      
      public final boolean isInitialized() {
        if (!hasRowCount())
          return false; 
        if (!getRowCount().isInitialized())
          return false; 
        if (hasOffset() && 
          !getOffset().isInitialized())
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
                input.readMessage((MessageLite.Builder)
                    getRowCountFieldBuilder().getBuilder(), extensionRegistry);
                this.bitField0_ |= 0x1;
                continue;
              case 18:
                input.readMessage((MessageLite.Builder)
                    getOffsetFieldBuilder().getBuilder(), extensionRegistry);
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
      
      public boolean hasRowCount() {
        return ((this.bitField0_ & 0x1) != 0);
      }
      
      public MysqlxExpr.Expr getRowCount() {
        if (this.rowCountBuilder_ == null)
          return (this.rowCount_ == null) ? MysqlxExpr.Expr.getDefaultInstance() : this.rowCount_; 
        return (MysqlxExpr.Expr)this.rowCountBuilder_.getMessage();
      }
      
      public Builder setRowCount(MysqlxExpr.Expr value) {
        if (this.rowCountBuilder_ == null) {
          if (value == null)
            throw new NullPointerException(); 
          this.rowCount_ = value;
          onChanged();
        } else {
          this.rowCountBuilder_.setMessage((AbstractMessage)value);
        } 
        this.bitField0_ |= 0x1;
        return this;
      }
      
      public Builder setRowCount(MysqlxExpr.Expr.Builder builderForValue) {
        if (this.rowCountBuilder_ == null) {
          this.rowCount_ = builderForValue.build();
          onChanged();
        } else {
          this.rowCountBuilder_.setMessage((AbstractMessage)builderForValue.build());
        } 
        this.bitField0_ |= 0x1;
        return this;
      }
      
      public Builder mergeRowCount(MysqlxExpr.Expr value) {
        if (this.rowCountBuilder_ == null) {
          if ((this.bitField0_ & 0x1) != 0 && this.rowCount_ != null && this.rowCount_ != 
            
            MysqlxExpr.Expr.getDefaultInstance()) {
            this
              .rowCount_ = MysqlxExpr.Expr.newBuilder(this.rowCount_).mergeFrom(value).buildPartial();
          } else {
            this.rowCount_ = value;
          } 
          onChanged();
        } else {
          this.rowCountBuilder_.mergeFrom((AbstractMessage)value);
        } 
        this.bitField0_ |= 0x1;
        return this;
      }
      
      public Builder clearRowCount() {
        if (this.rowCountBuilder_ == null) {
          this.rowCount_ = null;
          onChanged();
        } else {
          this.rowCountBuilder_.clear();
        } 
        this.bitField0_ &= 0xFFFFFFFE;
        return this;
      }
      
      public MysqlxExpr.Expr.Builder getRowCountBuilder() {
        this.bitField0_ |= 0x1;
        onChanged();
        return (MysqlxExpr.Expr.Builder)getRowCountFieldBuilder().getBuilder();
      }
      
      public MysqlxExpr.ExprOrBuilder getRowCountOrBuilder() {
        if (this.rowCountBuilder_ != null)
          return (MysqlxExpr.ExprOrBuilder)this.rowCountBuilder_.getMessageOrBuilder(); 
        return (this.rowCount_ == null) ? 
          MysqlxExpr.Expr.getDefaultInstance() : this.rowCount_;
      }
      
      private SingleFieldBuilderV3<MysqlxExpr.Expr, MysqlxExpr.Expr.Builder, MysqlxExpr.ExprOrBuilder> getRowCountFieldBuilder() {
        if (this.rowCountBuilder_ == null) {
          this
            
            .rowCountBuilder_ = new SingleFieldBuilderV3((AbstractMessage)getRowCount(), (AbstractMessage.BuilderParent)getParentForChildren(), isClean());
          this.rowCount_ = null;
        } 
        return this.rowCountBuilder_;
      }
      
      public boolean hasOffset() {
        return ((this.bitField0_ & 0x2) != 0);
      }
      
      public MysqlxExpr.Expr getOffset() {
        if (this.offsetBuilder_ == null)
          return (this.offset_ == null) ? MysqlxExpr.Expr.getDefaultInstance() : this.offset_; 
        return (MysqlxExpr.Expr)this.offsetBuilder_.getMessage();
      }
      
      public Builder setOffset(MysqlxExpr.Expr value) {
        if (this.offsetBuilder_ == null) {
          if (value == null)
            throw new NullPointerException(); 
          this.offset_ = value;
          onChanged();
        } else {
          this.offsetBuilder_.setMessage((AbstractMessage)value);
        } 
        this.bitField0_ |= 0x2;
        return this;
      }
      
      public Builder setOffset(MysqlxExpr.Expr.Builder builderForValue) {
        if (this.offsetBuilder_ == null) {
          this.offset_ = builderForValue.build();
          onChanged();
        } else {
          this.offsetBuilder_.setMessage((AbstractMessage)builderForValue.build());
        } 
        this.bitField0_ |= 0x2;
        return this;
      }
      
      public Builder mergeOffset(MysqlxExpr.Expr value) {
        if (this.offsetBuilder_ == null) {
          if ((this.bitField0_ & 0x2) != 0 && this.offset_ != null && this.offset_ != 
            
            MysqlxExpr.Expr.getDefaultInstance()) {
            this
              .offset_ = MysqlxExpr.Expr.newBuilder(this.offset_).mergeFrom(value).buildPartial();
          } else {
            this.offset_ = value;
          } 
          onChanged();
        } else {
          this.offsetBuilder_.mergeFrom((AbstractMessage)value);
        } 
        this.bitField0_ |= 0x2;
        return this;
      }
      
      public Builder clearOffset() {
        if (this.offsetBuilder_ == null) {
          this.offset_ = null;
          onChanged();
        } else {
          this.offsetBuilder_.clear();
        } 
        this.bitField0_ &= 0xFFFFFFFD;
        return this;
      }
      
      public MysqlxExpr.Expr.Builder getOffsetBuilder() {
        this.bitField0_ |= 0x2;
        onChanged();
        return (MysqlxExpr.Expr.Builder)getOffsetFieldBuilder().getBuilder();
      }
      
      public MysqlxExpr.ExprOrBuilder getOffsetOrBuilder() {
        if (this.offsetBuilder_ != null)
          return (MysqlxExpr.ExprOrBuilder)this.offsetBuilder_.getMessageOrBuilder(); 
        return (this.offset_ == null) ? 
          MysqlxExpr.Expr.getDefaultInstance() : this.offset_;
      }
      
      private SingleFieldBuilderV3<MysqlxExpr.Expr, MysqlxExpr.Expr.Builder, MysqlxExpr.ExprOrBuilder> getOffsetFieldBuilder() {
        if (this.offsetBuilder_ == null) {
          this
            
            .offsetBuilder_ = new SingleFieldBuilderV3((AbstractMessage)getOffset(), (AbstractMessage.BuilderParent)getParentForChildren(), isClean());
          this.offset_ = null;
        } 
        return this.offsetBuilder_;
      }
      
      public final Builder setUnknownFields(UnknownFieldSet unknownFields) {
        return (Builder)super.setUnknownFields(unknownFields);
      }
      
      public final Builder mergeUnknownFields(UnknownFieldSet unknownFields) {
        return (Builder)super.mergeUnknownFields(unknownFields);
      }
    }
    
    private static final LimitExpr DEFAULT_INSTANCE = new LimitExpr();
    
    public static LimitExpr getDefaultInstance() {
      return DEFAULT_INSTANCE;
    }
    
    @Deprecated
    public static final Parser<LimitExpr> PARSER = (Parser<LimitExpr>)new AbstractParser<LimitExpr>() {
        public MysqlxCrud.LimitExpr parsePartialFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
          MysqlxCrud.LimitExpr.Builder builder = MysqlxCrud.LimitExpr.newBuilder();
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
    
    public static Parser<LimitExpr> parser() {
      return PARSER;
    }
    
    public Parser<LimitExpr> getParserForType() {
      return PARSER;
    }
    
    public LimitExpr getDefaultInstanceForType() {
      return DEFAULT_INSTANCE;
    }
  }
  
  public static interface OrderOrBuilder extends MessageOrBuilder {
    boolean hasExpr();
    
    MysqlxExpr.Expr getExpr();
    
    MysqlxExpr.ExprOrBuilder getExprOrBuilder();
    
    boolean hasDirection();
    
    MysqlxCrud.Order.Direction getDirection();
  }
  
  public static final class Order extends GeneratedMessageV3 implements OrderOrBuilder {
    private static final long serialVersionUID = 0L;
    
    private int bitField0_;
    
    public static final int EXPR_FIELD_NUMBER = 1;
    
    private MysqlxExpr.Expr expr_;
    
    public static final int DIRECTION_FIELD_NUMBER = 2;
    
    private int direction_;
    
    private byte memoizedIsInitialized;
    
    private Order(GeneratedMessageV3.Builder<?> builder) {
      super(builder);
      this.memoizedIsInitialized = -1;
    }
    
    private Order() {
      this.memoizedIsInitialized = -1;
      this.direction_ = 1;
    }
    
    protected Object newInstance(GeneratedMessageV3.UnusedPrivateParameter unused) {
      return new Order();
    }
    
    public final UnknownFieldSet getUnknownFields() {
      return this.unknownFields;
    }
    
    public static final Descriptors.Descriptor getDescriptor() {
      return MysqlxCrud.internal_static_Mysqlx_Crud_Order_descriptor;
    }
    
    protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() {
      return MysqlxCrud.internal_static_Mysqlx_Crud_Order_fieldAccessorTable.ensureFieldAccessorsInitialized(Order.class, Builder.class);
    }
    
    public enum Direction implements ProtocolMessageEnum {
      ASC(1),
      DESC(2);
      
      public static final int ASC_VALUE = 1;
      
      public static final int DESC_VALUE = 2;
      
      private static final Internal.EnumLiteMap<Direction> internalValueMap = new Internal.EnumLiteMap<Direction>() {
          public MysqlxCrud.Order.Direction findValueByNumber(int number) {
            return MysqlxCrud.Order.Direction.forNumber(number);
          }
        };
      
      private static final Direction[] VALUES = values();
      
      private final int value;
      
      public final int getNumber() {
        return this.value;
      }
      
      public static Direction forNumber(int value) {
        switch (value) {
          case 1:
            return ASC;
          case 2:
            return DESC;
        } 
        return null;
      }
      
      public static Internal.EnumLiteMap<Direction> internalGetValueMap() {
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
        return MysqlxCrud.Order.getDescriptor().getEnumTypes().get(0);
      }
      
      Direction(int value) {
        this.value = value;
      }
    }
    
    public boolean hasExpr() {
      return ((this.bitField0_ & 0x1) != 0);
    }
    
    public MysqlxExpr.Expr getExpr() {
      return (this.expr_ == null) ? MysqlxExpr.Expr.getDefaultInstance() : this.expr_;
    }
    
    public MysqlxExpr.ExprOrBuilder getExprOrBuilder() {
      return (this.expr_ == null) ? MysqlxExpr.Expr.getDefaultInstance() : this.expr_;
    }
    
    public boolean hasDirection() {
      return ((this.bitField0_ & 0x2) != 0);
    }
    
    public Direction getDirection() {
      Direction result = Direction.valueOf(this.direction_);
      return (result == null) ? Direction.ASC : result;
    }
    
    public final boolean isInitialized() {
      byte isInitialized = this.memoizedIsInitialized;
      if (isInitialized == 1)
        return true; 
      if (isInitialized == 0)
        return false; 
      if (!hasExpr()) {
        this.memoizedIsInitialized = 0;
        return false;
      } 
      if (!getExpr().isInitialized()) {
        this.memoizedIsInitialized = 0;
        return false;
      } 
      this.memoizedIsInitialized = 1;
      return true;
    }
    
    public void writeTo(CodedOutputStream output) throws IOException {
      if ((this.bitField0_ & 0x1) != 0)
        output.writeMessage(1, (MessageLite)getExpr()); 
      if ((this.bitField0_ & 0x2) != 0)
        output.writeEnum(2, this.direction_); 
      getUnknownFields().writeTo(output);
    }
    
    public int getSerializedSize() {
      int size = this.memoizedSize;
      if (size != -1)
        return size; 
      size = 0;
      if ((this.bitField0_ & 0x1) != 0)
        size += 
          CodedOutputStream.computeMessageSize(1, (MessageLite)getExpr()); 
      if ((this.bitField0_ & 0x2) != 0)
        size += 
          CodedOutputStream.computeEnumSize(2, this.direction_); 
      size += getUnknownFields().getSerializedSize();
      this.memoizedSize = size;
      return size;
    }
    
    public boolean equals(Object obj) {
      if (obj == this)
        return true; 
      if (!(obj instanceof Order))
        return super.equals(obj); 
      Order other = (Order)obj;
      if (hasExpr() != other.hasExpr())
        return false; 
      if (hasExpr() && 
        
        !getExpr().equals(other.getExpr()))
        return false; 
      if (hasDirection() != other.hasDirection())
        return false; 
      if (hasDirection() && 
        this.direction_ != other.direction_)
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
      if (hasExpr()) {
        hash = 37 * hash + 1;
        hash = 53 * hash + getExpr().hashCode();
      } 
      if (hasDirection()) {
        hash = 37 * hash + 2;
        hash = 53 * hash + this.direction_;
      } 
      hash = 29 * hash + getUnknownFields().hashCode();
      this.memoizedHashCode = hash;
      return hash;
    }
    
    public static Order parseFrom(ByteBuffer data) throws InvalidProtocolBufferException {
      return (Order)PARSER.parseFrom(data);
    }
    
    public static Order parseFrom(ByteBuffer data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
      return (Order)PARSER.parseFrom(data, extensionRegistry);
    }
    
    public static Order parseFrom(ByteString data) throws InvalidProtocolBufferException {
      return (Order)PARSER.parseFrom(data);
    }
    
    public static Order parseFrom(ByteString data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
      return (Order)PARSER.parseFrom(data, extensionRegistry);
    }
    
    public static Order parseFrom(byte[] data) throws InvalidProtocolBufferException {
      return (Order)PARSER.parseFrom(data);
    }
    
    public static Order parseFrom(byte[] data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
      return (Order)PARSER.parseFrom(data, extensionRegistry);
    }
    
    public static Order parseFrom(InputStream input) throws IOException {
      return 
        (Order)GeneratedMessageV3.parseWithIOException(PARSER, input);
    }
    
    public static Order parseFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
      return 
        (Order)GeneratedMessageV3.parseWithIOException(PARSER, input, extensionRegistry);
    }
    
    public static Order parseDelimitedFrom(InputStream input) throws IOException {
      return 
        (Order)GeneratedMessageV3.parseDelimitedWithIOException(PARSER, input);
    }
    
    public static Order parseDelimitedFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
      return 
        (Order)GeneratedMessageV3.parseDelimitedWithIOException(PARSER, input, extensionRegistry);
    }
    
    public static Order parseFrom(CodedInputStream input) throws IOException {
      return 
        (Order)GeneratedMessageV3.parseWithIOException(PARSER, input);
    }
    
    public static Order parseFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
      return 
        (Order)GeneratedMessageV3.parseWithIOException(PARSER, input, extensionRegistry);
    }
    
    public Builder newBuilderForType() {
      return newBuilder();
    }
    
    public static Builder newBuilder() {
      return DEFAULT_INSTANCE.toBuilder();
    }
    
    public static Builder newBuilder(Order prototype) {
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
    
    public static final class Builder extends GeneratedMessageV3.Builder<Builder> implements MysqlxCrud.OrderOrBuilder {
      private int bitField0_;
      
      private MysqlxExpr.Expr expr_;
      
      private SingleFieldBuilderV3<MysqlxExpr.Expr, MysqlxExpr.Expr.Builder, MysqlxExpr.ExprOrBuilder> exprBuilder_;
      
      private int direction_;
      
      public static final Descriptors.Descriptor getDescriptor() {
        return MysqlxCrud.internal_static_Mysqlx_Crud_Order_descriptor;
      }
      
      protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() {
        return MysqlxCrud.internal_static_Mysqlx_Crud_Order_fieldAccessorTable
          .ensureFieldAccessorsInitialized(MysqlxCrud.Order.class, Builder.class);
      }
      
      private Builder() {
        this.direction_ = 1;
        maybeForceBuilderInitialization();
      }
      
      private Builder(GeneratedMessageV3.BuilderParent parent) {
        super(parent);
        this.direction_ = 1;
        maybeForceBuilderInitialization();
      }
      
      private void maybeForceBuilderInitialization() {
        if (MysqlxCrud.Order.alwaysUseFieldBuilders)
          getExprFieldBuilder(); 
      }
      
      public Builder clear() {
        super.clear();
        if (this.exprBuilder_ == null) {
          this.expr_ = null;
        } else {
          this.exprBuilder_.clear();
        } 
        this.bitField0_ &= 0xFFFFFFFE;
        this.direction_ = 1;
        this.bitField0_ &= 0xFFFFFFFD;
        return this;
      }
      
      public Descriptors.Descriptor getDescriptorForType() {
        return MysqlxCrud.internal_static_Mysqlx_Crud_Order_descriptor;
      }
      
      public MysqlxCrud.Order getDefaultInstanceForType() {
        return MysqlxCrud.Order.getDefaultInstance();
      }
      
      public MysqlxCrud.Order build() {
        MysqlxCrud.Order result = buildPartial();
        if (!result.isInitialized())
          throw newUninitializedMessageException(result); 
        return result;
      }
      
      public MysqlxCrud.Order buildPartial() {
        MysqlxCrud.Order result = new MysqlxCrud.Order(this);
        int from_bitField0_ = this.bitField0_;
        int to_bitField0_ = 0;
        if ((from_bitField0_ & 0x1) != 0) {
          if (this.exprBuilder_ == null) {
            result.expr_ = this.expr_;
          } else {
            result.expr_ = (MysqlxExpr.Expr)this.exprBuilder_.build();
          } 
          to_bitField0_ |= 0x1;
        } 
        if ((from_bitField0_ & 0x2) != 0)
          to_bitField0_ |= 0x2; 
        result.direction_ = this.direction_;
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
        if (other instanceof MysqlxCrud.Order)
          return mergeFrom((MysqlxCrud.Order)other); 
        super.mergeFrom(other);
        return this;
      }
      
      public Builder mergeFrom(MysqlxCrud.Order other) {
        if (other == MysqlxCrud.Order.getDefaultInstance())
          return this; 
        if (other.hasExpr())
          mergeExpr(other.getExpr()); 
        if (other.hasDirection())
          setDirection(other.getDirection()); 
        mergeUnknownFields(other.getUnknownFields());
        onChanged();
        return this;
      }
      
      public final boolean isInitialized() {
        if (!hasExpr())
          return false; 
        if (!getExpr().isInitialized())
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
            MysqlxCrud.Order.Direction tmpValue;
            int tag = input.readTag();
            switch (tag) {
              case 0:
                done = true;
                continue;
              case 10:
                input.readMessage((MessageLite.Builder)getExprFieldBuilder().getBuilder(), extensionRegistry);
                this.bitField0_ |= 0x1;
                continue;
              case 16:
                tmpRaw = input.readEnum();
                tmpValue = MysqlxCrud.Order.Direction.forNumber(tmpRaw);
                if (tmpValue == null) {
                  mergeUnknownVarintField(2, tmpRaw);
                  continue;
                } 
                this.direction_ = tmpRaw;
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
      
      public boolean hasExpr() {
        return ((this.bitField0_ & 0x1) != 0);
      }
      
      public MysqlxExpr.Expr getExpr() {
        if (this.exprBuilder_ == null)
          return (this.expr_ == null) ? MysqlxExpr.Expr.getDefaultInstance() : this.expr_; 
        return (MysqlxExpr.Expr)this.exprBuilder_.getMessage();
      }
      
      public Builder setExpr(MysqlxExpr.Expr value) {
        if (this.exprBuilder_ == null) {
          if (value == null)
            throw new NullPointerException(); 
          this.expr_ = value;
          onChanged();
        } else {
          this.exprBuilder_.setMessage((AbstractMessage)value);
        } 
        this.bitField0_ |= 0x1;
        return this;
      }
      
      public Builder setExpr(MysqlxExpr.Expr.Builder builderForValue) {
        if (this.exprBuilder_ == null) {
          this.expr_ = builderForValue.build();
          onChanged();
        } else {
          this.exprBuilder_.setMessage((AbstractMessage)builderForValue.build());
        } 
        this.bitField0_ |= 0x1;
        return this;
      }
      
      public Builder mergeExpr(MysqlxExpr.Expr value) {
        if (this.exprBuilder_ == null) {
          if ((this.bitField0_ & 0x1) != 0 && this.expr_ != null && this.expr_ != MysqlxExpr.Expr.getDefaultInstance()) {
            this.expr_ = MysqlxExpr.Expr.newBuilder(this.expr_).mergeFrom(value).buildPartial();
          } else {
            this.expr_ = value;
          } 
          onChanged();
        } else {
          this.exprBuilder_.mergeFrom((AbstractMessage)value);
        } 
        this.bitField0_ |= 0x1;
        return this;
      }
      
      public Builder clearExpr() {
        if (this.exprBuilder_ == null) {
          this.expr_ = null;
          onChanged();
        } else {
          this.exprBuilder_.clear();
        } 
        this.bitField0_ &= 0xFFFFFFFE;
        return this;
      }
      
      public MysqlxExpr.Expr.Builder getExprBuilder() {
        this.bitField0_ |= 0x1;
        onChanged();
        return (MysqlxExpr.Expr.Builder)getExprFieldBuilder().getBuilder();
      }
      
      public MysqlxExpr.ExprOrBuilder getExprOrBuilder() {
        if (this.exprBuilder_ != null)
          return (MysqlxExpr.ExprOrBuilder)this.exprBuilder_.getMessageOrBuilder(); 
        return (this.expr_ == null) ? MysqlxExpr.Expr.getDefaultInstance() : this.expr_;
      }
      
      private SingleFieldBuilderV3<MysqlxExpr.Expr, MysqlxExpr.Expr.Builder, MysqlxExpr.ExprOrBuilder> getExprFieldBuilder() {
        if (this.exprBuilder_ == null) {
          this.exprBuilder_ = new SingleFieldBuilderV3((AbstractMessage)getExpr(), (AbstractMessage.BuilderParent)getParentForChildren(), isClean());
          this.expr_ = null;
        } 
        return this.exprBuilder_;
      }
      
      public boolean hasDirection() {
        return ((this.bitField0_ & 0x2) != 0);
      }
      
      public MysqlxCrud.Order.Direction getDirection() {
        MysqlxCrud.Order.Direction result = MysqlxCrud.Order.Direction.valueOf(this.direction_);
        return (result == null) ? MysqlxCrud.Order.Direction.ASC : result;
      }
      
      public Builder setDirection(MysqlxCrud.Order.Direction value) {
        if (value == null)
          throw new NullPointerException(); 
        this.bitField0_ |= 0x2;
        this.direction_ = value.getNumber();
        onChanged();
        return this;
      }
      
      public Builder clearDirection() {
        this.bitField0_ &= 0xFFFFFFFD;
        this.direction_ = 1;
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
    
    private static final Order DEFAULT_INSTANCE = new Order();
    
    public static Order getDefaultInstance() {
      return DEFAULT_INSTANCE;
    }
    
    @Deprecated
    public static final Parser<Order> PARSER = (Parser<Order>)new AbstractParser<Order>() {
        public MysqlxCrud.Order parsePartialFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
          MysqlxCrud.Order.Builder builder = MysqlxCrud.Order.newBuilder();
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
    
    public static Parser<Order> parser() {
      return PARSER;
    }
    
    public Parser<Order> getParserForType() {
      return PARSER;
    }
    
    public Order getDefaultInstanceForType() {
      return DEFAULT_INSTANCE;
    }
  }
  
  public static interface UpdateOperationOrBuilder extends MessageOrBuilder {
    boolean hasSource();
    
    MysqlxExpr.ColumnIdentifier getSource();
    
    MysqlxExpr.ColumnIdentifierOrBuilder getSourceOrBuilder();
    
    boolean hasOperation();
    
    MysqlxCrud.UpdateOperation.UpdateType getOperation();
    
    boolean hasValue();
    
    MysqlxExpr.Expr getValue();
    
    MysqlxExpr.ExprOrBuilder getValueOrBuilder();
  }
  
  public static final class UpdateOperation extends GeneratedMessageV3 implements UpdateOperationOrBuilder {
    private static final long serialVersionUID = 0L;
    
    private int bitField0_;
    
    public static final int SOURCE_FIELD_NUMBER = 1;
    
    private MysqlxExpr.ColumnIdentifier source_;
    
    public static final int OPERATION_FIELD_NUMBER = 2;
    
    private int operation_;
    
    public static final int VALUE_FIELD_NUMBER = 3;
    
    private MysqlxExpr.Expr value_;
    
    private byte memoizedIsInitialized;
    
    private UpdateOperation(GeneratedMessageV3.Builder<?> builder) {
      super(builder);
      this.memoizedIsInitialized = -1;
    }
    
    private UpdateOperation() {
      this.memoizedIsInitialized = -1;
      this.operation_ = 1;
    }
    
    protected Object newInstance(GeneratedMessageV3.UnusedPrivateParameter unused) {
      return new UpdateOperation();
    }
    
    public final UnknownFieldSet getUnknownFields() {
      return this.unknownFields;
    }
    
    public static final Descriptors.Descriptor getDescriptor() {
      return MysqlxCrud.internal_static_Mysqlx_Crud_UpdateOperation_descriptor;
    }
    
    protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() {
      return MysqlxCrud.internal_static_Mysqlx_Crud_UpdateOperation_fieldAccessorTable.ensureFieldAccessorsInitialized(UpdateOperation.class, Builder.class);
    }
    
    public enum UpdateType implements ProtocolMessageEnum {
      SET(1),
      ITEM_REMOVE(2),
      ITEM_SET(3),
      ITEM_REPLACE(4),
      ITEM_MERGE(5),
      ARRAY_INSERT(6),
      ARRAY_APPEND(7),
      MERGE_PATCH(8);
      
      public static final int SET_VALUE = 1;
      
      public static final int ITEM_REMOVE_VALUE = 2;
      
      public static final int ITEM_SET_VALUE = 3;
      
      public static final int ITEM_REPLACE_VALUE = 4;
      
      public static final int ITEM_MERGE_VALUE = 5;
      
      public static final int ARRAY_INSERT_VALUE = 6;
      
      public static final int ARRAY_APPEND_VALUE = 7;
      
      public static final int MERGE_PATCH_VALUE = 8;
      
      private static final Internal.EnumLiteMap<UpdateType> internalValueMap = new Internal.EnumLiteMap<UpdateType>() {
          public MysqlxCrud.UpdateOperation.UpdateType findValueByNumber(int number) {
            return MysqlxCrud.UpdateOperation.UpdateType.forNumber(number);
          }
        };
      
      private static final UpdateType[] VALUES = values();
      
      private final int value;
      
      public final int getNumber() {
        return this.value;
      }
      
      public static UpdateType forNumber(int value) {
        switch (value) {
          case 1:
            return SET;
          case 2:
            return ITEM_REMOVE;
          case 3:
            return ITEM_SET;
          case 4:
            return ITEM_REPLACE;
          case 5:
            return ITEM_MERGE;
          case 6:
            return ARRAY_INSERT;
          case 7:
            return ARRAY_APPEND;
          case 8:
            return MERGE_PATCH;
        } 
        return null;
      }
      
      public static Internal.EnumLiteMap<UpdateType> internalGetValueMap() {
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
        return MysqlxCrud.UpdateOperation.getDescriptor().getEnumTypes().get(0);
      }
      
      UpdateType(int value) {
        this.value = value;
      }
    }
    
    public boolean hasSource() {
      return ((this.bitField0_ & 0x1) != 0);
    }
    
    public MysqlxExpr.ColumnIdentifier getSource() {
      return (this.source_ == null) ? MysqlxExpr.ColumnIdentifier.getDefaultInstance() : this.source_;
    }
    
    public MysqlxExpr.ColumnIdentifierOrBuilder getSourceOrBuilder() {
      return (this.source_ == null) ? MysqlxExpr.ColumnIdentifier.getDefaultInstance() : this.source_;
    }
    
    public boolean hasOperation() {
      return ((this.bitField0_ & 0x2) != 0);
    }
    
    public UpdateType getOperation() {
      UpdateType result = UpdateType.valueOf(this.operation_);
      return (result == null) ? UpdateType.SET : result;
    }
    
    public boolean hasValue() {
      return ((this.bitField0_ & 0x4) != 0);
    }
    
    public MysqlxExpr.Expr getValue() {
      return (this.value_ == null) ? MysqlxExpr.Expr.getDefaultInstance() : this.value_;
    }
    
    public MysqlxExpr.ExprOrBuilder getValueOrBuilder() {
      return (this.value_ == null) ? MysqlxExpr.Expr.getDefaultInstance() : this.value_;
    }
    
    public final boolean isInitialized() {
      byte isInitialized = this.memoizedIsInitialized;
      if (isInitialized == 1)
        return true; 
      if (isInitialized == 0)
        return false; 
      if (!hasSource()) {
        this.memoizedIsInitialized = 0;
        return false;
      } 
      if (!hasOperation()) {
        this.memoizedIsInitialized = 0;
        return false;
      } 
      if (!getSource().isInitialized()) {
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
        output.writeMessage(1, (MessageLite)getSource()); 
      if ((this.bitField0_ & 0x2) != 0)
        output.writeEnum(2, this.operation_); 
      if ((this.bitField0_ & 0x4) != 0)
        output.writeMessage(3, (MessageLite)getValue()); 
      getUnknownFields().writeTo(output);
    }
    
    public int getSerializedSize() {
      int size = this.memoizedSize;
      if (size != -1)
        return size; 
      size = 0;
      if ((this.bitField0_ & 0x1) != 0)
        size += 
          CodedOutputStream.computeMessageSize(1, (MessageLite)getSource()); 
      if ((this.bitField0_ & 0x2) != 0)
        size += 
          CodedOutputStream.computeEnumSize(2, this.operation_); 
      if ((this.bitField0_ & 0x4) != 0)
        size += 
          CodedOutputStream.computeMessageSize(3, (MessageLite)getValue()); 
      size += getUnknownFields().getSerializedSize();
      this.memoizedSize = size;
      return size;
    }
    
    public boolean equals(Object obj) {
      if (obj == this)
        return true; 
      if (!(obj instanceof UpdateOperation))
        return super.equals(obj); 
      UpdateOperation other = (UpdateOperation)obj;
      if (hasSource() != other.hasSource())
        return false; 
      if (hasSource() && 
        
        !getSource().equals(other.getSource()))
        return false; 
      if (hasOperation() != other.hasOperation())
        return false; 
      if (hasOperation() && 
        this.operation_ != other.operation_)
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
      if (hasSource()) {
        hash = 37 * hash + 1;
        hash = 53 * hash + getSource().hashCode();
      } 
      if (hasOperation()) {
        hash = 37 * hash + 2;
        hash = 53 * hash + this.operation_;
      } 
      if (hasValue()) {
        hash = 37 * hash + 3;
        hash = 53 * hash + getValue().hashCode();
      } 
      hash = 29 * hash + getUnknownFields().hashCode();
      this.memoizedHashCode = hash;
      return hash;
    }
    
    public static UpdateOperation parseFrom(ByteBuffer data) throws InvalidProtocolBufferException {
      return (UpdateOperation)PARSER.parseFrom(data);
    }
    
    public static UpdateOperation parseFrom(ByteBuffer data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
      return (UpdateOperation)PARSER.parseFrom(data, extensionRegistry);
    }
    
    public static UpdateOperation parseFrom(ByteString data) throws InvalidProtocolBufferException {
      return (UpdateOperation)PARSER.parseFrom(data);
    }
    
    public static UpdateOperation parseFrom(ByteString data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
      return (UpdateOperation)PARSER.parseFrom(data, extensionRegistry);
    }
    
    public static UpdateOperation parseFrom(byte[] data) throws InvalidProtocolBufferException {
      return (UpdateOperation)PARSER.parseFrom(data);
    }
    
    public static UpdateOperation parseFrom(byte[] data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
      return (UpdateOperation)PARSER.parseFrom(data, extensionRegistry);
    }
    
    public static UpdateOperation parseFrom(InputStream input) throws IOException {
      return 
        (UpdateOperation)GeneratedMessageV3.parseWithIOException(PARSER, input);
    }
    
    public static UpdateOperation parseFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
      return 
        (UpdateOperation)GeneratedMessageV3.parseWithIOException(PARSER, input, extensionRegistry);
    }
    
    public static UpdateOperation parseDelimitedFrom(InputStream input) throws IOException {
      return 
        (UpdateOperation)GeneratedMessageV3.parseDelimitedWithIOException(PARSER, input);
    }
    
    public static UpdateOperation parseDelimitedFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
      return 
        (UpdateOperation)GeneratedMessageV3.parseDelimitedWithIOException(PARSER, input, extensionRegistry);
    }
    
    public static UpdateOperation parseFrom(CodedInputStream input) throws IOException {
      return 
        (UpdateOperation)GeneratedMessageV3.parseWithIOException(PARSER, input);
    }
    
    public static UpdateOperation parseFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
      return 
        (UpdateOperation)GeneratedMessageV3.parseWithIOException(PARSER, input, extensionRegistry);
    }
    
    public Builder newBuilderForType() {
      return newBuilder();
    }
    
    public static Builder newBuilder() {
      return DEFAULT_INSTANCE.toBuilder();
    }
    
    public static Builder newBuilder(UpdateOperation prototype) {
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
    
    public static final class Builder extends GeneratedMessageV3.Builder<Builder> implements MysqlxCrud.UpdateOperationOrBuilder {
      private int bitField0_;
      
      private MysqlxExpr.ColumnIdentifier source_;
      
      private SingleFieldBuilderV3<MysqlxExpr.ColumnIdentifier, MysqlxExpr.ColumnIdentifier.Builder, MysqlxExpr.ColumnIdentifierOrBuilder> sourceBuilder_;
      
      private int operation_;
      
      private MysqlxExpr.Expr value_;
      
      private SingleFieldBuilderV3<MysqlxExpr.Expr, MysqlxExpr.Expr.Builder, MysqlxExpr.ExprOrBuilder> valueBuilder_;
      
      public static final Descriptors.Descriptor getDescriptor() {
        return MysqlxCrud.internal_static_Mysqlx_Crud_UpdateOperation_descriptor;
      }
      
      protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() {
        return MysqlxCrud.internal_static_Mysqlx_Crud_UpdateOperation_fieldAccessorTable
          .ensureFieldAccessorsInitialized(MysqlxCrud.UpdateOperation.class, Builder.class);
      }
      
      private Builder() {
        this.operation_ = 1;
        maybeForceBuilderInitialization();
      }
      
      private Builder(GeneratedMessageV3.BuilderParent parent) {
        super(parent);
        this.operation_ = 1;
        maybeForceBuilderInitialization();
      }
      
      private void maybeForceBuilderInitialization() {
        if (MysqlxCrud.UpdateOperation.alwaysUseFieldBuilders) {
          getSourceFieldBuilder();
          getValueFieldBuilder();
        } 
      }
      
      public Builder clear() {
        super.clear();
        if (this.sourceBuilder_ == null) {
          this.source_ = null;
        } else {
          this.sourceBuilder_.clear();
        } 
        this.bitField0_ &= 0xFFFFFFFE;
        this.operation_ = 1;
        this.bitField0_ &= 0xFFFFFFFD;
        if (this.valueBuilder_ == null) {
          this.value_ = null;
        } else {
          this.valueBuilder_.clear();
        } 
        this.bitField0_ &= 0xFFFFFFFB;
        return this;
      }
      
      public Descriptors.Descriptor getDescriptorForType() {
        return MysqlxCrud.internal_static_Mysqlx_Crud_UpdateOperation_descriptor;
      }
      
      public MysqlxCrud.UpdateOperation getDefaultInstanceForType() {
        return MysqlxCrud.UpdateOperation.getDefaultInstance();
      }
      
      public MysqlxCrud.UpdateOperation build() {
        MysqlxCrud.UpdateOperation result = buildPartial();
        if (!result.isInitialized())
          throw newUninitializedMessageException(result); 
        return result;
      }
      
      public MysqlxCrud.UpdateOperation buildPartial() {
        MysqlxCrud.UpdateOperation result = new MysqlxCrud.UpdateOperation(this);
        int from_bitField0_ = this.bitField0_;
        int to_bitField0_ = 0;
        if ((from_bitField0_ & 0x1) != 0) {
          if (this.sourceBuilder_ == null) {
            result.source_ = this.source_;
          } else {
            result.source_ = (MysqlxExpr.ColumnIdentifier)this.sourceBuilder_.build();
          } 
          to_bitField0_ |= 0x1;
        } 
        if ((from_bitField0_ & 0x2) != 0)
          to_bitField0_ |= 0x2; 
        result.operation_ = this.operation_;
        if ((from_bitField0_ & 0x4) != 0) {
          if (this.valueBuilder_ == null) {
            result.value_ = this.value_;
          } else {
            result.value_ = (MysqlxExpr.Expr)this.valueBuilder_.build();
          } 
          to_bitField0_ |= 0x4;
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
        if (other instanceof MysqlxCrud.UpdateOperation)
          return mergeFrom((MysqlxCrud.UpdateOperation)other); 
        super.mergeFrom(other);
        return this;
      }
      
      public Builder mergeFrom(MysqlxCrud.UpdateOperation other) {
        if (other == MysqlxCrud.UpdateOperation.getDefaultInstance())
          return this; 
        if (other.hasSource())
          mergeSource(other.getSource()); 
        if (other.hasOperation())
          setOperation(other.getOperation()); 
        if (other.hasValue())
          mergeValue(other.getValue()); 
        mergeUnknownFields(other.getUnknownFields());
        onChanged();
        return this;
      }
      
      public final boolean isInitialized() {
        if (!hasSource())
          return false; 
        if (!hasOperation())
          return false; 
        if (!getSource().isInitialized())
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
            int tmpRaw;
            MysqlxCrud.UpdateOperation.UpdateType tmpValue;
            int tag = input.readTag();
            switch (tag) {
              case 0:
                done = true;
                continue;
              case 10:
                input.readMessage((MessageLite.Builder)getSourceFieldBuilder().getBuilder(), extensionRegistry);
                this.bitField0_ |= 0x1;
                continue;
              case 16:
                tmpRaw = input.readEnum();
                tmpValue = MysqlxCrud.UpdateOperation.UpdateType.forNumber(tmpRaw);
                if (tmpValue == null) {
                  mergeUnknownVarintField(2, tmpRaw);
                  continue;
                } 
                this.operation_ = tmpRaw;
                this.bitField0_ |= 0x2;
                continue;
              case 26:
                input.readMessage((MessageLite.Builder)getValueFieldBuilder().getBuilder(), extensionRegistry);
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
      
      public boolean hasSource() {
        return ((this.bitField0_ & 0x1) != 0);
      }
      
      public MysqlxExpr.ColumnIdentifier getSource() {
        if (this.sourceBuilder_ == null)
          return (this.source_ == null) ? MysqlxExpr.ColumnIdentifier.getDefaultInstance() : this.source_; 
        return (MysqlxExpr.ColumnIdentifier)this.sourceBuilder_.getMessage();
      }
      
      public Builder setSource(MysqlxExpr.ColumnIdentifier value) {
        if (this.sourceBuilder_ == null) {
          if (value == null)
            throw new NullPointerException(); 
          this.source_ = value;
          onChanged();
        } else {
          this.sourceBuilder_.setMessage((AbstractMessage)value);
        } 
        this.bitField0_ |= 0x1;
        return this;
      }
      
      public Builder setSource(MysqlxExpr.ColumnIdentifier.Builder builderForValue) {
        if (this.sourceBuilder_ == null) {
          this.source_ = builderForValue.build();
          onChanged();
        } else {
          this.sourceBuilder_.setMessage((AbstractMessage)builderForValue.build());
        } 
        this.bitField0_ |= 0x1;
        return this;
      }
      
      public Builder mergeSource(MysqlxExpr.ColumnIdentifier value) {
        if (this.sourceBuilder_ == null) {
          if ((this.bitField0_ & 0x1) != 0 && this.source_ != null && this.source_ != MysqlxExpr.ColumnIdentifier.getDefaultInstance()) {
            this.source_ = MysqlxExpr.ColumnIdentifier.newBuilder(this.source_).mergeFrom(value).buildPartial();
          } else {
            this.source_ = value;
          } 
          onChanged();
        } else {
          this.sourceBuilder_.mergeFrom((AbstractMessage)value);
        } 
        this.bitField0_ |= 0x1;
        return this;
      }
      
      public Builder clearSource() {
        if (this.sourceBuilder_ == null) {
          this.source_ = null;
          onChanged();
        } else {
          this.sourceBuilder_.clear();
        } 
        this.bitField0_ &= 0xFFFFFFFE;
        return this;
      }
      
      public MysqlxExpr.ColumnIdentifier.Builder getSourceBuilder() {
        this.bitField0_ |= 0x1;
        onChanged();
        return (MysqlxExpr.ColumnIdentifier.Builder)getSourceFieldBuilder().getBuilder();
      }
      
      public MysqlxExpr.ColumnIdentifierOrBuilder getSourceOrBuilder() {
        if (this.sourceBuilder_ != null)
          return (MysqlxExpr.ColumnIdentifierOrBuilder)this.sourceBuilder_.getMessageOrBuilder(); 
        return (this.source_ == null) ? MysqlxExpr.ColumnIdentifier.getDefaultInstance() : this.source_;
      }
      
      private SingleFieldBuilderV3<MysqlxExpr.ColumnIdentifier, MysqlxExpr.ColumnIdentifier.Builder, MysqlxExpr.ColumnIdentifierOrBuilder> getSourceFieldBuilder() {
        if (this.sourceBuilder_ == null) {
          this.sourceBuilder_ = new SingleFieldBuilderV3((AbstractMessage)getSource(), (AbstractMessage.BuilderParent)getParentForChildren(), isClean());
          this.source_ = null;
        } 
        return this.sourceBuilder_;
      }
      
      public boolean hasOperation() {
        return ((this.bitField0_ & 0x2) != 0);
      }
      
      public MysqlxCrud.UpdateOperation.UpdateType getOperation() {
        MysqlxCrud.UpdateOperation.UpdateType result = MysqlxCrud.UpdateOperation.UpdateType.valueOf(this.operation_);
        return (result == null) ? MysqlxCrud.UpdateOperation.UpdateType.SET : result;
      }
      
      public Builder setOperation(MysqlxCrud.UpdateOperation.UpdateType value) {
        if (value == null)
          throw new NullPointerException(); 
        this.bitField0_ |= 0x2;
        this.operation_ = value.getNumber();
        onChanged();
        return this;
      }
      
      public Builder clearOperation() {
        this.bitField0_ &= 0xFFFFFFFD;
        this.operation_ = 1;
        onChanged();
        return this;
      }
      
      public boolean hasValue() {
        return ((this.bitField0_ & 0x4) != 0);
      }
      
      public MysqlxExpr.Expr getValue() {
        if (this.valueBuilder_ == null)
          return (this.value_ == null) ? MysqlxExpr.Expr.getDefaultInstance() : this.value_; 
        return (MysqlxExpr.Expr)this.valueBuilder_.getMessage();
      }
      
      public Builder setValue(MysqlxExpr.Expr value) {
        if (this.valueBuilder_ == null) {
          if (value == null)
            throw new NullPointerException(); 
          this.value_ = value;
          onChanged();
        } else {
          this.valueBuilder_.setMessage((AbstractMessage)value);
        } 
        this.bitField0_ |= 0x4;
        return this;
      }
      
      public Builder setValue(MysqlxExpr.Expr.Builder builderForValue) {
        if (this.valueBuilder_ == null) {
          this.value_ = builderForValue.build();
          onChanged();
        } else {
          this.valueBuilder_.setMessage((AbstractMessage)builderForValue.build());
        } 
        this.bitField0_ |= 0x4;
        return this;
      }
      
      public Builder mergeValue(MysqlxExpr.Expr value) {
        if (this.valueBuilder_ == null) {
          if ((this.bitField0_ & 0x4) != 0 && this.value_ != null && this.value_ != 
            
            MysqlxExpr.Expr.getDefaultInstance()) {
            this
              .value_ = MysqlxExpr.Expr.newBuilder(this.value_).mergeFrom(value).buildPartial();
          } else {
            this.value_ = value;
          } 
          onChanged();
        } else {
          this.valueBuilder_.mergeFrom((AbstractMessage)value);
        } 
        this.bitField0_ |= 0x4;
        return this;
      }
      
      public Builder clearValue() {
        if (this.valueBuilder_ == null) {
          this.value_ = null;
          onChanged();
        } else {
          this.valueBuilder_.clear();
        } 
        this.bitField0_ &= 0xFFFFFFFB;
        return this;
      }
      
      public MysqlxExpr.Expr.Builder getValueBuilder() {
        this.bitField0_ |= 0x4;
        onChanged();
        return (MysqlxExpr.Expr.Builder)getValueFieldBuilder().getBuilder();
      }
      
      public MysqlxExpr.ExprOrBuilder getValueOrBuilder() {
        if (this.valueBuilder_ != null)
          return (MysqlxExpr.ExprOrBuilder)this.valueBuilder_.getMessageOrBuilder(); 
        return (this.value_ == null) ? 
          MysqlxExpr.Expr.getDefaultInstance() : this.value_;
      }
      
      private SingleFieldBuilderV3<MysqlxExpr.Expr, MysqlxExpr.Expr.Builder, MysqlxExpr.ExprOrBuilder> getValueFieldBuilder() {
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
    
    private static final UpdateOperation DEFAULT_INSTANCE = new UpdateOperation();
    
    public static UpdateOperation getDefaultInstance() {
      return DEFAULT_INSTANCE;
    }
    
    @Deprecated
    public static final Parser<UpdateOperation> PARSER = (Parser<UpdateOperation>)new AbstractParser<UpdateOperation>() {
        public MysqlxCrud.UpdateOperation parsePartialFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
          MysqlxCrud.UpdateOperation.Builder builder = MysqlxCrud.UpdateOperation.newBuilder();
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
    
    public static Parser<UpdateOperation> parser() {
      return PARSER;
    }
    
    public Parser<UpdateOperation> getParserForType() {
      return PARSER;
    }
    
    public UpdateOperation getDefaultInstanceForType() {
      return DEFAULT_INSTANCE;
    }
  }
  
  public static interface FindOrBuilder extends MessageOrBuilder {
    boolean hasCollection();
    
    MysqlxCrud.Collection getCollection();
    
    MysqlxCrud.CollectionOrBuilder getCollectionOrBuilder();
    
    boolean hasDataModel();
    
    MysqlxCrud.DataModel getDataModel();
    
    List<MysqlxCrud.Projection> getProjectionList();
    
    MysqlxCrud.Projection getProjection(int param1Int);
    
    int getProjectionCount();
    
    List<? extends MysqlxCrud.ProjectionOrBuilder> getProjectionOrBuilderList();
    
    MysqlxCrud.ProjectionOrBuilder getProjectionOrBuilder(int param1Int);
    
    List<MysqlxDatatypes.Scalar> getArgsList();
    
    MysqlxDatatypes.Scalar getArgs(int param1Int);
    
    int getArgsCount();
    
    List<? extends MysqlxDatatypes.ScalarOrBuilder> getArgsOrBuilderList();
    
    MysqlxDatatypes.ScalarOrBuilder getArgsOrBuilder(int param1Int);
    
    boolean hasCriteria();
    
    MysqlxExpr.Expr getCriteria();
    
    MysqlxExpr.ExprOrBuilder getCriteriaOrBuilder();
    
    boolean hasLimit();
    
    MysqlxCrud.Limit getLimit();
    
    MysqlxCrud.LimitOrBuilder getLimitOrBuilder();
    
    List<MysqlxCrud.Order> getOrderList();
    
    MysqlxCrud.Order getOrder(int param1Int);
    
    int getOrderCount();
    
    List<? extends MysqlxCrud.OrderOrBuilder> getOrderOrBuilderList();
    
    MysqlxCrud.OrderOrBuilder getOrderOrBuilder(int param1Int);
    
    List<MysqlxExpr.Expr> getGroupingList();
    
    MysqlxExpr.Expr getGrouping(int param1Int);
    
    int getGroupingCount();
    
    List<? extends MysqlxExpr.ExprOrBuilder> getGroupingOrBuilderList();
    
    MysqlxExpr.ExprOrBuilder getGroupingOrBuilder(int param1Int);
    
    boolean hasGroupingCriteria();
    
    MysqlxExpr.Expr getGroupingCriteria();
    
    MysqlxExpr.ExprOrBuilder getGroupingCriteriaOrBuilder();
    
    boolean hasLocking();
    
    MysqlxCrud.Find.RowLock getLocking();
    
    boolean hasLockingOptions();
    
    MysqlxCrud.Find.RowLockOptions getLockingOptions();
    
    boolean hasLimitExpr();
    
    MysqlxCrud.LimitExpr getLimitExpr();
    
    MysqlxCrud.LimitExprOrBuilder getLimitExprOrBuilder();
  }
  
  public static final class Find extends GeneratedMessageV3 implements FindOrBuilder {
    private static final long serialVersionUID = 0L;
    
    private int bitField0_;
    
    public static final int COLLECTION_FIELD_NUMBER = 2;
    
    private MysqlxCrud.Collection collection_;
    
    public static final int DATA_MODEL_FIELD_NUMBER = 3;
    
    private int dataModel_;
    
    public static final int PROJECTION_FIELD_NUMBER = 4;
    
    private List<MysqlxCrud.Projection> projection_;
    
    public static final int ARGS_FIELD_NUMBER = 11;
    
    private List<MysqlxDatatypes.Scalar> args_;
    
    public static final int CRITERIA_FIELD_NUMBER = 5;
    
    private MysqlxExpr.Expr criteria_;
    
    public static final int LIMIT_FIELD_NUMBER = 6;
    
    private MysqlxCrud.Limit limit_;
    
    public static final int ORDER_FIELD_NUMBER = 7;
    
    private List<MysqlxCrud.Order> order_;
    
    public static final int GROUPING_FIELD_NUMBER = 8;
    
    private List<MysqlxExpr.Expr> grouping_;
    
    public static final int GROUPING_CRITERIA_FIELD_NUMBER = 9;
    
    private MysqlxExpr.Expr groupingCriteria_;
    
    public static final int LOCKING_FIELD_NUMBER = 12;
    
    private int locking_;
    
    public static final int LOCKING_OPTIONS_FIELD_NUMBER = 13;
    
    private int lockingOptions_;
    
    public static final int LIMIT_EXPR_FIELD_NUMBER = 14;
    
    private MysqlxCrud.LimitExpr limitExpr_;
    
    private byte memoizedIsInitialized;
    
    private Find(GeneratedMessageV3.Builder<?> builder) {
      super(builder);
      this.memoizedIsInitialized = -1;
    }
    
    private Find() {
      this.memoizedIsInitialized = -1;
      this.dataModel_ = 1;
      this.projection_ = Collections.emptyList();
      this.args_ = Collections.emptyList();
      this.order_ = Collections.emptyList();
      this.grouping_ = Collections.emptyList();
      this.locking_ = 1;
      this.lockingOptions_ = 1;
    }
    
    protected Object newInstance(GeneratedMessageV3.UnusedPrivateParameter unused) {
      return new Find();
    }
    
    public final UnknownFieldSet getUnknownFields() {
      return this.unknownFields;
    }
    
    public static final Descriptors.Descriptor getDescriptor() {
      return MysqlxCrud.internal_static_Mysqlx_Crud_Find_descriptor;
    }
    
    protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() {
      return MysqlxCrud.internal_static_Mysqlx_Crud_Find_fieldAccessorTable.ensureFieldAccessorsInitialized(Find.class, Builder.class);
    }
    
    public enum RowLock implements ProtocolMessageEnum {
      SHARED_LOCK(1),
      EXCLUSIVE_LOCK(2);
      
      public static final int SHARED_LOCK_VALUE = 1;
      
      public static final int EXCLUSIVE_LOCK_VALUE = 2;
      
      private static final Internal.EnumLiteMap<RowLock> internalValueMap = new Internal.EnumLiteMap<RowLock>() {
          public MysqlxCrud.Find.RowLock findValueByNumber(int number) {
            return MysqlxCrud.Find.RowLock.forNumber(number);
          }
        };
      
      private static final RowLock[] VALUES = values();
      
      private final int value;
      
      public final int getNumber() {
        return this.value;
      }
      
      public static RowLock forNumber(int value) {
        switch (value) {
          case 1:
            return SHARED_LOCK;
          case 2:
            return EXCLUSIVE_LOCK;
        } 
        return null;
      }
      
      public static Internal.EnumLiteMap<RowLock> internalGetValueMap() {
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
        return MysqlxCrud.Find.getDescriptor().getEnumTypes().get(0);
      }
      
      RowLock(int value) {
        this.value = value;
      }
    }
    
    public enum RowLockOptions implements ProtocolMessageEnum {
      NOWAIT(1),
      SKIP_LOCKED(2);
      
      public static final int NOWAIT_VALUE = 1;
      
      public static final int SKIP_LOCKED_VALUE = 2;
      
      private static final Internal.EnumLiteMap<RowLockOptions> internalValueMap = new Internal.EnumLiteMap<RowLockOptions>() {
          public MysqlxCrud.Find.RowLockOptions findValueByNumber(int number) {
            return MysqlxCrud.Find.RowLockOptions.forNumber(number);
          }
        };
      
      private static final RowLockOptions[] VALUES = values();
      
      private final int value;
      
      public final int getNumber() {
        return this.value;
      }
      
      public static RowLockOptions forNumber(int value) {
        switch (value) {
          case 1:
            return NOWAIT;
          case 2:
            return SKIP_LOCKED;
        } 
        return null;
      }
      
      public static Internal.EnumLiteMap<RowLockOptions> internalGetValueMap() {
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
        return MysqlxCrud.Find.getDescriptor().getEnumTypes().get(1);
      }
      
      RowLockOptions(int value) {
        this.value = value;
      }
    }
    
    public boolean hasCollection() {
      return ((this.bitField0_ & 0x1) != 0);
    }
    
    public MysqlxCrud.Collection getCollection() {
      return (this.collection_ == null) ? MysqlxCrud.Collection.getDefaultInstance() : this.collection_;
    }
    
    public MysqlxCrud.CollectionOrBuilder getCollectionOrBuilder() {
      return (this.collection_ == null) ? MysqlxCrud.Collection.getDefaultInstance() : this.collection_;
    }
    
    public boolean hasDataModel() {
      return ((this.bitField0_ & 0x2) != 0);
    }
    
    public MysqlxCrud.DataModel getDataModel() {
      MysqlxCrud.DataModel result = MysqlxCrud.DataModel.valueOf(this.dataModel_);
      return (result == null) ? MysqlxCrud.DataModel.DOCUMENT : result;
    }
    
    public List<MysqlxCrud.Projection> getProjectionList() {
      return this.projection_;
    }
    
    public List<? extends MysqlxCrud.ProjectionOrBuilder> getProjectionOrBuilderList() {
      return (List)this.projection_;
    }
    
    public int getProjectionCount() {
      return this.projection_.size();
    }
    
    public MysqlxCrud.Projection getProjection(int index) {
      return this.projection_.get(index);
    }
    
    public MysqlxCrud.ProjectionOrBuilder getProjectionOrBuilder(int index) {
      return this.projection_.get(index);
    }
    
    public List<MysqlxDatatypes.Scalar> getArgsList() {
      return this.args_;
    }
    
    public List<? extends MysqlxDatatypes.ScalarOrBuilder> getArgsOrBuilderList() {
      return (List)this.args_;
    }
    
    public int getArgsCount() {
      return this.args_.size();
    }
    
    public MysqlxDatatypes.Scalar getArgs(int index) {
      return this.args_.get(index);
    }
    
    public MysqlxDatatypes.ScalarOrBuilder getArgsOrBuilder(int index) {
      return this.args_.get(index);
    }
    
    public boolean hasCriteria() {
      return ((this.bitField0_ & 0x4) != 0);
    }
    
    public MysqlxExpr.Expr getCriteria() {
      return (this.criteria_ == null) ? MysqlxExpr.Expr.getDefaultInstance() : this.criteria_;
    }
    
    public MysqlxExpr.ExprOrBuilder getCriteriaOrBuilder() {
      return (this.criteria_ == null) ? MysqlxExpr.Expr.getDefaultInstance() : this.criteria_;
    }
    
    public boolean hasLimit() {
      return ((this.bitField0_ & 0x8) != 0);
    }
    
    public MysqlxCrud.Limit getLimit() {
      return (this.limit_ == null) ? MysqlxCrud.Limit.getDefaultInstance() : this.limit_;
    }
    
    public MysqlxCrud.LimitOrBuilder getLimitOrBuilder() {
      return (this.limit_ == null) ? MysqlxCrud.Limit.getDefaultInstance() : this.limit_;
    }
    
    public List<MysqlxCrud.Order> getOrderList() {
      return this.order_;
    }
    
    public List<? extends MysqlxCrud.OrderOrBuilder> getOrderOrBuilderList() {
      return (List)this.order_;
    }
    
    public int getOrderCount() {
      return this.order_.size();
    }
    
    public MysqlxCrud.Order getOrder(int index) {
      return this.order_.get(index);
    }
    
    public MysqlxCrud.OrderOrBuilder getOrderOrBuilder(int index) {
      return this.order_.get(index);
    }
    
    public List<MysqlxExpr.Expr> getGroupingList() {
      return this.grouping_;
    }
    
    public List<? extends MysqlxExpr.ExprOrBuilder> getGroupingOrBuilderList() {
      return (List)this.grouping_;
    }
    
    public int getGroupingCount() {
      return this.grouping_.size();
    }
    
    public MysqlxExpr.Expr getGrouping(int index) {
      return this.grouping_.get(index);
    }
    
    public MysqlxExpr.ExprOrBuilder getGroupingOrBuilder(int index) {
      return this.grouping_.get(index);
    }
    
    public boolean hasGroupingCriteria() {
      return ((this.bitField0_ & 0x10) != 0);
    }
    
    public MysqlxExpr.Expr getGroupingCriteria() {
      return (this.groupingCriteria_ == null) ? MysqlxExpr.Expr.getDefaultInstance() : this.groupingCriteria_;
    }
    
    public MysqlxExpr.ExprOrBuilder getGroupingCriteriaOrBuilder() {
      return (this.groupingCriteria_ == null) ? MysqlxExpr.Expr.getDefaultInstance() : this.groupingCriteria_;
    }
    
    public boolean hasLocking() {
      return ((this.bitField0_ & 0x20) != 0);
    }
    
    public RowLock getLocking() {
      RowLock result = RowLock.valueOf(this.locking_);
      return (result == null) ? RowLock.SHARED_LOCK : result;
    }
    
    public boolean hasLockingOptions() {
      return ((this.bitField0_ & 0x40) != 0);
    }
    
    public RowLockOptions getLockingOptions() {
      RowLockOptions result = RowLockOptions.valueOf(this.lockingOptions_);
      return (result == null) ? RowLockOptions.NOWAIT : result;
    }
    
    public boolean hasLimitExpr() {
      return ((this.bitField0_ & 0x80) != 0);
    }
    
    public MysqlxCrud.LimitExpr getLimitExpr() {
      return (this.limitExpr_ == null) ? MysqlxCrud.LimitExpr.getDefaultInstance() : this.limitExpr_;
    }
    
    public MysqlxCrud.LimitExprOrBuilder getLimitExprOrBuilder() {
      return (this.limitExpr_ == null) ? MysqlxCrud.LimitExpr.getDefaultInstance() : this.limitExpr_;
    }
    
    public final boolean isInitialized() {
      byte isInitialized = this.memoizedIsInitialized;
      if (isInitialized == 1)
        return true; 
      if (isInitialized == 0)
        return false; 
      if (!hasCollection()) {
        this.memoizedIsInitialized = 0;
        return false;
      } 
      if (!getCollection().isInitialized()) {
        this.memoizedIsInitialized = 0;
        return false;
      } 
      int i;
      for (i = 0; i < getProjectionCount(); i++) {
        if (!getProjection(i).isInitialized()) {
          this.memoizedIsInitialized = 0;
          return false;
        } 
      } 
      for (i = 0; i < getArgsCount(); i++) {
        if (!getArgs(i).isInitialized()) {
          this.memoizedIsInitialized = 0;
          return false;
        } 
      } 
      if (hasCriteria() && 
        !getCriteria().isInitialized()) {
        this.memoizedIsInitialized = 0;
        return false;
      } 
      if (hasLimit() && 
        !getLimit().isInitialized()) {
        this.memoizedIsInitialized = 0;
        return false;
      } 
      for (i = 0; i < getOrderCount(); i++) {
        if (!getOrder(i).isInitialized()) {
          this.memoizedIsInitialized = 0;
          return false;
        } 
      } 
      for (i = 0; i < getGroupingCount(); i++) {
        if (!getGrouping(i).isInitialized()) {
          this.memoizedIsInitialized = 0;
          return false;
        } 
      } 
      if (hasGroupingCriteria() && 
        !getGroupingCriteria().isInitialized()) {
        this.memoizedIsInitialized = 0;
        return false;
      } 
      if (hasLimitExpr() && 
        !getLimitExpr().isInitialized()) {
        this.memoizedIsInitialized = 0;
        return false;
      } 
      this.memoizedIsInitialized = 1;
      return true;
    }
    
    public void writeTo(CodedOutputStream output) throws IOException {
      if ((this.bitField0_ & 0x1) != 0)
        output.writeMessage(2, (MessageLite)getCollection()); 
      if ((this.bitField0_ & 0x2) != 0)
        output.writeEnum(3, this.dataModel_); 
      int i;
      for (i = 0; i < this.projection_.size(); i++)
        output.writeMessage(4, (MessageLite)this.projection_.get(i)); 
      if ((this.bitField0_ & 0x4) != 0)
        output.writeMessage(5, (MessageLite)getCriteria()); 
      if ((this.bitField0_ & 0x8) != 0)
        output.writeMessage(6, (MessageLite)getLimit()); 
      for (i = 0; i < this.order_.size(); i++)
        output.writeMessage(7, (MessageLite)this.order_.get(i)); 
      for (i = 0; i < this.grouping_.size(); i++)
        output.writeMessage(8, (MessageLite)this.grouping_.get(i)); 
      if ((this.bitField0_ & 0x10) != 0)
        output.writeMessage(9, (MessageLite)getGroupingCriteria()); 
      for (i = 0; i < this.args_.size(); i++)
        output.writeMessage(11, (MessageLite)this.args_.get(i)); 
      if ((this.bitField0_ & 0x20) != 0)
        output.writeEnum(12, this.locking_); 
      if ((this.bitField0_ & 0x40) != 0)
        output.writeEnum(13, this.lockingOptions_); 
      if ((this.bitField0_ & 0x80) != 0)
        output.writeMessage(14, (MessageLite)getLimitExpr()); 
      getUnknownFields().writeTo(output);
    }
    
    public int getSerializedSize() {
      int size = this.memoizedSize;
      if (size != -1)
        return size; 
      size = 0;
      if ((this.bitField0_ & 0x1) != 0)
        size += 
          CodedOutputStream.computeMessageSize(2, (MessageLite)getCollection()); 
      if ((this.bitField0_ & 0x2) != 0)
        size += 
          CodedOutputStream.computeEnumSize(3, this.dataModel_); 
      int i;
      for (i = 0; i < this.projection_.size(); i++)
        size += 
          CodedOutputStream.computeMessageSize(4, (MessageLite)this.projection_.get(i)); 
      if ((this.bitField0_ & 0x4) != 0)
        size += 
          CodedOutputStream.computeMessageSize(5, (MessageLite)getCriteria()); 
      if ((this.bitField0_ & 0x8) != 0)
        size += 
          CodedOutputStream.computeMessageSize(6, (MessageLite)getLimit()); 
      for (i = 0; i < this.order_.size(); i++)
        size += 
          CodedOutputStream.computeMessageSize(7, (MessageLite)this.order_.get(i)); 
      for (i = 0; i < this.grouping_.size(); i++)
        size += 
          CodedOutputStream.computeMessageSize(8, (MessageLite)this.grouping_.get(i)); 
      if ((this.bitField0_ & 0x10) != 0)
        size += 
          CodedOutputStream.computeMessageSize(9, (MessageLite)getGroupingCriteria()); 
      for (i = 0; i < this.args_.size(); i++)
        size += 
          CodedOutputStream.computeMessageSize(11, (MessageLite)this.args_.get(i)); 
      if ((this.bitField0_ & 0x20) != 0)
        size += 
          CodedOutputStream.computeEnumSize(12, this.locking_); 
      if ((this.bitField0_ & 0x40) != 0)
        size += 
          CodedOutputStream.computeEnumSize(13, this.lockingOptions_); 
      if ((this.bitField0_ & 0x80) != 0)
        size += 
          CodedOutputStream.computeMessageSize(14, (MessageLite)getLimitExpr()); 
      size += getUnknownFields().getSerializedSize();
      this.memoizedSize = size;
      return size;
    }
    
    public boolean equals(Object obj) {
      if (obj == this)
        return true; 
      if (!(obj instanceof Find))
        return super.equals(obj); 
      Find other = (Find)obj;
      if (hasCollection() != other.hasCollection())
        return false; 
      if (hasCollection() && 
        
        !getCollection().equals(other.getCollection()))
        return false; 
      if (hasDataModel() != other.hasDataModel())
        return false; 
      if (hasDataModel() && 
        this.dataModel_ != other.dataModel_)
        return false; 
      if (!getProjectionList().equals(other.getProjectionList()))
        return false; 
      if (!getArgsList().equals(other.getArgsList()))
        return false; 
      if (hasCriteria() != other.hasCriteria())
        return false; 
      if (hasCriteria() && 
        
        !getCriteria().equals(other.getCriteria()))
        return false; 
      if (hasLimit() != other.hasLimit())
        return false; 
      if (hasLimit() && 
        
        !getLimit().equals(other.getLimit()))
        return false; 
      if (!getOrderList().equals(other.getOrderList()))
        return false; 
      if (!getGroupingList().equals(other.getGroupingList()))
        return false; 
      if (hasGroupingCriteria() != other.hasGroupingCriteria())
        return false; 
      if (hasGroupingCriteria() && 
        
        !getGroupingCriteria().equals(other.getGroupingCriteria()))
        return false; 
      if (hasLocking() != other.hasLocking())
        return false; 
      if (hasLocking() && 
        this.locking_ != other.locking_)
        return false; 
      if (hasLockingOptions() != other.hasLockingOptions())
        return false; 
      if (hasLockingOptions() && 
        this.lockingOptions_ != other.lockingOptions_)
        return false; 
      if (hasLimitExpr() != other.hasLimitExpr())
        return false; 
      if (hasLimitExpr() && 
        
        !getLimitExpr().equals(other.getLimitExpr()))
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
      if (hasCollection()) {
        hash = 37 * hash + 2;
        hash = 53 * hash + getCollection().hashCode();
      } 
      if (hasDataModel()) {
        hash = 37 * hash + 3;
        hash = 53 * hash + this.dataModel_;
      } 
      if (getProjectionCount() > 0) {
        hash = 37 * hash + 4;
        hash = 53 * hash + getProjectionList().hashCode();
      } 
      if (getArgsCount() > 0) {
        hash = 37 * hash + 11;
        hash = 53 * hash + getArgsList().hashCode();
      } 
      if (hasCriteria()) {
        hash = 37 * hash + 5;
        hash = 53 * hash + getCriteria().hashCode();
      } 
      if (hasLimit()) {
        hash = 37 * hash + 6;
        hash = 53 * hash + getLimit().hashCode();
      } 
      if (getOrderCount() > 0) {
        hash = 37 * hash + 7;
        hash = 53 * hash + getOrderList().hashCode();
      } 
      if (getGroupingCount() > 0) {
        hash = 37 * hash + 8;
        hash = 53 * hash + getGroupingList().hashCode();
      } 
      if (hasGroupingCriteria()) {
        hash = 37 * hash + 9;
        hash = 53 * hash + getGroupingCriteria().hashCode();
      } 
      if (hasLocking()) {
        hash = 37 * hash + 12;
        hash = 53 * hash + this.locking_;
      } 
      if (hasLockingOptions()) {
        hash = 37 * hash + 13;
        hash = 53 * hash + this.lockingOptions_;
      } 
      if (hasLimitExpr()) {
        hash = 37 * hash + 14;
        hash = 53 * hash + getLimitExpr().hashCode();
      } 
      hash = 29 * hash + getUnknownFields().hashCode();
      this.memoizedHashCode = hash;
      return hash;
    }
    
    public static Find parseFrom(ByteBuffer data) throws InvalidProtocolBufferException {
      return (Find)PARSER.parseFrom(data);
    }
    
    public static Find parseFrom(ByteBuffer data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
      return (Find)PARSER.parseFrom(data, extensionRegistry);
    }
    
    public static Find parseFrom(ByteString data) throws InvalidProtocolBufferException {
      return (Find)PARSER.parseFrom(data);
    }
    
    public static Find parseFrom(ByteString data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
      return (Find)PARSER.parseFrom(data, extensionRegistry);
    }
    
    public static Find parseFrom(byte[] data) throws InvalidProtocolBufferException {
      return (Find)PARSER.parseFrom(data);
    }
    
    public static Find parseFrom(byte[] data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
      return (Find)PARSER.parseFrom(data, extensionRegistry);
    }
    
    public static Find parseFrom(InputStream input) throws IOException {
      return 
        (Find)GeneratedMessageV3.parseWithIOException(PARSER, input);
    }
    
    public static Find parseFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
      return 
        (Find)GeneratedMessageV3.parseWithIOException(PARSER, input, extensionRegistry);
    }
    
    public static Find parseDelimitedFrom(InputStream input) throws IOException {
      return 
        (Find)GeneratedMessageV3.parseDelimitedWithIOException(PARSER, input);
    }
    
    public static Find parseDelimitedFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
      return 
        (Find)GeneratedMessageV3.parseDelimitedWithIOException(PARSER, input, extensionRegistry);
    }
    
    public static Find parseFrom(CodedInputStream input) throws IOException {
      return 
        (Find)GeneratedMessageV3.parseWithIOException(PARSER, input);
    }
    
    public static Find parseFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
      return 
        (Find)GeneratedMessageV3.parseWithIOException(PARSER, input, extensionRegistry);
    }
    
    public Builder newBuilderForType() {
      return newBuilder();
    }
    
    public static Builder newBuilder() {
      return DEFAULT_INSTANCE.toBuilder();
    }
    
    public static Builder newBuilder(Find prototype) {
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
    
    public static final class Builder extends GeneratedMessageV3.Builder<Builder> implements MysqlxCrud.FindOrBuilder {
      private int bitField0_;
      
      private MysqlxCrud.Collection collection_;
      
      private SingleFieldBuilderV3<MysqlxCrud.Collection, MysqlxCrud.Collection.Builder, MysqlxCrud.CollectionOrBuilder> collectionBuilder_;
      
      private int dataModel_;
      
      private List<MysqlxCrud.Projection> projection_;
      
      private RepeatedFieldBuilderV3<MysqlxCrud.Projection, MysqlxCrud.Projection.Builder, MysqlxCrud.ProjectionOrBuilder> projectionBuilder_;
      
      private List<MysqlxDatatypes.Scalar> args_;
      
      private RepeatedFieldBuilderV3<MysqlxDatatypes.Scalar, MysqlxDatatypes.Scalar.Builder, MysqlxDatatypes.ScalarOrBuilder> argsBuilder_;
      
      private MysqlxExpr.Expr criteria_;
      
      private SingleFieldBuilderV3<MysqlxExpr.Expr, MysqlxExpr.Expr.Builder, MysqlxExpr.ExprOrBuilder> criteriaBuilder_;
      
      private MysqlxCrud.Limit limit_;
      
      private SingleFieldBuilderV3<MysqlxCrud.Limit, MysqlxCrud.Limit.Builder, MysqlxCrud.LimitOrBuilder> limitBuilder_;
      
      private List<MysqlxCrud.Order> order_;
      
      private RepeatedFieldBuilderV3<MysqlxCrud.Order, MysqlxCrud.Order.Builder, MysqlxCrud.OrderOrBuilder> orderBuilder_;
      
      private List<MysqlxExpr.Expr> grouping_;
      
      private RepeatedFieldBuilderV3<MysqlxExpr.Expr, MysqlxExpr.Expr.Builder, MysqlxExpr.ExprOrBuilder> groupingBuilder_;
      
      private MysqlxExpr.Expr groupingCriteria_;
      
      private SingleFieldBuilderV3<MysqlxExpr.Expr, MysqlxExpr.Expr.Builder, MysqlxExpr.ExprOrBuilder> groupingCriteriaBuilder_;
      
      private int locking_;
      
      private int lockingOptions_;
      
      private MysqlxCrud.LimitExpr limitExpr_;
      
      private SingleFieldBuilderV3<MysqlxCrud.LimitExpr, MysqlxCrud.LimitExpr.Builder, MysqlxCrud.LimitExprOrBuilder> limitExprBuilder_;
      
      public static final Descriptors.Descriptor getDescriptor() {
        return MysqlxCrud.internal_static_Mysqlx_Crud_Find_descriptor;
      }
      
      protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() {
        return MysqlxCrud.internal_static_Mysqlx_Crud_Find_fieldAccessorTable
          .ensureFieldAccessorsInitialized(MysqlxCrud.Find.class, Builder.class);
      }
      
      private Builder() {
        this.dataModel_ = 1;
        this
          .projection_ = Collections.emptyList();
        this
          .args_ = Collections.emptyList();
        this
          .order_ = Collections.emptyList();
        this
          .grouping_ = Collections.emptyList();
        this.locking_ = 1;
        this.lockingOptions_ = 1;
        maybeForceBuilderInitialization();
      }
      
      private Builder(GeneratedMessageV3.BuilderParent parent) {
        super(parent);
        this.dataModel_ = 1;
        this.projection_ = Collections.emptyList();
        this.args_ = Collections.emptyList();
        this.order_ = Collections.emptyList();
        this.grouping_ = Collections.emptyList();
        this.locking_ = 1;
        this.lockingOptions_ = 1;
        maybeForceBuilderInitialization();
      }
      
      private void maybeForceBuilderInitialization() {
        if (MysqlxCrud.Find.alwaysUseFieldBuilders) {
          getCollectionFieldBuilder();
          getProjectionFieldBuilder();
          getArgsFieldBuilder();
          getCriteriaFieldBuilder();
          getLimitFieldBuilder();
          getOrderFieldBuilder();
          getGroupingFieldBuilder();
          getGroupingCriteriaFieldBuilder();
          getLimitExprFieldBuilder();
        } 
      }
      
      public Builder clear() {
        super.clear();
        if (this.collectionBuilder_ == null) {
          this.collection_ = null;
        } else {
          this.collectionBuilder_.clear();
        } 
        this.bitField0_ &= 0xFFFFFFFE;
        this.dataModel_ = 1;
        this.bitField0_ &= 0xFFFFFFFD;
        if (this.projectionBuilder_ == null) {
          this.projection_ = Collections.emptyList();
        } else {
          this.projection_ = null;
          this.projectionBuilder_.clear();
        } 
        this.bitField0_ &= 0xFFFFFFFB;
        if (this.argsBuilder_ == null) {
          this.args_ = Collections.emptyList();
        } else {
          this.args_ = null;
          this.argsBuilder_.clear();
        } 
        this.bitField0_ &= 0xFFFFFFF7;
        if (this.criteriaBuilder_ == null) {
          this.criteria_ = null;
        } else {
          this.criteriaBuilder_.clear();
        } 
        this.bitField0_ &= 0xFFFFFFEF;
        if (this.limitBuilder_ == null) {
          this.limit_ = null;
        } else {
          this.limitBuilder_.clear();
        } 
        this.bitField0_ &= 0xFFFFFFDF;
        if (this.orderBuilder_ == null) {
          this.order_ = Collections.emptyList();
        } else {
          this.order_ = null;
          this.orderBuilder_.clear();
        } 
        this.bitField0_ &= 0xFFFFFFBF;
        if (this.groupingBuilder_ == null) {
          this.grouping_ = Collections.emptyList();
        } else {
          this.grouping_ = null;
          this.groupingBuilder_.clear();
        } 
        this.bitField0_ &= 0xFFFFFF7F;
        if (this.groupingCriteriaBuilder_ == null) {
          this.groupingCriteria_ = null;
        } else {
          this.groupingCriteriaBuilder_.clear();
        } 
        this.bitField0_ &= 0xFFFFFEFF;
        this.locking_ = 1;
        this.bitField0_ &= 0xFFFFFDFF;
        this.lockingOptions_ = 1;
        this.bitField0_ &= 0xFFFFFBFF;
        if (this.limitExprBuilder_ == null) {
          this.limitExpr_ = null;
        } else {
          this.limitExprBuilder_.clear();
        } 
        this.bitField0_ &= 0xFFFFF7FF;
        return this;
      }
      
      public Descriptors.Descriptor getDescriptorForType() {
        return MysqlxCrud.internal_static_Mysqlx_Crud_Find_descriptor;
      }
      
      public MysqlxCrud.Find getDefaultInstanceForType() {
        return MysqlxCrud.Find.getDefaultInstance();
      }
      
      public MysqlxCrud.Find build() {
        MysqlxCrud.Find result = buildPartial();
        if (!result.isInitialized())
          throw newUninitializedMessageException(result); 
        return result;
      }
      
      public MysqlxCrud.Find buildPartial() {
        MysqlxCrud.Find result = new MysqlxCrud.Find(this);
        int from_bitField0_ = this.bitField0_;
        int to_bitField0_ = 0;
        if ((from_bitField0_ & 0x1) != 0) {
          if (this.collectionBuilder_ == null) {
            result.collection_ = this.collection_;
          } else {
            result.collection_ = (MysqlxCrud.Collection)this.collectionBuilder_.build();
          } 
          to_bitField0_ |= 0x1;
        } 
        if ((from_bitField0_ & 0x2) != 0)
          to_bitField0_ |= 0x2; 
        result.dataModel_ = this.dataModel_;
        if (this.projectionBuilder_ == null) {
          if ((this.bitField0_ & 0x4) != 0) {
            this.projection_ = Collections.unmodifiableList(this.projection_);
            this.bitField0_ &= 0xFFFFFFFB;
          } 
          result.projection_ = this.projection_;
        } else {
          result.projection_ = this.projectionBuilder_.build();
        } 
        if (this.argsBuilder_ == null) {
          if ((this.bitField0_ & 0x8) != 0) {
            this.args_ = Collections.unmodifiableList(this.args_);
            this.bitField0_ &= 0xFFFFFFF7;
          } 
          result.args_ = this.args_;
        } else {
          result.args_ = this.argsBuilder_.build();
        } 
        if ((from_bitField0_ & 0x10) != 0) {
          if (this.criteriaBuilder_ == null) {
            result.criteria_ = this.criteria_;
          } else {
            result.criteria_ = (MysqlxExpr.Expr)this.criteriaBuilder_.build();
          } 
          to_bitField0_ |= 0x4;
        } 
        if ((from_bitField0_ & 0x20) != 0) {
          if (this.limitBuilder_ == null) {
            result.limit_ = this.limit_;
          } else {
            result.limit_ = (MysqlxCrud.Limit)this.limitBuilder_.build();
          } 
          to_bitField0_ |= 0x8;
        } 
        if (this.orderBuilder_ == null) {
          if ((this.bitField0_ & 0x40) != 0) {
            this.order_ = Collections.unmodifiableList(this.order_);
            this.bitField0_ &= 0xFFFFFFBF;
          } 
          result.order_ = this.order_;
        } else {
          result.order_ = this.orderBuilder_.build();
        } 
        if (this.groupingBuilder_ == null) {
          if ((this.bitField0_ & 0x80) != 0) {
            this.grouping_ = Collections.unmodifiableList(this.grouping_);
            this.bitField0_ &= 0xFFFFFF7F;
          } 
          result.grouping_ = this.grouping_;
        } else {
          result.grouping_ = this.groupingBuilder_.build();
        } 
        if ((from_bitField0_ & 0x100) != 0) {
          if (this.groupingCriteriaBuilder_ == null) {
            result.groupingCriteria_ = this.groupingCriteria_;
          } else {
            result.groupingCriteria_ = (MysqlxExpr.Expr)this.groupingCriteriaBuilder_.build();
          } 
          to_bitField0_ |= 0x10;
        } 
        if ((from_bitField0_ & 0x200) != 0)
          to_bitField0_ |= 0x20; 
        result.locking_ = this.locking_;
        if ((from_bitField0_ & 0x400) != 0)
          to_bitField0_ |= 0x40; 
        result.lockingOptions_ = this.lockingOptions_;
        if ((from_bitField0_ & 0x800) != 0) {
          if (this.limitExprBuilder_ == null) {
            result.limitExpr_ = this.limitExpr_;
          } else {
            result.limitExpr_ = (MysqlxCrud.LimitExpr)this.limitExprBuilder_.build();
          } 
          to_bitField0_ |= 0x80;
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
        if (other instanceof MysqlxCrud.Find)
          return mergeFrom((MysqlxCrud.Find)other); 
        super.mergeFrom(other);
        return this;
      }
      
      public Builder mergeFrom(MysqlxCrud.Find other) {
        if (other == MysqlxCrud.Find.getDefaultInstance())
          return this; 
        if (other.hasCollection())
          mergeCollection(other.getCollection()); 
        if (other.hasDataModel())
          setDataModel(other.getDataModel()); 
        if (this.projectionBuilder_ == null) {
          if (!other.projection_.isEmpty()) {
            if (this.projection_.isEmpty()) {
              this.projection_ = other.projection_;
              this.bitField0_ &= 0xFFFFFFFB;
            } else {
              ensureProjectionIsMutable();
              this.projection_.addAll(other.projection_);
            } 
            onChanged();
          } 
        } else if (!other.projection_.isEmpty()) {
          if (this.projectionBuilder_.isEmpty()) {
            this.projectionBuilder_.dispose();
            this.projectionBuilder_ = null;
            this.projection_ = other.projection_;
            this.bitField0_ &= 0xFFFFFFFB;
            this.projectionBuilder_ = MysqlxCrud.Find.alwaysUseFieldBuilders ? getProjectionFieldBuilder() : null;
          } else {
            this.projectionBuilder_.addAllMessages(other.projection_);
          } 
        } 
        if (this.argsBuilder_ == null) {
          if (!other.args_.isEmpty()) {
            if (this.args_.isEmpty()) {
              this.args_ = other.args_;
              this.bitField0_ &= 0xFFFFFFF7;
            } else {
              ensureArgsIsMutable();
              this.args_.addAll(other.args_);
            } 
            onChanged();
          } 
        } else if (!other.args_.isEmpty()) {
          if (this.argsBuilder_.isEmpty()) {
            this.argsBuilder_.dispose();
            this.argsBuilder_ = null;
            this.args_ = other.args_;
            this.bitField0_ &= 0xFFFFFFF7;
            this.argsBuilder_ = MysqlxCrud.Find.alwaysUseFieldBuilders ? getArgsFieldBuilder() : null;
          } else {
            this.argsBuilder_.addAllMessages(other.args_);
          } 
        } 
        if (other.hasCriteria())
          mergeCriteria(other.getCriteria()); 
        if (other.hasLimit())
          mergeLimit(other.getLimit()); 
        if (this.orderBuilder_ == null) {
          if (!other.order_.isEmpty()) {
            if (this.order_.isEmpty()) {
              this.order_ = other.order_;
              this.bitField0_ &= 0xFFFFFFBF;
            } else {
              ensureOrderIsMutable();
              this.order_.addAll(other.order_);
            } 
            onChanged();
          } 
        } else if (!other.order_.isEmpty()) {
          if (this.orderBuilder_.isEmpty()) {
            this.orderBuilder_.dispose();
            this.orderBuilder_ = null;
            this.order_ = other.order_;
            this.bitField0_ &= 0xFFFFFFBF;
            this.orderBuilder_ = MysqlxCrud.Find.alwaysUseFieldBuilders ? getOrderFieldBuilder() : null;
          } else {
            this.orderBuilder_.addAllMessages(other.order_);
          } 
        } 
        if (this.groupingBuilder_ == null) {
          if (!other.grouping_.isEmpty()) {
            if (this.grouping_.isEmpty()) {
              this.grouping_ = other.grouping_;
              this.bitField0_ &= 0xFFFFFF7F;
            } else {
              ensureGroupingIsMutable();
              this.grouping_.addAll(other.grouping_);
            } 
            onChanged();
          } 
        } else if (!other.grouping_.isEmpty()) {
          if (this.groupingBuilder_.isEmpty()) {
            this.groupingBuilder_.dispose();
            this.groupingBuilder_ = null;
            this.grouping_ = other.grouping_;
            this.bitField0_ &= 0xFFFFFF7F;
            this.groupingBuilder_ = MysqlxCrud.Find.alwaysUseFieldBuilders ? getGroupingFieldBuilder() : null;
          } else {
            this.groupingBuilder_.addAllMessages(other.grouping_);
          } 
        } 
        if (other.hasGroupingCriteria())
          mergeGroupingCriteria(other.getGroupingCriteria()); 
        if (other.hasLocking())
          setLocking(other.getLocking()); 
        if (other.hasLockingOptions())
          setLockingOptions(other.getLockingOptions()); 
        if (other.hasLimitExpr())
          mergeLimitExpr(other.getLimitExpr()); 
        mergeUnknownFields(other.getUnknownFields());
        onChanged();
        return this;
      }
      
      public final boolean isInitialized() {
        if (!hasCollection())
          return false; 
        if (!getCollection().isInitialized())
          return false; 
        int i;
        for (i = 0; i < getProjectionCount(); i++) {
          if (!getProjection(i).isInitialized())
            return false; 
        } 
        for (i = 0; i < getArgsCount(); i++) {
          if (!getArgs(i).isInitialized())
            return false; 
        } 
        if (hasCriteria() && !getCriteria().isInitialized())
          return false; 
        if (hasLimit() && !getLimit().isInitialized())
          return false; 
        for (i = 0; i < getOrderCount(); i++) {
          if (!getOrder(i).isInitialized())
            return false; 
        } 
        for (i = 0; i < getGroupingCount(); i++) {
          if (!getGrouping(i).isInitialized())
            return false; 
        } 
        if (hasGroupingCriteria() && !getGroupingCriteria().isInitialized())
          return false; 
        if (hasLimitExpr() && !getLimitExpr().isInitialized())
          return false; 
        return true;
      }
      
      public Builder mergeFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
        if (extensionRegistry == null)
          throw new NullPointerException(); 
        try {
          boolean done = false;
          while (!done) {
            int i;
            MysqlxCrud.Projection projection;
            MysqlxCrud.Order order;
            MysqlxExpr.Expr expr;
            MysqlxDatatypes.Scalar m;
            int tmpRaw;
            MysqlxCrud.DataModel dataModel;
            MysqlxCrud.Find.RowLock rowLock;
            MysqlxCrud.Find.RowLockOptions tmpValue;
            int tag = input.readTag();
            switch (tag) {
              case 0:
                done = true;
                continue;
              case 18:
                input.readMessage((MessageLite.Builder)getCollectionFieldBuilder().getBuilder(), extensionRegistry);
                this.bitField0_ |= 0x1;
                continue;
              case 24:
                i = input.readEnum();
                dataModel = MysqlxCrud.DataModel.forNumber(i);
                if (dataModel == null) {
                  mergeUnknownVarintField(3, i);
                  continue;
                } 
                this.dataModel_ = i;
                this.bitField0_ |= 0x2;
                continue;
              case 34:
                projection = (MysqlxCrud.Projection)input.readMessage(MysqlxCrud.Projection.PARSER, extensionRegistry);
                if (this.projectionBuilder_ == null) {
                  ensureProjectionIsMutable();
                  this.projection_.add(projection);
                  continue;
                } 
                this.projectionBuilder_.addMessage((AbstractMessage)projection);
                continue;
              case 42:
                input.readMessage((MessageLite.Builder)getCriteriaFieldBuilder().getBuilder(), extensionRegistry);
                this.bitField0_ |= 0x10;
                continue;
              case 50:
                input.readMessage((MessageLite.Builder)getLimitFieldBuilder().getBuilder(), extensionRegistry);
                this.bitField0_ |= 0x20;
                continue;
              case 58:
                order = (MysqlxCrud.Order)input.readMessage(MysqlxCrud.Order.PARSER, extensionRegistry);
                if (this.orderBuilder_ == null) {
                  ensureOrderIsMutable();
                  this.order_.add(order);
                  continue;
                } 
                this.orderBuilder_.addMessage((AbstractMessage)order);
                continue;
              case 66:
                expr = (MysqlxExpr.Expr)input.readMessage(MysqlxExpr.Expr.PARSER, extensionRegistry);
                if (this.groupingBuilder_ == null) {
                  ensureGroupingIsMutable();
                  this.grouping_.add(expr);
                  continue;
                } 
                this.groupingBuilder_.addMessage((AbstractMessage)expr);
                continue;
              case 74:
                input.readMessage((MessageLite.Builder)getGroupingCriteriaFieldBuilder().getBuilder(), extensionRegistry);
                this.bitField0_ |= 0x100;
                continue;
              case 90:
                m = (MysqlxDatatypes.Scalar)input.readMessage(MysqlxDatatypes.Scalar.PARSER, extensionRegistry);
                if (this.argsBuilder_ == null) {
                  ensureArgsIsMutable();
                  this.args_.add(m);
                  continue;
                } 
                this.argsBuilder_.addMessage((AbstractMessage)m);
                continue;
              case 96:
                tmpRaw = input.readEnum();
                rowLock = MysqlxCrud.Find.RowLock.forNumber(tmpRaw);
                if (rowLock == null) {
                  mergeUnknownVarintField(12, tmpRaw);
                  continue;
                } 
                this.locking_ = tmpRaw;
                this.bitField0_ |= 0x200;
                continue;
              case 104:
                tmpRaw = input.readEnum();
                tmpValue = MysqlxCrud.Find.RowLockOptions.forNumber(tmpRaw);
                if (tmpValue == null) {
                  mergeUnknownVarintField(13, tmpRaw);
                  continue;
                } 
                this.lockingOptions_ = tmpRaw;
                this.bitField0_ |= 0x400;
                continue;
              case 114:
                input.readMessage((MessageLite.Builder)getLimitExprFieldBuilder().getBuilder(), extensionRegistry);
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
      
      public boolean hasCollection() {
        return ((this.bitField0_ & 0x1) != 0);
      }
      
      public MysqlxCrud.Collection getCollection() {
        if (this.collectionBuilder_ == null)
          return (this.collection_ == null) ? MysqlxCrud.Collection.getDefaultInstance() : this.collection_; 
        return (MysqlxCrud.Collection)this.collectionBuilder_.getMessage();
      }
      
      public Builder setCollection(MysqlxCrud.Collection value) {
        if (this.collectionBuilder_ == null) {
          if (value == null)
            throw new NullPointerException(); 
          this.collection_ = value;
          onChanged();
        } else {
          this.collectionBuilder_.setMessage((AbstractMessage)value);
        } 
        this.bitField0_ |= 0x1;
        return this;
      }
      
      public Builder setCollection(MysqlxCrud.Collection.Builder builderForValue) {
        if (this.collectionBuilder_ == null) {
          this.collection_ = builderForValue.build();
          onChanged();
        } else {
          this.collectionBuilder_.setMessage((AbstractMessage)builderForValue.build());
        } 
        this.bitField0_ |= 0x1;
        return this;
      }
      
      public Builder mergeCollection(MysqlxCrud.Collection value) {
        if (this.collectionBuilder_ == null) {
          if ((this.bitField0_ & 0x1) != 0 && this.collection_ != null && this.collection_ != MysqlxCrud.Collection.getDefaultInstance()) {
            this.collection_ = MysqlxCrud.Collection.newBuilder(this.collection_).mergeFrom(value).buildPartial();
          } else {
            this.collection_ = value;
          } 
          onChanged();
        } else {
          this.collectionBuilder_.mergeFrom((AbstractMessage)value);
        } 
        this.bitField0_ |= 0x1;
        return this;
      }
      
      public Builder clearCollection() {
        if (this.collectionBuilder_ == null) {
          this.collection_ = null;
          onChanged();
        } else {
          this.collectionBuilder_.clear();
        } 
        this.bitField0_ &= 0xFFFFFFFE;
        return this;
      }
      
      public MysqlxCrud.Collection.Builder getCollectionBuilder() {
        this.bitField0_ |= 0x1;
        onChanged();
        return (MysqlxCrud.Collection.Builder)getCollectionFieldBuilder().getBuilder();
      }
      
      public MysqlxCrud.CollectionOrBuilder getCollectionOrBuilder() {
        if (this.collectionBuilder_ != null)
          return (MysqlxCrud.CollectionOrBuilder)this.collectionBuilder_.getMessageOrBuilder(); 
        return (this.collection_ == null) ? MysqlxCrud.Collection.getDefaultInstance() : this.collection_;
      }
      
      private SingleFieldBuilderV3<MysqlxCrud.Collection, MysqlxCrud.Collection.Builder, MysqlxCrud.CollectionOrBuilder> getCollectionFieldBuilder() {
        if (this.collectionBuilder_ == null) {
          this.collectionBuilder_ = new SingleFieldBuilderV3((AbstractMessage)getCollection(), (AbstractMessage.BuilderParent)getParentForChildren(), isClean());
          this.collection_ = null;
        } 
        return this.collectionBuilder_;
      }
      
      public boolean hasDataModel() {
        return ((this.bitField0_ & 0x2) != 0);
      }
      
      public MysqlxCrud.DataModel getDataModel() {
        MysqlxCrud.DataModel result = MysqlxCrud.DataModel.valueOf(this.dataModel_);
        return (result == null) ? MysqlxCrud.DataModel.DOCUMENT : result;
      }
      
      public Builder setDataModel(MysqlxCrud.DataModel value) {
        if (value == null)
          throw new NullPointerException(); 
        this.bitField0_ |= 0x2;
        this.dataModel_ = value.getNumber();
        onChanged();
        return this;
      }
      
      public Builder clearDataModel() {
        this.bitField0_ &= 0xFFFFFFFD;
        this.dataModel_ = 1;
        onChanged();
        return this;
      }
      
      private void ensureProjectionIsMutable() {
        if ((this.bitField0_ & 0x4) == 0) {
          this.projection_ = new ArrayList<>(this.projection_);
          this.bitField0_ |= 0x4;
        } 
      }
      
      public List<MysqlxCrud.Projection> getProjectionList() {
        if (this.projectionBuilder_ == null)
          return Collections.unmodifiableList(this.projection_); 
        return this.projectionBuilder_.getMessageList();
      }
      
      public int getProjectionCount() {
        if (this.projectionBuilder_ == null)
          return this.projection_.size(); 
        return this.projectionBuilder_.getCount();
      }
      
      public MysqlxCrud.Projection getProjection(int index) {
        if (this.projectionBuilder_ == null)
          return this.projection_.get(index); 
        return (MysqlxCrud.Projection)this.projectionBuilder_.getMessage(index);
      }
      
      public Builder setProjection(int index, MysqlxCrud.Projection value) {
        if (this.projectionBuilder_ == null) {
          if (value == null)
            throw new NullPointerException(); 
          ensureProjectionIsMutable();
          this.projection_.set(index, value);
          onChanged();
        } else {
          this.projectionBuilder_.setMessage(index, (AbstractMessage)value);
        } 
        return this;
      }
      
      public Builder setProjection(int index, MysqlxCrud.Projection.Builder builderForValue) {
        if (this.projectionBuilder_ == null) {
          ensureProjectionIsMutable();
          this.projection_.set(index, builderForValue.build());
          onChanged();
        } else {
          this.projectionBuilder_.setMessage(index, (AbstractMessage)builderForValue.build());
        } 
        return this;
      }
      
      public Builder addProjection(MysqlxCrud.Projection value) {
        if (this.projectionBuilder_ == null) {
          if (value == null)
            throw new NullPointerException(); 
          ensureProjectionIsMutable();
          this.projection_.add(value);
          onChanged();
        } else {
          this.projectionBuilder_.addMessage((AbstractMessage)value);
        } 
        return this;
      }
      
      public Builder addProjection(int index, MysqlxCrud.Projection value) {
        if (this.projectionBuilder_ == null) {
          if (value == null)
            throw new NullPointerException(); 
          ensureProjectionIsMutable();
          this.projection_.add(index, value);
          onChanged();
        } else {
          this.projectionBuilder_.addMessage(index, (AbstractMessage)value);
        } 
        return this;
      }
      
      public Builder addProjection(MysqlxCrud.Projection.Builder builderForValue) {
        if (this.projectionBuilder_ == null) {
          ensureProjectionIsMutable();
          this.projection_.add(builderForValue.build());
          onChanged();
        } else {
          this.projectionBuilder_.addMessage((AbstractMessage)builderForValue.build());
        } 
        return this;
      }
      
      public Builder addProjection(int index, MysqlxCrud.Projection.Builder builderForValue) {
        if (this.projectionBuilder_ == null) {
          ensureProjectionIsMutable();
          this.projection_.add(index, builderForValue.build());
          onChanged();
        } else {
          this.projectionBuilder_.addMessage(index, (AbstractMessage)builderForValue.build());
        } 
        return this;
      }
      
      public Builder addAllProjection(Iterable<? extends MysqlxCrud.Projection> values) {
        if (this.projectionBuilder_ == null) {
          ensureProjectionIsMutable();
          AbstractMessageLite.Builder.addAll(values, this.projection_);
          onChanged();
        } else {
          this.projectionBuilder_.addAllMessages(values);
        } 
        return this;
      }
      
      public Builder clearProjection() {
        if (this.projectionBuilder_ == null) {
          this.projection_ = Collections.emptyList();
          this.bitField0_ &= 0xFFFFFFFB;
          onChanged();
        } else {
          this.projectionBuilder_.clear();
        } 
        return this;
      }
      
      public Builder removeProjection(int index) {
        if (this.projectionBuilder_ == null) {
          ensureProjectionIsMutable();
          this.projection_.remove(index);
          onChanged();
        } else {
          this.projectionBuilder_.remove(index);
        } 
        return this;
      }
      
      public MysqlxCrud.Projection.Builder getProjectionBuilder(int index) {
        return (MysqlxCrud.Projection.Builder)getProjectionFieldBuilder().getBuilder(index);
      }
      
      public MysqlxCrud.ProjectionOrBuilder getProjectionOrBuilder(int index) {
        if (this.projectionBuilder_ == null)
          return this.projection_.get(index); 
        return (MysqlxCrud.ProjectionOrBuilder)this.projectionBuilder_.getMessageOrBuilder(index);
      }
      
      public List<? extends MysqlxCrud.ProjectionOrBuilder> getProjectionOrBuilderList() {
        if (this.projectionBuilder_ != null)
          return this.projectionBuilder_.getMessageOrBuilderList(); 
        return Collections.unmodifiableList((List)this.projection_);
      }
      
      public MysqlxCrud.Projection.Builder addProjectionBuilder() {
        return (MysqlxCrud.Projection.Builder)getProjectionFieldBuilder().addBuilder((AbstractMessage)MysqlxCrud.Projection.getDefaultInstance());
      }
      
      public MysqlxCrud.Projection.Builder addProjectionBuilder(int index) {
        return (MysqlxCrud.Projection.Builder)getProjectionFieldBuilder().addBuilder(index, (AbstractMessage)MysqlxCrud.Projection.getDefaultInstance());
      }
      
      public List<MysqlxCrud.Projection.Builder> getProjectionBuilderList() {
        return getProjectionFieldBuilder().getBuilderList();
      }
      
      private RepeatedFieldBuilderV3<MysqlxCrud.Projection, MysqlxCrud.Projection.Builder, MysqlxCrud.ProjectionOrBuilder> getProjectionFieldBuilder() {
        if (this.projectionBuilder_ == null) {
          this.projectionBuilder_ = new RepeatedFieldBuilderV3(this.projection_, ((this.bitField0_ & 0x4) != 0), (AbstractMessage.BuilderParent)getParentForChildren(), isClean());
          this.projection_ = null;
        } 
        return this.projectionBuilder_;
      }
      
      private void ensureArgsIsMutable() {
        if ((this.bitField0_ & 0x8) == 0) {
          this.args_ = new ArrayList<>(this.args_);
          this.bitField0_ |= 0x8;
        } 
      }
      
      public List<MysqlxDatatypes.Scalar> getArgsList() {
        if (this.argsBuilder_ == null)
          return Collections.unmodifiableList(this.args_); 
        return this.argsBuilder_.getMessageList();
      }
      
      public int getArgsCount() {
        if (this.argsBuilder_ == null)
          return this.args_.size(); 
        return this.argsBuilder_.getCount();
      }
      
      public MysqlxDatatypes.Scalar getArgs(int index) {
        if (this.argsBuilder_ == null)
          return this.args_.get(index); 
        return (MysqlxDatatypes.Scalar)this.argsBuilder_.getMessage(index);
      }
      
      public Builder setArgs(int index, MysqlxDatatypes.Scalar value) {
        if (this.argsBuilder_ == null) {
          if (value == null)
            throw new NullPointerException(); 
          ensureArgsIsMutable();
          this.args_.set(index, value);
          onChanged();
        } else {
          this.argsBuilder_.setMessage(index, (AbstractMessage)value);
        } 
        return this;
      }
      
      public Builder setArgs(int index, MysqlxDatatypes.Scalar.Builder builderForValue) {
        if (this.argsBuilder_ == null) {
          ensureArgsIsMutable();
          this.args_.set(index, builderForValue.build());
          onChanged();
        } else {
          this.argsBuilder_.setMessage(index, (AbstractMessage)builderForValue.build());
        } 
        return this;
      }
      
      public Builder addArgs(MysqlxDatatypes.Scalar value) {
        if (this.argsBuilder_ == null) {
          if (value == null)
            throw new NullPointerException(); 
          ensureArgsIsMutable();
          this.args_.add(value);
          onChanged();
        } else {
          this.argsBuilder_.addMessage((AbstractMessage)value);
        } 
        return this;
      }
      
      public Builder addArgs(int index, MysqlxDatatypes.Scalar value) {
        if (this.argsBuilder_ == null) {
          if (value == null)
            throw new NullPointerException(); 
          ensureArgsIsMutable();
          this.args_.add(index, value);
          onChanged();
        } else {
          this.argsBuilder_.addMessage(index, (AbstractMessage)value);
        } 
        return this;
      }
      
      public Builder addArgs(MysqlxDatatypes.Scalar.Builder builderForValue) {
        if (this.argsBuilder_ == null) {
          ensureArgsIsMutable();
          this.args_.add(builderForValue.build());
          onChanged();
        } else {
          this.argsBuilder_.addMessage((AbstractMessage)builderForValue.build());
        } 
        return this;
      }
      
      public Builder addArgs(int index, MysqlxDatatypes.Scalar.Builder builderForValue) {
        if (this.argsBuilder_ == null) {
          ensureArgsIsMutable();
          this.args_.add(index, builderForValue.build());
          onChanged();
        } else {
          this.argsBuilder_.addMessage(index, (AbstractMessage)builderForValue.build());
        } 
        return this;
      }
      
      public Builder addAllArgs(Iterable<? extends MysqlxDatatypes.Scalar> values) {
        if (this.argsBuilder_ == null) {
          ensureArgsIsMutable();
          AbstractMessageLite.Builder.addAll(values, this.args_);
          onChanged();
        } else {
          this.argsBuilder_.addAllMessages(values);
        } 
        return this;
      }
      
      public Builder clearArgs() {
        if (this.argsBuilder_ == null) {
          this.args_ = Collections.emptyList();
          this.bitField0_ &= 0xFFFFFFF7;
          onChanged();
        } else {
          this.argsBuilder_.clear();
        } 
        return this;
      }
      
      public Builder removeArgs(int index) {
        if (this.argsBuilder_ == null) {
          ensureArgsIsMutable();
          this.args_.remove(index);
          onChanged();
        } else {
          this.argsBuilder_.remove(index);
        } 
        return this;
      }
      
      public MysqlxDatatypes.Scalar.Builder getArgsBuilder(int index) {
        return (MysqlxDatatypes.Scalar.Builder)getArgsFieldBuilder().getBuilder(index);
      }
      
      public MysqlxDatatypes.ScalarOrBuilder getArgsOrBuilder(int index) {
        if (this.argsBuilder_ == null)
          return this.args_.get(index); 
        return (MysqlxDatatypes.ScalarOrBuilder)this.argsBuilder_.getMessageOrBuilder(index);
      }
      
      public List<? extends MysqlxDatatypes.ScalarOrBuilder> getArgsOrBuilderList() {
        if (this.argsBuilder_ != null)
          return this.argsBuilder_.getMessageOrBuilderList(); 
        return Collections.unmodifiableList((List)this.args_);
      }
      
      public MysqlxDatatypes.Scalar.Builder addArgsBuilder() {
        return (MysqlxDatatypes.Scalar.Builder)getArgsFieldBuilder().addBuilder((AbstractMessage)MysqlxDatatypes.Scalar.getDefaultInstance());
      }
      
      public MysqlxDatatypes.Scalar.Builder addArgsBuilder(int index) {
        return (MysqlxDatatypes.Scalar.Builder)getArgsFieldBuilder().addBuilder(index, (AbstractMessage)MysqlxDatatypes.Scalar.getDefaultInstance());
      }
      
      public List<MysqlxDatatypes.Scalar.Builder> getArgsBuilderList() {
        return getArgsFieldBuilder().getBuilderList();
      }
      
      private RepeatedFieldBuilderV3<MysqlxDatatypes.Scalar, MysqlxDatatypes.Scalar.Builder, MysqlxDatatypes.ScalarOrBuilder> getArgsFieldBuilder() {
        if (this.argsBuilder_ == null) {
          this.argsBuilder_ = new RepeatedFieldBuilderV3(this.args_, ((this.bitField0_ & 0x8) != 0), (AbstractMessage.BuilderParent)getParentForChildren(), isClean());
          this.args_ = null;
        } 
        return this.argsBuilder_;
      }
      
      public boolean hasCriteria() {
        return ((this.bitField0_ & 0x10) != 0);
      }
      
      public MysqlxExpr.Expr getCriteria() {
        if (this.criteriaBuilder_ == null)
          return (this.criteria_ == null) ? MysqlxExpr.Expr.getDefaultInstance() : this.criteria_; 
        return (MysqlxExpr.Expr)this.criteriaBuilder_.getMessage();
      }
      
      public Builder setCriteria(MysqlxExpr.Expr value) {
        if (this.criteriaBuilder_ == null) {
          if (value == null)
            throw new NullPointerException(); 
          this.criteria_ = value;
          onChanged();
        } else {
          this.criteriaBuilder_.setMessage((AbstractMessage)value);
        } 
        this.bitField0_ |= 0x10;
        return this;
      }
      
      public Builder setCriteria(MysqlxExpr.Expr.Builder builderForValue) {
        if (this.criteriaBuilder_ == null) {
          this.criteria_ = builderForValue.build();
          onChanged();
        } else {
          this.criteriaBuilder_.setMessage((AbstractMessage)builderForValue.build());
        } 
        this.bitField0_ |= 0x10;
        return this;
      }
      
      public Builder mergeCriteria(MysqlxExpr.Expr value) {
        if (this.criteriaBuilder_ == null) {
          if ((this.bitField0_ & 0x10) != 0 && this.criteria_ != null && this.criteria_ != MysqlxExpr.Expr.getDefaultInstance()) {
            this.criteria_ = MysqlxExpr.Expr.newBuilder(this.criteria_).mergeFrom(value).buildPartial();
          } else {
            this.criteria_ = value;
          } 
          onChanged();
        } else {
          this.criteriaBuilder_.mergeFrom((AbstractMessage)value);
        } 
        this.bitField0_ |= 0x10;
        return this;
      }
      
      public Builder clearCriteria() {
        if (this.criteriaBuilder_ == null) {
          this.criteria_ = null;
          onChanged();
        } else {
          this.criteriaBuilder_.clear();
        } 
        this.bitField0_ &= 0xFFFFFFEF;
        return this;
      }
      
      public MysqlxExpr.Expr.Builder getCriteriaBuilder() {
        this.bitField0_ |= 0x10;
        onChanged();
        return (MysqlxExpr.Expr.Builder)getCriteriaFieldBuilder().getBuilder();
      }
      
      public MysqlxExpr.ExprOrBuilder getCriteriaOrBuilder() {
        if (this.criteriaBuilder_ != null)
          return (MysqlxExpr.ExprOrBuilder)this.criteriaBuilder_.getMessageOrBuilder(); 
        return (this.criteria_ == null) ? MysqlxExpr.Expr.getDefaultInstance() : this.criteria_;
      }
      
      private SingleFieldBuilderV3<MysqlxExpr.Expr, MysqlxExpr.Expr.Builder, MysqlxExpr.ExprOrBuilder> getCriteriaFieldBuilder() {
        if (this.criteriaBuilder_ == null) {
          this.criteriaBuilder_ = new SingleFieldBuilderV3((AbstractMessage)getCriteria(), (AbstractMessage.BuilderParent)getParentForChildren(), isClean());
          this.criteria_ = null;
        } 
        return this.criteriaBuilder_;
      }
      
      public boolean hasLimit() {
        return ((this.bitField0_ & 0x20) != 0);
      }
      
      public MysqlxCrud.Limit getLimit() {
        if (this.limitBuilder_ == null)
          return (this.limit_ == null) ? MysqlxCrud.Limit.getDefaultInstance() : this.limit_; 
        return (MysqlxCrud.Limit)this.limitBuilder_.getMessage();
      }
      
      public Builder setLimit(MysqlxCrud.Limit value) {
        if (this.limitBuilder_ == null) {
          if (value == null)
            throw new NullPointerException(); 
          this.limit_ = value;
          onChanged();
        } else {
          this.limitBuilder_.setMessage((AbstractMessage)value);
        } 
        this.bitField0_ |= 0x20;
        return this;
      }
      
      public Builder setLimit(MysqlxCrud.Limit.Builder builderForValue) {
        if (this.limitBuilder_ == null) {
          this.limit_ = builderForValue.build();
          onChanged();
        } else {
          this.limitBuilder_.setMessage((AbstractMessage)builderForValue.build());
        } 
        this.bitField0_ |= 0x20;
        return this;
      }
      
      public Builder mergeLimit(MysqlxCrud.Limit value) {
        if (this.limitBuilder_ == null) {
          if ((this.bitField0_ & 0x20) != 0 && this.limit_ != null && this.limit_ != MysqlxCrud.Limit.getDefaultInstance()) {
            this.limit_ = MysqlxCrud.Limit.newBuilder(this.limit_).mergeFrom(value).buildPartial();
          } else {
            this.limit_ = value;
          } 
          onChanged();
        } else {
          this.limitBuilder_.mergeFrom((AbstractMessage)value);
        } 
        this.bitField0_ |= 0x20;
        return this;
      }
      
      public Builder clearLimit() {
        if (this.limitBuilder_ == null) {
          this.limit_ = null;
          onChanged();
        } else {
          this.limitBuilder_.clear();
        } 
        this.bitField0_ &= 0xFFFFFFDF;
        return this;
      }
      
      public MysqlxCrud.Limit.Builder getLimitBuilder() {
        this.bitField0_ |= 0x20;
        onChanged();
        return (MysqlxCrud.Limit.Builder)getLimitFieldBuilder().getBuilder();
      }
      
      public MysqlxCrud.LimitOrBuilder getLimitOrBuilder() {
        if (this.limitBuilder_ != null)
          return (MysqlxCrud.LimitOrBuilder)this.limitBuilder_.getMessageOrBuilder(); 
        return (this.limit_ == null) ? MysqlxCrud.Limit.getDefaultInstance() : this.limit_;
      }
      
      private SingleFieldBuilderV3<MysqlxCrud.Limit, MysqlxCrud.Limit.Builder, MysqlxCrud.LimitOrBuilder> getLimitFieldBuilder() {
        if (this.limitBuilder_ == null) {
          this.limitBuilder_ = new SingleFieldBuilderV3((AbstractMessage)getLimit(), (AbstractMessage.BuilderParent)getParentForChildren(), isClean());
          this.limit_ = null;
        } 
        return this.limitBuilder_;
      }
      
      private void ensureOrderIsMutable() {
        if ((this.bitField0_ & 0x40) == 0) {
          this.order_ = new ArrayList<>(this.order_);
          this.bitField0_ |= 0x40;
        } 
      }
      
      public List<MysqlxCrud.Order> getOrderList() {
        if (this.orderBuilder_ == null)
          return Collections.unmodifiableList(this.order_); 
        return this.orderBuilder_.getMessageList();
      }
      
      public int getOrderCount() {
        if (this.orderBuilder_ == null)
          return this.order_.size(); 
        return this.orderBuilder_.getCount();
      }
      
      public MysqlxCrud.Order getOrder(int index) {
        if (this.orderBuilder_ == null)
          return this.order_.get(index); 
        return (MysqlxCrud.Order)this.orderBuilder_.getMessage(index);
      }
      
      public Builder setOrder(int index, MysqlxCrud.Order value) {
        if (this.orderBuilder_ == null) {
          if (value == null)
            throw new NullPointerException(); 
          ensureOrderIsMutable();
          this.order_.set(index, value);
          onChanged();
        } else {
          this.orderBuilder_.setMessage(index, (AbstractMessage)value);
        } 
        return this;
      }
      
      public Builder setOrder(int index, MysqlxCrud.Order.Builder builderForValue) {
        if (this.orderBuilder_ == null) {
          ensureOrderIsMutable();
          this.order_.set(index, builderForValue.build());
          onChanged();
        } else {
          this.orderBuilder_.setMessage(index, (AbstractMessage)builderForValue.build());
        } 
        return this;
      }
      
      public Builder addOrder(MysqlxCrud.Order value) {
        if (this.orderBuilder_ == null) {
          if (value == null)
            throw new NullPointerException(); 
          ensureOrderIsMutable();
          this.order_.add(value);
          onChanged();
        } else {
          this.orderBuilder_.addMessage((AbstractMessage)value);
        } 
        return this;
      }
      
      public Builder addOrder(int index, MysqlxCrud.Order value) {
        if (this.orderBuilder_ == null) {
          if (value == null)
            throw new NullPointerException(); 
          ensureOrderIsMutable();
          this.order_.add(index, value);
          onChanged();
        } else {
          this.orderBuilder_.addMessage(index, (AbstractMessage)value);
        } 
        return this;
      }
      
      public Builder addOrder(MysqlxCrud.Order.Builder builderForValue) {
        if (this.orderBuilder_ == null) {
          ensureOrderIsMutable();
          this.order_.add(builderForValue.build());
          onChanged();
        } else {
          this.orderBuilder_.addMessage((AbstractMessage)builderForValue.build());
        } 
        return this;
      }
      
      public Builder addOrder(int index, MysqlxCrud.Order.Builder builderForValue) {
        if (this.orderBuilder_ == null) {
          ensureOrderIsMutable();
          this.order_.add(index, builderForValue.build());
          onChanged();
        } else {
          this.orderBuilder_.addMessage(index, (AbstractMessage)builderForValue.build());
        } 
        return this;
      }
      
      public Builder addAllOrder(Iterable<? extends MysqlxCrud.Order> values) {
        if (this.orderBuilder_ == null) {
          ensureOrderIsMutable();
          AbstractMessageLite.Builder.addAll(values, this.order_);
          onChanged();
        } else {
          this.orderBuilder_.addAllMessages(values);
        } 
        return this;
      }
      
      public Builder clearOrder() {
        if (this.orderBuilder_ == null) {
          this.order_ = Collections.emptyList();
          this.bitField0_ &= 0xFFFFFFBF;
          onChanged();
        } else {
          this.orderBuilder_.clear();
        } 
        return this;
      }
      
      public Builder removeOrder(int index) {
        if (this.orderBuilder_ == null) {
          ensureOrderIsMutable();
          this.order_.remove(index);
          onChanged();
        } else {
          this.orderBuilder_.remove(index);
        } 
        return this;
      }
      
      public MysqlxCrud.Order.Builder getOrderBuilder(int index) {
        return (MysqlxCrud.Order.Builder)getOrderFieldBuilder().getBuilder(index);
      }
      
      public MysqlxCrud.OrderOrBuilder getOrderOrBuilder(int index) {
        if (this.orderBuilder_ == null)
          return this.order_.get(index); 
        return (MysqlxCrud.OrderOrBuilder)this.orderBuilder_.getMessageOrBuilder(index);
      }
      
      public List<? extends MysqlxCrud.OrderOrBuilder> getOrderOrBuilderList() {
        if (this.orderBuilder_ != null)
          return this.orderBuilder_.getMessageOrBuilderList(); 
        return Collections.unmodifiableList((List)this.order_);
      }
      
      public MysqlxCrud.Order.Builder addOrderBuilder() {
        return (MysqlxCrud.Order.Builder)getOrderFieldBuilder().addBuilder((AbstractMessage)MysqlxCrud.Order.getDefaultInstance());
      }
      
      public MysqlxCrud.Order.Builder addOrderBuilder(int index) {
        return (MysqlxCrud.Order.Builder)getOrderFieldBuilder().addBuilder(index, (AbstractMessage)MysqlxCrud.Order.getDefaultInstance());
      }
      
      public List<MysqlxCrud.Order.Builder> getOrderBuilderList() {
        return getOrderFieldBuilder().getBuilderList();
      }
      
      private RepeatedFieldBuilderV3<MysqlxCrud.Order, MysqlxCrud.Order.Builder, MysqlxCrud.OrderOrBuilder> getOrderFieldBuilder() {
        if (this.orderBuilder_ == null) {
          this.orderBuilder_ = new RepeatedFieldBuilderV3(this.order_, ((this.bitField0_ & 0x40) != 0), (AbstractMessage.BuilderParent)getParentForChildren(), isClean());
          this.order_ = null;
        } 
        return this.orderBuilder_;
      }
      
      private void ensureGroupingIsMutable() {
        if ((this.bitField0_ & 0x80) == 0) {
          this.grouping_ = new ArrayList<>(this.grouping_);
          this.bitField0_ |= 0x80;
        } 
      }
      
      public List<MysqlxExpr.Expr> getGroupingList() {
        if (this.groupingBuilder_ == null)
          return Collections.unmodifiableList(this.grouping_); 
        return this.groupingBuilder_.getMessageList();
      }
      
      public int getGroupingCount() {
        if (this.groupingBuilder_ == null)
          return this.grouping_.size(); 
        return this.groupingBuilder_.getCount();
      }
      
      public MysqlxExpr.Expr getGrouping(int index) {
        if (this.groupingBuilder_ == null)
          return this.grouping_.get(index); 
        return (MysqlxExpr.Expr)this.groupingBuilder_.getMessage(index);
      }
      
      public Builder setGrouping(int index, MysqlxExpr.Expr value) {
        if (this.groupingBuilder_ == null) {
          if (value == null)
            throw new NullPointerException(); 
          ensureGroupingIsMutable();
          this.grouping_.set(index, value);
          onChanged();
        } else {
          this.groupingBuilder_.setMessage(index, (AbstractMessage)value);
        } 
        return this;
      }
      
      public Builder setGrouping(int index, MysqlxExpr.Expr.Builder builderForValue) {
        if (this.groupingBuilder_ == null) {
          ensureGroupingIsMutable();
          this.grouping_.set(index, builderForValue.build());
          onChanged();
        } else {
          this.groupingBuilder_.setMessage(index, (AbstractMessage)builderForValue.build());
        } 
        return this;
      }
      
      public Builder addGrouping(MysqlxExpr.Expr value) {
        if (this.groupingBuilder_ == null) {
          if (value == null)
            throw new NullPointerException(); 
          ensureGroupingIsMutable();
          this.grouping_.add(value);
          onChanged();
        } else {
          this.groupingBuilder_.addMessage((AbstractMessage)value);
        } 
        return this;
      }
      
      public Builder addGrouping(int index, MysqlxExpr.Expr value) {
        if (this.groupingBuilder_ == null) {
          if (value == null)
            throw new NullPointerException(); 
          ensureGroupingIsMutable();
          this.grouping_.add(index, value);
          onChanged();
        } else {
          this.groupingBuilder_.addMessage(index, (AbstractMessage)value);
        } 
        return this;
      }
      
      public Builder addGrouping(MysqlxExpr.Expr.Builder builderForValue) {
        if (this.groupingBuilder_ == null) {
          ensureGroupingIsMutable();
          this.grouping_.add(builderForValue.build());
          onChanged();
        } else {
          this.groupingBuilder_.addMessage((AbstractMessage)builderForValue.build());
        } 
        return this;
      }
      
      public Builder addGrouping(int index, MysqlxExpr.Expr.Builder builderForValue) {
        if (this.groupingBuilder_ == null) {
          ensureGroupingIsMutable();
          this.grouping_.add(index, builderForValue.build());
          onChanged();
        } else {
          this.groupingBuilder_.addMessage(index, (AbstractMessage)builderForValue.build());
        } 
        return this;
      }
      
      public Builder addAllGrouping(Iterable<? extends MysqlxExpr.Expr> values) {
        if (this.groupingBuilder_ == null) {
          ensureGroupingIsMutable();
          AbstractMessageLite.Builder.addAll(values, this.grouping_);
          onChanged();
        } else {
          this.groupingBuilder_.addAllMessages(values);
        } 
        return this;
      }
      
      public Builder clearGrouping() {
        if (this.groupingBuilder_ == null) {
          this.grouping_ = Collections.emptyList();
          this.bitField0_ &= 0xFFFFFF7F;
          onChanged();
        } else {
          this.groupingBuilder_.clear();
        } 
        return this;
      }
      
      public Builder removeGrouping(int index) {
        if (this.groupingBuilder_ == null) {
          ensureGroupingIsMutable();
          this.grouping_.remove(index);
          onChanged();
        } else {
          this.groupingBuilder_.remove(index);
        } 
        return this;
      }
      
      public MysqlxExpr.Expr.Builder getGroupingBuilder(int index) {
        return (MysqlxExpr.Expr.Builder)getGroupingFieldBuilder().getBuilder(index);
      }
      
      public MysqlxExpr.ExprOrBuilder getGroupingOrBuilder(int index) {
        if (this.groupingBuilder_ == null)
          return this.grouping_.get(index); 
        return (MysqlxExpr.ExprOrBuilder)this.groupingBuilder_.getMessageOrBuilder(index);
      }
      
      public List<? extends MysqlxExpr.ExprOrBuilder> getGroupingOrBuilderList() {
        if (this.groupingBuilder_ != null)
          return this.groupingBuilder_.getMessageOrBuilderList(); 
        return Collections.unmodifiableList((List)this.grouping_);
      }
      
      public MysqlxExpr.Expr.Builder addGroupingBuilder() {
        return (MysqlxExpr.Expr.Builder)getGroupingFieldBuilder().addBuilder((AbstractMessage)MysqlxExpr.Expr.getDefaultInstance());
      }
      
      public MysqlxExpr.Expr.Builder addGroupingBuilder(int index) {
        return (MysqlxExpr.Expr.Builder)getGroupingFieldBuilder().addBuilder(index, (AbstractMessage)MysqlxExpr.Expr.getDefaultInstance());
      }
      
      public List<MysqlxExpr.Expr.Builder> getGroupingBuilderList() {
        return getGroupingFieldBuilder().getBuilderList();
      }
      
      private RepeatedFieldBuilderV3<MysqlxExpr.Expr, MysqlxExpr.Expr.Builder, MysqlxExpr.ExprOrBuilder> getGroupingFieldBuilder() {
        if (this.groupingBuilder_ == null) {
          this.groupingBuilder_ = new RepeatedFieldBuilderV3(this.grouping_, ((this.bitField0_ & 0x80) != 0), (AbstractMessage.BuilderParent)getParentForChildren(), isClean());
          this.grouping_ = null;
        } 
        return this.groupingBuilder_;
      }
      
      public boolean hasGroupingCriteria() {
        return ((this.bitField0_ & 0x100) != 0);
      }
      
      public MysqlxExpr.Expr getGroupingCriteria() {
        if (this.groupingCriteriaBuilder_ == null)
          return (this.groupingCriteria_ == null) ? MysqlxExpr.Expr.getDefaultInstance() : this.groupingCriteria_; 
        return (MysqlxExpr.Expr)this.groupingCriteriaBuilder_.getMessage();
      }
      
      public Builder setGroupingCriteria(MysqlxExpr.Expr value) {
        if (this.groupingCriteriaBuilder_ == null) {
          if (value == null)
            throw new NullPointerException(); 
          this.groupingCriteria_ = value;
          onChanged();
        } else {
          this.groupingCriteriaBuilder_.setMessage((AbstractMessage)value);
        } 
        this.bitField0_ |= 0x100;
        return this;
      }
      
      public Builder setGroupingCriteria(MysqlxExpr.Expr.Builder builderForValue) {
        if (this.groupingCriteriaBuilder_ == null) {
          this.groupingCriteria_ = builderForValue.build();
          onChanged();
        } else {
          this.groupingCriteriaBuilder_.setMessage((AbstractMessage)builderForValue.build());
        } 
        this.bitField0_ |= 0x100;
        return this;
      }
      
      public Builder mergeGroupingCriteria(MysqlxExpr.Expr value) {
        if (this.groupingCriteriaBuilder_ == null) {
          if ((this.bitField0_ & 0x100) != 0 && this.groupingCriteria_ != null && this.groupingCriteria_ != MysqlxExpr.Expr.getDefaultInstance()) {
            this.groupingCriteria_ = MysqlxExpr.Expr.newBuilder(this.groupingCriteria_).mergeFrom(value).buildPartial();
          } else {
            this.groupingCriteria_ = value;
          } 
          onChanged();
        } else {
          this.groupingCriteriaBuilder_.mergeFrom((AbstractMessage)value);
        } 
        this.bitField0_ |= 0x100;
        return this;
      }
      
      public Builder clearGroupingCriteria() {
        if (this.groupingCriteriaBuilder_ == null) {
          this.groupingCriteria_ = null;
          onChanged();
        } else {
          this.groupingCriteriaBuilder_.clear();
        } 
        this.bitField0_ &= 0xFFFFFEFF;
        return this;
      }
      
      public MysqlxExpr.Expr.Builder getGroupingCriteriaBuilder() {
        this.bitField0_ |= 0x100;
        onChanged();
        return (MysqlxExpr.Expr.Builder)getGroupingCriteriaFieldBuilder().getBuilder();
      }
      
      public MysqlxExpr.ExprOrBuilder getGroupingCriteriaOrBuilder() {
        if (this.groupingCriteriaBuilder_ != null)
          return (MysqlxExpr.ExprOrBuilder)this.groupingCriteriaBuilder_.getMessageOrBuilder(); 
        return (this.groupingCriteria_ == null) ? MysqlxExpr.Expr.getDefaultInstance() : this.groupingCriteria_;
      }
      
      private SingleFieldBuilderV3<MysqlxExpr.Expr, MysqlxExpr.Expr.Builder, MysqlxExpr.ExprOrBuilder> getGroupingCriteriaFieldBuilder() {
        if (this.groupingCriteriaBuilder_ == null) {
          this.groupingCriteriaBuilder_ = new SingleFieldBuilderV3((AbstractMessage)getGroupingCriteria(), (AbstractMessage.BuilderParent)getParentForChildren(), isClean());
          this.groupingCriteria_ = null;
        } 
        return this.groupingCriteriaBuilder_;
      }
      
      public boolean hasLocking() {
        return ((this.bitField0_ & 0x200) != 0);
      }
      
      public MysqlxCrud.Find.RowLock getLocking() {
        MysqlxCrud.Find.RowLock result = MysqlxCrud.Find.RowLock.valueOf(this.locking_);
        return (result == null) ? MysqlxCrud.Find.RowLock.SHARED_LOCK : result;
      }
      
      public Builder setLocking(MysqlxCrud.Find.RowLock value) {
        if (value == null)
          throw new NullPointerException(); 
        this.bitField0_ |= 0x200;
        this.locking_ = value.getNumber();
        onChanged();
        return this;
      }
      
      public Builder clearLocking() {
        this.bitField0_ &= 0xFFFFFDFF;
        this.locking_ = 1;
        onChanged();
        return this;
      }
      
      public boolean hasLockingOptions() {
        return ((this.bitField0_ & 0x400) != 0);
      }
      
      public MysqlxCrud.Find.RowLockOptions getLockingOptions() {
        MysqlxCrud.Find.RowLockOptions result = MysqlxCrud.Find.RowLockOptions.valueOf(this.lockingOptions_);
        return (result == null) ? MysqlxCrud.Find.RowLockOptions.NOWAIT : result;
      }
      
      public Builder setLockingOptions(MysqlxCrud.Find.RowLockOptions value) {
        if (value == null)
          throw new NullPointerException(); 
        this.bitField0_ |= 0x400;
        this.lockingOptions_ = value.getNumber();
        onChanged();
        return this;
      }
      
      public Builder clearLockingOptions() {
        this.bitField0_ &= 0xFFFFFBFF;
        this.lockingOptions_ = 1;
        onChanged();
        return this;
      }
      
      public boolean hasLimitExpr() {
        return ((this.bitField0_ & 0x800) != 0);
      }
      
      public MysqlxCrud.LimitExpr getLimitExpr() {
        if (this.limitExprBuilder_ == null)
          return (this.limitExpr_ == null) ? MysqlxCrud.LimitExpr.getDefaultInstance() : this.limitExpr_; 
        return (MysqlxCrud.LimitExpr)this.limitExprBuilder_.getMessage();
      }
      
      public Builder setLimitExpr(MysqlxCrud.LimitExpr value) {
        if (this.limitExprBuilder_ == null) {
          if (value == null)
            throw new NullPointerException(); 
          this.limitExpr_ = value;
          onChanged();
        } else {
          this.limitExprBuilder_.setMessage((AbstractMessage)value);
        } 
        this.bitField0_ |= 0x800;
        return this;
      }
      
      public Builder setLimitExpr(MysqlxCrud.LimitExpr.Builder builderForValue) {
        if (this.limitExprBuilder_ == null) {
          this.limitExpr_ = builderForValue.build();
          onChanged();
        } else {
          this.limitExprBuilder_.setMessage((AbstractMessage)builderForValue.build());
        } 
        this.bitField0_ |= 0x800;
        return this;
      }
      
      public Builder mergeLimitExpr(MysqlxCrud.LimitExpr value) {
        if (this.limitExprBuilder_ == null) {
          if ((this.bitField0_ & 0x800) != 0 && this.limitExpr_ != null && this.limitExpr_ != 
            
            MysqlxCrud.LimitExpr.getDefaultInstance()) {
            this
              .limitExpr_ = MysqlxCrud.LimitExpr.newBuilder(this.limitExpr_).mergeFrom(value).buildPartial();
          } else {
            this.limitExpr_ = value;
          } 
          onChanged();
        } else {
          this.limitExprBuilder_.mergeFrom((AbstractMessage)value);
        } 
        this.bitField0_ |= 0x800;
        return this;
      }
      
      public Builder clearLimitExpr() {
        if (this.limitExprBuilder_ == null) {
          this.limitExpr_ = null;
          onChanged();
        } else {
          this.limitExprBuilder_.clear();
        } 
        this.bitField0_ &= 0xFFFFF7FF;
        return this;
      }
      
      public MysqlxCrud.LimitExpr.Builder getLimitExprBuilder() {
        this.bitField0_ |= 0x800;
        onChanged();
        return (MysqlxCrud.LimitExpr.Builder)getLimitExprFieldBuilder().getBuilder();
      }
      
      public MysqlxCrud.LimitExprOrBuilder getLimitExprOrBuilder() {
        if (this.limitExprBuilder_ != null)
          return (MysqlxCrud.LimitExprOrBuilder)this.limitExprBuilder_.getMessageOrBuilder(); 
        return (this.limitExpr_ == null) ? 
          MysqlxCrud.LimitExpr.getDefaultInstance() : this.limitExpr_;
      }
      
      private SingleFieldBuilderV3<MysqlxCrud.LimitExpr, MysqlxCrud.LimitExpr.Builder, MysqlxCrud.LimitExprOrBuilder> getLimitExprFieldBuilder() {
        if (this.limitExprBuilder_ == null) {
          this
            
            .limitExprBuilder_ = new SingleFieldBuilderV3((AbstractMessage)getLimitExpr(), (AbstractMessage.BuilderParent)getParentForChildren(), isClean());
          this.limitExpr_ = null;
        } 
        return this.limitExprBuilder_;
      }
      
      public final Builder setUnknownFields(UnknownFieldSet unknownFields) {
        return (Builder)super.setUnknownFields(unknownFields);
      }
      
      public final Builder mergeUnknownFields(UnknownFieldSet unknownFields) {
        return (Builder)super.mergeUnknownFields(unknownFields);
      }
    }
    
    private static final Find DEFAULT_INSTANCE = new Find();
    
    public static Find getDefaultInstance() {
      return DEFAULT_INSTANCE;
    }
    
    @Deprecated
    public static final Parser<Find> PARSER = (Parser<Find>)new AbstractParser<Find>() {
        public MysqlxCrud.Find parsePartialFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
          MysqlxCrud.Find.Builder builder = MysqlxCrud.Find.newBuilder();
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
    
    public static Parser<Find> parser() {
      return PARSER;
    }
    
    public Parser<Find> getParserForType() {
      return PARSER;
    }
    
    public Find getDefaultInstanceForType() {
      return DEFAULT_INSTANCE;
    }
  }
  
  public static interface InsertOrBuilder extends MessageOrBuilder {
    boolean hasCollection();
    
    MysqlxCrud.Collection getCollection();
    
    MysqlxCrud.CollectionOrBuilder getCollectionOrBuilder();
    
    boolean hasDataModel();
    
    MysqlxCrud.DataModel getDataModel();
    
    List<MysqlxCrud.Column> getProjectionList();
    
    MysqlxCrud.Column getProjection(int param1Int);
    
    int getProjectionCount();
    
    List<? extends MysqlxCrud.ColumnOrBuilder> getProjectionOrBuilderList();
    
    MysqlxCrud.ColumnOrBuilder getProjectionOrBuilder(int param1Int);
    
    List<MysqlxCrud.Insert.TypedRow> getRowList();
    
    MysqlxCrud.Insert.TypedRow getRow(int param1Int);
    
    int getRowCount();
    
    List<? extends MysqlxCrud.Insert.TypedRowOrBuilder> getRowOrBuilderList();
    
    MysqlxCrud.Insert.TypedRowOrBuilder getRowOrBuilder(int param1Int);
    
    List<MysqlxDatatypes.Scalar> getArgsList();
    
    MysqlxDatatypes.Scalar getArgs(int param1Int);
    
    int getArgsCount();
    
    List<? extends MysqlxDatatypes.ScalarOrBuilder> getArgsOrBuilderList();
    
    MysqlxDatatypes.ScalarOrBuilder getArgsOrBuilder(int param1Int);
    
    boolean hasUpsert();
    
    boolean getUpsert();
  }
  
  public static final class Insert extends GeneratedMessageV3 implements InsertOrBuilder {
    private static final long serialVersionUID = 0L;
    
    private int bitField0_;
    
    public static final int COLLECTION_FIELD_NUMBER = 1;
    
    private MysqlxCrud.Collection collection_;
    
    public static final int DATA_MODEL_FIELD_NUMBER = 2;
    
    private int dataModel_;
    
    public static final int PROJECTION_FIELD_NUMBER = 3;
    
    private List<MysqlxCrud.Column> projection_;
    
    public static final int ROW_FIELD_NUMBER = 4;
    
    private List<TypedRow> row_;
    
    public static final int ARGS_FIELD_NUMBER = 5;
    
    private List<MysqlxDatatypes.Scalar> args_;
    
    public static final int UPSERT_FIELD_NUMBER = 6;
    
    private boolean upsert_;
    
    private byte memoizedIsInitialized;
    
    private Insert(GeneratedMessageV3.Builder<?> builder) {
      super(builder);
      this.memoizedIsInitialized = -1;
    }
    
    private Insert() {
      this.memoizedIsInitialized = -1;
      this.dataModel_ = 1;
      this.projection_ = Collections.emptyList();
      this.row_ = Collections.emptyList();
      this.args_ = Collections.emptyList();
    }
    
    protected Object newInstance(GeneratedMessageV3.UnusedPrivateParameter unused) {
      return new Insert();
    }
    
    public final UnknownFieldSet getUnknownFields() {
      return this.unknownFields;
    }
    
    public static final Descriptors.Descriptor getDescriptor() {
      return MysqlxCrud.internal_static_Mysqlx_Crud_Insert_descriptor;
    }
    
    protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() {
      return MysqlxCrud.internal_static_Mysqlx_Crud_Insert_fieldAccessorTable.ensureFieldAccessorsInitialized(Insert.class, Builder.class);
    }
    
    public static final class TypedRow extends GeneratedMessageV3 implements TypedRowOrBuilder {
      private static final long serialVersionUID = 0L;
      
      public static final int FIELD_FIELD_NUMBER = 1;
      
      private List<MysqlxExpr.Expr> field_;
      
      private byte memoizedIsInitialized;
      
      private TypedRow(GeneratedMessageV3.Builder<?> builder) {
        super(builder);
        this.memoizedIsInitialized = -1;
      }
      
      private TypedRow() {
        this.memoizedIsInitialized = -1;
        this.field_ = Collections.emptyList();
      }
      
      protected Object newInstance(GeneratedMessageV3.UnusedPrivateParameter unused) {
        return new TypedRow();
      }
      
      public final UnknownFieldSet getUnknownFields() {
        return this.unknownFields;
      }
      
      public static final Descriptors.Descriptor getDescriptor() {
        return MysqlxCrud.internal_static_Mysqlx_Crud_Insert_TypedRow_descriptor;
      }
      
      protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() {
        return MysqlxCrud.internal_static_Mysqlx_Crud_Insert_TypedRow_fieldAccessorTable.ensureFieldAccessorsInitialized(TypedRow.class, Builder.class);
      }
      
      public List<MysqlxExpr.Expr> getFieldList() {
        return this.field_;
      }
      
      public List<? extends MysqlxExpr.ExprOrBuilder> getFieldOrBuilderList() {
        return (List)this.field_;
      }
      
      public int getFieldCount() {
        return this.field_.size();
      }
      
      public MysqlxExpr.Expr getField(int index) {
        return this.field_.get(index);
      }
      
      public MysqlxExpr.ExprOrBuilder getFieldOrBuilder(int index) {
        return this.field_.get(index);
      }
      
      public final boolean isInitialized() {
        byte isInitialized = this.memoizedIsInitialized;
        if (isInitialized == 1)
          return true; 
        if (isInitialized == 0)
          return false; 
        for (int i = 0; i < getFieldCount(); i++) {
          if (!getField(i).isInitialized()) {
            this.memoizedIsInitialized = 0;
            return false;
          } 
        } 
        this.memoizedIsInitialized = 1;
        return true;
      }
      
      public void writeTo(CodedOutputStream output) throws IOException {
        for (int i = 0; i < this.field_.size(); i++)
          output.writeMessage(1, (MessageLite)this.field_.get(i)); 
        getUnknownFields().writeTo(output);
      }
      
      public int getSerializedSize() {
        int size = this.memoizedSize;
        if (size != -1)
          return size; 
        size = 0;
        for (int i = 0; i < this.field_.size(); i++)
          size += CodedOutputStream.computeMessageSize(1, (MessageLite)this.field_.get(i)); 
        size += getUnknownFields().getSerializedSize();
        this.memoizedSize = size;
        return size;
      }
      
      public boolean equals(Object obj) {
        if (obj == this)
          return true; 
        if (!(obj instanceof TypedRow))
          return super.equals(obj); 
        TypedRow other = (TypedRow)obj;
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
      
      public static TypedRow parseFrom(ByteBuffer data) throws InvalidProtocolBufferException {
        return (TypedRow)PARSER.parseFrom(data);
      }
      
      public static TypedRow parseFrom(ByteBuffer data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
        return (TypedRow)PARSER.parseFrom(data, extensionRegistry);
      }
      
      public static TypedRow parseFrom(ByteString data) throws InvalidProtocolBufferException {
        return (TypedRow)PARSER.parseFrom(data);
      }
      
      public static TypedRow parseFrom(ByteString data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
        return (TypedRow)PARSER.parseFrom(data, extensionRegistry);
      }
      
      public static TypedRow parseFrom(byte[] data) throws InvalidProtocolBufferException {
        return (TypedRow)PARSER.parseFrom(data);
      }
      
      public static TypedRow parseFrom(byte[] data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
        return (TypedRow)PARSER.parseFrom(data, extensionRegistry);
      }
      
      public static TypedRow parseFrom(InputStream input) throws IOException {
        return (TypedRow)GeneratedMessageV3.parseWithIOException(PARSER, input);
      }
      
      public static TypedRow parseFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
        return (TypedRow)GeneratedMessageV3.parseWithIOException(PARSER, input, extensionRegistry);
      }
      
      public static TypedRow parseDelimitedFrom(InputStream input) throws IOException {
        return (TypedRow)GeneratedMessageV3.parseDelimitedWithIOException(PARSER, input);
      }
      
      public static TypedRow parseDelimitedFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
        return (TypedRow)GeneratedMessageV3.parseDelimitedWithIOException(PARSER, input, extensionRegistry);
      }
      
      public static TypedRow parseFrom(CodedInputStream input) throws IOException {
        return (TypedRow)GeneratedMessageV3.parseWithIOException(PARSER, input);
      }
      
      public static TypedRow parseFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
        return (TypedRow)GeneratedMessageV3.parseWithIOException(PARSER, input, extensionRegistry);
      }
      
      public Builder newBuilderForType() {
        return newBuilder();
      }
      
      public static Builder newBuilder() {
        return DEFAULT_INSTANCE.toBuilder();
      }
      
      public static Builder newBuilder(TypedRow prototype) {
        return DEFAULT_INSTANCE.toBuilder().mergeFrom(prototype);
      }
      
      public Builder toBuilder() {
        return (this == DEFAULT_INSTANCE) ? new Builder() : (new Builder()).mergeFrom(this);
      }
      
      protected Builder newBuilderForType(GeneratedMessageV3.BuilderParent parent) {
        Builder builder = new Builder(parent);
        return builder;
      }
      
      public static final class Builder extends GeneratedMessageV3.Builder<Builder> implements MysqlxCrud.Insert.TypedRowOrBuilder {
        private int bitField0_;
        
        private List<MysqlxExpr.Expr> field_;
        
        private RepeatedFieldBuilderV3<MysqlxExpr.Expr, MysqlxExpr.Expr.Builder, MysqlxExpr.ExprOrBuilder> fieldBuilder_;
        
        public static final Descriptors.Descriptor getDescriptor() {
          return MysqlxCrud.internal_static_Mysqlx_Crud_Insert_TypedRow_descriptor;
        }
        
        protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() {
          return MysqlxCrud.internal_static_Mysqlx_Crud_Insert_TypedRow_fieldAccessorTable.ensureFieldAccessorsInitialized(MysqlxCrud.Insert.TypedRow.class, Builder.class);
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
          if (this.fieldBuilder_ == null) {
            this.field_ = Collections.emptyList();
          } else {
            this.field_ = null;
            this.fieldBuilder_.clear();
          } 
          this.bitField0_ &= 0xFFFFFFFE;
          return this;
        }
        
        public Descriptors.Descriptor getDescriptorForType() {
          return MysqlxCrud.internal_static_Mysqlx_Crud_Insert_TypedRow_descriptor;
        }
        
        public MysqlxCrud.Insert.TypedRow getDefaultInstanceForType() {
          return MysqlxCrud.Insert.TypedRow.getDefaultInstance();
        }
        
        public MysqlxCrud.Insert.TypedRow build() {
          MysqlxCrud.Insert.TypedRow result = buildPartial();
          if (!result.isInitialized())
            throw newUninitializedMessageException(result); 
          return result;
        }
        
        public MysqlxCrud.Insert.TypedRow buildPartial() {
          MysqlxCrud.Insert.TypedRow result = new MysqlxCrud.Insert.TypedRow(this);
          int from_bitField0_ = this.bitField0_;
          if (this.fieldBuilder_ == null) {
            if ((this.bitField0_ & 0x1) != 0) {
              this.field_ = Collections.unmodifiableList(this.field_);
              this.bitField0_ &= 0xFFFFFFFE;
            } 
            result.field_ = this.field_;
          } else {
            result.field_ = this.fieldBuilder_.build();
          } 
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
          if (other instanceof MysqlxCrud.Insert.TypedRow)
            return mergeFrom((MysqlxCrud.Insert.TypedRow)other); 
          super.mergeFrom(other);
          return this;
        }
        
        public Builder mergeFrom(MysqlxCrud.Insert.TypedRow other) {
          if (other == MysqlxCrud.Insert.TypedRow.getDefaultInstance())
            return this; 
          if (this.fieldBuilder_ == null) {
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
          } else if (!other.field_.isEmpty()) {
            if (this.fieldBuilder_.isEmpty()) {
              this.fieldBuilder_.dispose();
              this.fieldBuilder_ = null;
              this.field_ = other.field_;
              this.bitField0_ &= 0xFFFFFFFE;
              this.fieldBuilder_ = MysqlxCrud.Insert.TypedRow.alwaysUseFieldBuilders ? getFieldFieldBuilder() : null;
            } else {
              this.fieldBuilder_.addAllMessages(other.field_);
            } 
          } 
          mergeUnknownFields(other.getUnknownFields());
          onChanged();
          return this;
        }
        
        public final boolean isInitialized() {
          for (int i = 0; i < getFieldCount(); i++) {
            if (!getField(i).isInitialized())
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
              MysqlxExpr.Expr m;
              int tag = input.readTag();
              switch (tag) {
                case 0:
                  done = true;
                  continue;
                case 10:
                  m = (MysqlxExpr.Expr)input.readMessage(MysqlxExpr.Expr.PARSER, extensionRegistry);
                  if (this.fieldBuilder_ == null) {
                    ensureFieldIsMutable();
                    this.field_.add(m);
                    continue;
                  } 
                  this.fieldBuilder_.addMessage((AbstractMessage)m);
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
        
        public List<MysqlxExpr.Expr> getFieldList() {
          if (this.fieldBuilder_ == null)
            return Collections.unmodifiableList(this.field_); 
          return this.fieldBuilder_.getMessageList();
        }
        
        public int getFieldCount() {
          if (this.fieldBuilder_ == null)
            return this.field_.size(); 
          return this.fieldBuilder_.getCount();
        }
        
        public MysqlxExpr.Expr getField(int index) {
          if (this.fieldBuilder_ == null)
            return this.field_.get(index); 
          return (MysqlxExpr.Expr)this.fieldBuilder_.getMessage(index);
        }
        
        public Builder setField(int index, MysqlxExpr.Expr value) {
          if (this.fieldBuilder_ == null) {
            if (value == null)
              throw new NullPointerException(); 
            ensureFieldIsMutable();
            this.field_.set(index, value);
            onChanged();
          } else {
            this.fieldBuilder_.setMessage(index, (AbstractMessage)value);
          } 
          return this;
        }
        
        public Builder setField(int index, MysqlxExpr.Expr.Builder builderForValue) {
          if (this.fieldBuilder_ == null) {
            ensureFieldIsMutable();
            this.field_.set(index, builderForValue.build());
            onChanged();
          } else {
            this.fieldBuilder_.setMessage(index, (AbstractMessage)builderForValue.build());
          } 
          return this;
        }
        
        public Builder addField(MysqlxExpr.Expr value) {
          if (this.fieldBuilder_ == null) {
            if (value == null)
              throw new NullPointerException(); 
            ensureFieldIsMutable();
            this.field_.add(value);
            onChanged();
          } else {
            this.fieldBuilder_.addMessage((AbstractMessage)value);
          } 
          return this;
        }
        
        public Builder addField(int index, MysqlxExpr.Expr value) {
          if (this.fieldBuilder_ == null) {
            if (value == null)
              throw new NullPointerException(); 
            ensureFieldIsMutable();
            this.field_.add(index, value);
            onChanged();
          } else {
            this.fieldBuilder_.addMessage(index, (AbstractMessage)value);
          } 
          return this;
        }
        
        public Builder addField(MysqlxExpr.Expr.Builder builderForValue) {
          if (this.fieldBuilder_ == null) {
            ensureFieldIsMutable();
            this.field_.add(builderForValue.build());
            onChanged();
          } else {
            this.fieldBuilder_.addMessage((AbstractMessage)builderForValue.build());
          } 
          return this;
        }
        
        public Builder addField(int index, MysqlxExpr.Expr.Builder builderForValue) {
          if (this.fieldBuilder_ == null) {
            ensureFieldIsMutable();
            this.field_.add(index, builderForValue.build());
            onChanged();
          } else {
            this.fieldBuilder_.addMessage(index, (AbstractMessage)builderForValue.build());
          } 
          return this;
        }
        
        public Builder addAllField(Iterable<? extends MysqlxExpr.Expr> values) {
          if (this.fieldBuilder_ == null) {
            ensureFieldIsMutable();
            AbstractMessageLite.Builder.addAll(values, this.field_);
            onChanged();
          } else {
            this.fieldBuilder_.addAllMessages(values);
          } 
          return this;
        }
        
        public Builder clearField() {
          if (this.fieldBuilder_ == null) {
            this.field_ = Collections.emptyList();
            this.bitField0_ &= 0xFFFFFFFE;
            onChanged();
          } else {
            this.fieldBuilder_.clear();
          } 
          return this;
        }
        
        public Builder removeField(int index) {
          if (this.fieldBuilder_ == null) {
            ensureFieldIsMutable();
            this.field_.remove(index);
            onChanged();
          } else {
            this.fieldBuilder_.remove(index);
          } 
          return this;
        }
        
        public MysqlxExpr.Expr.Builder getFieldBuilder(int index) {
          return (MysqlxExpr.Expr.Builder)getFieldFieldBuilder().getBuilder(index);
        }
        
        public MysqlxExpr.ExprOrBuilder getFieldOrBuilder(int index) {
          if (this.fieldBuilder_ == null)
            return this.field_.get(index); 
          return (MysqlxExpr.ExprOrBuilder)this.fieldBuilder_.getMessageOrBuilder(index);
        }
        
        public List<? extends MysqlxExpr.ExprOrBuilder> getFieldOrBuilderList() {
          if (this.fieldBuilder_ != null)
            return this.fieldBuilder_.getMessageOrBuilderList(); 
          return Collections.unmodifiableList((List)this.field_);
        }
        
        public MysqlxExpr.Expr.Builder addFieldBuilder() {
          return (MysqlxExpr.Expr.Builder)getFieldFieldBuilder().addBuilder((AbstractMessage)MysqlxExpr.Expr.getDefaultInstance());
        }
        
        public MysqlxExpr.Expr.Builder addFieldBuilder(int index) {
          return (MysqlxExpr.Expr.Builder)getFieldFieldBuilder().addBuilder(index, (AbstractMessage)MysqlxExpr.Expr.getDefaultInstance());
        }
        
        public List<MysqlxExpr.Expr.Builder> getFieldBuilderList() {
          return getFieldFieldBuilder().getBuilderList();
        }
        
        private RepeatedFieldBuilderV3<MysqlxExpr.Expr, MysqlxExpr.Expr.Builder, MysqlxExpr.ExprOrBuilder> getFieldFieldBuilder() {
          if (this.fieldBuilder_ == null) {
            this.fieldBuilder_ = new RepeatedFieldBuilderV3(this.field_, ((this.bitField0_ & 0x1) != 0), (AbstractMessage.BuilderParent)getParentForChildren(), isClean());
            this.field_ = null;
          } 
          return this.fieldBuilder_;
        }
        
        public final Builder setUnknownFields(UnknownFieldSet unknownFields) {
          return (Builder)super.setUnknownFields(unknownFields);
        }
        
        public final Builder mergeUnknownFields(UnknownFieldSet unknownFields) {
          return (Builder)super.mergeUnknownFields(unknownFields);
        }
      }
      
      private static final TypedRow DEFAULT_INSTANCE = new TypedRow();
      
      public static TypedRow getDefaultInstance() {
        return DEFAULT_INSTANCE;
      }
      
      @Deprecated
      public static final Parser<TypedRow> PARSER = (Parser<TypedRow>)new AbstractParser<TypedRow>() {
          public MysqlxCrud.Insert.TypedRow parsePartialFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            MysqlxCrud.Insert.TypedRow.Builder builder = MysqlxCrud.Insert.TypedRow.newBuilder();
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
      
      public static Parser<TypedRow> parser() {
        return PARSER;
      }
      
      public Parser<TypedRow> getParserForType() {
        return PARSER;
      }
      
      public TypedRow getDefaultInstanceForType() {
        return DEFAULT_INSTANCE;
      }
    }
    
    public boolean hasCollection() {
      return ((this.bitField0_ & 0x1) != 0);
    }
    
    public MysqlxCrud.Collection getCollection() {
      return (this.collection_ == null) ? MysqlxCrud.Collection.getDefaultInstance() : this.collection_;
    }
    
    public MysqlxCrud.CollectionOrBuilder getCollectionOrBuilder() {
      return (this.collection_ == null) ? MysqlxCrud.Collection.getDefaultInstance() : this.collection_;
    }
    
    public boolean hasDataModel() {
      return ((this.bitField0_ & 0x2) != 0);
    }
    
    public MysqlxCrud.DataModel getDataModel() {
      MysqlxCrud.DataModel result = MysqlxCrud.DataModel.valueOf(this.dataModel_);
      return (result == null) ? MysqlxCrud.DataModel.DOCUMENT : result;
    }
    
    public List<MysqlxCrud.Column> getProjectionList() {
      return this.projection_;
    }
    
    public List<? extends MysqlxCrud.ColumnOrBuilder> getProjectionOrBuilderList() {
      return (List)this.projection_;
    }
    
    public int getProjectionCount() {
      return this.projection_.size();
    }
    
    public MysqlxCrud.Column getProjection(int index) {
      return this.projection_.get(index);
    }
    
    public MysqlxCrud.ColumnOrBuilder getProjectionOrBuilder(int index) {
      return this.projection_.get(index);
    }
    
    public List<TypedRow> getRowList() {
      return this.row_;
    }
    
    public List<? extends TypedRowOrBuilder> getRowOrBuilderList() {
      return (List)this.row_;
    }
    
    public int getRowCount() {
      return this.row_.size();
    }
    
    public TypedRow getRow(int index) {
      return this.row_.get(index);
    }
    
    public TypedRowOrBuilder getRowOrBuilder(int index) {
      return this.row_.get(index);
    }
    
    public List<MysqlxDatatypes.Scalar> getArgsList() {
      return this.args_;
    }
    
    public List<? extends MysqlxDatatypes.ScalarOrBuilder> getArgsOrBuilderList() {
      return (List)this.args_;
    }
    
    public int getArgsCount() {
      return this.args_.size();
    }
    
    public MysqlxDatatypes.Scalar getArgs(int index) {
      return this.args_.get(index);
    }
    
    public MysqlxDatatypes.ScalarOrBuilder getArgsOrBuilder(int index) {
      return this.args_.get(index);
    }
    
    public boolean hasUpsert() {
      return ((this.bitField0_ & 0x4) != 0);
    }
    
    public boolean getUpsert() {
      return this.upsert_;
    }
    
    public final boolean isInitialized() {
      byte isInitialized = this.memoizedIsInitialized;
      if (isInitialized == 1)
        return true; 
      if (isInitialized == 0)
        return false; 
      if (!hasCollection()) {
        this.memoizedIsInitialized = 0;
        return false;
      } 
      if (!getCollection().isInitialized()) {
        this.memoizedIsInitialized = 0;
        return false;
      } 
      int i;
      for (i = 0; i < getProjectionCount(); i++) {
        if (!getProjection(i).isInitialized()) {
          this.memoizedIsInitialized = 0;
          return false;
        } 
      } 
      for (i = 0; i < getRowCount(); i++) {
        if (!getRow(i).isInitialized()) {
          this.memoizedIsInitialized = 0;
          return false;
        } 
      } 
      for (i = 0; i < getArgsCount(); i++) {
        if (!getArgs(i).isInitialized()) {
          this.memoizedIsInitialized = 0;
          return false;
        } 
      } 
      this.memoizedIsInitialized = 1;
      return true;
    }
    
    public void writeTo(CodedOutputStream output) throws IOException {
      if ((this.bitField0_ & 0x1) != 0)
        output.writeMessage(1, (MessageLite)getCollection()); 
      if ((this.bitField0_ & 0x2) != 0)
        output.writeEnum(2, this.dataModel_); 
      int i;
      for (i = 0; i < this.projection_.size(); i++)
        output.writeMessage(3, (MessageLite)this.projection_.get(i)); 
      for (i = 0; i < this.row_.size(); i++)
        output.writeMessage(4, (MessageLite)this.row_.get(i)); 
      for (i = 0; i < this.args_.size(); i++)
        output.writeMessage(5, (MessageLite)this.args_.get(i)); 
      if ((this.bitField0_ & 0x4) != 0)
        output.writeBool(6, this.upsert_); 
      getUnknownFields().writeTo(output);
    }
    
    public int getSerializedSize() {
      int size = this.memoizedSize;
      if (size != -1)
        return size; 
      size = 0;
      if ((this.bitField0_ & 0x1) != 0)
        size += 
          CodedOutputStream.computeMessageSize(1, (MessageLite)getCollection()); 
      if ((this.bitField0_ & 0x2) != 0)
        size += 
          CodedOutputStream.computeEnumSize(2, this.dataModel_); 
      int i;
      for (i = 0; i < this.projection_.size(); i++)
        size += 
          CodedOutputStream.computeMessageSize(3, (MessageLite)this.projection_.get(i)); 
      for (i = 0; i < this.row_.size(); i++)
        size += 
          CodedOutputStream.computeMessageSize(4, (MessageLite)this.row_.get(i)); 
      for (i = 0; i < this.args_.size(); i++)
        size += 
          CodedOutputStream.computeMessageSize(5, (MessageLite)this.args_.get(i)); 
      if ((this.bitField0_ & 0x4) != 0)
        size += 
          CodedOutputStream.computeBoolSize(6, this.upsert_); 
      size += getUnknownFields().getSerializedSize();
      this.memoizedSize = size;
      return size;
    }
    
    public boolean equals(Object obj) {
      if (obj == this)
        return true; 
      if (!(obj instanceof Insert))
        return super.equals(obj); 
      Insert other = (Insert)obj;
      if (hasCollection() != other.hasCollection())
        return false; 
      if (hasCollection() && 
        
        !getCollection().equals(other.getCollection()))
        return false; 
      if (hasDataModel() != other.hasDataModel())
        return false; 
      if (hasDataModel() && 
        this.dataModel_ != other.dataModel_)
        return false; 
      if (!getProjectionList().equals(other.getProjectionList()))
        return false; 
      if (!getRowList().equals(other.getRowList()))
        return false; 
      if (!getArgsList().equals(other.getArgsList()))
        return false; 
      if (hasUpsert() != other.hasUpsert())
        return false; 
      if (hasUpsert() && 
        getUpsert() != other
        .getUpsert())
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
      if (hasCollection()) {
        hash = 37 * hash + 1;
        hash = 53 * hash + getCollection().hashCode();
      } 
      if (hasDataModel()) {
        hash = 37 * hash + 2;
        hash = 53 * hash + this.dataModel_;
      } 
      if (getProjectionCount() > 0) {
        hash = 37 * hash + 3;
        hash = 53 * hash + getProjectionList().hashCode();
      } 
      if (getRowCount() > 0) {
        hash = 37 * hash + 4;
        hash = 53 * hash + getRowList().hashCode();
      } 
      if (getArgsCount() > 0) {
        hash = 37 * hash + 5;
        hash = 53 * hash + getArgsList().hashCode();
      } 
      if (hasUpsert()) {
        hash = 37 * hash + 6;
        hash = 53 * hash + Internal.hashBoolean(
            getUpsert());
      } 
      hash = 29 * hash + getUnknownFields().hashCode();
      this.memoizedHashCode = hash;
      return hash;
    }
    
    public static Insert parseFrom(ByteBuffer data) throws InvalidProtocolBufferException {
      return (Insert)PARSER.parseFrom(data);
    }
    
    public static Insert parseFrom(ByteBuffer data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
      return (Insert)PARSER.parseFrom(data, extensionRegistry);
    }
    
    public static Insert parseFrom(ByteString data) throws InvalidProtocolBufferException {
      return (Insert)PARSER.parseFrom(data);
    }
    
    public static Insert parseFrom(ByteString data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
      return (Insert)PARSER.parseFrom(data, extensionRegistry);
    }
    
    public static Insert parseFrom(byte[] data) throws InvalidProtocolBufferException {
      return (Insert)PARSER.parseFrom(data);
    }
    
    public static Insert parseFrom(byte[] data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
      return (Insert)PARSER.parseFrom(data, extensionRegistry);
    }
    
    public static Insert parseFrom(InputStream input) throws IOException {
      return 
        (Insert)GeneratedMessageV3.parseWithIOException(PARSER, input);
    }
    
    public static Insert parseFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
      return 
        (Insert)GeneratedMessageV3.parseWithIOException(PARSER, input, extensionRegistry);
    }
    
    public static Insert parseDelimitedFrom(InputStream input) throws IOException {
      return 
        (Insert)GeneratedMessageV3.parseDelimitedWithIOException(PARSER, input);
    }
    
    public static Insert parseDelimitedFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
      return 
        (Insert)GeneratedMessageV3.parseDelimitedWithIOException(PARSER, input, extensionRegistry);
    }
    
    public static Insert parseFrom(CodedInputStream input) throws IOException {
      return 
        (Insert)GeneratedMessageV3.parseWithIOException(PARSER, input);
    }
    
    public static Insert parseFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
      return 
        (Insert)GeneratedMessageV3.parseWithIOException(PARSER, input, extensionRegistry);
    }
    
    public Builder newBuilderForType() {
      return newBuilder();
    }
    
    public static Builder newBuilder() {
      return DEFAULT_INSTANCE.toBuilder();
    }
    
    public static Builder newBuilder(Insert prototype) {
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
    
    public static final class Builder extends GeneratedMessageV3.Builder<Builder> implements MysqlxCrud.InsertOrBuilder {
      private int bitField0_;
      
      private MysqlxCrud.Collection collection_;
      
      private SingleFieldBuilderV3<MysqlxCrud.Collection, MysqlxCrud.Collection.Builder, MysqlxCrud.CollectionOrBuilder> collectionBuilder_;
      
      private int dataModel_;
      
      private List<MysqlxCrud.Column> projection_;
      
      private RepeatedFieldBuilderV3<MysqlxCrud.Column, MysqlxCrud.Column.Builder, MysqlxCrud.ColumnOrBuilder> projectionBuilder_;
      
      private List<MysqlxCrud.Insert.TypedRow> row_;
      
      private RepeatedFieldBuilderV3<MysqlxCrud.Insert.TypedRow, MysqlxCrud.Insert.TypedRow.Builder, MysqlxCrud.Insert.TypedRowOrBuilder> rowBuilder_;
      
      private List<MysqlxDatatypes.Scalar> args_;
      
      private RepeatedFieldBuilderV3<MysqlxDatatypes.Scalar, MysqlxDatatypes.Scalar.Builder, MysqlxDatatypes.ScalarOrBuilder> argsBuilder_;
      
      private boolean upsert_;
      
      public static final Descriptors.Descriptor getDescriptor() {
        return MysqlxCrud.internal_static_Mysqlx_Crud_Insert_descriptor;
      }
      
      protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() {
        return MysqlxCrud.internal_static_Mysqlx_Crud_Insert_fieldAccessorTable
          .ensureFieldAccessorsInitialized(MysqlxCrud.Insert.class, Builder.class);
      }
      
      private Builder() {
        this.dataModel_ = 1;
        this
          .projection_ = Collections.emptyList();
        this
          .row_ = Collections.emptyList();
        this
          .args_ = Collections.emptyList();
        maybeForceBuilderInitialization();
      }
      
      private Builder(GeneratedMessageV3.BuilderParent parent) {
        super(parent);
        this.dataModel_ = 1;
        this.projection_ = Collections.emptyList();
        this.row_ = Collections.emptyList();
        this.args_ = Collections.emptyList();
        maybeForceBuilderInitialization();
      }
      
      private void maybeForceBuilderInitialization() {
        if (MysqlxCrud.Insert.alwaysUseFieldBuilders) {
          getCollectionFieldBuilder();
          getProjectionFieldBuilder();
          getRowFieldBuilder();
          getArgsFieldBuilder();
        } 
      }
      
      public Builder clear() {
        super.clear();
        if (this.collectionBuilder_ == null) {
          this.collection_ = null;
        } else {
          this.collectionBuilder_.clear();
        } 
        this.bitField0_ &= 0xFFFFFFFE;
        this.dataModel_ = 1;
        this.bitField0_ &= 0xFFFFFFFD;
        if (this.projectionBuilder_ == null) {
          this.projection_ = Collections.emptyList();
        } else {
          this.projection_ = null;
          this.projectionBuilder_.clear();
        } 
        this.bitField0_ &= 0xFFFFFFFB;
        if (this.rowBuilder_ == null) {
          this.row_ = Collections.emptyList();
        } else {
          this.row_ = null;
          this.rowBuilder_.clear();
        } 
        this.bitField0_ &= 0xFFFFFFF7;
        if (this.argsBuilder_ == null) {
          this.args_ = Collections.emptyList();
        } else {
          this.args_ = null;
          this.argsBuilder_.clear();
        } 
        this.bitField0_ &= 0xFFFFFFEF;
        this.upsert_ = false;
        this.bitField0_ &= 0xFFFFFFDF;
        return this;
      }
      
      public Descriptors.Descriptor getDescriptorForType() {
        return MysqlxCrud.internal_static_Mysqlx_Crud_Insert_descriptor;
      }
      
      public MysqlxCrud.Insert getDefaultInstanceForType() {
        return MysqlxCrud.Insert.getDefaultInstance();
      }
      
      public MysqlxCrud.Insert build() {
        MysqlxCrud.Insert result = buildPartial();
        if (!result.isInitialized())
          throw newUninitializedMessageException(result); 
        return result;
      }
      
      public MysqlxCrud.Insert buildPartial() {
        MysqlxCrud.Insert result = new MysqlxCrud.Insert(this);
        int from_bitField0_ = this.bitField0_;
        int to_bitField0_ = 0;
        if ((from_bitField0_ & 0x1) != 0) {
          if (this.collectionBuilder_ == null) {
            result.collection_ = this.collection_;
          } else {
            result.collection_ = (MysqlxCrud.Collection)this.collectionBuilder_.build();
          } 
          to_bitField0_ |= 0x1;
        } 
        if ((from_bitField0_ & 0x2) != 0)
          to_bitField0_ |= 0x2; 
        result.dataModel_ = this.dataModel_;
        if (this.projectionBuilder_ == null) {
          if ((this.bitField0_ & 0x4) != 0) {
            this.projection_ = Collections.unmodifiableList(this.projection_);
            this.bitField0_ &= 0xFFFFFFFB;
          } 
          result.projection_ = this.projection_;
        } else {
          result.projection_ = this.projectionBuilder_.build();
        } 
        if (this.rowBuilder_ == null) {
          if ((this.bitField0_ & 0x8) != 0) {
            this.row_ = Collections.unmodifiableList(this.row_);
            this.bitField0_ &= 0xFFFFFFF7;
          } 
          result.row_ = this.row_;
        } else {
          result.row_ = this.rowBuilder_.build();
        } 
        if (this.argsBuilder_ == null) {
          if ((this.bitField0_ & 0x10) != 0) {
            this.args_ = Collections.unmodifiableList(this.args_);
            this.bitField0_ &= 0xFFFFFFEF;
          } 
          result.args_ = this.args_;
        } else {
          result.args_ = this.argsBuilder_.build();
        } 
        if ((from_bitField0_ & 0x20) != 0) {
          result.upsert_ = this.upsert_;
          to_bitField0_ |= 0x4;
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
        if (other instanceof MysqlxCrud.Insert)
          return mergeFrom((MysqlxCrud.Insert)other); 
        super.mergeFrom(other);
        return this;
      }
      
      public Builder mergeFrom(MysqlxCrud.Insert other) {
        if (other == MysqlxCrud.Insert.getDefaultInstance())
          return this; 
        if (other.hasCollection())
          mergeCollection(other.getCollection()); 
        if (other.hasDataModel())
          setDataModel(other.getDataModel()); 
        if (this.projectionBuilder_ == null) {
          if (!other.projection_.isEmpty()) {
            if (this.projection_.isEmpty()) {
              this.projection_ = other.projection_;
              this.bitField0_ &= 0xFFFFFFFB;
            } else {
              ensureProjectionIsMutable();
              this.projection_.addAll(other.projection_);
            } 
            onChanged();
          } 
        } else if (!other.projection_.isEmpty()) {
          if (this.projectionBuilder_.isEmpty()) {
            this.projectionBuilder_.dispose();
            this.projectionBuilder_ = null;
            this.projection_ = other.projection_;
            this.bitField0_ &= 0xFFFFFFFB;
            this.projectionBuilder_ = MysqlxCrud.Insert.alwaysUseFieldBuilders ? getProjectionFieldBuilder() : null;
          } else {
            this.projectionBuilder_.addAllMessages(other.projection_);
          } 
        } 
        if (this.rowBuilder_ == null) {
          if (!other.row_.isEmpty()) {
            if (this.row_.isEmpty()) {
              this.row_ = other.row_;
              this.bitField0_ &= 0xFFFFFFF7;
            } else {
              ensureRowIsMutable();
              this.row_.addAll(other.row_);
            } 
            onChanged();
          } 
        } else if (!other.row_.isEmpty()) {
          if (this.rowBuilder_.isEmpty()) {
            this.rowBuilder_.dispose();
            this.rowBuilder_ = null;
            this.row_ = other.row_;
            this.bitField0_ &= 0xFFFFFFF7;
            this.rowBuilder_ = MysqlxCrud.Insert.alwaysUseFieldBuilders ? getRowFieldBuilder() : null;
          } else {
            this.rowBuilder_.addAllMessages(other.row_);
          } 
        } 
        if (this.argsBuilder_ == null) {
          if (!other.args_.isEmpty()) {
            if (this.args_.isEmpty()) {
              this.args_ = other.args_;
              this.bitField0_ &= 0xFFFFFFEF;
            } else {
              ensureArgsIsMutable();
              this.args_.addAll(other.args_);
            } 
            onChanged();
          } 
        } else if (!other.args_.isEmpty()) {
          if (this.argsBuilder_.isEmpty()) {
            this.argsBuilder_.dispose();
            this.argsBuilder_ = null;
            this.args_ = other.args_;
            this.bitField0_ &= 0xFFFFFFEF;
            this.argsBuilder_ = MysqlxCrud.Insert.alwaysUseFieldBuilders ? getArgsFieldBuilder() : null;
          } else {
            this.argsBuilder_.addAllMessages(other.args_);
          } 
        } 
        if (other.hasUpsert())
          setUpsert(other.getUpsert()); 
        mergeUnknownFields(other.getUnknownFields());
        onChanged();
        return this;
      }
      
      public final boolean isInitialized() {
        if (!hasCollection())
          return false; 
        if (!getCollection().isInitialized())
          return false; 
        int i;
        for (i = 0; i < getProjectionCount(); i++) {
          if (!getProjection(i).isInitialized())
            return false; 
        } 
        for (i = 0; i < getRowCount(); i++) {
          if (!getRow(i).isInitialized())
            return false; 
        } 
        for (i = 0; i < getArgsCount(); i++) {
          if (!getArgs(i).isInitialized())
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
            MysqlxCrud.Column column;
            MysqlxCrud.Insert.TypedRow typedRow;
            MysqlxDatatypes.Scalar m;
            MysqlxCrud.DataModel tmpValue;
            int tag = input.readTag();
            switch (tag) {
              case 0:
                done = true;
                continue;
              case 10:
                input.readMessage((MessageLite.Builder)getCollectionFieldBuilder().getBuilder(), extensionRegistry);
                this.bitField0_ |= 0x1;
                continue;
              case 16:
                tmpRaw = input.readEnum();
                tmpValue = MysqlxCrud.DataModel.forNumber(tmpRaw);
                if (tmpValue == null) {
                  mergeUnknownVarintField(2, tmpRaw);
                  continue;
                } 
                this.dataModel_ = tmpRaw;
                this.bitField0_ |= 0x2;
                continue;
              case 26:
                column = (MysqlxCrud.Column)input.readMessage(MysqlxCrud.Column.PARSER, extensionRegistry);
                if (this.projectionBuilder_ == null) {
                  ensureProjectionIsMutable();
                  this.projection_.add(column);
                  continue;
                } 
                this.projectionBuilder_.addMessage((AbstractMessage)column);
                continue;
              case 34:
                typedRow = (MysqlxCrud.Insert.TypedRow)input.readMessage(MysqlxCrud.Insert.TypedRow.PARSER, extensionRegistry);
                if (this.rowBuilder_ == null) {
                  ensureRowIsMutable();
                  this.row_.add(typedRow);
                  continue;
                } 
                this.rowBuilder_.addMessage((AbstractMessage)typedRow);
                continue;
              case 42:
                m = (MysqlxDatatypes.Scalar)input.readMessage(MysqlxDatatypes.Scalar.PARSER, extensionRegistry);
                if (this.argsBuilder_ == null) {
                  ensureArgsIsMutable();
                  this.args_.add(m);
                  continue;
                } 
                this.argsBuilder_.addMessage((AbstractMessage)m);
                continue;
              case 48:
                this.upsert_ = input.readBool();
                this.bitField0_ |= 0x20;
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
      
      public boolean hasCollection() {
        return ((this.bitField0_ & 0x1) != 0);
      }
      
      public MysqlxCrud.Collection getCollection() {
        if (this.collectionBuilder_ == null)
          return (this.collection_ == null) ? MysqlxCrud.Collection.getDefaultInstance() : this.collection_; 
        return (MysqlxCrud.Collection)this.collectionBuilder_.getMessage();
      }
      
      public Builder setCollection(MysqlxCrud.Collection value) {
        if (this.collectionBuilder_ == null) {
          if (value == null)
            throw new NullPointerException(); 
          this.collection_ = value;
          onChanged();
        } else {
          this.collectionBuilder_.setMessage((AbstractMessage)value);
        } 
        this.bitField0_ |= 0x1;
        return this;
      }
      
      public Builder setCollection(MysqlxCrud.Collection.Builder builderForValue) {
        if (this.collectionBuilder_ == null) {
          this.collection_ = builderForValue.build();
          onChanged();
        } else {
          this.collectionBuilder_.setMessage((AbstractMessage)builderForValue.build());
        } 
        this.bitField0_ |= 0x1;
        return this;
      }
      
      public Builder mergeCollection(MysqlxCrud.Collection value) {
        if (this.collectionBuilder_ == null) {
          if ((this.bitField0_ & 0x1) != 0 && this.collection_ != null && this.collection_ != MysqlxCrud.Collection.getDefaultInstance()) {
            this.collection_ = MysqlxCrud.Collection.newBuilder(this.collection_).mergeFrom(value).buildPartial();
          } else {
            this.collection_ = value;
          } 
          onChanged();
        } else {
          this.collectionBuilder_.mergeFrom((AbstractMessage)value);
        } 
        this.bitField0_ |= 0x1;
        return this;
      }
      
      public Builder clearCollection() {
        if (this.collectionBuilder_ == null) {
          this.collection_ = null;
          onChanged();
        } else {
          this.collectionBuilder_.clear();
        } 
        this.bitField0_ &= 0xFFFFFFFE;
        return this;
      }
      
      public MysqlxCrud.Collection.Builder getCollectionBuilder() {
        this.bitField0_ |= 0x1;
        onChanged();
        return (MysqlxCrud.Collection.Builder)getCollectionFieldBuilder().getBuilder();
      }
      
      public MysqlxCrud.CollectionOrBuilder getCollectionOrBuilder() {
        if (this.collectionBuilder_ != null)
          return (MysqlxCrud.CollectionOrBuilder)this.collectionBuilder_.getMessageOrBuilder(); 
        return (this.collection_ == null) ? MysqlxCrud.Collection.getDefaultInstance() : this.collection_;
      }
      
      private SingleFieldBuilderV3<MysqlxCrud.Collection, MysqlxCrud.Collection.Builder, MysqlxCrud.CollectionOrBuilder> getCollectionFieldBuilder() {
        if (this.collectionBuilder_ == null) {
          this.collectionBuilder_ = new SingleFieldBuilderV3((AbstractMessage)getCollection(), (AbstractMessage.BuilderParent)getParentForChildren(), isClean());
          this.collection_ = null;
        } 
        return this.collectionBuilder_;
      }
      
      public boolean hasDataModel() {
        return ((this.bitField0_ & 0x2) != 0);
      }
      
      public MysqlxCrud.DataModel getDataModel() {
        MysqlxCrud.DataModel result = MysqlxCrud.DataModel.valueOf(this.dataModel_);
        return (result == null) ? MysqlxCrud.DataModel.DOCUMENT : result;
      }
      
      public Builder setDataModel(MysqlxCrud.DataModel value) {
        if (value == null)
          throw new NullPointerException(); 
        this.bitField0_ |= 0x2;
        this.dataModel_ = value.getNumber();
        onChanged();
        return this;
      }
      
      public Builder clearDataModel() {
        this.bitField0_ &= 0xFFFFFFFD;
        this.dataModel_ = 1;
        onChanged();
        return this;
      }
      
      private void ensureProjectionIsMutable() {
        if ((this.bitField0_ & 0x4) == 0) {
          this.projection_ = new ArrayList<>(this.projection_);
          this.bitField0_ |= 0x4;
        } 
      }
      
      public List<MysqlxCrud.Column> getProjectionList() {
        if (this.projectionBuilder_ == null)
          return Collections.unmodifiableList(this.projection_); 
        return this.projectionBuilder_.getMessageList();
      }
      
      public int getProjectionCount() {
        if (this.projectionBuilder_ == null)
          return this.projection_.size(); 
        return this.projectionBuilder_.getCount();
      }
      
      public MysqlxCrud.Column getProjection(int index) {
        if (this.projectionBuilder_ == null)
          return this.projection_.get(index); 
        return (MysqlxCrud.Column)this.projectionBuilder_.getMessage(index);
      }
      
      public Builder setProjection(int index, MysqlxCrud.Column value) {
        if (this.projectionBuilder_ == null) {
          if (value == null)
            throw new NullPointerException(); 
          ensureProjectionIsMutable();
          this.projection_.set(index, value);
          onChanged();
        } else {
          this.projectionBuilder_.setMessage(index, (AbstractMessage)value);
        } 
        return this;
      }
      
      public Builder setProjection(int index, MysqlxCrud.Column.Builder builderForValue) {
        if (this.projectionBuilder_ == null) {
          ensureProjectionIsMutable();
          this.projection_.set(index, builderForValue.build());
          onChanged();
        } else {
          this.projectionBuilder_.setMessage(index, (AbstractMessage)builderForValue.build());
        } 
        return this;
      }
      
      public Builder addProjection(MysqlxCrud.Column value) {
        if (this.projectionBuilder_ == null) {
          if (value == null)
            throw new NullPointerException(); 
          ensureProjectionIsMutable();
          this.projection_.add(value);
          onChanged();
        } else {
          this.projectionBuilder_.addMessage((AbstractMessage)value);
        } 
        return this;
      }
      
      public Builder addProjection(int index, MysqlxCrud.Column value) {
        if (this.projectionBuilder_ == null) {
          if (value == null)
            throw new NullPointerException(); 
          ensureProjectionIsMutable();
          this.projection_.add(index, value);
          onChanged();
        } else {
          this.projectionBuilder_.addMessage(index, (AbstractMessage)value);
        } 
        return this;
      }
      
      public Builder addProjection(MysqlxCrud.Column.Builder builderForValue) {
        if (this.projectionBuilder_ == null) {
          ensureProjectionIsMutable();
          this.projection_.add(builderForValue.build());
          onChanged();
        } else {
          this.projectionBuilder_.addMessage((AbstractMessage)builderForValue.build());
        } 
        return this;
      }
      
      public Builder addProjection(int index, MysqlxCrud.Column.Builder builderForValue) {
        if (this.projectionBuilder_ == null) {
          ensureProjectionIsMutable();
          this.projection_.add(index, builderForValue.build());
          onChanged();
        } else {
          this.projectionBuilder_.addMessage(index, (AbstractMessage)builderForValue.build());
        } 
        return this;
      }
      
      public Builder addAllProjection(Iterable<? extends MysqlxCrud.Column> values) {
        if (this.projectionBuilder_ == null) {
          ensureProjectionIsMutable();
          AbstractMessageLite.Builder.addAll(values, this.projection_);
          onChanged();
        } else {
          this.projectionBuilder_.addAllMessages(values);
        } 
        return this;
      }
      
      public Builder clearProjection() {
        if (this.projectionBuilder_ == null) {
          this.projection_ = Collections.emptyList();
          this.bitField0_ &= 0xFFFFFFFB;
          onChanged();
        } else {
          this.projectionBuilder_.clear();
        } 
        return this;
      }
      
      public Builder removeProjection(int index) {
        if (this.projectionBuilder_ == null) {
          ensureProjectionIsMutable();
          this.projection_.remove(index);
          onChanged();
        } else {
          this.projectionBuilder_.remove(index);
        } 
        return this;
      }
      
      public MysqlxCrud.Column.Builder getProjectionBuilder(int index) {
        return (MysqlxCrud.Column.Builder)getProjectionFieldBuilder().getBuilder(index);
      }
      
      public MysqlxCrud.ColumnOrBuilder getProjectionOrBuilder(int index) {
        if (this.projectionBuilder_ == null)
          return this.projection_.get(index); 
        return (MysqlxCrud.ColumnOrBuilder)this.projectionBuilder_.getMessageOrBuilder(index);
      }
      
      public List<? extends MysqlxCrud.ColumnOrBuilder> getProjectionOrBuilderList() {
        if (this.projectionBuilder_ != null)
          return this.projectionBuilder_.getMessageOrBuilderList(); 
        return Collections.unmodifiableList((List)this.projection_);
      }
      
      public MysqlxCrud.Column.Builder addProjectionBuilder() {
        return (MysqlxCrud.Column.Builder)getProjectionFieldBuilder().addBuilder((AbstractMessage)MysqlxCrud.Column.getDefaultInstance());
      }
      
      public MysqlxCrud.Column.Builder addProjectionBuilder(int index) {
        return (MysqlxCrud.Column.Builder)getProjectionFieldBuilder().addBuilder(index, (AbstractMessage)MysqlxCrud.Column.getDefaultInstance());
      }
      
      public List<MysqlxCrud.Column.Builder> getProjectionBuilderList() {
        return getProjectionFieldBuilder().getBuilderList();
      }
      
      private RepeatedFieldBuilderV3<MysqlxCrud.Column, MysqlxCrud.Column.Builder, MysqlxCrud.ColumnOrBuilder> getProjectionFieldBuilder() {
        if (this.projectionBuilder_ == null) {
          this.projectionBuilder_ = new RepeatedFieldBuilderV3(this.projection_, ((this.bitField0_ & 0x4) != 0), (AbstractMessage.BuilderParent)getParentForChildren(), isClean());
          this.projection_ = null;
        } 
        return this.projectionBuilder_;
      }
      
      private void ensureRowIsMutable() {
        if ((this.bitField0_ & 0x8) == 0) {
          this.row_ = new ArrayList<>(this.row_);
          this.bitField0_ |= 0x8;
        } 
      }
      
      public List<MysqlxCrud.Insert.TypedRow> getRowList() {
        if (this.rowBuilder_ == null)
          return Collections.unmodifiableList(this.row_); 
        return this.rowBuilder_.getMessageList();
      }
      
      public int getRowCount() {
        if (this.rowBuilder_ == null)
          return this.row_.size(); 
        return this.rowBuilder_.getCount();
      }
      
      public MysqlxCrud.Insert.TypedRow getRow(int index) {
        if (this.rowBuilder_ == null)
          return this.row_.get(index); 
        return (MysqlxCrud.Insert.TypedRow)this.rowBuilder_.getMessage(index);
      }
      
      public Builder setRow(int index, MysqlxCrud.Insert.TypedRow value) {
        if (this.rowBuilder_ == null) {
          if (value == null)
            throw new NullPointerException(); 
          ensureRowIsMutable();
          this.row_.set(index, value);
          onChanged();
        } else {
          this.rowBuilder_.setMessage(index, (AbstractMessage)value);
        } 
        return this;
      }
      
      public Builder setRow(int index, MysqlxCrud.Insert.TypedRow.Builder builderForValue) {
        if (this.rowBuilder_ == null) {
          ensureRowIsMutable();
          this.row_.set(index, builderForValue.build());
          onChanged();
        } else {
          this.rowBuilder_.setMessage(index, (AbstractMessage)builderForValue.build());
        } 
        return this;
      }
      
      public Builder addRow(MysqlxCrud.Insert.TypedRow value) {
        if (this.rowBuilder_ == null) {
          if (value == null)
            throw new NullPointerException(); 
          ensureRowIsMutable();
          this.row_.add(value);
          onChanged();
        } else {
          this.rowBuilder_.addMessage((AbstractMessage)value);
        } 
        return this;
      }
      
      public Builder addRow(int index, MysqlxCrud.Insert.TypedRow value) {
        if (this.rowBuilder_ == null) {
          if (value == null)
            throw new NullPointerException(); 
          ensureRowIsMutable();
          this.row_.add(index, value);
          onChanged();
        } else {
          this.rowBuilder_.addMessage(index, (AbstractMessage)value);
        } 
        return this;
      }
      
      public Builder addRow(MysqlxCrud.Insert.TypedRow.Builder builderForValue) {
        if (this.rowBuilder_ == null) {
          ensureRowIsMutable();
          this.row_.add(builderForValue.build());
          onChanged();
        } else {
          this.rowBuilder_.addMessage((AbstractMessage)builderForValue.build());
        } 
        return this;
      }
      
      public Builder addRow(int index, MysqlxCrud.Insert.TypedRow.Builder builderForValue) {
        if (this.rowBuilder_ == null) {
          ensureRowIsMutable();
          this.row_.add(index, builderForValue.build());
          onChanged();
        } else {
          this.rowBuilder_.addMessage(index, (AbstractMessage)builderForValue.build());
        } 
        return this;
      }
      
      public Builder addAllRow(Iterable<? extends MysqlxCrud.Insert.TypedRow> values) {
        if (this.rowBuilder_ == null) {
          ensureRowIsMutable();
          AbstractMessageLite.Builder.addAll(values, this.row_);
          onChanged();
        } else {
          this.rowBuilder_.addAllMessages(values);
        } 
        return this;
      }
      
      public Builder clearRow() {
        if (this.rowBuilder_ == null) {
          this.row_ = Collections.emptyList();
          this.bitField0_ &= 0xFFFFFFF7;
          onChanged();
        } else {
          this.rowBuilder_.clear();
        } 
        return this;
      }
      
      public Builder removeRow(int index) {
        if (this.rowBuilder_ == null) {
          ensureRowIsMutable();
          this.row_.remove(index);
          onChanged();
        } else {
          this.rowBuilder_.remove(index);
        } 
        return this;
      }
      
      public MysqlxCrud.Insert.TypedRow.Builder getRowBuilder(int index) {
        return (MysqlxCrud.Insert.TypedRow.Builder)getRowFieldBuilder().getBuilder(index);
      }
      
      public MysqlxCrud.Insert.TypedRowOrBuilder getRowOrBuilder(int index) {
        if (this.rowBuilder_ == null)
          return this.row_.get(index); 
        return (MysqlxCrud.Insert.TypedRowOrBuilder)this.rowBuilder_.getMessageOrBuilder(index);
      }
      
      public List<? extends MysqlxCrud.Insert.TypedRowOrBuilder> getRowOrBuilderList() {
        if (this.rowBuilder_ != null)
          return this.rowBuilder_.getMessageOrBuilderList(); 
        return Collections.unmodifiableList((List)this.row_);
      }
      
      public MysqlxCrud.Insert.TypedRow.Builder addRowBuilder() {
        return (MysqlxCrud.Insert.TypedRow.Builder)getRowFieldBuilder().addBuilder((AbstractMessage)MysqlxCrud.Insert.TypedRow.getDefaultInstance());
      }
      
      public MysqlxCrud.Insert.TypedRow.Builder addRowBuilder(int index) {
        return (MysqlxCrud.Insert.TypedRow.Builder)getRowFieldBuilder().addBuilder(index, (AbstractMessage)MysqlxCrud.Insert.TypedRow.getDefaultInstance());
      }
      
      public List<MysqlxCrud.Insert.TypedRow.Builder> getRowBuilderList() {
        return getRowFieldBuilder().getBuilderList();
      }
      
      private RepeatedFieldBuilderV3<MysqlxCrud.Insert.TypedRow, MysqlxCrud.Insert.TypedRow.Builder, MysqlxCrud.Insert.TypedRowOrBuilder> getRowFieldBuilder() {
        if (this.rowBuilder_ == null) {
          this.rowBuilder_ = new RepeatedFieldBuilderV3(this.row_, ((this.bitField0_ & 0x8) != 0), (AbstractMessage.BuilderParent)getParentForChildren(), isClean());
          this.row_ = null;
        } 
        return this.rowBuilder_;
      }
      
      private void ensureArgsIsMutable() {
        if ((this.bitField0_ & 0x10) == 0) {
          this.args_ = new ArrayList<>(this.args_);
          this.bitField0_ |= 0x10;
        } 
      }
      
      public List<MysqlxDatatypes.Scalar> getArgsList() {
        if (this.argsBuilder_ == null)
          return Collections.unmodifiableList(this.args_); 
        return this.argsBuilder_.getMessageList();
      }
      
      public int getArgsCount() {
        if (this.argsBuilder_ == null)
          return this.args_.size(); 
        return this.argsBuilder_.getCount();
      }
      
      public MysqlxDatatypes.Scalar getArgs(int index) {
        if (this.argsBuilder_ == null)
          return this.args_.get(index); 
        return (MysqlxDatatypes.Scalar)this.argsBuilder_.getMessage(index);
      }
      
      public Builder setArgs(int index, MysqlxDatatypes.Scalar value) {
        if (this.argsBuilder_ == null) {
          if (value == null)
            throw new NullPointerException(); 
          ensureArgsIsMutable();
          this.args_.set(index, value);
          onChanged();
        } else {
          this.argsBuilder_.setMessage(index, (AbstractMessage)value);
        } 
        return this;
      }
      
      public Builder setArgs(int index, MysqlxDatatypes.Scalar.Builder builderForValue) {
        if (this.argsBuilder_ == null) {
          ensureArgsIsMutable();
          this.args_.set(index, builderForValue.build());
          onChanged();
        } else {
          this.argsBuilder_.setMessage(index, (AbstractMessage)builderForValue.build());
        } 
        return this;
      }
      
      public Builder addArgs(MysqlxDatatypes.Scalar value) {
        if (this.argsBuilder_ == null) {
          if (value == null)
            throw new NullPointerException(); 
          ensureArgsIsMutable();
          this.args_.add(value);
          onChanged();
        } else {
          this.argsBuilder_.addMessage((AbstractMessage)value);
        } 
        return this;
      }
      
      public Builder addArgs(int index, MysqlxDatatypes.Scalar value) {
        if (this.argsBuilder_ == null) {
          if (value == null)
            throw new NullPointerException(); 
          ensureArgsIsMutable();
          this.args_.add(index, value);
          onChanged();
        } else {
          this.argsBuilder_.addMessage(index, (AbstractMessage)value);
        } 
        return this;
      }
      
      public Builder addArgs(MysqlxDatatypes.Scalar.Builder builderForValue) {
        if (this.argsBuilder_ == null) {
          ensureArgsIsMutable();
          this.args_.add(builderForValue.build());
          onChanged();
        } else {
          this.argsBuilder_.addMessage((AbstractMessage)builderForValue.build());
        } 
        return this;
      }
      
      public Builder addArgs(int index, MysqlxDatatypes.Scalar.Builder builderForValue) {
        if (this.argsBuilder_ == null) {
          ensureArgsIsMutable();
          this.args_.add(index, builderForValue.build());
          onChanged();
        } else {
          this.argsBuilder_.addMessage(index, (AbstractMessage)builderForValue.build());
        } 
        return this;
      }
      
      public Builder addAllArgs(Iterable<? extends MysqlxDatatypes.Scalar> values) {
        if (this.argsBuilder_ == null) {
          ensureArgsIsMutable();
          AbstractMessageLite.Builder.addAll(values, this.args_);
          onChanged();
        } else {
          this.argsBuilder_.addAllMessages(values);
        } 
        return this;
      }
      
      public Builder clearArgs() {
        if (this.argsBuilder_ == null) {
          this.args_ = Collections.emptyList();
          this.bitField0_ &= 0xFFFFFFEF;
          onChanged();
        } else {
          this.argsBuilder_.clear();
        } 
        return this;
      }
      
      public Builder removeArgs(int index) {
        if (this.argsBuilder_ == null) {
          ensureArgsIsMutable();
          this.args_.remove(index);
          onChanged();
        } else {
          this.argsBuilder_.remove(index);
        } 
        return this;
      }
      
      public MysqlxDatatypes.Scalar.Builder getArgsBuilder(int index) {
        return (MysqlxDatatypes.Scalar.Builder)getArgsFieldBuilder().getBuilder(index);
      }
      
      public MysqlxDatatypes.ScalarOrBuilder getArgsOrBuilder(int index) {
        if (this.argsBuilder_ == null)
          return this.args_.get(index); 
        return (MysqlxDatatypes.ScalarOrBuilder)this.argsBuilder_.getMessageOrBuilder(index);
      }
      
      public List<? extends MysqlxDatatypes.ScalarOrBuilder> getArgsOrBuilderList() {
        if (this.argsBuilder_ != null)
          return this.argsBuilder_.getMessageOrBuilderList(); 
        return Collections.unmodifiableList((List)this.args_);
      }
      
      public MysqlxDatatypes.Scalar.Builder addArgsBuilder() {
        return (MysqlxDatatypes.Scalar.Builder)getArgsFieldBuilder().addBuilder(
            (AbstractMessage)MysqlxDatatypes.Scalar.getDefaultInstance());
      }
      
      public MysqlxDatatypes.Scalar.Builder addArgsBuilder(int index) {
        return (MysqlxDatatypes.Scalar.Builder)getArgsFieldBuilder().addBuilder(index, 
            (AbstractMessage)MysqlxDatatypes.Scalar.getDefaultInstance());
      }
      
      public List<MysqlxDatatypes.Scalar.Builder> getArgsBuilderList() {
        return getArgsFieldBuilder().getBuilderList();
      }
      
      private RepeatedFieldBuilderV3<MysqlxDatatypes.Scalar, MysqlxDatatypes.Scalar.Builder, MysqlxDatatypes.ScalarOrBuilder> getArgsFieldBuilder() {
        if (this.argsBuilder_ == null) {
          this
            
            .argsBuilder_ = new RepeatedFieldBuilderV3(this.args_, ((this.bitField0_ & 0x10) != 0), (AbstractMessage.BuilderParent)getParentForChildren(), isClean());
          this.args_ = null;
        } 
        return this.argsBuilder_;
      }
      
      public boolean hasUpsert() {
        return ((this.bitField0_ & 0x20) != 0);
      }
      
      public boolean getUpsert() {
        return this.upsert_;
      }
      
      public Builder setUpsert(boolean value) {
        this.bitField0_ |= 0x20;
        this.upsert_ = value;
        onChanged();
        return this;
      }
      
      public Builder clearUpsert() {
        this.bitField0_ &= 0xFFFFFFDF;
        this.upsert_ = false;
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
    
    private static final Insert DEFAULT_INSTANCE = new Insert();
    
    public static Insert getDefaultInstance() {
      return DEFAULT_INSTANCE;
    }
    
    @Deprecated
    public static final Parser<Insert> PARSER = (Parser<Insert>)new AbstractParser<Insert>() {
        public MysqlxCrud.Insert parsePartialFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
          MysqlxCrud.Insert.Builder builder = MysqlxCrud.Insert.newBuilder();
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
    
    public static Parser<Insert> parser() {
      return PARSER;
    }
    
    public Parser<Insert> getParserForType() {
      return PARSER;
    }
    
    public Insert getDefaultInstanceForType() {
      return DEFAULT_INSTANCE;
    }
    
    public static interface TypedRowOrBuilder extends MessageOrBuilder {
      List<MysqlxExpr.Expr> getFieldList();
      
      MysqlxExpr.Expr getField(int param2Int);
      
      int getFieldCount();
      
      List<? extends MysqlxExpr.ExprOrBuilder> getFieldOrBuilderList();
      
      MysqlxExpr.ExprOrBuilder getFieldOrBuilder(int param2Int);
    }
  }
  
  public static interface UpdateOrBuilder extends MessageOrBuilder {
    boolean hasCollection();
    
    MysqlxCrud.Collection getCollection();
    
    MysqlxCrud.CollectionOrBuilder getCollectionOrBuilder();
    
    boolean hasDataModel();
    
    MysqlxCrud.DataModel getDataModel();
    
    boolean hasCriteria();
    
    MysqlxExpr.Expr getCriteria();
    
    MysqlxExpr.ExprOrBuilder getCriteriaOrBuilder();
    
    boolean hasLimit();
    
    MysqlxCrud.Limit getLimit();
    
    MysqlxCrud.LimitOrBuilder getLimitOrBuilder();
    
    List<MysqlxCrud.Order> getOrderList();
    
    MysqlxCrud.Order getOrder(int param1Int);
    
    int getOrderCount();
    
    List<? extends MysqlxCrud.OrderOrBuilder> getOrderOrBuilderList();
    
    MysqlxCrud.OrderOrBuilder getOrderOrBuilder(int param1Int);
    
    List<MysqlxCrud.UpdateOperation> getOperationList();
    
    MysqlxCrud.UpdateOperation getOperation(int param1Int);
    
    int getOperationCount();
    
    List<? extends MysqlxCrud.UpdateOperationOrBuilder> getOperationOrBuilderList();
    
    MysqlxCrud.UpdateOperationOrBuilder getOperationOrBuilder(int param1Int);
    
    List<MysqlxDatatypes.Scalar> getArgsList();
    
    MysqlxDatatypes.Scalar getArgs(int param1Int);
    
    int getArgsCount();
    
    List<? extends MysqlxDatatypes.ScalarOrBuilder> getArgsOrBuilderList();
    
    MysqlxDatatypes.ScalarOrBuilder getArgsOrBuilder(int param1Int);
    
    boolean hasLimitExpr();
    
    MysqlxCrud.LimitExpr getLimitExpr();
    
    MysqlxCrud.LimitExprOrBuilder getLimitExprOrBuilder();
  }
  
  public static final class Update extends GeneratedMessageV3 implements UpdateOrBuilder {
    private static final long serialVersionUID = 0L;
    
    private int bitField0_;
    
    public static final int COLLECTION_FIELD_NUMBER = 2;
    
    private MysqlxCrud.Collection collection_;
    
    public static final int DATA_MODEL_FIELD_NUMBER = 3;
    
    private int dataModel_;
    
    public static final int CRITERIA_FIELD_NUMBER = 4;
    
    private MysqlxExpr.Expr criteria_;
    
    public static final int LIMIT_FIELD_NUMBER = 5;
    
    private MysqlxCrud.Limit limit_;
    
    public static final int ORDER_FIELD_NUMBER = 6;
    
    private List<MysqlxCrud.Order> order_;
    
    public static final int OPERATION_FIELD_NUMBER = 7;
    
    private List<MysqlxCrud.UpdateOperation> operation_;
    
    public static final int ARGS_FIELD_NUMBER = 8;
    
    private List<MysqlxDatatypes.Scalar> args_;
    
    public static final int LIMIT_EXPR_FIELD_NUMBER = 9;
    
    private MysqlxCrud.LimitExpr limitExpr_;
    
    private byte memoizedIsInitialized;
    
    private Update(GeneratedMessageV3.Builder<?> builder) {
      super(builder);
      this.memoizedIsInitialized = -1;
    }
    
    private Update() {
      this.memoizedIsInitialized = -1;
      this.dataModel_ = 1;
      this.order_ = Collections.emptyList();
      this.operation_ = Collections.emptyList();
      this.args_ = Collections.emptyList();
    }
    
    protected Object newInstance(GeneratedMessageV3.UnusedPrivateParameter unused) {
      return new Update();
    }
    
    public final UnknownFieldSet getUnknownFields() {
      return this.unknownFields;
    }
    
    public static final Descriptors.Descriptor getDescriptor() {
      return MysqlxCrud.internal_static_Mysqlx_Crud_Update_descriptor;
    }
    
    protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() {
      return MysqlxCrud.internal_static_Mysqlx_Crud_Update_fieldAccessorTable.ensureFieldAccessorsInitialized(Update.class, Builder.class);
    }
    
    public boolean hasCollection() {
      return ((this.bitField0_ & 0x1) != 0);
    }
    
    public MysqlxCrud.Collection getCollection() {
      return (this.collection_ == null) ? MysqlxCrud.Collection.getDefaultInstance() : this.collection_;
    }
    
    public MysqlxCrud.CollectionOrBuilder getCollectionOrBuilder() {
      return (this.collection_ == null) ? MysqlxCrud.Collection.getDefaultInstance() : this.collection_;
    }
    
    public boolean hasDataModel() {
      return ((this.bitField0_ & 0x2) != 0);
    }
    
    public MysqlxCrud.DataModel getDataModel() {
      MysqlxCrud.DataModel result = MysqlxCrud.DataModel.valueOf(this.dataModel_);
      return (result == null) ? MysqlxCrud.DataModel.DOCUMENT : result;
    }
    
    public boolean hasCriteria() {
      return ((this.bitField0_ & 0x4) != 0);
    }
    
    public MysqlxExpr.Expr getCriteria() {
      return (this.criteria_ == null) ? MysqlxExpr.Expr.getDefaultInstance() : this.criteria_;
    }
    
    public MysqlxExpr.ExprOrBuilder getCriteriaOrBuilder() {
      return (this.criteria_ == null) ? MysqlxExpr.Expr.getDefaultInstance() : this.criteria_;
    }
    
    public boolean hasLimit() {
      return ((this.bitField0_ & 0x8) != 0);
    }
    
    public MysqlxCrud.Limit getLimit() {
      return (this.limit_ == null) ? MysqlxCrud.Limit.getDefaultInstance() : this.limit_;
    }
    
    public MysqlxCrud.LimitOrBuilder getLimitOrBuilder() {
      return (this.limit_ == null) ? MysqlxCrud.Limit.getDefaultInstance() : this.limit_;
    }
    
    public List<MysqlxCrud.Order> getOrderList() {
      return this.order_;
    }
    
    public List<? extends MysqlxCrud.OrderOrBuilder> getOrderOrBuilderList() {
      return (List)this.order_;
    }
    
    public int getOrderCount() {
      return this.order_.size();
    }
    
    public MysqlxCrud.Order getOrder(int index) {
      return this.order_.get(index);
    }
    
    public MysqlxCrud.OrderOrBuilder getOrderOrBuilder(int index) {
      return this.order_.get(index);
    }
    
    public List<MysqlxCrud.UpdateOperation> getOperationList() {
      return this.operation_;
    }
    
    public List<? extends MysqlxCrud.UpdateOperationOrBuilder> getOperationOrBuilderList() {
      return (List)this.operation_;
    }
    
    public int getOperationCount() {
      return this.operation_.size();
    }
    
    public MysqlxCrud.UpdateOperation getOperation(int index) {
      return this.operation_.get(index);
    }
    
    public MysqlxCrud.UpdateOperationOrBuilder getOperationOrBuilder(int index) {
      return this.operation_.get(index);
    }
    
    public List<MysqlxDatatypes.Scalar> getArgsList() {
      return this.args_;
    }
    
    public List<? extends MysqlxDatatypes.ScalarOrBuilder> getArgsOrBuilderList() {
      return (List)this.args_;
    }
    
    public int getArgsCount() {
      return this.args_.size();
    }
    
    public MysqlxDatatypes.Scalar getArgs(int index) {
      return this.args_.get(index);
    }
    
    public MysqlxDatatypes.ScalarOrBuilder getArgsOrBuilder(int index) {
      return this.args_.get(index);
    }
    
    public boolean hasLimitExpr() {
      return ((this.bitField0_ & 0x10) != 0);
    }
    
    public MysqlxCrud.LimitExpr getLimitExpr() {
      return (this.limitExpr_ == null) ? MysqlxCrud.LimitExpr.getDefaultInstance() : this.limitExpr_;
    }
    
    public MysqlxCrud.LimitExprOrBuilder getLimitExprOrBuilder() {
      return (this.limitExpr_ == null) ? MysqlxCrud.LimitExpr.getDefaultInstance() : this.limitExpr_;
    }
    
    public final boolean isInitialized() {
      byte isInitialized = this.memoizedIsInitialized;
      if (isInitialized == 1)
        return true; 
      if (isInitialized == 0)
        return false; 
      if (!hasCollection()) {
        this.memoizedIsInitialized = 0;
        return false;
      } 
      if (!getCollection().isInitialized()) {
        this.memoizedIsInitialized = 0;
        return false;
      } 
      if (hasCriteria() && 
        !getCriteria().isInitialized()) {
        this.memoizedIsInitialized = 0;
        return false;
      } 
      if (hasLimit() && 
        !getLimit().isInitialized()) {
        this.memoizedIsInitialized = 0;
        return false;
      } 
      int i;
      for (i = 0; i < getOrderCount(); i++) {
        if (!getOrder(i).isInitialized()) {
          this.memoizedIsInitialized = 0;
          return false;
        } 
      } 
      for (i = 0; i < getOperationCount(); i++) {
        if (!getOperation(i).isInitialized()) {
          this.memoizedIsInitialized = 0;
          return false;
        } 
      } 
      for (i = 0; i < getArgsCount(); i++) {
        if (!getArgs(i).isInitialized()) {
          this.memoizedIsInitialized = 0;
          return false;
        } 
      } 
      if (hasLimitExpr() && 
        !getLimitExpr().isInitialized()) {
        this.memoizedIsInitialized = 0;
        return false;
      } 
      this.memoizedIsInitialized = 1;
      return true;
    }
    
    public void writeTo(CodedOutputStream output) throws IOException {
      if ((this.bitField0_ & 0x1) != 0)
        output.writeMessage(2, (MessageLite)getCollection()); 
      if ((this.bitField0_ & 0x2) != 0)
        output.writeEnum(3, this.dataModel_); 
      if ((this.bitField0_ & 0x4) != 0)
        output.writeMessage(4, (MessageLite)getCriteria()); 
      if ((this.bitField0_ & 0x8) != 0)
        output.writeMessage(5, (MessageLite)getLimit()); 
      int i;
      for (i = 0; i < this.order_.size(); i++)
        output.writeMessage(6, (MessageLite)this.order_.get(i)); 
      for (i = 0; i < this.operation_.size(); i++)
        output.writeMessage(7, (MessageLite)this.operation_.get(i)); 
      for (i = 0; i < this.args_.size(); i++)
        output.writeMessage(8, (MessageLite)this.args_.get(i)); 
      if ((this.bitField0_ & 0x10) != 0)
        output.writeMessage(9, (MessageLite)getLimitExpr()); 
      getUnknownFields().writeTo(output);
    }
    
    public int getSerializedSize() {
      int size = this.memoizedSize;
      if (size != -1)
        return size; 
      size = 0;
      if ((this.bitField0_ & 0x1) != 0)
        size += 
          CodedOutputStream.computeMessageSize(2, (MessageLite)getCollection()); 
      if ((this.bitField0_ & 0x2) != 0)
        size += 
          CodedOutputStream.computeEnumSize(3, this.dataModel_); 
      if ((this.bitField0_ & 0x4) != 0)
        size += 
          CodedOutputStream.computeMessageSize(4, (MessageLite)getCriteria()); 
      if ((this.bitField0_ & 0x8) != 0)
        size += 
          CodedOutputStream.computeMessageSize(5, (MessageLite)getLimit()); 
      int i;
      for (i = 0; i < this.order_.size(); i++)
        size += 
          CodedOutputStream.computeMessageSize(6, (MessageLite)this.order_.get(i)); 
      for (i = 0; i < this.operation_.size(); i++)
        size += 
          CodedOutputStream.computeMessageSize(7, (MessageLite)this.operation_.get(i)); 
      for (i = 0; i < this.args_.size(); i++)
        size += 
          CodedOutputStream.computeMessageSize(8, (MessageLite)this.args_.get(i)); 
      if ((this.bitField0_ & 0x10) != 0)
        size += 
          CodedOutputStream.computeMessageSize(9, (MessageLite)getLimitExpr()); 
      size += getUnknownFields().getSerializedSize();
      this.memoizedSize = size;
      return size;
    }
    
    public boolean equals(Object obj) {
      if (obj == this)
        return true; 
      if (!(obj instanceof Update))
        return super.equals(obj); 
      Update other = (Update)obj;
      if (hasCollection() != other.hasCollection())
        return false; 
      if (hasCollection() && 
        
        !getCollection().equals(other.getCollection()))
        return false; 
      if (hasDataModel() != other.hasDataModel())
        return false; 
      if (hasDataModel() && 
        this.dataModel_ != other.dataModel_)
        return false; 
      if (hasCriteria() != other.hasCriteria())
        return false; 
      if (hasCriteria() && 
        
        !getCriteria().equals(other.getCriteria()))
        return false; 
      if (hasLimit() != other.hasLimit())
        return false; 
      if (hasLimit() && 
        
        !getLimit().equals(other.getLimit()))
        return false; 
      if (!getOrderList().equals(other.getOrderList()))
        return false; 
      if (!getOperationList().equals(other.getOperationList()))
        return false; 
      if (!getArgsList().equals(other.getArgsList()))
        return false; 
      if (hasLimitExpr() != other.hasLimitExpr())
        return false; 
      if (hasLimitExpr() && 
        
        !getLimitExpr().equals(other.getLimitExpr()))
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
      if (hasCollection()) {
        hash = 37 * hash + 2;
        hash = 53 * hash + getCollection().hashCode();
      } 
      if (hasDataModel()) {
        hash = 37 * hash + 3;
        hash = 53 * hash + this.dataModel_;
      } 
      if (hasCriteria()) {
        hash = 37 * hash + 4;
        hash = 53 * hash + getCriteria().hashCode();
      } 
      if (hasLimit()) {
        hash = 37 * hash + 5;
        hash = 53 * hash + getLimit().hashCode();
      } 
      if (getOrderCount() > 0) {
        hash = 37 * hash + 6;
        hash = 53 * hash + getOrderList().hashCode();
      } 
      if (getOperationCount() > 0) {
        hash = 37 * hash + 7;
        hash = 53 * hash + getOperationList().hashCode();
      } 
      if (getArgsCount() > 0) {
        hash = 37 * hash + 8;
        hash = 53 * hash + getArgsList().hashCode();
      } 
      if (hasLimitExpr()) {
        hash = 37 * hash + 9;
        hash = 53 * hash + getLimitExpr().hashCode();
      } 
      hash = 29 * hash + getUnknownFields().hashCode();
      this.memoizedHashCode = hash;
      return hash;
    }
    
    public static Update parseFrom(ByteBuffer data) throws InvalidProtocolBufferException {
      return (Update)PARSER.parseFrom(data);
    }
    
    public static Update parseFrom(ByteBuffer data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
      return (Update)PARSER.parseFrom(data, extensionRegistry);
    }
    
    public static Update parseFrom(ByteString data) throws InvalidProtocolBufferException {
      return (Update)PARSER.parseFrom(data);
    }
    
    public static Update parseFrom(ByteString data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
      return (Update)PARSER.parseFrom(data, extensionRegistry);
    }
    
    public static Update parseFrom(byte[] data) throws InvalidProtocolBufferException {
      return (Update)PARSER.parseFrom(data);
    }
    
    public static Update parseFrom(byte[] data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
      return (Update)PARSER.parseFrom(data, extensionRegistry);
    }
    
    public static Update parseFrom(InputStream input) throws IOException {
      return 
        (Update)GeneratedMessageV3.parseWithIOException(PARSER, input);
    }
    
    public static Update parseFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
      return 
        (Update)GeneratedMessageV3.parseWithIOException(PARSER, input, extensionRegistry);
    }
    
    public static Update parseDelimitedFrom(InputStream input) throws IOException {
      return 
        (Update)GeneratedMessageV3.parseDelimitedWithIOException(PARSER, input);
    }
    
    public static Update parseDelimitedFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
      return 
        (Update)GeneratedMessageV3.parseDelimitedWithIOException(PARSER, input, extensionRegistry);
    }
    
    public static Update parseFrom(CodedInputStream input) throws IOException {
      return 
        (Update)GeneratedMessageV3.parseWithIOException(PARSER, input);
    }
    
    public static Update parseFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
      return 
        (Update)GeneratedMessageV3.parseWithIOException(PARSER, input, extensionRegistry);
    }
    
    public Builder newBuilderForType() {
      return newBuilder();
    }
    
    public static Builder newBuilder() {
      return DEFAULT_INSTANCE.toBuilder();
    }
    
    public static Builder newBuilder(Update prototype) {
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
    
    public static final class Builder extends GeneratedMessageV3.Builder<Builder> implements MysqlxCrud.UpdateOrBuilder {
      private int bitField0_;
      
      private MysqlxCrud.Collection collection_;
      
      private SingleFieldBuilderV3<MysqlxCrud.Collection, MysqlxCrud.Collection.Builder, MysqlxCrud.CollectionOrBuilder> collectionBuilder_;
      
      private int dataModel_;
      
      private MysqlxExpr.Expr criteria_;
      
      private SingleFieldBuilderV3<MysqlxExpr.Expr, MysqlxExpr.Expr.Builder, MysqlxExpr.ExprOrBuilder> criteriaBuilder_;
      
      private MysqlxCrud.Limit limit_;
      
      private SingleFieldBuilderV3<MysqlxCrud.Limit, MysqlxCrud.Limit.Builder, MysqlxCrud.LimitOrBuilder> limitBuilder_;
      
      private List<MysqlxCrud.Order> order_;
      
      private RepeatedFieldBuilderV3<MysqlxCrud.Order, MysqlxCrud.Order.Builder, MysqlxCrud.OrderOrBuilder> orderBuilder_;
      
      private List<MysqlxCrud.UpdateOperation> operation_;
      
      private RepeatedFieldBuilderV3<MysqlxCrud.UpdateOperation, MysqlxCrud.UpdateOperation.Builder, MysqlxCrud.UpdateOperationOrBuilder> operationBuilder_;
      
      private List<MysqlxDatatypes.Scalar> args_;
      
      private RepeatedFieldBuilderV3<MysqlxDatatypes.Scalar, MysqlxDatatypes.Scalar.Builder, MysqlxDatatypes.ScalarOrBuilder> argsBuilder_;
      
      private MysqlxCrud.LimitExpr limitExpr_;
      
      private SingleFieldBuilderV3<MysqlxCrud.LimitExpr, MysqlxCrud.LimitExpr.Builder, MysqlxCrud.LimitExprOrBuilder> limitExprBuilder_;
      
      public static final Descriptors.Descriptor getDescriptor() {
        return MysqlxCrud.internal_static_Mysqlx_Crud_Update_descriptor;
      }
      
      protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() {
        return MysqlxCrud.internal_static_Mysqlx_Crud_Update_fieldAccessorTable
          .ensureFieldAccessorsInitialized(MysqlxCrud.Update.class, Builder.class);
      }
      
      private Builder() {
        this.dataModel_ = 1;
        this
          .order_ = Collections.emptyList();
        this
          .operation_ = Collections.emptyList();
        this
          .args_ = Collections.emptyList();
        maybeForceBuilderInitialization();
      }
      
      private Builder(GeneratedMessageV3.BuilderParent parent) {
        super(parent);
        this.dataModel_ = 1;
        this.order_ = Collections.emptyList();
        this.operation_ = Collections.emptyList();
        this.args_ = Collections.emptyList();
        maybeForceBuilderInitialization();
      }
      
      private void maybeForceBuilderInitialization() {
        if (MysqlxCrud.Update.alwaysUseFieldBuilders) {
          getCollectionFieldBuilder();
          getCriteriaFieldBuilder();
          getLimitFieldBuilder();
          getOrderFieldBuilder();
          getOperationFieldBuilder();
          getArgsFieldBuilder();
          getLimitExprFieldBuilder();
        } 
      }
      
      public Builder clear() {
        super.clear();
        if (this.collectionBuilder_ == null) {
          this.collection_ = null;
        } else {
          this.collectionBuilder_.clear();
        } 
        this.bitField0_ &= 0xFFFFFFFE;
        this.dataModel_ = 1;
        this.bitField0_ &= 0xFFFFFFFD;
        if (this.criteriaBuilder_ == null) {
          this.criteria_ = null;
        } else {
          this.criteriaBuilder_.clear();
        } 
        this.bitField0_ &= 0xFFFFFFFB;
        if (this.limitBuilder_ == null) {
          this.limit_ = null;
        } else {
          this.limitBuilder_.clear();
        } 
        this.bitField0_ &= 0xFFFFFFF7;
        if (this.orderBuilder_ == null) {
          this.order_ = Collections.emptyList();
        } else {
          this.order_ = null;
          this.orderBuilder_.clear();
        } 
        this.bitField0_ &= 0xFFFFFFEF;
        if (this.operationBuilder_ == null) {
          this.operation_ = Collections.emptyList();
        } else {
          this.operation_ = null;
          this.operationBuilder_.clear();
        } 
        this.bitField0_ &= 0xFFFFFFDF;
        if (this.argsBuilder_ == null) {
          this.args_ = Collections.emptyList();
        } else {
          this.args_ = null;
          this.argsBuilder_.clear();
        } 
        this.bitField0_ &= 0xFFFFFFBF;
        if (this.limitExprBuilder_ == null) {
          this.limitExpr_ = null;
        } else {
          this.limitExprBuilder_.clear();
        } 
        this.bitField0_ &= 0xFFFFFF7F;
        return this;
      }
      
      public Descriptors.Descriptor getDescriptorForType() {
        return MysqlxCrud.internal_static_Mysqlx_Crud_Update_descriptor;
      }
      
      public MysqlxCrud.Update getDefaultInstanceForType() {
        return MysqlxCrud.Update.getDefaultInstance();
      }
      
      public MysqlxCrud.Update build() {
        MysqlxCrud.Update result = buildPartial();
        if (!result.isInitialized())
          throw newUninitializedMessageException(result); 
        return result;
      }
      
      public MysqlxCrud.Update buildPartial() {
        MysqlxCrud.Update result = new MysqlxCrud.Update(this);
        int from_bitField0_ = this.bitField0_;
        int to_bitField0_ = 0;
        if ((from_bitField0_ & 0x1) != 0) {
          if (this.collectionBuilder_ == null) {
            result.collection_ = this.collection_;
          } else {
            result.collection_ = (MysqlxCrud.Collection)this.collectionBuilder_.build();
          } 
          to_bitField0_ |= 0x1;
        } 
        if ((from_bitField0_ & 0x2) != 0)
          to_bitField0_ |= 0x2; 
        result.dataModel_ = this.dataModel_;
        if ((from_bitField0_ & 0x4) != 0) {
          if (this.criteriaBuilder_ == null) {
            result.criteria_ = this.criteria_;
          } else {
            result.criteria_ = (MysqlxExpr.Expr)this.criteriaBuilder_.build();
          } 
          to_bitField0_ |= 0x4;
        } 
        if ((from_bitField0_ & 0x8) != 0) {
          if (this.limitBuilder_ == null) {
            result.limit_ = this.limit_;
          } else {
            result.limit_ = (MysqlxCrud.Limit)this.limitBuilder_.build();
          } 
          to_bitField0_ |= 0x8;
        } 
        if (this.orderBuilder_ == null) {
          if ((this.bitField0_ & 0x10) != 0) {
            this.order_ = Collections.unmodifiableList(this.order_);
            this.bitField0_ &= 0xFFFFFFEF;
          } 
          result.order_ = this.order_;
        } else {
          result.order_ = this.orderBuilder_.build();
        } 
        if (this.operationBuilder_ == null) {
          if ((this.bitField0_ & 0x20) != 0) {
            this.operation_ = Collections.unmodifiableList(this.operation_);
            this.bitField0_ &= 0xFFFFFFDF;
          } 
          result.operation_ = this.operation_;
        } else {
          result.operation_ = this.operationBuilder_.build();
        } 
        if (this.argsBuilder_ == null) {
          if ((this.bitField0_ & 0x40) != 0) {
            this.args_ = Collections.unmodifiableList(this.args_);
            this.bitField0_ &= 0xFFFFFFBF;
          } 
          result.args_ = this.args_;
        } else {
          result.args_ = this.argsBuilder_.build();
        } 
        if ((from_bitField0_ & 0x80) != 0) {
          if (this.limitExprBuilder_ == null) {
            result.limitExpr_ = this.limitExpr_;
          } else {
            result.limitExpr_ = (MysqlxCrud.LimitExpr)this.limitExprBuilder_.build();
          } 
          to_bitField0_ |= 0x10;
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
        if (other instanceof MysqlxCrud.Update)
          return mergeFrom((MysqlxCrud.Update)other); 
        super.mergeFrom(other);
        return this;
      }
      
      public Builder mergeFrom(MysqlxCrud.Update other) {
        if (other == MysqlxCrud.Update.getDefaultInstance())
          return this; 
        if (other.hasCollection())
          mergeCollection(other.getCollection()); 
        if (other.hasDataModel())
          setDataModel(other.getDataModel()); 
        if (other.hasCriteria())
          mergeCriteria(other.getCriteria()); 
        if (other.hasLimit())
          mergeLimit(other.getLimit()); 
        if (this.orderBuilder_ == null) {
          if (!other.order_.isEmpty()) {
            if (this.order_.isEmpty()) {
              this.order_ = other.order_;
              this.bitField0_ &= 0xFFFFFFEF;
            } else {
              ensureOrderIsMutable();
              this.order_.addAll(other.order_);
            } 
            onChanged();
          } 
        } else if (!other.order_.isEmpty()) {
          if (this.orderBuilder_.isEmpty()) {
            this.orderBuilder_.dispose();
            this.orderBuilder_ = null;
            this.order_ = other.order_;
            this.bitField0_ &= 0xFFFFFFEF;
            this.orderBuilder_ = MysqlxCrud.Update.alwaysUseFieldBuilders ? getOrderFieldBuilder() : null;
          } else {
            this.orderBuilder_.addAllMessages(other.order_);
          } 
        } 
        if (this.operationBuilder_ == null) {
          if (!other.operation_.isEmpty()) {
            if (this.operation_.isEmpty()) {
              this.operation_ = other.operation_;
              this.bitField0_ &= 0xFFFFFFDF;
            } else {
              ensureOperationIsMutable();
              this.operation_.addAll(other.operation_);
            } 
            onChanged();
          } 
        } else if (!other.operation_.isEmpty()) {
          if (this.operationBuilder_.isEmpty()) {
            this.operationBuilder_.dispose();
            this.operationBuilder_ = null;
            this.operation_ = other.operation_;
            this.bitField0_ &= 0xFFFFFFDF;
            this.operationBuilder_ = MysqlxCrud.Update.alwaysUseFieldBuilders ? getOperationFieldBuilder() : null;
          } else {
            this.operationBuilder_.addAllMessages(other.operation_);
          } 
        } 
        if (this.argsBuilder_ == null) {
          if (!other.args_.isEmpty()) {
            if (this.args_.isEmpty()) {
              this.args_ = other.args_;
              this.bitField0_ &= 0xFFFFFFBF;
            } else {
              ensureArgsIsMutable();
              this.args_.addAll(other.args_);
            } 
            onChanged();
          } 
        } else if (!other.args_.isEmpty()) {
          if (this.argsBuilder_.isEmpty()) {
            this.argsBuilder_.dispose();
            this.argsBuilder_ = null;
            this.args_ = other.args_;
            this.bitField0_ &= 0xFFFFFFBF;
            this.argsBuilder_ = MysqlxCrud.Update.alwaysUseFieldBuilders ? getArgsFieldBuilder() : null;
          } else {
            this.argsBuilder_.addAllMessages(other.args_);
          } 
        } 
        if (other.hasLimitExpr())
          mergeLimitExpr(other.getLimitExpr()); 
        mergeUnknownFields(other.getUnknownFields());
        onChanged();
        return this;
      }
      
      public final boolean isInitialized() {
        if (!hasCollection())
          return false; 
        if (!getCollection().isInitialized())
          return false; 
        if (hasCriteria() && !getCriteria().isInitialized())
          return false; 
        if (hasLimit() && !getLimit().isInitialized())
          return false; 
        int i;
        for (i = 0; i < getOrderCount(); i++) {
          if (!getOrder(i).isInitialized())
            return false; 
        } 
        for (i = 0; i < getOperationCount(); i++) {
          if (!getOperation(i).isInitialized())
            return false; 
        } 
        for (i = 0; i < getArgsCount(); i++) {
          if (!getArgs(i).isInitialized())
            return false; 
        } 
        if (hasLimitExpr() && !getLimitExpr().isInitialized())
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
            MysqlxCrud.Order order;
            MysqlxCrud.UpdateOperation updateOperation;
            MysqlxDatatypes.Scalar m;
            MysqlxCrud.DataModel tmpValue;
            int tag = input.readTag();
            switch (tag) {
              case 0:
                done = true;
                continue;
              case 18:
                input.readMessage((MessageLite.Builder)getCollectionFieldBuilder().getBuilder(), extensionRegistry);
                this.bitField0_ |= 0x1;
                continue;
              case 24:
                tmpRaw = input.readEnum();
                tmpValue = MysqlxCrud.DataModel.forNumber(tmpRaw);
                if (tmpValue == null) {
                  mergeUnknownVarintField(3, tmpRaw);
                  continue;
                } 
                this.dataModel_ = tmpRaw;
                this.bitField0_ |= 0x2;
                continue;
              case 34:
                input.readMessage((MessageLite.Builder)getCriteriaFieldBuilder().getBuilder(), extensionRegistry);
                this.bitField0_ |= 0x4;
                continue;
              case 42:
                input.readMessage((MessageLite.Builder)getLimitFieldBuilder().getBuilder(), extensionRegistry);
                this.bitField0_ |= 0x8;
                continue;
              case 50:
                order = (MysqlxCrud.Order)input.readMessage(MysqlxCrud.Order.PARSER, extensionRegistry);
                if (this.orderBuilder_ == null) {
                  ensureOrderIsMutable();
                  this.order_.add(order);
                  continue;
                } 
                this.orderBuilder_.addMessage((AbstractMessage)order);
                continue;
              case 58:
                updateOperation = (MysqlxCrud.UpdateOperation)input.readMessage(MysqlxCrud.UpdateOperation.PARSER, extensionRegistry);
                if (this.operationBuilder_ == null) {
                  ensureOperationIsMutable();
                  this.operation_.add(updateOperation);
                  continue;
                } 
                this.operationBuilder_.addMessage((AbstractMessage)updateOperation);
                continue;
              case 66:
                m = (MysqlxDatatypes.Scalar)input.readMessage(MysqlxDatatypes.Scalar.PARSER, extensionRegistry);
                if (this.argsBuilder_ == null) {
                  ensureArgsIsMutable();
                  this.args_.add(m);
                  continue;
                } 
                this.argsBuilder_.addMessage((AbstractMessage)m);
                continue;
              case 74:
                input.readMessage((MessageLite.Builder)getLimitExprFieldBuilder().getBuilder(), extensionRegistry);
                this.bitField0_ |= 0x80;
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
      
      public boolean hasCollection() {
        return ((this.bitField0_ & 0x1) != 0);
      }
      
      public MysqlxCrud.Collection getCollection() {
        if (this.collectionBuilder_ == null)
          return (this.collection_ == null) ? MysqlxCrud.Collection.getDefaultInstance() : this.collection_; 
        return (MysqlxCrud.Collection)this.collectionBuilder_.getMessage();
      }
      
      public Builder setCollection(MysqlxCrud.Collection value) {
        if (this.collectionBuilder_ == null) {
          if (value == null)
            throw new NullPointerException(); 
          this.collection_ = value;
          onChanged();
        } else {
          this.collectionBuilder_.setMessage((AbstractMessage)value);
        } 
        this.bitField0_ |= 0x1;
        return this;
      }
      
      public Builder setCollection(MysqlxCrud.Collection.Builder builderForValue) {
        if (this.collectionBuilder_ == null) {
          this.collection_ = builderForValue.build();
          onChanged();
        } else {
          this.collectionBuilder_.setMessage((AbstractMessage)builderForValue.build());
        } 
        this.bitField0_ |= 0x1;
        return this;
      }
      
      public Builder mergeCollection(MysqlxCrud.Collection value) {
        if (this.collectionBuilder_ == null) {
          if ((this.bitField0_ & 0x1) != 0 && this.collection_ != null && this.collection_ != MysqlxCrud.Collection.getDefaultInstance()) {
            this.collection_ = MysqlxCrud.Collection.newBuilder(this.collection_).mergeFrom(value).buildPartial();
          } else {
            this.collection_ = value;
          } 
          onChanged();
        } else {
          this.collectionBuilder_.mergeFrom((AbstractMessage)value);
        } 
        this.bitField0_ |= 0x1;
        return this;
      }
      
      public Builder clearCollection() {
        if (this.collectionBuilder_ == null) {
          this.collection_ = null;
          onChanged();
        } else {
          this.collectionBuilder_.clear();
        } 
        this.bitField0_ &= 0xFFFFFFFE;
        return this;
      }
      
      public MysqlxCrud.Collection.Builder getCollectionBuilder() {
        this.bitField0_ |= 0x1;
        onChanged();
        return (MysqlxCrud.Collection.Builder)getCollectionFieldBuilder().getBuilder();
      }
      
      public MysqlxCrud.CollectionOrBuilder getCollectionOrBuilder() {
        if (this.collectionBuilder_ != null)
          return (MysqlxCrud.CollectionOrBuilder)this.collectionBuilder_.getMessageOrBuilder(); 
        return (this.collection_ == null) ? MysqlxCrud.Collection.getDefaultInstance() : this.collection_;
      }
      
      private SingleFieldBuilderV3<MysqlxCrud.Collection, MysqlxCrud.Collection.Builder, MysqlxCrud.CollectionOrBuilder> getCollectionFieldBuilder() {
        if (this.collectionBuilder_ == null) {
          this.collectionBuilder_ = new SingleFieldBuilderV3((AbstractMessage)getCollection(), (AbstractMessage.BuilderParent)getParentForChildren(), isClean());
          this.collection_ = null;
        } 
        return this.collectionBuilder_;
      }
      
      public boolean hasDataModel() {
        return ((this.bitField0_ & 0x2) != 0);
      }
      
      public MysqlxCrud.DataModel getDataModel() {
        MysqlxCrud.DataModel result = MysqlxCrud.DataModel.valueOf(this.dataModel_);
        return (result == null) ? MysqlxCrud.DataModel.DOCUMENT : result;
      }
      
      public Builder setDataModel(MysqlxCrud.DataModel value) {
        if (value == null)
          throw new NullPointerException(); 
        this.bitField0_ |= 0x2;
        this.dataModel_ = value.getNumber();
        onChanged();
        return this;
      }
      
      public Builder clearDataModel() {
        this.bitField0_ &= 0xFFFFFFFD;
        this.dataModel_ = 1;
        onChanged();
        return this;
      }
      
      public boolean hasCriteria() {
        return ((this.bitField0_ & 0x4) != 0);
      }
      
      public MysqlxExpr.Expr getCriteria() {
        if (this.criteriaBuilder_ == null)
          return (this.criteria_ == null) ? MysqlxExpr.Expr.getDefaultInstance() : this.criteria_; 
        return (MysqlxExpr.Expr)this.criteriaBuilder_.getMessage();
      }
      
      public Builder setCriteria(MysqlxExpr.Expr value) {
        if (this.criteriaBuilder_ == null) {
          if (value == null)
            throw new NullPointerException(); 
          this.criteria_ = value;
          onChanged();
        } else {
          this.criteriaBuilder_.setMessage((AbstractMessage)value);
        } 
        this.bitField0_ |= 0x4;
        return this;
      }
      
      public Builder setCriteria(MysqlxExpr.Expr.Builder builderForValue) {
        if (this.criteriaBuilder_ == null) {
          this.criteria_ = builderForValue.build();
          onChanged();
        } else {
          this.criteriaBuilder_.setMessage((AbstractMessage)builderForValue.build());
        } 
        this.bitField0_ |= 0x4;
        return this;
      }
      
      public Builder mergeCriteria(MysqlxExpr.Expr value) {
        if (this.criteriaBuilder_ == null) {
          if ((this.bitField0_ & 0x4) != 0 && this.criteria_ != null && this.criteria_ != MysqlxExpr.Expr.getDefaultInstance()) {
            this.criteria_ = MysqlxExpr.Expr.newBuilder(this.criteria_).mergeFrom(value).buildPartial();
          } else {
            this.criteria_ = value;
          } 
          onChanged();
        } else {
          this.criteriaBuilder_.mergeFrom((AbstractMessage)value);
        } 
        this.bitField0_ |= 0x4;
        return this;
      }
      
      public Builder clearCriteria() {
        if (this.criteriaBuilder_ == null) {
          this.criteria_ = null;
          onChanged();
        } else {
          this.criteriaBuilder_.clear();
        } 
        this.bitField0_ &= 0xFFFFFFFB;
        return this;
      }
      
      public MysqlxExpr.Expr.Builder getCriteriaBuilder() {
        this.bitField0_ |= 0x4;
        onChanged();
        return (MysqlxExpr.Expr.Builder)getCriteriaFieldBuilder().getBuilder();
      }
      
      public MysqlxExpr.ExprOrBuilder getCriteriaOrBuilder() {
        if (this.criteriaBuilder_ != null)
          return (MysqlxExpr.ExprOrBuilder)this.criteriaBuilder_.getMessageOrBuilder(); 
        return (this.criteria_ == null) ? MysqlxExpr.Expr.getDefaultInstance() : this.criteria_;
      }
      
      private SingleFieldBuilderV3<MysqlxExpr.Expr, MysqlxExpr.Expr.Builder, MysqlxExpr.ExprOrBuilder> getCriteriaFieldBuilder() {
        if (this.criteriaBuilder_ == null) {
          this.criteriaBuilder_ = new SingleFieldBuilderV3((AbstractMessage)getCriteria(), (AbstractMessage.BuilderParent)getParentForChildren(), isClean());
          this.criteria_ = null;
        } 
        return this.criteriaBuilder_;
      }
      
      public boolean hasLimit() {
        return ((this.bitField0_ & 0x8) != 0);
      }
      
      public MysqlxCrud.Limit getLimit() {
        if (this.limitBuilder_ == null)
          return (this.limit_ == null) ? MysqlxCrud.Limit.getDefaultInstance() : this.limit_; 
        return (MysqlxCrud.Limit)this.limitBuilder_.getMessage();
      }
      
      public Builder setLimit(MysqlxCrud.Limit value) {
        if (this.limitBuilder_ == null) {
          if (value == null)
            throw new NullPointerException(); 
          this.limit_ = value;
          onChanged();
        } else {
          this.limitBuilder_.setMessage((AbstractMessage)value);
        } 
        this.bitField0_ |= 0x8;
        return this;
      }
      
      public Builder setLimit(MysqlxCrud.Limit.Builder builderForValue) {
        if (this.limitBuilder_ == null) {
          this.limit_ = builderForValue.build();
          onChanged();
        } else {
          this.limitBuilder_.setMessage((AbstractMessage)builderForValue.build());
        } 
        this.bitField0_ |= 0x8;
        return this;
      }
      
      public Builder mergeLimit(MysqlxCrud.Limit value) {
        if (this.limitBuilder_ == null) {
          if ((this.bitField0_ & 0x8) != 0 && this.limit_ != null && this.limit_ != MysqlxCrud.Limit.getDefaultInstance()) {
            this.limit_ = MysqlxCrud.Limit.newBuilder(this.limit_).mergeFrom(value).buildPartial();
          } else {
            this.limit_ = value;
          } 
          onChanged();
        } else {
          this.limitBuilder_.mergeFrom((AbstractMessage)value);
        } 
        this.bitField0_ |= 0x8;
        return this;
      }
      
      public Builder clearLimit() {
        if (this.limitBuilder_ == null) {
          this.limit_ = null;
          onChanged();
        } else {
          this.limitBuilder_.clear();
        } 
        this.bitField0_ &= 0xFFFFFFF7;
        return this;
      }
      
      public MysqlxCrud.Limit.Builder getLimitBuilder() {
        this.bitField0_ |= 0x8;
        onChanged();
        return (MysqlxCrud.Limit.Builder)getLimitFieldBuilder().getBuilder();
      }
      
      public MysqlxCrud.LimitOrBuilder getLimitOrBuilder() {
        if (this.limitBuilder_ != null)
          return (MysqlxCrud.LimitOrBuilder)this.limitBuilder_.getMessageOrBuilder(); 
        return (this.limit_ == null) ? MysqlxCrud.Limit.getDefaultInstance() : this.limit_;
      }
      
      private SingleFieldBuilderV3<MysqlxCrud.Limit, MysqlxCrud.Limit.Builder, MysqlxCrud.LimitOrBuilder> getLimitFieldBuilder() {
        if (this.limitBuilder_ == null) {
          this.limitBuilder_ = new SingleFieldBuilderV3((AbstractMessage)getLimit(), (AbstractMessage.BuilderParent)getParentForChildren(), isClean());
          this.limit_ = null;
        } 
        return this.limitBuilder_;
      }
      
      private void ensureOrderIsMutable() {
        if ((this.bitField0_ & 0x10) == 0) {
          this.order_ = new ArrayList<>(this.order_);
          this.bitField0_ |= 0x10;
        } 
      }
      
      public List<MysqlxCrud.Order> getOrderList() {
        if (this.orderBuilder_ == null)
          return Collections.unmodifiableList(this.order_); 
        return this.orderBuilder_.getMessageList();
      }
      
      public int getOrderCount() {
        if (this.orderBuilder_ == null)
          return this.order_.size(); 
        return this.orderBuilder_.getCount();
      }
      
      public MysqlxCrud.Order getOrder(int index) {
        if (this.orderBuilder_ == null)
          return this.order_.get(index); 
        return (MysqlxCrud.Order)this.orderBuilder_.getMessage(index);
      }
      
      public Builder setOrder(int index, MysqlxCrud.Order value) {
        if (this.orderBuilder_ == null) {
          if (value == null)
            throw new NullPointerException(); 
          ensureOrderIsMutable();
          this.order_.set(index, value);
          onChanged();
        } else {
          this.orderBuilder_.setMessage(index, (AbstractMessage)value);
        } 
        return this;
      }
      
      public Builder setOrder(int index, MysqlxCrud.Order.Builder builderForValue) {
        if (this.orderBuilder_ == null) {
          ensureOrderIsMutable();
          this.order_.set(index, builderForValue.build());
          onChanged();
        } else {
          this.orderBuilder_.setMessage(index, (AbstractMessage)builderForValue.build());
        } 
        return this;
      }
      
      public Builder addOrder(MysqlxCrud.Order value) {
        if (this.orderBuilder_ == null) {
          if (value == null)
            throw new NullPointerException(); 
          ensureOrderIsMutable();
          this.order_.add(value);
          onChanged();
        } else {
          this.orderBuilder_.addMessage((AbstractMessage)value);
        } 
        return this;
      }
      
      public Builder addOrder(int index, MysqlxCrud.Order value) {
        if (this.orderBuilder_ == null) {
          if (value == null)
            throw new NullPointerException(); 
          ensureOrderIsMutable();
          this.order_.add(index, value);
          onChanged();
        } else {
          this.orderBuilder_.addMessage(index, (AbstractMessage)value);
        } 
        return this;
      }
      
      public Builder addOrder(MysqlxCrud.Order.Builder builderForValue) {
        if (this.orderBuilder_ == null) {
          ensureOrderIsMutable();
          this.order_.add(builderForValue.build());
          onChanged();
        } else {
          this.orderBuilder_.addMessage((AbstractMessage)builderForValue.build());
        } 
        return this;
      }
      
      public Builder addOrder(int index, MysqlxCrud.Order.Builder builderForValue) {
        if (this.orderBuilder_ == null) {
          ensureOrderIsMutable();
          this.order_.add(index, builderForValue.build());
          onChanged();
        } else {
          this.orderBuilder_.addMessage(index, (AbstractMessage)builderForValue.build());
        } 
        return this;
      }
      
      public Builder addAllOrder(Iterable<? extends MysqlxCrud.Order> values) {
        if (this.orderBuilder_ == null) {
          ensureOrderIsMutable();
          AbstractMessageLite.Builder.addAll(values, this.order_);
          onChanged();
        } else {
          this.orderBuilder_.addAllMessages(values);
        } 
        return this;
      }
      
      public Builder clearOrder() {
        if (this.orderBuilder_ == null) {
          this.order_ = Collections.emptyList();
          this.bitField0_ &= 0xFFFFFFEF;
          onChanged();
        } else {
          this.orderBuilder_.clear();
        } 
        return this;
      }
      
      public Builder removeOrder(int index) {
        if (this.orderBuilder_ == null) {
          ensureOrderIsMutable();
          this.order_.remove(index);
          onChanged();
        } else {
          this.orderBuilder_.remove(index);
        } 
        return this;
      }
      
      public MysqlxCrud.Order.Builder getOrderBuilder(int index) {
        return (MysqlxCrud.Order.Builder)getOrderFieldBuilder().getBuilder(index);
      }
      
      public MysqlxCrud.OrderOrBuilder getOrderOrBuilder(int index) {
        if (this.orderBuilder_ == null)
          return this.order_.get(index); 
        return (MysqlxCrud.OrderOrBuilder)this.orderBuilder_.getMessageOrBuilder(index);
      }
      
      public List<? extends MysqlxCrud.OrderOrBuilder> getOrderOrBuilderList() {
        if (this.orderBuilder_ != null)
          return this.orderBuilder_.getMessageOrBuilderList(); 
        return Collections.unmodifiableList((List)this.order_);
      }
      
      public MysqlxCrud.Order.Builder addOrderBuilder() {
        return (MysqlxCrud.Order.Builder)getOrderFieldBuilder().addBuilder((AbstractMessage)MysqlxCrud.Order.getDefaultInstance());
      }
      
      public MysqlxCrud.Order.Builder addOrderBuilder(int index) {
        return (MysqlxCrud.Order.Builder)getOrderFieldBuilder().addBuilder(index, (AbstractMessage)MysqlxCrud.Order.getDefaultInstance());
      }
      
      public List<MysqlxCrud.Order.Builder> getOrderBuilderList() {
        return getOrderFieldBuilder().getBuilderList();
      }
      
      private RepeatedFieldBuilderV3<MysqlxCrud.Order, MysqlxCrud.Order.Builder, MysqlxCrud.OrderOrBuilder> getOrderFieldBuilder() {
        if (this.orderBuilder_ == null) {
          this.orderBuilder_ = new RepeatedFieldBuilderV3(this.order_, ((this.bitField0_ & 0x10) != 0), (AbstractMessage.BuilderParent)getParentForChildren(), isClean());
          this.order_ = null;
        } 
        return this.orderBuilder_;
      }
      
      private void ensureOperationIsMutable() {
        if ((this.bitField0_ & 0x20) == 0) {
          this.operation_ = new ArrayList<>(this.operation_);
          this.bitField0_ |= 0x20;
        } 
      }
      
      public List<MysqlxCrud.UpdateOperation> getOperationList() {
        if (this.operationBuilder_ == null)
          return Collections.unmodifiableList(this.operation_); 
        return this.operationBuilder_.getMessageList();
      }
      
      public int getOperationCount() {
        if (this.operationBuilder_ == null)
          return this.operation_.size(); 
        return this.operationBuilder_.getCount();
      }
      
      public MysqlxCrud.UpdateOperation getOperation(int index) {
        if (this.operationBuilder_ == null)
          return this.operation_.get(index); 
        return (MysqlxCrud.UpdateOperation)this.operationBuilder_.getMessage(index);
      }
      
      public Builder setOperation(int index, MysqlxCrud.UpdateOperation value) {
        if (this.operationBuilder_ == null) {
          if (value == null)
            throw new NullPointerException(); 
          ensureOperationIsMutable();
          this.operation_.set(index, value);
          onChanged();
        } else {
          this.operationBuilder_.setMessage(index, (AbstractMessage)value);
        } 
        return this;
      }
      
      public Builder setOperation(int index, MysqlxCrud.UpdateOperation.Builder builderForValue) {
        if (this.operationBuilder_ == null) {
          ensureOperationIsMutable();
          this.operation_.set(index, builderForValue.build());
          onChanged();
        } else {
          this.operationBuilder_.setMessage(index, (AbstractMessage)builderForValue.build());
        } 
        return this;
      }
      
      public Builder addOperation(MysqlxCrud.UpdateOperation value) {
        if (this.operationBuilder_ == null) {
          if (value == null)
            throw new NullPointerException(); 
          ensureOperationIsMutable();
          this.operation_.add(value);
          onChanged();
        } else {
          this.operationBuilder_.addMessage((AbstractMessage)value);
        } 
        return this;
      }
      
      public Builder addOperation(int index, MysqlxCrud.UpdateOperation value) {
        if (this.operationBuilder_ == null) {
          if (value == null)
            throw new NullPointerException(); 
          ensureOperationIsMutable();
          this.operation_.add(index, value);
          onChanged();
        } else {
          this.operationBuilder_.addMessage(index, (AbstractMessage)value);
        } 
        return this;
      }
      
      public Builder addOperation(MysqlxCrud.UpdateOperation.Builder builderForValue) {
        if (this.operationBuilder_ == null) {
          ensureOperationIsMutable();
          this.operation_.add(builderForValue.build());
          onChanged();
        } else {
          this.operationBuilder_.addMessage((AbstractMessage)builderForValue.build());
        } 
        return this;
      }
      
      public Builder addOperation(int index, MysqlxCrud.UpdateOperation.Builder builderForValue) {
        if (this.operationBuilder_ == null) {
          ensureOperationIsMutable();
          this.operation_.add(index, builderForValue.build());
          onChanged();
        } else {
          this.operationBuilder_.addMessage(index, (AbstractMessage)builderForValue.build());
        } 
        return this;
      }
      
      public Builder addAllOperation(Iterable<? extends MysqlxCrud.UpdateOperation> values) {
        if (this.operationBuilder_ == null) {
          ensureOperationIsMutable();
          AbstractMessageLite.Builder.addAll(values, this.operation_);
          onChanged();
        } else {
          this.operationBuilder_.addAllMessages(values);
        } 
        return this;
      }
      
      public Builder clearOperation() {
        if (this.operationBuilder_ == null) {
          this.operation_ = Collections.emptyList();
          this.bitField0_ &= 0xFFFFFFDF;
          onChanged();
        } else {
          this.operationBuilder_.clear();
        } 
        return this;
      }
      
      public Builder removeOperation(int index) {
        if (this.operationBuilder_ == null) {
          ensureOperationIsMutable();
          this.operation_.remove(index);
          onChanged();
        } else {
          this.operationBuilder_.remove(index);
        } 
        return this;
      }
      
      public MysqlxCrud.UpdateOperation.Builder getOperationBuilder(int index) {
        return (MysqlxCrud.UpdateOperation.Builder)getOperationFieldBuilder().getBuilder(index);
      }
      
      public MysqlxCrud.UpdateOperationOrBuilder getOperationOrBuilder(int index) {
        if (this.operationBuilder_ == null)
          return this.operation_.get(index); 
        return (MysqlxCrud.UpdateOperationOrBuilder)this.operationBuilder_.getMessageOrBuilder(index);
      }
      
      public List<? extends MysqlxCrud.UpdateOperationOrBuilder> getOperationOrBuilderList() {
        if (this.operationBuilder_ != null)
          return this.operationBuilder_.getMessageOrBuilderList(); 
        return Collections.unmodifiableList((List)this.operation_);
      }
      
      public MysqlxCrud.UpdateOperation.Builder addOperationBuilder() {
        return (MysqlxCrud.UpdateOperation.Builder)getOperationFieldBuilder().addBuilder((AbstractMessage)MysqlxCrud.UpdateOperation.getDefaultInstance());
      }
      
      public MysqlxCrud.UpdateOperation.Builder addOperationBuilder(int index) {
        return (MysqlxCrud.UpdateOperation.Builder)getOperationFieldBuilder().addBuilder(index, (AbstractMessage)MysqlxCrud.UpdateOperation.getDefaultInstance());
      }
      
      public List<MysqlxCrud.UpdateOperation.Builder> getOperationBuilderList() {
        return getOperationFieldBuilder().getBuilderList();
      }
      
      private RepeatedFieldBuilderV3<MysqlxCrud.UpdateOperation, MysqlxCrud.UpdateOperation.Builder, MysqlxCrud.UpdateOperationOrBuilder> getOperationFieldBuilder() {
        if (this.operationBuilder_ == null) {
          this.operationBuilder_ = new RepeatedFieldBuilderV3(this.operation_, ((this.bitField0_ & 0x20) != 0), (AbstractMessage.BuilderParent)getParentForChildren(), isClean());
          this.operation_ = null;
        } 
        return this.operationBuilder_;
      }
      
      private void ensureArgsIsMutable() {
        if ((this.bitField0_ & 0x40) == 0) {
          this.args_ = new ArrayList<>(this.args_);
          this.bitField0_ |= 0x40;
        } 
      }
      
      public List<MysqlxDatatypes.Scalar> getArgsList() {
        if (this.argsBuilder_ == null)
          return Collections.unmodifiableList(this.args_); 
        return this.argsBuilder_.getMessageList();
      }
      
      public int getArgsCount() {
        if (this.argsBuilder_ == null)
          return this.args_.size(); 
        return this.argsBuilder_.getCount();
      }
      
      public MysqlxDatatypes.Scalar getArgs(int index) {
        if (this.argsBuilder_ == null)
          return this.args_.get(index); 
        return (MysqlxDatatypes.Scalar)this.argsBuilder_.getMessage(index);
      }
      
      public Builder setArgs(int index, MysqlxDatatypes.Scalar value) {
        if (this.argsBuilder_ == null) {
          if (value == null)
            throw new NullPointerException(); 
          ensureArgsIsMutable();
          this.args_.set(index, value);
          onChanged();
        } else {
          this.argsBuilder_.setMessage(index, (AbstractMessage)value);
        } 
        return this;
      }
      
      public Builder setArgs(int index, MysqlxDatatypes.Scalar.Builder builderForValue) {
        if (this.argsBuilder_ == null) {
          ensureArgsIsMutable();
          this.args_.set(index, builderForValue.build());
          onChanged();
        } else {
          this.argsBuilder_.setMessage(index, (AbstractMessage)builderForValue.build());
        } 
        return this;
      }
      
      public Builder addArgs(MysqlxDatatypes.Scalar value) {
        if (this.argsBuilder_ == null) {
          if (value == null)
            throw new NullPointerException(); 
          ensureArgsIsMutable();
          this.args_.add(value);
          onChanged();
        } else {
          this.argsBuilder_.addMessage((AbstractMessage)value);
        } 
        return this;
      }
      
      public Builder addArgs(int index, MysqlxDatatypes.Scalar value) {
        if (this.argsBuilder_ == null) {
          if (value == null)
            throw new NullPointerException(); 
          ensureArgsIsMutable();
          this.args_.add(index, value);
          onChanged();
        } else {
          this.argsBuilder_.addMessage(index, (AbstractMessage)value);
        } 
        return this;
      }
      
      public Builder addArgs(MysqlxDatatypes.Scalar.Builder builderForValue) {
        if (this.argsBuilder_ == null) {
          ensureArgsIsMutable();
          this.args_.add(builderForValue.build());
          onChanged();
        } else {
          this.argsBuilder_.addMessage((AbstractMessage)builderForValue.build());
        } 
        return this;
      }
      
      public Builder addArgs(int index, MysqlxDatatypes.Scalar.Builder builderForValue) {
        if (this.argsBuilder_ == null) {
          ensureArgsIsMutable();
          this.args_.add(index, builderForValue.build());
          onChanged();
        } else {
          this.argsBuilder_.addMessage(index, (AbstractMessage)builderForValue.build());
        } 
        return this;
      }
      
      public Builder addAllArgs(Iterable<? extends MysqlxDatatypes.Scalar> values) {
        if (this.argsBuilder_ == null) {
          ensureArgsIsMutable();
          AbstractMessageLite.Builder.addAll(values, this.args_);
          onChanged();
        } else {
          this.argsBuilder_.addAllMessages(values);
        } 
        return this;
      }
      
      public Builder clearArgs() {
        if (this.argsBuilder_ == null) {
          this.args_ = Collections.emptyList();
          this.bitField0_ &= 0xFFFFFFBF;
          onChanged();
        } else {
          this.argsBuilder_.clear();
        } 
        return this;
      }
      
      public Builder removeArgs(int index) {
        if (this.argsBuilder_ == null) {
          ensureArgsIsMutable();
          this.args_.remove(index);
          onChanged();
        } else {
          this.argsBuilder_.remove(index);
        } 
        return this;
      }
      
      public MysqlxDatatypes.Scalar.Builder getArgsBuilder(int index) {
        return (MysqlxDatatypes.Scalar.Builder)getArgsFieldBuilder().getBuilder(index);
      }
      
      public MysqlxDatatypes.ScalarOrBuilder getArgsOrBuilder(int index) {
        if (this.argsBuilder_ == null)
          return this.args_.get(index); 
        return (MysqlxDatatypes.ScalarOrBuilder)this.argsBuilder_.getMessageOrBuilder(index);
      }
      
      public List<? extends MysqlxDatatypes.ScalarOrBuilder> getArgsOrBuilderList() {
        if (this.argsBuilder_ != null)
          return this.argsBuilder_.getMessageOrBuilderList(); 
        return Collections.unmodifiableList((List)this.args_);
      }
      
      public MysqlxDatatypes.Scalar.Builder addArgsBuilder() {
        return (MysqlxDatatypes.Scalar.Builder)getArgsFieldBuilder().addBuilder(
            (AbstractMessage)MysqlxDatatypes.Scalar.getDefaultInstance());
      }
      
      public MysqlxDatatypes.Scalar.Builder addArgsBuilder(int index) {
        return (MysqlxDatatypes.Scalar.Builder)getArgsFieldBuilder().addBuilder(index, 
            (AbstractMessage)MysqlxDatatypes.Scalar.getDefaultInstance());
      }
      
      public List<MysqlxDatatypes.Scalar.Builder> getArgsBuilderList() {
        return getArgsFieldBuilder().getBuilderList();
      }
      
      private RepeatedFieldBuilderV3<MysqlxDatatypes.Scalar, MysqlxDatatypes.Scalar.Builder, MysqlxDatatypes.ScalarOrBuilder> getArgsFieldBuilder() {
        if (this.argsBuilder_ == null) {
          this
            
            .argsBuilder_ = new RepeatedFieldBuilderV3(this.args_, ((this.bitField0_ & 0x40) != 0), (AbstractMessage.BuilderParent)getParentForChildren(), isClean());
          this.args_ = null;
        } 
        return this.argsBuilder_;
      }
      
      public boolean hasLimitExpr() {
        return ((this.bitField0_ & 0x80) != 0);
      }
      
      public MysqlxCrud.LimitExpr getLimitExpr() {
        if (this.limitExprBuilder_ == null)
          return (this.limitExpr_ == null) ? MysqlxCrud.LimitExpr.getDefaultInstance() : this.limitExpr_; 
        return (MysqlxCrud.LimitExpr)this.limitExprBuilder_.getMessage();
      }
      
      public Builder setLimitExpr(MysqlxCrud.LimitExpr value) {
        if (this.limitExprBuilder_ == null) {
          if (value == null)
            throw new NullPointerException(); 
          this.limitExpr_ = value;
          onChanged();
        } else {
          this.limitExprBuilder_.setMessage((AbstractMessage)value);
        } 
        this.bitField0_ |= 0x80;
        return this;
      }
      
      public Builder setLimitExpr(MysqlxCrud.LimitExpr.Builder builderForValue) {
        if (this.limitExprBuilder_ == null) {
          this.limitExpr_ = builderForValue.build();
          onChanged();
        } else {
          this.limitExprBuilder_.setMessage((AbstractMessage)builderForValue.build());
        } 
        this.bitField0_ |= 0x80;
        return this;
      }
      
      public Builder mergeLimitExpr(MysqlxCrud.LimitExpr value) {
        if (this.limitExprBuilder_ == null) {
          if ((this.bitField0_ & 0x80) != 0 && this.limitExpr_ != null && this.limitExpr_ != 
            
            MysqlxCrud.LimitExpr.getDefaultInstance()) {
            this
              .limitExpr_ = MysqlxCrud.LimitExpr.newBuilder(this.limitExpr_).mergeFrom(value).buildPartial();
          } else {
            this.limitExpr_ = value;
          } 
          onChanged();
        } else {
          this.limitExprBuilder_.mergeFrom((AbstractMessage)value);
        } 
        this.bitField0_ |= 0x80;
        return this;
      }
      
      public Builder clearLimitExpr() {
        if (this.limitExprBuilder_ == null) {
          this.limitExpr_ = null;
          onChanged();
        } else {
          this.limitExprBuilder_.clear();
        } 
        this.bitField0_ &= 0xFFFFFF7F;
        return this;
      }
      
      public MysqlxCrud.LimitExpr.Builder getLimitExprBuilder() {
        this.bitField0_ |= 0x80;
        onChanged();
        return (MysqlxCrud.LimitExpr.Builder)getLimitExprFieldBuilder().getBuilder();
      }
      
      public MysqlxCrud.LimitExprOrBuilder getLimitExprOrBuilder() {
        if (this.limitExprBuilder_ != null)
          return (MysqlxCrud.LimitExprOrBuilder)this.limitExprBuilder_.getMessageOrBuilder(); 
        return (this.limitExpr_ == null) ? 
          MysqlxCrud.LimitExpr.getDefaultInstance() : this.limitExpr_;
      }
      
      private SingleFieldBuilderV3<MysqlxCrud.LimitExpr, MysqlxCrud.LimitExpr.Builder, MysqlxCrud.LimitExprOrBuilder> getLimitExprFieldBuilder() {
        if (this.limitExprBuilder_ == null) {
          this
            
            .limitExprBuilder_ = new SingleFieldBuilderV3((AbstractMessage)getLimitExpr(), (AbstractMessage.BuilderParent)getParentForChildren(), isClean());
          this.limitExpr_ = null;
        } 
        return this.limitExprBuilder_;
      }
      
      public final Builder setUnknownFields(UnknownFieldSet unknownFields) {
        return (Builder)super.setUnknownFields(unknownFields);
      }
      
      public final Builder mergeUnknownFields(UnknownFieldSet unknownFields) {
        return (Builder)super.mergeUnknownFields(unknownFields);
      }
    }
    
    private static final Update DEFAULT_INSTANCE = new Update();
    
    public static Update getDefaultInstance() {
      return DEFAULT_INSTANCE;
    }
    
    @Deprecated
    public static final Parser<Update> PARSER = (Parser<Update>)new AbstractParser<Update>() {
        public MysqlxCrud.Update parsePartialFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
          MysqlxCrud.Update.Builder builder = MysqlxCrud.Update.newBuilder();
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
    
    public static Parser<Update> parser() {
      return PARSER;
    }
    
    public Parser<Update> getParserForType() {
      return PARSER;
    }
    
    public Update getDefaultInstanceForType() {
      return DEFAULT_INSTANCE;
    }
  }
  
  public static interface DeleteOrBuilder extends MessageOrBuilder {
    boolean hasCollection();
    
    MysqlxCrud.Collection getCollection();
    
    MysqlxCrud.CollectionOrBuilder getCollectionOrBuilder();
    
    boolean hasDataModel();
    
    MysqlxCrud.DataModel getDataModel();
    
    boolean hasCriteria();
    
    MysqlxExpr.Expr getCriteria();
    
    MysqlxExpr.ExprOrBuilder getCriteriaOrBuilder();
    
    boolean hasLimit();
    
    MysqlxCrud.Limit getLimit();
    
    MysqlxCrud.LimitOrBuilder getLimitOrBuilder();
    
    List<MysqlxCrud.Order> getOrderList();
    
    MysqlxCrud.Order getOrder(int param1Int);
    
    int getOrderCount();
    
    List<? extends MysqlxCrud.OrderOrBuilder> getOrderOrBuilderList();
    
    MysqlxCrud.OrderOrBuilder getOrderOrBuilder(int param1Int);
    
    List<MysqlxDatatypes.Scalar> getArgsList();
    
    MysqlxDatatypes.Scalar getArgs(int param1Int);
    
    int getArgsCount();
    
    List<? extends MysqlxDatatypes.ScalarOrBuilder> getArgsOrBuilderList();
    
    MysqlxDatatypes.ScalarOrBuilder getArgsOrBuilder(int param1Int);
    
    boolean hasLimitExpr();
    
    MysqlxCrud.LimitExpr getLimitExpr();
    
    MysqlxCrud.LimitExprOrBuilder getLimitExprOrBuilder();
  }
  
  public static final class Delete extends GeneratedMessageV3 implements DeleteOrBuilder {
    private static final long serialVersionUID = 0L;
    
    private int bitField0_;
    
    public static final int COLLECTION_FIELD_NUMBER = 1;
    
    private MysqlxCrud.Collection collection_;
    
    public static final int DATA_MODEL_FIELD_NUMBER = 2;
    
    private int dataModel_;
    
    public static final int CRITERIA_FIELD_NUMBER = 3;
    
    private MysqlxExpr.Expr criteria_;
    
    public static final int LIMIT_FIELD_NUMBER = 4;
    
    private MysqlxCrud.Limit limit_;
    
    public static final int ORDER_FIELD_NUMBER = 5;
    
    private List<MysqlxCrud.Order> order_;
    
    public static final int ARGS_FIELD_NUMBER = 6;
    
    private List<MysqlxDatatypes.Scalar> args_;
    
    public static final int LIMIT_EXPR_FIELD_NUMBER = 7;
    
    private MysqlxCrud.LimitExpr limitExpr_;
    
    private byte memoizedIsInitialized;
    
    private Delete(GeneratedMessageV3.Builder<?> builder) {
      super(builder);
      this.memoizedIsInitialized = -1;
    }
    
    private Delete() {
      this.memoizedIsInitialized = -1;
      this.dataModel_ = 1;
      this.order_ = Collections.emptyList();
      this.args_ = Collections.emptyList();
    }
    
    protected Object newInstance(GeneratedMessageV3.UnusedPrivateParameter unused) {
      return new Delete();
    }
    
    public final UnknownFieldSet getUnknownFields() {
      return this.unknownFields;
    }
    
    public static final Descriptors.Descriptor getDescriptor() {
      return MysqlxCrud.internal_static_Mysqlx_Crud_Delete_descriptor;
    }
    
    protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() {
      return MysqlxCrud.internal_static_Mysqlx_Crud_Delete_fieldAccessorTable.ensureFieldAccessorsInitialized(Delete.class, Builder.class);
    }
    
    public boolean hasCollection() {
      return ((this.bitField0_ & 0x1) != 0);
    }
    
    public MysqlxCrud.Collection getCollection() {
      return (this.collection_ == null) ? MysqlxCrud.Collection.getDefaultInstance() : this.collection_;
    }
    
    public MysqlxCrud.CollectionOrBuilder getCollectionOrBuilder() {
      return (this.collection_ == null) ? MysqlxCrud.Collection.getDefaultInstance() : this.collection_;
    }
    
    public boolean hasDataModel() {
      return ((this.bitField0_ & 0x2) != 0);
    }
    
    public MysqlxCrud.DataModel getDataModel() {
      MysqlxCrud.DataModel result = MysqlxCrud.DataModel.valueOf(this.dataModel_);
      return (result == null) ? MysqlxCrud.DataModel.DOCUMENT : result;
    }
    
    public boolean hasCriteria() {
      return ((this.bitField0_ & 0x4) != 0);
    }
    
    public MysqlxExpr.Expr getCriteria() {
      return (this.criteria_ == null) ? MysqlxExpr.Expr.getDefaultInstance() : this.criteria_;
    }
    
    public MysqlxExpr.ExprOrBuilder getCriteriaOrBuilder() {
      return (this.criteria_ == null) ? MysqlxExpr.Expr.getDefaultInstance() : this.criteria_;
    }
    
    public boolean hasLimit() {
      return ((this.bitField0_ & 0x8) != 0);
    }
    
    public MysqlxCrud.Limit getLimit() {
      return (this.limit_ == null) ? MysqlxCrud.Limit.getDefaultInstance() : this.limit_;
    }
    
    public MysqlxCrud.LimitOrBuilder getLimitOrBuilder() {
      return (this.limit_ == null) ? MysqlxCrud.Limit.getDefaultInstance() : this.limit_;
    }
    
    public List<MysqlxCrud.Order> getOrderList() {
      return this.order_;
    }
    
    public List<? extends MysqlxCrud.OrderOrBuilder> getOrderOrBuilderList() {
      return (List)this.order_;
    }
    
    public int getOrderCount() {
      return this.order_.size();
    }
    
    public MysqlxCrud.Order getOrder(int index) {
      return this.order_.get(index);
    }
    
    public MysqlxCrud.OrderOrBuilder getOrderOrBuilder(int index) {
      return this.order_.get(index);
    }
    
    public List<MysqlxDatatypes.Scalar> getArgsList() {
      return this.args_;
    }
    
    public List<? extends MysqlxDatatypes.ScalarOrBuilder> getArgsOrBuilderList() {
      return (List)this.args_;
    }
    
    public int getArgsCount() {
      return this.args_.size();
    }
    
    public MysqlxDatatypes.Scalar getArgs(int index) {
      return this.args_.get(index);
    }
    
    public MysqlxDatatypes.ScalarOrBuilder getArgsOrBuilder(int index) {
      return this.args_.get(index);
    }
    
    public boolean hasLimitExpr() {
      return ((this.bitField0_ & 0x10) != 0);
    }
    
    public MysqlxCrud.LimitExpr getLimitExpr() {
      return (this.limitExpr_ == null) ? MysqlxCrud.LimitExpr.getDefaultInstance() : this.limitExpr_;
    }
    
    public MysqlxCrud.LimitExprOrBuilder getLimitExprOrBuilder() {
      return (this.limitExpr_ == null) ? MysqlxCrud.LimitExpr.getDefaultInstance() : this.limitExpr_;
    }
    
    public final boolean isInitialized() {
      byte isInitialized = this.memoizedIsInitialized;
      if (isInitialized == 1)
        return true; 
      if (isInitialized == 0)
        return false; 
      if (!hasCollection()) {
        this.memoizedIsInitialized = 0;
        return false;
      } 
      if (!getCollection().isInitialized()) {
        this.memoizedIsInitialized = 0;
        return false;
      } 
      if (hasCriteria() && 
        !getCriteria().isInitialized()) {
        this.memoizedIsInitialized = 0;
        return false;
      } 
      if (hasLimit() && 
        !getLimit().isInitialized()) {
        this.memoizedIsInitialized = 0;
        return false;
      } 
      int i;
      for (i = 0; i < getOrderCount(); i++) {
        if (!getOrder(i).isInitialized()) {
          this.memoizedIsInitialized = 0;
          return false;
        } 
      } 
      for (i = 0; i < getArgsCount(); i++) {
        if (!getArgs(i).isInitialized()) {
          this.memoizedIsInitialized = 0;
          return false;
        } 
      } 
      if (hasLimitExpr() && 
        !getLimitExpr().isInitialized()) {
        this.memoizedIsInitialized = 0;
        return false;
      } 
      this.memoizedIsInitialized = 1;
      return true;
    }
    
    public void writeTo(CodedOutputStream output) throws IOException {
      if ((this.bitField0_ & 0x1) != 0)
        output.writeMessage(1, (MessageLite)getCollection()); 
      if ((this.bitField0_ & 0x2) != 0)
        output.writeEnum(2, this.dataModel_); 
      if ((this.bitField0_ & 0x4) != 0)
        output.writeMessage(3, (MessageLite)getCriteria()); 
      if ((this.bitField0_ & 0x8) != 0)
        output.writeMessage(4, (MessageLite)getLimit()); 
      int i;
      for (i = 0; i < this.order_.size(); i++)
        output.writeMessage(5, (MessageLite)this.order_.get(i)); 
      for (i = 0; i < this.args_.size(); i++)
        output.writeMessage(6, (MessageLite)this.args_.get(i)); 
      if ((this.bitField0_ & 0x10) != 0)
        output.writeMessage(7, (MessageLite)getLimitExpr()); 
      getUnknownFields().writeTo(output);
    }
    
    public int getSerializedSize() {
      int size = this.memoizedSize;
      if (size != -1)
        return size; 
      size = 0;
      if ((this.bitField0_ & 0x1) != 0)
        size += 
          CodedOutputStream.computeMessageSize(1, (MessageLite)getCollection()); 
      if ((this.bitField0_ & 0x2) != 0)
        size += 
          CodedOutputStream.computeEnumSize(2, this.dataModel_); 
      if ((this.bitField0_ & 0x4) != 0)
        size += 
          CodedOutputStream.computeMessageSize(3, (MessageLite)getCriteria()); 
      if ((this.bitField0_ & 0x8) != 0)
        size += 
          CodedOutputStream.computeMessageSize(4, (MessageLite)getLimit()); 
      int i;
      for (i = 0; i < this.order_.size(); i++)
        size += 
          CodedOutputStream.computeMessageSize(5, (MessageLite)this.order_.get(i)); 
      for (i = 0; i < this.args_.size(); i++)
        size += 
          CodedOutputStream.computeMessageSize(6, (MessageLite)this.args_.get(i)); 
      if ((this.bitField0_ & 0x10) != 0)
        size += 
          CodedOutputStream.computeMessageSize(7, (MessageLite)getLimitExpr()); 
      size += getUnknownFields().getSerializedSize();
      this.memoizedSize = size;
      return size;
    }
    
    public boolean equals(Object obj) {
      if (obj == this)
        return true; 
      if (!(obj instanceof Delete))
        return super.equals(obj); 
      Delete other = (Delete)obj;
      if (hasCollection() != other.hasCollection())
        return false; 
      if (hasCollection() && 
        
        !getCollection().equals(other.getCollection()))
        return false; 
      if (hasDataModel() != other.hasDataModel())
        return false; 
      if (hasDataModel() && 
        this.dataModel_ != other.dataModel_)
        return false; 
      if (hasCriteria() != other.hasCriteria())
        return false; 
      if (hasCriteria() && 
        
        !getCriteria().equals(other.getCriteria()))
        return false; 
      if (hasLimit() != other.hasLimit())
        return false; 
      if (hasLimit() && 
        
        !getLimit().equals(other.getLimit()))
        return false; 
      if (!getOrderList().equals(other.getOrderList()))
        return false; 
      if (!getArgsList().equals(other.getArgsList()))
        return false; 
      if (hasLimitExpr() != other.hasLimitExpr())
        return false; 
      if (hasLimitExpr() && 
        
        !getLimitExpr().equals(other.getLimitExpr()))
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
      if (hasCollection()) {
        hash = 37 * hash + 1;
        hash = 53 * hash + getCollection().hashCode();
      } 
      if (hasDataModel()) {
        hash = 37 * hash + 2;
        hash = 53 * hash + this.dataModel_;
      } 
      if (hasCriteria()) {
        hash = 37 * hash + 3;
        hash = 53 * hash + getCriteria().hashCode();
      } 
      if (hasLimit()) {
        hash = 37 * hash + 4;
        hash = 53 * hash + getLimit().hashCode();
      } 
      if (getOrderCount() > 0) {
        hash = 37 * hash + 5;
        hash = 53 * hash + getOrderList().hashCode();
      } 
      if (getArgsCount() > 0) {
        hash = 37 * hash + 6;
        hash = 53 * hash + getArgsList().hashCode();
      } 
      if (hasLimitExpr()) {
        hash = 37 * hash + 7;
        hash = 53 * hash + getLimitExpr().hashCode();
      } 
      hash = 29 * hash + getUnknownFields().hashCode();
      this.memoizedHashCode = hash;
      return hash;
    }
    
    public static Delete parseFrom(ByteBuffer data) throws InvalidProtocolBufferException {
      return (Delete)PARSER.parseFrom(data);
    }
    
    public static Delete parseFrom(ByteBuffer data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
      return (Delete)PARSER.parseFrom(data, extensionRegistry);
    }
    
    public static Delete parseFrom(ByteString data) throws InvalidProtocolBufferException {
      return (Delete)PARSER.parseFrom(data);
    }
    
    public static Delete parseFrom(ByteString data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
      return (Delete)PARSER.parseFrom(data, extensionRegistry);
    }
    
    public static Delete parseFrom(byte[] data) throws InvalidProtocolBufferException {
      return (Delete)PARSER.parseFrom(data);
    }
    
    public static Delete parseFrom(byte[] data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
      return (Delete)PARSER.parseFrom(data, extensionRegistry);
    }
    
    public static Delete parseFrom(InputStream input) throws IOException {
      return 
        (Delete)GeneratedMessageV3.parseWithIOException(PARSER, input);
    }
    
    public static Delete parseFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
      return 
        (Delete)GeneratedMessageV3.parseWithIOException(PARSER, input, extensionRegistry);
    }
    
    public static Delete parseDelimitedFrom(InputStream input) throws IOException {
      return 
        (Delete)GeneratedMessageV3.parseDelimitedWithIOException(PARSER, input);
    }
    
    public static Delete parseDelimitedFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
      return 
        (Delete)GeneratedMessageV3.parseDelimitedWithIOException(PARSER, input, extensionRegistry);
    }
    
    public static Delete parseFrom(CodedInputStream input) throws IOException {
      return 
        (Delete)GeneratedMessageV3.parseWithIOException(PARSER, input);
    }
    
    public static Delete parseFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
      return 
        (Delete)GeneratedMessageV3.parseWithIOException(PARSER, input, extensionRegistry);
    }
    
    public Builder newBuilderForType() {
      return newBuilder();
    }
    
    public static Builder newBuilder() {
      return DEFAULT_INSTANCE.toBuilder();
    }
    
    public static Builder newBuilder(Delete prototype) {
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
    
    public static final class Builder extends GeneratedMessageV3.Builder<Builder> implements MysqlxCrud.DeleteOrBuilder {
      private int bitField0_;
      
      private MysqlxCrud.Collection collection_;
      
      private SingleFieldBuilderV3<MysqlxCrud.Collection, MysqlxCrud.Collection.Builder, MysqlxCrud.CollectionOrBuilder> collectionBuilder_;
      
      private int dataModel_;
      
      private MysqlxExpr.Expr criteria_;
      
      private SingleFieldBuilderV3<MysqlxExpr.Expr, MysqlxExpr.Expr.Builder, MysqlxExpr.ExprOrBuilder> criteriaBuilder_;
      
      private MysqlxCrud.Limit limit_;
      
      private SingleFieldBuilderV3<MysqlxCrud.Limit, MysqlxCrud.Limit.Builder, MysqlxCrud.LimitOrBuilder> limitBuilder_;
      
      private List<MysqlxCrud.Order> order_;
      
      private RepeatedFieldBuilderV3<MysqlxCrud.Order, MysqlxCrud.Order.Builder, MysqlxCrud.OrderOrBuilder> orderBuilder_;
      
      private List<MysqlxDatatypes.Scalar> args_;
      
      private RepeatedFieldBuilderV3<MysqlxDatatypes.Scalar, MysqlxDatatypes.Scalar.Builder, MysqlxDatatypes.ScalarOrBuilder> argsBuilder_;
      
      private MysqlxCrud.LimitExpr limitExpr_;
      
      private SingleFieldBuilderV3<MysqlxCrud.LimitExpr, MysqlxCrud.LimitExpr.Builder, MysqlxCrud.LimitExprOrBuilder> limitExprBuilder_;
      
      public static final Descriptors.Descriptor getDescriptor() {
        return MysqlxCrud.internal_static_Mysqlx_Crud_Delete_descriptor;
      }
      
      protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() {
        return MysqlxCrud.internal_static_Mysqlx_Crud_Delete_fieldAccessorTable
          .ensureFieldAccessorsInitialized(MysqlxCrud.Delete.class, Builder.class);
      }
      
      private Builder() {
        this.dataModel_ = 1;
        this
          .order_ = Collections.emptyList();
        this
          .args_ = Collections.emptyList();
        maybeForceBuilderInitialization();
      }
      
      private Builder(GeneratedMessageV3.BuilderParent parent) {
        super(parent);
        this.dataModel_ = 1;
        this.order_ = Collections.emptyList();
        this.args_ = Collections.emptyList();
        maybeForceBuilderInitialization();
      }
      
      private void maybeForceBuilderInitialization() {
        if (MysqlxCrud.Delete.alwaysUseFieldBuilders) {
          getCollectionFieldBuilder();
          getCriteriaFieldBuilder();
          getLimitFieldBuilder();
          getOrderFieldBuilder();
          getArgsFieldBuilder();
          getLimitExprFieldBuilder();
        } 
      }
      
      public Builder clear() {
        super.clear();
        if (this.collectionBuilder_ == null) {
          this.collection_ = null;
        } else {
          this.collectionBuilder_.clear();
        } 
        this.bitField0_ &= 0xFFFFFFFE;
        this.dataModel_ = 1;
        this.bitField0_ &= 0xFFFFFFFD;
        if (this.criteriaBuilder_ == null) {
          this.criteria_ = null;
        } else {
          this.criteriaBuilder_.clear();
        } 
        this.bitField0_ &= 0xFFFFFFFB;
        if (this.limitBuilder_ == null) {
          this.limit_ = null;
        } else {
          this.limitBuilder_.clear();
        } 
        this.bitField0_ &= 0xFFFFFFF7;
        if (this.orderBuilder_ == null) {
          this.order_ = Collections.emptyList();
        } else {
          this.order_ = null;
          this.orderBuilder_.clear();
        } 
        this.bitField0_ &= 0xFFFFFFEF;
        if (this.argsBuilder_ == null) {
          this.args_ = Collections.emptyList();
        } else {
          this.args_ = null;
          this.argsBuilder_.clear();
        } 
        this.bitField0_ &= 0xFFFFFFDF;
        if (this.limitExprBuilder_ == null) {
          this.limitExpr_ = null;
        } else {
          this.limitExprBuilder_.clear();
        } 
        this.bitField0_ &= 0xFFFFFFBF;
        return this;
      }
      
      public Descriptors.Descriptor getDescriptorForType() {
        return MysqlxCrud.internal_static_Mysqlx_Crud_Delete_descriptor;
      }
      
      public MysqlxCrud.Delete getDefaultInstanceForType() {
        return MysqlxCrud.Delete.getDefaultInstance();
      }
      
      public MysqlxCrud.Delete build() {
        MysqlxCrud.Delete result = buildPartial();
        if (!result.isInitialized())
          throw newUninitializedMessageException(result); 
        return result;
      }
      
      public MysqlxCrud.Delete buildPartial() {
        MysqlxCrud.Delete result = new MysqlxCrud.Delete(this);
        int from_bitField0_ = this.bitField0_;
        int to_bitField0_ = 0;
        if ((from_bitField0_ & 0x1) != 0) {
          if (this.collectionBuilder_ == null) {
            result.collection_ = this.collection_;
          } else {
            result.collection_ = (MysqlxCrud.Collection)this.collectionBuilder_.build();
          } 
          to_bitField0_ |= 0x1;
        } 
        if ((from_bitField0_ & 0x2) != 0)
          to_bitField0_ |= 0x2; 
        result.dataModel_ = this.dataModel_;
        if ((from_bitField0_ & 0x4) != 0) {
          if (this.criteriaBuilder_ == null) {
            result.criteria_ = this.criteria_;
          } else {
            result.criteria_ = (MysqlxExpr.Expr)this.criteriaBuilder_.build();
          } 
          to_bitField0_ |= 0x4;
        } 
        if ((from_bitField0_ & 0x8) != 0) {
          if (this.limitBuilder_ == null) {
            result.limit_ = this.limit_;
          } else {
            result.limit_ = (MysqlxCrud.Limit)this.limitBuilder_.build();
          } 
          to_bitField0_ |= 0x8;
        } 
        if (this.orderBuilder_ == null) {
          if ((this.bitField0_ & 0x10) != 0) {
            this.order_ = Collections.unmodifiableList(this.order_);
            this.bitField0_ &= 0xFFFFFFEF;
          } 
          result.order_ = this.order_;
        } else {
          result.order_ = this.orderBuilder_.build();
        } 
        if (this.argsBuilder_ == null) {
          if ((this.bitField0_ & 0x20) != 0) {
            this.args_ = Collections.unmodifiableList(this.args_);
            this.bitField0_ &= 0xFFFFFFDF;
          } 
          result.args_ = this.args_;
        } else {
          result.args_ = this.argsBuilder_.build();
        } 
        if ((from_bitField0_ & 0x40) != 0) {
          if (this.limitExprBuilder_ == null) {
            result.limitExpr_ = this.limitExpr_;
          } else {
            result.limitExpr_ = (MysqlxCrud.LimitExpr)this.limitExprBuilder_.build();
          } 
          to_bitField0_ |= 0x10;
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
        if (other instanceof MysqlxCrud.Delete)
          return mergeFrom((MysqlxCrud.Delete)other); 
        super.mergeFrom(other);
        return this;
      }
      
      public Builder mergeFrom(MysqlxCrud.Delete other) {
        if (other == MysqlxCrud.Delete.getDefaultInstance())
          return this; 
        if (other.hasCollection())
          mergeCollection(other.getCollection()); 
        if (other.hasDataModel())
          setDataModel(other.getDataModel()); 
        if (other.hasCriteria())
          mergeCriteria(other.getCriteria()); 
        if (other.hasLimit())
          mergeLimit(other.getLimit()); 
        if (this.orderBuilder_ == null) {
          if (!other.order_.isEmpty()) {
            if (this.order_.isEmpty()) {
              this.order_ = other.order_;
              this.bitField0_ &= 0xFFFFFFEF;
            } else {
              ensureOrderIsMutable();
              this.order_.addAll(other.order_);
            } 
            onChanged();
          } 
        } else if (!other.order_.isEmpty()) {
          if (this.orderBuilder_.isEmpty()) {
            this.orderBuilder_.dispose();
            this.orderBuilder_ = null;
            this.order_ = other.order_;
            this.bitField0_ &= 0xFFFFFFEF;
            this.orderBuilder_ = MysqlxCrud.Delete.alwaysUseFieldBuilders ? getOrderFieldBuilder() : null;
          } else {
            this.orderBuilder_.addAllMessages(other.order_);
          } 
        } 
        if (this.argsBuilder_ == null) {
          if (!other.args_.isEmpty()) {
            if (this.args_.isEmpty()) {
              this.args_ = other.args_;
              this.bitField0_ &= 0xFFFFFFDF;
            } else {
              ensureArgsIsMutable();
              this.args_.addAll(other.args_);
            } 
            onChanged();
          } 
        } else if (!other.args_.isEmpty()) {
          if (this.argsBuilder_.isEmpty()) {
            this.argsBuilder_.dispose();
            this.argsBuilder_ = null;
            this.args_ = other.args_;
            this.bitField0_ &= 0xFFFFFFDF;
            this.argsBuilder_ = MysqlxCrud.Delete.alwaysUseFieldBuilders ? getArgsFieldBuilder() : null;
          } else {
            this.argsBuilder_.addAllMessages(other.args_);
          } 
        } 
        if (other.hasLimitExpr())
          mergeLimitExpr(other.getLimitExpr()); 
        mergeUnknownFields(other.getUnknownFields());
        onChanged();
        return this;
      }
      
      public final boolean isInitialized() {
        if (!hasCollection())
          return false; 
        if (!getCollection().isInitialized())
          return false; 
        if (hasCriteria() && !getCriteria().isInitialized())
          return false; 
        if (hasLimit() && !getLimit().isInitialized())
          return false; 
        int i;
        for (i = 0; i < getOrderCount(); i++) {
          if (!getOrder(i).isInitialized())
            return false; 
        } 
        for (i = 0; i < getArgsCount(); i++) {
          if (!getArgs(i).isInitialized())
            return false; 
        } 
        if (hasLimitExpr() && !getLimitExpr().isInitialized())
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
            MysqlxCrud.Order order;
            MysqlxDatatypes.Scalar m;
            MysqlxCrud.DataModel tmpValue;
            int tag = input.readTag();
            switch (tag) {
              case 0:
                done = true;
                continue;
              case 10:
                input.readMessage((MessageLite.Builder)getCollectionFieldBuilder().getBuilder(), extensionRegistry);
                this.bitField0_ |= 0x1;
                continue;
              case 16:
                tmpRaw = input.readEnum();
                tmpValue = MysqlxCrud.DataModel.forNumber(tmpRaw);
                if (tmpValue == null) {
                  mergeUnknownVarintField(2, tmpRaw);
                  continue;
                } 
                this.dataModel_ = tmpRaw;
                this.bitField0_ |= 0x2;
                continue;
              case 26:
                input.readMessage((MessageLite.Builder)getCriteriaFieldBuilder().getBuilder(), extensionRegistry);
                this.bitField0_ |= 0x4;
                continue;
              case 34:
                input.readMessage((MessageLite.Builder)getLimitFieldBuilder().getBuilder(), extensionRegistry);
                this.bitField0_ |= 0x8;
                continue;
              case 42:
                order = (MysqlxCrud.Order)input.readMessage(MysqlxCrud.Order.PARSER, extensionRegistry);
                if (this.orderBuilder_ == null) {
                  ensureOrderIsMutable();
                  this.order_.add(order);
                  continue;
                } 
                this.orderBuilder_.addMessage((AbstractMessage)order);
                continue;
              case 50:
                m = (MysqlxDatatypes.Scalar)input.readMessage(MysqlxDatatypes.Scalar.PARSER, extensionRegistry);
                if (this.argsBuilder_ == null) {
                  ensureArgsIsMutable();
                  this.args_.add(m);
                  continue;
                } 
                this.argsBuilder_.addMessage((AbstractMessage)m);
                continue;
              case 58:
                input.readMessage((MessageLite.Builder)getLimitExprFieldBuilder().getBuilder(), extensionRegistry);
                this.bitField0_ |= 0x40;
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
      
      public boolean hasCollection() {
        return ((this.bitField0_ & 0x1) != 0);
      }
      
      public MysqlxCrud.Collection getCollection() {
        if (this.collectionBuilder_ == null)
          return (this.collection_ == null) ? MysqlxCrud.Collection.getDefaultInstance() : this.collection_; 
        return (MysqlxCrud.Collection)this.collectionBuilder_.getMessage();
      }
      
      public Builder setCollection(MysqlxCrud.Collection value) {
        if (this.collectionBuilder_ == null) {
          if (value == null)
            throw new NullPointerException(); 
          this.collection_ = value;
          onChanged();
        } else {
          this.collectionBuilder_.setMessage((AbstractMessage)value);
        } 
        this.bitField0_ |= 0x1;
        return this;
      }
      
      public Builder setCollection(MysqlxCrud.Collection.Builder builderForValue) {
        if (this.collectionBuilder_ == null) {
          this.collection_ = builderForValue.build();
          onChanged();
        } else {
          this.collectionBuilder_.setMessage((AbstractMessage)builderForValue.build());
        } 
        this.bitField0_ |= 0x1;
        return this;
      }
      
      public Builder mergeCollection(MysqlxCrud.Collection value) {
        if (this.collectionBuilder_ == null) {
          if ((this.bitField0_ & 0x1) != 0 && this.collection_ != null && this.collection_ != MysqlxCrud.Collection.getDefaultInstance()) {
            this.collection_ = MysqlxCrud.Collection.newBuilder(this.collection_).mergeFrom(value).buildPartial();
          } else {
            this.collection_ = value;
          } 
          onChanged();
        } else {
          this.collectionBuilder_.mergeFrom((AbstractMessage)value);
        } 
        this.bitField0_ |= 0x1;
        return this;
      }
      
      public Builder clearCollection() {
        if (this.collectionBuilder_ == null) {
          this.collection_ = null;
          onChanged();
        } else {
          this.collectionBuilder_.clear();
        } 
        this.bitField0_ &= 0xFFFFFFFE;
        return this;
      }
      
      public MysqlxCrud.Collection.Builder getCollectionBuilder() {
        this.bitField0_ |= 0x1;
        onChanged();
        return (MysqlxCrud.Collection.Builder)getCollectionFieldBuilder().getBuilder();
      }
      
      public MysqlxCrud.CollectionOrBuilder getCollectionOrBuilder() {
        if (this.collectionBuilder_ != null)
          return (MysqlxCrud.CollectionOrBuilder)this.collectionBuilder_.getMessageOrBuilder(); 
        return (this.collection_ == null) ? MysqlxCrud.Collection.getDefaultInstance() : this.collection_;
      }
      
      private SingleFieldBuilderV3<MysqlxCrud.Collection, MysqlxCrud.Collection.Builder, MysqlxCrud.CollectionOrBuilder> getCollectionFieldBuilder() {
        if (this.collectionBuilder_ == null) {
          this.collectionBuilder_ = new SingleFieldBuilderV3((AbstractMessage)getCollection(), (AbstractMessage.BuilderParent)getParentForChildren(), isClean());
          this.collection_ = null;
        } 
        return this.collectionBuilder_;
      }
      
      public boolean hasDataModel() {
        return ((this.bitField0_ & 0x2) != 0);
      }
      
      public MysqlxCrud.DataModel getDataModel() {
        MysqlxCrud.DataModel result = MysqlxCrud.DataModel.valueOf(this.dataModel_);
        return (result == null) ? MysqlxCrud.DataModel.DOCUMENT : result;
      }
      
      public Builder setDataModel(MysqlxCrud.DataModel value) {
        if (value == null)
          throw new NullPointerException(); 
        this.bitField0_ |= 0x2;
        this.dataModel_ = value.getNumber();
        onChanged();
        return this;
      }
      
      public Builder clearDataModel() {
        this.bitField0_ &= 0xFFFFFFFD;
        this.dataModel_ = 1;
        onChanged();
        return this;
      }
      
      public boolean hasCriteria() {
        return ((this.bitField0_ & 0x4) != 0);
      }
      
      public MysqlxExpr.Expr getCriteria() {
        if (this.criteriaBuilder_ == null)
          return (this.criteria_ == null) ? MysqlxExpr.Expr.getDefaultInstance() : this.criteria_; 
        return (MysqlxExpr.Expr)this.criteriaBuilder_.getMessage();
      }
      
      public Builder setCriteria(MysqlxExpr.Expr value) {
        if (this.criteriaBuilder_ == null) {
          if (value == null)
            throw new NullPointerException(); 
          this.criteria_ = value;
          onChanged();
        } else {
          this.criteriaBuilder_.setMessage((AbstractMessage)value);
        } 
        this.bitField0_ |= 0x4;
        return this;
      }
      
      public Builder setCriteria(MysqlxExpr.Expr.Builder builderForValue) {
        if (this.criteriaBuilder_ == null) {
          this.criteria_ = builderForValue.build();
          onChanged();
        } else {
          this.criteriaBuilder_.setMessage((AbstractMessage)builderForValue.build());
        } 
        this.bitField0_ |= 0x4;
        return this;
      }
      
      public Builder mergeCriteria(MysqlxExpr.Expr value) {
        if (this.criteriaBuilder_ == null) {
          if ((this.bitField0_ & 0x4) != 0 && this.criteria_ != null && this.criteria_ != MysqlxExpr.Expr.getDefaultInstance()) {
            this.criteria_ = MysqlxExpr.Expr.newBuilder(this.criteria_).mergeFrom(value).buildPartial();
          } else {
            this.criteria_ = value;
          } 
          onChanged();
        } else {
          this.criteriaBuilder_.mergeFrom((AbstractMessage)value);
        } 
        this.bitField0_ |= 0x4;
        return this;
      }
      
      public Builder clearCriteria() {
        if (this.criteriaBuilder_ == null) {
          this.criteria_ = null;
          onChanged();
        } else {
          this.criteriaBuilder_.clear();
        } 
        this.bitField0_ &= 0xFFFFFFFB;
        return this;
      }
      
      public MysqlxExpr.Expr.Builder getCriteriaBuilder() {
        this.bitField0_ |= 0x4;
        onChanged();
        return (MysqlxExpr.Expr.Builder)getCriteriaFieldBuilder().getBuilder();
      }
      
      public MysqlxExpr.ExprOrBuilder getCriteriaOrBuilder() {
        if (this.criteriaBuilder_ != null)
          return (MysqlxExpr.ExprOrBuilder)this.criteriaBuilder_.getMessageOrBuilder(); 
        return (this.criteria_ == null) ? MysqlxExpr.Expr.getDefaultInstance() : this.criteria_;
      }
      
      private SingleFieldBuilderV3<MysqlxExpr.Expr, MysqlxExpr.Expr.Builder, MysqlxExpr.ExprOrBuilder> getCriteriaFieldBuilder() {
        if (this.criteriaBuilder_ == null) {
          this.criteriaBuilder_ = new SingleFieldBuilderV3((AbstractMessage)getCriteria(), (AbstractMessage.BuilderParent)getParentForChildren(), isClean());
          this.criteria_ = null;
        } 
        return this.criteriaBuilder_;
      }
      
      public boolean hasLimit() {
        return ((this.bitField0_ & 0x8) != 0);
      }
      
      public MysqlxCrud.Limit getLimit() {
        if (this.limitBuilder_ == null)
          return (this.limit_ == null) ? MysqlxCrud.Limit.getDefaultInstance() : this.limit_; 
        return (MysqlxCrud.Limit)this.limitBuilder_.getMessage();
      }
      
      public Builder setLimit(MysqlxCrud.Limit value) {
        if (this.limitBuilder_ == null) {
          if (value == null)
            throw new NullPointerException(); 
          this.limit_ = value;
          onChanged();
        } else {
          this.limitBuilder_.setMessage((AbstractMessage)value);
        } 
        this.bitField0_ |= 0x8;
        return this;
      }
      
      public Builder setLimit(MysqlxCrud.Limit.Builder builderForValue) {
        if (this.limitBuilder_ == null) {
          this.limit_ = builderForValue.build();
          onChanged();
        } else {
          this.limitBuilder_.setMessage((AbstractMessage)builderForValue.build());
        } 
        this.bitField0_ |= 0x8;
        return this;
      }
      
      public Builder mergeLimit(MysqlxCrud.Limit value) {
        if (this.limitBuilder_ == null) {
          if ((this.bitField0_ & 0x8) != 0 && this.limit_ != null && this.limit_ != MysqlxCrud.Limit.getDefaultInstance()) {
            this.limit_ = MysqlxCrud.Limit.newBuilder(this.limit_).mergeFrom(value).buildPartial();
          } else {
            this.limit_ = value;
          } 
          onChanged();
        } else {
          this.limitBuilder_.mergeFrom((AbstractMessage)value);
        } 
        this.bitField0_ |= 0x8;
        return this;
      }
      
      public Builder clearLimit() {
        if (this.limitBuilder_ == null) {
          this.limit_ = null;
          onChanged();
        } else {
          this.limitBuilder_.clear();
        } 
        this.bitField0_ &= 0xFFFFFFF7;
        return this;
      }
      
      public MysqlxCrud.Limit.Builder getLimitBuilder() {
        this.bitField0_ |= 0x8;
        onChanged();
        return (MysqlxCrud.Limit.Builder)getLimitFieldBuilder().getBuilder();
      }
      
      public MysqlxCrud.LimitOrBuilder getLimitOrBuilder() {
        if (this.limitBuilder_ != null)
          return (MysqlxCrud.LimitOrBuilder)this.limitBuilder_.getMessageOrBuilder(); 
        return (this.limit_ == null) ? MysqlxCrud.Limit.getDefaultInstance() : this.limit_;
      }
      
      private SingleFieldBuilderV3<MysqlxCrud.Limit, MysqlxCrud.Limit.Builder, MysqlxCrud.LimitOrBuilder> getLimitFieldBuilder() {
        if (this.limitBuilder_ == null) {
          this.limitBuilder_ = new SingleFieldBuilderV3((AbstractMessage)getLimit(), (AbstractMessage.BuilderParent)getParentForChildren(), isClean());
          this.limit_ = null;
        } 
        return this.limitBuilder_;
      }
      
      private void ensureOrderIsMutable() {
        if ((this.bitField0_ & 0x10) == 0) {
          this.order_ = new ArrayList<>(this.order_);
          this.bitField0_ |= 0x10;
        } 
      }
      
      public List<MysqlxCrud.Order> getOrderList() {
        if (this.orderBuilder_ == null)
          return Collections.unmodifiableList(this.order_); 
        return this.orderBuilder_.getMessageList();
      }
      
      public int getOrderCount() {
        if (this.orderBuilder_ == null)
          return this.order_.size(); 
        return this.orderBuilder_.getCount();
      }
      
      public MysqlxCrud.Order getOrder(int index) {
        if (this.orderBuilder_ == null)
          return this.order_.get(index); 
        return (MysqlxCrud.Order)this.orderBuilder_.getMessage(index);
      }
      
      public Builder setOrder(int index, MysqlxCrud.Order value) {
        if (this.orderBuilder_ == null) {
          if (value == null)
            throw new NullPointerException(); 
          ensureOrderIsMutable();
          this.order_.set(index, value);
          onChanged();
        } else {
          this.orderBuilder_.setMessage(index, (AbstractMessage)value);
        } 
        return this;
      }
      
      public Builder setOrder(int index, MysqlxCrud.Order.Builder builderForValue) {
        if (this.orderBuilder_ == null) {
          ensureOrderIsMutable();
          this.order_.set(index, builderForValue.build());
          onChanged();
        } else {
          this.orderBuilder_.setMessage(index, (AbstractMessage)builderForValue.build());
        } 
        return this;
      }
      
      public Builder addOrder(MysqlxCrud.Order value) {
        if (this.orderBuilder_ == null) {
          if (value == null)
            throw new NullPointerException(); 
          ensureOrderIsMutable();
          this.order_.add(value);
          onChanged();
        } else {
          this.orderBuilder_.addMessage((AbstractMessage)value);
        } 
        return this;
      }
      
      public Builder addOrder(int index, MysqlxCrud.Order value) {
        if (this.orderBuilder_ == null) {
          if (value == null)
            throw new NullPointerException(); 
          ensureOrderIsMutable();
          this.order_.add(index, value);
          onChanged();
        } else {
          this.orderBuilder_.addMessage(index, (AbstractMessage)value);
        } 
        return this;
      }
      
      public Builder addOrder(MysqlxCrud.Order.Builder builderForValue) {
        if (this.orderBuilder_ == null) {
          ensureOrderIsMutable();
          this.order_.add(builderForValue.build());
          onChanged();
        } else {
          this.orderBuilder_.addMessage((AbstractMessage)builderForValue.build());
        } 
        return this;
      }
      
      public Builder addOrder(int index, MysqlxCrud.Order.Builder builderForValue) {
        if (this.orderBuilder_ == null) {
          ensureOrderIsMutable();
          this.order_.add(index, builderForValue.build());
          onChanged();
        } else {
          this.orderBuilder_.addMessage(index, (AbstractMessage)builderForValue.build());
        } 
        return this;
      }
      
      public Builder addAllOrder(Iterable<? extends MysqlxCrud.Order> values) {
        if (this.orderBuilder_ == null) {
          ensureOrderIsMutable();
          AbstractMessageLite.Builder.addAll(values, this.order_);
          onChanged();
        } else {
          this.orderBuilder_.addAllMessages(values);
        } 
        return this;
      }
      
      public Builder clearOrder() {
        if (this.orderBuilder_ == null) {
          this.order_ = Collections.emptyList();
          this.bitField0_ &= 0xFFFFFFEF;
          onChanged();
        } else {
          this.orderBuilder_.clear();
        } 
        return this;
      }
      
      public Builder removeOrder(int index) {
        if (this.orderBuilder_ == null) {
          ensureOrderIsMutable();
          this.order_.remove(index);
          onChanged();
        } else {
          this.orderBuilder_.remove(index);
        } 
        return this;
      }
      
      public MysqlxCrud.Order.Builder getOrderBuilder(int index) {
        return (MysqlxCrud.Order.Builder)getOrderFieldBuilder().getBuilder(index);
      }
      
      public MysqlxCrud.OrderOrBuilder getOrderOrBuilder(int index) {
        if (this.orderBuilder_ == null)
          return this.order_.get(index); 
        return (MysqlxCrud.OrderOrBuilder)this.orderBuilder_.getMessageOrBuilder(index);
      }
      
      public List<? extends MysqlxCrud.OrderOrBuilder> getOrderOrBuilderList() {
        if (this.orderBuilder_ != null)
          return this.orderBuilder_.getMessageOrBuilderList(); 
        return Collections.unmodifiableList((List)this.order_);
      }
      
      public MysqlxCrud.Order.Builder addOrderBuilder() {
        return (MysqlxCrud.Order.Builder)getOrderFieldBuilder().addBuilder((AbstractMessage)MysqlxCrud.Order.getDefaultInstance());
      }
      
      public MysqlxCrud.Order.Builder addOrderBuilder(int index) {
        return (MysqlxCrud.Order.Builder)getOrderFieldBuilder().addBuilder(index, (AbstractMessage)MysqlxCrud.Order.getDefaultInstance());
      }
      
      public List<MysqlxCrud.Order.Builder> getOrderBuilderList() {
        return getOrderFieldBuilder().getBuilderList();
      }
      
      private RepeatedFieldBuilderV3<MysqlxCrud.Order, MysqlxCrud.Order.Builder, MysqlxCrud.OrderOrBuilder> getOrderFieldBuilder() {
        if (this.orderBuilder_ == null) {
          this.orderBuilder_ = new RepeatedFieldBuilderV3(this.order_, ((this.bitField0_ & 0x10) != 0), (AbstractMessage.BuilderParent)getParentForChildren(), isClean());
          this.order_ = null;
        } 
        return this.orderBuilder_;
      }
      
      private void ensureArgsIsMutable() {
        if ((this.bitField0_ & 0x20) == 0) {
          this.args_ = new ArrayList<>(this.args_);
          this.bitField0_ |= 0x20;
        } 
      }
      
      public List<MysqlxDatatypes.Scalar> getArgsList() {
        if (this.argsBuilder_ == null)
          return Collections.unmodifiableList(this.args_); 
        return this.argsBuilder_.getMessageList();
      }
      
      public int getArgsCount() {
        if (this.argsBuilder_ == null)
          return this.args_.size(); 
        return this.argsBuilder_.getCount();
      }
      
      public MysqlxDatatypes.Scalar getArgs(int index) {
        if (this.argsBuilder_ == null)
          return this.args_.get(index); 
        return (MysqlxDatatypes.Scalar)this.argsBuilder_.getMessage(index);
      }
      
      public Builder setArgs(int index, MysqlxDatatypes.Scalar value) {
        if (this.argsBuilder_ == null) {
          if (value == null)
            throw new NullPointerException(); 
          ensureArgsIsMutable();
          this.args_.set(index, value);
          onChanged();
        } else {
          this.argsBuilder_.setMessage(index, (AbstractMessage)value);
        } 
        return this;
      }
      
      public Builder setArgs(int index, MysqlxDatatypes.Scalar.Builder builderForValue) {
        if (this.argsBuilder_ == null) {
          ensureArgsIsMutable();
          this.args_.set(index, builderForValue.build());
          onChanged();
        } else {
          this.argsBuilder_.setMessage(index, (AbstractMessage)builderForValue.build());
        } 
        return this;
      }
      
      public Builder addArgs(MysqlxDatatypes.Scalar value) {
        if (this.argsBuilder_ == null) {
          if (value == null)
            throw new NullPointerException(); 
          ensureArgsIsMutable();
          this.args_.add(value);
          onChanged();
        } else {
          this.argsBuilder_.addMessage((AbstractMessage)value);
        } 
        return this;
      }
      
      public Builder addArgs(int index, MysqlxDatatypes.Scalar value) {
        if (this.argsBuilder_ == null) {
          if (value == null)
            throw new NullPointerException(); 
          ensureArgsIsMutable();
          this.args_.add(index, value);
          onChanged();
        } else {
          this.argsBuilder_.addMessage(index, (AbstractMessage)value);
        } 
        return this;
      }
      
      public Builder addArgs(MysqlxDatatypes.Scalar.Builder builderForValue) {
        if (this.argsBuilder_ == null) {
          ensureArgsIsMutable();
          this.args_.add(builderForValue.build());
          onChanged();
        } else {
          this.argsBuilder_.addMessage((AbstractMessage)builderForValue.build());
        } 
        return this;
      }
      
      public Builder addArgs(int index, MysqlxDatatypes.Scalar.Builder builderForValue) {
        if (this.argsBuilder_ == null) {
          ensureArgsIsMutable();
          this.args_.add(index, builderForValue.build());
          onChanged();
        } else {
          this.argsBuilder_.addMessage(index, (AbstractMessage)builderForValue.build());
        } 
        return this;
      }
      
      public Builder addAllArgs(Iterable<? extends MysqlxDatatypes.Scalar> values) {
        if (this.argsBuilder_ == null) {
          ensureArgsIsMutable();
          AbstractMessageLite.Builder.addAll(values, this.args_);
          onChanged();
        } else {
          this.argsBuilder_.addAllMessages(values);
        } 
        return this;
      }
      
      public Builder clearArgs() {
        if (this.argsBuilder_ == null) {
          this.args_ = Collections.emptyList();
          this.bitField0_ &= 0xFFFFFFDF;
          onChanged();
        } else {
          this.argsBuilder_.clear();
        } 
        return this;
      }
      
      public Builder removeArgs(int index) {
        if (this.argsBuilder_ == null) {
          ensureArgsIsMutable();
          this.args_.remove(index);
          onChanged();
        } else {
          this.argsBuilder_.remove(index);
        } 
        return this;
      }
      
      public MysqlxDatatypes.Scalar.Builder getArgsBuilder(int index) {
        return (MysqlxDatatypes.Scalar.Builder)getArgsFieldBuilder().getBuilder(index);
      }
      
      public MysqlxDatatypes.ScalarOrBuilder getArgsOrBuilder(int index) {
        if (this.argsBuilder_ == null)
          return this.args_.get(index); 
        return (MysqlxDatatypes.ScalarOrBuilder)this.argsBuilder_.getMessageOrBuilder(index);
      }
      
      public List<? extends MysqlxDatatypes.ScalarOrBuilder> getArgsOrBuilderList() {
        if (this.argsBuilder_ != null)
          return this.argsBuilder_.getMessageOrBuilderList(); 
        return Collections.unmodifiableList((List)this.args_);
      }
      
      public MysqlxDatatypes.Scalar.Builder addArgsBuilder() {
        return (MysqlxDatatypes.Scalar.Builder)getArgsFieldBuilder().addBuilder(
            (AbstractMessage)MysqlxDatatypes.Scalar.getDefaultInstance());
      }
      
      public MysqlxDatatypes.Scalar.Builder addArgsBuilder(int index) {
        return (MysqlxDatatypes.Scalar.Builder)getArgsFieldBuilder().addBuilder(index, 
            (AbstractMessage)MysqlxDatatypes.Scalar.getDefaultInstance());
      }
      
      public List<MysqlxDatatypes.Scalar.Builder> getArgsBuilderList() {
        return getArgsFieldBuilder().getBuilderList();
      }
      
      private RepeatedFieldBuilderV3<MysqlxDatatypes.Scalar, MysqlxDatatypes.Scalar.Builder, MysqlxDatatypes.ScalarOrBuilder> getArgsFieldBuilder() {
        if (this.argsBuilder_ == null) {
          this
            
            .argsBuilder_ = new RepeatedFieldBuilderV3(this.args_, ((this.bitField0_ & 0x20) != 0), (AbstractMessage.BuilderParent)getParentForChildren(), isClean());
          this.args_ = null;
        } 
        return this.argsBuilder_;
      }
      
      public boolean hasLimitExpr() {
        return ((this.bitField0_ & 0x40) != 0);
      }
      
      public MysqlxCrud.LimitExpr getLimitExpr() {
        if (this.limitExprBuilder_ == null)
          return (this.limitExpr_ == null) ? MysqlxCrud.LimitExpr.getDefaultInstance() : this.limitExpr_; 
        return (MysqlxCrud.LimitExpr)this.limitExprBuilder_.getMessage();
      }
      
      public Builder setLimitExpr(MysqlxCrud.LimitExpr value) {
        if (this.limitExprBuilder_ == null) {
          if (value == null)
            throw new NullPointerException(); 
          this.limitExpr_ = value;
          onChanged();
        } else {
          this.limitExprBuilder_.setMessage((AbstractMessage)value);
        } 
        this.bitField0_ |= 0x40;
        return this;
      }
      
      public Builder setLimitExpr(MysqlxCrud.LimitExpr.Builder builderForValue) {
        if (this.limitExprBuilder_ == null) {
          this.limitExpr_ = builderForValue.build();
          onChanged();
        } else {
          this.limitExprBuilder_.setMessage((AbstractMessage)builderForValue.build());
        } 
        this.bitField0_ |= 0x40;
        return this;
      }
      
      public Builder mergeLimitExpr(MysqlxCrud.LimitExpr value) {
        if (this.limitExprBuilder_ == null) {
          if ((this.bitField0_ & 0x40) != 0 && this.limitExpr_ != null && this.limitExpr_ != 
            
            MysqlxCrud.LimitExpr.getDefaultInstance()) {
            this
              .limitExpr_ = MysqlxCrud.LimitExpr.newBuilder(this.limitExpr_).mergeFrom(value).buildPartial();
          } else {
            this.limitExpr_ = value;
          } 
          onChanged();
        } else {
          this.limitExprBuilder_.mergeFrom((AbstractMessage)value);
        } 
        this.bitField0_ |= 0x40;
        return this;
      }
      
      public Builder clearLimitExpr() {
        if (this.limitExprBuilder_ == null) {
          this.limitExpr_ = null;
          onChanged();
        } else {
          this.limitExprBuilder_.clear();
        } 
        this.bitField0_ &= 0xFFFFFFBF;
        return this;
      }
      
      public MysqlxCrud.LimitExpr.Builder getLimitExprBuilder() {
        this.bitField0_ |= 0x40;
        onChanged();
        return (MysqlxCrud.LimitExpr.Builder)getLimitExprFieldBuilder().getBuilder();
      }
      
      public MysqlxCrud.LimitExprOrBuilder getLimitExprOrBuilder() {
        if (this.limitExprBuilder_ != null)
          return (MysqlxCrud.LimitExprOrBuilder)this.limitExprBuilder_.getMessageOrBuilder(); 
        return (this.limitExpr_ == null) ? 
          MysqlxCrud.LimitExpr.getDefaultInstance() : this.limitExpr_;
      }
      
      private SingleFieldBuilderV3<MysqlxCrud.LimitExpr, MysqlxCrud.LimitExpr.Builder, MysqlxCrud.LimitExprOrBuilder> getLimitExprFieldBuilder() {
        if (this.limitExprBuilder_ == null) {
          this
            
            .limitExprBuilder_ = new SingleFieldBuilderV3((AbstractMessage)getLimitExpr(), (AbstractMessage.BuilderParent)getParentForChildren(), isClean());
          this.limitExpr_ = null;
        } 
        return this.limitExprBuilder_;
      }
      
      public final Builder setUnknownFields(UnknownFieldSet unknownFields) {
        return (Builder)super.setUnknownFields(unknownFields);
      }
      
      public final Builder mergeUnknownFields(UnknownFieldSet unknownFields) {
        return (Builder)super.mergeUnknownFields(unknownFields);
      }
    }
    
    private static final Delete DEFAULT_INSTANCE = new Delete();
    
    public static Delete getDefaultInstance() {
      return DEFAULT_INSTANCE;
    }
    
    @Deprecated
    public static final Parser<Delete> PARSER = (Parser<Delete>)new AbstractParser<Delete>() {
        public MysqlxCrud.Delete parsePartialFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
          MysqlxCrud.Delete.Builder builder = MysqlxCrud.Delete.newBuilder();
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
    
    public static Parser<Delete> parser() {
      return PARSER;
    }
    
    public Parser<Delete> getParserForType() {
      return PARSER;
    }
    
    public Delete getDefaultInstanceForType() {
      return DEFAULT_INSTANCE;
    }
  }
  
  public static interface CreateViewOrBuilder extends MessageOrBuilder {
    boolean hasCollection();
    
    MysqlxCrud.Collection getCollection();
    
    MysqlxCrud.CollectionOrBuilder getCollectionOrBuilder();
    
    boolean hasDefiner();
    
    String getDefiner();
    
    ByteString getDefinerBytes();
    
    boolean hasAlgorithm();
    
    MysqlxCrud.ViewAlgorithm getAlgorithm();
    
    boolean hasSecurity();
    
    MysqlxCrud.ViewSqlSecurity getSecurity();
    
    boolean hasCheck();
    
    MysqlxCrud.ViewCheckOption getCheck();
    
    List<String> getColumnList();
    
    int getColumnCount();
    
    String getColumn(int param1Int);
    
    ByteString getColumnBytes(int param1Int);
    
    boolean hasStmt();
    
    MysqlxCrud.Find getStmt();
    
    MysqlxCrud.FindOrBuilder getStmtOrBuilder();
    
    boolean hasReplaceExisting();
    
    boolean getReplaceExisting();
  }
  
  public static final class CreateView extends GeneratedMessageV3 implements CreateViewOrBuilder {
    private static final long serialVersionUID = 0L;
    
    private int bitField0_;
    
    public static final int COLLECTION_FIELD_NUMBER = 1;
    
    private MysqlxCrud.Collection collection_;
    
    public static final int DEFINER_FIELD_NUMBER = 2;
    
    private volatile Object definer_;
    
    public static final int ALGORITHM_FIELD_NUMBER = 3;
    
    private int algorithm_;
    
    public static final int SECURITY_FIELD_NUMBER = 4;
    
    private int security_;
    
    public static final int CHECK_FIELD_NUMBER = 5;
    
    private int check_;
    
    public static final int COLUMN_FIELD_NUMBER = 6;
    
    private LazyStringList column_;
    
    public static final int STMT_FIELD_NUMBER = 7;
    
    private MysqlxCrud.Find stmt_;
    
    public static final int REPLACE_EXISTING_FIELD_NUMBER = 8;
    
    private boolean replaceExisting_;
    
    private byte memoizedIsInitialized;
    
    private CreateView(GeneratedMessageV3.Builder<?> builder) {
      super(builder);
      this.memoizedIsInitialized = -1;
    }
    
    private CreateView() {
      this.memoizedIsInitialized = -1;
      this.definer_ = "";
      this.algorithm_ = 1;
      this.security_ = 2;
      this.check_ = 1;
      this.column_ = LazyStringArrayList.EMPTY;
    }
    
    protected Object newInstance(GeneratedMessageV3.UnusedPrivateParameter unused) {
      return new CreateView();
    }
    
    public final UnknownFieldSet getUnknownFields() {
      return this.unknownFields;
    }
    
    public static final Descriptors.Descriptor getDescriptor() {
      return MysqlxCrud.internal_static_Mysqlx_Crud_CreateView_descriptor;
    }
    
    protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() {
      return MysqlxCrud.internal_static_Mysqlx_Crud_CreateView_fieldAccessorTable.ensureFieldAccessorsInitialized(CreateView.class, Builder.class);
    }
    
    public boolean hasCollection() {
      return ((this.bitField0_ & 0x1) != 0);
    }
    
    public MysqlxCrud.Collection getCollection() {
      return (this.collection_ == null) ? MysqlxCrud.Collection.getDefaultInstance() : this.collection_;
    }
    
    public MysqlxCrud.CollectionOrBuilder getCollectionOrBuilder() {
      return (this.collection_ == null) ? MysqlxCrud.Collection.getDefaultInstance() : this.collection_;
    }
    
    public boolean hasDefiner() {
      return ((this.bitField0_ & 0x2) != 0);
    }
    
    public String getDefiner() {
      Object ref = this.definer_;
      if (ref instanceof String)
        return (String)ref; 
      ByteString bs = (ByteString)ref;
      String s = bs.toStringUtf8();
      if (bs.isValidUtf8())
        this.definer_ = s; 
      return s;
    }
    
    public ByteString getDefinerBytes() {
      Object ref = this.definer_;
      if (ref instanceof String) {
        ByteString b = ByteString.copyFromUtf8((String)ref);
        this.definer_ = b;
        return b;
      } 
      return (ByteString)ref;
    }
    
    public boolean hasAlgorithm() {
      return ((this.bitField0_ & 0x4) != 0);
    }
    
    public MysqlxCrud.ViewAlgorithm getAlgorithm() {
      MysqlxCrud.ViewAlgorithm result = MysqlxCrud.ViewAlgorithm.valueOf(this.algorithm_);
      return (result == null) ? MysqlxCrud.ViewAlgorithm.UNDEFINED : result;
    }
    
    public boolean hasSecurity() {
      return ((this.bitField0_ & 0x8) != 0);
    }
    
    public MysqlxCrud.ViewSqlSecurity getSecurity() {
      MysqlxCrud.ViewSqlSecurity result = MysqlxCrud.ViewSqlSecurity.valueOf(this.security_);
      return (result == null) ? MysqlxCrud.ViewSqlSecurity.DEFINER : result;
    }
    
    public boolean hasCheck() {
      return ((this.bitField0_ & 0x10) != 0);
    }
    
    public MysqlxCrud.ViewCheckOption getCheck() {
      MysqlxCrud.ViewCheckOption result = MysqlxCrud.ViewCheckOption.valueOf(this.check_);
      return (result == null) ? MysqlxCrud.ViewCheckOption.LOCAL : result;
    }
    
    public ProtocolStringList getColumnList() {
      return (ProtocolStringList)this.column_;
    }
    
    public int getColumnCount() {
      return this.column_.size();
    }
    
    public String getColumn(int index) {
      return (String)this.column_.get(index);
    }
    
    public ByteString getColumnBytes(int index) {
      return this.column_.getByteString(index);
    }
    
    public boolean hasStmt() {
      return ((this.bitField0_ & 0x20) != 0);
    }
    
    public MysqlxCrud.Find getStmt() {
      return (this.stmt_ == null) ? MysqlxCrud.Find.getDefaultInstance() : this.stmt_;
    }
    
    public MysqlxCrud.FindOrBuilder getStmtOrBuilder() {
      return (this.stmt_ == null) ? MysqlxCrud.Find.getDefaultInstance() : this.stmt_;
    }
    
    public boolean hasReplaceExisting() {
      return ((this.bitField0_ & 0x40) != 0);
    }
    
    public boolean getReplaceExisting() {
      return this.replaceExisting_;
    }
    
    public final boolean isInitialized() {
      byte isInitialized = this.memoizedIsInitialized;
      if (isInitialized == 1)
        return true; 
      if (isInitialized == 0)
        return false; 
      if (!hasCollection()) {
        this.memoizedIsInitialized = 0;
        return false;
      } 
      if (!hasStmt()) {
        this.memoizedIsInitialized = 0;
        return false;
      } 
      if (!getCollection().isInitialized()) {
        this.memoizedIsInitialized = 0;
        return false;
      } 
      if (!getStmt().isInitialized()) {
        this.memoizedIsInitialized = 0;
        return false;
      } 
      this.memoizedIsInitialized = 1;
      return true;
    }
    
    public void writeTo(CodedOutputStream output) throws IOException {
      if ((this.bitField0_ & 0x1) != 0)
        output.writeMessage(1, (MessageLite)getCollection()); 
      if ((this.bitField0_ & 0x2) != 0)
        GeneratedMessageV3.writeString(output, 2, this.definer_); 
      if ((this.bitField0_ & 0x4) != 0)
        output.writeEnum(3, this.algorithm_); 
      if ((this.bitField0_ & 0x8) != 0)
        output.writeEnum(4, this.security_); 
      if ((this.bitField0_ & 0x10) != 0)
        output.writeEnum(5, this.check_); 
      for (int i = 0; i < this.column_.size(); i++)
        GeneratedMessageV3.writeString(output, 6, this.column_.getRaw(i)); 
      if ((this.bitField0_ & 0x20) != 0)
        output.writeMessage(7, (MessageLite)getStmt()); 
      if ((this.bitField0_ & 0x40) != 0)
        output.writeBool(8, this.replaceExisting_); 
      getUnknownFields().writeTo(output);
    }
    
    public int getSerializedSize() {
      int size = this.memoizedSize;
      if (size != -1)
        return size; 
      size = 0;
      if ((this.bitField0_ & 0x1) != 0)
        size += 
          CodedOutputStream.computeMessageSize(1, (MessageLite)getCollection()); 
      if ((this.bitField0_ & 0x2) != 0)
        size += GeneratedMessageV3.computeStringSize(2, this.definer_); 
      if ((this.bitField0_ & 0x4) != 0)
        size += 
          CodedOutputStream.computeEnumSize(3, this.algorithm_); 
      if ((this.bitField0_ & 0x8) != 0)
        size += 
          CodedOutputStream.computeEnumSize(4, this.security_); 
      if ((this.bitField0_ & 0x10) != 0)
        size += 
          CodedOutputStream.computeEnumSize(5, this.check_); 
      int dataSize = 0;
      for (int i = 0; i < this.column_.size(); i++)
        dataSize += computeStringSizeNoTag(this.column_.getRaw(i)); 
      size += dataSize;
      size += 1 * getColumnList().size();
      if ((this.bitField0_ & 0x20) != 0)
        size += 
          CodedOutputStream.computeMessageSize(7, (MessageLite)getStmt()); 
      if ((this.bitField0_ & 0x40) != 0)
        size += 
          CodedOutputStream.computeBoolSize(8, this.replaceExisting_); 
      size += getUnknownFields().getSerializedSize();
      this.memoizedSize = size;
      return size;
    }
    
    public boolean equals(Object obj) {
      if (obj == this)
        return true; 
      if (!(obj instanceof CreateView))
        return super.equals(obj); 
      CreateView other = (CreateView)obj;
      if (hasCollection() != other.hasCollection())
        return false; 
      if (hasCollection() && 
        
        !getCollection().equals(other.getCollection()))
        return false; 
      if (hasDefiner() != other.hasDefiner())
        return false; 
      if (hasDefiner() && 
        
        !getDefiner().equals(other.getDefiner()))
        return false; 
      if (hasAlgorithm() != other.hasAlgorithm())
        return false; 
      if (hasAlgorithm() && 
        this.algorithm_ != other.algorithm_)
        return false; 
      if (hasSecurity() != other.hasSecurity())
        return false; 
      if (hasSecurity() && 
        this.security_ != other.security_)
        return false; 
      if (hasCheck() != other.hasCheck())
        return false; 
      if (hasCheck() && 
        this.check_ != other.check_)
        return false; 
      if (!getColumnList().equals(other.getColumnList()))
        return false; 
      if (hasStmt() != other.hasStmt())
        return false; 
      if (hasStmt() && 
        
        !getStmt().equals(other.getStmt()))
        return false; 
      if (hasReplaceExisting() != other.hasReplaceExisting())
        return false; 
      if (hasReplaceExisting() && 
        getReplaceExisting() != other
        .getReplaceExisting())
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
      if (hasCollection()) {
        hash = 37 * hash + 1;
        hash = 53 * hash + getCollection().hashCode();
      } 
      if (hasDefiner()) {
        hash = 37 * hash + 2;
        hash = 53 * hash + getDefiner().hashCode();
      } 
      if (hasAlgorithm()) {
        hash = 37 * hash + 3;
        hash = 53 * hash + this.algorithm_;
      } 
      if (hasSecurity()) {
        hash = 37 * hash + 4;
        hash = 53 * hash + this.security_;
      } 
      if (hasCheck()) {
        hash = 37 * hash + 5;
        hash = 53 * hash + this.check_;
      } 
      if (getColumnCount() > 0) {
        hash = 37 * hash + 6;
        hash = 53 * hash + getColumnList().hashCode();
      } 
      if (hasStmt()) {
        hash = 37 * hash + 7;
        hash = 53 * hash + getStmt().hashCode();
      } 
      if (hasReplaceExisting()) {
        hash = 37 * hash + 8;
        hash = 53 * hash + Internal.hashBoolean(
            getReplaceExisting());
      } 
      hash = 29 * hash + getUnknownFields().hashCode();
      this.memoizedHashCode = hash;
      return hash;
    }
    
    public static CreateView parseFrom(ByteBuffer data) throws InvalidProtocolBufferException {
      return (CreateView)PARSER.parseFrom(data);
    }
    
    public static CreateView parseFrom(ByteBuffer data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
      return (CreateView)PARSER.parseFrom(data, extensionRegistry);
    }
    
    public static CreateView parseFrom(ByteString data) throws InvalidProtocolBufferException {
      return (CreateView)PARSER.parseFrom(data);
    }
    
    public static CreateView parseFrom(ByteString data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
      return (CreateView)PARSER.parseFrom(data, extensionRegistry);
    }
    
    public static CreateView parseFrom(byte[] data) throws InvalidProtocolBufferException {
      return (CreateView)PARSER.parseFrom(data);
    }
    
    public static CreateView parseFrom(byte[] data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
      return (CreateView)PARSER.parseFrom(data, extensionRegistry);
    }
    
    public static CreateView parseFrom(InputStream input) throws IOException {
      return 
        (CreateView)GeneratedMessageV3.parseWithIOException(PARSER, input);
    }
    
    public static CreateView parseFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
      return 
        (CreateView)GeneratedMessageV3.parseWithIOException(PARSER, input, extensionRegistry);
    }
    
    public static CreateView parseDelimitedFrom(InputStream input) throws IOException {
      return 
        (CreateView)GeneratedMessageV3.parseDelimitedWithIOException(PARSER, input);
    }
    
    public static CreateView parseDelimitedFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
      return 
        (CreateView)GeneratedMessageV3.parseDelimitedWithIOException(PARSER, input, extensionRegistry);
    }
    
    public static CreateView parseFrom(CodedInputStream input) throws IOException {
      return 
        (CreateView)GeneratedMessageV3.parseWithIOException(PARSER, input);
    }
    
    public static CreateView parseFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
      return 
        (CreateView)GeneratedMessageV3.parseWithIOException(PARSER, input, extensionRegistry);
    }
    
    public Builder newBuilderForType() {
      return newBuilder();
    }
    
    public static Builder newBuilder() {
      return DEFAULT_INSTANCE.toBuilder();
    }
    
    public static Builder newBuilder(CreateView prototype) {
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
    
    public static final class Builder extends GeneratedMessageV3.Builder<Builder> implements MysqlxCrud.CreateViewOrBuilder {
      private int bitField0_;
      
      private MysqlxCrud.Collection collection_;
      
      private SingleFieldBuilderV3<MysqlxCrud.Collection, MysqlxCrud.Collection.Builder, MysqlxCrud.CollectionOrBuilder> collectionBuilder_;
      
      private Object definer_;
      
      private int algorithm_;
      
      private int security_;
      
      private int check_;
      
      private LazyStringList column_;
      
      private MysqlxCrud.Find stmt_;
      
      private SingleFieldBuilderV3<MysqlxCrud.Find, MysqlxCrud.Find.Builder, MysqlxCrud.FindOrBuilder> stmtBuilder_;
      
      private boolean replaceExisting_;
      
      public static final Descriptors.Descriptor getDescriptor() {
        return MysqlxCrud.internal_static_Mysqlx_Crud_CreateView_descriptor;
      }
      
      protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() {
        return MysqlxCrud.internal_static_Mysqlx_Crud_CreateView_fieldAccessorTable
          .ensureFieldAccessorsInitialized(MysqlxCrud.CreateView.class, Builder.class);
      }
      
      private Builder() {
        this.definer_ = "";
        this.algorithm_ = 1;
        this.security_ = 2;
        this.check_ = 1;
        this.column_ = LazyStringArrayList.EMPTY;
        maybeForceBuilderInitialization();
      }
      
      private Builder(GeneratedMessageV3.BuilderParent parent) {
        super(parent);
        this.definer_ = "";
        this.algorithm_ = 1;
        this.security_ = 2;
        this.check_ = 1;
        this.column_ = LazyStringArrayList.EMPTY;
        maybeForceBuilderInitialization();
      }
      
      private void maybeForceBuilderInitialization() {
        if (MysqlxCrud.CreateView.alwaysUseFieldBuilders) {
          getCollectionFieldBuilder();
          getStmtFieldBuilder();
        } 
      }
      
      public Builder clear() {
        super.clear();
        if (this.collectionBuilder_ == null) {
          this.collection_ = null;
        } else {
          this.collectionBuilder_.clear();
        } 
        this.bitField0_ &= 0xFFFFFFFE;
        this.definer_ = "";
        this.bitField0_ &= 0xFFFFFFFD;
        this.algorithm_ = 1;
        this.bitField0_ &= 0xFFFFFFFB;
        this.security_ = 2;
        this.bitField0_ &= 0xFFFFFFF7;
        this.check_ = 1;
        this.bitField0_ &= 0xFFFFFFEF;
        this.column_ = LazyStringArrayList.EMPTY;
        this.bitField0_ &= 0xFFFFFFDF;
        if (this.stmtBuilder_ == null) {
          this.stmt_ = null;
        } else {
          this.stmtBuilder_.clear();
        } 
        this.bitField0_ &= 0xFFFFFFBF;
        this.replaceExisting_ = false;
        this.bitField0_ &= 0xFFFFFF7F;
        return this;
      }
      
      public Descriptors.Descriptor getDescriptorForType() {
        return MysqlxCrud.internal_static_Mysqlx_Crud_CreateView_descriptor;
      }
      
      public MysqlxCrud.CreateView getDefaultInstanceForType() {
        return MysqlxCrud.CreateView.getDefaultInstance();
      }
      
      public MysqlxCrud.CreateView build() {
        MysqlxCrud.CreateView result = buildPartial();
        if (!result.isInitialized())
          throw newUninitializedMessageException(result); 
        return result;
      }
      
      public MysqlxCrud.CreateView buildPartial() {
        MysqlxCrud.CreateView result = new MysqlxCrud.CreateView(this);
        int from_bitField0_ = this.bitField0_;
        int to_bitField0_ = 0;
        if ((from_bitField0_ & 0x1) != 0) {
          if (this.collectionBuilder_ == null) {
            result.collection_ = this.collection_;
          } else {
            result.collection_ = (MysqlxCrud.Collection)this.collectionBuilder_.build();
          } 
          to_bitField0_ |= 0x1;
        } 
        if ((from_bitField0_ & 0x2) != 0)
          to_bitField0_ |= 0x2; 
        result.definer_ = this.definer_;
        if ((from_bitField0_ & 0x4) != 0)
          to_bitField0_ |= 0x4; 
        result.algorithm_ = this.algorithm_;
        if ((from_bitField0_ & 0x8) != 0)
          to_bitField0_ |= 0x8; 
        result.security_ = this.security_;
        if ((from_bitField0_ & 0x10) != 0)
          to_bitField0_ |= 0x10; 
        result.check_ = this.check_;
        if ((this.bitField0_ & 0x20) != 0) {
          this.column_ = this.column_.getUnmodifiableView();
          this.bitField0_ &= 0xFFFFFFDF;
        } 
        result.column_ = this.column_;
        if ((from_bitField0_ & 0x40) != 0) {
          if (this.stmtBuilder_ == null) {
            result.stmt_ = this.stmt_;
          } else {
            result.stmt_ = (MysqlxCrud.Find)this.stmtBuilder_.build();
          } 
          to_bitField0_ |= 0x20;
        } 
        if ((from_bitField0_ & 0x80) != 0) {
          result.replaceExisting_ = this.replaceExisting_;
          to_bitField0_ |= 0x40;
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
        if (other instanceof MysqlxCrud.CreateView)
          return mergeFrom((MysqlxCrud.CreateView)other); 
        super.mergeFrom(other);
        return this;
      }
      
      public Builder mergeFrom(MysqlxCrud.CreateView other) {
        if (other == MysqlxCrud.CreateView.getDefaultInstance())
          return this; 
        if (other.hasCollection())
          mergeCollection(other.getCollection()); 
        if (other.hasDefiner()) {
          this.bitField0_ |= 0x2;
          this.definer_ = other.definer_;
          onChanged();
        } 
        if (other.hasAlgorithm())
          setAlgorithm(other.getAlgorithm()); 
        if (other.hasSecurity())
          setSecurity(other.getSecurity()); 
        if (other.hasCheck())
          setCheck(other.getCheck()); 
        if (!other.column_.isEmpty()) {
          if (this.column_.isEmpty()) {
            this.column_ = other.column_;
            this.bitField0_ &= 0xFFFFFFDF;
          } else {
            ensureColumnIsMutable();
            this.column_.addAll((java.util.Collection)other.column_);
          } 
          onChanged();
        } 
        if (other.hasStmt())
          mergeStmt(other.getStmt()); 
        if (other.hasReplaceExisting())
          setReplaceExisting(other.getReplaceExisting()); 
        mergeUnknownFields(other.getUnknownFields());
        onChanged();
        return this;
      }
      
      public final boolean isInitialized() {
        if (!hasCollection())
          return false; 
        if (!hasStmt())
          return false; 
        if (!getCollection().isInitialized())
          return false; 
        if (!getStmt().isInitialized())
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
            ByteString bs;
            MysqlxCrud.ViewAlgorithm viewAlgorithm;
            MysqlxCrud.ViewSqlSecurity viewSqlSecurity;
            MysqlxCrud.ViewCheckOption tmpValue;
            int tag = input.readTag();
            switch (tag) {
              case 0:
                done = true;
                continue;
              case 10:
                input.readMessage((MessageLite.Builder)getCollectionFieldBuilder().getBuilder(), extensionRegistry);
                this.bitField0_ |= 0x1;
                continue;
              case 18:
                this.definer_ = input.readBytes();
                this.bitField0_ |= 0x2;
                continue;
              case 24:
                tmpRaw = input.readEnum();
                viewAlgorithm = MysqlxCrud.ViewAlgorithm.forNumber(tmpRaw);
                if (viewAlgorithm == null) {
                  mergeUnknownVarintField(3, tmpRaw);
                  continue;
                } 
                this.algorithm_ = tmpRaw;
                this.bitField0_ |= 0x4;
                continue;
              case 32:
                tmpRaw = input.readEnum();
                viewSqlSecurity = MysqlxCrud.ViewSqlSecurity.forNumber(tmpRaw);
                if (viewSqlSecurity == null) {
                  mergeUnknownVarintField(4, tmpRaw);
                  continue;
                } 
                this.security_ = tmpRaw;
                this.bitField0_ |= 0x8;
                continue;
              case 40:
                tmpRaw = input.readEnum();
                tmpValue = MysqlxCrud.ViewCheckOption.forNumber(tmpRaw);
                if (tmpValue == null) {
                  mergeUnknownVarintField(5, tmpRaw);
                  continue;
                } 
                this.check_ = tmpRaw;
                this.bitField0_ |= 0x10;
                continue;
              case 50:
                bs = input.readBytes();
                ensureColumnIsMutable();
                this.column_.add(bs);
                continue;
              case 58:
                input.readMessage((MessageLite.Builder)getStmtFieldBuilder().getBuilder(), extensionRegistry);
                this.bitField0_ |= 0x40;
                continue;
              case 64:
                this.replaceExisting_ = input.readBool();
                this.bitField0_ |= 0x80;
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
      
      public boolean hasCollection() {
        return ((this.bitField0_ & 0x1) != 0);
      }
      
      public MysqlxCrud.Collection getCollection() {
        if (this.collectionBuilder_ == null)
          return (this.collection_ == null) ? MysqlxCrud.Collection.getDefaultInstance() : this.collection_; 
        return (MysqlxCrud.Collection)this.collectionBuilder_.getMessage();
      }
      
      public Builder setCollection(MysqlxCrud.Collection value) {
        if (this.collectionBuilder_ == null) {
          if (value == null)
            throw new NullPointerException(); 
          this.collection_ = value;
          onChanged();
        } else {
          this.collectionBuilder_.setMessage((AbstractMessage)value);
        } 
        this.bitField0_ |= 0x1;
        return this;
      }
      
      public Builder setCollection(MysqlxCrud.Collection.Builder builderForValue) {
        if (this.collectionBuilder_ == null) {
          this.collection_ = builderForValue.build();
          onChanged();
        } else {
          this.collectionBuilder_.setMessage((AbstractMessage)builderForValue.build());
        } 
        this.bitField0_ |= 0x1;
        return this;
      }
      
      public Builder mergeCollection(MysqlxCrud.Collection value) {
        if (this.collectionBuilder_ == null) {
          if ((this.bitField0_ & 0x1) != 0 && this.collection_ != null && this.collection_ != MysqlxCrud.Collection.getDefaultInstance()) {
            this.collection_ = MysqlxCrud.Collection.newBuilder(this.collection_).mergeFrom(value).buildPartial();
          } else {
            this.collection_ = value;
          } 
          onChanged();
        } else {
          this.collectionBuilder_.mergeFrom((AbstractMessage)value);
        } 
        this.bitField0_ |= 0x1;
        return this;
      }
      
      public Builder clearCollection() {
        if (this.collectionBuilder_ == null) {
          this.collection_ = null;
          onChanged();
        } else {
          this.collectionBuilder_.clear();
        } 
        this.bitField0_ &= 0xFFFFFFFE;
        return this;
      }
      
      public MysqlxCrud.Collection.Builder getCollectionBuilder() {
        this.bitField0_ |= 0x1;
        onChanged();
        return (MysqlxCrud.Collection.Builder)getCollectionFieldBuilder().getBuilder();
      }
      
      public MysqlxCrud.CollectionOrBuilder getCollectionOrBuilder() {
        if (this.collectionBuilder_ != null)
          return (MysqlxCrud.CollectionOrBuilder)this.collectionBuilder_.getMessageOrBuilder(); 
        return (this.collection_ == null) ? MysqlxCrud.Collection.getDefaultInstance() : this.collection_;
      }
      
      private SingleFieldBuilderV3<MysqlxCrud.Collection, MysqlxCrud.Collection.Builder, MysqlxCrud.CollectionOrBuilder> getCollectionFieldBuilder() {
        if (this.collectionBuilder_ == null) {
          this.collectionBuilder_ = new SingleFieldBuilderV3((AbstractMessage)getCollection(), (AbstractMessage.BuilderParent)getParentForChildren(), isClean());
          this.collection_ = null;
        } 
        return this.collectionBuilder_;
      }
      
      public boolean hasDefiner() {
        return ((this.bitField0_ & 0x2) != 0);
      }
      
      public String getDefiner() {
        Object ref = this.definer_;
        if (!(ref instanceof String)) {
          ByteString bs = (ByteString)ref;
          String s = bs.toStringUtf8();
          if (bs.isValidUtf8())
            this.definer_ = s; 
          return s;
        } 
        return (String)ref;
      }
      
      public ByteString getDefinerBytes() {
        Object ref = this.definer_;
        if (ref instanceof String) {
          ByteString b = ByteString.copyFromUtf8((String)ref);
          this.definer_ = b;
          return b;
        } 
        return (ByteString)ref;
      }
      
      public Builder setDefiner(String value) {
        if (value == null)
          throw new NullPointerException(); 
        this.bitField0_ |= 0x2;
        this.definer_ = value;
        onChanged();
        return this;
      }
      
      public Builder clearDefiner() {
        this.bitField0_ &= 0xFFFFFFFD;
        this.definer_ = MysqlxCrud.CreateView.getDefaultInstance().getDefiner();
        onChanged();
        return this;
      }
      
      public Builder setDefinerBytes(ByteString value) {
        if (value == null)
          throw new NullPointerException(); 
        this.bitField0_ |= 0x2;
        this.definer_ = value;
        onChanged();
        return this;
      }
      
      public boolean hasAlgorithm() {
        return ((this.bitField0_ & 0x4) != 0);
      }
      
      public MysqlxCrud.ViewAlgorithm getAlgorithm() {
        MysqlxCrud.ViewAlgorithm result = MysqlxCrud.ViewAlgorithm.valueOf(this.algorithm_);
        return (result == null) ? MysqlxCrud.ViewAlgorithm.UNDEFINED : result;
      }
      
      public Builder setAlgorithm(MysqlxCrud.ViewAlgorithm value) {
        if (value == null)
          throw new NullPointerException(); 
        this.bitField0_ |= 0x4;
        this.algorithm_ = value.getNumber();
        onChanged();
        return this;
      }
      
      public Builder clearAlgorithm() {
        this.bitField0_ &= 0xFFFFFFFB;
        this.algorithm_ = 1;
        onChanged();
        return this;
      }
      
      public boolean hasSecurity() {
        return ((this.bitField0_ & 0x8) != 0);
      }
      
      public MysqlxCrud.ViewSqlSecurity getSecurity() {
        MysqlxCrud.ViewSqlSecurity result = MysqlxCrud.ViewSqlSecurity.valueOf(this.security_);
        return (result == null) ? MysqlxCrud.ViewSqlSecurity.DEFINER : result;
      }
      
      public Builder setSecurity(MysqlxCrud.ViewSqlSecurity value) {
        if (value == null)
          throw new NullPointerException(); 
        this.bitField0_ |= 0x8;
        this.security_ = value.getNumber();
        onChanged();
        return this;
      }
      
      public Builder clearSecurity() {
        this.bitField0_ &= 0xFFFFFFF7;
        this.security_ = 2;
        onChanged();
        return this;
      }
      
      public boolean hasCheck() {
        return ((this.bitField0_ & 0x10) != 0);
      }
      
      public MysqlxCrud.ViewCheckOption getCheck() {
        MysqlxCrud.ViewCheckOption result = MysqlxCrud.ViewCheckOption.valueOf(this.check_);
        return (result == null) ? MysqlxCrud.ViewCheckOption.LOCAL : result;
      }
      
      public Builder setCheck(MysqlxCrud.ViewCheckOption value) {
        if (value == null)
          throw new NullPointerException(); 
        this.bitField0_ |= 0x10;
        this.check_ = value.getNumber();
        onChanged();
        return this;
      }
      
      public Builder clearCheck() {
        this.bitField0_ &= 0xFFFFFFEF;
        this.check_ = 1;
        onChanged();
        return this;
      }
      
      private void ensureColumnIsMutable() {
        if ((this.bitField0_ & 0x20) == 0) {
          this.column_ = (LazyStringList)new LazyStringArrayList(this.column_);
          this.bitField0_ |= 0x20;
        } 
      }
      
      public ProtocolStringList getColumnList() {
        return (ProtocolStringList)this.column_.getUnmodifiableView();
      }
      
      public int getColumnCount() {
        return this.column_.size();
      }
      
      public String getColumn(int index) {
        return (String)this.column_.get(index);
      }
      
      public ByteString getColumnBytes(int index) {
        return this.column_.getByteString(index);
      }
      
      public Builder setColumn(int index, String value) {
        if (value == null)
          throw new NullPointerException(); 
        ensureColumnIsMutable();
        this.column_.set(index, value);
        onChanged();
        return this;
      }
      
      public Builder addColumn(String value) {
        if (value == null)
          throw new NullPointerException(); 
        ensureColumnIsMutable();
        this.column_.add(value);
        onChanged();
        return this;
      }
      
      public Builder addAllColumn(Iterable<String> values) {
        ensureColumnIsMutable();
        AbstractMessageLite.Builder.addAll(values, (List)this.column_);
        onChanged();
        return this;
      }
      
      public Builder clearColumn() {
        this.column_ = LazyStringArrayList.EMPTY;
        this.bitField0_ &= 0xFFFFFFDF;
        onChanged();
        return this;
      }
      
      public Builder addColumnBytes(ByteString value) {
        if (value == null)
          throw new NullPointerException(); 
        ensureColumnIsMutable();
        this.column_.add(value);
        onChanged();
        return this;
      }
      
      public boolean hasStmt() {
        return ((this.bitField0_ & 0x40) != 0);
      }
      
      public MysqlxCrud.Find getStmt() {
        if (this.stmtBuilder_ == null)
          return (this.stmt_ == null) ? MysqlxCrud.Find.getDefaultInstance() : this.stmt_; 
        return (MysqlxCrud.Find)this.stmtBuilder_.getMessage();
      }
      
      public Builder setStmt(MysqlxCrud.Find value) {
        if (this.stmtBuilder_ == null) {
          if (value == null)
            throw new NullPointerException(); 
          this.stmt_ = value;
          onChanged();
        } else {
          this.stmtBuilder_.setMessage((AbstractMessage)value);
        } 
        this.bitField0_ |= 0x40;
        return this;
      }
      
      public Builder setStmt(MysqlxCrud.Find.Builder builderForValue) {
        if (this.stmtBuilder_ == null) {
          this.stmt_ = builderForValue.build();
          onChanged();
        } else {
          this.stmtBuilder_.setMessage((AbstractMessage)builderForValue.build());
        } 
        this.bitField0_ |= 0x40;
        return this;
      }
      
      public Builder mergeStmt(MysqlxCrud.Find value) {
        if (this.stmtBuilder_ == null) {
          if ((this.bitField0_ & 0x40) != 0 && this.stmt_ != null && this.stmt_ != 
            
            MysqlxCrud.Find.getDefaultInstance()) {
            this
              .stmt_ = MysqlxCrud.Find.newBuilder(this.stmt_).mergeFrom(value).buildPartial();
          } else {
            this.stmt_ = value;
          } 
          onChanged();
        } else {
          this.stmtBuilder_.mergeFrom((AbstractMessage)value);
        } 
        this.bitField0_ |= 0x40;
        return this;
      }
      
      public Builder clearStmt() {
        if (this.stmtBuilder_ == null) {
          this.stmt_ = null;
          onChanged();
        } else {
          this.stmtBuilder_.clear();
        } 
        this.bitField0_ &= 0xFFFFFFBF;
        return this;
      }
      
      public MysqlxCrud.Find.Builder getStmtBuilder() {
        this.bitField0_ |= 0x40;
        onChanged();
        return (MysqlxCrud.Find.Builder)getStmtFieldBuilder().getBuilder();
      }
      
      public MysqlxCrud.FindOrBuilder getStmtOrBuilder() {
        if (this.stmtBuilder_ != null)
          return (MysqlxCrud.FindOrBuilder)this.stmtBuilder_.getMessageOrBuilder(); 
        return (this.stmt_ == null) ? 
          MysqlxCrud.Find.getDefaultInstance() : this.stmt_;
      }
      
      private SingleFieldBuilderV3<MysqlxCrud.Find, MysqlxCrud.Find.Builder, MysqlxCrud.FindOrBuilder> getStmtFieldBuilder() {
        if (this.stmtBuilder_ == null) {
          this
            
            .stmtBuilder_ = new SingleFieldBuilderV3((AbstractMessage)getStmt(), (AbstractMessage.BuilderParent)getParentForChildren(), isClean());
          this.stmt_ = null;
        } 
        return this.stmtBuilder_;
      }
      
      public boolean hasReplaceExisting() {
        return ((this.bitField0_ & 0x80) != 0);
      }
      
      public boolean getReplaceExisting() {
        return this.replaceExisting_;
      }
      
      public Builder setReplaceExisting(boolean value) {
        this.bitField0_ |= 0x80;
        this.replaceExisting_ = value;
        onChanged();
        return this;
      }
      
      public Builder clearReplaceExisting() {
        this.bitField0_ &= 0xFFFFFF7F;
        this.replaceExisting_ = false;
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
    
    private static final CreateView DEFAULT_INSTANCE = new CreateView();
    
    public static CreateView getDefaultInstance() {
      return DEFAULT_INSTANCE;
    }
    
    @Deprecated
    public static final Parser<CreateView> PARSER = (Parser<CreateView>)new AbstractParser<CreateView>() {
        public MysqlxCrud.CreateView parsePartialFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
          MysqlxCrud.CreateView.Builder builder = MysqlxCrud.CreateView.newBuilder();
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
    
    public static Parser<CreateView> parser() {
      return PARSER;
    }
    
    public Parser<CreateView> getParserForType() {
      return PARSER;
    }
    
    public CreateView getDefaultInstanceForType() {
      return DEFAULT_INSTANCE;
    }
  }
  
  public static interface ModifyViewOrBuilder extends MessageOrBuilder {
    boolean hasCollection();
    
    MysqlxCrud.Collection getCollection();
    
    MysqlxCrud.CollectionOrBuilder getCollectionOrBuilder();
    
    boolean hasDefiner();
    
    String getDefiner();
    
    ByteString getDefinerBytes();
    
    boolean hasAlgorithm();
    
    MysqlxCrud.ViewAlgorithm getAlgorithm();
    
    boolean hasSecurity();
    
    MysqlxCrud.ViewSqlSecurity getSecurity();
    
    boolean hasCheck();
    
    MysqlxCrud.ViewCheckOption getCheck();
    
    List<String> getColumnList();
    
    int getColumnCount();
    
    String getColumn(int param1Int);
    
    ByteString getColumnBytes(int param1Int);
    
    boolean hasStmt();
    
    MysqlxCrud.Find getStmt();
    
    MysqlxCrud.FindOrBuilder getStmtOrBuilder();
  }
  
  public static final class ModifyView extends GeneratedMessageV3 implements ModifyViewOrBuilder {
    private static final long serialVersionUID = 0L;
    
    private int bitField0_;
    
    public static final int COLLECTION_FIELD_NUMBER = 1;
    
    private MysqlxCrud.Collection collection_;
    
    public static final int DEFINER_FIELD_NUMBER = 2;
    
    private volatile Object definer_;
    
    public static final int ALGORITHM_FIELD_NUMBER = 3;
    
    private int algorithm_;
    
    public static final int SECURITY_FIELD_NUMBER = 4;
    
    private int security_;
    
    public static final int CHECK_FIELD_NUMBER = 5;
    
    private int check_;
    
    public static final int COLUMN_FIELD_NUMBER = 6;
    
    private LazyStringList column_;
    
    public static final int STMT_FIELD_NUMBER = 7;
    
    private MysqlxCrud.Find stmt_;
    
    private byte memoizedIsInitialized;
    
    private ModifyView(GeneratedMessageV3.Builder<?> builder) {
      super(builder);
      this.memoizedIsInitialized = -1;
    }
    
    private ModifyView() {
      this.memoizedIsInitialized = -1;
      this.definer_ = "";
      this.algorithm_ = 1;
      this.security_ = 1;
      this.check_ = 1;
      this.column_ = LazyStringArrayList.EMPTY;
    }
    
    protected Object newInstance(GeneratedMessageV3.UnusedPrivateParameter unused) {
      return new ModifyView();
    }
    
    public final UnknownFieldSet getUnknownFields() {
      return this.unknownFields;
    }
    
    public static final Descriptors.Descriptor getDescriptor() {
      return MysqlxCrud.internal_static_Mysqlx_Crud_ModifyView_descriptor;
    }
    
    protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() {
      return MysqlxCrud.internal_static_Mysqlx_Crud_ModifyView_fieldAccessorTable.ensureFieldAccessorsInitialized(ModifyView.class, Builder.class);
    }
    
    public boolean hasCollection() {
      return ((this.bitField0_ & 0x1) != 0);
    }
    
    public MysqlxCrud.Collection getCollection() {
      return (this.collection_ == null) ? MysqlxCrud.Collection.getDefaultInstance() : this.collection_;
    }
    
    public MysqlxCrud.CollectionOrBuilder getCollectionOrBuilder() {
      return (this.collection_ == null) ? MysqlxCrud.Collection.getDefaultInstance() : this.collection_;
    }
    
    public boolean hasDefiner() {
      return ((this.bitField0_ & 0x2) != 0);
    }
    
    public String getDefiner() {
      Object ref = this.definer_;
      if (ref instanceof String)
        return (String)ref; 
      ByteString bs = (ByteString)ref;
      String s = bs.toStringUtf8();
      if (bs.isValidUtf8())
        this.definer_ = s; 
      return s;
    }
    
    public ByteString getDefinerBytes() {
      Object ref = this.definer_;
      if (ref instanceof String) {
        ByteString b = ByteString.copyFromUtf8((String)ref);
        this.definer_ = b;
        return b;
      } 
      return (ByteString)ref;
    }
    
    public boolean hasAlgorithm() {
      return ((this.bitField0_ & 0x4) != 0);
    }
    
    public MysqlxCrud.ViewAlgorithm getAlgorithm() {
      MysqlxCrud.ViewAlgorithm result = MysqlxCrud.ViewAlgorithm.valueOf(this.algorithm_);
      return (result == null) ? MysqlxCrud.ViewAlgorithm.UNDEFINED : result;
    }
    
    public boolean hasSecurity() {
      return ((this.bitField0_ & 0x8) != 0);
    }
    
    public MysqlxCrud.ViewSqlSecurity getSecurity() {
      MysqlxCrud.ViewSqlSecurity result = MysqlxCrud.ViewSqlSecurity.valueOf(this.security_);
      return (result == null) ? MysqlxCrud.ViewSqlSecurity.INVOKER : result;
    }
    
    public boolean hasCheck() {
      return ((this.bitField0_ & 0x10) != 0);
    }
    
    public MysqlxCrud.ViewCheckOption getCheck() {
      MysqlxCrud.ViewCheckOption result = MysqlxCrud.ViewCheckOption.valueOf(this.check_);
      return (result == null) ? MysqlxCrud.ViewCheckOption.LOCAL : result;
    }
    
    public ProtocolStringList getColumnList() {
      return (ProtocolStringList)this.column_;
    }
    
    public int getColumnCount() {
      return this.column_.size();
    }
    
    public String getColumn(int index) {
      return (String)this.column_.get(index);
    }
    
    public ByteString getColumnBytes(int index) {
      return this.column_.getByteString(index);
    }
    
    public boolean hasStmt() {
      return ((this.bitField0_ & 0x20) != 0);
    }
    
    public MysqlxCrud.Find getStmt() {
      return (this.stmt_ == null) ? MysqlxCrud.Find.getDefaultInstance() : this.stmt_;
    }
    
    public MysqlxCrud.FindOrBuilder getStmtOrBuilder() {
      return (this.stmt_ == null) ? MysqlxCrud.Find.getDefaultInstance() : this.stmt_;
    }
    
    public final boolean isInitialized() {
      byte isInitialized = this.memoizedIsInitialized;
      if (isInitialized == 1)
        return true; 
      if (isInitialized == 0)
        return false; 
      if (!hasCollection()) {
        this.memoizedIsInitialized = 0;
        return false;
      } 
      if (!getCollection().isInitialized()) {
        this.memoizedIsInitialized = 0;
        return false;
      } 
      if (hasStmt() && 
        !getStmt().isInitialized()) {
        this.memoizedIsInitialized = 0;
        return false;
      } 
      this.memoizedIsInitialized = 1;
      return true;
    }
    
    public void writeTo(CodedOutputStream output) throws IOException {
      if ((this.bitField0_ & 0x1) != 0)
        output.writeMessage(1, (MessageLite)getCollection()); 
      if ((this.bitField0_ & 0x2) != 0)
        GeneratedMessageV3.writeString(output, 2, this.definer_); 
      if ((this.bitField0_ & 0x4) != 0)
        output.writeEnum(3, this.algorithm_); 
      if ((this.bitField0_ & 0x8) != 0)
        output.writeEnum(4, this.security_); 
      if ((this.bitField0_ & 0x10) != 0)
        output.writeEnum(5, this.check_); 
      for (int i = 0; i < this.column_.size(); i++)
        GeneratedMessageV3.writeString(output, 6, this.column_.getRaw(i)); 
      if ((this.bitField0_ & 0x20) != 0)
        output.writeMessage(7, (MessageLite)getStmt()); 
      getUnknownFields().writeTo(output);
    }
    
    public int getSerializedSize() {
      int size = this.memoizedSize;
      if (size != -1)
        return size; 
      size = 0;
      if ((this.bitField0_ & 0x1) != 0)
        size += 
          CodedOutputStream.computeMessageSize(1, (MessageLite)getCollection()); 
      if ((this.bitField0_ & 0x2) != 0)
        size += GeneratedMessageV3.computeStringSize(2, this.definer_); 
      if ((this.bitField0_ & 0x4) != 0)
        size += 
          CodedOutputStream.computeEnumSize(3, this.algorithm_); 
      if ((this.bitField0_ & 0x8) != 0)
        size += 
          CodedOutputStream.computeEnumSize(4, this.security_); 
      if ((this.bitField0_ & 0x10) != 0)
        size += 
          CodedOutputStream.computeEnumSize(5, this.check_); 
      int dataSize = 0;
      for (int i = 0; i < this.column_.size(); i++)
        dataSize += computeStringSizeNoTag(this.column_.getRaw(i)); 
      size += dataSize;
      size += 1 * getColumnList().size();
      if ((this.bitField0_ & 0x20) != 0)
        size += 
          CodedOutputStream.computeMessageSize(7, (MessageLite)getStmt()); 
      size += getUnknownFields().getSerializedSize();
      this.memoizedSize = size;
      return size;
    }
    
    public boolean equals(Object obj) {
      if (obj == this)
        return true; 
      if (!(obj instanceof ModifyView))
        return super.equals(obj); 
      ModifyView other = (ModifyView)obj;
      if (hasCollection() != other.hasCollection())
        return false; 
      if (hasCollection() && 
        
        !getCollection().equals(other.getCollection()))
        return false; 
      if (hasDefiner() != other.hasDefiner())
        return false; 
      if (hasDefiner() && 
        
        !getDefiner().equals(other.getDefiner()))
        return false; 
      if (hasAlgorithm() != other.hasAlgorithm())
        return false; 
      if (hasAlgorithm() && 
        this.algorithm_ != other.algorithm_)
        return false; 
      if (hasSecurity() != other.hasSecurity())
        return false; 
      if (hasSecurity() && 
        this.security_ != other.security_)
        return false; 
      if (hasCheck() != other.hasCheck())
        return false; 
      if (hasCheck() && 
        this.check_ != other.check_)
        return false; 
      if (!getColumnList().equals(other.getColumnList()))
        return false; 
      if (hasStmt() != other.hasStmt())
        return false; 
      if (hasStmt() && 
        
        !getStmt().equals(other.getStmt()))
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
      if (hasCollection()) {
        hash = 37 * hash + 1;
        hash = 53 * hash + getCollection().hashCode();
      } 
      if (hasDefiner()) {
        hash = 37 * hash + 2;
        hash = 53 * hash + getDefiner().hashCode();
      } 
      if (hasAlgorithm()) {
        hash = 37 * hash + 3;
        hash = 53 * hash + this.algorithm_;
      } 
      if (hasSecurity()) {
        hash = 37 * hash + 4;
        hash = 53 * hash + this.security_;
      } 
      if (hasCheck()) {
        hash = 37 * hash + 5;
        hash = 53 * hash + this.check_;
      } 
      if (getColumnCount() > 0) {
        hash = 37 * hash + 6;
        hash = 53 * hash + getColumnList().hashCode();
      } 
      if (hasStmt()) {
        hash = 37 * hash + 7;
        hash = 53 * hash + getStmt().hashCode();
      } 
      hash = 29 * hash + getUnknownFields().hashCode();
      this.memoizedHashCode = hash;
      return hash;
    }
    
    public static ModifyView parseFrom(ByteBuffer data) throws InvalidProtocolBufferException {
      return (ModifyView)PARSER.parseFrom(data);
    }
    
    public static ModifyView parseFrom(ByteBuffer data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
      return (ModifyView)PARSER.parseFrom(data, extensionRegistry);
    }
    
    public static ModifyView parseFrom(ByteString data) throws InvalidProtocolBufferException {
      return (ModifyView)PARSER.parseFrom(data);
    }
    
    public static ModifyView parseFrom(ByteString data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
      return (ModifyView)PARSER.parseFrom(data, extensionRegistry);
    }
    
    public static ModifyView parseFrom(byte[] data) throws InvalidProtocolBufferException {
      return (ModifyView)PARSER.parseFrom(data);
    }
    
    public static ModifyView parseFrom(byte[] data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
      return (ModifyView)PARSER.parseFrom(data, extensionRegistry);
    }
    
    public static ModifyView parseFrom(InputStream input) throws IOException {
      return 
        (ModifyView)GeneratedMessageV3.parseWithIOException(PARSER, input);
    }
    
    public static ModifyView parseFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
      return 
        (ModifyView)GeneratedMessageV3.parseWithIOException(PARSER, input, extensionRegistry);
    }
    
    public static ModifyView parseDelimitedFrom(InputStream input) throws IOException {
      return 
        (ModifyView)GeneratedMessageV3.parseDelimitedWithIOException(PARSER, input);
    }
    
    public static ModifyView parseDelimitedFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
      return 
        (ModifyView)GeneratedMessageV3.parseDelimitedWithIOException(PARSER, input, extensionRegistry);
    }
    
    public static ModifyView parseFrom(CodedInputStream input) throws IOException {
      return 
        (ModifyView)GeneratedMessageV3.parseWithIOException(PARSER, input);
    }
    
    public static ModifyView parseFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
      return 
        (ModifyView)GeneratedMessageV3.parseWithIOException(PARSER, input, extensionRegistry);
    }
    
    public Builder newBuilderForType() {
      return newBuilder();
    }
    
    public static Builder newBuilder() {
      return DEFAULT_INSTANCE.toBuilder();
    }
    
    public static Builder newBuilder(ModifyView prototype) {
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
    
    public static final class Builder extends GeneratedMessageV3.Builder<Builder> implements MysqlxCrud.ModifyViewOrBuilder {
      private int bitField0_;
      
      private MysqlxCrud.Collection collection_;
      
      private SingleFieldBuilderV3<MysqlxCrud.Collection, MysqlxCrud.Collection.Builder, MysqlxCrud.CollectionOrBuilder> collectionBuilder_;
      
      private Object definer_;
      
      private int algorithm_;
      
      private int security_;
      
      private int check_;
      
      private LazyStringList column_;
      
      private MysqlxCrud.Find stmt_;
      
      private SingleFieldBuilderV3<MysqlxCrud.Find, MysqlxCrud.Find.Builder, MysqlxCrud.FindOrBuilder> stmtBuilder_;
      
      public static final Descriptors.Descriptor getDescriptor() {
        return MysqlxCrud.internal_static_Mysqlx_Crud_ModifyView_descriptor;
      }
      
      protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() {
        return MysqlxCrud.internal_static_Mysqlx_Crud_ModifyView_fieldAccessorTable
          .ensureFieldAccessorsInitialized(MysqlxCrud.ModifyView.class, Builder.class);
      }
      
      private Builder() {
        this.definer_ = "";
        this.algorithm_ = 1;
        this.security_ = 1;
        this.check_ = 1;
        this.column_ = LazyStringArrayList.EMPTY;
        maybeForceBuilderInitialization();
      }
      
      private Builder(GeneratedMessageV3.BuilderParent parent) {
        super(parent);
        this.definer_ = "";
        this.algorithm_ = 1;
        this.security_ = 1;
        this.check_ = 1;
        this.column_ = LazyStringArrayList.EMPTY;
        maybeForceBuilderInitialization();
      }
      
      private void maybeForceBuilderInitialization() {
        if (MysqlxCrud.ModifyView.alwaysUseFieldBuilders) {
          getCollectionFieldBuilder();
          getStmtFieldBuilder();
        } 
      }
      
      public Builder clear() {
        super.clear();
        if (this.collectionBuilder_ == null) {
          this.collection_ = null;
        } else {
          this.collectionBuilder_.clear();
        } 
        this.bitField0_ &= 0xFFFFFFFE;
        this.definer_ = "";
        this.bitField0_ &= 0xFFFFFFFD;
        this.algorithm_ = 1;
        this.bitField0_ &= 0xFFFFFFFB;
        this.security_ = 1;
        this.bitField0_ &= 0xFFFFFFF7;
        this.check_ = 1;
        this.bitField0_ &= 0xFFFFFFEF;
        this.column_ = LazyStringArrayList.EMPTY;
        this.bitField0_ &= 0xFFFFFFDF;
        if (this.stmtBuilder_ == null) {
          this.stmt_ = null;
        } else {
          this.stmtBuilder_.clear();
        } 
        this.bitField0_ &= 0xFFFFFFBF;
        return this;
      }
      
      public Descriptors.Descriptor getDescriptorForType() {
        return MysqlxCrud.internal_static_Mysqlx_Crud_ModifyView_descriptor;
      }
      
      public MysqlxCrud.ModifyView getDefaultInstanceForType() {
        return MysqlxCrud.ModifyView.getDefaultInstance();
      }
      
      public MysqlxCrud.ModifyView build() {
        MysqlxCrud.ModifyView result = buildPartial();
        if (!result.isInitialized())
          throw newUninitializedMessageException(result); 
        return result;
      }
      
      public MysqlxCrud.ModifyView buildPartial() {
        MysqlxCrud.ModifyView result = new MysqlxCrud.ModifyView(this);
        int from_bitField0_ = this.bitField0_;
        int to_bitField0_ = 0;
        if ((from_bitField0_ & 0x1) != 0) {
          if (this.collectionBuilder_ == null) {
            result.collection_ = this.collection_;
          } else {
            result.collection_ = (MysqlxCrud.Collection)this.collectionBuilder_.build();
          } 
          to_bitField0_ |= 0x1;
        } 
        if ((from_bitField0_ & 0x2) != 0)
          to_bitField0_ |= 0x2; 
        result.definer_ = this.definer_;
        if ((from_bitField0_ & 0x4) != 0)
          to_bitField0_ |= 0x4; 
        result.algorithm_ = this.algorithm_;
        if ((from_bitField0_ & 0x8) != 0)
          to_bitField0_ |= 0x8; 
        result.security_ = this.security_;
        if ((from_bitField0_ & 0x10) != 0)
          to_bitField0_ |= 0x10; 
        result.check_ = this.check_;
        if ((this.bitField0_ & 0x20) != 0) {
          this.column_ = this.column_.getUnmodifiableView();
          this.bitField0_ &= 0xFFFFFFDF;
        } 
        result.column_ = this.column_;
        if ((from_bitField0_ & 0x40) != 0) {
          if (this.stmtBuilder_ == null) {
            result.stmt_ = this.stmt_;
          } else {
            result.stmt_ = (MysqlxCrud.Find)this.stmtBuilder_.build();
          } 
          to_bitField0_ |= 0x20;
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
        if (other instanceof MysqlxCrud.ModifyView)
          return mergeFrom((MysqlxCrud.ModifyView)other); 
        super.mergeFrom(other);
        return this;
      }
      
      public Builder mergeFrom(MysqlxCrud.ModifyView other) {
        if (other == MysqlxCrud.ModifyView.getDefaultInstance())
          return this; 
        if (other.hasCollection())
          mergeCollection(other.getCollection()); 
        if (other.hasDefiner()) {
          this.bitField0_ |= 0x2;
          this.definer_ = other.definer_;
          onChanged();
        } 
        if (other.hasAlgorithm())
          setAlgorithm(other.getAlgorithm()); 
        if (other.hasSecurity())
          setSecurity(other.getSecurity()); 
        if (other.hasCheck())
          setCheck(other.getCheck()); 
        if (!other.column_.isEmpty()) {
          if (this.column_.isEmpty()) {
            this.column_ = other.column_;
            this.bitField0_ &= 0xFFFFFFDF;
          } else {
            ensureColumnIsMutable();
            this.column_.addAll((java.util.Collection)other.column_);
          } 
          onChanged();
        } 
        if (other.hasStmt())
          mergeStmt(other.getStmt()); 
        mergeUnknownFields(other.getUnknownFields());
        onChanged();
        return this;
      }
      
      public final boolean isInitialized() {
        if (!hasCollection())
          return false; 
        if (!getCollection().isInitialized())
          return false; 
        if (hasStmt() && !getStmt().isInitialized())
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
            ByteString bs;
            MysqlxCrud.ViewAlgorithm viewAlgorithm;
            MysqlxCrud.ViewSqlSecurity viewSqlSecurity;
            MysqlxCrud.ViewCheckOption tmpValue;
            int tag = input.readTag();
            switch (tag) {
              case 0:
                done = true;
                continue;
              case 10:
                input.readMessage((MessageLite.Builder)getCollectionFieldBuilder().getBuilder(), extensionRegistry);
                this.bitField0_ |= 0x1;
                continue;
              case 18:
                this.definer_ = input.readBytes();
                this.bitField0_ |= 0x2;
                continue;
              case 24:
                tmpRaw = input.readEnum();
                viewAlgorithm = MysqlxCrud.ViewAlgorithm.forNumber(tmpRaw);
                if (viewAlgorithm == null) {
                  mergeUnknownVarintField(3, tmpRaw);
                  continue;
                } 
                this.algorithm_ = tmpRaw;
                this.bitField0_ |= 0x4;
                continue;
              case 32:
                tmpRaw = input.readEnum();
                viewSqlSecurity = MysqlxCrud.ViewSqlSecurity.forNumber(tmpRaw);
                if (viewSqlSecurity == null) {
                  mergeUnknownVarintField(4, tmpRaw);
                  continue;
                } 
                this.security_ = tmpRaw;
                this.bitField0_ |= 0x8;
                continue;
              case 40:
                tmpRaw = input.readEnum();
                tmpValue = MysqlxCrud.ViewCheckOption.forNumber(tmpRaw);
                if (tmpValue == null) {
                  mergeUnknownVarintField(5, tmpRaw);
                  continue;
                } 
                this.check_ = tmpRaw;
                this.bitField0_ |= 0x10;
                continue;
              case 50:
                bs = input.readBytes();
                ensureColumnIsMutable();
                this.column_.add(bs);
                continue;
              case 58:
                input.readMessage((MessageLite.Builder)getStmtFieldBuilder().getBuilder(), extensionRegistry);
                this.bitField0_ |= 0x40;
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
      
      public boolean hasCollection() {
        return ((this.bitField0_ & 0x1) != 0);
      }
      
      public MysqlxCrud.Collection getCollection() {
        if (this.collectionBuilder_ == null)
          return (this.collection_ == null) ? MysqlxCrud.Collection.getDefaultInstance() : this.collection_; 
        return (MysqlxCrud.Collection)this.collectionBuilder_.getMessage();
      }
      
      public Builder setCollection(MysqlxCrud.Collection value) {
        if (this.collectionBuilder_ == null) {
          if (value == null)
            throw new NullPointerException(); 
          this.collection_ = value;
          onChanged();
        } else {
          this.collectionBuilder_.setMessage((AbstractMessage)value);
        } 
        this.bitField0_ |= 0x1;
        return this;
      }
      
      public Builder setCollection(MysqlxCrud.Collection.Builder builderForValue) {
        if (this.collectionBuilder_ == null) {
          this.collection_ = builderForValue.build();
          onChanged();
        } else {
          this.collectionBuilder_.setMessage((AbstractMessage)builderForValue.build());
        } 
        this.bitField0_ |= 0x1;
        return this;
      }
      
      public Builder mergeCollection(MysqlxCrud.Collection value) {
        if (this.collectionBuilder_ == null) {
          if ((this.bitField0_ & 0x1) != 0 && this.collection_ != null && this.collection_ != MysqlxCrud.Collection.getDefaultInstance()) {
            this.collection_ = MysqlxCrud.Collection.newBuilder(this.collection_).mergeFrom(value).buildPartial();
          } else {
            this.collection_ = value;
          } 
          onChanged();
        } else {
          this.collectionBuilder_.mergeFrom((AbstractMessage)value);
        } 
        this.bitField0_ |= 0x1;
        return this;
      }
      
      public Builder clearCollection() {
        if (this.collectionBuilder_ == null) {
          this.collection_ = null;
          onChanged();
        } else {
          this.collectionBuilder_.clear();
        } 
        this.bitField0_ &= 0xFFFFFFFE;
        return this;
      }
      
      public MysqlxCrud.Collection.Builder getCollectionBuilder() {
        this.bitField0_ |= 0x1;
        onChanged();
        return (MysqlxCrud.Collection.Builder)getCollectionFieldBuilder().getBuilder();
      }
      
      public MysqlxCrud.CollectionOrBuilder getCollectionOrBuilder() {
        if (this.collectionBuilder_ != null)
          return (MysqlxCrud.CollectionOrBuilder)this.collectionBuilder_.getMessageOrBuilder(); 
        return (this.collection_ == null) ? MysqlxCrud.Collection.getDefaultInstance() : this.collection_;
      }
      
      private SingleFieldBuilderV3<MysqlxCrud.Collection, MysqlxCrud.Collection.Builder, MysqlxCrud.CollectionOrBuilder> getCollectionFieldBuilder() {
        if (this.collectionBuilder_ == null) {
          this.collectionBuilder_ = new SingleFieldBuilderV3((AbstractMessage)getCollection(), (AbstractMessage.BuilderParent)getParentForChildren(), isClean());
          this.collection_ = null;
        } 
        return this.collectionBuilder_;
      }
      
      public boolean hasDefiner() {
        return ((this.bitField0_ & 0x2) != 0);
      }
      
      public String getDefiner() {
        Object ref = this.definer_;
        if (!(ref instanceof String)) {
          ByteString bs = (ByteString)ref;
          String s = bs.toStringUtf8();
          if (bs.isValidUtf8())
            this.definer_ = s; 
          return s;
        } 
        return (String)ref;
      }
      
      public ByteString getDefinerBytes() {
        Object ref = this.definer_;
        if (ref instanceof String) {
          ByteString b = ByteString.copyFromUtf8((String)ref);
          this.definer_ = b;
          return b;
        } 
        return (ByteString)ref;
      }
      
      public Builder setDefiner(String value) {
        if (value == null)
          throw new NullPointerException(); 
        this.bitField0_ |= 0x2;
        this.definer_ = value;
        onChanged();
        return this;
      }
      
      public Builder clearDefiner() {
        this.bitField0_ &= 0xFFFFFFFD;
        this.definer_ = MysqlxCrud.ModifyView.getDefaultInstance().getDefiner();
        onChanged();
        return this;
      }
      
      public Builder setDefinerBytes(ByteString value) {
        if (value == null)
          throw new NullPointerException(); 
        this.bitField0_ |= 0x2;
        this.definer_ = value;
        onChanged();
        return this;
      }
      
      public boolean hasAlgorithm() {
        return ((this.bitField0_ & 0x4) != 0);
      }
      
      public MysqlxCrud.ViewAlgorithm getAlgorithm() {
        MysqlxCrud.ViewAlgorithm result = MysqlxCrud.ViewAlgorithm.valueOf(this.algorithm_);
        return (result == null) ? MysqlxCrud.ViewAlgorithm.UNDEFINED : result;
      }
      
      public Builder setAlgorithm(MysqlxCrud.ViewAlgorithm value) {
        if (value == null)
          throw new NullPointerException(); 
        this.bitField0_ |= 0x4;
        this.algorithm_ = value.getNumber();
        onChanged();
        return this;
      }
      
      public Builder clearAlgorithm() {
        this.bitField0_ &= 0xFFFFFFFB;
        this.algorithm_ = 1;
        onChanged();
        return this;
      }
      
      public boolean hasSecurity() {
        return ((this.bitField0_ & 0x8) != 0);
      }
      
      public MysqlxCrud.ViewSqlSecurity getSecurity() {
        MysqlxCrud.ViewSqlSecurity result = MysqlxCrud.ViewSqlSecurity.valueOf(this.security_);
        return (result == null) ? MysqlxCrud.ViewSqlSecurity.INVOKER : result;
      }
      
      public Builder setSecurity(MysqlxCrud.ViewSqlSecurity value) {
        if (value == null)
          throw new NullPointerException(); 
        this.bitField0_ |= 0x8;
        this.security_ = value.getNumber();
        onChanged();
        return this;
      }
      
      public Builder clearSecurity() {
        this.bitField0_ &= 0xFFFFFFF7;
        this.security_ = 1;
        onChanged();
        return this;
      }
      
      public boolean hasCheck() {
        return ((this.bitField0_ & 0x10) != 0);
      }
      
      public MysqlxCrud.ViewCheckOption getCheck() {
        MysqlxCrud.ViewCheckOption result = MysqlxCrud.ViewCheckOption.valueOf(this.check_);
        return (result == null) ? MysqlxCrud.ViewCheckOption.LOCAL : result;
      }
      
      public Builder setCheck(MysqlxCrud.ViewCheckOption value) {
        if (value == null)
          throw new NullPointerException(); 
        this.bitField0_ |= 0x10;
        this.check_ = value.getNumber();
        onChanged();
        return this;
      }
      
      public Builder clearCheck() {
        this.bitField0_ &= 0xFFFFFFEF;
        this.check_ = 1;
        onChanged();
        return this;
      }
      
      private void ensureColumnIsMutable() {
        if ((this.bitField0_ & 0x20) == 0) {
          this.column_ = (LazyStringList)new LazyStringArrayList(this.column_);
          this.bitField0_ |= 0x20;
        } 
      }
      
      public ProtocolStringList getColumnList() {
        return (ProtocolStringList)this.column_.getUnmodifiableView();
      }
      
      public int getColumnCount() {
        return this.column_.size();
      }
      
      public String getColumn(int index) {
        return (String)this.column_.get(index);
      }
      
      public ByteString getColumnBytes(int index) {
        return this.column_.getByteString(index);
      }
      
      public Builder setColumn(int index, String value) {
        if (value == null)
          throw new NullPointerException(); 
        ensureColumnIsMutable();
        this.column_.set(index, value);
        onChanged();
        return this;
      }
      
      public Builder addColumn(String value) {
        if (value == null)
          throw new NullPointerException(); 
        ensureColumnIsMutable();
        this.column_.add(value);
        onChanged();
        return this;
      }
      
      public Builder addAllColumn(Iterable<String> values) {
        ensureColumnIsMutable();
        AbstractMessageLite.Builder.addAll(values, (List)this.column_);
        onChanged();
        return this;
      }
      
      public Builder clearColumn() {
        this.column_ = LazyStringArrayList.EMPTY;
        this.bitField0_ &= 0xFFFFFFDF;
        onChanged();
        return this;
      }
      
      public Builder addColumnBytes(ByteString value) {
        if (value == null)
          throw new NullPointerException(); 
        ensureColumnIsMutable();
        this.column_.add(value);
        onChanged();
        return this;
      }
      
      public boolean hasStmt() {
        return ((this.bitField0_ & 0x40) != 0);
      }
      
      public MysqlxCrud.Find getStmt() {
        if (this.stmtBuilder_ == null)
          return (this.stmt_ == null) ? MysqlxCrud.Find.getDefaultInstance() : this.stmt_; 
        return (MysqlxCrud.Find)this.stmtBuilder_.getMessage();
      }
      
      public Builder setStmt(MysqlxCrud.Find value) {
        if (this.stmtBuilder_ == null) {
          if (value == null)
            throw new NullPointerException(); 
          this.stmt_ = value;
          onChanged();
        } else {
          this.stmtBuilder_.setMessage((AbstractMessage)value);
        } 
        this.bitField0_ |= 0x40;
        return this;
      }
      
      public Builder setStmt(MysqlxCrud.Find.Builder builderForValue) {
        if (this.stmtBuilder_ == null) {
          this.stmt_ = builderForValue.build();
          onChanged();
        } else {
          this.stmtBuilder_.setMessage((AbstractMessage)builderForValue.build());
        } 
        this.bitField0_ |= 0x40;
        return this;
      }
      
      public Builder mergeStmt(MysqlxCrud.Find value) {
        if (this.stmtBuilder_ == null) {
          if ((this.bitField0_ & 0x40) != 0 && this.stmt_ != null && this.stmt_ != 
            
            MysqlxCrud.Find.getDefaultInstance()) {
            this
              .stmt_ = MysqlxCrud.Find.newBuilder(this.stmt_).mergeFrom(value).buildPartial();
          } else {
            this.stmt_ = value;
          } 
          onChanged();
        } else {
          this.stmtBuilder_.mergeFrom((AbstractMessage)value);
        } 
        this.bitField0_ |= 0x40;
        return this;
      }
      
      public Builder clearStmt() {
        if (this.stmtBuilder_ == null) {
          this.stmt_ = null;
          onChanged();
        } else {
          this.stmtBuilder_.clear();
        } 
        this.bitField0_ &= 0xFFFFFFBF;
        return this;
      }
      
      public MysqlxCrud.Find.Builder getStmtBuilder() {
        this.bitField0_ |= 0x40;
        onChanged();
        return (MysqlxCrud.Find.Builder)getStmtFieldBuilder().getBuilder();
      }
      
      public MysqlxCrud.FindOrBuilder getStmtOrBuilder() {
        if (this.stmtBuilder_ != null)
          return (MysqlxCrud.FindOrBuilder)this.stmtBuilder_.getMessageOrBuilder(); 
        return (this.stmt_ == null) ? 
          MysqlxCrud.Find.getDefaultInstance() : this.stmt_;
      }
      
      private SingleFieldBuilderV3<MysqlxCrud.Find, MysqlxCrud.Find.Builder, MysqlxCrud.FindOrBuilder> getStmtFieldBuilder() {
        if (this.stmtBuilder_ == null) {
          this
            
            .stmtBuilder_ = new SingleFieldBuilderV3((AbstractMessage)getStmt(), (AbstractMessage.BuilderParent)getParentForChildren(), isClean());
          this.stmt_ = null;
        } 
        return this.stmtBuilder_;
      }
      
      public final Builder setUnknownFields(UnknownFieldSet unknownFields) {
        return (Builder)super.setUnknownFields(unknownFields);
      }
      
      public final Builder mergeUnknownFields(UnknownFieldSet unknownFields) {
        return (Builder)super.mergeUnknownFields(unknownFields);
      }
    }
    
    private static final ModifyView DEFAULT_INSTANCE = new ModifyView();
    
    public static ModifyView getDefaultInstance() {
      return DEFAULT_INSTANCE;
    }
    
    @Deprecated
    public static final Parser<ModifyView> PARSER = (Parser<ModifyView>)new AbstractParser<ModifyView>() {
        public MysqlxCrud.ModifyView parsePartialFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
          MysqlxCrud.ModifyView.Builder builder = MysqlxCrud.ModifyView.newBuilder();
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
    
    public static Parser<ModifyView> parser() {
      return PARSER;
    }
    
    public Parser<ModifyView> getParserForType() {
      return PARSER;
    }
    
    public ModifyView getDefaultInstanceForType() {
      return DEFAULT_INSTANCE;
    }
  }
  
  public static interface DropViewOrBuilder extends MessageOrBuilder {
    boolean hasCollection();
    
    MysqlxCrud.Collection getCollection();
    
    MysqlxCrud.CollectionOrBuilder getCollectionOrBuilder();
    
    boolean hasIfExists();
    
    boolean getIfExists();
  }
  
  public static final class DropView extends GeneratedMessageV3 implements DropViewOrBuilder {
    private static final long serialVersionUID = 0L;
    
    private int bitField0_;
    
    public static final int COLLECTION_FIELD_NUMBER = 1;
    
    private MysqlxCrud.Collection collection_;
    
    public static final int IF_EXISTS_FIELD_NUMBER = 2;
    
    private boolean ifExists_;
    
    private byte memoizedIsInitialized;
    
    private DropView(GeneratedMessageV3.Builder<?> builder) {
      super(builder);
      this.memoizedIsInitialized = -1;
    }
    
    private DropView() {
      this.memoizedIsInitialized = -1;
    }
    
    protected Object newInstance(GeneratedMessageV3.UnusedPrivateParameter unused) {
      return new DropView();
    }
    
    public final UnknownFieldSet getUnknownFields() {
      return this.unknownFields;
    }
    
    public static final Descriptors.Descriptor getDescriptor() {
      return MysqlxCrud.internal_static_Mysqlx_Crud_DropView_descriptor;
    }
    
    protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() {
      return MysqlxCrud.internal_static_Mysqlx_Crud_DropView_fieldAccessorTable.ensureFieldAccessorsInitialized(DropView.class, Builder.class);
    }
    
    public boolean hasCollection() {
      return ((this.bitField0_ & 0x1) != 0);
    }
    
    public MysqlxCrud.Collection getCollection() {
      return (this.collection_ == null) ? MysqlxCrud.Collection.getDefaultInstance() : this.collection_;
    }
    
    public MysqlxCrud.CollectionOrBuilder getCollectionOrBuilder() {
      return (this.collection_ == null) ? MysqlxCrud.Collection.getDefaultInstance() : this.collection_;
    }
    
    public boolean hasIfExists() {
      return ((this.bitField0_ & 0x2) != 0);
    }
    
    public boolean getIfExists() {
      return this.ifExists_;
    }
    
    public final boolean isInitialized() {
      byte isInitialized = this.memoizedIsInitialized;
      if (isInitialized == 1)
        return true; 
      if (isInitialized == 0)
        return false; 
      if (!hasCollection()) {
        this.memoizedIsInitialized = 0;
        return false;
      } 
      if (!getCollection().isInitialized()) {
        this.memoizedIsInitialized = 0;
        return false;
      } 
      this.memoizedIsInitialized = 1;
      return true;
    }
    
    public void writeTo(CodedOutputStream output) throws IOException {
      if ((this.bitField0_ & 0x1) != 0)
        output.writeMessage(1, (MessageLite)getCollection()); 
      if ((this.bitField0_ & 0x2) != 0)
        output.writeBool(2, this.ifExists_); 
      getUnknownFields().writeTo(output);
    }
    
    public int getSerializedSize() {
      int size = this.memoizedSize;
      if (size != -1)
        return size; 
      size = 0;
      if ((this.bitField0_ & 0x1) != 0)
        size += 
          CodedOutputStream.computeMessageSize(1, (MessageLite)getCollection()); 
      if ((this.bitField0_ & 0x2) != 0)
        size += 
          CodedOutputStream.computeBoolSize(2, this.ifExists_); 
      size += getUnknownFields().getSerializedSize();
      this.memoizedSize = size;
      return size;
    }
    
    public boolean equals(Object obj) {
      if (obj == this)
        return true; 
      if (!(obj instanceof DropView))
        return super.equals(obj); 
      DropView other = (DropView)obj;
      if (hasCollection() != other.hasCollection())
        return false; 
      if (hasCollection() && 
        
        !getCollection().equals(other.getCollection()))
        return false; 
      if (hasIfExists() != other.hasIfExists())
        return false; 
      if (hasIfExists() && 
        getIfExists() != other
        .getIfExists())
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
      if (hasCollection()) {
        hash = 37 * hash + 1;
        hash = 53 * hash + getCollection().hashCode();
      } 
      if (hasIfExists()) {
        hash = 37 * hash + 2;
        hash = 53 * hash + Internal.hashBoolean(
            getIfExists());
      } 
      hash = 29 * hash + getUnknownFields().hashCode();
      this.memoizedHashCode = hash;
      return hash;
    }
    
    public static DropView parseFrom(ByteBuffer data) throws InvalidProtocolBufferException {
      return (DropView)PARSER.parseFrom(data);
    }
    
    public static DropView parseFrom(ByteBuffer data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
      return (DropView)PARSER.parseFrom(data, extensionRegistry);
    }
    
    public static DropView parseFrom(ByteString data) throws InvalidProtocolBufferException {
      return (DropView)PARSER.parseFrom(data);
    }
    
    public static DropView parseFrom(ByteString data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
      return (DropView)PARSER.parseFrom(data, extensionRegistry);
    }
    
    public static DropView parseFrom(byte[] data) throws InvalidProtocolBufferException {
      return (DropView)PARSER.parseFrom(data);
    }
    
    public static DropView parseFrom(byte[] data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
      return (DropView)PARSER.parseFrom(data, extensionRegistry);
    }
    
    public static DropView parseFrom(InputStream input) throws IOException {
      return 
        (DropView)GeneratedMessageV3.parseWithIOException(PARSER, input);
    }
    
    public static DropView parseFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
      return 
        (DropView)GeneratedMessageV3.parseWithIOException(PARSER, input, extensionRegistry);
    }
    
    public static DropView parseDelimitedFrom(InputStream input) throws IOException {
      return 
        (DropView)GeneratedMessageV3.parseDelimitedWithIOException(PARSER, input);
    }
    
    public static DropView parseDelimitedFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
      return 
        (DropView)GeneratedMessageV3.parseDelimitedWithIOException(PARSER, input, extensionRegistry);
    }
    
    public static DropView parseFrom(CodedInputStream input) throws IOException {
      return 
        (DropView)GeneratedMessageV3.parseWithIOException(PARSER, input);
    }
    
    public static DropView parseFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
      return 
        (DropView)GeneratedMessageV3.parseWithIOException(PARSER, input, extensionRegistry);
    }
    
    public Builder newBuilderForType() {
      return newBuilder();
    }
    
    public static Builder newBuilder() {
      return DEFAULT_INSTANCE.toBuilder();
    }
    
    public static Builder newBuilder(DropView prototype) {
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
    
    public static final class Builder extends GeneratedMessageV3.Builder<Builder> implements MysqlxCrud.DropViewOrBuilder {
      private int bitField0_;
      
      private MysqlxCrud.Collection collection_;
      
      private SingleFieldBuilderV3<MysqlxCrud.Collection, MysqlxCrud.Collection.Builder, MysqlxCrud.CollectionOrBuilder> collectionBuilder_;
      
      private boolean ifExists_;
      
      public static final Descriptors.Descriptor getDescriptor() {
        return MysqlxCrud.internal_static_Mysqlx_Crud_DropView_descriptor;
      }
      
      protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() {
        return MysqlxCrud.internal_static_Mysqlx_Crud_DropView_fieldAccessorTable
          .ensureFieldAccessorsInitialized(MysqlxCrud.DropView.class, Builder.class);
      }
      
      private Builder() {
        maybeForceBuilderInitialization();
      }
      
      private Builder(GeneratedMessageV3.BuilderParent parent) {
        super(parent);
        maybeForceBuilderInitialization();
      }
      
      private void maybeForceBuilderInitialization() {
        if (MysqlxCrud.DropView.alwaysUseFieldBuilders)
          getCollectionFieldBuilder(); 
      }
      
      public Builder clear() {
        super.clear();
        if (this.collectionBuilder_ == null) {
          this.collection_ = null;
        } else {
          this.collectionBuilder_.clear();
        } 
        this.bitField0_ &= 0xFFFFFFFE;
        this.ifExists_ = false;
        this.bitField0_ &= 0xFFFFFFFD;
        return this;
      }
      
      public Descriptors.Descriptor getDescriptorForType() {
        return MysqlxCrud.internal_static_Mysqlx_Crud_DropView_descriptor;
      }
      
      public MysqlxCrud.DropView getDefaultInstanceForType() {
        return MysqlxCrud.DropView.getDefaultInstance();
      }
      
      public MysqlxCrud.DropView build() {
        MysqlxCrud.DropView result = buildPartial();
        if (!result.isInitialized())
          throw newUninitializedMessageException(result); 
        return result;
      }
      
      public MysqlxCrud.DropView buildPartial() {
        MysqlxCrud.DropView result = new MysqlxCrud.DropView(this);
        int from_bitField0_ = this.bitField0_;
        int to_bitField0_ = 0;
        if ((from_bitField0_ & 0x1) != 0) {
          if (this.collectionBuilder_ == null) {
            result.collection_ = this.collection_;
          } else {
            result.collection_ = (MysqlxCrud.Collection)this.collectionBuilder_.build();
          } 
          to_bitField0_ |= 0x1;
        } 
        if ((from_bitField0_ & 0x2) != 0) {
          result.ifExists_ = this.ifExists_;
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
        if (other instanceof MysqlxCrud.DropView)
          return mergeFrom((MysqlxCrud.DropView)other); 
        super.mergeFrom(other);
        return this;
      }
      
      public Builder mergeFrom(MysqlxCrud.DropView other) {
        if (other == MysqlxCrud.DropView.getDefaultInstance())
          return this; 
        if (other.hasCollection())
          mergeCollection(other.getCollection()); 
        if (other.hasIfExists())
          setIfExists(other.getIfExists()); 
        mergeUnknownFields(other.getUnknownFields());
        onChanged();
        return this;
      }
      
      public final boolean isInitialized() {
        if (!hasCollection())
          return false; 
        if (!getCollection().isInitialized())
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
                input.readMessage((MessageLite.Builder)
                    getCollectionFieldBuilder().getBuilder(), extensionRegistry);
                this.bitField0_ |= 0x1;
                continue;
              case 16:
                this.ifExists_ = input.readBool();
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
      
      public boolean hasCollection() {
        return ((this.bitField0_ & 0x1) != 0);
      }
      
      public MysqlxCrud.Collection getCollection() {
        if (this.collectionBuilder_ == null)
          return (this.collection_ == null) ? MysqlxCrud.Collection.getDefaultInstance() : this.collection_; 
        return (MysqlxCrud.Collection)this.collectionBuilder_.getMessage();
      }
      
      public Builder setCollection(MysqlxCrud.Collection value) {
        if (this.collectionBuilder_ == null) {
          if (value == null)
            throw new NullPointerException(); 
          this.collection_ = value;
          onChanged();
        } else {
          this.collectionBuilder_.setMessage((AbstractMessage)value);
        } 
        this.bitField0_ |= 0x1;
        return this;
      }
      
      public Builder setCollection(MysqlxCrud.Collection.Builder builderForValue) {
        if (this.collectionBuilder_ == null) {
          this.collection_ = builderForValue.build();
          onChanged();
        } else {
          this.collectionBuilder_.setMessage((AbstractMessage)builderForValue.build());
        } 
        this.bitField0_ |= 0x1;
        return this;
      }
      
      public Builder mergeCollection(MysqlxCrud.Collection value) {
        if (this.collectionBuilder_ == null) {
          if ((this.bitField0_ & 0x1) != 0 && this.collection_ != null && this.collection_ != 
            
            MysqlxCrud.Collection.getDefaultInstance()) {
            this
              .collection_ = MysqlxCrud.Collection.newBuilder(this.collection_).mergeFrom(value).buildPartial();
          } else {
            this.collection_ = value;
          } 
          onChanged();
        } else {
          this.collectionBuilder_.mergeFrom((AbstractMessage)value);
        } 
        this.bitField0_ |= 0x1;
        return this;
      }
      
      public Builder clearCollection() {
        if (this.collectionBuilder_ == null) {
          this.collection_ = null;
          onChanged();
        } else {
          this.collectionBuilder_.clear();
        } 
        this.bitField0_ &= 0xFFFFFFFE;
        return this;
      }
      
      public MysqlxCrud.Collection.Builder getCollectionBuilder() {
        this.bitField0_ |= 0x1;
        onChanged();
        return (MysqlxCrud.Collection.Builder)getCollectionFieldBuilder().getBuilder();
      }
      
      public MysqlxCrud.CollectionOrBuilder getCollectionOrBuilder() {
        if (this.collectionBuilder_ != null)
          return (MysqlxCrud.CollectionOrBuilder)this.collectionBuilder_.getMessageOrBuilder(); 
        return (this.collection_ == null) ? 
          MysqlxCrud.Collection.getDefaultInstance() : this.collection_;
      }
      
      private SingleFieldBuilderV3<MysqlxCrud.Collection, MysqlxCrud.Collection.Builder, MysqlxCrud.CollectionOrBuilder> getCollectionFieldBuilder() {
        if (this.collectionBuilder_ == null) {
          this
            
            .collectionBuilder_ = new SingleFieldBuilderV3((AbstractMessage)getCollection(), (AbstractMessage.BuilderParent)getParentForChildren(), isClean());
          this.collection_ = null;
        } 
        return this.collectionBuilder_;
      }
      
      public boolean hasIfExists() {
        return ((this.bitField0_ & 0x2) != 0);
      }
      
      public boolean getIfExists() {
        return this.ifExists_;
      }
      
      public Builder setIfExists(boolean value) {
        this.bitField0_ |= 0x2;
        this.ifExists_ = value;
        onChanged();
        return this;
      }
      
      public Builder clearIfExists() {
        this.bitField0_ &= 0xFFFFFFFD;
        this.ifExists_ = false;
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
    
    private static final DropView DEFAULT_INSTANCE = new DropView();
    
    public static DropView getDefaultInstance() {
      return DEFAULT_INSTANCE;
    }
    
    @Deprecated
    public static final Parser<DropView> PARSER = (Parser<DropView>)new AbstractParser<DropView>() {
        public MysqlxCrud.DropView parsePartialFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
          MysqlxCrud.DropView.Builder builder = MysqlxCrud.DropView.newBuilder();
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
    
    public static Parser<DropView> parser() {
      return PARSER;
    }
    
    public Parser<DropView> getParserForType() {
      return PARSER;
    }
    
    public DropView getDefaultInstanceForType() {
      return DEFAULT_INSTANCE;
    }
  }
  
  public static Descriptors.FileDescriptor getDescriptor() {
    return descriptor;
  }
  
  static {
    String[] descriptorData = { "\n\021mysqlx_crud.proto\022\013Mysqlx.Crud\032\fmysqlx.proto\032\021mysqlx_expr.proto\032\026mysqlx_datatypes.proto\"[\n\006Column\022\f\n\004name\030\001 \001(\t\022\r\n\005alias\030\002 \001(\t\0224\n\rdocument_path\030\003 \003(\0132\035.Mysqlx.Expr.DocumentPathItem\">\n\nProjection\022!\n\006source\030\001 \002(\0132\021.Mysqlx.Expr.Expr\022\r\n\005alias\030\002 \001(\t\"*\n\nCollection\022\f\n\004name\030\001 \002(\t\022\016\n\006schema\030\002 \001(\t\"*\n\005Limit\022\021\n\trow_count\030\001 \002(\004\022\016\n\006offset\030\002 \001(\004\"T\n\tLimitExpr\022$\n\trow_count\030\001 \002(\0132\021.Mysqlx.Expr.Expr\022!\n\006offset\030\002 \001(\0132\021.Mysqlx.Expr.Expr\"~\n\005Order\022\037\n\004expr\030\001 \002(\0132\021.Mysqlx.Expr.Expr\0224\n\tdirection\030\002 \001(\0162\034.Mysqlx.Crud.Order.Direction:\003ASC\"\036\n\tDirection\022\007\n\003ASC\020\001\022\b\n\004DESC\020\002\"¬\002\n\017UpdateOperation\022-\n\006source\030\001 \002(\0132\035.Mysqlx.Expr.ColumnIdentifier\022:\n\toperation\030\002 \002(\0162'.Mysqlx.Crud.UpdateOperation.UpdateType\022 \n\005value\030\003 \001(\0132\021.Mysqlx.Expr.Expr\"\001\n\nUpdateType\022\007\n\003SET\020\001\022\017\n\013ITEM_REMOVE\020\002\022\f\n\bITEM_SET\020\003\022\020\n\fITEM_REPLACE\020\004\022\016\n\nITEM_MERGE\020\005\022\020\n\fARRAY_INSERT\020\006\022\020\n\fARRAY_APPEND\020\007\022\017\n\013MERGE_PATCH\020\b\"ê\004\n\004Find\022+\n\ncollection\030\002 \002(\0132\027.Mysqlx.Crud.Collection\022*\n\ndata_model\030\003 \001(\0162\026.Mysqlx.Crud.DataModel\022+\n\nprojection\030\004 \003(\0132\027.Mysqlx.Crud.Projection\022&\n\004args\030\013 \003(\0132\030.Mysqlx.Datatypes.Scalar\022#\n\bcriteria\030\005 \001(\0132\021.Mysqlx.Expr.Expr\022!\n\005limit\030\006 \001(\0132\022.Mysqlx.Crud.Limit\022!\n\005order\030\007 \003(\0132\022.Mysqlx.Crud.Order\022#\n\bgrouping\030\b \003(\0132\021.Mysqlx.Expr.Expr\022,\n\021grouping_criteria\030\t \001(\0132\021.Mysqlx.Expr.Expr\022*\n\007locking\030\f \001(\0162\031.Mysqlx.Crud.Find.RowLock\0229\n\017locking_options\030\r \001(\0162 .Mysqlx.Crud.Find.RowLockOptions\022*\n\nlimit_expr\030\016 \001(\0132\026.Mysqlx.Crud.LimitExpr\".\n\007RowLock\022\017\n\013SHARED_LOCK\020\001\022\022\n\016EXCLUSIVE_LOCK\020\002\"-\n\016RowLockOptions\022\n\n\006NOWAIT\020\001\022\017\n\013SKIP_LOCKED\020\002:\004ê0\021\"¨\002\n\006Insert\022+\n\ncollection\030\001 \002(\0132\027.Mysqlx.Crud.Collection\022*\n\ndata_model\030\002 \001(\0162\026.Mysqlx.Crud.DataModel\022'\n\nprojection\030\003 \003(\0132\023.Mysqlx.Crud.Column\022)\n\003row\030\004 \003(\0132\034.Mysqlx.Crud.Insert.TypedRow\022&\n\004args\030\005 \003(\0132\030.Mysqlx.Datatypes.Scalar\022\025\n\006upsert\030\006 \001(\b:\005false\032,\n\bTypedRow\022 \n\005field\030\001 \003(\0132\021.Mysqlx.Expr.Expr:\004ê0\022\"×\002\n\006Update\022+\n\ncollection\030\002 \002(\0132\027.Mysqlx.Crud.Collection\022*\n\ndata_model\030\003 \001(\0162\026.Mysqlx.Crud.DataModel\022#\n\bcriteria\030\004 \001(\0132\021.Mysqlx.Expr.Expr\022!\n\005limit\030\005 \001(\0132\022.Mysqlx.Crud.Limit\022!\n\005order\030\006 \003(\0132\022.Mysqlx.Crud.Order\022/\n\toperation\030\007 \003(\0132\034.Mysqlx.Crud.UpdateOperation\022&\n\004args\030\b \003(\0132\030.Mysqlx.Datatypes.Scalar\022*\n\nlimit_expr\030\t \001(\0132\026.Mysqlx.Crud.LimitExpr:\004ê0\023\"¦\002\n\006Delete\022+\n\ncollection\030\001 \002(\0132\027.Mysqlx.Crud.Collection\022*\n\ndata_model\030\002 \001(\0162\026.Mysqlx.Crud.DataModel\022#\n\bcriteria\030\003 \001(\0132\021.Mysqlx.Expr.Expr\022!\n\005limit\030\004 \001(\0132\022.Mysqlx.Crud.Limit\022!\n\005order\030\005 \003(\0132\022.Mysqlx.Crud.Order\022&\n\004args\030\006 \003(\0132\030.Mysqlx.Datatypes.Scalar\022*\n\nlimit_expr\030\007 \001(\0132\026.Mysqlx.Crud.LimitExpr:\004ê0\024\"Â\002\n\nCreateView\022+\n\ncollection\030\001 \002(\0132\027.Mysqlx.Crud.Collection\022\017\n\007definer\030\002 \001(\t\0228\n\talgorithm\030\003 \001(\0162\032.Mysqlx.Crud.ViewAlgorithm:\tUNDEFINED\0227\n\bsecurity\030\004 \001(\0162\034.Mysqlx.Crud.ViewSqlSecurity:\007DEFINER\022+\n\005check\030\005 \001(\0162\034.Mysqlx.Crud.ViewCheckOption\022\016\n\006column\030\006 \003(\t\022\037\n\004stmt\030\007 \002(\0132\021.Mysqlx.Crud.Find\022\037\n\020replace_existing\030\b \001(\b:\005false:\004ê0\036\"\002\n\nModifyView\022+\n\ncollection\030\001 \002(\0132\027.Mysqlx.Crud.Collection\022\017\n\007definer\030\002 \001(\t\022-\n\talgorithm\030\003 \001(\0162\032.Mysqlx.Crud.ViewAlgorithm\022.\n\bsecurity\030\004 \001(\0162\034.Mysqlx.Crud.ViewSqlSecurity\022+\n\005check\030\005 \001(\0162\034.Mysqlx.Crud.ViewCheckOption\022\016\n\006column\030\006 \003(\t\022\037\n\004stmt\030\007 \001(\0132\021.Mysqlx.Crud.Find:\004ê0\037\"W\n\bDropView\022+\n\ncollection\030\001 \002(\0132\027.Mysqlx.Crud.Collection\022\030\n\tif_exists\030\002 \001(\b:\005false:\004ê0 *$\n\tDataModel\022\f\n\bDOCUMENT\020\001\022\t\n\005TABLE\020\002*8\n\rViewAlgorithm\022\r\n\tUNDEFINED\020\001\022\t\n\005MERGE\020\002\022\r\n\tTEMPTABLE\020\003*+\n\017ViewSqlSecurity\022\013\n\007INVOKER\020\001\022\013\n\007DEFINER\020\002**\n\017ViewCheckOption\022\t\n\005LOCAL\020\001\022\f\n\bCASCADED\020\002B\031\n\027com.mysql.cj.x.protobuf" };
    descriptor = Descriptors.FileDescriptor.internalBuildGeneratedFileFrom(descriptorData, new Descriptors.FileDescriptor[] { Mysqlx.getDescriptor(), 
          MysqlxExpr.getDescriptor(), 
          MysqlxDatatypes.getDescriptor() });
    internal_static_Mysqlx_Crud_Column_descriptor = getDescriptor().getMessageTypes().get(0);
    internal_static_Mysqlx_Crud_Column_fieldAccessorTable = new GeneratedMessageV3.FieldAccessorTable(internal_static_Mysqlx_Crud_Column_descriptor, new String[] { "Name", "Alias", "DocumentPath" });
    internal_static_Mysqlx_Crud_Projection_descriptor = getDescriptor().getMessageTypes().get(1);
    internal_static_Mysqlx_Crud_Projection_fieldAccessorTable = new GeneratedMessageV3.FieldAccessorTable(internal_static_Mysqlx_Crud_Projection_descriptor, new String[] { "Source", "Alias" });
    internal_static_Mysqlx_Crud_Collection_descriptor = getDescriptor().getMessageTypes().get(2);
    internal_static_Mysqlx_Crud_Collection_fieldAccessorTable = new GeneratedMessageV3.FieldAccessorTable(internal_static_Mysqlx_Crud_Collection_descriptor, new String[] { "Name", "Schema" });
    internal_static_Mysqlx_Crud_Limit_descriptor = getDescriptor().getMessageTypes().get(3);
    internal_static_Mysqlx_Crud_Limit_fieldAccessorTable = new GeneratedMessageV3.FieldAccessorTable(internal_static_Mysqlx_Crud_Limit_descriptor, new String[] { "RowCount", "Offset" });
    internal_static_Mysqlx_Crud_LimitExpr_descriptor = getDescriptor().getMessageTypes().get(4);
    internal_static_Mysqlx_Crud_LimitExpr_fieldAccessorTable = new GeneratedMessageV3.FieldAccessorTable(internal_static_Mysqlx_Crud_LimitExpr_descriptor, new String[] { "RowCount", "Offset" });
    internal_static_Mysqlx_Crud_Order_descriptor = getDescriptor().getMessageTypes().get(5);
    internal_static_Mysqlx_Crud_Order_fieldAccessorTable = new GeneratedMessageV3.FieldAccessorTable(internal_static_Mysqlx_Crud_Order_descriptor, new String[] { "Expr", "Direction" });
    internal_static_Mysqlx_Crud_UpdateOperation_descriptor = getDescriptor().getMessageTypes().get(6);
    internal_static_Mysqlx_Crud_UpdateOperation_fieldAccessorTable = new GeneratedMessageV3.FieldAccessorTable(internal_static_Mysqlx_Crud_UpdateOperation_descriptor, new String[] { "Source", "Operation", "Value" });
    internal_static_Mysqlx_Crud_Find_descriptor = getDescriptor().getMessageTypes().get(7);
    internal_static_Mysqlx_Crud_Find_fieldAccessorTable = new GeneratedMessageV3.FieldAccessorTable(internal_static_Mysqlx_Crud_Find_descriptor, new String[] { 
          "Collection", "DataModel", "Projection", "Args", "Criteria", "Limit", "Order", "Grouping", "GroupingCriteria", "Locking", 
          "LockingOptions", "LimitExpr" });
    internal_static_Mysqlx_Crud_Insert_descriptor = getDescriptor().getMessageTypes().get(8);
    internal_static_Mysqlx_Crud_Insert_fieldAccessorTable = new GeneratedMessageV3.FieldAccessorTable(internal_static_Mysqlx_Crud_Insert_descriptor, new String[] { "Collection", "DataModel", "Projection", "Row", "Args", "Upsert" });
    internal_static_Mysqlx_Crud_Insert_TypedRow_descriptor = internal_static_Mysqlx_Crud_Insert_descriptor.getNestedTypes().get(0);
    internal_static_Mysqlx_Crud_Insert_TypedRow_fieldAccessorTable = new GeneratedMessageV3.FieldAccessorTable(internal_static_Mysqlx_Crud_Insert_TypedRow_descriptor, new String[] { "Field" });
    internal_static_Mysqlx_Crud_Update_descriptor = getDescriptor().getMessageTypes().get(9);
    internal_static_Mysqlx_Crud_Update_fieldAccessorTable = new GeneratedMessageV3.FieldAccessorTable(internal_static_Mysqlx_Crud_Update_descriptor, new String[] { "Collection", "DataModel", "Criteria", "Limit", "Order", "Operation", "Args", "LimitExpr" });
    internal_static_Mysqlx_Crud_Delete_descriptor = getDescriptor().getMessageTypes().get(10);
    internal_static_Mysqlx_Crud_Delete_fieldAccessorTable = new GeneratedMessageV3.FieldAccessorTable(internal_static_Mysqlx_Crud_Delete_descriptor, new String[] { "Collection", "DataModel", "Criteria", "Limit", "Order", "Args", "LimitExpr" });
    internal_static_Mysqlx_Crud_CreateView_descriptor = getDescriptor().getMessageTypes().get(11);
    internal_static_Mysqlx_Crud_CreateView_fieldAccessorTable = new GeneratedMessageV3.FieldAccessorTable(internal_static_Mysqlx_Crud_CreateView_descriptor, new String[] { "Collection", "Definer", "Algorithm", "Security", "Check", "Column", "Stmt", "ReplaceExisting" });
    internal_static_Mysqlx_Crud_ModifyView_descriptor = getDescriptor().getMessageTypes().get(12);
    internal_static_Mysqlx_Crud_ModifyView_fieldAccessorTable = new GeneratedMessageV3.FieldAccessorTable(internal_static_Mysqlx_Crud_ModifyView_descriptor, new String[] { "Collection", "Definer", "Algorithm", "Security", "Check", "Column", "Stmt" });
    internal_static_Mysqlx_Crud_DropView_descriptor = getDescriptor().getMessageTypes().get(13);
    internal_static_Mysqlx_Crud_DropView_fieldAccessorTable = new GeneratedMessageV3.FieldAccessorTable(internal_static_Mysqlx_Crud_DropView_descriptor, new String[] { "Collection", "IfExists" });
    ExtensionRegistry registry = ExtensionRegistry.newInstance();
    registry.add(Mysqlx.clientMessageId);
    Descriptors.FileDescriptor.internalUpdateFileDescriptor(descriptor, registry);
    Mysqlx.getDescriptor();
    MysqlxExpr.getDescriptor();
    MysqlxDatatypes.getDescriptor();
  }
}
