package nl.rubixstudios.smokebomb.packetlistener;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.*;
import net.minecraft.server.v1_8_R3.PacketPlayOutEntityEquipment;
import nl.rubixstudios.smokebomb.Bomb;
import nl.rubixstudios.smokebomb.BombManager;
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;

public class MyPacketListener {
    public MyPacketListener() {
        ProtocolLibrary.getProtocolManager().addPacketListener(listenToPackets(Bomb.getInstance()));
    }

    private PacketAdapter listenToPackets(Bomb plugin) {

        return new PacketAdapter(plugin, ListenerPriority.NORMAL, PacketType.Play.Server.ENTITY_EQUIPMENT) {
            @Override
            public void onPacketReceiving(PacketEvent event) {
                Bukkit.broadcastMessage("Receiving: " + event.getPacketType());
            }

            @Override
            public void onPacketSending(PacketEvent event) {
                Bukkit.broadcastMessage("Sending: " + event.getPacketType());

               Bukkit.broadcastMessage("TARGET: " + event.getPacket().getModifier().getTarget());


                event.getPacket().

                if (event.getPlayer().hasMetadata("smokebomb")) {
                    Bukkit.broadcastMessage("Player: " + event.getPlayer().getName());

                    event.getPlayer().removeMetadata("smokebomb", Bomb.getInstance());
                }
            }
        };
    }

}