package fr.combattant54.lotrplugin.roles.darkside;

import fr.combattant54.lotrapi.annotations.Role;
import fr.combattant54.lotrapi.basekeys.RoleBase;
import fr.combattant54.lotrapi.enums.Aura;
import fr.combattant54.lotrapi.enums.Category;
import fr.combattant54.lotrapi.enums.RoleAttribute;
import fr.combattant54.lotrapi.game.WereWolfAPI;
import fr.combattant54.lotrapi.player.interfaces.IPlayerWW;
import fr.combattant54.lotrapi.role.impl.RoleWereWolf;
import fr.combattant54.lotrapi.role.utils.DescriptionBuilder;
import org.jetbrains.annotations.NotNull;

@Role(
        key= RoleBase.WEREWOLF,
        defaultAura = Aura.DARK,
        category = Category.DARK_SIDE,
        attribute = RoleAttribute.DARK_SIDE
)
public class Ork extends RoleWereWolf {
    public Ork(WereWolfAPI game, IPlayerWW playerWW) {
        super(game, playerWW);
    }

    @Override
    public @NotNull String getDescription() {
        return new DescriptionBuilder(game, this)
                .setDescription(game.translate("lort.description.ork.description"))
                .setEffects(game.translate("lotr.description.ork.effects"))
                .build();
    }

    @Override
    public void recoverPower() {

    }
}
