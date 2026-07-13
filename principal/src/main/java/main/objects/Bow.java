package main.objects;

import main.entities.Player;
import main.game.GamePanel;
import main.util.ResourceLoader;

public class Bow extends GameObject {
    private static final String IMAGE_PATH = "/objects/bow.png";
    private static final String SOUND_PATH = "/audio/sfx/null.wav";

    public Bow() {
        name = "Bow";
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
        player.equipBow();
        gp.audio.playEffect(SOUND_PATH);
    }
}
