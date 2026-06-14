package main.entities;

import java.awt.image.BufferedImage;

public class Entity {
    
    public int x, y;
    public int speed;
    
    public BufferedImage up_1, up_m, up_2, down_1, down_m, down_2, left_1,
    left_m, left_2, right_1, right_m, right_2, test, test_1, test_2;
    public String direction;

    public int spriteCounter = 0;
    public int spriteNum = 1;
}
