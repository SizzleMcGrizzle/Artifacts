package me.sizzlemcgrizzle.artifacts;

import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.flags.StateFlag;
import de.craftlancer.clcapture.CLCapture;
import de.craftlancer.clcapture.CapturePoint;
import de.craftlancer.clcapture.CapturePointType;
import de.craftlancer.core.util.ItemBuilder;
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

import java.util.Optional;

public class PowerArtifactsFlag implements Listener {
    
    static final StateFlag CAPTURE_POWER_ARTIFACTS_FLAG = new StateFlag("power-artifacts", false);
    static final StateFlag DUNGEON_POWER_ARTIFACTS_FLAG = new StateFlag("power-dungeon-artifacts", false);
    private final CLCapture clCapture = (CLCapture) Bukkit.getPluginManager().getPlugin("CLCapture");
    
    static void registerFlag() {
        WorldGuard.getInstance().getFlagRegistry().register(CAPTURE_POWER_ARTIFACTS_FLAG);
        WorldGuard.getInstance().getFlagRegistry().register(DUNGEON_POWER_ARTIFACTS_FLAG);
    }
    
    @EventHandler(priority = EventPriority.LOWEST)
    public void onArtifactUse(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        ItemStack mainHand = player.getInventory().getItemInMainHand();
        
        if (mainHand == null || mainHand.getType() == Material.AIR || !mainHand.hasItemMeta() || !mainHand.getItemMeta().hasCustomModelData())
            return;
        
        int mainHandModelData = mainHand.getItemMeta().getCustomModelData();
        int modelData;
        
        Optional<Artifact> optional = ArtifactsPlugin.getInstance().getArtifacts().stream()
                .filter(a -> a.getType() == mainHand.getType() && (a.getNormalDataNumber() == mainHandModelData || a.getPoweredDataNumber() == mainHandModelData)).findFirst();
        
        if (!optional.isPresent())
            return;
        
        Artifact artifact = optional.get();
        PoweredType type = ArtifactsPlugin.getInstance().getPoweredTypeInRegion(player.getLocation());
        
        if (type != null) {
            if (isTypeActive(type)) {
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
        } else if (mainHandModelData == artifact.getPoweredDataNumber())
            modelData = artifact.getNormalDataNumber();
        else
            return;
        
        player.getInventory().setItemInMainHand(new ItemBuilder(mainHand).setCustomModelData(modelData).build());
        player.updateInventory();
    }
    
    private boolean isTypeActive(PoweredType type) {
        if (type == PoweredType.DUNGEON)
            return true;
        
        if (clCapture == null)
            return false;
        
        return clCapture.getPoints().stream().anyMatch(point -> (point.getState() == CapturePoint.CapturePointState.ACTIVE
                || point.getState() == CapturePoint.CapturePointState.CAPTURED)
                && point.getArtifactModifier() == CapturePointType.ArtifactModifer.POWERED);
    }
    
    public enum PoweredType {
        CAPTURE,
        DUNGEON
    }
}
