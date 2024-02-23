package fr.combattant54.lotrplugin.listeners;

import fr.combattant54.lotrplugin.game.PlayerWW;
import fr.combattant54.lotrapi.enums.StateGame;
import fr.combattant54.lotrapi.game.WereWolfAPI;
import fr.combattant54.lotrapi.utils.BukkitUtils;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.projectiles.ProjectileSource;

public class DamagesListener implements Listener {

    private final WereWolfAPI game;

    public DamagesListener(WereWolfAPI game) {
        this.game = game;
    }

    @EventHandler
    private void onPlayerDamageByPlayer(EntityDamageByEntityEvent event) {

        if (!game.isState(StateGame.GAME)) {
            return;
        }

        if (!(event.getEntity() instanceof Player)) return;

        Player player = (Player) event.getEntity();

        Player striker;


        if (!(event.getDamager() instanceof Player)) {

            if (!(event.getDamager() instanceof Arrow)) return;

            ProjectileSource shooter = ((Arrow) event.getDamager()).getShooter();

            if (!(shooter instanceof Player)) return;

            striker = (Player) shooter;
        } else {
            striker = (Player) event.getDamager();
        }

        game.getPlayerWW(player.getUniqueId())
                .ifPresent(playerWW -> game.getPlayerWW(striker.getUniqueId())
                        .ifPresent(strikerWW -> {
                            ((PlayerWW) playerWW).addLastMinutesDamagedPlayer(strikerWW);
                            BukkitUtils.scheduleSyncDelayedTask(game, () -> ((PlayerWW) playerWW).removeLastMinutesDamagedPlayer(strikerWW), 60 * 20);
                        }));
    }

}
