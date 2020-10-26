package me.sizzlemcgrizzle.artifacts.artifacts;

import me.sizzlemcgrizzle.artifacts.ArtifactsPlugin;
import me.sizzlemcgrizzle.artifacts.settings.Settings;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class ReapersScythe extends Artifact {
    
    private Map<Player, Double> playerDamageMap = new HashMap<>();
    
    public ReapersScythe(String name, Material type, int normalModelData, int poweredModelData, ManaRegistry registry) {
        super(name, type, normalModelData, poweredModelData, registry);
    }
    
    @EventHandler(ignoreCancelled = true)
    public void onPlayerHit(EntityDamageByEntityEvent event) {
        if (!(event.getDamager() instanceof Player))
            return;
        
        EntityDamageEvent.DamageCause cause = event.getCause();
        Entity victim = event.getEntity();
        Player damager = (Player) event.getDamager();
        
        if (!isPoweredArtifact(damager.getInventory().getItemInMainHand()))
            return;
        
        if (cause == EntityDamageEvent.DamageCause.ENTITY_SWEEP_ATTACK)
            event.setDamage(0);
        
        if (cause != EntityDamageEvent.DamageCause.ENTITY_ATTACK)
            return;
        
        playerDamageMap.put(damager, event.getFinalDamage());
        
        Collection<Entity> entities = damager.getWorld().getNearbyEntities(victim.getLocation(), 1.5, 1.5, 1.5, e -> {
            if (!(e instanceof LivingEntity))
                return false;
            if (e.equals(damager) || e.equals(victim))
                return false;
            if (e.isDead())
                return false;
            return e instanceof Player || e instanceof Monster;
        });
        
        
        if (entities.size() == 0)
            return;
        
        if (!getManaRegistry().take(damager.getUniqueId(), Settings.REAPERS_SCYTHE_MANA_PER_USE / ((double) entities.size() / 10))) {
            ArtifactsPlugin.tell(damager, Settings.PREFIX + "&eYou are out of energy! Walk into a charger to regain energy!");
            return;
        }
        
        damager.getWorld().playSound(damager.getLocation(), Sound.ENTITY_WITHER_HURT, 0.5F, 1F);
        
        for (Entity entity : entities) {
            LivingEntity e = (LivingEntity) entity;
            e.damage(playerDamageMap.get(damager));
            e.getWorld().spawnParticle(Particle.DAMAGE_INDICATOR, e.getLocation().clone().add(0, 2, 0), 1);
            
            damager.setHealth(Double.min(damager.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue(), damager.getHealth() + 0.25 * playerDamageMap.get(damager)));
            damager.getWorld().spawnParticle(Particle.HEART, damager.getLocation().clone().add(Math.random() - 0.5, Math.random() * 2 + 1.5, Math.random() - 0.5), 1);
        }
        
        
    }
}
