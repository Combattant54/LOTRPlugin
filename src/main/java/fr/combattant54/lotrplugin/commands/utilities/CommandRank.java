package fr.combattant54.lotrplugin.commands.utilities;

import fr.combattant54.lotrapi.annotations.PlayerCommand;
import fr.combattant54.lotrapi.basekeys.Prefix;
import fr.combattant54.lotrapi.commands.ICommand;
import fr.combattant54.lotrapi.enums.StateGame;
import fr.combattant54.lotrapi.game.WereWolfAPI;
import fr.combattant54.lotrapi.player.utils.Formatter;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.UUID;

@PlayerCommand(key = "werewolf.commands.player.rank.command",
        descriptionKey = "werewolf.commands.player.rank.description",
        statesGame = StateGame.LOBBY,
        argNumbers = 0)
public class CommandRank implements ICommand {

    @Override
    public void execute(WereWolfAPI game, Player player, String[] args) {

        UUID uuid = player.getUniqueId();

        List<? extends UUID> queue = game.getModerationManager().getQueue();

        if (!game.isState(StateGame.LOBBY)) {
            player.sendMessage(game.translate(Prefix.RED, "werewolf.check.already_begin"));
            return;
        }

        if (queue.contains(uuid)) {
            player.sendMessage(game.translate(Prefix.GREEN, "werewolf.commands.player.rank.perform",
                    Formatter.number(queue.indexOf(uuid) + 1)));
        } else {
            player.sendMessage(game.translate(Prefix.RED, "werewolf.commands.player.rank.not_in_queue"));
        }
    }
}
