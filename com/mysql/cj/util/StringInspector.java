package com.mysql.cj.util;

import com.mysql.cj.Messages;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;

public class StringInspector {
  private static final int NON_COMMENTS_MYSQL_VERSION_REF_LENGTH = 5;
  
  private String source = null;
  
  private String openingMarkers = null;
  
  private String closingMarkers = null;
  
  private String overridingMarkers = null;
  
  private Set<SearchMode> defaultSearchMode = null;
  
  private int srcLen = 0;
  
  private int pos = 0;
  
  private int stopAt = 0;
  
  private boolean escaped = false;
  
  private boolean inMysqlBlock = false;
  
  private int markedPos = this.pos;
  
  private int markedStopAt = this.stopAt;
  
  private boolean markedEscape = this.escaped;
  
  private boolean markedInMysqlBlock = this.inMysqlBlock;
  
  public StringInspector(String source, String openingMarkers, String closingMarkers, String overridingMarkers, Set<SearchMode> searchMode) {
    this(source, 0, openingMarkers, closingMarkers, overridingMarkers, searchMode);
  }
  
  public StringInspector(String source, int startingPosition, String openingMarkers, String closingMarkers, String overridingMarkers, Set<SearchMode> searchMode) {
    if (source == null)
      throw new IllegalArgumentException(Messages.getString("StringInspector.1")); 
    this.source = source;
    this.openingMarkers = openingMarkers;
    this.closingMarkers = closingMarkers;
    this.overridingMarkers = overridingMarkers;
    this.defaultSearchMode = searchMode;
    if (this.defaultSearchMode.contains(SearchMode.SKIP_BETWEEN_MARKERS)) {
      if (this.openingMarkers == null || this.closingMarkers == null || this.openingMarkers.length() != this.closingMarkers.length())
        throw new IllegalArgumentException(Messages.getString("StringInspector.2", new String[] { this.openingMarkers, this.closingMarkers })); 
      if (this.overridingMarkers == null)
        throw new IllegalArgumentException(Messages.getString("StringInspector.3", new String[] { this.overridingMarkers, this.openingMarkers })); 
      for (char c : this.overridingMarkers.toCharArray()) {
        if (this.openingMarkers.indexOf(c) == -1)
          throw new IllegalArgumentException(Messages.getString("StringInspector.3", new String[] { this.overridingMarkers, this.openingMarkers })); 
      } 
    } 
    this.srcLen = source.length();
    this.pos = 0;
    this.stopAt = this.srcLen;
    setStartPosition(startingPosition);
  }
  
  public int setStartPosition(int pos) {
    if (pos < 0)
      throw new IllegalArgumentException(Messages.getString("StringInspector.4")); 
    if (pos > this.stopAt)
      throw new IllegalArgumentException(Messages.getString("StringInspector.5")); 
    int prevPos = this.pos;
    this.pos = pos;
    resetEscaped();
    this.inMysqlBlock = false;
    return prevPos;
  }
  
  public int setStopPosition(int pos) {
    if (pos < 0)
      throw new IllegalArgumentException(Messages.getString("StringInspector.6")); 
    if (pos > this.srcLen)
      throw new IllegalArgumentException(Messages.getString("StringInspector.7")); 
    int prevPos = this.stopAt;
    this.stopAt = pos;
    return prevPos;
  }
  
  public void mark() {
    this.markedPos = this.pos;
    this.markedStopAt = this.stopAt;
    this.markedEscape = this.escaped;
    this.markedInMysqlBlock = this.inMysqlBlock;
  }
  
  public void reset() {
    this.pos = this.markedPos;
    this.stopAt = this.markedStopAt;
    this.escaped = this.markedEscape;
    this.inMysqlBlock = this.markedInMysqlBlock;
  }
  
  public void restart() {
    this.pos = 0;
    this.stopAt = this.srcLen;
    this.escaped = false;
    this.inMysqlBlock = false;
  }
  
  public char getChar() {
    if (this.pos >= this.stopAt)
      return Character.MIN_VALUE; 
    return this.source.charAt(this.pos);
  }
  
  public int getPosition() {
    return this.pos;
  }
  
  public int incrementPosition() {
    return incrementPosition(this.defaultSearchMode);
  }
  
  public int incrementPosition(Set<SearchMode> searchMode) {
    if (this.pos >= this.stopAt)
      return -1; 
    if (searchMode.contains(SearchMode.ALLOW_BACKSLASH_ESCAPE) && getChar() == '\\') {
      this.escaped = !this.escaped;
    } else if (this.escaped) {
      this.escaped = false;
    } 
    return ++this.pos;
  }
  
  public int incrementPosition(int by) {
    return incrementPosition(by, this.defaultSearchMode);
  }
  
  public int incrementPosition(int by, Set<SearchMode> searchMode) {
    for (int i = 0; i < by; i++) {
      if (incrementPosition(searchMode) == -1)
        return -1; 
    } 
    return this.pos;
  }
  
  private void resetEscaped() {
    this.escaped = false;
    if (this.defaultSearchMode.contains(SearchMode.ALLOW_BACKSLASH_ESCAPE))
      for (int i = this.pos - 1; i >= 0 && 
        this.source.charAt(i) == '\\'; i--)
        this.escaped = !this.escaped;  
  }
  
  public int indexOfNextChar() {
    return indexOfNextChar(this.defaultSearchMode);
  }
  
  private int indexOfNextChar(Set<SearchMode> searchMode) {
    // Byte code:
    //   0: aload_0
    //   1: getfield source : Ljava/lang/String;
    //   4: ifnonnull -> 9
    //   7: iconst_m1
    //   8: ireturn
    //   9: aload_0
    //   10: getfield pos : I
    //   13: aload_0
    //   14: getfield stopAt : I
    //   17: if_icmplt -> 22
    //   20: iconst_m1
    //   21: ireturn
    //   22: iconst_0
    //   23: istore_2
    //   24: aload_0
    //   25: getfield source : Ljava/lang/String;
    //   28: aload_0
    //   29: getfield pos : I
    //   32: invokevirtual charAt : (I)C
    //   35: istore_3
    //   36: aload_0
    //   37: getfield pos : I
    //   40: iconst_1
    //   41: iadd
    //   42: aload_0
    //   43: getfield srcLen : I
    //   46: if_icmpge -> 65
    //   49: aload_0
    //   50: getfield source : Ljava/lang/String;
    //   53: aload_0
    //   54: getfield pos : I
    //   57: iconst_1
    //   58: iadd
    //   59: invokevirtual charAt : (I)C
    //   62: goto -> 66
    //   65: iconst_0
    //   66: istore #4
    //   68: aload_0
    //   69: getfield pos : I
    //   72: aload_0
    //   73: getfield stopAt : I
    //   76: if_icmpge -> 1426
    //   79: iload_3
    //   80: istore_2
    //   81: iload #4
    //   83: istore_3
    //   84: aload_0
    //   85: getfield pos : I
    //   88: iconst_2
    //   89: iadd
    //   90: aload_0
    //   91: getfield srcLen : I
    //   94: if_icmpge -> 113
    //   97: aload_0
    //   98: getfield source : Ljava/lang/String;
    //   101: aload_0
    //   102: getfield pos : I
    //   105: iconst_2
    //   106: iadd
    //   107: invokevirtual charAt : (I)C
    //   110: goto -> 114
    //   113: iconst_0
    //   114: istore #4
    //   116: iconst_0
    //   117: istore #5
    //   119: aload_1
    //   120: getstatic com/mysql/cj/util/SearchMode.ALLOW_BACKSLASH_ESCAPE : Lcom/mysql/cj/util/SearchMode;
    //   123: invokeinterface contains : (Ljava/lang/Object;)Z
    //   128: ifeq -> 138
    //   131: aload_0
    //   132: getfield escaped : Z
    //   135: ifne -> 142
    //   138: iconst_1
    //   139: goto -> 143
    //   142: iconst_0
    //   143: istore #6
    //   145: iload #6
    //   147: ifeq -> 270
    //   150: aload_1
    //   151: getstatic com/mysql/cj/util/SearchMode.SKIP_BETWEEN_MARKERS : Lcom/mysql/cj/util/SearchMode;
    //   154: invokeinterface contains : (Ljava/lang/Object;)Z
    //   159: ifeq -> 270
    //   162: aload_0
    //   163: getfield openingMarkers : Ljava/lang/String;
    //   166: iload_2
    //   167: invokevirtual indexOf : (I)I
    //   170: iconst_m1
    //   171: if_icmpeq -> 270
    //   174: aload_0
    //   175: aload_1
    //   176: invokespecial indexOfClosingMarker : (Ljava/util/Set;)I
    //   179: pop
    //   180: aload_0
    //   181: getfield pos : I
    //   184: aload_0
    //   185: getfield stopAt : I
    //   188: if_icmplt -> 204
    //   191: aload_0
    //   192: dup
    //   193: getfield pos : I
    //   196: iconst_1
    //   197: isub
    //   198: putfield pos : I
    //   201: goto -> 1408
    //   204: aload_0
    //   205: getfield pos : I
    //   208: iconst_1
    //   209: iadd
    //   210: aload_0
    //   211: getfield srcLen : I
    //   214: if_icmpge -> 233
    //   217: aload_0
    //   218: getfield source : Ljava/lang/String;
    //   221: aload_0
    //   222: getfield pos : I
    //   225: iconst_1
    //   226: iadd
    //   227: invokevirtual charAt : (I)C
    //   230: goto -> 234
    //   233: iconst_0
    //   234: istore_3
    //   235: aload_0
    //   236: getfield pos : I
    //   239: iconst_2
    //   240: iadd
    //   241: aload_0
    //   242: getfield srcLen : I
    //   245: if_icmpge -> 264
    //   248: aload_0
    //   249: getfield source : Ljava/lang/String;
    //   252: aload_0
    //   253: getfield pos : I
    //   256: iconst_2
    //   257: iadd
    //   258: invokevirtual charAt : (I)C
    //   261: goto -> 265
    //   264: iconst_0
    //   265: istore #4
    //   267: goto -> 1408
    //   270: iload #6
    //   272: ifeq -> 495
    //   275: aload_1
    //   276: getstatic com/mysql/cj/util/SearchMode.SKIP_BLOCK_COMMENTS : Lcom/mysql/cj/util/SearchMode;
    //   279: invokeinterface contains : (Ljava/lang/Object;)Z
    //   284: ifeq -> 495
    //   287: iload_2
    //   288: bipush #47
    //   290: if_icmpne -> 495
    //   293: iload_3
    //   294: bipush #42
    //   296: if_icmpne -> 495
    //   299: iload #4
    //   301: bipush #33
    //   303: if_icmpeq -> 495
    //   306: iload #4
    //   308: bipush #43
    //   310: if_icmpeq -> 495
    //   313: aload_0
    //   314: dup
    //   315: getfield pos : I
    //   318: iconst_1
    //   319: iadd
    //   320: putfield pos : I
    //   323: aload_0
    //   324: dup
    //   325: getfield pos : I
    //   328: iconst_1
    //   329: iadd
    //   330: dup_x1
    //   331: putfield pos : I
    //   334: aload_0
    //   335: getfield stopAt : I
    //   338: if_icmpge -> 395
    //   341: aload_0
    //   342: getfield source : Ljava/lang/String;
    //   345: aload_0
    //   346: getfield pos : I
    //   349: invokevirtual charAt : (I)C
    //   352: bipush #42
    //   354: if_icmpne -> 323
    //   357: aload_0
    //   358: getfield pos : I
    //   361: iconst_1
    //   362: iadd
    //   363: aload_0
    //   364: getfield srcLen : I
    //   367: if_icmpge -> 386
    //   370: aload_0
    //   371: getfield source : Ljava/lang/String;
    //   374: aload_0
    //   375: getfield pos : I
    //   378: iconst_1
    //   379: iadd
    //   380: invokevirtual charAt : (I)C
    //   383: goto -> 387
    //   386: iconst_0
    //   387: bipush #47
    //   389: if_icmpeq -> 395
    //   392: goto -> 323
    //   395: aload_0
    //   396: getfield pos : I
    //   399: aload_0
    //   400: getfield stopAt : I
    //   403: if_icmplt -> 419
    //   406: aload_0
    //   407: dup
    //   408: getfield pos : I
    //   411: iconst_1
    //   412: isub
    //   413: putfield pos : I
    //   416: goto -> 429
    //   419: aload_0
    //   420: dup
    //   421: getfield pos : I
    //   424: iconst_1
    //   425: iadd
    //   426: putfield pos : I
    //   429: aload_0
    //   430: getfield pos : I
    //   433: iconst_1
    //   434: iadd
    //   435: aload_0
    //   436: getfield srcLen : I
    //   439: if_icmpge -> 458
    //   442: aload_0
    //   443: getfield source : Ljava/lang/String;
    //   446: aload_0
    //   447: getfield pos : I
    //   450: iconst_1
    //   451: iadd
    //   452: invokevirtual charAt : (I)C
    //   455: goto -> 459
    //   458: iconst_0
    //   459: istore_3
    //   460: aload_0
    //   461: getfield pos : I
    //   464: iconst_2
    //   465: iadd
    //   466: aload_0
    //   467: getfield srcLen : I
    //   470: if_icmpge -> 489
    //   473: aload_0
    //   474: getfield source : Ljava/lang/String;
    //   477: aload_0
    //   478: getfield pos : I
    //   481: iconst_2
    //   482: iadd
    //   483: invokevirtual charAt : (I)C
    //   486: goto -> 490
    //   489: iconst_0
    //   490: istore #4
    //   492: goto -> 1408
    //   495: iload #6
    //   497: ifeq -> 840
    //   500: aload_1
    //   501: getstatic com/mysql/cj/util/SearchMode.SKIP_LINE_COMMENTS : Lcom/mysql/cj/util/SearchMode;
    //   504: invokeinterface contains : (Ljava/lang/Object;)Z
    //   509: ifeq -> 840
    //   512: iload_2
    //   513: bipush #45
    //   515: if_icmpne -> 555
    //   518: iload_3
    //   519: bipush #45
    //   521: if_icmpne -> 555
    //   524: iload #4
    //   526: invokestatic isWhitespace : (C)Z
    //   529: ifne -> 561
    //   532: iload #4
    //   534: bipush #59
    //   536: if_icmpne -> 543
    //   539: iconst_1
    //   540: goto -> 544
    //   543: iconst_0
    //   544: dup
    //   545: istore #5
    //   547: ifne -> 561
    //   550: iload #4
    //   552: ifeq -> 561
    //   555: iload_2
    //   556: bipush #35
    //   558: if_icmpne -> 840
    //   561: iload #5
    //   563: ifeq -> 652
    //   566: aload_0
    //   567: dup
    //   568: getfield pos : I
    //   571: iconst_1
    //   572: iadd
    //   573: putfield pos : I
    //   576: aload_0
    //   577: dup
    //   578: getfield pos : I
    //   581: iconst_1
    //   582: iadd
    //   583: putfield pos : I
    //   586: aload_0
    //   587: getfield pos : I
    //   590: iconst_1
    //   591: iadd
    //   592: aload_0
    //   593: getfield srcLen : I
    //   596: if_icmpge -> 615
    //   599: aload_0
    //   600: getfield source : Ljava/lang/String;
    //   603: aload_0
    //   604: getfield pos : I
    //   607: iconst_1
    //   608: iadd
    //   609: invokevirtual charAt : (I)C
    //   612: goto -> 616
    //   615: iconst_0
    //   616: istore_3
    //   617: aload_0
    //   618: getfield pos : I
    //   621: iconst_2
    //   622: iadd
    //   623: aload_0
    //   624: getfield srcLen : I
    //   627: if_icmpge -> 646
    //   630: aload_0
    //   631: getfield source : Ljava/lang/String;
    //   634: aload_0
    //   635: getfield pos : I
    //   638: iconst_2
    //   639: iadd
    //   640: invokevirtual charAt : (I)C
    //   643: goto -> 647
    //   646: iconst_0
    //   647: istore #4
    //   649: goto -> 1408
    //   652: aload_0
    //   653: dup
    //   654: getfield pos : I
    //   657: iconst_1
    //   658: iadd
    //   659: dup_x1
    //   660: putfield pos : I
    //   663: aload_0
    //   664: getfield stopAt : I
    //   667: if_icmpge -> 697
    //   670: aload_0
    //   671: getfield source : Ljava/lang/String;
    //   674: aload_0
    //   675: getfield pos : I
    //   678: invokevirtual charAt : (I)C
    //   681: dup
    //   682: istore_2
    //   683: bipush #10
    //   685: if_icmpeq -> 697
    //   688: iload_2
    //   689: bipush #13
    //   691: if_icmpeq -> 697
    //   694: goto -> 652
    //   697: aload_0
    //   698: getfield pos : I
    //   701: aload_0
    //   702: getfield stopAt : I
    //   705: if_icmplt -> 721
    //   708: aload_0
    //   709: dup
    //   710: getfield pos : I
    //   713: iconst_1
    //   714: isub
    //   715: putfield pos : I
    //   718: goto -> 1408
    //   721: aload_0
    //   722: getfield pos : I
    //   725: iconst_1
    //   726: iadd
    //   727: aload_0
    //   728: getfield srcLen : I
    //   731: if_icmpge -> 750
    //   734: aload_0
    //   735: getfield source : Ljava/lang/String;
    //   738: aload_0
    //   739: getfield pos : I
    //   742: iconst_1
    //   743: iadd
    //   744: invokevirtual charAt : (I)C
    //   747: goto -> 751
    //   750: iconst_0
    //   751: istore_3
    //   752: iload_2
    //   753: bipush #13
    //   755: if_icmpne -> 805
    //   758: iload_3
    //   759: bipush #10
    //   761: if_icmpne -> 805
    //   764: aload_0
    //   765: dup
    //   766: getfield pos : I
    //   769: iconst_1
    //   770: iadd
    //   771: putfield pos : I
    //   774: aload_0
    //   775: getfield pos : I
    //   778: iconst_1
    //   779: iadd
    //   780: aload_0
    //   781: getfield srcLen : I
    //   784: if_icmpge -> 803
    //   787: aload_0
    //   788: getfield source : Ljava/lang/String;
    //   791: aload_0
    //   792: getfield pos : I
    //   795: iconst_1
    //   796: iadd
    //   797: invokevirtual charAt : (I)C
    //   800: goto -> 804
    //   803: iconst_0
    //   804: istore_3
    //   805: aload_0
    //   806: getfield pos : I
    //   809: iconst_2
    //   810: iadd
    //   811: aload_0
    //   812: getfield srcLen : I
    //   815: if_icmpge -> 834
    //   818: aload_0
    //   819: getfield source : Ljava/lang/String;
    //   822: aload_0
    //   823: getfield pos : I
    //   826: iconst_2
    //   827: iadd
    //   828: invokevirtual charAt : (I)C
    //   831: goto -> 835
    //   834: iconst_0
    //   835: istore #4
    //   837: goto -> 1408
    //   840: iload #6
    //   842: ifeq -> 1068
    //   845: aload_1
    //   846: getstatic com/mysql/cj/util/SearchMode.SKIP_HINT_BLOCKS : Lcom/mysql/cj/util/SearchMode;
    //   849: invokeinterface contains : (Ljava/lang/Object;)Z
    //   854: ifeq -> 1068
    //   857: iload_2
    //   858: bipush #47
    //   860: if_icmpne -> 1068
    //   863: iload_3
    //   864: bipush #42
    //   866: if_icmpne -> 1068
    //   869: iload #4
    //   871: bipush #43
    //   873: if_icmpne -> 1068
    //   876: aload_0
    //   877: dup
    //   878: getfield pos : I
    //   881: iconst_1
    //   882: iadd
    //   883: putfield pos : I
    //   886: aload_0
    //   887: dup
    //   888: getfield pos : I
    //   891: iconst_1
    //   892: iadd
    //   893: putfield pos : I
    //   896: aload_0
    //   897: dup
    //   898: getfield pos : I
    //   901: iconst_1
    //   902: iadd
    //   903: dup_x1
    //   904: putfield pos : I
    //   907: aload_0
    //   908: getfield stopAt : I
    //   911: if_icmpge -> 968
    //   914: aload_0
    //   915: getfield source : Ljava/lang/String;
    //   918: aload_0
    //   919: getfield pos : I
    //   922: invokevirtual charAt : (I)C
    //   925: bipush #42
    //   927: if_icmpne -> 896
    //   930: aload_0
    //   931: getfield pos : I
    //   934: iconst_1
    //   935: iadd
    //   936: aload_0
    //   937: getfield srcLen : I
    //   940: if_icmpge -> 959
    //   943: aload_0
    //   944: getfield source : Ljava/lang/String;
    //   947: aload_0
    //   948: getfield pos : I
    //   951: iconst_1
    //   952: iadd
    //   953: invokevirtual charAt : (I)C
    //   956: goto -> 960
    //   959: iconst_0
    //   960: bipush #47
    //   962: if_icmpeq -> 968
    //   965: goto -> 896
    //   968: aload_0
    //   969: getfield pos : I
    //   972: aload_0
    //   973: getfield stopAt : I
    //   976: if_icmplt -> 992
    //   979: aload_0
    //   980: dup
    //   981: getfield pos : I
    //   984: iconst_1
    //   985: isub
    //   986: putfield pos : I
    //   989: goto -> 1002
    //   992: aload_0
    //   993: dup
    //   994: getfield pos : I
    //   997: iconst_1
    //   998: iadd
    //   999: putfield pos : I
    //   1002: aload_0
    //   1003: getfield pos : I
    //   1006: iconst_1
    //   1007: iadd
    //   1008: aload_0
    //   1009: getfield srcLen : I
    //   1012: if_icmpge -> 1031
    //   1015: aload_0
    //   1016: getfield source : Ljava/lang/String;
    //   1019: aload_0
    //   1020: getfield pos : I
    //   1023: iconst_1
    //   1024: iadd
    //   1025: invokevirtual charAt : (I)C
    //   1028: goto -> 1032
    //   1031: iconst_0
    //   1032: istore_3
    //   1033: aload_0
    //   1034: getfield pos : I
    //   1037: iconst_2
    //   1038: iadd
    //   1039: aload_0
    //   1040: getfield srcLen : I
    //   1043: if_icmpge -> 1062
    //   1046: aload_0
    //   1047: getfield source : Ljava/lang/String;
    //   1050: aload_0
    //   1051: getfield pos : I
    //   1054: iconst_2
    //   1055: iadd
    //   1056: invokevirtual charAt : (I)C
    //   1059: goto -> 1063
    //   1062: iconst_0
    //   1063: istore #4
    //   1065: goto -> 1408
    //   1068: iload #6
    //   1070: ifeq -> 1295
    //   1073: aload_1
    //   1074: getstatic com/mysql/cj/util/SearchMode.SKIP_MYSQL_MARKERS : Lcom/mysql/cj/util/SearchMode;
    //   1077: invokeinterface contains : (Ljava/lang/Object;)Z
    //   1082: ifeq -> 1295
    //   1085: iload_2
    //   1086: bipush #47
    //   1088: if_icmpne -> 1295
    //   1091: iload_3
    //   1092: bipush #42
    //   1094: if_icmpne -> 1295
    //   1097: iload #4
    //   1099: bipush #33
    //   1101: if_icmpne -> 1295
    //   1104: aload_0
    //   1105: dup
    //   1106: getfield pos : I
    //   1109: iconst_1
    //   1110: iadd
    //   1111: putfield pos : I
    //   1114: aload_0
    //   1115: dup
    //   1116: getfield pos : I
    //   1119: iconst_1
    //   1120: iadd
    //   1121: putfield pos : I
    //   1124: iload #4
    //   1126: bipush #33
    //   1128: if_icmpne -> 1224
    //   1131: iconst_0
    //   1132: istore #7
    //   1134: iload #7
    //   1136: iconst_5
    //   1137: if_icmpge -> 1187
    //   1140: aload_0
    //   1141: getfield pos : I
    //   1144: iconst_1
    //   1145: iadd
    //   1146: iload #7
    //   1148: iadd
    //   1149: aload_0
    //   1150: getfield srcLen : I
    //   1153: if_icmpge -> 1187
    //   1156: aload_0
    //   1157: getfield source : Ljava/lang/String;
    //   1160: aload_0
    //   1161: getfield pos : I
    //   1164: iconst_1
    //   1165: iadd
    //   1166: iload #7
    //   1168: iadd
    //   1169: invokevirtual charAt : (I)C
    //   1172: invokestatic isDigit : (C)Z
    //   1175: ifne -> 1181
    //   1178: goto -> 1187
    //   1181: iinc #7, 1
    //   1184: goto -> 1134
    //   1187: iload #7
    //   1189: iconst_5
    //   1190: if_icmpne -> 1224
    //   1193: aload_0
    //   1194: dup
    //   1195: getfield pos : I
    //   1198: iconst_5
    //   1199: iadd
    //   1200: putfield pos : I
    //   1203: aload_0
    //   1204: getfield pos : I
    //   1207: aload_0
    //   1208: getfield stopAt : I
    //   1211: if_icmplt -> 1224
    //   1214: aload_0
    //   1215: aload_0
    //   1216: getfield stopAt : I
    //   1219: iconst_1
    //   1220: isub
    //   1221: putfield pos : I
    //   1224: aload_0
    //   1225: getfield pos : I
    //   1228: iconst_1
    //   1229: iadd
    //   1230: aload_0
    //   1231: getfield srcLen : I
    //   1234: if_icmpge -> 1253
    //   1237: aload_0
    //   1238: getfield source : Ljava/lang/String;
    //   1241: aload_0
    //   1242: getfield pos : I
    //   1245: iconst_1
    //   1246: iadd
    //   1247: invokevirtual charAt : (I)C
    //   1250: goto -> 1254
    //   1253: iconst_0
    //   1254: istore_3
    //   1255: aload_0
    //   1256: getfield pos : I
    //   1259: iconst_2
    //   1260: iadd
    //   1261: aload_0
    //   1262: getfield srcLen : I
    //   1265: if_icmpge -> 1284
    //   1268: aload_0
    //   1269: getfield source : Ljava/lang/String;
    //   1272: aload_0
    //   1273: getfield pos : I
    //   1276: iconst_2
    //   1277: iadd
    //   1278: invokevirtual charAt : (I)C
    //   1281: goto -> 1285
    //   1284: iconst_0
    //   1285: istore #4
    //   1287: aload_0
    //   1288: iconst_1
    //   1289: putfield inMysqlBlock : Z
    //   1292: goto -> 1408
    //   1295: aload_0
    //   1296: getfield inMysqlBlock : Z
    //   1299: ifeq -> 1384
    //   1302: iload #6
    //   1304: ifeq -> 1384
    //   1307: aload_1
    //   1308: getstatic com/mysql/cj/util/SearchMode.SKIP_MYSQL_MARKERS : Lcom/mysql/cj/util/SearchMode;
    //   1311: invokeinterface contains : (Ljava/lang/Object;)Z
    //   1316: ifeq -> 1384
    //   1319: iload_2
    //   1320: bipush #42
    //   1322: if_icmpne -> 1384
    //   1325: iload_3
    //   1326: bipush #47
    //   1328: if_icmpne -> 1384
    //   1331: aload_0
    //   1332: dup
    //   1333: getfield pos : I
    //   1336: iconst_1
    //   1337: iadd
    //   1338: putfield pos : I
    //   1341: iload #4
    //   1343: istore_3
    //   1344: aload_0
    //   1345: getfield pos : I
    //   1348: iconst_2
    //   1349: iadd
    //   1350: aload_0
    //   1351: getfield srcLen : I
    //   1354: if_icmpge -> 1373
    //   1357: aload_0
    //   1358: getfield source : Ljava/lang/String;
    //   1361: aload_0
    //   1362: getfield pos : I
    //   1365: iconst_2
    //   1366: iadd
    //   1367: invokevirtual charAt : (I)C
    //   1370: goto -> 1374
    //   1373: iconst_0
    //   1374: istore #4
    //   1376: aload_0
    //   1377: iconst_0
    //   1378: putfield inMysqlBlock : Z
    //   1381: goto -> 1408
    //   1384: aload_1
    //   1385: getstatic com/mysql/cj/util/SearchMode.SKIP_WHITE_SPACE : Lcom/mysql/cj/util/SearchMode;
    //   1388: invokeinterface contains : (Ljava/lang/Object;)Z
    //   1393: ifeq -> 1403
    //   1396: iload_2
    //   1397: invokestatic isWhitespace : (C)Z
    //   1400: ifne -> 1408
    //   1403: aload_0
    //   1404: getfield pos : I
    //   1407: ireturn
    //   1408: aload_0
    //   1409: iconst_0
    //   1410: putfield escaped : Z
    //   1413: aload_0
    //   1414: dup
    //   1415: getfield pos : I
    //   1418: iconst_1
    //   1419: iadd
    //   1420: putfield pos : I
    //   1423: goto -> 68
    //   1426: iconst_m1
    //   1427: ireturn
    // Line number table:
    //   Java source line number -> byte code offset
    //   #349	-> 0
    //   #350	-> 7
    //   #353	-> 9
    //   #354	-> 20
    //   #357	-> 22
    //   #358	-> 24
    //   #359	-> 36
    //   #361	-> 68
    //   #362	-> 79
    //   #363	-> 81
    //   #364	-> 84
    //   #366	-> 116
    //   #367	-> 119
    //   #369	-> 145
    //   #371	-> 174
    //   #372	-> 180
    //   #373	-> 191
    //   #376	-> 204
    //   #377	-> 235
    //   #380	-> 270
    //   #383	-> 313
    //   #384	-> 323
    //   #385	-> 380
    //   #388	-> 395
    //   #389	-> 406
    //   #391	-> 419
    //   #395	-> 429
    //   #396	-> 460
    //   #398	-> 495
    //   #399	-> 526
    //   #401	-> 561
    //   #403	-> 566
    //   #404	-> 576
    //   #406	-> 586
    //   #407	-> 617
    //   #410	-> 652
    //   #413	-> 697
    //   #414	-> 708
    //   #417	-> 721
    //   #418	-> 752
    //   #420	-> 764
    //   #421	-> 774
    //   #423	-> 805
    //   #427	-> 840
    //   #429	-> 876
    //   #430	-> 886
    //   #431	-> 896
    //   #432	-> 953
    //   #435	-> 968
    //   #436	-> 979
    //   #438	-> 992
    //   #442	-> 1002
    //   #443	-> 1033
    //   #445	-> 1068
    //   #447	-> 1104
    //   #448	-> 1114
    //   #449	-> 1124
    //   #451	-> 1131
    //   #452	-> 1134
    //   #453	-> 1140
    //   #454	-> 1178
    //   #452	-> 1181
    //   #457	-> 1187
    //   #458	-> 1193
    //   #459	-> 1203
    //   #460	-> 1214
    //   #466	-> 1224
    //   #467	-> 1255
    //   #469	-> 1287
    //   #471	-> 1295
    //   #473	-> 1331
    //   #475	-> 1341
    //   #476	-> 1344
    //   #478	-> 1376
    //   #480	-> 1384
    //   #482	-> 1403
    //   #486	-> 1408
    //   #361	-> 1413
    //   #489	-> 1426
    // Local variable table:
    //   start	length	slot	name	descriptor
    //   1134	90	7	i	I
    //   119	1294	5	dashDashCommentImmediateEnd	Z
    //   145	1268	6	checkSkipConditions	Z
    //   0	1428	0	this	Lcom/mysql/cj/util/StringInspector;
    //   0	1428	1	searchMode	Ljava/util/Set;
    //   24	1404	2	c0	C
    //   36	1392	3	c1	C
    //   68	1360	4	c2	C
    // Local variable type table:
    //   start	length	slot	name	signature
    //   0	1428	1	searchMode	Ljava/util/Set<Lcom/mysql/cj/util/SearchMode;>;
  }
  
  private int indexOfClosingMarker(Set<SearchMode> searchMode) {
    if (this.source == null)
      return -1; 
    if (this.pos >= this.stopAt)
      return -1; 
    char c0 = this.source.charAt(this.pos);
    int markerIndex = this.openingMarkers.indexOf(c0);
    if (markerIndex == -1)
      return this.pos; 
    int nestedMarkersCount = 0;
    char openingMarker = c0;
    char closingMarker = this.closingMarkers.charAt(markerIndex);
    boolean outerIsAnOverridingMarker = (this.overridingMarkers.indexOf(openingMarker) != -1);
    while (++this.pos < this.stopAt && ((c0 = this.source.charAt(this.pos)) != closingMarker || nestedMarkersCount != 0)) {
      if (!outerIsAnOverridingMarker && this.overridingMarkers.indexOf(c0) != -1) {
        int overridingMarkerIndex = this.openingMarkers.indexOf(c0);
        int overridingNestedMarkersCount = 0;
        char overridingOpeningMarker = c0;
        char overridingClosingMarker = this.closingMarkers.charAt(overridingMarkerIndex);
        while (++this.pos < this.stopAt && ((c0 = this.source.charAt(this.pos)) != overridingClosingMarker || overridingNestedMarkersCount != 0)) {
          if (c0 == overridingOpeningMarker) {
            overridingNestedMarkersCount++;
            continue;
          } 
          if (c0 == overridingClosingMarker) {
            overridingNestedMarkersCount--;
            continue;
          } 
          if (searchMode.contains(SearchMode.ALLOW_BACKSLASH_ESCAPE) && c0 == '\\')
            this.pos++; 
        } 
        if (this.pos >= this.stopAt)
          this.pos--; 
        continue;
      } 
      if (c0 == openingMarker) {
        nestedMarkersCount++;
        continue;
      } 
      if (c0 == closingMarker) {
        nestedMarkersCount--;
        continue;
      } 
      if (searchMode.contains(SearchMode.ALLOW_BACKSLASH_ESCAPE) && c0 == '\\')
        this.pos++; 
    } 
    return this.pos;
  }
  
  public int indexOfNextAlphanumericChar() {
    if (this.source == null)
      return -1; 
    if (this.pos >= this.stopAt)
      return -1; 
    Set<SearchMode> searchMode = this.defaultSearchMode;
    if (!this.defaultSearchMode.contains(SearchMode.SKIP_WHITE_SPACE)) {
      searchMode = EnumSet.copyOf(this.defaultSearchMode);
      searchMode.add(SearchMode.SKIP_WHITE_SPACE);
    } 
    while (this.pos < this.stopAt) {
      int prevPos = this.pos;
      if (indexOfNextChar(searchMode) == -1)
        return -1; 
      if (Character.isLetterOrDigit(this.source.charAt(this.pos)))
        return this.pos; 
      if (this.pos == prevPos)
        incrementPosition(searchMode); 
    } 
    return -1;
  }
  
  public int indexOfNextNonWsChar() {
    if (this.source == null)
      return -1; 
    if (this.pos >= this.stopAt)
      return -1; 
    Set<SearchMode> searchMode = this.defaultSearchMode;
    if (!this.defaultSearchMode.contains(SearchMode.SKIP_WHITE_SPACE)) {
      searchMode = EnumSet.copyOf(this.defaultSearchMode);
      searchMode.add(SearchMode.SKIP_WHITE_SPACE);
    } 
    return indexOfNextChar(searchMode);
  }
  
  public int indexOfNextWsChar() {
    if (this.source == null)
      return -1; 
    if (this.pos >= this.stopAt)
      return -1; 
    Set<SearchMode> searchMode = this.defaultSearchMode;
    if (this.defaultSearchMode.contains(SearchMode.SKIP_WHITE_SPACE)) {
      searchMode = EnumSet.copyOf(this.defaultSearchMode);
      searchMode.remove(SearchMode.SKIP_WHITE_SPACE);
    } 
    while (this.pos < this.stopAt) {
      int prevPos = this.pos;
      if (indexOfNextChar(searchMode) == -1)
        return -1; 
      if (Character.isWhitespace(this.source.charAt(this.pos)))
        return this.pos; 
      if (this.pos == prevPos)
        incrementPosition(searchMode); 
    } 
    return -1;
  }
  
  public int indexOfIgnoreCase(String searchFor) {
    return indexOfIgnoreCase(searchFor, this.defaultSearchMode);
  }
  
  public int indexOfIgnoreCase(String searchFor, Set<SearchMode> searchMode) {
    if (searchFor == null)
      return -1; 
    int searchForLength = searchFor.length();
    int localStopAt = this.srcLen - searchForLength + 1;
    if (localStopAt > this.stopAt)
      localStopAt = this.stopAt; 
    if (this.pos >= localStopAt || searchForLength == 0)
      return -1; 
    char firstCharOfSearchForUc = Character.toUpperCase(searchFor.charAt(0));
    char firstCharOfSearchForLc = Character.toLowerCase(searchFor.charAt(0));
    Set<SearchMode> localSearchMode = searchMode;
    if (Character.isWhitespace(firstCharOfSearchForLc) && this.defaultSearchMode.contains(SearchMode.SKIP_WHITE_SPACE)) {
      localSearchMode = EnumSet.copyOf(this.defaultSearchMode);
      localSearchMode.remove(SearchMode.SKIP_WHITE_SPACE);
    } 
    while (this.pos < localStopAt) {
      if (indexOfNextChar(localSearchMode) == -1)
        return -1; 
      if (StringUtils.isCharEqualIgnoreCase(getChar(), firstCharOfSearchForUc, firstCharOfSearchForLc) && 
        StringUtils.regionMatchesIgnoreCase(this.source, this.pos, searchFor))
        return this.pos; 
      incrementPosition(localSearchMode);
    } 
    return -1;
  }
  
  public int indexOfIgnoreCase(String... searchFor) {
    if (searchFor == null)
      return -1; 
    int searchForLength = 0;
    for (String searchForPart : searchFor)
      searchForLength += searchForPart.length(); 
    if (searchForLength == 0)
      return -1; 
    int searchForWordsCount = searchFor.length;
    searchForLength += (searchForWordsCount > 0) ? (searchForWordsCount - 1) : 0;
    int localStopAt = this.srcLen - searchForLength + 1;
    if (localStopAt > this.stopAt)
      localStopAt = this.stopAt; 
    if (this.pos >= localStopAt)
      return -1; 
    Set<SearchMode> searchMode1 = this.defaultSearchMode;
    if (Character.isWhitespace(searchFor[0].charAt(0)) && this.defaultSearchMode.contains(SearchMode.SKIP_WHITE_SPACE)) {
      searchMode1 = EnumSet.copyOf(this.defaultSearchMode);
      searchMode1.remove(SearchMode.SKIP_WHITE_SPACE);
    } 
    Set<SearchMode> searchMode2 = EnumSet.copyOf(this.defaultSearchMode);
    searchMode2.add(SearchMode.SKIP_WHITE_SPACE);
    searchMode2.remove(SearchMode.SKIP_BETWEEN_MARKERS);
    while (this.pos < localStopAt) {
      int positionOfFirstWord = indexOfIgnoreCase(searchFor[0], searchMode1);
      if (positionOfFirstWord == -1 || positionOfFirstWord >= localStopAt)
        return -1; 
      mark();
      int startingPositionForNextWord = incrementPosition(searchFor[0].length(), searchMode2);
      int wc = 0;
      boolean match = true;
      while (++wc < searchForWordsCount && match) {
        if (indexOfNextChar(searchMode2) == -1 || startingPositionForNextWord == this.pos || 
          !StringUtils.regionMatchesIgnoreCase(this.source, this.pos, searchFor[wc])) {
          match = false;
          continue;
        } 
        startingPositionForNextWord = incrementPosition(searchFor[wc].length(), searchMode2);
      } 
      if (match) {
        reset();
        return positionOfFirstWord;
      } 
    } 
    return -1;
  }
  
  public int matchesIgnoreCase(String toMatch) {
    if (toMatch == null)
      return -1; 
    int toMatchLength = toMatch.length();
    int localStopAt = this.srcLen - toMatchLength + 1;
    if (localStopAt > this.stopAt)
      localStopAt = this.stopAt; 
    if (this.pos >= localStopAt || toMatchLength == 0)
      return -1; 
    char firstCharOfToMatchUc = Character.toUpperCase(toMatch.charAt(0));
    char firstCharOfToMatchLc = Character.toLowerCase(toMatch.charAt(0));
    if (StringUtils.isCharEqualIgnoreCase(getChar(), firstCharOfToMatchUc, firstCharOfToMatchLc) && 
      StringUtils.regionMatchesIgnoreCase(this.source, this.pos, toMatch))
      return this.pos + toMatchLength; 
    return -1;
  }
  
  public int matchesIgnoreCase(String... toMatch) {
    if (toMatch == null)
      return -1; 
    int toMatchLength = 0;
    for (String toMatchPart : toMatch)
      toMatchLength += toMatchPart.length(); 
    if (toMatchLength == 0)
      return -1; 
    int toMatchWordsCount = toMatch.length;
    toMatchLength += (toMatchWordsCount > 0) ? (toMatchWordsCount - 1) : 0;
    int localStopAt = this.srcLen - toMatchLength + 1;
    if (localStopAt > this.stopAt)
      localStopAt = this.stopAt; 
    if (this.pos >= localStopAt)
      return -1; 
    Set<SearchMode> searchMode2 = EnumSet.copyOf(this.defaultSearchMode);
    searchMode2.add(SearchMode.SKIP_WHITE_SPACE);
    searchMode2.remove(SearchMode.SKIP_BETWEEN_MARKERS);
    mark();
    int endOfMatch = -1;
    int startingPositionForNextWord = -1;
    for (String searchForPart : toMatch) {
      if (getPosition() == startingPositionForNextWord) {
        reset();
        return -1;
      } 
      endOfMatch = matchesIgnoreCase(searchForPart);
      if (endOfMatch == -1) {
        reset();
        return -1;
      } 
      startingPositionForNextWord = incrementPosition(searchForPart.length(), searchMode2);
      indexOfNextChar(searchMode2);
    } 
    reset();
    return endOfMatch;
  }
  
  public String stripCommentsAndHints() {
    restart();
    Set<SearchMode> searchMode = EnumSet.of(SearchMode.SKIP_BLOCK_COMMENTS, SearchMode.SKIP_LINE_COMMENTS, SearchMode.SKIP_HINT_BLOCKS);
    if (this.defaultSearchMode.contains(SearchMode.ALLOW_BACKSLASH_ESCAPE))
      searchMode.add(SearchMode.ALLOW_BACKSLASH_ESCAPE); 
    StringBuilder noCommsStr = new StringBuilder(this.source.length());
    while (this.pos < this.stopAt) {
      int prevPos = this.pos;
      if (indexOfNextChar(searchMode) == -1)
        return noCommsStr.toString(); 
      if (!this.escaped && this.openingMarkers.indexOf(getChar()) != -1) {
        int idxOpMrkr = this.pos;
        if (indexOfClosingMarker(searchMode) < this.srcLen)
          incrementPosition(searchMode); 
        noCommsStr.append(this.source, idxOpMrkr, this.pos);
        continue;
      } 
      if (this.pos - prevPos > 1 && 
        prevPos > 0 && !Character.isWhitespace(this.source.charAt(prevPos - 1)) && !Character.isWhitespace(this.source.charAt(this.pos)))
        noCommsStr.append(" "); 
      noCommsStr.append(getChar());
      incrementPosition(searchMode);
    } 
    return noCommsStr.toString();
  }
  
  public List<String> split(String delimiter, boolean trim) {
    if (delimiter == null)
      throw new IllegalArgumentException(Messages.getString("StringInspector.8")); 
    restart();
    int startPos = 0;
    List<String> splitParts = new ArrayList<>();
    while (indexOfIgnoreCase(delimiter) != -1) {
      indexOfIgnoreCase(delimiter);
      String part = this.source.substring(startPos, this.pos);
      if (trim)
        part = part.trim(); 
      splitParts.add(part);
      startPos = incrementPosition(delimiter.length());
    } 
    String token = this.source.substring(startPos);
    if (trim)
      token = token.trim(); 
    splitParts.add(token);
    return splitParts;
  }
}
