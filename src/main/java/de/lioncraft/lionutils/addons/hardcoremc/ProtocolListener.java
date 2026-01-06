package de.lioncraft.lionutils.addons.hardcoremc;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.ListeningWhitelist;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.events.PacketListener;
import com.comphenix.protocol.injector.packet.PacketRegistry;
import de.lioncraft.lionutils.Main;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

public class ProtocolListener extends PacketAdapter {
    private static ProtocolManager protocolManager;

    public static ProtocolManager getProtocolManager() {
        return protocolManager;
    }

    public static void setProtocolManager(ProtocolManager protocolManager) {
        ProtocolListener.protocolManager = protocolManager;
    }

    public ProtocolListener() {
        super(Main.getPlugin(), PacketType.Play.Server.LOGIN);
    }

    @Override
    public void onPacketSending(PacketEvent packetEvent) {
        if (packetEvent.getPacket().getType() == PacketType.Play.Server.LOGIN ) {
            packetEvent.getPacket().getBooleans().write(0, true);
        }
    }
}
