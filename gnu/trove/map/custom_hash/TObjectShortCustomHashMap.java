package gnu.trove.map.custom_hash;

import gnu.trove.TShortCollection;
import gnu.trove.function.TShortFunction;
import gnu.trove.impl.Constants;
import gnu.trove.impl.hash.TCustomObjectHash;
import gnu.trove.impl.hash.THash;
import gnu.trove.impl.hash.TObjectHash;
import gnu.trove.iterator.TObjectShortIterator;
import gnu.trove.iterator.TShortIterator;
import gnu.trove.iterator.hash.TObjectHashIterator;
import gnu.trove.map.TObjectShortMap;
import gnu.trove.procedure.TObjectProcedure;
import gnu.trove.procedure.TObjectShortProcedure;
import gnu.trove.procedure.TShortProcedure;
import gnu.trove.strategy.HashingStrategy;
import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.lang.reflect.Array;
import java.util.AbstractSet;
import java.util.Arrays;
import java.util.Collection;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;

public class TObjectShortCustomHashMap<K> extends TCustomObjectHash<K> implements TObjectShortMap<K>, Externalizable {
  static final long serialVersionUID = 1L;
  
  private final TObjectShortProcedure<K> PUT_ALL_PROC = new TObjectShortProcedure<K>() {
      public boolean execute(K key, short value) {
        TObjectShortCustomHashMap.this.put(key, value);
        return true;
      }
    };
  
  protected transient short[] _values;
  
  protected short no_entry_value;
  
  public TObjectShortCustomHashMap(HashingStrategy<? super K> strategy) {
    super(strategy);
    this.no_entry_value = Constants.DEFAULT_SHORT_NO_ENTRY_VALUE;
  }
  
  public TObjectShortCustomHashMap(HashingStrategy<? super K> strategy, int initialCapacity) {
    super(strategy, initialCapacity);
    this.no_entry_value = Constants.DEFAULT_SHORT_NO_ENTRY_VALUE;
  }
  
  public TObjectShortCustomHashMap(HashingStrategy<? super K> strategy, int initialCapacity, float loadFactor) {
    super(strategy, initialCapacity, loadFactor);
    this.no_entry_value = Constants.DEFAULT_SHORT_NO_ENTRY_VALUE;
  }
  
  public TObjectShortCustomHashMap(HashingStrategy<? super K> strategy, int initialCapacity, float loadFactor, short noEntryValue) {
    super(strategy, initialCapacity, loadFactor);
    this.no_entry_value = noEntryValue;
    if (this.no_entry_value != 0)
      Arrays.fill(this._values, this.no_entry_value); 
  }
  
  public TObjectShortCustomHashMap(HashingStrategy<? super K> strategy, TObjectShortMap<? extends K> map) {
    this(strategy, map.size(), 0.5F, map.getNoEntryValue());
    if (map instanceof TObjectShortCustomHashMap) {
      TObjectShortCustomHashMap hashmap = (TObjectShortCustomHashMap)map;
      this._loadFactor = Math.abs(hashmap._loadFactor);
      this.no_entry_value = hashmap.no_entry_value;
      this.strategy = hashmap.strategy;
      if (this.no_entry_value != 0)
        Arrays.fill(this._values, this.no_entry_value); 
      setUp(saturatedCast(fastCeil(10.0D / this._loadFactor)));
    } 
    putAll(map);
  }
  
  public int setUp(int initialCapacity) {
    int capacity = super.setUp(initialCapacity);
    this._values = new short[capacity];
    return capacity;
  }
  
  protected void rehash(int newCapacity) {
    // Byte code:
    //   0: aload_0
    //   1: getfield _set : [Ljava/lang/Object;
    //   4: arraylength
    //   5: istore_2
    //   6: aload_0
    //   7: getfield _set : [Ljava/lang/Object;
    //   10: checkcast [Ljava/lang/Object;
    //   13: astore_3
    //   14: aload_0
    //   15: getfield _values : [S
    //   18: astore #4
    //   20: aload_0
    //   21: iload_1
    //   22: anewarray java/lang/Object
    //   25: putfield _set : [Ljava/lang/Object;
    //   28: aload_0
    //   29: getfield _set : [Ljava/lang/Object;
    //   32: getstatic gnu/trove/map/custom_hash/TObjectShortCustomHashMap.FREE : Ljava/lang/Object;
    //   35: invokestatic fill : ([Ljava/lang/Object;Ljava/lang/Object;)V
    //   38: aload_0
    //   39: iload_1
    //   40: newarray short
    //   42: putfield _values : [S
    //   45: aload_0
    //   46: getfield _values : [S
    //   49: aload_0
    //   50: getfield no_entry_value : S
    //   53: invokestatic fill : ([SS)V
    //   56: iload_2
    //   57: istore #5
    //   59: iload #5
    //   61: iinc #5, -1
    //   64: ifle -> 133
    //   67: aload_3
    //   68: iload #5
    //   70: aaload
    //   71: astore #6
    //   73: aload #6
    //   75: getstatic gnu/trove/map/custom_hash/TObjectShortCustomHashMap.FREE : Ljava/lang/Object;
    //   78: if_acmpeq -> 130
    //   81: aload #6
    //   83: getstatic gnu/trove/map/custom_hash/TObjectShortCustomHashMap.REMOVED : Ljava/lang/Object;
    //   86: if_acmpeq -> 130
    //   89: aload_0
    //   90: aload #6
    //   92: invokevirtual insertKey : (Ljava/lang/Object;)I
    //   95: istore #7
    //   97: iload #7
    //   99: ifge -> 118
    //   102: aload_0
    //   103: aload_0
    //   104: getfield _set : [Ljava/lang/Object;
    //   107: iload #7
    //   109: ineg
    //   110: iconst_1
    //   111: isub
    //   112: aaload
    //   113: aload #6
    //   115: invokevirtual throwObjectContractViolation : (Ljava/lang/Object;Ljava/lang/Object;)V
    //   118: aload_0
    //   119: getfield _values : [S
    //   122: iload #7
    //   124: aload #4
    //   126: iload #5
    //   128: saload
    //   129: sastore
    //   130: goto -> 59
    //   133: return
    // Line number table:
    //   Java source line number -> byte code offset
    //   #189	-> 0
    //   #192	-> 6
    //   #193	-> 14
    //   #195	-> 20
    //   #196	-> 28
    //   #197	-> 38
    //   #198	-> 45
    //   #200	-> 56
    //   #201	-> 67
    //   #202	-> 73
    //   #203	-> 89
    //   #204	-> 97
    //   #205	-> 102
    //   #207	-> 118
    //   #209	-> 130
    //   #210	-> 133
    // Local variable table:
    //   start	length	slot	name	descriptor
    //   97	33	7	index	I
    //   73	57	6	o	Ljava/lang/Object;
    //   59	74	5	i	I
    //   0	134	0	this	Lgnu/trove/map/custom_hash/TObjectShortCustomHashMap;
    //   0	134	1	newCapacity	I
    //   6	128	2	oldCapacity	I
    //   14	120	3	oldKeys	[Ljava/lang/Object;
    //   20	114	4	oldVals	[S
    // Local variable type table:
    //   start	length	slot	name	signature
    //   73	57	6	o	TK;
    //   0	134	0	this	Lgnu/trove/map/custom_hash/TObjectShortCustomHashMap<TK;>;
    //   14	120	3	oldKeys	[TK;
  }
  
  public short getNoEntryValue() {
    return this.no_entry_value;
  }
  
  public boolean containsKey(Object key) {
    return contains(key);
  }
  
  public boolean containsValue(short val) {
    // Byte code:
    //   0: aload_0
    //   1: getfield _set : [Ljava/lang/Object;
    //   4: astore_2
    //   5: aload_0
    //   6: getfield _values : [S
    //   9: astore_3
    //   10: aload_3
    //   11: arraylength
    //   12: istore #4
    //   14: iload #4
    //   16: iinc #4, -1
    //   19: ifle -> 52
    //   22: aload_2
    //   23: iload #4
    //   25: aaload
    //   26: getstatic gnu/trove/map/custom_hash/TObjectShortCustomHashMap.FREE : Ljava/lang/Object;
    //   29: if_acmpeq -> 14
    //   32: aload_2
    //   33: iload #4
    //   35: aaload
    //   36: getstatic gnu/trove/map/custom_hash/TObjectShortCustomHashMap.REMOVED : Ljava/lang/Object;
    //   39: if_acmpeq -> 14
    //   42: iload_1
    //   43: aload_3
    //   44: iload #4
    //   46: saload
    //   47: if_icmpne -> 14
    //   50: iconst_1
    //   51: ireturn
    //   52: iconst_0
    //   53: ireturn
    // Line number table:
    //   Java source line number -> byte code offset
    //   #229	-> 0
    //   #230	-> 5
    //   #232	-> 10
    //   #233	-> 22
    //   #234	-> 50
    //   #237	-> 52
    // Local variable table:
    //   start	length	slot	name	descriptor
    //   14	38	4	i	I
    //   0	54	0	this	Lgnu/trove/map/custom_hash/TObjectShortCustomHashMap;
    //   0	54	1	val	S
    //   5	49	2	keys	[Ljava/lang/Object;
    //   10	44	3	vals	[S
    // Local variable type table:
    //   start	length	slot	name	signature
    //   0	54	0	this	Lgnu/trove/map/custom_hash/TObjectShortCustomHashMap<TK;>;
  }
  
  public short get(Object key) {
    int index = index(key);
    return (index < 0) ? this.no_entry_value : this._values[index];
  }
  
  public short put(K key, short value) {
    int index = insertKey(key);
    return doPut(value, index);
  }
  
  public short putIfAbsent(K key, short value) {
    int index = insertKey(key);
    if (index < 0)
      return this._values[-index - 1]; 
    return doPut(value, index);
  }
  
  private short doPut(short value, int index) {
    short previous = this.no_entry_value;
    boolean isNewMapping = true;
    if (index < 0) {
      index = -index - 1;
      previous = this._values[index];
      isNewMapping = false;
    } 
    this._values[index] = value;
    if (isNewMapping)
      postInsertHook(this.consumeFreeSlot); 
    return previous;
  }
  
  public short remove(Object key) {
    short prev = this.no_entry_value;
    int index = index(key);
    if (index >= 0) {
      prev = this._values[index];
      removeAt(index);
    } 
    return prev;
  }
  
  protected void removeAt(int index) {
    this._values[index] = this.no_entry_value;
    super.removeAt(index);
  }
  
  public void putAll(Map<? extends K, ? extends Short> map) {
    Set<? extends Map.Entry<? extends K, ? extends Short>> set = map.entrySet();
    for (Map.Entry<? extends K, ? extends Short> entry : set)
      put(entry.getKey(), ((Short)entry.getValue()).shortValue()); 
  }
  
  public void putAll(TObjectShortMap<? extends K> map) {
    map.forEachEntry(this.PUT_ALL_PROC);
  }
  
  public void clear() {
    // Byte code:
    //   0: aload_0
    //   1: invokespecial clear : ()V
    //   4: aload_0
    //   5: getfield _set : [Ljava/lang/Object;
    //   8: iconst_0
    //   9: aload_0
    //   10: getfield _set : [Ljava/lang/Object;
    //   13: arraylength
    //   14: getstatic gnu/trove/map/custom_hash/TObjectShortCustomHashMap.FREE : Ljava/lang/Object;
    //   17: invokestatic fill : ([Ljava/lang/Object;IILjava/lang/Object;)V
    //   20: aload_0
    //   21: getfield _values : [S
    //   24: iconst_0
    //   25: aload_0
    //   26: getfield _values : [S
    //   29: arraylength
    //   30: aload_0
    //   31: getfield no_entry_value : S
    //   34: invokestatic fill : ([SIIS)V
    //   37: return
    // Line number table:
    //   Java source line number -> byte code offset
    //   #328	-> 0
    //   #329	-> 4
    //   #330	-> 20
    //   #331	-> 37
    // Local variable table:
    //   start	length	slot	name	descriptor
    //   0	38	0	this	Lgnu/trove/map/custom_hash/TObjectShortCustomHashMap;
    // Local variable type table:
    //   start	length	slot	name	signature
    //   0	38	0	this	Lgnu/trove/map/custom_hash/TObjectShortCustomHashMap<TK;>;
  }
  
  public Set<K> keySet() {
    return new KeyView();
  }
  
  public Object[] keys() {
    // Byte code:
    //   0: aload_0
    //   1: invokevirtual size : ()I
    //   4: anewarray java/lang/Object
    //   7: checkcast [Ljava/lang/Object;
    //   10: astore_1
    //   11: aload_0
    //   12: getfield _set : [Ljava/lang/Object;
    //   15: astore_2
    //   16: aload_2
    //   17: arraylength
    //   18: istore_3
    //   19: iconst_0
    //   20: istore #4
    //   22: iload_3
    //   23: iinc #3, -1
    //   26: ifle -> 60
    //   29: aload_2
    //   30: iload_3
    //   31: aaload
    //   32: getstatic gnu/trove/map/custom_hash/TObjectShortCustomHashMap.FREE : Ljava/lang/Object;
    //   35: if_acmpeq -> 22
    //   38: aload_2
    //   39: iload_3
    //   40: aaload
    //   41: getstatic gnu/trove/map/custom_hash/TObjectShortCustomHashMap.REMOVED : Ljava/lang/Object;
    //   44: if_acmpeq -> 22
    //   47: aload_1
    //   48: iload #4
    //   50: iinc #4, 1
    //   53: aload_2
    //   54: iload_3
    //   55: aaload
    //   56: aastore
    //   57: goto -> 22
    //   60: aload_1
    //   61: areturn
    // Line number table:
    //   Java source line number -> byte code offset
    //   #345	-> 0
    //   #346	-> 11
    //   #348	-> 16
    //   #349	-> 29
    //   #351	-> 47
    //   #354	-> 60
    // Local variable table:
    //   start	length	slot	name	descriptor
    //   19	41	3	i	I
    //   22	38	4	j	I
    //   0	62	0	this	Lgnu/trove/map/custom_hash/TObjectShortCustomHashMap;
    //   11	51	1	keys	[Ljava/lang/Object;
    //   16	46	2	k	[Ljava/lang/Object;
    // Local variable type table:
    //   start	length	slot	name	signature
    //   0	62	0	this	Lgnu/trove/map/custom_hash/TObjectShortCustomHashMap<TK;>;
    //   11	51	1	keys	[TK;
  }
  
  public K[] keys(K[] a) {
    // Byte code:
    //   0: aload_0
    //   1: invokevirtual size : ()I
    //   4: istore_2
    //   5: aload_1
    //   6: arraylength
    //   7: iload_2
    //   8: if_icmpge -> 29
    //   11: aload_1
    //   12: invokevirtual getClass : ()Ljava/lang/Class;
    //   15: invokevirtual getComponentType : ()Ljava/lang/Class;
    //   18: iload_2
    //   19: invokestatic newInstance : (Ljava/lang/Class;I)Ljava/lang/Object;
    //   22: checkcast [Ljava/lang/Object;
    //   25: checkcast [Ljava/lang/Object;
    //   28: astore_1
    //   29: aload_0
    //   30: getfield _set : [Ljava/lang/Object;
    //   33: astore_3
    //   34: aload_3
    //   35: arraylength
    //   36: istore #4
    //   38: iconst_0
    //   39: istore #5
    //   41: iload #4
    //   43: iinc #4, -1
    //   46: ifle -> 83
    //   49: aload_3
    //   50: iload #4
    //   52: aaload
    //   53: getstatic gnu/trove/map/custom_hash/TObjectShortCustomHashMap.FREE : Ljava/lang/Object;
    //   56: if_acmpeq -> 41
    //   59: aload_3
    //   60: iload #4
    //   62: aaload
    //   63: getstatic gnu/trove/map/custom_hash/TObjectShortCustomHashMap.REMOVED : Ljava/lang/Object;
    //   66: if_acmpeq -> 41
    //   69: aload_1
    //   70: iload #5
    //   72: iinc #5, 1
    //   75: aload_3
    //   76: iload #4
    //   78: aaload
    //   79: aastore
    //   80: goto -> 41
    //   83: aload_1
    //   84: areturn
    // Line number table:
    //   Java source line number -> byte code offset
    //   #360	-> 0
    //   #361	-> 5
    //   #363	-> 11
    //   #364	-> 12
    //   #363	-> 19
    //   #367	-> 29
    //   #369	-> 34
    //   #370	-> 49
    //   #372	-> 69
    //   #375	-> 83
    // Local variable table:
    //   start	length	slot	name	descriptor
    //   38	45	4	i	I
    //   41	42	5	j	I
    //   0	85	0	this	Lgnu/trove/map/custom_hash/TObjectShortCustomHashMap;
    //   0	85	1	a	[Ljava/lang/Object;
    //   5	80	2	size	I
    //   34	51	3	k	[Ljava/lang/Object;
    // Local variable type table:
    //   start	length	slot	name	signature
    //   0	85	0	this	Lgnu/trove/map/custom_hash/TObjectShortCustomHashMap<TK;>;
    //   0	85	1	a	[TK;
  }
  
  public TShortCollection valueCollection() {
    return new TShortValueCollection();
  }
  
  public short[] values() {
    // Byte code:
    //   0: aload_0
    //   1: invokevirtual size : ()I
    //   4: newarray short
    //   6: astore_1
    //   7: aload_0
    //   8: getfield _values : [S
    //   11: astore_2
    //   12: aload_0
    //   13: getfield _set : [Ljava/lang/Object;
    //   16: astore_3
    //   17: aload_2
    //   18: arraylength
    //   19: istore #4
    //   21: iconst_0
    //   22: istore #5
    //   24: iload #4
    //   26: iinc #4, -1
    //   29: ifle -> 66
    //   32: aload_3
    //   33: iload #4
    //   35: aaload
    //   36: getstatic gnu/trove/map/custom_hash/TObjectShortCustomHashMap.FREE : Ljava/lang/Object;
    //   39: if_acmpeq -> 24
    //   42: aload_3
    //   43: iload #4
    //   45: aaload
    //   46: getstatic gnu/trove/map/custom_hash/TObjectShortCustomHashMap.REMOVED : Ljava/lang/Object;
    //   49: if_acmpeq -> 24
    //   52: aload_1
    //   53: iload #5
    //   55: iinc #5, 1
    //   58: aload_2
    //   59: iload #4
    //   61: saload
    //   62: sastore
    //   63: goto -> 24
    //   66: aload_1
    //   67: areturn
    // Line number table:
    //   Java source line number -> byte code offset
    //   #387	-> 0
    //   #388	-> 7
    //   #389	-> 12
    //   #391	-> 17
    //   #392	-> 32
    //   #393	-> 52
    //   #396	-> 66
    // Local variable table:
    //   start	length	slot	name	descriptor
    //   21	45	4	i	I
    //   24	42	5	j	I
    //   0	68	0	this	Lgnu/trove/map/custom_hash/TObjectShortCustomHashMap;
    //   7	61	1	vals	[S
    //   12	56	2	v	[S
    //   17	51	3	keys	[Ljava/lang/Object;
    // Local variable type table:
    //   start	length	slot	name	signature
    //   0	68	0	this	Lgnu/trove/map/custom_hash/TObjectShortCustomHashMap<TK;>;
  }
  
  public short[] values(short[] array) {
    // Byte code:
    //   0: aload_0
    //   1: invokevirtual size : ()I
    //   4: istore_2
    //   5: aload_1
    //   6: arraylength
    //   7: iload_2
    //   8: if_icmpge -> 15
    //   11: iload_2
    //   12: newarray short
    //   14: astore_1
    //   15: aload_0
    //   16: getfield _values : [S
    //   19: astore_3
    //   20: aload_0
    //   21: getfield _set : [Ljava/lang/Object;
    //   24: astore #4
    //   26: aload_3
    //   27: arraylength
    //   28: istore #5
    //   30: iconst_0
    //   31: istore #6
    //   33: iload #5
    //   35: iinc #5, -1
    //   38: ifle -> 77
    //   41: aload #4
    //   43: iload #5
    //   45: aaload
    //   46: getstatic gnu/trove/map/custom_hash/TObjectShortCustomHashMap.FREE : Ljava/lang/Object;
    //   49: if_acmpeq -> 33
    //   52: aload #4
    //   54: iload #5
    //   56: aaload
    //   57: getstatic gnu/trove/map/custom_hash/TObjectShortCustomHashMap.REMOVED : Ljava/lang/Object;
    //   60: if_acmpeq -> 33
    //   63: aload_1
    //   64: iload #6
    //   66: iinc #6, 1
    //   69: aload_3
    //   70: iload #5
    //   72: saload
    //   73: sastore
    //   74: goto -> 33
    //   77: aload_1
    //   78: arraylength
    //   79: iload_2
    //   80: if_icmple -> 90
    //   83: aload_1
    //   84: iload_2
    //   85: aload_0
    //   86: getfield no_entry_value : S
    //   89: sastore
    //   90: aload_1
    //   91: areturn
    // Line number table:
    //   Java source line number -> byte code offset
    //   #402	-> 0
    //   #403	-> 5
    //   #404	-> 11
    //   #407	-> 15
    //   #408	-> 20
    //   #410	-> 26
    //   #411	-> 41
    //   #412	-> 63
    //   #415	-> 77
    //   #416	-> 83
    //   #418	-> 90
    // Local variable table:
    //   start	length	slot	name	descriptor
    //   30	47	5	i	I
    //   33	44	6	j	I
    //   0	92	0	this	Lgnu/trove/map/custom_hash/TObjectShortCustomHashMap;
    //   0	92	1	array	[S
    //   5	87	2	size	I
    //   20	72	3	v	[S
    //   26	66	4	keys	[Ljava/lang/Object;
    // Local variable type table:
    //   start	length	slot	name	signature
    //   0	92	0	this	Lgnu/trove/map/custom_hash/TObjectShortCustomHashMap<TK;>;
  }
  
  public TObjectShortIterator<K> iterator() {
    return new TObjectShortHashIterator<K>(this);
  }
  
  public boolean increment(K key) {
    return adjustValue(key, (short)1);
  }
  
  public boolean adjustValue(K key, short amount) {
    int index = index(key);
    if (index < 0)
      return false; 
    this._values[index] = (short)(this._values[index] + amount);
    return true;
  }
  
  public short adjustOrPutValue(K key, short adjust_amount, short put_amount) {
    boolean isNewMapping;
    short newValue;
    int index = insertKey(key);
    if (index < 0) {
      index = -index - 1;
      newValue = this._values[index] = (short)(this._values[index] + adjust_amount);
      isNewMapping = false;
    } else {
      newValue = this._values[index] = put_amount;
      isNewMapping = true;
    } 
    if (isNewMapping)
      postInsertHook(this.consumeFreeSlot); 
    return newValue;
  }
  
  public boolean forEachKey(TObjectProcedure<? super K> procedure) {
    return forEach(procedure);
  }
  
  public boolean forEachValue(TShortProcedure procedure) {
    // Byte code:
    //   0: aload_0
    //   1: getfield _set : [Ljava/lang/Object;
    //   4: astore_2
    //   5: aload_0
    //   6: getfield _values : [S
    //   9: astore_3
    //   10: aload_3
    //   11: arraylength
    //   12: istore #4
    //   14: iload #4
    //   16: iinc #4, -1
    //   19: ifle -> 57
    //   22: aload_2
    //   23: iload #4
    //   25: aaload
    //   26: getstatic gnu/trove/map/custom_hash/TObjectShortCustomHashMap.FREE : Ljava/lang/Object;
    //   29: if_acmpeq -> 14
    //   32: aload_2
    //   33: iload #4
    //   35: aaload
    //   36: getstatic gnu/trove/map/custom_hash/TObjectShortCustomHashMap.REMOVED : Ljava/lang/Object;
    //   39: if_acmpeq -> 14
    //   42: aload_1
    //   43: aload_3
    //   44: iload #4
    //   46: saload
    //   47: invokeinterface execute : (S)Z
    //   52: ifne -> 14
    //   55: iconst_0
    //   56: ireturn
    //   57: iconst_1
    //   58: ireturn
    // Line number table:
    //   Java source line number -> byte code offset
    //   #495	-> 0
    //   #496	-> 5
    //   #497	-> 10
    //   #498	-> 22
    //   #499	-> 47
    //   #500	-> 55
    //   #503	-> 57
    // Local variable table:
    //   start	length	slot	name	descriptor
    //   14	43	4	i	I
    //   0	59	0	this	Lgnu/trove/map/custom_hash/TObjectShortCustomHashMap;
    //   0	59	1	procedure	Lgnu/trove/procedure/TShortProcedure;
    //   5	54	2	keys	[Ljava/lang/Object;
    //   10	49	3	values	[S
    // Local variable type table:
    //   start	length	slot	name	signature
    //   0	59	0	this	Lgnu/trove/map/custom_hash/TObjectShortCustomHashMap<TK;>;
  }
  
  public boolean forEachEntry(TObjectShortProcedure<? super K> procedure) {
    // Byte code:
    //   0: aload_0
    //   1: getfield _set : [Ljava/lang/Object;
    //   4: astore_2
    //   5: aload_0
    //   6: getfield _values : [S
    //   9: astore_3
    //   10: aload_2
    //   11: arraylength
    //   12: istore #4
    //   14: iload #4
    //   16: iinc #4, -1
    //   19: ifle -> 61
    //   22: aload_2
    //   23: iload #4
    //   25: aaload
    //   26: getstatic gnu/trove/map/custom_hash/TObjectShortCustomHashMap.FREE : Ljava/lang/Object;
    //   29: if_acmpeq -> 14
    //   32: aload_2
    //   33: iload #4
    //   35: aaload
    //   36: getstatic gnu/trove/map/custom_hash/TObjectShortCustomHashMap.REMOVED : Ljava/lang/Object;
    //   39: if_acmpeq -> 14
    //   42: aload_1
    //   43: aload_2
    //   44: iload #4
    //   46: aaload
    //   47: aload_3
    //   48: iload #4
    //   50: saload
    //   51: invokeinterface execute : (Ljava/lang/Object;S)Z
    //   56: ifne -> 14
    //   59: iconst_0
    //   60: ireturn
    //   61: iconst_1
    //   62: ireturn
    // Line number table:
    //   Java source line number -> byte code offset
    //   #517	-> 0
    //   #518	-> 5
    //   #519	-> 10
    //   #520	-> 22
    //   #522	-> 51
    //   #523	-> 59
    //   #526	-> 61
    // Local variable table:
    //   start	length	slot	name	descriptor
    //   14	47	4	i	I
    //   0	63	0	this	Lgnu/trove/map/custom_hash/TObjectShortCustomHashMap;
    //   0	63	1	procedure	Lgnu/trove/procedure/TObjectShortProcedure;
    //   5	58	2	keys	[Ljava/lang/Object;
    //   10	53	3	values	[S
    // Local variable type table:
    //   start	length	slot	name	signature
    //   0	63	0	this	Lgnu/trove/map/custom_hash/TObjectShortCustomHashMap<TK;>;
    //   0	63	1	procedure	Lgnu/trove/procedure/TObjectShortProcedure<-TK;>;
  }
  
  public boolean retainEntries(TObjectShortProcedure<? super K> procedure) {
    // Byte code:
    //   0: iconst_0
    //   1: istore_2
    //   2: aload_0
    //   3: getfield _set : [Ljava/lang/Object;
    //   6: checkcast [Ljava/lang/Object;
    //   9: astore_3
    //   10: aload_0
    //   11: getfield _values : [S
    //   14: astore #4
    //   16: aload_0
    //   17: invokevirtual tempDisableAutoCompaction : ()V
    //   20: aload_3
    //   21: arraylength
    //   22: istore #5
    //   24: iload #5
    //   26: iinc #5, -1
    //   29: ifle -> 81
    //   32: aload_3
    //   33: iload #5
    //   35: aaload
    //   36: getstatic gnu/trove/map/custom_hash/TObjectShortCustomHashMap.FREE : Ljava/lang/Object;
    //   39: if_acmpeq -> 24
    //   42: aload_3
    //   43: iload #5
    //   45: aaload
    //   46: getstatic gnu/trove/map/custom_hash/TObjectShortCustomHashMap.REMOVED : Ljava/lang/Object;
    //   49: if_acmpeq -> 24
    //   52: aload_1
    //   53: aload_3
    //   54: iload #5
    //   56: aaload
    //   57: aload #4
    //   59: iload #5
    //   61: saload
    //   62: invokeinterface execute : (Ljava/lang/Object;S)Z
    //   67: ifne -> 24
    //   70: aload_0
    //   71: iload #5
    //   73: invokevirtual removeAt : (I)V
    //   76: iconst_1
    //   77: istore_2
    //   78: goto -> 24
    //   81: aload_0
    //   82: iconst_1
    //   83: invokevirtual reenableAutoCompaction : (Z)V
    //   86: goto -> 99
    //   89: astore #6
    //   91: aload_0
    //   92: iconst_1
    //   93: invokevirtual reenableAutoCompaction : (Z)V
    //   96: aload #6
    //   98: athrow
    //   99: iload_2
    //   100: ireturn
    // Line number table:
    //   Java source line number -> byte code offset
    //   #538	-> 0
    //   #540	-> 2
    //   #541	-> 10
    //   #544	-> 16
    //   #546	-> 20
    //   #547	-> 32
    //   #549	-> 62
    //   #550	-> 70
    //   #551	-> 76
    //   #556	-> 81
    //   #557	-> 86
    //   #556	-> 89
    //   #559	-> 99
    // Local variable table:
    //   start	length	slot	name	descriptor
    //   24	57	5	i	I
    //   0	101	0	this	Lgnu/trove/map/custom_hash/TObjectShortCustomHashMap;
    //   0	101	1	procedure	Lgnu/trove/procedure/TObjectShortProcedure;
    //   2	99	2	modified	Z
    //   10	91	3	keys	[Ljava/lang/Object;
    //   16	85	4	values	[S
    // Local variable type table:
    //   start	length	slot	name	signature
    //   0	101	0	this	Lgnu/trove/map/custom_hash/TObjectShortCustomHashMap<TK;>;
    //   0	101	1	procedure	Lgnu/trove/procedure/TObjectShortProcedure<-TK;>;
    //   10	91	3	keys	[TK;
    // Exception table:
    //   from	to	target	type
    //   20	81	89	finally
    //   89	91	89	finally
  }
  
  public void transformValues(TShortFunction function) {
    // Byte code:
    //   0: aload_0
    //   1: getfield _set : [Ljava/lang/Object;
    //   4: astore_2
    //   5: aload_0
    //   6: getfield _values : [S
    //   9: astore_3
    //   10: aload_3
    //   11: arraylength
    //   12: istore #4
    //   14: iload #4
    //   16: iinc #4, -1
    //   19: ifle -> 56
    //   22: aload_2
    //   23: iload #4
    //   25: aaload
    //   26: ifnull -> 14
    //   29: aload_2
    //   30: iload #4
    //   32: aaload
    //   33: getstatic gnu/trove/map/custom_hash/TObjectShortCustomHashMap.REMOVED : Ljava/lang/Object;
    //   36: if_acmpeq -> 14
    //   39: aload_3
    //   40: iload #4
    //   42: aload_1
    //   43: aload_3
    //   44: iload #4
    //   46: saload
    //   47: invokeinterface execute : (S)S
    //   52: sastore
    //   53: goto -> 14
    //   56: return
    // Line number table:
    //   Java source line number -> byte code offset
    //   #569	-> 0
    //   #570	-> 5
    //   #571	-> 10
    //   #572	-> 22
    //   #573	-> 39
    //   #576	-> 56
    // Local variable table:
    //   start	length	slot	name	descriptor
    //   14	42	4	i	I
    //   0	57	0	this	Lgnu/trove/map/custom_hash/TObjectShortCustomHashMap;
    //   0	57	1	function	Lgnu/trove/function/TShortFunction;
    //   5	52	2	keys	[Ljava/lang/Object;
    //   10	47	3	values	[S
    // Local variable type table:
    //   start	length	slot	name	signature
    //   0	57	0	this	Lgnu/trove/map/custom_hash/TObjectShortCustomHashMap<TK;>;
  }
  
  public boolean equals(Object other) {
    if (!(other instanceof TObjectShortMap))
      return false; 
    TObjectShortMap that = (TObjectShortMap)other;
    if (that.size() != size())
      return false; 
    try {
      TObjectShortIterator<K> iter = iterator();
      while (iter.hasNext()) {
        iter.advance();
        Object key = iter.key();
        short value = iter.value();
        if (value == this.no_entry_value) {
          if (that.get(key) != that.getNoEntryValue() || 
            !that.containsKey(key))
            return false; 
          continue;
        } 
        if (value != that.get(key))
          return false; 
      } 
    } catch (ClassCastException classCastException) {}
    return true;
  }
  
  public int hashCode() {
    // Byte code:
    //   0: iconst_0
    //   1: istore_1
    //   2: aload_0
    //   3: getfield _set : [Ljava/lang/Object;
    //   6: astore_2
    //   7: aload_0
    //   8: getfield _values : [S
    //   11: astore_3
    //   12: aload_3
    //   13: arraylength
    //   14: istore #4
    //   16: iload #4
    //   18: iinc #4, -1
    //   21: ifle -> 76
    //   24: aload_2
    //   25: iload #4
    //   27: aaload
    //   28: getstatic gnu/trove/map/custom_hash/TObjectShortCustomHashMap.FREE : Ljava/lang/Object;
    //   31: if_acmpeq -> 16
    //   34: aload_2
    //   35: iload #4
    //   37: aaload
    //   38: getstatic gnu/trove/map/custom_hash/TObjectShortCustomHashMap.REMOVED : Ljava/lang/Object;
    //   41: if_acmpeq -> 16
    //   44: iload_1
    //   45: aload_3
    //   46: iload #4
    //   48: saload
    //   49: invokestatic hash : (I)I
    //   52: aload_2
    //   53: iload #4
    //   55: aaload
    //   56: ifnonnull -> 63
    //   59: iconst_0
    //   60: goto -> 70
    //   63: aload_2
    //   64: iload #4
    //   66: aaload
    //   67: invokevirtual hashCode : ()I
    //   70: ixor
    //   71: iadd
    //   72: istore_1
    //   73: goto -> 16
    //   76: iload_1
    //   77: ireturn
    // Line number table:
    //   Java source line number -> byte code offset
    //   #623	-> 0
    //   #624	-> 2
    //   #625	-> 7
    //   #626	-> 12
    //   #627	-> 24
    //   #628	-> 44
    //   #629	-> 67
    //   #632	-> 76
    // Local variable table:
    //   start	length	slot	name	descriptor
    //   16	60	4	i	I
    //   0	78	0	this	Lgnu/trove/map/custom_hash/TObjectShortCustomHashMap;
    //   2	76	1	hashcode	I
    //   7	71	2	keys	[Ljava/lang/Object;
    //   12	66	3	values	[S
    // Local variable type table:
    //   start	length	slot	name	signature
    //   0	78	0	this	Lgnu/trove/map/custom_hash/TObjectShortCustomHashMap<TK;>;
  }
  
  protected class KeyView extends MapBackedView<K> {
    public Iterator<K> iterator() {
      return (Iterator<K>)new TObjectHashIterator((TObjectHash)TObjectShortCustomHashMap.this);
    }
    
    public boolean removeElement(K key) {
      return (TObjectShortCustomHashMap.this.no_entry_value != TObjectShortCustomHashMap.this.remove(key));
    }
    
    public boolean containsElement(K key) {
      return TObjectShortCustomHashMap.this.contains(key);
    }
  }
  
  private abstract class MapBackedView<E> extends AbstractSet<E> implements Set<E>, Iterable<E> {
    private MapBackedView() {}
    
    public boolean contains(Object key) {
      return containsElement((E)key);
    }
    
    public boolean remove(Object o) {
      return removeElement((E)o);
    }
    
    public void clear() {
      TObjectShortCustomHashMap.this.clear();
    }
    
    public boolean add(E obj) {
      throw new UnsupportedOperationException();
    }
    
    public int size() {
      return TObjectShortCustomHashMap.this.size();
    }
    
    public Object[] toArray() {
      Object[] result = new Object[size()];
      Iterator<E> e = iterator();
      for (int i = 0; e.hasNext(); i++)
        result[i] = e.next(); 
      return result;
    }
    
    public <T> T[] toArray(T[] a) {
      int size = size();
      if (a.length < size)
        a = (T[])Array.newInstance(a
            .getClass().getComponentType(), size); 
      Iterator<E> it = iterator();
      T[] arrayOfT = a;
      for (int i = 0; i < size; i++)
        arrayOfT[i] = (T)it.next(); 
      if (a.length > size)
        a[size] = null; 
      return a;
    }
    
    public boolean isEmpty() {
      return TObjectShortCustomHashMap.this.isEmpty();
    }
    
    public boolean addAll(Collection<? extends E> collection) {
      throw new UnsupportedOperationException();
    }
    
    public boolean retainAll(Collection<?> collection) {
      boolean changed = false;
      Iterator<E> i = iterator();
      while (i.hasNext()) {
        if (!collection.contains(i.next())) {
          i.remove();
          changed = true;
        } 
      } 
      return changed;
    }
    
    public abstract boolean removeElement(E param1E);
    
    public abstract boolean containsElement(E param1E);
  }
  
  class TShortValueCollection implements TShortCollection {
    public TShortIterator iterator() {
      return new TObjectShortValueHashIterator();
    }
    
    public short getNoEntryValue() {
      return TObjectShortCustomHashMap.this.no_entry_value;
    }
    
    public int size() {
      return TObjectShortCustomHashMap.this._size;
    }
    
    public boolean isEmpty() {
      return (0 == TObjectShortCustomHashMap.this._size);
    }
    
    public boolean contains(short entry) {
      return TObjectShortCustomHashMap.this.containsValue(entry);
    }
    
    public short[] toArray() {
      return TObjectShortCustomHashMap.this.values();
    }
    
    public short[] toArray(short[] dest) {
      return TObjectShortCustomHashMap.this.values(dest);
    }
    
    public boolean add(short entry) {
      throw new UnsupportedOperationException();
    }
    
    public boolean remove(short entry) {
      short[] values = TObjectShortCustomHashMap.this._values;
      Object[] set = TObjectShortCustomHashMap.this._set;
      for (int i = values.length; i-- > 0;) {
        if (set[i] != TObjectHash.FREE && set[i] != TObjectHash.REMOVED && entry == values[i]) {
          TObjectShortCustomHashMap.this.removeAt(i);
          return true;
        } 
      } 
      return false;
    }
    
    public boolean containsAll(Collection<?> collection) {
      for (Object element : collection) {
        if (element instanceof Short) {
          short ele = ((Short)element).shortValue();
          if (!TObjectShortCustomHashMap.this.containsValue(ele))
            return false; 
          continue;
        } 
        return false;
      } 
      return true;
    }
    
    public boolean containsAll(TShortCollection collection) {
      TShortIterator iter = collection.iterator();
      while (iter.hasNext()) {
        if (!TObjectShortCustomHashMap.this.containsValue(iter.next()))
          return false; 
      } 
      return true;
    }
    
    public boolean containsAll(short[] array) {
      for (short element : array) {
        if (!TObjectShortCustomHashMap.this.containsValue(element))
          return false; 
      } 
      return true;
    }
    
    public boolean addAll(Collection<? extends Short> collection) {
      throw new UnsupportedOperationException();
    }
    
    public boolean addAll(TShortCollection collection) {
      throw new UnsupportedOperationException();
    }
    
    public boolean addAll(short[] array) {
      throw new UnsupportedOperationException();
    }
    
    public boolean retainAll(Collection<?> collection) {
      boolean modified = false;
      TShortIterator iter = iterator();
      while (iter.hasNext()) {
        if (!collection.contains(Short.valueOf(iter.next()))) {
          iter.remove();
          modified = true;
        } 
      } 
      return modified;
    }
    
    public boolean retainAll(TShortCollection collection) {
      if (this == collection)
        return false; 
      boolean modified = false;
      TShortIterator iter = iterator();
      while (iter.hasNext()) {
        if (!collection.contains(iter.next())) {
          iter.remove();
          modified = true;
        } 
      } 
      return modified;
    }
    
    public boolean retainAll(short[] array) {
      boolean changed = false;
      Arrays.sort(array);
      short[] values = TObjectShortCustomHashMap.this._values;
      Object[] set = TObjectShortCustomHashMap.this._set;
      for (int i = set.length; i-- > 0;) {
        if (set[i] != TObjectHash.FREE && set[i] != TObjectHash.REMOVED && 
          
          Arrays.binarySearch(array, values[i]) < 0) {
          TObjectShortCustomHashMap.this.removeAt(i);
          changed = true;
        } 
      } 
      return changed;
    }
    
    public boolean removeAll(Collection<?> collection) {
      boolean changed = false;
      for (Object element : collection) {
        if (element instanceof Short) {
          short c = ((Short)element).shortValue();
          if (remove(c))
            changed = true; 
        } 
      } 
      return changed;
    }
    
    public boolean removeAll(TShortCollection collection) {
      if (this == collection) {
        clear();
        return true;
      } 
      boolean changed = false;
      TShortIterator iter = collection.iterator();
      while (iter.hasNext()) {
        short element = iter.next();
        if (remove(element))
          changed = true; 
      } 
      return changed;
    }
    
    public boolean removeAll(short[] array) {
      boolean changed = false;
      for (int i = array.length; i-- > 0;) {
        if (remove(array[i]))
          changed = true; 
      } 
      return changed;
    }
    
    public void clear() {
      TObjectShortCustomHashMap.this.clear();
    }
    
    public boolean forEach(TShortProcedure procedure) {
      return TObjectShortCustomHashMap.this.forEachValue(procedure);
    }
    
    public String toString() {
      final StringBuilder buf = new StringBuilder("{");
      TObjectShortCustomHashMap.this.forEachValue(new TShortProcedure() {
            private boolean first = true;
            
            public boolean execute(short value) {
              if (this.first) {
                this.first = false;
              } else {
                buf.append(", ");
              } 
              buf.append(value);
              return true;
            }
          });
      buf.append("}");
      return buf.toString();
    }
    
    class TObjectShortValueHashIterator implements TShortIterator {
      protected THash _hash = (THash)TObjectShortCustomHashMap.this;
      
      protected int _expectedSize;
      
      protected int _index;
      
      TObjectShortValueHashIterator() {
        this._expectedSize = this._hash.size();
        this._index = this._hash.capacity();
      }
      
      public boolean hasNext() {
        return (nextIndex() >= 0);
      }
      
      public short next() {
        moveToNextIndex();
        return TObjectShortCustomHashMap.this._values[this._index];
      }
      
      public void remove() {
        if (this._expectedSize != this._hash.size())
          throw new ConcurrentModificationException(); 
        try {
          this._hash.tempDisableAutoCompaction();
          TObjectShortCustomHashMap.this.removeAt(this._index);
        } finally {
          this._hash.reenableAutoCompaction(false);
        } 
        this._expectedSize--;
      }
      
      protected final void moveToNextIndex() {
        if ((this._index = nextIndex()) < 0)
          throw new NoSuchElementException(); 
      }
      
      protected final int nextIndex() {
        if (this._expectedSize != this._hash.size())
          throw new ConcurrentModificationException(); 
        Object[] set = TObjectShortCustomHashMap.this._set;
        int i = this._index;
        while (i-- > 0 && (set[i] == TCustomObjectHash.FREE || set[i] == TCustomObjectHash.REMOVED));
        return i;
      }
    }
  }
  
  class TObjectShortHashIterator<K> extends TObjectHashIterator<K> implements TObjectShortIterator<K> {
    private final TObjectShortCustomHashMap<K> _map;
    
    public TObjectShortHashIterator(TObjectShortCustomHashMap<K> map) {
      super((TObjectHash)map);
      this._map = map;
    }
    
    public void advance() {
      moveToNextIndex();
    }
    
    public K key() {
      return (K)this._map._set[this._index];
    }
    
    public short value() {
      return this._map._values[this._index];
    }
    
    public short setValue(short val) {
      short old = value();
      this._map._values[this._index] = val;
      return old;
    }
  }
  
  public void writeExternal(ObjectOutput out) throws IOException {
    // Byte code:
    //   0: aload_1
    //   1: iconst_0
    //   2: invokeinterface writeByte : (I)V
    //   7: aload_0
    //   8: aload_1
    //   9: invokespecial writeExternal : (Ljava/io/ObjectOutput;)V
    //   12: aload_1
    //   13: aload_0
    //   14: getfield strategy : Lgnu/trove/strategy/HashingStrategy;
    //   17: invokeinterface writeObject : (Ljava/lang/Object;)V
    //   22: aload_1
    //   23: aload_0
    //   24: getfield no_entry_value : S
    //   27: invokeinterface writeShort : (I)V
    //   32: aload_1
    //   33: aload_0
    //   34: getfield _size : I
    //   37: invokeinterface writeInt : (I)V
    //   42: aload_0
    //   43: getfield _set : [Ljava/lang/Object;
    //   46: arraylength
    //   47: istore_2
    //   48: iload_2
    //   49: iinc #2, -1
    //   52: ifle -> 106
    //   55: aload_0
    //   56: getfield _set : [Ljava/lang/Object;
    //   59: iload_2
    //   60: aaload
    //   61: getstatic gnu/trove/map/custom_hash/TObjectShortCustomHashMap.REMOVED : Ljava/lang/Object;
    //   64: if_acmpeq -> 48
    //   67: aload_0
    //   68: getfield _set : [Ljava/lang/Object;
    //   71: iload_2
    //   72: aaload
    //   73: getstatic gnu/trove/map/custom_hash/TObjectShortCustomHashMap.FREE : Ljava/lang/Object;
    //   76: if_acmpeq -> 48
    //   79: aload_1
    //   80: aload_0
    //   81: getfield _set : [Ljava/lang/Object;
    //   84: iload_2
    //   85: aaload
    //   86: invokeinterface writeObject : (Ljava/lang/Object;)V
    //   91: aload_1
    //   92: aload_0
    //   93: getfield _values : [S
    //   96: iload_2
    //   97: saload
    //   98: invokeinterface writeShort : (I)V
    //   103: goto -> 48
    //   106: return
    // Line number table:
    //   Java source line number -> byte code offset
    //   #1093	-> 0
    //   #1096	-> 7
    //   #1099	-> 12
    //   #1102	-> 22
    //   #1105	-> 32
    //   #1108	-> 42
    //   #1109	-> 55
    //   #1110	-> 79
    //   #1111	-> 91
    //   #1114	-> 106
    // Local variable table:
    //   start	length	slot	name	descriptor
    //   48	58	2	i	I
    //   0	107	0	this	Lgnu/trove/map/custom_hash/TObjectShortCustomHashMap;
    //   0	107	1	out	Ljava/io/ObjectOutput;
    // Local variable type table:
    //   start	length	slot	name	signature
    //   0	107	0	this	Lgnu/trove/map/custom_hash/TObjectShortCustomHashMap<TK;>;
  }
  
  public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
    in.readByte();
    super.readExternal(in);
    this.strategy = (HashingStrategy)in.readObject();
    this.no_entry_value = in.readShort();
    int size = in.readInt();
    setUp(size);
    while (size-- > 0) {
      K key = (K)in.readObject();
      short val = in.readShort();
      put(key, val);
    } 
  }
  
  public String toString() {
    final StringBuilder buf = new StringBuilder("{");
    forEachEntry(new TObjectShortProcedure<K>() {
          private boolean first = true;
          
          public boolean execute(K key, short value) {
            if (this.first) {
              this.first = false;
            } else {
              buf.append(",");
            } 
            buf.append(key).append("=").append(value);
            return true;
          }
        });
    buf.append("}");
    return buf.toString();
  }
  
  public TObjectShortCustomHashMap() {}
}