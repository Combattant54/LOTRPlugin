package fr.combattant54.lotrplugin.commands.utilities;

import fr.combattant54.lotrplugin.Register;
import fr.combattant54.lotrapi.annotations.Configuration;
import fr.combattant54.lotrapi.annotations.ConfigurationBasic;
import fr.combattant54.lotrapi.annotations.PlayerCommand;
import fr.combattant54.lotrapi.basekeys.ConfigBase;
import fr.combattant54.lotrapi.basekeys.Prefix;
import fr.combattant54.lotrapi.commands.ICommand;
import fr.combattant54.lotrapi.game.WereWolfAPI;
import fr.combattant54.lotrapi.player.utils.Formatter;
import fr.combattant54.lotrapi.utils.Wrapper;
import org.bukkit.entity.Player;

import java.util.stream.Collectors;
import java.util.stream.Stream;

@PlayerCommand(key = "werewolf.commands.player.configurations_roles.command",
        descriptionKey = "werewolf.commands.player.configurations_roles.description",
        argNumbers = 0)
public class CommandConfigurationsRoles implements ICommand {

    @Override
    public void execute(WereWolfAPI game, Player player, String[] args) {

        if (game.getConfig().isConfigActive(ConfigBase.HIDE_COMPOSITION)) {
            player.sendMessage(game.translate(Prefix.RED, "werewolf.commands.player.compo.composition_hide"));
            return;
        }

        player.sendMessage(game.translate(Prefix.ORANGE, "werewolf.commands.player.configurations_roles.list"));

        String message = Stream.concat(Register.get().getRolesRegister().stream().map(Wrapper::getMetaDatas)
                                .filter(role -> game.getConfig().getRoleCount(role.key()) > 0)
                                .flatMap(role -> Stream.of(role.configurations()))
                                .map(Configuration::config)
                                .filter(ConfigurationBasic::appearInMenu)
                                .filter(ConfigurationBasic::appearInConfigurationList)
                                .filter(configurationWrapper -> game.getConfig().isConfigActive(configurationWrapper.key()))
                                .map(configurationWrapper -> "§a-§f " + game.translate(configurationWrapper.key())),
                        Register.get().getRolesRegister()
                                .stream()
                                .map(Wrapper::getMetaDatas)
                                .filter(role -> game.getConfig().getRoleCount(role.key()) > 0)
                                .flatMap(role -> Stream.of(role.configValues()))
                                .map(intValue -> "§a-§f " + game.translate(intValue.key(),
                                        Formatter.number(game.getConfig().getValue(intValue.key())))))
                .sorted(String::compareToIgnoreCase)
                .collect(Collectors.joining("\n"));

        player.sendMessage(message);
    }
}
