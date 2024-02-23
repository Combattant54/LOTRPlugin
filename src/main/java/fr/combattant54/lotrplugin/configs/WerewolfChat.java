package fr.combattant54.lotrplugin.configs;

import fr.combattant54.lotrapi.annotations.Configuration;
import fr.combattant54.lotrapi.annotations.ConfigurationBasic;
import fr.combattant54.lotrapi.annotations.IntValue;
import fr.combattant54.lotrapi.annotations.Timer;
import fr.combattant54.lotrapi.basekeys.ConfigBase;
import fr.combattant54.lotrapi.basekeys.IntValueBase;
import fr.combattant54.lotrapi.basekeys.TimerBase;
import fr.combattant54.lotrapi.enums.StateGame;
import fr.combattant54.lotrapi.enums.UniversalMaterial;
import fr.combattant54.lotrapi.events.game.day_cycle.DayEvent;
import fr.combattant54.lotrapi.events.game.day_cycle.NightEvent;
import fr.combattant54.lotrapi.game.WereWolfAPI;
import fr.combattant54.lotrapi.listeners.impl.ListenerWerewolf;
import fr.combattant54.lotrapi.utils.BukkitUtils;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;

@Configuration(config = @ConfigurationBasic(key = ConfigBase.WEREWOLF_CHAT,
        defaultValue = true,
        meetUpValue = true),
        timers = @Timer(key = TimerBase.WEREWOLF_CHAT_DURATION,
                defaultValue = 30,
                meetUpValue = 30),
        configValues = @IntValue(key = WerewolfChat.CONFIG,
                defaultValue = 1, meetUpValue = 1, step = 1, item = UniversalMaterial.BOOK))
public class WerewolfChat extends ListenerWerewolf {

    public static final String CONFIG = IntValueBase.WEREWOLF_CHAT;

    public WerewolfChat(WereWolfAPI game) {
        super(game);
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onDay(DayEvent event) {

        WereWolfAPI game = this.getGame();

        if (game.isState(StateGame.END)) return;

        BukkitUtils.scheduleSyncDelayedTask(game, () -> {
            Bukkit.getPluginManager().callEvent(new NightEvent(event.getNumber()));
            game.getWerewolfChatHandler().enableWereWolfChat();
            BukkitUtils.scheduleSyncDelayedTask(game, () -> game.getWerewolfChatHandler().disableWereWolfChat(),
                    game.getConfig().getTimerValue(TimerBase.WEREWOLF_CHAT_DURATION) * 20L);

        }, game.getConfig().getTimerValue(TimerBase.DAY_DURATION) * 20L);
    }
}
