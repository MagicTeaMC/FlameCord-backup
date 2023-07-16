package net.md_5.bungee.protocol.packet;

import com.google.common.base.Preconditions;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.LongArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import com.mojang.brigadier.tree.ArgumentCommandNode;
import com.mojang.brigadier.tree.CommandNode;
import com.mojang.brigadier.tree.LiteralCommandNode;
import com.mojang.brigadier.tree.RootCommandNode;
import io.netty.buffer.ByteBuf;
import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Deque;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import net.md_5.bungee.protocol.AbstractPacketHandler;
import net.md_5.bungee.protocol.DefinedPacket;
import net.md_5.bungee.protocol.ProtocolConstants;

public class Commands extends DefinedPacket {
  private static final int FLAG_TYPE = 3;
  
  private static final int FLAG_EXECUTABLE = 4;
  
  private static final int FLAG_REDIRECT = 8;
  
  private static final int FLAG_SUGGESTIONS = 16;
  
  private static final int NODE_ROOT = 0;
  
  private static final int NODE_LITERAL = 1;
  
  private static final int NODE_ARGUMENT = 2;
  
  private RootCommandNode root;
  
  public void setRoot(RootCommandNode root) {
    this.root = root;
  }
  
  public String toString() {
    return "Commands(root=" + getRoot() + ")";
  }
  
  public Commands() {}
  
  public Commands(RootCommandNode root) {
    this.root = root;
  }
  
  public boolean equals(Object o) {
    if (o == this)
      return true; 
    if (!(o instanceof Commands))
      return false; 
    Commands other = (Commands)o;
    if (!other.canEqual(this))
      return false; 
    Object this$root = getRoot(), other$root = other.getRoot();
    return !((this$root == null) ? (other$root != null) : !this$root.equals(other$root));
  }
  
  protected boolean canEqual(Object other) {
    return other instanceof Commands;
  }
  
  public int hashCode() {
    int PRIME = 59;
    result = 1;
    Object $root = getRoot();
    return result * 59 + (($root == null) ? 43 : $root.hashCode());
  }
  
  public RootCommandNode getRoot() {
    return this.root;
  }
  
  public void read(ByteBuf buf, ProtocolConstants.Direction direction, int protocolVersion) {
    boolean mustCycle;
    int nodeCount = readVarInt(buf);
    NetworkNode[] nodes = new NetworkNode[nodeCount];
    Deque<NetworkNode> nodeQueue = new ArrayDeque<>(nodes.length);
    for (int i = 0; i < nodeCount; i++) {
      ArgumentBuilder argumentBuilder;
      LiteralArgumentBuilder literalArgumentBuilder;
      RequiredArgumentBuilder requiredArgumentBuilder;
      String name;
      byte flags = buf.readByte();
      int[] children = readVarIntArray(buf);
      int redirectNode = ((flags & 0x8) != 0) ? readVarInt(buf) : 0;
      switch (flags & 0x3) {
        case 0:
          argumentBuilder = null;
          break;
        case 1:
          literalArgumentBuilder = LiteralArgumentBuilder.literal(readString(buf));
          break;
        case 2:
          name = readString(buf);
          requiredArgumentBuilder = RequiredArgumentBuilder.argument(name, ArgumentRegistry.read(buf, protocolVersion));
          if ((flags & 0x10) != 0) {
            String suggster = readString(buf);
            requiredArgumentBuilder.suggests(SuggestionRegistry.getProvider(suggster));
          } 
          break;
        default:
          throw new IllegalArgumentException("Unhandled node type " + flags);
      } 
      NetworkNode node = new NetworkNode((ArgumentBuilder)requiredArgumentBuilder, flags, redirectNode, children);
      nodes[i] = node;
      nodeQueue.add(node);
    } 
    do {
      if (nodeQueue.isEmpty()) {
        int rootIndex = readVarInt(buf);
        this.root = (RootCommandNode)(nodes[rootIndex]).command;
        return;
      } 
      mustCycle = false;
      for (Iterator<NetworkNode> iter = nodeQueue.iterator(); iter.hasNext(); ) {
        NetworkNode node = iter.next();
        if (node.buildSelf(nodes)) {
          iter.remove();
          mustCycle = true;
        } 
      } 
    } while (mustCycle);
    throw new IllegalStateException("Did not finish building root node");
  }
  
  public void write(ByteBuf buf, ProtocolConstants.Direction direction, int protocolVersion) {
    Map<CommandNode, Integer> indexMap = new LinkedHashMap<>();
    Deque<CommandNode> nodeQueue = new ArrayDeque<>();
    nodeQueue.add(this.root);
    while (!nodeQueue.isEmpty()) {
      CommandNode command = nodeQueue.pollFirst();
      if (!indexMap.containsKey(command)) {
        int i = indexMap.size();
        indexMap.put(command, Integer.valueOf(i));
        nodeQueue.addAll(command.getChildren());
        if (command.getRedirect() != null)
          nodeQueue.add(command.getRedirect()); 
      } 
    } 
    writeVarInt(indexMap.size(), buf);
    int currentIndex = 0;
    for (Map.Entry<CommandNode, Integer> entry : indexMap.entrySet()) {
      Preconditions.checkState((((Integer)entry.getValue()).intValue() == currentIndex++), "Iteration out of order!");
      CommandNode node = entry.getKey();
      byte flags = 0;
      if (node.getRedirect() != null)
        flags = (byte)(flags | 0x8); 
      if (node.getCommand() != null)
        flags = (byte)(flags | 0x4); 
      if (node instanceof RootCommandNode) {
        flags = (byte)(flags | 0x0);
      } else if (node instanceof LiteralCommandNode) {
        flags = (byte)(flags | 0x1);
      } else if (node instanceof ArgumentCommandNode) {
        flags = (byte)(flags | 0x2);
        if (((ArgumentCommandNode)node).getCustomSuggestions() != null)
          flags = (byte)(flags | 0x10); 
      } else {
        throw new IllegalArgumentException("Unhandled node type " + node);
      } 
      buf.writeByte(flags);
      writeVarInt(node.getChildren().size(), buf);
      for (CommandNode child : node.getChildren())
        writeVarInt(((Integer)indexMap.get(child)).intValue(), buf); 
      if (node.getRedirect() != null)
        writeVarInt(((Integer)indexMap.get(node.getRedirect())).intValue(), buf); 
      if (node instanceof LiteralCommandNode) {
        writeString(((LiteralCommandNode)node).getLiteral(), buf);
        continue;
      } 
      if (node instanceof ArgumentCommandNode) {
        ArgumentCommandNode argumentNode = (ArgumentCommandNode)node;
        writeString(argumentNode.getName(), buf);
        ArgumentRegistry.write(argumentNode.getType(), buf, protocolVersion);
        if (argumentNode.getCustomSuggestions() != null)
          writeString(SuggestionRegistry.getKey(argumentNode.getCustomSuggestions()), buf); 
      } 
    } 
    int rootIndex = ((Integer)indexMap.get(this.root)).intValue();
    Preconditions.checkState((rootIndex == 0), "How did root not land up at index 0?!?");
    writeVarInt(rootIndex, buf);
  }
  
  public void handle(AbstractPacketHandler handler) throws Exception {
    handler.handle(this);
  }
  
  private static class NetworkNode {
    private final ArgumentBuilder argumentBuilder;
    
    private final byte flags;
    
    private final int redirectNode;
    
    private final int[] children;
    
    private CommandNode command;
    
    public NetworkNode(ArgumentBuilder argumentBuilder, byte flags, int redirectNode, int[] children) {
      this.argumentBuilder = argumentBuilder;
      this.flags = flags;
      this.redirectNode = redirectNode;
      this.children = children;
    }
    
    public void setCommand(CommandNode command) {
      this.command = command;
    }
    
    public boolean equals(Object o) {
      if (o == this)
        return true; 
      if (!(o instanceof NetworkNode))
        return false; 
      NetworkNode other = (NetworkNode)o;
      if (!other.canEqual(this))
        return false; 
      if (getFlags() != other.getFlags())
        return false; 
      if (getRedirectNode() != other.getRedirectNode())
        return false; 
      Object this$argumentBuilder = getArgumentBuilder(), other$argumentBuilder = other.getArgumentBuilder();
      if ((this$argumentBuilder == null) ? (other$argumentBuilder != null) : !this$argumentBuilder.equals(other$argumentBuilder))
        return false; 
      if (!Arrays.equals(getChildren(), other.getChildren()))
        return false; 
      Object this$command = getCommand(), other$command = other.getCommand();
      return !((this$command == null) ? (other$command != null) : !this$command.equals(other$command));
    }
    
    protected boolean canEqual(Object other) {
      return other instanceof NetworkNode;
    }
    
    public int hashCode() {
      int PRIME = 59;
      result = 1;
      result = result * 59 + getFlags();
      result = result * 59 + getRedirectNode();
      Object $argumentBuilder = getArgumentBuilder();
      result = result * 59 + (($argumentBuilder == null) ? 43 : $argumentBuilder.hashCode());
      result = result * 59 + Arrays.hashCode(getChildren());
      Object $command = getCommand();
      return result * 59 + (($command == null) ? 43 : $command.hashCode());
    }
    
    public String toString() {
      return "Commands.NetworkNode(argumentBuilder=" + getArgumentBuilder() + ", flags=" + getFlags() + ", redirectNode=" + getRedirectNode() + ", children=" + Arrays.toString(getChildren()) + ", command=" + getCommand() + ")";
    }
    
    public ArgumentBuilder getArgumentBuilder() {
      return this.argumentBuilder;
    }
    
    public byte getFlags() {
      return this.flags;
    }
    
    public int getRedirectNode() {
      return this.redirectNode;
    }
    
    public int[] getChildren() {
      return this.children;
    }
    
    public CommandNode getCommand() {
      return this.command;
    }
    
    private boolean buildSelf(NetworkNode[] otherNodes) {
      if (this.command == null)
        if (this.argumentBuilder == null) {
          this.command = (CommandNode)new RootCommandNode();
        } else {
          if ((this.flags & 0x8) != 0) {
            if ((otherNodes[this.redirectNode]).command == null)
              return false; 
            this.argumentBuilder.redirect((otherNodes[this.redirectNode]).command);
          } 
          if ((this.flags & 0x4) != 0)
            this.argumentBuilder.executes(new Command() {
                  public int run(CommandContext context) throws CommandSyntaxException {
                    return 0;
                  }
                }); 
          this.command = this.argumentBuilder.build();
        }  
      for (int childIndex : this.children) {
        if ((otherNodes[childIndex]).command == null)
          return false; 
      } 
      for (int childIndex : this.children) {
        CommandNode<?> child = (otherNodes[childIndex]).command;
        Preconditions.checkArgument(!(child instanceof RootCommandNode), "Cannot have RootCommandNode as child");
        this.command.addChild(child);
      } 
      return true;
    }
  }
  
  private static class ArgumentRegistry {
    public boolean equals(Object o) {
      if (o == this)
        return true; 
      if (!(o instanceof ArgumentRegistry))
        return false; 
      ArgumentRegistry other = (ArgumentRegistry)o;
      return !!other.canEqual(this);
    }
    
    protected boolean canEqual(Object other) {
      return other instanceof ArgumentRegistry;
    }
    
    public int hashCode() {
      int result = 1;
      return 1;
    }
    
    public String toString() {
      return "Commands.ArgumentRegistry()";
    }
    
    private static final Map<String, ArgumentSerializer> PROVIDERS = new HashMap<>();
    
    private static final ArgumentSerializer[] IDS_1_19 = new ArgumentSerializer[] { 
        get("brigadier:bool", VOID), 
        get("brigadier:float", FLOAT_RANGE), 
        get("brigadier:double", DOUBLE_RANGE), 
        get("brigadier:integer", INTEGER_RANGE), 
        get("brigadier:long", LONG_RANGE), 
        get("brigadier:string", STRING), 
        get("minecraft:entity", BYTE), 
        get("minecraft:game_profile", VOID), 
        get("minecraft:block_pos", VOID), 
        get("minecraft:column_pos", VOID), 
        get("minecraft:vec3", VOID), 
        get("minecraft:vec2", VOID), 
        get("minecraft:block_state", VOID), 
        get("minecraft:block_predicate", VOID), 
        get("minecraft:item_stack", VOID), 
        get("minecraft:item_predicate", VOID), 
        get("minecraft:color", VOID), 
        get("minecraft:component", VOID), 
        get("minecraft:message", VOID), 
        get("minecraft:nbt_compound_tag", VOID), 
        get("minecraft:nbt_tag", VOID), 
        get("minecraft:nbt_path", VOID), 
        get("minecraft:objective", VOID), 
        get("minecraft:objective_criteria", VOID), 
        get("minecraft:operation", VOID), 
        get("minecraft:particle", VOID), 
        get("minecraft:angle", VOID), 
        get("minecraft:rotation", VOID), 
        get("minecraft:scoreboard_slot", VOID), 
        get("minecraft:score_holder", BYTE), 
        get("minecraft:swizzle", VOID), 
        get("minecraft:team", VOID), 
        get("minecraft:item_slot", VOID), 
        get("minecraft:resource_location", VOID), 
        get("minecraft:mob_effect", VOID), 
        get("minecraft:function", VOID), 
        get("minecraft:entity_anchor", VOID), 
        get("minecraft:int_range", VOID), 
        get("minecraft:float_range", VOID), 
        get("minecraft:item_enchantment", VOID), 
        get("minecraft:entity_summon", VOID), 
        get("minecraft:dimension", VOID), 
        get("minecraft:time", VOID), 
        get("minecraft:resource_or_tag", RAW_STRING), 
        get("minecraft:resource", RAW_STRING), 
        get("minecraft:template_mirror", VOID), 
        get("minecraft:template_rotation", VOID), 
        get("minecraft:uuid", VOID) };
    
    private static final ArgumentSerializer[] IDS_1_19_3 = new ArgumentSerializer[] { 
        get("brigadier:bool", VOID), 
        get("brigadier:float", FLOAT_RANGE), 
        get("brigadier:double", DOUBLE_RANGE), 
        get("brigadier:integer", INTEGER_RANGE), 
        get("brigadier:long", LONG_RANGE), 
        get("brigadier:string", STRING), 
        get("minecraft:entity", BYTE), 
        get("minecraft:game_profile", VOID), 
        get("minecraft:block_pos", VOID), 
        get("minecraft:column_pos", VOID), 
        get("minecraft:vec3", VOID), 
        get("minecraft:vec2", VOID), 
        get("minecraft:block_state", VOID), 
        get("minecraft:block_predicate", VOID), 
        get("minecraft:item_stack", VOID), 
        get("minecraft:item_predicate", VOID), 
        get("minecraft:color", VOID), 
        get("minecraft:component", VOID), 
        get("minecraft:message", VOID), 
        get("minecraft:nbt_compound_tag", VOID), 
        get("minecraft:nbt_tag", VOID), 
        get("minecraft:nbt_path", VOID), 
        get("minecraft:objective", VOID), 
        get("minecraft:objective_criteria", VOID), 
        get("minecraft:operation", VOID), 
        get("minecraft:particle", VOID), 
        get("minecraft:angle", VOID), 
        get("minecraft:rotation", VOID), 
        get("minecraft:scoreboard_slot", VOID), 
        get("minecraft:score_holder", BYTE), 
        get("minecraft:swizzle", VOID), 
        get("minecraft:team", VOID), 
        get("minecraft:item_slot", VOID), 
        get("minecraft:resource_location", VOID), 
        get("minecraft:function", VOID), 
        get("minecraft:entity_anchor", VOID), 
        get("minecraft:int_range", VOID), 
        get("minecraft:float_range", VOID), 
        get("minecraft:dimension", VOID), 
        get("minecraft:gamemode", VOID), 
        get("minecraft:time", VOID), 
        get("minecraft:resource_or_tag", RAW_STRING), 
        get("minecraft:resource_or_tag_key", RAW_STRING), 
        get("minecraft:resource", RAW_STRING), 
        get("minecraft:resource_key", RAW_STRING), 
        get("minecraft:template_mirror", VOID), 
        get("minecraft:template_rotation", VOID), 
        get("minecraft:uuid", VOID) };
    
    private static final ArgumentSerializer[] IDS_1_19_4 = new ArgumentSerializer[] { 
        get("brigadier:bool", VOID), 
        get("brigadier:float", FLOAT_RANGE), 
        get("brigadier:double", DOUBLE_RANGE), 
        get("brigadier:integer", INTEGER_RANGE), 
        get("brigadier:long", LONG_RANGE), 
        get("brigadier:string", STRING), 
        get("minecraft:entity", BYTE), 
        get("minecraft:game_profile", VOID), 
        get("minecraft:block_pos", VOID), 
        get("minecraft:column_pos", VOID), 
        get("minecraft:vec3", VOID), 
        get("minecraft:vec2", VOID), 
        get("minecraft:block_state", VOID), 
        get("minecraft:block_predicate", VOID), 
        get("minecraft:item_stack", VOID), 
        get("minecraft:item_predicate", VOID), 
        get("minecraft:color", VOID), 
        get("minecraft:component", VOID), 
        get("minecraft:message", VOID), 
        get("minecraft:nbt_compound_tag", VOID), 
        get("minecraft:nbt_tag", VOID), 
        get("minecraft:nbt_path", VOID), 
        get("minecraft:objective", VOID), 
        get("minecraft:objective_criteria", VOID), 
        get("minecraft:operation", VOID), 
        get("minecraft:particle", VOID), 
        get("minecraft:angle", VOID), 
        get("minecraft:rotation", VOID), 
        get("minecraft:scoreboard_slot", VOID), 
        get("minecraft:score_holder", BYTE), 
        get("minecraft:swizzle", VOID), 
        get("minecraft:team", VOID), 
        get("minecraft:item_slot", VOID), 
        get("minecraft:resource_location", VOID), 
        get("minecraft:function", VOID), 
        get("minecraft:entity_anchor", VOID), 
        get("minecraft:int_range", VOID), 
        get("minecraft:float_range", VOID), 
        get("minecraft:dimension", VOID), 
        get("minecraft:gamemode", VOID), 
        get("minecraft:time", INTEGER), 
        get("minecraft:resource_or_tag", RAW_STRING), 
        get("minecraft:resource_or_tag_key", RAW_STRING), 
        get("minecraft:resource", RAW_STRING), 
        get("minecraft:resource_key", RAW_STRING), 
        get("minecraft:template_mirror", VOID), 
        get("minecraft:template_rotation", VOID), 
        get("minecraft:uuid", VOID), 
        get("minecraft:heightmap", VOID) };
    
    private static final Map<Class<?>, ProperArgumentSerializer<?>> PROPER_PROVIDERS = new HashMap<>();
    
    private static final ArgumentSerializer<Void> VOID = new ArgumentSerializer<Void>() {
        protected Void read(ByteBuf buf) {
          return null;
        }
        
        protected void write(ByteBuf buf, Void t) {}
      };
    
    private static final ArgumentSerializer<Boolean> BOOLEAN = new ArgumentSerializer<Boolean>() {
        protected Boolean read(ByteBuf buf) {
          return Boolean.valueOf(buf.readBoolean());
        }
        
        protected void write(ByteBuf buf, Boolean t) {
          buf.writeBoolean(t.booleanValue());
        }
      };
    
    private static final ArgumentSerializer<Byte> BYTE = new ArgumentSerializer<Byte>() {
        protected Byte read(ByteBuf buf) {
          return Byte.valueOf(buf.readByte());
        }
        
        protected void write(ByteBuf buf, Byte t) {
          buf.writeByte(t.byteValue());
        }
      };
    
    private static final ArgumentSerializer<FloatArgumentType> FLOAT_RANGE = new ArgumentSerializer<FloatArgumentType>() {
        protected FloatArgumentType read(ByteBuf buf) {
          byte flags = buf.readByte();
          float min = ((flags & 0x1) != 0) ? buf.readFloat() : -3.4028235E38F;
          float max = ((flags & 0x2) != 0) ? buf.readFloat() : Float.MAX_VALUE;
          return FloatArgumentType.floatArg(min, max);
        }
        
        protected void write(ByteBuf buf, FloatArgumentType t) {
          boolean hasMin = (t.getMinimum() != -3.4028235E38F);
          boolean hasMax = (t.getMaximum() != Float.MAX_VALUE);
          buf.writeByte(Commands.binaryFlag(hasMin, hasMax));
          if (hasMin)
            buf.writeFloat(t.getMinimum()); 
          if (hasMax)
            buf.writeFloat(t.getMaximum()); 
        }
      };
    
    private static final ArgumentSerializer<DoubleArgumentType> DOUBLE_RANGE = new ArgumentSerializer<DoubleArgumentType>() {
        protected DoubleArgumentType read(ByteBuf buf) {
          byte flags = buf.readByte();
          double min = ((flags & 0x1) != 0) ? buf.readDouble() : -1.7976931348623157E308D;
          double max = ((flags & 0x2) != 0) ? buf.readDouble() : Double.MAX_VALUE;
          return DoubleArgumentType.doubleArg(min, max);
        }
        
        protected void write(ByteBuf buf, DoubleArgumentType t) {
          boolean hasMin = (t.getMinimum() != -1.7976931348623157E308D);
          boolean hasMax = (t.getMaximum() != Double.MAX_VALUE);
          buf.writeByte(Commands.binaryFlag(hasMin, hasMax));
          if (hasMin)
            buf.writeDouble(t.getMinimum()); 
          if (hasMax)
            buf.writeDouble(t.getMaximum()); 
        }
      };
    
    private static final ArgumentSerializer<IntegerArgumentType> INTEGER_RANGE = new ArgumentSerializer<IntegerArgumentType>() {
        protected IntegerArgumentType read(ByteBuf buf) {
          byte flags = buf.readByte();
          int min = ((flags & 0x1) != 0) ? buf.readInt() : Integer.MIN_VALUE;
          int max = ((flags & 0x2) != 0) ? buf.readInt() : Integer.MAX_VALUE;
          return IntegerArgumentType.integer(min, max);
        }
        
        protected void write(ByteBuf buf, IntegerArgumentType t) {
          boolean hasMin = (t.getMinimum() != Integer.MIN_VALUE);
          boolean hasMax = (t.getMaximum() != Integer.MAX_VALUE);
          buf.writeByte(Commands.binaryFlag(hasMin, hasMax));
          if (hasMin)
            buf.writeInt(t.getMinimum()); 
          if (hasMax)
            buf.writeInt(t.getMaximum()); 
        }
      };
    
    private static final ArgumentSerializer<Integer> INTEGER = new ArgumentSerializer<Integer>() {
        protected Integer read(ByteBuf buf) {
          return Integer.valueOf(buf.readInt());
        }
        
        protected void write(ByteBuf buf, Integer t) {
          buf.writeInt(t.intValue());
        }
      };
    
    private static final ArgumentSerializer<LongArgumentType> LONG_RANGE = new ArgumentSerializer<LongArgumentType>() {
        protected LongArgumentType read(ByteBuf buf) {
          byte flags = buf.readByte();
          long min = ((flags & 0x1) != 0) ? buf.readLong() : Long.MIN_VALUE;
          long max = ((flags & 0x2) != 0) ? buf.readLong() : Long.MAX_VALUE;
          return LongArgumentType.longArg(min, max);
        }
        
        protected void write(ByteBuf buf, LongArgumentType t) {
          boolean hasMin = (t.getMinimum() != Long.MIN_VALUE);
          boolean hasMax = (t.getMaximum() != Long.MAX_VALUE);
          buf.writeByte(Commands.binaryFlag(hasMin, hasMax));
          if (hasMin)
            buf.writeLong(t.getMinimum()); 
          if (hasMax)
            buf.writeLong(t.getMaximum()); 
        }
      };
    
    private static final ProperArgumentSerializer<StringArgumentType> STRING = new ProperArgumentSerializer<StringArgumentType>() {
        protected StringArgumentType read(ByteBuf buf) {
          int val = DefinedPacket.readVarInt(buf);
          switch (val) {
            case 0:
              return StringArgumentType.word();
            case 1:
              return StringArgumentType.string();
            case 2:
              return StringArgumentType.greedyString();
          } 
          throw new IllegalArgumentException("Unknown string type " + val);
        }
        
        protected void write(ByteBuf buf, StringArgumentType t) {
          DefinedPacket.writeVarInt(t.getType().ordinal(), buf);
        }
        
        protected int getIntKey() {
          return 5;
        }
        
        protected String getKey() {
          return "brigadier:string";
        }
      };
    
    private static final ArgumentSerializer<String> RAW_STRING = new ArgumentSerializer<String>() {
        protected String read(ByteBuf buf) {
          return DefinedPacket.readString(buf);
        }
        
        protected void write(ByteBuf buf, String t) {
          DefinedPacket.writeString(t, buf);
        }
      };
    
    static {
    
    }
    
    private static void register(String name, ArgumentSerializer serializer) {
      PROVIDERS.put(name, serializer);
    }
    
    private static ArgumentSerializer get(String name, ArgumentSerializer serializer) {
      return serializer;
    }
    
    private static ArgumentType<?> read(ByteBuf buf, int protocolVersion) {
      Object key;
      ArgumentSerializer<?> reader;
      if (protocolVersion >= 759) {
        key = Integer.valueOf(DefinedPacket.readVarInt(buf));
        if (protocolVersion >= 762) {
          reader = IDS_1_19_4[((Integer)key).intValue()];
        } else if (protocolVersion >= 761) {
          reader = IDS_1_19_3[((Integer)key).intValue()];
        } else {
          reader = IDS_1_19[((Integer)key).intValue()];
        } 
      } else {
        key = DefinedPacket.readString(buf);
        reader = PROVIDERS.get(key);
      } 
      Preconditions.checkArgument((reader != null), "No provider for argument " + key);
      Object val = reader.read(buf);
      return (val != null && PROPER_PROVIDERS.containsKey(val.getClass())) ? (ArgumentType)val : new DummyType(key, reader, val);
    }
    
    private static void write(ArgumentType<?> arg, ByteBuf buf, int protocolVersion) {
      ProperArgumentSerializer<ArgumentType<?>> proper = (ProperArgumentSerializer)PROPER_PROVIDERS.get(arg.getClass());
      if (proper != null) {
        if (protocolVersion >= 759) {
          DefinedPacket.writeVarInt(proper.getIntKey(), buf);
        } else {
          DefinedPacket.writeString(proper.getKey(), buf);
        } 
        proper.write(buf, arg);
      } else {
        Preconditions.checkArgument(arg instanceof DummyType, "Non dummy arg " + arg.getClass());
        DummyType dummy = (DummyType)arg;
        if (dummy.key instanceof Integer) {
          DefinedPacket.writeVarInt(((Integer)dummy.key).intValue(), buf);
        } else {
          DefinedPacket.writeString((String)dummy.key, buf);
        } 
        dummy.serializer.write(buf, dummy.value);
      } 
    }
    
    private static class DummyType<T> implements ArgumentType<T> {
      private final Object key;
      
      private final Commands.ArgumentRegistry.ArgumentSerializer<T> serializer;
      
      private final T value;
      
      public DummyType(Object key, Commands.ArgumentRegistry.ArgumentSerializer<T> serializer, T value) {
        this.key = key;
        this.serializer = serializer;
        this.value = value;
      }
      
      public boolean equals(Object o) {
        if (o == this)
          return true; 
        if (!(o instanceof DummyType))
          return false; 
        DummyType<?> other = (DummyType)o;
        if (!other.canEqual(this))
          return false; 
        Object this$key = getKey(), other$key = other.getKey();
        if ((this$key == null) ? (other$key != null) : !this$key.equals(other$key))
          return false; 
        Object<T> this$serializer = (Object<T>)getSerializer();
        Object<?> other$serializer = (Object<?>)other.getSerializer();
        if ((this$serializer == null) ? (other$serializer != null) : !this$serializer.equals(other$serializer))
          return false; 
        Object this$value = getValue(), other$value = other.getValue();
        return !((this$value == null) ? (other$value != null) : !this$value.equals(other$value));
      }
      
      protected boolean canEqual(Object other) {
        return other instanceof DummyType;
      }
      
      public int hashCode() {
        int PRIME = 59;
        result = 1;
        Object $key = getKey();
        result = result * 59 + (($key == null) ? 43 : $key.hashCode());
        Object<T> $serializer = (Object<T>)getSerializer();
        result = result * 59 + (($serializer == null) ? 43 : $serializer.hashCode());
        Object $value = getValue();
        return result * 59 + (($value == null) ? 43 : $value.hashCode());
      }
      
      public String toString() {
        return "Commands.ArgumentRegistry.DummyType(key=" + getKey() + ", serializer=" + getSerializer() + ", value=" + getValue() + ")";
      }
      
      public Object getKey() {
        return this.key;
      }
      
      public Commands.ArgumentRegistry.ArgumentSerializer<T> getSerializer() {
        return this.serializer;
      }
      
      public T getValue() {
        return this.value;
      }
      
      public T parse(StringReader reader) throws CommandSyntaxException {
        throw new UnsupportedOperationException("Not supported.");
      }
    }
    
    private static abstract class ArgumentSerializer<T> {
      private ArgumentSerializer() {}
      
      protected abstract T read(ByteBuf param2ByteBuf);
      
      protected abstract void write(ByteBuf param2ByteBuf, T param2T);
    }
    
    private static abstract class ProperArgumentSerializer<T> extends ArgumentSerializer<T> {
      private ProperArgumentSerializer() {}
      
      protected abstract int getIntKey();
      
      protected abstract String getKey();
    }
  }
  
  public static class SuggestionRegistry {
    public boolean equals(Object o) {
      if (o == this)
        return true; 
      if (!(o instanceof SuggestionRegistry))
        return false; 
      SuggestionRegistry other = (SuggestionRegistry)o;
      return !!other.canEqual(this);
    }
    
    protected boolean canEqual(Object other) {
      return other instanceof SuggestionRegistry;
    }
    
    public int hashCode() {
      int result = 1;
      return 1;
    }
    
    public String toString() {
      return "Commands.SuggestionRegistry()";
    }
    
    public static final SuggestionProvider ASK_SERVER = new DummyProvider("minecraft:ask_server");
    
    private static final Map<String, SuggestionProvider<DummyProvider>> PROVIDERS = new HashMap<>();
    
    static {
      PROVIDERS.put("minecraft:ask_server", ASK_SERVER);
      registerDummy("minecraft:all_recipes");
      registerDummy("minecraft:available_sounds");
      registerDummy("minecraft:available_biomes");
      registerDummy("minecraft:summonable_entities");
    }
    
    private static void registerDummy(String name) {
      PROVIDERS.put(name, new DummyProvider(name));
    }
    
    private static SuggestionProvider<DummyProvider> getProvider(String key) {
      SuggestionProvider<DummyProvider> provider = PROVIDERS.get(key);
      Preconditions.checkArgument((provider != null), "Unknown completion provider " + key);
      return provider;
    }
    
    private static String getKey(SuggestionProvider<DummyProvider> provider) {
      Preconditions.checkArgument(provider instanceof DummyProvider, "Non dummy provider " + provider);
      return ((DummyProvider)provider).key;
    }
    
    private static final class DummyProvider implements SuggestionProvider<DummyProvider> {
      private final String key;
      
      public DummyProvider(String key) {
        this.key = key;
      }
      
      public boolean equals(Object o) {
        if (o == this)
          return true; 
        if (!(o instanceof DummyProvider))
          return false; 
        DummyProvider other = (DummyProvider)o;
        Object this$key = getKey(), other$key = other.getKey();
        return !((this$key == null) ? (other$key != null) : !this$key.equals(other$key));
      }
      
      public int hashCode() {
        int PRIME = 59;
        result = 1;
        Object $key = getKey();
        return result * 59 + (($key == null) ? 43 : $key.hashCode());
      }
      
      public String toString() {
        return "Commands.SuggestionRegistry.DummyProvider(key=" + getKey() + ")";
      }
      
      public String getKey() {
        return this.key;
      }
      
      public CompletableFuture<Suggestions> getSuggestions(CommandContext<DummyProvider> context, SuggestionsBuilder builder) throws CommandSyntaxException {
        return builder.buildFuture();
      }
    }
  }
  
  private static byte binaryFlag(boolean first, boolean second) {
    byte ret = 0;
    if (first)
      ret = (byte)(ret | 0x1); 
    if (second)
      ret = (byte)(ret | 0x2); 
    return ret;
  }
}
