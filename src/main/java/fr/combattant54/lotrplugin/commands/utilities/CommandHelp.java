package fr.combattant54.lotrplugin.commands.utilities;

import fr.combattant54.lotrplugin.Register;
import fr.combattant54.lotrapi.annotations.PlayerCommand;
import fr.combattant54.lotrapi.basekeys.Prefix;
import fr.combattant54.lotrapi.commands.ICommand;
import fr.combattant54.lotrapi.game.WereWolfAPI;
import fr.combattant54.lotrapi.registers.IRegisterManager;
import fr.combattant54.lotrapi.utils.Wrapper;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.entity.Player;

@PlayerCommand(key = "werewolf.commands.player.help.command",
        descriptionKey = "")
public class CommandHelp implements ICommand {

    @Override
    public void execute(WereWolfAPI game, Player player, String[] args) {

        IRegisterManager registerManager = Register.get();

        TextComponent textComponent1 = new TextComponent(game.translate(Prefix.GREEN, "werewolf.commands.admin.help.help"));

        for (Wrapper<ICommand, PlayerCommand> command : registerManager.getPlayerCommandsRegister()) {
            if (!command.getMetaDatas().descriptionKey().isEmpty()) {

                TextComponent textComponent = new TextComponent(
                        String.format("/ww §b%s ",
                                game.translate(command.getMetaDatas().key())));

                textComponent.setHoverEvent(
                        new HoverEvent(
                                HoverEvent.Action.SHOW_TEXT,
                                new ComponentBuilder(
                                        game.translate(command.getMetaDatas().descriptionKey()))
                                        .create()));
                textComponent.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, String.format("/ww %s ",
                        game.translate(command.getMetaDatas().key()))));
                textComponent1.addExtra(textComponent);
            }

        }

        player.spigot().sendMessage(textComponent1);
    }
}
