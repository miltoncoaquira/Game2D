package main.game;
import javax.swing.JPanel;
import java.awt.AlphaComposite;
import java.awt.Composite;
import java.awt.Dimension;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;

import main.audio.AudioManager;
import main.entities.Enemy;
import main.entities.Player;
import main.input.KeyHandler;
import main.input.KeySetting;
import main.objects.Key;
import main.objects.ObjectManager;
import main.objects.SuperKey;
import main.tiles.TileManager;
import main.util.ResourceLoader;

public class GamePanel extends JPanel implements Runnable {
    private static final String[] PLAYER_OPTIONS = {"Default_Player", "Player_2"};
    private static final String[] WEAPON_OPTIONS = {
            Player.WEAPON_NONE, Player.WEAPON_SWORD, Player.WEAPON_BOW, Player.WEAPON_AXE};
    private static final String[] VOLUME_OPTIONS = {"Musica", "Efectos"};
    private static final int ENEMY_START_COL = 30;
    private static final int ENEMY_START_ROW = 32;
    private static final int ENEMY_2_START_COL = 12;
    private static final int ENEMY_2_START_ROW = 32;
    private static final String GAME_OVER_SOUND_PATH = "/audio/sfx/game_over.wav";
    private static final String CONGRATULATIONS_SOUND_PATH = "/audio/sfx/congratulations.wav";
    private static final long TEMPORARY_MESSAGE_DURATION_NANOS = 2_000_000_000L;

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
    public final String fightMusicPath = "/audio/music/fight.wav";
    Thread gameThread;
    private boolean characterMenuOpen;
    private int characterSelection;
    private int weaponSelection;
    private int menuSection;
    private int volumeSelection;
    private BufferedImage heartRedImage;
    private BufferedImage heartWhiteImage;
    private boolean enemiesSpawned;
    private boolean enemiesDefeated;
    private boolean gameOver;
    private boolean congratulationsShown;
    private boolean interactionMessageOpen;
    private boolean awaitMovementRelease;
    private String interactionMessage;
    private String temporaryMessage;
    private long temporaryMessageEndTime;

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
    public Enemy enemy;
    public Enemy enemy2;
    
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
                if(gameOver == false && congratulationsShown == false) {
                    if(interactionMessageOpen) {
                        updateInteractionMessage();
                    } else {
                        updateCharacterMenu();
                        if(keyH_1.spawnEnemiesPressed) {
                            if(characterMenuOpen == false) {
                                spawnEnemies();
                            }
                            keyH_1.spawnEnemiesPressed = false;
                        }
                        if(keyH_1.reduceEnemyOneLifePressed) {
                            if(enemy != null && enemy.reduceHealthToOneForDemo()) {
                                showInteractionMessage("Vida reducida de enemy1 para demo de juego finalizado");
                            }
                            keyH_1.reduceEnemyOneLifePressed = false;
                        }
                        if(characterMenuOpen == false && interactionMessageOpen == false) {
                            player.update();
                            if(keyH_1.swordAttackPressed) {
                                if(player.startSwordAttack()) {
                                    attackEnemiesWithSword();
                                }
                                keyH_1.swordAttackPressed = false;
                            }
                            if(enemiesSpawned && interactionMessageOpen == false) {
                                enemy.update(player);
                                enemy2.update(player);
                                enemy.damagePlayer(player);
                                enemy2.damagePlayer(player);
                            }
                        } else {
                            keyH_1.swordAttackPressed = false;
                        }
                        updateGameOverState();
                    }
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
        enemy = null;
        enemy2 = null;
        enemiesSpawned = false;
        enemiesDefeated = false;
        gameOver = false;
        congratulationsShown = false;
        interactionMessageOpen = false;
        awaitMovementRelease = false;
        interactionMessage = null;
        temporaryMessage = null;
        temporaryMessageEndTime = 0;
        characterMenuOpen = false;
        characterSelection = 0;
        weaponSelection = 0;
        menuSection = 0;
        volumeSelection = 0;
        audio.playMusic(backgroundMusicPath, true);
        System.out.println("Juego reiniciado.");
    }

    public void showCongratulations() {
        if(congratulationsShown) {
            return;
        }

        congratulationsShown = true;
        characterMenuOpen = false;
        audio.stopMusic();
        audio.playEffect(CONGRATULATIONS_SOUND_PATH);
    }

    public void showInteractionMessage(String message) {
        interactionMessage = message;
        interactionMessageOpen = true;
        awaitMovementRelease = keyH_1.isMovementPressed();
        characterMenuOpen = false;
    }

    public void showTemporaryMessage(String message) {
        temporaryMessage = message;
        temporaryMessageEndTime = System.nanoTime() + TEMPORARY_MESSAGE_DURATION_NANOS;
    }

    private void updateInteractionMessage() {
        if(awaitMovementRelease) {
            if(keyH_1.isMovementPressed() == false) {
                awaitMovementRelease = false;
            }
            return;
        }

        if(keyH_1.isMovementPressed()) {
            interactionMessageOpen = false;
            interactionMessage = null;
        }
    }

    private void updateGameOverState() {
        if(player.getLives() > 0 || gameOver) {
            return;
        }

        gameOver = true;
        characterMenuOpen = false;
        audio.stopMusic();
        audio.playEffect(GAME_OVER_SOUND_PATH);
        System.out.println("GAME OVER");
    }

    private void spawnEnemies() {
        if(enemiesSpawned) {
            return;
        }

        enemy = new Enemy(this, ENEMY_START_COL, ENEMY_START_ROW, "Enemy_1", 2, 3, 20);
        enemy2 = new Enemy(this, ENEMY_2_START_COL, ENEMY_2_START_ROW, "Enemy_2", 1, 4, 10);
        enemiesSpawned = true;
        enemiesDefeated = false;
        audio.playMusic(fightMusicPath, true);
        System.out.println("Enemigos aparecieron. Musica de combate activada.");
    }

    private void attackEnemiesWithSword() {
        Rectangle attackArea = player.getSwordAttackArea();
        attackEnemyWithSword(enemy, false, attackArea);
        attackEnemyWithSword(enemy2, true, attackArea);
    }

    private void attackEnemyWithSword(Enemy target, boolean isSecondEnemy, Rectangle attackArea) {
        if(target == null || target.isAlive() == false || attackArea.intersects(target.getHitbox()) == false) {
            return;
        }

        target.takeSwordHit();
        if(target.isAlive()) {
            return;
        }

        if(isSecondEnemy) {
            objM.addObject(new Key(), target.worldX, target.worldY);
            if(enemy != null && enemy.isAlive()) {
                enemy.doubleSpeed();
            }
            System.out.println("Enemy_2 dejo una Key.");
        } else {
            objM.addObject(new SuperKey(), target.worldX, target.worldY);
            System.out.println("Enemy_1 dejo una SuperKey.");
        }

        restoreStageMusicAfterCombat();
    }

    private void restoreStageMusicAfterCombat() {
        if(enemiesDefeated || enemy == null || enemy2 == null || enemy.isAlive() || enemy2.isAlive()) {
            return;
        }

        enemiesDefeated = true;
        audio.playMusic(backgroundMusicPath, true);
        System.out.println("Ambos enemigos fueron derrotados. Musica de escenario activada.");
    }

    private String getGameplayMusicPath() {
        return enemiesSpawned && enemiesDefeated == false ? fightMusicPath : backgroundMusicPath;
    }

    private void updateCharacterMenu() {
        if(keyH_1.characterMenuPressed) {
            characterMenuOpen = !characterMenuOpen;
            audio.playMusic(characterMenuOpen ? menuMusicPath : getGameplayMusicPath(), true);
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
                    audio.playMusic(getGameplayMusicPath(), true);
                    clearCharacterMenuInput();
                }
            } else if(menuSection == 0) {
                player.setPlayerSprites(PLAYER_OPTIONS[characterSelection]);
                characterMenuOpen = false;
                audio.playMusic(getGameplayMusicPath(), true);
                clearCharacterMenuInput();
            } else {
                characterMenuOpen = false;
                audio.playMusic(getGameplayMusicPath(), true);
                clearCharacterMenuInput();
            }
        }
        if(keyH_1.menuCancelPressed) {
            characterMenuOpen = false;
            audio.playMusic(getGameplayMusicPath(), true);
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

        g2.setFont(new Font("SansSerif", Font.PLAIN, 13));
        g2.drawString("Espacio: atacar", hudX + 80, hudY + 140);
    }

    private void drawDamageEffect(Graphics2D g2) {
        float opacity = player.getDamageFlashOpacity();

        if(opacity <= 0.0f) {
            return;
        }

        Composite originalComposite = g2.getComposite();
        g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, opacity));
        g2.setColor(new Color(220, 30, 40));
        g2.fillRect(0, 0, screenWidth, screenHeight);
        g2.setComposite(originalComposite);
    }

    private void drawCongratulations(Graphics2D g2) {
        if(congratulationsShown == false) {
            return;
        }

        drawScreenMessage(g2, "¡FELICITACIONES!", "Presiona R para reiniciar", new Color(48, 155, 72));
    }

    private void drawGameOver(Graphics2D g2) {
        if(gameOver == false) {
            return;
        }

        drawScreenMessage(g2, "GAME OVER", "Presiona R para reiniciar", new Color(175, 36, 45));
    }

    private void drawInteractionMessage(Graphics2D g2) {
        if(interactionMessageOpen == false) {
            return;
        }

        drawScreenMessage(g2, "AVISO", interactionMessage, new Color(222, 165, 44));
    }

    private void drawTemporaryMessage(Graphics2D g2) {
        if(temporaryMessage == null || System.nanoTime() >= temporaryMessageEndTime) {
            return;
        }

        int panelWidth = 320;
        int panelHeight = 62;
        int panelX = (screenWidth - panelWidth) / 2;
        int panelY = 28;

        g2.setColor(new Color(20, 20, 28, 225));
        g2.fillRoundRect(panelX, panelY, panelWidth, panelHeight, 14, 14);
        g2.setColor(new Color(222, 165, 44));
        g2.drawRoundRect(panelX, panelY, panelWidth, panelHeight, 14, 14);
        g2.setFont(new Font("SansSerif", Font.BOLD, 20));
        int messageX = (screenWidth - g2.getFontMetrics().stringWidth(temporaryMessage)) / 2;
        g2.drawString(temporaryMessage, messageX, panelY + 39);
    }

    private void drawScreenMessage(Graphics2D g2, String title, String message, Color titleColor) {
        g2.setFont(new Font("SansSerif", Font.PLAIN, 21));
        int requiredWidth = g2.getFontMetrics().stringWidth(message) + 80;
        int panelWidth = Math.max(500, Math.min(screenWidth - 40, requiredWidth));
        int panelHeight = 180;
        int panelX = (screenWidth - panelWidth) / 2;
        int panelY = (screenHeight - panelHeight) / 2;

        g2.setColor(new Color(0, 0, 0, 165));
        g2.fillRect(0, 0, screenWidth, screenHeight);
        g2.setColor(new Color(20, 20, 28, 238));
        g2.fillRoundRect(panelX, panelY, panelWidth, panelHeight, 20, 20);
        g2.setColor(titleColor);
        g2.drawRoundRect(panelX, panelY, panelWidth, panelHeight, 20, 20);

        g2.setFont(new Font("SansSerif", Font.BOLD, 40));
        int titleX = (screenWidth - g2.getFontMetrics().stringWidth(title)) / 2;
        g2.drawString(title, titleX, panelY + 76);

        g2.setFont(new Font("SansSerif", Font.PLAIN, 21));
        g2.setColor(Color.WHITE);
        int messageX = (screenWidth - g2.getFontMetrics().stringWidth(message)) / 2;
        g2.drawString(message, messageX, panelY + 128);
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
        if(enemiesSpawned) {
            enemy.draw(g2);
            enemy2.draw(g2);
        }
        player.draw(g2);
        drawHud(g2);
        drawDamageEffect(g2);
        drawCongratulations(g2);
        if(characterMenuOpen && gameOver == false) {
            drawCharacterMenu(g2);
        }
        drawInteractionMessage(g2);
        drawTemporaryMessage(g2);
        drawGameOver(g2);
        // player2.draw(g2);

        g2.dispose();
    }
}
