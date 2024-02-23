package fr.combattant54.lotrplugin.guis;


import fr.combattant54.lotrplugin.Main;
import fr.minuskube.inv.ClickableItem;
import fr.minuskube.inv.SmartInventory;
import fr.minuskube.inv.content.InventoryContents;
import fr.minuskube.inv.content.InventoryProvider;
import fr.combattant54.lotrapi.basekeys.Prefix;
import fr.combattant54.lotrapi.enums.StateGame;
import fr.combattant54.lotrapi.enums.UniversalMaterial;
import fr.combattant54.lotrapi.game.WereWolfAPI;
import fr.combattant54.lotrapi.player.utils.Formatter;
import fr.combattant54.lotrapi.utils.BukkitUtils;
import fr.combattant54.lotrapi.utils.ItemBuilder;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

public class MapsGUI implements InventoryProvider {


    public static final SmartInventory INVENTORY = SmartInventory.builder()
            .id("maps")
            .manager(JavaPlugin.getPlugin(Main.class).getInvManager())
            .provider(new MapsGUI())
            .size(2, 9)
            .title(JavaPlugin.getPlugin(Main.class).getWereWolfAPI().translate("werewolf.menus.maps.name"))
            .closeable(true)
            .build();


    @Override
    public void init(Player player, InventoryContents contents) {
        Main main = JavaPlugin.getPlugin(Main.class);
        WereWolfAPI game = main.getWereWolfAPI();
        contents.set(0, 0, ClickableItem.of((new ItemBuilder(UniversalMaterial.COMPASS.getType()).setDisplayName(game.translate("werewolf.menus.return")).build()), e -> MainGUI.INVENTORY.open(player)));
    }

    @Override
    public void update(Player player, InventoryContents contents) {

        Main main = JavaPlugin.getPlugin(Main.class);
        WereWolfAPI game = main.getWereWolfAPI();

        File repertoire = new File(main.getDataFolder() + File.separator + "maps");
        File[] files = repertoire.listFiles();
        if (files == null) return;
        int i = 1;

        for (File file : files) {
            contents.set(0, i, ClickableItem.of((
                            new ItemBuilder(UniversalMaterial.MAP.getType())
                                    .setDisplayName(game.translate("werewolf.menus.maps.map",
                                            Formatter.format("&world&", file.getName()))).build()),
                    e -> {
                        if (!game.isState(StateGame.LOBBY)) {
                            player.sendMessage(game.translate(Prefix.RED, "werewolf.check.game_in_progress"));
                            return;
                        }
                        game.getMapManager().loadMap(file);
                    }));
            i++;
        }


        contents.set(1, 1, ClickableItem.of((
                        new ItemBuilder(UniversalMaterial.LAVA_BUCKET.getType())
                                .setDisplayName(game.translate("werewolf.menus.maps.new")).build()),
                e -> BukkitUtils.scheduleSyncDelayedTask(game, () -> {
                    if (!game.isState(StateGame.LOBBY)) {
                        player.sendMessage(game.translate(Prefix.RED, "werewolf.check.game_in_progress"));
                        return;
                    }
                    player.closeInventory();
                    game.getMapManager().loadMap(null);
                })));
    }


}

