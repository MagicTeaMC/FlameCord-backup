package com.mysql.cj.protocol.x;

import com.mysql.cj.Messages;
import com.mysql.cj.exceptions.CJException;
import com.mysql.cj.exceptions.ExceptionFactory;
import com.mysql.cj.exceptions.WrongArgumentException;
import com.mysql.cj.util.Util;
import java.io.InputStream;
import java.io.OutputStream;

public class CompressorStreamsFactory {
  private CompressionAlgorithm compressionAlgorithm;
  
  private InputStream compressorInputStreamInstance = null;
  
  private ContinuousInputStream underlyingInputStream = null;
  
  private OutputStream compressorOutputStreamInstance = null;
  
  private ReusableOutputStream underlyingOutputStream = null;
  
  public CompressorStreamsFactory(CompressionAlgorithm algorithm) {
    this.compressionAlgorithm = algorithm;
  }
  
  public CompressionMode getCompressionMode() {
    return this.compressionAlgorithm.getCompressionMode();
  }
  
  public boolean areCompressedStreamsContinuous() {
    return (getCompressionMode() == CompressionMode.STREAM);
  }
  
  public InputStream getInputStreamInstance(InputStream in) {
    InputStream compressionIn, underlyingIn = in;
    if (areCompressedStreamsContinuous()) {
      if (this.compressorInputStreamInstance != null) {
        this.underlyingInputStream.addInputStream(underlyingIn);
        return this.compressorInputStreamInstance;
      } 
      this.underlyingInputStream = new ContinuousInputStream(underlyingIn);
      underlyingIn = this.underlyingInputStream;
    } 
    try {
      compressionIn = (InputStream)Util.getInstance(InputStream.class, this.compressionAlgorithm.getInputStreamClassName(), new Class[] { InputStream.class }, new Object[] { underlyingIn }, null);
    } catch (CJException e) {
      throw (WrongArgumentException)ExceptionFactory.createException(WrongArgumentException.class, Messages.getString("Protocol.Compression.IoFactory.0", new Object[] { this.compressionAlgorithm
              .getInputStreamClassName(), this.compressionAlgorithm.getAlgorithmIdentifier() }), e);
    } 
    if (areCompressedStreamsContinuous())
      this.compressorInputStreamInstance = compressionIn; 
    return compressionIn;
  }
  
  public OutputStream getOutputStreamInstance(OutputStream out) {
    OutputStream compressionOut, underlyingOut = out;
    if (areCompressedStreamsContinuous()) {
      if (this.compressorOutputStreamInstance != null) {
        this.underlyingOutputStream.setOutputStream(underlyingOut);
        return this.compressorOutputStreamInstance;
      } 
      this.underlyingOutputStream = new ReusableOutputStream(underlyingOut);
      underlyingOut = this.underlyingOutputStream;
    } 
    try {
      compressionOut = (OutputStream)Util.getInstance(OutputStream.class, this.compressionAlgorithm.getOutputStreamClassName(), new Class[] { OutputStream.class }, new Object[] { underlyingOut }, null);
    } catch (CJException e) {
      throw (WrongArgumentException)ExceptionFactory.createException(WrongArgumentException.class, Messages.getString("Protocol.Compression.IoFactory.1", new Object[] { this.compressionAlgorithm
              .getOutputStreamClassName(), this.compressionAlgorithm.getAlgorithmIdentifier() }), e);
    } 
    if (areCompressedStreamsContinuous()) {
      compressionOut = new ContinuousOutputStream(compressionOut);
      this.compressorOutputStreamInstance = compressionOut;
    } 
    return compressionOut;
  }
}
