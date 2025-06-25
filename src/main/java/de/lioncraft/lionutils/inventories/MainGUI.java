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
    }
    public static void open(HumanEntity player){
        if(player.isOp()){
            player.openInventory(opInv);
        }else{
            player.openInventory(mainInv);
        }
    }
}
