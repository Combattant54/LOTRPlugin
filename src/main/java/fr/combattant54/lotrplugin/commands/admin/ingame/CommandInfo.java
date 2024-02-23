package fr.combattant54.lotrplugin.commands.admin.ingame;

import fr.combattant54.lotrapi.annotations.AdminCommand;
import fr.combattant54.lotrapi.basekeys.Prefix;
import fr.combattant54.lotrapi.commands.ICommand;
import fr.combattant54.lotrapi.game.WereWolfAPI;
import fr.combattant54.lotrapi.player.utils.Formatter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

@AdminCommand(key = "werewolf.commands.admin.info.command",
        descriptionKey = "werewolf.commands.admin.info.description",
        moderatorAccess = true)
public class CommandInfo implements ICommand {

    @Override
    public void execute(WereWolfAPI game, Player player, String[] args) {

        if (args.length == 0) {
            player.sendMessage(game.translate(Prefix.RED, "werewolf.check.parameters", Formatter.number(1)));
            return;
        }

        StringBuilder sb = new StringBuilder();

        for (String w : args) {
            sb.append(w).append(" ");
        }
        Bukkit.broadcastMessage(game.translate("werewolf.commands.admin.info.send",
                Formatter.format("&message&", ChatColor.translateAlternateColorCodes('&', sb.toString()))));
    }
}
