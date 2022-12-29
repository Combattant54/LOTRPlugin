package fr.ph1lou.werewolfplugin.commands.utilities;

import fr.ph1lou.werewolfapi.annotations.PlayerCommand;
import fr.ph1lou.werewolfapi.annotations.Event;
import fr.ph1lou.werewolfapi.listeners.impl.ListenerWerewolf;
import fr.ph1lou.werewolfapi.player.utils.Formatter;
import fr.ph1lou.werewolfapi.commands.ICommand;
import fr.ph1lou.werewolfapi.game.WereWolfAPI;
import fr.ph1lou.werewolfapi.basekeys.ConfigBase;
import fr.ph1lou.werewolfapi.basekeys.Prefix;
import fr.ph1lou.werewolfapi.utils.Wrapper;
import fr.ph1lou.werewolfplugin.Register;
import org.bukkit.entity.Player;

@PlayerCommand(key = "werewolf.commands.player.random_events.command",
        descriptionKey = "werewolf.commands.player.random_events.description",
        argNumbers = 0)
public class CommandRandomEvents implements ICommand {

    @Override
    public void execute(WereWolfAPI game, Player player, String[] args) {

        if (game.getConfig().isConfigActive(ConfigBase.HIDE_EVENTS)) {

            player.sendMessage(game.translate(Prefix.RED , "werewolf.commands.player.random_events.disable"));

            return;
        }

        player.sendMessage(game.translate(Prefix.GREEN , "werewolf.commands.player.random_events.list"));

        StringBuilder sb = new StringBuilder();

        for (Wrapper<ListenerWerewolf, Event> randomEventRegister : Register.get().getRandomEventsRegister()) {

            if (game.getConfig().getProbability(randomEventRegister.getMetaDatas().key()) > 0) {
                sb.append(game.translate("werewolf.commands.player.random_events.command_message",
                        Formatter.format("&event&",game.translate(randomEventRegister.getMetaDatas().key())),
                                Formatter.number(game.getConfig().getProbability(randomEventRegister.getMetaDatas().key()))))
                        .append(", ");
            }
        }

        player.sendMessage(sb.toString());
    }
}
