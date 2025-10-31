package de.lioncraft.lionutils.utils.status;

import de.lioncraft.lionapi.guimanagement.Items;
import de.lioncraft.lionapi.messageHandling.DM;
import de.lioncraft.lionapi.messageHandling.lionchat.LionChat;
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
    private static final boolean enableStatusByDefault = Main.getPlugin().getConfig().getBoolean("settings.status.enabled-by-default");
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
    private HashMap<String, Status> createdStatus = new HashMap<>();
    private String currentStatus;
    private UUID player;
    private long afktimer;

    public StatusSettings(OfflinePlayer player) {
        setPlayer(player);
        autoStatus = true;
        enabled = isEnableStatusByDefault();
        isAFK = false;
        setCurrentStatus(new Status(getPlayerText(), player, false, TextColor.color(255, 255, 255)));
    }

    public static boolean isEnableStatusByDefault() {
        return enableStatusByDefault;
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
        player = UUID.fromString((String) map.get("player"));

        //Converting old status data to the new format.
        Object cs = map.get("currentstatus");
        if(cs instanceof Integer i){
            List<Status> list = (List<Status>) map.get("statuses");
            for(Status s : list){
                addStatus(s);
            }
            setCurrentStatus("created:"+list.get(i).getName());
        }
        //End of conversion
        else {
            currentStatus = (String) cs;
            createdStatus = (HashMap<String, Status>) map.get("statuses");
        }

        settings.put(player, this);
    }

    private void addStatus(Status status){
        String name = status.getName();
        if(createdStatus.containsKey(name)){
            if(Character.isDigit(name.charAt(name.length()-1))){
                int i = Integer.parseInt(String.valueOf(name.charAt(name.length()-1)));
                name = name.substring(0, name.length()-1)+i;
            }else{
                name = name + "_1";
            }
            status.setName(name);
            addStatus(status);
        }else{
            createdStatus.put(name, status);
        }
    }

    public void addCreatedStatus(String name){
        Status s = new Status(null, getPlayer(), false, TextColor.color(255, 255, 255));
        if (name != null && !name.isBlank()) s.setName(name);
        createdStatus.put(s.getName(), s);
    }

    @Override
    public @NotNull Map<String, Object> serialize() {
        Map<String, Object> map = new HashMap<>();
        map.put("enabled", enabled);
        map.put("auto", autoStatus);
        map.put("statuses", createdStatus);
        map.put("currentstatus", currentStatus);
        map.put("player", player.toString());
        return map;
    }
    public StatusSettings(String currentStatus, OfflinePlayer player) {
        this.currentStatus = currentStatus;
        setPlayer(player);
        autoStatus = enabled = true;
        isAFK = false;
        afktimer = System.currentTimeMillis();
    }

    public static HashMap<UUID, StatusSettings> getSettings() {
        return settings;
    }

    public Status getCreatedStatus(String name){
        if (createdStatus.containsKey(name)) return createdStatus.get(name);
        return createdStatus.get(name.replace("created:", ""));
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
        return createdStatus.values().stream().toList();
    }

    public Status getCurrentStatus() {
        return switch (getStatusType()){
            case "global" ->GlobalStatus.getStatus(getCurrentStatusName());
            case "created" -> createdStatus.get(getCurrentStatusName());
            case "custom" -> CustomStatusManager.getCustomStatus(getCurrentStatusName());
            default -> null;
        };
    }

    public String getStatusType(){
        if(currentStatus.contains(":")){
            return currentStatus.substring(0, currentStatus.indexOf(":"));
        }
        else {
            LionChat.sendDebugMessage("Wrong value for currentStatus detected: "+currentStatus);
            return "unknown";
        }
    }

    public String getCurrentStatusName(){
        return currentStatus.substring(currentStatus.indexOf(":")+1);
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

    public List<String> getCreatedStatusNames(){
        return createdStatus.keySet().stream().toList();
    }


    public void removeStatus(String s){
        if (currentStatus.equals(s)){
            if (getStatusType().equals("created")) {
                createdStatus.remove(getCurrentStatusName());
            }
            resetCurrentStatus();
        } else {
            if (s.startsWith("created:")){
                createdStatus.remove(s.replaceFirst("created:", ""));
            }
        }
    }

    public void setAutoStatus(boolean autoStatus) {
        this.autoStatus = autoStatus;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
        if(enabled){
            update();
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
                GlobalStatus.afk.update(getPlayer().getPlayer());
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

    public void resetCurrentStatus(){
        try {
            if(!createdStatus.isEmpty()){
                setCurrentStatus(getCreatedStatusNames().get(0));
            }else{
                setEnabled(false);
            }
        }catch (NoSuchElementException e){
            setEnabled(false);
        }
    }

    public void setCurrentStatus(Status status){
        if(status instanceof GlobalStatus g){
            String s = g.getName();
            setCurrentStatus("global:"+s);

            //TODO Check for CustomStatus
        } else {
            if(!createdStatus.containsValue(status)){
                createdStatus.put(status.getName(), status.attachToPlayer(getPlayer()));
            }
            setCurrentStatus("created:"+status.getName());
        }
    }

    public void setCurrentStatus(String s){
        if (s == null) setEnabled(false);
        else
        if (s.startsWith("global:")||s.startsWith("created:")||s.startsWith("custom:")){
            currentStatus = s;
        }
        else{
            if(createdStatus.containsKey(s)){
                currentStatus = "created:"+s;
            } else if (GlobalStatus.getStatus(s) != null) {
                currentStatus = "global:"+s;
            } else if (CustomStatusManager.getCustomStatus(s) != null) {
                currentStatus = "custom:"+s;
            }
        }
        update();
    }

    public boolean checkStatus(String name){
        if (!(name.startsWith("global:")||name.startsWith("created:")||name.startsWith("custom:"))){
            if (createdStatus.containsKey(name)){
                name = "created:"+name;
            } else if (GlobalStatus.getStatus(name) != null) {
                name = "global:"+name;
            } else if (CustomStatusManager.getCustomStatus(name) != null) {
                name = "custom:"+name;
            } else return false;
        }
        String actualName = name.substring(name.indexOf(":")+1);
        if (name.startsWith("created:")) return createdStatus.containsKey(actualName);
        if (name.startsWith("global:")) return GlobalStatus.getStatus(actualName) != null;
        if (name.startsWith("custom:")) return CustomStatusManager.getCustomStatus(actualName) != null;
        return false;
    }

    public void update(){
        if (!isEnabled()) return;
        Player p = Bukkit.getPlayer(player);
        if (p != null){
            switch (getStatusType()){
                case "global" -> GlobalStatus.getStatus(getCurrentStatusName()).update(p);
                case "created" -> createdStatus.get(getCurrentStatusName()).update();
                case "custom" -> CustomStatusManager.getCustomStatus(getCurrentStatusName()).update(p);
            }
        }
    }

    public boolean isCurrentStatus(String s){
        if (getCurrentStatusName().equals(s)) return true;
        return currentStatus.equals(s);
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
        yml.set("global", GlobalStatus.getGlobalStatusesList());
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
