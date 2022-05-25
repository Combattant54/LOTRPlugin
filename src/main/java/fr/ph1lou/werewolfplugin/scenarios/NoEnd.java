package fr.ph1lou.werewolfplugin.scenarios;

import fr.ph1lou.werewolfapi.GetWereWolfAPI;
import fr.ph1lou.werewolfapi.annotations.Scenario;
import fr.ph1lou.werewolfapi.basekeys.ScenarioBase;
import fr.ph1lou.werewolfapi.listeners.impl.ListenerManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerTeleportEvent;

@Scenario(key = ScenarioBase.NO_END, defaultValue = true, meetUpValue = true)
public class NoEnd extends ListenerManager {

    public NoEnd(GetWereWolfAPI main) {
        super(main);
    }

    @EventHandler
    private void onPlayerTeleport(PlayerTeleportEvent event) {

        if (event.getCause().equals(PlayerTeleportEvent.TeleportCause.END_PORTAL)) {
            event.setCancelled(true);
        }
    }
}
