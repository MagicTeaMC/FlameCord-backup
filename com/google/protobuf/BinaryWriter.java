package com.google.protobuf;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayDeque;
import java.util.List;
import java.util.Map;
import java.util.Queue;

@CheckReturnValue
abstract class BinaryWriter extends ByteOutput implements Writer {
  public static final int DEFAULT_CHUNK_SIZE = 4096;
  
  private final BufferAllocator alloc;
  
  private final int chunkSize;
  
  final ArrayDeque<AllocatedBuffer> buffers = new ArrayDeque<>(4);
  
  int totalDoneBytes;
  
  private static final int MAP_KEY_NUMBER = 1;
  
  private static final int MAP_VALUE_NUMBER = 2;
  
  public static BinaryWriter newHeapInstance(BufferAllocator alloc) {
    return newHeapInstance(alloc, 4096);
  }
  
  public static BinaryWriter newHeapInstance(BufferAllocator alloc, int chunkSize) {
    return isUnsafeHeapSupported() ? 
      newUnsafeHeapInstance(alloc, chunkSize) : 
      newSafeHeapInstance(alloc, chunkSize);
  }
  
  public static BinaryWriter newDirectInstance(BufferAllocator alloc) {
    return newDirectInstance(alloc, 4096);
  }
  
  public static BinaryWriter newDirectInstance(BufferAllocator alloc, int chunkSize) {
    return isUnsafeDirectSupported() ? 
      newUnsafeDirectInstance(alloc, chunkSize) : 
      newSafeDirectInstance(alloc, chunkSize);
  }
  
  static boolean isUnsafeHeapSupported() {
    return UnsafeHeapWriter.isSupported();
  }
  
  static boolean isUnsafeDirectSupported() {
    return UnsafeDirectWriter.isSupported();
  }
  
  static BinaryWriter newSafeHeapInstance(BufferAllocator alloc, int chunkSize) {
    return new SafeHeapWriter(alloc, chunkSize);
  }
  
  static BinaryWriter newUnsafeHeapInstance(BufferAllocator alloc, int chunkSize) {
    if (!isUnsafeHeapSupported())
      throw new UnsupportedOperationException("Unsafe operations not supported"); 
    return new UnsafeHeapWriter(alloc, chunkSize);
  }
  
  static BinaryWriter newSafeDirectInstance(BufferAllocator alloc, int chunkSize) {
    return new SafeDirectWriter(alloc, chunkSize);
  }
  
  static BinaryWriter newUnsafeDirectInstance(BufferAllocator alloc, int chunkSize) {
    if (!isUnsafeDirectSupported())
      throw new UnsupportedOperationException("Unsafe operations not supported"); 
    return new UnsafeDirectWriter(alloc, chunkSize);
  }
  
  private BinaryWriter(BufferAllocator alloc, int chunkSize) {
    if (chunkSize <= 0)
      throw new IllegalArgumentException("chunkSize must be > 0"); 
    this.alloc = Internal.<BufferAllocator>checkNotNull(alloc, "alloc");
    this.chunkSize = chunkSize;
  }
  
  public final Writer.FieldOrder fieldOrder() {
    return Writer.FieldOrder.DESCENDING;
  }
  
  @CanIgnoreReturnValue
  public final Queue<AllocatedBuffer> complete() {
    finishCurrentBuffer();
    return this.buffers;
  }
  
  public final void writeSFixed32(int fieldNumber, int value) throws IOException {
    writeFixed32(fieldNumber, value);
  }
  
  public final void writeInt64(int fieldNumber, long value) throws IOException {
    writeUInt64(fieldNumber, value);
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
  
  public final void writeInt32List(int fieldNumber, List<Integer> list, boolean packed) throws IOException {
    if (list instanceof IntArrayList) {
      writeInt32List_Internal(fieldNumber, (IntArrayList)list, packed);
    } else {
      writeInt32List_Internal(fieldNumber, list, packed);
    } 
  }
  
  private final void writeInt32List_Internal(int fieldNumber, List<Integer> list, boolean packed) throws IOException {
    if (packed) {
      requireSpace(10 + list.size() * 10);
      int prevBytes = getTotalBytesWritten();
      for (int i = list.size() - 1; i >= 0; i--)
        writeInt32(((Integer)list.get(i)).intValue()); 
      int length = getTotalBytesWritten() - prevBytes;
      writeVarint32(length);
      writeTag(fieldNumber, 2);
    } else {
      for (int i = list.size() - 1; i >= 0; i--)
        writeInt32(fieldNumber, ((Integer)list.get(i)).intValue()); 
    } 
  }
  
  private final void writeInt32List_Internal(int fieldNumber, IntArrayList list, boolean packed) throws IOException {
    if (packed) {
      requireSpace(10 + list.size() * 10);
      int prevBytes = getTotalBytesWritten();
      for (int i = list.size() - 1; i >= 0; i--)
        writeInt32(list.getInt(i)); 
      int length = getTotalBytesWritten() - prevBytes;
      writeVarint32(length);
      writeTag(fieldNumber, 2);
    } else {
      for (int i = list.size() - 1; i >= 0; i--)
        writeInt32(fieldNumber, list.getInt(i)); 
    } 
  }
  
  public final void writeFixed32List(int fieldNumber, List<Integer> list, boolean packed) throws IOException {
    if (list instanceof IntArrayList) {
      writeFixed32List_Internal(fieldNumber, (IntArrayList)list, packed);
    } else {
      writeFixed32List_Internal(fieldNumber, list, packed);
    } 
  }
  
  private final void writeFixed32List_Internal(int fieldNumber, List<Integer> list, boolean packed) throws IOException {
    if (packed) {
      requireSpace(10 + list.size() * 4);
      int prevBytes = getTotalBytesWritten();
      for (int i = list.size() - 1; i >= 0; i--)
        writeFixed32(((Integer)list.get(i)).intValue()); 
      int length = getTotalBytesWritten() - prevBytes;
      writeVarint32(length);
      writeTag(fieldNumber, 2);
    } else {
      for (int i = list.size() - 1; i >= 0; i--)
        writeFixed32(fieldNumber, ((Integer)list.get(i)).intValue()); 
    } 
  }
  
  private final void writeFixed32List_Internal(int fieldNumber, IntArrayList list, boolean packed) throws IOException {
    if (packed) {
      requireSpace(10 + list.size() * 4);
      int prevBytes = getTotalBytesWritten();
      for (int i = list.size() - 1; i >= 0; i--)
        writeFixed32(list.getInt(i)); 
      int length = getTotalBytesWritten() - prevBytes;
      writeVarint32(length);
      writeTag(fieldNumber, 2);
    } else {
      for (int i = list.size() - 1; i >= 0; i--)
        writeFixed32(fieldNumber, list.getInt(i)); 
    } 
  }
  
  public final void writeInt64List(int fieldNumber, List<Long> list, boolean packed) throws IOException {
    writeUInt64List(fieldNumber, list, packed);
  }
  
  public final void writeUInt64List(int fieldNumber, List<Long> list, boolean packed) throws IOException {
    if (list instanceof LongArrayList) {
      writeUInt64List_Internal(fieldNumber, (LongArrayList)list, packed);
    } else {
      writeUInt64List_Internal(fieldNumber, list, packed);
    } 
  }
  
  private final void writeUInt64List_Internal(int fieldNumber, List<Long> list, boolean packed) throws IOException {
    if (packed) {
      requireSpace(10 + list.size() * 10);
      int prevBytes = getTotalBytesWritten();
      for (int i = list.size() - 1; i >= 0; i--)
        writeVarint64(((Long)list.get(i)).longValue()); 
      int length = getTotalBytesWritten() - prevBytes;
      writeVarint32(length);
      writeTag(fieldNumber, 2);
    } else {
      for (int i = list.size() - 1; i >= 0; i--)
        writeUInt64(fieldNumber, ((Long)list.get(i)).longValue()); 
    } 
  }
  
  private final void writeUInt64List_Internal(int fieldNumber, LongArrayList list, boolean packed) throws IOException {
    if (packed) {
      requireSpace(10 + list.size() * 10);
      int prevBytes = getTotalBytesWritten();
      for (int i = list.size() - 1; i >= 0; i--)
        writeVarint64(list.getLong(i)); 
      int length = getTotalBytesWritten() - prevBytes;
      writeVarint32(length);
      writeTag(fieldNumber, 2);
    } else {
      for (int i = list.size() - 1; i >= 0; i--)
        writeUInt64(fieldNumber, list.getLong(i)); 
    } 
  }
  
  public final void writeFixed64List(int fieldNumber, List<Long> list, boolean packed) throws IOException {
    if (list instanceof LongArrayList) {
      writeFixed64List_Internal(fieldNumber, (LongArrayList)list, packed);
    } else {
      writeFixed64List_Internal(fieldNumber, list, packed);
    } 
  }
  
  private final void writeFixed64List_Internal(int fieldNumber, List<Long> list, boolean packed) throws IOException {
    if (packed) {
      requireSpace(10 + list.size() * 8);
      int prevBytes = getTotalBytesWritten();
      for (int i = list.size() - 1; i >= 0; i--)
        writeFixed64(((Long)list.get(i)).longValue()); 
      int length = getTotalBytesWritten() - prevBytes;
      writeVarint32(length);
      writeTag(fieldNumber, 2);
    } else {
      for (int i = list.size() - 1; i >= 0; i--)
        writeFixed64(fieldNumber, ((Long)list.get(i)).longValue()); 
    } 
  }
  
  private final void writeFixed64List_Internal(int fieldNumber, LongArrayList list, boolean packed) throws IOException {
    if (packed) {
      requireSpace(10 + list.size() * 8);
      int prevBytes = getTotalBytesWritten();
      for (int i = list.size() - 1; i >= 0; i--)
        writeFixed64(list.getLong(i)); 
      int length = getTotalBytesWritten() - prevBytes;
      writeVarint32(length);
      writeTag(fieldNumber, 2);
    } else {
      for (int i = list.size() - 1; i >= 0; i--)
        writeFixed64(fieldNumber, list.getLong(i)); 
    } 
  }
  
  public final void writeFloatList(int fieldNumber, List<Float> list, boolean packed) throws IOException {
    if (list instanceof FloatArrayList) {
      writeFloatList_Internal(fieldNumber, (FloatArrayList)list, packed);
    } else {
      writeFloatList_Internal(fieldNumber, list, packed);
    } 
  }
  
  private final void writeFloatList_Internal(int fieldNumber, List<Float> list, boolean packed) throws IOException {
    if (packed) {
      requireSpace(10 + list.size() * 4);
      int prevBytes = getTotalBytesWritten();
      for (int i = list.size() - 1; i >= 0; i--)
        writeFixed32(Float.floatToRawIntBits(((Float)list.get(i)).floatValue())); 
      int length = getTotalBytesWritten() - prevBytes;
      writeVarint32(length);
      writeTag(fieldNumber, 2);
    } else {
      for (int i = list.size() - 1; i >= 0; i--)
        writeFloat(fieldNumber, ((Float)list.get(i)).floatValue()); 
    } 
  }
  
  private final void writeFloatList_Internal(int fieldNumber, FloatArrayList list, boolean packed) throws IOException {
    if (packed) {
      requireSpace(10 + list.size() * 4);
      int prevBytes = getTotalBytesWritten();
      for (int i = list.size() - 1; i >= 0; i--)
        writeFixed32(Float.floatToRawIntBits(list.getFloat(i))); 
      int length = getTotalBytesWritten() - prevBytes;
      writeVarint32(length);
      writeTag(fieldNumber, 2);
    } else {
      for (int i = list.size() - 1; i >= 0; i--)
        writeFloat(fieldNumber, list.getFloat(i)); 
    } 
  }
  
  public final void writeDoubleList(int fieldNumber, List<Double> list, boolean packed) throws IOException {
    if (list instanceof DoubleArrayList) {
      writeDoubleList_Internal(fieldNumber, (DoubleArrayList)list, packed);
    } else {
      writeDoubleList_Internal(fieldNumber, list, packed);
    } 
  }
  
  private final void writeDoubleList_Internal(int fieldNumber, List<Double> list, boolean packed) throws IOException {
    if (packed) {
      requireSpace(10 + list.size() * 8);
      int prevBytes = getTotalBytesWritten();
      for (int i = list.size() - 1; i >= 0; i--)
        writeFixed64(Double.doubleToRawLongBits(((Double)list.get(i)).doubleValue())); 
      int length = getTotalBytesWritten() - prevBytes;
      writeVarint32(length);
      writeTag(fieldNumber, 2);
    } else {
      for (int i = list.size() - 1; i >= 0; i--)
        writeDouble(fieldNumber, ((Double)list.get(i)).doubleValue()); 
    } 
  }
  
  private final void writeDoubleList_Internal(int fieldNumber, DoubleArrayList list, boolean packed) throws IOException {
    if (packed) {
      requireSpace(10 + list.size() * 8);
      int prevBytes = getTotalBytesWritten();
      for (int i = list.size() - 1; i >= 0; i--)
        writeFixed64(Double.doubleToRawLongBits(list.getDouble(i))); 
      int length = getTotalBytesWritten() - prevBytes;
      writeVarint32(length);
      writeTag(fieldNumber, 2);
    } else {
      for (int i = list.size() - 1; i >= 0; i--)
        writeDouble(fieldNumber, list.getDouble(i)); 
    } 
  }
  
  public final void writeEnumList(int fieldNumber, List<Integer> list, boolean packed) throws IOException {
    writeInt32List(fieldNumber, list, packed);
  }
  
  public final void writeBoolList(int fieldNumber, List<Boolean> list, boolean packed) throws IOException {
    if (list instanceof BooleanArrayList) {
      writeBoolList_Internal(fieldNumber, (BooleanArrayList)list, packed);
    } else {
      writeBoolList_Internal(fieldNumber, list, packed);
    } 
  }
  
  private final void writeBoolList_Internal(int fieldNumber, List<Boolean> list, boolean packed) throws IOException {
    if (packed) {
      requireSpace(10 + list.size());
      int prevBytes = getTotalBytesWritten();
      for (int i = list.size() - 1; i >= 0; i--)
        writeBool(((Boolean)list.get(i)).booleanValue()); 
      int length = getTotalBytesWritten() - prevBytes;
      writeVarint32(length);
      writeTag(fieldNumber, 2);
    } else {
      for (int i = list.size() - 1; i >= 0; i--)
        writeBool(fieldNumber, ((Boolean)list.get(i)).booleanValue()); 
    } 
  }
  
  private final void writeBoolList_Internal(int fieldNumber, BooleanArrayList list, boolean packed) throws IOException {
    if (packed) {
      requireSpace(10 + list.size());
      int prevBytes = getTotalBytesWritten();
      for (int i = list.size() - 1; i >= 0; i--)
        writeBool(list.getBoolean(i)); 
      int length = getTotalBytesWritten() - prevBytes;
      writeVarint32(length);
      writeTag(fieldNumber, 2);
    } else {
      for (int i = list.size() - 1; i >= 0; i--)
        writeBool(fieldNumber, list.getBoolean(i)); 
    } 
  }
  
  public final void writeStringList(int fieldNumber, List<String> list) throws IOException {
    if (list instanceof LazyStringList) {
      LazyStringList lazyList = (LazyStringList)list;
      for (int i = list.size() - 1; i >= 0; i--)
        writeLazyString(fieldNumber, lazyList.getRaw(i)); 
    } else {
      for (int i = list.size() - 1; i >= 0; i--)
        writeString(fieldNumber, list.get(i)); 
    } 
  }
  
  private void writeLazyString(int fieldNumber, Object value) throws IOException {
    if (value instanceof String) {
      writeString(fieldNumber, (String)value);
    } else {
      writeBytes(fieldNumber, (ByteString)value);
    } 
  }
  
  public final void writeBytesList(int fieldNumber, List<ByteString> list) throws IOException {
    for (int i = list.size() - 1; i >= 0; i--)
      writeBytes(fieldNumber, list.get(i)); 
  }
  
  public final void writeUInt32List(int fieldNumber, List<Integer> list, boolean packed) throws IOException {
    if (list instanceof IntArrayList) {
      writeUInt32List_Internal(fieldNumber, (IntArrayList)list, packed);
    } else {
      writeUInt32List_Internal(fieldNumber, list, packed);
    } 
  }
  
  private final void writeUInt32List_Internal(int fieldNumber, List<Integer> list, boolean packed) throws IOException {
    if (packed) {
      requireSpace(10 + list.size() * 5);
      int prevBytes = getTotalBytesWritten();
      for (int i = list.size() - 1; i >= 0; i--)
        writeVarint32(((Integer)list.get(i)).intValue()); 
      int length = getTotalBytesWritten() - prevBytes;
      writeVarint32(length);
      writeTag(fieldNumber, 2);
    } else {
      for (int i = list.size() - 1; i >= 0; i--)
        writeUInt32(fieldNumber, ((Integer)list.get(i)).intValue()); 
    } 
  }
  
  private final void writeUInt32List_Internal(int fieldNumber, IntArrayList list, boolean packed) throws IOException {
    if (packed) {
      requireSpace(10 + list.size() * 5);
      int prevBytes = getTotalBytesWritten();
      for (int i = list.size() - 1; i >= 0; i--)
        writeVarint32(list.getInt(i)); 
      int length = getTotalBytesWritten() - prevBytes;
      writeVarint32(length);
      writeTag(fieldNumber, 2);
    } else {
      for (int i = list.size() - 1; i >= 0; i--)
        writeUInt32(fieldNumber, list.getInt(i)); 
    } 
  }
  
  public final void writeSFixed32List(int fieldNumber, List<Integer> list, boolean packed) throws IOException {
    writeFixed32List(fieldNumber, list, packed);
  }
  
  public final void writeSFixed64List(int fieldNumber, List<Long> list, boolean packed) throws IOException {
    writeFixed64List(fieldNumber, list, packed);
  }
  
  public final void writeSInt32List(int fieldNumber, List<Integer> list, boolean packed) throws IOException {
    if (list instanceof IntArrayList) {
      writeSInt32List_Internal(fieldNumber, (IntArrayList)list, packed);
    } else {
      writeSInt32List_Internal(fieldNumber, list, packed);
    } 
  }
  
  private final void writeSInt32List_Internal(int fieldNumber, List<Integer> list, boolean packed) throws IOException {
    if (packed) {
      requireSpace(10 + list.size() * 5);
      int prevBytes = getTotalBytesWritten();
      for (int i = list.size() - 1; i >= 0; i--)
        writeSInt32(((Integer)list.get(i)).intValue()); 
      int length = getTotalBytesWritten() - prevBytes;
      writeVarint32(length);
      writeTag(fieldNumber, 2);
    } else {
      for (int i = list.size() - 1; i >= 0; i--)
        writeSInt32(fieldNumber, ((Integer)list.get(i)).intValue()); 
    } 
  }
  
  private final void writeSInt32List_Internal(int fieldNumber, IntArrayList list, boolean packed) throws IOException {
    if (packed) {
      requireSpace(10 + list.size() * 5);
      int prevBytes = getTotalBytesWritten();
      for (int i = list.size() - 1; i >= 0; i--)
        writeSInt32(list.getInt(i)); 
      int length = getTotalBytesWritten() - prevBytes;
      writeVarint32(length);
      writeTag(fieldNumber, 2);
    } else {
      for (int i = list.size() - 1; i >= 0; i--)
        writeSInt32(fieldNumber, list.getInt(i)); 
    } 
  }
  
  public final void writeSInt64List(int fieldNumber, List<Long> list, boolean packed) throws IOException {
    if (list instanceof LongArrayList) {
      writeSInt64List_Internal(fieldNumber, (LongArrayList)list, packed);
    } else {
      writeSInt64List_Internal(fieldNumber, list, packed);
    } 
  }
  
  public <K, V> void writeMap(int fieldNumber, MapEntryLite.Metadata<K, V> metadata, Map<K, V> map) throws IOException {
    for (Map.Entry<K, V> entry : map.entrySet()) {
      int prevBytes = getTotalBytesWritten();
      writeMapEntryField(this, 2, metadata.valueType, entry.getValue());
      writeMapEntryField(this, 1, metadata.keyType, entry.getKey());
      int length = getTotalBytesWritten() - prevBytes;
      writeVarint32(length);
      writeTag(fieldNumber, 2);
    } 
  }
  
  static final void writeMapEntryField(Writer writer, int fieldNumber, WireFormat.FieldType fieldType, Object object) throws IOException {
    switch (fieldType) {
      case BOOL:
        writer.writeBool(fieldNumber, ((Boolean)object).booleanValue());
        return;
      case FIXED32:
        writer.writeFixed32(fieldNumber, ((Integer)object).intValue());
        return;
      case FIXED64:
        writer.writeFixed64(fieldNumber, ((Long)object).longValue());
        return;
      case INT32:
        writer.writeInt32(fieldNumber, ((Integer)object).intValue());
        return;
      case INT64:
        writer.writeInt64(fieldNumber, ((Long)object).longValue());
        return;
      case SFIXED32:
        writer.writeSFixed32(fieldNumber, ((Integer)object).intValue());
        return;
      case SFIXED64:
        writer.writeSFixed64(fieldNumber, ((Long)object).longValue());
        return;
      case SINT32:
        writer.writeSInt32(fieldNumber, ((Integer)object).intValue());
        return;
      case SINT64:
        writer.writeSInt64(fieldNumber, ((Long)object).longValue());
        return;
      case STRING:
        writer.writeString(fieldNumber, (String)object);
        return;
      case UINT32:
        writer.writeUInt32(fieldNumber, ((Integer)object).intValue());
        return;
      case UINT64:
        writer.writeUInt64(fieldNumber, ((Long)object).longValue());
        return;
      case FLOAT:
        writer.writeFloat(fieldNumber, ((Float)object).floatValue());
        return;
      case DOUBLE:
        writer.writeDouble(fieldNumber, ((Double)object).doubleValue());
        return;
      case MESSAGE:
        writer.writeMessage(fieldNumber, object);
        return;
      case BYTES:
        writer.writeBytes(fieldNumber, (ByteString)object);
        return;
      case ENUM:
        if (object instanceof Internal.EnumLite) {
          writer.writeEnum(fieldNumber, ((Internal.EnumLite)object).getNumber());
        } else if (object instanceof Integer) {
          writer.writeEnum(fieldNumber, ((Integer)object).intValue());
        } else {
          throw new IllegalArgumentException("Unexpected type for enum in map.");
        } 
        return;
    } 
    throw new IllegalArgumentException("Unsupported map value type for: " + fieldType);
  }
  
  private final void writeSInt64List_Internal(int fieldNumber, List<Long> list, boolean packed) throws IOException {
    if (packed) {
      requireSpace(10 + list.size() * 10);
      int prevBytes = getTotalBytesWritten();
      for (int i = list.size() - 1; i >= 0; i--)
        writeSInt64(((Long)list.get(i)).longValue()); 
      int length = getTotalBytesWritten() - prevBytes;
      writeVarint32(length);
      writeTag(fieldNumber, 2);
    } else {
      for (int i = list.size() - 1; i >= 0; i--)
        writeSInt64(fieldNumber, ((Long)list.get(i)).longValue()); 
    } 
  }
  
  private final void writeSInt64List_Internal(int fieldNumber, LongArrayList list, boolean packed) throws IOException {
    if (packed) {
      requireSpace(10 + list.size() * 10);
      int prevBytes = getTotalBytesWritten();
      for (int i = list.size() - 1; i >= 0; i--)
        writeSInt64(list.getLong(i)); 
      int length = getTotalBytesWritten() - prevBytes;
      writeVarint32(length);
      writeTag(fieldNumber, 2);
    } else {
      for (int i = list.size() - 1; i >= 0; i--)
        writeSInt64(fieldNumber, list.getLong(i)); 
    } 
  }
  
  public final void writeMessageList(int fieldNumber, List<?> list) throws IOException {
    for (int i = list.size() - 1; i >= 0; i--)
      writeMessage(fieldNumber, list.get(i)); 
  }
  
  public final void writeMessageList(int fieldNumber, List<?> list, Schema schema) throws IOException {
    for (int i = list.size() - 1; i >= 0; i--)
      writeMessage(fieldNumber, list.get(i), schema); 
  }
  
  @Deprecated
  public final void writeGroupList(int fieldNumber, List<?> list) throws IOException {
    for (int i = list.size() - 1; i >= 0; i--)
      writeGroup(fieldNumber, list.get(i)); 
  }
  
  @Deprecated
  public final void writeGroupList(int fieldNumber, List<?> list, Schema schema) throws IOException {
    for (int i = list.size() - 1; i >= 0; i--)
      writeGroup(fieldNumber, list.get(i), schema); 
  }
  
  public final void writeMessageSetItem(int fieldNumber, Object value) throws IOException {
    writeTag(1, 4);
    if (value instanceof ByteString) {
      writeBytes(3, (ByteString)value);
    } else {
      writeMessage(3, value);
    } 
    writeUInt32(2, fieldNumber);
    writeTag(1, 3);
  }
  
  final AllocatedBuffer newHeapBuffer() {
    return this.alloc.allocateHeapBuffer(this.chunkSize);
  }
  
  final AllocatedBuffer newHeapBuffer(int capacity) {
    return this.alloc.allocateHeapBuffer(Math.max(capacity, this.chunkSize));
  }
  
  final AllocatedBuffer newDirectBuffer() {
    return this.alloc.allocateDirectBuffer(this.chunkSize);
  }
  
  final AllocatedBuffer newDirectBuffer(int capacity) {
    return this.alloc.allocateDirectBuffer(Math.max(capacity, this.chunkSize));
  }
  
  private static byte computeUInt64SizeNoTag(long value) {
    if ((value & 0xFFFFFFFFFFFFFF80L) == 0L)
      return 1; 
    if (value < 0L)
      return 10; 
    byte n = 2;
    if ((value & 0xFFFFFFF800000000L) != 0L) {
      n = (byte)(n + 4);
      value >>>= 28L;
    } 
    if ((value & 0xFFFFFFFFFFE00000L) != 0L) {
      n = (byte)(n + 2);
      value >>>= 14L;
    } 
    if ((value & 0xFFFFFFFFFFFFC000L) != 0L)
      n = (byte)(n + 1); 
    return n;
  }
  
  public abstract int getTotalBytesWritten();
  
  abstract void requireSpace(int paramInt);
  
  abstract void finishCurrentBuffer();
  
  abstract void writeTag(int paramInt1, int paramInt2);
  
  abstract void writeVarint32(int paramInt);
  
  abstract void writeInt32(int paramInt);
  
  abstract void writeSInt32(int paramInt);
  
  abstract void writeFixed32(int paramInt);
  
  abstract void writeVarint64(long paramLong);
  
  abstract void writeSInt64(long paramLong);
  
  abstract void writeFixed64(long paramLong);
  
  abstract void writeBool(boolean paramBoolean);
  
  abstract void writeString(String paramString);
  
  private static final class SafeHeapWriter extends BinaryWriter {
    private AllocatedBuffer allocatedBuffer;
    
    private byte[] buffer;
    
    private int offset;
    
    private int limit;
    
    private int offsetMinusOne;
    
    private int limitMinusOne;
    
    private int pos;
    
    SafeHeapWriter(BufferAllocator alloc, int chunkSize) {
      super(alloc, chunkSize);
      nextBuffer();
    }
    
    void finishCurrentBuffer() {
      if (this.allocatedBuffer != null) {
        this.totalDoneBytes += bytesWrittenToCurrentBuffer();
        this.allocatedBuffer.position(this.pos - this.allocatedBuffer.arrayOffset() + 1);
        this.allocatedBuffer = null;
        this.pos = 0;
        this.limitMinusOne = 0;
      } 
    }
    
    private void nextBuffer() {
      nextBuffer(newHeapBuffer());
    }
    
    private void nextBuffer(int capacity) {
      nextBuffer(newHeapBuffer(capacity));
    }
    
    private void nextBuffer(AllocatedBuffer allocatedBuffer) {
      if (!allocatedBuffer.hasArray())
        throw new RuntimeException("Allocator returned non-heap buffer"); 
      finishCurrentBuffer();
      this.buffers.addFirst(allocatedBuffer);
      this.allocatedBuffer = allocatedBuffer;
      this.buffer = allocatedBuffer.array();
      int arrayOffset = allocatedBuffer.arrayOffset();
      this.limit = arrayOffset + allocatedBuffer.limit();
      this.offset = arrayOffset + allocatedBuffer.position();
      this.offsetMinusOne = this.offset - 1;
      this.limitMinusOne = this.limit - 1;
      this.pos = this.limitMinusOne;
    }
    
    public int getTotalBytesWritten() {
      return this.totalDoneBytes + bytesWrittenToCurrentBuffer();
    }
    
    int bytesWrittenToCurrentBuffer() {
      return this.limitMinusOne - this.pos;
    }
    
    int spaceLeft() {
      return this.pos - this.offsetMinusOne;
    }
    
    public void writeUInt32(int fieldNumber, int value) throws IOException {
      requireSpace(10);
      writeVarint32(value);
      writeTag(fieldNumber, 0);
    }
    
    public void writeInt32(int fieldNumber, int value) throws IOException {
      requireSpace(15);
      writeInt32(value);
      writeTag(fieldNumber, 0);
    }
    
    public void writeSInt32(int fieldNumber, int value) throws IOException {
      requireSpace(10);
      writeSInt32(value);
      writeTag(fieldNumber, 0);
    }
    
    public void writeFixed32(int fieldNumber, int value) throws IOException {
      requireSpace(9);
      writeFixed32(value);
      writeTag(fieldNumber, 5);
    }
    
    public void writeUInt64(int fieldNumber, long value) throws IOException {
      requireSpace(15);
      writeVarint64(value);
      writeTag(fieldNumber, 0);
    }
    
    public void writeSInt64(int fieldNumber, long value) throws IOException {
      requireSpace(15);
      writeSInt64(value);
      writeTag(fieldNumber, 0);
    }
    
    public void writeFixed64(int fieldNumber, long value) throws IOException {
      requireSpace(13);
      writeFixed64(value);
      writeTag(fieldNumber, 1);
    }
    
    public void writeBool(int fieldNumber, boolean value) throws IOException {
      requireSpace(6);
      write((byte)(value ? 1 : 0));
      writeTag(fieldNumber, 0);
    }
    
    public void writeString(int fieldNumber, String value) throws IOException {
      int prevBytes = getTotalBytesWritten();
      writeString(value);
      int length = getTotalBytesWritten() - prevBytes;
      requireSpace(10);
      writeVarint32(length);
      writeTag(fieldNumber, 2);
    }
    
    public void writeBytes(int fieldNumber, ByteString value) throws IOException {
      try {
        value.writeToReverse(this);
      } catch (IOException e) {
        throw new RuntimeException(e);
      } 
      requireSpace(10);
      writeVarint32(value.size());
      writeTag(fieldNumber, 2);
    }
    
    public void writeMessage(int fieldNumber, Object value) throws IOException {
      int prevBytes = getTotalBytesWritten();
      Protobuf.getInstance().writeTo(value, this);
      int length = getTotalBytesWritten() - prevBytes;
      requireSpace(10);
      writeVarint32(length);
      writeTag(fieldNumber, 2);
    }
    
    public void writeMessage(int fieldNumber, Object value, Schema<Object> schema) throws IOException {
      int prevBytes = getTotalBytesWritten();
      schema.writeTo(value, this);
      int length = getTotalBytesWritten() - prevBytes;
      requireSpace(10);
      writeVarint32(length);
      writeTag(fieldNumber, 2);
    }
    
    @Deprecated
    public void writeGroup(int fieldNumber, Object value) throws IOException {
      writeTag(fieldNumber, 4);
      Protobuf.getInstance().writeTo(value, this);
      writeTag(fieldNumber, 3);
    }
    
    public void writeGroup(int fieldNumber, Object value, Schema<Object> schema) throws IOException {
      writeTag(fieldNumber, 4);
      schema.writeTo(value, this);
      writeTag(fieldNumber, 3);
    }
    
    public void writeStartGroup(int fieldNumber) {
      writeTag(fieldNumber, 3);
    }
    
    public void writeEndGroup(int fieldNumber) {
      writeTag(fieldNumber, 4);
    }
    
    void writeInt32(int value) {
      if (value >= 0) {
        writeVarint32(value);
      } else {
        writeVarint64(value);
      } 
    }
    
    void writeSInt32(int value) {
      writeVarint32(CodedOutputStream.encodeZigZag32(value));
    }
    
    void writeSInt64(long value) {
      writeVarint64(CodedOutputStream.encodeZigZag64(value));
    }
    
    void writeBool(boolean value) {
      write((byte)(value ? 1 : 0));
    }
    
    void writeTag(int fieldNumber, int wireType) {
      writeVarint32(WireFormat.makeTag(fieldNumber, wireType));
    }
    
    void writeVarint32(int value) {
      if ((value & 0xFFFFFF80) == 0) {
        writeVarint32OneByte(value);
      } else if ((value & 0xFFFFC000) == 0) {
        writeVarint32TwoBytes(value);
      } else if ((value & 0xFFE00000) == 0) {
        writeVarint32ThreeBytes(value);
      } else if ((value & 0xF0000000) == 0) {
        writeVarint32FourBytes(value);
      } else {
        writeVarint32FiveBytes(value);
      } 
    }
    
    private void writeVarint32OneByte(int value) {
      this.buffer[this.pos--] = (byte)value;
    }
    
    private void writeVarint32TwoBytes(int value) {
      this.buffer[this.pos--] = (byte)(value >>> 7);
      this.buffer[this.pos--] = (byte)(value & 0x7F | 0x80);
    }
    
    private void writeVarint32ThreeBytes(int value) {
      this.buffer[this.pos--] = (byte)(value >>> 14);
      this.buffer[this.pos--] = (byte)(value >>> 7 & 0x7F | 0x80);
      this.buffer[this.pos--] = (byte)(value & 0x7F | 0x80);
    }
    
    private void writeVarint32FourBytes(int value) {
      this.buffer[this.pos--] = (byte)(value >>> 21);
      this.buffer[this.pos--] = (byte)(value >>> 14 & 0x7F | 0x80);
      this.buffer[this.pos--] = (byte)(value >>> 7 & 0x7F | 0x80);
      this.buffer[this.pos--] = (byte)(value & 0x7F | 0x80);
    }
    
    private void writeVarint32FiveBytes(int value) {
      this.buffer[this.pos--] = (byte)(value >>> 28);
      this.buffer[this.pos--] = (byte)(value >>> 21 & 0x7F | 0x80);
      this.buffer[this.pos--] = (byte)(value >>> 14 & 0x7F | 0x80);
      this.buffer[this.pos--] = (byte)(value >>> 7 & 0x7F | 0x80);
      this.buffer[this.pos--] = (byte)(value & 0x7F | 0x80);
    }
    
    void writeVarint64(long value) {
      switch (BinaryWriter.computeUInt64SizeNoTag(value)) {
        case 1:
          writeVarint64OneByte(value);
          break;
        case 2:
          writeVarint64TwoBytes(value);
          break;
        case 3:
          writeVarint64ThreeBytes(value);
          break;
        case 4:
          writeVarint64FourBytes(value);
          break;
        case 5:
          writeVarint64FiveBytes(value);
          break;
        case 6:
          writeVarint64SixBytes(value);
          break;
        case 7:
          writeVarint64SevenBytes(value);
          break;
        case 8:
          writeVarint64EightBytes(value);
          break;
        case 9:
          writeVarint64NineBytes(value);
          break;
        case 10:
          writeVarint64TenBytes(value);
          break;
      } 
    }
    
    private void writeVarint64OneByte(long value) {
      this.buffer[this.pos--] = (byte)(int)value;
    }
    
    private void writeVarint64TwoBytes(long value) {
      this.buffer[this.pos--] = (byte)(int)(value >>> 7L);
      this.buffer[this.pos--] = (byte)((int)value & 0x7F | 0x80);
    }
    
    private void writeVarint64ThreeBytes(long value) {
      this.buffer[this.pos--] = (byte)((int)value >>> 14);
      this.buffer[this.pos--] = (byte)(int)(value >>> 7L & 0x7FL | 0x80L);
      this.buffer[this.pos--] = (byte)(int)(value & 0x7FL | 0x80L);
    }
    
    private void writeVarint64FourBytes(long value) {
      this.buffer[this.pos--] = (byte)(int)(value >>> 21L);
      this.buffer[this.pos--] = (byte)(int)(value >>> 14L & 0x7FL | 0x80L);
      this.buffer[this.pos--] = (byte)(int)(value >>> 7L & 0x7FL | 0x80L);
      this.buffer[this.pos--] = (byte)(int)(value & 0x7FL | 0x80L);
    }
    
    private void writeVarint64FiveBytes(long value) {
      this.buffer[this.pos--] = (byte)(int)(value >>> 28L);
      this.buffer[this.pos--] = (byte)(int)(value >>> 21L & 0x7FL | 0x80L);
      this.buffer[this.pos--] = (byte)(int)(value >>> 14L & 0x7FL | 0x80L);
      this.buffer[this.pos--] = (byte)(int)(value >>> 7L & 0x7FL | 0x80L);
      this.buffer[this.pos--] = (byte)(int)(value & 0x7FL | 0x80L);
    }
    
    private void writeVarint64SixBytes(long value) {
      this.buffer[this.pos--] = (byte)(int)(value >>> 35L);
      this.buffer[this.pos--] = (byte)(int)(value >>> 28L & 0x7FL | 0x80L);
      this.buffer[this.pos--] = (byte)(int)(value >>> 21L & 0x7FL | 0x80L);
      this.buffer[this.pos--] = (byte)(int)(value >>> 14L & 0x7FL | 0x80L);
      this.buffer[this.pos--] = (byte)(int)(value >>> 7L & 0x7FL | 0x80L);
      this.buffer[this.pos--] = (byte)(int)(value & 0x7FL | 0x80L);
    }
    
    private void writeVarint64SevenBytes(long value) {
      this.buffer[this.pos--] = (byte)(int)(value >>> 42L);
      this.buffer[this.pos--] = (byte)(int)(value >>> 35L & 0x7FL | 0x80L);
      this.buffer[this.pos--] = (byte)(int)(value >>> 28L & 0x7FL | 0x80L);
      this.buffer[this.pos--] = (byte)(int)(value >>> 21L & 0x7FL | 0x80L);
      this.buffer[this.pos--] = (byte)(int)(value >>> 14L & 0x7FL | 0x80L);
      this.buffer[this.pos--] = (byte)(int)(value >>> 7L & 0x7FL | 0x80L);
      this.buffer[this.pos--] = (byte)(int)(value & 0x7FL | 0x80L);
    }
    
    private void writeVarint64EightBytes(long value) {
      this.buffer[this.pos--] = (byte)(int)(value >>> 49L);
      this.buffer[this.pos--] = (byte)(int)(value >>> 42L & 0x7FL | 0x80L);
      this.buffer[this.pos--] = (byte)(int)(value >>> 35L & 0x7FL | 0x80L);
      this.buffer[this.pos--] = (byte)(int)(value >>> 28L & 0x7FL | 0x80L);
      this.buffer[this.pos--] = (byte)(int)(value >>> 21L & 0x7FL | 0x80L);
      this.buffer[this.pos--] = (byte)(int)(value >>> 14L & 0x7FL | 0x80L);
      this.buffer[this.pos--] = (byte)(int)(value >>> 7L & 0x7FL | 0x80L);
      this.buffer[this.pos--] = (byte)(int)(value & 0x7FL | 0x80L);
    }
    
    private void writeVarint64NineBytes(long value) {
      this.buffer[this.pos--] = (byte)(int)(value >>> 56L);
      this.buffer[this.pos--] = (byte)(int)(value >>> 49L & 0x7FL | 0x80L);
      this.buffer[this.pos--] = (byte)(int)(value >>> 42L & 0x7FL | 0x80L);
      this.buffer[this.pos--] = (byte)(int)(value >>> 35L & 0x7FL | 0x80L);
      this.buffer[this.pos--] = (byte)(int)(value >>> 28L & 0x7FL | 0x80L);
      this.buffer[this.pos--] = (byte)(int)(value >>> 21L & 0x7FL | 0x80L);
      this.buffer[this.pos--] = (byte)(int)(value >>> 14L & 0x7FL | 0x80L);
      this.buffer[this.pos--] = (byte)(int)(value >>> 7L & 0x7FL | 0x80L);
      this.buffer[this.pos--] = (byte)(int)(value & 0x7FL | 0x80L);
    }
    
    private void writeVarint64TenBytes(long value) {
      this.buffer[this.pos--] = (byte)(int)(value >>> 63L);
      this.buffer[this.pos--] = (byte)(int)(value >>> 56L & 0x7FL | 0x80L);
      this.buffer[this.pos--] = (byte)(int)(value >>> 49L & 0x7FL | 0x80L);
      this.buffer[this.pos--] = (byte)(int)(value >>> 42L & 0x7FL | 0x80L);
      this.buffer[this.pos--] = (byte)(int)(value >>> 35L & 0x7FL | 0x80L);
      this.buffer[this.pos--] = (byte)(int)(value >>> 28L & 0x7FL | 0x80L);
      this.buffer[this.pos--] = (byte)(int)(value >>> 21L & 0x7FL | 0x80L);
      this.buffer[this.pos--] = (byte)(int)(value >>> 14L & 0x7FL | 0x80L);
      this.buffer[this.pos--] = (byte)(int)(value >>> 7L & 0x7FL | 0x80L);
      this.buffer[this.pos--] = (byte)(int)(value & 0x7FL | 0x80L);
    }
    
    void writeFixed32(int value) {
      this.buffer[this.pos--] = (byte)(value >> 24 & 0xFF);
      this.buffer[this.pos--] = (byte)(value >> 16 & 0xFF);
      this.buffer[this.pos--] = (byte)(value >> 8 & 0xFF);
      this.buffer[this.pos--] = (byte)(value & 0xFF);
    }
    
    void writeFixed64(long value) {
      this.buffer[this.pos--] = (byte)((int)(value >> 56L) & 0xFF);
      this.buffer[this.pos--] = (byte)((int)(value >> 48L) & 0xFF);
      this.buffer[this.pos--] = (byte)((int)(value >> 40L) & 0xFF);
      this.buffer[this.pos--] = (byte)((int)(value >> 32L) & 0xFF);
      this.buffer[this.pos--] = (byte)((int)(value >> 24L) & 0xFF);
      this.buffer[this.pos--] = (byte)((int)(value >> 16L) & 0xFF);
      this.buffer[this.pos--] = (byte)((int)(value >> 8L) & 0xFF);
      this.buffer[this.pos--] = (byte)((int)value & 0xFF);
    }
    
    void writeString(String in) {
      requireSpace(in.length());
      int i = in.length() - 1;
      this.pos -= i;
      char c;
      for (; i >= 0 && (c = in.charAt(i)) < ''; i--)
        this.buffer[this.pos + i] = (byte)c; 
      if (i == -1) {
        this.pos--;
        return;
      } 
      this.pos += i;
      for (; i >= 0; i--) {
        c = in.charAt(i);
        if (c < '' && this.pos > this.offsetMinusOne) {
          this.buffer[this.pos--] = (byte)c;
        } else if (c < 'ࠀ' && this.pos > this.offset) {
          this.buffer[this.pos--] = (byte)(0x80 | 0x3F & c);
          this.buffer[this.pos--] = (byte)(0x3C0 | c >>> 6);
        } else if ((c < '?' || '?' < c) && this.pos > this.offset + 1) {
          this.buffer[this.pos--] = (byte)(0x80 | 0x3F & c);
          this.buffer[this.pos--] = (byte)(0x80 | 0x3F & c >>> 6);
          this.buffer[this.pos--] = (byte)(0x1E0 | c >>> 12);
        } else if (this.pos > this.offset + 2) {
          char high = Character.MIN_VALUE;
          if (i == 0 || !Character.isSurrogatePair(high = in.charAt(i - 1), c))
            throw new Utf8.UnpairedSurrogateException(i - 1, i); 
          i--;
          int codePoint = Character.toCodePoint(high, c);
          this.buffer[this.pos--] = (byte)(0x80 | 0x3F & codePoint);
          this.buffer[this.pos--] = (byte)(0x80 | 0x3F & codePoint >>> 6);
          this.buffer[this.pos--] = (byte)(0x80 | 0x3F & codePoint >>> 12);
          this.buffer[this.pos--] = (byte)(0xF0 | codePoint >>> 18);
        } else {
          requireSpace(i);
          i++;
        } 
      } 
    }
    
    public void write(byte value) {
      this.buffer[this.pos--] = value;
    }
    
    public void write(byte[] value, int offset, int length) {
      if (spaceLeft() < length)
        nextBuffer(length); 
      this.pos -= length;
      System.arraycopy(value, offset, this.buffer, this.pos + 1, length);
    }
    
    public void writeLazy(byte[] value, int offset, int length) {
      if (spaceLeft() < length) {
        this.totalDoneBytes += length;
        this.buffers.addFirst(AllocatedBuffer.wrap(value, offset, length));
        nextBuffer();
        return;
      } 
      this.pos -= length;
      System.arraycopy(value, offset, this.buffer, this.pos + 1, length);
    }
    
    public void write(ByteBuffer value) {
      int length = value.remaining();
      if (spaceLeft() < length)
        nextBuffer(length); 
      this.pos -= length;
      value.get(this.buffer, this.pos + 1, length);
    }
    
    public void writeLazy(ByteBuffer value) {
      int length = value.remaining();
      if (spaceLeft() < length) {
        this.totalDoneBytes += length;
        this.buffers.addFirst(AllocatedBuffer.wrap(value));
        nextBuffer();
      } 
      this.pos -= length;
      value.get(this.buffer, this.pos + 1, length);
    }
    
    void requireSpace(int size) {
      if (spaceLeft() < size)
        nextBuffer(size); 
    }
  }
  
  private static final class UnsafeHeapWriter extends BinaryWriter {
    private AllocatedBuffer allocatedBuffer;
    
    private byte[] buffer;
    
    private long offset;
    
    private long limit;
    
    private long offsetMinusOne;
    
    private long limitMinusOne;
    
    private long pos;
    
    UnsafeHeapWriter(BufferAllocator alloc, int chunkSize) {
      super(alloc, chunkSize);
      nextBuffer();
    }
    
    static boolean isSupported() {
      return UnsafeUtil.hasUnsafeArrayOperations();
    }
    
    void finishCurrentBuffer() {
      if (this.allocatedBuffer != null) {
        this.totalDoneBytes += bytesWrittenToCurrentBuffer();
        this.allocatedBuffer.position(arrayPos() - this.allocatedBuffer.arrayOffset() + 1);
        this.allocatedBuffer = null;
        this.pos = 0L;
        this.limitMinusOne = 0L;
      } 
    }
    
    private int arrayPos() {
      return (int)this.pos;
    }
    
    private void nextBuffer() {
      nextBuffer(newHeapBuffer());
    }
    
    private void nextBuffer(int capacity) {
      nextBuffer(newHeapBuffer(capacity));
    }
    
    private void nextBuffer(AllocatedBuffer allocatedBuffer) {
      if (!allocatedBuffer.hasArray())
        throw new RuntimeException("Allocator returned non-heap buffer"); 
      finishCurrentBuffer();
      this.buffers.addFirst(allocatedBuffer);
      this.allocatedBuffer = allocatedBuffer;
      this.buffer = allocatedBuffer.array();
      int arrayOffset = allocatedBuffer.arrayOffset();
      this.limit = arrayOffset + allocatedBuffer.limit();
      this.offset = arrayOffset + allocatedBuffer.position();
      this.offsetMinusOne = this.offset - 1L;
      this.limitMinusOne = this.limit - 1L;
      this.pos = this.limitMinusOne;
    }
    
    public int getTotalBytesWritten() {
      return this.totalDoneBytes + bytesWrittenToCurrentBuffer();
    }
    
    int bytesWrittenToCurrentBuffer() {
      return (int)(this.limitMinusOne - this.pos);
    }
    
    int spaceLeft() {
      return (int)(this.pos - this.offsetMinusOne);
    }
    
    public void writeUInt32(int fieldNumber, int value) {
      requireSpace(10);
      writeVarint32(value);
      writeTag(fieldNumber, 0);
    }
    
    public void writeInt32(int fieldNumber, int value) {
      requireSpace(15);
      writeInt32(value);
      writeTag(fieldNumber, 0);
    }
    
    public void writeSInt32(int fieldNumber, int value) {
      requireSpace(10);
      writeSInt32(value);
      writeTag(fieldNumber, 0);
    }
    
    public void writeFixed32(int fieldNumber, int value) {
      requireSpace(9);
      writeFixed32(value);
      writeTag(fieldNumber, 5);
    }
    
    public void writeUInt64(int fieldNumber, long value) {
      requireSpace(15);
      writeVarint64(value);
      writeTag(fieldNumber, 0);
    }
    
    public void writeSInt64(int fieldNumber, long value) {
      requireSpace(15);
      writeSInt64(value);
      writeTag(fieldNumber, 0);
    }
    
    public void writeFixed64(int fieldNumber, long value) {
      requireSpace(13);
      writeFixed64(value);
      writeTag(fieldNumber, 1);
    }
    
    public void writeBool(int fieldNumber, boolean value) {
      requireSpace(6);
      write((byte)(value ? 1 : 0));
      writeTag(fieldNumber, 0);
    }
    
    public void writeString(int fieldNumber, String value) {
      int prevBytes = getTotalBytesWritten();
      writeString(value);
      int length = getTotalBytesWritten() - prevBytes;
      requireSpace(10);
      writeVarint32(length);
      writeTag(fieldNumber, 2);
    }
    
    public void writeBytes(int fieldNumber, ByteString value) {
      try {
        value.writeToReverse(this);
      } catch (IOException e) {
        throw new RuntimeException(e);
      } 
      requireSpace(10);
      writeVarint32(value.size());
      writeTag(fieldNumber, 2);
    }
    
    public void writeMessage(int fieldNumber, Object value) throws IOException {
      int prevBytes = getTotalBytesWritten();
      Protobuf.getInstance().writeTo(value, this);
      int length = getTotalBytesWritten() - prevBytes;
      requireSpace(10);
      writeVarint32(length);
      writeTag(fieldNumber, 2);
    }
    
    public void writeMessage(int fieldNumber, Object value, Schema<Object> schema) throws IOException {
      int prevBytes = getTotalBytesWritten();
      schema.writeTo(value, this);
      int length = getTotalBytesWritten() - prevBytes;
      requireSpace(10);
      writeVarint32(length);
      writeTag(fieldNumber, 2);
    }
    
    public void writeGroup(int fieldNumber, Object value) throws IOException {
      writeTag(fieldNumber, 4);
      Protobuf.getInstance().writeTo(value, this);
      writeTag(fieldNumber, 3);
    }
    
    public void writeGroup(int fieldNumber, Object value, Schema<Object> schema) throws IOException {
      writeTag(fieldNumber, 4);
      schema.writeTo(value, this);
      writeTag(fieldNumber, 3);
    }
    
    public void writeStartGroup(int fieldNumber) {
      writeTag(fieldNumber, 3);
    }
    
    public void writeEndGroup(int fieldNumber) {
      writeTag(fieldNumber, 4);
    }
    
    void writeInt32(int value) {
      if (value >= 0) {
        writeVarint32(value);
      } else {
        writeVarint64(value);
      } 
    }
    
    void writeSInt32(int value) {
      writeVarint32(CodedOutputStream.encodeZigZag32(value));
    }
    
    void writeSInt64(long value) {
      writeVarint64(CodedOutputStream.encodeZigZag64(value));
    }
    
    void writeBool(boolean value) {
      write((byte)(value ? 1 : 0));
    }
    
    void writeTag(int fieldNumber, int wireType) {
      writeVarint32(WireFormat.makeTag(fieldNumber, wireType));
    }
    
    void writeVarint32(int value) {
      if ((value & 0xFFFFFF80) == 0) {
        writeVarint32OneByte(value);
      } else if ((value & 0xFFFFC000) == 0) {
        writeVarint32TwoBytes(value);
      } else if ((value & 0xFFE00000) == 0) {
        writeVarint32ThreeBytes(value);
      } else if ((value & 0xF0000000) == 0) {
        writeVarint32FourBytes(value);
      } else {
        writeVarint32FiveBytes(value);
      } 
    }
    
    private void writeVarint32OneByte(int value) {
      UnsafeUtil.putByte(this.buffer, this.pos--, (byte)value);
    }
    
    private void writeVarint32TwoBytes(int value) {
      UnsafeUtil.putByte(this.buffer, this.pos--, (byte)(value >>> 7));
      UnsafeUtil.putByte(this.buffer, this.pos--, (byte)(value & 0x7F | 0x80));
    }
    
    private void writeVarint32ThreeBytes(int value) {
      UnsafeUtil.putByte(this.buffer, this.pos--, (byte)(value >>> 14));
      UnsafeUtil.putByte(this.buffer, this.pos--, (byte)(value >>> 7 & 0x7F | 0x80));
      UnsafeUtil.putByte(this.buffer, this.pos--, (byte)(value & 0x7F | 0x80));
    }
    
    private void writeVarint32FourBytes(int value) {
      UnsafeUtil.putByte(this.buffer, this.pos--, (byte)(value >>> 21));
      UnsafeUtil.putByte(this.buffer, this.pos--, (byte)(value >>> 14 & 0x7F | 0x80));
      UnsafeUtil.putByte(this.buffer, this.pos--, (byte)(value >>> 7 & 0x7F | 0x80));
      UnsafeUtil.putByte(this.buffer, this.pos--, (byte)(value & 0x7F | 0x80));
    }
    
    private void writeVarint32FiveBytes(int value) {
      UnsafeUtil.putByte(this.buffer, this.pos--, (byte)(value >>> 28));
      UnsafeUtil.putByte(this.buffer, this.pos--, (byte)(value >>> 21 & 0x7F | 0x80));
      UnsafeUtil.putByte(this.buffer, this.pos--, (byte)(value >>> 14 & 0x7F | 0x80));
      UnsafeUtil.putByte(this.buffer, this.pos--, (byte)(value >>> 7 & 0x7F | 0x80));
      UnsafeUtil.putByte(this.buffer, this.pos--, (byte)(value & 0x7F | 0x80));
    }
    
    void writeVarint64(long value) {
      switch (BinaryWriter.computeUInt64SizeNoTag(value)) {
        case 1:
          writeVarint64OneByte(value);
          break;
        case 2:
          writeVarint64TwoBytes(value);
          break;
        case 3:
          writeVarint64ThreeBytes(value);
          break;
        case 4:
          writeVarint64FourBytes(value);
          break;
        case 5:
          writeVarint64FiveBytes(value);
          break;
        case 6:
          writeVarint64SixBytes(value);
          break;
        case 7:
          writeVarint64SevenBytes(value);
          break;
        case 8:
          writeVarint64EightBytes(value);
          break;
        case 9:
          writeVarint64NineBytes(value);
          break;
        case 10:
          writeVarint64TenBytes(value);
          break;
      } 
    }
    
    private void writeVarint64OneByte(long value) {
      UnsafeUtil.putByte(this.buffer, this.pos--, (byte)(int)value);
    }
    
    private void writeVarint64TwoBytes(long value) {
      UnsafeUtil.putByte(this.buffer, this.pos--, (byte)(int)(value >>> 7L));
      UnsafeUtil.putByte(this.buffer, this.pos--, (byte)((int)value & 0x7F | 0x80));
    }
    
    private void writeVarint64ThreeBytes(long value) {
      UnsafeUtil.putByte(this.buffer, this.pos--, (byte)((int)value >>> 14));
      UnsafeUtil.putByte(this.buffer, this.pos--, (byte)(int)(value >>> 7L & 0x7FL | 0x80L));
      UnsafeUtil.putByte(this.buffer, this.pos--, (byte)(int)(value & 0x7FL | 0x80L));
    }
    
    private void writeVarint64FourBytes(long value) {
      UnsafeUtil.putByte(this.buffer, this.pos--, (byte)(int)(value >>> 21L));
      UnsafeUtil.putByte(this.buffer, this.pos--, (byte)(int)(value >>> 14L & 0x7FL | 0x80L));
      UnsafeUtil.putByte(this.buffer, this.pos--, (byte)(int)(value >>> 7L & 0x7FL | 0x80L));
      UnsafeUtil.putByte(this.buffer, this.pos--, (byte)(int)(value & 0x7FL | 0x80L));
    }
    
    private void writeVarint64FiveBytes(long value) {
      UnsafeUtil.putByte(this.buffer, this.pos--, (byte)(int)(value >>> 28L));
      UnsafeUtil.putByte(this.buffer, this.pos--, (byte)(int)(value >>> 21L & 0x7FL | 0x80L));
      UnsafeUtil.putByte(this.buffer, this.pos--, (byte)(int)(value >>> 14L & 0x7FL | 0x80L));
      UnsafeUtil.putByte(this.buffer, this.pos--, (byte)(int)(value >>> 7L & 0x7FL | 0x80L));
      UnsafeUtil.putByte(this.buffer, this.pos--, (byte)(int)(value & 0x7FL | 0x80L));
    }
    
    private void writeVarint64SixBytes(long value) {
      UnsafeUtil.putByte(this.buffer, this.pos--, (byte)(int)(value >>> 35L));
      UnsafeUtil.putByte(this.buffer, this.pos--, (byte)(int)(value >>> 28L & 0x7FL | 0x80L));
      UnsafeUtil.putByte(this.buffer, this.pos--, (byte)(int)(value >>> 21L & 0x7FL | 0x80L));
      UnsafeUtil.putByte(this.buffer, this.pos--, (byte)(int)(value >>> 14L & 0x7FL | 0x80L));
      UnsafeUtil.putByte(this.buffer, this.pos--, (byte)(int)(value >>> 7L & 0x7FL | 0x80L));
      UnsafeUtil.putByte(this.buffer, this.pos--, (byte)(int)(value & 0x7FL | 0x80L));
    }
    
    private void writeVarint64SevenBytes(long value) {
      UnsafeUtil.putByte(this.buffer, this.pos--, (byte)(int)(value >>> 42L));
      UnsafeUtil.putByte(this.buffer, this.pos--, (byte)(int)(value >>> 35L & 0x7FL | 0x80L));
      UnsafeUtil.putByte(this.buffer, this.pos--, (byte)(int)(value >>> 28L & 0x7FL | 0x80L));
      UnsafeUtil.putByte(this.buffer, this.pos--, (byte)(int)(value >>> 21L & 0x7FL | 0x80L));
      UnsafeUtil.putByte(this.buffer, this.pos--, (byte)(int)(value >>> 14L & 0x7FL | 0x80L));
      UnsafeUtil.putByte(this.buffer, this.pos--, (byte)(int)(value >>> 7L & 0x7FL | 0x80L));
      UnsafeUtil.putByte(this.buffer, this.pos--, (byte)(int)(value & 0x7FL | 0x80L));
    }
    
    private void writeVarint64EightBytes(long value) {
      UnsafeUtil.putByte(this.buffer, this.pos--, (byte)(int)(value >>> 49L));
      UnsafeUtil.putByte(this.buffer, this.pos--, (byte)(int)(value >>> 42L & 0x7FL | 0x80L));
      UnsafeUtil.putByte(this.buffer, this.pos--, (byte)(int)(value >>> 35L & 0x7FL | 0x80L));
      UnsafeUtil.putByte(this.buffer, this.pos--, (byte)(int)(value >>> 28L & 0x7FL | 0x80L));
      UnsafeUtil.putByte(this.buffer, this.pos--, (byte)(int)(value >>> 21L & 0x7FL | 0x80L));
      UnsafeUtil.putByte(this.buffer, this.pos--, (byte)(int)(value >>> 14L & 0x7FL | 0x80L));
      UnsafeUtil.putByte(this.buffer, this.pos--, (byte)(int)(value >>> 7L & 0x7FL | 0x80L));
      UnsafeUtil.putByte(this.buffer, this.pos--, (byte)(int)(value & 0x7FL | 0x80L));
    }
    
    private void writeVarint64NineBytes(long value) {
      UnsafeUtil.putByte(this.buffer, this.pos--, (byte)(int)(value >>> 56L));
      UnsafeUtil.putByte(this.buffer, this.pos--, (byte)(int)(value >>> 49L & 0x7FL | 0x80L));
      UnsafeUtil.putByte(this.buffer, this.pos--, (byte)(int)(value >>> 42L & 0x7FL | 0x80L));
      UnsafeUtil.putByte(this.buffer, this.pos--, (byte)(int)(value >>> 35L & 0x7FL | 0x80L));
      UnsafeUtil.putByte(this.buffer, this.pos--, (byte)(int)(value >>> 28L & 0x7FL | 0x80L));
      UnsafeUtil.putByte(this.buffer, this.pos--, (byte)(int)(value >>> 21L & 0x7FL | 0x80L));
      UnsafeUtil.putByte(this.buffer, this.pos--, (byte)(int)(value >>> 14L & 0x7FL | 0x80L));
      UnsafeUtil.putByte(this.buffer, this.pos--, (byte)(int)(value >>> 7L & 0x7FL | 0x80L));
      UnsafeUtil.putByte(this.buffer, this.pos--, (byte)(int)(value & 0x7FL | 0x80L));
    }
    
    private void writeVarint64TenBytes(long value) {
      UnsafeUtil.putByte(this.buffer, this.pos--, (byte)(int)(value >>> 63L));
      UnsafeUtil.putByte(this.buffer, this.pos--, (byte)(int)(value >>> 56L & 0x7FL | 0x80L));
      UnsafeUtil.putByte(this.buffer, this.pos--, (byte)(int)(value >>> 49L & 0x7FL | 0x80L));
      UnsafeUtil.putByte(this.buffer, this.pos--, (byte)(int)(value >>> 42L & 0x7FL | 0x80L));
      UnsafeUtil.putByte(this.buffer, this.pos--, (byte)(int)(value >>> 35L & 0x7FL | 0x80L));
      UnsafeUtil.putByte(this.buffer, this.pos--, (byte)(int)(value >>> 28L & 0x7FL | 0x80L));
      UnsafeUtil.putByte(this.buffer, this.pos--, (byte)(int)(value >>> 21L & 0x7FL | 0x80L));
      UnsafeUtil.putByte(this.buffer, this.pos--, (byte)(int)(value >>> 14L & 0x7FL | 0x80L));
      UnsafeUtil.putByte(this.buffer, this.pos--, (byte)(int)(value >>> 7L & 0x7FL | 0x80L));
      UnsafeUtil.putByte(this.buffer, this.pos--, (byte)(int)(value & 0x7FL | 0x80L));
    }
    
    void writeFixed32(int value) {
      UnsafeUtil.putByte(this.buffer, this.pos--, (byte)(value >> 24 & 0xFF));
      UnsafeUtil.putByte(this.buffer, this.pos--, (byte)(value >> 16 & 0xFF));
      UnsafeUtil.putByte(this.buffer, this.pos--, (byte)(value >> 8 & 0xFF));
      UnsafeUtil.putByte(this.buffer, this.pos--, (byte)(value & 0xFF));
    }
    
    void writeFixed64(long value) {
      UnsafeUtil.putByte(this.buffer, this.pos--, (byte)((int)(value >> 56L) & 0xFF));
      UnsafeUtil.putByte(this.buffer, this.pos--, (byte)((int)(value >> 48L) & 0xFF));
      UnsafeUtil.putByte(this.buffer, this.pos--, (byte)((int)(value >> 40L) & 0xFF));
      UnsafeUtil.putByte(this.buffer, this.pos--, (byte)((int)(value >> 32L) & 0xFF));
      UnsafeUtil.putByte(this.buffer, this.pos--, (byte)((int)(value >> 24L) & 0xFF));
      UnsafeUtil.putByte(this.buffer, this.pos--, (byte)((int)(value >> 16L) & 0xFF));
      UnsafeUtil.putByte(this.buffer, this.pos--, (byte)((int)(value >> 8L) & 0xFF));
      UnsafeUtil.putByte(this.buffer, this.pos--, (byte)((int)value & 0xFF));
    }
    
    void writeString(String in) {
      requireSpace(in.length());
      int i = in.length() - 1;
      char c;
      for (; i >= 0 && (c = in.charAt(i)) < ''; i--)
        UnsafeUtil.putByte(this.buffer, this.pos--, (byte)c); 
      if (i == -1)
        return; 
      for (; i >= 0; i--) {
        c = in.charAt(i);
        UnsafeUtil.putByte(this.buffer, this.pos--, (byte)c);
        UnsafeUtil.putByte(this.buffer, this.pos--, (byte)(0x80 | 0x3F & c));
        UnsafeUtil.putByte(this.buffer, this.pos--, (byte)(0x3C0 | c >>> 6));
        UnsafeUtil.putByte(this.buffer, this.pos--, (byte)(0x80 | 0x3F & c));
        UnsafeUtil.putByte(this.buffer, this.pos--, (byte)(0x80 | 0x3F & c >>> 6));
        UnsafeUtil.putByte(this.buffer, this.pos--, (byte)(0x1E0 | c >>> 12));
        char high;
        if (i == 0 || !Character.isSurrogatePair(high = in.charAt(i - 1), c))
          throw new Utf8.UnpairedSurrogateException(i - 1, i); 
        i--;
        int codePoint = Character.toCodePoint(high, c);
        UnsafeUtil.putByte(this.buffer, this.pos--, (byte)(0x80 | 0x3F & codePoint));
        UnsafeUtil.putByte(this.buffer, this.pos--, (byte)(0x80 | 0x3F & codePoint >>> 6));
        UnsafeUtil.putByte(this.buffer, this.pos--, (byte)(0x80 | 0x3F & codePoint >>> 12));
        UnsafeUtil.putByte(this.buffer, this.pos--, (byte)(0xF0 | codePoint >>> 18));
        requireSpace(i);
        i++;
      } 
    }
    
    public void write(byte value) {
      UnsafeUtil.putByte(this.buffer, this.pos--, value);
    }
    
    public void write(byte[] value, int offset, int length) {
      if (offset < 0 || offset + length > value.length)
        throw new ArrayIndexOutOfBoundsException(
            String.format("value.length=%d, offset=%d, length=%d", new Object[] { Integer.valueOf(value.length), Integer.valueOf(offset), Integer.valueOf(length) })); 
      requireSpace(length);
      this.pos -= length;
      System.arraycopy(value, offset, this.buffer, arrayPos() + 1, length);
    }
    
    public void writeLazy(byte[] value, int offset, int length) {
      if (offset < 0 || offset + length > value.length)
        throw new ArrayIndexOutOfBoundsException(
            String.format("value.length=%d, offset=%d, length=%d", new Object[] { Integer.valueOf(value.length), Integer.valueOf(offset), Integer.valueOf(length) })); 
      if (spaceLeft() < length) {
        this.totalDoneBytes += length;
        this.buffers.addFirst(AllocatedBuffer.wrap(value, offset, length));
        nextBuffer();
        return;
      } 
      this.pos -= length;
      System.arraycopy(value, offset, this.buffer, arrayPos() + 1, length);
    }
    
    public void write(ByteBuffer value) {
      int length = value.remaining();
      requireSpace(length);
      this.pos -= length;
      value.get(this.buffer, arrayPos() + 1, length);
    }
    
    public void writeLazy(ByteBuffer value) {
      int length = value.remaining();
      if (spaceLeft() < length) {
        this.totalDoneBytes += length;
        this.buffers.addFirst(AllocatedBuffer.wrap(value));
        nextBuffer();
      } 
      this.pos -= length;
      value.get(this.buffer, arrayPos() + 1, length);
    }
    
    void requireSpace(int size) {
      if (spaceLeft() < size)
        nextBuffer(size); 
    }
  }
  
  private static final class SafeDirectWriter extends BinaryWriter {
    private ByteBuffer buffer;
    
    private int limitMinusOne;
    
    private int pos;
    
    SafeDirectWriter(BufferAllocator alloc, int chunkSize) {
      super(alloc, chunkSize);
      nextBuffer();
    }
    
    private void nextBuffer() {
      nextBuffer(newDirectBuffer());
    }
    
    private void nextBuffer(int capacity) {
      nextBuffer(newDirectBuffer(capacity));
    }
    
    private void nextBuffer(AllocatedBuffer allocatedBuffer) {
      if (!allocatedBuffer.hasNioBuffer())
        throw new RuntimeException("Allocated buffer does not have NIO buffer"); 
      ByteBuffer nioBuffer = allocatedBuffer.nioBuffer();
      if (!nioBuffer.isDirect())
        throw new RuntimeException("Allocator returned non-direct buffer"); 
      finishCurrentBuffer();
      this.buffers.addFirst(allocatedBuffer);
      this.buffer = nioBuffer;
      this.buffer.limit(this.buffer.capacity());
      this.buffer.position(0);
      this.buffer.order(ByteOrder.LITTLE_ENDIAN);
      this.limitMinusOne = this.buffer.limit() - 1;
      this.pos = this.limitMinusOne;
    }
    
    public int getTotalBytesWritten() {
      return this.totalDoneBytes + bytesWrittenToCurrentBuffer();
    }
    
    private int bytesWrittenToCurrentBuffer() {
      return this.limitMinusOne - this.pos;
    }
    
    private int spaceLeft() {
      return this.pos + 1;
    }
    
    void finishCurrentBuffer() {
      if (this.buffer != null) {
        this.totalDoneBytes += bytesWrittenToCurrentBuffer();
        this.buffer.position(this.pos + 1);
        this.buffer = null;
        this.pos = 0;
        this.limitMinusOne = 0;
      } 
    }
    
    public void writeUInt32(int fieldNumber, int value) {
      requireSpace(10);
      writeVarint32(value);
      writeTag(fieldNumber, 0);
    }
    
    public void writeInt32(int fieldNumber, int value) {
      requireSpace(15);
      writeInt32(value);
      writeTag(fieldNumber, 0);
    }
    
    public void writeSInt32(int fieldNumber, int value) {
      requireSpace(10);
      writeSInt32(value);
      writeTag(fieldNumber, 0);
    }
    
    public void writeFixed32(int fieldNumber, int value) {
      requireSpace(9);
      writeFixed32(value);
      writeTag(fieldNumber, 5);
    }
    
    public void writeUInt64(int fieldNumber, long value) {
      requireSpace(15);
      writeVarint64(value);
      writeTag(fieldNumber, 0);
    }
    
    public void writeSInt64(int fieldNumber, long value) {
      requireSpace(15);
      writeSInt64(value);
      writeTag(fieldNumber, 0);
    }
    
    public void writeFixed64(int fieldNumber, long value) {
      requireSpace(13);
      writeFixed64(value);
      writeTag(fieldNumber, 1);
    }
    
    public void writeBool(int fieldNumber, boolean value) {
      requireSpace(6);
      write((byte)(value ? 1 : 0));
      writeTag(fieldNumber, 0);
    }
    
    public void writeString(int fieldNumber, String value) {
      int prevBytes = getTotalBytesWritten();
      writeString(value);
      int length = getTotalBytesWritten() - prevBytes;
      requireSpace(10);
      writeVarint32(length);
      writeTag(fieldNumber, 2);
    }
    
    public void writeBytes(int fieldNumber, ByteString value) {
      try {
        value.writeToReverse(this);
      } catch (IOException e) {
        throw new RuntimeException(e);
      } 
      requireSpace(10);
      writeVarint32(value.size());
      writeTag(fieldNumber, 2);
    }
    
    public void writeMessage(int fieldNumber, Object value) throws IOException {
      int prevBytes = getTotalBytesWritten();
      Protobuf.getInstance().writeTo(value, this);
      int length = getTotalBytesWritten() - prevBytes;
      requireSpace(10);
      writeVarint32(length);
      writeTag(fieldNumber, 2);
    }
    
    public void writeMessage(int fieldNumber, Object value, Schema<Object> schema) throws IOException {
      int prevBytes = getTotalBytesWritten();
      schema.writeTo(value, this);
      int length = getTotalBytesWritten() - prevBytes;
      requireSpace(10);
      writeVarint32(length);
      writeTag(fieldNumber, 2);
    }
    
    @Deprecated
    public void writeGroup(int fieldNumber, Object value) throws IOException {
      writeTag(fieldNumber, 4);
      Protobuf.getInstance().writeTo(value, this);
      writeTag(fieldNumber, 3);
    }
    
    public void writeGroup(int fieldNumber, Object value, Schema<Object> schema) throws IOException {
      writeTag(fieldNumber, 4);
      schema.writeTo(value, this);
      writeTag(fieldNumber, 3);
    }
    
    @Deprecated
    public void writeStartGroup(int fieldNumber) {
      writeTag(fieldNumber, 3);
    }
    
    @Deprecated
    public void writeEndGroup(int fieldNumber) {
      writeTag(fieldNumber, 4);
    }
    
    void writeInt32(int value) {
      if (value >= 0) {
        writeVarint32(value);
      } else {
        writeVarint64(value);
      } 
    }
    
    void writeSInt32(int value) {
      writeVarint32(CodedOutputStream.encodeZigZag32(value));
    }
    
    void writeSInt64(long value) {
      writeVarint64(CodedOutputStream.encodeZigZag64(value));
    }
    
    void writeBool(boolean value) {
      write((byte)(value ? 1 : 0));
    }
    
    void writeTag(int fieldNumber, int wireType) {
      writeVarint32(WireFormat.makeTag(fieldNumber, wireType));
    }
    
    void writeVarint32(int value) {
      if ((value & 0xFFFFFF80) == 0) {
        writeVarint32OneByte(value);
      } else if ((value & 0xFFFFC000) == 0) {
        writeVarint32TwoBytes(value);
      } else if ((value & 0xFFE00000) == 0) {
        writeVarint32ThreeBytes(value);
      } else if ((value & 0xF0000000) == 0) {
        writeVarint32FourBytes(value);
      } else {
        writeVarint32FiveBytes(value);
      } 
    }
    
    private void writeVarint32OneByte(int value) {
      this.buffer.put(this.pos--, (byte)value);
    }
    
    private void writeVarint32TwoBytes(int value) {
      this.pos -= 2;
      this.buffer.putShort(this.pos + 1, (short)((value & 0x3F80) << 1 | value & 0x7F | 0x80));
    }
    
    private void writeVarint32ThreeBytes(int value) {
      this.pos -= 3;
      this.buffer.putInt(this.pos, (value & 0x1FC000) << 10 | (value & 0x3F80 | 0x4000) << 9 | (value & 0x7F | 0x80) << 8);
    }
    
    private void writeVarint32FourBytes(int value) {
      this.pos -= 4;
      this.buffer.putInt(this.pos + 1, (value & 0xFE00000) << 3 | (value & 0x1FC000 | 0x200000) << 2 | (value & 0x3F80 | 0x4000) << 1 | value & 0x7F | 0x80);
    }
    
    private void writeVarint32FiveBytes(int value) {
      this.buffer.put(this.pos--, (byte)(value >>> 28));
      this.pos -= 4;
      this.buffer.putInt(this.pos + 1, (value >>> 21 & 0x7F | 0x80) << 24 | (value >>> 14 & 0x7F | 0x80) << 16 | (value >>> 7 & 0x7F | 0x80) << 8 | value & 0x7F | 0x80);
    }
    
    void writeVarint64(long value) {
      switch (BinaryWriter.computeUInt64SizeNoTag(value)) {
        case 1:
          writeVarint64OneByte(value);
          break;
        case 2:
          writeVarint64TwoBytes(value);
          break;
        case 3:
          writeVarint64ThreeBytes(value);
          break;
        case 4:
          writeVarint64FourBytes(value);
          break;
        case 5:
          writeVarint64FiveBytes(value);
          break;
        case 6:
          writeVarint64SixBytes(value);
          break;
        case 7:
          writeVarint64SevenBytes(value);
          break;
        case 8:
          writeVarint64EightBytes(value);
          break;
        case 9:
          writeVarint64NineBytes(value);
          break;
        case 10:
          writeVarint64TenBytes(value);
          break;
      } 
    }
    
    private void writeVarint64OneByte(long value) {
      writeVarint32OneByte((int)value);
    }
    
    private void writeVarint64TwoBytes(long value) {
      writeVarint32TwoBytes((int)value);
    }
    
    private void writeVarint64ThreeBytes(long value) {
      writeVarint32ThreeBytes((int)value);
    }
    
    private void writeVarint64FourBytes(long value) {
      writeVarint32FourBytes((int)value);
    }
    
    private void writeVarint64FiveBytes(long value) {
      this.pos -= 5;
      this.buffer.putLong(this.pos - 2, (value & 0x7F0000000L) << 28L | (value & 0xFE00000L | 0x10000000L) << 27L | (value & 0x1FC000L | 0x200000L) << 26L | (value & 0x3F80L | 0x4000L) << 25L | (value & 0x7FL | 0x80L) << 24L);
    }
    
    private void writeVarint64SixBytes(long value) {
      this.pos -= 6;
      this.buffer.putLong(this.pos - 1, (value & 0x3F800000000L) << 21L | (value & 0x7F0000000L | 0x800000000L) << 20L | (value & 0xFE00000L | 0x10000000L) << 19L | (value & 0x1FC000L | 0x200000L) << 18L | (value & 0x3F80L | 0x4000L) << 17L | (value & 0x7FL | 0x80L) << 16L);
    }
    
    private void writeVarint64SevenBytes(long value) {
      this.pos -= 7;
      this.buffer.putLong(this.pos, (value & 0x1FC0000000000L) << 14L | (value & 0x3F800000000L | 0x40000000000L) << 13L | (value & 0x7F0000000L | 0x800000000L) << 12L | (value & 0xFE00000L | 0x10000000L) << 11L | (value & 0x1FC000L | 0x200000L) << 10L | (value & 0x3F80L | 0x4000L) << 9L | (value & 0x7FL | 0x80L) << 8L);
    }
    
    private void writeVarint64EightBytes(long value) {
      this.pos -= 8;
      this.buffer.putLong(this.pos + 1, (value & 0xFE000000000000L) << 7L | (value & 0x1FC0000000000L | 0x2000000000000L) << 6L | (value & 0x3F800000000L | 0x40000000000L) << 5L | (value & 0x7F0000000L | 0x800000000L) << 4L | (value & 0xFE00000L | 0x10000000L) << 3L | (value & 0x1FC000L | 0x200000L) << 2L | (value & 0x3F80L | 0x4000L) << 1L | value & 0x7FL | 0x80L);
    }
    
    private void writeVarint64EightBytesWithSign(long value) {
      this.pos -= 8;
      this.buffer.putLong(this.pos + 1, (value & 0xFE000000000000L | 0x100000000000000L) << 7L | (value & 0x1FC0000000000L | 0x2000000000000L) << 6L | (value & 0x3F800000000L | 0x40000000000L) << 5L | (value & 0x7F0000000L | 0x800000000L) << 4L | (value & 0xFE00000L | 0x10000000L) << 3L | (value & 0x1FC000L | 0x200000L) << 2L | (value & 0x3F80L | 0x4000L) << 1L | value & 0x7FL | 0x80L);
    }
    
    private void writeVarint64NineBytes(long value) {
      this.buffer.put(this.pos--, (byte)(int)(value >>> 56L));
      writeVarint64EightBytesWithSign(value & 0xFFFFFFFFFFFFFFL);
    }
    
    private void writeVarint64TenBytes(long value) {
      this.buffer.put(this.pos--, (byte)(int)(value >>> 63L));
      this.buffer.put(this.pos--, (byte)(int)(value >>> 56L & 0x7FL | 0x80L));
      writeVarint64EightBytesWithSign(value & 0xFFFFFFFFFFFFFFL);
    }
    
    void writeFixed32(int value) {
      this.pos -= 4;
      this.buffer.putInt(this.pos + 1, value);
    }
    
    void writeFixed64(long value) {
      this.pos -= 8;
      this.buffer.putLong(this.pos + 1, value);
    }
    
    void writeString(String in) {
      requireSpace(in.length());
      int i = in.length() - 1;
      this.pos -= i;
      char c;
      for (; i >= 0 && (c = in.charAt(i)) < ''; i--)
        this.buffer.put(this.pos + i, (byte)c); 
      if (i == -1) {
        this.pos--;
        return;
      } 
      this.pos += i;
      for (; i >= 0; i--) {
        c = in.charAt(i);
        if (c < '' && this.pos >= 0) {
          this.buffer.put(this.pos--, (byte)c);
        } else if (c < 'ࠀ' && this.pos > 0) {
          this.buffer.put(this.pos--, (byte)(0x80 | 0x3F & c));
          this.buffer.put(this.pos--, (byte)(0x3C0 | c >>> 6));
        } else if ((c < '?' || '?' < c) && this.pos > 1) {
          this.buffer.put(this.pos--, (byte)(0x80 | 0x3F & c));
          this.buffer.put(this.pos--, (byte)(0x80 | 0x3F & c >>> 6));
          this.buffer.put(this.pos--, (byte)(0x1E0 | c >>> 12));
        } else if (this.pos > 2) {
          char high = Character.MIN_VALUE;
          if (i == 0 || !Character.isSurrogatePair(high = in.charAt(i - 1), c))
            throw new Utf8.UnpairedSurrogateException(i - 1, i); 
          i--;
          int codePoint = Character.toCodePoint(high, c);
          this.buffer.put(this.pos--, (byte)(0x80 | 0x3F & codePoint));
          this.buffer.put(this.pos--, (byte)(0x80 | 0x3F & codePoint >>> 6));
          this.buffer.put(this.pos--, (byte)(0x80 | 0x3F & codePoint >>> 12));
          this.buffer.put(this.pos--, (byte)(0xF0 | codePoint >>> 18));
        } else {
          requireSpace(i);
          i++;
        } 
      } 
    }
    
    public void write(byte value) {
      this.buffer.put(this.pos--, value);
    }
    
    public void write(byte[] value, int offset, int length) {
      if (spaceLeft() < length)
        nextBuffer(length); 
      this.pos -= length;
      this.buffer.position(this.pos + 1);
      this.buffer.put(value, offset, length);
    }
    
    public void writeLazy(byte[] value, int offset, int length) {
      if (spaceLeft() < length) {
        this.totalDoneBytes += length;
        this.buffers.addFirst(AllocatedBuffer.wrap(value, offset, length));
        nextBuffer();
        return;
      } 
      this.pos -= length;
      this.buffer.position(this.pos + 1);
      this.buffer.put(value, offset, length);
    }
    
    public void write(ByteBuffer value) {
      int length = value.remaining();
      if (spaceLeft() < length)
        nextBuffer(length); 
      this.pos -= length;
      this.buffer.position(this.pos + 1);
      this.buffer.put(value);
    }
    
    public void writeLazy(ByteBuffer value) {
      int length = value.remaining();
      if (spaceLeft() < length) {
        this.totalDoneBytes += length;
        this.buffers.addFirst(AllocatedBuffer.wrap(value));
        nextBuffer();
        return;
      } 
      this.pos -= length;
      this.buffer.position(this.pos + 1);
      this.buffer.put(value);
    }
    
    void requireSpace(int size) {
      if (spaceLeft() < size)
        nextBuffer(size); 
    }
  }
  
  private static final class UnsafeDirectWriter extends BinaryWriter {
    private ByteBuffer buffer;
    
    private long bufferOffset;
    
    private long limitMinusOne;
    
    private long pos;
    
    UnsafeDirectWriter(BufferAllocator alloc, int chunkSize) {
      super(alloc, chunkSize);
      nextBuffer();
    }
    
    private static boolean isSupported() {
      return UnsafeUtil.hasUnsafeByteBufferOperations();
    }
    
    private void nextBuffer() {
      nextBuffer(newDirectBuffer());
    }
    
    private void nextBuffer(int capacity) {
      nextBuffer(newDirectBuffer(capacity));
    }
    
    private void nextBuffer(AllocatedBuffer allocatedBuffer) {
      if (!allocatedBuffer.hasNioBuffer())
        throw new RuntimeException("Allocated buffer does not have NIO buffer"); 
      ByteBuffer nioBuffer = allocatedBuffer.nioBuffer();
      if (!nioBuffer.isDirect())
        throw new RuntimeException("Allocator returned non-direct buffer"); 
      finishCurrentBuffer();
      this.buffers.addFirst(allocatedBuffer);
      this.buffer = nioBuffer;
      this.buffer.limit(this.buffer.capacity());
      this.buffer.position(0);
      this.bufferOffset = UnsafeUtil.addressOffset(this.buffer);
      this.limitMinusOne = this.bufferOffset + (this.buffer.limit() - 1);
      this.pos = this.limitMinusOne;
    }
    
    public int getTotalBytesWritten() {
      return this.totalDoneBytes + bytesWrittenToCurrentBuffer();
    }
    
    private int bytesWrittenToCurrentBuffer() {
      return (int)(this.limitMinusOne - this.pos);
    }
    
    private int spaceLeft() {
      return bufferPos() + 1;
    }
    
    void finishCurrentBuffer() {
      if (this.buffer != null) {
        this.totalDoneBytes += bytesWrittenToCurrentBuffer();
        this.buffer.position(bufferPos() + 1);
        this.buffer = null;
        this.pos = 0L;
        this.limitMinusOne = 0L;
      } 
    }
    
    private int bufferPos() {
      return (int)(this.pos - this.bufferOffset);
    }
    
    public void writeUInt32(int fieldNumber, int value) {
      requireSpace(10);
      writeVarint32(value);
      writeTag(fieldNumber, 0);
    }
    
    public void writeInt32(int fieldNumber, int value) {
      requireSpace(15);
      writeInt32(value);
      writeTag(fieldNumber, 0);
    }
    
    public void writeSInt32(int fieldNumber, int value) {
      requireSpace(10);
      writeSInt32(value);
      writeTag(fieldNumber, 0);
    }
    
    public void writeFixed32(int fieldNumber, int value) {
      requireSpace(9);
      writeFixed32(value);
      writeTag(fieldNumber, 5);
    }
    
    public void writeUInt64(int fieldNumber, long value) {
      requireSpace(15);
      writeVarint64(value);
      writeTag(fieldNumber, 0);
    }
    
    public void writeSInt64(int fieldNumber, long value) {
      requireSpace(15);
      writeSInt64(value);
      writeTag(fieldNumber, 0);
    }
    
    public void writeFixed64(int fieldNumber, long value) {
      requireSpace(13);
      writeFixed64(value);
      writeTag(fieldNumber, 1);
    }
    
    public void writeBool(int fieldNumber, boolean value) {
      requireSpace(6);
      write((byte)(value ? 1 : 0));
      writeTag(fieldNumber, 0);
    }
    
    public void writeString(int fieldNumber, String value) {
      int prevBytes = getTotalBytesWritten();
      writeString(value);
      int length = getTotalBytesWritten() - prevBytes;
      requireSpace(10);
      writeVarint32(length);
      writeTag(fieldNumber, 2);
    }
    
    public void writeBytes(int fieldNumber, ByteString value) {
      try {
        value.writeToReverse(this);
      } catch (IOException e) {
        throw new RuntimeException(e);
      } 
      requireSpace(10);
      writeVarint32(value.size());
      writeTag(fieldNumber, 2);
    }
    
    public void writeMessage(int fieldNumber, Object value) throws IOException {
      int prevBytes = getTotalBytesWritten();
      Protobuf.getInstance().writeTo(value, this);
      int length = getTotalBytesWritten() - prevBytes;
      requireSpace(10);
      writeVarint32(length);
      writeTag(fieldNumber, 2);
    }
    
    public void writeMessage(int fieldNumber, Object value, Schema<Object> schema) throws IOException {
      int prevBytes = getTotalBytesWritten();
      schema.writeTo(value, this);
      int length = getTotalBytesWritten() - prevBytes;
      requireSpace(10);
      writeVarint32(length);
      writeTag(fieldNumber, 2);
    }
    
    public void writeGroup(int fieldNumber, Object value) throws IOException {
      writeTag(fieldNumber, 4);
      Protobuf.getInstance().writeTo(value, this);
      writeTag(fieldNumber, 3);
    }
    
    public void writeGroup(int fieldNumber, Object value, Schema<Object> schema) throws IOException {
      writeTag(fieldNumber, 4);
      schema.writeTo(value, this);
      writeTag(fieldNumber, 3);
    }
    
    @Deprecated
    public void writeStartGroup(int fieldNumber) {
      writeTag(fieldNumber, 3);
    }
    
    @Deprecated
    public void writeEndGroup(int fieldNumber) {
      writeTag(fieldNumber, 4);
    }
    
    void writeInt32(int value) {
      if (value >= 0) {
        writeVarint32(value);
      } else {
        writeVarint64(value);
      } 
    }
    
    void writeSInt32(int value) {
      writeVarint32(CodedOutputStream.encodeZigZag32(value));
    }
    
    void writeSInt64(long value) {
      writeVarint64(CodedOutputStream.encodeZigZag64(value));
    }
    
    void writeBool(boolean value) {
      write((byte)(value ? 1 : 0));
    }
    
    void writeTag(int fieldNumber, int wireType) {
      writeVarint32(WireFormat.makeTag(fieldNumber, wireType));
    }
    
    void writeVarint32(int value) {
      if ((value & 0xFFFFFF80) == 0) {
        writeVarint32OneByte(value);
      } else if ((value & 0xFFFFC000) == 0) {
        writeVarint32TwoBytes(value);
      } else if ((value & 0xFFE00000) == 0) {
        writeVarint32ThreeBytes(value);
      } else if ((value & 0xF0000000) == 0) {
        writeVarint32FourBytes(value);
      } else {
        writeVarint32FiveBytes(value);
      } 
    }
    
    private void writeVarint32OneByte(int value) {
      UnsafeUtil.putByte(this.pos--, (byte)value);
    }
    
    private void writeVarint32TwoBytes(int value) {
      UnsafeUtil.putByte(this.pos--, (byte)(value >>> 7));
      UnsafeUtil.putByte(this.pos--, (byte)(value & 0x7F | 0x80));
    }
    
    private void writeVarint32ThreeBytes(int value) {
      UnsafeUtil.putByte(this.pos--, (byte)(value >>> 14));
      UnsafeUtil.putByte(this.pos--, (byte)(value >>> 7 & 0x7F | 0x80));
      UnsafeUtil.putByte(this.pos--, (byte)(value & 0x7F | 0x80));
    }
    
    private void writeVarint32FourBytes(int value) {
      UnsafeUtil.putByte(this.pos--, (byte)(value >>> 21));
      UnsafeUtil.putByte(this.pos--, (byte)(value >>> 14 & 0x7F | 0x80));
      UnsafeUtil.putByte(this.pos--, (byte)(value >>> 7 & 0x7F | 0x80));
      UnsafeUtil.putByte(this.pos--, (byte)(value & 0x7F | 0x80));
    }
    
    private void writeVarint32FiveBytes(int value) {
      UnsafeUtil.putByte(this.pos--, (byte)(value >>> 28));
      UnsafeUtil.putByte(this.pos--, (byte)(value >>> 21 & 0x7F | 0x80));
      UnsafeUtil.putByte(this.pos--, (byte)(value >>> 14 & 0x7F | 0x80));
      UnsafeUtil.putByte(this.pos--, (byte)(value >>> 7 & 0x7F | 0x80));
      UnsafeUtil.putByte(this.pos--, (byte)(value & 0x7F | 0x80));
    }
    
    void writeVarint64(long value) {
      switch (BinaryWriter.computeUInt64SizeNoTag(value)) {
        case 1:
          writeVarint64OneByte(value);
          break;
        case 2:
          writeVarint64TwoBytes(value);
          break;
        case 3:
          writeVarint64ThreeBytes(value);
          break;
        case 4:
          writeVarint64FourBytes(value);
          break;
        case 5:
          writeVarint64FiveBytes(value);
          break;
        case 6:
          writeVarint64SixBytes(value);
          break;
        case 7:
          writeVarint64SevenBytes(value);
          break;
        case 8:
          writeVarint64EightBytes(value);
          break;
        case 9:
          writeVarint64NineBytes(value);
          break;
        case 10:
          writeVarint64TenBytes(value);
          break;
      } 
    }
    
    private void writeVarint64OneByte(long value) {
      UnsafeUtil.putByte(this.pos--, (byte)(int)value);
    }
    
    private void writeVarint64TwoBytes(long value) {
      UnsafeUtil.putByte(this.pos--, (byte)(int)(value >>> 7L));
      UnsafeUtil.putByte(this.pos--, (byte)((int)value & 0x7F | 0x80));
    }
    
    private void writeVarint64ThreeBytes(long value) {
      UnsafeUtil.putByte(this.pos--, (byte)((int)value >>> 14));
      UnsafeUtil.putByte(this.pos--, (byte)(int)(value >>> 7L & 0x7FL | 0x80L));
      UnsafeUtil.putByte(this.pos--, (byte)(int)(value & 0x7FL | 0x80L));
    }
    
    private void writeVarint64FourBytes(long value) {
      UnsafeUtil.putByte(this.pos--, (byte)(int)(value >>> 21L));
      UnsafeUtil.putByte(this.pos--, (byte)(int)(value >>> 14L & 0x7FL | 0x80L));
      UnsafeUtil.putByte(this.pos--, (byte)(int)(value >>> 7L & 0x7FL | 0x80L));
      UnsafeUtil.putByte(this.pos--, (byte)(int)(value & 0x7FL | 0x80L));
    }
    
    private void writeVarint64FiveBytes(long value) {
      UnsafeUtil.putByte(this.pos--, (byte)(int)(value >>> 28L));
      UnsafeUtil.putByte(this.pos--, (byte)(int)(value >>> 21L & 0x7FL | 0x80L));
      UnsafeUtil.putByte(this.pos--, (byte)(int)(value >>> 14L & 0x7FL | 0x80L));
      UnsafeUtil.putByte(this.pos--, (byte)(int)(value >>> 7L & 0x7FL | 0x80L));
      UnsafeUtil.putByte(this.pos--, (byte)(int)(value & 0x7FL | 0x80L));
    }
    
    private void writeVarint64SixBytes(long value) {
      UnsafeUtil.putByte(this.pos--, (byte)(int)(value >>> 35L));
      UnsafeUtil.putByte(this.pos--, (byte)(int)(value >>> 28L & 0x7FL | 0x80L));
      UnsafeUtil.putByte(this.pos--, (byte)(int)(value >>> 21L & 0x7FL | 0x80L));
      UnsafeUtil.putByte(this.pos--, (byte)(int)(value >>> 14L & 0x7FL | 0x80L));
      UnsafeUtil.putByte(this.pos--, (byte)(int)(value >>> 7L & 0x7FL | 0x80L));
      UnsafeUtil.putByte(this.pos--, (byte)(int)(value & 0x7FL | 0x80L));
    }
    
    private void writeVarint64SevenBytes(long value) {
      UnsafeUtil.putByte(this.pos--, (byte)(int)(value >>> 42L));
      UnsafeUtil.putByte(this.pos--, (byte)(int)(value >>> 35L & 0x7FL | 0x80L));
      UnsafeUtil.putByte(this.pos--, (byte)(int)(value >>> 28L & 0x7FL | 0x80L));
      UnsafeUtil.putByte(this.pos--, (byte)(int)(value >>> 21L & 0x7FL | 0x80L));
      UnsafeUtil.putByte(this.pos--, (byte)(int)(value >>> 14L & 0x7FL | 0x80L));
      UnsafeUtil.putByte(this.pos--, (byte)(int)(value >>> 7L & 0x7FL | 0x80L));
      UnsafeUtil.putByte(this.pos--, (byte)(int)(value & 0x7FL | 0x80L));
    }
    
    private void writeVarint64EightBytes(long value) {
      UnsafeUtil.putByte(this.pos--, (byte)(int)(value >>> 49L));
      UnsafeUtil.putByte(this.pos--, (byte)(int)(value >>> 42L & 0x7FL | 0x80L));
      UnsafeUtil.putByte(this.pos--, (byte)(int)(value >>> 35L & 0x7FL | 0x80L));
      UnsafeUtil.putByte(this.pos--, (byte)(int)(value >>> 28L & 0x7FL | 0x80L));
      UnsafeUtil.putByte(this.pos--, (byte)(int)(value >>> 21L & 0x7FL | 0x80L));
      UnsafeUtil.putByte(this.pos--, (byte)(int)(value >>> 14L & 0x7FL | 0x80L));
      UnsafeUtil.putByte(this.pos--, (byte)(int)(value >>> 7L & 0x7FL | 0x80L));
      UnsafeUtil.putByte(this.pos--, (byte)(int)(value & 0x7FL | 0x80L));
    }
    
    private void writeVarint64NineBytes(long value) {
      UnsafeUtil.putByte(this.pos--, (byte)(int)(value >>> 56L));
      UnsafeUtil.putByte(this.pos--, (byte)(int)(value >>> 49L & 0x7FL | 0x80L));
      UnsafeUtil.putByte(this.pos--, (byte)(int)(value >>> 42L & 0x7FL | 0x80L));
      UnsafeUtil.putByte(this.pos--, (byte)(int)(value >>> 35L & 0x7FL | 0x80L));
      UnsafeUtil.putByte(this.pos--, (byte)(int)(value >>> 28L & 0x7FL | 0x80L));
      UnsafeUtil.putByte(this.pos--, (byte)(int)(value >>> 21L & 0x7FL | 0x80L));
      UnsafeUtil.putByte(this.pos--, (byte)(int)(value >>> 14L & 0x7FL | 0x80L));
      UnsafeUtil.putByte(this.pos--, (byte)(int)(value >>> 7L & 0x7FL | 0x80L));
      UnsafeUtil.putByte(this.pos--, (byte)(int)(value & 0x7FL | 0x80L));
    }
    
    private void writeVarint64TenBytes(long value) {
      UnsafeUtil.putByte(this.pos--, (byte)(int)(value >>> 63L));
      UnsafeUtil.putByte(this.pos--, (byte)(int)(value >>> 56L & 0x7FL | 0x80L));
      UnsafeUtil.putByte(this.pos--, (byte)(int)(value >>> 49L & 0x7FL | 0x80L));
      UnsafeUtil.putByte(this.pos--, (byte)(int)(value >>> 42L & 0x7FL | 0x80L));
      UnsafeUtil.putByte(this.pos--, (byte)(int)(value >>> 35L & 0x7FL | 0x80L));
      UnsafeUtil.putByte(this.pos--, (byte)(int)(value >>> 28L & 0x7FL | 0x80L));
      UnsafeUtil.putByte(this.pos--, (byte)(int)(value >>> 21L & 0x7FL | 0x80L));
      UnsafeUtil.putByte(this.pos--, (byte)(int)(value >>> 14L & 0x7FL | 0x80L));
      UnsafeUtil.putByte(this.pos--, (byte)(int)(value >>> 7L & 0x7FL | 0x80L));
      UnsafeUtil.putByte(this.pos--, (byte)(int)(value & 0x7FL | 0x80L));
    }
    
    void writeFixed32(int value) {
      UnsafeUtil.putByte(this.pos--, (byte)(value >> 24 & 0xFF));
      UnsafeUtil.putByte(this.pos--, (byte)(value >> 16 & 0xFF));
      UnsafeUtil.putByte(this.pos--, (byte)(value >> 8 & 0xFF));
      UnsafeUtil.putByte(this.pos--, (byte)(value & 0xFF));
    }
    
    void writeFixed64(long value) {
      UnsafeUtil.putByte(this.pos--, (byte)((int)(value >> 56L) & 0xFF));
      UnsafeUtil.putByte(this.pos--, (byte)((int)(value >> 48L) & 0xFF));
      UnsafeUtil.putByte(this.pos--, (byte)((int)(value >> 40L) & 0xFF));
      UnsafeUtil.putByte(this.pos--, (byte)((int)(value >> 32L) & 0xFF));
      UnsafeUtil.putByte(this.pos--, (byte)((int)(value >> 24L) & 0xFF));
      UnsafeUtil.putByte(this.pos--, (byte)((int)(value >> 16L) & 0xFF));
      UnsafeUtil.putByte(this.pos--, (byte)((int)(value >> 8L) & 0xFF));
      UnsafeUtil.putByte(this.pos--, (byte)((int)value & 0xFF));
    }
    
    void writeString(String in) {
      requireSpace(in.length());
      int i = in.length() - 1;
      char c;
      for (; i >= 0 && (c = in.charAt(i)) < ''; i--)
        UnsafeUtil.putByte(this.pos--, (byte)c); 
      if (i == -1)
        return; 
      for (; i >= 0; i--) {
        c = in.charAt(i);
        UnsafeUtil.putByte(this.pos--, (byte)c);
        UnsafeUtil.putByte(this.pos--, (byte)(0x80 | 0x3F & c));
        UnsafeUtil.putByte(this.pos--, (byte)(0x3C0 | c >>> 6));
        UnsafeUtil.putByte(this.pos--, (byte)(0x80 | 0x3F & c));
        UnsafeUtil.putByte(this.pos--, (byte)(0x80 | 0x3F & c >>> 6));
        UnsafeUtil.putByte(this.pos--, (byte)(0x1E0 | c >>> 12));
        char high;
        if (i == 0 || !Character.isSurrogatePair(high = in.charAt(i - 1), c))
          throw new Utf8.UnpairedSurrogateException(i - 1, i); 
        i--;
        int codePoint = Character.toCodePoint(high, c);
        UnsafeUtil.putByte(this.pos--, (byte)(0x80 | 0x3F & codePoint));
        UnsafeUtil.putByte(this.pos--, (byte)(0x80 | 0x3F & codePoint >>> 6));
        UnsafeUtil.putByte(this.pos--, (byte)(0x80 | 0x3F & codePoint >>> 12));
        UnsafeUtil.putByte(this.pos--, (byte)(0xF0 | codePoint >>> 18));
        requireSpace(i);
        i++;
      } 
    }
    
    public void write(byte value) {
      UnsafeUtil.putByte(this.pos--, value);
    }
    
    public void write(byte[] value, int offset, int length) {
      if (spaceLeft() < length)
        nextBuffer(length); 
      this.pos -= length;
      this.buffer.position(bufferPos() + 1);
      this.buffer.put(value, offset, length);
    }
    
    public void writeLazy(byte[] value, int offset, int length) {
      if (spaceLeft() < length) {
        this.totalDoneBytes += length;
        this.buffers.addFirst(AllocatedBuffer.wrap(value, offset, length));
        nextBuffer();
        return;
      } 
      this.pos -= length;
      this.buffer.position(bufferPos() + 1);
      this.buffer.put(value, offset, length);
    }
    
    public void write(ByteBuffer value) {
      int length = value.remaining();
      if (spaceLeft() < length)
        nextBuffer(length); 
      this.pos -= length;
      this.buffer.position(bufferPos() + 1);
      this.buffer.put(value);
    }
    
    public void writeLazy(ByteBuffer value) {
      int length = value.remaining();
      if (spaceLeft() < length) {
        this.totalDoneBytes += length;
        this.buffers.addFirst(AllocatedBuffer.wrap(value));
        nextBuffer();
        return;
      } 
      this.pos -= length;
      this.buffer.position(bufferPos() + 1);
      this.buffer.put(value);
    }
    
    void requireSpace(int size) {
      if (spaceLeft() < size)
        nextBuffer(size); 
    }
  }
}
