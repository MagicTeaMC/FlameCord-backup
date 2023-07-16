package com.google.protobuf;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.BufferOverflowException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.logging.Level;
import java.util.logging.Logger;

public abstract class CodedOutputStream extends ByteOutput {
  private static final Logger logger = Logger.getLogger(CodedOutputStream.class.getName());
  
  private static final boolean HAS_UNSAFE_ARRAY_OPERATIONS = UnsafeUtil.hasUnsafeArrayOperations();
  
  CodedOutputStreamWriter wrapper;
  
  @Deprecated
  public static final int LITTLE_ENDIAN_32_SIZE = 4;
  
  public static final int DEFAULT_BUFFER_SIZE = 4096;
  
  private boolean serializationDeterministic;
  
  static int computePreferredBufferSize(int dataLength) {
    if (dataLength > 4096)
      return 4096; 
    return dataLength;
  }
  
  public static CodedOutputStream newInstance(OutputStream output) {
    return newInstance(output, 4096);
  }
  
  public static CodedOutputStream newInstance(OutputStream output, int bufferSize) {
    return new OutputStreamEncoder(output, bufferSize);
  }
  
  public static CodedOutputStream newInstance(byte[] flatArray) {
    return newInstance(flatArray, 0, flatArray.length);
  }
  
  public static CodedOutputStream newInstance(byte[] flatArray, int offset, int length) {
    return new ArrayEncoder(flatArray, offset, length);
  }
  
  public static CodedOutputStream newInstance(ByteBuffer buffer) {
    if (buffer.hasArray())
      return new HeapNioEncoder(buffer); 
    if (buffer.isDirect() && !buffer.isReadOnly())
      return UnsafeDirectNioEncoder.isSupported() ? 
        newUnsafeInstance(buffer) : 
        newSafeInstance(buffer); 
    throw new IllegalArgumentException("ByteBuffer is read-only");
  }
  
  static CodedOutputStream newUnsafeInstance(ByteBuffer buffer) {
    return new UnsafeDirectNioEncoder(buffer);
  }
  
  static CodedOutputStream newSafeInstance(ByteBuffer buffer) {
    return new SafeDirectNioEncoder(buffer);
  }
  
  public void useDeterministicSerialization() {
    this.serializationDeterministic = true;
  }
  
  boolean isSerializationDeterministic() {
    return this.serializationDeterministic;
  }
  
  @Deprecated
  public static CodedOutputStream newInstance(ByteBuffer byteBuffer, int unused) {
    return newInstance(byteBuffer);
  }
  
  static CodedOutputStream newInstance(ByteOutput byteOutput, int bufferSize) {
    if (bufferSize < 0)
      throw new IllegalArgumentException("bufferSize must be positive"); 
    return new ByteOutputEncoder(byteOutput, bufferSize);
  }
  
  private CodedOutputStream() {}
  
  public final void writeSInt32(int fieldNumber, int value) throws IOException {
    writeUInt32(fieldNumber, encodeZigZag32(value));
  }
  
  public final void writeSFixed32(int fieldNumber, int value) throws IOException {
    writeFixed32(fieldNumber, value);
  }
  
  public final void writeInt64(int fieldNumber, long value) throws IOException {
    writeUInt64(fieldNumber, value);
  }
  
  public final void writeSInt64(int fieldNumber, long value) throws IOException {
    writeUInt64(fieldNumber, encodeZigZag64(value));
  }
  
  public final void writeSFixed64(int fieldNumber, long value) throws IOException {
    writeFixed64(fieldNumber, value);
  }
  
  public final void writeFloat(int fieldNumber, float value) throws IOException {
    writeFixed32(fieldNumber, Float.floatToRawIntBits(value));
  }
  
  public final void writeDouble(int fieldNumber, double value) throws IOException {
    writeFixed64(fieldNumber, Double.doubleToRawLongBits(value));
  }
  
  public final void writeEnum(int fieldNumber, int value) throws IOException {
    writeInt32(fieldNumber, value);
  }
  
  public final void writeRawByte(byte value) throws IOException {
    write(value);
  }
  
  public final void writeRawByte(int value) throws IOException {
    write((byte)value);
  }
  
  public final void writeRawBytes(byte[] value) throws IOException {
    write(value, 0, value.length);
  }
  
  public final void writeRawBytes(byte[] value, int offset, int length) throws IOException {
    write(value, offset, length);
  }
  
  public final void writeRawBytes(ByteString value) throws IOException {
    value.writeTo(this);
  }
  
  public final void writeSInt32NoTag(int value) throws IOException {
    writeUInt32NoTag(encodeZigZag32(value));
  }
  
  public final void writeSFixed32NoTag(int value) throws IOException {
    writeFixed32NoTag(value);
  }
  
  public final void writeInt64NoTag(long value) throws IOException {
    writeUInt64NoTag(value);
  }
  
  public final void writeSInt64NoTag(long value) throws IOException {
    writeUInt64NoTag(encodeZigZag64(value));
  }
  
  public final void writeSFixed64NoTag(long value) throws IOException {
    writeFixed64NoTag(value);
  }
  
  public final void writeFloatNoTag(float value) throws IOException {
    writeFixed32NoTag(Float.floatToRawIntBits(value));
  }
  
  public final void writeDoubleNoTag(double value) throws IOException {
    writeFixed64NoTag(Double.doubleToRawLongBits(value));
  }
  
  public final void writeBoolNoTag(boolean value) throws IOException {
    write((byte)(value ? 1 : 0));
  }
  
  public final void writeEnumNoTag(int value) throws IOException {
    writeInt32NoTag(value);
  }
  
  public final void writeByteArrayNoTag(byte[] value) throws IOException {
    writeByteArrayNoTag(value, 0, value.length);
  }
  
  public static int computeInt32Size(int fieldNumber, int value) {
    return computeTagSize(fieldNumber) + computeInt32SizeNoTag(value);
  }
  
  public static int computeUInt32Size(int fieldNumber, int value) {
    return computeTagSize(fieldNumber) + computeUInt32SizeNoTag(value);
  }
  
  public static int computeSInt32Size(int fieldNumber, int value) {
    return computeTagSize(fieldNumber) + computeSInt32SizeNoTag(value);
  }
  
  public static int computeFixed32Size(int fieldNumber, int value) {
    return computeTagSize(fieldNumber) + computeFixed32SizeNoTag(value);
  }
  
  public static int computeSFixed32Size(int fieldNumber, int value) {
    return computeTagSize(fieldNumber) + computeSFixed32SizeNoTag(value);
  }
  
  public static int computeInt64Size(int fieldNumber, long value) {
    return computeTagSize(fieldNumber) + computeInt64SizeNoTag(value);
  }
  
  public static int computeUInt64Size(int fieldNumber, long value) {
    return computeTagSize(fieldNumber) + computeUInt64SizeNoTag(value);
  }
  
  public static int computeSInt64Size(int fieldNumber, long value) {
    return computeTagSize(fieldNumber) + computeSInt64SizeNoTag(value);
  }
  
  public static int computeFixed64Size(int fieldNumber, long value) {
    return computeTagSize(fieldNumber) + computeFixed64SizeNoTag(value);
  }
  
  public static int computeSFixed64Size(int fieldNumber, long value) {
    return computeTagSize(fieldNumber) + computeSFixed64SizeNoTag(value);
  }
  
  public static int computeFloatSize(int fieldNumber, float value) {
    return computeTagSize(fieldNumber) + computeFloatSizeNoTag(value);
  }
  
  public static int computeDoubleSize(int fieldNumber, double value) {
    return computeTagSize(fieldNumber) + computeDoubleSizeNoTag(value);
  }
  
  public static int computeBoolSize(int fieldNumber, boolean value) {
    return computeTagSize(fieldNumber) + computeBoolSizeNoTag(value);
  }
  
  public static int computeEnumSize(int fieldNumber, int value) {
    return computeTagSize(fieldNumber) + computeEnumSizeNoTag(value);
  }
  
  public static int computeStringSize(int fieldNumber, String value) {
    return computeTagSize(fieldNumber) + computeStringSizeNoTag(value);
  }
  
  public static int computeBytesSize(int fieldNumber, ByteString value) {
    return computeTagSize(fieldNumber) + computeBytesSizeNoTag(value);
  }
  
  public static int computeByteArraySize(int fieldNumber, byte[] value) {
    return computeTagSize(fieldNumber) + computeByteArraySizeNoTag(value);
  }
  
  public static int computeByteBufferSize(int fieldNumber, ByteBuffer value) {
    return computeTagSize(fieldNumber) + computeByteBufferSizeNoTag(value);
  }
  
  public static int computeLazyFieldSize(int fieldNumber, LazyFieldLite value) {
    return computeTagSize(fieldNumber) + computeLazyFieldSizeNoTag(value);
  }
  
  public static int computeMessageSize(int fieldNumber, MessageLite value) {
    return computeTagSize(fieldNumber) + computeMessageSizeNoTag(value);
  }
  
  static int computeMessageSize(int fieldNumber, MessageLite value, Schema schema) {
    return computeTagSize(fieldNumber) + computeMessageSizeNoTag(value, schema);
  }
  
  public static int computeMessageSetExtensionSize(int fieldNumber, MessageLite value) {
    return computeTagSize(1) * 2 + 
      computeUInt32Size(2, fieldNumber) + 
      computeMessageSize(3, value);
  }
  
  public static int computeRawMessageSetExtensionSize(int fieldNumber, ByteString value) {
    return computeTagSize(1) * 2 + 
      computeUInt32Size(2, fieldNumber) + 
      computeBytesSize(3, value);
  }
  
  public static int computeLazyFieldMessageSetExtensionSize(int fieldNumber, LazyFieldLite value) {
    return computeTagSize(1) * 2 + 
      computeUInt32Size(2, fieldNumber) + 
      computeLazyFieldSize(3, value);
  }
  
  public static int computeTagSize(int fieldNumber) {
    return computeUInt32SizeNoTag(WireFormat.makeTag(fieldNumber, 0));
  }
  
  public static int computeInt32SizeNoTag(int value) {
    if (value >= 0)
      return computeUInt32SizeNoTag(value); 
    return 10;
  }
  
  public static int computeUInt32SizeNoTag(int value) {
    if ((value & 0xFFFFFF80) == 0)
      return 1; 
    if ((value & 0xFFFFC000) == 0)
      return 2; 
    if ((value & 0xFFE00000) == 0)
      return 3; 
    if ((value & 0xF0000000) == 0)
      return 4; 
    return 5;
  }
  
  public static int computeSInt32SizeNoTag(int value) {
    return computeUInt32SizeNoTag(encodeZigZag32(value));
  }
  
  public static int computeFixed32SizeNoTag(int unused) {
    return 4;
  }
  
  public static int computeSFixed32SizeNoTag(int unused) {
    return 4;
  }
  
  public static int computeInt64SizeNoTag(long value) {
    return computeUInt64SizeNoTag(value);
  }
  
  public static int computeUInt64SizeNoTag(long value) {
    if ((value & 0xFFFFFFFFFFFFFF80L) == 0L)
      return 1; 
    if (value < 0L)
      return 10; 
    int n = 2;
    if ((value & 0xFFFFFFF800000000L) != 0L) {
      n += 4;
      value >>>= 28L;
    } 
    if ((value & 0xFFFFFFFFFFE00000L) != 0L) {
      n += 2;
      value >>>= 14L;
    } 
    if ((value & 0xFFFFFFFFFFFFC000L) != 0L)
      n++; 
    return n;
  }
  
  public static int computeSInt64SizeNoTag(long value) {
    return computeUInt64SizeNoTag(encodeZigZag64(value));
  }
  
  public static int computeFixed64SizeNoTag(long unused) {
    return 8;
  }
  
  public static int computeSFixed64SizeNoTag(long unused) {
    return 8;
  }
  
  public static int computeFloatSizeNoTag(float unused) {
    return 4;
  }
  
  public static int computeDoubleSizeNoTag(double unused) {
    return 8;
  }
  
  public static int computeBoolSizeNoTag(boolean unused) {
    return 1;
  }
  
  public static int computeEnumSizeNoTag(int value) {
    return computeInt32SizeNoTag(value);
  }
  
  public static int computeStringSizeNoTag(String value) {
    int length;
    try {
      length = Utf8.encodedLength(value);
    } catch (UnpairedSurrogateException e) {
      byte[] bytes = value.getBytes(Internal.UTF_8);
      length = bytes.length;
    } 
    return computeLengthDelimitedFieldSize(length);
  }
  
  public static int computeLazyFieldSizeNoTag(LazyFieldLite value) {
    return computeLengthDelimitedFieldSize(value.getSerializedSize());
  }
  
  public static int computeBytesSizeNoTag(ByteString value) {
    return computeLengthDelimitedFieldSize(value.size());
  }
  
  public static int computeByteArraySizeNoTag(byte[] value) {
    return computeLengthDelimitedFieldSize(value.length);
  }
  
  public static int computeByteBufferSizeNoTag(ByteBuffer value) {
    return computeLengthDelimitedFieldSize(value.capacity());
  }
  
  public static int computeMessageSizeNoTag(MessageLite value) {
    return computeLengthDelimitedFieldSize(value.getSerializedSize());
  }
  
  static int computeMessageSizeNoTag(MessageLite value, Schema schema) {
    return computeLengthDelimitedFieldSize(((AbstractMessageLite)value).getSerializedSize(schema));
  }
  
  static int computeLengthDelimitedFieldSize(int fieldLength) {
    return computeUInt32SizeNoTag(fieldLength) + fieldLength;
  }
  
  public static int encodeZigZag32(int n) {
    return n << 1 ^ n >> 31;
  }
  
  public static long encodeZigZag64(long n) {
    return n << 1L ^ n >> 63L;
  }
  
  public final void checkNoSpaceLeft() {
    if (spaceLeft() != 0)
      throw new IllegalStateException("Did not write as much data as expected."); 
  }
  
  public static class OutOfSpaceException extends IOException {
    private static final long serialVersionUID = -6947486886997889499L;
    
    private static final String MESSAGE = "CodedOutputStream was writing to a flat byte array and ran out of space.";
    
    OutOfSpaceException() {
      super("CodedOutputStream was writing to a flat byte array and ran out of space.");
    }
    
    OutOfSpaceException(String explanationMessage) {
      super("CodedOutputStream was writing to a flat byte array and ran out of space.: " + explanationMessage);
    }
    
    OutOfSpaceException(Throwable cause) {
      super("CodedOutputStream was writing to a flat byte array and ran out of space.", cause);
    }
    
    OutOfSpaceException(String explanationMessage, Throwable cause) {
      super("CodedOutputStream was writing to a flat byte array and ran out of space.: " + explanationMessage, cause);
    }
  }
  
  final void inefficientWriteStringNoTag(String value, Utf8.UnpairedSurrogateException cause) throws IOException {
    logger.log(Level.WARNING, "Converting ill-formed UTF-16. Your Protocol Buffer will not round trip correctly!", cause);
    byte[] bytes = value.getBytes(Internal.UTF_8);
    try {
      writeUInt32NoTag(bytes.length);
      writeLazy(bytes, 0, bytes.length);
    } catch (IndexOutOfBoundsException e) {
      throw new OutOfSpaceException(e);
    } 
  }
  
  @Deprecated
  public final void writeGroup(int fieldNumber, MessageLite value) throws IOException {
    writeTag(fieldNumber, 3);
    writeGroupNoTag(value);
    writeTag(fieldNumber, 4);
  }
  
  @Deprecated
  final void writeGroup(int fieldNumber, MessageLite value, Schema schema) throws IOException {
    writeTag(fieldNumber, 3);
    writeGroupNoTag(value, schema);
    writeTag(fieldNumber, 4);
  }
  
  @Deprecated
  public final void writeGroupNoTag(MessageLite value) throws IOException {
    value.writeTo(this);
  }
  
  @Deprecated
  final void writeGroupNoTag(MessageLite value, Schema<MessageLite> schema) throws IOException {
    schema.writeTo(value, this.wrapper);
  }
  
  @Deprecated
  public static int computeGroupSize(int fieldNumber, MessageLite value) {
    return computeTagSize(fieldNumber) * 2 + value.getSerializedSize();
  }
  
  @Deprecated
  static int computeGroupSize(int fieldNumber, MessageLite value, Schema schema) {
    return computeTagSize(fieldNumber) * 2 + computeGroupSizeNoTag(value, schema);
  }
  
  @Deprecated
  @InlineMe(replacement = "value.getSerializedSize()")
  public static int computeGroupSizeNoTag(MessageLite value) {
    return value.getSerializedSize();
  }
  
  @Deprecated
  static int computeGroupSizeNoTag(MessageLite value, Schema schema) {
    return ((AbstractMessageLite)value).getSerializedSize(schema);
  }
  
  @Deprecated
  @InlineMe(replacement = "this.writeUInt32NoTag(value)")
  public final void writeRawVarint32(int value) throws IOException {
    writeUInt32NoTag(value);
  }
  
  @Deprecated
  @InlineMe(replacement = "this.writeUInt64NoTag(value)")
  public final void writeRawVarint64(long value) throws IOException {
    writeUInt64NoTag(value);
  }
  
  @Deprecated
  @InlineMe(replacement = "CodedOutputStream.computeUInt32SizeNoTag(value)", imports = {"com.google.protobuf.CodedOutputStream"})
  public static int computeRawVarint32Size(int value) {
    return computeUInt32SizeNoTag(value);
  }
  
  @Deprecated
  @InlineMe(replacement = "CodedOutputStream.computeUInt64SizeNoTag(value)", imports = {"com.google.protobuf.CodedOutputStream"})
  public static int computeRawVarint64Size(long value) {
    return computeUInt64SizeNoTag(value);
  }
  
  @Deprecated
  @InlineMe(replacement = "this.writeFixed32NoTag(value)")
  public final void writeRawLittleEndian32(int value) throws IOException {
    writeFixed32NoTag(value);
  }
  
  @Deprecated
  @InlineMe(replacement = "this.writeFixed64NoTag(value)")
  public final void writeRawLittleEndian64(long value) throws IOException {
    writeFixed64NoTag(value);
  }
  
  public abstract void writeTag(int paramInt1, int paramInt2) throws IOException;
  
  public abstract void writeInt32(int paramInt1, int paramInt2) throws IOException;
  
  public abstract void writeUInt32(int paramInt1, int paramInt2) throws IOException;
  
  public abstract void writeFixed32(int paramInt1, int paramInt2) throws IOException;
  
  public abstract void writeUInt64(int paramInt, long paramLong) throws IOException;
  
  public abstract void writeFixed64(int paramInt, long paramLong) throws IOException;
  
  public abstract void writeBool(int paramInt, boolean paramBoolean) throws IOException;
  
  public abstract void writeString(int paramInt, String paramString) throws IOException;
  
  public abstract void writeBytes(int paramInt, ByteString paramByteString) throws IOException;
  
  public abstract void writeByteArray(int paramInt, byte[] paramArrayOfbyte) throws IOException;
  
  public abstract void writeByteArray(int paramInt1, byte[] paramArrayOfbyte, int paramInt2, int paramInt3) throws IOException;
  
  public abstract void writeByteBuffer(int paramInt, ByteBuffer paramByteBuffer) throws IOException;
  
  public abstract void writeRawBytes(ByteBuffer paramByteBuffer) throws IOException;
  
  public abstract void writeMessage(int paramInt, MessageLite paramMessageLite) throws IOException;
  
  abstract void writeMessage(int paramInt, MessageLite paramMessageLite, Schema paramSchema) throws IOException;
  
  public abstract void writeMessageSetExtension(int paramInt, MessageLite paramMessageLite) throws IOException;
  
  public abstract void writeRawMessageSetExtension(int paramInt, ByteString paramByteString) throws IOException;
  
  public abstract void writeInt32NoTag(int paramInt) throws IOException;
  
  public abstract void writeUInt32NoTag(int paramInt) throws IOException;
  
  public abstract void writeFixed32NoTag(int paramInt) throws IOException;
  
  public abstract void writeUInt64NoTag(long paramLong) throws IOException;
  
  public abstract void writeFixed64NoTag(long paramLong) throws IOException;
  
  public abstract void writeStringNoTag(String paramString) throws IOException;
  
  public abstract void writeBytesNoTag(ByteString paramByteString) throws IOException;
  
  public abstract void writeMessageNoTag(MessageLite paramMessageLite) throws IOException;
  
  abstract void writeMessageNoTag(MessageLite paramMessageLite, Schema paramSchema) throws IOException;
  
  public abstract void write(byte paramByte) throws IOException;
  
  public abstract void write(byte[] paramArrayOfbyte, int paramInt1, int paramInt2) throws IOException;
  
  public abstract void writeLazy(byte[] paramArrayOfbyte, int paramInt1, int paramInt2) throws IOException;
  
  public abstract void write(ByteBuffer paramByteBuffer) throws IOException;
  
  public abstract void writeLazy(ByteBuffer paramByteBuffer) throws IOException;
  
  public abstract void flush() throws IOException;
  
  public abstract int spaceLeft();
  
  public abstract int getTotalBytesWritten();
  
  abstract void writeByteArrayNoTag(byte[] paramArrayOfbyte, int paramInt1, int paramInt2) throws IOException;
  
  private static class ArrayEncoder extends CodedOutputStream {
    private final byte[] buffer;
    
    private final int offset;
    
    private final int limit;
    
    private int position;
    
    ArrayEncoder(byte[] buffer, int offset, int length) {
      if (buffer == null)
        throw new NullPointerException("buffer"); 
      if ((offset | length | buffer.length - offset + length) < 0)
        throw new IllegalArgumentException(
            String.format("Array range is invalid. Buffer.length=%d, offset=%d, length=%d", new Object[] { Integer.valueOf(buffer.length), Integer.valueOf(offset), Integer.valueOf(length) })); 
      this.buffer = buffer;
      this.offset = offset;
      this.position = offset;
      this.limit = offset + length;
    }
    
    public final void writeTag(int fieldNumber, int wireType) throws IOException {
      writeUInt32NoTag(WireFormat.makeTag(fieldNumber, wireType));
    }
    
    public final void writeInt32(int fieldNumber, int value) throws IOException {
      writeTag(fieldNumber, 0);
      writeInt32NoTag(value);
    }
    
    public final void writeUInt32(int fieldNumber, int value) throws IOException {
      writeTag(fieldNumber, 0);
      writeUInt32NoTag(value);
    }
    
    public final void writeFixed32(int fieldNumber, int value) throws IOException {
      writeTag(fieldNumber, 5);
      writeFixed32NoTag(value);
    }
    
    public final void writeUInt64(int fieldNumber, long value) throws IOException {
      writeTag(fieldNumber, 0);
      writeUInt64NoTag(value);
    }
    
    public final void writeFixed64(int fieldNumber, long value) throws IOException {
      writeTag(fieldNumber, 1);
      writeFixed64NoTag(value);
    }
    
    public final void writeBool(int fieldNumber, boolean value) throws IOException {
      writeTag(fieldNumber, 0);
      write((byte)(value ? 1 : 0));
    }
    
    public final void writeString(int fieldNumber, String value) throws IOException {
      writeTag(fieldNumber, 2);
      writeStringNoTag(value);
    }
    
    public final void writeBytes(int fieldNumber, ByteString value) throws IOException {
      writeTag(fieldNumber, 2);
      writeBytesNoTag(value);
    }
    
    public final void writeByteArray(int fieldNumber, byte[] value) throws IOException {
      writeByteArray(fieldNumber, value, 0, value.length);
    }
    
    public final void writeByteArray(int fieldNumber, byte[] value, int offset, int length) throws IOException {
      writeTag(fieldNumber, 2);
      writeByteArrayNoTag(value, offset, length);
    }
    
    public final void writeByteBuffer(int fieldNumber, ByteBuffer value) throws IOException {
      writeTag(fieldNumber, 2);
      writeUInt32NoTag(value.capacity());
      writeRawBytes(value);
    }
    
    public final void writeBytesNoTag(ByteString value) throws IOException {
      writeUInt32NoTag(value.size());
      value.writeTo(this);
    }
    
    public final void writeByteArrayNoTag(byte[] value, int offset, int length) throws IOException {
      writeUInt32NoTag(length);
      write(value, offset, length);
    }
    
    public final void writeRawBytes(ByteBuffer value) throws IOException {
      if (value.hasArray()) {
        write(value.array(), value.arrayOffset(), value.capacity());
      } else {
        ByteBuffer duplicated = value.duplicate();
        duplicated.clear();
        write(duplicated);
      } 
    }
    
    public final void writeMessage(int fieldNumber, MessageLite value) throws IOException {
      writeTag(fieldNumber, 2);
      writeMessageNoTag(value);
    }
    
    final void writeMessage(int fieldNumber, MessageLite value, Schema<MessageLite> schema) throws IOException {
      writeTag(fieldNumber, 2);
      writeUInt32NoTag(((AbstractMessageLite)value).getSerializedSize(schema));
      schema.writeTo(value, this.wrapper);
    }
    
    public final void writeMessageSetExtension(int fieldNumber, MessageLite value) throws IOException {
      writeTag(1, 3);
      writeUInt32(2, fieldNumber);
      writeMessage(3, value);
      writeTag(1, 4);
    }
    
    public final void writeRawMessageSetExtension(int fieldNumber, ByteString value) throws IOException {
      writeTag(1, 3);
      writeUInt32(2, fieldNumber);
      writeBytes(3, value);
      writeTag(1, 4);
    }
    
    public final void writeMessageNoTag(MessageLite value) throws IOException {
      writeUInt32NoTag(value.getSerializedSize());
      value.writeTo(this);
    }
    
    final void writeMessageNoTag(MessageLite value, Schema<MessageLite> schema) throws IOException {
      writeUInt32NoTag(((AbstractMessageLite)value).getSerializedSize(schema));
      schema.writeTo(value, this.wrapper);
    }
    
    public final void write(byte value) throws IOException {
      try {
        this.buffer[this.position++] = value;
      } catch (IndexOutOfBoundsException e) {
        throw new CodedOutputStream.OutOfSpaceException(
            String.format("Pos: %d, limit: %d, len: %d", new Object[] { Integer.valueOf(this.position), Integer.valueOf(this.limit), Integer.valueOf(1) }), e);
      } 
    }
    
    public final void writeInt32NoTag(int value) throws IOException {
      if (value >= 0) {
        writeUInt32NoTag(value);
      } else {
        writeUInt64NoTag(value);
      } 
    }
    
    public final void writeUInt32NoTag(int value) throws IOException {
      try {
        while (true) {
          if ((value & 0xFFFFFF80) == 0) {
            this.buffer[this.position++] = (byte)value;
            return;
          } 
          this.buffer[this.position++] = (byte)(value & 0x7F | 0x80);
          value >>>= 7;
        } 
      } catch (IndexOutOfBoundsException e) {
        throw new CodedOutputStream.OutOfSpaceException(
            String.format("Pos: %d, limit: %d, len: %d", new Object[] { Integer.valueOf(this.position), Integer.valueOf(this.limit), Integer.valueOf(1) }), e);
      } 
    }
    
    public final void writeFixed32NoTag(int value) throws IOException {
      try {
        this.buffer[this.position++] = (byte)(value & 0xFF);
        this.buffer[this.position++] = (byte)(value >> 8 & 0xFF);
        this.buffer[this.position++] = (byte)(value >> 16 & 0xFF);
        this.buffer[this.position++] = (byte)(value >> 24 & 0xFF);
      } catch (IndexOutOfBoundsException e) {
        throw new CodedOutputStream.OutOfSpaceException(
            String.format("Pos: %d, limit: %d, len: %d", new Object[] { Integer.valueOf(this.position), Integer.valueOf(this.limit), Integer.valueOf(1) }), e);
      } 
    }
    
    public final void writeUInt64NoTag(long value) throws IOException {
      if (CodedOutputStream.HAS_UNSAFE_ARRAY_OPERATIONS) {
        if (spaceLeft() >= 10)
          while (true) {
            if ((value & 0xFFFFFFFFFFFFFF80L) == 0L) {
              UnsafeUtil.putByte(this.buffer, this.position++, (byte)(int)value);
              return;
            } 
            UnsafeUtil.putByte(this.buffer, this.position++, (byte)((int)value & 0x7F | 0x80));
            value >>>= 7L;
          }  
      } else {
        try {
          while (true) {
            if ((value & 0xFFFFFFFFFFFFFF80L) == 0L) {
              this.buffer[this.position++] = (byte)(int)value;
              return;
            } 
            this.buffer[this.position++] = (byte)((int)value & 0x7F | 0x80);
            value >>>= 7L;
          } 
        } catch (IndexOutOfBoundsException e) {
          throw new CodedOutputStream.OutOfSpaceException(
              String.format("Pos: %d, limit: %d, len: %d", new Object[] { Integer.valueOf(this.position), Integer.valueOf(this.limit), Integer.valueOf(1) }), e);
        } 
      } 
      while (true) {
        if ((value & 0xFFFFFFFFFFFFFF80L) == 0L) {
          this.buffer[this.position++] = (byte)(int)value;
          return;
        } 
        this.buffer[this.position++] = (byte)((int)value & 0x7F | 0x80);
        value >>>= 7L;
      } 
    }
    
    public final void writeFixed64NoTag(long value) throws IOException {
      try {
        this.buffer[this.position++] = (byte)((int)value & 0xFF);
        this.buffer[this.position++] = (byte)((int)(value >> 8L) & 0xFF);
        this.buffer[this.position++] = (byte)((int)(value >> 16L) & 0xFF);
        this.buffer[this.position++] = (byte)((int)(value >> 24L) & 0xFF);
        this.buffer[this.position++] = (byte)((int)(value >> 32L) & 0xFF);
        this.buffer[this.position++] = (byte)((int)(value >> 40L) & 0xFF);
        this.buffer[this.position++] = (byte)((int)(value >> 48L) & 0xFF);
        this.buffer[this.position++] = (byte)((int)(value >> 56L) & 0xFF);
      } catch (IndexOutOfBoundsException e) {
        throw new CodedOutputStream.OutOfSpaceException(
            String.format("Pos: %d, limit: %d, len: %d", new Object[] { Integer.valueOf(this.position), Integer.valueOf(this.limit), Integer.valueOf(1) }), e);
      } 
    }
    
    public final void write(byte[] value, int offset, int length) throws IOException {
      try {
        System.arraycopy(value, offset, this.buffer, this.position, length);
        this.position += length;
      } catch (IndexOutOfBoundsException e) {
        throw new CodedOutputStream.OutOfSpaceException(
            String.format("Pos: %d, limit: %d, len: %d", new Object[] { Integer.valueOf(this.position), Integer.valueOf(this.limit), Integer.valueOf(length) }), e);
      } 
    }
    
    public final void writeLazy(byte[] value, int offset, int length) throws IOException {
      write(value, offset, length);
    }
    
    public final void write(ByteBuffer value) throws IOException {
      int length = value.remaining();
      try {
        value.get(this.buffer, this.position, length);
        this.position += length;
      } catch (IndexOutOfBoundsException e) {
        throw new CodedOutputStream.OutOfSpaceException(
            String.format("Pos: %d, limit: %d, len: %d", new Object[] { Integer.valueOf(this.position), Integer.valueOf(this.limit), Integer.valueOf(length) }), e);
      } 
    }
    
    public final void writeLazy(ByteBuffer value) throws IOException {
      write(value);
    }
    
    public final void writeStringNoTag(String value) throws IOException {
      int oldPosition = this.position;
      try {
        int maxLength = value.length() * 3;
        int maxLengthVarIntSize = computeUInt32SizeNoTag(maxLength);
        int minLengthVarIntSize = computeUInt32SizeNoTag(value.length());
        if (minLengthVarIntSize == maxLengthVarIntSize) {
          this.position = oldPosition + minLengthVarIntSize;
          int newPosition = Utf8.encode(value, this.buffer, this.position, spaceLeft());
          this.position = oldPosition;
          int length = newPosition - oldPosition - minLengthVarIntSize;
          writeUInt32NoTag(length);
          this.position = newPosition;
        } else {
          int length = Utf8.encodedLength(value);
          writeUInt32NoTag(length);
          this.position = Utf8.encode(value, this.buffer, this.position, spaceLeft());
        } 
      } catch (UnpairedSurrogateException e) {
        this.position = oldPosition;
        inefficientWriteStringNoTag(value, e);
      } catch (IndexOutOfBoundsException e) {
        throw new CodedOutputStream.OutOfSpaceException(e);
      } 
    }
    
    public void flush() {}
    
    public final int spaceLeft() {
      return this.limit - this.position;
    }
    
    public final int getTotalBytesWritten() {
      return this.position - this.offset;
    }
  }
  
  private static final class HeapNioEncoder extends ArrayEncoder {
    private final ByteBuffer byteBuffer;
    
    private int initialPosition;
    
    HeapNioEncoder(ByteBuffer byteBuffer) {
      super(byteBuffer
          .array(), byteBuffer
          .arrayOffset() + byteBuffer.position(), byteBuffer
          .remaining());
      this.byteBuffer = byteBuffer;
      this.initialPosition = byteBuffer.position();
    }
    
    public void flush() {
      this.byteBuffer.position(this.initialPosition + getTotalBytesWritten());
    }
  }
  
  private static final class SafeDirectNioEncoder extends CodedOutputStream {
    private final ByteBuffer originalBuffer;
    
    private final ByteBuffer buffer;
    
    private final int initialPosition;
    
    SafeDirectNioEncoder(ByteBuffer buffer) {
      this.originalBuffer = buffer;
      this.buffer = buffer.duplicate().order(ByteOrder.LITTLE_ENDIAN);
      this.initialPosition = buffer.position();
    }
    
    public void writeTag(int fieldNumber, int wireType) throws IOException {
      writeUInt32NoTag(WireFormat.makeTag(fieldNumber, wireType));
    }
    
    public void writeInt32(int fieldNumber, int value) throws IOException {
      writeTag(fieldNumber, 0);
      writeInt32NoTag(value);
    }
    
    public void writeUInt32(int fieldNumber, int value) throws IOException {
      writeTag(fieldNumber, 0);
      writeUInt32NoTag(value);
    }
    
    public void writeFixed32(int fieldNumber, int value) throws IOException {
      writeTag(fieldNumber, 5);
      writeFixed32NoTag(value);
    }
    
    public void writeUInt64(int fieldNumber, long value) throws IOException {
      writeTag(fieldNumber, 0);
      writeUInt64NoTag(value);
    }
    
    public void writeFixed64(int fieldNumber, long value) throws IOException {
      writeTag(fieldNumber, 1);
      writeFixed64NoTag(value);
    }
    
    public void writeBool(int fieldNumber, boolean value) throws IOException {
      writeTag(fieldNumber, 0);
      write((byte)(value ? 1 : 0));
    }
    
    public void writeString(int fieldNumber, String value) throws IOException {
      writeTag(fieldNumber, 2);
      writeStringNoTag(value);
    }
    
    public void writeBytes(int fieldNumber, ByteString value) throws IOException {
      writeTag(fieldNumber, 2);
      writeBytesNoTag(value);
    }
    
    public void writeByteArray(int fieldNumber, byte[] value) throws IOException {
      writeByteArray(fieldNumber, value, 0, value.length);
    }
    
    public void writeByteArray(int fieldNumber, byte[] value, int offset, int length) throws IOException {
      writeTag(fieldNumber, 2);
      writeByteArrayNoTag(value, offset, length);
    }
    
    public void writeByteBuffer(int fieldNumber, ByteBuffer value) throws IOException {
      writeTag(fieldNumber, 2);
      writeUInt32NoTag(value.capacity());
      writeRawBytes(value);
    }
    
    public void writeMessage(int fieldNumber, MessageLite value) throws IOException {
      writeTag(fieldNumber, 2);
      writeMessageNoTag(value);
    }
    
    void writeMessage(int fieldNumber, MessageLite value, Schema schema) throws IOException {
      writeTag(fieldNumber, 2);
      writeMessageNoTag(value, schema);
    }
    
    public void writeMessageSetExtension(int fieldNumber, MessageLite value) throws IOException {
      writeTag(1, 3);
      writeUInt32(2, fieldNumber);
      writeMessage(3, value);
      writeTag(1, 4);
    }
    
    public void writeRawMessageSetExtension(int fieldNumber, ByteString value) throws IOException {
      writeTag(1, 3);
      writeUInt32(2, fieldNumber);
      writeBytes(3, value);
      writeTag(1, 4);
    }
    
    public void writeMessageNoTag(MessageLite value) throws IOException {
      writeUInt32NoTag(value.getSerializedSize());
      value.writeTo(this);
    }
    
    void writeMessageNoTag(MessageLite value, Schema<MessageLite> schema) throws IOException {
      writeUInt32NoTag(((AbstractMessageLite)value).getSerializedSize(schema));
      schema.writeTo(value, this.wrapper);
    }
    
    public void write(byte value) throws IOException {
      try {
        this.buffer.put(value);
      } catch (BufferOverflowException e) {
        throw new CodedOutputStream.OutOfSpaceException(e);
      } 
    }
    
    public void writeBytesNoTag(ByteString value) throws IOException {
      writeUInt32NoTag(value.size());
      value.writeTo(this);
    }
    
    public void writeByteArrayNoTag(byte[] value, int offset, int length) throws IOException {
      writeUInt32NoTag(length);
      write(value, offset, length);
    }
    
    public void writeRawBytes(ByteBuffer value) throws IOException {
      if (value.hasArray()) {
        write(value.array(), value.arrayOffset(), value.capacity());
      } else {
        ByteBuffer duplicated = value.duplicate();
        duplicated.clear();
        write(duplicated);
      } 
    }
    
    public void writeInt32NoTag(int value) throws IOException {
      if (value >= 0) {
        writeUInt32NoTag(value);
      } else {
        writeUInt64NoTag(value);
      } 
    }
    
    public void writeUInt32NoTag(int value) throws IOException {
      try {
        while (true) {
          if ((value & 0xFFFFFF80) == 0) {
            this.buffer.put((byte)value);
            return;
          } 
          this.buffer.put((byte)(value & 0x7F | 0x80));
          value >>>= 7;
        } 
      } catch (BufferOverflowException e) {
        throw new CodedOutputStream.OutOfSpaceException(e);
      } 
    }
    
    public void writeFixed32NoTag(int value) throws IOException {
      try {
        this.buffer.putInt(value);
      } catch (BufferOverflowException e) {
        throw new CodedOutputStream.OutOfSpaceException(e);
      } 
    }
    
    public void writeUInt64NoTag(long value) throws IOException {
      try {
        while (true) {
          if ((value & 0xFFFFFFFFFFFFFF80L) == 0L) {
            this.buffer.put((byte)(int)value);
            return;
          } 
          this.buffer.put((byte)((int)value & 0x7F | 0x80));
          value >>>= 7L;
        } 
      } catch (BufferOverflowException e) {
        throw new CodedOutputStream.OutOfSpaceException(e);
      } 
    }
    
    public void writeFixed64NoTag(long value) throws IOException {
      try {
        this.buffer.putLong(value);
      } catch (BufferOverflowException e) {
        throw new CodedOutputStream.OutOfSpaceException(e);
      } 
    }
    
    public void write(byte[] value, int offset, int length) throws IOException {
      try {
        this.buffer.put(value, offset, length);
      } catch (IndexOutOfBoundsException e) {
        throw new CodedOutputStream.OutOfSpaceException(e);
      } catch (BufferOverflowException e) {
        throw new CodedOutputStream.OutOfSpaceException(e);
      } 
    }
    
    public void writeLazy(byte[] value, int offset, int length) throws IOException {
      write(value, offset, length);
    }
    
    public void write(ByteBuffer value) throws IOException {
      try {
        this.buffer.put(value);
      } catch (BufferOverflowException e) {
        throw new CodedOutputStream.OutOfSpaceException(e);
      } 
    }
    
    public void writeLazy(ByteBuffer value) throws IOException {
      write(value);
    }
    
    public void writeStringNoTag(String value) throws IOException {
      int startPos = this.buffer.position();
      try {
        int maxEncodedSize = value.length() * 3;
        int maxLengthVarIntSize = computeUInt32SizeNoTag(maxEncodedSize);
        int minLengthVarIntSize = computeUInt32SizeNoTag(value.length());
        if (minLengthVarIntSize == maxLengthVarIntSize) {
          int startOfBytes = this.buffer.position() + minLengthVarIntSize;
          this.buffer.position(startOfBytes);
          encode(value);
          int endOfBytes = this.buffer.position();
          this.buffer.position(startPos);
          writeUInt32NoTag(endOfBytes - startOfBytes);
          this.buffer.position(endOfBytes);
        } else {
          int length = Utf8.encodedLength(value);
          writeUInt32NoTag(length);
          encode(value);
        } 
      } catch (UnpairedSurrogateException e) {
        this.buffer.position(startPos);
        inefficientWriteStringNoTag(value, e);
      } catch (IllegalArgumentException e) {
        throw new CodedOutputStream.OutOfSpaceException(e);
      } 
    }
    
    public void flush() {
      this.originalBuffer.position(this.buffer.position());
    }
    
    public int spaceLeft() {
      return this.buffer.remaining();
    }
    
    public int getTotalBytesWritten() {
      return this.buffer.position() - this.initialPosition;
    }
    
    private void encode(String value) throws IOException {
      try {
        Utf8.encodeUtf8(value, this.buffer);
      } catch (IndexOutOfBoundsException e) {
        throw new CodedOutputStream.OutOfSpaceException(e);
      } 
    }
  }
  
  private static final class UnsafeDirectNioEncoder extends CodedOutputStream {
    private final ByteBuffer originalBuffer;
    
    private final ByteBuffer buffer;
    
    private final long address;
    
    private final long initialPosition;
    
    private final long limit;
    
    private final long oneVarintLimit;
    
    private long position;
    
    UnsafeDirectNioEncoder(ByteBuffer buffer) {
      this.originalBuffer = buffer;
      this.buffer = buffer.duplicate().order(ByteOrder.LITTLE_ENDIAN);
      this.address = UnsafeUtil.addressOffset(buffer);
      this.initialPosition = this.address + buffer.position();
      this.limit = this.address + buffer.limit();
      this.oneVarintLimit = this.limit - 10L;
      this.position = this.initialPosition;
    }
    
    static boolean isSupported() {
      return UnsafeUtil.hasUnsafeByteBufferOperations();
    }
    
    public void writeTag(int fieldNumber, int wireType) throws IOException {
      writeUInt32NoTag(WireFormat.makeTag(fieldNumber, wireType));
    }
    
    public void writeInt32(int fieldNumber, int value) throws IOException {
      writeTag(fieldNumber, 0);
      writeInt32NoTag(value);
    }
    
    public void writeUInt32(int fieldNumber, int value) throws IOException {
      writeTag(fieldNumber, 0);
      writeUInt32NoTag(value);
    }
    
    public void writeFixed32(int fieldNumber, int value) throws IOException {
      writeTag(fieldNumber, 5);
      writeFixed32NoTag(value);
    }
    
    public void writeUInt64(int fieldNumber, long value) throws IOException {
      writeTag(fieldNumber, 0);
      writeUInt64NoTag(value);
    }
    
    public void writeFixed64(int fieldNumber, long value) throws IOException {
      writeTag(fieldNumber, 1);
      writeFixed64NoTag(value);
    }
    
    public void writeBool(int fieldNumber, boolean value) throws IOException {
      writeTag(fieldNumber, 0);
      write((byte)(value ? 1 : 0));
    }
    
    public void writeString(int fieldNumber, String value) throws IOException {
      writeTag(fieldNumber, 2);
      writeStringNoTag(value);
    }
    
    public void writeBytes(int fieldNumber, ByteString value) throws IOException {
      writeTag(fieldNumber, 2);
      writeBytesNoTag(value);
    }
    
    public void writeByteArray(int fieldNumber, byte[] value) throws IOException {
      writeByteArray(fieldNumber, value, 0, value.length);
    }
    
    public void writeByteArray(int fieldNumber, byte[] value, int offset, int length) throws IOException {
      writeTag(fieldNumber, 2);
      writeByteArrayNoTag(value, offset, length);
    }
    
    public void writeByteBuffer(int fieldNumber, ByteBuffer value) throws IOException {
      writeTag(fieldNumber, 2);
      writeUInt32NoTag(value.capacity());
      writeRawBytes(value);
    }
    
    public void writeMessage(int fieldNumber, MessageLite value) throws IOException {
      writeTag(fieldNumber, 2);
      writeMessageNoTag(value);
    }
    
    void writeMessage(int fieldNumber, MessageLite value, Schema schema) throws IOException {
      writeTag(fieldNumber, 2);
      writeMessageNoTag(value, schema);
    }
    
    public void writeMessageSetExtension(int fieldNumber, MessageLite value) throws IOException {
      writeTag(1, 3);
      writeUInt32(2, fieldNumber);
      writeMessage(3, value);
      writeTag(1, 4);
    }
    
    public void writeRawMessageSetExtension(int fieldNumber, ByteString value) throws IOException {
      writeTag(1, 3);
      writeUInt32(2, fieldNumber);
      writeBytes(3, value);
      writeTag(1, 4);
    }
    
    public void writeMessageNoTag(MessageLite value) throws IOException {
      writeUInt32NoTag(value.getSerializedSize());
      value.writeTo(this);
    }
    
    void writeMessageNoTag(MessageLite value, Schema<MessageLite> schema) throws IOException {
      writeUInt32NoTag(((AbstractMessageLite)value).getSerializedSize(schema));
      schema.writeTo(value, this.wrapper);
    }
    
    public void write(byte value) throws IOException {
      if (this.position >= this.limit)
        throw new CodedOutputStream.OutOfSpaceException(
            String.format("Pos: %d, limit: %d, len: %d", new Object[] { Long.valueOf(this.position), Long.valueOf(this.limit), Integer.valueOf(1) })); 
      UnsafeUtil.putByte(this.position++, value);
    }
    
    public void writeBytesNoTag(ByteString value) throws IOException {
      writeUInt32NoTag(value.size());
      value.writeTo(this);
    }
    
    public void writeByteArrayNoTag(byte[] value, int offset, int length) throws IOException {
      writeUInt32NoTag(length);
      write(value, offset, length);
    }
    
    public void writeRawBytes(ByteBuffer value) throws IOException {
      if (value.hasArray()) {
        write(value.array(), value.arrayOffset(), value.capacity());
      } else {
        ByteBuffer duplicated = value.duplicate();
        duplicated.clear();
        write(duplicated);
      } 
    }
    
    public void writeInt32NoTag(int value) throws IOException {
      if (value >= 0) {
        writeUInt32NoTag(value);
      } else {
        writeUInt64NoTag(value);
      } 
    }
    
    public void writeUInt32NoTag(int value) throws IOException {
      // Byte code:
      //   0: aload_0
      //   1: getfield position : J
      //   4: aload_0
      //   5: getfield oneVarintLimit : J
      //   8: lcmp
      //   9: ifgt -> 67
      //   12: iload_1
      //   13: bipush #-128
      //   15: iand
      //   16: ifne -> 36
      //   19: aload_0
      //   20: dup
      //   21: getfield position : J
      //   24: dup2_x1
      //   25: lconst_1
      //   26: ladd
      //   27: putfield position : J
      //   30: iload_1
      //   31: i2b
      //   32: invokestatic putByte : (JB)V
      //   35: return
      //   36: aload_0
      //   37: dup
      //   38: getfield position : J
      //   41: dup2_x1
      //   42: lconst_1
      //   43: ladd
      //   44: putfield position : J
      //   47: iload_1
      //   48: bipush #127
      //   50: iand
      //   51: sipush #128
      //   54: ior
      //   55: i2b
      //   56: invokestatic putByte : (JB)V
      //   59: iload_1
      //   60: bipush #7
      //   62: iushr
      //   63: istore_1
      //   64: goto -> 12
      //   67: aload_0
      //   68: getfield position : J
      //   71: aload_0
      //   72: getfield limit : J
      //   75: lcmp
      //   76: ifge -> 134
      //   79: iload_1
      //   80: bipush #-128
      //   82: iand
      //   83: ifne -> 103
      //   86: aload_0
      //   87: dup
      //   88: getfield position : J
      //   91: dup2_x1
      //   92: lconst_1
      //   93: ladd
      //   94: putfield position : J
      //   97: iload_1
      //   98: i2b
      //   99: invokestatic putByte : (JB)V
      //   102: return
      //   103: aload_0
      //   104: dup
      //   105: getfield position : J
      //   108: dup2_x1
      //   109: lconst_1
      //   110: ladd
      //   111: putfield position : J
      //   114: iload_1
      //   115: bipush #127
      //   117: iand
      //   118: sipush #128
      //   121: ior
      //   122: i2b
      //   123: invokestatic putByte : (JB)V
      //   126: iload_1
      //   127: bipush #7
      //   129: iushr
      //   130: istore_1
      //   131: goto -> 67
      //   134: new com/google/protobuf/CodedOutputStream$OutOfSpaceException
      //   137: dup
      //   138: ldc 'Pos: %d, limit: %d, len: %d'
      //   140: iconst_3
      //   141: anewarray java/lang/Object
      //   144: dup
      //   145: iconst_0
      //   146: aload_0
      //   147: getfield position : J
      //   150: invokestatic valueOf : (J)Ljava/lang/Long;
      //   153: aastore
      //   154: dup
      //   155: iconst_1
      //   156: aload_0
      //   157: getfield limit : J
      //   160: invokestatic valueOf : (J)Ljava/lang/Long;
      //   163: aastore
      //   164: dup
      //   165: iconst_2
      //   166: iconst_1
      //   167: invokestatic valueOf : (I)Ljava/lang/Integer;
      //   170: aastore
      //   171: invokestatic format : (Ljava/lang/String;[Ljava/lang/Object;)Ljava/lang/String;
      //   174: invokespecial <init> : (Ljava/lang/String;)V
      //   177: athrow
      // Line number table:
      //   Java source line number -> byte code offset
      //   #2034	-> 0
      //   #2037	-> 12
      //   #2038	-> 19
      //   #2039	-> 35
      //   #2041	-> 36
      //   #2042	-> 59
      //   #2046	-> 67
      //   #2047	-> 79
      //   #2048	-> 86
      //   #2049	-> 102
      //   #2051	-> 103
      //   #2052	-> 126
      //   #2055	-> 134
      //   #2056	-> 150
      // Local variable table:
      //   start	length	slot	name	descriptor
      //   0	178	0	this	Lcom/google/protobuf/CodedOutputStream$UnsafeDirectNioEncoder;
      //   0	178	1	value	I
    }
    
    public void writeFixed32NoTag(int value) throws IOException {
      this.buffer.putInt(bufferPos(this.position), value);
      this.position += 4L;
    }
    
    public void writeUInt64NoTag(long value) throws IOException {
      // Byte code:
      //   0: aload_0
      //   1: getfield position : J
      //   4: aload_0
      //   5: getfield oneVarintLimit : J
      //   8: lcmp
      //   9: ifgt -> 72
      //   12: lload_1
      //   13: ldc2_w -128
      //   16: land
      //   17: lconst_0
      //   18: lcmp
      //   19: ifne -> 40
      //   22: aload_0
      //   23: dup
      //   24: getfield position : J
      //   27: dup2_x1
      //   28: lconst_1
      //   29: ladd
      //   30: putfield position : J
      //   33: lload_1
      //   34: l2i
      //   35: i2b
      //   36: invokestatic putByte : (JB)V
      //   39: return
      //   40: aload_0
      //   41: dup
      //   42: getfield position : J
      //   45: dup2_x1
      //   46: lconst_1
      //   47: ladd
      //   48: putfield position : J
      //   51: lload_1
      //   52: l2i
      //   53: bipush #127
      //   55: iand
      //   56: sipush #128
      //   59: ior
      //   60: i2b
      //   61: invokestatic putByte : (JB)V
      //   64: lload_1
      //   65: bipush #7
      //   67: lushr
      //   68: lstore_1
      //   69: goto -> 12
      //   72: aload_0
      //   73: getfield position : J
      //   76: aload_0
      //   77: getfield limit : J
      //   80: lcmp
      //   81: ifge -> 144
      //   84: lload_1
      //   85: ldc2_w -128
      //   88: land
      //   89: lconst_0
      //   90: lcmp
      //   91: ifne -> 112
      //   94: aload_0
      //   95: dup
      //   96: getfield position : J
      //   99: dup2_x1
      //   100: lconst_1
      //   101: ladd
      //   102: putfield position : J
      //   105: lload_1
      //   106: l2i
      //   107: i2b
      //   108: invokestatic putByte : (JB)V
      //   111: return
      //   112: aload_0
      //   113: dup
      //   114: getfield position : J
      //   117: dup2_x1
      //   118: lconst_1
      //   119: ladd
      //   120: putfield position : J
      //   123: lload_1
      //   124: l2i
      //   125: bipush #127
      //   127: iand
      //   128: sipush #128
      //   131: ior
      //   132: i2b
      //   133: invokestatic putByte : (JB)V
      //   136: lload_1
      //   137: bipush #7
      //   139: lushr
      //   140: lstore_1
      //   141: goto -> 72
      //   144: new com/google/protobuf/CodedOutputStream$OutOfSpaceException
      //   147: dup
      //   148: ldc 'Pos: %d, limit: %d, len: %d'
      //   150: iconst_3
      //   151: anewarray java/lang/Object
      //   154: dup
      //   155: iconst_0
      //   156: aload_0
      //   157: getfield position : J
      //   160: invokestatic valueOf : (J)Ljava/lang/Long;
      //   163: aastore
      //   164: dup
      //   165: iconst_1
      //   166: aload_0
      //   167: getfield limit : J
      //   170: invokestatic valueOf : (J)Ljava/lang/Long;
      //   173: aastore
      //   174: dup
      //   175: iconst_2
      //   176: iconst_1
      //   177: invokestatic valueOf : (I)Ljava/lang/Integer;
      //   180: aastore
      //   181: invokestatic format : (Ljava/lang/String;[Ljava/lang/Object;)Ljava/lang/String;
      //   184: invokespecial <init> : (Ljava/lang/String;)V
      //   187: athrow
      // Line number table:
      //   Java source line number -> byte code offset
      //   #2068	-> 0
      //   #2071	-> 12
      //   #2072	-> 22
      //   #2073	-> 39
      //   #2075	-> 40
      //   #2076	-> 64
      //   #2080	-> 72
      //   #2081	-> 84
      //   #2082	-> 94
      //   #2083	-> 111
      //   #2085	-> 112
      //   #2086	-> 136
      //   #2089	-> 144
      //   #2090	-> 160
      // Local variable table:
      //   start	length	slot	name	descriptor
      //   0	188	0	this	Lcom/google/protobuf/CodedOutputStream$UnsafeDirectNioEncoder;
      //   0	188	1	value	J
    }
    
    public void writeFixed64NoTag(long value) throws IOException {
      this.buffer.putLong(bufferPos(this.position), value);
      this.position += 8L;
    }
    
    public void write(byte[] value, int offset, int length) throws IOException {
      if (value == null || offset < 0 || length < 0 || value.length - length < offset || this.limit - length < this.position) {
        if (value == null)
          throw new NullPointerException("value"); 
        throw new CodedOutputStream.OutOfSpaceException(
            String.format("Pos: %d, limit: %d, len: %d", new Object[] { Long.valueOf(this.position), Long.valueOf(this.limit), Integer.valueOf(length) }));
      } 
      UnsafeUtil.copyMemory(value, offset, this.position, length);
      this.position += length;
    }
    
    public void writeLazy(byte[] value, int offset, int length) throws IOException {
      write(value, offset, length);
    }
    
    public void write(ByteBuffer value) throws IOException {
      try {
        int length = value.remaining();
        repositionBuffer(this.position);
        this.buffer.put(value);
        this.position += length;
      } catch (BufferOverflowException e) {
        throw new CodedOutputStream.OutOfSpaceException(e);
      } 
    }
    
    public void writeLazy(ByteBuffer value) throws IOException {
      write(value);
    }
    
    public void writeStringNoTag(String value) throws IOException {
      long prevPos = this.position;
      try {
        int maxEncodedSize = value.length() * 3;
        int maxLengthVarIntSize = computeUInt32SizeNoTag(maxEncodedSize);
        int minLengthVarIntSize = computeUInt32SizeNoTag(value.length());
        if (minLengthVarIntSize == maxLengthVarIntSize) {
          int stringStart = bufferPos(this.position) + minLengthVarIntSize;
          this.buffer.position(stringStart);
          Utf8.encodeUtf8(value, this.buffer);
          int length = this.buffer.position() - stringStart;
          writeUInt32NoTag(length);
          this.position += length;
        } else {
          int length = Utf8.encodedLength(value);
          writeUInt32NoTag(length);
          repositionBuffer(this.position);
          Utf8.encodeUtf8(value, this.buffer);
          this.position += length;
        } 
      } catch (UnpairedSurrogateException e) {
        this.position = prevPos;
        repositionBuffer(this.position);
        inefficientWriteStringNoTag(value, e);
      } catch (IllegalArgumentException e) {
        throw new CodedOutputStream.OutOfSpaceException(e);
      } catch (IndexOutOfBoundsException e) {
        throw new CodedOutputStream.OutOfSpaceException(e);
      } 
    }
    
    public void flush() {
      this.originalBuffer.position(bufferPos(this.position));
    }
    
    public int spaceLeft() {
      return (int)(this.limit - this.position);
    }
    
    public int getTotalBytesWritten() {
      return (int)(this.position - this.initialPosition);
    }
    
    private void repositionBuffer(long pos) {
      this.buffer.position(bufferPos(pos));
    }
    
    private int bufferPos(long pos) {
      return (int)(pos - this.address);
    }
  }
  
  private static abstract class AbstractBufferedEncoder extends CodedOutputStream {
    final byte[] buffer;
    
    final int limit;
    
    int position;
    
    int totalBytesWritten;
    
    AbstractBufferedEncoder(int bufferSize) {
      if (bufferSize < 0)
        throw new IllegalArgumentException("bufferSize must be >= 0"); 
      this.buffer = new byte[Math.max(bufferSize, 20)];
      this.limit = this.buffer.length;
    }
    
    public final int spaceLeft() {
      throw new UnsupportedOperationException("spaceLeft() can only be called on CodedOutputStreams that are writing to a flat array or ByteBuffer.");
    }
    
    public final int getTotalBytesWritten() {
      return this.totalBytesWritten;
    }
    
    final void buffer(byte value) {
      this.buffer[this.position++] = value;
      this.totalBytesWritten++;
    }
    
    final void bufferTag(int fieldNumber, int wireType) {
      bufferUInt32NoTag(WireFormat.makeTag(fieldNumber, wireType));
    }
    
    final void bufferInt32NoTag(int value) {
      if (value >= 0) {
        bufferUInt32NoTag(value);
      } else {
        bufferUInt64NoTag(value);
      } 
    }
    
    final void bufferUInt32NoTag(int value) {
      if (CodedOutputStream.HAS_UNSAFE_ARRAY_OPERATIONS) {
        long originalPos = this.position;
        while (true) {
          if ((value & 0xFFFFFF80) == 0) {
            UnsafeUtil.putByte(this.buffer, this.position++, (byte)value);
            break;
          } 
          UnsafeUtil.putByte(this.buffer, this.position++, (byte)(value & 0x7F | 0x80));
          value >>>= 7;
        } 
        int delta = (int)(this.position - originalPos);
        this.totalBytesWritten += delta;
      } else {
        while (true) {
          if ((value & 0xFFFFFF80) == 0) {
            this.buffer[this.position++] = (byte)value;
            this.totalBytesWritten++;
            return;
          } 
          this.buffer[this.position++] = (byte)(value & 0x7F | 0x80);
          this.totalBytesWritten++;
          value >>>= 7;
        } 
      } 
    }
    
    final void bufferUInt64NoTag(long value) {
      if (CodedOutputStream.HAS_UNSAFE_ARRAY_OPERATIONS) {
        long originalPos = this.position;
        while (true) {
          if ((value & 0xFFFFFFFFFFFFFF80L) == 0L) {
            UnsafeUtil.putByte(this.buffer, this.position++, (byte)(int)value);
            break;
          } 
          UnsafeUtil.putByte(this.buffer, this.position++, (byte)((int)value & 0x7F | 0x80));
          value >>>= 7L;
        } 
        int delta = (int)(this.position - originalPos);
        this.totalBytesWritten += delta;
      } else {
        while (true) {
          if ((value & 0xFFFFFFFFFFFFFF80L) == 0L) {
            this.buffer[this.position++] = (byte)(int)value;
            this.totalBytesWritten++;
            return;
          } 
          this.buffer[this.position++] = (byte)((int)value & 0x7F | 0x80);
          this.totalBytesWritten++;
          value >>>= 7L;
        } 
      } 
    }
    
    final void bufferFixed32NoTag(int value) {
      this.buffer[this.position++] = (byte)(value & 0xFF);
      this.buffer[this.position++] = (byte)(value >> 8 & 0xFF);
      this.buffer[this.position++] = (byte)(value >> 16 & 0xFF);
      this.buffer[this.position++] = (byte)(value >> 24 & 0xFF);
      this.totalBytesWritten += 4;
    }
    
    final void bufferFixed64NoTag(long value) {
      this.buffer[this.position++] = (byte)(int)(value & 0xFFL);
      this.buffer[this.position++] = (byte)(int)(value >> 8L & 0xFFL);
      this.buffer[this.position++] = (byte)(int)(value >> 16L & 0xFFL);
      this.buffer[this.position++] = (byte)(int)(value >> 24L & 0xFFL);
      this.buffer[this.position++] = (byte)((int)(value >> 32L) & 0xFF);
      this.buffer[this.position++] = (byte)((int)(value >> 40L) & 0xFF);
      this.buffer[this.position++] = (byte)((int)(value >> 48L) & 0xFF);
      this.buffer[this.position++] = (byte)((int)(value >> 56L) & 0xFF);
      this.totalBytesWritten += 8;
    }
  }
  
  private static final class ByteOutputEncoder extends AbstractBufferedEncoder {
    private final ByteOutput out;
    
    ByteOutputEncoder(ByteOutput out, int bufferSize) {
      super(bufferSize);
      if (out == null)
        throw new NullPointerException("out"); 
      this.out = out;
    }
    
    public void writeTag(int fieldNumber, int wireType) throws IOException {
      writeUInt32NoTag(WireFormat.makeTag(fieldNumber, wireType));
    }
    
    public void writeInt32(int fieldNumber, int value) throws IOException {
      flushIfNotAvailable(20);
      bufferTag(fieldNumber, 0);
      bufferInt32NoTag(value);
    }
    
    public void writeUInt32(int fieldNumber, int value) throws IOException {
      flushIfNotAvailable(20);
      bufferTag(fieldNumber, 0);
      bufferUInt32NoTag(value);
    }
    
    public void writeFixed32(int fieldNumber, int value) throws IOException {
      flushIfNotAvailable(14);
      bufferTag(fieldNumber, 5);
      bufferFixed32NoTag(value);
    }
    
    public void writeUInt64(int fieldNumber, long value) throws IOException {
      flushIfNotAvailable(20);
      bufferTag(fieldNumber, 0);
      bufferUInt64NoTag(value);
    }
    
    public void writeFixed64(int fieldNumber, long value) throws IOException {
      flushIfNotAvailable(18);
      bufferTag(fieldNumber, 1);
      bufferFixed64NoTag(value);
    }
    
    public void writeBool(int fieldNumber, boolean value) throws IOException {
      flushIfNotAvailable(11);
      bufferTag(fieldNumber, 0);
      buffer((byte)(value ? 1 : 0));
    }
    
    public void writeString(int fieldNumber, String value) throws IOException {
      writeTag(fieldNumber, 2);
      writeStringNoTag(value);
    }
    
    public void writeBytes(int fieldNumber, ByteString value) throws IOException {
      writeTag(fieldNumber, 2);
      writeBytesNoTag(value);
    }
    
    public void writeByteArray(int fieldNumber, byte[] value) throws IOException {
      writeByteArray(fieldNumber, value, 0, value.length);
    }
    
    public void writeByteArray(int fieldNumber, byte[] value, int offset, int length) throws IOException {
      writeTag(fieldNumber, 2);
      writeByteArrayNoTag(value, offset, length);
    }
    
    public void writeByteBuffer(int fieldNumber, ByteBuffer value) throws IOException {
      writeTag(fieldNumber, 2);
      writeUInt32NoTag(value.capacity());
      writeRawBytes(value);
    }
    
    public void writeBytesNoTag(ByteString value) throws IOException {
      writeUInt32NoTag(value.size());
      value.writeTo(this);
    }
    
    public void writeByteArrayNoTag(byte[] value, int offset, int length) throws IOException {
      writeUInt32NoTag(length);
      write(value, offset, length);
    }
    
    public void writeRawBytes(ByteBuffer value) throws IOException {
      if (value.hasArray()) {
        write(value.array(), value.arrayOffset(), value.capacity());
      } else {
        ByteBuffer duplicated = value.duplicate();
        duplicated.clear();
        write(duplicated);
      } 
    }
    
    public void writeMessage(int fieldNumber, MessageLite value) throws IOException {
      writeTag(fieldNumber, 2);
      writeMessageNoTag(value);
    }
    
    void writeMessage(int fieldNumber, MessageLite value, Schema schema) throws IOException {
      writeTag(fieldNumber, 2);
      writeMessageNoTag(value, schema);
    }
    
    public void writeMessageSetExtension(int fieldNumber, MessageLite value) throws IOException {
      writeTag(1, 3);
      writeUInt32(2, fieldNumber);
      writeMessage(3, value);
      writeTag(1, 4);
    }
    
    public void writeRawMessageSetExtension(int fieldNumber, ByteString value) throws IOException {
      writeTag(1, 3);
      writeUInt32(2, fieldNumber);
      writeBytes(3, value);
      writeTag(1, 4);
    }
    
    public void writeMessageNoTag(MessageLite value) throws IOException {
      writeUInt32NoTag(value.getSerializedSize());
      value.writeTo(this);
    }
    
    void writeMessageNoTag(MessageLite value, Schema<MessageLite> schema) throws IOException {
      writeUInt32NoTag(((AbstractMessageLite)value).getSerializedSize(schema));
      schema.writeTo(value, this.wrapper);
    }
    
    public void write(byte value) throws IOException {
      if (this.position == this.limit)
        doFlush(); 
      buffer(value);
    }
    
    public void writeInt32NoTag(int value) throws IOException {
      if (value >= 0) {
        writeUInt32NoTag(value);
      } else {
        writeUInt64NoTag(value);
      } 
    }
    
    public void writeUInt32NoTag(int value) throws IOException {
      flushIfNotAvailable(5);
      bufferUInt32NoTag(value);
    }
    
    public void writeFixed32NoTag(int value) throws IOException {
      flushIfNotAvailable(4);
      bufferFixed32NoTag(value);
    }
    
    public void writeUInt64NoTag(long value) throws IOException {
      flushIfNotAvailable(10);
      bufferUInt64NoTag(value);
    }
    
    public void writeFixed64NoTag(long value) throws IOException {
      flushIfNotAvailable(8);
      bufferFixed64NoTag(value);
    }
    
    public void writeStringNoTag(String value) throws IOException {
      int maxLength = value.length() * 3;
      int maxLengthVarIntSize = computeUInt32SizeNoTag(maxLength);
      if (maxLengthVarIntSize + maxLength > this.limit) {
        byte[] encodedBytes = new byte[maxLength];
        int actualLength = Utf8.encode(value, encodedBytes, 0, maxLength);
        writeUInt32NoTag(actualLength);
        writeLazy(encodedBytes, 0, actualLength);
        return;
      } 
      if (maxLengthVarIntSize + maxLength > this.limit - this.position)
        doFlush(); 
      int oldPosition = this.position;
      try {
        int minLengthVarIntSize = computeUInt32SizeNoTag(value.length());
        if (minLengthVarIntSize == maxLengthVarIntSize) {
          this.position = oldPosition + minLengthVarIntSize;
          int newPosition = Utf8.encode(value, this.buffer, this.position, this.limit - this.position);
          this.position = oldPosition;
          int length = newPosition - oldPosition - minLengthVarIntSize;
          bufferUInt32NoTag(length);
          this.position = newPosition;
          this.totalBytesWritten += length;
        } else {
          int length = Utf8.encodedLength(value);
          bufferUInt32NoTag(length);
          this.position = Utf8.encode(value, this.buffer, this.position, length);
          this.totalBytesWritten += length;
        } 
      } catch (UnpairedSurrogateException e) {
        this.totalBytesWritten -= this.position - oldPosition;
        this.position = oldPosition;
        inefficientWriteStringNoTag(value, e);
      } catch (IndexOutOfBoundsException e) {
        throw new CodedOutputStream.OutOfSpaceException(e);
      } 
    }
    
    public void flush() throws IOException {
      if (this.position > 0)
        doFlush(); 
    }
    
    public void write(byte[] value, int offset, int length) throws IOException {
      flush();
      this.out.write(value, offset, length);
      this.totalBytesWritten += length;
    }
    
    public void writeLazy(byte[] value, int offset, int length) throws IOException {
      flush();
      this.out.writeLazy(value, offset, length);
      this.totalBytesWritten += length;
    }
    
    public void write(ByteBuffer value) throws IOException {
      flush();
      int length = value.remaining();
      this.out.write(value);
      this.totalBytesWritten += length;
    }
    
    public void writeLazy(ByteBuffer value) throws IOException {
      flush();
      int length = value.remaining();
      this.out.writeLazy(value);
      this.totalBytesWritten += length;
    }
    
    private void flushIfNotAvailable(int requiredSize) throws IOException {
      if (this.limit - this.position < requiredSize)
        doFlush(); 
    }
    
    private void doFlush() throws IOException {
      this.out.write(this.buffer, 0, this.position);
      this.position = 0;
    }
  }
  
  private static final class OutputStreamEncoder extends AbstractBufferedEncoder {
    private final OutputStream out;
    
    OutputStreamEncoder(OutputStream out, int bufferSize) {
      super(bufferSize);
      if (out == null)
        throw new NullPointerException("out"); 
      this.out = out;
    }
    
    public void writeTag(int fieldNumber, int wireType) throws IOException {
      writeUInt32NoTag(WireFormat.makeTag(fieldNumber, wireType));
    }
    
    public void writeInt32(int fieldNumber, int value) throws IOException {
      flushIfNotAvailable(20);
      bufferTag(fieldNumber, 0);
      bufferInt32NoTag(value);
    }
    
    public void writeUInt32(int fieldNumber, int value) throws IOException {
      flushIfNotAvailable(20);
      bufferTag(fieldNumber, 0);
      bufferUInt32NoTag(value);
    }
    
    public void writeFixed32(int fieldNumber, int value) throws IOException {
      flushIfNotAvailable(14);
      bufferTag(fieldNumber, 5);
      bufferFixed32NoTag(value);
    }
    
    public void writeUInt64(int fieldNumber, long value) throws IOException {
      flushIfNotAvailable(20);
      bufferTag(fieldNumber, 0);
      bufferUInt64NoTag(value);
    }
    
    public void writeFixed64(int fieldNumber, long value) throws IOException {
      flushIfNotAvailable(18);
      bufferTag(fieldNumber, 1);
      bufferFixed64NoTag(value);
    }
    
    public void writeBool(int fieldNumber, boolean value) throws IOException {
      flushIfNotAvailable(11);
      bufferTag(fieldNumber, 0);
      buffer((byte)(value ? 1 : 0));
    }
    
    public void writeString(int fieldNumber, String value) throws IOException {
      writeTag(fieldNumber, 2);
      writeStringNoTag(value);
    }
    
    public void writeBytes(int fieldNumber, ByteString value) throws IOException {
      writeTag(fieldNumber, 2);
      writeBytesNoTag(value);
    }
    
    public void writeByteArray(int fieldNumber, byte[] value) throws IOException {
      writeByteArray(fieldNumber, value, 0, value.length);
    }
    
    public void writeByteArray(int fieldNumber, byte[] value, int offset, int length) throws IOException {
      writeTag(fieldNumber, 2);
      writeByteArrayNoTag(value, offset, length);
    }
    
    public void writeByteBuffer(int fieldNumber, ByteBuffer value) throws IOException {
      writeTag(fieldNumber, 2);
      writeUInt32NoTag(value.capacity());
      writeRawBytes(value);
    }
    
    public void writeBytesNoTag(ByteString value) throws IOException {
      writeUInt32NoTag(value.size());
      value.writeTo(this);
    }
    
    public void writeByteArrayNoTag(byte[] value, int offset, int length) throws IOException {
      writeUInt32NoTag(length);
      write(value, offset, length);
    }
    
    public void writeRawBytes(ByteBuffer value) throws IOException {
      if (value.hasArray()) {
        write(value.array(), value.arrayOffset(), value.capacity());
      } else {
        ByteBuffer duplicated = value.duplicate();
        duplicated.clear();
        write(duplicated);
      } 
    }
    
    public void writeMessage(int fieldNumber, MessageLite value) throws IOException {
      writeTag(fieldNumber, 2);
      writeMessageNoTag(value);
    }
    
    void writeMessage(int fieldNumber, MessageLite value, Schema schema) throws IOException {
      writeTag(fieldNumber, 2);
      writeMessageNoTag(value, schema);
    }
    
    public void writeMessageSetExtension(int fieldNumber, MessageLite value) throws IOException {
      writeTag(1, 3);
      writeUInt32(2, fieldNumber);
      writeMessage(3, value);
      writeTag(1, 4);
    }
    
    public void writeRawMessageSetExtension(int fieldNumber, ByteString value) throws IOException {
      writeTag(1, 3);
      writeUInt32(2, fieldNumber);
      writeBytes(3, value);
      writeTag(1, 4);
    }
    
    public void writeMessageNoTag(MessageLite value) throws IOException {
      writeUInt32NoTag(value.getSerializedSize());
      value.writeTo(this);
    }
    
    void writeMessageNoTag(MessageLite value, Schema<MessageLite> schema) throws IOException {
      writeUInt32NoTag(((AbstractMessageLite)value).getSerializedSize(schema));
      schema.writeTo(value, this.wrapper);
    }
    
    public void write(byte value) throws IOException {
      if (this.position == this.limit)
        doFlush(); 
      buffer(value);
    }
    
    public void writeInt32NoTag(int value) throws IOException {
      if (value >= 0) {
        writeUInt32NoTag(value);
      } else {
        writeUInt64NoTag(value);
      } 
    }
    
    public void writeUInt32NoTag(int value) throws IOException {
      flushIfNotAvailable(5);
      bufferUInt32NoTag(value);
    }
    
    public void writeFixed32NoTag(int value) throws IOException {
      flushIfNotAvailable(4);
      bufferFixed32NoTag(value);
    }
    
    public void writeUInt64NoTag(long value) throws IOException {
      flushIfNotAvailable(10);
      bufferUInt64NoTag(value);
    }
    
    public void writeFixed64NoTag(long value) throws IOException {
      flushIfNotAvailable(8);
      bufferFixed64NoTag(value);
    }
    
    public void writeStringNoTag(String value) throws IOException {
      try {
        int maxLength = value.length() * 3;
        int maxLengthVarIntSize = computeUInt32SizeNoTag(maxLength);
        if (maxLengthVarIntSize + maxLength > this.limit) {
          byte[] encodedBytes = new byte[maxLength];
          int actualLength = Utf8.encode(value, encodedBytes, 0, maxLength);
          writeUInt32NoTag(actualLength);
          writeLazy(encodedBytes, 0, actualLength);
          return;
        } 
        if (maxLengthVarIntSize + maxLength > this.limit - this.position)
          doFlush(); 
        int minLengthVarIntSize = computeUInt32SizeNoTag(value.length());
        int oldPosition = this.position;
        try {
          int length;
          if (minLengthVarIntSize == maxLengthVarIntSize) {
            this.position = oldPosition + minLengthVarIntSize;
            int newPosition = Utf8.encode(value, this.buffer, this.position, this.limit - this.position);
            this.position = oldPosition;
            length = newPosition - oldPosition - minLengthVarIntSize;
            bufferUInt32NoTag(length);
            this.position = newPosition;
          } else {
            length = Utf8.encodedLength(value);
            bufferUInt32NoTag(length);
            this.position = Utf8.encode(value, this.buffer, this.position, length);
          } 
          this.totalBytesWritten += length;
        } catch (UnpairedSurrogateException e) {
          this.totalBytesWritten -= this.position - oldPosition;
          this.position = oldPosition;
          throw e;
        } catch (ArrayIndexOutOfBoundsException e) {
          throw new CodedOutputStream.OutOfSpaceException(e);
        } 
      } catch (UnpairedSurrogateException e) {
        inefficientWriteStringNoTag(value, e);
      } 
    }
    
    public void flush() throws IOException {
      if (this.position > 0)
        doFlush(); 
    }
    
    public void write(byte[] value, int offset, int length) throws IOException {
      if (this.limit - this.position >= length) {
        System.arraycopy(value, offset, this.buffer, this.position, length);
        this.position += length;
        this.totalBytesWritten += length;
      } else {
        int bytesWritten = this.limit - this.position;
        System.arraycopy(value, offset, this.buffer, this.position, bytesWritten);
        offset += bytesWritten;
        length -= bytesWritten;
        this.position = this.limit;
        this.totalBytesWritten += bytesWritten;
        doFlush();
        if (length <= this.limit) {
          System.arraycopy(value, offset, this.buffer, 0, length);
          this.position = length;
        } else {
          this.out.write(value, offset, length);
        } 
        this.totalBytesWritten += length;
      } 
    }
    
    public void writeLazy(byte[] value, int offset, int length) throws IOException {
      write(value, offset, length);
    }
    
    public void write(ByteBuffer value) throws IOException {
      int length = value.remaining();
      if (this.limit - this.position >= length) {
        value.get(this.buffer, this.position, length);
        this.position += length;
        this.totalBytesWritten += length;
      } else {
        int bytesWritten = this.limit - this.position;
        value.get(this.buffer, this.position, bytesWritten);
        length -= bytesWritten;
        this.position = this.limit;
        this.totalBytesWritten += bytesWritten;
        doFlush();
        while (length > this.limit) {
          value.get(this.buffer, 0, this.limit);
          this.out.write(this.buffer, 0, this.limit);
          length -= this.limit;
          this.totalBytesWritten += this.limit;
        } 
        value.get(this.buffer, 0, length);
        this.position = length;
        this.totalBytesWritten += length;
      } 
    }
    
    public void writeLazy(ByteBuffer value) throws IOException {
      write(value);
    }
    
    private void flushIfNotAvailable(int requiredSize) throws IOException {
      if (this.limit - this.position < requiredSize)
        doFlush(); 
    }
    
    private void doFlush() throws IOException {
      this.out.write(this.buffer, 0, this.position);
      this.position = 0;
    }
  }
}
