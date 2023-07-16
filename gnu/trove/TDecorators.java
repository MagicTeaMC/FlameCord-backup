package gnu.trove;

import gnu.trove.decorator.TByteByteMapDecorator;
import gnu.trove.decorator.TByteCharMapDecorator;
import gnu.trove.decorator.TByteDoubleMapDecorator;
import gnu.trove.decorator.TByteFloatMapDecorator;
import gnu.trove.decorator.TByteIntMapDecorator;
import gnu.trove.decorator.TByteListDecorator;
import gnu.trove.decorator.TByteLongMapDecorator;
import gnu.trove.decorator.TByteObjectMapDecorator;
import gnu.trove.decorator.TByteSetDecorator;
import gnu.trove.decorator.TByteShortMapDecorator;
import gnu.trove.decorator.TCharByteMapDecorator;
import gnu.trove.decorator.TCharCharMapDecorator;
import gnu.trove.decorator.TCharDoubleMapDecorator;
import gnu.trove.decorator.TCharFloatMapDecorator;
import gnu.trove.decorator.TCharIntMapDecorator;
import gnu.trove.decorator.TCharListDecorator;
import gnu.trove.decorator.TCharLongMapDecorator;
import gnu.trove.decorator.TCharObjectMapDecorator;
import gnu.trove.decorator.TCharSetDecorator;
import gnu.trove.decorator.TCharShortMapDecorator;
import gnu.trove.decorator.TDoubleByteMapDecorator;
import gnu.trove.decorator.TDoubleCharMapDecorator;
import gnu.trove.decorator.TDoubleDoubleMapDecorator;
import gnu.trove.decorator.TDoubleFloatMapDecorator;
import gnu.trove.decorator.TDoubleIntMapDecorator;
import gnu.trove.decorator.TDoubleListDecorator;
import gnu.trove.decorator.TDoubleLongMapDecorator;
import gnu.trove.decorator.TDoubleObjectMapDecorator;
import gnu.trove.decorator.TDoubleSetDecorator;
import gnu.trove.decorator.TDoubleShortMapDecorator;
import gnu.trove.decorator.TFloatByteMapDecorator;
import gnu.trove.decorator.TFloatCharMapDecorator;
import gnu.trove.decorator.TFloatDoubleMapDecorator;
import gnu.trove.decorator.TFloatFloatMapDecorator;
import gnu.trove.decorator.TFloatIntMapDecorator;
import gnu.trove.decorator.TFloatListDecorator;
import gnu.trove.decorator.TFloatLongMapDecorator;
import gnu.trove.decorator.TFloatObjectMapDecorator;
import gnu.trove.decorator.TFloatSetDecorator;
import gnu.trove.decorator.TFloatShortMapDecorator;
import gnu.trove.decorator.TIntByteMapDecorator;
import gnu.trove.decorator.TIntCharMapDecorator;
import gnu.trove.decorator.TIntDoubleMapDecorator;
import gnu.trove.decorator.TIntFloatMapDecorator;
import gnu.trove.decorator.TIntIntMapDecorator;
import gnu.trove.decorator.TIntListDecorator;
import gnu.trove.decorator.TIntLongMapDecorator;
import gnu.trove.decorator.TIntObjectMapDecorator;
import gnu.trove.decorator.TIntSetDecorator;
import gnu.trove.decorator.TIntShortMapDecorator;
import gnu.trove.decorator.TLongByteMapDecorator;
import gnu.trove.decorator.TLongCharMapDecorator;
import gnu.trove.decorator.TLongDoubleMapDecorator;
import gnu.trove.decorator.TLongFloatMapDecorator;
import gnu.trove.decorator.TLongIntMapDecorator;
import gnu.trove.decorator.TLongListDecorator;
import gnu.trove.decorator.TLongLongMapDecorator;
import gnu.trove.decorator.TLongObjectMapDecorator;
import gnu.trove.decorator.TLongSetDecorator;
import gnu.trove.decorator.TLongShortMapDecorator;
import gnu.trove.decorator.TObjectByteMapDecorator;
import gnu.trove.decorator.TObjectCharMapDecorator;
import gnu.trove.decorator.TObjectDoubleMapDecorator;
import gnu.trove.decorator.TObjectFloatMapDecorator;
import gnu.trove.decorator.TObjectIntMapDecorator;
import gnu.trove.decorator.TObjectLongMapDecorator;
import gnu.trove.decorator.TObjectShortMapDecorator;
import gnu.trove.decorator.TShortByteMapDecorator;
import gnu.trove.decorator.TShortCharMapDecorator;
import gnu.trove.decorator.TShortDoubleMapDecorator;
import gnu.trove.decorator.TShortFloatMapDecorator;
import gnu.trove.decorator.TShortIntMapDecorator;
import gnu.trove.decorator.TShortListDecorator;
import gnu.trove.decorator.TShortLongMapDecorator;
import gnu.trove.decorator.TShortObjectMapDecorator;
import gnu.trove.decorator.TShortSetDecorator;
import gnu.trove.decorator.TShortShortMapDecorator;
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
import java.util.List;
import java.util.Map;
import java.util.Set;

public class TDecorators {
  public static Map<Double, Double> wrap(TDoubleDoubleMap map) {
    return (Map<Double, Double>)new TDoubleDoubleMapDecorator(map);
  }
  
  public static Map<Double, Float> wrap(TDoubleFloatMap map) {
    return (Map<Double, Float>)new TDoubleFloatMapDecorator(map);
  }
  
  public static Map<Double, Integer> wrap(TDoubleIntMap map) {
    return (Map<Double, Integer>)new TDoubleIntMapDecorator(map);
  }
  
  public static Map<Double, Long> wrap(TDoubleLongMap map) {
    return (Map<Double, Long>)new TDoubleLongMapDecorator(map);
  }
  
  public static Map<Double, Byte> wrap(TDoubleByteMap map) {
    return (Map<Double, Byte>)new TDoubleByteMapDecorator(map);
  }
  
  public static Map<Double, Short> wrap(TDoubleShortMap map) {
    return (Map<Double, Short>)new TDoubleShortMapDecorator(map);
  }
  
  public static Map<Double, Character> wrap(TDoubleCharMap map) {
    return (Map<Double, Character>)new TDoubleCharMapDecorator(map);
  }
  
  public static Map<Float, Double> wrap(TFloatDoubleMap map) {
    return (Map<Float, Double>)new TFloatDoubleMapDecorator(map);
  }
  
  public static Map<Float, Float> wrap(TFloatFloatMap map) {
    return (Map<Float, Float>)new TFloatFloatMapDecorator(map);
  }
  
  public static Map<Float, Integer> wrap(TFloatIntMap map) {
    return (Map<Float, Integer>)new TFloatIntMapDecorator(map);
  }
  
  public static Map<Float, Long> wrap(TFloatLongMap map) {
    return (Map<Float, Long>)new TFloatLongMapDecorator(map);
  }
  
  public static Map<Float, Byte> wrap(TFloatByteMap map) {
    return (Map<Float, Byte>)new TFloatByteMapDecorator(map);
  }
  
  public static Map<Float, Short> wrap(TFloatShortMap map) {
    return (Map<Float, Short>)new TFloatShortMapDecorator(map);
  }
  
  public static Map<Float, Character> wrap(TFloatCharMap map) {
    return (Map<Float, Character>)new TFloatCharMapDecorator(map);
  }
  
  public static Map<Integer, Double> wrap(TIntDoubleMap map) {
    return (Map<Integer, Double>)new TIntDoubleMapDecorator(map);
  }
  
  public static Map<Integer, Float> wrap(TIntFloatMap map) {
    return (Map<Integer, Float>)new TIntFloatMapDecorator(map);
  }
  
  public static Map<Integer, Integer> wrap(TIntIntMap map) {
    return (Map<Integer, Integer>)new TIntIntMapDecorator(map);
  }
  
  public static Map<Integer, Long> wrap(TIntLongMap map) {
    return (Map<Integer, Long>)new TIntLongMapDecorator(map);
  }
  
  public static Map<Integer, Byte> wrap(TIntByteMap map) {
    return (Map<Integer, Byte>)new TIntByteMapDecorator(map);
  }
  
  public static Map<Integer, Short> wrap(TIntShortMap map) {
    return (Map<Integer, Short>)new TIntShortMapDecorator(map);
  }
  
  public static Map<Integer, Character> wrap(TIntCharMap map) {
    return (Map<Integer, Character>)new TIntCharMapDecorator(map);
  }
  
  public static Map<Long, Double> wrap(TLongDoubleMap map) {
    return (Map<Long, Double>)new TLongDoubleMapDecorator(map);
  }
  
  public static Map<Long, Float> wrap(TLongFloatMap map) {
    return (Map<Long, Float>)new TLongFloatMapDecorator(map);
  }
  
  public static Map<Long, Integer> wrap(TLongIntMap map) {
    return (Map<Long, Integer>)new TLongIntMapDecorator(map);
  }
  
  public static Map<Long, Long> wrap(TLongLongMap map) {
    return (Map<Long, Long>)new TLongLongMapDecorator(map);
  }
  
  public static Map<Long, Byte> wrap(TLongByteMap map) {
    return (Map<Long, Byte>)new TLongByteMapDecorator(map);
  }
  
  public static Map<Long, Short> wrap(TLongShortMap map) {
    return (Map<Long, Short>)new TLongShortMapDecorator(map);
  }
  
  public static Map<Long, Character> wrap(TLongCharMap map) {
    return (Map<Long, Character>)new TLongCharMapDecorator(map);
  }
  
  public static Map<Byte, Double> wrap(TByteDoubleMap map) {
    return (Map<Byte, Double>)new TByteDoubleMapDecorator(map);
  }
  
  public static Map<Byte, Float> wrap(TByteFloatMap map) {
    return (Map<Byte, Float>)new TByteFloatMapDecorator(map);
  }
  
  public static Map<Byte, Integer> wrap(TByteIntMap map) {
    return (Map<Byte, Integer>)new TByteIntMapDecorator(map);
  }
  
  public static Map<Byte, Long> wrap(TByteLongMap map) {
    return (Map<Byte, Long>)new TByteLongMapDecorator(map);
  }
  
  public static Map<Byte, Byte> wrap(TByteByteMap map) {
    return (Map<Byte, Byte>)new TByteByteMapDecorator(map);
  }
  
  public static Map<Byte, Short> wrap(TByteShortMap map) {
    return (Map<Byte, Short>)new TByteShortMapDecorator(map);
  }
  
  public static Map<Byte, Character> wrap(TByteCharMap map) {
    return (Map<Byte, Character>)new TByteCharMapDecorator(map);
  }
  
  public static Map<Short, Double> wrap(TShortDoubleMap map) {
    return (Map<Short, Double>)new TShortDoubleMapDecorator(map);
  }
  
  public static Map<Short, Float> wrap(TShortFloatMap map) {
    return (Map<Short, Float>)new TShortFloatMapDecorator(map);
  }
  
  public static Map<Short, Integer> wrap(TShortIntMap map) {
    return (Map<Short, Integer>)new TShortIntMapDecorator(map);
  }
  
  public static Map<Short, Long> wrap(TShortLongMap map) {
    return (Map<Short, Long>)new TShortLongMapDecorator(map);
  }
  
  public static Map<Short, Byte> wrap(TShortByteMap map) {
    return (Map<Short, Byte>)new TShortByteMapDecorator(map);
  }
  
  public static Map<Short, Short> wrap(TShortShortMap map) {
    return (Map<Short, Short>)new TShortShortMapDecorator(map);
  }
  
  public static Map<Short, Character> wrap(TShortCharMap map) {
    return (Map<Short, Character>)new TShortCharMapDecorator(map);
  }
  
  public static Map<Character, Double> wrap(TCharDoubleMap map) {
    return (Map<Character, Double>)new TCharDoubleMapDecorator(map);
  }
  
  public static Map<Character, Float> wrap(TCharFloatMap map) {
    return (Map<Character, Float>)new TCharFloatMapDecorator(map);
  }
  
  public static Map<Character, Integer> wrap(TCharIntMap map) {
    return (Map<Character, Integer>)new TCharIntMapDecorator(map);
  }
  
  public static Map<Character, Long> wrap(TCharLongMap map) {
    return (Map<Character, Long>)new TCharLongMapDecorator(map);
  }
  
  public static Map<Character, Byte> wrap(TCharByteMap map) {
    return (Map<Character, Byte>)new TCharByteMapDecorator(map);
  }
  
  public static Map<Character, Short> wrap(TCharShortMap map) {
    return (Map<Character, Short>)new TCharShortMapDecorator(map);
  }
  
  public static Map<Character, Character> wrap(TCharCharMap map) {
    return (Map<Character, Character>)new TCharCharMapDecorator(map);
  }
  
  public static <T> Map<T, Double> wrap(TObjectDoubleMap<T> map) {
    return (Map<T, Double>)new TObjectDoubleMapDecorator(map);
  }
  
  public static <T> Map<T, Float> wrap(TObjectFloatMap<T> map) {
    return (Map<T, Float>)new TObjectFloatMapDecorator(map);
  }
  
  public static <T> Map<T, Integer> wrap(TObjectIntMap<T> map) {
    return (Map<T, Integer>)new TObjectIntMapDecorator(map);
  }
  
  public static <T> Map<T, Long> wrap(TObjectLongMap<T> map) {
    return (Map<T, Long>)new TObjectLongMapDecorator(map);
  }
  
  public static <T> Map<T, Byte> wrap(TObjectByteMap<T> map) {
    return (Map<T, Byte>)new TObjectByteMapDecorator(map);
  }
  
  public static <T> Map<T, Short> wrap(TObjectShortMap<T> map) {
    return (Map<T, Short>)new TObjectShortMapDecorator(map);
  }
  
  public static <T> Map<T, Character> wrap(TObjectCharMap<T> map) {
    return (Map<T, Character>)new TObjectCharMapDecorator(map);
  }
  
  public static <T> Map<Double, T> wrap(TDoubleObjectMap<T> map) {
    return (Map<Double, T>)new TDoubleObjectMapDecorator(map);
  }
  
  public static <T> Map<Float, T> wrap(TFloatObjectMap<T> map) {
    return (Map<Float, T>)new TFloatObjectMapDecorator(map);
  }
  
  public static <T> Map<Integer, T> wrap(TIntObjectMap<T> map) {
    return (Map<Integer, T>)new TIntObjectMapDecorator(map);
  }
  
  public static <T> Map<Long, T> wrap(TLongObjectMap<T> map) {
    return (Map<Long, T>)new TLongObjectMapDecorator(map);
  }
  
  public static <T> Map<Byte, T> wrap(TByteObjectMap<T> map) {
    return (Map<Byte, T>)new TByteObjectMapDecorator(map);
  }
  
  public static <T> Map<Short, T> wrap(TShortObjectMap<T> map) {
    return (Map<Short, T>)new TShortObjectMapDecorator(map);
  }
  
  public static <T> Map<Character, T> wrap(TCharObjectMap<T> map) {
    return (Map<Character, T>)new TCharObjectMapDecorator(map);
  }
  
  public static Set<Double> wrap(TDoubleSet set) {
    return (Set<Double>)new TDoubleSetDecorator(set);
  }
  
  public static Set<Float> wrap(TFloatSet set) {
    return (Set<Float>)new TFloatSetDecorator(set);
  }
  
  public static Set<Integer> wrap(TIntSet set) {
    return (Set<Integer>)new TIntSetDecorator(set);
  }
  
  public static Set<Long> wrap(TLongSet set) {
    return (Set<Long>)new TLongSetDecorator(set);
  }
  
  public static Set<Byte> wrap(TByteSet set) {
    return (Set<Byte>)new TByteSetDecorator(set);
  }
  
  public static Set<Short> wrap(TShortSet set) {
    return (Set<Short>)new TShortSetDecorator(set);
  }
  
  public static Set<Character> wrap(TCharSet set) {
    return (Set<Character>)new TCharSetDecorator(set);
  }
  
  public static List<Double> wrap(TDoubleList list) {
    return (List<Double>)new TDoubleListDecorator(list);
  }
  
  public static List<Float> wrap(TFloatList list) {
    return (List<Float>)new TFloatListDecorator(list);
  }
  
  public static List<Integer> wrap(TIntList list) {
    return (List<Integer>)new TIntListDecorator(list);
  }
  
  public static List<Long> wrap(TLongList list) {
    return (List<Long>)new TLongListDecorator(list);
  }
  
  public static List<Byte> wrap(TByteList list) {
    return (List<Byte>)new TByteListDecorator(list);
  }
  
  public static List<Short> wrap(TShortList list) {
    return (List<Short>)new TShortListDecorator(list);
  }
  
  public static List<Character> wrap(TCharList list) {
    return (List<Character>)new TCharListDecorator(list);
  }
}
