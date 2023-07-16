package com.mysql.cj.protocol.a.result;

import com.mysql.cj.exceptions.ExceptionInterceptor;
import com.mysql.cj.protocol.ColumnDefinition;
import com.mysql.cj.protocol.ValueDecoder;
import com.mysql.cj.protocol.a.NativeConstants;
import com.mysql.cj.protocol.a.NativePacketPayload;
import com.mysql.cj.result.Row;
import com.mysql.cj.result.ValueFactory;

public class TextBufferRow extends AbstractBufferRow {
  public TextBufferRow(NativePacketPayload buf, ColumnDefinition cd, ExceptionInterceptor exceptionInterceptor, ValueDecoder valueDecoder) {
    super(exceptionInterceptor);
    this.rowFromServer = buf;
    this.homePosition = this.rowFromServer.getPosition();
    this.valueDecoder = valueDecoder;
    if (cd.getFields() != null)
      setMetadata(cd); 
  }
  
  protected int findAndSeekToOffset(int index) {
    if (index == 0) {
      this.lastRequestedIndex = 0;
      this.lastRequestedPos = this.homePosition;
      this.rowFromServer.setPosition(this.homePosition);
      return 0;
    } 
    if (index == this.lastRequestedIndex) {
      this.rowFromServer.setPosition(this.lastRequestedPos);
      return this.lastRequestedPos;
    } 
    int startingIndex = 0;
    if (index > this.lastRequestedIndex) {
      if (this.lastRequestedIndex >= 0) {
        startingIndex = this.lastRequestedIndex;
      } else {
        startingIndex = 0;
      } 
      this.rowFromServer.setPosition(this.lastRequestedPos);
    } else {
      this.rowFromServer.setPosition(this.homePosition);
    } 
    for (int i = startingIndex; i < index; i++)
      this.rowFromServer.skipBytes(NativeConstants.StringSelfDataType.STRING_LENENC); 
    this.lastRequestedIndex = index;
    this.lastRequestedPos = this.rowFromServer.getPosition();
    return this.lastRequestedPos;
  }
  
  public byte[] getBytes(int index) {
    if (getNull(index))
      return null; 
    findAndSeekToOffset(index);
    return this.rowFromServer.readBytes(NativeConstants.StringSelfDataType.STRING_LENENC);
  }
  
  public boolean getNull(int columnIndex) {
    findAndSeekToOffset(columnIndex);
    this.wasNull = (this.rowFromServer.readInteger(NativeConstants.IntegerDataType.INT_LENENC) == -1L);
    return this.wasNull;
  }
  
  public Row setMetadata(ColumnDefinition f) {
    super.setMetadata(f);
    return (Row)this;
  }
  
  public <T> T getValue(int columnIndex, ValueFactory<T> vf) {
    findAndSeekToOffset(columnIndex);
    int length = (int)this.rowFromServer.readInteger(NativeConstants.IntegerDataType.INT_LENENC);
    return (T)getValueFromBytes(columnIndex, this.rowFromServer.getByteBuffer(), this.rowFromServer.getPosition(), length, vf);
  }
}
