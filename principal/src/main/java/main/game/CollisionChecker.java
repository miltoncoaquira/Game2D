package main.game;

import main.entities.Entity;

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
}