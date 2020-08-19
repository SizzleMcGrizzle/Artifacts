package me.sizzlemcgrizzle.artifacts.command;

import de.craftlancer.core.command.CommandHandler;
import org.bukkit.plugin.Plugin;

public class ArtifactsCommandGroup extends CommandHandler {
    
    public ArtifactsCommandGroup(Plugin plugin, String commandName) {
        super(plugin);
        
        registerSubCommand("changeModelData", new ArtifactsChangeModelDataCommand(plugin));
    }
}
