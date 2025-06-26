package dev.moonu.Whitelist.commands;

import dev.moonu.Whitelist.Main;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

/**
 * Manages and loads all game command handlers
 */
public class GameCommandLoader implements CommandExecutor, TabCompleter {
    private final Main plugin;
    private final Map<String, GameCommandHandler> commandHandlers;
    
    public GameCommandLoader(Main plugin) {
        this.plugin = plugin;
        this.commandHandlers = new HashMap<>();
    }
    
    /**
     * Register all command handlers
     */
    public void registerCommands() {
        // Register the discordwhitelist command
        DiscordWhitelistCommand discordWhitelistCommand = new DiscordWhitelistCommand(plugin, plugin.getMessageManager());
        registerCommand(discordWhitelistCommand);
        
        // Set this class as the executor for all registered commands
        for (String commandName : commandHandlers.keySet()) {
            Objects.requireNonNull(plugin.getCommand(commandName)).setExecutor(this);
            Objects.requireNonNull(plugin.getCommand(commandName)).setTabCompleter(this);
        }
        
        plugin.getLogger().info("Registered " + commandHandlers.size() + " game commands");
    }
    
    /**
     * Register a command handler
     */
    private void registerCommand(GameCommandHandler handler) {
        commandHandlers.put(handler.getCommandName().toLowerCase(), handler);
    }
    
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        GameCommandHandler handler = commandHandlers.get(command.getName().toLowerCase());
        
        if (handler != null) {
            return handler.onCommand(sender, command, label, args);
        }
        
        return false;
    }
    
    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        GameCommandHandler handler = commandHandlers.get(command.getName().toLowerCase());
        
        if (handler != null) {
            return handler.onTabComplete(sender, command, label, args);
        }
        
        return Collections.emptyList();
    }
    
    /**
     * Get all registered command handlers
     */
    public Map<String, GameCommandHandler> getCommandHandlers() {
        return Collections.unmodifiableMap(commandHandlers);
    }
}
