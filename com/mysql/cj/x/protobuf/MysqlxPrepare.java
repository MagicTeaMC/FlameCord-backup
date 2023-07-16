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

public final class MysqlxPrepare {
  private static final Descriptors.Descriptor internal_static_Mysqlx_Prepare_Prepare_descriptor;
  
  private static final GeneratedMessageV3.FieldAccessorTable internal_static_Mysqlx_Prepare_Prepare_fieldAccessorTable;
  
  private static final Descriptors.Descriptor internal_static_Mysqlx_Prepare_Prepare_OneOfMessage_descriptor;
  
  private static final GeneratedMessageV3.FieldAccessorTable internal_static_Mysqlx_Prepare_Prepare_OneOfMessage_fieldAccessorTable;
  
  private static final Descriptors.Descriptor internal_static_Mysqlx_Prepare_Execute_descriptor;
  
  private static final GeneratedMessageV3.FieldAccessorTable internal_static_Mysqlx_Prepare_Execute_fieldAccessorTable;
  
  private static final Descriptors.Descriptor internal_static_Mysqlx_Prepare_Deallocate_descriptor;
  
  private static final GeneratedMessageV3.FieldAccessorTable internal_static_Mysqlx_Prepare_Deallocate_fieldAccessorTable;
  
  private static Descriptors.FileDescriptor descriptor;
  
  public static void registerAllExtensions(ExtensionRegistryLite registry) {}
  
  public static void registerAllExtensions(ExtensionRegistry registry) {
    registerAllExtensions((ExtensionRegistryLite)registry);
  }
  
  public static interface PrepareOrBuilder extends MessageOrBuilder {
    boolean hasStmtId();
    
    int getStmtId();
    
    boolean hasStmt();
    
    MysqlxPrepare.Prepare.OneOfMessage getStmt();
    
    MysqlxPrepare.Prepare.OneOfMessageOrBuilder getStmtOrBuilder();
  }
  
  public static final class Prepare extends GeneratedMessageV3 implements PrepareOrBuilder {
    private static final long serialVersionUID = 0L;
    
    private int bitField0_;
    
    public static final int STMT_ID_FIELD_NUMBER = 1;
    
    private int stmtId_;
    
    public static final int STMT_FIELD_NUMBER = 2;
    
    private OneOfMessage stmt_;
    
    private byte memoizedIsInitialized;
    
    private Prepare(GeneratedMessageV3.Builder<?> builder) {
      super(builder);
      this.memoizedIsInitialized = -1;
    }
    
    private Prepare() {
      this.memoizedIsInitialized = -1;
    }
    
    protected Object newInstance(GeneratedMessageV3.UnusedPrivateParameter unused) {
      return new Prepare();
    }
    
    public final UnknownFieldSet getUnknownFields() {
      return this.unknownFields;
    }
    
    public static final Descriptors.Descriptor getDescriptor() {
      return MysqlxPrepare.internal_static_Mysqlx_Prepare_Prepare_descriptor;
    }
    
    protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() {
      return MysqlxPrepare.internal_static_Mysqlx_Prepare_Prepare_fieldAccessorTable.ensureFieldAccessorsInitialized(Prepare.class, Builder.class);
    }
    
    public static final class OneOfMessage extends GeneratedMessageV3 implements OneOfMessageOrBuilder {
      private static final long serialVersionUID = 0L;
      
      private int bitField0_;
      
      public static final int TYPE_FIELD_NUMBER = 1;
      
      private int type_;
      
      public static final int FIND_FIELD_NUMBER = 2;
      
      private MysqlxCrud.Find find_;
      
      public static final int INSERT_FIELD_NUMBER = 3;
      
      private MysqlxCrud.Insert insert_;
      
      public static final int UPDATE_FIELD_NUMBER = 4;
      
      private MysqlxCrud.Update update_;
      
      public static final int DELETE_FIELD_NUMBER = 5;
      
      private MysqlxCrud.Delete delete_;
      
      public static final int STMT_EXECUTE_FIELD_NUMBER = 6;
      
      private MysqlxSql.StmtExecute stmtExecute_;
      
      private byte memoizedIsInitialized;
      
      private OneOfMessage(GeneratedMessageV3.Builder<?> builder) {
        super(builder);
        this.memoizedIsInitialized = -1;
      }
      
      private OneOfMessage() {
        this.memoizedIsInitialized = -1;
        this.type_ = 0;
      }
      
      protected Object newInstance(GeneratedMessageV3.UnusedPrivateParameter unused) {
        return new OneOfMessage();
      }
      
      public final UnknownFieldSet getUnknownFields() {
        return this.unknownFields;
      }
      
      public static final Descriptors.Descriptor getDescriptor() {
        return MysqlxPrepare.internal_static_Mysqlx_Prepare_Prepare_OneOfMessage_descriptor;
      }
      
      protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() {
        return MysqlxPrepare.internal_static_Mysqlx_Prepare_Prepare_OneOfMessage_fieldAccessorTable.ensureFieldAccessorsInitialized(OneOfMessage.class, Builder.class);
      }
      
      public enum Type implements ProtocolMessageEnum {
        FIND(0),
        INSERT(1),
        UPDATE(2),
        DELETE(4),
        STMT(5);
        
        public static final int FIND_VALUE = 0;
        
        public static final int INSERT_VALUE = 1;
        
        public static final int UPDATE_VALUE = 2;
        
        public static final int DELETE_VALUE = 4;
        
        public static final int STMT_VALUE = 5;
        
        private static final Internal.EnumLiteMap<Type> internalValueMap = new Internal.EnumLiteMap<Type>() {
            public MysqlxPrepare.Prepare.OneOfMessage.Type findValueByNumber(int number) {
              return MysqlxPrepare.Prepare.OneOfMessage.Type.forNumber(number);
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
              return FIND;
            case 1:
              return INSERT;
            case 2:
              return UPDATE;
            case 4:
              return DELETE;
            case 5:
              return STMT;
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
          return MysqlxPrepare.Prepare.OneOfMessage.getDescriptor().getEnumTypes().get(0);
        }
        
        Type(int value) {
          this.value = value;
        }
      }
      
      public boolean hasType() {
        return ((this.bitField0_ & 0x1) != 0);
      }
      
      public Type getType() {
        Type result = Type.valueOf(this.type_);
        return (result == null) ? Type.FIND : result;
      }
      
      public boolean hasFind() {
        return ((this.bitField0_ & 0x2) != 0);
      }
      
      public MysqlxCrud.Find getFind() {
        return (this.find_ == null) ? MysqlxCrud.Find.getDefaultInstance() : this.find_;
      }
      
      public MysqlxCrud.FindOrBuilder getFindOrBuilder() {
        return (this.find_ == null) ? MysqlxCrud.Find.getDefaultInstance() : this.find_;
      }
      
      public boolean hasInsert() {
        return ((this.bitField0_ & 0x4) != 0);
      }
      
      public MysqlxCrud.Insert getInsert() {
        return (this.insert_ == null) ? MysqlxCrud.Insert.getDefaultInstance() : this.insert_;
      }
      
      public MysqlxCrud.InsertOrBuilder getInsertOrBuilder() {
        return (this.insert_ == null) ? MysqlxCrud.Insert.getDefaultInstance() : this.insert_;
      }
      
      public boolean hasUpdate() {
        return ((this.bitField0_ & 0x8) != 0);
      }
      
      public MysqlxCrud.Update getUpdate() {
        return (this.update_ == null) ? MysqlxCrud.Update.getDefaultInstance() : this.update_;
      }
      
      public MysqlxCrud.UpdateOrBuilder getUpdateOrBuilder() {
        return (this.update_ == null) ? MysqlxCrud.Update.getDefaultInstance() : this.update_;
      }
      
      public boolean hasDelete() {
        return ((this.bitField0_ & 0x10) != 0);
      }
      
      public MysqlxCrud.Delete getDelete() {
        return (this.delete_ == null) ? MysqlxCrud.Delete.getDefaultInstance() : this.delete_;
      }
      
      public MysqlxCrud.DeleteOrBuilder getDeleteOrBuilder() {
        return (this.delete_ == null) ? MysqlxCrud.Delete.getDefaultInstance() : this.delete_;
      }
      
      public boolean hasStmtExecute() {
        return ((this.bitField0_ & 0x20) != 0);
      }
      
      public MysqlxSql.StmtExecute getStmtExecute() {
        return (this.stmtExecute_ == null) ? MysqlxSql.StmtExecute.getDefaultInstance() : this.stmtExecute_;
      }
      
      public MysqlxSql.StmtExecuteOrBuilder getStmtExecuteOrBuilder() {
        return (this.stmtExecute_ == null) ? MysqlxSql.StmtExecute.getDefaultInstance() : this.stmtExecute_;
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
        if (hasFind() && !getFind().isInitialized()) {
          this.memoizedIsInitialized = 0;
          return false;
        } 
        if (hasInsert() && !getInsert().isInitialized()) {
          this.memoizedIsInitialized = 0;
          return false;
        } 
        if (hasUpdate() && !getUpdate().isInitialized()) {
          this.memoizedIsInitialized = 0;
          return false;
        } 
        if (hasDelete() && !getDelete().isInitialized()) {
          this.memoizedIsInitialized = 0;
          return false;
        } 
        if (hasStmtExecute() && !getStmtExecute().isInitialized()) {
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
          output.writeMessage(2, (MessageLite)getFind()); 
        if ((this.bitField0_ & 0x4) != 0)
          output.writeMessage(3, (MessageLite)getInsert()); 
        if ((this.bitField0_ & 0x8) != 0)
          output.writeMessage(4, (MessageLite)getUpdate()); 
        if ((this.bitField0_ & 0x10) != 0)
          output.writeMessage(5, (MessageLite)getDelete()); 
        if ((this.bitField0_ & 0x20) != 0)
          output.writeMessage(6, (MessageLite)getStmtExecute()); 
        getUnknownFields().writeTo(output);
      }
      
      public int getSerializedSize() {
        int size = this.memoizedSize;
        if (size != -1)
          return size; 
        size = 0;
        if ((this.bitField0_ & 0x1) != 0)
          size += CodedOutputStream.computeEnumSize(1, this.type_); 
        if ((this.bitField0_ & 0x2) != 0)
          size += CodedOutputStream.computeMessageSize(2, (MessageLite)getFind()); 
        if ((this.bitField0_ & 0x4) != 0)
          size += CodedOutputStream.computeMessageSize(3, (MessageLite)getInsert()); 
        if ((this.bitField0_ & 0x8) != 0)
          size += CodedOutputStream.computeMessageSize(4, (MessageLite)getUpdate()); 
        if ((this.bitField0_ & 0x10) != 0)
          size += CodedOutputStream.computeMessageSize(5, (MessageLite)getDelete()); 
        if ((this.bitField0_ & 0x20) != 0)
          size += CodedOutputStream.computeMessageSize(6, (MessageLite)getStmtExecute()); 
        size += getUnknownFields().getSerializedSize();
        this.memoizedSize = size;
        return size;
      }
      
      public boolean equals(Object obj) {
        if (obj == this)
          return true; 
        if (!(obj instanceof OneOfMessage))
          return super.equals(obj); 
        OneOfMessage other = (OneOfMessage)obj;
        if (hasType() != other.hasType())
          return false; 
        if (hasType() && this.type_ != other.type_)
          return false; 
        if (hasFind() != other.hasFind())
          return false; 
        if (hasFind() && !getFind().equals(other.getFind()))
          return false; 
        if (hasInsert() != other.hasInsert())
          return false; 
        if (hasInsert() && !getInsert().equals(other.getInsert()))
          return false; 
        if (hasUpdate() != other.hasUpdate())
          return false; 
        if (hasUpdate() && !getUpdate().equals(other.getUpdate()))
          return false; 
        if (hasDelete() != other.hasDelete())
          return false; 
        if (hasDelete() && !getDelete().equals(other.getDelete()))
          return false; 
        if (hasStmtExecute() != other.hasStmtExecute())
          return false; 
        if (hasStmtExecute() && !getStmtExecute().equals(other.getStmtExecute()))
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
        if (hasFind()) {
          hash = 37 * hash + 2;
          hash = 53 * hash + getFind().hashCode();
        } 
        if (hasInsert()) {
          hash = 37 * hash + 3;
          hash = 53 * hash + getInsert().hashCode();
        } 
        if (hasUpdate()) {
          hash = 37 * hash + 4;
          hash = 53 * hash + getUpdate().hashCode();
        } 
        if (hasDelete()) {
          hash = 37 * hash + 5;
          hash = 53 * hash + getDelete().hashCode();
        } 
        if (hasStmtExecute()) {
          hash = 37 * hash + 6;
          hash = 53 * hash + getStmtExecute().hashCode();
        } 
        hash = 29 * hash + getUnknownFields().hashCode();
        this.memoizedHashCode = hash;
        return hash;
      }
      
      public static OneOfMessage parseFrom(ByteBuffer data) throws InvalidProtocolBufferException {
        return (OneOfMessage)PARSER.parseFrom(data);
      }
      
      public static OneOfMessage parseFrom(ByteBuffer data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
        return (OneOfMessage)PARSER.parseFrom(data, extensionRegistry);
      }
      
      public static OneOfMessage parseFrom(ByteString data) throws InvalidProtocolBufferException {
        return (OneOfMessage)PARSER.parseFrom(data);
      }
      
      public static OneOfMessage parseFrom(ByteString data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
        return (OneOfMessage)PARSER.parseFrom(data, extensionRegistry);
      }
      
      public static OneOfMessage parseFrom(byte[] data) throws InvalidProtocolBufferException {
        return (OneOfMessage)PARSER.parseFrom(data);
      }
      
      public static OneOfMessage parseFrom(byte[] data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
        return (OneOfMessage)PARSER.parseFrom(data, extensionRegistry);
      }
      
      public static OneOfMessage parseFrom(InputStream input) throws IOException {
        return (OneOfMessage)GeneratedMessageV3.parseWithIOException(PARSER, input);
      }
      
      public static OneOfMessage parseFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
        return (OneOfMessage)GeneratedMessageV3.parseWithIOException(PARSER, input, extensionRegistry);
      }
      
      public static OneOfMessage parseDelimitedFrom(InputStream input) throws IOException {
        return (OneOfMessage)GeneratedMessageV3.parseDelimitedWithIOException(PARSER, input);
      }
      
      public static OneOfMessage parseDelimitedFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
        return (OneOfMessage)GeneratedMessageV3.parseDelimitedWithIOException(PARSER, input, extensionRegistry);
      }
      
      public static OneOfMessage parseFrom(CodedInputStream input) throws IOException {
        return (OneOfMessage)GeneratedMessageV3.parseWithIOException(PARSER, input);
      }
      
      public static OneOfMessage parseFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
        return (OneOfMessage)GeneratedMessageV3.parseWithIOException(PARSER, input, extensionRegistry);
      }
      
      public Builder newBuilderForType() {
        return newBuilder();
      }
      
      public static Builder newBuilder() {
        return DEFAULT_INSTANCE.toBuilder();
      }
      
      public static Builder newBuilder(OneOfMessage prototype) {
        return DEFAULT_INSTANCE.toBuilder().mergeFrom(prototype);
      }
      
      public Builder toBuilder() {
        return (this == DEFAULT_INSTANCE) ? new Builder() : (new Builder()).mergeFrom(this);
      }
      
      protected Builder newBuilderForType(GeneratedMessageV3.BuilderParent parent) {
        Builder builder = new Builder(parent);
        return builder;
      }
      
      public static final class Builder extends GeneratedMessageV3.Builder<Builder> implements MysqlxPrepare.Prepare.OneOfMessageOrBuilder {
        private int bitField0_;
        
        private int type_;
        
        private MysqlxCrud.Find find_;
        
        private SingleFieldBuilderV3<MysqlxCrud.Find, MysqlxCrud.Find.Builder, MysqlxCrud.FindOrBuilder> findBuilder_;
        
        private MysqlxCrud.Insert insert_;
        
        private SingleFieldBuilderV3<MysqlxCrud.Insert, MysqlxCrud.Insert.Builder, MysqlxCrud.InsertOrBuilder> insertBuilder_;
        
        private MysqlxCrud.Update update_;
        
        private SingleFieldBuilderV3<MysqlxCrud.Update, MysqlxCrud.Update.Builder, MysqlxCrud.UpdateOrBuilder> updateBuilder_;
        
        private MysqlxCrud.Delete delete_;
        
        private SingleFieldBuilderV3<MysqlxCrud.Delete, MysqlxCrud.Delete.Builder, MysqlxCrud.DeleteOrBuilder> deleteBuilder_;
        
        private MysqlxSql.StmtExecute stmtExecute_;
        
        private SingleFieldBuilderV3<MysqlxSql.StmtExecute, MysqlxSql.StmtExecute.Builder, MysqlxSql.StmtExecuteOrBuilder> stmtExecuteBuilder_;
        
        public static final Descriptors.Descriptor getDescriptor() {
          return MysqlxPrepare.internal_static_Mysqlx_Prepare_Prepare_OneOfMessage_descriptor;
        }
        
        protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() {
          return MysqlxPrepare.internal_static_Mysqlx_Prepare_Prepare_OneOfMessage_fieldAccessorTable.ensureFieldAccessorsInitialized(MysqlxPrepare.Prepare.OneOfMessage.class, Builder.class);
        }
        
        private Builder() {
          this.type_ = 0;
          maybeForceBuilderInitialization();
        }
        
        private Builder(GeneratedMessageV3.BuilderParent parent) {
          super(parent);
          this.type_ = 0;
          maybeForceBuilderInitialization();
        }
        
        private void maybeForceBuilderInitialization() {
          if (MysqlxPrepare.Prepare.OneOfMessage.alwaysUseFieldBuilders) {
            getFindFieldBuilder();
            getInsertFieldBuilder();
            getUpdateFieldBuilder();
            getDeleteFieldBuilder();
            getStmtExecuteFieldBuilder();
          } 
        }
        
        public Builder clear() {
          super.clear();
          this.type_ = 0;
          this.bitField0_ &= 0xFFFFFFFE;
          if (this.findBuilder_ == null) {
            this.find_ = null;
          } else {
            this.findBuilder_.clear();
          } 
          this.bitField0_ &= 0xFFFFFFFD;
          if (this.insertBuilder_ == null) {
            this.insert_ = null;
          } else {
            this.insertBuilder_.clear();
          } 
          this.bitField0_ &= 0xFFFFFFFB;
          if (this.updateBuilder_ == null) {
            this.update_ = null;
          } else {
            this.updateBuilder_.clear();
          } 
          this.bitField0_ &= 0xFFFFFFF7;
          if (this.deleteBuilder_ == null) {
            this.delete_ = null;
          } else {
            this.deleteBuilder_.clear();
          } 
          this.bitField0_ &= 0xFFFFFFEF;
          if (this.stmtExecuteBuilder_ == null) {
            this.stmtExecute_ = null;
          } else {
            this.stmtExecuteBuilder_.clear();
          } 
          this.bitField0_ &= 0xFFFFFFDF;
          return this;
        }
        
        public Descriptors.Descriptor getDescriptorForType() {
          return MysqlxPrepare.internal_static_Mysqlx_Prepare_Prepare_OneOfMessage_descriptor;
        }
        
        public MysqlxPrepare.Prepare.OneOfMessage getDefaultInstanceForType() {
          return MysqlxPrepare.Prepare.OneOfMessage.getDefaultInstance();
        }
        
        public MysqlxPrepare.Prepare.OneOfMessage build() {
          MysqlxPrepare.Prepare.OneOfMessage result = buildPartial();
          if (!result.isInitialized())
            throw newUninitializedMessageException(result); 
          return result;
        }
        
        public MysqlxPrepare.Prepare.OneOfMessage buildPartial() {
          MysqlxPrepare.Prepare.OneOfMessage result = new MysqlxPrepare.Prepare.OneOfMessage(this);
          int from_bitField0_ = this.bitField0_;
          int to_bitField0_ = 0;
          if ((from_bitField0_ & 0x1) != 0)
            to_bitField0_ |= 0x1; 
          result.type_ = this.type_;
          if ((from_bitField0_ & 0x2) != 0) {
            if (this.findBuilder_ == null) {
              result.find_ = this.find_;
            } else {
              result.find_ = (MysqlxCrud.Find)this.findBuilder_.build();
            } 
            to_bitField0_ |= 0x2;
          } 
          if ((from_bitField0_ & 0x4) != 0) {
            if (this.insertBuilder_ == null) {
              result.insert_ = this.insert_;
            } else {
              result.insert_ = (MysqlxCrud.Insert)this.insertBuilder_.build();
            } 
            to_bitField0_ |= 0x4;
          } 
          if ((from_bitField0_ & 0x8) != 0) {
            if (this.updateBuilder_ == null) {
              result.update_ = this.update_;
            } else {
              result.update_ = (MysqlxCrud.Update)this.updateBuilder_.build();
            } 
            to_bitField0_ |= 0x8;
          } 
          if ((from_bitField0_ & 0x10) != 0) {
            if (this.deleteBuilder_ == null) {
              result.delete_ = this.delete_;
            } else {
              result.delete_ = (MysqlxCrud.Delete)this.deleteBuilder_.build();
            } 
            to_bitField0_ |= 0x10;
          } 
          if ((from_bitField0_ & 0x20) != 0) {
            if (this.stmtExecuteBuilder_ == null) {
              result.stmtExecute_ = this.stmtExecute_;
            } else {
              result.stmtExecute_ = (MysqlxSql.StmtExecute)this.stmtExecuteBuilder_.build();
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
          if (other instanceof MysqlxPrepare.Prepare.OneOfMessage)
            return mergeFrom((MysqlxPrepare.Prepare.OneOfMessage)other); 
          super.mergeFrom(other);
          return this;
        }
        
        public Builder mergeFrom(MysqlxPrepare.Prepare.OneOfMessage other) {
          if (other == MysqlxPrepare.Prepare.OneOfMessage.getDefaultInstance())
            return this; 
          if (other.hasType())
            setType(other.getType()); 
          if (other.hasFind())
            mergeFind(other.getFind()); 
          if (other.hasInsert())
            mergeInsert(other.getInsert()); 
          if (other.hasUpdate())
            mergeUpdate(other.getUpdate()); 
          if (other.hasDelete())
            mergeDelete(other.getDelete()); 
          if (other.hasStmtExecute())
            mergeStmtExecute(other.getStmtExecute()); 
          mergeUnknownFields(other.getUnknownFields());
          onChanged();
          return this;
        }
        
        public final boolean isInitialized() {
          if (!hasType())
            return false; 
          if (hasFind() && !getFind().isInitialized())
            return false; 
          if (hasInsert() && !getInsert().isInitialized())
            return false; 
          if (hasUpdate() && !getUpdate().isInitialized())
            return false; 
          if (hasDelete() && !getDelete().isInitialized())
            return false; 
          if (hasStmtExecute() && !getStmtExecute().isInitialized())
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
              MysqlxPrepare.Prepare.OneOfMessage.Type tmpValue;
              int tag = input.readTag();
              switch (tag) {
                case 0:
                  done = true;
                  continue;
                case 8:
                  tmpRaw = input.readEnum();
                  tmpValue = MysqlxPrepare.Prepare.OneOfMessage.Type.forNumber(tmpRaw);
                  if (tmpValue == null) {
                    mergeUnknownVarintField(1, tmpRaw);
                    continue;
                  } 
                  this.type_ = tmpRaw;
                  this.bitField0_ |= 0x1;
                  continue;
                case 18:
                  input.readMessage((MessageLite.Builder)getFindFieldBuilder().getBuilder(), extensionRegistry);
                  this.bitField0_ |= 0x2;
                  continue;
                case 26:
                  input.readMessage((MessageLite.Builder)getInsertFieldBuilder().getBuilder(), extensionRegistry);
                  this.bitField0_ |= 0x4;
                  continue;
                case 34:
                  input.readMessage((MessageLite.Builder)getUpdateFieldBuilder().getBuilder(), extensionRegistry);
                  this.bitField0_ |= 0x8;
                  continue;
                case 42:
                  input.readMessage((MessageLite.Builder)getDeleteFieldBuilder().getBuilder(), extensionRegistry);
                  this.bitField0_ |= 0x10;
                  continue;
                case 50:
                  input.readMessage((MessageLite.Builder)getStmtExecuteFieldBuilder().getBuilder(), extensionRegistry);
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
        
        public boolean hasType() {
          return ((this.bitField0_ & 0x1) != 0);
        }
        
        public MysqlxPrepare.Prepare.OneOfMessage.Type getType() {
          MysqlxPrepare.Prepare.OneOfMessage.Type result = MysqlxPrepare.Prepare.OneOfMessage.Type.valueOf(this.type_);
          return (result == null) ? MysqlxPrepare.Prepare.OneOfMessage.Type.FIND : result;
        }
        
        public Builder setType(MysqlxPrepare.Prepare.OneOfMessage.Type value) {
          if (value == null)
            throw new NullPointerException(); 
          this.bitField0_ |= 0x1;
          this.type_ = value.getNumber();
          onChanged();
          return this;
        }
        
        public Builder clearType() {
          this.bitField0_ &= 0xFFFFFFFE;
          this.type_ = 0;
          onChanged();
          return this;
        }
        
        public boolean hasFind() {
          return ((this.bitField0_ & 0x2) != 0);
        }
        
        public MysqlxCrud.Find getFind() {
          if (this.findBuilder_ == null)
            return (this.find_ == null) ? MysqlxCrud.Find.getDefaultInstance() : this.find_; 
          return (MysqlxCrud.Find)this.findBuilder_.getMessage();
        }
        
        public Builder setFind(MysqlxCrud.Find value) {
          if (this.findBuilder_ == null) {
            if (value == null)
              throw new NullPointerException(); 
            this.find_ = value;
            onChanged();
          } else {
            this.findBuilder_.setMessage((AbstractMessage)value);
          } 
          this.bitField0_ |= 0x2;
          return this;
        }
        
        public Builder setFind(MysqlxCrud.Find.Builder builderForValue) {
          if (this.findBuilder_ == null) {
            this.find_ = builderForValue.build();
            onChanged();
          } else {
            this.findBuilder_.setMessage((AbstractMessage)builderForValue.build());
          } 
          this.bitField0_ |= 0x2;
          return this;
        }
        
        public Builder mergeFind(MysqlxCrud.Find value) {
          if (this.findBuilder_ == null) {
            if ((this.bitField0_ & 0x2) != 0 && this.find_ != null && this.find_ != MysqlxCrud.Find.getDefaultInstance()) {
              this.find_ = MysqlxCrud.Find.newBuilder(this.find_).mergeFrom(value).buildPartial();
            } else {
              this.find_ = value;
            } 
            onChanged();
          } else {
            this.findBuilder_.mergeFrom((AbstractMessage)value);
          } 
          this.bitField0_ |= 0x2;
          return this;
        }
        
        public Builder clearFind() {
          if (this.findBuilder_ == null) {
            this.find_ = null;
            onChanged();
          } else {
            this.findBuilder_.clear();
          } 
          this.bitField0_ &= 0xFFFFFFFD;
          return this;
        }
        
        public MysqlxCrud.Find.Builder getFindBuilder() {
          this.bitField0_ |= 0x2;
          onChanged();
          return (MysqlxCrud.Find.Builder)getFindFieldBuilder().getBuilder();
        }
        
        public MysqlxCrud.FindOrBuilder getFindOrBuilder() {
          if (this.findBuilder_ != null)
            return (MysqlxCrud.FindOrBuilder)this.findBuilder_.getMessageOrBuilder(); 
          return (this.find_ == null) ? MysqlxCrud.Find.getDefaultInstance() : this.find_;
        }
        
        private SingleFieldBuilderV3<MysqlxCrud.Find, MysqlxCrud.Find.Builder, MysqlxCrud.FindOrBuilder> getFindFieldBuilder() {
          if (this.findBuilder_ == null) {
            this.findBuilder_ = new SingleFieldBuilderV3((AbstractMessage)getFind(), (AbstractMessage.BuilderParent)getParentForChildren(), isClean());
            this.find_ = null;
          } 
          return this.findBuilder_;
        }
        
        public boolean hasInsert() {
          return ((this.bitField0_ & 0x4) != 0);
        }
        
        public MysqlxCrud.Insert getInsert() {
          if (this.insertBuilder_ == null)
            return (this.insert_ == null) ? MysqlxCrud.Insert.getDefaultInstance() : this.insert_; 
          return (MysqlxCrud.Insert)this.insertBuilder_.getMessage();
        }
        
        public Builder setInsert(MysqlxCrud.Insert value) {
          if (this.insertBuilder_ == null) {
            if (value == null)
              throw new NullPointerException(); 
            this.insert_ = value;
            onChanged();
          } else {
            this.insertBuilder_.setMessage((AbstractMessage)value);
          } 
          this.bitField0_ |= 0x4;
          return this;
        }
        
        public Builder setInsert(MysqlxCrud.Insert.Builder builderForValue) {
          if (this.insertBuilder_ == null) {
            this.insert_ = builderForValue.build();
            onChanged();
          } else {
            this.insertBuilder_.setMessage((AbstractMessage)builderForValue.build());
          } 
          this.bitField0_ |= 0x4;
          return this;
        }
        
        public Builder mergeInsert(MysqlxCrud.Insert value) {
          if (this.insertBuilder_ == null) {
            if ((this.bitField0_ & 0x4) != 0 && this.insert_ != null && this.insert_ != MysqlxCrud.Insert.getDefaultInstance()) {
              this.insert_ = MysqlxCrud.Insert.newBuilder(this.insert_).mergeFrom(value).buildPartial();
            } else {
              this.insert_ = value;
            } 
            onChanged();
          } else {
            this.insertBuilder_.mergeFrom((AbstractMessage)value);
          } 
          this.bitField0_ |= 0x4;
          return this;
        }
        
        public Builder clearInsert() {
          if (this.insertBuilder_ == null) {
            this.insert_ = null;
            onChanged();
          } else {
            this.insertBuilder_.clear();
          } 
          this.bitField0_ &= 0xFFFFFFFB;
          return this;
        }
        
        public MysqlxCrud.Insert.Builder getInsertBuilder() {
          this.bitField0_ |= 0x4;
          onChanged();
          return (MysqlxCrud.Insert.Builder)getInsertFieldBuilder().getBuilder();
        }
        
        public MysqlxCrud.InsertOrBuilder getInsertOrBuilder() {
          if (this.insertBuilder_ != null)
            return (MysqlxCrud.InsertOrBuilder)this.insertBuilder_.getMessageOrBuilder(); 
          return (this.insert_ == null) ? MysqlxCrud.Insert.getDefaultInstance() : this.insert_;
        }
        
        private SingleFieldBuilderV3<MysqlxCrud.Insert, MysqlxCrud.Insert.Builder, MysqlxCrud.InsertOrBuilder> getInsertFieldBuilder() {
          if (this.insertBuilder_ == null) {
            this.insertBuilder_ = new SingleFieldBuilderV3((AbstractMessage)getInsert(), (AbstractMessage.BuilderParent)getParentForChildren(), isClean());
            this.insert_ = null;
          } 
          return this.insertBuilder_;
        }
        
        public boolean hasUpdate() {
          return ((this.bitField0_ & 0x8) != 0);
        }
        
        public MysqlxCrud.Update getUpdate() {
          if (this.updateBuilder_ == null)
            return (this.update_ == null) ? MysqlxCrud.Update.getDefaultInstance() : this.update_; 
          return (MysqlxCrud.Update)this.updateBuilder_.getMessage();
        }
        
        public Builder setUpdate(MysqlxCrud.Update value) {
          if (this.updateBuilder_ == null) {
            if (value == null)
              throw new NullPointerException(); 
            this.update_ = value;
            onChanged();
          } else {
            this.updateBuilder_.setMessage((AbstractMessage)value);
          } 
          this.bitField0_ |= 0x8;
          return this;
        }
        
        public Builder setUpdate(MysqlxCrud.Update.Builder builderForValue) {
          if (this.updateBuilder_ == null) {
            this.update_ = builderForValue.build();
            onChanged();
          } else {
            this.updateBuilder_.setMessage((AbstractMessage)builderForValue.build());
          } 
          this.bitField0_ |= 0x8;
          return this;
        }
        
        public Builder mergeUpdate(MysqlxCrud.Update value) {
          if (this.updateBuilder_ == null) {
            if ((this.bitField0_ & 0x8) != 0 && this.update_ != null && this.update_ != MysqlxCrud.Update.getDefaultInstance()) {
              this.update_ = MysqlxCrud.Update.newBuilder(this.update_).mergeFrom(value).buildPartial();
            } else {
              this.update_ = value;
            } 
            onChanged();
          } else {
            this.updateBuilder_.mergeFrom((AbstractMessage)value);
          } 
          this.bitField0_ |= 0x8;
          return this;
        }
        
        public Builder clearUpdate() {
          if (this.updateBuilder_ == null) {
            this.update_ = null;
            onChanged();
          } else {
            this.updateBuilder_.clear();
          } 
          this.bitField0_ &= 0xFFFFFFF7;
          return this;
        }
        
        public MysqlxCrud.Update.Builder getUpdateBuilder() {
          this.bitField0_ |= 0x8;
          onChanged();
          return (MysqlxCrud.Update.Builder)getUpdateFieldBuilder().getBuilder();
        }
        
        public MysqlxCrud.UpdateOrBuilder getUpdateOrBuilder() {
          if (this.updateBuilder_ != null)
            return (MysqlxCrud.UpdateOrBuilder)this.updateBuilder_.getMessageOrBuilder(); 
          return (this.update_ == null) ? MysqlxCrud.Update.getDefaultInstance() : this.update_;
        }
        
        private SingleFieldBuilderV3<MysqlxCrud.Update, MysqlxCrud.Update.Builder, MysqlxCrud.UpdateOrBuilder> getUpdateFieldBuilder() {
          if (this.updateBuilder_ == null) {
            this.updateBuilder_ = new SingleFieldBuilderV3((AbstractMessage)getUpdate(), (AbstractMessage.BuilderParent)getParentForChildren(), isClean());
            this.update_ = null;
          } 
          return this.updateBuilder_;
        }
        
        public boolean hasDelete() {
          return ((this.bitField0_ & 0x10) != 0);
        }
        
        public MysqlxCrud.Delete getDelete() {
          if (this.deleteBuilder_ == null)
            return (this.delete_ == null) ? MysqlxCrud.Delete.getDefaultInstance() : this.delete_; 
          return (MysqlxCrud.Delete)this.deleteBuilder_.getMessage();
        }
        
        public Builder setDelete(MysqlxCrud.Delete value) {
          if (this.deleteBuilder_ == null) {
            if (value == null)
              throw new NullPointerException(); 
            this.delete_ = value;
            onChanged();
          } else {
            this.deleteBuilder_.setMessage((AbstractMessage)value);
          } 
          this.bitField0_ |= 0x10;
          return this;
        }
        
        public Builder setDelete(MysqlxCrud.Delete.Builder builderForValue) {
          if (this.deleteBuilder_ == null) {
            this.delete_ = builderForValue.build();
            onChanged();
          } else {
            this.deleteBuilder_.setMessage((AbstractMessage)builderForValue.build());
          } 
          this.bitField0_ |= 0x10;
          return this;
        }
        
        public Builder mergeDelete(MysqlxCrud.Delete value) {
          if (this.deleteBuilder_ == null) {
            if ((this.bitField0_ & 0x10) != 0 && this.delete_ != null && this.delete_ != MysqlxCrud.Delete.getDefaultInstance()) {
              this.delete_ = MysqlxCrud.Delete.newBuilder(this.delete_).mergeFrom(value).buildPartial();
            } else {
              this.delete_ = value;
            } 
            onChanged();
          } else {
            this.deleteBuilder_.mergeFrom((AbstractMessage)value);
          } 
          this.bitField0_ |= 0x10;
          return this;
        }
        
        public Builder clearDelete() {
          if (this.deleteBuilder_ == null) {
            this.delete_ = null;
            onChanged();
          } else {
            this.deleteBuilder_.clear();
          } 
          this.bitField0_ &= 0xFFFFFFEF;
          return this;
        }
        
        public MysqlxCrud.Delete.Builder getDeleteBuilder() {
          this.bitField0_ |= 0x10;
          onChanged();
          return (MysqlxCrud.Delete.Builder)getDeleteFieldBuilder().getBuilder();
        }
        
        public MysqlxCrud.DeleteOrBuilder getDeleteOrBuilder() {
          if (this.deleteBuilder_ != null)
            return (MysqlxCrud.DeleteOrBuilder)this.deleteBuilder_.getMessageOrBuilder(); 
          return (this.delete_ == null) ? MysqlxCrud.Delete.getDefaultInstance() : this.delete_;
        }
        
        private SingleFieldBuilderV3<MysqlxCrud.Delete, MysqlxCrud.Delete.Builder, MysqlxCrud.DeleteOrBuilder> getDeleteFieldBuilder() {
          if (this.deleteBuilder_ == null) {
            this.deleteBuilder_ = new SingleFieldBuilderV3((AbstractMessage)getDelete(), (AbstractMessage.BuilderParent)getParentForChildren(), isClean());
            this.delete_ = null;
          } 
          return this.deleteBuilder_;
        }
        
        public boolean hasStmtExecute() {
          return ((this.bitField0_ & 0x20) != 0);
        }
        
        public MysqlxSql.StmtExecute getStmtExecute() {
          if (this.stmtExecuteBuilder_ == null)
            return (this.stmtExecute_ == null) ? MysqlxSql.StmtExecute.getDefaultInstance() : this.stmtExecute_; 
          return (MysqlxSql.StmtExecute)this.stmtExecuteBuilder_.getMessage();
        }
        
        public Builder setStmtExecute(MysqlxSql.StmtExecute value) {
          if (this.stmtExecuteBuilder_ == null) {
            if (value == null)
              throw new NullPointerException(); 
            this.stmtExecute_ = value;
            onChanged();
          } else {
            this.stmtExecuteBuilder_.setMessage((AbstractMessage)value);
          } 
          this.bitField0_ |= 0x20;
          return this;
        }
        
        public Builder setStmtExecute(MysqlxSql.StmtExecute.Builder builderForValue) {
          if (this.stmtExecuteBuilder_ == null) {
            this.stmtExecute_ = builderForValue.build();
            onChanged();
          } else {
            this.stmtExecuteBuilder_.setMessage((AbstractMessage)builderForValue.build());
          } 
          this.bitField0_ |= 0x20;
          return this;
        }
        
        public Builder mergeStmtExecute(MysqlxSql.StmtExecute value) {
          if (this.stmtExecuteBuilder_ == null) {
            if ((this.bitField0_ & 0x20) != 0 && this.stmtExecute_ != null && this.stmtExecute_ != MysqlxSql.StmtExecute.getDefaultInstance()) {
              this.stmtExecute_ = MysqlxSql.StmtExecute.newBuilder(this.stmtExecute_).mergeFrom(value).buildPartial();
            } else {
              this.stmtExecute_ = value;
            } 
            onChanged();
          } else {
            this.stmtExecuteBuilder_.mergeFrom((AbstractMessage)value);
          } 
          this.bitField0_ |= 0x20;
          return this;
        }
        
        public Builder clearStmtExecute() {
          if (this.stmtExecuteBuilder_ == null) {
            this.stmtExecute_ = null;
            onChanged();
          } else {
            this.stmtExecuteBuilder_.clear();
          } 
          this.bitField0_ &= 0xFFFFFFDF;
          return this;
        }
        
        public MysqlxSql.StmtExecute.Builder getStmtExecuteBuilder() {
          this.bitField0_ |= 0x20;
          onChanged();
          return (MysqlxSql.StmtExecute.Builder)getStmtExecuteFieldBuilder().getBuilder();
        }
        
        public MysqlxSql.StmtExecuteOrBuilder getStmtExecuteOrBuilder() {
          if (this.stmtExecuteBuilder_ != null)
            return (MysqlxSql.StmtExecuteOrBuilder)this.stmtExecuteBuilder_.getMessageOrBuilder(); 
          return (this.stmtExecute_ == null) ? MysqlxSql.StmtExecute.getDefaultInstance() : this.stmtExecute_;
        }
        
        private SingleFieldBuilderV3<MysqlxSql.StmtExecute, MysqlxSql.StmtExecute.Builder, MysqlxSql.StmtExecuteOrBuilder> getStmtExecuteFieldBuilder() {
          if (this.stmtExecuteBuilder_ == null) {
            this.stmtExecuteBuilder_ = new SingleFieldBuilderV3((AbstractMessage)getStmtExecute(), (AbstractMessage.BuilderParent)getParentForChildren(), isClean());
            this.stmtExecute_ = null;
          } 
          return this.stmtExecuteBuilder_;
        }
        
        public final Builder setUnknownFields(UnknownFieldSet unknownFields) {
          return (Builder)super.setUnknownFields(unknownFields);
        }
        
        public final Builder mergeUnknownFields(UnknownFieldSet unknownFields) {
          return (Builder)super.mergeUnknownFields(unknownFields);
        }
      }
      
      private static final OneOfMessage DEFAULT_INSTANCE = new OneOfMessage();
      
      public static OneOfMessage getDefaultInstance() {
        return DEFAULT_INSTANCE;
      }
      
      @Deprecated
      public static final Parser<OneOfMessage> PARSER = (Parser<OneOfMessage>)new AbstractParser<OneOfMessage>() {
          public MysqlxPrepare.Prepare.OneOfMessage parsePartialFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            MysqlxPrepare.Prepare.OneOfMessage.Builder builder = MysqlxPrepare.Prepare.OneOfMessage.newBuilder();
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
      
      public static Parser<OneOfMessage> parser() {
        return PARSER;
      }
      
      public Parser<OneOfMessage> getParserForType() {
        return PARSER;
      }
      
      public OneOfMessage getDefaultInstanceForType() {
        return DEFAULT_INSTANCE;
      }
    }
    
    public boolean hasStmtId() {
      return ((this.bitField0_ & 0x1) != 0);
    }
    
    public int getStmtId() {
      return this.stmtId_;
    }
    
    public boolean hasStmt() {
      return ((this.bitField0_ & 0x2) != 0);
    }
    
    public OneOfMessage getStmt() {
      return (this.stmt_ == null) ? OneOfMessage.getDefaultInstance() : this.stmt_;
    }
    
    public OneOfMessageOrBuilder getStmtOrBuilder() {
      return (this.stmt_ == null) ? OneOfMessage.getDefaultInstance() : this.stmt_;
    }
    
    public final boolean isInitialized() {
      byte isInitialized = this.memoizedIsInitialized;
      if (isInitialized == 1)
        return true; 
      if (isInitialized == 0)
        return false; 
      if (!hasStmtId()) {
        this.memoizedIsInitialized = 0;
        return false;
      } 
      if (!hasStmt()) {
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
        output.writeUInt32(1, this.stmtId_); 
      if ((this.bitField0_ & 0x2) != 0)
        output.writeMessage(2, (MessageLite)getStmt()); 
      getUnknownFields().writeTo(output);
    }
    
    public int getSerializedSize() {
      int size = this.memoizedSize;
      if (size != -1)
        return size; 
      size = 0;
      if ((this.bitField0_ & 0x1) != 0)
        size += 
          CodedOutputStream.computeUInt32Size(1, this.stmtId_); 
      if ((this.bitField0_ & 0x2) != 0)
        size += 
          CodedOutputStream.computeMessageSize(2, (MessageLite)getStmt()); 
      size += getUnknownFields().getSerializedSize();
      this.memoizedSize = size;
      return size;
    }
    
    public boolean equals(Object obj) {
      if (obj == this)
        return true; 
      if (!(obj instanceof Prepare))
        return super.equals(obj); 
      Prepare other = (Prepare)obj;
      if (hasStmtId() != other.hasStmtId())
        return false; 
      if (hasStmtId() && 
        getStmtId() != other
        .getStmtId())
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
      if (hasStmtId()) {
        hash = 37 * hash + 1;
        hash = 53 * hash + getStmtId();
      } 
      if (hasStmt()) {
        hash = 37 * hash + 2;
        hash = 53 * hash + getStmt().hashCode();
      } 
      hash = 29 * hash + getUnknownFields().hashCode();
      this.memoizedHashCode = hash;
      return hash;
    }
    
    public static Prepare parseFrom(ByteBuffer data) throws InvalidProtocolBufferException {
      return (Prepare)PARSER.parseFrom(data);
    }
    
    public static Prepare parseFrom(ByteBuffer data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
      return (Prepare)PARSER.parseFrom(data, extensionRegistry);
    }
    
    public static Prepare parseFrom(ByteString data) throws InvalidProtocolBufferException {
      return (Prepare)PARSER.parseFrom(data);
    }
    
    public static Prepare parseFrom(ByteString data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
      return (Prepare)PARSER.parseFrom(data, extensionRegistry);
    }
    
    public static Prepare parseFrom(byte[] data) throws InvalidProtocolBufferException {
      return (Prepare)PARSER.parseFrom(data);
    }
    
    public static Prepare parseFrom(byte[] data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
      return (Prepare)PARSER.parseFrom(data, extensionRegistry);
    }
    
    public static Prepare parseFrom(InputStream input) throws IOException {
      return 
        (Prepare)GeneratedMessageV3.parseWithIOException(PARSER, input);
    }
    
    public static Prepare parseFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
      return 
        (Prepare)GeneratedMessageV3.parseWithIOException(PARSER, input, extensionRegistry);
    }
    
    public static Prepare parseDelimitedFrom(InputStream input) throws IOException {
      return 
        (Prepare)GeneratedMessageV3.parseDelimitedWithIOException(PARSER, input);
    }
    
    public static Prepare parseDelimitedFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
      return 
        (Prepare)GeneratedMessageV3.parseDelimitedWithIOException(PARSER, input, extensionRegistry);
    }
    
    public static Prepare parseFrom(CodedInputStream input) throws IOException {
      return 
        (Prepare)GeneratedMessageV3.parseWithIOException(PARSER, input);
    }
    
    public static Prepare parseFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
      return 
        (Prepare)GeneratedMessageV3.parseWithIOException(PARSER, input, extensionRegistry);
    }
    
    public Builder newBuilderForType() {
      return newBuilder();
    }
    
    public static Builder newBuilder() {
      return DEFAULT_INSTANCE.toBuilder();
    }
    
    public static Builder newBuilder(Prepare prototype) {
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
    
    public static final class Builder extends GeneratedMessageV3.Builder<Builder> implements MysqlxPrepare.PrepareOrBuilder {
      private int bitField0_;
      
      private int stmtId_;
      
      private MysqlxPrepare.Prepare.OneOfMessage stmt_;
      
      private SingleFieldBuilderV3<MysqlxPrepare.Prepare.OneOfMessage, MysqlxPrepare.Prepare.OneOfMessage.Builder, MysqlxPrepare.Prepare.OneOfMessageOrBuilder> stmtBuilder_;
      
      public static final Descriptors.Descriptor getDescriptor() {
        return MysqlxPrepare.internal_static_Mysqlx_Prepare_Prepare_descriptor;
      }
      
      protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() {
        return MysqlxPrepare.internal_static_Mysqlx_Prepare_Prepare_fieldAccessorTable
          .ensureFieldAccessorsInitialized(MysqlxPrepare.Prepare.class, Builder.class);
      }
      
      private Builder() {
        maybeForceBuilderInitialization();
      }
      
      private Builder(GeneratedMessageV3.BuilderParent parent) {
        super(parent);
        maybeForceBuilderInitialization();
      }
      
      private void maybeForceBuilderInitialization() {
        if (MysqlxPrepare.Prepare.alwaysUseFieldBuilders)
          getStmtFieldBuilder(); 
      }
      
      public Builder clear() {
        super.clear();
        this.stmtId_ = 0;
        this.bitField0_ &= 0xFFFFFFFE;
        if (this.stmtBuilder_ == null) {
          this.stmt_ = null;
        } else {
          this.stmtBuilder_.clear();
        } 
        this.bitField0_ &= 0xFFFFFFFD;
        return this;
      }
      
      public Descriptors.Descriptor getDescriptorForType() {
        return MysqlxPrepare.internal_static_Mysqlx_Prepare_Prepare_descriptor;
      }
      
      public MysqlxPrepare.Prepare getDefaultInstanceForType() {
        return MysqlxPrepare.Prepare.getDefaultInstance();
      }
      
      public MysqlxPrepare.Prepare build() {
        MysqlxPrepare.Prepare result = buildPartial();
        if (!result.isInitialized())
          throw newUninitializedMessageException(result); 
        return result;
      }
      
      public MysqlxPrepare.Prepare buildPartial() {
        MysqlxPrepare.Prepare result = new MysqlxPrepare.Prepare(this);
        int from_bitField0_ = this.bitField0_;
        int to_bitField0_ = 0;
        if ((from_bitField0_ & 0x1) != 0) {
          result.stmtId_ = this.stmtId_;
          to_bitField0_ |= 0x1;
        } 
        if ((from_bitField0_ & 0x2) != 0) {
          if (this.stmtBuilder_ == null) {
            result.stmt_ = this.stmt_;
          } else {
            result.stmt_ = (MysqlxPrepare.Prepare.OneOfMessage)this.stmtBuilder_.build();
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
        if (other instanceof MysqlxPrepare.Prepare)
          return mergeFrom((MysqlxPrepare.Prepare)other); 
        super.mergeFrom(other);
        return this;
      }
      
      public Builder mergeFrom(MysqlxPrepare.Prepare other) {
        if (other == MysqlxPrepare.Prepare.getDefaultInstance())
          return this; 
        if (other.hasStmtId())
          setStmtId(other.getStmtId()); 
        if (other.hasStmt())
          mergeStmt(other.getStmt()); 
        mergeUnknownFields(other.getUnknownFields());
        onChanged();
        return this;
      }
      
      public final boolean isInitialized() {
        if (!hasStmtId())
          return false; 
        if (!hasStmt())
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
            int tag = input.readTag();
            switch (tag) {
              case 0:
                done = true;
                continue;
              case 8:
                this.stmtId_ = input.readUInt32();
                this.bitField0_ |= 0x1;
                continue;
              case 18:
                input.readMessage((MessageLite.Builder)
                    getStmtFieldBuilder().getBuilder(), extensionRegistry);
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
      
      public boolean hasStmtId() {
        return ((this.bitField0_ & 0x1) != 0);
      }
      
      public int getStmtId() {
        return this.stmtId_;
      }
      
      public Builder setStmtId(int value) {
        this.bitField0_ |= 0x1;
        this.stmtId_ = value;
        onChanged();
        return this;
      }
      
      public Builder clearStmtId() {
        this.bitField0_ &= 0xFFFFFFFE;
        this.stmtId_ = 0;
        onChanged();
        return this;
      }
      
      public boolean hasStmt() {
        return ((this.bitField0_ & 0x2) != 0);
      }
      
      public MysqlxPrepare.Prepare.OneOfMessage getStmt() {
        if (this.stmtBuilder_ == null)
          return (this.stmt_ == null) ? MysqlxPrepare.Prepare.OneOfMessage.getDefaultInstance() : this.stmt_; 
        return (MysqlxPrepare.Prepare.OneOfMessage)this.stmtBuilder_.getMessage();
      }
      
      public Builder setStmt(MysqlxPrepare.Prepare.OneOfMessage value) {
        if (this.stmtBuilder_ == null) {
          if (value == null)
            throw new NullPointerException(); 
          this.stmt_ = value;
          onChanged();
        } else {
          this.stmtBuilder_.setMessage((AbstractMessage)value);
        } 
        this.bitField0_ |= 0x2;
        return this;
      }
      
      public Builder setStmt(MysqlxPrepare.Prepare.OneOfMessage.Builder builderForValue) {
        if (this.stmtBuilder_ == null) {
          this.stmt_ = builderForValue.build();
          onChanged();
        } else {
          this.stmtBuilder_.setMessage((AbstractMessage)builderForValue.build());
        } 
        this.bitField0_ |= 0x2;
        return this;
      }
      
      public Builder mergeStmt(MysqlxPrepare.Prepare.OneOfMessage value) {
        if (this.stmtBuilder_ == null) {
          if ((this.bitField0_ & 0x2) != 0 && this.stmt_ != null && this.stmt_ != 
            
            MysqlxPrepare.Prepare.OneOfMessage.getDefaultInstance()) {
            this
              .stmt_ = MysqlxPrepare.Prepare.OneOfMessage.newBuilder(this.stmt_).mergeFrom(value).buildPartial();
          } else {
            this.stmt_ = value;
          } 
          onChanged();
        } else {
          this.stmtBuilder_.mergeFrom((AbstractMessage)value);
        } 
        this.bitField0_ |= 0x2;
        return this;
      }
      
      public Builder clearStmt() {
        if (this.stmtBuilder_ == null) {
          this.stmt_ = null;
          onChanged();
        } else {
          this.stmtBuilder_.clear();
        } 
        this.bitField0_ &= 0xFFFFFFFD;
        return this;
      }
      
      public MysqlxPrepare.Prepare.OneOfMessage.Builder getStmtBuilder() {
        this.bitField0_ |= 0x2;
        onChanged();
        return (MysqlxPrepare.Prepare.OneOfMessage.Builder)getStmtFieldBuilder().getBuilder();
      }
      
      public MysqlxPrepare.Prepare.OneOfMessageOrBuilder getStmtOrBuilder() {
        if (this.stmtBuilder_ != null)
          return (MysqlxPrepare.Prepare.OneOfMessageOrBuilder)this.stmtBuilder_.getMessageOrBuilder(); 
        return (this.stmt_ == null) ? 
          MysqlxPrepare.Prepare.OneOfMessage.getDefaultInstance() : this.stmt_;
      }
      
      private SingleFieldBuilderV3<MysqlxPrepare.Prepare.OneOfMessage, MysqlxPrepare.Prepare.OneOfMessage.Builder, MysqlxPrepare.Prepare.OneOfMessageOrBuilder> getStmtFieldBuilder() {
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
    
    private static final Prepare DEFAULT_INSTANCE = new Prepare();
    
    public static Prepare getDefaultInstance() {
      return DEFAULT_INSTANCE;
    }
    
    @Deprecated
    public static final Parser<Prepare> PARSER = (Parser<Prepare>)new AbstractParser<Prepare>() {
        public MysqlxPrepare.Prepare parsePartialFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
          MysqlxPrepare.Prepare.Builder builder = MysqlxPrepare.Prepare.newBuilder();
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
    
    public static Parser<Prepare> parser() {
      return PARSER;
    }
    
    public Parser<Prepare> getParserForType() {
      return PARSER;
    }
    
    public Prepare getDefaultInstanceForType() {
      return DEFAULT_INSTANCE;
    }
    
    public static interface OneOfMessageOrBuilder extends MessageOrBuilder {
      boolean hasType();
      
      MysqlxPrepare.Prepare.OneOfMessage.Type getType();
      
      boolean hasFind();
      
      MysqlxCrud.Find getFind();
      
      MysqlxCrud.FindOrBuilder getFindOrBuilder();
      
      boolean hasInsert();
      
      MysqlxCrud.Insert getInsert();
      
      MysqlxCrud.InsertOrBuilder getInsertOrBuilder();
      
      boolean hasUpdate();
      
      MysqlxCrud.Update getUpdate();
      
      MysqlxCrud.UpdateOrBuilder getUpdateOrBuilder();
      
      boolean hasDelete();
      
      MysqlxCrud.Delete getDelete();
      
      MysqlxCrud.DeleteOrBuilder getDeleteOrBuilder();
      
      boolean hasStmtExecute();
      
      MysqlxSql.StmtExecute getStmtExecute();
      
      MysqlxSql.StmtExecuteOrBuilder getStmtExecuteOrBuilder();
    }
  }
  
  public static interface ExecuteOrBuilder extends MessageOrBuilder {
    boolean hasStmtId();
    
    int getStmtId();
    
    List<MysqlxDatatypes.Any> getArgsList();
    
    MysqlxDatatypes.Any getArgs(int param1Int);
    
    int getArgsCount();
    
    List<? extends MysqlxDatatypes.AnyOrBuilder> getArgsOrBuilderList();
    
    MysqlxDatatypes.AnyOrBuilder getArgsOrBuilder(int param1Int);
    
    boolean hasCompactMetadata();
    
    boolean getCompactMetadata();
  }
  
  public static final class Execute extends GeneratedMessageV3 implements ExecuteOrBuilder {
    private static final long serialVersionUID = 0L;
    
    private int bitField0_;
    
    public static final int STMT_ID_FIELD_NUMBER = 1;
    
    private int stmtId_;
    
    public static final int ARGS_FIELD_NUMBER = 2;
    
    private List<MysqlxDatatypes.Any> args_;
    
    public static final int COMPACT_METADATA_FIELD_NUMBER = 3;
    
    private boolean compactMetadata_;
    
    private byte memoizedIsInitialized;
    
    private Execute(GeneratedMessageV3.Builder<?> builder) {
      super(builder);
      this.memoizedIsInitialized = -1;
    }
    
    private Execute() {
      this.memoizedIsInitialized = -1;
      this.args_ = Collections.emptyList();
    }
    
    protected Object newInstance(GeneratedMessageV3.UnusedPrivateParameter unused) {
      return new Execute();
    }
    
    public final UnknownFieldSet getUnknownFields() {
      return this.unknownFields;
    }
    
    public static final Descriptors.Descriptor getDescriptor() {
      return MysqlxPrepare.internal_static_Mysqlx_Prepare_Execute_descriptor;
    }
    
    protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() {
      return MysqlxPrepare.internal_static_Mysqlx_Prepare_Execute_fieldAccessorTable.ensureFieldAccessorsInitialized(Execute.class, Builder.class);
    }
    
    public boolean hasStmtId() {
      return ((this.bitField0_ & 0x1) != 0);
    }
    
    public int getStmtId() {
      return this.stmtId_;
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
      return ((this.bitField0_ & 0x2) != 0);
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
      if (!hasStmtId()) {
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
      if ((this.bitField0_ & 0x1) != 0)
        output.writeUInt32(1, this.stmtId_); 
      for (int i = 0; i < this.args_.size(); i++)
        output.writeMessage(2, (MessageLite)this.args_.get(i)); 
      if ((this.bitField0_ & 0x2) != 0)
        output.writeBool(3, this.compactMetadata_); 
      getUnknownFields().writeTo(output);
    }
    
    public int getSerializedSize() {
      int size = this.memoizedSize;
      if (size != -1)
        return size; 
      size = 0;
      if ((this.bitField0_ & 0x1) != 0)
        size += 
          CodedOutputStream.computeUInt32Size(1, this.stmtId_); 
      for (int i = 0; i < this.args_.size(); i++)
        size += 
          CodedOutputStream.computeMessageSize(2, (MessageLite)this.args_.get(i)); 
      if ((this.bitField0_ & 0x2) != 0)
        size += 
          CodedOutputStream.computeBoolSize(3, this.compactMetadata_); 
      size += getUnknownFields().getSerializedSize();
      this.memoizedSize = size;
      return size;
    }
    
    public boolean equals(Object obj) {
      if (obj == this)
        return true; 
      if (!(obj instanceof Execute))
        return super.equals(obj); 
      Execute other = (Execute)obj;
      if (hasStmtId() != other.hasStmtId())
        return false; 
      if (hasStmtId() && 
        getStmtId() != other
        .getStmtId())
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
      if (hasStmtId()) {
        hash = 37 * hash + 1;
        hash = 53 * hash + getStmtId();
      } 
      if (getArgsCount() > 0) {
        hash = 37 * hash + 2;
        hash = 53 * hash + getArgsList().hashCode();
      } 
      if (hasCompactMetadata()) {
        hash = 37 * hash + 3;
        hash = 53 * hash + Internal.hashBoolean(
            getCompactMetadata());
      } 
      hash = 29 * hash + getUnknownFields().hashCode();
      this.memoizedHashCode = hash;
      return hash;
    }
    
    public static Execute parseFrom(ByteBuffer data) throws InvalidProtocolBufferException {
      return (Execute)PARSER.parseFrom(data);
    }
    
    public static Execute parseFrom(ByteBuffer data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
      return (Execute)PARSER.parseFrom(data, extensionRegistry);
    }
    
    public static Execute parseFrom(ByteString data) throws InvalidProtocolBufferException {
      return (Execute)PARSER.parseFrom(data);
    }
    
    public static Execute parseFrom(ByteString data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
      return (Execute)PARSER.parseFrom(data, extensionRegistry);
    }
    
    public static Execute parseFrom(byte[] data) throws InvalidProtocolBufferException {
      return (Execute)PARSER.parseFrom(data);
    }
    
    public static Execute parseFrom(byte[] data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
      return (Execute)PARSER.parseFrom(data, extensionRegistry);
    }
    
    public static Execute parseFrom(InputStream input) throws IOException {
      return 
        (Execute)GeneratedMessageV3.parseWithIOException(PARSER, input);
    }
    
    public static Execute parseFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
      return 
        (Execute)GeneratedMessageV3.parseWithIOException(PARSER, input, extensionRegistry);
    }
    
    public static Execute parseDelimitedFrom(InputStream input) throws IOException {
      return 
        (Execute)GeneratedMessageV3.parseDelimitedWithIOException(PARSER, input);
    }
    
    public static Execute parseDelimitedFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
      return 
        (Execute)GeneratedMessageV3.parseDelimitedWithIOException(PARSER, input, extensionRegistry);
    }
    
    public static Execute parseFrom(CodedInputStream input) throws IOException {
      return 
        (Execute)GeneratedMessageV3.parseWithIOException(PARSER, input);
    }
    
    public static Execute parseFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
      return 
        (Execute)GeneratedMessageV3.parseWithIOException(PARSER, input, extensionRegistry);
    }
    
    public Builder newBuilderForType() {
      return newBuilder();
    }
    
    public static Builder newBuilder() {
      return DEFAULT_INSTANCE.toBuilder();
    }
    
    public static Builder newBuilder(Execute prototype) {
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
    
    public static final class Builder extends GeneratedMessageV3.Builder<Builder> implements MysqlxPrepare.ExecuteOrBuilder {
      private int bitField0_;
      
      private int stmtId_;
      
      private List<MysqlxDatatypes.Any> args_;
      
      private RepeatedFieldBuilderV3<MysqlxDatatypes.Any, MysqlxDatatypes.Any.Builder, MysqlxDatatypes.AnyOrBuilder> argsBuilder_;
      
      private boolean compactMetadata_;
      
      public static final Descriptors.Descriptor getDescriptor() {
        return MysqlxPrepare.internal_static_Mysqlx_Prepare_Execute_descriptor;
      }
      
      protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() {
        return MysqlxPrepare.internal_static_Mysqlx_Prepare_Execute_fieldAccessorTable
          .ensureFieldAccessorsInitialized(MysqlxPrepare.Execute.class, Builder.class);
      }
      
      private Builder() {
        this
          .args_ = Collections.emptyList();
      }
      
      private Builder(GeneratedMessageV3.BuilderParent parent) {
        super(parent);
        this.args_ = Collections.emptyList();
      }
      
      public Builder clear() {
        super.clear();
        this.stmtId_ = 0;
        this.bitField0_ &= 0xFFFFFFFE;
        if (this.argsBuilder_ == null) {
          this.args_ = Collections.emptyList();
        } else {
          this.args_ = null;
          this.argsBuilder_.clear();
        } 
        this.bitField0_ &= 0xFFFFFFFD;
        this.compactMetadata_ = false;
        this.bitField0_ &= 0xFFFFFFFB;
        return this;
      }
      
      public Descriptors.Descriptor getDescriptorForType() {
        return MysqlxPrepare.internal_static_Mysqlx_Prepare_Execute_descriptor;
      }
      
      public MysqlxPrepare.Execute getDefaultInstanceForType() {
        return MysqlxPrepare.Execute.getDefaultInstance();
      }
      
      public MysqlxPrepare.Execute build() {
        MysqlxPrepare.Execute result = buildPartial();
        if (!result.isInitialized())
          throw newUninitializedMessageException(result); 
        return result;
      }
      
      public MysqlxPrepare.Execute buildPartial() {
        MysqlxPrepare.Execute result = new MysqlxPrepare.Execute(this);
        int from_bitField0_ = this.bitField0_;
        int to_bitField0_ = 0;
        if ((from_bitField0_ & 0x1) != 0) {
          result.stmtId_ = this.stmtId_;
          to_bitField0_ |= 0x1;
        } 
        if (this.argsBuilder_ == null) {
          if ((this.bitField0_ & 0x2) != 0) {
            this.args_ = Collections.unmodifiableList(this.args_);
            this.bitField0_ &= 0xFFFFFFFD;
          } 
          result.args_ = this.args_;
        } else {
          result.args_ = this.argsBuilder_.build();
        } 
        if ((from_bitField0_ & 0x4) != 0) {
          result.compactMetadata_ = this.compactMetadata_;
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
        if (other instanceof MysqlxPrepare.Execute)
          return mergeFrom((MysqlxPrepare.Execute)other); 
        super.mergeFrom(other);
        return this;
      }
      
      public Builder mergeFrom(MysqlxPrepare.Execute other) {
        if (other == MysqlxPrepare.Execute.getDefaultInstance())
          return this; 
        if (other.hasStmtId())
          setStmtId(other.getStmtId()); 
        if (this.argsBuilder_ == null) {
          if (!other.args_.isEmpty()) {
            if (this.args_.isEmpty()) {
              this.args_ = other.args_;
              this.bitField0_ &= 0xFFFFFFFD;
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
            this.bitField0_ &= 0xFFFFFFFD;
            this.argsBuilder_ = MysqlxPrepare.Execute.alwaysUseFieldBuilders ? getArgsFieldBuilder() : null;
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
        if (!hasStmtId())
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
              case 8:
                this.stmtId_ = input.readUInt32();
                this.bitField0_ |= 0x1;
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
              case 24:
                this.compactMetadata_ = input.readBool();
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
      
      public boolean hasStmtId() {
        return ((this.bitField0_ & 0x1) != 0);
      }
      
      public int getStmtId() {
        return this.stmtId_;
      }
      
      public Builder setStmtId(int value) {
        this.bitField0_ |= 0x1;
        this.stmtId_ = value;
        onChanged();
        return this;
      }
      
      public Builder clearStmtId() {
        this.bitField0_ &= 0xFFFFFFFE;
        this.stmtId_ = 0;
        onChanged();
        return this;
      }
      
      private void ensureArgsIsMutable() {
        if ((this.bitField0_ & 0x2) == 0) {
          this.args_ = new ArrayList<>(this.args_);
          this.bitField0_ |= 0x2;
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
          this.bitField0_ &= 0xFFFFFFFD;
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
            
            .argsBuilder_ = new RepeatedFieldBuilderV3(this.args_, ((this.bitField0_ & 0x2) != 0), (AbstractMessage.BuilderParent)getParentForChildren(), isClean());
          this.args_ = null;
        } 
        return this.argsBuilder_;
      }
      
      public boolean hasCompactMetadata() {
        return ((this.bitField0_ & 0x4) != 0);
      }
      
      public boolean getCompactMetadata() {
        return this.compactMetadata_;
      }
      
      public Builder setCompactMetadata(boolean value) {
        this.bitField0_ |= 0x4;
        this.compactMetadata_ = value;
        onChanged();
        return this;
      }
      
      public Builder clearCompactMetadata() {
        this.bitField0_ &= 0xFFFFFFFB;
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
    
    private static final Execute DEFAULT_INSTANCE = new Execute();
    
    public static Execute getDefaultInstance() {
      return DEFAULT_INSTANCE;
    }
    
    @Deprecated
    public static final Parser<Execute> PARSER = (Parser<Execute>)new AbstractParser<Execute>() {
        public MysqlxPrepare.Execute parsePartialFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
          MysqlxPrepare.Execute.Builder builder = MysqlxPrepare.Execute.newBuilder();
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
    
    public static Parser<Execute> parser() {
      return PARSER;
    }
    
    public Parser<Execute> getParserForType() {
      return PARSER;
    }
    
    public Execute getDefaultInstanceForType() {
      return DEFAULT_INSTANCE;
    }
  }
  
  public static interface DeallocateOrBuilder extends MessageOrBuilder {
    boolean hasStmtId();
    
    int getStmtId();
  }
  
  public static final class Deallocate extends GeneratedMessageV3 implements DeallocateOrBuilder {
    private static final long serialVersionUID = 0L;
    
    private int bitField0_;
    
    public static final int STMT_ID_FIELD_NUMBER = 1;
    
    private int stmtId_;
    
    private byte memoizedIsInitialized;
    
    private Deallocate(GeneratedMessageV3.Builder<?> builder) {
      super(builder);
      this.memoizedIsInitialized = -1;
    }
    
    private Deallocate() {
      this.memoizedIsInitialized = -1;
    }
    
    protected Object newInstance(GeneratedMessageV3.UnusedPrivateParameter unused) {
      return new Deallocate();
    }
    
    public final UnknownFieldSet getUnknownFields() {
      return this.unknownFields;
    }
    
    public static final Descriptors.Descriptor getDescriptor() {
      return MysqlxPrepare.internal_static_Mysqlx_Prepare_Deallocate_descriptor;
    }
    
    protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() {
      return MysqlxPrepare.internal_static_Mysqlx_Prepare_Deallocate_fieldAccessorTable.ensureFieldAccessorsInitialized(Deallocate.class, Builder.class);
    }
    
    public boolean hasStmtId() {
      return ((this.bitField0_ & 0x1) != 0);
    }
    
    public int getStmtId() {
      return this.stmtId_;
    }
    
    public final boolean isInitialized() {
      byte isInitialized = this.memoizedIsInitialized;
      if (isInitialized == 1)
        return true; 
      if (isInitialized == 0)
        return false; 
      if (!hasStmtId()) {
        this.memoizedIsInitialized = 0;
        return false;
      } 
      this.memoizedIsInitialized = 1;
      return true;
    }
    
    public void writeTo(CodedOutputStream output) throws IOException {
      if ((this.bitField0_ & 0x1) != 0)
        output.writeUInt32(1, this.stmtId_); 
      getUnknownFields().writeTo(output);
    }
    
    public int getSerializedSize() {
      int size = this.memoizedSize;
      if (size != -1)
        return size; 
      size = 0;
      if ((this.bitField0_ & 0x1) != 0)
        size += 
          CodedOutputStream.computeUInt32Size(1, this.stmtId_); 
      size += getUnknownFields().getSerializedSize();
      this.memoizedSize = size;
      return size;
    }
    
    public boolean equals(Object obj) {
      if (obj == this)
        return true; 
      if (!(obj instanceof Deallocate))
        return super.equals(obj); 
      Deallocate other = (Deallocate)obj;
      if (hasStmtId() != other.hasStmtId())
        return false; 
      if (hasStmtId() && 
        getStmtId() != other
        .getStmtId())
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
      if (hasStmtId()) {
        hash = 37 * hash + 1;
        hash = 53 * hash + getStmtId();
      } 
      hash = 29 * hash + getUnknownFields().hashCode();
      this.memoizedHashCode = hash;
      return hash;
    }
    
    public static Deallocate parseFrom(ByteBuffer data) throws InvalidProtocolBufferException {
      return (Deallocate)PARSER.parseFrom(data);
    }
    
    public static Deallocate parseFrom(ByteBuffer data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
      return (Deallocate)PARSER.parseFrom(data, extensionRegistry);
    }
    
    public static Deallocate parseFrom(ByteString data) throws InvalidProtocolBufferException {
      return (Deallocate)PARSER.parseFrom(data);
    }
    
    public static Deallocate parseFrom(ByteString data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
      return (Deallocate)PARSER.parseFrom(data, extensionRegistry);
    }
    
    public static Deallocate parseFrom(byte[] data) throws InvalidProtocolBufferException {
      return (Deallocate)PARSER.parseFrom(data);
    }
    
    public static Deallocate parseFrom(byte[] data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
      return (Deallocate)PARSER.parseFrom(data, extensionRegistry);
    }
    
    public static Deallocate parseFrom(InputStream input) throws IOException {
      return 
        (Deallocate)GeneratedMessageV3.parseWithIOException(PARSER, input);
    }
    
    public static Deallocate parseFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
      return 
        (Deallocate)GeneratedMessageV3.parseWithIOException(PARSER, input, extensionRegistry);
    }
    
    public static Deallocate parseDelimitedFrom(InputStream input) throws IOException {
      return 
        (Deallocate)GeneratedMessageV3.parseDelimitedWithIOException(PARSER, input);
    }
    
    public static Deallocate parseDelimitedFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
      return 
        (Deallocate)GeneratedMessageV3.parseDelimitedWithIOException(PARSER, input, extensionRegistry);
    }
    
    public static Deallocate parseFrom(CodedInputStream input) throws IOException {
      return 
        (Deallocate)GeneratedMessageV3.parseWithIOException(PARSER, input);
    }
    
    public static Deallocate parseFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
      return 
        (Deallocate)GeneratedMessageV3.parseWithIOException(PARSER, input, extensionRegistry);
    }
    
    public Builder newBuilderForType() {
      return newBuilder();
    }
    
    public static Builder newBuilder() {
      return DEFAULT_INSTANCE.toBuilder();
    }
    
    public static Builder newBuilder(Deallocate prototype) {
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
    
    public static final class Builder extends GeneratedMessageV3.Builder<Builder> implements MysqlxPrepare.DeallocateOrBuilder {
      private int bitField0_;
      
      private int stmtId_;
      
      public static final Descriptors.Descriptor getDescriptor() {
        return MysqlxPrepare.internal_static_Mysqlx_Prepare_Deallocate_descriptor;
      }
      
      protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() {
        return MysqlxPrepare.internal_static_Mysqlx_Prepare_Deallocate_fieldAccessorTable
          .ensureFieldAccessorsInitialized(MysqlxPrepare.Deallocate.class, Builder.class);
      }
      
      private Builder() {}
      
      private Builder(GeneratedMessageV3.BuilderParent parent) {
        super(parent);
      }
      
      public Builder clear() {
        super.clear();
        this.stmtId_ = 0;
        this.bitField0_ &= 0xFFFFFFFE;
        return this;
      }
      
      public Descriptors.Descriptor getDescriptorForType() {
        return MysqlxPrepare.internal_static_Mysqlx_Prepare_Deallocate_descriptor;
      }
      
      public MysqlxPrepare.Deallocate getDefaultInstanceForType() {
        return MysqlxPrepare.Deallocate.getDefaultInstance();
      }
      
      public MysqlxPrepare.Deallocate build() {
        MysqlxPrepare.Deallocate result = buildPartial();
        if (!result.isInitialized())
          throw newUninitializedMessageException(result); 
        return result;
      }
      
      public MysqlxPrepare.Deallocate buildPartial() {
        MysqlxPrepare.Deallocate result = new MysqlxPrepare.Deallocate(this);
        int from_bitField0_ = this.bitField0_;
        int to_bitField0_ = 0;
        if ((from_bitField0_ & 0x1) != 0) {
          result.stmtId_ = this.stmtId_;
          to_bitField0_ |= 0x1;
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
        if (other instanceof MysqlxPrepare.Deallocate)
          return mergeFrom((MysqlxPrepare.Deallocate)other); 
        super.mergeFrom(other);
        return this;
      }
      
      public Builder mergeFrom(MysqlxPrepare.Deallocate other) {
        if (other == MysqlxPrepare.Deallocate.getDefaultInstance())
          return this; 
        if (other.hasStmtId())
          setStmtId(other.getStmtId()); 
        mergeUnknownFields(other.getUnknownFields());
        onChanged();
        return this;
      }
      
      public final boolean isInitialized() {
        if (!hasStmtId())
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
                this.stmtId_ = input.readUInt32();
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
      
      public boolean hasStmtId() {
        return ((this.bitField0_ & 0x1) != 0);
      }
      
      public int getStmtId() {
        return this.stmtId_;
      }
      
      public Builder setStmtId(int value) {
        this.bitField0_ |= 0x1;
        this.stmtId_ = value;
        onChanged();
        return this;
      }
      
      public Builder clearStmtId() {
        this.bitField0_ &= 0xFFFFFFFE;
        this.stmtId_ = 0;
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
    
    private static final Deallocate DEFAULT_INSTANCE = new Deallocate();
    
    public static Deallocate getDefaultInstance() {
      return DEFAULT_INSTANCE;
    }
    
    @Deprecated
    public static final Parser<Deallocate> PARSER = (Parser<Deallocate>)new AbstractParser<Deallocate>() {
        public MysqlxPrepare.Deallocate parsePartialFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
          MysqlxPrepare.Deallocate.Builder builder = MysqlxPrepare.Deallocate.newBuilder();
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
    
    public static Parser<Deallocate> parser() {
      return PARSER;
    }
    
    public Parser<Deallocate> getParserForType() {
      return PARSER;
    }
    
    public Deallocate getDefaultInstanceForType() {
      return DEFAULT_INSTANCE;
    }
  }
  
  public static Descriptors.FileDescriptor getDescriptor() {
    return descriptor;
  }
  
  static {
    String[] descriptorData = { "\n\024mysqlx_prepare.proto\022\016Mysqlx.Prepare\032\fmysqlx.proto\032\020mysqlx_sql.proto\032\021mysqlx_crud.proto\032\026mysqlx_datatypes.proto\"\003\n\007Prepare\022\017\n\007stmt_id\030\001 \002(\r\0222\n\004stmt\030\002 \002(\0132$.Mysqlx.Prepare.Prepare.OneOfMessage\032Æ\002\n\fOneOfMessage\0227\n\004type\030\001 \002(\0162).Mysqlx.Prepare.Prepare.OneOfMessage.Type\022\037\n\004find\030\002 \001(\0132\021.Mysqlx.Crud.Find\022#\n\006insert\030\003 \001(\0132\023.Mysqlx.Crud.Insert\022#\n\006update\030\004 \001(\0132\023.Mysqlx.Crud.Update\022#\n\006delete\030\005 \001(\0132\023.Mysqlx.Crud.Delete\022-\n\fstmt_execute\030\006 \001(\0132\027.Mysqlx.Sql.StmtExecute\">\n\004Type\022\b\n\004FIND\020\000\022\n\n\006INSERT\020\001\022\n\n\006UPDATE\020\002\022\n\n\006DELETE\020\004\022\b\n\004STMT\020\005:\004ê0(\"f\n\007Execute\022\017\n\007stmt_id\030\001 \002(\r\022#\n\004args\030\002 \003(\0132\025.Mysqlx.Datatypes.Any\022\037\n\020compact_metadata\030\003 \001(\b:\005false:\004ê0)\"#\n\nDeallocate\022\017\n\007stmt_id\030\001 \002(\r:\004ê0*B\031\n\027com.mysql.cj.x.protobuf" };
    descriptor = Descriptors.FileDescriptor.internalBuildGeneratedFileFrom(descriptorData, new Descriptors.FileDescriptor[] { Mysqlx.getDescriptor(), 
          MysqlxSql.getDescriptor(), 
          MysqlxCrud.getDescriptor(), 
          MysqlxDatatypes.getDescriptor() });
    internal_static_Mysqlx_Prepare_Prepare_descriptor = getDescriptor().getMessageTypes().get(0);
    internal_static_Mysqlx_Prepare_Prepare_fieldAccessorTable = new GeneratedMessageV3.FieldAccessorTable(internal_static_Mysqlx_Prepare_Prepare_descriptor, new String[] { "StmtId", "Stmt" });
    internal_static_Mysqlx_Prepare_Prepare_OneOfMessage_descriptor = internal_static_Mysqlx_Prepare_Prepare_descriptor.getNestedTypes().get(0);
    internal_static_Mysqlx_Prepare_Prepare_OneOfMessage_fieldAccessorTable = new GeneratedMessageV3.FieldAccessorTable(internal_static_Mysqlx_Prepare_Prepare_OneOfMessage_descriptor, new String[] { "Type", "Find", "Insert", "Update", "Delete", "StmtExecute" });
    internal_static_Mysqlx_Prepare_Execute_descriptor = getDescriptor().getMessageTypes().get(1);
    internal_static_Mysqlx_Prepare_Execute_fieldAccessorTable = new GeneratedMessageV3.FieldAccessorTable(internal_static_Mysqlx_Prepare_Execute_descriptor, new String[] { "StmtId", "Args", "CompactMetadata" });
    internal_static_Mysqlx_Prepare_Deallocate_descriptor = getDescriptor().getMessageTypes().get(2);
    internal_static_Mysqlx_Prepare_Deallocate_fieldAccessorTable = new GeneratedMessageV3.FieldAccessorTable(internal_static_Mysqlx_Prepare_Deallocate_descriptor, new String[] { "StmtId" });
    ExtensionRegistry registry = ExtensionRegistry.newInstance();
    registry.add(Mysqlx.clientMessageId);
    Descriptors.FileDescriptor.internalUpdateFileDescriptor(descriptor, registry);
    Mysqlx.getDescriptor();
    MysqlxSql.getDescriptor();
    MysqlxCrud.getDescriptor();
    MysqlxDatatypes.getDescriptor();
  }
}
