package main.input;
import java.awt.event.KeyListener;
import java.awt.event.KeyEvent;

public class KeyHandler implements KeyListener {

    public boolean upPressed, downPressed, leftPressed, rightPressed;
    public boolean speedUpPressed, speedDownPressed;
    
    @Override
    public void keyTyped(KeyEvent e) {
    }

    @Override
    public void keyPressed(KeyEvent e) {
            int code = e.getKeyCode();
            if(code == KeyEvent.VK_W) {
                upPressed = true;
                System.out.println("W");
            }
            if(code == KeyEvent.VK_S) {
                downPressed = true; 
                System.out.println("S");
            }
            if(code == KeyEvent.VK_A) {
                leftPressed = true;
                System.out.println("A");
            }
            if(code == KeyEvent.VK_D) {
                rightPressed = true;    
                System.out.println("D");
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
        if(code == KeyEvent.VK_W) {
            upPressed = false;
        }
        if(code == KeyEvent.VK_S) {
            downPressed = false;
        }
        if(code == KeyEvent.VK_A) {
            leftPressed = false;
        }
        if(code == KeyEvent.VK_D) {
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
