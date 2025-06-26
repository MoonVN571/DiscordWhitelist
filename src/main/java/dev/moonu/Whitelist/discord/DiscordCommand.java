package dev.moonu.Whitelist.discord;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

/**
 * Interface for Discord command handlers
 */
public interface DiscordCommand {
    
    /**
     * Execute the Discord command
     * @param event The message received event
     * @param args Command arguments (excluding the command name)
     */
    void execute(MessageReceivedEvent event, String[] args);
    
    /**
     * Get the command name (e.g., "whitelist", "wl")
     * @return Command name
     */
    String getName();
    
    /**
     * Get command aliases
     * @return Array of command aliases
     */
    String[] getAliases();
    
    /**
     * Get command description
     * @return Command description
     */
    String getDescription();
    
    /**
     * Get command usage
     * @return Command usage string
     */
    String getUsage();
    
    /**
     * Check if the user has permission to use this command
     * @param event The message received event
     * @return true if user has permission
     */
    boolean hasPermission(MessageReceivedEvent event);
}
