package fr.combattant54.lotrplugin.commands.admin.ingame;

import fr.combattant54.lotrplugin.game.GameManager;
import fr.combattant54.lotrapi.annotations.AdminCommand;
import fr.combattant54.lotrapi.basekeys.Prefix;
import fr.combattant54.lotrapi.commands.ICommand;
import fr.combattant54.lotrapi.enums.StateGame;
import fr.combattant54.lotrapi.game.WereWolfAPI;
import fr.combattant54.lotrapi.player.utils.Formatter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.UUID;

@AdminCommand(key = "werewolf.commands.admin.late.command",
        descriptionKey = "werewolf.commands.admin.late.description",
        statesGame = {StateGame.TRANSPORTATION, StateGame.START},
        argNumbers = 1,
        moderatorAccess = true)
public class CommandLate implements ICommand {

    @Override
    public void execute(WereWolfAPI game, Player player, String[] args) {

        Player player1 = Bukkit.getPlayer(args[0]);

        if (player1 == null) {
            player.sendMessage(game.translate(Prefix.RED, "werewolf.check.offline_player"));
            return;
        }

        UUID uuid = player1.getUniqueId();

        if (game.getPlayerWW(uuid).isPresent()) {
            player.sendMessage(game.translate(Prefix.RED, "werewolf.commands.admin.late.in_game"));
            return;
        }

        if (game.getModerationManager().getModerators().contains(uuid)) {
            return;
        }

        if(game.isState(StateGame.TRANSPORTATION)){
            player.sendMessage(game.translate(Prefix.RED, "werewolf.commands.admin.late.teleportation"));
            return;
        }

        Bukkit.broadcastMessage(game.translate(Prefix.GREEN, "werewolf.commands.admin.late.launch",
                Formatter.player(player1.getName())));

        ((GameManager) game).addLatePlayer(player1);
    }
}
