package main.objects;

import javax.imageio.ImageIO;

import main.entities.Player;
import main.game.GamePanel;

public class Door extends GameObject {

    public Door() {
        name = "Door";
        collision = true;
        consumable = false;

        try {
            // Reuses an existing sprite as a placeholder until dedicated object art is added.
            image = ImageIO.read(getClass().getResourceAsStream("/tiles/tilesWorld_1/id_07.png"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onContact(Player player, GamePanel gp) {
        if(collision == false) {
            return;
        }

        collision = false;
        System.out.println("Abriste la puerta.");
    }
}
