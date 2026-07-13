package main.objects;

import main.entities.Player;
import main.game.GamePanel;
import main.util.ResourceLoader;

public class Potion extends GameObject {
    private static final String IMAGE_PATH = "/objects/potion.png";
    private static final String SOUND_PATH = "/audio/sfx/null.wav";

    public Potion() {
        name = "Potion";
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
        player.addSpeed(2);
        gp.audio.playEffect(SOUND_PATH);
        System.out.println("Tomaste una pocion. Tu velocidad aumento un poco.");
    }
}
