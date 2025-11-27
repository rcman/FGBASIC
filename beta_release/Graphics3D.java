import java.awt.*;

public class Graphics3D {
    private GraphicsWindow graphics;
    private double rotX, rotY, rotZ;
    private double cameraX = 0, cameraY = 0, cameraZ = -500;
    
    public Graphics3D(GraphicsWindow graphics) {
        this.graphics = graphics;
        this.rotX = 0;
        this.rotY = 0;
        this.rotZ = 0;
    }
    
    public void rotate(double rx, double ry, double rz) {
        this.rotX = Math.toRadians(rx);
        this.rotY = Math.toRadians(ry);
        this.rotZ = Math.toRadians(rz);
    }
    
    public void setCamera(double x, double y, double z) {
        this.cameraX = x;
        this.cameraY = y;
        this.cameraZ = z;
    }
    
    public void drawBox(double x, double y, double z, double width, double height, double depth) {
        if (graphics == null) return;
        
        // Define 8 vertices of the box
        double[][] vertices = {
            {x - width/2, y - height/2, z - depth/2},
            {x + width/2, y - height/2, z - depth/2},
            {x + width/2, y + height/2, z - depth/2},
            {x - width/2, y + height/2, z - depth/2},
            {x - width/2, y - height/2, z + depth/2},
            {x + width/2, y - height/2, z + depth/2},
            {x + width/2, y + height/2, z + depth/2},
            {x - width/2, y + height/2, z + depth/2}
        };
        
        // Apply rotation and projection
        int[][] projected = new int[8][2];
        for (int i = 0; i < 8; i++) {
            double[] rotated = rotatePoint(vertices[i]);
            projected[i] = projectPoint(rotated);
        }
        
        // Draw edges
        int[][] edges = {
            {0, 1}, {1, 2}, {2, 3}, {3, 0}, // Front face
            {4, 5}, {5, 6}, {6, 7}, {7, 4}, // Back face
            {0, 4}, {1, 5}, {2, 6}, {3, 7}  // Connecting edges
        };
        
        for (int[] edge : edges) {
            graphics.drawLine(
                projected[edge[0]][0], projected[edge[0]][1],
                projected[edge[1]][0], projected[edge[1]][1]
            );
        }
    }
    
    public void drawSphere(double x, double y, double z, double radius) {
        if (graphics == null) return;
        
        int segments = 16;
        
        // Draw latitude circles
        for (int lat = 0; lat < segments; lat++) {
            double theta1 = Math.PI * lat / segments;
            double theta2 = Math.PI * (lat + 1) / segments;
            
            for (int lon = 0; lon < segments; lon++) {
                double phi1 = 2 * Math.PI * lon / segments;
                double phi2 = 2 * Math.PI * (lon + 1) / segments;
                
                // Calculate vertices
                double x1 = x + radius * Math.sin(theta1) * Math.cos(phi1);
                double y1 = y + radius * Math.cos(theta1);
                double z1 = z + radius * Math.sin(theta1) * Math.sin(phi1);
                
                double x2 = x + radius * Math.sin(theta1) * Math.cos(phi2);
                double y2 = y + radius * Math.cos(theta1);
                double z2 = z + radius * Math.sin(theta1) * Math.sin(phi2);
                
                // Project and draw
                double[] p1 = rotatePoint(new double[]{x1, y1, z1});
                double[] p2 = rotatePoint(new double[]{x2, y2, z2});
                
                int[] proj1 = projectPoint(p1);
                int[] proj2 = projectPoint(p2);
                
                graphics.drawLine(proj1[0], proj1[1], proj2[0], proj2[1]);
            }
        }
    }
    
    private double[] rotatePoint(double[] point) {
        double x = point[0];
        double y = point[1];
        double z = point[2];
        
        // Rotate around X axis
        double y1 = y * Math.cos(rotX) - z * Math.sin(rotX);
        double z1 = y * Math.sin(rotX) + z * Math.cos(rotX);
        
        // Rotate around Y axis
        double x2 = x * Math.cos(rotY) + z1 * Math.sin(rotY);
        double z2 = -x * Math.sin(rotY) + z1 * Math.cos(rotY);
        
        // Rotate around Z axis
        double x3 = x2 * Math.cos(rotZ) - y1 * Math.sin(rotZ);
        double y3 = x2 * Math.sin(rotZ) + y1 * Math.cos(rotZ);
        
        return new double[]{x3, y3, z2};
    }
    
    private int[] projectPoint(double[] point) {
        // Simple perspective projection
        double x = point[0];
        double y = point[1];
        double z = point[2];
        
        double fov = 256;
        double distance = z - cameraZ;
        
        if (distance == 0) distance = 1;
        
        double scale = fov / distance;
        
        int screenX = (int)((x - cameraX) * scale) + 320;
        int screenY = (int)((y - cameraY) * scale) + 240;
        
        return new int[]{screenX, screenY};
    }
}
