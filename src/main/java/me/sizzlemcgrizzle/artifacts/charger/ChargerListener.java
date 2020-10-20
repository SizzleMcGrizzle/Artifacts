package me.sizzlemcgrizzle.artifacts.charger;

import de.craftlancer.core.conversation.FormattedConversable;
import me.sizzlemcgrizzle.artifacts.ArtifactsPlugin;
import me.sizzlemcgrizzle.artifacts.settings.Settings;
import me.sizzlemcgrizzle.blueprints.api.BlueprintPostPasteEvent;
import org.bukkit.conversations.Conversation;
import org.bukkit.conversations.ConversationFactory;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

public class ChargerListener implements Listener {
    
    @EventHandler
    public void onChargerBlueprintPaste(BlueprintPostPasteEvent event) {
        
        if (!event.getType().equalsIgnoreCase("charger"))
            return;
        
        if (ArtifactsPlugin.getInstance().getChargers().stream().anyMatch(charger -> charger.getBaseLocation().equals(event.getFeatureLocation())))
            return;
        
        ArtifactsPlugin.getInstance().addCharger(new Charger(event.getFeatureLocation(), event.getBlocksPasted()));
        System.out.println("ewew");
    }
    
    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOW)
    public void onChargerBreak(BlockBreakEvent event) {
        if (!event.getPlayer().hasPermission("artifacts.charger"))
            return;
        
        if (ArtifactsPlugin.getInstance().getChargers().stream().anyMatch(charger -> charger.getStructure().contains(event.getBlock().getLocation()))) {
            event.setCancelled(true);
            
            Charger charger = ArtifactsPlugin.getInstance().getChargers().stream()
                    .filter(c -> c.getStructure().contains(event.getBlock().getLocation())).findFirst().get();
            
            ConversationFactory conversation = new ConversationFactory(ArtifactsPlugin.getInstance())
                    .withLocalEcho(false)
                    .withModality(false)
                    .withTimeout(60)
                    .withFirstPrompt(new ConfirmationPrompt(charger, event.getPlayer()))
                    .addConversationAbandonedListener(conversationAbandonedEvent -> {
                        if (!conversationAbandonedEvent.gracefulExit()) {
                            ArtifactsPlugin.tell(event.getPlayer(), Settings.PREFIX + "&eCharger removal timed out.");
                        }
                    });
            
            Conversation convo = conversation.buildConversation(new FormattedConversable(event.getPlayer()));
            convo.begin();
        }
    }
}
