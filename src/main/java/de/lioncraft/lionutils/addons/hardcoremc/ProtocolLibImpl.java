package de.lioncraft.lionutils.addons.hardcoremc;

import com.comphenix.protocol.ProtocolLibrary;
import org.bukkit.plugin.Plugin;

public final class ProtocolLibImpl {
    private ProtocolLibImpl() {}
    public static void registerNewListener() {
        ProtocolLibrary.getProtocolManager().addPacketListener(new ProtocolListener());
    }
    public static void removeListeners(Plugin plugin) {
        ProtocolLibrary.getProtocolManager().removePacketListeners(plugin);
    }
}
