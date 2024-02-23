package fr.combattant54.lotrplugin.configs;

import fr.combattant54.lotrapi.annotations.Configuration;
import fr.combattant54.lotrapi.annotations.ConfigurationBasic;
import fr.combattant54.lotrapi.basekeys.ConfigBase;
import fr.combattant54.lotrapi.basekeys.Prefix;
import fr.combattant54.lotrapi.enums.Day;
import fr.combattant54.lotrapi.enums.Sound;
import fr.combattant54.lotrapi.events.UpdateNameTagEvent;
import fr.combattant54.lotrapi.events.UpdatePlayerNameTagEvent;
import fr.combattant54.lotrapi.events.game.day_cycle.DayEvent;
import fr.combattant54.lotrapi.events.game.life_cycle.AnnouncementDeathEvent;
import fr.combattant54.lotrapi.game.WereWolfAPI;
import fr.combattant54.lotrapi.listeners.impl.ListenerWerewolf;
import fr.combattant54.lotrapi.player.interfaces.IPlayerWW;
import fr.combattant54.lotrapi.player.utils.Formatter;
import org.apache.commons.lang.ArrayUtils;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Configuration(config = @ConfigurationBasic(key = ConfigBase.THIERCELIEUX,
        loreKey = "werewolf.configurations.thiercelieux.description"))
public class Thiercelieux extends ListenerWerewolf {

    private final List<AnnouncementDeathEvent> announcementDeathEvents = new ArrayList<>();
    private final Set<IPlayerWW> playerWWList = new HashSet<>();

    public Thiercelieux(WereWolfAPI main) {
        super(main);
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onAnnouncement(AnnouncementDeathEvent event) {

        if (getGame().isDay(Day.DAY)) {
            return;
        }

        event.setCancelled(true);
        this.announcementDeathEvents.add(0, event);
        this.playerWWList.add(event.getPlayerWW());
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onUpdate(UpdatePlayerNameTagEvent event) {

        if (getGame().isDay(Day.DAY)) {
            return;
        }

        if (this.playerWWList.stream()
                .noneMatch(playerWW -> playerWW.getUUID().equals(event.getPlayerUUID()))) {
            return;
        }

        event.setTabVisibility(false);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onDay(DayEvent event) {

        this.playerWWList.clear();

        this.announcementDeathEvents.forEach(announcementDeathEvent -> {
            Formatter[] formatters = (Formatter[]) ArrayUtils.addAll(announcementDeathEvent.getFormatters().toArray(new Formatter[0]),
                    new Formatter[]{Formatter.player(announcementDeathEvent.getPlayerName()),
                            Formatter.role(this.getGame().translate(announcementDeathEvent.getRole()))});

            announcementDeathEvent.getTargetPlayer().sendMessageWithKey("werewolf.utils.bar");
            announcementDeathEvent.getTargetPlayer().sendMessageWithKey(Prefix.RED, announcementDeathEvent.getFormat(), formatters);
            announcementDeathEvent.getTargetPlayer().sendMessageWithKey("werewolf.utils.bar");
            announcementDeathEvent.getTargetPlayer().sendSound(Sound.AMBIENCE_THUNDER);

            Bukkit.getPluginManager().callEvent(new UpdateNameTagEvent(announcementDeathEvent.getPlayerWW()));
        });

        this.announcementDeathEvents.clear();
        this.playerWWList.clear();

    }
}
