package me.sizzlemcgrizzle.artifacts.artifacts;

import me.sizzlemcgrizzle.artifacts.ArtifactsPlugin;
import me.sizzlemcgrizzle.artifacts.settings.Settings;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

public class WindBlade extends Artifact {
    
    public WindBlade(String name, Material type, int normalDataNumber, int poweredDataNumber, ManaRegistry registry) {
        super(name, type, normalDataNumber, poweredDataNumber, registry);
    }
    
    @Override
    public void onRightClick(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        
        
        if (!getManaRegistry().take(player.getUniqueId(), 500)) {
            ArtifactsPlugin.tell(player, Settings.PREFIX + "&eYou are out of energy! Walk into a charger to regain energy!");
            return;
        }
        
        Vector vec = player.getLocation().getDirection();
        
        if (Double.isNaN(vec.getX()) && Double.isNaN(vec.getY()) && Double.isNaN(vec.getZ()) && vec.length() == 0.0D)
            return;
        
        vec.normalize();
        vec.multiply(1.2);
        
        player.playSound(player.getLocation(), Sound.BLOCK_FIRE_EXTINGUISH, 0.1F, 1F);
        Particle.DustOptions particle = new Particle.DustOptions(Color.WHITE, 10F);
        new BukkitRunnable() {
            
            int counter = 0;
            
            @Override
            public void run() {
                if (counter == 5)
                    cancel();
                if (counter == 2)
                    player.playSound(player.getLocation(), Sound.BLOCK_FIRE_EXTINGUISH, 0.1F, 1F);
                player.getWorld().spawnParticle(Particle.REDSTONE, player.getLocation(), 5, particle);
                counter++;
            }
        }.runTaskTimer(ArtifactsPlugin.instance, 0, 1);
        
        player.setVelocity(vec);
    }
    
}
