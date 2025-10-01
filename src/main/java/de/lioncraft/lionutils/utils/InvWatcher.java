package de.lioncraft.lionutils.utils;

import de.lioncraft.lionapi.guimanagement.Interaction.Button;
import de.lioncraft.lionapi.guimanagement.Items;
import de.lioncraft.lionapi.guimanagement.buttons;
import de.lioncraft.lionapi.guimanagement.createItem;
import de.lioncraft.lionutils.Main;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class InvWatcher {
    private static HashMap<Player, InvWatcher> active;
    private static HashMap<Inventory, InvWatcher> active2;
    private static boolean update, changes;
    private List<Player> viewers;
    private final Inventory inv;
    private final Player player;
    public InvWatcher(@Nullable List<Player> viewer, @NotNull Player player){
        viewers = viewer;
        if(viewers == null){
            viewers = new ArrayList<>();
        }
        this.player = player;
        inv = Bukkit.createInventory(null, 54, Component.text(player.getName() + "'s Inventory", TextColor.color(255, 128, 0)));
        for(int i = 36; i < 54; i++){
            inv.setItem(i, Items.blockButton);
        }
        Button b = new Button(Items.get(Component.text("Ender Chest"), Material.ENDER_CHEST, "Click to open " + player.getName() + "'s", "Enderchest."), inventoryClickEvent -> {
            inventoryClickEvent.getWhoClicked().openInventory(player.getEnderChest());
            return true;
        });
        inv.setItem(45, b.getButton());
        inv.setItem(47, null);
        inv.setItem(48, null);
        inv.setItem(50, null);
        inv.setItem(51, null);
        inv.setItem(53, null);
        active.put(player, this);
        active2.put(inv, this);
        for(Player p : viewers){
            open(p);
        }
    }
    public void open(Player p){
        if (!viewers.contains(p)) {
            viewers.add(p);
        }
        p.openInventory(inv);
        update();
    }
    public void close(Player viewer){
        viewers.remove(viewer);
        if(viewers.isEmpty()){
            active.remove(player);
            active2.remove(inv);
        }
    }
    public void changeItem(int invSlot){
        int i = getPlayerSlot(invSlot);
        if(i < 41){
            player.getInventory().setItem(i, inv.getItem(invSlot));
        }
    }
    public void changeItem(int invSlot, ItemStack is){
        int i = getPlayerSlot(invSlot);
        if(i < 41){
            player.getInventory().setItem(i, is);
        }
    }
    public void update(){
        for(int i = 0; i < 36; i++){
            inv.setItem(i, player.getInventory().getItem(i));
        }
        inv.setItem(47, player.getInventory().getBoots());
        inv.setItem(48, player.getInventory().getLeggings());
        inv.setItem(50, player.getInventory().getChestplate());
        inv.setItem(51, player.getInventory().getHelmet());
        inv.setItem(53, player.getInventory().getItemInOffHand());
    }
    public void updateLater(int slot){
        list.add(new UpdateDelay(slot).runTaskLater(Main.getPlugin(), 1));
    }

    public void updatePlayer(int slotPlayer){
        if (slotPlayer < 36) {
            inv.setItem(slotPlayer, player.getInventory().getItem(slotPlayer));
        }else{
            inv.setItem(47, player.getInventory().getBoots());
            inv.setItem(48, player.getInventory().getLeggings());
            inv.setItem(50, player.getInventory().getChestplate());
            inv.setItem(51, player.getInventory().getHelmet());

        }
        inv.setItem(53, player.getInventory().getItem(EquipmentSlot.OFF_HAND));
    }
    private int getInvSlot(int playerInvSlot){
        if(playerInvSlot < 36){
            return playerInvSlot;
        } else switch (playerInvSlot){
            case 36: return 47;
            case 37: return 48;
            case 38: return 50;
            case 39: return 51;
            case 40: return 53;
        }
        throw new RuntimeException(playerInvSlot + " is not a valid Slot in a Player's inventory");
    }
    private int getPlayerSlot(int invSlot){
        if(invSlot < 36){
            return invSlot;
        } else switch (invSlot){
            case 47: return 36;
            case 48: return 37;
            case 50: return 38;
            case 51: return 39;
            case 53: return 40;
        }
        return 41;
    }

    public static void Initialize(){
        active = new HashMap<>();
        active2 = new HashMap<>();
        changes = Main.getPlugin().getConfig().getBoolean("inventory-view.allow-changes");
        update = Main.getPlugin().getConfig().getBoolean("inventory-view.update");
    }
    private List<BukkitTask> list = new ArrayList<>();
    private class UpdateDelay extends BukkitRunnable {
        private int slot;
        public UpdateDelay(int slot) {
            this.slot = slot;
        }
        @Override
        public void run() {
            changeItem(slot);
        }
    }
    public static InvWatcher getWatcher(Player target){
        if(active.get(target) == null){
            new InvWatcher(null, target);
        }
        return active.get(target);
    }
    public static InvWatcher getWatcher(Inventory inv){
        return active2.get(inv);
    }
    public static boolean hasViewers(Player p){
        return active.containsKey(p);
    }
    public static boolean hasViewers(Inventory p){
        return active2.containsKey(p);
    }
    public static void open(Player viewer, Player target){
        InvWatcher iv = getWatcher(target);
        iv.open(viewer);
    }
}
