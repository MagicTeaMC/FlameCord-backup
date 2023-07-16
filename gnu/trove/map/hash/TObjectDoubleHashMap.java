package gnu.trove.map.hash;

import gnu.trove.TDoubleCollection;
import gnu.trove.function.TDoubleFunction;
import gnu.trove.impl.Constants;
import gnu.trove.impl.hash.THash;
import gnu.trove.impl.hash.TObjectHash;
import gnu.trove.iterator.TDoubleIterator;
import gnu.trove.iterator.TObjectDoubleIterator;
import gnu.trove.iterator.hash.TObjectHashIterator;
import gnu.trove.map.TObjectDoubleMap;
import gnu.trove.procedure.TDoubleProcedure;
import gnu.trove.procedure.TObjectDoubleProcedure;
import gnu.trove.procedure.TObjectProcedure;
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

public class TObjectDoubleHashMap<K> extends TObjectHash<K> implements TObjectDoubleMap<K>, Externalizable {
  static final long serialVersionUID = 1L;
  
  private final TObjectDoubleProcedure<K> PUT_ALL_PROC = new TObjectDoubleProcedure<K>() {
      public boolean execute(K key, double value) {
        TObjectDoubleHashMap.this.put(key, value);
        return true;
      }
    };
  
  protected transient double[] _values;
  
  protected double no_entry_value;
  
  public TObjectDoubleHashMap() {
    this.no_entry_value = Constants.DEFAULT_DOUBLE_NO_ENTRY_VALUE;
  }
  
  public TObjectDoubleHashMap(int initialCapacity) {
    super(initialCapacity);
    this.no_entry_value = Constants.DEFAULT_DOUBLE_NO_ENTRY_VALUE;
  }
  
  public TObjectDoubleHashMap(int initialCapacity, float loadFactor) {
    super(initialCapacity, loadFactor);
    this.no_entry_value = Constants.DEFAULT_DOUBLE_NO_ENTRY_VALUE;
  }
  
  public TObjectDoubleHashMap(int initialCapacity, float loadFactor, double noEntryValue) {
    super(initialCapacity, loadFactor);
    this.no_entry_value = noEntryValue;
    if (this.no_entry_value != 0.0D)
      Arrays.fill(this._values, this.no_entry_value); 
  }
  
  public TObjectDoubleHashMap(TObjectDoubleMap<? extends K> map) {
    this(map.size(), 0.5F, map.getNoEntryValue());
    if (map instanceof TObjectDoubleHashMap) {
      TObjectDoubleHashMap hashmap = (TObjectDoubleHashMap)map;
      this._loadFactor = Math.abs(hashmap._loadFactor);
      this.no_entry_value = hashmap.no_entry_value;
      if (this.no_entry_value != 0.0D)
        Arrays.fill(this._values, this.no_entry_value); 
      setUp(saturatedCast(fastCeil(10.0D / this._loadFactor)));
    } 
    putAll(map);
  }
  
  public int setUp(int initialCapacity) {
    int capacity = super.setUp(initialCapacity);
    this._values = new double[capacity];
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
    //   15: getfield _values : [D
    //   18: astore #4
    //   20: aload_0
    //   21: iload_1
    //   22: anewarray java/lang/Object
    //   25: putfield _set : [Ljava/lang/Object;
    //   28: aload_0
    //   29: getfield _set : [Ljava/lang/Object;
    //   32: getstatic gnu/trove/map/hash/TObjectDoubleHashMap.FREE : Ljava/lang/Object;
    //   35: invokestatic fill : ([Ljava/lang/Object;Ljava/lang/Object;)V
    //   38: aload_0
    //   39: iload_1
    //   40: newarray double
    //   42: putfield _values : [D
    //   45: aload_0
    //   46: getfield _values : [D
    //   49: aload_0
    //   50: getfield no_entry_value : D
    //   53: invokestatic fill : ([DD)V
    //   56: iload_2
    //   57: istore #5
    //   59: iload #5
    //   61: iinc #5, -1
    //   64: ifle -> 146
    //   67: aload_3
    //   68: iload #5
    //   70: aaload
    //   71: getstatic gnu/trove/map/hash/TObjectDoubleHashMap.FREE : Ljava/lang/Object;
    //   74: if_acmpeq -> 59
    //   77: aload_3
    //   78: iload #5
    //   80: aaload
    //   81: getstatic gnu/trove/map/hash/TObjectDoubleHashMap.REMOVED : Ljava/lang/Object;
    //   84: if_acmpeq -> 59
    //   87: aload_3
    //   88: iload #5
    //   90: aaload
    //   91: astore #6
    //   93: aload_0
    //   94: aload #6
    //   96: invokevirtual insertKey : (Ljava/lang/Object;)I
    //   99: istore #7
    //   101: iload #7
    //   103: ifge -> 122
    //   106: aload_0
    //   107: aload_0
    //   108: getfield _set : [Ljava/lang/Object;
    //   111: iload #7
    //   113: ineg
    //   114: iconst_1
    //   115: isub
    //   116: aaload
    //   117: aload #6
    //   119: invokevirtual throwObjectContractViolation : (Ljava/lang/Object;Ljava/lang/Object;)V
    //   122: aload_0
    //   123: getfield _set : [Ljava/lang/Object;
    //   126: iload #7
    //   128: aload #6
    //   130: aastore
    //   131: aload_0
    //   132: getfield _values : [D
    //   135: iload #7
    //   137: aload #4
    //   139: iload #5
    //   141: daload
    //   142: dastore
    //   143: goto -> 59
    //   146: return
    // Line number table:
    //   Java source line number -> byte code offset
    //   #175	-> 0
    //   #178	-> 6
    //   #179	-> 14
    //   #181	-> 20
    //   #182	-> 28
    //   #183	-> 38
    //   #184	-> 45
    //   #186	-> 56
    //   #187	-> 67
    //   #188	-> 87
    //   #189	-> 93
    //   #190	-> 101
    //   #191	-> 106
    //   #193	-> 122
    //   #194	-> 131
    //   #195	-> 143
    //   #197	-> 146
    // Local variable table:
    //   start	length	slot	name	descriptor
    //   93	50	6	o	Ljava/lang/Object;
    //   101	42	7	index	I
    //   59	87	5	i	I
    //   0	147	0	this	Lgnu/trove/map/hash/TObjectDoubleHashMap;
    //   0	147	1	newCapacity	I
    //   6	141	2	oldCapacity	I
    //   14	133	3	oldKeys	[Ljava/lang/Object;
    //   20	127	4	oldVals	[D
    // Local variable type table:
    //   start	length	slot	name	signature
    //   93	50	6	o	TK;
    //   0	147	0	this	Lgnu/trove/map/hash/TObjectDoubleHashMap<TK;>;
    //   14	133	3	oldKeys	[TK;
  }
  
  public double getNoEntryValue() {
    return this.no_entry_value;
  }
  
  public boolean containsKey(Object key) {
    return contains(key);
  }
  
  public boolean containsValue(double val) {
    // Byte code:
    //   0: aload_0
    //   1: getfield _set : [Ljava/lang/Object;
    //   4: astore_3
    //   5: aload_0
    //   6: getfield _values : [D
    //   9: astore #4
    //   11: aload #4
    //   13: arraylength
    //   14: istore #5
    //   16: iload #5
    //   18: iinc #5, -1
    //   21: ifle -> 56
    //   24: aload_3
    //   25: iload #5
    //   27: aaload
    //   28: getstatic gnu/trove/map/hash/TObjectDoubleHashMap.FREE : Ljava/lang/Object;
    //   31: if_acmpeq -> 16
    //   34: aload_3
    //   35: iload #5
    //   37: aaload
    //   38: getstatic gnu/trove/map/hash/TObjectDoubleHashMap.REMOVED : Ljava/lang/Object;
    //   41: if_acmpeq -> 16
    //   44: dload_1
    //   45: aload #4
    //   47: iload #5
    //   49: daload
    //   50: dcmpl
    //   51: ifne -> 16
    //   54: iconst_1
    //   55: ireturn
    //   56: iconst_0
    //   57: ireturn
    // Line number table:
    //   Java source line number -> byte code offset
    //   #216	-> 0
    //   #217	-> 5
    //   #219	-> 11
    //   #220	-> 24
    //   #221	-> 54
    //   #224	-> 56
    // Local variable table:
    //   start	length	slot	name	descriptor
    //   16	40	5	i	I
    //   0	58	0	this	Lgnu/trove/map/hash/TObjectDoubleHashMap;
    //   0	58	1	val	D
    //   5	53	3	keys	[Ljava/lang/Object;
    //   11	47	4	vals	[D
    // Local variable type table:
    //   start	length	slot	name	signature
    //   0	58	0	this	Lgnu/trove/map/hash/TObjectDoubleHashMap<TK;>;
  }
  
  public double get(Object key) {
    int index = index(key);
    return (index < 0) ? this.no_entry_value : this._values[index];
  }
  
  public double put(K key, double value) {
    int index = insertKey(key);
    return doPut(value, index);
  }
  
  public double putIfAbsent(K key, double value) {
    int index = insertKey(key);
    if (index < 0)
      return this._values[-index - 1]; 
    return doPut(value, index);
  }
  
  private double doPut(double value, int index) {
    double previous = this.no_entry_value;
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
  
  public double remove(Object key) {
    double prev = this.no_entry_value;
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
  
  public void putAll(Map<? extends K, ? extends Double> map) {
    Set<? extends Map.Entry<? extends K, ? extends Double>> set = map.entrySet();
    for (Map.Entry<? extends K, ? extends Double> entry : set)
      put(entry.getKey(), ((Double)entry.getValue()).doubleValue()); 
  }
  
  public void putAll(TObjectDoubleMap<? extends K> map) {
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
    //   14: getstatic gnu/trove/map/hash/TObjectDoubleHashMap.FREE : Ljava/lang/Object;
    //   17: invokestatic fill : ([Ljava/lang/Object;IILjava/lang/Object;)V
    //   20: aload_0
    //   21: getfield _values : [D
    //   24: iconst_0
    //   25: aload_0
    //   26: getfield _values : [D
    //   29: arraylength
    //   30: aload_0
    //   31: getfield no_entry_value : D
    //   34: invokestatic fill : ([DIID)V
    //   37: return
    // Line number table:
    //   Java source line number -> byte code offset
    //   #315	-> 0
    //   #316	-> 4
    //   #317	-> 20
    //   #318	-> 37
    // Local variable table:
    //   start	length	slot	name	descriptor
    //   0	38	0	this	Lgnu/trove/map/hash/TObjectDoubleHashMap;
    // Local variable type table:
    //   start	length	slot	name	signature
    //   0	38	0	this	Lgnu/trove/map/hash/TObjectDoubleHashMap<TK;>;
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
    //   32: getstatic gnu/trove/map/hash/TObjectDoubleHashMap.FREE : Ljava/lang/Object;
    //   35: if_acmpeq -> 22
    //   38: aload_2
    //   39: iload_3
    //   40: aaload
    //   41: getstatic gnu/trove/map/hash/TObjectDoubleHashMap.REMOVED : Ljava/lang/Object;
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
    //   #332	-> 0
    //   #333	-> 11
    //   #335	-> 16
    //   #336	-> 29
    //   #338	-> 47
    //   #341	-> 60
    // Local variable table:
    //   start	length	slot	name	descriptor
    //   19	41	3	i	I
    //   22	38	4	j	I
    //   0	62	0	this	Lgnu/trove/map/hash/TObjectDoubleHashMap;
    //   11	51	1	keys	[Ljava/lang/Object;
    //   16	46	2	k	[Ljava/lang/Object;
    // Local variable type table:
    //   start	length	slot	name	signature
    //   0	62	0	this	Lgnu/trove/map/hash/TObjectDoubleHashMap<TK;>;
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
    //   53: getstatic gnu/trove/map/hash/TObjectDoubleHashMap.FREE : Ljava/lang/Object;
    //   56: if_acmpeq -> 41
    //   59: aload_3
    //   60: iload #4
    //   62: aaload
    //   63: getstatic gnu/trove/map/hash/TObjectDoubleHashMap.REMOVED : Ljava/lang/Object;
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
    //   #347	-> 0
    //   #348	-> 5
    //   #350	-> 11
    //   #351	-> 12
    //   #350	-> 19
    //   #354	-> 29
    //   #356	-> 34
    //   #357	-> 49
    //   #359	-> 69
    //   #362	-> 83
    // Local variable table:
    //   start	length	slot	name	descriptor
    //   38	45	4	i	I
    //   41	42	5	j	I
    //   0	85	0	this	Lgnu/trove/map/hash/TObjectDoubleHashMap;
    //   0	85	1	a	[Ljava/lang/Object;
    //   5	80	2	size	I
    //   34	51	3	k	[Ljava/lang/Object;
    // Local variable type table:
    //   start	length	slot	name	signature
    //   0	85	0	this	Lgnu/trove/map/hash/TObjectDoubleHashMap<TK;>;
    //   0	85	1	a	[TK;
  }
  
  public TDoubleCollection valueCollection() {
    return new TDoubleValueCollection();
  }
  
  public double[] values() {
    // Byte code:
    //   0: aload_0
    //   1: invokevirtual size : ()I
    //   4: newarray double
    //   6: astore_1
    //   7: aload_0
    //   8: getfield _values : [D
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
    //   36: getstatic gnu/trove/map/hash/TObjectDoubleHashMap.FREE : Ljava/lang/Object;
    //   39: if_acmpeq -> 24
    //   42: aload_3
    //   43: iload #4
    //   45: aaload
    //   46: getstatic gnu/trove/map/hash/TObjectDoubleHashMap.REMOVED : Ljava/lang/Object;
    //   49: if_acmpeq -> 24
    //   52: aload_1
    //   53: iload #5
    //   55: iinc #5, 1
    //   58: aload_2
    //   59: iload #4
    //   61: daload
    //   62: dastore
    //   63: goto -> 24
    //   66: aload_1
    //   67: areturn
    // Line number table:
    //   Java source line number -> byte code offset
    //   #374	-> 0
    //   #375	-> 7
    //   #376	-> 12
    //   #378	-> 17
    //   #379	-> 32
    //   #380	-> 52
    //   #383	-> 66
    // Local variable table:
    //   start	length	slot	name	descriptor
    //   21	45	4	i	I
    //   24	42	5	j	I
    //   0	68	0	this	Lgnu/trove/map/hash/TObjectDoubleHashMap;
    //   7	61	1	vals	[D
    //   12	56	2	v	[D
    //   17	51	3	keys	[Ljava/lang/Object;
    // Local variable type table:
    //   start	length	slot	name	signature
    //   0	68	0	this	Lgnu/trove/map/hash/TObjectDoubleHashMap<TK;>;
  }
  
  public double[] values(double[] array) {
    // Byte code:
    //   0: aload_0
    //   1: invokevirtual size : ()I
    //   4: istore_2
    //   5: aload_1
    //   6: arraylength
    //   7: iload_2
    //   8: if_icmpge -> 15
    //   11: iload_2
    //   12: newarray double
    //   14: astore_1
    //   15: aload_0
    //   16: getfield _values : [D
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
    //   46: getstatic gnu/trove/map/hash/TObjectDoubleHashMap.FREE : Ljava/lang/Object;
    //   49: if_acmpeq -> 33
    //   52: aload #4
    //   54: iload #5
    //   56: aaload
    //   57: getstatic gnu/trove/map/hash/TObjectDoubleHashMap.REMOVED : Ljava/lang/Object;
    //   60: if_acmpeq -> 33
    //   63: aload_1
    //   64: iload #6
    //   66: iinc #6, 1
    //   69: aload_3
    //   70: iload #5
    //   72: daload
    //   73: dastore
    //   74: goto -> 33
    //   77: aload_1
    //   78: arraylength
    //   79: iload_2
    //   80: if_icmple -> 90
    //   83: aload_1
    //   84: iload_2
    //   85: aload_0
    //   86: getfield no_entry_value : D
    //   89: dastore
    //   90: aload_1
    //   91: areturn
    // Line number table:
    //   Java source line number -> byte code offset
    //   #389	-> 0
    //   #390	-> 5
    //   #391	-> 11
    //   #394	-> 15
    //   #395	-> 20
    //   #397	-> 26
    //   #398	-> 41
    //   #399	-> 63
    //   #402	-> 77
    //   #403	-> 83
    //   #405	-> 90
    // Local variable table:
    //   start	length	slot	name	descriptor
    //   30	47	5	i	I
    //   33	44	6	j	I
    //   0	92	0	this	Lgnu/trove/map/hash/TObjectDoubleHashMap;
    //   0	92	1	array	[D
    //   5	87	2	size	I
    //   20	72	3	v	[D
    //   26	66	4	keys	[Ljava/lang/Object;
    // Local variable type table:
    //   start	length	slot	name	signature
    //   0	92	0	this	Lgnu/trove/map/hash/TObjectDoubleHashMap<TK;>;
  }
  
  public TObjectDoubleIterator<K> iterator() {
    return new TObjectDoubleHashIterator<K>(this);
  }
  
  public boolean increment(K key) {
    return adjustValue(key, 1.0D);
  }
  
  public boolean adjustValue(K key, double amount) {
    int index = index(key);
    if (index < 0)
      return false; 
    this._values[index] = this._values[index] + amount;
    return true;
  }
  
  public double adjustOrPutValue(K key, double adjust_amount, double put_amount) {
    int index = insertKey(key);
    index = -index - 1;
    double newValue = this._values[index] = this._values[index] + adjust_amount;
    boolean isNewMapping = false;
    newValue = this._values[index] = put_amount;
    isNewMapping = true;
    if (isNewMapping)
      postInsertHook(this.consumeFreeSlot); 
    return newValue;
  }
  
  public boolean forEachKey(TObjectProcedure<? super K> procedure) {
    return forEach(procedure);
  }
  
  public boolean forEachValue(TDoubleProcedure procedure) {
    // Byte code:
    //   0: aload_0
    //   1: getfield _set : [Ljava/lang/Object;
    //   4: astore_2
    //   5: aload_0
    //   6: getfield _values : [D
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
    //   26: getstatic gnu/trove/map/hash/TObjectDoubleHashMap.FREE : Ljava/lang/Object;
    //   29: if_acmpeq -> 14
    //   32: aload_2
    //   33: iload #4
    //   35: aaload
    //   36: getstatic gnu/trove/map/hash/TObjectDoubleHashMap.REMOVED : Ljava/lang/Object;
    //   39: if_acmpeq -> 14
    //   42: aload_1
    //   43: aload_3
    //   44: iload #4
    //   46: daload
    //   47: invokeinterface execute : (D)Z
    //   52: ifne -> 14
    //   55: iconst_0
    //   56: ireturn
    //   57: iconst_1
    //   58: ireturn
    // Line number table:
    //   Java source line number -> byte code offset
    //   #483	-> 0
    //   #484	-> 5
    //   #485	-> 10
    //   #486	-> 22
    //   #487	-> 47
    //   #488	-> 55
    //   #491	-> 57
    // Local variable table:
    //   start	length	slot	name	descriptor
    //   14	43	4	i	I
    //   0	59	0	this	Lgnu/trove/map/hash/TObjectDoubleHashMap;
    //   0	59	1	procedure	Lgnu/trove/procedure/TDoubleProcedure;
    //   5	54	2	keys	[Ljava/lang/Object;
    //   10	49	3	values	[D
    // Local variable type table:
    //   start	length	slot	name	signature
    //   0	59	0	this	Lgnu/trove/map/hash/TObjectDoubleHashMap<TK;>;
  }
  
  public boolean forEachEntry(TObjectDoubleProcedure<? super K> procedure) {
    // Byte code:
    //   0: aload_0
    //   1: getfield _set : [Ljava/lang/Object;
    //   4: astore_2
    //   5: aload_0
    //   6: getfield _values : [D
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
    //   26: getstatic gnu/trove/map/hash/TObjectDoubleHashMap.FREE : Ljava/lang/Object;
    //   29: if_acmpeq -> 14
    //   32: aload_2
    //   33: iload #4
    //   35: aaload
    //   36: getstatic gnu/trove/map/hash/TObjectDoubleHashMap.REMOVED : Ljava/lang/Object;
    //   39: if_acmpeq -> 14
    //   42: aload_1
    //   43: aload_2
    //   44: iload #4
    //   46: aaload
    //   47: aload_3
    //   48: iload #4
    //   50: daload
    //   51: invokeinterface execute : (Ljava/lang/Object;D)Z
    //   56: ifne -> 14
    //   59: iconst_0
    //   60: ireturn
    //   61: iconst_1
    //   62: ireturn
    // Line number table:
    //   Java source line number -> byte code offset
    //   #505	-> 0
    //   #506	-> 5
    //   #507	-> 10
    //   #508	-> 22
    //   #510	-> 51
    //   #511	-> 59
    //   #514	-> 61
    // Local variable table:
    //   start	length	slot	name	descriptor
    //   14	47	4	i	I
    //   0	63	0	this	Lgnu/trove/map/hash/TObjectDoubleHashMap;
    //   0	63	1	procedure	Lgnu/trove/procedure/TObjectDoubleProcedure;
    //   5	58	2	keys	[Ljava/lang/Object;
    //   10	53	3	values	[D
    // Local variable type table:
    //   start	length	slot	name	signature
    //   0	63	0	this	Lgnu/trove/map/hash/TObjectDoubleHashMap<TK;>;
    //   0	63	1	procedure	Lgnu/trove/procedure/TObjectDoubleProcedure<-TK;>;
  }
  
  public boolean retainEntries(TObjectDoubleProcedure<? super K> procedure) {
    // Byte code:
    //   0: iconst_0
    //   1: istore_2
    //   2: aload_0
    //   3: getfield _set : [Ljava/lang/Object;
    //   6: checkcast [Ljava/lang/Object;
    //   9: astore_3
    //   10: aload_0
    //   11: getfield _values : [D
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
    //   36: getstatic gnu/trove/map/hash/TObjectDoubleHashMap.FREE : Ljava/lang/Object;
    //   39: if_acmpeq -> 24
    //   42: aload_3
    //   43: iload #5
    //   45: aaload
    //   46: getstatic gnu/trove/map/hash/TObjectDoubleHashMap.REMOVED : Ljava/lang/Object;
    //   49: if_acmpeq -> 24
    //   52: aload_1
    //   53: aload_3
    //   54: iload #5
    //   56: aaload
    //   57: aload #4
    //   59: iload #5
    //   61: daload
    //   62: invokeinterface execute : (Ljava/lang/Object;D)Z
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
    //   #526	-> 0
    //   #528	-> 2
    //   #529	-> 10
    //   #532	-> 16
    //   #534	-> 20
    //   #535	-> 32
    //   #537	-> 62
    //   #538	-> 70
    //   #539	-> 76
    //   #544	-> 81
    //   #545	-> 86
    //   #544	-> 89
    //   #547	-> 99
    // Local variable table:
    //   start	length	slot	name	descriptor
    //   24	57	5	i	I
    //   0	101	0	this	Lgnu/trove/map/hash/TObjectDoubleHashMap;
    //   0	101	1	procedure	Lgnu/trove/procedure/TObjectDoubleProcedure;
    //   2	99	2	modified	Z
    //   10	91	3	keys	[Ljava/lang/Object;
    //   16	85	4	values	[D
    // Local variable type table:
    //   start	length	slot	name	signature
    //   0	101	0	this	Lgnu/trove/map/hash/TObjectDoubleHashMap<TK;>;
    //   0	101	1	procedure	Lgnu/trove/procedure/TObjectDoubleProcedure<-TK;>;
    //   10	91	3	keys	[TK;
    // Exception table:
    //   from	to	target	type
    //   20	81	89	finally
    //   89	91	89	finally
  }
  
  public void transformValues(TDoubleFunction function) {
    // Byte code:
    //   0: aload_0
    //   1: getfield _set : [Ljava/lang/Object;
    //   4: astore_2
    //   5: aload_0
    //   6: getfield _values : [D
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
    //   33: getstatic gnu/trove/map/hash/TObjectDoubleHashMap.REMOVED : Ljava/lang/Object;
    //   36: if_acmpeq -> 14
    //   39: aload_3
    //   40: iload #4
    //   42: aload_1
    //   43: aload_3
    //   44: iload #4
    //   46: daload
    //   47: invokeinterface execute : (D)D
    //   52: dastore
    //   53: goto -> 14
    //   56: return
    // Line number table:
    //   Java source line number -> byte code offset
    //   #557	-> 0
    //   #558	-> 5
    //   #559	-> 10
    //   #560	-> 22
    //   #561	-> 39
    //   #564	-> 56
    // Local variable table:
    //   start	length	slot	name	descriptor
    //   14	42	4	i	I
    //   0	57	0	this	Lgnu/trove/map/hash/TObjectDoubleHashMap;
    //   0	57	1	function	Lgnu/trove/function/TDoubleFunction;
    //   5	52	2	keys	[Ljava/lang/Object;
    //   10	47	3	values	[D
    // Local variable type table:
    //   start	length	slot	name	signature
    //   0	57	0	this	Lgnu/trove/map/hash/TObjectDoubleHashMap<TK;>;
  }
  
  public boolean equals(Object other) {
    if (!(other instanceof TObjectDoubleMap))
      return false; 
    TObjectDoubleMap that = (TObjectDoubleMap)other;
    if (that.size() != size())
      return false; 
    try {
      TObjectDoubleIterator<K> iter = iterator();
      while (iter.hasNext()) {
        iter.advance();
        Object key = iter.key();
        double value = iter.value();
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
    //   8: getfield _values : [D
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
    //   28: getstatic gnu/trove/map/hash/TObjectDoubleHashMap.FREE : Ljava/lang/Object;
    //   31: if_acmpeq -> 16
    //   34: aload_2
    //   35: iload #4
    //   37: aaload
    //   38: getstatic gnu/trove/map/hash/TObjectDoubleHashMap.REMOVED : Ljava/lang/Object;
    //   41: if_acmpeq -> 16
    //   44: iload_1
    //   45: aload_3
    //   46: iload #4
    //   48: daload
    //   49: invokestatic hash : (D)I
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
    //   #611	-> 0
    //   #612	-> 2
    //   #613	-> 7
    //   #614	-> 12
    //   #615	-> 24
    //   #616	-> 44
    //   #617	-> 67
    //   #620	-> 76
    // Local variable table:
    //   start	length	slot	name	descriptor
    //   16	60	4	i	I
    //   0	78	0	this	Lgnu/trove/map/hash/TObjectDoubleHashMap;
    //   2	76	1	hashcode	I
    //   7	71	2	keys	[Ljava/lang/Object;
    //   12	66	3	values	[D
    // Local variable type table:
    //   start	length	slot	name	signature
    //   0	78	0	this	Lgnu/trove/map/hash/TObjectDoubleHashMap<TK;>;
  }
  
  protected class KeyView extends MapBackedView<K> {
    public Iterator<K> iterator() {
      return (Iterator<K>)new TObjectHashIterator(TObjectDoubleHashMap.this);
    }
    
    public boolean removeElement(K key) {
      return (TObjectDoubleHashMap.this.no_entry_value != TObjectDoubleHashMap.this.remove(key));
    }
    
    public boolean containsElement(K key) {
      return TObjectDoubleHashMap.this.contains(key);
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
      TObjectDoubleHashMap.this.clear();
    }
    
    public boolean add(E obj) {
      throw new UnsupportedOperationException();
    }
    
    public int size() {
      return TObjectDoubleHashMap.this.size();
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
      return TObjectDoubleHashMap.this.isEmpty();
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
  
  class TDoubleValueCollection implements TDoubleCollection {
    public TDoubleIterator iterator() {
      return new TObjectDoubleValueHashIterator();
    }
    
    public double getNoEntryValue() {
      return TObjectDoubleHashMap.this.no_entry_value;
    }
    
    public int size() {
      return TObjectDoubleHashMap.this._size;
    }
    
    public boolean isEmpty() {
      return (0 == TObjectDoubleHashMap.this._size);
    }
    
    public boolean contains(double entry) {
      return TObjectDoubleHashMap.this.containsValue(entry);
    }
    
    public double[] toArray() {
      return TObjectDoubleHashMap.this.values();
    }
    
    public double[] toArray(double[] dest) {
      return TObjectDoubleHashMap.this.values(dest);
    }
    
    public boolean add(double entry) {
      throw new UnsupportedOperationException();
    }
    
    public boolean remove(double entry) {
      double[] values = TObjectDoubleHashMap.this._values;
      Object[] set = TObjectDoubleHashMap.this._set;
      for (int i = values.length; i-- > 0;) {
        if (set[i] != TObjectHash.FREE && set[i] != TObjectHash.REMOVED && entry == values[i]) {
          TObjectDoubleHashMap.this.removeAt(i);
          return true;
        } 
      } 
      return false;
    }
    
    public boolean containsAll(Collection<?> collection) {
      for (Object element : collection) {
        if (element instanceof Double) {
          double ele = ((Double)element).doubleValue();
          if (!TObjectDoubleHashMap.this.containsValue(ele))
            return false; 
          continue;
        } 
        return false;
      } 
      return true;
    }
    
    public boolean containsAll(TDoubleCollection collection) {
      TDoubleIterator iter = collection.iterator();
      while (iter.hasNext()) {
        if (!TObjectDoubleHashMap.this.containsValue(iter.next()))
          return false; 
      } 
      return true;
    }
    
    public boolean containsAll(double[] array) {
      for (double element : array) {
        if (!TObjectDoubleHashMap.this.containsValue(element))
          return false; 
      } 
      return true;
    }
    
    public boolean addAll(Collection<? extends Double> collection) {
      throw new UnsupportedOperationException();
    }
    
    public boolean addAll(TDoubleCollection collection) {
      throw new UnsupportedOperationException();
    }
    
    public boolean addAll(double[] array) {
      throw new UnsupportedOperationException();
    }
    
    public boolean retainAll(Collection<?> collection) {
      boolean modified = false;
      TDoubleIterator iter = iterator();
      while (iter.hasNext()) {
        if (!collection.contains(Double.valueOf(iter.next()))) {
          iter.remove();
          modified = true;
        } 
      } 
      return modified;
    }
    
    public boolean retainAll(TDoubleCollection collection) {
      if (this == collection)
        return false; 
      boolean modified = false;
      TDoubleIterator iter = iterator();
      while (iter.hasNext()) {
        if (!collection.contains(iter.next())) {
          iter.remove();
          modified = true;
        } 
      } 
      return modified;
    }
    
    public boolean retainAll(double[] array) {
      boolean changed = false;
      Arrays.sort(array);
      double[] values = TObjectDoubleHashMap.this._values;
      Object[] set = TObjectDoubleHashMap.this._set;
      for (int i = set.length; i-- > 0;) {
        if (set[i] != TObjectHash.FREE && set[i] != TObjectHash.REMOVED && 
          
          Arrays.binarySearch(array, values[i]) < 0) {
          TObjectDoubleHashMap.this.removeAt(i);
          changed = true;
        } 
      } 
      return changed;
    }
    
    public boolean removeAll(Collection<?> collection) {
      boolean changed = false;
      for (Object element : collection) {
        if (element instanceof Double) {
          double c = ((Double)element).doubleValue();
          if (remove(c))
            changed = true; 
        } 
      } 
      return changed;
    }
    
    public boolean removeAll(TDoubleCollection collection) {
      if (this == collection) {
        clear();
        return true;
      } 
      boolean changed = false;
      TDoubleIterator iter = collection.iterator();
      while (iter.hasNext()) {
        double element = iter.next();
        if (remove(element))
          changed = true; 
      } 
      return changed;
    }
    
    public boolean removeAll(double[] array) {
      boolean changed = false;
      for (int i = array.length; i-- > 0;) {
        if (remove(array[i]))
          changed = true; 
      } 
      return changed;
    }
    
    public void clear() {
      TObjectDoubleHashMap.this.clear();
    }
    
    public boolean forEach(TDoubleProcedure procedure) {
      return TObjectDoubleHashMap.this.forEachValue(procedure);
    }
    
    public String toString() {
      final StringBuilder buf = new StringBuilder("{");
      TObjectDoubleHashMap.this.forEachValue(new TDoubleProcedure() {
            private boolean first = true;
            
            public boolean execute(double value) {
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
    
    class TObjectDoubleValueHashIterator implements TDoubleIterator {
      protected THash _hash = (THash)TObjectDoubleHashMap.this;
      
      protected int _expectedSize;
      
      protected int _index;
      
      TObjectDoubleValueHashIterator() {
        this._expectedSize = this._hash.size();
        this._index = this._hash.capacity();
      }
      
      public boolean hasNext() {
        return (nextIndex() >= 0);
      }
      
      public double next() {
        moveToNextIndex();
        return TObjectDoubleHashMap.this._values[this._index];
      }
      
      public void remove() {
        if (this._expectedSize != this._hash.size())
          throw new ConcurrentModificationException(); 
        try {
          this._hash.tempDisableAutoCompaction();
          TObjectDoubleHashMap.this.removeAt(this._index);
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
        Object[] set = TObjectDoubleHashMap.this._set;
        int i = this._index;
        while (i-- > 0 && (set[i] == TObjectHash.FREE || set[i] == TObjectHash.REMOVED));
        return i;
      }
    }
  }
  
  class TObjectDoubleHashIterator<K> extends TObjectHashIterator<K> implements TObjectDoubleIterator<K> {
    private final TObjectDoubleHashMap<K> _map;
    
    public TObjectDoubleHashIterator(TObjectDoubleHashMap<K> map) {
      super(map);
      this._map = map;
    }
    
    public void advance() {
      moveToNextIndex();
    }
    
    public K key() {
      return (K)this._map._set[this._index];
    }
    
    public double value() {
      return this._map._values[this._index];
    }
    
    public double setValue(double val) {
      double old = value();
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
    //   14: getfield no_entry_value : D
    //   17: invokeinterface writeDouble : (D)V
    //   22: aload_1
    //   23: aload_0
    //   24: getfield _size : I
    //   27: invokeinterface writeInt : (I)V
    //   32: aload_0
    //   33: getfield _set : [Ljava/lang/Object;
    //   36: arraylength
    //   37: istore_2
    //   38: iload_2
    //   39: iinc #2, -1
    //   42: ifle -> 96
    //   45: aload_0
    //   46: getfield _set : [Ljava/lang/Object;
    //   49: iload_2
    //   50: aaload
    //   51: getstatic gnu/trove/map/hash/TObjectDoubleHashMap.REMOVED : Ljava/lang/Object;
    //   54: if_acmpeq -> 38
    //   57: aload_0
    //   58: getfield _set : [Ljava/lang/Object;
    //   61: iload_2
    //   62: aaload
    //   63: getstatic gnu/trove/map/hash/TObjectDoubleHashMap.FREE : Ljava/lang/Object;
    //   66: if_acmpeq -> 38
    //   69: aload_1
    //   70: aload_0
    //   71: getfield _set : [Ljava/lang/Object;
    //   74: iload_2
    //   75: aaload
    //   76: invokeinterface writeObject : (Ljava/lang/Object;)V
    //   81: aload_1
    //   82: aload_0
    //   83: getfield _values : [D
    //   86: iload_2
    //   87: daload
    //   88: invokeinterface writeDouble : (D)V
    //   93: goto -> 38
    //   96: return
    // Line number table:
    //   Java source line number -> byte code offset
    //   #1082	-> 0
    //   #1085	-> 7
    //   #1088	-> 12
    //   #1091	-> 22
    //   #1094	-> 32
    //   #1095	-> 45
    //   #1096	-> 69
    //   #1097	-> 81
    //   #1100	-> 96
    // Local variable table:
    //   start	length	slot	name	descriptor
    //   38	58	2	i	I
    //   0	97	0	this	Lgnu/trove/map/hash/TObjectDoubleHashMap;
    //   0	97	1	out	Ljava/io/ObjectOutput;
    // Local variable type table:
    //   start	length	slot	name	signature
    //   0	97	0	this	Lgnu/trove/map/hash/TObjectDoubleHashMap<TK;>;
  }
  
  public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
    in.readByte();
    super.readExternal(in);
    this.no_entry_value = in.readDouble();
    int size = in.readInt();
    setUp(size);
    while (size-- > 0) {
      K key = (K)in.readObject();
      double val = in.readDouble();
      put(key, val);
    } 
  }
  
  public String toString() {
    final StringBuilder buf = new StringBuilder("{");
    forEachEntry(new TObjectDoubleProcedure<K>() {
          private boolean first = true;
          
          public boolean execute(K key, double value) {
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
}
