package io.github.ph1lou.werewolfplugin.guis;


import fr.minuskube.inv.ClickableItem;
import fr.minuskube.inv.SmartInventory;
import fr.minuskube.inv.content.InventoryContents;
import fr.minuskube.inv.content.InventoryProvider;
import io.github.ph1lou.werewolfapi.IConfiguration;
import io.github.ph1lou.werewolfapi.WereWolfAPI;
import io.github.ph1lou.werewolfapi.enums.UniversalMaterial;
import io.github.ph1lou.werewolfapi.utils.ItemBuilder;
import io.github.ph1lou.werewolfplugin.Main;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.text.DecimalFormat;

public class Borders implements InventoryProvider {


    public static final SmartInventory INVENTORY = SmartInventory.builder()
            .id("borders")
            .manager(JavaPlugin.getPlugin(Main.class).getInvManager())
            .provider(new Borders())
            .size(3, 9)
            .title(JavaPlugin.getPlugin(Main.class).getWereWolfAPI().translate("werewolf.menu.border.name"))
            .closeable(true)
            .build();


    @Override
    public void init(Player player, InventoryContents contents) {
        Main main = JavaPlugin.getPlugin(Main.class);
        WereWolfAPI game = main.getWereWolfAPI();

        contents.set(0, 0, ClickableItem.of((new ItemBuilder(UniversalMaterial.COMPASS.getType()).setDisplayName(game.translate("werewolf.menu.return")).build()), e -> Config.INVENTORY.open(player)));
    }

    @Override
    public void update(Player player, InventoryContents contents) {

        Main main = JavaPlugin.getPlugin(Main.class);
        WereWolfAPI game = main.getWereWolfAPI();
        IConfiguration config = game.getConfig();

        contents.set(0, 3, ClickableItem.of((new ItemBuilder(Material.STONE_BUTTON).setDisplayName(game.translate("werewolf.utils.display", "-", config.getBorderMax())).build()), e -> {
            if (game.getConfig().getBorderMax() >= 100) {
                game.getConfig().setBorderMax(game.getConfig().getBorderMax() - 100);
                game.getMapManager().changeBorder(game.getConfig().getBorderMax() / 2);
                Borders.INVENTORY.open(player);
            }
        }));
        contents.set(0, 4, ClickableItem.empty((new ItemBuilder(Material.GLASS).setDisplayName(game.translate("werewolf.menu.border.radius_border_max", config.getBorderMax())).build())));
        contents.set(0, 5, ClickableItem.of((new ItemBuilder(Material.STONE_BUTTON).setDisplayName(game.translate("werewolf.utils.display", "+", config.getBorderMax())).build()), e -> {
            game.getConfig().setBorderMax(game.getConfig().getBorderMax() + 100);
            game.getMapManager().changeBorder(game.getConfig().getBorderMax() / 2);
            Borders.INVENTORY.open(player);
        }));
        contents.set(1, 3, ClickableItem.of((new ItemBuilder(Material.STONE_BUTTON).setDisplayName(game.translate("werewolf.utils.display", "-", config.getBorderMin())).build()), e -> {
            if (game.getConfig().getBorderMin() >= 100) {
                game.getConfig().setBorderMin(game.getConfig().getBorderMin() - 100);
                Borders.INVENTORY.open(player);
            }
        }));


        contents.set(1, 4, ClickableItem.empty((new ItemBuilder(Material.GLASS).setDisplayName(game.translate("werewolf.menu.border.radius_border_min", config.getBorderMin())).build())));
        contents.set(1, 5, ClickableItem.of((new ItemBuilder(Material.STONE_BUTTON).setDisplayName(game.translate("werewolf.utils.display", "+", config.getBorderMin())).build()), e -> {
            game.getConfig().setBorderMin(game.getConfig().getBorderMin() + 100);
            Borders.INVENTORY.open(player);
        }));

        String borderSpeed = new DecimalFormat("0.0")
                .format(config.getBorderSpeed());

        contents.set(2, 3, ClickableItem.of((new ItemBuilder(Material.STONE_BUTTON).setDisplayName(game.translate("werewolf.utils.display", "-", borderSpeed)).build()), e -> {
            if (game.getConfig().getBorderSpeed() >= 0.1) {
                game.getConfig().setBorderSpeed(game.getConfig().getBorderSpeed() - 0.1);
                Borders.INVENTORY.open(player);
            }
        }));
        contents.set(2, 4, ClickableItem.empty((new ItemBuilder(Material.GLASS).setDisplayName(game.translate("werewolf.menu.border.speed", borderSpeed)).build())));
        contents.set(2, 5, ClickableItem.of((new ItemBuilder(Material.STONE_BUTTON).setDisplayName(game.translate("werewolf.utils.display", "+", borderSpeed)).build()), e -> {
            game.getConfig().setBorderSpeed(game.getConfig().getBorderSpeed() + 0.1);
            Borders.INVENTORY.open(player);
        }));

    }

}

