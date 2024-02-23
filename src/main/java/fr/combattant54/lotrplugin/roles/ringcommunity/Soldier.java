package fr.combattant54.lotrplugin.roles.ringcommunity;

import fr.combattant54.lotrapi.annotations.Role;
import fr.combattant54.lotrapi.basekeys.RoleBase;
import fr.combattant54.lotrapi.enums.Category;
import fr.combattant54.lotrapi.enums.RoleAttribute;
import fr.combattant54.lotrapi.game.WereWolfAPI;
import fr.combattant54.lotrapi.player.interfaces.IPlayerWW;
import fr.combattant54.lotrapi.role.impl.RoleImpl;
import fr.combattant54.lotrapi.role.utils.DescriptionBuilder;
import org.jetbrains.annotations.NotNull;

@Role(
        key = RoleBase.VILLAGER,
        category = Category.RING_COMMUNITY,
        attribute = RoleAttribute.RING_COMMUNITY
)
public class Soldier extends RoleImpl {

    public Soldier(@NotNull WereWolfAPI game, @NotNull IPlayerWW playerWW) {
        super(game, playerWW);
    }

    @Override
    public @NotNull String getDescription() {
        return new DescriptionBuilder(game, this)
                .setDescription(game.translate("lotr.roles.soldier.description"))
                .build();
    }

    @Override
    public void recoverPower() {

    }
}
