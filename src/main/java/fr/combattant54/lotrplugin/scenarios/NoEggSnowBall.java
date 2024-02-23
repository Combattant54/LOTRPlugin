package fr.combattant54.lotrplugin.scenarios;

import fr.combattant54.lotrapi.annotations.Scenario;
import fr.combattant54.lotrapi.basekeys.ScenarioBase;
import fr.combattant54.lotrapi.game.WereWolfAPI;
import fr.combattant54.lotrapi.listeners.impl.ListenerWerewolf;
import org.bukkit.entity.Egg;
import org.bukkit.entity.Snowball;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.ProjectileLaunchEvent;

@Scenario(key = ScenarioBase.NO_EGG_SNOWBALL, defaultValue = true, meetUpValue = true)
public class NoEggSnowBall extends ListenerWerewolf {

    public NoEggSnowBall(WereWolfAPI main) {
        super(main);
    }

    @EventHandler
    public void onProjectileThrownEvent(ProjectileLaunchEvent event) {

        if (event.getEntity() instanceof Snowball || event.getEntity() instanceof Egg) {
            event.setCancelled(true);
        }
    }

}
