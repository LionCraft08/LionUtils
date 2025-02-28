package de.lioncraft.lionutils.inventories;

import de.lioncraft.lionapi.guimanagement.Interaction.Button;
import de.lioncraft.lionapi.guimanagement.Items;
import de.lioncraft.lionapi.guimanagement.Interaction.Setting;
import de.lioncraft.lionutils.listeners.SettingsListeners;
import de.lioncraft.lionutils.utils.Settings;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.Sound;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class PlayerSettingsGUI {
    private static ItemStack fly, chat, invulnerable, invisible, move, hitMobs, mineBlocks, pickupItems;
    public static void Initialize(){
        fly = Items.get(Component.text("Player can fly"), Material.ELYTRA, "Sets if the Player", "can fly like in Creative");
        chat = Items.get(Component.text("Player can Chat"), Material.GOAT_HORN, "Sets if the Player", "can send Chat Messages");
        invulnerable = Items.get(Component.text("Player is invulnerable"), Material.NETHERITE_CHESTPLATE, "Sets if the Player", "is invulnerable");
        invisible = Items.get(Component.text("Player is visible"), Material.SPYGLASS, "Sets if the Player", "is visible to others");
        move = Items.get(Component.text("Player can move"), Material.GOLDEN_BOOTS, "Sets if the Player", "can change it's current Position");
        hitMobs = Items.get(Component.text("Player can Hit Entities"), Material.IRON_SWORD, "Sets if the Player", "can hit any Entity");
        mineBlocks = Items.get(Component.text("Player can mine Blocks"), Material.DIAMOND_PICKAXE, "Sets if the Player", "can mine any Block");
        pickupItems = Items.get(Component.text("Player can pickup Items"), Material.FISHING_ROD, "Sets if the Player", "can pickup any Item");
    }
    public static void openSelectUI(HumanEntity player){
        Inventory selectGlobalInv = Bukkit.createInventory(null, 54, Component.text("Select Player"));
        selectGlobalInv.setContents(Items.blockButtons);
        Button back = new Button(Items.backButton.clone(), inventoryClickEvent -> {MainGUI.open(inventoryClickEvent.getWhoClicked());return false;});
        selectGlobalInv.setItem(45, back.getButton());
        Button global = new Button(Items.get(Component.text("Global Settings", TextColor.color(255, 255, 0)), Material.PLAYER_HEAD, TextColor.color(255, 0,255), "Opens the Settings for every Player", "Existing different Settings from", "Global will be overwritten"), inventoryClickEvent -> {
            openSettingsUI(inventoryClickEvent.getWhoClicked(), null);
            return false;});
        selectGlobalInv.setItem(13, global.getButton());
        selectGlobalInv.setItem(49, Items.closeButton);
        int i = 28;
        for(Settings s : Settings.getSettings()){
            if (i > 34 && i < 37) {
                i = 37;
            }
            Button b = new Button(Items.getPlayerHead(s.getPlayer()), inventoryClickEvent -> {
                if(inventoryClickEvent.getClick().isMouseClick()){
                    Settings.removeSetting(s.getPlayer());
                }else{
                    openSettingsUI(player, s.getPlayer());
                }
            return true;});
            selectGlobalInv.setItem(i, b.getButton());
            i++;
            if (i >= 44) {
                break;
            }
        }
        if(i <= 43){
            Button add = new Button(Items.plusButton.asQuantity(5), inventoryClickEvent -> {
                inventoryClickEvent.getWhoClicked().openInventory(addPlayerGUI());
            return true;});
            selectGlobalInv.setItem(i, add.getButton());
        }
        player.openInventory(selectGlobalInv);
    }
    public static void openSettingsUI(@NotNull HumanEntity viewer, @Nullable OfflinePlayer player){
        Settings s = Settings.getSettings(player);
        Inventory settingsInv = Bukkit.createInventory(null, 54, Component.text("Player Settings"));
        settingsInv.setContents(Items.blockButtons);
        Button back2 = new Button(Items.backButton.clone(), inventoryClickEvent -> {
            openSelectUI(inventoryClickEvent.getWhoClicked());
            return false;});
        if(player != null){
            settingsInv.setItem(4, Items.getPlayerHead(player));
        }
        settingsInv.setItem(45, back2.getButton());
        Setting fly = new Setting(s.canFly(), PlayerSettingsGUI.fly, s::setCanFly);
        Setting move = new Setting(s.canMove(), PlayerSettingsGUI.move, s::setCanMove);
        Setting chat = new Setting(s.canChat(), PlayerSettingsGUI.chat, s::setCanChat);
        Setting invulnerable = new Setting(s.isInvulnerable(), PlayerSettingsGUI.invulnerable, s::setInvulnerable);
        Setting visible = new Setting(s.isVisible(), PlayerSettingsGUI.invisible, s::setVisible);
        Setting pickup = new Setting(s.canPickupItems(), PlayerSettingsGUI.pickupItems, s::setCanPickupItems);
        Setting mine = new Setting(s.canMineBlocks(), PlayerSettingsGUI.mineBlocks, s::setCanMineBlocks);
        Setting hit = new Setting(s.canHitEntities(), PlayerSettingsGUI.hitMobs, s::setCanHitEntities);
        settingsInv.setItem(10, fly.getTopItem());
        settingsInv.setItem(11, fly.getBottomItem());
        settingsInv.setItem(19, move.getTopItem());
        settingsInv.setItem(20, move.getBottomItem());
        settingsInv.setItem(28, chat.getTopItem());
        settingsInv.setItem(29, chat.getBottomItem());
        settingsInv.setItem(37, invulnerable.getTopItem());
        settingsInv.setItem(38, invulnerable.getBottomItem());
        settingsInv.setItem(14, visible.getTopItem());
        settingsInv.setItem(15, visible.getBottomItem());
        settingsInv.setItem(23, pickup.getTopItem());
        settingsInv.setItem(24, pickup.getBottomItem());
        settingsInv.setItem(32, mine.getTopItem());
        settingsInv.setItem(33, mine.getBottomItem());
        settingsInv.setItem(41, hit.getTopItem());
        settingsInv.setItem(42, hit.getBottomItem());

        viewer.openInventory(settingsInv);
        if(viewer instanceof Player p){
            p.playSound(p, Sound.BLOCK_AMETHYST_BLOCK_STEP, 1.0f, 1.0f);
        }
    }

    public static Inventory addPlayerGUI(){
        Inventory inv = Bukkit.createInventory(null, 54, Component.text("Attach custom Settings to"));
        inv.setContents(Items.blockButtons);
        Settings s = Settings.getSettings(null);
        int i = 10;
        for(Player p : Bukkit.getOnlinePlayers()){
            if(Settings.getSettings(p)==s){
                inv.setItem(i, Items.getPlayerHead(p));
                i++;
                if(i == 17 || i == 26 || i == 35){
                    i+=2;
                }
                if(i == 18 || i == 27 || i == 36){
                    i++;
                }
                if (i > 43) {
                    break;
                }
            }
        }
        SettingsListeners.addPlayerAddToSettingsInv(inv);
        return inv;
    }
}
