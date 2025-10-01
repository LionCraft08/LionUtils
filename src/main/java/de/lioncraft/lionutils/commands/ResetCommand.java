package de.lioncraft.lionutils.commands;

import de.lioncraft.lionapi.messageHandling.DM;
import de.lioncraft.lionapi.messageHandling.lionchat.LionChat;
import de.lioncraft.lionutils.utils.ResetUtils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class ResetCommand implements TabExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if(args.length >= 1){
            switch (args[0]){
                case "all" -> {
                    LionChat.sendSystemMessage(Component.text("WARNING! This command will reset the whole server including the map and Player Inventories! Click ", TextColor.color(255, 128, 0)).append(Component.text("here", TextColor.color(255, 0, 255)).clickEvent(ClickEvent.runCommand("/reset confirm"))).append(Component.text(" to resume", TextColor.color(255, 128, 0))), sender);
                }
                case "world" -> LionChat.sendSystemMessage(Component.text("WARNING! This command will reset the map (overworld, nether, end)! Click ", TextColor.color(255, 128, 0)).append(Component.text("here", TextColor.color(255, 0, 255)).clickEvent(ClickEvent.runCommand("/reset confirmworld"))).append(Component.text(" to resume", TextColor.color(255, 128, 0))), sender);
                case "players" -> LionChat.sendSystemMessage(Component.text("WARNING! This command will reset every Player Inventory! Click ", TextColor.color(255, 128, 0)).append(Component.text("here", TextColor.color(255, 0, 255)).clickEvent(ClickEvent.runCommand("/reset confirmplayer"))).append(Component.text(" to resume", TextColor.color(255, 128, 0))), sender);
                case "environment" -> {
                    World w = Bukkit.getServer().getWorld("world");
                }
                case "confirm"->{
                    ResetUtils.resetAll();
                }
                case "confirmplayer"->{
                    ResetUtils.resetPlayerData();
                }
            }
        }else sender.sendMessage(DM.messagePrefix.append(Component.text("WARNING! This command will reset the whole server including the map and Player Inventories! Click ", TextColor.color(255, 128, 0)).append(Component.text("here", TextColor.color(255, 0, 255)).clickEvent(ClickEvent.runCommand("/reset confirm"))).append(Component.text(" to resume", TextColor.color(255, 128, 0)))));
        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        return switch (args.length){
            case 1 -> List.of("all", "world", "environment", "players");
            default -> List.of();
        };
    }
}
