package main.objects;

import javax.imageio.ImageIO;

import main.entities.Player;
import main.game.GamePanel;

public class Sword extends GameObject {

    public Sword() {
        name = "Sword";
        consumable = true;

        try {
            // Reuses an existing sprite as a placeholder until dedicated object art is added.
            image = ImageIO.read(getClass().getResourceAsStream("/tiles/tilesWorld_1/id_13.png"));
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
        player.equipSword();
    }
}
