package me.sizzlemcgrizzle.artifacts.command;

import de.craftlancer.core.Utils;
import de.craftlancer.core.command.SubCommand;
import me.sizzlemcgrizzle.artifacts.ArtifactsPlugin;
import me.sizzlemcgrizzle.artifacts.artifacts.Artifact;
import me.sizzlemcgrizzle.artifacts.settings.Settings;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class ArtifactsChangeModelDataCommand extends SubCommand {
    
    protected ArtifactsChangeModelDataCommand(Plugin plugin) {
        super(ArtifactsPlugin.ADMIN_PERMISSION,
                plugin,
                false,
                "Sets the powered/unpowered model data for specified artifact to use.",
                "<artifact>", "<state>", "<modelData>");
    }
    
    @Override
    protected List<String> onTabComplete(CommandSender sender, String[] args) {
        if (args.length == 2)
            return Utils.getMatches(args[1], ArtifactsPlugin.instance.getArtifacts().stream().map(Artifact::getName).collect(Collectors.toList()));
        if (args.length == 3)
            return Utils.getMatches(args[2], Arrays.asList("POWERED", "UNPOWERED"));
        if (args.length == 4)
            return Collections.singletonList("#");
        
        return Collections.emptyList();
    }
    
    @Override
    protected String execute(CommandSender sender, Command command, String s, String[] args) {
        
        if (!(sender instanceof Player))
            return null;
        
        if (args.length < 2)
            return ArtifactsPlugin.colorize(Settings.PREFIX + "&cPlease enter an artifact type.");
        if (args.length < 3)
            return ArtifactsPlugin.colorize(Settings.PREFIX + "&cPlease enter a state.");
        if (args.length < 4)
            return ArtifactsPlugin.colorize(Settings.PREFIX + "&cPlease enter a model data number.");
        
        if (ArtifactsPlugin.instance.getArtifacts().stream().noneMatch(artifact -> artifact.getName().equalsIgnoreCase(args[1])))
            return ArtifactsPlugin.colorize(Settings.PREFIX + "&cPlease enter a valid artifact type!");
        
        try {
            Artifact artifact = ArtifactsPlugin.instance.getArtifacts().stream().filter(a -> a.getName().equalsIgnoreCase(args[1])).findFirst().get();
            int data = Integer.parseInt(args[3]);
            
            if (args[2].equalsIgnoreCase("powered"))
                artifact.setPoweredDataNumber(data);
            else if (args[2].equalsIgnoreCase("unpowered"))
                artifact.setNormalDataNumber(data);
            else
                return ArtifactsPlugin.colorize(Settings.PREFIX + "&cPlease enter a valid second argument (use tab complete)");
            
            
        } catch (NumberFormatException e) {
            return ArtifactsPlugin.colorize(Settings.PREFIX + "&cYou must enter a number in the third argument!");
        }
        
        return ArtifactsPlugin.colorize(Settings.PREFIX + "&aCustom model data successfully set.");
        
    }
    
    @Override
    public void help(CommandSender commandSender) {
    
    }
}
