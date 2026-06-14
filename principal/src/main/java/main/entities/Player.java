package main.entities;
import main.game.GamePanel;
import main.input.KeyHandler;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import javax.imageio.ImageIO;

// import java.awt.Color;


public class Player extends Entity {
    
    GamePanel gp;
    KeyHandler keyH;
    int scalePlayer = 2;

    public Player(GamePanel gp, KeyHandler keyH) {
        this.gp = gp;
        this.keyH = keyH;
        setDefaultValues();
        getPlayerImage();
    }

    public void setDefaultValues() {
        x = 16;
        y = 16;
        speed = 6;
        direction = "down";
    }
    
    public void getPlayerImage() {

        try {
        up_1 = ImageIO.read(getClass().getResourceAsStream("/player/up_1.png"));
        up_m = ImageIO.read(getClass().getResourceAsStream("/player/up_m.png"));
        up_2 = ImageIO.read(getClass().getResourceAsStream("/player/up_2.png"));
        down_1 = ImageIO.read(getClass().getResourceAsStream("/player/down_1.png"));
        down_m = ImageIO.read(getClass().getResourceAsStream("/player/down_m.png"));
        down_2 = ImageIO.read(getClass().getResourceAsStream("/player/down_2.png"));
        left_1 = ImageIO.read(getClass().getResourceAsStream("/player/left_1.png"));
        left_m = ImageIO.read(getClass().getResourceAsStream("/player/left_m.png"));
        left_2 = ImageIO.read(getClass().getResourceAsStream("/player/left_2.png"));
        right_1 = ImageIO.read(getClass().getResourceAsStream("/player/right_1.png"));
        right_m = ImageIO.read(getClass().getResourceAsStream("/player/right_m.png"));
        right_2 = ImageIO.read(getClass().getResourceAsStream("/player/right_2.png"));
        // test = ImageIO.read(getClass().getResourceAsStream("/player/test_5.png"));
        // test_1 = ImageIO.read(getClass().getResourceAsStream("/player/test_2.png"));
        // test_2 = ImageIO.read(getClass().getResourceAsStream("/player/New Piskel ().png"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void update() {
        
        if(keyH.speedUpPressed == true) {
            if(speed < 20) {
                speed += 1;
                System.out.println("Velocidad: " + speed);
            }
            keyH.speedUpPressed = false;
        }
        
        if(keyH.speedDownPressed == true) {
            if(speed > 1) {
                speed -= 1;
                System.out.println("Velocidad: " + speed);
            }
            keyH.speedDownPressed = false;
        }

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
            if(spriteCounter > 12 ) {
                if(spriteNum == 1) {
                    spriteNum = 2;
                } else if(spriteNum == 2) {
                    spriteNum = 3;
                } else if(spriteNum == 3) {
                    spriteNum = 4;
                } else {
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
                switch (spriteNum) {
                    case 1:
                        image = up_1;
                        break;
                    case 2:
                        image = up_m;
                        break;
                    case 3:
                        image = up_2;
                        break;
                    case 4:
                        image = up_m;
                        break;
                }
            break;    
            case "down":
                switch (spriteNum) {
                    case 1:
                        image = down_1;
                        break;
                    case 2:
                        image = down_m;
                        break;
                    case 3:
                        image = down_2;
                        break;
                    case 4:
                        image = down_m;
                        break;
                }
            break;
            case "left":
                switch (spriteNum) {
                    case 1:
                        image = left_1;
                        break;
                    case 2:
                        image = left_m;
                        break;
                    case 3:
                        image = left_2;
                        break;
                    case 4:
                        image = left_m;
                        break;
                }
            break;
            case "right":
                switch (spriteNum) {
                    case 1:
                        image = right_1;
                        break;
                    case 2:
                        image = right_m;
                        break;
                    case 3:
                        image = right_2;
                        break;
                    case 4:
                        image = right_m;
                        break;
                }
                break;
        }
        g2.drawImage(image, x, y, scalePlayer * gp.tileSize, scalePlayer * gp.tileSize, null);
    }
    public void drawTest(Graphics2D g2) {
        g2.drawImage(test, x, y, null);
        // g2.drawImage(test_1, x+32, y+32, null);
        // g2.drawImage(test_2, x+64, y+64, null);
    }    
}