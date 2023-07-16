package net.md_5.bungee;

import com.google.common.base.Preconditions;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.ArrayDeque;
import java.util.Queue;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.Connection;
import net.md_5.bungee.api.connection.Server;
import net.md_5.bungee.netty.ChannelWrapper;
import net.md_5.bungee.protocol.DefinedPacket;
import net.md_5.bungee.protocol.packet.PluginMessage;

public class ServerConnection implements Server {
  private final ChannelWrapper ch;
  
  private final BungeeServerInfo info;
  
  private boolean isObsolete;
  
  private final boolean forgeServer = false;
  
  private final Queue<KeepAliveData> keepAlives;
  
  private final Connection.Unsafe unsafe;
  
  public ServerConnection(ChannelWrapper ch, BungeeServerInfo info) {
    this.forgeServer = false;
    this.keepAlives = new ArrayDeque<>();
    this.unsafe = new Connection.Unsafe() {
        public void sendPacket(DefinedPacket packet) {
          ServerConnection.this.ch.write(packet);
        }
      };
    this.ch = ch;
    this.info = info;
  }
  
  public ChannelWrapper getCh() {
    return this.ch;
  }
  
  public BungeeServerInfo getInfo() {
    return this.info;
  }
  
  public boolean isObsolete() {
    return this.isObsolete;
  }
  
  public void setObsolete(boolean isObsolete) {
    this.isObsolete = isObsolete;
  }
  
  public boolean isForgeServer() {
    getClass();
    return false;
  }
  
  public Queue<KeepAliveData> getKeepAlives() {
    return this.keepAlives;
  }
  
  public void sendData(String channel, byte[] data) {
    unsafe().sendPacket((DefinedPacket)new PluginMessage(channel, data, false));
  }
  
  public void disconnect(String reason) {
    disconnect(new BaseComponent[0]);
  }
  
  public void disconnect(BaseComponent... reason) {
    Preconditions.checkArgument((reason.length == 0), "Server cannot have disconnect reason");
    this.ch.close();
  }
  
  public void disconnect(BaseComponent reason) {
    disconnect(new BaseComponent[0]);
  }
  
  public InetSocketAddress getAddress() {
    return (InetSocketAddress)getSocketAddress();
  }
  
  public SocketAddress getSocketAddress() {
    return getInfo().getAddress();
  }
  
  public boolean isConnected() {
    return !this.ch.isClosed();
  }
  
  public Connection.Unsafe unsafe() {
    return this.unsafe;
  }
  
  public static class KeepAliveData {
    private final long id;
    
    private final long time;
    
    public KeepAliveData(long id, long time) {
      this.id = id;
      this.time = time;
    }
    
    public boolean equals(Object o) {
      if (o == this)
        return true; 
      if (!(o instanceof KeepAliveData))
        return false; 
      KeepAliveData other = (KeepAliveData)o;
      return !other.canEqual(this) ? false : ((getId() != other.getId()) ? false : (!(getTime() != other.getTime())));
    }
    
    protected boolean canEqual(Object other) {
      return other instanceof KeepAliveData;
    }
    
    public int hashCode() {
      int PRIME = 59;
      result = 1;
      long $id = getId();
      result = result * 59 + (int)($id >>> 32L ^ $id);
      long $time = getTime();
      return result * 59 + (int)($time >>> 32L ^ $time);
    }
    
    public String toString() {
      return "ServerConnection.KeepAliveData(id=" + getId() + ", time=" + getTime() + ")";
    }
    
    public long getId() {
      return this.id;
    }
    
    public long getTime() {
      return this.time;
    }
  }
}
