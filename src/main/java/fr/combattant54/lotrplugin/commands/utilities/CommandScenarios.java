package fr.combattant54.lotrplugin.commands.utilities;

import fr.combattant54.lotrplugin.Register;
import fr.combattant54.lotrapi.annotations.PlayerCommand;
import fr.combattant54.lotrapi.annotations.Scenario;
import fr.combattant54.lotrapi.basekeys.ConfigBase;
import fr.combattant54.lotrapi.basekeys.Prefix;
import fr.combattant54.lotrapi.commands.ICommand;
import fr.combattant54.lotrapi.game.WereWolfAPI;
import fr.combattant54.lotrapi.listeners.impl.ListenerWerewolf;
import fr.combattant54.lotrapi.utils.Wrapper;
import org.bukkit.entity.Player;

@PlayerCommand(key = "werewolf.commands.player.scenarios.command",
        descriptionKey = "werewolf.commands.player.scenarios.description",
        argNumbers = 0)
public class CommandScenarios implements ICommand {

    @Override
    public void execute(WereWolfAPI game, Player player, String[] args) {

        if (game.getConfig().isConfigActive(ConfigBase.HIDE_SCENARIOS)) {

            player.sendMessage(game.translate(Prefix.RED, "werewolf.commands.player.scenarios.disable"));

            return;
        }

        player.sendMessage(game.translate(Prefix.GREEN, "werewolf.commands.player.scenarios.list"));
        StringBuilder sb = new StringBuilder();

        int i = 0;
        for (Wrapper<ListenerWerewolf, Scenario> scenarioRegister : Register.get().getScenariosRegister()) {
            if (game.getConfig().isScenarioActive(scenarioRegister.getMetaDatas().key())) {
                sb.append(i % 2 == 0 ? "§b" : "")
                        .append(game.translate(scenarioRegister.getMetaDatas().key()))
                        .append("§f, ");
                i++;
            }
        }

        player.sendMessage(sb.toString());
    }
}
