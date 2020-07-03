package me.sizzlemcgrizzle.artifacts.charger;

import me.sizzlemcgrizzle.artifacts.ArtifactsPlugin;
import org.bukkit.Location;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.util.BoundingBox;

import javax.annotation.Nonnull;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Charger implements ConfigurationSerializable {
    
    private BoundingBox chargeRegion;
    private Location baseLocation;
    private List<Location> structure;
    
    public Charger(Location baseLocation, List<Location> structure) {
        this.baseLocation = baseLocation;
        this.structure = structure;
        
        chargeRegion = new BoundingBox(
                baseLocation.getX() + 1, baseLocation.getY() - 2,
                baseLocation.getZ() + 1, baseLocation.getX(),
                baseLocation.getY() + 3, baseLocation.getZ());
    }
    
    public Charger(Map<String, Object> map) {
        this.baseLocation = (Location) map.get("base");
        this.structure = (List<Location>) map.get("structure");
        
        chargeRegion = new BoundingBox(
                baseLocation.getX() + 1, baseLocation.getY() - 2,
                baseLocation.getZ() + 1, baseLocation.getX(),
                baseLocation.getY() + 3, baseLocation.getZ());
    }
    
    public BoundingBox getChargeRegion() {
        return chargeRegion;
    }
    
    public List<Location> getStructure() {
        return structure;
    }
    
    public Location getBaseLocation() {
        return baseLocation;
    }
    
    public boolean containsLocation(Location location) {
        return chargeRegion.contains(location.getX(), location.getY(), location.getZ());
    }
    
    public boolean isLoaded() {
        return baseLocation.getWorld().isChunkLoaded(baseLocation.getBlockX() >> 4, baseLocation.getBlockZ() >> 4);
    }
    
    @Override
    public @Nonnull
    Map<String, Object> serialize() {
        Map<String, Object> map = new HashMap<>();
        
        map.put("base", baseLocation);
        map.put("structure", structure);
        
        return map;
    }
    
    public static List<Charger> registerChargers() {
        File chargersFile = new File(ArtifactsPlugin.instance.getDataFolder(), "chargers.yml");
        YamlConfiguration config;
        
        if (!chargersFile.exists())
            ArtifactsPlugin.instance.saveResource(chargersFile.getName(), true);
        
        config = YamlConfiguration.loadConfiguration(chargersFile);
        
        return (List<Charger>) config.getList("chargers");
        
    }
    
    public static void saveChargers(List<Charger> chargers) {
        File chargersFile = new File(ArtifactsPlugin.instance.getDataFolder(), "chargers.yml");
        YamlConfiguration config;
        
        if (!chargersFile.exists())
            ArtifactsPlugin.instance.saveResource(chargersFile.getName(), true);
        
        config = YamlConfiguration.loadConfiguration(chargersFile);
        
        config.set("chargers", chargers);
        
        try {
            config.save(chargersFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
}
