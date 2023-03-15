package nl.rubixstudios.bombplugin.configs;

import nl.rubixstudios.bombplugin.Bomb;
import org.bukkit.configuration.file.FileConfiguration;

public class Language {

    public static String NO_PERMISSION;
    public static String BOMB_COOLDOWN_EXPIRED;
    public static String BOMB_COOLDOWN_ACTIVE;
    public static String BOMB_NOT_FOUND;
    public static String BOMB_LIST_HEADER;
    public static String BOMB_LIST_ITEM;

    public static String BOMB_LIST_FOOTER;

    public static String GIVE_BOMB_INVALID_AMOUNT;

    public static String BOMB_GIVEN;

    public static String GIVE_BOMB_INVALID_ARGUMENTS;

    public static String GIVE_BOMB_PLAYER_NOT_ONLINE;

    public static void initializeLanguageValues() {

        final FileConfiguration config = Bomb.getInstance().getLanguageConfig().getCustomConfig();

        NO_PERMISSION = config.getString("NO_PERMISSION");
        BOMB_COOLDOWN_EXPIRED = config.getString("BOMB_COOLDOWN_EXPIRED");
        BOMB_COOLDOWN_ACTIVE = config.getString("BOMB_COOLDOWN_ACTIVE");
        BOMB_NOT_FOUND = config.getString("BOMB_NOT_FOUND");
        BOMB_LIST_HEADER = config.getString("BOMB_LIST_HEADER");
        BOMB_LIST_ITEM = config.getString("BOMB_LIST_ITEM");
        BOMB_LIST_FOOTER = config.getString("BOMB_LIST_FOOTER");
        GIVE_BOMB_INVALID_AMOUNT = config.getString("GIVE_BOMB_INVALID_AMOUNT");
        BOMB_GIVEN = config.getString("BOMB_GIVEN");
        GIVE_BOMB_INVALID_ARGUMENTS = config.getString("GIVE_BOMB_INVALID_ARGUMENTS");
        GIVE_BOMB_PLAYER_NOT_ONLINE = config.getString("GIVE_BOMB_PLAYER_NOT_ONLINE");
    }
}
