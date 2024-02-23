package fr.combattant54.lotrplugin.random_events;

import fr.combattant54.lotrapi.annotations.RandomEvent;
import fr.combattant54.lotrapi.basekeys.EventBase;
import fr.combattant54.lotrapi.events.game.timers.WereWolfListEvent;
import fr.combattant54.lotrapi.events.random_events.IncompleteListEvent;
import fr.combattant54.lotrapi.events.werewolf.AppearInWereWolfListEvent;
import fr.combattant54.lotrapi.events.werewolf.RequestSeeWereWolfListEvent;
import fr.combattant54.lotrapi.game.WereWolfAPI;
import fr.combattant54.lotrapi.listeners.impl.ListenerWerewolf;
import fr.combattant54.lotrapi.player.interfaces.IPlayerWW;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

@RandomEvent(key = EventBase.INCOMPLETE_LIST, loreKey = "werewolf.random_events.incomplete_list.description")
public class IncompleteList extends ListenerWerewolf {

    private final Map<IPlayerWW, List<IPlayerWW>> forgetWerewolves = new HashMap<>();

    public IncompleteList(WereWolfAPI game) {
        super(game);
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onWerewolfList(WereWolfListEvent event) {

        IncompleteListEvent incompleteListEvent = new IncompleteListEvent();

        Bukkit.getPluginManager().callEvent(incompleteListEvent);

        if (incompleteListEvent.isCancelled()) {
            return;
        }

        for (IPlayerWW playerWW1 : this.getGame().getPlayersWW()) {

            RequestSeeWereWolfListEvent requestSeeWereWolfListEvent = new RequestSeeWereWolfListEvent(playerWW1);
            Bukkit.getPluginManager().callEvent(requestSeeWereWolfListEvent);

            if (requestSeeWereWolfListEvent.isAccept()) {

                this.forgetWerewolves.put(playerWW1, new ArrayList<>());

                for (IPlayerWW playerWW2 : this.getGame().getPlayersWW()) {

                    AppearInWereWolfListEvent appearInWereWolfListEvent =
                            new AppearInWereWolfListEvent(playerWW2, playerWW1);
                    Bukkit.getPluginManager().callEvent(appearInWereWolfListEvent);

                    if (appearInWereWolfListEvent.isAppear()) {
                        this.forgetWerewolves.get(playerWW1).add(playerWW2);
                    }
                }
                Collections.shuffle(this.forgetWerewolves.get(playerWW1), this.getGame().getRandom());
                AtomicInteger werewolfSize = new AtomicInteger((this.forgetWerewolves.get(playerWW1).size() - 1) / 2);
                //Remove half of werewolves
                this.forgetWerewolves.get(playerWW1).removeIf(playerWW -> {
                    if (playerWW.equals(playerWW1)) {
                        return true;
                    }
                    return werewolfSize.getAndDecrement() > 0;
                });
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onAppearInWerewolfListEvent(AppearInWereWolfListEvent event) {

        this.getGame().getPlayerWW(event.getPlayerWW().getUUID())
                .ifPresent(playerWW1 -> this.getGame().getPlayerWW(event.getTargetWW().getUUID())
                        .ifPresent(playerWW2 -> {
                            if (this.forgetWerewolves.containsKey(playerWW1) &&
                                    this.forgetWerewolves.get(playerWW1).contains(playerWW2)) {
                                event.setAppear(false);
                            }
                        }));
    }
}
