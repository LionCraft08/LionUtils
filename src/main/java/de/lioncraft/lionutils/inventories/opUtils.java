package de.lioncraft.lionutils.inventories;

import de.lioncraft.lionapi.guimanagement.Interaction.Button;
import de.lioncraft.lionapi.guimanagement.Interaction.LionButtonFactory;
import de.lioncraft.lionapi.guimanagement.Interaction.Setting;
import de.lioncraft.lionapi.guimanagement.Items;
import de.lioncraft.lionapi.guimanagement.MainMenu;
import de.lioncraft.lionapi.messageHandling.DM;
import de.lioncraft.lionapi.messageHandling.lionchat.LionChat;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

public final class opUtils {
    private static boolean sharedHearts;
    private opUtils(){}
    private static Inventory inv;
    public static void openUI(Player player){
        if(inv == null){
            init();
        }
        if (player.isOp()){
            player.openInventory(inv);
        }else{
            player.playSound(player, Sound.ENTITY_SILVERFISH_DEATH, 1, 1);
        }

    }
    private static void init(){
        inv = Bukkit.createInventory(null, 54, Component.text("Operator Utils", TextColor.color(0, 255, 255)));
        inv.setContents(Items.blockButtons);
        inv.setItem(49, Items.closeButton);
        inv.setItem(45, MainMenu.getToMainMenuButton());
        inv.setItem(10, LionButtonFactory.createButton(Items.get(Component.text("Heal",
                            TextColor.color(255, 128, 0)),
                        Material.ENCHANTED_GOLDEN_APPLE,
                        "Heals every player and resets their nourishment"),
                "lionutils_heal"
                ));

        inv.setItem(12, LionButtonFactory.createButton(Items.get(Component.text("Teleport"), Material.ENDER_PEARL, "Teleports every Player to you"),
                "lionutils_tp"));

        inv.setItem(14, LionButtonFactory.createButton(Items.get("Challenges", Material.DIAMOND_SWORD, "Different small Challenges"),
                "lionutils_challenges"));

        inv.setItem(16, LionButtonFactory.createButton(Items.get(Component.text("Player Management", TextColor.color(0, 255, 255)),
                Material.PLAYER_HEAD, "Opens a GUI to manage Player Abilities"),
                "lionutils_player_management"));

        inv.setItem(28, LionButtonFactory.createButton(Items.get(Component.text("Create starter house", TextColor.color(0, 255, 255)),
                Material.OAK_LOG, "Creates a Structure, teleports players into it",
                        "sets the world spawn and protects it from",
                        "being destroyed by players"),
                "lionutils_spawn_house"));
    }

    public static Button getButton(boolean disappear){
        return new Button(Items.getBackButton("Operator Utils"), inventoryClickEvent -> {
            openUI((Player) inventoryClickEvent.getWhoClicked());
            return disappear;
        });
    }
}
