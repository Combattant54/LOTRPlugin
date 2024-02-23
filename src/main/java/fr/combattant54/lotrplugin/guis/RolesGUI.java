package fr.combattant54.lotrplugin.guis;


import fr.combattant54.lotrplugin.Main;
import fr.combattant54.lotrplugin.Register;
import fr.combattant54.lotrplugin.game.GameManager;
import fr.minuskube.inv.ClickableItem;
import fr.minuskube.inv.SmartInventory;
import fr.minuskube.inv.content.InventoryContents;
import fr.minuskube.inv.content.InventoryProvider;
import fr.minuskube.inv.content.Pagination;
import fr.combattant54.lotrapi.annotations.ConfigurationBasic;
import fr.combattant54.lotrapi.annotations.Role;
import fr.combattant54.lotrapi.enums.Camp;
import fr.combattant54.lotrapi.enums.Category;
import fr.combattant54.lotrapi.enums.StateGame;
import fr.combattant54.lotrapi.enums.UniversalMaterial;
import fr.combattant54.lotrapi.game.IConfiguration;
import fr.combattant54.lotrapi.game.WereWolfAPI;
import fr.combattant54.lotrapi.player.utils.Formatter;
import fr.combattant54.lotrapi.role.interfaces.IRole;
import fr.combattant54.lotrapi.utils.ItemBuilder;
import fr.combattant54.lotrapi.utils.Wrapper;
import fr.combattant54.lotrplugin.utils.InventoryUtils;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;

public class RolesGUI implements InventoryProvider {

    private Category category;

    public RolesGUI(Player player, Category category) {
        this.category = category;
    }

    public static SmartInventory getInventory(Player player, Category category) {

        Main main = JavaPlugin.getPlugin(Main.class);
        return SmartInventory.builder()
                .id("roles")
                .manager(main.getInvManager())
                .provider(new RolesGUI(player, category))
                .size(6, 9)
                .title(main.getWereWolfAPI().translate("werewolf.menus.roles.name"))
                .closeable(true)
                .build();
    }


    @Override
    public void init(Player player, InventoryContents contents) {
        Main main = JavaPlugin.getPlugin(Main.class);
        GameManager game = (GameManager) main.getWereWolfAPI();
        IConfiguration config = game.getConfig();

        contents.set(0, 0, ClickableItem.of((
                new ItemBuilder(UniversalMaterial.COMPASS.getType())
                        .setDisplayName(game.translate("werewolf.menus.return"))
                        .build()), e -> MainGUI.INVENTORY.open(player)));

        contents.set(0, 8, ClickableItem.of((new ItemBuilder(UniversalMaterial.BARRIER.getType())
                .setDisplayName(game.translate("werewolf.menus.roles.zero")).build()), e -> {
            for (Wrapper<IRole, Role> roleRegister : main.getRegisterManager().getRolesRegister()) {
                config.setRole(roleRegister.getMetaDatas().key(), 0);
            }
            game.setRoleInitialSize(0);
        }));
    }

    @Override
    public void update(Player player, InventoryContents contents) {

        Main main = JavaPlugin.getPlugin(Main.class);
        GameManager game = (GameManager) main.getWereWolfAPI();
        IConfiguration config = game.getConfig();
        Pagination pagination = contents.pagination();

        List<String> lore = new ArrayList<>(Arrays.asList(game.translate("werewolf.menus.lore.left"),
                game.translate("werewolf.menus.lore.right")));

        contents.set(5, 1, ClickableItem.of((new ItemBuilder(Category.DARK_SIDE == this.category ? Material.EMERALD_BLOCK : Material.REDSTONE_BLOCK)
                .setDisplayName(game.translate(Camp.DARK_SIDE.getKey())).setAmount(Math.max(1, count(game, Category.DARK_SIDE))).build()), e -> this.category = Category.DARK_SIDE));
        contents.set(5, 3, ClickableItem.of((new ItemBuilder(Category.RING_COMMUNITY == this.category ? Material.EMERALD_BLOCK : Material.REDSTONE_BLOCK)
                .setDisplayName(game.translate(Camp.RING_COMMUNITY.getKey())).setAmount(Math.max(1, count(game, Category.RING_COMMUNITY))).build()), e -> this.category = Category.RING_COMMUNITY));
        contents.set(5, 5, ClickableItem.of((new ItemBuilder(Category.NEUTRAL == this.category ? Material.EMERALD_BLOCK : Material.REDSTONE_BLOCK)
                .setDisplayName(game.translate(Camp.NEUTRAL.getKey())).setAmount(Math.max(1, count(game, Category.NEUTRAL))).build()), e -> this.category = Category.NEUTRAL));
        contents.set(5, 7, ClickableItem.of((new ItemBuilder(Category.ADDONS == this.category ? Material.EMERALD_BLOCK : Material.REDSTONE_BLOCK)
                .setDisplayName(game.translate("werewolf.categories.addons")).setAmount(Math.max(1, count(game, Category.ADDONS))).build()), e -> this.category = Category.ADDONS));


        lore.add(game.translate("werewolf.menus.lore.shift"));

        List<ClickableItem> items = new ArrayList<>();

        main.getRegisterManager().getRolesRegister()
                .stream()
                .sorted((o1, o2) -> game.translate(o1.getMetaDatas().key())
                        .compareToIgnoreCase(game.translate(o2.getMetaDatas().key())))
                .forEach(roleRegister -> {

                    Optional<String> addonKey = Register.get().getModuleKey(roleRegister.getMetaDatas().key());
                    if (roleRegister.getMetaDatas().category() == this.category ||
                            (addonKey.isPresent() &&
                                    !addonKey.get().equals(Main.KEY) &&
                                    this.category == Category.ADDONS)) {

                        String key = roleRegister.getMetaDatas().key();
                        AtomicBoolean unRemovable = new AtomicBoolean(false);
                        List<String> lore2 = new ArrayList<>(lore);


                        if (game.getConfig().getRoleCount(key) > 0) {
                            lore2.addAll(AdvancedConfigurationUtils.getLore(game,
                                    roleRegister.getMetaDatas().loreKey(),
                                    roleRegister.getMetaDatas().configurations(),
                                    roleRegister.getMetaDatas().timers(),
                                    roleRegister.getMetaDatas().configValues(),
                                    new ConfigurationBasic[]{}));
                        }
                        Arrays.stream(roleRegister.getMetaDatas().requireRoles())
                                .forEach(roleKey -> lore2.add(game.translate("werewolf.menus.roles.need",
                                        Formatter.role(game.translate(roleKey)))));
                        main.getRegisterManager().getRolesRegister().stream()
                                .filter(roleRegister1 -> Arrays.stream(roleRegister1.getMetaDatas().requireRoles())
                                        .anyMatch(requiredRole -> requiredRole.equals(roleRegister1.getMetaDatas().key())))
                                .map(iRoleRoleWrapper -> iRoleRoleWrapper.getMetaDatas().key())
                                .filter(roleRegister1Key -> game.getConfig().getRoleCount(roleRegister1Key) > 0)
                                .findFirst().ifPresent(role -> {
                                    lore2.add(game.translate("werewolf.menus.roles.dependant_load",
                                            Formatter.role(game.translate(role))));
                                    unRemovable.set(true);
                                });

                        Optional<String> incompatible = Arrays.stream(roleRegister.getMetaDatas().incompatibleRoles())
                                .filter(s -> game.getConfig().getRoleCount(s) > 0)
                                .map(game::translate)
                                .findFirst();

                        incompatible
                                .ifPresent(role -> lore2.add(game.translate("werewolf.menus.roles.incompatible",
                                        Formatter.role(role))));

                        if (config.getRoleCount(key) > 0) {
                            items.add(ClickableItem.of((
                                    new ItemBuilder(UniversalMaterial.GREEN_TERRACOTTA.getStack())
                                            .setAmount(config.getRoleCount(key))
                                            .setLore(lore2)
                                            .setDisplayName(game.translate(key))
                                            .build()), e -> {

                                if (e.isShiftClick()) {
                                    AdvancedRolesGUI.getInventory(roleRegister.getMetaDatas(), pagination.getPage()).open(player);
                                } else if (e.isLeftClick()) {
                                    selectPlus(game, key);
                                } else if (e.isRightClick()) {
                                    int count = game.getConfig().getRoleCount(key);
                                    if (!unRemovable.get() || count > 1) {
                                        if (roleRegister.getMetaDatas().requireDouble() && count == 2) {
                                            selectMinus(game, key);
                                        }
                                        selectMinus(game, key);
                                    }
                                }
                            }));
                        } else {

                            items.add(ClickableItem.of((new ItemBuilder(UniversalMaterial.RED_TERRACOTTA.getStack())
                                    .setAmount(1)
                                    .setLore(lore2)
                                    .setDisplayName(game.translate(key)).build()), e -> {

                                if (e.isShiftClick()) {
                                    AdvancedRolesGUI.getInventory(roleRegister.getMetaDatas(), pagination.getPage()).open(player);
                                } else if (e.isLeftClick()) {
                                    if (incompatible.isPresent()) {
                                        return;
                                    }
                                    if (Arrays.stream(roleRegister.getMetaDatas().requireRoles())
                                            .anyMatch(requireRole -> game.getConfig().getRoleCount(requireRole) == 0)) {
                                        return;
                                    }
                                    if (roleRegister.getMetaDatas().requireDouble()) {
                                        selectPlus(game, key);
                                    }
                                    selectPlus(game, key);
                                }
                            }));
                        }

                    }
                });

        InventoryUtils.fillInventory(game, items, pagination, contents, () -> getInventory(player, this.category), 36);
    }

    public void selectMinus(GameManager game, String key) {

        if (game.isState(StateGame.GAME)) return;

        IConfiguration config = game.getConfig();
        if (config.getRoleCount(key) > 0) {
            game.setRoleInitialSize(game.getTotalRoles() - 1);
            config.removeOneRole(key);
        }
    }

    public void selectPlus(GameManager game, String key) {

        if (game.isState(StateGame.GAME)) return;

        IConfiguration config = game.getConfig();
        config.addOneRole(key);
        game.setRoleInitialSize(game.getTotalRoles() + 1);
    }

    private int count(WereWolfAPI game, Category category) {
        int i = 0;
        for (Wrapper<IRole, Role> roleRegister : Register.get().getRolesRegister()) {
            if (roleRegister.getMetaDatas().category() == category) {
                i += game.getConfig().getRoleCount(roleRegister.getMetaDatas().key());
            }

        }
        return i;
    }
}

