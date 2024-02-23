package fr.combattant54.lotrplugin.scenarios;

import fr.combattant54.lotrapi.annotations.Scenario;
import fr.combattant54.lotrapi.basekeys.ScenarioBase;
import fr.combattant54.lotrapi.game.WereWolfAPI;
import fr.combattant54.lotrapi.listeners.impl.ListenerWerewolf;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.inventory.CraftingInventory;
import org.bukkit.inventory.ItemStack;

@Scenario(key = ScenarioBase.ROD_LESS, defaultValue = true, meetUpValue = true)
public class RodLess extends ListenerWerewolf {

    public RodLess(WereWolfAPI main) {
        super(main);
    }

    @EventHandler
    public void onCraft(PrepareItemCraftEvent event) {

        CraftingInventory inv = event.getInventory();

        if (inv.getResult() == null) return;

        if (inv.getResult().getType().equals(Material.FISHING_ROD)) {
            inv.setResult(new ItemStack(Material.AIR));
        }
    }
}
