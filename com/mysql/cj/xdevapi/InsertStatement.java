package com.mysql.cj.xdevapi;

import java.util.Arrays;
import java.util.List;

public interface InsertStatement extends Statement<InsertStatement, InsertResult> {
  InsertStatement values(List<Object> paramList);
  
  InsertStatement values(Object... values) {
    return values(Arrays.asList(values));
  }
}
