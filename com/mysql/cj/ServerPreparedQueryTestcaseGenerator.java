package com.mysql.cj;

import com.mysql.cj.protocol.ColumnDefinition;
import com.mysql.cj.protocol.ProtocolEntityFactory;
import com.mysql.cj.protocol.a.NativePacketPayload;
import com.mysql.cj.util.TestUtils;
import java.io.IOException;

public class ServerPreparedQueryTestcaseGenerator extends ServerPreparedQuery {
  public ServerPreparedQueryTestcaseGenerator(NativeSession sess) {
    super(sess);
  }
  
  public void closeQuery() {
    dumpCloseForTestcase();
    super.closeQuery();
  }
  
  private void dumpCloseForTestcase() {
    StringBuilder buf = new StringBuilder();
    this.session.getProtocol().generateQueryCommentBlock(buf);
    buf.append("DEALLOCATE PREPARE debug_stmt_");
    buf.append(this.statementId);
    buf.append(";\n");
    TestUtils.dumpTestcaseQuery(buf.toString());
  }
  
  public void serverPrepare(String sql) throws IOException {
    dumpPrepareForTestcase();
    super.serverPrepare(sql);
  }
  
  private void dumpPrepareForTestcase() {
    StringBuilder buf = new StringBuilder(getOriginalSql().length() + 64);
    this.session.getProtocol().generateQueryCommentBlock(buf);
    buf.append("PREPARE debug_stmt_");
    buf.append(this.statementId);
    buf.append(" FROM \"");
    buf.append(getOriginalSql());
    buf.append("\";\n");
    TestUtils.dumpTestcaseQuery(buf.toString());
  }
  
  public <T extends com.mysql.cj.protocol.Resultset> T serverExecute(int maxRowsToRetrieve, boolean createStreamingResultSet, ColumnDefinition metadata, ProtocolEntityFactory<T, NativePacketPayload> resultSetFactory) {
    dumpExecuteForTestcase();
    return super.serverExecute(maxRowsToRetrieve, createStreamingResultSet, metadata, resultSetFactory);
  }
  
  private void dumpExecuteForTestcase() {
    StringBuilder buf = new StringBuilder();
    int i;
    for (i = 0; i < getParameterCount(); i++) {
      this.session.getProtocol().generateQueryCommentBlock(buf);
      buf.append("SET @debug_stmt_param");
      buf.append(this.statementId);
      buf.append("_");
      buf.append(i);
      buf.append("=");
      BindValue bv = this.queryBindings.getBindValues()[i];
      buf.append(bv.isNull() ? "NULL" : bv.getString());
      buf.append(";\n");
    } 
    this.session.getProtocol().generateQueryCommentBlock(buf);
    buf.append("EXECUTE debug_stmt_");
    buf.append(this.statementId);
    if (getParameterCount() > 0) {
      buf.append(" USING ");
      for (i = 0; i < getParameterCount(); i++) {
        if (i > 0)
          buf.append(", "); 
        buf.append("@debug_stmt_param");
        buf.append(this.statementId);
        buf.append("_");
        buf.append(i);
      } 
    } 
    buf.append(";\n");
    TestUtils.dumpTestcaseQuery(buf.toString());
  }
}
