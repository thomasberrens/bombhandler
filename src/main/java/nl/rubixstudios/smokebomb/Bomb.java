package nl.rubixstudios.smokebomb;

import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketListener;
import lombok.Getter;
import nl.rubixstudios.smokebomb.bombs.SmokeBomb;
import nl.rubixstudios.smokebomb.listener.OnDamageListener;
import nl.rubixstudios.smokebomb.packetlistener.MyPacketListener;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Item;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.Vector;

public final class Bomb extends JavaPlugin implements Listener {

    @Getter private static Bomb instance;



    @Override
    public void onEnable() {
        // Plugin startup logic
        instance = this;

        MyPacketListener packetListener = new MyPacketListener();

        // Register events
        getServer().getPluginManager().registerEvents(this, this);
        getServer().getPluginManager().registerEvents(new OnDamageListener(), this);

    }

    @EventHandler
    private void onJoin(PlayerJoinEvent event) {

        // give player smoke bomb
        event.getPlayer().getInventory().addItem(new ItemStack(Material.FIREBALL, 64));
        event.getPlayer().getInventory().addItem(new ItemStack(Material.BLAZE_ROD, 64));
        event.getPlayer().getInventory().addItem(new ItemStack(Material.FIREWORK, 64));
    }


    @EventHandler
    private void onRightClick(PlayerInteractEvent event) {

        // check if player is holding a smoke bomb
        if (!event.getItem().getType().equals(Material.FIREBALL)) return;


            // remove smoke bomb from inventory
            event.getItem().setAmount(event.getItem().getAmount() - 1);

            // spawn smoke bomb
             final Item bomb = event.getPlayer().getWorld().dropItemNaturally(event.getPlayer().getEyeLocation(), event.getItem());

             final Vector vector = event.getPlayer().getLocation().getDirection(); // Give an direction for shooting the fireball.
            bomb.setVelocity(vector);

            event.getPlayer().updateInventory();
            event.setCancelled(true);
            BombManager.getInstance().addBomb(new SmokeBomb(event.getPlayer(), bomb));


    }



    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}