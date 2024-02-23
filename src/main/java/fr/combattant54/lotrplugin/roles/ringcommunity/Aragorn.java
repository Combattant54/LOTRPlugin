package fr.combattant54.lotrplugin.roles.ringcommunity;

import fr.combattant54.lotrapi.annotations.Role;
import fr.combattant54.lotrapi.basekeys.RoleBase;
import fr.combattant54.lotrapi.enums.Category;
import fr.combattant54.lotrapi.enums.Day;
import fr.combattant54.lotrapi.enums.RoleAttribute;
import fr.combattant54.lotrapi.game.WereWolfAPI;
import fr.combattant54.lotrapi.player.impl.PotionModifier;
import fr.combattant54.lotrapi.player.interfaces.IPlayerWW;
import fr.combattant54.lotrapi.role.impl.RoleImpl;
import fr.combattant54.lotrapi.role.utils.DescriptionBuilder;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;

@Role(
        key = RoleBase.ARAGORN,
        category = Category.RING_COMMUNITY,
        attribute = RoleAttribute.RING_COMMUNITY
)
public class Aragorn extends RoleImpl {

    public Aragorn(@NotNull WereWolfAPI game, @NotNull IPlayerWW playerWW) {
        super(game, playerWW);
    }

    @Override
    public @NotNull String getDescription() {
        return new DescriptionBuilder(game, this)
                .setDescription(game.translate("lotr.roles.aragorn.description"))
                .setEffects(game.translate("lotr.roles.aragorn.effects"))
                .build();
    }

    @Override
    public void second() {
        super.second();

        this.getPlayerWW()
                .addPotionModifier(PotionModifier.add(PotionEffectType.DAMAGE_RESISTANCE,
                        this.getKey()));

        if (this.game.isDay(Day.DAY)){
            this.getPlayerWW()
                    .addPotionModifier(PotionModifier.add(PotionEffectType.INCREASE_DAMAGE,
                            this.getKey()));
        } else {
            this.getPlayerWW()
                    .addPotionModifier(PotionModifier.remove(PotionEffectType.INCREASE_DAMAGE,
                            this.getKey(), 0));
        }
    }

    @Override
    public void recoverPower() {
    }
}
