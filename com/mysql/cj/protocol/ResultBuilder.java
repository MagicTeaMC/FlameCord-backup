package com.mysql.cj.protocol;

public interface ResultBuilder<T> {
  boolean addProtocolEntity(ProtocolEntity paramProtocolEntity);
  
  T build();
}
