package de.lioncraft.lionutils.commands;

import de.lioncraft.lionapi.messageHandling.DM;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.Statistic;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class StatsCommand implements TabExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        Statistic s;
        OfflinePlayer p = null;
        if(args.length < 2) sender.sendMessage(DM.wrongArgs);
        else if (args.length <= 4) {
            try {
                s = Statistic.valueOf(args[1]);
            }catch (IllegalArgumentException e){
                sender.sendMessage(DM.error("This Statistic does not exist!"));
                return true;
            }
            if(sender instanceof Player player) p = player;
            int value;

            if(args.length > 2){
                if(args.length > 3){
                    OfflinePlayer p2 = Bukkit.getOfflinePlayer(args[3]);
                    if(p2.hasPlayedBefore()){
                        p = p2;
                    }
                }
                if(p == null){
                    sender.sendMessage(DM.noPlayer);
                    return true;
                }
                try {
                    value = p.getStatistic(s, Material.valueOf(args[2]));
                }catch (IllegalArgumentException e){
                    try {
                        value = p.getStatistic(s, EntityType.valueOf(args[2]));
                    }catch (IllegalArgumentException e2){
                        try {
                            value = p.getStatistic(s);
                        }catch (IllegalArgumentException e3){
                            sender.sendMessage(DM.error(args[1] + " doesn't accept " + args[2] + " as arguments!"));
                            return true;
                        }
                    }
                }
            }else{
                if(p != null){
                    try {
                        value = p.getStatistic(s);
                    }catch (IllegalArgumentException e){
                        sender.sendMessage(DM.error("Specify args for the Statistic (Entity, Item or Block)"));
                        return true;
                    }
                }else{
                    sender.sendMessage(DM.notAPlayer);
                    return true;
                }
            }
            if(value != -1){
                sender.sendMessage(DM.info(Component.text(p.getName()).append(Component.text(" hat den Wert ")).append(Component.text(value, TextColor.color(255, 0, 128))).append(Component.text(" für ", TextColor.color(255, 255, 255))).append(Component.text(s.toString(), TextColor.color(255, 0, 128)))));
            }else sender.sendMessage(DM.commandError);

        }else sender.sendMessage(DM.wrongArgs);


        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        return switch (args.length){
            case 1 -> List.of("get");
            case 2 -> getList();
            case 3 -> getArgs(args[1]);
            case 4 -> null;
            default -> new ArrayList<>();
        };
    }
    private static List<String> getArgs(String s){
        try{
            return getList(Statistic.valueOf(s).getType());
        }catch (IllegalArgumentException e){
            return List.of("empty");
        }
    }
    private static HashMap<Statistic.Type, List<String>> results = new HashMap<>();
    private static List<String> getList(Statistic.Type st){
        if(!results.containsKey(st)){
            results.put(st, new ArrayList<>());
            switch (st){
                case ITEM -> {
                    for (Material m : Material.values())
                        if (m.isItem()) results.get(st).add(m.toString());
                }
                case BLOCK -> {
                    for (Material m : Material.values())
                        if (m.isBlock()) results.get(st).add(m.toString());
                }
                case ENTITY -> {
                    for (EntityType et : EntityType.values())
                        results.get(st).add(et.toString());
                }
                default -> results.get(st).add("empty");
            }
        }
        return results.get(st);
    }
    private static List<String> statistics = new ArrayList<>();
    private static List<String> getList(){
        if(statistics.isEmpty()){
            for(Statistic s : Statistic.values()){
                statistics.add(s.toString());
            }
        }
        return statistics;
    }
}
