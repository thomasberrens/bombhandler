package nl.rubixstudios.bombplugin.configs;

import nl.rubixstudios.bombplugin.Bomb;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;

public class LanguageConfig {

    private File configFile;
    private FileConfiguration config;

    public void createCustomConfig() {
        configFile = new File(Bomb.getInstance().getDataFolder(), "language.yml");
        if (!configFile.exists()) {
            configFile.getParentFile().mkdirs();
            saveDefaultConfig();
        }
        config = YamlConfiguration.loadConfiguration(configFile);

    }

    private void saveDefaultConfig() {
        try (InputStream in = Bomb.getInstance().getResource("language.yml")) {
            if (in == null) {
                return;
            }
            Files.copy(in, configFile.toPath());
        } catch (IOException e) {
            System.out.println("Failed to save language yml");
            e.printStackTrace();
        }
    }

    public FileConfiguration getCustomConfig() {
        return config;
    }


}
