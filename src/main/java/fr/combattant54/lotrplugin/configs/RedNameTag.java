package fr.combattant54.lotrplugin.configs;

import fr.combattant54.lotrapi.annotations.Configuration;
import fr.combattant54.lotrapi.annotations.ConfigurationBasic;
import fr.combattant54.lotrapi.basekeys.ConfigBase;
import fr.combattant54.lotrapi.events.UpdateNameTagEvent;
import fr.combattant54.lotrapi.game.WereWolfAPI;
import fr.combattant54.lotrapi.listeners.impl.ListenerWerewolf;
import org.bukkit.Bukkit;

@Configuration(config = @ConfigurationBasic(key = ConfigBase.RED_NAME_TAG, defaultValue = true, meetUpValue = true))
public class RedNameTag extends ListenerWerewolf {

    public RedNameTag(WereWolfAPI main) {
        super(main);
    }

    @Override
    public void register(boolean isActive) {
        super.register(isActive);
        Bukkit.getOnlinePlayers().forEach(player -> Bukkit.getPluginManager().callEvent(
                new UpdateNameTagEvent(player)));
    }
}
