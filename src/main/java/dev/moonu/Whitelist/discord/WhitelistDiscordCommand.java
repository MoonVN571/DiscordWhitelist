package dev.moonu.Whitelist.discord;

import dev.moonu.Whitelist.Main;
import dev.moonu.Whitelist.MessageManager;
import dev.moonu.Whitelist.WhitelistManager;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.bukkit.OfflinePlayer;

import java.util.*;

/**
 * Handles Discord whitelist commands
 */
public class WhitelistDiscordCommand implements DiscordCommand {
    private final Main plugin;
    private final MessageManager messageManager;
    private final WhitelistManager whitelistManager;
    private final Map<String, Boolean> authorizedUserIds;
    
    public WhitelistDiscordCommand(Main plugin, MessageManager messageManager, 
                                 WhitelistManager whitelistManager, Map<String, Boolean> authorizedUserIds) {
        this.plugin = plugin;
        this.messageManager = messageManager;
        this.whitelistManager = whitelistManager;
        this.authorizedUserIds = authorizedUserIds;
    }
    
    @Override
    public void execute(MessageReceivedEvent event, String[] args) {
        if (args.length < 1) {
            sendInvalidUsage(event);
            return;
        }
        
        String action = args[0].toLowerCase();
        
        switch (action) {
            case "add":
                handleAdd(event, args);
                break;
            case "remove":
                handleRemove(event, args);
                break;
            case "list":
                handleList(event);
                break;
            case "help":
                handleHelp(event);
                break;
            default:
                sendInvalidUsage(event);
                break;
        }
    }
    
    @Override
    public String getName() {
        return "whitelist";
    }
    
    @Override
    public String[] getAliases() {
        return new String[]{"wl"};
    }
    
    @Override
    public String getDescription() {
        return "Manage server whitelist";
    }
    
    @Override
    public String getUsage() {
        return "whitelist <add|remove|list|help> [username]";
    }
    
    @Override
    public boolean hasPermission(MessageReceivedEvent event) {
        return authorizedUserIds.containsKey(event.getAuthor().getId());
    }
    
    /**
     * Handle add subcommand
     */
    private void handleAdd(MessageReceivedEvent event, String[] args) {
        if (args.length != 2) {
            sendInvalidUsage(event);
            return;
        }
        
        String username = args[1];
        boolean added = whitelistManager.addToWhitelist(username);
        
        if (added) {
            whitelistManager.logWhitelistAction("ADD", username, 
                event.getAuthor().getName(), event.getAuthor().getId());
            
            Map<String, String> placeholders = new HashMap<>();
            placeholders.put("player", username);
            event.getChannel().sendMessage(messageManager.getMessage("discord.player_added", placeholders)).queue();
        } else {
            Map<String, String> placeholders = new HashMap<>();
            placeholders.put("player", username);
            event.getChannel().sendMessage(messageManager.getMessage("discord.player_already_whitelisted", placeholders)).queue();
        }
    }
    
    /**
     * Handle remove subcommand
     */
    private void handleRemove(MessageReceivedEvent event, String[] args) {
        if (args.length != 2) {
            sendInvalidUsage(event);
            return;
        }
        
        String username = args[1];
        boolean removed = whitelistManager.removeFromWhitelist(username);
        
        if (removed) {
            whitelistManager.logWhitelistAction("REMOVE", username, 
                event.getAuthor().getName(), event.getAuthor().getId());
            
            Map<String, String> placeholders = new HashMap<>();
            placeholders.put("player", username);
            event.getChannel().sendMessage(messageManager.getMessage("discord.player_removed", placeholders)).queue();
        } else {
            Map<String, String> placeholders = new HashMap<>();
            placeholders.put("player", username);
            event.getChannel().sendMessage(messageManager.getMessage("discord.player_not_whitelisted", placeholders)).queue();
        }
    }
    
    /**
     * Handle list subcommand
     */
    private void handleList(MessageReceivedEvent event) {
        String list = String.join(", ", whitelistManager.getWhitelistedPlayers().stream()
                .map(OfflinePlayer::getName)
                .filter(Objects::nonNull)
                .toList());
        
        Map<String, String> placeholders = new HashMap<>();
        placeholders.put("list", list.isEmpty() ? "No players whitelisted" : list);
        event.getChannel().sendMessage(messageManager.getMessage("discord.whitelist_list", placeholders)).queue();
    }
    
    /**
     * Handle help subcommand
     */
    private void handleHelp(MessageReceivedEvent event) {
        Map<String, String> placeholders = new HashMap<>();
        placeholders.put("prefix", plugin.getCommandPrefix());
        event.getChannel().sendMessage(messageManager.getMessage("discord.help_message", placeholders)).queue();
    }
    
    /**
     * Send invalid usage message
     */
    private void sendInvalidUsage(MessageReceivedEvent event) {
        Map<String, String> placeholders = new HashMap<>();
        placeholders.put("prefix", plugin.getCommandPrefix());
        event.getChannel().sendMessage(messageManager.getMessage("discord.invalid_command", placeholders)).queue();
    }
}
