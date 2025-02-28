package de.lioncraft.lionutils.commands;

import de.lioncraft.lionapi.messageHandling.DM;
import de.lioncraft.lionapi.messageHandling.defaultMessages;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class Ping implements TabExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        Player p = null;
        if(sender instanceof Player p2) {
            p = p2;
        }
        if (args.length >= 1) {
            if (Bukkit.getPlayer(args[0]) != null) {
                p = Bukkit.getPlayer(args[0]);
            } else {
                sender.sendMessage(DM.noPlayer);
                return true;
            }
        }
        if(p == null){
            sender.sendMessage(DM.notAPlayer);
            return true;
        }
        Component c;
        int i = p.getPing();
        if(i <= 50){
            c = Component.text(i + "ms", TextColor.color(0, 255, 0));
        } else if (i <= 100) {
            c = Component.text(i + "ms", TextColor.color(255, 255, 0));
        } else if (i <= 500) {
            c = Component.text(i + "ms", TextColor.color(255, 128, 0));
        }else c = Component.text(i + "ms", TextColor.color(255, 0, 0));
        if(p == sender){
            sender.sendMessage(DM.messagePrefix.append(Component.text("Your current Ping is ").append(c)));
        }else{
            sender.sendMessage(DM.messagePrefix.append(Component.text(p.getName() + "'s current ping is ").append(c)));
        }
        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if(args.length < 2){
            return null;
        }
        return List.of();
    }
}
