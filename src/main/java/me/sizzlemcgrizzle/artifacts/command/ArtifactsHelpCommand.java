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

import java.util.Collections;
import java.util.List;
import java.util.Map;

public class ArtifactsHelpCommand extends HelpCommand {
    
    private static final String MESSAGE_LINE = ChatColor.DARK_GRAY + "+-------------------------------------------+";
    private static final String NEW_LINE = "\n";
    
    private final String commandName;
    
    private Plugin plugin;
    private Map<String, SubCommand> commands;
    
    public ArtifactsHelpCommand(Plugin plugin, Map<String, SubCommand> map, String commandName) {
        super(ArtifactsPlugin.DEFAULT_PERMISSION, plugin, map);
        
        this.commandName = "/" + commandName + " ";
        this.commands = map;
        this.plugin = plugin;
    }
    
    @Override
    public void help(CommandSender sender) {
        if (!(sender instanceof Player))
            return;
        
        Player player = (Player) sender;
        
        ComponentBuilder componentBuilder = new ComponentBuilder();
        
        componentBuilder.append(ChatColor.translateAlternateColorCodes('&', MESSAGE_LINE));
        componentBuilder.append(NEW_LINE);
        componentBuilder.append(getPluginColor() + "  " + plugin.getDescription().getName() + ChatColor.GRAY + " " + plugin.getDescription().getVersion());
        componentBuilder.append(NEW_LINE);
        componentBuilder.append(NEW_LINE);
        commands.entrySet().stream().filter(entry -> entry.getValue().getPermission().isEmpty() || player.hasPermission(entry.getValue().getPermission())).forEach(entry -> {
            componentBuilder.append(ChatColor.GRAY + "  - " + ChatColor.GOLD + commandName + entry.getKey());
            componentBuilder.event(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                    new ComponentBuilder(ChatColor.YELLOW + "Click to suggest command " + ChatColor.GOLD + commandName + entry.getKey()).create()));
            componentBuilder.event(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, commandName + entry.getKey() + " "));
            componentBuilder.append(NEW_LINE);
        });
        componentBuilder.append(NEW_LINE);
        componentBuilder.append(ChatColor.translateAlternateColorCodes('&', MESSAGE_LINE));
        
        player.spigot().sendMessage(componentBuilder.create());
        
        if (playSound())
            player.playSound(player.getLocation(), getSound(), 0.5F, 1F);
    }
    
    public String getPluginColor() {
        return ChatColor.DARK_PURPLE + "" + ChatColor.BOLD;
    }
    
    private boolean playSound() {
        return true;
    }
    
    private Sound getSound() {
        return Sound.BLOCK_NOTE_BLOCK_BASS;
    }
    
    @Override
    protected List<String> onTabComplete(CommandSender sender, String[] args) {
        return Collections.emptyList();
    }
}
