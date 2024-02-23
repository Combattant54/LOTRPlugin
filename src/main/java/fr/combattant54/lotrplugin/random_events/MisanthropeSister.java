package fr.combattant54.lotrplugin.random_events;

import fr.combattant54.lotrapi.annotations.RandomEvent;
import fr.combattant54.lotrapi.basekeys.EventBase;
import fr.combattant54.lotrapi.basekeys.Prefix;
import fr.combattant54.lotrapi.basekeys.RoleBase;
import fr.combattant54.lotrapi.enums.StatePlayer;
import fr.combattant54.lotrapi.events.game.timers.WereWolfListEvent;
import fr.combattant54.lotrapi.events.random_events.MysanthropeSisterEvent;
import fr.combattant54.lotrapi.events.werewolf.AppearInWereWolfListEvent;
import fr.combattant54.lotrapi.events.werewolf.NewWereWolfEvent;
import fr.combattant54.lotrapi.events.werewolf.WereWolfCanSpeakInChatEvent;
import fr.combattant54.lotrapi.game.WereWolfAPI;
import fr.combattant54.lotrapi.listeners.impl.ListenerWerewolf;
import fr.combattant54.lotrapi.player.interfaces.IPlayerWW;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@RandomEvent(key = EventBase.SISTER_MISANTHROPE, loreKey = "werewolf.random_events.sister_misanthrope.description")
public class MisanthropeSister extends ListenerWerewolf {

    @Nullable()
    private IPlayerWW sisterWW;

    public MisanthropeSister(WereWolfAPI main) {
        super(main);
    }

    @EventHandler
    public void onWerewolfList(WereWolfListEvent event) {
        WereWolfAPI game = this.getGame();
        List<IPlayerWW> sisters = game.getPlayersWW()
                .stream()
                .filter(playerWW -> playerWW.isState(StatePlayer.ALIVE))
                .filter(playerWW -> playerWW.getRole().isKey(RoleBase.SISTER))
                .collect(Collectors.toList());

        if (sisters.size() < 2) {
            return;
        }
        Collections.shuffle(sisters, game.getRandom());

        this.sisterWW = sisters.get(0);

        if (this.sisterWW.getRole().isWereWolf()) {
            return;
        }

        MysanthropeSisterEvent mysanthropeSisterEvent = new MysanthropeSisterEvent(this.sisterWW);

        Bukkit.getPluginManager().callEvent(mysanthropeSisterEvent);

        if (mysanthropeSisterEvent.isCancelled()) {
            return;
        }

        this.sisterWW.sendMessageWithKey(Prefix.BLUE, "werewolf.random_events.sister_misanthrope.message");
        this.sisterWW.getRole().setInfected();
        Bukkit.getPluginManager().callEvent(new NewWereWolfEvent(this.sisterWW));
    }

    @EventHandler
    public void onWerewolfChat(WereWolfCanSpeakInChatEvent event) {
        if (event.getPlayerWW().equals(this.sisterWW)) {
            event.setCanSpeak(false);
        }
    }

    @EventHandler
    public void onRequestWerewolfList(AppearInWereWolfListEvent event) {
        if (this.sisterWW != null && event.getPlayerWW().getUUID().equals(this.sisterWW.getUUID())) {
            event.setAppear(false);
        }
    }
}
