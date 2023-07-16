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
import com.google.protobuf.RepeatedFieldBuilderV3;
import com.google.protobuf.UninitializedMessageException;
import com.google.protobuf.UnknownFieldSet;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class MysqlxSql {
  private static final Descriptors.Descriptor internal_static_Mysqlx_Sql_StmtExecute_descriptor;
  
  private static final GeneratedMessageV3.FieldAccessorTable internal_static_Mysqlx_Sql_StmtExecute_fieldAccessorTable;
  
  private static final Descriptors.Descriptor internal_static_Mysqlx_Sql_StmtExecuteOk_descriptor;
  
  private static final GeneratedMessageV3.FieldAccessorTable internal_static_Mysqlx_Sql_StmtExecuteOk_fieldAccessorTable;
  
  private static Descriptors.FileDescriptor descriptor;
  
  public static void registerAllExtensions(ExtensionRegistryLite registry) {}
  
  public static void registerAllExtensions(ExtensionRegistry registry) {
    registerAllExtensions((ExtensionRegistryLite)registry);
  }
  
  public static interface StmtExecuteOrBuilder extends MessageOrBuilder {
    boolean hasNamespace();
    
    String getNamespace();
    
    ByteString getNamespaceBytes();
    
    boolean hasStmt();
    
    ByteString getStmt();
    
    List<MysqlxDatatypes.Any> getArgsList();
    
    MysqlxDatatypes.Any getArgs(int param1Int);
    
    int getArgsCount();
    
    List<? extends MysqlxDatatypes.AnyOrBuilder> getArgsOrBuilderList();
    
    MysqlxDatatypes.AnyOrBuilder getArgsOrBuilder(int param1Int);
    
    boolean hasCompactMetadata();
    
    boolean getCompactMetadata();
  }
  
  public static final class StmtExecute extends GeneratedMessageV3 implements StmtExecuteOrBuilder {
    private static final long serialVersionUID = 0L;
    
    private int bitField0_;
    
    public static final int NAMESPACE_FIELD_NUMBER = 3;
    
    private volatile Object namespace_;
    
    public static final int STMT_FIELD_NUMBER = 1;
    
    private ByteString stmt_;
    
    public static final int ARGS_FIELD_NUMBER = 2;
    
    private List<MysqlxDatatypes.Any> args_;
    
    public static final int COMPACT_METADATA_FIELD_NUMBER = 4;
    
    private boolean compactMetadata_;
    
    private byte memoizedIsInitialized;
    
    private StmtExecute(GeneratedMessageV3.Builder<?> builder) {
      super(builder);
      this.memoizedIsInitialized = -1;
    }
    
    private StmtExecute() {
      this.memoizedIsInitialized = -1;
      this.namespace_ = "sql";
      this.stmt_ = ByteString.EMPTY;
      this.args_ = Collections.emptyList();
    }
    
    protected Object newInstance(GeneratedMessageV3.UnusedPrivateParameter unused) {
      return new StmtExecute();
    }
    
    public final UnknownFieldSet getUnknownFields() {
      return this.unknownFields;
    }
    
    public static final Descriptors.Descriptor getDescriptor() {
      return MysqlxSql.internal_static_Mysqlx_Sql_StmtExecute_descriptor;
    }
    
    protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() {
      return MysqlxSql.internal_static_Mysqlx_Sql_StmtExecute_fieldAccessorTable.ensureFieldAccessorsInitialized(StmtExecute.class, Builder.class);
    }
    
    public boolean hasNamespace() {
      return ((this.bitField0_ & 0x1) != 0);
    }
    
    public String getNamespace() {
      Object ref = this.namespace_;
      if (ref instanceof String)
        return (String)ref; 
      ByteString bs = (ByteString)ref;
      String s = bs.toStringUtf8();
      if (bs.isValidUtf8())
        this.namespace_ = s; 
      return s;
    }
    
    public ByteString getNamespaceBytes() {
      Object ref = this.namespace_;
      if (ref instanceof String) {
        ByteString b = ByteString.copyFromUtf8((String)ref);
        this.namespace_ = b;
        return b;
      } 
      return (ByteString)ref;
    }
    
    public boolean hasStmt() {
      return ((this.bitField0_ & 0x2) != 0);
    }
    
    public ByteString getStmt() {
      return this.stmt_;
    }
    
    public List<MysqlxDatatypes.Any> getArgsList() {
      return this.args_;
    }
    
    public List<? extends MysqlxDatatypes.AnyOrBuilder> getArgsOrBuilderList() {
      return (List)this.args_;
    }
    
    public int getArgsCount() {
      return this.args_.size();
    }
    
    public MysqlxDatatypes.Any getArgs(int index) {
      return this.args_.get(index);
    }
    
    public MysqlxDatatypes.AnyOrBuilder getArgsOrBuilder(int index) {
      return this.args_.get(index);
    }
    
    public boolean hasCompactMetadata() {
      return ((this.bitField0_ & 0x4) != 0);
    }
    
    public boolean getCompactMetadata() {
      return this.compactMetadata_;
    }
    
    public final boolean isInitialized() {
      byte isInitialized = this.memoizedIsInitialized;
      if (isInitialized == 1)
        return true; 
      if (isInitialized == 0)
        return false; 
      if (!hasStmt()) {
        this.memoizedIsInitialized = 0;
        return false;
      } 
      for (int i = 0; i < getArgsCount(); i++) {
        if (!getArgs(i).isInitialized()) {
          this.memoizedIsInitialized = 0;
          return false;
        } 
      } 
      this.memoizedIsInitialized = 1;
      return true;
    }
    
    public void writeTo(CodedOutputStream output) throws IOException {
      if ((this.bitField0_ & 0x2) != 0)
        output.writeBytes(1, this.stmt_); 
      for (int i = 0; i < this.args_.size(); i++)
        output.writeMessage(2, (MessageLite)this.args_.get(i)); 
      if ((this.bitField0_ & 0x1) != 0)
        GeneratedMessageV3.writeString(output, 3, this.namespace_); 
      if ((this.bitField0_ & 0x4) != 0)
        output.writeBool(4, this.compactMetadata_); 
      getUnknownFields().writeTo(output);
    }
    
    public int getSerializedSize() {
      int size = this.memoizedSize;
      if (size != -1)
        return size; 
      size = 0;
      if ((this.bitField0_ & 0x2) != 0)
        size += 
          CodedOutputStream.computeBytesSize(1, this.stmt_); 
      for (int i = 0; i < this.args_.size(); i++)
        size += 
          CodedOutputStream.computeMessageSize(2, (MessageLite)this.args_.get(i)); 
      if ((this.bitField0_ & 0x1) != 0)
        size += GeneratedMessageV3.computeStringSize(3, this.namespace_); 
      if ((this.bitField0_ & 0x4) != 0)
        size += 
          CodedOutputStream.computeBoolSize(4, this.compactMetadata_); 
      size += getUnknownFields().getSerializedSize();
      this.memoizedSize = size;
      return size;
    }
    
    public boolean equals(Object obj) {
      if (obj == this)
        return true; 
      if (!(obj instanceof StmtExecute))
        return super.equals(obj); 
      StmtExecute other = (StmtExecute)obj;
      if (hasNamespace() != other.hasNamespace())
        return false; 
      if (hasNamespace() && 
        
        !getNamespace().equals(other.getNamespace()))
        return false; 
      if (hasStmt() != other.hasStmt())
        return false; 
      if (hasStmt() && 
        
        !getStmt().equals(other.getStmt()))
        return false; 
      if (!getArgsList().equals(other.getArgsList()))
        return false; 
      if (hasCompactMetadata() != other.hasCompactMetadata())
        return false; 
      if (hasCompactMetadata() && 
        getCompactMetadata() != other
        .getCompactMetadata())
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
      if (hasNamespace()) {
        hash = 37 * hash + 3;
        hash = 53 * hash + getNamespace().hashCode();
      } 
      if (hasStmt()) {
        hash = 37 * hash + 1;
        hash = 53 * hash + getStmt().hashCode();
      } 
      if (getArgsCount() > 0) {
        hash = 37 * hash + 2;
        hash = 53 * hash + getArgsList().hashCode();
      } 
      if (hasCompactMetadata()) {
        hash = 37 * hash + 4;
        hash = 53 * hash + Internal.hashBoolean(
            getCompactMetadata());
      } 
      hash = 29 * hash + getUnknownFields().hashCode();
      this.memoizedHashCode = hash;
      return hash;
    }
    
    public static StmtExecute parseFrom(ByteBuffer data) throws InvalidProtocolBufferException {
      return (StmtExecute)PARSER.parseFrom(data);
    }
    
    public static StmtExecute parseFrom(ByteBuffer data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
      return (StmtExecute)PARSER.parseFrom(data, extensionRegistry);
    }
    
    public static StmtExecute parseFrom(ByteString data) throws InvalidProtocolBufferException {
      return (StmtExecute)PARSER.parseFrom(data);
    }
    
    public static StmtExecute parseFrom(ByteString data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
      return (StmtExecute)PARSER.parseFrom(data, extensionRegistry);
    }
    
    public static StmtExecute parseFrom(byte[] data) throws InvalidProtocolBufferException {
      return (StmtExecute)PARSER.parseFrom(data);
    }
    
    public static StmtExecute parseFrom(byte[] data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
      return (StmtExecute)PARSER.parseFrom(data, extensionRegistry);
    }
    
    public static StmtExecute parseFrom(InputStream input) throws IOException {
      return 
        (StmtExecute)GeneratedMessageV3.parseWithIOException(PARSER, input);
    }
    
    public static StmtExecute parseFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
      return 
        (StmtExecute)GeneratedMessageV3.parseWithIOException(PARSER, input, extensionRegistry);
    }
    
    public static StmtExecute parseDelimitedFrom(InputStream input) throws IOException {
      return 
        (StmtExecute)GeneratedMessageV3.parseDelimitedWithIOException(PARSER, input);
    }
    
    public static StmtExecute parseDelimitedFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
      return 
        (StmtExecute)GeneratedMessageV3.parseDelimitedWithIOException(PARSER, input, extensionRegistry);
    }
    
    public static StmtExecute parseFrom(CodedInputStream input) throws IOException {
      return 
        (StmtExecute)GeneratedMessageV3.parseWithIOException(PARSER, input);
    }
    
    public static StmtExecute parseFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
      return 
        (StmtExecute)GeneratedMessageV3.parseWithIOException(PARSER, input, extensionRegistry);
    }
    
    public Builder newBuilderForType() {
      return newBuilder();
    }
    
    public static Builder newBuilder() {
      return DEFAULT_INSTANCE.toBuilder();
    }
    
    public static Builder newBuilder(StmtExecute prototype) {
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
    
    public static final class Builder extends GeneratedMessageV3.Builder<Builder> implements MysqlxSql.StmtExecuteOrBuilder {
      private int bitField0_;
      
      private Object namespace_;
      
      private ByteString stmt_;
      
      private List<MysqlxDatatypes.Any> args_;
      
      private RepeatedFieldBuilderV3<MysqlxDatatypes.Any, MysqlxDatatypes.Any.Builder, MysqlxDatatypes.AnyOrBuilder> argsBuilder_;
      
      private boolean compactMetadata_;
      
      public static final Descriptors.Descriptor getDescriptor() {
        return MysqlxSql.internal_static_Mysqlx_Sql_StmtExecute_descriptor;
      }
      
      protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() {
        return MysqlxSql.internal_static_Mysqlx_Sql_StmtExecute_fieldAccessorTable
          .ensureFieldAccessorsInitialized(MysqlxSql.StmtExecute.class, Builder.class);
      }
      
      private Builder() {
        this.namespace_ = "sql";
        this.stmt_ = ByteString.EMPTY;
        this
          .args_ = Collections.emptyList();
      }
      
      private Builder(GeneratedMessageV3.BuilderParent parent) {
        super(parent);
        this.namespace_ = "sql";
        this.stmt_ = ByteString.EMPTY;
        this.args_ = Collections.emptyList();
      }
      
      public Builder clear() {
        super.clear();
        this.namespace_ = "sql";
        this.bitField0_ &= 0xFFFFFFFE;
        this.stmt_ = ByteString.EMPTY;
        this.bitField0_ &= 0xFFFFFFFD;
        if (this.argsBuilder_ == null) {
          this.args_ = Collections.emptyList();
        } else {
          this.args_ = null;
          this.argsBuilder_.clear();
        } 
        this.bitField0_ &= 0xFFFFFFFB;
        this.compactMetadata_ = false;
        this.bitField0_ &= 0xFFFFFFF7;
        return this;
      }
      
      public Descriptors.Descriptor getDescriptorForType() {
        return MysqlxSql.internal_static_Mysqlx_Sql_StmtExecute_descriptor;
      }
      
      public MysqlxSql.StmtExecute getDefaultInstanceForType() {
        return MysqlxSql.StmtExecute.getDefaultInstance();
      }
      
      public MysqlxSql.StmtExecute build() {
        MysqlxSql.StmtExecute result = buildPartial();
        if (!result.isInitialized())
          throw newUninitializedMessageException(result); 
        return result;
      }
      
      public MysqlxSql.StmtExecute buildPartial() {
        MysqlxSql.StmtExecute result = new MysqlxSql.StmtExecute(this);
        int from_bitField0_ = this.bitField0_;
        int to_bitField0_ = 0;
        if ((from_bitField0_ & 0x1) != 0)
          to_bitField0_ |= 0x1; 
        result.namespace_ = this.namespace_;
        if ((from_bitField0_ & 0x2) != 0)
          to_bitField0_ |= 0x2; 
        result.stmt_ = this.stmt_;
        if (this.argsBuilder_ == null) {
          if ((this.bitField0_ & 0x4) != 0) {
            this.args_ = Collections.unmodifiableList(this.args_);
            this.bitField0_ &= 0xFFFFFFFB;
          } 
          result.args_ = this.args_;
        } else {
          result.args_ = this.argsBuilder_.build();
        } 
        if ((from_bitField0_ & 0x8) != 0) {
          result.compactMetadata_ = this.compactMetadata_;
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
        if (other instanceof MysqlxSql.StmtExecute)
          return mergeFrom((MysqlxSql.StmtExecute)other); 
        super.mergeFrom(other);
        return this;
      }
      
      public Builder mergeFrom(MysqlxSql.StmtExecute other) {
        if (other == MysqlxSql.StmtExecute.getDefaultInstance())
          return this; 
        if (other.hasNamespace()) {
          this.bitField0_ |= 0x1;
          this.namespace_ = other.namespace_;
          onChanged();
        } 
        if (other.hasStmt())
          setStmt(other.getStmt()); 
        if (this.argsBuilder_ == null) {
          if (!other.args_.isEmpty()) {
            if (this.args_.isEmpty()) {
              this.args_ = other.args_;
              this.bitField0_ &= 0xFFFFFFFB;
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
            this.bitField0_ &= 0xFFFFFFFB;
            this.argsBuilder_ = MysqlxSql.StmtExecute.alwaysUseFieldBuilders ? getArgsFieldBuilder() : null;
          } else {
            this.argsBuilder_.addAllMessages(other.args_);
          } 
        } 
        if (other.hasCompactMetadata())
          setCompactMetadata(other.getCompactMetadata()); 
        mergeUnknownFields(other.getUnknownFields());
        onChanged();
        return this;
      }
      
      public final boolean isInitialized() {
        if (!hasStmt())
          return false; 
        for (int i = 0; i < getArgsCount(); i++) {
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
            MysqlxDatatypes.Any m;
            int tag = input.readTag();
            switch (tag) {
              case 0:
                done = true;
                continue;
              case 10:
                this.stmt_ = input.readBytes();
                this.bitField0_ |= 0x2;
                continue;
              case 18:
                m = (MysqlxDatatypes.Any)input.readMessage(MysqlxDatatypes.Any.PARSER, extensionRegistry);
                if (this.argsBuilder_ == null) {
                  ensureArgsIsMutable();
                  this.args_.add(m);
                  continue;
                } 
                this.argsBuilder_.addMessage((AbstractMessage)m);
                continue;
              case 26:
                this.namespace_ = input.readBytes();
                this.bitField0_ |= 0x1;
                continue;
              case 32:
                this.compactMetadata_ = input.readBool();
                this.bitField0_ |= 0x8;
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
      
      public boolean hasNamespace() {
        return ((this.bitField0_ & 0x1) != 0);
      }
      
      public String getNamespace() {
        Object ref = this.namespace_;
        if (!(ref instanceof String)) {
          ByteString bs = (ByteString)ref;
          String s = bs.toStringUtf8();
          if (bs.isValidUtf8())
            this.namespace_ = s; 
          return s;
        } 
        return (String)ref;
      }
      
      public ByteString getNamespaceBytes() {
        Object ref = this.namespace_;
        if (ref instanceof String) {
          ByteString b = ByteString.copyFromUtf8((String)ref);
          this.namespace_ = b;
          return b;
        } 
        return (ByteString)ref;
      }
      
      public Builder setNamespace(String value) {
        if (value == null)
          throw new NullPointerException(); 
        this.bitField0_ |= 0x1;
        this.namespace_ = value;
        onChanged();
        return this;
      }
      
      public Builder clearNamespace() {
        this.bitField0_ &= 0xFFFFFFFE;
        this.namespace_ = MysqlxSql.StmtExecute.getDefaultInstance().getNamespace();
        onChanged();
        return this;
      }
      
      public Builder setNamespaceBytes(ByteString value) {
        if (value == null)
          throw new NullPointerException(); 
        this.bitField0_ |= 0x1;
        this.namespace_ = value;
        onChanged();
        return this;
      }
      
      public boolean hasStmt() {
        return ((this.bitField0_ & 0x2) != 0);
      }
      
      public ByteString getStmt() {
        return this.stmt_;
      }
      
      public Builder setStmt(ByteString value) {
        if (value == null)
          throw new NullPointerException(); 
        this.bitField0_ |= 0x2;
        this.stmt_ = value;
        onChanged();
        return this;
      }
      
      public Builder clearStmt() {
        this.bitField0_ &= 0xFFFFFFFD;
        this.stmt_ = MysqlxSql.StmtExecute.getDefaultInstance().getStmt();
        onChanged();
        return this;
      }
      
      private void ensureArgsIsMutable() {
        if ((this.bitField0_ & 0x4) == 0) {
          this.args_ = new ArrayList<>(this.args_);
          this.bitField0_ |= 0x4;
        } 
      }
      
      public List<MysqlxDatatypes.Any> getArgsList() {
        if (this.argsBuilder_ == null)
          return Collections.unmodifiableList(this.args_); 
        return this.argsBuilder_.getMessageList();
      }
      
      public int getArgsCount() {
        if (this.argsBuilder_ == null)
          return this.args_.size(); 
        return this.argsBuilder_.getCount();
      }
      
      public MysqlxDatatypes.Any getArgs(int index) {
        if (this.argsBuilder_ == null)
          return this.args_.get(index); 
        return (MysqlxDatatypes.Any)this.argsBuilder_.getMessage(index);
      }
      
      public Builder setArgs(int index, MysqlxDatatypes.Any value) {
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
      
      public Builder setArgs(int index, MysqlxDatatypes.Any.Builder builderForValue) {
        if (this.argsBuilder_ == null) {
          ensureArgsIsMutable();
          this.args_.set(index, builderForValue.build());
          onChanged();
        } else {
          this.argsBuilder_.setMessage(index, (AbstractMessage)builderForValue.build());
        } 
        return this;
      }
      
      public Builder addArgs(MysqlxDatatypes.Any value) {
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
      
      public Builder addArgs(int index, MysqlxDatatypes.Any value) {
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
      
      public Builder addArgs(MysqlxDatatypes.Any.Builder builderForValue) {
        if (this.argsBuilder_ == null) {
          ensureArgsIsMutable();
          this.args_.add(builderForValue.build());
          onChanged();
        } else {
          this.argsBuilder_.addMessage((AbstractMessage)builderForValue.build());
        } 
        return this;
      }
      
      public Builder addArgs(int index, MysqlxDatatypes.Any.Builder builderForValue) {
        if (this.argsBuilder_ == null) {
          ensureArgsIsMutable();
          this.args_.add(index, builderForValue.build());
          onChanged();
        } else {
          this.argsBuilder_.addMessage(index, (AbstractMessage)builderForValue.build());
        } 
        return this;
      }
      
      public Builder addAllArgs(Iterable<? extends MysqlxDatatypes.Any> values) {
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
          this.bitField0_ &= 0xFFFFFFFB;
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
      
      public MysqlxDatatypes.Any.Builder getArgsBuilder(int index) {
        return (MysqlxDatatypes.Any.Builder)getArgsFieldBuilder().getBuilder(index);
      }
      
      public MysqlxDatatypes.AnyOrBuilder getArgsOrBuilder(int index) {
        if (this.argsBuilder_ == null)
          return this.args_.get(index); 
        return (MysqlxDatatypes.AnyOrBuilder)this.argsBuilder_.getMessageOrBuilder(index);
      }
      
      public List<? extends MysqlxDatatypes.AnyOrBuilder> getArgsOrBuilderList() {
        if (this.argsBuilder_ != null)
          return this.argsBuilder_.getMessageOrBuilderList(); 
        return Collections.unmodifiableList((List)this.args_);
      }
      
      public MysqlxDatatypes.Any.Builder addArgsBuilder() {
        return (MysqlxDatatypes.Any.Builder)getArgsFieldBuilder().addBuilder(
            (AbstractMessage)MysqlxDatatypes.Any.getDefaultInstance());
      }
      
      public MysqlxDatatypes.Any.Builder addArgsBuilder(int index) {
        return (MysqlxDatatypes.Any.Builder)getArgsFieldBuilder().addBuilder(index, 
            (AbstractMessage)MysqlxDatatypes.Any.getDefaultInstance());
      }
      
      public List<MysqlxDatatypes.Any.Builder> getArgsBuilderList() {
        return getArgsFieldBuilder().getBuilderList();
      }
      
      private RepeatedFieldBuilderV3<MysqlxDatatypes.Any, MysqlxDatatypes.Any.Builder, MysqlxDatatypes.AnyOrBuilder> getArgsFieldBuilder() {
        if (this.argsBuilder_ == null) {
          this
            
            .argsBuilder_ = new RepeatedFieldBuilderV3(this.args_, ((this.bitField0_ & 0x4) != 0), (AbstractMessage.BuilderParent)getParentForChildren(), isClean());
          this.args_ = null;
        } 
        return this.argsBuilder_;
      }
      
      public boolean hasCompactMetadata() {
        return ((this.bitField0_ & 0x8) != 0);
      }
      
      public boolean getCompactMetadata() {
        return this.compactMetadata_;
      }
      
      public Builder setCompactMetadata(boolean value) {
        this.bitField0_ |= 0x8;
        this.compactMetadata_ = value;
        onChanged();
        return this;
      }
      
      public Builder clearCompactMetadata() {
        this.bitField0_ &= 0xFFFFFFF7;
        this.compactMetadata_ = false;
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
    
    private static final StmtExecute DEFAULT_INSTANCE = new StmtExecute();
    
    public static StmtExecute getDefaultInstance() {
      return DEFAULT_INSTANCE;
    }
    
    @Deprecated
    public static final Parser<StmtExecute> PARSER = (Parser<StmtExecute>)new AbstractParser<StmtExecute>() {
        public MysqlxSql.StmtExecute parsePartialFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
          MysqlxSql.StmtExecute.Builder builder = MysqlxSql.StmtExecute.newBuilder();
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
    
    public static Parser<StmtExecute> parser() {
      return PARSER;
    }
    
    public Parser<StmtExecute> getParserForType() {
      return PARSER;
    }
    
    public StmtExecute getDefaultInstanceForType() {
      return DEFAULT_INSTANCE;
    }
  }
  
  public static interface StmtExecuteOkOrBuilder extends MessageOrBuilder {}
  
  public static final class StmtExecuteOk extends GeneratedMessageV3 implements StmtExecuteOkOrBuilder {
    private static final long serialVersionUID = 0L;
    
    private byte memoizedIsInitialized;
    
    private StmtExecuteOk(GeneratedMessageV3.Builder<?> builder) {
      super(builder);
      this.memoizedIsInitialized = -1;
    }
    
    private StmtExecuteOk() {
      this.memoizedIsInitialized = -1;
    }
    
    protected Object newInstance(GeneratedMessageV3.UnusedPrivateParameter unused) {
      return new StmtExecuteOk();
    }
    
    public final UnknownFieldSet getUnknownFields() {
      return this.unknownFields;
    }
    
    public static final Descriptors.Descriptor getDescriptor() {
      return MysqlxSql.internal_static_Mysqlx_Sql_StmtExecuteOk_descriptor;
    }
    
    protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() {
      return MysqlxSql.internal_static_Mysqlx_Sql_StmtExecuteOk_fieldAccessorTable.ensureFieldAccessorsInitialized(StmtExecuteOk.class, Builder.class);
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
      if (!(obj instanceof StmtExecuteOk))
        return super.equals(obj); 
      StmtExecuteOk other = (StmtExecuteOk)obj;
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
    
    public static StmtExecuteOk parseFrom(ByteBuffer data) throws InvalidProtocolBufferException {
      return (StmtExecuteOk)PARSER.parseFrom(data);
    }
    
    public static StmtExecuteOk parseFrom(ByteBuffer data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
      return (StmtExecuteOk)PARSER.parseFrom(data, extensionRegistry);
    }
    
    public static StmtExecuteOk parseFrom(ByteString data) throws InvalidProtocolBufferException {
      return (StmtExecuteOk)PARSER.parseFrom(data);
    }
    
    public static StmtExecuteOk parseFrom(ByteString data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
      return (StmtExecuteOk)PARSER.parseFrom(data, extensionRegistry);
    }
    
    public static StmtExecuteOk parseFrom(byte[] data) throws InvalidProtocolBufferException {
      return (StmtExecuteOk)PARSER.parseFrom(data);
    }
    
    public static StmtExecuteOk parseFrom(byte[] data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
      return (StmtExecuteOk)PARSER.parseFrom(data, extensionRegistry);
    }
    
    public static StmtExecuteOk parseFrom(InputStream input) throws IOException {
      return 
        (StmtExecuteOk)GeneratedMessageV3.parseWithIOException(PARSER, input);
    }
    
    public static StmtExecuteOk parseFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
      return 
        (StmtExecuteOk)GeneratedMessageV3.parseWithIOException(PARSER, input, extensionRegistry);
    }
    
    public static StmtExecuteOk parseDelimitedFrom(InputStream input) throws IOException {
      return 
        (StmtExecuteOk)GeneratedMessageV3.parseDelimitedWithIOException(PARSER, input);
    }
    
    public static StmtExecuteOk parseDelimitedFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
      return 
        (StmtExecuteOk)GeneratedMessageV3.parseDelimitedWithIOException(PARSER, input, extensionRegistry);
    }
    
    public static StmtExecuteOk parseFrom(CodedInputStream input) throws IOException {
      return 
        (StmtExecuteOk)GeneratedMessageV3.parseWithIOException(PARSER, input);
    }
    
    public static StmtExecuteOk parseFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
      return 
        (StmtExecuteOk)GeneratedMessageV3.parseWithIOException(PARSER, input, extensionRegistry);
    }
    
    public Builder newBuilderForType() {
      return newBuilder();
    }
    
    public static Builder newBuilder() {
      return DEFAULT_INSTANCE.toBuilder();
    }
    
    public static Builder newBuilder(StmtExecuteOk prototype) {
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
    
    public static final class Builder extends GeneratedMessageV3.Builder<Builder> implements MysqlxSql.StmtExecuteOkOrBuilder {
      public static final Descriptors.Descriptor getDescriptor() {
        return MysqlxSql.internal_static_Mysqlx_Sql_StmtExecuteOk_descriptor;
      }
      
      protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() {
        return MysqlxSql.internal_static_Mysqlx_Sql_StmtExecuteOk_fieldAccessorTable
          .ensureFieldAccessorsInitialized(MysqlxSql.StmtExecuteOk.class, Builder.class);
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
        return MysqlxSql.internal_static_Mysqlx_Sql_StmtExecuteOk_descriptor;
      }
      
      public MysqlxSql.StmtExecuteOk getDefaultInstanceForType() {
        return MysqlxSql.StmtExecuteOk.getDefaultInstance();
      }
      
      public MysqlxSql.StmtExecuteOk build() {
        MysqlxSql.StmtExecuteOk result = buildPartial();
        if (!result.isInitialized())
          throw newUninitializedMessageException(result); 
        return result;
      }
      
      public MysqlxSql.StmtExecuteOk buildPartial() {
        MysqlxSql.StmtExecuteOk result = new MysqlxSql.StmtExecuteOk(this);
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
        if (other instanceof MysqlxSql.StmtExecuteOk)
          return mergeFrom((MysqlxSql.StmtExecuteOk)other); 
        super.mergeFrom(other);
        return this;
      }
      
      public Builder mergeFrom(MysqlxSql.StmtExecuteOk other) {
        if (other == MysqlxSql.StmtExecuteOk.getDefaultInstance())
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
    
    private static final StmtExecuteOk DEFAULT_INSTANCE = new StmtExecuteOk();
    
    public static StmtExecuteOk getDefaultInstance() {
      return DEFAULT_INSTANCE;
    }
    
    @Deprecated
    public static final Parser<StmtExecuteOk> PARSER = (Parser<StmtExecuteOk>)new AbstractParser<StmtExecuteOk>() {
        public MysqlxSql.StmtExecuteOk parsePartialFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
          MysqlxSql.StmtExecuteOk.Builder builder = MysqlxSql.StmtExecuteOk.newBuilder();
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
    
    public static Parser<StmtExecuteOk> parser() {
      return PARSER;
    }
    
    public Parser<StmtExecuteOk> getParserForType() {
      return PARSER;
    }
    
    public StmtExecuteOk getDefaultInstanceForType() {
      return DEFAULT_INSTANCE;
    }
  }
  
  public static Descriptors.FileDescriptor getDescriptor() {
    return descriptor;
  }
  
  static {
    String[] descriptorData = { "\n\020mysqlx_sql.proto\022\nMysqlx.Sql\032\fmysqlx.proto\032\026mysqlx_datatypes.proto\"\n\013StmtExecute\022\026\n\tnamespace\030\003 \001(\t:\003sql\022\f\n\004stmt\030\001 \002(\f\022#\n\004args\030\002 \003(\0132\025.Mysqlx.Datatypes.Any\022\037\n\020compact_metadata\030\004 \001(\b:\005false:\004ê0\f\"\025\n\rStmtExecuteOk:\004ê0\021B\031\n\027com.mysql.cj.x.protobuf" };
    descriptor = Descriptors.FileDescriptor.internalBuildGeneratedFileFrom(descriptorData, new Descriptors.FileDescriptor[] { Mysqlx.getDescriptor(), 
          MysqlxDatatypes.getDescriptor() });
    internal_static_Mysqlx_Sql_StmtExecute_descriptor = getDescriptor().getMessageTypes().get(0);
    internal_static_Mysqlx_Sql_StmtExecute_fieldAccessorTable = new GeneratedMessageV3.FieldAccessorTable(internal_static_Mysqlx_Sql_StmtExecute_descriptor, new String[] { "Namespace", "Stmt", "Args", "CompactMetadata" });
    internal_static_Mysqlx_Sql_StmtExecuteOk_descriptor = getDescriptor().getMessageTypes().get(1);
    internal_static_Mysqlx_Sql_StmtExecuteOk_fieldAccessorTable = new GeneratedMessageV3.FieldAccessorTable(internal_static_Mysqlx_Sql_StmtExecuteOk_descriptor, new String[0]);
    ExtensionRegistry registry = ExtensionRegistry.newInstance();
    registry.add(Mysqlx.clientMessageId);
    registry.add(Mysqlx.serverMessageId);
    Descriptors.FileDescriptor.internalUpdateFileDescriptor(descriptor, registry);
    Mysqlx.getDescriptor();
    MysqlxDatatypes.getDescriptor();
  }
}
