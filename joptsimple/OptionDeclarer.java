package joptsimple;

import java.util.List;

public interface OptionDeclarer {
  OptionSpecBuilder accepts(String paramString);
  
  OptionSpecBuilder accepts(String paramString1, String paramString2);
  
  OptionSpecBuilder acceptsAll(List<String> paramList);
  
  OptionSpecBuilder acceptsAll(List<String> paramList, String paramString);
  
  NonOptionArgumentSpec<String> nonOptions();
  
  NonOptionArgumentSpec<String> nonOptions(String paramString);
  
  void posixlyCorrect(boolean paramBoolean);
  
  void allowsUnrecognizedOptions();
  
  void recognizeAlternativeLongOptions(boolean paramBoolean);
}
