package fr.combattant54.lotrplugin.commands.admin.ingame;

import fr.combattant54.lotrapi.annotations.AdminCommand;
import fr.combattant54.lotrapi.basekeys.Prefix;
import fr.combattant54.lotrapi.commands.ICommand;
import fr.combattant54.lotrapi.enums.Sound;
import fr.combattant54.lotrapi.enums.StateGame;
import fr.combattant54.lotrapi.game.WereWolfAPI;
import fr.combattant54.lotrapi.versions.VersionUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

@AdminCommand(key = "werewolf.commands.admin.final_heal.command",
        descriptionKey = "werewolf.commands.admin.final_heal.description",
        argNumbers = 0,
        statesGame = {StateGame.START, StateGame.GAME})
public class CommandFinalHeal implements ICommand {

    @Override
    public void execute(WereWolfAPI game, Player player, String[] args) {

        Bukkit.getOnlinePlayers().forEach(player1 -> {
            player1.setHealth(VersionUtils.getVersionUtils().getPlayerMaxHealth(player1));
            Sound.NOTE_STICKS.play(player1);
            player1.sendMessage(game.translate(Prefix.ORANGE, "werewolf.commands.admin.final_heal.send"));
        });
    }
}
