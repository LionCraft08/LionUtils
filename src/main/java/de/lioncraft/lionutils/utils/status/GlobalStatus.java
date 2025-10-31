package de.lioncraft.lionutils.utils.status;

import de.lioncraft.lionapi.guimanagement.Interaction.Button;
import de.lioncraft.lionapi.guimanagement.Items;
import de.lioncraft.lionutils.Main;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class GlobalStatus extends Status implements ConfigurationSerializable {
    public static final GlobalStatus afk = new GlobalStatus(List.of(Component.text("AFK", TextColor.color(128, 128, 128))), null, false, TextColor.color(100, 100, 100), Material.CLOCK);
    public static final GlobalStatus building = new GlobalStatus(List.of(Component.text("BUILDING", TextColor.color(0, 255, 255))), null, false, TextColor.color(255, 255, 255), Material.STONE_BRICKS);

    private static final HashMap<String, GlobalStatus> globalStatusList = new HashMap<>();


    public GlobalStatus(@Nullable List<TextComponent> parts, OfflinePlayer player, boolean showInPlayerListOnly, TextColor color) {
        super(parts, player, showInPlayerListOnly, color);
    }

    public GlobalStatus(Map<String, Object> data) {
        super(data);
    }

    public GlobalStatus(@Nullable List<TextComponent> parts, OfflinePlayer player, boolean showInPlayerListOnly, TextColor color, Material material) {
        super(parts, player, showInPlayerListOnly, color, material);
    }

    public Map<String, Object> serialize(){
        return super.serialize();
    }
    public static void addGlobalStatus(GlobalStatus status){
        globalStatusList.put(status.getName(), status);
    }

    public static void removeGlobalStatus(String status){
        globalStatusList.remove(status);
        for(StatusSettings s : StatusSettings.getSettings().values()){
            if(s.getStatusType().equalsIgnoreCase("global")
            && s.getCurrentStatusName().equals(status)){
                s.resetCurrentStatus();
            }
        }
    }

    public static GlobalStatus getStatus(String name){
        return globalStatusList.get(name);
    }

    @Override
    public Status attachToPlayer(OfflinePlayer p){
        return this;
    }

    @Override
    public void update(){}

//    public ItemStack getItem(boolean isOP, boolean isActive){
//        ItemStack is;
//        if(isOP)  is= Items.get(getAsComponent(), getMaterial(), TextColor.color(getNameColor()), "- Leftclick to select", "- Rightclick to configure", "- Mousewheel to delete");
//        else is = Items.get(getAsComponent(), getMaterial(), TextColor.color(getNameColor()), "- Leftclick to select", "Global Status, can't be configured!");
//        ItemMeta im = is.getItemMeta();
//        im.getPersistentDataContainer().set(StatusListeners.disabledClickItems, PersistentDataType.BOOLEAN, true);
//        if(isActive)
//        {
//            im.setEnchantmentGlintOverride(true);
//        }
//        is.setItemMeta(im);
//        Button b = new Button(is, inventoryClickEvent -> {
//            if(inventoryClickEvent.isLeftClick()){
//                attachToPlayer((Player) inventoryClickEvent.getWhoClicked());
//                new openStatusConfigureGUILater((Player) inventoryClickEvent.getWhoClicked(), -1, null).runTaskLater(Main.getPlugin(), 1);
//            }else
//            if(inventoryClickEvent.getWhoClicked().isOp()){
//                if (this instanceof TeamStatus) return false;
//                if(inventoryClickEvent.isRightClick()){
//                    new openStatusConfigureGUILater((Player) inventoryClickEvent.getWhoClicked(), -2, this).runTaskLater(Main.getPlugin(), 1);
//                }else if(inventoryClickEvent.getClick().equals(ClickType.MIDDLE)){
//                    GlobalStatus.removeGlobalStatus(this);
//                }else return false;
//            }else return false;
//        return true;});
//        return b.getButton();
//    }

    public static HashMap<String, GlobalStatus> getGlobalStatusList(){
        return globalStatusList;
    }

    public static List<GlobalStatus> getGlobalStatusesList(){
        return globalStatusList.values().stream().toList();
    }
}
