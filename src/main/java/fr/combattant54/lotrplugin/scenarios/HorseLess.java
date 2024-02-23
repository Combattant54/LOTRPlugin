package fr.combattant54.lotrplugin.scenarios;

import fr.combattant54.lotrapi.annotations.Scenario;
import fr.combattant54.lotrapi.basekeys.ScenarioBase;
import fr.combattant54.lotrapi.game.WereWolfAPI;
import fr.combattant54.lotrapi.listeners.impl.ListenerWerewolf;
import org.bukkit.entity.Horse;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.spigotmc.event.entity.EntityMountEvent;

@Scenario(key = ScenarioBase.HORSE_LESS, defaultValue = true, meetUpValue = true)
public class HorseLess extends ListenerWerewolf {

    public HorseLess(WereWolfAPI main) {
        super(main);
    }

    @EventHandler
    public void onEntityMount(EntityMountEvent event) {

        if (event.getEntity() instanceof Player) {
            if (event.getMount() instanceof Horse) {
                event.setCancelled(true);
            }
        }
    }
}
