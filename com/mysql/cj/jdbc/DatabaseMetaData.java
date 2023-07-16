package com.mysql.cj.jdbc;

import com.mysql.cj.Messages;
import com.mysql.cj.MysqlType;
import com.mysql.cj.NativeSession;
import com.mysql.cj.conf.PropertyDefinitions;
import com.mysql.cj.conf.PropertyKey;
import com.mysql.cj.conf.RuntimeProperty;
import com.mysql.cj.exceptions.AssertionFailedException;
import com.mysql.cj.exceptions.CJException;
import com.mysql.cj.exceptions.ExceptionInterceptor;
import com.mysql.cj.jdbc.exceptions.SQLError;
import com.mysql.cj.jdbc.exceptions.SQLExceptionsMapping;
import com.mysql.cj.jdbc.result.ResultSetFactory;
import com.mysql.cj.jdbc.result.ResultSetImpl;
import com.mysql.cj.protocol.ColumnDefinition;
import com.mysql.cj.protocol.ResultsetRows;
import com.mysql.cj.protocol.a.result.ByteArrayRow;
import com.mysql.cj.protocol.a.result.ResultsetRowsStatic;
import com.mysql.cj.result.DefaultColumnDefinition;
import com.mysql.cj.result.Field;
import com.mysql.cj.result.Row;
import com.mysql.cj.util.SearchMode;
import com.mysql.cj.util.StringUtils;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.RowIdLifetime;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.StringTokenizer;
import java.util.TreeMap;
import java.util.TreeSet;

public class DatabaseMetaData implements DatabaseMetaData {
  protected static int maxBufferSize = 65535;
  
  protected static final int MAX_IDENTIFIER_LENGTH = 64;
  
  private static final String SUPPORTS_FK = "SUPPORTS_FK";
  
  protected abstract class IteratorWithCleanup<T> {
    abstract void close() throws SQLException;
    
    abstract boolean hasNext() throws SQLException;
    
    abstract T next() throws SQLException;
  }
  
  class LocalAndReferencedColumns {
    String constraintName;
    
    List<String> localColumnsList;
    
    String referencedDatabase;
    
    List<String> referencedColumnsList;
    
    String referencedTable;
    
    LocalAndReferencedColumns(List<String> localColumns, List<String> refColumns, String constName, String refDatabase, String refTable) {
      this.localColumnsList = localColumns;
      this.referencedColumnsList = refColumns;
      this.constraintName = constName;
      this.referencedTable = refTable;
      this.referencedDatabase = refDatabase;
    }
  }
  
  protected class StringListIterator extends IteratorWithCleanup<String> {
    int idx = -1;
    
    List<String> list;
    
    StringListIterator(List<String> list) {
      this.list = list;
    }
    
    void close() throws SQLException {
      this.list = null;
    }
    
    boolean hasNext() throws SQLException {
      return (this.idx < this.list.size() - 1);
    }
    
    String next() throws SQLException {
      this.idx++;
      return this.list.get(this.idx);
    }
  }
  
  protected class SingleStringIterator extends IteratorWithCleanup<String> {
    boolean onFirst = true;
    
    String value;
    
    SingleStringIterator(String s) {
      this.value = s;
    }
    
    void close() throws SQLException {}
    
    boolean hasNext() throws SQLException {
      return this.onFirst;
    }
    
    String next() throws SQLException {
      this.onFirst = false;
      return this.value;
    }
  }
  
  class TypeDescriptor {
    int bufferLength;
    
    Integer datetimePrecision;
    
    Integer columnSize;
    
    Integer charOctetLength;
    
    Integer decimalDigits;
    
    String isNullable;
    
    int nullability;
    
    int numPrecRadix;
    
    String mysqlTypeName;
    
    MysqlType mysqlType;
    
    TypeDescriptor(String typeInfo, String nullabilityInfo) throws SQLException {
      String temp;
      StringTokenizer tokenizer;
      int fract, numElements;
      this.datetimePrecision = null;
      this.columnSize = null;
      this.charOctetLength = null;
      this.decimalDigits = null;
      this.numPrecRadix = 10;
      if (typeInfo == null)
        throw SQLError.createSQLException(Messages.getString("DatabaseMetaData.0"), "S1009", DatabaseMetaData.this
            .getExceptionInterceptor()); 
      this.mysqlType = MysqlType.getByName(typeInfo);
      int maxLength = 0;
      switch (this.mysqlType) {
        case LOCAL_TEMPORARY:
          temp = typeInfo.substring(typeInfo.indexOf("(") + 1, typeInfo.lastIndexOf(")"));
          tokenizer = new StringTokenizer(temp, ",");
          while (tokenizer.hasMoreTokens()) {
            String nextToken = tokenizer.nextToken();
            maxLength = Math.max(maxLength, nextToken.length() - 2);
          } 
          this.columnSize = Integer.valueOf(maxLength);
          break;
        case null:
          temp = typeInfo.substring(typeInfo.indexOf("(") + 1, typeInfo.lastIndexOf(")"));
          tokenizer = new StringTokenizer(temp, ",");
          numElements = tokenizer.countTokens();
          if (numElements > 0)
            maxLength += numElements - 1; 
          while (tokenizer.hasMoreTokens()) {
            String setMember = tokenizer.nextToken().trim();
            if (setMember.startsWith("'") && setMember.endsWith("'")) {
              maxLength += setMember.length() - 2;
              continue;
            } 
            maxLength += setMember.length();
          } 
          this.columnSize = Integer.valueOf(maxLength);
          break;
        case null:
        case null:
          if (typeInfo.indexOf(",") != -1) {
            this.columnSize = Integer.valueOf(typeInfo.substring(typeInfo.indexOf("(") + 1, typeInfo.indexOf(",")).trim());
            this.decimalDigits = Integer.valueOf(typeInfo.substring(typeInfo.indexOf(",") + 1, typeInfo.indexOf(")")).trim());
            break;
          } 
          if (typeInfo.indexOf("(") != -1) {
            int size = Integer.parseInt(typeInfo.substring(typeInfo.indexOf("(") + 1, typeInfo.indexOf(")")).trim());
            if (size > 23) {
              this.mysqlType = (this.mysqlType == MysqlType.FLOAT) ? MysqlType.DOUBLE : MysqlType.DOUBLE_UNSIGNED;
              this.columnSize = Integer.valueOf(22);
              this.decimalDigits = Integer.valueOf(0);
            } 
            break;
          } 
          this.columnSize = Integer.valueOf(12);
          this.decimalDigits = Integer.valueOf(0);
          break;
        case TABLE:
        case VIEW:
        case SYSTEM_TABLE:
        case SYSTEM_VIEW:
          if (typeInfo.indexOf(",") != -1) {
            this.columnSize = Integer.valueOf(typeInfo.substring(typeInfo.indexOf("(") + 1, typeInfo.indexOf(",")).trim());
            this.decimalDigits = Integer.valueOf(typeInfo.substring(typeInfo.indexOf(",") + 1, typeInfo.indexOf(")")).trim());
            break;
          } 
          switch (this.mysqlType) {
            case TABLE:
            case VIEW:
              this.columnSize = Integer.valueOf(65);
              break;
            case SYSTEM_TABLE:
            case SYSTEM_VIEW:
              this.columnSize = Integer.valueOf(22);
              break;
          } 
          this.decimalDigits = Integer.valueOf(0);
          break;
        case null:
        case null:
        case null:
        case null:
        case null:
        case null:
        case null:
        case null:
        case null:
        case null:
        case null:
        case null:
        case null:
        case null:
          if (this.mysqlType == MysqlType.CHAR)
            this.columnSize = Integer.valueOf(1); 
          if (typeInfo.indexOf("(") != -1) {
            int endParenIndex = typeInfo.indexOf(")");
            if (endParenIndex == -1)
              endParenIndex = typeInfo.length(); 
            this.columnSize = Integer.valueOf(typeInfo.substring(typeInfo.indexOf("(") + 1, endParenIndex).trim());
            if (DatabaseMetaData.this.tinyInt1isBit && this.columnSize.intValue() == 1 && StringUtils.startsWithIgnoreCase(typeInfo, "tinyint")) {
              if (DatabaseMetaData.this.transformedBitIsBoolean) {
                this.mysqlType = MysqlType.BOOLEAN;
                break;
              } 
              this.mysqlType = MysqlType.BIT;
            } 
          } 
          break;
        case null:
          if (DatabaseMetaData.this.tinyInt1isBit && typeInfo.indexOf("(1)") != -1) {
            if (DatabaseMetaData.this.transformedBitIsBoolean) {
              this.mysqlType = MysqlType.BOOLEAN;
              break;
            } 
            this.mysqlType = MysqlType.BIT;
            break;
          } 
          this.columnSize = Integer.valueOf(3);
          break;
        case null:
          this.columnSize = Integer.valueOf(3);
          break;
        case null:
          this.datetimePrecision = Integer.valueOf(0);
          this.columnSize = Integer.valueOf(10);
          break;
        case null:
          this.datetimePrecision = Integer.valueOf(0);
          this.columnSize = Integer.valueOf(8);
          if (typeInfo.indexOf("(") != -1 && (
            fract = Integer.parseInt(typeInfo.substring(typeInfo.indexOf("(") + 1, typeInfo.indexOf(")")).trim())) > 0) {
            this.datetimePrecision = Integer.valueOf(fract);
            TypeDescriptor typeDescriptor = this;
            typeDescriptor.columnSize = Integer.valueOf(typeDescriptor.columnSize.intValue() + fract + 1);
          } 
          break;
        case null:
        case null:
          this.datetimePrecision = Integer.valueOf(0);
          this.columnSize = Integer.valueOf(19);
          if (typeInfo.indexOf("(") != -1 && (
            fract = Integer.parseInt(typeInfo.substring(typeInfo.indexOf("(") + 1, typeInfo.indexOf(")")).trim())) > 0) {
            this.datetimePrecision = Integer.valueOf(fract);
            TypeDescriptor typeDescriptor = this;
            typeDescriptor.columnSize = Integer.valueOf(typeDescriptor.columnSize.intValue() + fract + 1);
          } 
          break;
      } 
      if (this.columnSize == null)
        this.columnSize = Integer.valueOf((this.mysqlType.getPrecision().longValue() > 2147483647L) ? Integer.MAX_VALUE : this.mysqlType.getPrecision().intValue()); 
      switch (this.mysqlType) {
        case null:
        case null:
        case null:
        case null:
        case null:
        case null:
        case null:
        case null:
        case null:
        case null:
        case null:
        case null:
        case null:
        case null:
          this.charOctetLength = this.columnSize;
          break;
      } 
      this.bufferLength = DatabaseMetaData.maxBufferSize;
      this.numPrecRadix = 10;
      if (nullabilityInfo != null) {
        if (nullabilityInfo.equals("YES")) {
          this.nullability = 1;
          this.isNullable = "YES";
        } else if (nullabilityInfo.equals("UNKNOWN")) {
          this.nullability = 2;
          this.isNullable = "";
        } else {
          this.nullability = 0;
          this.isNullable = "NO";
        } 
      } else {
        this.nullability = 0;
        this.isNullable = "NO";
      } 
    }
  }
  
  protected class IndexMetaDataKey implements Comparable<IndexMetaDataKey> {
    Boolean columnNonUnique;
    
    Short columnType;
    
    String columnIndexName;
    
    Short columnOrdinalPosition;
    
    IndexMetaDataKey(boolean columnNonUnique, short columnType, String columnIndexName, short columnOrdinalPosition) {
      this.columnNonUnique = Boolean.valueOf(columnNonUnique);
      this.columnType = Short.valueOf(columnType);
      this.columnIndexName = columnIndexName;
      this.columnOrdinalPosition = Short.valueOf(columnOrdinalPosition);
    }
    
    public int compareTo(IndexMetaDataKey indexInfoKey) {
      int compareResult;
      if ((compareResult = this.columnNonUnique.compareTo(indexInfoKey.columnNonUnique)) != 0)
        return compareResult; 
      if ((compareResult = this.columnType.compareTo(indexInfoKey.columnType)) != 0)
        return compareResult; 
      if ((compareResult = this.columnIndexName.compareTo(indexInfoKey.columnIndexName)) != 0)
        return compareResult; 
      return this.columnOrdinalPosition.compareTo(indexInfoKey.columnOrdinalPosition);
    }
    
    public boolean equals(Object obj) {
      if (obj == null)
        return false; 
      if (obj == this)
        return true; 
      if (!(obj instanceof IndexMetaDataKey))
        return false; 
      return (compareTo((IndexMetaDataKey)obj) == 0);
    }
    
    public int hashCode() {
      assert false : "hashCode not designed";
      return 0;
    }
  }
  
  protected class TableMetaDataKey implements Comparable<TableMetaDataKey> {
    String tableType;
    
    String tableCat;
    
    String tableSchem;
    
    String tableName;
    
    TableMetaDataKey(String tableType, String tableCat, String tableSchem, String tableName) {
      this.tableType = (tableType == null) ? "" : tableType;
      this.tableCat = (tableCat == null) ? "" : tableCat;
      this.tableSchem = (tableSchem == null) ? "" : tableSchem;
      this.tableName = (tableName == null) ? "" : tableName;
    }
    
    public int compareTo(TableMetaDataKey tablesKey) {
      int compareResult;
      if ((compareResult = this.tableType.compareTo(tablesKey.tableType)) != 0)
        return compareResult; 
      if ((compareResult = this.tableCat.compareTo(tablesKey.tableCat)) != 0)
        return compareResult; 
      if ((compareResult = this.tableSchem.compareTo(tablesKey.tableSchem)) != 0)
        return compareResult; 
      return this.tableName.compareTo(tablesKey.tableName);
    }
    
    public boolean equals(Object obj) {
      if (obj == null)
        return false; 
      if (obj == this)
        return true; 
      if (!(obj instanceof TableMetaDataKey))
        return false; 
      return (compareTo((TableMetaDataKey)obj) == 0);
    }
    
    public int hashCode() {
      assert false : "hashCode not designed";
      return 0;
    }
  }
  
  protected class ComparableWrapper<K extends Comparable<? super K>, V> implements Comparable<ComparableWrapper<K, V>> {
    K key;
    
    V value;
    
    public ComparableWrapper(K key, V value) {
      this.key = key;
      this.value = value;
    }
    
    public K getKey() {
      return this.key;
    }
    
    public V getValue() {
      return this.value;
    }
    
    public int compareTo(ComparableWrapper<K, V> other) {
      return ((Comparable)getKey()).compareTo(other.getKey());
    }
    
    public boolean equals(Object obj) {
      if (obj == null)
        return false; 
      if (obj == this)
        return true; 
      if (!(obj instanceof ComparableWrapper))
        return false; 
      Object otherKey = ((ComparableWrapper)obj).getKey();
      return this.key.equals(otherKey);
    }
    
    public int hashCode() {
      assert false : "hashCode not designed";
      return 0;
    }
    
    public String toString() {
      return "{KEY:" + this.key + "; VALUE:" + this.value + "}";
    }
  }
  
  protected enum TableType {
    LOCAL_TEMPORARY("LOCAL TEMPORARY"),
    SYSTEM_TABLE("SYSTEM TABLE"),
    SYSTEM_VIEW("SYSTEM VIEW"),
    TABLE("TABLE", new String[] { "BASE TABLE" }),
    VIEW("VIEW"),
    UNKNOWN("UNKNOWN");
    
    private String name;
    
    private byte[] nameAsBytes;
    
    private String[] synonyms;
    
    TableType(String tableTypeName, String[] tableTypeSynonyms) {
      this.name = tableTypeName;
      this.nameAsBytes = tableTypeName.getBytes();
      this.synonyms = tableTypeSynonyms;
    }
    
    String getName() {
      return this.name;
    }
    
    byte[] asBytes() {
      return this.nameAsBytes;
    }
    
    boolean equalsTo(String tableTypeName) {
      return this.name.equalsIgnoreCase(tableTypeName);
    }
    
    static TableType getTableTypeEqualTo(String tableTypeName) {
      for (TableType tableType : values()) {
        if (tableType.equalsTo(tableTypeName))
          return tableType; 
      } 
      return UNKNOWN;
    }
    
    boolean compliesWith(String tableTypeName) {
      if (equalsTo(tableTypeName))
        return true; 
      if (this.synonyms != null)
        for (String synonym : this.synonyms) {
          if (synonym.equalsIgnoreCase(tableTypeName))
            return true; 
        }  
      return false;
    }
    
    static TableType getTableTypeCompliantWith(String tableTypeName) {
      for (TableType tableType : values()) {
        if (tableType.compliesWith(tableTypeName))
          return tableType; 
      } 
      return UNKNOWN;
    }
  }
  
  protected enum ProcedureType {
    PROCEDURE, FUNCTION;
  }
  
  protected static final byte[] TABLE_AS_BYTES = "TABLE".getBytes();
  
  protected static final byte[] SYSTEM_TABLE_AS_BYTES = "SYSTEM TABLE".getBytes();
  
  protected static final byte[] VIEW_AS_BYTES = "VIEW".getBytes();
  
  private static final String[] MYSQL_KEYWORDS = new String[] { 
      "ACCESSIBLE", "ADD", "ALL", "ALTER", "ANALYZE", "AND", "AS", "ASC", "ASENSITIVE", "BEFORE", 
      "BETWEEN", "BIGINT", "BINARY", "BLOB", "BOTH", "BY", "CALL", "CASCADE", "CASE", "CHANGE", 
      "CHAR", "CHARACTER", "CHECK", "COLLATE", "COLUMN", "CONDITION", "CONSTRAINT", "CONTINUE", "CONVERT", "CREATE", 
      "CROSS", "CUBE", "CUME_DIST", "CURRENT_DATE", "CURRENT_TIME", "CURRENT_TIMESTAMP", "CURRENT_USER", "CURSOR", "DATABASE", "DATABASES", 
      "DAY_HOUR", "DAY_MICROSECOND", "DAY_MINUTE", "DAY_SECOND", "DEC", "DECIMAL", "DECLARE", "DEFAULT", "DELAYED", "DELETE", 
      "DENSE_RANK", "DESC", "DESCRIBE", "DETERMINISTIC", "DISTINCT", "DISTINCTROW", "DIV", "DOUBLE", "DROP", "DUAL", 
      "EACH", "ELSE", "ELSEIF", "EMPTY", "ENCLOSED", "ESCAPED", "EXCEPT", "EXISTS", "EXIT", "EXPLAIN", 
      "FALSE", "FETCH", "FIRST_VALUE", "FLOAT", "FLOAT4", "FLOAT8", "FOR", "FORCE", "FOREIGN", "FROM", 
      "FULLTEXT", "FUNCTION", "GENERATED", "GET", "GRANT", "GROUP", "GROUPING", "GROUPS", "HAVING", "HIGH_PRIORITY", 
      "HOUR_MICROSECOND", "HOUR_MINUTE", "HOUR_SECOND", "IF", "IGNORE", "IN", "INDEX", "INFILE", "INNER", "INOUT", 
      "INSENSITIVE", "INSERT", "INT", "INT1", "INT2", "INT3", "INT4", "INT8", "INTEGER", "INTERVAL", 
      "INTO", "IO_AFTER_GTIDS", "IO_BEFORE_GTIDS", "IS", "ITERATE", "JOIN", "JSON_TABLE", "KEY", "KEYS", "KILL", 
      "LAG", "LAST_VALUE", "LEAD", "LEADING", "LEAVE", "LEFT", "LIKE", "LIMIT", "LINEAR", "LINES", 
      "LOAD", "LOCALTIME", "LOCALTIMESTAMP", "LOCK", "LONG", "LONGBLOB", "LONGTEXT", "LOOP", "LOW_PRIORITY", "MASTER_BIND", 
      "MASTER_SSL_VERIFY_SERVER_CERT", "MATCH", "MAXVALUE", "MEDIUMBLOB", "MEDIUMINT", "MEDIUMTEXT", "MIDDLEINT", "MINUTE_MICROSECOND", "MINUTE_SECOND", "MOD", 
      "MODIFIES", "NATURAL", "NOT", "NO_WRITE_TO_BINLOG", "NTH_VALUE", "NTILE", "NULL", "NUMERIC", "OF", "ON", 
      "OPTIMIZE", "OPTIMIZER_COSTS", "OPTION", "OPTIONALLY", "OR", "ORDER", "OUT", "OUTER", "OUTFILE", "OVER", 
      "PARTITION", "PERCENT_RANK", "PERSIST", "PERSIST_ONLY", "PRECISION", "PRIMARY", "PROCEDURE", "PURGE", "RANGE", "RANK", 
      "READ", "READS", "READ_WRITE", "REAL", "RECURSIVE", "REFERENCES", "REGEXP", "RELEASE", "RENAME", "REPEAT", 
      "REPLACE", "REQUIRE", "RESIGNAL", "RESTRICT", "RETURN", "REVOKE", "RIGHT", "RLIKE", "ROW", "ROWS", 
      "ROW_NUMBER", "SCHEMA", "SCHEMAS", "SECOND_MICROSECOND", "SELECT", "SENSITIVE", "SEPARATOR", "SET", "SHOW", "SIGNAL", 
      "SMALLINT", "SPATIAL", "SPECIFIC", "SQL", "SQLEXCEPTION", "SQLSTATE", "SQLWARNING", "SQL_BIG_RESULT", "SQL_CALC_FOUND_ROWS", "SQL_SMALL_RESULT", 
      "SSL", "STARTING", "STORED", "STRAIGHT_JOIN", "SYSTEM", "TABLE", "TERMINATED", "THEN", "TINYBLOB", "TINYINT", 
      "TINYTEXT", "TO", "TRAILING", "TRIGGER", "TRUE", "UNDO", "UNION", "UNIQUE", "UNLOCK", "UNSIGNED", 
      "UPDATE", "USAGE", "USE", "USING", "UTC_DATE", "UTC_TIME", "UTC_TIMESTAMP", "VALUES", "VARBINARY", "VARCHAR", 
      "VARCHARACTER", "VARYING", "VIRTUAL", "WHEN", "WHERE", "WHILE", "WINDOW", "WITH", "WRITE", "XOR", 
      "YEAR_MONTH", "ZEROFILL" };
  
  static final List<String> SQL2003_KEYWORDS = Arrays.asList(new String[] { 
        "ABS", "ALL", "ALLOCATE", "ALTER", "AND", "ANY", "ARE", "ARRAY", "AS", "ASENSITIVE", 
        "ASYMMETRIC", "AT", "ATOMIC", "AUTHORIZATION", "AVG", "BEGIN", "BETWEEN", "BIGINT", "BINARY", "BLOB", 
        "BOOLEAN", "BOTH", "BY", "CALL", "CALLED", "CARDINALITY", "CASCADED", "CASE", "CAST", "CEIL", 
        "CEILING", "CHAR", "CHARACTER", "CHARACTER_LENGTH", "CHAR_LENGTH", "CHECK", "CLOB", "CLOSE", "COALESCE", "COLLATE", 
        "COLLECT", "COLUMN", "COMMIT", "CONDITION", "CONNECT", "CONSTRAINT", "CONVERT", "CORR", "CORRESPONDING", "COUNT", 
        "COVAR_POP", "COVAR_SAMP", "CREATE", "CROSS", "CUBE", "CUME_DIST", "CURRENT", "CURRENT_DATE", "CURRENT_DEFAULT_TRANSFORM_GROUP", "CURRENT_PATH", 
        "CURRENT_ROLE", "CURRENT_TIME", "CURRENT_TIMESTAMP", "CURRENT_TRANSFORM_GROUP_FOR_TYPE", "CURRENT_USER", "CURSOR", "CYCLE", "DATE", "DAY", "DEALLOCATE", 
        "DEC", "DECIMAL", "DECLARE", "DEFAULT", "DELETE", "DENSE_RANK", "DEREF", "DESCRIBE", "DETERMINISTIC", "DISCONNECT", 
        "DISTINCT", "DOUBLE", "DROP", "DYNAMIC", "EACH", "ELEMENT", "ELSE", "END", "END-EXEC", "ESCAPE", 
        "EVERY", "EXCEPT", "EXEC", "EXECUTE", "EXISTS", "EXP", "EXTERNAL", "EXTRACT", "FALSE", "FETCH", 
        "FILTER", "FLOAT", "FLOOR", "FOR", "FOREIGN", "FREE", "FROM", "FULL", "FUNCTION", "FUSION", 
        "GET", "GLOBAL", "GRANT", "GROUP", "GROUPING", "HAVING", "HOLD", "HOUR", "IDENTITY", "IN", 
        "INDICATOR", "INNER", "INOUT", "INSENSITIVE", "INSERT", "INT", "INTEGER", "INTERSECT", "INTERSECTION", "INTERVAL", 
        "INTO", "IS", "JOIN", "LANGUAGE", "LARGE", "LATERAL", "LEADING", "LEFT", "LIKE", "LN", 
        "LOCAL", "LOCALTIME", "LOCALTIMESTAMP", "LOWER", "MATCH", "MAX", "MEMBER", "MERGE", "METHOD", "MIN", 
        "MINUTE", "MOD", "MODIFIES", "MODULE", "MONTH", "MULTISET", "NATIONAL", "NATURAL", "NCHAR", "NCLOB", 
        "NEW", "NO", "NONE", "NORMALIZE", "NOT", "NULL", "NULLIF", "NUMERIC", "OCTET_LENGTH", "OF", 
        "OLD", "ON", "ONLY", "OPEN", "OR", "ORDER", "OUT", "OUTER", "OVER", "OVERLAPS", 
        "OVERLAY", "PARAMETER", "PARTITION", "PERCENTILE_CONT", "PERCENTILE_DISC", "PERCENT_RANK", "POSITION", "POWER", "PRECISION", "PREPARE", 
        "PRIMARY", "PROCEDURE", "RANGE", "RANK", "READS", "REAL", "RECURSIVE", "REF", "REFERENCES", "REFERENCING", 
        "REGR_AVGX", "REGR_AVGY", "REGR_COUNT", "REGR_INTERCEPT", "REGR_R2", "REGR_SLOPE", "REGR_SXX", "REGR_SXY", "REGR_SYY", "RELEASE", 
        "RESULT", "RETURN", "RETURNS", "REVOKE", "RIGHT", "ROLLBACK", "ROLLUP", "ROW", "ROWS", "ROW_NUMBER", 
        "SAVEPOINT", "SCOPE", "SCROLL", "SEARCH", "SECOND", "SELECT", "SENSITIVE", "SESSION_USER", "SET", "SIMILAR", 
        "SMALLINT", "SOME", "SPECIFIC", "SPECIFICTYPE", "SQL", "SQLEXCEPTION", "SQLSTATE", "SQLWARNING", "SQRT", "START", 
        "STATIC", "STDDEV_POP", "STDDEV_SAMP", "SUBMULTISET", "SUBSTRING", "SUM", "SYMMETRIC", "SYSTEM", "SYSTEM_USER", "TABLE", 
        "TABLESAMPLE", "THEN", "TIME", "TIMESTAMP", "TIMEZONE_HOUR", "TIMEZONE_MINUTE", "TO", "TRAILING", "TRANSLATE", "TRANSLATION", 
        "TREAT", "TRIGGER", "TRIM", "TRUE", "UESCAPE", "UNION", "UNIQUE", "UNKNOWN", "UNNEST", "UPDATE", 
        "UPPER", "USER", "USING", "VALUE", "VALUES", "VARCHAR", "VARYING", "VAR_POP", "VAR_SAMP", "WHEN", 
        "WHENEVER", "WHERE", "WIDTH_BUCKET", "WINDOW", "WITH", "WITHIN", "WITHOUT", "YEAR" });
  
  private static volatile String mysqlKeywords = null;
  
  protected JdbcConnection conn;
  
  protected NativeSession session;
  
  protected String database = null;
  
  protected final String quotedId;
  
  protected boolean pedantic;
  
  protected boolean tinyInt1isBit;
  
  protected boolean transformedBitIsBoolean;
  
  protected boolean useHostsInPrivileges;
  
  protected boolean yearIsDateType;
  
  protected RuntimeProperty<PropertyDefinitions.DatabaseTerm> databaseTerm;
  
  protected RuntimeProperty<Boolean> nullDatabaseMeansCurrent;
  
  protected ResultSetFactory resultSetFactory;
  
  private String metadataEncoding;
  
  private int metadataCollationIndex;
  
  private ExceptionInterceptor exceptionInterceptor;
  
  protected static DatabaseMetaData getInstance(JdbcConnection connToSet, String databaseToSet, boolean checkForInfoSchema, ResultSetFactory resultSetFactory) throws SQLException {
    if (checkForInfoSchema && ((Boolean)connToSet.getPropertySet().getBooleanProperty(PropertyKey.useInformationSchema).getValue()).booleanValue())
      return new DatabaseMetaDataUsingInfoSchema(connToSet, databaseToSet, resultSetFactory); 
    return new DatabaseMetaData(connToSet, databaseToSet, resultSetFactory);
  }
  
  protected DatabaseMetaData(JdbcConnection connToSet, String databaseToSet, ResultSetFactory resultSetFactory) {
    this.conn = connToSet;
    this.session = (NativeSession)connToSet.getSession();
    this.database = databaseToSet;
    this.resultSetFactory = resultSetFactory;
    this.exceptionInterceptor = this.conn.getExceptionInterceptor();
    this.databaseTerm = this.conn.getPropertySet().getEnumProperty(PropertyKey.databaseTerm);
    this.nullDatabaseMeansCurrent = this.conn.getPropertySet().getBooleanProperty(PropertyKey.nullDatabaseMeansCurrent);
    this.pedantic = ((Boolean)this.conn.getPropertySet().getBooleanProperty(PropertyKey.pedantic).getValue()).booleanValue();
    this.tinyInt1isBit = ((Boolean)this.conn.getPropertySet().getBooleanProperty(PropertyKey.tinyInt1isBit).getValue()).booleanValue();
    this.transformedBitIsBoolean = ((Boolean)this.conn.getPropertySet().getBooleanProperty(PropertyKey.transformedBitIsBoolean).getValue()).booleanValue();
    this.useHostsInPrivileges = ((Boolean)this.conn.getPropertySet().getBooleanProperty(PropertyKey.useHostsInPrivileges).getValue()).booleanValue();
    this.yearIsDateType = ((Boolean)this.conn.getPropertySet().getBooleanProperty(PropertyKey.yearIsDateType).getValue()).booleanValue();
    this.quotedId = this.session.getIdentifierQuoteString();
  }
  
  public boolean allProceduresAreCallable() throws SQLException {
    try {
      return false;
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public boolean allTablesAreSelectable() throws SQLException {
    try {
      return false;
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  protected void convertToJdbcFunctionList(ResultSet proceduresRs, List<ComparableWrapper<String, Row>> procedureRows, Field[] fields) throws SQLException {
    while (proceduresRs.next()) {
      String procDb = proceduresRs.getString("db");
      String functionName = proceduresRs.getString("name");
      byte[][] rowData = (byte[][])null;
      if (fields != null && fields.length == 9) {
        rowData = new byte[9][];
        rowData[0] = (this.databaseTerm.getValue() == PropertyDefinitions.DatabaseTerm.SCHEMA) ? s2b("def") : s2b(procDb);
        rowData[1] = (this.databaseTerm.getValue() == PropertyDefinitions.DatabaseTerm.SCHEMA) ? s2b(procDb) : null;
        rowData[2] = s2b(functionName);
        rowData[3] = null;
        rowData[4] = null;
        rowData[5] = null;
        rowData[6] = s2b(proceduresRs.getString("comment"));
        rowData[7] = s2b(Integer.toString(2));
        rowData[8] = s2b(functionName);
      } else {
        rowData = new byte[6][];
        rowData[0] = (this.databaseTerm.getValue() == PropertyDefinitions.DatabaseTerm.SCHEMA) ? s2b("def") : s2b(procDb);
        rowData[1] = (this.databaseTerm.getValue() == PropertyDefinitions.DatabaseTerm.SCHEMA) ? s2b(procDb) : null;
        rowData[2] = s2b(functionName);
        rowData[3] = s2b(proceduresRs.getString("comment"));
        rowData[4] = s2b(Integer.toString(1));
        rowData[5] = s2b(functionName);
      } 
      procedureRows.add((ComparableWrapper)new ComparableWrapper<>(StringUtils.getFullyQualifiedName(procDb, functionName, this.quotedId, this.pedantic), new ByteArrayRow(rowData, 
              getExceptionInterceptor())));
    } 
  }
  
  protected void convertToJdbcProcedureList(boolean fromSelect, ResultSet proceduresRs, List<ComparableWrapper<String, Row>> procedureRows) throws SQLException {
    while (proceduresRs.next()) {
      String procDb = proceduresRs.getString("db");
      String procedureName = proceduresRs.getString("name");
      byte[][] rowData = new byte[9][];
      rowData[0] = (this.databaseTerm.getValue() == PropertyDefinitions.DatabaseTerm.SCHEMA) ? s2b("def") : s2b(procDb);
      rowData[1] = (this.databaseTerm.getValue() == PropertyDefinitions.DatabaseTerm.SCHEMA) ? s2b(procDb) : null;
      rowData[2] = s2b(procedureName);
      rowData[3] = null;
      rowData[4] = null;
      rowData[5] = null;
      rowData[6] = s2b(proceduresRs.getString("comment"));
      boolean isFunction = fromSelect ? "FUNCTION".equalsIgnoreCase(proceduresRs.getString("type")) : false;
      rowData[7] = s2b(isFunction ? Integer.toString(2) : Integer.toString(1));
      rowData[8] = s2b(procedureName);
      procedureRows.add((ComparableWrapper)new ComparableWrapper<>(StringUtils.getFullyQualifiedName(procDb, procedureName, this.quotedId, this.pedantic), new ByteArrayRow(rowData, 
              getExceptionInterceptor())));
    } 
  }
  
  private Row convertTypeDescriptorToProcedureRow(byte[] procNameAsBytes, byte[] procCatAsBytes, String paramName, boolean isOutParam, boolean isInParam, boolean isReturnParam, TypeDescriptor typeDesc, boolean forGetFunctionColumns, int ordinal) throws SQLException {
    byte[][] row = forGetFunctionColumns ? new byte[17][] : new byte[20][];
    row[0] = (this.databaseTerm.getValue() == PropertyDefinitions.DatabaseTerm.SCHEMA) ? s2b("def") : procCatAsBytes;
    row[1] = (this.databaseTerm.getValue() == PropertyDefinitions.DatabaseTerm.SCHEMA) ? procCatAsBytes : null;
    row[2] = procNameAsBytes;
    row[3] = s2b(paramName);
    row[4] = s2b(String.valueOf(getColumnType(isOutParam, isInParam, isReturnParam, forGetFunctionColumns)));
    row[5] = Short.toString((typeDesc.mysqlType == MysqlType.YEAR && !this.yearIsDateType) ? 5 : 
        (short)typeDesc.mysqlType.getJdbcType()).getBytes();
    row[6] = s2b(typeDesc.mysqlType.getName());
    row[7] = (typeDesc.datetimePrecision == null) ? s2b(typeDesc.columnSize.toString()) : s2b(typeDesc.datetimePrecision.toString());
    row[8] = (typeDesc.columnSize == null) ? null : s2b(typeDesc.columnSize.toString());
    row[9] = (typeDesc.decimalDigits == null) ? null : s2b(typeDesc.decimalDigits.toString());
    row[10] = s2b(Integer.toString(typeDesc.numPrecRadix));
    switch (typeDesc.nullability) {
      case 0:
        row[11] = s2b(String.valueOf(0));
        break;
      case 1:
        row[11] = s2b(String.valueOf(1));
        break;
      case 2:
        row[11] = s2b(String.valueOf(2));
        break;
      default:
        throw SQLError.createSQLException(Messages.getString("DatabaseMetaData.1"), "S1000", 
            getExceptionInterceptor());
    } 
    row[12] = null;
    if (forGetFunctionColumns) {
      row[13] = (typeDesc.charOctetLength == null) ? null : s2b(typeDesc.charOctetLength.toString());
      row[14] = s2b(String.valueOf(ordinal));
      row[15] = s2b(typeDesc.isNullable);
      row[16] = procNameAsBytes;
    } else {
      row[13] = null;
      row[14] = null;
      row[15] = null;
      row[16] = (typeDesc.charOctetLength == null) ? null : s2b(typeDesc.charOctetLength.toString());
      row[17] = s2b(String.valueOf(ordinal));
      row[18] = s2b(typeDesc.isNullable);
      row[19] = procNameAsBytes;
    } 
    return (Row)new ByteArrayRow(row, getExceptionInterceptor());
  }
  
  protected int getColumnType(boolean isOutParam, boolean isInParam, boolean isReturnParam, boolean forGetFunctionColumns) {
    return getProcedureOrFunctionColumnType(isOutParam, isInParam, isReturnParam, forGetFunctionColumns);
  }
  
  protected static int getProcedureOrFunctionColumnType(boolean isOutParam, boolean isInParam, boolean isReturnParam, boolean forGetFunctionColumns) {
    if (isInParam && isOutParam)
      return forGetFunctionColumns ? 2 : 2; 
    if (isInParam)
      return forGetFunctionColumns ? 1 : 1; 
    if (isOutParam)
      return forGetFunctionColumns ? 3 : 4; 
    if (isReturnParam)
      return forGetFunctionColumns ? 4 : 5; 
    return forGetFunctionColumns ? 0 : 0;
  }
  
  protected ExceptionInterceptor getExceptionInterceptor() {
    return this.exceptionInterceptor;
  }
  
  public boolean dataDefinitionCausesTransactionCommit() throws SQLException {
    try {
      return true;
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public boolean dataDefinitionIgnoredInTransactions() throws SQLException {
    try {
      return false;
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public boolean deletesAreDetected(int type) throws SQLException {
    try {
      return false;
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public boolean doesMaxRowSizeIncludeBlobs() throws SQLException {
    try {
      return true;
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public List<Row> extractForeignKeyForTable(ArrayList<Row> rows, ResultSet rs, String dbName) throws SQLException {
    byte[][] row = new byte[3][];
    row[0] = rs.getBytes(1);
    row[1] = s2b("SUPPORTS_FK");
    String createTableString = rs.getString(2);
    StringTokenizer lineTokenizer = new StringTokenizer(createTableString, "\n");
    StringBuilder commentBuf = new StringBuilder("comment; ");
    boolean firstTime = true;
    while (lineTokenizer.hasMoreTokens()) {
      String line = lineTokenizer.nextToken().trim();
      String constraintName = null;
      if (StringUtils.startsWithIgnoreCase(line, "CONSTRAINT")) {
        boolean usingBackTicks = true;
        int beginPos = StringUtils.indexOfQuoteDoubleAware(line, this.quotedId, 0);
        if (beginPos == -1) {
          beginPos = line.indexOf("\"");
          usingBackTicks = false;
        } 
        if (beginPos != -1) {
          int endPos = -1;
          if (usingBackTicks) {
            endPos = StringUtils.indexOfQuoteDoubleAware(line, this.quotedId, beginPos + 1);
          } else {
            endPos = StringUtils.indexOfQuoteDoubleAware(line, "\"", beginPos + 1);
          } 
          if (endPos != -1) {
            constraintName = line.substring(beginPos + 1, endPos);
            line = line.substring(endPos + 1, line.length()).trim();
          } 
        } 
      } 
      if (line.startsWith("FOREIGN KEY")) {
        if (line.endsWith(","))
          line = line.substring(0, line.length() - 1); 
        int indexOfFK = line.indexOf("FOREIGN KEY");
        String localColumnName = null;
        String referencedDbName = StringUtils.quoteIdentifier(dbName, this.quotedId, this.pedantic);
        String referencedTableName = null;
        String referencedColumnName = null;
        if (indexOfFK != -1) {
          int afterFk = indexOfFK + "FOREIGN KEY".length();
          int indexOfRef = StringUtils.indexOfIgnoreCase(afterFk, line, "REFERENCES", this.quotedId, this.quotedId, SearchMode.__BSE_MRK_COM_MYM_HNT_WS);
          if (indexOfRef != -1) {
            int indexOfParenOpen = line.indexOf('(', afterFk);
            int indexOfParenClose = StringUtils.indexOfIgnoreCase(indexOfParenOpen, line, ")", this.quotedId, this.quotedId, SearchMode.__BSE_MRK_COM_MYM_HNT_WS);
            if (indexOfParenOpen == -1 || indexOfParenClose == -1);
            localColumnName = line.substring(indexOfParenOpen + 1, indexOfParenClose);
            int afterRef = indexOfRef + "REFERENCES".length();
            int referencedColumnBegin = StringUtils.indexOfIgnoreCase(afterRef, line, "(", this.quotedId, this.quotedId, SearchMode.__BSE_MRK_COM_MYM_HNT_WS);
            if (referencedColumnBegin != -1) {
              referencedTableName = line.substring(afterRef, referencedColumnBegin);
              int referencedColumnEnd = StringUtils.indexOfIgnoreCase(referencedColumnBegin + 1, line, ")", this.quotedId, this.quotedId, SearchMode.__BSE_MRK_COM_MYM_HNT_WS);
              if (referencedColumnEnd != -1)
                referencedColumnName = line.substring(referencedColumnBegin + 1, referencedColumnEnd); 
              int indexOfDbSep = StringUtils.indexOfIgnoreCase(0, referencedTableName, ".", this.quotedId, this.quotedId, SearchMode.__BSE_MRK_COM_MYM_HNT_WS);
              if (indexOfDbSep != -1) {
                referencedDbName = referencedTableName.substring(0, indexOfDbSep);
                referencedTableName = referencedTableName.substring(indexOfDbSep + 1);
              } 
            } 
          } 
        } 
        if (!firstTime) {
          commentBuf.append("; ");
        } else {
          firstTime = false;
        } 
        if (constraintName != null) {
          commentBuf.append(constraintName);
        } else {
          commentBuf.append("not_available");
        } 
        commentBuf.append("(");
        commentBuf.append(localColumnName);
        commentBuf.append(") REFER ");
        commentBuf.append(referencedDbName);
        commentBuf.append("/");
        commentBuf.append(referencedTableName);
        commentBuf.append("(");
        commentBuf.append(referencedColumnName);
        commentBuf.append(")");
        int lastParenIndex = line.lastIndexOf(")");
        if (lastParenIndex != line.length() - 1) {
          String cascadeOptions = line.substring(lastParenIndex + 1);
          commentBuf.append(" ");
          commentBuf.append(cascadeOptions);
        } 
      } 
    } 
    row[2] = s2b(commentBuf.toString());
    rows.add(new ByteArrayRow(row, getExceptionInterceptor()));
    return rows;
  }
  
  public ResultSet extractForeignKeyFromCreateTable(String dbName, String tableName) throws SQLException {
    ArrayList<String> tableList = new ArrayList<>();
    ResultSet rs = null;
    Statement stmt = null;
    if (tableName != null) {
      tableList.add(tableName);
    } else {
      try {
        rs = (this.databaseTerm.getValue() == PropertyDefinitions.DatabaseTerm.SCHEMA) ? getTables(null, dbName, null, new String[] { "TABLE" }) : getTables(dbName, null, null, new String[] { "TABLE" });
        while (rs.next())
          tableList.add(rs.getString("TABLE_NAME")); 
      } finally {
        if (rs != null)
          rs.close(); 
        rs = null;
      } 
    } 
    ArrayList<Row> rows = new ArrayList<>();
    Field[] fields = new Field[3];
    fields[0] = new Field("", "Name", this.metadataCollationIndex, this.metadataEncoding, MysqlType.CHAR, 2147483647);
    fields[1] = new Field("", "Type", this.metadataCollationIndex, this.metadataEncoding, MysqlType.CHAR, 255);
    fields[2] = new Field("", "Comment", this.metadataCollationIndex, this.metadataEncoding, MysqlType.CHAR, 2147483647);
    int numTables = tableList.size();
    stmt = this.conn.getMetadataSafeStatement();
    try {
      for (int i = 0; i < numTables; i++) {
        String tableToExtract = tableList.get(i);
        String query = "SHOW CREATE TABLE " + StringUtils.getFullyQualifiedName(dbName, tableToExtract, this.quotedId, this.pedantic);
        try {
          rs = stmt.executeQuery(query);
        } catch (SQLException sqlEx) {
          String sqlState = sqlEx.getSQLState();
          if (!"42S02".equals(sqlState) && sqlEx.getErrorCode() != 1146 && sqlEx
            .getErrorCode() != 1049)
            throw sqlEx; 
        } 
        while (rs != null && rs.next())
          extractForeignKeyForTable(rows, rs, dbName); 
      } 
    } finally {
      if (rs != null)
        rs.close(); 
      rs = null;
      if (stmt != null)
        stmt.close(); 
      stmt = null;
    } 
    return (ResultSet)this.resultSetFactory.createFromResultsetRows(1007, 1004, (ResultsetRows)new ResultsetRowsStatic(rows, (ColumnDefinition)new DefaultColumnDefinition(fields)));
  }
  
  public ResultSet getAttributes(String arg0, String arg1, String arg2, String arg3) throws SQLException {
    try {
      Field[] fields = new Field[21];
      fields[0] = new Field("", "TYPE_CAT", this.metadataCollationIndex, this.metadataEncoding, MysqlType.CHAR, 32);
      fields[1] = new Field("", "TYPE_SCHEM", this.metadataCollationIndex, this.metadataEncoding, MysqlType.CHAR, 32);
      fields[2] = new Field("", "TYPE_NAME", this.metadataCollationIndex, this.metadataEncoding, MysqlType.CHAR, 32);
      fields[3] = new Field("", "ATTR_NAME", this.metadataCollationIndex, this.metadataEncoding, MysqlType.CHAR, 32);
      fields[4] = new Field("", "DATA_TYPE", this.metadataCollationIndex, this.metadataEncoding, MysqlType.SMALLINT, 32);
      fields[5] = new Field("", "ATTR_TYPE_NAME", this.metadataCollationIndex, this.metadataEncoding, MysqlType.CHAR, 32);
      fields[6] = new Field("", "ATTR_SIZE", this.metadataCollationIndex, this.metadataEncoding, MysqlType.INT, 32);
      fields[7] = new Field("", "DECIMAL_DIGITS", this.metadataCollationIndex, this.metadataEncoding, MysqlType.INT, 32);
      fields[8] = new Field("", "NUM_PREC_RADIX", this.metadataCollationIndex, this.metadataEncoding, MysqlType.INT, 32);
      fields[9] = new Field("", "NULLABLE ", this.metadataCollationIndex, this.metadataEncoding, MysqlType.INT, 32);
      fields[10] = new Field("", "REMARKS", this.metadataCollationIndex, this.metadataEncoding, MysqlType.CHAR, 32);
      fields[11] = new Field("", "ATTR_DEF", this.metadataCollationIndex, this.metadataEncoding, MysqlType.CHAR, 32);
      fields[12] = new Field("", "SQL_DATA_TYPE", this.metadataCollationIndex, this.metadataEncoding, MysqlType.INT, 32);
      fields[13] = new Field("", "SQL_DATETIME_SUB", this.metadataCollationIndex, this.metadataEncoding, MysqlType.INT, 32);
      fields[14] = new Field("", "CHAR_OCTET_LENGTH", this.metadataCollationIndex, this.metadataEncoding, MysqlType.INT, 32);
      fields[15] = new Field("", "ORDINAL_POSITION", this.metadataCollationIndex, this.metadataEncoding, MysqlType.INT, 32);
      fields[16] = new Field("", "IS_NULLABLE", this.metadataCollationIndex, this.metadataEncoding, MysqlType.CHAR, 32);
      fields[17] = new Field("", "SCOPE_CATALOG", this.metadataCollationIndex, this.metadataEncoding, MysqlType.CHAR, 32);
      fields[18] = new Field("", "SCOPE_SCHEMA", this.metadataCollationIndex, this.metadataEncoding, MysqlType.CHAR, 32);
      fields[19] = new Field("", "SCOPE_TABLE", this.metadataCollationIndex, this.metadataEncoding, MysqlType.CHAR, 32);
      fields[20] = new Field("", "SOURCE_DATA_TYPE", this.metadataCollationIndex, this.metadataEncoding, MysqlType.SMALLINT, 32);
      return (ResultSet)this.resultSetFactory.createFromResultsetRows(1007, 1004, (ResultsetRows)new ResultsetRowsStatic(new ArrayList(), (ColumnDefinition)new DefaultColumnDefinition(fields)));
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public ResultSet getBestRowIdentifier(String catalog, String schema, final String table, int scope, boolean nullable) throws SQLException {
    try {
      if (table == null)
        throw SQLError.createSQLException(Messages.getString("DatabaseMetaData.2"), "S1009", 
            getExceptionInterceptor()); 
      Field[] fields = new Field[8];
      fields[0] = new Field("", "SCOPE", this.metadataCollationIndex, this.metadataEncoding, MysqlType.SMALLINT, 5);
      fields[1] = new Field("", "COLUMN_NAME", this.metadataCollationIndex, this.metadataEncoding, MysqlType.CHAR, 32);
      fields[2] = new Field("", "DATA_TYPE", this.metadataCollationIndex, this.metadataEncoding, MysqlType.INT, 32);
      fields[3] = new Field("", "TYPE_NAME", this.metadataCollationIndex, this.metadataEncoding, MysqlType.CHAR, 32);
      fields[4] = new Field("", "COLUMN_SIZE", this.metadataCollationIndex, this.metadataEncoding, MysqlType.INT, 10);
      fields[5] = new Field("", "BUFFER_LENGTH", this.metadataCollationIndex, this.metadataEncoding, MysqlType.INT, 10);
      fields[6] = new Field("", "DECIMAL_DIGITS", this.metadataCollationIndex, this.metadataEncoding, MysqlType.SMALLINT, 10);
      fields[7] = new Field("", "PSEUDO_COLUMN", this.metadataCollationIndex, this.metadataEncoding, MysqlType.SMALLINT, 5);
      final ArrayList<Row> rows = new ArrayList<>();
      final Statement stmt = this.conn.getMetadataSafeStatement();
      String db = getDatabase(catalog, schema);
      try {
        (new IterateBlock<String>(getDatabaseIterator(db)) {
            void forEach(String dbStr) throws SQLException {
              ResultSet results = null;
              try {
                StringBuilder queryBuf = new StringBuilder("SHOW COLUMNS FROM ");
                queryBuf.append(StringUtils.quoteIdentifier(table, DatabaseMetaData.this.quotedId, DatabaseMetaData.this.pedantic));
                queryBuf.append(" FROM ");
                queryBuf.append(StringUtils.quoteIdentifier(dbStr, DatabaseMetaData.this.quotedId, DatabaseMetaData.this.pedantic));
                try {
                  results = stmt.executeQuery(queryBuf.toString());
                } catch (SQLException sqlEx) {
                  String sqlState = sqlEx.getSQLState();
                  int errorCode = sqlEx.getErrorCode();
                  if (!"42S02".equals(sqlState) && errorCode != 1146 && errorCode != 1049)
                    throw sqlEx; 
                } 
                while (results != null && results.next()) {
                  String keyType = results.getString("Key");
                  if (keyType != null && 
                    StringUtils.startsWithIgnoreCase(keyType, "PRI")) {
                    byte[][] rowVal = new byte[8][];
                    rowVal[0] = Integer.toString(2).getBytes();
                    rowVal[1] = results.getBytes("Field");
                    String type = results.getString("Type");
                    int size = stmt.getMaxFieldSize();
                    int decimals = 0;
                    boolean hasLength = false;
                    if (type.indexOf("enum") != -1) {
                      String temp = type.substring(type.indexOf("("), type.indexOf(")"));
                      StringTokenizer tokenizer = new StringTokenizer(temp, ",");
                      int maxLength = 0;
                      while (tokenizer.hasMoreTokens())
                        maxLength = Math.max(maxLength, tokenizer.nextToken().length() - 2); 
                      size = maxLength;
                      decimals = 0;
                      type = "enum";
                    } else if (type.indexOf("(") != -1) {
                      hasLength = true;
                      if (type.indexOf(",") != -1) {
                        size = Integer.parseInt(type.substring(type.indexOf("(") + 1, type.indexOf(",")));
                        decimals = Integer.parseInt(type.substring(type.indexOf(",") + 1, type.indexOf(")")));
                      } else {
                        size = Integer.parseInt(type.substring(type.indexOf("(") + 1, type.indexOf(")")));
                      } 
                      type = type.substring(0, type.indexOf("("));
                    } 
                    MysqlType ft = MysqlType.getByName(type.toUpperCase());
                    rowVal[2] = DatabaseMetaData.this.s2b(
                        String.valueOf((ft == MysqlType.YEAR && !DatabaseMetaData.this.yearIsDateType) ? 5 : ft.getJdbcType()));
                    rowVal[3] = DatabaseMetaData.this.s2b(type);
                    rowVal[4] = hasLength ? Integer.toString(size + decimals).getBytes() : Long.toString(ft.getPrecision().longValue()).getBytes();
                    rowVal[5] = Integer.toString(DatabaseMetaData.maxBufferSize).getBytes();
                    rowVal[6] = Integer.toString(decimals).getBytes();
                    rowVal[7] = Integer.toString(1).getBytes();
                    rows.add(new ByteArrayRow(rowVal, DatabaseMetaData.this.getExceptionInterceptor()));
                  } 
                } 
              } catch (SQLException sqlEx) {
                if (!"42S02".equals(sqlEx.getSQLState()))
                  throw sqlEx; 
              } finally {
                if (results != null) {
                  try {
                    results.close();
                  } catch (Exception exception) {}
                  results = null;
                } 
              } 
            }
          }).doForAll();
      } finally {
        if (stmt != null)
          stmt.close(); 
      } 
      return (ResultSet)this.resultSetFactory.createFromResultsetRows(1007, 1004, (ResultsetRows)new ResultsetRowsStatic(rows, (ColumnDefinition)new DefaultColumnDefinition(fields)));
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  private void getCallStmtParameterTypes(String db, String quotedProcName, ProcedureType procType, String parameterNamePattern, List<Row> resultRows, boolean forGetFunctionColumns) throws SQLException {
    Statement paramRetrievalStmt = null;
    ResultSet paramRetrievalRs = null;
    String parameterDef = null;
    byte[] procNameAsBytes = null;
    byte[] procCatAsBytes = null;
    boolean isProcedureInAnsiMode = false;
    String storageDefnDelims = null;
    String storageDefnClosures = null;
    try {
      paramRetrievalStmt = this.conn.getMetadataSafeStatement();
      String oldDb = this.conn.getDatabase();
      if (this.conn.lowerCaseTableNames() && db != null && db.length() != 0 && oldDb != null && oldDb.length() != 0) {
        ResultSet rs = null;
        try {
          this.conn.setDatabase(StringUtils.unQuoteIdentifier(db, this.quotedId));
          rs = paramRetrievalStmt.executeQuery("SELECT DATABASE()");
          rs.next();
          db = rs.getString(1);
        } finally {
          this.conn.setDatabase(oldDb);
          if (rs != null)
            rs.close(); 
        } 
      } 
      if (paramRetrievalStmt.getMaxRows() != 0)
        paramRetrievalStmt.setMaxRows(0); 
      int dotIndex = " ".equals(this.quotedId) ? quotedProcName.indexOf(".") : StringUtils.indexOfIgnoreCase(0, quotedProcName, ".", this.quotedId, this.quotedId, 
          this.session.getServerSession().isNoBackslashEscapesSet() ? SearchMode.__MRK_COM_MYM_HNT_WS : SearchMode.__BSE_MRK_COM_MYM_HNT_WS);
      String dbName = null;
      if (dotIndex != -1 && dotIndex + 1 < quotedProcName.length()) {
        dbName = quotedProcName.substring(0, dotIndex);
        quotedProcName = quotedProcName.substring(dotIndex + 1);
      } else {
        dbName = StringUtils.quoteIdentifier(db, this.quotedId, this.pedantic);
      } 
      String tmpProcName = StringUtils.unQuoteIdentifier(quotedProcName, this.quotedId);
      procNameAsBytes = StringUtils.getBytes(tmpProcName, "UTF-8");
      tmpProcName = StringUtils.unQuoteIdentifier(dbName, this.quotedId);
      procCatAsBytes = StringUtils.getBytes(tmpProcName, "UTF-8");
      StringBuilder procNameBuf = new StringBuilder();
      procNameBuf.append(dbName);
      procNameBuf.append('.');
      procNameBuf.append(quotedProcName);
      String fieldName = null;
      if (procType == ProcedureType.PROCEDURE) {
        paramRetrievalRs = paramRetrievalStmt.executeQuery("SHOW CREATE PROCEDURE " + procNameBuf.toString());
        fieldName = "Create Procedure";
      } else {
        paramRetrievalRs = paramRetrievalStmt.executeQuery("SHOW CREATE FUNCTION " + procNameBuf.toString());
        fieldName = "Create Function";
      } 
      if (paramRetrievalRs.next()) {
        String procedureDef = paramRetrievalRs.getString(fieldName);
        if (!((Boolean)this.conn.getPropertySet().getBooleanProperty(PropertyKey.noAccessToProcedureBodies).getValue()).booleanValue() && (procedureDef == null || procedureDef
          .length() == 0))
          throw SQLError.createSQLException(Messages.getString("DatabaseMetaData.4"), "S1000", 
              getExceptionInterceptor()); 
        try {
          String sqlMode = paramRetrievalRs.getString("sql_mode");
          if (StringUtils.indexOfIgnoreCase(sqlMode, "ANSI") != -1)
            isProcedureInAnsiMode = true; 
        } catch (SQLException sQLException) {}
        String identifierMarkers = isProcedureInAnsiMode ? "`\"" : "`";
        String identifierAndStringMarkers = "'" + identifierMarkers;
        storageDefnDelims = "(" + identifierMarkers;
        storageDefnClosures = ")" + identifierMarkers;
        if (procedureDef != null && procedureDef.length() != 0) {
          procedureDef = StringUtils.stripCommentsAndHints(procedureDef, identifierAndStringMarkers, identifierAndStringMarkers, 
              !this.session.getServerSession().isNoBackslashEscapesSet());
          int openParenIndex = StringUtils.indexOfIgnoreCase(0, procedureDef, "(", this.quotedId, this.quotedId, 
              this.session.getServerSession().isNoBackslashEscapesSet() ? SearchMode.__MRK_COM_MYM_HNT_WS : SearchMode.__FULL);
          int endOfParamDeclarationIndex = 0;
          endOfParamDeclarationIndex = endPositionOfParameterDeclaration(openParenIndex, procedureDef, this.quotedId);
          if (procType == ProcedureType.FUNCTION) {
            int returnsIndex = StringUtils.indexOfIgnoreCase(0, procedureDef, " RETURNS ", this.quotedId, this.quotedId, 
                this.session.getServerSession().isNoBackslashEscapesSet() ? SearchMode.__MRK_COM_MYM_HNT_WS : SearchMode.__FULL);
            int endReturnsDef = findEndOfReturnsClause(procedureDef, returnsIndex);
            int declarationStart = returnsIndex + "RETURNS ".length();
            while (declarationStart < procedureDef.length() && 
              Character.isWhitespace(procedureDef.charAt(declarationStart)))
              declarationStart++; 
            String returnsDefn = procedureDef.substring(declarationStart, endReturnsDef).trim();
            TypeDescriptor returnDescriptor = new TypeDescriptor(returnsDefn, "YES");
            resultRows.add(convertTypeDescriptorToProcedureRow(procNameAsBytes, procCatAsBytes, "", false, false, true, returnDescriptor, forGetFunctionColumns, 0));
          } 
          if (openParenIndex == -1 || endOfParamDeclarationIndex == -1)
            throw SQLError.createSQLException(Messages.getString("DatabaseMetaData.5"), "S1000", 
                getExceptionInterceptor()); 
          parameterDef = procedureDef.substring(openParenIndex + 1, endOfParamDeclarationIndex);
        } 
      } 
    } finally {
      SQLException sqlExRethrow = null;
      if (paramRetrievalRs != null) {
        try {
          paramRetrievalRs.close();
        } catch (SQLException sqlEx) {
          sqlExRethrow = sqlEx;
        } 
        paramRetrievalRs = null;
      } 
      if (paramRetrievalStmt != null) {
        try {
          paramRetrievalStmt.close();
        } catch (SQLException sqlEx) {
          sqlExRethrow = sqlEx;
        } 
        paramRetrievalStmt = null;
      } 
      if (sqlExRethrow != null)
        throw sqlExRethrow; 
    } 
    if (parameterDef != null) {
      int ordinal = 1;
      List<String> parseList = StringUtils.split(parameterDef, ",", storageDefnDelims, storageDefnClosures, true);
      int parseListLen = parseList.size();
      for (int i = 0; i < parseListLen; i++) {
        String declaration = parseList.get(i);
        if (declaration.trim().length() == 0)
          break; 
        declaration = declaration.replaceAll("[\\t\\n\\x0B\\f\\r]", " ");
        StringTokenizer declarationTok = new StringTokenizer(declaration, " \t");
        String paramName = null;
        boolean isOutParam = false;
        boolean isInParam = false;
        if (declarationTok.hasMoreTokens()) {
          String possibleParamName = declarationTok.nextToken();
          if (possibleParamName.equalsIgnoreCase("OUT")) {
            isOutParam = true;
            if (declarationTok.hasMoreTokens()) {
              paramName = declarationTok.nextToken();
            } else {
              throw SQLError.createSQLException(Messages.getString("DatabaseMetaData.6"), "S1000", 
                  getExceptionInterceptor());
            } 
          } else if (possibleParamName.equalsIgnoreCase("INOUT")) {
            isOutParam = true;
            isInParam = true;
            if (declarationTok.hasMoreTokens()) {
              paramName = declarationTok.nextToken();
            } else {
              throw SQLError.createSQLException(Messages.getString("DatabaseMetaData.6"), "S1000", 
                  getExceptionInterceptor());
            } 
          } else if (possibleParamName.equalsIgnoreCase("IN")) {
            isOutParam = false;
            isInParam = true;
            if (declarationTok.hasMoreTokens()) {
              paramName = declarationTok.nextToken();
            } else {
              throw SQLError.createSQLException(Messages.getString("DatabaseMetaData.6"), "S1000", 
                  getExceptionInterceptor());
            } 
          } else {
            isOutParam = false;
            isInParam = true;
            paramName = possibleParamName;
          } 
          TypeDescriptor typeDesc = null;
          if (declarationTok.hasMoreTokens()) {
            StringBuilder typeInfoBuf = new StringBuilder(declarationTok.nextToken());
            while (declarationTok.hasMoreTokens()) {
              typeInfoBuf.append(" ");
              typeInfoBuf.append(declarationTok.nextToken());
            } 
            String typeInfo = typeInfoBuf.toString();
            typeDesc = new TypeDescriptor(typeInfo, "YES");
          } else {
            throw SQLError.createSQLException(Messages.getString("DatabaseMetaData.7"), "S1000", 
                getExceptionInterceptor());
          } 
          if ((paramName.startsWith("`") && paramName.endsWith("`")) || (isProcedureInAnsiMode && paramName
            .startsWith("\"") && paramName.endsWith("\"")))
            paramName = paramName.substring(1, paramName.length() - 1); 
          if (parameterNamePattern == null || StringUtils.wildCompareIgnoreCase(paramName, parameterNamePattern)) {
            Row row = convertTypeDescriptorToProcedureRow(procNameAsBytes, procCatAsBytes, paramName, isOutParam, isInParam, false, typeDesc, forGetFunctionColumns, ordinal++);
            resultRows.add(row);
          } 
        } else {
          throw SQLError.createSQLException(Messages.getString("DatabaseMetaData.8"), "S1000", 
              getExceptionInterceptor());
        } 
      } 
    } 
  }
  
  private int endPositionOfParameterDeclaration(int beginIndex, String procedureDef, String quoteChar) throws SQLException {
    int currentPos = beginIndex + 1;
    int parenDepth = 1;
    while (parenDepth > 0 && currentPos < procedureDef.length()) {
      int closedParenIndex = StringUtils.indexOfIgnoreCase(currentPos, procedureDef, ")", quoteChar, quoteChar, 
          this.session.getServerSession().isNoBackslashEscapesSet() ? SearchMode.__MRK_COM_MYM_HNT_WS : SearchMode.__BSE_MRK_COM_MYM_HNT_WS);
      if (closedParenIndex != -1) {
        int nextOpenParenIndex = StringUtils.indexOfIgnoreCase(currentPos, procedureDef, "(", quoteChar, quoteChar, 
            this.session.getServerSession().isNoBackslashEscapesSet() ? SearchMode.__MRK_COM_MYM_HNT_WS : SearchMode.__BSE_MRK_COM_MYM_HNT_WS);
        if (nextOpenParenIndex != -1 && nextOpenParenIndex < closedParenIndex) {
          parenDepth++;
          currentPos = closedParenIndex + 1;
          continue;
        } 
        parenDepth--;
        currentPos = closedParenIndex;
        continue;
      } 
      throw SQLError.createSQLException(Messages.getString("DatabaseMetaData.5"), "S1000", 
          getExceptionInterceptor());
    } 
    return currentPos;
  }
  
  private int findEndOfReturnsClause(String procedureDefn, int positionOfReturnKeyword) throws SQLException {
    String openingMarkers = this.quotedId + "(";
    String closingMarkers = this.quotedId + ")";
    String[] tokens = { 
        "LANGUAGE", "NOT", "DETERMINISTIC", "CONTAINS", "NO", "READ", "MODIFIES", "SQL", "COMMENT", "BEGIN", 
        "RETURN" };
    int startLookingAt = positionOfReturnKeyword + "RETURNS".length() + 1;
    int endOfReturn = -1;
    int i;
    for (i = 0; i < tokens.length; i++) {
      int nextEndOfReturn = StringUtils.indexOfIgnoreCase(startLookingAt, procedureDefn, tokens[i], openingMarkers, closingMarkers, 
          this.session.getServerSession().isNoBackslashEscapesSet() ? SearchMode.__MRK_COM_MYM_HNT_WS : SearchMode.__BSE_MRK_COM_MYM_HNT_WS);
      if (nextEndOfReturn != -1 && (
        endOfReturn == -1 || nextEndOfReturn < endOfReturn))
        endOfReturn = nextEndOfReturn; 
    } 
    if (endOfReturn != -1)
      return endOfReturn; 
    endOfReturn = StringUtils.indexOfIgnoreCase(startLookingAt, procedureDefn, ":", openingMarkers, closingMarkers, 
        this.session.getServerSession().isNoBackslashEscapesSet() ? SearchMode.__MRK_COM_MYM_HNT_WS : SearchMode.__BSE_MRK_COM_MYM_HNT_WS);
    if (endOfReturn != -1)
      for (i = endOfReturn; i > 0; i--) {
        if (Character.isWhitespace(procedureDefn.charAt(i)))
          return i; 
      }  
    throw SQLError.createSQLException(Messages.getString("DatabaseMetaData.5"), "S1000", getExceptionInterceptor());
  }
  
  private int getCascadeDeleteOption(String cascadeOptions) {
    int onDeletePos = cascadeOptions.indexOf("ON DELETE");
    if (onDeletePos != -1) {
      String deleteOptions = cascadeOptions.substring(onDeletePos, cascadeOptions.length());
      if (deleteOptions.startsWith("ON DELETE CASCADE"))
        return 0; 
      if (deleteOptions.startsWith("ON DELETE SET NULL"))
        return 2; 
    } 
    return 1;
  }
  
  private int getCascadeUpdateOption(String cascadeOptions) {
    int onUpdatePos = cascadeOptions.indexOf("ON UPDATE");
    if (onUpdatePos != -1) {
      String updateOptions = cascadeOptions.substring(onUpdatePos, cascadeOptions.length());
      if (updateOptions.startsWith("ON UPDATE CASCADE"))
        return 0; 
      if (updateOptions.startsWith("ON UPDATE SET NULL"))
        return 2; 
    } 
    return 1;
  }
  
  protected IteratorWithCleanup<String> getDatabaseIterator(String dbSpec) throws SQLException {
    if (dbSpec == null)
      return ((Boolean)this.nullDatabaseMeansCurrent.getValue()).booleanValue() ? new SingleStringIterator(this.database) : new StringListIterator(getDatabases()); 
    return new SingleStringIterator(this.pedantic ? dbSpec : StringUtils.unQuoteIdentifier(dbSpec, this.quotedId));
  }
  
  protected IteratorWithCleanup<String> getSchemaPatternIterator(String schemaPattern) throws SQLException {
    if (schemaPattern == null)
      return ((Boolean)this.nullDatabaseMeansCurrent.getValue()).booleanValue() ? new SingleStringIterator(this.database) : new StringListIterator(getDatabases()); 
    return new StringListIterator(getDatabases(schemaPattern));
  }
  
  protected List<String> getDatabases() throws SQLException {
    return getDatabases(null);
  }
  
  protected List<String> getDatabases(String dbPattern) throws SQLException {
    PreparedStatement pStmt = null;
    ResultSet results = null;
    Statement stmt = null;
    try {
      stmt = this.conn.getMetadataSafeStatement();
      StringBuilder queryBuf = new StringBuilder("SHOW DATABASES");
      if (dbPattern != null)
        queryBuf.append(" LIKE ?"); 
      pStmt = prepareMetaDataSafeStatement(queryBuf.toString());
      if (dbPattern != null)
        pStmt.setString(1, dbPattern); 
      results = pStmt.executeQuery();
      int dbCount = 0;
      if (results.last()) {
        dbCount = results.getRow();
        results.beforeFirst();
      } 
      List<String> resultsAsList = new ArrayList<>(dbCount);
      while (results.next())
        resultsAsList.add(results.getString(1)); 
      Collections.sort(resultsAsList);
      return resultsAsList;
    } finally {
      if (results != null) {
        try {
          results.close();
        } catch (SQLException sqlEx) {
          AssertionFailedException.shouldNotHappen(sqlEx);
        } 
        results = null;
      } 
      if (pStmt != null) {
        try {
          pStmt.close();
        } catch (Exception exception) {}
        pStmt = null;
      } 
      if (stmt != null) {
        try {
          stmt.close();
        } catch (SQLException sqlEx) {
          AssertionFailedException.shouldNotHappen(sqlEx);
        } 
        stmt = null;
      } 
    } 
  }
  
  public ResultSet getCatalogs() throws SQLException {
    try {
      List<String> resultsAsList = (this.databaseTerm.getValue() == PropertyDefinitions.DatabaseTerm.SCHEMA) ? new ArrayList<>() : getDatabases();
      Field[] fields = new Field[1];
      fields[0] = new Field("", "TABLE_CAT", this.metadataCollationIndex, this.metadataEncoding, MysqlType.VARCHAR, 0);
      ArrayList<Row> tuples = new ArrayList<>(resultsAsList.size());
      for (String cat : resultsAsList) {
        byte[][] rowVal = new byte[1][];
        rowVal[0] = s2b(cat);
        tuples.add(new ByteArrayRow(rowVal, getExceptionInterceptor()));
      } 
      return (ResultSet)this.resultSetFactory.createFromResultsetRows(1007, 1004, (ResultsetRows)new ResultsetRowsStatic(tuples, (ColumnDefinition)new DefaultColumnDefinition(fields)));
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public String getCatalogSeparator() throws SQLException {
    try {
      return ".";
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public String getCatalogTerm() throws SQLException {
    try {
      return (this.databaseTerm.getValue() == PropertyDefinitions.DatabaseTerm.SCHEMA) ? "CATALOG" : "database";
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  protected String getDatabase(String catalog, String schema) {
    if (this.databaseTerm.getValue() == PropertyDefinitions.DatabaseTerm.SCHEMA)
      return (schema == null && ((Boolean)this.nullDatabaseMeansCurrent.getValue()).booleanValue()) ? this.database : schema; 
    return (catalog == null && ((Boolean)this.nullDatabaseMeansCurrent.getValue()).booleanValue()) ? this.database : catalog;
  }
  
  protected Field[] getColumnPrivilegesFields() {
    Field[] fields = new Field[8];
    fields[0] = new Field("", "TABLE_CAT", this.metadataCollationIndex, this.metadataEncoding, MysqlType.CHAR, 64);
    fields[1] = new Field("", "TABLE_SCHEM", this.metadataCollationIndex, this.metadataEncoding, MysqlType.CHAR, 1);
    fields[2] = new Field("", "TABLE_NAME", this.metadataCollationIndex, this.metadataEncoding, MysqlType.CHAR, 64);
    fields[3] = new Field("", "COLUMN_NAME", this.metadataCollationIndex, this.metadataEncoding, MysqlType.CHAR, 64);
    fields[4] = new Field("", "GRANTOR", this.metadataCollationIndex, this.metadataEncoding, MysqlType.CHAR, 77);
    fields[5] = new Field("", "GRANTEE", this.metadataCollationIndex, this.metadataEncoding, MysqlType.CHAR, 77);
    fields[6] = new Field("", "PRIVILEGE", this.metadataCollationIndex, this.metadataEncoding, MysqlType.CHAR, 64);
    fields[7] = new Field("", "IS_GRANTABLE", this.metadataCollationIndex, this.metadataEncoding, MysqlType.CHAR, 3);
    return fields;
  }
  
  public ResultSet getColumnPrivileges(String catalog, String schema, String table, String columnNamePattern) throws SQLException {
    try {
      String db = getDatabase(catalog, schema);
      StringBuilder grantQueryBuf = new StringBuilder("SELECT c.host, c.db, t.grantor, c.user, c.table_name, c.column_name, c.column_priv");
      grantQueryBuf.append(" FROM mysql.columns_priv c, mysql.tables_priv t");
      grantQueryBuf.append(" WHERE c.host = t.host AND c.db = t.db AND c.table_name = t.table_name");
      if (db != null)
        grantQueryBuf.append(" AND c.db = ?"); 
      grantQueryBuf.append(" AND c.table_name = ?");
      if (columnNamePattern != null)
        grantQueryBuf.append(" AND c.column_name LIKE ?"); 
      PreparedStatement pStmt = null;
      ResultSet results = null;
      ArrayList<Row> grantRows = new ArrayList<>();
      try {
        pStmt = prepareMetaDataSafeStatement(grantQueryBuf.toString());
        int nextId = 1;
        if (db != null)
          pStmt.setString(nextId++, db); 
        pStmt.setString(nextId++, table);
        if (columnNamePattern != null)
          pStmt.setString(nextId, columnNamePattern); 
        results = pStmt.executeQuery();
        while (results.next()) {
          String host = results.getString(1);
          db = results.getString(2);
          String grantor = results.getString(3);
          String user = results.getString(4);
          if (user == null || user.length() == 0)
            user = "%"; 
          StringBuilder fullUser = new StringBuilder(user);
          if (host != null && this.useHostsInPrivileges) {
            fullUser.append("@");
            fullUser.append(host);
          } 
          String columnName = results.getString(6);
          String allPrivileges = results.getString(7);
          if (allPrivileges != null) {
            allPrivileges = allPrivileges.toUpperCase(Locale.ENGLISH);
            StringTokenizer st = new StringTokenizer(allPrivileges, ",");
            while (st.hasMoreTokens()) {
              String privilege = st.nextToken().trim();
              byte[][] tuple = new byte[8][];
              tuple[0] = (this.databaseTerm.getValue() == PropertyDefinitions.DatabaseTerm.SCHEMA) ? s2b("def") : s2b(db);
              tuple[1] = (this.databaseTerm.getValue() == PropertyDefinitions.DatabaseTerm.SCHEMA) ? s2b(db) : null;
              tuple[2] = s2b(table);
              tuple[3] = s2b(columnName);
              tuple[4] = (grantor != null) ? s2b(grantor) : null;
              tuple[5] = s2b(fullUser.toString());
              tuple[6] = s2b(privilege);
              tuple[7] = null;
              grantRows.add(new ByteArrayRow(tuple, getExceptionInterceptor()));
            } 
          } 
        } 
      } finally {
        if (results != null) {
          try {
            results.close();
          } catch (Exception exception) {}
          results = null;
        } 
        if (pStmt != null) {
          try {
            pStmt.close();
          } catch (Exception exception) {}
          pStmt = null;
        } 
      } 
      return (ResultSet)this.resultSetFactory.createFromResultsetRows(1007, 1004, (ResultsetRows)new ResultsetRowsStatic(grantRows, (ColumnDefinition)new DefaultColumnDefinition(
              getColumnPrivilegesFields())));
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public ResultSet getColumns(String catalog, final String schemaPattern, final String tableNamePattern, String columnNamePattern) throws SQLException {
    try {
      String db = getDatabase(catalog, schemaPattern);
      final String colPattern = columnNamePattern;
      Field[] fields = createColumnsFields();
      final ArrayList<Row> rows = new ArrayList<>();
      final Statement stmt = this.conn.getMetadataSafeStatement();
      final boolean dbMapsToSchema = (this.databaseTerm.getValue() == PropertyDefinitions.DatabaseTerm.SCHEMA);
      try {
        (new IterateBlock<String>(dbMapsToSchema ? getSchemaPatternIterator(db) : getDatabaseIterator(db)) {
            void forEach(String dbStr) throws SQLException {
              ArrayList<String> tableNameList = new ArrayList<>();
              ResultSet tables = null;
              try {
                tables = dbMapsToSchema ? DatabaseMetaData.this.getTables(null, dbStr, tableNamePattern, new String[0]) : DatabaseMetaData.this.getTables(dbStr, schemaPattern, tableNamePattern, new String[0]);
                while (tables.next()) {
                  String tableNameFromList = tables.getString("TABLE_NAME");
                  tableNameList.add(tableNameFromList);
                } 
              } finally {
                if (tables != null) {
                  try {
                    tables.close();
                  } catch (Exception sqlEx) {
                    AssertionFailedException.shouldNotHappen(sqlEx);
                  } 
                  tables = null;
                } 
              } 
              for (String tableName : tableNameList) {
                ResultSet results = null;
                try {
                  StringBuilder queryBuf = new StringBuilder("SHOW FULL COLUMNS FROM ");
                  queryBuf.append(StringUtils.quoteIdentifier(tableName, DatabaseMetaData.this.quotedId, DatabaseMetaData.this.pedantic));
                  queryBuf.append(" FROM ");
                  queryBuf.append(StringUtils.quoteIdentifier(dbStr, DatabaseMetaData.this.quotedId, DatabaseMetaData.this.pedantic));
                  if (colPattern != null) {
                    queryBuf.append(" LIKE ");
                    queryBuf.append(StringUtils.quoteIdentifier(colPattern, "'", true));
                  } 
                  boolean fixUpOrdinalsRequired = false;
                  Map<String, Integer> ordinalFixUpMap = null;
                  if (colPattern != null && !colPattern.equals("%")) {
                    fixUpOrdinalsRequired = true;
                    StringBuilder fullColumnQueryBuf = new StringBuilder("SHOW FULL COLUMNS FROM ");
                    fullColumnQueryBuf
                      .append(StringUtils.quoteIdentifier(tableName, DatabaseMetaData.this.quotedId, DatabaseMetaData.this.pedantic));
                    fullColumnQueryBuf.append(" FROM ");
                    fullColumnQueryBuf.append(StringUtils.quoteIdentifier(dbStr, DatabaseMetaData.this.quotedId, DatabaseMetaData.this.pedantic));
                    results = stmt.executeQuery(fullColumnQueryBuf.toString());
                    ordinalFixUpMap = new HashMap<>();
                    int fullOrdinalPos = 1;
                    while (results.next()) {
                      String fullOrdColName = results.getString("Field");
                      ordinalFixUpMap.put(fullOrdColName, Integer.valueOf(fullOrdinalPos++));
                    } 
                    results.close();
                  } 
                  results = stmt.executeQuery(queryBuf.toString());
                  int ordPos = 1;
                  while (results.next()) {
                    DatabaseMetaData.TypeDescriptor typeDesc = new DatabaseMetaData.TypeDescriptor(results.getString("Type"), results.getString("Null"));
                    byte[][] rowVal = new byte[24][];
                    rowVal[0] = (DatabaseMetaData.this.databaseTerm.getValue() == PropertyDefinitions.DatabaseTerm.SCHEMA) ? DatabaseMetaData.this.s2b("def") : DatabaseMetaData.this.s2b(dbStr);
                    rowVal[1] = (DatabaseMetaData.this.databaseTerm.getValue() == PropertyDefinitions.DatabaseTerm.SCHEMA) ? DatabaseMetaData.this.s2b(dbStr) : null;
                    rowVal[2] = DatabaseMetaData.this.s2b(tableName);
                    rowVal[3] = results.getBytes("Field");
                    rowVal[4] = Short.toString((typeDesc.mysqlType == MysqlType.YEAR && !DatabaseMetaData.this.yearIsDateType) ? 5 : 
                        (short)typeDesc.mysqlType.getJdbcType()).getBytes();
                    rowVal[5] = DatabaseMetaData.this.s2b(typeDesc.mysqlType.getName());
                    if (typeDesc.columnSize == null) {
                      rowVal[6] = null;
                    } else {
                      String collation = results.getString("Collation");
                      int mbminlen = 1;
                      if (collation != null)
                        if (collation.indexOf("ucs2") > -1 || collation.indexOf("utf16") > -1) {
                          mbminlen = 2;
                        } else if (collation.indexOf("utf32") > -1) {
                          mbminlen = 4;
                        }  
                      rowVal[6] = (mbminlen == 1) ? DatabaseMetaData.this.s2b(typeDesc.columnSize.toString()) : DatabaseMetaData.this
                        .s2b(Integer.valueOf(typeDesc.columnSize.intValue() / mbminlen).toString());
                    } 
                    rowVal[7] = DatabaseMetaData.this.s2b(Integer.toString(typeDesc.bufferLength));
                    rowVal[8] = (typeDesc.decimalDigits == null) ? null : DatabaseMetaData.this.s2b(typeDesc.decimalDigits.toString());
                    rowVal[9] = DatabaseMetaData.this.s2b(Integer.toString(typeDesc.numPrecRadix));
                    rowVal[10] = DatabaseMetaData.this.s2b(Integer.toString(typeDesc.nullability));
                    try {
                      rowVal[11] = results.getBytes("Comment");
                    } catch (Exception E) {
                      rowVal[11] = new byte[0];
                    } 
                    rowVal[12] = results.getBytes("Default");
                    (new byte[1])[0] = 48;
                    rowVal[13] = new byte[1];
                    (new byte[1])[0] = 48;
                    rowVal[14] = new byte[1];
                    if (StringUtils.indexOfIgnoreCase(typeDesc.mysqlType.getName(), "CHAR") != -1 || 
                      StringUtils.indexOfIgnoreCase(typeDesc.mysqlType.getName(), "BLOB") != -1 || 
                      StringUtils.indexOfIgnoreCase(typeDesc.mysqlType.getName(), "TEXT") != -1 || 
                      StringUtils.indexOfIgnoreCase(typeDesc.mysqlType.getName(), "ENUM") != -1 || 
                      StringUtils.indexOfIgnoreCase(typeDesc.mysqlType.getName(), "SET") != -1 || 
                      StringUtils.indexOfIgnoreCase(typeDesc.mysqlType.getName(), "BINARY") != -1) {
                      rowVal[15] = rowVal[6];
                    } else {
                      rowVal[15] = null;
                    } 
                    if (!fixUpOrdinalsRequired) {
                      rowVal[16] = Integer.toString(ordPos++).getBytes();
                    } else {
                      String origColName = results.getString("Field");
                      Integer realOrdinal = ordinalFixUpMap.get(origColName);
                      if (realOrdinal != null) {
                        rowVal[16] = realOrdinal.toString().getBytes();
                      } else {
                        throw SQLError.createSQLException(Messages.getString("DatabaseMetaData.10"), "S1000", DatabaseMetaData.this
                            .getExceptionInterceptor());
                      } 
                    } 
                    rowVal[17] = DatabaseMetaData.this.s2b(typeDesc.isNullable);
                    rowVal[18] = null;
                    rowVal[19] = null;
                    rowVal[20] = null;
                    rowVal[21] = null;
                    rowVal[22] = DatabaseMetaData.this.s2b("");
                    String extra = results.getString("Extra");
                    if (extra != null) {
                      rowVal[22] = DatabaseMetaData.this.s2b((StringUtils.indexOfIgnoreCase(extra, "auto_increment") != -1) ? "YES" : "NO");
                      rowVal[23] = DatabaseMetaData.this.s2b((StringUtils.indexOfIgnoreCase(extra, "generated") != -1) ? "YES" : "NO");
                    } 
                    rows.add(new ByteArrayRow(rowVal, DatabaseMetaData.this.getExceptionInterceptor()));
                  } 
                } finally {
                  if (results != null) {
                    try {
                      results.close();
                    } catch (Exception exception) {}
                    results = null;
                  } 
                } 
              } 
            }
          }).doForAll();
      } finally {
        if (stmt != null)
          stmt.close(); 
      } 
      return (ResultSet)this.resultSetFactory.createFromResultsetRows(1007, 1004, (ResultsetRows)new ResultsetRowsStatic(rows, (ColumnDefinition)new DefaultColumnDefinition(fields)));
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  protected Field[] createColumnsFields() {
    Field[] fields = new Field[24];
    fields[0] = new Field("", "TABLE_CAT", this.metadataCollationIndex, this.metadataEncoding, MysqlType.CHAR, 255);
    fields[1] = new Field("", "TABLE_SCHEM", this.metadataCollationIndex, this.metadataEncoding, MysqlType.CHAR, 0);
    fields[2] = new Field("", "TABLE_NAME", this.metadataCollationIndex, this.metadataEncoding, MysqlType.CHAR, 255);
    fields[3] = new Field("", "COLUMN_NAME", this.metadataCollationIndex, this.metadataEncoding, MysqlType.CHAR, 32);
    fields[4] = new Field("", "DATA_TYPE", this.metadataCollationIndex, this.metadataEncoding, MysqlType.INT, 5);
    fields[5] = new Field("", "TYPE_NAME", this.metadataCollationIndex, this.metadataEncoding, MysqlType.CHAR, 16);
    fields[6] = new Field("", "COLUMN_SIZE", this.metadataCollationIndex, this.metadataEncoding, MysqlType.INT, 
        Integer.toString(2147483647).length());
    fields[7] = new Field("", "BUFFER_LENGTH", this.metadataCollationIndex, this.metadataEncoding, MysqlType.INT, 10);
    fields[8] = new Field("", "DECIMAL_DIGITS", this.metadataCollationIndex, this.metadataEncoding, MysqlType.INT, 10);
    fields[9] = new Field("", "NUM_PREC_RADIX", this.metadataCollationIndex, this.metadataEncoding, MysqlType.INT, 10);
    fields[10] = new Field("", "NULLABLE", this.metadataCollationIndex, this.metadataEncoding, MysqlType.INT, 10);
    fields[11] = new Field("", "REMARKS", this.metadataCollationIndex, this.metadataEncoding, MysqlType.CHAR, 0);
    fields[12] = new Field("", "COLUMN_DEF", this.metadataCollationIndex, this.metadataEncoding, MysqlType.CHAR, 0);
    fields[13] = new Field("", "SQL_DATA_TYPE", this.metadataCollationIndex, this.metadataEncoding, MysqlType.INT, 10);
    fields[14] = new Field("", "SQL_DATETIME_SUB", this.metadataCollationIndex, this.metadataEncoding, MysqlType.INT, 10);
    fields[15] = new Field("", "CHAR_OCTET_LENGTH", this.metadataCollationIndex, this.metadataEncoding, MysqlType.INT, 
        Integer.toString(2147483647).length());
    fields[16] = new Field("", "ORDINAL_POSITION", this.metadataCollationIndex, this.metadataEncoding, MysqlType.INT, 10);
    fields[17] = new Field("", "IS_NULLABLE", this.metadataCollationIndex, this.metadataEncoding, MysqlType.CHAR, 3);
    fields[18] = new Field("", "SCOPE_CATALOG", this.metadataCollationIndex, this.metadataEncoding, MysqlType.CHAR, 255);
    fields[19] = new Field("", "SCOPE_SCHEMA", this.metadataCollationIndex, this.metadataEncoding, MysqlType.CHAR, 255);
    fields[20] = new Field("", "SCOPE_TABLE", this.metadataCollationIndex, this.metadataEncoding, MysqlType.CHAR, 255);
    fields[21] = new Field("", "SOURCE_DATA_TYPE", this.metadataCollationIndex, this.metadataEncoding, MysqlType.SMALLINT, 10);
    fields[22] = new Field("", "IS_AUTOINCREMENT", this.metadataCollationIndex, this.metadataEncoding, MysqlType.CHAR, 3);
    fields[23] = new Field("", "IS_GENERATEDCOLUMN", this.metadataCollationIndex, this.metadataEncoding, MysqlType.CHAR, 3);
    return fields;
  }
  
  public Connection getConnection() throws SQLException {
    try {
      return this.conn;
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public ResultSet getCrossReference(String primaryCatalog, String primarySchema, final String primaryTable, String foreignCatalog, String foreignSchema, final String foreignTable) throws SQLException {
    try {
      if (primaryTable == null)
        throw SQLError.createSQLException(Messages.getString("DatabaseMetaData.2"), "S1009", 
            getExceptionInterceptor()); 
      String foreignDb = getDatabase(foreignCatalog, foreignSchema);
      Field[] fields = createFkMetadataFields();
      final ArrayList<Row> tuples = new ArrayList<>();
      Statement stmt = this.conn.getMetadataSafeStatement();
      final boolean dbMapsToSchema = (this.databaseTerm.getValue() == PropertyDefinitions.DatabaseTerm.SCHEMA);
      try {
        (new IterateBlock<String>(getDatabaseIterator(foreignDb)) {
            void forEach(String dbStr) throws SQLException {
              ResultSet fkresults = null;
              try {
                fkresults = DatabaseMetaData.this.extractForeignKeyFromCreateTable(dbStr, null);
                String foreignTableWithCase = DatabaseMetaData.this.getTableNameWithCase(foreignTable);
                String primaryTableWithCase = DatabaseMetaData.this.getTableNameWithCase(primaryTable);
                while (fkresults.next()) {
                  String tableType = fkresults.getString("Type");
                  if (tableType != null && (tableType.equalsIgnoreCase("innodb") || tableType.equalsIgnoreCase("SUPPORTS_FK"))) {
                    String comment = fkresults.getString("Comment").trim();
                    if (comment != null) {
                      StringTokenizer commentTokens = new StringTokenizer(comment, ";", false);
                      if (commentTokens.hasMoreTokens())
                        String str = commentTokens.nextToken(); 
                      while (commentTokens.hasMoreTokens()) {
                        String keys = commentTokens.nextToken();
                        DatabaseMetaData.LocalAndReferencedColumns parsedInfo = DatabaseMetaData.this.parseTableStatusIntoLocalAndReferencedColumns(keys);
                        int keySeq = 1;
                        Iterator<String> referencingColumns = parsedInfo.localColumnsList.iterator();
                        Iterator<String> referencedColumns = parsedInfo.referencedColumnsList.iterator();
                        while (referencingColumns.hasNext()) {
                          String referencingColumn = StringUtils.unQuoteIdentifier(referencingColumns.next(), DatabaseMetaData.this.quotedId);
                          String dummy = fkresults.getString("Name");
                          if (dummy.compareTo(foreignTableWithCase) != 0)
                            continue; 
                          if (parsedInfo.referencedTable.compareTo(primaryTableWithCase) != 0)
                            continue; 
                          byte[][] tuple = new byte[14][];
                          tuple[0] = dbMapsToSchema ? DatabaseMetaData.this.s2b("def") : DatabaseMetaData.this.s2b(parsedInfo.referencedDatabase);
                          tuple[1] = dbMapsToSchema ? DatabaseMetaData.this.s2b(parsedInfo.referencedDatabase) : null;
                          tuple[2] = DatabaseMetaData.this.s2b(parsedInfo.referencedTable);
                          tuple[3] = DatabaseMetaData.this.s2b(StringUtils.unQuoteIdentifier(referencedColumns.next(), DatabaseMetaData.this.quotedId));
                          tuple[4] = dbMapsToSchema ? DatabaseMetaData.this.s2b("def") : DatabaseMetaData.this.s2b(dbStr);
                          tuple[5] = dbMapsToSchema ? DatabaseMetaData.this.s2b(dbStr) : null;
                          tuple[6] = DatabaseMetaData.this.s2b(dummy);
                          tuple[7] = DatabaseMetaData.this.s2b(referencingColumn);
                          tuple[8] = Integer.toString(keySeq).getBytes();
                          int[] actions = DatabaseMetaData.this.getForeignKeyActions(keys);
                          tuple[9] = Integer.toString(actions[1]).getBytes();
                          tuple[10] = Integer.toString(actions[0]).getBytes();
                          tuple[11] = DatabaseMetaData.this.s2b(parsedInfo.constraintName);
                          tuple[12] = null;
                          tuple[13] = Integer.toString(7).getBytes();
                          tuples.add(new ByteArrayRow(tuple, DatabaseMetaData.this.getExceptionInterceptor()));
                          keySeq++;
                        } 
                      } 
                    } 
                  } 
                } 
              } finally {
                if (fkresults != null) {
                  try {
                    fkresults.close();
                  } catch (Exception sqlEx) {
                    AssertionFailedException.shouldNotHappen(sqlEx);
                  } 
                  fkresults = null;
                } 
              } 
            }
          }).doForAll();
      } finally {
        if (stmt != null)
          stmt.close(); 
      } 
      return (ResultSet)this.resultSetFactory.createFromResultsetRows(1007, 1004, (ResultsetRows)new ResultsetRowsStatic(tuples, (ColumnDefinition)new DefaultColumnDefinition(fields)));
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  protected Field[] createFkMetadataFields() {
    Field[] fields = new Field[14];
    fields[0] = new Field("", "PKTABLE_CAT", this.metadataCollationIndex, this.metadataEncoding, MysqlType.CHAR, 255);
    fields[1] = new Field("", "PKTABLE_SCHEM", this.metadataCollationIndex, this.metadataEncoding, MysqlType.CHAR, 0);
    fields[2] = new Field("", "PKTABLE_NAME", this.metadataCollationIndex, this.metadataEncoding, MysqlType.CHAR, 255);
    fields[3] = new Field("", "PKCOLUMN_NAME", this.metadataCollationIndex, this.metadataEncoding, MysqlType.CHAR, 32);
    fields[4] = new Field("", "FKTABLE_CAT", this.metadataCollationIndex, this.metadataEncoding, MysqlType.CHAR, 255);
    fields[5] = new Field("", "FKTABLE_SCHEM", this.metadataCollationIndex, this.metadataEncoding, MysqlType.CHAR, 0);
    fields[6] = new Field("", "FKTABLE_NAME", this.metadataCollationIndex, this.metadataEncoding, MysqlType.CHAR, 255);
    fields[7] = new Field("", "FKCOLUMN_NAME", this.metadataCollationIndex, this.metadataEncoding, MysqlType.CHAR, 32);
    fields[8] = new Field("", "KEY_SEQ", this.metadataCollationIndex, this.metadataEncoding, MysqlType.SMALLINT, 2);
    fields[9] = new Field("", "UPDATE_RULE", this.metadataCollationIndex, this.metadataEncoding, MysqlType.SMALLINT, 2);
    fields[10] = new Field("", "DELETE_RULE", this.metadataCollationIndex, this.metadataEncoding, MysqlType.SMALLINT, 2);
    fields[11] = new Field("", "FK_NAME", this.metadataCollationIndex, this.metadataEncoding, MysqlType.CHAR, 0);
    fields[12] = new Field("", "PK_NAME", this.metadataCollationIndex, this.metadataEncoding, MysqlType.CHAR, 0);
    fields[13] = new Field("", "DEFERRABILITY", this.metadataCollationIndex, this.metadataEncoding, MysqlType.SMALLINT, 2);
    return fields;
  }
  
  public int getDatabaseMajorVersion() throws SQLException {
    try {
      return this.conn.getServerVersion().getMajor();
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public int getDatabaseMinorVersion() throws SQLException {
    try {
      return this.conn.getServerVersion().getMinor();
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public String getDatabaseProductName() throws SQLException {
    try {
      return "MySQL";
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public String getDatabaseProductVersion() throws SQLException {
    try {
      return this.conn.getServerVersion().toString();
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public int getDefaultTransactionIsolation() throws SQLException {
    try {
      return 4;
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public int getDriverMajorVersion() {
    return NonRegisteringDriver.getMajorVersionInternal();
  }
  
  public int getDriverMinorVersion() {
    return NonRegisteringDriver.getMinorVersionInternal();
  }
  
  public String getDriverName() throws SQLException {
    try {
      return "MySQL Connector/J";
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public String getDriverVersion() throws SQLException {
    try {
      return "mysql-connector-j-8.0.33 (Revision: 7d6b0800528b6b25c68b52dc10d6c1c8429c100c)";
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public ResultSet getExportedKeys(String catalog, String schema, final String table) throws SQLException {
    try {
      if (table == null)
        throw SQLError.createSQLException(Messages.getString("DatabaseMetaData.2"), "S1009", 
            getExceptionInterceptor()); 
      Field[] fields = createFkMetadataFields();
      final ArrayList<Row> rows = new ArrayList<>();
      Statement stmt = this.conn.getMetadataSafeStatement();
      String db = getDatabase(catalog, schema);
      try {
        (new IterateBlock<String>(getDatabaseIterator(db)) {
            void forEach(String dbStr) throws SQLException {
              ResultSet fkresults = null;
              try {
                fkresults = DatabaseMetaData.this.extractForeignKeyFromCreateTable(dbStr, null);
                String tableNameWithCase = DatabaseMetaData.this.getTableNameWithCase(table);
                while (fkresults.next()) {
                  String tableType = fkresults.getString("Type");
                  if (tableType != null && (tableType.equalsIgnoreCase("innodb") || tableType.equalsIgnoreCase("SUPPORTS_FK"))) {
                    String comment = fkresults.getString("Comment").trim();
                    if (comment != null) {
                      StringTokenizer commentTokens = new StringTokenizer(comment, ";", false);
                      if (commentTokens.hasMoreTokens()) {
                        commentTokens.nextToken();
                        while (commentTokens.hasMoreTokens()) {
                          String keysComment = commentTokens.nextToken();
                          DatabaseMetaData.this.populateKeyResults(dbStr, tableNameWithCase, keysComment, rows, fkresults.getString("Name"), true);
                        } 
                      } 
                    } 
                  } 
                } 
              } finally {
                if (fkresults != null) {
                  try {
                    fkresults.close();
                  } catch (SQLException sqlEx) {
                    AssertionFailedException.shouldNotHappen(sqlEx);
                  } 
                  fkresults = null;
                } 
              } 
            }
          }).doForAll();
      } finally {
        if (stmt != null)
          stmt.close(); 
      } 
      return (ResultSet)this.resultSetFactory.createFromResultsetRows(1007, 1004, (ResultsetRows)new ResultsetRowsStatic(rows, (ColumnDefinition)new DefaultColumnDefinition(fields)));
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public String getExtraNameCharacters() throws SQLException {
    try {
      return "#@";
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  protected int[] getForeignKeyActions(String commentString) {
    int[] actions = { 1, 1 };
    int lastParenIndex = commentString.lastIndexOf(")");
    if (lastParenIndex != commentString.length() - 1) {
      String cascadeOptions = commentString.substring(lastParenIndex + 1).trim().toUpperCase(Locale.ENGLISH);
      actions[0] = getCascadeDeleteOption(cascadeOptions);
      actions[1] = getCascadeUpdateOption(cascadeOptions);
    } 
    return actions;
  }
  
  public String getIdentifierQuoteString() throws SQLException {
    try {
      return this.session.getIdentifierQuoteString();
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public ResultSet getImportedKeys(String catalog, String schema, final String table) throws SQLException {
    try {
      if (table == null)
        throw SQLError.createSQLException(Messages.getString("DatabaseMetaData.2"), "S1009", 
            getExceptionInterceptor()); 
      Field[] fields = createFkMetadataFields();
      final ArrayList<Row> rows = new ArrayList<>();
      Statement stmt = this.conn.getMetadataSafeStatement();
      String db = getDatabase(catalog, schema);
      try {
        (new IterateBlock<String>(getDatabaseIterator(db)) {
            void forEach(String dbStr) throws SQLException {
              ResultSet fkresults = null;
              try {
                fkresults = DatabaseMetaData.this.extractForeignKeyFromCreateTable(dbStr, table);
                while (fkresults.next()) {
                  String tableType = fkresults.getString("Type");
                  if (tableType != null && (tableType.equalsIgnoreCase("innodb") || tableType.equalsIgnoreCase("SUPPORTS_FK"))) {
                    String comment = fkresults.getString("Comment").trim();
                    if (comment != null) {
                      StringTokenizer commentTokens = new StringTokenizer(comment, ";", false);
                      if (commentTokens.hasMoreTokens()) {
                        commentTokens.nextToken();
                        while (commentTokens.hasMoreTokens()) {
                          String keysComment = commentTokens.nextToken();
                          DatabaseMetaData.this.populateKeyResults(dbStr, table, keysComment, rows, null, false);
                        } 
                      } 
                    } 
                  } 
                } 
              } finally {
                if (fkresults != null) {
                  try {
                    fkresults.close();
                  } catch (SQLException sqlEx) {
                    AssertionFailedException.shouldNotHappen(sqlEx);
                  } 
                  fkresults = null;
                } 
              } 
            }
          }).doForAll();
      } finally {
        if (stmt != null)
          stmt.close(); 
      } 
      return (ResultSet)this.resultSetFactory.createFromResultsetRows(1007, 1004, (ResultsetRows)new ResultsetRowsStatic(rows, (ColumnDefinition)new DefaultColumnDefinition(fields)));
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public ResultSet getIndexInfo(String catalog, String schema, final String table, final boolean unique, boolean approximate) throws SQLException {
    try {
      Field[] fields = createIndexInfoFields();
      final SortedMap<IndexMetaDataKey, Row> sortedRows = new TreeMap<>();
      ArrayList<Row> rows = new ArrayList<>();
      final Statement stmt = this.conn.getMetadataSafeStatement();
      String db = getDatabase(catalog, schema);
      final boolean dbMapsToSchema = (this.databaseTerm.getValue() == PropertyDefinitions.DatabaseTerm.SCHEMA);
      try {
        (new IterateBlock<String>(getDatabaseIterator(db)) {
            void forEach(String dbStr) throws SQLException {
              ResultSet results = null;
              try {
                StringBuilder queryBuf = new StringBuilder("SHOW INDEX FROM ");
                queryBuf.append(StringUtils.quoteIdentifier(table, DatabaseMetaData.this.quotedId, DatabaseMetaData.this.pedantic));
                queryBuf.append(" FROM ");
                queryBuf.append(StringUtils.quoteIdentifier(dbStr, DatabaseMetaData.this.quotedId, DatabaseMetaData.this.pedantic));
                try {
                  results = stmt.executeQuery(queryBuf.toString());
                } catch (SQLException sqlEx) {
                  String sqlState = sqlEx.getSQLState();
                  int errorCode = sqlEx.getErrorCode();
                  if (!"42S02".equals(sqlState) && errorCode != 1146 && errorCode != 1049)
                    throw sqlEx; 
                } 
                while (results != null && results.next()) {
                  byte[][] row = new byte[14][];
                  row[0] = dbMapsToSchema ? DatabaseMetaData.this.s2b("def") : DatabaseMetaData.this.s2b(dbStr);
                  row[1] = dbMapsToSchema ? DatabaseMetaData.this.s2b(dbStr) : null;
                  row[2] = results.getBytes("Table");
                  boolean indexIsUnique = (results.getInt("Non_unique") == 0);
                  row[3] = !indexIsUnique ? DatabaseMetaData.this.s2b("true") : DatabaseMetaData.this.s2b("false");
                  row[4] = null;
                  row[5] = results.getBytes("Key_name");
                  short indexType = 3;
                  row[6] = Integer.toString(indexType).getBytes();
                  row[7] = results.getBytes("Seq_in_index");
                  row[8] = results.getBytes("Column_name");
                  row[9] = results.getBytes("Collation");
                  long cardinality = results.getLong("Cardinality");
                  row[10] = DatabaseMetaData.this.s2b(String.valueOf(cardinality));
                  row[11] = DatabaseMetaData.this.s2b("0");
                  row[12] = null;
                  DatabaseMetaData.IndexMetaDataKey indexInfoKey = new DatabaseMetaData.IndexMetaDataKey(!indexIsUnique, indexType, results.getString("Key_name").toLowerCase(), results.getShort("Seq_in_index"));
                  if (unique) {
                    if (indexIsUnique)
                      sortedRows.put(indexInfoKey, new ByteArrayRow(row, DatabaseMetaData.this.getExceptionInterceptor())); 
                    continue;
                  } 
                  sortedRows.put(indexInfoKey, new ByteArrayRow(row, DatabaseMetaData.this.getExceptionInterceptor()));
                } 
              } finally {
                if (results != null) {
                  try {
                    results.close();
                  } catch (Exception exception) {}
                  results = null;
                } 
              } 
            }
          }).doForAll();
        Iterator<Row> sortedRowsIterator = sortedRows.values().iterator();
        while (sortedRowsIterator.hasNext())
          rows.add(sortedRowsIterator.next()); 
        ResultSetImpl resultSetImpl = this.resultSetFactory.createFromResultsetRows(1007, 1004, (ResultsetRows)new ResultsetRowsStatic(rows, (ColumnDefinition)new DefaultColumnDefinition(fields)));
        return (ResultSet)resultSetImpl;
      } finally {
        if (stmt != null)
          stmt.close(); 
      } 
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  protected Field[] createIndexInfoFields() {
    Field[] fields = new Field[13];
    fields[0] = new Field("", "TABLE_CAT", this.metadataCollationIndex, this.metadataEncoding, MysqlType.CHAR, 255);
    fields[1] = new Field("", "TABLE_SCHEM", this.metadataCollationIndex, this.metadataEncoding, MysqlType.CHAR, 0);
    fields[2] = new Field("", "TABLE_NAME", this.metadataCollationIndex, this.metadataEncoding, MysqlType.CHAR, 255);
    fields[3] = new Field("", "NON_UNIQUE", this.metadataCollationIndex, this.metadataEncoding, MysqlType.BOOLEAN, 4);
    fields[4] = new Field("", "INDEX_QUALIFIER", this.metadataCollationIndex, this.metadataEncoding, MysqlType.CHAR, 1);
    fields[5] = new Field("", "INDEX_NAME", this.metadataCollationIndex, this.metadataEncoding, MysqlType.CHAR, 32);
    fields[6] = new Field("", "TYPE", this.metadataCollationIndex, this.metadataEncoding, MysqlType.SMALLINT, 32);
    fields[7] = new Field("", "ORDINAL_POSITION", this.metadataCollationIndex, this.metadataEncoding, MysqlType.SMALLINT, 5);
    fields[8] = new Field("", "COLUMN_NAME", this.metadataCollationIndex, this.metadataEncoding, MysqlType.CHAR, 32);
    fields[9] = new Field("", "ASC_OR_DESC", this.metadataCollationIndex, this.metadataEncoding, MysqlType.CHAR, 1);
    fields[10] = new Field("", "CARDINALITY", this.metadataCollationIndex, this.metadataEncoding, MysqlType.BIGINT, 20);
    fields[11] = new Field("", "PAGES", this.metadataCollationIndex, this.metadataEncoding, MysqlType.BIGINT, 20);
    fields[12] = new Field("", "FILTER_CONDITION", this.metadataCollationIndex, this.metadataEncoding, MysqlType.CHAR, 32);
    return fields;
  }
  
  public int getJDBCMajorVersion() throws SQLException {
    try {
      return 4;
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public int getJDBCMinorVersion() throws SQLException {
    try {
      return 2;
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public int getMaxBinaryLiteralLength() throws SQLException {
    try {
      return 16777208;
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public int getMaxCatalogNameLength() throws SQLException {
    try {
      return 32;
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public int getMaxCharLiteralLength() throws SQLException {
    try {
      return 16777208;
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public int getMaxColumnNameLength() throws SQLException {
    try {
      return 64;
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public int getMaxColumnsInGroupBy() throws SQLException {
    try {
      return 64;
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public int getMaxColumnsInIndex() throws SQLException {
    try {
      return 16;
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public int getMaxColumnsInOrderBy() throws SQLException {
    try {
      return 64;
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public int getMaxColumnsInSelect() throws SQLException {
    try {
      return 256;
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public int getMaxColumnsInTable() throws SQLException {
    try {
      return 512;
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public int getMaxConnections() throws SQLException {
    try {
      return 0;
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public int getMaxCursorNameLength() throws SQLException {
    try {
      return 64;
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public int getMaxIndexLength() throws SQLException {
    try {
      return 256;
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public int getMaxProcedureNameLength() throws SQLException {
    try {
      return 0;
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public int getMaxRowSize() throws SQLException {
    try {
      return 2147483639;
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public int getMaxSchemaNameLength() throws SQLException {
    try {
      return 0;
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public int getMaxStatementLength() throws SQLException {
    try {
      return maxBufferSize - 4;
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public int getMaxStatements() throws SQLException {
    try {
      return 0;
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public int getMaxTableNameLength() throws SQLException {
    try {
      return 64;
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public int getMaxTablesInSelect() throws SQLException {
    try {
      return 256;
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public int getMaxUserNameLength() throws SQLException {
    try {
      return 16;
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public String getNumericFunctions() throws SQLException {
    try {
      return "ABS,ACOS,ASIN,ATAN,ATAN2,BIT_COUNT,CEILING,COS,COT,DEGREES,EXP,FLOOR,LOG,LOG10,MAX,MIN,MOD,PI,POW,POWER,RADIANS,RAND,ROUND,SIN,SQRT,TAN,TRUNCATE";
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  protected Field[] getPrimaryKeysFields() {
    Field[] fields = new Field[6];
    fields[0] = new Field("", "TABLE_CAT", this.metadataCollationIndex, this.metadataEncoding, MysqlType.CHAR, 255);
    fields[1] = new Field("", "TABLE_SCHEM", this.metadataCollationIndex, this.metadataEncoding, MysqlType.CHAR, 0);
    fields[2] = new Field("", "TABLE_NAME", this.metadataCollationIndex, this.metadataEncoding, MysqlType.CHAR, 255);
    fields[3] = new Field("", "COLUMN_NAME", this.metadataCollationIndex, this.metadataEncoding, MysqlType.CHAR, 32);
    fields[4] = new Field("", "KEY_SEQ", this.metadataCollationIndex, this.metadataEncoding, MysqlType.SMALLINT, 5);
    fields[5] = new Field("", "PK_NAME", this.metadataCollationIndex, this.metadataEncoding, MysqlType.CHAR, 32);
    return fields;
  }
  
  public ResultSet getPrimaryKeys(String catalog, String schema, final String table) throws SQLException {
    try {
      if (table == null)
        throw SQLError.createSQLException(Messages.getString("DatabaseMetaData.2"), "S1009", 
            getExceptionInterceptor()); 
      String db = getDatabase(catalog, schema);
      final boolean dbMapsToSchema = (this.databaseTerm.getValue() == PropertyDefinitions.DatabaseTerm.SCHEMA);
      final ArrayList<Row> rows = new ArrayList<>();
      final Statement stmt = this.conn.getMetadataSafeStatement();
      try {
        (new IterateBlock<String>(getDatabaseIterator(db)) {
            void forEach(String dbStr) throws SQLException {
              ResultSet rs = null;
              try {
                StringBuilder queryBuf = new StringBuilder("SHOW KEYS FROM ");
                queryBuf.append(StringUtils.quoteIdentifier(table, DatabaseMetaData.this.quotedId, DatabaseMetaData.this.pedantic));
                queryBuf.append(" FROM ");
                queryBuf.append(StringUtils.quoteIdentifier(dbStr, DatabaseMetaData.this.quotedId, DatabaseMetaData.this.pedantic));
                try {
                  rs = stmt.executeQuery(queryBuf.toString());
                } catch (SQLException sqlEx) {
                  String sqlState = sqlEx.getSQLState();
                  int errorCode = sqlEx.getErrorCode();
                  if (!"42S02".equals(sqlState) && errorCode != 1146 && errorCode != 1049)
                    throw sqlEx; 
                } 
                TreeMap<String, byte[][]> sortMap = (TreeMap)new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
                while (rs != null && rs.next()) {
                  String keyType = rs.getString("Key_name");
                  if (keyType != null && (
                    keyType.equalsIgnoreCase("PRIMARY") || keyType.equalsIgnoreCase("PRI"))) {
                    byte[][] tuple = new byte[6][];
                    tuple[0] = dbMapsToSchema ? DatabaseMetaData.this.s2b("def") : DatabaseMetaData.this.s2b(dbStr);
                    tuple[1] = dbMapsToSchema ? DatabaseMetaData.this.s2b(dbStr) : null;
                    tuple[2] = DatabaseMetaData.this.s2b(table);
                    String columnName = rs.getString("Column_name");
                    tuple[3] = DatabaseMetaData.this.s2b(columnName);
                    tuple[4] = DatabaseMetaData.this.s2b(rs.getString("Seq_in_index"));
                    tuple[5] = DatabaseMetaData.this.s2b(keyType);
                    sortMap.put(columnName, tuple);
                  } 
                } 
                Iterator<byte[][]> sortedIterator = (Iterator)sortMap.values().iterator();
                while (sortedIterator.hasNext())
                  rows.add(new ByteArrayRow(sortedIterator.next(), DatabaseMetaData.this.getExceptionInterceptor())); 
              } finally {
                if (rs != null) {
                  try {
                    rs.close();
                  } catch (Exception exception) {}
                  rs = null;
                } 
              } 
            }
          }).doForAll();
      } finally {
        if (stmt != null)
          stmt.close(); 
      } 
      return (ResultSet)this.resultSetFactory.createFromResultsetRows(1007, 1004, (ResultsetRows)new ResultsetRowsStatic(rows, (ColumnDefinition)new DefaultColumnDefinition(
              getPrimaryKeysFields())));
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public ResultSet getProcedureColumns(String catalog, String schemaPattern, String procedureNamePattern, String columnNamePattern) throws SQLException {
    try {
      return getProcedureOrFunctionColumns(createProcedureColumnsFields(), catalog, schemaPattern, procedureNamePattern, columnNamePattern, true, ((Boolean)this.conn
          .getPropertySet().getBooleanProperty(PropertyKey.getProceduresReturnsFunctions).getValue()).booleanValue());
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  protected Field[] createProcedureColumnsFields() {
    Field[] fields = new Field[20];
    fields[0] = new Field("", "PROCEDURE_CAT", this.metadataCollationIndex, this.metadataEncoding, MysqlType.CHAR, 512);
    fields[1] = new Field("", "PROCEDURE_SCHEM", this.metadataCollationIndex, this.metadataEncoding, MysqlType.CHAR, 512);
    fields[2] = new Field("", "PROCEDURE_NAME", this.metadataCollationIndex, this.metadataEncoding, MysqlType.CHAR, 512);
    fields[3] = new Field("", "COLUMN_NAME", this.metadataCollationIndex, this.metadataEncoding, MysqlType.CHAR, 512);
    fields[4] = new Field("", "COLUMN_TYPE", this.metadataCollationIndex, this.metadataEncoding, MysqlType.CHAR, 64);
    fields[5] = new Field("", "DATA_TYPE", this.metadataCollationIndex, this.metadataEncoding, MysqlType.SMALLINT, 6);
    fields[6] = new Field("", "TYPE_NAME", this.metadataCollationIndex, this.metadataEncoding, MysqlType.CHAR, 64);
    fields[7] = new Field("", "PRECISION", this.metadataCollationIndex, this.metadataEncoding, MysqlType.INT, 12);
    fields[8] = new Field("", "LENGTH", this.metadataCollationIndex, this.metadataEncoding, MysqlType.INT, 12);
    fields[9] = new Field("", "SCALE", this.metadataCollationIndex, this.metadataEncoding, MysqlType.SMALLINT, 12);
    fields[10] = new Field("", "RADIX", this.metadataCollationIndex, this.metadataEncoding, MysqlType.SMALLINT, 6);
    fields[11] = new Field("", "NULLABLE", this.metadataCollationIndex, this.metadataEncoding, MysqlType.SMALLINT, 6);
    fields[12] = new Field("", "REMARKS", this.metadataCollationIndex, this.metadataEncoding, MysqlType.CHAR, 512);
    fields[13] = new Field("", "COLUMN_DEF", this.metadataCollationIndex, this.metadataEncoding, MysqlType.CHAR, 512);
    fields[14] = new Field("", "SQL_DATA_TYPE", this.metadataCollationIndex, this.metadataEncoding, MysqlType.INT, 12);
    fields[15] = new Field("", "SQL_DATETIME_SUB", this.metadataCollationIndex, this.metadataEncoding, MysqlType.INT, 12);
    fields[16] = new Field("", "CHAR_OCTET_LENGTH", this.metadataCollationIndex, this.metadataEncoding, MysqlType.INT, 12);
    fields[17] = new Field("", "ORDINAL_POSITION", this.metadataCollationIndex, this.metadataEncoding, MysqlType.INT, 12);
    fields[18] = new Field("", "IS_NULLABLE", this.metadataCollationIndex, this.metadataEncoding, MysqlType.CHAR, 512);
    fields[19] = new Field("", "SPECIFIC_NAME", this.metadataCollationIndex, this.metadataEncoding, MysqlType.CHAR, 512);
    return fields;
  }
  
  protected ResultSet getProcedureOrFunctionColumns(Field[] fields, String catalog, String schemaPattern, String procedureOrFunctionNamePattern, String columnNamePattern, boolean returnProcedures, boolean returnFunctions) throws SQLException {
    String db = getDatabase(catalog, schemaPattern);
    boolean dbMapsToSchema = (this.databaseTerm.getValue() == PropertyDefinitions.DatabaseTerm.SCHEMA);
    List<ComparableWrapper<String, ProcedureType>> procsOrFuncsToExtractList = new ArrayList<>();
    ResultSet procsAndOrFuncsRs = null;
    try {
      String tmpProcedureOrFunctionNamePattern = null;
      if (procedureOrFunctionNamePattern != null && !procedureOrFunctionNamePattern.equals("%"))
        tmpProcedureOrFunctionNamePattern = StringUtils.sanitizeProcOrFuncName(procedureOrFunctionNamePattern); 
      if (tmpProcedureOrFunctionNamePattern == null) {
        tmpProcedureOrFunctionNamePattern = procedureOrFunctionNamePattern;
      } else {
        String tmpDb = db;
        List<String> parseList = StringUtils.splitDBdotName(tmpProcedureOrFunctionNamePattern, tmpDb, this.quotedId, this.session
            .getServerSession().isNoBackslashEscapesSet());
        if (parseList.size() == 2) {
          tmpDb = parseList.get(0);
          tmpProcedureOrFunctionNamePattern = parseList.get(1);
        } 
      } 
      procsAndOrFuncsRs = getProceduresAndOrFunctions(createFieldMetadataForGetProcedures(), catalog, schemaPattern, tmpProcedureOrFunctionNamePattern, returnProcedures, returnFunctions);
      boolean hasResults = false;
      while (procsAndOrFuncsRs.next()) {
        procsOrFuncsToExtractList.add(new ComparableWrapper<>(
              StringUtils.getFullyQualifiedName(dbMapsToSchema ? procsAndOrFuncsRs.getString(2) : procsAndOrFuncsRs.getString(1), procsAndOrFuncsRs
                .getString(3), this.quotedId, this.pedantic), 
              (procsAndOrFuncsRs.getShort(8) == 1) ? ProcedureType.PROCEDURE : ProcedureType.FUNCTION));
        hasResults = true;
      } 
      if (hasResults)
        Collections.sort(procsOrFuncsToExtractList); 
    } finally {
      SQLException rethrowSqlEx = null;
      if (procsAndOrFuncsRs != null)
        try {
          procsAndOrFuncsRs.close();
        } catch (SQLException sqlEx) {
          rethrowSqlEx = sqlEx;
        }  
      if (rethrowSqlEx != null)
        throw rethrowSqlEx; 
    } 
    ArrayList<Row> resultRows = new ArrayList<>();
    int idx = 0;
    String procNameToCall = "";
    for (ComparableWrapper<String, ProcedureType> procOrFunc : procsOrFuncsToExtractList) {
      String procName = procOrFunc.getKey();
      ProcedureType procType = procOrFunc.getValue();
      if (!" ".equals(this.quotedId)) {
        idx = StringUtils.indexOfIgnoreCase(0, procName, ".", this.quotedId, this.quotedId, 
            this.session.getServerSession().isNoBackslashEscapesSet() ? SearchMode.__MRK_COM_MYM_HNT_WS : SearchMode.__BSE_MRK_COM_MYM_HNT_WS);
      } else {
        idx = procName.indexOf(".");
      } 
      if (idx > 0) {
        db = StringUtils.unQuoteIdentifier(procName.substring(0, idx), this.quotedId);
        procNameToCall = procName;
      } else {
        procNameToCall = procName;
      } 
      getCallStmtParameterTypes(db, procNameToCall, procType, columnNamePattern, resultRows, (fields.length == 17));
    } 
    return (ResultSet)this.resultSetFactory.createFromResultsetRows(1007, 1004, (ResultsetRows)new ResultsetRowsStatic(resultRows, (ColumnDefinition)new DefaultColumnDefinition(fields)));
  }
  
  public ResultSet getProcedures(String catalog, String schemaPattern, String procedureNamePattern) throws SQLException {
    try {
      return getProceduresAndOrFunctions(createFieldMetadataForGetProcedures(), catalog, schemaPattern, procedureNamePattern, true, ((Boolean)this.conn
          .getPropertySet().getBooleanProperty(PropertyKey.getProceduresReturnsFunctions).getValue()).booleanValue());
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  protected Field[] createFieldMetadataForGetProcedures() {
    Field[] fields = new Field[9];
    fields[0] = new Field("", "PROCEDURE_CAT", this.metadataCollationIndex, this.metadataEncoding, MysqlType.CHAR, 255);
    fields[1] = new Field("", "PROCEDURE_SCHEM", this.metadataCollationIndex, this.metadataEncoding, MysqlType.CHAR, 255);
    fields[2] = new Field("", "PROCEDURE_NAME", this.metadataCollationIndex, this.metadataEncoding, MysqlType.CHAR, 255);
    fields[3] = new Field("", "reserved1", this.metadataCollationIndex, this.metadataEncoding, MysqlType.CHAR, 0);
    fields[4] = new Field("", "reserved2", this.metadataCollationIndex, this.metadataEncoding, MysqlType.CHAR, 0);
    fields[5] = new Field("", "reserved3", this.metadataCollationIndex, this.metadataEncoding, MysqlType.CHAR, 0);
    fields[6] = new Field("", "REMARKS", this.metadataCollationIndex, this.metadataEncoding, MysqlType.CHAR, 255);
    fields[7] = new Field("", "PROCEDURE_TYPE", this.metadataCollationIndex, this.metadataEncoding, MysqlType.SMALLINT, 6);
    fields[8] = new Field("", "SPECIFIC_NAME", this.metadataCollationIndex, this.metadataEncoding, MysqlType.CHAR, 255);
    return fields;
  }
  
  protected ResultSet getProceduresAndOrFunctions(final Field[] fields, String catalog, String schemaPattern, final String procedureNamePattern, final boolean returnProcedures, final boolean returnFunctions) throws SQLException {
    ArrayList<Row> procedureRows = new ArrayList<>();
    String db = getDatabase(catalog, schemaPattern);
    final boolean dbMapsToSchema = (this.databaseTerm.getValue() == PropertyDefinitions.DatabaseTerm.SCHEMA);
    final List<ComparableWrapper<String, Row>> procedureRowsToSort = new ArrayList<>();
    (new IterateBlock<String>(dbMapsToSchema ? getSchemaPatternIterator(db) : getDatabaseIterator(db)) {
        void forEach(String dbPattern) throws SQLException {
          ResultSet proceduresRs = null;
          StringBuilder selectFromMySQLProcSQL = new StringBuilder();
          selectFromMySQLProcSQL.append("SELECT db, name, type, comment FROM mysql.proc WHERE");
          if (returnProcedures && !returnFunctions) {
            selectFromMySQLProcSQL.append(" type = 'PROCEDURE' AND ");
          } else if (!returnProcedures && returnFunctions) {
            selectFromMySQLProcSQL.append(" type = 'FUNCTION' AND ");
          } 
          selectFromMySQLProcSQL.append(dbMapsToSchema ? " db LIKE ?" : " db = ?");
          if (procedureNamePattern != null && procedureNamePattern.length() > 0)
            selectFromMySQLProcSQL.append(" AND name LIKE ?"); 
          selectFromMySQLProcSQL.append(" ORDER BY name, type");
          PreparedStatement proceduresStmt = DatabaseMetaData.this.prepareMetaDataSafeStatement(selectFromMySQLProcSQL.toString());
          try {
            if (DatabaseMetaData.this.conn.lowerCaseTableNames())
              dbPattern = dbPattern.toLowerCase(); 
            proceduresStmt.setString(1, dbPattern);
            if (procedureNamePattern != null && procedureNamePattern.length() > 0)
              proceduresStmt.setString(2, procedureNamePattern); 
            try {
              proceduresRs = proceduresStmt.executeQuery();
              if (returnProcedures)
                DatabaseMetaData.this.convertToJdbcProcedureList(true, proceduresRs, procedureRowsToSort); 
              if (returnFunctions)
                DatabaseMetaData.this.convertToJdbcFunctionList(proceduresRs, procedureRowsToSort, fields); 
            } catch (SQLException sqlEx) {
              if (returnFunctions) {
                proceduresStmt.close();
                String sql = "SHOW FUNCTION STATUS WHERE " + (dbMapsToSchema ? "Db LIKE ?" : "Db = ?");
                if (procedureNamePattern != null && procedureNamePattern.length() > 0)
                  sql = sql + " AND Name LIKE ?"; 
                proceduresStmt = DatabaseMetaData.this.prepareMetaDataSafeStatement(sql);
                proceduresStmt.setString(1, dbPattern);
                if (procedureNamePattern != null && procedureNamePattern.length() > 0)
                  proceduresStmt.setString(2, procedureNamePattern); 
                proceduresRs = proceduresStmt.executeQuery();
                DatabaseMetaData.this.convertToJdbcFunctionList(proceduresRs, procedureRowsToSort, fields);
              } 
              if (returnProcedures) {
                proceduresStmt.close();
                String sql = "SHOW PROCEDURE STATUS WHERE " + (dbMapsToSchema ? "Db LIKE ?" : "Db = ?");
                if (procedureNamePattern != null && procedureNamePattern.length() > 0)
                  sql = sql + " AND Name LIKE ?"; 
                proceduresStmt = DatabaseMetaData.this.prepareMetaDataSafeStatement(sql);
                proceduresStmt.setString(1, dbPattern);
                if (procedureNamePattern != null && procedureNamePattern.length() > 0)
                  proceduresStmt.setString(2, procedureNamePattern); 
                proceduresRs = proceduresStmt.executeQuery();
                DatabaseMetaData.this.convertToJdbcProcedureList(false, proceduresRs, procedureRowsToSort);
              } 
            } 
          } finally {
            SQLException rethrowSqlEx = null;
            if (proceduresRs != null)
              try {
                proceduresRs.close();
              } catch (SQLException sqlEx) {
                rethrowSqlEx = sqlEx;
              }  
            if (proceduresStmt != null)
              try {
                proceduresStmt.close();
              } catch (SQLException sqlEx) {
                rethrowSqlEx = sqlEx;
              }  
            if (rethrowSqlEx != null)
              throw rethrowSqlEx; 
          } 
        }
      }).doForAll();
    Collections.sort(procedureRowsToSort);
    for (ComparableWrapper<String, Row> procRow : procedureRowsToSort)
      procedureRows.add(procRow.getValue()); 
    return (ResultSet)this.resultSetFactory.createFromResultsetRows(1007, 1004, (ResultsetRows)new ResultsetRowsStatic(procedureRows, (ColumnDefinition)new DefaultColumnDefinition(fields)));
  }
  
  public String getProcedureTerm() throws SQLException {
    try {
      return "PROCEDURE";
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public int getResultSetHoldability() throws SQLException {
    try {
      return 1;
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  void populateKeyResults(String db, String table, String keysComment, List<Row> resultRows, String fkTableName, boolean isExport) throws SQLException {
    LocalAndReferencedColumns parsedInfo = parseTableStatusIntoLocalAndReferencedColumns(keysComment);
    if (isExport && !parsedInfo.referencedTable.equals(table))
      return; 
    if (parsedInfo.localColumnsList.size() != parsedInfo.referencedColumnsList.size())
      throw SQLError.createSQLException(Messages.getString("DatabaseMetaData.12"), "S1000", getExceptionInterceptor()); 
    Iterator<String> localColumnNames = parsedInfo.localColumnsList.iterator();
    Iterator<String> referColumnNames = parsedInfo.referencedColumnsList.iterator();
    int keySeqIndex = 1;
    boolean dbMapsToSchema = (this.databaseTerm.getValue() == PropertyDefinitions.DatabaseTerm.SCHEMA);
    while (localColumnNames.hasNext()) {
      byte[][] tuple = new byte[14][];
      String lColumnName = StringUtils.unQuoteIdentifier(localColumnNames.next(), this.quotedId);
      String rColumnName = StringUtils.unQuoteIdentifier(referColumnNames.next(), this.quotedId);
      tuple[0] = dbMapsToSchema ? s2b("def") : s2b(parsedInfo.referencedDatabase);
      tuple[1] = dbMapsToSchema ? s2b(parsedInfo.referencedDatabase) : null;
      tuple[2] = s2b(isExport ? table : parsedInfo.referencedTable);
      tuple[3] = s2b(rColumnName);
      tuple[4] = dbMapsToSchema ? s2b("def") : s2b(db);
      tuple[5] = dbMapsToSchema ? s2b(db) : null;
      tuple[6] = s2b(isExport ? fkTableName : table);
      tuple[7] = s2b(lColumnName);
      tuple[8] = s2b(Integer.toString(keySeqIndex++));
      int[] actions = getForeignKeyActions(keysComment);
      tuple[9] = s2b(Integer.toString(actions[1]));
      tuple[10] = s2b(Integer.toString(actions[0]));
      tuple[11] = s2b(parsedInfo.constraintName);
      tuple[12] = null;
      tuple[13] = s2b(Integer.toString(7));
      resultRows.add(new ByteArrayRow(tuple, getExceptionInterceptor()));
    } 
  }
  
  public ResultSet getSchemas() throws SQLException {
    try {
      return getSchemas(null, null);
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public ResultSet getSchemas(String catalog, String schemaPattern) throws SQLException {
    try {
      List<String> dbList = (this.databaseTerm.getValue() == PropertyDefinitions.DatabaseTerm.SCHEMA) ? getDatabases(schemaPattern) : new ArrayList<>();
      Field[] fields = new Field[2];
      fields[0] = new Field("", "TABLE_SCHEM", this.metadataCollationIndex, this.metadataEncoding, MysqlType.CHAR, 0);
      fields[1] = new Field("", "TABLE_CATALOG", this.metadataCollationIndex, this.metadataEncoding, MysqlType.CHAR, 0);
      ArrayList<Row> tuples = new ArrayList<>(dbList.size());
      for (String db : dbList) {
        byte[][] rowVal = new byte[2][];
        rowVal[0] = s2b(db);
        rowVal[1] = s2b("def");
        tuples.add(new ByteArrayRow(rowVal, getExceptionInterceptor()));
      } 
      return (ResultSet)this.resultSetFactory.createFromResultsetRows(1007, 1004, (ResultsetRows)new ResultsetRowsStatic(tuples, (ColumnDefinition)new DefaultColumnDefinition(fields)));
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public String getSchemaTerm() throws SQLException {
    try {
      return (this.databaseTerm.getValue() == PropertyDefinitions.DatabaseTerm.SCHEMA) ? "SCHEMA" : "";
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public String getSearchStringEscape() throws SQLException {
    try {
      return "\\";
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public String getSQLKeywords() throws SQLException {
    try {
      if (mysqlKeywords != null)
        return mysqlKeywords; 
      synchronized (DatabaseMetaData.class) {
        if (mysqlKeywords != null)
          return mysqlKeywords; 
        Set<String> mysqlKeywordSet = new TreeSet<>();
        StringBuilder mysqlKeywordsBuffer = new StringBuilder();
        Collections.addAll(mysqlKeywordSet, MYSQL_KEYWORDS);
        mysqlKeywordSet.removeAll(SQL2003_KEYWORDS);
        for (String keyword : mysqlKeywordSet)
          mysqlKeywordsBuffer.append(",").append(keyword); 
        mysqlKeywords = mysqlKeywordsBuffer.substring(1);
        return mysqlKeywords;
      } 
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public int getSQLStateType() throws SQLException {
    try {
      return 2;
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public String getStringFunctions() throws SQLException {
    try {
      return "ASCII,BIN,BIT_LENGTH,CHAR,CHARACTER_LENGTH,CHAR_LENGTH,CONCAT,CONCAT_WS,CONV,ELT,EXPORT_SET,FIELD,FIND_IN_SET,HEX,INSERT,INSTR,LCASE,LEFT,LENGTH,LOAD_FILE,LOCATE,LOCATE,LOWER,LPAD,LTRIM,MAKE_SET,MATCH,MID,OCT,OCTET_LENGTH,ORD,POSITION,QUOTE,REPEAT,REPLACE,REVERSE,RIGHT,RPAD,RTRIM,SOUNDEX,SPACE,STRCMP,SUBSTRING,SUBSTRING,SUBSTRING,SUBSTRING,SUBSTRING_INDEX,TRIM,UCASE,UPPER";
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public ResultSet getSuperTables(String arg0, String arg1, String arg2) throws SQLException {
    try {
      Field[] fields = new Field[4];
      fields[0] = new Field("", "TABLE_CAT", this.metadataCollationIndex, this.metadataEncoding, MysqlType.CHAR, 32);
      fields[1] = new Field("", "TABLE_SCHEM", this.metadataCollationIndex, this.metadataEncoding, MysqlType.CHAR, 32);
      fields[2] = new Field("", "TABLE_NAME", this.metadataCollationIndex, this.metadataEncoding, MysqlType.CHAR, 32);
      fields[3] = new Field("", "SUPERTABLE_NAME", this.metadataCollationIndex, this.metadataEncoding, MysqlType.CHAR, 32);
      return (ResultSet)this.resultSetFactory.createFromResultsetRows(1007, 1004, (ResultsetRows)new ResultsetRowsStatic(new ArrayList(), (ColumnDefinition)new DefaultColumnDefinition(fields)));
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public ResultSet getSuperTypes(String arg0, String arg1, String arg2) throws SQLException {
    try {
      Field[] fields = new Field[6];
      fields[0] = new Field("", "TYPE_CAT", this.metadataCollationIndex, this.metadataEncoding, MysqlType.CHAR, 32);
      fields[1] = new Field("", "TYPE_SCHEM", this.metadataCollationIndex, this.metadataEncoding, MysqlType.CHAR, 32);
      fields[2] = new Field("", "TYPE_NAME", this.metadataCollationIndex, this.metadataEncoding, MysqlType.CHAR, 32);
      fields[3] = new Field("", "SUPERTYPE_CAT", this.metadataCollationIndex, this.metadataEncoding, MysqlType.CHAR, 32);
      fields[4] = new Field("", "SUPERTYPE_SCHEM", this.metadataCollationIndex, this.metadataEncoding, MysqlType.CHAR, 32);
      fields[5] = new Field("", "SUPERTYPE_NAME", this.metadataCollationIndex, this.metadataEncoding, MysqlType.CHAR, 32);
      return (ResultSet)this.resultSetFactory.createFromResultsetRows(1007, 1004, (ResultsetRows)new ResultsetRowsStatic(new ArrayList(), (ColumnDefinition)new DefaultColumnDefinition(fields)));
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public String getSystemFunctions() throws SQLException {
    try {
      return "DATABASE,USER,SYSTEM_USER,SESSION_USER,PASSWORD,ENCRYPT,LAST_INSERT_ID,VERSION";
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  protected String getTableNameWithCase(String table) {
    String tableNameWithCase = this.conn.lowerCaseTableNames() ? table.toLowerCase() : table;
    return tableNameWithCase;
  }
  
  public ResultSet getTablePrivileges(String catalog, String schemaPattern, String tableNamePattern) throws SQLException {
    try {
      Field[] fields = new Field[7];
      fields[0] = new Field("", "TABLE_CAT", this.metadataCollationIndex, this.metadataEncoding, MysqlType.CHAR, 64);
      fields[1] = new Field("", "TABLE_SCHEM", this.metadataCollationIndex, this.metadataEncoding, MysqlType.CHAR, 1);
      fields[2] = new Field("", "TABLE_NAME", this.metadataCollationIndex, this.metadataEncoding, MysqlType.CHAR, 64);
      fields[3] = new Field("", "GRANTOR", this.metadataCollationIndex, this.metadataEncoding, MysqlType.CHAR, 77);
      fields[4] = new Field("", "GRANTEE", this.metadataCollationIndex, this.metadataEncoding, MysqlType.CHAR, 77);
      fields[5] = new Field("", "PRIVILEGE", this.metadataCollationIndex, this.metadataEncoding, MysqlType.CHAR, 64);
      fields[6] = new Field("", "IS_GRANTABLE", this.metadataCollationIndex, this.metadataEncoding, MysqlType.CHAR, 3);
      String dbPattern = getDatabase(catalog, schemaPattern);
      StringBuilder grantQueryBuf = new StringBuilder("SELECT host,db,table_name,grantor,user,table_priv FROM mysql.tables_priv");
      StringBuilder conditionBuf = new StringBuilder();
      if (dbPattern != null)
        conditionBuf.append((this.databaseTerm.getValue() == PropertyDefinitions.DatabaseTerm.SCHEMA) ? " db LIKE ?" : " db = ?"); 
      if (tableNamePattern != null) {
        if (conditionBuf.length() > 0)
          conditionBuf.append(" AND"); 
        conditionBuf.append(" table_name LIKE ?");
      } 
      if (conditionBuf.length() > 0) {
        grantQueryBuf.append(" WHERE");
        grantQueryBuf.append(conditionBuf);
      } 
      ResultSet results = null;
      ArrayList<Row> grantRows = new ArrayList<>();
      PreparedStatement pStmt = null;
      try {
        pStmt = prepareMetaDataSafeStatement(grantQueryBuf.toString());
        int nextId = 1;
        if (dbPattern != null)
          pStmt.setString(nextId++, dbPattern); 
        if (tableNamePattern != null)
          pStmt.setString(nextId, tableNamePattern); 
        results = pStmt.executeQuery();
        while (results.next()) {
          String host = results.getString(1);
          String db = results.getString(2);
          String table = results.getString(3);
          String grantor = results.getString(4);
          String user = results.getString(5);
          if (user == null || user.length() == 0)
            user = "%"; 
          StringBuilder fullUser = new StringBuilder(user);
          if (host != null && this.useHostsInPrivileges) {
            fullUser.append("@");
            fullUser.append(host);
          } 
          String allPrivileges = results.getString(6);
          if (allPrivileges != null) {
            allPrivileges = allPrivileges.toUpperCase(Locale.ENGLISH);
            StringTokenizer st = new StringTokenizer(allPrivileges, ",");
            while (st.hasMoreTokens()) {
              String privilege = st.nextToken().trim();
              ResultSet columnResults = null;
              try {
                columnResults = getColumns(catalog, schemaPattern, table, null);
                while (columnResults.next()) {
                  byte[][] tuple = new byte[8][];
                  tuple[0] = (this.databaseTerm.getValue() == PropertyDefinitions.DatabaseTerm.SCHEMA) ? s2b("def") : s2b(db);
                  tuple[1] = (this.databaseTerm.getValue() == PropertyDefinitions.DatabaseTerm.SCHEMA) ? s2b(db) : null;
                  tuple[2] = s2b(table);
                  tuple[3] = (grantor != null) ? s2b(grantor) : null;
                  tuple[4] = s2b(fullUser.toString());
                  tuple[5] = s2b(privilege);
                  tuple[6] = null;
                  grantRows.add(new ByteArrayRow(tuple, getExceptionInterceptor()));
                } 
              } finally {
                if (columnResults != null)
                  try {
                    columnResults.close();
                  } catch (Exception exception) {} 
              } 
            } 
          } 
        } 
      } finally {
        if (results != null) {
          try {
            results.close();
          } catch (Exception exception) {}
          results = null;
        } 
        if (pStmt != null) {
          try {
            pStmt.close();
          } catch (Exception exception) {}
          pStmt = null;
        } 
      } 
      return (ResultSet)this.resultSetFactory.createFromResultsetRows(1007, 1004, (ResultsetRows)new ResultsetRowsStatic(grantRows, (ColumnDefinition)new DefaultColumnDefinition(fields)));
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public ResultSet getTables(String catalog, String schemaPattern, String tableNamePattern, final String[] types) throws SQLException {
    try {
      final SortedMap<TableMetaDataKey, Row> sortedRows = new TreeMap<>();
      ArrayList<Row> tuples = new ArrayList<>();
      final Statement stmt = this.conn.getMetadataSafeStatement();
      String db = getDatabase(catalog, schemaPattern);
      final boolean dbMapsToSchema = (this.databaseTerm.getValue() == PropertyDefinitions.DatabaseTerm.SCHEMA);
      if (tableNamePattern != null) {
        List<String> parseList = StringUtils.splitDBdotName(tableNamePattern, db, this.quotedId, this.session.getServerSession().isNoBackslashEscapesSet());
        if (parseList.size() == 2)
          tableNamePattern = parseList.get(1); 
      } 
      final String tableNamePat = tableNamePattern;
      try {
        (new IterateBlock<String>(dbMapsToSchema ? getSchemaPatternIterator(db) : getDatabaseIterator(db)) {
            void forEach(String dbPattern) throws SQLException {
              boolean operatingOnSystemDB = ("information_schema".equalsIgnoreCase(dbPattern) || "mysql".equalsIgnoreCase(dbPattern) || "performance_schema".equalsIgnoreCase(dbPattern));
              ResultSet results = null;
              try {
                try {
                  StringBuilder sqlBuf = new StringBuilder("SHOW FULL TABLES FROM ");
                  sqlBuf.append(StringUtils.quoteIdentifier(dbPattern, DatabaseMetaData.this.quotedId, DatabaseMetaData.this.pedantic));
                  if (tableNamePat != null) {
                    sqlBuf.append(" LIKE ");
                    sqlBuf.append(StringUtils.quoteIdentifier(tableNamePat, "'", true));
                  } 
                  results = stmt.executeQuery(sqlBuf.toString());
                } catch (SQLException sqlEx) {
                  if ("08S01".equals(sqlEx.getSQLState()))
                    throw sqlEx; 
                  return;
                } 
                boolean shouldReportTables = false;
                boolean shouldReportViews = false;
                boolean shouldReportSystemTables = false;
                boolean shouldReportSystemViews = false;
                boolean shouldReportLocalTemporaries = false;
                if (types == null || types.length == 0) {
                  shouldReportTables = true;
                  shouldReportViews = true;
                  shouldReportSystemTables = true;
                  shouldReportSystemViews = true;
                  shouldReportLocalTemporaries = true;
                } else {
                  for (int i = 0; i < types.length; i++) {
                    if (DatabaseMetaData.TableType.TABLE.equalsTo(types[i])) {
                      shouldReportTables = true;
                    } else if (DatabaseMetaData.TableType.VIEW.equalsTo(types[i])) {
                      shouldReportViews = true;
                    } else if (DatabaseMetaData.TableType.SYSTEM_TABLE.equalsTo(types[i])) {
                      shouldReportSystemTables = true;
                    } else if (DatabaseMetaData.TableType.SYSTEM_VIEW.equalsTo(types[i])) {
                      shouldReportSystemViews = true;
                    } else if (DatabaseMetaData.TableType.LOCAL_TEMPORARY.equalsTo(types[i])) {
                      shouldReportLocalTemporaries = true;
                    } 
                  } 
                } 
                int typeColumnIndex = 0;
                boolean hasTableTypes = false;
                try {
                  typeColumnIndex = results.findColumn("table_type");
                  hasTableTypes = true;
                } catch (SQLException sqlEx) {
                  try {
                    typeColumnIndex = results.findColumn("Type");
                    hasTableTypes = true;
                  } catch (SQLException sqlEx2) {
                    hasTableTypes = false;
                  } 
                } 
                while (results.next()) {
                  byte[][] row = new byte[10][];
                  row[0] = dbMapsToSchema ? DatabaseMetaData.this.s2b("def") : DatabaseMetaData.this.s2b(dbPattern);
                  row[1] = dbMapsToSchema ? DatabaseMetaData.this.s2b(dbPattern) : null;
                  row[2] = results.getBytes(1);
                  row[4] = new byte[0];
                  row[5] = null;
                  row[6] = null;
                  row[7] = null;
                  row[8] = null;
                  row[9] = null;
                  if (hasTableTypes) {
                    boolean reportTable;
                    DatabaseMetaData.TableMetaDataKey tablesKey;
                    String tableType = results.getString(typeColumnIndex);
                    switch (DatabaseMetaData.TableType.getTableTypeCompliantWith(tableType)) {
                      case TABLE:
                        reportTable = false;
                        tablesKey = null;
                        if (operatingOnSystemDB && shouldReportSystemTables) {
                          row[3] = DatabaseMetaData.TableType.SYSTEM_TABLE.asBytes();
                          tablesKey = new DatabaseMetaData.TableMetaDataKey(DatabaseMetaData.TableType.SYSTEM_TABLE.getName(), dbPattern, null, results.getString(1));
                          reportTable = true;
                        } else if (!operatingOnSystemDB && shouldReportTables) {
                          row[3] = DatabaseMetaData.TableType.TABLE.asBytes();
                          tablesKey = new DatabaseMetaData.TableMetaDataKey(DatabaseMetaData.TableType.TABLE.getName(), dbPattern, null, results.getString(1));
                          reportTable = true;
                        } 
                        if (reportTable)
                          sortedRows.put(tablesKey, new ByteArrayRow(row, DatabaseMetaData.this.getExceptionInterceptor())); 
                        continue;
                      case VIEW:
                        if (shouldReportViews) {
                          row[3] = DatabaseMetaData.TableType.VIEW.asBytes();
                          sortedRows.put(new DatabaseMetaData.TableMetaDataKey(DatabaseMetaData.TableType.VIEW.getName(), dbPattern, null, results.getString(1)), new ByteArrayRow(row, DatabaseMetaData.this
                                .getExceptionInterceptor()));
                        } 
                        continue;
                      case SYSTEM_TABLE:
                        if (shouldReportSystemTables) {
                          row[3] = DatabaseMetaData.TableType.SYSTEM_TABLE.asBytes();
                          sortedRows.put(new DatabaseMetaData.TableMetaDataKey(DatabaseMetaData.TableType.SYSTEM_TABLE.getName(), dbPattern, null, results.getString(1)), new ByteArrayRow(row, DatabaseMetaData.this
                                .getExceptionInterceptor()));
                        } 
                        continue;
                      case SYSTEM_VIEW:
                        if (shouldReportSystemViews) {
                          row[3] = DatabaseMetaData.TableType.SYSTEM_VIEW.asBytes();
                          sortedRows.put(new DatabaseMetaData.TableMetaDataKey(DatabaseMetaData.TableType.SYSTEM_VIEW.getName(), dbPattern, null, results.getString(1)), new ByteArrayRow(row, DatabaseMetaData.this
                                .getExceptionInterceptor()));
                        } 
                        continue;
                      case LOCAL_TEMPORARY:
                        if (shouldReportLocalTemporaries) {
                          row[3] = DatabaseMetaData.TableType.LOCAL_TEMPORARY.asBytes();
                          sortedRows.put(new DatabaseMetaData.TableMetaDataKey(DatabaseMetaData.TableType.LOCAL_TEMPORARY.getName(), dbPattern, null, results.getString(1)), new ByteArrayRow(row, DatabaseMetaData.this
                                .getExceptionInterceptor()));
                        } 
                        continue;
                    } 
                    row[3] = DatabaseMetaData.TableType.TABLE.asBytes();
                    sortedRows.put(new DatabaseMetaData.TableMetaDataKey(DatabaseMetaData.TableType.TABLE.getName(), dbPattern, null, results.getString(1)), new ByteArrayRow(row, DatabaseMetaData.this
                          .getExceptionInterceptor()));
                    continue;
                  } 
                  if (shouldReportTables) {
                    row[3] = DatabaseMetaData.TableType.TABLE.asBytes();
                    sortedRows.put(new DatabaseMetaData.TableMetaDataKey(DatabaseMetaData.TableType.TABLE.getName(), dbPattern, null, results.getString(1)), new ByteArrayRow(row, DatabaseMetaData.this
                          .getExceptionInterceptor()));
                  } 
                } 
              } finally {
                if (results != null) {
                  try {
                    results.close();
                  } catch (Exception exception) {}
                  results = null;
                } 
              } 
            }
          }).doForAll();
      } finally {
        if (stmt != null)
          stmt.close(); 
      } 
      tuples.addAll(sortedRows.values());
      return (ResultSet)this.resultSetFactory.createFromResultsetRows(1007, 1004, (ResultsetRows)new ResultsetRowsStatic(tuples, 
            createTablesFields()));
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  protected ColumnDefinition createTablesFields() {
    Field[] fields = new Field[10];
    fields[0] = new Field("", "TABLE_CAT", this.metadataCollationIndex, this.metadataEncoding, MysqlType.VARCHAR, 255);
    fields[1] = new Field("", "TABLE_SCHEM", this.metadataCollationIndex, this.metadataEncoding, MysqlType.VARCHAR, 0);
    fields[2] = new Field("", "TABLE_NAME", this.metadataCollationIndex, this.metadataEncoding, MysqlType.VARCHAR, 255);
    fields[3] = new Field("", "TABLE_TYPE", this.metadataCollationIndex, this.metadataEncoding, MysqlType.VARCHAR, 5);
    fields[4] = new Field("", "REMARKS", this.metadataCollationIndex, this.metadataEncoding, MysqlType.VARCHAR, 0);
    fields[5] = new Field("", "TYPE_CAT", this.metadataCollationIndex, this.metadataEncoding, MysqlType.VARCHAR, 0);
    fields[6] = new Field("", "TYPE_SCHEM", this.metadataCollationIndex, this.metadataEncoding, MysqlType.VARCHAR, 0);
    fields[7] = new Field("", "TYPE_NAME", this.metadataCollationIndex, this.metadataEncoding, MysqlType.VARCHAR, 0);
    fields[8] = new Field("", "SELF_REFERENCING_COL_NAME", this.metadataCollationIndex, this.metadataEncoding, MysqlType.VARCHAR, 0);
    fields[9] = new Field("", "REF_GENERATION", this.metadataCollationIndex, this.metadataEncoding, MysqlType.VARCHAR, 0);
    return (ColumnDefinition)new DefaultColumnDefinition(fields);
  }
  
  public ResultSet getTableTypes() throws SQLException {
    try {
      ArrayList<Row> tuples = new ArrayList<>();
      Field[] fields = { new Field("", "TABLE_TYPE", this.metadataCollationIndex, this.metadataEncoding, MysqlType.VARCHAR, 256) };
      tuples.add(new ByteArrayRow(new byte[][] { TableType.LOCAL_TEMPORARY.asBytes() }, getExceptionInterceptor()));
      tuples.add(new ByteArrayRow(new byte[][] { TableType.SYSTEM_TABLE.asBytes() }, getExceptionInterceptor()));
      tuples.add(new ByteArrayRow(new byte[][] { TableType.SYSTEM_VIEW.asBytes() }, getExceptionInterceptor()));
      tuples.add(new ByteArrayRow(new byte[][] { TableType.TABLE.asBytes() }, getExceptionInterceptor()));
      tuples.add(new ByteArrayRow(new byte[][] { TableType.VIEW.asBytes() }, getExceptionInterceptor()));
      return (ResultSet)this.resultSetFactory.createFromResultsetRows(1007, 1004, (ResultsetRows)new ResultsetRowsStatic(tuples, (ColumnDefinition)new DefaultColumnDefinition(fields)));
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public String getTimeDateFunctions() throws SQLException {
    try {
      return "DAYOFWEEK,WEEKDAY,DAYOFMONTH,DAYOFYEAR,MONTH,DAYNAME,MONTHNAME,QUARTER,WEEK,YEAR,HOUR,MINUTE,SECOND,PERIOD_ADD,PERIOD_DIFF,TO_DAYS,FROM_DAYS,DATE_FORMAT,TIME_FORMAT,CURDATE,CURRENT_DATE,CURTIME,CURRENT_TIME,NOW,SYSDATE,CURRENT_TIMESTAMP,UNIX_TIMESTAMP,FROM_UNIXTIME,SEC_TO_TIME,TIME_TO_SEC";
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  private byte[][] getTypeInfo(String mysqlTypeName) throws SQLException {
    MysqlType mt = MysqlType.getByName(mysqlTypeName);
    byte[][] rowVal = new byte[18][];
    rowVal[0] = s2b(mysqlTypeName);
    rowVal[1] = Integer.toString((mt == MysqlType.YEAR && !this.yearIsDateType) ? 5 : mt.getJdbcType()).getBytes();
    rowVal[2] = Integer.toString((mt.getPrecision().longValue() > 2147483647L) ? Integer.MAX_VALUE : mt.getPrecision().intValue()).getBytes();
    switch (mt) {
      case LOCAL_TEMPORARY:
      case null:
      case null:
      case null:
      case null:
      case null:
      case null:
      case null:
      case null:
      case null:
      case null:
      case null:
      case null:
      case null:
      case null:
      case null:
      case null:
      case null:
      case null:
      case null:
      case null:
        rowVal[3] = s2b("'");
        rowVal[4] = s2b("'");
        break;
      default:
        rowVal[3] = s2b("");
        rowVal[4] = s2b("");
        break;
    } 
    rowVal[5] = s2b(mt.getCreateParams());
    rowVal[6] = Integer.toString(1).getBytes();
    rowVal[7] = s2b("true");
    rowVal[8] = Integer.toString(3).getBytes();
    rowVal[9] = s2b(mt.isAllowed(32) ? "true" : "false");
    rowVal[10] = s2b("false");
    switch (mt) {
      case SYSTEM_TABLE:
      case SYSTEM_VIEW:
      case null:
      case null:
      case null:
      case null:
      case null:
      case null:
      case null:
      case null:
      case null:
      case null:
      case null:
      case null:
      case null:
        rowVal[11] = s2b("true");
        break;
      default:
        rowVal[11] = s2b("false");
        break;
    } 
    rowVal[12] = s2b(mt.getName());
    switch (mt) {
      case TABLE:
      case VIEW:
      case SYSTEM_TABLE:
      case SYSTEM_VIEW:
        rowVal[13] = s2b("-308");
        rowVal[14] = s2b("308");
        rowVal[15] = s2b("0");
        rowVal[16] = s2b("0");
        rowVal[17] = s2b("10");
        return rowVal;
      case null:
      case null:
        rowVal[13] = s2b("-38");
        rowVal[14] = s2b("38");
        rowVal[15] = s2b("0");
        rowVal[16] = s2b("0");
        rowVal[17] = s2b("10");
        return rowVal;
    } 
    rowVal[13] = s2b("0");
    rowVal[14] = s2b("0");
    rowVal[15] = s2b("0");
    rowVal[16] = s2b("0");
    rowVal[17] = s2b("10");
    return rowVal;
  }
  
  public ResultSet getTypeInfo() throws SQLException {
    try {
      Field[] fields = new Field[18];
      fields[0] = new Field("", "TYPE_NAME", this.metadataCollationIndex, this.metadataEncoding, MysqlType.CHAR, 32);
      fields[1] = new Field("", "DATA_TYPE", this.metadataCollationIndex, this.metadataEncoding, MysqlType.INT, 5);
      fields[2] = new Field("", "PRECISION", this.metadataCollationIndex, this.metadataEncoding, MysqlType.INT, 10);
      fields[3] = new Field("", "LITERAL_PREFIX", this.metadataCollationIndex, this.metadataEncoding, MysqlType.CHAR, 4);
      fields[4] = new Field("", "LITERAL_SUFFIX", this.metadataCollationIndex, this.metadataEncoding, MysqlType.CHAR, 4);
      fields[5] = new Field("", "CREATE_PARAMS", this.metadataCollationIndex, this.metadataEncoding, MysqlType.CHAR, 32);
      fields[6] = new Field("", "NULLABLE", this.metadataCollationIndex, this.metadataEncoding, MysqlType.SMALLINT, 5);
      fields[7] = new Field("", "CASE_SENSITIVE", this.metadataCollationIndex, this.metadataEncoding, MysqlType.BOOLEAN, 3);
      fields[8] = new Field("", "SEARCHABLE", this.metadataCollationIndex, this.metadataEncoding, MysqlType.SMALLINT, 3);
      fields[9] = new Field("", "UNSIGNED_ATTRIBUTE", this.metadataCollationIndex, this.metadataEncoding, MysqlType.BOOLEAN, 3);
      fields[10] = new Field("", "FIXED_PREC_SCALE", this.metadataCollationIndex, this.metadataEncoding, MysqlType.BOOLEAN, 3);
      fields[11] = new Field("", "AUTO_INCREMENT", this.metadataCollationIndex, this.metadataEncoding, MysqlType.BOOLEAN, 3);
      fields[12] = new Field("", "LOCAL_TYPE_NAME", this.metadataCollationIndex, this.metadataEncoding, MysqlType.CHAR, 32);
      fields[13] = new Field("", "MINIMUM_SCALE", this.metadataCollationIndex, this.metadataEncoding, MysqlType.SMALLINT, 5);
      fields[14] = new Field("", "MAXIMUM_SCALE", this.metadataCollationIndex, this.metadataEncoding, MysqlType.SMALLINT, 5);
      fields[15] = new Field("", "SQL_DATA_TYPE", this.metadataCollationIndex, this.metadataEncoding, MysqlType.INT, 10);
      fields[16] = new Field("", "SQL_DATETIME_SUB", this.metadataCollationIndex, this.metadataEncoding, MysqlType.INT, 10);
      fields[17] = new Field("", "NUM_PREC_RADIX", this.metadataCollationIndex, this.metadataEncoding, MysqlType.INT, 10);
      ArrayList<Row> tuples = new ArrayList<>();
      tuples.add(new ByteArrayRow(getTypeInfo("BIT"), getExceptionInterceptor()));
      tuples.add(new ByteArrayRow(getTypeInfo("TINYINT"), getExceptionInterceptor()));
      tuples.add(new ByteArrayRow(getTypeInfo("TINYINT UNSIGNED"), getExceptionInterceptor()));
      tuples.add(new ByteArrayRow(getTypeInfo("BIGINT"), getExceptionInterceptor()));
      tuples.add(new ByteArrayRow(getTypeInfo("BIGINT UNSIGNED"), getExceptionInterceptor()));
      tuples.add(new ByteArrayRow(getTypeInfo("LONG VARBINARY"), getExceptionInterceptor()));
      tuples.add(new ByteArrayRow(getTypeInfo("MEDIUMBLOB"), getExceptionInterceptor()));
      tuples.add(new ByteArrayRow(getTypeInfo("LONGBLOB"), getExceptionInterceptor()));
      tuples.add(new ByteArrayRow(getTypeInfo("BLOB"), getExceptionInterceptor()));
      tuples.add(new ByteArrayRow(getTypeInfo("VARBINARY"), getExceptionInterceptor()));
      tuples.add(new ByteArrayRow(getTypeInfo("TINYBLOB"), getExceptionInterceptor()));
      tuples.add(new ByteArrayRow(getTypeInfo("BINARY"), getExceptionInterceptor()));
      tuples.add(new ByteArrayRow(getTypeInfo("LONG VARCHAR"), getExceptionInterceptor()));
      tuples.add(new ByteArrayRow(getTypeInfo("MEDIUMTEXT"), getExceptionInterceptor()));
      tuples.add(new ByteArrayRow(getTypeInfo("LONGTEXT"), getExceptionInterceptor()));
      tuples.add(new ByteArrayRow(getTypeInfo("TEXT"), getExceptionInterceptor()));
      tuples.add(new ByteArrayRow(getTypeInfo("CHAR"), getExceptionInterceptor()));
      tuples.add(new ByteArrayRow(getTypeInfo("ENUM"), getExceptionInterceptor()));
      tuples.add(new ByteArrayRow(getTypeInfo("SET"), getExceptionInterceptor()));
      tuples.add(new ByteArrayRow(getTypeInfo("DECIMAL"), getExceptionInterceptor()));
      tuples.add(new ByteArrayRow(getTypeInfo("NUMERIC"), getExceptionInterceptor()));
      tuples.add(new ByteArrayRow(getTypeInfo("INTEGER"), getExceptionInterceptor()));
      tuples.add(new ByteArrayRow(getTypeInfo("INT"), getExceptionInterceptor()));
      tuples.add(new ByteArrayRow(getTypeInfo("MEDIUMINT"), getExceptionInterceptor()));
      tuples.add(new ByteArrayRow(getTypeInfo("INTEGER UNSIGNED"), getExceptionInterceptor()));
      tuples.add(new ByteArrayRow(getTypeInfo("INT UNSIGNED"), getExceptionInterceptor()));
      tuples.add(new ByteArrayRow(getTypeInfo("MEDIUMINT UNSIGNED"), getExceptionInterceptor()));
      tuples.add(new ByteArrayRow(getTypeInfo("SMALLINT"), getExceptionInterceptor()));
      tuples.add(new ByteArrayRow(getTypeInfo("SMALLINT UNSIGNED"), getExceptionInterceptor()));
      if (!this.yearIsDateType)
        tuples.add(new ByteArrayRow(getTypeInfo("YEAR"), getExceptionInterceptor())); 
      tuples.add(new ByteArrayRow(getTypeInfo("FLOAT"), getExceptionInterceptor()));
      tuples.add(new ByteArrayRow(getTypeInfo("DOUBLE"), getExceptionInterceptor()));
      tuples.add(new ByteArrayRow(getTypeInfo("DOUBLE PRECISION"), getExceptionInterceptor()));
      tuples.add(new ByteArrayRow(getTypeInfo("REAL"), getExceptionInterceptor()));
      tuples.add(new ByteArrayRow(getTypeInfo("DOUBLE UNSIGNED"), getExceptionInterceptor()));
      tuples.add(new ByteArrayRow(getTypeInfo("DOUBLE PRECISION UNSIGNED"), getExceptionInterceptor()));
      tuples.add(new ByteArrayRow(getTypeInfo("VARCHAR"), getExceptionInterceptor()));
      tuples.add(new ByteArrayRow(getTypeInfo("TINYTEXT"), getExceptionInterceptor()));
      tuples.add(new ByteArrayRow(getTypeInfo("BOOL"), getExceptionInterceptor()));
      tuples.add(new ByteArrayRow(getTypeInfo("DATE"), getExceptionInterceptor()));
      if (this.yearIsDateType)
        tuples.add(new ByteArrayRow(getTypeInfo("YEAR"), getExceptionInterceptor())); 
      tuples.add(new ByteArrayRow(getTypeInfo("TIME"), getExceptionInterceptor()));
      tuples.add(new ByteArrayRow(getTypeInfo("DATETIME"), getExceptionInterceptor()));
      tuples.add(new ByteArrayRow(getTypeInfo("TIMESTAMP"), getExceptionInterceptor()));
      return (ResultSet)this.resultSetFactory.createFromResultsetRows(1007, 1004, (ResultsetRows)new ResultsetRowsStatic(tuples, (ColumnDefinition)new DefaultColumnDefinition(fields)));
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public ResultSet getUDTs(String catalog, String schemaPattern, String typeNamePattern, int[] types) throws SQLException {
    try {
      Field[] fields = new Field[7];
      fields[0] = new Field("", "TYPE_CAT", this.metadataCollationIndex, this.metadataEncoding, MysqlType.VARCHAR, 32);
      fields[1] = new Field("", "TYPE_SCHEM", this.metadataCollationIndex, this.metadataEncoding, MysqlType.VARCHAR, 32);
      fields[2] = new Field("", "TYPE_NAME", this.metadataCollationIndex, this.metadataEncoding, MysqlType.VARCHAR, 32);
      fields[3] = new Field("", "CLASS_NAME", this.metadataCollationIndex, this.metadataEncoding, MysqlType.VARCHAR, 32);
      fields[4] = new Field("", "DATA_TYPE", this.metadataCollationIndex, this.metadataEncoding, MysqlType.INT, 10);
      fields[5] = new Field("", "REMARKS", this.metadataCollationIndex, this.metadataEncoding, MysqlType.VARCHAR, 32);
      fields[6] = new Field("", "BASE_TYPE", this.metadataCollationIndex, this.metadataEncoding, MysqlType.SMALLINT, 10);
      ArrayList<Row> tuples = new ArrayList<>();
      return (ResultSet)this.resultSetFactory.createFromResultsetRows(1007, 1004, (ResultsetRows)new ResultsetRowsStatic(tuples, (ColumnDefinition)new DefaultColumnDefinition(fields)));
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public String getURL() throws SQLException {
    try {
      return this.conn.getURL();
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public String getUserName() throws SQLException {
    try {
      if (this.useHostsInPrivileges) {
        Statement stmt = null;
        ResultSet rs = null;
        try {
          stmt = this.conn.getMetadataSafeStatement();
          rs = stmt.executeQuery("SELECT USER()");
          rs.next();
          return rs.getString(1);
        } finally {
          if (rs != null) {
            try {
              rs.close();
            } catch (Exception ex) {
              AssertionFailedException.shouldNotHappen(ex);
            } 
            rs = null;
          } 
          if (stmt != null) {
            try {
              stmt.close();
            } catch (Exception ex) {
              AssertionFailedException.shouldNotHappen(ex);
            } 
            stmt = null;
          } 
        } 
      } 
      return this.conn.getUser();
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  protected Field[] getVersionColumnsFields() {
    Field[] fields = new Field[8];
    fields[0] = new Field("", "SCOPE", this.metadataCollationIndex, this.metadataEncoding, MysqlType.SMALLINT, 5);
    fields[1] = new Field("", "COLUMN_NAME", this.metadataCollationIndex, this.metadataEncoding, MysqlType.CHAR, 32);
    fields[2] = new Field("", "DATA_TYPE", this.metadataCollationIndex, this.metadataEncoding, MysqlType.INT, 5);
    fields[3] = new Field("", "TYPE_NAME", this.metadataCollationIndex, this.metadataEncoding, MysqlType.CHAR, 16);
    fields[4] = new Field("", "COLUMN_SIZE", this.metadataCollationIndex, this.metadataEncoding, MysqlType.INT, 16);
    fields[5] = new Field("", "BUFFER_LENGTH", this.metadataCollationIndex, this.metadataEncoding, MysqlType.INT, 16);
    fields[6] = new Field("", "DECIMAL_DIGITS", this.metadataCollationIndex, this.metadataEncoding, MysqlType.SMALLINT, 16);
    fields[7] = new Field("", "PSEUDO_COLUMN", this.metadataCollationIndex, this.metadataEncoding, MysqlType.SMALLINT, 5);
    return fields;
  }
  
  public ResultSet getVersionColumns(String catalog, String schema, final String table) throws SQLException {
    try {
      if (table == null)
        throw SQLError.createSQLException(Messages.getString("DatabaseMetaData.2"), "S1009", 
            getExceptionInterceptor()); 
      final ArrayList<Row> rows = new ArrayList<>();
      final Statement stmt = this.conn.getMetadataSafeStatement();
      String db = getDatabase(catalog, schema);
      try {
        (new IterateBlock<String>(getDatabaseIterator(db)) {
            void forEach(String dbStr) throws SQLException {
              ResultSet results = null;
              try {
                StringBuilder whereBuf = new StringBuilder(" Extra LIKE '%on update CURRENT_TIMESTAMP%'");
                List<String> rsFields = new ArrayList<>();
                if (whereBuf.length() > 0 || rsFields.size() > 0) {
                  StringBuilder queryBuf = new StringBuilder("SHOW COLUMNS FROM ");
                  queryBuf.append(StringUtils.quoteIdentifier(table, DatabaseMetaData.this.quotedId, DatabaseMetaData.this.pedantic));
                  queryBuf.append(" FROM ");
                  queryBuf.append(StringUtils.quoteIdentifier(dbStr, DatabaseMetaData.this.quotedId, DatabaseMetaData.this.pedantic));
                  queryBuf.append(" WHERE");
                  queryBuf.append(whereBuf.toString());
                  try {
                    results = stmt.executeQuery(queryBuf.toString());
                  } catch (SQLException sqlEx) {
                    String sqlState = sqlEx.getSQLState();
                    int errorCode = sqlEx.getErrorCode();
                    if (!"42S02".equals(sqlState) && errorCode != 1146 && errorCode != 1049)
                      throw sqlEx; 
                  } 
                  while (results != null && results.next()) {
                    DatabaseMetaData.TypeDescriptor typeDesc = new DatabaseMetaData.TypeDescriptor(results.getString("Type"), results.getString("Null"));
                    byte[][] rowVal = new byte[8][];
                    rowVal[0] = null;
                    rowVal[1] = results.getBytes("Field");
                    rowVal[2] = Short.toString((typeDesc.mysqlType == MysqlType.YEAR && !DatabaseMetaData.this.yearIsDateType) ? 5 : 
                        (short)typeDesc.mysqlType.getJdbcType()).getBytes();
                    rowVal[3] = DatabaseMetaData.this.s2b(typeDesc.mysqlType.getName());
                    rowVal[4] = (typeDesc.columnSize == null) ? null : DatabaseMetaData.this.s2b(typeDesc.columnSize.toString());
                    rowVal[5] = DatabaseMetaData.this.s2b(Integer.toString(typeDesc.bufferLength));
                    rowVal[6] = (typeDesc.decimalDigits == null) ? null : DatabaseMetaData.this.s2b(typeDesc.decimalDigits.toString());
                    rowVal[7] = Integer.toString(1).getBytes();
                    rows.add(new ByteArrayRow(rowVal, DatabaseMetaData.this.getExceptionInterceptor()));
                  } 
                } 
              } catch (SQLException sqlEx) {
                if (!"42S02".equals(sqlEx.getSQLState()))
                  throw sqlEx; 
              } finally {
                if (results != null) {
                  try {
                    results.close();
                  } catch (Exception exception) {}
                  results = null;
                } 
              } 
            }
          }).doForAll();
      } finally {
        if (stmt != null)
          stmt.close(); 
      } 
      return (ResultSet)this.resultSetFactory.createFromResultsetRows(1007, 1004, (ResultsetRows)new ResultsetRowsStatic(rows, (ColumnDefinition)new DefaultColumnDefinition(
              getVersionColumnsFields())));
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public boolean insertsAreDetected(int type) throws SQLException {
    try {
      return false;
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public boolean isCatalogAtStart() throws SQLException {
    try {
      return true;
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public boolean isReadOnly() throws SQLException {
    try {
      return false;
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public boolean locatorsUpdateCopy() throws SQLException {
    try {
      return !((Boolean)this.conn.getPropertySet().getBooleanProperty(PropertyKey.emulateLocators).getValue()).booleanValue();
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public boolean nullPlusNonNullIsNull() throws SQLException {
    try {
      return true;
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public boolean nullsAreSortedAtEnd() throws SQLException {
    try {
      return false;
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public boolean nullsAreSortedAtStart() throws SQLException {
    try {
      return false;
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public boolean nullsAreSortedHigh() throws SQLException {
    try {
      return false;
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public boolean nullsAreSortedLow() throws SQLException {
    try {
      return !nullsAreSortedHigh();
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public boolean othersDeletesAreVisible(int type) throws SQLException {
    try {
      return false;
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public boolean othersInsertsAreVisible(int type) throws SQLException {
    try {
      return false;
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public boolean othersUpdatesAreVisible(int type) throws SQLException {
    try {
      return false;
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public boolean ownDeletesAreVisible(int type) throws SQLException {
    try {
      return false;
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public boolean ownInsertsAreVisible(int type) throws SQLException {
    try {
      return false;
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public boolean ownUpdatesAreVisible(int type) throws SQLException {
    try {
      return false;
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  protected LocalAndReferencedColumns parseTableStatusIntoLocalAndReferencedColumns(String keysComment) throws SQLException {
    String columnsDelimitter = ",";
    int indexOfOpenParenLocalColumns = StringUtils.indexOfIgnoreCase(0, keysComment, "(", this.quotedId, this.quotedId, SearchMode.__BSE_MRK_COM_MYM_HNT_WS);
    if (indexOfOpenParenLocalColumns == -1)
      throw SQLError.createSQLException(Messages.getString("DatabaseMetaData.14"), "S1000", getExceptionInterceptor()); 
    String constraintName = StringUtils.unQuoteIdentifier(keysComment.substring(0, indexOfOpenParenLocalColumns).trim(), this.quotedId);
    keysComment = keysComment.substring(indexOfOpenParenLocalColumns, keysComment.length());
    String keysCommentTrimmed = keysComment.trim();
    int indexOfCloseParenLocalColumns = StringUtils.indexOfIgnoreCase(0, keysCommentTrimmed, ")", this.quotedId, this.quotedId, SearchMode.__BSE_MRK_COM_MYM_HNT_WS);
    if (indexOfCloseParenLocalColumns == -1)
      throw SQLError.createSQLException(Messages.getString("DatabaseMetaData.15"), "S1000", getExceptionInterceptor()); 
    String localColumnNamesString = keysCommentTrimmed.substring(1, indexOfCloseParenLocalColumns);
    int indexOfRefer = StringUtils.indexOfIgnoreCase(0, keysCommentTrimmed, "REFER ", this.quotedId, this.quotedId, SearchMode.__BSE_MRK_COM_MYM_HNT_WS);
    if (indexOfRefer == -1)
      throw SQLError.createSQLException(Messages.getString("DatabaseMetaData.16"), "S1000", getExceptionInterceptor()); 
    int indexOfOpenParenReferCol = StringUtils.indexOfIgnoreCase(indexOfRefer, keysCommentTrimmed, "(", this.quotedId, this.quotedId, SearchMode.__MRK_COM_MYM_HNT_WS);
    if (indexOfOpenParenReferCol == -1)
      throw SQLError.createSQLException(Messages.getString("DatabaseMetaData.17"), "S1000", getExceptionInterceptor()); 
    String referDbTableString = keysCommentTrimmed.substring(indexOfRefer + "REFER ".length(), indexOfOpenParenReferCol);
    int indexOfSlash = StringUtils.indexOfIgnoreCase(0, referDbTableString, "/", this.quotedId, this.quotedId, SearchMode.__MRK_COM_MYM_HNT_WS);
    if (indexOfSlash == -1)
      throw SQLError.createSQLException(Messages.getString("DatabaseMetaData.18"), "S1000", getExceptionInterceptor()); 
    String referDb = StringUtils.unQuoteIdentifier(referDbTableString.substring(0, indexOfSlash), this.quotedId);
    String referTable = StringUtils.unQuoteIdentifier(referDbTableString.substring(indexOfSlash + 1).trim(), this.quotedId);
    int indexOfCloseParenRefer = StringUtils.indexOfIgnoreCase(indexOfOpenParenReferCol, keysCommentTrimmed, ")", this.quotedId, this.quotedId, SearchMode.__BSE_MRK_COM_MYM_HNT_WS);
    if (indexOfCloseParenRefer == -1)
      throw SQLError.createSQLException(Messages.getString("DatabaseMetaData.19"), "S1000", getExceptionInterceptor()); 
    String referColumnNamesString = keysCommentTrimmed.substring(indexOfOpenParenReferCol + 1, indexOfCloseParenRefer);
    List<String> referColumnsList = StringUtils.split(referColumnNamesString, columnsDelimitter, this.quotedId, this.quotedId, false);
    List<String> localColumnsList = StringUtils.split(localColumnNamesString, columnsDelimitter, this.quotedId, this.quotedId, false);
    return new LocalAndReferencedColumns(localColumnsList, referColumnsList, constraintName, referDb, referTable);
  }
  
  protected byte[] s2b(String s) throws SQLException {
    if (s == null)
      return null; 
    try {
      return StringUtils.getBytes(s, this.conn.getCharacterSetMetadata());
    } catch (CJException e) {
      throw SQLExceptionsMapping.translateException(e, getExceptionInterceptor());
    } 
  }
  
  public boolean storesLowerCaseIdentifiers() throws SQLException {
    try {
      return this.conn.storesLowerCaseTableName();
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public boolean storesLowerCaseQuotedIdentifiers() throws SQLException {
    try {
      return this.conn.storesLowerCaseTableName();
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public boolean storesMixedCaseIdentifiers() throws SQLException {
    try {
      return !this.conn.storesLowerCaseTableName();
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public boolean storesMixedCaseQuotedIdentifiers() throws SQLException {
    try {
      return !this.conn.storesLowerCaseTableName();
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public boolean storesUpperCaseIdentifiers() throws SQLException {
    try {
      return false;
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public boolean storesUpperCaseQuotedIdentifiers() throws SQLException {
    try {
      return true;
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public boolean supportsAlterTableWithAddColumn() throws SQLException {
    try {
      return true;
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public boolean supportsAlterTableWithDropColumn() throws SQLException {
    try {
      return true;
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public boolean supportsANSI92EntryLevelSQL() throws SQLException {
    try {
      return true;
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public boolean supportsANSI92FullSQL() throws SQLException {
    try {
      return false;
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public boolean supportsANSI92IntermediateSQL() throws SQLException {
    try {
      return false;
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public boolean supportsBatchUpdates() throws SQLException {
    try {
      return true;
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public boolean supportsCatalogsInDataManipulation() throws SQLException {
    try {
      return (this.databaseTerm.getValue() == PropertyDefinitions.DatabaseTerm.CATALOG);
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public boolean supportsCatalogsInIndexDefinitions() throws SQLException {
    try {
      return (this.databaseTerm.getValue() == PropertyDefinitions.DatabaseTerm.CATALOG);
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public boolean supportsCatalogsInPrivilegeDefinitions() throws SQLException {
    try {
      return (this.databaseTerm.getValue() == PropertyDefinitions.DatabaseTerm.CATALOG);
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public boolean supportsCatalogsInProcedureCalls() throws SQLException {
    try {
      return (this.databaseTerm.getValue() == PropertyDefinitions.DatabaseTerm.CATALOG);
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public boolean supportsCatalogsInTableDefinitions() throws SQLException {
    try {
      return (this.databaseTerm.getValue() == PropertyDefinitions.DatabaseTerm.CATALOG);
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public boolean supportsColumnAliasing() throws SQLException {
    try {
      return true;
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public boolean supportsConvert() throws SQLException {
    try {
      return false;
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public boolean supportsConvert(int fromType, int toType) throws SQLException {
    try {
      return MysqlType.supportsConvert(fromType, toType);
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public boolean supportsCoreSQLGrammar() throws SQLException {
    try {
      return true;
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public boolean supportsCorrelatedSubqueries() throws SQLException {
    try {
      return true;
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public boolean supportsDataDefinitionAndDataManipulationTransactions() throws SQLException {
    try {
      return false;
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public boolean supportsDataManipulationTransactionsOnly() throws SQLException {
    try {
      return false;
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public boolean supportsDifferentTableCorrelationNames() throws SQLException {
    try {
      return true;
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public boolean supportsExpressionsInOrderBy() throws SQLException {
    try {
      return true;
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public boolean supportsExtendedSQLGrammar() throws SQLException {
    try {
      return false;
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public boolean supportsFullOuterJoins() throws SQLException {
    try {
      return false;
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public boolean supportsGetGeneratedKeys() {
    try {
      return true;
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public boolean supportsGroupBy() throws SQLException {
    try {
      return true;
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public boolean supportsGroupByBeyondSelect() throws SQLException {
    try {
      return true;
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public boolean supportsGroupByUnrelated() throws SQLException {
    try {
      return true;
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public boolean supportsIntegrityEnhancementFacility() throws SQLException {
    try {
      if (!((Boolean)this.conn.getPropertySet().getBooleanProperty(PropertyKey.overrideSupportsIntegrityEnhancementFacility).getValue()).booleanValue())
        return false; 
      return true;
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public boolean supportsLikeEscapeClause() throws SQLException {
    try {
      return true;
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public boolean supportsLimitedOuterJoins() throws SQLException {
    try {
      return true;
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public boolean supportsMinimumSQLGrammar() throws SQLException {
    try {
      return true;
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public boolean supportsMixedCaseIdentifiers() throws SQLException {
    try {
      return !this.conn.lowerCaseTableNames();
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public boolean supportsMixedCaseQuotedIdentifiers() throws SQLException {
    try {
      return !this.conn.lowerCaseTableNames();
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public boolean supportsMultipleOpenResults() throws SQLException {
    try {
      return true;
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public boolean supportsMultipleResultSets() throws SQLException {
    try {
      return true;
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public boolean supportsMultipleTransactions() throws SQLException {
    try {
      return true;
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public boolean supportsNamedParameters() throws SQLException {
    try {
      return false;
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public boolean supportsNonNullableColumns() throws SQLException {
    try {
      return true;
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public boolean supportsOpenCursorsAcrossCommit() throws SQLException {
    try {
      return false;
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public boolean supportsOpenCursorsAcrossRollback() throws SQLException {
    try {
      return false;
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public boolean supportsOpenStatementsAcrossCommit() throws SQLException {
    try {
      return false;
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public boolean supportsOpenStatementsAcrossRollback() throws SQLException {
    try {
      return false;
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public boolean supportsOrderByUnrelated() throws SQLException {
    try {
      return false;
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public boolean supportsOuterJoins() throws SQLException {
    try {
      return true;
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public boolean supportsPositionedDelete() throws SQLException {
    try {
      return false;
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public boolean supportsPositionedUpdate() throws SQLException {
    try {
      return false;
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public boolean supportsResultSetConcurrency(int type, int concurrency) throws SQLException {
    try {
      if ((type == 1003 || type == 1004) && (concurrency == 1007 || concurrency == 1008))
        return true; 
      if (type == 1005)
        return false; 
      throw SQLError.createSQLException(Messages.getString("DatabaseMetaData.20"), "S1009", getExceptionInterceptor());
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public boolean supportsResultSetHoldability(int holdability) throws SQLException {
    try {
      return (holdability == 1);
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public boolean supportsResultSetType(int type) throws SQLException {
    try {
      return (type == 1003 || type == 1004);
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public boolean supportsSavepoints() throws SQLException {
    try {
      return true;
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public boolean supportsSchemasInDataManipulation() throws SQLException {
    try {
      return (this.databaseTerm.getValue() == PropertyDefinitions.DatabaseTerm.SCHEMA);
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public boolean supportsSchemasInIndexDefinitions() throws SQLException {
    try {
      return (this.databaseTerm.getValue() == PropertyDefinitions.DatabaseTerm.SCHEMA);
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public boolean supportsSchemasInPrivilegeDefinitions() throws SQLException {
    try {
      return (this.databaseTerm.getValue() == PropertyDefinitions.DatabaseTerm.SCHEMA);
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public boolean supportsSchemasInProcedureCalls() throws SQLException {
    try {
      return (this.databaseTerm.getValue() == PropertyDefinitions.DatabaseTerm.SCHEMA);
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public boolean supportsSchemasInTableDefinitions() throws SQLException {
    try {
      return (this.databaseTerm.getValue() == PropertyDefinitions.DatabaseTerm.SCHEMA);
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public boolean supportsSelectForUpdate() throws SQLException {
    try {
      return true;
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public boolean supportsStatementPooling() throws SQLException {
    try {
      return false;
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public boolean supportsStoredProcedures() throws SQLException {
    try {
      return true;
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public boolean supportsSubqueriesInComparisons() throws SQLException {
    try {
      return true;
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public boolean supportsSubqueriesInExists() throws SQLException {
    try {
      return true;
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public boolean supportsSubqueriesInIns() throws SQLException {
    try {
      return true;
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public boolean supportsSubqueriesInQuantifieds() throws SQLException {
    try {
      return true;
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public boolean supportsTableCorrelationNames() throws SQLException {
    try {
      return true;
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public boolean supportsTransactionIsolationLevel(int level) throws SQLException {
    try {
      switch (level) {
        case 1:
        case 2:
        case 4:
        case 8:
          return true;
      } 
      return false;
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public boolean supportsTransactions() throws SQLException {
    try {
      return true;
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public boolean supportsUnion() throws SQLException {
    try {
      return true;
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public boolean supportsUnionAll() throws SQLException {
    try {
      return true;
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public boolean updatesAreDetected(int type) throws SQLException {
    try {
      return false;
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public boolean usesLocalFilePerTable() throws SQLException {
    try {
      return false;
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public boolean usesLocalFiles() throws SQLException {
    try {
      return false;
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public ResultSet getClientInfoProperties() throws SQLException {
    try {
      Field[] fields = new Field[4];
      fields[0] = new Field("", "NAME", this.metadataCollationIndex, this.metadataEncoding, MysqlType.VARCHAR, 255);
      fields[1] = new Field("", "MAX_LEN", this.metadataCollationIndex, this.metadataEncoding, MysqlType.INT, 10);
      fields[2] = new Field("", "DEFAULT_VALUE", this.metadataCollationIndex, this.metadataEncoding, MysqlType.VARCHAR, 255);
      fields[3] = new Field("", "DESCRIPTION", this.metadataCollationIndex, this.metadataEncoding, MysqlType.VARCHAR, 255);
      return (ResultSet)this.resultSetFactory.createFromResultsetRows(1007, 1004, (ResultsetRows)new ResultsetRowsStatic(new ArrayList(), (ColumnDefinition)new DefaultColumnDefinition(fields)));
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public ResultSet getFunctionColumns(String catalog, String schemaPattern, String functionNamePattern, String columnNamePattern) throws SQLException {
    try {
      return getProcedureOrFunctionColumns(createFunctionColumnsFields(), catalog, schemaPattern, functionNamePattern, columnNamePattern, false, true);
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  protected Field[] createFunctionColumnsFields() {
    Field[] fields = { 
        new Field("", "FUNCTION_CAT", this.metadataCollationIndex, this.metadataEncoding, MysqlType.VARCHAR, 512), new Field("", "FUNCTION_SCHEM", this.metadataCollationIndex, this.metadataEncoding, MysqlType.VARCHAR, 512), new Field("", "FUNCTION_NAME", this.metadataCollationIndex, this.metadataEncoding, MysqlType.VARCHAR, 512), new Field("", "COLUMN_NAME", this.metadataCollationIndex, this.metadataEncoding, MysqlType.VARCHAR, 512), new Field("", "COLUMN_TYPE", this.metadataCollationIndex, this.metadataEncoding, MysqlType.VARCHAR, 64), new Field("", "DATA_TYPE", this.metadataCollationIndex, this.metadataEncoding, MysqlType.SMALLINT, 6), new Field("", "TYPE_NAME", this.metadataCollationIndex, this.metadataEncoding, MysqlType.VARCHAR, 64), new Field("", "PRECISION", this.metadataCollationIndex, this.metadataEncoding, MysqlType.INT, 12), new Field("", "LENGTH", this.metadataCollationIndex, this.metadataEncoding, MysqlType.INT, 12), new Field("", "SCALE", this.metadataCollationIndex, this.metadataEncoding, MysqlType.SMALLINT, 12), 
        new Field("", "RADIX", this.metadataCollationIndex, this.metadataEncoding, MysqlType.SMALLINT, 6), new Field("", "NULLABLE", this.metadataCollationIndex, this.metadataEncoding, MysqlType.SMALLINT, 6), new Field("", "REMARKS", this.metadataCollationIndex, this.metadataEncoding, MysqlType.VARCHAR, 512), new Field("", "CHAR_OCTET_LENGTH", this.metadataCollationIndex, this.metadataEncoding, MysqlType.INT, 32), new Field("", "ORDINAL_POSITION", this.metadataCollationIndex, this.metadataEncoding, MysqlType.INT, 32), new Field("", "IS_NULLABLE", this.metadataCollationIndex, this.metadataEncoding, MysqlType.VARCHAR, 12), new Field("", "SPECIFIC_NAME", this.metadataCollationIndex, this.metadataEncoding, MysqlType.VARCHAR, 64) };
    return fields;
  }
  
  protected Field[] getFunctionsFields() {
    Field[] fields = new Field[6];
    fields[0] = new Field("", "FUNCTION_CAT", this.metadataCollationIndex, this.metadataEncoding, MysqlType.CHAR, 255);
    fields[1] = new Field("", "FUNCTION_SCHEM", this.metadataCollationIndex, this.metadataEncoding, MysqlType.CHAR, 255);
    fields[2] = new Field("", "FUNCTION_NAME", this.metadataCollationIndex, this.metadataEncoding, MysqlType.CHAR, 255);
    fields[3] = new Field("", "REMARKS", this.metadataCollationIndex, this.metadataEncoding, MysqlType.CHAR, 255);
    fields[4] = new Field("", "FUNCTION_TYPE", this.metadataCollationIndex, this.metadataEncoding, MysqlType.SMALLINT, 6);
    fields[5] = new Field("", "SPECIFIC_NAME", this.metadataCollationIndex, this.metadataEncoding, MysqlType.CHAR, 255);
    return fields;
  }
  
  public ResultSet getFunctions(String catalog, String schemaPattern, String functionNamePattern) throws SQLException {
    try {
      return getProceduresAndOrFunctions(getFunctionsFields(), catalog, schemaPattern, functionNamePattern, false, true);
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public boolean providesQueryObjectGenerator() throws SQLException {
    return false;
  }
  
  public boolean supportsStoredFunctionsUsingCallSyntax() throws SQLException {
    try {
      return true;
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  protected PreparedStatement prepareMetaDataSafeStatement(String sql) throws SQLException {
    PreparedStatement pStmt = this.conn.clientPrepareStatement(sql, 1004, 1007);
    if (pStmt.getMaxRows() != 0)
      pStmt.setMaxRows(0); 
    ((JdbcStatement)pStmt).setHoldResultsOpenOverClose(true);
    return pStmt;
  }
  
  public ResultSet getPseudoColumns(String catalog, String schemaPattern, String tableNamePattern, String columnNamePattern) throws SQLException {
    try {
      Field[] fields = { 
          new Field("", "TABLE_CAT", this.metadataCollationIndex, this.metadataEncoding, MysqlType.VARCHAR, 512), new Field("", "TABLE_SCHEM", this.metadataCollationIndex, this.metadataEncoding, MysqlType.VARCHAR, 512), new Field("", "TABLE_NAME", this.metadataCollationIndex, this.metadataEncoding, MysqlType.VARCHAR, 512), new Field("", "COLUMN_NAME", this.metadataCollationIndex, this.metadataEncoding, MysqlType.VARCHAR, 512), new Field("", "DATA_TYPE", this.metadataCollationIndex, this.metadataEncoding, MysqlType.INT, 12), new Field("", "COLUMN_SIZE", this.metadataCollationIndex, this.metadataEncoding, MysqlType.INT, 12), new Field("", "DECIMAL_DIGITS", this.metadataCollationIndex, this.metadataEncoding, MysqlType.INT, 12), new Field("", "NUM_PREC_RADIX", this.metadataCollationIndex, this.metadataEncoding, MysqlType.INT, 12), new Field("", "COLUMN_USAGE", this.metadataCollationIndex, this.metadataEncoding, MysqlType.VARCHAR, 512), new Field("", "REMARKS", this.metadataCollationIndex, this.metadataEncoding, MysqlType.VARCHAR, 512), 
          new Field("", "CHAR_OCTET_LENGTH", this.metadataCollationIndex, this.metadataEncoding, MysqlType.INT, 12), new Field("", "IS_NULLABLE", this.metadataCollationIndex, this.metadataEncoding, MysqlType.VARCHAR, 512) };
      return (ResultSet)this.resultSetFactory.createFromResultsetRows(1007, 1004, (ResultsetRows)new ResultsetRowsStatic(new ArrayList(), (ColumnDefinition)new DefaultColumnDefinition(fields)));
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public boolean generatedKeyAlwaysReturned() throws SQLException {
    try {
      return true;
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public <T> T unwrap(Class<T> iface) throws SQLException {
    try {
      try {
        return iface.cast(this);
      } catch (ClassCastException cce) {
        throw SQLError.createSQLException(Messages.getString("Common.UnableToUnwrap", new Object[] { iface.toString() }), "S1009", this.conn
            .getExceptionInterceptor());
      } 
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public boolean isWrapperFor(Class<?> iface) throws SQLException {
    try {
      return iface.isInstance(this);
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public RowIdLifetime getRowIdLifetime() throws SQLException {
    try {
      return RowIdLifetime.ROWID_UNSUPPORTED;
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public boolean autoCommitFailureClosesAllResultSets() throws SQLException {
    try {
      return false;
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public String getMetadataEncoding() {
    return this.metadataEncoding;
  }
  
  public void setMetadataEncoding(String metadataEncoding) {
    this.metadataEncoding = metadataEncoding;
  }
  
  public int getMetadataCollationIndex() {
    return this.metadataCollationIndex;
  }
  
  public void setMetadataCollationIndex(int metadataCollationIndex) {
    this.metadataCollationIndex = metadataCollationIndex;
  }
}
