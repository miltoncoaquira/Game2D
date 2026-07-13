package main.objects;

import main.entities.Player;
import main.game.GamePanel;
import main.util.ResourceLoader;

public class Door extends GameObject {
    private static final String CLOSED_IMAGE_PATH = "/objects/door_close.png";
    private static final String OPEN_IMAGE_PATH = "/objects/door_open.png";
    private static final String SOUND_PATH = "/audio/sfx/door.wav";
    private static final long LOCKED_MESSAGE_COOLDOWN_NANOS = 750_000_000L;

    private long lastLockedMessageTime;

    public Door() {
        name = "Door";
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

        if(player.useKey() == false) {
            long currentTime = System.nanoTime();

            if(currentTime - lastLockedMessageTime >= LOCKED_MESSAGE_COOLDOWN_NANOS) {
                gp.showInteractionMessage("Necesitas una llave");
                lastLockedMessageTime = currentTime;
            }
            return;
        }

        collision = false;
        try {
            image = ResourceLoader.loadImage(OPEN_IMAGE_PATH);
        } catch (Exception e) {
            e.printStackTrace();
        }
        gp.audio.playEffect(SOUND_PATH);
        System.out.println("Abriste la puerta.");
    }
}
