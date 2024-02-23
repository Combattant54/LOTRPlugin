package fr.combattant54.lotrplugin.timers;

import fr.combattant54.lotrapi.annotations.Timer;
import fr.combattant54.lotrapi.basekeys.Prefix;
import fr.combattant54.lotrapi.basekeys.TimerBase;
import fr.combattant54.lotrapi.enums.Sound;
import fr.combattant54.lotrapi.enums.StatePlayer;
import fr.combattant54.lotrapi.events.UpdateNameTagEvent;
import fr.combattant54.lotrapi.events.game.timers.WereWolfListEvent;
import fr.combattant54.lotrapi.game.WereWolfAPI;
import fr.combattant54.lotrapi.listeners.impl.ListenerWerewolf;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;

@Timer(key = TimerBase.WEREWOLF_LIST,
        defaultValue = 60 * 10,
        meetUpValue = 60 * 5,
        decrementAfterRole = true,
        onZero = WereWolfListEvent.class)
public class WerewolfList extends ListenerWerewolf {

    public WerewolfList(WereWolfAPI main) {
        super(main);
    }

    @EventHandler
    public void onWerewolfList(WereWolfListEvent event) {
        this.getGame().getPlayersWW().stream()
                .filter(playerWW -> !playerWW.isState(StatePlayer.DEATH))
                .filter(playerWW -> playerWW.getRole().isWereWolf())
                .forEach(playerWW -> {
                    playerWW.sendMessageWithKey(Prefix.YELLOW, "werewolf.roles.werewolf.see_others");
                    Sound.WOLF_HOWL.play(playerWW);
                    Bukkit.getPluginManager().callEvent(new UpdateNameTagEvent(playerWW));
                });
    }
}
