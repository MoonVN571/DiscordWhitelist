package dev.moonu.Whitelist;

import net.dv8tion.jda.api.*;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.events.session.ReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.cache.CacheFlag;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class Main extends JavaPlugin {
    private JDA jda;
    private final Map<String, Boolean> authorizedRoleIds = new HashMap<>();
    private String commandPrefix;
    private File logFile;
    private static final ArrayList<String> subCommands = new ArrayList<String>() {{
        add("reload");
    }};

    @Override
    public void onEnable() {
        loadConfiguration();
        startDiscordBot();
        setupLogFile();
    }

    @Override
    public void onDisable() {
        if (jda != null) jda.shutdown();
    }

    private void loadConfiguration() {
        saveDefaultConfig();
        FileConfiguration config = getConfig();

        commandPrefix = config.getString("discord.prefix", "a");
        authorizedRoleIds.clear();
        for (String roleId : config.getStringList("discord.authorized_roles")) {
            authorizedRoleIds.put(roleId, true);
        }
    }

    private void startDiscordBot() {
        String token = getConfig().getString("discord.token");

        if (token == null || token.isEmpty()) {
            getLogger().severe("Discord token is missing in config.yml");
            return;
        }

        try {
            jda = JDABuilder.createDefault(token,
                            GatewayIntent.GUILD_MESSAGES,
                            GatewayIntent.MESSAGE_CONTENT)
                    .disableCache(CacheFlag.VOICE_STATE, CacheFlag.EMOJI, CacheFlag.STICKER, CacheFlag.SCHEDULED_EVENTS)
                    .addEventListeners(new CommandListener())
                    .build();
        } catch (Exception e) {
            getLogger().severe("Failed to start Discord bot: " + e.getMessage());
        }
    }

    private void setupLogFile() {
        logFile = new File(getDataFolder(), "whitelist_log.txt");
        if (!logFile.exists()) {
            try {
                boolean mkDir = logFile.getParentFile().mkdirs();
                boolean mkFile = logFile.createNewFile();
                getLogger().info("Logging has been setup successful");
            } catch (IOException e) {
                getLogger().severe("Could not create whitelist_log.txt: " + e.getMessage());
            }
        }
    }

    private void logWhitelistAction(String action, String targetUsername, String authorDisplayName, String authorId) {
        try (FileWriter writer = new FileWriter(logFile, true)) {
            writer.write("[" + java.time.LocalDateTime.now() + "] " + action + " " + targetUsername + " by " + authorDisplayName + "(" + authorId + ")\n");
        } catch (IOException e) {
            getLogger().severe("Failed to write to whitelist_log.txt: " + e.getMessage());
        }
    }


    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, Command command, @NotNull String label, String[] args) {
        if(!command.getName().equalsIgnoreCase("discordwhitelist")) return null;
        if (args.length == 0) {
            return subCommands;
        } else if (args.length == 1) {
            ArrayList<String> answer = new ArrayList<>();
            for (String subCommand : subCommands) {
                if (args[0].toLowerCase().startsWith(args[0].toLowerCase()))
                    answer.add(subCommand);
            }
            return answer;
        }
        return Collections.emptyList();
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, Command command, @NotNull String label, String[] args) {
        if (command.getName().equalsIgnoreCase("discordwhitelist")) {
            if(args.length == 0) {
                sender.sendMessage("§c[DiscordWhitelist] Usage: /discordwhitelist reload");
                return false;
            }
            reloadConfig();
            loadConfiguration();
            sender.sendMessage("§a[DiscordWhitelist] Configuration reloaded.");
            return true;
        }
        return false;
    }

    public boolean addToWhitelist(String username) {
        OfflinePlayer player = Bukkit.getOfflinePlayer(username);
        if (!player.isWhitelisted()) {
            player.setWhitelisted(true);
            getLogger().info(username + " added to server whitelist.");
            return true;
        }
        return false;
    }

    public boolean removeFromWhitelist(String username) {
        OfflinePlayer player = Bukkit.getOfflinePlayer(username);
        if (player.isWhitelisted()) {
            player.setWhitelisted(false);
            getLogger().info(username + " removed from server whitelist.");
            return true;
        }
        return false;
    }

    public Set<OfflinePlayer> getWhitelistedPlayers() {
        return Bukkit.getWhitelistedPlayers();
    }

    class CommandListener extends ListenerAdapter {
        @Override
        public void onReady(ReadyEvent event) {
            getLogger().info("Logged in as " + event.getJDA().getSelfUser().getName() + "!");
        }

        @Override
        public void onMessageReceived(MessageReceivedEvent event) {
            if (event.getAuthor().isBot()) return;
            Message message = event.getMessage();
            String content = message.getContentRaw().trim().toLowerCase();
            String[] fullArgs = content.split("\\s+");

            if (fullArgs.length < 2) return;

            String command = fullArgs[0].substring(1);
            if (!command.equals("wl") && !command.equals("whitelist")) return;

            boolean isAuthorized = event.getMember() != null &&
                    event.getMember().getRoles().stream()
                            .anyMatch(role -> authorizedRoleIds.containsKey(role.getId()));

            if (!isAuthorized) {
                event.getChannel().sendMessage("❌ **|** Bạn không có quyền để sử dụng lệnh này!").queue();
                return;
            }

            String action = fullArgs[1].toLowerCase();
            List<String> args = Arrays.asList(fullArgs).subList(2, fullArgs.length);

            if (action.equals("add") && args.size() == 1) {
                String username = args.get(0);
                boolean added = addToWhitelist(username);
                if (added) {
                    logWhitelistAction("ADD", username, event.getAuthor().getName(), event.getAuthor().getId());
                    event.getChannel().sendMessage("✅ **|** Added `" + username + "` to server whitelist").queue();
                } else {
                    event.getChannel().sendMessage("ℹ️ **|** `" + username + "` is already whitelisted").queue();
                }
            } else if (action.equals("remove") && args.size() == 1) {
                String username = args.get(0);
                boolean removed = removeFromWhitelist(username);
                if (removed) {
                    logWhitelistAction("REMOVE", username, event.getAuthor().getName(), event.getAuthor().getId());
                    event.getChannel().sendMessage("❌ **|** Removed `" + username + "` from server whitelist").queue();
                } else {
                    event.getChannel().sendMessage("ℹ️ **|** `" + username + "` is not on the whitelist").queue();
                }
            } else if (action.equals("list")) {
                String list = String.join(", ", getWhitelistedPlayers().stream()
                        .map(OfflinePlayer::getName)
                        .filter(java.util.Objects::nonNull)
                        .toList());
                event.getChannel().sendMessage("📜 **|** Whitelisted Players: " + list).queue();
            } else {
                event.getChannel().sendMessage("❌ **|** Invalid command. Usage: " + commandPrefix + "whitelist <add|remove|list> [username]").queue();
            }
        }
    }
}