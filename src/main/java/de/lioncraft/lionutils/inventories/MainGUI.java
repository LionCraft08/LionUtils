package de.lioncraft.lionutils.inventories;

import de.lioncraft.lionapi.guimanagement.Interaction.Button;
import de.lioncraft.lionapi.guimanagement.Items;
import de.lioncraft.lionutils.utils.status.Inventories;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

public abstract class MainGUI {
    private static Inventory opInv, mainInv;
    public static void Initialize(){
        opInv = Bukkit.createInventory(null, 54, Component.text("LionSystems", TextColor.color(255, 0, 255)));
        opInv.setContents(Items.blockButtons);
        opInv.setItem(49, Items.closeButton);
        mainInv = Bukkit.createInventory(null, 54, Component.text("LionSystems Main", TextColor.color(0, 255, 255)));
        mainInv.setContents(Items.blockButtons);
        mainInv.setItem(49, Items.closeButton);
        Button settings = new Button(Items.get(Component.text("Settings", TextColor.color(255, 0, 0)), Material.REPEATING_COMMAND_BLOCK, "Click to open the Settings"), inventoryClickEvent -> {
            if(inventoryClickEvent.getWhoClicked() instanceof Player p){
                SettingsGUI.open(p);
                p.playSound(p, Sound.UI_BUTTON_CLICK, 0.6f, 1.0f);
            }
            return false;
        });
        opInv.setItem(53, settings.getButton());
        Button playerManagement = new Button(Items.get(Component.text("Player Management", TextColor.color(0, 255, 255)), Material.PLAYER_HEAD, "Opens a GUI to manage Player Abilities"), inventoryClickEvent -> {
            if(inventoryClickEvent.getWhoClicked() instanceof Player p){
                PlayerSettingsGUI.openSelectUI(p);
                p.playSound(p, Sound.UI_BUTTON_CLICK, 0.6f, 1.0f);
            }
            return false;
        });
        opInv.setItem(10, playerManagement.getButton());
        Button status = new Button(Items.get("Status", Material.NAME_TAG, "Click to configure your status."), inventoryClickEvent -> {
            Inventories.openMainMenu((Player) inventoryClickEvent.getWhoClicked());
        return false;});
        opInv.setItem(12, status.getButton());
        mainInv.setItem(10, status.getButton());
        if(Bukkit.getPluginManager().isPluginEnabled("LionWaypoints")){
            Button wp = new Button(Items.get(Component.text("LionWaypoints"), Material.RECOVERY_COMPASS, "Click to open the Waypoint Menu"), inventoryClickEvent -> {
                ((Player)inventoryClickEvent.getWhoClicked()).performCommand("wp");
            return false;});

            opInv.setItem(20, wp.getButton());
            mainInv.setItem(20, wp.getButton());
        }
        Button DamageDisplay = new Button(Items.get(Component.text("Damage Display", TextColor.color(255, 128, 0)), Material.HEART_POTTERY_SHERD, "Click to open a Menu to ", "configure the Health Display Settings"), inventoryClickEvent -> {
            de.lioncraft.lionutils.inventories.DamageDisplay.open(inventoryClickEvent.getWhoClicked());
        return false;});
        opInv.setItem(14, DamageDisplay.getButton());
        Button openOpUtils = new Button(Items.get(Component.text("OPUtils"), Material.COMPARATOR, "Some useful stuff for Operators"), e -> {
            opUtils.openUI(e.getWhoClicked());
        return false;});
        opInv.setItem(16, openOpUtils.getButton());
    }
    public static void open(HumanEntity player){
        if(player.isOp()){
            player.openInventory(opInv);
        }else{
            player.openInventory(mainInv);
        }
    }
}
