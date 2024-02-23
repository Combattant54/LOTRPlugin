package fr.combattant54.lotrplugin.guis;


import fr.combattant54.lotrplugin.Main;
import fr.minuskube.inv.ClickableItem;
import fr.minuskube.inv.SmartInventory;
import fr.minuskube.inv.content.InventoryContents;
import fr.minuskube.inv.content.InventoryProvider;
import fr.combattant54.lotrapi.annotations.Scenario;
import fr.combattant54.lotrapi.enums.UniversalMaterial;
import fr.combattant54.lotrapi.game.WereWolfAPI;
import fr.combattant54.lotrapi.player.utils.Formatter;
import fr.combattant54.lotrapi.utils.ItemBuilder;
import fr.combattant54.lotrplugin.utils.InventoryUtils;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.concurrent.atomic.AtomicInteger;

public class AdvancedScenariosGUI implements InventoryProvider {

    private final Scenario register;
    private final int page;

    public AdvancedScenariosGUI(Scenario register, int page) {
        this.register = register;
        this.page = page;
    }

    public static SmartInventory getInventory(Scenario register, int page) {

        Main api = JavaPlugin.getPlugin(Main.class);

        WereWolfAPI game = api.getWereWolfAPI();
        return SmartInventory.builder()
                .id("advanced" + register.key())
                .manager(api.getInvManager())
                .provider(new AdvancedScenariosGUI(register, page))
                .size(InventoryUtils.getRowNumbers((register.configurations().length
                        + register.timers().length +
                        register.configValues().length) * 2, true), 9)
                .title(game.translate("werewolf.menus.advanced_tool_role.menu",
                        Formatter.role(game.translate(register.key()))))
                .closeable(true)
                .build();
    }


    @Override
    public void init(Player player, InventoryContents contents) {
        Main main = JavaPlugin.getPlugin(Main.class);
        WereWolfAPI game = main.getWereWolfAPI();

        contents.set(0, 0, ClickableItem.of(new ItemBuilder(UniversalMaterial.COMPASS.getType())
                        .setDisplayName(game.translate("werewolf.menus.return")).build(),
                e -> ScenariosGUI.INVENTORY.open(player, page)));

    }

    @Override
    public void update(Player player, InventoryContents contents) {

        Main main = JavaPlugin.getPlugin(Main.class);
        WereWolfAPI game = main.getWereWolfAPI();

        AtomicInteger i = new AtomicInteger(2);

        AdvancedConfigurationUtils.getIntConfigs(game, this.register.configValues()).forEach(clickableItem -> {
            contents.set(i.get() / 9, i.get() % 9, clickableItem);
            i.set(i.get() + 2);
        });
    }

}

