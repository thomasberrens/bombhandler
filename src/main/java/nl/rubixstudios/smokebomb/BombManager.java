package nl.rubixstudios.smokebomb;

import lombok.Getter;
import nl.rubixstudios.smokebomb.bombs.CustomBomb;
import org.bukkit.Bukkit;
import org.bukkit.Location;
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
                        Bukkit.broadcastMessage("Stop");

                        new ArrayList<>(customBomb.getAffectedPlayers()).forEach(customBomb::removeEffect);


                        BombManager.getInstance().getCustomBombs().remove(customBomb);
                    }
                });
            }
        };
    }

    public void initializeParticleLocations(CustomBomb customBomb){
        final Location center = customBomb.getBomb().getLocation();
        int numLayers = 16;
        double radius = customBomb.getRadius() + 2;

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

                //    particleLoc.getWorld().spigot().playEffect(particleLoc, bomb.getParticleEffect(), 0, 0, 0, 0, 0, 0, 6, 16);

                customBomb.getParticleLocations().add(particleLoc);

                if (j % (numParticles / 4) == 0) { // Spawn additional particles in between
                    double x2 = layerRadius * Math.sin(phi) * Math.cos(theta + deltaTheta / 2);
                    double y2 = layerRadius * Math.cos(phi);
                    double z2 = layerRadius * Math.sin(phi) * Math.sin(theta + deltaTheta / 2);
                    Location particleLoc2 = center.clone().add(x2, y2, z2);
                    //    particleLoc2.getWorld().spigot().playEffect(particleLoc2, bomb.getParticleEffect(), 0, 0, 0, 0, 0, 0, 6, 16);
                    customBomb.getParticleLocations().add(particleLoc2);
                }

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
