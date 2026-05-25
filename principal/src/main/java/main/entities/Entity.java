package main.entities;

import java.awt.image.BufferedImage;

public class Entity {
    
    public int x, y;
    public int speed;
    
    public BufferedImage up_1, up_2, down_1, down_2, left_1, left_2, right_1, right_2;
    public String direction = "down";

    public int spriteCounter = 0;
    public int spriteNum = 1;
}
