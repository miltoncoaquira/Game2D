package main.game;
import javax.swing.JPanel;
import java.awt.Dimension;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.image.BufferedImage;

import main.audio.AudioManager;
import main.entities.Player;
import main.input.KeyHandler;
import main.input.KeySetting;
import main.objects.ObjectManager;
import main.tiles.TileManager;
import main.util.ResourceLoader;

public class GamePanel extends JPanel implements Runnable {
    private static final String[] PLAYER_OPTIONS = {"Default_Player", "Player_2"};
    private static final String[] WEAPON_OPTIONS = {
            Player.WEAPON_NONE, Player.WEAPON_SWORD, Player.WEAPON_BOW, Player.WEAPON_AXE};
    private static final String[] VOLUME_OPTIONS = {"Musica", "Efectos"};

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
    public final String worldMapPath = "/maps/world/mapWorld02.txt";
    public final String objectMapPath = "/maps/objects/mapObjects.txt";
    public final String backgroundMusicPath = "/audio/music/stage.wav";
    public final String menuMusicPath = "/audio/music/menu.wav";
    Thread gameThread;
    private boolean characterMenuOpen;
    private int characterSelection;
    private int weaponSelection;
    private int menuSection;
    private int volumeSelection;
    private BufferedImage heartRedImage;
    private BufferedImage heartWhiteImage;

    KeyHandler keyH_1 = new KeyHandler("WASD");
    public Player player = new Player(this, keyH_1, "Default_Player");
    // public Player player = new Player(this, keyH_1, "Player_2");
    // KeyHandler keyH_2 = new KeyHandler("IJKL");
    // public Player player2 = new Player(this, keyH_2, "Player_2");
    KeySetting keyS = new KeySetting("90");

    public TileManager tileM = new TileManager(this, keyS, worldMapPath);
    public CollisionChecker cChecker = new CollisionChecker(this);
    public ObjectManager objM = new ObjectManager(this, objectMapPath);
    public final AudioManager audio = new AudioManager();
    
    public GamePanel() {
        this.setPreferredSize(new Dimension(screenWidth, screenHeight));
        this.setBackground(Color.black);
        this.setDoubleBuffered(true);
        this.addKeyListener(keyH_1);
        // this.addKeyListener(keyH_2);
        this.addKeyListener(keyS);
        loadHudImages();
        this.setFocusable(true);
        this.requestFocusInWindow();
    }

    private void loadHudImages() {
        try {
            heartRedImage = ResourceLoader.loadImage("/ui/heart_red.png");
            heartWhiteImage = ResourceLoader.loadImage("/ui/heart_white.png");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void startGameThread() {
        audio.playMusic(backgroundMusicPath, true);
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
                if(keyH_1.restartPressed) {
                    resetGame();
                }
                updateCharacterMenu();
                if(characterMenuOpen == false) {
                    player.update();
                }
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

    public void resetGame() {
        keyH_1.resetInputState();
        player = new Player(this, keyH_1, "Default_Player");
        objM = new ObjectManager(this, objectMapPath);
        characterMenuOpen = false;
        characterSelection = 0;
        weaponSelection = 0;
        menuSection = 0;
        volumeSelection = 0;
        audio.playMusic(backgroundMusicPath, true);
        System.out.println("Juego reiniciado.");
    }

    private void updateCharacterMenu() {
        if(keyH_1.characterMenuPressed) {
            characterMenuOpen = !characterMenuOpen;
            audio.playMusic(characterMenuOpen ? menuMusicPath : backgroundMusicPath, true);
            keyH_1.characterMenuPressed = false;
            clearCharacterMenuInput();
            return;
        }

        if(characterMenuOpen == false) {
            return;
        }

        if(keyH_1.menuLeftPressed || keyH_1.menuRightPressed) {
            int direction = keyH_1.menuRightPressed ? 1 : -1;
            menuSection = Math.floorMod(menuSection + direction, 3);
            keyH_1.menuLeftPressed = false;
            keyH_1.menuRightPressed = false;
        }
        if(keyH_1.menuUpPressed) {
            if(menuSection == 1) {
                weaponSelection = Math.floorMod(weaponSelection - 1, WEAPON_OPTIONS.length);
            } else if(menuSection == 2) {
                volumeSelection = Math.floorMod(volumeSelection - 1, VOLUME_OPTIONS.length);
            } else {
                characterSelection = Math.floorMod(characterSelection - 1, PLAYER_OPTIONS.length);
            }
            keyH_1.menuUpPressed = false;
        }
        if(keyH_1.menuDownPressed) {
            if(menuSection == 1) {
                weaponSelection = (weaponSelection + 1) % WEAPON_OPTIONS.length;
            } else if(menuSection == 2) {
                volumeSelection = (volumeSelection + 1) % VOLUME_OPTIONS.length;
            } else {
                characterSelection = (characterSelection + 1) % PLAYER_OPTIONS.length;
            }
            keyH_1.menuDownPressed = false;
        }
        if(keyH_1.menuVolumeDownPressed || keyH_1.menuVolumeUpPressed) {
            if(menuSection == 2) {
                int adjustment = keyH_1.menuVolumeUpPressed ? 10 : -10;
                adjustSelectedVolume(adjustment);
            }
            keyH_1.menuVolumeDownPressed = false;
            keyH_1.menuVolumeUpPressed = false;
        }
        if(keyH_1.menuConfirmPressed) {
            if(menuSection == 1) {
                if(player.selectWeapon(WEAPON_OPTIONS[weaponSelection])) {
                    characterMenuOpen = false;
                    audio.playMusic(backgroundMusicPath, true);
                    clearCharacterMenuInput();
                }
            } else if(menuSection == 0) {
                player.setPlayerSprites(PLAYER_OPTIONS[characterSelection]);
                characterMenuOpen = false;
                audio.playMusic(backgroundMusicPath, true);
                clearCharacterMenuInput();
            } else {
                characterMenuOpen = false;
                audio.playMusic(backgroundMusicPath, true);
                clearCharacterMenuInput();
            }
        }
        if(keyH_1.menuCancelPressed) {
            characterMenuOpen = false;
            audio.playMusic(backgroundMusicPath, true);
            clearCharacterMenuInput();
        }
    }

    private void adjustSelectedVolume(int adjustment) {
        if(volumeSelection == 0) {
            audio.setMusicVolume(audio.getMusicVolume() + adjustment);
            System.out.println("Volumen de musica: " + audio.getMusicVolume() + "%");
        } else {
            audio.setEffectsVolume(audio.getEffectsVolume() + adjustment);
            System.out.println("Volumen de efectos: " + audio.getEffectsVolume() + "%");
        }
    }

    private void clearCharacterMenuInput() {
        keyH_1.menuUpPressed = false;
        keyH_1.menuDownPressed = false;
        keyH_1.menuLeftPressed = false;
        keyH_1.menuRightPressed = false;
        keyH_1.menuConfirmPressed = false;
        keyH_1.menuCancelPressed = false;
        keyH_1.menuVolumeDownPressed = false;
        keyH_1.menuVolumeUpPressed = false;
    }

    private void drawLegacyCharacterMenu(Graphics2D g2) {
        int menuWidth = 360;
        int menuHeight = 210;
        int menuX = (screenWidth - menuWidth) / 2;
        int menuY = (screenHeight - menuHeight) / 2;

        g2.setColor(new Color(0, 0, 0, 190));
        g2.fillRoundRect(menuX, menuY, menuWidth, menuHeight, 18, 18);
        g2.setColor(Color.WHITE);
        g2.drawRoundRect(menuX, menuY, menuWidth, menuHeight, 18, 18);

        g2.setFont(new Font("SansSerif", Font.BOLD, 22));
        g2.drawString("Seleccionar personaje", menuX + 72, menuY + 38);

        g2.setFont(new Font("SansSerif", Font.PLAIN, 18));
        for(int index = 0; index < PLAYER_OPTIONS.length; index++) {
            int optionY = menuY + 58 + index * 43;
            boolean selected = index == characterSelection;

            g2.setColor(selected ? new Color(67, 130, 190) : new Color(45, 45, 45));
            g2.fillRoundRect(menuX + 25, optionY, menuWidth - 50, 34, 10, 10);
            g2.setColor(Color.WHITE);
            g2.drawString((selected ? "> " : "  ") + PLAYER_OPTIONS[index], menuX + 42, optionY + 23);
        }

        g2.setFont(new Font("SansSerif", Font.PLAIN, 14));
        g2.setColor(Color.LIGHT_GRAY);
        g2.drawString("↑/↓: elegir   Enter: confirmar   P o Esc: cerrar", menuX + 25, menuY + 188);
    }
    private void drawCharacterMenu(Graphics2D g2) {
        int menuWidth = 790;
        int menuHeight = 310;
        int menuX = (screenWidth - menuWidth) / 2;
        int menuY = (screenHeight - menuHeight) / 2;

        g2.setColor(new Color(0, 0, 0, 190));
        g2.fillRoundRect(menuX, menuY, menuWidth, menuHeight, 18, 18);
        g2.setColor(Color.WHITE);
        g2.drawRoundRect(menuX, menuY, menuWidth, menuHeight, 18, 18);

        g2.setFont(new Font("SansSerif", Font.BOLD, 22));
        g2.drawString("Seleccionar personaje, arma y audio", menuX + 205, menuY + 38);

        g2.setFont(new Font("SansSerif", Font.BOLD, 16));
        g2.setColor(menuSection == 0 ? Color.WHITE : Color.LIGHT_GRAY);
        g2.drawString("Personaje", menuX + 25, menuY + 56);
        g2.setColor(menuSection == 1 ? Color.WHITE : Color.LIGHT_GRAY);
        g2.drawString("Arma", menuX + 285, menuY + 56);
        g2.setColor(menuSection == 2 ? Color.WHITE : Color.LIGHT_GRAY);
        g2.drawString("Audio", menuX + 545, menuY + 56);

        g2.setFont(new Font("SansSerif", Font.PLAIN, 18));
        for(int index = 0; index < PLAYER_OPTIONS.length; index++) {
            int optionY = menuY + 65 + index * 43;
            boolean selected = menuSection == 0 && index == characterSelection;

            g2.setColor(selected ? new Color(67, 130, 190) : new Color(45, 45, 45));
            g2.fillRoundRect(menuX + 25, optionY, 220, 34, 10, 10);
            g2.setColor(Color.WHITE);
            g2.drawString((selected ? "> " : "  ") + PLAYER_OPTIONS[index], menuX + 42, optionY + 23);
        }

        for(int index = 0; index < WEAPON_OPTIONS.length; index++) {
            int optionY = menuY + 65 + index * 43;
            boolean selected = menuSection == 1 && index == weaponSelection;
            boolean unlocked = player.isWeaponUnlocked(WEAPON_OPTIONS[index]);

            g2.setColor(selected ? new Color(67, 130, 190) : new Color(45, 45, 45));
            g2.fillRoundRect(menuX + 285, optionY, 220, 34, 10, 10);
            g2.setColor(unlocked ? Color.WHITE : Color.GRAY);
            String label = (selected ? "> " : "  ") + WEAPON_OPTIONS[index];
            if(unlocked == false) {
                label += " (bloqueada)";
            }
            g2.drawString(label, menuX + 302, optionY + 23);
        }

        for(int index = 0; index < VOLUME_OPTIONS.length; index++) {
            int optionY = menuY + 65 + index * 43;
            boolean selected = menuSection == 2 && index == volumeSelection;
            int volume = index == 0 ? audio.getMusicVolume() : audio.getEffectsVolume();

            g2.setColor(selected ? new Color(67, 130, 190) : new Color(45, 45, 45));
            g2.fillRoundRect(menuX + 545, optionY, 220, 34, 10, 10);
            g2.setColor(Color.WHITE);
            g2.drawString((selected ? "> " : "  ") + VOLUME_OPTIONS[index] + ": " + volume + "%",
                    menuX + 562, optionY + 23);
        }

        g2.setFont(new Font("SansSerif", Font.PLAIN, 14));
        g2.setColor(Color.LIGHT_GRAY);
        g2.drawString("Flechas: navegar   -/+: volumen   P: seleccionar   Enter o Esc: cerrar",
                menuX + 175, menuY + 288);
    }

    private void drawHud(Graphics2D g2) {
        int hudX = 14;
        int hudY = 14;
        int hudWidth = 250;
        int hudHeight = 175;

        g2.setColor(new Color(0, 0, 0, 145));
        g2.fillRoundRect(hudX, hudY, hudWidth, hudHeight, 14, 14);
        g2.setColor(Color.WHITE);
        g2.drawRoundRect(hudX, hudY, hudWidth, hudHeight, 14, 14);

        g2.setFont(new Font("SansSerif", Font.BOLD, 15));
        g2.drawString("Vidas", hudX + 14, hudY + 25);
        for(int index = 0; index < Player.MAX_LIVES; index++) {
            BufferedImage heartImage = index < player.getLives() ? heartRedImage : heartWhiteImage;
            int heartX = hudX + 14 + index * 44;
            int heartY = hudY + 34;

            if(heartImage != null) {
                g2.drawImage(heartImage, heartX, heartY, 38, 38, null);
            } else {
                Color heartColor = index < player.getLives() ? new Color(220, 40, 45) : Color.WHITE;
                drawHeart(g2, heartX, heartY, 34, heartColor);
            }
        }

        g2.setFont(new Font("SansSerif", Font.BOLD, 15));
        g2.setColor(Color.WHITE);
        g2.drawString("Arma actual: " + player.getCurrentWeapon(), hudX + 14, hudY + 100);

        BufferedImage weaponImage = player.getCurrentWeaponImage();
        if(weaponImage != null) {
            g2.drawImage(weaponImage, hudX + 14, hudY + 108, 51, 51, null);
        }
    }

    private void drawHeart(Graphics2D g2, int x, int y, int size, Color color) {
        int lobeSize = size / 2 + 2;
        Polygon lowerHalf = new Polygon(
                new int[] {x, x + size, x + size / 2},
                new int[] {y + size / 2, y + size / 2, y + size},
                3);

        g2.setColor(Color.DARK_GRAY);
        g2.fillOval(x, y, lobeSize, lobeSize);
        g2.fillOval(x + size / 2 - 2, y, lobeSize, lobeSize);
        g2.fillPolygon(lowerHalf);

        g2.setColor(color);
        g2.fillOval(x + 1, y + 1, lobeSize - 2, lobeSize - 2);
        g2.fillOval(x + size / 2 - 1, y + 1, lobeSize - 2, lobeSize - 2);
        g2.fillPolygon(new Polygon(
                new int[] {x + 1, x + size - 1, x + size / 2},
                new int[] {y + size / 2, y + size / 2, y + size - 1},
                3));
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D)g;
        tileM.draw(g2);
        objM.draw(g2);
        player.draw(g2);
        drawHud(g2);
        if(characterMenuOpen) {
            drawCharacterMenu(g2);
        }
        // player2.draw(g2);

        g2.dispose();
    }
}
