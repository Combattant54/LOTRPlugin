package fr.combattant54.lotrplugin.save;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import fr.combattant54.lotrplugin.game.Configuration;
import fr.combattant54.lotrplugin.game.StorageConfiguration;
import fr.combattant54.lotrapi.game.IConfiguration;
import fr.combattant54.lotrapi.statistics.impl.GameReview;

public class Serializer {


    public static Gson gson() {
        return new GsonBuilder()
                .setPrettyPrinting()
                .disableHtmlEscaping()
                .create();
    }

    public static String serialize(IConfiguration config) {
        return gson().toJson(config);
    }

    public static String serialize(StorageConfiguration config) {
        return gson().toJson(config);
    }


    public static String serialize(GameReview game) {
        return gson().toJson(game);
    }

    public static Configuration deserialize(String json) {
        return gson().fromJson(json, Configuration.class);
    }

    public static StorageConfiguration deserializeConfiguration(String json) {
        return gson().fromJson(json, StorageConfiguration.class);
    }

}
