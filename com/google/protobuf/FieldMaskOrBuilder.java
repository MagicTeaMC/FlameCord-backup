package com.google.protobuf;

import java.util.List;

public interface FieldMaskOrBuilder extends MessageOrBuilder {
  List<String> getPathsList();
  
  int getPathsCount();
  
  String getPaths(int paramInt);
  
  ByteString getPathsBytes(int paramInt);
}
