package fr.combattant54.lotrplugin.scenarios;

import fr.combattant54.lotrapi.annotations.Scenario;
import fr.combattant54.lotrapi.basekeys.ScenarioBase;
import fr.combattant54.lotrapi.game.WereWolfAPI;
import fr.combattant54.lotrapi.listeners.impl.ListenerWerewolf;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerTeleportEvent;

@Scenario(key = ScenarioBase.NO_END, defaultValue = true, meetUpValue = true)
public class NoEnd extends ListenerWerewolf {

    public NoEnd(WereWolfAPI main) {
        super(main);
    }

    @EventHandler
    private void onPlayerTeleport(PlayerTeleportEvent event) {

        if (event.getCause().equals(PlayerTeleportEvent.TeleportCause.END_PORTAL)) {
            event.setCancelled(true);
        }
    }
}
