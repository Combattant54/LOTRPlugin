package fr.combattant54.lotrplugin.roles.ringcommunity;

import fr.combattant54.lotrapi.annotations.IntValue;
import fr.combattant54.lotrapi.annotations.Role;
import fr.combattant54.lotrapi.annotations.Timer;
import fr.combattant54.lotrapi.basekeys.IntValueBase;
import fr.combattant54.lotrapi.basekeys.RoleBase;
import fr.combattant54.lotrapi.basekeys.TimerBase;
import fr.combattant54.lotrapi.enums.Category;
import fr.combattant54.lotrapi.enums.RoleAttribute;
import fr.combattant54.lotrapi.enums.UniversalMaterial;
import fr.combattant54.lotrapi.game.WereWolfAPI;
import fr.combattant54.lotrapi.player.impl.PotionModifier;
import fr.combattant54.lotrapi.player.interfaces.IPlayerWW;
import fr.combattant54.lotrapi.player.utils.Formatter;
import fr.combattant54.lotrapi.role.impl.RoleImpl;
import fr.combattant54.lotrapi.role.interfaces.IAffectedPlayers;
import fr.combattant54.lotrapi.role.interfaces.IPower;
import fr.combattant54.lotrapi.role.utils.DescriptionBuilder;
import fr.combattant54.lotrapi.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.BlockIterator;
import org.bukkit.util.BoundingBox;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Role(
        key = RoleBase.GIMLI,
        category = Category.RING_COMMUNITY,
        attribute = RoleAttribute.RING_COMMUNITY,
        timers = {@Timer(key = TimerBase.GIMLI_COOLDOWN_AXE, defaultValue = 5*60, meetUpValue = 2*60)},
        configValues = {
                @IntValue(key = IntValueBase.GIMLI_AXE_SECONDS, defaultValue = 5, meetUpValue = 5, step = 1, item = UniversalMaterial.GOLDEN_AXE),
                @IntValue(key = IntValueBase.GIMLI_AXE_DISTANCE, defaultValue = 10, meetUpValue = 10, step=2, item = UniversalMaterial.GRAY_WOOL)
        }
)
public class Gimli extends RoleImpl implements IPower, IAffectedPlayers {
    private List<IPlayerWW> affectedPlayer = new ArrayList<>();
    private boolean havePower = true;


    public Gimli(@NotNull WereWolfAPI game, @NotNull IPlayerWW playerWW) {
        super(game, playerWW);
    }

    @Override
    public void addAffectedPlayer(IPlayerWW iPlayerWW) {
        affectedPlayer.add(iPlayerWW);
    }

    @Override
    public void removeAffectedPlayer(IPlayerWW iPlayerWW) {
        affectedPlayer.remove(iPlayerWW);
    }

    @Override
    public void clearAffectedPlayer() {
        affectedPlayer.clear();
    }

    @Override
    public List<? extends IPlayerWW> getAffectedPlayers() {
        return affectedPlayer;
    }

    @Override
    public void setPower(boolean hasPower) {
        this.havePower = hasPower;
    }

    @Override
    public boolean hasPower() {
        return havePower;
    }

    @Override
    public @NotNull String getDescription() {
        return new DescriptionBuilder(game, this)
                .setDescription(game.translate("lotr.roles.gimli.description"))
                .setEffects(game.translate("lotr.roles.gimli.effects"))
                .setItems(game.translate("lotr.roles.gimli.items",
                        Formatter.timer(Utils.conversion(game.getConfig().getTimerValue(TimerBase.GIMLI_COOLDOWN_AXE))),
                        Formatter.format("&duration&", game.getConfig().getValue(IntValueBase.GIMLI_AXE_SECONDS)),
                        Formatter.format("&distance&", game.getConfig().getValue(IntValueBase.GIMLI_AXE_DISTANCE))
                        ))
                .build();
    }

    @Override
    public void recoverPower() {
        this.havePower = true;
    }

    @EventHandler
    public void onGoldenAxeInteract(PlayerInteractEvent event){
        if (event.getItem() == null || event.getItem().getType() != Material.GOLDEN_AXE){
            return;
        }

        if (!event.getPlayer().getUniqueId().equals(this.getPlayerUUID())){
            return;
        }

        if (!this.hasPower()){
            return;
        }

        Player player = Bukkit.getPlayer(this.getPlayerUUID());
        BlockIterator iterator = new BlockIterator(
                player.getLocation(),
                1.7f,
                game.getConfig().getValue(IntValueBase.GIMLI_AXE_DISTANCE)
        );

        while (iterator.hasNext()){
            Block block = iterator.next();
            if (!block.isEmpty()){
                return;
            }
            BoundingBox box = BoundingBox.of(block.getLocation(), 0.5, 0.5, 0.5);
            IPlayerWW targetPlayer = player.getLocation().getWorld().getNearbyEntities(
                        box,
                        (Entity entity) -> {return entity.getType().equals(EntityType.PLAYER);}
                    ).stream()
                    .map((Entity entity) -> {return (Player)entity;})
                    .filter(p -> !p.getUniqueId().equals(player.getUniqueId()))
                    .sorted(Comparator.comparing((Player p) -> {return p.getLocation().distance(player.getLocation());}))
                    .map((Player p) -> {return game.getPlayerWW(p.getUniqueId());})
                    .filter(Optional::isPresent)
                    .map(Optional::get)
                    .findFirst()
                    .orElse(null);

            if (targetPlayer == null){
                continue;
            }
            int duration = game.getConfig().getValue(IntValueBase.FOX_SMELL_NUMBER);
            targetPlayer
                    .addPotionModifier(PotionModifier.add(
                            PotionEffectType.SLOW,
                            duration,
                            0,
                            this.getKey()
                    ));
            targetPlayer
                    .addPotionModifier(PotionModifier.add(
                            PotionEffectType.WEAKNESS,
                            duration,
                            0,
                            this.getKey()
                    ));
            return;
        }
    }

    @Override
    public void second() {
        super.second();

        this.getPlayerWW()
                .addPotionModifier(PotionModifier.add(
                        PotionEffectType.DAMAGE_RESISTANCE,
                        this.getKey()
                ));

    }
}
