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

public final class MysqlxExpr {
  private static final Descriptors.Descriptor internal_static_Mysqlx_Expr_Expr_descriptor;
  
  private static final GeneratedMessageV3.FieldAccessorTable internal_static_Mysqlx_Expr_Expr_fieldAccessorTable;
  
  private static final Descriptors.Descriptor internal_static_Mysqlx_Expr_Identifier_descriptor;
  
  private static final GeneratedMessageV3.FieldAccessorTable internal_static_Mysqlx_Expr_Identifier_fieldAccessorTable;
  
  private static final Descriptors.Descriptor internal_static_Mysqlx_Expr_DocumentPathItem_descriptor;
  
  private static final GeneratedMessageV3.FieldAccessorTable internal_static_Mysqlx_Expr_DocumentPathItem_fieldAccessorTable;
  
  private static final Descriptors.Descriptor internal_static_Mysqlx_Expr_ColumnIdentifier_descriptor;
  
  private static final GeneratedMessageV3.FieldAccessorTable internal_static_Mysqlx_Expr_ColumnIdentifier_fieldAccessorTable;
  
  private static final Descriptors.Descriptor internal_static_Mysqlx_Expr_FunctionCall_descriptor;
  
  private static final GeneratedMessageV3.FieldAccessorTable internal_static_Mysqlx_Expr_FunctionCall_fieldAccessorTable;
  
  private static final Descriptors.Descriptor internal_static_Mysqlx_Expr_Operator_descriptor;
  
  private static final GeneratedMessageV3.FieldAccessorTable internal_static_Mysqlx_Expr_Operator_fieldAccessorTable;
  
  private static final Descriptors.Descriptor internal_static_Mysqlx_Expr_Object_descriptor;
  
  private static final GeneratedMessageV3.FieldAccessorTable internal_static_Mysqlx_Expr_Object_fieldAccessorTable;
  
  private static final Descriptors.Descriptor internal_static_Mysqlx_Expr_Object_ObjectField_descriptor;
  
  private static final GeneratedMessageV3.FieldAccessorTable internal_static_Mysqlx_Expr_Object_ObjectField_fieldAccessorTable;
  
  private static final Descriptors.Descriptor internal_static_Mysqlx_Expr_Array_descriptor;
  
  private static final GeneratedMessageV3.FieldAccessorTable internal_static_Mysqlx_Expr_Array_fieldAccessorTable;
  
  private static Descriptors.FileDescriptor descriptor;
  
  public static void registerAllExtensions(ExtensionRegistryLite registry) {}
  
  public static void registerAllExtensions(ExtensionRegistry registry) {
    registerAllExtensions((ExtensionRegistryLite)registry);
  }
  
  public static interface ExprOrBuilder extends MessageOrBuilder {
    boolean hasType();
    
    MysqlxExpr.Expr.Type getType();
    
    boolean hasIdentifier();
    
    MysqlxExpr.ColumnIdentifier getIdentifier();
    
    MysqlxExpr.ColumnIdentifierOrBuilder getIdentifierOrBuilder();
    
    boolean hasVariable();
    
    String getVariable();
    
    ByteString getVariableBytes();
    
    boolean hasLiteral();
    
    MysqlxDatatypes.Scalar getLiteral();
    
    MysqlxDatatypes.ScalarOrBuilder getLiteralOrBuilder();
    
    boolean hasFunctionCall();
    
    MysqlxExpr.FunctionCall getFunctionCall();
    
    MysqlxExpr.FunctionCallOrBuilder getFunctionCallOrBuilder();
    
    boolean hasOperator();
    
    MysqlxExpr.Operator getOperator();
    
    MysqlxExpr.OperatorOrBuilder getOperatorOrBuilder();
    
    boolean hasPosition();
    
    int getPosition();
    
    boolean hasObject();
    
    MysqlxExpr.Object getObject();
    
    MysqlxExpr.ObjectOrBuilder getObjectOrBuilder();
    
    boolean hasArray();
    
    MysqlxExpr.Array getArray();
    
    MysqlxExpr.ArrayOrBuilder getArrayOrBuilder();
  }
  
  public static final class Expr extends GeneratedMessageV3 implements ExprOrBuilder {
    private static final long serialVersionUID = 0L;
    
    private int bitField0_;
    
    public static final int TYPE_FIELD_NUMBER = 1;
    
    private int type_;
    
    public static final int IDENTIFIER_FIELD_NUMBER = 2;
    
    private MysqlxExpr.ColumnIdentifier identifier_;
    
    public static final int VARIABLE_FIELD_NUMBER = 3;
    
    private volatile Object variable_;
    
    public static final int LITERAL_FIELD_NUMBER = 4;
    
    private MysqlxDatatypes.Scalar literal_;
    
    public static final int FUNCTION_CALL_FIELD_NUMBER = 5;
    
    private MysqlxExpr.FunctionCall functionCall_;
    
    public static final int OPERATOR_FIELD_NUMBER = 6;
    
    private MysqlxExpr.Operator operator_;
    
    public static final int POSITION_FIELD_NUMBER = 7;
    
    private int position_;
    
    public static final int OBJECT_FIELD_NUMBER = 8;
    
    private MysqlxExpr.Object object_;
    
    public static final int ARRAY_FIELD_NUMBER = 9;
    
    private MysqlxExpr.Array array_;
    
    private byte memoizedIsInitialized;
    
    private Expr(GeneratedMessageV3.Builder<?> builder) {
      super(builder);
      this.memoizedIsInitialized = -1;
    }
    
    private Expr() {
      this.memoizedIsInitialized = -1;
      this.type_ = 1;
      this.variable_ = "";
    }
    
    protected Object newInstance(GeneratedMessageV3.UnusedPrivateParameter unused) {
      return new Expr();
    }
    
    public final UnknownFieldSet getUnknownFields() {
      return this.unknownFields;
    }
    
    public static final Descriptors.Descriptor getDescriptor() {
      return MysqlxExpr.internal_static_Mysqlx_Expr_Expr_descriptor;
    }
    
    protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() {
      return MysqlxExpr.internal_static_Mysqlx_Expr_Expr_fieldAccessorTable.ensureFieldAccessorsInitialized(Expr.class, Builder.class);
    }
    
    public enum Type implements ProtocolMessageEnum {
      IDENT(1),
      LITERAL(2),
      VARIABLE(3),
      FUNC_CALL(4),
      OPERATOR(5),
      PLACEHOLDER(6),
      OBJECT(7),
      ARRAY(8);
      
      public static final int IDENT_VALUE = 1;
      
      public static final int LITERAL_VALUE = 2;
      
      public static final int VARIABLE_VALUE = 3;
      
      public static final int FUNC_CALL_VALUE = 4;
      
      public static final int OPERATOR_VALUE = 5;
      
      public static final int PLACEHOLDER_VALUE = 6;
      
      public static final int OBJECT_VALUE = 7;
      
      public static final int ARRAY_VALUE = 8;
      
      private static final Internal.EnumLiteMap<Type> internalValueMap = new Internal.EnumLiteMap<Type>() {
          public MysqlxExpr.Expr.Type findValueByNumber(int number) {
            return MysqlxExpr.Expr.Type.forNumber(number);
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
            return IDENT;
          case 2:
            return LITERAL;
          case 3:
            return VARIABLE;
          case 4:
            return FUNC_CALL;
          case 5:
            return OPERATOR;
          case 6:
            return PLACEHOLDER;
          case 7:
            return OBJECT;
          case 8:
            return ARRAY;
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
        return MysqlxExpr.Expr.getDescriptor().getEnumTypes().get(0);
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
      return (result == null) ? Type.IDENT : result;
    }
    
    public boolean hasIdentifier() {
      return ((this.bitField0_ & 0x2) != 0);
    }
    
    public MysqlxExpr.ColumnIdentifier getIdentifier() {
      return (this.identifier_ == null) ? MysqlxExpr.ColumnIdentifier.getDefaultInstance() : this.identifier_;
    }
    
    public MysqlxExpr.ColumnIdentifierOrBuilder getIdentifierOrBuilder() {
      return (this.identifier_ == null) ? MysqlxExpr.ColumnIdentifier.getDefaultInstance() : this.identifier_;
    }
    
    public boolean hasVariable() {
      return ((this.bitField0_ & 0x4) != 0);
    }
    
    public String getVariable() {
      Object ref = this.variable_;
      if (ref instanceof String)
        return (String)ref; 
      ByteString bs = (ByteString)ref;
      String s = bs.toStringUtf8();
      if (bs.isValidUtf8())
        this.variable_ = s; 
      return s;
    }
    
    public ByteString getVariableBytes() {
      Object ref = this.variable_;
      if (ref instanceof String) {
        ByteString b = ByteString.copyFromUtf8((String)ref);
        this.variable_ = b;
        return b;
      } 
      return (ByteString)ref;
    }
    
    public boolean hasLiteral() {
      return ((this.bitField0_ & 0x8) != 0);
    }
    
    public MysqlxDatatypes.Scalar getLiteral() {
      return (this.literal_ == null) ? MysqlxDatatypes.Scalar.getDefaultInstance() : this.literal_;
    }
    
    public MysqlxDatatypes.ScalarOrBuilder getLiteralOrBuilder() {
      return (this.literal_ == null) ? MysqlxDatatypes.Scalar.getDefaultInstance() : this.literal_;
    }
    
    public boolean hasFunctionCall() {
      return ((this.bitField0_ & 0x10) != 0);
    }
    
    public MysqlxExpr.FunctionCall getFunctionCall() {
      return (this.functionCall_ == null) ? MysqlxExpr.FunctionCall.getDefaultInstance() : this.functionCall_;
    }
    
    public MysqlxExpr.FunctionCallOrBuilder getFunctionCallOrBuilder() {
      return (this.functionCall_ == null) ? MysqlxExpr.FunctionCall.getDefaultInstance() : this.functionCall_;
    }
    
    public boolean hasOperator() {
      return ((this.bitField0_ & 0x20) != 0);
    }
    
    public MysqlxExpr.Operator getOperator() {
      return (this.operator_ == null) ? MysqlxExpr.Operator.getDefaultInstance() : this.operator_;
    }
    
    public MysqlxExpr.OperatorOrBuilder getOperatorOrBuilder() {
      return (this.operator_ == null) ? MysqlxExpr.Operator.getDefaultInstance() : this.operator_;
    }
    
    public boolean hasPosition() {
      return ((this.bitField0_ & 0x40) != 0);
    }
    
    public int getPosition() {
      return this.position_;
    }
    
    public boolean hasObject() {
      return ((this.bitField0_ & 0x80) != 0);
    }
    
    public MysqlxExpr.Object getObject() {
      return (this.object_ == null) ? MysqlxExpr.Object.getDefaultInstance() : this.object_;
    }
    
    public MysqlxExpr.ObjectOrBuilder getObjectOrBuilder() {
      return (this.object_ == null) ? MysqlxExpr.Object.getDefaultInstance() : this.object_;
    }
    
    public boolean hasArray() {
      return ((this.bitField0_ & 0x100) != 0);
    }
    
    public MysqlxExpr.Array getArray() {
      return (this.array_ == null) ? MysqlxExpr.Array.getDefaultInstance() : this.array_;
    }
    
    public MysqlxExpr.ArrayOrBuilder getArrayOrBuilder() {
      return (this.array_ == null) ? MysqlxExpr.Array.getDefaultInstance() : this.array_;
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
      if (hasIdentifier() && 
        !getIdentifier().isInitialized()) {
        this.memoizedIsInitialized = 0;
        return false;
      } 
      if (hasLiteral() && 
        !getLiteral().isInitialized()) {
        this.memoizedIsInitialized = 0;
        return false;
      } 
      if (hasFunctionCall() && 
        !getFunctionCall().isInitialized()) {
        this.memoizedIsInitialized = 0;
        return false;
      } 
      if (hasOperator() && 
        !getOperator().isInitialized()) {
        this.memoizedIsInitialized = 0;
        return false;
      } 
      if (hasObject() && 
        !getObject().isInitialized()) {
        this.memoizedIsInitialized = 0;
        return false;
      } 
      if (hasArray() && 
        !getArray().isInitialized()) {
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
        output.writeMessage(2, (MessageLite)getIdentifier()); 
      if ((this.bitField0_ & 0x4) != 0)
        GeneratedMessageV3.writeString(output, 3, this.variable_); 
      if ((this.bitField0_ & 0x8) != 0)
        output.writeMessage(4, (MessageLite)getLiteral()); 
      if ((this.bitField0_ & 0x10) != 0)
        output.writeMessage(5, (MessageLite)getFunctionCall()); 
      if ((this.bitField0_ & 0x20) != 0)
        output.writeMessage(6, (MessageLite)getOperator()); 
      if ((this.bitField0_ & 0x40) != 0)
        output.writeUInt32(7, this.position_); 
      if ((this.bitField0_ & 0x80) != 0)
        output.writeMessage(8, (MessageLite)getObject()); 
      if ((this.bitField0_ & 0x100) != 0)
        output.writeMessage(9, (MessageLite)getArray()); 
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
          CodedOutputStream.computeMessageSize(2, (MessageLite)getIdentifier()); 
      if ((this.bitField0_ & 0x4) != 0)
        size += GeneratedMessageV3.computeStringSize(3, this.variable_); 
      if ((this.bitField0_ & 0x8) != 0)
        size += 
          CodedOutputStream.computeMessageSize(4, (MessageLite)getLiteral()); 
      if ((this.bitField0_ & 0x10) != 0)
        size += 
          CodedOutputStream.computeMessageSize(5, (MessageLite)getFunctionCall()); 
      if ((this.bitField0_ & 0x20) != 0)
        size += 
          CodedOutputStream.computeMessageSize(6, (MessageLite)getOperator()); 
      if ((this.bitField0_ & 0x40) != 0)
        size += 
          CodedOutputStream.computeUInt32Size(7, this.position_); 
      if ((this.bitField0_ & 0x80) != 0)
        size += 
          CodedOutputStream.computeMessageSize(8, (MessageLite)getObject()); 
      if ((this.bitField0_ & 0x100) != 0)
        size += 
          CodedOutputStream.computeMessageSize(9, (MessageLite)getArray()); 
      size += getUnknownFields().getSerializedSize();
      this.memoizedSize = size;
      return size;
    }
    
    public boolean equals(Object obj) {
      if (obj == this)
        return true; 
      if (!(obj instanceof Expr))
        return super.equals(obj); 
      Expr other = (Expr)obj;
      if (hasType() != other.hasType())
        return false; 
      if (hasType() && 
        this.type_ != other.type_)
        return false; 
      if (hasIdentifier() != other.hasIdentifier())
        return false; 
      if (hasIdentifier() && 
        
        !getIdentifier().equals(other.getIdentifier()))
        return false; 
      if (hasVariable() != other.hasVariable())
        return false; 
      if (hasVariable() && 
        
        !getVariable().equals(other.getVariable()))
        return false; 
      if (hasLiteral() != other.hasLiteral())
        return false; 
      if (hasLiteral() && 
        
        !getLiteral().equals(other.getLiteral()))
        return false; 
      if (hasFunctionCall() != other.hasFunctionCall())
        return false; 
      if (hasFunctionCall() && 
        
        !getFunctionCall().equals(other.getFunctionCall()))
        return false; 
      if (hasOperator() != other.hasOperator())
        return false; 
      if (hasOperator() && 
        
        !getOperator().equals(other.getOperator()))
        return false; 
      if (hasPosition() != other.hasPosition())
        return false; 
      if (hasPosition() && 
        getPosition() != other
        .getPosition())
        return false; 
      if (hasObject() != other.hasObject())
        return false; 
      if (hasObject() && 
        
        !getObject().equals(other.getObject()))
        return false; 
      if (hasArray() != other.hasArray())
        return false; 
      if (hasArray() && 
        
        !getArray().equals(other.getArray()))
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
      if (hasIdentifier()) {
        hash = 37 * hash + 2;
        hash = 53 * hash + getIdentifier().hashCode();
      } 
      if (hasVariable()) {
        hash = 37 * hash + 3;
        hash = 53 * hash + getVariable().hashCode();
      } 
      if (hasLiteral()) {
        hash = 37 * hash + 4;
        hash = 53 * hash + getLiteral().hashCode();
      } 
      if (hasFunctionCall()) {
        hash = 37 * hash + 5;
        hash = 53 * hash + getFunctionCall().hashCode();
      } 
      if (hasOperator()) {
        hash = 37 * hash + 6;
        hash = 53 * hash + getOperator().hashCode();
      } 
      if (hasPosition()) {
        hash = 37 * hash + 7;
        hash = 53 * hash + getPosition();
      } 
      if (hasObject()) {
        hash = 37 * hash + 8;
        hash = 53 * hash + getObject().hashCode();
      } 
      if (hasArray()) {
        hash = 37 * hash + 9;
        hash = 53 * hash + getArray().hashCode();
      } 
      hash = 29 * hash + getUnknownFields().hashCode();
      this.memoizedHashCode = hash;
      return hash;
    }
    
    public static Expr parseFrom(ByteBuffer data) throws InvalidProtocolBufferException {
      return (Expr)PARSER.parseFrom(data);
    }
    
    public static Expr parseFrom(ByteBuffer data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
      return (Expr)PARSER.parseFrom(data, extensionRegistry);
    }
    
    public static Expr parseFrom(ByteString data) throws InvalidProtocolBufferException {
      return (Expr)PARSER.parseFrom(data);
    }
    
    public static Expr parseFrom(ByteString data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
      return (Expr)PARSER.parseFrom(data, extensionRegistry);
    }
    
    public static Expr parseFrom(byte[] data) throws InvalidProtocolBufferException {
      return (Expr)PARSER.parseFrom(data);
    }
    
    public static Expr parseFrom(byte[] data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
      return (Expr)PARSER.parseFrom(data, extensionRegistry);
    }
    
    public static Expr parseFrom(InputStream input) throws IOException {
      return 
        (Expr)GeneratedMessageV3.parseWithIOException(PARSER, input);
    }
    
    public static Expr parseFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
      return 
        (Expr)GeneratedMessageV3.parseWithIOException(PARSER, input, extensionRegistry);
    }
    
    public static Expr parseDelimitedFrom(InputStream input) throws IOException {
      return 
        (Expr)GeneratedMessageV3.parseDelimitedWithIOException(PARSER, input);
    }
    
    public static Expr parseDelimitedFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
      return 
        (Expr)GeneratedMessageV3.parseDelimitedWithIOException(PARSER, input, extensionRegistry);
    }
    
    public static Expr parseFrom(CodedInputStream input) throws IOException {
      return 
        (Expr)GeneratedMessageV3.parseWithIOException(PARSER, input);
    }
    
    public static Expr parseFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
      return 
        (Expr)GeneratedMessageV3.parseWithIOException(PARSER, input, extensionRegistry);
    }
    
    public Builder newBuilderForType() {
      return newBuilder();
    }
    
    public static Builder newBuilder() {
      return DEFAULT_INSTANCE.toBuilder();
    }
    
    public static Builder newBuilder(Expr prototype) {
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
    
    public static final class Builder extends GeneratedMessageV3.Builder<Builder> implements MysqlxExpr.ExprOrBuilder {
      private int bitField0_;
      
      private int type_;
      
      private MysqlxExpr.ColumnIdentifier identifier_;
      
      private SingleFieldBuilderV3<MysqlxExpr.ColumnIdentifier, MysqlxExpr.ColumnIdentifier.Builder, MysqlxExpr.ColumnIdentifierOrBuilder> identifierBuilder_;
      
      private Object variable_;
      
      private MysqlxDatatypes.Scalar literal_;
      
      private SingleFieldBuilderV3<MysqlxDatatypes.Scalar, MysqlxDatatypes.Scalar.Builder, MysqlxDatatypes.ScalarOrBuilder> literalBuilder_;
      
      private MysqlxExpr.FunctionCall functionCall_;
      
      private SingleFieldBuilderV3<MysqlxExpr.FunctionCall, MysqlxExpr.FunctionCall.Builder, MysqlxExpr.FunctionCallOrBuilder> functionCallBuilder_;
      
      private MysqlxExpr.Operator operator_;
      
      private SingleFieldBuilderV3<MysqlxExpr.Operator, MysqlxExpr.Operator.Builder, MysqlxExpr.OperatorOrBuilder> operatorBuilder_;
      
      private int position_;
      
      private MysqlxExpr.Object object_;
      
      private SingleFieldBuilderV3<MysqlxExpr.Object, MysqlxExpr.Object.Builder, MysqlxExpr.ObjectOrBuilder> objectBuilder_;
      
      private MysqlxExpr.Array array_;
      
      private SingleFieldBuilderV3<MysqlxExpr.Array, MysqlxExpr.Array.Builder, MysqlxExpr.ArrayOrBuilder> arrayBuilder_;
      
      public static final Descriptors.Descriptor getDescriptor() {
        return MysqlxExpr.internal_static_Mysqlx_Expr_Expr_descriptor;
      }
      
      protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() {
        return MysqlxExpr.internal_static_Mysqlx_Expr_Expr_fieldAccessorTable
          .ensureFieldAccessorsInitialized(MysqlxExpr.Expr.class, Builder.class);
      }
      
      private Builder() {
        this.type_ = 1;
        this.variable_ = "";
        maybeForceBuilderInitialization();
      }
      
      private Builder(GeneratedMessageV3.BuilderParent parent) {
        super(parent);
        this.type_ = 1;
        this.variable_ = "";
        maybeForceBuilderInitialization();
      }
      
      private void maybeForceBuilderInitialization() {
        if (MysqlxExpr.Expr.alwaysUseFieldBuilders) {
          getIdentifierFieldBuilder();
          getLiteralFieldBuilder();
          getFunctionCallFieldBuilder();
          getOperatorFieldBuilder();
          getObjectFieldBuilder();
          getArrayFieldBuilder();
        } 
      }
      
      public Builder clear() {
        super.clear();
        this.type_ = 1;
        this.bitField0_ &= 0xFFFFFFFE;
        if (this.identifierBuilder_ == null) {
          this.identifier_ = null;
        } else {
          this.identifierBuilder_.clear();
        } 
        this.bitField0_ &= 0xFFFFFFFD;
        this.variable_ = "";
        this.bitField0_ &= 0xFFFFFFFB;
        if (this.literalBuilder_ == null) {
          this.literal_ = null;
        } else {
          this.literalBuilder_.clear();
        } 
        this.bitField0_ &= 0xFFFFFFF7;
        if (this.functionCallBuilder_ == null) {
          this.functionCall_ = null;
        } else {
          this.functionCallBuilder_.clear();
        } 
        this.bitField0_ &= 0xFFFFFFEF;
        if (this.operatorBuilder_ == null) {
          this.operator_ = null;
        } else {
          this.operatorBuilder_.clear();
        } 
        this.bitField0_ &= 0xFFFFFFDF;
        this.position_ = 0;
        this.bitField0_ &= 0xFFFFFFBF;
        if (this.objectBuilder_ == null) {
          this.object_ = null;
        } else {
          this.objectBuilder_.clear();
        } 
        this.bitField0_ &= 0xFFFFFF7F;
        if (this.arrayBuilder_ == null) {
          this.array_ = null;
        } else {
          this.arrayBuilder_.clear();
        } 
        this.bitField0_ &= 0xFFFFFEFF;
        return this;
      }
      
      public Descriptors.Descriptor getDescriptorForType() {
        return MysqlxExpr.internal_static_Mysqlx_Expr_Expr_descriptor;
      }
      
      public MysqlxExpr.Expr getDefaultInstanceForType() {
        return MysqlxExpr.Expr.getDefaultInstance();
      }
      
      public MysqlxExpr.Expr build() {
        MysqlxExpr.Expr result = buildPartial();
        if (!result.isInitialized())
          throw newUninitializedMessageException(result); 
        return result;
      }
      
      public MysqlxExpr.Expr buildPartial() {
        MysqlxExpr.Expr result = new MysqlxExpr.Expr(this);
        int from_bitField0_ = this.bitField0_;
        int to_bitField0_ = 0;
        if ((from_bitField0_ & 0x1) != 0)
          to_bitField0_ |= 0x1; 
        result.type_ = this.type_;
        if ((from_bitField0_ & 0x2) != 0) {
          if (this.identifierBuilder_ == null) {
            result.identifier_ = this.identifier_;
          } else {
            result.identifier_ = (MysqlxExpr.ColumnIdentifier)this.identifierBuilder_.build();
          } 
          to_bitField0_ |= 0x2;
        } 
        if ((from_bitField0_ & 0x4) != 0)
          to_bitField0_ |= 0x4; 
        result.variable_ = this.variable_;
        if ((from_bitField0_ & 0x8) != 0) {
          if (this.literalBuilder_ == null) {
            result.literal_ = this.literal_;
          } else {
            result.literal_ = (MysqlxDatatypes.Scalar)this.literalBuilder_.build();
          } 
          to_bitField0_ |= 0x8;
        } 
        if ((from_bitField0_ & 0x10) != 0) {
          if (this.functionCallBuilder_ == null) {
            result.functionCall_ = this.functionCall_;
          } else {
            result.functionCall_ = (MysqlxExpr.FunctionCall)this.functionCallBuilder_.build();
          } 
          to_bitField0_ |= 0x10;
        } 
        if ((from_bitField0_ & 0x20) != 0) {
          if (this.operatorBuilder_ == null) {
            result.operator_ = this.operator_;
          } else {
            result.operator_ = (MysqlxExpr.Operator)this.operatorBuilder_.build();
          } 
          to_bitField0_ |= 0x20;
        } 
        if ((from_bitField0_ & 0x40) != 0) {
          result.position_ = this.position_;
          to_bitField0_ |= 0x40;
        } 
        if ((from_bitField0_ & 0x80) != 0) {
          if (this.objectBuilder_ == null) {
            result.object_ = this.object_;
          } else {
            result.object_ = (MysqlxExpr.Object)this.objectBuilder_.build();
          } 
          to_bitField0_ |= 0x80;
        } 
        if ((from_bitField0_ & 0x100) != 0) {
          if (this.arrayBuilder_ == null) {
            result.array_ = this.array_;
          } else {
            result.array_ = (MysqlxExpr.Array)this.arrayBuilder_.build();
          } 
          to_bitField0_ |= 0x100;
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
        if (other instanceof MysqlxExpr.Expr)
          return mergeFrom((MysqlxExpr.Expr)other); 
        super.mergeFrom(other);
        return this;
      }
      
      public Builder mergeFrom(MysqlxExpr.Expr other) {
        if (other == MysqlxExpr.Expr.getDefaultInstance())
          return this; 
        if (other.hasType())
          setType(other.getType()); 
        if (other.hasIdentifier())
          mergeIdentifier(other.getIdentifier()); 
        if (other.hasVariable()) {
          this.bitField0_ |= 0x4;
          this.variable_ = other.variable_;
          onChanged();
        } 
        if (other.hasLiteral())
          mergeLiteral(other.getLiteral()); 
        if (other.hasFunctionCall())
          mergeFunctionCall(other.getFunctionCall()); 
        if (other.hasOperator())
          mergeOperator(other.getOperator()); 
        if (other.hasPosition())
          setPosition(other.getPosition()); 
        if (other.hasObject())
          mergeObject(other.getObject()); 
        if (other.hasArray())
          mergeArray(other.getArray()); 
        mergeUnknownFields(other.getUnknownFields());
        onChanged();
        return this;
      }
      
      public final boolean isInitialized() {
        if (!hasType())
          return false; 
        if (hasIdentifier() && !getIdentifier().isInitialized())
          return false; 
        if (hasLiteral() && !getLiteral().isInitialized())
          return false; 
        if (hasFunctionCall() && !getFunctionCall().isInitialized())
          return false; 
        if (hasOperator() && !getOperator().isInitialized())
          return false; 
        if (hasObject() && !getObject().isInitialized())
          return false; 
        if (hasArray() && !getArray().isInitialized())
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
            MysqlxExpr.Expr.Type tmpValue;
            int tag = input.readTag();
            switch (tag) {
              case 0:
                done = true;
                continue;
              case 8:
                tmpRaw = input.readEnum();
                tmpValue = MysqlxExpr.Expr.Type.forNumber(tmpRaw);
                if (tmpValue == null) {
                  mergeUnknownVarintField(1, tmpRaw);
                  continue;
                } 
                this.type_ = tmpRaw;
                this.bitField0_ |= 0x1;
                continue;
              case 18:
                input.readMessage((MessageLite.Builder)getIdentifierFieldBuilder().getBuilder(), extensionRegistry);
                this.bitField0_ |= 0x2;
                continue;
              case 26:
                this.variable_ = input.readBytes();
                this.bitField0_ |= 0x4;
                continue;
              case 34:
                input.readMessage((MessageLite.Builder)getLiteralFieldBuilder().getBuilder(), extensionRegistry);
                this.bitField0_ |= 0x8;
                continue;
              case 42:
                input.readMessage((MessageLite.Builder)getFunctionCallFieldBuilder().getBuilder(), extensionRegistry);
                this.bitField0_ |= 0x10;
                continue;
              case 50:
                input.readMessage((MessageLite.Builder)getOperatorFieldBuilder().getBuilder(), extensionRegistry);
                this.bitField0_ |= 0x20;
                continue;
              case 56:
                this.position_ = input.readUInt32();
                this.bitField0_ |= 0x40;
                continue;
              case 66:
                input.readMessage((MessageLite.Builder)getObjectFieldBuilder().getBuilder(), extensionRegistry);
                this.bitField0_ |= 0x80;
                continue;
              case 74:
                input.readMessage((MessageLite.Builder)getArrayFieldBuilder().getBuilder(), extensionRegistry);
                this.bitField0_ |= 0x100;
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
      
      public MysqlxExpr.Expr.Type getType() {
        MysqlxExpr.Expr.Type result = MysqlxExpr.Expr.Type.valueOf(this.type_);
        return (result == null) ? MysqlxExpr.Expr.Type.IDENT : result;
      }
      
      public Builder setType(MysqlxExpr.Expr.Type value) {
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
      
      public boolean hasIdentifier() {
        return ((this.bitField0_ & 0x2) != 0);
      }
      
      public MysqlxExpr.ColumnIdentifier getIdentifier() {
        if (this.identifierBuilder_ == null)
          return (this.identifier_ == null) ? MysqlxExpr.ColumnIdentifier.getDefaultInstance() : this.identifier_; 
        return (MysqlxExpr.ColumnIdentifier)this.identifierBuilder_.getMessage();
      }
      
      public Builder setIdentifier(MysqlxExpr.ColumnIdentifier value) {
        if (this.identifierBuilder_ == null) {
          if (value == null)
            throw new NullPointerException(); 
          this.identifier_ = value;
          onChanged();
        } else {
          this.identifierBuilder_.setMessage((AbstractMessage)value);
        } 
        this.bitField0_ |= 0x2;
        return this;
      }
      
      public Builder setIdentifier(MysqlxExpr.ColumnIdentifier.Builder builderForValue) {
        if (this.identifierBuilder_ == null) {
          this.identifier_ = builderForValue.build();
          onChanged();
        } else {
          this.identifierBuilder_.setMessage((AbstractMessage)builderForValue.build());
        } 
        this.bitField0_ |= 0x2;
        return this;
      }
      
      public Builder mergeIdentifier(MysqlxExpr.ColumnIdentifier value) {
        if (this.identifierBuilder_ == null) {
          if ((this.bitField0_ & 0x2) != 0 && this.identifier_ != null && this.identifier_ != MysqlxExpr.ColumnIdentifier.getDefaultInstance()) {
            this.identifier_ = MysqlxExpr.ColumnIdentifier.newBuilder(this.identifier_).mergeFrom(value).buildPartial();
          } else {
            this.identifier_ = value;
          } 
          onChanged();
        } else {
          this.identifierBuilder_.mergeFrom((AbstractMessage)value);
        } 
        this.bitField0_ |= 0x2;
        return this;
      }
      
      public Builder clearIdentifier() {
        if (this.identifierBuilder_ == null) {
          this.identifier_ = null;
          onChanged();
        } else {
          this.identifierBuilder_.clear();
        } 
        this.bitField0_ &= 0xFFFFFFFD;
        return this;
      }
      
      public MysqlxExpr.ColumnIdentifier.Builder getIdentifierBuilder() {
        this.bitField0_ |= 0x2;
        onChanged();
        return (MysqlxExpr.ColumnIdentifier.Builder)getIdentifierFieldBuilder().getBuilder();
      }
      
      public MysqlxExpr.ColumnIdentifierOrBuilder getIdentifierOrBuilder() {
        if (this.identifierBuilder_ != null)
          return (MysqlxExpr.ColumnIdentifierOrBuilder)this.identifierBuilder_.getMessageOrBuilder(); 
        return (this.identifier_ == null) ? MysqlxExpr.ColumnIdentifier.getDefaultInstance() : this.identifier_;
      }
      
      private SingleFieldBuilderV3<MysqlxExpr.ColumnIdentifier, MysqlxExpr.ColumnIdentifier.Builder, MysqlxExpr.ColumnIdentifierOrBuilder> getIdentifierFieldBuilder() {
        if (this.identifierBuilder_ == null) {
          this.identifierBuilder_ = new SingleFieldBuilderV3((AbstractMessage)getIdentifier(), (AbstractMessage.BuilderParent)getParentForChildren(), isClean());
          this.identifier_ = null;
        } 
        return this.identifierBuilder_;
      }
      
      public boolean hasVariable() {
        return ((this.bitField0_ & 0x4) != 0);
      }
      
      public String getVariable() {
        Object ref = this.variable_;
        if (!(ref instanceof String)) {
          ByteString bs = (ByteString)ref;
          String s = bs.toStringUtf8();
          if (bs.isValidUtf8())
            this.variable_ = s; 
          return s;
        } 
        return (String)ref;
      }
      
      public ByteString getVariableBytes() {
        Object ref = this.variable_;
        if (ref instanceof String) {
          ByteString b = ByteString.copyFromUtf8((String)ref);
          this.variable_ = b;
          return b;
        } 
        return (ByteString)ref;
      }
      
      public Builder setVariable(String value) {
        if (value == null)
          throw new NullPointerException(); 
        this.bitField0_ |= 0x4;
        this.variable_ = value;
        onChanged();
        return this;
      }
      
      public Builder clearVariable() {
        this.bitField0_ &= 0xFFFFFFFB;
        this.variable_ = MysqlxExpr.Expr.getDefaultInstance().getVariable();
        onChanged();
        return this;
      }
      
      public Builder setVariableBytes(ByteString value) {
        if (value == null)
          throw new NullPointerException(); 
        this.bitField0_ |= 0x4;
        this.variable_ = value;
        onChanged();
        return this;
      }
      
      public boolean hasLiteral() {
        return ((this.bitField0_ & 0x8) != 0);
      }
      
      public MysqlxDatatypes.Scalar getLiteral() {
        if (this.literalBuilder_ == null)
          return (this.literal_ == null) ? MysqlxDatatypes.Scalar.getDefaultInstance() : this.literal_; 
        return (MysqlxDatatypes.Scalar)this.literalBuilder_.getMessage();
      }
      
      public Builder setLiteral(MysqlxDatatypes.Scalar value) {
        if (this.literalBuilder_ == null) {
          if (value == null)
            throw new NullPointerException(); 
          this.literal_ = value;
          onChanged();
        } else {
          this.literalBuilder_.setMessage((AbstractMessage)value);
        } 
        this.bitField0_ |= 0x8;
        return this;
      }
      
      public Builder setLiteral(MysqlxDatatypes.Scalar.Builder builderForValue) {
        if (this.literalBuilder_ == null) {
          this.literal_ = builderForValue.build();
          onChanged();
        } else {
          this.literalBuilder_.setMessage((AbstractMessage)builderForValue.build());
        } 
        this.bitField0_ |= 0x8;
        return this;
      }
      
      public Builder mergeLiteral(MysqlxDatatypes.Scalar value) {
        if (this.literalBuilder_ == null) {
          if ((this.bitField0_ & 0x8) != 0 && this.literal_ != null && this.literal_ != 
            
            MysqlxDatatypes.Scalar.getDefaultInstance()) {
            this
              .literal_ = MysqlxDatatypes.Scalar.newBuilder(this.literal_).mergeFrom(value).buildPartial();
          } else {
            this.literal_ = value;
          } 
          onChanged();
        } else {
          this.literalBuilder_.mergeFrom((AbstractMessage)value);
        } 
        this.bitField0_ |= 0x8;
        return this;
      }
      
      public Builder clearLiteral() {
        if (this.literalBuilder_ == null) {
          this.literal_ = null;
          onChanged();
        } else {
          this.literalBuilder_.clear();
        } 
        this.bitField0_ &= 0xFFFFFFF7;
        return this;
      }
      
      public MysqlxDatatypes.Scalar.Builder getLiteralBuilder() {
        this.bitField0_ |= 0x8;
        onChanged();
        return (MysqlxDatatypes.Scalar.Builder)getLiteralFieldBuilder().getBuilder();
      }
      
      public MysqlxDatatypes.ScalarOrBuilder getLiteralOrBuilder() {
        if (this.literalBuilder_ != null)
          return (MysqlxDatatypes.ScalarOrBuilder)this.literalBuilder_.getMessageOrBuilder(); 
        return (this.literal_ == null) ? 
          MysqlxDatatypes.Scalar.getDefaultInstance() : this.literal_;
      }
      
      private SingleFieldBuilderV3<MysqlxDatatypes.Scalar, MysqlxDatatypes.Scalar.Builder, MysqlxDatatypes.ScalarOrBuilder> getLiteralFieldBuilder() {
        if (this.literalBuilder_ == null) {
          this
            
            .literalBuilder_ = new SingleFieldBuilderV3((AbstractMessage)getLiteral(), (AbstractMessage.BuilderParent)getParentForChildren(), isClean());
          this.literal_ = null;
        } 
        return this.literalBuilder_;
      }
      
      public boolean hasFunctionCall() {
        return ((this.bitField0_ & 0x10) != 0);
      }
      
      public MysqlxExpr.FunctionCall getFunctionCall() {
        if (this.functionCallBuilder_ == null)
          return (this.functionCall_ == null) ? MysqlxExpr.FunctionCall.getDefaultInstance() : this.functionCall_; 
        return (MysqlxExpr.FunctionCall)this.functionCallBuilder_.getMessage();
      }
      
      public Builder setFunctionCall(MysqlxExpr.FunctionCall value) {
        if (this.functionCallBuilder_ == null) {
          if (value == null)
            throw new NullPointerException(); 
          this.functionCall_ = value;
          onChanged();
        } else {
          this.functionCallBuilder_.setMessage((AbstractMessage)value);
        } 
        this.bitField0_ |= 0x10;
        return this;
      }
      
      public Builder setFunctionCall(MysqlxExpr.FunctionCall.Builder builderForValue) {
        if (this.functionCallBuilder_ == null) {
          this.functionCall_ = builderForValue.build();
          onChanged();
        } else {
          this.functionCallBuilder_.setMessage((AbstractMessage)builderForValue.build());
        } 
        this.bitField0_ |= 0x10;
        return this;
      }
      
      public Builder mergeFunctionCall(MysqlxExpr.FunctionCall value) {
        if (this.functionCallBuilder_ == null) {
          if ((this.bitField0_ & 0x10) != 0 && this.functionCall_ != null && this.functionCall_ != 
            
            MysqlxExpr.FunctionCall.getDefaultInstance()) {
            this
              .functionCall_ = MysqlxExpr.FunctionCall.newBuilder(this.functionCall_).mergeFrom(value).buildPartial();
          } else {
            this.functionCall_ = value;
          } 
          onChanged();
        } else {
          this.functionCallBuilder_.mergeFrom((AbstractMessage)value);
        } 
        this.bitField0_ |= 0x10;
        return this;
      }
      
      public Builder clearFunctionCall() {
        if (this.functionCallBuilder_ == null) {
          this.functionCall_ = null;
          onChanged();
        } else {
          this.functionCallBuilder_.clear();
        } 
        this.bitField0_ &= 0xFFFFFFEF;
        return this;
      }
      
      public MysqlxExpr.FunctionCall.Builder getFunctionCallBuilder() {
        this.bitField0_ |= 0x10;
        onChanged();
        return (MysqlxExpr.FunctionCall.Builder)getFunctionCallFieldBuilder().getBuilder();
      }
      
      public MysqlxExpr.FunctionCallOrBuilder getFunctionCallOrBuilder() {
        if (this.functionCallBuilder_ != null)
          return (MysqlxExpr.FunctionCallOrBuilder)this.functionCallBuilder_.getMessageOrBuilder(); 
        return (this.functionCall_ == null) ? 
          MysqlxExpr.FunctionCall.getDefaultInstance() : this.functionCall_;
      }
      
      private SingleFieldBuilderV3<MysqlxExpr.FunctionCall, MysqlxExpr.FunctionCall.Builder, MysqlxExpr.FunctionCallOrBuilder> getFunctionCallFieldBuilder() {
        if (this.functionCallBuilder_ == null) {
          this
            
            .functionCallBuilder_ = new SingleFieldBuilderV3((AbstractMessage)getFunctionCall(), (AbstractMessage.BuilderParent)getParentForChildren(), isClean());
          this.functionCall_ = null;
        } 
        return this.functionCallBuilder_;
      }
      
      public boolean hasOperator() {
        return ((this.bitField0_ & 0x20) != 0);
      }
      
      public MysqlxExpr.Operator getOperator() {
        if (this.operatorBuilder_ == null)
          return (this.operator_ == null) ? MysqlxExpr.Operator.getDefaultInstance() : this.operator_; 
        return (MysqlxExpr.Operator)this.operatorBuilder_.getMessage();
      }
      
      public Builder setOperator(MysqlxExpr.Operator value) {
        if (this.operatorBuilder_ == null) {
          if (value == null)
            throw new NullPointerException(); 
          this.operator_ = value;
          onChanged();
        } else {
          this.operatorBuilder_.setMessage((AbstractMessage)value);
        } 
        this.bitField0_ |= 0x20;
        return this;
      }
      
      public Builder setOperator(MysqlxExpr.Operator.Builder builderForValue) {
        if (this.operatorBuilder_ == null) {
          this.operator_ = builderForValue.build();
          onChanged();
        } else {
          this.operatorBuilder_.setMessage((AbstractMessage)builderForValue.build());
        } 
        this.bitField0_ |= 0x20;
        return this;
      }
      
      public Builder mergeOperator(MysqlxExpr.Operator value) {
        if (this.operatorBuilder_ == null) {
          if ((this.bitField0_ & 0x20) != 0 && this.operator_ != null && this.operator_ != 
            
            MysqlxExpr.Operator.getDefaultInstance()) {
            this
              .operator_ = MysqlxExpr.Operator.newBuilder(this.operator_).mergeFrom(value).buildPartial();
          } else {
            this.operator_ = value;
          } 
          onChanged();
        } else {
          this.operatorBuilder_.mergeFrom((AbstractMessage)value);
        } 
        this.bitField0_ |= 0x20;
        return this;
      }
      
      public Builder clearOperator() {
        if (this.operatorBuilder_ == null) {
          this.operator_ = null;
          onChanged();
        } else {
          this.operatorBuilder_.clear();
        } 
        this.bitField0_ &= 0xFFFFFFDF;
        return this;
      }
      
      public MysqlxExpr.Operator.Builder getOperatorBuilder() {
        this.bitField0_ |= 0x20;
        onChanged();
        return (MysqlxExpr.Operator.Builder)getOperatorFieldBuilder().getBuilder();
      }
      
      public MysqlxExpr.OperatorOrBuilder getOperatorOrBuilder() {
        if (this.operatorBuilder_ != null)
          return (MysqlxExpr.OperatorOrBuilder)this.operatorBuilder_.getMessageOrBuilder(); 
        return (this.operator_ == null) ? 
          MysqlxExpr.Operator.getDefaultInstance() : this.operator_;
      }
      
      private SingleFieldBuilderV3<MysqlxExpr.Operator, MysqlxExpr.Operator.Builder, MysqlxExpr.OperatorOrBuilder> getOperatorFieldBuilder() {
        if (this.operatorBuilder_ == null) {
          this
            
            .operatorBuilder_ = new SingleFieldBuilderV3((AbstractMessage)getOperator(), (AbstractMessage.BuilderParent)getParentForChildren(), isClean());
          this.operator_ = null;
        } 
        return this.operatorBuilder_;
      }
      
      public boolean hasPosition() {
        return ((this.bitField0_ & 0x40) != 0);
      }
      
      public int getPosition() {
        return this.position_;
      }
      
      public Builder setPosition(int value) {
        this.bitField0_ |= 0x40;
        this.position_ = value;
        onChanged();
        return this;
      }
      
      public Builder clearPosition() {
        this.bitField0_ &= 0xFFFFFFBF;
        this.position_ = 0;
        onChanged();
        return this;
      }
      
      public boolean hasObject() {
        return ((this.bitField0_ & 0x80) != 0);
      }
      
      public MysqlxExpr.Object getObject() {
        if (this.objectBuilder_ == null)
          return (this.object_ == null) ? MysqlxExpr.Object.getDefaultInstance() : this.object_; 
        return (MysqlxExpr.Object)this.objectBuilder_.getMessage();
      }
      
      public Builder setObject(MysqlxExpr.Object value) {
        if (this.objectBuilder_ == null) {
          if (value == null)
            throw new NullPointerException(); 
          this.object_ = value;
          onChanged();
        } else {
          this.objectBuilder_.setMessage((AbstractMessage)value);
        } 
        this.bitField0_ |= 0x80;
        return this;
      }
      
      public Builder setObject(MysqlxExpr.Object.Builder builderForValue) {
        if (this.objectBuilder_ == null) {
          this.object_ = builderForValue.build();
          onChanged();
        } else {
          this.objectBuilder_.setMessage((AbstractMessage)builderForValue.build());
        } 
        this.bitField0_ |= 0x80;
        return this;
      }
      
      public Builder mergeObject(MysqlxExpr.Object value) {
        if (this.objectBuilder_ == null) {
          if ((this.bitField0_ & 0x80) != 0 && this.object_ != null && this.object_ != 
            
            MysqlxExpr.Object.getDefaultInstance()) {
            this
              .object_ = MysqlxExpr.Object.newBuilder(this.object_).mergeFrom(value).buildPartial();
          } else {
            this.object_ = value;
          } 
          onChanged();
        } else {
          this.objectBuilder_.mergeFrom((AbstractMessage)value);
        } 
        this.bitField0_ |= 0x80;
        return this;
      }
      
      public Builder clearObject() {
        if (this.objectBuilder_ == null) {
          this.object_ = null;
          onChanged();
        } else {
          this.objectBuilder_.clear();
        } 
        this.bitField0_ &= 0xFFFFFF7F;
        return this;
      }
      
      public MysqlxExpr.Object.Builder getObjectBuilder() {
        this.bitField0_ |= 0x80;
        onChanged();
        return (MysqlxExpr.Object.Builder)getObjectFieldBuilder().getBuilder();
      }
      
      public MysqlxExpr.ObjectOrBuilder getObjectOrBuilder() {
        if (this.objectBuilder_ != null)
          return (MysqlxExpr.ObjectOrBuilder)this.objectBuilder_.getMessageOrBuilder(); 
        return (this.object_ == null) ? 
          MysqlxExpr.Object.getDefaultInstance() : this.object_;
      }
      
      private SingleFieldBuilderV3<MysqlxExpr.Object, MysqlxExpr.Object.Builder, MysqlxExpr.ObjectOrBuilder> getObjectFieldBuilder() {
        if (this.objectBuilder_ == null) {
          this
            
            .objectBuilder_ = new SingleFieldBuilderV3((AbstractMessage)getObject(), (AbstractMessage.BuilderParent)getParentForChildren(), isClean());
          this.object_ = null;
        } 
        return this.objectBuilder_;
      }
      
      public boolean hasArray() {
        return ((this.bitField0_ & 0x100) != 0);
      }
      
      public MysqlxExpr.Array getArray() {
        if (this.arrayBuilder_ == null)
          return (this.array_ == null) ? MysqlxExpr.Array.getDefaultInstance() : this.array_; 
        return (MysqlxExpr.Array)this.arrayBuilder_.getMessage();
      }
      
      public Builder setArray(MysqlxExpr.Array value) {
        if (this.arrayBuilder_ == null) {
          if (value == null)
            throw new NullPointerException(); 
          this.array_ = value;
          onChanged();
        } else {
          this.arrayBuilder_.setMessage((AbstractMessage)value);
        } 
        this.bitField0_ |= 0x100;
        return this;
      }
      
      public Builder setArray(MysqlxExpr.Array.Builder builderForValue) {
        if (this.arrayBuilder_ == null) {
          this.array_ = builderForValue.build();
          onChanged();
        } else {
          this.arrayBuilder_.setMessage((AbstractMessage)builderForValue.build());
        } 
        this.bitField0_ |= 0x100;
        return this;
      }
      
      public Builder mergeArray(MysqlxExpr.Array value) {
        if (this.arrayBuilder_ == null) {
          if ((this.bitField0_ & 0x100) != 0 && this.array_ != null && this.array_ != 
            
            MysqlxExpr.Array.getDefaultInstance()) {
            this
              .array_ = MysqlxExpr.Array.newBuilder(this.array_).mergeFrom(value).buildPartial();
          } else {
            this.array_ = value;
          } 
          onChanged();
        } else {
          this.arrayBuilder_.mergeFrom((AbstractMessage)value);
        } 
        this.bitField0_ |= 0x100;
        return this;
      }
      
      public Builder clearArray() {
        if (this.arrayBuilder_ == null) {
          this.array_ = null;
          onChanged();
        } else {
          this.arrayBuilder_.clear();
        } 
        this.bitField0_ &= 0xFFFFFEFF;
        return this;
      }
      
      public MysqlxExpr.Array.Builder getArrayBuilder() {
        this.bitField0_ |= 0x100;
        onChanged();
        return (MysqlxExpr.Array.Builder)getArrayFieldBuilder().getBuilder();
      }
      
      public MysqlxExpr.ArrayOrBuilder getArrayOrBuilder() {
        if (this.arrayBuilder_ != null)
          return (MysqlxExpr.ArrayOrBuilder)this.arrayBuilder_.getMessageOrBuilder(); 
        return (this.array_ == null) ? 
          MysqlxExpr.Array.getDefaultInstance() : this.array_;
      }
      
      private SingleFieldBuilderV3<MysqlxExpr.Array, MysqlxExpr.Array.Builder, MysqlxExpr.ArrayOrBuilder> getArrayFieldBuilder() {
        if (this.arrayBuilder_ == null) {
          this
            
            .arrayBuilder_ = new SingleFieldBuilderV3((AbstractMessage)getArray(), (AbstractMessage.BuilderParent)getParentForChildren(), isClean());
          this.array_ = null;
        } 
        return this.arrayBuilder_;
      }
      
      public final Builder setUnknownFields(UnknownFieldSet unknownFields) {
        return (Builder)super.setUnknownFields(unknownFields);
      }
      
      public final Builder mergeUnknownFields(UnknownFieldSet unknownFields) {
        return (Builder)super.mergeUnknownFields(unknownFields);
      }
    }
    
    private static final Expr DEFAULT_INSTANCE = new Expr();
    
    public static Expr getDefaultInstance() {
      return DEFAULT_INSTANCE;
    }
    
    @Deprecated
    public static final Parser<Expr> PARSER = (Parser<Expr>)new AbstractParser<Expr>() {
        public MysqlxExpr.Expr parsePartialFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
          MysqlxExpr.Expr.Builder builder = MysqlxExpr.Expr.newBuilder();
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
    
    public static Parser<Expr> parser() {
      return PARSER;
    }
    
    public Parser<Expr> getParserForType() {
      return PARSER;
    }
    
    public Expr getDefaultInstanceForType() {
      return DEFAULT_INSTANCE;
    }
  }
  
  public static interface IdentifierOrBuilder extends MessageOrBuilder {
    boolean hasName();
    
    String getName();
    
    ByteString getNameBytes();
    
    boolean hasSchemaName();
    
    String getSchemaName();
    
    ByteString getSchemaNameBytes();
  }
  
  public static final class Identifier extends GeneratedMessageV3 implements IdentifierOrBuilder {
    private static final long serialVersionUID = 0L;
    
    private int bitField0_;
    
    public static final int NAME_FIELD_NUMBER = 1;
    
    private volatile Object name_;
    
    public static final int SCHEMA_NAME_FIELD_NUMBER = 2;
    
    private volatile Object schemaName_;
    
    private byte memoizedIsInitialized;
    
    private Identifier(GeneratedMessageV3.Builder<?> builder) {
      super(builder);
      this.memoizedIsInitialized = -1;
    }
    
    private Identifier() {
      this.memoizedIsInitialized = -1;
      this.name_ = "";
      this.schemaName_ = "";
    }
    
    protected Object newInstance(GeneratedMessageV3.UnusedPrivateParameter unused) {
      return new Identifier();
    }
    
    public final UnknownFieldSet getUnknownFields() {
      return this.unknownFields;
    }
    
    public static final Descriptors.Descriptor getDescriptor() {
      return MysqlxExpr.internal_static_Mysqlx_Expr_Identifier_descriptor;
    }
    
    protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() {
      return MysqlxExpr.internal_static_Mysqlx_Expr_Identifier_fieldAccessorTable.ensureFieldAccessorsInitialized(Identifier.class, Builder.class);
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
    
    public boolean hasSchemaName() {
      return ((this.bitField0_ & 0x2) != 0);
    }
    
    public String getSchemaName() {
      Object ref = this.schemaName_;
      if (ref instanceof String)
        return (String)ref; 
      ByteString bs = (ByteString)ref;
      String s = bs.toStringUtf8();
      if (bs.isValidUtf8())
        this.schemaName_ = s; 
      return s;
    }
    
    public ByteString getSchemaNameBytes() {
      Object ref = this.schemaName_;
      if (ref instanceof String) {
        ByteString b = ByteString.copyFromUtf8((String)ref);
        this.schemaName_ = b;
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
        GeneratedMessageV3.writeString(output, 2, this.schemaName_); 
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
        size += GeneratedMessageV3.computeStringSize(2, this.schemaName_); 
      size += getUnknownFields().getSerializedSize();
      this.memoizedSize = size;
      return size;
    }
    
    public boolean equals(Object obj) {
      if (obj == this)
        return true; 
      if (!(obj instanceof Identifier))
        return super.equals(obj); 
      Identifier other = (Identifier)obj;
      if (hasName() != other.hasName())
        return false; 
      if (hasName() && 
        
        !getName().equals(other.getName()))
        return false; 
      if (hasSchemaName() != other.hasSchemaName())
        return false; 
      if (hasSchemaName() && 
        
        !getSchemaName().equals(other.getSchemaName()))
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
      if (hasSchemaName()) {
        hash = 37 * hash + 2;
        hash = 53 * hash + getSchemaName().hashCode();
      } 
      hash = 29 * hash + getUnknownFields().hashCode();
      this.memoizedHashCode = hash;
      return hash;
    }
    
    public static Identifier parseFrom(ByteBuffer data) throws InvalidProtocolBufferException {
      return (Identifier)PARSER.parseFrom(data);
    }
    
    public static Identifier parseFrom(ByteBuffer data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
      return (Identifier)PARSER.parseFrom(data, extensionRegistry);
    }
    
    public static Identifier parseFrom(ByteString data) throws InvalidProtocolBufferException {
      return (Identifier)PARSER.parseFrom(data);
    }
    
    public static Identifier parseFrom(ByteString data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
      return (Identifier)PARSER.parseFrom(data, extensionRegistry);
    }
    
    public static Identifier parseFrom(byte[] data) throws InvalidProtocolBufferException {
      return (Identifier)PARSER.parseFrom(data);
    }
    
    public static Identifier parseFrom(byte[] data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
      return (Identifier)PARSER.parseFrom(data, extensionRegistry);
    }
    
    public static Identifier parseFrom(InputStream input) throws IOException {
      return 
        (Identifier)GeneratedMessageV3.parseWithIOException(PARSER, input);
    }
    
    public static Identifier parseFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
      return 
        (Identifier)GeneratedMessageV3.parseWithIOException(PARSER, input, extensionRegistry);
    }
    
    public static Identifier parseDelimitedFrom(InputStream input) throws IOException {
      return 
        (Identifier)GeneratedMessageV3.parseDelimitedWithIOException(PARSER, input);
    }
    
    public static Identifier parseDelimitedFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
      return 
        (Identifier)GeneratedMessageV3.parseDelimitedWithIOException(PARSER, input, extensionRegistry);
    }
    
    public static Identifier parseFrom(CodedInputStream input) throws IOException {
      return 
        (Identifier)GeneratedMessageV3.parseWithIOException(PARSER, input);
    }
    
    public static Identifier parseFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
      return 
        (Identifier)GeneratedMessageV3.parseWithIOException(PARSER, input, extensionRegistry);
    }
    
    public Builder newBuilderForType() {
      return newBuilder();
    }
    
    public static Builder newBuilder() {
      return DEFAULT_INSTANCE.toBuilder();
    }
    
    public static Builder newBuilder(Identifier prototype) {
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
    
    public static final class Builder extends GeneratedMessageV3.Builder<Builder> implements MysqlxExpr.IdentifierOrBuilder {
      private int bitField0_;
      
      private Object name_;
      
      private Object schemaName_;
      
      public static final Descriptors.Descriptor getDescriptor() {
        return MysqlxExpr.internal_static_Mysqlx_Expr_Identifier_descriptor;
      }
      
      protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() {
        return MysqlxExpr.internal_static_Mysqlx_Expr_Identifier_fieldAccessorTable
          .ensureFieldAccessorsInitialized(MysqlxExpr.Identifier.class, Builder.class);
      }
      
      private Builder() {
        this.name_ = "";
        this.schemaName_ = "";
      }
      
      private Builder(GeneratedMessageV3.BuilderParent parent) {
        super(parent);
        this.name_ = "";
        this.schemaName_ = "";
      }
      
      public Builder clear() {
        super.clear();
        this.name_ = "";
        this.bitField0_ &= 0xFFFFFFFE;
        this.schemaName_ = "";
        this.bitField0_ &= 0xFFFFFFFD;
        return this;
      }
      
      public Descriptors.Descriptor getDescriptorForType() {
        return MysqlxExpr.internal_static_Mysqlx_Expr_Identifier_descriptor;
      }
      
      public MysqlxExpr.Identifier getDefaultInstanceForType() {
        return MysqlxExpr.Identifier.getDefaultInstance();
      }
      
      public MysqlxExpr.Identifier build() {
        MysqlxExpr.Identifier result = buildPartial();
        if (!result.isInitialized())
          throw newUninitializedMessageException(result); 
        return result;
      }
      
      public MysqlxExpr.Identifier buildPartial() {
        MysqlxExpr.Identifier result = new MysqlxExpr.Identifier(this);
        int from_bitField0_ = this.bitField0_;
        int to_bitField0_ = 0;
        if ((from_bitField0_ & 0x1) != 0)
          to_bitField0_ |= 0x1; 
        result.name_ = this.name_;
        if ((from_bitField0_ & 0x2) != 0)
          to_bitField0_ |= 0x2; 
        result.schemaName_ = this.schemaName_;
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
        if (other instanceof MysqlxExpr.Identifier)
          return mergeFrom((MysqlxExpr.Identifier)other); 
        super.mergeFrom(other);
        return this;
      }
      
      public Builder mergeFrom(MysqlxExpr.Identifier other) {
        if (other == MysqlxExpr.Identifier.getDefaultInstance())
          return this; 
        if (other.hasName()) {
          this.bitField0_ |= 0x1;
          this.name_ = other.name_;
          onChanged();
        } 
        if (other.hasSchemaName()) {
          this.bitField0_ |= 0x2;
          this.schemaName_ = other.schemaName_;
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
                this.schemaName_ = input.readBytes();
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
        this.name_ = MysqlxExpr.Identifier.getDefaultInstance().getName();
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
      
      public boolean hasSchemaName() {
        return ((this.bitField0_ & 0x2) != 0);
      }
      
      public String getSchemaName() {
        Object ref = this.schemaName_;
        if (!(ref instanceof String)) {
          ByteString bs = (ByteString)ref;
          String s = bs.toStringUtf8();
          if (bs.isValidUtf8())
            this.schemaName_ = s; 
          return s;
        } 
        return (String)ref;
      }
      
      public ByteString getSchemaNameBytes() {
        Object ref = this.schemaName_;
        if (ref instanceof String) {
          ByteString b = ByteString.copyFromUtf8((String)ref);
          this.schemaName_ = b;
          return b;
        } 
        return (ByteString)ref;
      }
      
      public Builder setSchemaName(String value) {
        if (value == null)
          throw new NullPointerException(); 
        this.bitField0_ |= 0x2;
        this.schemaName_ = value;
        onChanged();
        return this;
      }
      
      public Builder clearSchemaName() {
        this.bitField0_ &= 0xFFFFFFFD;
        this.schemaName_ = MysqlxExpr.Identifier.getDefaultInstance().getSchemaName();
        onChanged();
        return this;
      }
      
      public Builder setSchemaNameBytes(ByteString value) {
        if (value == null)
          throw new NullPointerException(); 
        this.bitField0_ |= 0x2;
        this.schemaName_ = value;
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
    
    private static final Identifier DEFAULT_INSTANCE = new Identifier();
    
    public static Identifier getDefaultInstance() {
      return DEFAULT_INSTANCE;
    }
    
    @Deprecated
    public static final Parser<Identifier> PARSER = (Parser<Identifier>)new AbstractParser<Identifier>() {
        public MysqlxExpr.Identifier parsePartialFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
          MysqlxExpr.Identifier.Builder builder = MysqlxExpr.Identifier.newBuilder();
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
    
    public static Parser<Identifier> parser() {
      return PARSER;
    }
    
    public Parser<Identifier> getParserForType() {
      return PARSER;
    }
    
    public Identifier getDefaultInstanceForType() {
      return DEFAULT_INSTANCE;
    }
  }
  
  public static interface DocumentPathItemOrBuilder extends MessageOrBuilder {
    boolean hasType();
    
    MysqlxExpr.DocumentPathItem.Type getType();
    
    boolean hasValue();
    
    String getValue();
    
    ByteString getValueBytes();
    
    boolean hasIndex();
    
    int getIndex();
  }
  
  public static final class DocumentPathItem extends GeneratedMessageV3 implements DocumentPathItemOrBuilder {
    private static final long serialVersionUID = 0L;
    
    private int bitField0_;
    
    public static final int TYPE_FIELD_NUMBER = 1;
    
    private int type_;
    
    public static final int VALUE_FIELD_NUMBER = 2;
    
    private volatile Object value_;
    
    public static final int INDEX_FIELD_NUMBER = 3;
    
    private int index_;
    
    private byte memoizedIsInitialized;
    
    private DocumentPathItem(GeneratedMessageV3.Builder<?> builder) {
      super(builder);
      this.memoizedIsInitialized = -1;
    }
    
    private DocumentPathItem() {
      this.memoizedIsInitialized = -1;
      this.type_ = 1;
      this.value_ = "";
    }
    
    protected Object newInstance(GeneratedMessageV3.UnusedPrivateParameter unused) {
      return new DocumentPathItem();
    }
    
    public final UnknownFieldSet getUnknownFields() {
      return this.unknownFields;
    }
    
    public static final Descriptors.Descriptor getDescriptor() {
      return MysqlxExpr.internal_static_Mysqlx_Expr_DocumentPathItem_descriptor;
    }
    
    protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() {
      return MysqlxExpr.internal_static_Mysqlx_Expr_DocumentPathItem_fieldAccessorTable.ensureFieldAccessorsInitialized(DocumentPathItem.class, Builder.class);
    }
    
    public enum Type implements ProtocolMessageEnum {
      MEMBER(1),
      MEMBER_ASTERISK(2),
      ARRAY_INDEX(3),
      ARRAY_INDEX_ASTERISK(4),
      DOUBLE_ASTERISK(5);
      
      public static final int MEMBER_VALUE = 1;
      
      public static final int MEMBER_ASTERISK_VALUE = 2;
      
      public static final int ARRAY_INDEX_VALUE = 3;
      
      public static final int ARRAY_INDEX_ASTERISK_VALUE = 4;
      
      public static final int DOUBLE_ASTERISK_VALUE = 5;
      
      private static final Internal.EnumLiteMap<Type> internalValueMap = new Internal.EnumLiteMap<Type>() {
          public MysqlxExpr.DocumentPathItem.Type findValueByNumber(int number) {
            return MysqlxExpr.DocumentPathItem.Type.forNumber(number);
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
            return MEMBER;
          case 2:
            return MEMBER_ASTERISK;
          case 3:
            return ARRAY_INDEX;
          case 4:
            return ARRAY_INDEX_ASTERISK;
          case 5:
            return DOUBLE_ASTERISK;
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
        return MysqlxExpr.DocumentPathItem.getDescriptor().getEnumTypes().get(0);
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
      return (result == null) ? Type.MEMBER : result;
    }
    
    public boolean hasValue() {
      return ((this.bitField0_ & 0x2) != 0);
    }
    
    public String getValue() {
      Object ref = this.value_;
      if (ref instanceof String)
        return (String)ref; 
      ByteString bs = (ByteString)ref;
      String s = bs.toStringUtf8();
      if (bs.isValidUtf8())
        this.value_ = s; 
      return s;
    }
    
    public ByteString getValueBytes() {
      Object ref = this.value_;
      if (ref instanceof String) {
        ByteString b = ByteString.copyFromUtf8((String)ref);
        this.value_ = b;
        return b;
      } 
      return (ByteString)ref;
    }
    
    public boolean hasIndex() {
      return ((this.bitField0_ & 0x4) != 0);
    }
    
    public int getIndex() {
      return this.index_;
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
        GeneratedMessageV3.writeString(output, 2, this.value_); 
      if ((this.bitField0_ & 0x4) != 0)
        output.writeUInt32(3, this.index_); 
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
        size += GeneratedMessageV3.computeStringSize(2, this.value_); 
      if ((this.bitField0_ & 0x4) != 0)
        size += 
          CodedOutputStream.computeUInt32Size(3, this.index_); 
      size += getUnknownFields().getSerializedSize();
      this.memoizedSize = size;
      return size;
    }
    
    public boolean equals(Object obj) {
      if (obj == this)
        return true; 
      if (!(obj instanceof DocumentPathItem))
        return super.equals(obj); 
      DocumentPathItem other = (DocumentPathItem)obj;
      if (hasType() != other.hasType())
        return false; 
      if (hasType() && 
        this.type_ != other.type_)
        return false; 
      if (hasValue() != other.hasValue())
        return false; 
      if (hasValue() && 
        
        !getValue().equals(other.getValue()))
        return false; 
      if (hasIndex() != other.hasIndex())
        return false; 
      if (hasIndex() && 
        getIndex() != other
        .getIndex())
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
      if (hasValue()) {
        hash = 37 * hash + 2;
        hash = 53 * hash + getValue().hashCode();
      } 
      if (hasIndex()) {
        hash = 37 * hash + 3;
        hash = 53 * hash + getIndex();
      } 
      hash = 29 * hash + getUnknownFields().hashCode();
      this.memoizedHashCode = hash;
      return hash;
    }
    
    public static DocumentPathItem parseFrom(ByteBuffer data) throws InvalidProtocolBufferException {
      return (DocumentPathItem)PARSER.parseFrom(data);
    }
    
    public static DocumentPathItem parseFrom(ByteBuffer data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
      return (DocumentPathItem)PARSER.parseFrom(data, extensionRegistry);
    }
    
    public static DocumentPathItem parseFrom(ByteString data) throws InvalidProtocolBufferException {
      return (DocumentPathItem)PARSER.parseFrom(data);
    }
    
    public static DocumentPathItem parseFrom(ByteString data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
      return (DocumentPathItem)PARSER.parseFrom(data, extensionRegistry);
    }
    
    public static DocumentPathItem parseFrom(byte[] data) throws InvalidProtocolBufferException {
      return (DocumentPathItem)PARSER.parseFrom(data);
    }
    
    public static DocumentPathItem parseFrom(byte[] data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
      return (DocumentPathItem)PARSER.parseFrom(data, extensionRegistry);
    }
    
    public static DocumentPathItem parseFrom(InputStream input) throws IOException {
      return 
        (DocumentPathItem)GeneratedMessageV3.parseWithIOException(PARSER, input);
    }
    
    public static DocumentPathItem parseFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
      return 
        (DocumentPathItem)GeneratedMessageV3.parseWithIOException(PARSER, input, extensionRegistry);
    }
    
    public static DocumentPathItem parseDelimitedFrom(InputStream input) throws IOException {
      return 
        (DocumentPathItem)GeneratedMessageV3.parseDelimitedWithIOException(PARSER, input);
    }
    
    public static DocumentPathItem parseDelimitedFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
      return 
        (DocumentPathItem)GeneratedMessageV3.parseDelimitedWithIOException(PARSER, input, extensionRegistry);
    }
    
    public static DocumentPathItem parseFrom(CodedInputStream input) throws IOException {
      return 
        (DocumentPathItem)GeneratedMessageV3.parseWithIOException(PARSER, input);
    }
    
    public static DocumentPathItem parseFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
      return 
        (DocumentPathItem)GeneratedMessageV3.parseWithIOException(PARSER, input, extensionRegistry);
    }
    
    public Builder newBuilderForType() {
      return newBuilder();
    }
    
    public static Builder newBuilder() {
      return DEFAULT_INSTANCE.toBuilder();
    }
    
    public static Builder newBuilder(DocumentPathItem prototype) {
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
    
    public static final class Builder extends GeneratedMessageV3.Builder<Builder> implements MysqlxExpr.DocumentPathItemOrBuilder {
      private int bitField0_;
      
      private int type_;
      
      private Object value_;
      
      private int index_;
      
      public static final Descriptors.Descriptor getDescriptor() {
        return MysqlxExpr.internal_static_Mysqlx_Expr_DocumentPathItem_descriptor;
      }
      
      protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() {
        return MysqlxExpr.internal_static_Mysqlx_Expr_DocumentPathItem_fieldAccessorTable
          .ensureFieldAccessorsInitialized(MysqlxExpr.DocumentPathItem.class, Builder.class);
      }
      
      private Builder() {
        this.type_ = 1;
        this.value_ = "";
      }
      
      private Builder(GeneratedMessageV3.BuilderParent parent) {
        super(parent);
        this.type_ = 1;
        this.value_ = "";
      }
      
      public Builder clear() {
        super.clear();
        this.type_ = 1;
        this.bitField0_ &= 0xFFFFFFFE;
        this.value_ = "";
        this.bitField0_ &= 0xFFFFFFFD;
        this.index_ = 0;
        this.bitField0_ &= 0xFFFFFFFB;
        return this;
      }
      
      public Descriptors.Descriptor getDescriptorForType() {
        return MysqlxExpr.internal_static_Mysqlx_Expr_DocumentPathItem_descriptor;
      }
      
      public MysqlxExpr.DocumentPathItem getDefaultInstanceForType() {
        return MysqlxExpr.DocumentPathItem.getDefaultInstance();
      }
      
      public MysqlxExpr.DocumentPathItem build() {
        MysqlxExpr.DocumentPathItem result = buildPartial();
        if (!result.isInitialized())
          throw newUninitializedMessageException(result); 
        return result;
      }
      
      public MysqlxExpr.DocumentPathItem buildPartial() {
        MysqlxExpr.DocumentPathItem result = new MysqlxExpr.DocumentPathItem(this);
        int from_bitField0_ = this.bitField0_;
        int to_bitField0_ = 0;
        if ((from_bitField0_ & 0x1) != 0)
          to_bitField0_ |= 0x1; 
        result.type_ = this.type_;
        if ((from_bitField0_ & 0x2) != 0)
          to_bitField0_ |= 0x2; 
        result.value_ = this.value_;
        if ((from_bitField0_ & 0x4) != 0) {
          result.index_ = this.index_;
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
        if (other instanceof MysqlxExpr.DocumentPathItem)
          return mergeFrom((MysqlxExpr.DocumentPathItem)other); 
        super.mergeFrom(other);
        return this;
      }
      
      public Builder mergeFrom(MysqlxExpr.DocumentPathItem other) {
        if (other == MysqlxExpr.DocumentPathItem.getDefaultInstance())
          return this; 
        if (other.hasType())
          setType(other.getType()); 
        if (other.hasValue()) {
          this.bitField0_ |= 0x2;
          this.value_ = other.value_;
          onChanged();
        } 
        if (other.hasIndex())
          setIndex(other.getIndex()); 
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
            MysqlxExpr.DocumentPathItem.Type tmpValue;
            int tag = input.readTag();
            switch (tag) {
              case 0:
                done = true;
                continue;
              case 8:
                tmpRaw = input.readEnum();
                tmpValue = MysqlxExpr.DocumentPathItem.Type.forNumber(tmpRaw);
                if (tmpValue == null) {
                  mergeUnknownVarintField(1, tmpRaw);
                  continue;
                } 
                this.type_ = tmpRaw;
                this.bitField0_ |= 0x1;
                continue;
              case 18:
                this.value_ = input.readBytes();
                this.bitField0_ |= 0x2;
                continue;
              case 24:
                this.index_ = input.readUInt32();
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
      
      public MysqlxExpr.DocumentPathItem.Type getType() {
        MysqlxExpr.DocumentPathItem.Type result = MysqlxExpr.DocumentPathItem.Type.valueOf(this.type_);
        return (result == null) ? MysqlxExpr.DocumentPathItem.Type.MEMBER : result;
      }
      
      public Builder setType(MysqlxExpr.DocumentPathItem.Type value) {
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
      
      public boolean hasValue() {
        return ((this.bitField0_ & 0x2) != 0);
      }
      
      public String getValue() {
        Object ref = this.value_;
        if (!(ref instanceof String)) {
          ByteString bs = (ByteString)ref;
          String s = bs.toStringUtf8();
          if (bs.isValidUtf8())
            this.value_ = s; 
          return s;
        } 
        return (String)ref;
      }
      
      public ByteString getValueBytes() {
        Object ref = this.value_;
        if (ref instanceof String) {
          ByteString b = ByteString.copyFromUtf8((String)ref);
          this.value_ = b;
          return b;
        } 
        return (ByteString)ref;
      }
      
      public Builder setValue(String value) {
        if (value == null)
          throw new NullPointerException(); 
        this.bitField0_ |= 0x2;
        this.value_ = value;
        onChanged();
        return this;
      }
      
      public Builder clearValue() {
        this.bitField0_ &= 0xFFFFFFFD;
        this.value_ = MysqlxExpr.DocumentPathItem.getDefaultInstance().getValue();
        onChanged();
        return this;
      }
      
      public Builder setValueBytes(ByteString value) {
        if (value == null)
          throw new NullPointerException(); 
        this.bitField0_ |= 0x2;
        this.value_ = value;
        onChanged();
        return this;
      }
      
      public boolean hasIndex() {
        return ((this.bitField0_ & 0x4) != 0);
      }
      
      public int getIndex() {
        return this.index_;
      }
      
      public Builder setIndex(int value) {
        this.bitField0_ |= 0x4;
        this.index_ = value;
        onChanged();
        return this;
      }
      
      public Builder clearIndex() {
        this.bitField0_ &= 0xFFFFFFFB;
        this.index_ = 0;
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
    
    private static final DocumentPathItem DEFAULT_INSTANCE = new DocumentPathItem();
    
    public static DocumentPathItem getDefaultInstance() {
      return DEFAULT_INSTANCE;
    }
    
    @Deprecated
    public static final Parser<DocumentPathItem> PARSER = (Parser<DocumentPathItem>)new AbstractParser<DocumentPathItem>() {
        public MysqlxExpr.DocumentPathItem parsePartialFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
          MysqlxExpr.DocumentPathItem.Builder builder = MysqlxExpr.DocumentPathItem.newBuilder();
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
    
    public static Parser<DocumentPathItem> parser() {
      return PARSER;
    }
    
    public Parser<DocumentPathItem> getParserForType() {
      return PARSER;
    }
    
    public DocumentPathItem getDefaultInstanceForType() {
      return DEFAULT_INSTANCE;
    }
  }
  
  public static interface ColumnIdentifierOrBuilder extends MessageOrBuilder {
    List<MysqlxExpr.DocumentPathItem> getDocumentPathList();
    
    MysqlxExpr.DocumentPathItem getDocumentPath(int param1Int);
    
    int getDocumentPathCount();
    
    List<? extends MysqlxExpr.DocumentPathItemOrBuilder> getDocumentPathOrBuilderList();
    
    MysqlxExpr.DocumentPathItemOrBuilder getDocumentPathOrBuilder(int param1Int);
    
    boolean hasName();
    
    String getName();
    
    ByteString getNameBytes();
    
    boolean hasTableName();
    
    String getTableName();
    
    ByteString getTableNameBytes();
    
    boolean hasSchemaName();
    
    String getSchemaName();
    
    ByteString getSchemaNameBytes();
  }
  
  public static final class ColumnIdentifier extends GeneratedMessageV3 implements ColumnIdentifierOrBuilder {
    private static final long serialVersionUID = 0L;
    
    private int bitField0_;
    
    public static final int DOCUMENT_PATH_FIELD_NUMBER = 1;
    
    private List<MysqlxExpr.DocumentPathItem> documentPath_;
    
    public static final int NAME_FIELD_NUMBER = 2;
    
    private volatile Object name_;
    
    public static final int TABLE_NAME_FIELD_NUMBER = 3;
    
    private volatile Object tableName_;
    
    public static final int SCHEMA_NAME_FIELD_NUMBER = 4;
    
    private volatile Object schemaName_;
    
    private byte memoizedIsInitialized;
    
    private ColumnIdentifier(GeneratedMessageV3.Builder<?> builder) {
      super(builder);
      this.memoizedIsInitialized = -1;
    }
    
    private ColumnIdentifier() {
      this.memoizedIsInitialized = -1;
      this.documentPath_ = Collections.emptyList();
      this.name_ = "";
      this.tableName_ = "";
      this.schemaName_ = "";
    }
    
    protected Object newInstance(GeneratedMessageV3.UnusedPrivateParameter unused) {
      return new ColumnIdentifier();
    }
    
    public final UnknownFieldSet getUnknownFields() {
      return this.unknownFields;
    }
    
    public static final Descriptors.Descriptor getDescriptor() {
      return MysqlxExpr.internal_static_Mysqlx_Expr_ColumnIdentifier_descriptor;
    }
    
    protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() {
      return MysqlxExpr.internal_static_Mysqlx_Expr_ColumnIdentifier_fieldAccessorTable.ensureFieldAccessorsInitialized(ColumnIdentifier.class, Builder.class);
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
    
    public boolean hasTableName() {
      return ((this.bitField0_ & 0x2) != 0);
    }
    
    public String getTableName() {
      Object ref = this.tableName_;
      if (ref instanceof String)
        return (String)ref; 
      ByteString bs = (ByteString)ref;
      String s = bs.toStringUtf8();
      if (bs.isValidUtf8())
        this.tableName_ = s; 
      return s;
    }
    
    public ByteString getTableNameBytes() {
      Object ref = this.tableName_;
      if (ref instanceof String) {
        ByteString b = ByteString.copyFromUtf8((String)ref);
        this.tableName_ = b;
        return b;
      } 
      return (ByteString)ref;
    }
    
    public boolean hasSchemaName() {
      return ((this.bitField0_ & 0x4) != 0);
    }
    
    public String getSchemaName() {
      Object ref = this.schemaName_;
      if (ref instanceof String)
        return (String)ref; 
      ByteString bs = (ByteString)ref;
      String s = bs.toStringUtf8();
      if (bs.isValidUtf8())
        this.schemaName_ = s; 
      return s;
    }
    
    public ByteString getSchemaNameBytes() {
      Object ref = this.schemaName_;
      if (ref instanceof String) {
        ByteString b = ByteString.copyFromUtf8((String)ref);
        this.schemaName_ = b;
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
      for (int i = 0; i < this.documentPath_.size(); i++)
        output.writeMessage(1, (MessageLite)this.documentPath_.get(i)); 
      if ((this.bitField0_ & 0x1) != 0)
        GeneratedMessageV3.writeString(output, 2, this.name_); 
      if ((this.bitField0_ & 0x2) != 0)
        GeneratedMessageV3.writeString(output, 3, this.tableName_); 
      if ((this.bitField0_ & 0x4) != 0)
        GeneratedMessageV3.writeString(output, 4, this.schemaName_); 
      getUnknownFields().writeTo(output);
    }
    
    public int getSerializedSize() {
      int size = this.memoizedSize;
      if (size != -1)
        return size; 
      size = 0;
      for (int i = 0; i < this.documentPath_.size(); i++)
        size += 
          CodedOutputStream.computeMessageSize(1, (MessageLite)this.documentPath_.get(i)); 
      if ((this.bitField0_ & 0x1) != 0)
        size += GeneratedMessageV3.computeStringSize(2, this.name_); 
      if ((this.bitField0_ & 0x2) != 0)
        size += GeneratedMessageV3.computeStringSize(3, this.tableName_); 
      if ((this.bitField0_ & 0x4) != 0)
        size += GeneratedMessageV3.computeStringSize(4, this.schemaName_); 
      size += getUnknownFields().getSerializedSize();
      this.memoizedSize = size;
      return size;
    }
    
    public boolean equals(Object obj) {
      if (obj == this)
        return true; 
      if (!(obj instanceof ColumnIdentifier))
        return super.equals(obj); 
      ColumnIdentifier other = (ColumnIdentifier)obj;
      if (!getDocumentPathList().equals(other.getDocumentPathList()))
        return false; 
      if (hasName() != other.hasName())
        return false; 
      if (hasName() && 
        
        !getName().equals(other.getName()))
        return false; 
      if (hasTableName() != other.hasTableName())
        return false; 
      if (hasTableName() && 
        
        !getTableName().equals(other.getTableName()))
        return false; 
      if (hasSchemaName() != other.hasSchemaName())
        return false; 
      if (hasSchemaName() && 
        
        !getSchemaName().equals(other.getSchemaName()))
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
      if (getDocumentPathCount() > 0) {
        hash = 37 * hash + 1;
        hash = 53 * hash + getDocumentPathList().hashCode();
      } 
      if (hasName()) {
        hash = 37 * hash + 2;
        hash = 53 * hash + getName().hashCode();
      } 
      if (hasTableName()) {
        hash = 37 * hash + 3;
        hash = 53 * hash + getTableName().hashCode();
      } 
      if (hasSchemaName()) {
        hash = 37 * hash + 4;
        hash = 53 * hash + getSchemaName().hashCode();
      } 
      hash = 29 * hash + getUnknownFields().hashCode();
      this.memoizedHashCode = hash;
      return hash;
    }
    
    public static ColumnIdentifier parseFrom(ByteBuffer data) throws InvalidProtocolBufferException {
      return (ColumnIdentifier)PARSER.parseFrom(data);
    }
    
    public static ColumnIdentifier parseFrom(ByteBuffer data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
      return (ColumnIdentifier)PARSER.parseFrom(data, extensionRegistry);
    }
    
    public static ColumnIdentifier parseFrom(ByteString data) throws InvalidProtocolBufferException {
      return (ColumnIdentifier)PARSER.parseFrom(data);
    }
    
    public static ColumnIdentifier parseFrom(ByteString data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
      return (ColumnIdentifier)PARSER.parseFrom(data, extensionRegistry);
    }
    
    public static ColumnIdentifier parseFrom(byte[] data) throws InvalidProtocolBufferException {
      return (ColumnIdentifier)PARSER.parseFrom(data);
    }
    
    public static ColumnIdentifier parseFrom(byte[] data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
      return (ColumnIdentifier)PARSER.parseFrom(data, extensionRegistry);
    }
    
    public static ColumnIdentifier parseFrom(InputStream input) throws IOException {
      return 
        (ColumnIdentifier)GeneratedMessageV3.parseWithIOException(PARSER, input);
    }
    
    public static ColumnIdentifier parseFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
      return 
        (ColumnIdentifier)GeneratedMessageV3.parseWithIOException(PARSER, input, extensionRegistry);
    }
    
    public static ColumnIdentifier parseDelimitedFrom(InputStream input) throws IOException {
      return 
        (ColumnIdentifier)GeneratedMessageV3.parseDelimitedWithIOException(PARSER, input);
    }
    
    public static ColumnIdentifier parseDelimitedFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
      return 
        (ColumnIdentifier)GeneratedMessageV3.parseDelimitedWithIOException(PARSER, input, extensionRegistry);
    }
    
    public static ColumnIdentifier parseFrom(CodedInputStream input) throws IOException {
      return 
        (ColumnIdentifier)GeneratedMessageV3.parseWithIOException(PARSER, input);
    }
    
    public static ColumnIdentifier parseFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
      return 
        (ColumnIdentifier)GeneratedMessageV3.parseWithIOException(PARSER, input, extensionRegistry);
    }
    
    public Builder newBuilderForType() {
      return newBuilder();
    }
    
    public static Builder newBuilder() {
      return DEFAULT_INSTANCE.toBuilder();
    }
    
    public static Builder newBuilder(ColumnIdentifier prototype) {
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
    
    public static final class Builder extends GeneratedMessageV3.Builder<Builder> implements MysqlxExpr.ColumnIdentifierOrBuilder {
      private int bitField0_;
      
      private List<MysqlxExpr.DocumentPathItem> documentPath_;
      
      private RepeatedFieldBuilderV3<MysqlxExpr.DocumentPathItem, MysqlxExpr.DocumentPathItem.Builder, MysqlxExpr.DocumentPathItemOrBuilder> documentPathBuilder_;
      
      private Object name_;
      
      private Object tableName_;
      
      private Object schemaName_;
      
      public static final Descriptors.Descriptor getDescriptor() {
        return MysqlxExpr.internal_static_Mysqlx_Expr_ColumnIdentifier_descriptor;
      }
      
      protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() {
        return MysqlxExpr.internal_static_Mysqlx_Expr_ColumnIdentifier_fieldAccessorTable
          .ensureFieldAccessorsInitialized(MysqlxExpr.ColumnIdentifier.class, Builder.class);
      }
      
      private Builder() {
        this
          .documentPath_ = Collections.emptyList();
        this.name_ = "";
        this.tableName_ = "";
        this.schemaName_ = "";
      }
      
      private Builder(GeneratedMessageV3.BuilderParent parent) {
        super(parent);
        this.documentPath_ = Collections.emptyList();
        this.name_ = "";
        this.tableName_ = "";
        this.schemaName_ = "";
      }
      
      public Builder clear() {
        super.clear();
        if (this.documentPathBuilder_ == null) {
          this.documentPath_ = Collections.emptyList();
        } else {
          this.documentPath_ = null;
          this.documentPathBuilder_.clear();
        } 
        this.bitField0_ &= 0xFFFFFFFE;
        this.name_ = "";
        this.bitField0_ &= 0xFFFFFFFD;
        this.tableName_ = "";
        this.bitField0_ &= 0xFFFFFFFB;
        this.schemaName_ = "";
        this.bitField0_ &= 0xFFFFFFF7;
        return this;
      }
      
      public Descriptors.Descriptor getDescriptorForType() {
        return MysqlxExpr.internal_static_Mysqlx_Expr_ColumnIdentifier_descriptor;
      }
      
      public MysqlxExpr.ColumnIdentifier getDefaultInstanceForType() {
        return MysqlxExpr.ColumnIdentifier.getDefaultInstance();
      }
      
      public MysqlxExpr.ColumnIdentifier build() {
        MysqlxExpr.ColumnIdentifier result = buildPartial();
        if (!result.isInitialized())
          throw newUninitializedMessageException(result); 
        return result;
      }
      
      public MysqlxExpr.ColumnIdentifier buildPartial() {
        MysqlxExpr.ColumnIdentifier result = new MysqlxExpr.ColumnIdentifier(this);
        int from_bitField0_ = this.bitField0_;
        int to_bitField0_ = 0;
        if (this.documentPathBuilder_ == null) {
          if ((this.bitField0_ & 0x1) != 0) {
            this.documentPath_ = Collections.unmodifiableList(this.documentPath_);
            this.bitField0_ &= 0xFFFFFFFE;
          } 
          result.documentPath_ = this.documentPath_;
        } else {
          result.documentPath_ = this.documentPathBuilder_.build();
        } 
        if ((from_bitField0_ & 0x2) != 0)
          to_bitField0_ |= 0x1; 
        result.name_ = this.name_;
        if ((from_bitField0_ & 0x4) != 0)
          to_bitField0_ |= 0x2; 
        result.tableName_ = this.tableName_;
        if ((from_bitField0_ & 0x8) != 0)
          to_bitField0_ |= 0x4; 
        result.schemaName_ = this.schemaName_;
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
        if (other instanceof MysqlxExpr.ColumnIdentifier)
          return mergeFrom((MysqlxExpr.ColumnIdentifier)other); 
        super.mergeFrom(other);
        return this;
      }
      
      public Builder mergeFrom(MysqlxExpr.ColumnIdentifier other) {
        if (other == MysqlxExpr.ColumnIdentifier.getDefaultInstance())
          return this; 
        if (this.documentPathBuilder_ == null) {
          if (!other.documentPath_.isEmpty()) {
            if (this.documentPath_.isEmpty()) {
              this.documentPath_ = other.documentPath_;
              this.bitField0_ &= 0xFFFFFFFE;
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
            this.bitField0_ &= 0xFFFFFFFE;
            this.documentPathBuilder_ = MysqlxExpr.ColumnIdentifier.alwaysUseFieldBuilders ? getDocumentPathFieldBuilder() : null;
          } else {
            this.documentPathBuilder_.addAllMessages(other.documentPath_);
          } 
        } 
        if (other.hasName()) {
          this.bitField0_ |= 0x2;
          this.name_ = other.name_;
          onChanged();
        } 
        if (other.hasTableName()) {
          this.bitField0_ |= 0x4;
          this.tableName_ = other.tableName_;
          onChanged();
        } 
        if (other.hasSchemaName()) {
          this.bitField0_ |= 0x8;
          this.schemaName_ = other.schemaName_;
          onChanged();
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
                m = (MysqlxExpr.DocumentPathItem)input.readMessage(MysqlxExpr.DocumentPathItem.PARSER, extensionRegistry);
                if (this.documentPathBuilder_ == null) {
                  ensureDocumentPathIsMutable();
                  this.documentPath_.add(m);
                  continue;
                } 
                this.documentPathBuilder_.addMessage((AbstractMessage)m);
                continue;
              case 18:
                this.name_ = input.readBytes();
                this.bitField0_ |= 0x2;
                continue;
              case 26:
                this.tableName_ = input.readBytes();
                this.bitField0_ |= 0x4;
                continue;
              case 34:
                this.schemaName_ = input.readBytes();
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
      
      private void ensureDocumentPathIsMutable() {
        if ((this.bitField0_ & 0x1) == 0) {
          this.documentPath_ = new ArrayList<>(this.documentPath_);
          this.bitField0_ |= 0x1;
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
          this.bitField0_ &= 0xFFFFFFFE;
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
        return (MysqlxExpr.DocumentPathItem.Builder)getDocumentPathFieldBuilder().addBuilder((AbstractMessage)MysqlxExpr.DocumentPathItem.getDefaultInstance());
      }
      
      public MysqlxExpr.DocumentPathItem.Builder addDocumentPathBuilder(int index) {
        return (MysqlxExpr.DocumentPathItem.Builder)getDocumentPathFieldBuilder().addBuilder(index, (AbstractMessage)MysqlxExpr.DocumentPathItem.getDefaultInstance());
      }
      
      public List<MysqlxExpr.DocumentPathItem.Builder> getDocumentPathBuilderList() {
        return getDocumentPathFieldBuilder().getBuilderList();
      }
      
      private RepeatedFieldBuilderV3<MysqlxExpr.DocumentPathItem, MysqlxExpr.DocumentPathItem.Builder, MysqlxExpr.DocumentPathItemOrBuilder> getDocumentPathFieldBuilder() {
        if (this.documentPathBuilder_ == null) {
          this.documentPathBuilder_ = new RepeatedFieldBuilderV3(this.documentPath_, ((this.bitField0_ & 0x1) != 0), (AbstractMessage.BuilderParent)getParentForChildren(), isClean());
          this.documentPath_ = null;
        } 
        return this.documentPathBuilder_;
      }
      
      public boolean hasName() {
        return ((this.bitField0_ & 0x2) != 0);
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
        this.bitField0_ |= 0x2;
        this.name_ = value;
        onChanged();
        return this;
      }
      
      public Builder clearName() {
        this.bitField0_ &= 0xFFFFFFFD;
        this.name_ = MysqlxExpr.ColumnIdentifier.getDefaultInstance().getName();
        onChanged();
        return this;
      }
      
      public Builder setNameBytes(ByteString value) {
        if (value == null)
          throw new NullPointerException(); 
        this.bitField0_ |= 0x2;
        this.name_ = value;
        onChanged();
        return this;
      }
      
      public boolean hasTableName() {
        return ((this.bitField0_ & 0x4) != 0);
      }
      
      public String getTableName() {
        Object ref = this.tableName_;
        if (!(ref instanceof String)) {
          ByteString bs = (ByteString)ref;
          String s = bs.toStringUtf8();
          if (bs.isValidUtf8())
            this.tableName_ = s; 
          return s;
        } 
        return (String)ref;
      }
      
      public ByteString getTableNameBytes() {
        Object ref = this.tableName_;
        if (ref instanceof String) {
          ByteString b = ByteString.copyFromUtf8((String)ref);
          this.tableName_ = b;
          return b;
        } 
        return (ByteString)ref;
      }
      
      public Builder setTableName(String value) {
        if (value == null)
          throw new NullPointerException(); 
        this.bitField0_ |= 0x4;
        this.tableName_ = value;
        onChanged();
        return this;
      }
      
      public Builder clearTableName() {
        this.bitField0_ &= 0xFFFFFFFB;
        this.tableName_ = MysqlxExpr.ColumnIdentifier.getDefaultInstance().getTableName();
        onChanged();
        return this;
      }
      
      public Builder setTableNameBytes(ByteString value) {
        if (value == null)
          throw new NullPointerException(); 
        this.bitField0_ |= 0x4;
        this.tableName_ = value;
        onChanged();
        return this;
      }
      
      public boolean hasSchemaName() {
        return ((this.bitField0_ & 0x8) != 0);
      }
      
      public String getSchemaName() {
        Object ref = this.schemaName_;
        if (!(ref instanceof String)) {
          ByteString bs = (ByteString)ref;
          String s = bs.toStringUtf8();
          if (bs.isValidUtf8())
            this.schemaName_ = s; 
          return s;
        } 
        return (String)ref;
      }
      
      public ByteString getSchemaNameBytes() {
        Object ref = this.schemaName_;
        if (ref instanceof String) {
          ByteString b = ByteString.copyFromUtf8((String)ref);
          this.schemaName_ = b;
          return b;
        } 
        return (ByteString)ref;
      }
      
      public Builder setSchemaName(String value) {
        if (value == null)
          throw new NullPointerException(); 
        this.bitField0_ |= 0x8;
        this.schemaName_ = value;
        onChanged();
        return this;
      }
      
      public Builder clearSchemaName() {
        this.bitField0_ &= 0xFFFFFFF7;
        this.schemaName_ = MysqlxExpr.ColumnIdentifier.getDefaultInstance().getSchemaName();
        onChanged();
        return this;
      }
      
      public Builder setSchemaNameBytes(ByteString value) {
        if (value == null)
          throw new NullPointerException(); 
        this.bitField0_ |= 0x8;
        this.schemaName_ = value;
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
    
    private static final ColumnIdentifier DEFAULT_INSTANCE = new ColumnIdentifier();
    
    public static ColumnIdentifier getDefaultInstance() {
      return DEFAULT_INSTANCE;
    }
    
    @Deprecated
    public static final Parser<ColumnIdentifier> PARSER = (Parser<ColumnIdentifier>)new AbstractParser<ColumnIdentifier>() {
        public MysqlxExpr.ColumnIdentifier parsePartialFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
          MysqlxExpr.ColumnIdentifier.Builder builder = MysqlxExpr.ColumnIdentifier.newBuilder();
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
    
    public static Parser<ColumnIdentifier> parser() {
      return PARSER;
    }
    
    public Parser<ColumnIdentifier> getParserForType() {
      return PARSER;
    }
    
    public ColumnIdentifier getDefaultInstanceForType() {
      return DEFAULT_INSTANCE;
    }
  }
  
  public static interface FunctionCallOrBuilder extends MessageOrBuilder {
    boolean hasName();
    
    MysqlxExpr.Identifier getName();
    
    MysqlxExpr.IdentifierOrBuilder getNameOrBuilder();
    
    List<MysqlxExpr.Expr> getParamList();
    
    MysqlxExpr.Expr getParam(int param1Int);
    
    int getParamCount();
    
    List<? extends MysqlxExpr.ExprOrBuilder> getParamOrBuilderList();
    
    MysqlxExpr.ExprOrBuilder getParamOrBuilder(int param1Int);
  }
  
  public static final class FunctionCall extends GeneratedMessageV3 implements FunctionCallOrBuilder {
    private static final long serialVersionUID = 0L;
    
    private int bitField0_;
    
    public static final int NAME_FIELD_NUMBER = 1;
    
    private MysqlxExpr.Identifier name_;
    
    public static final int PARAM_FIELD_NUMBER = 2;
    
    private List<MysqlxExpr.Expr> param_;
    
    private byte memoizedIsInitialized;
    
    private FunctionCall(GeneratedMessageV3.Builder<?> builder) {
      super(builder);
      this.memoizedIsInitialized = -1;
    }
    
    private FunctionCall() {
      this.memoizedIsInitialized = -1;
      this.param_ = Collections.emptyList();
    }
    
    protected Object newInstance(GeneratedMessageV3.UnusedPrivateParameter unused) {
      return new FunctionCall();
    }
    
    public final UnknownFieldSet getUnknownFields() {
      return this.unknownFields;
    }
    
    public static final Descriptors.Descriptor getDescriptor() {
      return MysqlxExpr.internal_static_Mysqlx_Expr_FunctionCall_descriptor;
    }
    
    protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() {
      return MysqlxExpr.internal_static_Mysqlx_Expr_FunctionCall_fieldAccessorTable.ensureFieldAccessorsInitialized(FunctionCall.class, Builder.class);
    }
    
    public boolean hasName() {
      return ((this.bitField0_ & 0x1) != 0);
    }
    
    public MysqlxExpr.Identifier getName() {
      return (this.name_ == null) ? MysqlxExpr.Identifier.getDefaultInstance() : this.name_;
    }
    
    public MysqlxExpr.IdentifierOrBuilder getNameOrBuilder() {
      return (this.name_ == null) ? MysqlxExpr.Identifier.getDefaultInstance() : this.name_;
    }
    
    public List<MysqlxExpr.Expr> getParamList() {
      return this.param_;
    }
    
    public List<? extends MysqlxExpr.ExprOrBuilder> getParamOrBuilderList() {
      return (List)this.param_;
    }
    
    public int getParamCount() {
      return this.param_.size();
    }
    
    public MysqlxExpr.Expr getParam(int index) {
      return this.param_.get(index);
    }
    
    public MysqlxExpr.ExprOrBuilder getParamOrBuilder(int index) {
      return this.param_.get(index);
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
      if (!getName().isInitialized()) {
        this.memoizedIsInitialized = 0;
        return false;
      } 
      for (int i = 0; i < getParamCount(); i++) {
        if (!getParam(i).isInitialized()) {
          this.memoizedIsInitialized = 0;
          return false;
        } 
      } 
      this.memoizedIsInitialized = 1;
      return true;
    }
    
    public void writeTo(CodedOutputStream output) throws IOException {
      if ((this.bitField0_ & 0x1) != 0)
        output.writeMessage(1, (MessageLite)getName()); 
      for (int i = 0; i < this.param_.size(); i++)
        output.writeMessage(2, (MessageLite)this.param_.get(i)); 
      getUnknownFields().writeTo(output);
    }
    
    public int getSerializedSize() {
      int size = this.memoizedSize;
      if (size != -1)
        return size; 
      size = 0;
      if ((this.bitField0_ & 0x1) != 0)
        size += 
          CodedOutputStream.computeMessageSize(1, (MessageLite)getName()); 
      for (int i = 0; i < this.param_.size(); i++)
        size += 
          CodedOutputStream.computeMessageSize(2, (MessageLite)this.param_.get(i)); 
      size += getUnknownFields().getSerializedSize();
      this.memoizedSize = size;
      return size;
    }
    
    public boolean equals(Object obj) {
      if (obj == this)
        return true; 
      if (!(obj instanceof FunctionCall))
        return super.equals(obj); 
      FunctionCall other = (FunctionCall)obj;
      if (hasName() != other.hasName())
        return false; 
      if (hasName() && 
        
        !getName().equals(other.getName()))
        return false; 
      if (!getParamList().equals(other.getParamList()))
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
      if (getParamCount() > 0) {
        hash = 37 * hash + 2;
        hash = 53 * hash + getParamList().hashCode();
      } 
      hash = 29 * hash + getUnknownFields().hashCode();
      this.memoizedHashCode = hash;
      return hash;
    }
    
    public static FunctionCall parseFrom(ByteBuffer data) throws InvalidProtocolBufferException {
      return (FunctionCall)PARSER.parseFrom(data);
    }
    
    public static FunctionCall parseFrom(ByteBuffer data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
      return (FunctionCall)PARSER.parseFrom(data, extensionRegistry);
    }
    
    public static FunctionCall parseFrom(ByteString data) throws InvalidProtocolBufferException {
      return (FunctionCall)PARSER.parseFrom(data);
    }
    
    public static FunctionCall parseFrom(ByteString data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
      return (FunctionCall)PARSER.parseFrom(data, extensionRegistry);
    }
    
    public static FunctionCall parseFrom(byte[] data) throws InvalidProtocolBufferException {
      return (FunctionCall)PARSER.parseFrom(data);
    }
    
    public static FunctionCall parseFrom(byte[] data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
      return (FunctionCall)PARSER.parseFrom(data, extensionRegistry);
    }
    
    public static FunctionCall parseFrom(InputStream input) throws IOException {
      return 
        (FunctionCall)GeneratedMessageV3.parseWithIOException(PARSER, input);
    }
    
    public static FunctionCall parseFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
      return 
        (FunctionCall)GeneratedMessageV3.parseWithIOException(PARSER, input, extensionRegistry);
    }
    
    public static FunctionCall parseDelimitedFrom(InputStream input) throws IOException {
      return 
        (FunctionCall)GeneratedMessageV3.parseDelimitedWithIOException(PARSER, input);
    }
    
    public static FunctionCall parseDelimitedFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
      return 
        (FunctionCall)GeneratedMessageV3.parseDelimitedWithIOException(PARSER, input, extensionRegistry);
    }
    
    public static FunctionCall parseFrom(CodedInputStream input) throws IOException {
      return 
        (FunctionCall)GeneratedMessageV3.parseWithIOException(PARSER, input);
    }
    
    public static FunctionCall parseFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
      return 
        (FunctionCall)GeneratedMessageV3.parseWithIOException(PARSER, input, extensionRegistry);
    }
    
    public Builder newBuilderForType() {
      return newBuilder();
    }
    
    public static Builder newBuilder() {
      return DEFAULT_INSTANCE.toBuilder();
    }
    
    public static Builder newBuilder(FunctionCall prototype) {
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
    
    public static final class Builder extends GeneratedMessageV3.Builder<Builder> implements MysqlxExpr.FunctionCallOrBuilder {
      private int bitField0_;
      
      private MysqlxExpr.Identifier name_;
      
      private SingleFieldBuilderV3<MysqlxExpr.Identifier, MysqlxExpr.Identifier.Builder, MysqlxExpr.IdentifierOrBuilder> nameBuilder_;
      
      private List<MysqlxExpr.Expr> param_;
      
      private RepeatedFieldBuilderV3<MysqlxExpr.Expr, MysqlxExpr.Expr.Builder, MysqlxExpr.ExprOrBuilder> paramBuilder_;
      
      public static final Descriptors.Descriptor getDescriptor() {
        return MysqlxExpr.internal_static_Mysqlx_Expr_FunctionCall_descriptor;
      }
      
      protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() {
        return MysqlxExpr.internal_static_Mysqlx_Expr_FunctionCall_fieldAccessorTable
          .ensureFieldAccessorsInitialized(MysqlxExpr.FunctionCall.class, Builder.class);
      }
      
      private Builder() {
        this
          .param_ = Collections.emptyList();
        maybeForceBuilderInitialization();
      }
      
      private Builder(GeneratedMessageV3.BuilderParent parent) {
        super(parent);
        this.param_ = Collections.emptyList();
        maybeForceBuilderInitialization();
      }
      
      private void maybeForceBuilderInitialization() {
        if (MysqlxExpr.FunctionCall.alwaysUseFieldBuilders) {
          getNameFieldBuilder();
          getParamFieldBuilder();
        } 
      }
      
      public Builder clear() {
        super.clear();
        if (this.nameBuilder_ == null) {
          this.name_ = null;
        } else {
          this.nameBuilder_.clear();
        } 
        this.bitField0_ &= 0xFFFFFFFE;
        if (this.paramBuilder_ == null) {
          this.param_ = Collections.emptyList();
        } else {
          this.param_ = null;
          this.paramBuilder_.clear();
        } 
        this.bitField0_ &= 0xFFFFFFFD;
        return this;
      }
      
      public Descriptors.Descriptor getDescriptorForType() {
        return MysqlxExpr.internal_static_Mysqlx_Expr_FunctionCall_descriptor;
      }
      
      public MysqlxExpr.FunctionCall getDefaultInstanceForType() {
        return MysqlxExpr.FunctionCall.getDefaultInstance();
      }
      
      public MysqlxExpr.FunctionCall build() {
        MysqlxExpr.FunctionCall result = buildPartial();
        if (!result.isInitialized())
          throw newUninitializedMessageException(result); 
        return result;
      }
      
      public MysqlxExpr.FunctionCall buildPartial() {
        MysqlxExpr.FunctionCall result = new MysqlxExpr.FunctionCall(this);
        int from_bitField0_ = this.bitField0_;
        int to_bitField0_ = 0;
        if ((from_bitField0_ & 0x1) != 0) {
          if (this.nameBuilder_ == null) {
            result.name_ = this.name_;
          } else {
            result.name_ = (MysqlxExpr.Identifier)this.nameBuilder_.build();
          } 
          to_bitField0_ |= 0x1;
        } 
        if (this.paramBuilder_ == null) {
          if ((this.bitField0_ & 0x2) != 0) {
            this.param_ = Collections.unmodifiableList(this.param_);
            this.bitField0_ &= 0xFFFFFFFD;
          } 
          result.param_ = this.param_;
        } else {
          result.param_ = this.paramBuilder_.build();
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
        if (other instanceof MysqlxExpr.FunctionCall)
          return mergeFrom((MysqlxExpr.FunctionCall)other); 
        super.mergeFrom(other);
        return this;
      }
      
      public Builder mergeFrom(MysqlxExpr.FunctionCall other) {
        if (other == MysqlxExpr.FunctionCall.getDefaultInstance())
          return this; 
        if (other.hasName())
          mergeName(other.getName()); 
        if (this.paramBuilder_ == null) {
          if (!other.param_.isEmpty()) {
            if (this.param_.isEmpty()) {
              this.param_ = other.param_;
              this.bitField0_ &= 0xFFFFFFFD;
            } else {
              ensureParamIsMutable();
              this.param_.addAll(other.param_);
            } 
            onChanged();
          } 
        } else if (!other.param_.isEmpty()) {
          if (this.paramBuilder_.isEmpty()) {
            this.paramBuilder_.dispose();
            this.paramBuilder_ = null;
            this.param_ = other.param_;
            this.bitField0_ &= 0xFFFFFFFD;
            this.paramBuilder_ = MysqlxExpr.FunctionCall.alwaysUseFieldBuilders ? getParamFieldBuilder() : null;
          } else {
            this.paramBuilder_.addAllMessages(other.param_);
          } 
        } 
        mergeUnknownFields(other.getUnknownFields());
        onChanged();
        return this;
      }
      
      public final boolean isInitialized() {
        if (!hasName())
          return false; 
        if (!getName().isInitialized())
          return false; 
        for (int i = 0; i < getParamCount(); i++) {
          if (!getParam(i).isInitialized())
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
                input.readMessage((MessageLite.Builder)getNameFieldBuilder().getBuilder(), extensionRegistry);
                this.bitField0_ |= 0x1;
                continue;
              case 18:
                m = (MysqlxExpr.Expr)input.readMessage(MysqlxExpr.Expr.PARSER, extensionRegistry);
                if (this.paramBuilder_ == null) {
                  ensureParamIsMutable();
                  this.param_.add(m);
                  continue;
                } 
                this.paramBuilder_.addMessage((AbstractMessage)m);
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
      
      public MysqlxExpr.Identifier getName() {
        if (this.nameBuilder_ == null)
          return (this.name_ == null) ? MysqlxExpr.Identifier.getDefaultInstance() : this.name_; 
        return (MysqlxExpr.Identifier)this.nameBuilder_.getMessage();
      }
      
      public Builder setName(MysqlxExpr.Identifier value) {
        if (this.nameBuilder_ == null) {
          if (value == null)
            throw new NullPointerException(); 
          this.name_ = value;
          onChanged();
        } else {
          this.nameBuilder_.setMessage((AbstractMessage)value);
        } 
        this.bitField0_ |= 0x1;
        return this;
      }
      
      public Builder setName(MysqlxExpr.Identifier.Builder builderForValue) {
        if (this.nameBuilder_ == null) {
          this.name_ = builderForValue.build();
          onChanged();
        } else {
          this.nameBuilder_.setMessage((AbstractMessage)builderForValue.build());
        } 
        this.bitField0_ |= 0x1;
        return this;
      }
      
      public Builder mergeName(MysqlxExpr.Identifier value) {
        if (this.nameBuilder_ == null) {
          if ((this.bitField0_ & 0x1) != 0 && this.name_ != null && this.name_ != MysqlxExpr.Identifier.getDefaultInstance()) {
            this.name_ = MysqlxExpr.Identifier.newBuilder(this.name_).mergeFrom(value).buildPartial();
          } else {
            this.name_ = value;
          } 
          onChanged();
        } else {
          this.nameBuilder_.mergeFrom((AbstractMessage)value);
        } 
        this.bitField0_ |= 0x1;
        return this;
      }
      
      public Builder clearName() {
        if (this.nameBuilder_ == null) {
          this.name_ = null;
          onChanged();
        } else {
          this.nameBuilder_.clear();
        } 
        this.bitField0_ &= 0xFFFFFFFE;
        return this;
      }
      
      public MysqlxExpr.Identifier.Builder getNameBuilder() {
        this.bitField0_ |= 0x1;
        onChanged();
        return (MysqlxExpr.Identifier.Builder)getNameFieldBuilder().getBuilder();
      }
      
      public MysqlxExpr.IdentifierOrBuilder getNameOrBuilder() {
        if (this.nameBuilder_ != null)
          return (MysqlxExpr.IdentifierOrBuilder)this.nameBuilder_.getMessageOrBuilder(); 
        return (this.name_ == null) ? MysqlxExpr.Identifier.getDefaultInstance() : this.name_;
      }
      
      private SingleFieldBuilderV3<MysqlxExpr.Identifier, MysqlxExpr.Identifier.Builder, MysqlxExpr.IdentifierOrBuilder> getNameFieldBuilder() {
        if (this.nameBuilder_ == null) {
          this.nameBuilder_ = new SingleFieldBuilderV3((AbstractMessage)getName(), (AbstractMessage.BuilderParent)getParentForChildren(), isClean());
          this.name_ = null;
        } 
        return this.nameBuilder_;
      }
      
      private void ensureParamIsMutable() {
        if ((this.bitField0_ & 0x2) == 0) {
          this.param_ = new ArrayList<>(this.param_);
          this.bitField0_ |= 0x2;
        } 
      }
      
      public List<MysqlxExpr.Expr> getParamList() {
        if (this.paramBuilder_ == null)
          return Collections.unmodifiableList(this.param_); 
        return this.paramBuilder_.getMessageList();
      }
      
      public int getParamCount() {
        if (this.paramBuilder_ == null)
          return this.param_.size(); 
        return this.paramBuilder_.getCount();
      }
      
      public MysqlxExpr.Expr getParam(int index) {
        if (this.paramBuilder_ == null)
          return this.param_.get(index); 
        return (MysqlxExpr.Expr)this.paramBuilder_.getMessage(index);
      }
      
      public Builder setParam(int index, MysqlxExpr.Expr value) {
        if (this.paramBuilder_ == null) {
          if (value == null)
            throw new NullPointerException(); 
          ensureParamIsMutable();
          this.param_.set(index, value);
          onChanged();
        } else {
          this.paramBuilder_.setMessage(index, (AbstractMessage)value);
        } 
        return this;
      }
      
      public Builder setParam(int index, MysqlxExpr.Expr.Builder builderForValue) {
        if (this.paramBuilder_ == null) {
          ensureParamIsMutable();
          this.param_.set(index, builderForValue.build());
          onChanged();
        } else {
          this.paramBuilder_.setMessage(index, (AbstractMessage)builderForValue.build());
        } 
        return this;
      }
      
      public Builder addParam(MysqlxExpr.Expr value) {
        if (this.paramBuilder_ == null) {
          if (value == null)
            throw new NullPointerException(); 
          ensureParamIsMutable();
          this.param_.add(value);
          onChanged();
        } else {
          this.paramBuilder_.addMessage((AbstractMessage)value);
        } 
        return this;
      }
      
      public Builder addParam(int index, MysqlxExpr.Expr value) {
        if (this.paramBuilder_ == null) {
          if (value == null)
            throw new NullPointerException(); 
          ensureParamIsMutable();
          this.param_.add(index, value);
          onChanged();
        } else {
          this.paramBuilder_.addMessage(index, (AbstractMessage)value);
        } 
        return this;
      }
      
      public Builder addParam(MysqlxExpr.Expr.Builder builderForValue) {
        if (this.paramBuilder_ == null) {
          ensureParamIsMutable();
          this.param_.add(builderForValue.build());
          onChanged();
        } else {
          this.paramBuilder_.addMessage((AbstractMessage)builderForValue.build());
        } 
        return this;
      }
      
      public Builder addParam(int index, MysqlxExpr.Expr.Builder builderForValue) {
        if (this.paramBuilder_ == null) {
          ensureParamIsMutable();
          this.param_.add(index, builderForValue.build());
          onChanged();
        } else {
          this.paramBuilder_.addMessage(index, (AbstractMessage)builderForValue.build());
        } 
        return this;
      }
      
      public Builder addAllParam(Iterable<? extends MysqlxExpr.Expr> values) {
        if (this.paramBuilder_ == null) {
          ensureParamIsMutable();
          AbstractMessageLite.Builder.addAll(values, this.param_);
          onChanged();
        } else {
          this.paramBuilder_.addAllMessages(values);
        } 
        return this;
      }
      
      public Builder clearParam() {
        if (this.paramBuilder_ == null) {
          this.param_ = Collections.emptyList();
          this.bitField0_ &= 0xFFFFFFFD;
          onChanged();
        } else {
          this.paramBuilder_.clear();
        } 
        return this;
      }
      
      public Builder removeParam(int index) {
        if (this.paramBuilder_ == null) {
          ensureParamIsMutable();
          this.param_.remove(index);
          onChanged();
        } else {
          this.paramBuilder_.remove(index);
        } 
        return this;
      }
      
      public MysqlxExpr.Expr.Builder getParamBuilder(int index) {
        return (MysqlxExpr.Expr.Builder)getParamFieldBuilder().getBuilder(index);
      }
      
      public MysqlxExpr.ExprOrBuilder getParamOrBuilder(int index) {
        if (this.paramBuilder_ == null)
          return this.param_.get(index); 
        return (MysqlxExpr.ExprOrBuilder)this.paramBuilder_.getMessageOrBuilder(index);
      }
      
      public List<? extends MysqlxExpr.ExprOrBuilder> getParamOrBuilderList() {
        if (this.paramBuilder_ != null)
          return this.paramBuilder_.getMessageOrBuilderList(); 
        return Collections.unmodifiableList((List)this.param_);
      }
      
      public MysqlxExpr.Expr.Builder addParamBuilder() {
        return (MysqlxExpr.Expr.Builder)getParamFieldBuilder().addBuilder(
            (AbstractMessage)MysqlxExpr.Expr.getDefaultInstance());
      }
      
      public MysqlxExpr.Expr.Builder addParamBuilder(int index) {
        return (MysqlxExpr.Expr.Builder)getParamFieldBuilder().addBuilder(index, 
            (AbstractMessage)MysqlxExpr.Expr.getDefaultInstance());
      }
      
      public List<MysqlxExpr.Expr.Builder> getParamBuilderList() {
        return getParamFieldBuilder().getBuilderList();
      }
      
      private RepeatedFieldBuilderV3<MysqlxExpr.Expr, MysqlxExpr.Expr.Builder, MysqlxExpr.ExprOrBuilder> getParamFieldBuilder() {
        if (this.paramBuilder_ == null) {
          this
            
            .paramBuilder_ = new RepeatedFieldBuilderV3(this.param_, ((this.bitField0_ & 0x2) != 0), (AbstractMessage.BuilderParent)getParentForChildren(), isClean());
          this.param_ = null;
        } 
        return this.paramBuilder_;
      }
      
      public final Builder setUnknownFields(UnknownFieldSet unknownFields) {
        return (Builder)super.setUnknownFields(unknownFields);
      }
      
      public final Builder mergeUnknownFields(UnknownFieldSet unknownFields) {
        return (Builder)super.mergeUnknownFields(unknownFields);
      }
    }
    
    private static final FunctionCall DEFAULT_INSTANCE = new FunctionCall();
    
    public static FunctionCall getDefaultInstance() {
      return DEFAULT_INSTANCE;
    }
    
    @Deprecated
    public static final Parser<FunctionCall> PARSER = (Parser<FunctionCall>)new AbstractParser<FunctionCall>() {
        public MysqlxExpr.FunctionCall parsePartialFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
          MysqlxExpr.FunctionCall.Builder builder = MysqlxExpr.FunctionCall.newBuilder();
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
    
    public static Parser<FunctionCall> parser() {
      return PARSER;
    }
    
    public Parser<FunctionCall> getParserForType() {
      return PARSER;
    }
    
    public FunctionCall getDefaultInstanceForType() {
      return DEFAULT_INSTANCE;
    }
  }
  
  public static interface OperatorOrBuilder extends MessageOrBuilder {
    boolean hasName();
    
    String getName();
    
    ByteString getNameBytes();
    
    List<MysqlxExpr.Expr> getParamList();
    
    MysqlxExpr.Expr getParam(int param1Int);
    
    int getParamCount();
    
    List<? extends MysqlxExpr.ExprOrBuilder> getParamOrBuilderList();
    
    MysqlxExpr.ExprOrBuilder getParamOrBuilder(int param1Int);
  }
  
  public static final class Operator extends GeneratedMessageV3 implements OperatorOrBuilder {
    private static final long serialVersionUID = 0L;
    
    private int bitField0_;
    
    public static final int NAME_FIELD_NUMBER = 1;
    
    private volatile Object name_;
    
    public static final int PARAM_FIELD_NUMBER = 2;
    
    private List<MysqlxExpr.Expr> param_;
    
    private byte memoizedIsInitialized;
    
    private Operator(GeneratedMessageV3.Builder<?> builder) {
      super(builder);
      this.memoizedIsInitialized = -1;
    }
    
    private Operator() {
      this.memoizedIsInitialized = -1;
      this.name_ = "";
      this.param_ = Collections.emptyList();
    }
    
    protected Object newInstance(GeneratedMessageV3.UnusedPrivateParameter unused) {
      return new Operator();
    }
    
    public final UnknownFieldSet getUnknownFields() {
      return this.unknownFields;
    }
    
    public static final Descriptors.Descriptor getDescriptor() {
      return MysqlxExpr.internal_static_Mysqlx_Expr_Operator_descriptor;
    }
    
    protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() {
      return MysqlxExpr.internal_static_Mysqlx_Expr_Operator_fieldAccessorTable.ensureFieldAccessorsInitialized(Operator.class, Builder.class);
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
    
    public List<MysqlxExpr.Expr> getParamList() {
      return this.param_;
    }
    
    public List<? extends MysqlxExpr.ExprOrBuilder> getParamOrBuilderList() {
      return (List)this.param_;
    }
    
    public int getParamCount() {
      return this.param_.size();
    }
    
    public MysqlxExpr.Expr getParam(int index) {
      return this.param_.get(index);
    }
    
    public MysqlxExpr.ExprOrBuilder getParamOrBuilder(int index) {
      return this.param_.get(index);
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
      for (int i = 0; i < getParamCount(); i++) {
        if (!getParam(i).isInitialized()) {
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
      for (int i = 0; i < this.param_.size(); i++)
        output.writeMessage(2, (MessageLite)this.param_.get(i)); 
      getUnknownFields().writeTo(output);
    }
    
    public int getSerializedSize() {
      int size = this.memoizedSize;
      if (size != -1)
        return size; 
      size = 0;
      if ((this.bitField0_ & 0x1) != 0)
        size += GeneratedMessageV3.computeStringSize(1, this.name_); 
      for (int i = 0; i < this.param_.size(); i++)
        size += 
          CodedOutputStream.computeMessageSize(2, (MessageLite)this.param_.get(i)); 
      size += getUnknownFields().getSerializedSize();
      this.memoizedSize = size;
      return size;
    }
    
    public boolean equals(Object obj) {
      if (obj == this)
        return true; 
      if (!(obj instanceof Operator))
        return super.equals(obj); 
      Operator other = (Operator)obj;
      if (hasName() != other.hasName())
        return false; 
      if (hasName() && 
        
        !getName().equals(other.getName()))
        return false; 
      if (!getParamList().equals(other.getParamList()))
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
      if (getParamCount() > 0) {
        hash = 37 * hash + 2;
        hash = 53 * hash + getParamList().hashCode();
      } 
      hash = 29 * hash + getUnknownFields().hashCode();
      this.memoizedHashCode = hash;
      return hash;
    }
    
    public static Operator parseFrom(ByteBuffer data) throws InvalidProtocolBufferException {
      return (Operator)PARSER.parseFrom(data);
    }
    
    public static Operator parseFrom(ByteBuffer data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
      return (Operator)PARSER.parseFrom(data, extensionRegistry);
    }
    
    public static Operator parseFrom(ByteString data) throws InvalidProtocolBufferException {
      return (Operator)PARSER.parseFrom(data);
    }
    
    public static Operator parseFrom(ByteString data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
      return (Operator)PARSER.parseFrom(data, extensionRegistry);
    }
    
    public static Operator parseFrom(byte[] data) throws InvalidProtocolBufferException {
      return (Operator)PARSER.parseFrom(data);
    }
    
    public static Operator parseFrom(byte[] data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
      return (Operator)PARSER.parseFrom(data, extensionRegistry);
    }
    
    public static Operator parseFrom(InputStream input) throws IOException {
      return 
        (Operator)GeneratedMessageV3.parseWithIOException(PARSER, input);
    }
    
    public static Operator parseFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
      return 
        (Operator)GeneratedMessageV3.parseWithIOException(PARSER, input, extensionRegistry);
    }
    
    public static Operator parseDelimitedFrom(InputStream input) throws IOException {
      return 
        (Operator)GeneratedMessageV3.parseDelimitedWithIOException(PARSER, input);
    }
    
    public static Operator parseDelimitedFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
      return 
        (Operator)GeneratedMessageV3.parseDelimitedWithIOException(PARSER, input, extensionRegistry);
    }
    
    public static Operator parseFrom(CodedInputStream input) throws IOException {
      return 
        (Operator)GeneratedMessageV3.parseWithIOException(PARSER, input);
    }
    
    public static Operator parseFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
      return 
        (Operator)GeneratedMessageV3.parseWithIOException(PARSER, input, extensionRegistry);
    }
    
    public Builder newBuilderForType() {
      return newBuilder();
    }
    
    public static Builder newBuilder() {
      return DEFAULT_INSTANCE.toBuilder();
    }
    
    public static Builder newBuilder(Operator prototype) {
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
    
    public static final class Builder extends GeneratedMessageV3.Builder<Builder> implements MysqlxExpr.OperatorOrBuilder {
      private int bitField0_;
      
      private Object name_;
      
      private List<MysqlxExpr.Expr> param_;
      
      private RepeatedFieldBuilderV3<MysqlxExpr.Expr, MysqlxExpr.Expr.Builder, MysqlxExpr.ExprOrBuilder> paramBuilder_;
      
      public static final Descriptors.Descriptor getDescriptor() {
        return MysqlxExpr.internal_static_Mysqlx_Expr_Operator_descriptor;
      }
      
      protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() {
        return MysqlxExpr.internal_static_Mysqlx_Expr_Operator_fieldAccessorTable
          .ensureFieldAccessorsInitialized(MysqlxExpr.Operator.class, Builder.class);
      }
      
      private Builder() {
        this.name_ = "";
        this
          .param_ = Collections.emptyList();
      }
      
      private Builder(GeneratedMessageV3.BuilderParent parent) {
        super(parent);
        this.name_ = "";
        this.param_ = Collections.emptyList();
      }
      
      public Builder clear() {
        super.clear();
        this.name_ = "";
        this.bitField0_ &= 0xFFFFFFFE;
        if (this.paramBuilder_ == null) {
          this.param_ = Collections.emptyList();
        } else {
          this.param_ = null;
          this.paramBuilder_.clear();
        } 
        this.bitField0_ &= 0xFFFFFFFD;
        return this;
      }
      
      public Descriptors.Descriptor getDescriptorForType() {
        return MysqlxExpr.internal_static_Mysqlx_Expr_Operator_descriptor;
      }
      
      public MysqlxExpr.Operator getDefaultInstanceForType() {
        return MysqlxExpr.Operator.getDefaultInstance();
      }
      
      public MysqlxExpr.Operator build() {
        MysqlxExpr.Operator result = buildPartial();
        if (!result.isInitialized())
          throw newUninitializedMessageException(result); 
        return result;
      }
      
      public MysqlxExpr.Operator buildPartial() {
        MysqlxExpr.Operator result = new MysqlxExpr.Operator(this);
        int from_bitField0_ = this.bitField0_;
        int to_bitField0_ = 0;
        if ((from_bitField0_ & 0x1) != 0)
          to_bitField0_ |= 0x1; 
        result.name_ = this.name_;
        if (this.paramBuilder_ == null) {
          if ((this.bitField0_ & 0x2) != 0) {
            this.param_ = Collections.unmodifiableList(this.param_);
            this.bitField0_ &= 0xFFFFFFFD;
          } 
          result.param_ = this.param_;
        } else {
          result.param_ = this.paramBuilder_.build();
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
        if (other instanceof MysqlxExpr.Operator)
          return mergeFrom((MysqlxExpr.Operator)other); 
        super.mergeFrom(other);
        return this;
      }
      
      public Builder mergeFrom(MysqlxExpr.Operator other) {
        if (other == MysqlxExpr.Operator.getDefaultInstance())
          return this; 
        if (other.hasName()) {
          this.bitField0_ |= 0x1;
          this.name_ = other.name_;
          onChanged();
        } 
        if (this.paramBuilder_ == null) {
          if (!other.param_.isEmpty()) {
            if (this.param_.isEmpty()) {
              this.param_ = other.param_;
              this.bitField0_ &= 0xFFFFFFFD;
            } else {
              ensureParamIsMutable();
              this.param_.addAll(other.param_);
            } 
            onChanged();
          } 
        } else if (!other.param_.isEmpty()) {
          if (this.paramBuilder_.isEmpty()) {
            this.paramBuilder_.dispose();
            this.paramBuilder_ = null;
            this.param_ = other.param_;
            this.bitField0_ &= 0xFFFFFFFD;
            this.paramBuilder_ = MysqlxExpr.Operator.alwaysUseFieldBuilders ? getParamFieldBuilder() : null;
          } else {
            this.paramBuilder_.addAllMessages(other.param_);
          } 
        } 
        mergeUnknownFields(other.getUnknownFields());
        onChanged();
        return this;
      }
      
      public final boolean isInitialized() {
        if (!hasName())
          return false; 
        for (int i = 0; i < getParamCount(); i++) {
          if (!getParam(i).isInitialized())
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
                this.name_ = input.readBytes();
                this.bitField0_ |= 0x1;
                continue;
              case 18:
                m = (MysqlxExpr.Expr)input.readMessage(MysqlxExpr.Expr.PARSER, extensionRegistry);
                if (this.paramBuilder_ == null) {
                  ensureParamIsMutable();
                  this.param_.add(m);
                  continue;
                } 
                this.paramBuilder_.addMessage((AbstractMessage)m);
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
        this.name_ = MysqlxExpr.Operator.getDefaultInstance().getName();
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
      
      private void ensureParamIsMutable() {
        if ((this.bitField0_ & 0x2) == 0) {
          this.param_ = new ArrayList<>(this.param_);
          this.bitField0_ |= 0x2;
        } 
      }
      
      public List<MysqlxExpr.Expr> getParamList() {
        if (this.paramBuilder_ == null)
          return Collections.unmodifiableList(this.param_); 
        return this.paramBuilder_.getMessageList();
      }
      
      public int getParamCount() {
        if (this.paramBuilder_ == null)
          return this.param_.size(); 
        return this.paramBuilder_.getCount();
      }
      
      public MysqlxExpr.Expr getParam(int index) {
        if (this.paramBuilder_ == null)
          return this.param_.get(index); 
        return (MysqlxExpr.Expr)this.paramBuilder_.getMessage(index);
      }
      
      public Builder setParam(int index, MysqlxExpr.Expr value) {
        if (this.paramBuilder_ == null) {
          if (value == null)
            throw new NullPointerException(); 
          ensureParamIsMutable();
          this.param_.set(index, value);
          onChanged();
        } else {
          this.paramBuilder_.setMessage(index, (AbstractMessage)value);
        } 
        return this;
      }
      
      public Builder setParam(int index, MysqlxExpr.Expr.Builder builderForValue) {
        if (this.paramBuilder_ == null) {
          ensureParamIsMutable();
          this.param_.set(index, builderForValue.build());
          onChanged();
        } else {
          this.paramBuilder_.setMessage(index, (AbstractMessage)builderForValue.build());
        } 
        return this;
      }
      
      public Builder addParam(MysqlxExpr.Expr value) {
        if (this.paramBuilder_ == null) {
          if (value == null)
            throw new NullPointerException(); 
          ensureParamIsMutable();
          this.param_.add(value);
          onChanged();
        } else {
          this.paramBuilder_.addMessage((AbstractMessage)value);
        } 
        return this;
      }
      
      public Builder addParam(int index, MysqlxExpr.Expr value) {
        if (this.paramBuilder_ == null) {
          if (value == null)
            throw new NullPointerException(); 
          ensureParamIsMutable();
          this.param_.add(index, value);
          onChanged();
        } else {
          this.paramBuilder_.addMessage(index, (AbstractMessage)value);
        } 
        return this;
      }
      
      public Builder addParam(MysqlxExpr.Expr.Builder builderForValue) {
        if (this.paramBuilder_ == null) {
          ensureParamIsMutable();
          this.param_.add(builderForValue.build());
          onChanged();
        } else {
          this.paramBuilder_.addMessage((AbstractMessage)builderForValue.build());
        } 
        return this;
      }
      
      public Builder addParam(int index, MysqlxExpr.Expr.Builder builderForValue) {
        if (this.paramBuilder_ == null) {
          ensureParamIsMutable();
          this.param_.add(index, builderForValue.build());
          onChanged();
        } else {
          this.paramBuilder_.addMessage(index, (AbstractMessage)builderForValue.build());
        } 
        return this;
      }
      
      public Builder addAllParam(Iterable<? extends MysqlxExpr.Expr> values) {
        if (this.paramBuilder_ == null) {
          ensureParamIsMutable();
          AbstractMessageLite.Builder.addAll(values, this.param_);
          onChanged();
        } else {
          this.paramBuilder_.addAllMessages(values);
        } 
        return this;
      }
      
      public Builder clearParam() {
        if (this.paramBuilder_ == null) {
          this.param_ = Collections.emptyList();
          this.bitField0_ &= 0xFFFFFFFD;
          onChanged();
        } else {
          this.paramBuilder_.clear();
        } 
        return this;
      }
      
      public Builder removeParam(int index) {
        if (this.paramBuilder_ == null) {
          ensureParamIsMutable();
          this.param_.remove(index);
          onChanged();
        } else {
          this.paramBuilder_.remove(index);
        } 
        return this;
      }
      
      public MysqlxExpr.Expr.Builder getParamBuilder(int index) {
        return (MysqlxExpr.Expr.Builder)getParamFieldBuilder().getBuilder(index);
      }
      
      public MysqlxExpr.ExprOrBuilder getParamOrBuilder(int index) {
        if (this.paramBuilder_ == null)
          return this.param_.get(index); 
        return (MysqlxExpr.ExprOrBuilder)this.paramBuilder_.getMessageOrBuilder(index);
      }
      
      public List<? extends MysqlxExpr.ExprOrBuilder> getParamOrBuilderList() {
        if (this.paramBuilder_ != null)
          return this.paramBuilder_.getMessageOrBuilderList(); 
        return Collections.unmodifiableList((List)this.param_);
      }
      
      public MysqlxExpr.Expr.Builder addParamBuilder() {
        return (MysqlxExpr.Expr.Builder)getParamFieldBuilder().addBuilder(
            (AbstractMessage)MysqlxExpr.Expr.getDefaultInstance());
      }
      
      public MysqlxExpr.Expr.Builder addParamBuilder(int index) {
        return (MysqlxExpr.Expr.Builder)getParamFieldBuilder().addBuilder(index, 
            (AbstractMessage)MysqlxExpr.Expr.getDefaultInstance());
      }
      
      public List<MysqlxExpr.Expr.Builder> getParamBuilderList() {
        return getParamFieldBuilder().getBuilderList();
      }
      
      private RepeatedFieldBuilderV3<MysqlxExpr.Expr, MysqlxExpr.Expr.Builder, MysqlxExpr.ExprOrBuilder> getParamFieldBuilder() {
        if (this.paramBuilder_ == null) {
          this
            
            .paramBuilder_ = new RepeatedFieldBuilderV3(this.param_, ((this.bitField0_ & 0x2) != 0), (AbstractMessage.BuilderParent)getParentForChildren(), isClean());
          this.param_ = null;
        } 
        return this.paramBuilder_;
      }
      
      public final Builder setUnknownFields(UnknownFieldSet unknownFields) {
        return (Builder)super.setUnknownFields(unknownFields);
      }
      
      public final Builder mergeUnknownFields(UnknownFieldSet unknownFields) {
        return (Builder)super.mergeUnknownFields(unknownFields);
      }
    }
    
    private static final Operator DEFAULT_INSTANCE = new Operator();
    
    public static Operator getDefaultInstance() {
      return DEFAULT_INSTANCE;
    }
    
    @Deprecated
    public static final Parser<Operator> PARSER = (Parser<Operator>)new AbstractParser<Operator>() {
        public MysqlxExpr.Operator parsePartialFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
          MysqlxExpr.Operator.Builder builder = MysqlxExpr.Operator.newBuilder();
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
    
    public static Parser<Operator> parser() {
      return PARSER;
    }
    
    public Parser<Operator> getParserForType() {
      return PARSER;
    }
    
    public Operator getDefaultInstanceForType() {
      return DEFAULT_INSTANCE;
    }
  }
  
  public static interface ObjectOrBuilder extends MessageOrBuilder {
    List<MysqlxExpr.Object.ObjectField> getFldList();
    
    MysqlxExpr.Object.ObjectField getFld(int param1Int);
    
    int getFldCount();
    
    List<? extends MysqlxExpr.Object.ObjectFieldOrBuilder> getFldOrBuilderList();
    
    MysqlxExpr.Object.ObjectFieldOrBuilder getFldOrBuilder(int param1Int);
  }
  
  public static final class Object extends GeneratedMessageV3 implements ObjectOrBuilder {
    private static final long serialVersionUID = 0L;
    
    public static final int FLD_FIELD_NUMBER = 1;
    
    private List<ObjectField> fld_;
    
    private byte memoizedIsInitialized;
    
    private Object(GeneratedMessageV3.Builder<?> builder) {
      super(builder);
      this.memoizedIsInitialized = -1;
    }
    
    private Object() {
      this.memoizedIsInitialized = -1;
      this.fld_ = Collections.emptyList();
    }
    
    protected Object newInstance(GeneratedMessageV3.UnusedPrivateParameter unused) {
      return new Object();
    }
    
    public final UnknownFieldSet getUnknownFields() {
      return this.unknownFields;
    }
    
    public static final Descriptors.Descriptor getDescriptor() {
      return MysqlxExpr.internal_static_Mysqlx_Expr_Object_descriptor;
    }
    
    protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() {
      return MysqlxExpr.internal_static_Mysqlx_Expr_Object_fieldAccessorTable.ensureFieldAccessorsInitialized(Object.class, Builder.class);
    }
    
    public static final class ObjectField extends GeneratedMessageV3 implements ObjectFieldOrBuilder {
      private static final long serialVersionUID = 0L;
      
      private int bitField0_;
      
      public static final int KEY_FIELD_NUMBER = 1;
      
      private volatile Object key_;
      
      public static final int VALUE_FIELD_NUMBER = 2;
      
      private MysqlxExpr.Expr value_;
      
      private byte memoizedIsInitialized;
      
      private ObjectField(GeneratedMessageV3.Builder<?> builder) {
        super(builder);
        this.memoizedIsInitialized = -1;
      }
      
      private ObjectField() {
        this.memoizedIsInitialized = -1;
        this.key_ = "";
      }
      
      protected Object newInstance(GeneratedMessageV3.UnusedPrivateParameter unused) {
        return new ObjectField();
      }
      
      public final UnknownFieldSet getUnknownFields() {
        return this.unknownFields;
      }
      
      public static final Descriptors.Descriptor getDescriptor() {
        return MysqlxExpr.internal_static_Mysqlx_Expr_Object_ObjectField_descriptor;
      }
      
      protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() {
        return MysqlxExpr.internal_static_Mysqlx_Expr_Object_ObjectField_fieldAccessorTable.ensureFieldAccessorsInitialized(ObjectField.class, Builder.class);
      }
      
      public boolean hasKey() {
        return ((this.bitField0_ & 0x1) != 0);
      }
      
      public String getKey() {
        Object ref = this.key_;
        if (ref instanceof String)
          return (String)ref; 
        ByteString bs = (ByteString)ref;
        String s = bs.toStringUtf8();
        if (bs.isValidUtf8())
          this.key_ = s; 
        return s;
      }
      
      public ByteString getKeyBytes() {
        Object ref = this.key_;
        if (ref instanceof String) {
          ByteString b = ByteString.copyFromUtf8((String)ref);
          this.key_ = b;
          return b;
        } 
        return (ByteString)ref;
      }
      
      public boolean hasValue() {
        return ((this.bitField0_ & 0x2) != 0);
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
        if (!hasKey()) {
          this.memoizedIsInitialized = 0;
          return false;
        } 
        if (!hasValue()) {
          this.memoizedIsInitialized = 0;
          return false;
        } 
        if (!getValue().isInitialized()) {
          this.memoizedIsInitialized = 0;
          return false;
        } 
        this.memoizedIsInitialized = 1;
        return true;
      }
      
      public void writeTo(CodedOutputStream output) throws IOException {
        if ((this.bitField0_ & 0x1) != 0)
          GeneratedMessageV3.writeString(output, 1, this.key_); 
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
          size += GeneratedMessageV3.computeStringSize(1, this.key_); 
        if ((this.bitField0_ & 0x2) != 0)
          size += CodedOutputStream.computeMessageSize(2, (MessageLite)getValue()); 
        size += getUnknownFields().getSerializedSize();
        this.memoizedSize = size;
        return size;
      }
      
      public boolean equals(Object obj) {
        if (obj == this)
          return true; 
        if (!(obj instanceof ObjectField))
          return super.equals(obj); 
        ObjectField other = (ObjectField)obj;
        if (hasKey() != other.hasKey())
          return false; 
        if (hasKey() && !getKey().equals(other.getKey()))
          return false; 
        if (hasValue() != other.hasValue())
          return false; 
        if (hasValue() && !getValue().equals(other.getValue()))
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
        if (hasKey()) {
          hash = 37 * hash + 1;
          hash = 53 * hash + getKey().hashCode();
        } 
        if (hasValue()) {
          hash = 37 * hash + 2;
          hash = 53 * hash + getValue().hashCode();
        } 
        hash = 29 * hash + getUnknownFields().hashCode();
        this.memoizedHashCode = hash;
        return hash;
      }
      
      public static ObjectField parseFrom(ByteBuffer data) throws InvalidProtocolBufferException {
        return (ObjectField)PARSER.parseFrom(data);
      }
      
      public static ObjectField parseFrom(ByteBuffer data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
        return (ObjectField)PARSER.parseFrom(data, extensionRegistry);
      }
      
      public static ObjectField parseFrom(ByteString data) throws InvalidProtocolBufferException {
        return (ObjectField)PARSER.parseFrom(data);
      }
      
      public static ObjectField parseFrom(ByteString data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
        return (ObjectField)PARSER.parseFrom(data, extensionRegistry);
      }
      
      public static ObjectField parseFrom(byte[] data) throws InvalidProtocolBufferException {
        return (ObjectField)PARSER.parseFrom(data);
      }
      
      public static ObjectField parseFrom(byte[] data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
        return (ObjectField)PARSER.parseFrom(data, extensionRegistry);
      }
      
      public static ObjectField parseFrom(InputStream input) throws IOException {
        return (ObjectField)GeneratedMessageV3.parseWithIOException(PARSER, input);
      }
      
      public static ObjectField parseFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
        return (ObjectField)GeneratedMessageV3.parseWithIOException(PARSER, input, extensionRegistry);
      }
      
      public static ObjectField parseDelimitedFrom(InputStream input) throws IOException {
        return (ObjectField)GeneratedMessageV3.parseDelimitedWithIOException(PARSER, input);
      }
      
      public static ObjectField parseDelimitedFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
        return (ObjectField)GeneratedMessageV3.parseDelimitedWithIOException(PARSER, input, extensionRegistry);
      }
      
      public static ObjectField parseFrom(CodedInputStream input) throws IOException {
        return (ObjectField)GeneratedMessageV3.parseWithIOException(PARSER, input);
      }
      
      public static ObjectField parseFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
        return (ObjectField)GeneratedMessageV3.parseWithIOException(PARSER, input, extensionRegistry);
      }
      
      public Builder newBuilderForType() {
        return newBuilder();
      }
      
      public static Builder newBuilder() {
        return DEFAULT_INSTANCE.toBuilder();
      }
      
      public static Builder newBuilder(ObjectField prototype) {
        return DEFAULT_INSTANCE.toBuilder().mergeFrom(prototype);
      }
      
      public Builder toBuilder() {
        return (this == DEFAULT_INSTANCE) ? new Builder() : (new Builder()).mergeFrom(this);
      }
      
      protected Builder newBuilderForType(GeneratedMessageV3.BuilderParent parent) {
        Builder builder = new Builder(parent);
        return builder;
      }
      
      public static final class Builder extends GeneratedMessageV3.Builder<Builder> implements MysqlxExpr.Object.ObjectFieldOrBuilder {
        private int bitField0_;
        
        private Object key_;
        
        private MysqlxExpr.Expr value_;
        
        private SingleFieldBuilderV3<MysqlxExpr.Expr, MysqlxExpr.Expr.Builder, MysqlxExpr.ExprOrBuilder> valueBuilder_;
        
        public static final Descriptors.Descriptor getDescriptor() {
          return MysqlxExpr.internal_static_Mysqlx_Expr_Object_ObjectField_descriptor;
        }
        
        protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() {
          return MysqlxExpr.internal_static_Mysqlx_Expr_Object_ObjectField_fieldAccessorTable.ensureFieldAccessorsInitialized(MysqlxExpr.Object.ObjectField.class, Builder.class);
        }
        
        private Builder() {
          this.key_ = "";
          maybeForceBuilderInitialization();
        }
        
        private Builder(GeneratedMessageV3.BuilderParent parent) {
          super(parent);
          this.key_ = "";
          maybeForceBuilderInitialization();
        }
        
        private void maybeForceBuilderInitialization() {
          if (MysqlxExpr.Object.ObjectField.alwaysUseFieldBuilders)
            getValueFieldBuilder(); 
        }
        
        public Builder clear() {
          super.clear();
          this.key_ = "";
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
          return MysqlxExpr.internal_static_Mysqlx_Expr_Object_ObjectField_descriptor;
        }
        
        public MysqlxExpr.Object.ObjectField getDefaultInstanceForType() {
          return MysqlxExpr.Object.ObjectField.getDefaultInstance();
        }
        
        public MysqlxExpr.Object.ObjectField build() {
          MysqlxExpr.Object.ObjectField result = buildPartial();
          if (!result.isInitialized())
            throw newUninitializedMessageException(result); 
          return result;
        }
        
        public MysqlxExpr.Object.ObjectField buildPartial() {
          MysqlxExpr.Object.ObjectField result = new MysqlxExpr.Object.ObjectField(this);
          int from_bitField0_ = this.bitField0_;
          int to_bitField0_ = 0;
          if ((from_bitField0_ & 0x1) != 0)
            to_bitField0_ |= 0x1; 
          result.key_ = this.key_;
          if ((from_bitField0_ & 0x2) != 0) {
            if (this.valueBuilder_ == null) {
              result.value_ = this.value_;
            } else {
              result.value_ = (MysqlxExpr.Expr)this.valueBuilder_.build();
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
          if (other instanceof MysqlxExpr.Object.ObjectField)
            return mergeFrom((MysqlxExpr.Object.ObjectField)other); 
          super.mergeFrom(other);
          return this;
        }
        
        public Builder mergeFrom(MysqlxExpr.Object.ObjectField other) {
          if (other == MysqlxExpr.Object.ObjectField.getDefaultInstance())
            return this; 
          if (other.hasKey()) {
            this.bitField0_ |= 0x1;
            this.key_ = other.key_;
            onChanged();
          } 
          if (other.hasValue())
            mergeValue(other.getValue()); 
          mergeUnknownFields(other.getUnknownFields());
          onChanged();
          return this;
        }
        
        public final boolean isInitialized() {
          if (!hasKey())
            return false; 
          if (!hasValue())
            return false; 
          if (!getValue().isInitialized())
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
                  this.key_ = input.readBytes();
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
        
        public boolean hasKey() {
          return ((this.bitField0_ & 0x1) != 0);
        }
        
        public String getKey() {
          Object ref = this.key_;
          if (!(ref instanceof String)) {
            ByteString bs = (ByteString)ref;
            String s = bs.toStringUtf8();
            if (bs.isValidUtf8())
              this.key_ = s; 
            return s;
          } 
          return (String)ref;
        }
        
        public ByteString getKeyBytes() {
          Object ref = this.key_;
          if (ref instanceof String) {
            ByteString b = ByteString.copyFromUtf8((String)ref);
            this.key_ = b;
            return b;
          } 
          return (ByteString)ref;
        }
        
        public Builder setKey(String value) {
          if (value == null)
            throw new NullPointerException(); 
          this.bitField0_ |= 0x1;
          this.key_ = value;
          onChanged();
          return this;
        }
        
        public Builder clearKey() {
          this.bitField0_ &= 0xFFFFFFFE;
          this.key_ = MysqlxExpr.Object.ObjectField.getDefaultInstance().getKey();
          onChanged();
          return this;
        }
        
        public Builder setKeyBytes(ByteString value) {
          if (value == null)
            throw new NullPointerException(); 
          this.bitField0_ |= 0x1;
          this.key_ = value;
          onChanged();
          return this;
        }
        
        public boolean hasValue() {
          return ((this.bitField0_ & 0x2) != 0);
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
          this.bitField0_ |= 0x2;
          return this;
        }
        
        public Builder setValue(MysqlxExpr.Expr.Builder builderForValue) {
          if (this.valueBuilder_ == null) {
            this.value_ = builderForValue.build();
            onChanged();
          } else {
            this.valueBuilder_.setMessage((AbstractMessage)builderForValue.build());
          } 
          this.bitField0_ |= 0x2;
          return this;
        }
        
        public Builder mergeValue(MysqlxExpr.Expr value) {
          if (this.valueBuilder_ == null) {
            if ((this.bitField0_ & 0x2) != 0 && this.value_ != null && this.value_ != MysqlxExpr.Expr.getDefaultInstance()) {
              this.value_ = MysqlxExpr.Expr.newBuilder(this.value_).mergeFrom(value).buildPartial();
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
        
        public MysqlxExpr.Expr.Builder getValueBuilder() {
          this.bitField0_ |= 0x2;
          onChanged();
          return (MysqlxExpr.Expr.Builder)getValueFieldBuilder().getBuilder();
        }
        
        public MysqlxExpr.ExprOrBuilder getValueOrBuilder() {
          if (this.valueBuilder_ != null)
            return (MysqlxExpr.ExprOrBuilder)this.valueBuilder_.getMessageOrBuilder(); 
          return (this.value_ == null) ? MysqlxExpr.Expr.getDefaultInstance() : this.value_;
        }
        
        private SingleFieldBuilderV3<MysqlxExpr.Expr, MysqlxExpr.Expr.Builder, MysqlxExpr.ExprOrBuilder> getValueFieldBuilder() {
          if (this.valueBuilder_ == null) {
            this.valueBuilder_ = new SingleFieldBuilderV3((AbstractMessage)getValue(), (AbstractMessage.BuilderParent)getParentForChildren(), isClean());
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
      
      private static final ObjectField DEFAULT_INSTANCE = new ObjectField();
      
      public static ObjectField getDefaultInstance() {
        return DEFAULT_INSTANCE;
      }
      
      @Deprecated
      public static final Parser<ObjectField> PARSER = (Parser<ObjectField>)new AbstractParser<ObjectField>() {
          public MysqlxExpr.Object.ObjectField parsePartialFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            MysqlxExpr.Object.ObjectField.Builder builder = MysqlxExpr.Object.ObjectField.newBuilder();
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
      
      public static Parser<ObjectField> parser() {
        return PARSER;
      }
      
      public Parser<ObjectField> getParserForType() {
        return PARSER;
      }
      
      public ObjectField getDefaultInstanceForType() {
        return DEFAULT_INSTANCE;
      }
    }
    
    public List<ObjectField> getFldList() {
      return this.fld_;
    }
    
    public List<? extends ObjectFieldOrBuilder> getFldOrBuilderList() {
      return (List)this.fld_;
    }
    
    public int getFldCount() {
      return this.fld_.size();
    }
    
    public ObjectField getFld(int index) {
      return this.fld_.get(index);
    }
    
    public ObjectFieldOrBuilder getFldOrBuilder(int index) {
      return this.fld_.get(index);
    }
    
    public final boolean isInitialized() {
      byte isInitialized = this.memoizedIsInitialized;
      if (isInitialized == 1)
        return true; 
      if (isInitialized == 0)
        return false; 
      for (int i = 0; i < getFldCount(); i++) {
        if (!getFld(i).isInitialized()) {
          this.memoizedIsInitialized = 0;
          return false;
        } 
      } 
      this.memoizedIsInitialized = 1;
      return true;
    }
    
    public void writeTo(CodedOutputStream output) throws IOException {
      for (int i = 0; i < this.fld_.size(); i++)
        output.writeMessage(1, (MessageLite)this.fld_.get(i)); 
      getUnknownFields().writeTo(output);
    }
    
    public int getSerializedSize() {
      int size = this.memoizedSize;
      if (size != -1)
        return size; 
      size = 0;
      for (int i = 0; i < this.fld_.size(); i++)
        size += 
          CodedOutputStream.computeMessageSize(1, (MessageLite)this.fld_.get(i)); 
      size += getUnknownFields().getSerializedSize();
      this.memoizedSize = size;
      return size;
    }
    
    public boolean equals(Object obj) {
      if (obj == this)
        return true; 
      if (!(obj instanceof Object))
        return super.equals(obj); 
      Object other = (Object)obj;
      if (!getFldList().equals(other.getFldList()))
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
      if (getFldCount() > 0) {
        hash = 37 * hash + 1;
        hash = 53 * hash + getFldList().hashCode();
      } 
      hash = 29 * hash + getUnknownFields().hashCode();
      this.memoizedHashCode = hash;
      return hash;
    }
    
    public static Object parseFrom(ByteBuffer data) throws InvalidProtocolBufferException {
      return (Object)PARSER.parseFrom(data);
    }
    
    public static Object parseFrom(ByteBuffer data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
      return (Object)PARSER.parseFrom(data, extensionRegistry);
    }
    
    public static Object parseFrom(ByteString data) throws InvalidProtocolBufferException {
      return (Object)PARSER.parseFrom(data);
    }
    
    public static Object parseFrom(ByteString data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
      return (Object)PARSER.parseFrom(data, extensionRegistry);
    }
    
    public static Object parseFrom(byte[] data) throws InvalidProtocolBufferException {
      return (Object)PARSER.parseFrom(data);
    }
    
    public static Object parseFrom(byte[] data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
      return (Object)PARSER.parseFrom(data, extensionRegistry);
    }
    
    public static Object parseFrom(InputStream input) throws IOException {
      return 
        (Object)GeneratedMessageV3.parseWithIOException(PARSER, input);
    }
    
    public static Object parseFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
      return 
        (Object)GeneratedMessageV3.parseWithIOException(PARSER, input, extensionRegistry);
    }
    
    public static Object parseDelimitedFrom(InputStream input) throws IOException {
      return 
        (Object)GeneratedMessageV3.parseDelimitedWithIOException(PARSER, input);
    }
    
    public static Object parseDelimitedFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
      return 
        (Object)GeneratedMessageV3.parseDelimitedWithIOException(PARSER, input, extensionRegistry);
    }
    
    public static Object parseFrom(CodedInputStream input) throws IOException {
      return 
        (Object)GeneratedMessageV3.parseWithIOException(PARSER, input);
    }
    
    public static Object parseFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
      return 
        (Object)GeneratedMessageV3.parseWithIOException(PARSER, input, extensionRegistry);
    }
    
    public Builder newBuilderForType() {
      return newBuilder();
    }
    
    public static Builder newBuilder() {
      return DEFAULT_INSTANCE.toBuilder();
    }
    
    public static Builder newBuilder(Object prototype) {
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
    
    public static final class Builder extends GeneratedMessageV3.Builder<Builder> implements MysqlxExpr.ObjectOrBuilder {
      private int bitField0_;
      
      private List<MysqlxExpr.Object.ObjectField> fld_;
      
      private RepeatedFieldBuilderV3<MysqlxExpr.Object.ObjectField, MysqlxExpr.Object.ObjectField.Builder, MysqlxExpr.Object.ObjectFieldOrBuilder> fldBuilder_;
      
      public static final Descriptors.Descriptor getDescriptor() {
        return MysqlxExpr.internal_static_Mysqlx_Expr_Object_descriptor;
      }
      
      protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() {
        return MysqlxExpr.internal_static_Mysqlx_Expr_Object_fieldAccessorTable
          .ensureFieldAccessorsInitialized(MysqlxExpr.Object.class, Builder.class);
      }
      
      private Builder() {
        this
          .fld_ = Collections.emptyList();
      }
      
      private Builder(GeneratedMessageV3.BuilderParent parent) {
        super(parent);
        this.fld_ = Collections.emptyList();
      }
      
      public Builder clear() {
        super.clear();
        if (this.fldBuilder_ == null) {
          this.fld_ = Collections.emptyList();
        } else {
          this.fld_ = null;
          this.fldBuilder_.clear();
        } 
        this.bitField0_ &= 0xFFFFFFFE;
        return this;
      }
      
      public Descriptors.Descriptor getDescriptorForType() {
        return MysqlxExpr.internal_static_Mysqlx_Expr_Object_descriptor;
      }
      
      public MysqlxExpr.Object getDefaultInstanceForType() {
        return MysqlxExpr.Object.getDefaultInstance();
      }
      
      public MysqlxExpr.Object build() {
        MysqlxExpr.Object result = buildPartial();
        if (!result.isInitialized())
          throw newUninitializedMessageException(result); 
        return result;
      }
      
      public MysqlxExpr.Object buildPartial() {
        MysqlxExpr.Object result = new MysqlxExpr.Object(this);
        int from_bitField0_ = this.bitField0_;
        if (this.fldBuilder_ == null) {
          if ((this.bitField0_ & 0x1) != 0) {
            this.fld_ = Collections.unmodifiableList(this.fld_);
            this.bitField0_ &= 0xFFFFFFFE;
          } 
          result.fld_ = this.fld_;
        } else {
          result.fld_ = this.fldBuilder_.build();
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
        if (other instanceof MysqlxExpr.Object)
          return mergeFrom((MysqlxExpr.Object)other); 
        super.mergeFrom(other);
        return this;
      }
      
      public Builder mergeFrom(MysqlxExpr.Object other) {
        if (other == MysqlxExpr.Object.getDefaultInstance())
          return this; 
        if (this.fldBuilder_ == null) {
          if (!other.fld_.isEmpty()) {
            if (this.fld_.isEmpty()) {
              this.fld_ = other.fld_;
              this.bitField0_ &= 0xFFFFFFFE;
            } else {
              ensureFldIsMutable();
              this.fld_.addAll(other.fld_);
            } 
            onChanged();
          } 
        } else if (!other.fld_.isEmpty()) {
          if (this.fldBuilder_.isEmpty()) {
            this.fldBuilder_.dispose();
            this.fldBuilder_ = null;
            this.fld_ = other.fld_;
            this.bitField0_ &= 0xFFFFFFFE;
            this.fldBuilder_ = MysqlxExpr.Object.alwaysUseFieldBuilders ? getFldFieldBuilder() : null;
          } else {
            this.fldBuilder_.addAllMessages(other.fld_);
          } 
        } 
        mergeUnknownFields(other.getUnknownFields());
        onChanged();
        return this;
      }
      
      public final boolean isInitialized() {
        for (int i = 0; i < getFldCount(); i++) {
          if (!getFld(i).isInitialized())
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
            MysqlxExpr.Object.ObjectField m;
            int tag = input.readTag();
            switch (tag) {
              case 0:
                done = true;
                continue;
              case 10:
                m = (MysqlxExpr.Object.ObjectField)input.readMessage(MysqlxExpr.Object.ObjectField.PARSER, extensionRegistry);
                if (this.fldBuilder_ == null) {
                  ensureFldIsMutable();
                  this.fld_.add(m);
                  continue;
                } 
                this.fldBuilder_.addMessage((AbstractMessage)m);
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
      
      private void ensureFldIsMutable() {
        if ((this.bitField0_ & 0x1) == 0) {
          this.fld_ = new ArrayList<>(this.fld_);
          this.bitField0_ |= 0x1;
        } 
      }
      
      public List<MysqlxExpr.Object.ObjectField> getFldList() {
        if (this.fldBuilder_ == null)
          return Collections.unmodifiableList(this.fld_); 
        return this.fldBuilder_.getMessageList();
      }
      
      public int getFldCount() {
        if (this.fldBuilder_ == null)
          return this.fld_.size(); 
        return this.fldBuilder_.getCount();
      }
      
      public MysqlxExpr.Object.ObjectField getFld(int index) {
        if (this.fldBuilder_ == null)
          return this.fld_.get(index); 
        return (MysqlxExpr.Object.ObjectField)this.fldBuilder_.getMessage(index);
      }
      
      public Builder setFld(int index, MysqlxExpr.Object.ObjectField value) {
        if (this.fldBuilder_ == null) {
          if (value == null)
            throw new NullPointerException(); 
          ensureFldIsMutable();
          this.fld_.set(index, value);
          onChanged();
        } else {
          this.fldBuilder_.setMessage(index, (AbstractMessage)value);
        } 
        return this;
      }
      
      public Builder setFld(int index, MysqlxExpr.Object.ObjectField.Builder builderForValue) {
        if (this.fldBuilder_ == null) {
          ensureFldIsMutable();
          this.fld_.set(index, builderForValue.build());
          onChanged();
        } else {
          this.fldBuilder_.setMessage(index, (AbstractMessage)builderForValue.build());
        } 
        return this;
      }
      
      public Builder addFld(MysqlxExpr.Object.ObjectField value) {
        if (this.fldBuilder_ == null) {
          if (value == null)
            throw new NullPointerException(); 
          ensureFldIsMutable();
          this.fld_.add(value);
          onChanged();
        } else {
          this.fldBuilder_.addMessage((AbstractMessage)value);
        } 
        return this;
      }
      
      public Builder addFld(int index, MysqlxExpr.Object.ObjectField value) {
        if (this.fldBuilder_ == null) {
          if (value == null)
            throw new NullPointerException(); 
          ensureFldIsMutable();
          this.fld_.add(index, value);
          onChanged();
        } else {
          this.fldBuilder_.addMessage(index, (AbstractMessage)value);
        } 
        return this;
      }
      
      public Builder addFld(MysqlxExpr.Object.ObjectField.Builder builderForValue) {
        if (this.fldBuilder_ == null) {
          ensureFldIsMutable();
          this.fld_.add(builderForValue.build());
          onChanged();
        } else {
          this.fldBuilder_.addMessage((AbstractMessage)builderForValue.build());
        } 
        return this;
      }
      
      public Builder addFld(int index, MysqlxExpr.Object.ObjectField.Builder builderForValue) {
        if (this.fldBuilder_ == null) {
          ensureFldIsMutable();
          this.fld_.add(index, builderForValue.build());
          onChanged();
        } else {
          this.fldBuilder_.addMessage(index, (AbstractMessage)builderForValue.build());
        } 
        return this;
      }
      
      public Builder addAllFld(Iterable<? extends MysqlxExpr.Object.ObjectField> values) {
        if (this.fldBuilder_ == null) {
          ensureFldIsMutable();
          AbstractMessageLite.Builder.addAll(values, this.fld_);
          onChanged();
        } else {
          this.fldBuilder_.addAllMessages(values);
        } 
        return this;
      }
      
      public Builder clearFld() {
        if (this.fldBuilder_ == null) {
          this.fld_ = Collections.emptyList();
          this.bitField0_ &= 0xFFFFFFFE;
          onChanged();
        } else {
          this.fldBuilder_.clear();
        } 
        return this;
      }
      
      public Builder removeFld(int index) {
        if (this.fldBuilder_ == null) {
          ensureFldIsMutable();
          this.fld_.remove(index);
          onChanged();
        } else {
          this.fldBuilder_.remove(index);
        } 
        return this;
      }
      
      public MysqlxExpr.Object.ObjectField.Builder getFldBuilder(int index) {
        return (MysqlxExpr.Object.ObjectField.Builder)getFldFieldBuilder().getBuilder(index);
      }
      
      public MysqlxExpr.Object.ObjectFieldOrBuilder getFldOrBuilder(int index) {
        if (this.fldBuilder_ == null)
          return this.fld_.get(index); 
        return (MysqlxExpr.Object.ObjectFieldOrBuilder)this.fldBuilder_.getMessageOrBuilder(index);
      }
      
      public List<? extends MysqlxExpr.Object.ObjectFieldOrBuilder> getFldOrBuilderList() {
        if (this.fldBuilder_ != null)
          return this.fldBuilder_.getMessageOrBuilderList(); 
        return Collections.unmodifiableList((List)this.fld_);
      }
      
      public MysqlxExpr.Object.ObjectField.Builder addFldBuilder() {
        return (MysqlxExpr.Object.ObjectField.Builder)getFldFieldBuilder().addBuilder(
            (AbstractMessage)MysqlxExpr.Object.ObjectField.getDefaultInstance());
      }
      
      public MysqlxExpr.Object.ObjectField.Builder addFldBuilder(int index) {
        return (MysqlxExpr.Object.ObjectField.Builder)getFldFieldBuilder().addBuilder(index, 
            (AbstractMessage)MysqlxExpr.Object.ObjectField.getDefaultInstance());
      }
      
      public List<MysqlxExpr.Object.ObjectField.Builder> getFldBuilderList() {
        return getFldFieldBuilder().getBuilderList();
      }
      
      private RepeatedFieldBuilderV3<MysqlxExpr.Object.ObjectField, MysqlxExpr.Object.ObjectField.Builder, MysqlxExpr.Object.ObjectFieldOrBuilder> getFldFieldBuilder() {
        if (this.fldBuilder_ == null) {
          this
            
            .fldBuilder_ = new RepeatedFieldBuilderV3(this.fld_, ((this.bitField0_ & 0x1) != 0), (AbstractMessage.BuilderParent)getParentForChildren(), isClean());
          this.fld_ = null;
        } 
        return this.fldBuilder_;
      }
      
      public final Builder setUnknownFields(UnknownFieldSet unknownFields) {
        return (Builder)super.setUnknownFields(unknownFields);
      }
      
      public final Builder mergeUnknownFields(UnknownFieldSet unknownFields) {
        return (Builder)super.mergeUnknownFields(unknownFields);
      }
    }
    
    private static final Object DEFAULT_INSTANCE = new Object();
    
    public static Object getDefaultInstance() {
      return DEFAULT_INSTANCE;
    }
    
    @Deprecated
    public static final Parser<Object> PARSER = (Parser<Object>)new AbstractParser<Object>() {
        public MysqlxExpr.Object parsePartialFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
          MysqlxExpr.Object.Builder builder = MysqlxExpr.Object.newBuilder();
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
    
    public static Parser<Object> parser() {
      return PARSER;
    }
    
    public Parser<Object> getParserForType() {
      return PARSER;
    }
    
    public Object getDefaultInstanceForType() {
      return DEFAULT_INSTANCE;
    }
    
    public static interface ObjectFieldOrBuilder extends MessageOrBuilder {
      boolean hasKey();
      
      String getKey();
      
      ByteString getKeyBytes();
      
      boolean hasValue();
      
      MysqlxExpr.Expr getValue();
      
      MysqlxExpr.ExprOrBuilder getValueOrBuilder();
    }
  }
  
  public static interface ArrayOrBuilder extends MessageOrBuilder {
    List<MysqlxExpr.Expr> getValueList();
    
    MysqlxExpr.Expr getValue(int param1Int);
    
    int getValueCount();
    
    List<? extends MysqlxExpr.ExprOrBuilder> getValueOrBuilderList();
    
    MysqlxExpr.ExprOrBuilder getValueOrBuilder(int param1Int);
  }
  
  public static final class Array extends GeneratedMessageV3 implements ArrayOrBuilder {
    private static final long serialVersionUID = 0L;
    
    public static final int VALUE_FIELD_NUMBER = 1;
    
    private List<MysqlxExpr.Expr> value_;
    
    private byte memoizedIsInitialized;
    
    private Array(GeneratedMessageV3.Builder<?> builder) {
      super(builder);
      this.memoizedIsInitialized = -1;
    }
    
    private Array() {
      this.memoizedIsInitialized = -1;
      this.value_ = Collections.emptyList();
    }
    
    protected Object newInstance(GeneratedMessageV3.UnusedPrivateParameter unused) {
      return new Array();
    }
    
    public final UnknownFieldSet getUnknownFields() {
      return this.unknownFields;
    }
    
    public static final Descriptors.Descriptor getDescriptor() {
      return MysqlxExpr.internal_static_Mysqlx_Expr_Array_descriptor;
    }
    
    protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() {
      return MysqlxExpr.internal_static_Mysqlx_Expr_Array_fieldAccessorTable.ensureFieldAccessorsInitialized(Array.class, Builder.class);
    }
    
    public List<MysqlxExpr.Expr> getValueList() {
      return this.value_;
    }
    
    public List<? extends MysqlxExpr.ExprOrBuilder> getValueOrBuilderList() {
      return (List)this.value_;
    }
    
    public int getValueCount() {
      return this.value_.size();
    }
    
    public MysqlxExpr.Expr getValue(int index) {
      return this.value_.get(index);
    }
    
    public MysqlxExpr.ExprOrBuilder getValueOrBuilder(int index) {
      return this.value_.get(index);
    }
    
    public final boolean isInitialized() {
      byte isInitialized = this.memoizedIsInitialized;
      if (isInitialized == 1)
        return true; 
      if (isInitialized == 0)
        return false; 
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
      for (int i = 0; i < this.value_.size(); i++)
        output.writeMessage(1, (MessageLite)this.value_.get(i)); 
      getUnknownFields().writeTo(output);
    }
    
    public int getSerializedSize() {
      int size = this.memoizedSize;
      if (size != -1)
        return size; 
      size = 0;
      for (int i = 0; i < this.value_.size(); i++)
        size += 
          CodedOutputStream.computeMessageSize(1, (MessageLite)this.value_.get(i)); 
      size += getUnknownFields().getSerializedSize();
      this.memoizedSize = size;
      return size;
    }
    
    public boolean equals(Object obj) {
      if (obj == this)
        return true; 
      if (!(obj instanceof Array))
        return super.equals(obj); 
      Array other = (Array)obj;
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
      if (getValueCount() > 0) {
        hash = 37 * hash + 1;
        hash = 53 * hash + getValueList().hashCode();
      } 
      hash = 29 * hash + getUnknownFields().hashCode();
      this.memoizedHashCode = hash;
      return hash;
    }
    
    public static Array parseFrom(ByteBuffer data) throws InvalidProtocolBufferException {
      return (Array)PARSER.parseFrom(data);
    }
    
    public static Array parseFrom(ByteBuffer data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
      return (Array)PARSER.parseFrom(data, extensionRegistry);
    }
    
    public static Array parseFrom(ByteString data) throws InvalidProtocolBufferException {
      return (Array)PARSER.parseFrom(data);
    }
    
    public static Array parseFrom(ByteString data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
      return (Array)PARSER.parseFrom(data, extensionRegistry);
    }
    
    public static Array parseFrom(byte[] data) throws InvalidProtocolBufferException {
      return (Array)PARSER.parseFrom(data);
    }
    
    public static Array parseFrom(byte[] data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
      return (Array)PARSER.parseFrom(data, extensionRegistry);
    }
    
    public static Array parseFrom(InputStream input) throws IOException {
      return 
        (Array)GeneratedMessageV3.parseWithIOException(PARSER, input);
    }
    
    public static Array parseFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
      return 
        (Array)GeneratedMessageV3.parseWithIOException(PARSER, input, extensionRegistry);
    }
    
    public static Array parseDelimitedFrom(InputStream input) throws IOException {
      return 
        (Array)GeneratedMessageV3.parseDelimitedWithIOException(PARSER, input);
    }
    
    public static Array parseDelimitedFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
      return 
        (Array)GeneratedMessageV3.parseDelimitedWithIOException(PARSER, input, extensionRegistry);
    }
    
    public static Array parseFrom(CodedInputStream input) throws IOException {
      return 
        (Array)GeneratedMessageV3.parseWithIOException(PARSER, input);
    }
    
    public static Array parseFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
      return 
        (Array)GeneratedMessageV3.parseWithIOException(PARSER, input, extensionRegistry);
    }
    
    public Builder newBuilderForType() {
      return newBuilder();
    }
    
    public static Builder newBuilder() {
      return DEFAULT_INSTANCE.toBuilder();
    }
    
    public static Builder newBuilder(Array prototype) {
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
    
    public static final class Builder extends GeneratedMessageV3.Builder<Builder> implements MysqlxExpr.ArrayOrBuilder {
      private int bitField0_;
      
      private List<MysqlxExpr.Expr> value_;
      
      private RepeatedFieldBuilderV3<MysqlxExpr.Expr, MysqlxExpr.Expr.Builder, MysqlxExpr.ExprOrBuilder> valueBuilder_;
      
      public static final Descriptors.Descriptor getDescriptor() {
        return MysqlxExpr.internal_static_Mysqlx_Expr_Array_descriptor;
      }
      
      protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() {
        return MysqlxExpr.internal_static_Mysqlx_Expr_Array_fieldAccessorTable
          .ensureFieldAccessorsInitialized(MysqlxExpr.Array.class, Builder.class);
      }
      
      private Builder() {
        this
          .value_ = Collections.emptyList();
      }
      
      private Builder(GeneratedMessageV3.BuilderParent parent) {
        super(parent);
        this.value_ = Collections.emptyList();
      }
      
      public Builder clear() {
        super.clear();
        if (this.valueBuilder_ == null) {
          this.value_ = Collections.emptyList();
        } else {
          this.value_ = null;
          this.valueBuilder_.clear();
        } 
        this.bitField0_ &= 0xFFFFFFFE;
        return this;
      }
      
      public Descriptors.Descriptor getDescriptorForType() {
        return MysqlxExpr.internal_static_Mysqlx_Expr_Array_descriptor;
      }
      
      public MysqlxExpr.Array getDefaultInstanceForType() {
        return MysqlxExpr.Array.getDefaultInstance();
      }
      
      public MysqlxExpr.Array build() {
        MysqlxExpr.Array result = buildPartial();
        if (!result.isInitialized())
          throw newUninitializedMessageException(result); 
        return result;
      }
      
      public MysqlxExpr.Array buildPartial() {
        MysqlxExpr.Array result = new MysqlxExpr.Array(this);
        int from_bitField0_ = this.bitField0_;
        if (this.valueBuilder_ == null) {
          if ((this.bitField0_ & 0x1) != 0) {
            this.value_ = Collections.unmodifiableList(this.value_);
            this.bitField0_ &= 0xFFFFFFFE;
          } 
          result.value_ = this.value_;
        } else {
          result.value_ = this.valueBuilder_.build();
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
        if (other instanceof MysqlxExpr.Array)
          return mergeFrom((MysqlxExpr.Array)other); 
        super.mergeFrom(other);
        return this;
      }
      
      public Builder mergeFrom(MysqlxExpr.Array other) {
        if (other == MysqlxExpr.Array.getDefaultInstance())
          return this; 
        if (this.valueBuilder_ == null) {
          if (!other.value_.isEmpty()) {
            if (this.value_.isEmpty()) {
              this.value_ = other.value_;
              this.bitField0_ &= 0xFFFFFFFE;
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
            this.bitField0_ &= 0xFFFFFFFE;
            this.valueBuilder_ = MysqlxExpr.Array.alwaysUseFieldBuilders ? getValueFieldBuilder() : null;
          } else {
            this.valueBuilder_.addAllMessages(other.value_);
          } 
        } 
        mergeUnknownFields(other.getUnknownFields());
        onChanged();
        return this;
      }
      
      public final boolean isInitialized() {
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
            MysqlxExpr.Expr m;
            int tag = input.readTag();
            switch (tag) {
              case 0:
                done = true;
                continue;
              case 10:
                m = (MysqlxExpr.Expr)input.readMessage(MysqlxExpr.Expr.PARSER, extensionRegistry);
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
      
      private void ensureValueIsMutable() {
        if ((this.bitField0_ & 0x1) == 0) {
          this.value_ = new ArrayList<>(this.value_);
          this.bitField0_ |= 0x1;
        } 
      }
      
      public List<MysqlxExpr.Expr> getValueList() {
        if (this.valueBuilder_ == null)
          return Collections.unmodifiableList(this.value_); 
        return this.valueBuilder_.getMessageList();
      }
      
      public int getValueCount() {
        if (this.valueBuilder_ == null)
          return this.value_.size(); 
        return this.valueBuilder_.getCount();
      }
      
      public MysqlxExpr.Expr getValue(int index) {
        if (this.valueBuilder_ == null)
          return this.value_.get(index); 
        return (MysqlxExpr.Expr)this.valueBuilder_.getMessage(index);
      }
      
      public Builder setValue(int index, MysqlxExpr.Expr value) {
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
      
      public Builder setValue(int index, MysqlxExpr.Expr.Builder builderForValue) {
        if (this.valueBuilder_ == null) {
          ensureValueIsMutable();
          this.value_.set(index, builderForValue.build());
          onChanged();
        } else {
          this.valueBuilder_.setMessage(index, (AbstractMessage)builderForValue.build());
        } 
        return this;
      }
      
      public Builder addValue(MysqlxExpr.Expr value) {
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
      
      public Builder addValue(int index, MysqlxExpr.Expr value) {
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
      
      public Builder addValue(MysqlxExpr.Expr.Builder builderForValue) {
        if (this.valueBuilder_ == null) {
          ensureValueIsMutable();
          this.value_.add(builderForValue.build());
          onChanged();
        } else {
          this.valueBuilder_.addMessage((AbstractMessage)builderForValue.build());
        } 
        return this;
      }
      
      public Builder addValue(int index, MysqlxExpr.Expr.Builder builderForValue) {
        if (this.valueBuilder_ == null) {
          ensureValueIsMutable();
          this.value_.add(index, builderForValue.build());
          onChanged();
        } else {
          this.valueBuilder_.addMessage(index, (AbstractMessage)builderForValue.build());
        } 
        return this;
      }
      
      public Builder addAllValue(Iterable<? extends MysqlxExpr.Expr> values) {
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
          this.bitField0_ &= 0xFFFFFFFE;
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
      
      public MysqlxExpr.Expr.Builder getValueBuilder(int index) {
        return (MysqlxExpr.Expr.Builder)getValueFieldBuilder().getBuilder(index);
      }
      
      public MysqlxExpr.ExprOrBuilder getValueOrBuilder(int index) {
        if (this.valueBuilder_ == null)
          return this.value_.get(index); 
        return (MysqlxExpr.ExprOrBuilder)this.valueBuilder_.getMessageOrBuilder(index);
      }
      
      public List<? extends MysqlxExpr.ExprOrBuilder> getValueOrBuilderList() {
        if (this.valueBuilder_ != null)
          return this.valueBuilder_.getMessageOrBuilderList(); 
        return Collections.unmodifiableList((List)this.value_);
      }
      
      public MysqlxExpr.Expr.Builder addValueBuilder() {
        return (MysqlxExpr.Expr.Builder)getValueFieldBuilder().addBuilder(
            (AbstractMessage)MysqlxExpr.Expr.getDefaultInstance());
      }
      
      public MysqlxExpr.Expr.Builder addValueBuilder(int index) {
        return (MysqlxExpr.Expr.Builder)getValueFieldBuilder().addBuilder(index, 
            (AbstractMessage)MysqlxExpr.Expr.getDefaultInstance());
      }
      
      public List<MysqlxExpr.Expr.Builder> getValueBuilderList() {
        return getValueFieldBuilder().getBuilderList();
      }
      
      private RepeatedFieldBuilderV3<MysqlxExpr.Expr, MysqlxExpr.Expr.Builder, MysqlxExpr.ExprOrBuilder> getValueFieldBuilder() {
        if (this.valueBuilder_ == null) {
          this
            
            .valueBuilder_ = new RepeatedFieldBuilderV3(this.value_, ((this.bitField0_ & 0x1) != 0), (AbstractMessage.BuilderParent)getParentForChildren(), isClean());
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
    
    private static final Array DEFAULT_INSTANCE = new Array();
    
    public static Array getDefaultInstance() {
      return DEFAULT_INSTANCE;
    }
    
    @Deprecated
    public static final Parser<Array> PARSER = (Parser<Array>)new AbstractParser<Array>() {
        public MysqlxExpr.Array parsePartialFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
          MysqlxExpr.Array.Builder builder = MysqlxExpr.Array.newBuilder();
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
    
    public static Parser<Array> parser() {
      return PARSER;
    }
    
    public Parser<Array> getParserForType() {
      return PARSER;
    }
    
    public Array getDefaultInstanceForType() {
      return DEFAULT_INSTANCE;
    }
  }
  
  public static Descriptors.FileDescriptor getDescriptor() {
    return descriptor;
  }
  
  static {
    String[] descriptorData = { "\n\021mysqlx_expr.proto\022\013Mysqlx.Expr\032\026mysqlx_datatypes.proto\"Ä\003\n\004Expr\022$\n\004type\030\001 \002(\0162\026.Mysqlx.Expr.Expr.Type\0221\n\nidentifier\030\002 \001(\0132\035.Mysqlx.Expr.ColumnIdentifier\022\020\n\bvariable\030\003 \001(\t\022)\n\007literal\030\004 \001(\0132\030.Mysqlx.Datatypes.Scalar\0220\n\rfunction_call\030\005 \001(\0132\031.Mysqlx.Expr.FunctionCall\022'\n\boperator\030\006 \001(\0132\025.Mysqlx.Expr.Operator\022\020\n\bposition\030\007 \001(\r\022#\n\006object\030\b \001(\0132\023.Mysqlx.Expr.Object\022!\n\005array\030\t \001(\0132\022.Mysqlx.Expr.Array\"q\n\004Type\022\t\n\005IDENT\020\001\022\013\n\007LITERAL\020\002\022\f\n\bVARIABLE\020\003\022\r\n\tFUNC_CALL\020\004\022\f\n\bOPERATOR\020\005\022\017\n\013PLACEHOLDER\020\006\022\n\n\006OBJECT\020\007\022\t\n\005ARRAY\020\b\"/\n\nIdentifier\022\f\n\004name\030\001 \002(\t\022\023\n\013schema_name\030\002 \001(\t\"Ë\001\n\020DocumentPathItem\0220\n\004type\030\001 \002(\0162\".Mysqlx.Expr.DocumentPathItem.Type\022\r\n\005value\030\002 \001(\t\022\r\n\005index\030\003 \001(\r\"g\n\004Type\022\n\n\006MEMBER\020\001\022\023\n\017MEMBER_ASTERISK\020\002\022\017\n\013ARRAY_INDEX\020\003\022\030\n\024ARRAY_INDEX_ASTERISK\020\004\022\023\n\017DOUBLE_ASTERISK\020\005\"\n\020ColumnIdentifier\0224\n\rdocument_path\030\001 \003(\0132\035.Mysqlx.Expr.DocumentPathItem\022\f\n\004name\030\002 \001(\t\022\022\n\ntable_name\030\003 \001(\t\022\023\n\013schema_name\030\004 \001(\t\"W\n\fFunctionCall\022%\n\004name\030\001 \002(\0132\027.Mysqlx.Expr.Identifier\022 \n\005param\030\002 \003(\0132\021.Mysqlx.Expr.Expr\":\n\bOperator\022\f\n\004name\030\001 \002(\t\022 \n\005param\030\002 \003(\0132\021.Mysqlx.Expr.Expr\"t\n\006Object\022,\n\003fld\030\001 \003(\0132\037.Mysqlx.Expr.Object.ObjectField\032<\n\013ObjectField\022\013\n\003key\030\001 \002(\t\022 \n\005value\030\002 \002(\0132\021.Mysqlx.Expr.Expr\")\n\005Array\022 \n\005value\030\001 \003(\0132\021.Mysqlx.Expr.ExprB\031\n\027com.mysql.cj.x.protobuf" };
    descriptor = Descriptors.FileDescriptor.internalBuildGeneratedFileFrom(descriptorData, new Descriptors.FileDescriptor[] { MysqlxDatatypes.getDescriptor() });
    internal_static_Mysqlx_Expr_Expr_descriptor = getDescriptor().getMessageTypes().get(0);
    internal_static_Mysqlx_Expr_Expr_fieldAccessorTable = new GeneratedMessageV3.FieldAccessorTable(internal_static_Mysqlx_Expr_Expr_descriptor, new String[] { "Type", "Identifier", "Variable", "Literal", "FunctionCall", "Operator", "Position", "Object", "Array" });
    internal_static_Mysqlx_Expr_Identifier_descriptor = getDescriptor().getMessageTypes().get(1);
    internal_static_Mysqlx_Expr_Identifier_fieldAccessorTable = new GeneratedMessageV3.FieldAccessorTable(internal_static_Mysqlx_Expr_Identifier_descriptor, new String[] { "Name", "SchemaName" });
    internal_static_Mysqlx_Expr_DocumentPathItem_descriptor = getDescriptor().getMessageTypes().get(2);
    internal_static_Mysqlx_Expr_DocumentPathItem_fieldAccessorTable = new GeneratedMessageV3.FieldAccessorTable(internal_static_Mysqlx_Expr_DocumentPathItem_descriptor, new String[] { "Type", "Value", "Index" });
    internal_static_Mysqlx_Expr_ColumnIdentifier_descriptor = getDescriptor().getMessageTypes().get(3);
    internal_static_Mysqlx_Expr_ColumnIdentifier_fieldAccessorTable = new GeneratedMessageV3.FieldAccessorTable(internal_static_Mysqlx_Expr_ColumnIdentifier_descriptor, new String[] { "DocumentPath", "Name", "TableName", "SchemaName" });
    internal_static_Mysqlx_Expr_FunctionCall_descriptor = getDescriptor().getMessageTypes().get(4);
    internal_static_Mysqlx_Expr_FunctionCall_fieldAccessorTable = new GeneratedMessageV3.FieldAccessorTable(internal_static_Mysqlx_Expr_FunctionCall_descriptor, new String[] { "Name", "Param" });
    internal_static_Mysqlx_Expr_Operator_descriptor = getDescriptor().getMessageTypes().get(5);
    internal_static_Mysqlx_Expr_Operator_fieldAccessorTable = new GeneratedMessageV3.FieldAccessorTable(internal_static_Mysqlx_Expr_Operator_descriptor, new String[] { "Name", "Param" });
    internal_static_Mysqlx_Expr_Object_descriptor = getDescriptor().getMessageTypes().get(6);
    internal_static_Mysqlx_Expr_Object_fieldAccessorTable = new GeneratedMessageV3.FieldAccessorTable(internal_static_Mysqlx_Expr_Object_descriptor, new String[] { "Fld" });
    internal_static_Mysqlx_Expr_Object_ObjectField_descriptor = internal_static_Mysqlx_Expr_Object_descriptor.getNestedTypes().get(0);
    internal_static_Mysqlx_Expr_Object_ObjectField_fieldAccessorTable = new GeneratedMessageV3.FieldAccessorTable(internal_static_Mysqlx_Expr_Object_ObjectField_descriptor, new String[] { "Key", "Value" });
    internal_static_Mysqlx_Expr_Array_descriptor = getDescriptor().getMessageTypes().get(7);
    internal_static_Mysqlx_Expr_Array_fieldAccessorTable = new GeneratedMessageV3.FieldAccessorTable(internal_static_Mysqlx_Expr_Array_descriptor, new String[] { "Value" });
    MysqlxDatatypes.getDescriptor();
  }
}
