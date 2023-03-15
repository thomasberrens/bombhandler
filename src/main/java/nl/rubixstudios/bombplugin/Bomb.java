package nl.rubixstudios.bombplugin;

import lombok.Getter;
import me.qiooip.lazarus.timer.TimerManager;
import nl.rubixstudios.bombplugin.bombs.*;
import nl.rubixstudios.bombplugin.commands.GiveBombCommand;
import nl.rubixstudios.bombplugin.commands.ListBombsCommand;
import nl.rubixstudios.bombplugin.configs.BombConfig;
import nl.rubixstudios.bombplugin.configs.Language;
import nl.rubixstudios.bombplugin.configs.LanguageConfig;
import nl.rubixstudios.bombplugin.listener.OnDamageListener;
import nl.rubixstudios.bombplugin.util.ColorUtil;
import nl.rubixstudios.bombplugin.util.NBTUtil;
import org.bukkit.Bukkit;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.Vector;

public final class Bomb extends JavaPlugin implements Listener {

    @Getter private static Bomb instance;

    @Getter private BombConfig bombConfig;

    @Getter private LanguageConfig languageConfig;

    @Getter private boolean fullyEnabled = false;

    @Override
    public void onEnable() {
        // Plugin startup logic
        instance = this;

        this.log("&7===&f=============================================&7===");
        this.log("- &eName&7: &e&lBombs");
        this.log("- &eVersion&7: &f" + this.getDescription().getVersion());
        this.log("- &eAuthor&7: &f" + this.getDescription().getAuthors());
        this.log("");

        bombConfig = new BombConfig();
        bombConfig.createCustomConfig();

        languageConfig = new LanguageConfig();
        languageConfig.createCustomConfig();

        Language.initializeLanguageValues();
        // Register events
        getServer().getPluginManager().registerEvents(this, this);
        getServer().getPluginManager().registerEvents(new OnDamageListener(), this);

        // Register commands
        getCommand("givebomb").setExecutor(new GiveBombCommand());
        getCommand("listbombs").setExecutor(new ListBombsCommand());

        if (!foundDependencies()) {
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }


        this.log("");
        this.log("- &aSuccesfully enabled &e&lBombs&a plugin.");
        this.log("&7===&f=============================================&7===");

        this.fullyEnabled = true;
    }


    private boolean foundDependencies() {
        if(!getServer().getPluginManager().isPluginEnabled("Lazarus")) {
            this.log("&eChecking Dependencies:");
            this.log("   &c&lLazarus is not installed on this server!");
            this.log("");
            this.log("- &cDisabling plugin...");
            this.log("&7===&f=============================================&7===");
            return false;
        } else {
            this.log("&eChecking Dependencies:");
            this.log("   &aSuccesfully detected &e&lLazarus&a!");
            this.log("");
        }

        if(!getServer().getPluginManager().isPluginEnabled("ProtocolLib")) {
            this.log("   &c&lProtocolLib is not installed on this server!");
            this.log("");
            this.log("- &cDisabling plugin...");
            this.log("&7===&f=============================================&7===");
            return false;
        } else {
            this.log("   &aSuccesfully detected &e&lProtocolLib&a!");
            this.log("");
        }
        return true;
    }

    public void log(String message) {
        Bukkit.getConsoleSender().sendMessage(ColorUtil.translate(message));
    }

    private BombType getBombType(ItemStack item) {
        for (BombType bombType : BombType.values()) {
            if (!NBTUtil.hasItemData(item, bombType.name())) continue;

            return bombType;
        }

        return null;
    }

    private CustomBomb createBomb(final Player player, final BombType bombType, final Item bomb) {
        switch (bombType) {
            case SMOKE_BOMB:
                return new SmokeCustomBomb(player, bomb);
            case FLASH_BANG:
                return new FlashBang(player, bomb);
            case BLEED_BOMB:
                return new BleedCustomBomb(player, bomb);
        }

        return null;
    }

    @EventHandler
    private void onPickupBomb(PlayerPickupItemEvent event) {
        final Item item = event.getItem();

        if (!item.hasMetadata("bomb")) return;

        event.setCancelled(true);
    }

    @EventHandler
    private void onRightClick(PlayerInteractEvent event) {

        if (!event.getAction().equals(Action.RIGHT_CLICK_BLOCK) && !event.getAction().equals(Action.RIGHT_CLICK_AIR)) return;

        final ItemStack item = event.getItem();

        if (item == null) return;

        final BombType bombType = getBombType(item);

        if (bombType == null) return;

        if (TimerManager.getInstance().getCooldownTimer().isActive(event.getPlayer(), bombType.name())) {
            event.getPlayer().sendMessage(ColorUtil.translate(Language.BOMB_COOLDOWN_ACTIVE)
                    .replaceAll("<bomb>", bombType.name().toLowerCase().replace("_", " ")));
            return;
        }


        final ItemStack bombToDrop = item.clone();
        bombToDrop.setAmount(1);

        final Item bomb = event.getPlayer().getWorld().dropItemNaturally(event.getPlayer().getEyeLocation(), bombToDrop);

        bomb.setMetadata("bomb", new FixedMetadataValue(this, true));

        final Vector vector = event.getPlayer().getLocation().getDirection(); // Give an direction for shooting the fireball.
        bomb.setVelocity(vector);

        if (item.getAmount() - 1 <= 0) {
            event.getPlayer().getInventory().remove(item);
        } else {
            item.setAmount(item.getAmount() - 1);
        }

        event.getPlayer().updateInventory();

        BombManager.getInstance().addBomb(createBomb(event.getPlayer(), bombType, bomb));

        event.setCancelled(true);
    }



    @Override
    public void onDisable() {
        // Plugin shutdown logic
        if (fullyEnabled) {
            this.log("&7===&f=============================================&7===");
            this.log("- &cDisabling &e&lBombs " + this.getDescription().getVersion());
            this.log("");

            Bukkit.getServicesManager().unregisterAll(this);
            this.log("");
            this.log("&7===&f=============================================&7===");
        }
    }
}
