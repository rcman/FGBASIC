import java.awt.*;

public class TurtleGraphics {
    private double x, y;
    private double angle;
    private boolean penDown;
    private GraphicsWindow graphics;
    
    public TurtleGraphics(GraphicsWindow graphics) {
        this.graphics = graphics;
        this.x = 320;
        this.y = 240;
        this.angle = 0; // 0 degrees = pointing right
        this.penDown = true;
    }
    
    public void forward(double distance) {
        double radians = Math.toRadians(angle);
        double newX = x + distance * Math.cos(radians);
        double newY = y + distance * Math.sin(radians);
        
        if (penDown) {
            graphics.drawLine((int)x, (int)y, (int)newX, (int)newY);
        }
        
        x = newX;
        y = newY;
    }
    
    public void backward(double distance) {
        forward(-distance);
    }
    
    public void right(double degrees) {
        angle += degrees;
        while (angle >= 360) angle -= 360;
        while (angle < 0) angle += 360;
    }
    
    public void left(double degrees) {
        right(-degrees);
    }
    
    public void penUp() {
        penDown = false;
    }
    
    public void penDown() {
        penDown = true;
    }
    
    public void home() {
        if (penDown) {
            graphics.drawLine((int)x, (int)y, 320, 240);
        }
        x = 320;
        y = 240;
        angle = 0;
    }
    
    public void setPosition(double newX, double newY) {
        if (penDown) {
            graphics.drawLine((int)x, (int)y, (int)newX, (int)newY);
        }
        x = newX;
        y = newY;
    }
    
    public void setHeading(double degrees) {
        angle = degrees;
        while (angle >= 360) angle -= 360;
        while (angle < 0) angle += 360;
    }
    
    public double getX() { return x; }
    public double getY() { return y; }
    public double getHeading() { return angle; }
    public boolean isPenDown() { return penDown; }
}
