package fr.combattant54.lotrplugin.scenarios;

import fr.combattant54.lotrapi.annotations.Scenario;
import fr.combattant54.lotrapi.basekeys.ScenarioBase;
import fr.combattant54.lotrapi.events.game.game_cycle.StartEvent;
import fr.combattant54.lotrapi.events.game.life_cycle.FinalJoinEvent;
import fr.combattant54.lotrapi.game.WereWolfAPI;
import fr.combattant54.lotrapi.listeners.impl.ListenerWerewolf;
import fr.combattant54.lotrapi.player.impl.PotionModifier;
import fr.combattant54.lotrapi.utils.BukkitUtils;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.potion.PotionEffectType;


@Scenario(key = ScenarioBase.CAT_EYES, meetUpValue = true)
public class CatEyes extends ListenerWerewolf {

    public CatEyes(WereWolfAPI api) {
        super(api);
    }

    @EventHandler
    private void onStartEvent(StartEvent event) {
        this.getGame()
                .getPlayersWW()
                .forEach(playerWW -> playerWW.addPotionModifier(PotionModifier.add(PotionEffectType.NIGHT_VISION, ScenarioBase.CAT_EYES)));
    }

    @EventHandler
    private void onFinalJoin(FinalJoinEvent event) {
        event.getPlayerWW().addPotionModifier(PotionModifier.add(PotionEffectType.NIGHT_VISION, ScenarioBase.CAT_EYES));
    }

    @Override
    public void register(boolean isActive) {


        if (isActive) {
            if (!isRegister()) {
                this.getGame().getPlayersWW().forEach(playerWW -> playerWW.addPotionModifier(PotionModifier.add(PotionEffectType.NIGHT_VISION, ScenarioBase.CAT_EYES)));
                BukkitUtils.registerListener(this);
                register = true;
            }
        } else if (isRegister()) {
            register = false;
            HandlerList.unregisterAll(this);

            this.getGame().getPlayersWW()
                    .forEach(playerWW -> playerWW
                            .addPotionModifier(
                                    PotionModifier.remove(PotionEffectType.NIGHT_VISION,
                                            ScenarioBase.CAT_EYES,
                                            0)));
        }
    }
}
