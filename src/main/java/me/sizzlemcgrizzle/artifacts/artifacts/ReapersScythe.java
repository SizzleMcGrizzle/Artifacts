package me.sizzlemcgrizzle.artifacts.artifacts;

import me.sizzlemcgrizzle.artifacts.ArtifactsPlugin;
import me.sizzlemcgrizzle.artifacts.settings.Settings;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ReapersScythe extends Artifact {
    
    private Map<Player, Double> playerDamageMap = new HashMap<>();
    
    public ReapersScythe(String name, Material type, int customModelDataInt, int customModelDataPoweredInt) {
        super(name, type, customModelDataInt, customModelDataPoweredInt);
    }
    
    @EventHandler(ignoreCancelled = true)
    public void onPlayerHit(EntityDamageByEntityEvent event) {
        if (!(event.getDamager() instanceof Player) || !(event.getEntity() instanceof Player))
            return;
        
        EntityDamageEvent.DamageCause cause = event.getCause();
        Player victim = (Player) event.getEntity();
        Player damager = (Player) event.getDamager();
        
        if (!isPoweredArtifact(damager.getInventory().getItemInMainHand()))
            return;
        
        if (cause == EntityDamageEvent.DamageCause.ENTITY_SWEEP_ATTACK)
            event.setDamage(0);
        
        if (cause != EntityDamageEvent.DamageCause.ENTITY_ATTACK)
            return;
        
        if (!ArtifactsPlugin.instance.getManaRegistry().hasMana(damager.getUniqueId())) {
            ArtifactsPlugin.tell(damager, Settings.PREFIX + "&eYou are out of energy! Walk into a charger to regain energy!");
            return;
        }
        
        playerDamageMap.put(damager, event.getFinalDamage());
        
        List<Player> inRangePlayers = Bukkit.getOnlinePlayers().stream().filter(player -> player != damager && player != victim && !player.isDead() && player.getLocation().distanceSquared(victim.getLocation()) <= 9).collect(Collectors.toList());
        
        if (inRangePlayers.size() == 0)
            return;
        
        ArtifactsPlugin.instance.getManaRegistry().take(damager.getUniqueId(), 2 / inRangePlayers.size() * 200);
        
        for (Player player : inRangePlayers) {
            player.damage(playerDamageMap.get(damager));
            player.getWorld().spawnParticle(Particle.DAMAGE_INDICATOR, player.getLocation().clone().add(0, 2, 0), 1);
            
            damager.setHealth(Double.min(damager.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue(), damager.getHealth() + 0.25 * playerDamageMap.get(damager)));
            damager.getWorld().spawnParticle(Particle.HEART, damager.getLocation().clone().add(0, 2, 0), 10);
            damager.getWorld().playSound(damager.getLocation(), Sound.ENTITY_WITHER_HURT, 0.5F, 1F);
        }
        
        
    }
}
