package com.mysql.cj.jdbc;

import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Driver extends NonRegisteringDriver implements Driver {
  static {
    try {
      DriverManager.registerDriver(new Driver());
    } catch (SQLException E) {
      throw new RuntimeException("Can't register driver!");
    } 
  }
}
