package me.sizzlemcgrizzle.artifacts.artifacts;

import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

public class ReapersScythe extends Artifact {
    
    public ReapersScythe(String name, Material type, int customModelDataInt, int customModelDataPoweredInt) {
        super(name, type, customModelDataInt, customModelDataPoweredInt);
    }
    
    @EventHandler
    public void onPlayerHit(EntityDamageByEntityEvent event) {
        if (!(event.getDamager() instanceof Player))
            return;
        
        Player damager = (Player) event.getDamager();
        
        if (!isPoweredArtifact(damager.getInventory().getItemInMainHand()))
            return;
        
        damager.playSound(damager.getLocation(), Sound.ENTITY_WITHER_HURT, 0.5F, 1F);
        damager.setHealth(Double.min(20, damager.getHealth() + 0.5));
        
        
    }
}
