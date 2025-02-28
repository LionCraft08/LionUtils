package de.lioncraft.lionutils.utils;

import de.lioncraft.lionutils.Main;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MOTD {
    private static final List<String> playersOnline = new ArrayList<>(Main.getPlugin().getConfig().getStringList("modts.withonlineplayers"));
    private static final List<String> noPlayersOnline = new ArrayList<>(Main.getPlugin().getConfig().getStringList("modts.noonlineplayers"));
    private static final Random random = new Random();

    public static String getRandomMOTD(){
        if(Bukkit.getServer().getOnlinePlayers().isEmpty()){
            return noPlayersOnline.get(random.nextInt(noPlayersOnline.size()));
        }else{
            String s = playersOnline.get(random.nextInt(playersOnline.size()));
            if(!s.contains("%playername%")){
                return s;
            }
            int index1 = s.indexOf("%playername%");
            int index2 = s.lastIndexOf("%playername%");
            Player p = Bukkit.getOnlinePlayers().stream().toList().get(random.nextInt(Bukkit.getOnlinePlayers().size()));
            if(index1 == index2){
                return s.replaceAll("%playername%", p.getName());
            }
            Player p2 = Bukkit.getOnlinePlayers().stream().toList().get(random.nextInt(Bukkit.getOnlinePlayers().size()));
            if(Bukkit.getOnlinePlayers().size() > 1 && p2 == p){
                while(p2 == p){
                    p2 = Bukkit.getOnlinePlayers().stream().toList().get(random.nextInt(Bukkit.getOnlinePlayers().size()));
                }
            }
            return s.replaceFirst("%playername%", p.getName()).replaceAll("%playername%", p2.getName());
        }
    }
    public static Component getRandomCMOTD(){
        String s = getRandomMOTD();
        List<TextColor> list = List.of(TextColor.color(255, 255, 0), TextColor.color(0, 255, 0), TextColor.color(255, 128, 0), TextColor.color(255, 0, 0), TextColor.color(255, 0, 255), TextColor.color(0, 255, 255), TextColor.color(0, 0, 255), TextColor.color(211, 5, 255), TextColor.color(255, 255, 255));
        return Component.text(s, list.get(random.nextInt(list.size())));
    }
}

