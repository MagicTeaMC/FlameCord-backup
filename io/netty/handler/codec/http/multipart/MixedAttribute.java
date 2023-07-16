package io.netty.handler.codec.http.multipart;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufHolder;
import io.netty.handler.codec.http.HttpConstants;
import io.netty.util.ReferenceCounted;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;

public class MixedAttribute extends AbstractMixedHttpData<Attribute> implements Attribute {
  public MixedAttribute(String name, long limitSize) {
    this(name, limitSize, HttpConstants.DEFAULT_CHARSET);
  }
  
  public MixedAttribute(String name, long definedSize, long limitSize) {
    this(name, definedSize, limitSize, HttpConstants.DEFAULT_CHARSET);
  }
  
  public MixedAttribute(String name, long limitSize, Charset charset) {
    this(name, limitSize, charset, DiskAttribute.baseDirectory, DiskAttribute.deleteOnExitTemporaryFile);
  }
  
  public MixedAttribute(String name, long limitSize, Charset charset, String baseDir, boolean deleteOnExit) {
    this(name, 0L, limitSize, charset, baseDir, deleteOnExit);
  }
  
  public MixedAttribute(String name, long definedSize, long limitSize, Charset charset) {
    this(name, definedSize, limitSize, charset, DiskAttribute.baseDirectory, DiskAttribute.deleteOnExitTemporaryFile);
  }
  
  public MixedAttribute(String name, long definedSize, long limitSize, Charset charset, String baseDir, boolean deleteOnExit) {
    super(limitSize, baseDir, deleteOnExit, new MemoryAttribute(name, definedSize, charset));
  }
  
  public MixedAttribute(String name, String value, long limitSize) {
    this(name, value, limitSize, HttpConstants.DEFAULT_CHARSET, DiskAttribute.baseDirectory, DiskFileUpload.deleteOnExitTemporaryFile);
  }
  
  public MixedAttribute(String name, String value, long limitSize, Charset charset) {
    this(name, value, limitSize, charset, DiskAttribute.baseDirectory, DiskFileUpload.deleteOnExitTemporaryFile);
  }
  
  private static Attribute makeInitialAttributeFromValue(String name, String value, long limitSize, Charset charset, String baseDir, boolean deleteOnExit) {
    if (value.length() > limitSize)
      try {
        return new DiskAttribute(name, value, charset, baseDir, deleteOnExit);
      } catch (IOException e) {
        try {
          return new MemoryAttribute(name, value, charset);
        } catch (IOException ignore) {
          throw new IllegalArgumentException(e);
        } 
      }  
    try {
      return new MemoryAttribute(name, value, charset);
    } catch (IOException e) {
      throw new IllegalArgumentException(e);
    } 
  }
  
  public MixedAttribute(String name, String value, long limitSize, Charset charset, String baseDir, boolean deleteOnExit) {
    super(limitSize, baseDir, deleteOnExit, 
        makeInitialAttributeFromValue(name, value, limitSize, charset, baseDir, deleteOnExit));
  }
  
  public String getValue() throws IOException {
    return this.wrapped.getValue();
  }
  
  public void setValue(String value) throws IOException {
    this.wrapped.setValue(value);
  }
  
  Attribute makeDiskData() {
    DiskAttribute diskAttribute = new DiskAttribute(getName(), definedLength(), this.baseDir, this.deleteOnExit);
    diskAttribute.setMaxSize(getMaxSize());
    return diskAttribute;
  }
  
  public Attribute copy() {
    return super.copy();
  }
  
  public Attribute duplicate() {
    return super.duplicate();
  }
  
  public Attribute replace(ByteBuf content) {
    return super.replace(content);
  }
  
  public Attribute retain() {
    return super.retain();
  }
  
  public Attribute retain(int increment) {
    return super.retain(increment);
  }
  
  public Attribute retainedDuplicate() {
    return super.retainedDuplicate();
  }
  
  public Attribute touch() {
    return super.touch();
  }
  
  public Attribute touch(Object hint) {
    return super.touch(hint);
  }
}
