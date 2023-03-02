package nl.rubixstudios.smokebomb.bombs;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.WrappedDataWatcher;
import com.comphenix.protocol.wrappers.WrappedWatchableObject;
import net.minecraft.server.v1_8_R3.*;
import org.bukkit.Bukkit;
import org.bukkit.Effect;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_8_R3.inventory.CraftItemStack;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.potion.PotionEffectType;

import java.lang.reflect.InvocationTargetException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class SmokeBomb extends Bomb{
    public SmokeBomb(Player thrower, Item bomb) {
        super(thrower, bomb);

        setParticleEffect(Effect.LARGE_SMOKE);
    }

    @Override
    public void affectPlayer(Player player) {

        player.addPotionEffect(PotionEffectType.INVISIBILITY.createEffect(6 * 20, 1));
        updateArmor(player, true);

        Bukkit.broadcastMessage("AFFECT: " + player.getName());
        player.setMetadata("smokebomb", new FixedMetadataValue(nl.rubixstudios.smokebomb.Bomb.getInstance(), true));

        addAffectedPlayer(player);
    }

    @Override
    public void removeEffect(Player player) {
        player.removePotionEffect(PotionEffectType.INVISIBILITY);

        updateArmor(player, false);

        player.removeMetadata("smokebomb", nl.rubixstudios.smokebomb.Bomb.getInstance());

        removeAffectedPlayer(player);

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

    @Override
    public boolean isAffected(Player player) {
        return player.hasMetadata("smokebomb");
    }

    @Override
    public boolean mustBeAffected(Player player) {
        // check for lazarus api
        final boolean isInTheSameFaction = false;

        return !isInTheSameFaction;
    }
}
