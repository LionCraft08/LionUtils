package de.lioncraft.lionutils.utils.spectator;

import de.lioncraft.lionapi.guimanagement.Items;
import de.lioncraft.lionapi.messageHandling.lionchat.LionChat;
import de.lioncraft.lionutils.listeners.SpectatorListeners;
import de.lioncraft.lionutils.utils.InvWatcher;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;

import java.util.HashMap;

public final class SpectatorItems {
    private static HashMap<String, ItemStack> spectatorItems = new HashMap<>();
    private static HashMap<String, ItemInteractFunction> spectatorFunctions = new HashMap<>();
    public static ItemStack getItemStack(String s){
        return new ItemStack(Material.WHITE_DYE); //TODO()
    }
    public static void init(){
        addItem(Items.get(Component.text("Currently visible", TextColor.color(10, 180, 0)),
                Material.GREEN_DYE, "<green>Click to become invisible", "<green>to others"),
                "visible", (is, p, ct) -> {
                    ct.setCancelled(true);
                    p.getInventory().setItem(ct.getHand(), getItemStack("invisible"));
                    SpectatorManager.getSpectatorPlayer(p).setInvisible(true);
                });
        addItem(Items.get(Component.text("Currently invisible", TextColor.color(200, 50, 50)),
                Material.GRAY_DYE, "<red>Click to become visible", "<red>again"),
                "invisible", (is, p, ct) -> {
                    ct.setCancelled(true);
                    p.getInventory().setItem(ct.getHand(), getItemStack("visible"));
                    SpectatorManager.getSpectatorPlayer(p).setInvisible(false);
                });
        addItem(Items.get(Component.text("Open Inventory", TextColor.color(0, 150, 150)),
                Material.CHEST, "<#FFFFFF>Open the targeted Player's Inv"),
                "inventory", (is, p, ct) -> {
                    ct.setCancelled(true);
                    Entity et = p.getTargetEntity(10, true);
                    if (et instanceof Player target){
                        InvWatcher.open(p, target);
                    }else LionChat.sendSystemMessage("You need to look at a player", p);
                });
    }

    private static void addItem(ItemStack is, String id, ItemInteractFunction onClick){
        is.editMeta(itemMeta -> {
            itemMeta.getPersistentDataContainer().set(SpectatorListeners.spectatorItemKey, PersistentDataType.STRING, id);
        });
        spectatorItems.put(id, is);
        spectatorFunctions.put(id, onClick);
    }




    @FunctionalInterface
    public interface ItemInteractFunction{
        void onClick(ItemStack is, Player p, PlayerInteractEvent ct);
    }
}
