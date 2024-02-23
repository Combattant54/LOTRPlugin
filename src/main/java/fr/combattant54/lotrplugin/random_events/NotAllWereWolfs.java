package fr.combattant54.lotrplugin.random_events;

import fr.combattant54.lotrapi.annotations.RandomEvent;
import fr.combattant54.lotrapi.basekeys.ConfigBase;
import fr.combattant54.lotrapi.basekeys.EventBase;
import fr.combattant54.lotrapi.basekeys.RoleBase;
import fr.combattant54.lotrapi.enums.Camp;
import fr.combattant54.lotrapi.enums.Category;
import fr.combattant54.lotrapi.enums.StatePlayer;
import fr.combattant54.lotrapi.enums.UpdateCompositionReason;
import fr.combattant54.lotrapi.events.UpdatePlayerNameTagEvent;
import fr.combattant54.lotrapi.events.game.game_cycle.UpdateCompositionEvent;
import fr.combattant54.lotrapi.events.game.life_cycle.AnnouncementDeathEvent;
import fr.combattant54.lotrapi.events.game.life_cycle.FinalDeathEvent;
import fr.combattant54.lotrapi.events.game.timers.RepartitionEvent;
import fr.combattant54.lotrapi.events.random_events.NotAllWerewolfEvent;
import fr.combattant54.lotrapi.game.WereWolfAPI;
import fr.combattant54.lotrapi.listeners.impl.ListenerWerewolf;
import fr.combattant54.lotrapi.player.interfaces.IPlayerWW;
import fr.combattant54.lotrapi.role.interfaces.IRole;
import fr.combattant54.lotrplugin.Register;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;

import java.util.List;
import java.util.stream.Collectors;

@RandomEvent(key = EventBase.NOT_ALL_WEREWOLFS, loreKey = "werewolf.random_events.not_all_werewolfs.description")
public class NotAllWereWolfs extends ListenerWerewolf {

    public NotAllWereWolfs(WereWolfAPI game) {
        super(game);
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onRoles(RepartitionEvent event) {

        NotAllWerewolfEvent notAllWerewolfEvent = new NotAllWerewolfEvent();

        Bukkit.getPluginManager().callEvent(notAllWerewolfEvent);

        if (notAllWerewolfEvent.isCancelled()) {
            return;
        }

        List<IRole> defaultWereWolfs = this.getGame().getPlayersWW().stream()
                .filter(this::isDefaultWereWolf)
                .map(IPlayerWW::getRole)
                .collect(Collectors.toList());

        defaultWereWolfs.forEach(r -> this.getGame().getConfig().removeOneRole(r.getKey()));
        defaultWereWolfs.forEach(r -> this.getGame().getConfig().addOneRole(RoleBase.WEREWOLF));
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onDeathAnnounce(AnnouncementDeathEvent event) {
        if (!this.isDefaultWereWolf(event.getRole())) return;

        event.setRole(RoleBase.WEREWOLF);
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onAnnounceDeath(FinalDeathEvent event) {
        if (!this.isDefaultWereWolf(event.getPlayerWW())) return;
        this.getGame().getConfig().removeOneRole(RoleBase.WEREWOLF);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onCompositionUpdate(UpdateCompositionEvent event) {
        if (!this.isDefaultWereWolf(event.getKey())) return;
        if (event.getReason() != UpdateCompositionReason.DEATH) return;
        event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onUpdatePlayerNameTag(UpdatePlayerNameTagEvent event) {
        IPlayerWW playerWW = this.getGame().getPlayerWW(event.getPlayerUUID()).orElse(null);

        if (playerWW == null) return;

        if (!playerWW.isState(StatePlayer.DEATH)) return;

        if (!this.isDefaultWereWolf(playerWW)) return;

        if (this.getGame().getConfig().isConfigActive(ConfigBase.SHOW_ROLE_TO_DEATH)) {
            event.setSuffix(event.getSuffix()
                    .replace(this.getGame().translate(playerWW.getRole().getKey()),
                            "")
                    + this.getGame().translate(RoleBase.WEREWOLF));
        } else if (this.getGame().getConfig().isConfigActive(ConfigBase.SHOW_ROLE_CATEGORY_TO_DEATH)) {
            event.setSuffix(event.getSuffix()
                    .replace(this.getGame().translate(playerWW.getRole().getCamp().getKey()),
                            "")
                    + this.getGame().translate(Camp.DARK_SIDE.getKey()));
        }
    }

    private boolean isDefaultWereWolf(IPlayerWW playerWW) {
        return this.isDefaultWereWolf(playerWW.getRole());
    }

    private boolean isDefaultWereWolf(IRole role) {
        return this.isDefaultWereWolf(role.getKey());
    }

    private boolean isDefaultWereWolf(String key) {
        return Register.get().getRolesRegister().stream().filter(r -> r.getMetaDatas().key().equalsIgnoreCase(key)).anyMatch(r -> r.getMetaDatas().category() == Category.DARK_SIDE);
    }
}
