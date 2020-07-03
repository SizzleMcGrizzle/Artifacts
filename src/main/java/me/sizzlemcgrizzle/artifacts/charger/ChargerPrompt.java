package me.sizzlemcgrizzle.artifacts.charger;

import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.chat.ComponentSerializer;
import org.apache.commons.lang.ArrayUtils;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.FixedSetPrompt;
import org.bukkit.conversations.Prompt;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class ChargerPrompt extends FixedSetPrompt {
    protected final BaseComponent[] promptText;
    
    public ChargerPrompt() {
        ComponentBuilder builder = new ComponentBuilder();
        
        this.promptText = builder.append(" &a&l[Yes] ")
                .event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/convo yes"))
                .append("&c&l[No] ")
                .event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/convo no")).create();
    }
    
    @Nonnull
    @Override
    public String getPromptText(ConversationContext context) {
        return ComponentSerializer.toString(promptText);
    }
    
    @Override
    protected boolean isInputValid(@Nonnull ConversationContext context, @Nonnull String input) {
        String[] accepted = new String[]{"true", "false", "on", "off", "yes", "no", "y", "n", "1", "0",
                "right", "wrong", "correct", "incorrect", "valid", "invalid"};
        return ArrayUtils.contains(accepted, input.toLowerCase());
    }
    
    @Override
    protected @Nullable
    Prompt acceptValidatedInput(@Nonnull ConversationContext conversationContext, @Nonnull String s) {
        return this.acceptValidatedInput(conversationContext, s);
    }
}
