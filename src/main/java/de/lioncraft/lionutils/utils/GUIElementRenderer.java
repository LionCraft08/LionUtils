package de.lioncraft.lionutils.utils;

import de.lioncraft.lionapi.LionAPI;
import de.lioncraft.lionapi.messageHandling.ColorGradient;
import de.lioncraft.lionutils.Main;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.TimeZone;

import static de.lioncraft.lionutils.Main.*;

public final class GUIElementRenderer {
    private static String timeFormat, timeZone, serverName;
    private static List<Component> footerList;

    public static void init() {
        timeFormat = getPlugin().getConfig().getString("tablist.time-format");
        timeZone = getPlugin().getConfig().getString("tablist.time-zone");
        serverName = getPlugin().getConfig().getString("tablist.server-name");
    }

    private GUIElementRenderer(){}
    private static Component line;

    static {
        if (Main.getPlugin().getConfig().getBoolean("tablist.use-lionapi-gradiant")){
            line = ColorGradient.getNewGradiant("-----------------------------", TextColor.color(250, 0, 255), TextColor.color(0, 0, 255));
        } else line = lm().msg("tablist.divider");
    }

    private static Component ls = lm().msg("tablist.header01");

    public static Component getHeader(){
        return getHeader(timeZone);
    }

    public static void update(){
        footerList = Main.lm().getMessageAsList("tablist.footer", serverName, ""+Bukkit.getOnlinePlayers().size());
    }

    public static Component getHeader(String timeZone){
        if (timeZone.isBlank()) timeZone = TimeZone.getDefault().getID();
        return Component.text("").appendNewline().append(ls).appendNewline().appendNewline().append(getTime(timeZone)).appendNewline().append(line);
    }

    public static Component getFooter(){
        return getFooter(serverName);
    }

    public static Component getFooter(String servername){
        Component c = line.appendNewline();
        for (Component footer : footerList){
            c = c.append(footer).appendNewline();
        }
        return c;
//        return line.appendNewline().append(Component.text("Server: "+servername, TextColor.color(0, 255, 255))).appendNewline().appendNewline()
//                .append(Component.text("Netzwerk", TextColor.color(0, 200, 255), TextDecoration.UNDERLINED)).appendNewline().appendNewline()
//                .append(Component.text("Players: "+ Bukkit.getOnlinePlayers().size(), TextColor.color(0, 150, 255)))
//                .append(Component.text("        Server: 1", TextColor.color(0, 100, 255))).appendNewline();
    }

    public static Component getTime(String timeZone){
        ZonedDateTime zonedDateTime = ZonedDateTime.now(ZoneId.of(timeZone));
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(timeFormat);
        return ColorGradient.getNewGradiant(zonedDateTime.format(formatter), TextColor.color(128, 0, 255), TextColor.color(255, 0, 128));
    }
}
