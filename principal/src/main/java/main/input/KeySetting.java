package main.input;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class KeySetting implements KeyListener{
    
    public boolean incPressed, decPressed;
    private String keys;
    private int zoom_increment;
    private int zoom_decrement;

    public KeySetting( String keys) {
        this.keys = keys;
        zoom_increment = KeyEvent.getExtendedKeyCodeForChar(keys.charAt(0));
        zoom_decrement = KeyEvent.getExtendedKeyCodeForChar(keys.charAt(1));
    }

    @Override
    public void keyPressed(KeyEvent e) {
        int code = e.getKeyCode();
        if(code == zoom_increment) {
            incPressed = true;
        }
        if(code == zoom_decrement) {
            decPressed = true; 
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        int code = e.getKeyCode();
        if(code == zoom_increment) {
            incPressed = false;
        }
        if(code == zoom_decrement) {
            decPressed = false;
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {
        // TODO Auto-generated method stub
        // throw new UnsupportedOperationException("Unimplemented method 'keyTyped'");
    }
}
