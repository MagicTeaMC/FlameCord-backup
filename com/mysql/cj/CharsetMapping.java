package com.mysql.cj;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

public class CharsetMapping {
  public static final int MAP_SIZE = 1024;
  
  private static final String[] COLLATION_INDEX_TO_COLLATION_NAME;
  
  private static final Map<Integer, MysqlCharset> COLLATION_INDEX_TO_CHARSET;
  
  private static final Map<String, MysqlCharset> CHARSET_NAME_TO_CHARSET;
  
  private static final Map<String, Integer> CHARSET_NAME_TO_COLLATION_INDEX;
  
  private static final Map<String, Integer> COLLATION_NAME_TO_COLLATION_INDEX;
  
  private static final Map<String, List<MysqlCharset>> JAVA_ENCODING_UC_TO_MYSQL_CHARSET;
  
  private static final Set<String> MULTIBYTE_ENCODINGS;
  
  private static final Set<Integer> IMPERMISSIBLE_INDEXES;
  
  public static final String MYSQL_CHARSET_NAME_armscii8 = "armscii8";
  
  public static final String MYSQL_CHARSET_NAME_ascii = "ascii";
  
  public static final String MYSQL_CHARSET_NAME_big5 = "big5";
  
  public static final String MYSQL_CHARSET_NAME_binary = "binary";
  
  public static final String MYSQL_CHARSET_NAME_cp1250 = "cp1250";
  
  public static final String MYSQL_CHARSET_NAME_cp1251 = "cp1251";
  
  public static final String MYSQL_CHARSET_NAME_cp1256 = "cp1256";
  
  public static final String MYSQL_CHARSET_NAME_cp1257 = "cp1257";
  
  public static final String MYSQL_CHARSET_NAME_cp850 = "cp850";
  
  public static final String MYSQL_CHARSET_NAME_cp852 = "cp852";
  
  public static final String MYSQL_CHARSET_NAME_cp866 = "cp866";
  
  public static final String MYSQL_CHARSET_NAME_cp932 = "cp932";
  
  public static final String MYSQL_CHARSET_NAME_dec8 = "dec8";
  
  public static final String MYSQL_CHARSET_NAME_eucjpms = "eucjpms";
  
  public static final String MYSQL_CHARSET_NAME_euckr = "euckr";
  
  public static final String MYSQL_CHARSET_NAME_gb18030 = "gb18030";
  
  public static final String MYSQL_CHARSET_NAME_gb2312 = "gb2312";
  
  public static final String MYSQL_CHARSET_NAME_gbk = "gbk";
  
  public static final String MYSQL_CHARSET_NAME_geostd8 = "geostd8";
  
  public static final String MYSQL_CHARSET_NAME_greek = "greek";
  
  public static final String MYSQL_CHARSET_NAME_hebrew = "hebrew";
  
  public static final String MYSQL_CHARSET_NAME_hp8 = "hp8";
  
  public static final String MYSQL_CHARSET_NAME_keybcs2 = "keybcs2";
  
  public static final String MYSQL_CHARSET_NAME_koi8r = "koi8r";
  
  public static final String MYSQL_CHARSET_NAME_koi8u = "koi8u";
  
  public static final String MYSQL_CHARSET_NAME_latin1 = "latin1";
  
  public static final String MYSQL_CHARSET_NAME_latin2 = "latin2";
  
  public static final String MYSQL_CHARSET_NAME_latin5 = "latin5";
  
  public static final String MYSQL_CHARSET_NAME_latin7 = "latin7";
  
  public static final String MYSQL_CHARSET_NAME_macce = "macce";
  
  public static final String MYSQL_CHARSET_NAME_macroman = "macroman";
  
  public static final String MYSQL_CHARSET_NAME_sjis = "sjis";
  
  public static final String MYSQL_CHARSET_NAME_swe7 = "swe7";
  
  public static final String MYSQL_CHARSET_NAME_tis620 = "tis620";
  
  public static final String MYSQL_CHARSET_NAME_ucs2 = "ucs2";
  
  public static final String MYSQL_CHARSET_NAME_ujis = "ujis";
  
  public static final String MYSQL_CHARSET_NAME_utf16 = "utf16";
  
  public static final String MYSQL_CHARSET_NAME_utf16le = "utf16le";
  
  public static final String MYSQL_CHARSET_NAME_utf32 = "utf32";
  
  public static final String MYSQL_CHARSET_NAME_utf8 = "utf8";
  
  public static final String MYSQL_CHARSET_NAME_utf8mb3 = "utf8mb3";
  
  public static final String MYSQL_CHARSET_NAME_utf8mb4 = "utf8mb4";
  
  public static final int MYSQL_COLLATION_INDEX_utf8mb4_general_ci = 45;
  
  public static final int MYSQL_COLLATION_INDEX_utf8mb4_0900_ai_ci = 255;
  
  public static final int MYSQL_COLLATION_INDEX_binary = 63;
  
  static {
    MysqlCharset[] charset = { 
        new MysqlCharset("ascii", 1, 0, new String[] { "US-ASCII", "ASCII" }), new MysqlCharset("big5", 2, 0, new String[] { "Big5" }), new MysqlCharset("gbk", 2, 0, new String[] { "GBK" }), new MysqlCharset("sjis", 2, 0, new String[] { "SHIFT_JIS", "Cp943", "WINDOWS-31J" }), new MysqlCharset("cp932", 2, 1, new String[] { "WINDOWS-31J" }), new MysqlCharset("gb2312", 2, 0, new String[] { "GB2312" }), new MysqlCharset("ujis", 3, 0, new String[] { "EUC_JP" }), new MysqlCharset("eucjpms", 3, 0, new String[] { "EUC_JP_Solaris" }, new ServerVersion(5, 0, 3)), new MysqlCharset("gb18030", 4, 0, new String[] { "GB18030" }, new ServerVersion(5, 7, 4)), new MysqlCharset("euckr", 2, 0, new String[] { "EUC-KR" }), 
        new MysqlCharset("latin1", 1, 1, new String[] { "Cp1252", "ISO8859_1" }), new MysqlCharset("swe7", 1, 0, new String[] { "Cp1252" }), new MysqlCharset("hp8", 1, 0, new String[] { "Cp1252" }), new MysqlCharset("dec8", 1, 0, new String[] { "Cp1252" }), new MysqlCharset("armscii8", 1, 0, new String[] { "Cp1252" }), new MysqlCharset("geostd8", 1, 0, new String[] { "Cp1252" }), new MysqlCharset("latin2", 1, 0, new String[] { "ISO8859_2" }), new MysqlCharset("greek", 1, 0, new String[] { "ISO8859_7", "greek" }), new MysqlCharset("latin7", 1, 0, new String[] { "ISO-8859-13" }), new MysqlCharset("hebrew", 1, 0, new String[] { "ISO8859_8" }), 
        new MysqlCharset("latin5", 1, 0, new String[] { "ISO8859_9" }), new MysqlCharset("cp850", 1, 0, new String[] { "Cp850", "Cp437" }), new MysqlCharset("cp852", 1, 0, new String[] { "Cp852" }), new MysqlCharset("keybcs2", 1, 0, new String[] { "Cp852" }), new MysqlCharset("cp866", 1, 0, new String[] { "Cp866" }), new MysqlCharset("koi8r", 1, 1, new String[] { "KOI8_R" }), new MysqlCharset("koi8u", 1, 0, new String[] { "KOI8_R" }), new MysqlCharset("tis620", 1, 0, new String[] { "TIS620" }), new MysqlCharset("cp1250", 1, 0, new String[] { "Cp1250" }), new MysqlCharset("cp1251", 1, 1, new String[] { "Cp1251" }), 
        new MysqlCharset("cp1256", 1, 0, new String[] { "Cp1256" }), new MysqlCharset("cp1257", 1, 0, new String[] { "Cp1257" }), new MysqlCharset("macroman", 1, 0, new String[] { "MacRoman" }), new MysqlCharset("macce", 1, 0, new String[] { "MacCentralEurope" }), new MysqlCharset("utf8mb3", 3, 0, new String[] { "UTF-8" }, new String[] { "utf8" }), new MysqlCharset("utf8mb4", 4, 1, new String[] { "UTF-8" }), new MysqlCharset("binary", 1, 1, new String[] { "ISO8859_1" }), new MysqlCharset("ucs2", 2, 0, new String[] { "UnicodeBig" }), new MysqlCharset("utf16", 4, 0, new String[] { "UTF-16" }), new MysqlCharset("utf16le", 4, 0, new String[] { "UTF-16LE" }), 
        new MysqlCharset("utf32", 4, 0, new String[] { "UTF-32" }) };
    HashMap<String, MysqlCharset> charsetNameToMysqlCharsetMap = new HashMap<>();
    HashMap<String, List<MysqlCharset>> javaUcToMysqlCharsetMap = new HashMap<>();
    Set<String> tempMultibyteEncodings = new HashSet<>();
    for (int i = 0; i < charset.length; i++) {
      String charsetName = (charset[i]).charsetName;
      charsetNameToMysqlCharsetMap.put(charsetName, charset[i]);
      for (String alias : (charset[i]).aliases)
        charsetNameToMysqlCharsetMap.put(alias, charset[i]); 
      for (String encUC : (charset[i]).javaEncodingsUc) {
        List<MysqlCharset> charsets = javaUcToMysqlCharsetMap.get(encUC);
        if (charsets == null) {
          charsets = new ArrayList<>();
          javaUcToMysqlCharsetMap.put(encUC, charsets);
        } 
        charsets.add(charset[i]);
        if ((charset[i]).mblen > 1)
          tempMultibyteEncodings.add(encUC); 
      } 
    } 
    CHARSET_NAME_TO_CHARSET = Collections.unmodifiableMap(charsetNameToMysqlCharsetMap);
    JAVA_ENCODING_UC_TO_MYSQL_CHARSET = Collections.unmodifiableMap(javaUcToMysqlCharsetMap);
    MULTIBYTE_ENCODINGS = Collections.unmodifiableSet(tempMultibyteEncodings);
    Collation[] collation = new Collation[1024];
    collation[1] = new Collation(1, "big5_chinese_ci", 1, "big5");
    collation[2] = new Collation(2, "latin2_czech_cs", 0, "latin2");
    collation[3] = new Collation(3, "dec8_swedish_ci", 0, "dec8");
    collation[4] = new Collation(4, "cp850_general_ci", 1, "cp850");
    collation[5] = new Collation(5, "latin1_german1_ci", 0, "latin1");
    collation[6] = new Collation(6, "hp8_english_ci", 0, "hp8");
    collation[7] = new Collation(7, "koi8r_general_ci", 0, "koi8r");
    collation[8] = new Collation(8, "latin1_swedish_ci", 1, "latin1");
    collation[9] = new Collation(9, "latin2_general_ci", 1, "latin2");
    collation[10] = new Collation(10, "swe7_swedish_ci", 0, "swe7");
    collation[11] = new Collation(11, "ascii_general_ci", 0, "ascii");
    collation[12] = new Collation(12, "ujis_japanese_ci", 0, "ujis");
    collation[13] = new Collation(13, "sjis_japanese_ci", 0, "sjis");
    collation[14] = new Collation(14, "cp1251_bulgarian_ci", 0, "cp1251");
    collation[15] = new Collation(15, "latin1_danish_ci", 0, "latin1");
    collation[16] = new Collation(16, "hebrew_general_ci", 0, "hebrew");
    collation[18] = new Collation(18, "tis620_thai_ci", 0, "tis620");
    collation[19] = new Collation(19, "euckr_korean_ci", 0, "euckr");
    collation[20] = new Collation(20, "latin7_estonian_cs", 0, "latin7");
    collation[21] = new Collation(21, "latin2_hungarian_ci", 0, "latin2");
    collation[22] = new Collation(22, "koi8u_general_ci", 0, "koi8u");
    collation[23] = new Collation(23, "cp1251_ukrainian_ci", 0, "cp1251");
    collation[24] = new Collation(24, "gb2312_chinese_ci", 0, "gb2312");
    collation[25] = new Collation(25, "greek_general_ci", 0, "greek");
    collation[26] = new Collation(26, "cp1250_general_ci", 1, "cp1250");
    collation[27] = new Collation(27, "latin2_croatian_ci", 0, "latin2");
    collation[28] = new Collation(28, "gbk_chinese_ci", 1, "gbk");
    collation[29] = new Collation(29, "cp1257_lithuanian_ci", 0, "cp1257");
    collation[30] = new Collation(30, "latin5_turkish_ci", 1, "latin5");
    collation[31] = new Collation(31, "latin1_german2_ci", 0, "latin1");
    collation[32] = new Collation(32, "armscii8_general_ci", 0, "armscii8");
    collation[33] = new Collation(33, new String[] { "utf8mb3_general_ci", "utf8_general_ci" }, 1, "utf8mb3");
    collation[34] = new Collation(34, "cp1250_czech_cs", 0, "cp1250");
    collation[35] = new Collation(35, "ucs2_general_ci", 1, "ucs2");
    collation[36] = new Collation(36, "cp866_general_ci", 1, "cp866");
    collation[37] = new Collation(37, "keybcs2_general_ci", 1, "keybcs2");
    collation[38] = new Collation(38, "macce_general_ci", 1, "macce");
    collation[39] = new Collation(39, "macroman_general_ci", 1, "macroman");
    collation[40] = new Collation(40, "cp852_general_ci", 1, "cp852");
    collation[41] = new Collation(41, "latin7_general_ci", 1, "latin7");
    collation[42] = new Collation(42, "latin7_general_cs", 0, "latin7");
    collation[43] = new Collation(43, "macce_bin", 0, "macce");
    collation[44] = new Collation(44, "cp1250_croatian_ci", 0, "cp1250");
    collation[45] = new Collation(45, "utf8mb4_general_ci", 0, "utf8mb4");
    collation[46] = new Collation(46, "utf8mb4_bin", 0, "utf8mb4");
    collation[47] = new Collation(47, "latin1_bin", 0, "latin1");
    collation[48] = new Collation(48, "latin1_general_ci", 0, "latin1");
    collation[49] = new Collation(49, "latin1_general_cs", 0, "latin1");
    collation[50] = new Collation(50, "cp1251_bin", 0, "cp1251");
    collation[51] = new Collation(51, "cp1251_general_ci", 1, "cp1251");
    collation[52] = new Collation(52, "cp1251_general_cs", 0, "cp1251");
    collation[53] = new Collation(53, "macroman_bin", 0, "macroman");
    collation[54] = new Collation(54, "utf16_general_ci", 1, "utf16");
    collation[55] = new Collation(55, "utf16_bin", 0, "utf16");
    collation[56] = new Collation(56, "utf16le_general_ci", 1, "utf16le");
    collation[57] = new Collation(57, "cp1256_general_ci", 1, "cp1256");
    collation[58] = new Collation(58, "cp1257_bin", 0, "cp1257");
    collation[59] = new Collation(59, "cp1257_general_ci", 1, "cp1257");
    collation[60] = new Collation(60, "utf32_general_ci", 1, "utf32");
    collation[61] = new Collation(61, "utf32_bin", 0, "utf32");
    collation[62] = new Collation(62, "utf16le_bin", 0, "utf16le");
    collation[63] = new Collation(63, "binary", 1, "binary");
    collation[64] = new Collation(64, "armscii8_bin", 0, "armscii8");
    collation[65] = new Collation(65, "ascii_bin", 0, "ascii");
    collation[66] = new Collation(66, "cp1250_bin", 0, "cp1250");
    collation[67] = new Collation(67, "cp1256_bin", 0, "cp1256");
    collation[68] = new Collation(68, "cp866_bin", 0, "cp866");
    collation[69] = new Collation(69, "dec8_bin", 0, "dec8");
    collation[70] = new Collation(70, "greek_bin", 0, "greek");
    collation[71] = new Collation(71, "hebrew_bin", 0, "hebrew");
    collation[72] = new Collation(72, "hp8_bin", 0, "hp8");
    collation[73] = new Collation(73, "keybcs2_bin", 0, "keybcs2");
    collation[74] = new Collation(74, "koi8r_bin", 0, "koi8r");
    collation[75] = new Collation(75, "koi8u_bin", 0, "koi8u");
    collation[76] = new Collation(76, new String[] { "utf8mb3_tolower_ci", "utf8_tolower_ci" }, 0, "utf8mb3");
    collation[77] = new Collation(77, "latin2_bin", 0, "latin2");
    collation[78] = new Collation(78, "latin5_bin", 0, "latin5");
    collation[79] = new Collation(79, "latin7_bin", 0, "latin7");
    collation[80] = new Collation(80, "cp850_bin", 0, "cp850");
    collation[81] = new Collation(81, "cp852_bin", 0, "cp852");
    collation[82] = new Collation(82, "swe7_bin", 0, "swe7");
    collation[83] = new Collation(83, new String[] { "utf8mb3_bin", "utf8_bin" }, 0, "utf8mb3");
    collation[84] = new Collation(84, "big5_bin", 0, "big5");
    collation[85] = new Collation(85, "euckr_bin", 0, "euckr");
    collation[86] = new Collation(86, "gb2312_bin", 0, "gb2312");
    collation[87] = new Collation(87, "gbk_bin", 0, "gbk");
    collation[88] = new Collation(88, "sjis_bin", 0, "sjis");
    collation[89] = new Collation(89, "tis620_bin", 0, "tis620");
    collation[90] = new Collation(90, "ucs2_bin", 0, "ucs2");
    collation[91] = new Collation(91, "ujis_bin", 0, "ujis");
    collation[92] = new Collation(92, "geostd8_general_ci", 0, "geostd8");
    collation[93] = new Collation(93, "geostd8_bin", 0, "geostd8");
    collation[94] = new Collation(94, "latin1_spanish_ci", 0, "latin1");
    collation[95] = new Collation(95, "cp932_japanese_ci", 1, "cp932");
    collation[96] = new Collation(96, "cp932_bin", 0, "cp932");
    collation[97] = new Collation(97, "eucjpms_japanese_ci", 1, "eucjpms");
    collation[98] = new Collation(98, "eucjpms_bin", 0, "eucjpms");
    collation[99] = new Collation(99, "cp1250_polish_ci", 0, "cp1250");
    collation[101] = new Collation(101, "utf16_unicode_ci", 0, "utf16");
    collation[102] = new Collation(102, "utf16_icelandic_ci", 0, "utf16");
    collation[103] = new Collation(103, "utf16_latvian_ci", 0, "utf16");
    collation[104] = new Collation(104, "utf16_romanian_ci", 0, "utf16");
    collation[105] = new Collation(105, "utf16_slovenian_ci", 0, "utf16");
    collation[106] = new Collation(106, "utf16_polish_ci", 0, "utf16");
    collation[107] = new Collation(107, "utf16_estonian_ci", 0, "utf16");
    collation[108] = new Collation(108, "utf16_spanish_ci", 0, "utf16");
    collation[109] = new Collation(109, "utf16_swedish_ci", 0, "utf16");
    collation[110] = new Collation(110, "utf16_turkish_ci", 0, "utf16");
    collation[111] = new Collation(111, "utf16_czech_ci", 0, "utf16");
    collation[112] = new Collation(112, "utf16_danish_ci", 0, "utf16");
    collation[113] = new Collation(113, "utf16_lithuanian_ci", 0, "utf16");
    collation[114] = new Collation(114, "utf16_slovak_ci", 0, "utf16");
    collation[115] = new Collation(115, "utf16_spanish2_ci", 0, "utf16");
    collation[116] = new Collation(116, "utf16_roman_ci", 0, "utf16");
    collation[117] = new Collation(117, "utf16_persian_ci", 0, "utf16");
    collation[118] = new Collation(118, "utf16_esperanto_ci", 0, "utf16");
    collation[119] = new Collation(119, "utf16_hungarian_ci", 0, "utf16");
    collation[120] = new Collation(120, "utf16_sinhala_ci", 0, "utf16");
    collation[121] = new Collation(121, "utf16_german2_ci", 0, "utf16");
    collation[122] = new Collation(122, "utf16_croatian_ci", 0, "utf16");
    collation[123] = new Collation(123, "utf16_unicode_520_ci", 0, "utf16");
    collation[124] = new Collation(124, "utf16_vietnamese_ci", 0, "utf16");
    collation[128] = new Collation(128, "ucs2_unicode_ci", 0, "ucs2");
    collation[129] = new Collation(129, "ucs2_icelandic_ci", 0, "ucs2");
    collation[130] = new Collation(130, "ucs2_latvian_ci", 0, "ucs2");
    collation[131] = new Collation(131, "ucs2_romanian_ci", 0, "ucs2");
    collation[132] = new Collation(132, "ucs2_slovenian_ci", 0, "ucs2");
    collation[133] = new Collation(133, "ucs2_polish_ci", 0, "ucs2");
    collation[134] = new Collation(134, "ucs2_estonian_ci", 0, "ucs2");
    collation[135] = new Collation(135, "ucs2_spanish_ci", 0, "ucs2");
    collation[136] = new Collation(136, "ucs2_swedish_ci", 0, "ucs2");
    collation[137] = new Collation(137, "ucs2_turkish_ci", 0, "ucs2");
    collation[138] = new Collation(138, "ucs2_czech_ci", 0, "ucs2");
    collation[139] = new Collation(139, "ucs2_danish_ci", 0, "ucs2");
    collation[140] = new Collation(140, "ucs2_lithuanian_ci", 0, "ucs2");
    collation[141] = new Collation(141, "ucs2_slovak_ci", 0, "ucs2");
    collation[142] = new Collation(142, "ucs2_spanish2_ci", 0, "ucs2");
    collation[143] = new Collation(143, "ucs2_roman_ci", 0, "ucs2");
    collation[144] = new Collation(144, "ucs2_persian_ci", 0, "ucs2");
    collation[145] = new Collation(145, "ucs2_esperanto_ci", 0, "ucs2");
    collation[146] = new Collation(146, "ucs2_hungarian_ci", 0, "ucs2");
    collation[147] = new Collation(147, "ucs2_sinhala_ci", 0, "ucs2");
    collation[148] = new Collation(148, "ucs2_german2_ci", 0, "ucs2");
    collation[149] = new Collation(149, "ucs2_croatian_ci", 0, "ucs2");
    collation[150] = new Collation(150, "ucs2_unicode_520_ci", 0, "ucs2");
    collation[151] = new Collation(151, "ucs2_vietnamese_ci", 0, "ucs2");
    collation[159] = new Collation(159, "ucs2_general_mysql500_ci", 0, "ucs2");
    collation[160] = new Collation(160, "utf32_unicode_ci", 0, "utf32");
    collation[161] = new Collation(161, "utf32_icelandic_ci", 0, "utf32");
    collation[162] = new Collation(162, "utf32_latvian_ci", 0, "utf32");
    collation[163] = new Collation(163, "utf32_romanian_ci", 0, "utf32");
    collation[164] = new Collation(164, "utf32_slovenian_ci", 0, "utf32");
    collation[165] = new Collation(165, "utf32_polish_ci", 0, "utf32");
    collation[166] = new Collation(166, "utf32_estonian_ci", 0, "utf32");
    collation[167] = new Collation(167, "utf32_spanish_ci", 0, "utf32");
    collation[168] = new Collation(168, "utf32_swedish_ci", 0, "utf32");
    collation[169] = new Collation(169, "utf32_turkish_ci", 0, "utf32");
    collation[170] = new Collation(170, "utf32_czech_ci", 0, "utf32");
    collation[171] = new Collation(171, "utf32_danish_ci", 0, "utf32");
    collation[172] = new Collation(172, "utf32_lithuanian_ci", 0, "utf32");
    collation[173] = new Collation(173, "utf32_slovak_ci", 0, "utf32");
    collation[174] = new Collation(174, "utf32_spanish2_ci", 0, "utf32");
    collation[175] = new Collation(175, "utf32_roman_ci", 0, "utf32");
    collation[176] = new Collation(176, "utf32_persian_ci", 0, "utf32");
    collation[177] = new Collation(177, "utf32_esperanto_ci", 0, "utf32");
    collation[178] = new Collation(178, "utf32_hungarian_ci", 0, "utf32");
    collation[179] = new Collation(179, "utf32_sinhala_ci", 0, "utf32");
    collation[180] = new Collation(180, "utf32_german2_ci", 0, "utf32");
    collation[181] = new Collation(181, "utf32_croatian_ci", 0, "utf32");
    collation[182] = new Collation(182, "utf32_unicode_520_ci", 0, "utf32");
    collation[183] = new Collation(183, "utf32_vietnamese_ci", 0, "utf32");
    collation[192] = new Collation(192, new String[] { "utf8mb3_unicode_ci", "utf8_unicode_ci" }, 0, "utf8mb3");
    collation[193] = new Collation(193, new String[] { "utf8mb3_icelandic_ci", "utf8_icelandic_ci" }, 0, "utf8mb3");
    collation[194] = new Collation(194, new String[] { "utf8mb3_latvian_ci", "utf8_latvian_ci" }, 0, "utf8mb3");
    collation[195] = new Collation(195, new String[] { "utf8mb3_romanian_ci", "utf8_romanian_ci" }, 0, "utf8mb3");
    collation[196] = new Collation(196, new String[] { "utf8mb3_slovenian_ci", "utf8_slovenian_ci" }, 0, "utf8mb3");
    collation[197] = new Collation(197, new String[] { "utf8mb3_polish_ci", "utf8_polish_ci" }, 0, "utf8mb3");
    collation[198] = new Collation(198, new String[] { "utf8mb3_estonian_ci", "utf8_estonian_ci" }, 0, "utf8mb3");
    collation[199] = new Collation(199, new String[] { "utf8mb3_spanish_ci", "utf8_spanish_ci" }, 0, "utf8mb3");
    collation[200] = new Collation(200, new String[] { "utf8mb3_swedish_ci", "utf8_swedish_ci" }, 0, "utf8mb3");
    collation[201] = new Collation(201, new String[] { "utf8mb3_turkish_ci", "utf8_turkish_ci" }, 0, "utf8mb3");
    collation[202] = new Collation(202, new String[] { "utf8mb3_czech_ci", "utf8_czech_ci" }, 0, "utf8mb3");
    collation[203] = new Collation(203, new String[] { "utf8mb3_danish_ci", "utf8_danish_ci" }, 0, "utf8mb3");
    collation[204] = new Collation(204, new String[] { "utf8mb3_lithuanian_ci", "utf8_lithuanian_ci" }, 0, "utf8mb3");
    collation[205] = new Collation(205, new String[] { "utf8mb3_slovak_ci", "utf8_slovak_ci" }, 0, "utf8mb3");
    collation[206] = new Collation(206, new String[] { "utf8mb3_spanish2_ci", "utf8_spanish2_ci" }, 0, "utf8mb3");
    collation[207] = new Collation(207, new String[] { "utf8mb3_roman_ci", "utf8_roman_ci" }, 0, "utf8mb3");
    collation[208] = new Collation(208, new String[] { "utf8mb3_persian_ci", "utf8_persian_ci" }, 0, "utf8mb3");
    collation[209] = new Collation(209, new String[] { "utf8mb3_esperanto_ci", "utf8_esperanto_ci" }, 0, "utf8mb3");
    collation[210] = new Collation(210, new String[] { "utf8mb3_hungarian_ci", "utf8_hungarian_ci" }, 0, "utf8mb3");
    collation[211] = new Collation(211, new String[] { "utf8mb3_sinhala_ci", "utf8_sinhala_ci" }, 0, "utf8mb3");
    collation[212] = new Collation(212, new String[] { "utf8mb3_german2_ci", "utf8_german2_ci" }, 0, "utf8mb3");
    collation[213] = new Collation(213, new String[] { "utf8mb3_croatian_ci", "utf8_croatian_ci" }, 0, "utf8mb3");
    collation[214] = new Collation(214, new String[] { "utf8mb3_unicode_520_ci", "utf8_unicode_520_ci" }, 0, "utf8mb3");
    collation[215] = new Collation(215, new String[] { "utf8mb3_vietnamese_ci", "utf8_vietnamese_ci" }, 0, "utf8mb3");
    collation[223] = new Collation(223, new String[] { "utf8mb3_general_mysql500_ci", "utf8_general_mysql500_ci" }, 0, "utf8mb3");
    collation[224] = new Collation(224, "utf8mb4_unicode_ci", 0, "utf8mb4");
    collation[225] = new Collation(225, "utf8mb4_icelandic_ci", 0, "utf8mb4");
    collation[226] = new Collation(226, "utf8mb4_latvian_ci", 0, "utf8mb4");
    collation[227] = new Collation(227, "utf8mb4_romanian_ci", 0, "utf8mb4");
    collation[228] = new Collation(228, "utf8mb4_slovenian_ci", 0, "utf8mb4");
    collation[229] = new Collation(229, "utf8mb4_polish_ci", 0, "utf8mb4");
    collation[230] = new Collation(230, "utf8mb4_estonian_ci", 0, "utf8mb4");
    collation[231] = new Collation(231, "utf8mb4_spanish_ci", 0, "utf8mb4");
    collation[232] = new Collation(232, "utf8mb4_swedish_ci", 0, "utf8mb4");
    collation[233] = new Collation(233, "utf8mb4_turkish_ci", 0, "utf8mb4");
    collation[234] = new Collation(234, "utf8mb4_czech_ci", 0, "utf8mb4");
    collation[235] = new Collation(235, "utf8mb4_danish_ci", 0, "utf8mb4");
    collation[236] = new Collation(236, "utf8mb4_lithuanian_ci", 0, "utf8mb4");
    collation[237] = new Collation(237, "utf8mb4_slovak_ci", 0, "utf8mb4");
    collation[238] = new Collation(238, "utf8mb4_spanish2_ci", 0, "utf8mb4");
    collation[239] = new Collation(239, "utf8mb4_roman_ci", 0, "utf8mb4");
    collation[240] = new Collation(240, "utf8mb4_persian_ci", 0, "utf8mb4");
    collation[241] = new Collation(241, "utf8mb4_esperanto_ci", 0, "utf8mb4");
    collation[242] = new Collation(242, "utf8mb4_hungarian_ci", 0, "utf8mb4");
    collation[243] = new Collation(243, "utf8mb4_sinhala_ci", 0, "utf8mb4");
    collation[244] = new Collation(244, "utf8mb4_german2_ci", 0, "utf8mb4");
    collation[245] = new Collation(245, "utf8mb4_croatian_ci", 0, "utf8mb4");
    collation[246] = new Collation(246, "utf8mb4_unicode_520_ci", 0, "utf8mb4");
    collation[247] = new Collation(247, "utf8mb4_vietnamese_ci", 0, "utf8mb4");
    collation[248] = new Collation(248, "gb18030_chinese_ci", 1, "gb18030");
    collation[249] = new Collation(249, "gb18030_bin", 0, "gb18030");
    collation[250] = new Collation(250, "gb18030_unicode_520_ci", 0, "gb18030");
    collation[255] = new Collation(255, "utf8mb4_0900_ai_ci", 1, "utf8mb4");
    collation[256] = new Collation(256, "utf8mb4_de_pb_0900_ai_ci", 0, "utf8mb4");
    collation[257] = new Collation(257, "utf8mb4_is_0900_ai_ci", 0, "utf8mb4");
    collation[258] = new Collation(258, "utf8mb4_lv_0900_ai_ci", 0, "utf8mb4");
    collation[259] = new Collation(259, "utf8mb4_ro_0900_ai_ci", 0, "utf8mb4");
    collation[260] = new Collation(260, "utf8mb4_sl_0900_ai_ci", 0, "utf8mb4");
    collation[261] = new Collation(261, "utf8mb4_pl_0900_ai_ci", 0, "utf8mb4");
    collation[262] = new Collation(262, "utf8mb4_et_0900_ai_ci", 0, "utf8mb4");
    collation[263] = new Collation(263, "utf8mb4_es_0900_ai_ci", 0, "utf8mb4");
    collation[264] = new Collation(264, "utf8mb4_sv_0900_ai_ci", 0, "utf8mb4");
    collation[265] = new Collation(265, "utf8mb4_tr_0900_ai_ci", 0, "utf8mb4");
    collation[266] = new Collation(266, "utf8mb4_cs_0900_ai_ci", 0, "utf8mb4");
    collation[267] = new Collation(267, "utf8mb4_da_0900_ai_ci", 0, "utf8mb4");
    collation[268] = new Collation(268, "utf8mb4_lt_0900_ai_ci", 0, "utf8mb4");
    collation[269] = new Collation(269, "utf8mb4_sk_0900_ai_ci", 0, "utf8mb4");
    collation[270] = new Collation(270, "utf8mb4_es_trad_0900_ai_ci", 0, "utf8mb4");
    collation[271] = new Collation(271, "utf8mb4_la_0900_ai_ci", 0, "utf8mb4");
    collation[273] = new Collation(273, "utf8mb4_eo_0900_ai_ci", 0, "utf8mb4");
    collation[274] = new Collation(274, "utf8mb4_hu_0900_ai_ci", 0, "utf8mb4");
    collation[275] = new Collation(275, "utf8mb4_hr_0900_ai_ci", 0, "utf8mb4");
    collation[277] = new Collation(277, "utf8mb4_vi_0900_ai_ci", 0, "utf8mb4");
    collation[278] = new Collation(278, "utf8mb4_0900_as_cs", 0, "utf8mb4");
    collation[279] = new Collation(279, "utf8mb4_de_pb_0900_as_cs", 0, "utf8mb4");
    collation[280] = new Collation(280, "utf8mb4_is_0900_as_cs", 0, "utf8mb4");
    collation[281] = new Collation(281, "utf8mb4_lv_0900_as_cs", 0, "utf8mb4");
    collation[282] = new Collation(282, "utf8mb4_ro_0900_as_cs", 0, "utf8mb4");
    collation[283] = new Collation(283, "utf8mb4_sl_0900_as_cs", 0, "utf8mb4");
    collation[284] = new Collation(284, "utf8mb4_pl_0900_as_cs", 0, "utf8mb4");
    collation[285] = new Collation(285, "utf8mb4_et_0900_as_cs", 0, "utf8mb4");
    collation[286] = new Collation(286, "utf8mb4_es_0900_as_cs", 0, "utf8mb4");
    collation[287] = new Collation(287, "utf8mb4_sv_0900_as_cs", 0, "utf8mb4");
    collation[288] = new Collation(288, "utf8mb4_tr_0900_as_cs", 0, "utf8mb4");
    collation[289] = new Collation(289, "utf8mb4_cs_0900_as_cs", 0, "utf8mb4");
    collation[290] = new Collation(290, "utf8mb4_da_0900_as_cs", 0, "utf8mb4");
    collation[291] = new Collation(291, "utf8mb4_lt_0900_as_cs", 0, "utf8mb4");
    collation[292] = new Collation(292, "utf8mb4_sk_0900_as_cs", 0, "utf8mb4");
    collation[293] = new Collation(293, "utf8mb4_es_trad_0900_as_cs", 0, "utf8mb4");
    collation[294] = new Collation(294, "utf8mb4_la_0900_as_cs", 0, "utf8mb4");
    collation[296] = new Collation(296, "utf8mb4_eo_0900_as_cs", 0, "utf8mb4");
    collation[297] = new Collation(297, "utf8mb4_hu_0900_as_cs", 0, "utf8mb4");
    collation[298] = new Collation(298, "utf8mb4_hr_0900_as_cs", 0, "utf8mb4");
    collation[300] = new Collation(300, "utf8mb4_vi_0900_as_cs", 0, "utf8mb4");
    collation[303] = new Collation(303, "utf8mb4_ja_0900_as_cs", 0, "utf8mb4");
    collation[304] = new Collation(304, "utf8mb4_ja_0900_as_cs_ks", 0, "utf8mb4");
    collation[305] = new Collation(305, "utf8mb4_0900_as_ci", 0, "utf8mb4");
    collation[306] = new Collation(306, "utf8mb4_ru_0900_ai_ci", 0, "utf8mb4");
    collation[307] = new Collation(307, "utf8mb4_ru_0900_as_cs", 0, "utf8mb4");
    collation[308] = new Collation(308, "utf8mb4_zh_0900_as_cs", 0, "utf8mb4");
    collation[309] = new Collation(309, "utf8mb4_0900_bin", 0, "utf8mb4");
    collation[310] = new Collation(310, "utf8mb4_nb_0900_ai_ci", 0, "utf8mb4");
    collation[311] = new Collation(311, "utf8mb4_nb_0900_as_cs", 0, "utf8mb4");
    collation[312] = new Collation(312, "utf8mb4_nn_0900_ai_ci", 0, "utf8mb4");
    collation[313] = new Collation(313, "utf8mb4_nn_0900_as_cs", 0, "utf8mb4");
    collation[314] = new Collation(314, "utf8mb4_sr_latn_0900_ai_ci", 0, "utf8mb4");
    collation[315] = new Collation(315, "utf8mb4_sr_latn_0900_as_cs", 0, "utf8mb4");
    collation[316] = new Collation(316, "utf8mb4_bs_0900_ai_ci", 0, "utf8mb4");
    collation[317] = new Collation(317, "utf8mb4_bs_0900_as_cs", 0, "utf8mb4");
    collation[318] = new Collation(318, "utf8mb4_bg_0900_ai_ci", 0, "utf8mb4");
    collation[319] = new Collation(319, "utf8mb4_bg_0900_as_cs", 0, "utf8mb4");
    collation[320] = new Collation(320, "utf8mb4_gl_0900_ai_ci", 0, "utf8mb4");
    collation[321] = new Collation(321, "utf8mb4_gl_0900_as_cs", 0, "utf8mb4");
    collation[322] = new Collation(322, "utf8mb4_mn_cyrl_0900_ai_ci", 0, "utf8mb4");
    collation[323] = new Collation(323, "utf8mb4_mn_cyrl_0900_as_cs", 0, "utf8mb4");
    COLLATION_INDEX_TO_COLLATION_NAME = new String[1024];
    Map<Integer, MysqlCharset> collationIndexToCharset = new TreeMap<>();
    Map<String, Integer> charsetNameToCollationIndexMap = new TreeMap<>();
    Map<String, Integer> charsetNameToCollationPriorityMap = new TreeMap<>();
    Map<String, Integer> collationNameToCollationIndexMap = new TreeMap<>();
    Set<Integer> impermissibleIndexes = new HashSet<>();
    for (int j = 1; j < 1024; j++) {
      Collation coll = collation[j];
      if (coll != null) {
        COLLATION_INDEX_TO_COLLATION_NAME[j] = coll.collationNames[0];
        collationIndexToCharset.put(Integer.valueOf(j), coll.mysqlCharset);
        for (String collationName : coll.collationNames)
          collationNameToCollationIndexMap.put(collationName, Integer.valueOf(j)); 
        String charsetName = coll.mysqlCharset.charsetName;
        if (!charsetNameToCollationIndexMap.containsKey(charsetName) || ((Integer)charsetNameToCollationPriorityMap.get(charsetName)).intValue() < coll.priority) {
          charsetNameToCollationIndexMap.put(charsetName, Integer.valueOf(j));
          charsetNameToCollationPriorityMap.put(charsetName, Integer.valueOf(coll.priority));
        } 
        if (charsetName.equals("ucs2") || charsetName.equals("utf16") || charsetName
          .equals("utf16le") || charsetName.equals("utf32"))
          impermissibleIndexes.add(Integer.valueOf(j)); 
      } 
    } 
    for (MysqlCharset cs : charset) {
      Integer idx;
      if (!cs.aliases.isEmpty() && (idx = charsetNameToCollationIndexMap.get(cs.charsetName)) != null)
        for (String alias : cs.aliases)
          charsetNameToCollationIndexMap.put(alias, idx);  
    } 
    COLLATION_INDEX_TO_CHARSET = Collections.unmodifiableMap(collationIndexToCharset);
    CHARSET_NAME_TO_COLLATION_INDEX = Collections.unmodifiableMap(charsetNameToCollationIndexMap);
    COLLATION_NAME_TO_COLLATION_INDEX = Collections.unmodifiableMap(collationNameToCollationIndexMap);
    IMPERMISSIBLE_INDEXES = Collections.unmodifiableSet(impermissibleIndexes);
    collation = null;
  }
  
  protected static String getStaticMysqlCharsetForJavaEncoding(String javaEncoding, ServerVersion version) {
    List<MysqlCharset> mysqlCharsets = JAVA_ENCODING_UC_TO_MYSQL_CHARSET.get(javaEncoding.toUpperCase(Locale.ENGLISH));
    if (mysqlCharsets != null) {
      if (version == null)
        return ((MysqlCharset)mysqlCharsets.get(0)).charsetName; 
      MysqlCharset currentChoice = null;
      for (MysqlCharset charset : mysqlCharsets) {
        if (charset.isOkayForVersion(version) && (currentChoice == null || currentChoice.minimumVersion.compareTo(charset.minimumVersion) < 0 || (currentChoice.priority < charset.priority && currentChoice.minimumVersion
          .compareTo(charset.minimumVersion) == 0)))
          currentChoice = charset; 
      } 
      if (currentChoice != null)
        return currentChoice.charsetName; 
    } 
    return null;
  }
  
  protected static int getStaticCollationIndexForJavaEncoding(String javaEncoding, ServerVersion version) {
    String charsetName = getStaticMysqlCharsetForJavaEncoding(javaEncoding, version);
    return getStaticCollationIndexForMysqlCharsetName(charsetName);
  }
  
  protected static int getStaticCollationIndexForMysqlCharsetName(String charsetName) {
    if (charsetName != null) {
      Integer ci = CHARSET_NAME_TO_COLLATION_INDEX.get(charsetName);
      if (ci != null)
        return ci.intValue(); 
    } 
    return 0;
  }
  
  public static String getStaticMysqlCharsetNameForCollationIndex(Integer collationIndex) {
    MysqlCharset charset = null;
    if (collationIndex != null)
      charset = COLLATION_INDEX_TO_CHARSET.get(collationIndex); 
    return (charset != null) ? charset.charsetName : null;
  }
  
  public static String getStaticCollationNameForCollationIndex(Integer collationIndex) {
    if (collationIndex != null && collationIndex.intValue() > 0 && collationIndex.intValue() < 1024)
      return COLLATION_INDEX_TO_COLLATION_NAME[collationIndex.intValue()]; 
    return null;
  }
  
  protected static Integer getStaticCollationIndexForCollationName(String collationName) {
    return COLLATION_NAME_TO_COLLATION_INDEX.get(collationName);
  }
  
  protected static String getStaticJavaEncodingForMysqlCharset(String mysqlCharsetName, String fallbackJavaEncoding) {
    MysqlCharset cs = getStaticMysqlCharsetByName(mysqlCharsetName);
    return (cs != null) ? cs.getMatchingJavaEncoding(fallbackJavaEncoding) : fallbackJavaEncoding;
  }
  
  protected static MysqlCharset getStaticMysqlCharsetByName(String mysqlCharsetName) {
    return CHARSET_NAME_TO_CHARSET.get(mysqlCharsetName);
  }
  
  protected static List<String> getStaticMysqlCharsetAliasesByName(String mysqlCharsetName) {
    MysqlCharset cs;
    if ((cs = CHARSET_NAME_TO_CHARSET.get(mysqlCharsetName)) != null)
      return cs.aliases; 
    return null;
  }
  
  protected static String getStaticJavaEncodingForMysqlCharset(String mysqlCharsetName) {
    return getStaticJavaEncodingForMysqlCharset(mysqlCharsetName, null);
  }
  
  protected static String getStaticJavaEncodingForCollationIndex(Integer collationIndex, String fallbackJavaEncoding) {
    MysqlCharset charset = null;
    if (collationIndex != null)
      charset = COLLATION_INDEX_TO_CHARSET.get(collationIndex); 
    return (charset != null) ? charset.getMatchingJavaEncoding(fallbackJavaEncoding) : fallbackJavaEncoding;
  }
  
  public static String getStaticJavaEncodingForCollationIndex(Integer collationIndex) {
    return getStaticJavaEncodingForCollationIndex(collationIndex, null);
  }
  
  protected static boolean isStaticMultibyteCharset(String javaEncodingName) {
    return MULTIBYTE_ENCODINGS.contains(javaEncodingName.toUpperCase(Locale.ENGLISH));
  }
  
  protected static int getStaticMblen(String charsetName) {
    if (charsetName != null) {
      MysqlCharset cs = getStaticMysqlCharsetByName(charsetName);
      if (cs != null)
        return cs.mblen; 
    } 
    return 0;
  }
  
  protected static boolean isStaticImpermissibleCollation(int collationIndex) {
    return IMPERMISSIBLE_INDEXES.contains(Integer.valueOf(collationIndex));
  }
}
