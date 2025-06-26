package dev.moonu.Whitelist.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Interface for all in-game command handlers
 */
public interface GameCommandHandler {
    
    /**
     * Execute the command
     * @param sender Command sender
     * @param command Command object
     * @param label Command label
     * @param args Command arguments
     * @return true if command was handled successfully
     */
    boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args);
    
    /**
     * Handle tab completion
     * @param sender Command sender
     * @param command Command object
     * @param label Command label
     * @param args Command arguments
     * @return List of tab completion suggestions
     */
    List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args);
    
    /**
     * Get the command name this handler manages
     * @return Command name
     */
    String getCommandName();
}
