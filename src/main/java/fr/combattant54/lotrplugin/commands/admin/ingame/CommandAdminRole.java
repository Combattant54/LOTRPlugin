package fr.combattant54.lotrplugin.commands.admin.ingame;

import fr.combattant54.lotrapi.annotations.AdminCommand;
import fr.combattant54.lotrapi.basekeys.Prefix;
import fr.combattant54.lotrapi.basekeys.RoleBase;
import fr.combattant54.lotrapi.commands.ICommand;
import fr.combattant54.lotrapi.enums.StateGame;
import fr.combattant54.lotrapi.enums.StatePlayer;
import fr.combattant54.lotrapi.game.WereWolfAPI;
import fr.combattant54.lotrapi.player.interfaces.IPlayerWW;
import fr.combattant54.lotrapi.player.utils.Formatter;
import fr.combattant54.lotrapi.role.interfaces.IAffectedPlayers;
import fr.combattant54.lotrapi.role.interfaces.IPower;
import fr.combattant54.lotrapi.role.interfaces.IRole;
import fr.combattant54.lotrapi.role.interfaces.ITransformed;
import org.bukkit.entity.Player;

import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;

@AdminCommand(key = "werewolf.commands.admin.role.command",
        descriptionKey = "werewolf.commands.admin.role.description",
        statesGame = {StateGame.GAME, StateGame.END},
        argNumbers = {0, 1},
        moderatorAccess = true)
public class CommandAdminRole implements ICommand {

    @Override
    public void execute(WereWolfAPI game, Player player, String[] args) {

        UUID uuid = player.getUniqueId();
        IPlayerWW playerWW = game.getPlayerWW(uuid).orElse(null);

        if (playerWW != null &&
                playerWW.isState(StatePlayer.ALIVE)) {
            player.sendMessage(game.translate(Prefix.RED, "werewolf.commands.admin.role.in_game"));
            return;
        }

        if (args.length == 0) {
            game.getPlayersWW()
                    .stream()
                    .filter(playerWW1 -> playerWW1.isState(StatePlayer.ALIVE))
                    .forEach(playerWW1 -> player.sendMessage(game.translate("werewolf.commands.admin.role.role",
                            Formatter.player(playerWW1.getName()),
                            Formatter.role(game.translate(playerWW1.getRole().getKey())))));
            return;
        }

        AtomicReference<UUID> playerAtomicUUID = new AtomicReference<>();

        game.getPlayersWW()
                .stream()
                .filter(playerWW1 -> playerWW1.getName().equalsIgnoreCase(args[0]))
                .forEach(playerWW1 -> playerAtomicUUID.set(playerWW1.getUUID()));

        if (playerAtomicUUID.get() == null) {
            player.sendMessage(game.translate(Prefix.RED, "werewolf.check.not_in_game_player"));
            return;
        }

        UUID playerUUID = playerAtomicUUID.get();
        IPlayerWW targetWW = game.getPlayerWW(playerUUID).orElse(null);

        if (targetWW == null) {
            player.sendMessage(game.translate(Prefix.RED, "werewolf.check.not_in_game_player"));
            return;
        }

        IRole role = targetWW.getRole();
        player.sendMessage(game.translate("werewolf.commands.admin.role.role",
                Formatter.player(args[0]),
                Formatter.role(game.translate(role.getKey()))));

        if (role instanceof IPower) {
            player.sendMessage(game.translate("werewolf.commands.admin.role.power",
                    Formatter.format("&on&", ((IPower) role).hasPower())));
        }
        if (role instanceof ITransformed) {
            player.sendMessage(game.translate("werewolf.commands.admin.role.transformed",
                    Formatter.format("&on&", game.translate(((ITransformed) role).isTransformed() ?
                            "werewolf.commands.admin.role.yes" :
                            "werewolf.commands.admin.role.no"))));
        }

        StringBuilder sb = new StringBuilder();

        if (targetWW.getRole() instanceof IAffectedPlayers) {
            IAffectedPlayers affectedPlayers = (IAffectedPlayers) targetWW.getRole();

            for (IPlayerWW playerWW1 : affectedPlayers.getAffectedPlayers()) {
                if (playerWW1 != null) {
                    sb.append(playerWW1.getName()).append(" ");
                }
            }
            if (sb.length() != 0) {
                player.sendMessage(game.translate("werewolf.commands.admin.role.affected",
                        Formatter.player(sb.toString())));
            }
        }

        if (role.isKey(RoleBase.SISTER)) {
            sb = new StringBuilder();

            for (IPlayerWW playerWW1 : game.getPlayersWW()) {
                if (playerWW1.getRole().isKey(RoleBase.SISTER) && !playerWW1.equals(targetWW)) {
                    sb.append(playerWW1.getName()).append(" ");
                }
            }
            if (sb.length() != 0) {
                player.sendMessage(game.translate("werewolf.roles.sister.sisters",
                        Formatter.format("&list&", sb.toString())));

            }
        }

        if (role.isKey(RoleBase.SIAMESE_TWIN)) {
            sb = new StringBuilder();

            for (IPlayerWW playerWW1 : game.getPlayersWW()) {
                if (playerWW1.getRole().isKey(RoleBase.SIAMESE_TWIN) && !playerWW1.equals(targetWW)) {
                    sb.append(playerWW1.getName()).append(" ");
                }
            }
            if (sb.length() != 0) {
                player.sendMessage(game.translate("werewolf.roles.siamese_twin.siamese_twin",
                        Formatter.format("&list&", sb.toString())));

            }
        }

        sb = new StringBuilder();

        for (IPlayerWW playerWW1 : targetWW.getKillers()) {
            if (playerWW1 != null) {
                sb.append(playerWW1.getName()).append(" ");
            } else sb.append(game.translate("werewolf.utils.pve")).append(" ");
        }

        if (sb.length() != 0) {
            player.sendMessage(game.translate("werewolf.commands.admin.role.kill_by",
                    Formatter.player(sb.toString())));
        }
    }
}
