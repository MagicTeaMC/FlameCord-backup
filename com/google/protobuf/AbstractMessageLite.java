package com.google.protobuf;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public abstract class AbstractMessageLite<MessageType extends AbstractMessageLite<MessageType, BuilderType>, BuilderType extends AbstractMessageLite.Builder<MessageType, BuilderType>> implements MessageLite {
  protected int memoizedHashCode = 0;
  
  public ByteString toByteString() {
    try {
      ByteString.CodedBuilder out = ByteString.newCodedBuilder(getSerializedSize());
      writeTo(out.getCodedOutput());
      return out.build();
    } catch (IOException e) {
      throw new RuntimeException(getSerializingExceptionMessage("ByteString"), e);
    } 
  }
  
  public byte[] toByteArray() {
    try {
      byte[] result = new byte[getSerializedSize()];
      CodedOutputStream output = CodedOutputStream.newInstance(result);
      writeTo(output);
      output.checkNoSpaceLeft();
      return result;
    } catch (IOException e) {
      throw new RuntimeException(getSerializingExceptionMessage("byte array"), e);
    } 
  }
  
  public void writeTo(OutputStream output) throws IOException {
    int bufferSize = CodedOutputStream.computePreferredBufferSize(getSerializedSize());
    CodedOutputStream codedOutput = CodedOutputStream.newInstance(output, bufferSize);
    writeTo(codedOutput);
    codedOutput.flush();
  }
  
  public void writeDelimitedTo(OutputStream output) throws IOException {
    int serialized = getSerializedSize();
    int bufferSize = CodedOutputStream.computePreferredBufferSize(
        CodedOutputStream.computeUInt32SizeNoTag(serialized) + serialized);
    CodedOutputStream codedOutput = CodedOutputStream.newInstance(output, bufferSize);
    codedOutput.writeUInt32NoTag(serialized);
    writeTo(codedOutput);
    codedOutput.flush();
  }
  
  int getMemoizedSerializedSize() {
    throw new UnsupportedOperationException();
  }
  
  void setMemoizedSerializedSize(int size) {
    throw new UnsupportedOperationException();
  }
  
  int getSerializedSize(Schema<AbstractMessageLite<MessageType, BuilderType>> schema) {
    int memoizedSerializedSize = getMemoizedSerializedSize();
    if (memoizedSerializedSize == -1) {
      memoizedSerializedSize = schema.getSerializedSize(this);
      setMemoizedSerializedSize(memoizedSerializedSize);
    } 
    return memoizedSerializedSize;
  }
  
  UninitializedMessageException newUninitializedMessageException() {
    return new UninitializedMessageException(this);
  }
  
  private String getSerializingExceptionMessage(String target) {
    return "Serializing " + 
      getClass().getName() + " to a " + target + " threw an IOException (should never happen).";
  }
  
  protected static void checkByteStringIsUtf8(ByteString byteString) throws IllegalArgumentException {
    if (!byteString.isValidUtf8())
      throw new IllegalArgumentException("Byte string is not UTF-8."); 
  }
  
  @Deprecated
  protected static <T> void addAll(Iterable<T> values, Collection<? super T> list) {
    Builder.addAll(values, (List<? super T>)list);
  }
  
  protected static <T> void addAll(Iterable<T> values, List<? super T> list) {
    Builder.addAll(values, list);
  }
  
  public static abstract class Builder<MessageType extends AbstractMessageLite<MessageType, BuilderType>, BuilderType extends Builder<MessageType, BuilderType>> implements MessageLite.Builder {
    public BuilderType mergeFrom(CodedInputStream input) throws IOException {
      return mergeFrom(input, ExtensionRegistryLite.getEmptyRegistry());
    }
    
    public BuilderType mergeFrom(ByteString data) throws InvalidProtocolBufferException {
      try {
        CodedInputStream input = data.newCodedInput();
        mergeFrom(input);
        input.checkLastTagWas(0);
        return (BuilderType)this;
      } catch (InvalidProtocolBufferException e) {
        throw e;
      } catch (IOException e) {
        throw new RuntimeException(getReadingExceptionMessage("ByteString"), e);
      } 
    }
    
    public BuilderType mergeFrom(ByteString data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
      try {
        CodedInputStream input = data.newCodedInput();
        mergeFrom(input, extensionRegistry);
        input.checkLastTagWas(0);
        return (BuilderType)this;
      } catch (InvalidProtocolBufferException e) {
        throw e;
      } catch (IOException e) {
        throw new RuntimeException(getReadingExceptionMessage("ByteString"), e);
      } 
    }
    
    public BuilderType mergeFrom(byte[] data) throws InvalidProtocolBufferException {
      return mergeFrom(data, 0, data.length);
    }
    
    public BuilderType mergeFrom(byte[] data, int off, int len) throws InvalidProtocolBufferException {
      try {
        CodedInputStream input = CodedInputStream.newInstance(data, off, len);
        mergeFrom(input);
        input.checkLastTagWas(0);
        return (BuilderType)this;
      } catch (InvalidProtocolBufferException e) {
        throw e;
      } catch (IOException e) {
        throw new RuntimeException(getReadingExceptionMessage("byte array"), e);
      } 
    }
    
    public BuilderType mergeFrom(byte[] data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
      return mergeFrom(data, 0, data.length, extensionRegistry);
    }
    
    public BuilderType mergeFrom(byte[] data, int off, int len, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
      try {
        CodedInputStream input = CodedInputStream.newInstance(data, off, len);
        mergeFrom(input, extensionRegistry);
        input.checkLastTagWas(0);
        return (BuilderType)this;
      } catch (InvalidProtocolBufferException e) {
        throw e;
      } catch (IOException e) {
        throw new RuntimeException(getReadingExceptionMessage("byte array"), e);
      } 
    }
    
    public BuilderType mergeFrom(InputStream input) throws IOException {
      CodedInputStream codedInput = CodedInputStream.newInstance(input);
      mergeFrom(codedInput);
      codedInput.checkLastTagWas(0);
      return (BuilderType)this;
    }
    
    public BuilderType mergeFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
      CodedInputStream codedInput = CodedInputStream.newInstance(input);
      mergeFrom(codedInput, extensionRegistry);
      codedInput.checkLastTagWas(0);
      return (BuilderType)this;
    }
    
    static final class LimitedInputStream extends FilterInputStream {
      private int limit;
      
      LimitedInputStream(InputStream in, int limit) {
        super(in);
        this.limit = limit;
      }
      
      public int available() throws IOException {
        return Math.min(super.available(), this.limit);
      }
      
      public int read() throws IOException {
        if (this.limit <= 0)
          return -1; 
        int result = super.read();
        if (result >= 0)
          this.limit--; 
        return result;
      }
      
      public int read(byte[] b, int off, int len) throws IOException {
        if (this.limit <= 0)
          return -1; 
        len = Math.min(len, this.limit);
        int result = super.read(b, off, len);
        if (result >= 0)
          this.limit -= result; 
        return result;
      }
      
      public long skip(long n) throws IOException {
        int result = (int)super.skip(Math.min(n, this.limit));
        if (result >= 0)
          this.limit -= result; 
        return result;
      }
    }
    
    public boolean mergeDelimitedFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
      int firstByte = input.read();
      if (firstByte == -1)
        return false; 
      int size = CodedInputStream.readRawVarint32(firstByte, input);
      InputStream limitedInput = new LimitedInputStream(input, size);
      mergeFrom(limitedInput, extensionRegistry);
      return true;
    }
    
    public boolean mergeDelimitedFrom(InputStream input) throws IOException {
      return mergeDelimitedFrom(input, ExtensionRegistryLite.getEmptyRegistry());
    }
    
    public BuilderType mergeFrom(MessageLite other) {
      if (!getDefaultInstanceForType().getClass().isInstance(other))
        throw new IllegalArgumentException("mergeFrom(MessageLite) can only merge messages of the same type."); 
      return internalMergeFrom((MessageType)other);
    }
    
    private String getReadingExceptionMessage(String target) {
      return "Reading " + 
        getClass().getName() + " from a " + target + " threw an IOException (should never happen).";
    }
    
    private static <T> void addAllCheckingNulls(Iterable<T> values, List<? super T> list) {
      if (list instanceof ArrayList && values instanceof Collection)
        ((ArrayList)list).ensureCapacity(list.size() + ((Collection)values).size()); 
      int begin = list.size();
      for (T value : values) {
        if (value == null) {
          String message = "Element at index " + (list.size() - begin) + " is null.";
          for (int i = list.size() - 1; i >= begin; i--)
            list.remove(i); 
          throw new NullPointerException(message);
        } 
        list.add(value);
      } 
    }
    
    protected static UninitializedMessageException newUninitializedMessageException(MessageLite message) {
      return new UninitializedMessageException(message);
    }
    
    @Deprecated
    protected static <T> void addAll(Iterable<T> values, Collection<? super T> list) {
      addAll(values, (List<? super T>)list);
    }
    
    protected static <T> void addAll(Iterable<T> values, List<? super T> list) {
      Internal.checkNotNull(values);
      if (values instanceof LazyStringList) {
        List<?> lazyValues = ((LazyStringList)values).getUnderlyingElements();
        LazyStringList lazyList = (LazyStringList)list;
        int begin = list.size();
        for (Object value : lazyValues) {
          if (value == null) {
            String message = "Element at index " + (lazyList.size() - begin) + " is null.";
            for (int i = lazyList.size() - 1; i >= begin; i--)
              lazyList.remove(i); 
            throw new NullPointerException(message);
          } 
          if (value instanceof ByteString) {
            lazyList.add((ByteString)value);
            continue;
          } 
          lazyList.add((String)value);
        } 
      } else if (values instanceof PrimitiveNonBoxingCollection) {
        list.addAll((Collection<? extends T>)values);
      } else {
        addAllCheckingNulls(values, list);
      } 
    }
    
    public abstract BuilderType clone();
    
    public abstract BuilderType mergeFrom(CodedInputStream param1CodedInputStream, ExtensionRegistryLite param1ExtensionRegistryLite) throws IOException;
    
    protected abstract BuilderType internalMergeFrom(MessageType param1MessageType);
  }
  
  protected static interface InternalOneOfEnum {
    int getNumber();
  }
}
