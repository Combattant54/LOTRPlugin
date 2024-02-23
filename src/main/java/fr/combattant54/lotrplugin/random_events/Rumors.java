package fr.combattant54.lotrplugin.random_events;

import fr.combattant54.lotrapi.annotations.RandomEvent;
import fr.combattant54.lotrapi.annotations.Timer;
import fr.combattant54.lotrapi.basekeys.EventBase;
import fr.combattant54.lotrapi.events.game.timers.RepartitionEvent;
import fr.combattant54.lotrapi.events.random_events.RumorsEvent;
import fr.combattant54.lotrapi.events.random_events.RumorsWriteEvent;
import fr.combattant54.lotrapi.game.WereWolfAPI;
import fr.combattant54.lotrapi.listeners.impl.ListenerWerewolf;
import fr.combattant54.lotrapi.player.interfaces.IPlayerWW;
import fr.combattant54.lotrapi.player.utils.Formatter;
import fr.combattant54.lotrapi.utils.BukkitUtils;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RandomEvent(key = EventBase.RUMORS, loreKey = "werewolf.random_events.rumors.description",
        timers = {@Timer(key = Rumors.TIMER_START, defaultValue = 80 * 60, meetUpValue = 30 * 60, step = 30),
                @Timer(key = Rumors.PERIOD, defaultValue = 40 * 60, meetUpValue = 20 * 60, step = 30)})
public class Rumors extends ListenerWerewolf {

    public static final String TIMER_START = "werewolf.random_events.rumors.timer_start";
    public static final String PERIOD = "werewolf.random_events.rumors.period";
    private final Map<IPlayerWW, String> rumors = new HashMap<>();
    private boolean active = false;

    public Rumors(WereWolfAPI main) {
        super(main);
    }

    @EventHandler(ignoreCancelled = true)
    public void onRepartition(RepartitionEvent event) {
        WereWolfAPI game = this.getGame();

        BukkitUtils.scheduleSyncDelayedTask(game, () -> {
            if (isRegister()) {
                RumorsEvent rumorEvent = new RumorsEvent();
                Bukkit.getPluginManager().callEvent(rumorEvent);

                if (rumorEvent.isCancelled()) return;

                active = true;

                TextComponent textComponent = new TextComponent(
                        game.translate(
                                "werewolf.random_events.rumors.message"));
                textComponent.setClickEvent(
                        new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND,
                                String.format("/ww %s",
                                        game.translate("werewolf.random_events.rumors.command"))));
                Bukkit.getOnlinePlayers().forEach(player -> player.spigot().sendMessage(textComponent));

                BukkitUtils.scheduleSyncDelayedTask(game, () -> {
                    if (isRegister()) {
                        active = false;
                        register(false);
                        List<String> rumors = new ArrayList<>(this.rumors.values());

                        if (rumors.size() == 0) {
                            return;
                        }

                        Collections.shuffle(rumors, game.getRandom());

                        Bukkit.broadcastMessage(game.translate("werewolf.random_events.rumors.rumors_announcement",
                                Formatter.format("&rumors&", String.join("\n", rumors))));
                    }
                }, 20L * 60);
            }
        }, (long) (20L * game.getConfig().getTimerValue(TIMER_START) +
                game.getRandom().nextDouble() * 20 * game.getConfig().getTimerValue(PERIOD)));
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerWriteMessage(RumorsWriteEvent event) {

        if (!active) return;

        if (event.isCancelled()) {
            return;
        }

        this.rumors.put(event.getPlayerWW(), event.getMessage());

    }
}
