package main.input;
import java.awt.event.KeyListener;
import java.awt.event.KeyEvent;

public class KeyHandler implements KeyListener {

    public boolean upPressed, downPressed, leftPressed, rightPressed;
    public boolean speedUpPressed, speedDownPressed;
    public boolean restartPressed;
    public boolean characterMenuPressed;
    public boolean menuUpPressed, menuDownPressed, menuLeftPressed, menuRightPressed;
    public boolean menuConfirmPressed, menuCancelPressed;
    public boolean menuVolumeDownPressed, menuVolumeUpPressed;
    private int up;
    private int left;
    private int down;
    private int right;
    private String keys;
    private boolean characterMenuKeyDown;
    private boolean menuUpKeyDown, menuDownKeyDown, menuLeftKeyDown, menuRightKeyDown;
    private boolean menuConfirmKeyDown, menuCancelKeyDown;
    private boolean menuVolumeDownKeyDown, menuVolumeUpKeyDown;


    public KeyHandler(String keys) {
        this.keys = keys;
        up    = KeyEvent.getExtendedKeyCodeForChar(keys.charAt(0));
        left = KeyEvent.getExtendedKeyCodeForChar(keys.charAt(1));
        down  = KeyEvent.getExtendedKeyCodeForChar(keys.charAt(2));
        right = KeyEvent.getExtendedKeyCodeForChar(keys.charAt(3));
    }    

    @Override
    public void keyTyped(KeyEvent e) {
    }

    @Override
    public void keyPressed(KeyEvent e) {
            int code = e.getKeyCode();
            if(code == up) {
                upPressed = true;
                System.out.println(keys.charAt(0));
            }
            if(code == down) {
                downPressed = true; 
                System.out.println(keys.charAt(2));
            }
            if(code == left) {
                leftPressed = true;
                System.out.println(keys.charAt(1));
            }
            if(code == right) {
                rightPressed = true;    
                System.out.println(keys.charAt(3));
            }
            if(code == KeyEvent.VK_M) {
                speedUpPressed = true;
                System.out.println("M - Velocidad +");
            }
            if(code == KeyEvent.VK_L) {
                speedDownPressed = true;
                System.out.println("L - Velocidad -");
            }
            if(code == KeyEvent.VK_R) {
                restartPressed = true;
                System.out.println("R - Reiniciar juego");
            }
            if(code == KeyEvent.VK_ENTER && characterMenuKeyDown == false) {
                characterMenuPressed = true;
                characterMenuKeyDown = true;
            }
            if(code == KeyEvent.VK_UP && menuUpKeyDown == false) {
                menuUpPressed = true;
                menuUpKeyDown = true;
            }
            if(code == KeyEvent.VK_DOWN && menuDownKeyDown == false) {
                menuDownPressed = true;
                menuDownKeyDown = true;
            }
            if(code == KeyEvent.VK_LEFT && menuLeftKeyDown == false) {
                menuLeftPressed = true;
                menuLeftKeyDown = true;
            }
            if(code == KeyEvent.VK_RIGHT && menuRightKeyDown == false) {
                menuRightPressed = true;
                menuRightKeyDown = true;
            }
            if(code == KeyEvent.VK_P && menuConfirmKeyDown == false) {
                menuConfirmPressed = true;
                menuConfirmKeyDown = true;
            }
            if(code == KeyEvent.VK_ESCAPE && menuCancelKeyDown == false) {
                menuCancelPressed = true;
                menuCancelKeyDown = true;
            }
            if((code == KeyEvent.VK_MINUS || code == KeyEvent.VK_SUBTRACT) && menuVolumeDownKeyDown == false) {
                menuVolumeDownPressed = true;
                menuVolumeDownKeyDown = true;
            }
            if((code == KeyEvent.VK_EQUALS || code == KeyEvent.VK_ADD) && menuVolumeUpKeyDown == false) {
                menuVolumeUpPressed = true;
                menuVolumeUpKeyDown = true;
            }
    
    }



    @Override
    public void keyReleased(KeyEvent e) {
        int code = e.getKeyCode();
        if(code == up) {
            upPressed = false;
        }
        if(code == down) {
            downPressed = false;
        }
        if(code == left) {
            leftPressed = false;
        }
        if(code == right) {
            rightPressed = false;
        }
        if(code == KeyEvent.VK_M) {
            speedUpPressed = false;
        }
        if(code == KeyEvent.VK_L) {
            speedDownPressed = false;
        }
        if(code == KeyEvent.VK_R) {
            restartPressed = false;
        }
        if(code == KeyEvent.VK_ENTER) {
            characterMenuKeyDown = false;
        }
        if(code == KeyEvent.VK_UP) {
            menuUpKeyDown = false;
        }
        if(code == KeyEvent.VK_DOWN) {
            menuDownKeyDown = false;
        }
        if(code == KeyEvent.VK_LEFT) {
            menuLeftKeyDown = false;
        }
        if(code == KeyEvent.VK_RIGHT) {
            menuRightKeyDown = false;
        }
        if(code == KeyEvent.VK_P) {
            menuConfirmKeyDown = false;
        }
        if(code == KeyEvent.VK_ESCAPE) {
            menuCancelKeyDown = false;
        }
        if(code == KeyEvent.VK_MINUS || code == KeyEvent.VK_SUBTRACT) {
            menuVolumeDownKeyDown = false;
        }
        if(code == KeyEvent.VK_EQUALS || code == KeyEvent.VK_ADD) {
            menuVolumeUpKeyDown = false;
        }
    }

    public void resetInputState() {
        upPressed = false;
        downPressed = false;
        leftPressed = false;
        rightPressed = false;
        speedUpPressed = false;
        speedDownPressed = false;
        restartPressed = false;
        characterMenuPressed = false;
        menuUpPressed = false;
        menuDownPressed = false;
        menuLeftPressed = false;
        menuRightPressed = false;
        menuConfirmPressed = false;
        menuCancelPressed = false;
        menuVolumeDownPressed = false;
        menuVolumeUpPressed = false;
        characterMenuKeyDown = false;
        menuUpKeyDown = false;
        menuDownKeyDown = false;
        menuLeftKeyDown = false;
        menuRightKeyDown = false;
        menuConfirmKeyDown = false;
        menuCancelKeyDown = false;
        menuVolumeDownKeyDown = false;
        menuVolumeUpKeyDown = false;
    }
    
}
