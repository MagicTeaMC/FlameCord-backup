package com.mysql.cj.sasl;

import com.mysql.cj.exceptions.ExceptionFactory;
import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.spec.KeySpec;
import javax.crypto.Mac;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import javax.security.sasl.SaslException;

public class ScramSha256SaslClient extends ScramShaSaslClient {
  public static final String IANA_MECHANISM_NAME = "SCRAM-SHA-256";
  
  public static final String MECHANISM_NAME = "MYSQLCJ-SCRAM-SHA-256";
  
  private static final String SHA256_ALGORITHM = "SHA-256";
  
  private static final String HMAC_SHA256_ALGORITHM = "HmacSHA256";
  
  private static final String PBKCF2_HMAC_SHA256_ALGORITHM = "PBKDF2WithHmacSHA256";
  
  private static final int SHA256_HASH_LENGTH = 32;
  
  public ScramSha256SaslClient(String authorizationId, String authenticationId, String password) throws SaslException {
    super(authorizationId, authenticationId, password);
  }
  
  String getIanaMechanismName() {
    return "SCRAM-SHA-256";
  }
  
  public String getMechanismName() {
    return "MYSQLCJ-SCRAM-SHA-256";
  }
  
  byte[] h(byte[] str) {
    try {
      MessageDigest sha256 = MessageDigest.getInstance("SHA-256");
      return sha256.digest(str);
    } catch (NoSuchAlgorithmException e) {
      throw ExceptionFactory.createException("Failed computing authentication hashes", e);
    } 
  }
  
  byte[] hmac(byte[] key, byte[] str) {
    try {
      Mac hmacSha256 = Mac.getInstance("HmacSHA256");
      hmacSha256.init(new SecretKeySpec(key, "HmacSHA256"));
      return hmacSha256.doFinal(str);
    } catch (NoSuchAlgorithmException|java.security.InvalidKeyException e) {
      throw ExceptionFactory.createException("Failed computing authentication hashes", e);
    } 
  }
  
  byte[] hi(String str, byte[] salt, int iterations) {
    KeySpec spec = new PBEKeySpec(str.toCharArray(), salt, iterations, 256);
    try {
      SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
      return factory.generateSecret(spec).getEncoded();
    } catch (NoSuchAlgorithmException|java.security.spec.InvalidKeySpecException e) {
      throw ExceptionFactory.createException(e.getMessage());
    } 
  }
}
