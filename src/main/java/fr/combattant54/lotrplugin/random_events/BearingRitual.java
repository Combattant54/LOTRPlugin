package fr.combattant54.lotrplugin.random_events;

import fr.combattant54.lotrapi.annotations.RandomEvent;
import fr.combattant54.lotrapi.annotations.Timer;
import fr.combattant54.lotrapi.basekeys.EventBase;
import fr.combattant54.lotrapi.basekeys.TimerBase;
import fr.combattant54.lotrapi.events.game.timers.RepartitionEvent;
import fr.combattant54.lotrapi.events.random_events.BearingRitualEvent;
import fr.combattant54.lotrapi.game.WereWolfAPI;
import fr.combattant54.lotrapi.listeners.impl.ListenerWerewolf;
import fr.combattant54.lotrapi.utils.BukkitUtils;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageEvent;

@RandomEvent(key = EventBase.BEARING_RITUAL, loreKey = "werewolf.random_events.bearing_ritual.description",
        timers = {@Timer(key = BearingRitual.TIMER_START, defaultValue = 60 * 60, meetUpValue = 30 * 60, step = 30),
                @Timer(key = BearingRitual.PERIOD, defaultValue = 40 * 60, meetUpValue = 20 * 60, step = 30)})
public class BearingRitual extends ListenerWerewolf {

    public static final String TIMER_START = "werewolf.random_events.bearing_ritual.timer_start";
    public static final String PERIOD = "werewolf.random_events.bearing_ritual.period";

    private boolean active = false;

    public BearingRitual(WereWolfAPI api) {
        super(api);
    }

    @EventHandler(ignoreCancelled = true)
    public void onRepartition(RepartitionEvent event) {
        WereWolfAPI game = this.getGame();

        BukkitUtils.scheduleSyncDelayedTask(game, () -> {
            if (isRegister()) {
                BearingRitualEvent bearingRitualEvent = new BearingRitualEvent();
                Bukkit.getPluginManager().callEvent(bearingRitualEvent);

                if (bearingRitualEvent.isCancelled()) return;

                active = true;

                Bukkit.broadcastMessage(game.translate("werewolf.random_events.bearing_ritual.message"));

                BukkitUtils.scheduleSyncDelayedTask(game, () -> {
                    if (isRegister()) {
                        active = false;
                        register(false);
                        Bukkit.broadcastMessage(game.translate("werewolf.random_events.bearing_ritual.end"));
                    }
                }, game.getConfig().getTimerValue(TimerBase.DAY_DURATION) * 40L);
            }
        }, (long) (20L * game.getConfig().getTimerValue(TIMER_START) + game.getRandom().nextDouble() * 15 * game.getConfig().getTimerValue(PERIOD)));
    }

    @EventHandler
    public void onPlayerDamage(EntityDamageEvent event) {

        if (!active) return;

        if (!event.getCause().equals(EntityDamageEvent.DamageCause.FALL)) return;

        event.setCancelled(true);
    }
}
