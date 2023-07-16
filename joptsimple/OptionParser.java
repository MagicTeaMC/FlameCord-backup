package joptsimple;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import joptsimple.internal.AbbreviationMap;
import joptsimple.internal.OptionNameMap;
import joptsimple.internal.SimpleOptionNameMap;
import joptsimple.util.KeyValuePair;

public class OptionParser implements OptionDeclarer {
  private final OptionNameMap<AbstractOptionSpec<?>> recognizedOptions;
  
  private final ArrayList<AbstractOptionSpec<?>> trainingOrder;
  
  private final Map<List<String>, Set<OptionSpec<?>>> requiredIf;
  
  private final Map<List<String>, Set<OptionSpec<?>>> requiredUnless;
  
  private final Map<List<String>, Set<OptionSpec<?>>> availableIf;
  
  private final Map<List<String>, Set<OptionSpec<?>>> availableUnless;
  
  private OptionParserState state;
  
  private boolean posixlyCorrect;
  
  private boolean allowsUnrecognizedOptions;
  
  private HelpFormatter helpFormatter = new BuiltinHelpFormatter();
  
  public OptionParser() {
    this(true);
  }
  
  public OptionParser(boolean allowAbbreviations) {
    this.trainingOrder = new ArrayList<>();
    this.requiredIf = new HashMap<>();
    this.requiredUnless = new HashMap<>();
    this.availableIf = new HashMap<>();
    this.availableUnless = new HashMap<>();
    this.state = OptionParserState.moreOptions(false);
    this.recognizedOptions = allowAbbreviations ? (OptionNameMap<AbstractOptionSpec<?>>)new AbbreviationMap() : (OptionNameMap<AbstractOptionSpec<?>>)new SimpleOptionNameMap();
    recognize(new NonOptionArgumentSpec());
  }
  
  public OptionParser(String optionSpecification) {
    this();
    (new OptionSpecTokenizer(optionSpecification)).configure(this);
  }
  
  public OptionSpecBuilder accepts(String option) {
    return acceptsAll(Collections.singletonList(option));
  }
  
  public OptionSpecBuilder accepts(String option, String description) {
    return acceptsAll(Collections.singletonList(option), description);
  }
  
  public OptionSpecBuilder acceptsAll(List<String> options) {
    return acceptsAll(options, "");
  }
  
  public OptionSpecBuilder acceptsAll(List<String> options, String description) {
    if (options.isEmpty())
      throw new IllegalArgumentException("need at least one option"); 
    ParserRules.ensureLegalOptions(options);
    return new OptionSpecBuilder(this, options, description);
  }
  
  public NonOptionArgumentSpec<String> nonOptions() {
    NonOptionArgumentSpec<String> spec = new NonOptionArgumentSpec<>();
    recognize(spec);
    return spec;
  }
  
  public NonOptionArgumentSpec<String> nonOptions(String description) {
    NonOptionArgumentSpec<String> spec = new NonOptionArgumentSpec<>(description);
    recognize(spec);
    return spec;
  }
  
  public void posixlyCorrect(boolean setting) {
    this.posixlyCorrect = setting;
    this.state = OptionParserState.moreOptions(setting);
  }
  
  boolean posixlyCorrect() {
    return this.posixlyCorrect;
  }
  
  public void allowsUnrecognizedOptions() {
    this.allowsUnrecognizedOptions = true;
  }
  
  boolean doesAllowsUnrecognizedOptions() {
    return this.allowsUnrecognizedOptions;
  }
  
  public void recognizeAlternativeLongOptions(boolean recognize) {
    if (recognize) {
      recognize(new AlternativeLongOptionSpec());
    } else {
      this.recognizedOptions.remove(String.valueOf("W"));
    } 
  }
  
  void recognize(AbstractOptionSpec<?> spec) {
    this.recognizedOptions.putAll(spec.options(), spec);
    this.trainingOrder.add(spec);
  }
  
  public void printHelpOn(OutputStream sink) throws IOException {
    printHelpOn(new OutputStreamWriter(sink));
  }
  
  public void printHelpOn(Writer sink) throws IOException {
    sink.write(this.helpFormatter.format((Map)_recognizedOptions()));
    sink.flush();
  }
  
  public void formatHelpWith(HelpFormatter formatter) {
    if (formatter == null)
      throw new NullPointerException(); 
    this.helpFormatter = formatter;
  }
  
  public Map<String, OptionSpec<?>> recognizedOptions() {
    return new LinkedHashMap<>((Map)_recognizedOptions());
  }
  
  private Map<String, AbstractOptionSpec<?>> _recognizedOptions() {
    Map<String, AbstractOptionSpec<?>> options = new LinkedHashMap<>();
    for (AbstractOptionSpec<?> spec : this.trainingOrder) {
      for (String option : spec.options())
        options.put(option, spec); 
    } 
    return options;
  }
  
  public OptionSet parse(String... arguments) {
    ArgumentList argumentList = new ArgumentList(arguments);
    OptionSet detected = new OptionSet(this.recognizedOptions.toJavaUtilMap());
    detected.add((AbstractOptionSpec)this.recognizedOptions.get("[arguments]"));
    while (argumentList.hasMore())
      this.state.handleArgument(this, argumentList, detected); 
    reset();
    ensureRequiredOptions(detected);
    ensureAllowedOptions(detected);
    return detected;
  }
  
  public void mutuallyExclusive(OptionSpecBuilder... specs) {
    for (int i = 0; i < specs.length; i++) {
      for (int j = 0; j < specs.length; j++) {
        if (i != j)
          specs[i].availableUnless(specs[j], (OptionSpec<?>[])new OptionSpec[0]); 
      } 
    } 
  }
  
  private void ensureRequiredOptions(OptionSet options) {
    List<AbstractOptionSpec<?>> missingRequiredOptions = missingRequiredOptions(options);
    boolean helpOptionPresent = isHelpOptionPresent(options);
    if (!missingRequiredOptions.isEmpty() && !helpOptionPresent)
      throw new MissingRequiredOptionsException(missingRequiredOptions); 
  }
  
  private void ensureAllowedOptions(OptionSet options) {
    List<AbstractOptionSpec<?>> forbiddenOptions = unavailableOptions(options);
    boolean helpOptionPresent = isHelpOptionPresent(options);
    if (!forbiddenOptions.isEmpty() && !helpOptionPresent)
      throw new UnavailableOptionException(forbiddenOptions); 
  }
  
  private List<AbstractOptionSpec<?>> missingRequiredOptions(OptionSet options) {
    List<AbstractOptionSpec<?>> missingRequiredOptions = new ArrayList<>();
    for (AbstractOptionSpec<?> each : (Iterable<AbstractOptionSpec<?>>)this.recognizedOptions.toJavaUtilMap().values()) {
      if (each.isRequired() && !options.has(each))
        missingRequiredOptions.add(each); 
    } 
    for (Map.Entry<List<String>, Set<OptionSpec<?>>> each : this.requiredIf.entrySet()) {
      AbstractOptionSpec<?> required = specFor(((List<String>)each.getKey()).iterator().next());
      if (optionsHasAnyOf(options, each.getValue()) && !options.has(required))
        missingRequiredOptions.add(required); 
    } 
    for (Map.Entry<List<String>, Set<OptionSpec<?>>> each : this.requiredUnless.entrySet()) {
      AbstractOptionSpec<?> required = specFor(((List<String>)each.getKey()).iterator().next());
      if (!optionsHasAnyOf(options, each.getValue()) && !options.has(required))
        missingRequiredOptions.add(required); 
    } 
    return missingRequiredOptions;
  }
  
  private List<AbstractOptionSpec<?>> unavailableOptions(OptionSet options) {
    List<AbstractOptionSpec<?>> unavailableOptions = new ArrayList<>();
    for (Map.Entry<List<String>, Set<OptionSpec<?>>> eachEntry : this.availableIf.entrySet()) {
      AbstractOptionSpec<?> forbidden = specFor(((List<String>)eachEntry.getKey()).iterator().next());
      if (!optionsHasAnyOf(options, eachEntry.getValue()) && options.has(forbidden))
        unavailableOptions.add(forbidden); 
    } 
    for (Map.Entry<List<String>, Set<OptionSpec<?>>> eachEntry : this.availableUnless.entrySet()) {
      AbstractOptionSpec<?> forbidden = specFor(((List<String>)eachEntry.getKey()).iterator().next());
      if (optionsHasAnyOf(options, eachEntry.getValue()) && options.has(forbidden))
        unavailableOptions.add(forbidden); 
    } 
    return unavailableOptions;
  }
  
  private boolean optionsHasAnyOf(OptionSet options, Collection<OptionSpec<?>> specs) {
    for (OptionSpec<?> each : specs) {
      if (options.has(each))
        return true; 
    } 
    return false;
  }
  
  private boolean isHelpOptionPresent(OptionSet options) {
    boolean helpOptionPresent = false;
    for (AbstractOptionSpec<?> each : (Iterable<AbstractOptionSpec<?>>)this.recognizedOptions.toJavaUtilMap().values()) {
      if (each.isForHelp() && options.has(each)) {
        helpOptionPresent = true;
        break;
      } 
    } 
    return helpOptionPresent;
  }
  
  void handleLongOptionToken(String candidate, ArgumentList arguments, OptionSet detected) {
    KeyValuePair optionAndArgument = parseLongOptionWithArgument(candidate);
    if (!isRecognized(optionAndArgument.key))
      throw OptionException.unrecognizedOption(optionAndArgument.key); 
    AbstractOptionSpec<?> optionSpec = specFor(optionAndArgument.key);
    optionSpec.handleOption(this, arguments, detected, optionAndArgument.value);
  }
  
  void handleShortOptionToken(String candidate, ArgumentList arguments, OptionSet detected) {
    KeyValuePair optionAndArgument = parseShortOptionWithArgument(candidate);
    if (isRecognized(optionAndArgument.key)) {
      specFor(optionAndArgument.key).handleOption(this, arguments, detected, optionAndArgument.value);
    } else {
      handleShortOptionCluster(candidate, arguments, detected);
    } 
  }
  
  private void handleShortOptionCluster(String candidate, ArgumentList arguments, OptionSet detected) {
    char[] options = extractShortOptionsFrom(candidate);
    validateOptionCharacters(options);
    for (int i = 0; i < options.length; i++) {
      AbstractOptionSpec<?> optionSpec = specFor(options[i]);
      if (optionSpec.acceptsArguments() && options.length > i + 1) {
        String detectedArgument = String.valueOf(options, i + 1, options.length - 1 - i);
        optionSpec.handleOption(this, arguments, detected, detectedArgument);
        break;
      } 
      optionSpec.handleOption(this, arguments, detected, (String)null);
    } 
  }
  
  void handleNonOptionArgument(String candidate, ArgumentList arguments, OptionSet detectedOptions) {
    specFor("[arguments]").handleOption(this, arguments, detectedOptions, candidate);
  }
  
  void noMoreOptions() {
    this.state = OptionParserState.noMoreOptions();
  }
  
  boolean looksLikeAnOption(String argument) {
    return (ParserRules.isShortOptionToken(argument) || ParserRules.isLongOptionToken(argument));
  }
  
  boolean isRecognized(String option) {
    return this.recognizedOptions.contains(option);
  }
  
  void requiredIf(List<String> precedentSynonyms, String required) {
    requiredIf(precedentSynonyms, specFor(required));
  }
  
  void requiredIf(List<String> precedentSynonyms, OptionSpec<?> required) {
    putDependentOption(precedentSynonyms, required, this.requiredIf);
  }
  
  void requiredUnless(List<String> precedentSynonyms, String required) {
    requiredUnless(precedentSynonyms, specFor(required));
  }
  
  void requiredUnless(List<String> precedentSynonyms, OptionSpec<?> required) {
    putDependentOption(precedentSynonyms, required, this.requiredUnless);
  }
  
  void availableIf(List<String> precedentSynonyms, String available) {
    availableIf(precedentSynonyms, specFor(available));
  }
  
  void availableIf(List<String> precedentSynonyms, OptionSpec<?> available) {
    putDependentOption(precedentSynonyms, available, this.availableIf);
  }
  
  void availableUnless(List<String> precedentSynonyms, String available) {
    availableUnless(precedentSynonyms, specFor(available));
  }
  
  void availableUnless(List<String> precedentSynonyms, OptionSpec<?> available) {
    putDependentOption(precedentSynonyms, available, this.availableUnless);
  }
  
  private void putDependentOption(List<String> precedentSynonyms, OptionSpec<?> required, Map<List<String>, Set<OptionSpec<?>>> target) {
    for (String each : precedentSynonyms) {
      AbstractOptionSpec<?> spec = specFor(each);
      if (spec == null)
        throw new UnconfiguredOptionException(precedentSynonyms); 
    } 
    Set<OptionSpec<?>> associated = target.get(precedentSynonyms);
    if (associated == null) {
      associated = new HashSet<>();
      target.put(precedentSynonyms, associated);
    } 
    associated.add(required);
  }
  
  private AbstractOptionSpec<?> specFor(char option) {
    return specFor(String.valueOf(option));
  }
  
  private AbstractOptionSpec<?> specFor(String option) {
    return (AbstractOptionSpec)this.recognizedOptions.get(option);
  }
  
  private void reset() {
    this.state = OptionParserState.moreOptions(this.posixlyCorrect);
  }
  
  private static char[] extractShortOptionsFrom(String argument) {
    char[] options = new char[argument.length() - 1];
    argument.getChars(1, argument.length(), options, 0);
    return options;
  }
  
  private void validateOptionCharacters(char[] options) {
    for (char each : options) {
      String option = String.valueOf(each);
      if (!isRecognized(option))
        throw OptionException.unrecognizedOption(option); 
      if (specFor(option).acceptsArguments())
        return; 
    } 
  }
  
  private static KeyValuePair parseLongOptionWithArgument(String argument) {
    return KeyValuePair.valueOf(argument.substring(2));
  }
  
  private static KeyValuePair parseShortOptionWithArgument(String argument) {
    return KeyValuePair.valueOf(argument.substring(1));
  }
}
