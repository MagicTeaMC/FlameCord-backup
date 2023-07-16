package com.mysql.cj.callback;

public class FidoAuthenticationCallback implements MysqlCallback {
  private byte[] scramble;
  
  private String relyingPartyId;
  
  private byte[] credentialId;
  
  private byte[] authenticatorData;
  
  private byte[] signature;
  
  public FidoAuthenticationCallback(byte[] scramble, String relyingPartyId, byte[] credentialId) {
    this.scramble = scramble;
    this.relyingPartyId = relyingPartyId;
    this.credentialId = credentialId;
  }
  
  public byte[] getScramble() {
    return this.scramble;
  }
  
  public String getRelyingPartyId() {
    return this.relyingPartyId;
  }
  
  public byte[] getCredentialId() {
    return this.credentialId;
  }
  
  public void setAuthenticatorData(byte[] authenticatorData) {
    this.authenticatorData = authenticatorData;
  }
  
  public byte[] getAuthenticatorData() {
    return this.authenticatorData;
  }
  
  public void setSignature(byte[] signature) {
    this.signature = signature;
  }
  
  public byte[] getSignature() {
    return this.signature;
  }
}
