package main.tiles;

import main.game.GamePanel;
import main.input.KeySetting;
import java.awt.Graphics2D;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import javax.imageio.ImageIO;

public class TileManager {
    GamePanel gp;
    public Tile[] tile;
    int mapTileNum[][];
    int cantidadTiles = 35;
    KeySetting keyS;
    int zoom = 1;
    private int fpsCounter = 1;

    public TileManager(GamePanel gp, KeySetting keyS, String mapPath) {
        this.gp = gp;
        this.keyS = keyS;
        tile = new Tile[cantidadTiles];
        getTileImage();
        mapTileNum = new int[gp.maxWorldRow][gp.maxWorldCol];
        loadMap(mapPath);
    }

    public void getTileImage() {
        try {
            int cantidadTiles = 35;
            int i = 0;
            while( i < cantidadTiles) {
                tile[i] = new Tile(); 
                tile[i].image = ImageIO.read( getClass().getResourceAsStream(
                                String.format("/tiles/tilesWorld_1/id_%02d.png", i)) );
                i++;
            }
            setCollisionTiles();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setCollisionTiles() {
        int[] solidTileIds = {1, 2, 3, 4, 5, 8, 9, 10};

        for( int i = 0; i < solidTileIds.length; ++i ) 
                tile[solidTileIds[i]].collision = true;
    }

    public boolean isSolidTile(int row, int col) {
        int tileNum = mapTileNum[row][col];
        return tileNum >= 0 && tileNum < tile.length && tile[tileNum].collision;
    }

    public void loadMap(String mapPath){
        try {
            InputStream is = getClass().getResourceAsStream(mapPath);
            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            int col = 0;
            int row = 0;
            while(col < gp.maxWorldCol && row < gp.maxWorldRow) {
                String line = br.readLine();
                while(col < gp.maxWorldCol) {
                    String numbers[] = line.split(" ");
                    int num = Integer.parseInt(numbers[col]);
                    mapTileNum[row][col] = num;
                    col++;
                }
                col = 0;
                row++;
            }
            br.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void draw(Graphics2D g2) {
        int worldCol = 0;
        int worldRow = 0;
        int X = zoom * gp.tileSize;

        if(fpsCounter > 0){
            if( keyS.incPressed == true && zoom < 7){
                zoom++;
                System.out.println("Zoom -> " + zoom );
            }
            if( keyS.decPressed == true && zoom > 1){
                zoom--;
                System.out.println("Zoom -> " + zoom );
            }
        }

        while( worldCol < gp.maxWorldCol && worldRow < gp.maxWorldRow) {
            int tileNum = mapTileNum[worldRow][worldCol];
            int worldX = worldCol * gp.tileSize;
            int worldY = worldRow * gp.tileSize;
            int screenX = worldX - gp.player.worldX + gp.player.screenX;
            int screenY = worldY - gp.player.worldY + gp.player.screenY;

            if( worldX > gp.player.worldX - gp.player.screenX - gp.tileSize &&
                worldX < gp.player.worldX + gp.player.screenX + 5*gp.tileSize &&
                worldY > gp.player.worldY - gp.player.screenY - gp.tileSize &&
                worldY < gp.player.worldY + gp.player.screenY + 5*gp.tileSize) {
                
                g2.drawImage(tile[tileNum].image, screenX, screenY, X, X, null);
            }
            worldCol++;
            
            if(worldCol == gp.maxWorldCol) {
                worldCol = 0;
                worldRow++;
            }
        }
        fpsCounter++;
        if(fpsCounter > 30 )
            fpsCounter = 0;
    }
}
