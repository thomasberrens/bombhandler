package nl.rubixstudios.smokebomb;

import lombok.Getter;
import nl.rubixstudios.smokebomb.bombs.BleedCustomBomb;
import nl.rubixstudios.smokebomb.bombs.FlashBang;
import nl.rubixstudios.smokebomb.bombs.SmokeCustomBomb;
import nl.rubixstudios.smokebomb.listener.OnDamageListener;
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
        if (event.getItem().getType().equals(Material.FIREBALL)) {


            // remove smoke bomb from inventory
            event.getItem().setAmount(event.getItem().getAmount() - 1);

            // spawn smoke bomb
            final Item bomb = event.getPlayer().getWorld().dropItemNaturally(event.getPlayer().getEyeLocation(), event.getItem());

            final Vector vector = event.getPlayer().getLocation().getDirection(); // Give an direction for shooting the fireball.
            bomb.setVelocity(vector);

            event.getPlayer().updateInventory();
            event.setCancelled(true);
            BombManager.getInstance().addBomb(new SmokeCustomBomb(event.getPlayer(), bomb));

        }

        if (event.getItem().getType().equals(Material.BLAZE_ROD)) {
            // remove smoke bomb from inventory
            event.getItem().setAmount(event.getItem().getAmount() - 1);

            // spawn smoke bomb
            final Item bomb = event.getPlayer().getWorld().dropItemNaturally(event.getPlayer().getEyeLocation(), event.getItem());

            final Vector vector = event.getPlayer().getLocation().getDirection(); // Give an direction for shooting the fireball.
            bomb.setVelocity(vector);

            event.getPlayer().updateInventory();
            event.setCancelled(true);
            BombManager.getInstance().addBomb(new BleedCustomBomb(event.getPlayer(), bomb));
        }

        if (event.getItem().getType().equals(Material.FIREWORK)) {
            // remove smoke bomb from inventory
            event.getItem().setAmount(event.getItem().getAmount() - 1);

            final ItemStack cloned = event.getItem().clone();
            cloned.setAmount(1);
            // spawn smoke bomb
            final Item bomb = event.getPlayer().getWorld().dropItemNaturally(event.getPlayer().getEyeLocation(), cloned);

            final Vector vector = event.getPlayer().getLocation().getDirection(); // Give an direction for shooting the fireball.
            bomb.setVelocity(vector);

            event.getPlayer().updateInventory();
            event.setCancelled(true);
            BombManager.getInstance().addBomb(new FlashBang(event.getPlayer(), bomb));
        }


    }



    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
