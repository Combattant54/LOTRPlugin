package fr.combattant54.lotrplugin.commands.utilities;

import fr.combattant54.lotrapi.annotations.PlayerCommand;
import fr.combattant54.lotrapi.basekeys.Prefix;
import fr.combattant54.lotrapi.commands.ICommand;
import fr.combattant54.lotrapi.game.WereWolfAPI;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.entity.Player;

@PlayerCommand(key = "werewolf.commands.player.doc.command",
        descriptionKey = "werewolf.commands.player.doc.description",
        argNumbers = 0)
public class CommandDoc implements ICommand {

    @Override
    public void execute(WereWolfAPI game, Player player, String[] args) {

        TextComponent textComponent1 = new TextComponent(game.translate(Prefix.ORANGE, "werewolf.commands.player.doc.link"));

        textComponent1.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, game.translate("werewolf.description.doc")));

        player.spigot().sendMessage(textComponent1);
    }
}
