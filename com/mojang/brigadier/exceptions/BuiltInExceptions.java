package com.mojang.brigadier.exceptions;

import com.mojang.brigadier.LiteralMessage;
import com.mojang.brigadier.Message;

public class BuiltInExceptions implements BuiltInExceptionProvider {
  private static final Dynamic2CommandExceptionType DOUBLE_TOO_SMALL;
  
  private static final Dynamic2CommandExceptionType DOUBLE_TOO_BIG;
  
  private static final Dynamic2CommandExceptionType FLOAT_TOO_SMALL;
  
  private static final Dynamic2CommandExceptionType FLOAT_TOO_BIG;
  
  private static final Dynamic2CommandExceptionType INTEGER_TOO_SMALL;
  
  private static final Dynamic2CommandExceptionType INTEGER_TOO_BIG;
  
  private static final Dynamic2CommandExceptionType LONG_TOO_SMALL;
  
  private static final Dynamic2CommandExceptionType LONG_TOO_BIG;
  
  private static final DynamicCommandExceptionType LITERAL_INCORRECT;
  
  static {
    DOUBLE_TOO_SMALL = new Dynamic2CommandExceptionType((found, min) -> new LiteralMessage("Double must not be less than " + min + ", found " + found));
    DOUBLE_TOO_BIG = new Dynamic2CommandExceptionType((found, max) -> new LiteralMessage("Double must not be more than " + max + ", found " + found));
    FLOAT_TOO_SMALL = new Dynamic2CommandExceptionType((found, min) -> new LiteralMessage("Float must not be less than " + min + ", found " + found));
    FLOAT_TOO_BIG = new Dynamic2CommandExceptionType((found, max) -> new LiteralMessage("Float must not be more than " + max + ", found " + found));
    INTEGER_TOO_SMALL = new Dynamic2CommandExceptionType((found, min) -> new LiteralMessage("Integer must not be less than " + min + ", found " + found));
    INTEGER_TOO_BIG = new Dynamic2CommandExceptionType((found, max) -> new LiteralMessage("Integer must not be more than " + max + ", found " + found));
    LONG_TOO_SMALL = new Dynamic2CommandExceptionType((found, min) -> new LiteralMessage("Long must not be less than " + min + ", found " + found));
    LONG_TOO_BIG = new Dynamic2CommandExceptionType((found, max) -> new LiteralMessage("Long must not be more than " + max + ", found " + found));
    LITERAL_INCORRECT = new DynamicCommandExceptionType(expected -> new LiteralMessage("Expected literal " + expected));
  }
  
  private static final SimpleCommandExceptionType READER_EXPECTED_START_OF_QUOTE = new SimpleCommandExceptionType((Message)new LiteralMessage("Expected quote to start a string"));
  
  private static final SimpleCommandExceptionType READER_EXPECTED_END_OF_QUOTE = new SimpleCommandExceptionType((Message)new LiteralMessage("Unclosed quoted string"));
  
  private static final DynamicCommandExceptionType READER_INVALID_ESCAPE;
  
  private static final DynamicCommandExceptionType READER_INVALID_BOOL;
  
  private static final DynamicCommandExceptionType READER_INVALID_INT;
  
  static {
    READER_INVALID_ESCAPE = new DynamicCommandExceptionType(character -> new LiteralMessage("Invalid escape sequence '" + character + "' in quoted string"));
    READER_INVALID_BOOL = new DynamicCommandExceptionType(value -> new LiteralMessage("Invalid bool, expected true or false but found '" + value + "'"));
    READER_INVALID_INT = new DynamicCommandExceptionType(value -> new LiteralMessage("Invalid integer '" + value + "'"));
  }
  
  private static final SimpleCommandExceptionType READER_EXPECTED_INT = new SimpleCommandExceptionType((Message)new LiteralMessage("Expected integer"));
  
  private static final DynamicCommandExceptionType READER_INVALID_LONG;
  
  static {
    READER_INVALID_LONG = new DynamicCommandExceptionType(value -> new LiteralMessage("Invalid long '" + value + "'"));
  }
  
  private static final SimpleCommandExceptionType READER_EXPECTED_LONG = new SimpleCommandExceptionType((Message)new LiteralMessage("Expected long"));
  
  private static final DynamicCommandExceptionType READER_INVALID_DOUBLE;
  
  static {
    READER_INVALID_DOUBLE = new DynamicCommandExceptionType(value -> new LiteralMessage("Invalid double '" + value + "'"));
  }
  
  private static final SimpleCommandExceptionType READER_EXPECTED_DOUBLE = new SimpleCommandExceptionType((Message)new LiteralMessage("Expected double"));
  
  private static final DynamicCommandExceptionType READER_INVALID_FLOAT;
  
  static {
    READER_INVALID_FLOAT = new DynamicCommandExceptionType(value -> new LiteralMessage("Invalid float '" + value + "'"));
  }
  
  private static final SimpleCommandExceptionType READER_EXPECTED_FLOAT = new SimpleCommandExceptionType((Message)new LiteralMessage("Expected float"));
  
  private static final SimpleCommandExceptionType READER_EXPECTED_BOOL = new SimpleCommandExceptionType((Message)new LiteralMessage("Expected bool"));
  
  private static final DynamicCommandExceptionType READER_EXPECTED_SYMBOL;
  
  static {
    READER_EXPECTED_SYMBOL = new DynamicCommandExceptionType(symbol -> new LiteralMessage("Expected '" + symbol + "'"));
  }
  
  private static final SimpleCommandExceptionType DISPATCHER_UNKNOWN_COMMAND = new SimpleCommandExceptionType((Message)new LiteralMessage("Unknown command"));
  
  private static final SimpleCommandExceptionType DISPATCHER_UNKNOWN_ARGUMENT = new SimpleCommandExceptionType((Message)new LiteralMessage("Incorrect argument for command"));
  
  private static final SimpleCommandExceptionType DISPATCHER_EXPECTED_ARGUMENT_SEPARATOR = new SimpleCommandExceptionType((Message)new LiteralMessage("Expected whitespace to end one argument, but found trailing data"));
  
  private static final DynamicCommandExceptionType DISPATCHER_PARSE_EXCEPTION;
  
  static {
    DISPATCHER_PARSE_EXCEPTION = new DynamicCommandExceptionType(message -> new LiteralMessage("Could not parse command: " + message));
  }
  
  public Dynamic2CommandExceptionType doubleTooLow() {
    return DOUBLE_TOO_SMALL;
  }
  
  public Dynamic2CommandExceptionType doubleTooHigh() {
    return DOUBLE_TOO_BIG;
  }
  
  public Dynamic2CommandExceptionType floatTooLow() {
    return FLOAT_TOO_SMALL;
  }
  
  public Dynamic2CommandExceptionType floatTooHigh() {
    return FLOAT_TOO_BIG;
  }
  
  public Dynamic2CommandExceptionType integerTooLow() {
    return INTEGER_TOO_SMALL;
  }
  
  public Dynamic2CommandExceptionType integerTooHigh() {
    return INTEGER_TOO_BIG;
  }
  
  public Dynamic2CommandExceptionType longTooLow() {
    return LONG_TOO_SMALL;
  }
  
  public Dynamic2CommandExceptionType longTooHigh() {
    return LONG_TOO_BIG;
  }
  
  public DynamicCommandExceptionType literalIncorrect() {
    return LITERAL_INCORRECT;
  }
  
  public SimpleCommandExceptionType readerExpectedStartOfQuote() {
    return READER_EXPECTED_START_OF_QUOTE;
  }
  
  public SimpleCommandExceptionType readerExpectedEndOfQuote() {
    return READER_EXPECTED_END_OF_QUOTE;
  }
  
  public DynamicCommandExceptionType readerInvalidEscape() {
    return READER_INVALID_ESCAPE;
  }
  
  public DynamicCommandExceptionType readerInvalidBool() {
    return READER_INVALID_BOOL;
  }
  
  public DynamicCommandExceptionType readerInvalidInt() {
    return READER_INVALID_INT;
  }
  
  public SimpleCommandExceptionType readerExpectedInt() {
    return READER_EXPECTED_INT;
  }
  
  public DynamicCommandExceptionType readerInvalidLong() {
    return READER_INVALID_LONG;
  }
  
  public SimpleCommandExceptionType readerExpectedLong() {
    return READER_EXPECTED_LONG;
  }
  
  public DynamicCommandExceptionType readerInvalidDouble() {
    return READER_INVALID_DOUBLE;
  }
  
  public SimpleCommandExceptionType readerExpectedDouble() {
    return READER_EXPECTED_DOUBLE;
  }
  
  public DynamicCommandExceptionType readerInvalidFloat() {
    return READER_INVALID_FLOAT;
  }
  
  public SimpleCommandExceptionType readerExpectedFloat() {
    return READER_EXPECTED_FLOAT;
  }
  
  public SimpleCommandExceptionType readerExpectedBool() {
    return READER_EXPECTED_BOOL;
  }
  
  public DynamicCommandExceptionType readerExpectedSymbol() {
    return READER_EXPECTED_SYMBOL;
  }
  
  public SimpleCommandExceptionType dispatcherUnknownCommand() {
    return DISPATCHER_UNKNOWN_COMMAND;
  }
  
  public SimpleCommandExceptionType dispatcherUnknownArgument() {
    return DISPATCHER_UNKNOWN_ARGUMENT;
  }
  
  public SimpleCommandExceptionType dispatcherExpectedArgumentSeparator() {
    return DISPATCHER_EXPECTED_ARGUMENT_SEPARATOR;
  }
  
  public DynamicCommandExceptionType dispatcherParseException() {
    return DISPATCHER_PARSE_EXCEPTION;
  }
}
