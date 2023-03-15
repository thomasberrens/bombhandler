package nl.rubixstudios.bombplugin.bombs.config;

import lombok.Getter;
import nl.rubixstudios.bombplugin.Bomb;
import nl.rubixstudios.bombplugin.bombs.BombType;
import nl.rubixstudios.bombplugin.configs.BombConfig;

import java.util.HashMap;

public class BombConfigurations {


    private static BombConfigurations instance;

    @Getter
    private final HashMap<BombType, BombValues> bombValues = new HashMap<>();


    private BombConfigurations() {
        initializeBombConfigs();
    }

    private void initializeBombConfigs() {
        // initialize smoke bomb config

        final BombConfig bombConfig = Bomb.getInstance().getBombConfig();

        final BombValues smokeBombValues = new BombValues(
                bombConfig.getBombDuration(BombType.SMOKE_BOMB),
                bombConfig.getBombRadius(BombType.SMOKE_BOMB),
                bombConfig.getBombCooldown(BombType.SMOKE_BOMB));

        bombValues.put(BombType.SMOKE_BOMB, smokeBombValues);

        // initialize flash bang config

        final BombValues flashBangValues = new BombValues(
                bombConfig.getBombDuration(BombType.FLASH_BANG),
                bombConfig.getBombRadius(BombType.FLASH_BANG),
                bombConfig.getBombCooldown(BombType.FLASH_BANG));

        bombValues.put(BombType.FLASH_BANG, flashBangValues);

        // initialize bleed bomb config
        final BombValues bleedBombValues = new BombValues(
                bombConfig.getBombDuration(BombType.BLEED_BOMB),
                bombConfig.getBombRadius(BombType.BLEED_BOMB),
                bombConfig.getBombCooldown(BombType.BLEED_BOMB));


        bombValues.put(BombType.BLEED_BOMB, bleedBombValues);

    }


    public static BombConfigurations getInstance() {
        if (instance == null) {
            instance = new BombConfigurations();
        }
        return instance;
    }
}
