package de.lioncraft.lionutils.listeners;

import com.destroystokyo.paper.event.server.PaperServerListPingEvent;
import de.lioncraft.lionapi.velocity.connections.ConnectionManager;
import de.lioncraft.lionutils.Main;
import de.lioncraft.lionutils.messages.MessageSender;
import de.lioncraft.lionutils.utils.MOTD;
import de.lioncraft.lionutils.utils.MainChatMessageRenderer;
import de.lioncraft.lionutils.utils.Settings;
import io.papermc.paper.chat.ChatRenderer;
import io.papermc.paper.command.brigadier.argument.SignedMessageResolver;
import io.papermc.paper.event.player.AsyncChatDecorateEvent;
import io.papermc.paper.event.player.AsyncChatEvent;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class ChatListeners implements Listener {
    @EventHandler
    public void onChat(AsyncChatEvent e){
        if(!e.signedMessage().isSystem()){
            e.renderer(new MainChatMessageRenderer());
            e.viewers().removeIf(audience -> {
                if(audience instanceof Player p){
                    return !Settings.getSettings(p).isRecieveChat();
                }
                return false;});
        }

    }
    @EventHandler
    public void onJoin(PlayerJoinEvent e){
        e.joinMessage(Component.text("+ >>", TextColor.color(0 ,255, 0), TextDecoration.BOLD).append(Component.text(" ", TextColor.color(255, 255, 255)).append(e.getPlayer().displayName())));
        if (!ConnectionManager.isConnectedToVelocity()){
            for(Player p : Bukkit.getServer().getOnlinePlayers()){
                MessageSender.sendFooter(p);
            }
            MessageSender.sendHeader(e.getPlayer());
        }


    }

    @EventHandler
    public void onLeave(PlayerQuitEvent e){
        e.quitMessage(Component.text("- <<", TextColor.color(255 ,70, 0), TextDecoration.BOLD).append(Component.text(" ", TextColor.color(255, 255, 255)).append(e.getPlayer().displayName())));
        if (!ConnectionManager.isConnectedToVelocity())
            for(Player p : Bukkit.getServer().getOnlinePlayers()){
                if (p != e.getPlayer()) MessageSender.sendFooter(p);
            }
    }

    @EventHandler
    public void onMOTD(PaperServerListPingEvent e){
        e.motd(MOTD.getRandomCMOTD());
    }
    private static Component getHeader(){
        Component c = Component.text("L", TextColor.color(0, 255, 255))
                .append(Component.text("i", TextColor.color(0, 240, 255)))
                .append(Component.text("o", TextColor.color(0, 230, 255)))
                .append(Component.text("n", TextColor.color(0, 210, 255)))
                .append(Component.text("C", TextColor.color(0, 190, 255)))
                .append(Component.text("r", TextColor.color(0, 170, 255)))
                .append(Component.text("a", TextColor.color(0, 150, 255)))
                .append(Component.text("f", TextColor.color(0, 130, 255)))
                .append(Component.text("t", TextColor.color(0, 110, 255)));
        return c;
    }
    private static Component getLine(){
        Component c = Component.text("--", TextColor.color(255, 0, 255))
                .append(Component.text("--", TextColor.color(230, 0, 255)))
                .append(Component.text("--", TextColor.color(210, 0, 255)))
                .append(Component.text("--", TextColor.color(190, 0, 255)))
                .append(Component.text("--", TextColor.color(170, 0, 255)))
                .append(Component.text("--", TextColor.color(150, 0, 255)))
                .append(Component.text("--", TextColor.color(130, 0, 255)))
                .append(Component.text("--", TextColor.color(110, 0, 255)))
                .append(Component.text("--", TextColor.color(90, 0, 255)));
        return c;
    }
}
