package main.entities;

import main.game.GamePanel;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;

public class Enemy extends Entity {

    private final GamePanel gp;
    private BufferedImage enemyImage;
    private final int scale = 2;

    public boolean ignoreTileCollision;

    public Enemy(GamePanel gp, double worldX, double worldY, double speed, boolean ignoreTileCollision) {
        this.gp = gp;
        this.worldX = worldX;
        this.worldY = worldY;
        this.speed = speed > 1 ? speed : 150;
        this.direction = "down";

        this.solidArea = new Rectangle(16, 16, 64, 64);

        this.ignoreTileCollision = ignoreTileCollision;

        loadImage();
    }

    private void loadImage() {
        try {
            enemyImage = ImageIO.read(getClass().getResourceAsStream("/entities/enemy.png"));
        } catch (Exception e) {
            enemyImage = null;
        }
    }

    public void update(double deltaTime) {
        //oordenadas actuales del jugador
        double targetX = gp.player.worldX;
        double targetY = gp.player.worldY;

        // Math.signum nos da 1, -1 o 0 para saber exactamente hacia que lado caminar
        double dx = Math.signum(targetX - worldX);
        double dy = Math.signum(targetY - worldY);

        double nextWorldX = worldX;
        double nextWorldY = worldY;

        double distance = speed * deltaTime;

        //Decide moverse en el eje X o Y segun que distancia sea mayor
        if (Math.abs(targetX - worldX) > Math.abs(targetY - worldY)) {
            nextWorldX += distance * dx;
        } else {
            nextWorldY += distance * dy;
        }

        //Si ignora paredes
        if (this.ignoreTileCollision) {
            worldX = nextWorldX;
            worldY = nextWorldY;
            return;
        }

    }

    public void draw(Graphics2D g2) {
        int screenX = (int)(worldX - gp.player.worldX + gp.player.screenX);
        int screenY = (int)(worldY - gp.player.worldY + gp.player.screenY);

        //Solo se dibuja si está en pantalla
        if (worldX > gp.player.worldX - gp.player.screenX - gp.tileSize
                && worldX < gp.player.worldX + gp.player.screenX + gp.tileSize
                && worldY > gp.player.worldY - gp.player.screenY - gp.tileSize
                && worldY < gp.player.worldY + gp.player.screenY + gp.tileSize) {

            if (enemyImage != null) {
                g2.drawImage(enemyImage, screenX, screenY, gp.tileSize * scale, gp.tileSize * scale, null);
            } else {
                g2.fillOval(screenX + 8, screenY + 8, gp.tileSize, gp.tileSize); //genera un ovalo si no hay imagen
            }
        }
    }
}