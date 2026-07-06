package main.game;
import javax.swing.JPanel;
import java.awt.Dimension;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.awt.RenderingHints.Key;

import main.entities.Player;
import main.entities.Item;
import main.entities.Enemy;
import main.input.KeyHandler;
import main.input.KeySetting;
import main.tiles.TileManager;

//imports para el gameover y leaderboard
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.DefaultListModel;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.JFrame;

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

    public List<Enemy> enemies = new ArrayList<>();
    public List<Item> items = new ArrayList<>();
    public float enemyBaseSpeed = 150f;

    public String currentMapName;

    private long lastSpawnTime;
    private Random random = new Random();

    public GamePanel(int characterIndex, String mapName) { //parametros del menu principal
        this.setPreferredSize(new Dimension(screenWidth, screenHeight));
        this.setBackground(Color.black);
        this.setDoubleBuffered(true);
        this.addKeyListener(keyH_1);
        // this.addKeyListener(keyH_2);
        this.addKeyListener(keyS);
        this.setFocusable(true);
        this.requestFocusInWindow();

        String spriteName = (characterIndex == 2) ? "Player_2" : "Default_Player";//Multiples personajes
        this.player = new Player(this, keyH_1, spriteName);
        this.currentMapName = mapName;
        tileM.loadMap("/maps/" + mapName + ".txt");

        tileM.loadSpawnMap("/maps/" + mapName + "_spawn.txt"); //ubicacion del archivo de spawn

        lastSpawnTime = System.currentTimeMillis(); //cronometro para spawneo

        setupGame();

    }

    public void setupGame() { //Instanciar las entidades, en este caso solo el enemigo
        enemies.add(new Enemy(this, 200, 200, enemyBaseSpeed, true));
        spawnRandomItem();
    }

    public void spawnRandomItem() {
        if (tileM.spawnPoints.isEmpty()) {
            return;
        }

        int randomIndex = random.nextInt(tileM.spawnPoints.size()); // Elige un indice random de la lista de 1s
        int[] pos = tileM.spawnPoints.get(randomIndex);

        int worldX = pos[0] * tileSize;
        int worldY = pos[1] * tileSize;

        items.add(new Item(this, worldX, worldY));
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
        long lastTime = System.nanoTime();

        while(gameThread != null) {
            long currentTime = System.nanoTime();

            if(currentTime >= nextDrawTime) {
                double deltaTime = (currentTime - lastTime) / 1000000000.0;
                lastTime = currentTime;

                update(deltaTime);
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

    public void update(double deltaTime) {
        player.update(deltaTime);

        for (Enemy enemy : enemies) {
            enemy.update(deltaTime); //Le damos deltatime a los enemigos para movimiento correcto
            if (cChecker.checkEntityCollision(player, enemy)) {
                player.takeDamage();
                if (!player.alive) {
                    triggerGameOver();
                    return; //
                }
            }
        }

        //Checa si el jugador toca algún item
        for (int i = 0; i < items.size(); i++) {
            Item item = items.get(i);

            if (!item.collected && cChecker.checkEntityCollision(player, item)) { //Si no ha sido recolectado y toca al player
                item.collect(); //
                player.addPoints(10);
                System.out.println("Obtuviste " + player.points + " puntos.");
                items.remove(i);
                i--; // Retrocedemos el índice para no saltarnos el siguiente elemento de la lista ya que removemos este indice.
            }
        }

        long currentTimeMillis = System.currentTimeMillis();
        if (currentTimeMillis - lastSpawnTime >= 5000) { //5 segundos (5000 milisegundos)
            spawnRandomItem();
            lastSpawnTime = currentTimeMillis; //Reiniciamos el cronometro
        }
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D)g;
        tileM.draw(g2);
        // player.drawTest(g2);

        for (Item item : items) {
            item.draw(g2); // Dibuja si no ha sido recolectado
        }

        for (Enemy enemy : enemies) {
            enemy.draw(g2); // Dibuja el enemigo
        }

        player.draw(g2);    
        // player2.draw(g2);

        g2.dispose();
    }

    public void triggerGameOver() {
        //Detenemos el bucle del gameLoop
        gameThread = null;

        //Ejecucion de la interfaz de la leaderboard
        SwingUtilities.invokeLater(() -> {
            try {
                //Inicializamos base de datos
                LeaderboardDB.init();

                // Pedimos el nombre al jugador
                String playerName = JOptionPane.showInputDialog(
                        this,
                        "Has perdido.\nIngresa tu nombre para guardar tu puntuacion:",
                        "Game Over",
                        JOptionPane.QUESTION_MESSAGE
                );

                if (playerName == null || playerName.trim().isEmpty()) {
                    playerName = "Anonimo";
                }

                //Guardamos el puntaje
                LeaderboardDB.saveScore(playerName.trim(), currentMapName, player.points);

                //Obtenemos los 10 mejores puntajes para este mapa
                List<LeaderboardDB.ScoreEntry> topScores = LeaderboardDB.getTopScores(currentMapName, 10);

                //Preparamos la lista visual
                DefaultListModel<String> listModel = new DefaultListModel<>();
                for (int i = 0; i < topScores.size(); i++) {
                    LeaderboardDB.ScoreEntry entry = topScores.get(i);
                    listModel.addElement((i + 1) + ". " + entry.playerName() + " - " + entry.score() + " pts");
                }

                JList<String> list = new JList<>(listModel);
                JScrollPane scroll = new JScrollPane(list);
                scroll.setPreferredSize(new Dimension(250, 200));

                String[] options = {"Jugar de nuevo", "Salir del juego"};
                int choice = JOptionPane.showOptionDialog(
                        this,
                        scroll,
                        "Top Puntajes - " + currentMapName,
                        JOptionPane.DEFAULT_OPTION,
                        JOptionPane.PLAIN_MESSAGE,
                        null,
                        options,
                        options[0]
                );

                if (choice == 0) {
                    JFrame topFrame = (JFrame) SwingUtilities.getWindowAncestor(this);
                    topFrame.dispose();
                    new StartMenu().setVisible(true); //Reabre el menu Principal
                } else {
                    System.exit(0); // Cierra la aplicación
                }

            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });
    }
}
