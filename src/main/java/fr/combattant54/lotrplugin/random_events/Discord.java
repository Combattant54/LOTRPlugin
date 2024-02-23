package fr.combattant54.lotrplugin.random_events;

import com.google.common.collect.Sets;
import fr.combattant54.lotrapi.annotations.RandomEvent;
import fr.combattant54.lotrapi.annotations.Timer;
import fr.combattant54.lotrapi.basekeys.EventBase;
import fr.combattant54.lotrapi.basekeys.Prefix;
import fr.combattant54.lotrapi.enums.Camp;
import fr.combattant54.lotrapi.enums.StatePlayer;
import fr.combattant54.lotrapi.events.game.timers.RepartitionEvent;
import fr.combattant54.lotrapi.events.random_events.DiscordEvent;
import fr.combattant54.lotrapi.events.werewolf.NewWereWolfEvent;
import fr.combattant54.lotrapi.game.WereWolfAPI;
import fr.combattant54.lotrapi.listeners.impl.ListenerWerewolf;
import fr.combattant54.lotrapi.player.interfaces.IPlayerWW;
import fr.combattant54.lotrapi.role.interfaces.ICamp;
import fr.combattant54.lotrapi.role.interfaces.IRole;
import fr.combattant54.lotrapi.utils.BukkitUtils;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;

import java.util.List;
import java.util.stream.Collectors;

@RandomEvent(key = EventBase.DISCORD, loreKey = "werewolf.random_events.discord.description",
        timers = {@Timer(key = Discord.TIMER_START, defaultValue = 55 * 60, meetUpValue = 20 * 60, step = 30),
                @Timer(key = Discord.PERIOD, defaultValue = 30 * 60, meetUpValue = 15 * 60, step = 30)})
public class Discord extends ListenerWerewolf {

    public static final String TIMER_START = "werewolf.random_events.discord.timer_start";
    public static final String PERIOD = "werewolf.random_events.discord.period";

    public Discord(WereWolfAPI main) {
        super(main);
    }

    @EventHandler(ignoreCancelled = true)
    public void onRepartition(RepartitionEvent event) {
        WereWolfAPI game = this.getGame();

        BukkitUtils.scheduleSyncDelayedTask(game, () -> {
            if (isRegister()) {

                List<IPlayerWW> playerWWsWerewolf = game.getPlayersWW().stream()
                        .filter(playerWW -> playerWW.isState(StatePlayer.ALIVE))
                        .map(IPlayerWW::getRole)
                        .filter(ICamp::isWereWolf)
                        .map(IRole::getPlayerWW)
                        .collect(Collectors.toList());

                if (playerWWsWerewolf.isEmpty()) return;

                IPlayerWW werewolf = playerWWsWerewolf
                        .get((int) Math.floor(game.getRandom().nextDouble() * playerWWsWerewolf.size()));

                List<IPlayerWW> playerWWsNeutral = game.getPlayersWW().stream()
                        .filter(playerWW -> playerWW.isState(StatePlayer.ALIVE))
                        .filter(playerWW -> !playerWW.equals(werewolf))
                        .map(IPlayerWW::getRole)
                        .filter(ICamp::isNeutral)
                        .filter(iRole -> !iRole.isWereWolf())
                        .map(IRole::getPlayerWW)
                        .collect(Collectors.toList());

                if (playerWWsNeutral.isEmpty()) return;

                IPlayerWW neutral = playerWWsNeutral
                        .get((int) Math.floor(game.getRandom().nextDouble() * playerWWsNeutral.size()));


                List<IPlayerWW> playerWWsVillager = game.getPlayersWW().stream()
                        .filter(playerWW -> playerWW.isState(StatePlayer.ALIVE))
                        .filter(playerWW -> !playerWW.equals(werewolf))
                        .filter(playerWW -> !playerWW.equals(neutral))
                        .map(IPlayerWW::getRole)
                        .filter(iRole -> iRole.isCamp(Camp.RING_COMMUNITY))
                        .map(IRole::getPlayerWW)
                        .collect(Collectors.toList());

                if (playerWWsVillager.isEmpty()) return;

                IPlayerWW villager = playerWWsVillager
                        .get((int) Math.floor(game.getRandom().nextDouble() * playerWWsVillager.size()));

                DiscordEvent discordEvent = new DiscordEvent(Sets.newHashSet(werewolf, neutral, villager));
                Bukkit.getPluginManager().callEvent(discordEvent);

                if (discordEvent.isCancelled()) return;

                werewolf.getRole().setSolitary(true);
                werewolf.sendMessageWithKey(Prefix.RED, "werewolf.configurations.lone_wolf.message");

                if (werewolf.getMaxHealth() < 30) {
                    werewolf.addPlayerMaxHealth(Math.min(8, 30 - werewolf.getMaxHealth()));
                }
                neutral.getRole().setTransformedToVillager(true);
                if (neutral.getRole().isCamp(Camp.RING_COMMUNITY)) {
                    neutral.sendMessageWithKey(Prefix.RED, "werewolf.random_events.discord.to_villager");
                }

                villager.getRole().setInfected();
                Bukkit.getPluginManager().callEvent(
                        new NewWereWolfEvent(villager));

                register(false);

                Bukkit.broadcastMessage(game.translate("werewolf.random_events.discord.message"));
            }
        }, (long) (20L * game.getConfig().getTimerValue(TIMER_START) + game.getRandom().nextDouble() * 20 * game.getConfig().getTimerValue(PERIOD)));
    }
}
