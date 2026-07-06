package main.game;

import main.entities.Entity;
import java.awt.Rectangle; //Colision con rectangulos

public class CollisionChecker {
    private GamePanel gp;

    public CollisionChecker(GamePanel gp) {
        this.gp = gp;
    }

    public boolean checkTile(Entity entity, int nextWorldX, int nextWorldY) {
        int entityLeftWorldX = nextWorldX + entity.solidArea.x;
        int entityRightWorldX = nextWorldX + entity.solidArea.x + entity.solidArea.width - 1;
        int entityTopWorldY = nextWorldY + entity.solidArea.y;
        int entityBottomWorldY = nextWorldY + entity.solidArea.y + entity.solidArea.height - 1;

        int leftCol = entityLeftWorldX / gp.tileSize;
        int rightCol = entityRightWorldX / gp.tileSize;
        int topRow = entityTopWorldY / gp.tileSize;
        int bottomRow = entityBottomWorldY / gp.tileSize;

        if(leftCol < 0 || rightCol >= gp.maxWorldCol || topRow < 0 || bottomRow >= gp.maxWorldRow) {
            return true;
        }

        return gp.tileM.isSolidTile(topRow, leftCol)
                || gp.tileM.isSolidTile(topRow, rightCol)
                || gp.tileM.isSolidTile(bottomRow, leftCol)
                || gp.tileM.isSolidTile(bottomRow, rightCol);
    }



    public boolean checkEntityCollision(Entity first, Entity second) {
        // Crea un rectángulo temporal con la posición actual exacta de la primera entidad
        Rectangle firstRect = new Rectangle(
                (int)first.worldX + first.solidArea.x,
                (int)first.worldY + first.solidArea.y,
                first.solidArea.width,
                first.solidArea.height
        );

        // Crea un rectángulo temporal con la posición actual exacta de la segunda entidad
        Rectangle secondRect = new Rectangle(
                (int)second.worldX + second.solidArea.x,
                (int)second.worldY + second.solidArea.y,
                second.solidArea.width,
                second.solidArea.height
        );

        return firstRect.intersects(secondRect); //Devuelve true si los rectangulos se tocan
    }
}
