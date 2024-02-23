package fr.combattant54.lotrplugin.guis;


import fr.combattant54.lotrplugin.Main;
import fr.minuskube.inv.ClickableItem;
import fr.minuskube.inv.SmartInventory;
import fr.minuskube.inv.content.InventoryContents;
import fr.minuskube.inv.content.InventoryProvider;
import fr.combattant54.lotrapi.annotations.Author;
import fr.combattant54.lotrapi.annotations.ModuleWerewolf;
import fr.combattant54.lotrapi.enums.UniversalMaterial;
import fr.combattant54.lotrapi.game.WereWolfAPI;
import fr.combattant54.lotrapi.utils.ItemBuilder;
import fr.combattant54.lotrapi.utils.Wrapper;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class AddonsGUI implements InventoryProvider {


    public static final SmartInventory INVENTORY = SmartInventory.builder()
            .id("addonMenu")
            .manager(JavaPlugin.getPlugin(Main.class).getInvManager())
            .provider(new AddonsGUI())
            .size(4, 9)
            .title(JavaPlugin.getPlugin(Main.class)
                    .getWereWolfAPI().translate("werewolf.menus.addon.name"))
            .closeable(true)
            .build();


    @Override
    public void init(Player player, InventoryContents contents) {
        Main main = JavaPlugin.getPlugin(Main.class);
        WereWolfAPI game = main.getWereWolfAPI();
        int i = 1;


        for (Wrapper<JavaPlugin, ModuleWerewolf> addon : main.getRegisterManager().getModulesRegister()) {

            List<String> lore = Arrays.stream(addon.getMetaDatas().loreKeys())
                    .map(game::translate).collect(Collectors.toList());
            lore.add(game.translate("werewolf.utils.author"));
            lore.add(Arrays.stream(addon.getMetaDatas().authors())
                    .map(Author::name).collect(Collectors.joining(", ")));

            contents.set(i / 9, i % 9, ClickableItem.empty(new ItemBuilder(addon.getMetaDatas().item().getStack())
                    .setDisplayName(game.translate(addon.getMetaDatas().key())).setLore(lore).build()));
            i++;
        }
        contents.set(0, 0, ClickableItem.of((new ItemBuilder(
                UniversalMaterial.COMPASS.getType())
                .setDisplayName(game.translate("werewolf.menus.return"))
                .build()), e -> MainGUI.INVENTORY.open(player)));
    }

    @Override
    public void update(Player player, InventoryContents contents) {


    }


}

