package org.apache.http.cookie;

import java.util.Date;
import org.apache.http.annotation.Obsolete;

public interface SetCookie extends Cookie {
  void setValue(String paramString);
  
  @Obsolete
  void setComment(String paramString);
  
  void setExpiryDate(Date paramDate);
  
  void setDomain(String paramString);
  
  void setPath(String paramString);
  
  void setSecure(boolean paramBoolean);
  
  @Obsolete
  void setVersion(int paramInt);
}
