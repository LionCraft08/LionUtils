package de.lioncraft.lionutils.listeners;

import de.lioncraft.lionutils.Main;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.inventory.PrepareAnvilEvent;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.view.AnvilView;

public class AnvilFixes implements Listener {
    private static boolean fixToExpensive;
    public AnvilFixes(){
        fixToExpensive = Main.getPlugin().getConfig().getBoolean("settings.block-to-expensive");
    }

    @EventHandler
    public void onAnvil(PrepareAnvilEvent e){
        if(fixToExpensive){
            if(e.getView().getRepairCost() > 39){
                e.getView().bypassEnchantmentLevelRestriction(true);
                e.getView().setRepairCost(e.getView().getRepairCost()+1);
                e.getView().setMaximumRepairCost(999999);
            }
        }
    }
}
