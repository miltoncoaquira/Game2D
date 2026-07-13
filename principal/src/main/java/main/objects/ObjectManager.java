package main.objects;

import main.game.GamePanel;
import main.entities.Player;
import main.util.ResourceLoader;
import java.awt.Graphics2D;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

public class ObjectManager {
    private final GamePanel gp;
    public GameObject[] objects = new GameObject[50];

    public ObjectManager(GamePanel gp, String objectsPath) {
        this.gp = gp;
        loadObjects(objectsPath);
    }

    public void loadObjects(String objectsPath) {
        try (InputStream is = ResourceLoader.openStream(objectsPath);
             BufferedReader br = new BufferedReader(new InputStreamReader(is))) {
            String line;
            int index = 0;

            while((line = br.readLine()) != null && index < objects.length) {
                line = line.trim();

                if(line.isEmpty() || line.startsWith("#")) {
                    continue;
                }

                String[] data = line.split("\\s+");
                if(data.length < 3) {
                    continue;
                }

                GameObject object = createObject(data[0]);
                if(object == null) {
                    System.out.println("Objeto desconocido: " + data[0]);
                    continue;
                }

                object.worldX = Integer.parseInt(data[1]) * gp.tileSize;
                object.worldY = Integer.parseInt(data[2]) * gp.tileSize;
                objects[index] = object;
                index++;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private GameObject createObject(String objectId) {
        return switch (objectId.toUpperCase()) {
            case "AXE" -> new Axe();
            case "BOW" -> new Bow();
            case "CHEST" -> new Chest();
            case "COIN" -> new Coin();
            case "DOOR" -> new Door();
            case "KEY" -> new Key();
            case "SUPERKEY" -> new SuperKey();
            case "POTION" -> new Potion();
            case "SWORD" -> new Sword();
            case "SPEED" -> new SpeedBoost();
            default -> null;
        };
    }

    public void handleContact(int index, Player player) {
        GameObject object = objects[index];

        if(object == null || object.active == false) {
            return;
        }

        object.onContact(player, gp);

        if(object.consumable && object.active == false) {
            objects[index] = null;
        }
    }

    public void draw(Graphics2D g2) {
        for(GameObject object : objects) {
            if(object == null || object.active == false || object.image == null) {
                continue;
            }

            int screenX = object.worldX - gp.player.worldX + gp.player.screenX;
            int screenY = object.worldY - gp.player.worldY + gp.player.screenY;

            if(object.worldX > gp.player.worldX - gp.player.screenX - gp.tileSize &&
               object.worldX < gp.player.worldX + gp.player.screenX + 5 * gp.tileSize &&
               object.worldY > gp.player.worldY - gp.player.screenY - gp.tileSize &&
               object.worldY < gp.player.worldY + gp.player.screenY + 5 * gp.tileSize) {

                int renderSize = gp.tileSize * object.renderScale;
                g2.drawImage(object.image, screenX, screenY, renderSize, renderSize, null);
            }
        }
    }
}
