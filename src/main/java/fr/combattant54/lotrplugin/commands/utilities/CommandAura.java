package fr.combattant54.lotrplugin.commands.utilities;

import fr.combattant54.lotrapi.annotations.PlayerCommand;
import fr.combattant54.lotrapi.basekeys.Prefix;
import fr.combattant54.lotrapi.commands.ICommand;
import fr.combattant54.lotrapi.game.WereWolfAPI;
import org.bukkit.entity.Player;

@PlayerCommand(key = "werewolf.commands.player.aura.command",
        descriptionKey = "werewolf.commands.player.aura.description",
        argNumbers = 0)
public class CommandAura implements ICommand {

    @Override
    public void execute(WereWolfAPI game, Player player, String[] args) {

        player.sendMessage(game.translate(Prefix.BLUE, "werewolf.commands.player.aura.prefix"));
        game.translateArray("werewolf.commands.player.aura.messages").forEach(player::sendMessage);
    }
}
