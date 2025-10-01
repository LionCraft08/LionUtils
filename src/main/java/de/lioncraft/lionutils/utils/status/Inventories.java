package de.lioncraft.lionutils.utils.status;

import de.lioncraft.lionapi.guimanagement.Interaction.Button;
import de.lioncraft.lionapi.guimanagement.Interaction.MultipleSelection;
import de.lioncraft.lionapi.guimanagement.Interaction.Setting;
import de.lioncraft.lionapi.guimanagement.Items;
import de.lioncraft.lionapi.guimanagement.MainMenu;
import de.lioncraft.lionapi.messageHandling.ColorGradient;
import de.lioncraft.lionutils.Main;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Bukkit;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.block.banner.Pattern;
import org.bukkit.block.banner.PatternType;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.AnvilInventory;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BannerMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class Inventories {
    public static void openConfigurePartMenu(HumanEntity player, int part, @NotNull Status status){
        StatusPart c = status.getList().get(part);
        int color = 0;
        List<ItemStack> list = new ArrayList<>();
        int i = 0;
        for(Material m : getMap().keySet()){
            list.add(Items.get(Component.translatable(m.getItemTranslationKey()), m, Component.text("Click to change the Color", TextColor.color(255, 255, 255))));
            if(c.getColor() != null){
                if(getColor(m).value() == c.getColor().value()){
                    color = i;
                }
            }else color = indexOf(Material.WHITE_DYE);
            i++;
        }
        Inventory inv = Bukkit.createInventory(null, 54, Component.text("Configure Status"));
        MultipleSelection ms = new MultipleSelection(list, color, (currentState, currentButton, event) -> {
            c.setColor(getMap().get(currentButton.getType()));
            status.update();
            inv.setItem(4, status.getItem(part));
        });
        inv.setContents(Items.blockButtons);
        inv.setItem(49, Items.closeButton);
        Button back = new Button(Items.backButton, event -> {
            openConfigureStatusMenu(event.getWhoClicked(), status);
        return true;});
        Button content = new Button(Items.get(status.getRawContent(part), Material.ANVIL, "Click to change the Text"), event -> {
            openChangeTextInventory(status, part, event.getWhoClicked());
        return true;});

        Button gradiant = new Button(getGradiantItem(c.hasGradiant()), inventoryClickEvent -> {
            c.setHasGradiant(!c.hasGradiant());
            status.update();
            openConfigurePartMenu(player, part, status);
        return true;});
        inv.setItem(31, gradiant.getButton());
        if(c.hasGradiant()){
            int color2 = 0;
            List<ItemStack> list2 = new ArrayList<>();
            int i2 = 0;
            for(Material m : getMap().keySet()){
                list2.add(Items.get(Component.translatable(m.getItemTranslationKey()), m, Component.text("Click to change the end color of the Gradiant", TextColor.color(255, 255, 255))));
                if(c.getGradiantColor() != null){
                    if(getColor(m).value() == c.getGradiantColor().value()){
                        color2 = i2;
                    }
                }else color2 = indexOf(Material.WHITE_DYE);
                i2++;
            }
            MultipleSelection ms2 = new MultipleSelection(list2, color2, (currentState, currentButton, event) -> {
                c.setGradiantColor(getMap().get(currentButton.getType()));
                status.update();
                inv.setItem(4, status.getItem(part));
            });
            inv.setItem(42, ms2.getButton());
        }
        inv.setItem(45, back.getButton());
        inv.setItem(20, content.getButton());
        inv.setItem(24, ms.getButton());
        inv.setItem(4, status.getItem(part));

        player.openInventory(inv);
    }
    private static ItemStack getGradiantItem(boolean enabled){
        String s = "enable";
        if(enabled){
            s = "disable";
        }
        ItemStack is = Items.get(ColorGradient.getNewGradiant("Gradiant", TextColor.color(255, 0, 255), TextColor.color(0, 255, 255)), Material.LIGHT_BLUE_BANNER, "Click to " + s + " a color gradiant");
        BannerMeta bm = (BannerMeta) is.getItemMeta();
        bm.setPatterns(List.of(new Pattern(DyeColor.PURPLE, PatternType.GRADIENT)));
        is.setItemMeta(bm);
        return is;
    }
    public static void openConfigureStatusMenu(HumanEntity player, Status status){
        Inventory inv = Bukkit.createInventory(null, 54, Component.text("Configure Status"));
        inv.setContents(Items.blockButtons);
        inv.setItem(49, Items.closeButton);
        Button back = new Button(Items.getBackButton("Status"), inventoryClickEvent -> {
            new openStatusConfigureGUILater((Player) inventoryClickEvent.getWhoClicked(), -1, null).runTaskLater(Main.getPlugin(), 1);
        return true;});
        inv.setItem(45, back.getButton());
        boolean b = true;
        int i = 10;
        for(int j = 0; j < status.getList().size();j++){
            if(i == 17||i==18) i = 19;
            if (i==25){
                b = false;
                break;
            }
            inv.setItem(i, status.getItem(j));
            i++;
        }
        if(b){
            Button add = new Button(Items.plusButton, inventoryClickEvent -> {
                status.addPart(Component.text("EmptyPart"));
                openConfigureStatusMenu(inventoryClickEvent.getWhoClicked(), status);
            return true;});
            inv.setItem(i, add.getButton());
        }

        Component c = status.getAsComponent(null);
        List<ItemStack> list = new ArrayList<>();
        for(Material m : getMap().keySet()){
            list.add(Items.get(Component.translatable(m.getItemTranslationKey()), m, Component.text("Click to change Name Color"), Component.text("The current Text looks like this: ", TextColor.color(255, 255, 255)), c.append(Component.text(player.getName(), getColor(m)))));
        }
        MultipleSelection ms = new MultipleSelection(list, getMap().values().stream().toList().indexOf(status.getNameColor()), (i1, itemStack, inventoryClickEvent) -> {
            status.setNameColor(getMap().get(itemStack.getType()));
        });
        inv.setItem(38, ms.getButton());
        player.openInventory(inv);
    }
    public static void openMainMenu(Player player){
        Inventory inv = Bukkit.createInventory(null, 54, Component.text("Status"));
        inv.setContents(Items.blockButtons);
        inv.setItem(49, Items.closeButton);
        inv.setItem(45, MainMenu.getToMainMenuButton());
        StatusSettings s = StatusSettings.getSettings(player);
        int i = 10;
        if(GlobalStatus.getGlobalStatusList().size() > 7){
            i = 9;
        }

        for(GlobalStatus status : GlobalStatus.getGlobalStatusList()){
            inv.setItem(i, status.getItem(player.isOp(), s.isCurrentStatus(status)));
            i++;
            if(i == 18) break;
        }

        if(player.isOp() && i < 18){
            Button add = new Button(Items.plusButton, inventoryClickEvent -> {
                GlobalStatus.addGlobalStatus(new GlobalStatus(null, null, false, TextColor.color(255, 255, 255), Material.NAME_TAG));
                new openStatusConfigureGUILater((Player) inventoryClickEvent.getWhoClicked(), -1, null).runTaskLater(Main.getPlugin(), 1);
            return true;});
            inv.setItem(i, add.getButton());
        }

        i = 0;
        boolean keepBorders = false;
        int amount = s.getCreatedStatus().size();
        if(amount <= 10){
            i = 28;
            keepBorders = true;
        } else if (amount <= 12) {
            i = 27;
        }else if (amount <= 18){
            i = 18;
        }
        for(Status status : s.getCreatedStatus()){
            inv.setItem(i, status.getItem(player.isOp()));
            i++;
            if((i+3)%9==0){
                if(keepBorders){
                    i+=4;
                }else i+=3;
            }
        }
        if(amount < 18){
            Button b = new Button(Items.plusButton.asQuantity(2), inventoryClickEvent -> {
                StatusSettings.getSettings(player).getCreatedStatus().add(new Status(null, player, false, TextColor.color(255, 255, 255), Material.NAME_TAG));
                new openStatusConfigureGUILater(player, -1, null).runTaskLater(Main.getPlugin(), 1);
            return true;});
            if(i ==45 || i ==46){
                i = 42;
            }
            inv.setItem(i, b.getButton());
        }
        Setting autoToggle = new Setting(s.isAutoStatus(), getAutoToggle(), s::setAutoStatus);
        inv.setItem(34, autoToggle.getTopItem());
        inv.setItem(43, autoToggle.getBottomItem());
        Setting enabled = new Setting(s.isEnabled(), getEnabled(), s::setEnabled);
        inv.setItem(35, enabled.getTopItem());
        inv.setItem(44, enabled.getBottomItem());

        player.openInventory(inv);
    }

    public static void openGradiantCreator(String text, HumanEntity player){
        Inventory inv = Bukkit.createInventory(null, 54, Component.text("Gradiant Creator"));
    }
    private static ItemStack autoToggle, enabled;
    public static ItemStack getAutoToggle(){
        if(autoToggle == null){
            autoToggle = Items.get(Component.text("Auto Status"), Material.OBSERVER, "Sets if your Status gets automatically", "changed based on your current activity.");
        }
        return autoToggle;
    }
    public static ItemStack getEnabled(){
        if(enabled == null){
            enabled = Items.get(Component.text("Enabled"), Material.LEVER, "Sets if your Status is enabled");
        }
        return enabled;
    }
    private static void openChangeTextInventory(Status status, int part, HumanEntity viewer){
        AnvilInventory inv = (AnvilInventory) viewer.openAnvil(null, true).getTopInventory();
        inv.setFirstItem(Items.get(status.getRawContent(part), Material.PAPER, "Enter the new Content above"));
        inv.setResult(StatusSettings.getDeny());
        StatusListeners.registerNewInventory(inv, part, status);
    }

    @ApiStatus.Internal
    private static final Map<Material, TextColor> itemColorMap = new HashMap<>(Map.of(Material.RED_DYE, TextColor.color(255, 0, 0),
            Material.ORANGE_DYE, TextColor.color(255, 128, 0), Material.YELLOW_DYE, TextColor.color(255, 255, 0),
            Material.LIME_DYE, TextColor.color(128, 255, 0), Material.GREEN_DYE, TextColor.color(0, 200, 50),
            Material.CYAN_DYE, TextColor.color(0, 150, 150), Material.LIGHT_BLUE_DYE, TextColor.color(0, 255, 255),
            Material.BLUE_DYE, TextColor.color(0, 0, 255), Material.PURPLE_DYE, TextColor.color(128, 0, 255),
            Material.MAGENTA_DYE, TextColor.color(255, 0, 128)));
    private static TextColor getColor(Material material){
        return getMap().get(material);
    }
    private static  Map<Material, TextColor> getMap(){
        if(!itemColorMap.containsKey(Material.WHITE_DYE)){
            itemColorMap.put(Material.PINK_DYE, TextColor.color(255, 0, 255));
            itemColorMap.put(Material.WHITE_DYE, TextColor.color(255, 255, 255));
            itemColorMap.put(Material.BLACK_DYE, TextColor.color(0, 0, 0));
            itemColorMap.put(Material.BROWN_DYE, TextColor.color(82, 47, 29));
            itemColorMap.put(Material.GRAY_DYE, TextColor.color(128, 128, 128));
        }
        return itemColorMap;
    }
    private static List<ItemStack> getItems(){
        List<ItemStack> finalColorItems = new ArrayList<>();
        for (Material m : getMap().keySet()) {
            ItemStack is = new ItemStack(m);
            ItemMeta im = is.getItemMeta();
            im.displayName(Component.text("Sets the Color to ").append(Component.text(m.toString().replaceAll("DYE", ""), getMap().get(m))));
            is.setItemMeta(im);
            finalColorItems.add(is);
        }
        return finalColorItems;
    }
    private static int indexOf(Material m){
        int i =0;
        for(Material material : getMap().keySet()){
            if(material == m){
                break;
            }
            i++;
        }
        if(i > getMap().size()){
            i = indexOf(Material.WHITE_DYE);
        }
        return i;
    }


}
