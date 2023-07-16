package com.google.protobuf;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

final class MessageLiteToString {
  private static final String LIST_SUFFIX = "List";
  
  private static final String BUILDER_LIST_SUFFIX = "OrBuilderList";
  
  private static final String MAP_SUFFIX = "Map";
  
  private static final String BYTES_SUFFIX = "Bytes";
  
  private static final char[] INDENT_BUFFER = new char[80];
  
  static {
    Arrays.fill(INDENT_BUFFER, ' ');
  }
  
  static String toString(MessageLite messageLite, String commentString) {
    StringBuilder buffer = new StringBuilder();
    buffer.append("# ").append(commentString);
    reflectivePrintWithIndent(messageLite, buffer, 0);
    return buffer.toString();
  }
  
  private static void reflectivePrintWithIndent(MessageLite messageLite, StringBuilder buffer, int indent) {
    Set<String> setters = new HashSet<>();
    Map<String, Method> hazzers = new HashMap<>();
    Map<String, Method> getters = new TreeMap<>();
    for (Method method : messageLite.getClass().getDeclaredMethods()) {
      if (!Modifier.isStatic(method.getModifiers()))
        if (method.getName().length() >= 3)
          if (method.getName().startsWith("set")) {
            setters.add(method.getName());
          } else if (Modifier.isPublic(method.getModifiers())) {
            if ((method.getParameterTypes()).length == 0)
              if (method.getName().startsWith("has")) {
                hazzers.put(method.getName(), method);
              } else if (method.getName().startsWith("get")) {
                getters.put(method.getName(), method);
              }  
          }   
    } 
    for (Map.Entry<String, Method> getter : getters.entrySet()) {
      String suffix = ((String)getter.getKey()).substring(3);
      if (suffix.endsWith("List") && 
        !suffix.endsWith("OrBuilderList") && 
        
        !suffix.equals("List")) {
        Method listMethod = getter.getValue();
        if (listMethod != null && listMethod.getReturnType().equals(List.class)) {
          printField(buffer, indent, suffix
              
              .substring(0, suffix.length() - "List".length()), 
              GeneratedMessageLite.invokeOrDie(listMethod, messageLite, new Object[0]));
          continue;
        } 
      } 
      if (suffix.endsWith("Map") && 
        
        !suffix.equals("Map")) {
        Method mapMethod = getter.getValue();
        if (mapMethod != null && mapMethod
          .getReturnType().equals(Map.class) && 
          
          !mapMethod.isAnnotationPresent((Class)Deprecated.class) && 
          
          Modifier.isPublic(mapMethod.getModifiers())) {
          printField(buffer, indent, suffix
              
              .substring(0, suffix.length() - "Map".length()), 
              GeneratedMessageLite.invokeOrDie(mapMethod, messageLite, new Object[0]));
          continue;
        } 
      } 
      if (!setters.contains("set" + suffix))
        continue; 
      if (suffix.endsWith("Bytes") && getters
        .containsKey("get" + suffix.substring(0, suffix.length() - "Bytes".length())))
        continue; 
      Method getMethod = getter.getValue();
      Method hasMethod = hazzers.get("has" + suffix);
      if (getMethod != null) {
        Object value = GeneratedMessageLite.invokeOrDie(getMethod, messageLite, new Object[0]);
        boolean hasValue = (hasMethod == null) ? (!isDefaultValue(value)) : ((Boolean)GeneratedMessageLite.invokeOrDie(hasMethod, messageLite, new Object[0])).booleanValue();
        if (hasValue)
          printField(buffer, indent, suffix, value); 
      } 
    } 
    if (messageLite instanceof GeneratedMessageLite.ExtendableMessage) {
      Iterator<Map.Entry<GeneratedMessageLite.ExtensionDescriptor, Object>> iter = ((GeneratedMessageLite.ExtendableMessage)messageLite).extensions.iterator();
      while (iter.hasNext()) {
        Map.Entry<GeneratedMessageLite.ExtensionDescriptor, Object> entry = iter.next();
        printField(buffer, indent, "[" + ((GeneratedMessageLite.ExtensionDescriptor)entry.getKey()).getNumber() + "]", entry.getValue());
      } 
    } 
    if (((GeneratedMessageLite)messageLite).unknownFields != null)
      ((GeneratedMessageLite)messageLite).unknownFields.printWithIndent(buffer, indent); 
  }
  
  private static boolean isDefaultValue(Object o) {
    if (o instanceof Boolean)
      return !((Boolean)o).booleanValue(); 
    if (o instanceof Integer)
      return (((Integer)o).intValue() == 0); 
    if (o instanceof Float)
      return (Float.floatToRawIntBits(((Float)o).floatValue()) == 0); 
    if (o instanceof Double)
      return (Double.doubleToRawLongBits(((Double)o).doubleValue()) == 0L); 
    if (o instanceof String)
      return o.equals(""); 
    if (o instanceof ByteString)
      return o.equals(ByteString.EMPTY); 
    if (o instanceof MessageLite)
      return (o == ((MessageLite)o).getDefaultInstanceForType()); 
    if (o instanceof java.lang.Enum)
      return (((java.lang.Enum)o).ordinal() == 0); 
    return false;
  }
  
  static void printField(StringBuilder buffer, int indent, String name, Object object) {
    if (object instanceof List) {
      List<?> list = (List)object;
      for (Object entry : list)
        printField(buffer, indent, name, entry); 
      return;
    } 
    if (object instanceof Map) {
      Map<?, ?> map = (Map<?, ?>)object;
      for (Map.Entry<?, ?> entry : map.entrySet())
        printField(buffer, indent, name, entry); 
      return;
    } 
    buffer.append('\n');
    indent(indent, buffer);
    buffer.append(pascalCaseToSnakeCase(name));
    if (object instanceof String) {
      buffer.append(": \"").append(TextFormatEscaper.escapeText((String)object)).append('"');
    } else if (object instanceof ByteString) {
      buffer.append(": \"").append(TextFormatEscaper.escapeBytes((ByteString)object)).append('"');
    } else if (object instanceof GeneratedMessageLite) {
      buffer.append(" {");
      reflectivePrintWithIndent((GeneratedMessageLite)object, buffer, indent + 2);
      buffer.append("\n");
      indent(indent, buffer);
      buffer.append("}");
    } else if (object instanceof Map.Entry) {
      buffer.append(" {");
      Map.Entry<?, ?> entry = (Map.Entry<?, ?>)object;
      printField(buffer, indent + 2, "key", entry.getKey());
      printField(buffer, indent + 2, "value", entry.getValue());
      buffer.append("\n");
      indent(indent, buffer);
      buffer.append("}");
    } else {
      buffer.append(": ").append(object);
    } 
  }
  
  private static void indent(int indent, StringBuilder buffer) {
    while (indent > 0) {
      int partialIndent = indent;
      if (partialIndent > INDENT_BUFFER.length)
        partialIndent = INDENT_BUFFER.length; 
      buffer.append(INDENT_BUFFER, 0, partialIndent);
      indent -= partialIndent;
    } 
  }
  
  private static String pascalCaseToSnakeCase(String pascalCase) {
    if (pascalCase.isEmpty())
      return pascalCase; 
    StringBuilder builder = new StringBuilder();
    builder.append(Character.toLowerCase(pascalCase.charAt(0)));
    for (int i = 1; i < pascalCase.length(); i++) {
      char ch = pascalCase.charAt(i);
      if (Character.isUpperCase(ch))
        builder.append("_"); 
      builder.append(Character.toLowerCase(ch));
    } 
    return builder.toString();
  }
}
