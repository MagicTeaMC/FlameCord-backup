package com.mysql.cj.callback;

@FunctionalInterface
public interface MysqlCallbackHandler {
  void handle(MysqlCallback paramMysqlCallback);
}
