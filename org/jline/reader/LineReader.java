package org.jline.reader;

import java.io.File;
import java.util.Collection;
import java.util.Map;
import org.jline.keymap.KeyMap;
import org.jline.terminal.MouseEvent;
import org.jline.terminal.Terminal;
import org.jline.utils.AttributedString;

public interface LineReader {
  public static final String PROP_SUPPORT_PARSEDLINE = "org.jline.reader.support.parsedline";
  
  public static final String CALLBACK_INIT = "callback-init";
  
  public static final String CALLBACK_FINISH = "callback-finish";
  
  public static final String CALLBACK_KEYMAP = "callback-keymap";
  
  public static final String ACCEPT_AND_INFER_NEXT_HISTORY = "accept-and-infer-next-history";
  
  public static final String ACCEPT_AND_HOLD = "accept-and-hold";
  
  public static final String ACCEPT_LINE = "accept-line";
  
  public static final String ACCEPT_LINE_AND_DOWN_HISTORY = "accept-line-and-down-history";
  
  public static final String ARGUMENT_BASE = "argument-base";
  
  public static final String BACKWARD_CHAR = "backward-char";
  
  public static final String BACKWARD_DELETE_CHAR = "backward-delete-char";
  
  public static final String BACKWARD_DELETE_WORD = "backward-delete-word";
  
  public static final String BACKWARD_KILL_LINE = "backward-kill-line";
  
  public static final String BACKWARD_KILL_WORD = "backward-kill-word";
  
  public static final String BACKWARD_WORD = "backward-word";
  
  public static final String BEEP = "beep";
  
  public static final String BEGINNING_OF_BUFFER_OR_HISTORY = "beginning-of-buffer-or-history";
  
  public static final String BEGINNING_OF_HISTORY = "beginning-of-history";
  
  public static final String BEGINNING_OF_LINE = "beginning-of-line";
  
  public static final String BEGINNING_OF_LINE_HIST = "beginning-of-line-hist";
  
  public static final String CAPITALIZE_WORD = "capitalize-word";
  
  public static final String CHARACTER_SEARCH = "character-search";
  
  public static final String CHARACTER_SEARCH_BACKWARD = "character-search-backward";
  
  public static final String CLEAR = "clear";
  
  public static final String CLEAR_SCREEN = "clear-screen";
  
  public static final String COMPLETE_PREFIX = "complete-prefix";
  
  public static final String COMPLETE_WORD = "complete-word";
  
  public static final String COPY_PREV_WORD = "copy-prev-word";
  
  public static final String COPY_REGION_AS_KILL = "copy-region-as-kill";
  
  public static final String DELETE_CHAR = "delete-char";
  
  public static final String DELETE_CHAR_OR_LIST = "delete-char-or-list";
  
  public static final String DELETE_WORD = "delete-word";
  
  public static final String DIGIT_ARGUMENT = "digit-argument";
  
  public static final String DO_LOWERCASE_VERSION = "do-lowercase-version";
  
  public static final String DOWN_CASE_WORD = "down-case-word";
  
  public static final String DOWN_HISTORY = "down-history";
  
  public static final String DOWN_LINE = "down-line";
  
  public static final String DOWN_LINE_OR_HISTORY = "down-line-or-history";
  
  public static final String DOWN_LINE_OR_SEARCH = "down-line-or-search";
  
  public static final String EDIT_AND_EXECUTE_COMMAND = "edit-and-execute-command";
  
  public static final String EMACS_BACKWARD_WORD = "emacs-backward-word";
  
  public static final String EMACS_EDITING_MODE = "emacs-editing-mode";
  
  public static final String EMACS_FORWARD_WORD = "emacs-forward-word";
  
  public static final String END_OF_BUFFER_OR_HISTORY = "end-of-buffer-or-history";
  
  public static final String END_OF_HISTORY = "end-of-history";
  
  public static final String END_OF_LINE = "end-of-line";
  
  public static final String END_OF_LINE_HIST = "end-of-line-hist";
  
  public static final String EXCHANGE_POINT_AND_MARK = "exchange-point-and-mark";
  
  public static final String EXECUTE_NAMED_CMD = "execute-named-cmd";
  
  public static final String EXPAND_HISTORY = "expand-history";
  
  public static final String EXPAND_OR_COMPLETE = "expand-or-complete";
  
  public static final String EXPAND_OR_COMPLETE_PREFIX = "expand-or-complete-prefix";
  
  public static final String EXPAND_WORD = "expand-word";
  
  public static final String FRESH_LINE = "fresh-line";
  
  public static final String FORWARD_CHAR = "forward-char";
  
  public static final String FORWARD_WORD = "forward-word";
  
  public static final String HISTORY_BEGINNING_SEARCH_BACKWARD = "history-beginning-search-backward";
  
  public static final String HISTORY_BEGINNING_SEARCH_FORWARD = "history-beginning-search-forward";
  
  public static final String HISTORY_INCREMENTAL_PATTERN_SEARCH_BACKWARD = "history-incremental-pattern-search-backward";
  
  public static final String HISTORY_INCREMENTAL_PATTERN_SEARCH_FORWARD = "history-incremental-pattern-search-forward";
  
  public static final String HISTORY_INCREMENTAL_SEARCH_BACKWARD = "history-incremental-search-backward";
  
  public static final String HISTORY_INCREMENTAL_SEARCH_FORWARD = "history-incremental-search-forward";
  
  public static final String HISTORY_SEARCH_BACKWARD = "history-search-backward";
  
  public static final String HISTORY_SEARCH_FORWARD = "history-search-forward";
  
  public static final String INSERT_CLOSE_CURLY = "insert-close-curly";
  
  public static final String INSERT_CLOSE_PAREN = "insert-close-paren";
  
  public static final String INSERT_CLOSE_SQUARE = "insert-close-square";
  
  public static final String INFER_NEXT_HISTORY = "infer-next-history";
  
  public static final String INSERT_COMMENT = "insert-comment";
  
  public static final String INSERT_LAST_WORD = "insert-last-word";
  
  public static final String KILL_BUFFER = "kill-buffer";
  
  public static final String KILL_LINE = "kill-line";
  
  public static final String KILL_REGION = "kill-region";
  
  public static final String KILL_WHOLE_LINE = "kill-whole-line";
  
  public static final String KILL_WORD = "kill-word";
  
  public static final String LIST_CHOICES = "list-choices";
  
  public static final String LIST_EXPAND = "list-expand";
  
  public static final String MAGIC_SPACE = "magic-space";
  
  public static final String MENU_EXPAND_OR_COMPLETE = "menu-expand-or-complete";
  
  public static final String MENU_COMPLETE = "menu-complete";
  
  public static final String MENU_SELECT = "menu-select";
  
  public static final String NEG_ARGUMENT = "neg-argument";
  
  public static final String OVERWRITE_MODE = "overwrite-mode";
  
  public static final String PUT_REPLACE_SELECTION = "put-replace-selection";
  
  public static final String QUOTED_INSERT = "quoted-insert";
  
  public static final String READ_COMMAND = "read-command";
  
  public static final String RECURSIVE_EDIT = "recursive-edit";
  
  public static final String REDISPLAY = "redisplay";
  
  public static final String REDRAW_LINE = "redraw-line";
  
  public static final String REDO = "redo";
  
  public static final String REVERSE_MENU_COMPLETE = "reverse-menu-complete";
  
  public static final String SELF_INSERT = "self-insert";
  
  public static final String SELF_INSERT_UNMETA = "self-insert-unmeta";
  
  public static final String SEND_BREAK = "abort";
  
  public static final String SET_LOCAL_HISTORY = "set-local-history";
  
  public static final String SET_MARK_COMMAND = "set-mark-command";
  
  public static final String SPELL_WORD = "spell-word";
  
  public static final String SPLIT_UNDO = "split-undo";
  
  public static final String TRANSPOSE_CHARS = "transpose-chars";
  
  public static final String TRANSPOSE_WORDS = "transpose-words";
  
  public static final String UNDEFINED_KEY = "undefined-key";
  
  public static final String UNDO = "undo";
  
  public static final String UNIVERSAL_ARGUMENT = "universal-argument";
  
  public static final String UP_CASE_WORD = "up-case-word";
  
  public static final String UP_HISTORY = "up-history";
  
  public static final String UP_LINE = "up-line";
  
  public static final String UP_LINE_OR_HISTORY = "up-line-or-history";
  
  public static final String UP_LINE_OR_SEARCH = "up-line-or-search";
  
  public static final String VI_ADD_EOL = "vi-add-eol";
  
  public static final String VI_ADD_NEXT = "vi-add-next";
  
  public static final String VI_BACKWARD_BLANK_WORD = "vi-backward-blank-word";
  
  public static final String VI_BACKWARD_BLANK_WORD_END = "vi-backward-blank-word-end";
  
  public static final String VI_BACKWARD_CHAR = "vi-backward-char";
  
  public static final String VI_BACKWARD_DELETE_CHAR = "vi-backward-delete-char";
  
  public static final String VI_BACKWARD_KILL_WORD = "vi-backward-kill-word";
  
  public static final String VI_BACKWARD_WORD = "vi-backward-word";
  
  public static final String VI_BACKWARD_WORD_END = "vi-backward-word-end";
  
  public static final String VI_BEGINNING_OF_LINE = "vi-beginning-of-line";
  
  public static final String VI_CHANGE = "vi-change-to";
  
  public static final String VI_CHANGE_EOL = "vi-change-eol";
  
  public static final String VI_CHANGE_WHOLE_LINE = "vi-change-whole-line";
  
  public static final String VI_CMD_MODE = "vi-cmd-mode";
  
  public static final String VI_DELETE = "vi-delete";
  
  public static final String VI_DELETE_CHAR = "vi-delete-char";
  
  public static final String VI_DIGIT_OR_BEGINNING_OF_LINE = "vi-digit-or-beginning-of-line";
  
  public static final String VI_DOWN_LINE_OR_HISTORY = "vi-down-line-or-history";
  
  public static final String VI_END_OF_LINE = "vi-end-of-line";
  
  public static final String VI_FETCH_HISTORY = "vi-fetch-history";
  
  public static final String VI_FIND_NEXT_CHAR = "vi-find-next-char";
  
  public static final String VI_FIND_NEXT_CHAR_SKIP = "vi-find-next-char-skip";
  
  public static final String VI_FIND_PREV_CHAR = "vi-find-prev-char";
  
  public static final String VI_FIND_PREV_CHAR_SKIP = "vi-find-prev-char-skip";
  
  public static final String VI_FIRST_NON_BLANK = "vi-first-non-blank";
  
  public static final String VI_FORWARD_BLANK_WORD = "vi-forward-blank-word";
  
  public static final String VI_FORWARD_BLANK_WORD_END = "vi-forward-blank-word-end";
  
  public static final String VI_FORWARD_CHAR = "vi-forward-char";
  
  public static final String VI_FORWARD_WORD = "vi-forward-word";
  
  public static final String VI_FORWARD_WORD_END = "vi-forward-word-end";
  
  public static final String VI_GOTO_COLUMN = "vi-goto-column";
  
  public static final String VI_HISTORY_SEARCH_BACKWARD = "vi-history-search-backward";
  
  public static final String VI_HISTORY_SEARCH_FORWARD = "vi-history-search-forward";
  
  public static final String VI_INSERT = "vi-insert";
  
  public static final String VI_INSERT_BOL = "vi-insert-bol";
  
  public static final String VI_INSERT_COMMENT = "vi-insert-comment";
  
  public static final String VI_JOIN = "vi-join";
  
  public static final String VI_KILL_EOL = "vi-kill-eol";
  
  public static final String VI_KILL_LINE = "vi-kill-line";
  
  public static final String VI_MATCH_BRACKET = "vi-match-bracket";
  
  public static final String VI_OPEN_LINE_ABOVE = "vi-open-line-above";
  
  public static final String VI_OPEN_LINE_BELOW = "vi-open-line-below";
  
  public static final String VI_OPER_SWAP_CASE = "vi-oper-swap-case";
  
  public static final String VI_PUT_AFTER = "vi-put-after";
  
  public static final String VI_PUT_BEFORE = "vi-put-before";
  
  public static final String VI_QUOTED_INSERT = "vi-quoted-insert";
  
  public static final String VI_REPEAT_CHANGE = "vi-repeat-change";
  
  public static final String VI_REPEAT_FIND = "vi-repeat-find";
  
  public static final String VI_REPEAT_SEARCH = "vi-repeat-search";
  
  public static final String VI_REPLACE = "vi-replace";
  
  public static final String VI_REPLACE_CHARS = "vi-replace-chars";
  
  public static final String VI_REV_REPEAT_FIND = "vi-rev-repeat-find";
  
  public static final String VI_REV_REPEAT_SEARCH = "vi-rev-repeat-search";
  
  public static final String VI_SET_BUFFER = "vi-set-buffer";
  
  public static final String VI_SUBSTITUTE = "vi-substitute";
  
  public static final String VI_SWAP_CASE = "vi-swap-case";
  
  public static final String VI_UNDO_CHANGE = "vi-undo-change";
  
  public static final String VI_UP_LINE_OR_HISTORY = "vi-up-line-or-history";
  
  public static final String VI_YANK = "vi-yank";
  
  public static final String VI_YANK_EOL = "vi-yank-eol";
  
  public static final String VI_YANK_WHOLE_LINE = "vi-yank-whole-line";
  
  public static final String VISUAL_LINE_MODE = "visual-line-mode";
  
  public static final String VISUAL_MODE = "visual-mode";
  
  public static final String WHAT_CURSOR_POSITION = "what-cursor-position";
  
  public static final String YANK = "yank";
  
  public static final String YANK_POP = "yank-pop";
  
  public static final String MOUSE = "mouse";
  
  public static final String FOCUS_IN = "terminal-focus-in";
  
  public static final String FOCUS_OUT = "terminal-focus-out";
  
  public static final String BEGIN_PASTE = "begin-paste";
  
  public static final String VICMD = "vicmd";
  
  public static final String VIINS = "viins";
  
  public static final String VIOPP = "viopp";
  
  public static final String VISUAL = "visual";
  
  public static final String MAIN = "main";
  
  public static final String EMACS = "emacs";
  
  public static final String SAFE = ".safe";
  
  public static final String MENU = "menu";
  
  public static final String BIND_TTY_SPECIAL_CHARS = "bind-tty-special-chars";
  
  public static final String COMMENT_BEGIN = "comment-begin";
  
  public static final String BELL_STYLE = "bell-style";
  
  public static final String PREFER_VISIBLE_BELL = "prefer-visible-bell";
  
  public static final String LIST_MAX = "list-max";
  
  public static final String MENU_LIST_MAX = "menu-list-max";
  
  public static final String DISABLE_HISTORY = "disable-history";
  
  public static final String DISABLE_COMPLETION = "disable-completion";
  
  public static final String EDITING_MODE = "editing-mode";
  
  public static final String KEYMAP = "keymap";
  
  public static final String BLINK_MATCHING_PAREN = "blink-matching-paren";
  
  public static final String WORDCHARS = "WORDCHARS";
  
  public static final String REMOVE_SUFFIX_CHARS = "REMOVE_SUFFIX_CHARS";
  
  public static final String SEARCH_TERMINATORS = "search-terminators";
  
  public static final String ERRORS = "errors";
  
  public static final String OTHERS_GROUP_NAME = "OTHERS_GROUP_NAME";
  
  public static final String ORIGINAL_GROUP_NAME = "ORIGINAL_GROUP_NAME";
  
  public static final String COMPLETION_STYLE_GROUP = "COMPLETION_STYLE_GROUP";
  
  public static final String COMPLETION_STYLE_LIST_GROUP = "COMPLETION_STYLE_LIST_GROUP";
  
  public static final String COMPLETION_STYLE_SELECTION = "COMPLETION_STYLE_SELECTION";
  
  public static final String COMPLETION_STYLE_LIST_SELECTION = "COMPLETION_STYLE_LIST_SELECTION";
  
  public static final String COMPLETION_STYLE_DESCRIPTION = "COMPLETION_STYLE_DESCRIPTION";
  
  public static final String COMPLETION_STYLE_LIST_DESCRIPTION = "COMPLETION_STYLE_LIST_DESCRIPTION";
  
  public static final String COMPLETION_STYLE_STARTING = "COMPLETION_STYLE_STARTING";
  
  public static final String COMPLETION_STYLE_LIST_STARTING = "COMPLETION_STYLE_LIST_STARTING";
  
  public static final String COMPLETION_STYLE_BACKGROUND = "COMPLETION_STYLE_BACKGROUND";
  
  public static final String COMPLETION_STYLE_LIST_BACKGROUND = "COMPLETION_STYLE_LIST_BACKGROUND";
  
  public static final String SECONDARY_PROMPT_PATTERN = "secondary-prompt-pattern";
  
  public static final String LINE_OFFSET = "line-offset";
  
  public static final String AMBIGUOUS_BINDING = "ambiguous-binding";
  
  public static final String HISTORY_IGNORE = "history-ignore";
  
  public static final String HISTORY_FILE = "history-file";
  
  public static final String HISTORY_SIZE = "history-size";
  
  public static final String HISTORY_FILE_SIZE = "history-file-size";
  
  public static final String INDENTATION = "indentation";
  
  public static final String FEATURES_MAX_BUFFER_SIZE = "features-max-buffer-size";
  
  public static final String SUGGESTIONS_MIN_BUFFER_SIZE = "suggestions-min-buffer-size";
  
  Map<String, KeyMap<Binding>> defaultKeyMaps();
  
  String readLine() throws UserInterruptException, EndOfFileException;
  
  String readLine(Character paramCharacter) throws UserInterruptException, EndOfFileException;
  
  String readLine(String paramString) throws UserInterruptException, EndOfFileException;
  
  String readLine(String paramString, Character paramCharacter) throws UserInterruptException, EndOfFileException;
  
  String readLine(String paramString1, Character paramCharacter, String paramString2) throws UserInterruptException, EndOfFileException;
  
  String readLine(String paramString1, String paramString2, Character paramCharacter, String paramString3) throws UserInterruptException, EndOfFileException;
  
  String readLine(String paramString1, String paramString2, MaskingCallback paramMaskingCallback, String paramString3) throws UserInterruptException, EndOfFileException;
  
  void printAbove(String paramString);
  
  void printAbove(AttributedString paramAttributedString);
  
  boolean isReading();
  
  LineReader variable(String paramString, Object paramObject);
  
  LineReader option(Option paramOption, boolean paramBoolean);
  
  void callWidget(String paramString);
  
  Map<String, Object> getVariables();
  
  Object getVariable(String paramString);
  
  void setVariable(String paramString, Object paramObject);
  
  boolean isSet(Option paramOption);
  
  void setOpt(Option paramOption);
  
  void unsetOpt(Option paramOption);
  
  Terminal getTerminal();
  
  Map<String, Widget> getWidgets();
  
  Map<String, Widget> getBuiltinWidgets();
  
  Buffer getBuffer();
  
  String getAppName();
  
  void runMacro(String paramString);
  
  MouseEvent readMouseEvent();
  
  History getHistory();
  
  Parser getParser();
  
  Highlighter getHighlighter();
  
  Expander getExpander();
  
  Map<String, KeyMap<Binding>> getKeyMaps();
  
  String getKeyMap();
  
  boolean setKeyMap(String paramString);
  
  KeyMap<Binding> getKeys();
  
  ParsedLine getParsedLine();
  
  String getSearchTerm();
  
  RegionType getRegionActive();
  
  int getRegionMark();
  
  void addCommandsInBuffer(Collection<String> paramCollection);
  
  void editAndAddInBuffer(File paramFile) throws Exception;
  
  String getLastBinding();
  
  String getTailTip();
  
  void setTailTip(String paramString);
  
  void setAutosuggestion(SuggestionType paramSuggestionType);
  
  SuggestionType getAutosuggestion();
  
  public enum Option {
    COMPLETE_IN_WORD,
    COMPLETE_MATCHER_CAMELCASE,
    COMPLETE_MATCHER_TYPO(true),
    DISABLE_EVENT_EXPANSION,
    HISTORY_VERIFY,
    HISTORY_IGNORE_SPACE(true),
    HISTORY_IGNORE_DUPS(true),
    HISTORY_REDUCE_BLANKS(true),
    HISTORY_BEEP(true),
    HISTORY_INCREMENTAL(true),
    HISTORY_TIMESTAMPED(true),
    AUTO_GROUP(true),
    AUTO_MENU(true),
    AUTO_LIST(true),
    AUTO_MENU_LIST,
    RECOGNIZE_EXACT,
    GROUP(true),
    GROUP_PERSIST,
    CASE_INSENSITIVE,
    LIST_AMBIGUOUS,
    LIST_PACKED,
    LIST_ROWS_FIRST,
    GLOB_COMPLETE,
    MENU_COMPLETE,
    AUTO_FRESH_LINE,
    DELAY_LINE_WRAP,
    AUTO_PARAM_SLASH(true),
    AUTO_REMOVE_SLASH(true),
    USE_FORWARD_SLASH,
    INSERT_TAB,
    MOUSE,
    DISABLE_HIGHLIGHTER,
    BRACKETED_PASTE(true),
    ERASE_LINE_ON_FINISH,
    CASE_INSENSITIVE_SEARCH,
    INSERT_BRACKET,
    EMPTY_WORD_OPTIONS(true);
    
    private final boolean def;
    
    Option(boolean def) {
      this.def = def;
    }
    
    public final boolean isSet(Map<Option, Boolean> options) {
      Boolean b = options.get(this);
      return (b != null) ? b.booleanValue() : isDef();
    }
    
    public boolean isDef() {
      return this.def;
    }
  }
  
  public enum RegionType {
    NONE, CHAR, LINE, PASTE;
  }
  
  public enum SuggestionType {
    NONE, HISTORY, COMPLETER, TAIL_TIP;
  }
}
