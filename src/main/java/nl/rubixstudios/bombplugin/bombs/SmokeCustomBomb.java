package nl.rubixstudios.bombplugin.bombs;

import me.qiooip.lazarus.timer.TimerManager;
import me.qiooip.lazarus.timer.cooldown.CooldownTimer;
import nl.rubixstudios.bombplugin.bombs.config.BombConfigurations;
import nl.rubixstudios.bombplugin.bombs.config.BombValues;
import nl.rubixstudios.bombplugin.configs.Language;
import nl.rubixstudios.bombplugin.util.ColorUtil;
import nl.rubixstudios.bombplugin.util.LazarusUtil;
import nl.rubixstudios.bombplugin.util.nms.NmsUtil;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.potion.PotionEffectType;
import nl.rubixstudios.bombplugin.BombManager;

public class SmokeCustomBomb extends CustomBomb {
    public SmokeCustomBomb(Player thrower, Item bomb) {
        super(thrower, bomb);

        setParticleEffect(Effect.LARGE_SMOKE);

        final BombValues bombValues = BombConfigurations.getInstance().getBombValues().get(BombType.SMOKE_BOMB);

        setRadius(bombValues.getRadius());

        setDuration(bombValues.getDuration());

        final CooldownTimer cooldownTimer = TimerManager.getInstance().getCooldownTimer();

        cooldownTimer.activate(thrower, getType().name(), bombValues.getBombCooldown(), ColorUtil.translate(Language.BOMB_COOLDOWN_EXPIRED)
                .replaceAll("<bomb>", getType().name().toLowerCase().replace("_", " ")));

    }

    private long lastParticleUpdate = 0;

    private Location startLocation;


    @Override
    public void affectPlayer(Player player) {

        player.addPotionEffect(PotionEffectType.INVISIBILITY.createEffect((int) (this.getDuration() * 20), 1));
        NmsUtil.updateArmor(player, true);

        player.setMetadata(BombType.SMOKE_BOMB.name(), new FixedMetadataValue(nl.rubixstudios.bombplugin.Bomb.getInstance(), true));

        addAffectedPlayer(player);
    }

    @Override
    public void removeEffect(Player player) {
        player.removePotionEffect(PotionEffectType.INVISIBILITY);

        NmsUtil.updateArmor(player, false);

        player.removeMetadata(BombType.SMOKE_BOMB.name(), nl.rubixstudios.bombplugin.Bomb.getInstance());

        removeAffectedPlayer(player);

    }

    @Override
    public boolean isAffected(Player player) {
        return player.hasMetadata(BombType.SMOKE_BOMB.name());
    }

    @Override
    public boolean mustBeAffected(Player player) {
        // check for lazarus api
        final boolean isInTheSameFaction = LazarusUtil.isInSameFaction(player, getThrower())
                || LazarusUtil.isAlly(player, getThrower());

        return isInTheSameFaction;
    }

    @Override
    public void onActivation() {
        lastParticleUpdate = getActivateTime();
        startLocation = getBomb().getLocation();
    }

    @Override
    public void update() {

        if (!isActivated()) return;

        if (System.currentTimeMillis() - lastParticleUpdate > 1.35 * 1000) {
            lastParticleUpdate = System.currentTimeMillis();

            BombManager.getInstance().spawnParticles(this);
        }
    }

    @Override
    public BombType getType() {
        return BombType.SMOKE_BOMB;
    }
}
