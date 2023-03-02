package nl.rubixstudios.smokebomb.listener;

import net.minecraft.server.v1_8_R3.EntityPlayer;
import net.minecraft.server.v1_8_R3.Packet;
import net.minecraft.server.v1_8_R3.PacketPlayOutEntityEquipment;
import nl.rubixstudios.smokebomb.Bomb;
import nl.rubixstudios.smokebomb.BombManager;
import nl.rubixstudios.smokebomb.util.ParticleUtil;
import org.bukkit.Bukkit;
import org.bukkit.Effect;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.inventory.EquipmentSetEvent;
import org.bukkit.event.player.PlayerItemDamageEvent;

import java.util.HashSet;
import java.util.Set;

public class OnDamageListener implements Listener {

    // this has to be a config value
    private final float DAMAGE_MULTIPLIER = 1.2f;


    @EventHandler (priority = EventPriority.HIGHEST)
    private void onDamage(EntityDamageEvent event) {
        if (event.isCancelled()) return;

        if (!((event.getEntity() instanceof Player))) return;

        Player player = (Player) event.getEntity();

        if (player.hasMetadata("bleedbomb")) {

            Bukkit.broadcastMessage("Multiplying");

            event.setDamage(event.getDamage() * DAMAGE_MULTIPLIER);

            ParticleUtil.runColoredParticlesCubeAtPlayer(player, 4, 255, 0,0, Effect.COLOURED_DUST);

        }
    }

    @EventHandler
    private void onDurabilityChange(PlayerItemDamageEvent event) {
        if (event.isCancelled()) return;

        Player player = event.getPlayer();

        if (player.hasMetadata("smokebomb")) {
            updateArmor(player, true);
            event.setCancelled(true);
        }
    }

    public void updateArmor(Player player, boolean remove) {
        Set<PacketPlayOutEntityEquipment> packets = this.getEquipmentPackets(player, remove);

        for(Player other : player.getWorld().getPlayers()) {
            if(other == player) continue;

            for(PacketPlayOutEntityEquipment packet : packets) {
                this.sendPacket(other, packet);
            }
        }

        player.updateInventory();
    }

    public void sendPacket(Player player, Object packet) {
        ((CraftPlayer) player).getHandle().playerConnection.sendPacket((Packet) packet);
    }

    private Set<PacketPlayOutEntityEquipment> getEquipmentPackets(Player player, boolean remove) {
        Set<PacketPlayOutEntityEquipment> packets = new HashSet<>();

        for (int slot = 1; slot < 5; slot++) {
            EntityPlayer entityPlayer = ((CraftPlayer) player).getHandle();

            if (entityPlayer.inventory.armor[slot - 1] == null) continue; // Skip empty slots
            PacketPlayOutEntityEquipment equipment =  new PacketPlayOutEntityEquipment(player.getEntityId(), slot, remove ? null : entityPlayer.inventory.armor[slot - 1]);
            packets.add(equipment);
        }

        return packets;
    }
}
