package fr.combattant54.lotrplugin.guis;


import fr.combattant54.lotrplugin.Main;
import fr.minuskube.inv.ClickableItem;
import fr.minuskube.inv.SmartInventory;
import fr.minuskube.inv.content.InventoryContents;
import fr.minuskube.inv.content.InventoryProvider;
import fr.combattant54.lotrapi.enums.UniversalMaterial;
import fr.combattant54.lotrapi.game.WereWolfAPI;
import fr.combattant54.lotrapi.utils.ItemBuilder;
import fr.combattant54.lotrplugin.save.StuffLoader;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class MeetUpGUI implements InventoryProvider {


    public static final SmartInventory INVENTORY = SmartInventory.builder()
            .id("stuffs")
            .manager(JavaPlugin.getPlugin(Main.class).getInvManager())
            .provider(new MeetUpGUI())
            .size(1, 9)
            .title(JavaPlugin.getPlugin(Main.class).getWereWolfAPI().translate("werewolf.menus.meet_up.name"))
            .closeable(true)
            .build();


    @Override
    public void init(Player player, InventoryContents contents) {
        Main main = JavaPlugin.getPlugin(Main.class);
        WereWolfAPI game = main.getWereWolfAPI();

        contents.set(0, 0, ClickableItem.of((new ItemBuilder(UniversalMaterial.COMPASS.getType())
                .setDisplayName(game.translate("werewolf.menus.return"))
                .build()), e -> MainGUI.INVENTORY.open(player)));
    }

    @Override
    public void update(Player player, InventoryContents contents) {

        Main main = JavaPlugin.getPlugin(Main.class);
        WereWolfAPI game = main.getWereWolfAPI();

        contents.set(0, 4, ClickableItem.of((new ItemBuilder(game.getConfig().isMeetUp() ?
                Material.BARRIER : UniversalMaterial.GOLDEN_SWORD.getType())
                .setDisplayName(game.translate(game.getConfig().isMeetUp() ?
                        "werewolf.menus.meet_up.disable" :
                        "werewolf.menus.meet_up.enable"))
                .setLore(game.translate("werewolf.menus.meet_up.warning"))
                .build()), e -> {
            game.getConfig().setMeetUp(!game.getConfig().isMeetUp());
            game.getConfig().resetSwitchMeetUp();
            game.getListenersManager().updateListeners();

            if (game.getConfig().isMeetUp()) {
                StuffLoader.loadAllStuffMeetUP(game);
                game.getConfig().setBorderMax(1000);
                game.getConfig().setBorderMin(300);
                game.getMapManager().changeBorder(game.getConfig().getBorderMax() / 2);
            } else {
                StuffLoader.loadAllStuffDefault(game);
                game.getConfig().setBorderMax(2000);
                game.getConfig().setBorderMin(500);
                game.getMapManager().changeBorder(game.getConfig().getBorderMax() / 2);
                game.getStuffs().clearStartLoot();
                game.getStuffs().clearDeathLoot();
            }
            e.setCurrentItem(new ItemBuilder(game.getConfig().isMeetUp() ?
                    Material.BARRIER : UniversalMaterial.GOLDEN_SWORD.getType())
                    .setDisplayName(game.translate(game.getConfig().isMeetUp() ?
                            "werewolf.menus.meet_up.disable" :
                            "werewolf.menus.meet_up.enable"))
                    .setLore(game.translate("werewolf.menus.meet_up.warning"))
                    .build());
        }));

    }

}

