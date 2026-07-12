package main.objects;

import javax.imageio.ImageIO;

import main.entities.Player;
import main.game.GamePanel;

public class Coin extends GameObject {

    public Coin() {
        name = "Coin";
        consumable = true;

        try {
            // Reuses an existing sprite as a placeholder until dedicated object art is added.
            image = ImageIO.read(getClass().getResourceAsStream("/tiles/tilesWorld_1/id_26.png"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onContact(Player player, GamePanel gp) {
        if(active == false) {
            return;
        }

        active = false;
        player.addCoin(1);
        System.out.println("Recogiste una moneda.");
    }
}
