package fr.combattant54.lotrplugin.random_events;

import fr.combattant54.lotrapi.annotations.RandomEvent;
import fr.combattant54.lotrapi.basekeys.EventBase;
import fr.combattant54.lotrapi.basekeys.Prefix;
import fr.combattant54.lotrapi.basekeys.RoleBase;
import fr.combattant54.lotrapi.basekeys.TimerBase;
import fr.combattant54.lotrapi.enums.StatePlayer;
import fr.combattant54.lotrapi.events.game.timers.RepartitionEvent;
import fr.combattant54.lotrapi.events.random_events.SwapEvent;
import fr.combattant54.lotrapi.game.WereWolfAPI;
import fr.combattant54.lotrapi.listeners.impl.ListenerWerewolf;
import fr.combattant54.lotrapi.player.interfaces.IPlayerWW;
import fr.combattant54.lotrapi.role.interfaces.IRole;
import fr.combattant54.lotrapi.utils.BukkitUtils;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;

import java.util.List;
import java.util.stream.Collectors;

@RandomEvent(key = EventBase.SWAP, loreKey = "werewolf.random_events.swap.description")
public class Swap extends ListenerWerewolf {

    public Swap(WereWolfAPI main) {
        super(main);
    }

    @EventHandler(ignoreCancelled = true)
    public void onRepartition(RepartitionEvent event) {
        WereWolfAPI game = this.getGame();

        if (game.getConfig().getTimerValue(TimerBase.WEREWOLF_LIST) <= 1) {
            return;
        }

        int timer = Math.min(
                game.getConfig().getTimerValue(TimerBase.WEREWOLF_LIST),
                game.getConfig().getTimerValue(TimerBase.LOVER_DURATION)
        );

        if (timer <= 0) {
            return;
        }

        BukkitUtils.scheduleSyncDelayedTask(game, () -> {
            if (isRegister()) {

                List<IPlayerWW> playerWWS = game.getPlayersWW().stream()
                        .filter(playerWW -> playerWW.isState(StatePlayer.ALIVE))
                        .collect(Collectors.toList());

                if (playerWWS.isEmpty()) return;

                IPlayerWW playerWW1 = playerWWS.get((int) Math.floor(game.getRandom().nextDouble() * playerWWS.size()));

                playerWWS = game.getPlayersWW().stream()
                        .filter(playerWW -> playerWW.isState(StatePlayer.ALIVE))
                        .filter(playerWW -> !playerWW.equals(playerWW1))
                        .collect(Collectors.toList());

                if (playerWWS.isEmpty()) return;

                IPlayerWW playerWW2 = playerWWS.get((int) Math.floor(game.getRandom().nextDouble() * playerWWS.size()));

                SwapEvent swapEvent = new SwapEvent(playerWW1, playerWW2);
                Bukkit.getPluginManager().callEvent(swapEvent);

                if (swapEvent.isCancelled()) return;

                IRole roles1 = playerWW1.getRole();
                IRole roles2 = playerWW2.getRole();
                playerWW1.setRole(roles2);
                playerWW2.setRole(roles1);
                Bukkit.broadcastMessage(game.translate("werewolf.random_events.swap.message"));
                register(false);
                playerWW1.addPlayerMaxHealth(20 - playerWW1.getMaxHealth());
                playerWW2.addPlayerMaxHealth(20 - playerWW2.getMaxHealth());
                playerWW1.clearPotionEffects(roles1.getKey());
                playerWW2.clearPotionEffects(roles2.getKey());
                playerWW1.clearPotionEffects(RoleBase.WEREWOLF);
                playerWW2.clearPotionEffects(RoleBase.WEREWOLF);
                playerWW1.sendMessageWithKey(Prefix.RED, "werewolf.random_events.swap.concerned");
                playerWW2.sendMessageWithKey(Prefix.RED, "werewolf.random_events.swap.concerned");
                roles1.recoverPower();
                roles2.recoverPower();
                roles1.recoverPotionEffects();
                roles2.recoverPotionEffects();

            }
        }, Math.max(0, (long) (game.getRandom().nextDouble() * timer * 20) - 5));
    }

}
