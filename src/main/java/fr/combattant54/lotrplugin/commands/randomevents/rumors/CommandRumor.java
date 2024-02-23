package fr.combattant54.lotrplugin.commands.randomevents.rumors;

import fr.combattant54.lotrapi.annotations.PlayerCommand;
import fr.combattant54.lotrapi.basekeys.EventBase;
import fr.combattant54.lotrapi.basekeys.Prefix;
import fr.combattant54.lotrapi.commands.ICommand;
import fr.combattant54.lotrapi.enums.StateGame;
import fr.combattant54.lotrapi.enums.StatePlayer;
import fr.combattant54.lotrapi.events.random_events.RumorsWriteEvent;
import fr.combattant54.lotrapi.game.WereWolfAPI;
import fr.combattant54.lotrapi.player.interfaces.IPlayerWW;
import fr.combattant54.lotrapi.player.utils.Formatter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.UUID;

@PlayerCommand(key = "werewolf.random_events.rumors.command", descriptionKey = "",
        statesGame = StateGame.GAME,
        statesPlayer = StatePlayer.ALIVE,
        autoCompletion = false)
public class CommandRumor implements ICommand {

    @Override
    public void execute(WereWolfAPI game, Player player, String[] args) {
        UUID uuid = player.getUniqueId();
        IPlayerWW playerWW = game.getPlayerWW(uuid).orElse(null);

        if (playerWW == null) {
            return;
        }

        if (args.length == 0) {
            player.sendMessage(game.translate(Prefix.RED, "werewolf.check.parameters", Formatter.number(1)));
            return;
        }

        game.getListenersManager().getRandomEvent(EventBase.RUMORS)
                .ifPresent(listenerWerewolf -> {
                    if (listenerWerewolf.isRegister()) {

                        StringBuilder sb = new StringBuilder();

                        for (String w : args) {
                            sb.append(w).append(" ");
                        }

                        RumorsWriteEvent event = new RumorsWriteEvent(playerWW, sb.toString());
                        Bukkit.getPluginManager().callEvent(event);

                        if (event.isCancelled()) {
                            playerWW.sendMessageWithKey(Prefix.RED, "werewolf.check.cancel");
                            return;
                        }

                        playerWW.sendMessageWithKey(Prefix.YELLOW, "werewolf.random_events.rumors.perform");
                    } else {
                        playerWW.sendMessageWithKey(Prefix.RED, "werewolf.random_events.rumors.no_rumor");
                    }
                });
    }
}
