package org.apache.logging.log4j.util;

import org.apache.logging.log4j.message.MultiformatMessage;

public interface MultiFormatStringBuilderFormattable extends MultiformatMessage, StringBuilderFormattable {
  void formatTo(String[] paramArrayOfString, StringBuilder paramStringBuilder);
}
