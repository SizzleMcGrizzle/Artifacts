package me.sizzlemcgrizzle.artifacts.charger;

import me.sizzlemcgrizzle.artifacts.ArtifactsPlugin;
import me.sizzlemcgrizzle.artifacts.settings.Settings;
import org.apache.commons.lang.ArrayUtils;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.Prompt;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class ConfirmationPrompt extends ChargerPrompt {
    
    private Charger charger;
    private Player player;
    
    private String[] yes = new String[]{"yes", "1", "true", "y", "correct", "valid"};
    private String[] no = new String[]{"no", "0", "false", "n", "wrong", "invalid"};
    
    public ConfirmationPrompt(Charger charger, Player player) {
        super();
        
        this.charger = charger;
        this.player = player;
    }
    
    @Override
    protected @Nullable
    Prompt acceptValidatedInput(@Nonnull ConversationContext conversationContext, @Nonnull String input) {
        if (ArrayUtils.contains(yes, input.toLowerCase())) {
            
            ArtifactsPlugin.tell(player, Settings.PREFIX + "&eCharger has been removed.");
            ArtifactsPlugin.getInstance().removeCharger(charger);
            charger = null;
            
        } else if (ArrayUtils.contains(no, input.toLowerCase())) {
            
            ArtifactsPlugin.tell(player, Settings.PREFIX + "&eCharger removal has been cancelled.");
            ArtifactsPlugin.getInstance().removeCharger(charger);
            charger = null;
            
        }
        return Prompt.END_OF_CONVERSATION;
    }
}

