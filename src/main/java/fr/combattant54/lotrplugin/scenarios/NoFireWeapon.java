package fr.combattant54.lotrplugin.scenarios;

import fr.combattant54.lotrapi.annotations.Scenario;
import fr.combattant54.lotrapi.basekeys.ScenarioBase;
import fr.combattant54.lotrapi.events.game.utils.EnchantmentEvent;
import fr.combattant54.lotrapi.game.WereWolfAPI;
import fr.combattant54.lotrapi.listeners.impl.ListenerWerewolf;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.EventHandler;

@Scenario(key = ScenarioBase.NO_FIRE_WEAPONS, defaultValue = true, meetUpValue = true)
public class NoFireWeapon extends ListenerWerewolf {

    public NoFireWeapon(WereWolfAPI main) {
        super(main);
    }

    @EventHandler
    public void onEnchant(EnchantmentEvent event) {
        event.getFinalEnchants().remove(Enchantment.ARROW_FIRE);
        event.getFinalEnchants().remove(Enchantment.FIRE_ASPECT);
    }
}
