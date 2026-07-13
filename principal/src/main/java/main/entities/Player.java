package main.entities;
import main.game.GamePanel;
import main.input.KeyHandler;
import main.util.ResourceLoader;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;

public class Player extends Entity {
    private static final String STEP_SOUND_PATH = "/audio/sfx/null.wav";
    private static final long STEP_SOUND_COOLDOWN_NANOS = 180_000_000L;
    
    private GamePanel gp;
    private KeyHandler keyH;
    private int scalePlayer = 2;
    private int coinCount;
    private int keyCount;
    private int superKeyCount;
    private boolean hasAxe;
    private boolean hasBow;
    private boolean hasSword;
    private long lastStepSoundTime;
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
        loadPlayerImages();
    }

    public void setDefaultValues() {
        worldX = 1860;
        worldY = 336;
        speed = 6;
        direction = "down_m";
        solidArea = new Rectangle(24, 48, 36, 36);
        printWorldCoordinates();
    }
    
    private void loadPlayerImages() {
        try {
            up_1 = ResourceLoader.loadImage("/player/" + playerSprites + "/up_1.png");
            up_m = ResourceLoader.loadImage("/player/" + playerSprites + "/up_m.png");
            up_2 = ResourceLoader.loadImage("/player/" + playerSprites + "/up_2.png");
            down_1 = ResourceLoader.loadImage("/player/" + playerSprites + "/down_1.png");
            down_m = ResourceLoader.loadImage("/player/" + playerSprites + "/down_m.png");
            down_2 = ResourceLoader.loadImage("/player/" + playerSprites + "/down_2.png");
            left_1 = ResourceLoader.loadImage("/player/" + playerSprites + "/left_1.png");
            left_m = ResourceLoader.loadImage("/player/" + playerSprites + "/left_m.png");
            left_2 = ResourceLoader.loadImage("/player/" + playerSprites + "/left_2.png");
            right_1 = ResourceLoader.loadImage("/player/" + playerSprites + "/right_1.png");
            right_m = ResourceLoader.loadImage("/player/" + playerSprites + "/right_m.png");
            right_2 = ResourceLoader.loadImage("/player/" + playerSprites + "/right_2.png");
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
            boolean moved = false;

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

            boolean collidesWithTile = gp.cChecker.checkTile(this, nextWorldX, nextWorldY);
            int objectIndex = gp.cChecker.checkObject(this, nextWorldX, nextWorldY);
            boolean collidesWithObject = objectIndex != -1 && gp.objM.objects[objectIndex].collision;

            if(collidesWithTile == false && collidesWithObject == false) {
                worldX = nextWorldX;
                worldY = nextWorldY;
                moved = true;
            }

            if(objectIndex != -1) {
                gp.objM.handleContact(objectIndex, this);
            }

            if(moved) {
                printWorldCoordinates();
                playStepSound();
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

    private void printWorldCoordinates() {
        System.out.printf("CoordenadasMundo: columna=%d, fila=%d (mundo X=%d, Y=%d)%n",
                worldX / gp.tileSize + 1, worldY / gp.tileSize + 1, worldX, worldY);
    }

    private void playStepSound() {
        long currentTime = System.nanoTime();

        if(currentTime - lastStepSoundTime < STEP_SOUND_COOLDOWN_NANOS) {
            return;
        }

        gp.audio.playEffect(STEP_SOUND_PATH);
        lastStepSoundTime = currentTime;
    }

    public void addCoin(int amount) {
        coinCount += amount;
        System.out.println("Monedas: " + coinCount);
    }

    public void addKey(int amount) {
        keyCount += amount;
        System.out.println("Llaves: " + keyCount);
    }

    public boolean useKey() {
        if(keyCount <= 0) {
            return false;
        }

        keyCount--;
        System.out.println("Llaves: " + keyCount);
        return true;
    }

    public void addSuperKey(int amount) {
        superKeyCount += amount;
        System.out.println("SuperKeys: " + superKeyCount);
    }

    public boolean useSuperKey() {
        if(superKeyCount <= 0) {
            return false;
        }

        superKeyCount--;
        System.out.println("SuperKeys: " + superKeyCount);
        return true;
    }

    public void equipSword() {
        hasSword = true;
        System.out.println("Conseguiste la espada.");
    }

    public void equipBow() {
        hasBow = true;
        System.out.println("Conseguiste el arco.");
    }

    public void equipAxe() {
        hasAxe = true;
        System.out.println("Conseguiste el hacha.");
    }

    public void addSpeed(int amount) {
        speed = Math.min(20, speed + amount);
        System.out.println("Velocidad: " + speed);
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
}
