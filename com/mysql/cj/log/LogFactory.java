package com.mysql.cj.log;

import com.mysql.cj.exceptions.CJException;
import com.mysql.cj.exceptions.ExceptionFactory;
import com.mysql.cj.exceptions.WrongArgumentException;
import com.mysql.cj.util.Util;

public class LogFactory {
  public static Log getLogger(String className, String instanceName) {
    if (className == null)
      throw (WrongArgumentException)ExceptionFactory.createException(WrongArgumentException.class, "Logger class can not be NULL"); 
    if (instanceName == null)
      throw (WrongArgumentException)ExceptionFactory.createException(WrongArgumentException.class, "Logger instance name can not be NULL"); 
    try {
      return (Log)Util.getInstance(Log.class, className, new Class[] { String.class }, new Object[] { instanceName }, null);
    } catch (CJException e1) {
      if (ClassNotFoundException.class.isInstance(e1.getCause()))
        try {
          return (Log)Util.getInstance(Log.class, Util.getPackageName(LogFactory.class) + "." + className, new Class[] { String.class }, new Object[] { instanceName }, null);
        } catch (CJException e2) {
          throw e1;
        }  
      throw e1;
    } 
  }
}
