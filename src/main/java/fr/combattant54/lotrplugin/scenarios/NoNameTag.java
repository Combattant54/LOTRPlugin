package fr.combattant54.lotrplugin.scenarios;

import fr.combattant54.lotrapi.annotations.Scenario;
import fr.combattant54.lotrapi.basekeys.ScenarioBase;
import fr.combattant54.lotrapi.events.UpdateNameTagEvent;
import fr.combattant54.lotrapi.events.UpdatePlayerNameTagEvent;
import fr.combattant54.lotrapi.game.WereWolfAPI;
import fr.combattant54.lotrapi.listeners.impl.ListenerWerewolf;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;

@Scenario(key = ScenarioBase.NO_NAME_TAG)
public class NoNameTag extends ListenerWerewolf {

    public NoNameTag(WereWolfAPI main) {
        super(main);
    }

    @EventHandler
    public void onUpdateNameTag(UpdatePlayerNameTagEvent event) {
        event.setVisibility(false);
    }

    @Override
    public void register(boolean isActive) {

        super.register(isActive);
        Bukkit.getOnlinePlayers().forEach(player -> Bukkit.getPluginManager().callEvent(
                new UpdateNameTagEvent(player)));
    }
}
