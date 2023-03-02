package nl.rubixstudios.smokebomb.bombs;

import org.bukkit.Bukkit;
import org.bukkit.Effect;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;

public class BleedBomb extends Bomb{

    public BleedBomb(Player thrower, Item bomb) {
        super(thrower, bomb);

        setParticleEffect(Effect.WITCH_MAGIC);
    }

    @Override
    public void affectPlayer(Player player) {
        player.setMetadata("bleedbomb", new FixedMetadataValue(nl.rubixstudios.smokebomb.Bomb.getInstance(), true));

        Bukkit.broadcastMessage("AFFECT: " + player.getName());

        addAffectedPlayer(player);
    }

    @Override
    public void removeEffect(Player player) {
        player.removeMetadata("bleedbomb", nl.rubixstudios.smokebomb.Bomb.getInstance());

        removeAffectedPlayer(player);
    }

    @Override
    public boolean isAffected(Player player) {
        return player.hasMetadata("bleedbomb");
    }

    @Override
    public boolean mustBeAffected(Player player) {
        // check for lazarus api
        final boolean isInTheSameFaction = false;

        return !isInTheSameFaction;
    }
}
