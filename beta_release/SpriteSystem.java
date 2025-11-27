import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;

public class SpriteSystem {
    private Map<Integer, Sprite> sprites;

    public SpriteSystem() {
        sprites = new ConcurrentHashMap<>();
    }
    
    public void createSprite(int id, int width, int height) {
        // Validate dimensions
        if (width <= 0 || height <= 0) {
            throw new IllegalArgumentException("Sprite dimensions must be positive: " + width + "x" + height);
        }
        if (width > 2048 || height > 2048) {
            throw new IllegalArgumentException("Sprite dimensions too large (max 2048x2048): " + width + "x" + height);
        }
        Sprite sprite = new Sprite(width, height);
        sprites.put(id, sprite);
    }
    
    public void loadSpriteImage(int id, String filename) throws IOException {
        if (filename == null || filename.trim().isEmpty()) {
            throw new IOException("Filename cannot be null or empty");
        }
        BufferedImage img = ImageIO.read(new File(filename));
        if (img == null) {
            throw new IOException("Unable to load image: " + filename);
        }
        Sprite sprite = new Sprite(img.getWidth(), img.getHeight());
        sprite.image = img;
        sprites.put(id, sprite);
    }
    
    public void setSpritePixel(int id, int x, int y, Color color) {
        Sprite sprite = sprites.get(id);
        if (sprite != null) {
            sprite.setPixel(x, y, color);
        }
    }
    
    public void setSpritePosition(int id, int x, int y) {
        Sprite sprite = sprites.get(id);
        if (sprite != null) {
            sprite.x = x;
            sprite.y = y;
        }
    }
    
    public void setSpriteVisible(int id, boolean visible) {
        Sprite sprite = sprites.get(id);
        if (sprite != null) {
            sprite.visible = visible;
        }
    }
    
    public void moveSprite(int id, int dx, int dy) {
        Sprite sprite = sprites.get(id);
        if (sprite != null) {
            sprite.x += dx;
            sprite.y += dy;
        }
    }
    
    public void drawAllSprites(Graphics2D g) {
        for (Sprite sprite : sprites.values()) {
            if (sprite.visible) {
                g.drawImage(sprite.image, sprite.x, sprite.y, null);
            }
        }
    }
    
    public void clearSprite(int id) {
        Sprite sprite = sprites.get(id);
        if (sprite != null) {
            Graphics2D g = null;
            try {
                g = sprite.image.createGraphics();
                g.setComposite(AlphaComposite.Clear);
                g.fillRect(0, 0, sprite.width, sprite.height);
            } finally {
                if (g != null) {
                    g.dispose();
                }
            }
        }
    }
    
    public void deleteSprite(int id) {
        sprites.remove(id);
    }
    
    public void clearAll() {
        sprites.clear();
    }
    
    public Sprite getSprite(int id) {
        return sprites.get(id);
    }
    
    public boolean checkCollision(int id1, int id2) {
        Sprite s1 = sprites.get(id1);
        Sprite s2 = sprites.get(id2);
        
        if (s1 == null || s2 == null || !s1.visible || !s2.visible) {
            return false;
        }
        
        // Simple bounding box collision
        return s1.x < s2.x + s2.width &&
               s1.x + s1.width > s2.x &&
               s1.y < s2.y + s2.height &&
               s1.y + s1.height > s2.y;
    }
    
    public boolean checkPixelCollision(int id1, int id2) {
        Sprite s1 = sprites.get(id1);
        Sprite s2 = sprites.get(id2);
        
        if (s1 == null || s2 == null || !s1.visible || !s2.visible) {
            return false;
        }
        
        // Calculate overlap region
        int x1 = Math.max(s1.x, s2.x);
        int y1 = Math.max(s1.y, s2.y);
        int x2 = Math.min(s1.x + s1.width, s2.x + s2.width);
        int y2 = Math.min(s1.y + s1.height, s2.y + s2.height);
        
        if (x1 >= x2 || y1 >= y2) return false;
        
        // Check pixel-by-pixel in overlap region
        for (int y = y1; y < y2; y++) {
            for (int x = x1; x < x2; x++) {
                int px1 = x - s1.x;
                int py1 = y - s1.y;
                int px2 = x - s2.x;
                int py2 = y - s2.y;
                
                if (px1 >= 0 && px1 < s1.width && py1 >= 0 && py1 < s1.height &&
                    px2 >= 0 && px2 < s2.width && py2 >= 0 && py2 < s2.height) {
                    
                    int rgb1 = s1.image.getRGB(px1, py1);
                    int rgb2 = s2.image.getRGB(px2, py2);
                    
                    int alpha1 = (rgb1 >> 24) & 0xFF;
                    int alpha2 = (rgb2 >> 24) & 0xFF;
                    
                    if (alpha1 > 0 && alpha2 > 0) {
                        return true;
                    }
                }
            }
        }
        
        return false;
    }
    
    public static class Sprite {
        BufferedImage image;
        int x, y;
        int width, height;
        boolean visible;
        
        public Sprite(int width, int height) {
            this.width = width;
            this.height = height;
            this.image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
            this.x = 0;
            this.y = 0;
            this.visible = true;

            // Clear to transparent
            Graphics2D g = null;
            try {
                g = image.createGraphics();
                g.setComposite(AlphaComposite.Clear);
                g.fillRect(0, 0, width, height);
            } finally {
                if (g != null) {
                    g.dispose();
                }
            }
        }
        
        public void setPixel(int x, int y, Color color) {
            if (x >= 0 && x < width && y >= 0 && y < height) {
                // Ensure alpha channel is fully opaque (0xFF000000)
                int rgb = color.getRGB() | 0xFF000000;
                image.setRGB(x, y, rgb);
            }
        }
        
        public void fillRect(int x, int y, int w, int h, Color color) {
            Graphics2D g = null;
            try {
                g = image.createGraphics();
                // Ensure we're drawing opaque pixels, not transparent
                g.setComposite(AlphaComposite.Src);
                g.setColor(color);
                g.fillRect(x, y, w, h);
            } finally {
                if (g != null) {
                    g.dispose();
                }
            }
        }

        public void drawLine(int x1, int y1, int x2, int y2, Color color) {
            Graphics2D g = null;
            try {
                g = image.createGraphics();
                // Ensure we're drawing opaque pixels, not transparent
                g.setComposite(AlphaComposite.Src);
                g.setColor(color);
                g.drawLine(x1, y1, x2, y2);
            } finally {
                if (g != null) {
                    g.dispose();
                }
            }
        }
    }
}
