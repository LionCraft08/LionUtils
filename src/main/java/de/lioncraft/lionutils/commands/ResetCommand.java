package de.lioncraft.lionutils.commands;

import de.lioncraft.lionapi.messageHandling.DM;
import de.lioncraft.lionapi.messageHandling.lionchat.LionChat;
import de.lioncraft.lionutils.Main;
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

import static de.lioncraft.lionutils.Main.lm;

public class ResetCommand implements TabExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!Main.getPlugin().getConfig().getBoolean("allow-reset")) {
            LionChat.sendSystemMessage(lm().msg("command.reset.blocked"), sender);
        }
        if(args.length >= 1){
            switch (args[0]){
                case "all" -> {
                    LionChat.sendSystemMessage(lm().msg("command.reset.warn", lm().msg("command.reset.placeholder_here").clickEvent(ClickEvent.runCommand("/reset confirm"))), sender);
                }
                case "world" -> LionChat.sendSystemMessage(lm().msg("command.reset.warn_worlds", lm().msg("command.reset.placeholder_here").clickEvent(ClickEvent.runCommand("/reset confirmworld"))), sender);
                case "players" -> LionChat.sendSystemMessage(lm().msg("command.reset.warn_players", lm().msg("command.reset.placeholder_here").clickEvent(ClickEvent.runCommand("/reset confirmplayer"))), sender);
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
        }else LionChat.sendSystemMessage(lm().msg("command.reset.warn", lm().msg("command.reset.placeholder_here").clickEvent(ClickEvent.runCommand("/reset confirm"))), sender);
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
