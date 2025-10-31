package de.lioncraft.lionutils.utils.status;

import de.lioncraft.lionapi.teams.Team;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

public class TeamStatus extends GlobalStatus {
    public TeamStatus(OfflinePlayer player) {
        super(null, player, false, TextColor.color(255, 255, 255), Material.MINECART);
        super.setName("team");
    }
    @Override
    public Component getAsComponent(String playerName){
        Component c = Component.text("[", TextColor.color(128, 128, 128));
        if (playerName != null){
            OfflinePlayer p = Bukkit.getOfflinePlayerIfCached(playerName);
            if (p != null){
                Team t = Team.getTeam(p);
                if(t != null){
                    c = c.append(Component.text(t.getName(), TextColor.color(0, 190, 200)));
                }
            }
        }else c = c.append(Component.text("TEAMNAME", TextColor.color(0, 190, 200)));
        c = c.append(Component.text("]", TextColor.color(128, 128, 128)))
                .appendSpace();
        if (playerName != null) {
            c = c.append(Component.text(playerName, getNameColor()));
        }

        return c;
    }
}
