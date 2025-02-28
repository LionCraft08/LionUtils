package de.lioncraft.lionutils.commands;

import de.lioncraft.lionapi.messageHandling.DM;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.GameMode;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Locale;

public class Flyspeed implements TabExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if(sender instanceof Player p)  {
            if(p.getAllowFlight()||p.getGameMode().equals(GameMode.CREATIVE)||p.getGameMode().equals(GameMode.SPECTATOR)){
                if(args.length >= 1){
                    switch (args[0]){
                        case "get":
                            p.sendMessage(DM.messagePrefix.append(Component.text("Deine Fluggeschwindigkeit beträgt " + p.getFlySpeed()*10 + " Bratwürste").hoverEvent(Component.text("Idk welche Einheit das ist...").asHoverEvent())));
                            break;
                        case "reset":
                            p.setFlySpeed(0.1f);
                            p.sendMessage(DM.messagePrefix.append(Component.text("Deine Fluggeschwindigkeit beträgt nun " + p.getFlySpeed()*10 + " Bratwürste").hoverEvent(Component.text("Idk welche Einheit das ist...").asHoverEvent())));
                            break;
                        default:
                            try {
                                float f = Float.parseFloat(args[0]);
                                if(f > 10 || f < -10){
                                    p.sendMessage(DM.messagePrefix.append(Component.text(f + " ist außerhalb der Range von -10 bis 10.")));
                                }else {
                                    p.setFlySpeed(f / 10f);
                                    p.sendMessage(DM.messagePrefix.append(Component.text("Deine Fluggeschwindigkeit beträgt nun " + p.getFlySpeed() * 10 + " Bratwürste").hoverEvent(Component.text("Idk welche Einheit das ist...").asHoverEvent())));
                                }
                            }catch (NumberFormatException e){
                                p.sendMessage(DM.messagePrefix.append(Component.text("["+e.getClass().getName()+"] \"" + args[0] + "\" ist keine Zahl")));
                            }
                    }
                }else{
                    p.sendMessage(DM.messagePrefix.append(Component.text("Deine Fluggeschwindigkeit beträgt " + p.getFlySpeed()*10 + " Bratwürste").hoverEvent(Component.text("Idk welche Einheit das ist...").asHoverEvent())));
                }
            }else p.sendMessage(DM.messagePrefix.append(Component.text("Du kannst nicht fliegen!", TextColor.color(255, 128, 0))));
        }
        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        return switch (args.length){
            case 1->List.of("get", "reset", "2", "5", "10");
            default -> List.of();
        };
    }
}
