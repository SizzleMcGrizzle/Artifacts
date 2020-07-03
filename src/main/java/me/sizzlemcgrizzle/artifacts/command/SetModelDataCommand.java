package me.sizzlemcgrizzle.artifacts.command;

import me.sizzlemcgrizzle.artifacts.ArtifactsPlugin;
import me.sizzlemcgrizzle.artifacts.settings.Settings;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

public class SetModelDataCommand implements CommandExecutor {
    
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        
        if (!(sender instanceof Player))
            return false;
        
        Player player = (Player) sender;
        
        if (!player.hasPermission(ArtifactsPlugin.ADMIN_PERMISSION)) {
            ArtifactsPlugin.tell(player, Settings.PREFIX + "&cYou do not have access to this command.");
            return false;
        }
        
        if (args.length == 0) {
            ArtifactsPlugin.tell(player, Settings.PREFIX + "&cYou must enter a model data number!");
            return false;
        }
        
        if (player.getInventory().getItemInMainHand().getType() == Material.AIR) {
            player.sendMessage(Settings.PREFIX + "&cPlease hold an item in your main hand.");
            return false;
        }
        
        try {
            ItemMeta meta = player.getInventory().getItemInMainHand().getItemMeta();
            meta.setCustomModelData(Integer.valueOf(args[0]));
            player.getInventory().getItemInMainHand().setItemMeta(meta);
            player.updateInventory();
        } catch (NumberFormatException e) {
            ArtifactsPlugin.tell(player, Settings.PREFIX + "&cYou must enter a number!");
        }
        
        ArtifactsPlugin.tell(player, Settings.PREFIX + "&aYour item has been given a custom model data of " + args[0] + ".");
        
        return true;
    }
}
