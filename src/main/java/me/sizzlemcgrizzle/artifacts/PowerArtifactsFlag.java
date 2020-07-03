package me.sizzlemcgrizzle.artifacts;

import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import de.craftlancer.clcapture.CLCapture;
import de.craftlancer.clcapture.CapturePoint;
import de.craftlancer.clcapture.CapturePointType;
import me.sizzlemcgrizzle.artifacts.artifacts.Artifact;
import net.raidstone.wgevents.events.RegionEnteredEvent;
import net.raidstone.wgevents.events.RegionLeftEvent;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Optional;

public class PowerArtifactsFlag implements Listener {
    
    static final StateFlag POWER_ARTIFACTS_FLAG = new StateFlag("power-artifacts", false);
    private final CLCapture clCapture = (CLCapture) Bukkit.getPluginManager().getPlugin("CLCapture");
    
    static void registerFlag() {
        WorldGuard.getInstance().getFlagRegistry().register(POWER_ARTIFACTS_FLAG);
    }
    
    @EventHandler
    public void onRegionEnter(RegionEnteredEvent event) {
        Player player = event.getPlayer();
        
        if (player == null)
            return;
        
        ProtectedRegion region = event.getRegion();
        
        StateFlag.State state = region.getFlag(POWER_ARTIFACTS_FLAG);
        
        if (state != StateFlag.State.ALLOW)
            return;
        
        if (!isPoweredPointActive())
            return;
        
        int slot = 0;
        for (ItemStack i : player.getInventory().getContents()) {
            if (i == null
                    || i.getType() == Material.AIR
                    || i.getItemMeta() == null
                    || !i.getItemMeta().hasCustomModelData()
                    || ArtifactsPlugin.instance.getArtifacts().stream().noneMatch(a -> a.getType() == i.getType())) {
                slot++;
                continue;
            }
            Optional<Artifact> optional = ArtifactsPlugin.instance.getArtifacts().stream().filter(artifact -> artifact.getNormalDataNumber() == i.getItemMeta().getCustomModelData()).findFirst();
            
            if (!optional.isPresent()) {
                slot++;
                continue;
            }
            
            ItemMeta meta = i.getItemMeta();
            meta.setCustomModelData(optional.get().getPoweredDataNumber());
            i.setItemMeta(meta);
            
            player.getInventory().setItem(slot, i);
            slot++;
        }
        
    }
    
    @EventHandler
    public void onRegionLeave(RegionLeftEvent event) {
        Player player = event.getPlayer();
        
        if (player == null)
            return;
        
        int slot = 0;
        for (ItemStack i : player.getInventory().getContents()) {
            if (i == null
                    || i.getType() == Material.AIR
                    || i.getItemMeta() == null
                    || !i.getItemMeta().hasCustomModelData()
                    || ArtifactsPlugin.instance.getArtifacts().stream().noneMatch(a -> a.getType() == i.getType())) {
                slot++;
                continue;
            }
            Optional<Artifact> optional = ArtifactsPlugin.instance.getArtifacts().stream().filter(artifact -> artifact.getPoweredDataNumber() == i.getItemMeta().getCustomModelData()).findFirst();
            
            if (!optional.isPresent()) {
                slot++;
                continue;
            }
            
            ItemMeta meta = i.getItemMeta();
            meta.setCustomModelData(optional.get().getNormalDataNumber());
            i.setItemMeta(meta);
            
            player.getInventory().setItem(slot, i);
            slot++;
        }
    }
    
    @EventHandler(priority = EventPriority.LOWEST)
    public void onArtifactUse(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        ItemStack mainHand = event.getItem();
        
        if (mainHand == null || mainHand.getType() == Material.AIR || !mainHand.hasItemMeta() || !mainHand.getItemMeta().hasCustomModelData())
            return;
        
        if (ArtifactsPlugin.instance.getArtifacts().stream().noneMatch(artifact -> artifact.getType() == mainHand.getType()))
            return;
        
        int mainHandModelData = mainHand.getItemMeta().getCustomModelData();
        int modelData;
        
        Optional<Artifact> optional = ArtifactsPlugin.instance.getArtifacts().stream()
                .filter(a -> a.getType() == mainHand.getType() && (a.getNormalDataNumber() == mainHandModelData || a.getPoweredDataNumber() == mainHandModelData)).findFirst();
        
        if (!optional.isPresent())
            return;
        
        Artifact artifact = optional.get();
        
        if (ArtifactsPlugin.instance.isPoweredArtifactRegion(player.getLocation())) {
            if (isPoweredPointActive()) {
                if (mainHandModelData == artifact.getNormalDataNumber())
                    modelData = artifact.getPoweredDataNumber();
                else {
                    if (event.getAction() == Action.RIGHT_CLICK_BLOCK || event.getAction() == Action.RIGHT_CLICK_AIR)
                        artifact.onRightClick(event);
                    if (event.getAction() == Action.LEFT_CLICK_BLOCK || event.getAction() == Action.LEFT_CLICK_AIR)
                        artifact.onLeftClick(event);
                    return;
                }
            } else if (mainHandModelData == artifact.getPoweredDataNumber())
                modelData = artifact.getNormalDataNumber();
            else
                return;
        } else if (!ArtifactsPlugin.instance.isPoweredArtifactRegion(player.getLocation()))
            modelData = artifact.getNormalDataNumber();
        else
            return;
        
        ItemMeta meta = mainHand.getItemMeta();
        meta.setCustomModelData(modelData);
        mainHand.setItemMeta(meta);
        player.getInventory().setItemInMainHand(mainHand);
        player.updateInventory();
    }
    
    private boolean isPoweredPointActive() {
        if (clCapture == null)
            return false;
        
        if (clCapture.getPoints().stream().noneMatch(point -> point.getState() == CapturePoint.CapturePointState.ACTIVE
                || point.getState() == CapturePoint.CapturePointState.CAPTURED))
            return false;
        
        return clCapture.getPoints().stream().anyMatch(point -> point.getType().getArtifactModifer() == CapturePointType.ArtifactModifer.POWERED);
    }
}
