package main.tile;

import main.game.GamePanel;

import java.awt.Graphics2D;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import javax.imageio.ImageIO;

public class TileManager {
    GamePanel gp;
    public Tile[] tile;
    int mapTileNum[][];
    int z = 35;

    public TileManager(GamePanel gp) {
        this.gp = gp;
        tile = new Tile[z];
        getTileImage();
        mapTileNum = new int[gp.maxWorldCol][gp.maxWorldRow];
        loadMap("/maps/mapWorld02.txt");
    }

    public void getTileImage() {
        try {
            // while().......
            int cantidadTiles = 35;
            int i = 0;
            while( i < cantidadTiles) {
                tile[i] = new Tile(); 
                tile[i].image = ImageIO.read( getClass().getResourceAsStream(
                                String.format("/tiles/tilesWorld_1/id_%02d.png", i)) );
                i++;
            }
                
            // tile[0] = new Tile();
            // tile[0].image = ImageIO.read(getClass().getResourceAsStream("/tiles/grass1.png"));
            // tile[1] = new Tile();
            // tile[1].image = ImageIO.read(getClass().getResourceAsStream("/tiles/grass2.png"));
            // tile[2] = new Tile();
            // tile[2].image = ImageIO.read(getClass().getResourceAsStream("/tiles/grass3.png"));
            // tile[3] = new Tile();
            // tile[3].image = ImageIO.read(getClass().getResourceAsStream("/tiles/abismo1.png"));
            // tile[4] = new Tile();
            // tile[4].image = ImageIO.read(getClass().getResourceAsStream("/tiles/abismo2.png"));
            // tile[5] = new Tile();
            // tile[5].image = ImageIO.read(getClass().getResourceAsStream("/tiles/grass5.png"));
            // tile[6] = new Tile();
            // tile[6].image = ImageIO.read(getClass().getResourceAsStream("/tiles/id_1.png"));
        } catch (Exception e) {
            e.printStackTrace();
        }
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

        while( worldCol < gp.maxWorldCol && worldRow < gp.maxWorldRow) {
            int tileNum = mapTileNum[worldRow][worldCol];
            int worldX = worldCol * gp.tileSize;
            int worldY = worldRow * gp.tileSize;
            int screenX = worldX - gp.player.worldX + gp.player.screenX;
            int screenY = worldY - gp.player.worldY + gp.player.screenY;

            if( worldX > gp.player.worldX - gp.player.screenX - gp.tileSize &&
                worldX < gp.player.worldX + gp.player.screenX + 5*gp.tileSize &&
                worldY > gp.player.worldY - gp.player.screenY - gp.tileSize &&
                worldY  < gp.player.worldY + gp.player.screenY + 5*gp.tileSize) {
                
                g2.drawImage(tile[tileNum].image, screenX, screenY, gp.tileSize, gp.tileSize, null);
            }
            worldCol++;

            if(worldCol == gp.maxWorldCol) {
                worldCol = 0;
                worldRow++;
            }
            
        }
    }
}
/*8 9 9 9 9 9 9 9 9 9 9 9 9 9 9 10
7 4 4 4 4 4 4 4 4 4 4 4 4 4 4 11
7 4 4 4 4 4 4 4 4 4 4 4 4 4 4 11
7 0 0 0 0 0 0 0 0 0 0 0 0 0 0 11
7 0 0 0 0 0 0 0 0 0 0 0 0 0 0 11
7 0 0 0 0 14 14 14 14 14 14 0 0 0 0 11
7 0 0 0 0 0 0 0 0 0 0 0 0 0 0 11
7 3 3 3 3 3 3 3 3 3 3 3 3 3 3 11
13 2 2 2 2 2 2 2 2 2 2 2 2 2 2 12
1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1
1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1
6 6 6 6 6 6 6 6 6 6 6 6 6 6 6 6 */




