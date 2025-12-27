package de.lioncraft.lionutils.commands;

import de.lioncraft.lionapi.messageHandling.MSG;
import de.lioncraft.lionapi.messageHandling.lionchat.LionChat;
import de.lioncraft.lionutils.Main;
import de.lioncraft.lionutils.utils.StructureUtils;
import net.kyori.adventure.text.Component;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jspecify.annotations.NonNull;

import java.util.ArrayList;
import java.util.List;

public class StructureCommand implements TabExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NonNull @NotNull String[] args) {
        if (args.length == 0) {
            LionChat.sendSystemMessage(MSG.WRONG_ARGS, sender);
        }else
            switch (args[0]) {
                case "place" -> {
                    if (sender instanceof Player p) {
                        StructureUtils.createStructure(p.getLocation(), StructureUtils.getRotationFromPlayer(p));
                        LionChat.sendSystemMessage(Main.lm().msg("command.structure.placed"), p);
                    } else LionChat.sendSystemMessage(MSG.NOT_A_PLAYER, sender);
                }
                case "auto_placement" -> {
                    if (sender instanceof Player p) {
                        StructureUtils.doEverything(p);
                        LionChat.sendSystemMessage(Main.lm().msg("command.structure.placed"), p);
                    }
                    else LionChat.sendSystemMessage(MSG.NOT_A_PLAYER, sender);
                }
                case "open_doors" -> {
                    StructureUtils.setEntrance(true);
                    LionChat.sendSystemMessage(Main.lm().msg("command.structure.doors_opened"), sender);
                }
                case "close_doors" -> {
                    StructureUtils.setEntrance(false);
                    LionChat.sendSystemMessage(Main.lm().msg("command.structure.doors_closed"), sender);
                }
            }


        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NonNull @NotNull String[] args) {
        List<String> list = new ArrayList<>();
        switch (args.length) {
            case 0, 1 -> {
                list.add("place");
                list.add("auto_placement");
                list.add("open_doors");
                list.add("close_doors");
            }
        }

        if (args.length == 0 || args[args.length-1].isBlank()) return list;
        list.removeIf(s -> !s.startsWith(args[args.length-1]));
        return list;
    }
}
