package de.lioncraft.lionutils.inventories;

import de.lioncraft.lionapi.guimanagement.Interaction.Button;
import de.lioncraft.lionapi.guimanagement.Items;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

public abstract class TimerGUI {
    private static Inventory inv;
    private static void init(){
        inv = Bukkit.createInventory(null, 54, Component.text("Timer Config", TextColor.color(0, 255, 255)));
        inv.setContents(Items.blockButtons);
        inv.setItem(49, Items.closeButton);
        Button back = new Button(Items.getBackButton("Settings"), inventoryClickEvent -> {
            if(inventoryClickEvent.getWhoClicked() instanceof Player p){
                SettingsGUI.open(p);
            }
        return false;});
        inv.setItem(45, back.getButton());
    }
    /*public static void tick(){
        if(!inv.getViewers().isEmpty()){
            inv.setItem(19, Items.get("Days"));
        }
    }*/
}
