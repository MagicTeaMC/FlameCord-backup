package gnu.trove.map;

import gnu.trove.TIntCollection;
import gnu.trove.function.TIntFunction;
import gnu.trove.iterator.TDoubleIntIterator;
import gnu.trove.procedure.TDoubleIntProcedure;
import gnu.trove.procedure.TDoubleProcedure;
import gnu.trove.procedure.TIntProcedure;
import gnu.trove.set.TDoubleSet;
import java.util.Map;

public interface TDoubleIntMap {
  double getNoEntryKey();
  
  int getNoEntryValue();
  
  int put(double paramDouble, int paramInt);
  
  int putIfAbsent(double paramDouble, int paramInt);
  
  void putAll(Map<? extends Double, ? extends Integer> paramMap);
  
  void putAll(TDoubleIntMap paramTDoubleIntMap);
  
  int get(double paramDouble);
  
  void clear();
  
  boolean isEmpty();
  
  int remove(double paramDouble);
  
  int size();
  
  TDoubleSet keySet();
  
  double[] keys();
  
  double[] keys(double[] paramArrayOfdouble);
  
  TIntCollection valueCollection();
  
  int[] values();
  
  int[] values(int[] paramArrayOfint);
  
  boolean containsValue(int paramInt);
  
  boolean containsKey(double paramDouble);
  
  TDoubleIntIterator iterator();
  
  boolean forEachKey(TDoubleProcedure paramTDoubleProcedure);
  
  boolean forEachValue(TIntProcedure paramTIntProcedure);
  
  boolean forEachEntry(TDoubleIntProcedure paramTDoubleIntProcedure);
  
  void transformValues(TIntFunction paramTIntFunction);
  
  boolean retainEntries(TDoubleIntProcedure paramTDoubleIntProcedure);
  
  boolean increment(double paramDouble);
  
  boolean adjustValue(double paramDouble, int paramInt);
  
  int adjustOrPutValue(double paramDouble, int paramInt1, int paramInt2);
}
