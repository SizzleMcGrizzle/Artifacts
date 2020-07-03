package me.sizzlemcgrizzle.artifacts;

import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.flags.StateFlag;
import de.craftlancer.clcapture.CLCapture;
import de.craftlancer.clcapture.CapturePoint;
import de.craftlancer.clcapture.CapturePointType;
import me.sizzlemcgrizzle.artifacts.artifacts.Artifact;
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
        } else if (!ArtifactsPlugin.instance.isPoweredArtifactRegion(player.getLocation()) && mainHandModelData == artifact.getPoweredDataNumber())
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
