package nl.rubixstudios.bombplugin.commands;

import nl.rubixstudios.bombplugin.Bomb;
import nl.rubixstudios.bombplugin.bombs.BombType;
import nl.rubixstudios.bombplugin.configs.Language;
import nl.rubixstudios.bombplugin.util.ColorUtil;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class GiveBombCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (!commandSender.hasPermission("bomb.give") || !commandSender.isOp()) {
            commandSender.sendMessage(ColorUtil.translate(Language.NO_PERMISSION));
            return true;
        }

        if (strings.length != 3) {
            commandSender.sendMessage(ColorUtil.translate(Language.GIVE_BOMB_INVALID_ARGUMENTS));
            return true;
        }

        final Player player = Bukkit.getPlayer(strings[0]);

        if (player == null) {
            commandSender.sendMessage(ColorUtil.translate(Language.GIVE_BOMB_PLAYER_NOT_ONLINE).replaceAll("<player>", strings[0]));
            return true;
        }

        final BombType bombType = getBombType(strings[1]);

        if (bombType == null) {
            commandSender.sendMessage(ColorUtil.translate(Language.BOMB_NOT_FOUND).replaceAll("<bomb>", strings[0]));
            return true;
        }

        int amount = 1;

            try {
                amount = Integer.parseInt(strings[2]);
            } catch (NumberFormatException e) {
                commandSender.sendMessage(ColorUtil.translate(Language.GIVE_BOMB_INVALID_AMOUNT).replaceAll("<amount>", strings[2]));
                return true;
            }


        final ItemStack bombItem = Bomb.getInstance().getBombConfig().getBombItem(bombType);
        bombItem.setAmount(amount);

        player.getInventory().addItem(bombItem);

        player.sendMessage(ColorUtil.translate(Language.BOMB_GIVEN).replaceAll("<bomb>", bombType.name().toLowerCase().replace("_", " ")).replaceAll("<amount>", String.valueOf(amount)));

        player.updateInventory();

        return true;
    }


    private BombType getBombType(String bombType) {
        for (BombType value : BombType.values()) {
            if (value.name().equalsIgnoreCase(bombType)) {
                return value;
            }
        }

        return null;
    }
}
