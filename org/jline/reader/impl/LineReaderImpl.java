package org.jline.reader.impl;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.Flushable;
import java.io.IOError;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.Spliterators;
import java.util.TreeMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.IntBinaryOperator;
import java.util.function.Supplier;
import java.util.function.ToIntFunction;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import org.jline.keymap.BindingReader;
import org.jline.keymap.KeyMap;
import org.jline.reader.Binding;
import org.jline.reader.Buffer;
import org.jline.reader.Candidate;
import org.jline.reader.Completer;
import org.jline.reader.CompletingParsedLine;
import org.jline.reader.CompletionMatcher;
import org.jline.reader.EOFError;
import org.jline.reader.Editor;
import org.jline.reader.EndOfFileException;
import org.jline.reader.Expander;
import org.jline.reader.Highlighter;
import org.jline.reader.History;
import org.jline.reader.LineReader;
import org.jline.reader.Macro;
import org.jline.reader.MaskingCallback;
import org.jline.reader.ParsedLine;
import org.jline.reader.Parser;
import org.jline.reader.Reference;
import org.jline.reader.SyntaxError;
import org.jline.reader.UserInterruptException;
import org.jline.reader.Widget;
import org.jline.reader.impl.history.DefaultHistory;
import org.jline.terminal.Attributes;
import org.jline.terminal.Cursor;
import org.jline.terminal.MouseEvent;
import org.jline.terminal.Size;
import org.jline.terminal.Terminal;
import org.jline.utils.AttributedCharSequence;
import org.jline.utils.AttributedString;
import org.jline.utils.AttributedStringBuilder;
import org.jline.utils.AttributedStyle;
import org.jline.utils.Curses;
import org.jline.utils.Display;
import org.jline.utils.InfoCmp;
import org.jline.utils.Log;
import org.jline.utils.Status;
import org.jline.utils.StyleResolver;
import org.jline.utils.WCWidth;

public class LineReaderImpl implements LineReader, Flushable {
  public static final char NULL_MASK = '\000';
  
  public static final int TAB_WIDTH = 4;
  
  public static final String DEFAULT_WORDCHARS = "*?_-.[]~=/&;!#$%^(){}<>";
  
  public static final String DEFAULT_REMOVE_SUFFIX_CHARS = " \t\n;&|";
  
  public static final String DEFAULT_COMMENT_BEGIN = "#";
  
  public static final String DEFAULT_SEARCH_TERMINATORS = "\033\n";
  
  public static final String DEFAULT_BELL_STYLE = "";
  
  public static final int DEFAULT_LIST_MAX = 100;
  
  public static final int DEFAULT_MENU_LIST_MAX = 2147483647;
  
  public static final int DEFAULT_ERRORS = 2;
  
  public static final long DEFAULT_BLINK_MATCHING_PAREN = 500L;
  
  public static final long DEFAULT_AMBIGUOUS_BINDING = 1000L;
  
  public static final String DEFAULT_SECONDARY_PROMPT_PATTERN = "%M> ";
  
  public static final String DEFAULT_OTHERS_GROUP_NAME = "others";
  
  public static final String DEFAULT_ORIGINAL_GROUP_NAME = "original";
  
  public static final String DEFAULT_COMPLETION_STYLE_STARTING = "fg:cyan";
  
  public static final String DEFAULT_COMPLETION_STYLE_DESCRIPTION = "fg:bright-black";
  
  public static final String DEFAULT_COMPLETION_STYLE_GROUP = "fg:bright-magenta,bold";
  
  public static final String DEFAULT_COMPLETION_STYLE_SELECTION = "inverse";
  
  public static final String DEFAULT_COMPLETION_STYLE_BACKGROUND = "bg:default";
  
  public static final String DEFAULT_COMPLETION_STYLE_LIST_STARTING = "fg:cyan";
  
  public static final String DEFAULT_COMPLETION_STYLE_LIST_DESCRIPTION = "fg:bright-black";
  
  public static final String DEFAULT_COMPLETION_STYLE_LIST_GROUP = "fg:black,bold";
  
  public static final String DEFAULT_COMPLETION_STYLE_LIST_SELECTION = "inverse";
  
  public static final String DEFAULT_COMPLETION_STYLE_LIST_BACKGROUND = "bg:bright-magenta";
  
  public static final int DEFAULT_INDENTATION = 0;
  
  public static final int DEFAULT_FEATURES_MAX_BUFFER_SIZE = 1000;
  
  public static final int DEFAULT_SUGGESTIONS_MIN_BUFFER_SIZE = 1;
  
  private static final int MIN_ROWS = 3;
  
  public static final String BRACKETED_PASTE_ON = "\033[?2004h";
  
  public static final String BRACKETED_PASTE_OFF = "\033[?2004l";
  
  public static final String BRACKETED_PASTE_BEGIN = "\033[200~";
  
  public static final String BRACKETED_PASTE_END = "\033[201~";
  
  public static final String FOCUS_IN_SEQ = "\033[I";
  
  public static final String FOCUS_OUT_SEQ = "\033[O";
  
  protected final Terminal terminal;
  
  protected final String appName;
  
  protected final Map<String, KeyMap<Binding>> keyMaps;
  
  protected final Map<String, Object> variables;
  
  protected enum State {
    NORMAL, DONE, IGNORE, EOF, INTERRUPT;
  }
  
  protected enum ViMoveMode {
    NORMAL, YANK, DELETE, CHANGE;
  }
  
  protected enum BellType {
    NONE, AUDIBLE, VISIBLE;
  }
  
  protected History history = (History)new DefaultHistory();
  
  protected Completer completer = null;
  
  protected Highlighter highlighter = new DefaultHighlighter();
  
  protected Parser parser = new DefaultParser();
  
  protected Expander expander = new DefaultExpander();
  
  protected CompletionMatcher completionMatcher = new CompletionMatcherImpl();
  
  protected final Map<LineReader.Option, Boolean> options = new HashMap<>();
  
  protected final Buffer buf = new BufferImpl();
  
  protected String tailTip = "";
  
  protected LineReader.SuggestionType autosuggestion = LineReader.SuggestionType.NONE;
  
  protected final Size size = new Size();
  
  protected AttributedString prompt = AttributedString.EMPTY;
  
  protected AttributedString rightPrompt = AttributedString.EMPTY;
  
  protected MaskingCallback maskingCallback;
  
  protected Map<Integer, String> modifiedHistory = new HashMap<>();
  
  protected Buffer historyBuffer = null;
  
  protected CharSequence searchBuffer;
  
  protected StringBuffer searchTerm = null;
  
  protected boolean searchFailing;
  
  protected boolean searchBackward;
  
  protected int searchIndex = -1;
  
  protected boolean doAutosuggestion;
  
  protected final BindingReader bindingReader;
  
  protected int findChar;
  
  protected int findDir;
  
  protected int findTailAdd;
  
  private int searchDir;
  
  private String searchString;
  
  protected int regionMark;
  
  protected LineReader.RegionType regionActive;
  
  private boolean forceChar;
  
  private boolean forceLine;
  
  protected String yankBuffer = "";
  
  protected ViMoveMode viMoveMode = ViMoveMode.NORMAL;
  
  protected KillRing killRing = new KillRing();
  
  protected UndoTree<Buffer> undo = new UndoTree<>(this::setBuffer);
  
  protected boolean isUndo;
  
  protected final ReentrantLock lock = new ReentrantLock();
  
  protected State state = State.DONE;
  
  protected final AtomicBoolean startedReading = new AtomicBoolean();
  
  protected boolean reading;
  
  protected Supplier<AttributedString> post;
  
  protected Map<String, Widget> builtinWidgets;
  
  protected Map<String, Widget> widgets;
  
  protected int count;
  
  protected int mult;
  
  protected int universal = 4;
  
  protected int repeatCount;
  
  protected boolean isArgDigit;
  
  protected ParsedLine parsedLine;
  
  protected boolean skipRedisplay;
  
  protected Display display;
  
  protected boolean overTyping = false;
  
  protected String keyMap;
  
  protected int smallTerminalOffset = 0;
  
  protected boolean nextCommandFromHistory = false;
  
  protected int nextHistoryId = -1;
  
  protected List<String> commandsBuffer = new ArrayList<>();
  
  int candidateStartPosition = 0;
  
  private static final String DESC_PREFIX = "(";
  
  private static final String DESC_SUFFIX = ")";
  
  private static final int MARGIN_BETWEEN_DISPLAY_AND_DESC = 1;
  
  private static final int MARGIN_BETWEEN_COLUMNS = 3;
  
  private static final int MENU_LIST_WIDTH = 25;
  
  public LineReaderImpl(Terminal terminal) throws IOException {
    this(terminal, null, null);
  }
  
  public LineReaderImpl(Terminal terminal, String appName) throws IOException {
    this(terminal, appName, null);
  }
  
  public LineReaderImpl(Terminal terminal, String appName, Map<String, Object> variables) {
    Objects.requireNonNull(terminal, "terminal can not be null");
    this.terminal = terminal;
    if (appName == null)
      appName = "JLine"; 
    this.appName = appName;
    if (variables != null) {
      this.variables = variables;
    } else {
      this.variables = new HashMap<>();
    } 
    this.keyMaps = defaultKeyMaps();
    this.builtinWidgets = builtinWidgets();
    this.widgets = new HashMap<>(this.builtinWidgets);
    this.bindingReader = new BindingReader(terminal.reader());
    doDisplay();
  }
  
  public Terminal getTerminal() {
    return this.terminal;
  }
  
  public String getAppName() {
    return this.appName;
  }
  
  public Map<String, KeyMap<Binding>> getKeyMaps() {
    return this.keyMaps;
  }
  
  public KeyMap<Binding> getKeys() {
    return this.keyMaps.get(this.keyMap);
  }
  
  public Map<String, Widget> getWidgets() {
    return this.widgets;
  }
  
  public Map<String, Widget> getBuiltinWidgets() {
    return Collections.unmodifiableMap(this.builtinWidgets);
  }
  
  public Buffer getBuffer() {
    return this.buf;
  }
  
  public void setAutosuggestion(LineReader.SuggestionType type) {
    this.autosuggestion = type;
  }
  
  public LineReader.SuggestionType getAutosuggestion() {
    return this.autosuggestion;
  }
  
  public String getTailTip() {
    return this.tailTip;
  }
  
  public void setTailTip(String tailTip) {
    this.tailTip = tailTip;
  }
  
  public void runMacro(String macro) {
    this.bindingReader.runMacro(macro);
  }
  
  public MouseEvent readMouseEvent() {
    Objects.requireNonNull(this.bindingReader);
    return this.terminal.readMouseEvent(this.bindingReader::readCharacter);
  }
  
  public void setCompleter(Completer completer) {
    this.completer = completer;
  }
  
  public Completer getCompleter() {
    return this.completer;
  }
  
  public void setHistory(History history) {
    Objects.requireNonNull(history);
    this.history = history;
  }
  
  public History getHistory() {
    return this.history;
  }
  
  public void setHighlighter(Highlighter highlighter) {
    this.highlighter = highlighter;
  }
  
  public Highlighter getHighlighter() {
    return this.highlighter;
  }
  
  public Parser getParser() {
    return this.parser;
  }
  
  public void setParser(Parser parser) {
    this.parser = parser;
  }
  
  public Expander getExpander() {
    return this.expander;
  }
  
  public void setExpander(Expander expander) {
    this.expander = expander;
  }
  
  public void setCompletionMatcher(CompletionMatcher completionMatcher) {
    this.completionMatcher = completionMatcher;
  }
  
  public String readLine() throws UserInterruptException, EndOfFileException {
    return readLine((String)null, (String)null, (MaskingCallback)null, (String)null);
  }
  
  public String readLine(Character mask) throws UserInterruptException, EndOfFileException {
    return readLine((String)null, (String)null, mask, (String)null);
  }
  
  public String readLine(String prompt) throws UserInterruptException, EndOfFileException {
    return readLine(prompt, (String)null, (MaskingCallback)null, (String)null);
  }
  
  public String readLine(String prompt, Character mask) throws UserInterruptException, EndOfFileException {
    return readLine(prompt, (String)null, mask, (String)null);
  }
  
  public String readLine(String prompt, Character mask, String buffer) throws UserInterruptException, EndOfFileException {
    return readLine(prompt, (String)null, mask, buffer);
  }
  
  public String readLine(String prompt, String rightPrompt, Character mask, String buffer) throws UserInterruptException, EndOfFileException {
    return readLine(prompt, rightPrompt, (mask != null) ? new SimpleMaskingCallback(mask) : null, buffer);
  }
  
  public String readLine(String prompt, String rightPrompt, MaskingCallback maskingCallback, String buffer) throws UserInterruptException, EndOfFileException {
    if (!this.commandsBuffer.isEmpty()) {
      String cmd = this.commandsBuffer.remove(0);
      boolean done = false;
      while (true) {
        try {
          this.parser.parse(cmd, cmd.length() + 1, Parser.ParseContext.ACCEPT_LINE);
          done = true;
        } catch (EOFError e) {
          if (this.commandsBuffer.isEmpty())
            throw new IllegalArgumentException("Incompleted command: \n" + cmd); 
          cmd = cmd + "\n";
          cmd = cmd + (String)this.commandsBuffer.remove(0);
        } catch (SyntaxError e) {
          done = true;
        } catch (Exception e) {
          this.commandsBuffer.clear();
          throw new IllegalArgumentException(e.getMessage());
        } 
        if (done) {
          AttributedStringBuilder sb = new AttributedStringBuilder();
          sb.styled(AttributedStyle::bold, cmd);
          sb.toAttributedString().println(this.terminal);
          this.terminal.flush();
          return finish(cmd);
        } 
      } 
    } 
    if (!this.startedReading.compareAndSet(false, true))
      throw new IllegalStateException(); 
    Thread readLineThread = Thread.currentThread();
    Terminal.SignalHandler previousIntrHandler = null;
    Terminal.SignalHandler previousWinchHandler = null;
    Terminal.SignalHandler previousContHandler = null;
    Attributes originalAttributes = null;
    boolean dumb = isTerminalDumb();
    try {
      this.maskingCallback = maskingCallback;
      this.repeatCount = 0;
      this.mult = 1;
      this.regionActive = LineReader.RegionType.NONE;
      this.regionMark = -1;
      this.smallTerminalOffset = 0;
      this.state = State.NORMAL;
      this.modifiedHistory.clear();
      setPrompt(prompt);
      setRightPrompt(rightPrompt);
      this.buf.clear();
      if (buffer != null)
        this.buf.write(buffer); 
      if (this.nextCommandFromHistory && this.nextHistoryId > 0) {
        if (this.history.size() > this.nextHistoryId) {
          this.history.moveTo(this.nextHistoryId);
        } else {
          this.history.moveTo(this.history.last());
        } 
        this.buf.write(this.history.current());
      } else {
        this.nextHistoryId = -1;
      } 
      this.nextCommandFromHistory = false;
      this.undo.clear();
      this.parsedLine = null;
      this.keyMap = "main";
      if (this.history != null)
        this.history.attach(this); 
      try {
        this.lock.lock();
        this.reading = true;
        previousIntrHandler = this.terminal.handle(Terminal.Signal.INT, signal -> readLineThread.interrupt());
        previousWinchHandler = this.terminal.handle(Terminal.Signal.WINCH, this::handleSignal);
        previousContHandler = this.terminal.handle(Terminal.Signal.CONT, this::handleSignal);
        originalAttributes = this.terminal.enterRawMode();
        doDisplay();
        if (!dumb) {
          this.terminal.puts(InfoCmp.Capability.keypad_xmit, new Object[0]);
          if (isSet(LineReader.Option.AUTO_FRESH_LINE))
            callWidget("fresh-line"); 
          if (isSet(LineReader.Option.MOUSE))
            this.terminal.trackMouse(Terminal.MouseTracking.Normal); 
          if (isSet(LineReader.Option.BRACKETED_PASTE))
            this.terminal.writer().write("\033[?2004h"); 
        } else {
          Attributes attr = new Attributes(originalAttributes);
          attr.setInputFlag(Attributes.InputFlag.IGNCR, true);
          this.terminal.setAttributes(attr);
        } 
        callWidget("callback-init");
        this.undo.newState(this.buf.copy());
        redrawLine();
        redisplay();
      } finally {
        this.lock.unlock();
      } 
      while (true) {
        KeyMap<Binding> local = null;
        if (isInViCmdMode() && this.regionActive != LineReader.RegionType.NONE)
          local = this.keyMaps.get("visual"); 
        Binding o = readBinding(getKeys(), local);
        if (o == null)
          throw (new EndOfFileException()).partialLine((this.buf.length() > 0) ? this.buf.toString() : null); 
        Log.trace(new Object[] { "Binding: ", o });
        if (this.buf.length() == 0 && getLastBinding().charAt(0) == originalAttributes.getControlChar(Attributes.ControlChar.VEOF))
          throw new EndOfFileException(); 
        this.isArgDigit = false;
        this.count = ((this.repeatCount == 0) ? 1 : this.repeatCount) * this.mult;
        this.isUndo = false;
        if (this.regionActive == LineReader.RegionType.PASTE)
          this.regionActive = LineReader.RegionType.NONE; 
        try {
          String str;
          this.lock.lock();
          Buffer copy = (this.buf.length() <= getInt("features-max-buffer-size", 1000)) ? this.buf.copy() : null;
          Widget w = getWidget(o);
          if (!w.apply())
            beep(); 
          if (!this.isUndo && copy != null && this.buf.length() <= getInt("features-max-buffer-size", 1000) && 
            !copy.toString().equals(this.buf.toString()))
            this.undo.newState(this.buf.copy()); 
          switch (this.state) {
            case DONE:
              str = finishBuffer();
              return str;
            case IGNORE:
              str = "";
              return str;
            case EOF:
              throw new EndOfFileException();
            case INTERRUPT:
              throw new UserInterruptException(this.buf.toString());
          } 
          if (!this.isArgDigit) {
            this.repeatCount = 0;
            this.mult = 1;
          } 
          if (!dumb)
            redisplay(); 
        } finally {
          this.lock.unlock();
        } 
      } 
    } catch (IOError e) {
      if (e.getCause() instanceof java.io.InterruptedIOException)
        throw new UserInterruptException(this.buf.toString()); 
      throw e;
    } finally {
      try {
        this.lock.lock();
        this.reading = false;
        cleanup();
        if (originalAttributes != null)
          this.terminal.setAttributes(originalAttributes); 
        if (previousIntrHandler != null)
          this.terminal.handle(Terminal.Signal.INT, previousIntrHandler); 
        if (previousWinchHandler != null)
          this.terminal.handle(Terminal.Signal.WINCH, previousWinchHandler); 
        if (previousContHandler != null)
          this.terminal.handle(Terminal.Signal.CONT, previousContHandler); 
      } finally {
        this.lock.unlock();
      } 
      this.startedReading.set(false);
    } 
  }
  
  private boolean isTerminalDumb() {
    return ("dumb".equals(this.terminal.getType()) || "dumb-color"
      .equals(this.terminal.getType()));
  }
  
  private void doDisplay() {
    this.size.copy(this.terminal.getBufferSize());
    this.display = new Display(this.terminal, false);
    this.display.resize(this.size.getRows(), this.size.getColumns());
    if (isSet(LineReader.Option.DELAY_LINE_WRAP))
      this.display.setDelayLineWrap(true); 
  }
  
  public void printAbove(String str) {
    try {
      this.lock.lock();
      boolean reading = this.reading;
      if (reading)
        this.display.update(Collections.emptyList(), 0); 
      if (str.endsWith("\n") || str.endsWith("\n\033[m") || str.endsWith("\n\033[0m")) {
        this.terminal.writer().print(str);
      } else {
        this.terminal.writer().println(str);
      } 
      if (reading)
        redisplay(false); 
      this.terminal.flush();
    } finally {
      this.lock.unlock();
    } 
  }
  
  public void printAbove(AttributedString str) {
    printAbove(str.toAnsi(this.terminal));
  }
  
  public boolean isReading() {
    try {
      this.lock.lock();
      return this.reading;
    } finally {
      this.lock.unlock();
    } 
  }
  
  protected boolean freshLine() {
    boolean wrapAtEol = this.terminal.getBooleanCapability(InfoCmp.Capability.auto_right_margin);
    boolean delayedWrapAtEol = (wrapAtEol && this.terminal.getBooleanCapability(InfoCmp.Capability.eat_newline_glitch));
    AttributedStringBuilder sb = new AttributedStringBuilder();
    sb.style(AttributedStyle.DEFAULT.foreground(8));
    sb.append("~");
    sb.style(AttributedStyle.DEFAULT);
    if (!wrapAtEol || delayedWrapAtEol) {
      for (int i = 0; i < this.size.getColumns() - 1; i++)
        sb.append(" "); 
      sb.append(KeyMap.key(this.terminal, InfoCmp.Capability.carriage_return));
      sb.append(" ");
      sb.append(KeyMap.key(this.terminal, InfoCmp.Capability.carriage_return));
    } else {
      String el = this.terminal.getStringCapability(InfoCmp.Capability.clr_eol);
      if (el != null)
        Curses.tputs((Appendable)sb, el, new Object[0]); 
      for (int i = 0; i < this.size.getColumns() - 2; i++)
        sb.append(" "); 
      sb.append(KeyMap.key(this.terminal, InfoCmp.Capability.carriage_return));
      sb.append(" ");
      sb.append(KeyMap.key(this.terminal, InfoCmp.Capability.carriage_return));
    } 
    sb.print(this.terminal);
    return true;
  }
  
  public void callWidget(String name) {
    try {
      this.lock.lock();
      if (!this.reading)
        throw new IllegalStateException("Widgets can only be called during a `readLine` call"); 
      try {
        Widget w;
        if (name.startsWith(".")) {
          w = this.builtinWidgets.get(name.substring(1));
        } else {
          w = this.widgets.get(name);
        } 
        if (w != null)
          w.apply(); 
      } catch (Throwable t) {
        Log.debug(new Object[] { "Error executing widget '", name, "'", t });
      } 
    } finally {
      this.lock.unlock();
    } 
  }
  
  public boolean redrawLine() {
    this.display.reset();
    return true;
  }
  
  public void putString(CharSequence str) {
    this.buf.write(str, this.overTyping);
  }
  
  public void flush() {
    this.terminal.flush();
  }
  
  public boolean isKeyMap(String name) {
    return this.keyMap.equals(name);
  }
  
  public int readCharacter() {
    if (this.lock.isHeldByCurrentThread())
      try {
        this.lock.unlock();
        return this.bindingReader.readCharacter();
      } finally {
        this.lock.lock();
      }  
    return this.bindingReader.readCharacter();
  }
  
  public int peekCharacter(long timeout) {
    return this.bindingReader.peekCharacter(timeout);
  }
  
  protected <T> T doReadBinding(KeyMap<T> keys, KeyMap<T> local) {
    if (this.lock.isHeldByCurrentThread())
      try {
        this.lock.unlock();
        return (T)this.bindingReader.readBinding(keys, local);
      } finally {
        this.lock.lock();
      }  
    return (T)this.bindingReader.readBinding(keys, local);
  }
  
  protected String doReadStringUntil(String sequence) {
    if (this.lock.isHeldByCurrentThread())
      try {
        this.lock.unlock();
        return this.bindingReader.readStringUntil(sequence);
      } finally {
        this.lock.lock();
      }  
    return this.bindingReader.readStringUntil(sequence);
  }
  
  public Binding readBinding(KeyMap<Binding> keys) {
    return readBinding(keys, null);
  }
  
  public Binding readBinding(KeyMap<Binding> keys, KeyMap<Binding> local) {
    Binding o = doReadBinding(keys, local);
    if (o instanceof Reference) {
      String ref = ((Reference)o).name();
      if (!"yank-pop".equals(ref) && !"yank".equals(ref))
        this.killRing.resetLastYank(); 
      if (!"kill-line".equals(ref) && !"kill-whole-line".equals(ref) && 
        !"backward-kill-word".equals(ref) && !"kill-word".equals(ref))
        this.killRing.resetLastKill(); 
    } 
    return o;
  }
  
  public ParsedLine getParsedLine() {
    return this.parsedLine;
  }
  
  public String getLastBinding() {
    return this.bindingReader.getLastBinding();
  }
  
  public String getSearchTerm() {
    return (this.searchTerm != null) ? this.searchTerm.toString() : null;
  }
  
  public LineReader.RegionType getRegionActive() {
    return this.regionActive;
  }
  
  public int getRegionMark() {
    return this.regionMark;
  }
  
  public boolean setKeyMap(String name) {
    KeyMap<Binding> map = this.keyMaps.get(name);
    if (map == null)
      return false; 
    this.keyMap = name;
    if (this.reading)
      callWidget("callback-keymap"); 
    return true;
  }
  
  public String getKeyMap() {
    return this.keyMap;
  }
  
  public LineReader variable(String name, Object value) {
    this.variables.put(name, value);
    return this;
  }
  
  public Map<String, Object> getVariables() {
    return this.variables;
  }
  
  public Object getVariable(String name) {
    return this.variables.get(name);
  }
  
  public void setVariable(String name, Object value) {
    this.variables.put(name, value);
  }
  
  public LineReader option(LineReader.Option option, boolean value) {
    this.options.put(option, Boolean.valueOf(value));
    return this;
  }
  
  public boolean isSet(LineReader.Option option) {
    return option.isSet(this.options);
  }
  
  public void setOpt(LineReader.Option option) {
    this.options.put(option, Boolean.TRUE);
  }
  
  public void unsetOpt(LineReader.Option option) {
    this.options.put(option, Boolean.FALSE);
  }
  
  public void addCommandsInBuffer(Collection<String> commands) {
    this.commandsBuffer.addAll(commands);
  }
  
  public void editAndAddInBuffer(File file) throws Exception {
    if (isSet(LineReader.Option.BRACKETED_PASTE))
      this.terminal.writer().write("\033[?2004l"); 
    Constructor<?> ctor = Class.forName("org.jline.builtins.Nano").getConstructor(new Class[] { Terminal.class, File.class });
    Editor editor = (Editor)ctor.newInstance(new Object[] { this.terminal, new File(file.getParent()) });
    editor.setRestricted(true);
    editor.open(Collections.singletonList(file.getName()));
    editor.run();
    BufferedReader br = new BufferedReader(new FileReader(file));
    this.commandsBuffer.clear();
    String line;
    while ((line = br.readLine()) != null)
      this.commandsBuffer.add(line); 
    br.close();
  }
  
  protected String finishBuffer() {
    return finish(this.buf.toString());
  }
  
  protected String finish(String str) {
    String historyLine = str;
    if (!isSet(LineReader.Option.DISABLE_EVENT_EXPANSION)) {
      StringBuilder sb = new StringBuilder();
      boolean escaped = false;
      for (int i = 0; i < str.length(); i++) {
        char ch = str.charAt(i);
        if (escaped) {
          escaped = false;
          if (ch != '\n')
            sb.append(ch); 
        } else if (this.parser.isEscapeChar(ch)) {
          escaped = true;
        } else {
          sb.append(ch);
        } 
      } 
      str = sb.toString();
    } 
    if (this.maskingCallback != null)
      historyLine = this.maskingCallback.history(historyLine); 
    if (historyLine != null && historyLine.length() > 0)
      this.history.add(Instant.now(), historyLine); 
    return str;
  }
  
  protected void handleSignal(Terminal.Signal signal) {
    this.doAutosuggestion = false;
    if (signal == Terminal.Signal.WINCH) {
      Status status = Status.getStatus(this.terminal, false);
      if (status != null)
        status.hardReset(); 
      this.size.copy(this.terminal.getBufferSize());
      this.display.resize(this.size.getRows(), this.size.getColumns());
      redisplay();
    } else if (signal == Terminal.Signal.CONT) {
      this.terminal.enterRawMode();
      this.size.copy(this.terminal.getBufferSize());
      this.display.resize(this.size.getRows(), this.size.getColumns());
      this.terminal.puts(InfoCmp.Capability.keypad_xmit, new Object[0]);
      redrawLine();
      redisplay();
    } 
  }
  
  protected Widget getWidget(Object binding) {
    Widget w;
    if (binding instanceof Widget) {
      w = (Widget)binding;
    } else if (binding instanceof Macro) {
      String macro = ((Macro)binding).getSequence();
      w = (() -> {
          this.bindingReader.runMacro(macro);
          return true;
        });
    } else if (binding instanceof Reference) {
      String name = ((Reference)binding).name();
      w = this.widgets.get(name);
      if (w == null)
        w = (() -> {
            this.post = (());
            return false;
          }); 
    } else {
      w = (() -> {
          this.post = (());
          return false;
        });
    } 
    return w;
  }
  
  public void setPrompt(String prompt) {
    this
      .prompt = (prompt == null) ? AttributedString.EMPTY : expandPromptPattern(prompt, 0, "", 0);
  }
  
  public void setRightPrompt(String rightPrompt) {
    this
      .rightPrompt = (rightPrompt == null) ? AttributedString.EMPTY : expandPromptPattern(rightPrompt, 0, "", 0);
  }
  
  protected void setBuffer(Buffer buffer) {
    this.buf.copyFrom(buffer);
  }
  
  protected void setBuffer(String buffer) {
    this.buf.clear();
    this.buf.write(buffer);
  }
  
  protected String viDeleteChangeYankToRemap(String op) {
    switch (op) {
      case "abort":
      case "backward-char":
      case "forward-char":
      case "end-of-line":
      case "vi-match-bracket":
      case "vi-digit-or-beginning-of-line":
      case "neg-argument":
      case "digit-argument":
      case "vi-backward-char":
      case "vi-backward-word":
      case "vi-forward-char":
      case "vi-forward-word":
      case "vi-forward-word-end":
      case "vi-first-non-blank":
      case "vi-goto-column":
      case "vi-delete":
      case "vi-yank":
      case "vi-change-to":
      case "vi-find-next-char":
      case "vi-find-next-char-skip":
      case "vi-find-prev-char":
      case "vi-find-prev-char-skip":
      case "vi-repeat-find":
      case "vi-rev-repeat-find":
        return op;
    } 
    return "vi-cmd-mode";
  }
  
  protected int switchCase(int ch) {
    if (Character.isUpperCase(ch))
      return Character.toLowerCase(ch); 
    if (Character.isLowerCase(ch))
      return Character.toUpperCase(ch); 
    return ch;
  }
  
  protected boolean isInViMoveOperation() {
    return (this.viMoveMode != ViMoveMode.NORMAL);
  }
  
  protected boolean isInViChangeOperation() {
    return (this.viMoveMode == ViMoveMode.CHANGE);
  }
  
  protected boolean isInViCmdMode() {
    return "vicmd".equals(this.keyMap);
  }
  
  protected boolean viForwardChar() {
    if (this.count < 0)
      return callNeg(this::viBackwardChar); 
    int lim = findeol();
    if (isInViCmdMode() && !isInViMoveOperation())
      lim--; 
    if (this.buf.cursor() >= lim)
      return false; 
    while (this.count-- > 0 && this.buf.cursor() < lim)
      this.buf.move(1); 
    return true;
  }
  
  protected boolean viBackwardChar() {
    if (this.count < 0)
      return callNeg(this::viForwardChar); 
    int lim = findbol();
    if (this.buf.cursor() == lim)
      return false; 
    while (this.count-- > 0 && this.buf.cursor() > 0) {
      this.buf.move(-1);
      if (this.buf.currChar() == 10) {
        this.buf.move(1);
        break;
      } 
    } 
    return true;
  }
  
  protected boolean forwardWord() {
    if (this.count < 0)
      return callNeg(this::backwardWord); 
    while (this.count-- > 0) {
      while (this.buf.cursor() < this.buf.length() && isWord(this.buf.currChar()))
        this.buf.move(1); 
      if (isInViChangeOperation() && this.count == 0)
        break; 
      while (this.buf.cursor() < this.buf.length() && !isWord(this.buf.currChar()))
        this.buf.move(1); 
    } 
    return true;
  }
  
  protected boolean viForwardWord() {
    if (this.count < 0)
      return callNeg(this::viBackwardWord); 
    while (this.count-- > 0) {
      if (isViAlphaNum(this.buf.currChar())) {
        while (this.buf.cursor() < this.buf.length() && isViAlphaNum(this.buf.currChar()))
          this.buf.move(1); 
      } else {
        while (this.buf.cursor() < this.buf.length() && 
          !isViAlphaNum(this.buf.currChar()) && 
          !isWhitespace(this.buf.currChar()))
          this.buf.move(1); 
      } 
      if (isInViChangeOperation() && this.count == 0)
        return true; 
      int nl = (this.buf.currChar() == 10) ? 1 : 0;
      while (this.buf.cursor() < this.buf.length() && nl < 2 && 
        
        isWhitespace(this.buf.currChar())) {
        this.buf.move(1);
        nl += (this.buf.currChar() == 10) ? 1 : 0;
      } 
    } 
    return true;
  }
  
  protected boolean viForwardBlankWord() {
    if (this.count < 0)
      return callNeg(this::viBackwardBlankWord); 
    while (this.count-- > 0) {
      while (this.buf.cursor() < this.buf.length() && !isWhitespace(this.buf.currChar()))
        this.buf.move(1); 
      if (isInViChangeOperation() && this.count == 0)
        return true; 
      int nl = (this.buf.currChar() == 10) ? 1 : 0;
      while (this.buf.cursor() < this.buf.length() && nl < 2 && 
        
        isWhitespace(this.buf.currChar())) {
        this.buf.move(1);
        nl += (this.buf.currChar() == 10) ? 1 : 0;
      } 
    } 
    return true;
  }
  
  protected boolean emacsForwardWord() {
    return forwardWord();
  }
  
  protected boolean viForwardBlankWordEnd() {
    if (this.count < 0)
      return false; 
    while (this.count-- > 0) {
      while (this.buf.cursor() < this.buf.length()) {
        this.buf.move(1);
        if (!isWhitespace(this.buf.currChar()))
          break; 
      } 
      while (this.buf.cursor() < this.buf.length()) {
        this.buf.move(1);
        if (isWhitespace(this.buf.currChar()))
          break; 
      } 
    } 
    return true;
  }
  
  protected boolean viForwardWordEnd() {
    if (this.count < 0)
      return callNeg(this::backwardWord); 
    while (this.count-- > 0) {
      while (this.buf.cursor() < this.buf.length() && 
        isWhitespace(this.buf.nextChar()))
        this.buf.move(1); 
      if (this.buf.cursor() < this.buf.length()) {
        if (isViAlphaNum(this.buf.nextChar())) {
          this.buf.move(1);
          while (this.buf.cursor() < this.buf.length() && isViAlphaNum(this.buf.nextChar()))
            this.buf.move(1); 
          continue;
        } 
        this.buf.move(1);
        while (this.buf.cursor() < this.buf.length() && !isViAlphaNum(this.buf.nextChar()) && !isWhitespace(this.buf.nextChar()))
          this.buf.move(1); 
      } 
    } 
    if (this.buf.cursor() < this.buf.length() && isInViMoveOperation())
      this.buf.move(1); 
    return true;
  }
  
  protected boolean backwardWord() {
    if (this.count < 0)
      return callNeg(this::forwardWord); 
    while (this.count-- > 0) {
      while (this.buf.cursor() > 0 && !isWord(this.buf.atChar(this.buf.cursor() - 1)))
        this.buf.move(-1); 
      while (this.buf.cursor() > 0 && isWord(this.buf.atChar(this.buf.cursor() - 1)))
        this.buf.move(-1); 
    } 
    return true;
  }
  
  protected boolean viBackwardWord() {
    if (this.count < 0)
      return callNeg(this::viForwardWord); 
    while (this.count-- > 0) {
      int nl = 0;
      while (this.buf.cursor() > 0) {
        this.buf.move(-1);
        if (!isWhitespace(this.buf.currChar()))
          break; 
        nl += (this.buf.currChar() == 10) ? 1 : 0;
        if (nl == 2) {
          this.buf.move(1);
          break;
        } 
      } 
      if (this.buf.cursor() > 0) {
        if (isViAlphaNum(this.buf.currChar())) {
          while (this.buf.cursor() > 0 && 
            isViAlphaNum(this.buf.prevChar()))
            this.buf.move(-1); 
          continue;
        } 
        while (this.buf.cursor() > 0 && 
          !isViAlphaNum(this.buf.prevChar()) && !isWhitespace(this.buf.prevChar()))
          this.buf.move(-1); 
      } 
    } 
    return true;
  }
  
  protected boolean viBackwardBlankWord() {
    if (this.count < 0)
      return callNeg(this::viForwardBlankWord); 
    while (this.count-- > 0) {
      while (this.buf.cursor() > 0) {
        this.buf.move(-1);
        if (!isWhitespace(this.buf.currChar()))
          break; 
      } 
      while (this.buf.cursor() > 0) {
        this.buf.move(-1);
        if (isWhitespace(this.buf.currChar()))
          break; 
      } 
    } 
    return true;
  }
  
  protected boolean viBackwardWordEnd() {
    if (this.count < 0)
      return callNeg(this::viForwardWordEnd); 
    while (this.count-- > 0 && this.buf.cursor() > 1) {
      int start;
      if (isViAlphaNum(this.buf.currChar())) {
        start = 1;
      } else if (!isWhitespace(this.buf.currChar())) {
        start = 2;
      } else {
        start = 0;
      } 
      while (this.buf.cursor() > 0) {
        boolean same = (start != 1 && isWhitespace(this.buf.currChar()));
        if (start != 0)
          same |= isViAlphaNum(this.buf.currChar()); 
        if (same == ((start == 2)))
          break; 
        this.buf.move(-1);
      } 
      while (this.buf.cursor() > 0 && isWhitespace(this.buf.currChar()))
        this.buf.move(-1); 
    } 
    return true;
  }
  
  protected boolean viBackwardBlankWordEnd() {
    if (this.count < 0)
      return callNeg(this::viForwardBlankWordEnd); 
    while (this.count-- > 0) {
      while (this.buf.cursor() > 0 && !isWhitespace(this.buf.currChar()))
        this.buf.move(-1); 
      while (this.buf.cursor() > 0 && isWhitespace(this.buf.currChar()))
        this.buf.move(-1); 
    } 
    return true;
  }
  
  protected boolean emacsBackwardWord() {
    return backwardWord();
  }
  
  protected boolean backwardDeleteWord() {
    if (this.count < 0)
      return callNeg(this::deleteWord); 
    int cursor = this.buf.cursor();
    while (this.count-- > 0) {
      while (cursor > 0 && !isWord(this.buf.atChar(cursor - 1)))
        cursor--; 
      while (cursor > 0 && isWord(this.buf.atChar(cursor - 1)))
        cursor--; 
    } 
    this.buf.backspace(this.buf.cursor() - cursor);
    return true;
  }
  
  protected boolean viBackwardKillWord() {
    if (this.count < 0)
      return false; 
    int lim = findbol();
    int x = this.buf.cursor();
    while (this.count-- > 0) {
      while (x > lim && isWhitespace(this.buf.atChar(x - 1)))
        x--; 
      if (x > lim) {
        if (isViAlphaNum(this.buf.atChar(x - 1))) {
          while (x > lim && isViAlphaNum(this.buf.atChar(x - 1)))
            x--; 
          continue;
        } 
        while (x > lim && !isViAlphaNum(this.buf.atChar(x - 1)) && !isWhitespace(this.buf.atChar(x - 1)))
          x--; 
      } 
    } 
    this.killRing.addBackwards(this.buf.substring(x, this.buf.cursor()));
    this.buf.backspace(this.buf.cursor() - x);
    return true;
  }
  
  protected boolean backwardKillWord() {
    if (this.count < 0)
      return callNeg(this::killWord); 
    int x = this.buf.cursor();
    while (this.count-- > 0) {
      while (x > 0 && !isWord(this.buf.atChar(x - 1)))
        x--; 
      while (x > 0 && isWord(this.buf.atChar(x - 1)))
        x--; 
    } 
    this.killRing.addBackwards(this.buf.substring(x, this.buf.cursor()));
    this.buf.backspace(this.buf.cursor() - x);
    return true;
  }
  
  protected boolean copyPrevWord() {
    int t1;
    if (this.count <= 0)
      return false; 
    int t0 = this.buf.cursor();
    while (true) {
      t1 = t0;
      while (t0 > 0 && !isWord(this.buf.atChar(t0 - 1)))
        t0--; 
      while (t0 > 0 && isWord(this.buf.atChar(t0 - 1)))
        t0--; 
      if (--this.count == 0)
        break; 
      if (t0 == 0)
        return false; 
    } 
    this.buf.write(this.buf.substring(t0, t1));
    return true;
  }
  
  protected boolean upCaseWord() {
    int count = Math.abs(this.count);
    int cursor = this.buf.cursor();
    while (count-- > 0) {
      while (this.buf.cursor() < this.buf.length() && !isWord(this.buf.currChar()))
        this.buf.move(1); 
      while (this.buf.cursor() < this.buf.length() && isWord(this.buf.currChar())) {
        this.buf.currChar(Character.toUpperCase(this.buf.currChar()));
        this.buf.move(1);
      } 
    } 
    if (this.count < 0)
      this.buf.cursor(cursor); 
    return true;
  }
  
  protected boolean downCaseWord() {
    int count = Math.abs(this.count);
    int cursor = this.buf.cursor();
    while (count-- > 0) {
      while (this.buf.cursor() < this.buf.length() && !isWord(this.buf.currChar()))
        this.buf.move(1); 
      while (this.buf.cursor() < this.buf.length() && isWord(this.buf.currChar())) {
        this.buf.currChar(Character.toLowerCase(this.buf.currChar()));
        this.buf.move(1);
      } 
    } 
    if (this.count < 0)
      this.buf.cursor(cursor); 
    return true;
  }
  
  protected boolean capitalizeWord() {
    int count = Math.abs(this.count);
    int cursor = this.buf.cursor();
    while (count-- > 0) {
      boolean first = true;
      while (this.buf.cursor() < this.buf.length() && !isWord(this.buf.currChar()))
        this.buf.move(1); 
      while (this.buf.cursor() < this.buf.length() && isWord(this.buf.currChar()) && !isAlpha(this.buf.currChar()))
        this.buf.move(1); 
      while (this.buf.cursor() < this.buf.length() && isWord(this.buf.currChar())) {
        this.buf.currChar(first ? 
            Character.toUpperCase(this.buf.currChar()) : 
            Character.toLowerCase(this.buf.currChar()));
        this.buf.move(1);
        first = false;
      } 
    } 
    if (this.count < 0)
      this.buf.cursor(cursor); 
    return true;
  }
  
  protected boolean deleteWord() {
    if (this.count < 0)
      return callNeg(this::backwardDeleteWord); 
    int x = this.buf.cursor();
    while (this.count-- > 0) {
      while (x < this.buf.length() && !isWord(this.buf.atChar(x)))
        x++; 
      while (x < this.buf.length() && isWord(this.buf.atChar(x)))
        x++; 
    } 
    this.buf.delete(x - this.buf.cursor());
    return true;
  }
  
  protected boolean killWord() {
    if (this.count < 0)
      return callNeg(this::backwardKillWord); 
    int x = this.buf.cursor();
    while (this.count-- > 0) {
      while (x < this.buf.length() && !isWord(this.buf.atChar(x)))
        x++; 
      while (x < this.buf.length() && isWord(this.buf.atChar(x)))
        x++; 
    } 
    this.killRing.add(this.buf.substring(this.buf.cursor(), x));
    this.buf.delete(x - this.buf.cursor());
    return true;
  }
  
  protected boolean transposeWords() {
    int lstart = this.buf.cursor() - 1;
    int lend = this.buf.cursor();
    while (this.buf.atChar(lstart) != 0 && this.buf.atChar(lstart) != 10)
      lstart--; 
    lstart++;
    while (this.buf.atChar(lend) != 0 && this.buf.atChar(lend) != 10)
      lend++; 
    if (lend - lstart < 2)
      return false; 
    int words = 0;
    boolean inWord = false;
    if (!isDelimiter(this.buf.atChar(lstart))) {
      words++;
      inWord = true;
    } 
    for (int i = lstart; i < lend; i++) {
      if (isDelimiter(this.buf.atChar(i))) {
        inWord = false;
      } else {
        if (!inWord)
          words++; 
        inWord = true;
      } 
    } 
    if (words < 2)
      return false; 
    boolean neg = (this.count < 0);
    for (int count = Math.max(this.count, -this.count); count > 0; count--) {
      int sta2, end2, sta1 = this.buf.cursor();
      while (sta1 > lstart && !isDelimiter(this.buf.atChar(sta1 - 1)))
        sta1--; 
      int end1 = sta1;
      while (end1 < lend && !isDelimiter(this.buf.atChar(++end1)));
      if (neg) {
        end2 = sta1 - 1;
        while (end2 > lstart && isDelimiter(this.buf.atChar(end2 - 1)))
          end2--; 
        if (end2 < lstart) {
          sta2 = end1;
          while (isDelimiter(this.buf.atChar(++sta2)));
          end2 = sta2;
          while (end2 < lend && !isDelimiter(this.buf.atChar(++end2)));
        } else {
          sta2 = end2;
          while (sta2 > lstart && !isDelimiter(this.buf.atChar(sta2 - 1)))
            sta2--; 
        } 
      } else {
        sta2 = end1;
        while (sta2 < lend && isDelimiter(this.buf.atChar(++sta2)));
        if (sta2 == lend) {
          end2 = sta1;
          while (isDelimiter(this.buf.atChar(end2 - 1)))
            end2--; 
          sta2 = end2;
          while (sta2 > lstart && !isDelimiter(this.buf.atChar(sta2 - 1)))
            sta2--; 
        } else {
          end2 = sta2;
          while (end2 < lend && !isDelimiter(this.buf.atChar(++end2)));
        } 
      } 
      if (sta1 < sta2) {
        String res = this.buf.substring(0, sta1) + this.buf.substring(sta2, end2) + this.buf.substring(end1, sta2) + this.buf.substring(sta1, end1) + this.buf.substring(end2);
        this.buf.clear();
        this.buf.write(res);
        this.buf.cursor(neg ? end1 : end2);
      } else {
        String res = this.buf.substring(0, sta2) + this.buf.substring(sta1, end1) + this.buf.substring(end2, sta1) + this.buf.substring(sta2, end2) + this.buf.substring(end1);
        this.buf.clear();
        this.buf.write(res);
        this.buf.cursor(neg ? end2 : end1);
      } 
    } 
    return true;
  }
  
  private int findbol() {
    int x = this.buf.cursor();
    while (x > 0 && this.buf.atChar(x - 1) != 10)
      x--; 
    return x;
  }
  
  private int findeol() {
    int x = this.buf.cursor();
    while (x < this.buf.length() && this.buf.atChar(x) != 10)
      x++; 
    return x;
  }
  
  protected boolean insertComment() {
    return doInsertComment(false);
  }
  
  protected boolean viInsertComment() {
    return doInsertComment(true);
  }
  
  protected boolean doInsertComment(boolean isViMode) {
    String comment = getString("comment-begin", "#");
    beginningOfLine();
    putString(comment);
    if (isViMode)
      setKeyMap("viins"); 
    return acceptLine();
  }
  
  protected boolean viFindNextChar() {
    if ((this.findChar = vigetkey()) > 0) {
      this.findDir = 1;
      this.findTailAdd = 0;
      return vifindchar(false);
    } 
    return false;
  }
  
  protected boolean viFindPrevChar() {
    if ((this.findChar = vigetkey()) > 0) {
      this.findDir = -1;
      this.findTailAdd = 0;
      return vifindchar(false);
    } 
    return false;
  }
  
  protected boolean viFindNextCharSkip() {
    if ((this.findChar = vigetkey()) > 0) {
      this.findDir = 1;
      this.findTailAdd = -1;
      return vifindchar(false);
    } 
    return false;
  }
  
  protected boolean viFindPrevCharSkip() {
    if ((this.findChar = vigetkey()) > 0) {
      this.findDir = -1;
      this.findTailAdd = 1;
      return vifindchar(false);
    } 
    return false;
  }
  
  protected boolean viRepeatFind() {
    return vifindchar(true);
  }
  
  protected boolean viRevRepeatFind() {
    if (this.count < 0)
      return callNeg(() -> vifindchar(true)); 
    this.findTailAdd = -this.findTailAdd;
    this.findDir = -this.findDir;
    boolean ret = vifindchar(true);
    this.findTailAdd = -this.findTailAdd;
    this.findDir = -this.findDir;
    return ret;
  }
  
  private int vigetkey() {
    int ch = readCharacter();
    KeyMap<Binding> km = this.keyMaps.get("main");
    if (km != null) {
      Binding b = (Binding)km.getBound(new String(Character.toChars(ch)));
      if (b instanceof Reference) {
        String func = ((Reference)b).name();
        if ("abort".equals(func))
          return -1; 
      } 
    } 
    return ch;
  }
  
  private boolean vifindchar(boolean repeat) {
    if (this.findDir == 0)
      return false; 
    if (this.count < 0)
      return callNeg(this::viRevRepeatFind); 
    if (repeat && this.findTailAdd != 0)
      if (this.findDir > 0) {
        if (this.buf.cursor() < this.buf.length() && this.buf.nextChar() == this.findChar)
          this.buf.move(1); 
      } else if (this.buf.cursor() > 0 && this.buf.prevChar() == this.findChar) {
        this.buf.move(-1);
      }  
    int cursor = this.buf.cursor();
    while (this.count-- > 0) {
      do {
        this.buf.move(this.findDir);
      } while (this.buf.cursor() > 0 && this.buf.cursor() < this.buf.length() && this.buf
        .currChar() != this.findChar && this.buf
        .currChar() != 10);
      if (this.buf.cursor() <= 0 || this.buf.cursor() >= this.buf.length() || this.buf
        .currChar() == 10) {
        this.buf.cursor(cursor);
        return false;
      } 
    } 
    if (this.findTailAdd != 0)
      this.buf.move(this.findTailAdd); 
    if (this.findDir == 1 && isInViMoveOperation())
      this.buf.move(1); 
    return true;
  }
  
  private boolean callNeg(Widget widget) {
    this.count = -this.count;
    boolean ret = widget.apply();
    this.count = -this.count;
    return ret;
  }
  
  protected boolean viHistorySearchForward() {
    this.searchDir = 1;
    this.searchIndex = 0;
    return (getViSearchString() && viRepeatSearch());
  }
  
  protected boolean viHistorySearchBackward() {
    this.searchDir = -1;
    this.searchIndex = this.history.size() - 1;
    return (getViSearchString() && viRepeatSearch());
  }
  
  protected boolean viRepeatSearch() {
    if (this.searchDir == 0)
      return false; 
    int si = (this.searchDir < 0) ? searchBackwards(this.searchString, this.searchIndex, false) : searchForwards(this.searchString, this.searchIndex, false);
    if (si == -1 || si == this.history.index())
      return false; 
    this.searchIndex = si;
    this.buf.clear();
    this.history.moveTo(this.searchIndex);
    this.buf.write(this.history.get(this.searchIndex));
    if ("vicmd".equals(this.keyMap))
      this.buf.move(-1); 
    return true;
  }
  
  protected boolean viRevRepeatSearch() {
    this.searchDir = -this.searchDir;
    boolean ret = viRepeatSearch();
    this.searchDir = -this.searchDir;
    return ret;
  }
  
  private boolean getViSearchString() {
    if (this.searchDir == 0)
      return false; 
    String searchPrompt = (this.searchDir < 0) ? "?" : "/";
    Buffer searchBuffer = new BufferImpl();
    KeyMap<Binding> keyMap = this.keyMaps.get("main");
    if (keyMap == null)
      keyMap = this.keyMaps.get(".safe"); 
    while (true) {
      this.post = (() -> new AttributedString(searchPrompt + searchBuffer.toString() + "_"));
      redisplay();
      Binding b = doReadBinding(keyMap, null);
      if (b instanceof Reference) {
        int c;
        String func = ((Reference)b).name();
        switch (func) {
          case "abort":
            this.post = null;
            return false;
          case "accept-line":
          case "vi-cmd-mode":
            this.searchString = searchBuffer.toString();
            this.post = null;
            return true;
          case "magic-space":
            searchBuffer.write(32);
            continue;
          case "redisplay":
            redisplay();
            continue;
          case "clear-screen":
            clearScreen();
            continue;
          case "self-insert":
            searchBuffer.write(getLastBinding());
            continue;
          case "self-insert-unmeta":
            if (getLastBinding().charAt(0) == '\033') {
              String s = getLastBinding().substring(1);
              if ("\r".equals(s))
                s = "\n"; 
              searchBuffer.write(s);
            } 
            continue;
          case "backward-delete-char":
          case "vi-backward-delete-char":
            if (searchBuffer.length() > 0)
              searchBuffer.backspace(); 
            continue;
          case "backward-kill-word":
          case "vi-backward-kill-word":
            if (searchBuffer.length() > 0 && !isWhitespace(searchBuffer.prevChar()))
              searchBuffer.backspace(); 
            if (searchBuffer.length() > 0 && isWhitespace(searchBuffer.prevChar()))
              searchBuffer.backspace(); 
            continue;
          case "quoted-insert":
          case "vi-quoted-insert":
            c = readCharacter();
            if (c >= 0) {
              searchBuffer.write(c);
              continue;
            } 
            beep();
            continue;
        } 
        beep();
      } 
    } 
  }
  
  protected boolean insertCloseCurly() {
    return insertClose("}");
  }
  
  protected boolean insertCloseParen() {
    return insertClose(")");
  }
  
  protected boolean insertCloseSquare() {
    return insertClose("]");
  }
  
  protected boolean insertClose(String s) {
    putString(s);
    long blink = getLong("blink-matching-paren", 500L);
    if (blink <= 0L) {
      removeIndentation();
      return true;
    } 
    int closePosition = this.buf.cursor();
    this.buf.move(-1);
    doViMatchBracket();
    redisplay();
    peekCharacter(blink);
    int blinkPosition = this.buf.cursor();
    this.buf.cursor(closePosition);
    if (blinkPosition != closePosition - 1)
      removeIndentation(); 
    return true;
  }
  
  private void removeIndentation() {
    int indent = getInt("indentation", 0);
    if (indent > 0) {
      this.buf.move(-1);
      for (int i = 0; i < indent; i++) {
        this.buf.move(-1);
        if (this.buf.currChar() == 32) {
          this.buf.delete();
        } else {
          this.buf.move(1);
          break;
        } 
      } 
      this.buf.move(1);
    } 
  }
  
  protected boolean viMatchBracket() {
    return doViMatchBracket();
  }
  
  protected boolean undefinedKey() {
    return false;
  }
  
  protected boolean doViMatchBracket() {
    int pos = this.buf.cursor();
    if (pos == this.buf.length())
      return false; 
    int type = getBracketType(this.buf.atChar(pos));
    int move = (type < 0) ? -1 : 1;
    int count = 1;
    if (type == 0)
      return false; 
    while (count > 0) {
      pos += move;
      if (pos < 0 || pos >= this.buf.length())
        return false; 
      int curType = getBracketType(this.buf.atChar(pos));
      if (curType == type) {
        count++;
        continue;
      } 
      if (curType == -type)
        count--; 
    } 
    if (move > 0 && isInViMoveOperation())
      pos++; 
    this.buf.cursor(pos);
    return true;
  }
  
  protected int getBracketType(int ch) {
    switch (ch) {
      case 91:
        return 1;
      case 93:
        return -1;
      case 123:
        return 2;
      case 125:
        return -2;
      case 40:
        return 3;
      case 41:
        return -3;
    } 
    return 0;
  }
  
  protected boolean transposeChars() {
    int lstart = this.buf.cursor() - 1;
    int lend = this.buf.cursor();
    while (this.buf.atChar(lstart) != 0 && this.buf.atChar(lstart) != 10)
      lstart--; 
    lstart++;
    while (this.buf.atChar(lend) != 0 && this.buf.atChar(lend) != 10)
      lend++; 
    if (lend - lstart < 2)
      return false; 
    boolean neg = (this.count < 0);
    for (int count = Math.max(this.count, -this.count); count > 0; count--) {
      while (this.buf.cursor() <= lstart)
        this.buf.move(1); 
      while (this.buf.cursor() >= lend)
        this.buf.move(-1); 
      int c = this.buf.currChar();
      this.buf.currChar(this.buf.prevChar());
      this.buf.move(-1);
      this.buf.currChar(c);
      this.buf.move(neg ? 0 : 2);
    } 
    return true;
  }
  
  protected boolean undo() {
    this.isUndo = true;
    if (this.undo.canUndo()) {
      this.undo.undo();
      return true;
    } 
    return false;
  }
  
  protected boolean redo() {
    this.isUndo = true;
    if (this.undo.canRedo()) {
      this.undo.redo();
      return true;
    } 
    return false;
  }
  
  protected boolean sendBreak() {
    if (this.searchTerm == null) {
      this.buf.clear();
      println();
      redrawLine();
      return false;
    } 
    return true;
  }
  
  protected boolean backwardChar() {
    return (this.buf.move(-this.count) != 0);
  }
  
  protected boolean forwardChar() {
    return (this.buf.move(this.count) != 0);
  }
  
  protected boolean viDigitOrBeginningOfLine() {
    if (this.repeatCount > 0)
      return digitArgument(); 
    return beginningOfLine();
  }
  
  protected boolean universalArgument() {
    this.mult *= this.universal;
    this.isArgDigit = true;
    return true;
  }
  
  protected boolean argumentBase() {
    if (this.repeatCount > 0 && this.repeatCount < 32) {
      this.universal = this.repeatCount;
      this.isArgDigit = true;
      return true;
    } 
    return false;
  }
  
  protected boolean negArgument() {
    this.mult *= -1;
    this.isArgDigit = true;
    return true;
  }
  
  protected boolean digitArgument() {
    String s = getLastBinding();
    this.repeatCount = this.repeatCount * 10 + s.charAt(s.length() - 1) - 48;
    this.isArgDigit = true;
    return true;
  }
  
  protected boolean viDelete() {
    int cursorStart = this.buf.cursor();
    Binding o = readBinding(getKeys());
    if (o instanceof Reference) {
      String op = viDeleteChangeYankToRemap(((Reference)o).name());
      if ("vi-delete".equals(op)) {
        killWholeLine();
      } else {
        this.viMoveMode = ViMoveMode.DELETE;
        Widget widget = this.widgets.get(op);
        if (widget != null && !widget.apply()) {
          this.viMoveMode = ViMoveMode.NORMAL;
          return false;
        } 
        this.viMoveMode = ViMoveMode.NORMAL;
      } 
      return viDeleteTo(cursorStart, this.buf.cursor());
    } 
    pushBackBinding();
    return false;
  }
  
  protected boolean viYankTo() {
    int cursorStart = this.buf.cursor();
    Binding o = readBinding(getKeys());
    if (o instanceof Reference) {
      String op = viDeleteChangeYankToRemap(((Reference)o).name());
      if ("vi-yank".equals(op)) {
        this.yankBuffer = this.buf.toString();
        return true;
      } 
      this.viMoveMode = ViMoveMode.YANK;
      Widget widget = this.widgets.get(op);
      if (widget != null && !widget.apply())
        return false; 
      this.viMoveMode = ViMoveMode.NORMAL;
      return viYankTo(cursorStart, this.buf.cursor());
    } 
    pushBackBinding();
    return false;
  }
  
  protected boolean viYankWholeLine() {
    int p = this.buf.cursor();
    while (this.buf.move(-1) == -1 && this.buf.prevChar() != 10);
    int s = this.buf.cursor();
    for (int i = 0; i < this.repeatCount; i++)
      while (this.buf.move(1) == 1 && this.buf.prevChar() != 10); 
    int e = this.buf.cursor();
    this.yankBuffer = this.buf.substring(s, e);
    if (!this.yankBuffer.endsWith("\n"))
      this.yankBuffer += "\n"; 
    this.buf.cursor(p);
    return true;
  }
  
  protected boolean viChange() {
    int cursorStart = this.buf.cursor();
    Binding o = readBinding(getKeys());
    if (o instanceof Reference) {
      String op = viDeleteChangeYankToRemap(((Reference)o).name());
      if ("vi-change-to".equals(op)) {
        killWholeLine();
      } else {
        this.viMoveMode = ViMoveMode.CHANGE;
        Widget widget = this.widgets.get(op);
        if (widget != null && !widget.apply()) {
          this.viMoveMode = ViMoveMode.NORMAL;
          return false;
        } 
        this.viMoveMode = ViMoveMode.NORMAL;
      } 
      boolean res = viChange(cursorStart, this.buf.cursor());
      setKeyMap("viins");
      return res;
    } 
    pushBackBinding();
    return false;
  }
  
  protected void cleanup() {
    if (isSet(LineReader.Option.ERASE_LINE_ON_FINISH)) {
      Buffer oldBuffer = this.buf.copy();
      AttributedString oldPrompt = this.prompt;
      this.buf.clear();
      this.prompt = new AttributedString("");
      doCleanup(false);
      this.prompt = oldPrompt;
      this.buf.copyFrom(oldBuffer);
    } else {
      doCleanup(true);
    } 
  }
  
  protected void doCleanup(boolean nl) {
    this.buf.cursor(this.buf.length());
    this.post = null;
    if (this.size.getColumns() > 0 || this.size.getRows() > 0) {
      this.doAutosuggestion = false;
      redisplay(false);
      if (nl)
        println(); 
      this.terminal.puts(InfoCmp.Capability.keypad_local, new Object[0]);
      this.terminal.trackMouse(Terminal.MouseTracking.Off);
      if (isSet(LineReader.Option.BRACKETED_PASTE) && !isTerminalDumb())
        this.terminal.writer().write("\033[?2004l"); 
      flush();
    } 
    this.history.moveToEnd();
  }
  
  protected boolean historyIncrementalSearchForward() {
    return doSearchHistory(false);
  }
  
  protected boolean historyIncrementalSearchBackward() {
    return doSearchHistory(true);
  }
  
  static class Pair<U, V> {
    final U u;
    
    final V v;
    
    public Pair(U u, V v) {
      this.u = u;
      this.v = v;
    }
    
    public U getU() {
      return this.u;
    }
    
    public V getV() {
      return this.v;
    }
  }
  
  protected boolean doSearchHistory(boolean backward) {
    if (this.history.isEmpty())
      return false; 
    KeyMap<Binding> terminators = new KeyMap();
    getString("search-terminators", "\033\n")
      .codePoints().forEach(c -> bind(terminators, "accept-line", new CharSequence[] { new String(Character.toChars(c)) }));
    Buffer originalBuffer = this.buf.copy();
    this.searchIndex = -1;
    this.searchTerm = new StringBuffer();
    this.searchBackward = backward;
    this.searchFailing = false;
    this.post = (() -> new AttributedString((this.searchFailing ? "failing " : "") + (this.searchBackward ? "bck-i-search" : "fwd-i-search") + ": " + this.searchTerm + "_"));
    redisplay();
    try {
      while (true) {
        boolean bool;
        int prevSearchIndex = this.searchIndex;
        Binding operation = readBinding(getKeys(), terminators);
        String ref = (operation instanceof Reference) ? ((Reference)operation).name() : "";
        boolean next = false;
        switch (ref) {
          case "abort":
            beep();
            this.buf.copyFrom(originalBuffer);
            bool = true;
            return bool;
          case "history-incremental-search-backward":
            this.searchBackward = true;
            next = true;
            break;
          case "history-incremental-search-forward":
            this.searchBackward = false;
            next = true;
            break;
          case "backward-delete-char":
            if (this.searchTerm.length() > 0)
              this.searchTerm.deleteCharAt(this.searchTerm.length() - 1); 
            break;
          case "self-insert":
            this.searchTerm.append(getLastBinding());
            break;
          default:
            if (this.searchIndex != -1)
              this.history.moveTo(this.searchIndex); 
            pushBackBinding();
            bool = true;
            return bool;
        } 
        String pattern = doGetSearchPattern();
        if (pattern.length() == 0) {
          this.buf.copyFrom(originalBuffer);
          this.searchFailing = false;
        } else {
          boolean caseInsensitive = isSet(LineReader.Option.CASE_INSENSITIVE_SEARCH);
          Pattern pat = Pattern.compile(pattern, caseInsensitive ? 66 : 
              64);
          Pair<Integer, Integer> pair = null;
          if (this.searchBackward) {
            boolean nextOnly = next;
            pair = matches(pat, this.buf.toString(), this.searchIndex).stream().filter(p -> nextOnly ? ((((Integer)p.v).intValue() < this.buf.cursor())) : ((((Integer)p.v).intValue() <= this.buf.cursor()))).max(Comparator.comparing(Pair::getV)).orElse(null);
            if (pair == null)
              pair = StreamSupport.stream(Spliterators.spliteratorUnknownSize(this.history.reverseIterator((this.searchIndex < 0) ? this.history.last() : (this.searchIndex - 1)), 16), false).flatMap(e -> matches(pat, e.line(), e.index()).stream()).findFirst().orElse(null); 
          } else {
            boolean nextOnly = next;
            pair = matches(pat, this.buf.toString(), this.searchIndex).stream().filter(p -> nextOnly ? ((((Integer)p.v).intValue() > this.buf.cursor())) : ((((Integer)p.v).intValue() >= this.buf.cursor()))).min(Comparator.comparing(Pair::getV)).orElse(null);
            if (pair == null) {
              pair = StreamSupport.stream(Spliterators.spliteratorUnknownSize(this.history.iterator(((this.searchIndex < 0) ? this.history.last() : this.searchIndex) + 1), 16), false).flatMap(e -> matches(pat, e.line(), e.index()).stream()).findFirst().orElse(null);
              if (pair == null && this.searchIndex >= 0)
                pair = matches(pat, originalBuffer.toString(), -1).stream().min(Comparator.comparing(Pair::getV)).orElse(null); 
            } 
          } 
          if (pair != null) {
            this.searchIndex = ((Integer)pair.u).intValue();
            this.buf.clear();
            if (this.searchIndex >= 0) {
              this.buf.write(this.history.get(this.searchIndex));
            } else {
              this.buf.write(originalBuffer.toString());
            } 
            this.buf.cursor(((Integer)pair.v).intValue());
            this.searchFailing = false;
          } else {
            this.searchFailing = true;
            beep();
          } 
        } 
        redisplay();
      } 
    } catch (IOError e) {
      if (!(e.getCause() instanceof InterruptedException))
        throw e; 
      return true;
    } finally {
      this.searchTerm = null;
      this.searchIndex = -1;
      this.post = null;
    } 
  }
  
  private List<Pair<Integer, Integer>> matches(Pattern p, String line, int index) {
    List<Pair<Integer, Integer>> starts = new ArrayList<>();
    Matcher m = p.matcher(line);
    while (m.find())
      starts.add(new Pair<>(Integer.valueOf(index), Integer.valueOf(m.start()))); 
    return starts;
  }
  
  private String doGetSearchPattern() {
    StringBuilder sb = new StringBuilder();
    boolean inQuote = false;
    for (int i = 0; i < this.searchTerm.length(); i++) {
      char c = this.searchTerm.charAt(i);
      if (Character.isLowerCase(c)) {
        if (inQuote) {
          sb.append("\\E");
          inQuote = false;
        } 
        sb.append("[").append(Character.toLowerCase(c)).append(Character.toUpperCase(c)).append("]");
      } else {
        if (!inQuote) {
          sb.append("\\Q");
          inQuote = true;
        } 
        sb.append(c);
      } 
    } 
    if (inQuote)
      sb.append("\\E"); 
    return sb.toString();
  }
  
  private void pushBackBinding() {
    pushBackBinding(false);
  }
  
  private void pushBackBinding(boolean skip) {
    String s = getLastBinding();
    if (s != null) {
      this.bindingReader.runMacro(s);
      this.skipRedisplay = skip;
    } 
  }
  
  protected boolean historySearchForward() {
    if (this.historyBuffer == null || this.buf.length() == 0 || 
      !this.buf.toString().equals(this.history.current())) {
      this.historyBuffer = this.buf.copy();
      this.searchBuffer = getFirstWord();
    } 
    int index = this.history.index() + 1;
    if (index < this.history.last() + 1) {
      int searchIndex = searchForwards(this.searchBuffer.toString(), index, true);
      if (searchIndex == -1) {
        this.history.moveToEnd();
        if (!this.buf.toString().equals(this.historyBuffer.toString())) {
          setBuffer(this.historyBuffer.toString());
          this.historyBuffer = null;
        } else {
          return false;
        } 
      } else if (this.history.moveTo(searchIndex)) {
        setBuffer(this.history.current());
      } else {
        this.history.moveToEnd();
        setBuffer(this.historyBuffer.toString());
        return false;
      } 
    } else {
      this.history.moveToEnd();
      if (!this.buf.toString().equals(this.historyBuffer.toString())) {
        setBuffer(this.historyBuffer.toString());
        this.historyBuffer = null;
      } else {
        return false;
      } 
    } 
    return true;
  }
  
  private CharSequence getFirstWord() {
    String s = this.buf.toString();
    int i = 0;
    while (i < s.length() && !Character.isWhitespace(s.charAt(i)))
      i++; 
    return s.substring(0, i);
  }
  
  protected boolean historySearchBackward() {
    if (this.historyBuffer == null || this.buf.length() == 0 || 
      !this.buf.toString().equals(this.history.current())) {
      this.historyBuffer = this.buf.copy();
      this.searchBuffer = getFirstWord();
    } 
    int searchIndex = searchBackwards(this.searchBuffer.toString(), this.history.index(), true);
    if (searchIndex == -1)
      return false; 
    if (this.history.moveTo(searchIndex)) {
      setBuffer(this.history.current());
    } else {
      return false;
    } 
    return true;
  }
  
  public int searchBackwards(String searchTerm, int startIndex) {
    return searchBackwards(searchTerm, startIndex, false);
  }
  
  public int searchBackwards(String searchTerm) {
    return searchBackwards(searchTerm, this.history.index(), false);
  }
  
  public int searchBackwards(String searchTerm, int startIndex, boolean startsWith) {
    boolean caseInsensitive = isSet(LineReader.Option.CASE_INSENSITIVE_SEARCH);
    if (caseInsensitive)
      searchTerm = searchTerm.toLowerCase(); 
    ListIterator<History.Entry> it = this.history.iterator(startIndex);
    while (it.hasPrevious()) {
      History.Entry e = it.previous();
      String line = e.line();
      if (caseInsensitive)
        line = line.toLowerCase(); 
      int idx = line.indexOf(searchTerm);
      if ((startsWith && idx == 0) || (!startsWith && idx >= 0))
        return e.index(); 
    } 
    return -1;
  }
  
  public int searchForwards(String searchTerm, int startIndex, boolean startsWith) {
    boolean caseInsensitive = isSet(LineReader.Option.CASE_INSENSITIVE_SEARCH);
    if (caseInsensitive)
      searchTerm = searchTerm.toLowerCase(); 
    if (startIndex > this.history.last())
      startIndex = this.history.last(); 
    ListIterator<History.Entry> it = this.history.iterator(startIndex);
    if (this.searchIndex != -1 && it.hasNext())
      it.next(); 
    while (it.hasNext()) {
      History.Entry e = it.next();
      String line = e.line();
      if (caseInsensitive)
        line = line.toLowerCase(); 
      int idx = line.indexOf(searchTerm);
      if ((startsWith && idx == 0) || (!startsWith && idx >= 0))
        return e.index(); 
    } 
    return -1;
  }
  
  public int searchForwards(String searchTerm, int startIndex) {
    return searchForwards(searchTerm, startIndex, false);
  }
  
  public int searchForwards(String searchTerm) {
    return searchForwards(searchTerm, this.history.index());
  }
  
  protected boolean quit() {
    getBuffer().clear();
    return acceptLine();
  }
  
  protected boolean acceptAndHold() {
    this.nextCommandFromHistory = false;
    acceptLine();
    if (!this.buf.toString().isEmpty()) {
      this.nextHistoryId = Integer.MAX_VALUE;
      this.nextCommandFromHistory = true;
    } 
    return this.nextCommandFromHistory;
  }
  
  protected boolean acceptLineAndDownHistory() {
    this.nextCommandFromHistory = false;
    acceptLine();
    if (this.nextHistoryId < 0)
      this.nextHistoryId = this.history.index(); 
    if (this.history.size() > this.nextHistoryId + 1) {
      this.nextHistoryId++;
      this.nextCommandFromHistory = true;
    } 
    return this.nextCommandFromHistory;
  }
  
  protected boolean acceptAndInferNextHistory() {
    this.nextCommandFromHistory = false;
    acceptLine();
    if (!this.buf.toString().isEmpty()) {
      this.nextHistoryId = searchBackwards(this.buf.toString(), this.history.last());
      if (this.nextHistoryId >= 0 && this.history.size() > this.nextHistoryId + 1) {
        this.nextHistoryId++;
        this.nextCommandFromHistory = true;
      } 
    } 
    return this.nextCommandFromHistory;
  }
  
  protected boolean acceptLine() {
    this.parsedLine = null;
    int curPos = 0;
    if (!isSet(LineReader.Option.DISABLE_EVENT_EXPANSION))
      try {
        String str = this.buf.toString();
        String exp = this.expander.expandHistory(this.history, str);
        if (!exp.equals(str)) {
          this.buf.clear();
          this.buf.write(exp);
          if (isSet(LineReader.Option.HISTORY_VERIFY))
            return true; 
        } 
      } catch (IllegalArgumentException illegalArgumentException) {} 
    try {
      curPos = this.buf.cursor();
      this.parsedLine = this.parser.parse(this.buf.toString(), this.buf.cursor(), Parser.ParseContext.ACCEPT_LINE);
    } catch (EOFError e) {
      StringBuilder sb = new StringBuilder("\n");
      indention(e.getOpenBrackets(), sb);
      int curMove = sb.length();
      if (isSet(LineReader.Option.INSERT_BRACKET) && e.getOpenBrackets() > 1 && e.getNextClosingBracket() != null) {
        sb.append('\n');
        indention(e.getOpenBrackets() - 1, sb);
        sb.append(e.getNextClosingBracket());
      } 
      this.buf.write(sb.toString());
      this.buf.cursor(curPos + curMove);
      return true;
    } catch (SyntaxError syntaxError) {}
    callWidget("callback-finish");
    this.state = State.DONE;
    return true;
  }
  
  void indention(int nb, StringBuilder sb) {
    int indent = getInt("indentation", 0) * nb;
    for (int i = 0; i < indent; i++)
      sb.append(' '); 
  }
  
  protected boolean selfInsert() {
    for (int count = this.count; count > 0; count--)
      putString(getLastBinding()); 
    return true;
  }
  
  protected boolean selfInsertUnmeta() {
    if (getLastBinding().charAt(0) == '\033') {
      String s = getLastBinding().substring(1);
      if ("\r".equals(s))
        s = "\n"; 
      for (int count = this.count; count > 0; count--)
        putString(s); 
      return true;
    } 
    return false;
  }
  
  protected boolean overwriteMode() {
    this.overTyping = !this.overTyping;
    return true;
  }
  
  protected boolean beginningOfBufferOrHistory() {
    if (findbol() != 0) {
      this.buf.cursor(0);
      return true;
    } 
    return beginningOfHistory();
  }
  
  protected boolean beginningOfHistory() {
    if (this.history.moveToFirst()) {
      setBuffer(this.history.current());
      return true;
    } 
    return false;
  }
  
  protected boolean endOfBufferOrHistory() {
    if (findeol() != this.buf.length()) {
      this.buf.cursor(this.buf.length());
      return true;
    } 
    return endOfHistory();
  }
  
  protected boolean endOfHistory() {
    if (this.history.moveToLast()) {
      setBuffer(this.history.current());
      return true;
    } 
    return false;
  }
  
  protected boolean beginningOfLineHist() {
    if (this.count < 0)
      return callNeg(this::endOfLineHist); 
    while (this.count-- > 0) {
      int bol = findbol();
      if (bol != this.buf.cursor()) {
        this.buf.cursor(bol);
        continue;
      } 
      moveHistory(false);
      this.buf.cursor(0);
    } 
    return true;
  }
  
  protected boolean endOfLineHist() {
    if (this.count < 0)
      return callNeg(this::beginningOfLineHist); 
    while (this.count-- > 0) {
      int eol = findeol();
      if (eol != this.buf.cursor()) {
        this.buf.cursor(eol);
        continue;
      } 
      moveHistory(true);
    } 
    return true;
  }
  
  protected boolean upHistory() {
    while (this.count-- > 0) {
      if (!moveHistory(false))
        return !isSet(LineReader.Option.HISTORY_BEEP); 
    } 
    return true;
  }
  
  protected boolean downHistory() {
    while (this.count-- > 0) {
      if (!moveHistory(true))
        return !isSet(LineReader.Option.HISTORY_BEEP); 
    } 
    return true;
  }
  
  protected boolean viUpLineOrHistory() {
    return (upLine() || (
      upHistory() && viFirstNonBlank()));
  }
  
  protected boolean viDownLineOrHistory() {
    return (downLine() || (
      downHistory() && viFirstNonBlank()));
  }
  
  protected boolean upLine() {
    return this.buf.up();
  }
  
  protected boolean downLine() {
    return this.buf.down();
  }
  
  protected boolean upLineOrHistory() {
    return (upLine() || upHistory());
  }
  
  protected boolean upLineOrSearch() {
    return (upLine() || historySearchBackward());
  }
  
  protected boolean downLineOrHistory() {
    return (downLine() || downHistory());
  }
  
  protected boolean downLineOrSearch() {
    return (downLine() || historySearchForward());
  }
  
  protected boolean viCmdMode() {
    if (this.state == State.NORMAL)
      this.buf.move(-1); 
    return setKeyMap("vicmd");
  }
  
  protected boolean viInsert() {
    return setKeyMap("viins");
  }
  
  protected boolean viAddNext() {
    this.buf.move(1);
    return setKeyMap("viins");
  }
  
  protected boolean viAddEol() {
    return (endOfLine() && setKeyMap("viins"));
  }
  
  protected boolean emacsEditingMode() {
    return setKeyMap("emacs");
  }
  
  protected boolean viChangeWholeLine() {
    return (viFirstNonBlank() && viChangeEol());
  }
  
  protected boolean viChangeEol() {
    return (viChange(this.buf.cursor(), this.buf.length()) && 
      setKeyMap("viins"));
  }
  
  protected boolean viKillEol() {
    int eol = findeol();
    if (this.buf.cursor() == eol)
      return false; 
    this.killRing.add(this.buf.substring(this.buf.cursor(), eol));
    this.buf.delete(eol - this.buf.cursor());
    return true;
  }
  
  protected boolean quotedInsert() {
    int c = readCharacter();
    while (this.count-- > 0)
      putString(new String(Character.toChars(c))); 
    return true;
  }
  
  protected boolean viJoin() {
    if (this.buf.down()) {
      while (this.buf.move(-1) == -1 && this.buf.prevChar() != 10);
      this.buf.backspace();
      this.buf.write(32);
      this.buf.move(-1);
      return true;
    } 
    return false;
  }
  
  protected boolean viKillWholeLine() {
    return (killWholeLine() && setKeyMap("viins"));
  }
  
  protected boolean viInsertBol() {
    return (beginningOfLine() && setKeyMap("viins"));
  }
  
  protected boolean backwardDeleteChar() {
    if (this.count < 0)
      return callNeg(this::deleteChar); 
    if (this.buf.cursor() == 0)
      return false; 
    this.buf.backspace(this.count);
    return true;
  }
  
  protected boolean viFirstNonBlank() {
    beginningOfLine();
    while (this.buf.cursor() < this.buf.length() && isWhitespace(this.buf.currChar()))
      this.buf.move(1); 
    return true;
  }
  
  protected boolean viBeginningOfLine() {
    this.buf.cursor(findbol());
    return true;
  }
  
  protected boolean viEndOfLine() {
    if (this.count < 0)
      return false; 
    while (this.count-- > 0)
      this.buf.cursor(findeol() + 1); 
    this.buf.move(-1);
    return true;
  }
  
  protected boolean beginningOfLine() {
    while (this.count-- > 0)
      while (this.buf.move(-1) == -1 && this.buf.prevChar() != 10); 
    return true;
  }
  
  protected boolean endOfLine() {
    while (this.count-- > 0)
      while (this.buf.move(1) == 1 && this.buf.currChar() != 10); 
    return true;
  }
  
  protected boolean deleteChar() {
    if (this.count < 0)
      return callNeg(this::backwardDeleteChar); 
    if (this.buf.cursor() == this.buf.length())
      return false; 
    this.buf.delete(this.count);
    return true;
  }
  
  protected boolean viBackwardDeleteChar() {
    for (int i = 0; i < this.count; i++) {
      if (!this.buf.backspace())
        return false; 
    } 
    return true;
  }
  
  protected boolean viDeleteChar() {
    for (int i = 0; i < this.count; i++) {
      if (!this.buf.delete())
        return false; 
    } 
    return true;
  }
  
  protected boolean viSwapCase() {
    for (int i = 0; i < this.count; i++) {
      if (this.buf.cursor() < this.buf.length()) {
        int ch = this.buf.atChar(this.buf.cursor());
        ch = switchCase(ch);
        this.buf.currChar(ch);
        this.buf.move(1);
      } else {
        return false;
      } 
    } 
    return true;
  }
  
  protected boolean viReplaceChars() {
    int c = readCharacter();
    if (c < 0 || c == 27 || c == 3)
      return true; 
    for (int i = 0; i < this.count; i++) {
      if (this.buf.currChar((char)c)) {
        if (i < this.count - 1)
          this.buf.move(1); 
      } else {
        return false;
      } 
    } 
    return true;
  }
  
  protected boolean viChange(int startPos, int endPos) {
    return doViDeleteOrChange(startPos, endPos, true);
  }
  
  protected boolean viDeleteTo(int startPos, int endPos) {
    return doViDeleteOrChange(startPos, endPos, false);
  }
  
  protected boolean doViDeleteOrChange(int startPos, int endPos, boolean isChange) {
    if (startPos == endPos)
      return true; 
    if (endPos < startPos) {
      int tmp = endPos;
      endPos = startPos;
      startPos = tmp;
    } 
    this.buf.cursor(startPos);
    this.buf.delete(endPos - startPos);
    if (!isChange && startPos > 0 && startPos == this.buf.length())
      this.buf.move(-1); 
    return true;
  }
  
  protected boolean viYankTo(int startPos, int endPos) {
    int cursorPos = startPos;
    if (endPos < startPos) {
      int tmp = endPos;
      endPos = startPos;
      startPos = tmp;
    } 
    if (startPos == endPos) {
      this.yankBuffer = "";
      return true;
    } 
    this.yankBuffer = this.buf.substring(startPos, endPos);
    this.buf.cursor(cursorPos);
    return true;
  }
  
  protected boolean viOpenLineAbove() {
    while (this.buf.move(-1) == -1 && this.buf.prevChar() != 10);
    this.buf.write(10);
    this.buf.move(-1);
    return setKeyMap("viins");
  }
  
  protected boolean viOpenLineBelow() {
    while (this.buf.move(1) == 1 && this.buf.currChar() != 10);
    this.buf.write(10);
    return setKeyMap("viins");
  }
  
  protected boolean viPutAfter() {
    if (this.yankBuffer.indexOf('\n') >= 0) {
      while (this.buf.move(1) == 1 && this.buf.currChar() != 10);
      this.buf.move(1);
      putString(this.yankBuffer);
      this.buf.move(-this.yankBuffer.length());
    } else if (this.yankBuffer.length() != 0) {
      if (this.buf.cursor() < this.buf.length())
        this.buf.move(1); 
      for (int i = 0; i < this.count; i++)
        putString(this.yankBuffer); 
      this.buf.move(-1);
    } 
    return true;
  }
  
  protected boolean viPutBefore() {
    if (this.yankBuffer.indexOf('\n') >= 0) {
      while (this.buf.move(-1) == -1 && this.buf.prevChar() != 10);
      putString(this.yankBuffer);
      this.buf.move(-this.yankBuffer.length());
    } else if (this.yankBuffer.length() != 0) {
      if (this.buf.cursor() > 0)
        this.buf.move(-1); 
      for (int i = 0; i < this.count; i++)
        putString(this.yankBuffer); 
      this.buf.move(-1);
    } 
    return true;
  }
  
  protected boolean doLowercaseVersion() {
    this.bindingReader.runMacro(getLastBinding().toLowerCase());
    return true;
  }
  
  protected boolean setMarkCommand() {
    if (this.count < 0) {
      this.regionActive = LineReader.RegionType.NONE;
      return true;
    } 
    this.regionMark = this.buf.cursor();
    this.regionActive = LineReader.RegionType.CHAR;
    return true;
  }
  
  protected boolean exchangePointAndMark() {
    if (this.count == 0) {
      this.regionActive = LineReader.RegionType.CHAR;
      return true;
    } 
    int x = this.regionMark;
    this.regionMark = this.buf.cursor();
    this.buf.cursor(x);
    if (this.buf.cursor() > this.buf.length())
      this.buf.cursor(this.buf.length()); 
    if (this.count > 0)
      this.regionActive = LineReader.RegionType.CHAR; 
    return true;
  }
  
  protected boolean visualMode() {
    if (isInViMoveOperation()) {
      this.isArgDigit = true;
      this.forceLine = false;
      this.forceChar = true;
      return true;
    } 
    if (this.regionActive == LineReader.RegionType.NONE) {
      this.regionMark = this.buf.cursor();
      this.regionActive = LineReader.RegionType.CHAR;
    } else if (this.regionActive == LineReader.RegionType.CHAR) {
      this.regionActive = LineReader.RegionType.NONE;
    } else if (this.regionActive == LineReader.RegionType.LINE) {
      this.regionActive = LineReader.RegionType.CHAR;
    } 
    return true;
  }
  
  protected boolean visualLineMode() {
    if (isInViMoveOperation()) {
      this.isArgDigit = true;
      this.forceLine = true;
      this.forceChar = false;
      return true;
    } 
    if (this.regionActive == LineReader.RegionType.NONE) {
      this.regionMark = this.buf.cursor();
      this.regionActive = LineReader.RegionType.LINE;
    } else if (this.regionActive == LineReader.RegionType.CHAR) {
      this.regionActive = LineReader.RegionType.LINE;
    } else if (this.regionActive == LineReader.RegionType.LINE) {
      this.regionActive = LineReader.RegionType.NONE;
    } 
    return true;
  }
  
  protected boolean deactivateRegion() {
    this.regionActive = LineReader.RegionType.NONE;
    return true;
  }
  
  protected boolean whatCursorPosition() {
    this.post = (() -> {
        AttributedStringBuilder sb = new AttributedStringBuilder();
        if (this.buf.cursor() < this.buf.length()) {
          int c = this.buf.currChar();
          sb.append("Char: ");
          if (c == 32) {
            sb.append("SPC");
          } else if (c == 10) {
            sb.append("LFD");
          } else if (c < 32) {
            sb.append('^');
            sb.append((char)(c + 65 - 1));
          } else if (c == 127) {
            sb.append("^?");
          } else {
            sb.append((char)c);
          } 
          sb.append(" (");
          sb.append("0").append(Integer.toOctalString(c)).append(" ");
          sb.append(Integer.toString(c)).append(" ");
          sb.append("0x").append(Integer.toHexString(c)).append(" ");
          sb.append(")");
        } else {
          sb.append("EOF");
        } 
        sb.append("   ");
        sb.append("point ");
        sb.append(Integer.toString(this.buf.cursor() + 1));
        sb.append(" of ");
        sb.append(Integer.toString(this.buf.length() + 1));
        sb.append(" (");
        sb.append(Integer.toString((this.buf.length() == 0) ? 100 : (100 * this.buf.cursor() / this.buf.length())));
        sb.append("%)");
        sb.append("   ");
        sb.append("column ");
        sb.append(Integer.toString(this.buf.cursor() - findbol()));
        return sb.toAttributedString();
      });
    return true;
  }
  
  protected boolean editAndExecute() {
    boolean out = true;
    File file = null;
    try {
      file = File.createTempFile("jline-execute-", null);
      FileWriter writer = new FileWriter(file);
      writer.write(this.buf.toString());
      writer.close();
      editAndAddInBuffer(file);
    } catch (Exception e) {
      e.printStackTrace(this.terminal.writer());
      out = false;
    } finally {
      this.state = State.IGNORE;
      if (file != null && file.exists())
        file.delete(); 
    } 
    return out;
  }
  
  protected Map<String, Widget> builtinWidgets() {
    Map<String, Widget> widgets = new HashMap<>();
    addBuiltinWidget(widgets, "accept-and-infer-next-history", this::acceptAndInferNextHistory);
    addBuiltinWidget(widgets, "accept-and-hold", this::acceptAndHold);
    addBuiltinWidget(widgets, "accept-line", this::acceptLine);
    addBuiltinWidget(widgets, "accept-line-and-down-history", this::acceptLineAndDownHistory);
    addBuiltinWidget(widgets, "argument-base", this::argumentBase);
    addBuiltinWidget(widgets, "backward-char", this::backwardChar);
    addBuiltinWidget(widgets, "backward-delete-char", this::backwardDeleteChar);
    addBuiltinWidget(widgets, "backward-delete-word", this::backwardDeleteWord);
    addBuiltinWidget(widgets, "backward-kill-line", this::backwardKillLine);
    addBuiltinWidget(widgets, "backward-kill-word", this::backwardKillWord);
    addBuiltinWidget(widgets, "backward-word", this::backwardWord);
    addBuiltinWidget(widgets, "beep", this::beep);
    addBuiltinWidget(widgets, "beginning-of-buffer-or-history", this::beginningOfBufferOrHistory);
    addBuiltinWidget(widgets, "beginning-of-history", this::beginningOfHistory);
    addBuiltinWidget(widgets, "beginning-of-line", this::beginningOfLine);
    addBuiltinWidget(widgets, "beginning-of-line-hist", this::beginningOfLineHist);
    addBuiltinWidget(widgets, "capitalize-word", this::capitalizeWord);
    addBuiltinWidget(widgets, "clear", this::clear);
    addBuiltinWidget(widgets, "clear-screen", this::clearScreen);
    addBuiltinWidget(widgets, "complete-prefix", this::completePrefix);
    addBuiltinWidget(widgets, "complete-word", this::completeWord);
    addBuiltinWidget(widgets, "copy-prev-word", this::copyPrevWord);
    addBuiltinWidget(widgets, "copy-region-as-kill", this::copyRegionAsKill);
    addBuiltinWidget(widgets, "delete-char", this::deleteChar);
    addBuiltinWidget(widgets, "delete-char-or-list", this::deleteCharOrList);
    addBuiltinWidget(widgets, "delete-word", this::deleteWord);
    addBuiltinWidget(widgets, "digit-argument", this::digitArgument);
    addBuiltinWidget(widgets, "do-lowercase-version", this::doLowercaseVersion);
    addBuiltinWidget(widgets, "down-case-word", this::downCaseWord);
    addBuiltinWidget(widgets, "down-line", this::downLine);
    addBuiltinWidget(widgets, "down-line-or-history", this::downLineOrHistory);
    addBuiltinWidget(widgets, "down-line-or-search", this::downLineOrSearch);
    addBuiltinWidget(widgets, "down-history", this::downHistory);
    addBuiltinWidget(widgets, "edit-and-execute-command", this::editAndExecute);
    addBuiltinWidget(widgets, "emacs-editing-mode", this::emacsEditingMode);
    addBuiltinWidget(widgets, "emacs-backward-word", this::emacsBackwardWord);
    addBuiltinWidget(widgets, "emacs-forward-word", this::emacsForwardWord);
    addBuiltinWidget(widgets, "end-of-buffer-or-history", this::endOfBufferOrHistory);
    addBuiltinWidget(widgets, "end-of-history", this::endOfHistory);
    addBuiltinWidget(widgets, "end-of-line", this::endOfLine);
    addBuiltinWidget(widgets, "end-of-line-hist", this::endOfLineHist);
    addBuiltinWidget(widgets, "exchange-point-and-mark", this::exchangePointAndMark);
    addBuiltinWidget(widgets, "expand-history", this::expandHistory);
    addBuiltinWidget(widgets, "expand-or-complete", this::expandOrComplete);
    addBuiltinWidget(widgets, "expand-or-complete-prefix", this::expandOrCompletePrefix);
    addBuiltinWidget(widgets, "expand-word", this::expandWord);
    addBuiltinWidget(widgets, "fresh-line", this::freshLine);
    addBuiltinWidget(widgets, "forward-char", this::forwardChar);
    addBuiltinWidget(widgets, "forward-word", this::forwardWord);
    addBuiltinWidget(widgets, "history-incremental-search-backward", this::historyIncrementalSearchBackward);
    addBuiltinWidget(widgets, "history-incremental-search-forward", this::historyIncrementalSearchForward);
    addBuiltinWidget(widgets, "history-search-backward", this::historySearchBackward);
    addBuiltinWidget(widgets, "history-search-forward", this::historySearchForward);
    addBuiltinWidget(widgets, "insert-close-curly", this::insertCloseCurly);
    addBuiltinWidget(widgets, "insert-close-paren", this::insertCloseParen);
    addBuiltinWidget(widgets, "insert-close-square", this::insertCloseSquare);
    addBuiltinWidget(widgets, "insert-comment", this::insertComment);
    addBuiltinWidget(widgets, "kill-buffer", this::killBuffer);
    addBuiltinWidget(widgets, "kill-line", this::killLine);
    addBuiltinWidget(widgets, "kill-region", this::killRegion);
    addBuiltinWidget(widgets, "kill-whole-line", this::killWholeLine);
    addBuiltinWidget(widgets, "kill-word", this::killWord);
    addBuiltinWidget(widgets, "list-choices", this::listChoices);
    addBuiltinWidget(widgets, "menu-complete", this::menuComplete);
    addBuiltinWidget(widgets, "menu-expand-or-complete", this::menuExpandOrComplete);
    addBuiltinWidget(widgets, "neg-argument", this::negArgument);
    addBuiltinWidget(widgets, "overwrite-mode", this::overwriteMode);
    addBuiltinWidget(widgets, "quoted-insert", this::quotedInsert);
    addBuiltinWidget(widgets, "redisplay", this::redisplay);
    addBuiltinWidget(widgets, "redraw-line", this::redrawLine);
    addBuiltinWidget(widgets, "redo", this::redo);
    addBuiltinWidget(widgets, "self-insert", this::selfInsert);
    addBuiltinWidget(widgets, "self-insert-unmeta", this::selfInsertUnmeta);
    addBuiltinWidget(widgets, "abort", this::sendBreak);
    addBuiltinWidget(widgets, "set-mark-command", this::setMarkCommand);
    addBuiltinWidget(widgets, "transpose-chars", this::transposeChars);
    addBuiltinWidget(widgets, "transpose-words", this::transposeWords);
    addBuiltinWidget(widgets, "undefined-key", this::undefinedKey);
    addBuiltinWidget(widgets, "universal-argument", this::universalArgument);
    addBuiltinWidget(widgets, "undo", this::undo);
    addBuiltinWidget(widgets, "up-case-word", this::upCaseWord);
    addBuiltinWidget(widgets, "up-history", this::upHistory);
    addBuiltinWidget(widgets, "up-line", this::upLine);
    addBuiltinWidget(widgets, "up-line-or-history", this::upLineOrHistory);
    addBuiltinWidget(widgets, "up-line-or-search", this::upLineOrSearch);
    addBuiltinWidget(widgets, "vi-add-eol", this::viAddEol);
    addBuiltinWidget(widgets, "vi-add-next", this::viAddNext);
    addBuiltinWidget(widgets, "vi-backward-char", this::viBackwardChar);
    addBuiltinWidget(widgets, "vi-backward-delete-char", this::viBackwardDeleteChar);
    addBuiltinWidget(widgets, "vi-backward-blank-word", this::viBackwardBlankWord);
    addBuiltinWidget(widgets, "vi-backward-blank-word-end", this::viBackwardBlankWordEnd);
    addBuiltinWidget(widgets, "vi-backward-kill-word", this::viBackwardKillWord);
    addBuiltinWidget(widgets, "vi-backward-word", this::viBackwardWord);
    addBuiltinWidget(widgets, "vi-backward-word-end", this::viBackwardWordEnd);
    addBuiltinWidget(widgets, "vi-beginning-of-line", this::viBeginningOfLine);
    addBuiltinWidget(widgets, "vi-cmd-mode", this::viCmdMode);
    addBuiltinWidget(widgets, "vi-digit-or-beginning-of-line", this::viDigitOrBeginningOfLine);
    addBuiltinWidget(widgets, "vi-down-line-or-history", this::viDownLineOrHistory);
    addBuiltinWidget(widgets, "vi-change-to", this::viChange);
    addBuiltinWidget(widgets, "vi-change-eol", this::viChangeEol);
    addBuiltinWidget(widgets, "vi-change-whole-line", this::viChangeWholeLine);
    addBuiltinWidget(widgets, "vi-delete-char", this::viDeleteChar);
    addBuiltinWidget(widgets, "vi-delete", this::viDelete);
    addBuiltinWidget(widgets, "vi-end-of-line", this::viEndOfLine);
    addBuiltinWidget(widgets, "vi-kill-eol", this::viKillEol);
    addBuiltinWidget(widgets, "vi-first-non-blank", this::viFirstNonBlank);
    addBuiltinWidget(widgets, "vi-find-next-char", this::viFindNextChar);
    addBuiltinWidget(widgets, "vi-find-next-char-skip", this::viFindNextCharSkip);
    addBuiltinWidget(widgets, "vi-find-prev-char", this::viFindPrevChar);
    addBuiltinWidget(widgets, "vi-find-prev-char-skip", this::viFindPrevCharSkip);
    addBuiltinWidget(widgets, "vi-forward-blank-word", this::viForwardBlankWord);
    addBuiltinWidget(widgets, "vi-forward-blank-word-end", this::viForwardBlankWordEnd);
    addBuiltinWidget(widgets, "vi-forward-char", this::viForwardChar);
    addBuiltinWidget(widgets, "vi-forward-word", this::viForwardWord);
    addBuiltinWidget(widgets, "vi-forward-word", this::viForwardWord);
    addBuiltinWidget(widgets, "vi-forward-word-end", this::viForwardWordEnd);
    addBuiltinWidget(widgets, "vi-history-search-backward", this::viHistorySearchBackward);
    addBuiltinWidget(widgets, "vi-history-search-forward", this::viHistorySearchForward);
    addBuiltinWidget(widgets, "vi-insert", this::viInsert);
    addBuiltinWidget(widgets, "vi-insert-bol", this::viInsertBol);
    addBuiltinWidget(widgets, "vi-insert-comment", this::viInsertComment);
    addBuiltinWidget(widgets, "vi-join", this::viJoin);
    addBuiltinWidget(widgets, "vi-kill-line", this::viKillWholeLine);
    addBuiltinWidget(widgets, "vi-match-bracket", this::viMatchBracket);
    addBuiltinWidget(widgets, "vi-open-line-above", this::viOpenLineAbove);
    addBuiltinWidget(widgets, "vi-open-line-below", this::viOpenLineBelow);
    addBuiltinWidget(widgets, "vi-put-after", this::viPutAfter);
    addBuiltinWidget(widgets, "vi-put-before", this::viPutBefore);
    addBuiltinWidget(widgets, "vi-repeat-find", this::viRepeatFind);
    addBuiltinWidget(widgets, "vi-repeat-search", this::viRepeatSearch);
    addBuiltinWidget(widgets, "vi-replace-chars", this::viReplaceChars);
    addBuiltinWidget(widgets, "vi-rev-repeat-find", this::viRevRepeatFind);
    addBuiltinWidget(widgets, "vi-rev-repeat-search", this::viRevRepeatSearch);
    addBuiltinWidget(widgets, "vi-swap-case", this::viSwapCase);
    addBuiltinWidget(widgets, "vi-up-line-or-history", this::viUpLineOrHistory);
    addBuiltinWidget(widgets, "vi-yank", this::viYankTo);
    addBuiltinWidget(widgets, "vi-yank-whole-line", this::viYankWholeLine);
    addBuiltinWidget(widgets, "visual-line-mode", this::visualLineMode);
    addBuiltinWidget(widgets, "visual-mode", this::visualMode);
    addBuiltinWidget(widgets, "what-cursor-position", this::whatCursorPosition);
    addBuiltinWidget(widgets, "yank", this::yank);
    addBuiltinWidget(widgets, "yank-pop", this::yankPop);
    addBuiltinWidget(widgets, "mouse", this::mouse);
    addBuiltinWidget(widgets, "begin-paste", this::beginPaste);
    addBuiltinWidget(widgets, "terminal-focus-in", this::focusIn);
    addBuiltinWidget(widgets, "terminal-focus-out", this::focusOut);
    return widgets;
  }
  
  private void addBuiltinWidget(Map<String, Widget> widgets, String name, Widget widget) {
    widgets.put(name, namedWidget("." + name, widget));
  }
  
  private Widget namedWidget(final String name, final Widget widget) {
    return new Widget() {
        public String toString() {
          return name;
        }
        
        public boolean apply() {
          return widget.apply();
        }
      };
  }
  
  public boolean redisplay() {
    redisplay(true);
    return true;
  }
  
  protected void redisplay(boolean flush) {
    try {
      List<AttributedString> newLines, rightPromptLines;
      this.lock.lock();
      if (this.skipRedisplay) {
        this.skipRedisplay = false;
        return;
      } 
      Status status = Status.getStatus(this.terminal, false);
      if (status != null)
        status.redraw(); 
      if (this.size.getRows() > 0 && this.size.getRows() < 3) {
        AttributedStringBuilder sb = (new AttributedStringBuilder()).tabs(4);
        sb.append(this.prompt);
        concat(getHighlightedBuffer(this.buf.toString()).columnSplitLength(2147483647), sb);
        AttributedString attributedString1 = sb.toAttributedString();
        sb.setLength(0);
        sb.append(this.prompt);
        String line = this.buf.upToCursor();
        if (this.maskingCallback != null)
          line = this.maskingCallback.display(line); 
        concat((new AttributedString(line)).columnSplitLength(2147483647), sb);
        AttributedString toCursor = sb.toAttributedString();
        int w = WCWidth.wcwidth(8230);
        int width = this.size.getColumns();
        int cursor = toCursor.columnLength();
        int inc = width / 2 + 1;
        while (cursor <= this.smallTerminalOffset + w)
          this.smallTerminalOffset -= inc; 
        while (cursor >= this.smallTerminalOffset + width - w)
          this.smallTerminalOffset += inc; 
        if (this.smallTerminalOffset > 0) {
          sb.setLength(0);
          sb.append("…");
          sb.append(attributedString1.columnSubSequence(this.smallTerminalOffset + w, 2147483647));
          attributedString1 = sb.toAttributedString();
        } 
        int length = attributedString1.columnLength();
        if (length >= this.smallTerminalOffset + width) {
          sb.setLength(0);
          sb.append(attributedString1.columnSubSequence(0, width - w));
          sb.append("…");
          attributedString1 = sb.toAttributedString();
        } 
        this.display.update(Collections.singletonList(attributedString1), cursor - this.smallTerminalOffset, flush);
        return;
      } 
      List<AttributedString> secondaryPrompts = new ArrayList<>();
      AttributedString full = getDisplayedBufferWithPrompts(secondaryPrompts);
      if (this.size.getColumns() <= 0) {
        newLines = new ArrayList<>();
        newLines.add(full);
      } else {
        newLines = full.columnSplitLength(this.size.getColumns(), true, this.display.delayLineWrap());
      } 
      if (this.rightPrompt.length() == 0 || this.size.getColumns() <= 0) {
        rightPromptLines = new ArrayList<>();
      } else {
        rightPromptLines = this.rightPrompt.columnSplitLength(this.size.getColumns());
      } 
      while (newLines.size() < rightPromptLines.size())
        newLines.add(new AttributedString("")); 
      for (int i = 0; i < rightPromptLines.size(); i++) {
        AttributedString line = rightPromptLines.get(i);
        newLines.set(i, addRightPrompt(line, newLines.get(i)));
      } 
      int cursorPos = -1;
      int cursorNewLinesId = -1;
      int cursorColPos = -1;
      if (this.size.getColumns() > 0) {
        AttributedStringBuilder sb = (new AttributedStringBuilder()).tabs(4);
        sb.append(this.prompt);
        String buffer = this.buf.upToCursor();
        if (this.maskingCallback != null)
          buffer = this.maskingCallback.display(buffer); 
        sb.append(insertSecondaryPrompts(new AttributedString(buffer), secondaryPrompts, false));
        List<AttributedString> promptLines = sb.columnSplitLength(this.size.getColumns(), false, this.display.delayLineWrap());
        if (!promptLines.isEmpty()) {
          cursorNewLinesId = promptLines.size() - 1;
          cursorColPos = ((AttributedString)promptLines.get(promptLines.size() - 1)).columnLength();
          cursorPos = this.size.cursorPos(cursorNewLinesId, cursorColPos);
        } 
      } 
      List<AttributedString> newLinesToDisplay = new ArrayList<>();
      int displaySize = displayRows(status);
      if (newLines.size() > displaySize && !isTerminalDumb()) {
        StringBuilder sb = new StringBuilder(">....");
        for (int j = sb.toString().length(); j < this.size.getColumns(); j++)
          sb.append(" "); 
        AttributedString partialCommandInfo = new AttributedString(sb.toString());
        int lineId = newLines.size() - displaySize + 1;
        int endId = displaySize;
        int startId = 1;
        if (lineId > cursorNewLinesId) {
          lineId = cursorNewLinesId;
          endId = displaySize - 1;
          startId = 0;
        } else {
          newLinesToDisplay.add(partialCommandInfo);
        } 
        int cursorRowPos = 0;
        for (int k = startId; k < endId; k++) {
          if (cursorNewLinesId == lineId)
            cursorRowPos = k; 
          newLinesToDisplay.add(newLines.get(lineId++));
        } 
        if (startId == 0)
          newLinesToDisplay.add(partialCommandInfo); 
        cursorPos = this.size.cursorPos(cursorRowPos, cursorColPos);
      } else {
        newLinesToDisplay = newLines;
      } 
      this.display.update(newLinesToDisplay, cursorPos, flush);
    } finally {
      this.lock.unlock();
    } 
  }
  
  private void concat(List<AttributedString> lines, AttributedStringBuilder sb) {
    if (lines.size() > 1)
      for (int i = 0; i < lines.size() - 1; i++) {
        sb.append(lines.get(i));
        sb.style(sb.style().inverse());
        sb.append("\\n");
        sb.style(sb.style().inverseOff());
      }  
    sb.append(lines.get(lines.size() - 1));
  }
  
  private String matchPreviousCommand(String buffer) {
    if (buffer.length() == 0)
      return ""; 
    History history = getHistory();
    StringBuilder sb = new StringBuilder();
    for (char c : buffer.replace("\\", "\\\\").toCharArray()) {
      if (c == '(' || c == ')' || c == '[' || c == ']' || c == '{' || c == '}' || c == '^' || c == '*' || c == '$' || c == '.' || c == '?' || c == '+')
        sb.append('\\'); 
      sb.append(c);
    } 
    Pattern pattern = Pattern.compile(sb.toString() + ".*", 32);
    Iterator<History.Entry> iter = history.reverseIterator(history.last());
    String suggestion = "";
    int tot = 0;
    while (iter.hasNext()) {
      History.Entry entry = iter.next();
      Matcher matcher = pattern.matcher(entry.line());
      if (matcher.matches()) {
        suggestion = entry.line().substring(buffer.length());
        break;
      } 
      if (tot > 200)
        break; 
      tot++;
    } 
    return suggestion;
  }
  
  public AttributedString getDisplayedBufferWithPrompts(List<AttributedString> secondaryPrompts) {
    AttributedString attBuf = getHighlightedBuffer(this.buf.toString());
    AttributedString tNewBuf = insertSecondaryPrompts(attBuf, secondaryPrompts);
    AttributedStringBuilder full = (new AttributedStringBuilder()).tabs(4);
    full.append(this.prompt);
    full.append(tNewBuf);
    if (this.doAutosuggestion && !isTerminalDumb()) {
      String lastBinding = (getLastBinding() != null) ? getLastBinding() : "";
      if (this.autosuggestion == LineReader.SuggestionType.HISTORY) {
        AttributedStringBuilder sb = new AttributedStringBuilder();
        this.tailTip = matchPreviousCommand(this.buf.toString());
        sb.styled(AttributedStyle::faint, this.tailTip);
        full.append(sb.toAttributedString());
      } else if (this.autosuggestion == LineReader.SuggestionType.COMPLETER) {
        if (this.buf.length() >= getInt("suggestions-min-buffer-size", 1) && this.buf
          .length() == this.buf.cursor() && (
          !lastBinding.equals("\t") || this.buf.prevChar() == 32 || this.buf.prevChar() == 61)) {
          clearChoices();
          listChoices(true);
        } else if (!lastBinding.equals("\t")) {
          clearChoices();
        } 
      } else if (this.autosuggestion == LineReader.SuggestionType.TAIL_TIP && 
        this.buf.length() == this.buf.cursor()) {
        if (!lastBinding.equals("\t") || this.buf.prevChar() == 32)
          clearChoices(); 
        AttributedStringBuilder sb = new AttributedStringBuilder();
        if (this.buf.prevChar() != 32)
          if (!this.tailTip.startsWith("[")) {
            int idx = this.tailTip.indexOf(' ');
            int idb = this.buf.toString().lastIndexOf(' ');
            int idd = this.buf.toString().lastIndexOf('-');
            if (idx > 0 && ((idb == -1 && idb == idd) || (idb >= 0 && idb > idd))) {
              this.tailTip = this.tailTip.substring(idx);
            } else if (idb >= 0 && idb < idd) {
              sb.append(" ");
            } 
          } else {
            sb.append(" ");
          }  
        sb.styled(AttributedStyle::faint, this.tailTip);
        full.append(sb.toAttributedString());
      } 
    } 
    if (this.post != null) {
      full.append("\n");
      full.append(this.post.get());
    } 
    this.doAutosuggestion = true;
    return full.toAttributedString();
  }
  
  private AttributedString getHighlightedBuffer(String buffer) {
    if (this.maskingCallback != null)
      buffer = this.maskingCallback.display(buffer); 
    if (this.highlighter != null && !isSet(LineReader.Option.DISABLE_HIGHLIGHTER) && buffer
      .length() < getInt("features-max-buffer-size", 1000))
      return this.highlighter.highlight(this, buffer); 
    return new AttributedString(buffer);
  }
  
  private AttributedString expandPromptPattern(String pattern, int padToWidth, String message, int line) {
    ArrayList<AttributedString> parts = new ArrayList<>();
    boolean isHidden = false;
    int padPartIndex = -1;
    StringBuilder padPartString = null;
    StringBuilder sb = new StringBuilder();
    pattern = pattern + "%{";
    int plen = pattern.length();
    int padChar = -1;
    int padPos = -1;
    int cols = 0;
    int i;
    label75: for (i = 0; i < plen; ) {
      char ch = pattern.charAt(i++);
      if (ch == '%' && i < plen) {
        int count = 0;
        boolean countSeen = false;
        while (true) {
          String str;
          AttributedString astr;
          boolean neg;
          ch = pattern.charAt(i++);
          switch (ch) {
            case '{':
            case '}':
              str = sb.toString();
              if (!isHidden) {
                astr = AttributedString.fromAnsi(str);
                cols += astr.columnLength();
              } else {
                astr = new AttributedString(str, AttributedStyle.HIDDEN);
              } 
              if (padPartIndex == parts.size()) {
                padPartString = sb;
                if (i < plen)
                  sb = new StringBuilder(); 
              } else {
                sb.setLength(0);
              } 
              parts.add(astr);
              isHidden = (ch == '{');
              continue label75;
            case '%':
              sb.append(ch);
              continue label75;
            case 'N':
              sb.append(getInt("line-offset", 0) + line);
              continue label75;
            case 'M':
              if (message != null) {
                sb.append(message);
                continue label75;
              } 
              continue label75;
            case 'P':
              if (countSeen && count >= 0)
                padToWidth = count; 
              if (i < plen)
                padChar = pattern.charAt(i++); 
              padPos = sb.length();
              padPartIndex = parts.size();
              continue label75;
            case '-':
            case '0':
            case '1':
            case '2':
            case '3':
            case '4':
            case '5':
            case '6':
            case '7':
            case '8':
            case '9':
              neg = false;
              if (ch == '-') {
                neg = true;
                ch = pattern.charAt(i++);
              } 
              countSeen = true;
              count = 0;
              while (ch >= '0' && ch <= '9') {
                count = ((count < 0) ? 0 : (10 * count)) + ch - 48;
                ch = pattern.charAt(i++);
              } 
              if (neg)
                count = -count; 
              i--;
              continue;
          } 
          continue label75;
        } 
      } 
      sb.append(ch);
    } 
    if (padToWidth > cols) {
      int padCharCols = WCWidth.wcwidth(padChar);
      int padCount = (padToWidth - cols) / padCharCols;
      sb = padPartString;
      while (--padCount >= 0)
        sb.insert(padPos, (char)padChar); 
      parts.set(padPartIndex, AttributedString.fromAnsi(sb.toString()));
    } 
    return AttributedString.join(null, parts);
  }
  
  private AttributedString insertSecondaryPrompts(AttributedString str, List<AttributedString> prompts) {
    return insertSecondaryPrompts(str, prompts, true);
  }
  
  private AttributedString insertSecondaryPrompts(AttributedString strAtt, List<AttributedString> prompts, boolean computePrompts) {
    Objects.requireNonNull(prompts);
    List<AttributedString> lines = strAtt.columnSplitLength(2147483647);
    AttributedStringBuilder sb = new AttributedStringBuilder();
    String secondaryPromptPattern = getString("secondary-prompt-pattern", "%M> ");
    boolean needsMessage = (secondaryPromptPattern.contains("%M") && strAtt.length() < getInt("features-max-buffer-size", 1000));
    AttributedStringBuilder buf = new AttributedStringBuilder();
    int width = 0;
    List<String> missings = new ArrayList<>();
    if (computePrompts && secondaryPromptPattern.contains("%P")) {
      width = this.prompt.columnLength();
      if (width > this.size.getColumns() || this.prompt.contains('\n'))
        width = (new TerminalLine(this.prompt.toString(), 0, this.size.getColumns())).getEndLine().length(); 
      for (int i = 0; i < lines.size() - 1; i++) {
        buf.append(lines.get(i)).append("\n");
        String missing = "";
        if (needsMessage)
          try {
            this.parser.parse(buf.toString(), buf.length(), Parser.ParseContext.SECONDARY_PROMPT);
          } catch (EOFError e) {
            missing = e.getMissing();
          } catch (SyntaxError syntaxError) {} 
        missings.add(missing);
        AttributedString prompt = expandPromptPattern(secondaryPromptPattern, 0, missing, i + 1);
        width = Math.max(width, prompt.columnLength());
      } 
      buf.setLength(0);
    } 
    int line = 0;
    while (line < lines.size() - 1) {
      AttributedString prompt;
      sb.append(lines.get(line)).append("\n");
      buf.append(lines.get(line)).append("\n");
      if (computePrompts) {
        String missing = "";
        if (needsMessage)
          if (missings.isEmpty()) {
            try {
              this.parser.parse(buf.toString(), buf.length(), Parser.ParseContext.SECONDARY_PROMPT);
            } catch (EOFError e) {
              missing = e.getMissing();
            } catch (SyntaxError syntaxError) {}
          } else {
            missing = missings.get(line);
          }  
        prompt = expandPromptPattern(secondaryPromptPattern, width, missing, line + 1);
      } else {
        prompt = prompts.get(line);
      } 
      prompts.add(prompt);
      sb.append(prompt);
      line++;
    } 
    sb.append(lines.get(line));
    buf.append(lines.get(line));
    return sb.toAttributedString();
  }
  
  private AttributedString addRightPrompt(AttributedString prompt, AttributedString line) {
    int width = prompt.columnLength();
    boolean endsWithNl = (line.length() > 0 && line.charAt(line.length() - 1) == '\n');
    int nb = this.size.getColumns() - width - line.columnLength() + (endsWithNl ? 1 : 0);
    if (nb >= 3) {
      AttributedStringBuilder sb = new AttributedStringBuilder(this.size.getColumns());
      sb.append(line, 0, endsWithNl ? (line.length() - 1) : line.length());
      for (int j = 0; j < nb; j++)
        sb.append(' '); 
      sb.append(prompt);
      if (endsWithNl)
        sb.append('\n'); 
      line = sb.toAttributedString();
    } 
    return line;
  }
  
  protected boolean insertTab() {
    return (isSet(LineReader.Option.INSERT_TAB) && 
      getLastBinding().equals("\t") && this.buf
      .toString().matches("(^|[\\s\\S]*\n)[\r\n\t ]*"));
  }
  
  protected boolean expandHistory() {
    String str = this.buf.toString();
    String exp = this.expander.expandHistory(this.history, str);
    if (!exp.equals(str)) {
      this.buf.clear();
      this.buf.write(exp);
      return true;
    } 
    return false;
  }
  
  protected enum CompletionType {
    Expand, ExpandComplete, Complete, List;
  }
  
  protected boolean expandWord() {
    if (insertTab())
      return selfInsert(); 
    return doComplete(CompletionType.Expand, isSet(LineReader.Option.MENU_COMPLETE), false);
  }
  
  protected boolean expandOrComplete() {
    if (insertTab())
      return selfInsert(); 
    return doComplete(CompletionType.ExpandComplete, isSet(LineReader.Option.MENU_COMPLETE), false);
  }
  
  protected boolean expandOrCompletePrefix() {
    if (insertTab())
      return selfInsert(); 
    return doComplete(CompletionType.ExpandComplete, isSet(LineReader.Option.MENU_COMPLETE), true);
  }
  
  protected boolean completeWord() {
    if (insertTab())
      return selfInsert(); 
    return doComplete(CompletionType.Complete, isSet(LineReader.Option.MENU_COMPLETE), false);
  }
  
  protected boolean menuComplete() {
    if (insertTab())
      return selfInsert(); 
    return doComplete(CompletionType.Complete, true, false);
  }
  
  protected boolean menuExpandOrComplete() {
    if (insertTab())
      return selfInsert(); 
    return doComplete(CompletionType.ExpandComplete, true, false);
  }
  
  protected boolean completePrefix() {
    if (insertTab())
      return selfInsert(); 
    return doComplete(CompletionType.Complete, isSet(LineReader.Option.MENU_COMPLETE), true);
  }
  
  protected boolean listChoices() {
    return listChoices(false);
  }
  
  private boolean listChoices(boolean forSuggestion) {
    return doComplete(CompletionType.List, isSet(LineReader.Option.MENU_COMPLETE), false, forSuggestion);
  }
  
  protected boolean deleteCharOrList() {
    if (this.buf.cursor() != this.buf.length() || this.buf.length() == 0)
      return deleteChar(); 
    return doComplete(CompletionType.List, isSet(LineReader.Option.MENU_COMPLETE), false);
  }
  
  protected boolean doComplete(CompletionType lst, boolean useMenu, boolean prefix) {
    return doComplete(lst, useMenu, prefix, false);
  }
  
  protected boolean doComplete(CompletionType lst, boolean useMenu, boolean prefix, boolean forSuggestion) {
    CompletingParsedLine line;
    if (getBoolean("disable-completion", false))
      return true; 
    if (!isSet(LineReader.Option.DISABLE_EVENT_EXPANSION))
      try {
        if (expandHistory())
          return true; 
      } catch (Exception e) {
        Log.info(new Object[] { "Error while expanding history", e });
        return false;
      }  
    try {
      line = wrap(this.parser.parse(this.buf.toString(), this.buf.cursor(), Parser.ParseContext.COMPLETE));
    } catch (Exception e) {
      Log.info(new Object[] { "Error while parsing line", e });
      return false;
    } 
    List<Candidate> candidates = new ArrayList<>();
    try {
      if (this.completer != null)
        this.completer.complete(this, (ParsedLine)line, candidates); 
    } catch (Exception e) {
      Log.info(new Object[] { "Error while finding completion candidates", e });
      if (Log.isDebugEnabled())
        e.printStackTrace(); 
      return false;
    } 
    if (lst == CompletionType.ExpandComplete || lst == CompletionType.Expand) {
      String w = this.expander.expandVar(line.word());
      if (!line.word().equals(w)) {
        if (prefix) {
          this.buf.backspace(line.wordCursor());
        } else {
          this.buf.move(line.word().length() - line.wordCursor());
          this.buf.backspace(line.word().length());
        } 
        this.buf.write(w);
        return true;
      } 
      if (lst == CompletionType.Expand)
        return false; 
      lst = CompletionType.Complete;
    } 
    boolean caseInsensitive = isSet(LineReader.Option.CASE_INSENSITIVE);
    int errors = getInt("errors", 2);
    this.completionMatcher.compile(this.options, prefix, line, caseInsensitive, errors, getOriginalGroupName());
    List<Candidate> possible = this.completionMatcher.matches(candidates);
    if (possible.isEmpty())
      return false; 
    this.size.copy(this.terminal.getSize());
    try {
      String current;
      if (lst == CompletionType.List) {
        Objects.requireNonNull(line);
        doList(possible, line.word(), false, line::escape, forSuggestion);
        return !possible.isEmpty();
      } 
      Candidate completion = null;
      if (possible.size() == 1) {
        completion = possible.get(0);
      } else if (isSet(LineReader.Option.RECOGNIZE_EXACT)) {
        completion = this.completionMatcher.exactMatch();
      } 
      if (completion != null && !completion.value().isEmpty()) {
        if (prefix) {
          this.buf.backspace(line.rawWordCursor());
        } else {
          this.buf.move(line.rawWordLength() - line.rawWordCursor());
          this.buf.backspace(line.rawWordLength());
        } 
        this.buf.write(line.escape(completion.value(), completion.complete()));
        if (completion.complete())
          if (this.buf.currChar() != 32) {
            this.buf.write(" ");
          } else {
            this.buf.move(1);
          }  
        if (completion.suffix() != null) {
          if (this.autosuggestion == LineReader.SuggestionType.COMPLETER)
            listChoices(true); 
          redisplay();
          Binding op = readBinding(getKeys());
          if (op != null) {
            String chars = getString("REMOVE_SUFFIX_CHARS", " \t\n;&|");
            String ref = (op instanceof Reference) ? ((Reference)op).name() : null;
            if (("self-insert".equals(ref) && chars.indexOf(getLastBinding().charAt(0)) >= 0) || "accept-line"
              .equals(ref)) {
              this.buf.backspace(completion.suffix().length());
              if (getLastBinding().charAt(0) != ' ')
                this.buf.write(32); 
            } 
            pushBackBinding(true);
          } 
        } 
        return true;
      } 
      if (useMenu) {
        this.buf.move(line.word().length() - line.wordCursor());
        this.buf.backspace(line.word().length());
        Objects.requireNonNull(line);
        doMenu(possible, line.word(), line::escape);
        return true;
      } 
      if (prefix) {
        current = line.word().substring(0, line.wordCursor());
      } else {
        current = line.word();
        this.buf.move(line.rawWordLength() - line.rawWordCursor());
      } 
      String commonPrefix = this.completionMatcher.getCommonPrefix();
      boolean hasUnambiguous = (commonPrefix.startsWith(current) && !commonPrefix.equals(current));
      if (hasUnambiguous) {
        this.buf.backspace(line.rawWordLength());
        this.buf.write(line.escape(commonPrefix, false));
        callWidget("redisplay");
        current = commonPrefix;
        if (((!isSet(LineReader.Option.AUTO_LIST) && isSet(LineReader.Option.AUTO_MENU)) || (
          isSet(LineReader.Option.AUTO_LIST) && isSet(LineReader.Option.LIST_AMBIGUOUS))) && 
          !nextBindingIsComplete())
          return true; 
      } 
      Objects.requireNonNull(line);
      if (isSet(LineReader.Option.AUTO_LIST) && !doList(possible, current, true, line::escape))
        return true; 
      if (isSet(LineReader.Option.AUTO_MENU)) {
        this.buf.backspace(current.length());
        Objects.requireNonNull(line);
        doMenu(possible, line.word(), line::escape);
      } 
      return true;
    } finally {
      this.size.copy(this.terminal.getBufferSize());
    } 
  }
  
  private CompletingParsedLine wrap(final ParsedLine line) {
    if (line instanceof CompletingParsedLine)
      return (CompletingParsedLine)line; 
    return new CompletingParsedLine() {
        public String word() {
          return line.word();
        }
        
        public int wordCursor() {
          return line.wordCursor();
        }
        
        public int wordIndex() {
          return line.wordIndex();
        }
        
        public List<String> words() {
          return line.words();
        }
        
        public String line() {
          return line.line();
        }
        
        public int cursor() {
          return line.cursor();
        }
        
        public CharSequence escape(CharSequence candidate, boolean complete) {
          return candidate;
        }
        
        public int rawWordCursor() {
          return wordCursor();
        }
        
        public int rawWordLength() {
          return word().length();
        }
      };
  }
  
  protected Comparator<Candidate> getCandidateComparator(boolean caseInsensitive, String word) {
    String wdi = caseInsensitive ? word.toLowerCase() : word;
    ToIntFunction<String> wordDistance = w -> ReaderUtils.distance(wdi, caseInsensitive ? w.toLowerCase() : w);
    return 
      Comparator.comparing(Candidate::value, Comparator.comparingInt(wordDistance))
      .thenComparing(Comparator.naturalOrder());
  }
  
  protected String getOthersGroupName() {
    return getString("OTHERS_GROUP_NAME", "others");
  }
  
  protected String getOriginalGroupName() {
    return getString("ORIGINAL_GROUP_NAME", "original");
  }
  
  protected Comparator<String> getGroupComparator() {
    return Comparator.comparingInt(s -> getOthersGroupName().equals(s) ? 1 : (getOriginalGroupName().equals(s) ? -1 : 0))
      .thenComparing(String::toLowerCase, Comparator.naturalOrder());
  }
  
  private void mergeCandidates(List<Candidate> possible) {
    Map<String, List<Candidate>> keyedCandidates = new HashMap<>();
    for (Candidate candidate : possible) {
      if (candidate.key() != null) {
        List<Candidate> cands = keyedCandidates.computeIfAbsent(candidate.key(), s -> new ArrayList());
        cands.add(candidate);
      } 
    } 
    if (!keyedCandidates.isEmpty())
      for (List<Candidate> candidates : keyedCandidates.values()) {
        if (candidates.size() >= 1) {
          possible.removeAll(candidates);
          candidates.sort(Comparator.comparing(Candidate::value));
          Candidate first = candidates.get(0);
          String disp = candidates.stream().map(Candidate::displ).collect(Collectors.joining(" "));
          possible.add(new Candidate(first.value(), disp, first.group(), first
                .descr(), first.suffix(), null, first.complete()));
        } 
      }  
  }
  
  protected boolean nextBindingIsComplete() {
    redisplay();
    KeyMap<Binding> keyMap = this.keyMaps.get("menu");
    Binding operation = readBinding(getKeys(), keyMap);
    if (operation instanceof Reference && "menu-complete".equals(((Reference)operation).name()))
      return true; 
    pushBackBinding();
    return false;
  }
  
  private int displayRows() {
    return displayRows(Status.getStatus(this.terminal, false));
  }
  
  private int displayRows(Status status) {
    return this.size.getRows() - ((status != null) ? status.size() : 0);
  }
  
  private int promptLines() {
    AttributedString text = insertSecondaryPrompts(AttributedStringBuilder.append(new CharSequence[] { (CharSequence)this.prompt, this.buf.toString() }, ), new ArrayList<>());
    return text.columnSplitLength(this.size.getColumns(), false, this.display.delayLineWrap()).size();
  }
  
  private class MenuSupport implements Supplier<AttributedString> {
    final List<Candidate> possible;
    
    final BiFunction<CharSequence, Boolean, CharSequence> escaper;
    
    int selection;
    
    int topLine;
    
    String word;
    
    AttributedString computed;
    
    int lines;
    
    int columns;
    
    String completed;
    
    public MenuSupport(List<Candidate> original, String completed, BiFunction<CharSequence, Boolean, CharSequence> escaper) {
      this.possible = new ArrayList<>();
      this.escaper = escaper;
      this.selection = -1;
      this.topLine = 0;
      this.word = "";
      this.completed = completed;
      LineReaderImpl.this.computePost(original, null, this.possible, completed);
      next();
    }
    
    public Candidate completion() {
      return this.possible.get(this.selection);
    }
    
    public void next() {
      this.selection = (this.selection + 1) % this.possible.size();
      update();
    }
    
    public void previous() {
      this.selection = (this.selection + this.possible.size() - 1) % this.possible.size();
      update();
    }
    
    private void major(int step) {
      int axis = LineReaderImpl.this.isSet(LineReader.Option.LIST_ROWS_FIRST) ? this.columns : this.lines;
      int sel = this.selection + step * axis;
      if (sel < 0) {
        int pos = (sel + axis) % axis;
        int remainders = this.possible.size() % axis;
        sel = this.possible.size() - remainders + pos;
        if (sel >= this.possible.size())
          sel -= axis; 
      } else if (sel >= this.possible.size()) {
        sel %= axis;
      } 
      this.selection = sel;
      update();
    }
    
    private void minor(int step) {
      int axis = LineReaderImpl.this.isSet(LineReader.Option.LIST_ROWS_FIRST) ? this.columns : this.lines;
      int row = this.selection % axis;
      int options = this.possible.size();
      if (this.selection - row + axis > options)
        axis = options % axis; 
      this.selection = this.selection - row + (axis + row + step) % axis;
      update();
    }
    
    public void up() {
      if (LineReaderImpl.this.isSet(LineReader.Option.LIST_ROWS_FIRST)) {
        major(-1);
      } else {
        minor(-1);
      } 
    }
    
    public void down() {
      if (LineReaderImpl.this.isSet(LineReader.Option.LIST_ROWS_FIRST)) {
        major(1);
      } else {
        minor(1);
      } 
    }
    
    public void left() {
      if (LineReaderImpl.this.isSet(LineReader.Option.LIST_ROWS_FIRST)) {
        minor(-1);
      } else {
        major(-1);
      } 
    }
    
    public void right() {
      if (LineReaderImpl.this.isSet(LineReader.Option.LIST_ROWS_FIRST)) {
        minor(1);
      } else {
        major(1);
      } 
    }
    
    private void update() {
      LineReaderImpl.this.buf.backspace(this.word.length());
      this.word = ((CharSequence)this.escaper.apply(completion().value(), Boolean.valueOf(true))).toString();
      LineReaderImpl.this.buf.write(this.word);
      LineReaderImpl.PostResult pr = LineReaderImpl.this.computePost(this.possible, completion(), null, this.completed);
      int displaySize = LineReaderImpl.this.displayRows() - LineReaderImpl.this.promptLines();
      if (pr.lines > displaySize) {
        int displayed = displaySize - 1;
        if (pr.selectedLine >= 0)
          if (pr.selectedLine < this.topLine) {
            this.topLine = pr.selectedLine;
          } else if (pr.selectedLine >= this.topLine + displayed) {
            this.topLine = pr.selectedLine - displayed + 1;
          }  
        AttributedString post = pr.post;
        if (post.length() > 0 && post.charAt(post.length() - 1) != '\n')
          post = (new AttributedStringBuilder(post.length() + 1)).append(post).append("\n").toAttributedString(); 
        List<AttributedString> lines = post.columnSplitLength(LineReaderImpl.this.size.getColumns(), true, LineReaderImpl.this.display.delayLineWrap());
        List<AttributedString> sub = new ArrayList<>(lines.subList(this.topLine, this.topLine + displayed));
        sub.add((new AttributedStringBuilder())
            .style(AttributedStyle.DEFAULT.foreground(6))
            .append("rows ")
            .append(Integer.toString(this.topLine + 1))
            .append(" to ")
            .append(Integer.toString(this.topLine + displayed))
            .append(" of ")
            .append(Integer.toString(lines.size()))
            .append("\n")
            .style(AttributedStyle.DEFAULT).toAttributedString());
        this.computed = AttributedString.join(AttributedString.EMPTY, sub);
      } else {
        this.computed = pr.post;
      } 
      this.lines = pr.lines;
      this.columns = (this.possible.size() + this.lines - 1) / this.lines;
    }
    
    public AttributedString get() {
      return this.computed;
    }
  }
  
  protected boolean doMenu(List<Candidate> original, String completed, BiFunction<CharSequence, Boolean, CharSequence> escaper) {
    List<Candidate> possible = new ArrayList<>();
    boolean caseInsensitive = isSet(LineReader.Option.CASE_INSENSITIVE);
    original.sort(getCandidateComparator(caseInsensitive, completed));
    mergeCandidates(original);
    computePost(original, null, possible, completed);
    boolean defaultAutoGroup = isSet(LineReader.Option.AUTO_GROUP);
    boolean defaultGroup = isSet(LineReader.Option.GROUP);
    if (!isSet(LineReader.Option.GROUP_PERSIST)) {
      option(LineReader.Option.AUTO_GROUP, false);
      option(LineReader.Option.GROUP, false);
    } 
    MenuSupport menuSupport = new MenuSupport(original, completed, escaper);
    this.post = menuSupport;
    callWidget("redisplay");
    KeyMap<Binding> keyMap = this.keyMaps.get("menu");
    Binding operation;
    while ((operation = readBinding(getKeys(), keyMap)) != null) {
      Candidate completion;
      String ref = (operation instanceof Reference) ? ((Reference)operation).name() : "";
      switch (ref) {
        case "menu-complete":
          menuSupport.next();
          break;
        case "reverse-menu-complete":
          menuSupport.previous();
          break;
        case "up-line-or-history":
        case "up-line-or-search":
          menuSupport.up();
          break;
        case "down-line-or-history":
        case "down-line-or-search":
          menuSupport.down();
          break;
        case "forward-char":
          menuSupport.right();
          break;
        case "backward-char":
          menuSupport.left();
          break;
        case "clear-screen":
          clearScreen();
          break;
        default:
          completion = menuSupport.completion();
          if (completion.suffix() != null) {
            String chars = getString("REMOVE_SUFFIX_CHARS", " \t\n;&|");
            if (("self-insert".equals(ref) && chars
              .indexOf(getLastBinding().charAt(0)) >= 0) || "backward-delete-char"
              .equals(ref))
              this.buf.backspace(completion.suffix().length()); 
          } 
          if (completion.complete() && 
            getLastBinding().charAt(0) != ' ' && ("self-insert"
            .equals(ref) || getLastBinding().charAt(0) != ' '))
            this.buf.write(32); 
          if (!"accept-line".equals(ref) && (
            !"self-insert".equals(ref) || completion
            .suffix() == null || 
            !completion.suffix().startsWith(getLastBinding())))
            pushBackBinding(true); 
          this.post = null;
          option(LineReader.Option.AUTO_GROUP, defaultAutoGroup);
          option(LineReader.Option.GROUP, defaultGroup);
          return true;
      } 
      this.doAutosuggestion = false;
      callWidget("redisplay");
    } 
    option(LineReader.Option.AUTO_GROUP, defaultAutoGroup);
    option(LineReader.Option.GROUP, defaultGroup);
    return false;
  }
  
  protected boolean clearChoices() {
    return doList(new ArrayList<>(), "", false, null, false);
  }
  
  protected boolean doList(List<Candidate> possible, String completed, boolean runLoop, BiFunction<CharSequence, Boolean, CharSequence> escaper) {
    return doList(possible, completed, runLoop, escaper, false);
  }
  
  protected boolean doList(List<Candidate> possible, String completed, boolean runLoop, BiFunction<CharSequence, Boolean, CharSequence> escaper, boolean forSuggestion) {
    mergeCandidates(possible);
    AttributedString text = insertSecondaryPrompts(AttributedStringBuilder.append(new CharSequence[] { (CharSequence)this.prompt, this.buf.toString() }, ), new ArrayList<>());
    int promptLines = text.columnSplitLength(this.size.getColumns(), false, this.display.delayLineWrap()).size();
    PostResult postResult = computePost(possible, null, null, completed);
    int lines = postResult.lines;
    int listMax = getInt("list-max", 100);
    if ((listMax > 0 && possible.size() >= listMax) || lines >= this.size
      .getRows() - promptLines)
      if (!forSuggestion) {
        this.post = (() -> new AttributedString(getAppName() + ": do you wish to see all " + possible.size() + " possibilities (" + lines + " lines)?"));
        redisplay(true);
        int c = readCharacter();
        if (c != 121 && c != 89 && c != 9) {
          this.post = null;
          return false;
        } 
      } else {
        return false;
      }  
    boolean caseInsensitive = isSet(LineReader.Option.CASE_INSENSITIVE);
    StringBuilder sb = new StringBuilder();
    this.candidateStartPosition = 0;
    while (true) {
      List<Candidate> cands;
      String current = completed + sb.toString();
      if (sb.length() > 0) {
        this.completionMatcher.compile(this.options, false, new CompletingWord(current), caseInsensitive, 0, null);
        cands = (List<Candidate>)this.completionMatcher.matches(possible).stream().sorted(getCandidateComparator(caseInsensitive, current)).collect(Collectors.toList());
      } else {
        cands = (List<Candidate>)possible.stream().sorted(getCandidateComparator(caseInsensitive, current)).collect(Collectors.toList());
      } 
      if (isSet(LineReader.Option.AUTO_MENU_LIST) && this.candidateStartPosition == 0)
        this.candidateStartPosition = candidateStartPosition(cands); 
      this.post = (() -> {
          AttributedString t = insertSecondaryPrompts(AttributedStringBuilder.append(new CharSequence[] { (CharSequence)this.prompt, this.buf.toString() }, ), new ArrayList<>());
          int pl = t.columnSplitLength(this.size.getColumns(), false, this.display.delayLineWrap()).size();
          PostResult pr = computePost(cands, null, null, current);
          if (pr.lines >= this.size.getRows() - pl) {
            this.post = null;
            int oldCursor = this.buf.cursor();
            this.buf.cursor(this.buf.length());
            redisplay(false);
            this.buf.cursor(oldCursor);
            println();
            List<AttributedString> ls = pr.post.columnSplitLength(this.size.getColumns(), false, this.display.delayLineWrap());
            Display d = new Display(this.terminal, false);
            d.resize(this.size.getRows(), this.size.getColumns());
            d.update(ls, -1);
            println();
            redrawLine();
            return new AttributedString("");
          } 
          return pr.post;
        });
      if (!runLoop)
        return false; 
      redisplay();
      Binding b = doReadBinding(getKeys(), null);
      if (b instanceof Reference) {
        String name = ((Reference)b).name();
        if ("backward-delete-char".equals(name) || "vi-backward-delete-char".equals(name)) {
          if (sb.length() == 0) {
            pushBackBinding();
            this.post = null;
            return false;
          } 
          sb.setLength(sb.length() - 1);
          this.buf.backspace();
          continue;
        } 
        if ("self-insert".equals(name)) {
          sb.append(getLastBinding());
          callWidget(name);
          if (cands.isEmpty()) {
            this.post = null;
            return false;
          } 
          continue;
        } 
        if ("\t".equals(getLastBinding())) {
          if (cands.size() == 1 || sb.length() > 0) {
            this.post = null;
            pushBackBinding();
          } else if (isSet(LineReader.Option.AUTO_MENU)) {
            this.buf.backspace(((CharSequence)escaper.apply(current, Boolean.valueOf(false))).length());
            doMenu(cands, current, escaper);
          } 
          return false;
        } 
        pushBackBinding();
        this.post = null;
        return false;
      } 
      if (b == null) {
        this.post = null;
        return false;
      } 
    } 
  }
  
  private static class CompletingWord implements CompletingParsedLine {
    private final String word;
    
    public CompletingWord(String word) {
      this.word = word;
    }
    
    public CharSequence escape(CharSequence candidate, boolean complete) {
      return null;
    }
    
    public int rawWordCursor() {
      return this.word.length();
    }
    
    public int rawWordLength() {
      return this.word.length();
    }
    
    public String word() {
      return this.word;
    }
    
    public int wordCursor() {
      return this.word.length();
    }
    
    public int wordIndex() {
      return 0;
    }
    
    public List<String> words() {
      return null;
    }
    
    public String line() {
      return this.word;
    }
    
    public int cursor() {
      return this.word.length();
    }
  }
  
  protected static class PostResult {
    final AttributedString post;
    
    final int lines;
    
    final int selectedLine;
    
    public PostResult(AttributedString post, int lines, int selectedLine) {
      this.post = post;
      this.lines = lines;
      this.selectedLine = selectedLine;
    }
  }
  
  protected PostResult computePost(List<Candidate> possible, Candidate selection, List<Candidate> ordered, String completed) {
    Objects.requireNonNull(this.display);
    return computePost(possible, selection, ordered, completed, this.display::wcwidth, this.size.getColumns(), isSet(LineReader.Option.AUTO_GROUP), isSet(LineReader.Option.GROUP), isSet(LineReader.Option.LIST_ROWS_FIRST));
  }
  
  protected PostResult computePost(List<Candidate> possible, Candidate selection, List<Candidate> ordered, String completed, Function<String, Integer> wcwidth, int width, boolean autoGroup, boolean groupName, boolean rowsFirst) {
    List<Object> strings = new ArrayList();
    if (groupName) {
      Comparator<String> groupComparator = getGroupComparator();
      Map<String, Map<String, Candidate>> sorted = (groupComparator != null) ? new TreeMap<>(groupComparator) : new LinkedHashMap<>();
      for (Candidate cand : possible) {
        String group = cand.group();
        ((Map<String, Candidate>)sorted.computeIfAbsent((group != null) ? group : "", s -> new LinkedHashMap<>()))
          .put(cand.value(), cand);
      } 
      for (Map.Entry<String, Map<String, Candidate>> entry : sorted.entrySet()) {
        String group = entry.getKey();
        if (group.isEmpty() && sorted.size() > 1)
          group = getOthersGroupName(); 
        if (!group.isEmpty() && autoGroup)
          strings.add(group); 
        strings.add(new ArrayList(((Map)entry.getValue()).values()));
        if (ordered != null)
          ordered.addAll(((Map)entry.getValue()).values()); 
      } 
    } else {
      Set<String> groups = new LinkedHashSet<>();
      TreeMap<String, Candidate> sorted = new TreeMap<>();
      for (Candidate cand : possible) {
        String group = cand.group();
        if (group != null)
          groups.add(group); 
        sorted.put(cand.value(), cand);
      } 
      if (autoGroup)
        strings.addAll(groups); 
      strings.add(new ArrayList(sorted.values()));
      if (ordered != null)
        ordered.addAll(sorted.values()); 
    } 
    return toColumns(strings, selection, completed, wcwidth, width, rowsFirst);
  }
  
  private static class TerminalLine {
    private String endLine;
    
    private int startPos;
    
    public TerminalLine(String line, int startPos, int width) {
      this.startPos = startPos;
      this.endLine = line.substring(line.lastIndexOf('\n') + 1);
      boolean first = true;
      while (this.endLine.length() + (first ? startPos : 0) > width) {
        if (first) {
          this.endLine = this.endLine.substring(width - startPos);
        } else {
          this.endLine = this.endLine.substring(width);
        } 
        first = false;
      } 
      if (!first)
        this.startPos = 0; 
    }
    
    public int getStartPos() {
      return this.startPos;
    }
    
    public String getEndLine() {
      return this.endLine;
    }
  }
  
  private int candidateStartPosition(List<Candidate> cands) {
    List<String> values = (List<String>)cands.stream().map(c -> AttributedString.stripAnsi(c.displ())).filter(c -> (!c.matches("\\w+") && c.length() > 1)).collect(Collectors.toList());
    Set<String> notDelimiters = new HashSet<>();
    values.forEach(v -> v.substring(0, v.length() - 1).chars().filter(()).forEach(()));
    int width = this.size.getColumns();
    int promptLength = (this.prompt != null) ? this.prompt.length() : 0;
    if (promptLength > 0) {
      TerminalLine tp = new TerminalLine(this.prompt.toString(), 0, width);
      promptLength = tp.getEndLine().length();
    } 
    TerminalLine tl = new TerminalLine(this.buf.substring(0, this.buf.cursor()), promptLength, width);
    int out = tl.getStartPos();
    String buffer = tl.getEndLine();
    for (int i = buffer.length(); i > 0; i--) {
      if (buffer.substring(0, i).matches(".*\\W") && 
        !notDelimiters.contains(buffer.substring(i - 1, i))) {
        out += i;
        break;
      } 
    } 
    return out;
  }
  
  protected PostResult toColumns(List<Object> items, Candidate selection, String completed, Function<String, Integer> wcwidth, int width, boolean rowsFirst) {
    int[] out = new int[2];
    int maxWidth = 0;
    int listSize = 0;
    for (Object item : items) {
      if (item instanceof String) {
        int len = ((Integer)wcwidth.apply((String)item)).intValue();
        maxWidth = Math.max(maxWidth, len);
        continue;
      } 
      if (item instanceof List)
        for (Candidate cand : item) {
          listSize++;
          int len = ((Integer)wcwidth.apply(cand.displ())).intValue();
          if (cand.descr() != null) {
            len++;
            len += "(".length();
            len += ((Integer)wcwidth.apply(cand.descr())).intValue();
            len += ")".length();
          } 
          maxWidth = Math.max(maxWidth, len);
        }  
    } 
    AttributedStringBuilder sb = new AttributedStringBuilder();
    if (listSize > 0)
      if (isSet(LineReader.Option.AUTO_MENU_LIST) && listSize < 
        Math.min(getInt("menu-list-max", 2147483647), displayRows() - promptLines())) {
        maxWidth = Math.max(maxWidth, 25);
        sb.tabs(Math.max(Math.min(this.candidateStartPosition, width - maxWidth - 1), 1));
        width = maxWidth + 2;
        if (!isSet(LineReader.Option.GROUP_PERSIST)) {
          List<Candidate> list = new ArrayList<>();
          for (Object o : items) {
            if (o instanceof Collection)
              list.addAll((Collection<? extends Candidate>)o); 
          } 
          list = (List<Candidate>)list.stream().sorted(getCandidateComparator(isSet(LineReader.Option.CASE_INSENSITIVE), "")).collect(Collectors.toList());
          toColumns(list, width, maxWidth, sb, selection, completed, rowsFirst, true, out);
        } else {
          for (Object list : items)
            toColumns(list, width, maxWidth, sb, selection, completed, rowsFirst, true, out); 
        } 
      } else {
        for (Object list : items)
          toColumns(list, width, maxWidth, sb, selection, completed, rowsFirst, false, out); 
      }  
    if (sb.length() > 0 && sb.charAt(sb.length() - 1) == '\n')
      sb.setLength(sb.length() - 1); 
    return new PostResult(sb.toAttributedString(), out[0], out[1]);
  }
  
  protected void toColumns(Object items, int width, int maxWidth, AttributedStringBuilder sb, Candidate selection, String completed, boolean rowsFirst, boolean doMenuList, int[] out) {
    if (maxWidth <= 0 || width <= 0)
      return; 
    if (items instanceof String) {
      if (doMenuList) {
        sb.style(AttributedStyle.DEFAULT);
        sb.append('\t');
      } 
      AttributedStringBuilder asb = new AttributedStringBuilder();
      asb.style(getCompletionStyleGroup(doMenuList))
        .append((String)items)
        .style(AttributedStyle.DEFAULT);
      if (doMenuList)
        for (int k = ((String)items).length(); k < maxWidth + 1; k++)
          asb.append(' ');  
      sb.style(getCompletionStyleBackground(doMenuList));
      sb.append((AttributedCharSequence)asb);
      sb.append("\n");
      out[0] = out[0] + 1;
    } else if (items instanceof List) {
      IntBinaryOperator index;
      List<Candidate> candidates = (List<Candidate>)items;
      maxWidth = Math.min(width, maxWidth);
      int c = width / maxWidth;
      while (c > 1 && c * maxWidth + (c - 1) * 3 >= width)
        c--; 
      int lines = (candidates.size() + c - 1) / c;
      int columns = (candidates.size() + lines - 1) / lines;
      if (rowsFirst) {
        index = ((i, j) -> i * columns + j);
      } else {
        index = ((i, j) -> j * lines + i);
      } 
      for (int i = 0; i < lines; i++) {
        if (doMenuList) {
          sb.style(AttributedStyle.DEFAULT);
          sb.append('\t');
        } 
        AttributedStringBuilder asb = new AttributedStringBuilder();
        for (int j = 0; j < columns; j++) {
          int idx = index.applyAsInt(i, j);
          if (idx < candidates.size()) {
            Candidate cand = candidates.get(idx);
            boolean hasRightItem = (j < columns - 1 && index.applyAsInt(i, j + 1) < candidates.size());
            AttributedString left = AttributedString.fromAnsi(cand.displ());
            AttributedString right = AttributedString.fromAnsi(cand.descr());
            int lw = left.columnLength();
            int rw = 0;
            if (right != null) {
              int rem = maxWidth - lw + 1 + "(".length() + ")".length();
              rw = right.columnLength();
              if (rw > rem) {
                right = AttributedStringBuilder.append(new CharSequence[] { (CharSequence)right
                      .columnSubSequence(0, rem - WCWidth.wcwidth(8230)), "…" });
                rw = right.columnLength();
              } 
              right = AttributedStringBuilder.append(new CharSequence[] { "(", (CharSequence)right, ")" });
              rw += "(".length() + ")".length();
            } 
            if (cand == selection) {
              out[1] = i;
              asb.style(getCompletionStyleSelection(doMenuList));
              if (left.toString().regionMatches(
                  isSet(LineReader.Option.CASE_INSENSITIVE), 0, completed, 0, completed.length())) {
                asb.append(left.toString(), 0, completed.length());
                asb.append(left.toString(), completed.length(), left.length());
              } else {
                asb.append(left.toString());
              } 
              for (int k = 0; k < maxWidth - lw - rw; k++)
                asb.append(' '); 
              if (right != null)
                asb.append(right); 
              asb.style(AttributedStyle.DEFAULT);
            } else {
              if (left.toString().regionMatches(
                  isSet(LineReader.Option.CASE_INSENSITIVE), 0, completed, 0, completed.length())) {
                asb.style(getCompletionStyleStarting(doMenuList));
                asb.append(left, 0, completed.length());
                asb.style(AttributedStyle.DEFAULT);
                asb.append(left, completed.length(), left.length());
              } else {
                asb.append(left);
              } 
              if (right != null || hasRightItem)
                for (int k = 0; k < maxWidth - lw - rw; k++)
                  asb.append(' ');  
              if (right != null) {
                asb.style(getCompletionStyleDescription(doMenuList));
                asb.append(right);
                asb.style(AttributedStyle.DEFAULT);
              } else if (doMenuList) {
                for (int k = lw; k < maxWidth; k++)
                  asb.append(' '); 
              } 
            } 
            if (hasRightItem)
              for (int k = 0; k < 3; k++)
                asb.append(' ');  
            if (doMenuList)
              asb.append(' '); 
          } 
        } 
        sb.style(getCompletionStyleBackground(doMenuList));
        sb.append((AttributedCharSequence)asb);
        sb.append('\n');
      } 
      out[0] = out[0] + lines;
    } 
  }
  
  protected AttributedStyle getCompletionStyleStarting(boolean menuList) {
    return menuList ? getCompletionStyleListStarting() : getCompletionStyleStarting();
  }
  
  protected AttributedStyle getCompletionStyleDescription(boolean menuList) {
    return menuList ? getCompletionStyleListDescription() : getCompletionStyleDescription();
  }
  
  protected AttributedStyle getCompletionStyleGroup(boolean menuList) {
    return menuList ? getCompletionStyleListGroup() : getCompletionStyleGroup();
  }
  
  protected AttributedStyle getCompletionStyleSelection(boolean menuList) {
    return menuList ? getCompletionStyleListSelection() : getCompletionStyleSelection();
  }
  
  protected AttributedStyle getCompletionStyleBackground(boolean menuList) {
    return menuList ? getCompletionStyleListBackground() : getCompletionStyleBackground();
  }
  
  protected AttributedStyle getCompletionStyleStarting() {
    return getCompletionStyle("COMPLETION_STYLE_STARTING", "fg:cyan");
  }
  
  protected AttributedStyle getCompletionStyleDescription() {
    return getCompletionStyle("COMPLETION_STYLE_DESCRIPTION", "fg:bright-black");
  }
  
  protected AttributedStyle getCompletionStyleGroup() {
    return getCompletionStyle("COMPLETION_STYLE_GROUP", "fg:bright-magenta,bold");
  }
  
  protected AttributedStyle getCompletionStyleSelection() {
    return getCompletionStyle("COMPLETION_STYLE_SELECTION", "inverse");
  }
  
  protected AttributedStyle getCompletionStyleBackground() {
    return getCompletionStyle("COMPLETION_STYLE_BACKGROUND", "bg:default");
  }
  
  protected AttributedStyle getCompletionStyleListStarting() {
    return getCompletionStyle("COMPLETION_STYLE_LIST_STARTING", "fg:cyan");
  }
  
  protected AttributedStyle getCompletionStyleListDescription() {
    return getCompletionStyle("COMPLETION_STYLE_LIST_DESCRIPTION", "fg:bright-black");
  }
  
  protected AttributedStyle getCompletionStyleListGroup() {
    return getCompletionStyle("COMPLETION_STYLE_LIST_GROUP", "fg:black,bold");
  }
  
  protected AttributedStyle getCompletionStyleListSelection() {
    return getCompletionStyle("COMPLETION_STYLE_LIST_SELECTION", "inverse");
  }
  
  protected AttributedStyle getCompletionStyleListBackground() {
    return getCompletionStyle("COMPLETION_STYLE_LIST_BACKGROUND", "bg:bright-magenta");
  }
  
  protected AttributedStyle getCompletionStyle(String name, String value) {
    return (new StyleResolver(s -> getString(s, null))).resolve("." + name, value);
  }
  
  protected AttributedStyle buildStyle(String str) {
    return AttributedString.fromAnsi("\033[" + str + "m ").styleAt(0);
  }
  
  protected boolean moveHistory(boolean next, int count) {
    boolean ok = true;
    for (int i = 0; i < count && (ok = moveHistory(next)); i++);
    return ok;
  }
  
  protected boolean moveHistory(boolean next) {
    if (!this.buf.toString().equals(this.history.current()))
      this.modifiedHistory.put(Integer.valueOf(this.history.index()), this.buf.toString()); 
    if (next && !this.history.next())
      return false; 
    if (!next && !this.history.previous())
      return false; 
    setBuffer(this.modifiedHistory.containsKey(Integer.valueOf(this.history.index())) ? 
        this.modifiedHistory.get(Integer.valueOf(this.history.index())) : 
        this.history.current());
    return true;
  }
  
  void print(String str) {
    this.terminal.writer().write(str);
  }
  
  void println(String s) {
    print(s);
    println();
  }
  
  void println() {
    this.terminal.puts(InfoCmp.Capability.carriage_return, new Object[0]);
    print("\n");
    redrawLine();
  }
  
  protected boolean killBuffer() {
    this.killRing.add(this.buf.toString());
    this.buf.clear();
    return true;
  }
  
  protected boolean killWholeLine() {
    int start, end;
    if (this.buf.length() == 0)
      return false; 
    if (this.count < 0) {
      end = this.buf.cursor();
      while (this.buf.atChar(end) != 0 && this.buf.atChar(end) != 10)
        end++; 
      start = end;
      for (int count = -this.count; count > 0; count--) {
        while (start > 0 && this.buf.atChar(start - 1) != 10)
          start--; 
        start--;
      } 
    } else {
      start = this.buf.cursor();
      while (start > 0 && this.buf.atChar(start - 1) != 10)
        start--; 
      end = start;
      while (this.count-- > 0) {
        while (end < this.buf.length() && this.buf.atChar(end) != 10)
          end++; 
        if (end < this.buf.length())
          end++; 
      } 
    } 
    String killed = this.buf.substring(start, end);
    this.buf.cursor(start);
    this.buf.delete(end - start);
    this.killRing.add(killed);
    return true;
  }
  
  public boolean killLine() {
    if (this.count < 0)
      return callNeg(this::backwardKillLine); 
    if (this.buf.cursor() == this.buf.length())
      return false; 
    int cp = this.buf.cursor();
    int len = cp;
    while (this.count-- > 0) {
      if (this.buf.atChar(len) == 10) {
        len++;
        continue;
      } 
      while (this.buf.atChar(len) != 0 && this.buf.atChar(len) != 10)
        len++; 
    } 
    int num = len - cp;
    String killed = this.buf.substring(cp, cp + num);
    this.buf.delete(num);
    this.killRing.add(killed);
    return true;
  }
  
  public boolean backwardKillLine() {
    if (this.count < 0)
      return callNeg(this::killLine); 
    if (this.buf.cursor() == 0)
      return false; 
    int cp = this.buf.cursor();
    int beg = cp;
    while (this.count-- > 0 && 
      beg != 0) {
      if (this.buf.atChar(beg - 1) == 10) {
        beg--;
        continue;
      } 
      while (beg > 0 && this.buf.atChar(beg - 1) != 0 && this.buf.atChar(beg - 1) != 10)
        beg--; 
    } 
    int num = cp - beg;
    String killed = this.buf.substring(cp - beg, cp);
    this.buf.cursor(beg);
    this.buf.delete(num);
    this.killRing.add(killed);
    return true;
  }
  
  public boolean killRegion() {
    return doCopyKillRegion(true);
  }
  
  public boolean copyRegionAsKill() {
    return doCopyKillRegion(false);
  }
  
  private boolean doCopyKillRegion(boolean kill) {
    if (this.regionMark > this.buf.length())
      this.regionMark = this.buf.length(); 
    if (this.regionActive == LineReader.RegionType.LINE) {
      int start = this.regionMark;
      int end = this.buf.cursor();
      if (start < end) {
        while (start > 0 && this.buf.atChar(start - 1) != 10)
          start--; 
        while (end < this.buf.length() - 1 && this.buf.atChar(end + 1) != 10)
          end++; 
        if (isInViCmdMode())
          end++; 
        this.killRing.add(this.buf.substring(start, end));
        if (kill)
          this.buf.backspace(end - start); 
      } else {
        while (end > 0 && this.buf.atChar(end - 1) != 10)
          end--; 
        while (start < this.buf.length() && this.buf.atChar(start) != 10)
          start++; 
        if (isInViCmdMode())
          start++; 
        this.killRing.addBackwards(this.buf.substring(end, start));
        if (kill) {
          this.buf.cursor(end);
          this.buf.delete(start - end);
        } 
      } 
    } else if (this.regionMark > this.buf.cursor()) {
      if (isInViCmdMode())
        this.regionMark++; 
      this.killRing.add(this.buf.substring(this.buf.cursor(), this.regionMark));
      if (kill)
        this.buf.delete(this.regionMark - this.buf.cursor()); 
    } else {
      if (isInViCmdMode())
        this.buf.move(1); 
      this.killRing.add(this.buf.substring(this.regionMark, this.buf.cursor()));
      if (kill)
        this.buf.backspace(this.buf.cursor() - this.regionMark); 
    } 
    if (kill)
      this.regionActive = LineReader.RegionType.NONE; 
    return true;
  }
  
  public boolean yank() {
    String yanked = this.killRing.yank();
    if (yanked == null)
      return false; 
    putString(yanked);
    return true;
  }
  
  public boolean yankPop() {
    if (!this.killRing.lastYank())
      return false; 
    String current = this.killRing.yank();
    if (current == null)
      return false; 
    this.buf.backspace(current.length());
    String yanked = this.killRing.yankPop();
    if (yanked == null)
      return false; 
    putString(yanked);
    return true;
  }
  
  public boolean mouse() {
    MouseEvent event = readMouseEvent();
    if (event.getType() == MouseEvent.Type.Released && event
      .getButton() == MouseEvent.Button.Button1) {
      StringBuilder tsb = new StringBuilder();
      Cursor cursor = this.terminal.getCursorPosition(c -> tsb.append((char)c));
      this.bindingReader.runMacro(tsb.toString());
      List<AttributedString> secondaryPrompts = new ArrayList<>();
      getDisplayedBufferWithPrompts(secondaryPrompts);
      AttributedStringBuilder sb = (new AttributedStringBuilder()).tabs(4);
      sb.append(this.prompt);
      sb.append(insertSecondaryPrompts(new AttributedString(this.buf.upToCursor()), secondaryPrompts, false));
      List<AttributedString> promptLines = sb.columnSplitLength(this.size.getColumns(), false, this.display.delayLineWrap());
      int currentLine = promptLines.size() - 1;
      int wantedLine = Math.max(0, Math.min(currentLine + event.getY() - cursor.getY(), secondaryPrompts.size()));
      int pl0 = (currentLine == 0) ? this.prompt.columnLength() : ((AttributedString)secondaryPrompts.get(currentLine - 1)).columnLength();
      int pl1 = (wantedLine == 0) ? this.prompt.columnLength() : ((AttributedString)secondaryPrompts.get(wantedLine - 1)).columnLength();
      int adjust = pl1 - pl0;
      this.buf.moveXY(event.getX() - cursor.getX() - adjust, event.getY() - cursor.getY());
    } 
    return true;
  }
  
  public boolean beginPaste() {
    String str = doReadStringUntil("\033[201~");
    this.regionActive = LineReader.RegionType.PASTE;
    this.regionMark = getBuffer().cursor();
    getBuffer().write(str.replace('\r', '\n'));
    return true;
  }
  
  public boolean focusIn() {
    return false;
  }
  
  public boolean focusOut() {
    return false;
  }
  
  public boolean clear() {
    this.display.update(Collections.emptyList(), 0);
    return true;
  }
  
  public boolean clearScreen() {
    if (this.terminal.puts(InfoCmp.Capability.clear_screen, new Object[0])) {
      if ("windows-conemu".equals(this.terminal.getType()) && 
        !Boolean.getBoolean("org.jline.terminal.conemu.disable-activate"))
        this.terminal.writer().write("\033[9999E"); 
      Status status = Status.getStatus(this.terminal, false);
      if (status != null)
        status.reset(); 
      redrawLine();
    } else {
      println();
    } 
    return true;
  }
  
  public boolean beep() {
    BellType bell_preference = BellType.AUDIBLE;
    switch (getString("bell-style", "").toLowerCase()) {
      case "none":
      case "off":
        bell_preference = BellType.NONE;
        break;
      case "audible":
        bell_preference = BellType.AUDIBLE;
        break;
      case "visible":
        bell_preference = BellType.VISIBLE;
        break;
      case "on":
        bell_preference = getBoolean("prefer-visible-bell", false) ? BellType.VISIBLE : BellType.AUDIBLE;
        break;
    } 
    if (bell_preference == BellType.VISIBLE) {
      if (this.terminal.puts(InfoCmp.Capability.flash_screen, new Object[0]) || this.terminal
        .puts(InfoCmp.Capability.bell, new Object[0]))
        flush(); 
    } else if (bell_preference == BellType.AUDIBLE && 
      this.terminal.puts(InfoCmp.Capability.bell, new Object[0])) {
      flush();
    } 
    return true;
  }
  
  protected boolean isDelimiter(int c) {
    return !Character.isLetterOrDigit(c);
  }
  
  protected boolean isWhitespace(int c) {
    return Character.isWhitespace(c);
  }
  
  protected boolean isViAlphaNum(int c) {
    return (c == 95 || Character.isLetterOrDigit(c));
  }
  
  protected boolean isAlpha(int c) {
    return Character.isLetter(c);
  }
  
  protected boolean isWord(int c) {
    String wordchars = getString("WORDCHARS", "*?_-.[]~=/&;!#$%^(){}<>");
    return (Character.isLetterOrDigit(c) || (c < 128 && wordchars
      .indexOf((char)c) >= 0));
  }
  
  String getString(String name, String def) {
    return ReaderUtils.getString(this, name, def);
  }
  
  boolean getBoolean(String name, boolean def) {
    return ReaderUtils.getBoolean(this, name, def);
  }
  
  int getInt(String name, int def) {
    return ReaderUtils.getInt(this, name, def);
  }
  
  long getLong(String name, long def) {
    return ReaderUtils.getLong(this, name, def);
  }
  
  public Map<String, KeyMap<Binding>> defaultKeyMaps() {
    Map<String, KeyMap<Binding>> keyMaps = new HashMap<>();
    keyMaps.put("emacs", emacs());
    keyMaps.put("vicmd", viCmd());
    keyMaps.put("viins", viInsertion());
    keyMaps.put("menu", menu());
    keyMaps.put("viopp", viOpp());
    keyMaps.put("visual", visual());
    keyMaps.put(".safe", safe());
    if (getBoolean("bind-tty-special-chars", true)) {
      Attributes attr = this.terminal.getAttributes();
      bindConsoleChars(keyMaps.get("emacs"), attr);
      bindConsoleChars(keyMaps.get("viins"), attr);
    } 
    for (KeyMap<Binding> keyMap : keyMaps.values()) {
      keyMap.setUnicode(new Reference("self-insert"));
      keyMap.setAmbiguousTimeout(getLong("ambiguous-binding", 1000L));
    } 
    keyMaps.put("main", keyMaps.get("emacs"));
    return keyMaps;
  }
  
  public KeyMap<Binding> emacs() {
    KeyMap<Binding> emacs = new KeyMap();
    bindKeys(emacs);
    bind(emacs, "set-mark-command", new CharSequence[] { KeyMap.ctrl('@') });
    bind(emacs, "beginning-of-line", new CharSequence[] { KeyMap.ctrl('A') });
    bind(emacs, "backward-char", new CharSequence[] { KeyMap.ctrl('B') });
    bind(emacs, "delete-char-or-list", new CharSequence[] { KeyMap.ctrl('D') });
    bind(emacs, "end-of-line", new CharSequence[] { KeyMap.ctrl('E') });
    bind(emacs, "forward-char", new CharSequence[] { KeyMap.ctrl('F') });
    bind(emacs, "abort", new CharSequence[] { KeyMap.ctrl('G') });
    bind(emacs, "backward-delete-char", new CharSequence[] { KeyMap.ctrl('H') });
    bind(emacs, "expand-or-complete", new CharSequence[] { KeyMap.ctrl('I') });
    bind(emacs, "accept-line", new CharSequence[] { KeyMap.ctrl('J') });
    bind(emacs, "kill-line", new CharSequence[] { KeyMap.ctrl('K') });
    bind(emacs, "clear-screen", new CharSequence[] { KeyMap.ctrl('L') });
    bind(emacs, "accept-line", new CharSequence[] { KeyMap.ctrl('M') });
    bind(emacs, "down-line-or-history", new CharSequence[] { KeyMap.ctrl('N') });
    bind(emacs, "accept-line-and-down-history", new CharSequence[] { KeyMap.ctrl('O') });
    bind(emacs, "up-line-or-history", new CharSequence[] { KeyMap.ctrl('P') });
    bind(emacs, "history-incremental-search-backward", new CharSequence[] { KeyMap.ctrl('R') });
    bind(emacs, "history-incremental-search-forward", new CharSequence[] { KeyMap.ctrl('S') });
    bind(emacs, "transpose-chars", new CharSequence[] { KeyMap.ctrl('T') });
    bind(emacs, "kill-whole-line", new CharSequence[] { KeyMap.ctrl('U') });
    bind(emacs, "quoted-insert", new CharSequence[] { KeyMap.ctrl('V') });
    bind(emacs, "backward-kill-word", new CharSequence[] { KeyMap.ctrl('W') });
    bind(emacs, "yank", new CharSequence[] { KeyMap.ctrl('Y') });
    bind(emacs, "character-search", new CharSequence[] { KeyMap.ctrl(']') });
    bind(emacs, "undo", new CharSequence[] { KeyMap.ctrl('_') });
    bind(emacs, "self-insert", KeyMap.range(" -~"));
    bind(emacs, "insert-close-paren", new CharSequence[] { ")" });
    bind(emacs, "insert-close-square", new CharSequence[] { "]" });
    bind(emacs, "insert-close-curly", new CharSequence[] { "}" });
    bind(emacs, "backward-delete-char", new CharSequence[] { KeyMap.del() });
    bind(emacs, "vi-match-bracket", new CharSequence[] { KeyMap.translate("^X^B") });
    bind(emacs, "abort", new CharSequence[] { KeyMap.translate("^X^G") });
    bind(emacs, "edit-and-execute-command", new CharSequence[] { KeyMap.translate("^X^E") });
    bind(emacs, "vi-find-next-char", new CharSequence[] { KeyMap.translate("^X^F") });
    bind(emacs, "vi-join", new CharSequence[] { KeyMap.translate("^X^J") });
    bind(emacs, "kill-buffer", new CharSequence[] { KeyMap.translate("^X^K") });
    bind(emacs, "infer-next-history", new CharSequence[] { KeyMap.translate("^X^N") });
    bind(emacs, "overwrite-mode", new CharSequence[] { KeyMap.translate("^X^O") });
    bind(emacs, "redo", new CharSequence[] { KeyMap.translate("^X^R") });
    bind(emacs, "undo", new CharSequence[] { KeyMap.translate("^X^U") });
    bind(emacs, "vi-cmd-mode", new CharSequence[] { KeyMap.translate("^X^V") });
    bind(emacs, "exchange-point-and-mark", new CharSequence[] { KeyMap.translate("^X^X") });
    bind(emacs, "do-lowercase-version", new CharSequence[] { KeyMap.translate("^XA-^XZ") });
    bind(emacs, "what-cursor-position", new CharSequence[] { KeyMap.translate("^X=") });
    bind(emacs, "kill-line", new CharSequence[] { KeyMap.translate("^X^?") });
    bind(emacs, "abort", new CharSequence[] { KeyMap.alt(KeyMap.ctrl('G')) });
    bind(emacs, "backward-kill-word", new CharSequence[] { KeyMap.alt(KeyMap.ctrl('H')) });
    bind(emacs, "self-insert-unmeta", new CharSequence[] { KeyMap.alt(KeyMap.ctrl('M')) });
    bind(emacs, "complete-word", new CharSequence[] { KeyMap.alt(KeyMap.esc()) });
    bind(emacs, "character-search-backward", new CharSequence[] { KeyMap.alt(KeyMap.ctrl(']')) });
    bind(emacs, "copy-prev-word", new CharSequence[] { KeyMap.alt(KeyMap.ctrl('_')) });
    bind(emacs, "set-mark-command", new CharSequence[] { KeyMap.alt(' ') });
    bind(emacs, "neg-argument", new CharSequence[] { KeyMap.alt('-') });
    bind(emacs, "digit-argument", KeyMap.range("\\E0-\\E9"));
    bind(emacs, "beginning-of-history", new CharSequence[] { KeyMap.alt('<') });
    bind(emacs, "list-choices", new CharSequence[] { KeyMap.alt('=') });
    bind(emacs, "end-of-history", new CharSequence[] { KeyMap.alt('>') });
    bind(emacs, "list-choices", new CharSequence[] { KeyMap.alt('?') });
    bind(emacs, "do-lowercase-version", KeyMap.range("^[A-^[Z"));
    bind(emacs, "accept-and-hold", new CharSequence[] { KeyMap.alt('a') });
    bind(emacs, "backward-word", new CharSequence[] { KeyMap.alt('b') });
    bind(emacs, "capitalize-word", new CharSequence[] { KeyMap.alt('c') });
    bind(emacs, "kill-word", new CharSequence[] { KeyMap.alt('d') });
    bind(emacs, "kill-word", new CharSequence[] { KeyMap.translate("^[[3;5~") });
    bind(emacs, "forward-word", new CharSequence[] { KeyMap.alt('f') });
    bind(emacs, "down-case-word", new CharSequence[] { KeyMap.alt('l') });
    bind(emacs, "history-search-forward", new CharSequence[] { KeyMap.alt('n') });
    bind(emacs, "history-search-backward", new CharSequence[] { KeyMap.alt('p') });
    bind(emacs, "transpose-words", new CharSequence[] { KeyMap.alt('t') });
    bind(emacs, "up-case-word", new CharSequence[] { KeyMap.alt('u') });
    bind(emacs, "yank-pop", new CharSequence[] { KeyMap.alt('y') });
    bind(emacs, "backward-kill-word", new CharSequence[] { KeyMap.alt(KeyMap.del()) });
    bindArrowKeys(emacs);
    bind(emacs, "forward-word", new CharSequence[] { KeyMap.translate("^[[1;5C") });
    bind(emacs, "backward-word", new CharSequence[] { KeyMap.translate("^[[1;5D") });
    bind(emacs, "forward-word", new CharSequence[] { KeyMap.alt(key(InfoCmp.Capability.key_right)) });
    bind(emacs, "backward-word", new CharSequence[] { KeyMap.alt(key(InfoCmp.Capability.key_left)) });
    bind(emacs, "forward-word", new CharSequence[] { KeyMap.alt(KeyMap.translate("^[[C")) });
    bind(emacs, "backward-word", new CharSequence[] { KeyMap.alt(KeyMap.translate("^[[D")) });
    return emacs;
  }
  
  public KeyMap<Binding> viInsertion() {
    KeyMap<Binding> viins = new KeyMap();
    bindKeys(viins);
    bind(viins, "self-insert", KeyMap.range("^@-^_"));
    bind(viins, "list-choices", new CharSequence[] { KeyMap.ctrl('D') });
    bind(viins, "abort", new CharSequence[] { KeyMap.ctrl('G') });
    bind(viins, "backward-delete-char", new CharSequence[] { KeyMap.ctrl('H') });
    bind(viins, "expand-or-complete", new CharSequence[] { KeyMap.ctrl('I') });
    bind(viins, "accept-line", new CharSequence[] { KeyMap.ctrl('J') });
    bind(viins, "clear-screen", new CharSequence[] { KeyMap.ctrl('L') });
    bind(viins, "accept-line", new CharSequence[] { KeyMap.ctrl('M') });
    bind(viins, "menu-complete", new CharSequence[] { KeyMap.ctrl('N') });
    bind(viins, "reverse-menu-complete", new CharSequence[] { KeyMap.ctrl('P') });
    bind(viins, "history-incremental-search-backward", new CharSequence[] { KeyMap.ctrl('R') });
    bind(viins, "history-incremental-search-forward", new CharSequence[] { KeyMap.ctrl('S') });
    bind(viins, "transpose-chars", new CharSequence[] { KeyMap.ctrl('T') });
    bind(viins, "kill-whole-line", new CharSequence[] { KeyMap.ctrl('U') });
    bind(viins, "quoted-insert", new CharSequence[] { KeyMap.ctrl('V') });
    bind(viins, "backward-kill-word", new CharSequence[] { KeyMap.ctrl('W') });
    bind(viins, "yank", new CharSequence[] { KeyMap.ctrl('Y') });
    bind(viins, "vi-cmd-mode", new CharSequence[] { KeyMap.ctrl('[') });
    bind(viins, "undo", new CharSequence[] { KeyMap.ctrl('_') });
    bind(viins, "history-incremental-search-backward", new CharSequence[] { KeyMap.ctrl('X') + "r" });
    bind(viins, "history-incremental-search-forward", new CharSequence[] { KeyMap.ctrl('X') + "s" });
    bind(viins, "self-insert", KeyMap.range(" -~"));
    bind(viins, "insert-close-paren", new CharSequence[] { ")" });
    bind(viins, "insert-close-square", new CharSequence[] { "]" });
    bind(viins, "insert-close-curly", new CharSequence[] { "}" });
    bind(viins, "backward-delete-char", new CharSequence[] { KeyMap.del() });
    bindArrowKeys(viins);
    return viins;
  }
  
  public KeyMap<Binding> viCmd() {
    KeyMap<Binding> vicmd = new KeyMap();
    bind(vicmd, "list-choices", new CharSequence[] { KeyMap.ctrl('D') });
    bind(vicmd, "emacs-editing-mode", new CharSequence[] { KeyMap.ctrl('E') });
    bind(vicmd, "abort", new CharSequence[] { KeyMap.ctrl('G') });
    bind(vicmd, "vi-backward-char", new CharSequence[] { KeyMap.ctrl('H') });
    bind(vicmd, "accept-line", new CharSequence[] { KeyMap.ctrl('J') });
    bind(vicmd, "kill-line", new CharSequence[] { KeyMap.ctrl('K') });
    bind(vicmd, "clear-screen", new CharSequence[] { KeyMap.ctrl('L') });
    bind(vicmd, "accept-line", new CharSequence[] { KeyMap.ctrl('M') });
    bind(vicmd, "vi-down-line-or-history", new CharSequence[] { KeyMap.ctrl('N') });
    bind(vicmd, "vi-up-line-or-history", new CharSequence[] { KeyMap.ctrl('P') });
    bind(vicmd, "quoted-insert", new CharSequence[] { KeyMap.ctrl('Q') });
    bind(vicmd, "history-incremental-search-backward", new CharSequence[] { KeyMap.ctrl('R') });
    bind(vicmd, "history-incremental-search-forward", new CharSequence[] { KeyMap.ctrl('S') });
    bind(vicmd, "transpose-chars", new CharSequence[] { KeyMap.ctrl('T') });
    bind(vicmd, "kill-whole-line", new CharSequence[] { KeyMap.ctrl('U') });
    bind(vicmd, "quoted-insert", new CharSequence[] { KeyMap.ctrl('V') });
    bind(vicmd, "backward-kill-word", new CharSequence[] { KeyMap.ctrl('W') });
    bind(vicmd, "yank", new CharSequence[] { KeyMap.ctrl('Y') });
    bind(vicmd, "history-incremental-search-backward", new CharSequence[] { KeyMap.ctrl('X') + "r" });
    bind(vicmd, "history-incremental-search-forward", new CharSequence[] { KeyMap.ctrl('X') + "s" });
    bind(vicmd, "abort", new CharSequence[] { KeyMap.alt(KeyMap.ctrl('G')) });
    bind(vicmd, "backward-kill-word", new CharSequence[] { KeyMap.alt(KeyMap.ctrl('H')) });
    bind(vicmd, "self-insert-unmeta", new CharSequence[] { KeyMap.alt(KeyMap.ctrl('M')) });
    bind(vicmd, "complete-word", new CharSequence[] { KeyMap.alt(KeyMap.esc()) });
    bind(vicmd, "character-search-backward", new CharSequence[] { KeyMap.alt(KeyMap.ctrl(']')) });
    bind(vicmd, "set-mark-command", new CharSequence[] { KeyMap.alt(' ') });
    bind(vicmd, "digit-argument", new CharSequence[] { KeyMap.alt('-') });
    bind(vicmd, "beginning-of-history", new CharSequence[] { KeyMap.alt('<') });
    bind(vicmd, "list-choices", new CharSequence[] { KeyMap.alt('=') });
    bind(vicmd, "end-of-history", new CharSequence[] { KeyMap.alt('>') });
    bind(vicmd, "list-choices", new CharSequence[] { KeyMap.alt('?') });
    bind(vicmd, "do-lowercase-version", KeyMap.range("^[A-^[Z"));
    bind(vicmd, "backward-word", new CharSequence[] { KeyMap.alt('b') });
    bind(vicmd, "capitalize-word", new CharSequence[] { KeyMap.alt('c') });
    bind(vicmd, "kill-word", new CharSequence[] { KeyMap.alt('d') });
    bind(vicmd, "forward-word", new CharSequence[] { KeyMap.alt('f') });
    bind(vicmd, "down-case-word", new CharSequence[] { KeyMap.alt('l') });
    bind(vicmd, "history-search-forward", new CharSequence[] { KeyMap.alt('n') });
    bind(vicmd, "history-search-backward", new CharSequence[] { KeyMap.alt('p') });
    bind(vicmd, "transpose-words", new CharSequence[] { KeyMap.alt('t') });
    bind(vicmd, "up-case-word", new CharSequence[] { KeyMap.alt('u') });
    bind(vicmd, "yank-pop", new CharSequence[] { KeyMap.alt('y') });
    bind(vicmd, "backward-kill-word", new CharSequence[] { KeyMap.alt(KeyMap.del()) });
    bind(vicmd, "forward-char", new CharSequence[] { " " });
    bind(vicmd, "vi-insert-comment", new CharSequence[] { "#" });
    bind(vicmd, "end-of-line", new CharSequence[] { "$" });
    bind(vicmd, "vi-match-bracket", new CharSequence[] { "%" });
    bind(vicmd, "vi-down-line-or-history", new CharSequence[] { "+" });
    bind(vicmd, "vi-rev-repeat-find", new CharSequence[] { "," });
    bind(vicmd, "vi-up-line-or-history", new CharSequence[] { "-" });
    bind(vicmd, "vi-repeat-change", new CharSequence[] { "." });
    bind(vicmd, "vi-history-search-backward", new CharSequence[] { "/" });
    bind(vicmd, "vi-digit-or-beginning-of-line", new CharSequence[] { "0" });
    bind(vicmd, "digit-argument", KeyMap.range("1-9"));
    bind(vicmd, "vi-repeat-find", new CharSequence[] { ";" });
    bind(vicmd, "list-choices", new CharSequence[] { "=" });
    bind(vicmd, "vi-history-search-forward", new CharSequence[] { "?" });
    bind(vicmd, "vi-add-eol", new CharSequence[] { "A" });
    bind(vicmd, "vi-backward-blank-word", new CharSequence[] { "B" });
    bind(vicmd, "vi-change-eol", new CharSequence[] { "C" });
    bind(vicmd, "vi-kill-eol", new CharSequence[] { "D" });
    bind(vicmd, "vi-forward-blank-word-end", new CharSequence[] { "E" });
    bind(vicmd, "vi-find-prev-char", new CharSequence[] { "F" });
    bind(vicmd, "vi-fetch-history", new CharSequence[] { "G" });
    bind(vicmd, "vi-insert-bol", new CharSequence[] { "I" });
    bind(vicmd, "vi-join", new CharSequence[] { "J" });
    bind(vicmd, "vi-rev-repeat-search", new CharSequence[] { "N" });
    bind(vicmd, "vi-open-line-above", new CharSequence[] { "O" });
    bind(vicmd, "vi-put-before", new CharSequence[] { "P" });
    bind(vicmd, "vi-replace", new CharSequence[] { "R" });
    bind(vicmd, "vi-kill-line", new CharSequence[] { "S" });
    bind(vicmd, "vi-find-prev-char-skip", new CharSequence[] { "T" });
    bind(vicmd, "redo", new CharSequence[] { "U" });
    bind(vicmd, "visual-line-mode", new CharSequence[] { "V" });
    bind(vicmd, "vi-forward-blank-word", new CharSequence[] { "W" });
    bind(vicmd, "vi-backward-delete-char", new CharSequence[] { "X" });
    bind(vicmd, "vi-yank-whole-line", new CharSequence[] { "Y" });
    bind(vicmd, "vi-first-non-blank", new CharSequence[] { "^" });
    bind(vicmd, "vi-add-next", new CharSequence[] { "a" });
    bind(vicmd, "vi-backward-word", new CharSequence[] { "b" });
    bind(vicmd, "vi-change-to", new CharSequence[] { "c" });
    bind(vicmd, "vi-delete", new CharSequence[] { "d" });
    bind(vicmd, "vi-forward-word-end", new CharSequence[] { "e" });
    bind(vicmd, "vi-find-next-char", new CharSequence[] { "f" });
    bind(vicmd, "what-cursor-position", new CharSequence[] { "ga" });
    bind(vicmd, "vi-backward-blank-word-end", new CharSequence[] { "gE" });
    bind(vicmd, "vi-backward-word-end", new CharSequence[] { "ge" });
    bind(vicmd, "vi-backward-char", new CharSequence[] { "h" });
    bind(vicmd, "vi-insert", new CharSequence[] { "i" });
    bind(vicmd, "down-line-or-history", new CharSequence[] { "j" });
    bind(vicmd, "up-line-or-history", new CharSequence[] { "k" });
    bind(vicmd, "vi-forward-char", new CharSequence[] { "l" });
    bind(vicmd, "vi-repeat-search", new CharSequence[] { "n" });
    bind(vicmd, "vi-open-line-below", new CharSequence[] { "o" });
    bind(vicmd, "vi-put-after", new CharSequence[] { "p" });
    bind(vicmd, "vi-replace-chars", new CharSequence[] { "r" });
    bind(vicmd, "vi-substitute", new CharSequence[] { "s" });
    bind(vicmd, "vi-find-next-char-skip", new CharSequence[] { "t" });
    bind(vicmd, "undo", new CharSequence[] { "u" });
    bind(vicmd, "visual-mode", new CharSequence[] { "v" });
    bind(vicmd, "vi-forward-word", new CharSequence[] { "w" });
    bind(vicmd, "vi-delete-char", new CharSequence[] { "x" });
    bind(vicmd, "vi-yank", new CharSequence[] { "y" });
    bind(vicmd, "vi-goto-column", new CharSequence[] { "|" });
    bind(vicmd, "vi-swap-case", new CharSequence[] { "~" });
    bind(vicmd, "vi-backward-char", new CharSequence[] { KeyMap.del() });
    bindArrowKeys(vicmd);
    return vicmd;
  }
  
  public KeyMap<Binding> menu() {
    KeyMap<Binding> menu = new KeyMap();
    bind(menu, "menu-complete", new CharSequence[] { "\t" });
    bind(menu, "reverse-menu-complete", new CharSequence[] { key(InfoCmp.Capability.back_tab) });
    bind(menu, "accept-line", new CharSequence[] { "\r", "\n" });
    bindArrowKeys(menu);
    return menu;
  }
  
  public KeyMap<Binding> safe() {
    KeyMap<Binding> safe = new KeyMap();
    bind(safe, "self-insert", KeyMap.range("^@-^?"));
    bind(safe, "accept-line", new CharSequence[] { "\r", "\n" });
    bind(safe, "abort", new CharSequence[] { KeyMap.ctrl('G') });
    return safe;
  }
  
  public KeyMap<Binding> visual() {
    KeyMap<Binding> visual = new KeyMap();
    bind(visual, "up-line", new CharSequence[] { key(InfoCmp.Capability.key_up), "k" });
    bind(visual, "down-line", new CharSequence[] { key(InfoCmp.Capability.key_down), "j" });
    bind(visual, this::deactivateRegion, new CharSequence[] { KeyMap.esc() });
    bind(visual, "exchange-point-and-mark", new CharSequence[] { "o" });
    bind(visual, "put-replace-selection", new CharSequence[] { "p" });
    bind(visual, "vi-delete", new CharSequence[] { "x" });
    bind(visual, "vi-oper-swap-case", new CharSequence[] { "~" });
    return visual;
  }
  
  public KeyMap<Binding> viOpp() {
    KeyMap<Binding> viOpp = new KeyMap();
    bind(viOpp, "up-line", new CharSequence[] { key(InfoCmp.Capability.key_up), "k" });
    bind(viOpp, "down-line", new CharSequence[] { key(InfoCmp.Capability.key_down), "j" });
    bind(viOpp, "vi-cmd-mode", new CharSequence[] { KeyMap.esc() });
    return viOpp;
  }
  
  private void bind(KeyMap<Binding> map, String widget, Iterable<? extends CharSequence> keySeqs) {
    map.bind(new Reference(widget), keySeqs);
  }
  
  private void bind(KeyMap<Binding> map, String widget, CharSequence... keySeqs) {
    map.bind(new Reference(widget), keySeqs);
  }
  
  private void bind(KeyMap<Binding> map, Widget widget, CharSequence... keySeqs) {
    map.bind(widget, keySeqs);
  }
  
  private String key(InfoCmp.Capability capability) {
    return KeyMap.key(this.terminal, capability);
  }
  
  private void bindKeys(KeyMap<Binding> emacs) {
    Widget beep = namedWidget("beep", this::beep);
    Stream.<InfoCmp.Capability>of(InfoCmp.Capability.values())
      .filter(c -> c.name().startsWith("key_"))
      .map(this::key)
      .forEach(k -> bind(emacs, beep, new CharSequence[] { k }));
  }
  
  private void bindArrowKeys(KeyMap<Binding> map) {
    bind(map, "up-line-or-search", new CharSequence[] { key(InfoCmp.Capability.key_up) });
    bind(map, "down-line-or-search", new CharSequence[] { key(InfoCmp.Capability.key_down) });
    bind(map, "backward-char", new CharSequence[] { key(InfoCmp.Capability.key_left) });
    bind(map, "forward-char", new CharSequence[] { key(InfoCmp.Capability.key_right) });
    bind(map, "beginning-of-line", new CharSequence[] { key(InfoCmp.Capability.key_home) });
    bind(map, "end-of-line", new CharSequence[] { key(InfoCmp.Capability.key_end) });
    bind(map, "delete-char", new CharSequence[] { key(InfoCmp.Capability.key_dc) });
    bind(map, "kill-whole-line", new CharSequence[] { key(InfoCmp.Capability.key_dl) });
    bind(map, "overwrite-mode", new CharSequence[] { key(InfoCmp.Capability.key_ic) });
    bind(map, "mouse", new CharSequence[] { key(InfoCmp.Capability.key_mouse) });
    bind(map, "begin-paste", new CharSequence[] { "\033[200~" });
    bind(map, "terminal-focus-in", new CharSequence[] { "\033[I" });
    bind(map, "terminal-focus-out", new CharSequence[] { "\033[O" });
  }
  
  private void bindConsoleChars(KeyMap<Binding> keyMap, Attributes attr) {
    if (attr != null) {
      rebind(keyMap, "backward-delete-char", 
          KeyMap.del(), (char)attr.getControlChar(Attributes.ControlChar.VERASE));
      rebind(keyMap, "backward-kill-word", 
          KeyMap.ctrl('W'), (char)attr.getControlChar(Attributes.ControlChar.VWERASE));
      rebind(keyMap, "kill-whole-line", 
          KeyMap.ctrl('U'), (char)attr.getControlChar(Attributes.ControlChar.VKILL));
      rebind(keyMap, "quoted-insert", 
          KeyMap.ctrl('V'), (char)attr.getControlChar(Attributes.ControlChar.VLNEXT));
    } 
  }
  
  private void rebind(KeyMap<Binding> keyMap, String operation, String prevBinding, char newBinding) {
    if (newBinding > '\000' && newBinding < '') {
      Reference ref = new Reference(operation);
      bind(keyMap, "self-insert", new CharSequence[] { prevBinding });
      keyMap.bind(ref, Character.toString(newBinding));
    } 
  }
}
