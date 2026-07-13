package main.objects;

import main.entities.Player;
import main.game.GamePanel;
import main.util.ResourceLoader;

public class SpeedBoost extends GameObject {
    private static final String IMAGE_PATH = "/objects/speed_boost.png";
    private static final String SOUND_PATH = "/audio/sfx/null.wav";

    public SpeedBoost() {
        name = "SpeedBoost";
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
        player.addSpeed(5);
        gp.audio.playEffect(SOUND_PATH);
    }
}
