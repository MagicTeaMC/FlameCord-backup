package net.md_5.bungee.util;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import java.util.List;
import java.util.regex.Pattern;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ScoreComponent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.hover.content.Content;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.score.Score;

public final class ChatComponentTransformer {
  private static final ChatComponentTransformer INSTANCE = new ChatComponentTransformer();
  
  private static final Pattern SELECTOR_PATTERN = Pattern.compile("^@([pares])(?:\\[([^ ]*)\\])?$");
  
  public BaseComponent[] legacyHoverTransform(ProxiedPlayer player, BaseComponent... components) {
    if (player.getPendingConnection().getVersion() < 735)
      for (int i = 0; i < components.length; i++) {
        BaseComponent next = components[i];
        if (next.getHoverEvent() != null && !next.getHoverEvent().isLegacy()) {
          next = next.duplicate();
          next.getHoverEvent().setLegacy(true);
          if (next.getHoverEvent().getContents().size() > 1) {
            Content exception = next.getHoverEvent().getContents().get(0);
            next.getHoverEvent().getContents().clear();
            next.getHoverEvent().getContents().add(exception);
          } 
          components[i] = next;
        } 
      }  
    return components;
  }
  
  public static ChatComponentTransformer getInstance() {
    return INSTANCE;
  }
  
  public BaseComponent[] transform(ProxiedPlayer player, BaseComponent... components) {
    return transform(player, false, components);
  }
  
  public BaseComponent[] transform(ProxiedPlayer player, boolean transformHover, BaseComponent... components) {
    if (components == null || components.length < 1 || (components.length == 1 && components[0] == null))
      return new BaseComponent[] { (BaseComponent)new TextComponent("") }; 
    if (transformHover)
      components = legacyHoverTransform(player, components); 
    for (BaseComponent root : components) {
      if (root.getExtra() != null && !root.getExtra().isEmpty()) {
        List<BaseComponent> list = Lists.newArrayList((Object[])transform(player, transformHover, (BaseComponent[])root.getExtra().toArray((Object[])new BaseComponent[0])));
        root.setExtra(list);
      } 
      if (root instanceof ScoreComponent)
        transformScoreComponent(player, (ScoreComponent)root); 
    } 
    return components;
  }
  
  private void transformScoreComponent(ProxiedPlayer player, ScoreComponent component) {
    Preconditions.checkArgument(!isSelectorPattern(component.getName()), "Cannot transform entity selector patterns");
    if (component.getValue() != null && !component.getValue().isEmpty())
      return; 
    if (component.getName().equals("*"))
      component.setName(player.getName()); 
    if (player.getScoreboard().getObjective(component.getObjective()) != null) {
      Score score = player.getScoreboard().getScore(component.getName());
      if (score != null)
        component.setValue(Integer.toString(score.getValue())); 
    } 
  }
  
  public boolean isSelectorPattern(String pattern) {
    return SELECTOR_PATTERN.matcher(pattern).matches();
  }
}
