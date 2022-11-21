package fr.ph1lou.werewolfplugin.game;


import fr.ph1lou.werewolfapi.basekeys.LoverBase;
import fr.ph1lou.werewolfapi.player.utils.Formatter;
import fr.ph1lou.werewolfapi.game.IConfiguration;
import fr.ph1lou.werewolfapi.player.interfaces.IPlayerWW;
import fr.ph1lou.werewolfapi.enums.Category;
import fr.ph1lou.werewolfapi.basekeys.ConfigBase;
import fr.ph1lou.werewolfapi.basekeys.Prefix;
import fr.ph1lou.werewolfapi.enums.StateGame;
import fr.ph1lou.werewolfapi.enums.StatePlayer;
import fr.ph1lou.werewolfapi.basekeys.TimerBase;
import fr.ph1lou.werewolfapi.events.game.game_cycle.WinEvent;
import fr.ph1lou.werewolfapi.events.game.utils.CountRemainingRolesCategoriesEvent;
import fr.ph1lou.werewolfapi.events.game.utils.EndPlayerMessageEvent;
import fr.ph1lou.werewolfapi.events.game.utils.WinConditionsCheckEvent;
import fr.ph1lou.werewolfapi.events.lovers.AroundLoverEvent;
import fr.ph1lou.werewolfapi.versions.VersionUtils;
import fr.ph1lou.werewolfplugin.Main;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public class End {

    private String winner = null;
    private final GameManager game;

    public End(GameManager game) {
        this.game = game;
    }

    public void checkVictory() {

        if(game.isDebug()){
            return;
        }

        if (game.getConfig().isConfigActive(ConfigBase.TROLL_ROLE)) return;

        if (game.isState(StateGame.END)) return;

        if (game.getPlayersCount() == 0) {
            winner = "werewolf.end.death";
            end();
            return;
        }
        IConfiguration config = game.getConfig();

        if (config.getLoverCount(LoverBase.AMNESIAC_LOVER) *
                config.getLoverCount(LoverBase.LOVER) <= 1) {

            game.getLoversManager().getLovers().stream()
                    .filter(lover -> lover.isKey(LoverBase.AMNESIAC_LOVER) || lover.isKey(LoverBase.LOVER))
                    .forEach(lover -> {
                        Set<IPlayerWW> lovers = new HashSet<>(lover.getLovers());

                        if (lover.isAlive()) {
                            AroundLoverEvent event = new AroundLoverEvent(lovers);
                            Bukkit.getPluginManager().callEvent(event);

                            if (event.getPlayerWWS().size() == game.getPlayersCount()) {
                                winner = lover.getKey();
                                end();
                            }
                        }
                    });
        }

        if (winner != null) return;

        WinConditionsCheckEvent winConditionsCheckEvent = new WinConditionsCheckEvent();
        Bukkit.getPluginManager().callEvent(winConditionsCheckEvent);

        if (winConditionsCheckEvent.isCancelled()) {
            String winnerTeam = winConditionsCheckEvent.getVictoryTeam();

            if (winnerTeam.isEmpty()) return;

            winner = winnerTeam;
            end();
            return;
        }

        CountRemainingRolesCategoriesEvent event =
                new CountRemainingRolesCategoriesEvent();

        Bukkit.getPluginManager().callEvent(event);

        if (event.getWerewolf() == game.getPlayersCount()) {
            if(event.getVillager() == 0){ //useless
                winner = Category.WEREWOLF.getKey();
                end();
                return;
            }
            else {
                Bukkit.broadcastMessage("Signalez ce Code d'erreur à Ph1Lou sur discord : 1398");
            }
        }
        if (event.getVillager() == game.getPlayersCount()) {
            if(event.getWerewolf() == 0) { //useless
                winner = Category.VILLAGER.getKey();
                end();
            }
            else {
                Bukkit.broadcastMessage("Signalez ce Code d'erreur à Ph1Lou sur discord : 1399");
            }
        }
    }

    private void end() {

        Bukkit.getPluginManager().callEvent(new WinEvent(winner,
                game.getPlayersWW()
                        .stream()
                        .filter(playerWW -> playerWW.isState(StatePlayer.ALIVE))
                        .collect(Collectors.toSet())));

        String subtitles_victory = game.translate(winner);

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
            p.sendMessage(game.translate(Prefix.ORANGE , "werewolf.end.message",
                    Formatter.format("&winner&",subtitles_victory)));
            VersionUtils.getVersionUtils().sendTitle(p, game.translate("werewolf.end.victory"), subtitles_victory, 20, 60, 20);
            TextComponent msg = new TextComponent(game.translate("werewolf.utils.bar")+ "\n" +
                    game.translate(Prefix.YELLOW,"werewolf.bug") + "\n" +
                    game.translate("werewolf.utils.bar"));
            msg.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, "https://discord.gg/GXXCVUA"));
            p.spigot().sendMessage(msg);
        }

        Bukkit.getScheduler().scheduleSyncDelayedTask(JavaPlugin.getPlugin(Main.class), game::stopGame, 20L * game.getConfig().getTimerValue(TimerBase.AUTO_RESTART_DURATION));
        Bukkit.broadcastMessage(game.translate(Prefix.ORANGE , "werewolf.announcement.restart",
                Formatter.timer(game, TimerBase.AUTO_RESTART_DURATION)));
    }




}
