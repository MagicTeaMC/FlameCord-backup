package gnu.trove.impl.hash;

import gnu.trove.impl.PrimeFinder;
import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

public abstract class THash implements Externalizable {
  static final long serialVersionUID = -1792948471915530295L;
  
  protected static final float DEFAULT_LOAD_FACTOR = 0.5F;
  
  protected static final int DEFAULT_CAPACITY = 10;
  
  protected transient int _size;
  
  protected transient int _free;
  
  protected float _loadFactor;
  
  protected int _maxSize;
  
  protected int _autoCompactRemovesRemaining;
  
  protected float _autoCompactionFactor;
  
  protected transient boolean _autoCompactTemporaryDisable = false;
  
  public THash() {
    this(10, 0.5F);
  }
  
  public THash(int initialCapacity) {
    this(initialCapacity, 0.5F);
  }
  
  public THash(int initialCapacity, float loadFactor) {
    if (initialCapacity < 0)
      throw new IllegalArgumentException("negative capacity: " + initialCapacity); 
    if (0.0F >= loadFactor)
      throw new IllegalArgumentException("load factor out of range: " + loadFactor); 
    this._loadFactor = loadFactor;
    this._autoCompactionFactor = loadFactor;
    setUp(saturatedCast(fastCeil(initialCapacity / loadFactor)));
  }
  
  protected static long fastCeil(double v) {
    long possible_result = (long)v;
    if (v - possible_result > 0.0D)
      possible_result++; 
    return possible_result;
  }
  
  protected static int saturatedCast(long v) {
    int r = (int)(v & 0x7FFFFFFFL);
    if (r != v)
      return Integer.MAX_VALUE; 
    return r;
  }
  
  public boolean isEmpty() {
    return (0 == this._size);
  }
  
  public int size() {
    return this._size;
  }
  
  public abstract int capacity();
  
  public void ensureCapacity(int desiredCapacity) {
    if (desiredCapacity > this._maxSize - size()) {
      rehash(PrimeFinder.nextPrime(Math.max(this._size + 1, 
              saturatedCast(fastCeil((desiredCapacity + this._size) / this._loadFactor) + 1L))));
      if (capacity() >= PrimeFinder.largestPrime)
        this._loadFactor = 1.0F; 
      computeMaxSize(capacity());
    } 
  }
  
  public void compact() {
    rehash(PrimeFinder.nextPrime(Math.max(this._size + 1, 
            saturatedCast(fastCeil(this._size / this._loadFactor) + 1L))));
    computeMaxSize(capacity());
    if (this._autoCompactionFactor != 0.0F)
      computeNextAutoCompactionAmount(size()); 
  }
  
  public void setAutoCompactionFactor(float factor) {
    if (factor < 0.0F)
      throw new IllegalArgumentException("Factor must be >= 0: " + factor); 
    this._autoCompactionFactor = factor;
  }
  
  public float getAutoCompactionFactor() {
    return this._autoCompactionFactor;
  }
  
  public final void trimToSize() {
    compact();
  }
  
  protected void removeAt(int index) {
    this._size--;
    if (this._autoCompactionFactor != 0.0F) {
      this._autoCompactRemovesRemaining--;
      if (!this._autoCompactTemporaryDisable && this._autoCompactRemovesRemaining <= 0)
        compact(); 
    } 
  }
  
  public void clear() {
    this._size = 0;
    this._free = capacity();
  }
  
  protected int setUp(int initialCapacity) {
    int capacity = PrimeFinder.nextPrime(initialCapacity);
    if (capacity >= PrimeFinder.largestPrime)
      this._loadFactor = 1.0F; 
    computeMaxSize(capacity);
    computeNextAutoCompactionAmount(initialCapacity);
    return capacity;
  }
  
  protected abstract void rehash(int paramInt);
  
  public void tempDisableAutoCompaction() {
    this._autoCompactTemporaryDisable = true;
  }
  
  public void reenableAutoCompaction(boolean check_for_compaction) {
    this._autoCompactTemporaryDisable = false;
    if (check_for_compaction && this._autoCompactRemovesRemaining <= 0 && this._autoCompactionFactor != 0.0F)
      compact(); 
  }
  
  protected void computeMaxSize(int capacity) {
    this._maxSize = Math.min(capacity - 1, (int)(capacity * this._loadFactor));
    this._free = capacity - this._size;
  }
  
  protected void computeNextAutoCompactionAmount(int size) {
    if (this._autoCompactionFactor != 0.0F)
      this._autoCompactRemovesRemaining = (int)(size * this._autoCompactionFactor + 0.5F); 
  }
  
  protected final void postInsertHook(boolean usedFreeSlot) {
    if (usedFreeSlot)
      this._free--; 
    if (++this._size > this._maxSize || this._free == 0) {
      int newCapacity = (this._size > this._maxSize) ? PrimeFinder.nextPrime(capacity() << 1) : capacity();
      rehash(newCapacity);
      computeMaxSize(capacity());
    } 
  }
  
  protected int calculateGrownCapacity() {
    return capacity() << 1;
  }
  
  public void writeExternal(ObjectOutput out) throws IOException {
    out.writeByte(0);
    out.writeFloat(this._loadFactor);
    out.writeFloat(this._autoCompactionFactor);
  }
  
  public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
    in.readByte();
    float old_factor = this._loadFactor;
    this._loadFactor = Math.abs(in.readFloat());
    this._autoCompactionFactor = in.readFloat();
    if (old_factor != this._loadFactor)
      setUp(saturatedCast((long)Math.ceil(10.0D / this._loadFactor))); 
  }
}
