package me.sizzlemcgrizzle.artifacts.settings;

import me.sizzlemcgrizzle.artifacts.ArtifactsPlugin;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;

public class Settings {
    
    public static String PREFIX;
    public static int MAX_MANA;
    
    private static File SETTINGS_FILE = new File(ArtifactsPlugin.instance.getDataFolder(), "settings.yml");
    
    public static void load() {
        if (!SETTINGS_FILE.exists())
            ArtifactsPlugin.instance.saveResource(SETTINGS_FILE.getName(), true);
        
        YamlConfiguration config = YamlConfiguration.loadConfiguration(SETTINGS_FILE);
        
        PREFIX = config.getString("Prefix");
        MAX_MANA = config.getInt("Max_Mana");
    }
}
