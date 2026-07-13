package main.entities;
import main.game.GamePanel;
import main.input.KeyHandler;
import main.util.ResourceLoader;
import java.awt.AlphaComposite;
import java.awt.Composite;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;

public class Player extends Entity {
    public static final int MAX_LIVES = 5;
    public static final String WEAPON_NONE = "Sin arma";
    public static final String WEAPON_SWORD = "Espada";
    public static final String WEAPON_BOW = "Arco";
    public static final String WEAPON_AXE = "Hacha";
    private static final String STEP_SOUND_PATH = "/audio/sfx/null.wav";
    private static final long STEP_SOUND_COOLDOWN_NANOS = 180_000_000L;
    private static final long DAMAGE_COOLDOWN_NANOS = 1_000_000_000L;
    private static final long DAMAGE_FLASH_NANOS = 250_000_000L;
    private static final String SWORD_SOUND_PATH = "/audio/sfx/sword.wav";
    private static final long SWORD_ATTACK_DURATION_NANOS = 220_000_000L;
    
    private GamePanel gp;
    private KeyHandler keyH;
    private int scalePlayer = 2;
    private int coinCount;
    private int keyCount;
    private int superKeyCount;
    private int lives = MAX_LIVES;
    private boolean hasAxe;
    private boolean hasBow;
    private boolean hasSword;
    private String currentWeapon = WEAPON_NONE;
    private BufferedImage axeImage;
    private BufferedImage bowImage;
    private BufferedImage swordImage;
    private BufferedImage upSwordAttackImage;
    private BufferedImage downSwordAttackImage;
    private BufferedImage leftSwordAttackImage;
    private BufferedImage rightSwordAttackImage;
    private long lastStepSoundTime;
    private long lastDamageTime;
    private long swordAttackEndTime;
    private double movementMultiplier = 1.0;
    public final int screenX ;
    public final int screenY ;
    private String playerSprites;

    public Player(GamePanel gp, KeyHandler keyH, String playerSprites) {
        this.gp = gp;
        this.keyH = keyH;
        screenX = gp.screenWidth/2 - (gp.tileSize*scalePlayer)/2;
        screenY = gp.screenHeight/2 - (gp.tileSize*scalePlayer)/2;
        this.playerSprites = playerSprites;
        updateMovementMultiplier();

        setDefaultValues();
        loadPlayerImages();
        loadWeaponImages();
        loadSwordAttackImages();
    }

    public void setDefaultValues() {
        worldX = 1120;
        worldY = 920;
        speed = 6;
        direction = "down_m";
        solidArea = new Rectangle(24, 48, 36, 36);
        printWorldCoordinates();
    }
    
    private void loadPlayerImages() {
        try {
            sprites[0] = ResourceLoader.loadImage("/player/" + playerSprites + "/up_1.png");
            sprites[1] = ResourceLoader.loadImage("/player/" + playerSprites + "/up_m.png");
            sprites[2] = ResourceLoader.loadImage("/player/" + playerSprites + "/up_2.png");
            sprites[3] = sprites[1];
            sprites[4] = ResourceLoader.loadImage("/player/" + playerSprites + "/down_1.png");
            sprites[5] = ResourceLoader.loadImage("/player/" + playerSprites + "/down_m.png");
            sprites[6] = ResourceLoader.loadImage("/player/" + playerSprites + "/down_2.png");
            sprites[7] = sprites[5];
            sprites[8] = ResourceLoader.loadImage("/player/" + playerSprites + "/left_1.png");
            sprites[9] = ResourceLoader.loadImage("/player/" + playerSprites + "/left_m.png");
            sprites[10] = ResourceLoader.loadImage("/player/" + playerSprites + "/left_2.png");
            sprites[11] = sprites[9];
            sprites[12] = ResourceLoader.loadImage("/player/" + playerSprites + "/right_1.png");
            sprites[13] = ResourceLoader.loadImage("/player/" + playerSprites + "/right_m.png");
            sprites[14] = ResourceLoader.loadImage("/player/" + playerSprites + "/right_2.png");
            sprites[15] = sprites[13];
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadWeaponImages() {
        try {
            axeImage = ResourceLoader.loadImage("/objects/axe.png");
            bowImage = ResourceLoader.loadImage("/objects/bow.png");
            swordImage = ResourceLoader.loadImage("/objects/sword.png");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadSwordAttackImages() {
        upSwordAttackImage = loadOptionalSwordAttackImage("up");
        downSwordAttackImage = loadOptionalSwordAttackImage("down");
        leftSwordAttackImage = loadOptionalSwordAttackImage("left");
        rightSwordAttackImage = loadOptionalSwordAttackImage("right");
    }

    private BufferedImage loadOptionalSwordAttackImage(String attackDirection) {
        try {
            return ResourceLoader.loadImage("/player/" + playerSprites + "/" + attackDirection + "_sword.png");
        } catch (Exception e) {
            return null;
        }
    }

    public void setPlayerSprites(String playerSprites) {
        if(this.playerSprites.equals(playerSprites)) {
            return;
        }

        this.playerSprites = playerSprites;
        updateMovementMultiplier();
        loadPlayerImages();
        loadSwordAttackImages();
        System.out.println("Personaje seleccionado: " + playerSprites);
    }

    public String getPlayerSprites() {
        return playerSprites;
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
            int movementSpeed = getMovementSpeed();
            boolean moved = false;

            if(keyH.upPressed == true) {
                direction = "up";
                nextWorldY -= movementSpeed;
            }
            else if(keyH.downPressed == true) {
                direction = "down";
                nextWorldY += movementSpeed;
            }
            else if(keyH.leftPressed == true) {
                direction = "left";
                nextWorldX -= movementSpeed;
            }
            else if(keyH.rightPressed == true) {
                direction = "right";
                nextWorldX += movementSpeed;
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

    private void updateMovementMultiplier() {
        movementMultiplier = "Player_2".equals(playerSprites) ? 1.5 : 1.0;
    }

    private int getMovementSpeed() {
        return (int) Math.round(speed * movementMultiplier);
    }

    public int getLives() {
        return lives;
    }

    public void loseLife() {
        if(lives <= 0) {
            return;
        }

        lives--;
        System.out.println("Vidas: " + lives);
    }

    public boolean restoreLife(int amount) {
        if(amount <= 0 || lives >= MAX_LIVES) {
            return false;
        }

        lives = Math.min(MAX_LIVES, lives + amount);
        System.out.println("Vida recuperada. Vidas: " + lives);
        return true;
    }

    public boolean takeDamage(int damage) {
        if(damage <= 0 || lives <= 0) {
            return false;
        }

        long currentTime = System.nanoTime();
        if(currentTime - lastDamageTime < DAMAGE_COOLDOWN_NANOS) {
            return false;
        }

        lives = Math.max(0, lives - damage);
        lastDamageTime = currentTime;
        System.out.println("Danio recibido: " + damage + ". Vidas: " + lives);
        return true;
    }

    public boolean startSwordAttack() {
        if(WEAPON_SWORD.equals(currentWeapon) == false || isSwordAttacking()) {
            return false;
        }

        if("Player_2".equals(playerSprites)) {
            gp.showTemporaryMessage("No puedes atacar");
            return false;
        }

        swordAttackEndTime = System.nanoTime() + SWORD_ATTACK_DURATION_NANOS;
        gp.audio.playEffect(SWORD_SOUND_PATH);
        return true;
    }

    public boolean isSwordAttacking() {
        return System.nanoTime() < swordAttackEndTime;
    }

    public Rectangle getSwordAttackArea() {
        int bodyX = worldX + solidArea.x;
        int bodyY = worldY + solidArea.y;
        int reach = gp.tileSize * 2;
        int thickness = solidArea.width + gp.tileSize;
        int offset = gp.tileSize / 2;

        return switch (direction) {
            case "up" -> new Rectangle(bodyX - offset, bodyY - reach, thickness, reach);
            case "down" -> new Rectangle(bodyX - offset, bodyY + solidArea.height, thickness, reach);
            case "left" -> new Rectangle(bodyX - reach, bodyY - offset, reach, thickness);
            case "right" -> new Rectangle(bodyX + solidArea.width, bodyY - offset, reach, thickness);
            default -> new Rectangle(bodyX - offset, bodyY + solidArea.height, thickness, reach);
        };
    }

    public float getDamageFlashOpacity() {
        long elapsed = System.nanoTime() - lastDamageTime;

        if(lastDamageTime == 0 || elapsed >= DAMAGE_FLASH_NANOS) {
            return 0.0f;
        }

        return (float) (0.4 * (1.0 - (double) elapsed / DAMAGE_FLASH_NANOS));
    }

    public String getCurrentWeapon() {
        return currentWeapon;
    }

    public BufferedImage getCurrentWeaponImage() {
        return switch (currentWeapon) {
            case WEAPON_SWORD -> swordImage;
            case WEAPON_BOW -> bowImage;
            case WEAPON_AXE -> axeImage;
            default -> null;
        };
    }

    public boolean isWeaponUnlocked(String weapon) {
        return switch (weapon) {
            case WEAPON_NONE -> true;
            case WEAPON_SWORD -> hasSword;
            case WEAPON_BOW -> hasBow;
            case WEAPON_AXE -> hasAxe;
            default -> false;
        };
    }

    public boolean selectWeapon(String weapon) {
        if(isWeaponUnlocked(weapon) == false) {
            System.out.println("Aun no tienes el arma: " + weapon);
            return false;
        }

        currentWeapon = weapon;
        System.out.println("Arma actual: " + currentWeapon);
        return true;
    }

    public void equipSword() {
        hasSword = true;
        currentWeapon = WEAPON_SWORD;
        System.out.println("Conseguiste la espada.");
    }

    public void equipBow() {
        hasBow = true;
        currentWeapon = WEAPON_BOW;
        System.out.println("Conseguiste el arco.");
    }

    public void equipAxe() {
        hasAxe = true;
        currentWeapon = WEAPON_AXE;
        System.out.println("Conseguiste el hacha.");
    }

    public void addSpeed(int amount) {
        speed = Math.min(20, speed + amount);
        System.out.println("Velocidad: " + speed);
    }

    public void draw(Graphics2D g2) {
        int spriteIndex = switch (direction) {
            case "up" -> spriteNum - 1;
            case "down" -> 4 + spriteNum - 1;
            case "left" -> 8 + spriteNum - 1;
            case "right" -> 12 + spriteNum - 1;
            default -> 5;
        };
        BufferedImage image = isSwordAttacking() ? getSwordAttackImage() : sprites[spriteIndex];

        if(image == null) {
            image = sprites[spriteIndex] != null ? sprites[spriteIndex] : sprites[5];
        }

        Composite originalComposite = g2.getComposite();
        long elapsed = System.nanoTime() - lastDamageTime;
        if(lastDamageTime != 0 && elapsed < DAMAGE_COOLDOWN_NANOS && (elapsed / 100_000_000L) % 2 == 0) {
            g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.45f));
        }

        g2.drawImage(image, screenX, screenY, scalePlayer * gp.tileSize, scalePlayer * gp.tileSize, null);
        g2.setComposite(originalComposite);
    }

    private BufferedImage getSwordAttackImage() {
        return switch (direction) {
            case "up" -> upSwordAttackImage;
            case "down" -> downSwordAttackImage;
            case "left" -> leftSwordAttackImage;
            case "right" -> rightSwordAttackImage;
            default -> downSwordAttackImage;
        };
    }
}
