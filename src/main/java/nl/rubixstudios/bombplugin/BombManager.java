package nl.rubixstudios.bombplugin;

import lombok.Getter;
import nl.rubixstudios.bombplugin.bombs.CustomBomb;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class BombManager {
    // instance
    private static BombManager instance;

    @Getter
    private final List<CustomBomb> customBombs = new ArrayList<>();


    public BombManager() {
        instance = this;

        // start runnable
        bombHandler().runTaskTimer(Bomb.getInstance(), 1L, 1L);
    }

    public BukkitRunnable bombHandler() {
        return new BukkitRunnable() {
            @Override
            public void run() {
                if (BombManager.getInstance().getCustomBombs().isEmpty()) return;

                final List<CustomBomb> customBombs = new ArrayList<>(BombManager.getInstance().getCustomBombs());
                customBombs.forEach(customBomb -> {

                    if (customBomb.getBomb().isOnGround() && !customBomb.isActivated()) {
                        activateBomb(customBomb);
                    } else if (!customBomb.getBomb().isOnGround()) {
                        return;
                    }

                    customBomb.update();

                    final float lerpTime = customBomb.getLerpTime();

                    final List<Player> nearbyPlayers = customBomb.getBomb().getNearbyEntities(customBomb.getRadius() - 1, customBomb.getRadius() - 1, customBomb.getRadius() - 1).stream().filter(entity -> entity instanceof Player).map(entity -> (Player) entity).collect(Collectors.toList());

                    for (Player nearbyPlayer : nearbyPlayers) {
                        if (customBomb.mustBeAffected(nearbyPlayer) && !customBomb.isAffected(nearbyPlayer)) {
                            customBomb.affectPlayer(nearbyPlayer);
                        }

                    }

                    for (Player affectedPlayer : new ArrayList<>(customBomb.getAffectedPlayers())) {
                        if (nearbyPlayers.contains(affectedPlayer)) continue;

                        customBomb.removeEffect(affectedPlayer);
                    }


                    if (lerpTime >= 1) {

                        new ArrayList<>(customBomb.getAffectedPlayers()).forEach(customBomb::removeEffect);


                        BombManager.getInstance().getCustomBombs().remove(customBomb);
                    }
                });
            }
        };
    }

    public void initializeParticleLocations(CustomBomb customBomb) {
        final World world = customBomb.getBomb().getWorld();
        final Location dropLoc = customBomb.getBomb().getLocation();

        final float maxRadius = customBomb.getRadius();

        // Calculate the step to ensure a 1-block gap between layers
        double step = Math.asin(1.0 / maxRadius);

        for (double theta = 0; theta <= Math.PI; theta += step) {
            double radius = maxRadius * Math.sin(theta);
            double y = dropLoc.getY() + (maxRadius * Math.cos(theta));

            for (double phi = 0; phi < 2 * Math.PI; phi += step) {
                double x = dropLoc.getX() + (radius * Math.cos(phi));
                double z = dropLoc.getZ() + (radius * Math.sin(phi));

                Location particleLoc = new Location(world, x, y, z);

                customBomb.getParticleLocations().add(particleLoc);
            }
        }
    }

    public void spawnParticles(CustomBomb customBomb) {
        for (Location particleLocation : customBomb.getParticleLocations()) {
            particleLocation.getWorld().spigot().playEffect(particleLocation, customBomb.getParticleEffect(), 0, 0, 0, 0, 0, 0, 6, 16);
        }
    }

    private void activateBomb(CustomBomb customBomb) {

        customBomb.setActivated(true);
        customBomb.setActivateTime(System.currentTimeMillis());

        initializeParticleLocations(customBomb);
        spawnParticles(customBomb);

        customBomb.onActivation();


    }

    // getter for instance
    public static BombManager getInstance() {
        if (instance == null) {
            instance = new BombManager();
        }
        return instance;
    }


    public void addBomb(CustomBomb customBomb) {
        customBombs.add(customBomb);
    }


}
