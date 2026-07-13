package main.objects;

import main.entities.Player;
import main.game.GamePanel;
import main.util.ResourceLoader;

public class SuperKey extends GameObject {
    private static final String IMAGE_PATH = "/objects/super_key.png";
    private static final String SOUND_PATH = "/audio/sfx/superkey.wav";

    public SuperKey() {
        name = "SuperKey";
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
        player.addSuperKey(1);
        gp.audio.playEffect(SOUND_PATH);
        System.out.println("Recogiste una SuperKey.");
    }
}
