package de.lioncraft.lionutils.listeners;

import de.lioncraft.lionapi.guimanagement.Items;
import de.lioncraft.lionapi.guimanagement.buttons;
import de.lioncraft.lionapi.messageHandling.DM;
import de.lioncraft.lionutils.utils.InvWatcher;
import io.papermc.paper.event.player.PlayerInventorySlotChangeEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.*;

public class InvListeners implements Listener {
    @EventHandler
    public void onInvChange(PlayerInventorySlotChangeEvent e){
        if(InvWatcher.hasViewers(e.getPlayer())){
            InvWatcher.getWatcher(e.getPlayer()).updatePlayer(e.getSlot());
        }
    }
    @EventHandler
    public void onInvClick(InventoryClickEvent e){
        if(InvWatcher.hasViewers(e.getClickedInventory())){
            if(e.getCurrentItem() != null){
                if(e.getCurrentItem().isSimilar(Items.blockButton)){
                    e.setCancelled(true);
                    return;
                }
            }
            if((e.getAction().equals(InventoryAction.PLACE_ALL)&&e.getCurrentItem() == null)|| e.getAction().equals(InventoryAction.PICKUP_ALL)||e.getAction().equals(InventoryAction.SWAP_WITH_CURSOR)){
                InvWatcher.getWatcher(e.getClickedInventory()).changeItem(e.getSlot(), e.getCursor());
            }else InvWatcher.getWatcher(e.getClickedInventory()).updateLater(e.getSlot());

        }

    }

    @EventHandler
    public void onDrag(InventoryCloseEvent e){
        if(InvWatcher.hasViewers(e.getInventory())){
            InvWatcher.getWatcher(e.getInventory()).close((Player) e.getPlayer());
        }
    }
    @EventHandler
    public void onDrag(InventoryDragEvent e){
        if(InvWatcher.hasViewers(e.getInventory())){
            InvWatcher invWatcher = InvWatcher.getWatcher(e.getInventory());
            for(Integer i : e.getInventorySlots()){
                invWatcher.changeItem(i, e.getNewItems().get(i));
            }
        }
    }
}
