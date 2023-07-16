package com.lmax.disruptor;

public interface EventSink<E> {
  void publishEvent(EventTranslator<E> paramEventTranslator);
  
  boolean tryPublishEvent(EventTranslator<E> paramEventTranslator);
  
  <A> void publishEvent(EventTranslatorOneArg<E, A> paramEventTranslatorOneArg, A paramA);
  
  <A> boolean tryPublishEvent(EventTranslatorOneArg<E, A> paramEventTranslatorOneArg, A paramA);
  
  <A, B> void publishEvent(EventTranslatorTwoArg<E, A, B> paramEventTranslatorTwoArg, A paramA, B paramB);
  
  <A, B> boolean tryPublishEvent(EventTranslatorTwoArg<E, A, B> paramEventTranslatorTwoArg, A paramA, B paramB);
  
  <A, B, C> void publishEvent(EventTranslatorThreeArg<E, A, B, C> paramEventTranslatorThreeArg, A paramA, B paramB, C paramC);
  
  <A, B, C> boolean tryPublishEvent(EventTranslatorThreeArg<E, A, B, C> paramEventTranslatorThreeArg, A paramA, B paramB, C paramC);
  
  void publishEvent(EventTranslatorVararg<E> paramEventTranslatorVararg, Object... paramVarArgs);
  
  boolean tryPublishEvent(EventTranslatorVararg<E> paramEventTranslatorVararg, Object... paramVarArgs);
  
  void publishEvents(EventTranslator<E>[] paramArrayOfEventTranslator);
  
  void publishEvents(EventTranslator<E>[] paramArrayOfEventTranslator, int paramInt1, int paramInt2);
  
  boolean tryPublishEvents(EventTranslator<E>[] paramArrayOfEventTranslator);
  
  boolean tryPublishEvents(EventTranslator<E>[] paramArrayOfEventTranslator, int paramInt1, int paramInt2);
  
  <A> void publishEvents(EventTranslatorOneArg<E, A> paramEventTranslatorOneArg, A[] paramArrayOfA);
  
  <A> void publishEvents(EventTranslatorOneArg<E, A> paramEventTranslatorOneArg, int paramInt1, int paramInt2, A[] paramArrayOfA);
  
  <A> boolean tryPublishEvents(EventTranslatorOneArg<E, A> paramEventTranslatorOneArg, A[] paramArrayOfA);
  
  <A> boolean tryPublishEvents(EventTranslatorOneArg<E, A> paramEventTranslatorOneArg, int paramInt1, int paramInt2, A[] paramArrayOfA);
  
  <A, B> void publishEvents(EventTranslatorTwoArg<E, A, B> paramEventTranslatorTwoArg, A[] paramArrayOfA, B[] paramArrayOfB);
  
  <A, B> void publishEvents(EventTranslatorTwoArg<E, A, B> paramEventTranslatorTwoArg, int paramInt1, int paramInt2, A[] paramArrayOfA, B[] paramArrayOfB);
  
  <A, B> boolean tryPublishEvents(EventTranslatorTwoArg<E, A, B> paramEventTranslatorTwoArg, A[] paramArrayOfA, B[] paramArrayOfB);
  
  <A, B> boolean tryPublishEvents(EventTranslatorTwoArg<E, A, B> paramEventTranslatorTwoArg, int paramInt1, int paramInt2, A[] paramArrayOfA, B[] paramArrayOfB);
  
  <A, B, C> void publishEvents(EventTranslatorThreeArg<E, A, B, C> paramEventTranslatorThreeArg, A[] paramArrayOfA, B[] paramArrayOfB, C[] paramArrayOfC);
  
  <A, B, C> void publishEvents(EventTranslatorThreeArg<E, A, B, C> paramEventTranslatorThreeArg, int paramInt1, int paramInt2, A[] paramArrayOfA, B[] paramArrayOfB, C[] paramArrayOfC);
  
  <A, B, C> boolean tryPublishEvents(EventTranslatorThreeArg<E, A, B, C> paramEventTranslatorThreeArg, A[] paramArrayOfA, B[] paramArrayOfB, C[] paramArrayOfC);
  
  <A, B, C> boolean tryPublishEvents(EventTranslatorThreeArg<E, A, B, C> paramEventTranslatorThreeArg, int paramInt1, int paramInt2, A[] paramArrayOfA, B[] paramArrayOfB, C[] paramArrayOfC);
  
  void publishEvents(EventTranslatorVararg<E> paramEventTranslatorVararg, Object[]... paramVarArgs);
  
  void publishEvents(EventTranslatorVararg<E> paramEventTranslatorVararg, int paramInt1, int paramInt2, Object[]... paramVarArgs);
  
  boolean tryPublishEvents(EventTranslatorVararg<E> paramEventTranslatorVararg, Object[]... paramVarArgs);
  
  boolean tryPublishEvents(EventTranslatorVararg<E> paramEventTranslatorVararg, int paramInt1, int paramInt2, Object[]... paramVarArgs);
}
