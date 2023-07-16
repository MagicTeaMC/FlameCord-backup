package net.md_5.bungee.compress;

import java.util.function.Supplier;
import net.md_5.bungee.jni.NativeCode;
import net.md_5.bungee.jni.zlib.BungeeZlib;

public class CompressFactory {
  public static final NativeCode<BungeeZlib> zlib = new NativeCode("native-compress", net.md_5.bungee.jni.zlib.JavaZlib::new, net.md_5.bungee.jni.zlib.NativeZlib::new);
}
