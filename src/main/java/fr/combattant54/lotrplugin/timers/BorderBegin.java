package fr.combattant54.lotrplugin.timers;

import fr.combattant54.lotrapi.annotations.Timer;
import fr.combattant54.lotrapi.basekeys.Prefix;
import fr.combattant54.lotrapi.basekeys.TimerBase;
import fr.combattant54.lotrapi.enums.Sound;
import fr.combattant54.lotrapi.events.game.timers.BorderStartEvent;
import fr.combattant54.lotrapi.game.WereWolfAPI;
import fr.combattant54.lotrapi.listeners.impl.ListenerWerewolf;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;

@Timer(key = TimerBase.BORDER_BEGIN,
        defaultValue = 60 * 60,
        meetUpValue = 30 * 60,
        decrement = true,
        onZero = BorderStartEvent.class)
public class BorderBegin extends ListenerWerewolf {

    public BorderBegin(WereWolfAPI main) {
        super(main);
    }

    @EventHandler
    public void onBorderStart(BorderStartEvent event) {
        Bukkit.getOnlinePlayers()
                .forEach(player -> {
                    player.sendMessage(this.getGame().translate(Prefix.ORANGE, "werewolf.announcement.border"));
                    Sound.FIREWORK_LAUNCH.play(player);
                });
    }
}
