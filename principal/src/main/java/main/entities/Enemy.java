package main.entities;

import java.awt.Point;
import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

import main.game.GamePanel;
import main.util.ResourceLoader;

public class Enemy extends Entity {
    private static final int REPATH_INTERVAL_FRAMES = 20;
    private static final long DAMAGE_FLASH_DURATION_NANOS = 180_000_000L;
    private static final int[][] DIRECTIONS = {{0, -1}, {1, 0}, {0, 1}, {-1, 0}};

    private final GamePanel gp;
    private final int contactDamage;
    private final int maxHealth;
    private final List<Point> path = new ArrayList<>();
    private int health;
    private boolean alive = true;
    private long lastSwordHitTime;
    private int repathTimer;
    private int lastTargetCol = -1;
    private int lastTargetRow = -1;

    public Enemy(GamePanel gp, int spawnCol, int spawnRow) {
        this(gp, spawnCol, spawnRow, "Enemy_1", 1);
    }

    public Enemy(GamePanel gp, int spawnCol, int spawnRow, String spriteSet) {
        this(gp, spawnCol, spawnRow, spriteSet, 1);
    }

    public Enemy(GamePanel gp, int spawnCol, int spawnRow, String spriteSet, int contactDamage) {
        this(gp, spawnCol, spawnRow, spriteSet, contactDamage, 3);
    }

    public Enemy(GamePanel gp, int spawnCol, int spawnRow, String spriteSet, int contactDamage, int movementSpeed) {
        this(gp, spawnCol, spawnRow, spriteSet, contactDamage, movementSpeed,
                "Enemy_1".equals(spriteSet) ? 20 : 10);
    }

    public Enemy(GamePanel gp, int spawnCol, int spawnRow, String spriteSet, int contactDamage,
            int movementSpeed, int maxHealth) {
        this.gp = gp;
        this.contactDamage = Math.max(1, contactDamage);
        this.maxHealth = Math.max(1, maxHealth);
        health = this.maxHealth;
        speed = Math.max(1, movementSpeed);
        direction = "down";
        solidArea = new Rectangle(24, 48, 36, 36);
        worldX = tileToWorld(spawnCol, solidArea.x, solidArea.width);
        worldY = tileToWorld(spawnRow, solidArea.y, solidArea.height);
        loadSprites(spriteSet);
    }

    public void update(Player target) {
        if(alive == false) {
            return;
        }

        Point targetTile = toTile(target.worldX, target.worldY, target.solidArea.x,
                target.solidArea.y, target.solidArea.width, target.solidArea.height);

        if(repathTimer <= 0 || targetTile.x != lastTargetCol || targetTile.y != lastTargetRow) {
            findPath(targetTile.x, targetTile.y);
            lastTargetCol = targetTile.x;
            lastTargetRow = targetTile.y;
            repathTimer = REPATH_INTERVAL_FRAMES;
        } else {
            repathTimer--;
        }

        if(followPath()) {
            updateAnimation();
        }
    }

    public boolean findPath(int targetCol, int targetRow) {
        Point start = toTile(worldX, worldY, solidArea.x, solidArea.y, solidArea.width, solidArea.height);

        if(isWalkable(targetCol, targetRow) == false || isWalkable(start.x, start.y) == false) {
            path.clear();
            return false;
        }

        boolean[][] visited = new boolean[gp.maxWorldRow][gp.maxWorldCol];
        Point[][] previous = new Point[gp.maxWorldRow][gp.maxWorldCol];
        Deque<Point> frontier = new ArrayDeque<>();

        frontier.add(start);
        visited[start.y][start.x] = true;

        while(frontier.isEmpty() == false) {
            Point current = frontier.removeFirst();

            if(current.x == targetCol && current.y == targetRow) {
                buildPath(previous, start, current);
                return true;
            }

            for(int[] direction : DIRECTIONS) {
                int nextCol = current.x + direction[0];
                int nextRow = current.y + direction[1];

                if(isWalkable(nextCol, nextRow) == false || visited[nextRow][nextCol]) {
                    continue;
                }

                visited[nextRow][nextCol] = true;
                previous[nextRow][nextCol] = current;
                frontier.addLast(new Point(nextCol, nextRow));
            }
        }

        path.clear();
        return false;
    }

    public List<Point> getPath() {
        return List.copyOf(path);
    }

    public void damagePlayer(Player player) {
        if(alive == false) {
            return;
        }

        Rectangle enemyBounds = new Rectangle(
                worldX + solidArea.x,
                worldY + solidArea.y,
                solidArea.width,
                solidArea.height);
        Rectangle playerBounds = new Rectangle(
                player.worldX + player.solidArea.x,
                player.worldY + player.solidArea.y,
                player.solidArea.width,
                player.solidArea.height);

        if(enemyBounds.intersects(playerBounds)) {
            player.takeDamage(contactDamage);
        }
    }

    public void draw(Graphics2D g2) {
        if(alive == false) {
            return;
        }

        int spriteIndex = switch (direction) {
            case "up" -> 12 + spriteNum - 1;
            case "down" -> spriteNum - 1;
            case "left" -> 4 + spriteNum - 1;
            case "right" -> 8 + spriteNum - 1;
            default -> 1;
        };
        BufferedImage image = sprites[spriteIndex];

        if(image == null) {
            return;
        }

        int screenX = worldX - gp.player.worldX + gp.player.screenX;
        int screenY = worldY - gp.player.worldY + gp.player.screenY;

        if(worldX > gp.player.worldX - gp.player.screenX - gp.tileSize
                && worldX < gp.player.worldX + gp.player.screenX + gp.tileSize
                && worldY > gp.player.worldY - gp.player.screenY - gp.tileSize
                && worldY < gp.player.worldY + gp.player.screenY + gp.tileSize) {
            g2.drawImage(image, screenX, screenY, gp.tileSize * 2, gp.tileSize * 2, null);
            drawDamageEffect(g2, screenX, screenY);
        }
    }

    public boolean takeSwordHit() {
        if(alive == false) {
            return false;
        }

        health--;
        lastSwordHitTime = System.nanoTime();
        System.out.println("Vida del enemigo: " + health + "/" + maxHealth);

        if(health == 0) {
            alive = false;
            path.clear();
            System.out.println("Enemigo derrotado.");
        }

        return true;
    }

    public boolean isAlive() {
        return alive;
    }

    public Rectangle getHitbox() {
        return new Rectangle(worldX + solidArea.x, worldY + solidArea.y,
                solidArea.width, solidArea.height);
    }

    public void doubleSpeed() {
        speed *= 2;
        System.out.println("Velocidad del enemigo duplicada: " + speed);
    }

    public boolean reduceHealthToOneForDemo() {
        if(alive == false) {
            return false;
        }

        health = 1;
        return true;
    }

    private void drawDamageEffect(Graphics2D g2, int screenX, int screenY) {
        long elapsed = System.nanoTime() - lastSwordHitTime;
        if(lastSwordHitTime == 0 || elapsed >= DAMAGE_FLASH_DURATION_NANOS) {
            return;
        }

        float opacity = (float) (0.42 * (1.0 - (double) elapsed / DAMAGE_FLASH_DURATION_NANOS));
        Composite originalComposite = g2.getComposite();
        g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, opacity));
        g2.setColor(new Color(255, 55, 55));
        g2.fillRect(screenX, screenY, gp.tileSize * 2, gp.tileSize * 2);
        g2.setComposite(originalComposite);
    }

    private void loadSprites(String spriteSet) {
        try {
            for(int index = 0; index < sprites.length; index++) {
                sprites[index] = ResourceLoader.loadImage(
                        String.format("/enemy/%s/sprite_%02d.png", spriteSet, index));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void updateAnimation() {
        spriteCounter++;

        if(spriteCounter > 12) {
            spriteNum = spriteNum % 4 + 1;
            spriteCounter = 0;
        }
    }

    private void buildPath(Point[][] previous, Point start, Point target) {
        List<Point> newPath = new ArrayList<>();
        Point current = target;

        while(current.equals(start) == false) {
            newPath.add(0, current);
            current = previous[current.y][current.x];
        }

        path.clear();
        path.addAll(newPath);
    }

    private boolean followPath() {
        if(path.isEmpty()) {
            return false;
        }

        Point nextTile = path.get(0);
        int targetX = tileToWorld(nextTile.x, solidArea.x, solidArea.width);
        int targetY = tileToWorld(nextTile.y, solidArea.y, solidArea.height);
        int nextWorldX = worldX;
        int nextWorldY = worldY;

        if(worldX < targetX) {
            nextWorldX = Math.min(worldX + speed, targetX);
            direction = "right";
        } else if(worldX > targetX) {
            nextWorldX = Math.max(worldX - speed, targetX);
            direction = "left";
        } else if(worldY < targetY) {
            nextWorldY = Math.min(worldY + speed, targetY);
            direction = "down";
        } else if(worldY > targetY) {
            nextWorldY = Math.max(worldY - speed, targetY);
            direction = "up";
        }

        if(gp.cChecker.checkTile(this, nextWorldX, nextWorldY)) {
            path.clear();
            repathTimer = 0;
            return false;
        }

        worldX = nextWorldX;
        worldY = nextWorldY;

        if(worldX == targetX && worldY == targetY) {
            path.remove(0);
        }

        return true;
    }

    private Point toTile(int x, int y, int areaX, int areaY, int areaWidth, int areaHeight) {
        int centerX = x + areaX + areaWidth / 2;
        int centerY = y + areaY + areaHeight / 2;
        return new Point(centerX / gp.tileSize, centerY / gp.tileSize);
    }

    private int tileToWorld(int tileCoordinate, int areaOffset, int areaSize) {
        return tileCoordinate * gp.tileSize + (gp.tileSize - areaSize) / 2 - areaOffset;
    }

    private boolean isWalkable(int col, int row) {
        return col >= 0 && col < gp.maxWorldCol
                && row >= 0 && row < gp.maxWorldRow
                && gp.tileM.isSolidTile(row, col) == false;
    }
}
