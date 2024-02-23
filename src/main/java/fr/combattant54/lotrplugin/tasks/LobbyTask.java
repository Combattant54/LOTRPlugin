package fr.combattant54.lotrplugin.tasks;

import fr.combattant54.lotrplugin.game.GameManager;
import fr.combattant54.lotrapi.enums.StateGame;
import fr.combattant54.lotrapi.utils.BukkitUtils;
import org.bukkit.scheduler.BukkitRunnable;


public class LobbyTask extends BukkitRunnable {


    private final GameManager game;

    public LobbyTask(GameManager game) {
        this.game = game;
    }

    @Override
    public void run() {

        if (game.isState(StateGame.END)) {
            cancel();
            return;
        }

        game.getScore().updateBoard();

        if (game.isState(StateGame.TRANSPORTATION)) {
            cancel();
            BukkitUtils.registerListener(new TransportationTask(game));
        }
    }
}
