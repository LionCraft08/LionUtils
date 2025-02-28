package de.lioncraft.lionutils.utils;

import de.lioncraft.lionutils.Main;
import de.lioncraft.lionutils.listeners.ChatListeners;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.OfflinePlayer;
import org.bukkit.attribute.Attribute;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.entity.Player;
import org.bukkit.entity.Pose;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.util.*;

public class Settings implements ConfigurationSerializable {
    private static final Map<OfflinePlayer, Settings> playerSettings = new HashMap<>();
    private static Settings global;

    public static @NotNull Settings getSettings(OfflinePlayer p){
        if(p == null){
            return global;
        }
        if(playerSettings.containsKey(p)){
            if(playerSettings.get(p) != null){
                return playerSettings.get(p);
            }
        }
        return global;
    }
    public static void removeSetting(OfflinePlayer p){
        playerSettings.remove(p);
    }
    public static List<Settings> getSettings(){
        List<Settings> list = new ArrayList<>();
        for(Settings s : playerSettings.values()){
            if(!list.contains(s)) list.add(s);
        }
        return list;
    }

    private OfflinePlayer player;
    //Configurable by Operator
    private boolean canFly, isInvulnerable, canPickupItems, isVisible, canChat, canMineBlocks, canHitEntities, canMove;
    //Configurable by Player
    private boolean recieveChat, pvp;


    public Settings() {
        player = null;
        reset();
    }

    public Settings(OfflinePlayer players) {
        this.player = players;
        reset();
    }

    public void update(){
        if(this == global){
            for(Player p : Bukkit.getOnlinePlayers()){
                if(Settings.getSettings(p) == this){
                    update(p);
                }
            }
        }else {
            update(player);
        }
    }
    protected void update(OfflinePlayer player){
        if(player == null){
            return;
        }
        if(player.getPlayer() == null){
            return;
        }
        if(player.getPlayer().getGameMode().equals(GameMode.SURVIVAL)||(player.getPlayer().getGameMode().equals(GameMode.ADVENTURE))){
            player.getPlayer().setAllowFlight(canFly);
        }
        player.getPlayer().setInvulnerable(isInvulnerable);
        player.getPlayer().setCanPickupItems(canPickupItems);
        player.getPlayer().setInvisible(!isVisible);
    }
    public boolean canFly() {
        return canFly;
    }
    public boolean isInvulnerable() {
        return isInvulnerable;
    }
    public boolean canPickupItems() {
        return canPickupItems;
    }

    public boolean isRecieveChat() {
        return recieveChat;
    }

    public void setRecieveChat(boolean recieveChat) {
        this.recieveChat = recieveChat;
    }

    public boolean isVisible() {
        return isVisible;
    }
    public boolean canChat() {
        return canChat;
    }
    public boolean canMineBlocks() {
        return canMineBlocks;
    }
    public boolean canHitEntities() {
        return canHitEntities;
    }
    public boolean canMove() {
        return canMove;
    }
    public void reset(){
        canFly = false;
        isInvulnerable = false;
        pvp = canPickupItems = isVisible = canChat = canMineBlocks = canHitEntities = canMove = recieveChat = true;
        update();
    }

    @Override
    public @NotNull Map<String, Object> serialize() {
        Map<String, Object> map = new HashMap<>();
        map.put("canFly", canFly);
        map.put("isInvulnerable", isInvulnerable);
        map.put("canPickupItems", canPickupItems);
        map.put("isVisible", isVisible);
        map.put("canChat", canChat);
        map.put("canMineBlocks", canMineBlocks);
        map.put("canHitEntities", canHitEntities);
        map.put("canMove", canMove);
        map.put("recieveChat", recieveChat);
        map.put("player", player);
        return map;
    }

    public Settings(Map<String, Object> map) {
        canFly = (boolean) map.get("canFly");
        isInvulnerable = (boolean) map.get("isInvulnerable");
        canPickupItems = (boolean) map.get("canPickupItems");
        isVisible = (boolean) map.get("isVisible");
        canChat = (boolean) map.get("canChat");
        canMineBlocks = (boolean) map.get("canMineBlocks");
        canHitEntities = (boolean) map.get("canHitEntities");
        canMove = (boolean) map.get("canMove");
        player = (OfflinePlayer) map.get("player");
        recieveChat = (boolean) map.get("recieveChat");
        if(player != null){
            playerSettings.put(player, this);
        }
    }
    public Settings cloneTo(OfflinePlayer p){
        Settings s = new Settings(serialize());
        s.setPlayer(p);
        playerSettings.put(p, s);
        return s;
    }
    private void setPlayer(OfflinePlayer p){
        player = p;
    }

    public OfflinePlayer getPlayer() {
        return player;
    }

    public void setCanFly(boolean canFly) {
        this.canFly = canFly;
        update();
    }

    public void setInvulnerable(boolean invulnerable) {
        isInvulnerable = invulnerable;
        update();
    }

    public void setCanPickupItems(boolean canPickupItems) {
        this.canPickupItems = canPickupItems;
        update();
    }

    public void setVisible(boolean visible) {
        isVisible = visible;
        update();
    }

    public void setCanChat(boolean canChat) {
        this.canChat = canChat;
    }

    public void setCanMineBlocks(boolean canMineBlocks) {
        this.canMineBlocks = canMineBlocks;
    }

    public void setCanHitEntities(boolean canHitEntities) {
        this.canHitEntities = canHitEntities;
    }

    public void setCanMove(boolean canMove) {
        this.canMove = canMove;
    }

    public boolean canPvp() {
        return pvp;
    }

    public static void serializeAll() throws IOException {
        File f = new File(Main.getPlugin().getDataFolder(), "settings.yml");
        if(f.exists()){
            f.delete();
        }
        f.createNewFile();
        YamlConfiguration yml = YamlConfiguration.loadConfiguration(f);
        yml.set("player", getSettings());
        yml.set("global", global);
        yml.save(f);
    }
    public static void deserializeAll(){
        File f = new File(Main.getPlugin().getDataFolder(), "settings.yml");
        if(!f.exists()){
            global = new Settings();
            return;
        }
        YamlConfiguration yml = YamlConfiguration.loadConfiguration(f);
        global = (Settings) yml.get("global");
        if(global == null){
            global = new Settings();
        }
        List<Settings> playerSettings = (List<Settings>) yml.get("player");
        if(playerSettings != null){
            for(Settings s : playerSettings){
                Settings.playerSettings.put(s.getPlayer(), s);
            }
        }

    }
}
