package com.google.protobuf;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

public abstract class CodedInputStream {
  private static final int DEFAULT_BUFFER_SIZE = 4096;
  
  private static final int DEFAULT_SIZE_LIMIT = 2147483647;
  
  private static volatile int defaultRecursionLimit = 100;
  
  int recursionDepth;
  
  int recursionLimit = defaultRecursionLimit;
  
  int sizeLimit = Integer.MAX_VALUE;
  
  CodedInputStreamReader wrapper;
  
  private boolean shouldDiscardUnknownFields;
  
  public static CodedInputStream newInstance(InputStream input) {
    return newInstance(input, 4096);
  }
  
  public static CodedInputStream newInstance(InputStream input, int bufferSize) {
    if (bufferSize <= 0)
      throw new IllegalArgumentException("bufferSize must be > 0"); 
    if (input == null)
      return newInstance(Internal.EMPTY_BYTE_ARRAY); 
    return new StreamDecoder(input, bufferSize);
  }
  
  public static CodedInputStream newInstance(Iterable<ByteBuffer> input) {
    if (!UnsafeDirectNioDecoder.isSupported())
      return newInstance(new IterableByteBufferInputStream(input)); 
    return newInstance(input, false);
  }
  
  static CodedInputStream newInstance(Iterable<ByteBuffer> bufs, boolean bufferIsImmutable) {
    int flag = 0;
    int totalSize = 0;
    for (ByteBuffer buf : bufs) {
      totalSize += buf.remaining();
      if (buf.hasArray()) {
        flag |= 0x1;
        continue;
      } 
      if (buf.isDirect()) {
        flag |= 0x2;
        continue;
      } 
      flag |= 0x4;
    } 
    if (flag == 2)
      return new IterableDirectByteBufferDecoder(bufs, totalSize, bufferIsImmutable); 
    return newInstance(new IterableByteBufferInputStream(bufs));
  }
  
  public static CodedInputStream newInstance(byte[] buf) {
    return newInstance(buf, 0, buf.length);
  }
  
  public static CodedInputStream newInstance(byte[] buf, int off, int len) {
    return newInstance(buf, off, len, false);
  }
  
  static CodedInputStream newInstance(byte[] buf, int off, int len, boolean bufferIsImmutable) {
    ArrayDecoder result = new ArrayDecoder(buf, off, len, bufferIsImmutable);
    try {
      result.pushLimit(len);
    } catch (InvalidProtocolBufferException ex) {
      throw new IllegalArgumentException(ex);
    } 
    return result;
  }
  
  public static CodedInputStream newInstance(ByteBuffer buf) {
    return newInstance(buf, false);
  }
  
  static CodedInputStream newInstance(ByteBuffer buf, boolean bufferIsImmutable) {
    if (buf.hasArray())
      return newInstance(buf
          .array(), buf.arrayOffset() + buf.position(), buf.remaining(), bufferIsImmutable); 
    if (buf.isDirect() && UnsafeDirectNioDecoder.isSupported())
      return new UnsafeDirectNioDecoder(buf, bufferIsImmutable); 
    byte[] buffer = new byte[buf.remaining()];
    buf.duplicate().get(buffer);
    return newInstance(buffer, 0, buffer.length, true);
  }
  
  public void checkRecursionLimit() throws InvalidProtocolBufferException {
    if (this.recursionDepth >= this.recursionLimit)
      throw InvalidProtocolBufferException.recursionLimitExceeded(); 
  }
  
  public final int setRecursionLimit(int limit) {
    if (limit < 0)
      throw new IllegalArgumentException("Recursion limit cannot be negative: " + limit); 
    int oldLimit = this.recursionLimit;
    this.recursionLimit = limit;
    return oldLimit;
  }
  
  public final int setSizeLimit(int limit) {
    if (limit < 0)
      throw new IllegalArgumentException("Size limit cannot be negative: " + limit); 
    int oldLimit = this.sizeLimit;
    this.sizeLimit = limit;
    return oldLimit;
  }
  
  private CodedInputStream() {
    this.shouldDiscardUnknownFields = false;
  }
  
  final void discardUnknownFields() {
    this.shouldDiscardUnknownFields = true;
  }
  
  final void unsetDiscardUnknownFields() {
    this.shouldDiscardUnknownFields = false;
  }
  
  final boolean shouldDiscardUnknownFields() {
    return this.shouldDiscardUnknownFields;
  }
  
  public static int decodeZigZag32(int n) {
    return n >>> 1 ^ -(n & 0x1);
  }
  
  public static long decodeZigZag64(long n) {
    return n >>> 1L ^ -(n & 0x1L);
  }
  
  public static int readRawVarint32(int firstByte, InputStream input) throws IOException {
    if ((firstByte & 0x80) == 0)
      return firstByte; 
    int result = firstByte & 0x7F;
    int offset = 7;
    for (; offset < 32; offset += 7) {
      int b = input.read();
      if (b == -1)
        throw InvalidProtocolBufferException.truncatedMessage(); 
      result |= (b & 0x7F) << offset;
      if ((b & 0x80) == 0)
        return result; 
    } 
    for (; offset < 64; offset += 7) {
      int b = input.read();
      if (b == -1)
        throw InvalidProtocolBufferException.truncatedMessage(); 
      if ((b & 0x80) == 0)
        return result; 
    } 
    throw InvalidProtocolBufferException.malformedVarint();
  }
  
  static int readRawVarint32(InputStream input) throws IOException {
    int firstByte = input.read();
    if (firstByte == -1)
      throw InvalidProtocolBufferException.truncatedMessage(); 
    return readRawVarint32(firstByte, input);
  }
  
  public abstract int readTag() throws IOException;
  
  public abstract void checkLastTagWas(int paramInt) throws InvalidProtocolBufferException;
  
  public abstract int getLastTag();
  
  public abstract boolean skipField(int paramInt) throws IOException;
  
  @Deprecated
  public abstract boolean skipField(int paramInt, CodedOutputStream paramCodedOutputStream) throws IOException;
  
  public abstract void skipMessage() throws IOException;
  
  public abstract void skipMessage(CodedOutputStream paramCodedOutputStream) throws IOException;
  
  public abstract double readDouble() throws IOException;
  
  public abstract float readFloat() throws IOException;
  
  public abstract long readUInt64() throws IOException;
  
  public abstract long readInt64() throws IOException;
  
  public abstract int readInt32() throws IOException;
  
  public abstract long readFixed64() throws IOException;
  
  public abstract int readFixed32() throws IOException;
  
  public abstract boolean readBool() throws IOException;
  
  public abstract String readString() throws IOException;
  
  public abstract String readStringRequireUtf8() throws IOException;
  
  public abstract void readGroup(int paramInt, MessageLite.Builder paramBuilder, ExtensionRegistryLite paramExtensionRegistryLite) throws IOException;
  
  public abstract <T extends MessageLite> T readGroup(int paramInt, Parser<T> paramParser, ExtensionRegistryLite paramExtensionRegistryLite) throws IOException;
  
  @Deprecated
  public abstract void readUnknownGroup(int paramInt, MessageLite.Builder paramBuilder) throws IOException;
  
  public abstract void readMessage(MessageLite.Builder paramBuilder, ExtensionRegistryLite paramExtensionRegistryLite) throws IOException;
  
  public abstract <T extends MessageLite> T readMessage(Parser<T> paramParser, ExtensionRegistryLite paramExtensionRegistryLite) throws IOException;
  
  public abstract ByteString readBytes() throws IOException;
  
  public abstract byte[] readByteArray() throws IOException;
  
  public abstract ByteBuffer readByteBuffer() throws IOException;
  
  public abstract int readUInt32() throws IOException;
  
  public abstract int readEnum() throws IOException;
  
  public abstract int readSFixed32() throws IOException;
  
  public abstract long readSFixed64() throws IOException;
  
  public abstract int readSInt32() throws IOException;
  
  public abstract long readSInt64() throws IOException;
  
  public abstract int readRawVarint32() throws IOException;
  
  public abstract long readRawVarint64() throws IOException;
  
  abstract long readRawVarint64SlowPath() throws IOException;
  
  public abstract int readRawLittleEndian32() throws IOException;
  
  public abstract long readRawLittleEndian64() throws IOException;
  
  public abstract void enableAliasing(boolean paramBoolean);
  
  public abstract void resetSizeCounter();
  
  public abstract int pushLimit(int paramInt) throws InvalidProtocolBufferException;
  
  public abstract void popLimit(int paramInt);
  
  public abstract int getBytesUntilLimit();
  
  public abstract boolean isAtEnd() throws IOException;
  
  public abstract int getTotalBytesRead();
  
  public abstract byte readRawByte() throws IOException;
  
  public abstract byte[] readRawBytes(int paramInt) throws IOException;
  
  public abstract void skipRawBytes(int paramInt) throws IOException;
  
  private static final class ArrayDecoder extends CodedInputStream {
    private final byte[] buffer;
    
    private final boolean immutable;
    
    private int limit;
    
    private int bufferSizeAfterLimit;
    
    private int pos;
    
    private int startPos;
    
    private int lastTag;
    
    private boolean enableAliasing;
    
    private int currentLimit = Integer.MAX_VALUE;
    
    private ArrayDecoder(byte[] buffer, int offset, int len, boolean immutable) {
      this.buffer = buffer;
      this.limit = offset + len;
      this.pos = offset;
      this.startPos = this.pos;
      this.immutable = immutable;
    }
    
    public int readTag() throws IOException {
      if (isAtEnd()) {
        this.lastTag = 0;
        return 0;
      } 
      this.lastTag = readRawVarint32();
      if (WireFormat.getTagFieldNumber(this.lastTag) == 0)
        throw InvalidProtocolBufferException.invalidTag(); 
      return this.lastTag;
    }
    
    public void checkLastTagWas(int value) throws InvalidProtocolBufferException {
      if (this.lastTag != value)
        throw InvalidProtocolBufferException.invalidEndTag(); 
    }
    
    public int getLastTag() {
      return this.lastTag;
    }
    
    public boolean skipField(int tag) throws IOException {
      switch (WireFormat.getTagWireType(tag)) {
        case 0:
          skipRawVarint();
          return true;
        case 1:
          skipRawBytes(8);
          return true;
        case 2:
          skipRawBytes(readRawVarint32());
          return true;
        case 3:
          skipMessage();
          checkLastTagWas(
              WireFormat.makeTag(WireFormat.getTagFieldNumber(tag), 4));
          return true;
        case 4:
          return false;
        case 5:
          skipRawBytes(4);
          return true;
      } 
      throw InvalidProtocolBufferException.invalidWireType();
    }
    
    public boolean skipField(int tag, CodedOutputStream output) throws IOException {
      long l;
      ByteString byteString;
      int endtag;
      int value;
      switch (WireFormat.getTagWireType(tag)) {
        case 0:
          l = readInt64();
          output.writeUInt32NoTag(tag);
          output.writeUInt64NoTag(l);
          return true;
        case 1:
          l = readRawLittleEndian64();
          output.writeUInt32NoTag(tag);
          output.writeFixed64NoTag(l);
          return true;
        case 2:
          byteString = readBytes();
          output.writeUInt32NoTag(tag);
          output.writeBytesNoTag(byteString);
          return true;
        case 3:
          output.writeUInt32NoTag(tag);
          skipMessage(output);
          endtag = WireFormat.makeTag(
              WireFormat.getTagFieldNumber(tag), 4);
          checkLastTagWas(endtag);
          output.writeUInt32NoTag(endtag);
          return true;
        case 4:
          return false;
        case 5:
          value = readRawLittleEndian32();
          output.writeUInt32NoTag(tag);
          output.writeFixed32NoTag(value);
          return true;
      } 
      throw InvalidProtocolBufferException.invalidWireType();
    }
    
    public void skipMessage() throws IOException {
      int tag;
      do {
        tag = readTag();
      } while (tag != 0 && skipField(tag));
    }
    
    public void skipMessage(CodedOutputStream output) throws IOException {
      int tag;
      do {
        tag = readTag();
      } while (tag != 0 && skipField(tag, output));
    }
    
    public double readDouble() throws IOException {
      return Double.longBitsToDouble(readRawLittleEndian64());
    }
    
    public float readFloat() throws IOException {
      return Float.intBitsToFloat(readRawLittleEndian32());
    }
    
    public long readUInt64() throws IOException {
      return readRawVarint64();
    }
    
    public long readInt64() throws IOException {
      return readRawVarint64();
    }
    
    public int readInt32() throws IOException {
      return readRawVarint32();
    }
    
    public long readFixed64() throws IOException {
      return readRawLittleEndian64();
    }
    
    public int readFixed32() throws IOException {
      return readRawLittleEndian32();
    }
    
    public boolean readBool() throws IOException {
      return (readRawVarint64() != 0L);
    }
    
    public String readString() throws IOException {
      int size = readRawVarint32();
      if (size > 0 && size <= this.limit - this.pos) {
        String result = new String(this.buffer, this.pos, size, Internal.UTF_8);
        this.pos += size;
        return result;
      } 
      if (size == 0)
        return ""; 
      if (size < 0)
        throw InvalidProtocolBufferException.negativeSize(); 
      throw InvalidProtocolBufferException.truncatedMessage();
    }
    
    public String readStringRequireUtf8() throws IOException {
      int size = readRawVarint32();
      if (size > 0 && size <= this.limit - this.pos) {
        String result = Utf8.decodeUtf8(this.buffer, this.pos, size);
        this.pos += size;
        return result;
      } 
      if (size == 0)
        return ""; 
      if (size <= 0)
        throw InvalidProtocolBufferException.negativeSize(); 
      throw InvalidProtocolBufferException.truncatedMessage();
    }
    
    public void readGroup(int fieldNumber, MessageLite.Builder builder, ExtensionRegistryLite extensionRegistry) throws IOException {
      checkRecursionLimit();
      this.recursionDepth++;
      builder.mergeFrom(this, extensionRegistry);
      checkLastTagWas(WireFormat.makeTag(fieldNumber, 4));
      this.recursionDepth--;
    }
    
    public <T extends MessageLite> T readGroup(int fieldNumber, Parser<T> parser, ExtensionRegistryLite extensionRegistry) throws IOException {
      checkRecursionLimit();
      this.recursionDepth++;
      MessageLite messageLite = (MessageLite)parser.parsePartialFrom(this, extensionRegistry);
      checkLastTagWas(WireFormat.makeTag(fieldNumber, 4));
      this.recursionDepth--;
      return (T)messageLite;
    }
    
    @Deprecated
    public void readUnknownGroup(int fieldNumber, MessageLite.Builder builder) throws IOException {
      readGroup(fieldNumber, builder, ExtensionRegistryLite.getEmptyRegistry());
    }
    
    public void readMessage(MessageLite.Builder builder, ExtensionRegistryLite extensionRegistry) throws IOException {
      int length = readRawVarint32();
      checkRecursionLimit();
      int oldLimit = pushLimit(length);
      this.recursionDepth++;
      builder.mergeFrom(this, extensionRegistry);
      checkLastTagWas(0);
      this.recursionDepth--;
      if (getBytesUntilLimit() != 0)
        throw InvalidProtocolBufferException.truncatedMessage(); 
      popLimit(oldLimit);
    }
    
    public <T extends MessageLite> T readMessage(Parser<T> parser, ExtensionRegistryLite extensionRegistry) throws IOException {
      int length = readRawVarint32();
      checkRecursionLimit();
      int oldLimit = pushLimit(length);
      this.recursionDepth++;
      MessageLite messageLite = (MessageLite)parser.parsePartialFrom(this, extensionRegistry);
      checkLastTagWas(0);
      this.recursionDepth--;
      if (getBytesUntilLimit() != 0)
        throw InvalidProtocolBufferException.truncatedMessage(); 
      popLimit(oldLimit);
      return (T)messageLite;
    }
    
    public ByteString readBytes() throws IOException {
      int size = readRawVarint32();
      if (size > 0 && size <= this.limit - this.pos) {
        ByteString result = (this.immutable && this.enableAliasing) ? ByteString.wrap(this.buffer, this.pos, size) : ByteString.copyFrom(this.buffer, this.pos, size);
        this.pos += size;
        return result;
      } 
      if (size == 0)
        return ByteString.EMPTY; 
      return ByteString.wrap(readRawBytes(size));
    }
    
    public byte[] readByteArray() throws IOException {
      int size = readRawVarint32();
      return readRawBytes(size);
    }
    
    public ByteBuffer readByteBuffer() throws IOException {
      int size = readRawVarint32();
      if (size > 0 && size <= this.limit - this.pos) {
        ByteBuffer result = (!this.immutable && this.enableAliasing) ? ByteBuffer.wrap(this.buffer, this.pos, size).slice() : ByteBuffer.wrap(Arrays.copyOfRange(this.buffer, this.pos, this.pos + size));
        this.pos += size;
        return result;
      } 
      if (size == 0)
        return Internal.EMPTY_BYTE_BUFFER; 
      if (size < 0)
        throw InvalidProtocolBufferException.negativeSize(); 
      throw InvalidProtocolBufferException.truncatedMessage();
    }
    
    public int readUInt32() throws IOException {
      return readRawVarint32();
    }
    
    public int readEnum() throws IOException {
      return readRawVarint32();
    }
    
    public int readSFixed32() throws IOException {
      return readRawLittleEndian32();
    }
    
    public long readSFixed64() throws IOException {
      return readRawLittleEndian64();
    }
    
    public int readSInt32() throws IOException {
      return decodeZigZag32(readRawVarint32());
    }
    
    public long readSInt64() throws IOException {
      return decodeZigZag64(readRawVarint64());
    }
    
    public int readRawVarint32() throws IOException {
      int tempPos = this.pos;
      if (this.limit != tempPos) {
        byte[] buffer = this.buffer;
        int x;
        if ((x = buffer[tempPos++]) >= 0) {
          this.pos = tempPos;
          return x;
        } 
        if (this.limit - tempPos >= 9) {
          if ((x ^= buffer[tempPos++] << 7) < 0) {
            x ^= 0xFFFFFF80;
          } else if ((x ^= buffer[tempPos++] << 14) >= 0) {
            x ^= 0x3F80;
          } else if ((x ^= buffer[tempPos++] << 21) < 0) {
            x ^= 0xFFE03F80;
          } else {
            int y = buffer[tempPos++];
            x ^= y << 28;
            x ^= 0xFE03F80;
            if (y < 0 && buffer[tempPos++] < 0 && buffer[tempPos++] < 0 && buffer[tempPos++] < 0 && buffer[tempPos++] < 0 && buffer[tempPos++] < 0)
              return (int)readRawVarint64SlowPath(); 
          } 
          this.pos = tempPos;
          return x;
        } 
      } 
      return (int)readRawVarint64SlowPath();
    }
    
    private void skipRawVarint() throws IOException {
      if (this.limit - this.pos >= 10) {
        skipRawVarintFastPath();
      } else {
        skipRawVarintSlowPath();
      } 
    }
    
    private void skipRawVarintFastPath() throws IOException {
      for (int i = 0; i < 10; i++) {
        if (this.buffer[this.pos++] >= 0)
          return; 
      } 
      throw InvalidProtocolBufferException.malformedVarint();
    }
    
    private void skipRawVarintSlowPath() throws IOException {
      for (int i = 0; i < 10; i++) {
        if (readRawByte() >= 0)
          return; 
      } 
      throw InvalidProtocolBufferException.malformedVarint();
    }
    
    public long readRawVarint64() throws IOException {
      int tempPos = this.pos;
      if (this.limit != tempPos) {
        byte[] buffer = this.buffer;
        int y;
        if ((y = buffer[tempPos++]) >= 0) {
          this.pos = tempPos;
          return y;
        } 
        if (this.limit - tempPos >= 9) {
          long x;
          if ((y ^= buffer[tempPos++] << 7) < 0) {
            x = (y ^ 0xFFFFFF80);
          } else if ((y ^= buffer[tempPos++] << 14) >= 0) {
            x = (y ^ 0x3F80);
          } else if ((y ^= buffer[tempPos++] << 21) < 0) {
            x = (y ^ 0xFFE03F80);
          } else if ((x = y ^ buffer[tempPos++] << 28L) >= 0L) {
            x ^= 0xFE03F80L;
          } else if ((x ^= buffer[tempPos++] << 35L) < 0L) {
            x ^= 0xFFFFFFF80FE03F80L;
          } else if ((x ^= buffer[tempPos++] << 42L) >= 0L) {
            x ^= 0x3F80FE03F80L;
          } else if ((x ^= buffer[tempPos++] << 49L) < 0L) {
            x ^= 0xFFFE03F80FE03F80L;
          } else {
            x ^= buffer[tempPos++] << 56L;
            x ^= 0xFE03F80FE03F80L;
            if (x < 0L && 
              buffer[tempPos++] < 0L)
              return readRawVarint64SlowPath(); 
          } 
          this.pos = tempPos;
          return x;
        } 
      } 
      return readRawVarint64SlowPath();
    }
    
    long readRawVarint64SlowPath() throws IOException {
      long result = 0L;
      for (int shift = 0; shift < 64; shift += 7) {
        byte b = readRawByte();
        result |= (b & Byte.MAX_VALUE) << shift;
        if ((b & 0x80) == 0)
          return result; 
      } 
      throw InvalidProtocolBufferException.malformedVarint();
    }
    
    public int readRawLittleEndian32() throws IOException {
      int tempPos = this.pos;
      if (this.limit - tempPos < 4)
        throw InvalidProtocolBufferException.truncatedMessage(); 
      byte[] buffer = this.buffer;
      this.pos = tempPos + 4;
      return buffer[tempPos] & 0xFF | (buffer[tempPos + 1] & 0xFF) << 8 | (buffer[tempPos + 2] & 0xFF) << 16 | (buffer[tempPos + 3] & 0xFF) << 24;
    }
    
    public long readRawLittleEndian64() throws IOException {
      int tempPos = this.pos;
      if (this.limit - tempPos < 8)
        throw InvalidProtocolBufferException.truncatedMessage(); 
      byte[] buffer = this.buffer;
      this.pos = tempPos + 8;
      return buffer[tempPos] & 0xFFL | (buffer[tempPos + 1] & 0xFFL) << 8L | (buffer[tempPos + 2] & 0xFFL) << 16L | (buffer[tempPos + 3] & 0xFFL) << 24L | (buffer[tempPos + 4] & 0xFFL) << 32L | (buffer[tempPos + 5] & 0xFFL) << 40L | (buffer[tempPos + 6] & 0xFFL) << 48L | (buffer[tempPos + 7] & 0xFFL) << 56L;
    }
    
    public void enableAliasing(boolean enabled) {
      this.enableAliasing = enabled;
    }
    
    public void resetSizeCounter() {
      this.startPos = this.pos;
    }
    
    public int pushLimit(int byteLimit) throws InvalidProtocolBufferException {
      if (byteLimit < 0)
        throw InvalidProtocolBufferException.negativeSize(); 
      byteLimit += getTotalBytesRead();
      if (byteLimit < 0)
        throw InvalidProtocolBufferException.parseFailure(); 
      int oldLimit = this.currentLimit;
      if (byteLimit > oldLimit)
        throw InvalidProtocolBufferException.truncatedMessage(); 
      this.currentLimit = byteLimit;
      recomputeBufferSizeAfterLimit();
      return oldLimit;
    }
    
    private void recomputeBufferSizeAfterLimit() {
      this.limit += this.bufferSizeAfterLimit;
      int bufferEnd = this.limit - this.startPos;
      if (bufferEnd > this.currentLimit) {
        this.bufferSizeAfterLimit = bufferEnd - this.currentLimit;
        this.limit -= this.bufferSizeAfterLimit;
      } else {
        this.bufferSizeAfterLimit = 0;
      } 
    }
    
    public void popLimit(int oldLimit) {
      this.currentLimit = oldLimit;
      recomputeBufferSizeAfterLimit();
    }
    
    public int getBytesUntilLimit() {
      if (this.currentLimit == Integer.MAX_VALUE)
        return -1; 
      return this.currentLimit - getTotalBytesRead();
    }
    
    public boolean isAtEnd() throws IOException {
      return (this.pos == this.limit);
    }
    
    public int getTotalBytesRead() {
      return this.pos - this.startPos;
    }
    
    public byte readRawByte() throws IOException {
      if (this.pos == this.limit)
        throw InvalidProtocolBufferException.truncatedMessage(); 
      return this.buffer[this.pos++];
    }
    
    public byte[] readRawBytes(int length) throws IOException {
      if (length > 0 && length <= this.limit - this.pos) {
        int tempPos = this.pos;
        this.pos += length;
        return Arrays.copyOfRange(this.buffer, tempPos, this.pos);
      } 
      if (length <= 0) {
        if (length == 0)
          return Internal.EMPTY_BYTE_ARRAY; 
        throw InvalidProtocolBufferException.negativeSize();
      } 
      throw InvalidProtocolBufferException.truncatedMessage();
    }
    
    public void skipRawBytes(int length) throws IOException {
      if (length >= 0 && length <= this.limit - this.pos) {
        this.pos += length;
        return;
      } 
      if (length < 0)
        throw InvalidProtocolBufferException.negativeSize(); 
      throw InvalidProtocolBufferException.truncatedMessage();
    }
  }
  
  private static final class UnsafeDirectNioDecoder extends CodedInputStream {
    private final ByteBuffer buffer;
    
    private final boolean immutable;
    
    private final long address;
    
    private long limit;
    
    private long pos;
    
    private long startPos;
    
    private int bufferSizeAfterLimit;
    
    private int lastTag;
    
    private boolean enableAliasing;
    
    private int currentLimit = Integer.MAX_VALUE;
    
    static boolean isSupported() {
      return UnsafeUtil.hasUnsafeByteBufferOperations();
    }
    
    private UnsafeDirectNioDecoder(ByteBuffer buffer, boolean immutable) {
      this.buffer = buffer;
      this.address = UnsafeUtil.addressOffset(buffer);
      this.limit = this.address + buffer.limit();
      this.pos = this.address + buffer.position();
      this.startPos = this.pos;
      this.immutable = immutable;
    }
    
    public int readTag() throws IOException {
      if (isAtEnd()) {
        this.lastTag = 0;
        return 0;
      } 
      this.lastTag = readRawVarint32();
      if (WireFormat.getTagFieldNumber(this.lastTag) == 0)
        throw InvalidProtocolBufferException.invalidTag(); 
      return this.lastTag;
    }
    
    public void checkLastTagWas(int value) throws InvalidProtocolBufferException {
      if (this.lastTag != value)
        throw InvalidProtocolBufferException.invalidEndTag(); 
    }
    
    public int getLastTag() {
      return this.lastTag;
    }
    
    public boolean skipField(int tag) throws IOException {
      switch (WireFormat.getTagWireType(tag)) {
        case 0:
          skipRawVarint();
          return true;
        case 1:
          skipRawBytes(8);
          return true;
        case 2:
          skipRawBytes(readRawVarint32());
          return true;
        case 3:
          skipMessage();
          checkLastTagWas(
              WireFormat.makeTag(WireFormat.getTagFieldNumber(tag), 4));
          return true;
        case 4:
          return false;
        case 5:
          skipRawBytes(4);
          return true;
      } 
      throw InvalidProtocolBufferException.invalidWireType();
    }
    
    public boolean skipField(int tag, CodedOutputStream output) throws IOException {
      long l;
      ByteString byteString;
      int endtag;
      int value;
      switch (WireFormat.getTagWireType(tag)) {
        case 0:
          l = readInt64();
          output.writeUInt32NoTag(tag);
          output.writeUInt64NoTag(l);
          return true;
        case 1:
          l = readRawLittleEndian64();
          output.writeUInt32NoTag(tag);
          output.writeFixed64NoTag(l);
          return true;
        case 2:
          byteString = readBytes();
          output.writeUInt32NoTag(tag);
          output.writeBytesNoTag(byteString);
          return true;
        case 3:
          output.writeUInt32NoTag(tag);
          skipMessage(output);
          endtag = WireFormat.makeTag(
              WireFormat.getTagFieldNumber(tag), 4);
          checkLastTagWas(endtag);
          output.writeUInt32NoTag(endtag);
          return true;
        case 4:
          return false;
        case 5:
          value = readRawLittleEndian32();
          output.writeUInt32NoTag(tag);
          output.writeFixed32NoTag(value);
          return true;
      } 
      throw InvalidProtocolBufferException.invalidWireType();
    }
    
    public void skipMessage() throws IOException {
      int tag;
      do {
        tag = readTag();
      } while (tag != 0 && skipField(tag));
    }
    
    public void skipMessage(CodedOutputStream output) throws IOException {
      int tag;
      do {
        tag = readTag();
      } while (tag != 0 && skipField(tag, output));
    }
    
    public double readDouble() throws IOException {
      return Double.longBitsToDouble(readRawLittleEndian64());
    }
    
    public float readFloat() throws IOException {
      return Float.intBitsToFloat(readRawLittleEndian32());
    }
    
    public long readUInt64() throws IOException {
      return readRawVarint64();
    }
    
    public long readInt64() throws IOException {
      return readRawVarint64();
    }
    
    public int readInt32() throws IOException {
      return readRawVarint32();
    }
    
    public long readFixed64() throws IOException {
      return readRawLittleEndian64();
    }
    
    public int readFixed32() throws IOException {
      return readRawLittleEndian32();
    }
    
    public boolean readBool() throws IOException {
      return (readRawVarint64() != 0L);
    }
    
    public String readString() throws IOException {
      int size = readRawVarint32();
      if (size > 0 && size <= remaining()) {
        byte[] bytes = new byte[size];
        UnsafeUtil.copyMemory(this.pos, bytes, 0L, size);
        String result = new String(bytes, Internal.UTF_8);
        this.pos += size;
        return result;
      } 
      if (size == 0)
        return ""; 
      if (size < 0)
        throw InvalidProtocolBufferException.negativeSize(); 
      throw InvalidProtocolBufferException.truncatedMessage();
    }
    
    public String readStringRequireUtf8() throws IOException {
      int size = readRawVarint32();
      if (size > 0 && size <= remaining()) {
        int bufferPos = bufferPos(this.pos);
        String result = Utf8.decodeUtf8(this.buffer, bufferPos, size);
        this.pos += size;
        return result;
      } 
      if (size == 0)
        return ""; 
      if (size <= 0)
        throw InvalidProtocolBufferException.negativeSize(); 
      throw InvalidProtocolBufferException.truncatedMessage();
    }
    
    public void readGroup(int fieldNumber, MessageLite.Builder builder, ExtensionRegistryLite extensionRegistry) throws IOException {
      checkRecursionLimit();
      this.recursionDepth++;
      builder.mergeFrom(this, extensionRegistry);
      checkLastTagWas(WireFormat.makeTag(fieldNumber, 4));
      this.recursionDepth--;
    }
    
    public <T extends MessageLite> T readGroup(int fieldNumber, Parser<T> parser, ExtensionRegistryLite extensionRegistry) throws IOException {
      checkRecursionLimit();
      this.recursionDepth++;
      MessageLite messageLite = (MessageLite)parser.parsePartialFrom(this, extensionRegistry);
      checkLastTagWas(WireFormat.makeTag(fieldNumber, 4));
      this.recursionDepth--;
      return (T)messageLite;
    }
    
    @Deprecated
    public void readUnknownGroup(int fieldNumber, MessageLite.Builder builder) throws IOException {
      readGroup(fieldNumber, builder, ExtensionRegistryLite.getEmptyRegistry());
    }
    
    public void readMessage(MessageLite.Builder builder, ExtensionRegistryLite extensionRegistry) throws IOException {
      int length = readRawVarint32();
      checkRecursionLimit();
      int oldLimit = pushLimit(length);
      this.recursionDepth++;
      builder.mergeFrom(this, extensionRegistry);
      checkLastTagWas(0);
      this.recursionDepth--;
      if (getBytesUntilLimit() != 0)
        throw InvalidProtocolBufferException.truncatedMessage(); 
      popLimit(oldLimit);
    }
    
    public <T extends MessageLite> T readMessage(Parser<T> parser, ExtensionRegistryLite extensionRegistry) throws IOException {
      int length = readRawVarint32();
      checkRecursionLimit();
      int oldLimit = pushLimit(length);
      this.recursionDepth++;
      MessageLite messageLite = (MessageLite)parser.parsePartialFrom(this, extensionRegistry);
      checkLastTagWas(0);
      this.recursionDepth--;
      if (getBytesUntilLimit() != 0)
        throw InvalidProtocolBufferException.truncatedMessage(); 
      popLimit(oldLimit);
      return (T)messageLite;
    }
    
    public ByteString readBytes() throws IOException {
      int size = readRawVarint32();
      if (size > 0 && size <= remaining()) {
        if (this.immutable && this.enableAliasing) {
          ByteBuffer result = slice(this.pos, this.pos + size);
          this.pos += size;
          return ByteString.wrap(result);
        } 
        byte[] bytes = new byte[size];
        UnsafeUtil.copyMemory(this.pos, bytes, 0L, size);
        this.pos += size;
        return ByteString.wrap(bytes);
      } 
      if (size == 0)
        return ByteString.EMPTY; 
      if (size < 0)
        throw InvalidProtocolBufferException.negativeSize(); 
      throw InvalidProtocolBufferException.truncatedMessage();
    }
    
    public byte[] readByteArray() throws IOException {
      return readRawBytes(readRawVarint32());
    }
    
    public ByteBuffer readByteBuffer() throws IOException {
      int size = readRawVarint32();
      if (size > 0 && size <= remaining()) {
        if (!this.immutable && this.enableAliasing) {
          ByteBuffer result = slice(this.pos, this.pos + size);
          this.pos += size;
          return result;
        } 
        byte[] bytes = new byte[size];
        UnsafeUtil.copyMemory(this.pos, bytes, 0L, size);
        this.pos += size;
        return ByteBuffer.wrap(bytes);
      } 
      if (size == 0)
        return Internal.EMPTY_BYTE_BUFFER; 
      if (size < 0)
        throw InvalidProtocolBufferException.negativeSize(); 
      throw InvalidProtocolBufferException.truncatedMessage();
    }
    
    public int readUInt32() throws IOException {
      return readRawVarint32();
    }
    
    public int readEnum() throws IOException {
      return readRawVarint32();
    }
    
    public int readSFixed32() throws IOException {
      return readRawLittleEndian32();
    }
    
    public long readSFixed64() throws IOException {
      return readRawLittleEndian64();
    }
    
    public int readSInt32() throws IOException {
      return decodeZigZag32(readRawVarint32());
    }
    
    public long readSInt64() throws IOException {
      return decodeZigZag64(readRawVarint64());
    }
    
    public int readRawVarint32() throws IOException {
      long tempPos = this.pos;
      if (this.limit != tempPos) {
        int x;
        if ((x = UnsafeUtil.getByte(tempPos++)) >= 0) {
          this.pos = tempPos;
          return x;
        } 
        if (this.limit - tempPos >= 9L) {
          if ((x ^= UnsafeUtil.getByte(tempPos++) << 7) < 0) {
            x ^= 0xFFFFFF80;
          } else if ((x ^= UnsafeUtil.getByte(tempPos++) << 14) >= 0) {
            x ^= 0x3F80;
          } else if ((x ^= UnsafeUtil.getByte(tempPos++) << 21) < 0) {
            x ^= 0xFFE03F80;
          } else {
            int y = UnsafeUtil.getByte(tempPos++);
            x ^= y << 28;
            x ^= 0xFE03F80;
            if (y < 0 && 
              UnsafeUtil.getByte(tempPos++) < 0 && 
              UnsafeUtil.getByte(tempPos++) < 0 && 
              UnsafeUtil.getByte(tempPos++) < 0 && 
              UnsafeUtil.getByte(tempPos++) < 0 && 
              UnsafeUtil.getByte(tempPos++) < 0)
              return (int)readRawVarint64SlowPath(); 
          } 
          this.pos = tempPos;
          return x;
        } 
      } 
      return (int)readRawVarint64SlowPath();
    }
    
    private void skipRawVarint() throws IOException {
      if (remaining() >= 10) {
        skipRawVarintFastPath();
      } else {
        skipRawVarintSlowPath();
      } 
    }
    
    private void skipRawVarintFastPath() throws IOException {
      for (int i = 0; i < 10; i++) {
        if (UnsafeUtil.getByte(this.pos++) >= 0)
          return; 
      } 
      throw InvalidProtocolBufferException.malformedVarint();
    }
    
    private void skipRawVarintSlowPath() throws IOException {
      for (int i = 0; i < 10; i++) {
        if (readRawByte() >= 0)
          return; 
      } 
      throw InvalidProtocolBufferException.malformedVarint();
    }
    
    public long readRawVarint64() throws IOException {
      long tempPos = this.pos;
      if (this.limit != tempPos) {
        int y;
        if ((y = UnsafeUtil.getByte(tempPos++)) >= 0) {
          this.pos = tempPos;
          return y;
        } 
        if (this.limit - tempPos >= 9L) {
          long x;
          if ((y ^= UnsafeUtil.getByte(tempPos++) << 7) < 0) {
            x = (y ^ 0xFFFFFF80);
          } else if ((y ^= UnsafeUtil.getByte(tempPos++) << 14) >= 0) {
            x = (y ^ 0x3F80);
          } else if ((y ^= UnsafeUtil.getByte(tempPos++) << 21) < 0) {
            x = (y ^ 0xFFE03F80);
          } else if ((x = y ^ UnsafeUtil.getByte(tempPos++) << 28L) >= 0L) {
            x ^= 0xFE03F80L;
          } else if ((x ^= UnsafeUtil.getByte(tempPos++) << 35L) < 0L) {
            x ^= 0xFFFFFFF80FE03F80L;
          } else if ((x ^= UnsafeUtil.getByte(tempPos++) << 42L) >= 0L) {
            x ^= 0x3F80FE03F80L;
          } else if ((x ^= UnsafeUtil.getByte(tempPos++) << 49L) < 0L) {
            x ^= 0xFFFE03F80FE03F80L;
          } else {
            x ^= UnsafeUtil.getByte(tempPos++) << 56L;
            x ^= 0xFE03F80FE03F80L;
            if (x < 0L && 
              UnsafeUtil.getByte(tempPos++) < 0L)
              return readRawVarint64SlowPath(); 
          } 
          this.pos = tempPos;
          return x;
        } 
      } 
      return readRawVarint64SlowPath();
    }
    
    long readRawVarint64SlowPath() throws IOException {
      long result = 0L;
      for (int shift = 0; shift < 64; shift += 7) {
        byte b = readRawByte();
        result |= (b & Byte.MAX_VALUE) << shift;
        if ((b & 0x80) == 0)
          return result; 
      } 
      throw InvalidProtocolBufferException.malformedVarint();
    }
    
    public int readRawLittleEndian32() throws IOException {
      long tempPos = this.pos;
      if (this.limit - tempPos < 4L)
        throw InvalidProtocolBufferException.truncatedMessage(); 
      this.pos = tempPos + 4L;
      return UnsafeUtil.getByte(tempPos) & 0xFF | (
        UnsafeUtil.getByte(tempPos + 1L) & 0xFF) << 8 | (
        UnsafeUtil.getByte(tempPos + 2L) & 0xFF) << 16 | (
        UnsafeUtil.getByte(tempPos + 3L) & 0xFF) << 24;
    }
    
    public long readRawLittleEndian64() throws IOException {
      long tempPos = this.pos;
      if (this.limit - tempPos < 8L)
        throw InvalidProtocolBufferException.truncatedMessage(); 
      this.pos = tempPos + 8L;
      return UnsafeUtil.getByte(tempPos) & 0xFFL | (
        UnsafeUtil.getByte(tempPos + 1L) & 0xFFL) << 8L | (
        UnsafeUtil.getByte(tempPos + 2L) & 0xFFL) << 16L | (
        UnsafeUtil.getByte(tempPos + 3L) & 0xFFL) << 24L | (
        UnsafeUtil.getByte(tempPos + 4L) & 0xFFL) << 32L | (
        UnsafeUtil.getByte(tempPos + 5L) & 0xFFL) << 40L | (
        UnsafeUtil.getByte(tempPos + 6L) & 0xFFL) << 48L | (
        UnsafeUtil.getByte(tempPos + 7L) & 0xFFL) << 56L;
    }
    
    public void enableAliasing(boolean enabled) {
      this.enableAliasing = enabled;
    }
    
    public void resetSizeCounter() {
      this.startPos = this.pos;
    }
    
    public int pushLimit(int byteLimit) throws InvalidProtocolBufferException {
      if (byteLimit < 0)
        throw InvalidProtocolBufferException.negativeSize(); 
      byteLimit += getTotalBytesRead();
      int oldLimit = this.currentLimit;
      if (byteLimit > oldLimit)
        throw InvalidProtocolBufferException.truncatedMessage(); 
      this.currentLimit = byteLimit;
      recomputeBufferSizeAfterLimit();
      return oldLimit;
    }
    
    public void popLimit(int oldLimit) {
      this.currentLimit = oldLimit;
      recomputeBufferSizeAfterLimit();
    }
    
    public int getBytesUntilLimit() {
      if (this.currentLimit == Integer.MAX_VALUE)
        return -1; 
      return this.currentLimit - getTotalBytesRead();
    }
    
    public boolean isAtEnd() throws IOException {
      return (this.pos == this.limit);
    }
    
    public int getTotalBytesRead() {
      return (int)(this.pos - this.startPos);
    }
    
    public byte readRawByte() throws IOException {
      if (this.pos == this.limit)
        throw InvalidProtocolBufferException.truncatedMessage(); 
      return UnsafeUtil.getByte(this.pos++);
    }
    
    public byte[] readRawBytes(int length) throws IOException {
      if (length >= 0 && length <= remaining()) {
        byte[] bytes = new byte[length];
        slice(this.pos, this.pos + length).get(bytes);
        this.pos += length;
        return bytes;
      } 
      if (length <= 0) {
        if (length == 0)
          return Internal.EMPTY_BYTE_ARRAY; 
        throw InvalidProtocolBufferException.negativeSize();
      } 
      throw InvalidProtocolBufferException.truncatedMessage();
    }
    
    public void skipRawBytes(int length) throws IOException {
      if (length >= 0 && length <= remaining()) {
        this.pos += length;
        return;
      } 
      if (length < 0)
        throw InvalidProtocolBufferException.negativeSize(); 
      throw InvalidProtocolBufferException.truncatedMessage();
    }
    
    private void recomputeBufferSizeAfterLimit() {
      this.limit += this.bufferSizeAfterLimit;
      int bufferEnd = (int)(this.limit - this.startPos);
      if (bufferEnd > this.currentLimit) {
        this.bufferSizeAfterLimit = bufferEnd - this.currentLimit;
        this.limit -= this.bufferSizeAfterLimit;
      } else {
        this.bufferSizeAfterLimit = 0;
      } 
    }
    
    private int remaining() {
      return (int)(this.limit - this.pos);
    }
    
    private int bufferPos(long pos) {
      return (int)(pos - this.address);
    }
    
    private ByteBuffer slice(long begin, long end) throws IOException {
      int prevPos = this.buffer.position();
      int prevLimit = this.buffer.limit();
      Buffer asBuffer = this.buffer;
      try {
        asBuffer.position(bufferPos(begin));
        asBuffer.limit(bufferPos(end));
        return this.buffer.slice();
      } catch (IllegalArgumentException e) {
        InvalidProtocolBufferException ex = InvalidProtocolBufferException.truncatedMessage();
        ex.initCause(e);
        throw ex;
      } finally {
        asBuffer.position(prevPos);
        asBuffer.limit(prevLimit);
      } 
    }
  }
  
  private static final class StreamDecoder extends CodedInputStream {
    private final InputStream input;
    
    private final byte[] buffer;
    
    private int bufferSize;
    
    private int bufferSizeAfterLimit;
    
    private int pos;
    
    private int lastTag;
    
    private int totalBytesRetired;
    
    private int currentLimit = Integer.MAX_VALUE;
    
    private RefillCallback refillCallback;
    
    private static int read(InputStream input, byte[] data, int offset, int length) throws IOException {
      try {
        return input.read(data, offset, length);
      } catch (InvalidProtocolBufferException e) {
        e.setThrownFromInputStream();
        throw e;
      } 
    }
    
    private static long skip(InputStream input, long length) throws IOException {
      try {
        return input.skip(length);
      } catch (InvalidProtocolBufferException e) {
        e.setThrownFromInputStream();
        throw e;
      } 
    }
    
    private static int available(InputStream input) throws IOException {
      try {
        return input.available();
      } catch (InvalidProtocolBufferException e) {
        e.setThrownFromInputStream();
        throw e;
      } 
    }
    
    public int readTag() throws IOException {
      if (isAtEnd()) {
        this.lastTag = 0;
        return 0;
      } 
      this.lastTag = readRawVarint32();
      if (WireFormat.getTagFieldNumber(this.lastTag) == 0)
        throw InvalidProtocolBufferException.invalidTag(); 
      return this.lastTag;
    }
    
    public void checkLastTagWas(int value) throws InvalidProtocolBufferException {
      if (this.lastTag != value)
        throw InvalidProtocolBufferException.invalidEndTag(); 
    }
    
    public int getLastTag() {
      return this.lastTag;
    }
    
    public boolean skipField(int tag) throws IOException {
      switch (WireFormat.getTagWireType(tag)) {
        case 0:
          skipRawVarint();
          return true;
        case 1:
          skipRawBytes(8);
          return true;
        case 2:
          skipRawBytes(readRawVarint32());
          return true;
        case 3:
          skipMessage();
          checkLastTagWas(
              WireFormat.makeTag(WireFormat.getTagFieldNumber(tag), 4));
          return true;
        case 4:
          return false;
        case 5:
          skipRawBytes(4);
          return true;
      } 
      throw InvalidProtocolBufferException.invalidWireType();
    }
    
    public boolean skipField(int tag, CodedOutputStream output) throws IOException {
      long l;
      ByteString byteString;
      int endtag;
      int value;
      switch (WireFormat.getTagWireType(tag)) {
        case 0:
          l = readInt64();
          output.writeUInt32NoTag(tag);
          output.writeUInt64NoTag(l);
          return true;
        case 1:
          l = readRawLittleEndian64();
          output.writeUInt32NoTag(tag);
          output.writeFixed64NoTag(l);
          return true;
        case 2:
          byteString = readBytes();
          output.writeUInt32NoTag(tag);
          output.writeBytesNoTag(byteString);
          return true;
        case 3:
          output.writeUInt32NoTag(tag);
          skipMessage(output);
          endtag = WireFormat.makeTag(
              WireFormat.getTagFieldNumber(tag), 4);
          checkLastTagWas(endtag);
          output.writeUInt32NoTag(endtag);
          return true;
        case 4:
          return false;
        case 5:
          value = readRawLittleEndian32();
          output.writeUInt32NoTag(tag);
          output.writeFixed32NoTag(value);
          return true;
      } 
      throw InvalidProtocolBufferException.invalidWireType();
    }
    
    public void skipMessage() throws IOException {
      int tag;
      do {
        tag = readTag();
      } while (tag != 0 && skipField(tag));
    }
    
    public void skipMessage(CodedOutputStream output) throws IOException {
      int tag;
      do {
        tag = readTag();
      } while (tag != 0 && skipField(tag, output));
    }
    
    private static interface RefillCallback {
      void onRefill();
    }
    
    private class SkippedDataSink implements RefillCallback {
      private int lastPos = CodedInputStream.StreamDecoder.this.pos;
      
      private ByteArrayOutputStream byteArrayStream;
      
      public void onRefill() {
        if (this.byteArrayStream == null)
          this.byteArrayStream = new ByteArrayOutputStream(); 
        this.byteArrayStream.write(CodedInputStream.StreamDecoder.this.buffer, this.lastPos, CodedInputStream.StreamDecoder.this.pos - this.lastPos);
        this.lastPos = 0;
      }
      
      ByteBuffer getSkippedData() {
        if (this.byteArrayStream == null)
          return ByteBuffer.wrap(CodedInputStream.StreamDecoder.this.buffer, this.lastPos, CodedInputStream.StreamDecoder.this.pos - this.lastPos); 
        this.byteArrayStream.write(CodedInputStream.StreamDecoder.this.buffer, this.lastPos, CodedInputStream.StreamDecoder.this.pos);
        return ByteBuffer.wrap(this.byteArrayStream.toByteArray());
      }
    }
    
    public double readDouble() throws IOException {
      return Double.longBitsToDouble(readRawLittleEndian64());
    }
    
    public float readFloat() throws IOException {
      return Float.intBitsToFloat(readRawLittleEndian32());
    }
    
    public long readUInt64() throws IOException {
      return readRawVarint64();
    }
    
    public long readInt64() throws IOException {
      return readRawVarint64();
    }
    
    public int readInt32() throws IOException {
      return readRawVarint32();
    }
    
    public long readFixed64() throws IOException {
      return readRawLittleEndian64();
    }
    
    public int readFixed32() throws IOException {
      return readRawLittleEndian32();
    }
    
    public boolean readBool() throws IOException {
      return (readRawVarint64() != 0L);
    }
    
    public String readString() throws IOException {
      int size = readRawVarint32();
      if (size > 0 && size <= this.bufferSize - this.pos) {
        String result = new String(this.buffer, this.pos, size, Internal.UTF_8);
        this.pos += size;
        return result;
      } 
      if (size == 0)
        return ""; 
      if (size <= this.bufferSize) {
        refillBuffer(size);
        String result = new String(this.buffer, this.pos, size, Internal.UTF_8);
        this.pos += size;
        return result;
      } 
      return new String(readRawBytesSlowPath(size, false), Internal.UTF_8);
    }
    
    public String readStringRequireUtf8() throws IOException {
      byte[] bytes;
      int tempPos, size = readRawVarint32();
      int oldPos = this.pos;
      if (size <= this.bufferSize - oldPos && size > 0) {
        bytes = this.buffer;
        this.pos = oldPos + size;
        tempPos = oldPos;
      } else {
        if (size == 0)
          return ""; 
        if (size <= this.bufferSize) {
          refillBuffer(size);
          bytes = this.buffer;
          tempPos = 0;
          this.pos = tempPos + size;
        } else {
          bytes = readRawBytesSlowPath(size, false);
          tempPos = 0;
        } 
      } 
      return Utf8.decodeUtf8(bytes, tempPos, size);
    }
    
    public void readGroup(int fieldNumber, MessageLite.Builder builder, ExtensionRegistryLite extensionRegistry) throws IOException {
      checkRecursionLimit();
      this.recursionDepth++;
      builder.mergeFrom(this, extensionRegistry);
      checkLastTagWas(WireFormat.makeTag(fieldNumber, 4));
      this.recursionDepth--;
    }
    
    public <T extends MessageLite> T readGroup(int fieldNumber, Parser<T> parser, ExtensionRegistryLite extensionRegistry) throws IOException {
      checkRecursionLimit();
      this.recursionDepth++;
      MessageLite messageLite = (MessageLite)parser.parsePartialFrom(this, extensionRegistry);
      checkLastTagWas(WireFormat.makeTag(fieldNumber, 4));
      this.recursionDepth--;
      return (T)messageLite;
    }
    
    @Deprecated
    public void readUnknownGroup(int fieldNumber, MessageLite.Builder builder) throws IOException {
      readGroup(fieldNumber, builder, ExtensionRegistryLite.getEmptyRegistry());
    }
    
    public void readMessage(MessageLite.Builder builder, ExtensionRegistryLite extensionRegistry) throws IOException {
      int length = readRawVarint32();
      checkRecursionLimit();
      int oldLimit = pushLimit(length);
      this.recursionDepth++;
      builder.mergeFrom(this, extensionRegistry);
      checkLastTagWas(0);
      this.recursionDepth--;
      if (getBytesUntilLimit() != 0)
        throw InvalidProtocolBufferException.truncatedMessage(); 
      popLimit(oldLimit);
    }
    
    public <T extends MessageLite> T readMessage(Parser<T> parser, ExtensionRegistryLite extensionRegistry) throws IOException {
      int length = readRawVarint32();
      checkRecursionLimit();
      int oldLimit = pushLimit(length);
      this.recursionDepth++;
      MessageLite messageLite = (MessageLite)parser.parsePartialFrom(this, extensionRegistry);
      checkLastTagWas(0);
      this.recursionDepth--;
      if (getBytesUntilLimit() != 0)
        throw InvalidProtocolBufferException.truncatedMessage(); 
      popLimit(oldLimit);
      return (T)messageLite;
    }
    
    public ByteString readBytes() throws IOException {
      int size = readRawVarint32();
      if (size <= this.bufferSize - this.pos && size > 0) {
        ByteString result = ByteString.copyFrom(this.buffer, this.pos, size);
        this.pos += size;
        return result;
      } 
      if (size == 0)
        return ByteString.EMPTY; 
      return readBytesSlowPath(size);
    }
    
    public byte[] readByteArray() throws IOException {
      int size = readRawVarint32();
      if (size <= this.bufferSize - this.pos && size > 0) {
        byte[] result = Arrays.copyOfRange(this.buffer, this.pos, this.pos + size);
        this.pos += size;
        return result;
      } 
      return readRawBytesSlowPath(size, false);
    }
    
    public ByteBuffer readByteBuffer() throws IOException {
      int size = readRawVarint32();
      if (size <= this.bufferSize - this.pos && size > 0) {
        ByteBuffer result = ByteBuffer.wrap(Arrays.copyOfRange(this.buffer, this.pos, this.pos + size));
        this.pos += size;
        return result;
      } 
      if (size == 0)
        return Internal.EMPTY_BYTE_BUFFER; 
      return ByteBuffer.wrap(readRawBytesSlowPath(size, true));
    }
    
    public int readUInt32() throws IOException {
      return readRawVarint32();
    }
    
    public int readEnum() throws IOException {
      return readRawVarint32();
    }
    
    public int readSFixed32() throws IOException {
      return readRawLittleEndian32();
    }
    
    public long readSFixed64() throws IOException {
      return readRawLittleEndian64();
    }
    
    public int readSInt32() throws IOException {
      return decodeZigZag32(readRawVarint32());
    }
    
    public long readSInt64() throws IOException {
      return decodeZigZag64(readRawVarint64());
    }
    
    public int readRawVarint32() throws IOException {
      int tempPos = this.pos;
      if (this.bufferSize != tempPos) {
        byte[] buffer = this.buffer;
        int x;
        if ((x = buffer[tempPos++]) >= 0) {
          this.pos = tempPos;
          return x;
        } 
        if (this.bufferSize - tempPos >= 9) {
          if ((x ^= buffer[tempPos++] << 7) < 0) {
            x ^= 0xFFFFFF80;
          } else if ((x ^= buffer[tempPos++] << 14) >= 0) {
            x ^= 0x3F80;
          } else if ((x ^= buffer[tempPos++] << 21) < 0) {
            x ^= 0xFFE03F80;
          } else {
            int y = buffer[tempPos++];
            x ^= y << 28;
            x ^= 0xFE03F80;
            if (y < 0 && buffer[tempPos++] < 0 && buffer[tempPos++] < 0 && buffer[tempPos++] < 0 && buffer[tempPos++] < 0 && buffer[tempPos++] < 0)
              return (int)readRawVarint64SlowPath(); 
          } 
          this.pos = tempPos;
          return x;
        } 
      } 
      return (int)readRawVarint64SlowPath();
    }
    
    private void skipRawVarint() throws IOException {
      if (this.bufferSize - this.pos >= 10) {
        skipRawVarintFastPath();
      } else {
        skipRawVarintSlowPath();
      } 
    }
    
    private void skipRawVarintFastPath() throws IOException {
      for (int i = 0; i < 10; i++) {
        if (this.buffer[this.pos++] >= 0)
          return; 
      } 
      throw InvalidProtocolBufferException.malformedVarint();
    }
    
    private void skipRawVarintSlowPath() throws IOException {
      for (int i = 0; i < 10; i++) {
        if (readRawByte() >= 0)
          return; 
      } 
      throw InvalidProtocolBufferException.malformedVarint();
    }
    
    public long readRawVarint64() throws IOException {
      int tempPos = this.pos;
      if (this.bufferSize != tempPos) {
        byte[] buffer = this.buffer;
        int y;
        if ((y = buffer[tempPos++]) >= 0) {
          this.pos = tempPos;
          return y;
        } 
        if (this.bufferSize - tempPos >= 9) {
          long x;
          if ((y ^= buffer[tempPos++] << 7) < 0) {
            x = (y ^ 0xFFFFFF80);
          } else if ((y ^= buffer[tempPos++] << 14) >= 0) {
            x = (y ^ 0x3F80);
          } else if ((y ^= buffer[tempPos++] << 21) < 0) {
            x = (y ^ 0xFFE03F80);
          } else if ((x = y ^ buffer[tempPos++] << 28L) >= 0L) {
            x ^= 0xFE03F80L;
          } else if ((x ^= buffer[tempPos++] << 35L) < 0L) {
            x ^= 0xFFFFFFF80FE03F80L;
          } else if ((x ^= buffer[tempPos++] << 42L) >= 0L) {
            x ^= 0x3F80FE03F80L;
          } else if ((x ^= buffer[tempPos++] << 49L) < 0L) {
            x ^= 0xFFFE03F80FE03F80L;
          } else {
            x ^= buffer[tempPos++] << 56L;
            x ^= 0xFE03F80FE03F80L;
            if (x < 0L && 
              buffer[tempPos++] < 0L)
              return readRawVarint64SlowPath(); 
          } 
          this.pos = tempPos;
          return x;
        } 
      } 
      return readRawVarint64SlowPath();
    }
    
    long readRawVarint64SlowPath() throws IOException {
      long result = 0L;
      for (int shift = 0; shift < 64; shift += 7) {
        byte b = readRawByte();
        result |= (b & Byte.MAX_VALUE) << shift;
        if ((b & 0x80) == 0)
          return result; 
      } 
      throw InvalidProtocolBufferException.malformedVarint();
    }
    
    public int readRawLittleEndian32() throws IOException {
      int tempPos = this.pos;
      if (this.bufferSize - tempPos < 4) {
        refillBuffer(4);
        tempPos = this.pos;
      } 
      byte[] buffer = this.buffer;
      this.pos = tempPos + 4;
      return buffer[tempPos] & 0xFF | (buffer[tempPos + 1] & 0xFF) << 8 | (buffer[tempPos + 2] & 0xFF) << 16 | (buffer[tempPos + 3] & 0xFF) << 24;
    }
    
    public long readRawLittleEndian64() throws IOException {
      int tempPos = this.pos;
      if (this.bufferSize - tempPos < 8) {
        refillBuffer(8);
        tempPos = this.pos;
      } 
      byte[] buffer = this.buffer;
      this.pos = tempPos + 8;
      return buffer[tempPos] & 0xFFL | (buffer[tempPos + 1] & 0xFFL) << 8L | (buffer[tempPos + 2] & 0xFFL) << 16L | (buffer[tempPos + 3] & 0xFFL) << 24L | (buffer[tempPos + 4] & 0xFFL) << 32L | (buffer[tempPos + 5] & 0xFFL) << 40L | (buffer[tempPos + 6] & 0xFFL) << 48L | (buffer[tempPos + 7] & 0xFFL) << 56L;
    }
    
    public void enableAliasing(boolean enabled) {}
    
    public void resetSizeCounter() {
      this.totalBytesRetired = -this.pos;
    }
    
    public int pushLimit(int byteLimit) throws InvalidProtocolBufferException {
      if (byteLimit < 0)
        throw InvalidProtocolBufferException.negativeSize(); 
      byteLimit += this.totalBytesRetired + this.pos;
      int oldLimit = this.currentLimit;
      if (byteLimit > oldLimit)
        throw InvalidProtocolBufferException.truncatedMessage(); 
      this.currentLimit = byteLimit;
      recomputeBufferSizeAfterLimit();
      return oldLimit;
    }
    
    private void recomputeBufferSizeAfterLimit() {
      this.bufferSize += this.bufferSizeAfterLimit;
      int bufferEnd = this.totalBytesRetired + this.bufferSize;
      if (bufferEnd > this.currentLimit) {
        this.bufferSizeAfterLimit = bufferEnd - this.currentLimit;
        this.bufferSize -= this.bufferSizeAfterLimit;
      } else {
        this.bufferSizeAfterLimit = 0;
      } 
    }
    
    public void popLimit(int oldLimit) {
      this.currentLimit = oldLimit;
      recomputeBufferSizeAfterLimit();
    }
    
    public int getBytesUntilLimit() {
      if (this.currentLimit == Integer.MAX_VALUE)
        return -1; 
      int currentAbsolutePosition = this.totalBytesRetired + this.pos;
      return this.currentLimit - currentAbsolutePosition;
    }
    
    public boolean isAtEnd() throws IOException {
      return (this.pos == this.bufferSize && !tryRefillBuffer(1));
    }
    
    public int getTotalBytesRead() {
      return this.totalBytesRetired + this.pos;
    }
    
    private StreamDecoder(InputStream input, int bufferSize) {
      this.refillCallback = null;
      Internal.checkNotNull(input, "input");
      this.input = input;
      this.buffer = new byte[bufferSize];
      this.bufferSize = 0;
      this.pos = 0;
      this.totalBytesRetired = 0;
    }
    
    private void refillBuffer(int n) throws IOException {
      if (!tryRefillBuffer(n)) {
        if (n > this.sizeLimit - this.totalBytesRetired - this.pos)
          throw InvalidProtocolBufferException.sizeLimitExceeded(); 
        throw InvalidProtocolBufferException.truncatedMessage();
      } 
    }
    
    private boolean tryRefillBuffer(int n) throws IOException {
      if (this.pos + n <= this.bufferSize)
        throw new IllegalStateException("refillBuffer() called when " + n + " bytes were already available in buffer"); 
      if (n > this.sizeLimit - this.totalBytesRetired - this.pos)
        return false; 
      if (this.totalBytesRetired + this.pos + n > this.currentLimit)
        return false; 
      if (this.refillCallback != null)
        this.refillCallback.onRefill(); 
      int tempPos = this.pos;
      if (tempPos > 0) {
        if (this.bufferSize > tempPos)
          System.arraycopy(this.buffer, tempPos, this.buffer, 0, this.bufferSize - tempPos); 
        this.totalBytesRetired += tempPos;
        this.bufferSize -= tempPos;
        this.pos = 0;
      } 
      int bytesRead = read(this.input, this.buffer, this.bufferSize, 
          
          Math.min(this.buffer.length - this.bufferSize, this.sizeLimit - this.totalBytesRetired - this.bufferSize));
      if (bytesRead == 0 || bytesRead < -1 || bytesRead > this.buffer.length)
        throw new IllegalStateException(this.input
            .getClass() + "#read(byte[]) returned invalid result: " + bytesRead + "\nThe InputStream implementation is buggy."); 
      if (bytesRead > 0) {
        this.bufferSize += bytesRead;
        recomputeBufferSizeAfterLimit();
        return (this.bufferSize >= n) ? true : tryRefillBuffer(n);
      } 
      return false;
    }
    
    public byte readRawByte() throws IOException {
      if (this.pos == this.bufferSize)
        refillBuffer(1); 
      return this.buffer[this.pos++];
    }
    
    public byte[] readRawBytes(int size) throws IOException {
      int tempPos = this.pos;
      if (size <= this.bufferSize - tempPos && size > 0) {
        this.pos = tempPos + size;
        return Arrays.copyOfRange(this.buffer, tempPos, tempPos + size);
      } 
      return readRawBytesSlowPath(size, false);
    }
    
    private byte[] readRawBytesSlowPath(int size, boolean ensureNoLeakedReferences) throws IOException {
      byte[] result = readRawBytesSlowPathOneChunk(size);
      if (result != null)
        return ensureNoLeakedReferences ? (byte[])result.clone() : result; 
      int originalBufferPos = this.pos;
      int bufferedBytes = this.bufferSize - this.pos;
      this.totalBytesRetired += this.bufferSize;
      this.pos = 0;
      this.bufferSize = 0;
      int sizeLeft = size - bufferedBytes;
      List<byte[]> chunks = readRawBytesSlowPathRemainingChunks(sizeLeft);
      byte[] bytes = new byte[size];
      System.arraycopy(this.buffer, originalBufferPos, bytes, 0, bufferedBytes);
      int tempPos = bufferedBytes;
      for (byte[] chunk : chunks) {
        System.arraycopy(chunk, 0, bytes, tempPos, chunk.length);
        tempPos += chunk.length;
      } 
      return bytes;
    }
    
    private byte[] readRawBytesSlowPathOneChunk(int size) throws IOException {
      if (size == 0)
        return Internal.EMPTY_BYTE_ARRAY; 
      if (size < 0)
        throw InvalidProtocolBufferException.negativeSize(); 
      int currentMessageSize = this.totalBytesRetired + this.pos + size;
      if (currentMessageSize - this.sizeLimit > 0)
        throw InvalidProtocolBufferException.sizeLimitExceeded(); 
      if (currentMessageSize > this.currentLimit) {
        skipRawBytes(this.currentLimit - this.totalBytesRetired - this.pos);
        throw InvalidProtocolBufferException.truncatedMessage();
      } 
      int bufferedBytes = this.bufferSize - this.pos;
      int sizeLeft = size - bufferedBytes;
      if (sizeLeft < 4096 || sizeLeft <= available(this.input)) {
        byte[] bytes = new byte[size];
        System.arraycopy(this.buffer, this.pos, bytes, 0, bufferedBytes);
        this.totalBytesRetired += this.bufferSize;
        this.pos = 0;
        this.bufferSize = 0;
        int tempPos = bufferedBytes;
        while (tempPos < bytes.length) {
          int n = read(this.input, bytes, tempPos, size - tempPos);
          if (n == -1)
            throw InvalidProtocolBufferException.truncatedMessage(); 
          this.totalBytesRetired += n;
          tempPos += n;
        } 
        return bytes;
      } 
      return null;
    }
    
    private List<byte[]> readRawBytesSlowPathRemainingChunks(int sizeLeft) throws IOException {
      List<byte[]> chunks = (List)new ArrayList<>();
      while (sizeLeft > 0) {
        byte[] chunk = new byte[Math.min(sizeLeft, 4096)];
        int tempPos = 0;
        while (tempPos < chunk.length) {
          int n = this.input.read(chunk, tempPos, chunk.length - tempPos);
          if (n == -1)
            throw InvalidProtocolBufferException.truncatedMessage(); 
          this.totalBytesRetired += n;
          tempPos += n;
        } 
        sizeLeft -= chunk.length;
        chunks.add(chunk);
      } 
      return chunks;
    }
    
    private ByteString readBytesSlowPath(int size) throws IOException {
      byte[] result = readRawBytesSlowPathOneChunk(size);
      if (result != null)
        return ByteString.copyFrom(result); 
      int originalBufferPos = this.pos;
      int bufferedBytes = this.bufferSize - this.pos;
      this.totalBytesRetired += this.bufferSize;
      this.pos = 0;
      this.bufferSize = 0;
      int sizeLeft = size - bufferedBytes;
      List<byte[]> chunks = readRawBytesSlowPathRemainingChunks(sizeLeft);
      byte[] bytes = new byte[size];
      System.arraycopy(this.buffer, originalBufferPos, bytes, 0, bufferedBytes);
      int tempPos = bufferedBytes;
      for (byte[] chunk : chunks) {
        System.arraycopy(chunk, 0, bytes, tempPos, chunk.length);
        tempPos += chunk.length;
      } 
      return ByteString.wrap(bytes);
    }
    
    public void skipRawBytes(int size) throws IOException {
      if (size <= this.bufferSize - this.pos && size >= 0) {
        this.pos += size;
      } else {
        skipRawBytesSlowPath(size);
      } 
    }
    
    private void skipRawBytesSlowPath(int size) throws IOException {
      if (size < 0)
        throw InvalidProtocolBufferException.negativeSize(); 
      if (this.totalBytesRetired + this.pos + size > this.currentLimit) {
        skipRawBytes(this.currentLimit - this.totalBytesRetired - this.pos);
        throw InvalidProtocolBufferException.truncatedMessage();
      } 
      int totalSkipped = 0;
      if (this.refillCallback == null) {
        this.totalBytesRetired += this.pos;
        totalSkipped = this.bufferSize - this.pos;
        this.bufferSize = 0;
        this.pos = 0;
        try {
          while (totalSkipped < size) {
            int toSkip = size - totalSkipped;
            long skipped = skip(this.input, toSkip);
            if (skipped < 0L || skipped > toSkip)
              throw new IllegalStateException(this.input
                  .getClass() + "#skip returned invalid result: " + skipped + "\nThe InputStream implementation is buggy."); 
            if (skipped == 0L)
              break; 
            totalSkipped += (int)skipped;
          } 
        } finally {
          this.totalBytesRetired += totalSkipped;
          recomputeBufferSizeAfterLimit();
        } 
      } 
      if (totalSkipped < size) {
        int tempPos = this.bufferSize - this.pos;
        this.pos = this.bufferSize;
        refillBuffer(1);
        while (size - tempPos > this.bufferSize) {
          tempPos += this.bufferSize;
          this.pos = this.bufferSize;
          refillBuffer(1);
        } 
        this.pos = size - tempPos;
      } 
    }
  }
  
  private static final class IterableDirectByteBufferDecoder extends CodedInputStream {
    private final Iterable<ByteBuffer> input;
    
    private final Iterator<ByteBuffer> iterator;
    
    private ByteBuffer currentByteBuffer;
    
    private final boolean immutable;
    
    private boolean enableAliasing;
    
    private int totalBufferSize;
    
    private int bufferSizeAfterCurrentLimit;
    
    private int currentLimit = Integer.MAX_VALUE;
    
    private int lastTag;
    
    private int totalBytesRead;
    
    private int startOffset;
    
    private long currentByteBufferPos;
    
    private long currentByteBufferStartPos;
    
    private long currentAddress;
    
    private long currentByteBufferLimit;
    
    private IterableDirectByteBufferDecoder(Iterable<ByteBuffer> inputBufs, int size, boolean immutableFlag) {
      this.totalBufferSize = size;
      this.input = inputBufs;
      this.iterator = this.input.iterator();
      this.immutable = immutableFlag;
      this.startOffset = this.totalBytesRead = 0;
      if (size == 0) {
        this.currentByteBuffer = Internal.EMPTY_BYTE_BUFFER;
        this.currentByteBufferPos = 0L;
        this.currentByteBufferStartPos = 0L;
        this.currentByteBufferLimit = 0L;
        this.currentAddress = 0L;
      } else {
        tryGetNextByteBuffer();
      } 
    }
    
    private void getNextByteBuffer() throws InvalidProtocolBufferException {
      if (!this.iterator.hasNext())
        throw InvalidProtocolBufferException.truncatedMessage(); 
      tryGetNextByteBuffer();
    }
    
    private void tryGetNextByteBuffer() {
      this.currentByteBuffer = this.iterator.next();
      this.totalBytesRead += (int)(this.currentByteBufferPos - this.currentByteBufferStartPos);
      this.currentByteBufferPos = this.currentByteBuffer.position();
      this.currentByteBufferStartPos = this.currentByteBufferPos;
      this.currentByteBufferLimit = this.currentByteBuffer.limit();
      this.currentAddress = UnsafeUtil.addressOffset(this.currentByteBuffer);
      this.currentByteBufferPos += this.currentAddress;
      this.currentByteBufferStartPos += this.currentAddress;
      this.currentByteBufferLimit += this.currentAddress;
    }
    
    public int readTag() throws IOException {
      if (isAtEnd()) {
        this.lastTag = 0;
        return 0;
      } 
      this.lastTag = readRawVarint32();
      if (WireFormat.getTagFieldNumber(this.lastTag) == 0)
        throw InvalidProtocolBufferException.invalidTag(); 
      return this.lastTag;
    }
    
    public void checkLastTagWas(int value) throws InvalidProtocolBufferException {
      if (this.lastTag != value)
        throw InvalidProtocolBufferException.invalidEndTag(); 
    }
    
    public int getLastTag() {
      return this.lastTag;
    }
    
    public boolean skipField(int tag) throws IOException {
      switch (WireFormat.getTagWireType(tag)) {
        case 0:
          skipRawVarint();
          return true;
        case 1:
          skipRawBytes(8);
          return true;
        case 2:
          skipRawBytes(readRawVarint32());
          return true;
        case 3:
          skipMessage();
          checkLastTagWas(
              WireFormat.makeTag(WireFormat.getTagFieldNumber(tag), 4));
          return true;
        case 4:
          return false;
        case 5:
          skipRawBytes(4);
          return true;
      } 
      throw InvalidProtocolBufferException.invalidWireType();
    }
    
    public boolean skipField(int tag, CodedOutputStream output) throws IOException {
      long l;
      ByteString byteString;
      int endtag;
      int value;
      switch (WireFormat.getTagWireType(tag)) {
        case 0:
          l = readInt64();
          output.writeUInt32NoTag(tag);
          output.writeUInt64NoTag(l);
          return true;
        case 1:
          l = readRawLittleEndian64();
          output.writeUInt32NoTag(tag);
          output.writeFixed64NoTag(l);
          return true;
        case 2:
          byteString = readBytes();
          output.writeUInt32NoTag(tag);
          output.writeBytesNoTag(byteString);
          return true;
        case 3:
          output.writeUInt32NoTag(tag);
          skipMessage(output);
          endtag = WireFormat.makeTag(
              WireFormat.getTagFieldNumber(tag), 4);
          checkLastTagWas(endtag);
          output.writeUInt32NoTag(endtag);
          return true;
        case 4:
          return false;
        case 5:
          value = readRawLittleEndian32();
          output.writeUInt32NoTag(tag);
          output.writeFixed32NoTag(value);
          return true;
      } 
      throw InvalidProtocolBufferException.invalidWireType();
    }
    
    public void skipMessage() throws IOException {
      int tag;
      do {
        tag = readTag();
      } while (tag != 0 && skipField(tag));
    }
    
    public void skipMessage(CodedOutputStream output) throws IOException {
      int tag;
      do {
        tag = readTag();
      } while (tag != 0 && skipField(tag, output));
    }
    
    public double readDouble() throws IOException {
      return Double.longBitsToDouble(readRawLittleEndian64());
    }
    
    public float readFloat() throws IOException {
      return Float.intBitsToFloat(readRawLittleEndian32());
    }
    
    public long readUInt64() throws IOException {
      return readRawVarint64();
    }
    
    public long readInt64() throws IOException {
      return readRawVarint64();
    }
    
    public int readInt32() throws IOException {
      return readRawVarint32();
    }
    
    public long readFixed64() throws IOException {
      return readRawLittleEndian64();
    }
    
    public int readFixed32() throws IOException {
      return readRawLittleEndian32();
    }
    
    public boolean readBool() throws IOException {
      return (readRawVarint64() != 0L);
    }
    
    public String readString() throws IOException {
      int size = readRawVarint32();
      if (size > 0 && size <= this.currentByteBufferLimit - this.currentByteBufferPos) {
        byte[] bytes = new byte[size];
        UnsafeUtil.copyMemory(this.currentByteBufferPos, bytes, 0L, size);
        String result = new String(bytes, Internal.UTF_8);
        this.currentByteBufferPos += size;
        return result;
      } 
      if (size > 0 && size <= remaining()) {
        byte[] bytes = new byte[size];
        readRawBytesTo(bytes, 0, size);
        String result = new String(bytes, Internal.UTF_8);
        return result;
      } 
      if (size == 0)
        return ""; 
      if (size < 0)
        throw InvalidProtocolBufferException.negativeSize(); 
      throw InvalidProtocolBufferException.truncatedMessage();
    }
    
    public String readStringRequireUtf8() throws IOException {
      int size = readRawVarint32();
      if (size > 0 && size <= this.currentByteBufferLimit - this.currentByteBufferPos) {
        int bufferPos = (int)(this.currentByteBufferPos - this.currentByteBufferStartPos);
        String result = Utf8.decodeUtf8(this.currentByteBuffer, bufferPos, size);
        this.currentByteBufferPos += size;
        return result;
      } 
      if (size >= 0 && size <= remaining()) {
        byte[] bytes = new byte[size];
        readRawBytesTo(bytes, 0, size);
        return Utf8.decodeUtf8(bytes, 0, size);
      } 
      if (size == 0)
        return ""; 
      if (size <= 0)
        throw InvalidProtocolBufferException.negativeSize(); 
      throw InvalidProtocolBufferException.truncatedMessage();
    }
    
    public void readGroup(int fieldNumber, MessageLite.Builder builder, ExtensionRegistryLite extensionRegistry) throws IOException {
      checkRecursionLimit();
      this.recursionDepth++;
      builder.mergeFrom(this, extensionRegistry);
      checkLastTagWas(WireFormat.makeTag(fieldNumber, 4));
      this.recursionDepth--;
    }
    
    public <T extends MessageLite> T readGroup(int fieldNumber, Parser<T> parser, ExtensionRegistryLite extensionRegistry) throws IOException {
      checkRecursionLimit();
      this.recursionDepth++;
      MessageLite messageLite = (MessageLite)parser.parsePartialFrom(this, extensionRegistry);
      checkLastTagWas(WireFormat.makeTag(fieldNumber, 4));
      this.recursionDepth--;
      return (T)messageLite;
    }
    
    @Deprecated
    public void readUnknownGroup(int fieldNumber, MessageLite.Builder builder) throws IOException {
      readGroup(fieldNumber, builder, ExtensionRegistryLite.getEmptyRegistry());
    }
    
    public void readMessage(MessageLite.Builder builder, ExtensionRegistryLite extensionRegistry) throws IOException {
      int length = readRawVarint32();
      checkRecursionLimit();
      int oldLimit = pushLimit(length);
      this.recursionDepth++;
      builder.mergeFrom(this, extensionRegistry);
      checkLastTagWas(0);
      this.recursionDepth--;
      if (getBytesUntilLimit() != 0)
        throw InvalidProtocolBufferException.truncatedMessage(); 
      popLimit(oldLimit);
    }
    
    public <T extends MessageLite> T readMessage(Parser<T> parser, ExtensionRegistryLite extensionRegistry) throws IOException {
      int length = readRawVarint32();
      checkRecursionLimit();
      int oldLimit = pushLimit(length);
      this.recursionDepth++;
      MessageLite messageLite = (MessageLite)parser.parsePartialFrom(this, extensionRegistry);
      checkLastTagWas(0);
      this.recursionDepth--;
      if (getBytesUntilLimit() != 0)
        throw InvalidProtocolBufferException.truncatedMessage(); 
      popLimit(oldLimit);
      return (T)messageLite;
    }
    
    public ByteString readBytes() throws IOException {
      int size = readRawVarint32();
      if (size > 0 && size <= this.currentByteBufferLimit - this.currentByteBufferPos) {
        if (this.immutable && this.enableAliasing) {
          int idx = (int)(this.currentByteBufferPos - this.currentAddress);
          ByteString result = ByteString.wrap(slice(idx, idx + size));
          this.currentByteBufferPos += size;
          return result;
        } 
        byte[] bytes = new byte[size];
        UnsafeUtil.copyMemory(this.currentByteBufferPos, bytes, 0L, size);
        this.currentByteBufferPos += size;
        return ByteString.wrap(bytes);
      } 
      if (size > 0 && size <= remaining()) {
        if (this.immutable && this.enableAliasing) {
          ArrayList<ByteString> byteStrings = new ArrayList<>();
          int l = size;
          while (l > 0) {
            if (currentRemaining() == 0L)
              getNextByteBuffer(); 
            int bytesToCopy = Math.min(l, (int)currentRemaining());
            int idx = (int)(this.currentByteBufferPos - this.currentAddress);
            byteStrings.add(ByteString.wrap(slice(idx, idx + bytesToCopy)));
            l -= bytesToCopy;
            this.currentByteBufferPos += bytesToCopy;
          } 
          return ByteString.copyFrom(byteStrings);
        } 
        byte[] temp = new byte[size];
        readRawBytesTo(temp, 0, size);
        return ByteString.wrap(temp);
      } 
      if (size == 0)
        return ByteString.EMPTY; 
      if (size < 0)
        throw InvalidProtocolBufferException.negativeSize(); 
      throw InvalidProtocolBufferException.truncatedMessage();
    }
    
    public byte[] readByteArray() throws IOException {
      return readRawBytes(readRawVarint32());
    }
    
    public ByteBuffer readByteBuffer() throws IOException {
      int size = readRawVarint32();
      if (size > 0 && size <= currentRemaining()) {
        if (!this.immutable && this.enableAliasing) {
          this.currentByteBufferPos += size;
          return slice((int)(this.currentByteBufferPos - this.currentAddress - size), (int)(this.currentByteBufferPos - this.currentAddress));
        } 
        byte[] bytes = new byte[size];
        UnsafeUtil.copyMemory(this.currentByteBufferPos, bytes, 0L, size);
        this.currentByteBufferPos += size;
        return ByteBuffer.wrap(bytes);
      } 
      if (size > 0 && size <= remaining()) {
        byte[] temp = new byte[size];
        readRawBytesTo(temp, 0, size);
        return ByteBuffer.wrap(temp);
      } 
      if (size == 0)
        return Internal.EMPTY_BYTE_BUFFER; 
      if (size < 0)
        throw InvalidProtocolBufferException.negativeSize(); 
      throw InvalidProtocolBufferException.truncatedMessage();
    }
    
    public int readUInt32() throws IOException {
      return readRawVarint32();
    }
    
    public int readEnum() throws IOException {
      return readRawVarint32();
    }
    
    public int readSFixed32() throws IOException {
      return readRawLittleEndian32();
    }
    
    public long readSFixed64() throws IOException {
      return readRawLittleEndian64();
    }
    
    public int readSInt32() throws IOException {
      return decodeZigZag32(readRawVarint32());
    }
    
    public long readSInt64() throws IOException {
      return decodeZigZag64(readRawVarint64());
    }
    
    public int readRawVarint32() throws IOException {
      long tempPos = this.currentByteBufferPos;
      if (this.currentByteBufferLimit != this.currentByteBufferPos) {
        int x;
        if ((x = UnsafeUtil.getByte(tempPos++)) >= 0) {
          this.currentByteBufferPos++;
          return x;
        } 
        if (this.currentByteBufferLimit - this.currentByteBufferPos >= 10L) {
          if ((x ^= UnsafeUtil.getByte(tempPos++) << 7) < 0) {
            x ^= 0xFFFFFF80;
          } else if ((x ^= UnsafeUtil.getByte(tempPos++) << 14) >= 0) {
            x ^= 0x3F80;
          } else if ((x ^= UnsafeUtil.getByte(tempPos++) << 21) < 0) {
            x ^= 0xFFE03F80;
          } else {
            int y = UnsafeUtil.getByte(tempPos++);
            x ^= y << 28;
            x ^= 0xFE03F80;
            if (y < 0 && 
              UnsafeUtil.getByte(tempPos++) < 0 && 
              UnsafeUtil.getByte(tempPos++) < 0 && 
              UnsafeUtil.getByte(tempPos++) < 0 && 
              UnsafeUtil.getByte(tempPos++) < 0 && 
              UnsafeUtil.getByte(tempPos++) < 0)
              return (int)readRawVarint64SlowPath(); 
          } 
          this.currentByteBufferPos = tempPos;
          return x;
        } 
      } 
      return (int)readRawVarint64SlowPath();
    }
    
    public long readRawVarint64() throws IOException {
      long tempPos = this.currentByteBufferPos;
      if (this.currentByteBufferLimit != this.currentByteBufferPos) {
        int y;
        if ((y = UnsafeUtil.getByte(tempPos++)) >= 0) {
          this.currentByteBufferPos++;
          return y;
        } 
        if (this.currentByteBufferLimit - this.currentByteBufferPos >= 10L) {
          long x;
          if ((y ^= UnsafeUtil.getByte(tempPos++) << 7) < 0) {
            x = (y ^ 0xFFFFFF80);
          } else if ((y ^= UnsafeUtil.getByte(tempPos++) << 14) >= 0) {
            x = (y ^ 0x3F80);
          } else if ((y ^= UnsafeUtil.getByte(tempPos++) << 21) < 0) {
            x = (y ^ 0xFFE03F80);
          } else if ((x = y ^ UnsafeUtil.getByte(tempPos++) << 28L) >= 0L) {
            x ^= 0xFE03F80L;
          } else if ((x ^= UnsafeUtil.getByte(tempPos++) << 35L) < 0L) {
            x ^= 0xFFFFFFF80FE03F80L;
          } else if ((x ^= UnsafeUtil.getByte(tempPos++) << 42L) >= 0L) {
            x ^= 0x3F80FE03F80L;
          } else if ((x ^= UnsafeUtil.getByte(tempPos++) << 49L) < 0L) {
            x ^= 0xFFFE03F80FE03F80L;
          } else {
            x ^= UnsafeUtil.getByte(tempPos++) << 56L;
            x ^= 0xFE03F80FE03F80L;
            if (x < 0L && 
              UnsafeUtil.getByte(tempPos++) < 0L)
              return readRawVarint64SlowPath(); 
          } 
          this.currentByteBufferPos = tempPos;
          return x;
        } 
      } 
      return readRawVarint64SlowPath();
    }
    
    long readRawVarint64SlowPath() throws IOException {
      long result = 0L;
      for (int shift = 0; shift < 64; shift += 7) {
        byte b = readRawByte();
        result |= (b & Byte.MAX_VALUE) << shift;
        if ((b & 0x80) == 0)
          return result; 
      } 
      throw InvalidProtocolBufferException.malformedVarint();
    }
    
    public int readRawLittleEndian32() throws IOException {
      if (currentRemaining() >= 4L) {
        long tempPos = this.currentByteBufferPos;
        this.currentByteBufferPos += 4L;
        return UnsafeUtil.getByte(tempPos) & 0xFF | (
          UnsafeUtil.getByte(tempPos + 1L) & 0xFF) << 8 | (
          UnsafeUtil.getByte(tempPos + 2L) & 0xFF) << 16 | (
          UnsafeUtil.getByte(tempPos + 3L) & 0xFF) << 24;
      } 
      return readRawByte() & 0xFF | (
        readRawByte() & 0xFF) << 8 | (
        readRawByte() & 0xFF) << 16 | (
        readRawByte() & 0xFF) << 24;
    }
    
    public long readRawLittleEndian64() throws IOException {
      if (currentRemaining() >= 8L) {
        long tempPos = this.currentByteBufferPos;
        this.currentByteBufferPos += 8L;
        return UnsafeUtil.getByte(tempPos) & 0xFFL | (
          UnsafeUtil.getByte(tempPos + 1L) & 0xFFL) << 8L | (
          UnsafeUtil.getByte(tempPos + 2L) & 0xFFL) << 16L | (
          UnsafeUtil.getByte(tempPos + 3L) & 0xFFL) << 24L | (
          UnsafeUtil.getByte(tempPos + 4L) & 0xFFL) << 32L | (
          UnsafeUtil.getByte(tempPos + 5L) & 0xFFL) << 40L | (
          UnsafeUtil.getByte(tempPos + 6L) & 0xFFL) << 48L | (
          UnsafeUtil.getByte(tempPos + 7L) & 0xFFL) << 56L;
      } 
      return readRawByte() & 0xFFL | (
        readRawByte() & 0xFFL) << 8L | (
        readRawByte() & 0xFFL) << 16L | (
        readRawByte() & 0xFFL) << 24L | (
        readRawByte() & 0xFFL) << 32L | (
        readRawByte() & 0xFFL) << 40L | (
        readRawByte() & 0xFFL) << 48L | (
        readRawByte() & 0xFFL) << 56L;
    }
    
    public void enableAliasing(boolean enabled) {
      this.enableAliasing = enabled;
    }
    
    public void resetSizeCounter() {
      this.startOffset = (int)(this.totalBytesRead + this.currentByteBufferPos - this.currentByteBufferStartPos);
    }
    
    public int pushLimit(int byteLimit) throws InvalidProtocolBufferException {
      if (byteLimit < 0)
        throw InvalidProtocolBufferException.negativeSize(); 
      byteLimit += getTotalBytesRead();
      int oldLimit = this.currentLimit;
      if (byteLimit > oldLimit)
        throw InvalidProtocolBufferException.truncatedMessage(); 
      this.currentLimit = byteLimit;
      recomputeBufferSizeAfterLimit();
      return oldLimit;
    }
    
    private void recomputeBufferSizeAfterLimit() {
      this.totalBufferSize += this.bufferSizeAfterCurrentLimit;
      int bufferEnd = this.totalBufferSize - this.startOffset;
      if (bufferEnd > this.currentLimit) {
        this.bufferSizeAfterCurrentLimit = bufferEnd - this.currentLimit;
        this.totalBufferSize -= this.bufferSizeAfterCurrentLimit;
      } else {
        this.bufferSizeAfterCurrentLimit = 0;
      } 
    }
    
    public void popLimit(int oldLimit) {
      this.currentLimit = oldLimit;
      recomputeBufferSizeAfterLimit();
    }
    
    public int getBytesUntilLimit() {
      if (this.currentLimit == Integer.MAX_VALUE)
        return -1; 
      return this.currentLimit - getTotalBytesRead();
    }
    
    public boolean isAtEnd() throws IOException {
      return (this.totalBytesRead + this.currentByteBufferPos - this.currentByteBufferStartPos == this.totalBufferSize);
    }
    
    public int getTotalBytesRead() {
      return (int)((this.totalBytesRead - this.startOffset) + this.currentByteBufferPos - this.currentByteBufferStartPos);
    }
    
    public byte readRawByte() throws IOException {
      if (currentRemaining() == 0L)
        getNextByteBuffer(); 
      return UnsafeUtil.getByte(this.currentByteBufferPos++);
    }
    
    public byte[] readRawBytes(int length) throws IOException {
      if (length >= 0 && length <= currentRemaining()) {
        byte[] bytes = new byte[length];
        UnsafeUtil.copyMemory(this.currentByteBufferPos, bytes, 0L, length);
        this.currentByteBufferPos += length;
        return bytes;
      } 
      if (length >= 0 && length <= remaining()) {
        byte[] bytes = new byte[length];
        readRawBytesTo(bytes, 0, length);
        return bytes;
      } 
      if (length <= 0) {
        if (length == 0)
          return Internal.EMPTY_BYTE_ARRAY; 
        throw InvalidProtocolBufferException.negativeSize();
      } 
      throw InvalidProtocolBufferException.truncatedMessage();
    }
    
    private void readRawBytesTo(byte[] bytes, int offset, int length) throws IOException {
      if (length >= 0 && length <= remaining()) {
        int l = length;
        while (l > 0) {
          if (currentRemaining() == 0L)
            getNextByteBuffer(); 
          int bytesToCopy = Math.min(l, (int)currentRemaining());
          UnsafeUtil.copyMemory(this.currentByteBufferPos, bytes, (length - l + offset), bytesToCopy);
          l -= bytesToCopy;
          this.currentByteBufferPos += bytesToCopy;
        } 
        return;
      } 
      if (length <= 0) {
        if (length == 0)
          return; 
        throw InvalidProtocolBufferException.negativeSize();
      } 
      throw InvalidProtocolBufferException.truncatedMessage();
    }
    
    public void skipRawBytes(int length) throws IOException {
      if (length >= 0 && length <= (this.totalBufferSize - this.totalBytesRead) - this.currentByteBufferPos + this.currentByteBufferStartPos) {
        int l = length;
        while (l > 0) {
          if (currentRemaining() == 0L)
            getNextByteBuffer(); 
          int rl = Math.min(l, (int)currentRemaining());
          l -= rl;
          this.currentByteBufferPos += rl;
        } 
        return;
      } 
      if (length < 0)
        throw InvalidProtocolBufferException.negativeSize(); 
      throw InvalidProtocolBufferException.truncatedMessage();
    }
    
    private void skipRawVarint() throws IOException {
      for (int i = 0; i < 10; i++) {
        if (readRawByte() >= 0)
          return; 
      } 
      throw InvalidProtocolBufferException.malformedVarint();
    }
    
    private int remaining() {
      return (int)((this.totalBufferSize - this.totalBytesRead) - this.currentByteBufferPos + this.currentByteBufferStartPos);
    }
    
    private long currentRemaining() {
      return this.currentByteBufferLimit - this.currentByteBufferPos;
    }
    
    private ByteBuffer slice(int begin, int end) throws IOException {
      int prevPos = this.currentByteBuffer.position();
      int prevLimit = this.currentByteBuffer.limit();
      Buffer asBuffer = this.currentByteBuffer;
      try {
        asBuffer.position(begin);
        asBuffer.limit(end);
        return this.currentByteBuffer.slice();
      } catch (IllegalArgumentException e) {
        throw InvalidProtocolBufferException.truncatedMessage();
      } finally {
        asBuffer.position(prevPos);
        asBuffer.limit(prevLimit);
      } 
    }
  }
}
