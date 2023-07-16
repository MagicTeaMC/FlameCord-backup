package gnu.trove.map.hash;

import gnu.trove.function.TObjectFunction;
import gnu.trove.impl.HashFunctions;
import gnu.trove.impl.hash.TObjectHash;
import gnu.trove.iterator.hash.TObjectHashIterator;
import gnu.trove.map.TMap;
import gnu.trove.procedure.TObjectObjectProcedure;
import gnu.trove.procedure.TObjectProcedure;
import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.lang.reflect.Array;
import java.util.AbstractSet;
import java.util.Collection;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class THashMap<K, V> extends TObjectHash<K> implements TMap<K, V>, Externalizable {
  static final long serialVersionUID = 1L;
  
  protected transient V[] _values;
  
  public THashMap() {}
  
  public THashMap(int initialCapacity) {
    super(initialCapacity);
  }
  
  public THashMap(int initialCapacity, float loadFactor) {
    super(initialCapacity, loadFactor);
  }
  
  public THashMap(Map<? extends K, ? extends V> map) {
    this(map.size());
    putAll(map);
  }
  
  public THashMap(THashMap<? extends K, ? extends V> map) {
    this(map.size());
    putAll((Map<? extends K, ? extends V>)map);
  }
  
  public int setUp(int initialCapacity) {
    int capacity = super.setUp(initialCapacity);
    this._values = (V[])new Object[capacity];
    return capacity;
  }
  
  public V put(K key, V value) {
    int index = insertKey(key);
    return doPut(value, index);
  }
  
  public V putIfAbsent(K key, V value) {
    int index = insertKey(key);
    if (index < 0)
      return this._values[-index - 1]; 
    return doPut(value, index);
  }
  
  private V doPut(V value, int index) {
    V previous = null;
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
  
  public boolean equals(Object other) {
    if (!(other instanceof Map))
      return false; 
    Map<K, V> that = (Map<K, V>)other;
    if (that.size() != size())
      return false; 
    return forEachEntry(new EqProcedure<K, V>(that));
  }
  
  public int hashCode() {
    HashProcedure p = new HashProcedure();
    forEachEntry(p);
    return p.getHashCode();
  }
  
  public String toString() {
    final StringBuilder buf = new StringBuilder("{");
    forEachEntry(new TObjectObjectProcedure<K, V>() {
          private boolean first = true;
          
          public boolean execute(K key, V value) {
            if (this.first) {
              this.first = false;
            } else {
              buf.append(", ");
            } 
            buf.append(key);
            buf.append("=");
            buf.append(value);
            return true;
          }
        });
    buf.append("}");
    return buf.toString();
  }
  
  private final class HashProcedure implements TObjectObjectProcedure<K, V> {
    private int h = 0;
    
    public int getHashCode() {
      return this.h;
    }
    
    public final boolean execute(K key, V value) {
      this.h += HashFunctions.hash(key) ^ ((value == null) ? 0 : value.hashCode());
      return true;
    }
    
    private HashProcedure() {}
  }
  
  private final class EqProcedure<K, V> implements TObjectObjectProcedure<K, V> {
    private final Map<K, V> _otherMap;
    
    EqProcedure(Map<K, V> otherMap) {
      this._otherMap = otherMap;
    }
    
    public final boolean execute(K key, V value) {
      if (value == null && !this._otherMap.containsKey(key))
        return false; 
      V oValue = this._otherMap.get(key);
      return (oValue == value || (oValue != null && THashMap.this
        .equals(oValue, value)));
    }
  }
  
  public boolean forEachKey(TObjectProcedure<? super K> procedure) {
    return forEach(procedure);
  }
  
  public boolean forEachValue(TObjectProcedure<? super V> procedure) {
    // Byte code:
    //   0: aload_0
    //   1: getfield _values : [Ljava/lang/Object;
    //   4: astore_2
    //   5: aload_0
    //   6: getfield _set : [Ljava/lang/Object;
    //   9: astore_3
    //   10: aload_2
    //   11: arraylength
    //   12: istore #4
    //   14: iload #4
    //   16: iinc #4, -1
    //   19: ifle -> 57
    //   22: aload_3
    //   23: iload #4
    //   25: aaload
    //   26: getstatic gnu/trove/map/hash/THashMap.FREE : Ljava/lang/Object;
    //   29: if_acmpeq -> 14
    //   32: aload_3
    //   33: iload #4
    //   35: aaload
    //   36: getstatic gnu/trove/map/hash/THashMap.REMOVED : Ljava/lang/Object;
    //   39: if_acmpeq -> 14
    //   42: aload_1
    //   43: aload_2
    //   44: iload #4
    //   46: aaload
    //   47: invokeinterface execute : (Ljava/lang/Object;)Z
    //   52: ifne -> 14
    //   55: iconst_0
    //   56: ireturn
    //   57: iconst_1
    //   58: ireturn
    // Line number table:
    //   Java source line number -> byte code offset
    //   #294	-> 0
    //   #295	-> 5
    //   #296	-> 10
    //   #297	-> 22
    //   #299	-> 47
    //   #300	-> 55
    //   #303	-> 57
    // Local variable table:
    //   start	length	slot	name	descriptor
    //   14	43	4	i	I
    //   0	59	0	this	Lgnu/trove/map/hash/THashMap;
    //   0	59	1	procedure	Lgnu/trove/procedure/TObjectProcedure;
    //   5	54	2	values	[Ljava/lang/Object;
    //   10	49	3	set	[Ljava/lang/Object;
    // Local variable type table:
    //   start	length	slot	name	signature
    //   0	59	0	this	Lgnu/trove/map/hash/THashMap<TK;TV;>;
    //   0	59	1	procedure	Lgnu/trove/procedure/TObjectProcedure<-TV;>;
    //   5	54	2	values	[TV;
  }
  
  public boolean forEachEntry(TObjectObjectProcedure<? super K, ? super V> procedure) {
    // Byte code:
    //   0: aload_0
    //   1: getfield _set : [Ljava/lang/Object;
    //   4: astore_2
    //   5: aload_0
    //   6: getfield _values : [Ljava/lang/Object;
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
    //   26: getstatic gnu/trove/map/hash/THashMap.FREE : Ljava/lang/Object;
    //   29: if_acmpeq -> 14
    //   32: aload_2
    //   33: iload #4
    //   35: aaload
    //   36: getstatic gnu/trove/map/hash/THashMap.REMOVED : Ljava/lang/Object;
    //   39: if_acmpeq -> 14
    //   42: aload_1
    //   43: aload_2
    //   44: iload #4
    //   46: aaload
    //   47: aload_3
    //   48: iload #4
    //   50: aaload
    //   51: invokeinterface execute : (Ljava/lang/Object;Ljava/lang/Object;)Z
    //   56: ifne -> 14
    //   59: iconst_0
    //   60: ireturn
    //   61: iconst_1
    //   62: ireturn
    // Line number table:
    //   Java source line number -> byte code offset
    //   #317	-> 0
    //   #318	-> 5
    //   #319	-> 10
    //   #320	-> 22
    //   #322	-> 51
    //   #323	-> 59
    //   #326	-> 61
    // Local variable table:
    //   start	length	slot	name	descriptor
    //   14	47	4	i	I
    //   0	63	0	this	Lgnu/trove/map/hash/THashMap;
    //   0	63	1	procedure	Lgnu/trove/procedure/TObjectObjectProcedure;
    //   5	58	2	keys	[Ljava/lang/Object;
    //   10	53	3	values	[Ljava/lang/Object;
    // Local variable type table:
    //   start	length	slot	name	signature
    //   0	63	0	this	Lgnu/trove/map/hash/THashMap<TK;TV;>;
    //   0	63	1	procedure	Lgnu/trove/procedure/TObjectObjectProcedure<-TK;-TV;>;
    //   10	53	3	values	[TV;
  }
  
  public boolean retainEntries(TObjectObjectProcedure<? super K, ? super V> procedure) {
    // Byte code:
    //   0: iconst_0
    //   1: istore_2
    //   2: aload_0
    //   3: getfield _set : [Ljava/lang/Object;
    //   6: astore_3
    //   7: aload_0
    //   8: getfield _values : [Ljava/lang/Object;
    //   11: astore #4
    //   13: aload_0
    //   14: invokevirtual tempDisableAutoCompaction : ()V
    //   17: aload_3
    //   18: arraylength
    //   19: istore #5
    //   21: iload #5
    //   23: iinc #5, -1
    //   26: ifle -> 78
    //   29: aload_3
    //   30: iload #5
    //   32: aaload
    //   33: getstatic gnu/trove/map/hash/THashMap.FREE : Ljava/lang/Object;
    //   36: if_acmpeq -> 21
    //   39: aload_3
    //   40: iload #5
    //   42: aaload
    //   43: getstatic gnu/trove/map/hash/THashMap.REMOVED : Ljava/lang/Object;
    //   46: if_acmpeq -> 21
    //   49: aload_1
    //   50: aload_3
    //   51: iload #5
    //   53: aaload
    //   54: aload #4
    //   56: iload #5
    //   58: aaload
    //   59: invokeinterface execute : (Ljava/lang/Object;Ljava/lang/Object;)Z
    //   64: ifne -> 21
    //   67: aload_0
    //   68: iload #5
    //   70: invokevirtual removeAt : (I)V
    //   73: iconst_1
    //   74: istore_2
    //   75: goto -> 21
    //   78: aload_0
    //   79: iconst_1
    //   80: invokevirtual reenableAutoCompaction : (Z)V
    //   83: goto -> 96
    //   86: astore #6
    //   88: aload_0
    //   89: iconst_1
    //   90: invokevirtual reenableAutoCompaction : (Z)V
    //   93: aload #6
    //   95: athrow
    //   96: iload_2
    //   97: ireturn
    // Line number table:
    //   Java source line number -> byte code offset
    //   #339	-> 0
    //   #340	-> 2
    //   #341	-> 7
    //   #344	-> 13
    //   #346	-> 17
    //   #347	-> 29
    //   #349	-> 59
    //   #350	-> 67
    //   #351	-> 73
    //   #355	-> 78
    //   #356	-> 83
    //   #355	-> 86
    //   #358	-> 96
    // Local variable table:
    //   start	length	slot	name	descriptor
    //   21	57	5	i	I
    //   0	98	0	this	Lgnu/trove/map/hash/THashMap;
    //   0	98	1	procedure	Lgnu/trove/procedure/TObjectObjectProcedure;
    //   2	96	2	modified	Z
    //   7	91	3	keys	[Ljava/lang/Object;
    //   13	85	4	values	[Ljava/lang/Object;
    // Local variable type table:
    //   start	length	slot	name	signature
    //   0	98	0	this	Lgnu/trove/map/hash/THashMap<TK;TV;>;
    //   0	98	1	procedure	Lgnu/trove/procedure/TObjectObjectProcedure<-TK;-TV;>;
    //   13	85	4	values	[TV;
    // Exception table:
    //   from	to	target	type
    //   17	78	86	finally
    //   86	88	86	finally
  }
  
  public void transformValues(TObjectFunction<V, V> function) {
    // Byte code:
    //   0: aload_0
    //   1: getfield _values : [Ljava/lang/Object;
    //   4: astore_2
    //   5: aload_0
    //   6: getfield _set : [Ljava/lang/Object;
    //   9: astore_3
    //   10: aload_2
    //   11: arraylength
    //   12: istore #4
    //   14: iload #4
    //   16: iinc #4, -1
    //   19: ifle -> 59
    //   22: aload_3
    //   23: iload #4
    //   25: aaload
    //   26: getstatic gnu/trove/map/hash/THashMap.FREE : Ljava/lang/Object;
    //   29: if_acmpeq -> 14
    //   32: aload_3
    //   33: iload #4
    //   35: aaload
    //   36: getstatic gnu/trove/map/hash/THashMap.REMOVED : Ljava/lang/Object;
    //   39: if_acmpeq -> 14
    //   42: aload_2
    //   43: iload #4
    //   45: aload_1
    //   46: aload_2
    //   47: iload #4
    //   49: aaload
    //   50: invokeinterface execute : (Ljava/lang/Object;)Ljava/lang/Object;
    //   55: aastore
    //   56: goto -> 14
    //   59: return
    // Line number table:
    //   Java source line number -> byte code offset
    //   #368	-> 0
    //   #369	-> 5
    //   #370	-> 10
    //   #371	-> 22
    //   #372	-> 42
    //   #375	-> 59
    // Local variable table:
    //   start	length	slot	name	descriptor
    //   14	45	4	i	I
    //   0	60	0	this	Lgnu/trove/map/hash/THashMap;
    //   0	60	1	function	Lgnu/trove/function/TObjectFunction;
    //   5	55	2	values	[Ljava/lang/Object;
    //   10	50	3	set	[Ljava/lang/Object;
    // Local variable type table:
    //   start	length	slot	name	signature
    //   0	60	0	this	Lgnu/trove/map/hash/THashMap<TK;TV;>;
    //   0	60	1	function	Lgnu/trove/function/TObjectFunction<TV;TV;>;
    //   5	55	2	values	[TV;
  }
  
  protected void rehash(int newCapacity) {
    // Byte code:
    //   0: aload_0
    //   1: getfield _set : [Ljava/lang/Object;
    //   4: arraylength
    //   5: istore_2
    //   6: aload_0
    //   7: invokevirtual size : ()I
    //   10: istore_3
    //   11: aload_0
    //   12: getfield _set : [Ljava/lang/Object;
    //   15: astore #4
    //   17: aload_0
    //   18: getfield _values : [Ljava/lang/Object;
    //   21: astore #5
    //   23: aload_0
    //   24: iload_1
    //   25: anewarray java/lang/Object
    //   28: putfield _set : [Ljava/lang/Object;
    //   31: aload_0
    //   32: getfield _set : [Ljava/lang/Object;
    //   35: getstatic gnu/trove/map/hash/THashMap.FREE : Ljava/lang/Object;
    //   38: invokestatic fill : ([Ljava/lang/Object;Ljava/lang/Object;)V
    //   41: aload_0
    //   42: iload_1
    //   43: anewarray java/lang/Object
    //   46: checkcast [Ljava/lang/Object;
    //   49: putfield _values : [Ljava/lang/Object;
    //   52: iconst_0
    //   53: istore #6
    //   55: iload_2
    //   56: istore #7
    //   58: iload #7
    //   60: iinc #7, -1
    //   63: ifle -> 146
    //   66: aload #4
    //   68: iload #7
    //   70: aaload
    //   71: astore #8
    //   73: aload #8
    //   75: getstatic gnu/trove/map/hash/THashMap.FREE : Ljava/lang/Object;
    //   78: if_acmpeq -> 58
    //   81: aload #8
    //   83: getstatic gnu/trove/map/hash/THashMap.REMOVED : Ljava/lang/Object;
    //   86: if_acmpne -> 92
    //   89: goto -> 58
    //   92: aload_0
    //   93: aload #8
    //   95: invokevirtual insertKey : (Ljava/lang/Object;)I
    //   98: istore #9
    //   100: iload #9
    //   102: ifge -> 128
    //   105: aload_0
    //   106: aload_0
    //   107: getfield _set : [Ljava/lang/Object;
    //   110: iload #9
    //   112: ineg
    //   113: iconst_1
    //   114: isub
    //   115: aaload
    //   116: aload #8
    //   118: aload_0
    //   119: invokevirtual size : ()I
    //   122: iload_3
    //   123: aload #4
    //   125: invokevirtual throwObjectContractViolation : (Ljava/lang/Object;Ljava/lang/Object;II[Ljava/lang/Object;)V
    //   128: aload_0
    //   129: getfield _values : [Ljava/lang/Object;
    //   132: iload #9
    //   134: aload #5
    //   136: iload #7
    //   138: aaload
    //   139: aastore
    //   140: iinc #6, 1
    //   143: goto -> 58
    //   146: aload_0
    //   147: invokevirtual size : ()I
    //   150: iload_3
    //   151: invokestatic reportPotentialConcurrentMod : (II)Ljava/lang/String;
    //   154: pop
    //   155: return
    // Line number table:
    //   Java source line number -> byte code offset
    //   #385	-> 0
    //   #386	-> 6
    //   #387	-> 11
    //   #388	-> 17
    //   #390	-> 23
    //   #391	-> 31
    //   #392	-> 41
    //   #396	-> 52
    //   #397	-> 55
    //   #398	-> 66
    //   #400	-> 73
    //   #402	-> 92
    //   #403	-> 100
    //   #404	-> 105
    //   #406	-> 128
    //   #408	-> 140
    //   #409	-> 143
    //   #412	-> 146
    //   #413	-> 155
    // Local variable table:
    //   start	length	slot	name	descriptor
    //   73	70	8	o	Ljava/lang/Object;
    //   100	43	9	index	I
    //   58	88	7	i	I
    //   0	156	0	this	Lgnu/trove/map/hash/THashMap;
    //   0	156	1	newCapacity	I
    //   6	150	2	oldCapacity	I
    //   11	145	3	oldSize	I
    //   17	139	4	oldKeys	[Ljava/lang/Object;
    //   23	133	5	oldVals	[Ljava/lang/Object;
    //   55	101	6	count	I
    // Local variable type table:
    //   start	length	slot	name	signature
    //   0	156	0	this	Lgnu/trove/map/hash/THashMap<TK;TV;>;
    //   23	133	5	oldVals	[TV;
  }
  
  public V get(Object key) {
    int index = index(key);
    return (index < 0) ? null : this._values[index];
  }
  
  public void clear() {
    // Byte code:
    //   0: aload_0
    //   1: invokevirtual size : ()I
    //   4: ifne -> 8
    //   7: return
    //   8: aload_0
    //   9: invokespecial clear : ()V
    //   12: aload_0
    //   13: getfield _set : [Ljava/lang/Object;
    //   16: iconst_0
    //   17: aload_0
    //   18: getfield _set : [Ljava/lang/Object;
    //   21: arraylength
    //   22: getstatic gnu/trove/map/hash/THashMap.FREE : Ljava/lang/Object;
    //   25: invokestatic fill : ([Ljava/lang/Object;IILjava/lang/Object;)V
    //   28: aload_0
    //   29: getfield _values : [Ljava/lang/Object;
    //   32: iconst_0
    //   33: aload_0
    //   34: getfield _values : [Ljava/lang/Object;
    //   37: arraylength
    //   38: aconst_null
    //   39: invokestatic fill : ([Ljava/lang/Object;IILjava/lang/Object;)V
    //   42: return
    // Line number table:
    //   Java source line number -> byte code offset
    //   #433	-> 0
    //   #434	-> 7
    //   #437	-> 8
    //   #439	-> 12
    //   #440	-> 28
    //   #441	-> 42
    // Local variable table:
    //   start	length	slot	name	descriptor
    //   0	43	0	this	Lgnu/trove/map/hash/THashMap;
    // Local variable type table:
    //   start	length	slot	name	signature
    //   0	43	0	this	Lgnu/trove/map/hash/THashMap<TK;TV;>;
  }
  
  public V remove(Object key) {
    V prev = null;
    int index = index(key);
    if (index >= 0) {
      prev = this._values[index];
      removeAt(index);
    } 
    return prev;
  }
  
  public void removeAt(int index) {
    this._values[index] = null;
    super.removeAt(index);
  }
  
  public Collection<V> values() {
    return new ValueView();
  }
  
  public Set<K> keySet() {
    return new KeyView();
  }
  
  public Set<Map.Entry<K, V>> entrySet() {
    return new EntryView();
  }
  
  public boolean containsValue(Object val) {
    // Byte code:
    //   0: aload_0
    //   1: getfield _set : [Ljava/lang/Object;
    //   4: astore_2
    //   5: aload_0
    //   6: getfield _values : [Ljava/lang/Object;
    //   9: astore_3
    //   10: aconst_null
    //   11: aload_1
    //   12: if_acmpne -> 60
    //   15: aload_3
    //   16: arraylength
    //   17: istore #4
    //   19: iload #4
    //   21: iinc #4, -1
    //   24: ifle -> 57
    //   27: aload_2
    //   28: iload #4
    //   30: aaload
    //   31: getstatic gnu/trove/map/hash/THashMap.FREE : Ljava/lang/Object;
    //   34: if_acmpeq -> 19
    //   37: aload_2
    //   38: iload #4
    //   40: aaload
    //   41: getstatic gnu/trove/map/hash/THashMap.REMOVED : Ljava/lang/Object;
    //   44: if_acmpeq -> 19
    //   47: aload_1
    //   48: aload_3
    //   49: iload #4
    //   51: aaload
    //   52: if_acmpne -> 19
    //   55: iconst_1
    //   56: ireturn
    //   57: goto -> 114
    //   60: aload_3
    //   61: arraylength
    //   62: istore #4
    //   64: iload #4
    //   66: iinc #4, -1
    //   69: ifle -> 114
    //   72: aload_2
    //   73: iload #4
    //   75: aaload
    //   76: getstatic gnu/trove/map/hash/THashMap.FREE : Ljava/lang/Object;
    //   79: if_acmpeq -> 64
    //   82: aload_2
    //   83: iload #4
    //   85: aaload
    //   86: getstatic gnu/trove/map/hash/THashMap.REMOVED : Ljava/lang/Object;
    //   89: if_acmpeq -> 64
    //   92: aload_1
    //   93: aload_3
    //   94: iload #4
    //   96: aaload
    //   97: if_acmpeq -> 112
    //   100: aload_0
    //   101: aload_1
    //   102: aload_3
    //   103: iload #4
    //   105: aaload
    //   106: invokevirtual equals : (Ljava/lang/Object;Ljava/lang/Object;)Z
    //   109: ifeq -> 64
    //   112: iconst_1
    //   113: ireturn
    //   114: iconst_0
    //   115: ireturn
    // Line number table:
    //   Java source line number -> byte code offset
    //   #510	-> 0
    //   #511	-> 5
    //   #515	-> 10
    //   #516	-> 15
    //   #517	-> 27
    //   #519	-> 55
    //   #523	-> 60
    //   #524	-> 72
    //   #525	-> 106
    //   #526	-> 112
    //   #530	-> 114
    // Local variable table:
    //   start	length	slot	name	descriptor
    //   19	38	4	i	I
    //   64	50	4	i	I
    //   0	116	0	this	Lgnu/trove/map/hash/THashMap;
    //   0	116	1	val	Ljava/lang/Object;
    //   5	111	2	set	[Ljava/lang/Object;
    //   10	106	3	vals	[Ljava/lang/Object;
    // Local variable type table:
    //   start	length	slot	name	signature
    //   0	116	0	this	Lgnu/trove/map/hash/THashMap<TK;TV;>;
    //   10	106	3	vals	[TV;
  }
  
  public boolean containsKey(Object key) {
    return contains(key);
  }
  
  public void putAll(Map<? extends K, ? extends V> map) {
    ensureCapacity(map.size());
    for (Map.Entry<? extends K, ? extends V> e : map.entrySet())
      put(e.getKey(), e.getValue()); 
  }
  
  protected class ValueView extends MapBackedView<V> {
    public Iterator<V> iterator() {
      return (Iterator<V>)new TObjectHashIterator(THashMap.this) {
          protected V objectAtIndex(int index) {
            return THashMap.this._values[index];
          }
        };
    }
    
    public boolean containsElement(V value) {
      return THashMap.this.containsValue(value);
    }
    
    public boolean removeElement(V value) {
      V[] arrayOfV = THashMap.this._values;
      Object[] set = THashMap.this._set;
      for (int i = arrayOfV.length; i-- > 0;) {
        if ((set[i] != TObjectHash.FREE && set[i] != TObjectHash.REMOVED && value == arrayOfV[i]) || (null != arrayOfV[i] && THashMap.this
          
          .equals(arrayOfV[i], value))) {
          THashMap.this.removeAt(i);
          return true;
        } 
      } 
      return false;
    }
  }
  
  protected class EntryView extends MapBackedView<Map.Entry<K, V>> {
    private final class EntryIterator extends TObjectHashIterator {
      EntryIterator(THashMap<K, V> map) {
        super(map);
      }
      
      public THashMap<K, V>.Entry objectAtIndex(int index) {
        return new THashMap.Entry((K)THashMap.this._set[index], THashMap.this._values[index], index);
      }
    }
    
    public Iterator<Map.Entry<K, V>> iterator() {
      return (Iterator<Map.Entry<K, V>>)new EntryIterator(THashMap.this);
    }
    
    public boolean removeElement(Map.Entry<K, V> entry) {
      if (entry == null)
        return false; 
      K key = keyForEntry(entry);
      int index = THashMap.this.index(key);
      if (index >= 0) {
        V val = valueForEntry(entry);
        if (val == THashMap.this._values[index] || (null != val && THashMap.this
          .equals(val, THashMap.this._values[index]))) {
          THashMap.this.removeAt(index);
          return true;
        } 
      } 
      return false;
    }
    
    public boolean containsElement(Map.Entry<K, V> entry) {
      V val = (V)THashMap.this.get(keyForEntry(entry));
      V entryValue = entry.getValue();
      return (entryValue == val || (null != val && THashMap.this
        .equals(val, entryValue)));
    }
    
    protected V valueForEntry(Map.Entry<K, V> entry) {
      return entry.getValue();
    }
    
    protected K keyForEntry(Map.Entry<K, V> entry) {
      return entry.getKey();
    }
  }
  
  private abstract class MapBackedView<E> extends AbstractSet<E> implements Set<E>, Iterable<E> {
    private MapBackedView() {}
    
    public boolean contains(Object key) {
      return containsElement((E)key);
    }
    
    public boolean remove(Object o) {
      try {
        return removeElement((E)o);
      } catch (ClassCastException ex) {
        return false;
      } 
    }
    
    public void clear() {
      THashMap.this.clear();
    }
    
    public boolean add(E obj) {
      throw new UnsupportedOperationException();
    }
    
    public int size() {
      return THashMap.this.size();
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
        a = (T[])Array.newInstance(a.getClass().getComponentType(), size); 
      Iterator<E> it = iterator();
      T[] arrayOfT = a;
      for (int i = 0; i < size; i++)
        arrayOfT[i] = (T)it.next(); 
      if (a.length > size)
        a[size] = null; 
      return a;
    }
    
    public boolean isEmpty() {
      return THashMap.this.isEmpty();
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
    
    public String toString() {
      Iterator<E> i = iterator();
      if (!i.hasNext())
        return "{}"; 
      StringBuilder sb = new StringBuilder();
      sb.append('{');
      while (true) {
        E e = i.next();
        sb.append((e == this) ? "(this Collection)" : e);
        if (!i.hasNext())
          return sb.append('}').toString(); 
        sb.append(", ");
      } 
    }
    
    public abstract Iterator<E> iterator();
    
    public abstract boolean removeElement(E param1E);
    
    public abstract boolean containsElement(E param1E);
  }
  
  protected class KeyView extends MapBackedView<K> {
    public Iterator<K> iterator() {
      return (Iterator<K>)new TObjectHashIterator(THashMap.this);
    }
    
    public boolean removeElement(K key) {
      return (null != THashMap.this.remove(key));
    }
    
    public boolean containsElement(K key) {
      return THashMap.this.contains(key);
    }
  }
  
  final class Entry implements Map.Entry<K, V> {
    private K key;
    
    private V val;
    
    private final int index;
    
    Entry(K key, V value, int index) {
      this.key = key;
      this.val = value;
      this.index = index;
    }
    
    public K getKey() {
      return this.key;
    }
    
    public V getValue() {
      return this.val;
    }
    
    public V setValue(V o) {
      if (THashMap.this._values[this.index] != this.val)
        throw new ConcurrentModificationException(); 
      V retval = this.val;
      THashMap.this._values[this.index] = o;
      this.val = o;
      return retval;
    }
    
    public boolean equals(Object o) {
      if (o instanceof Map.Entry) {
        Map.Entry<K, V> e1 = this;
        Map.Entry e2 = (Map.Entry)o;
        return (THashMap.this.equals(e1.getKey(), e2.getKey()) && THashMap.this
          .equals(e1.getValue(), e1.getValue()));
      } 
      return false;
    }
    
    public int hashCode() {
      return ((getKey() == null) ? 0 : getKey().hashCode()) ^ ((getValue() == null) ? 0 : getValue().hashCode());
    }
    
    public String toString() {
      return (new StringBuilder()).append(this.key).append("=").append(this.val).toString();
    }
  }
  
  public void writeExternal(ObjectOutput out) throws IOException {
    // Byte code:
    //   0: aload_1
    //   1: iconst_1
    //   2: invokeinterface writeByte : (I)V
    //   7: aload_0
    //   8: aload_1
    //   9: invokespecial writeExternal : (Ljava/io/ObjectOutput;)V
    //   12: aload_1
    //   13: aload_0
    //   14: getfield _size : I
    //   17: invokeinterface writeInt : (I)V
    //   22: aload_0
    //   23: getfield _set : [Ljava/lang/Object;
    //   26: arraylength
    //   27: istore_2
    //   28: iload_2
    //   29: iinc #2, -1
    //   32: ifle -> 86
    //   35: aload_0
    //   36: getfield _set : [Ljava/lang/Object;
    //   39: iload_2
    //   40: aaload
    //   41: getstatic gnu/trove/map/hash/THashMap.REMOVED : Ljava/lang/Object;
    //   44: if_acmpeq -> 28
    //   47: aload_0
    //   48: getfield _set : [Ljava/lang/Object;
    //   51: iload_2
    //   52: aaload
    //   53: getstatic gnu/trove/map/hash/THashMap.FREE : Ljava/lang/Object;
    //   56: if_acmpeq -> 28
    //   59: aload_1
    //   60: aload_0
    //   61: getfield _set : [Ljava/lang/Object;
    //   64: iload_2
    //   65: aaload
    //   66: invokeinterface writeObject : (Ljava/lang/Object;)V
    //   71: aload_1
    //   72: aload_0
    //   73: getfield _values : [Ljava/lang/Object;
    //   76: iload_2
    //   77: aaload
    //   78: invokeinterface writeObject : (Ljava/lang/Object;)V
    //   83: goto -> 28
    //   86: return
    // Line number table:
    //   Java source line number -> byte code offset
    //   #875	-> 0
    //   #878	-> 7
    //   #881	-> 12
    //   #884	-> 22
    //   #885	-> 35
    //   #886	-> 59
    //   #887	-> 71
    //   #890	-> 86
    // Local variable table:
    //   start	length	slot	name	descriptor
    //   28	58	2	i	I
    //   0	87	0	this	Lgnu/trove/map/hash/THashMap;
    //   0	87	1	out	Ljava/io/ObjectOutput;
    // Local variable type table:
    //   start	length	slot	name	signature
    //   0	87	0	this	Lgnu/trove/map/hash/THashMap<TK;TV;>;
  }
  
  public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
    byte version = in.readByte();
    if (version != 0)
      super.readExternal(in); 
    int size = in.readInt();
    setUp(size);
    while (size-- > 0) {
      K key = (K)in.readObject();
      V val = (V)in.readObject();
      put(key, val);
    } 
  }
}
