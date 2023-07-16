package net.md_5.bungee.protocol.packet;

import com.mojang.brigadier.LiteralMessage;
import com.mojang.brigadier.Message;
import com.mojang.brigadier.context.StringRange;
import com.mojang.brigadier.suggestion.Suggestion;
import com.mojang.brigadier.suggestion.Suggestions;
import io.netty.buffer.ByteBuf;
import java.util.LinkedList;
import java.util.List;
import net.md_5.bungee.protocol.AbstractPacketHandler;
import net.md_5.bungee.protocol.DefinedPacket;
import net.md_5.bungee.protocol.ProtocolConstants;

public class TabCompleteResponse extends DefinedPacket {
  private int transactionId;
  
  private Suggestions suggestions;
  
  private List<String> commands;
  
  public void setTransactionId(int transactionId) {
    this.transactionId = transactionId;
  }
  
  public void setSuggestions(Suggestions suggestions) {
    this.suggestions = suggestions;
  }
  
  public void setCommands(List<String> commands) {
    this.commands = commands;
  }
  
  public String toString() {
    return "TabCompleteResponse(transactionId=" + getTransactionId() + ", suggestions=" + getSuggestions() + ", commands=" + getCommands() + ")";
  }
  
  public TabCompleteResponse() {}
  
  public boolean equals(Object o) {
    if (o == this)
      return true; 
    if (!(o instanceof TabCompleteResponse))
      return false; 
    TabCompleteResponse other = (TabCompleteResponse)o;
    if (!other.canEqual(this))
      return false; 
    if (getTransactionId() != other.getTransactionId())
      return false; 
    Object this$suggestions = getSuggestions(), other$suggestions = other.getSuggestions();
    if ((this$suggestions == null) ? (other$suggestions != null) : !this$suggestions.equals(other$suggestions))
      return false; 
    Object<String> this$commands = (Object<String>)getCommands(), other$commands = (Object<String>)other.getCommands();
    return !((this$commands == null) ? (other$commands != null) : !this$commands.equals(other$commands));
  }
  
  protected boolean canEqual(Object other) {
    return other instanceof TabCompleteResponse;
  }
  
  public int hashCode() {
    int PRIME = 59;
    result = 1;
    result = result * 59 + getTransactionId();
    Object $suggestions = getSuggestions();
    result = result * 59 + (($suggestions == null) ? 43 : $suggestions.hashCode());
    Object<String> $commands = (Object<String>)getCommands();
    return result * 59 + (($commands == null) ? 43 : $commands.hashCode());
  }
  
  public int getTransactionId() {
    return this.transactionId;
  }
  
  public Suggestions getSuggestions() {
    return this.suggestions;
  }
  
  public List<String> getCommands() {
    return this.commands;
  }
  
  public TabCompleteResponse(int transactionId, Suggestions suggestions) {
    this.transactionId = transactionId;
    this.suggestions = suggestions;
  }
  
  public TabCompleteResponse(List<String> commands) {
    this.commands = commands;
  }
  
  public void read(ByteBuf buf, ProtocolConstants.Direction direction, int protocolVersion) {
    if (protocolVersion >= 393) {
      this.transactionId = readVarInt(buf);
      int start = readVarInt(buf);
      int length = readVarInt(buf);
      StringRange range = StringRange.between(start, start + length);
      int cnt = readVarInt(buf);
      List<Suggestion> matches = new LinkedList<>();
      for (int i = 0; i < cnt; i++) {
        String match = readString(buf);
        String tooltip = buf.readBoolean() ? readString(buf) : null;
        matches.add(new Suggestion(range, match, (Message)new LiteralMessage(tooltip)));
      } 
      this.suggestions = new Suggestions(range, matches);
    } else {
      this.commands = readStringArray(buf);
    } 
  }
  
  public void write(ByteBuf buf, ProtocolConstants.Direction direction, int protocolVersion) {
    if (protocolVersion >= 393) {
      writeVarInt(this.transactionId, buf);
      writeVarInt(this.suggestions.getRange().getStart(), buf);
      writeVarInt(this.suggestions.getRange().getLength(), buf);
      writeVarInt(this.suggestions.getList().size(), buf);
      for (Suggestion suggestion : this.suggestions.getList()) {
        writeString(suggestion.getText(), buf);
        buf.writeBoolean((suggestion.getTooltip() != null && suggestion.getTooltip().getString() != null));
        if (suggestion.getTooltip() != null && suggestion.getTooltip().getString() != null)
          writeString(suggestion.getTooltip().getString(), buf); 
      } 
    } else {
      writeStringArray(this.commands, buf);
    } 
  }
  
  public void handle(AbstractPacketHandler handler) throws Exception {
    handler.handle(this);
  }
}
