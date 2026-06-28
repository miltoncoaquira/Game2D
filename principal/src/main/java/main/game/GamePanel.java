package main.game;
import javax.swing.JPanel;
import java.awt.Dimension;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints.Key;

import main.entities.Player;
import main.input.KeyHandler;
import main.input.KeySetting;
import main.tiles.TileManager;

public class GamePanel extends JPanel implements Runnable {

    final int originalTileSize = 16;
    final int scale = 3;
    public final int tileSize = originalTileSize * scale;
    public final int maxScreenCol = 20;
    public final int maxScreenRow = 15;
    public final int screenWidth = tileSize * maxScreenCol;
    public final int screenHeight = tileSize * maxScreenRow;

    public final int maxWorldCol = 50;
    public final int maxWorldRow = 50;
    public final int worldWidth = tileSize * maxWorldCol;
    public final int worldHeight = tileSize * maxWorldRow;
    Thread gameThread;

    KeyHandler keyH_1 = new KeyHandler("WASD");
    public Player player = new Player(this, keyH_1, "Default_Player");
    // public Player player = new Player(this, keyH_1, "Player_2");
    // KeyHandler keyH_2 = new KeyHandler("IJKL");
    // public Player player2 = new Player(this, keyH_2, "Player_2");
    KeySetting keyS = new KeySetting("90");

    public TileManager tileM = new TileManager(this, keyS);
    public CollisionChecker cChecker = new CollisionChecker(this);
    
    public GamePanel() {
        this.setPreferredSize(new Dimension(screenWidth, screenHeight));
        this.setBackground(Color.black);
        this.setDoubleBuffered(true);
        this.addKeyListener(keyH_1);
        // this.addKeyListener(keyH_2);
        this.addKeyListener(keyS);
        this.setFocusable(true);
        this.requestFocusInWindow();
    }

    public void startGameThread() {
        gameThread = new Thread(this);
        gameThread.start();
    }

    int FPS = 60;
    @Override
    public void run() {
        double drawInterval = 1000000000/FPS;
        double nextDrawTime = System.nanoTime() + drawInterval;
        while(gameThread != null) {
            double currentTime = System.nanoTime();
            if(currentTime >= nextDrawTime) {
                player.update();
                repaint();
                nextDrawTime += drawInterval;
            } else {
                try {
                    Thread.sleep(1);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        }
    }


    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D)g;
        tileM.draw(g2);
        // player.drawTest(g2);
        player.draw(g2);    
        // player2.draw(g2);

        g2.dispose();
    }
}
