package fr.combattant54.lotrplugin.commands.admin.ingame;

import fr.combattant54.lotrapi.annotations.AdminCommand;
import fr.combattant54.lotrapi.basekeys.Prefix;
import fr.combattant54.lotrapi.commands.ICommand;
import fr.combattant54.lotrapi.events.UpdateStuffEvent;
import fr.combattant54.lotrapi.game.IStuffManager;
import fr.combattant54.lotrapi.game.WereWolfAPI;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.Objects;
import java.util.UUID;

@AdminCommand(key = "werewolf.commands.admin.loot_death.command", descriptionKey = "",
        moderatorAccess = true,
        autoCompletion = false,
        argNumbers = 0)
public class CommandLootDeath implements ICommand {

    @Override
    public void execute(WereWolfAPI game, Player player, String[] args) {

        IStuffManager stuffManager = game.getStuffs();
        UUID uuid = player.getUniqueId();

        stuffManager.clearDeathLoot();

        Arrays.stream(player.getInventory().getContents())
                .filter(Objects::nonNull)
                .forEach(stuffManager::addDeathLoot);

        player.sendMessage(game.translate(Prefix.GREEN, "werewolf.commands.admin.loot_death.perform"));
        player.setGameMode(GameMode.ADVENTURE);

        if (!stuffManager.isInTempStuff(uuid)) {
            return;
        }
        ItemStack[] items = stuffManager.recoverTempStuff(uuid);
        for (int i = 0; i < 40; i++) {
            player.getInventory().setItem(i, items[i]);
        }

        Bukkit.getPluginManager().callEvent(new UpdateStuffEvent());
    }
}
