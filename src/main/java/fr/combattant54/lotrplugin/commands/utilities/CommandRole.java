package fr.combattant54.lotrplugin.commands.utilities;

import fr.combattant54.lotrapi.annotations.PlayerCommand;
import fr.combattant54.lotrapi.commands.ICommand;
import fr.combattant54.lotrapi.enums.StateGame;
import fr.combattant54.lotrapi.game.WereWolfAPI;
import fr.combattant54.lotrapi.player.interfaces.IPlayerWW;
import org.bukkit.entity.Player;

import java.util.UUID;

@PlayerCommand(key = "werewolf.commands.player.role.command",
        descriptionKey = "werewolf.commands.player.role.description",
        statesGame = { StateGame.GAME, StateGame.END },
        argNumbers = 0)
public class CommandRole implements ICommand {

    @Override
    public void execute(WereWolfAPI game, Player player, String[] args) {

        UUID uuid = player.getUniqueId();
        IPlayerWW playerWW = game.getPlayerWW(uuid).orElse(null);

        if (playerWW == null) return;

        player.sendMessage(playerWW.getRole().getDescription());

    }
}
