package fr.combattant54.lotrplugin.random_events;

import fr.combattant54.lotrapi.annotations.IntValue;
import fr.combattant54.lotrapi.annotations.RandomEvent;
import fr.combattant54.lotrapi.annotations.Timer;
import fr.combattant54.lotrapi.basekeys.EventBase;
import fr.combattant54.lotrapi.basekeys.Prefix;
import fr.combattant54.lotrapi.enums.Category;
import fr.combattant54.lotrapi.enums.StatePlayer;
import fr.combattant54.lotrapi.enums.UniversalMaterial;
import fr.combattant54.lotrapi.events.game.timers.RepartitionEvent;
import fr.combattant54.lotrapi.events.random_events.WolfFleasEvent;
import fr.combattant54.lotrapi.game.WereWolfAPI;
import fr.combattant54.lotrapi.listeners.impl.ListenerWerewolf;
import fr.combattant54.lotrapi.player.interfaces.IPlayerWW;
import fr.combattant54.lotrapi.role.interfaces.IRole;
import fr.combattant54.lotrplugin.Register;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RandomEvent(key = EventBase.WOLF_FLEAS, loreKey = "werewolf.random_events.wolf_fleas.description",
        configValues = @IntValue(key = WolfFleas.DISTANCE, defaultValue = 30, meetUpValue = 30, step = 1, item = UniversalMaterial.COMPASS),
        timers = {
                @Timer(key = WolfFleas.TIMER_START, defaultValue = 50 * 60, meetUpValue = 10 * 60, step = 30),
                @Timer(key = WolfFleas.PERIOD, defaultValue = 100 * 60, meetUpValue = 40 * 60, step = 30),
                @Timer(key = WolfFleas.DURATION, defaultValue = 3 * 60, meetUpValue = 3 * 60, step = 10)})
public class WolfFleas extends ListenerWerewolf {
    public static final String TIMER_START = "werewolf.random_events.wolf_fleas.timer_start";
    public static final String PERIOD = "werewolf.random_events.wolf_fleas.period";
    public static final String DURATION = "werewolf.random_events.wolf_fleas.duration";
    public static final String DISTANCE = "werewolf.random_events.wolf_fleas.distance";

    private final Map<IPlayerWW, Integer> fleas = new HashMap<>();
    private final Map<IPlayerWW, Integer> contamination = new HashMap<>();

    public WolfFleas(WereWolfAPI game) {
        super(game);
    }

    @Override
    public void second() {

        if (this.fleas.isEmpty()) return;

        this.fleas.keySet().stream()
                .filter(p -> this.fleas.get(p) == this.getGame().getTimer())
                .forEach(p -> p.sendMessageWithKey(Prefix.ORANGE, "werewolf.random_events.wolf_fleas.werewolf_message"));

        this.getGame().getPlayersWW().stream()
                .filter(playerWW -> playerWW.isState(StatePlayer.ALIVE))
                .filter(playerWW -> !this.isDefaultWereWolf(playerWW))
                .filter(playerWW -> this.fleas.keySet().stream()
                        .filter(ww -> ww.isState(StatePlayer.ALIVE))
                        .filter(this::hasFleas)
                        .anyMatch(ww -> ww.getLocation().getWorld() == playerWW.getLocation().getWorld() &&
                                ww.getLocation().distance(playerWW.getLocation()) <= this.getGame().getConfig().getValue(WolfFleas.DISTANCE))
                )
                .forEach(p -> {
                    this.contamination.put(p, this.contamination.getOrDefault(p, 0) + 1);

                    if (this.contamination.get(p) == this.getGame().getConfig().getTimerValue(WolfFleas.DURATION)) {
                        p.sendMessageWithKey(Prefix.RED, "werewolf.random_events.wolf_fleas.target_message");
                        this.contamination.remove(p);
                    }
                });

        new HashSet<>(this.fleas.keySet()).stream()
                .filter(this::hasFleas)
                .filter(p -> {
                    Player player = Bukkit.getPlayer(p.getUUID());
                    return player != null &&
                            player.getInventory().getHelmet() == null
                            && player.getInventory().getChestplate() == null
                            && player.getInventory().getLeggings() == null
                            && player.getInventory().getBoots() == null;
                })
                .filter(p -> p.getEyeLocation().getBlock().getType() == Material.WATER)
                .forEach(p -> {
                    p.sendMessageWithKey(Prefix.GREEN, "werewolf.random_events.wolf_fleas.cleaned");
                    this.fleas.remove(p);
                });
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onRepartition(RepartitionEvent event) {
        List<IPlayerWW> defaultWereWolfs = this.getGame()
                .getPlayersWW().stream()
                .filter(this::isDefaultWereWolf)
                .collect(Collectors.toList());

        if (defaultWereWolfs.isEmpty()) return;

        WolfFleasEvent wolfFleasEvent = new WolfFleasEvent();

        Bukkit.getPluginManager().callEvent(wolfFleasEvent);

        if (wolfFleasEvent.isCancelled()) {
            return;
        }

        int start = this.getGame().getConfig().getTimerValue(WolfFleas.TIMER_START);
        int period = this.getGame().getConfig().getTimerValue(WolfFleas.PERIOD);

        defaultWereWolfs.forEach(p -> this.fleas.put(p, start + this.getGame().getRandom().nextInt(period + 1)));
    }

    private boolean hasFleas(IPlayerWW playerWW) {
        if (!this.fleas.containsKey(playerWW)) return false;

        return this.getGame().getTimer() >= this.fleas.get(playerWW);
    }

    private boolean isDefaultWereWolf(IPlayerWW playerWW) {
        return this.isDefaultWereWolf(playerWW.getRole());
    }

    private boolean isDefaultWereWolf(IRole role) {
        return this.isDefaultWereWolf(role.getKey());
    }

    private boolean isDefaultWereWolf(String key) {
        return Register.get().getRolesRegister()
                .stream().filter(r -> r.getMetaDatas().key().equalsIgnoreCase(key))
                .anyMatch(r -> r.getMetaDatas().category() == Category.DARK_SIDE);
    }
}