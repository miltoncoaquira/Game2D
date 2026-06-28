package main.input;
import java.awt.event.KeyListener;
import java.security.PublicKey;
import java.awt.event.KeyEvent;

public class KeyHandler implements KeyListener {

    public boolean upPressed, downPressed, leftPressed, rightPressed;
    public boolean speedUpPressed, speedDownPressed;
    private int up;
    private int left;
    private int down;
    private int right;
    private String keys;


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
    }
    
}