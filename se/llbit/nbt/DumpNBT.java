package se.llbit.nbt;

import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.zip.GZIPInputStream;

public class DumpNBT {
  public static void main(String[] args) throws IOException {
    if (args.length > 0) {
      try(FileInputStream fin = new FileInputStream(args[0]); 
          GZIPInputStream gzin = new GZIPInputStream(fin); 
          DataInputStream in = new DataInputStream(gzin)) {
        Tag root = NamedTag.read(in);
        System.out.println(root.dumpTree());
      } 
    } else {
      System.err.println("Missing filename argument.");
      System.err.println("Usage: DumpNBT <GZipped NBT file>");
    } 
  }
}
