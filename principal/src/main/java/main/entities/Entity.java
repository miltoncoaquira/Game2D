package main.entities;

import java.awt.Rectangle;
import java.awt.image.BufferedImage;

public class Entity {
    
    public int worldX, worldY;
    public int speed;
    
    public BufferedImage up_1, up_m, up_2, down_1, down_m, down_2, left_1,
    left_m, left_2, right_1, right_m, right_2;
    public String direction;
    public Rectangle solidArea = new Rectangle(0, 0, 48, 48);

    public int spriteCounter = 0;
    public int spriteNum = 1;
}
