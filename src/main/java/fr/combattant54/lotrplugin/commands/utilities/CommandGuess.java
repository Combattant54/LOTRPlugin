package fr.combattant54.lotrplugin.commands.utilities;

import fr.combattant54.lotrapi.annotations.RoleCommand;
import fr.combattant54.lotrapi.basekeys.RoleBase;
import fr.combattant54.lotrapi.commands.ICommandRole;
import fr.combattant54.lotrapi.enums.Category;
import fr.combattant54.lotrapi.enums.StatePlayer;
import fr.combattant54.lotrapi.game.WereWolfAPI;
import fr.combattant54.lotrapi.player.interfaces.IPlayerWW;
import fr.combattant54.lotrapi.role.interfaces.IGuesser;
import fr.combattant54.lotrplugin.guis.GuessInventory;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@RoleCommand(key = "werewolf.commands.player.guess.command",
        roleKeys = { RoleBase.MASTERMIND,
                RoleBase.SILENCER_WEREWOLF
        },
        argNumbers = 1)
public class CommandGuess implements ICommandRole {

    @Override
    public void execute(WereWolfAPI game, IPlayerWW playerWW, String[] args) {

        Player playerArg = Bukkit.getPlayer(args[0]);

        if (playerArg == null) {
            playerWW.sendMessageWithKey("werewolf.check.offline_player");
            return;
        }

        UUID argUUID = playerArg.getUniqueId();
        IPlayerWW targetWW = game.getPlayerWW(argUUID).orElse(null);

        if (targetWW == null || targetWW.isState(StatePlayer.DEATH)) {
            playerWW.sendMessageWithKey("werewolf.check.player_not_found");
            return;
        }

        if (!(playerWW.getRole() instanceof IGuesser)) {
            playerWW.sendMessageWithKey("werewolf.check.permission_denied");
            return;
        }

        IGuesser role = (IGuesser) playerWW.getRole();

        if (!role.canGuess(targetWW)) return;

        Set<Category> categories = role.getAvailableCategories();
        if (categories.isEmpty()) {
            categories = new HashSet<>();
            categories.add(Category.RING_COMMUNITY);
            categories.add(Category.NEUTRAL);
            categories.add(Category.DARK_SIDE);
            categories.add(Category.ADDONS);
        }

        Player player = Bukkit.getPlayer(playerWW.getUUID());

        if(player != null){
            GuessInventory.getInventory(targetWW, categories).open(player);
        }
    }
}
