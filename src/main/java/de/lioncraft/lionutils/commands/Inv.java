package de.lioncraft.lionutils.commands;

import de.lioncraft.lionapi.messageHandling.DM;
import de.lioncraft.lionapi.messageHandling.defaultMessages;
import de.lioncraft.lionutils.utils.InvWatcher;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class Inv implements TabExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if(sender instanceof Player p){
            if(args.length >= 1){
                Player target = Bukkit.getPlayer(args[0]);
                if(target != null){
                    if(target == sender){
                        if(args.length >= 2){
                            if (args[1].equalsIgnoreCase("confirm")) {
                                InvWatcher.open(p, target);
                            }else sender.sendMessage(DM.wrongArgs);
                        }else{
                            sender.sendMessage(DM.error(Component.text("Opening your own Inventory can cause Item Duplication and other glitches." +
                                    "Click here to open it anyways.").clickEvent(ClickEvent.runCommand("/inv " + p.getName() + " confirm"))));
                        }
                    }else InvWatcher.open(p, target);
                }else sender.sendMessage(DM.noPlayer);
            }else sender.sendMessage(DM.wrongArgs);
        }else sender.sendMessage(DM.notAPlayer);
        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        return null;
    }
}
