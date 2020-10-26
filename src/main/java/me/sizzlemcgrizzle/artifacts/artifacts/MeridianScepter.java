package me.sizzlemcgrizzle.artifacts.artifacts;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketContainer;
import me.sizzlemcgrizzle.artifacts.ArtifactsPlugin;
import me.sizzlemcgrizzle.artifacts.settings.Settings;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.FluidCollisionMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.RayTraceResult;
import org.bukkit.util.Vector;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

public class MeridianScepter extends Artifact {
    
    private static final Particle.DustOptions PARTICLE = new Particle.DustOptions(Color.BLACK, 2);
    private Map<Arrow, BukkitTask> arrowMap = new HashMap<>();
    
    public MeridianScepter(String name, Material type, int normalDataNumber, int poweredDataNumber, ManaRegistry registry) {
        super(name, type, normalDataNumber, poweredDataNumber, registry);
    }
    
    @Override
    public void onRightClick(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        
        if (!getManaRegistry().take(player.getUniqueId(), Settings.MERIDIAN_SCEPTER_MANA_PER_USE)) {
            ArtifactsPlugin.tell(player, Settings.PREFIX + "&eYou are out of energy! Walk into a charger to regain energy!");
            return;
        }
        
        RayTraceResult result = player.getWorld().rayTrace(player.getEyeLocation(), player.getLocation().getDirection(),
                30,
                FluidCollisionMode.NEVER,
                true,
                5,
                e -> e instanceof Player && !e.getUniqueId().equals(player.getUniqueId()) && !e.isDead());
        
        Arrow arrow = player.launchProjectile(Arrow.class);
        
        PacketContainer arrowPacketContainer = new PacketContainer(PacketType.Play.Server.ENTITY_DESTROY);
        arrowPacketContainer.getIntegerArrays().write(0, new int[]{arrow.getEntityId()});
        Bukkit.getOnlinePlayers().forEach(p -> {
            try {
                ArtifactsPlugin.getInstance().getProtocolManager().sendServerPacket(p, arrowPacketContainer);
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
        });
        
        arrow.getWorld().playSound(arrow.getLocation(), Sound.ENTITY_WITHER_SHOOT, 0.5F, 1F);
        
        arrowMap.put(arrow, new BukkitRunnable() {
            
            private int tickID = 0;
            private Location location;
            
            @Override
            public void run() {
                tickID++;
                
                if (tickID % 300 == 0) {
                    arrow.remove();
                    arrowMap.remove(arrow);
                    cancel();
                    return;
                }
                if (tickID == 1 || tickID % 10 == 0)
                    arrow.getWorld().playSound(arrow.getLocation(), Sound.ENTITY_BLAZE_AMBIENT, 0.2F, 1F);
                if (location == null)
                    location = player.getLocation().clone().add(0, -0.5, 0);
                
                Vector vector;
                if (result == null || result.getHitEntity() == null) {
                    vector = location.add(location.getDirection().multiply(20)).toVector();
                } else
                    vector = result.getHitEntity().getLocation().add(0, 2, 0).clone().toVector();
                
                Vector v = vector.subtract(arrow.getLocation().toVector());
                arrow.setVelocity(v.normalize().multiply(0.25));
                arrow.getWorld().spawnParticle(Particle.REDSTONE, arrow.getLocation(), 1, PARTICLE);
            }
        }.runTaskTimer(ArtifactsPlugin.getInstance(), 0, 1));
        
    }
    
    @EventHandler(ignoreCancelled = true)
    public void onArrowHit(ProjectileHitEvent event) {
        
        if (!(event.getEntity() instanceof Arrow))
            return;
        
        Arrow projectile = (Arrow) event.getEntity();
        
        if (!arrowMap.containsKey(projectile))
            return;
        
        event.getEntity().remove();
        
        arrowMap.get(projectile).cancel();
        arrowMap.remove(projectile);
        
        if (event.getHitEntity() == null || !(event.getHitEntity() instanceof Player))
            return;
        
        Player player = (Player) event.getHitEntity();
        Location hitLocation = player.getLocation();
        
        hitLocation.getWorld().strikeLightningEffect(hitLocation);
        hitLocation.getWorld().playSound(player.getLocation(), Sound.ENTITY_LIGHTNING_BOLT_IMPACT, 0.7F, 1F);
        player.damage(5);
    }
}
