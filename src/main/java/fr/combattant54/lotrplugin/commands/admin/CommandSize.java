package fr.combattant54.lotrplugin.commands.admin;

import fr.combattant54.lotrapi.annotations.AdminCommand;
import fr.combattant54.lotrapi.basekeys.Prefix;
import fr.combattant54.lotrapi.commands.ICommand;
import fr.combattant54.lotrapi.enums.StateGame;
import fr.combattant54.lotrapi.game.WereWolfAPI;
import fr.combattant54.lotrapi.player.utils.Formatter;
import fr.combattant54.lotrapi.versions.VersionUtils;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

@AdminCommand(key = "werewolf.commands.admin.size.command",
        descriptionKey = "werewolf.commands.admin.size.description",
        argNumbers = 0,
        statesGame = StateGame.LOBBY,
        moderatorAccess = true)
public class CommandSize implements ICommand {


    @Override
    public void execute(WereWolfAPI game, Player player, String[] args) {

        World world = game.getMapManager().getWorld();

        Location location = world.getSpawnLocation();
        player.sendMessage(game.translate(Prefix.YELLOW, "werewolf.commands.admin.size.begin"));
        int size = VersionUtils.getVersionUtils().biomeSize(location, world);
        player.sendMessage(game.translate(Prefix.GREEN, "werewolf.commands.admin.size.result",
                Formatter.number(size)));

        TextComponent msg = new TextComponent(game.translate(Prefix.YELLOW, "werewolf.commands.admin.size.change"));
        msg.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, String.format("/a %s", game.translate("werewolf.commands.admin.change.command"))));
        player.spigot().sendMessage(msg);
    }
}
