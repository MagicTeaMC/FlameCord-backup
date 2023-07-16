package com.mysql.cj.sasl;

import com.mysql.cj.util.SaslPrep;
import com.mysql.cj.util.StringUtils;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import javax.security.sasl.SaslClient;
import javax.security.sasl.SaslException;

public abstract class ScramShaSaslClient implements SaslClient {
  protected static final int MINIMUM_ITERATIONS = 4096;
  
  protected static final String GS2_CBIND_FLAG = "n";
  
  protected enum ScramExchangeStage {
    TERMINATED(null),
    SERVER_FINAL((String)TERMINATED),
    SERVER_FIRST_CLIENT_FINAL((String)SERVER_FINAL),
    CLIENT_FIRST((String)SERVER_FIRST_CLIENT_FINAL);
    
    private ScramExchangeStage next;
    
    ScramExchangeStage(ScramExchangeStage next) {
      this.next = next;
    }
    
    public ScramExchangeStage getNext() {
      return (this.next == null) ? this : this.next;
    }
  }
  
  protected static final byte[] CLIENT_KEY = "Client Key".getBytes();
  
  protected static final byte[] SERVER_KEY = "Server Key".getBytes();
  
  protected String authorizationId;
  
  protected String authenticationId;
  
  protected String password;
  
  protected ScramExchangeStage scramStage = ScramExchangeStage.CLIENT_FIRST;
  
  protected String cNonce;
  
  protected String gs2Header;
  
  protected String clientFirstMessageBare;
  
  protected byte[] serverSignature;
  
  public ScramShaSaslClient(String authorizationId, String authenticationId, String password) throws SaslException {
    this.authorizationId = StringUtils.isNullOrEmpty(authorizationId) ? "" : authorizationId;
    this.authenticationId = StringUtils.isNullOrEmpty(authenticationId) ? this.authorizationId : authenticationId;
    if (StringUtils.isNullOrEmpty(this.authenticationId))
      throw new SaslException("The authenticationId cannot be null or empty."); 
    this.password = StringUtils.isNullOrEmpty(password) ? "" : password;
    this.scramStage = ScramExchangeStage.CLIENT_FIRST;
  }
  
  abstract String getIanaMechanismName();
  
  public boolean hasInitialResponse() {
    return true;
  }
  
  public byte[] evaluateChallenge(byte[] challenge) throws SaslException {
    try {
      byte[] arrayOfByte1;
      String serverFirstMessage;
      Map<String, String> serverFirstAttributes;
      String sNonce;
      byte[] salt;
      int iterations;
      String clientFinalMessageWithoutProof;
      byte[] saltedPassword, clientKey, storedKey;
      String authMessage;
      byte[] clientSignature, clientProof;
      String clientFinalMessage;
      byte[] serverKey, arrayOfByte2;
      String serverFinalMessage;
      Map<String, String> serverFinalAttributes;
      byte[] verifier;
      switch (this.scramStage) {
        case CLIENT_FIRST:
          this.gs2Header = "n," + (StringUtils.isNullOrEmpty(this.authorizationId) ? "" : ("a=" + prepUserName(this.authorizationId))) + ",";
          this.cNonce = generateRandomPrintableAsciiString(32);
          this.clientFirstMessageBare = "n=" + prepUserName(this.authenticationId) + ",r=" + this.cNonce;
          clientFirstMessage = this.gs2Header + this.clientFirstMessageBare;
          return StringUtils.getBytes(clientFirstMessage, "UTF-8");
        case SERVER_FIRST_CLIENT_FINAL:
          serverFirstMessage = StringUtils.toString(challenge, StandardCharsets.UTF_8);
          serverFirstAttributes = parseChallenge(serverFirstMessage);
          if (!serverFirstAttributes.containsKey("r") || !serverFirstAttributes.containsKey("s") || !serverFirstAttributes.containsKey("i"))
            throw new SaslException("Missing required SCRAM attribute from server first message."); 
          sNonce = serverFirstAttributes.get("r");
          if (!sNonce.startsWith(this.cNonce))
            throw new SaslException("Invalid server nonce for " + getIanaMechanismName() + " authentication."); 
          salt = Base64.getDecoder().decode(serverFirstAttributes.get("s"));
          iterations = Integer.parseInt(serverFirstAttributes.get("i"));
          if (iterations < 4096)
            throw new SaslException("Announced " + getIanaMechanismName() + " iteration count is too low."); 
          clientFinalMessageWithoutProof = "c=" + Base64.getEncoder().encodeToString(StringUtils.getBytes(this.gs2Header, "UTF-8")) + ",r=" + sNonce;
          saltedPassword = hi(SaslPrep.prepare(this.password, SaslPrep.StringType.STORED), salt, iterations);
          clientKey = hmac(saltedPassword, CLIENT_KEY);
          storedKey = h(clientKey);
          authMessage = this.clientFirstMessageBare + "," + serverFirstMessage + "," + clientFinalMessageWithoutProof;
          clientSignature = hmac(storedKey, StringUtils.getBytes(authMessage, "UTF-8"));
          clientProof = (byte[])clientKey.clone();
          xorInPlace(clientProof, clientSignature);
          clientFinalMessage = clientFinalMessageWithoutProof + ",p=" + Base64.getEncoder().encodeToString(clientProof);
          serverKey = hmac(saltedPassword, SERVER_KEY);
          this.serverSignature = hmac(serverKey, StringUtils.getBytes(authMessage, "UTF-8"));
          return StringUtils.getBytes(clientFinalMessage, "UTF-8");
        case SERVER_FINAL:
          serverFinalMessage = StringUtils.toString(challenge, "UTF-8");
          serverFinalAttributes = parseChallenge(serverFinalMessage);
          if (serverFinalAttributes.containsKey("e"))
            throw new SaslException("Authentication failed due to server error '" + (String)serverFinalAttributes.get("e") + "'."); 
          if (!serverFinalAttributes.containsKey("v"))
            throw new SaslException("Missing required SCRAM attribute from server final message."); 
          verifier = Base64.getDecoder().decode(serverFinalAttributes.get("v"));
          if (!MessageDigest.isEqual(this.serverSignature, verifier))
            throw new SaslException(getIanaMechanismName() + " server signature could not be verified."); 
          break;
        default:
          throw new SaslException("Unexpected SCRAM authentication message.");
      } 
      String clientFirstMessage = null;
      return (byte[])clientFirstMessage;
    } catch (Throwable e) {
      this.scramStage = ScramExchangeStage.TERMINATED;
      throw e;
    } finally {
      this.scramStage = this.scramStage.getNext();
    } 
  }
  
  public boolean isComplete() {
    return (this.scramStage == ScramExchangeStage.TERMINATED);
  }
  
  public byte[] unwrap(byte[] incoming, int offset, int len) throws SaslException {
    throw new IllegalStateException("Integrity and/or privacy has not been negotiated.");
  }
  
  public byte[] wrap(byte[] outgoing, int offset, int len) throws SaslException {
    throw new IllegalStateException("Integrity and/or privacy has not been negotiated.");
  }
  
  public Object getNegotiatedProperty(String propName) {
    return null;
  }
  
  public void dispose() throws SaslException {}
  
  private String prepUserName(String userName) {
    return SaslPrep.prepare(userName, SaslPrep.StringType.QUERY).replace("=", "=2D").replace(",", "=2C");
  }
  
  private Map<String, String> parseChallenge(String challenge) {
    Map<String, String> attributesMap = new HashMap<>();
    for (String attribute : challenge.split(",")) {
      String[] keyValue = attribute.split("=", 2);
      attributesMap.put(keyValue[0], keyValue[1]);
    } 
    return attributesMap;
  }
  
  private String generateRandomPrintableAsciiString(int length) {
    int first = 33;
    int last = 126;
    int excl = 44;
    int bound = 93;
    Random random = new SecureRandom();
    char[] result = new char[length];
    for (int i = 0; i < length; ) {
      int randomValue = random.nextInt(93) + 33;
      if (randomValue != 44)
        result[i++] = (char)randomValue; 
    } 
    return new String(result);
  }
  
  abstract byte[] h(byte[] paramArrayOfbyte);
  
  abstract byte[] hmac(byte[] paramArrayOfbyte1, byte[] paramArrayOfbyte2);
  
  abstract byte[] hi(String paramString, byte[] paramArrayOfbyte, int paramInt);
  
  byte[] xorInPlace(byte[] inOut, byte[] other) {
    for (int i = 0; i < inOut.length; i++)
      inOut[i] = (byte)(inOut[i] ^ other[i]); 
    return inOut;
  }
}
