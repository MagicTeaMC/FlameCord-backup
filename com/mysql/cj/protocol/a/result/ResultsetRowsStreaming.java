package com.mysql.cj.protocol.a.result;

import com.mysql.cj.Messages;
import com.mysql.cj.conf.PropertyKey;
import com.mysql.cj.exceptions.CJException;
import com.mysql.cj.exceptions.ExceptionFactory;
import com.mysql.cj.exceptions.ExceptionInterceptor;
import com.mysql.cj.exceptions.StreamingNotifiable;
import com.mysql.cj.protocol.ColumnDefinition;
import com.mysql.cj.protocol.Message;
import com.mysql.cj.protocol.ProtocolEntity;
import com.mysql.cj.protocol.ProtocolEntityFactory;
import com.mysql.cj.protocol.Resultset;
import com.mysql.cj.protocol.ResultsetRow;
import com.mysql.cj.protocol.ResultsetRows;
import com.mysql.cj.protocol.a.BinaryRowFactory;
import com.mysql.cj.protocol.a.NativeMessageBuilder;
import com.mysql.cj.protocol.a.NativePacketPayload;
import com.mysql.cj.protocol.a.NativeProtocol;
import com.mysql.cj.protocol.a.TextRowFactory;
import com.mysql.cj.result.Row;
import com.mysql.cj.util.Util;

public class ResultsetRowsStreaming<T extends ProtocolEntity> extends AbstractResultsetRows implements ResultsetRows {
  private NativeProtocol protocol;
  
  private boolean isAfterEnd = false;
  
  private boolean noMoreRows = false;
  
  private boolean isBinaryEncoded = false;
  
  private Row nextRow;
  
  private boolean streamerClosed = false;
  
  private ExceptionInterceptor exceptionInterceptor;
  
  private ProtocolEntityFactory<T, NativePacketPayload> resultSetFactory;
  
  private NativeMessageBuilder commandBuilder = null;
  
  public ResultsetRowsStreaming(NativeProtocol io, ColumnDefinition columnDefinition, boolean isBinaryEncoded, ProtocolEntityFactory<T, NativePacketPayload> resultSetFactory) {
    this.protocol = io;
    this.isBinaryEncoded = isBinaryEncoded;
    this.metadata = columnDefinition;
    this.exceptionInterceptor = this.protocol.getExceptionInterceptor();
    this.resultSetFactory = resultSetFactory;
    this.rowFactory = this.isBinaryEncoded ? (ProtocolEntityFactory<ResultsetRow, NativePacketPayload>)new BinaryRowFactory(this.protocol, this.metadata, Resultset.Concurrency.READ_ONLY, true) : (ProtocolEntityFactory<ResultsetRow, NativePacketPayload>)new TextRowFactory(this.protocol, this.metadata, Resultset.Concurrency.READ_ONLY, true);
    this.commandBuilder = new NativeMessageBuilder(this.protocol.getServerSession().supportsQueryAttributes());
  }
  
  public void close() {
    Object mutex = (this.owner != null && this.owner.getSyncMutex() != null) ? this.owner.getSyncMutex() : this;
    boolean hadMore = false;
    int howMuchMore = 0;
    synchronized (mutex) {
      while (next() != null) {
        hadMore = true;
        howMuchMore++;
        if (howMuchMore % 100 == 0)
          Thread.yield(); 
      } 
      if (!((Boolean)this.protocol.getPropertySet().getBooleanProperty(PropertyKey.clobberStreamingResults).getValue()).booleanValue() && ((Integer)this.protocol
        .getPropertySet().getIntegerProperty(PropertyKey.netTimeoutForStreamingResults).getValue()).intValue() > 0) {
        int oldValue = this.protocol.getServerSession().getServerVariable("net_write_timeout", 60);
        this.protocol.clearInputStream();
        try {
          this.protocol.sendCommand((Message)this.commandBuilder.buildComQuery(this.protocol.getSharedSendPacket(), "SET net_write_timeout=" + oldValue, (String)this.protocol
                .getPropertySet().getStringProperty(PropertyKey.characterEncoding).getValue()), false, 0);
        } catch (Exception ex) {
          throw ExceptionFactory.createException(ex.getMessage(), ex, this.exceptionInterceptor);
        } 
      } 
      if (((Boolean)this.protocol.getPropertySet().getBooleanProperty(PropertyKey.useUsageAdvisor).getValue()).booleanValue() && 
        hadMore)
        this.owner.getSession().getProfilerEventHandler().processEvent((byte)0, this.owner.getSession(), this.owner
            .getOwningQuery(), null, 0L, new Throwable(), 
            Messages.getString("RowDataDynamic.1", (Object[])new String[] { String.valueOf(howMuchMore), this.owner.getPointOfOrigin() })); 
    } 
    this.metadata = null;
    this.owner = null;
  }
  
  public boolean hasNext() {
    boolean hasNext = (this.nextRow != null);
    if (!hasNext && !this.streamerClosed) {
      this.protocol.unsetStreamingData(this);
      this.streamerClosed = true;
    } 
    return hasNext;
  }
  
  public boolean isAfterLast() {
    return this.isAfterEnd;
  }
  
  public boolean isBeforeFirst() {
    return (this.currentPositionInFetchedRows < 0);
  }
  
  public boolean isEmpty() {
    return this.wasEmpty;
  }
  
  public boolean isFirst() {
    return (this.currentPositionInFetchedRows == 0);
  }
  
  public boolean isLast() {
    return (!isBeforeFirst() && !isAfterLast() && this.noMoreRows);
  }
  
  public Row next() {
    try {
      if (!this.noMoreRows) {
        this.nextRow = (Row)this.protocol.read(ResultsetRow.class, this.rowFactory);
        if (this.nextRow == null) {
          this.noMoreRows = true;
          this.isAfterEnd = true;
          if (this.currentPositionInFetchedRows == -1)
            this.wasEmpty = true; 
        } 
      } else {
        this.nextRow = null;
        this.isAfterEnd = true;
      } 
      if (this.nextRow == null && !this.streamerClosed)
        if (this.protocol.getServerSession().hasMoreResults()) {
          this.protocol.readNextResultset((ProtocolEntity)this.owner, this.owner.getOwningStatementMaxRows(), true, this.isBinaryEncoded, this.resultSetFactory);
        } else {
          this.protocol.unsetStreamingData(this);
          this.streamerClosed = true;
        }  
      if (this.nextRow != null && 
        this.currentPositionInFetchedRows != Integer.MAX_VALUE)
        this.currentPositionInFetchedRows++; 
      return this.nextRow;
    } catch (CJException sqlEx) {
      if (sqlEx instanceof StreamingNotifiable)
        ((StreamingNotifiable)sqlEx).setWasStreamingResults(); 
      this.noMoreRows = true;
      throw sqlEx;
    } catch (Exception ex) {
      CJException cjEx = ExceptionFactory.createException(
          Messages.getString("RowDataDynamic.2", (Object[])new String[] { ex.getClass().getName(), ex.getMessage(), Util.stackTraceToString(ex) }), ex, this.exceptionInterceptor);
      throw cjEx;
    } 
  }
  
  public int getPosition() {
    throw ExceptionFactory.createException(Messages.getString("ResultSet.ForwardOnly"));
  }
  
  public void afterLast() {
    throw ExceptionFactory.createException(Messages.getString("ResultSet.ForwardOnly"));
  }
  
  public void beforeFirst() {
    throw ExceptionFactory.createException(Messages.getString("ResultSet.ForwardOnly"));
  }
  
  public void beforeLast() {
    throw ExceptionFactory.createException(Messages.getString("ResultSet.ForwardOnly"));
  }
  
  public void moveRowRelative(int rows) {
    throw ExceptionFactory.createException(Messages.getString("ResultSet.ForwardOnly"));
  }
  
  public void setCurrentRow(int rowNumber) {
    throw ExceptionFactory.createException(Messages.getString("ResultSet.ForwardOnly"));
  }
}
