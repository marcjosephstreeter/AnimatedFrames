package org.inventivetalent.animatedframes;

import lombok.NonNull;
import org.apache.commons.lang.enums.Enum;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.*;

import java.util.*;

import static org.inventivetalent.animatedframes.AnimatedFramesPlugin.ShowImgName;

public class ToggleViewEntityCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            ShowImgName = !ShowImgName;
            World world = Bukkit.getServer().getWorld("world");
            Location location = player.getLocation();

            Set<Entity> entities = Collections.newSetFromMap(new IdentityHashMap<>());
            for (Entity entity : world.getEntities()) {
                world.getNearbyEntities(location, 10, 10, 10).forEach(nearbyEntity -> {

               if(nearbyEntity instanceof ArmorStand) {
                   if (nearbyEntity.getCustomName() != null){
                       if (!(nearbyEntity.getCustomName().equals("Armour Stand"))) {
                           nearbyEntity.setCustomNameVisible(ShowImgName);

                       }
                   }

                    }
                    });


            }
        }else {
            System.out.println("you are not allowed to use this command!");
        }
        return true;
    }
}
