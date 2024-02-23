package fr.combattant54.lotrplugin.commands.admin.ingame;

import fr.combattant54.lotrapi.annotations.AdminCommand;
import fr.combattant54.lotrapi.basekeys.Prefix;
import fr.combattant54.lotrapi.commands.ICommand;
import fr.combattant54.lotrapi.enums.StateGame;
import fr.combattant54.lotrapi.enums.StatePlayer;
import fr.combattant54.lotrapi.game.WereWolfAPI;
import fr.combattant54.lotrapi.player.interfaces.IPlayerWW;
import fr.combattant54.lotrapi.player.utils.Formatter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.UUID;

@AdminCommand(key = "werewolf.commands.admin.tp_group.command",
        descriptionKey = "werewolf.commands.admin.tp_group.description",
        argNumbers = {1, 2},
        moderatorAccess = true,
        statesGame = StateGame.GAME)
public class CommandTPGroup implements ICommand {

    @Override
    public void execute(WereWolfAPI game, Player player, String[] args) {

        Player playerArg = Bukkit.getPlayer(args[0]);
        String playerName = player.getName();

        if (playerArg == null) {
            player.sendMessage(game.translate(Prefix.RED, "werewolf.check.offline_player"));
            return;
        }
        UUID argUUID = playerArg.getUniqueId();
        IPlayerWW playerWW = game.getPlayerWW(argUUID).orElse(null);

        if (playerWW == null) {
            player.sendMessage(game.translate(Prefix.RED, "werewolf.check.player_not_found"));
            return;
        }

        if (!playerWW.isState(StatePlayer.ALIVE)) {
            return;
        }
        int d = 20;
        int size = game.getGroup();
        double r = Math.random() * 2 * Math.PI;

        Location location = playerArg.getLocation();
        StringBuilder sb = new StringBuilder();
        try {
            if (args.length == 2) {
                d = Integer.parseInt(args[1]);
            }
        } catch (NumberFormatException ignored) {
        }
        for (Player p : Bukkit.getOnlinePlayers()) {
            UUID uuid = p.getUniqueId();
            IPlayerWW playerWW1 = game.getPlayerWW(uuid).orElse(null);

            if (size > 0 && playerWW1 != null && playerWW1.isState(StatePlayer.ALIVE)) {
                if (p.getWorld().equals(playerArg.getWorld())) {
                    if (p.getLocation().distance(location) <= d) {
                        size--;
                        sb.append(p.getName()).append(" ");
                        playerWW1.sendMessageWithKey(Prefix.YELLOW, "werewolf.commands.admin.tp_group.perform",
                                Formatter.player(playerName));
                        game.getMapManager().transportation(playerWW1, r);
                    }
                }
            }
        }
        Bukkit.getConsoleSender().sendMessage(game.translate(Prefix.YELLOW, "werewolf.commands.admin.tp_group.broadcast",
                Formatter.format("&players&", sb.toString()),
                Formatter.player(playerName)));
    }
}
