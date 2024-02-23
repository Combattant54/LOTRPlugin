package fr.combattant54.lotrplugin.commands.admin.ingame;

import fr.combattant54.lotrplugin.game.GameManager;
import fr.combattant54.lotrapi.annotations.AdminCommand;
import fr.combattant54.lotrapi.basekeys.Prefix;
import fr.combattant54.lotrapi.commands.ICommand;
import fr.combattant54.lotrapi.enums.StateGame;
import fr.combattant54.lotrapi.events.UpdateNameTagEvent;
import fr.combattant54.lotrapi.events.game.permissions.HostEvent;
import fr.combattant54.lotrapi.game.IModerationManager;
import fr.combattant54.lotrapi.game.WereWolfAPI;
import fr.combattant54.lotrapi.player.utils.Formatter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.UUID;

@AdminCommand(key = "werewolf.commands.admin.host.command",
        descriptionKey = "werewolf.commands.admin.host.description",
        argNumbers = 1)
public class CommandHost implements ICommand {

    @Override
    public void execute(WereWolfAPI game, Player player, String[] args) {

        IModerationManager moderationManager = game.getModerationManager();
        Player host = Bukkit.getPlayer(args[0]);

        if (host == null) {
            player.sendMessage(game.translate(Prefix.RED, "werewolf.check.offline_player"));
            return;
        }

        UUID uuid = host.getUniqueId();

        if (moderationManager.getHosts().contains(uuid)) {

            if (moderationManager.getHosts().size() == 1) {
                player.sendMessage(game.translate(Prefix.RED, "werewolf.commands.admin.host.one"));
                return;
            }
            Bukkit.broadcastMessage(game.translate(Prefix.RED, "werewolf.commands.admin.host.remove",
                    Formatter.player(host.getName())));
            moderationManager.getHosts().remove(uuid);

        } else {
            if (game.isState(StateGame.LOBBY) && !game.getPlayerWW(uuid).isPresent() &&
                    !game.getModerationManager().getModerators().contains(uuid)) {
                ((GameManager) game).finalJoin(host);
            }
            moderationManager.addHost(uuid);
            Bukkit.broadcastMessage(game.translate(Prefix.GREEN, "werewolf.commands.admin.host.add",
                    Formatter.player(host.getName())));
        }
        Bukkit.getPluginManager().callEvent(new HostEvent(uuid, moderationManager.getHosts().contains(uuid)));
        Bukkit.getPluginManager().callEvent(new UpdateNameTagEvent(host));
    }
}
