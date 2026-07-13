package main.objects;

import main.entities.Player;
import main.game.GamePanel;
import main.util.ResourceLoader;

public class Key extends GameObject {
    private static final String IMAGE_PATH = "/objects/key.png";
    private static final String SOUND_PATH = "/audio/sfx/key.wav";

    public Key() {
        name = "Key";
        consumable = true;

        try {
            image = ResourceLoader.loadImage(IMAGE_PATH);
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
        player.addKey(1);
        gp.audio.playEffect(SOUND_PATH);
        System.out.println("Recogiste una llave.");
    }
}
