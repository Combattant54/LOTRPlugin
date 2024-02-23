package fr.combattant54.lotrplugin.commands.roles.ringcommunity.info.gandalf;

import fr.combattant54.lotrapi.annotations.RoleCommand;
import fr.combattant54.lotrapi.basekeys.IntValueBase;
import fr.combattant54.lotrapi.basekeys.Prefix;
import fr.combattant54.lotrapi.basekeys.RoleBase;
import fr.combattant54.lotrapi.basekeys.TimerBase;
import fr.combattant54.lotrapi.commands.ICommandRole;
import fr.combattant54.lotrapi.enums.StatePlayer;
import fr.combattant54.lotrapi.game.WereWolfAPI;
import fr.combattant54.lotrapi.player.interfaces.IPlayerWW;
import fr.combattant54.lotrapi.player.utils.Formatter;
import fr.combattant54.lotrapi.role.interfaces.IAffectedPlayers;
import fr.combattant54.lotrapi.role.interfaces.ILimitedUse;
import fr.combattant54.lotrapi.role.interfaces.IPower;
import fr.combattant54.lotrapi.role.interfaces.IRole;
import fr.combattant54.lotrapi.utils.BukkitUtils;
import fr.combattant54.lotrapi.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.UUID;

@RoleCommand(
        key = "lotr.roles.gandalf.command",
        roleKeys = RoleBase.FOX,
        argNumbers = 1,
        requiredPower = true
)
public class CommandGandalf implements ICommandRole {

    @Override
    public void execute(WereWolfAPI game, IPlayerWW playerWW, String[] strings) {
        UUID playerID = playerWW.getUUID();
        IRole gandalf = playerWW.getRole();
        Player playerArg = Bukkit.getPlayer(strings[0]);

        if (playerArg == null){
            playerWW.sendMessageWithKey(Prefix.RED, "lotr.check.offline_player");
            return;
        }

        UUID argID = playerArg.getUniqueId();
        IPlayerWW argPlayerWW = game.getPlayerWW(argID).orElse(null);

        if (playerID.equals(argID)){
            playerWW.sendMessageWithKey(Prefix.RED, "lotr.check.not_yourself");
            return;
        }

        if (argPlayerWW == null || !argPlayerWW.isState(StatePlayer.ALIVE)){
            playerWW.sendMessageWithKey(Prefix.RED, "lotr.check.player_not_found");
            return;
        }

        if (((ILimitedUse) gandalf).getUse() >= game.getConfig().getValue(IntValueBase.FOX_SMELL_NUMBER)){
            playerWW.sendMessageWithKey(Prefix.RED, "lotr.check.power");
            return;
        }

        ((IPower) gandalf).setPower(false);
        ((ILimitedUse) gandalf).setUse(((ILimitedUse)gandalf).getUse() + 1);


        Player player = Bukkit.getPlayer(playerID);
        ((IAffectedPlayers) gandalf).clearAffectedPlayer();
        ((IAffectedPlayers) gandalf).addAffectedPlayer(argPlayerWW);

        int cooldownvalue = game.getConfig().getTimerValue(TimerBase.FOX_SMELL_DURATION);
        String coolDownString = Utils.conversion(cooldownvalue);
        playerWW.sendMessageWithKey(Prefix.YELLOW, "lotr.roles.gandalf.start_see_inventory",
                Formatter.player(playerArg.getName()),
                Formatter.format("cooldown", coolDownString)
        );

        player.openInventory(playerArg.getInventory());

        BukkitUtils.scheduleSyncDelayedTask(
                game,
                gandalf::recoverPower,
                (long) cooldownvalue * 20
        );
    }
}
