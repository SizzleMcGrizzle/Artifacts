package me.sizzlemcgrizzle.artifacts;

import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import de.craftlancer.core.LambdaRunnable;
import me.sizzlemcgrizzle.artifacts.artifacts.Artifact;
import me.sizzlemcgrizzle.artifacts.artifacts.MagneticMaul;
import me.sizzlemcgrizzle.artifacts.artifacts.ManaRegistry;
import me.sizzlemcgrizzle.artifacts.artifacts.MeridianScepter;
import me.sizzlemcgrizzle.artifacts.artifacts.ReapersScythe;
import me.sizzlemcgrizzle.artifacts.artifacts.WindBlade;
import me.sizzlemcgrizzle.artifacts.charger.Charger;
import me.sizzlemcgrizzle.artifacts.charger.ChargerListener;
import me.sizzlemcgrizzle.artifacts.command.ArtifactsCommandGroup;
import me.sizzlemcgrizzle.artifacts.command.SetModelDataCommand;
import me.sizzlemcgrizzle.artifacts.settings.Settings;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ArtifactsPlugin extends JavaPlugin {
    
    public static ArtifactsPlugin instance;
    public static String ADMIN_PERMISSION = "artifacts.admin";
    public static String DEFAULT_PERMISSION = "artifacts.default";
    
    private static File ARTIFACTS_FILE;
    
    private List<Artifact> artifacts = new ArrayList<>();
    private List<Charger> chargers = new ArrayList<>();
    
    private ManaRegistry manaRegistry;
    private RegionContainer container;
    private ProtocolManager protocolManager;
    
    @Override
    public void onLoad() {
        PowerArtifactsFlag.registerFlag();
        protocolManager = ProtocolLibrary.getProtocolManager();
    }
    
    @Override
    public void onEnable() {
        instance = this;
        ARTIFACTS_FILE = new File(this.getDataFolder(), "artifacts.yml");
        ConfigurationSerialization.registerClass(Charger.class);
        Settings.load();
        
        this.manaRegistry = new ManaRegistry();
        
        registerArtifacts();
        
        Bukkit.getPluginManager().registerEvents(new PowerArtifactsFlag(), this);
        Bukkit.getPluginManager().registerEvents(new ChargerListener(), this);
        
        getCommand("artifacts").setExecutor(new ArtifactsCommandGroup(this));
        getCommand("setmodeldata").setExecutor(new SetModelDataCommand());
        
        
        chargers = Charger.registerChargers();
        
        this.container = WorldGuard.getInstance().getPlatform().getRegionContainer();
        
        new LambdaRunnable(manaRegistry::run).runTaskTimer(this, 4, 4);
    }
    
    @Override
    public void onDisable() {
        saveArtifacts();
        Charger.saveChargers(chargers);
    }
    
    private void registerArtifacts() {
        if (!ARTIFACTS_FILE.exists())
            saveResource(ARTIFACTS_FILE.getName(), true);
        
        YamlConfiguration config = YamlConfiguration.loadConfiguration(ARTIFACTS_FILE);
        
        ConfigurationSection windBladeSection = config.getConfigurationSection("windblade");
        ConfigurationSection reapersScytheSection = config.getConfigurationSection("reapersscythe");
        ConfigurationSection magneticMaulSection = config.getConfigurationSection("magneticmaul");
        ConfigurationSection meridianScepterSection = config.getConfigurationSection("meridianscepter");
        
        artifacts.add(new WindBlade(windBladeSection.getName(),
                Material.valueOf(windBladeSection.getString("type")),
                windBladeSection.getInt("normalDataNumber"),
                windBladeSection.getInt("poweredDataNumber"), manaRegistry));
        
        artifacts.add(new ReapersScythe(reapersScytheSection.getName(),
                Material.valueOf(reapersScytheSection.getString("type")),
                reapersScytheSection.getInt("normalDataNumber"),
                reapersScytheSection.getInt("poweredDataNumber"), manaRegistry));
        
        artifacts.add(new MagneticMaul(magneticMaulSection.getName(),
                Material.valueOf(magneticMaulSection.getString("type")),
                magneticMaulSection.getInt("normalDataNumber"),
                magneticMaulSection.getInt("poweredDataNumber"), manaRegistry));
        artifacts.add(new MeridianScepter(meridianScepterSection.getName(),
                Material.valueOf(meridianScepterSection.getString("type")),
                meridianScepterSection.getInt("normalDataNumber"),
                meridianScepterSection.getInt("poweredDataNumber"), manaRegistry));
        
        artifacts.forEach(artifact -> Bukkit.getPluginManager().registerEvents(artifact, this));
        
    }
    
    private void saveArtifacts() {
        if (!ARTIFACTS_FILE.exists())
            saveResource(ARTIFACTS_FILE.getName(), true);
        
        artifacts.forEach(artifact -> {
            YamlConfiguration config = YamlConfiguration.loadConfiguration(ARTIFACTS_FILE);
            
            ConfigurationSection section = config.getConfigurationSection(artifact.getName());
            
            if (section == null) {
                config.createSection(artifact.getName());
                section = config.getConfigurationSection(artifact.getName());
            }
            
            section.set("type", artifact.getType().toString());
            section.set("normalDataNumber", artifact.getNormalDataNumber());
            section.set("poweredDataNumber", artifact.getPoweredDataNumber());
            
            try {
                config.save(ARTIFACTS_FILE);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }
    
    
    public boolean isPoweredArtifactRegion(Location location) {
        RegionManager manager = container.get(BukkitAdapter.adapt(location.getWorld()));
        manager.getApplicableRegions(BlockVector3.at(location.getX(), location.getY(), location.getZ()));
        
        boolean bool = false;
        for (ProtectedRegion region : manager.getApplicableRegions(BlockVector3.at(location.getX(), location.getY(), location.getZ()))) {
            StateFlag.State state = region.getFlag(PowerArtifactsFlag.POWER_ARTIFACTS_FLAG);
            if (state == StateFlag.State.ALLOW) {
                bool = true;
                break;
            }
        }
        return bool;
    }
    
    public ProtocolManager getProtocolManager() {
        return protocolManager;
    }
    
    public List<Artifact> getArtifacts() {
        return artifacts;
    }
    
    public List<Charger> getChargers() {
        return chargers;
    }
    
    public void addCharger(Charger charger) {
        chargers.add(charger);
    }
    
    public void removeCharger(Charger charger) {
        charger.getStructure().forEach(location -> location.getBlock().setType(Material.AIR));
        chargers.remove(charger);
    }
    
    public static void tell(Player player, String message) {
        player.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
    }
    
    public static String colorize(String message) {
        return ChatColor.translateAlternateColorCodes('&', message);
    }
    
    public ManaRegistry getManaRegistry() {
        return manaRegistry;
    }
}
