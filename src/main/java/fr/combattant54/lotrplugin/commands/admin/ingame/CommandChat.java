package fr.combattant54.lotrplugin.commands.admin.ingame;

import fr.combattant54.lotrapi.annotations.AdminCommand;
import fr.combattant54.lotrapi.basekeys.ConfigBase;
import fr.combattant54.lotrapi.basekeys.Prefix;
import fr.combattant54.lotrapi.commands.ICommand;
import fr.combattant54.lotrapi.game.WereWolfAPI;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

@AdminCommand(key = "werewolf.commands.admin.chat.command",
        descriptionKey = "werewolf.commands.admin.chat.description",
        moderatorAccess = true,
        argNumbers = 0)
public class CommandChat implements ICommand {

    @Override
    public void execute(WereWolfAPI game, Player player, String[] args) {

        game.getConfig().switchConfigValue(ConfigBase.CHAT);

        Bukkit.broadcastMessage(game.getConfig().isConfigActive(ConfigBase.CHAT) ?
                game.translate(Prefix.GREEN, "werewolf.commands.admin.chat.on") :
                game.translate(Prefix.RED, "werewolf.commands.admin.chat.off"));
    }
}
