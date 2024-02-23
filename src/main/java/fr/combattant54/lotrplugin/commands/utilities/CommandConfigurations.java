package fr.combattant54.lotrplugin.commands.utilities;

import fr.combattant54.lotrplugin.Register;
import fr.combattant54.lotrapi.annotations.Configuration;
import fr.combattant54.lotrapi.annotations.ConfigurationBasic;
import fr.combattant54.lotrapi.annotations.PlayerCommand;
import fr.combattant54.lotrapi.basekeys.ConfigBase;
import fr.combattant54.lotrapi.basekeys.Prefix;
import fr.combattant54.lotrapi.commands.ICommand;
import fr.combattant54.lotrapi.game.WereWolfAPI;
import fr.combattant54.lotrapi.utils.Wrapper;
import org.bukkit.entity.Player;

import java.util.stream.Collectors;

@PlayerCommand(key = "werewolf.commands.player.configurations.command",
        descriptionKey = "werewolf.commands.player.configurations.description",
        argNumbers = 0)
public class CommandConfigurations implements ICommand {

    @Override
    public void execute(WereWolfAPI game, Player player, String[] args) {

        player.sendMessage(game.translate(Prefix.ORANGE, "werewolf.commands.player.configurations.list"));

        String message = Register.get().getConfigsRegister()
                .stream()
                .map(Wrapper::getMetaDatas)
                .map(Configuration::config)
                .filter(ConfigurationBasic::appearInMenu)
                .filter(ConfigurationBasic::appearInConfigurationList)
                .filter(configurationWrapper -> game.getConfig().isConfigActive(configurationWrapper.key()) &&
                                                this.hideCompositionCondition(game, configurationWrapper.key()))
                .map(configurationWrapper -> "§a-§f " + game.translate(configurationWrapper.key()))
                .sorted(String::compareToIgnoreCase)
                .collect(Collectors.joining("\n"));

        player.sendMessage(message);
    }

    private boolean hideCompositionCondition(WereWolfAPI game, String key) {
        return !key.equals(ConfigBase.LONE_WOLF)
               || !game.getConfig().isConfigActive(ConfigBase.HIDE_COMPOSITION);
    }
}
