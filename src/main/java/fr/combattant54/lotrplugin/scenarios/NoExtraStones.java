package fr.combattant54.lotrplugin.scenarios;

import fr.combattant54.lotrapi.annotations.Scenario;
import fr.combattant54.lotrapi.basekeys.ScenarioBase;
import fr.combattant54.lotrapi.enums.UniversalMaterial;
import fr.combattant54.lotrapi.game.WereWolfAPI;
import fr.combattant54.lotrapi.listeners.impl.ListenerWerewolf;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.List;

@Scenario(key = ScenarioBase.NO_EXTRA_STONES, defaultValue = true, meetUpValue = true)
public class NoExtraStones extends ListenerWerewolf {

    public NoExtraStones(WereWolfAPI main) {
        super(main);
    }

    @EventHandler(priority = EventPriority.LOWEST)
    private void onBlockBreak(BlockBreakEvent event) {

        Block block = event.getBlock();
        List<ItemStack> itemStacks = Arrays.asList(UniversalMaterial.ANDESITE.getStack(),
                UniversalMaterial.DIORITE.getStack(),
                UniversalMaterial.GRANITE.getStack());

        if (event
                .getBlock()
                .getDrops()
                .stream()
                .anyMatch(itemStack -> itemStacks.stream()
                        .anyMatch(itemStack1 -> itemStack1.isSimilar(itemStack)))) {
            block.setType(Material.AIR);
            Location loc = new Location(block.getWorld(), block.getLocation().getBlockX() + 0.5, block.getLocation().getBlockY() + 0.5, block.getLocation().getBlockZ() + 0.5);
            block.getWorld().dropItem(loc, new ItemStack(Material.COBBLESTONE, 1));
        }
    }

}
