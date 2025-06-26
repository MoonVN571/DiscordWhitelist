package dev.moonu.Whitelist.commands;

import dev.moonu.Whitelist.Main;
import dev.moonu.Whitelist.MessageManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.*;

/**
 * Handles the /discordwhitelist command
 */
public class DiscordWhitelistCommand implements GameCommandHandler {
    private final Main plugin;
    private final MessageManager messageManager;
    private static final List<String> SUB_COMMANDS = Arrays.asList("reload", "status");
    
    public DiscordWhitelistCommand(Main plugin, MessageManager messageManager) {
        this.plugin = plugin;
        this.messageManager = messageManager;
    }
    
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!command.getName().equalsIgnoreCase("discordwhitelist")) {
            return false;
        }
        
        if (args.length == 0) {
            sender.sendMessage(messageManager.getMessage("game.command_usage"));
            return true;
        }
        
        String subCommand = args[0].toLowerCase();
        
        switch (subCommand) {
            case "reload":
                return handleReload(sender);
            case "status":
                return handleStatus(sender);
            default:
                sender.sendMessage(messageManager.getMessage("game.unknown_subcommand"));
                return true;
        }
    }
    
    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!command.getName().equalsIgnoreCase("discordwhitelist")) {
            return Collections.emptyList();
        }
        
        if (args.length == 1) {
            List<String> completions = new ArrayList<>();
            String partial = args[0].toLowerCase();
            
            for (String subCommand : SUB_COMMANDS) {
                if (subCommand.toLowerCase().startsWith(partial)) {
                    completions.add(subCommand);
                }
            }
            return completions;
        }
        
        return Collections.emptyList();
    }
    
    @Override
    public String getCommandName() {
        return "discordwhitelist";
    }
    
    /**
     * Handle the reload subcommand
     */
    private boolean handleReload(CommandSender sender) {
        if (!sender.hasPermission("discordwhitelist.admin.reload")) {
            sender.sendMessage(messageManager.getMessage("game.no_permission"));
            return true;
        }
        
        try {
            plugin.reloadPlugin();
            sender.sendMessage(messageManager.getMessage("game.reload_success"));
        } catch (Exception e) {
            sender.sendMessage(messageManager.getMessage("game.reload_failed"));
            plugin.getLogger().severe("Failed to reload plugin: " + e.getMessage());
        }
        return true;
    }
    
    /**
     * Handle the status subcommand
     */
    private boolean handleStatus(CommandSender sender) {
        if (!sender.hasPermission("discordwhitelist.admin.status")) {
            sender.sendMessage(messageManager.getMessage("game.no_permission"));
            return true;
        }
        
        boolean botConnected = plugin.getDiscordBotManager().isConnected();
        Map<String, String> placeholders = new HashMap<>();
        placeholders.put("status", botConnected ? "Connected" : "Disconnected");
        
        sender.sendMessage(messageManager.getMessage("game.bot_status", placeholders));
        return true;
    }
}
