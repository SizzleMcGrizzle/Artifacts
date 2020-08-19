package me.sizzlemcgrizzle.artifacts.artifacts;

import me.sizzlemcgrizzle.artifacts.ArtifactsPlugin;
import me.sizzlemcgrizzle.artifacts.settings.Settings;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.util.Vector;

import java.util.List;
import java.util.stream.Collectors;

public class MagneticMaul extends Artifact {
    
    public MagneticMaul(String name, Material type, int customModelDataInt, int customModelDataPoweredInt) {
        super(name, type, customModelDataInt, customModelDataPoweredInt);
    }
    
    @Override
    public void onRightClick(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        
        if (!ArtifactsPlugin.instance.getManaRegistry().hasMana(player.getUniqueId())) {
            ArtifactsPlugin.tell(player, Settings.PREFIX + "&eYou are out of energy! Walk into a charger to regain energy!");
            return;
        }
        
        List<Player> inRangePlayers = Bukkit.getOnlinePlayers().stream().filter(p ->
                p != player
                        && !p.isDead()
                        && player.hasLineOfSight(p)
                        && p.getLocation().distanceSquared(player.getLocation()) <= 25).collect(Collectors.toList());
        
        if (inRangePlayers.size() == 0)
            return;
        
        ArtifactsPlugin.instance.getManaRegistry().take(player.getUniqueId(), inRangePlayers.size() * 100);
        player.getWorld().playSound(player.getLocation(), Sound.ENTITY_BAT_LOOP, 0.1F, 2F);
        
        for (Player p : inRangePlayers) {
            Vector vector = player.getLocation().toVector().subtract(p.getLocation().toVector());
            p.setVelocity(vector.multiply(0.1));
            p.getWorld().spawnParticle(Particle.CRIT_MAGIC, p.getLocation(), 5);
        }
    }
}
