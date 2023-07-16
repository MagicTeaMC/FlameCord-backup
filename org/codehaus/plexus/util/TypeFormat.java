package org.codehaus.plexus.util;

public final class TypeFormat {
  private static final char[] DIGITS = new char[] { 
      '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 
      'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 
      'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 
      'u', 'v', 'w', 'x', 'y', 'z' };
  
  public static int indexOf(CharSequence pattern, CharSequence chars, int fromIndex) {
    int patternLength = pattern.length();
    fromIndex = Math.max(0, fromIndex);
    if (patternLength != 0) {
      char firstChar = pattern.charAt(0);
      int last = chars.length() - patternLength;
      for (int i = fromIndex; i <= last; i++) {
        if (chars.charAt(i) == firstChar) {
          boolean match = true;
          for (int j = 1; j < patternLength; j++) {
            if (chars.charAt(i + j) != pattern.charAt(j)) {
              match = false;
              break;
            } 
          } 
          if (match)
            return i; 
        } 
      } 
      return -1;
    } 
    return Math.min(0, fromIndex);
  }
  
  public static boolean parseBoolean(CharSequence chars) {
    return (chars.length() == 4 && (chars.charAt(0) == 't' || chars.charAt(0) == 'T') && (chars.charAt(1) == 'r' || chars.charAt(1) == 'R') && (chars.charAt(2) == 'u' || chars.charAt(2) == 'U') && (chars.charAt(3) == 'e' || chars.charAt(3) == 'E'));
  }
  
  public static short parseShort(CharSequence chars) {
    return parseShort(chars, 10);
  }
  
  public static short parseShort(CharSequence chars, int radix) {
    try {
      boolean isNegative = (chars.charAt(0) == '-');
      int result = 0;
      int limit = isNegative ? -32768 : -32767;
      int multmin = limit / radix;
      int length = chars.length();
      int i = (isNegative || chars.charAt(0) == '+') ? 1 : 0;
      do {
        int digit = Character.digit(chars.charAt(i), radix);
        int tmp = result * radix;
        if (digit < 0 || result < multmin || tmp < limit + digit)
          throw new NumberFormatException("For input characters: \"" + chars.toString() + "\""); 
        result = tmp - digit;
      } while (++i < length);
      return (short)(isNegative ? result : -result);
    } catch (IndexOutOfBoundsException e) {
      throw new NumberFormatException("For input characters: \"" + chars.toString() + "\"");
    } 
  }
  
  public static int parseInt(CharSequence chars) {
    return parseInt(chars, 10);
  }
  
  public static int parseInt(CharSequence chars, int radix) {
    try {
      boolean isNegative = (chars.charAt(0) == '-');
      int result = 0;
      int limit = isNegative ? Integer.MIN_VALUE : -2147483647;
      int multmin = limit / radix;
      int length = chars.length();
      int i = (isNegative || chars.charAt(0) == '+') ? 1 : 0;
      do {
        int digit = Character.digit(chars.charAt(i), radix);
        int tmp = result * radix;
        if (digit < 0 || result < multmin || tmp < limit + digit)
          throw new NumberFormatException("For input characters: \"" + chars.toString() + "\""); 
        result = tmp - digit;
      } while (++i < length);
      return isNegative ? result : -result;
    } catch (IndexOutOfBoundsException e) {
      throw new NumberFormatException("For input characters: \"" + chars.toString() + "\"");
    } 
  }
  
  public static long parseLong(CharSequence chars) {
    return parseLong(chars, 10);
  }
  
  public static long parseLong(CharSequence chars, int radix) {
    try {
      boolean isNegative = (chars.charAt(0) == '-');
      long result = 0L;
      long limit = isNegative ? Long.MIN_VALUE : -9223372036854775807L;
      long multmin = limit / radix;
      int length = chars.length();
      int i = (isNegative || chars.charAt(0) == '+') ? 1 : 0;
      do {
        int digit = Character.digit(chars.charAt(i), radix);
        long tmp = result * radix;
        if (digit < 0 || result < multmin || tmp < limit + digit)
          throw new NumberFormatException("For input characters: \"" + chars.toString() + "\""); 
        result = tmp - digit;
      } while (++i < length);
      return isNegative ? result : -result;
    } catch (IndexOutOfBoundsException e) {
      throw new NumberFormatException("For input characters: \"" + chars.toString() + "\"");
    } 
  }
  
  public static float parseFloat(CharSequence chars) {
    double d = parseDouble(chars);
    if (d >= 1.401298464324817E-45D && d <= 3.4028234663852886E38D)
      return (float)d; 
    throw new NumberFormatException("Float overflow for input characters: \"" + chars.toString() + "\"");
  }
  
  public static double parseDouble(CharSequence chars) throws NumberFormatException {
    try {
      int length = chars.length();
      double result = 0.0D;
      int exp = 0;
      boolean isNegative = (chars.charAt(0) == '-');
      int i = (isNegative || chars.charAt(0) == '+') ? 1 : 0;
      if (chars.charAt(i) == 'N' || chars.charAt(i) == 'I') {
        if (chars.toString().equals("NaN"))
          return Double.NaN; 
        if (chars.subSequence(i, length).toString().equals("Infinity"))
          return isNegative ? Double.NEGATIVE_INFINITY : Double.POSITIVE_INFINITY; 
      } 
      boolean fraction = false;
      do {
        char c = chars.charAt(i);
        if (c == '.' && !fraction) {
          fraction = true;
        } else {
          if (c == 'e' || c == 'E')
            break; 
          if (c >= '0' && c <= '9') {
            result = result * 10.0D + (c - 48);
            if (fraction)
              exp--; 
          } else {
            throw new NumberFormatException("For input characters: \"" + chars.toString() + "\"");
          } 
        } 
      } while (++i < length);
      result = isNegative ? -result : result;
      if (i < length) {
        i++;
        boolean negE = (chars.charAt(i) == '-');
        i = (negE || chars.charAt(i) == '+') ? (i + 1) : i;
        int valE = 0;
        do {
          char c = chars.charAt(i);
          if (c >= '0' && c <= '9') {
            valE = valE * 10 + c - 48;
            if (valE > 10000000)
              valE = 10000000; 
          } else {
            throw new NumberFormatException("For input characters: \"" + chars.toString() + "\"");
          } 
        } while (++i < length);
        exp += negE ? -valE : valE;
      } 
      return multE(result, exp);
    } catch (IndexOutOfBoundsException e) {
      throw new NumberFormatException("For input characters: \"" + chars.toString() + "\"");
    } 
  }
  
  public static StringBuffer format(boolean b, StringBuffer sb) {
    return b ? sb.append("true") : sb.append("false");
  }
  
  public static StringBuffer format(short s, StringBuffer sb) {
    return format(s, sb);
  }
  
  public static StringBuffer format(short s, int radix, StringBuffer sb) {
    return format(s, radix, sb);
  }
  
  public static StringBuffer format(int i, StringBuffer sb) {
    if (i <= 0) {
      if (i == Integer.MIN_VALUE)
        return sb.append("-2147483648"); 
      if (i == 0)
        return sb.append('0'); 
      i = -i;
      sb.append('-');
    } 
    int j = 1;
    for (; j < 10 && i >= INT_POW_10[j]; j++);
    for (; --j >= 0; j--) {
      int pow10 = INT_POW_10[j];
      int digit = i / pow10;
      i -= digit * pow10;
      sb.append(DIGITS[digit]);
    } 
    return sb;
  }
  
  private static final int[] INT_POW_10 = new int[10];
  
  static {
    int j = 1;
    for (int i = 0; i < 10; i++) {
      INT_POW_10[i] = j;
      j *= 10;
    } 
  }
  
  public static StringBuffer format(int i, int radix, StringBuffer sb) {
    if (radix == 10)
      return format(i, sb); 
    if (radix < 2 || radix > 36)
      throw new IllegalArgumentException("radix: " + radix); 
    if (i < 0) {
      sb.append('-');
    } else {
      i = -i;
    } 
    format2(i, radix, sb);
    return sb;
  }
  
  private static void format2(int i, int radix, StringBuffer sb) {
    if (i <= -radix) {
      format2(i / radix, radix, sb);
      sb.append(DIGITS[-(i % radix)]);
    } else {
      sb.append(DIGITS[-i]);
    } 
  }
  
  public static StringBuffer format(long l, StringBuffer sb) {
    if (l <= 0L) {
      if (l == Long.MIN_VALUE)
        return sb.append("-9223372036854775808"); 
      if (l == 0L)
        return sb.append('0'); 
      l = -l;
      sb.append('-');
    } 
    int j = 1;
    for (; j < 19 && l >= LONG_POW_10[j]; j++);
    for (; --j >= 0; j--) {
      long pow10 = LONG_POW_10[j];
      int digit = (int)(l / pow10);
      l -= digit * pow10;
      sb.append(DIGITS[digit]);
    } 
    return sb;
  }
  
  private static final long[] LONG_POW_10 = new long[19];
  
  static {
    long pow = 1L;
    for (int k = 0; k < 19; k++) {
      LONG_POW_10[k] = pow;
      pow *= 10L;
    } 
  }
  
  public static StringBuffer format(long l, int radix, StringBuffer sb) {
    if (radix == 10)
      return format(l, sb); 
    if (radix < 2 || radix > 36)
      throw new IllegalArgumentException("radix: " + radix); 
    if (l < 0L) {
      sb.append('-');
    } else {
      l = -l;
    } 
    format2(l, radix, sb);
    return sb;
  }
  
  private static void format2(long l, int radix, StringBuffer sb) {
    if (l <= -radix) {
      format2(l / radix, radix, sb);
      sb.append(DIGITS[(int)-(l % radix)]);
    } else {
      sb.append(DIGITS[(int)-l]);
    } 
  }
  
  public static StringBuffer format(float f, StringBuffer sb) {
    return format(f, 0.0F, sb);
  }
  
  public static StringBuffer format(float f, float precision, StringBuffer sb) {
    boolean precisionOnLastDigit;
    if (precision > 0.0F) {
      precisionOnLastDigit = true;
    } else if (precision == 0.0F) {
      if (f != 0.0F) {
        precisionOnLastDigit = false;
        precision = Math.max(Math.abs(f * FLOAT_RELATIVE_ERROR), Float.MIN_VALUE);
      } else {
        return sb.append("0.0");
      } 
    } else {
      throw new IllegalArgumentException("precision: Negative values not allowed");
    } 
    return format(f, precision, precisionOnLastDigit, sb);
  }
  
  public static StringBuffer format(double d, StringBuffer sb) {
    return format(d, 0.0D, sb);
  }
  
  public static StringBuffer format(double d, int digits, StringBuffer sb) {
    if (digits >= 1 && digits <= 19) {
      double precision = Math.abs(d / DOUBLE_POW_10[digits - 1]);
      return format(d, precision, sb);
    } 
    throw new IllegalArgumentException("digits: " + digits + " is not in range [1 .. 19]");
  }
  
  public static StringBuffer format(double d, double precision, StringBuffer sb) {
    boolean precisionOnLastDigit = false;
    if (precision > 0.0D) {
      precisionOnLastDigit = true;
    } else if (precision == 0.0D) {
      if (d != 0.0D) {
        precision = Math.max(Math.abs(d * DOUBLE_RELATIVE_ERROR), Double.MIN_VALUE);
      } else {
        return sb.append("0.0");
      } 
    } else if (precision < 0.0D) {
      throw new IllegalArgumentException("precision: Negative values not allowed");
    } 
    return format(d, precision, precisionOnLastDigit, sb);
  }
  
  private static StringBuffer format(double d, double precision, boolean precisionOnLastDigit, StringBuffer sb) {
    if (Double.isNaN(d))
      return sb.append("NaN"); 
    if (Double.isInfinite(d))
      return (d >= 0.0D) ? sb.append("Infinity") : sb.append("-Infinity"); 
    if (d < 0.0D) {
      d = -d;
      sb.append('-');
    } 
    int rank = (int)Math.floor(Math.log(precision) / LOG_10);
    double digitValue = multE(d, -rank);
    if (digitValue >= 9.223372036854776E18D)
      throw new IllegalArgumentException("Specified precision would result in too many digits"); 
    int digitStart = sb.length();
    format(Math.round(digitValue), sb);
    int digitLength = sb.length() - digitStart;
    int dotPos = digitLength + rank;
    boolean useScientificNotation = false;
    if (dotPos <= -LEADING_ZEROS.length || dotPos > digitLength) {
      sb.insert(digitStart + 1, '.');
      useScientificNotation = true;
    } else if (dotPos > 0) {
      sb.insert(digitStart + dotPos, '.');
    } else {
      sb.insert(digitStart, LEADING_ZEROS[-dotPos]);
    } 
    if (!precisionOnLastDigit) {
      int newLength = sb.length();
      while (true) {
        newLength--;
        if (sb.charAt(newLength) != '0') {
          sb.setLength(newLength + 1);
          break;
        } 
      } 
    } 
    if (sb.charAt(sb.length() - 1) == '.')
      if (precisionOnLastDigit) {
        sb.setLength(sb.length() - 1);
      } else {
        sb.append('0');
      }  
    if (useScientificNotation) {
      sb.append('E');
      format(dotPos - 1, sb);
    } 
    return sb;
  }
  
  private static final double LOG_10 = Math.log(10.0D);
  
  private static final float FLOAT_RELATIVE_ERROR = (float)Math.pow(2.0D, -24.0D);
  
  private static final double DOUBLE_RELATIVE_ERROR = Math.pow(2.0D, -53.0D);
  
  private static String[] LEADING_ZEROS = new String[] { "0.", "0.0", "0.00" };
  
  private static final double multE(double value, int E) {
    if (E >= 0) {
      if (E <= 308)
        return value * DOUBLE_POW_10[E]; 
      value *= 1.0E21D;
      E = Math.min(308, E - 21);
      return value * DOUBLE_POW_10[E];
    } 
    if (E >= -308)
      return value / DOUBLE_POW_10[-E]; 
    value /= 1.0E21D;
    E = Math.max(-308, E + 21);
    return value / DOUBLE_POW_10[-E];
  }
  
  private static final double[] DOUBLE_POW_10 = new double[] { 
      1.0D, 10.0D, 100.0D, 1000.0D, 10000.0D, 100000.0D, 1000000.0D, 1.0E7D, 1.0E8D, 1.0E9D, 
      1.0E10D, 1.0E11D, 1.0E12D, 1.0E13D, 1.0E14D, 1.0E15D, 1.0E16D, 1.0E17D, 1.0E18D, 1.0E19D, 
      1.0E20D, 1.0E21D, 1.0E22D, 9.999999999999999E22D, 1.0E24D, 1.0E25D, 1.0E26D, 1.0E27D, 1.0E28D, 1.0E29D, 
      1.0E30D, 1.0E31D, 1.0E32D, 1.0E33D, 1.0E34D, 1.0E35D, 1.0E36D, 1.0E37D, 1.0E38D, 1.0E39D, 
      1.0E40D, 1.0E41D, 1.0E42D, 1.0E43D, 1.0E44D, 1.0E45D, 1.0E46D, 1.0E47D, 1.0E48D, 1.0E49D, 
      1.0E50D, 1.0E51D, 1.0E52D, 1.0E53D, 1.0E54D, 1.0E55D, 1.0E56D, 1.0E57D, 1.0E58D, 1.0E59D, 
      1.0E60D, 1.0E61D, 1.0E62D, 1.0E63D, 1.0E64D, 1.0E65D, 1.0E66D, 1.0E67D, 1.0E68D, 1.0E69D, 
      1.0E70D, 1.0E71D, 1.0E72D, 1.0E73D, 1.0E74D, 1.0E75D, 1.0E76D, 1.0E77D, 1.0E78D, 1.0E79D, 
      1.0E80D, 1.0E81D, 1.0E82D, 1.0E83D, 1.0E84D, 1.0E85D, 1.0E86D, 1.0E87D, 1.0E88D, 1.0E89D, 
      1.0E90D, 1.0E91D, 1.0E92D, 1.0E93D, 1.0E94D, 1.0E95D, 1.0E96D, 1.0E97D, 1.0E98D, 1.0E99D, 
      1.0E100D, 1.0E101D, 1.0E102D, 1.0E103D, 1.0E104D, 1.0E105D, 1.0E106D, 1.0E107D, 1.0E108D, 1.0E109D, 
      1.0E110D, 1.0E111D, 1.0E112D, 1.0E113D, 1.0E114D, 1.0E115D, 1.0E116D, 1.0E117D, 1.0E118D, 1.0E119D, 
      1.0E120D, 1.0E121D, 1.0E122D, 1.0E123D, 1.0E124D, 1.0E125D, 1.0E126D, 1.0E127D, 1.0E128D, 1.0E129D, 
      1.0E130D, 1.0E131D, 1.0E132D, 1.0E133D, 1.0E134D, 1.0E135D, 1.0E136D, 1.0E137D, 1.0E138D, 1.0E139D, 
      1.0E140D, 1.0E141D, 1.0E142D, 1.0E143D, 1.0E144D, 1.0E145D, 1.0E146D, 1.0E147D, 1.0E148D, 1.0E149D, 
      1.0E150D, 1.0E151D, 1.0E152D, 1.0E153D, 1.0E154D, 1.0E155D, 1.0E156D, 1.0E157D, 1.0E158D, 1.0E159D, 
      1.0E160D, 1.0E161D, 1.0E162D, 1.0E163D, 1.0E164D, 1.0E165D, 1.0E166D, 1.0E167D, 1.0E168D, 1.0E169D, 
      1.0E170D, 1.0E171D, 1.0E172D, 1.0E173D, 1.0E174D, 1.0E175D, 1.0E176D, 1.0E177D, 1.0E178D, 1.0E179D, 
      1.0E180D, 1.0E181D, 1.0E182D, 1.0E183D, 1.0E184D, 1.0E185D, 1.0E186D, 1.0E187D, 1.0E188D, 1.0E189D, 
      1.0E190D, 1.0E191D, 1.0E192D, 1.0E193D, 1.0E194D, 1.0E195D, 1.0E196D, 1.0E197D, 1.0E198D, 1.0E199D, 
      1.0E200D, 1.0E201D, 1.0E202D, 1.0E203D, 1.0E204D, 1.0E205D, 1.0E206D, 1.0E207D, 1.0E208D, 1.0E209D, 
      1.0E210D, 1.0E211D, 1.0E212D, 1.0E213D, 1.0E214D, 1.0E215D, 1.0E216D, 1.0E217D, 1.0E218D, 1.0E219D, 
      1.0E220D, 1.0E221D, 1.0E222D, 1.0E223D, 1.0E224D, 1.0E225D, 1.0E226D, 1.0E227D, 1.0E228D, 1.0E229D, 
      1.0E230D, 1.0E231D, 1.0E232D, 1.0E233D, 1.0E234D, 1.0E235D, 1.0E236D, 1.0E237D, 1.0E238D, 1.0E239D, 
      1.0E240D, 1.0E241D, 1.0E242D, 1.0E243D, 1.0E244D, 1.0E245D, 1.0E246D, 1.0E247D, 1.0E248D, 1.0E249D, 
      1.0E250D, 1.0E251D, 1.0E252D, 1.0E253D, 1.0E254D, 1.0E255D, 1.0E256D, 1.0E257D, 1.0E258D, 1.0E259D, 
      1.0E260D, 1.0E261D, 1.0E262D, 1.0E263D, 1.0E264D, 1.0E265D, 1.0E266D, 1.0E267D, 1.0E268D, 1.0E269D, 
      1.0E270D, 1.0E271D, 1.0E272D, 1.0E273D, 1.0E274D, 1.0E275D, 1.0E276D, 1.0E277D, 1.0E278D, 1.0E279D, 
      1.0E280D, 1.0E281D, 1.0E282D, 1.0E283D, 1.0E284D, 1.0E285D, 1.0E286D, 1.0E287D, 1.0E288D, 1.0E289D, 
      1.0E290D, 1.0E291D, 1.0E292D, 1.0E293D, 1.0E294D, 1.0E295D, 1.0E296D, 1.0E297D, 1.0E298D, 1.0E299D, 
      1.0E300D, 1.0E301D, 1.0E302D, 1.0E303D, 1.0E304D, 1.0E305D, 1.0E306D, 1.0E307D, 1.0E308D };
}
