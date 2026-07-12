package main.game;

import java.awt.Rectangle;

import main.entities.Entity;
import main.objects.GameObject;

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

    public int checkObject(Entity entity, int nextWorldX, int nextWorldY) {
        Rectangle entityArea = new Rectangle(
                nextWorldX + entity.solidArea.x,
                nextWorldY + entity.solidArea.y,
                entity.solidArea.width,
                entity.solidArea.height);

        for(int i = 0; i < gp.objM.objects.length; i++) {
            GameObject object = gp.objM.objects[i];

            if(object == null || object.active == false) {
                continue;
            }

            Rectangle objectArea = new Rectangle(
                    object.worldX + object.solidArea.x,
                    object.worldY + object.solidArea.y,
                    object.solidArea.width,
                    object.solidArea.height);

            if(entityArea.intersects(objectArea)) {
                return i;
            }
        }

        return -1;
    }
}
