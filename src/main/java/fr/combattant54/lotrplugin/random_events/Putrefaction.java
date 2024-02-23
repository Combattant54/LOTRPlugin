package fr.combattant54.lotrplugin.random_events;

import fr.combattant54.lotrapi.annotations.RandomEvent;
import fr.combattant54.lotrapi.annotations.Timer;
import fr.combattant54.lotrapi.basekeys.EventBase;
import fr.combattant54.lotrapi.basekeys.TimerBase;
import fr.combattant54.lotrapi.events.game.timers.RepartitionEvent;
import fr.combattant54.lotrapi.events.random_events.PutrefactionEvent;
import fr.combattant54.lotrapi.game.WereWolfAPI;
import fr.combattant54.lotrapi.listeners.impl.ListenerWerewolf;
import fr.combattant54.lotrapi.utils.BukkitUtils;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerMoveEvent;

import java.util.Objects;

@RandomEvent(key = EventBase.PUTREFACTION, loreKey = "werewolf.random_events.putrefaction.description",
        timers = {@Timer(key = Putrefaction.TIMER_START, defaultValue = 60 * 60, meetUpValue = 30 * 60, step = 30),
                @Timer(key = Putrefaction.PERIOD, defaultValue = 15 * 60, meetUpValue = 10 * 60, step = 30)})
public class Putrefaction extends ListenerWerewolf {

    public static final String TIMER_START = "werewolf.random_events.putrefaction.timer_start";
    public static final String PERIOD = "werewolf.random_events.putrefaction.period";

    private boolean active = false;

    public Putrefaction(WereWolfAPI main) {
        super(main);
    }

    @EventHandler(ignoreCancelled = true)
    public void onRepartition(RepartitionEvent event) {
        WereWolfAPI game = this.getGame();

        BukkitUtils.scheduleSyncDelayedTask(game, () -> {
            if (isRegister()) {

                PutrefactionEvent putrefactionEvent = new PutrefactionEvent();
                Bukkit.getPluginManager().callEvent(putrefactionEvent);

                if (putrefactionEvent.isCancelled()) return;

                active = true;

                Bukkit.broadcastMessage(game.translate("werewolf.random_events.putrefaction.message"));

                BukkitUtils.scheduleSyncDelayedTask(game, () -> {
                    if (isRegister()) {
                        active = false;
                        register(false);
                        Bukkit.broadcastMessage(game.translate("werewolf.random_events.putrefaction.end"));
                    }
                }, game.getConfig().getTimerValue(TimerBase.DAY_DURATION) * 40L);
            }
        }, (long) (20L * game.getConfig().getTimerValue(TIMER_START) +
                game.getRandom().nextDouble() * game.getConfig().getTimerValue(PERIOD) * 20));
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {

        if (!active) return;

        if (Objects.requireNonNull(event.getTo()).getBlock().isLiquid()) {
            event.getPlayer().damage(0.5);
        }
    }
}
