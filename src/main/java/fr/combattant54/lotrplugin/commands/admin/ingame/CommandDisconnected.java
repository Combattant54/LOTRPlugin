package fr.combattant54.lotrplugin.commands.admin.ingame;

import fr.combattant54.lotrapi.annotations.AdminCommand;
import fr.combattant54.lotrapi.commands.ICommand;
import fr.combattant54.lotrapi.enums.StateGame;
import fr.combattant54.lotrapi.enums.StatePlayer;
import fr.combattant54.lotrapi.game.WereWolfAPI;
import fr.combattant54.lotrapi.player.interfaces.IPlayerWW;
import fr.combattant54.lotrapi.player.utils.Formatter;
import fr.combattant54.lotrapi.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

@AdminCommand(key = "werewolf.commands.admin.disconnected.command",
        descriptionKey = "werewolf.commands.admin.disconnected.description",
        moderatorAccess = true,
        statesGame = {StateGame.START, StateGame.GAME},
        argNumbers = 0)
public class CommandDisconnected implements ICommand {

    @Override
    public void execute(WereWolfAPI game, Player player, String[] args) {


        for (IPlayerWW playerWW : game.getPlayersWW()) {
            Player player1 = Bukkit.getPlayer(playerWW.getUUID());
            if (playerWW.isState(StatePlayer.ALIVE) && player1 == null) {
                player.sendMessage(game.translate("werewolf.commands.admin.disconnected.send",
                        Formatter.player(playerWW.getName()),
                        Formatter.timer(Utils.conversion(game.getTimer() - playerWW.getDisconnectedTime()))));
            }
        }
    }
}
