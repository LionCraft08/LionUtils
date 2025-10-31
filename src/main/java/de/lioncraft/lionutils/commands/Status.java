package de.lioncraft.lionutils.commands;

import de.lioncraft.lionapi.messageHandling.DM;
import de.lioncraft.lionapi.messageHandling.lionchat.LionChat;
import de.lioncraft.lionutils.utils.status.GlobalStatus;
import de.lioncraft.lionutils.utils.status.Inventories;
import de.lioncraft.lionutils.utils.status.StatusSettings;
import net.kyori.adventure.text.Component;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class Status implements TabExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if(sender instanceof Player p){
            if(args.length == 0){
                Inventories.openMainMenu(p);
            } else switch (args[0]){
                case "set":
                    StatusSettings ss = StatusSettings.getSettings(p);
                    if(args.length >= 2) {
                        if (ss.checkStatus(args[1])){
                            StatusSettings.getSettings(p).setCurrentStatus(args[1]);
                            LionChat.sendSystemMessage(Component.text("Welcome, ").append(p.displayName()), p);
                        }else LionChat.sendSystemMessage("Could not find that status!", p);
                    }
                    else p.sendMessage(DM.wrongArgs);
                    break;
                case "toggle":
                    StatusSettings s = StatusSettings.getSettings(p);
                    boolean b = !s.isEnabled();
                    if(args.length >= 2){
                        if(args[1].equalsIgnoreCase("on") || args[1].equalsIgnoreCase("true")||args[1].equalsIgnoreCase("1")){
                            b = true;
                        } else if (args[1].equalsIgnoreCase("off") || args[1].equalsIgnoreCase("false")||args[1].equalsIgnoreCase("0")) {
                            b = false;
                        }
                    }
                    s.setEnabled(b);
                    if(s.isEnabled()){
                        p.sendMessage(DM.messagePrefix.append(Component.text("Enabled your Status")));
                    }else p.sendMessage(DM.messagePrefix.append(Component.text("Disabled your Status")));
                    break;
                case "openui":
                    Inventories.openMainMenu(p);
                    break;
                default:
                    p.sendMessage(DM.wrongArgs);
            }
        }else sender.sendMessage(DM.notAPlayer);
        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        return switch (args.length){
            case 1 -> List.of("set", "toggle", "openui");
            case 2 -> switch (args[0]){
                case "set" -> getGlobalNames();
                case "toggle" -> List.of("on", "off");
                default -> new ArrayList<>(List.of(""));
            };
            default -> List.of("");
        };
    }
    private static @NotNull List<String> getGlobalNames(){
        return new ArrayList<>(GlobalStatus.getGlobalStatusList().keySet());
    }
}
