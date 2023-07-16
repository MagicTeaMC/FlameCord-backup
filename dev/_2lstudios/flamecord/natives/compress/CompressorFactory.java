package dev._2lstudios.flamecord.natives.compress;

public interface CompressorFactory {
  String getName();
  
  boolean isCorrectEnvironment();
  
  String getNativePath();
  
  Compressor create(int paramInt);
}
