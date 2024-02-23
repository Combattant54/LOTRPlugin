package fr.combattant54.lotrplugin.listeners;

import fr.combattant54.lotrapi.enums.StateGame;
import fr.combattant54.lotrapi.events.ActionBarEvent;
import fr.combattant54.lotrapi.game.WereWolfAPI;
import fr.combattant54.lotrapi.player.utils.Formatter;
import fr.combattant54.lotrapi.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import java.text.DecimalFormat;

public class ActionBarListener implements Listener {

    private final WereWolfAPI game;

    public ActionBarListener(WereWolfAPI game) {
        this.game = game;
    }


    @EventHandler(priority = EventPriority.LOW)
    public void onActionBarEvent(ActionBarEvent event) {

        if (game.isState(StateGame.LOBBY)) return;

        if (game.isState(StateGame.TRANSPORTATION)) return;

        Player player = Bukkit.getPlayer(event.getPlayerUUID());

        if (player == null) return;

        int d = Utils.midDistance(player);
        event.setActionBar(event.getActionBar() + game.translate("werewolf.action_bar.in_game",
                Formatter.format("&min&", d),
                Formatter.format("&max&", d + 300),
                Formatter.format("&height&", (int) Math.floor(player.getLocation().getY()))));
    }

    @EventHandler
    public void onActionBarEventLobby(ActionBarEvent event) {

        if (!game.isState(StateGame.LOBBY)) return;

        Player player = Bukkit.getPlayer(event.getPlayerUUID());

        if (player == null) return;

        if (game.getMapManager().getPercentageGenerated() == 0) {

            if (game.getModerationManager()
                    .checkAccessAdminCommand(
                            "werewolf.commands.admin.generation.command",
                            player,
                            false)) {
                event.setActionBar(event.getActionBar() +
                        game.translate("werewolf.action_bar.generation"));
            }

            return;
        }


        if (game.getMapManager().getPercentageGenerated() < 100) {
            event.setActionBar(event.getActionBar() +
                    game.translate("werewolf.action_bar.progress",
                            Formatter.format("&progress&", new DecimalFormat("0.0")
                                    .format(game.getMapManager()
                                            .getPercentageGenerated()))));

            return;
        }

        event.setActionBar(event.getActionBar() +
                game.translate("werewolf.action_bar.complete"));


    }
}
