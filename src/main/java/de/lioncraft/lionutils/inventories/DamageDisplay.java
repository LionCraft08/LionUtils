package de.lioncraft.lionutils.inventories;

import de.lioncraft.lionapi.guimanagement.Interaction.Button;
import de.lioncraft.lionapi.guimanagement.Interaction.Setting;
import de.lioncraft.lionapi.guimanagement.Items;
import de.lioncraft.lionutils.Main;
import io.papermc.paper.scoreboard.numbers.NumberFormat;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.scoreboard.*;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.util.Map;


public final class DamageDisplay implements ConfigurationSerializable {
    private static DamageDisplay damageDisplay;
    private static Inventory GUI;
    public static void Init(){
        GUI = Bukkit.createInventory(null, 54, Component.text("Damage Display Config", TextColor.color(255, 128, 0)));
        GUI.setContents(Items.blockButtons);
        GUI.setItem(49, Items.closeButton);
        Button back = new Button(Items.getBackButton("Main GUI"), inventoryClickEvent -> {
            MainGUI.open(inventoryClickEvent.getWhoClicked());
        return false;});
        GUI.setItem(45, back.getButton());
        Setting tabList = new Setting(damageDisplay.isTabListActive(), Items.get(Component.text("TabList Display", TextColor.color(255, 128, 0)), Material.PAPER, "Click to toggle the Damage ", "Display in the Player List"), b -> {
            damageDisplay.setTabListActive(b);
        });
        GUI.setItem(10, tabList.getTopItem());
        GUI.setItem(19, tabList.getBottomItem());
        Setting chatDisplay = new Setting(damageDisplay.isChatActive(), Items.get(Component.text("Chat Damage Display", TextColor.color(255, 0, 128)), Material.KNOWLEDGE_BOOK, "Click to toggle the Damage ", "Display in Chat"), b -> {
            damageDisplay.setChatActive(b);
        });
        GUI.setItem(12, chatDisplay.getTopItem());
        GUI.setItem(21, chatDisplay.getBottomItem());
        Setting chatCause = new Setting(damageDisplay.isChatShowCause(), Items.get(Component.text("Chat Damage Cause", TextColor.color(255, 0, 200)), Material.CREEPER_HEAD, "Click to toggle the Display ", "of the Damage Cause"), b -> {
            damageDisplay.setChatShowCause(b);
        });
        GUI.setItem(14, chatCause.getTopItem());
        GUI.setItem(23, chatCause.getBottomItem());
        Setting chatHealth = new Setting(damageDisplay.isChatShowPlayerHealth(), Items.get(Component.text("Chat Show Current Health", TextColor.color(255, 128, 0)), Material.PLAYER_HEAD, "Click to toggle the Display " + "of the Player's current health", "in the Damage Message"), b -> {
            damageDisplay.setChatShowPlayerHealth(b);
        });
        GUI.setItem(16, chatHealth.getTopItem());
        GUI.setItem(25, chatHealth.getBottomItem());
        Setting isHearts = new Setting(damageDisplay.isChatShowPlayerHealth(), Items.get(Component.text("Display Damage in Hearts", TextColor.color(255, 128, 0)), Material.HEART_POTTERY_SHERD, "If enabled, displays damage & health ", "in Hearts (Default: max. 10),", "if disabled, in HP (Max. 20)"), b -> {
            damageDisplay.setNumberOfHearts(b);
        });
        GUI.setItem(13, isHearts.getTopItem());
        GUI.setItem(22, isHearts.getBottomItem());
        Button update = new Button(Items.get("Reload", Material.HEART_OF_THE_SEA, "Click to reload the Tab List"), inventoryClickEvent -> {
            Component c = damageDisplay.updateTabList();
            if(inventoryClickEvent.getWhoClicked() instanceof Player p){
                p.sendMessage(c);
            }
            if(inventoryClickEvent.getWhoClicked() instanceof Player p){
                p.playSound(p, Sound.UI_BUTTON_CLICK, 1.0f, 1.0f);
            }
        return false;});
        GUI.setItem(53, update.getButton());

    }
    public static void open(HumanEntity player){
        player.openInventory(GUI);
    }
    public static void deserialize(){
        YamlConfiguration yml = getConfig();
        damageDisplay = (DamageDisplay) yml.get("data");
        if(damageDisplay == null){
            damageDisplay = new DamageDisplay();
        }
    }
    public static void save(){
        YamlConfiguration yml = getConfig();
        yml.set("data", damageDisplay);
        try {
            yml.save(getFile());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    private static YamlConfiguration getConfig(){
        File f2 = getFile();
        if(!f2.exists()){
            try {
                f2.createNewFile();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        return YamlConfiguration.loadConfiguration(f2);
    }

    private static @NotNull File getFile() {
        File f = Main.getPlugin().getDataFolder();
        return new File(f, "damageDisplayConfig.yml");
    }

    private boolean chatActive, tabListActive, chatShowCause, chatShowPlayerHealth, numberOfHearts;
    @Override
    public @NotNull Map<String, Object> serialize() {
        return Map.of("chatActive",chatActive , "tabListActive",tabListActive ,"chatShowCause",chatShowCause ,"chatShowPlayerHealth",chatShowPlayerHealth, "numberOfHearts", numberOfHearts);
    }
    public DamageDisplay(Map<String, Object> map){
        chatActive = (boolean) map.get("chatActive");
        tabListActive = (boolean) map.get("tabListActive");
        chatShowCause = (boolean) map.get("chatShowCause");
        chatShowPlayerHealth = (boolean) map.get("chatShowPlayerHealth");
        if(map.containsKey("numberOfHearts")) numberOfHearts = (boolean) map.get("numberOfHearts");
        else numberOfHearts = false;
    }
    public DamageDisplay(){
        chatActive = false;
        tabListActive = false;
        chatShowCause =true;
        chatShowPlayerHealth = false;
        numberOfHearts = false;
    }
    public boolean isChatActive() {
        return chatActive;
    }
    public void setChatActive(boolean chatActive) {
        this.chatActive = chatActive;
    }
    public boolean isTabListActive() {
        return tabListActive;
    }
    public void setTabListActive(boolean tabListActive) {
        this.tabListActive = tabListActive;
        Scoreboard sc = Bukkit.getScoreboardManager().getMainScoreboard();
        Objective o = sc.getObjective("hp");
        if(tabListActive){
            if(o == null){
                o = sc.registerNewObjective("hp", Criteria.HEALTH, Component.text("Health", TextColor.color(255, 0, 0)), RenderType.HEARTS);
                o.setDisplaySlot(DisplaySlot.PLAYER_LIST);
                o.numberFormat(NumberFormat.noStyle());

                updateTabList();
            }
        }else{
            if(o != null) o.unregister();
        }
    }
    public Component updateTabList(){
        Scoreboard sc = Bukkit.getScoreboardManager().getMainScoreboard();
        if(tabListActive){
            Objective o = sc.getObjective("hp");
            if(o == null){
                return Component.text("TabList Damage Display is currently not active!");
            }
            for(Player p : Bukkit.getOnlinePlayers()){
                p.sendHealthUpdate();
                Score score = o.getScore(p);
                if (!score.isScoreSet()) {
                    double d = p.getHealth();
                    p.setHealth(d);
                }
            }
            return Component.text("Updated the Display!");
        }
        return Component.text("TabList Damage Display is currently not active!");
    }
    public boolean isChatShowCause() {
        return chatShowCause;
    }
    public boolean isChatShowPlayerHealth() {
        return chatShowPlayerHealth;
    }
    public void setChatShowPlayerHealth(boolean chatShowPlayerHealth) {
        this.chatShowPlayerHealth = chatShowPlayerHealth;
    }
    public void setChatShowCause(boolean chatShowCause) {
        this.chatShowCause = chatShowCause;
    }

    public static DamageDisplay getDamageDisplay() {
        return damageDisplay;
    }

    public boolean isNumberOfHearts() {
        return numberOfHearts;
    }

    public void setNumberOfHearts(boolean numberOfHearts) {
        this.numberOfHearts = numberOfHearts;
    }
}
