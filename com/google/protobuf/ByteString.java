package com.google.protobuf;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.charset.UnsupportedCharsetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.NoSuchElementException;

@CheckReturnValue
public abstract class ByteString implements Iterable<Byte>, Serializable {
  static final int CONCATENATE_BY_COPY_SIZE = 128;
  
  static final int MIN_READ_FROM_CHUNK_SIZE = 256;
  
  static final int MAX_READ_FROM_CHUNK_SIZE = 8192;
  
  public static final ByteString EMPTY = new LiteralByteString(Internal.EMPTY_BYTE_ARRAY);
  
  private static final class SystemByteArrayCopier implements ByteArrayCopier {
    private SystemByteArrayCopier() {}
    
    public byte[] copyFrom(byte[] bytes, int offset, int size) {
      byte[] copy = new byte[size];
      System.arraycopy(bytes, offset, copy, 0, size);
      return copy;
    }
  }
  
  private static final class ArraysByteArrayCopier implements ByteArrayCopier {
    private ArraysByteArrayCopier() {}
    
    public byte[] copyFrom(byte[] bytes, int offset, int size) {
      return Arrays.copyOfRange(bytes, offset, offset + size);
    }
  }
  
  private static final ByteArrayCopier byteArrayCopier = Android.isOnAndroidDevice() ? new SystemByteArrayCopier() : new ArraysByteArrayCopier();
  
  private int hash = 0;
  
  private static final int UNSIGNED_BYTE_MASK = 255;
  
  public ByteIterator iterator() {
    return new AbstractByteIterator() {
        private int position = 0;
        
        private final int limit = ByteString.this.size();
        
        public boolean hasNext() {
          return (this.position < this.limit);
        }
        
        public byte nextByte() {
          int currentPos = this.position;
          if (currentPos >= this.limit)
            throw new NoSuchElementException(); 
          this.position = currentPos + 1;
          return ByteString.this.internalByteAt(currentPos);
        }
      };
  }
  
  static abstract class AbstractByteIterator implements ByteIterator {
    public final Byte next() {
      return Byte.valueOf(nextByte());
    }
    
    public final void remove() {
      throw new UnsupportedOperationException();
    }
  }
  
  public final boolean isEmpty() {
    return (size() == 0);
  }
  
  public static final ByteString empty() {
    return EMPTY;
  }
  
  private static int toInt(byte value) {
    return value & 0xFF;
  }
  
  private static int hexDigit(char c) {
    if (c >= '0' && c <= '9')
      return c - 48; 
    if (c >= 'A' && c <= 'F')
      return c - 65 + 10; 
    if (c >= 'a' && c <= 'f')
      return c - 97 + 10; 
    return -1;
  }
  
  private static int extractHexDigit(String hexString, int index) {
    int digit = hexDigit(hexString.charAt(index));
    if (digit == -1)
      throw new NumberFormatException("Invalid hexString " + hexString + " must only contain [0-9a-fA-F] but contained " + hexString
          
          .charAt(index) + " at index " + index); 
    return digit;
  }
  
  private static final Comparator<ByteString> UNSIGNED_LEXICOGRAPHICAL_COMPARATOR = new Comparator<ByteString>() {
      public int compare(ByteString former, ByteString latter) {
        ByteString.ByteIterator formerBytes = former.iterator();
        ByteString.ByteIterator latterBytes = latter.iterator();
        while (formerBytes.hasNext() && latterBytes.hasNext()) {
          int result = Integer.valueOf(ByteString.toInt(formerBytes.nextByte())).compareTo(Integer.valueOf(ByteString.toInt(latterBytes.nextByte())));
          if (result != 0)
            return result; 
        } 
        return Integer.valueOf(former.size()).compareTo(Integer.valueOf(latter.size()));
      }
    };
  
  public static Comparator<ByteString> unsignedLexicographicalComparator() {
    return UNSIGNED_LEXICOGRAPHICAL_COMPARATOR;
  }
  
  public final ByteString substring(int beginIndex) {
    return substring(beginIndex, size());
  }
  
  public final boolean startsWith(ByteString prefix) {
    return (size() >= prefix.size() && substring(0, prefix.size()).equals(prefix));
  }
  
  public final boolean endsWith(ByteString suffix) {
    return (size() >= suffix.size() && substring(size() - suffix.size()).equals(suffix));
  }
  
  public static ByteString fromHex(@CompileTimeConstant String hexString) {
    if (hexString.length() % 2 != 0)
      throw new NumberFormatException("Invalid hexString " + hexString + " of length " + hexString
          .length() + " must be even."); 
    byte[] bytes = new byte[hexString.length() / 2];
    for (int i = 0; i < bytes.length; i++) {
      int d1 = extractHexDigit(hexString, 2 * i);
      int d2 = extractHexDigit(hexString, 2 * i + 1);
      bytes[i] = (byte)(d1 << 4 | d2);
    } 
    return new LiteralByteString(bytes);
  }
  
  public static ByteString copyFrom(byte[] bytes, int offset, int size) {
    checkRange(offset, offset + size, bytes.length);
    return new LiteralByteString(byteArrayCopier.copyFrom(bytes, offset, size));
  }
  
  public static ByteString copyFrom(byte[] bytes) {
    return copyFrom(bytes, 0, bytes.length);
  }
  
  static ByteString wrap(ByteBuffer buffer) {
    if (buffer.hasArray()) {
      int offset = buffer.arrayOffset();
      return wrap(buffer.array(), offset + buffer.position(), buffer.remaining());
    } 
    return new NioByteString(buffer);
  }
  
  static ByteString wrap(byte[] bytes) {
    return new LiteralByteString(bytes);
  }
  
  static ByteString wrap(byte[] bytes, int offset, int length) {
    return new BoundedByteString(bytes, offset, length);
  }
  
  public static ByteString copyFrom(ByteBuffer bytes, int size) {
    checkRange(0, size, bytes.remaining());
    byte[] copy = new byte[size];
    bytes.get(copy);
    return new LiteralByteString(copy);
  }
  
  public static ByteString copyFrom(ByteBuffer bytes) {
    return copyFrom(bytes, bytes.remaining());
  }
  
  public static ByteString copyFrom(String text, String charsetName) throws UnsupportedEncodingException {
    return new LiteralByteString(text.getBytes(charsetName));
  }
  
  public static ByteString copyFrom(String text, Charset charset) {
    return new LiteralByteString(text.getBytes(charset));
  }
  
  public static ByteString copyFromUtf8(String text) {
    return new LiteralByteString(text.getBytes(Internal.UTF_8));
  }
  
  public static ByteString readFrom(InputStream streamToDrain) throws IOException {
    return readFrom(streamToDrain, 256, 8192);
  }
  
  public static ByteString readFrom(InputStream streamToDrain, int chunkSize) throws IOException {
    return readFrom(streamToDrain, chunkSize, chunkSize);
  }
  
  public static ByteString readFrom(InputStream streamToDrain, int minChunkSize, int maxChunkSize) throws IOException {
    Collection<ByteString> results = new ArrayList<>();
    int chunkSize = minChunkSize;
    while (true) {
      ByteString chunk = readChunk(streamToDrain, chunkSize);
      if (chunk == null)
        break; 
      results.add(chunk);
      chunkSize = Math.min(chunkSize * 2, maxChunkSize);
    } 
    return copyFrom(results);
  }
  
  private static ByteString readChunk(InputStream in, int chunkSize) throws IOException {
    byte[] buf = new byte[chunkSize];
    int bytesRead = 0;
    while (bytesRead < chunkSize) {
      int count = in.read(buf, bytesRead, chunkSize - bytesRead);
      if (count == -1)
        break; 
      bytesRead += count;
    } 
    if (bytesRead == 0)
      return null; 
    return copyFrom(buf, 0, bytesRead);
  }
  
  public final ByteString concat(ByteString other) {
    if (Integer.MAX_VALUE - size() < other.size())
      throw new IllegalArgumentException("ByteString would be too long: " + 
          size() + "+" + other.size()); 
    return RopeByteString.concatenate(this, other);
  }
  
  public static ByteString copyFrom(Iterable<ByteString> byteStrings) {
    int size;
    if (!(byteStrings instanceof Collection)) {
      int tempSize = 0;
      Iterator<ByteString> iter = byteStrings.iterator();
      while (iter.hasNext()) {
        iter.next();
        tempSize++;
      } 
      size = tempSize;
    } else {
      size = ((Collection)byteStrings).size();
    } 
    if (size == 0)
      return EMPTY; 
    return balancedConcat(byteStrings.iterator(), size);
  }
  
  private static ByteString balancedConcat(Iterator<ByteString> iterator, int length) {
    ByteString result;
    if (length < 1)
      throw new IllegalArgumentException(String.format("length (%s) must be >= 1", new Object[] { Integer.valueOf(length) })); 
    if (length == 1) {
      result = iterator.next();
    } else {
      int halfLength = length >>> 1;
      ByteString left = balancedConcat(iterator, halfLength);
      ByteString right = balancedConcat(iterator, length - halfLength);
      result = left.concat(right);
    } 
    return result;
  }
  
  public void copyTo(byte[] target, int offset) {
    copyTo(target, 0, offset, size());
  }
  
  @Deprecated
  public final void copyTo(byte[] target, int sourceOffset, int targetOffset, int numberToCopy) {
    checkRange(sourceOffset, sourceOffset + numberToCopy, size());
    checkRange(targetOffset, targetOffset + numberToCopy, target.length);
    if (numberToCopy > 0)
      copyToInternal(target, sourceOffset, targetOffset, numberToCopy); 
  }
  
  public final byte[] toByteArray() {
    int size = size();
    if (size == 0)
      return Internal.EMPTY_BYTE_ARRAY; 
    byte[] result = new byte[size];
    copyToInternal(result, 0, 0, size);
    return result;
  }
  
  final void writeTo(OutputStream out, int sourceOffset, int numberToWrite) throws IOException {
    checkRange(sourceOffset, sourceOffset + numberToWrite, size());
    if (numberToWrite > 0)
      writeToInternal(out, sourceOffset, numberToWrite); 
  }
  
  public final String toString(String charsetName) throws UnsupportedEncodingException {
    try {
      return toString(Charset.forName(charsetName));
    } catch (UnsupportedCharsetException e) {
      UnsupportedEncodingException exception = new UnsupportedEncodingException(charsetName);
      exception.initCause(e);
      throw exception;
    } 
  }
  
  public final String toString(Charset charset) {
    return (size() == 0) ? "" : toStringInternal(charset);
  }
  
  public final String toStringUtf8() {
    return toString(Internal.UTF_8);
  }
  
  static abstract class LeafByteString extends ByteString {
    protected final int getTreeDepth() {
      return 0;
    }
    
    protected final boolean isBalanced() {
      return true;
    }
    
    void writeToReverse(ByteOutput byteOutput) throws IOException {
      writeTo(byteOutput);
    }
    
    abstract boolean equalsRange(ByteString param1ByteString, int param1Int1, int param1Int2);
  }
  
  public final int hashCode() {
    int h = this.hash;
    if (h == 0) {
      int size = size();
      h = partialHash(size, 0, size);
      if (h == 0)
        h = 1; 
      this.hash = h;
    } 
    return h;
  }
  
  public static Output newOutput(int initialCapacity) {
    return new Output(initialCapacity);
  }
  
  public static Output newOutput() {
    return new Output(128);
  }
  
  public static final class Output extends OutputStream {
    private static final byte[] EMPTY_BYTE_ARRAY = new byte[0];
    
    private final int initialCapacity;
    
    private final ArrayList<ByteString> flushedBuffers;
    
    private int flushedBuffersTotalBytes;
    
    private byte[] buffer;
    
    private int bufferPos;
    
    Output(int initialCapacity) {
      if (initialCapacity < 0)
        throw new IllegalArgumentException("Buffer size < 0"); 
      this.initialCapacity = initialCapacity;
      this.flushedBuffers = new ArrayList<>();
      this.buffer = new byte[initialCapacity];
    }
    
    public synchronized void write(int b) {
      if (this.bufferPos == this.buffer.length)
        flushFullBuffer(1); 
      this.buffer[this.bufferPos++] = (byte)b;
    }
    
    public synchronized void write(byte[] b, int offset, int length) {
      if (length <= this.buffer.length - this.bufferPos) {
        System.arraycopy(b, offset, this.buffer, this.bufferPos, length);
        this.bufferPos += length;
      } else {
        int copySize = this.buffer.length - this.bufferPos;
        System.arraycopy(b, offset, this.buffer, this.bufferPos, copySize);
        offset += copySize;
        length -= copySize;
        flushFullBuffer(length);
        System.arraycopy(b, offset, this.buffer, 0, length);
        this.bufferPos = length;
      } 
    }
    
    public synchronized ByteString toByteString() {
      flushLastBuffer();
      return ByteString.copyFrom(this.flushedBuffers);
    }
    
    private byte[] copyArray(byte[] buffer, int length) {
      byte[] result = new byte[length];
      System.arraycopy(buffer, 0, result, 0, Math.min(buffer.length, length));
      return result;
    }
    
    public void writeTo(OutputStream out) throws IOException {
      ByteString[] cachedFlushBuffers;
      byte[] cachedBuffer;
      int cachedBufferPos;
      synchronized (this) {
        cachedFlushBuffers = this.flushedBuffers.<ByteString>toArray(new ByteString[this.flushedBuffers.size()]);
        cachedBuffer = this.buffer;
        cachedBufferPos = this.bufferPos;
      } 
      for (ByteString byteString : cachedFlushBuffers)
        byteString.writeTo(out); 
      out.write(copyArray(cachedBuffer, cachedBufferPos));
    }
    
    public synchronized int size() {
      return this.flushedBuffersTotalBytes + this.bufferPos;
    }
    
    public synchronized void reset() {
      this.flushedBuffers.clear();
      this.flushedBuffersTotalBytes = 0;
      this.bufferPos = 0;
    }
    
    public String toString() {
      return String.format("<ByteString.Output@%s size=%d>", new Object[] { Integer.toHexString(System.identityHashCode(this)), Integer.valueOf(size()) });
    }
    
    private void flushFullBuffer(int minSize) {
      this.flushedBuffers.add(new ByteString.LiteralByteString(this.buffer));
      this.flushedBuffersTotalBytes += this.buffer.length;
      int newSize = Math.max(this.initialCapacity, Math.max(minSize, this.flushedBuffersTotalBytes >>> 1));
      this.buffer = new byte[newSize];
      this.bufferPos = 0;
    }
    
    private void flushLastBuffer() {
      if (this.bufferPos < this.buffer.length) {
        if (this.bufferPos > 0) {
          byte[] bufferCopy = copyArray(this.buffer, this.bufferPos);
          this.flushedBuffers.add(new ByteString.LiteralByteString(bufferCopy));
        } 
      } else {
        this.flushedBuffers.add(new ByteString.LiteralByteString(this.buffer));
        this.buffer = EMPTY_BYTE_ARRAY;
      } 
      this.flushedBuffersTotalBytes += this.bufferPos;
      this.bufferPos = 0;
    }
  }
  
  static CodedBuilder newCodedBuilder(int size) {
    return new CodedBuilder(size);
  }
  
  static final class CodedBuilder {
    private final CodedOutputStream output;
    
    private final byte[] buffer;
    
    private CodedBuilder(int size) {
      this.buffer = new byte[size];
      this.output = CodedOutputStream.newInstance(this.buffer);
    }
    
    public ByteString build() {
      this.output.checkNoSpaceLeft();
      return new ByteString.LiteralByteString(this.buffer);
    }
    
    public CodedOutputStream getCodedOutput() {
      return this.output;
    }
  }
  
  protected final int peekCachedHashCode() {
    return this.hash;
  }
  
  static void checkIndex(int index, int size) {
    if ((index | size - index + 1) < 0) {
      if (index < 0)
        throw new ArrayIndexOutOfBoundsException("Index < 0: " + index); 
      throw new ArrayIndexOutOfBoundsException("Index > length: " + index + ", " + size);
    } 
  }
  
  @CanIgnoreReturnValue
  static int checkRange(int startIndex, int endIndex, int size) {
    int length = endIndex - startIndex;
    if ((startIndex | endIndex | length | size - endIndex) < 0) {
      if (startIndex < 0)
        throw new IndexOutOfBoundsException("Beginning index: " + startIndex + " < 0"); 
      if (endIndex < startIndex)
        throw new IndexOutOfBoundsException("Beginning index larger than ending index: " + startIndex + ", " + endIndex); 
      throw new IndexOutOfBoundsException("End index: " + endIndex + " >= " + size);
    } 
    return length;
  }
  
  public final String toString() {
    return String.format(Locale.ROOT, "<ByteString@%s size=%d contents=\"%s\">", new Object[] { Integer.toHexString(System.identityHashCode(this)), 
          Integer.valueOf(size()), 
          truncateAndEscapeForDisplay() });
  }
  
  private String truncateAndEscapeForDisplay() {
    int limit = 50;
    return (size() <= 50) ? TextFormatEscaper.escapeBytes(this) : (TextFormatEscaper.escapeBytes(substring(0, 47)) + "...");
  }
  
  public abstract byte byteAt(int paramInt);
  
  abstract byte internalByteAt(int paramInt);
  
  public abstract int size();
  
  public abstract ByteString substring(int paramInt1, int paramInt2);
  
  protected abstract void copyToInternal(byte[] paramArrayOfbyte, int paramInt1, int paramInt2, int paramInt3);
  
  public abstract void copyTo(ByteBuffer paramByteBuffer);
  
  public abstract void writeTo(OutputStream paramOutputStream) throws IOException;
  
  abstract void writeToInternal(OutputStream paramOutputStream, int paramInt1, int paramInt2) throws IOException;
  
  abstract void writeTo(ByteOutput paramByteOutput) throws IOException;
  
  abstract void writeToReverse(ByteOutput paramByteOutput) throws IOException;
  
  public abstract ByteBuffer asReadOnlyByteBuffer();
  
  public abstract List<ByteBuffer> asReadOnlyByteBufferList();
  
  protected abstract String toStringInternal(Charset paramCharset);
  
  public abstract boolean isValidUtf8();
  
  protected abstract int partialIsValidUtf8(int paramInt1, int paramInt2, int paramInt3);
  
  public abstract boolean equals(Object paramObject);
  
  public abstract InputStream newInput();
  
  public abstract CodedInputStream newCodedInput();
  
  protected abstract int getTreeDepth();
  
  protected abstract boolean isBalanced();
  
  protected abstract int partialHash(int paramInt1, int paramInt2, int paramInt3);
  
  private static class LiteralByteString extends LeafByteString {
    private static final long serialVersionUID = 1L;
    
    protected final byte[] bytes;
    
    LiteralByteString(byte[] bytes) {
      if (bytes == null)
        throw new NullPointerException(); 
      this.bytes = bytes;
    }
    
    public byte byteAt(int index) {
      return this.bytes[index];
    }
    
    byte internalByteAt(int index) {
      return this.bytes[index];
    }
    
    public int size() {
      return this.bytes.length;
    }
    
    public final ByteString substring(int beginIndex, int endIndex) {
      int length = checkRange(beginIndex, endIndex, size());
      if (length == 0)
        return ByteString.EMPTY; 
      return new ByteString.BoundedByteString(this.bytes, getOffsetIntoBytes() + beginIndex, length);
    }
    
    protected void copyToInternal(byte[] target, int sourceOffset, int targetOffset, int numberToCopy) {
      System.arraycopy(this.bytes, sourceOffset, target, targetOffset, numberToCopy);
    }
    
    public final void copyTo(ByteBuffer target) {
      target.put(this.bytes, getOffsetIntoBytes(), size());
    }
    
    public final ByteBuffer asReadOnlyByteBuffer() {
      return ByteBuffer.wrap(this.bytes, getOffsetIntoBytes(), size()).asReadOnlyBuffer();
    }
    
    public final List<ByteBuffer> asReadOnlyByteBufferList() {
      return Collections.singletonList(asReadOnlyByteBuffer());
    }
    
    public final void writeTo(OutputStream outputStream) throws IOException {
      outputStream.write(toByteArray());
    }
    
    final void writeToInternal(OutputStream outputStream, int sourceOffset, int numberToWrite) throws IOException {
      outputStream.write(this.bytes, getOffsetIntoBytes() + sourceOffset, numberToWrite);
    }
    
    final void writeTo(ByteOutput output) throws IOException {
      output.writeLazy(this.bytes, getOffsetIntoBytes(), size());
    }
    
    protected final String toStringInternal(Charset charset) {
      return new String(this.bytes, getOffsetIntoBytes(), size(), charset);
    }
    
    public final boolean isValidUtf8() {
      int offset = getOffsetIntoBytes();
      return Utf8.isValidUtf8(this.bytes, offset, offset + size());
    }
    
    protected final int partialIsValidUtf8(int state, int offset, int length) {
      int index = getOffsetIntoBytes() + offset;
      return Utf8.partialIsValidUtf8(state, this.bytes, index, index + length);
    }
    
    public final boolean equals(Object other) {
      if (other == this)
        return true; 
      if (!(other instanceof ByteString))
        return false; 
      if (size() != ((ByteString)other).size())
        return false; 
      if (size() == 0)
        return true; 
      if (other instanceof LiteralByteString) {
        LiteralByteString otherAsLiteral = (LiteralByteString)other;
        int thisHash = peekCachedHashCode();
        int thatHash = otherAsLiteral.peekCachedHashCode();
        if (thisHash != 0 && thatHash != 0 && thisHash != thatHash)
          return false; 
        return equalsRange((LiteralByteString)other, 0, size());
      } 
      return other.equals(this);
    }
    
    final boolean equalsRange(ByteString other, int offset, int length) {
      if (length > other.size())
        throw new IllegalArgumentException("Length too large: " + length + size()); 
      if (offset + length > other.size())
        throw new IllegalArgumentException("Ran off end of other: " + offset + ", " + length + ", " + other
            .size()); 
      if (other instanceof LiteralByteString) {
        LiteralByteString lbsOther = (LiteralByteString)other;
        byte[] thisBytes = this.bytes;
        byte[] otherBytes = lbsOther.bytes;
        int thisLimit = getOffsetIntoBytes() + length;
        int thisIndex = getOffsetIntoBytes();
        int otherIndex = lbsOther.getOffsetIntoBytes() + offset;
        for (; thisIndex < thisLimit; 
          thisIndex++, otherIndex++) {
          if (thisBytes[thisIndex] != otherBytes[otherIndex])
            return false; 
        } 
        return true;
      } 
      return other.substring(offset, offset + length).equals(substring(0, length));
    }
    
    protected final int partialHash(int h, int offset, int length) {
      return Internal.partialHash(h, this.bytes, getOffsetIntoBytes() + offset, length);
    }
    
    public final InputStream newInput() {
      return new ByteArrayInputStream(this.bytes, getOffsetIntoBytes(), size());
    }
    
    public final CodedInputStream newCodedInput() {
      return CodedInputStream.newInstance(this.bytes, 
          getOffsetIntoBytes(), size(), true);
    }
    
    protected int getOffsetIntoBytes() {
      return 0;
    }
  }
  
  private static final class BoundedByteString extends LiteralByteString {
    private final int bytesOffset;
    
    private final int bytesLength;
    
    private static final long serialVersionUID = 1L;
    
    BoundedByteString(byte[] bytes, int offset, int length) {
      super(bytes);
      checkRange(offset, offset + length, bytes.length);
      this.bytesOffset = offset;
      this.bytesLength = length;
    }
    
    public byte byteAt(int index) {
      checkIndex(index, size());
      return this.bytes[this.bytesOffset + index];
    }
    
    byte internalByteAt(int index) {
      return this.bytes[this.bytesOffset + index];
    }
    
    public int size() {
      return this.bytesLength;
    }
    
    protected int getOffsetIntoBytes() {
      return this.bytesOffset;
    }
    
    protected void copyToInternal(byte[] target, int sourceOffset, int targetOffset, int numberToCopy) {
      System.arraycopy(this.bytes, 
          getOffsetIntoBytes() + sourceOffset, target, targetOffset, numberToCopy);
    }
    
    Object writeReplace() {
      return ByteString.wrap(toByteArray());
    }
    
    private void readObject(ObjectInputStream in) throws IOException {
      throw new InvalidObjectException("BoundedByteStream instances are not to be serialized directly");
    }
  }
  
  public static interface ByteIterator extends Iterator<Byte> {
    byte nextByte();
  }
  
  private static interface ByteArrayCopier {
    byte[] copyFrom(byte[] param1ArrayOfbyte, int param1Int1, int param1Int2);
  }
}
