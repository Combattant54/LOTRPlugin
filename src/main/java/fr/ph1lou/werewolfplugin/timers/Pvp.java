package fr.ph1lou.werewolfplugin.timers;

import fr.ph1lou.werewolfapi.annotations.Timer;
import fr.ph1lou.werewolfapi.basekeys.Prefix;
import fr.ph1lou.werewolfapi.basekeys.TimerBase;
import fr.ph1lou.werewolfapi.enums.Sound;
import fr.ph1lou.werewolfapi.events.game.timers.PVPEvent;
import fr.ph1lou.werewolfapi.game.WereWolfAPI;
import fr.ph1lou.werewolfapi.listeners.impl.ListenerWerewolf;
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
