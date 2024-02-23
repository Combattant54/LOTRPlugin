package fr.combattant54.lotrplugin.commands.utilities;

import fr.combattant54.lotrplugin.Register;
import fr.combattant54.lotrapi.annotations.PlayerCommand;
import fr.combattant54.lotrapi.basekeys.ConfigBase;
import fr.combattant54.lotrapi.basekeys.Prefix;
import fr.combattant54.lotrapi.commands.ICommand;
import fr.combattant54.lotrapi.enums.Category;
import fr.combattant54.lotrapi.game.WereWolfAPI;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.concurrent.atomic.AtomicBoolean;

@PlayerCommand(key = "werewolf.commands.player.compo.command",
        descriptionKey = "werewolf.commands.player.compo.description",
        argNumbers = 0)
public class CommandComposition implements ICommand {

    @Override
    public void execute(WereWolfAPI game, Player player, String[] args) {


        if (game.getConfig().isConfigActive(ConfigBase.HIDE_COMPOSITION)) {

            player.sendMessage(game.translate(Prefix.RED, "werewolf.commands.player.compo.composition_hide"));

            return;
        }

        StringBuilder sb = new StringBuilder(game.translate("werewolf.commands.player.compo._"));
        sb.append('\n');
        sb.append(ChatColor.WHITE);

        sb.append(getCompo(game, Category.DARK_SIDE));
        sb.append(getCompo(game, Category.RING_COMMUNITY));
        sb.append(getCompo(game, Category.NEUTRAL));

        sb.append(game.translate("werewolf.commands.player.compo._"));
        player.sendMessage(sb.toString());
    }

    public String getCompo(WereWolfAPI game, Category category) {

        StringBuilder sb = new StringBuilder(category.getChatColor() + game.translate(category.getKey()));
        AtomicBoolean atomicBoolean = new AtomicBoolean(false);
        sb.append("§f : ");

        Register.get().getRolesRegister().stream()
                .filter(roleRegister -> roleRegister.getMetaDatas()
                                                .category() == category)
                .forEach(roleRegister -> {
                    String key = roleRegister.getMetaDatas().key();
                    int number = game.getConfig().getRoleCount(key);
                    if (number > 0) {
                        if (number == 1) {
                            sb.append(game.translate(roleRegister.getMetaDatas().key()))
                                    .append(", ");
                        } else {
                            sb.append(game.translate(roleRegister.getMetaDatas().key()))
                                    .append(" (§b").append(game.getConfig().getRoleCount(key))
                                    .append("§f), ");
                        }
                        atomicBoolean.set(true);
                    }
                });
        sb.replace(sb.length() - 2, sb.length(), "");
        sb.append("\n");
        if (!atomicBoolean.get()) {
            return "";
        }
        return sb.toString();
    }
}
