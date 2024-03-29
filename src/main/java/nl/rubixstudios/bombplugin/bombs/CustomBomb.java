package nl.rubixstudios.bombplugin.bombs;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public abstract class CustomBomb {
   @Getter @Setter
   private Player thrower;

   @Getter @Setter
   private float duration = 5f;

   @Getter
   private final Item bomb;

   @Getter @Setter private float radius = 8f;

   @Getter @Setter
   private Effect particleEffect;

   @Getter @Setter
   private long activateTime;

   @Getter @Setter
   private boolean activated = false;

    @Getter @Setter
    private List<Location> particleLocations = new ArrayList<>();

   @Getter @Setter
    private List<Player> affectedPlayers = new ArrayList<>();

    public CustomBomb(Player thrower, Item bomb) {
        this.thrower = thrower;
        this.bomb = bomb;
    }


    public abstract void affectPlayer(Player player);

    public abstract void removeEffect(Player player);

    public abstract boolean isAffected(final Player player);
    public abstract boolean mustBeAffected(final Player player);

    public abstract void onActivation();

    public abstract void update();

    public abstract BombType getType();


    public boolean isExpired() {
        return System.currentTimeMillis() - activateTime > duration * 1000;
    }

    public float getLerpTime() {
        return (System.currentTimeMillis() - activateTime) / (duration * 1000);
    }

    protected void addAffectedPlayer(Player player) {
        if (!affectedPlayers.contains(player)) {
            affectedPlayers.add(player);
        }
    }

    protected void removeAffectedPlayer(Player player) {
        affectedPlayers.remove(player);
    }


}
