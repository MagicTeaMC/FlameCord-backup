package com.lmax.disruptor;

import com.lmax.disruptor.dsl.ProducerType;

public final class RingBuffer<E> extends RingBufferFields<E> implements Cursored, EventSequencer<E>, EventSink<E> {
  public static final long INITIAL_CURSOR_VALUE = -1L;
  
  protected long p1;
  
  protected long p2;
  
  protected long p3;
  
  protected long p4;
  
  protected long p5;
  
  protected long p6;
  
  protected long p7;
  
  RingBuffer(EventFactory<E> eventFactory, Sequencer sequencer) {
    super(eventFactory, sequencer);
  }
  
  public static <E> RingBuffer<E> createMultiProducer(EventFactory<E> factory, int bufferSize, WaitStrategy waitStrategy) {
    MultiProducerSequencer sequencer = new MultiProducerSequencer(bufferSize, waitStrategy);
    return new RingBuffer<>(factory, sequencer);
  }
  
  public static <E> RingBuffer<E> createMultiProducer(EventFactory<E> factory, int bufferSize) {
    return createMultiProducer(factory, bufferSize, new BlockingWaitStrategy());
  }
  
  public static <E> RingBuffer<E> createSingleProducer(EventFactory<E> factory, int bufferSize, WaitStrategy waitStrategy) {
    SingleProducerSequencer sequencer = new SingleProducerSequencer(bufferSize, waitStrategy);
    return new RingBuffer<>(factory, sequencer);
  }
  
  public static <E> RingBuffer<E> createSingleProducer(EventFactory<E> factory, int bufferSize) {
    return createSingleProducer(factory, bufferSize, new BlockingWaitStrategy());
  }
  
  public static <E> RingBuffer<E> create(ProducerType producerType, EventFactory<E> factory, int bufferSize, WaitStrategy waitStrategy) {
    switch (producerType) {
      case SINGLE:
        return createSingleProducer(factory, bufferSize, waitStrategy);
      case MULTI:
        return createMultiProducer(factory, bufferSize, waitStrategy);
    } 
    throw new IllegalStateException(producerType.toString());
  }
  
  public E get(long sequence) {
    return elementAt(sequence);
  }
  
  public long next() {
    return this.sequencer.next();
  }
  
  public long next(int n) {
    return this.sequencer.next(n);
  }
  
  public long tryNext() throws InsufficientCapacityException {
    return this.sequencer.tryNext();
  }
  
  public long tryNext(int n) throws InsufficientCapacityException {
    return this.sequencer.tryNext(n);
  }
  
  @Deprecated
  public void resetTo(long sequence) {
    this.sequencer.claim(sequence);
    this.sequencer.publish(sequence);
  }
  
  public E claimAndGetPreallocated(long sequence) {
    this.sequencer.claim(sequence);
    return get(sequence);
  }
  
  @Deprecated
  public boolean isPublished(long sequence) {
    return this.sequencer.isAvailable(sequence);
  }
  
  public void addGatingSequences(Sequence... gatingSequences) {
    this.sequencer.addGatingSequences(gatingSequences);
  }
  
  public long getMinimumGatingSequence() {
    return this.sequencer.getMinimumSequence();
  }
  
  public boolean removeGatingSequence(Sequence sequence) {
    return this.sequencer.removeGatingSequence(sequence);
  }
  
  public SequenceBarrier newBarrier(Sequence... sequencesToTrack) {
    return this.sequencer.newBarrier(sequencesToTrack);
  }
  
  public EventPoller<E> newPoller(Sequence... gatingSequences) {
    return this.sequencer.newPoller(this, gatingSequences);
  }
  
  public long getCursor() {
    return this.sequencer.getCursor();
  }
  
  public int getBufferSize() {
    return this.bufferSize;
  }
  
  public boolean hasAvailableCapacity(int requiredCapacity) {
    return this.sequencer.hasAvailableCapacity(requiredCapacity);
  }
  
  public void publishEvent(EventTranslator<E> translator) {
    long sequence = this.sequencer.next();
    translateAndPublish(translator, sequence);
  }
  
  public boolean tryPublishEvent(EventTranslator<E> translator) {
    try {
      long sequence = this.sequencer.tryNext();
      translateAndPublish(translator, sequence);
      return true;
    } catch (InsufficientCapacityException e) {
      return false;
    } 
  }
  
  public <A> void publishEvent(EventTranslatorOneArg<E, A> translator, A arg0) {
    long sequence = this.sequencer.next();
    translateAndPublish(translator, sequence, arg0);
  }
  
  public <A> boolean tryPublishEvent(EventTranslatorOneArg<E, A> translator, A arg0) {
    try {
      long sequence = this.sequencer.tryNext();
      translateAndPublish(translator, sequence, arg0);
      return true;
    } catch (InsufficientCapacityException e) {
      return false;
    } 
  }
  
  public <A, B> void publishEvent(EventTranslatorTwoArg<E, A, B> translator, A arg0, B arg1) {
    long sequence = this.sequencer.next();
    translateAndPublish(translator, sequence, arg0, arg1);
  }
  
  public <A, B> boolean tryPublishEvent(EventTranslatorTwoArg<E, A, B> translator, A arg0, B arg1) {
    try {
      long sequence = this.sequencer.tryNext();
      translateAndPublish(translator, sequence, arg0, arg1);
      return true;
    } catch (InsufficientCapacityException e) {
      return false;
    } 
  }
  
  public <A, B, C> void publishEvent(EventTranslatorThreeArg<E, A, B, C> translator, A arg0, B arg1, C arg2) {
    long sequence = this.sequencer.next();
    translateAndPublish(translator, sequence, arg0, arg1, arg2);
  }
  
  public <A, B, C> boolean tryPublishEvent(EventTranslatorThreeArg<E, A, B, C> translator, A arg0, B arg1, C arg2) {
    try {
      long sequence = this.sequencer.tryNext();
      translateAndPublish(translator, sequence, arg0, arg1, arg2);
      return true;
    } catch (InsufficientCapacityException e) {
      return false;
    } 
  }
  
  public void publishEvent(EventTranslatorVararg<E> translator, Object... args) {
    long sequence = this.sequencer.next();
    translateAndPublish(translator, sequence, args);
  }
  
  public boolean tryPublishEvent(EventTranslatorVararg<E> translator, Object... args) {
    try {
      long sequence = this.sequencer.tryNext();
      translateAndPublish(translator, sequence, args);
      return true;
    } catch (InsufficientCapacityException e) {
      return false;
    } 
  }
  
  public void publishEvents(EventTranslator<E>[] translators) {
    publishEvents(translators, 0, translators.length);
  }
  
  public void publishEvents(EventTranslator<E>[] translators, int batchStartsAt, int batchSize) {
    checkBounds(translators, batchStartsAt, batchSize);
    long finalSequence = this.sequencer.next(batchSize);
    translateAndPublishBatch(translators, batchStartsAt, batchSize, finalSequence);
  }
  
  public boolean tryPublishEvents(EventTranslator<E>[] translators) {
    return tryPublishEvents(translators, 0, translators.length);
  }
  
  public boolean tryPublishEvents(EventTranslator<E>[] translators, int batchStartsAt, int batchSize) {
    checkBounds(translators, batchStartsAt, batchSize);
    try {
      long finalSequence = this.sequencer.tryNext(batchSize);
      translateAndPublishBatch(translators, batchStartsAt, batchSize, finalSequence);
      return true;
    } catch (InsufficientCapacityException e) {
      return false;
    } 
  }
  
  public <A> void publishEvents(EventTranslatorOneArg<E, A> translator, A[] arg0) {
    publishEvents(translator, 0, arg0.length, arg0);
  }
  
  public <A> void publishEvents(EventTranslatorOneArg<E, A> translator, int batchStartsAt, int batchSize, A[] arg0) {
    checkBounds(arg0, batchStartsAt, batchSize);
    long finalSequence = this.sequencer.next(batchSize);
    translateAndPublishBatch(translator, arg0, batchStartsAt, batchSize, finalSequence);
  }
  
  public <A> boolean tryPublishEvents(EventTranslatorOneArg<E, A> translator, A[] arg0) {
    return tryPublishEvents(translator, 0, arg0.length, arg0);
  }
  
  public <A> boolean tryPublishEvents(EventTranslatorOneArg<E, A> translator, int batchStartsAt, int batchSize, A[] arg0) {
    checkBounds(arg0, batchStartsAt, batchSize);
    try {
      long finalSequence = this.sequencer.tryNext(batchSize);
      translateAndPublishBatch(translator, arg0, batchStartsAt, batchSize, finalSequence);
      return true;
    } catch (InsufficientCapacityException e) {
      return false;
    } 
  }
  
  public <A, B> void publishEvents(EventTranslatorTwoArg<E, A, B> translator, A[] arg0, B[] arg1) {
    publishEvents(translator, 0, arg0.length, arg0, arg1);
  }
  
  public <A, B> void publishEvents(EventTranslatorTwoArg<E, A, B> translator, int batchStartsAt, int batchSize, A[] arg0, B[] arg1) {
    checkBounds(arg0, arg1, batchStartsAt, batchSize);
    long finalSequence = this.sequencer.next(batchSize);
    translateAndPublishBatch(translator, arg0, arg1, batchStartsAt, batchSize, finalSequence);
  }
  
  public <A, B> boolean tryPublishEvents(EventTranslatorTwoArg<E, A, B> translator, A[] arg0, B[] arg1) {
    return tryPublishEvents(translator, 0, arg0.length, arg0, arg1);
  }
  
  public <A, B> boolean tryPublishEvents(EventTranslatorTwoArg<E, A, B> translator, int batchStartsAt, int batchSize, A[] arg0, B[] arg1) {
    checkBounds(arg0, arg1, batchStartsAt, batchSize);
    try {
      long finalSequence = this.sequencer.tryNext(batchSize);
      translateAndPublishBatch(translator, arg0, arg1, batchStartsAt, batchSize, finalSequence);
      return true;
    } catch (InsufficientCapacityException e) {
      return false;
    } 
  }
  
  public <A, B, C> void publishEvents(EventTranslatorThreeArg<E, A, B, C> translator, A[] arg0, B[] arg1, C[] arg2) {
    publishEvents(translator, 0, arg0.length, arg0, arg1, arg2);
  }
  
  public <A, B, C> void publishEvents(EventTranslatorThreeArg<E, A, B, C> translator, int batchStartsAt, int batchSize, A[] arg0, B[] arg1, C[] arg2) {
    checkBounds(arg0, arg1, arg2, batchStartsAt, batchSize);
    long finalSequence = this.sequencer.next(batchSize);
    translateAndPublishBatch(translator, arg0, arg1, arg2, batchStartsAt, batchSize, finalSequence);
  }
  
  public <A, B, C> boolean tryPublishEvents(EventTranslatorThreeArg<E, A, B, C> translator, A[] arg0, B[] arg1, C[] arg2) {
    return tryPublishEvents(translator, 0, arg0.length, arg0, arg1, arg2);
  }
  
  public <A, B, C> boolean tryPublishEvents(EventTranslatorThreeArg<E, A, B, C> translator, int batchStartsAt, int batchSize, A[] arg0, B[] arg1, C[] arg2) {
    checkBounds(arg0, arg1, arg2, batchStartsAt, batchSize);
    try {
      long finalSequence = this.sequencer.tryNext(batchSize);
      translateAndPublishBatch(translator, arg0, arg1, arg2, batchStartsAt, batchSize, finalSequence);
      return true;
    } catch (InsufficientCapacityException e) {
      return false;
    } 
  }
  
  public void publishEvents(EventTranslatorVararg<E> translator, Object[]... args) {
    publishEvents(translator, 0, args.length, args);
  }
  
  public void publishEvents(EventTranslatorVararg<E> translator, int batchStartsAt, int batchSize, Object[]... args) {
    checkBounds(batchStartsAt, batchSize, args);
    long finalSequence = this.sequencer.next(batchSize);
    translateAndPublishBatch(translator, batchStartsAt, batchSize, finalSequence, args);
  }
  
  public boolean tryPublishEvents(EventTranslatorVararg<E> translator, Object[]... args) {
    return tryPublishEvents(translator, 0, args.length, args);
  }
  
  public boolean tryPublishEvents(EventTranslatorVararg<E> translator, int batchStartsAt, int batchSize, Object[]... args) {
    checkBounds(args, batchStartsAt, batchSize);
    try {
      long finalSequence = this.sequencer.tryNext(batchSize);
      translateAndPublishBatch(translator, batchStartsAt, batchSize, finalSequence, args);
      return true;
    } catch (InsufficientCapacityException e) {
      return false;
    } 
  }
  
  public void publish(long sequence) {
    this.sequencer.publish(sequence);
  }
  
  public void publish(long lo, long hi) {
    this.sequencer.publish(lo, hi);
  }
  
  public long remainingCapacity() {
    return this.sequencer.remainingCapacity();
  }
  
  private void checkBounds(EventTranslator<E>[] translators, int batchStartsAt, int batchSize) {
    checkBatchSizing(batchStartsAt, batchSize);
    batchOverRuns(translators, batchStartsAt, batchSize);
  }
  
  private void checkBatchSizing(int batchStartsAt, int batchSize) {
    if (batchStartsAt < 0 || batchSize < 0)
      throw new IllegalArgumentException("Both batchStartsAt and batchSize must be positive but got: batchStartsAt " + batchStartsAt + " and batchSize " + batchSize); 
    if (batchSize > this.bufferSize)
      throw new IllegalArgumentException("The ring buffer cannot accommodate " + batchSize + " it only has space for " + this.bufferSize + " entities."); 
  }
  
  private <A> void checkBounds(A[] arg0, int batchStartsAt, int batchSize) {
    checkBatchSizing(batchStartsAt, batchSize);
    batchOverRuns(arg0, batchStartsAt, batchSize);
  }
  
  private <A, B> void checkBounds(A[] arg0, B[] arg1, int batchStartsAt, int batchSize) {
    checkBatchSizing(batchStartsAt, batchSize);
    batchOverRuns(arg0, batchStartsAt, batchSize);
    batchOverRuns(arg1, batchStartsAt, batchSize);
  }
  
  private <A, B, C> void checkBounds(A[] arg0, B[] arg1, C[] arg2, int batchStartsAt, int batchSize) {
    checkBatchSizing(batchStartsAt, batchSize);
    batchOverRuns(arg0, batchStartsAt, batchSize);
    batchOverRuns(arg1, batchStartsAt, batchSize);
    batchOverRuns(arg2, batchStartsAt, batchSize);
  }
  
  private void checkBounds(int batchStartsAt, int batchSize, Object[][] args) {
    checkBatchSizing(batchStartsAt, batchSize);
    batchOverRuns(args, batchStartsAt, batchSize);
  }
  
  private <A> void batchOverRuns(A[] arg0, int batchStartsAt, int batchSize) {
    if (batchStartsAt + batchSize > arg0.length)
      throw new IllegalArgumentException("A batchSize of: " + batchSize + " with batchStatsAt of: " + batchStartsAt + " will overrun the available number of arguments: " + (arg0.length - batchStartsAt)); 
  }
  
  private void translateAndPublish(EventTranslator<E> translator, long sequence) {
    try {
      translator.translateTo(get(sequence), sequence);
    } finally {
      this.sequencer.publish(sequence);
    } 
  }
  
  private <A> void translateAndPublish(EventTranslatorOneArg<E, A> translator, long sequence, A arg0) {
    try {
      translator.translateTo(get(sequence), sequence, arg0);
    } finally {
      this.sequencer.publish(sequence);
    } 
  }
  
  private <A, B> void translateAndPublish(EventTranslatorTwoArg<E, A, B> translator, long sequence, A arg0, B arg1) {
    try {
      translator.translateTo(get(sequence), sequence, arg0, arg1);
    } finally {
      this.sequencer.publish(sequence);
    } 
  }
  
  private <A, B, C> void translateAndPublish(EventTranslatorThreeArg<E, A, B, C> translator, long sequence, A arg0, B arg1, C arg2) {
    try {
      translator.translateTo(get(sequence), sequence, arg0, arg1, arg2);
    } finally {
      this.sequencer.publish(sequence);
    } 
  }
  
  private void translateAndPublish(EventTranslatorVararg<E> translator, long sequence, Object... args) {
    try {
      translator.translateTo(get(sequence), sequence, args);
    } finally {
      this.sequencer.publish(sequence);
    } 
  }
  
  private void translateAndPublishBatch(EventTranslator<E>[] translators, int batchStartsAt, int batchSize, long finalSequence) {
    long initialSequence = finalSequence - (batchSize - 1);
    try {
      long sequence = initialSequence;
      int batchEndsAt = batchStartsAt + batchSize;
      for (int i = batchStartsAt; i < batchEndsAt; i++) {
        EventTranslator<E> translator = translators[i];
        translator.translateTo(get(sequence), sequence++);
      } 
    } finally {
      this.sequencer.publish(initialSequence, finalSequence);
    } 
  }
  
  private <A> void translateAndPublishBatch(EventTranslatorOneArg<E, A> translator, A[] arg0, int batchStartsAt, int batchSize, long finalSequence) {
    long initialSequence = finalSequence - (batchSize - 1);
    try {
      long sequence = initialSequence;
      int batchEndsAt = batchStartsAt + batchSize;
      for (int i = batchStartsAt; i < batchEndsAt; i++)
        translator.translateTo(get(sequence), sequence++, arg0[i]); 
    } finally {
      this.sequencer.publish(initialSequence, finalSequence);
    } 
  }
  
  private <A, B> void translateAndPublishBatch(EventTranslatorTwoArg<E, A, B> translator, A[] arg0, B[] arg1, int batchStartsAt, int batchSize, long finalSequence) {
    long initialSequence = finalSequence - (batchSize - 1);
    try {
      long sequence = initialSequence;
      int batchEndsAt = batchStartsAt + batchSize;
      for (int i = batchStartsAt; i < batchEndsAt; i++)
        translator.translateTo(get(sequence), sequence++, arg0[i], arg1[i]); 
    } finally {
      this.sequencer.publish(initialSequence, finalSequence);
    } 
  }
  
  private <A, B, C> void translateAndPublishBatch(EventTranslatorThreeArg<E, A, B, C> translator, A[] arg0, B[] arg1, C[] arg2, int batchStartsAt, int batchSize, long finalSequence) {
    long initialSequence = finalSequence - (batchSize - 1);
    try {
      long sequence = initialSequence;
      int batchEndsAt = batchStartsAt + batchSize;
      for (int i = batchStartsAt; i < batchEndsAt; i++)
        translator.translateTo(get(sequence), sequence++, arg0[i], arg1[i], arg2[i]); 
    } finally {
      this.sequencer.publish(initialSequence, finalSequence);
    } 
  }
  
  private void translateAndPublishBatch(EventTranslatorVararg<E> translator, int batchStartsAt, int batchSize, long finalSequence, Object[][] args) {
    long initialSequence = finalSequence - (batchSize - 1);
    try {
      long sequence = initialSequence;
      int batchEndsAt = batchStartsAt + batchSize;
      for (int i = batchStartsAt; i < batchEndsAt; i++)
        translator.translateTo(get(sequence), sequence++, args[i]); 
    } finally {
      this.sequencer.publish(initialSequence, finalSequence);
    } 
  }
  
  public String toString() {
    return "RingBuffer{bufferSize=" + this.bufferSize + ", sequencer=" + this.sequencer + "}";
  }
}
