package nl.rubixstudios.bombplugin.util.nms;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketContainer;
import net.minecraft.server.v1_8_R3.EntityPlayer;
import net.minecraft.server.v1_8_R3.PacketPlayOutEntityEquipment;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

import java.lang.reflect.InvocationTargetException;
import java.util.HashSet;
import java.util.Set;

public class NmsUtil {

    public static void updateArmor(Player player, boolean remove) {
        Set<PacketPlayOutEntityEquipment> packets = getEquipmentPackets(player, remove);

        for(Player other : player.getWorld().getPlayers()) {
            if(other == player) continue;

            for(PacketPlayOutEntityEquipment packet : packets) {
                sendPacket(other, PacketType.Play.Server.ENTITY_EQUIPMENT, packet);
            }
        }
    }
    public static void updateArmorFor(Player player, Player target, boolean remove) {
        Set<PacketPlayOutEntityEquipment> packets = getEquipmentPackets(target, remove);

        for(PacketPlayOutEntityEquipment packet : packets) {
            sendPacket(player,PacketType.Play.Server.ENTITY_EQUIPMENT, packet);
        }
    }

    private static Set<PacketPlayOutEntityEquipment> getEquipmentPackets(Player player, boolean remove) {
        Set<PacketPlayOutEntityEquipment> packets = new HashSet<>();

        for (int slot = 1; slot < 5; slot++) {
            PacketPlayOutEntityEquipment equipment = createEquipmentPacket(player, slot, remove);
            packets.add(equipment);
        }

        return packets;
    }

    public static PacketPlayOutEntityEquipment createEquipmentPacket(Player player, int slot, boolean remove) {
        EntityPlayer entityPlayer = ((CraftPlayer) player).getHandle();
        return new PacketPlayOutEntityEquipment(player.getEntityId(), slot, remove ? null : entityPlayer.inventory.armor[slot - 1]);
    }

    public static void sendPacket(Player player, PacketType packetType, Object packet) {
        final PacketContainer packetContainer = new PacketContainer(packetType, packet);

        try {
            ProtocolLibrary.getProtocolManager().sendServerPacket(player, packetContainer);
        } catch (InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }
}
