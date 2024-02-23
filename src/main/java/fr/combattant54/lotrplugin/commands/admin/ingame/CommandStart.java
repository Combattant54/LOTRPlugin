package fr.combattant54.lotrplugin.commands.admin.ingame;

import fr.combattant54.lotrplugin.game.GameManager;
import fr.combattant54.lotrplugin.save.ConfigurationLoader;
import fr.combattant54.lotrplugin.save.StuffLoader;
import fr.combattant54.lotrapi.annotations.AdminCommand;
import fr.combattant54.lotrapi.basekeys.Prefix;
import fr.combattant54.lotrapi.commands.ICommand;
import fr.combattant54.lotrapi.enums.StateGame;
import fr.combattant54.lotrapi.events.game.game_cycle.StartEvent;
import fr.combattant54.lotrapi.game.WereWolfAPI;
import fr.combattant54.lotrapi.player.utils.Formatter;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.WorldBorder;
import org.bukkit.entity.Player;

import java.text.DecimalFormat;

@AdminCommand(key = "werewolf.commands.admin.start.command",
        descriptionKey = "werewolf.commands.admin.start.description",
        statesGame = StateGame.LOBBY,
        argNumbers = 0)
public class CommandStart implements ICommand {

    @Override
    public void execute(WereWolfAPI game, Player player, String[] args) {

        if (game.getTotalRoles() - game.getPlayersCount() > 0) {
            player.sendMessage(
                    game.translate(Prefix.RED, "werewolf.commands.admin.start.too_much_role"));
            return;
        }

        if (game.getMapManager().getPercentageGenerated() < 100) {
            player.sendMessage(
                    game.translate(Prefix.RED, "werewolf.commands.admin.start.generation_not_finished",
                            Formatter.format("&progress&", new DecimalFormat("0.0")
                                    .format(game.getMapManager().getPercentageGenerated()))));
            return;
        }

        World world = game.getMapManager().getWorld();
        WorldBorder wb = world.getWorldBorder();
        wb.setCenter(world.getSpawnLocation().getX(), world.getSpawnLocation().getZ());
        wb.setSize(game.getConfig().getBorderMax());
        wb.setWarningDistance((int) (wb.getSize() / 7));
        ((GameManager) game).setState(StateGame.TRANSPORTATION);
        ConfigurationLoader.saveConfig(game, "saveCurrent");
        StuffLoader.saveStuff(game, "saveCurrent");
        Bukkit.getPluginManager().callEvent(new StartEvent(game));
    }
}
