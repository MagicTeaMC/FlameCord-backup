package com.google.protobuf;

import java.io.IOException;
import java.io.InputStream;
import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

final class RopeByteString extends ByteString {
  static final int[] minLengthByDepth = new int[] { 
      1, 1, 2, 3, 5, 8, 13, 21, 34, 55, 
      89, 144, 233, 377, 610, 987, 1597, 2584, 4181, 6765, 
      10946, 17711, 28657, 46368, 75025, 121393, 196418, 317811, 514229, 832040, 
      1346269, 2178309, 3524578, 5702887, 9227465, 14930352, 24157817, 39088169, 63245986, 102334155, 
      165580141, 267914296, 433494437, 701408733, 1134903170, 1836311903, Integer.MAX_VALUE };
  
  private final int totalLength;
  
  private final ByteString left;
  
  private final ByteString right;
  
  private final int leftLength;
  
  private final int treeDepth;
  
  private static final long serialVersionUID = 1L;
  
  private RopeByteString(ByteString left, ByteString right) {
    this.left = left;
    this.right = right;
    this.leftLength = left.size();
    this.totalLength = this.leftLength + right.size();
    this.treeDepth = Math.max(left.getTreeDepth(), right.getTreeDepth()) + 1;
  }
  
  static ByteString concatenate(ByteString left, ByteString right) {
    if (right.size() == 0)
      return left; 
    if (left.size() == 0)
      return right; 
    int newLength = left.size() + right.size();
    if (newLength < 128)
      return concatenateBytes(left, right); 
    if (left instanceof RopeByteString) {
      RopeByteString leftRope = (RopeByteString)left;
      if (leftRope.right.size() + right.size() < 128) {
        ByteString newRight = concatenateBytes(leftRope.right, right);
        return new RopeByteString(leftRope.left, newRight);
      } 
      if (leftRope.left.getTreeDepth() > leftRope.right.getTreeDepth() && leftRope
        .getTreeDepth() > right.getTreeDepth()) {
        ByteString newRight = new RopeByteString(leftRope.right, right);
        return new RopeByteString(leftRope.left, newRight);
      } 
    } 
    int newDepth = Math.max(left.getTreeDepth(), right.getTreeDepth()) + 1;
    if (newLength >= minLength(newDepth))
      return new RopeByteString(left, right); 
    return (new Balancer()).balance(left, right);
  }
  
  private static ByteString concatenateBytes(ByteString left, ByteString right) {
    int leftSize = left.size();
    int rightSize = right.size();
    byte[] bytes = new byte[leftSize + rightSize];
    left.copyTo(bytes, 0, 0, leftSize);
    right.copyTo(bytes, 0, leftSize, rightSize);
    return ByteString.wrap(bytes);
  }
  
  static RopeByteString newInstanceForTest(ByteString left, ByteString right) {
    return new RopeByteString(left, right);
  }
  
  static int minLength(int depth) {
    if (depth >= minLengthByDepth.length)
      return Integer.MAX_VALUE; 
    return minLengthByDepth[depth];
  }
  
  public byte byteAt(int index) {
    checkIndex(index, this.totalLength);
    return internalByteAt(index);
  }
  
  byte internalByteAt(int index) {
    if (index < this.leftLength)
      return this.left.internalByteAt(index); 
    return this.right.internalByteAt(index - this.leftLength);
  }
  
  public int size() {
    return this.totalLength;
  }
  
  public ByteString.ByteIterator iterator() {
    return new ByteString.AbstractByteIterator() {
        final RopeByteString.PieceIterator pieces = new RopeByteString.PieceIterator(RopeByteString.this);
        
        ByteString.ByteIterator current = nextPiece();
        
        private ByteString.ByteIterator nextPiece() {
          return this.pieces.hasNext() ? this.pieces.next().iterator() : null;
        }
        
        public boolean hasNext() {
          return (this.current != null);
        }
        
        public byte nextByte() {
          if (this.current == null)
            throw new NoSuchElementException(); 
          byte b = this.current.nextByte();
          if (!this.current.hasNext())
            this.current = nextPiece(); 
          return b;
        }
      };
  }
  
  protected int getTreeDepth() {
    return this.treeDepth;
  }
  
  protected boolean isBalanced() {
    return (this.totalLength >= minLength(this.treeDepth));
  }
  
  public ByteString substring(int beginIndex, int endIndex) {
    int length = checkRange(beginIndex, endIndex, this.totalLength);
    if (length == 0)
      return ByteString.EMPTY; 
    if (length == this.totalLength)
      return this; 
    if (endIndex <= this.leftLength)
      return this.left.substring(beginIndex, endIndex); 
    if (beginIndex >= this.leftLength)
      return this.right.substring(beginIndex - this.leftLength, endIndex - this.leftLength); 
    ByteString leftSub = this.left.substring(beginIndex);
    ByteString rightSub = this.right.substring(0, endIndex - this.leftLength);
    return new RopeByteString(leftSub, rightSub);
  }
  
  protected void copyToInternal(byte[] target, int sourceOffset, int targetOffset, int numberToCopy) {
    if (sourceOffset + numberToCopy <= this.leftLength) {
      this.left.copyToInternal(target, sourceOffset, targetOffset, numberToCopy);
    } else if (sourceOffset >= this.leftLength) {
      this.right.copyToInternal(target, sourceOffset - this.leftLength, targetOffset, numberToCopy);
    } else {
      int leftLength = this.leftLength - sourceOffset;
      this.left.copyToInternal(target, sourceOffset, targetOffset, leftLength);
      this.right.copyToInternal(target, 0, targetOffset + leftLength, numberToCopy - leftLength);
    } 
  }
  
  public void copyTo(ByteBuffer target) {
    this.left.copyTo(target);
    this.right.copyTo(target);
  }
  
  public ByteBuffer asReadOnlyByteBuffer() {
    ByteBuffer byteBuffer = ByteBuffer.wrap(toByteArray());
    return byteBuffer.asReadOnlyBuffer();
  }
  
  public List<ByteBuffer> asReadOnlyByteBufferList() {
    List<ByteBuffer> result = new ArrayList<>();
    PieceIterator pieces = new PieceIterator(this);
    while (pieces.hasNext()) {
      ByteString.LeafByteString byteString = pieces.next();
      result.add(byteString.asReadOnlyByteBuffer());
    } 
    return result;
  }
  
  public void writeTo(OutputStream outputStream) throws IOException {
    this.left.writeTo(outputStream);
    this.right.writeTo(outputStream);
  }
  
  void writeToInternal(OutputStream out, int sourceOffset, int numberToWrite) throws IOException {
    if (sourceOffset + numberToWrite <= this.leftLength) {
      this.left.writeToInternal(out, sourceOffset, numberToWrite);
    } else if (sourceOffset >= this.leftLength) {
      this.right.writeToInternal(out, sourceOffset - this.leftLength, numberToWrite);
    } else {
      int numberToWriteInLeft = this.leftLength - sourceOffset;
      this.left.writeToInternal(out, sourceOffset, numberToWriteInLeft);
      this.right.writeToInternal(out, 0, numberToWrite - numberToWriteInLeft);
    } 
  }
  
  void writeTo(ByteOutput output) throws IOException {
    this.left.writeTo(output);
    this.right.writeTo(output);
  }
  
  void writeToReverse(ByteOutput output) throws IOException {
    this.right.writeToReverse(output);
    this.left.writeToReverse(output);
  }
  
  protected String toStringInternal(Charset charset) {
    return new String(toByteArray(), charset);
  }
  
  public boolean isValidUtf8() {
    int leftPartial = this.left.partialIsValidUtf8(0, 0, this.leftLength);
    int state = this.right.partialIsValidUtf8(leftPartial, 0, this.right.size());
    return (state == 0);
  }
  
  protected int partialIsValidUtf8(int state, int offset, int length) {
    int toIndex = offset + length;
    if (toIndex <= this.leftLength)
      return this.left.partialIsValidUtf8(state, offset, length); 
    if (offset >= this.leftLength)
      return this.right.partialIsValidUtf8(state, offset - this.leftLength, length); 
    int leftLength = this.leftLength - offset;
    int leftPartial = this.left.partialIsValidUtf8(state, offset, leftLength);
    return this.right.partialIsValidUtf8(leftPartial, 0, length - leftLength);
  }
  
  public boolean equals(Object other) {
    if (other == this)
      return true; 
    if (!(other instanceof ByteString))
      return false; 
    ByteString otherByteString = (ByteString)other;
    if (this.totalLength != otherByteString.size())
      return false; 
    if (this.totalLength == 0)
      return true; 
    int thisHash = peekCachedHashCode();
    int thatHash = otherByteString.peekCachedHashCode();
    if (thisHash != 0 && thatHash != 0 && thisHash != thatHash)
      return false; 
    return equalsFragments(otherByteString);
  }
  
  private boolean equalsFragments(ByteString other) {
    int thisOffset = 0;
    Iterator<ByteString.LeafByteString> thisIter = new PieceIterator(this);
    ByteString.LeafByteString thisString = thisIter.next();
    int thatOffset = 0;
    Iterator<ByteString.LeafByteString> thatIter = new PieceIterator(other);
    ByteString.LeafByteString thatString = thatIter.next();
    int pos = 0;
    while (true) {
      int thisRemaining = thisString.size() - thisOffset;
      int thatRemaining = thatString.size() - thatOffset;
      int bytesToCompare = Math.min(thisRemaining, thatRemaining);
      boolean stillEqual = (thisOffset == 0) ? thisString.equalsRange(thatString, thatOffset, bytesToCompare) : thatString.equalsRange(thisString, thisOffset, bytesToCompare);
      if (!stillEqual)
        return false; 
      pos += bytesToCompare;
      if (pos >= this.totalLength) {
        if (pos == this.totalLength)
          return true; 
        throw new IllegalStateException();
      } 
      if (bytesToCompare == thisRemaining) {
        thisOffset = 0;
        thisString = thisIter.next();
      } else {
        thisOffset += bytesToCompare;
      } 
      if (bytesToCompare == thatRemaining) {
        thatOffset = 0;
        thatString = thatIter.next();
        continue;
      } 
      thatOffset += bytesToCompare;
    } 
  }
  
  protected int partialHash(int h, int offset, int length) {
    int toIndex = offset + length;
    if (toIndex <= this.leftLength)
      return this.left.partialHash(h, offset, length); 
    if (offset >= this.leftLength)
      return this.right.partialHash(h, offset - this.leftLength, length); 
    int leftLength = this.leftLength - offset;
    int leftPartial = this.left.partialHash(h, offset, leftLength);
    return this.right.partialHash(leftPartial, 0, length - leftLength);
  }
  
  public CodedInputStream newCodedInput() {
    return CodedInputStream.newInstance(asReadOnlyByteBufferList(), true);
  }
  
  public InputStream newInput() {
    return new RopeInputStream();
  }
  
  private static class Balancer {
    private final ArrayDeque<ByteString> prefixesStack = new ArrayDeque<>();
    
    private ByteString balance(ByteString left, ByteString right) {
      doBalance(left);
      doBalance(right);
      ByteString partialString = this.prefixesStack.pop();
      while (!this.prefixesStack.isEmpty()) {
        ByteString newLeft = this.prefixesStack.pop();
        partialString = new RopeByteString(newLeft, partialString);
      } 
      return partialString;
    }
    
    private void doBalance(ByteString root) {
      if (root.isBalanced()) {
        insert(root);
      } else if (root instanceof RopeByteString) {
        RopeByteString rbs = (RopeByteString)root;
        doBalance(rbs.left);
        doBalance(rbs.right);
      } else {
        throw new IllegalArgumentException("Has a new type of ByteString been created? Found " + root
            .getClass());
      } 
    }
    
    private void insert(ByteString byteString) {
      int depthBin = getDepthBinForLength(byteString.size());
      int binEnd = RopeByteString.minLength(depthBin + 1);
      if (this.prefixesStack.isEmpty() || ((ByteString)this.prefixesStack.peek()).size() >= binEnd) {
        this.prefixesStack.push(byteString);
      } else {
        int binStart = RopeByteString.minLength(depthBin);
        ByteString newTree = this.prefixesStack.pop();
        while (!this.prefixesStack.isEmpty() && ((ByteString)this.prefixesStack.peek()).size() < binStart) {
          ByteString left = this.prefixesStack.pop();
          newTree = new RopeByteString(left, newTree);
        } 
        newTree = new RopeByteString(newTree, byteString);
        while (!this.prefixesStack.isEmpty()) {
          depthBin = getDepthBinForLength(newTree.size());
          binEnd = RopeByteString.minLength(depthBin + 1);
          if (((ByteString)this.prefixesStack.peek()).size() < binEnd) {
            ByteString left = this.prefixesStack.pop();
            newTree = new RopeByteString(left, newTree);
          } 
        } 
        this.prefixesStack.push(newTree);
      } 
    }
    
    private int getDepthBinForLength(int length) {
      int depth = Arrays.binarySearch(RopeByteString.minLengthByDepth, length);
      if (depth < 0) {
        int insertionPoint = -(depth + 1);
        depth = insertionPoint - 1;
      } 
      return depth;
    }
    
    private Balancer() {}
  }
  
  private static final class PieceIterator implements Iterator<ByteString.LeafByteString> {
    private final ArrayDeque<RopeByteString> breadCrumbs;
    
    private ByteString.LeafByteString next;
    
    private PieceIterator(ByteString root) {
      if (root instanceof RopeByteString) {
        RopeByteString rbs = (RopeByteString)root;
        this.breadCrumbs = new ArrayDeque<>(rbs.getTreeDepth());
        this.breadCrumbs.push(rbs);
        this.next = getLeafByLeft(rbs.left);
      } else {
        this.breadCrumbs = null;
        this.next = (ByteString.LeafByteString)root;
      } 
    }
    
    private ByteString.LeafByteString getLeafByLeft(ByteString root) {
      ByteString pos = root;
      while (pos instanceof RopeByteString) {
        RopeByteString rbs = (RopeByteString)pos;
        this.breadCrumbs.push(rbs);
        pos = rbs.left;
      } 
      return (ByteString.LeafByteString)pos;
    }
    
    private ByteString.LeafByteString getNextNonEmptyLeaf() {
      while (true) {
        if (this.breadCrumbs == null || this.breadCrumbs.isEmpty())
          return null; 
        ByteString.LeafByteString result = getLeafByLeft((this.breadCrumbs.pop()).right);
        if (!result.isEmpty())
          return result; 
      } 
    }
    
    public boolean hasNext() {
      return (this.next != null);
    }
    
    public ByteString.LeafByteString next() {
      if (this.next == null)
        throw new NoSuchElementException(); 
      ByteString.LeafByteString result = this.next;
      this.next = getNextNonEmptyLeaf();
      return result;
    }
    
    public void remove() {
      throw new UnsupportedOperationException();
    }
  }
  
  Object writeReplace() {
    return ByteString.wrap(toByteArray());
  }
  
  private void readObject(ObjectInputStream in) throws IOException {
    throw new InvalidObjectException("RopeByteStream instances are not to be serialized directly");
  }
  
  private class RopeInputStream extends InputStream {
    private RopeByteString.PieceIterator pieceIterator;
    
    private ByteString.LeafByteString currentPiece;
    
    private int currentPieceSize;
    
    private int currentPieceIndex;
    
    private int currentPieceOffsetInRope;
    
    private int mark;
    
    public RopeInputStream() {
      initialize();
    }
    
    public int read(byte[] b, int offset, int length) {
      if (b == null)
        throw new NullPointerException(); 
      if (offset < 0 || length < 0 || length > b.length - offset)
        throw new IndexOutOfBoundsException(); 
      int bytesRead = readSkipInternal(b, offset, length);
      if (bytesRead == 0 && (length > 0 || availableInternal() == 0))
        return -1; 
      return bytesRead;
    }
    
    public long skip(long length) {
      if (length < 0L)
        throw new IndexOutOfBoundsException(); 
      if (length > 2147483647L)
        length = 2147483647L; 
      return readSkipInternal(null, 0, (int)length);
    }
    
    private int readSkipInternal(byte[] b, int offset, int length) {
      int bytesRemaining = length;
      while (bytesRemaining > 0) {
        advanceIfCurrentPieceFullyRead();
        if (this.currentPiece == null)
          break; 
        int currentPieceRemaining = this.currentPieceSize - this.currentPieceIndex;
        int count = Math.min(currentPieceRemaining, bytesRemaining);
        if (b != null) {
          this.currentPiece.copyTo(b, this.currentPieceIndex, offset, count);
          offset += count;
        } 
        this.currentPieceIndex += count;
        bytesRemaining -= count;
      } 
      return length - bytesRemaining;
    }
    
    public int read() throws IOException {
      advanceIfCurrentPieceFullyRead();
      if (this.currentPiece == null)
        return -1; 
      return this.currentPiece.byteAt(this.currentPieceIndex++) & 0xFF;
    }
    
    public int available() throws IOException {
      return availableInternal();
    }
    
    public boolean markSupported() {
      return true;
    }
    
    public void mark(int readAheadLimit) {
      this.mark = this.currentPieceOffsetInRope + this.currentPieceIndex;
    }
    
    public synchronized void reset() {
      initialize();
      readSkipInternal(null, 0, this.mark);
    }
    
    private void initialize() {
      this.pieceIterator = new RopeByteString.PieceIterator(RopeByteString.this);
      this.currentPiece = this.pieceIterator.next();
      this.currentPieceSize = this.currentPiece.size();
      this.currentPieceIndex = 0;
      this.currentPieceOffsetInRope = 0;
    }
    
    private void advanceIfCurrentPieceFullyRead() {
      if (this.currentPiece != null && this.currentPieceIndex == this.currentPieceSize) {
        this.currentPieceOffsetInRope += this.currentPieceSize;
        this.currentPieceIndex = 0;
        if (this.pieceIterator.hasNext()) {
          this.currentPiece = this.pieceIterator.next();
          this.currentPieceSize = this.currentPiece.size();
        } else {
          this.currentPiece = null;
          this.currentPieceSize = 0;
        } 
      } 
    }
    
    private int availableInternal() {
      int bytesRead = this.currentPieceOffsetInRope + this.currentPieceIndex;
      return RopeByteString.this.size() - bytesRead;
    }
  }
}
