package com.mysql.cj.util;

import java.util.Collections;
import java.util.EnumSet;
import java.util.Set;

public enum SearchMode {
  ALLOW_BACKSLASH_ESCAPE, SKIP_BETWEEN_MARKERS, SKIP_BLOCK_COMMENTS, SKIP_LINE_COMMENTS, SKIP_MYSQL_MARKERS, SKIP_HINT_BLOCKS, SKIP_WHITE_SPACE, VOID;
  
  public static final Set<SearchMode> __FULL;
  
  public static final Set<SearchMode> __BSE_MRK_COM_MYM_HNT_WS;
  
  public static final Set<SearchMode> __MRK_COM_MYM_HNT_WS;
  
  public static final Set<SearchMode> __BSE_COM_MYM_HNT_WS;
  
  public static final Set<SearchMode> __COM_MYM_HNT_WS;
  
  public static final Set<SearchMode> __BSE_MRK_WS;
  
  public static final Set<SearchMode> __MRK_WS;
  
  public static final Set<SearchMode> __NONE;
  
  static {
    __FULL = Collections.unmodifiableSet(EnumSet.allOf(SearchMode.class));
    __BSE_MRK_COM_MYM_HNT_WS = Collections.unmodifiableSet(EnumSet.of(ALLOW_BACKSLASH_ESCAPE, new SearchMode[] { SKIP_BETWEEN_MARKERS, SKIP_BLOCK_COMMENTS, SKIP_LINE_COMMENTS, SKIP_MYSQL_MARKERS, SKIP_HINT_BLOCKS, SKIP_WHITE_SPACE }));
    __MRK_COM_MYM_HNT_WS = Collections.unmodifiableSet(EnumSet.of(SKIP_BETWEEN_MARKERS, new SearchMode[] { SKIP_BLOCK_COMMENTS, SKIP_LINE_COMMENTS, SKIP_MYSQL_MARKERS, SKIP_HINT_BLOCKS, SKIP_WHITE_SPACE }));
    __BSE_COM_MYM_HNT_WS = Collections.unmodifiableSet(
        EnumSet.of(ALLOW_BACKSLASH_ESCAPE, new SearchMode[] { SKIP_BLOCK_COMMENTS, SKIP_LINE_COMMENTS, SKIP_MYSQL_MARKERS, SKIP_HINT_BLOCKS, SKIP_WHITE_SPACE }));
    __COM_MYM_HNT_WS = Collections.unmodifiableSet(EnumSet.of(SKIP_BLOCK_COMMENTS, SKIP_LINE_COMMENTS, SKIP_MYSQL_MARKERS, SKIP_HINT_BLOCKS, SKIP_WHITE_SPACE));
    __BSE_MRK_WS = Collections.unmodifiableSet(EnumSet.of(ALLOW_BACKSLASH_ESCAPE, SKIP_BETWEEN_MARKERS, SKIP_WHITE_SPACE));
    __MRK_WS = Collections.unmodifiableSet(EnumSet.of(SKIP_BETWEEN_MARKERS, SKIP_WHITE_SPACE));
    __NONE = Collections.unmodifiableSet(EnumSet.of(VOID));
  }
}
