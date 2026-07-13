package main.entities;

import java.awt.Rectangle;
import java.awt.image.BufferedImage;

public class Entity {
    
    public int worldX, worldY;
    public int speed;
    
    public BufferedImage[] sprites = new BufferedImage[16];
    public String direction;
    public Rectangle solidArea = new Rectangle(0, 0, 48, 48);

    public int spriteCounter = 0;
    public int spriteNum = 1;
}
