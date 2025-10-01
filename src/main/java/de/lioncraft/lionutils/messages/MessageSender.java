package de.lioncraft.lionutils.messages;


import de.lioncraft.lionutils.utils.GUIElementRenderer;
import net.kyori.adventure.audience.Audience;
import org.bukkit.entity.Player;

import java.util.TimeZone;

public final class MessageSender {
    private MessageSender(){}
    public static void sendHeader(Audience a){
        a.sendPlayerListHeader(GUIElementRenderer.getHeader(TimeZone.getDefault().getID()));
    }
    public static void sendFooter(Player p){
        p.sendPlayerListFooter(GUIElementRenderer.getFooter("Survival 01"));
    }
}
