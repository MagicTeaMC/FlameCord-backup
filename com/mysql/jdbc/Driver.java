package com.mysql.jdbc;

import com.mysql.cj.jdbc.Driver;

public class Driver extends Driver {
  static {
    System.err.println("Loading class `com.mysql.jdbc.Driver'. This is deprecated. The new driver class is `com.mysql.cj.jdbc.Driver'. The driver is automatically registered via the SPI and manual loading of the driver class is generally unnecessary.");
  }
}
