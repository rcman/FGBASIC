import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;

public class GraphicsWindow extends JFrame {
    private GraphicsPanel panel;
    private Map<Integer, BufferedImage> pages;
    private int currentPage;
    private BufferedImage buffer;
    private Graphics2D bufferGraphics;
    private Color currentColor;
    private int currentX, currentY;
    private SpriteSystem spriteSystem;
    private InputSystem inputSystem;
    private Font currentFont;
    private final Object graphicsLock = new Object();
    
    public GraphicsWindow() {
        setTitle("BASIC Graphics");
        setSize(640, 480);
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        
        pages = new HashMap<>();
        currentPage = 0;
        
        buffer = new BufferedImage(640, 480, BufferedImage.TYPE_INT_RGB);
        pages.put(0, buffer);
        bufferGraphics = buffer.createGraphics();
        bufferGraphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, 
            RenderingHints.VALUE_ANTIALIAS_ON);
        
        currentColor = Color.WHITE;
        currentX = 0;
        currentY = 0;
        currentFont = new Font("Arial", Font.PLAIN, 12);
        bufferGraphics.setFont(currentFont);
        
        spriteSystem = new SpriteSystem();
        inputSystem = new InputSystem();
        
        panel = new GraphicsPanel();
        panel.addMouseListener(inputSystem);
        panel.addMouseMotionListener(inputSystem);
        panel.addKeyListener(inputSystem);
        panel.setFocusable(true);
        add(panel);
        
        clear();
    }
    
    public SpriteSystem getSpriteSystem() {
        return spriteSystem;
    }
    
    public InputSystem getInputSystem() {
        return inputSystem;
    }
    
    public Color getCurrentColor() {
        return currentColor;
    }
    
    public void setPage(int pageNum) {
        synchronized (graphicsLock) {
            if (!pages.containsKey(pageNum)) {
                BufferedImage newPage = new BufferedImage(640, 480, BufferedImage.TYPE_INT_RGB);
                Graphics2D g = null;
                try {
                    g = newPage.createGraphics();
                    g.setColor(Color.BLACK);
                    g.fillRect(0, 0, 640, 480);
                } finally {
                    if (g != null) {
                        g.dispose();
                    }
                }
                pages.put(pageNum, newPage);
            }
            currentPage = pageNum;
            buffer = pages.get(pageNum);
            // Dispose old graphics context
            if (bufferGraphics != null) {
                bufferGraphics.dispose();
            }
            bufferGraphics = buffer.createGraphics();
            bufferGraphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);
            bufferGraphics.setFont(currentFont);
            bufferGraphics.setColor(currentColor);
        }
        repaint();
    }
    
    public int getCurrentPage() {
        return currentPage;
    }
    
    public void copyPage(int fromPage, int toPage) {
        BufferedImage source = pages.get(fromPage);
        if (source == null) return;
        
        BufferedImage dest = new BufferedImage(640, 480, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = dest.createGraphics();
        g.drawImage(source, 0, 0, null);
        g.dispose();
        pages.put(toPage, dest);
    }
    
    public void clear() {
        synchronized (graphicsLock) {
            bufferGraphics.setColor(Color.BLACK);
            bufferGraphics.fillRect(0, 0, 640, 480);
            currentX = 0;
            currentY = 0;
            spriteSystem.clearAll();
        }
        repaint();
    }
    
    public void setColor(int r, int g, int b) {
        currentColor = new Color(
            Math.max(0, Math.min(255, r)),
            Math.max(0, Math.min(255, g)),
            Math.max(0, Math.min(255, b))
        );
        bufferGraphics.setColor(currentColor);
    }
    
    public void drawPixel(int x, int y) {
        synchronized (graphicsLock) {
            bufferGraphics.setColor(currentColor);
            bufferGraphics.fillRect(x, y, 1, 1);
        }
        repaint();
    }

    public void drawLine(int x1, int y1, int x2, int y2) {
        synchronized (graphicsLock) {
            bufferGraphics.setColor(currentColor);
            bufferGraphics.drawLine(x1, y1, x2, y2);
            currentX = x2;
            currentY = y2;
        }
        repaint();
    }

    public void drawCircle(int cx, int cy, int radius) {
        synchronized (graphicsLock) {
            bufferGraphics.setColor(currentColor);
            bufferGraphics.drawOval(cx - radius, cy - radius, radius * 2, radius * 2);
        }
        repaint();
    }

    public void fillCircle(int cx, int cy, int radius) {
        synchronized (graphicsLock) {
            bufferGraphics.setColor(currentColor);
            bufferGraphics.fillOval(cx - radius, cy - radius, radius * 2, radius * 2);
        }
        repaint();
    }

    public void drawEllipse(int cx, int cy, int width, int height) {
        synchronized (graphicsLock) {
            bufferGraphics.setColor(currentColor);
            bufferGraphics.drawOval(cx - width/2, cy - height/2, width, height);
        }
        repaint();
    }

    public void fillEllipse(int cx, int cy, int width, int height) {
        synchronized (graphicsLock) {
            bufferGraphics.setColor(currentColor);
            bufferGraphics.fillOval(cx - width/2, cy - height/2, width, height);
        }
        repaint();
    }

    public void drawPolygon(int[] xPoints, int[] yPoints, int nPoints) {
        synchronized (graphicsLock) {
            bufferGraphics.setColor(currentColor);
            bufferGraphics.drawPolygon(xPoints, yPoints, nPoints);
        }
        repaint();
    }

    public void fillPolygon(int[] xPoints, int[] yPoints, int nPoints) {
        synchronized (graphicsLock) {
            bufferGraphics.setColor(currentColor);
            bufferGraphics.fillPolygon(xPoints, yPoints, nPoints);
        }
        repaint();
    }

    public void drawRect(int x, int y, int w, int h) {
        synchronized (graphicsLock) {
            bufferGraphics.setColor(currentColor);
            bufferGraphics.drawRect(x, y, w, h);
        }
        repaint();
    }

    public void fillRect(int x, int y, int w, int h) {
        synchronized (graphicsLock) {
            bufferGraphics.setColor(currentColor);
            bufferGraphics.fillRect(x, y, w, h);
        }
        repaint();
    }
    
    public void setFont(String fontName, int fontSize, int fontStyle) {
        currentFont = new Font(fontName, fontStyle, fontSize);
        bufferGraphics.setFont(currentFont);
    }
    
    public Font getFont() {
        return currentFont;
    }
    
    public void drawText(int x, int y, String text) {
        synchronized (graphicsLock) {
            bufferGraphics.setColor(currentColor);
            bufferGraphics.setFont(currentFont);
            bufferGraphics.drawString(text, x, y);
        }
        repaint();
    }
    
    public void moveTo(int x, int y) {
        currentX = x;
        currentY = y;
    }
    
    public void lineTo(int x, int y) {
        drawLine(currentX, currentY, x, y);
    }
    
    public int getCurrentX() { return currentX; }
    public int getCurrentY() { return currentY; }
    
    private class GraphicsPanel extends JPanel {
        private BufferedImage backBuffer;

        public GraphicsPanel() {
            super();
            // Enable double buffering explicitly
            setDoubleBuffered(true);
            // Create back buffer for sprite rendering
            backBuffer = new BufferedImage(640, 480, BufferedImage.TYPE_INT_RGB);
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            synchronized (graphicsLock) {
                // Render to back buffer first
                Graphics2D backG = backBuffer.createGraphics();
                try {
                    // Set rendering hints for better quality
                    backG.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION,
                        RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);
                    backG.setRenderingHint(RenderingHints.KEY_RENDERING,
                        RenderingHints.VALUE_RENDER_QUALITY);

                    // Draw the main buffer
                    backG.drawImage(buffer, 0, 0, null);

                    // Set composite for sprite drawing
                    backG.setComposite(AlphaComposite.SrcOver);

                    // Draw sprites on top
                    spriteSystem.drawAllSprites(backG);
                } finally {
                    backG.dispose();
                }
                // Now draw the complete back buffer to screen in one operation
                g.drawImage(backBuffer, 0, 0, null);
            }
        }
    }
}
