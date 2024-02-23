package fr.combattant54.lotrplugin.commands.admin;

import fr.combattant54.lotrplugin.game.GameManager;
import fr.combattant54.lotrapi.annotations.AdminCommand;
import fr.combattant54.lotrapi.basekeys.Prefix;
import fr.combattant54.lotrapi.commands.ICommand;
import fr.combattant54.lotrapi.enums.StateGame;
import fr.combattant54.lotrapi.game.WereWolfAPI;
import fr.combattant54.lotrapi.player.utils.Formatter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

@AdminCommand(key = "werewolf.commands.admin.stop.command",
        descriptionKey = "werewolf.commands.admin.stop.description",
        statesGame = {StateGame.START, StateGame.TRANSPORTATION, StateGame.GAME, StateGame.END},
        argNumbers = 0)
public class CommandStop implements ICommand {

    @Override
    public void execute(WereWolfAPI game, Player player, String[] args) {

        Bukkit.broadcastMessage(game.translate(Prefix.RED, "werewolf.commands.admin.stop.send",
                Formatter.player(player.getName())));
        ((GameManager) game).setState(StateGame.END);
        game.stopGame();

    }
}
