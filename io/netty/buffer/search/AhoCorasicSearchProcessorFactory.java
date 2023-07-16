package io.netty.buffer.search;

import io.netty.util.internal.PlatformDependent;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Queue;

public class AhoCorasicSearchProcessorFactory extends AbstractMultiSearchProcessorFactory {
  private final int[] jumpTable;
  
  private final int[] matchForNeedleId;
  
  static final int BITS_PER_SYMBOL = 8;
  
  static final int ALPHABET_SIZE = 256;
  
  private static class Context {
    int[] jumpTable;
    
    int[] matchForNeedleId;
    
    private Context() {}
  }
  
  public static class Processor implements MultiSearchProcessor {
    private final int[] jumpTable;
    
    private final int[] matchForNeedleId;
    
    private long currentPosition;
    
    Processor(int[] jumpTable, int[] matchForNeedleId) {
      this.jumpTable = jumpTable;
      this.matchForNeedleId = matchForNeedleId;
    }
    
    public boolean process(byte value) {
      this.currentPosition = PlatformDependent.getInt(this.jumpTable, this.currentPosition | value & 0xFFL);
      if (this.currentPosition < 0L) {
        this.currentPosition = -this.currentPosition;
        return false;
      } 
      return true;
    }
    
    public int getFoundNeedleId() {
      return this.matchForNeedleId[(int)this.currentPosition >> 8];
    }
    
    public void reset() {
      this.currentPosition = 0L;
    }
  }
  
  AhoCorasicSearchProcessorFactory(byte[]... needles) {
    for (byte[] needle : needles) {
      if (needle.length == 0)
        throw new IllegalArgumentException("Needle must be non empty"); 
    } 
    Context context = buildTrie(needles);
    this.jumpTable = context.jumpTable;
    this.matchForNeedleId = context.matchForNeedleId;
    linkSuffixes();
    for (int i = 0; i < this.jumpTable.length; i++) {
      if (this.matchForNeedleId[this.jumpTable[i] >> 8] >= 0)
        this.jumpTable[i] = -this.jumpTable[i]; 
    } 
  }
  
  private static Context buildTrie(byte[][] needles) {
    ArrayList<Integer> jumpTableBuilder = new ArrayList<Integer>(256);
    for (int i = 0; i < 256; i++)
      jumpTableBuilder.add(Integer.valueOf(-1)); 
    ArrayList<Integer> matchForBuilder = new ArrayList<Integer>();
    matchForBuilder.add(Integer.valueOf(-1));
    for (int needleId = 0; needleId < needles.length; needleId++) {
      byte[] needle = needles[needleId];
      int currentPosition = 0;
      for (byte ch0 : needle) {
        int ch = ch0 & 0xFF;
        int next = currentPosition + ch;
        if (((Integer)jumpTableBuilder.get(next)).intValue() == -1) {
          jumpTableBuilder.set(next, Integer.valueOf(jumpTableBuilder.size()));
          for (int k = 0; k < 256; k++)
            jumpTableBuilder.add(Integer.valueOf(-1)); 
          matchForBuilder.add(Integer.valueOf(-1));
        } 
        currentPosition = ((Integer)jumpTableBuilder.get(next)).intValue();
      } 
      matchForBuilder.set(currentPosition >> 8, Integer.valueOf(needleId));
    } 
    Context context = new Context();
    context.jumpTable = new int[jumpTableBuilder.size()];
    int j;
    for (j = 0; j < jumpTableBuilder.size(); j++)
      context.jumpTable[j] = ((Integer)jumpTableBuilder.get(j)).intValue(); 
    context.matchForNeedleId = new int[matchForBuilder.size()];
    for (j = 0; j < matchForBuilder.size(); j++)
      context.matchForNeedleId[j] = ((Integer)matchForBuilder.get(j)).intValue(); 
    return context;
  }
  
  private void linkSuffixes() {
    Queue<Integer> queue = new ArrayDeque<Integer>();
    queue.add(Integer.valueOf(0));
    int[] suffixLinks = new int[this.matchForNeedleId.length];
    Arrays.fill(suffixLinks, -1);
    while (!queue.isEmpty()) {
      int v = ((Integer)queue.remove()).intValue();
      int vPosition = v >> 8;
      int u = (suffixLinks[vPosition] == -1) ? 0 : suffixLinks[vPosition];
      if (this.matchForNeedleId[vPosition] == -1)
        this.matchForNeedleId[vPosition] = this.matchForNeedleId[u >> 8]; 
      for (int ch = 0; ch < 256; ch++) {
        int vIndex = v | ch;
        int uIndex = u | ch;
        int jumpV = this.jumpTable[vIndex];
        int jumpU = this.jumpTable[uIndex];
        if (jumpV != -1) {
          suffixLinks[jumpV >> 8] = (v > 0 && jumpU != -1) ? jumpU : 0;
          queue.add(Integer.valueOf(jumpV));
        } else {
          this.jumpTable[vIndex] = (jumpU != -1) ? jumpU : 0;
        } 
      } 
    } 
  }
  
  public Processor newSearchProcessor() {
    return new Processor(this.jumpTable, this.matchForNeedleId);
  }
}
