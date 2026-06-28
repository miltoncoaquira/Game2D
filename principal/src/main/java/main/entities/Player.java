package main.entities;
import main.game.GamePanel;
import main.input.KeyHandler;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;

import javax.imageio.ImageIO;

// import java.awt.Color;


public class Player extends Entity {
    
    private GamePanel gp;
    private KeyHandler keyH;
    private int scalePlayer = 2;
    public final int screenX ;
    public final int screenY ;
    private String playerSprites;

    public Player(GamePanel gp, KeyHandler keyH, String playerSprites) {
        this.gp = gp;
        this.keyH = keyH;
        screenX = gp.screenWidth/2 - (gp.tileSize*scalePlayer)/2;
        screenY = gp.screenHeight/2 - (gp.tileSize*scalePlayer)/2;
        this.playerSprites = playerSprites;

        setDefaultValues();
        getPlayerImage();
    }

    public void setDefaultValues() {
        worldX = gp.tileSize * (gp.maxWorldCol/2 -1);
        worldY = gp.tileSize * (gp.maxWorldRow/2 -9);
        speed = 6;
        direction = "down_m";
        solidArea = new Rectangle(24, 48, 36, 36);
    }
    
    public void getPlayerImage() {


        try {
        up_1 = ImageIO.read(getClass().getResourceAsStream( "/player/" + playerSprites + "/up_1.png"));
        up_m = ImageIO.read(getClass().getResourceAsStream( "/player/" + playerSprites + "/up_m.png"));
        up_2 = ImageIO.read(getClass().getResourceAsStream( "/player/" + playerSprites + "/up_2.png"));
        down_1 = ImageIO.read(getClass().getResourceAsStream( "/player/" + playerSprites + "/down_1.png"));
        down_m = ImageIO.read(getClass().getResourceAsStream( "/player/" + playerSprites + "/down_m.png"));
        down_2 = ImageIO.read(getClass().getResourceAsStream( "/player/" + playerSprites + "/down_2.png"));
        left_1 = ImageIO.read(getClass().getResourceAsStream( "/player/" + playerSprites + "/left_1.png"));
        left_m = ImageIO.read(getClass().getResourceAsStream( "/player/" + playerSprites + "/left_m.png"));
        left_2 = ImageIO.read(getClass().getResourceAsStream( "/player/" + playerSprites + "/left_2.png"));
        right_1 = ImageIO.read(getClass().getResourceAsStream( "/player/" + playerSprites + "/right_1.png"));
        right_m = ImageIO.read(getClass().getResourceAsStream( "/player/" + playerSprites + "/right_m.png"));
        right_2 = ImageIO.read(getClass().getResourceAsStream( "/player/" + playerSprites + "/right_2.png"));


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
            int nextWorldX = worldX;
            int nextWorldY = worldY;

            if(keyH.upPressed == true) {
                direction = "up";
                nextWorldY -= speed;
            }
            else if(keyH.downPressed == true) {
                direction = "down";
                nextWorldY += speed;
            }
            else if(keyH.leftPressed == true) {
                direction = "left";
                nextWorldX -= speed;
            }
            else if(keyH.rightPressed == true) {
                direction = "right";
                nextWorldX += speed;
            }

            if(gp.cChecker.checkTile(this, nextWorldX, nextWorldY) == false) {
                worldX = nextWorldX;
                worldY = nextWorldY;
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
        BufferedImage image = down_m;

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
        g2.drawImage(image, screenX, screenY, scalePlayer * gp.tileSize, scalePlayer * gp.tileSize, null);
    }

    public void drawTest(Graphics2D g2) {
        try {
        test = ImageIO.read(getClass().getResourceAsStream("/player/test.png"));
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        g2.drawImage(test, screenX, screenY,scalePlayer * gp.tileSize, scalePlayer * gp.tileSize, null);

    }    
}
