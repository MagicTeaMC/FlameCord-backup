package com.mysql.cj.util;

import com.mysql.cj.Messages;
import com.mysql.cj.ServerVersion;
import com.mysql.cj.exceptions.ExceptionFactory;
import com.mysql.cj.exceptions.WrongArgumentException;
import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.charset.UnsupportedCharsetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;

public class StringUtils {
  private static final int WILD_COMPARE_MATCH = 0;
  
  private static final int WILD_COMPARE_CONTINUE_WITH_WILD = 1;
  
  private static final int WILD_COMPARE_NO_MATCH = -1;
  
  static final char WILDCARD_MANY = '%';
  
  static final char WILDCARD_ONE = '_';
  
  static final char WILDCARD_ESCAPE = '\\';
  
  private static final String VALID_ID_CHARS = "abcdefghijklmnopqrstuvwxyzABCDEFGHIGKLMNOPQRSTUVWXYZ0123456789$_#@";
  
  public static String dumpAsHex(byte[] byteBuffer, int length) {
    length = Math.min(length, byteBuffer.length);
    StringBuilder fullOutBuilder = new StringBuilder(length * 4);
    StringBuilder asciiOutBuilder = new StringBuilder(16);
    int l;
    for (int p = 0; p < length; l = 0) {
      for (; l < 8 && p < length; p++, l++) {
        int asInt = byteBuffer[p] & 0xFF;
        if (asInt < 16)
          fullOutBuilder.append("0"); 
        fullOutBuilder.append(Integer.toHexString(asInt)).append(" ");
        asciiOutBuilder.append(" ").append((asInt >= 32 && asInt < 127) ? Character.valueOf((char)asInt) : ".");
      } 
      for (; l < 8; l++)
        fullOutBuilder.append("   "); 
      fullOutBuilder.append("   ").append(asciiOutBuilder).append(System.lineSeparator());
      asciiOutBuilder.setLength(0);
    } 
    return fullOutBuilder.toString();
  }
  
  public static String toHexString(byte[] byteBuffer, int length) {
    length = Math.min(length, byteBuffer.length);
    StringBuilder outputBuilder = new StringBuilder(length * 2);
    for (int i = 0; i < length; i++) {
      int asInt = byteBuffer[i] & 0xFF;
      if (asInt < 16)
        outputBuilder.append("0"); 
      outputBuilder.append(Integer.toHexString(asInt));
    } 
    return outputBuilder.toString();
  }
  
  private static boolean endsWith(byte[] dataFrom, String suffix) {
    for (int i = 1; i <= suffix.length(); i++) {
      int dfOffset = dataFrom.length - i;
      int suffixOffset = suffix.length() - i;
      if (dataFrom[dfOffset] != suffix.charAt(suffixOffset))
        return false; 
    } 
    return true;
  }
  
  public static char firstNonWsCharUc(String searchIn) {
    return firstNonWsCharUc(searchIn, 0);
  }
  
  public static char firstNonWsCharUc(String searchIn, int startAt) {
    if (searchIn == null)
      return Character.MIN_VALUE; 
    int length = searchIn.length();
    for (int i = startAt; i < length; i++) {
      char c = searchIn.charAt(i);
      if (!Character.isWhitespace(c))
        return Character.toUpperCase(c); 
    } 
    return Character.MIN_VALUE;
  }
  
  public static char firstAlphaCharUc(String searchIn, int startAt) {
    if (searchIn == null)
      return Character.MIN_VALUE; 
    int length = searchIn.length();
    for (int i = startAt; i < length; i++) {
      char c = searchIn.charAt(i);
      if (Character.isLetter(c))
        return Character.toUpperCase(c); 
    } 
    return Character.MIN_VALUE;
  }
  
  public static String fixDecimalExponent(String dString) {
    int ePos = dString.indexOf('E');
    if (ePos == -1)
      ePos = dString.indexOf('e'); 
    if (ePos != -1 && 
      dString.length() > ePos + 1) {
      char maybeMinusChar = dString.charAt(ePos + 1);
      if (maybeMinusChar != '-' && maybeMinusChar != '+') {
        StringBuilder strBuilder = new StringBuilder(dString.length() + 1);
        strBuilder.append(dString.substring(0, ePos + 1));
        strBuilder.append('+');
        strBuilder.append(dString.substring(ePos + 1, dString.length()));
        dString = strBuilder.toString();
      } 
    } 
    return dString;
  }
  
  public static byte[] getBytes(String s, String encoding) {
    if (s == null)
      return new byte[0]; 
    if (encoding == null)
      return getBytes(s); 
    try {
      return s.getBytes(encoding);
    } catch (UnsupportedEncodingException uee) {
      throw (WrongArgumentException)ExceptionFactory.createException(WrongArgumentException.class, Messages.getString("StringUtils.0", new Object[] { encoding }), uee);
    } 
  }
  
  public static byte[] getBytesWrapped(String s, char beginWrap, char endWrap, String encoding) {
    byte[] b;
    if (encoding == null) {
      StringBuilder strBuilder = new StringBuilder(s.length() + 2);
      strBuilder.append(beginWrap);
      strBuilder.append(s);
      strBuilder.append(endWrap);
      b = getBytes(strBuilder.toString());
    } else {
      StringBuilder strBuilder = new StringBuilder(s.length() + 2);
      strBuilder.append(beginWrap);
      strBuilder.append(s);
      strBuilder.append(endWrap);
      s = strBuilder.toString();
      b = getBytes(s, encoding);
    } 
    return b;
  }
  
  public static int indexOfIgnoreCase(String searchIn, String searchFor) {
    return indexOfIgnoreCase(0, searchIn, searchFor);
  }
  
  public static int indexOfIgnoreCase(int startingPosition, String searchIn, String searchFor) {
    if (searchIn == null || searchFor == null)
      return -1; 
    int searchInLength = searchIn.length();
    int searchForLength = searchFor.length();
    int stopSearchingAt = searchInLength - searchForLength;
    if (startingPosition > stopSearchingAt || searchForLength == 0)
      return -1; 
    char firstCharOfSearchForUc = Character.toUpperCase(searchFor.charAt(0));
    char firstCharOfSearchForLc = Character.toLowerCase(searchFor.charAt(0));
    for (int i = startingPosition; i <= stopSearchingAt; i++) {
      if (isCharAtPosNotEqualIgnoreCase(searchIn, i, firstCharOfSearchForUc, firstCharOfSearchForLc))
        while (++i <= stopSearchingAt && isCharAtPosNotEqualIgnoreCase(searchIn, i, firstCharOfSearchForUc, firstCharOfSearchForLc)); 
      if (i <= stopSearchingAt && regionMatchesIgnoreCase(searchIn, i, searchFor))
        return i; 
    } 
    return -1;
  }
  
  public static int indexOfIgnoreCase(int startingPosition, String searchIn, String[] searchForSequence, String openingMarkers, String closingMarkers, Set<SearchMode> searchMode) {
    StringInspector strInspector = new StringInspector(searchIn, startingPosition, openingMarkers, closingMarkers, "", searchMode);
    return strInspector.indexOfIgnoreCase(searchForSequence);
  }
  
  public static int indexOfIgnoreCase(int startingPosition, String searchIn, String searchFor, String openingMarkers, String closingMarkers, Set<SearchMode> searchMode) {
    return indexOfIgnoreCase(startingPosition, searchIn, searchFor, openingMarkers, closingMarkers, "", searchMode);
  }
  
  public static int indexOfIgnoreCase(int startingPosition, String searchIn, String searchFor, String openingMarkers, String closingMarkers, String overridingMarkers, Set<SearchMode> searchMode) {
    StringInspector strInspector = new StringInspector(searchIn, startingPosition, openingMarkers, closingMarkers, overridingMarkers, searchMode);
    return strInspector.indexOfIgnoreCase(searchFor);
  }
  
  public static int indexOfNextAlphanumericChar(int startingPosition, String searchIn, String openingMarkers, String closingMarkers, String overridingMarkers, Set<SearchMode> searchMode) {
    StringInspector strInspector = new StringInspector(searchIn, startingPosition, openingMarkers, closingMarkers, overridingMarkers, searchMode);
    return strInspector.indexOfNextAlphanumericChar();
  }
  
  public static int indexOfNextNonWsChar(int startingPosition, String searchIn, String openingMarkers, String closingMarkers, String overridingMarkers, Set<SearchMode> searchMode) {
    StringInspector strInspector = new StringInspector(searchIn, startingPosition, openingMarkers, closingMarkers, overridingMarkers, searchMode);
    return strInspector.indexOfNextNonWsChar();
  }
  
  public static int indexOfNextWsChar(int startingPosition, String searchIn, String openingMarkers, String closingMarkers, String overridingMarkers, Set<SearchMode> searchMode) {
    StringInspector strInspector = new StringInspector(searchIn, startingPosition, openingMarkers, closingMarkers, overridingMarkers, searchMode);
    return strInspector.indexOfNextWsChar();
  }
  
  private static boolean isCharAtPosNotEqualIgnoreCase(String searchIn, int pos, char firstCharOfSearchForUc, char firstCharOfSearchForLc) {
    return (Character.toLowerCase(searchIn.charAt(pos)) != firstCharOfSearchForLc && Character.toUpperCase(searchIn.charAt(pos)) != firstCharOfSearchForUc);
  }
  
  protected static boolean isCharEqualIgnoreCase(char charToCompare, char compareToCharUC, char compareToCharLC) {
    return (Character.toLowerCase(charToCompare) == compareToCharLC || Character.toUpperCase(charToCompare) == compareToCharUC);
  }
  
  public static List<String> split(String stringToSplit, String delimiter, boolean trim) {
    if (stringToSplit == null)
      return new ArrayList<>(); 
    if (delimiter == null)
      throw new IllegalArgumentException(); 
    String[] tokens = stringToSplit.split(delimiter, -1);
    List<String> tokensList = Arrays.asList(tokens);
    if (trim)
      tokensList = (List<String>)tokensList.stream().map(String::trim).collect(Collectors.toList()); 
    return tokensList;
  }
  
  public static List<String> split(String stringToSplit, String delimiter, String openingMarkers, String closingMarkers, boolean trim) {
    return split(stringToSplit, delimiter, openingMarkers, closingMarkers, "", trim);
  }
  
  public static List<String> split(String stringToSplit, String delimiter, String openingMarkers, String closingMarkers, boolean trim, Set<SearchMode> searchMode) {
    return split(stringToSplit, delimiter, openingMarkers, closingMarkers, "", trim, searchMode);
  }
  
  public static List<String> split(String stringToSplit, String delimiter, String openingMarkers, String closingMarkers, String overridingMarkers, boolean trim) {
    return split(stringToSplit, delimiter, openingMarkers, closingMarkers, overridingMarkers, trim, SearchMode.__MRK_COM_MYM_HNT_WS);
  }
  
  public static List<String> split(String stringToSplit, String delimiter, String openingMarkers, String closingMarkers, String overridingMarkers, boolean trim, Set<SearchMode> searchMode) {
    StringInspector strInspector = new StringInspector(stringToSplit, openingMarkers, closingMarkers, overridingMarkers, searchMode);
    return strInspector.split(delimiter, trim);
  }
  
  private static boolean startsWith(byte[] dataFrom, String chars) {
    int charsLength = chars.length();
    if (dataFrom.length < charsLength)
      return false; 
    for (int i = 0; i < charsLength; i++) {
      if (dataFrom[i] != chars.charAt(i))
        return false; 
    } 
    return true;
  }
  
  public static boolean regionMatchesIgnoreCase(String searchIn, int startAt, String searchFor) {
    return searchIn.regionMatches(true, startAt, searchFor, 0, searchFor.length());
  }
  
  public static boolean startsWithIgnoreCase(String searchIn, String searchFor) {
    return regionMatchesIgnoreCase(searchIn, 0, searchFor);
  }
  
  public static boolean startsWithIgnoreCaseAndNonAlphaNumeric(String searchIn, String searchFor) {
    if (searchIn == null)
      return (searchFor == null); 
    int beginPos = 0;
    int inLength = searchIn.length();
    for (; beginPos < inLength; beginPos++) {
      char c = searchIn.charAt(beginPos);
      if (Character.isLetterOrDigit(c))
        break; 
    } 
    return regionMatchesIgnoreCase(searchIn, beginPos, searchFor);
  }
  
  public static boolean startsWithIgnoreCaseAndWs(String searchIn, String searchFor) {
    return startsWithIgnoreCaseAndWs(searchIn, searchFor, 0);
  }
  
  public static boolean startsWithIgnoreCaseAndWs(String searchIn, String searchFor, int beginPos) {
    if (searchIn == null)
      return (searchFor == null); 
    for (; beginPos < searchIn.length() && 
      Character.isWhitespace(searchIn.charAt(beginPos)); beginPos++);
    return regionMatchesIgnoreCase(searchIn, beginPos, searchFor);
  }
  
  public static int startsWithIgnoreCaseAndWs(String searchIn, String[] searchFor) {
    for (int i = 0; i < searchFor.length; i++) {
      if (startsWithIgnoreCaseAndWs(searchIn, searchFor[i], 0))
        return i; 
    } 
    return -1;
  }
  
  public static boolean endsWithIgnoreCase(String searchIn, String searchFor) {
    int len = searchFor.length();
    return searchIn.regionMatches(true, searchIn.length() - len, searchFor, 0, len);
  }
  
  public static byte[] stripEnclosure(byte[] source, String prefix, String suffix) {
    if (source.length >= prefix.length() + suffix.length() && startsWith(source, prefix) && endsWith(source, suffix)) {
      int totalToStrip = prefix.length() + suffix.length();
      int enclosedLength = source.length - totalToStrip;
      byte[] enclosed = new byte[enclosedLength];
      int startPos = prefix.length();
      int numToCopy = enclosed.length;
      System.arraycopy(source, startPos, enclosed, 0, numToCopy);
      return enclosed;
    } 
    return source;
  }
  
  public static String toAsciiString(byte[] buffer) {
    return toAsciiString(buffer, 0, buffer.length);
  }
  
  public static String toAsciiString(byte[] buffer, int startPos, int length) {
    return new String(toAsciiCharArray(buffer, startPos, length));
  }
  
  public static char[] toAsciiCharArray(byte[] buffer, int startPos, int length) {
    char[] charArray = new char[length];
    int readpoint = startPos;
    for (int i = 0; i < length; i++) {
      charArray[i] = (char)buffer[readpoint];
      readpoint++;
    } 
    return charArray;
  }
  
  public static boolean wildCompareIgnoreCase(String searchIn, String searchFor) {
    return (wildCompareInternal(searchIn, searchFor) == 0);
  }
  
  private static int wildCompareInternal(String searchIn, String searchFor) {
    if (searchIn == null || searchFor == null)
      return -1; 
    if (searchFor.equals("%"))
      return 0; 
    int searchForPos = 0;
    int searchForEnd = searchFor.length();
    int searchInPos = 0;
    int searchInEnd = searchIn.length();
    int result = -1;
    while (searchForPos != searchForEnd) {
      while (searchFor.charAt(searchForPos) != '%' && searchFor.charAt(searchForPos) != '_') {
        if (searchFor.charAt(searchForPos) == '\\' && searchForPos + 1 != searchForEnd)
          searchForPos++; 
        if (searchInPos == searchInEnd || 
          Character.toUpperCase(searchFor.charAt(searchForPos++)) != Character.toUpperCase(searchIn.charAt(searchInPos++)))
          return 1; 
        if (searchForPos == searchForEnd)
          return (searchInPos != searchInEnd) ? 1 : 0; 
        result = 1;
      } 
      if (searchFor.charAt(searchForPos) == '_') {
        do {
          if (searchInPos == searchInEnd)
            return result; 
          searchInPos++;
        } while (++searchForPos < searchForEnd && searchFor.charAt(searchForPos) == '_');
        if (searchForPos == searchForEnd)
          break; 
      } 
      if (searchFor.charAt(searchForPos) == '%') {
        searchForPos++;
        for (; searchForPos != searchForEnd; searchForPos++) {
          if (searchFor.charAt(searchForPos) != '%')
            if (searchFor.charAt(searchForPos) == '_') {
              if (searchInPos == searchInEnd)
                return -1; 
              searchInPos++;
            } else {
              break;
            }  
        } 
        if (searchForPos == searchForEnd)
          return 0; 
        if (searchInPos == searchInEnd)
          return -1; 
        char cmp;
        if ((cmp = searchFor.charAt(searchForPos)) == '\\' && searchForPos + 1 != searchForEnd)
          cmp = searchFor.charAt(++searchForPos); 
        searchForPos++;
        while (true) {
          if (searchInPos != searchInEnd && Character.toUpperCase(searchIn.charAt(searchInPos)) != Character.toUpperCase(cmp)) {
            searchInPos++;
            continue;
          } 
          if (searchInPos++ == searchInEnd)
            return -1; 
          int tmp = wildCompareInternal(searchIn.substring(searchInPos), searchFor.substring(searchForPos));
          if (tmp <= 0)
            return tmp; 
          if (searchInPos == searchInEnd)
            break; 
        } 
        return -1;
      } 
    } 
    return (searchInPos != searchInEnd) ? 1 : 0;
  }
  
  public static int lastIndexOf(byte[] s, char c) {
    if (s == null)
      return -1; 
    for (int i = s.length - 1; i >= 0; i--) {
      if (s[i] == c)
        return i; 
    } 
    return -1;
  }
  
  public static int indexOf(byte[] s, char c) {
    if (s == null)
      return -1; 
    int length = s.length;
    for (int i = 0; i < length; i++) {
      if (s[i] == c)
        return i; 
    } 
    return -1;
  }
  
  public static boolean isNullOrEmpty(String str) {
    return (str == null || str.isEmpty());
  }
  
  public static boolean nullSafeEqual(String str1, String str2) {
    return ((str1 == null && str2 == null) || (str1 != null && str1.equals(str2)));
  }
  
  public static String stripCommentsAndHints(String source, String openingMarkers, String closingMarkers, boolean allowBackslashEscapes) {
    StringInspector strInspector = new StringInspector(source, openingMarkers, closingMarkers, "", allowBackslashEscapes ? SearchMode.__BSE_MRK_COM_MYM_HNT_WS : SearchMode.__MRK_COM_MYM_HNT_WS);
    return strInspector.stripCommentsAndHints();
  }
  
  public static String sanitizeProcOrFuncName(String src) {
    if (src == null || src.equals("%"))
      return null; 
    return src;
  }
  
  public static List<String> splitDBdotName(String source, String db, String quoteId, boolean isNoBslashEscSet) {
    String entityName;
    if (source == null || source.equals("%"))
      return Collections.emptyList(); 
    int dotIndex = -1;
    if (" ".equals(quoteId)) {
      dotIndex = source.indexOf(".");
    } else {
      dotIndex = indexOfIgnoreCase(0, source, ".", quoteId, quoteId, isNoBslashEscSet ? SearchMode.__MRK_WS : SearchMode.__BSE_MRK_WS);
    } 
    String database = db;
    if (dotIndex != -1) {
      database = unQuoteIdentifier(source.substring(0, dotIndex), quoteId);
      entityName = unQuoteIdentifier(source.substring(dotIndex + 1), quoteId);
    } else {
      entityName = unQuoteIdentifier(source, quoteId);
    } 
    return Arrays.asList(new String[] { database, entityName });
  }
  
  public static String getFullyQualifiedName(String db, String entity, String quoteId, boolean isPedantic) {
    StringBuilder fullyQualifiedName = new StringBuilder(quoteIdentifier((db == null) ? "" : db, quoteId, isPedantic));
    fullyQualifiedName.append('.');
    fullyQualifiedName.append(quoteIdentifier(entity, quoteId, isPedantic));
    return fullyQualifiedName.toString();
  }
  
  public static boolean isEmptyOrWhitespaceOnly(String str) {
    if (str == null || str.length() == 0)
      return true; 
    int length = str.length();
    for (int i = 0; i < length; i++) {
      if (!Character.isWhitespace(str.charAt(i)))
        return false; 
    } 
    return true;
  }
  
  public static String escapeQuote(String str, String quotChar) {
    if (str == null)
      return null; 
    str = toString(stripEnclosure(str.getBytes(), quotChar, quotChar));
    int lastNdx = str.indexOf(quotChar);
    String tmpSrc = str.substring(0, lastNdx);
    tmpSrc = tmpSrc + quotChar + quotChar;
    String tmpRest = str.substring(lastNdx + 1, str.length());
    lastNdx = tmpRest.indexOf(quotChar);
    while (lastNdx > -1) {
      tmpSrc = tmpSrc + tmpRest.substring(0, lastNdx);
      tmpSrc = tmpSrc + quotChar + quotChar;
      tmpRest = tmpRest.substring(lastNdx + 1, tmpRest.length());
      lastNdx = tmpRest.indexOf(quotChar);
    } 
    tmpSrc = tmpSrc + tmpRest;
    str = tmpSrc;
    return str;
  }
  
  public static String quoteIdentifier(String identifier, String quoteChar, boolean isPedantic) {
    if (identifier == null)
      return null; 
    identifier = identifier.trim();
    int quoteCharLength = quoteChar.length();
    if (quoteCharLength == 0)
      return identifier; 
    if (!isPedantic && identifier.startsWith(quoteChar) && identifier.endsWith(quoteChar)) {
      String identifierQuoteTrimmed = identifier.substring(quoteCharLength, identifier.length() - quoteCharLength);
      int quoteCharPos = identifierQuoteTrimmed.indexOf(quoteChar);
      while (quoteCharPos >= 0) {
        int quoteCharNextExpectedPos = quoteCharPos + quoteCharLength;
        int quoteCharNextPosition = identifierQuoteTrimmed.indexOf(quoteChar, quoteCharNextExpectedPos);
        if (quoteCharNextPosition == quoteCharNextExpectedPos)
          quoteCharPos = identifierQuoteTrimmed.indexOf(quoteChar, quoteCharNextPosition + quoteCharLength); 
      } 
      if (quoteCharPos < 0)
        return identifier; 
    } 
    return quoteChar + identifier.replaceAll(quoteChar, quoteChar + quoteChar) + quoteChar;
  }
  
  public static String quoteIdentifier(String identifier, boolean isPedantic) {
    return quoteIdentifier(identifier, "`", isPedantic);
  }
  
  public static String unQuoteIdentifier(String identifier, String quoteChar) {
    if (identifier == null)
      return null; 
    identifier = identifier.trim();
    int quoteCharLength = quoteChar.length();
    if (quoteCharLength == 0)
      return identifier; 
    if (identifier.startsWith(quoteChar) && identifier.endsWith(quoteChar)) {
      String identifierQuoteTrimmed = identifier.substring(quoteCharLength, identifier.length() - quoteCharLength);
      int quoteCharPos = identifierQuoteTrimmed.indexOf(quoteChar);
      while (quoteCharPos >= 0) {
        int quoteCharNextExpectedPos = quoteCharPos + quoteCharLength;
        int quoteCharNextPosition = identifierQuoteTrimmed.indexOf(quoteChar, quoteCharNextExpectedPos);
        if (quoteCharNextPosition == quoteCharNextExpectedPos) {
          quoteCharPos = identifierQuoteTrimmed.indexOf(quoteChar, quoteCharNextPosition + quoteCharLength);
          continue;
        } 
        return identifier;
      } 
      return identifier.substring(quoteCharLength, identifier.length() - quoteCharLength).replaceAll(quoteChar + quoteChar, quoteChar);
    } 
    return identifier;
  }
  
  public static int indexOfQuoteDoubleAware(String searchIn, String quoteChar, int startFrom) {
    if (searchIn == null || quoteChar == null || quoteChar.length() == 0 || startFrom > searchIn.length())
      return -1; 
    int lastIndex = searchIn.length() - 1;
    int beginPos = startFrom;
    int pos = -1;
    boolean next = true;
    while (next) {
      pos = searchIn.indexOf(quoteChar, beginPos);
      if (pos == -1 || pos == lastIndex || !searchIn.startsWith(quoteChar, pos + 1)) {
        next = false;
        continue;
      } 
      beginPos = pos + 2;
    } 
    return pos;
  }
  
  public static String toString(byte[] value, int offset, int length, String encoding) {
    if (encoding == null || "null".equalsIgnoreCase(encoding))
      return new String(value, offset, length); 
    try {
      return new String(value, offset, length, encoding);
    } catch (UnsupportedEncodingException uee) {
      throw (WrongArgumentException)ExceptionFactory.createException(WrongArgumentException.class, Messages.getString("StringUtils.0", new Object[] { encoding }), uee);
    } 
  }
  
  public static String toString(byte[] value, String encoding) {
    if (encoding == null)
      return new String(value); 
    try {
      return new String(value, encoding);
    } catch (UnsupportedEncodingException uee) {
      throw (WrongArgumentException)ExceptionFactory.createException(WrongArgumentException.class, Messages.getString("StringUtils.0", new Object[] { encoding }), uee);
    } 
  }
  
  public static String toString(byte[] value, Charset charset) {
    return new String(value, charset);
  }
  
  public static String toString(byte[] value, int offset, int length) {
    return new String(value, offset, length);
  }
  
  public static String toString(byte[] value) {
    return new String(value);
  }
  
  public static byte[] getBytes(char[] value) {
    return getBytes(value, 0, value.length);
  }
  
  public static byte[] getBytes(char[] c, String encoding) {
    return getBytes(c, 0, c.length, encoding);
  }
  
  public static byte[] getBytes(char[] value, int offset, int length) {
    return getBytes(value, offset, length, (String)null);
  }
  
  public static byte[] getBytes(char[] value, int offset, int length, String encoding) {
    Charset cs;
    try {
      if (encoding == null) {
        cs = Charset.defaultCharset();
      } else {
        cs = Charset.forName(encoding);
      } 
    } catch (UnsupportedCharsetException ex) {
      throw (WrongArgumentException)ExceptionFactory.createException(WrongArgumentException.class, Messages.getString("StringUtils.0", new Object[] { encoding }), ex);
    } 
    ByteBuffer buf = cs.encode(CharBuffer.wrap(value, offset, length));
    int encodedLen = buf.limit();
    byte[] asBytes = new byte[encodedLen];
    buf.get(asBytes, 0, encodedLen);
    return asBytes;
  }
  
  public static byte[] getBytes(String value) {
    return value.getBytes();
  }
  
  public static byte[] getBytes(String value, int offset, int length) {
    return value.substring(offset, offset + length).getBytes();
  }
  
  public static byte[] getBytes(String value, int offset, int length, String encoding) {
    if (encoding == null)
      return getBytes(value, offset, length); 
    try {
      return value.substring(offset, offset + length).getBytes(encoding);
    } catch (UnsupportedEncodingException uee) {
      throw (WrongArgumentException)ExceptionFactory.createException(WrongArgumentException.class, Messages.getString("StringUtils.0", new Object[] { encoding }), uee);
    } 
  }
  
  public static final boolean isValidIdChar(char c) {
    return ("abcdefghijklmnopqrstuvwxyzABCDEFGHIGKLMNOPQRSTUVWXYZ0123456789$_#@".indexOf(c) != -1);
  }
  
  private static final char[] HEX_DIGITS = new char[] { 
      '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 
      'a', 'b', 'c', 'd', 'e', 'f' };
  
  public static final void hexEscapeBlock(byte[] buf, int size, BiConsumer<Byte, Byte> bc) {
    for (int i = 0; i < size; i++)
      bc.accept(Byte.valueOf((byte)HEX_DIGITS[buf[i] >>> 4 & 0xF]), Byte.valueOf((byte)HEX_DIGITS[buf[i] & 0xF])); 
  }
  
  public static void appendAsHex(StringBuilder builder, byte[] bytes) {
    builder.append("0x");
    for (byte b : bytes)
      builder.append(HEX_DIGITS[b >>> 4 & 0xF]).append(HEX_DIGITS[b & 0xF]); 
  }
  
  public static void appendAsHex(StringBuilder builder, int value) {
    if (value == 0) {
      builder.append("0x0");
      return;
    } 
    int shift = 32;
    boolean nonZeroFound = false;
    builder.append("0x");
    do {
      shift -= 4;
      byte nibble = (byte)(value >>> shift & 0xF);
      if (nonZeroFound) {
        builder.append(HEX_DIGITS[nibble]);
      } else if (nibble != 0) {
        builder.append(HEX_DIGITS[nibble]);
        nonZeroFound = true;
      } 
    } while (shift != 0);
  }
  
  public static byte[] getBytesNullTerminated(String value, String encoding) {
    Charset cs = Charset.forName(encoding);
    ByteBuffer buf = cs.encode(value);
    int encodedLen = buf.limit();
    byte[] asBytes = new byte[encodedLen + 1];
    buf.get(asBytes, 0, encodedLen);
    asBytes[encodedLen] = 0;
    return asBytes;
  }
  
  public static boolean canHandleAsServerPreparedStatementNoCache(String sql, ServerVersion serverVersion, boolean allowMultiQueries, boolean noBackslashEscapes, boolean useAnsiQuotes) {
    if (startsWithIgnoreCaseAndNonAlphaNumeric(sql, "CALL"))
      return false; 
    boolean canHandleAsStatement = true;
    boolean allowBackslashEscapes = !noBackslashEscapes;
    String quoteChar = useAnsiQuotes ? "\"" : "'";
    if (allowMultiQueries) {
      if (indexOfIgnoreCase(0, sql, ";", quoteChar, quoteChar, allowBackslashEscapes ? SearchMode.__BSE_MRK_COM_MYM_HNT_WS : SearchMode.__MRK_COM_MYM_HNT_WS) != -1)
        canHandleAsStatement = false; 
    } else if (startsWithIgnoreCaseAndWs(sql, "XA ")) {
      canHandleAsStatement = false;
    } else if (startsWithIgnoreCaseAndWs(sql, "CREATE TABLE")) {
      canHandleAsStatement = false;
    } else if (startsWithIgnoreCaseAndWs(sql, "DO")) {
      canHandleAsStatement = false;
    } else if (startsWithIgnoreCaseAndWs(sql, "SET")) {
      canHandleAsStatement = false;
    } else if (startsWithIgnoreCaseAndWs(sql, "SHOW WARNINGS") && serverVersion.meetsMinimum(ServerVersion.parseVersion("5.7.2"))) {
      canHandleAsStatement = false;
    } else if (sql.startsWith("/* ping */")) {
      canHandleAsStatement = false;
    } 
    return canHandleAsStatement;
  }
  
  static final char[] EMPTY_SPACE = new char[255];
  
  static {
    for (int i = 0; i < EMPTY_SPACE.length; i++)
      EMPTY_SPACE[i] = ' '; 
  }
  
  public static String padString(String stringVal, int requiredLength) {
    int currentLength = stringVal.length();
    int difference = requiredLength - currentLength;
    if (difference > 0) {
      StringBuilder paddedBuf = new StringBuilder(requiredLength);
      paddedBuf.append(stringVal);
      paddedBuf.append(EMPTY_SPACE, 0, difference);
      return paddedBuf.toString();
    } 
    return stringVal;
  }
  
  public static int safeIntParse(String intAsString) {
    try {
      return Integer.parseInt(intAsString);
    } catch (NumberFormatException nfe) {
      return 0;
    } 
  }
  
  public static boolean isStrictlyNumeric(CharSequence cs) {
    if (cs == null || cs.length() == 0)
      return false; 
    for (int i = 0; i < cs.length(); i++) {
      if (!Character.isDigit(cs.charAt(i)))
        return false; 
    } 
    return true;
  }
  
  public static String safeTrim(String toTrim) {
    return isNullOrEmpty(toTrim) ? toTrim : toTrim.trim();
  }
  
  public static String stringArrayToString(String[] elems, String prefix, String midDelimiter, String lastDelimiter, String suffix) {
    StringBuilder valuesString = new StringBuilder();
    if (elems.length > 1) {
      valuesString.append(Arrays.<CharSequence>stream((CharSequence[])elems).limit((elems.length - 1)).collect(Collectors.joining(midDelimiter, prefix, lastDelimiter)));
    } else {
      valuesString.append(prefix);
    } 
    valuesString.append(elems[elems.length - 1]).append(suffix);
    return valuesString.toString();
  }
  
  public static boolean hasWildcards(String src) {
    return (indexOfIgnoreCase(0, src, "%") > -1 || indexOfIgnoreCase(0, src, "_") > -1);
  }
  
  public static String getUniqueSavepointId() {
    String uuid = UUID.randomUUID().toString();
    return uuid.replaceAll("-", "_");
  }
  
  public static String joinWithSerialComma(List<?> elements) {
    if (elements == null || elements.size() == 0)
      return ""; 
    if (elements.size() == 1)
      return elements.get(0).toString(); 
    if (elements.size() == 2)
      return (new StringBuilder()).append(elements.get(0)).append(" and ").append(elements.get(1)).toString(); 
    return (String)elements.subList(0, elements.size() - 1).stream().map(Object::toString).collect(Collectors.joining(", ", "", ", and ")) + elements
      .get(elements.size() - 1).toString();
  }
  
  public static byte[] unquoteBytes(byte[] bytes) {
    if (bytes[0] == 39 && bytes[bytes.length - 1] == 39) {
      byte[] valNoQuotes = new byte[bytes.length - 2];
      int j = 0;
      int quoteCnt = 0;
      for (int i = 1; i < bytes.length - 1; i++) {
        if (bytes[i] == 39) {
          quoteCnt++;
        } else {
          quoteCnt = 0;
        } 
        if (quoteCnt == 2) {
          quoteCnt = 0;
        } else {
          valNoQuotes[j++] = bytes[i];
        } 
      } 
      byte[] res = new byte[j];
      System.arraycopy(valNoQuotes, 0, res, 0, j);
      return res;
    } 
    return bytes;
  }
  
  public static byte[] quoteBytes(byte[] bytes) {
    byte[] withQuotes = new byte[bytes.length * 2 + 2];
    int j = 0;
    withQuotes[j++] = 39;
    for (int i = 0; i < bytes.length; i++) {
      if (bytes[i] == 39)
        withQuotes[j++] = 39; 
      withQuotes[j++] = bytes[i];
    } 
    withQuotes[j++] = 39;
    byte[] res = new byte[j];
    System.arraycopy(withQuotes, 0, res, 0, j);
    return res;
  }
  
  public static StringBuilder escapeString(StringBuilder buf, String x, boolean useAnsiQuotedIdentifiers, CharsetEncoder charsetEncoder) {
    int stringLength = x.length();
    buf.append('\'');
    for (int i = 0; i < stringLength; i++) {
      char c = x.charAt(i);
      switch (c) {
        case '\000':
          buf.append('\\');
          buf.append('0');
          break;
        case '\n':
          buf.append('\\');
          buf.append('n');
          break;
        case '\r':
          buf.append('\\');
          buf.append('r');
          break;
        case '\\':
          buf.append('\\');
          buf.append('\\');
          break;
        case '\'':
          buf.append('\'');
          buf.append('\'');
          break;
        case '"':
          if (useAnsiQuotedIdentifiers)
            buf.append('\\'); 
          buf.append('"');
          break;
        case '\032':
          buf.append('\\');
          buf.append('Z');
          break;
        case '¥':
        case '₩':
          if (charsetEncoder != null) {
            CharBuffer cbuf = CharBuffer.allocate(1);
            ByteBuffer bbuf = ByteBuffer.allocate(1);
            cbuf.put(c);
            cbuf.position(0);
            charsetEncoder.encode(cbuf, bbuf, true);
            if (bbuf.get(0) == 92)
              buf.append('\\'); 
          } 
          buf.append(c);
          break;
        default:
          buf.append(c);
          break;
      } 
    } 
    buf.append('\'');
    return buf;
  }
  
  public static void escapeBytes(ByteArrayOutputStream bOut, byte[] x) {
    int numBytes = x.length;
    for (int i = 0; i < numBytes; i++) {
      byte b = x[i];
      switch (b) {
        case 0:
          bOut.write(92);
          bOut.write(48);
          break;
        case 10:
          bOut.write(92);
          bOut.write(110);
          break;
        case 13:
          bOut.write(92);
          bOut.write(114);
          break;
        case 92:
          bOut.write(92);
          bOut.write(92);
          break;
        case 39:
          bOut.write(92);
          bOut.write(39);
          break;
        case 34:
          bOut.write(92);
          bOut.write(34);
          break;
        case 26:
          bOut.write(92);
          bOut.write(90);
          break;
        default:
          bOut.write(b);
          break;
      } 
    } 
  }
  
  public static String urlEncode(String stringToEncode) {
    try {
      return URLEncoder.encode(stringToEncode, StandardCharsets.UTF_8.name());
    } catch (UnsupportedEncodingException e) {
      return null;
    } 
  }
}
