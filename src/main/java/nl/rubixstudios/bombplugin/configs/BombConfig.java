package nl.rubixstudios.bombplugin.configs;

import nl.rubixstudios.bombplugin.Bomb;
import nl.rubixstudios.bombplugin.bombs.BombType;
import nl.rubixstudios.bombplugin.util.ColorUtil;
import nl.rubixstudios.bombplugin.util.NBTUtil;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.List;

public class BombConfig {
    private File configFile;
    private FileConfiguration config;

    public float getBombDuration(BombType bombType) {

       float duration = (float) getCustomConfig().getDouble("bombs." + bombType.name() + ".duration");

        return duration;
    }

    public float getBombRadius(BombType bombType) {

        float radius = (float) getCustomConfig().getDouble("bombs." + bombType.name() + ".radius");

        return (float) getCustomConfig().getDouble("bombs." + bombType.name() + ".radius");
    }

    public float getBleedBombDamageAmplifier() {

        float damageAmplifier = (float) getCustomConfig().getDouble("bombs." + BombType.BLEED_BOMB.name() + ".damageAmplifier");

        return damageAmplifier;
    }

    public int getBombCooldown(BombType bombType) {

        int cooldown = getCustomConfig().getInt("bombs." + bombType.name() + ".abilityCooldown");

        return cooldown;
    }

    public ItemStack getBombItem(BombType bombType) {

        final FileConfiguration customConfig = getCustomConfig();

        final String name = customConfig.getString("bombs." + bombType.name() + ".itemStack.name");
        final Material material = getMaterial(customConfig.getString("bombs." + bombType.name() + ".itemStack.material"));
        final List<String> lore = customConfig.getStringList("bombs." + bombType.name() + ".itemStack.lore");

        if (material == null) {
            throw new RuntimeException("Invalid material for bomb: " + bombType.name());
        }

        final ItemStack bombItem = new ItemStack(material);

        final ItemMeta bombMeta = bombItem.getItemMeta();

        bombMeta.setDisplayName(ColorUtil.translate(name));

        bombMeta.setLore(ColorUtil.translate(lore));

        bombItem.setItemMeta(bombMeta);

        NBTUtil.setItemDataString(bombItem, bombType.name(), bombType.name());

        return bombItem;
    }

    private Material getMaterial(String material) {
        return Material.getMaterial(material);
    }


    public void createCustomConfig() {
        configFile = new File(Bomb.getInstance().getDataFolder(), "bombconfig.yml");
        if (!configFile.exists()) {
            configFile.getParentFile().mkdirs();
            saveDefaultConfig();
        }
        config = YamlConfiguration.loadConfiguration(configFile);

    }

    private void saveDefaultConfig() {
        try (InputStream in = Bomb.getInstance().getResource("bombconfig.yml")) {
            if (in == null) {
                return;
            }
            Files.copy(in, configFile.toPath());
        } catch (IOException e) {
            System.out.println("Failed to save bomb config");
            e.printStackTrace();
        }
    }

    public FileConfiguration getCustomConfig() {
        return config;
    }
}
