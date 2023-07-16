package com.lmax.disruptor;

import java.util.Arrays;
import java.util.concurrent.atomic.AtomicReferenceFieldUpdater;

class SequenceGroups {
  static <T> void addSequences(T holder, AtomicReferenceFieldUpdater<T, Sequence[]> updater, Cursored cursor, Sequence... sequencesToAdd) {
    Sequence[] updatedSequences, currentSequences;
    do {
      currentSequences = updater.get(holder);
      updatedSequences = Arrays.<Sequence>copyOf(currentSequences, currentSequences.length + sequencesToAdd.length);
      long cursorSequence = cursor.getCursor();
      int index = currentSequences.length;
      for (Sequence sequence : sequencesToAdd) {
        sequence.set(cursorSequence);
        updatedSequences[index++] = sequence;
      } 
    } while (!updater.compareAndSet(holder, currentSequences, updatedSequences));
    long l = cursor.getCursor();
    for (Sequence sequence : sequencesToAdd)
      sequence.set(l); 
  }
  
  static <T> boolean removeSequence(T holder, AtomicReferenceFieldUpdater<T, Sequence[]> sequenceUpdater, Sequence sequence) {
    int numToRemove;
    Sequence[] oldSequences;
    Sequence[] newSequences;
    do {
      oldSequences = sequenceUpdater.get(holder);
      numToRemove = countMatching(oldSequences, sequence);
      if (0 == numToRemove)
        break; 
      int oldSize = oldSequences.length;
      newSequences = new Sequence[oldSize - numToRemove];
      for (int i = 0, pos = 0; i < oldSize; i++) {
        Sequence testSequence = oldSequences[i];
        if (sequence != testSequence)
          newSequences[pos++] = testSequence; 
      } 
    } while (!sequenceUpdater.compareAndSet(holder, oldSequences, newSequences));
    return (numToRemove != 0);
  }
  
  private static <T> int countMatching(T[] values, T toMatch) {
    int numToRemove = 0;
    for (T value : values) {
      if (value == toMatch)
        numToRemove++; 
    } 
    return numToRemove;
  }
}
