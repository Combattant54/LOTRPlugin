package fr.combattant54.lotrplugin.timers;

import fr.combattant54.lotrapi.annotations.Role;
import fr.combattant54.lotrapi.annotations.Timer;
import fr.combattant54.lotrapi.basekeys.ConfigBase;
import fr.combattant54.lotrapi.basekeys.Prefix;
import fr.combattant54.lotrapi.basekeys.RoleBase;
import fr.combattant54.lotrapi.basekeys.TimerBase;
import fr.combattant54.lotrapi.enums.Sound;
import fr.combattant54.lotrapi.enums.StateGame;
import fr.combattant54.lotrapi.events.TrollEvent;
import fr.combattant54.lotrapi.events.UpdateNameTagEvent;
import fr.combattant54.lotrapi.events.game.timers.RepartitionEvent;
import fr.combattant54.lotrapi.game.WereWolfAPI;
import fr.combattant54.lotrapi.listeners.impl.ListenerWerewolf;
import fr.combattant54.lotrapi.player.interfaces.IPlayerWW;
import fr.combattant54.lotrapi.player.utils.Formatter;
import fr.combattant54.lotrapi.role.interfaces.IRole;
import fr.combattant54.lotrapi.utils.BukkitUtils;
import fr.combattant54.lotrapi.utils.Wrapper;
import fr.combattant54.lotrplugin.Register;
import fr.combattant54.lotrplugin.game.GameManager;
import fr.combattant54.lotrplugin.roles.ringcommunity.Soldier;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.ItemStack;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Timer(key = TimerBase.ROLE_DURATION,
        defaultValue = 20 * 60,
        meetUpValue = 60,
        decrement = true,
        onZero = RepartitionEvent.class)
public class RoleDuration extends ListenerWerewolf {


    public RoleDuration(WereWolfAPI main) {
        super(main);
    }

    private void roleAnnouncement(IPlayerWW playerWW) {

        IRole iRole = playerWW.getRole();
        Sound.EXPLODE.play(playerWW);
        playerWW.sendMessageWithKey("lotr.description.description_message",
                Formatter.format("&description&", iRole.getDescription()));
        playerWW.sendMessageWithKey(Prefix.YELLOW, "lotr.announcement.review_role");

        iRole.recoverPotionEffects();
        iRole.recoverPower();

        if (this.getGame().getConfig().isConfigActive(ConfigBase.TROLL_ROLE)) return;

        for (ItemStack i : getGame().getStuffs().getStuffRole(iRole.getKey())) {
            playerWW.addItem(i);
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onRepartition(RepartitionEvent event) {

        GameManager game = (GameManager) this.getGame();

        game.setState(StateGame.GAME);

        List<IPlayerWW> playerWWS = new ArrayList<>(game.getPlayersWW());
        List<Wrapper<IRole, Role>> config = new ArrayList<>();

        game.getConfig().setConfig(ConfigBase.CHAT, false);

        game.getConfig().setRole(RoleBase.VILLAGER,
                Math.max(0,
                        game.getConfig()
                                .getRoleCount(RoleBase.VILLAGER) +
                                playerWWS.size() -
                                game.getTotalRoles()));

        Register.get().getRolesRegister()
                .forEach(roleRegister -> {
                    for (int i = 0; i < game.getConfig().getRoleCount(roleRegister.getMetaDatas().key()); i++) {
                        config.add(roleRegister);
                    }
                });

        Collections.shuffle(playerWWS, game.getRandom());

        while (!playerWWS.isEmpty()) {
            IPlayerWW playerWW = playerWWS.remove(0);
            Wrapper<IRole, Role> roleRegister = config.remove(0);
            IRole role;
            try {
                role = roleRegister.getClazz().getConstructor(WereWolfAPI.class,
                        IPlayerWW.class).newInstance(game,
                        playerWW);
            } catch (IllegalAccessException | InstantiationException | InvocationTargetException |
                     NoSuchMethodException exception) {
                exception.printStackTrace();
                role = new Soldier(game, playerWW);
            }
            BukkitUtils.registerListener(role);
            playerWW.setRole(role);
            Bukkit.getPluginManager().callEvent(new UpdateNameTagEvent(playerWW));
        }
        game.getPlayersWW()
                .forEach(this::roleAnnouncement);

        BukkitUtils.scheduleSyncDelayedTask(game, game::checkVictory);
    }


    @EventHandler(priority = EventPriority.LOWEST)
    public void onRepartitionFirst(RepartitionEvent event) {
        if (this.getGame().getConfig().isConfigActive(ConfigBase.TROLL_ROLE)) {
            Bukkit.getPluginManager().callEvent(new TrollEvent(this.getGame().getConfig().getTrollKey()));
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onTroll(TrollEvent event) {

        GameManager game = (GameManager) this.getGame();

        game.setState(StateGame.GAME);
        game.getConfig().setConfig(ConfigBase.CHAT, false);

        Register.get().getRolesRegister()
                .forEach(roleRegister -> {

                    if (roleRegister.getMetaDatas().key().equals(event.getTrollKey())) {

                        game.getPlayersWW()
                                .forEach(playerWW -> {
                                    IRole role;
                                    try {
                                        role = roleRegister.getClazz().getConstructor(WereWolfAPI.class,
                                                        IPlayerWW.class)
                                                .newInstance(game,
                                                        playerWW);
                                    } catch (InstantiationException | InvocationTargetException |
                                             IllegalAccessException | NoSuchMethodException exception) {
                                        exception.printStackTrace();
                                        role = new Soldier(game, playerWW);
                                    }
                                    BukkitUtils.registerListener(role);
                                    playerWW.setRole(role);
                                    Bukkit.getPluginManager().callEvent(new UpdateNameTagEvent(playerWW));
                                });
                    }
                });


        game.getPlayersWW()
                .forEach(this::roleAnnouncement);

        BukkitUtils.scheduleSyncDelayedTask(game, () -> {

            game.getPlayersWW()
                    .forEach(playerWW -> {
                        HandlerList.unregisterAll(playerWW.getRole());
                        Sound.PORTAL_TRIGGER.play(playerWW);
                        playerWW.clearPotionEffects();
                        playerWW.sendMessageWithKey(Prefix.RED, "werewolf.announcement.troll");
                        playerWW.addPlayerMaxHealth(20 - playerWW.getMaxHealth());
                    });;


            if (game.getConfig().isConfigActive(ConfigBase.DOUBLE_TROLL)) {
                Bukkit.getPluginManager().callEvent(new TrollEvent(game.getConfig().getTrollKey()));
                game.getConfig().switchConfigValue(ConfigBase.DOUBLE_TROLL);
                game.setDebug(false);
            } else {
                game.getConfig().setConfig(ConfigBase.TROLL_ROLE, false);
                Bukkit.getPluginManager().callEvent(new RepartitionEvent());
            }

        }, 1800L);
    }

}
