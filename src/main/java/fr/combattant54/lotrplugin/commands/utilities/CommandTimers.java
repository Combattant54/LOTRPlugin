package fr.combattant54.lotrplugin.commands.utilities;

import fr.combattant54.lotrplugin.Register;
import fr.combattant54.lotrapi.annotations.PlayerCommand;
import fr.combattant54.lotrapi.basekeys.Prefix;
import fr.combattant54.lotrapi.commands.ICommand;
import fr.combattant54.lotrapi.game.WereWolfAPI;
import fr.combattant54.lotrapi.player.utils.Formatter;
import org.bukkit.entity.Player;

import java.util.Comparator;
import java.util.stream.Collectors;

@PlayerCommand(key = "werewolf.commands.player.timers.command",
        descriptionKey = "werewolf.commands.player.timers.description",
        argNumbers = 0)
public class CommandTimers implements ICommand {

    @Override
    public void execute(WereWolfAPI game, Player player, String[] args) {

        player.sendMessage(game.translate(Prefix.BLUE, "werewolf.commands.player.timers.list"));

        String message = Register.get().getTimersRegister()
                .stream()
                .filter(timerWrapper -> game.getConfig().getTimerValue(timerWrapper.getMetaDatas().key()) >= 0)
                .sorted((o1, o2) -> game.translate(o1.getMetaDatas().key())
                        .compareToIgnoreCase(game.translate(o2.getMetaDatas().key())))
                .sorted(Comparator.comparingInt(o -> game.getConfig().getTimerValue(o.getMetaDatas().key())))
                .sorted((o1, o2) -> {
                    if (o1.getMetaDatas().decrement() && o2.getMetaDatas().decrement()) {
                        return 0;
                    }
                    if (o1.getMetaDatas().decrement()) {
                        return -1;
                    }
                    if (o2.getMetaDatas().decrement()) {
                        return 1;
                    }
                    if (o1.getMetaDatas().decrementAfterRole() && o2.getMetaDatas().decrementAfterRole()) {
                        return 0;
                    }
                    if (o1.getMetaDatas().decrementAfterRole()) {
                        return -1;
                    }
                    if (o2.getMetaDatas().decrementAfterRole()) {
                        return 1;
                    }
                    return 0;
                })
                .map(timerWrapper -> game.translate(timerWrapper.getMetaDatas().key(),
                        Formatter.timer(game, timerWrapper.getMetaDatas().key())))
                .collect(Collectors.joining(", "));

        player.sendMessage(message);
    }
}
