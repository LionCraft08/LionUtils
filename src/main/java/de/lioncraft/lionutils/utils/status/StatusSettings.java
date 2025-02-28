package de.lioncraft.lionutils.utils.status;

import de.lioncraft.lionapi.guimanagement.Items;
import de.lioncraft.lionapi.messageHandling.DM;
import de.lioncraft.lionutils.Main;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class StatusSettings implements ConfigurationSerializable {
    private static boolean enableStatusByDefault = Main.getPlugin().getConfig().getBoolean("settings.status.enabled-by-default");
    private static final ItemStack allow = Items.get("Click to change", Material.GREEN_WOOL, "Click to change the Content");
    private static final ItemStack deny = Items.get(Component.text("INVALID NAME"), Material.RED_WOOL, "The Content can't be empty.");

    private static final HashMap<UUID, StatusSettings> settings = new HashMap<>();
    public static StatusSettings getSettings(OfflinePlayer p){
        if(p != null){
            if(settings.get(p.getUniqueId()) == null){
                settings.put(p.getUniqueId(), new StatusSettings(p));
            }
        }else return null;
        return settings.get(p.getUniqueId());
    }

    private boolean autoStatus, enabled, isAFK;
    private List<Status> createdStatus = new ArrayList<>();
    private Status currentStatus;
    private UUID player;
    private long afktimer;
    private UUID selectedGlobalStatus;

    public StatusSettings(OfflinePlayer player) {
        setPlayer(player);
        autoStatus =
        enabled = true;
        isAFK = false;
        setCurrentStatus(new Status(getPlayerText(), player, false, TextColor.color(255, 255, 255)));
    }
    private void setPlayer(OfflinePlayer p){
        player = p.getUniqueId();
    }

    private static List<TextComponent> getPlayerText(){
        List<TextComponent> list = new ArrayList<>();
        list.add(Component.text("P", TextColor.color(0, 255, 255)));
        list.add(Component.text("l", TextColor.color(0, 220, 255)));
        list.add(Component.text("a", TextColor.color(0, 180, 255)));
        list.add(Component.text("y", TextColor.color(0, 140, 255)));
        list.add(Component.text("e", TextColor.color(0, 100, 255)));
        list.add(Component.text("r", TextColor.color(0, 60, 255)));
        return list;
    }
    public StatusSettings(Map<String, Object> map){
        enabled = (boolean) map.get("enabled");
        autoStatus = (boolean) map.get("auto");
        createdStatus = (List<Status>) map.get("statuses");
        player = UUID.fromString((String) map.get("player"));
        settings.put(player, this);
        currentStatus = createdStatus.get((Integer) map.get("currentstatus"));
        if(map.get("globalstatus")!=null){
            selectedGlobalStatus = UUID.fromString((String) map.get("globalstatus"));
        }
    }
    @Override
    public @NotNull Map<String, Object> serialize() {
        Map<String, Object> map = new HashMap<>();
        map.put("enabled", enabled);
        map.put("auto", autoStatus);
        map.put("statuses", createdStatus);
        if(selectedGlobalStatus != null){
            map.put("globalstatus", selectedGlobalStatus.toString());
        }
        if(!createdStatus.contains(currentStatus)){
            createdStatus.add(currentStatus);
        }
        map.put("currentstatus", createdStatus.indexOf(currentStatus));
        map.put("player", player.toString());
        return map;
    }
    public StatusSettings(Status currentStatus, OfflinePlayer player) {
        this.currentStatus = currentStatus;
        setPlayer(player);
        autoStatus = enabled = true;
        isAFK = false;
        afktimer = System.currentTimeMillis();
    }

    public static HashMap<UUID, StatusSettings> getSettings() {
        return settings;
    }

    public boolean isAutoStatus() {
        return autoStatus;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public boolean isAFK() {
        return isAFK;
    }

    public List<Status> getCreatedStatus() {
        return createdStatus;
    }

    public Status getCurrentStatus() {
        return currentStatus;
    }

    public OfflinePlayer getPlayer() {
        return Bukkit.getOfflinePlayer(player);
    }

    public long getAfktimer() {
        return afktimer;
    }
    public void resetAFKTimer(){
        if (isAFK) {
            setAFK(false);
        }
        afktimer = System.currentTimeMillis();
    }
    public void removeStatus(Status s){
        if(s instanceof GlobalStatus gs){
            if(getSelectedGlobalStatus() == gs){
                setCurrentStatus(getCurrentStatus());
            }
        }else{
            createdStatus.remove(s);
            if(currentStatus == s){
                try {
                    if(!createdStatus.isEmpty()){
                        setCurrentStatus(createdStatus.get(0));
                    }else{
                        setEnabled(false);
                    }
                }catch (NoSuchElementException e){
                    setEnabled(false);
                }
            }
        }
    }

    public void setAutoStatus(boolean autoStatus) {
        this.autoStatus = autoStatus;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
        if(enabled){
            GlobalStatus gs = getSelectedGlobalStatus();
            if(gs != null){
                gs.attachToPlayer(getPlayer().getPlayer());
            }else if(currentStatus != null){
                currentStatus.update();
            }else{
                Bukkit.getConsoleSender().sendMessage("Error 0234");
            }
        }else{
            if(getPlayer().getPlayer() != null){
                getPlayer().getPlayer().displayName(Component.text(getPlayer().getPlayer().getName()));
                getPlayer().getPlayer().playerListName(Component.text(getPlayer().getPlayer().getName()));
            }
        }
    }

    public void setAFK(boolean AFK) {
        isAFK = AFK;
        if(isAFK){
            if(getPlayer().isOnline()){
                GlobalStatus.afk.attachToPlayer(getPlayer().getPlayer());
            }
        }else{
            update();
        }
    }

    public static ItemStack getAllow() {
        return allow;
    }

    public static ItemStack getDeny() {
        return deny;
    }
    public void setCurrentStatus(Status status){
        if(status instanceof GlobalStatus g){
            status.attachToPlayer(getPlayer().getPlayer());
            selectedGlobalStatus = g.getId();
        } else {
            if(!createdStatus.contains(status)){
                createdStatus.add(status);
            }
            currentStatus = status;
            status.update();
            selectedGlobalStatus = null;
        }
    }

    public Component setCurrentStatus(String name, Player p){
        for(Status s : getCreatedStatus()){
            if(s.getContent().equalsIgnoreCase(name)){
                setCurrentStatus(s);
                return DM.messagePrefix.append(Component.text("Successfully set your Status to ").append(p.displayName()));
            }
        }

        for(Status s : GlobalStatus.getGlobalStatusList()){
            if(s.getContent().equalsIgnoreCase(name)){
                setCurrentStatus(s);
                return DM.messagePrefix.append(Component.text("Successfully set your Status to ").append(p.displayName()));
            }
        }

        return DM.messagePrefix.append(Component.text("This Status does not exist."));
    }
    public GlobalStatus getSelectedGlobalStatus(){
        return GlobalStatus.getStatus(selectedGlobalStatus);
    }
    public boolean isCurrentStatus(Status s){
        if(getSelectedGlobalStatus() != null){
            return getSelectedGlobalStatus().equals(s);
        }else{
            return getCurrentStatus().equals(s);
        }
    }
    public void update(){
        if(!getPlayer().isOnline()){
            return;
        }
        if(!isEnabled()){
            return;
        }

        if(getSelectedGlobalStatus() != null){
            getSelectedGlobalStatus().attachToPlayer(getPlayer().getPlayer());
        }else{
            if(currentStatus != null){
                currentStatus.update();
            }
        }
    }
    public static void serializeAll() throws IOException {
        File f = new File(Main.getPlugin().getDataFolder(), "status.yml");
        if(f.exists()){
            f.delete();
        }
        f.createNewFile();
        YamlConfiguration yml = YamlConfiguration.loadConfiguration(f);
        List<StatusSettings> list = new ArrayList<>(settings.values());
        yml.set("player", list);
        yml.set("global", GlobalStatus.getGlobalStatusList());
        yml.save(f);
    }
    public static void deserializeAll(){
        File f = new File(Main.getPlugin().getDataFolder(), "status.yml");
        YamlConfiguration yml = YamlConfiguration.loadConfiguration(f);
        List<GlobalStatus> list = (List<GlobalStatus>) yml.get("global");
        if(list != null)
            for(GlobalStatus s : list){
                GlobalStatus.addGlobalStatus(s);
            }
        else
        if(GlobalStatus.getGlobalStatusList().isEmpty() && !yml.isSet("global")) {
            GlobalStatus.addGlobalStatus(GlobalStatus.afk);
            GlobalStatus.addGlobalStatus(GlobalStatus.building);
        }
        List<StatusSettings> playerSettings = (List<StatusSettings>) yml.get("player");
        if(playerSettings != null){
            for(StatusSettings s : playerSettings){
                settings.put(s.player, s);
            }
        }

    }
}
