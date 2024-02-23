package fr.combattant54.lotrplugin.scenarios;

import fr.combattant54.lotrapi.annotations.Scenario;
import fr.combattant54.lotrapi.basekeys.Prefix;
import fr.combattant54.lotrapi.basekeys.ScenarioBase;
import fr.combattant54.lotrapi.enums.Sound;
import fr.combattant54.lotrapi.events.game.timers.RepartitionEvent;
import fr.combattant54.lotrapi.game.WereWolfAPI;
import fr.combattant54.lotrapi.listeners.impl.ListenerWerewolf;
import fr.combattant54.lotrapi.versions.VersionUtils;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;

@Scenario(key = ScenarioBase.FINAL_HEAL, defaultValue = true, meetUpValue = true)
public class FinalHeal extends ListenerWerewolf {

    public FinalHeal(WereWolfAPI main) {
        super(main);
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onRepartition(RepartitionEvent event) {
        Bukkit.getOnlinePlayers().forEach(player1 -> {
            player1.setHealth(VersionUtils.getVersionUtils().getPlayerMaxHealth(player1));
            Sound.NOTE_STICKS.play(player1);
            player1.sendMessage(this.getGame().translate(Prefix.ORANGE, "werewolf.commands.admin.final_heal.send"));
        });
    }
}
