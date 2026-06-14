package main.game;
import javax.swing.JPanel;
import java.awt.Dimension;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import main.entities.Player;
import main.input.KeyHandler;
import main.tile.TileManager;

public class GamePanel extends JPanel implements Runnable {
    
    final int originalTileSize = 16;
    final int scale = 3;
    public final int tileSize = originalTileSize * scale;
    public final int maxScreenCol = 16;
    public final int maxScreenRow = 12;
    final int screenWidth = tileSize * maxScreenCol;
    final int screenHeight = tileSize * maxScreenRow;
    
    KeyHandler keyH = new KeyHandler();
    Thread gameThread;
    Player player = new Player(this, keyH);
    Player player2 = new Player(this, keyH);
    TileManager tileM = new TileManager(this);
    
    public GamePanel() {
        this.setPreferredSize(new Dimension(screenWidth, screenHeight));
        this.setBackground(Color.black);
        this.setDoubleBuffered(true);
        this.addKeyListener(keyH);
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
        player.update();
        repaint();
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
        player.draw(g2);    
        g2.dispose();
    }
}