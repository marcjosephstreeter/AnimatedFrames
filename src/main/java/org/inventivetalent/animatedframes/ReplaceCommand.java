package org.inventivetalent.animatedframes;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ReplaceCommand implements CommandExecutor {

    AnimatedFramesPlugin plugin;

    public ReplaceCommand(AnimatedFramesPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (sender instanceof Player) {
            Player player = (Player) sender;
            if (player.hasPermission("replacecommand.replace")) {
                if (args.length > 2) {
                    player.sendMessage(ChatColor.RED + "you have put too many arguments!");
                    return false;
                } else if (args.length == 0) {
                    player.sendMessage(ChatColor.RED + "you must give arguments!");
                    return false;
                }
                if (args.length == 2) {
                    player.chat("/afremove " + args[0]);
                    Bukkit.getScheduler().runTaskLater(plugin, () -> player.chat("/afcreate " + args[0] + " " + args[1]), 20);

                }
            }


            return true;
        }
        return true;
    }
}


