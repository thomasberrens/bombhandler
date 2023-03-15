package nl.rubixstudios.bombplugin.commands;

import nl.rubixstudios.bombplugin.configs.Language;
import nl.rubixstudios.bombplugin.bombs.BombType;
import nl.rubixstudios.bombplugin.util.ColorUtil;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class ListBombsCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (!commandSender.hasPermission("bomb.list") || !commandSender.isOp()) {
            commandSender.sendMessage(ColorUtil.translate(Language.NO_PERMISSION));
            return true;
        }


        commandSender.sendMessage(ColorUtil.translate(Language.BOMB_LIST_HEADER));
        for (BombType value : BombType.values()) {
            commandSender.sendMessage(ColorUtil.translate(Language.BOMB_LIST_ITEM).replaceAll("<bomb>", value.name()));
        }
        commandSender.sendMessage(ColorUtil.translate(Language.BOMB_LIST_FOOTER));


        return true;
    }
}
