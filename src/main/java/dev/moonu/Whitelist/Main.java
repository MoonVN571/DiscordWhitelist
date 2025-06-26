package dev.moonu.Whitelist;

import dev.moonu.Whitelist.commands.GameCommandLoader;
import dev.moonu.Whitelist.discord.DiscordCommandManager;
import net.dv8tion.jda.api.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class Main extends JavaPlugin {
    private final Map<String, Boolean> authorizedRoleIds = new HashMap<>();
    private String commandPrefix;
    private FileConfiguration config;
    
    // Manager classes
    private MessageManager messageManager;
    private WhitelistManager whitelistManager;
    private DiscordBotManager discordBotManager;
    private DiscordCommandManager discordCommandManager;
    private GameCommandLoader gameCommandLoader;

    @Override
    public void onEnable() {
        // Initialize managers
        messageManager = new MessageManager(this);
        whitelistManager = new WhitelistManager(this, messageManager);
        discordBotManager = new DiscordBotManager(this, messageManager);
        discordCommandManager = new DiscordCommandManager(this, messageManager, "!");
        gameCommandLoader = new GameCommandLoader(this);
        
        loadConfiguration();
        startDiscordBot();
        registerCommands();
    }

    @Override
    public void onDisable() {
        if (discordBotManager != null) {
            discordBotManager.shutdown();
        }
    }

    private void loadConfiguration() {
        saveDefaultConfig();
        config = getConfig();

        commandPrefix = config.getString("discord.prefix", "!");
        String locale = config.getString("locale", "en");
        
        // Load messages with the specified locale
        messageManager.loadConfiguration(config, locale);
        
        authorizedRoleIds.clear();
        for (String roleId : config.getStringList("discord.authorized_roles")) {
            authorizedRoleIds.put(roleId, true);
        }
    }

    private void startDiscordBot() {
        String token = config.getString("discord.token");
        
        // Register Discord commands
        discordCommandManager.registerCommands(authorizedRoleIds);
        
        // Start the bot
        discordBotManager.startBot(token, discordCommandManager);
    }
    
    private void registerCommands() {
        // Register game commands
        gameCommandLoader.registerCommands();
    }
    
    /**
     * Reload the plugin configuration and restart components
     */
    public void reloadPlugin() {
        // Shutdown Discord bot
        if (discordBotManager != null) {
            discordBotManager.shutdown();
        }
        
        // Reload configuration
        reloadConfig();
        loadConfiguration();
        
        // Restart Discord bot
        startDiscordBot();
    }

    
    // Getter methods for managers
    public MessageManager getMessageManager() {
        return messageManager;
    }
    
    public WhitelistManager getWhitelistManager() {
        return whitelistManager;
    }
    
    public DiscordBotManager getDiscordBotManager() {
        return discordBotManager;
    }
    
    public String getCommandPrefix() {
        return commandPrefix;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        // Commands are now handled by GameCommandLoader
        return false;
    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        // Tab completion is now handled by GameCommandLoader
        return Collections.emptyList();
    }
}