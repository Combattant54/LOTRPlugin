package fr.combattant54.lotrplugin.scenarios;

import fr.combattant54.lotrapi.annotations.Scenario;
import fr.combattant54.lotrapi.basekeys.ScenarioBase;
import fr.combattant54.lotrapi.game.WereWolfAPI;
import fr.combattant54.lotrapi.listeners.impl.ListenerWerewolf;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.projectiles.ProjectileSource;

@Scenario(key = ScenarioBase.SLOW_BOW)
public class SlowBow extends ListenerWerewolf {

    public SlowBow(WereWolfAPI main) {
        super(main);
    }

    @EventHandler
    private void onPlayerDamageByEntity(EntityDamageByEntityEvent event) {

        if (!(event.getEntity() instanceof Player)) return;
        if (!(event.getDamager() instanceof Arrow)) return;

        ProjectileSource damager = ((Arrow) event.getDamager()).getShooter();

        if (!(damager instanceof Player)) return;
        Player player = (Player) event.getEntity();

        ((Player) damager).addPotionEffect(new PotionEffect(
                PotionEffectType.SPEED,
                160,
                0,
                false,
                false));
        player.removePotionEffect(PotionEffectType.SLOW);
        player.addPotionEffect(new PotionEffect(
                PotionEffectType.SLOW,
                160,
                0,
                false,
                false));
    }
}
