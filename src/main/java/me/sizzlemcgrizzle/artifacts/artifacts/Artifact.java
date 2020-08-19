package me.sizzlemcgrizzle.artifacts.artifacts;

import org.bukkit.Material;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public abstract class Artifact implements Listener {
    
    private String name;
    private Material type;
    private int normalDataNumber;
    private int poweredDataNumber;
    
    public Artifact(String name, Material type, int normalDataNumber, int poweredDataNumber) {
        this.name = name;
        this.type = type;
        this.normalDataNumber = normalDataNumber;
        this.poweredDataNumber = poweredDataNumber;
    }
    
    //Is an item the powered version of the artifact?
    public boolean isPoweredArtifact(ItemStack item) {
        return item != null && item.getItemMeta() != null && item.getItemMeta().hasCustomModelData() && item.getItemMeta().getCustomModelData() == poweredDataNumber;
    }
    
    //The name of the artifact
    public String getName() {
        return name;
    }
    
    //The normal custom data number of an artifact
    public int getNormalDataNumber() {
        return normalDataNumber;
    }
    
    //The powered custom data number of an artifact (activated when a player goes into a
    // region with the special flag allowed.
    public int getPoweredDataNumber() {
        return poweredDataNumber;
    }
    
    public void setNormalDataNumber(int normalDataNumber) {
        this.normalDataNumber = normalDataNumber;
    }
    
    public void setPoweredDataNumber(int poweredDataNumber) {
        this.poweredDataNumber = poweredDataNumber;
    }
    
    public Material getType() {
        return type;
    }
    
    public void setType(Material type) {
        this.type = type;
    }
    
    public void onLeftClick(PlayerInteractEvent event) {
    
    }
    
    public void onRightClick(PlayerInteractEvent event) {
    
    }
}
