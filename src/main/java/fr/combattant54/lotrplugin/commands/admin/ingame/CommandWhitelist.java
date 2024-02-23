package fr.combattant54.lotrplugin.commands.admin.ingame;

import fr.combattant54.lotrplugin.game.GameManager;
import fr.combattant54.lotrapi.annotations.AdminCommand;
import fr.combattant54.lotrapi.basekeys.Prefix;
import fr.combattant54.lotrapi.commands.ICommand;
import fr.combattant54.lotrapi.enums.StateGame;
import fr.combattant54.lotrapi.game.IModerationManager;
import fr.combattant54.lotrapi.game.WereWolfAPI;
import fr.combattant54.lotrapi.player.utils.Formatter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.UUID;

@AdminCommand(key = "werewolf.commands.admin.whitelist.command",
        descriptionKey = "werewolf.commands.admin.whitelist.description",
        moderatorAccess = true,
        statesGame = StateGame.LOBBY,
        argNumbers = 1)
public class CommandWhitelist implements ICommand {

    @Override
    public void execute(WereWolfAPI game, Player player, String[] args) {

        IModerationManager moderationManager = game.getModerationManager();
        Player playerArg = Bukkit.getPlayer(args[0]);

        if (playerArg == null) {
            player.sendMessage(game.translate(Prefix.RED, "werewolf.check.offline_player"));
            return;
        }

        UUID uuid = playerArg.getUniqueId();

        if (moderationManager.getWhiteListedPlayers().contains(uuid)) {
            player.sendMessage(game.translate(Prefix.RED, "werewolf.commands.admin.whitelist.remove",
                    Formatter.player(playerArg.getName())));
            moderationManager.removePlayerOnWhiteList(uuid);
        } else {
            player.sendMessage(game.translate(Prefix.GREEN, "werewolf.commands.admin.whitelist.add",
                    Formatter.player(playerArg.getName())));
            moderationManager.addPlayerOnWhiteList(uuid);
            ((GameManager) game).finalJoin(playerArg);
        }
    }
}
