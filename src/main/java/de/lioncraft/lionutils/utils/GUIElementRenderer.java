package de.lioncraft.lionutils.utils;

import de.lioncraft.lionapi.messageHandling.ColorGradient;
import de.lioncraft.lionutils.Main;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.TimeZone;

public final class GUIElementRenderer {
    private GUIElementRenderer(){}
    private static Component line = ColorGradient.getNewGradiant("-----------------------------", TextColor.color(250, 0, 255), TextColor.color(0, 0, 255));
    private static Component ls = Main.lm().msg("tablist.header01");
    public static Component getHeader(String timeZone){
        if (timeZone.isBlank()) timeZone = TimeZone.getDefault().getID();
        return Component.text("").appendNewline().append(ls).appendNewline().appendNewline().append(getTime(timeZone)).appendNewline().append(line);
    }

    public static Component getFooter(String servername){
        return line.appendNewline().append(Component.text("Server: "+servername, TextColor.color(0, 255, 255))).appendNewline().appendNewline()
                .append(Component.text("Netzwerk", TextColor.color(0, 200, 255), TextDecoration.UNDERLINED)).appendNewline().appendNewline()
                .append(Component.text("Players: "+ Bukkit.getOnlinePlayers().size(), TextColor.color(0, 150, 255)))
                .append(Component.text("        Server: 1", TextColor.color(0, 100, 255))).appendNewline();
    }

    public static Component getTime(String timeZone){
        ZonedDateTime zonedDateTime = ZonedDateTime.now(ZoneId.of(timeZone));
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy    HH:mm z");
        return ColorGradient.getNewGradiant(zonedDateTime.format(formatter), TextColor.color(128, 0, 255), TextColor.color(255, 0, 128));
    }
}
