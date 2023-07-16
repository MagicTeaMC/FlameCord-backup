package com.google.protobuf;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

final class FieldSet<T extends FieldSet.FieldDescriptorLite<T>> {
  private static final int DEFAULT_FIELD_MAP_ARRAY_SIZE = 16;
  
  private final SmallSortedMap<T, Object> fields;
  
  private boolean isImmutable;
  
  private boolean hasLazyField;
  
  private FieldSet() {
    this.fields = SmallSortedMap.newFieldMap(16);
  }
  
  private FieldSet(boolean dummy) {
    this(SmallSortedMap.newFieldMap(0));
    makeImmutable();
  }
  
  private FieldSet(SmallSortedMap<T, Object> fields) {
    this.fields = fields;
    makeImmutable();
  }
  
  public static <T extends FieldDescriptorLite<T>> FieldSet<T> newFieldSet() {
    return new FieldSet<>();
  }
  
  public static <T extends FieldDescriptorLite<T>> FieldSet<T> emptySet() {
    return DEFAULT_INSTANCE;
  }
  
  public static <T extends FieldDescriptorLite<T>> Builder<T> newBuilder() {
    return new Builder<>();
  }
  
  private static final FieldSet DEFAULT_INSTANCE = new FieldSet(true);
  
  boolean isEmpty() {
    return this.fields.isEmpty();
  }
  
  public void makeImmutable() {
    if (this.isImmutable)
      return; 
    for (int i = 0; i < this.fields.getNumArrayEntries(); i++) {
      Map.Entry<T, Object> entry = this.fields.getArrayEntryAt(i);
      if (entry.getValue() instanceof GeneratedMessageLite)
        ((GeneratedMessageLite)entry.getValue()).makeImmutable(); 
    } 
    this.fields.makeImmutable();
    this.isImmutable = true;
  }
  
  public boolean isImmutable() {
    return this.isImmutable;
  }
  
  public boolean equals(Object o) {
    if (this == o)
      return true; 
    if (!(o instanceof FieldSet))
      return false; 
    FieldSet<?> other = (FieldSet)o;
    return this.fields.equals(other.fields);
  }
  
  public int hashCode() {
    return this.fields.hashCode();
  }
  
  public FieldSet<T> clone() {
    FieldSet<T> clone = newFieldSet();
    for (int i = 0; i < this.fields.getNumArrayEntries(); i++) {
      Map.Entry<T, Object> entry = this.fields.getArrayEntryAt(i);
      clone.setField(entry.getKey(), entry.getValue());
    } 
    for (Map.Entry<T, Object> entry : this.fields.getOverflowEntries())
      clone.setField(entry.getKey(), entry.getValue()); 
    clone.hasLazyField = this.hasLazyField;
    return clone;
  }
  
  public void clear() {
    this.fields.clear();
    this.hasLazyField = false;
  }
  
  public Map<T, Object> getAllFields() {
    if (this.hasLazyField) {
      SmallSortedMap<T, Object> result = cloneAllFieldsMap(this.fields, false);
      if (this.fields.isImmutable())
        result.makeImmutable(); 
      return result;
    } 
    return this.fields.isImmutable() ? this.fields : Collections.<T, Object>unmodifiableMap(this.fields);
  }
  
  private static <T extends FieldDescriptorLite<T>> SmallSortedMap<T, Object> cloneAllFieldsMap(SmallSortedMap<T, Object> fields, boolean copyList) {
    SmallSortedMap<T, Object> result = SmallSortedMap.newFieldMap(16);
    for (int i = 0; i < fields.getNumArrayEntries(); i++)
      cloneFieldEntry(result, fields.getArrayEntryAt(i), copyList); 
    for (Map.Entry<T, Object> entry : fields.getOverflowEntries())
      cloneFieldEntry(result, entry, copyList); 
    return result;
  }
  
  private static <T extends FieldDescriptorLite<T>> void cloneFieldEntry(Map<T, Object> map, Map.Entry<T, Object> entry, boolean copyList) {
    FieldDescriptorLite fieldDescriptorLite = (FieldDescriptorLite)entry.getKey();
    Object value = entry.getValue();
    if (value instanceof LazyField) {
      map.put((T)fieldDescriptorLite, ((LazyField)value).getValue());
    } else if (copyList && value instanceof List) {
      map.put((T)fieldDescriptorLite, new ArrayList((List)value));
    } else {
      map.put((T)fieldDescriptorLite, value);
    } 
  }
  
  public Iterator<Map.Entry<T, Object>> iterator() {
    if (this.hasLazyField)
      return new LazyField.LazyIterator<>(this.fields.entrySet().iterator()); 
    return this.fields.entrySet().iterator();
  }
  
  Iterator<Map.Entry<T, Object>> descendingIterator() {
    if (this.hasLazyField)
      return new LazyField.LazyIterator<>(this.fields.descendingEntrySet().iterator()); 
    return this.fields.descendingEntrySet().iterator();
  }
  
  public boolean hasField(T descriptor) {
    if (descriptor.isRepeated())
      throw new IllegalArgumentException("hasField() can only be called on non-repeated fields."); 
    return (this.fields.get(descriptor) != null);
  }
  
  public Object getField(T descriptor) {
    Object o = this.fields.get(descriptor);
    if (o instanceof LazyField)
      return ((LazyField)o).getValue(); 
    return o;
  }
  
  public void setField(T descriptor, Object value) {
    if (descriptor.isRepeated()) {
      if (!(value instanceof List))
        throw new IllegalArgumentException("Wrong object type used with protocol message reflection."); 
      List newList = new ArrayList();
      newList.addAll((List)value);
      for (Object element : newList)
        verifyType(descriptor, element); 
      value = newList;
    } else {
      verifyType(descriptor, value);
    } 
    if (value instanceof LazyField)
      this.hasLazyField = true; 
    this.fields.put(descriptor, value);
  }
  
  public void clearField(T descriptor) {
    this.fields.remove(descriptor);
    if (this.fields.isEmpty())
      this.hasLazyField = false; 
  }
  
  public int getRepeatedFieldCount(T descriptor) {
    if (!descriptor.isRepeated())
      throw new IllegalArgumentException("getRepeatedField() can only be called on repeated fields."); 
    Object value = getField(descriptor);
    if (value == null)
      return 0; 
    return ((List)value).size();
  }
  
  public Object getRepeatedField(T descriptor, int index) {
    if (!descriptor.isRepeated())
      throw new IllegalArgumentException("getRepeatedField() can only be called on repeated fields."); 
    Object value = getField(descriptor);
    if (value == null)
      throw new IndexOutOfBoundsException(); 
    return ((List)value).get(index);
  }
  
  public void setRepeatedField(T descriptor, int index, Object value) {
    if (!descriptor.isRepeated())
      throw new IllegalArgumentException("getRepeatedField() can only be called on repeated fields."); 
    Object list = getField(descriptor);
    if (list == null)
      throw new IndexOutOfBoundsException(); 
    verifyType(descriptor, value);
    ((List<Object>)list).set(index, value);
  }
  
  public void addRepeatedField(T descriptor, Object value) {
    List<Object> list;
    if (!descriptor.isRepeated())
      throw new IllegalArgumentException("addRepeatedField() can only be called on repeated fields."); 
    verifyType(descriptor, value);
    Object existingValue = getField(descriptor);
    if (existingValue == null) {
      list = new ArrayList();
      this.fields.put(descriptor, list);
    } else {
      list = (List<Object>)existingValue;
    } 
    list.add(value);
  }
  
  private void verifyType(T descriptor, Object value) {
    if (!isValidType(descriptor.getLiteType(), value))
      throw new IllegalArgumentException(
          String.format("Wrong object type used with protocol message reflection.\nField number: %d, field java type: %s, value type: %s\n", new Object[] { Integer.valueOf(descriptor.getNumber()), descriptor
              .getLiteType().getJavaType(), value
              .getClass().getName() })); 
  }
  
  private static boolean isValidType(WireFormat.FieldType type, Object value) {
    Internal.checkNotNull(value);
    switch (type.getJavaType()) {
      case DOUBLE:
        return value instanceof Integer;
      case FLOAT:
        return value instanceof Long;
      case INT64:
        return value instanceof Float;
      case UINT64:
        return value instanceof Double;
      case INT32:
        return value instanceof Boolean;
      case FIXED64:
        return value instanceof String;
      case FIXED32:
        return (value instanceof ByteString || value instanceof byte[]);
      case BOOL:
        return (value instanceof Integer || value instanceof Internal.EnumLite);
      case GROUP:
        return (value instanceof MessageLite || value instanceof LazyField);
    } 
    return false;
  }
  
  public boolean isInitialized() {
    for (int i = 0; i < this.fields.getNumArrayEntries(); i++) {
      if (!isInitialized(this.fields.getArrayEntryAt(i)))
        return false; 
    } 
    for (Map.Entry<T, Object> entry : this.fields.getOverflowEntries()) {
      if (!isInitialized(entry))
        return false; 
    } 
    return true;
  }
  
  private static <T extends FieldDescriptorLite<T>> boolean isInitialized(Map.Entry<T, Object> entry) {
    FieldDescriptorLite fieldDescriptorLite = (FieldDescriptorLite)entry.getKey();
    if (fieldDescriptorLite.getLiteJavaType() == WireFormat.JavaType.MESSAGE)
      if (fieldDescriptorLite.isRepeated()) {
        for (Object element : entry.getValue()) {
          if (!isMessageFieldValueInitialized(element))
            return false; 
        } 
      } else {
        return isMessageFieldValueInitialized(entry.getValue());
      }  
    return true;
  }
  
  private static boolean isMessageFieldValueInitialized(Object value) {
    if (value instanceof MessageLiteOrBuilder)
      return ((MessageLiteOrBuilder)value).isInitialized(); 
    if (value instanceof LazyField)
      return true; 
    throw new IllegalArgumentException("Wrong object type used with protocol message reflection.");
  }
  
  static int getWireFormatForFieldType(WireFormat.FieldType type, boolean isPacked) {
    if (isPacked)
      return 2; 
    return type.getWireType();
  }
  
  public void mergeFrom(FieldSet<T> other) {
    for (int i = 0; i < other.fields.getNumArrayEntries(); i++)
      mergeFromField(other.fields.getArrayEntryAt(i)); 
    for (Map.Entry<T, Object> entry : other.fields.getOverflowEntries())
      mergeFromField(entry); 
  }
  
  private static Object cloneIfMutable(Object value) {
    if (value instanceof byte[]) {
      byte[] bytes = (byte[])value;
      byte[] copy = new byte[bytes.length];
      System.arraycopy(bytes, 0, copy, 0, bytes.length);
      return copy;
    } 
    return value;
  }
  
  private void mergeFromField(Map.Entry<T, Object> entry) {
    FieldDescriptorLite fieldDescriptorLite = (FieldDescriptorLite)entry.getKey();
    Object otherValue = entry.getValue();
    if (otherValue instanceof LazyField)
      otherValue = ((LazyField)otherValue).getValue(); 
    if (fieldDescriptorLite.isRepeated()) {
      Object value = getField((T)fieldDescriptorLite);
      if (value == null)
        value = new ArrayList(); 
      for (Object element : otherValue)
        ((List<Object>)value).add(cloneIfMutable(element)); 
      this.fields.put((T)fieldDescriptorLite, value);
    } else if (fieldDescriptorLite.getLiteJavaType() == WireFormat.JavaType.MESSAGE) {
      Object value = getField((T)fieldDescriptorLite);
      if (value == null) {
        this.fields.put((T)fieldDescriptorLite, cloneIfMutable(otherValue));
      } else {
        value = fieldDescriptorLite.internalMergeFrom(((MessageLite)value).toBuilder(), (MessageLite)otherValue).build();
        this.fields.put((T)fieldDescriptorLite, value);
      } 
    } else {
      this.fields.put((T)fieldDescriptorLite, cloneIfMutable(otherValue));
    } 
  }
  
  public static Object readPrimitiveField(CodedInputStream input, WireFormat.FieldType type, boolean checkUtf8) throws IOException {
    if (checkUtf8)
      return WireFormat.readPrimitiveField(input, type, WireFormat.Utf8Validation.STRICT); 
    return WireFormat.readPrimitiveField(input, type, WireFormat.Utf8Validation.LOOSE);
  }
  
  public void writeTo(CodedOutputStream output) throws IOException {
    for (int i = 0; i < this.fields.getNumArrayEntries(); i++) {
      Map.Entry<T, Object> entry = this.fields.getArrayEntryAt(i);
      writeField((FieldDescriptorLite)entry.getKey(), entry.getValue(), output);
    } 
    for (Map.Entry<T, Object> entry : this.fields.getOverflowEntries())
      writeField((FieldDescriptorLite)entry.getKey(), entry.getValue(), output); 
  }
  
  public void writeMessageSetTo(CodedOutputStream output) throws IOException {
    for (int i = 0; i < this.fields.getNumArrayEntries(); i++)
      writeMessageSetTo(this.fields.getArrayEntryAt(i), output); 
    for (Map.Entry<T, Object> entry : this.fields.getOverflowEntries())
      writeMessageSetTo(entry, output); 
  }
  
  private void writeMessageSetTo(Map.Entry<T, Object> entry, CodedOutputStream output) throws IOException {
    FieldDescriptorLite<?> fieldDescriptorLite = (FieldDescriptorLite)entry.getKey();
    if (fieldDescriptorLite.getLiteJavaType() == WireFormat.JavaType.MESSAGE && 
      !fieldDescriptorLite.isRepeated() && 
      !fieldDescriptorLite.isPacked()) {
      Object value = entry.getValue();
      if (value instanceof LazyField)
        value = ((LazyField)value).getValue(); 
      output.writeMessageSetExtension(((FieldDescriptorLite)entry.getKey()).getNumber(), (MessageLite)value);
    } else {
      writeField(fieldDescriptorLite, entry.getValue(), output);
    } 
  }
  
  static void writeElement(CodedOutputStream output, WireFormat.FieldType type, int number, Object value) throws IOException {
    if (type == WireFormat.FieldType.GROUP) {
      output.writeGroup(number, (MessageLite)value);
    } else {
      output.writeTag(number, getWireFormatForFieldType(type, false));
      writeElementNoTag(output, type, value);
    } 
  }
  
  static void writeElementNoTag(CodedOutputStream output, WireFormat.FieldType type, Object value) throws IOException {
    switch (type) {
      case DOUBLE:
        output.writeDoubleNoTag(((Double)value).doubleValue());
        break;
      case FLOAT:
        output.writeFloatNoTag(((Float)value).floatValue());
        break;
      case INT64:
        output.writeInt64NoTag(((Long)value).longValue());
        break;
      case UINT64:
        output.writeUInt64NoTag(((Long)value).longValue());
        break;
      case INT32:
        output.writeInt32NoTag(((Integer)value).intValue());
        break;
      case FIXED64:
        output.writeFixed64NoTag(((Long)value).longValue());
        break;
      case FIXED32:
        output.writeFixed32NoTag(((Integer)value).intValue());
        break;
      case BOOL:
        output.writeBoolNoTag(((Boolean)value).booleanValue());
        break;
      case GROUP:
        output.writeGroupNoTag((MessageLite)value);
        break;
      case MESSAGE:
        output.writeMessageNoTag((MessageLite)value);
        break;
      case STRING:
        if (value instanceof ByteString) {
          output.writeBytesNoTag((ByteString)value);
          break;
        } 
        output.writeStringNoTag((String)value);
        break;
      case BYTES:
        if (value instanceof ByteString) {
          output.writeBytesNoTag((ByteString)value);
          break;
        } 
        output.writeByteArrayNoTag((byte[])value);
        break;
      case UINT32:
        output.writeUInt32NoTag(((Integer)value).intValue());
        break;
      case SFIXED32:
        output.writeSFixed32NoTag(((Integer)value).intValue());
        break;
      case SFIXED64:
        output.writeSFixed64NoTag(((Long)value).longValue());
        break;
      case SINT32:
        output.writeSInt32NoTag(((Integer)value).intValue());
        break;
      case SINT64:
        output.writeSInt64NoTag(((Long)value).longValue());
        break;
      case ENUM:
        if (value instanceof Internal.EnumLite) {
          output.writeEnumNoTag(((Internal.EnumLite)value).getNumber());
          break;
        } 
        output.writeEnumNoTag(((Integer)value).intValue());
        break;
    } 
  }
  
  public static void writeField(FieldDescriptorLite<?> descriptor, Object value, CodedOutputStream output) throws IOException {
    WireFormat.FieldType type = descriptor.getLiteType();
    int number = descriptor.getNumber();
    if (descriptor.isRepeated()) {
      List<?> valueList = (List)value;
      if (descriptor.isPacked()) {
        output.writeTag(number, 2);
        int dataSize = 0;
        for (Object element : valueList)
          dataSize += computeElementSizeNoTag(type, element); 
        output.writeUInt32NoTag(dataSize);
        for (Object element : valueList)
          writeElementNoTag(output, type, element); 
      } else {
        for (Object element : valueList)
          writeElement(output, type, number, element); 
      } 
    } else if (value instanceof LazyField) {
      writeElement(output, type, number, ((LazyField)value).getValue());
    } else {
      writeElement(output, type, number, value);
    } 
  }
  
  public int getSerializedSize() {
    int size = 0;
    for (int i = 0; i < this.fields.getNumArrayEntries(); i++) {
      Map.Entry<T, Object> entry = this.fields.getArrayEntryAt(i);
      size += computeFieldSize((FieldDescriptorLite)entry.getKey(), entry.getValue());
    } 
    for (Map.Entry<T, Object> entry : this.fields.getOverflowEntries())
      size += computeFieldSize((FieldDescriptorLite)entry.getKey(), entry.getValue()); 
    return size;
  }
  
  public int getMessageSetSerializedSize() {
    int size = 0;
    for (int i = 0; i < this.fields.getNumArrayEntries(); i++)
      size += getMessageSetSerializedSize(this.fields.getArrayEntryAt(i)); 
    for (Map.Entry<T, Object> entry : this.fields.getOverflowEntries())
      size += getMessageSetSerializedSize(entry); 
    return size;
  }
  
  private int getMessageSetSerializedSize(Map.Entry<T, Object> entry) {
    FieldDescriptorLite<?> fieldDescriptorLite = (FieldDescriptorLite)entry.getKey();
    Object value = entry.getValue();
    if (fieldDescriptorLite.getLiteJavaType() == WireFormat.JavaType.MESSAGE && 
      !fieldDescriptorLite.isRepeated() && 
      !fieldDescriptorLite.isPacked()) {
      if (value instanceof LazyField)
        return CodedOutputStream.computeLazyFieldMessageSetExtensionSize(((FieldDescriptorLite)entry
            .getKey()).getNumber(), (LazyField)value); 
      return CodedOutputStream.computeMessageSetExtensionSize(((FieldDescriptorLite)entry
          .getKey()).getNumber(), (MessageLite)value);
    } 
    return computeFieldSize(fieldDescriptorLite, value);
  }
  
  static int computeElementSize(WireFormat.FieldType type, int number, Object value) {
    int tagSize = CodedOutputStream.computeTagSize(number);
    if (type == WireFormat.FieldType.GROUP)
      tagSize *= 2; 
    return tagSize + computeElementSizeNoTag(type, value);
  }
  
  static int computeElementSizeNoTag(WireFormat.FieldType type, Object value) {
    switch (type) {
      case DOUBLE:
        return CodedOutputStream.computeDoubleSizeNoTag(((Double)value).doubleValue());
      case FLOAT:
        return CodedOutputStream.computeFloatSizeNoTag(((Float)value).floatValue());
      case INT64:
        return CodedOutputStream.computeInt64SizeNoTag(((Long)value).longValue());
      case UINT64:
        return CodedOutputStream.computeUInt64SizeNoTag(((Long)value).longValue());
      case INT32:
        return CodedOutputStream.computeInt32SizeNoTag(((Integer)value).intValue());
      case FIXED64:
        return CodedOutputStream.computeFixed64SizeNoTag(((Long)value).longValue());
      case FIXED32:
        return CodedOutputStream.computeFixed32SizeNoTag(((Integer)value).intValue());
      case BOOL:
        return CodedOutputStream.computeBoolSizeNoTag(((Boolean)value).booleanValue());
      case GROUP:
        return CodedOutputStream.computeGroupSizeNoTag((MessageLite)value);
      case BYTES:
        if (value instanceof ByteString)
          return CodedOutputStream.computeBytesSizeNoTag((ByteString)value); 
        return CodedOutputStream.computeByteArraySizeNoTag((byte[])value);
      case STRING:
        if (value instanceof ByteString)
          return CodedOutputStream.computeBytesSizeNoTag((ByteString)value); 
        return CodedOutputStream.computeStringSizeNoTag((String)value);
      case UINT32:
        return CodedOutputStream.computeUInt32SizeNoTag(((Integer)value).intValue());
      case SFIXED32:
        return CodedOutputStream.computeSFixed32SizeNoTag(((Integer)value).intValue());
      case SFIXED64:
        return CodedOutputStream.computeSFixed64SizeNoTag(((Long)value).longValue());
      case SINT32:
        return CodedOutputStream.computeSInt32SizeNoTag(((Integer)value).intValue());
      case SINT64:
        return CodedOutputStream.computeSInt64SizeNoTag(((Long)value).longValue());
      case MESSAGE:
        if (value instanceof LazyField)
          return CodedOutputStream.computeLazyFieldSizeNoTag((LazyField)value); 
        return CodedOutputStream.computeMessageSizeNoTag((MessageLite)value);
      case ENUM:
        if (value instanceof Internal.EnumLite)
          return CodedOutputStream.computeEnumSizeNoTag(((Internal.EnumLite)value).getNumber()); 
        return CodedOutputStream.computeEnumSizeNoTag(((Integer)value).intValue());
    } 
    throw new RuntimeException("There is no way to get here, but the compiler thinks otherwise.");
  }
  
  public static int computeFieldSize(FieldDescriptorLite<?> descriptor, Object value) {
    WireFormat.FieldType type = descriptor.getLiteType();
    int number = descriptor.getNumber();
    if (descriptor.isRepeated()) {
      if (descriptor.isPacked()) {
        int dataSize = 0;
        for (Object element : value)
          dataSize += computeElementSizeNoTag(type, element); 
        return dataSize + 
          CodedOutputStream.computeTagSize(number) + 
          CodedOutputStream.computeUInt32SizeNoTag(dataSize);
      } 
      int size = 0;
      for (Object element : value)
        size += computeElementSize(type, number, element); 
      return size;
    } 
    return computeElementSize(type, number, value);
  }
  
  static final class Builder<T extends FieldDescriptorLite<T>> {
    private SmallSortedMap<T, Object> fields;
    
    private boolean hasLazyField;
    
    private boolean isMutable;
    
    private boolean hasNestedBuilders;
    
    private Builder() {
      this(SmallSortedMap.newFieldMap(16));
    }
    
    private Builder(SmallSortedMap<T, Object> fields) {
      this.fields = fields;
      this.isMutable = true;
    }
    
    public FieldSet<T> build() {
      return buildImpl(false);
    }
    
    public FieldSet<T> buildPartial() {
      return buildImpl(true);
    }
    
    private FieldSet<T> buildImpl(boolean partial) {
      if (this.fields.isEmpty())
        return FieldSet.emptySet(); 
      this.isMutable = false;
      SmallSortedMap<T, Object> fieldsForBuild = this.fields;
      if (this.hasNestedBuilders) {
        fieldsForBuild = FieldSet.cloneAllFieldsMap(this.fields, false);
        replaceBuilders(fieldsForBuild, partial);
      } 
      FieldSet<T> fieldSet = new FieldSet<>(fieldsForBuild);
      fieldSet.hasLazyField = this.hasLazyField;
      return fieldSet;
    }
    
    private static <T extends FieldSet.FieldDescriptorLite<T>> void replaceBuilders(SmallSortedMap<T, Object> fieldMap, boolean partial) {
      for (int i = 0; i < fieldMap.getNumArrayEntries(); i++)
        replaceBuilders(fieldMap.getArrayEntryAt(i), partial); 
      for (Map.Entry<T, Object> entry : fieldMap.getOverflowEntries())
        replaceBuilders(entry, partial); 
    }
    
    private static <T extends FieldSet.FieldDescriptorLite<T>> void replaceBuilders(Map.Entry<T, Object> entry, boolean partial) {
      entry.setValue(replaceBuilders((FieldSet.FieldDescriptorLite)entry.getKey(), entry.getValue(), partial));
    }
    
    private static <T extends FieldSet.FieldDescriptorLite<T>> Object replaceBuilders(T descriptor, Object value, boolean partial) {
      if (value == null)
        return value; 
      if (descriptor.getLiteJavaType() == WireFormat.JavaType.MESSAGE) {
        if (descriptor.isRepeated()) {
          if (!(value instanceof List))
            throw new IllegalStateException("Repeated field should contains a List but actually contains type: " + value
                
                .getClass()); 
          List<Object> list = (List<Object>)value;
          for (int i = 0; i < list.size(); i++) {
            Object oldElement = list.get(i);
            Object newElement = replaceBuilder(oldElement, partial);
            if (newElement != oldElement) {
              if (list == value)
                list = new ArrayList(list); 
              list.set(i, newElement);
            } 
          } 
          return list;
        } 
        return replaceBuilder(value, partial);
      } 
      return value;
    }
    
    private static Object replaceBuilder(Object value, boolean partial) {
      if (!(value instanceof MessageLite.Builder))
        return value; 
      MessageLite.Builder builder = (MessageLite.Builder)value;
      if (partial)
        return builder.buildPartial(); 
      return builder.build();
    }
    
    public static <T extends FieldSet.FieldDescriptorLite<T>> Builder<T> fromFieldSet(FieldSet<T> fieldSet) {
      Builder<T> builder = new Builder<>(FieldSet.cloneAllFieldsMap(fieldSet.fields, true));
      builder.hasLazyField = fieldSet.hasLazyField;
      return builder;
    }
    
    public Map<T, Object> getAllFields() {
      if (this.hasLazyField) {
        SmallSortedMap<T, Object> result = FieldSet.cloneAllFieldsMap(this.fields, false);
        if (this.fields.isImmutable()) {
          result.makeImmutable();
        } else {
          replaceBuilders(result, true);
        } 
        return result;
      } 
      return this.fields.isImmutable() ? this.fields : Collections.<T, Object>unmodifiableMap(this.fields);
    }
    
    public boolean hasField(T descriptor) {
      if (descriptor.isRepeated())
        throw new IllegalArgumentException("hasField() can only be called on non-repeated fields."); 
      return (this.fields.get(descriptor) != null);
    }
    
    public Object getField(T descriptor) {
      Object value = getFieldAllowBuilders(descriptor);
      return replaceBuilders(descriptor, value, true);
    }
    
    Object getFieldAllowBuilders(T descriptor) {
      Object o = this.fields.get(descriptor);
      if (o instanceof LazyField)
        return ((LazyField)o).getValue(); 
      return o;
    }
    
    private void ensureIsMutable() {
      if (!this.isMutable) {
        this.fields = FieldSet.cloneAllFieldsMap(this.fields, true);
        this.isMutable = true;
      } 
    }
    
    public void setField(T descriptor, Object value) {
      ensureIsMutable();
      if (descriptor.isRepeated()) {
        if (!(value instanceof List))
          throw new IllegalArgumentException("Wrong object type used with protocol message reflection."); 
        List newList = new ArrayList((List)value);
        for (Object element : newList) {
          verifyType(descriptor, element);
          this.hasNestedBuilders = (this.hasNestedBuilders || element instanceof MessageLite.Builder);
        } 
        value = newList;
      } else {
        verifyType(descriptor, value);
      } 
      if (value instanceof LazyField)
        this.hasLazyField = true; 
      this.hasNestedBuilders = (this.hasNestedBuilders || value instanceof MessageLite.Builder);
      this.fields.put(descriptor, value);
    }
    
    public void clearField(T descriptor) {
      ensureIsMutable();
      this.fields.remove(descriptor);
      if (this.fields.isEmpty())
        this.hasLazyField = false; 
    }
    
    public int getRepeatedFieldCount(T descriptor) {
      if (!descriptor.isRepeated())
        throw new IllegalArgumentException("getRepeatedFieldCount() can only be called on repeated fields."); 
      Object value = getFieldAllowBuilders(descriptor);
      if (value == null)
        return 0; 
      return ((List)value).size();
    }
    
    public Object getRepeatedField(T descriptor, int index) {
      if (this.hasNestedBuilders)
        ensureIsMutable(); 
      Object value = getRepeatedFieldAllowBuilders(descriptor, index);
      return replaceBuilder(value, true);
    }
    
    Object getRepeatedFieldAllowBuilders(T descriptor, int index) {
      if (!descriptor.isRepeated())
        throw new IllegalArgumentException("getRepeatedField() can only be called on repeated fields."); 
      Object value = getFieldAllowBuilders(descriptor);
      if (value == null)
        throw new IndexOutOfBoundsException(); 
      return ((List)value).get(index);
    }
    
    public void setRepeatedField(T descriptor, int index, Object value) {
      ensureIsMutable();
      if (!descriptor.isRepeated())
        throw new IllegalArgumentException("getRepeatedField() can only be called on repeated fields."); 
      this.hasNestedBuilders = (this.hasNestedBuilders || value instanceof MessageLite.Builder);
      Object list = getFieldAllowBuilders(descriptor);
      if (list == null)
        throw new IndexOutOfBoundsException(); 
      verifyType(descriptor, value);
      ((List<Object>)list).set(index, value);
    }
    
    public void addRepeatedField(T descriptor, Object value) {
      List<Object> list;
      ensureIsMutable();
      if (!descriptor.isRepeated())
        throw new IllegalArgumentException("addRepeatedField() can only be called on repeated fields."); 
      this.hasNestedBuilders = (this.hasNestedBuilders || value instanceof MessageLite.Builder);
      verifyType(descriptor, value);
      Object existingValue = getFieldAllowBuilders(descriptor);
      if (existingValue == null) {
        list = new ArrayList();
        this.fields.put(descriptor, list);
      } else {
        list = (List<Object>)existingValue;
      } 
      list.add(value);
    }
    
    private void verifyType(T descriptor, Object value) {
      if (!FieldSet.isValidType(descriptor.getLiteType(), value)) {
        if (descriptor.getLiteType().getJavaType() == WireFormat.JavaType.MESSAGE && value instanceof MessageLite.Builder)
          return; 
        throw new IllegalArgumentException(
            String.format("Wrong object type used with protocol message reflection.\nField number: %d, field java type: %s, value type: %s\n", new Object[] { Integer.valueOf(descriptor.getNumber()), descriptor
                .getLiteType().getJavaType(), value
                .getClass().getName() }));
      } 
    }
    
    public boolean isInitialized() {
      for (int i = 0; i < this.fields.getNumArrayEntries(); i++) {
        if (!FieldSet.isInitialized(this.fields.getArrayEntryAt(i)))
          return false; 
      } 
      for (Map.Entry<T, Object> entry : this.fields.getOverflowEntries()) {
        if (!FieldSet.isInitialized(entry))
          return false; 
      } 
      return true;
    }
    
    public void mergeFrom(FieldSet<T> other) {
      ensureIsMutable();
      for (int i = 0; i < other.fields.getNumArrayEntries(); i++)
        mergeFromField(other.fields.getArrayEntryAt(i)); 
      for (Map.Entry<T, Object> entry : (Iterable<Map.Entry<T, Object>>)other.fields.getOverflowEntries())
        mergeFromField(entry); 
    }
    
    private void mergeFromField(Map.Entry<T, Object> entry) {
      FieldSet.FieldDescriptorLite fieldDescriptorLite = (FieldSet.FieldDescriptorLite)entry.getKey();
      Object otherValue = entry.getValue();
      if (otherValue instanceof LazyField)
        otherValue = ((LazyField)otherValue).getValue(); 
      if (fieldDescriptorLite.isRepeated()) {
        List<Object> value = (List<Object>)getFieldAllowBuilders((T)fieldDescriptorLite);
        if (value == null) {
          value = new ArrayList();
          this.fields.put((T)fieldDescriptorLite, value);
        } 
        for (Object element : otherValue)
          value.add(FieldSet.cloneIfMutable(element)); 
      } else if (fieldDescriptorLite.getLiteJavaType() == WireFormat.JavaType.MESSAGE) {
        Object value = getFieldAllowBuilders((T)fieldDescriptorLite);
        if (value == null) {
          this.fields.put((T)fieldDescriptorLite, FieldSet.cloneIfMutable(otherValue));
        } else if (value instanceof MessageLite.Builder) {
          fieldDescriptorLite.internalMergeFrom((MessageLite.Builder)value, (MessageLite)otherValue);
        } else {
          value = fieldDescriptorLite.internalMergeFrom(((MessageLite)value).toBuilder(), (MessageLite)otherValue).build();
          this.fields.put((T)fieldDescriptorLite, value);
        } 
      } else {
        this.fields.put((T)fieldDescriptorLite, FieldSet.cloneIfMutable(otherValue));
      } 
    }
  }
  
  public static interface FieldDescriptorLite<T extends FieldDescriptorLite<T>> extends Comparable<T> {
    int getNumber();
    
    WireFormat.FieldType getLiteType();
    
    WireFormat.JavaType getLiteJavaType();
    
    boolean isRepeated();
    
    boolean isPacked();
    
    Internal.EnumLiteMap<?> getEnumType();
    
    MessageLite.Builder internalMergeFrom(MessageLite.Builder param1Builder, MessageLite param1MessageLite);
  }
}
