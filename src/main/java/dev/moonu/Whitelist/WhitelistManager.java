package dev.moonu.Whitelist;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Handles all whitelist operations and logging
 */
public class WhitelistManager {
    private final Main plugin;
    private final MessageManager messageManager;
    private final File logFile;
    
    public WhitelistManager(Main plugin, MessageManager messageManager) {
        this.plugin = plugin;
        this.messageManager = messageManager;
        this.logFile = new File(plugin.getDataFolder(), "whitelist_log.txt");
        setupLogFile();
    }
    
    /**
     * Setup the log file for whitelist actions
     */
    private void setupLogFile() {
        if (!logFile.exists()) {
            try {
                boolean mkDir = logFile.getParentFile().mkdirs();
                boolean mkFile = logFile.createNewFile();
                plugin.getLogger().info(messageManager.getMessage("console.log_setup_success"));
            } catch (IOException e) {
                Map<String, String> placeholders = new HashMap<>();
                placeholders.put("error", e.getMessage());
                plugin.getLogger().severe(messageManager.getMessage("console.log_file_creation_failed", placeholders));
            }
        }
    }
    
    /**
     * Add a player to the server whitelist
     * @param username The player's username
     * @return true if the player was added, false if already whitelisted
     */
    public boolean addToWhitelist(String username) {
        OfflinePlayer player = Bukkit.getOfflinePlayer(username);
        if (!player.isWhitelisted()) {
            player.setWhitelisted(true);
            Map<String, String> placeholders = new HashMap<>();
            placeholders.put("player", username);
            plugin.getLogger().info(messageManager.getMessage("console.player_added_log", placeholders));
            return true;
        }
        return false;
    }
    
    /**
     * Remove a player from the server whitelist
     * @param username The player's username
     * @return true if the player was removed, false if not whitelisted
     */
    public boolean removeFromWhitelist(String username) {
        OfflinePlayer player = Bukkit.getOfflinePlayer(username);
        if (player.isWhitelisted()) {
            player.setWhitelisted(false);
            Map<String, String> placeholders = new HashMap<>();
            placeholders.put("player", username);
            plugin.getLogger().info(messageManager.getMessage("console.player_removed_log", placeholders));
            return true;
        }
        return false;
    }
    
    /**
     * Get all whitelisted players
     * @return Set of whitelisted players
     */
    public Set<OfflinePlayer> getWhitelistedPlayers() {
        return Bukkit.getWhitelistedPlayers();
    }
    
    /**
     * Log a whitelist action to the log file
     * @param action The action performed (ADD, REMOVE, etc.)
     * @param targetUsername The target username
     * @param authorDisplayName The author's display name
     * @param authorId The author's Discord ID
     */
    public void logWhitelistAction(String action, String targetUsername, String authorDisplayName, String authorId) {
        try (FileWriter writer = new FileWriter(logFile, true)) {
            writer.write("[" + java.time.LocalDateTime.now() + "] " + action + " " + targetUsername + " by " + authorDisplayName + " (" + authorId + ")\n");
        } catch (IOException e) {
            Map<String, String> placeholders = new HashMap<>();
            placeholders.put("error", e.getMessage());
            plugin.getLogger().severe(messageManager.getMessage("console.log_write_failed", placeholders));
        }
    }
}
