package fr.combattant54.lotrplugin.commands.admin.ingame;

import fr.combattant54.lotrplugin.guis.MainGUI;
import fr.combattant54.lotrapi.annotations.AdminCommand;
import fr.combattant54.lotrapi.commands.ICommand;
import fr.combattant54.lotrapi.game.WereWolfAPI;
import org.bukkit.entity.Player;

@AdminCommand(key = "werewolf.commands.admin.config.command",
        descriptionKey = "werewolf.commands.admin.config.description",
        moderatorAccess = true,
        argNumbers = 0)
public class CommandConfig implements ICommand {

    @Override
    public void execute(WereWolfAPI game, Player player, String[] args) {
        MainGUI.INVENTORY.open(player);
    }
}
