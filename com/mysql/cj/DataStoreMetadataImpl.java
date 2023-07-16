package com.mysql.cj;

import com.mysql.cj.protocol.Message;
import com.mysql.cj.result.LongValueFactory;
import com.mysql.cj.result.Row;
import com.mysql.cj.result.ValueFactory;
import com.mysql.cj.xdevapi.ExprUnparser;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

public class DataStoreMetadataImpl implements DataStoreMetadata {
  private Session session;
  
  public DataStoreMetadataImpl(Session sess) {
    this.session = sess;
  }
  
  public boolean schemaExists(String schemaName) {
    StringBuilder stmt = new StringBuilder("select count(*) from information_schema.schemata where schema_name = '");
    stmt.append(schemaName.replaceAll("'", "\\'"));
    stmt.append("'");
    Function<Row, Long> rowToLong = r -> (Long)r.getValue(0, (ValueFactory)new LongValueFactory(this.session.getPropertySet()));
    List<Long> counters = this.session.<Message, Long, List<Long>>query(this.session.<Message>getMessageBuilder().buildSqlStatement(stmt.toString()), null, rowToLong, Collectors.toList());
    return (1L == ((Long)counters.get(0)).longValue());
  }
  
  public boolean tableExists(String schemaName, String tableName) {
    StringBuilder stmt = new StringBuilder("select count(*) from information_schema.tables where table_schema = '");
    stmt.append(schemaName.replaceAll("'", "\\'"));
    stmt.append("' and table_name = '");
    stmt.append(tableName.replaceAll("'", "\\'"));
    stmt.append("'");
    Function<Row, Long> rowToLong = r -> (Long)r.getValue(0, (ValueFactory)new LongValueFactory(this.session.getPropertySet()));
    List<Long> counters = this.session.<Message, Long, List<Long>>query(this.session.<Message>getMessageBuilder().buildSqlStatement(stmt.toString()), null, rowToLong, Collectors.toList());
    return (1L == ((Long)counters.get(0)).longValue());
  }
  
  public long getTableRowCount(String schemaName, String tableName) {
    StringBuilder stmt = new StringBuilder("select count(*) from ");
    stmt.append(ExprUnparser.quoteIdentifier(schemaName));
    stmt.append(".");
    stmt.append(ExprUnparser.quoteIdentifier(tableName));
    Function<Row, Long> rowToLong = r -> (Long)r.getValue(0, (ValueFactory)new LongValueFactory(this.session.getPropertySet()));
    List<Long> counters = this.session.<Message, Long, List<Long>>query(this.session.<Message>getMessageBuilder().buildSqlStatement(stmt.toString()), null, rowToLong, Collectors.toList());
    return ((Long)counters.get(0)).longValue();
  }
}
