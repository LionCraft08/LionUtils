package de.lioncraft.lionutils.inventories;

import de.lioncraft.lionapi.guimanagement.Interaction.Button;
import de.lioncraft.lionapi.guimanagement.Interaction.Setting;
import de.lioncraft.lionapi.guimanagement.Items;
import de.lioncraft.lionutils.commands.Inv;
import de.lioncraft.lionutils.data.ChallengesData;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import java.awt.*;

public final class ChallengesGUI {
    private ChallengesGUI(){}
    private static Inventory inv;
    public static void open(Player p){
        if (inv == null) createInv();
        p.openInventory(inv);
    }
    private static void createInv(){
        inv = Bukkit.createInventory(null, 54, Component.text("Challenges"));
        inv.setContents(Items.blockButtons);
        Button back = new Button(Items.getBackButton("Operator Utils"), e -> {
            opUtils.openUI((Player) e.getWhoClicked());
        return false;});
        inv.setItem(45, back.getButton());
        Setting s = new Setting(ChallengesData.getInstance().isSharedHearts(), Items.get("Shared Hearts", Material.LEAD, "Geteilte Herzen innerhalb eines Teams"), b -> {
            ChallengesData.getInstance().setSharedHearts(b);
        });
        inv.setItem(10, s.getTopItem());
        inv.setItem(19, s.getBottomItem());
        Setting global = new Setting(ChallengesData.getInstance().isGlobalShared(), Items.get("Global Hearts", Material.HEART_OF_THE_SEA, "Geteilte Herzen für alle"), b -> {
            ChallengesData.getInstance().setGlobalShared(b);
        });
        inv.setItem(12, global.getTopItem());
        inv.setItem(21, global.getBottomItem());

    }
}
