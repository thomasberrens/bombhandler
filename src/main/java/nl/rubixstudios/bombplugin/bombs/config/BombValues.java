package nl.rubixstudios.bombplugin.bombs.config;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class BombValues {
    private float duration;
    private float radius;

    private int bombCooldown;

}
