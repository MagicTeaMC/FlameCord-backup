package com.mysql.cj.jdbc;

import com.mysql.cj.Messages;
import com.mysql.cj.MysqlType;
import com.mysql.cj.PreparedQuery;
import com.mysql.cj.QueryInfo;
import com.mysql.cj.conf.PropertyDefinitions;
import com.mysql.cj.conf.PropertyKey;
import com.mysql.cj.exceptions.CJException;
import com.mysql.cj.exceptions.FeatureNotAvailableException;
import com.mysql.cj.jdbc.exceptions.SQLError;
import com.mysql.cj.jdbc.exceptions.SQLExceptionsMapping;
import com.mysql.cj.jdbc.result.ResultSetImpl;
import com.mysql.cj.jdbc.result.ResultSetInternalMethods;
import com.mysql.cj.protocol.ColumnDefinition;
import com.mysql.cj.protocol.ResultsetRows;
import com.mysql.cj.protocol.a.result.ByteArrayRow;
import com.mysql.cj.protocol.a.result.ResultsetRowsStatic;
import com.mysql.cj.result.DefaultColumnDefinition;
import com.mysql.cj.result.Field;
import com.mysql.cj.result.Row;
import com.mysql.cj.util.SearchMode;
import com.mysql.cj.util.StringUtils;
import com.mysql.cj.util.Util;
import java.io.InputStream;
import java.io.Reader;
import java.math.BigDecimal;
import java.net.URL;
import java.sql.Array;
import java.sql.Blob;
import java.sql.CallableStatement;
import java.sql.Clob;
import java.sql.DatabaseMetaData;
import java.sql.Date;
import java.sql.JDBCType;
import java.sql.NClob;
import java.sql.ParameterMetaData;
import java.sql.PreparedStatement;
import java.sql.Ref;
import java.sql.ResultSet;
import java.sql.RowId;
import java.sql.SQLException;
import java.sql.SQLType;
import java.sql.SQLXML;
import java.sql.Statement;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class CallableStatement extends ClientPreparedStatement implements CallableStatement {
  private static final int NOT_OUTPUT_PARAMETER_INDICATOR = -2147483648;
  
  private static final String PARAMETER_NAMESPACE_PREFIX = "@com_mysql_jdbc_outparam_";
  
  protected static class CallableStatementParam {
    int index;
    
    int inOutModifier;
    
    boolean isIn;
    
    boolean isOut;
    
    int jdbcType;
    
    short nullability;
    
    String paramName;
    
    int precision;
    
    int scale;
    
    String typeName;
    
    MysqlType desiredMysqlType = MysqlType.UNKNOWN;
    
    CallableStatementParam(String name, int idx, boolean in, boolean out, int jdbcType, String typeName, int precision, int scale, short nullability, int inOutModifier) {
      this.paramName = name;
      this.isIn = in;
      this.isOut = out;
      this.index = idx;
      this.jdbcType = jdbcType;
      this.typeName = typeName;
      this.precision = precision;
      this.scale = scale;
      this.nullability = nullability;
      this.inOutModifier = inOutModifier;
    }
    
    protected Object clone() throws CloneNotSupportedException {
      return super.clone();
    }
  }
  
  public class CallableStatementParamInfo implements ParameterMetaData {
    String dbInUse;
    
    boolean isFunctionCall;
    
    String nativeSql;
    
    int numParameters;
    
    List<CallableStatement.CallableStatementParam> parameterList;
    
    Map<String, CallableStatement.CallableStatementParam> parameterMap;
    
    boolean isReadOnlySafeProcedure = false;
    
    boolean isReadOnlySafeChecked = false;
    
    CallableStatementParamInfo(CallableStatementParamInfo fullParamInfo) {
      this.nativeSql = ((PreparedQuery)CallableStatement.this.query).getOriginalSql();
      this.dbInUse = CallableStatement.this.getCurrentDatabase();
      this.isFunctionCall = fullParamInfo.isFunctionCall;
      int[] localParameterMap = CallableStatement.this.placeholderToParameterIndexMap;
      int parameterMapLength = localParameterMap.length;
      this.isReadOnlySafeProcedure = fullParamInfo.isReadOnlySafeProcedure;
      this.isReadOnlySafeChecked = fullParamInfo.isReadOnlySafeChecked;
      this.parameterList = new ArrayList<>(fullParamInfo.numParameters);
      this.parameterMap = new HashMap<>(fullParamInfo.numParameters);
      if (this.isFunctionCall)
        this.parameterList.add(fullParamInfo.parameterList.get(0)); 
      int offset = this.isFunctionCall ? 1 : 0;
      for (int i = 0; i < parameterMapLength; i++) {
        if (localParameterMap[i] != 0) {
          CallableStatement.CallableStatementParam param = fullParamInfo.parameterList.get(localParameterMap[i] + offset);
          this.parameterList.add(param);
          this.parameterMap.put(param.paramName, param);
        } 
      } 
      this.numParameters = this.parameterList.size();
    }
    
    CallableStatementParamInfo(ResultSet paramTypesRs) throws SQLException {
      boolean hadRows = paramTypesRs.last();
      this.nativeSql = ((PreparedQuery)CallableStatement.this.query).getOriginalSql();
      this.dbInUse = CallableStatement.this.getCurrentDatabase();
      this.isFunctionCall = CallableStatement.this.callingStoredFunction;
      if (hadRows) {
        this.numParameters = paramTypesRs.getRow();
        this.parameterList = new ArrayList<>(this.numParameters);
        this.parameterMap = new HashMap<>(this.numParameters);
        paramTypesRs.beforeFirst();
        addParametersFromDBMD(paramTypesRs);
      } else {
        this.numParameters = 0;
      } 
      if (this.isFunctionCall)
        this.numParameters++; 
    }
    
    private void addParametersFromDBMD(ResultSet paramTypesRs) throws SQLException {
      int i = 0;
      while (paramTypesRs.next()) {
        int inOutModifier;
        String paramName = paramTypesRs.getString(4);
        switch (paramTypesRs.getInt(5)) {
          case 1:
            inOutModifier = 1;
            break;
          case 2:
            inOutModifier = 2;
            break;
          case 4:
          case 5:
            inOutModifier = 4;
            break;
          default:
            inOutModifier = 0;
            break;
        } 
        boolean isOutParameter = false;
        boolean isInParameter = false;
        if (i == 0 && this.isFunctionCall) {
          isOutParameter = true;
          isInParameter = false;
        } else if (inOutModifier == 2) {
          isOutParameter = true;
          isInParameter = true;
        } else if (inOutModifier == 1) {
          isOutParameter = false;
          isInParameter = true;
        } else if (inOutModifier == 4) {
          isOutParameter = true;
          isInParameter = false;
        } 
        int jdbcType = paramTypesRs.getInt(6);
        String typeName = paramTypesRs.getString(7);
        int precision = paramTypesRs.getInt(8);
        int scale = paramTypesRs.getInt(10);
        short nullability = paramTypesRs.getShort(12);
        CallableStatement.CallableStatementParam paramInfoToAdd = new CallableStatement.CallableStatementParam(paramName, i++, isInParameter, isOutParameter, jdbcType, typeName, precision, scale, nullability, inOutModifier);
        this.parameterList.add(paramInfoToAdd);
        this.parameterMap.put(paramName, paramInfoToAdd);
      } 
    }
    
    protected void checkBounds(int paramIndex) throws SQLException {
      int localParamIndex = paramIndex - 1;
      if (paramIndex < 0 || localParamIndex >= this.numParameters)
        throw SQLError.createSQLException(Messages.getString("CallableStatement.11", new Object[] { Integer.valueOf(paramIndex), Integer.valueOf(this.numParameters) }), "S1009", CallableStatement.this
            .getExceptionInterceptor()); 
    }
    
    protected Object clone() throws CloneNotSupportedException {
      return super.clone();
    }
    
    CallableStatement.CallableStatementParam getParameter(int index) {
      return this.parameterList.get(index);
    }
    
    CallableStatement.CallableStatementParam getParameter(String name) {
      return this.parameterMap.get(name);
    }
    
    public String getParameterClassName(int arg0) throws SQLException {
      try {
        String mysqlTypeName = getParameterTypeName(arg0);
        MysqlType mysqlType = MysqlType.getByName(mysqlTypeName);
        switch (mysqlType) {
          case YEAR:
            if (!((Boolean)CallableStatement.this.session.getPropertySet().getBooleanProperty(PropertyKey.yearIsDateType).getValue()).booleanValue())
              return Short.class.getName(); 
            return mysqlType.getClassName();
        } 
        return mysqlType.getClassName();
      } catch (CJException cJException) {
        throw SQLExceptionsMapping.translateException(cJException);
      } 
    }
    
    public int getParameterCount() throws SQLException {
      try {
        if (this.parameterList == null)
          return 0; 
        return this.parameterList.size();
      } catch (CJException cJException) {
        throw SQLExceptionsMapping.translateException(cJException);
      } 
    }
    
    public int getParameterMode(int arg0) throws SQLException {
      try {
        checkBounds(arg0);
        return (getParameter(arg0 - 1)).inOutModifier;
      } catch (CJException cJException) {
        throw SQLExceptionsMapping.translateException(cJException);
      } 
    }
    
    public int getParameterType(int arg0) throws SQLException {
      try {
        checkBounds(arg0);
        return (getParameter(arg0 - 1)).jdbcType;
      } catch (CJException cJException) {
        throw SQLExceptionsMapping.translateException(cJException);
      } 
    }
    
    public String getParameterTypeName(int arg0) throws SQLException {
      try {
        checkBounds(arg0);
        return (getParameter(arg0 - 1)).typeName;
      } catch (CJException cJException) {
        throw SQLExceptionsMapping.translateException(cJException);
      } 
    }
    
    public int getPrecision(int arg0) throws SQLException {
      try {
        checkBounds(arg0);
        return (getParameter(arg0 - 1)).precision;
      } catch (CJException cJException) {
        throw SQLExceptionsMapping.translateException(cJException);
      } 
    }
    
    public int getScale(int arg0) throws SQLException {
      try {
        checkBounds(arg0);
        return (getParameter(arg0 - 1)).scale;
      } catch (CJException cJException) {
        throw SQLExceptionsMapping.translateException(cJException);
      } 
    }
    
    public int isNullable(int arg0) throws SQLException {
      try {
        checkBounds(arg0);
        return (getParameter(arg0 - 1)).nullability;
      } catch (CJException cJException) {
        throw SQLExceptionsMapping.translateException(cJException);
      } 
    }
    
    public boolean isSigned(int arg0) throws SQLException {
      try {
        checkBounds(arg0);
        return false;
      } catch (CJException cJException) {
        throw SQLExceptionsMapping.translateException(cJException);
      } 
    }
    
    Iterator<CallableStatement.CallableStatementParam> iterator() {
      return this.parameterList.iterator();
    }
    
    int numberOfParameters() {
      return this.numParameters;
    }
    
    public boolean isWrapperFor(Class<?> iface) throws SQLException {
      try {
        CallableStatement.this.checkClosed();
        return iface.isInstance(this);
      } catch (CJException cJException) {
        throw SQLExceptionsMapping.translateException(cJException);
      } 
    }
    
    public <T> T unwrap(Class<T> iface) throws SQLException {
      try {
        try {
          return iface.cast(this);
        } catch (ClassCastException cce) {
          throw SQLError.createSQLException(Messages.getString("Common.UnableToUnwrap", new Object[] { iface.toString() }), "S1009", CallableStatement.this
              .getExceptionInterceptor());
        } 
      } catch (CJException cJException) {
        throw SQLExceptionsMapping.translateException(cJException);
      } 
    }
  }
  
  private static String mangleParameterName(String origParameterName) {
    if (origParameterName == null)
      return null; 
    int offset = 0;
    if (origParameterName.length() > 0 && origParameterName.charAt(0) == '@')
      offset = 1; 
    StringBuilder paramNameBuf = new StringBuilder("@com_mysql_jdbc_outparam_".length() + origParameterName.length());
    paramNameBuf.append("@com_mysql_jdbc_outparam_");
    paramNameBuf.append(origParameterName.substring(offset));
    return paramNameBuf.toString();
  }
  
  private boolean callingStoredFunction = false;
  
  private ResultSetInternalMethods functionReturnValueResults;
  
  private boolean hasOutputParams = false;
  
  private ResultSetInternalMethods outputParameterResults;
  
  protected boolean outputParamWasNull = false;
  
  private int[] parameterIndexToRsIndex;
  
  protected CallableStatementParamInfo paramInfo;
  
  private CallableStatementParam returnValueParam;
  
  private boolean noAccessToProcedureBodies;
  
  private int[] placeholderToParameterIndexMap;
  
  public CallableStatement(JdbcConnection conn, CallableStatementParamInfo paramInfo) throws SQLException {
    super(conn, paramInfo.nativeSql, paramInfo.dbInUse);
    this.paramInfo = paramInfo;
    this.callingStoredFunction = this.paramInfo.isFunctionCall;
    if (this.callingStoredFunction)
      ((PreparedQuery)this.query).setParameterCount(((PreparedQuery)this.query).getParameterCount() + 1); 
    this.retrieveGeneratedKeys = true;
    this.noAccessToProcedureBodies = ((Boolean)conn.getPropertySet().getBooleanProperty(PropertyKey.noAccessToProcedureBodies).getValue()).booleanValue();
  }
  
  protected static CallableStatement getInstance(JdbcConnection conn, String sql, String db, boolean isFunctionCall) throws SQLException {
    return new CallableStatement(conn, sql, db, isFunctionCall);
  }
  
  protected static CallableStatement getInstance(JdbcConnection conn, CallableStatementParamInfo paramInfo) throws SQLException {
    return new CallableStatement(conn, paramInfo);
  }
  
  private void generateParameterMap() throws SQLException {
    try {
      synchronized (checkClosed().getConnectionMutex()) {
        if (this.paramInfo == null)
          return; 
        int parameterCountFromMetaData = this.paramInfo.getParameterCount();
        if (this.callingStoredFunction)
          parameterCountFromMetaData--; 
        PreparedQuery q = (PreparedQuery)this.query;
        if (this.paramInfo != null && q.getParameterCount() != parameterCountFromMetaData) {
          this.placeholderToParameterIndexMap = new int[q.getParameterCount()];
          int startPos = this.callingStoredFunction ? StringUtils.indexOfIgnoreCase(q.getOriginalSql(), "SELECT") : StringUtils.indexOfIgnoreCase(q.getOriginalSql(), "CALL");
          if (startPos != -1) {
            int parenOpenPos = q.getOriginalSql().indexOf('(', startPos + 4);
            if (parenOpenPos != -1) {
              int parenClosePos = StringUtils.indexOfIgnoreCase(parenOpenPos, q.getOriginalSql(), ")", "'", "'", SearchMode.__FULL);
              if (parenClosePos != -1) {
                List<?> parsedParameters = StringUtils.split(q.getOriginalSql().substring(parenOpenPos + 1, parenClosePos), ",", "'\"", "'\"", true);
                int numParsedParameters = parsedParameters.size();
                if (numParsedParameters != q.getParameterCount());
                int placeholderCount = 0;
                for (int i = 0; i < numParsedParameters; i++) {
                  if (((String)parsedParameters.get(i)).equals("?"))
                    this.placeholderToParameterIndexMap[placeholderCount++] = i; 
                } 
              } 
            } 
          } 
        } 
      } 
      return;
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public CallableStatement(JdbcConnection conn, String sql, String db, boolean isFunctionCall) throws SQLException {
    super(conn, sql, db);
    this.callingStoredFunction = isFunctionCall;
    if (!this.callingStoredFunction) {
      if (!StringUtils.startsWithIgnoreCaseAndWs(sql, "CALL")) {
        fakeParameterTypes(false);
      } else {
        determineParameterTypes();
      } 
      generateParameterMap();
    } else {
      determineParameterTypes();
      generateParameterMap();
      ((PreparedQuery)this.query).setParameterCount(((PreparedQuery)this.query).getParameterCount() + 1);
    } 
    this.retrieveGeneratedKeys = true;
    this.noAccessToProcedureBodies = ((Boolean)conn.getPropertySet().getBooleanProperty(PropertyKey.noAccessToProcedureBodies).getValue()).booleanValue();
  }
  
  public void addBatch() throws SQLException {
    try {
      setOutParams();
      super.addBatch();
      return;
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  private CallableStatementParam checkIsOutputParam(int paramIndex) throws SQLException {
    try {
      synchronized (checkClosed().getConnectionMutex()) {
        if (this.callingStoredFunction) {
          if (paramIndex == 1) {
            if (this.returnValueParam == null)
              this.returnValueParam = new CallableStatementParam("", 0, false, true, MysqlType.VARCHAR.getJdbcType(), "VARCHAR", 0, 0, (short)2, 5); 
            return this.returnValueParam;
          } 
          paramIndex--;
        } 
        checkParameterIndexBounds(paramIndex);
        int localParamIndex = paramIndex - 1;
        if (this.placeholderToParameterIndexMap != null)
          localParamIndex = this.placeholderToParameterIndexMap[localParamIndex]; 
        CallableStatementParam paramDescriptor = this.paramInfo.getParameter(localParamIndex);
        if (this.noAccessToProcedureBodies) {
          paramDescriptor.isOut = true;
          paramDescriptor.isIn = true;
          paramDescriptor.inOutModifier = 2;
        } else if (!paramDescriptor.isOut) {
          throw SQLError.createSQLException(Messages.getString("CallableStatement.9", new Object[] { Integer.valueOf(paramIndex) }), "S1009", 
              getExceptionInterceptor());
        } 
        this.hasOutputParams = true;
        return paramDescriptor;
      } 
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  private void checkParameterIndexBounds(int paramIndex) throws SQLException {
    try {
      synchronized (checkClosed().getConnectionMutex()) {
        this.paramInfo.checkBounds(paramIndex);
      } 
      return;
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  private void checkStreamability() throws SQLException {
    if (this.hasOutputParams && createStreamingResultSet())
      throw SQLError.createSQLException(Messages.getString("CallableStatement.14"), "S1C00", 
          getExceptionInterceptor()); 
  }
  
  public void clearParameters() throws SQLException {
    try {
      synchronized (checkClosed().getConnectionMutex()) {
        super.clearParameters();
        try {
          if (this.outputParameterResults != null)
            this.outputParameterResults.close(); 
        } finally {
          this.outputParameterResults = null;
        } 
      } 
      return;
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  private void fakeParameterTypes(boolean isReallyProcedure) throws SQLException {
    try {
      synchronized (checkClosed().getConnectionMutex()) {
        String encoding = this.connection.getSession().getServerSession().getCharsetSettings().getMetadataEncoding();
        int collationIndex = this.connection.getSession().getServerSession().getCharsetSettings().getMetadataCollationIndex();
        Field[] fields = new Field[13];
        fields[0] = new Field("", "PROCEDURE_CAT", collationIndex, encoding, MysqlType.CHAR, 0);
        fields[1] = new Field("", "PROCEDURE_SCHEM", collationIndex, encoding, MysqlType.CHAR, 0);
        fields[2] = new Field("", "PROCEDURE_NAME", collationIndex, encoding, MysqlType.CHAR, 0);
        fields[3] = new Field("", "COLUMN_NAME", collationIndex, encoding, MysqlType.CHAR, 0);
        fields[4] = new Field("", "COLUMN_TYPE", collationIndex, encoding, MysqlType.CHAR, 0);
        fields[5] = new Field("", "DATA_TYPE", collationIndex, encoding, MysqlType.SMALLINT, 0);
        fields[6] = new Field("", "TYPE_NAME", collationIndex, encoding, MysqlType.CHAR, 0);
        fields[7] = new Field("", "PRECISION", collationIndex, encoding, MysqlType.INT, 0);
        fields[8] = new Field("", "LENGTH", collationIndex, encoding, MysqlType.INT, 0);
        fields[9] = new Field("", "SCALE", collationIndex, encoding, MysqlType.SMALLINT, 0);
        fields[10] = new Field("", "RADIX", collationIndex, encoding, MysqlType.SMALLINT, 0);
        fields[11] = new Field("", "NULLABLE", collationIndex, encoding, MysqlType.SMALLINT, 0);
        fields[12] = new Field("", "REMARKS", collationIndex, encoding, MysqlType.CHAR, 0);
        String procName = isReallyProcedure ? extractProcedureName() : null;
        byte[] procNameAsBytes = null;
        procNameAsBytes = (procName == null) ? null : StringUtils.getBytes(procName, "UTF-8");
        ArrayList<Row> resultRows = new ArrayList<>();
        for (int i = 0; i < ((PreparedQuery)this.query).getParameterCount(); i++) {
          byte[][] row = new byte[13][];
          row[0] = null;
          row[1] = null;
          row[2] = procNameAsBytes;
          row[3] = s2b(String.valueOf(i));
          row[4] = s2b(String.valueOf(1));
          row[5] = s2b(String.valueOf(MysqlType.VARCHAR.getJdbcType()));
          row[6] = s2b(MysqlType.VARCHAR.getName());
          row[7] = s2b(Integer.toString(65535));
          row[8] = s2b(Integer.toString(65535));
          row[9] = s2b(Integer.toString(0));
          row[10] = s2b(Integer.toString(10));
          row[11] = s2b(Integer.toString(2));
          row[12] = null;
          resultRows.add(new ByteArrayRow(row, getExceptionInterceptor()));
        } 
        ResultSetImpl resultSetImpl = this.resultSetFactory.createFromResultsetRows(1007, 1004, (ResultsetRows)new ResultsetRowsStatic(resultRows, (ColumnDefinition)new DefaultColumnDefinition(fields)));
        convertGetProcedureColumnsToInternalDescriptors((ResultSet)resultSetImpl);
      } 
      return;
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  private void determineParameterTypes() throws SQLException {
    try {
      synchronized (checkClosed().getConnectionMutex()) {
        ResultSet paramTypesRs = null;
        try {
          String procName = extractProcedureName();
          String quotedId = this.session.getIdentifierQuoteString();
          List<?> parseList = StringUtils.splitDBdotName(procName, "", quotedId, this.session.getServerSession().isNoBackslashEscapesSet());
          String tmpDb = "";
          if (parseList.size() == 2) {
            tmpDb = (String)parseList.get(0);
            procName = (String)parseList.get(1);
          } 
          DatabaseMetaData dbmd = this.connection.getMetaData();
          boolean useDb = false;
          if (tmpDb.length() <= 0)
            useDb = true; 
          paramTypesRs = (this.session.getPropertySet().getEnumProperty(PropertyKey.databaseTerm).getValue() == PropertyDefinitions.DatabaseTerm.SCHEMA) ? dbmd.getProcedureColumns(null, useDb ? getCurrentDatabase() : tmpDb, procName, "%") : dbmd.getProcedureColumns(useDb ? getCurrentDatabase() : tmpDb, null, procName, "%");
          boolean hasResults = false;
          try {
            if (paramTypesRs.next()) {
              paramTypesRs.previous();
              hasResults = true;
            } 
          } catch (Exception exception) {}
          if (hasResults) {
            convertGetProcedureColumnsToInternalDescriptors(paramTypesRs);
          } else {
            fakeParameterTypes(true);
          } 
        } finally {
          SQLException sqlExRethrow = null;
          if (paramTypesRs != null) {
            try {
              paramTypesRs.close();
            } catch (SQLException sqlEx) {
              sqlExRethrow = sqlEx;
            } 
            paramTypesRs = null;
          } 
          if (sqlExRethrow != null)
            throw sqlExRethrow; 
        } 
      } 
      return;
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  private void convertGetProcedureColumnsToInternalDescriptors(ResultSet paramTypesRs) throws SQLException {
    try {
      synchronized (checkClosed().getConnectionMutex()) {
        this.paramInfo = new CallableStatementParamInfo(paramTypesRs);
      } 
      return;
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public boolean execute() throws SQLException {
    try {
      synchronized (checkClosed().getConnectionMutex()) {
        boolean returnVal = false;
        checkStreamability();
        setInOutParamsOnServer();
        setOutParams();
        returnVal = super.execute();
        if (this.callingStoredFunction) {
          this.functionReturnValueResults = this.results;
          this.functionReturnValueResults.next();
          this.results = null;
        } 
        retrieveOutParams();
        if (!this.callingStoredFunction)
          return returnVal; 
        return false;
      } 
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public ResultSet executeQuery() throws SQLException {
    try {
      synchronized (checkClosed().getConnectionMutex()) {
        checkStreamability();
        ResultSet execResults = null;
        setInOutParamsOnServer();
        setOutParams();
        execResults = super.executeQuery();
        retrieveOutParams();
        return execResults;
      } 
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public int executeUpdate() throws SQLException {
    try {
      return Util.truncateAndConvertToInt(executeLargeUpdate());
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  private String extractProcedureName() throws SQLException {
    String sanitizedSql = StringUtils.stripCommentsAndHints(((PreparedQuery)this.query).getOriginalSql(), "`'\"", "`'\"", 
        !this.session.getServerSession().isNoBackslashEscapesSet());
    int endCallIndex = StringUtils.indexOfIgnoreCase(sanitizedSql, "CALL ");
    int offset = 5;
    if (endCallIndex == -1) {
      endCallIndex = StringUtils.indexOfIgnoreCase(sanitizedSql, "SELECT ");
      offset = 7;
    } 
    if (endCallIndex != -1) {
      StringBuilder nameBuf = new StringBuilder();
      String trimmedStatement = sanitizedSql.substring(endCallIndex + offset).trim();
      int statementLength = trimmedStatement.length();
      for (int i = 0; i < statementLength; i++) {
        char c = trimmedStatement.charAt(i);
        if (Character.isWhitespace(c) || c == '(' || c == '?')
          break; 
        nameBuf.append(c);
      } 
      return nameBuf.toString();
    } 
    throw SQLError.createSQLException(Messages.getString("CallableStatement.1"), "S1000", getExceptionInterceptor());
  }
  
  protected String fixParameterName(String paramNameIn) throws SQLException {
    try {
      synchronized (checkClosed().getConnectionMutex()) {
        if (paramNameIn == null)
          paramNameIn = "nullpn"; 
        if (this.noAccessToProcedureBodies)
          throw SQLError.createSQLException(Messages.getString("CallableStatement.23"), "S1009", 
              getExceptionInterceptor()); 
        return mangleParameterName(paramNameIn);
      } 
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public Array getArray(int i) throws SQLException {
    try {
      synchronized (checkClosed().getConnectionMutex()) {
        ResultSetInternalMethods rs = getOutputParameters(i);
        Array retValue = rs.getArray(mapOutputParameterIndexToRsIndex(i));
        this.outputParamWasNull = rs.wasNull();
        return retValue;
      } 
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public Array getArray(String parameterName) throws SQLException {
    try {
      synchronized (checkClosed().getConnectionMutex()) {
        ResultSetInternalMethods rs = getOutputParameters(0);
        Array retValue = rs.getArray(fixParameterName(parameterName));
        this.outputParamWasNull = rs.wasNull();
        return retValue;
      } 
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public BigDecimal getBigDecimal(int parameterIndex) throws SQLException {
    try {
      synchronized (checkClosed().getConnectionMutex()) {
        ResultSetInternalMethods rs = getOutputParameters(parameterIndex);
        BigDecimal retValue = rs.getBigDecimal(mapOutputParameterIndexToRsIndex(parameterIndex));
        this.outputParamWasNull = rs.wasNull();
        return retValue;
      } 
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  @Deprecated
  public BigDecimal getBigDecimal(int parameterIndex, int scale) throws SQLException {
    try {
      synchronized (checkClosed().getConnectionMutex()) {
        ResultSetInternalMethods rs = getOutputParameters(parameterIndex);
        BigDecimal retValue = rs.getBigDecimal(mapOutputParameterIndexToRsIndex(parameterIndex), scale);
        this.outputParamWasNull = rs.wasNull();
        return retValue;
      } 
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public BigDecimal getBigDecimal(String parameterName) throws SQLException {
    try {
      synchronized (checkClosed().getConnectionMutex()) {
        ResultSetInternalMethods rs = getOutputParameters(0);
        BigDecimal retValue = rs.getBigDecimal(fixParameterName(parameterName));
        this.outputParamWasNull = rs.wasNull();
        return retValue;
      } 
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public Blob getBlob(int parameterIndex) throws SQLException {
    try {
      synchronized (checkClosed().getConnectionMutex()) {
        ResultSetInternalMethods rs = getOutputParameters(parameterIndex);
        Blob retValue = rs.getBlob(mapOutputParameterIndexToRsIndex(parameterIndex));
        this.outputParamWasNull = rs.wasNull();
        return retValue;
      } 
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public Blob getBlob(String parameterName) throws SQLException {
    try {
      synchronized (checkClosed().getConnectionMutex()) {
        ResultSetInternalMethods rs = getOutputParameters(0);
        Blob retValue = rs.getBlob(fixParameterName(parameterName));
        this.outputParamWasNull = rs.wasNull();
        return retValue;
      } 
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public boolean getBoolean(int parameterIndex) throws SQLException {
    try {
      synchronized (checkClosed().getConnectionMutex()) {
        ResultSetInternalMethods rs = getOutputParameters(parameterIndex);
        boolean retValue = rs.getBoolean(mapOutputParameterIndexToRsIndex(parameterIndex));
        this.outputParamWasNull = rs.wasNull();
        return retValue;
      } 
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public boolean getBoolean(String parameterName) throws SQLException {
    try {
      synchronized (checkClosed().getConnectionMutex()) {
        ResultSetInternalMethods rs = getOutputParameters(0);
        boolean retValue = rs.getBoolean(fixParameterName(parameterName));
        this.outputParamWasNull = rs.wasNull();
        return retValue;
      } 
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public byte getByte(int parameterIndex) throws SQLException {
    try {
      synchronized (checkClosed().getConnectionMutex()) {
        ResultSetInternalMethods rs = getOutputParameters(parameterIndex);
        byte retValue = rs.getByte(mapOutputParameterIndexToRsIndex(parameterIndex));
        this.outputParamWasNull = rs.wasNull();
        return retValue;
      } 
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public byte getByte(String parameterName) throws SQLException {
    try {
      synchronized (checkClosed().getConnectionMutex()) {
        ResultSetInternalMethods rs = getOutputParameters(0);
        byte retValue = rs.getByte(fixParameterName(parameterName));
        this.outputParamWasNull = rs.wasNull();
        return retValue;
      } 
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public byte[] getBytes(int parameterIndex) throws SQLException {
    try {
      synchronized (checkClosed().getConnectionMutex()) {
        ResultSetInternalMethods rs = getOutputParameters(parameterIndex);
        byte[] retValue = rs.getBytes(mapOutputParameterIndexToRsIndex(parameterIndex));
        this.outputParamWasNull = rs.wasNull();
        return retValue;
      } 
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public byte[] getBytes(String parameterName) throws SQLException {
    try {
      synchronized (checkClosed().getConnectionMutex()) {
        ResultSetInternalMethods rs = getOutputParameters(0);
        byte[] retValue = rs.getBytes(fixParameterName(parameterName));
        this.outputParamWasNull = rs.wasNull();
        return retValue;
      } 
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public Clob getClob(int parameterIndex) throws SQLException {
    try {
      synchronized (checkClosed().getConnectionMutex()) {
        ResultSetInternalMethods rs = getOutputParameters(parameterIndex);
        Clob retValue = rs.getClob(mapOutputParameterIndexToRsIndex(parameterIndex));
        this.outputParamWasNull = rs.wasNull();
        return retValue;
      } 
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public Clob getClob(String parameterName) throws SQLException {
    try {
      synchronized (checkClosed().getConnectionMutex()) {
        ResultSetInternalMethods rs = getOutputParameters(0);
        Clob retValue = rs.getClob(fixParameterName(parameterName));
        this.outputParamWasNull = rs.wasNull();
        return retValue;
      } 
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public Date getDate(int parameterIndex) throws SQLException {
    try {
      synchronized (checkClosed().getConnectionMutex()) {
        ResultSetInternalMethods rs = getOutputParameters(parameterIndex);
        Date retValue = rs.getDate(mapOutputParameterIndexToRsIndex(parameterIndex));
        this.outputParamWasNull = rs.wasNull();
        return retValue;
      } 
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public Date getDate(int parameterIndex, Calendar cal) throws SQLException {
    try {
      synchronized (checkClosed().getConnectionMutex()) {
        ResultSetInternalMethods rs = getOutputParameters(parameterIndex);
        Date retValue = rs.getDate(mapOutputParameterIndexToRsIndex(parameterIndex), cal);
        this.outputParamWasNull = rs.wasNull();
        return retValue;
      } 
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public Date getDate(String parameterName) throws SQLException {
    try {
      synchronized (checkClosed().getConnectionMutex()) {
        ResultSetInternalMethods rs = getOutputParameters(0);
        Date retValue = rs.getDate(fixParameterName(parameterName));
        this.outputParamWasNull = rs.wasNull();
        return retValue;
      } 
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public Date getDate(String parameterName, Calendar cal) throws SQLException {
    try {
      synchronized (checkClosed().getConnectionMutex()) {
        ResultSetInternalMethods rs = getOutputParameters(0);
        Date retValue = rs.getDate(fixParameterName(parameterName), cal);
        this.outputParamWasNull = rs.wasNull();
        return retValue;
      } 
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public double getDouble(int parameterIndex) throws SQLException {
    try {
      synchronized (checkClosed().getConnectionMutex()) {
        ResultSetInternalMethods rs = getOutputParameters(parameterIndex);
        double retValue = rs.getDouble(mapOutputParameterIndexToRsIndex(parameterIndex));
        this.outputParamWasNull = rs.wasNull();
        return retValue;
      } 
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public double getDouble(String parameterName) throws SQLException {
    try {
      synchronized (checkClosed().getConnectionMutex()) {
        ResultSetInternalMethods rs = getOutputParameters(0);
        double retValue = rs.getDouble(fixParameterName(parameterName));
        this.outputParamWasNull = rs.wasNull();
        return retValue;
      } 
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public float getFloat(int parameterIndex) throws SQLException {
    try {
      synchronized (checkClosed().getConnectionMutex()) {
        ResultSetInternalMethods rs = getOutputParameters(parameterIndex);
        float retValue = rs.getFloat(mapOutputParameterIndexToRsIndex(parameterIndex));
        this.outputParamWasNull = rs.wasNull();
        return retValue;
      } 
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public float getFloat(String parameterName) throws SQLException {
    try {
      synchronized (checkClosed().getConnectionMutex()) {
        ResultSetInternalMethods rs = getOutputParameters(0);
        float retValue = rs.getFloat(fixParameterName(parameterName));
        this.outputParamWasNull = rs.wasNull();
        return retValue;
      } 
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public int getInt(int parameterIndex) throws SQLException {
    try {
      synchronized (checkClosed().getConnectionMutex()) {
        ResultSetInternalMethods rs = getOutputParameters(parameterIndex);
        int retValue = rs.getInt(mapOutputParameterIndexToRsIndex(parameterIndex));
        this.outputParamWasNull = rs.wasNull();
        return retValue;
      } 
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public int getInt(String parameterName) throws SQLException {
    try {
      synchronized (checkClosed().getConnectionMutex()) {
        ResultSetInternalMethods rs = getOutputParameters(0);
        int retValue = rs.getInt(fixParameterName(parameterName));
        this.outputParamWasNull = rs.wasNull();
        return retValue;
      } 
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public long getLong(int parameterIndex) throws SQLException {
    try {
      synchronized (checkClosed().getConnectionMutex()) {
        ResultSetInternalMethods rs = getOutputParameters(parameterIndex);
        long retValue = rs.getLong(mapOutputParameterIndexToRsIndex(parameterIndex));
        this.outputParamWasNull = rs.wasNull();
        return retValue;
      } 
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public long getLong(String parameterName) throws SQLException {
    try {
      synchronized (checkClosed().getConnectionMutex()) {
        ResultSetInternalMethods rs = getOutputParameters(0);
        long retValue = rs.getLong(fixParameterName(parameterName));
        this.outputParamWasNull = rs.wasNull();
        return retValue;
      } 
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  protected int getNamedParamIndex(String paramName, boolean forOut) throws SQLException {
    try {
      synchronized (checkClosed().getConnectionMutex()) {
        if (this.noAccessToProcedureBodies)
          throw SQLError.createSQLException("No access to parameters by name when connection has been configured not to access procedure bodies", "S1009", 
              getExceptionInterceptor()); 
        if (paramName == null || paramName.length() == 0)
          throw SQLError.createSQLException(Messages.getString("CallableStatement.2"), "S1009", 
              getExceptionInterceptor()); 
        CallableStatementParam namedParamInfo;
        if (this.paramInfo == null || (namedParamInfo = this.paramInfo.getParameter(paramName)) == null)
          throw SQLError.createSQLException(Messages.getString("CallableStatement.3", new Object[] { paramName }), "S1009", 
              getExceptionInterceptor()); 
        if (forOut && !namedParamInfo.isOut)
          throw SQLError.createSQLException(Messages.getString("CallableStatement.5", new Object[] { paramName }), "S1009", 
              getExceptionInterceptor()); 
        if (this.placeholderToParameterIndexMap == null)
          return namedParamInfo.index + 1; 
        for (int i = 0; i < this.placeholderToParameterIndexMap.length; i++) {
          if (this.placeholderToParameterIndexMap[i] == namedParamInfo.index)
            return i + 1; 
        } 
        throw SQLError.createSQLException(Messages.getString("CallableStatement.6", new Object[] { paramName }), "S1009", 
            getExceptionInterceptor());
      } 
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public Object getObject(int parameterIndex) throws SQLException {
    try {
      synchronized (checkClosed().getConnectionMutex()) {
        CallableStatementParam paramDescriptor = checkIsOutputParam(parameterIndex);
        ResultSetInternalMethods rs = getOutputParameters(parameterIndex);
        Object retVal = rs.getObjectStoredProc(mapOutputParameterIndexToRsIndex(parameterIndex), paramDescriptor.desiredMysqlType.getJdbcType());
        this.outputParamWasNull = rs.wasNull();
        return retVal;
      } 
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public Object getObject(int parameterIndex, Map<String, Class<?>> map) throws SQLException {
    try {
      synchronized (checkClosed().getConnectionMutex()) {
        ResultSetInternalMethods rs = getOutputParameters(parameterIndex);
        Object retVal = rs.getObject(mapOutputParameterIndexToRsIndex(parameterIndex), map);
        this.outputParamWasNull = rs.wasNull();
        return retVal;
      } 
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public Object getObject(String parameterName) throws SQLException {
    try {
      synchronized (checkClosed().getConnectionMutex()) {
        ResultSetInternalMethods rs = getOutputParameters(0);
        Object retValue = rs.getObject(fixParameterName(parameterName));
        this.outputParamWasNull = rs.wasNull();
        return retValue;
      } 
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public Object getObject(String parameterName, Map<String, Class<?>> map) throws SQLException {
    try {
      synchronized (checkClosed().getConnectionMutex()) {
        ResultSetInternalMethods rs = getOutputParameters(0);
        Object retValue = rs.getObject(fixParameterName(parameterName), map);
        this.outputParamWasNull = rs.wasNull();
        return retValue;
      } 
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public <T> T getObject(int parameterIndex, Class<T> type) throws SQLException {
    try {
      synchronized (checkClosed().getConnectionMutex()) {
        ResultSetInternalMethods rs = getOutputParameters(parameterIndex);
        T retVal = (T)((ResultSetImpl)rs).getObject(mapOutputParameterIndexToRsIndex(parameterIndex), type);
        this.outputParamWasNull = rs.wasNull();
        return retVal;
      } 
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public <T> T getObject(String parameterName, Class<T> type) throws SQLException {
    try {
      synchronized (checkClosed().getConnectionMutex()) {
        ResultSetInternalMethods rs = getOutputParameters(0);
        T retValue = (T)((ResultSetImpl)rs).getObject(fixParameterName(parameterName), type);
        this.outputParamWasNull = rs.wasNull();
        return retValue;
      } 
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  protected ResultSetInternalMethods getOutputParameters(int paramIndex) throws SQLException {
    try {
      synchronized (checkClosed().getConnectionMutex()) {
        this.outputParamWasNull = false;
        if (paramIndex == 1 && this.callingStoredFunction && this.returnValueParam != null)
          return this.functionReturnValueResults; 
        if (this.outputParameterResults == null) {
          if (this.paramInfo.numberOfParameters() == 0)
            throw SQLError.createSQLException(Messages.getString("CallableStatement.7"), "S1009", 
                getExceptionInterceptor()); 
          throw SQLError.createSQLException(Messages.getString("CallableStatement.8"), "S1000", 
              getExceptionInterceptor());
        } 
        return this.outputParameterResults;
      } 
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public ParameterMetaData getParameterMetaData() throws SQLException {
    try {
      synchronized (checkClosed().getConnectionMutex()) {
        if (this.placeholderToParameterIndexMap == null)
          return this.paramInfo; 
        return new CallableStatementParamInfo(this.paramInfo);
      } 
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public Ref getRef(int parameterIndex) throws SQLException {
    try {
      synchronized (checkClosed().getConnectionMutex()) {
        ResultSetInternalMethods rs = getOutputParameters(parameterIndex);
        Ref retValue = rs.getRef(mapOutputParameterIndexToRsIndex(parameterIndex));
        this.outputParamWasNull = rs.wasNull();
        return retValue;
      } 
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public Ref getRef(String parameterName) throws SQLException {
    try {
      synchronized (checkClosed().getConnectionMutex()) {
        ResultSetInternalMethods rs = getOutputParameters(0);
        Ref retValue = rs.getRef(fixParameterName(parameterName));
        this.outputParamWasNull = rs.wasNull();
        return retValue;
      } 
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public short getShort(int parameterIndex) throws SQLException {
    try {
      synchronized (checkClosed().getConnectionMutex()) {
        ResultSetInternalMethods rs = getOutputParameters(parameterIndex);
        short retValue = rs.getShort(mapOutputParameterIndexToRsIndex(parameterIndex));
        this.outputParamWasNull = rs.wasNull();
        return retValue;
      } 
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public short getShort(String parameterName) throws SQLException {
    try {
      synchronized (checkClosed().getConnectionMutex()) {
        ResultSetInternalMethods rs = getOutputParameters(0);
        short retValue = rs.getShort(fixParameterName(parameterName));
        this.outputParamWasNull = rs.wasNull();
        return retValue;
      } 
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public String getString(int parameterIndex) throws SQLException {
    try {
      synchronized (checkClosed().getConnectionMutex()) {
        ResultSetInternalMethods rs = getOutputParameters(parameterIndex);
        String retValue = rs.getString(mapOutputParameterIndexToRsIndex(parameterIndex));
        this.outputParamWasNull = rs.wasNull();
        return retValue;
      } 
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public String getString(String parameterName) throws SQLException {
    try {
      synchronized (checkClosed().getConnectionMutex()) {
        ResultSetInternalMethods rs = getOutputParameters(0);
        String retValue = rs.getString(fixParameterName(parameterName));
        this.outputParamWasNull = rs.wasNull();
        return retValue;
      } 
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public Time getTime(int parameterIndex) throws SQLException {
    try {
      synchronized (checkClosed().getConnectionMutex()) {
        ResultSetInternalMethods rs = getOutputParameters(parameterIndex);
        Time retValue = rs.getTime(mapOutputParameterIndexToRsIndex(parameterIndex));
        this.outputParamWasNull = rs.wasNull();
        return retValue;
      } 
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public Time getTime(int parameterIndex, Calendar cal) throws SQLException {
    try {
      synchronized (checkClosed().getConnectionMutex()) {
        ResultSetInternalMethods rs = getOutputParameters(parameterIndex);
        Time retValue = rs.getTime(mapOutputParameterIndexToRsIndex(parameterIndex), cal);
        this.outputParamWasNull = rs.wasNull();
        return retValue;
      } 
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public Time getTime(String parameterName) throws SQLException {
    try {
      synchronized (checkClosed().getConnectionMutex()) {
        ResultSetInternalMethods rs = getOutputParameters(0);
        Time retValue = rs.getTime(fixParameterName(parameterName));
        this.outputParamWasNull = rs.wasNull();
        return retValue;
      } 
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public Time getTime(String parameterName, Calendar cal) throws SQLException {
    try {
      synchronized (checkClosed().getConnectionMutex()) {
        ResultSetInternalMethods rs = getOutputParameters(0);
        Time retValue = rs.getTime(fixParameterName(parameterName), cal);
        this.outputParamWasNull = rs.wasNull();
        return retValue;
      } 
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public Timestamp getTimestamp(int parameterIndex) throws SQLException {
    try {
      synchronized (checkClosed().getConnectionMutex()) {
        ResultSetInternalMethods rs = getOutputParameters(parameterIndex);
        Timestamp retValue = rs.getTimestamp(mapOutputParameterIndexToRsIndex(parameterIndex));
        this.outputParamWasNull = rs.wasNull();
        return retValue;
      } 
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public Timestamp getTimestamp(int parameterIndex, Calendar cal) throws SQLException {
    try {
      synchronized (checkClosed().getConnectionMutex()) {
        ResultSetInternalMethods rs = getOutputParameters(parameterIndex);
        Timestamp retValue = rs.getTimestamp(mapOutputParameterIndexToRsIndex(parameterIndex), cal);
        this.outputParamWasNull = rs.wasNull();
        return retValue;
      } 
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public Timestamp getTimestamp(String parameterName) throws SQLException {
    try {
      synchronized (checkClosed().getConnectionMutex()) {
        ResultSetInternalMethods rs = getOutputParameters(0);
        Timestamp retValue = rs.getTimestamp(fixParameterName(parameterName));
        this.outputParamWasNull = rs.wasNull();
        return retValue;
      } 
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public Timestamp getTimestamp(String parameterName, Calendar cal) throws SQLException {
    try {
      synchronized (checkClosed().getConnectionMutex()) {
        ResultSetInternalMethods rs = getOutputParameters(0);
        Timestamp retValue = rs.getTimestamp(fixParameterName(parameterName), cal);
        this.outputParamWasNull = rs.wasNull();
        return retValue;
      } 
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public URL getURL(int parameterIndex) throws SQLException {
    try {
      synchronized (checkClosed().getConnectionMutex()) {
        ResultSetInternalMethods rs = getOutputParameters(parameterIndex);
        URL retValue = rs.getURL(mapOutputParameterIndexToRsIndex(parameterIndex));
        this.outputParamWasNull = rs.wasNull();
        return retValue;
      } 
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public URL getURL(String parameterName) throws SQLException {
    try {
      synchronized (checkClosed().getConnectionMutex()) {
        ResultSetInternalMethods rs = getOutputParameters(0);
        URL retValue = rs.getURL(fixParameterName(parameterName));
        this.outputParamWasNull = rs.wasNull();
        return retValue;
      } 
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  protected int mapOutputParameterIndexToRsIndex(int paramIndex) throws SQLException {
    try {
      synchronized (checkClosed().getConnectionMutex()) {
        if (this.returnValueParam != null && paramIndex == 1)
          return 1; 
        checkParameterIndexBounds(paramIndex);
        int localParamIndex = paramIndex - 1;
        if (this.placeholderToParameterIndexMap != null)
          localParamIndex = this.placeholderToParameterIndexMap[localParamIndex]; 
        int rsIndex = this.parameterIndexToRsIndex[localParamIndex];
        if (rsIndex == Integer.MIN_VALUE)
          throw SQLError.createSQLException(Messages.getString("CallableStatement.21", new Object[] { Integer.valueOf(paramIndex) }), "S1009", 
              getExceptionInterceptor()); 
        return rsIndex + 1;
      } 
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  protected void registerOutParameter(int parameterIndex, MysqlType mysqlType) throws SQLException {
    CallableStatementParam paramDescriptor = checkIsOutputParam(parameterIndex);
    paramDescriptor.desiredMysqlType = mysqlType;
  }
  
  public void registerOutParameter(int parameterIndex, int sqlType) throws SQLException {
    try {
      try {
        MysqlType mt = MysqlType.getByJdbcType(sqlType);
        registerOutParameter(parameterIndex, mt);
      } catch (FeatureNotAvailableException nae) {
        throw SQLError.createSQLFeatureNotSupportedException(Messages.getString("Statement.UnsupportedSQLType") + JDBCType.valueOf(sqlType), "S1C00", 
            getExceptionInterceptor());
      } 
      return;
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public void registerOutParameter(int parameterIndex, SQLType sqlType) throws SQLException {
    try {
      if (sqlType instanceof MysqlType) {
        registerOutParameter(parameterIndex, (MysqlType)sqlType);
      } else {
        registerOutParameter(parameterIndex, sqlType.getVendorTypeNumber().intValue());
      } 
      return;
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  protected void registerOutParameter(int parameterIndex, MysqlType mysqlType, int scale) throws SQLException {
    registerOutParameter(parameterIndex, mysqlType);
  }
  
  public void registerOutParameter(int parameterIndex, int sqlType, int scale) throws SQLException {
    try {
      registerOutParameter(parameterIndex, sqlType);
      return;
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public void registerOutParameter(int parameterIndex, SQLType sqlType, int scale) throws SQLException {
    try {
      if (sqlType instanceof MysqlType) {
        registerOutParameter(parameterIndex, (MysqlType)sqlType, scale);
      } else {
        registerOutParameter(parameterIndex, sqlType.getVendorTypeNumber().intValue(), scale);
      } 
      return;
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  protected void registerOutParameter(int parameterIndex, MysqlType mysqlType, String typeName) throws SQLException {
    registerOutParameter(parameterIndex, mysqlType);
  }
  
  public void registerOutParameter(int parameterIndex, int sqlType, String typeName) throws SQLException {
    try {
      try {
        MysqlType mt = MysqlType.getByJdbcType(sqlType);
        registerOutParameter(parameterIndex, mt, typeName);
      } catch (FeatureNotAvailableException nae) {
        throw SQLError.createSQLFeatureNotSupportedException(Messages.getString("Statement.UnsupportedSQLType") + JDBCType.valueOf(sqlType), "S1C00", 
            getExceptionInterceptor());
      } 
      return;
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public void registerOutParameter(int parameterIndex, SQLType sqlType, String typeName) throws SQLException {
    try {
      if (sqlType instanceof MysqlType) {
        registerOutParameter(parameterIndex, (MysqlType)sqlType, typeName);
      } else {
        registerOutParameter(parameterIndex, sqlType.getVendorTypeNumber().intValue(), typeName);
      } 
      return;
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public void registerOutParameter(String parameterName, int sqlType) throws SQLException {
    try {
      synchronized (checkClosed().getConnectionMutex()) {
        registerOutParameter(getNamedParamIndex(parameterName, true), sqlType);
      } 
      return;
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public void registerOutParameter(String parameterName, SQLType sqlType) throws SQLException {
    try {
      if (sqlType instanceof MysqlType) {
        registerOutParameter(getNamedParamIndex(parameterName, true), (MysqlType)sqlType);
      } else {
        registerOutParameter(getNamedParamIndex(parameterName, true), sqlType.getVendorTypeNumber().intValue());
      } 
      return;
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public void registerOutParameter(String parameterName, int sqlType, int scale) throws SQLException {
    try {
      registerOutParameter(getNamedParamIndex(parameterName, true), sqlType, scale);
      return;
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public void registerOutParameter(String parameterName, SQLType sqlType, int scale) throws SQLException {
    try {
      if (sqlType instanceof MysqlType) {
        registerOutParameter(getNamedParamIndex(parameterName, true), (MysqlType)sqlType, scale);
      } else {
        registerOutParameter(getNamedParamIndex(parameterName, true), sqlType.getVendorTypeNumber().intValue(), scale);
      } 
      return;
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public void registerOutParameter(String parameterName, int sqlType, String typeName) throws SQLException {
    try {
      registerOutParameter(getNamedParamIndex(parameterName, true), sqlType, typeName);
      return;
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public void registerOutParameter(String parameterName, SQLType sqlType, String typeName) throws SQLException {
    try {
      if (sqlType instanceof MysqlType) {
        registerOutParameter(getNamedParamIndex(parameterName, true), (MysqlType)sqlType, typeName);
      } else {
        registerOutParameter(parameterName, sqlType.getVendorTypeNumber().intValue(), typeName);
      } 
      return;
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  private void retrieveOutParams() throws SQLException {
    try {
      synchronized (checkClosed().getConnectionMutex()) {
        int numParameters = this.paramInfo.numberOfParameters();
        this.parameterIndexToRsIndex = new int[numParameters];
        for (int i = 0; i < numParameters; i++)
          this.parameterIndexToRsIndex[i] = Integer.MIN_VALUE; 
        int localParamIndex = 0;
        if (numParameters > 0) {
          StringBuilder outParameterQuery = new StringBuilder("SELECT ");
          boolean firstParam = true;
          boolean hadOutputParams = false;
          for (Iterator<CallableStatementParam> paramIter = this.paramInfo.iterator(); paramIter.hasNext(); ) {
            CallableStatementParam retrParamInfo = paramIter.next();
            if (retrParamInfo.isOut) {
              hadOutputParams = true;
              this.parameterIndexToRsIndex[retrParamInfo.index] = localParamIndex++;
              if (retrParamInfo.paramName == null)
                retrParamInfo.paramName = "nullnp" + retrParamInfo.index; 
              String outParameterName = mangleParameterName(retrParamInfo.paramName);
              if (!firstParam) {
                outParameterQuery.append(",");
              } else {
                firstParam = false;
              } 
              if (!outParameterName.startsWith("@"))
                outParameterQuery.append('@'); 
              outParameterQuery.append(outParameterName);
            } 
          } 
          if (hadOutputParams) {
            Statement outParameterStmt = null;
            ResultSet outParamRs = null;
            try {
              outParameterStmt = this.connection.createStatement();
              outParamRs = outParameterStmt.executeQuery(outParameterQuery.toString());
              this.outputParameterResults = (ResultSetInternalMethods)this.resultSetFactory.createFromResultsetRows(outParamRs.getConcurrency(), outParamRs.getType(), ((ResultSetInternalMethods)outParamRs)
                  .getRows());
              if (!this.outputParameterResults.next()) {
                this.outputParameterResults.close();
                this.outputParameterResults = null;
              } 
            } finally {
              if (outParameterStmt != null)
                outParameterStmt.close(); 
            } 
          } else {
            this.outputParameterResults = null;
          } 
        } else {
          this.outputParameterResults = null;
        } 
      } 
      return;
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public void setAsciiStream(String parameterName, InputStream x, int length) throws SQLException {
    try {
      setAsciiStream(getNamedParamIndex(parameterName, false), x, length);
      return;
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public void setBigDecimal(String parameterName, BigDecimal x) throws SQLException {
    try {
      setBigDecimal(getNamedParamIndex(parameterName, false), x);
      return;
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public void setBinaryStream(String parameterName, InputStream x, int length) throws SQLException {
    try {
      setBinaryStream(getNamedParamIndex(parameterName, false), x, length);
      return;
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public void setBoolean(String parameterName, boolean x) throws SQLException {
    try {
      setBoolean(getNamedParamIndex(parameterName, false), x);
      return;
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public void setByte(String parameterName, byte x) throws SQLException {
    try {
      setByte(getNamedParamIndex(parameterName, false), x);
      return;
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public void setBytes(String parameterName, byte[] x) throws SQLException {
    try {
      setBytes(getNamedParamIndex(parameterName, false), x);
      return;
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public void setCharacterStream(String parameterName, Reader reader, int length) throws SQLException {
    try {
      setCharacterStream(getNamedParamIndex(parameterName, false), reader, length);
      return;
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public void setDate(String parameterName, Date x) throws SQLException {
    try {
      setDate(getNamedParamIndex(parameterName, false), x);
      return;
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public void setDate(String parameterName, Date x, Calendar cal) throws SQLException {
    try {
      setDate(getNamedParamIndex(parameterName, false), x, cal);
      return;
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public void setDouble(String parameterName, double x) throws SQLException {
    try {
      setDouble(getNamedParamIndex(parameterName, false), x);
      return;
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public void setFloat(String parameterName, float x) throws SQLException {
    try {
      setFloat(getNamedParamIndex(parameterName, false), x);
      return;
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  private void setInOutParamsOnServer() throws SQLException {
    try {
      synchronized (checkClosed().getConnectionMutex()) {
        if (this.paramInfo.numParameters > 0)
          for (Iterator<CallableStatementParam> paramIter = this.paramInfo.iterator(); paramIter.hasNext(); ) {
            CallableStatementParam inParamInfo = paramIter.next();
            if (inParamInfo.isOut && inParamInfo.isIn) {
              if (inParamInfo.paramName == null)
                inParamInfo.paramName = "nullnp" + inParamInfo.index; 
              String inOutParameterName = mangleParameterName(inParamInfo.paramName);
              StringBuilder queryBuf = new StringBuilder(4 + inOutParameterName.length() + 1 + 1);
              queryBuf.append("SET ");
              queryBuf.append(inOutParameterName);
              queryBuf.append("=?");
              ClientPreparedStatement setPstmt = null;
              try {
                setPstmt = this.connection.clientPrepareStatement(queryBuf.toString()).<ClientPreparedStatement>unwrap(ClientPreparedStatement.class);
                setPstmt.getQueryBindings().setFromBindValue(0, ((PreparedQuery)this.query).getQueryBindings().getBindValues()[inParamInfo.index]);
                setPstmt.executeUpdate();
              } finally {
                if (setPstmt != null)
                  setPstmt.close(); 
              } 
            } 
          }  
      } 
      return;
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public void setInt(String parameterName, int x) throws SQLException {
    try {
      setInt(getNamedParamIndex(parameterName, false), x);
      return;
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public void setLong(String parameterName, long x) throws SQLException {
    try {
      setLong(getNamedParamIndex(parameterName, false), x);
      return;
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public void setNull(String parameterName, int sqlType) throws SQLException {
    try {
      setNull(getNamedParamIndex(parameterName, false), sqlType);
      return;
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public void setNull(String parameterName, int sqlType, String typeName) throws SQLException {
    try {
      setNull(getNamedParamIndex(parameterName, false), sqlType, typeName);
      return;
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public void setObject(String parameterName, Object x) throws SQLException {
    try {
      setObject(getNamedParamIndex(parameterName, false), x);
      return;
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public void setObject(String parameterName, Object x, int targetSqlType) throws SQLException {
    try {
      setObject(getNamedParamIndex(parameterName, false), x, targetSqlType);
      return;
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public void setObject(String parameterName, Object x, SQLType targetSqlType) throws SQLException {
    try {
      synchronized (checkClosed().getConnectionMutex()) {
        setObject(getNamedParamIndex(parameterName, false), x, targetSqlType);
      } 
      return;
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public void setObject(String parameterName, Object x, int targetSqlType, int scale) throws SQLException {
    try {
      setObject(getNamedParamIndex(parameterName, false), x, targetSqlType, scale);
      return;
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public void setObject(String parameterName, Object x, SQLType targetSqlType, int scaleOrLength) throws SQLException {
    try {
      synchronized (checkClosed().getConnectionMutex()) {
        setObject(getNamedParamIndex(parameterName, false), x, targetSqlType, scaleOrLength);
      } 
      return;
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  private void setOutParams() throws SQLException {
    try {
      synchronized (checkClosed().getConnectionMutex()) {
        if (this.paramInfo.numParameters > 0)
          for (Iterator<CallableStatementParam> paramIter = this.paramInfo.iterator(); paramIter.hasNext(); ) {
            CallableStatementParam outParamInfo = paramIter.next();
            if (!this.callingStoredFunction && outParamInfo.isOut) {
              if (outParamInfo.paramName == null)
                outParamInfo.paramName = "nullnp" + outParamInfo.index; 
              String outParameterName = mangleParameterName(outParamInfo.paramName);
              int outParamIndex = 0;
              if (this.placeholderToParameterIndexMap == null) {
                outParamIndex = outParamInfo.index + 1;
              } else {
                boolean found = false;
                for (int i = 0; i < this.placeholderToParameterIndexMap.length; i++) {
                  if (this.placeholderToParameterIndexMap[i] == outParamInfo.index) {
                    outParamIndex = i + 1;
                    found = true;
                    break;
                  } 
                } 
                if (!found)
                  throw SQLError.createSQLException(Messages.getString("CallableStatement.21", new Object[] { outParamInfo.paramName }), "S1009", 
                      getExceptionInterceptor()); 
              } 
              setBytes(outParamIndex, StringUtils.getBytes(outParameterName, this.charEncoding), false);
            } 
          }  
      } 
      return;
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public void setShort(String parameterName, short x) throws SQLException {
    try {
      setShort(getNamedParamIndex(parameterName, false), x);
      return;
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public void setString(String parameterName, String x) throws SQLException {
    try {
      setString(getNamedParamIndex(parameterName, false), x);
      return;
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public void setTime(String parameterName, Time x) throws SQLException {
    try {
      setTime(getNamedParamIndex(parameterName, false), x);
      return;
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public void setTime(String parameterName, Time x, Calendar cal) throws SQLException {
    try {
      setTime(getNamedParamIndex(parameterName, false), x, cal);
      return;
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public void setTimestamp(String parameterName, Timestamp x) throws SQLException {
    try {
      setTimestamp(getNamedParamIndex(parameterName, false), x);
      return;
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public void setTimestamp(String parameterName, Timestamp x, Calendar cal) throws SQLException {
    try {
      setTimestamp(getNamedParamIndex(parameterName, false), x, cal);
      return;
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public void setURL(String parameterName, URL val) throws SQLException {
    try {
      setURL(getNamedParamIndex(parameterName, false), val);
      return;
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public boolean wasNull() throws SQLException {
    try {
      synchronized (checkClosed().getConnectionMutex()) {
        return this.outputParamWasNull;
      } 
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public int[] executeBatch() throws SQLException {
    try {
      return Util.truncateAndConvertToInt(executeLargeBatch());
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  protected int getParameterIndexOffset() {
    if (this.callingStoredFunction)
      return -1; 
    return super.getParameterIndexOffset();
  }
  
  public void setAsciiStream(String parameterName, InputStream x) throws SQLException {
    try {
      setAsciiStream(getNamedParamIndex(parameterName, false), x);
      return;
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public void setAsciiStream(String parameterName, InputStream x, long length) throws SQLException {
    try {
      setAsciiStream(getNamedParamIndex(parameterName, false), x, length);
      return;
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public void setBinaryStream(String parameterName, InputStream x) throws SQLException {
    try {
      setBinaryStream(getNamedParamIndex(parameterName, false), x);
      return;
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public void setBinaryStream(String parameterName, InputStream x, long length) throws SQLException {
    try {
      setBinaryStream(getNamedParamIndex(parameterName, false), x, length);
      return;
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public void setBlob(String parameterName, Blob x) throws SQLException {
    try {
      setBlob(getNamedParamIndex(parameterName, false), x);
      return;
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public void setBlob(String parameterName, InputStream inputStream) throws SQLException {
    try {
      setBlob(getNamedParamIndex(parameterName, false), inputStream);
      return;
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public void setBlob(String parameterName, InputStream inputStream, long length) throws SQLException {
    try {
      setBlob(getNamedParamIndex(parameterName, false), inputStream, length);
      return;
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public void setCharacterStream(String parameterName, Reader reader) throws SQLException {
    try {
      setCharacterStream(getNamedParamIndex(parameterName, false), reader);
      return;
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public void setCharacterStream(String parameterName, Reader reader, long length) throws SQLException {
    try {
      setCharacterStream(getNamedParamIndex(parameterName, false), reader, length);
      return;
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public void setClob(String parameterName, Clob x) throws SQLException {
    try {
      setClob(getNamedParamIndex(parameterName, false), x);
      return;
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public void setClob(String parameterName, Reader reader) throws SQLException {
    try {
      setClob(getNamedParamIndex(parameterName, false), reader);
      return;
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public void setClob(String parameterName, Reader reader, long length) throws SQLException {
    try {
      setClob(getNamedParamIndex(parameterName, false), reader, length);
      return;
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public void setNCharacterStream(String parameterName, Reader value) throws SQLException {
    try {
      setNCharacterStream(getNamedParamIndex(parameterName, false), value);
      return;
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public void setNCharacterStream(String parameterName, Reader value, long length) throws SQLException {
    try {
      setNCharacterStream(getNamedParamIndex(parameterName, false), value, length);
      return;
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  private boolean checkReadOnlyProcedure() throws SQLException {
    try {
      synchronized (checkClosed().getConnectionMutex()) {
        if (this.noAccessToProcedureBodies)
          return false; 
        if (this.paramInfo.isReadOnlySafeChecked)
          return this.paramInfo.isReadOnlySafeProcedure; 
        ResultSet rs = null;
        PreparedStatement ps = null;
        try {
          String procName = extractProcedureName();
          String db = getCurrentDatabase();
          if (procName.indexOf(".") != -1) {
            db = procName.substring(0, procName.indexOf("."));
            if (StringUtils.startsWithIgnoreCaseAndWs(db, "`") && db.trim().endsWith("`"))
              db = db.substring(1, db.length() - 1); 
            procName = procName.substring(procName.indexOf(".") + 1);
            procName = StringUtils.toString(StringUtils.stripEnclosure(StringUtils.getBytes(procName), "`", "`"));
          } 
          ps = this.connection.prepareStatement("SELECT SQL_DATA_ACCESS FROM information_schema.routines WHERE routine_schema = ? AND routine_name = ?");
          ps.setMaxRows(0);
          ps.setFetchSize(0);
          ps.setString(1, db);
          ps.setString(2, procName);
          rs = ps.executeQuery();
          if (rs.next()) {
            String sqlDataAccess = rs.getString(1);
            if ("READS SQL DATA".equalsIgnoreCase(sqlDataAccess) || "NO SQL".equalsIgnoreCase(sqlDataAccess)) {
              synchronized (this.paramInfo) {
                this.paramInfo.isReadOnlySafeChecked = true;
                this.paramInfo.isReadOnlySafeProcedure = true;
              } 
              return true;
            } 
          } 
        } catch (SQLException sQLException) {
        
        } finally {
          if (rs != null)
            rs.close(); 
          if (ps != null)
            ps.close(); 
        } 
        this.paramInfo.isReadOnlySafeChecked = false;
        this.paramInfo.isReadOnlySafeProcedure = false;
      } 
      return false;
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  protected boolean checkReadOnlySafeStatement() throws SQLException {
    if (QueryInfo.isReadOnlySafeQuery(((PreparedQuery)this.query).getOriginalSql(), this.session.getServerSession().isNoBackslashEscapesSet())) {
      String sql = ((PreparedQuery)this.query).getOriginalSql();
      int statementKeywordPos = QueryInfo.indexOfStatementKeyword(sql, this.session.getServerSession().isNoBackslashEscapesSet());
      if (StringUtils.startsWithIgnoreCaseAndWs(sql, "CALL", statementKeywordPos) || 
        StringUtils.startsWithIgnoreCaseAndWs(sql, "SELECT", statementKeywordPos)) {
        if (!this.connection.isReadOnly())
          return true; 
        return checkReadOnlyProcedure();
      } 
      return true;
    } 
    return !this.connection.isReadOnly();
  }
  
  public RowId getRowId(int parameterIndex) throws SQLException {
    try {
      ResultSetInternalMethods rs = getOutputParameters(parameterIndex);
      RowId retValue = rs.getRowId(mapOutputParameterIndexToRsIndex(parameterIndex));
      this.outputParamWasNull = rs.wasNull();
      return retValue;
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public RowId getRowId(String parameterName) throws SQLException {
    try {
      ResultSetInternalMethods rs = getOutputParameters(0);
      RowId retValue = rs.getRowId(fixParameterName(parameterName));
      this.outputParamWasNull = rs.wasNull();
      return retValue;
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public void setRowId(String parameterName, RowId x) throws SQLException {
    try {
      setRowId(getNamedParamIndex(parameterName, false), x);
      return;
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public void setNString(String parameterName, String value) throws SQLException {
    try {
      setNString(getNamedParamIndex(parameterName, false), value);
      return;
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public void setNClob(String parameterName, NClob value) throws SQLException {
    try {
      setNClob(getNamedParamIndex(parameterName, false), value);
      return;
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public void setNClob(String parameterName, Reader reader) throws SQLException {
    try {
      setNClob(getNamedParamIndex(parameterName, false), reader);
      return;
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public void setNClob(String parameterName, Reader reader, long length) throws SQLException {
    try {
      setNClob(getNamedParamIndex(parameterName, false), reader, length);
      return;
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public void setSQLXML(String parameterName, SQLXML xmlObject) throws SQLException {
    try {
      setSQLXML(getNamedParamIndex(parameterName, false), xmlObject);
      return;
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public SQLXML getSQLXML(int parameterIndex) throws SQLException {
    try {
      ResultSetInternalMethods rs = getOutputParameters(parameterIndex);
      SQLXML retValue = rs.getSQLXML(mapOutputParameterIndexToRsIndex(parameterIndex));
      this.outputParamWasNull = rs.wasNull();
      return retValue;
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public SQLXML getSQLXML(String parameterName) throws SQLException {
    try {
      ResultSetInternalMethods rs = getOutputParameters(0);
      SQLXML retValue = rs.getSQLXML(fixParameterName(parameterName));
      this.outputParamWasNull = rs.wasNull();
      return retValue;
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public String getNString(int parameterIndex) throws SQLException {
    try {
      ResultSetInternalMethods rs = getOutputParameters(parameterIndex);
      String retValue = rs.getNString(mapOutputParameterIndexToRsIndex(parameterIndex));
      this.outputParamWasNull = rs.wasNull();
      return retValue;
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public String getNString(String parameterName) throws SQLException {
    try {
      ResultSetInternalMethods rs = getOutputParameters(0);
      String retValue = rs.getNString(fixParameterName(parameterName));
      this.outputParamWasNull = rs.wasNull();
      return retValue;
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public Reader getNCharacterStream(int parameterIndex) throws SQLException {
    try {
      ResultSetInternalMethods rs = getOutputParameters(parameterIndex);
      Reader retValue = rs.getNCharacterStream(mapOutputParameterIndexToRsIndex(parameterIndex));
      this.outputParamWasNull = rs.wasNull();
      return retValue;
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public Reader getNCharacterStream(String parameterName) throws SQLException {
    try {
      ResultSetInternalMethods rs = getOutputParameters(0);
      Reader retValue = rs.getNCharacterStream(fixParameterName(parameterName));
      this.outputParamWasNull = rs.wasNull();
      return retValue;
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public Reader getCharacterStream(int parameterIndex) throws SQLException {
    try {
      ResultSetInternalMethods rs = getOutputParameters(parameterIndex);
      Reader retValue = rs.getCharacterStream(mapOutputParameterIndexToRsIndex(parameterIndex));
      this.outputParamWasNull = rs.wasNull();
      return retValue;
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public Reader getCharacterStream(String parameterName) throws SQLException {
    try {
      ResultSetInternalMethods rs = getOutputParameters(0);
      Reader retValue = rs.getCharacterStream(fixParameterName(parameterName));
      this.outputParamWasNull = rs.wasNull();
      return retValue;
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public NClob getNClob(int parameterIndex) throws SQLException {
    try {
      ResultSetInternalMethods rs = getOutputParameters(parameterIndex);
      NClob retValue = rs.getNClob(mapOutputParameterIndexToRsIndex(parameterIndex));
      this.outputParamWasNull = rs.wasNull();
      return retValue;
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public NClob getNClob(String parameterName) throws SQLException {
    try {
      ResultSetInternalMethods rs = getOutputParameters(0);
      NClob retValue = rs.getNClob(fixParameterName(parameterName));
      this.outputParamWasNull = rs.wasNull();
      return retValue;
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  protected byte[] s2b(String s) {
    return (s == null) ? null : StringUtils.getBytes(s, this.charEncoding);
  }
  
  public long executeLargeUpdate() throws SQLException {
    try {
      synchronized (checkClosed().getConnectionMutex()) {
        long returnVal = -1L;
        checkStreamability();
        if (this.callingStoredFunction) {
          execute();
          return -1L;
        } 
        setInOutParamsOnServer();
        setOutParams();
        returnVal = super.executeLargeUpdate();
        retrieveOutParams();
        return returnVal;
      } 
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public long[] executeLargeBatch() throws SQLException {
    try {
      if (this.hasOutputParams)
        throw SQLError.createSQLException("Can't call executeBatch() on CallableStatement with OUTPUT parameters", "S1009", 
            getExceptionInterceptor()); 
      return super.executeLargeBatch();
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
}
