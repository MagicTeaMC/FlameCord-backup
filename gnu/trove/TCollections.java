package gnu.trove;

import gnu.trove.impl.sync.TSynchronizedByteByteMap;
import gnu.trove.impl.sync.TSynchronizedByteCharMap;
import gnu.trove.impl.sync.TSynchronizedByteCollection;
import gnu.trove.impl.sync.TSynchronizedByteDoubleMap;
import gnu.trove.impl.sync.TSynchronizedByteFloatMap;
import gnu.trove.impl.sync.TSynchronizedByteIntMap;
import gnu.trove.impl.sync.TSynchronizedByteList;
import gnu.trove.impl.sync.TSynchronizedByteLongMap;
import gnu.trove.impl.sync.TSynchronizedByteObjectMap;
import gnu.trove.impl.sync.TSynchronizedByteSet;
import gnu.trove.impl.sync.TSynchronizedByteShortMap;
import gnu.trove.impl.sync.TSynchronizedCharByteMap;
import gnu.trove.impl.sync.TSynchronizedCharCharMap;
import gnu.trove.impl.sync.TSynchronizedCharCollection;
import gnu.trove.impl.sync.TSynchronizedCharDoubleMap;
import gnu.trove.impl.sync.TSynchronizedCharFloatMap;
import gnu.trove.impl.sync.TSynchronizedCharIntMap;
import gnu.trove.impl.sync.TSynchronizedCharList;
import gnu.trove.impl.sync.TSynchronizedCharLongMap;
import gnu.trove.impl.sync.TSynchronizedCharObjectMap;
import gnu.trove.impl.sync.TSynchronizedCharSet;
import gnu.trove.impl.sync.TSynchronizedCharShortMap;
import gnu.trove.impl.sync.TSynchronizedDoubleByteMap;
import gnu.trove.impl.sync.TSynchronizedDoubleCharMap;
import gnu.trove.impl.sync.TSynchronizedDoubleCollection;
import gnu.trove.impl.sync.TSynchronizedDoubleDoubleMap;
import gnu.trove.impl.sync.TSynchronizedDoubleFloatMap;
import gnu.trove.impl.sync.TSynchronizedDoubleIntMap;
import gnu.trove.impl.sync.TSynchronizedDoubleList;
import gnu.trove.impl.sync.TSynchronizedDoubleLongMap;
import gnu.trove.impl.sync.TSynchronizedDoubleObjectMap;
import gnu.trove.impl.sync.TSynchronizedDoubleSet;
import gnu.trove.impl.sync.TSynchronizedDoubleShortMap;
import gnu.trove.impl.sync.TSynchronizedFloatByteMap;
import gnu.trove.impl.sync.TSynchronizedFloatCharMap;
import gnu.trove.impl.sync.TSynchronizedFloatCollection;
import gnu.trove.impl.sync.TSynchronizedFloatDoubleMap;
import gnu.trove.impl.sync.TSynchronizedFloatFloatMap;
import gnu.trove.impl.sync.TSynchronizedFloatIntMap;
import gnu.trove.impl.sync.TSynchronizedFloatList;
import gnu.trove.impl.sync.TSynchronizedFloatLongMap;
import gnu.trove.impl.sync.TSynchronizedFloatObjectMap;
import gnu.trove.impl.sync.TSynchronizedFloatSet;
import gnu.trove.impl.sync.TSynchronizedFloatShortMap;
import gnu.trove.impl.sync.TSynchronizedIntByteMap;
import gnu.trove.impl.sync.TSynchronizedIntCharMap;
import gnu.trove.impl.sync.TSynchronizedIntCollection;
import gnu.trove.impl.sync.TSynchronizedIntDoubleMap;
import gnu.trove.impl.sync.TSynchronizedIntFloatMap;
import gnu.trove.impl.sync.TSynchronizedIntIntMap;
import gnu.trove.impl.sync.TSynchronizedIntList;
import gnu.trove.impl.sync.TSynchronizedIntLongMap;
import gnu.trove.impl.sync.TSynchronizedIntObjectMap;
import gnu.trove.impl.sync.TSynchronizedIntSet;
import gnu.trove.impl.sync.TSynchronizedIntShortMap;
import gnu.trove.impl.sync.TSynchronizedLongByteMap;
import gnu.trove.impl.sync.TSynchronizedLongCharMap;
import gnu.trove.impl.sync.TSynchronizedLongCollection;
import gnu.trove.impl.sync.TSynchronizedLongDoubleMap;
import gnu.trove.impl.sync.TSynchronizedLongFloatMap;
import gnu.trove.impl.sync.TSynchronizedLongIntMap;
import gnu.trove.impl.sync.TSynchronizedLongList;
import gnu.trove.impl.sync.TSynchronizedLongLongMap;
import gnu.trove.impl.sync.TSynchronizedLongObjectMap;
import gnu.trove.impl.sync.TSynchronizedLongSet;
import gnu.trove.impl.sync.TSynchronizedLongShortMap;
import gnu.trove.impl.sync.TSynchronizedObjectByteMap;
import gnu.trove.impl.sync.TSynchronizedObjectCharMap;
import gnu.trove.impl.sync.TSynchronizedObjectDoubleMap;
import gnu.trove.impl.sync.TSynchronizedObjectFloatMap;
import gnu.trove.impl.sync.TSynchronizedObjectIntMap;
import gnu.trove.impl.sync.TSynchronizedObjectLongMap;
import gnu.trove.impl.sync.TSynchronizedObjectShortMap;
import gnu.trove.impl.sync.TSynchronizedRandomAccessByteList;
import gnu.trove.impl.sync.TSynchronizedRandomAccessCharList;
import gnu.trove.impl.sync.TSynchronizedRandomAccessDoubleList;
import gnu.trove.impl.sync.TSynchronizedRandomAccessFloatList;
import gnu.trove.impl.sync.TSynchronizedRandomAccessIntList;
import gnu.trove.impl.sync.TSynchronizedRandomAccessLongList;
import gnu.trove.impl.sync.TSynchronizedRandomAccessShortList;
import gnu.trove.impl.sync.TSynchronizedShortByteMap;
import gnu.trove.impl.sync.TSynchronizedShortCharMap;
import gnu.trove.impl.sync.TSynchronizedShortCollection;
import gnu.trove.impl.sync.TSynchronizedShortDoubleMap;
import gnu.trove.impl.sync.TSynchronizedShortFloatMap;
import gnu.trove.impl.sync.TSynchronizedShortIntMap;
import gnu.trove.impl.sync.TSynchronizedShortList;
import gnu.trove.impl.sync.TSynchronizedShortLongMap;
import gnu.trove.impl.sync.TSynchronizedShortObjectMap;
import gnu.trove.impl.sync.TSynchronizedShortSet;
import gnu.trove.impl.sync.TSynchronizedShortShortMap;
import gnu.trove.impl.unmodifiable.TUnmodifiableByteByteMap;
import gnu.trove.impl.unmodifiable.TUnmodifiableByteCharMap;
import gnu.trove.impl.unmodifiable.TUnmodifiableByteCollection;
import gnu.trove.impl.unmodifiable.TUnmodifiableByteDoubleMap;
import gnu.trove.impl.unmodifiable.TUnmodifiableByteFloatMap;
import gnu.trove.impl.unmodifiable.TUnmodifiableByteIntMap;
import gnu.trove.impl.unmodifiable.TUnmodifiableByteList;
import gnu.trove.impl.unmodifiable.TUnmodifiableByteLongMap;
import gnu.trove.impl.unmodifiable.TUnmodifiableByteObjectMap;
import gnu.trove.impl.unmodifiable.TUnmodifiableByteSet;
import gnu.trove.impl.unmodifiable.TUnmodifiableByteShortMap;
import gnu.trove.impl.unmodifiable.TUnmodifiableCharByteMap;
import gnu.trove.impl.unmodifiable.TUnmodifiableCharCharMap;
import gnu.trove.impl.unmodifiable.TUnmodifiableCharCollection;
import gnu.trove.impl.unmodifiable.TUnmodifiableCharDoubleMap;
import gnu.trove.impl.unmodifiable.TUnmodifiableCharFloatMap;
import gnu.trove.impl.unmodifiable.TUnmodifiableCharIntMap;
import gnu.trove.impl.unmodifiable.TUnmodifiableCharList;
import gnu.trove.impl.unmodifiable.TUnmodifiableCharLongMap;
import gnu.trove.impl.unmodifiable.TUnmodifiableCharObjectMap;
import gnu.trove.impl.unmodifiable.TUnmodifiableCharSet;
import gnu.trove.impl.unmodifiable.TUnmodifiableCharShortMap;
import gnu.trove.impl.unmodifiable.TUnmodifiableDoubleByteMap;
import gnu.trove.impl.unmodifiable.TUnmodifiableDoubleCharMap;
import gnu.trove.impl.unmodifiable.TUnmodifiableDoubleCollection;
import gnu.trove.impl.unmodifiable.TUnmodifiableDoubleDoubleMap;
import gnu.trove.impl.unmodifiable.TUnmodifiableDoubleFloatMap;
import gnu.trove.impl.unmodifiable.TUnmodifiableDoubleIntMap;
import gnu.trove.impl.unmodifiable.TUnmodifiableDoubleList;
import gnu.trove.impl.unmodifiable.TUnmodifiableDoubleLongMap;
import gnu.trove.impl.unmodifiable.TUnmodifiableDoubleObjectMap;
import gnu.trove.impl.unmodifiable.TUnmodifiableDoubleSet;
import gnu.trove.impl.unmodifiable.TUnmodifiableDoubleShortMap;
import gnu.trove.impl.unmodifiable.TUnmodifiableFloatByteMap;
import gnu.trove.impl.unmodifiable.TUnmodifiableFloatCharMap;
import gnu.trove.impl.unmodifiable.TUnmodifiableFloatCollection;
import gnu.trove.impl.unmodifiable.TUnmodifiableFloatDoubleMap;
import gnu.trove.impl.unmodifiable.TUnmodifiableFloatFloatMap;
import gnu.trove.impl.unmodifiable.TUnmodifiableFloatIntMap;
import gnu.trove.impl.unmodifiable.TUnmodifiableFloatList;
import gnu.trove.impl.unmodifiable.TUnmodifiableFloatLongMap;
import gnu.trove.impl.unmodifiable.TUnmodifiableFloatObjectMap;
import gnu.trove.impl.unmodifiable.TUnmodifiableFloatSet;
import gnu.trove.impl.unmodifiable.TUnmodifiableFloatShortMap;
import gnu.trove.impl.unmodifiable.TUnmodifiableIntByteMap;
import gnu.trove.impl.unmodifiable.TUnmodifiableIntCharMap;
import gnu.trove.impl.unmodifiable.TUnmodifiableIntCollection;
import gnu.trove.impl.unmodifiable.TUnmodifiableIntDoubleMap;
import gnu.trove.impl.unmodifiable.TUnmodifiableIntFloatMap;
import gnu.trove.impl.unmodifiable.TUnmodifiableIntIntMap;
import gnu.trove.impl.unmodifiable.TUnmodifiableIntList;
import gnu.trove.impl.unmodifiable.TUnmodifiableIntLongMap;
import gnu.trove.impl.unmodifiable.TUnmodifiableIntObjectMap;
import gnu.trove.impl.unmodifiable.TUnmodifiableIntSet;
import gnu.trove.impl.unmodifiable.TUnmodifiableIntShortMap;
import gnu.trove.impl.unmodifiable.TUnmodifiableLongByteMap;
import gnu.trove.impl.unmodifiable.TUnmodifiableLongCharMap;
import gnu.trove.impl.unmodifiable.TUnmodifiableLongCollection;
import gnu.trove.impl.unmodifiable.TUnmodifiableLongDoubleMap;
import gnu.trove.impl.unmodifiable.TUnmodifiableLongFloatMap;
import gnu.trove.impl.unmodifiable.TUnmodifiableLongIntMap;
import gnu.trove.impl.unmodifiable.TUnmodifiableLongList;
import gnu.trove.impl.unmodifiable.TUnmodifiableLongLongMap;
import gnu.trove.impl.unmodifiable.TUnmodifiableLongObjectMap;
import gnu.trove.impl.unmodifiable.TUnmodifiableLongSet;
import gnu.trove.impl.unmodifiable.TUnmodifiableLongShortMap;
import gnu.trove.impl.unmodifiable.TUnmodifiableObjectByteMap;
import gnu.trove.impl.unmodifiable.TUnmodifiableObjectCharMap;
import gnu.trove.impl.unmodifiable.TUnmodifiableObjectDoubleMap;
import gnu.trove.impl.unmodifiable.TUnmodifiableObjectFloatMap;
import gnu.trove.impl.unmodifiable.TUnmodifiableObjectIntMap;
import gnu.trove.impl.unmodifiable.TUnmodifiableObjectLongMap;
import gnu.trove.impl.unmodifiable.TUnmodifiableObjectShortMap;
import gnu.trove.impl.unmodifiable.TUnmodifiableRandomAccessByteList;
import gnu.trove.impl.unmodifiable.TUnmodifiableRandomAccessCharList;
import gnu.trove.impl.unmodifiable.TUnmodifiableRandomAccessDoubleList;
import gnu.trove.impl.unmodifiable.TUnmodifiableRandomAccessFloatList;
import gnu.trove.impl.unmodifiable.TUnmodifiableRandomAccessIntList;
import gnu.trove.impl.unmodifiable.TUnmodifiableRandomAccessLongList;
import gnu.trove.impl.unmodifiable.TUnmodifiableRandomAccessShortList;
import gnu.trove.impl.unmodifiable.TUnmodifiableShortByteMap;
import gnu.trove.impl.unmodifiable.TUnmodifiableShortCharMap;
import gnu.trove.impl.unmodifiable.TUnmodifiableShortCollection;
import gnu.trove.impl.unmodifiable.TUnmodifiableShortDoubleMap;
import gnu.trove.impl.unmodifiable.TUnmodifiableShortFloatMap;
import gnu.trove.impl.unmodifiable.TUnmodifiableShortIntMap;
import gnu.trove.impl.unmodifiable.TUnmodifiableShortList;
import gnu.trove.impl.unmodifiable.TUnmodifiableShortLongMap;
import gnu.trove.impl.unmodifiable.TUnmodifiableShortObjectMap;
import gnu.trove.impl.unmodifiable.TUnmodifiableShortSet;
import gnu.trove.impl.unmodifiable.TUnmodifiableShortShortMap;
import gnu.trove.list.TByteList;
import gnu.trove.list.TCharList;
import gnu.trove.list.TDoubleList;
import gnu.trove.list.TFloatList;
import gnu.trove.list.TIntList;
import gnu.trove.list.TLongList;
import gnu.trove.list.TShortList;
import gnu.trove.map.TByteByteMap;
import gnu.trove.map.TByteCharMap;
import gnu.trove.map.TByteDoubleMap;
import gnu.trove.map.TByteFloatMap;
import gnu.trove.map.TByteIntMap;
import gnu.trove.map.TByteLongMap;
import gnu.trove.map.TByteObjectMap;
import gnu.trove.map.TByteShortMap;
import gnu.trove.map.TCharByteMap;
import gnu.trove.map.TCharCharMap;
import gnu.trove.map.TCharDoubleMap;
import gnu.trove.map.TCharFloatMap;
import gnu.trove.map.TCharIntMap;
import gnu.trove.map.TCharLongMap;
import gnu.trove.map.TCharObjectMap;
import gnu.trove.map.TCharShortMap;
import gnu.trove.map.TDoubleByteMap;
import gnu.trove.map.TDoubleCharMap;
import gnu.trove.map.TDoubleDoubleMap;
import gnu.trove.map.TDoubleFloatMap;
import gnu.trove.map.TDoubleIntMap;
import gnu.trove.map.TDoubleLongMap;
import gnu.trove.map.TDoubleObjectMap;
import gnu.trove.map.TDoubleShortMap;
import gnu.trove.map.TFloatByteMap;
import gnu.trove.map.TFloatCharMap;
import gnu.trove.map.TFloatDoubleMap;
import gnu.trove.map.TFloatFloatMap;
import gnu.trove.map.TFloatIntMap;
import gnu.trove.map.TFloatLongMap;
import gnu.trove.map.TFloatObjectMap;
import gnu.trove.map.TFloatShortMap;
import gnu.trove.map.TIntByteMap;
import gnu.trove.map.TIntCharMap;
import gnu.trove.map.TIntDoubleMap;
import gnu.trove.map.TIntFloatMap;
import gnu.trove.map.TIntIntMap;
import gnu.trove.map.TIntLongMap;
import gnu.trove.map.TIntObjectMap;
import gnu.trove.map.TIntShortMap;
import gnu.trove.map.TLongByteMap;
import gnu.trove.map.TLongCharMap;
import gnu.trove.map.TLongDoubleMap;
import gnu.trove.map.TLongFloatMap;
import gnu.trove.map.TLongIntMap;
import gnu.trove.map.TLongLongMap;
import gnu.trove.map.TLongObjectMap;
import gnu.trove.map.TLongShortMap;
import gnu.trove.map.TObjectByteMap;
import gnu.trove.map.TObjectCharMap;
import gnu.trove.map.TObjectDoubleMap;
import gnu.trove.map.TObjectFloatMap;
import gnu.trove.map.TObjectIntMap;
import gnu.trove.map.TObjectLongMap;
import gnu.trove.map.TObjectShortMap;
import gnu.trove.map.TShortByteMap;
import gnu.trove.map.TShortCharMap;
import gnu.trove.map.TShortDoubleMap;
import gnu.trove.map.TShortFloatMap;
import gnu.trove.map.TShortIntMap;
import gnu.trove.map.TShortLongMap;
import gnu.trove.map.TShortObjectMap;
import gnu.trove.map.TShortShortMap;
import gnu.trove.set.TByteSet;
import gnu.trove.set.TCharSet;
import gnu.trove.set.TDoubleSet;
import gnu.trove.set.TFloatSet;
import gnu.trove.set.TIntSet;
import gnu.trove.set.TLongSet;
import gnu.trove.set.TShortSet;

public class TCollections {
  public static TDoubleCollection unmodifiableCollection(TDoubleCollection c) {
    return (TDoubleCollection)new TUnmodifiableDoubleCollection(c);
  }
  
  public static TFloatCollection unmodifiableCollection(TFloatCollection c) {
    return (TFloatCollection)new TUnmodifiableFloatCollection(c);
  }
  
  public static TIntCollection unmodifiableCollection(TIntCollection c) {
    return (TIntCollection)new TUnmodifiableIntCollection(c);
  }
  
  public static TLongCollection unmodifiableCollection(TLongCollection c) {
    return (TLongCollection)new TUnmodifiableLongCollection(c);
  }
  
  public static TByteCollection unmodifiableCollection(TByteCollection c) {
    return (TByteCollection)new TUnmodifiableByteCollection(c);
  }
  
  public static TShortCollection unmodifiableCollection(TShortCollection c) {
    return (TShortCollection)new TUnmodifiableShortCollection(c);
  }
  
  public static TCharCollection unmodifiableCollection(TCharCollection c) {
    return (TCharCollection)new TUnmodifiableCharCollection(c);
  }
  
  public static TDoubleSet unmodifiableSet(TDoubleSet s) {
    return (TDoubleSet)new TUnmodifiableDoubleSet(s);
  }
  
  public static TFloatSet unmodifiableSet(TFloatSet s) {
    return (TFloatSet)new TUnmodifiableFloatSet(s);
  }
  
  public static TIntSet unmodifiableSet(TIntSet s) {
    return (TIntSet)new TUnmodifiableIntSet(s);
  }
  
  public static TLongSet unmodifiableSet(TLongSet s) {
    return (TLongSet)new TUnmodifiableLongSet(s);
  }
  
  public static TByteSet unmodifiableSet(TByteSet s) {
    return (TByteSet)new TUnmodifiableByteSet(s);
  }
  
  public static TShortSet unmodifiableSet(TShortSet s) {
    return (TShortSet)new TUnmodifiableShortSet(s);
  }
  
  public static TCharSet unmodifiableSet(TCharSet s) {
    return (TCharSet)new TUnmodifiableCharSet(s);
  }
  
  public static TDoubleList unmodifiableList(TDoubleList list) {
    return (list instanceof java.util.RandomAccess) ? (TDoubleList)new TUnmodifiableRandomAccessDoubleList(list) : (TDoubleList)new TUnmodifiableDoubleList(list);
  }
  
  public static TFloatList unmodifiableList(TFloatList list) {
    return (list instanceof java.util.RandomAccess) ? (TFloatList)new TUnmodifiableRandomAccessFloatList(list) : (TFloatList)new TUnmodifiableFloatList(list);
  }
  
  public static TIntList unmodifiableList(TIntList list) {
    return (list instanceof java.util.RandomAccess) ? (TIntList)new TUnmodifiableRandomAccessIntList(list) : (TIntList)new TUnmodifiableIntList(list);
  }
  
  public static TLongList unmodifiableList(TLongList list) {
    return (list instanceof java.util.RandomAccess) ? (TLongList)new TUnmodifiableRandomAccessLongList(list) : (TLongList)new TUnmodifiableLongList(list);
  }
  
  public static TByteList unmodifiableList(TByteList list) {
    return (list instanceof java.util.RandomAccess) ? (TByteList)new TUnmodifiableRandomAccessByteList(list) : (TByteList)new TUnmodifiableByteList(list);
  }
  
  public static TShortList unmodifiableList(TShortList list) {
    return (list instanceof java.util.RandomAccess) ? (TShortList)new TUnmodifiableRandomAccessShortList(list) : (TShortList)new TUnmodifiableShortList(list);
  }
  
  public static TCharList unmodifiableList(TCharList list) {
    return (list instanceof java.util.RandomAccess) ? (TCharList)new TUnmodifiableRandomAccessCharList(list) : (TCharList)new TUnmodifiableCharList(list);
  }
  
  public static TDoubleDoubleMap unmodifiableMap(TDoubleDoubleMap m) {
    return (TDoubleDoubleMap)new TUnmodifiableDoubleDoubleMap(m);
  }
  
  public static TDoubleFloatMap unmodifiableMap(TDoubleFloatMap m) {
    return (TDoubleFloatMap)new TUnmodifiableDoubleFloatMap(m);
  }
  
  public static TDoubleIntMap unmodifiableMap(TDoubleIntMap m) {
    return (TDoubleIntMap)new TUnmodifiableDoubleIntMap(m);
  }
  
  public static TDoubleLongMap unmodifiableMap(TDoubleLongMap m) {
    return (TDoubleLongMap)new TUnmodifiableDoubleLongMap(m);
  }
  
  public static TDoubleByteMap unmodifiableMap(TDoubleByteMap m) {
    return (TDoubleByteMap)new TUnmodifiableDoubleByteMap(m);
  }
  
  public static TDoubleShortMap unmodifiableMap(TDoubleShortMap m) {
    return (TDoubleShortMap)new TUnmodifiableDoubleShortMap(m);
  }
  
  public static TDoubleCharMap unmodifiableMap(TDoubleCharMap m) {
    return (TDoubleCharMap)new TUnmodifiableDoubleCharMap(m);
  }
  
  public static TFloatDoubleMap unmodifiableMap(TFloatDoubleMap m) {
    return (TFloatDoubleMap)new TUnmodifiableFloatDoubleMap(m);
  }
  
  public static TFloatFloatMap unmodifiableMap(TFloatFloatMap m) {
    return (TFloatFloatMap)new TUnmodifiableFloatFloatMap(m);
  }
  
  public static TFloatIntMap unmodifiableMap(TFloatIntMap m) {
    return (TFloatIntMap)new TUnmodifiableFloatIntMap(m);
  }
  
  public static TFloatLongMap unmodifiableMap(TFloatLongMap m) {
    return (TFloatLongMap)new TUnmodifiableFloatLongMap(m);
  }
  
  public static TFloatByteMap unmodifiableMap(TFloatByteMap m) {
    return (TFloatByteMap)new TUnmodifiableFloatByteMap(m);
  }
  
  public static TFloatShortMap unmodifiableMap(TFloatShortMap m) {
    return (TFloatShortMap)new TUnmodifiableFloatShortMap(m);
  }
  
  public static TFloatCharMap unmodifiableMap(TFloatCharMap m) {
    return (TFloatCharMap)new TUnmodifiableFloatCharMap(m);
  }
  
  public static TIntDoubleMap unmodifiableMap(TIntDoubleMap m) {
    return (TIntDoubleMap)new TUnmodifiableIntDoubleMap(m);
  }
  
  public static TIntFloatMap unmodifiableMap(TIntFloatMap m) {
    return (TIntFloatMap)new TUnmodifiableIntFloatMap(m);
  }
  
  public static TIntIntMap unmodifiableMap(TIntIntMap m) {
    return (TIntIntMap)new TUnmodifiableIntIntMap(m);
  }
  
  public static TIntLongMap unmodifiableMap(TIntLongMap m) {
    return (TIntLongMap)new TUnmodifiableIntLongMap(m);
  }
  
  public static TIntByteMap unmodifiableMap(TIntByteMap m) {
    return (TIntByteMap)new TUnmodifiableIntByteMap(m);
  }
  
  public static TIntShortMap unmodifiableMap(TIntShortMap m) {
    return (TIntShortMap)new TUnmodifiableIntShortMap(m);
  }
  
  public static TIntCharMap unmodifiableMap(TIntCharMap m) {
    return (TIntCharMap)new TUnmodifiableIntCharMap(m);
  }
  
  public static TLongDoubleMap unmodifiableMap(TLongDoubleMap m) {
    return (TLongDoubleMap)new TUnmodifiableLongDoubleMap(m);
  }
  
  public static TLongFloatMap unmodifiableMap(TLongFloatMap m) {
    return (TLongFloatMap)new TUnmodifiableLongFloatMap(m);
  }
  
  public static TLongIntMap unmodifiableMap(TLongIntMap m) {
    return (TLongIntMap)new TUnmodifiableLongIntMap(m);
  }
  
  public static TLongLongMap unmodifiableMap(TLongLongMap m) {
    return (TLongLongMap)new TUnmodifiableLongLongMap(m);
  }
  
  public static TLongByteMap unmodifiableMap(TLongByteMap m) {
    return (TLongByteMap)new TUnmodifiableLongByteMap(m);
  }
  
  public static TLongShortMap unmodifiableMap(TLongShortMap m) {
    return (TLongShortMap)new TUnmodifiableLongShortMap(m);
  }
  
  public static TLongCharMap unmodifiableMap(TLongCharMap m) {
    return (TLongCharMap)new TUnmodifiableLongCharMap(m);
  }
  
  public static TByteDoubleMap unmodifiableMap(TByteDoubleMap m) {
    return (TByteDoubleMap)new TUnmodifiableByteDoubleMap(m);
  }
  
  public static TByteFloatMap unmodifiableMap(TByteFloatMap m) {
    return (TByteFloatMap)new TUnmodifiableByteFloatMap(m);
  }
  
  public static TByteIntMap unmodifiableMap(TByteIntMap m) {
    return (TByteIntMap)new TUnmodifiableByteIntMap(m);
  }
  
  public static TByteLongMap unmodifiableMap(TByteLongMap m) {
    return (TByteLongMap)new TUnmodifiableByteLongMap(m);
  }
  
  public static TByteByteMap unmodifiableMap(TByteByteMap m) {
    return (TByteByteMap)new TUnmodifiableByteByteMap(m);
  }
  
  public static TByteShortMap unmodifiableMap(TByteShortMap m) {
    return (TByteShortMap)new TUnmodifiableByteShortMap(m);
  }
  
  public static TByteCharMap unmodifiableMap(TByteCharMap m) {
    return (TByteCharMap)new TUnmodifiableByteCharMap(m);
  }
  
  public static TShortDoubleMap unmodifiableMap(TShortDoubleMap m) {
    return (TShortDoubleMap)new TUnmodifiableShortDoubleMap(m);
  }
  
  public static TShortFloatMap unmodifiableMap(TShortFloatMap m) {
    return (TShortFloatMap)new TUnmodifiableShortFloatMap(m);
  }
  
  public static TShortIntMap unmodifiableMap(TShortIntMap m) {
    return (TShortIntMap)new TUnmodifiableShortIntMap(m);
  }
  
  public static TShortLongMap unmodifiableMap(TShortLongMap m) {
    return (TShortLongMap)new TUnmodifiableShortLongMap(m);
  }
  
  public static TShortByteMap unmodifiableMap(TShortByteMap m) {
    return (TShortByteMap)new TUnmodifiableShortByteMap(m);
  }
  
  public static TShortShortMap unmodifiableMap(TShortShortMap m) {
    return (TShortShortMap)new TUnmodifiableShortShortMap(m);
  }
  
  public static TShortCharMap unmodifiableMap(TShortCharMap m) {
    return (TShortCharMap)new TUnmodifiableShortCharMap(m);
  }
  
  public static TCharDoubleMap unmodifiableMap(TCharDoubleMap m) {
    return (TCharDoubleMap)new TUnmodifiableCharDoubleMap(m);
  }
  
  public static TCharFloatMap unmodifiableMap(TCharFloatMap m) {
    return (TCharFloatMap)new TUnmodifiableCharFloatMap(m);
  }
  
  public static TCharIntMap unmodifiableMap(TCharIntMap m) {
    return (TCharIntMap)new TUnmodifiableCharIntMap(m);
  }
  
  public static TCharLongMap unmodifiableMap(TCharLongMap m) {
    return (TCharLongMap)new TUnmodifiableCharLongMap(m);
  }
  
  public static TCharByteMap unmodifiableMap(TCharByteMap m) {
    return (TCharByteMap)new TUnmodifiableCharByteMap(m);
  }
  
  public static TCharShortMap unmodifiableMap(TCharShortMap m) {
    return (TCharShortMap)new TUnmodifiableCharShortMap(m);
  }
  
  public static TCharCharMap unmodifiableMap(TCharCharMap m) {
    return (TCharCharMap)new TUnmodifiableCharCharMap(m);
  }
  
  public static <V> TDoubleObjectMap<V> unmodifiableMap(TDoubleObjectMap<V> m) {
    return (TDoubleObjectMap<V>)new TUnmodifiableDoubleObjectMap(m);
  }
  
  public static <V> TFloatObjectMap<V> unmodifiableMap(TFloatObjectMap<V> m) {
    return (TFloatObjectMap<V>)new TUnmodifiableFloatObjectMap(m);
  }
  
  public static <V> TIntObjectMap<V> unmodifiableMap(TIntObjectMap<V> m) {
    return (TIntObjectMap<V>)new TUnmodifiableIntObjectMap(m);
  }
  
  public static <V> TLongObjectMap<V> unmodifiableMap(TLongObjectMap<V> m) {
    return (TLongObjectMap<V>)new TUnmodifiableLongObjectMap(m);
  }
  
  public static <V> TByteObjectMap<V> unmodifiableMap(TByteObjectMap<V> m) {
    return (TByteObjectMap<V>)new TUnmodifiableByteObjectMap(m);
  }
  
  public static <V> TShortObjectMap<V> unmodifiableMap(TShortObjectMap<V> m) {
    return (TShortObjectMap<V>)new TUnmodifiableShortObjectMap(m);
  }
  
  public static <V> TCharObjectMap<V> unmodifiableMap(TCharObjectMap<V> m) {
    return (TCharObjectMap<V>)new TUnmodifiableCharObjectMap(m);
  }
  
  public static <K> TObjectDoubleMap<K> unmodifiableMap(TObjectDoubleMap<K> m) {
    return (TObjectDoubleMap<K>)new TUnmodifiableObjectDoubleMap(m);
  }
  
  public static <K> TObjectFloatMap<K> unmodifiableMap(TObjectFloatMap<K> m) {
    return (TObjectFloatMap<K>)new TUnmodifiableObjectFloatMap(m);
  }
  
  public static <K> TObjectIntMap<K> unmodifiableMap(TObjectIntMap<K> m) {
    return (TObjectIntMap<K>)new TUnmodifiableObjectIntMap(m);
  }
  
  public static <K> TObjectLongMap<K> unmodifiableMap(TObjectLongMap<K> m) {
    return (TObjectLongMap<K>)new TUnmodifiableObjectLongMap(m);
  }
  
  public static <K> TObjectByteMap<K> unmodifiableMap(TObjectByteMap<K> m) {
    return (TObjectByteMap<K>)new TUnmodifiableObjectByteMap(m);
  }
  
  public static <K> TObjectShortMap<K> unmodifiableMap(TObjectShortMap<K> m) {
    return (TObjectShortMap<K>)new TUnmodifiableObjectShortMap(m);
  }
  
  public static <K> TObjectCharMap<K> unmodifiableMap(TObjectCharMap<K> m) {
    return (TObjectCharMap<K>)new TUnmodifiableObjectCharMap(m);
  }
  
  public static TDoubleCollection synchronizedCollection(TDoubleCollection c) {
    return (TDoubleCollection)new TSynchronizedDoubleCollection(c);
  }
  
  static TDoubleCollection synchronizedCollection(TDoubleCollection c, Object mutex) {
    return (TDoubleCollection)new TSynchronizedDoubleCollection(c, mutex);
  }
  
  public static TFloatCollection synchronizedCollection(TFloatCollection c) {
    return (TFloatCollection)new TSynchronizedFloatCollection(c);
  }
  
  static TFloatCollection synchronizedCollection(TFloatCollection c, Object mutex) {
    return (TFloatCollection)new TSynchronizedFloatCollection(c, mutex);
  }
  
  public static TIntCollection synchronizedCollection(TIntCollection c) {
    return (TIntCollection)new TSynchronizedIntCollection(c);
  }
  
  static TIntCollection synchronizedCollection(TIntCollection c, Object mutex) {
    return (TIntCollection)new TSynchronizedIntCollection(c, mutex);
  }
  
  public static TLongCollection synchronizedCollection(TLongCollection c) {
    return (TLongCollection)new TSynchronizedLongCollection(c);
  }
  
  static TLongCollection synchronizedCollection(TLongCollection c, Object mutex) {
    return (TLongCollection)new TSynchronizedLongCollection(c, mutex);
  }
  
  public static TByteCollection synchronizedCollection(TByteCollection c) {
    return (TByteCollection)new TSynchronizedByteCollection(c);
  }
  
  static TByteCollection synchronizedCollection(TByteCollection c, Object mutex) {
    return (TByteCollection)new TSynchronizedByteCollection(c, mutex);
  }
  
  public static TShortCollection synchronizedCollection(TShortCollection c) {
    return (TShortCollection)new TSynchronizedShortCollection(c);
  }
  
  static TShortCollection synchronizedCollection(TShortCollection c, Object mutex) {
    return (TShortCollection)new TSynchronizedShortCollection(c, mutex);
  }
  
  public static TCharCollection synchronizedCollection(TCharCollection c) {
    return (TCharCollection)new TSynchronizedCharCollection(c);
  }
  
  static TCharCollection synchronizedCollection(TCharCollection c, Object mutex) {
    return (TCharCollection)new TSynchronizedCharCollection(c, mutex);
  }
  
  public static TDoubleSet synchronizedSet(TDoubleSet s) {
    return (TDoubleSet)new TSynchronizedDoubleSet(s);
  }
  
  static TDoubleSet synchronizedSet(TDoubleSet s, Object mutex) {
    return (TDoubleSet)new TSynchronizedDoubleSet(s, mutex);
  }
  
  public static TFloatSet synchronizedSet(TFloatSet s) {
    return (TFloatSet)new TSynchronizedFloatSet(s);
  }
  
  static TFloatSet synchronizedSet(TFloatSet s, Object mutex) {
    return (TFloatSet)new TSynchronizedFloatSet(s, mutex);
  }
  
  public static TIntSet synchronizedSet(TIntSet s) {
    return (TIntSet)new TSynchronizedIntSet(s);
  }
  
  static TIntSet synchronizedSet(TIntSet s, Object mutex) {
    return (TIntSet)new TSynchronizedIntSet(s, mutex);
  }
  
  public static TLongSet synchronizedSet(TLongSet s) {
    return (TLongSet)new TSynchronizedLongSet(s);
  }
  
  static TLongSet synchronizedSet(TLongSet s, Object mutex) {
    return (TLongSet)new TSynchronizedLongSet(s, mutex);
  }
  
  public static TByteSet synchronizedSet(TByteSet s) {
    return (TByteSet)new TSynchronizedByteSet(s);
  }
  
  static TByteSet synchronizedSet(TByteSet s, Object mutex) {
    return (TByteSet)new TSynchronizedByteSet(s, mutex);
  }
  
  public static TShortSet synchronizedSet(TShortSet s) {
    return (TShortSet)new TSynchronizedShortSet(s);
  }
  
  static TShortSet synchronizedSet(TShortSet s, Object mutex) {
    return (TShortSet)new TSynchronizedShortSet(s, mutex);
  }
  
  public static TCharSet synchronizedSet(TCharSet s) {
    return (TCharSet)new TSynchronizedCharSet(s);
  }
  
  static TCharSet synchronizedSet(TCharSet s, Object mutex) {
    return (TCharSet)new TSynchronizedCharSet(s, mutex);
  }
  
  public static TDoubleList synchronizedList(TDoubleList list) {
    return (list instanceof java.util.RandomAccess) ? (TDoubleList)new TSynchronizedRandomAccessDoubleList(list) : (TDoubleList)new TSynchronizedDoubleList(list);
  }
  
  static TDoubleList synchronizedList(TDoubleList list, Object mutex) {
    return (list instanceof java.util.RandomAccess) ? (TDoubleList)new TSynchronizedRandomAccessDoubleList(list, mutex) : (TDoubleList)new TSynchronizedDoubleList(list, mutex);
  }
  
  public static TFloatList synchronizedList(TFloatList list) {
    return (list instanceof java.util.RandomAccess) ? (TFloatList)new TSynchronizedRandomAccessFloatList(list) : (TFloatList)new TSynchronizedFloatList(list);
  }
  
  static TFloatList synchronizedList(TFloatList list, Object mutex) {
    return (list instanceof java.util.RandomAccess) ? (TFloatList)new TSynchronizedRandomAccessFloatList(list, mutex) : (TFloatList)new TSynchronizedFloatList(list, mutex);
  }
  
  public static TIntList synchronizedList(TIntList list) {
    return (list instanceof java.util.RandomAccess) ? (TIntList)new TSynchronizedRandomAccessIntList(list) : (TIntList)new TSynchronizedIntList(list);
  }
  
  static TIntList synchronizedList(TIntList list, Object mutex) {
    return (list instanceof java.util.RandomAccess) ? (TIntList)new TSynchronizedRandomAccessIntList(list, mutex) : (TIntList)new TSynchronizedIntList(list, mutex);
  }
  
  public static TLongList synchronizedList(TLongList list) {
    return (list instanceof java.util.RandomAccess) ? (TLongList)new TSynchronizedRandomAccessLongList(list) : (TLongList)new TSynchronizedLongList(list);
  }
  
  static TLongList synchronizedList(TLongList list, Object mutex) {
    return (list instanceof java.util.RandomAccess) ? (TLongList)new TSynchronizedRandomAccessLongList(list, mutex) : (TLongList)new TSynchronizedLongList(list, mutex);
  }
  
  public static TByteList synchronizedList(TByteList list) {
    return (list instanceof java.util.RandomAccess) ? (TByteList)new TSynchronizedRandomAccessByteList(list) : (TByteList)new TSynchronizedByteList(list);
  }
  
  static TByteList synchronizedList(TByteList list, Object mutex) {
    return (list instanceof java.util.RandomAccess) ? (TByteList)new TSynchronizedRandomAccessByteList(list, mutex) : (TByteList)new TSynchronizedByteList(list, mutex);
  }
  
  public static TShortList synchronizedList(TShortList list) {
    return (list instanceof java.util.RandomAccess) ? (TShortList)new TSynchronizedRandomAccessShortList(list) : (TShortList)new TSynchronizedShortList(list);
  }
  
  static TShortList synchronizedList(TShortList list, Object mutex) {
    return (list instanceof java.util.RandomAccess) ? (TShortList)new TSynchronizedRandomAccessShortList(list, mutex) : (TShortList)new TSynchronizedShortList(list, mutex);
  }
  
  public static TCharList synchronizedList(TCharList list) {
    return (list instanceof java.util.RandomAccess) ? (TCharList)new TSynchronizedRandomAccessCharList(list) : (TCharList)new TSynchronizedCharList(list);
  }
  
  static TCharList synchronizedList(TCharList list, Object mutex) {
    return (list instanceof java.util.RandomAccess) ? (TCharList)new TSynchronizedRandomAccessCharList(list, mutex) : (TCharList)new TSynchronizedCharList(list, mutex);
  }
  
  public static TDoubleDoubleMap synchronizedMap(TDoubleDoubleMap m) {
    return (TDoubleDoubleMap)new TSynchronizedDoubleDoubleMap(m);
  }
  
  public static TDoubleFloatMap synchronizedMap(TDoubleFloatMap m) {
    return (TDoubleFloatMap)new TSynchronizedDoubleFloatMap(m);
  }
  
  public static TDoubleIntMap synchronizedMap(TDoubleIntMap m) {
    return (TDoubleIntMap)new TSynchronizedDoubleIntMap(m);
  }
  
  public static TDoubleLongMap synchronizedMap(TDoubleLongMap m) {
    return (TDoubleLongMap)new TSynchronizedDoubleLongMap(m);
  }
  
  public static TDoubleByteMap synchronizedMap(TDoubleByteMap m) {
    return (TDoubleByteMap)new TSynchronizedDoubleByteMap(m);
  }
  
  public static TDoubleShortMap synchronizedMap(TDoubleShortMap m) {
    return (TDoubleShortMap)new TSynchronizedDoubleShortMap(m);
  }
  
  public static TDoubleCharMap synchronizedMap(TDoubleCharMap m) {
    return (TDoubleCharMap)new TSynchronizedDoubleCharMap(m);
  }
  
  public static TFloatDoubleMap synchronizedMap(TFloatDoubleMap m) {
    return (TFloatDoubleMap)new TSynchronizedFloatDoubleMap(m);
  }
  
  public static TFloatFloatMap synchronizedMap(TFloatFloatMap m) {
    return (TFloatFloatMap)new TSynchronizedFloatFloatMap(m);
  }
  
  public static TFloatIntMap synchronizedMap(TFloatIntMap m) {
    return (TFloatIntMap)new TSynchronizedFloatIntMap(m);
  }
  
  public static TFloatLongMap synchronizedMap(TFloatLongMap m) {
    return (TFloatLongMap)new TSynchronizedFloatLongMap(m);
  }
  
  public static TFloatByteMap synchronizedMap(TFloatByteMap m) {
    return (TFloatByteMap)new TSynchronizedFloatByteMap(m);
  }
  
  public static TFloatShortMap synchronizedMap(TFloatShortMap m) {
    return (TFloatShortMap)new TSynchronizedFloatShortMap(m);
  }
  
  public static TFloatCharMap synchronizedMap(TFloatCharMap m) {
    return (TFloatCharMap)new TSynchronizedFloatCharMap(m);
  }
  
  public static TIntDoubleMap synchronizedMap(TIntDoubleMap m) {
    return (TIntDoubleMap)new TSynchronizedIntDoubleMap(m);
  }
  
  public static TIntFloatMap synchronizedMap(TIntFloatMap m) {
    return (TIntFloatMap)new TSynchronizedIntFloatMap(m);
  }
  
  public static TIntIntMap synchronizedMap(TIntIntMap m) {
    return (TIntIntMap)new TSynchronizedIntIntMap(m);
  }
  
  public static TIntLongMap synchronizedMap(TIntLongMap m) {
    return (TIntLongMap)new TSynchronizedIntLongMap(m);
  }
  
  public static TIntByteMap synchronizedMap(TIntByteMap m) {
    return (TIntByteMap)new TSynchronizedIntByteMap(m);
  }
  
  public static TIntShortMap synchronizedMap(TIntShortMap m) {
    return (TIntShortMap)new TSynchronizedIntShortMap(m);
  }
  
  public static TIntCharMap synchronizedMap(TIntCharMap m) {
    return (TIntCharMap)new TSynchronizedIntCharMap(m);
  }
  
  public static TLongDoubleMap synchronizedMap(TLongDoubleMap m) {
    return (TLongDoubleMap)new TSynchronizedLongDoubleMap(m);
  }
  
  public static TLongFloatMap synchronizedMap(TLongFloatMap m) {
    return (TLongFloatMap)new TSynchronizedLongFloatMap(m);
  }
  
  public static TLongIntMap synchronizedMap(TLongIntMap m) {
    return (TLongIntMap)new TSynchronizedLongIntMap(m);
  }
  
  public static TLongLongMap synchronizedMap(TLongLongMap m) {
    return (TLongLongMap)new TSynchronizedLongLongMap(m);
  }
  
  public static TLongByteMap synchronizedMap(TLongByteMap m) {
    return (TLongByteMap)new TSynchronizedLongByteMap(m);
  }
  
  public static TLongShortMap synchronizedMap(TLongShortMap m) {
    return (TLongShortMap)new TSynchronizedLongShortMap(m);
  }
  
  public static TLongCharMap synchronizedMap(TLongCharMap m) {
    return (TLongCharMap)new TSynchronizedLongCharMap(m);
  }
  
  public static TByteDoubleMap synchronizedMap(TByteDoubleMap m) {
    return (TByteDoubleMap)new TSynchronizedByteDoubleMap(m);
  }
  
  public static TByteFloatMap synchronizedMap(TByteFloatMap m) {
    return (TByteFloatMap)new TSynchronizedByteFloatMap(m);
  }
  
  public static TByteIntMap synchronizedMap(TByteIntMap m) {
    return (TByteIntMap)new TSynchronizedByteIntMap(m);
  }
  
  public static TByteLongMap synchronizedMap(TByteLongMap m) {
    return (TByteLongMap)new TSynchronizedByteLongMap(m);
  }
  
  public static TByteByteMap synchronizedMap(TByteByteMap m) {
    return (TByteByteMap)new TSynchronizedByteByteMap(m);
  }
  
  public static TByteShortMap synchronizedMap(TByteShortMap m) {
    return (TByteShortMap)new TSynchronizedByteShortMap(m);
  }
  
  public static TByteCharMap synchronizedMap(TByteCharMap m) {
    return (TByteCharMap)new TSynchronizedByteCharMap(m);
  }
  
  public static TShortDoubleMap synchronizedMap(TShortDoubleMap m) {
    return (TShortDoubleMap)new TSynchronizedShortDoubleMap(m);
  }
  
  public static TShortFloatMap synchronizedMap(TShortFloatMap m) {
    return (TShortFloatMap)new TSynchronizedShortFloatMap(m);
  }
  
  public static TShortIntMap synchronizedMap(TShortIntMap m) {
    return (TShortIntMap)new TSynchronizedShortIntMap(m);
  }
  
  public static TShortLongMap synchronizedMap(TShortLongMap m) {
    return (TShortLongMap)new TSynchronizedShortLongMap(m);
  }
  
  public static TShortByteMap synchronizedMap(TShortByteMap m) {
    return (TShortByteMap)new TSynchronizedShortByteMap(m);
  }
  
  public static TShortShortMap synchronizedMap(TShortShortMap m) {
    return (TShortShortMap)new TSynchronizedShortShortMap(m);
  }
  
  public static TShortCharMap synchronizedMap(TShortCharMap m) {
    return (TShortCharMap)new TSynchronizedShortCharMap(m);
  }
  
  public static TCharDoubleMap synchronizedMap(TCharDoubleMap m) {
    return (TCharDoubleMap)new TSynchronizedCharDoubleMap(m);
  }
  
  public static TCharFloatMap synchronizedMap(TCharFloatMap m) {
    return (TCharFloatMap)new TSynchronizedCharFloatMap(m);
  }
  
  public static TCharIntMap synchronizedMap(TCharIntMap m) {
    return (TCharIntMap)new TSynchronizedCharIntMap(m);
  }
  
  public static TCharLongMap synchronizedMap(TCharLongMap m) {
    return (TCharLongMap)new TSynchronizedCharLongMap(m);
  }
  
  public static TCharByteMap synchronizedMap(TCharByteMap m) {
    return (TCharByteMap)new TSynchronizedCharByteMap(m);
  }
  
  public static TCharShortMap synchronizedMap(TCharShortMap m) {
    return (TCharShortMap)new TSynchronizedCharShortMap(m);
  }
  
  public static TCharCharMap synchronizedMap(TCharCharMap m) {
    return (TCharCharMap)new TSynchronizedCharCharMap(m);
  }
  
  public static <V> TDoubleObjectMap<V> synchronizedMap(TDoubleObjectMap<V> m) {
    return (TDoubleObjectMap<V>)new TSynchronizedDoubleObjectMap(m);
  }
  
  public static <V> TFloatObjectMap<V> synchronizedMap(TFloatObjectMap<V> m) {
    return (TFloatObjectMap<V>)new TSynchronizedFloatObjectMap(m);
  }
  
  public static <V> TIntObjectMap<V> synchronizedMap(TIntObjectMap<V> m) {
    return (TIntObjectMap<V>)new TSynchronizedIntObjectMap(m);
  }
  
  public static <V> TLongObjectMap<V> synchronizedMap(TLongObjectMap<V> m) {
    return (TLongObjectMap<V>)new TSynchronizedLongObjectMap(m);
  }
  
  public static <V> TByteObjectMap<V> synchronizedMap(TByteObjectMap<V> m) {
    return (TByteObjectMap<V>)new TSynchronizedByteObjectMap(m);
  }
  
  public static <V> TShortObjectMap<V> synchronizedMap(TShortObjectMap<V> m) {
    return (TShortObjectMap<V>)new TSynchronizedShortObjectMap(m);
  }
  
  public static <V> TCharObjectMap<V> synchronizedMap(TCharObjectMap<V> m) {
    return (TCharObjectMap<V>)new TSynchronizedCharObjectMap(m);
  }
  
  public static <K> TObjectDoubleMap<K> synchronizedMap(TObjectDoubleMap<K> m) {
    return (TObjectDoubleMap<K>)new TSynchronizedObjectDoubleMap(m);
  }
  
  public static <K> TObjectFloatMap<K> synchronizedMap(TObjectFloatMap<K> m) {
    return (TObjectFloatMap<K>)new TSynchronizedObjectFloatMap(m);
  }
  
  public static <K> TObjectIntMap<K> synchronizedMap(TObjectIntMap<K> m) {
    return (TObjectIntMap<K>)new TSynchronizedObjectIntMap(m);
  }
  
  public static <K> TObjectLongMap<K> synchronizedMap(TObjectLongMap<K> m) {
    return (TObjectLongMap<K>)new TSynchronizedObjectLongMap(m);
  }
  
  public static <K> TObjectByteMap<K> synchronizedMap(TObjectByteMap<K> m) {
    return (TObjectByteMap<K>)new TSynchronizedObjectByteMap(m);
  }
  
  public static <K> TObjectShortMap<K> synchronizedMap(TObjectShortMap<K> m) {
    return (TObjectShortMap<K>)new TSynchronizedObjectShortMap(m);
  }
  
  public static <K> TObjectCharMap<K> synchronizedMap(TObjectCharMap<K> m) {
    return (TObjectCharMap<K>)new TSynchronizedObjectCharMap(m);
  }
}
