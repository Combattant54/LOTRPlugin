package fr.combattant54.lotrplugin.scenarios;

import fr.combattant54.lotrapi.annotations.IntValue;
import fr.combattant54.lotrapi.annotations.Scenario;
import fr.combattant54.lotrapi.basekeys.IntValueBase;
import fr.combattant54.lotrapi.basekeys.ScenarioBase;
import fr.combattant54.lotrapi.enums.UniversalMaterial;
import fr.combattant54.lotrapi.game.WereWolfAPI;
import fr.combattant54.lotrapi.listeners.impl.ListenerWerewolf;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockExpEvent;

@Scenario(key = ScenarioBase.XP_BOOST,
        defaultValue = true,
        configValues = @IntValue(key = IntValueBase.XP_BOOST,
                defaultValue = 500,
                meetUpValue = 500,
                step = 10, item = UniversalMaterial.EXPERIENCE_BOTTLE))
public class XpBoost extends ListenerWerewolf {


    public XpBoost(WereWolfAPI main) {
        super(main);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onXp(BlockExpEvent event) {
        WereWolfAPI game = this.getGame();
        event.setExpToDrop((int) (event.getExpToDrop() *
                game.getConfig().getValue(IntValueBase.XP_BOOST) / 100f));
    }
}
