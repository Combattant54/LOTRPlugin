package fr.combattant54.lotrplugin.configs;

import fr.combattant54.lotrapi.annotations.Configuration;
import fr.combattant54.lotrapi.annotations.ConfigurationBasic;
import fr.combattant54.lotrapi.basekeys.ConfigBase;
import fr.combattant54.lotrapi.basekeys.RoleBase;
import fr.combattant54.lotrapi.enums.Camp;
import fr.combattant54.lotrapi.enums.StatePlayer;
import fr.combattant54.lotrapi.events.UpdatePlayerNameTagEvent;
import fr.combattant54.lotrapi.events.game.game_cycle.UpdateCompositionEvent;
import fr.combattant54.lotrapi.events.game.life_cycle.AnnouncementDeathEvent;
import fr.combattant54.lotrapi.events.game.timers.RepartitionEvent;
import fr.combattant54.lotrapi.game.WereWolfAPI;
import fr.combattant54.lotrapi.listeners.impl.ListenerWerewolf;
import fr.combattant54.lotrapi.player.interfaces.IPlayerWW;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Configuration(config = @ConfigurationBasic(key = ConfigBase.HIDDEN_ROLES,
        loreKey = "werewolf.configurations.hidden_roles.description"))
public class HiddenRoles extends ListenerWerewolf {

    @Nullable
    private IPlayerWW playerWW;

    public HiddenRoles(WereWolfAPI main) {
        super(main);
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onRole(RepartitionEvent event) {

        if (this.getGame().getPlayersWW()
                .stream()
                .anyMatch(playerWW1 -> playerWW1.getRole().isKey(RoleBase.PRIESTESS))) {
            return;
        }

        List<IPlayerWW> playerWWs = this.getGame().getPlayersWW()
                .stream()
                .filter(playerWW1 -> playerWW1.isState(StatePlayer.ALIVE))
                .filter(playerWW1 -> playerWW1.getRole().isCamp(Camp.RING_COMMUNITY))
                .collect(Collectors.toList());

        if (playerWWs.isEmpty()) {
            return;
        }

        Collections.shuffle(playerWWs, getGame().getRandom());

        this.playerWW = playerWWs.get(0);
    }

    @EventHandler(priority = EventPriority.HIGH)
    private void sendDeathMessage(AnnouncementDeathEvent event) {

        if (this.playerWW == null) {
            return;
        }

        if (event.getTargetPlayer().equals(this.playerWW)) {
            return; //la prêtresse voit les vrais rôles
        }

        if (event.getTargetPlayer().equals(event.getPlayerWW())) {
            return; //le mort voit son vrai rôle
        }

        IPlayerWW playerWW = event.getTargetPlayer();

        if (playerWW.getRole().isNeutral()) {
            if (this.playerWW.isState(StatePlayer.ALIVE) && getGame().getRandom().nextFloat() > 0.95) {
                event.setRole("werewolf.configurations.hidden_roles.magic");
            }
        } else if (getGame().getRandom().nextFloat() < 0.8) {

            if (this.playerWW.isState(StatePlayer.ALIVE)) {
                if (playerWW.getRole().isWereWolf()) {
                    event.setRole("werewolf.configurations.hidden_roles.magic");
                }

            } else {
                if (!playerWW.getRole().isWereWolf()) {
                    event.setRole("werewolf.configurations.hidden_roles.magic");
                }
            }
        } else {
            if (this.playerWW.isState(StatePlayer.ALIVE)) {
                if (!playerWW.getRole().isWereWolf()) {
                    event.setRole("werewolf.configurations.hidden_roles.magic");
                }
            } else {
                if (playerWW.getRole().isWereWolf()) {
                    event.setRole("werewolf.configurations.hidden_roles.magic");
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onCompositionUpdate(UpdateCompositionEvent event) {
        event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onUpdate(UpdatePlayerNameTagEvent event) {

        IPlayerWW playerWW = this.getGame().getPlayerWW(event.getPlayerUUID()).orElse(null);

        if (playerWW == null) {
            return;
        }

        if (!playerWW.isState(StatePlayer.DEATH)) return;

        event.setSuffix("");
    }
}
