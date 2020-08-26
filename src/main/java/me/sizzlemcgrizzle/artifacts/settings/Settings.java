package me.sizzlemcgrizzle.artifacts.settings;

import me.sizzlemcgrizzle.artifacts.ArtifactsPlugin;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;

public class Settings {
    
    public static String PREFIX;
    public static int MAX_MANA;
    public static int WIND_BLADE_MANA_PER_USE;
    public static int REAPERS_SCYTHE_MANA_PER_USE;
    public static int MAGNETIC_MAUL_MANA_PER_USE;
    public static int MERIDIAN_SCEPTER_MANA_PER_USE;
    
    private static File SETTINGS_FILE = new File(ArtifactsPlugin.instance.getDataFolder(), "settings.yml");
    
    public static void load() {
        if (!SETTINGS_FILE.exists())
            ArtifactsPlugin.instance.saveResource(SETTINGS_FILE.getName(), true);
        
        YamlConfiguration config = YamlConfiguration.loadConfiguration(SETTINGS_FILE);
        
        PREFIX = config.getString("Prefix");
        MAX_MANA = config.getInt("Max_Mana");
        WIND_BLADE_MANA_PER_USE = config.getInt("Wind_Blade_Mana_Per_Use");
        REAPERS_SCYTHE_MANA_PER_USE = config.getInt("Reapers_Scythe_Mana_Per_Use");
        MAGNETIC_MAUL_MANA_PER_USE = config.getInt("Magnetic_Maul_Mana_Per_Use");
        MERIDIAN_SCEPTER_MANA_PER_USE = config.getInt("Meridian_Scepter_Mana_Per_Use");
    }
}
