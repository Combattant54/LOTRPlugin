package fr.combattant54.lotrplugin.commands.admin.ingame;

import fr.combattant54.lotrapi.annotations.AdminCommand;
import fr.combattant54.lotrapi.commands.ICommand;
import fr.combattant54.lotrapi.enums.StateGame;
import fr.combattant54.lotrapi.game.WereWolfAPI;
import org.bukkit.entity.Player;

@AdminCommand(key = "werewolf.commands.admin.set_group.command",
        descriptionKey = "werewolf.commands.admin.set_group.description",
        moderatorAccess = true,
        argNumbers = 1,
        statesGame = StateGame.GAME)
public class CommandSetGroup implements ICommand {

    @Override
    public void execute(WereWolfAPI game, Player player, String[] args) {

        try {
            game.setGroup(Integer.parseInt(args[0]));
            player.performCommand(String.format("a %s", game.translate("werewolf.commands.admin.group.command")));

        } catch (NumberFormatException ignored) {
        }
    }
}
