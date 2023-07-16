package org.codehaus.plexus.util.cli;

import java.util.concurrent.Callable;

public interface CommandLineCallable extends Callable<Integer> {
  Integer call() throws CommandLineException;
}
