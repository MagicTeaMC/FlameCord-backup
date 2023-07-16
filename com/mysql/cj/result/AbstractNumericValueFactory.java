package com.mysql.cj.result;

import com.mysql.cj.Messages;
import com.mysql.cj.conf.PropertyKey;
import com.mysql.cj.conf.PropertySet;
import com.mysql.cj.exceptions.DataConversionException;
import com.mysql.cj.protocol.a.MysqlTextValueDecoder;
import com.mysql.cj.util.StringUtils;

public abstract class AbstractNumericValueFactory<T> extends DefaultValueFactory<T> {
  public AbstractNumericValueFactory(PropertySet pset) {
    super(pset);
  }
  
  public T createFromBytes(byte[] bytes, int offset, int length, Field f) {
    if (length == 0 && ((Boolean)this.pset.getBooleanProperty(PropertyKey.emptyStringsConvertToZero).getValue()).booleanValue())
      return createFromLong(0L); 
    String s = StringUtils.toString(bytes, offset, length, f.getEncoding());
    byte[] newBytes = s.getBytes();
    if (s.contains("e") || s.contains("E") || s.matches("-?\\d*\\.\\d*"))
      return createFromDouble(MysqlTextValueDecoder.getDouble(newBytes, 0, newBytes.length).doubleValue()); 
    if (s.matches("-?\\d+")) {
      if (s.charAt(0) == '-' || (length <= 19 && newBytes[0] >= 48 && newBytes[0] <= 56))
        return createFromLong(MysqlTextValueDecoder.getLong(newBytes, 0, newBytes.length)); 
      return createFromBigInteger(MysqlTextValueDecoder.getBigInteger(newBytes, 0, newBytes.length));
    } 
    throw new DataConversionException(Messages.getString("ResultSet.UnableToInterpretString", new Object[] { s }));
  }
  
  public T createFromYear(long l) {
    return createFromLong(l);
  }
}
