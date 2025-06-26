package dev.moonu.Whitelist;

import dev.moonu.Whitelist.discord.DiscordCommandManager;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.cache.CacheFlag;

import java.util.HashMap;
import java.util.Map;

/**
 * Manages the Discord bot connection and initialization
 */
public class DiscordBotManager {
    private final Main plugin;
    private final MessageManager messageManager;
    private JDA jda;
    
    public DiscordBotManager(Main plugin, MessageManager messageManager) {
        this.plugin = plugin;
        this.messageManager = messageManager;
    }
    
    /**
     * Start the Discord bot with the given configuration
     * @param token The Discord bot token
     * @param commandManager The command manager to register
     */
    public void startBot(String token, DiscordCommandManager commandManager) {
        if (token == null || token.isEmpty()) {
            plugin.getLogger().severe(messageManager.getMessage("console.token_missing"));
            return;
        }
        
        try {
            jda = JDABuilder.createDefault(token,
                            GatewayIntent.GUILD_MESSAGES,
                            GatewayIntent.MESSAGE_CONTENT)
                    .disableCache(CacheFlag.VOICE_STATE, CacheFlag.EMOJI, CacheFlag.STICKER, CacheFlag.SCHEDULED_EVENTS)
                    .addEventListeners(commandManager)
                    .build();
        } catch (Exception e) {
            Map<String, String> placeholders = new HashMap<>();
            placeholders.put("error", e.getMessage());
            plugin.getLogger().severe(messageManager.getMessage("console.bot_start_failed", placeholders));
        }
    }
    
    /**
     * Shutdown the Discord bot
     */
    public void shutdown() {
        if (jda != null) {
            jda.shutdown();
        }
    }
    
    /**
     * Get the JDA instance
     * @return The JDA instance
     */
    public JDA getJDA() {
        return jda;
    }
    
    /**
     * Check if the bot is connected and ready
     * @return true if connected, false otherwise
     */
    public boolean isConnected() {
        return jda != null && jda.getStatus() == JDA.Status.CONNECTED;
    }
}
