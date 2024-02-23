package fr.combattant54.lotrplugin.commands.admin.ingame;

import fr.combattant54.lotrapi.annotations.AdminCommand;
import fr.combattant54.lotrapi.basekeys.Prefix;
import fr.combattant54.lotrapi.commands.ICommand;
import fr.combattant54.lotrapi.enums.StateGame;
import fr.combattant54.lotrapi.game.IMapManager;
import fr.combattant54.lotrapi.game.WereWolfAPI;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

@AdminCommand(key = "werewolf.commands.admin.preview.command",
        descriptionKey = "werewolf.commands.admin.preview.description",
        statesGame = StateGame.LOBBY,
        argNumbers = 0,
        moderatorAccess = true)
public class CommandPreview implements ICommand {

    @Override
    public void execute(WereWolfAPI game, Player player, String[] args) {

        IMapManager mapManager = game.getMapManager();

        if (player.getWorld().equals(game.getMapManager().getWorld())) {
            player.teleport(Bukkit.getWorlds().get(0).getSpawnLocation());
            player.sendMessage(game.translate(Prefix.YELLOW, "werewolf.commands.admin.preview.lobby"));
        } else {
            player.teleport(game.getMapManager().getWorld().getSpawnLocation());
            player.sendMessage(game.translate(Prefix.YELLOW, "werewolf.commands.admin.preview.map"));
        }


    }
}
