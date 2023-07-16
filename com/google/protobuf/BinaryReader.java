package com.google.protobuf;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.List;
import java.util.Map;

@CheckReturnValue
abstract class BinaryReader implements Reader {
  private static final int FIXED32_MULTIPLE_MASK = 3;
  
  private static final int FIXED64_MULTIPLE_MASK = 7;
  
  public static BinaryReader newInstance(ByteBuffer buffer, boolean bufferIsImmutable) {
    if (buffer.hasArray())
      return new SafeHeapReader(buffer, bufferIsImmutable); 
    throw new IllegalArgumentException("Direct buffers not yet supported");
  }
  
  private BinaryReader() {}
  
  public boolean shouldDiscardUnknownFields() {
    return false;
  }
  
  public abstract int getTotalBytesRead();
  
  private static final class SafeHeapReader extends BinaryReader {
    private final boolean bufferIsImmutable;
    
    private final byte[] buffer;
    
    private int pos;
    
    private final int initialPos;
    
    private int limit;
    
    private int tag;
    
    private int endGroupTag;
    
    public SafeHeapReader(ByteBuffer bytebuf, boolean bufferIsImmutable) {
      this.bufferIsImmutable = bufferIsImmutable;
      this.buffer = bytebuf.array();
      this.initialPos = this.pos = bytebuf.arrayOffset() + bytebuf.position();
      this.limit = bytebuf.arrayOffset() + bytebuf.limit();
    }
    
    private boolean isAtEnd() {
      return (this.pos == this.limit);
    }
    
    public int getTotalBytesRead() {
      return this.pos - this.initialPos;
    }
    
    public int getFieldNumber() throws IOException {
      if (isAtEnd())
        return Integer.MAX_VALUE; 
      this.tag = readVarint32();
      if (this.tag == this.endGroupTag)
        return Integer.MAX_VALUE; 
      return WireFormat.getTagFieldNumber(this.tag);
    }
    
    public int getTag() {
      return this.tag;
    }
    
    public boolean skipField() throws IOException {
      if (isAtEnd() || this.tag == this.endGroupTag)
        return false; 
      switch (WireFormat.getTagWireType(this.tag)) {
        case 0:
          skipVarint();
          return true;
        case 1:
          skipBytes(8);
          return true;
        case 2:
          skipBytes(readVarint32());
          return true;
        case 5:
          skipBytes(4);
          return true;
        case 3:
          skipGroup();
          return true;
      } 
      throw InvalidProtocolBufferException.invalidWireType();
    }
    
    public double readDouble() throws IOException {
      requireWireType(1);
      return Double.longBitsToDouble(readLittleEndian64());
    }
    
    public float readFloat() throws IOException {
      requireWireType(5);
      return Float.intBitsToFloat(readLittleEndian32());
    }
    
    public long readUInt64() throws IOException {
      requireWireType(0);
      return readVarint64();
    }
    
    public long readInt64() throws IOException {
      requireWireType(0);
      return readVarint64();
    }
    
    public int readInt32() throws IOException {
      requireWireType(0);
      return readVarint32();
    }
    
    public long readFixed64() throws IOException {
      requireWireType(1);
      return readLittleEndian64();
    }
    
    public int readFixed32() throws IOException {
      requireWireType(5);
      return readLittleEndian32();
    }
    
    public boolean readBool() throws IOException {
      requireWireType(0);
      return (readVarint32() != 0);
    }
    
    public String readString() throws IOException {
      return readStringInternal(false);
    }
    
    public String readStringRequireUtf8() throws IOException {
      return readStringInternal(true);
    }
    
    public String readStringInternal(boolean requireUtf8) throws IOException {
      requireWireType(2);
      int size = readVarint32();
      if (size == 0)
        return ""; 
      requireBytes(size);
      if (requireUtf8 && !Utf8.isValidUtf8(this.buffer, this.pos, this.pos + size))
        throw InvalidProtocolBufferException.invalidUtf8(); 
      String result = new String(this.buffer, this.pos, size, Internal.UTF_8);
      this.pos += size;
      return result;
    }
    
    public <T> T readMessage(Class<T> clazz, ExtensionRegistryLite extensionRegistry) throws IOException {
      requireWireType(2);
      return readMessage(Protobuf.getInstance().schemaFor(clazz), extensionRegistry);
    }
    
    public <T> T readMessageBySchemaWithCheck(Schema<T> schema, ExtensionRegistryLite extensionRegistry) throws IOException {
      requireWireType(2);
      return readMessage(schema, extensionRegistry);
    }
    
    private <T> T readMessage(Schema<T> schema, ExtensionRegistryLite extensionRegistry) throws IOException {
      T newInstance = schema.newInstance();
      mergeMessageField(newInstance, schema, extensionRegistry);
      schema.makeImmutable(newInstance);
      return newInstance;
    }
    
    public <T> void mergeMessageField(T target, Schema<T> schema, ExtensionRegistryLite extensionRegistry) throws IOException {
      int size = readVarint32();
      requireBytes(size);
      int prevLimit = this.limit;
      int newLimit = this.pos + size;
      this.limit = newLimit;
      try {
        schema.mergeFrom(target, this, extensionRegistry);
        if (this.pos != newLimit)
          throw InvalidProtocolBufferException.parseFailure(); 
      } finally {
        this.limit = prevLimit;
      } 
    }
    
    @Deprecated
    public <T> T readGroup(Class<T> clazz, ExtensionRegistryLite extensionRegistry) throws IOException {
      requireWireType(3);
      return readGroup(Protobuf.getInstance().schemaFor(clazz), extensionRegistry);
    }
    
    @Deprecated
    public <T> T readGroupBySchemaWithCheck(Schema<T> schema, ExtensionRegistryLite extensionRegistry) throws IOException {
      requireWireType(3);
      return readGroup(schema, extensionRegistry);
    }
    
    private <T> T readGroup(Schema<T> schema, ExtensionRegistryLite extensionRegistry) throws IOException {
      T newInstance = schema.newInstance();
      mergeGroupField(newInstance, schema, extensionRegistry);
      schema.makeImmutable(newInstance);
      return newInstance;
    }
    
    public <T> void mergeGroupField(T target, Schema<T> schema, ExtensionRegistryLite extensionRegistry) throws IOException {
      int prevEndGroupTag = this.endGroupTag;
      this.endGroupTag = WireFormat.makeTag(WireFormat.getTagFieldNumber(this.tag), 4);
      try {
        schema.mergeFrom(target, this, extensionRegistry);
        if (this.tag != this.endGroupTag)
          throw InvalidProtocolBufferException.parseFailure(); 
      } finally {
        this.endGroupTag = prevEndGroupTag;
      } 
    }
    
    public ByteString readBytes() throws IOException {
      requireWireType(2);
      int size = readVarint32();
      if (size == 0)
        return ByteString.EMPTY; 
      requireBytes(size);
      ByteString bytes = this.bufferIsImmutable ? ByteString.wrap(this.buffer, this.pos, size) : ByteString.copyFrom(this.buffer, this.pos, size);
      this.pos += size;
      return bytes;
    }
    
    public int readUInt32() throws IOException {
      requireWireType(0);
      return readVarint32();
    }
    
    public int readEnum() throws IOException {
      requireWireType(0);
      return readVarint32();
    }
    
    public int readSFixed32() throws IOException {
      requireWireType(5);
      return readLittleEndian32();
    }
    
    public long readSFixed64() throws IOException {
      requireWireType(1);
      return readLittleEndian64();
    }
    
    public int readSInt32() throws IOException {
      requireWireType(0);
      return CodedInputStream.decodeZigZag32(readVarint32());
    }
    
    public long readSInt64() throws IOException {
      requireWireType(0);
      return CodedInputStream.decodeZigZag64(readVarint64());
    }
    
    public void readDoubleList(List<Double> target) throws IOException {
      int bytes;
      int fieldEndPos;
      int prevPos;
      int nextTag;
      if (target instanceof DoubleArrayList) {
        int i, j, k, m;
        DoubleArrayList plist = (DoubleArrayList)target;
        switch (WireFormat.getTagWireType(this.tag)) {
          case 2:
            i = readVarint32();
            verifyPackedFixed64Length(i);
            j = this.pos + i;
            while (this.pos < j)
              plist.addDouble(Double.longBitsToDouble(readLittleEndian64_NoCheck())); 
            return;
          case 1:
            do {
              plist.addDouble(readDouble());
              if (isAtEnd())
                return; 
              k = this.pos;
              m = readVarint32();
            } while (m == this.tag);
            this.pos = k;
            return;
        } 
        throw InvalidProtocolBufferException.invalidWireType();
      } 
      switch (WireFormat.getTagWireType(this.tag)) {
        case 2:
          bytes = readVarint32();
          verifyPackedFixed64Length(bytes);
          fieldEndPos = this.pos + bytes;
          while (this.pos < fieldEndPos)
            target.add(Double.valueOf(Double.longBitsToDouble(readLittleEndian64_NoCheck()))); 
          return;
        case 1:
          do {
            target.add(Double.valueOf(readDouble()));
            if (isAtEnd())
              return; 
            prevPos = this.pos;
            nextTag = readVarint32();
          } while (nextTag == this.tag);
          this.pos = prevPos;
          return;
      } 
      throw InvalidProtocolBufferException.invalidWireType();
    }
    
    public void readFloatList(List<Float> target) throws IOException {
      int bytes;
      int fieldEndPos;
      int prevPos;
      int nextTag;
      if (target instanceof FloatArrayList) {
        int i, j, k, m;
        FloatArrayList plist = (FloatArrayList)target;
        switch (WireFormat.getTagWireType(this.tag)) {
          case 2:
            i = readVarint32();
            verifyPackedFixed32Length(i);
            j = this.pos + i;
            while (this.pos < j)
              plist.addFloat(Float.intBitsToFloat(readLittleEndian32_NoCheck())); 
            return;
          case 5:
            do {
              plist.addFloat(readFloat());
              if (isAtEnd())
                return; 
              k = this.pos;
              m = readVarint32();
            } while (m == this.tag);
            this.pos = k;
            return;
        } 
        throw InvalidProtocolBufferException.invalidWireType();
      } 
      switch (WireFormat.getTagWireType(this.tag)) {
        case 2:
          bytes = readVarint32();
          verifyPackedFixed32Length(bytes);
          fieldEndPos = this.pos + bytes;
          while (this.pos < fieldEndPos)
            target.add(Float.valueOf(Float.intBitsToFloat(readLittleEndian32_NoCheck()))); 
          return;
        case 5:
          do {
            target.add(Float.valueOf(readFloat()));
            if (isAtEnd())
              return; 
            prevPos = this.pos;
            nextTag = readVarint32();
          } while (nextTag == this.tag);
          this.pos = prevPos;
          return;
      } 
      throw InvalidProtocolBufferException.invalidWireType();
    }
    
    public void readUInt64List(List<Long> target) throws IOException {
      int bytes;
      int fieldEndPos;
      int prevPos;
      int nextTag;
      if (target instanceof LongArrayList) {
        int i, j, k, m;
        LongArrayList plist = (LongArrayList)target;
        switch (WireFormat.getTagWireType(this.tag)) {
          case 2:
            i = readVarint32();
            j = this.pos + i;
            while (this.pos < j)
              plist.addLong(readVarint64()); 
            requirePosition(j);
            return;
          case 0:
            do {
              plist.addLong(readUInt64());
              if (isAtEnd())
                return; 
              k = this.pos;
              m = readVarint32();
            } while (m == this.tag);
            this.pos = k;
            return;
        } 
        throw InvalidProtocolBufferException.invalidWireType();
      } 
      switch (WireFormat.getTagWireType(this.tag)) {
        case 2:
          bytes = readVarint32();
          fieldEndPos = this.pos + bytes;
          while (this.pos < fieldEndPos)
            target.add(Long.valueOf(readVarint64())); 
          requirePosition(fieldEndPos);
          return;
        case 0:
          do {
            target.add(Long.valueOf(readUInt64()));
            if (isAtEnd())
              return; 
            prevPos = this.pos;
            nextTag = readVarint32();
          } while (nextTag == this.tag);
          this.pos = prevPos;
          return;
      } 
      throw InvalidProtocolBufferException.invalidWireType();
    }
    
    public void readInt64List(List<Long> target) throws IOException {
      int bytes;
      int fieldEndPos;
      int prevPos;
      int nextTag;
      if (target instanceof LongArrayList) {
        int i, j, k, m;
        LongArrayList plist = (LongArrayList)target;
        switch (WireFormat.getTagWireType(this.tag)) {
          case 2:
            i = readVarint32();
            j = this.pos + i;
            while (this.pos < j)
              plist.addLong(readVarint64()); 
            requirePosition(j);
            return;
          case 0:
            do {
              plist.addLong(readInt64());
              if (isAtEnd())
                return; 
              k = this.pos;
              m = readVarint32();
            } while (m == this.tag);
            this.pos = k;
            return;
        } 
        throw InvalidProtocolBufferException.invalidWireType();
      } 
      switch (WireFormat.getTagWireType(this.tag)) {
        case 2:
          bytes = readVarint32();
          fieldEndPos = this.pos + bytes;
          while (this.pos < fieldEndPos)
            target.add(Long.valueOf(readVarint64())); 
          requirePosition(fieldEndPos);
          return;
        case 0:
          do {
            target.add(Long.valueOf(readInt64()));
            if (isAtEnd())
              return; 
            prevPos = this.pos;
            nextTag = readVarint32();
          } while (nextTag == this.tag);
          this.pos = prevPos;
          return;
      } 
      throw InvalidProtocolBufferException.invalidWireType();
    }
    
    public void readInt32List(List<Integer> target) throws IOException {
      int bytes;
      int fieldEndPos;
      int prevPos;
      int nextTag;
      if (target instanceof IntArrayList) {
        int i, j, k, m;
        IntArrayList plist = (IntArrayList)target;
        switch (WireFormat.getTagWireType(this.tag)) {
          case 2:
            i = readVarint32();
            j = this.pos + i;
            while (this.pos < j)
              plist.addInt(readVarint32()); 
            requirePosition(j);
            return;
          case 0:
            do {
              plist.addInt(readInt32());
              if (isAtEnd())
                return; 
              k = this.pos;
              m = readVarint32();
            } while (m == this.tag);
            this.pos = k;
            return;
        } 
        throw InvalidProtocolBufferException.invalidWireType();
      } 
      switch (WireFormat.getTagWireType(this.tag)) {
        case 2:
          bytes = readVarint32();
          fieldEndPos = this.pos + bytes;
          while (this.pos < fieldEndPos)
            target.add(Integer.valueOf(readVarint32())); 
          requirePosition(fieldEndPos);
          return;
        case 0:
          do {
            target.add(Integer.valueOf(readInt32()));
            if (isAtEnd())
              return; 
            prevPos = this.pos;
            nextTag = readVarint32();
          } while (nextTag == this.tag);
          this.pos = prevPos;
          return;
      } 
      throw InvalidProtocolBufferException.invalidWireType();
    }
    
    public void readFixed64List(List<Long> target) throws IOException {
      int bytes;
      int fieldEndPos;
      int prevPos;
      int nextTag;
      if (target instanceof LongArrayList) {
        int i, j, k, m;
        LongArrayList plist = (LongArrayList)target;
        switch (WireFormat.getTagWireType(this.tag)) {
          case 2:
            i = readVarint32();
            verifyPackedFixed64Length(i);
            j = this.pos + i;
            while (this.pos < j)
              plist.addLong(readLittleEndian64_NoCheck()); 
            return;
          case 1:
            do {
              plist.addLong(readFixed64());
              if (isAtEnd())
                return; 
              k = this.pos;
              m = readVarint32();
            } while (m == this.tag);
            this.pos = k;
            return;
        } 
        throw InvalidProtocolBufferException.invalidWireType();
      } 
      switch (WireFormat.getTagWireType(this.tag)) {
        case 2:
          bytes = readVarint32();
          verifyPackedFixed64Length(bytes);
          fieldEndPos = this.pos + bytes;
          while (this.pos < fieldEndPos)
            target.add(Long.valueOf(readLittleEndian64_NoCheck())); 
          return;
        case 1:
          do {
            target.add(Long.valueOf(readFixed64()));
            if (isAtEnd())
              return; 
            prevPos = this.pos;
            nextTag = readVarint32();
          } while (nextTag == this.tag);
          this.pos = prevPos;
          return;
      } 
      throw InvalidProtocolBufferException.invalidWireType();
    }
    
    public void readFixed32List(List<Integer> target) throws IOException {
      int bytes;
      int fieldEndPos;
      int prevPos;
      int nextTag;
      if (target instanceof IntArrayList) {
        int i, j, k, m;
        IntArrayList plist = (IntArrayList)target;
        switch (WireFormat.getTagWireType(this.tag)) {
          case 2:
            i = readVarint32();
            verifyPackedFixed32Length(i);
            j = this.pos + i;
            while (this.pos < j)
              plist.addInt(readLittleEndian32_NoCheck()); 
            return;
          case 5:
            do {
              plist.addInt(readFixed32());
              if (isAtEnd())
                return; 
              k = this.pos;
              m = readVarint32();
            } while (m == this.tag);
            this.pos = k;
            return;
        } 
        throw InvalidProtocolBufferException.invalidWireType();
      } 
      switch (WireFormat.getTagWireType(this.tag)) {
        case 2:
          bytes = readVarint32();
          verifyPackedFixed32Length(bytes);
          fieldEndPos = this.pos + bytes;
          while (this.pos < fieldEndPos)
            target.add(Integer.valueOf(readLittleEndian32_NoCheck())); 
          return;
        case 5:
          do {
            target.add(Integer.valueOf(readFixed32()));
            if (isAtEnd())
              return; 
            prevPos = this.pos;
            nextTag = readVarint32();
          } while (nextTag == this.tag);
          this.pos = prevPos;
          return;
      } 
      throw InvalidProtocolBufferException.invalidWireType();
    }
    
    public void readBoolList(List<Boolean> target) throws IOException {
      int bytes;
      int fieldEndPos;
      int prevPos;
      int nextTag;
      if (target instanceof BooleanArrayList) {
        int i, j, k, m;
        BooleanArrayList plist = (BooleanArrayList)target;
        switch (WireFormat.getTagWireType(this.tag)) {
          case 2:
            i = readVarint32();
            j = this.pos + i;
            while (this.pos < j)
              plist.addBoolean((readVarint32() != 0)); 
            requirePosition(j);
            return;
          case 0:
            do {
              plist.addBoolean(readBool());
              if (isAtEnd())
                return; 
              k = this.pos;
              m = readVarint32();
            } while (m == this.tag);
            this.pos = k;
            return;
        } 
        throw InvalidProtocolBufferException.invalidWireType();
      } 
      switch (WireFormat.getTagWireType(this.tag)) {
        case 2:
          bytes = readVarint32();
          fieldEndPos = this.pos + bytes;
          while (this.pos < fieldEndPos)
            target.add(Boolean.valueOf((readVarint32() != 0))); 
          requirePosition(fieldEndPos);
          return;
        case 0:
          do {
            target.add(Boolean.valueOf(readBool()));
            if (isAtEnd())
              return; 
            prevPos = this.pos;
            nextTag = readVarint32();
          } while (nextTag == this.tag);
          this.pos = prevPos;
          return;
      } 
      throw InvalidProtocolBufferException.invalidWireType();
    }
    
    public void readStringList(List<String> target) throws IOException {
      readStringListInternal(target, false);
    }
    
    public void readStringListRequireUtf8(List<String> target) throws IOException {
      readStringListInternal(target, true);
    }
    
    public void readStringListInternal(List<String> target, boolean requireUtf8) throws IOException {
      int prevPos, nextTag;
      if (WireFormat.getTagWireType(this.tag) != 2)
        throw InvalidProtocolBufferException.invalidWireType(); 
      if (target instanceof LazyStringList && !requireUtf8) {
        int i, j;
        LazyStringList lazyList = (LazyStringList)target;
        do {
          lazyList.add(readBytes());
          if (isAtEnd())
            return; 
          i = this.pos;
          j = readVarint32();
        } while (j == this.tag);
        this.pos = i;
        return;
      } 
      do {
        target.add(readStringInternal(requireUtf8));
        if (isAtEnd())
          return; 
        prevPos = this.pos;
        nextTag = readVarint32();
      } while (nextTag == this.tag);
      this.pos = prevPos;
    }
    
    public <T> void readMessageList(List<T> target, Class<T> targetType, ExtensionRegistryLite extensionRegistry) throws IOException {
      Schema<T> schema = Protobuf.getInstance().schemaFor(targetType);
      readMessageList(target, schema, extensionRegistry);
    }
    
    public <T> void readMessageList(List<T> target, Schema<T> schema, ExtensionRegistryLite extensionRegistry) throws IOException {
      int prevPos, nextTag;
      if (WireFormat.getTagWireType(this.tag) != 2)
        throw InvalidProtocolBufferException.invalidWireType(); 
      int listTag = this.tag;
      do {
        target.add(readMessage(schema, extensionRegistry));
        if (isAtEnd())
          return; 
        prevPos = this.pos;
        nextTag = readVarint32();
      } while (nextTag == listTag);
      this.pos = prevPos;
    }
    
    @Deprecated
    public <T> void readGroupList(List<T> target, Class<T> targetType, ExtensionRegistryLite extensionRegistry) throws IOException {
      Schema<T> schema = Protobuf.getInstance().schemaFor(targetType);
      readGroupList(target, schema, extensionRegistry);
    }
    
    @Deprecated
    public <T> void readGroupList(List<T> target, Schema<T> schema, ExtensionRegistryLite extensionRegistry) throws IOException {
      int prevPos, nextTag;
      if (WireFormat.getTagWireType(this.tag) != 3)
        throw InvalidProtocolBufferException.invalidWireType(); 
      int listTag = this.tag;
      do {
        target.add(readGroup(schema, extensionRegistry));
        if (isAtEnd())
          return; 
        prevPos = this.pos;
        nextTag = readVarint32();
      } while (nextTag == listTag);
      this.pos = prevPos;
    }
    
    public void readBytesList(List<ByteString> target) throws IOException {
      int prevPos, nextTag;
      if (WireFormat.getTagWireType(this.tag) != 2)
        throw InvalidProtocolBufferException.invalidWireType(); 
      do {
        target.add(readBytes());
        if (isAtEnd())
          return; 
        prevPos = this.pos;
        nextTag = readVarint32();
      } while (nextTag == this.tag);
      this.pos = prevPos;
    }
    
    public void readUInt32List(List<Integer> target) throws IOException {
      int bytes;
      int fieldEndPos;
      int prevPos;
      int nextTag;
      if (target instanceof IntArrayList) {
        int i, j, k, m;
        IntArrayList plist = (IntArrayList)target;
        switch (WireFormat.getTagWireType(this.tag)) {
          case 2:
            i = readVarint32();
            j = this.pos + i;
            while (this.pos < j)
              plist.addInt(readVarint32()); 
            return;
          case 0:
            do {
              plist.addInt(readUInt32());
              if (isAtEnd())
                return; 
              k = this.pos;
              m = readVarint32();
            } while (m == this.tag);
            this.pos = k;
            return;
        } 
        throw InvalidProtocolBufferException.invalidWireType();
      } 
      switch (WireFormat.getTagWireType(this.tag)) {
        case 2:
          bytes = readVarint32();
          fieldEndPos = this.pos + bytes;
          while (this.pos < fieldEndPos)
            target.add(Integer.valueOf(readVarint32())); 
          return;
        case 0:
          do {
            target.add(Integer.valueOf(readUInt32()));
            if (isAtEnd())
              return; 
            prevPos = this.pos;
            nextTag = readVarint32();
          } while (nextTag == this.tag);
          this.pos = prevPos;
          return;
      } 
      throw InvalidProtocolBufferException.invalidWireType();
    }
    
    public void readEnumList(List<Integer> target) throws IOException {
      int bytes;
      int fieldEndPos;
      int prevPos;
      int nextTag;
      if (target instanceof IntArrayList) {
        int i, j, k, m;
        IntArrayList plist = (IntArrayList)target;
        switch (WireFormat.getTagWireType(this.tag)) {
          case 2:
            i = readVarint32();
            j = this.pos + i;
            while (this.pos < j)
              plist.addInt(readVarint32()); 
            return;
          case 0:
            do {
              plist.addInt(readEnum());
              if (isAtEnd())
                return; 
              k = this.pos;
              m = readVarint32();
            } while (m == this.tag);
            this.pos = k;
            return;
        } 
        throw InvalidProtocolBufferException.invalidWireType();
      } 
      switch (WireFormat.getTagWireType(this.tag)) {
        case 2:
          bytes = readVarint32();
          fieldEndPos = this.pos + bytes;
          while (this.pos < fieldEndPos)
            target.add(Integer.valueOf(readVarint32())); 
          return;
        case 0:
          do {
            target.add(Integer.valueOf(readEnum()));
            if (isAtEnd())
              return; 
            prevPos = this.pos;
            nextTag = readVarint32();
          } while (nextTag == this.tag);
          this.pos = prevPos;
          return;
      } 
      throw InvalidProtocolBufferException.invalidWireType();
    }
    
    public void readSFixed32List(List<Integer> target) throws IOException {
      int bytes;
      int fieldEndPos;
      int prevPos;
      int nextTag;
      if (target instanceof IntArrayList) {
        int i, j, k, m;
        IntArrayList plist = (IntArrayList)target;
        switch (WireFormat.getTagWireType(this.tag)) {
          case 2:
            i = readVarint32();
            verifyPackedFixed32Length(i);
            j = this.pos + i;
            while (this.pos < j)
              plist.addInt(readLittleEndian32_NoCheck()); 
            return;
          case 5:
            do {
              plist.addInt(readSFixed32());
              if (isAtEnd())
                return; 
              k = this.pos;
              m = readVarint32();
            } while (m == this.tag);
            this.pos = k;
            return;
        } 
        throw InvalidProtocolBufferException.invalidWireType();
      } 
      switch (WireFormat.getTagWireType(this.tag)) {
        case 2:
          bytes = readVarint32();
          verifyPackedFixed32Length(bytes);
          fieldEndPos = this.pos + bytes;
          while (this.pos < fieldEndPos)
            target.add(Integer.valueOf(readLittleEndian32_NoCheck())); 
          return;
        case 5:
          do {
            target.add(Integer.valueOf(readSFixed32()));
            if (isAtEnd())
              return; 
            prevPos = this.pos;
            nextTag = readVarint32();
          } while (nextTag == this.tag);
          this.pos = prevPos;
          return;
      } 
      throw InvalidProtocolBufferException.invalidWireType();
    }
    
    public void readSFixed64List(List<Long> target) throws IOException {
      int bytes;
      int fieldEndPos;
      int prevPos;
      int nextTag;
      if (target instanceof LongArrayList) {
        int i, j, k, m;
        LongArrayList plist = (LongArrayList)target;
        switch (WireFormat.getTagWireType(this.tag)) {
          case 2:
            i = readVarint32();
            verifyPackedFixed64Length(i);
            j = this.pos + i;
            while (this.pos < j)
              plist.addLong(readLittleEndian64_NoCheck()); 
            return;
          case 1:
            do {
              plist.addLong(readSFixed64());
              if (isAtEnd())
                return; 
              k = this.pos;
              m = readVarint32();
            } while (m == this.tag);
            this.pos = k;
            return;
        } 
        throw InvalidProtocolBufferException.invalidWireType();
      } 
      switch (WireFormat.getTagWireType(this.tag)) {
        case 2:
          bytes = readVarint32();
          verifyPackedFixed64Length(bytes);
          fieldEndPos = this.pos + bytes;
          while (this.pos < fieldEndPos)
            target.add(Long.valueOf(readLittleEndian64_NoCheck())); 
          return;
        case 1:
          do {
            target.add(Long.valueOf(readSFixed64()));
            if (isAtEnd())
              return; 
            prevPos = this.pos;
            nextTag = readVarint32();
          } while (nextTag == this.tag);
          this.pos = prevPos;
          return;
      } 
      throw InvalidProtocolBufferException.invalidWireType();
    }
    
    public void readSInt32List(List<Integer> target) throws IOException {
      int bytes;
      int fieldEndPos;
      int prevPos;
      int nextTag;
      if (target instanceof IntArrayList) {
        int i, j, k, m;
        IntArrayList plist = (IntArrayList)target;
        switch (WireFormat.getTagWireType(this.tag)) {
          case 2:
            i = readVarint32();
            j = this.pos + i;
            while (this.pos < j)
              plist.addInt(CodedInputStream.decodeZigZag32(readVarint32())); 
            return;
          case 0:
            do {
              plist.addInt(readSInt32());
              if (isAtEnd())
                return; 
              k = this.pos;
              m = readVarint32();
            } while (m == this.tag);
            this.pos = k;
            return;
        } 
        throw InvalidProtocolBufferException.invalidWireType();
      } 
      switch (WireFormat.getTagWireType(this.tag)) {
        case 2:
          bytes = readVarint32();
          fieldEndPos = this.pos + bytes;
          while (this.pos < fieldEndPos)
            target.add(Integer.valueOf(CodedInputStream.decodeZigZag32(readVarint32()))); 
          return;
        case 0:
          do {
            target.add(Integer.valueOf(readSInt32()));
            if (isAtEnd())
              return; 
            prevPos = this.pos;
            nextTag = readVarint32();
          } while (nextTag == this.tag);
          this.pos = prevPos;
          return;
      } 
      throw InvalidProtocolBufferException.invalidWireType();
    }
    
    public void readSInt64List(List<Long> target) throws IOException {
      int bytes;
      int fieldEndPos;
      int prevPos;
      int nextTag;
      if (target instanceof LongArrayList) {
        int i, j, k, m;
        LongArrayList plist = (LongArrayList)target;
        switch (WireFormat.getTagWireType(this.tag)) {
          case 2:
            i = readVarint32();
            j = this.pos + i;
            while (this.pos < j)
              plist.addLong(CodedInputStream.decodeZigZag64(readVarint64())); 
            return;
          case 0:
            do {
              plist.addLong(readSInt64());
              if (isAtEnd())
                return; 
              k = this.pos;
              m = readVarint32();
            } while (m == this.tag);
            this.pos = k;
            return;
        } 
        throw InvalidProtocolBufferException.invalidWireType();
      } 
      switch (WireFormat.getTagWireType(this.tag)) {
        case 2:
          bytes = readVarint32();
          fieldEndPos = this.pos + bytes;
          while (this.pos < fieldEndPos)
            target.add(Long.valueOf(CodedInputStream.decodeZigZag64(readVarint64()))); 
          return;
        case 0:
          do {
            target.add(Long.valueOf(readSInt64()));
            if (isAtEnd())
              return; 
            prevPos = this.pos;
            nextTag = readVarint32();
          } while (nextTag == this.tag);
          this.pos = prevPos;
          return;
      } 
      throw InvalidProtocolBufferException.invalidWireType();
    }
    
    public <K, V> void readMap(Map<K, V> target, MapEntryLite.Metadata<K, V> metadata, ExtensionRegistryLite extensionRegistry) throws IOException {
      requireWireType(2);
      int size = readVarint32();
      requireBytes(size);
      int prevLimit = this.limit;
      int newLimit = this.pos + size;
      this.limit = newLimit;
      try {
        K key = metadata.defaultKey;
        V value = metadata.defaultValue;
        while (true) {
          int number = getFieldNumber();
          if (number == Integer.MAX_VALUE)
            break; 
          try {
            switch (number) {
              case 1:
                key = (K)readField(metadata.keyType, null, null);
                continue;
              case 2:
                value = (V)readField(metadata.valueType, metadata.defaultValue
                    
                    .getClass(), extensionRegistry);
                continue;
            } 
            if (!skipField())
              throw new InvalidProtocolBufferException("Unable to parse map entry."); 
          } catch (InvalidWireTypeException ignore) {
            if (!skipField())
              throw new InvalidProtocolBufferException("Unable to parse map entry."); 
          } 
        } 
        target.put(key, value);
      } finally {
        this.limit = prevLimit;
      } 
    }
    
    private Object readField(WireFormat.FieldType fieldType, Class<?> messageType, ExtensionRegistryLite extensionRegistry) throws IOException {
      switch (fieldType) {
        case BOOL:
          return Boolean.valueOf(readBool());
        case BYTES:
          return readBytes();
        case DOUBLE:
          return Double.valueOf(readDouble());
        case ENUM:
          return Integer.valueOf(readEnum());
        case FIXED32:
          return Integer.valueOf(readFixed32());
        case FIXED64:
          return Long.valueOf(readFixed64());
        case FLOAT:
          return Float.valueOf(readFloat());
        case INT32:
          return Integer.valueOf(readInt32());
        case INT64:
          return Long.valueOf(readInt64());
        case MESSAGE:
          return readMessage(messageType, extensionRegistry);
        case SFIXED32:
          return Integer.valueOf(readSFixed32());
        case SFIXED64:
          return Long.valueOf(readSFixed64());
        case SINT32:
          return Integer.valueOf(readSInt32());
        case SINT64:
          return Long.valueOf(readSInt64());
        case STRING:
          return readStringRequireUtf8();
        case UINT32:
          return Integer.valueOf(readUInt32());
        case UINT64:
          return Long.valueOf(readUInt64());
      } 
      throw new RuntimeException("unsupported field type.");
    }
    
    private int readVarint32() throws IOException {
      int i = this.pos;
      if (this.limit == this.pos)
        throw InvalidProtocolBufferException.truncatedMessage(); 
      int x;
      if ((x = this.buffer[i++]) >= 0) {
        this.pos = i;
        return x;
      } 
      if (this.limit - i < 9)
        return (int)readVarint64SlowPath(); 
      if ((x ^= this.buffer[i++] << 7) < 0) {
        x ^= 0xFFFFFF80;
      } else if ((x ^= this.buffer[i++] << 14) >= 0) {
        x ^= 0x3F80;
      } else if ((x ^= this.buffer[i++] << 21) < 0) {
        x ^= 0xFFE03F80;
      } else {
        int y = this.buffer[i++];
        x ^= y << 28;
        x ^= 0xFE03F80;
        if (y < 0 && this.buffer[i++] < 0 && this.buffer[i++] < 0 && this.buffer[i++] < 0 && this.buffer[i++] < 0 && this.buffer[i++] < 0)
          throw InvalidProtocolBufferException.malformedVarint(); 
      } 
      this.pos = i;
      return x;
    }
    
    public long readVarint64() throws IOException {
      long x;
      int i = this.pos;
      if (this.limit == i)
        throw InvalidProtocolBufferException.truncatedMessage(); 
      byte[] buffer = this.buffer;
      int y;
      if ((y = buffer[i++]) >= 0) {
        this.pos = i;
        return y;
      } 
      if (this.limit - i < 9)
        return readVarint64SlowPath(); 
      if ((y ^= buffer[i++] << 7) < 0) {
        x = (y ^ 0xFFFFFF80);
      } else if ((y ^= buffer[i++] << 14) >= 0) {
        x = (y ^ 0x3F80);
      } else if ((y ^= buffer[i++] << 21) < 0) {
        x = (y ^ 0xFFE03F80);
      } else if ((x = y ^ buffer[i++] << 28L) >= 0L) {
        x ^= 0xFE03F80L;
      } else if ((x ^= buffer[i++] << 35L) < 0L) {
        x ^= 0xFFFFFFF80FE03F80L;
      } else if ((x ^= buffer[i++] << 42L) >= 0L) {
        x ^= 0x3F80FE03F80L;
      } else if ((x ^= buffer[i++] << 49L) < 0L) {
        x ^= 0xFFFE03F80FE03F80L;
      } else {
        x ^= buffer[i++] << 56L;
        x ^= 0xFE03F80FE03F80L;
        if (x < 0L && 
          buffer[i++] < 0L)
          throw InvalidProtocolBufferException.malformedVarint(); 
      } 
      this.pos = i;
      return x;
    }
    
    private long readVarint64SlowPath() throws IOException {
      long result = 0L;
      for (int shift = 0; shift < 64; shift += 7) {
        byte b = readByte();
        result |= (b & Byte.MAX_VALUE) << shift;
        if ((b & 0x80) == 0)
          return result; 
      } 
      throw InvalidProtocolBufferException.malformedVarint();
    }
    
    private byte readByte() throws IOException {
      if (this.pos == this.limit)
        throw InvalidProtocolBufferException.truncatedMessage(); 
      return this.buffer[this.pos++];
    }
    
    private int readLittleEndian32() throws IOException {
      requireBytes(4);
      return readLittleEndian32_NoCheck();
    }
    
    private long readLittleEndian64() throws IOException {
      requireBytes(8);
      return readLittleEndian64_NoCheck();
    }
    
    private int readLittleEndian32_NoCheck() {
      int p = this.pos;
      byte[] buffer = this.buffer;
      this.pos = p + 4;
      return buffer[p] & 0xFF | (buffer[p + 1] & 0xFF) << 8 | (buffer[p + 2] & 0xFF) << 16 | (buffer[p + 3] & 0xFF) << 24;
    }
    
    private long readLittleEndian64_NoCheck() {
      int p = this.pos;
      byte[] buffer = this.buffer;
      this.pos = p + 8;
      return buffer[p] & 0xFFL | (buffer[p + 1] & 0xFFL) << 8L | (buffer[p + 2] & 0xFFL) << 16L | (buffer[p + 3] & 0xFFL) << 24L | (buffer[p + 4] & 0xFFL) << 32L | (buffer[p + 5] & 0xFFL) << 40L | (buffer[p + 6] & 0xFFL) << 48L | (buffer[p + 7] & 0xFFL) << 56L;
    }
    
    private void skipVarint() throws IOException {
      if (this.limit - this.pos >= 10) {
        byte[] buffer = this.buffer;
        int p = this.pos;
        for (int i = 0; i < 10; i++) {
          if (buffer[p++] >= 0) {
            this.pos = p;
            return;
          } 
        } 
      } 
      skipVarintSlowPath();
    }
    
    private void skipVarintSlowPath() throws IOException {
      for (int i = 0; i < 10; i++) {
        if (readByte() >= 0)
          return; 
      } 
      throw InvalidProtocolBufferException.malformedVarint();
    }
    
    private void skipBytes(int size) throws IOException {
      requireBytes(size);
      this.pos += size;
    }
    
    private void skipGroup() throws IOException {
      int prevEndGroupTag = this.endGroupTag;
      this.endGroupTag = WireFormat.makeTag(WireFormat.getTagFieldNumber(this.tag), 4);
      do {
      
      } while (getFieldNumber() != Integer.MAX_VALUE && skipField());
      if (this.tag != this.endGroupTag)
        throw InvalidProtocolBufferException.parseFailure(); 
      this.endGroupTag = prevEndGroupTag;
    }
    
    private void requireBytes(int size) throws IOException {
      if (size < 0 || size > this.limit - this.pos)
        throw InvalidProtocolBufferException.truncatedMessage(); 
    }
    
    private void requireWireType(int requiredWireType) throws IOException {
      if (WireFormat.getTagWireType(this.tag) != requiredWireType)
        throw InvalidProtocolBufferException.invalidWireType(); 
    }
    
    private void verifyPackedFixed64Length(int bytes) throws IOException {
      requireBytes(bytes);
      if ((bytes & 0x7) != 0)
        throw InvalidProtocolBufferException.parseFailure(); 
    }
    
    private void verifyPackedFixed32Length(int bytes) throws IOException {
      requireBytes(bytes);
      if ((bytes & 0x3) != 0)
        throw InvalidProtocolBufferException.parseFailure(); 
    }
    
    private void requirePosition(int expectedPosition) throws IOException {
      if (this.pos != expectedPosition)
        throw InvalidProtocolBufferException.truncatedMessage(); 
    }
  }
}
