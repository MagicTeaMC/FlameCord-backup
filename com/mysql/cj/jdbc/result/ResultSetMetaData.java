package com.mysql.cj.jdbc.result;

import com.mysql.cj.Messages;
import com.mysql.cj.MysqlType;
import com.mysql.cj.Session;
import com.mysql.cj.conf.PropertyDefinitions;
import com.mysql.cj.conf.PropertyKey;
import com.mysql.cj.exceptions.CJException;
import com.mysql.cj.exceptions.ExceptionInterceptor;
import com.mysql.cj.jdbc.exceptions.SQLError;
import com.mysql.cj.jdbc.exceptions.SQLExceptionsMapping;
import com.mysql.cj.result.Field;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

public class ResultSetMetaData implements ResultSetMetaData {
  private Session session;
  
  private Field[] fields;
  
  private static int clampedGetLength(Field f) {
    long fieldLength = f.getLength();
    if (fieldLength > 2147483647L)
      fieldLength = 2147483647L; 
    return (int)fieldLength;
  }
  
  boolean useOldAliasBehavior = false;
  
  boolean treatYearAsDate = true;
  
  private ExceptionInterceptor exceptionInterceptor;
  
  public ResultSetMetaData(Session session, Field[] fields, boolean useOldAliasBehavior, boolean treatYearAsDate, ExceptionInterceptor exceptionInterceptor) {
    this.session = session;
    this.fields = fields;
    this.useOldAliasBehavior = useOldAliasBehavior;
    this.treatYearAsDate = treatYearAsDate;
    this.exceptionInterceptor = exceptionInterceptor;
  }
  
  public String getCatalogName(int column) throws SQLException {
    try {
      if (this.session.getPropertySet().getEnumProperty(PropertyKey.databaseTerm).getValue() == PropertyDefinitions.DatabaseTerm.SCHEMA)
        return ""; 
      String database = getField(column).getDatabaseName();
      return (database == null) ? "" : database;
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, this.exceptionInterceptor);
    } 
  }
  
  public String getColumnCharacterEncoding(int column) throws SQLException {
    return getField(column).getEncoding();
  }
  
  public String getColumnCharacterSet(int column) throws SQLException {
    return this.session.getServerSession().getCharsetSettings().getMysqlCharsetNameForCollationIndex(Integer.valueOf(getField(column).getCollationIndex()));
  }
  
  public String getColumnClassName(int column) throws SQLException {
    try {
      Field f = getField(column);
      switch (f.getMysqlType()) {
        case YEAR:
          if (!this.treatYearAsDate)
            return Short.class.getName(); 
          return f.getMysqlType().getClassName();
      } 
      return f.getMysqlType().getClassName();
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, this.exceptionInterceptor);
    } 
  }
  
  public int getColumnCount() throws SQLException {
    try {
      return this.fields.length;
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, this.exceptionInterceptor);
    } 
  }
  
  public int getColumnDisplaySize(int column) throws SQLException {
    try {
      Field f = getField(column);
      int lengthInBytes = clampedGetLength(f);
      return lengthInBytes / this.session.getServerSession().getCharsetSettings().getMaxBytesPerChar(Integer.valueOf(f.getCollationIndex()), f.getEncoding());
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, this.exceptionInterceptor);
    } 
  }
  
  public String getColumnLabel(int column) throws SQLException {
    try {
      if (this.useOldAliasBehavior)
        return getColumnName(column); 
      return getField(column).getColumnLabel();
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, this.exceptionInterceptor);
    } 
  }
  
  public String getColumnName(int column) throws SQLException {
    try {
      if (this.useOldAliasBehavior)
        return getField(column).getName(); 
      String name = getField(column).getOriginalName();
      if (name == null)
        return getField(column).getName(); 
      return name;
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, this.exceptionInterceptor);
    } 
  }
  
  public int getColumnType(int column) throws SQLException {
    try {
      Field f = getField(column);
      if (f.getMysqlType() == MysqlType.YEAR && !this.treatYearAsDate)
        return 5; 
      return f.getJavaType();
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, this.exceptionInterceptor);
    } 
  }
  
  public String getColumnTypeName(int column) throws SQLException {
    try {
      Field field = getField(column);
      return field.getMysqlType().getName();
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, this.exceptionInterceptor);
    } 
  }
  
  protected Field getField(int columnIndex) throws SQLException {
    if (columnIndex < 1 || columnIndex > this.fields.length)
      throw SQLError.createSQLException(Messages.getString("ResultSetMetaData.46"), "S1002", this.exceptionInterceptor); 
    return this.fields[columnIndex - 1];
  }
  
  public int getPrecision(int column) throws SQLException {
    try {
      Field f = getField(column);
      switch (f.getMysqlType()) {
        case TINYBLOB:
        case BLOB:
        case MEDIUMBLOB:
        case LONGBLOB:
          return clampedGetLength(f);
      } 
      return f.getMysqlType().isDecimal() ? clampedGetLength(f) : (
        clampedGetLength(f) / this.session.getServerSession().getCharsetSettings().getMaxBytesPerChar(Integer.valueOf(f.getCollationIndex()), f.getEncoding()));
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, this.exceptionInterceptor);
    } 
  }
  
  public int getScale(int column) throws SQLException {
    try {
      Field f = getField(column);
      if (f.getMysqlType().isDecimal())
        return f.getDecimals(); 
      return 0;
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, this.exceptionInterceptor);
    } 
  }
  
  public String getSchemaName(int column) throws SQLException {
    try {
      if (this.session.getPropertySet().getEnumProperty(PropertyKey.databaseTerm).getValue() == PropertyDefinitions.DatabaseTerm.CATALOG)
        return ""; 
      String database = getField(column).getDatabaseName();
      return (database == null) ? "" : database;
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, this.exceptionInterceptor);
    } 
  }
  
  public String getTableName(int column) throws SQLException {
    try {
      String res = this.useOldAliasBehavior ? getField(column).getTableName() : getField(column).getOriginalTableName();
      return (res == null) ? "" : res;
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, this.exceptionInterceptor);
    } 
  }
  
  public boolean isAutoIncrement(int column) throws SQLException {
    try {
      Field f = getField(column);
      return f.isAutoIncrement();
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, this.exceptionInterceptor);
    } 
  }
  
  public boolean isCaseSensitive(int column) throws SQLException {
    try {
      String collationName;
      Field field = getField(column);
      switch (field.getMysqlType()) {
        case YEAR:
        case BIT:
        case TINYINT:
        case TINYINT_UNSIGNED:
        case SMALLINT:
        case SMALLINT_UNSIGNED:
        case INT:
        case INT_UNSIGNED:
        case MEDIUMINT:
        case MEDIUMINT_UNSIGNED:
        case BIGINT:
        case BIGINT_UNSIGNED:
        case FLOAT:
        case FLOAT_UNSIGNED:
        case DOUBLE:
        case DOUBLE_UNSIGNED:
        case DATE:
        case TIME:
        case TIMESTAMP:
        case DATETIME:
          return false;
        case CHAR:
        case VARCHAR:
        case TINYTEXT:
        case TEXT:
        case MEDIUMTEXT:
        case LONGTEXT:
        case JSON:
        case ENUM:
        case SET:
          collationName = this.session.getServerSession().getCharsetSettings().getCollationNameForCollationIndex(Integer.valueOf(field.getCollationIndex()));
          return (collationName != null && !collationName.endsWith("_ci"));
      } 
      return true;
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, this.exceptionInterceptor);
    } 
  }
  
  public boolean isCurrency(int column) throws SQLException {
    try {
      return false;
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, this.exceptionInterceptor);
    } 
  }
  
  public boolean isDefinitelyWritable(int column) throws SQLException {
    try {
      return isWritable(column);
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, this.exceptionInterceptor);
    } 
  }
  
  public int isNullable(int column) throws SQLException {
    try {
      if (!getField(column).isNotNull())
        return 1; 
      return 0;
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, this.exceptionInterceptor);
    } 
  }
  
  public boolean isReadOnly(int column) throws SQLException {
    try {
      return getField(column).isReadOnly();
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, this.exceptionInterceptor);
    } 
  }
  
  public boolean isSearchable(int column) throws SQLException {
    try {
      return true;
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, this.exceptionInterceptor);
    } 
  }
  
  public boolean isSigned(int column) throws SQLException {
    try {
      return MysqlType.isSigned(getField(column).getMysqlType());
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, this.exceptionInterceptor);
    } 
  }
  
  public boolean isWritable(int column) throws SQLException {
    try {
      return !isReadOnly(column);
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, this.exceptionInterceptor);
    } 
  }
  
  public String toString() {
    StringBuilder toStringBuf = new StringBuilder();
    toStringBuf.append(super.toString());
    toStringBuf.append(" - Field level information: ");
    for (int i = 0; i < this.fields.length; i++) {
      toStringBuf.append("\n\t");
      toStringBuf.append(this.fields[i].toString());
    } 
    return toStringBuf.toString();
  }
  
  public boolean isWrapperFor(Class<?> iface) throws SQLException {
    try {
      return iface.isInstance(this);
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, this.exceptionInterceptor);
    } 
  }
  
  public <T> T unwrap(Class<T> iface) throws SQLException {
    try {
      try {
        return iface.cast(this);
      } catch (ClassCastException cce) {
        throw SQLError.createSQLException(Messages.getString("Common.UnableToUnwrap", new Object[] { iface.toString() }), "S1009", this.exceptionInterceptor);
      } 
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, this.exceptionInterceptor);
    } 
  }
  
  public Field[] getFields() {
    return this.fields;
  }
}
