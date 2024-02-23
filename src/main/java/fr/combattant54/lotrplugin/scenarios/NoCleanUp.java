package fr.combattant54.lotrplugin.scenarios;

import fr.combattant54.lotrapi.annotations.Scenario;
import fr.combattant54.lotrapi.basekeys.ScenarioBase;
import fr.combattant54.lotrapi.game.WereWolfAPI;
import fr.combattant54.lotrapi.listeners.impl.ListenerWerewolf;
import fr.combattant54.lotrapi.versions.VersionUtils;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.PlayerDeathEvent;

@Scenario(key = ScenarioBase.NO_CLEAN_UP)
public class NoCleanUp extends ListenerWerewolf {

    public NoCleanUp(WereWolfAPI main) {
        super(main);
    }

    @EventHandler(priority = EventPriority.LOWEST)
    private void onPlayerDeath(PlayerDeathEvent event) {

        Player killer = event.getEntity().getKiller();
        if (killer == null) return;
        killer.setHealth(Math.min(killer.getHealth() + 4,
                VersionUtils.getVersionUtils().getPlayerMaxHealth(killer)));
    }
}
