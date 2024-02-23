package fr.combattant54.lotrplugin.timers;

import fr.combattant54.lotrapi.annotations.Timer;
import fr.combattant54.lotrapi.basekeys.Prefix;
import fr.combattant54.lotrapi.basekeys.TimerBase;
import fr.combattant54.lotrapi.enums.Sound;
import fr.combattant54.lotrapi.events.game.timers.PVPEvent;
import fr.combattant54.lotrapi.game.WereWolfAPI;
import fr.combattant54.lotrapi.listeners.impl.ListenerWerewolf;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;

@Timer(key = TimerBase.PVP,
        defaultValue = 25 * 60,
        meetUpValue = 3 * 60,
        decrement = true,
        onZero = PVPEvent.class)
public class Pvp extends ListenerWerewolf {


    public Pvp(WereWolfAPI main) {
        super(main);
    }

    @EventHandler
    public void onPvp(PVPEvent event) {
        this.getGame().getMapManager().getWorld().setPVP(true);
        Bukkit.getOnlinePlayers()
                .forEach(player -> {
                    player.sendMessage(this.getGame()
                            .translate(Prefix.ORANGE, "werewolf.announcement.pvp"));
                    Sound.DONKEY_ANGRY.play(player);
                });
    }
}
