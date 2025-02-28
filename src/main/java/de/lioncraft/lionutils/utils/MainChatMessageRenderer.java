package de.lioncraft.lionutils.utils;

import de.lioncraft.lionutils.utils.status.StatusSettings;
import io.papermc.paper.chat.ChatRenderer;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class MainChatMessageRenderer implements ChatRenderer {
    @Override
    public @NotNull Component render(@NotNull Player source, @NotNull Component sourceDisplayName, @NotNull Component message, @NotNull Audience viewer) {
        TextColor color = TextColor.color(150, 0, 255);
        if(StatusSettings.getSettings(source).getCurrentStatus() != null){
            color = StatusSettings.getSettings(source).getCurrentStatus().getNameColor();
        }
        Component c = sourceDisplayName.append(Component.text(" >> ", color)).append(message.color(TextColor.color(255, 255, 255)));
        return c;
    }
}
