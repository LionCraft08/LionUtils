package de.lioncraft.lionutils.utils.status;

import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

class openStatusConfigureGUILater extends BukkitRunnable {
    @Override
    public void run() {
        if(part < -1) {
            Inventories.openConfigureStatusMenu(player, status);
        } else if (part == -1) {
            Inventories.openMainMenu(player);
        }else Inventories.openConfigurePartMenu(player, part, status);
    }
    private final Player player;
    private final int part;
    private final Status status;

    /**if part = -1: MainMenu
     * part = -2: StatusMenu
     * else: PartMenu
     * @param player the Player
     * @param part the part
     * @param status the Status
     */
    public openStatusConfigureGUILater(Player player, int part, Status status) {
        this.player = player;
        this.part = part;
        this.status = status;
    }

    public Player getPlayer() {
        return player;
    }
}
