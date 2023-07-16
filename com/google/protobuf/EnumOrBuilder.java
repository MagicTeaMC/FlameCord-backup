package com.google.protobuf;

import java.util.List;

public interface EnumOrBuilder extends MessageOrBuilder {
  String getName();
  
  ByteString getNameBytes();
  
  List<EnumValue> getEnumvalueList();
  
  EnumValue getEnumvalue(int paramInt);
  
  int getEnumvalueCount();
  
  List<? extends EnumValueOrBuilder> getEnumvalueOrBuilderList();
  
  EnumValueOrBuilder getEnumvalueOrBuilder(int paramInt);
  
  List<Option> getOptionsList();
  
  Option getOptions(int paramInt);
  
  int getOptionsCount();
  
  List<? extends OptionOrBuilder> getOptionsOrBuilderList();
  
  OptionOrBuilder getOptionsOrBuilder(int paramInt);
  
  boolean hasSourceContext();
  
  SourceContext getSourceContext();
  
  SourceContextOrBuilder getSourceContextOrBuilder();
  
  int getSyntaxValue();
  
  Syntax getSyntax();
}
