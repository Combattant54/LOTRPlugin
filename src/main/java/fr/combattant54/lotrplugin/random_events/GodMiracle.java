package fr.combattant54.lotrplugin.random_events;

import fr.combattant54.lotrapi.annotations.RandomEvent;
import fr.combattant54.lotrapi.basekeys.EventBase;
import fr.combattant54.lotrapi.events.game.life_cycle.ThirdDeathEvent;
import fr.combattant54.lotrapi.events.random_events.GodMiracleEvent;
import fr.combattant54.lotrapi.game.WereWolfAPI;
import fr.combattant54.lotrapi.listeners.impl.ListenerWerewolf;
import fr.combattant54.lotrapi.player.interfaces.IPlayerWW;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;

@RandomEvent(key = EventBase.GOD_MIRACLE, loreKey = "werewolf.random_events.god_miracle.description")
public class GodMiracle extends ListenerWerewolf {

    public GodMiracle(WereWolfAPI main) {
        super(main);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onDeath(ThirdDeathEvent event) {

        WereWolfAPI game = this.getGame();

        if (event.isCancelled()) return;

        if (game.getRandom().nextFloat() * 5 < 1) {

            IPlayerWW playerWW = event.getPlayerWW();
            GodMiracleEvent godMiracle = new GodMiracleEvent(playerWW);
            Bukkit.getPluginManager().callEvent(godMiracle);

            if (godMiracle.isCancelled()) return;

            event.setCancelled(true);
            register(false);
            game.resurrection(playerWW);
            Bukkit.broadcastMessage(game.translate("werewolf.random_events.god_miracle.message"));
        }
    }

}
