package nl.rubixstudios.smokebomb.bombs;

import org.bukkit.Bukkit;
import org.bukkit.Effect;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class FlashBang extends Bomb{
    public FlashBang(Player thrower, Item bomb) {
        super(thrower, bomb);

        setParticleEffect(Effect.CLOUD);
    }

    @Override
    public void affectPlayer(Player player) {
        player.setMetadata("flashbang", new FixedMetadataValue(nl.rubixstudios.smokebomb.Bomb.getInstance(), true));

        player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, (int) getDuration() * 20, 5));
        Bukkit.broadcastMessage("AFFECT: " + player.getName());
        addAffectedPlayer(player);
    }

    @Override
    public void removeEffect(Player player) {
        player.removeMetadata("flashbang", nl.rubixstudios.smokebomb.Bomb.getInstance());

        player.removePotionEffect(PotionEffectType.BLINDNESS);

        removeAffectedPlayer(player);
    }

    @Override
    public boolean isAffected(Player player) {
        return player.hasMetadata("flashbang");
    }

    @Override
    public boolean mustBeAffected(Player player) {
        // check for lazarus api
        final boolean isInTheSameFaction = false;

        return !isInTheSameFaction;
    }
}
