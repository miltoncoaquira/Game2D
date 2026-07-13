package main.objects;

import java.awt.Rectangle;
import java.awt.image.BufferedImage;

import main.entities.Player;
import main.game.GamePanel;

public class GameObject {
    public String name;
    public BufferedImage image;
    public int worldX, worldY;
    public boolean collision = false;
    public boolean consumable = false;
    public boolean active = true;
    public int renderScale = 1;
    public Rectangle solidArea = new Rectangle(0, 0, 48, 48);

    public void setRenderScale(int renderScale) {
        this.renderScale = renderScale;
        int size = 48 * renderScale;
        solidArea = new Rectangle(0, 0, size, size);
    }

    public void onContact(Player player, GamePanel gp) {
    }
}
