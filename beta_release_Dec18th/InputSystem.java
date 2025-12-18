import java.awt.event.*;
import java.util.concurrent.ConcurrentHashMap;

public class InputSystem implements MouseListener, MouseMotionListener, KeyListener {
    private ConcurrentHashMap<Integer, Boolean> keysPressed;
    private ConcurrentHashMap<Integer, Boolean> mouseButtons;
    private int mouseX, mouseY;
    private volatile boolean mouseClicked;
    private volatile int lastKeyPressed;
    private volatile char lastCharTyped;
    
    public InputSystem() {
        keysPressed = new ConcurrentHashMap<>();
        mouseButtons = new ConcurrentHashMap<>();
        mouseX = 0;
        mouseY = 0;
        mouseClicked = false;
        lastKeyPressed = 0;
        lastCharTyped = 0;
    }
    
    // Mouse methods
    @Override
    public void mouseClicked(MouseEvent e) {
        mouseClicked = true;
    }
    
    @Override
    public void mousePressed(MouseEvent e) {
        mouseButtons.put(e.getButton(), true);
    }
    
    @Override
    public void mouseReleased(MouseEvent e) {
        mouseButtons.put(e.getButton(), false);
    }
    
    @Override
    public void mouseEntered(MouseEvent e) {}
    
    @Override
    public void mouseExited(MouseEvent e) {}
    
    @Override
    public void mouseDragged(MouseEvent e) {
        mouseX = e.getX();
        mouseY = e.getY();
    }
    
    @Override
    public void mouseMoved(MouseEvent e) {
        mouseX = e.getX();
        mouseY = e.getY();
    }
    
    // Keyboard methods
    @Override
    public void keyTyped(KeyEvent e) {
        lastCharTyped = e.getKeyChar();
    }
    
    @Override
    public void keyPressed(KeyEvent e) {
        keysPressed.put(e.getKeyCode(), true);
        lastKeyPressed = e.getKeyCode();
    }
    
    @Override
    public void keyReleased(KeyEvent e) {
        keysPressed.put(e.getKeyCode(), false);
    }
    
    // Query methods
    public int getMouseX() { return mouseX; }
    public int getMouseY() { return mouseY; }
    
    public boolean isMouseClicked() {
        boolean result = mouseClicked;
        mouseClicked = false; // Reset after reading
        return result;
    }
    
    public boolean isMouseButton(int button) {
        return mouseButtons.getOrDefault(button, false);
    }
    
    public boolean isKeyPressed(int keyCode) {
        return keysPressed.getOrDefault(keyCode, false);
    }
    
    public int getLastKey() {
        int key = lastKeyPressed;
        lastKeyPressed = 0; // Reset after reading
        return key;
    }
    
    public char getLastChar() {
        char ch = lastCharTyped;
        lastCharTyped = 0; // Reset after reading
        return ch;
    }
    
    public void clearAll() {
        keysPressed.clear();
        mouseButtons.clear();
        mouseClicked = false;
        lastKeyPressed = 0;
        lastCharTyped = 0;
    }
}
