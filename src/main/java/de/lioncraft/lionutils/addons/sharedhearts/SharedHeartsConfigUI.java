package de.lioncraft.lionutils.addons.sharedhearts;

import de.lioncraft.lionapi.guimanagement.Interaction.Setting;
import de.lioncraft.lionapi.guimanagement.Items;
import de.lioncraft.lionapi.guimanagement.lioninventories.AddonManageMenu;
import de.lioncraft.lionutils.Main;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

public class SharedHeartsConfigUI {
    private static SharedHeartsConfigUI instance;
    public static SharedHeartsConfigUI getInstance() {
        if (instance == null) {instance = new SharedHeartsConfigUI();}
        return instance;
    }

    private Inventory inv;
    private SharedHeartsConfigUI() {
        inv = Bukkit.createInventory(null, 54, Main.lm().msg("inv.sharedhearts.name"));
        inv.setContents(Items.blockButtons);
        inv.setItem(49, Items.closeButton);
        inv.setItem(45, AddonManageMenu.getItem());

        Setting forceGlobal_Setting = new Setting(SharedHeartsAddon.getInstance().getSettings().getBoolValue("force-global"),
                Items.get(Main.lm().msg("inv.sharedhearts.forceglobal.title"),
                        Material.GLOBE_BANNER_PATTERN,
                        Main.lm().getMessageAsList("inv.sharedhearts.forceglobal.lore")),
                b -> {
                    SharedHeartsAddon.getInstance().getSettings().setBoolValue("force-global", b);
                });

        Setting syncDamageOnly_Setting = new Setting(SharedHeartsAddon.getInstance().getSettings().getBoolValue("sync-damage-only"),
                Items.get(Main.lm().msg("inv.sharedhearts.sync-damage-only.title"),
                        Material.ENCHANTED_GOLDEN_APPLE,
                        Main.lm().getMessageAsList("inv.sharedhearts.sync-damage-only.lore")),
                b -> {
                    SharedHeartsAddon.getInstance().getSettings().setBoolValue("sync-damage-only", b);
                });

        inv.setItem(10, forceGlobal_Setting.getTopItem());
        inv.setItem(19, forceGlobal_Setting.getBottomItem());
        inv.setItem(12, syncDamageOnly_Setting.getTopItem());
        inv.setItem(21, syncDamageOnly_Setting.getBottomItem());
    }

    public void openConfigMenu(Player player){
        player.openInventory(inv);
    }



}
