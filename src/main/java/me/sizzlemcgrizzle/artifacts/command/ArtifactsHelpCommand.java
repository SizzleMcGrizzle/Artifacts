package me.sizzlemcgrizzle.artifacts.command;

import de.craftlancer.core.command.HelpCommand;
import de.craftlancer.core.command.SubCommand;
import me.sizzlemcgrizzle.artifacts.ArtifactsPlugin;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import org.bukkit.Sound;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.Map;

public class ArtifactsHelpCommand extends HelpCommand {
    
    private Map<String, SubCommand> commands;
    
    public ArtifactsHelpCommand(Plugin plugin, Map<String, SubCommand> map) {
        super(ArtifactsPlugin.DEFAULT_PERMISSION, plugin, map);
        
        this.commands = map;
    }
    
    @Override
    public void help(CommandSender sender) {
        if (!(sender instanceof Player))
            return;
        
        Player player = (Player) sender;
        
        ComponentBuilder componentBuilder = new ComponentBuilder();
        
        componentBuilder.append(ChatColor.translateAlternateColorCodes('&', ArtifactsPlugin.MESSAGE_LINE));
        componentBuilder.append("\n");
        componentBuilder.append("\n");
        commands.entrySet().stream().filter(entry -> entry.getValue().getPermission().isEmpty() || player.hasPermission(entry.getValue().getPermission())).forEach(entry -> {
            componentBuilder.append(ChatColor.GRAY + "  - " + ChatColor.GOLD + "/" + "artifacts" + " " + entry.getKey());
            componentBuilder.event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(ChatColor.YELLOW + "Click to run command " + ChatColor.GOLD + "/" + "artifacts" + " " + entry.getKey()).create()));
            componentBuilder.event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/" + "artifacts" + " " + entry.getKey()));
            componentBuilder.append("\n");
        });
        componentBuilder.append("\n");
        componentBuilder.append(ChatColor.translateAlternateColorCodes('&', ArtifactsPlugin.MESSAGE_LINE));
        
        player.spigot().sendMessage(componentBuilder.create());
        player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_BASS, 0.5F, 1F);
    }
}
