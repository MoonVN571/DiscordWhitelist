package dev.moonu.Whitelist;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.Map;

/**
 * Handles all message loading and formatting for the plugin
 */
public class MessageManager {
    private final JavaPlugin plugin;
    private FileConfiguration config;
    private FileConfiguration localeConfig;
    
    public MessageManager(JavaPlugin plugin) {
        this.plugin = plugin;
    }
    
    /**
     * Load configuration and locale files
     * @param config The main plugin configuration
     * @param locale The locale to load (e.g., "en", "vi")
     */
    public void loadConfiguration(FileConfiguration config, String locale) {
        this.config = config;
        loadLocaleFile(locale);
    }
    
    /**
     * Load the locale file based on the specified locale
     * @param locale The locale to load
     */
    private void loadLocaleFile(String locale) {
        File localeFile = new File(plugin.getDataFolder(), "locale_" + locale + ".yml");
        if (localeFile.exists()) {
            localeConfig = YamlConfiguration.loadConfiguration(localeFile);
        } else {
            plugin.getLogger().warning("Locale file not found: " + localeFile.getName() + ". Using default messages.");
            localeConfig = null;
        }
    }
    
    /**
     * Get a message from the configuration with placeholder replacement
     * @param path The config path to the message
     * @param placeholders Key-value pairs for placeholder replacement
     * @return The formatted message
     */
    public String getMessage(String path, Map<String, String> placeholders) {
        String message;
        
        // For console messages, use hardcoded English messages
        if (path.startsWith("console.")) {
            message = getConsoleMessage(path);
        } else {
            // Try to get from locale file first, then fall back to config
            if (localeConfig != null && localeConfig.contains("messages." + path)) {
                message = localeConfig.getString("messages." + path);
            } else {
                message = config.getString("messages." + path, "Message not found: " + path);
            }
        }
        
        if (placeholders != null) {
            for (Map.Entry<String, String> entry : placeholders.entrySet()) {
                message = message.replace("{" + entry.getKey() + "}", entry.getValue());
            }
        }
        return message;
    }
    
    /**
     * Get a message from the configuration without placeholders
     * @param path The config path to the message
     * @return The message
     */
    public String getMessage(String path) {
        return getMessage(path, null);
    }
    
    /**
     * Get hardcoded console messages in English
     * @param path The message path
     * @return The console message
     */
    private String getConsoleMessage(String path) {
        switch (path) {
            case "console.bot_ready":
                return "Successfully logged in as {botname}!";
            case "console.player_added_log":
                return "{player} has been added to server whitelist.";
            case "console.player_removed_log":
                return "{player} has been removed from server whitelist.";
            case "console.token_missing":
                return "Discord bot token is missing in config.yml";
            case "console.bot_start_failed":
                return "Failed to start Discord bot: {error}";
            case "console.log_setup_success":
                return "Whitelist logging has been set up successfully";
            case "console.log_file_creation_failed":
                return "Could not create whitelist_log.txt: {error}";
            case "console.log_write_failed":
                return "Failed to write to whitelist_log.txt: {error}";
            default:
                return "Console message not found: " + path;
        }
    }
}
