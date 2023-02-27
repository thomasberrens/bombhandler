package nl.rubixstudios.smokebomb;

import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;

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

                    Bukkit.getOnlinePlayers().stream().filter(player -> player.getWorld().equals(bomb.getBomb().getWorld()) && player.getLocation().distance(bomb.getBomb().getLocation()) <= bomb.getRadius()).forEach(player -> {
                        if (!bomb.isAffected(player)) {
                            bomb.affectPlayer(player);
                        }
                    });

                    new ArrayList<>(bomb.getAffectedPlayers()).stream().filter(player -> !player.getWorld().equals(bomb.getBomb().getWorld()) || player.getLocation().distance(bomb.getBomb().getLocation()) > bomb.getRadius()).forEach(bomb::removeEffect);


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
        double radius = bomb.getRadius();

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

                particleLoc.getWorld().spigot().playEffect(particleLoc, Effect.LARGE_SMOKE, 0, 0, 0, 0, 0, 0, 6, 16);

                if (j % (numParticles / 4) == 0) { // Spawn additional particles in between
                    double x2 = layerRadius * Math.sin(phi) * Math.cos(theta + deltaTheta / 2);
                    double y2 = layerRadius * Math.cos(phi);
                    double z2 = layerRadius * Math.sin(phi) * Math.sin(theta + deltaTheta / 2);
                    Location particleLoc2 = center.clone().add(x2, y2, z2);
                    particleLoc2.getWorld().spigot().playEffect(particleLoc2, Effect.LARGE_SMOKE, 0, 0, 0, 0, 0, 0, 6, 16);

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
