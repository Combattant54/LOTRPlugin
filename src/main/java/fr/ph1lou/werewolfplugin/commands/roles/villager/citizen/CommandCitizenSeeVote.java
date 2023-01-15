package fr.ph1lou.werewolfplugin.commands.roles.villager.citizen;

import fr.ph1lou.werewolfapi.annotations.RoleCommand;
import fr.ph1lou.werewolfapi.basekeys.ConfigBase;
import fr.ph1lou.werewolfapi.basekeys.Prefix;
import fr.ph1lou.werewolfapi.basekeys.RoleBase;
import fr.ph1lou.werewolfapi.commands.ICommandRole;
import fr.ph1lou.werewolfapi.enums.VoteStatus;
import fr.ph1lou.werewolfapi.events.roles.citizen.CitizenSeeVoteEvent;
import fr.ph1lou.werewolfapi.events.roles.citizen.CitizenSeeWerewolfVoteEvent;
import fr.ph1lou.werewolfapi.game.WereWolfAPI;
import fr.ph1lou.werewolfapi.player.interfaces.IPlayerWW;
import fr.ph1lou.werewolfapi.player.utils.Formatter;
import fr.ph1lou.werewolfplugin.roles.villagers.Citizen;
import org.bukkit.Bukkit;

import java.util.UUID;

@RoleCommand(key = "werewolf.roles.citizen.command_1",
        roleKeys = RoleBase.CITIZEN,
        argNumbers = 0,
        autoCompletion = false)
public class CommandCitizenSeeVote implements ICommandRole {
    @Override
    public void execute(WereWolfAPI game, IPlayerWW playerWW, String[] args) {

        Citizen citizen = (Citizen) playerWW.getRole();
        if (!game.getConfig().isConfigActive(ConfigBase.NEW_VOTE)) {
            this.oldVote(game, playerWW, citizen);
            return;
        }
        if (args.length == 0) {
            return;
        }
        this.newVote(game, playerWW, citizen, args[0]);
    }

    private void newVote(WereWolfAPI game, IPlayerWW playerWW, Citizen citizen, String result) {
        UUID uuid;
        try {
            uuid = UUID.fromString(result);
        } catch (IllegalArgumentException ignored) {
            return;
        }
        IPlayerWW playerWW2 = game.getPlayerWW(uuid).orElse(null);
        if (playerWW2 == null) {
            return;
        }
        if (!citizen.hasPower()) {
            playerWW.sendMessageWithKey(Prefix.RED, "werewolf.check.power");
            return;
        }
        citizen.setPower(false);
        CitizenSeeWerewolfVoteEvent event1 = new CitizenSeeWerewolfVoteEvent(playerWW, playerWW2);
        Bukkit.getPluginManager().callEvent(event1);
        if (event1.isCancelled()) {
            playerWW.sendMessageWithKey(Prefix.RED, "werewolf.check.cancel");
            return;
        }
        playerWW.sendMessageWithKey(Prefix.RED, "werewolf.configurations.vote.new_vote_werewolf",
                Formatter.player(event1.getTargetWW().getName()));
    }

    private void oldVote(WereWolfAPI game, IPlayerWW playerWW, Citizen citizen) {
        if (citizen.getUse() >= 2) {
            playerWW.sendMessageWithKey(Prefix.RED, "werewolf.check.power");
            return;
        }
        if (!game.getVoteManager().isStatus(VoteStatus.WAITING)) {
            playerWW.sendMessageWithKey(Prefix.RED, "werewolf.check.power");
            return;
        }
        citizen.setUse(citizen.getUse() + 1);
        CitizenSeeVoteEvent seeVoteEvent = new CitizenSeeVoteEvent(playerWW, game.getVoteManager().getVotes());
        Bukkit.getPluginManager().callEvent(seeVoteEvent);
        if (seeVoteEvent.isCancelled()) {
            playerWW.sendMessageWithKey(Prefix.RED, "werewolf.check.cancel");
            return;
        }
        playerWW.sendMessageWithKey(Prefix.GREEN, "werewolf.roles.citizen.count_votes");

        game.getVoteManager().getPlayerVotes()
                .forEach((voterWW, voteWW) -> playerWW.sendMessageWithKey("werewolf.roles.citizen.see_vote",
                        Formatter.format("&voter&", voterWW.getName()),
                        Formatter.player(voteWW.getName())));
    }
}
