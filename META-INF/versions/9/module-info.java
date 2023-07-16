module com.google.gson {
  requires java.sql;
  requires jdk.unsupported;
  requires java.base;
  
  exports com.google.gson;
  exports com.google.gson.annotations;
  exports com.google.gson.reflect;
  exports com.google.gson.stream;
}
