package net.md_5.bungee;

import net.md_5.bungee.api.Title;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.chat.ComponentSerializer;
import net.md_5.bungee.protocol.DefinedPacket;
import net.md_5.bungee.protocol.ProtocolConstants;
import net.md_5.bungee.protocol.packet.ClearTitles;
import net.md_5.bungee.protocol.packet.Subtitle;
import net.md_5.bungee.protocol.packet.Title;
import net.md_5.bungee.protocol.packet.TitleTimes;

public class BungeeTitle implements Title {
  private TitlePacketHolder<Title> title;
  
  private TitlePacketHolder<Subtitle> subtitle;
  
  private TitlePacketHolder<TitleTimes> times;
  
  private TitlePacketHolder<ClearTitles> clear;
  
  private TitlePacketHolder<ClearTitles> reset;
  
  private static class TitlePacketHolder<T extends DefinedPacket> {
    private final Title oldPacket;
    
    private final T newPacket;
    
    public TitlePacketHolder(Title oldPacket, T newPacket) {
      this.oldPacket = oldPacket;
      this.newPacket = newPacket;
    }
    
    public boolean equals(Object o) {
      if (o == this)
        return true; 
      if (!(o instanceof TitlePacketHolder))
        return false; 
      TitlePacketHolder<?> other = (TitlePacketHolder)o;
      if (!other.canEqual(this))
        return false; 
      Object this$oldPacket = getOldPacket(), other$oldPacket = other.getOldPacket();
      if ((this$oldPacket == null) ? (other$oldPacket != null) : !this$oldPacket.equals(other$oldPacket))
        return false; 
      Object this$newPacket = getNewPacket(), other$newPacket = other.getNewPacket();
      return !((this$newPacket == null) ? (other$newPacket != null) : !this$newPacket.equals(other$newPacket));
    }
    
    protected boolean canEqual(Object other) {
      return other instanceof TitlePacketHolder;
    }
    
    public int hashCode() {
      int PRIME = 59;
      result = 1;
      Object $oldPacket = getOldPacket();
      result = result * 59 + (($oldPacket == null) ? 43 : $oldPacket.hashCode());
      Object $newPacket = getNewPacket();
      return result * 59 + (($newPacket == null) ? 43 : $newPacket.hashCode());
    }
    
    public String toString() {
      return "BungeeTitle.TitlePacketHolder(oldPacket=" + getOldPacket() + ", newPacket=" + getNewPacket() + ")";
    }
    
    public Title getOldPacket() {
      return this.oldPacket;
    }
    
    public T getNewPacket() {
      return this.newPacket;
    }
  }
  
  private static TitlePacketHolder<TitleTimes> createAnimationPacket() {
    TitlePacketHolder<TitleTimes> title = new TitlePacketHolder<>(new Title(Title.Action.TIMES), new TitleTimes());
    title.oldPacket.setFadeIn(20);
    title.oldPacket.setStay(60);
    title.oldPacket.setFadeOut(20);
    ((TitleTimes)title.newPacket).setFadeIn(20);
    ((TitleTimes)title.newPacket).setStay(60);
    ((TitleTimes)title.newPacket).setFadeOut(20);
    return title;
  }
  
  public Title title(BaseComponent text) {
    if (this.title == null) {
      Title packet = new Title(Title.Action.TITLE);
      this.title = new TitlePacketHolder<>(packet, packet);
    } 
    this.title.oldPacket.setText(ComponentSerializer.toString(text));
    return this;
  }
  
  public Title title(BaseComponent... text) {
    if (this.title == null) {
      Title packet = new Title(Title.Action.TITLE);
      this.title = new TitlePacketHolder<>(packet, packet);
    } 
    this.title.oldPacket.setText(ComponentSerializer.toString(text));
    return this;
  }
  
  public Title subTitle(BaseComponent text) {
    if (this.subtitle == null)
      this.subtitle = new TitlePacketHolder<>(new Title(Title.Action.SUBTITLE), new Subtitle()); 
    String serialized = ComponentSerializer.toString(text);
    this.subtitle.oldPacket.setText(serialized);
    ((Subtitle)this.subtitle.newPacket).setText(serialized);
    return this;
  }
  
  public Title subTitle(BaseComponent... text) {
    if (this.subtitle == null)
      this.subtitle = new TitlePacketHolder<>(new Title(Title.Action.SUBTITLE), new Subtitle()); 
    String serialized = ComponentSerializer.toString(text);
    this.subtitle.oldPacket.setText(serialized);
    ((Subtitle)this.subtitle.newPacket).setText(serialized);
    return this;
  }
  
  public Title fadeIn(int ticks) {
    if (this.times == null)
      this.times = createAnimationPacket(); 
    this.times.oldPacket.setFadeIn(ticks);
    ((TitleTimes)this.times.newPacket).setFadeIn(ticks);
    return this;
  }
  
  public Title stay(int ticks) {
    if (this.times == null)
      this.times = createAnimationPacket(); 
    this.times.oldPacket.setStay(ticks);
    ((TitleTimes)this.times.newPacket).setStay(ticks);
    return this;
  }
  
  public Title fadeOut(int ticks) {
    if (this.times == null)
      this.times = createAnimationPacket(); 
    this.times.oldPacket.setFadeOut(ticks);
    ((TitleTimes)this.times.newPacket).setFadeOut(ticks);
    return this;
  }
  
  public Title clear() {
    if (this.clear == null)
      this.clear = new TitlePacketHolder<>(new Title(Title.Action.CLEAR), new ClearTitles()); 
    this.title = null;
    return this;
  }
  
  public Title reset() {
    if (this.reset == null)
      this.reset = new TitlePacketHolder<>(new Title(Title.Action.RESET), new ClearTitles(true)); 
    this.title = null;
    this.subtitle = null;
    this.times = null;
    return this;
  }
  
  private static void sendPacket(ProxiedPlayer player, TitlePacketHolder packet) {
    if (packet != null)
      if (player.getPendingConnection().getVersion() >= 755) {
        player.unsafe().sendPacket((DefinedPacket)packet.newPacket);
      } else {
        player.unsafe().sendPacket((DefinedPacket)packet.oldPacket);
      }  
  }
  
  public Title send(ProxiedPlayer player) {
    if (ProtocolConstants.isBeforeOrEq(player.getPendingConnection().getVersion(), 5))
      return this; 
    sendPacket(player, this.clear);
    sendPacket(player, this.reset);
    sendPacket(player, this.times);
    sendPacket(player, this.subtitle);
    sendPacket(player, this.title);
    return this;
  }
}
