package main.entities;

import main.game.GamePanel;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;

public class Item extends Entity {

    private final GamePanel gp;
    public boolean collected = false;
    public int value = 10;
    private final int scale = 2;
    private BufferedImage itemImage;

    public Item(GamePanel gp, double worldX, double worldY) {
        this.gp = gp;
        this.worldX = worldX;
        this.worldY = worldY;
        this.solidArea = new Rectangle(8, 8, 32, 32);
        loadImage();
    }

    private void loadImage() {
        try {
            itemImage = ImageIO.read(getClass().getResourceAsStream("/entities/item.png"));
        } catch (Exception e) {
            itemImage = null;
        }
    }

    public Rectangle getWorldBounds() {
        return new Rectangle((int)worldX + solidArea.x, (int)worldY + solidArea.y, solidArea.width, solidArea.height);
    }

    public void collect() {
        collected = true;
    }

    public void draw(Graphics2D g2) {
        if (collected) {
            return;
        }

        int screenX = (int)(worldX - gp.player.worldX + gp.player.screenX);
        int screenY = (int)(worldY - gp.player.worldY + gp.player.screenY);

        if (worldX > gp.player.worldX - gp.player.screenX - gp.tileSize
                && worldX < gp.player.worldX + gp.player.screenX + gp.tileSize
                && worldY > gp.player.worldY - gp.player.screenY - gp.tileSize
                && worldY < gp.player.worldY + gp.player.screenY + gp.tileSize) {
            if (itemImage != null) {
                g2.drawImage(itemImage, (int)screenX, (int)screenY, gp.tileSize * scale, gp.tileSize * scale, null);
            } else {
                g2.fillOval((int)screenX + 8, (int)screenY + 8, gp.tileSize, gp.tileSize);
            }
        }
    }
}