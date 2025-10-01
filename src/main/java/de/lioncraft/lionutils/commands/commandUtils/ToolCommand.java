package de.lioncraft.lionutils.commands.commandUtils;

import de.lioncraft.lionapi.messageHandling.MSG;
import de.lioncraft.lionapi.messageHandling.lionchat.LionChat;
import de.lioncraft.lionutils.addons.CommandUtilsAddon;
import org.bukkit.Bukkit;
import org.bukkit.block.BlastFurnace;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.BrewerInventory;
import org.bukkit.inventory.FurnaceInventory;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ToolCommand implements TabExecutor {
    private static final HashMap<Player, FurnaceInventory> furnaces = new HashMap<>();
    private static final HashMap<Player, BrewerInventory> brewing_stands = new HashMap<>();
    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        if (commandSender instanceof Player p) {
            if (CommandUtilsAddon.getInstance().isEnabled()) {
                if (strings.length >= 1) {
                    Player target = null;
                    if (strings.length>1) target = Bukkit.getPlayer(strings[1]);
                    if (target == null) target = p;
                    String cmd = strings[0].replace("_", "").toLowerCase();
                    switch (cmd) {
                        case "craftingtable" -> {
                            if (CommandUtilsAddon.getInstance().getSettings().getBoolValue("crafting_table_enabled")) {
                                p.openInventory(Bukkit.createInventory(null, InventoryType.WORKBENCH));
                            }else sendDisabledMSG(p);
                        }
                        case "smithingtable" -> {
                            if (CommandUtilsAddon.getInstance().getSettings().getBoolValue("tool_blocks_enabled")) {
                                p.openInventory(Bukkit.createInventory(null, InventoryType.SMITHING));
                            }else sendDisabledMSG(p);
                        }
                        case "grindstone" -> {
                            if (CommandUtilsAddon.getInstance().getSettings().getBoolValue("tool_blocks_enabled")) {
                                p.openInventory(Bukkit.createInventory(null, InventoryType.GRINDSTONE));
                            }else sendDisabledMSG(p);
                        }
                        case "enchanter" -> {
                            if (CommandUtilsAddon.getInstance().getSettings().getBoolValue("tool_blocks_enabled")) {
                                p.openInventory(Bukkit.createInventory(null, InventoryType.ENCHANTING));
                            }else sendDisabledMSG(p);
                        }
                        case "furnace" -> {
                            if (CommandUtilsAddon.getInstance().getSettings().getBoolValue("furnace_command")) {
                                if (!furnaces.containsKey(target))
                                    furnaces.put(target, (FurnaceInventory) Bukkit.createInventory(null, InventoryType.FURNACE));
                                FurnaceInventory inv = furnaces.get(target);
                                p.openInventory(inv);
                            }else sendDisabledMSG(p);
                        }
                        case "brewingstand" -> {
                            if (CommandUtilsAddon.getInstance().getSettings().getBoolValue("brewing_stand_enabled")) {
                                if (!brewing_stands.containsKey(target))
                                    brewing_stands.put(target, (BrewerInventory) Bukkit.createInventory(null, InventoryType.BREWING));
                                BrewerInventory inv = brewing_stands.get(target);
                                p.openInventory(inv);
                            }else sendDisabledMSG(p);
                        }
                        case "enderchest" -> {
                            if (CommandUtilsAddon.getInstance().getSettings().getBoolValue("enderchest_enabled")) {
                                p.openInventory(target.getEnderChest());
                            }else sendDisabledMSG(p);
                        }
                    }
                } else CommandUtilsAddon.getInstance().sendMessage(MSG.WRONG_ARGS.getText(), commandSender);
            } else sendDisabledMSG(p);
        }else CommandUtilsAddon.getInstance().sendMessage(MSG.notAPlayer.getText(), commandSender);
        return false;
    }

    void sendDisabledMSG(Player p){
        CommandUtilsAddon.getInstance().sendMessage("<red>This Tool is disabled in the Settings.", p);
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        return switch (strings.length){
            case 1-> new ArrayList<>(List.of("ender_chest",
                    "crafting_table",
                    "furnace",
                    "smithing_table",
                    "grindstone",
                    "brewing_stand",
                    "enchanter"));

            default -> throw new IllegalStateException("Unexpected value: " + strings.length);
        };
    }
}
