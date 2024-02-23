package fr.combattant54.lotrplugin.commands.admin.ingame;

import fr.combattant54.lotrapi.annotations.AdminCommand;
import fr.combattant54.lotrapi.basekeys.Prefix;
import fr.combattant54.lotrapi.commands.ICommand;
import fr.combattant54.lotrapi.game.WereWolfAPI;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

@AdminCommand(key = "werewolf.commands.admin.inventory.command",
        descriptionKey = "werewolf.commands.admin.inventory.description",
        moderatorAccess = true,
        argNumbers = 1)
public class CommandInventory implements ICommand {

    @Override
    public void execute(WereWolfAPI game, Player player, String[] args) {

        Player pInv = Bukkit.getPlayer(args[0]);

        if (pInv == null) {
            player.sendMessage(game.translate(Prefix.RED, "werewolf.check.offline_player"));
            return;
        }

        Inventory inv = Bukkit.createInventory(null, 45, args[0]);

        for (int i = 0; i < 40; i++) {
            inv.setItem(i, pInv.getInventory().getItem(i));
        }

        player.openInventory(inv);
    }
}
