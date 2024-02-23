package fr.combattant54.lotrplugin.configs;

import fr.combattant54.lotrapi.annotations.Configuration;
import fr.combattant54.lotrapi.annotations.ConfigurationBasic;
import fr.combattant54.lotrapi.annotations.Timer;
import fr.combattant54.lotrapi.basekeys.ConfigBase;
import fr.combattant54.lotrapi.basekeys.Prefix;
import fr.combattant54.lotrapi.basekeys.TimerBase;
import fr.combattant54.lotrapi.enums.StatePlayer;
import fr.combattant54.lotrapi.events.game.configs.LoneWolfEvent;
import fr.combattant54.lotrapi.events.game.timers.WereWolfListEvent;
import fr.combattant54.lotrapi.game.WereWolfAPI;
import fr.combattant54.lotrapi.listeners.impl.ListenerWerewolf;
import fr.combattant54.lotrapi.player.interfaces.IPlayerWW;
import fr.combattant54.lotrapi.role.interfaces.IRole;
import fr.combattant54.lotrapi.utils.BukkitUtils;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;

import java.util.List;
import java.util.stream.Collectors;

@Configuration(config = @ConfigurationBasic(key = ConfigBase.LONE_WOLF),
        timers = @Timer(key = TimerBase.LONE_WOLF_DURATION, defaultValue = 60 * 60, meetUpValue = 20 * 60))
public class LoneWolf extends ListenerWerewolf {

    public LoneWolf(WereWolfAPI main) {
        super(main);
    }

    @EventHandler
    public void designAloneWolf(WereWolfListEvent event) {

        WereWolfAPI game = this.getGame();

        BukkitUtils.scheduleSyncDelayedTask(game,
                this::designSolitary,
                (long) (game.getRandom().nextFloat() * game.getConfig().getTimerValue(TimerBase.LONE_WOLF_DURATION) * 20));
    }

    private void designSolitary() {

        WereWolfAPI game = this.getGame();

        List<IRole> roleWWs = game.getPlayersWW().stream()
                .filter(playerWW -> playerWW.isState(StatePlayer.ALIVE))
                .map(IPlayerWW::getRole)
                .filter(IRole::isWereWolf)
                .collect(Collectors.toList());

        if (roleWWs.isEmpty()) return;

        IRole role = roleWWs.get((int) Math.floor(game.getRandom().nextDouble() * roleWWs.size()));

        LoneWolfEvent event = new LoneWolfEvent((role.getPlayerWW()));

        Bukkit.getPluginManager().callEvent(event);

        if (event.isCancelled()) return;

        role.getPlayerWW().sendMessageWithKey(Prefix.RED, "werewolf.configurations.lone_wolf.message");

        if (role.getPlayerWW().getMaxHealth() < 30) {
            role.getPlayerWW().addPlayerMaxHealth(Math.max(0, Math.min(8, 30 - role.getPlayerWW().getMaxHealth())));
        }
        role.setSolitary(true);
        register(false);
    }
}
