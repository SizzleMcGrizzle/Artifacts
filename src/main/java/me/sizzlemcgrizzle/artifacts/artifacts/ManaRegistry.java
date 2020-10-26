package me.sizzlemcgrizzle.artifacts.artifacts;

import me.sizzlemcgrizzle.artifacts.ArtifactsPlugin;
import me.sizzlemcgrizzle.artifacts.charger.Charger;
import me.sizzlemcgrizzle.artifacts.settings.Settings;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ManaRegistry {
    
    private Map<UUID, Double> playerManaMap = new HashMap<>();
    private long tickID = 0;
    
    public void run() {
        tickID += 4;
        ArtifactsPlugin.getInstance().getChargers().stream().filter(Charger::isLoaded).forEach(charger -> Bukkit.getOnlinePlayers().stream()
                .filter(p -> playerManaMap.containsKey(p.getUniqueId()) && playerManaMap.get(p.getUniqueId()) != Settings.MAX_MANA && charger.containsLocation(p.getLocation()))
                .forEach(p -> {
                    World world = charger.getBaseLocation().getWorld();
                    add(p.getUniqueId(), 100);
                    world.spawnParticle(Particle.FIREWORKS_SPARK, charger.getBaseLocation().clone().add(0.5, 2, 0.5), 10);
                    
                    if (tickID % 40 == 0) {
                        world.strikeLightningEffect(charger.getBaseLocation().clone().add(0.5, 0, 0.5));
                        world.playSound(charger.getBaseLocation(), Sound.ENTITY_LIGHTNING_BOLT_THUNDER, 0.5F, 1.0F);
                        world.playSound(charger.getBaseLocation(), Sound.BLOCK_BEACON_AMBIENT, 2F, 1.0F);
                    }
                }));
    }
    
    /**
     * @return true if successful, false if player doesn't have enough mana
     */
    public boolean take(UUID uuid, double mana) {
        if (playerManaMap.containsKey(uuid) && playerManaMap.get(uuid) < mana)
            return false;
        playerManaMap.put(uuid, playerManaMap.containsKey(uuid) ? Double.max(0, playerManaMap.get(uuid) - mana) : Settings.MAX_MANA - mana);
        displayActionBar(Bukkit.getPlayer(uuid));
        return true;
    }
    
    public void add(UUID uuid, double mana) {
        playerManaMap.put(uuid, playerManaMap.containsKey(uuid) ? Double.min(Settings.MAX_MANA, playerManaMap.get(uuid) + mana) : Settings.MAX_MANA);
        displayActionBar(Bukkit.getPlayer(uuid));
    }
    
    private void displayActionBar(Player player) {
        double progress = playerManaMap.get(player.getUniqueId()) / Settings.MAX_MANA;
        
        ComponentBuilder progressBar = new ComponentBuilder("[ ").color(ChatColor.DARK_GRAY).bold(true);
        
        for (int i = 0; i < 24; ++i) {
            progressBar.append("▌");
            
            if ((double) ((float) i / 24.0F) >= progress)
                progressBar.color(ChatColor.RED);
            else
                progressBar.color(ChatColor.GREEN);
            
            if (i == 11)
                progressBar.append(ChatColor.BOLD + " [").color(ChatColor.DARK_GRAY)
                        .append(Math.round((progress * 100)) + "%").color(ChatColor.DARK_PURPLE).bold(true)
                        .append("] ").color(ChatColor.DARK_GRAY);
        }
        
        progressBar.append(" ]").color(ChatColor.DARK_GRAY).bold(true);
        
        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(progressBar.create()));
    }
}
