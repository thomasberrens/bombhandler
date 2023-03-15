package nl.rubixstudios.bombplugin.util;

import me.qiooip.lazarus.factions.FactionsManager;
import me.qiooip.lazarus.factions.type.PlayerFaction;
import org.bukkit.entity.Player;

public class LazarusUtil {

    public static boolean isInSameFaction(final Player player, final Player other) {
        if (player == null || other == null) {
            return false;
        }

        if (player == other) return true;

        final PlayerFaction playerFaction = FactionsManager.getInstance().getPlayerFaction(player.getUniqueId());
        final PlayerFaction otherFaction = FactionsManager.getInstance().getPlayerFaction(other.getUniqueId());

        if (playerFaction == null || otherFaction == null) {
            return false;
        }

        return playerFaction.equals(otherFaction);
    }

    public static boolean isAlly(final Player player, final Player other) {
        if (player == null || other == null) {
            return false;
        }

        final PlayerFaction playerFaction = FactionsManager.getInstance().getPlayerFaction(player.getUniqueId());
        final PlayerFaction otherFaction = FactionsManager.getInstance().getPlayerFaction(other.getUniqueId());

        if (playerFaction == null || otherFaction == null) {
            return false;
        }

        return playerFaction.isAlly(otherFaction);
    }
}
