package fr.combattant54.lotrplugin.game;


import fr.combattant54.lotrapi.basekeys.ConfigBase;
import fr.combattant54.lotrapi.basekeys.Prefix;
import fr.combattant54.lotrapi.basekeys.TimerBase;
import fr.combattant54.lotrapi.enums.Category;
import fr.combattant54.lotrapi.enums.StateGame;
import fr.combattant54.lotrapi.enums.StatePlayer;
import fr.combattant54.lotrapi.events.game.game_cycle.WinEvent;
import fr.combattant54.lotrapi.events.game.utils.EndPlayerMessageEvent;
import fr.combattant54.lotrapi.events.game.utils.WinConditionsCheckEvent;
import fr.combattant54.lotrapi.player.interfaces.IPlayerWW;
import fr.combattant54.lotrapi.player.utils.Formatter;
import fr.combattant54.lotrapi.role.interfaces.ICamp;
import fr.combattant54.lotrapi.role.interfaces.IRole;
import fr.combattant54.lotrapi.versions.VersionUtils;
import fr.combattant54.lotrplugin.Main;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.Nullable;

import java.util.Set;
import java.util.stream.Collectors;

public class End {

    private final GameManager game;
    @Nullable
    private String winner = null;

    public End(GameManager game) {
        this.game = game;
    }

    public void checkVictory() {

        if (game.isDebug()) {
            return;
        }

        if (game.getConfig().isConfigActive(ConfigBase.TROLL_ROLE)) return;

        if (game.isState(StateGame.END)) return;

        if(game.getPlayersWW().stream().anyMatch(iPlayerWW -> iPlayerWW.isState(StatePlayer.JUDGEMENT))){
            return;
        }

        Set<IRole> iRolesAlive = game.getPlayersWW().stream()
                .filter(iPlayerWW -> iPlayerWW.isState(StatePlayer.ALIVE))
                .map(IPlayerWW::getRole)
                .collect(Collectors.toSet());


        if (iRolesAlive.isEmpty()) {
            winner = "werewolf.end.death";
            end();
            return;
        }

        if (winner != null) return;

        WinConditionsCheckEvent winConditionsCheckEvent = new WinConditionsCheckEvent();
        Bukkit.getPluginManager().callEvent(winConditionsCheckEvent);

        if (!winConditionsCheckEvent.isWin()) {
            String winnerTeam = winConditionsCheckEvent.getVictoryTeam();

            if (winnerTeam.isEmpty()) return;

            winner = winnerTeam;
            end();
            return;
        }

        if(iRolesAlive.stream().noneMatch(ICamp::isNeutral)){

            if (iRolesAlive.stream().allMatch(ICamp::isWereWolf)) {
                winner = Category.DARK_SIDE.getKey();
                end();
            }
            else if (iRolesAlive.stream().noneMatch(ICamp::isWereWolf)) {
                winner = Category.RING_COMMUNITY.getKey();
                end();
            }
        }
    }

    private void end() {

        game.cleanSchedules();

        Bukkit.getPluginManager().callEvent(new WinEvent(winner,
                game.getPlayersWW()
                        .stream()
                        .filter(playerWW -> playerWW.isState(StatePlayer.ALIVE))
                        .collect(Collectors.toSet())));

        String subtitlesVictory = game.translate(winner);

        game.setState(StateGame.END);

        game.getConfig().setConfig(ConfigBase.CHAT, true);

        for (IPlayerWW playerWW1 : game.getPlayersWW()) {

            String role = game.translate(playerWW1.getDeathRole());
            String playerName = playerWW1.getName();
            StringBuilder sb = new StringBuilder();

            if (playerWW1.isState(StatePlayer.DEATH)) {
                sb.append(game.translate("werewolf.end.reveal_death",
                        Formatter.player(playerName),
                        Formatter.role(role)));
            } else {
                sb.append(game.translate("werewolf.end.reveal",
                        Formatter.player(playerName),
                        Formatter.role(role)));
            }
            playerWW1.getDeathRoles().forEach(deathRole -> {
                if (!playerWW1.getDeathRole().equals(deathRole)) {
                    sb.append(" => ").append(game.translate(deathRole));
                }
            });

            EndPlayerMessageEvent endPlayerMessageEvent = new EndPlayerMessageEvent(playerWW1, sb);
            Bukkit.getPluginManager().callEvent(endPlayerMessageEvent);

            Bukkit.broadcastMessage(sb.toString());
        }

        for (Player p : Bukkit.getOnlinePlayers()) {
            p.sendMessage(game.translate(Prefix.ORANGE, "werewolf.end.message",
                    Formatter.format("&winner&", subtitlesVictory)));
            VersionUtils.getVersionUtils().sendTitle(p, game.translate("werewolf.end.victory"), subtitlesVictory, 20, 60, 20);
            TextComponent msg = new TextComponent(game.translate("werewolf.utils.bar") + "\n" +
                    game.translate(Prefix.YELLOW, "werewolf.bug") + "\n" +
                    game.translate("werewolf.utils.bar"));
            msg.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, "https://discord.gg/GXXCVUA"));
            p.spigot().sendMessage(msg);
        }

        Bukkit.getScheduler().scheduleSyncDelayedTask(JavaPlugin.getPlugin(Main.class), game::stopGame, 20L * game.getConfig().getTimerValue(TimerBase.AUTO_RESTART_DURATION));
        Bukkit.broadcastMessage(game.translate(Prefix.ORANGE, "werewolf.announcement.restart",
                Formatter.timer(game, TimerBase.AUTO_RESTART_DURATION)));
    }


}
