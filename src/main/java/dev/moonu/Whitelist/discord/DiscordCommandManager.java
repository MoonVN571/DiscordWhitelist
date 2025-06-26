package dev.moonu.Whitelist.discord;

import dev.moonu.Whitelist.Main;
import dev.moonu.Whitelist.MessageManager;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.events.session.ReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.util.*;

/**
 * Manages and routes Discord commands
 */
public class DiscordCommandManager extends ListenerAdapter {
    private final Main plugin;
    private final MessageManager messageManager;
    private final String commandPrefix;
    private final Map<String, DiscordCommand> commands;
    private final Map<String, String> aliases;
    
    public DiscordCommandManager(Main plugin, MessageManager messageManager, String commandPrefix) {
        this.plugin = plugin;
        this.messageManager = messageManager;
        this.commandPrefix = commandPrefix;
        this.commands = new HashMap<>();
        this.aliases = new HashMap<>();
    }
    
    /**
     * Register all Discord commands
     */
    public void registerCommands(Map<String, Boolean> authorizedRoleIds) {
        // Register whitelist command
        WhitelistDiscordCommand whitelistCommand = new WhitelistDiscordCommand(
            plugin, messageManager, plugin.getWhitelistManager(), authorizedRoleIds);
        registerCommand(whitelistCommand);
        
        plugin.getLogger().info("Registered " + commands.size() + " Discord commands");
    }
    
    /**
     * Register a Discord command
     */
    private void registerCommand(DiscordCommand command) {
        commands.put(command.getName().toLowerCase(), command);
        
        // Register aliases
        for (String alias : command.getAliases()) {
            aliases.put(alias.toLowerCase(), command.getName().toLowerCase());
        }
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
        String content = message.getContentRaw().trim();
        
        // Check if message starts with command prefix
        if (!content.startsWith(commandPrefix)) return;
        
        // Parse command and arguments
        String[] parts = content.substring(commandPrefix.length()).split("\\s+");
        if (parts.length == 0) return;
        
        String commandName = parts[0].toLowerCase();
        String[] args = Arrays.copyOfRange(parts, 1, parts.length);
        
        // Check for alias
        if (aliases.containsKey(commandName)) {
            commandName = aliases.get(commandName);
        }
        
        // Find and execute command
        DiscordCommand command = commands.get(commandName);
        if (command == null) return;
        
        // Check permissions
        if (!command.hasPermission(event)) {
            event.getChannel().sendMessage(messageManager.getMessage("discord.no_permission")).queue();
            return;
        }
        
        // Execute command
        try {
            command.execute(event, args);
        } catch (Exception e) {
            plugin.getLogger().severe("Error executing Discord command '" + commandName + "': " + e.getMessage());
            event.getChannel().sendMessage(messageManager.getMessage("discord.command_error")).queue();
        }
    }
    
    /**
     * Get all registered commands
     */
    public Map<String, DiscordCommand> getCommands() {
        return Collections.unmodifiableMap(commands);
    }
    
    /**
     * Get command prefix
     */
    public String getCommandPrefix() {
        return commandPrefix;
    }
}
