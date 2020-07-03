package me.sizzlemcgrizzle.artifacts.artifacts;

import me.sizzlemcgrizzle.artifacts.ArtifactsPlugin;
import me.sizzlemcgrizzle.artifacts.charger.Charger;
import me.sizzlemcgrizzle.artifacts.settings.Settings;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.HashMap;
import java.util.UUID;

public class WindBlade extends Artifact {
    //Maximum flight time in milliseconds
    private static final long MAX_FLIGHT_TIME = 4000;
    
    private HashMap<UUID, Long> cooldownMap = new HashMap<>();
    
    public WindBlade(String name, Material type, int normalDataNumber, int poweredDataNumber) {
        super(name, type, normalDataNumber, poweredDataNumber);
        
        run();
    }
    
    @Override
    public void run() {
        ArtifactsPlugin.instance.getChargers().stream().filter(Charger::isLoaded).forEach(charger -> {
            World world = charger.getBaseLocation().getWorld();
            cooldownMap.entrySet().stream().filter(entry -> entry.getValue() < MAX_FLIGHT_TIME).forEach(e -> {
                Player player = Bukkit.getPlayer(e.getKey());
                
                if (player == null || !player.isOnline())
                    return;
                
                if (charger.containsLocation(player.getLocation()) && isPoweredArtifact(player.getInventory().getItemInMainHand())) {
                    long time = e.getValue() + 25;
                    cooldownMap.replace(e.getKey(), Long.min(MAX_FLIGHT_TIME, time));
                    
                    displayActionBar(player);
                    world.spawnParticle(Particle.FIREWORKS_SPARK, charger.getBaseLocation().clone().add(0.5, 2, 0.5), 10);
                    
                    if (time % 500 == 0) {
                        world.strikeLightningEffect(charger.getBaseLocation().clone().add(0.5, 0, 0.5));
                        world.playSound(charger.getBaseLocation(), Sound.ENTITY_LIGHTNING_BOLT_THUNDER, 0.5F, 1.0F);
                        world.playSound(charger.getBaseLocation(), Sound.BLOCK_BEACON_AMBIENT, 2F, 1.0F);
                    }
                    
                }
            });
        });
    }
    
    @Override
    public void onRightClick(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        
        
        if (cooldownMap.containsKey(player.getUniqueId()) && cooldownMap.get(player.getUniqueId()) <= 0) {
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
        
        cooldownMap.compute(player.getUniqueId(), (a, b) -> cooldownMap.containsKey(a) ? b - 200 : MAX_FLIGHT_TIME - 200);
        displayActionBar(player);
    }
    
    private void displayActionBar(Player player) {
        double progress = (double) cooldownMap.get(player.getUniqueId()) / MAX_FLIGHT_TIME;
        
        ComponentBuilder progressBar = new ComponentBuilder();
        
        for (int i = 0; i < 24; ++i) {
            progressBar.append("▌");
            
            if ((double) ((float) i / 24.0F) >= progress)
                progressBar.color(ChatColor.RED);
            else
                progressBar.color(ChatColor.GREEN);
        }
        
        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(progressBar.create()));
    }
    
}
