package de.lioncraft.lionutils.utils;

import de.lioncraft.lionapi.teams.Team;
import de.lioncraft.lionapi.timer.MainTimer;
import de.lioncraft.lionapi.timer.Stopwatch;
import de.lioncraft.lionapi.timer.Timer;
import de.lioncraft.lionutils.Main;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.util.ArrayList;
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
        MainTimer.reset();
        Team.resetAll();

        for(Player p : Bukkit.getOnlinePlayers()){
            p.kick(Component.text("Resetting the Server...", TextColor.color(0, 255, 255)));
        }

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
        Bukkit.getServer().spigot().restart();
    }

    public static void resetPluginData(){

    }
    public static void deleteWorlds(Plugin pl){
        for(String s : pl.getConfig().getStringList("detected-worlds")){
            File f = new File(pl.getServer().getWorldContainer(), s);
            if(f.exists()){
                delete(f);
            }else pl.getLogger().warning("Invalid World to delete: "+s);
        }
        for (String s : Main.getPlugin().getConfig().getStringList("")){
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
