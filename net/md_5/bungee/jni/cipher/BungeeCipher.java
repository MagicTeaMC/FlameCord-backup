package net.md_5.bungee.jni.cipher;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import java.security.GeneralSecurityException;
import javax.crypto.SecretKey;

public interface BungeeCipher {
  void init(boolean paramBoolean, SecretKey paramSecretKey) throws GeneralSecurityException;
  
  void free();
  
  void cipher(ByteBuf paramByteBuf1, ByteBuf paramByteBuf2) throws GeneralSecurityException;
  
  ByteBuf cipher(ChannelHandlerContext paramChannelHandlerContext, ByteBuf paramByteBuf) throws GeneralSecurityException;
}
