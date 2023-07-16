package com.mysql.cj.protocol.a;

import com.mysql.cj.Messages;
import com.mysql.cj.conf.PropertyKey;
import com.mysql.cj.exceptions.ExceptionFactory;
import com.mysql.cj.protocol.ColumnDefinition;
import com.mysql.cj.protocol.Message;
import com.mysql.cj.protocol.ProtocolEntityFactory;
import com.mysql.cj.protocol.Resultset;
import com.mysql.cj.protocol.ResultsetRow;
import com.mysql.cj.protocol.a.result.BinaryBufferRow;
import com.mysql.cj.protocol.a.result.ByteArrayRow;
import com.mysql.cj.result.Field;

public class BinaryRowFactory extends AbstractRowFactory implements ProtocolEntityFactory<ResultsetRow, NativePacketPayload> {
  public BinaryRowFactory(NativeProtocol protocol, ColumnDefinition columnDefinition, Resultset.Concurrency resultSetConcurrency, boolean canReuseRowPacketForBufferRow) {
    this.columnDefinition = columnDefinition;
    this.resultSetConcurrency = resultSetConcurrency;
    this.canReuseRowPacketForBufferRow = canReuseRowPacketForBufferRow;
    this.useBufferRowSizeThreshold = protocol.getPropertySet().getMemorySizeProperty(PropertyKey.largeRowSizeThreshold);
    this.exceptionInterceptor = protocol.getExceptionInterceptor();
    this.valueDecoder = new MysqlBinaryValueDecoder();
  }
  
  public ResultsetRow createFromMessage(NativePacketPayload rowPacket) {
    boolean useBufferRow = (this.canReuseRowPacketForBufferRow || this.columnDefinition.hasLargeFields() || rowPacket.getPayloadLength() >= ((Integer)this.useBufferRowSizeThreshold.getValue()).intValue());
    rowPacket.setPosition(rowPacket.getPosition() + 1);
    if (this.resultSetConcurrency == Resultset.Concurrency.UPDATABLE || !useBufferRow)
      return unpackBinaryResultSetRow(this.columnDefinition.getFields(), rowPacket); 
    return (ResultsetRow)new BinaryBufferRow(rowPacket, this.columnDefinition, this.exceptionInterceptor, this.valueDecoder);
  }
  
  public boolean canReuseRowPacketForBufferRow() {
    return this.canReuseRowPacketForBufferRow;
  }
  
  private final ResultsetRow unpackBinaryResultSetRow(Field[] fields, NativePacketPayload binaryData) {
    int numFields = fields.length;
    byte[][] unpackedRowBytes = new byte[numFields][];
    int nullCount = (numFields + 9) / 8;
    int nullMaskPos = binaryData.getPosition();
    binaryData.setPosition(nullMaskPos + nullCount);
    int bit = 4;
    byte[] buf = binaryData.getByteBuffer();
    for (int i = 0; i < numFields; i++) {
      if ((buf[nullMaskPos] & bit) != 0) {
        unpackedRowBytes[i] = null;
      } else {
        extractNativeEncodedColumn(binaryData, fields, i, unpackedRowBytes);
      } 
      if (((bit <<= 1) & 0xFF) == 0) {
        bit = 1;
        nullMaskPos++;
      } 
    } 
    return (ResultsetRow)new ByteArrayRow(unpackedRowBytes, this.exceptionInterceptor, new MysqlBinaryValueDecoder());
  }
  
  private final void extractNativeEncodedColumn(NativePacketPayload binaryData, Field[] fields, int columnIndex, byte[][] unpackedRowData) {
    int type = fields[columnIndex].getMysqlTypeId();
    int len = NativeUtils.getBinaryEncodedLength(type);
    if (type != 6)
      if (len == 0) {
        unpackedRowData[columnIndex] = binaryData.readBytes(NativeConstants.StringSelfDataType.STRING_LENENC);
      } else if (len > 0) {
        unpackedRowData[columnIndex] = binaryData.readBytes(NativeConstants.StringLengthDataType.STRING_FIXED, len);
      } else {
        throw ExceptionFactory.createException(Messages.getString("MysqlIO.97", new Object[] { Integer.valueOf(type), Integer.valueOf(columnIndex), Integer.valueOf(fields.length) }));
      }  
  }
}
