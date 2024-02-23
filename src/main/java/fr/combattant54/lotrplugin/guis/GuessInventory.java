package fr.combattant54.lotrplugin.guis;

import fr.combattant54.lotrplugin.Main;
import fr.combattant54.lotrplugin.Register;
import fr.minuskube.inv.ClickableItem;
import fr.minuskube.inv.SmartInventory;
import fr.minuskube.inv.content.InventoryContents;
import fr.minuskube.inv.content.InventoryProvider;
import fr.minuskube.inv.content.Pagination;
import fr.combattant54.lotrapi.enums.Camp;
import fr.combattant54.lotrapi.enums.Category;
import fr.combattant54.lotrapi.enums.UniversalMaterial;
import fr.combattant54.lotrapi.events.game.guess.GuessEvent;
import fr.combattant54.lotrapi.game.IConfiguration;
import fr.combattant54.lotrapi.game.WereWolfAPI;
import fr.combattant54.lotrapi.player.interfaces.IPlayerWW;
import fr.combattant54.lotrapi.player.utils.Formatter;
import fr.combattant54.lotrapi.role.interfaces.IGuesser;
import fr.combattant54.lotrapi.utils.ItemBuilder;
import fr.combattant54.lotrplugin.utils.InventoryUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

public class GuessInventory implements InventoryProvider {

    private Category category;
    private final Set<Category> categories;
    private final IPlayerWW targetWW;

    public GuessInventory(IPlayerWW targetWW, Set<Category> categories) {
        this.targetWW = targetWW;
        this.categories = categories;
        if (categories.contains(Category.RING_COMMUNITY)) {
            category = Category.RING_COMMUNITY;
        }
        else if (categories.contains(Category.RING_COMMUNITY)) {
            category = Category.RING_COMMUNITY;
        } else if (categories.contains(Category.NEUTRAL)) {
            category = Category.NEUTRAL;
        } else {
            category = Category.ADDONS;
        }
    }

    public static SmartInventory getInventory(IPlayerWW targetWW, Set<Category> categories) {
        return SmartInventory.builder()
                .id("guess")
                .manager(JavaPlugin.getPlugin(Main.class).getInvManager())
                .provider(new GuessInventory(targetWW, categories))
                .size(6, 9)
                .title(JavaPlugin.getPlugin(Main.class).getWereWolfAPI().translate("werewolf.commands.player.guess.title",
                        Formatter.format("&player&", targetWW.getName())))
                .closeable(true)
                .build();
    }

    @Override
    public void init(Player player, InventoryContents contents) {
    }

    @Override
    public void update(Player player, InventoryContents contents) {
        Main main = JavaPlugin.getPlugin(Main.class);
        WereWolfAPI game = main.getWereWolfAPI();
        IConfiguration config = game.getConfig();
        Pagination pagination = contents.pagination();

        if (categories.contains(Category.DARK_SIDE)) {
            contents.set(5, 1, ClickableItem.of(
                    new ItemBuilder(
                            Category.DARK_SIDE == this.category ?
                                    Material.EMERALD_BLOCK : Material.REDSTONE_BLOCK)
                            .setDisplayName(game.translate(Camp.DARK_SIDE.getKey()))
                            .build(), e -> this.category = Category.DARK_SIDE));
        }

        if (categories.contains(Category.RING_COMMUNITY)) {
            contents.set(5, 3, ClickableItem.of((
                    new ItemBuilder(
                            Category.RING_COMMUNITY == this.category ?
                                    Material.EMERALD_BLOCK : Material.REDSTONE_BLOCK)
                            .setDisplayName(game.translate(Camp.RING_COMMUNITY.getKey()))
                            .build()), e -> this.category = Category.RING_COMMUNITY));
        }

        if (categories.contains(Category.NEUTRAL)) {
            contents.set(5, 5, ClickableItem.of((
                    new ItemBuilder(
                            Category.NEUTRAL == this.category ?
                                    Material.EMERALD_BLOCK : Material.REDSTONE_BLOCK)
                            .setDisplayName(game.translate(Camp.NEUTRAL.getKey()))
                            .build()), e -> this.category = Category.NEUTRAL));
        }

        if (categories.contains(Category.ADDONS)) {
            contents.set(5, 7, ClickableItem.of((
                    new ItemBuilder(
                            Category.ADDONS == this.category ?
                                    Material.EMERALD_BLOCK : Material.REDSTONE_BLOCK)
                            .setDisplayName(game.translate("werewolf.categories.addons"))
                            .build()), e -> this.category = Category.ADDONS));
        }

        List<ClickableItem> items = new ArrayList<>();

        main.getRegisterManager().getRolesRegister()
                .stream()
                .sorted((o1, o2) -> game.translate(o1.getMetaDatas().key())
                        .compareToIgnoreCase(game.translate(o2.getMetaDatas().key())))
                .forEach(roleRegister -> {

                    String key = roleRegister.getMetaDatas().key();
                    Optional<String> addonKey = Register.get().getModuleKey(key);

                    if (roleRegister.getMetaDatas().category() == this.category
                        ||
                        (addonKey.isPresent() &&
                         !addonKey.get().equals(Main.KEY) &&
                         this.category == Category.ADDONS)) {


                        List<String> lore = Arrays.stream(roleRegister.getMetaDatas().loreKey())
                                .map(game::translate)
                                .collect(Collectors.toList());

                        if (config.getTrollKey().equals(key)) {
                            items.add(ClickableItem.empty(new ItemBuilder(UniversalMaterial.GREEN_TERRACOTTA.getStack()).setLore(lore)
                                    .setDisplayName(game.translate(key)).build()));
                        } else {
                            items.add(ClickableItem.of((new ItemBuilder(UniversalMaterial.RED_TERRACOTTA.getStack())
                                    .setLore(lore)
                                    .setDisplayName(game.translate(key)).build()), event -> {

                                UUID uuid = player.getUniqueId();
                                game.getPlayerWW(uuid).ifPresent(iPlayerWW -> {
                                    if ((iPlayerWW.getRole() instanceof IGuesser)) {
                                        IGuesser role = (IGuesser) iPlayerWW.getRole();

                                        role.resolveGuess(key, targetWW);

                                        Bukkit.getPluginManager().callEvent(new GuessEvent(iPlayerWW, targetWW, key));
                                    }
                                });
                                player.closeInventory();
                            }));
                        }

                    }
                });
        InventoryUtils.fillInventory(game, items, pagination, contents, () -> getInventory(targetWW, this.categories), 36);
    }
}
