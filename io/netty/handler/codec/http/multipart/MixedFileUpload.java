package io.netty.handler.codec.http.multipart;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufHolder;
import io.netty.util.ReferenceCounted;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;

public class MixedFileUpload extends AbstractMixedHttpData<FileUpload> implements FileUpload {
  public MixedFileUpload(String name, String filename, String contentType, String contentTransferEncoding, Charset charset, long size, long limitSize) {
    this(name, filename, contentType, contentTransferEncoding, charset, size, limitSize, DiskFileUpload.baseDirectory, DiskFileUpload.deleteOnExitTemporaryFile);
  }
  
  public MixedFileUpload(String name, String filename, String contentType, String contentTransferEncoding, Charset charset, long size, long limitSize, String baseDir, boolean deleteOnExit) {
    super(limitSize, baseDir, deleteOnExit, (size > limitSize) ? new DiskFileUpload(name, filename, contentType, contentTransferEncoding, charset, size, baseDir, deleteOnExit) : new MemoryFileUpload(name, filename, contentType, contentTransferEncoding, charset, size));
  }
  
  public String getContentTransferEncoding() {
    return this.wrapped.getContentTransferEncoding();
  }
  
  public String getFilename() {
    return this.wrapped.getFilename();
  }
  
  public void setContentTransferEncoding(String contentTransferEncoding) {
    this.wrapped.setContentTransferEncoding(contentTransferEncoding);
  }
  
  public void setFilename(String filename) {
    this.wrapped.setFilename(filename);
  }
  
  public void setContentType(String contentType) {
    this.wrapped.setContentType(contentType);
  }
  
  public String getContentType() {
    return this.wrapped.getContentType();
  }
  
  FileUpload makeDiskData() {
    DiskFileUpload diskFileUpload = new DiskFileUpload(getName(), getFilename(), getContentType(), getContentTransferEncoding(), getCharset(), definedLength(), this.baseDir, this.deleteOnExit);
    diskFileUpload.setMaxSize(getMaxSize());
    return diskFileUpload;
  }
  
  public FileUpload copy() {
    return super.copy();
  }
  
  public FileUpload duplicate() {
    return super.duplicate();
  }
  
  public FileUpload retainedDuplicate() {
    return super.retainedDuplicate();
  }
  
  public FileUpload replace(ByteBuf content) {
    return super.replace(content);
  }
  
  public FileUpload touch() {
    return super.touch();
  }
  
  public FileUpload touch(Object hint) {
    return super.touch(hint);
  }
  
  public FileUpload retain() {
    return super.retain();
  }
  
  public FileUpload retain(int increment) {
    return super.retain(increment);
  }
}
