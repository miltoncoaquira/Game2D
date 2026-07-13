package main.objects;

import main.entities.Player;
import main.game.GamePanel;
import main.util.ResourceLoader;

public class Chest extends GameObject {
    private static final String CLOSED_IMAGE_PATH = "/objects/chest_closed.png";
    private static final String OPEN_IMAGE_PATH = "/objects/chest_open.png";
    private static final String SOUND_PATH = "/audio/sfx/chest.wav";
    private static final long LOCKED_MESSAGE_COOLDOWN_NANOS = 750_000_000L;

    private long lastLockedMessageTime;

    public Chest() {
        name = "Chest";
        collision = true;
        consumable = false;
        setRenderScale(2);

        try {
            image = ResourceLoader.loadImage(CLOSED_IMAGE_PATH);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onContact(Player player, GamePanel gp) {
        if(collision == false) {
            return;
        }

        if(player.useSuperKey() == false) {
            long currentTime = System.nanoTime();

            if(currentTime - lastLockedMessageTime >= LOCKED_MESSAGE_COOLDOWN_NANOS) {
                gp.showInteractionMessage("Necesitas una llave maestra");
                lastLockedMessageTime = currentTime;
            }
            return;
        }

        collision = false;
        gp.audio.playEffect(SOUND_PATH);
        gp.showCongratulations();
        player.addCoin(3);

        try {
            image = ResourceLoader.loadImage(OPEN_IMAGE_PATH);
        } catch (Exception e) {
            e.printStackTrace();
        }

        System.out.println("Abriste el cofre y encontraste 3 monedas.");
    }
}
