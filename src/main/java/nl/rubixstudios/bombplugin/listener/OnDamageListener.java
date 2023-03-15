package nl.rubixstudios.bombplugin.listener;

import nl.rubixstudios.bombplugin.Bomb;
import nl.rubixstudios.bombplugin.bombs.BombType;
import nl.rubixstudios.bombplugin.util.ParticleUtil;
import nl.rubixstudios.bombplugin.util.nms.NmsUtil;
import org.bukkit.Effect;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerItemDamageEvent;

public class OnDamageListener implements Listener {

    // this has to be a config value
    private final float DAMAGE_MULTIPLIER = Bomb.getInstance().getBombConfig().getBleedBombDamageAmplifier();


    @EventHandler (priority = EventPriority.HIGHEST)
    private void onDamage(EntityDamageEvent event) {
        if (event.isCancelled()) return;

        if (!((event.getEntity() instanceof Player))) return;

        Player player = (Player) event.getEntity();

        if (player.hasMetadata(BombType.BLEED_BOMB.name())) {

            event.setDamage(event.getDamage() * DAMAGE_MULTIPLIER);

            ParticleUtil.runColoredParticlesCubeAtPlayer(player, 4, 255, 0,0, Effect.COLOURED_DUST);

        }
    }

    @EventHandler
    private void onDurabilityChange(PlayerItemDamageEvent event) {
        if (event.isCancelled()) return;

        Player player = event.getPlayer();

        if (player.hasMetadata(BombType.SMOKE_BOMB.name())) {
            NmsUtil.updateArmor(player, true);
            event.setCancelled(true);
        }
    }

}
