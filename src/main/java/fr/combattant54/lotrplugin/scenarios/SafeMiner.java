package fr.combattant54.lotrplugin.scenarios;

import fr.combattant54.lotrapi.annotations.Scenario;
import fr.combattant54.lotrapi.basekeys.ScenarioBase;
import fr.combattant54.lotrapi.basekeys.TimerBase;
import fr.combattant54.lotrapi.game.WereWolfAPI;
import fr.combattant54.lotrapi.listeners.impl.ListenerWerewolf;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageEvent;

@Scenario(key = ScenarioBase.SAFE_MINER)
public class SafeMiner extends ListenerWerewolf {

    public SafeMiner(WereWolfAPI main) {
        super(main);
    }

    @EventHandler
    public void onDamage(EntityDamageEvent event) {

        if (getGame().getConfig().getTimerValue(TimerBase.DIGGING) <= 0) {
            return;
        }
        if (event.getCause().equals(EntityDamageEvent.DamageCause.ENTITY_ATTACK)) {
            return;
        }

        if (event.getEntity() instanceof Player) {
            int y = event.getEntity().getLocation().getBlockY();
            if (y > 0 && y < 30) {
                event.setCancelled(true);
            }
        }
    }
}
