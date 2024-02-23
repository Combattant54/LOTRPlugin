package fr.combattant54.lotrplugin.commands.utilities;

import fr.combattant54.lotrplugin.Register;
import fr.combattant54.lotrapi.annotations.PlayerCommand;
import fr.combattant54.lotrapi.annotations.RandomEvent;
import fr.combattant54.lotrapi.basekeys.ConfigBase;
import fr.combattant54.lotrapi.basekeys.Prefix;
import fr.combattant54.lotrapi.commands.ICommand;
import fr.combattant54.lotrapi.game.WereWolfAPI;
import fr.combattant54.lotrapi.listeners.impl.ListenerWerewolf;
import fr.combattant54.lotrapi.player.utils.Formatter;
import fr.combattant54.lotrapi.utils.Wrapper;
import org.bukkit.entity.Player;

@PlayerCommand(key = "werewolf.commands.player.random_events.command",
        descriptionKey = "werewolf.commands.player.random_events.description",
        argNumbers = 0)
public class CommandRandomEvents implements ICommand {

    @Override
    public void execute(WereWolfAPI game, Player player, String[] args) {

        if (game.getConfig().isConfigActive(ConfigBase.HIDE_EVENTS)) {

            player.sendMessage(game.translate(Prefix.RED, "werewolf.commands.player.random_events.disable"));

            return;
        }

        player.sendMessage(game.translate(Prefix.GREEN, "werewolf.commands.player.random_events.list"));

        StringBuilder sb = new StringBuilder();

        for (Wrapper<ListenerWerewolf, RandomEvent> randomEventRegister : Register.get().getRandomEventsRegister()) {

            if (game.getConfig().getProbability(randomEventRegister.getMetaDatas().key()) > 0) {
                sb.append(game.translate("werewolf.commands.player.random_events.command_message",
                                Formatter.format("&event&", game.translate(randomEventRegister.getMetaDatas().key())),
                                Formatter.number(game.getConfig().getProbability(randomEventRegister.getMetaDatas().key()))))
                        .append(", ");
            }
        }

        player.sendMessage(sb.toString());
    }
}
