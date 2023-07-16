package com.mysql.cj.xdevapi;

import com.mysql.cj.exceptions.WrongArgumentException;
import com.mysql.cj.protocol.ResultStreamer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.function.Supplier;

public class SqlMultiResult implements SqlResult, ResultStreamer {
  private Supplier<SqlResult> resultStream;
  
  private List<SqlResult> pendingResults = new ArrayList<>();
  
  private SqlResult currentResult;
  
  public SqlMultiResult(Supplier<SqlResult> resultStream) {
    this.resultStream = resultStream;
    this.currentResult = resultStream.get();
  }
  
  private SqlResult getCurrentResult() {
    if (this.currentResult == null)
      throw new WrongArgumentException("No active result"); 
    return this.currentResult;
  }
  
  public boolean nextResult() {
    if (this.currentResult == null)
      return false; 
    try {
      if (ResultStreamer.class.isAssignableFrom(this.currentResult.getClass()))
        ((ResultStreamer)this.currentResult).finishStreaming(); 
    } finally {
      this.currentResult = null;
    } 
    this.currentResult = (this.pendingResults.size() > 0) ? this.pendingResults.remove(0) : this.resultStream.get();
    return (this.currentResult != null);
  }
  
  public void finishStreaming() {
    if (this.currentResult == null)
      return; 
    if (ResultStreamer.class.isAssignableFrom(this.currentResult.getClass()))
      ((ResultStreamer)this.currentResult).finishStreaming(); 
    SqlResult pendingRs = null;
    while ((pendingRs = this.resultStream.get()) != null) {
      if (ResultStreamer.class.isAssignableFrom(pendingRs.getClass()))
        ((ResultStreamer)pendingRs).finishStreaming(); 
      this.pendingResults.add(pendingRs);
    } 
  }
  
  public boolean hasData() {
    return getCurrentResult().hasData();
  }
  
  public long getAffectedItemsCount() {
    return getCurrentResult().getAffectedItemsCount();
  }
  
  public Long getAutoIncrementValue() {
    return getCurrentResult().getAutoIncrementValue();
  }
  
  public int getWarningsCount() {
    return getCurrentResult().getWarningsCount();
  }
  
  public Iterator<Warning> getWarnings() {
    return getCurrentResult().getWarnings();
  }
  
  public int getColumnCount() {
    return getCurrentResult().getColumnCount();
  }
  
  public List<Column> getColumns() {
    return getCurrentResult().getColumns();
  }
  
  public List<String> getColumnNames() {
    return getCurrentResult().getColumnNames();
  }
  
  public long count() {
    return getCurrentResult().count();
  }
  
  public List<Row> fetchAll() {
    return getCurrentResult().fetchAll();
  }
  
  public Row next() {
    return getCurrentResult().next();
  }
  
  public boolean hasNext() {
    return getCurrentResult().hasNext();
  }
}
