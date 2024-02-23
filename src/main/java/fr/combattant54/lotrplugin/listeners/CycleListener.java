package fr.combattant54.lotrplugin.listeners;

import fr.combattant54.lotrplugin.statistiks.StatistiksUtils;
import fr.combattant54.lotrapi.basekeys.Prefix;
import fr.combattant54.lotrapi.basekeys.TimerBase;
import fr.combattant54.lotrapi.enums.Day;
import fr.combattant54.lotrapi.enums.StateGame;
import fr.combattant54.lotrapi.events.game.day_cycle.DayEvent;
import fr.combattant54.lotrapi.events.game.day_cycle.DayWillComeEvent;
import fr.combattant54.lotrapi.events.game.day_cycle.NightEvent;
import fr.combattant54.lotrapi.events.roles.SelectionEndEvent;
import fr.combattant54.lotrapi.game.WereWolfAPI;
import fr.combattant54.lotrapi.player.utils.Formatter;
import fr.combattant54.lotrapi.utils.BukkitUtils;
import fr.combattant54.lotrapi.versions.VersionUtils;
import fr.combattant54.lotrplugin.game.GameManager;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;


public class CycleListener implements Listener {

    private final GameManager game;

    public CycleListener(WereWolfAPI game) {
        this.game = (GameManager) game;
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onDay(DayEvent event) {

        game.setDay(Day.DAY);

        if (game.isState(StateGame.END)) return;

        game.getMapManager().getWorld().setTime(23500);

        Bukkit.broadcastMessage(game.translate(Prefix.ORANGE, "werewolf.announcement.day",
                Formatter.number(event.getNumber())));
        groupSizeChange();


        long duration = game.getConfig().getTimerValue(TimerBase.POWER_DURATION);

        if (2L * game.getConfig().getTimerValue(TimerBase.DAY_DURATION)
                - duration > 0) {

            BukkitUtils.scheduleSyncDelayedTask(game, () -> Bukkit.getPluginManager().callEvent(new SelectionEndEvent()), duration * 20);

        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onNight(NightEvent event) {

        long duration = game.getConfig().getTimerValue(TimerBase.DAY_DURATION)
                - 30;
        game.setDay(Day.NIGHT);

        if (game.isState(StateGame.END)) return;

        if (event.getNumber() % 2 == 0) {
            BukkitUtils.scheduleSyncDelayedTask(game, () -> {
                String message = StatistiksUtils.getMessage();
                if (!message.isEmpty()) {
                    Bukkit.broadcastMessage(message);
                }
            }, game.getConfig().getTimerValue(TimerBase.DAY_DURATION) * 10L);
        }

        game.getMapManager().getWorld().setTime(12000);

        Bukkit.broadcastMessage(game.translate(Prefix.YELLOW, "werewolf.announcement.night",
                Formatter.number(event.getNumber())));
        groupSizeChange();

        if (duration > 0) {
            BukkitUtils.scheduleSyncDelayedTask(game, () -> Bukkit.getPluginManager().callEvent(new DayWillComeEvent()), duration * 20);
        }

        BukkitUtils.scheduleSyncDelayedTask(game, () -> Bukkit.getPluginManager().callEvent(new DayEvent(event.getNumber() + 1)), (duration + 30) * 20);
    }

    public void groupSizeChange() {

        if (game.getPlayersCount() <= game.getGroup() * 3 && game.getGroup() > 3) {
            game.setGroup(game.getGroup() - 1);

            Bukkit.getOnlinePlayers()
                    .forEach(player -> {
                        player.sendMessage(
                                game.translate(
                                        Prefix.ORANGE, "werewolf.commands.admin.group.group_change",
                                        Formatter.number(game.getGroup())));
                        VersionUtils.getVersionUtils().sendTitle(
                                player,
                                game.translate("werewolf.commands.admin.group.top_title"),
                                game.translate("werewolf.commands.admin.group.bot_title",
                                        Formatter.number(game.getGroup())),
                                20,
                                60,
                                20);
                    });
        }
    }
}
