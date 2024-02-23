package fr.combattant54.lotrplugin.scenarios;

import fr.combattant54.lotrapi.annotations.Scenario;
import fr.combattant54.lotrapi.basekeys.ScenarioBase;
import fr.combattant54.lotrapi.game.WereWolfAPI;
import fr.combattant54.lotrapi.listeners.impl.ListenerWerewolf;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.PlayerDeathEvent;


@Scenario(key = ScenarioBase.COMPASS_TARGET_LAST_DEATH,
        incompatibleScenarios = ScenarioBase.COMPASS_MIDDLE)
public class CompassTargetLastDeath extends ListenerWerewolf {

    public CompassTargetLastDeath(WereWolfAPI main) {
        super(main);
    }

    @EventHandler
    private void onPlayerDeath(PlayerDeathEvent event) {

        WereWolfAPI game = this.getGame();

        if (!game.getPlayerWW(event.getEntity().getUniqueId()).isPresent()) return;

        Bukkit.getOnlinePlayers()
                .forEach(player -> player.setCompassTarget(event.getEntity().getLocation()));
    }

}