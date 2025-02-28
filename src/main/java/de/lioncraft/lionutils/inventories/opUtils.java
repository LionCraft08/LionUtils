package de.lioncraft.lionutils.inventories;

import de.lioncraft.lionapi.guimanagement.Interaction.Button;
import de.lioncraft.lionapi.guimanagement.Items;
import de.lioncraft.lionapi.messageHandling.DM;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

public final class opUtils {
    private opUtils(){}
    private static Inventory inv;
    public static void openUI(HumanEntity player){
        if(inv == null){
            init();
        }
        player.openInventory(inv);
    }
    private static void init(){
        inv = Bukkit.createInventory(null, 54, Component.text("Operator Utils", TextColor.color(0, 255, 255)));
        inv.setContents(Items.blockButtons);
        inv.setItem(49, Items.closeButton);
        Button back = new Button(Items.getBackButton("Main GUI"), inventoryClickEvent -> {
            MainGUI.open(inventoryClickEvent.getWhoClicked());
        return false;});
        inv.setItem(45, back.getButton());
        Button heal = new Button(Items.get(Component.text("Heal", TextColor.color(255, 128, 0)), Material.ENCHANTED_GOLDEN_APPLE, "Heals every player and resets their nourishment"), inventoryClickEvent -> {
            int i = 0;
            for(Player p : Bukkit.getOnlinePlayers()){
                heal(p);
                i++;
            }
            if(inventoryClickEvent.getWhoClicked() instanceof Player p){
                p.sendMessage(DM.info("You have healed " + i + " Players!"));
                p.playSound(p, Sound.ENTITY_ILLUSIONER_MIRROR_MOVE, 0.5f, 1.0f);
            }
        return false;});
        inv.setItem(10, heal.getButton());
        Button tp = new Button(Items.get(Component.text("Teleport"), Material.ENDER_PEARL, "Teleports every Player to you"), inventoryClickEvent -> {
            Player clicker = (Player) inventoryClickEvent.getWhoClicked();
            int i = 0;
            for (Player p : Bukkit.getOnlinePlayers()){
                if (p != clicker){
                    i++;
                    p.teleport(clicker.getLocation());
                }
            }
            if (i>0){
                clicker.sendMessage(DM.info("Teleported "+i+" Players to you"));
                clicker.playSound(clicker, Sound.ENTITY_PLAYER_TELEPORT, 1.0f, 1.0f);
            }else{
                clicker.sendMessage(DM.error("Could not find any Player to teleport"));
                clicker.playSound(clicker, Sound.ENTITY_SILVERFISH_HURT, 1.0f, 1.0f);
            }
        return false;});
        inv.setItem(12, tp.getButton());
    }
    public static void heal(Player p){
        p.setHealth(20);
        p.setSaturation(5);
        p.setFoodLevel(20);
    }
}
