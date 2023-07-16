package com.google.protobuf;

import java.io.IOException;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

@CheckReturnValue
final class CodedOutputStreamWriter implements Writer {
  private final CodedOutputStream output;
  
  public static CodedOutputStreamWriter forCodedOutput(CodedOutputStream output) {
    if (output.wrapper != null)
      return output.wrapper; 
    return new CodedOutputStreamWriter(output);
  }
  
  private CodedOutputStreamWriter(CodedOutputStream output) {
    this.output = Internal.<CodedOutputStream>checkNotNull(output, "output");
    this.output.wrapper = this;
  }
  
  public Writer.FieldOrder fieldOrder() {
    return Writer.FieldOrder.ASCENDING;
  }
  
  public int getTotalBytesWritten() {
    return this.output.getTotalBytesWritten();
  }
  
  public void writeSFixed32(int fieldNumber, int value) throws IOException {
    this.output.writeSFixed32(fieldNumber, value);
  }
  
  public void writeInt64(int fieldNumber, long value) throws IOException {
    this.output.writeInt64(fieldNumber, value);
  }
  
  public void writeSFixed64(int fieldNumber, long value) throws IOException {
    this.output.writeSFixed64(fieldNumber, value);
  }
  
  public void writeFloat(int fieldNumber, float value) throws IOException {
    this.output.writeFloat(fieldNumber, value);
  }
  
  public void writeDouble(int fieldNumber, double value) throws IOException {
    this.output.writeDouble(fieldNumber, value);
  }
  
  public void writeEnum(int fieldNumber, int value) throws IOException {
    this.output.writeEnum(fieldNumber, value);
  }
  
  public void writeUInt64(int fieldNumber, long value) throws IOException {
    this.output.writeUInt64(fieldNumber, value);
  }
  
  public void writeInt32(int fieldNumber, int value) throws IOException {
    this.output.writeInt32(fieldNumber, value);
  }
  
  public void writeFixed64(int fieldNumber, long value) throws IOException {
    this.output.writeFixed64(fieldNumber, value);
  }
  
  public void writeFixed32(int fieldNumber, int value) throws IOException {
    this.output.writeFixed32(fieldNumber, value);
  }
  
  public void writeBool(int fieldNumber, boolean value) throws IOException {
    this.output.writeBool(fieldNumber, value);
  }
  
  public void writeString(int fieldNumber, String value) throws IOException {
    this.output.writeString(fieldNumber, value);
  }
  
  public void writeBytes(int fieldNumber, ByteString value) throws IOException {
    this.output.writeBytes(fieldNumber, value);
  }
  
  public void writeUInt32(int fieldNumber, int value) throws IOException {
    this.output.writeUInt32(fieldNumber, value);
  }
  
  public void writeSInt32(int fieldNumber, int value) throws IOException {
    this.output.writeSInt32(fieldNumber, value);
  }
  
  public void writeSInt64(int fieldNumber, long value) throws IOException {
    this.output.writeSInt64(fieldNumber, value);
  }
  
  public void writeMessage(int fieldNumber, Object value) throws IOException {
    this.output.writeMessage(fieldNumber, (MessageLite)value);
  }
  
  public void writeMessage(int fieldNumber, Object value, Schema schema) throws IOException {
    this.output.writeMessage(fieldNumber, (MessageLite)value, schema);
  }
  
  @Deprecated
  public void writeGroup(int fieldNumber, Object value) throws IOException {
    this.output.writeGroup(fieldNumber, (MessageLite)value);
  }
  
  public void writeGroup(int fieldNumber, Object value, Schema schema) throws IOException {
    this.output.writeGroup(fieldNumber, (MessageLite)value, schema);
  }
  
  @Deprecated
  public void writeStartGroup(int fieldNumber) throws IOException {
    this.output.writeTag(fieldNumber, 3);
  }
  
  @Deprecated
  public void writeEndGroup(int fieldNumber) throws IOException {
    this.output.writeTag(fieldNumber, 4);
  }
  
  public final void writeMessageSetItem(int fieldNumber, Object value) throws IOException {
    if (value instanceof ByteString) {
      this.output.writeRawMessageSetExtension(fieldNumber, (ByteString)value);
    } else {
      this.output.writeMessageSetExtension(fieldNumber, (MessageLite)value);
    } 
  }
  
  public void writeInt32List(int fieldNumber, List<Integer> value, boolean packed) throws IOException {
    if (packed) {
      this.output.writeTag(fieldNumber, 2);
      int dataSize = 0;
      int i;
      for (i = 0; i < value.size(); i++)
        dataSize += CodedOutputStream.computeInt32SizeNoTag(((Integer)value.get(i)).intValue()); 
      this.output.writeUInt32NoTag(dataSize);
      for (i = 0; i < value.size(); i++)
        this.output.writeInt32NoTag(((Integer)value.get(i)).intValue()); 
    } else {
      for (int i = 0; i < value.size(); i++)
        this.output.writeInt32(fieldNumber, ((Integer)value.get(i)).intValue()); 
    } 
  }
  
  public void writeFixed32List(int fieldNumber, List<Integer> value, boolean packed) throws IOException {
    if (packed) {
      this.output.writeTag(fieldNumber, 2);
      int dataSize = 0;
      int i;
      for (i = 0; i < value.size(); i++)
        dataSize += CodedOutputStream.computeFixed32SizeNoTag(((Integer)value.get(i)).intValue()); 
      this.output.writeUInt32NoTag(dataSize);
      for (i = 0; i < value.size(); i++)
        this.output.writeFixed32NoTag(((Integer)value.get(i)).intValue()); 
    } else {
      for (int i = 0; i < value.size(); i++)
        this.output.writeFixed32(fieldNumber, ((Integer)value.get(i)).intValue()); 
    } 
  }
  
  public void writeInt64List(int fieldNumber, List<Long> value, boolean packed) throws IOException {
    if (packed) {
      this.output.writeTag(fieldNumber, 2);
      int dataSize = 0;
      int i;
      for (i = 0; i < value.size(); i++)
        dataSize += CodedOutputStream.computeInt64SizeNoTag(((Long)value.get(i)).longValue()); 
      this.output.writeUInt32NoTag(dataSize);
      for (i = 0; i < value.size(); i++)
        this.output.writeInt64NoTag(((Long)value.get(i)).longValue()); 
    } else {
      for (int i = 0; i < value.size(); i++)
        this.output.writeInt64(fieldNumber, ((Long)value.get(i)).longValue()); 
    } 
  }
  
  public void writeUInt64List(int fieldNumber, List<Long> value, boolean packed) throws IOException {
    if (packed) {
      this.output.writeTag(fieldNumber, 2);
      int dataSize = 0;
      int i;
      for (i = 0; i < value.size(); i++)
        dataSize += CodedOutputStream.computeUInt64SizeNoTag(((Long)value.get(i)).longValue()); 
      this.output.writeUInt32NoTag(dataSize);
      for (i = 0; i < value.size(); i++)
        this.output.writeUInt64NoTag(((Long)value.get(i)).longValue()); 
    } else {
      for (int i = 0; i < value.size(); i++)
        this.output.writeUInt64(fieldNumber, ((Long)value.get(i)).longValue()); 
    } 
  }
  
  public void writeFixed64List(int fieldNumber, List<Long> value, boolean packed) throws IOException {
    if (packed) {
      this.output.writeTag(fieldNumber, 2);
      int dataSize = 0;
      int i;
      for (i = 0; i < value.size(); i++)
        dataSize += CodedOutputStream.computeFixed64SizeNoTag(((Long)value.get(i)).longValue()); 
      this.output.writeUInt32NoTag(dataSize);
      for (i = 0; i < value.size(); i++)
        this.output.writeFixed64NoTag(((Long)value.get(i)).longValue()); 
    } else {
      for (int i = 0; i < value.size(); i++)
        this.output.writeFixed64(fieldNumber, ((Long)value.get(i)).longValue()); 
    } 
  }
  
  public void writeFloatList(int fieldNumber, List<Float> value, boolean packed) throws IOException {
    if (packed) {
      this.output.writeTag(fieldNumber, 2);
      int dataSize = 0;
      int i;
      for (i = 0; i < value.size(); i++)
        dataSize += CodedOutputStream.computeFloatSizeNoTag(((Float)value.get(i)).floatValue()); 
      this.output.writeUInt32NoTag(dataSize);
      for (i = 0; i < value.size(); i++)
        this.output.writeFloatNoTag(((Float)value.get(i)).floatValue()); 
    } else {
      for (int i = 0; i < value.size(); i++)
        this.output.writeFloat(fieldNumber, ((Float)value.get(i)).floatValue()); 
    } 
  }
  
  public void writeDoubleList(int fieldNumber, List<Double> value, boolean packed) throws IOException {
    if (packed) {
      this.output.writeTag(fieldNumber, 2);
      int dataSize = 0;
      int i;
      for (i = 0; i < value.size(); i++)
        dataSize += CodedOutputStream.computeDoubleSizeNoTag(((Double)value.get(i)).doubleValue()); 
      this.output.writeUInt32NoTag(dataSize);
      for (i = 0; i < value.size(); i++)
        this.output.writeDoubleNoTag(((Double)value.get(i)).doubleValue()); 
    } else {
      for (int i = 0; i < value.size(); i++)
        this.output.writeDouble(fieldNumber, ((Double)value.get(i)).doubleValue()); 
    } 
  }
  
  public void writeEnumList(int fieldNumber, List<Integer> value, boolean packed) throws IOException {
    if (packed) {
      this.output.writeTag(fieldNumber, 2);
      int dataSize = 0;
      int i;
      for (i = 0; i < value.size(); i++)
        dataSize += CodedOutputStream.computeEnumSizeNoTag(((Integer)value.get(i)).intValue()); 
      this.output.writeUInt32NoTag(dataSize);
      for (i = 0; i < value.size(); i++)
        this.output.writeEnumNoTag(((Integer)value.get(i)).intValue()); 
    } else {
      for (int i = 0; i < value.size(); i++)
        this.output.writeEnum(fieldNumber, ((Integer)value.get(i)).intValue()); 
    } 
  }
  
  public void writeBoolList(int fieldNumber, List<Boolean> value, boolean packed) throws IOException {
    if (packed) {
      this.output.writeTag(fieldNumber, 2);
      int dataSize = 0;
      int i;
      for (i = 0; i < value.size(); i++)
        dataSize += CodedOutputStream.computeBoolSizeNoTag(((Boolean)value.get(i)).booleanValue()); 
      this.output.writeUInt32NoTag(dataSize);
      for (i = 0; i < value.size(); i++)
        this.output.writeBoolNoTag(((Boolean)value.get(i)).booleanValue()); 
    } else {
      for (int i = 0; i < value.size(); i++)
        this.output.writeBool(fieldNumber, ((Boolean)value.get(i)).booleanValue()); 
    } 
  }
  
  public void writeStringList(int fieldNumber, List<String> value) throws IOException {
    if (value instanceof LazyStringList) {
      LazyStringList lazyList = (LazyStringList)value;
      for (int i = 0; i < value.size(); i++)
        writeLazyString(fieldNumber, lazyList.getRaw(i)); 
    } else {
      for (int i = 0; i < value.size(); i++)
        this.output.writeString(fieldNumber, value.get(i)); 
    } 
  }
  
  private void writeLazyString(int fieldNumber, Object value) throws IOException {
    if (value instanceof String) {
      this.output.writeString(fieldNumber, (String)value);
    } else {
      this.output.writeBytes(fieldNumber, (ByteString)value);
    } 
  }
  
  public void writeBytesList(int fieldNumber, List<ByteString> value) throws IOException {
    for (int i = 0; i < value.size(); i++)
      this.output.writeBytes(fieldNumber, value.get(i)); 
  }
  
  public void writeUInt32List(int fieldNumber, List<Integer> value, boolean packed) throws IOException {
    if (packed) {
      this.output.writeTag(fieldNumber, 2);
      int dataSize = 0;
      int i;
      for (i = 0; i < value.size(); i++)
        dataSize += CodedOutputStream.computeUInt32SizeNoTag(((Integer)value.get(i)).intValue()); 
      this.output.writeUInt32NoTag(dataSize);
      for (i = 0; i < value.size(); i++)
        this.output.writeUInt32NoTag(((Integer)value.get(i)).intValue()); 
    } else {
      for (int i = 0; i < value.size(); i++)
        this.output.writeUInt32(fieldNumber, ((Integer)value.get(i)).intValue()); 
    } 
  }
  
  public void writeSFixed32List(int fieldNumber, List<Integer> value, boolean packed) throws IOException {
    if (packed) {
      this.output.writeTag(fieldNumber, 2);
      int dataSize = 0;
      int i;
      for (i = 0; i < value.size(); i++)
        dataSize += CodedOutputStream.computeSFixed32SizeNoTag(((Integer)value.get(i)).intValue()); 
      this.output.writeUInt32NoTag(dataSize);
      for (i = 0; i < value.size(); i++)
        this.output.writeSFixed32NoTag(((Integer)value.get(i)).intValue()); 
    } else {
      for (int i = 0; i < value.size(); i++)
        this.output.writeSFixed32(fieldNumber, ((Integer)value.get(i)).intValue()); 
    } 
  }
  
  public void writeSFixed64List(int fieldNumber, List<Long> value, boolean packed) throws IOException {
    if (packed) {
      this.output.writeTag(fieldNumber, 2);
      int dataSize = 0;
      int i;
      for (i = 0; i < value.size(); i++)
        dataSize += CodedOutputStream.computeSFixed64SizeNoTag(((Long)value.get(i)).longValue()); 
      this.output.writeUInt32NoTag(dataSize);
      for (i = 0; i < value.size(); i++)
        this.output.writeSFixed64NoTag(((Long)value.get(i)).longValue()); 
    } else {
      for (int i = 0; i < value.size(); i++)
        this.output.writeSFixed64(fieldNumber, ((Long)value.get(i)).longValue()); 
    } 
  }
  
  public void writeSInt32List(int fieldNumber, List<Integer> value, boolean packed) throws IOException {
    if (packed) {
      this.output.writeTag(fieldNumber, 2);
      int dataSize = 0;
      int i;
      for (i = 0; i < value.size(); i++)
        dataSize += CodedOutputStream.computeSInt32SizeNoTag(((Integer)value.get(i)).intValue()); 
      this.output.writeUInt32NoTag(dataSize);
      for (i = 0; i < value.size(); i++)
        this.output.writeSInt32NoTag(((Integer)value.get(i)).intValue()); 
    } else {
      for (int i = 0; i < value.size(); i++)
        this.output.writeSInt32(fieldNumber, ((Integer)value.get(i)).intValue()); 
    } 
  }
  
  public void writeSInt64List(int fieldNumber, List<Long> value, boolean packed) throws IOException {
    if (packed) {
      this.output.writeTag(fieldNumber, 2);
      int dataSize = 0;
      int i;
      for (i = 0; i < value.size(); i++)
        dataSize += CodedOutputStream.computeSInt64SizeNoTag(((Long)value.get(i)).longValue()); 
      this.output.writeUInt32NoTag(dataSize);
      for (i = 0; i < value.size(); i++)
        this.output.writeSInt64NoTag(((Long)value.get(i)).longValue()); 
    } else {
      for (int i = 0; i < value.size(); i++)
        this.output.writeSInt64(fieldNumber, ((Long)value.get(i)).longValue()); 
    } 
  }
  
  public void writeMessageList(int fieldNumber, List<?> value) throws IOException {
    for (int i = 0; i < value.size(); i++)
      writeMessage(fieldNumber, value.get(i)); 
  }
  
  public void writeMessageList(int fieldNumber, List<?> value, Schema schema) throws IOException {
    for (int i = 0; i < value.size(); i++)
      writeMessage(fieldNumber, value.get(i), schema); 
  }
  
  @Deprecated
  public void writeGroupList(int fieldNumber, List<?> value) throws IOException {
    for (int i = 0; i < value.size(); i++)
      writeGroup(fieldNumber, value.get(i)); 
  }
  
  public void writeGroupList(int fieldNumber, List<?> value, Schema schema) throws IOException {
    for (int i = 0; i < value.size(); i++)
      writeGroup(fieldNumber, value.get(i), schema); 
  }
  
  public <K, V> void writeMap(int fieldNumber, MapEntryLite.Metadata<K, V> metadata, Map<K, V> map) throws IOException {
    if (this.output.isSerializationDeterministic()) {
      writeDeterministicMap(fieldNumber, metadata, map);
      return;
    } 
    for (Map.Entry<K, V> entry : map.entrySet()) {
      this.output.writeTag(fieldNumber, 2);
      this.output.writeUInt32NoTag(
          MapEntryLite.computeSerializedSize(metadata, entry.getKey(), entry.getValue()));
      MapEntryLite.writeTo(this.output, metadata, entry.getKey(), entry.getValue());
    } 
  }
  
  private <K, V> void writeDeterministicMap(int fieldNumber, MapEntryLite.Metadata<K, V> metadata, Map<K, V> map) throws IOException {
    V value;
    switch (metadata.keyType) {
      case BOOL:
        if ((value = map.get(Boolean.FALSE)) != null)
          writeDeterministicBooleanMapEntry(fieldNumber, false, value, (MapEntryLite.Metadata)metadata); 
        if ((value = map.get(Boolean.TRUE)) != null)
          writeDeterministicBooleanMapEntry(fieldNumber, true, value, (MapEntryLite.Metadata)metadata); 
        return;
      case FIXED32:
      case INT32:
      case SFIXED32:
      case SINT32:
      case UINT32:
        writeDeterministicIntegerMap(fieldNumber, (MapEntryLite.Metadata)metadata, (Map)map);
        return;
      case FIXED64:
      case INT64:
      case SFIXED64:
      case SINT64:
      case UINT64:
        writeDeterministicLongMap(fieldNumber, (MapEntryLite.Metadata)metadata, (Map)map);
        return;
      case STRING:
        writeDeterministicStringMap(fieldNumber, (MapEntryLite.Metadata)metadata, (Map)map);
        return;
    } 
    throw new IllegalArgumentException("does not support key type: " + metadata.keyType);
  }
  
  private <V> void writeDeterministicBooleanMapEntry(int fieldNumber, boolean key, V value, MapEntryLite.Metadata<Boolean, V> metadata) throws IOException {
    this.output.writeTag(fieldNumber, 2);
    this.output.writeUInt32NoTag(MapEntryLite.computeSerializedSize(metadata, Boolean.valueOf(key), value));
    MapEntryLite.writeTo(this.output, metadata, Boolean.valueOf(key), value);
  }
  
  private <V> void writeDeterministicIntegerMap(int fieldNumber, MapEntryLite.Metadata<Integer, V> metadata, Map<Integer, V> map) throws IOException {
    int[] keys = new int[map.size()];
    int index = 0;
    for (Iterator<Integer> iterator = map.keySet().iterator(); iterator.hasNext(); ) {
      int k = ((Integer)iterator.next()).intValue();
      keys[index++] = k;
    } 
    Arrays.sort(keys);
    for (int key : keys) {
      V value = map.get(Integer.valueOf(key));
      this.output.writeTag(fieldNumber, 2);
      this.output.writeUInt32NoTag(MapEntryLite.computeSerializedSize(metadata, Integer.valueOf(key), value));
      MapEntryLite.writeTo(this.output, metadata, Integer.valueOf(key), value);
    } 
  }
  
  private <V> void writeDeterministicLongMap(int fieldNumber, MapEntryLite.Metadata<Long, V> metadata, Map<Long, V> map) throws IOException {
    long[] keys = new long[map.size()];
    int index = 0;
    for (Iterator<Long> iterator = map.keySet().iterator(); iterator.hasNext(); ) {
      long k = ((Long)iterator.next()).longValue();
      keys[index++] = k;
    } 
    Arrays.sort(keys);
    for (long key : keys) {
      V value = map.get(Long.valueOf(key));
      this.output.writeTag(fieldNumber, 2);
      this.output.writeUInt32NoTag(MapEntryLite.computeSerializedSize(metadata, Long.valueOf(key), value));
      MapEntryLite.writeTo(this.output, metadata, Long.valueOf(key), value);
    } 
  }
  
  private <V> void writeDeterministicStringMap(int fieldNumber, MapEntryLite.Metadata<String, V> metadata, Map<String, V> map) throws IOException {
    String[] keys = new String[map.size()];
    int index = 0;
    for (String k : map.keySet())
      keys[index++] = k; 
    Arrays.sort((Object[])keys);
    for (String key : keys) {
      V value = map.get(key);
      this.output.writeTag(fieldNumber, 2);
      this.output.writeUInt32NoTag(MapEntryLite.computeSerializedSize(metadata, key, value));
      MapEntryLite.writeTo(this.output, metadata, key, value);
    } 
  }
}
