package fr.ph1lou.werewolfplugin.commands.admin;

import fr.ph1lou.werewolfapi.annotations.AdminCommand;
import fr.ph1lou.werewolfplugin.game.GameManager;
import fr.ph1lou.werewolfapi.player.utils.Formatter;
import fr.ph1lou.werewolfapi.commands.ICommand;
import fr.ph1lou.werewolfapi.game.WereWolfAPI;
import fr.ph1lou.werewolfapi.basekeys.Prefix;
import fr.ph1lou.werewolfapi.enums.StateGame;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

@AdminCommand(key = "werewolf.commands.admin.stop.command",
        descriptionKey = "werewolf.commands.admin.stop.description",
        statesGame = {StateGame.START, StateGame.TRANSPORTATION, StateGame.GAME, StateGame.END},
        argNumbers = 0)
public class CommandStop implements ICommand {

    @Override
    public void execute(WereWolfAPI game, Player player, String[] args) {

        Bukkit.broadcastMessage(game.translate(Prefix.RED , "werewolf.commands.admin.stop.send",
                Formatter.player(player.getName())));
        ((GameManager) game).setState(StateGame.END);
        game.stopGame();

    }
}
