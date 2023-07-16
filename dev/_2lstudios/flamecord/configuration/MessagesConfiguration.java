package dev._2lstudios.flamecord.configuration;

import java.io.File;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;

public class MessagesConfiguration extends FlameConfig {
  private final Logger logger;
  
  private final Map<String, String> messages = new HashMap<>();
  
  public MessagesConfiguration(Logger logger, ConfigurationProvider configurationProvider) {
    this.logger = logger;
    File configurationFile = new File("./messages.yml");
    Configuration configuration = load(configurationFile);
    setIfUnexistant("flamecord_reload", "&aAll files had been successfully reloaded!", configuration);
    setIfUnexistant("flamecord_help", "&aFlameCord&b {0}&a by&b LinsaFTW\n&e /flamecord reload&7 >&b Reloads FlameCord files!\n&e /flamecord firewall <add/remove> <ip>&7 >&b Firewall certain ips!\n&e /bplugins&7 >&b Show the plugin list!\n&e /bip <player>&7 >&b Show the ip and info of a player!\n&e /flamecord help&7 >&b Shows this message!", configuration);
    setIfUnexistant("flamecord_nopermission", "&cYou don't have permission to do this!", configuration);
    setIfUnexistant("alert", "&8[&4Alert&8]&r ", configuration);
    setIfUnexistant("already_connected", "&cYou are already connected to this server!", configuration);
    setIfUnexistant("already_connected_proxy", "&cYou are already connected to this proxy!", configuration);
    setIfUnexistant("already_connecting", "&cAlready connecting to this server!", configuration);
    setIfUnexistant("command_list", "&a[{0}] &e({1}): &r{2}", configuration);
    setIfUnexistant("connect_kick", "&cKicked whilst connecting to {0}: {1}", configuration);
    setIfUnexistant("current_server", "&6You are currently connected to {0}.", configuration);
    setIfUnexistant("fallback_kick", "&cCould not connect to a default or fallback server, please try again later: {0}", configuration);
    setIfUnexistant("fallback_lobby", "&cCould not connect to target server, you have been moved to a fallback server.", configuration);
    setIfUnexistant("lost_connection", "[Proxy] Lost connection to server.", configuration);
    setIfUnexistant("mojang_fail", "Error occurred while contacting login servers, are they down?", configuration);
    setIfUnexistant("no_permission", "&cYou do not have permission to execute this command!", configuration);
    setIfUnexistant("no_server", "&cThe specified server does not exist.", configuration);
    setIfUnexistant("no_server_permission", "&cYou don't have permission to access this server.", configuration);
    setIfUnexistant("outdated_client", "Outdated client! Please use {0}", configuration);
    setIfUnexistant("outdated_server", "Outdated server! I'm still on {0}", configuration);
    setIfUnexistant("proxy_full", "Server is full!", configuration);
    setIfUnexistant("restart", "[Proxy] Proxy restarting.", configuration);
    setIfUnexistant("server_list", "&6You may connect to the following servers at this time: ", configuration);
    setIfUnexistant("server_went_down", "&cThe server you were previously on went down, you have been connected to a fallback server", configuration);
    setIfUnexistant("total_players", "Total players online: {0}", configuration);
    setIfUnexistant("name_invalid", "Username contains invalid characters.", configuration);
    setIfUnexistant("ping_cannot_connect", "&c[Bungee] Can't connect to server.", configuration);
    setIfUnexistant("offline_mode_player", "Not authenticated with Minecraft.net", configuration);
    setIfUnexistant("secure_profile_required", "A secure profile is required to join this server.", configuration);
    setIfUnexistant("secure_profile_expired", "Secure profile expired.", configuration);
    setIfUnexistant("secure_profile_invalid", "Secure profile invalid.", configuration);
    setIfUnexistant("message_needed", "&cYou must supply a message.", configuration);
    setIfUnexistant("error_occurred_player", "&cAn error occurred while parsing your message. (Hover for details)", configuration);
    setIfUnexistant("error_occurred_console", "&cAn error occurred while parsing your message: {0}", configuration);
    setIfUnexistant("server_command_hover_players", "{0} players", configuration);
    setIfUnexistant("click_to_connect", "Click to connect to the server", configuration);
    setIfUnexistant("username_needed", "&cPlease follow this command by a user name.", configuration);
    setIfUnexistant("user_not_online", "&cThat user is not online.", configuration);
    setIfUnexistant("user_online_at", "&a{0} &ris online at {1}", configuration);
    setIfUnexistant("send_cmd_usage", "&cNot enough arguments, usage: /send <server|player|all|current> <target>", configuration);
    setIfUnexistant("player_only", "&cOnly in game players can use this command", configuration);
    setIfUnexistant("you_got_summoned", "&6Summoned to {0} by {1}", configuration);
    setIfUnexistant("command_perms_groups", "&6You have the following groups: {0}", configuration);
    setIfUnexistant("command_perms_permission", "&9- {0}", configuration);
    setIfUnexistant("command_ip", "&9IP of {0} is {1}", configuration);
    setIfUnexistant("illegal_chat_characters", "&cIllegal characters in chat ({0})", configuration);
    setIfUnexistant("antibot_accounts", "&c&lFlameCord\n\n&cYou have too many accounts! ({0})\n\n&cError? Contact us on discord.gg/gF36AT3", configuration);
    setIfUnexistant("antibot_fastchat", "&c&lFlameCord\n\n&cYou are chatting too fast!\n\n&cError? Contact us on discord.gg/gF36AT3", configuration);
    setIfUnexistant("antibot_firewall", "&c&lFlameCord\n\n&cYou are blocked from this server!\n\n&cError? Contact us on discord.gg/gF36AT3", configuration);
    setIfUnexistant("antibot_nickname", "&c&lFlameCord\n\n&cYour nickname was detected as bot! ({0})\n\n&cError? Contact us on discord.gg/gF36AT3", configuration);
    setIfUnexistant("antibot_password", "&c&lFlameCord\n\n&cYour password is used by other players! ({0})\n\n&cError? Contact us on discord.gg/gF36AT3", configuration);
    setIfUnexistant("antibot_ratelimit", "&c&lFlameCord\n\n&cYou are connecting too fast! ({0})\n\n&cError? Contact us on discord.gg/gF36AT3", configuration);
    setIfUnexistant("antibot_reconnect", "&c&lFlameCord\n\n&cReconnect {0} more times to enter!\n&cRefresh {1} more times to enter!\n\n&cError? Contact us on discord.gg/gF36AT3", configuration);
    setIfUnexistant("antibot_country", "&c&lFlameCord\n\n&cYour country {0} is blacklisted!\n\n&cError? Contact us on discord.gg/gF36AT3", configuration);
    setIfUnexistant("antibot_proxy", "&c&lFlameCord\n\n&cYou are using a Proxy/VPN! ({0})\n\n&cError? Contact us on discord.gg/gF36AT3", configuration);
    setIfUnexistant("antibot_stats", "&c&lFlameCord Antibot Stats\n &7■ Total Pings: &a{0}\n &7■ Total Connections: &b{1}\n\n &7■ Current Pings: &a{2}\n &7■ Current Connections: &b{3}", configuration);
    setIfUnexistant("flamecord_firewall_help", "&c/flamecord firewall <add/remove> <ip>", configuration);
    setIfUnexistant("flamecord_firewall_add", "&cThe ip {0} was added to the firewall!", configuration);
    setIfUnexistant("flamecord_firewall_remove", "&cThe ip {0} was removed from the firewall!", configuration);
    setIfUnexistant("flamecord_bplugins_nopermission", "&cYou don't have permission to do this!", configuration);
    setIfUnexistant("flamecord_bplugins_separator", ", ", configuration);
    setIfUnexistant("flamecord_bplugins_header", "&aPlugins ({0}): ", configuration);
    setIfUnexistant("flamecord_bip_nopermission", "&cYou don't have permission to do this!", configuration);
    setIfUnexistant("flamecord_bip_offline", "&cThe player is not online!", configuration);
    setIfUnexistant("flamecord_bip_usage", "&c/bip <player>", configuration);
    setIfUnexistant("flamecord_bip", "&aInformation about {0}&a:\n&aUUID: &b{1}\n&aIP: &b{2}\n&aPing: &b{3}ms\n&aLocale: &b{4}\n&aView Distance: &b{5}\n&aCurrent Server: &b{6}", configuration);
    setIfUnexistant("command_list_format", "&aServers:&r", configuration);
    for (String key : configuration.getKeys()) {
      Object value = configuration.get(key);
      if (value instanceof String)
        this.messages.put(key, ChatColor.translateAlternateColorCodes('&', (String)value)); 
    } 
    save(configuration, configurationFile);
  }
  
  public String getTranslation(String name, Object... args) {
    if (!this.messages.containsKey(name))
      this.logger.warning("[FlameCord] Tried to get translation '" + name + "' from messages.yml file but wasn't found. Please try resetting this file or report to a developer."); 
    return MessageFormat.format(this.messages.getOrDefault(name, "<translation '" + name + "' missing>"), args);
  }
}
