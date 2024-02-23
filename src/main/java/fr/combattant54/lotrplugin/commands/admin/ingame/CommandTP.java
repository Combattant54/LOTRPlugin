package fr.combattant54.lotrplugin.commands.admin.ingame;

import fr.combattant54.lotrapi.annotations.AdminCommand;
import fr.combattant54.lotrapi.basekeys.Prefix;
import fr.combattant54.lotrapi.commands.ICommand;
import fr.combattant54.lotrapi.game.IModerationManager;
import fr.combattant54.lotrapi.game.WereWolfAPI;
import fr.combattant54.lotrapi.player.utils.Formatter;
import io.papermc.lib.PaperLib;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.UUID;

@AdminCommand(key = "werewolf.commands.admin.teleportation.command",
        descriptionKey = "werewolf.commands.admin.teleportation.description",
        argNumbers = {1, 2},
        moderatorAccess = true)
public class CommandTP implements ICommand {

    @Override
    public void execute(WereWolfAPI game, Player player, String[] args) {

        UUID uuid = player.getUniqueId();
        Player playerArg1 = Bukkit.getPlayer(args[0]);
        IModerationManager moderationManager = game.getModerationManager();


        if (args.length == 1) {

            if (args[0].equals("@a")) {

                Bukkit.getOnlinePlayers()
                        .forEach(player1 ->
                                Bukkit.dispatchCommand(player, "a tp " + player1.getName()));
                return;
            }

            if (playerArg1 == null) {
                player.sendMessage(game.translate(Prefix.RED, "werewolf.check.offline_player"));
                return;
            }
            PaperLib.teleportAsync(player, playerArg1.getLocation());

            String message = game.translate(Prefix.YELLOW, "werewolf.commands.admin.teleportation.send",
                    Formatter.format("&player1&", player.getName()),
                    Formatter.format("&player2&", playerArg1.getName()));
            moderationManager.alertHostsAndModerators(message);
            if (!moderationManager.isStaff(uuid)) {
                player.sendMessage(message);
            }
            return;
        }

        if (args[0].equals("@a")) {

            Bukkit.getOnlinePlayers()
                    .forEach(player1 -> Bukkit.dispatchCommand(player, "a tp " + player1.getName() + " " + args[1]));
            return;
        }

        if (args[1].equals("@a")) {

            Bukkit.getOnlinePlayers()
                    .forEach(player1 -> Bukkit.dispatchCommand(player, "a tp " + args[0] + " " + player1.getName()));
            return;
        }

        Player playerArg2 = Bukkit.getPlayer(args[1]);

        if (playerArg2 == null || playerArg1 == null) {
            player.sendMessage(game.translate(Prefix.RED, "werewolf.check.offline_player"));
            return;
        }
        PaperLib.teleportAsync(playerArg1, playerArg2.getLocation());

        String message = game.translate(Prefix.YELLOW, "werewolf.commands.admin.teleportation.send",
                Formatter.format("&player1&", playerArg1.getName()),
                Formatter.format("&player2&", playerArg2.getName()));
        moderationManager.alertHostsAndModerators(message);
        if (!moderationManager.isStaff(uuid)) {
            player.sendMessage(message);
        }

    }

}
