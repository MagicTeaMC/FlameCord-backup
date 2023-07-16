package gnu.trove.map;

import gnu.trove.function.TObjectFunction;
import gnu.trove.procedure.TObjectObjectProcedure;
import gnu.trove.procedure.TObjectProcedure;
import java.util.Map;

public interface TMap<K, V> extends Map<K, V> {
  V putIfAbsent(K paramK, V paramV);
  
  boolean forEachKey(TObjectProcedure<? super K> paramTObjectProcedure);
  
  boolean forEachValue(TObjectProcedure<? super V> paramTObjectProcedure);
  
  boolean forEachEntry(TObjectObjectProcedure<? super K, ? super V> paramTObjectObjectProcedure);
  
  boolean retainEntries(TObjectObjectProcedure<? super K, ? super V> paramTObjectObjectProcedure);
  
  void transformValues(TObjectFunction<V, V> paramTObjectFunction);
}
