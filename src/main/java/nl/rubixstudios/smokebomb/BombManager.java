package nl.rubixstudios.smokebomb;

import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class BombManager {
    // instance
    @Getter
    private static BombManager instance;

    @Getter
    private final List<nl.rubixstudios.smokebomb.bombs.Bomb> bombs = new ArrayList<>();

    public BombManager() {
        instance = this;

        // start runnable
        bombHandler().runTaskTimer(Bomb.getInstance(), 1L, 1L);
    }

    public BukkitRunnable bombHandler() {
        return new BukkitRunnable() {
            @Override
            public void run() {
                if (BombManager.getInstance().getBombs().isEmpty()) return;


                final List<nl.rubixstudios.smokebomb.bombs.Bomb> bombs = new ArrayList<>(BombManager.getInstance().getBombs());
                bombs.forEach(bomb -> {

                    if (bomb.getBomb().isOnGround() && !bomb.isActivated()) {
                         activateBomb(bomb);
                    } else if (!bomb.getBomb().isOnGround()) {
                        return;
                    }

                    final float lerpTime = bomb.getLerpTime();

                   final List<Player> nearbyPlayers = bomb.getBomb().getNearbyEntities(bomb.getRadius() - 1, bomb.getRadius() - 1, bomb.getRadius() - 1).stream().filter(entity -> entity instanceof Player).map(entity -> (Player) entity).collect(Collectors.toList());

                    for (Player nearbyPlayer : nearbyPlayers) {
                        if (bomb.mustBeAffected(nearbyPlayer) && !bomb.isAffected(nearbyPlayer)) {
                            bomb.affectPlayer(nearbyPlayer);
                        }

                    }

                    for (Player affectedPlayer : new ArrayList<>(bomb.getAffectedPlayers())) {
                        if (nearbyPlayers.contains(affectedPlayer)) continue;

                        bomb.removeEffect(affectedPlayer);
                    }


                    if (lerpTime >= 1) {
                        Bukkit.broadcastMessage("Stop");

                        new ArrayList<>(bomb.getAffectedPlayers()).forEach(bomb::removeEffect);

                        BombManager.getInstance().getBombs().remove(bomb);
                    }
                });
            }
        };
    }

    private void spawnParticles(nl.rubixstudios.smokebomb.bombs.Bomb bomb) {
        Location center = bomb.getBomb().getLocation();
        int numLayers = 16;
        double radius = bomb.getRadius() + 2;

        int numParticles = 24;  // Number of particles per layer
        double deltaTheta = Math.PI * 2 / numParticles;


        double peakHeight = center.getY() + numLayers;
        double peakDensity = numParticles * 3;

        for (int i = 0; i < numLayers; i++) {
            double layerHeight = center.getY() + (double) i;
            double layerRadius = radius * (1.0 - (double) i / numLayers);
            for (int j = 0; j < numParticles; j++) {
                double theta = j * deltaTheta;
                double phi = Math.PI * i / numLayers;
                double x = layerRadius * Math.sin(phi) * Math.cos(theta);
                double y = layerRadius * Math.cos(phi);
                double z = layerRadius * Math.sin(phi) * Math.sin(theta);
                Location particleLoc = center.clone().add(x, y, z);

                System.out.println("Spawned particle at " + particleLoc.getX() + "," + particleLoc.getY() + "," + particleLoc.getZ());

                particleLoc.getWorld().spigot().playEffect(particleLoc, bomb.getParticleEffect(), 0, 0, 0, 0, 0, 0, 6, 16);

                if (j % (numParticles / 4) == 0) { // Spawn additional particles in between
                    double x2 = layerRadius * Math.sin(phi) * Math.cos(theta + deltaTheta / 2);
                    double y2 = layerRadius * Math.cos(phi);
                    double z2 = layerRadius * Math.sin(phi) * Math.sin(theta + deltaTheta / 2);
                    Location particleLoc2 = center.clone().add(x2, y2, z2);
                    particleLoc2.getWorld().spigot().playEffect(particleLoc2, bomb.getParticleEffect(), 0, 0, 0, 0, 0, 0, 6, 16);

                }

            }
        }
    }

    private void activateBomb(nl.rubixstudios.smokebomb.bombs.Bomb bomb) {

        bomb.setActivated(true);
        bomb.setActivateTime(System.currentTimeMillis());
        spawnParticles(bomb);


    }


    // getter for instance
    public static BombManager getInstance() {
        if (instance == null) {
            instance = new BombManager();
        }
        return instance;
    }


    public void addBomb(nl.rubixstudios.smokebomb.bombs.Bomb bomb) {
        bombs.add(bomb);
    }


}
