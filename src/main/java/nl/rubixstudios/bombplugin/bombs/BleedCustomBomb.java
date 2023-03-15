package nl.rubixstudios.bombplugin.bombs;

import me.qiooip.lazarus.timer.TimerManager;
import me.qiooip.lazarus.timer.cooldown.CooldownTimer;
import nl.rubixstudios.bombplugin.bombs.config.BombConfigurations;
import nl.rubixstudios.bombplugin.bombs.config.BombValues;
import nl.rubixstudios.bombplugin.configs.Language;
import nl.rubixstudios.bombplugin.util.ColorUtil;
import nl.rubixstudios.bombplugin.util.LazarusUtil;
import org.bukkit.Effect;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;

public class BleedCustomBomb extends CustomBomb {

    public BleedCustomBomb(Player thrower, Item bomb) {
        super(thrower, bomb);

        setParticleEffect(Effect.WITCH_MAGIC);

        final BombValues bombValues = BombConfigurations.getInstance().getBombValues().get(BombType.BLEED_BOMB);

        setRadius(bombValues.getRadius());
        setDuration(bombValues.getDuration());

        final CooldownTimer cooldownTimer = TimerManager.getInstance().getCooldownTimer();

        cooldownTimer.activate(thrower, getType().name(), bombValues.getBombCooldown(), ColorUtil.translate(Language.BOMB_COOLDOWN_EXPIRED)
                .replaceAll("<bomb>", getType().name().toLowerCase().replace("_", " ")));
    }

    @Override
    public void affectPlayer(Player player) {
        player.setMetadata(BombType.BLEED_BOMB.name(), new FixedMetadataValue(nl.rubixstudios.bombplugin.Bomb.getInstance(), true));

        addAffectedPlayer(player);
    }

    @Override
    public void removeEffect(Player player) {
        player.removeMetadata(BombType.BLEED_BOMB.name(), nl.rubixstudios.bombplugin.Bomb.getInstance());

        removeAffectedPlayer(player);
    }

    @Override
    public boolean isAffected(Player player) {
        return player.hasMetadata(BombType.BLEED_BOMB.name());
    }

    @Override
    public boolean mustBeAffected(Player player) {
        // check for lazarus api
        final boolean isInTheSameFaction = LazarusUtil.isInSameFaction(player, getThrower())
                || LazarusUtil.isAlly(player, getThrower());

        return !isInTheSameFaction;
    }

    @Override
    public void onActivation() {

    }

    @Override
    public void update() {

    }

    @Override
    public BombType getType() {
        return BombType.BLEED_BOMB;
    }
}
