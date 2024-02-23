package fr.combattant54.lotrplugin.commands.admin.ingame;

import fr.combattant54.lotrplugin.game.GameManager;
import fr.combattant54.lotrapi.annotations.AdminCommand;
import fr.combattant54.lotrapi.basekeys.Prefix;
import fr.combattant54.lotrapi.commands.ICommand;
import fr.combattant54.lotrapi.enums.StateGame;
import fr.combattant54.lotrapi.enums.StatePlayer;
import fr.combattant54.lotrapi.events.UpdateNameTagEvent;
import fr.combattant54.lotrapi.events.game.permissions.ModeratorEvent;
import fr.combattant54.lotrapi.game.IModerationManager;
import fr.combattant54.lotrapi.game.WereWolfAPI;
import fr.combattant54.lotrapi.player.interfaces.IPlayerWW;
import fr.combattant54.lotrapi.player.utils.Formatter;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;

import java.util.UUID;

@AdminCommand(key = "werewolf.commands.admin.moderator.command",
        descriptionKey = "",
        argNumbers = 1)
public class CommandModerator implements ICommand {


    @Override
    public void execute(WereWolfAPI game, Player player, String[] args) {


        IModerationManager moderationManager = game.getModerationManager();
        Player moderator = Bukkit.getPlayer(args[0]);

        if (moderator == null) {
            player.sendMessage(game.translate(Prefix.RED, "werewolf.check.offline_player"));
            return;
        }

        UUID argUUID = moderator.getUniqueId();
        IPlayerWW playerWW1 = game.getPlayerWW(argUUID).orElse(null);

        if (moderationManager.getModerators().contains(argUUID)) {
            Bukkit.broadcastMessage(game.translate(Prefix.RED, "werewolf.commands.admin.moderator.remove",
                    Formatter.player(moderator.getName())));
            moderationManager.getModerators().remove(argUUID);

            if (game.isState(StateGame.LOBBY)) {
                ((GameManager) game).finalJoin(moderator);
            }
            Bukkit.getPluginManager().callEvent(new ModeratorEvent(argUUID, false));
            Bukkit.getPluginManager().callEvent(new UpdateNameTagEvent(moderator));
            return;
        }

        if (!game.isState(StateGame.LOBBY)) {
            if (playerWW1 != null && !playerWW1.isState(StatePlayer.DEATH)) {
                player.sendMessage(game.translate(Prefix.RED, "werewolf.commands.admin.moderator.player_living"));
                return;
            }
        } else {
            if (playerWW1 != null) {
                ((GameManager) game).remove(argUUID);
            } else {
                moderationManager.getQueue().remove(argUUID);
            }
            game.getModerationManager().checkQueue();
        }
        moderator.setGameMode(GameMode.SPECTATOR);
        moderationManager.addModerator(argUUID);
        Bukkit.broadcastMessage(game.translate(Prefix.GREEN, "werewolf.commands.admin.moderator.add",
                Formatter.player(moderator.getName())));
        Bukkit.getPluginManager().callEvent(new ModeratorEvent(argUUID, true));
        Bukkit.getPluginManager().callEvent(new UpdateNameTagEvent(moderator));
    }
}
