package dev.moonu.Whitelist;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.events.session.ReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.bukkit.OfflinePlayer;

import java.util.*;

/**
 * Handles Discord bot events and commands
 */
public class DiscordCommandHandler extends ListenerAdapter {
    private final Main plugin;
    private final MessageManager messageManager;
    private final WhitelistManager whitelistManager;
    private final Map<String, Boolean> authorizedUserIds;
    private final String commandPrefix;
    
    public DiscordCommandHandler(Main plugin, MessageManager messageManager, WhitelistManager whitelistManager, 
                               Map<String, Boolean> authorizedUserIds, String commandPrefix) {
        this.plugin = plugin;
        this.messageManager = messageManager;
        this.whitelistManager = whitelistManager;
        this.authorizedUserIds = authorizedUserIds;
        this.commandPrefix = commandPrefix;
    }
    
    @Override
    public void onReady(ReadyEvent event) {
        Map<String, String> placeholders = new HashMap<>();
        placeholders.put("botname", event.getJDA().getSelfUser().getName());
        plugin.getLogger().info(messageManager.getMessage("console.bot_ready", placeholders));
    }
    
    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        if (event.getAuthor().isBot()) return;
        
        Message message = event.getMessage();
        String content = message.getContentRaw().trim().toLowerCase();
        String[] fullArgs = content.split("\\s+");
        
        if (fullArgs.length < 2) return;
        
        // Check if it starts with a command prefix and is a whitelist command
        if (!content.startsWith(commandPrefix)) return;
        
        String command = fullArgs[0].substring(commandPrefix.length()).toLowerCase();
        if (!command.equals("wl") && !command.equals("whitelist")) return;
        
        // Check authorization
        if (!isAuthorized(event)) {
            event.getChannel().sendMessage(messageManager.getMessage("discord.no_permission")).queue();
            return;
        }
        
        String action = fullArgs[1].toLowerCase();
        List<String> args = Arrays.asList(fullArgs).subList(2, fullArgs.length);
        
        // Handle different commands
        switch (action) {
            case "add":
                handleAddCommand(event, args);
                break;
            case "remove":
                handleRemoveCommand(event, args);
                break;
            case "list":
                handleListCommand(event);
                break;
            default:
                handleInvalidCommand(event);
                break;
        }
    }
    
    /**
     * Check if the user is authorized to use whitelist commands
     * @param event The message event
     * @return true if authorized, false otherwise
     */
    private boolean isAuthorized(MessageReceivedEvent event) {
        return this.authorizedUserIds.containsKey(event.getAuthor().getId());
    }
    
    /**
     * Handle the add command
     * @param event The message event
     * @param args The command arguments
     */
    private void handleAddCommand(MessageReceivedEvent event, List<String> args) {
        if (args.size() != 1) {
            handleInvalidCommand(event);
            return;
        }
        
        String username = args.get(0);
        boolean added = whitelistManager.addToWhitelist(username);
        
        if (added) {
            whitelistManager.logWhitelistAction("ADD", username, event.getAuthor().getName(), event.getAuthor().getId());
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
     * Handle the remove command
     * @param event The message event
     * @param args The command arguments
     */
    private void handleRemoveCommand(MessageReceivedEvent event, List<String> args) {
        if (args.size() != 1) {
            handleInvalidCommand(event);
            return;
        }
        
        String username = args.get(0);
        boolean removed = whitelistManager.removeFromWhitelist(username);
        
        if (removed) {
            whitelistManager.logWhitelistAction("REMOVE", username, event.getAuthor().getName(), event.getAuthor().getId());
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
     * Handle the list command
     * @param event The message event
     */
    private void handleListCommand(MessageReceivedEvent event) {
        String list = String.join(", ", whitelistManager.getWhitelistedPlayers().stream()
                .map(OfflinePlayer::getName)
                .filter(Objects::nonNull)
                .toList());
        
        Map<String, String> placeholders = new HashMap<>();
        placeholders.put("list", list.isEmpty() ? "No players whitelisted" : list);
        event.getChannel().sendMessage(messageManager.getMessage("discord.whitelist_list", placeholders)).queue();
    }
    
    /**
     * Handle invalid commands
     * @param event The message event
     */
    private void handleInvalidCommand(MessageReceivedEvent event) {
        Map<String, String> placeholders = new HashMap<>();
        placeholders.put("prefix", commandPrefix);
        event.getChannel().sendMessage(messageManager.getMessage("discord.invalid_command", placeholders)).queue();
    }
}
