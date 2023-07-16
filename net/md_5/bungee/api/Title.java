package net.md_5.bungee.api;

import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;

public interface Title {
  Title title(BaseComponent paramBaseComponent);
  
  Title title(BaseComponent... paramVarArgs);
  
  Title subTitle(BaseComponent paramBaseComponent);
  
  Title subTitle(BaseComponent... paramVarArgs);
  
  Title fadeIn(int paramInt);
  
  Title stay(int paramInt);
  
  Title fadeOut(int paramInt);
  
  Title clear();
  
  Title reset();
  
  Title send(ProxiedPlayer paramProxiedPlayer);
}
