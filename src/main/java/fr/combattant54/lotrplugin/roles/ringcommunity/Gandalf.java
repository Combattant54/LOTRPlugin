package fr.combattant54.lotrplugin.roles.ringcommunity;

import fr.combattant54.lotrapi.annotations.IntValue;
import fr.combattant54.lotrapi.annotations.Role;
import fr.combattant54.lotrapi.annotations.Timer;
import fr.combattant54.lotrapi.basekeys.IntValueBase;
import fr.combattant54.lotrapi.basekeys.Prefix;
import fr.combattant54.lotrapi.basekeys.RoleBase;
import fr.combattant54.lotrapi.basekeys.TimerBase;
import fr.combattant54.lotrapi.enums.Category;
import fr.combattant54.lotrapi.enums.RoleAttribute;
import fr.combattant54.lotrapi.enums.UniversalMaterial;
import fr.combattant54.lotrapi.game.WereWolfAPI;
import fr.combattant54.lotrapi.player.interfaces.IPlayerWW;
import fr.combattant54.lotrapi.player.utils.Formatter;
import fr.combattant54.lotrapi.role.impl.RoleImpl;
import fr.combattant54.lotrapi.role.interfaces.IAffectedPlayers;
import fr.combattant54.lotrapi.role.interfaces.ILimitedUse;
import fr.combattant54.lotrapi.role.interfaces.IPower;
import fr.combattant54.lotrapi.role.utils.DescriptionBuilder;
import fr.combattant54.lotrapi.utils.Utils;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

@Role(
        key = RoleBase.GANDALF,
        category = Category.RING_COMMUNITY,
        attribute = RoleAttribute.INFORMATION,
        timers = {@Timer(key = TimerBase.GANDALF_COOLDOWN_INVENTORY, defaultValue = 15*60, meetUpValue = 5*60, step = 10)},
        configValues = {
                @IntValue(key = IntValueBase.GANDALF_AMOUNT_INVENTORY, defaultValue = 3, meetUpValue = 3, step = 1, item = UniversalMaterial.ENDER_PEARL),

        }
)
public class Gandalf extends RoleImpl implements IPower, ILimitedUse, IAffectedPlayers {
    private List<IPlayerWW> affectedPlayer = new ArrayList<>();
    private int powerUsedAmount = 0;
    private boolean havePower;

    public Gandalf(@NotNull WereWolfAPI game, @NotNull IPlayerWW playerWW) {
        super(game, playerWW);
    }

    public void specInventory(IPlayerWW player){
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
    public int getUse() {
        return powerUsedAmount;
    }

    @Override
    public void setUse(int i) {
        powerUsedAmount = i;
    }

    @Override
    public void setPower(boolean b) {
        havePower = b;
    }

    @Override
    public boolean hasPower() {
        return havePower;
    }

    @Override
    public @NotNull String getDescription() {
        return new DescriptionBuilder(game, this)
                .setDescription(game.translate("lotr.roles.gandalf.description",
                        Formatter.timer(Utils.conversion(game.getConfig().getTimerValue(TimerBase.GANDALF_COOLDOWN_INVENTORY))),
                        Formatter.format("&amount&", game.getConfig().getValue(IntValueBase.GANDALF_AMOUNT_INVENTORY))
                        ))
                .build();
    }

    @Override
    public void recoverPower() {
        if (!this.hasPower()){
            this.getPlayerWW().sendMessageWithKey(Prefix.YELLOW, "werewolf.roles.bloodthirsty_werewolf.recover_power");
            this.havePower = true;
        }
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onClickInventory(InventoryClickEvent event){
        if (!event.getWhoClicked().getType().equals(EntityType.PLAYER)){
            return;
        }
        IPlayerWW player = this.game.getPlayerWW(((Player) event.getWhoClicked()).getUniqueId()).orElse(null);

        if(player == null || !this.getPlayerUUID().equals(player.getUUID())){
            return;
        }

        if (!event.getClickedInventory().getType().equals(InventoryType.PLAYER)){
            return;
        }

        Inventory playerInv = event.getClickedInventory();
        if (!(playerInv.getHolder() instanceof Player)){
            return;
        }

        IPlayerWW invOwner = this.game.getPlayerWW(((Player) playerInv.getHolder()).getUniqueId()).orElse(null);
        if (invOwner == null){
            return;
        }

        if (!this.getAffectedPlayers().contains(invOwner)){
            return;
        }

        event.setCancelled(true);

    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onInventoryClose(InventoryCloseEvent event){
        if (!event.getInventory().getType().equals(InventoryType.PLAYER)){
            return;
        }

        if (!event.getPlayer().getType().equals(EntityType.PLAYER)){
            return;
        }
        IPlayerWW player = this.game.getPlayerWW(((Player)event.getPlayer()).getUniqueId()).orElse(null);
        if (player == null){
            return;
        }

        if (!(event.getInventory().getHolder() instanceof Player)){
            return;
        }
        IPlayerWW invOwner = this.game.getPlayerWW(((Player) event.getInventory().getHolder()).getUniqueId()).orElse(null);

        if (invOwner == null || !this.affectedPlayer.contains(invOwner)){
            return;
        }

        this.removeAffectedPlayer(invOwner);

    }
}
