package fr.combattant54.lotrplugin.random_events;

import fr.combattant54.lotrapi.annotations.RandomEvent;
import fr.combattant54.lotrapi.annotations.Timer;
import fr.combattant54.lotrapi.basekeys.EventBase;
import fr.combattant54.lotrapi.basekeys.Prefix;
import fr.combattant54.lotrapi.enums.Sound;
import fr.combattant54.lotrapi.enums.StatePlayer;
import fr.combattant54.lotrapi.events.UpdateNameTagEvent;
import fr.combattant54.lotrapi.events.game.timers.WereWolfListEvent;
import fr.combattant54.lotrapi.events.random_events.AmnesicEvent;
import fr.combattant54.lotrapi.events.random_events.AmnesicTransformEvent;
import fr.combattant54.lotrapi.events.werewolf.AppearInWereWolfListEvent;
import fr.combattant54.lotrapi.game.WereWolfAPI;
import fr.combattant54.lotrapi.listeners.impl.ListenerWerewolf;
import fr.combattant54.lotrapi.player.interfaces.IPlayerWW;
import fr.combattant54.lotrapi.player.utils.Formatter;
import fr.combattant54.lotrapi.utils.BukkitUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RandomEvent(key = EventBase.AMNESIC,
        loreKey = "werewolf.random_events.amnesic.description",
        timers = @Timer(key = Amnesic.TIMER, defaultValue = 300, meetUpValue = 180, step = 30))
public class Amnesic extends ListenerWerewolf {

    public final static String TIMER = "werewolf.random_events.amnesic.timer";
    private final List<UUID> list = new ArrayList<>();
    private IPlayerWW temp;

    public Amnesic(WereWolfAPI main) {
        super(main);
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onWereWolfList(WereWolfListEvent event) {

        if (this.temp != null) {
            return;
        }

        WereWolfAPI game = this.getGame();

        List<IPlayerWW> playerWWS = game.getPlayersWW().stream()
                .filter(playerWW -> playerWW.isState(StatePlayer.ALIVE))
                .filter(playerWW -> !playerWW.getRole().isWereWolf())
                .collect(Collectors.toList());

        if (playerWWS.isEmpty()) return;

        this.temp = playerWWS.get((int) Math.floor(game.getRandom().nextDouble() * playerWWS.size()));

        AmnesicEvent event1 = new AmnesicEvent(this.temp);

        Bukkit.getPluginManager().callEvent(event1);

        if (event1.isCancelled()) {
            this.temp = null;
            return;
        }

        this.list.add(this.temp.getUUID());
    }


    @EventHandler
    public void onDamageByWereWolf(EntityDamageByEntityEvent event) {

        if (temp == null) return;

        if (temp.getRole().isWereWolf()) {
            return;
        }

        WereWolfAPI game = this.getGame();

        if (!(event.getEntity() instanceof Player)) {
            return;
        }

        if (!(event.getDamager() instanceof Player)) {
            return;
        }

        if (!temp.getUUID().equals(event.getEntity().getUniqueId())) {
            return;
        }

        IPlayerWW playerWW = game.getPlayerWW(event.getDamager().getUniqueId()).orElse(null);

        if (playerWW == null) {
            return;
        }

        if (!playerWW.getRole().isWereWolf()) {
            return;
        }

        AmnesicTransformEvent event1 = new AmnesicTransformEvent(this.temp, playerWW);

        Bukkit.getPluginManager().callEvent(event1);

        if (event1.isCancelled()) {
            return;
        }

        this.temp.getRole().setInfected();
        this.temp.sendMessageWithKey("werewolf.random_events.amnesic.message",
                Formatter.timer(game, TIMER));

        Bukkit.getPluginManager().callEvent(new UpdateNameTagEvent((Player) event.getEntity()));

        BukkitUtils.scheduleSyncDelayedTask(game, this::revealWereWolf, 20L * game.getConfig().getTimerValue(TIMER));
    }


    private void revealWereWolf() {

        if (this.temp == null) return;

        WereWolfAPI game = this.getGame();

        List<UUID> playerWWS = game.getPlayersWW().stream()
                .filter(playerWW -> playerWW.isState(StatePlayer.ALIVE))
                .filter(playerWW -> playerWW.getRole().isWereWolf())
                .map(IPlayerWW::getUUID)
                .filter(uuid -> !this.list.contains(uuid))
                .collect(Collectors.toList());

        if (playerWWS.isEmpty()) return;

        Collections.shuffle(playerWWS, game.getRandom());

        UUID uuid = playerWWS.get(0);
        IPlayerWW playerWW = game.getPlayerWW(uuid).orElse(null);

        if (playerWW == null) return;

        this.list.add(uuid);

        Player player = Bukkit.getPlayer(this.temp.getUUID());

        if (player == null) return;

        Bukkit.getPluginManager().callEvent(new UpdateNameTagEvent(player));

        playerWW.sendMessageWithKey(Prefix.ORANGE, "werewolf.roles.werewolf.new_werewolf");
        Sound.WOLF_HOWL.play(playerWW);

        this.temp.sendMessageWithKey(Prefix.GREEN, "werewolf.random_events.amnesic.new",
                Formatter.player(playerWW.getName()),
                Formatter.timer(game, TIMER));

        BukkitUtils.scheduleSyncDelayedTask(game, this::revealWereWolf, 20L * game.getConfig().getTimerValue(TIMER));

    }


    @EventHandler(priority = EventPriority.HIGHEST)
    public void onAppearInWereWolfList(AppearInWereWolfListEvent event) {

        if (this.temp == null) return;

        if (event.getPlayerWW().getUUID().equals(this.temp.getUUID())) {
            event.setAppear(this.list.contains(event.getPlayerWW().getUUID()));
            return;
        }

        if (!event.getPlayerWW().getUUID().equals(this.temp.getUUID())) {
            return;
        }

        event.setAppear(this.list.contains(event.getTargetWW().getUUID()));
    }
}
