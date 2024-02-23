package fr.combattant54.lotrplugin.commands.admin.ingame;

import fr.combattant54.lotrapi.annotations.AdminCommand;
import fr.combattant54.lotrapi.basekeys.Prefix;
import fr.combattant54.lotrapi.commands.ICommand;
import fr.combattant54.lotrapi.enums.StateGame;
import fr.combattant54.lotrapi.events.UpdateStuffEvent;
import fr.combattant54.lotrapi.game.IStuffManager;
import fr.combattant54.lotrapi.game.WereWolfAPI;
import fr.combattant54.lotrapi.player.utils.Formatter;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

@AdminCommand(key = "werewolf.commands.admin.loot_role.command", descriptionKey = "",
        statesGame = {StateGame.LOBBY, StateGame.TRANSPORTATION, StateGame.START},
        argNumbers = 1,
        autoCompletion = false,
        moderatorAccess = true)
public class CommandStuffRole implements ICommand {

    @Override
    public void execute(WereWolfAPI game, Player player, String[] args) {

        IStuffManager stuffManager = game.getStuffs();
        UUID uuid = player.getUniqueId();

        if (!stuffManager.isInTempStuff(uuid)) {
            return;
        }

        stuffManager.setStuffRole(args[0], Arrays.stream(player.getInventory().getContents())
                .filter(Objects::nonNull)
                .collect(Collectors.toList()));
        player.sendMessage(game.translate(Prefix.GREEN, "werewolf.commands.admin.loot_role.perform",
                Formatter.role(game.translate(args[0]))));


        ItemStack[] items = stuffManager.recoverTempStuff(uuid);
        for (int i = 0; i < 40; i++) {
            player.getInventory().setItem(i, items[i]);
        }

        player.setGameMode(GameMode.ADVENTURE);

        Bukkit.getPluginManager().callEvent(new UpdateStuffEvent());
    }
}
