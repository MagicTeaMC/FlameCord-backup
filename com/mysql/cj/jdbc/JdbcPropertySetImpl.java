package com.mysql.cj.jdbc;

import com.mysql.cj.conf.DefaultPropertySet;
import com.mysql.cj.conf.PropertyDefinition;
import com.mysql.cj.conf.PropertyDefinitions;
import com.mysql.cj.conf.PropertyKey;
import com.mysql.cj.conf.RuntimeProperty;
import com.mysql.cj.util.StringUtils;
import java.sql.DriverPropertyInfo;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class JdbcPropertySetImpl extends DefaultPropertySet implements JdbcPropertySet {
  private static final long serialVersionUID = -8223499903182568260L;
  
  public void postInitialization() {
    if (((Integer)getIntegerProperty(PropertyKey.maxRows).getValue()).intValue() == 0)
      getProperty(PropertyKey.maxRows).setValue(Integer.valueOf(-1), null); 
    String testEncoding = (String)getStringProperty(PropertyKey.characterEncoding).getValue();
    if (testEncoding != null) {
      String testString = "abc";
      StringUtils.getBytes(testString, testEncoding);
    } 
    if (((Boolean)getBooleanProperty(PropertyKey.useCursorFetch).getValue()).booleanValue())
      getProperty(PropertyKey.useServerPrepStmts).setValue(Boolean.valueOf(true)); 
  }
  
  public List<DriverPropertyInfo> exposeAsDriverPropertyInfo() throws SQLException {
    return (List<DriverPropertyInfo>)PropertyDefinitions.PROPERTY_KEY_TO_PROPERTY_DEFINITION.entrySet().stream()
      .filter(e -> !((PropertyDefinition)e.getValue()).getCategory().equals(PropertyDefinitions.CATEGORY_XDEVAPI)).map(Map.Entry::getKey).map(this::getProperty)
      .map(this::getAsDriverPropertyInfo).collect(Collectors.toList());
  }
  
  private DriverPropertyInfo getAsDriverPropertyInfo(RuntimeProperty<?> pr) {
    PropertyDefinition<?> pdef = pr.getPropertyDefinition();
    DriverPropertyInfo dpi = new DriverPropertyInfo(pdef.getName(), null);
    dpi.choices = pdef.getAllowableValues();
    dpi.value = (pr.getStringValue() != null) ? pr.getStringValue() : null;
    dpi.required = false;
    dpi.description = pdef.getDescription();
    return dpi;
  }
}
