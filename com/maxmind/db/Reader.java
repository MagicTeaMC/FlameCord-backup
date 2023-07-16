package com.maxmind.db;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.nio.ByteBuffer;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicReference;

public final class Reader implements Closeable {
  private static final int DATA_SECTION_SEPARATOR_SIZE = 16;
  
  private static final byte[] METADATA_START_MARKER = new byte[] { 
      -85, -51, -17, 77, 97, 120, 77, 105, 110, 100, 
      46, 99, 111, 109 };
  
  private final int ipV4Start;
  
  private final Metadata metadata;
  
  private final AtomicReference<BufferHolder> bufferHolderReference;
  
  private final NodeCache cache;
  
  private final ConcurrentHashMap<Class, CachedConstructor> constructors;
  
  public enum FileMode {
    MEMORY_MAPPED, MEMORY;
  }
  
  public Reader(File database) throws IOException {
    this(database, NoCache.getInstance());
  }
  
  public Reader(File database, NodeCache cache) throws IOException {
    this(database, FileMode.MEMORY_MAPPED, cache);
  }
  
  public Reader(InputStream source) throws IOException {
    this(source, NoCache.getInstance());
  }
  
  public Reader(InputStream source, NodeCache cache) throws IOException {
    this(new BufferHolder(source), "<InputStream>", cache);
  }
  
  public Reader(File database, FileMode fileMode) throws IOException {
    this(database, fileMode, NoCache.getInstance());
  }
  
  public Reader(File database, FileMode fileMode, NodeCache cache) throws IOException {
    this(new BufferHolder(database, fileMode), database.getName(), cache);
  }
  
  private Reader(BufferHolder bufferHolder, String name, NodeCache cache) throws IOException {
    this.bufferHolderReference = new AtomicReference<>(bufferHolder);
    if (cache == null)
      throw new NullPointerException("Cache cannot be null"); 
    this.cache = cache;
    ByteBuffer buffer = bufferHolder.get();
    int start = findMetadataStart(buffer, name);
    Decoder metadataDecoder = new Decoder(this.cache, buffer, start);
    this.metadata = metadataDecoder.<Metadata>decode(start, Metadata.class);
    this.ipV4Start = findIpV4StartNode(buffer);
    this.constructors = new ConcurrentHashMap<>();
  }
  
  public <T> T get(InetAddress ipAddress, Class<T> cls) throws IOException {
    return getRecord(ipAddress, cls).getData();
  }
  
  public <T> DatabaseRecord<T> getRecord(InetAddress ipAddress, Class<T> cls) throws IOException {
    ByteBuffer buffer = getBufferHolder().get();
    byte[] rawAddress = ipAddress.getAddress();
    int bitLength = rawAddress.length * 8;
    int record = startNode(bitLength);
    int nodeCount = this.metadata.getNodeCount();
    int pl = 0;
    for (; pl < bitLength && record < nodeCount; pl++) {
      int b = 0xFF & rawAddress[pl / 8];
      int bit = 0x1 & b >> 7 - pl % 8;
      record = readNode(buffer, record, bit);
    } 
    T dataRecord = null;
    if (record > nodeCount)
      dataRecord = resolveDataPointer(buffer, record, cls); 
    return new DatabaseRecord<>(dataRecord, ipAddress, pl);
  }
  
  private BufferHolder getBufferHolder() throws ClosedDatabaseException {
    BufferHolder bufferHolder = this.bufferHolderReference.get();
    if (bufferHolder == null)
      throw new ClosedDatabaseException(); 
    return bufferHolder;
  }
  
  private int startNode(int bitLength) {
    if (this.metadata.getIpVersion() == 6 && bitLength == 32)
      return this.ipV4Start; 
    return 0;
  }
  
  private int findIpV4StartNode(ByteBuffer buffer) throws InvalidDatabaseException {
    if (this.metadata.getIpVersion() == 4)
      return 0; 
    int node = 0;
    for (int i = 0; i < 96 && node < this.metadata.getNodeCount(); i++)
      node = readNode(buffer, node, 0); 
    return node;
  }
  
  private int readNode(ByteBuffer buffer, int nodeNumber, int index) throws InvalidDatabaseException {
    int middle, baseOffset = nodeNumber * this.metadata.getNodeByteSize();
    switch (this.metadata.getRecordSize()) {
      case 24:
        buffer.position(baseOffset + index * 3);
        return Decoder.decodeInteger(buffer, 0, 3);
      case 28:
        middle = buffer.get(baseOffset + 3);
        if (index == 0) {
          middle = (0xF0 & middle) >>> 4;
        } else {
          middle = 0xF & middle;
        } 
        buffer.position(baseOffset + index * 4);
        return Decoder.decodeInteger(buffer, middle, 3);
      case 32:
        buffer.position(baseOffset + index * 4);
        return Decoder.decodeInteger(buffer, 0, 4);
    } 
    throw new InvalidDatabaseException("Unknown record size: " + this.metadata
        .getRecordSize());
  }
  
  private <T> T resolveDataPointer(ByteBuffer buffer, int pointer, Class<T> cls) throws IOException {
    int resolved = pointer - this.metadata.getNodeCount() + this.metadata.getSearchTreeSize();
    if (resolved >= buffer.capacity())
      throw new InvalidDatabaseException("The MaxMind DB file's search tree is corrupt: contains pointer larger than the database."); 
    Decoder decoder = new Decoder(this.cache, buffer, (this.metadata.getSearchTreeSize() + 16), this.constructors);
    return decoder.decode(resolved, cls);
  }
  
  private int findMetadataStart(ByteBuffer buffer, String databaseName) throws InvalidDatabaseException {
    int fileSize = buffer.capacity();
    for (int i = 0; i < fileSize - METADATA_START_MARKER.length + 1; i++) {
      int j = 0;
      while (true) {
        if (j < METADATA_START_MARKER.length) {
          byte b = buffer.get(fileSize - i - j - 1);
          if (b != METADATA_START_MARKER[METADATA_START_MARKER.length - j - 1])
            break; 
          j++;
          continue;
        } 
        return fileSize - i;
      } 
    } 
    throw new InvalidDatabaseException("Could not find a MaxMind DB metadata marker in this file (" + databaseName + "). Is this a valid MaxMind DB file?");
  }
  
  public Metadata getMetadata() {
    return this.metadata;
  }
  
  public void close() throws IOException {
    this.bufferHolderReference.set(null);
  }
}
