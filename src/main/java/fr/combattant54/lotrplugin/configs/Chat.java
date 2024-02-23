package fr.combattant54.lotrplugin.configs;

import fr.combattant54.lotrapi.annotations.Configuration;
import fr.combattant54.lotrapi.annotations.ConfigurationBasic;
import fr.combattant54.lotrapi.basekeys.ConfigBase;

@Configuration(config = @ConfigurationBasic(key = ConfigBase.CHAT,
        defaultValue = true,
        meetUpValue = true,
        appearInMenu = false))
public class Chat {
}