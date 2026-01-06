package de.lioncraft.lionutils.addons.hardcoremc;

import de.lioncraft.lionapi.guimanagement.Interaction.LionButtonFactory;
import de.lioncraft.lionapi.guimanagement.Interaction.MultipleStringSelection;
import de.lioncraft.lionapi.guimanagement.Interaction.Setting;
import de.lioncraft.lionapi.guimanagement.Items;
import de.lioncraft.lionapi.guimanagement.lioninventories.AddonManageMenu;
import de.lioncraft.lionutils.Main;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class HardcoreMCConfigUI {
    private static HardcoreMCConfigUI instance;
    public static HardcoreMCConfigUI getInstance() {
        if (instance == null) {instance = new HardcoreMCConfigUI();}

        return instance;
    }

    private Inventory configInv;
    private ItemStack openButton;

    private HardcoreMCConfigUI() {
        configInv = Bukkit.createInventory(null, 54, Component.text("HardcoreMC Config"));
        configInv.setContents(Items.blockButtons);
        configInv.setItem(49, Items.closeButton);
        configInv.setItem(45, AddonManageMenu.getItem());
        Setting hardcoreHearts = new Setting(HardcoreMCAddon.getInstance().getSettings().getBoolValue("show_hardcore_hearts"),
                Items.get(Main.lm().msg("inv.hardcoremc.show_hearts.short"), Material.HEART_POTTERY_SHERD, Main.lm().getMessageAsList("inv.hardcoremc.show_hearts.tooltip", new String[0]).toArray(Component[]::new)),
                b -> HardcoreMCAddon.getInstance().getSettings().setBoolValue("show_hardcore_hearts", b));
        configInv.setItem(10, hardcoreHearts.getTopItem());
        configInv.setItem(19, hardcoreHearts.getBottomItem());


        MultipleStringSelection regeneration_mode = new MultipleStringSelection(Items.get(
                Main.lm().msg("inv.hardcoremc.regeneration_mode.short"),
                Material.GOLDEN_APPLE,
                Main.lm().getMessageAsList("inv.hardcoremc.regeneration_mode.tooltip").toArray(Component[]::new)),
                List.of("NORMAL", "EFFECTS", "NONE"),
                (i, s, inventoryClickEvent) -> {
                    HardcoreMCAddon.getInstance().getSettings().setStringValue("regeneration_mode", s);
                }

        );
        regeneration_mode.setCurrentString(getIndexOfRegenMode(HardcoreMCAddon.getInstance().getSettings().getStringValue("regeneration_mode")));
        configInv.setItem(12, regeneration_mode.getItem());

        openButton = LionButtonFactory.createButton(Items.get(
                        Main.lm().msg("inv.hardcoremc.description"),
                        Material.HEARTBREAK_POTTERY_SHERD,
                        Main.lm().getMessageAsList("inv.hardcoremc.tooltip").toArray(Component[]::new)
                ),
                "lionutils_hardcoremc_open_ui");
    }

    private int getIndexOfRegenMode(String s){
        return switch (s){
            case "NORMAL" -> 0;
            case "EFFECTS" -> 1;
            case "NONE" -> 2;
            default -> 0;
        };
    }

    public Inventory getConfigInv() {
        return configInv;
    }

    public @NotNull ItemStack getButton() {
        return openButton;
    }


}
