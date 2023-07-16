package com.mysql.cj.xdevapi;

import com.mysql.cj.x.protobuf.MysqlxDatatypes;
import com.mysql.cj.x.protobuf.MysqlxExpr;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class ExprUnparser {
  static Set<String> infixOperators = new HashSet<>();
  
  static {
    infixOperators.add("and");
    infixOperators.add("or");
  }
  
  static String scalarToString(MysqlxDatatypes.Scalar e) {
    switch (e.getType()) {
      case LITERAL:
        return "" + e.getVSignedInt();
      case IDENT:
        return "\"" + escapeLiteral(e.getVOctets().getValue().toStringUtf8()) + "\"";
      case FUNC_CALL:
        return "\"" + escapeLiteral(e.getVString().getValue().toStringUtf8()) + "\"";
      case OPERATOR:
        return "" + e.getVDouble();
      case PLACEHOLDER:
        return e.getVBool() ? "TRUE" : "FALSE";
      case ARRAY:
        return "NULL";
    } 
    throw new IllegalArgumentException("Unknown type tag: " + e.getType());
  }
  
  static String documentPathToString(List<MysqlxExpr.DocumentPathItem> items) {
    StringBuilder docPathString = new StringBuilder();
    for (MysqlxExpr.DocumentPathItem item : items) {
      switch (item.getType()) {
        case LITERAL:
          docPathString.append(".").append(quoteDocumentPathMember(item.getValue()));
        case IDENT:
          docPathString.append(".*");
        case FUNC_CALL:
          docPathString.append("[").append("" + Integer.toUnsignedLong(item.getIndex())).append("]");
        case OPERATOR:
          docPathString.append("[*]");
        case PLACEHOLDER:
          docPathString.append("**");
      } 
    } 
    return docPathString.toString();
  }
  
  static String columnIdentifierToString(MysqlxExpr.ColumnIdentifier e) {
    if (e.hasName()) {
      String s = quoteIdentifier(e.getName());
      if (e.hasTableName())
        s = quoteIdentifier(e.getTableName()) + "." + s; 
      if (e.hasSchemaName())
        s = quoteIdentifier(e.getSchemaName()) + "." + s; 
      if (e.getDocumentPathCount() > 0)
        s = s + "->$" + documentPathToString(e.getDocumentPathList()); 
      return s;
    } 
    return "$" + documentPathToString(e.getDocumentPathList());
  }
  
  static String functionCallToString(MysqlxExpr.FunctionCall e) {
    MysqlxExpr.Identifier i = e.getName();
    String s = quoteIdentifier(i.getName());
    if (i.hasSchemaName())
      s = quoteIdentifier(i.getSchemaName()) + "." + s; 
    s = s + "(";
    for (MysqlxExpr.Expr p : e.getParamList())
      s = s + exprToString(p) + ", "; 
    s = s.replaceAll(", $", "");
    s = s + ")";
    return s;
  }
  
  static String arrayToString(MysqlxExpr.Array e) {
    String s = "[";
    for (MysqlxExpr.Expr v : e.getValueList())
      s = s + exprToString(v) + ", "; 
    s = s.replaceAll(", $", "");
    s = s + "]";
    return s;
  }
  
  static String paramListToString(List<String> params) {
    String s = "(";
    boolean first = true;
    for (String param : params) {
      if (!first)
        s = s + ", "; 
      first = false;
      s = s + param;
    } 
    return s + ")";
  }
  
  static String operatorToString(MysqlxExpr.Operator e) {
    String name = e.getName();
    List<String> params = new ArrayList<>();
    for (MysqlxExpr.Expr p : e.getParamList())
      params.add(exprToString(p)); 
    if ("between".equals(name) || "not_between".equals(name)) {
      name = name.replaceAll("not_between", "not between");
      return String.format("(%s %s %s AND %s)", new Object[] { params.get(0), name, params.get(1), params.get(2) });
    } 
    if ("in".equals(name) || "not_in".equals(name)) {
      name = name.replaceAll("not_in", "not in");
      return String.format("%s %s%s", new Object[] { params.get(0), name, paramListToString(params.subList(1, params.size())) });
    } 
    if ("like".equals(name) || "not_like".equals(name)) {
      name = name.replaceAll("not_like", "not like");
      String s = String.format("%s %s %s", new Object[] { params.get(0), name, params.get(1) });
      if (params.size() == 3)
        s = s + " ESCAPE " + (String)params.get(2); 
      return s;
    } 
    if ("overlaps".equals(name) || "not_overlaps".equals(name)) {
      name = name.replaceAll("not_overlaps", "not overlaps");
      return String.format("%s %s %s", new Object[] { params.get(0), name, params.get(1) });
    } 
    if ("regexp".equals(name) || "not_regexp".equals("name")) {
      name = name.replaceAll("not_regexp", "not regexp");
      return String.format("(%s %s %s)", new Object[] { params.get(0), name, params.get(1) });
    } 
    if ("cast".equals(name))
      return String.format("cast(%s AS %s)", new Object[] { params.get(0), ((String)params.get(1)).replaceAll("\"", "") }); 
    if ((name.length() < 3 || infixOperators.contains(name)) && params.size() == 2)
      return String.format("(%s %s %s)", new Object[] { params.get(0), name, params.get(1) }); 
    if ("sign_minus".equals(name)) {
      name = name.replaceAll("sign_minus", "-");
      return String.format("%s%s", new Object[] { name, params.get(0) });
    } 
    if ("sign_plus".equals(name)) {
      name = name.replaceAll("sign_plus", "+");
      return String.format("%s%s", new Object[] { name, params.get(0) });
    } 
    if (params.size() == 1)
      return String.format("%s%s", new Object[] { name, params.get(0) }); 
    if (params.size() == 0)
      return name; 
    return name + paramListToString(params);
  }
  
  static String objectToString(MysqlxExpr.Object o) {
    String fields = o.getFldList().stream().map(f -> "'" + quoteJsonKey(f.getKey()) + "'" + ":" + exprToString(f.getValue())).collect(Collectors.joining(", "));
    return "{" + fields + "}";
  }
  
  public static String escapeLiteral(String s) {
    return s.replaceAll("\"", "\"\"");
  }
  
  public static String quoteIdentifier(String ident) {
    if (ident.contains("`") || ident.contains("\"") || ident.contains("'") || ident.contains("$") || ident.contains(".") || ident.contains("-"))
      return "`" + ident.replaceAll("`", "``") + "`"; 
    return ident;
  }
  
  public static String quoteJsonKey(String key) {
    return key.replaceAll("'", "\\\\'");
  }
  
  public static String quoteDocumentPathMember(String member) {
    if (!member.matches("[a-zA-Z0-9_]*"))
      return "\"" + member.replaceAll("\"", "\\\\\"") + "\""; 
    return member;
  }
  
  public static String exprToString(MysqlxExpr.Expr e) {
    switch (e.getType()) {
      case LITERAL:
        return scalarToString(e.getLiteral());
      case IDENT:
        return columnIdentifierToString(e.getIdentifier());
      case FUNC_CALL:
        return functionCallToString(e.getFunctionCall());
      case OPERATOR:
        return operatorToString(e.getOperator());
      case PLACEHOLDER:
        return ":" + Integer.toUnsignedLong(e.getPosition());
      case ARRAY:
        return arrayToString(e.getArray());
      case OBJECT:
        return objectToString(e.getObject());
    } 
    throw new IllegalArgumentException("Unknown type tag: " + e.getType());
  }
}
