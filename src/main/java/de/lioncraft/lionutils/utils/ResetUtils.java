package de.lioncraft.lionutils.utils;

import de.lioncraft.lionapi.messageHandling.lionchat.LionChat;
import de.lioncraft.lionapi.teams.Team;
import de.lioncraft.lionapi.timer.MainTimer;
import de.lioncraft.lionapi.timer.Stopwatch;
import de.lioncraft.lionapi.timer.Timer;
import de.lioncraft.lionapi.velocity.connections.ConnectionManager;
import de.lioncraft.lionapi.velocity.data.TransferrableObject;
import de.lioncraft.lionutils.Main;
import de.lioncraft.lionutils.events.PluginDataResetEvent;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.advancement.Advancement;
import org.bukkit.advancement.AdvancementProgress;
import org.bukkit.entity.Player;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffect;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ResetUtils {
    private static List<String> excludedWorlds = new ArrayList<>();
    private static List<File> filesToReset = new ArrayList<>();
    /** Excludes the world from being reset
     * @param w The Worldname
     */
    public static void addProtectedWorld(String w){
        excludedWorlds.add(w);
    }

    public static void addFileToReset(File f){
        filesToReset.add(f);
    }

    public static List<String> getExcludedWorlds() {
        return excludedWorlds;
    }

    public static void resetAll(){
        for(Player p : Bukkit.getOnlinePlayers()){
            if (ConnectionManager.isConnectedToVelocity()) sendServerSendRequest(p, "lobby");
            else p.kick(Component.text("Resetting the Server...", TextColor.color(0, 255, 255)));
        }



        resetPluginData();

        MainTimer.reset();
        Team.resetAll();



        List<String> list = new ArrayList<>();
        for(World w : Bukkit.getServer().getWorlds()){
            if(!excludedWorlds.contains(w.getName())) list.add(w.getName());
        }
        List<String> files = new ArrayList<>();
        for (File f : filesToReset){
            files.add(f.getPath());
        }
        Main.getPlugin().getConfig().set("detected-worlds", list);
        Main.getPlugin().getConfig().set("delete-worlds-on-startup", true);
        Main.getPlugin().getConfig().set("files-to-reset", files);
        Main.getPlugin().saveConfig();

        Bukkit.getScheduler().runTaskLater(Main.getPlugin(), () -> {
            Bukkit.getServer().spigot().restart();
        }, 24);

    }

    public static void resetPluginData(){
        Bukkit.getPluginManager().callEvent(new PluginDataResetEvent());
    }
    public static void resetPlayerData(){
        File root = new File(Bukkit.getWorldContainer(), "world/playerdata");
        for (OfflinePlayer p : Bukkit.getOfflinePlayers()){
            if(p.isOnline()){
                resetPlayerData(p.getPlayer());
            }else{
                File file = new File(root, p.getUniqueId().toString()+".dat");
                File file2 = new File(root, p.getUniqueId().toString()+".dat_old");
                if (file.exists()&&file.delete()){
                }else LionChat.sendLogMessage("Could not reset "+p.getName()+"'s Player data");
                if (file2.exists()&&file2.delete()){
                }else LionChat.sendLogMessage("Could not reset "+p.getName()+"'s old player data");
            }
        }
    }

    public static void sendServerSendRequest(Player p, String server){
        ConnectionManager.getConnectionToVelocity().sendMessage(
                new TransferrableObject("LionLobby_PlayerTransfer")
                        .addValue("player", p.getUniqueId().toString())
                        .addValue("server", server));
    }


    private static void resetPlayerData(Player player) {
        // Ensure the player is in a safe state (e.g., not dead)
        if (player.isDead()) {
            player.spigot().respawn();
        }

        // 1. Reset Inventory & Ender Chest
        PlayerInventory inventory = player.getInventory();
        inventory.clear();
        inventory.setArmorContents(null); // Clears armor slots
        inventory.setItemInOffHand(null); // Clears off-hand
        player.getEnderChest().clear();

        // 2. Reset Health, Hunger, and Saturation
        player.setHealth(20.0);
        player.setFoodLevel(20);
        player.setSaturation(5.0f); // A reasonable default saturation
        player.setExhaustion(0f);

        // 3. Reset Experience
        player.setTotalExperience(0);
        player.setLevel(0);
        player.setExp(0f);

        // 4. Clear All Potion Effects
        for (PotionEffect effect : player.getActivePotionEffects()) {
            player.removePotionEffect(effect.getType());
        }

        // 5. Revoke All Advancements
        Iterator<Advancement> advancements = Bukkit.getServer().advancementIterator();
        while (advancements.hasNext()) {
            Advancement advancement = advancements.next();
            AdvancementProgress progress = player.getAdvancementProgress(advancement);
            for (String criteria : progress.getAwardedCriteria()) {
                progress.revokeCriteria(criteria);
            }
        }
    }


    public static void deleteWorlds(Plugin pl){
        for(String s : pl.getConfig().getStringList("detected-worlds")){
            File f = new File(pl.getServer().getWorldContainer(), s);
            if(f.exists()){
                delete(f);
            }else pl.getLogger().warning("Invalid World to delete: "+s);
        }
        for (String s : Main.getPlugin().getConfig().getStringList("files-to-reset")){
            File f = new File(s);
            if (f.exists()) delete(f);
        }
        Main.getPlugin().getConfig().set("delete-worlds-on-startup", false);
        Main.getPlugin().getConfig().set("files-to-reset", null);
        Main.getPlugin().saveConfig();
    }
    private static void delete(File f){
        if(f.isFile()){
            f.delete();
        }else{
            for(File f2 : f.listFiles()) delete(f2);
        }
    }
}
