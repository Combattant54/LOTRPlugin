package fr.combattant54.lotrplugin.commands.roles.darkside;

import fr.combattant54.lotrapi.annotations.PlayerCommand;
import fr.combattant54.lotrapi.basekeys.EventBase;
import fr.combattant54.lotrapi.basekeys.Prefix;
import fr.combattant54.lotrapi.basekeys.TimerBase;
import fr.combattant54.lotrapi.commands.ICommand;
import fr.combattant54.lotrapi.enums.StateGame;
import fr.combattant54.lotrapi.enums.StatePlayer;
import fr.combattant54.lotrapi.events.werewolf.AppearInWereWolfListEvent;
import fr.combattant54.lotrapi.events.werewolf.RequestSeeWereWolfListEvent;
import fr.combattant54.lotrapi.game.WereWolfAPI;
import fr.combattant54.lotrapi.player.interfaces.IPlayerWW;
import fr.combattant54.lotrapi.player.utils.Formatter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.UUID;

@PlayerCommand(key = "werewolf.roles.werewolf.command",
        descriptionKey = "",
        argNumbers = 0,
        statesGame = StateGame.GAME,
        statesPlayer = StatePlayer.ALIVE)
public class CommandWereWolf implements ICommand {

    @Override
    public void execute(WereWolfAPI game, Player player, String[] args) {

        UUID uuid = player.getUniqueId();
        IPlayerWW playerWW = game.getPlayerWW(uuid).orElse(null);

        if (playerWW == null) return;

        if (game.getConfig().getTimerValue(TimerBase.WEREWOLF_LIST) > 0) {
            playerWW.sendMessageWithKey(Prefix.RED, "werewolf.roles.werewolf.list_not_revealed");
            return;
        }

        RequestSeeWereWolfListEvent requestSeeWereWolfListEvent = new RequestSeeWereWolfListEvent(game.getPlayerWW(uuid).orElse(null));
        Bukkit.getPluginManager().callEvent(requestSeeWereWolfListEvent);

        if (!requestSeeWereWolfListEvent.isAccept()) {
            playerWW.sendMessageWithKey(Prefix.RED, "werewolf.roles.werewolf.not_werewolf");
            return;
        }

        StringBuilder list = new StringBuilder();

        for (IPlayerWW playerWW1 : game.getPlayersWW()) {

            AppearInWereWolfListEvent appearInWereWolfListEvent =
                    new AppearInWereWolfListEvent(playerWW1, game.getPlayerWW(uuid).orElse(null));
            Bukkit.getPluginManager().callEvent(appearInWereWolfListEvent);

            if (playerWW1.isState(StatePlayer.ALIVE) && appearInWereWolfListEvent.isAppear()) {
                list.append(playerWW1.getName()).append(" ");
            }
        }
        playerWW.sendMessageWithKey(Prefix.YELLOW, "werewolf.roles.werewolf.werewolf_list", Formatter.format("&list&", list.toString()));

        game.getListenersManager().getRandomEvent(EventBase.DRUNKEN_WEREWOLF)
                .ifPresent(listenerWerewolf -> {
                    if (listenerWerewolf.isRegister()) {
                        playerWW.sendMessageWithKey("werewolf.commands.player.ww_chat.drunken");
                    }
                });

    }
}
