package fr.combattant54.lotrplugin.listeners;

import fr.combattant54.lotrapi.events.game.life_cycle.DeathItemsEvent;
import fr.combattant54.lotrapi.events.game.life_cycle.ResurrectionEvent;
import fr.combattant54.lotrapi.events.game.utils.EnchantmentEvent;
import fr.combattant54.lotrapi.events.game.utils.GoldenAppleParticleEvent;
import fr.combattant54.lotrapi.events.roles.InvisibleEvent;
import fr.combattant54.lotrapi.events.roles.StealEvent;
import fr.combattant54.lotrapi.game.WereWolfAPI;
import fr.combattant54.lotrapi.role.interfaces.IInvisible;
import org.bukkit.Bukkit;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class InvisibleListener implements Listener {


    private final WereWolfAPI game;

    //todo à tester

    public InvisibleListener(WereWolfAPI game) {
        this.game = game;
    }

    @EventHandler
    public void onEnchantment(EnchantmentEvent event) {

        if (!(event.getPlayerWW().getRole() instanceof IInvisible)) {
            return;
        }

        if (event.getEnchants().containsKey(Enchantment.KNOCKBACK)) {
            event.getFinalEnchants().put(Enchantment.KNOCKBACK,
                    Math.min(event.getEnchants().get(Enchantment.KNOCKBACK),
                            game.getConfig().getLimitKnockBack()));
        }
    }

    //Remove golden particle when invisible role become invisible and config set to 1
    @EventHandler
    public void onInvisibleRemoveGoldenParticle(InvisibleEvent event) {

        if (game.getConfig().getGoldenAppleParticles() != 1) {
            return;
        }

        Player player = Bukkit.getPlayer(event.getPlayerWW().getUUID());

        if (player == null) {
            return;
        }

        event.getPlayerWW().getPotionModifiers()
                .forEach(potionModifier -> {
                    if (potionModifier.getPotionEffectType() == PotionEffectType.ABSORPTION) {
                        if (event.isInvisible()) {
                            player.removePotionEffect(PotionEffectType.ABSORPTION);
                            player.addPotionEffect(new PotionEffect(PotionEffectType.ABSORPTION,
                                    potionModifier.getDuration() - (game.getTimer() - potionModifier.getTimer()) * 20,
                                    potionModifier.getAmplifier(), false, false));
                        } else {
                            player.removePotionEffect(PotionEffectType.ABSORPTION);
                            player.addPotionEffect(new PotionEffect(PotionEffectType.ABSORPTION,
                                    potionModifier.getDuration() - (game.getTimer() - potionModifier.getTimer()) * 20,
                                    potionModifier.getAmplifier()));
                        }
                    }
                });


    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onResurrection(ResurrectionEvent event) {

        if (!(event.getPlayerWW().getRole() instanceof IInvisible)) {
            return;
        }

        if (event.getPlayerWW().getPotionModifiers()
                .stream()
                .noneMatch(potionModifier -> potionModifier.getPotionEffectType() == PotionEffectType.INVISIBILITY)) {
            return;
        }

        ((IInvisible) event.getPlayerWW().getRole()).setInvisible(false);
    }

    @EventHandler
    public void onFinalDeath(DeathItemsEvent event) {

        if (!(event.getPlayerWW().getRole() instanceof IInvisible)) {
            return;
        }

        ((IInvisible) event.getPlayerWW().getRole()).setInvisible(false);

        if (!this.game.getConfig().isKnockBackForInvisibleRoleOnly()) return;

        for (ItemStack i : event.getItems()) {
            if (i != null) {
                i.removeEnchantment(Enchantment.KNOCKBACK);
            }
        }
    }

    @EventHandler
    public void onStealEvent(StealEvent event) {

        if (!(event.getPlayerWW().getRole() instanceof IInvisible)) {
            return;
        }

        ((IInvisible) event.getPlayerWW().getRole()).setInvisible(false);
    }

    @EventHandler
    public void onGoldenAppleEat(GoldenAppleParticleEvent event) {

        if (!(event.getPlayerWW().getRole() instanceof IInvisible)) {
            return;
        }

        if (!((IInvisible) event.getPlayerWW().getRole()).isInvisible()) return;

        event.setCancelled(true);
    }
}
