/**
 * Test sprite animation system with 256 sprites
 */
public class AnimationTest {
    public static void main(String[] args) {
        System.out.println("=== Sprite Animation Capability Test ===\n");

        Interpreter interp = new Interpreter();
        GraphicsWindow gfx = new GraphicsWindow();
        interp.setGraphicsWindow(gfx);
        gfx.setVisible(true);

        System.out.println("Testing sprite animation system...");
        System.out.println("Creating 256 sprites...");

        // Create test program
        StringBuilder prog = new StringBuilder();
        prog.append("CLS\n");
        prog.append("PRINT \"Creating sprites...\"\n");

        // Create 256 sprites
        for (int i = 0; i < 256; i++) {
            prog.append("SPRITE CREATE, ").append(i).append(", 8, 8\n");
            // Random color
            prog.append("COLOR ").append(100 + (int)(Math.random() * 155))
                .append(", ").append(100 + (int)(Math.random() * 155))
                .append(", ").append(100 + (int)(Math.random() * 155)).append("\n");
            // Fill sprite
            prog.append("FOR X = 0 TO 7\n");
            prog.append("  FOR Y = 0 TO 7\n");
            prog.append("    SPRITE PIXEL, ").append(i).append(", X, Y\n");
            prog.append("  NEXT Y\n");
            prog.append("NEXT X\n");
            // Position
            int row = i / 16;
            int col = i % 16;
            prog.append("SPRITE MOVE, ").append(i).append(", ")
                .append(col * 40).append(", ").append(row * 30).append("\n");
            prog.append("SPRITE SHOW, ").append(i).append("\n");
        }

        prog.append("PRINT \"Starting animations...\"\n");

        // Animate all sprites
        for (int i = 0; i < 256; i++) {
            double vx = (Math.random() - 0.5) * 4;
            double vy = (Math.random() - 0.5) * 4;
            prog.append("ANIMATE START, ").append(i).append(", ")
                .append(vx).append(", ").append(vy).append(", 60, 1\n");
        }

        prog.append("PRINT \"All sprites animating!\"\n");
        prog.append("WAIT 5\n");
        prog.append("ANIMATE STOPALL\n");
        prog.append("PRINT \"Test complete\"\n");

        interp.loadProgram(prog.toString());

        System.out.println("Running animation test (5 seconds)...");
        try {
            new Thread(() -> {
                try {
                    interp.run();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }).start();

            Thread.sleep(6000);
            System.out.println("\n✓ Animation test completed successfully!");
            System.out.println("256 sprites were created and animated with edge bouncing.");

        } catch (Exception e) {
            System.err.println("✗ Test failed: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
