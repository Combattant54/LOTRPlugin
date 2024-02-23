package fr.combattant54.lotrplugin.commands.admin;

import fr.combattant54.lotrapi.annotations.AdminCommand;
import fr.combattant54.lotrapi.basekeys.Prefix;
import fr.combattant54.lotrapi.commands.ICommand;
import fr.combattant54.lotrapi.enums.StateGame;
import fr.combattant54.lotrapi.game.WereWolfAPI;
import org.bukkit.entity.Player;

@AdminCommand(key = "werewolf.commands.admin.change.command",
        descriptionKey = "werewolf.commands.admin.change.description",
        statesGame = StateGame.LOBBY,
        argNumbers = 0)
public class CommandChange implements ICommand {

    @Override
    public void execute(WereWolfAPI game, Player player, String[] args) {


        player.sendMessage(game.translate(Prefix.YELLOW, "werewolf.commands.admin.change.in_progress"));
        game.getMapManager().loadMap();

        player.sendMessage(game.translate(Prefix.GREEN, "werewolf.commands.admin.change.finished"));

    }
}
