package main.entities;
import main.game.GamePanel;
import main.input.KeyHandler;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import javax.imageio.ImageIO;

import java.awt.Color;


public class Player extends Entity {
    
    GamePanel gp;
    KeyHandler keyH;
    public Player(GamePanel gp, KeyHandler keyH) {
        this.gp = gp;
        this.keyH = keyH;
        setDefaultValues();
        getPlayerImage();
    }

    public void setDefaultValues() {
        x = 100;
        y = 100;
        speed = 6;
        direction = "down";
    }
    public void getPlayerImage() {

        try {
        up_1 = ImageIO.read(getClass().getResourceAsStream("/player/up_1.png"));
        up_2 = ImageIO.read(getClass().getResourceAsStream("/player/up_2.png"));
        down_1 = ImageIO.read(getClass().getResourceAsStream("/player/down_1.png"));
        down_2 = ImageIO.read(getClass().getResourceAsStream("/player/down_2.png"));
        left_1 = ImageIO.read(getClass().getResourceAsStream("/player/left_1.png"));
        left_2 = ImageIO.read(getClass().getResourceAsStream("/player/left_2.png"));
        right_1 = ImageIO.read(getClass().getResourceAsStream("/player/right_1.png"));
        right_2 = ImageIO.read(getClass().getResourceAsStream("/player/right_2.png"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void update() {

        if(keyH.upPressed == true || keyH.downPressed == true || keyH.leftPressed == true || keyH.rightPressed == true) {

            if(keyH.upPressed == true) {
                direction = "up";
                y -= speed;
            }
            else if(keyH.downPressed == true) {
                direction = "down";
                y += speed;
            }
            else if(keyH.leftPressed == true) {
                direction = "left";
                x -= speed;
            }
            else if(keyH.rightPressed == true) {
                direction = "right";
                x += speed;
            }
            spriteCounter++;
            if(spriteCounter > 12) {
                if(spriteNum == 1) {
                    spriteNum = 2;
                } else if(spriteNum == 2) {
                    spriteNum = 1;
                }
                spriteCounter = 0;
            }
        }
    }

    public void draw(Graphics2D g2) {
        BufferedImage image = down_1;

        switch (direction) {
            case "up":
                if(spriteNum == 1) {
                    image = up_1;
                } else if (spriteNum == 2) {
                    image = up_2;
                }
                break;
            case "down":
                if(spriteNum == 1) {
                    image = down_1;
                } else if (spriteNum == 2) {
                    image = down_2;
                }
                break;
            case "left":
                if(spriteNum == 1) {
                    image = left_1;
                } else if (spriteNum == 2) {
                    image = left_2;
                }
                break;
            case "right":
                if(spriteNum == 1) {
                    image = right_1;
                } else if (spriteNum == 2) {
                    image = right_2;
                }
                break;
        }
        g2.drawImage(image, x, y, gp.tileSize, gp.tileSize, null);
    }



}
